package com.qunar.corp.cactus.service.impl;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.ImmutableZkCluster;
import com.qunar.corp.cactus.bean.RegisterAndUnRegisters;
import com.qunar.corp.cactus.bean.ZKCluster;
import com.qunar.corp.cactus.dao.ZKClusterDao;
import com.qunar.corp.cactus.monitor.SystemCounter;
import com.qunar.corp.cactus.service.SpecialService;
import com.qunar.corp.cactus.service.ZKClusterService;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.PathUtil;
import com.qunar.corp.cactus.util.UrlHelper;
import com.qunar.corp.cactus.zk.ZKService;
import com.qunar.corp.cactus.zk.ZKServiceSupplier;
import org.apache.curator.RetryLoop;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.alibaba.dubbo.common.Constants.*;
import static com.qunar.corp.cactus.util.ConstantHelper.*;

/**
 * Created by IntelliJ IDEA. User: liuzz Date: 13-10-28 Time: 下午7:00
 */
@Service
public class ZKClusterServiceImpl implements ZKClusterService {

    private static Logger log = LoggerFactory.getLogger(ZKClusterServiceImpl.class);

    @Resource
    private ZKClusterDao clusterDao;

    @Resource
    private SpecialService specialService;

    private Map<Long, ZKCluster> zkClusters = Maps.newHashMap();

    private Map<Long, ZKService> zkServiceCache = Maps.newHashMap();

    private final Map<String, Long> roomIdMapping = Maps.newHashMap();

    private Map<Long, Function<String, URL>> zkIDFunction = Maps.newHashMap();

    private Predicate<String> isServicePath(final String parent) {
        Predicate<String> predicate = Predicates.alwaysFalse();
        for (ZKService zkService : zkServiceCache.values()) {
            predicate = Predicates.or(predicate, isServicePath(zkService, parent));
        }
        return predicate;
    }

    private Predicate<String> isServicePath(final ZKService zkService, final String parent) {
        return new Predicate<String>() {
            @Override
            public boolean apply(String child) {
                if (!PathUtil.isLegalService(child)) {
                    return false;
                }

                List<String> children = getPathChildren(zkService, ZKPaths.makePath(parent, child));
                return children.size() >= 2 && children.contains(CONFIGURATORS) && children.contains(PROVIDERS);
            }
        };
    }

    private static final Function<String, String> STRING_DECODE_FUNC = new Function<String, String>() {
        @Override
        public String apply(String input) {
            return URL.decode(input);
        }
    };

    private static class DecodeWithZkId implements Function<String, URL> {

        private final long id;

        DecodeWithZkId(long id) {
            this.id = id;
        }

        @Override
        public URL apply(String input) {
            try {
                return URL.valueOf(input).addParameter(ConstantHelper.ZKID, id);
            } catch (Exception e) {
                log.warn("decode url error: {}", input, e);
                return null;
            }
        }
    }

    private final Predicate<String> isGroupNode = new Predicate<String>() {
        @Override
        public boolean apply(java.lang.String path) {
            path = ZKPaths.makePath(ConstantHelper.ROOT_PATH, path);
            for (ZKService zkService : zkServiceCache.values()) {
                if (Iterables.any(getPathChildren(zkService, path), isServicePath(zkService, path))) {
                    return true;
                }
            }
            return false;
        }
    };

    @PostConstruct
    public void after() {
        buildZKClusterCache();
    }

    private void buildZKClusterCache() {
        try {
            for (ZKCluster zkCluster : clusterDao.findAllCluster()) {
                initNewZKService(zkCluster);
                initRoomIdMapping(zkCluster);
            }

            zkClusters = ImmutableMap.copyOf(zkClusters);
        } catch (Exception e) {
            log.error("init zk service error", e);
            throw new RuntimeException("init zk service error", e);
        }
    }

    private void initRoomIdMapping(ZKCluster zkCluster) {
        Iterable<String> addresses = COMMA_SPLITTER.split(zkCluster.getAddress());
        for (String address : addresses) {
            roomIdMapping.put(getRoomSymbol(address), zkCluster.getId());
        }
    }

    private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    private static final String prefix = "l-zk";
    private static final String suffix = ".qunar.com";

    @Override
    public Optional<Long> findZkId(URL registry) {
        return Optional.fromNullable(roomIdMapping.get(getRoomSymbol(registry)));
    }

    // like l-zk1.plat.cn1.qunar.com:2181 -> plat.cn1
    private static String getRoomSymbol(URL registry) {
        return getRoomSymbol(registry.getAddress());
    }

    private static String getRoomSymbol(String address) {
        address = address.replace(suffix, "");
        if (address.startsWith(prefix)) {
            address = address.substring(prefix.length() + 2);
        }
        return address;
    }

    private void initNewZKService(ZKCluster zkCluster) throws Exception {
        ZKService zkService = new ZKServiceSupplier(zkCluster.getAddress()).get();
        zkClusters.put(zkCluster.getId(), new ImmutableZkCluster(zkCluster));
        zkServiceCache.put(zkCluster.getId(), zkService);
        zkIDFunction.put(zkCluster.getId(), new DecodeWithZkId(zkCluster.getId()));
    }

    @PreDestroy
    public void close() {
        for (ZKService zkService : zkServiceCache.values()) {
            zkService.close();
        }
    }

    @Override
    public void writeDatas(RegisterAndUnRegisters datas) {
        log.info("datas to be unRegistered: {}{}datas to be registered: {}",
                datas.getUnRegisters(), ConstantHelper.NEW_LINE, datas.getRegisters());

        for (GovernanceData unRegister : datas.getUnRegisters()) {
            deleteFromZk(unRegister);
        }

        for (GovernanceData register : datas.getRegisters()) {
            writeToZk(register);
        }
    }

    @Override
    public boolean isPreServicePath(String path) {
        return isGroupNode.apply(path);
    }

    private void deleteFromZk(GovernanceData data) {
        try {
            SystemCounter.zkChangeOperation.increment();
            getZkService(data).unRegister(makePath(data.getServiceName(), data.getUrl()));
            log.info("{} unRegister success", data);
        } catch (Exception e) {
            handleRetryException(e);
            throw new RuntimeException("occur error when unRegister data: " + data, e);
        }
    }

    private boolean handleRetryException(Exception e) {
        if (RetryLoop.isRetryException(e)) {
            SystemCounter.zkFailedOperation.increment();
            return true;
        }
        return false;
    }

    private void writeToZk(GovernanceData data) {
        try {
            SystemCounter.zkChangeOperation.increment();
            getZkService(data).register(makePath(data.getServiceName(), data.getUrl().removeParameter(ZKID)), false);
            log.info("{} register success", data);
        } catch (Exception e) {
            handleRetryException(e);
            throw new RuntimeException("occur error when register data: " + data, e);
        }
    }

    private ZKService getZkService(GovernanceData data) {
        Long zkId = data.getZkId();
        try {
            ZKService zkService = zkServiceCache.get(zkId);
            Preconditions.checkNotNull(zkService);
            return zkService;
        } catch (Throwable e) {
            log.error("invalid id: {}", zkId, e);
            throw new IllegalArgumentException("invalid id: " + zkId);
        }
    }

    private String makePath(String serviceName, URL url) {
        String group = UrlHelper.getGroup(url);
        url = url.removeParameter(FAKE_Q_REGISTRY_KEY);
        return PathUtil.makePath(group, serviceName, getCategory(url), URL.encode(url.toFullString()));
    }

    private String getCategory(URL url) {
        if (OVERRIDE_PROTOCOL.equals(url.getProtocol())) {
            return CONFIGURATORS_CATEGORY;
        } else if (ROUTE_PROTOCOL.equals(url.getProtocol())) {
            return ROUTERS_CATEGORY;
        } else {
            throw new RuntimeException("illegal protocol: " + url.getProtocol());
        }
    }

    @Override
    public List<ZKCluster> getZKClusters() {
        return ImmutableList.copyOf(zkClusters.values());
    }

    @Override
    public Optional<ZKCluster> findZkCluster(long zkClusterId) {
        return Optional.fromNullable(zkClusters.get(zkClusterId));
    }

    @Override
    public Set<String> getChildren(String path) {
        Map<Long, List<String>> longListMap = mergeNode(path);
        Set<String> result = Sets.newHashSet();
        for (List<String> strings : longListMap.values()) {
            result.addAll(strings);
        }
        return result;
    }

    private List<String> getPathChildren(ZKService zkService, String path) {
        try {
            return zkService.getPathChildren(path);
        } catch (Exception e) {
            if (!(e instanceof KeeperException.NoNodeException)) {
                log.error("get path children error with path {}", path, e);
            }
            handleRetryException(e);
            return Lists.newArrayList();
        }
    }

    private Map<Long, List<String>> mergeNode(String path) {
        Map<Long, List<String>> results = Maps.newHashMapWithExpectedSize(zkServiceCache.size());
        for (Map.Entry<Long, ZKService> longZKServiceEntry : zkServiceCache.entrySet()) {
            long id = longZKServiceEntry.getKey();
            ZKService zkService = longZKServiceEntry.getValue();
            List<String> pathChildren = getPathChildren(zkService, path);
            if (pathChildren != null && !pathChildren.isEmpty()) {
                List<String> strings = results.get(id);
                if (strings == null) {
                    strings = Lists.newArrayList();
                    results.put(id, strings);
                }
                strings.addAll(pathChildren);
            }
        }
        return results;
    }

    @Override
    public FluentIterable<URL> getUrls(String group, String serviceInterface, String category) {
        return getUrls(group, mergeNode(PathUtil.makePath(group, serviceInterface, category)));
    }

    private FluentIterable<URL> getUrls(String group, Map<Long, List<String>> results) {
        List<URL> allResults = Lists.newArrayList();
        for (Map.Entry<Long, List<String>> idAndResults : results.entrySet()) {
            Long key = idAndResults.getKey();
            List<String> value = idAndResults.getValue();
            Function<String, URL> stringURLFunction = zkIDFunction.get(key);
            stringURLFunction = Preconditions.checkNotNull(stringURLFunction);
            allResults.addAll(
                    FluentIterable.from(value).transform(STRING_DECODE_FUNC).transform(stringURLFunction)
                            .filter(Predicates.notNull()).transform(addFakeRegistry(group)).toList());
        }
        return FluentIterable.from(allResults);
    }

    private Function<URL, URL> addFakeRegistry(final String group) {
        return new Function<URL, URL>() {
            @Override
            public URL apply(URL input) {
                return input.addParameter(FAKE_Q_REGISTRY_KEY, group);
            }
        };
    }

    @Override
    public Set<String> getAllGroup() {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        builder.addAll(specialService.getGroups());
        builder.addAll(FluentIterable.from(getChildren(ConstantHelper.ROOT_PATH)).filter(isGroupNode));
        return builder.build();
    }

    @Override
    public Set<String> getServiceNamesByGroup(String group) {
        return FluentIterable.from(getChildren(group)).filter(isServicePath(group)).toSet();
    }

    private boolean isZKClusterExist(ZKCluster zkCluster) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("address", zkCluster.getAddress().trim());
        List<ZKCluster> zkClusters = clusterDao.findByCondition(param);
        return !(zkClusters == null || zkClusters.size() == 0);
    }

    @Override
    public boolean addNewZK(ZKCluster zkCluster) throws Exception {
        checkValidZkCluster(zkCluster);
        if (isZKClusterExist(zkCluster)) {
            return false;
        }
        clusterDao.insertCluster(zkCluster);
        return true;
    }

    private void checkValidZkCluster(ZKCluster zkCluster) {
        try {
            Preconditions.checkNotNull(zkCluster.getName());
            Preconditions.checkNotNull(zkCluster.getAddress());
        } catch (Throwable e) {
            log.error("{}", zkCluster);
            throw new RuntimeException(zkCluster.toString());
        }
    }

}
