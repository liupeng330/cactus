package com.qunar.corp.cactus.bean;

import com.alibaba.dubbo.common.URL;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.PathUtil;
import com.qunar.corp.cactus.web.serializer.*;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA. User: liuzz Date: 13-12-20 Time: 下午3:57
 */
public class GovernanceData implements Serializable {

    private Long id;

    private Long zkId;

    private String name;

    private String group;

    private String ip;

    private Integer port;

    private String serviceGroup;

    private String version;

    private String serviceName;

    private String urlStr;

    private URL url;

    // override provider consumer or router
    private PathType pathType;

    private String lastOperator;

    // delete online offline
    private Status status;

    // zkdata or userdata
    private DataType dataType;

    private Timestamp createTime;

    private Timestamp lastUpdateTime;

    public GovernanceData() {
    }

    public Long getId() {
        return id;
    }

    public GovernanceData setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getZkId() {
        return zkId;
    }

    public GovernanceData setZkId(Long zkId) {
        this.zkId = zkId;
        return this;
    }

    @JsonSerialize(using = JsonStatusSerializer.class)
    public Status getStatus() {
        return status;
    }

    @JsonDeserialize(using = JsonStatusDeSerializer.class)
    public GovernanceData setStatus(Status status) {
        this.status = status;
        return this;
    }

    public String getName() {
        return name;
    }

    public GovernanceData setName(String name) {
        this.name = name;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public GovernanceData setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public GovernanceData setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public GovernanceData setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getAddress() {
        return port > 0 ? ip + ":" + port : ip;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public GovernanceData setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public GovernanceData setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public GovernanceData setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getUrlStr() {
        return urlStr;
    }

    public GovernanceData setUrlStr(String urlStr) {
        this.urlStr = urlStr;
        this.url = URL.valueOf(urlStr);
        return this;
    }

    // 由于数据库中url里面不包含这个属性，所以，getUrl时取到的url会取不到这个属性，
    //未来有任何新的参数，统一加到这个方法中。
    public URL getFullUrl() {
        if (url == null) {
            return null;
        }
        return url.addParameter(ConstantHelper.ZKID, zkId)
                .addParameter(ConstantHelper.FAKE_Q_REGISTRY_KEY, PathUtil.makePath(group));
    }

    @JsonSerialize(using = JsonUrlSerializer.class)
    public URL getUrl() {
        return url;
    }

    @JsonDeserialize(using = JsonUrlDeSerializer.class)
    public GovernanceData setUrl(URL url) {
        this.url = url;
        this.urlStr = url.toFullString();
        return this;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    public Timestamp getCreateTime() {
        return createTime;
    }

    @JsonDeserialize(using = JsonDateDeSerializer.class)
    public GovernanceData setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
        return this;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    @JsonDeserialize(using = JsonDateDeSerializer.class)
    public GovernanceData setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }

    @JsonSerialize(using = JsonPathTypeSerializer.class)
    public PathType getPathType() {
        return pathType;
    }

    @JsonDeserialize(using = JsonPathTypeDeSerializer.class)
    public GovernanceData setPathType(PathType pathType) {
        this.pathType = pathType;
        return this;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    public DataType getDataType() {
        return dataType;
    }

    @JsonDeserialize(using = JsonDataTypeDeSerializer.class)
    public GovernanceData setDataType(DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    public String getLastOperator() {
        return lastOperator;
    }

    public GovernanceData setLastOperator(String lastOperator) {
        this.lastOperator = lastOperator;
        return this;
    }

    public ServiceSign getServiceSign() {
        return ServiceSign.makeServiceSign(zkId, group, ip == null ? null : IpAndPort.fromPart(ip, port).getAddress(),
                serviceGroup, serviceName, version);
    }

    public GovernanceData copy() {
        GovernanceData data = new GovernanceData();
        data.id = this.id;
        data.zkId = this.zkId;
        data.name = this.name;
        data.group = this.group;
        data.ip = this.ip;
        data.port = this.port;
        data.serviceGroup = this.serviceGroup;
        data.version = this.version;
        data.serviceName = this.serviceName;
        data.urlStr = this.urlStr;
        data.url = this.url;
        data.pathType = this.pathType;
        data.lastOperator = this.lastOperator;
        data.status = this.status;
        data.dataType = this.dataType;
        data.createTime = this.createTime;
        data.lastUpdateTime = this.lastUpdateTime;
        return data;
    }

    @Override
    public String toString() {
        return "GovernanceData{" +
                "id=" + id +
                ", zkId=" + zkId +
                ", name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", serviceGroup='" + serviceGroup + '\'' +
                ", version='" + version + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", urlStr='" + urlStr + '\'' +
                ", url=" + url +
                ", pathType=" + pathType +
                ", lastOperator='" + lastOperator + '\'' +
                ", status=" + status +
                ", dataType=" + dataType +
                ", createTime=" + createTime +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
