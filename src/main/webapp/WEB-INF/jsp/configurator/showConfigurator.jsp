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
    <title>显示configurators</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/configAddParam-14-12-24.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/operateMachine-14-12-24.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/showTable-2-18.js"></script>
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
        <div class="col-md-10" style="min-height: 500px">
            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">显示动态配置</h3>
                </div>

                <div class="panel-body">
                    <div style="min-height: 450px">
                        <%@ include file="../include/modal.jsp" %>
                        <table class="table table-responsive">
                            <tr>
                                <th class="wrapTd td-10-percent text-align-center ver-align-middle">
                                    配置名
                                </th>
                                <th class="wrapTd td-20-percent text-align-center ver-align-middle">
                                    机器名
                                </th>
                                <th class="wrapTd td-25-percent text-align-center ver-align-middle">
                                    参数
                                </th>
                                <th class="wrapTd td-10-percent text-align-center ver-align-middle">
                                    <div class="btn-group btn-group-sm">
                                        <button id="stateText" type="button" class="btn btn-default btn-sm dropdown-toggle"
                                                data-toggle="dropdown">
                                            状态
                                            <span class="caret"></span>
                                        </button>
                                        <ul class="dropdown-menu ulMenu" role="menu">
                                            <li>
                                                <a id="btn_doubleWeight_${row.index}"
                                                   onclick="showByState('${ONLINE}',this)">
                                                    启用
                                                </a>
                                            </li>
                                            <li>
                                                <a id="btn_halveWeight_${row.index}" type="button"
                                                   onclick="showByState('${OFFLINE}',this)">
                                                    禁用
                                                </a>
                                            </li>
                                            <li>
                                                <a id="btn_halveWeight_${row.index}" type="button"
                                                   onclick="showByState('all',this)">
                                                    全部
                                                </a>
                                            </li>
                                        </ul>
                                    </div>
                                </th>
                                <th class="wrapTd td-20-percent text-align-center ver-align-middle">
                                    操作时间
                                </th>
                                <th class="wrapTd td-15-percent text-align-center ver-align-middle">
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
                            <c:forEach items="${userDataList}" var="config" varStatus="rowNum">
                                <tr class="titleBg" state="${config.governanceData.status.code}">
                                    <td colspan="6" class="wrapTd">
                                        <c:set var="governanceData" value="${config.governanceData}"/>
                                        <div id="governanceData_${rowNum.index}">
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
                                        <p style="text-align: left">
                                        <span class="groupTitleField">
                                            应用： ${config.group}
                                        </span>
                                        <span class="serviceTitleField">
                                            <span class="over-c" style="overflow: hidden">
                                            服务：
                                            <c:if test="${config.governanceData.serviceGroup!='' and config.governanceData.serviceGroup!=null}">
                                                ${config.governanceData.serviceGroup}/
                                            </c:if>
                                                ${config.governanceData.serviceName}
                                            <c:if test="${config.governanceData.version!='' and config.governanceData.version!=null}">
                                                :${config.governanceData.version}
                                            </c:if>
                                            </span>
                                        </span>
                                        <span class="lastOperatorField">
                                            最后操作： ${config.governanceData.lastOperator}
                                        </span>
                                        </p>
                                    </td>
                                </tr>
                                <tr state="${config.governanceData.status.code}">
                                    <td class="text-align-center ver-align-middle">
                                        <div class="over_c" style="width: 100%;overflow:hidden;">
                                            <nobr>
                                                    ${config.governanceData.name}
                                            </nobr>
                                        </div>
                                    </td>
                                    <td class="text-align-center ver-align-middle">
                                        <div class="over_c" style="width: 100%;overflow:hidden;">
                                            <nobr>
                                                <a href="<%=request.getContextPath()%>/configurator/showDetail?id=${config.governanceData.id}"> ${config.hostnameAndPort}</a>
                                            </nobr>
                                        </div>
                                    </td>
                                    <td class="text-align-center ver-align-middle">
                                        <div class="over_c" style="width: 100%;overflow:hidden;">
                                            <nobr>
                                                    ${config.params}
                                            </nobr>
                                        </div>
                                    </td>

                                    <td class="text-align-center ver-align-middle">
                                        <div class="btn-group btn-group-sm">
                                            <c:if test="${config.governanceData.status.code == ONLINE}">
                                                <button type="button" class="btn btn-success btn-sm dropdown-toggle"
                                                        data-toggle="dropdown">
                                                    启用
                                                    <span class="caret"></span>
                                                </button>
                                            </c:if>
                                            <c:if test="${config.governanceData.status.code == OFFLINE}">
                                                <button type="button" class="btn btn-danger btn-sm dropdown-toggle"
                                                        data-toggle="dropdown">
                                                    禁用
                                                    <span class="caret"></span>
                                                </button>
                                            </c:if>
                                            <ul class="dropdown-menu ulMenu" role="menu">
                                                <c:if test="${config.governanceData.status.code == ONLINE}">
                                                    <li>
                                                        <a id="btn_disabled_${row.index}"
                                                           onclick="changeEnabledState('btn_disabled_${rowNum.index}','<%=request.getContextPath()%>/configurator/changeEnabledState/${OFFLINE}?group=${governanceData.group}')">
                                                            禁用
                                                        </a>
                                                    </li>
                                                </c:if>
                                                <c:if test="${config.governanceData.status.code == OFFLINE}">
                                                    <li>
                                                        <a id="btn_enabled_${row.index}"
                                                           onclick="changeEnabledState('btn_disabled_${rowNum.index}','<%=request.getContextPath()%>/configurator/changeEnabledState/${ONLINE}?group=${governanceData.group}')">
                                                            启用
                                                        </a>
                                                    </li>
                                                </c:if>
                                            </ul>
                                        </div>
                                    </td>

                                    <td class="text-align-center ver-align-middle">
                                        <div class="over_c" style="width: 100%;overflow:hidden;">
                                            <fmt:formatDate value="${config.governanceData.lastUpdateTime}"
                                                            pattern="yyyyMMdd HH:mm:ss"
                                                            type="both" timeStyle="long" dateStyle="long"/>
                                        </div>
                                    </td>
                                    <td class="text-align-center ver-align-middle">

                                        <c:if test="${governanceData.dataType.code != ZK_DATA}">

                                            <c:if test="${governanceData.status.code == OFFLINE}">
                                                <a class="btn btn-primary btn-sm"
                                                   href="<%=request.getContextPath()%>/distribute/distributeToUpdate?id=${governanceData.id}">修改</a>

                                                <button id="btn_delete_${rowNum.index}" class="btn btn-default btn-sm"
                                                        data-loading-text="正在删除..."
                                                        onclick="deleteUrl('btn_delete_${rowNum.index}','<%=request.getContextPath()%>/configurator/delete?group=${governanceData.group}')">
                                                    删除
                                                </button>
                                            </c:if>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr state="${config.governanceData.status.code}" style="border-left: 2px solid white;border-right: 2px solid white;">
                                    <td colspan="6">

                                    </td>
                                </tr>
                            </c:forEach>
                        </table>

                        <a class="btn btn-success"
                           href="<%=request.getContextPath()%>/configurator/addPageRender?group=${group}&serviceGroup=${serviceGroup}&service=${service}&version=${version}&address=${address}&zkId=${zkid}">新增</a>
                        <br/>
                    </div>

                </div>
            </div>
        </div>

    </div>

</div>
<script type="text/javascript">
    replace_overflow();
</script>
</body>
</html>