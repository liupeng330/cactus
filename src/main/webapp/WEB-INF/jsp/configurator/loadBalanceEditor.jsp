<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>设置负载均衡策略</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/configAddParam-14-12-24.js"></script>
</head>
<body>

<div class="container">
    <%@ include file="../include/header.jsp" %>
    <div class="row">
        <%@ include file="../include/sidebar.jsp" %>
        <div class="col-md-10" style="min-height: 500px">
            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">设置负载均衡策略</h3>
                </div>
                <div class="panel-body">
                    <div style="min-height: 450px">
                        <%@ include file="../include/modal.jsp" %>
                        <input id="rootPath" type="hidden" value="<%=request.getContextPath()%>"/>
                        <input id="id" type="hidden" value="${id}"/>
                        <input id="zkId" type="hidden" value="${zkid}"/>
                        <div class="row">
                            <div class="col-md-2">
                                应用
                            </div>
                            <div class="col-md-10">
                                ${group}
                                <input id="group" name="group" type="hidden" value="${group}"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                &nbsp;
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-2">
                                ServiceGroup
                            </div>
                            <div class="col-md-10">
                                ${serviceGroup}
                                <input id="serviceGroup" name="serviceGroup" type="hidden" value="${serviceGroup}"/>
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
                                ${service}
                                <input id="service" name="service" type="hidden" value="${service}"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                &nbsp;
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-2">
                                version
                            </div>
                            <div class="col-md-10">
                                ${version}
                                <input id="version" name="version" type="hidden" value="${version}"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                &nbsp;
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                &nbsp;
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-2">
                                负载均衡策略
                            </div>
                            <div class="col-md-10">
                                <select name="loadBalance" id="loadBalance">
                                    <c:forEach items="${loadbalanceList}" var="loadbalanceItem">
                                        <c:if test="${loadbalanceItem==loadBalance}">
                                            <option value="${loadbalanceItem}"
                                                    selected="true">${loadbalanceItem}</option>
                                        </c:if>
                                        <c:if test="${loadbalanceItem!=loadBalance}">
                                            <option value="${loadbalanceItem}">${loadbalanceItem}</option>
                                        </c:if>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-10"></div>
                            <button id="btn_save" type="button" data-loading-text="正在保存..."
                                    class="btn  btn-primary btn-sm"
                                    onclick="configLoadBalance()">
                                保存
                            </button>
                        </div>
                    </div>
                </div>
            </div>

        </div>

    </div>
</div>
</body>
</html>