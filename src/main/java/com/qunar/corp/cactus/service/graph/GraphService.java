package com.qunar.corp.cactus.service.graph;

import com.qunar.corp.cactus.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-12-16
 * Time: 下午2:31
 */
public interface GraphService {

    Set<String> getRelayOn(String group);

    Set<String> getOwners(String group);

    Pair<Set<String>, Map<String, Set<String>>> getRelayMe(String group);

    void freshGraph();

    boolean isOwnerOfGroup(String username, String group);

    StatsCount getStats();

    List<String> getAllGroups() ;

    List<String> searchService(String service);

    List<String> searchServiceUseSimpleName(String service);

    List<String> searchIp(String ip);

    List<String> searchHostname(String hostname);

    String transformHostnamePortToIpPort(String hostnamePort);

    Set<String> getAddressBelongGroups(String address);

    Set<String> getGroupsByOwner(String owner);

    List<String> searchGroups(String group) ;

    Set<String> getGroupsByService(String service);
}
