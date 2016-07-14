package com.qunar.corp.cactus.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.GovernanceQuery;
import com.qunar.corp.cactus.dao.GovernanceDataDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Date: 13-12-23 Time: 下午8:33
 *
 * @author: xiao.liang
 * @description:
 */
@Repository
public class GovernanceDataDaoImpl extends AbstractDao implements GovernanceDataDao {

    private static final String ID_KEY = "id";
    private static final String IDS_KEY = "ids";

    @Override
    public long insert(GovernanceData data) {
       List<Long> ids = batchInsert(Arrays.asList(data));
        return ids.size()>0 ? ids.get(0) : -1l;
    }

    @Override
    @Transactional
    public List<Long> batchInsert(List<GovernanceData> datas) {
        List<Long> ids = Lists.newArrayList();
        if (datas.isEmpty()) {
            return ids;
        }
        for (GovernanceData data : datas){
            sqlSession.insert("governance.insert", data);
            ids.add(data.getId());
        }
        return ids;
    }

    @Override
    public void update(GovernanceData governanceData) {
        sqlSession.update("governance.update", governanceData);
    }

    @Override
    @Transactional
    public void batchUpdate(List<GovernanceData> governanceDatas) {
        for (GovernanceData data : governanceDatas) {
           update(data);
        }
    }

    @Override
    public List<GovernanceData> findByCondition(GovernanceQuery query) {
        return sqlSession.selectList("governance.select", query);
    }

    @Override
    public GovernanceData findById(long id) {
        if (id <= 0) {
            return null;
        }
        Map<String, Object> param = Maps.newHashMapWithExpectedSize(1);
        param.put(ID_KEY, id);
        return sqlSession.selectOne("governance.select", param);
    }

    @Override
    public List<GovernanceData> findByIds(List<Long> ids) {
        Map<String, Object> param = Maps.newHashMapWithExpectedSize(1);
        param.put(IDS_KEY, ids);
        return sqlSession.selectList("governance.select", param);
    }

    @Override
    public List<GovernanceData> selectAll() {
        return sqlSession.selectList("governance.select");
    }

    @Override
    @Transactional
    public void batchUpdatePath(List<GovernanceData> updates) {
        for (GovernanceData update : updates) {
            logger.info("update: {}", update);
            sqlSession.update("governance.updatePath", update);
        }
    }
}
