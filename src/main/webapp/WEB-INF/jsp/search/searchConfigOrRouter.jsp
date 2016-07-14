<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html>
<head>
    <title>配置查询</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/datetimepicker.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/configAddParam-14-12-24.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/operateMachine-14-12-24.js"></script>
    <style type="text/css">
        table tbody:last-child {
            border-bottom: 2px solid white;
        }
    </style>
</head>
<body>

<div class="container">
<%@ include file="../include/header.jsp" %>
<%@ include file="../include/constantUtil.jsp" %>
<div class="row">
<%@ include file="../include/sidebar.jsp" %>
<div class="col-md-10">

<div class="panel panel-default" style="min-height: 500px">
<div class="panel-heading">
    <h3 class="panel-title">配置查询</h3>
</div>
<div class="panel-body" style="text-align: center">
<div style="min-height: 450px">
<input id="rootPath" type="hidden" value="<%=request.getContextPath()%>"/>

<div style="padding-top: 30px">
    <form action="<%=request.getContextPath()%>/search/searchConfigOrRouter" method="get">
        <input id="pathType" name="pathType" type="hidden" value="${pathType}"/>

        <table style="border-color: #ffffff;width: 100%">
            <tr>
                <td style="width: 45%;text-align: right;">
                    配置：
                    <input id="name" style="width:150px" type="text"
                           name="name"
                           value="${name}"
                           placeholder="请输入要查询的配置名"/>
                </td>
                <td style="text-align: right;">
                    应用：
                </td>
                <td style="text-align: left;">
                    <input id="group" style="width:150px" type="text"
                           name="group"
                           value="${group}"
                           autocomplete="off"
                           placeholder="请输入要查询的应用名"/>
                </td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td style="text-align: right;">
                    服务：
                    <input id="serviceName" style="width:150px" type="text"
                           name="serviceName"
                           value="${serviceName}"
                           autocomplete="off"
                           placeholder="请输入要查询的服务名"/>
                </td>
                <td style="text-align: right;">
                    状态：
                </td>
                <td style="text-align: left;">
                    <c:set var="NONE_STATUS" value="-1"/>
                    <select name="status" style="width:150px" id="status">
                        <c:choose>
                            <c:when test="${status == ONLINE}">
                                <option value="${ONLINE}" selected="selected">
                                    启用
                                </option>
                            </c:when>
                            <c:otherwise>
                                <option value="${ONLINE}">
                                    启用
                                </option>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${status == OFFLINE}">
                                <option value="${OFFLINE}" selected="selected">
                                    禁用
                                </option>
                            </c:when>
                            <c:otherwise>
                                <option value="${OFFLINE}">
                                    禁用
                                </option>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${status == DELETE}">
                                <option value="${DELETE}" selected="selected">
                                    删除
                                </option>
                            </c:when>
                            <c:otherwise>
                                <option value="${DELETE}">
                                    删除
                                </option>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${status == NONE_STATUS}">
                                <option value="${NONE_STATUS}" selected="selected">
                                    全部
                                </option>
                            </c:when>
                            <c:otherwise>
                                <option value="${NONE_STATUS}">
                                    全部
                                </option>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </td>


            </tr>
            <c:if test="${pathType == ROUTER}">
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td style="text-align: right;">
                        机器：
                        <input id="machine" style="width:150px" type="text"
                               name="machine"
                               value="${machine}"
                               autocomplete="off"
                               placeholder="请输入机器名或ip"/>
                    </td>
                    <td colspan="2" style="text-align: left">
                        <span style="font-size: 10px;color:red;">注：机器不支持模糊搜索，且需要同时指定应用名</span>
                    </td>
                </tr>
            </c:if>
            <tr>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td>
                    <input type="submit" class="btn btn-default btn-sm" value="查询"/>
                </td>
            </tr>
        </table>
    </form>
</div>
<div>
<br/>
<%@ include file="../include/modal.jsp" %>
<p style="color: red;font-weight: bold">${errorInfo}</p>
<table class="table table-responsive">
    <tr>
        <th class="wrapTd td-10-percent text-align-center">
            配置名
        </th>
        <th class="wrapTd td-15-percent text-align-center">
            机器名
        </th>
        <th class="wrapTd td-20-percent text-align-center">
            参数
        </th>
        <th class="wrapTd td-5-percent text-align-center">
            状态
        </th>
        <th class="wrapTd td-15-percent text-align-center">
            操作时间
        </th>
        <th class="wrapTd td-15-percent text-align-center">
            操作
        </th>
    </tr>

</table>
<table class="table table-bordered" style="table-layout:fixed;">
<col class="wrapTd td-10-percent"/>
<col class="wrapTd td-20-percent"/>
<col class="wrapTd td-25-percent"/>
<col class="wrapTd td-10-percent"/>
<col class="wrapTd td-20-percent"/>
<col class="wrapTd td-15-percent"/>
<c:forEach items="${list}" var="governanceView" varStatus="row">

<tr class="titleBg">

    <td colspan="6" class="wrapTd" style="vertical-align: middle;padding-top: 2px;text-align: left">

        <div class="groupTitleField">
            应用：${governanceView.group}
        </div>
        <div class="serviceTitleField">
            服务： <c:if
                test="${governanceView.governanceData.serviceGroup!='' and governanceView.governanceData.serviceGroup!=null}">
            ${governanceView.governanceData.serviceGroup}/
        </c:if>
                ${governanceView.governanceData.serviceName}
            <c:if test="${governanceView.governanceData.version!='' and governanceView.governanceData.version!=null}">
                :${governanceView.governanceData.version}
            </c:if>
        </div>
        <div class="lastOperatorField">
            最后操作： ${governanceView.governanceData.lastOperator}
        </div>
    </td>
</tr>
<tr>
    <td>
        <div class="over_c" style="width: 100%;overflow:hidden;">
            <nobr>
                    ${governanceView.governanceData.name}
            </nobr>
        </div>
        <c:set var="governanceData" value="${governanceView.governanceData}"/>
        <div id="governanceData_${row.index}">
            <input name="id" type="hidden" value="${governanceData.id}"/>
            <input name="zkId" type="hidden" value="${governanceData.zkId}"/>
            <input name="name" type="hidden" value="${governanceData.name}"/>
            <input name="group" type="hidden" value="${governanceData.group}"/>
            <input name="serviceName" type="hidden"
                   value="${governanceData.serviceName}"/>
            <input name="serviceGroup" type="hidden"
                   value="${governanceData.serviceGroup}"/>
            <input name="version" type="hidden" value="${governanceData.version}"/>
            <input name="ip" type="hidden" value="${governanceData.ip}"/>
            <input name="port" type="hidden" value="${governanceData.port}"/>
            <input name="urlStr" type="hidden" value="${governanceData.urlStr}"/>
            <input name="url" type="hidden" value="${governanceData.url}"/>
            <input name="status" type="hidden"
                   value="${governanceData.status}"/>
            <input name="pathType" type="hidden"
                   value="${governanceData.pathType}"/>
            <input name="dataType" type="hidden"
                   value="${governanceData.dataType}"/>
            <input name="lastOperator" type="hidden"
                   value="${governanceData.lastOperator}"/>
            <input name="createTime" type="hidden"
                   value="${governanceData.createTime.time}"/>
            <input name="lastUpdateTime" type="hidden"
                   value="${governanceData.lastUpdateTime.time}"/>
        </div>
    </td>

    <td>
        <div class="over_c" style="width: 100%;overflow:hidden;">
            <nobr>
                <c:if test="${pathType==CONFIG}">
                    <a href="<%=request.getContextPath()%>/configurator/showDetail?id=${governanceView.governanceData.id}">${governanceView.hostnameAndPort}</a>

                </c:if>
                <c:if test="${pathType==ROUTER}">
                    <a href="<%=request.getContextPath()%>/router/showDetail?id=${governanceView.governanceData.id}">${governanceView.hostnameAndPort}</a>
                </c:if>
            </nobr>
        </div>
    </td>
    <td>
        <div class="over_c" style="width: 100%;overflow:hidden;">
            <nobr>
                <c:if test="${pathType==CONFIG}">
                    ${governanceView.params}
                </c:if>
                <c:if test="${pathType==ROUTER}">
                    ${governanceView.rule}
                </c:if>
            </nobr>
        </div>
    </td>
    <td>

        <div class="btn-group btn-group-sm">
            <c:if test="${governanceView.governanceData.status.code == ONLINE}">
                <button type="button" class="btn btn-success btn-sm dropdown-toggle"
                        data-toggle="dropdown">
                    启用
                    <span class="caret"></span>
                </button>
            </c:if>
            <c:if test="${governanceView.governanceData.status.code == OFFLINE}">
                <button type="button" class="btn btn-danger btn-sm dropdown-toggle"
                        data-toggle="dropdown">
                    禁用
                    <span class="caret"></span>
                </button>
            </c:if>
            <ul class="dropdown-menu ulMenu" role="menu">
                <c:if test="${governanceView.governanceData.status.code == ONLINE}">
                    <c:if test="${pathType == CONFIG}">
                        <li>
                            <a id="btn_disabled_${row.index}"
                               onclick="changeEnabledState('btn_disabled_${row.index}','<%=request.getContextPath()%>/configurator/changeEnabledState/${OFFLINE}?group=${governanceData.group}')">
                                禁用
                            </a>
                        </li>
                    </c:if>
                    <c:if test="${pathType == ROUTER}">
                        <li>
                            <a id="btn_disabled_${row.index}"
                               onclick="changeEnabledState('btn_disabled_${row.index}','<%=request.getContextPath()%>/router/changeEnabledState/${OFFLINE}?group=${governanceData.group}')">
                                禁用
                            </a>
                        </li>
                    </c:if>
                    <c:if test="${pathType != CONFIG and pathType != ROUTER }">
                        <li>
                            <a id="btn_disabled_${row.index}"
                               onclick="changeEnabledState('btn_disabled_${row.index}','<%=request.getContextPath()%>/configurator/changeEnabledState/${OFFLINE}?group=${governanceData.group}')">
                                禁用
                            </a>
                        </li>
                    </c:if>
                </c:if>
                <c:if test="${governanceView.governanceData.status.code == OFFLINE}">
                    <c:if test="${pathType == CONFIG}">
                        <li>
                            <a id="btn_enabled_${row.index}"
                               onclick="changeEnabledState('btn_disabled_${row.index}','<%=request.getContextPath()%>/configurator/changeEnabledState/${ONLINE}?group=${governanceData.group}')">
                                启用
                            </a>
                        </li>
                    </c:if>
                    <c:if test="${pathType == ROUTER}">
                        <li>
                            <a id="btn_enabled_${row.index}"
                               onclick="changeEnabledState('btn_disabled_${row.index}','<%=request.getContextPath()%>/router/changeEnabledState/${ONLINE}?group=${governanceData.group}')">
                                启用
                            </a>
                        </li>
                    </c:if>
                    <c:if test="${pathType != CONFIG and pathType != ROUTER}">
                        <li>
                            <a id="btn_enabled_${row.index}"
                               onclick="changeEnabledState('btn_disabled_${row.index}','<%=request.getContextPath()%>/configurator/changeEnabledState/${ONLINE}?group=${governanceData.group}')">
                                启用
                            </a>
                        </li>
                    </c:if>
                </c:if>
            </ul>
        </div>

        <c:if test="${governanceView.governanceData.status.code == DELETE}">
            <b style="color: red">删除</b>
        </c:if>
    </td>

    <td>
        <div class="over_c" style="width: 100%;overflow:hidden;">
            <nobr>
                <fmt:formatDate value="${governanceView.governanceData.lastUpdateTime}"
                                pattern="yyyyMMdd HH:mm:ss"
                                type="both" timeStyle="long" dateStyle="long"/>
            </nobr>
        </div>
    </td>
    <td>

        <c:if test="${governanceData.dataType.code != ZK_DATA}">
            <c:if test="${governanceData.status.code == OFFLINE}">
                <a class="btn btn-primary btn-sm"
                   href="<%=request.getContextPath()%>/distribute/distributeToUpdate?id=${governanceData.id}">修改</a>

                <c:if test="${pathType == CONFIG}">
                    <button id="btn_delete_${row.index}" class="btn btn-default btn-sm"
                            data-loading-text="正在删除..."
                            onclick="deleteUrl('btn_delete_${row.index}','<%=request.getContextPath()%>/configurator/delete?group=${governanceData.group}')">
                        删除
                    </button>
                </c:if>
                <c:if test="${pathType == ROUTER}">

                    <br/>
                    <button id="btn_delete_${row.index}" class="btn btn-default btn-sm"
                            data-loading-text="正在删除..."
                            onclick="deleteUrl('btn_delete_${row.index}','<%=request.getContextPath()%>/router/delete?group=${governanceData.group}',
                                    '${governanceView.governanceData.urlStr}','${group}','',
                                    '${governanceView.governanceData.serviceName}','','0.0.0.0')">删除
                    </button>
                    <br/>
                    <a href="<%=request.getContextPath()%>/router/previewWithUrl?group=${governanceView.governanceData.group}&serviceKey=${governanceView.governanceData.url.serviceKey}&encodeUrl=${governanceView.encodeUrl}&zkId=${governanceView.governanceData.zkId}"
                       class="btn btn-info btn-sm"
                       target="_blank">预览</a>
                    <br/>
                </c:if>
                <c:if test="${pathType != CONFIG and pathType != ROUTER}">
                    <button id="btn_delete_${row.index}" class="btn btn-default btn-sm"
                            data-loading-text="正在删除..."
                            onclick="deleteUrl('btn_delete_${row.index}','<%=request.getContextPath()%>/configurator/delete?group=${governanceData.group}')">
                        删除
                    </button>
                </c:if>
            </c:if>
            <c:if test="${governanceData.status.code == ONLINE}">
                <c:if test="${pathType == ROUTER}">
                    <a href="<%=request.getContextPath()%>/router/preview?group=${governanceView.governanceData.group}&serviceKey=${governanceView.governanceData.url.serviceKey}&zkId=${governanceView.governanceData.zkId}"
                       class="btn btn-info btn-sm"
                       target="_blank">预览</a>
                </c:if>
            </c:if>
        </c:if>
    </td>
</tr>
<tr style="border-left: 2px solid white;border-right: 2px solid white;">
    <td colspan="6">

    </td>
</tr>
</c:forEach>
</table>
</div>
</div>
</div>
</div>
</div>

</div>
</div>
<script src="<%=request.getContextPath()%>/resource/js/bootstrap-datetimepicker.min.js"></script>
<script src="<%=request.getContextPath()%>/resource/js/bootstrap-datetimepicker.zh-CN.js"></script>
<script>
    $(document).ready(function () {
        replace_overflow();
        showHint('group', function () {
            return '/search/showGroupHint'
        }, function (value) {
            return {group: value}
        });
        showHint('serviceName', function () {
            return '/search/showServiceHint'
        }, function (value) {
            return {service: value}
        });
        showHint('machine', function () {
            return '/search/showNoPortMachineHint'
        }, function (value) {
            return {machine: value}
        });
    });

</script>
</body>
</html>