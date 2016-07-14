package com.qunar.corp.cactus.service.governance.config.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.google.common.base.*;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.dao.GovernanceDataDao;
import com.qunar.corp.cactus.event.EventType;
import com.qunar.corp.cactus.exception.LastProviderException;
import com.qunar.corp.cactus.service.AbstractService;
import com.qunar.corp.cactus.service.governance.IQueryGovernanceService;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.support.protocol.GovernanceFactory;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.ProviderPredicates;
import com.qunar.corp.cactus.util.UrlHelper;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import static com.alibaba.dubbo.common.Constants.DISABLED_KEY;
import static com.qunar.corp.cactus.util.ConstantHelper.*;

/**
 * @author zhenyu.nie created on 2014 2014/8/4 21:33
 */
@Service
class OnlineServiceImpl extends AbstractService implements OnlineService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private EventBus eventBus;

    @Resource
    private ProviderService providerService;

    @Resource
    private IQueryGovernanceService queryService;

    @Resource
    private GovernanceDataDao dao;

    @Resource(name = "configFactory")
    private GovernanceFactory configFactory;

    @Override
    @Transactional
    public void providerOnline(URL provider) {
        changeStatus(provider, ONLINE);
    }

    @Override
    @Transactional
    public void providerOffline(URL provider) {
        changeStatus(provider, OFFLINE);
    }

    private void changeStatus(URL provider, String type) {
        logger.info("{} service {}", type, provider);
        changeStatus(provider, ServiceSign.makeServiceSign(provider), provider.getParameter(CACTUS_USER_NAME),
                Integer.valueOf(provider.getParameter(CACTUS_USER_ID)), type);
    }

    private static final String ONLINE = "online";
    private static final String OFFLINE = "offline";

    private boolean changeStatus(URL provider, ServiceSign sign, String username, int userId, String type) {
        logger.info("{} provider {} with username {}", type, provider, username);

        FluentIterable<GovernanceData> relativeDatasForDisable = getRelativeDatasForDisable(sign);
        DisableData disableData = getCorrespondingData(relativeDatasForDisable, sign);
        DataToChange dataToChange = dealChange(provider, sign, username, type, disableData, relativeDatasForDisable);
        boolean result = writeToZk(dataToChange.zkData, type);

        // todo: 手动点击上线的情况在这里进行额外处理，其实这里应该上zk查一次，把查到的相关url全部删除掉
        if (!CACTUS_API.equals(username) && ONLINE.equals(type)) {
            apiDeal(provider, sign, username, type, disableData);
            Optional<GovernanceData> toUnRegister = Optional.of(makeApiDisableData(provider, sign));
            writeToZk(toUnRegister, type);

            // 有的是/dubbo，有的是dubbo这种样子的
            Optional<GovernanceData> compatibleToUnRegister = Optional.fromNullable(compatible(toUnRegister.get()));
            if (compatibleToUnRegister.isPresent()) {
                writeToZk(compatibleToUnRegister, type);
            }

            // 如果dubbo配置文件里面没有显示指定registry的group就有可能生成regsitryKey为空的url
            compatibleToUnRegister = Optional.fromNullable(compatibleDefaultRegistry(toUnRegister.get()));
            if (compatibleToUnRegister.isPresent()) {
                writeToZk(compatibleToUnRegister, type);
            }
        }

        // fix bug
        // 每次上线尝试性删除老版本的下线url
        if (ONLINE.equals(type)) {
            GovernanceData oldApiDisableData = null;
            try {
                oldApiDisableData = makeOldApiDisableData(provider, sign);
                writeToZk(Optional.of(oldApiDisableData), type);
            } catch (Exception e) {
                logger.warn("try to delete old offline url error, {}", oldApiDisableData, e);
            }
        }
        return result;
    }

    private GovernanceData compatible(GovernanceData governanceData) {
        URL url = governanceData.getUrl();
        String registryKey = url.getParameter(Q_REGISTRY_KEY);
        if (Strings.isNullOrEmpty(registryKey)) {
            return null;
        }

        String compatibleRegistryKey = registryKey.startsWith("/") ? registryKey.substring(1) : "/" + registryKey;
        return governanceData.copy().setUrl(url.addParameter(Q_REGISTRY_KEY, compatibleRegistryKey));
    }

    private static final String DUBBO = "dubbo";

    private GovernanceData compatibleDefaultRegistry(GovernanceData governanceData) {
        URL url = governanceData.getUrl();
        String registryKey = url.getParameter(Q_REGISTRY_KEY);
        if (Strings.isNullOrEmpty(registryKey) || !DUBBO.equals(registryKey)) {
            return null;
        }

        Map<String, String> params = Maps.newHashMap(url.getParameters());
        params.put(Q_REGISTRY_KEY, null);
        params.put(FAKE_Q_REGISTRY_KEY, DUBBO);
        return governanceData.copy().setUrl(url.clearParameters().addParameters(params));
    }

    private GovernanceData makeOldApiDisableData(URL provider, ServiceSign sign) {
        GovernanceData toDelete = makeApiDisableData(provider, sign);
        URL apiDisableUrl = toDelete.getUrl();
        String registryKey = apiDisableUrl.getParameter(Q_REGISTRY_KEY);
        URL oldDisableUrl = apiDisableUrl.addParameter(FAKE_Q_REGISTRY_KEY, registryKey).removeParameter(Q_REGISTRY_KEY);
        toDelete.setUrl(oldDisableUrl);
        return toDelete;
    }

    private DataToChange dealChange(URL provider, ServiceSign sign, String username, String type, DisableData disableData,
                                    FluentIterable<GovernanceData> relativeDatasForDisable) {
        if (CACTUS_API.equals(username)) {
            return apiDeal(provider, sign, username, type, disableData);
        } else {
            return defaultDeal(sign, username, type, disableData, relativeDatasForDisable);
        }
    }

    private DataToChange apiDeal(URL provider, ServiceSign sign, String username, String type, DisableData relativeDisableData) {
        Optional<GovernanceData> apiData = relativeDisableData.apiData;
        if (type.equals(ONLINE)) {
            return apiDealOnline(sign, username, apiData);
        } else {
            return apiDealOffline(provider, sign, username, apiData);
        }
    }

    private DataToChange apiDealOnline(ServiceSign sign, String username, Optional<GovernanceData> apiData) {
        if (apiData.isPresent()) {
            if (apiData.get().getStatus() == Status.ENABLE) {
                logger.info("try delete api data ({})", apiData.get());
                dao.update(dealLastOperator(apiData.get().copy().setStatus(Status.DEL), username));
            } else {
                logger.info("api data is already online");
            }
        } else {
            logger.info("no api data when online provider {}", sign);
        }
        return DataToChange.makeTrivial();
    }

    private DataToChange apiDealOffline(URL provider, ServiceSign sign, String username, Optional<GovernanceData> apiData) {
        if (apiData.isPresent()) {
            if (apiData.get().getStatus() == Status.ENABLE) {
                logger.info("api data is already offline");
            } else {
                logger.info("try enable api data ({})", apiData.get());
                dao.update(dealLastOperator(apiData.get().copy().setStatus(Status.ENABLE), username));
            }
        } else {
            GovernanceData disableData = makeApiDisableData(provider, sign);
            logger.info("try insert api data ({})", disableData);
            dao.insert(disableData);
        }
        return DataToChange.makeTrivial();
    }

    private DataToChange defaultDeal(ServiceSign sign, String username, String type, DisableData relativeDisableData,
                                     FluentIterable<GovernanceData> relativeDatasForDisable) {
        Optional<GovernanceData> zkData = relativeDisableData.zkData;
        if (type.equals(ONLINE)) {
            return defaultDealOnline(sign, username, zkData);
        } else {
            return defaultDealOffline(sign, username, zkData, relativeDatasForDisable);
        }
    }

    private DataToChange defaultDealOnline(ServiceSign sign, String username, Optional<GovernanceData> zkData) {
        if (isOnline(zkData)) {
            logger.info("provider ({}) is already online", sign);
            return DataToChange.makeTrivial();
        } else {
            logger.info("try delete zk data ({})", zkData.get());
            GovernanceData dataToDb = zkData.get().copy().setStatus(Status.DEL);
            dao.update(dealLastOperator(dataToDb, username));
            return DataToChange.makeDataToChange(zkData, EventType.DELETE);
        }
    }

    private DataToChange defaultDealOffline(ServiceSign sign, String username, Optional<GovernanceData> zkData,
                                            FluentIterable<GovernanceData> relativeDatasForDisable) {
        if (isOnline(zkData)) {
            if (isLastProvider(sign, relativeDatasForDisable)) {
                logger.info("offline last provider ({}) with username {}", sign, username);
                throw new LastProviderException(sign, "try to offline last provider " + sign);
            }
            if (zkData.isPresent()) {
                logger.info("try enable zk data ({})", zkData.get());
                GovernanceData dataToDb = dealLastOperator(zkData.get().copy().setStatus(Status.ENABLE), username);
                dao.update(dataToDb);
                return DataToChange.makeDataToChange(zkData, EventType.ENABLE);
            } else {
                GovernanceData dataToDb = dealLastOperator(makeDisableData(sign), username);
                logger.info("try insert zk data ({})", dataToDb);
                dao.insert(dataToDb);
                return DataToChange.makeDataToChange(Optional.of(dataToDb), EventType.NEW);
            }
        } else {
            logger.info("provider ({}) is already offline", sign);
            return DataToChange.makeTrivial();
        }
    }

    private static class DisableData {
        public final Optional<GovernanceData> zkData;
        public final Optional<GovernanceData> apiData;

        DisableData(Optional<GovernanceData> zkData, Optional<GovernanceData> apiData) {
            this.zkData = zkData;
            this.apiData = apiData;
        }

        static DisableData makeData(Optional<GovernanceData> zkData, Optional<GovernanceData> apiData) {
            return new DisableData(zkData, apiData);
        }
    }

    private static class DataToChange {
        public final Optional<GovernanceData> zkData;
        public final EventType eventType;

        private DataToChange(Optional<GovernanceData> zkData, EventType eventType) {
            this.zkData = zkData;
            this.eventType = eventType;
        }

        static DataToChange makeDataToChange(Optional<GovernanceData> zkData, EventType eventType) {
            return new DataToChange(zkData, eventType);
        }

        static DataToChange makeTrivial() {
            return new DataToChange(Optional.<GovernanceData> absent(), null);
        }
    }

    private FluentIterable<GovernanceData> getRelativeDatasForDisable(ServiceSign sign) {
        return queryService
                .getGovernanceDatas(
                        GovernanceQuery.createBuilder().fromGroupAndServiceKey(sign).zkId(sign.zkId).pathType(PathType.CONFIG)
                                .build()).filter(hasKeyOf(DISABLED_KEY));
    }

    private DisableData getCorrespondingData(FluentIterable<GovernanceData> relativeDatas, final ServiceSign sign) {
        FluentIterable<GovernanceData> correspondingDatas = relativeDatas.filter(new Predicate<GovernanceData>() {
            @Override
            public boolean apply(GovernanceData input) {
                return Objects.equal(input.getIp(), sign.getIp()) && Objects.equal(input.getPort(), sign.getPort());
            }
        });
        return DisableData.makeData(correspondingDatas.filter(isDataType(DataType.ZKDATA)).first(), correspondingDatas
                .filter(isDataType(DataType.APIDATA)).first());
    }

    private GovernanceData dealLastOperator(GovernanceData data, String username) {
        return data.copy().setLastOperator(username);
    }

    private boolean isOnline(Optional<GovernanceData> correspondingData) {
        return !correspondingData.isPresent() || correspondingData.get().getStatus() != Status.ENABLE;
    }

    @SuppressWarnings("unchecked")
    private boolean isLastProvider(ServiceSign sign, FluentIterable<GovernanceData> relativeDatas) {
        FluentIterable<GovernanceData> enabledRelatives = relativeDatas.filter(isDataEnable());
        Set<ServiceSign> disabledProviders = Sets.newHashSet(enabledRelatives.transform(UrlHelper.toFullUrl())
                .transform(toServiceSign()));
        Set<ServiceSign> enabledProviders = providerService
                .getItems(sign.getGroup(), sign.getServiceInterface(), ProviderPredicates.serviceKeyEqual(sign),
                        UrlHelper.zkIdEqual(sign.zkId))
                .transform(toServiceSign()).filter(Predicates.not(Predicates.in(disabledProviders)))
                .toSortedSet(addressSame());
        return enabledProviders.size() == 1
                && Objects.equal(enabledProviders.iterator().next().getAddress(), sign.getAddress());
    }

    private GovernanceData makeDisableData(ServiceSign sign) {
        Map<String, String> params = Maps.newHashMap();
        params.put(ConstantHelper.DATA_TYPE, DataType.ZKDATA.getText());
        params.put(ConstantHelper.STATUS, Status.ENABLE.getText());
        params.put(DISABLED_KEY, "true");
        return configFactory.create(sign, params);
    }

    private GovernanceData makeApiDisableData(URL provider, ServiceSign sign) {
        return new GovernanceData().setZkId(sign.zkId).setName("").setGroup(sign.getGroup())
                .setIp(sign.getIp()).setPort(sign.getPort()).setServiceGroup(sign.getServiceGroup())
                .setVersion(sign.getVersion()).setServiceName(sign.getServiceInterface())
                .setUrl(makeApiDisableUrl(provider, sign)).setPathType(PathType.CONFIG).setLastOperator(CACTUS_API)
                .setStatus(Status.ENABLE).setDataType(DataType.APIDATA);
    }

    private boolean writeToZk(Optional<GovernanceData> data, String type) {
        if (data.isPresent()) {
            RegisterAndUnRegisters registerAndUnRegisters = new RegisterAndUnRegisters();
            if (type.equals(ONLINE)) {
                registerAndUnRegisters.addUnRegister(data.get());
            } else {
                registerAndUnRegisters.addRegister(data.get());
            }

            try {
                zkClusterService.writeDatas(registerAndUnRegisters);
            } catch (RuntimeException e) {
                return handleException(e, data.get(), type);
            }
            return true;
        } else {
            return false;
        }
    }

    private Function<URL, ServiceSign> toServiceSign() {
        return new Function<URL, ServiceSign>() {
            @Override
            public ServiceSign apply(URL input) {
                return ServiceSign.makeServiceSign(input);
            }
        };
    }

    // equal to common rpc func makeDisableUrl
    private URL makeApiDisableUrl(URL provider, ServiceSign sign) {
        Map<String, String> params = Maps.newHashMap();
        if (!Strings.isNullOrEmpty(sign.getServiceGroup())) {
            params.put(Constants.GROUP_KEY, sign.getServiceGroup());
        }
        if (!Strings.isNullOrEmpty(sign.getVersion())) {
            params.put(Constants.VERSION_KEY, sign.getVersion());
        }
        params.put(Constants.DISABLED_KEY, "true");
        params.put(Constants.DYNAMIC_KEY, "false");
        params.put(Constants.CATEGORY_KEY, Constants.CONFIGURATORS_CATEGORY);
        params.put(Q_REGISTRY_KEY, provider.getParameter(Q_REGISTRY_KEY));
        return new URL(Constants.OVERRIDE_PROTOCOL, sign.getIp(), sign.getPort(), sign.getServiceInterface(), params);
    }

    private boolean handleException(RuntimeException e, GovernanceData data, String type) {
        if (e.getCause() != null && e.getCause() instanceof KeeperException.NoNodeException) {
            if (type.equals(ONLINE)) {
                logger.info("node {} is not exist", data);
                return false;
            } else {
                logger.error("provider {} maybe not export", data);
                throw e;
            }
        } else if (e.getCause() != null && e.getCause() instanceof KeeperException.NodeExistsException) {
            logger.info("node {} is already exist", data);
            return false;
        } else {
            logger.error("online error, {}", data, e);
            throw e;
        }
    }

    private Comparator<ServiceSign> addressSame() {
        return new Comparator<ServiceSign>() {
            @Override
            public int compare(ServiceSign lhs, ServiceSign rhs) {
                if (lhs == null && rhs != null) {
                    return -1;
                } else if (lhs == null) {
                    return 0;
                } else if (rhs == null) {
                    return 1;
                } else {
                    return lhs.getAddress().compareTo(rhs.getAddress());
                }
            }
        };
    }
}
