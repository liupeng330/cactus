package com.qunar.corp.cactus.service;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.qunar.corp.cactus.bean.RegisterAndUnRegisters;
import com.qunar.corp.cactus.bean.ZKCluster;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: liuzz Date: 13-10-28 Time: 下午6:59
 */
public interface ZKClusterService {

    List<ZKCluster> getZKClusters();

    Set<String> getAllGroup();

    Set<String> getServiceNamesByGroup(String path);

    boolean addNewZK(ZKCluster zkCluster) throws Exception;

    Optional<Long> findZkId(URL registry);

    Optional<ZKCluster> findZkCluster(long zkClusterId);

    Set<String> getChildren(String path);

    FluentIterable<URL> getUrls(String group, String serviceInterface, String category);

    void writeDatas(RegisterAndUnRegisters datas);

    boolean isPreServicePath(String path);
}
