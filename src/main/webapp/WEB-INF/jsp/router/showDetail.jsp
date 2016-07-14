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
                    <h3 class="panel-title">路由规则详细信息</h3>
                </div>

                <div class="panel-body">
                    <div style="min-height: 450px">
                        <c:if test="${router==null}">
                            该路由规则已被注销！
                        </c:if>
                        <c:if test="${router!=null}">
                            <table class="table table-striped">
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        名称
                                    </td>
                                    <td class="wrapTd">
                                            ${router.governanceData.name}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        应用
                                    </td>
                                    <td class="wrapTd">
                                            ${router.group}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        服务
                                    </td>
                                    <td class="wrapTd">
                                            ${router.governanceData.serviceName}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        URL
                                    </td>
                                    <td class="wrapTd">
                                        <c:out value="${router.governanceData.urlStr}" escapeXml="true"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        机器
                                    </td>
                                    <td class="wrapTd">
                                            ${router.hostnameAndPort}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        机器ip
                                    </td>
                                    <td class="wrapTd">
                                            ${router.governanceData.url.address}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        状态
                                    </td>
                                    <td class="wrapTd">
                                        <c:if test="${router.governanceData.status.code == ONLINE}">
                                            <b style="color: green;">启用</b>
                                        </c:if>
                                        <c:if test="${router.governanceData.status.code == OFFLINE}">
                                            <b style="color: red">禁用</b>
                                        </c:if>
                                        <c:if test="${router.governanceData.status.code == DELETE}">
                                            <b style="color: red">删除</b>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        impl
                                    </td>
                                    <td class="wrapTd">
                                            ${router.governanceData.serviceGroup}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        version
                                    </td>
                                    <td class="wrapTd">
                                            ${router.governanceData.version}
                                    </td>
                                </tr>
                            </table>
                            <hr/>
                            <p style="font-weight: bold;font-size: 15px"> 路由规则相关参数 </p>
                            <table class="table table-striped">
                                <tr>
                                    <td class="wrapTd title-16percent-td">
                                        匹配的消费者IP
                                    </td>
                                    <td class="wrapTd">
                                        <c:forEach items="${consumerMatchIp}" var="consumer">
                                             ${consumer.left}(${consumer.right})<br/>
                                        </c:forEach>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-16percent-td">
                                        不匹配的消费者IP
                                    </td>
                                    <td class="wrapTd">
                                        <c:forEach items="${consumerMismatchIp}" var="consumer">
                                            ${consumer.left}(${consumer.right})<br/>
                                        </c:forEach>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-16percent-td">
                                        匹配的提供者IP
                                    </td>
                                    <td class="wrapTd">
                                        <c:forEach items="${providerMatchIp}" var="provider">
                                            ${provider.left}(${provider.right})<br/>
                                        </c:forEach>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-16percent-td">
                                        不匹配的提供者IP
                                    </td>
                                    <td class="wrapTd">
                                        <c:forEach items="${providerMismatchIp}" var="provider">
                                            ${provider.left}(${provider.right})<br/>
                                        </c:forEach>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-16percent-td">
                                        匹配的消费者应用名
                                    </td>
                                    <td class="wrapTd">
                                        ${routerParams[CONSUMER_MATCH_APP_KEY]}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-16percent-td">
                                        不匹配的消费者应用名
                                    </td>
                                    <td class="wrapTd">
                                            ${routerParams[CONSUMER_MISMATCH_APP_KEY]}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-16percent-td">
                                        匹配的消费者方法名
                                    </td>
                                    <td class="wrapTd">
                                            ${routerParams[CONSUMER_MATCH_METHOD_KEY]}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-16percent-td">
                                        不匹配的消费者方法名
                                    </td>
                                    <td class="wrapTd">
                                            ${routerParams[CONSUMER_MISMATCH_METHOD_KEY]}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-16percent-td">
                                        匹配的提供者端口
                                    </td>
                                    <td class="wrapTd">
                                            ${routerParams[PROVIDER_MATCH_PORT_KEY]}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-16percent-td">
                                        不匹配的提供者端口
                                    </td>
                                    <td class="wrapTd">
                                            ${routerParams[PROVIDER_MISMATCH_PORT_KEY]}
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