<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>调试</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/mockConsumer-14-12-24.js"></script>
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
                    <h3 class="panel-title">调试</h3>
                </div>
                <div class="panel-body">
                    <div style="min-height: 450px">
                        <%@ include file="../include/modal.jsp" %>
                        <input id="rootPath" type="hidden" value="<%=request.getContextPath()%>"/>
                        <input id="zkId" name="zkId" type="hidden" value="${sign.zkId}"/>
                        <input id="needPerformanceTest" name="needPerformanceTest" type="hidden" value="0"/>
                        <div class="row">
                            <div class="col-md-2">
                                应用
                            </div>
                            <div class="col-md-10">
                                ${sign.group}
                                <input id="group" name="group" type="hidden" value="${sign.group}"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                &nbsp;
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-2">
                                服务
                            </div>
                            <div class="col-md-10">
                                ${sign.serviceKey}
                                <input id="serviceGroup" name="serviceGroup" type="hidden"
                                       value="${sign.serviceGroup}"/>
                                <input id="service" name="service" type="hidden" value="${sign.serviceInterface}"/>
                                <input id="version" name="version" type="hidden" value="${sign.version}"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                &nbsp;
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-2">
                                provider
                            </div>
                            <div class="col-md-10">
                                <select id="ipAndPort">
                                    <c:if test="${defaultProviderAddress.ip==''}">
                                        <option value="${defaultProviderAddress.ip}"
                                                selected="selected">${defaultProviderAddress.ip}</option>
                                    </c:if>
                                    <c:if test="${defaultProviderAddress.ip!=''}">
                                        <c:forEach items="${providerAddresses}" var="address">
                                            <c:if test="${address.ip==defaultProviderAddress.ip and address.port==defaultProviderAddress.port}">
                                                <option value="${address.ip}:${address.port}"
                                                        selected="selected">${address.hostname}(${address.ip}):${address.port}</option>
                                            </c:if>
                                            <c:if test="${address.ip!=defaultProviderAddress.ip or address.port!=defaultProviderAddress.port}">
                                                <option value="${address.ip}:${address.port}">${address.hostname}(${address.ip}):${address.port}</option>
                                            </c:if>
                                        </c:forEach>
                                    </c:if>
                                </select>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                &nbsp;
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-2">
                                method
                            </div>
                            <div class="col-md-10">
                                <select id="method">
                                    <c:if test="${defaultMethod==''}">
                                        <option value="${defaultMethod}" selected="selected">${defaultMethod}</option>
                                    </c:if>
                                    <c:if test="${defaultMethod!=''}">
                                        <c:forEach items="${methods}" var="method">
                                            <c:if test="${method==defaultMethod}">
                                                <option value="${method}" selected="selected">${method}</option>
                                            </c:if>
                                            <c:if test="${method!=defaultMethod}">
                                                <option value="${method}">${method}</option>
                                            </c:if>
                                        </c:forEach>
                                    </c:if>
                                </select>
                            </div>
                        </div>
                        <hr/>
                        <div class="row">
                            <div class="col-md-2">
                                input json:
                            </div>
                            <div class="col-md-10">
                                <a target="_blank" href="http://wiki.corp.qunar.com/pages/viewpage.action?pageId=63243280#3.dev%E4%BB%A5%E5%8F%8Abeta%E6%B5%8B%E8%AF%95dubbo%E6%8E%A5%E5%8F%A3-%E8%BE%93%E5%85%A5%E6%A0%BC%E5%BC%8F">格式说明</a>
                                <br><br>
                                <textarea name="parameter" id="parameter" cols="30" rows="10"
                                          style="width: 100%"></textarea>
                            </div>
                        </div>
                        <hr/>
                        <div class="row">
                            <div class="col-md-2">
                                result json:
                                <br><br><br>
                                <input class="btn btn-info btn-sm" type="button" value="调用"
                                       onclick="submitInvoke('<%=request.getContextPath()%>/mock/invoke')"/>
                            </div>
                            <div class="col-md-10">
                                按ctrl+enter即调用，timeout为10s
                                <br>
                                call state:
                                <span id="resultState" style="min-width: 70px; display:inline-block"></span>
                                call time:
                                <span id="callTime"></span>
                                <br><br>
                                <textarea name="result" id="result" cols="30" rows="10" style="width: 100%"
                                          readonly></textarea>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>

    </div>
    <script>
        $(document).ready(function () {
            document.onkeydown = function () {
                var oEvent = window.event;
                if (oEvent.keyCode == 13 && oEvent.ctrlKey) {
                    submitInvoke('<%=request.getContextPath()%>/mock/invoke');
                }
            }
        });
    </script>
</body>
</html>