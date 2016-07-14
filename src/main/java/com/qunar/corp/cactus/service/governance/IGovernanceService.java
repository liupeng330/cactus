package com.qunar.corp.cactus.service.governance;

import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.bean.GovernanceData;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-12-23
 * Time: 上午11:42
 *
 * 所有治理service的父接口
 * 事件只能这个类发
 */
public interface IGovernanceService {

    /**
     * 启用一条url
     * @param data
     */
    public void enable(GovernanceData data);

    /**
     * 下线一条url
     * @param data
     */
    public void disable(GovernanceData data);

    /**
     * 逻辑删除一条url
     * @param data
     */
    public void delete(GovernanceData data);

    List<Long> addDatas(List<ServiceSign> signs, Map<String, String> params);

    boolean changeDatas(GovernanceData data, Map<String, String> params);
}
