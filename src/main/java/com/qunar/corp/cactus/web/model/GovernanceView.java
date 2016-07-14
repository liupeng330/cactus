package com.qunar.corp.cactus.web.model;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.util.CommonCache;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.PathUtil;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.dubbo.common.Constants.*;

/**
 * Date: 13-12-25 Time: 下午7:48
 * 
 * @author: xiao.liang
 * @description:
 */
public class GovernanceView {
    private final String hostnameAndPort;
    private final String params;
    private final String rule;
    private final GovernanceData governanceData;
    private final String encodeUrl;
    private final String group;

    public GovernanceView(GovernanceData governanceData) {
        this.governanceData = governanceData;
        Map<String, String> parameters = new HashMap<String, String>(governanceData.getUrl().getParameters());
        parameters.remove(CATEGORY_KEY);
        parameters.remove(ConstantHelper.ZKID);
        parameters.remove(ConstantHelper.CACTUS);
        parameters.remove(ConstantHelper.Q_REGISTRY_KEY);
        parameters.remove(VERSION_KEY);
        parameters.remove(ENABLED_KEY);
        parameters.remove(ConstantHelper.NAME);
        parameters.remove(DYNAMIC_KEY);
        parameters.remove(APPLICATION_KEY);
        parameters.remove(ConstantHelper.CACTUS_TIME);
        parameters.remove(ConstantHelper.CACTUS);
        parameters.remove(GROUP_KEY);
        this.params = StringUtils.toQueryString(parameters);
        this.rule = governanceData.getUrl().getParameterAndDecoded(Constants.RULE_KEY);
        this.encodeUrl = URL.encode(governanceData.getUrlStr());
        String colonAndPortStr = governanceData.getUrl().getPort() == 0 ? "" : ":"+governanceData.getUrl().getPort();
        this.hostnameAndPort = CommonCache.getHostNameByIp(governanceData.getUrl().getIp())
                + colonAndPortStr;
        this.group = PathUtil.makePath(governanceData.getGroup());
    }

    public String getHostnameAndPort() {
        return hostnameAndPort;
    }

    public String getParams() {
        return params;
    }

    public String getRule() {
        return rule;
    }

    public GovernanceData getGovernanceData() {
        return governanceData;
    }

    public String getEncodeUrl() {
        return encodeUrl;
    }

    public String getGroup() {
        return group;
    }
}
