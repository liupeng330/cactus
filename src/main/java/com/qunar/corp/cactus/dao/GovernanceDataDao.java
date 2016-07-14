package com.qunar.corp.cactus.dao;

import com.qunar.corp.cactus.bean.GovernanceQuery;
import com.qunar.corp.cactus.bean.GovernanceData;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-12-23
 * Time: 上午11:31
 */
public interface GovernanceDataDao {

    long insert(GovernanceData data);

    List<Long> batchInsert(List<GovernanceData> datas);

    void update(GovernanceData governanceData);

    void batchUpdate(List<GovernanceData> governanceDatas);

    /**
     * 按条件查找
     */
    List<GovernanceData> findByCondition(GovernanceQuery query);

    GovernanceData findById(long id);

    List<GovernanceData> findByIds(List<Long> ids);

    List<GovernanceData> selectAll();

    void batchUpdatePath(List<GovernanceData> updates);
}
