<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>添加动态配置</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/configAddParam-14-12-24.js"></script>
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
                    <h3 class="panel-title">添加动态配置</h3>
                </div>
                <div class="panel-body">
                    <div style="min-height: 450px">
                        <%@ include file="../include/modal.jsp" %>
                        <input id="rootPath" type="hidden" value="<%=request.getContextPath()%>"/>
                        <input id="id" name="id" type="hidden" value="${id}"/>
                        <input id="zkId" name="zkId" type="hidden" value="${zkid}"/>
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
                                impl
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
                            <div class="col-md-2">
                                名称
                            </div>
                            <div class="col-md-10">
                                <c:if test="${id==EMPTY_ID}">
                                    <input id="name" name="name" type="input" value="${name}"/>
                                </c:if>
                                <c:if test="${id!=EMPTY_ID}">
                                    <input id="name" name="name" type="text" value="${name}" disabled="disabled"/>
                                </c:if>
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
                                <c:if test="${id!=EMPTY_ID}">
                                    <input id="hostname" style="width: 300px" type="text" value="${defaultHostName}" disabled="disabled"/>
                                    <select id="providerList">
                                        <option value="请选择">请选择</option>
                                        <c:forEach items="${providerList}" var="provider">
                                            <option value="${provider.hostNameAndPort}">${provider.hostNameAndPort}</option>
                                        </c:forEach>
                                    </select>
                                    <c:forEach items="${providerList}" var="provider">
                                        <input id="${provider.hostNameAndPort}" type="hidden" value="${provider.address}"/>
                                    </c:forEach>
                                </c:if>
                                <c:if test="${id==EMPTY_ID}">
                                    <input id="hostname" style="width: 300px" type="text" value="${defaultHostName}"/>
                                    <select id="providerList" onchange="selectAddress()">
                                        <option value="请选择">请选择</option>
                                        <c:forEach items="${providerList}" var="provider">
                                            <option value="${provider.hostNameAndPort}">${provider.hostNameAndPort}</option>
                                        </c:forEach>
                                    </select>
                                    <c:forEach items="${providerList}" var="provider">
                                        <input id="${provider.hostNameAndPort}" type="hidden" value="${provider.address}"/>
                                    </c:forEach>

                                </c:if>
                                <div class="wrapTd" style="color: red">多个地址以","号分隔，只能填写0.0.0.0或provider地址，0.0.0.0表示对所有provider生效</div>
                            </div>
                        </div>
                        <hr/>
                        <div class="row">
                            <div class="col-md-2">
                                动态配置
                            </div>
                            <div class="col-md-10">
                                <input id="params" name="params" type="hidden" value="${params}"/>
                            </div>
                        </div>
                        <div id="paramField">
                            <%--<div class="row">--%>
                            <%--<div class="col-md-1"></div>--%>
                            <%--<div class="col-md-11">--%>
                            <%--<div class="form-inline">--%>
                            <%--参数名：<input name="key" class="form-control" style="width: 100px;" type="text"/>--%>
                            <%--&nbsp; &nbsp;--%>
                            <%--参数值：<input name="value" class="form-control" style="width: 200px;" type="text"/>--%>
                            <%--</div>--%>
                            <%--</div>--%>
                            <%--</div>--%>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                &nbsp;
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <div class="col-md-1"></div>
                                <button type="button" class="btn  btn-default btn-sm" onclick="addParam()">
                                    新增参数
                                </button>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-10"></div>
                            <button id="btn_submit" type="button" class="btn  btn-primary btn-sm"
                                    data-loading-text="正在保存..."
                                    onclick="loadParamAndSubmit()">
                                保存
                            </button>
                        </div>
                    </div>
                </div>
            </div>

        </div>

    </div>
    <script>
        $(document).ready(function () {
            setParamsToInput();
        });
    </script>
</body>
</html>