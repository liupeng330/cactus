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
                    <h3 class="panel-title">设置mock策略</h3>
                </div>
                <div class="panel-body">
                    <div style="min-height: 450px">
                        <%@ include file="../include/modal.jsp" %>
                        <c:set var="data" value="${governanceView.governanceData}"></c:set>
                        <input id="rootPath" type="hidden" value="<%=request.getContextPath()%>"/>
                        <input id="id" type="hidden" value="${data.id}"/>
                        <input id="zkId" type="hidden" value="${data.zkId}"/>

                        <div class="row">
                            <div class="col-md-2">
                                应用
                            </div>
                            <div class="col-md-10">
                                ${governanceView.group}
                                <input id="group" name="group" type="hidden" value="${data.group}"/>
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
                                ${data.serviceGroup}
                                <input id="serviceGroup" name="serviceGroup" type="hidden"
                                       value="${data.serviceGroup}"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                &nbsp;
                            </div>
                        </div>
                        <c:if test="${data.serviceGroup!=''}">
                            <c:if test="${data.version==''}">
                                <c:set var="SERVICE_KEY" value="${data.serviceGroup}/${data.serviceName}"/>
                            </c:if>
                            <c:if test="${data.version!=''}">
                                <c:set var="SERVICE_KEY"
                                       value="${data.serviceGroup}/${data.serviceName}:${data.version}"/>
                            </c:if>
                        </c:if>
                        <c:if test="${data.serviceGroup==''}">
                            <c:if test="${data.version==''}">
                                <c:set var="SERVICE_KEY" value="${data.serviceName}"/>
                            </c:if>
                            <c:if test="${data.version!=''}">
                                <c:set var="SERVICE_KEY" value="${data.serviceName}:${data.version}"/>
                            </c:if>
                        </c:if>
                        <input id="serviceKey" type="hidden" value="${SERVICE_KEY}"/>

                        <div class="row">
                            <div class="col-md-2">
                                服务
                            </div>
                            <div class="col-md-10">
                                ${data.serviceName}
                                <input id="service" name="service" type="hidden" value="${data.serviceName}"/>
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
                                ${data.version}
                                <input id="version" name="version" type="hidden" value="${data.version}"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                &nbsp;
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-2">
                                地址
                            </div>
                            <div class="col-md-10">
                                ${data.url.address}
                                <input id="address" name="address" type="hidden" value="${data.url.address}"/>
                            </div>
                        </div>
                        <input id="app" type="hidden" value="${data.url.parameters['application']}"/>

                        <div class="row">
                            <div class="col-md-12">
                                &nbsp;
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-2">
                                设置
                            </div>
                            <div class="col-md-10">
                                <select name="mockType" id="mockType">
                                    <c:forEach items="${mockList}" var="mockItem">
                                        <c:if test="${mockItem.text==data.url.parameters['mock']}">

                                            <option value="${mockItem.text}"
                                                    selected="selected">
                                                <c:if test="${mockItem.text=='force:return+null'}">
                                                    屏蔽
                                                </c:if>
                                                <c:if test="${mockItem.text=='fail:return+null'}">
                                                    容错
                                                </c:if>
                                                <c:if test="${mockItem.text=='nonemock'}">
                                                    恢复
                                                </c:if>
                                            </option>
                                        </c:if>
                                        <c:if test="${mockItem.text!=data.url.parameters['mock']}">
                                            <option value="${mockItem.text}">
                                                <c:if test="${mockItem.text=='force:return+null'}">
                                                    屏蔽
                                                </c:if>
                                                <c:if test="${mockItem.text=='fail:return+null'}">
                                                    容错
                                                </c:if>
                                                <c:if test="${mockItem.text=='nonemock'}">
                                                    恢复
                                                </c:if>
                                            </option>
                                        </c:if>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-10"></div>
                            <button id="btn_save" type="button" data-loading-text="正在保存..."
                                    class="btn  btn-primary btn-sm"
                                    onclick="updateMock('<%=request.getContextPath()%>/configurator/setMock','btn_save')">
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