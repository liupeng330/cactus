package com.qunar.corp.cactus.service.governance;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.qunar.corp.cactus.bean.GovernanceQuery;
import com.qunar.corp.cactus.dao.GovernanceDataDao;
import com.qunar.corp.cactus.bean.GovernanceData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Date: 13-12-24 Time: 下午2:10
 * 
 * @author: xiao.liang
 * @description:
 */
@Service
public class QueryGovernanceServiceImpl implements IQueryGovernanceService {

    @Resource
    private GovernanceDataDao governanceDataDao;

    @Override
    public GovernanceData getGovernanceDataById(long id) {
        return governanceDataDao.findById(id);
    }

    @Override
    public FluentIterable<GovernanceData> getGovernanceDatas(GovernanceQuery governanceQuery) {
        return FluentIterable.from(governanceDataDao.findByCondition(governanceQuery));
    }

    @Override
    public FluentIterable<GovernanceData> getServiceKeyMatchedGovernanceDatas(GovernanceQuery governanceQuery) {
        final String serviceGroup = Strings.nullToEmpty((String) governanceQuery.get("serviceGroup"));
        final String version = Strings.nullToEmpty((String) governanceQuery.get("version"));
        return getGovernanceDatas(
                GovernanceQuery.createBuilder().fromGovernanceQuery(governanceQuery).removeServiceGroup().removeVersion()
                        .build()).filter(new Predicate<GovernanceData>() {
            @Override
            public boolean apply(GovernanceData input) {
                String inputServiceGroup = Strings.nullToEmpty(input.getServiceGroup());
                String inputVersion = Strings.nullToEmpty(input.getVersion());
                return (Objects.equal(inputServiceGroup, serviceGroup) && Objects.equal(inputVersion, version))
                        || (Objects.equal(inputServiceGroup, "*") && Objects.equal(inputVersion, version))
                        || (Objects.equal(inputServiceGroup, serviceGroup) && Objects.equal(inputVersion, "*"))
                        || (Objects.equal(inputServiceGroup, "*") && Objects.equal(inputVersion, "*"));
            }
        });
    }

}
