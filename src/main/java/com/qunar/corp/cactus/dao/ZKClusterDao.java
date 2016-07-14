package com.qunar.corp.cactus.dao;

import com.qunar.corp.cactus.bean.ZKCluster;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-10-28
 * Time: 下午6:57
 */
public interface ZKClusterDao {

    List<ZKCluster> findAllCluster();

    List<ZKCluster> findByCondition(Map<String,Object> param);

    ZKCluster insertCluster(ZKCluster zkCluster);
}
