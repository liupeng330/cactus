<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>提供者详细信息</title>
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
    <div class="row">
        <%@ include file="../include/sidebar.jsp" %>
        <div class="col-md-10" style="min-height: 500px">
            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">消费者详细信息</h3>
                </div>

                <div class="panel-body">
                    <div style="min-height: 450px">
                        <c:if test="${consumer==null}">
                            该consumer已被注销！
                        </c:if>
                        <c:if test="${consumer!=null}">
                            <table class="table table-striped">
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        服务名
                                    </td>
                                    <td class="wrapTd">
                                            ${consumer.interfaze}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        服务地址
                                    </td>
                                    <td class="wrapTd">
                                        <c:out value="${consumer.url}" escapeXml="true"></c:out>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        机器名
                                    </td>
                                    <td class="wrapTd">
                                        ${consumer.hostName}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        机器ip
                                    </td>
                                    <td class="wrapTd">
                                            ${consumer.address}
                                    </td>
                                </tr>
                                <c:set var="MOCK_KEY_ON_CONFIG_URL" value="newMockState"/>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        状态
                                    </td>
                                    <td class="wrapTd">
                                        <c:if test="${consumer.mock!=''}">
                                            <c:if test="${consumer.mock=='true'}">
                                                <b style="color: green">默认容错</b>
                                            </c:if>
                                            <c:if test="${consumer.mock=='return null'}">
                                                <b style="color: green">默认容错</b>
                                            </c:if>
                                        </c:if>
                                        <c:if test="${consumer.mock==''}">
                                            <c:if test="${consumer.params[MOCK_KEY_ON_CONFIG_URL]==''}">
                                                <b style="color: blue">正常</b>
                                            </c:if>
                                            <c:if test="${consumer.params[MOCK_KEY_ON_CONFIG_URL]=='force:return null'}">
                                                <b style="color: red">已屏蔽</b>
                                            </c:if>
                                            <c:if test="${consumer.params[MOCK_KEY_ON_CONFIG_URL]=='fail:return null'}">
                                                <b style="color: green">已容错</b>
                                            </c:if>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        应用
                                    </td>
                                    <td class="wrapTd">
                                            ${consumer.application}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        Dubbo版本
                                    </td>
                                    <td class="wrapTd">
                                            ${consumer.dubboVersion}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        进程号
                                    </td>
                                    <td class="wrapTd">
                                            ${consumer.pid}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        impl
                                    </td>
                                    <td class="wrapTd">
                                            ${consumer.serviceGroup}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        version
                                    </td>
                                    <td class="wrapTd">
                                            ${consumer.version}
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