package com.qunar.corp.cactus.support.protocol;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.DataType;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.PathType;
import com.qunar.corp.cactus.bean.Status;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.PathUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Map;

import static com.alibaba.dubbo.common.Constants.*;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.qunar.corp.cactus.util.ConstantHelper.FAKE_Q_REGISTRY_KEY;
import static com.qunar.corp.cactus.util.ConstantHelper.Q_REGISTRY_KEY;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-11-15
 * Time: 上午11:46
 */
public abstract class AbstractProtocolBuilder implements ProtocolBuilder {

    protected long zkId;
    protected String ip;
    protected int port = 0;
    protected String registryKey;
    protected String version;
    protected Map<String, String> params = Maps.newHashMap();
    protected String serviceName;
    protected String serviceGroup;

    @Override
    public ProtocolBuilder zkId(long zkId) {
        this.zkId = zkId;
        return this;
    }

    @Override
    public ProtocolBuilder ip(String ip) {
        this.ip = ip;
        return this;
    }

    @Override
    public ProtocolBuilder port(int port) {
        this.port = port;
        return this;
    }

    @Override
    public ProtocolBuilder service(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    @Override
    public ProtocolBuilder registryKey(String key) {
        this.registryKey = key;
        return this;
    }

    @Override
    public ProtocolBuilder serviceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
        return this;
    }

    @Override
    public ProtocolBuilder version(String version) {
        this.version = version;
        return this;
    }

    @Override
    public ProtocolBuilder withKeyValue(String key, String value) {
        if (!Strings.isNullOrEmpty(key) && !Strings.isNullOrEmpty(value)) {
            params.put(key, value);
        }
        return this;
    }

    @Override
    public ProtocolBuilder addParams(Map<String, String> params) {
        if (params != null) {
            this.params.putAll(params);
        }
        return this;
    }

    protected abstract String getProtocol();

    protected abstract String getCategory();

    protected abstract Map<String, String> getOriginParams();

    @Override
    public GovernanceData build() {
        GovernanceData data = new GovernanceData();

        Map<String, String> resultParams = getOriginParams();

        data.setZkId(zkId);

        resultParams.remove(FAKE_Q_REGISTRY_KEY);

        String group = PathUtil.getGroup(registryKey);
        data.setGroup(group);

        if (!isNullOrEmpty(registryKey)) {
            resultParams.put(Q_REGISTRY_KEY, registryKey);
        }

        if (!isNullOrEmpty(serviceGroup)) {
            resultParams.put(GROUP_KEY, serviceGroup);
        }
        data.setServiceGroup(nullToEmpty(serviceGroup));

        if (!isNullOrEmpty(version)) {
            resultParams.put(VERSION_KEY, version);
        }
        data.setVersion(nullToEmpty(version));

        data.setName(nullToEmpty(resultParams.get(ConstantHelper.NAME)));

        if (!isNullOrEmpty(resultParams.get(ConstantHelper.STATUS))) {
            data.setStatus(Status.fromText(resultParams.get(ConstantHelper.STATUS)));
            resultParams.remove(ConstantHelper.STATUS);
        } else {
            data.setStatus(Status.DISABLE);
        }

        if (!isNullOrEmpty(resultParams.get(ConstantHelper.DATA_TYPE))) {
            data.setDataType(DataType.fromText(resultParams.get(ConstantHelper.DATA_TYPE)));
            resultParams.remove(ConstantHelper.DATA_TYPE);
        } else {
            data.setDataType(DataType.USERDATA);
        }

        resultParams.put(DYNAMIC_KEY, "false");
        resultParams.put(CATEGORY_KEY, getCategory());
        resultParams.remove(ENABLED_KEY);
        resultParams.put(ConstantHelper.CACTUS, ConstantHelper.CACTUS_VERSION);
        resultParams.put(ConstantHelper.CACTUS_TIME, TIME_FORMATTER.print(DateTime.now()));
        if (!Strings.isNullOrEmpty(resultParams.get(DISABLED_KEY))) {
            resultParams.remove(ConstantHelper.CACTUS_TIME);
        }
        data.setUrl(new URL(getProtocol(), ip, port, serviceName, resultParams));

        data.setServiceName(serviceName);
        data.setPathType(PathType.fromText(getProtocol()));
        data.setLastOperator(UserContainer.getUserName());
        data.setIp(ip);
        data.setPort(port);
        return data;
    }

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");
}
