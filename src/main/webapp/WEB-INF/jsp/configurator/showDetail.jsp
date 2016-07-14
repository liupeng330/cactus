<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>首页</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
</head>
<body>

<div class="container">
    <%@ include file="../include/header.jsp" %>
    <%@ include file="../include/constantUtil.jsp" %>
    <div class="row">
        <%@ include file="../include/sidebar.jsp" %>
        <div class="col-md-10" style="min-height: 500px">
            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">动态配置详细信息</h3>
                </div>

                <div class="panel-body">
                    <div style="min-height: 450px">
                        <c:if test="${overrideUrl==null}">
                            该动态配置已被注销！
                        </c:if>
                        <c:if test="${overrideUrl!=null}">
                            <table class="table table-striped">
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        名称
                                    </td>
                                    <td class="wrapTd">
                                            ${overrideUrl.governanceData.name}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        应用
                                    </td>
                                    <td class="wrapTd">
                                            ${overrideUrl.group}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        服务
                                    </td>
                                    <td class="wrapTd">
                                            ${overrideUrl.governanceData.serviceName}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        URL
                                    </td>
                                    <td class="wrapTd">
                                        <c:out value="${overrideUrl.governanceData.urlStr}" escapeXml="true"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        机器
                                    </td>
                                    <td class="wrapTd">
                                            ${overrideUrl.hostnameAndPort}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        机器ip
                                    </td>
                                    <td class="wrapTd">
                                            ${overrideUrl.governanceData.url.address}
                                    </td>
                                </tr>
                                <c:set var="MOCK_KEY_ON_CONFIG_URL" value="newMockState"/>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        状态
                                    </td>
                                    <td class="wrapTd">
                                        <c:if test="${overrideUrl.governanceData.status.code == ONLINE}">
                                            <b style="color: green;">启用</b>
                                        </c:if>
                                        <c:if test="${overrideUrl.governanceData.status.code == OFFLINE}">
                                            <b style="color: red">禁用</b>
                                        </c:if>
                                        <c:if test="${overrideUrl.governanceData.status.code == DELETE}">
                                            <b style="color: red">删除</b>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        impl
                                    </td>
                                    <td class="wrapTd">
                                            ${overrideUrl.governanceData.serviceGroup}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        version
                                    </td>
                                    <td class="wrapTd">
                                            ${overrideUrl.governanceData.version}
                                    </td>
                                </tr>
                            </table>
                        </c:if>
                    </div>

                </div>
            </div>
        </div>

    </div>

</div>
</body>
</html>