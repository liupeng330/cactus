package com.qunar.corp.cactus.service.graph;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.netflix.nfgraph.OrdinalIterator;
import com.netflix.nfgraph.OrdinalSet;
import com.netflix.nfgraph.build.NFBuildGraph;
import com.netflix.nfgraph.compressed.NFCompressedGraph;
import com.netflix.nfgraph.spec.NFGraphSpec;
import com.netflix.nfgraph.spec.NFNodeSpec;
import com.netflix.nfgraph.spec.NFPropertySpec;
import com.netflix.nfgraph.util.OrdinalMap;
import com.qunar.corp.cactus.bean.IpAndPort;
import com.qunar.corp.cactus.service.AbstractService;
import com.qunar.corp.cactus.service.providerlevel.ConsumerService;
import com.qunar.corp.cactus.service.providerlevel.ProviderLevelService;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.util.CommonCache;
import com.qunar.corp.cactus.util.Pair;
import com.qunar.corp.cactus.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.alibaba.dubbo.common.Constants.APPLICATION_KEY;
import static com.qunar.corp.cactus.util.ConstantHelper.OWNER;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-12-16
 * Time: 下午2:34
 *
 * 一个图
 * 图里应该有的东西
 * group->servicename one-to-many
 * servicename->group many-to-many
 * owner->group one-to-many
 * group->owner one-to-many
 * app->group (relayon) one-to-many
 * group->app (relayme) one-to-many
 * ip:port->hostname:port
 * hostname:port->ip:port
 * ip:port->group (belongto)
 *
 * eveny N min rebuild Graph
 *
 */
@Service
public class GraphServiceImpl extends AbstractService implements GraphService {

    private static Logger log = LoggerFactory.getLogger(GraphServiceImpl.class);

    @Resource
    private ConsumerService consumerService;

    @Resource
    private ProviderService providerService;

    private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    private class GraphInfo {

        NFCompressedGraph graph;

        CactusOrdinalMap<String> apps = new CactusOrdinalMap<String>();
        CactusOrdinalMap<String> owners = new CactusOrdinalMap<String>();

        BiDirectionPrefixSearcher<String> groups;
        BiDirectionPrefixSearcher<String> providerIpPorts;
        BiDirectionPrefixSearcher<String> providerHostnamePorts;
        BiDirectionPrefixSearcher<String> services;
        PrefixSearcher<String> servicesSearcher;

        Map<String, BiDirectionCollection<String>> typeCollectionMapping;

        StatsCount statsCount;
    }

    private GraphInfo graphInfo;

    private static final String SERVICE_NODE = "Service";
    private static final String GROUP_NODE = "Group";
    private static final String APP_NODE = "App";
    private static final String OWNER_NODE = "Owner";
    private static final String PROVIDER_IP_PORT_NODE = "ProviderIpPort";
    private static final String PROVIDER_HOSTNAME_PORT_NODE = "ProviderHostnamePort";

    private static final String SERVICE_BELONG_TO_GROUP = "serviceBelongToGroup";
    private static final String APP_BELONG_TO_GROUP = "appBelongToGroup";
    private static final String BELONG_TO_OWNER = "hasOwner";
    private static final String HAS_SERVICE = "hasService";
    private static final String HAS_GROUP = "hasGroup";
    private static final String RELY_ON_GROUP = "relyOnGroup";
    private static final String RELY_ON = "relyOn";
    private static final String RELY_ME = "relyMe";
    private static final String PROVIDER_HOSTNAME_TO_IP = "providerHostnameToIp";
    private static final String PROVIDER_IP_TO_HOSTNAME = "providerIpToHostname";
    private static final String PROVIDER_BELONG_TO_GROUP = "providerBelongToGroup";

    private static final NFGraphSpec SCHEMA = new NFGraphSpec(
            new NFNodeSpec(
                    GROUP_NODE,
                    new NFPropertySpec(RELY_ME, APP_NODE, NFPropertySpec.MULTIPLE | NFPropertySpec.COMPACT),
                    new NFPropertySpec(RELY_ON, APP_NODE, NFPropertySpec.MULTIPLE | NFPropertySpec.COMPACT),
                    new NFPropertySpec(RELY_ON_GROUP, GROUP_NODE, NFPropertySpec.MULTIPLE | NFPropertySpec.COMPACT),
                    new NFPropertySpec(BELONG_TO_OWNER, OWNER_NODE, NFPropertySpec.MULTIPLE | NFPropertySpec.COMPACT),
                    new NFPropertySpec(HAS_SERVICE, SERVICE_NODE, NFPropertySpec.MULTIPLE | NFPropertySpec.COMPACT)
            ),
            new NFNodeSpec(
                    SERVICE_NODE,
                    new NFPropertySpec(SERVICE_BELONG_TO_GROUP, GROUP_NODE, NFPropertySpec.MULTIPLE | NFPropertySpec.COMPACT)
            ),
            new NFNodeSpec(
                    APP_NODE,
                    new NFPropertySpec(APP_BELONG_TO_GROUP, GROUP_NODE, NFPropertySpec.MULTIPLE | NFPropertySpec.COMPACT)
            ),
            new NFNodeSpec(
                    OWNER_NODE,
                    new NFPropertySpec(HAS_GROUP, GROUP_NODE, NFPropertySpec.MULTIPLE | NFPropertySpec.COMPACT)
            ),
            new NFNodeSpec(
                    PROVIDER_IP_PORT_NODE,
                    new NFPropertySpec(PROVIDER_IP_TO_HOSTNAME, PROVIDER_HOSTNAME_PORT_NODE, NFPropertySpec.SINGLE),
                    new NFPropertySpec(PROVIDER_BELONG_TO_GROUP, GROUP_NODE, NFPropertySpec.MULTIPLE | NFPropertySpec.COMPACT)
            ),
            new NFNodeSpec(
                    PROVIDER_HOSTNAME_PORT_NODE,
                    new NFPropertySpec(PROVIDER_HOSTNAME_TO_IP, PROVIDER_IP_PORT_NODE, NFPropertySpec.SINGLE)
            )
    );

    private void initTypeCollectionMapping(GraphInfo graphInfo) {
        Map<String, BiDirectionCollection<String>> mapping = Maps.newHashMap();
        mapping.put(SERVICE_NODE, graphInfo.services);
        mapping.put(GROUP_NODE, graphInfo.groups);
        mapping.put(APP_NODE, graphInfo.apps);
        mapping.put(OWNER_NODE, graphInfo.owners);
        mapping.put(PROVIDER_IP_PORT_NODE, graphInfo.providerIpPorts);
        mapping.put(PROVIDER_HOSTNAME_PORT_NODE, graphInfo.providerHostnamePorts);
        graphInfo.typeCollectionMapping = ImmutableMap.copyOf(mapping);
    }

    static class ConnectionInfo {
        final String name;
        final String fromNodeType;
        final String toNodeType;

        ConnectionInfo(String name, String fromNodeType, String toNodeType) {
            this.name = name;
            this.fromNodeType = fromNodeType;
            this.toNodeType = toNodeType;
        }
    }

    private final Map<String, ConnectionInfo> NAME_COLLECTION_MAPPING = initNameConnectionMapping();

    private Map<String, ConnectionInfo> initNameConnectionMapping() {
        Map<String, ConnectionInfo> mapping = Maps.newHashMap();
        mapping.put(SERVICE_BELONG_TO_GROUP, new ConnectionInfo(SERVICE_BELONG_TO_GROUP, SERVICE_NODE, GROUP_NODE));
        mapping.put(APP_BELONG_TO_GROUP, new ConnectionInfo(APP_BELONG_TO_GROUP, APP_NODE, GROUP_NODE));
        mapping.put(BELONG_TO_OWNER, new ConnectionInfo(BELONG_TO_OWNER, GROUP_NODE, OWNER_NODE));
        mapping.put(HAS_SERVICE, new ConnectionInfo(HAS_SERVICE, GROUP_NODE, SERVICE_NODE));
        mapping.put(HAS_GROUP, new ConnectionInfo(HAS_GROUP, OWNER_NODE, GROUP_NODE));
        mapping.put(RELY_ON_GROUP, new ConnectionInfo(RELY_ON_GROUP, GROUP_NODE, GROUP_NODE));
        mapping.put(RELY_ON, new ConnectionInfo(RELY_ON, GROUP_NODE, APP_NODE));
        mapping.put(RELY_ME, new ConnectionInfo(RELY_ME, GROUP_NODE, APP_NODE));
        mapping.put(PROVIDER_IP_TO_HOSTNAME, new ConnectionInfo(PROVIDER_IP_TO_HOSTNAME, PROVIDER_IP_PORT_NODE, PROVIDER_HOSTNAME_PORT_NODE));
        mapping.put(PROVIDER_HOSTNAME_TO_IP, new ConnectionInfo(PROVIDER_HOSTNAME_TO_IP, PROVIDER_HOSTNAME_PORT_NODE, PROVIDER_IP_PORT_NODE));
        mapping.put(PROVIDER_BELONG_TO_GROUP, new ConnectionInfo(PROVIDER_BELONG_TO_GROUP, PROVIDER_IP_PORT_NODE, GROUP_NODE));
        return ImmutableMap.copyOf(mapping);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void freshGraph() {
        log.info("start build graph");
        NFBuildGraph buildGraph = new NFBuildGraph(SCHEMA);

        CactusOrdinalMap<String> apps = new CactusOrdinalMap<String>();
        CactusOrdinalMap<String> owners = new CactusOrdinalMap<String>();
        BiDirectionPrefixSearcher newProviderIpPorts;
        BiDirectionPrefixSearcher newProviderHostnamePorts;
        BiDirectionPrefixSearcher services;
        PrefixSearcher<String> simpleServiceNameSearcher;

        Set<String> allGroup = ImmutableSet.copyOf(zkClusterService.getAllGroup());

        Map<Integer, Set<String>> groupServicesMapping = Maps.newHashMapWithExpectedSize(allGroup.size());
        Map<Integer, Set<Pair<String, String>>> groupAddressMapping = Maps.newHashMapWithExpectedSize(allGroup.size());
        PrepareData prepareData = prepare(allGroup);

        BiDirectionPrefixSearcher groups = new BiDirectionPrefixSearcher(allGroup, CASE_IGNORE_SEARCH_ASSIST);

        for (String group : allGroup) {
            int groupIndex = groups.get(group);
            log.debug("group {} with index {}", group, groupIndex);

            Set<Integer> ownerIndexes = Sets.newHashSet();
            Set<Pair<String, String>> addressesInGroup = getOrCreate(groupAddressMapping, groupIndex);
            Set<String> servicesInGroup = getOrCreate(groupServicesMapping, groupIndex);

            for (URL provider : providerService.getItems(group)) {
                try {
                    addOwnerIndex(ownerIndexes, provider, owners);

                    String ipAddress = provider.getAddress();
                    IpAndPort ipAndPort = IpAndPort.fromString(ipAddress);
                    String ip = ipAndPort.getIp();
                    String hostname = CommonCache.getHostNameByIp(ip);
                    String hostnameAddress;
                    if (hostname.equals(ip)) {
                        hostnameAddress = ipAddress;
                    } else {
                        hostnameAddress = hostname + ":" + ipAndPort.getPort();
                    }

                    String providerService = provider.getServiceInterface();
                    if (!PathUtil.isLegalService(providerService)) {
                        throw new IllegalArgumentException("illegal provider service: " + providerService);
                    }

                    servicesInGroup.add(providerService);
                    addressesInGroup.add(Pair.makePair(ipAddress, hostnameAddress));
                } catch (Exception e) {
                    log.warn("illegal provider, group={}, url={}", group, provider, e);
                }
            }

            initConnectionsBetweenAppAndGroup(buildGraph, groupIndex, apps, prepareData.relations.get(group));

            initConnectionsBetweenGroupAndOwner(buildGraph, groupIndex, ownerIndexes);

            initRelyMeConnections(buildGraph, groupIndex, apps, prepareData.relations.get(group));

            initRelyOnConnections(buildGraph, groups, apps, allGroup, group, groupIndex, prepareData);
        }

        Set<String> allService = getAllService(groupServicesMapping);
        services = new BiDirectionPrefixSearcher(allService, CASE_IGNORE_SEARCH_ASSIST);
        simpleServiceNameSearcher = PrefixSearcherImpl.Builder.from(allService).keyGetter(SIMPLE_SERVICE_NAME_GETTER)
                .searchAssist(CASE_IGNORE_SEARCH_ASSIST).build();
        initConnectionsBetweenGroupAndService(buildGraph, services, groupServicesMapping);

        Set<String> providerIpPortsSet = Sets.newHashSet();
        Set<String> providerHostnamePortsSet = Sets.newHashSet();
        initIpAndHostnameAddressBuilder(providerIpPortsSet, providerHostnamePortsSet, groupAddressMapping);
        newProviderIpPorts = new BiDirectionPrefixSearcher(providerIpPortsSet);
        newProviderHostnamePorts = new BiDirectionPrefixSearcher(providerHostnamePortsSet, CASE_IGNORE_SEARCH_ASSIST);

        initConnectionsWithAddress(buildGraph, newProviderIpPorts, newProviderHostnamePorts, groupAddressMapping);

        GraphInfo graphInfo = new GraphInfo();
        graphInfo.graph = buildGraph.compress();
        graphInfo.owners = owners;
        graphInfo.apps = apps;
        graphInfo.groups = groups;
        graphInfo.services = services;
        graphInfo.servicesSearcher = simpleServiceNameSearcher;
        graphInfo.providerIpPorts = newProviderIpPorts;
        graphInfo.providerHostnamePorts = newProviderHostnamePorts;
        initTypeCollectionMapping(graphInfo);
        graphInfo.statsCount = new StatsCount(allGroup.size(), prepareData.serviceCount, prepareData.providerCount,
                prepareData.consumerCount);
        this.graphInfo = graphInfo;
        log.info("build graph success");
    }

    private static Function<String, String> SIMPLE_SERVICE_NAME_GETTER = new Function<String, String>() {
        @Override
        public String apply(String input) {
            int indexOfPoint = input.lastIndexOf('.');
            if (indexOfPoint == -1) {
                return input;
            } else if (indexOfPoint == input.length() - 1) {
                throw new IllegalArgumentException("value end is '.': " + input);
            } else {
                return input.substring(indexOfPoint + 1);
            }
        }
    };

    private static PrefixSearcherImpl.SearchAssist CASE_IGNORE_SEARCH_ASSIST =
            new PrefixSearcherImpl.SearchAssist(Ordering.from(new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.toLowerCase().compareTo(rhs.toLowerCase());
                }
            }), new Function<String, String>() {
                @Override
                public String apply(String input) {
                    input = input.toLowerCase();
                    char[] chars = input.toCharArray();
                    char lastChar = chars[chars.length - 1];
                    if (lastChar > lastChar + 1) {
                        throw new IllegalArgumentException("illegal word, last char is max char");
                    } else {
                        chars[chars.length - 1] += 1;
                    }
                    return new String(chars);
                }
            });

    @Override
    public boolean isOwnerOfGroup(String username, String group) {
        GraphInfo graphInfo = this.graphInfo;

        username = Strings.nullToEmpty(username).trim();
        if (username.isEmpty()) {
            return false;
        }
        int ownerIndex = graphInfo.owners.get(username);
        if (ownerIndex < 0) {
            return false;
        }
        group = Strings.nullToEmpty(group).trim();
        int groupIndex = graphInfo.groups.get(group);
        if (groupIndex < 0) {
            return false;
        }

        OrdinalSet connectionSet = graphInfo.graph.getConnectionSet(OWNER_NODE, ownerIndex, HAS_GROUP);
        OrdinalIterator iterator = connectionSet.iterator();
        int i = iterator.nextOrdinal();
        while (i != OrdinalIterator.NO_MORE_ORDINALS) {
            if (i == groupIndex) {
                return true;
            } else {
                i = iterator.nextOrdinal();
            }
        }
        return false;
    }

    @Override
    public Set<String> getOwners(String group) {
        return getToNodeValues(graphInfo, group, BELONG_TO_OWNER);
    }

    @Override
    public Pair<Set<String>, Map<String, Set<String>>> getRelayMe(String group) {
        GraphInfo graphInfo = this.graphInfo;
        List<Integer> toNodeIndexes = getToNodeIndexes(graphInfo, group, RELY_ME);
        return Pair.makePair(getToNodeValues(graphInfo, toNodeIndexes, RELY_ME), makeRelyInfo(graphInfo, toNodeIndexes));
    }

    @Override
    public Set<String> getRelayOn(String group) {
        return getToNodeValues(graphInfo, group, RELY_ON_GROUP);
    }

    private Map<String, Set<String>> makeRelyInfo(GraphInfo graphInfo, List<Integer> appsIndexes) {
        Map<String, Set<String>> infos = Maps.newHashMap();
        for (int appIndex : appsIndexes) {
            OrdinalSet connectionSet = graphInfo.graph.getConnectionSet(APP_NODE, appIndex, APP_BELONG_TO_GROUP);
            OrdinalIterator iterator = connectionSet.iterator();
            int i = iterator.nextOrdinal();
            while (i != OrdinalIterator.NO_MORE_ORDINALS) {
                String group = graphInfo.groups.get(i);
                Set<String> groupApps = infos.get(group);
                if (groupApps == null) {
                    groupApps = Sets.newHashSet();
                    infos.put(group, groupApps);
                }
                groupApps.add(graphInfo.apps.get(appIndex));
                i = iterator.nextOrdinal();
            }
        }
        return infos;
    }

    private List<Integer> getToNodeIndexes(GraphInfo graphInfo, int fromNodeIndex, String fromNodeType, String connection) {
        if (fromNodeIndex < 0) {
            return ImmutableList.of();
        }
        OrdinalSet connectionSet = graphInfo.graph.getConnectionSet(fromNodeType, fromNodeIndex, connection);
        List<Integer> results = Lists.newArrayListWithExpectedSize(connectionSet.size());
        OrdinalIterator iterator = connectionSet.iterator();
        int connectedNodeIndex = iterator.nextOrdinal();
        while (connectedNodeIndex != OrdinalIterator.NO_MORE_ORDINALS) {
            results.add(connectedNodeIndex);
            connectedNodeIndex = iterator.nextOrdinal();
        }
        return results;
    }

    private List<Integer> getToNodeIndexes(GraphInfo graphInfo, String input, String connection) {
        input = Strings.nullToEmpty(input).trim();
        if (input.isEmpty()) {
            return ImmutableList.of();
        }
        ConnectionInfo connectionInfo = NAME_COLLECTION_MAPPING.get(connection);
        BiDirectionCollection<String> fromNodeBiDirectionCollection = graphInfo.typeCollectionMapping.get(connectionInfo.fromNodeType);
        return getToNodeIndexes(graphInfo, fromNodeBiDirectionCollection.get(input), connectionInfo.fromNodeType, connection);
    }

    private Set<String> getToNodeValues(GraphInfo graphInfo, String input, String connection) {
        List<Integer> toNodeIndexes = getToNodeIndexes(graphInfo, input, connection);
        return getToNodeValues(graphInfo, toNodeIndexes, connection);
    }

    private Set<String> getToNodeValues(GraphInfo graphInfo, List<Integer> toNodeIndexes, String connection) {
        ConnectionInfo connectionInfo = NAME_COLLECTION_MAPPING.get(connection);
        BiDirectionCollection<String> toNodeBiDirectionCollection = graphInfo.typeCollectionMapping.get(connectionInfo.toNodeType);
        Set<String> results = Sets.newHashSetWithExpectedSize(toNodeIndexes.size());
        for (int connectedIndex : toNodeIndexes) {
            results.add(toNodeBiDirectionCollection.get(connectedIndex));
        }
        return results;
    }

    @Override
    public Set<String> getGroupsByService(String service) {
        return getToNodeValues(graphInfo, service, SERVICE_BELONG_TO_GROUP);
    }

    private Set<String> getAllService(Map<Integer, Set<String>> groupServicesMapping) {
        Set<String> serviceSet = Sets.newHashSet();
        for (Set<String> servicesInGroup : groupServicesMapping.values()) {
            serviceSet.addAll(servicesInGroup);
        }
        return serviceSet;
    }

    private <T> Set<T> getOrCreate(Map<Integer, Set<T>> groupServicesMapping, int groupIndex) {
        Set<T> servicesInGroup = groupServicesMapping.get(groupIndex);
        if (servicesInGroup == null) {
            servicesInGroup = Sets.newHashSet();
            groupServicesMapping.put(groupIndex, servicesInGroup);
        }
        return servicesInGroup;
    }

    private void initConnectionsBetweenGroupAndOwner(NFBuildGraph buildGraph, int groupIndex, Set<Integer> ownerIndexes) {
        for (int ownerIndex : ownerIndexes) {
            buildGraph.addConnection(GROUP_NODE, groupIndex, BELONG_TO_OWNER, ownerIndex);
            buildGraph.addConnection(OWNER_NODE, ownerIndex, HAS_GROUP, groupIndex);
        }
    }

    private void initConnectionsBetweenAppAndGroup(NFBuildGraph buildGraph, int groupIndex, OrdinalMap<String> apps,
                                                   Map<String, ProviderAndConsumerApps> providerAndConsumerAppsMap) {
        Set<String> providerApps = Sets.newHashSet();
        for (ProviderAndConsumerApps providerAndConsumerApps : providerAndConsumerAppsMap.values()) {
            providerApps.addAll(providerAndConsumerApps.get(ItemType.PROVIDER));
        }
        for (String providerApp : providerApps) {
            int appIndex = apps.add(providerApp);
            buildGraph.addConnection(APP_NODE, appIndex, APP_BELONG_TO_GROUP, groupIndex);
        }
    }

    private void initRelyMeConnections(NFBuildGraph buildGraph, int groupIndex, OrdinalMap<String> apps,
                                       Map<String, ProviderAndConsumerApps> providerAndConsumerAppsMap) {
        Set<String> consumerApps = Sets.newHashSet();
        for (ProviderAndConsumerApps providerAndConsumerApps : providerAndConsumerAppsMap.values()) {
            consumerApps.addAll(providerAndConsumerApps.get(ItemType.CONSUMER));
        }
        for (String consumerApp : consumerApps) {
            int appIndex = apps.add(consumerApp);
            buildGraph.addConnection(GROUP_NODE, groupIndex, RELY_ME, appIndex);
        }
    }

    private void addOwnerIndex(Set<Integer> ownerIndexes, URL provider, OrdinalMap<String> owners) {
        Set<String> ownersByProvider = getOwners(provider);
        for (String owner : ownersByProvider) {
            ownerIndexes.add(owners.add(owner));
        }
    }

    private void initRelyOnConnections(NFBuildGraph buildGraph, BiDirectionCollection<String> groups, OrdinalMap<String> apps,
                                       Collection<String> allGroup, String group, int groupIndex, PrepareData prepareData) {
        Set<String> providerApps = FluentIterable
                .from(Iterables.concat(FluentIterable
                        .from(prepareData.relations.get(group).values())
                        .transform(new Function<ProviderAndConsumerApps, Set<String>>() {
                            @Override
                            public Set<String> apply(ProviderAndConsumerApps input) {
                                return input.get(ItemType.PROVIDER);
                            }
                        })))
                .toSet();

        Set<String> relyOnApps = Sets.newHashSet();
        Set<String> relyOnGroups = Sets.newHashSet();
        for (String otherGroup : allGroup) {
            Map<String, ProviderAndConsumerApps> serviceMap = prepareData.relations.get(otherGroup);
            for (ProviderAndConsumerApps providerAndConsumerApps : serviceMap.values()) {
                if (!Sets.intersection(providerApps, providerAndConsumerApps.get(ItemType.CONSUMER)).isEmpty()) {
                    relyOnGroups.add(otherGroup);
                    relyOnApps.addAll(providerAndConsumerApps.get(ItemType.PROVIDER));
                }
            }
        }

        for (String relyOnApp : relyOnApps) {
            buildGraph.addConnection(GROUP_NODE, groupIndex, RELY_ON, apps.add(relyOnApp));
        }
        relyOnGroups.remove(group);
        for (String relyOnGroup : relyOnGroups) {
            buildGraph.addConnection(GROUP_NODE, groupIndex, RELY_ON_GROUP, groups.get(relyOnGroup));
        }
    }

    private void initConnectionsBetweenGroupAndService(NFBuildGraph buildGraph, BiDirectionCollection<String> services,
                                                       Map<Integer, Set<String>> groupServicesMapping) {
        for (Map.Entry<Integer, Set<String>> groupServicesEntry : groupServicesMapping.entrySet()) {
            int groupIndex = groupServicesEntry.getKey();
            for (String service : groupServicesEntry.getValue()) {
                int serviceIndex = services.get(service);
                Preconditions.checkArgument(serviceIndex >= 0, "service should exist: %s", service);
                buildGraph.addConnection(GROUP_NODE, groupIndex, HAS_SERVICE, serviceIndex);
                buildGraph.addConnection(SERVICE_NODE, serviceIndex, SERVICE_BELONG_TO_GROUP, groupIndex);
            }
        }
    }

    private void initIpAndHostnameAddressBuilder(Set<String> providerIpPortsBuilder,
                                                 Set<String> providerHostnamePortsBuilder,
                                                 Map<Integer, Set<Pair<String, String>>> groupAddressMapping) {
        for (Set<Pair<String, String>> addresses : groupAddressMapping.values()) {
            for (Pair<String, String> address : addresses) {
                providerIpPortsBuilder.add(address.getLeft());
                if (!address.getLeft().equals(address.getRight())) {
                    providerHostnamePortsBuilder.add(address.getRight());
                }
            }
        }
    }

    private void initConnectionsWithAddress(NFBuildGraph buildGraph,
                                            BiDirectionCollection<String> providerIpPorts,
                                            BiDirectionCollection<String> providerHostnamePorts,
                                            Map<Integer, Set<Pair<String, String>>> groupAddressMapping) {
        for (Map.Entry<Integer, Set<Pair<String, String>>> groupAddressEntry : groupAddressMapping.entrySet()) {
            int groupIndex = groupAddressEntry.getKey();
            for (Pair<String, String> address : groupAddressEntry.getValue()) {
                String ipPort = address.getLeft();
                String hostnamePort = address.getRight();

                int ipPortIndex = providerIpPorts.get(ipPort);
                buildGraph.addConnection(PROVIDER_IP_PORT_NODE, ipPortIndex, PROVIDER_BELONG_TO_GROUP, groupIndex);

                if (!hostnamePort.equals(ipPort)) {
                    int hostnamePortIndex = providerHostnamePorts.get(hostnamePort);
                    buildGraph.addConnection(PROVIDER_HOSTNAME_PORT_NODE, hostnamePortIndex, PROVIDER_HOSTNAME_TO_IP, ipPortIndex);
                    buildGraph.addConnection(PROVIDER_IP_PORT_NODE, ipPortIndex, PROVIDER_IP_TO_HOSTNAME, hostnamePortIndex);
                }
            }
        }
    }

    private Set<String> getOwners(URL provider) {
        String owners = provider.getParameter(OWNER);
        if (!Strings.isNullOrEmpty(owners)) {
            return ImmutableSet.copyOf(COMMA_SPLITTER.split(owners));
        } else {
            return ImmutableSet.of();
        }
    }

    @Override
    public StatsCount getStats() {
        return graphInfo.statsCount;
    }

    @Override
    public List<String> getAllGroups() {
        return graphInfo.groups;
    }

    @Override
    public List<String> searchGroups(String group) {
        group = Strings.nullToEmpty(group).trim();
        if (group.isEmpty()) {
            return ImmutableList.of();
        }

        return graphInfo.groups.prefixSearch(group);
    }

    @Override
    public List<String> searchService(String service) {
        service = Strings.nullToEmpty(service).trim();
        if (Strings.isNullOrEmpty(service)) {
            return ImmutableList.of();
        }

        return graphInfo.services.prefixSearch(service);
    }

    @Override
    public List<String> searchServiceUseSimpleName(String service) {
        service = Strings.nullToEmpty(service).trim();
        if (Strings.isNullOrEmpty(service)) {
            return ImmutableList.of();
        }

        return graphInfo.servicesSearcher.prefixSearch(service);
    }

    @Override
    public List<String> searchIp(String ip) {
        ip = Strings.nullToEmpty(ip).trim();
        if (Strings.isNullOrEmpty(ip)) {
            return ImmutableList.of();
        }

        return graphInfo.providerIpPorts.prefixSearch(ip);
    }

    @Override
    public List<String> searchHostname(String hostname) {
        hostname = Strings.nullToEmpty(hostname).trim();
        if (Strings.isNullOrEmpty(hostname)) {
            return ImmutableList.of();
        }

        return graphInfo.providerHostnamePorts.prefixSearch(hostname);
    }

    @Override
    public String transformHostnamePortToIpPort(String hostnamePort) {
        hostnamePort = Strings.nullToEmpty(hostnamePort).trim();
        if (Strings.isNullOrEmpty(hostnamePort)) {
            return null;
        }

        Set<String> result = getToNodeValues(graphInfo, hostnamePort, PROVIDER_HOSTNAME_TO_IP);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.iterator().next();
        }
    }

    @Override
    public Set<String> getAddressBelongGroups(String address) {
        return getToNodeValues(graphInfo, address, PROVIDER_BELONG_TO_GROUP);
    }

    @Override
    public Set<String> getGroupsByOwner(String owner) {
        return getToNodeValues(graphInfo, owner, HAS_GROUP);
    }

    static class ProviderAndConsumerApps {

        private Set<String> providerApps = Sets.newHashSet();

        private Set<String> consumerApps = Sets.newHashSet();

        ProviderAndConsumerApps() {
        }

        Set<String> get(ItemType type) {
            if (type == ItemType.PROVIDER) {
                return providerApps;
            } else if (type == ItemType.CONSUMER) {
                return consumerApps;
            } else {
                throw new IllegalArgumentException("illegal type: " + type);
            }
        }
    }

    static class PrepareData {

        Map<String, Map<String, ProviderAndConsumerApps>> relations;
        int serviceCount;
        int providerCount;
        int consumerCount;

        PrepareData(Map<String, Map<String, ProviderAndConsumerApps>> relations, int serviceCount, int providerCount,
                    int consumerCount) {
            this.relations = relations;
            this.serviceCount = serviceCount;
            this.providerCount = providerCount;
            this.consumerCount = consumerCount;
        }
    }

    enum ItemType {
        PROVIDER, CONSUMER
    }

    private PrepareData prepare(Collection<String> allGroup) {
        Map<String, Map<String, ProviderAndConsumerApps>> relations = Maps.newHashMap();
        int serviceCount = 0;
        Map<ItemType, Integer> itemCounts = Maps.newHashMapWithExpectedSize(2);
        itemCounts.put(ItemType.PROVIDER, 0);
        itemCounts.put(ItemType.CONSUMER, 0);
        for (String group : allGroup) {
            Map<String, ProviderAndConsumerApps> serviceApps = Maps.newHashMap();
            Set<String> services = Sets.newHashSet();

            addItemCountsAndServicesAndApps(providerService, ItemType.PROVIDER, group, itemCounts, services, serviceApps);
            addItemCountsAndServicesAndApps(consumerService, ItemType.CONSUMER, group, itemCounts, services, serviceApps);

            serviceCount += services.size();
            relations.put(group, serviceApps);
        }
        return new PrepareData(relations, serviceCount, itemCounts.get(ItemType.PROVIDER), itemCounts.get(ItemType.CONSUMER));
    }

    @SuppressWarnings("unchecked")
    private void addItemCountsAndServicesAndApps(ProviderLevelService providerLevelService, ItemType itemType,
                                                 String group, Map<ItemType, Integer> counts, Set<String> services,
                                                 Map<String, ProviderAndConsumerApps> serviceApps) {
        List<URL> urls = providerLevelService.getItems(group).toList();
        counts.put(itemType, counts.get(itemType) + urls.size());
        for (URL url : urls) {
            String serviceInterface = url.getServiceInterface();
            services.add(serviceInterface);

            String app = GET_APP_FUNC.apply(url);
            if (app != null) {
                ProviderAndConsumerApps providerAndConsumerApps = getOrCreateApps(serviceApps, serviceInterface);
                providerAndConsumerApps.get(itemType).add(app);
            }
        }
    }

    private ProviderAndConsumerApps getOrCreateApps(Map<String, ProviderAndConsumerApps> serviceApps, String serviceInterface) {
        ProviderAndConsumerApps providerAndConsumerApps = serviceApps.get(serviceInterface);
        if (providerAndConsumerApps == null) {
            providerAndConsumerApps = new ProviderAndConsumerApps();
            serviceApps.put(serviceInterface, providerAndConsumerApps);
        }
        return providerAndConsumerApps;
    }

    private static final Function<URL, String> GET_APP_FUNC = new Function<URL, String>() {
        @Override
        public String apply(URL input) {
            String app = input.getParameter(APPLICATION_KEY);
            if (Strings.isNullOrEmpty(app)) {
                return null;
            } else {
                return app;
            }
        }
    };
}
