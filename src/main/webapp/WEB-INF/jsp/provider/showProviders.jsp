<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>显示提供者</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/modalService-8-20.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/configAddParam-14-12-24.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/operateMachine-14-12-24.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/showTable-2-18.js"></script>
</head>
<body>

<div class="container">
    <%@ include file="../include/header.jsp" %>
    <div class="row">
        <%@ include file="../include/sidebar.jsp" %>
        <div class="col-md-10" style="min-height: 500px">
            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">显示提供者</h3>
                </div>
                <ul class="nav nav-tabs">
                    <li class="active"><a href="#">提供者</a></li>
                    <li>
                        <a href="<%=request.getContextPath()%>/consumer/showConsumers?group=${group}&ipAndPort=${ipAndPort}&serviceKey=${serviceKey}&zkId=${zkid}">消费者</a>
                    </li>
                </ul>
                <div class="panel-body">
                    <div style="min-height: 450px">
                        <%@ include file="../include/modal.jsp" %>
                        <b> 应用:</b>&nbsp;${group}
                        <br/>
                        <br/>
                        <b> 服务:</b>&nbsp;${serviceKey}
                        <br/>
                        <br/>
                        <button class="btn btn-default btn-sm"
                                onclick="loadAndRelocate('<%=request.getContextPath()%>','${group}', '${serviceKey}', '${zkid}')">
                            批量添加动态配置
                        </button>
                        <br/><br/>
                        <table class="table table-striped">
                            <tr>
                                <th class="ver-align-middle">
                                    <input id="checkAll" type="checkbox" onclick="selectAll()"/>
                                </th>
                                <th class="td-45-percent ver-align-middle">
                                    提供者
                                </th>
                                <th class="td-20-percent ver-align-middle">
                                    权重
                                </th>
                                <th class="td-20-percent ver-align-middle">

                                    <div class="btn-group btn-group-sm">
                                        <button id="stateText" type="button" class="btn btn-default btn-sm dropdown-toggle"
                                                data-toggle="dropdown">
                                            状态
                                            <span class="caret"></span>
                                        </button>
                                        <ul class="dropdown-menu ulMenu" role="menu">
                                            <li>
                                                <a id="btn_doubleWeight_${row.index}"
                                                   onclick="showByState('false',this)">
                                                    在线
                                                </a>
                                            </li>
                                            <li>
                                                <a id="btn_halveWeight_${row.index}" type="button"
                                                   onclick="showByState('true',this)">
                                                    下线
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
                                <th class="td-15-percent ver-align-middle">
                                    操作
                                </th>
                            </tr>
                            <c:set var="DISABLED_KEY" value="disabled"/>
                            <c:set var="WEIGHT_KEY" value="weight"/>
                            <c:forEach items="${list}" var="provider" varStatus="row">
                                <tr state = "${provider.params[DISABLED_KEY]}">
                                    <td><input id="id_${provider.address}" data="${provider.address}" type="checkbox"
                                               onclick="changeSelectedAllBtnState('id_${provider.address}')"/></td>
                                    <td class="wrapTd td-45-percent itemContent">
                                        <a href="<%=request.getContextPath()%>/provider/showDetailByUrl?group=${group}&url=${provider.encodeUrl}">${provider.hostNameAndPort}</a>
                                        <br/>
                                    </td>
                                    <td class="wrapTd td-20-percent itemContent">
                                        <c:set var="DOUBLE_WEIGHT" value="1"/>
                                        <c:set var="HALVE_WEIGHT" value="0"/>
                                        <div class="btn-group btn-group-sm">
                                            <button type="button" class="btn btn-primary btn-sm dropdown-toggle"
                                                    data-toggle="dropdown">
                                                    ${provider.weight}
                                                <span class="caret"></span>
                                            </button>
                                            <ul class="dropdown-menu ulMenu" role="menu">
                                                <li>
                                                    <a id="btn_doubleWeight_${row.index}"
                                                       onclick="updateWeight('btn_doubleWeight_${row.index}','<%=request.getContextPath()%>/configurator/doubleOrHalveWeight','${group}', '${provider.url}', '${DOUBLE_WEIGHT}')">
                                                        倍权
                                                    </a>
                                                </li>
                                                <li>
                                                    <a id="btn_halveWeight_${row.index}" type="button"
                                                       onclick="updateWeight('btn_halveWeight_${row.index}','<%=request.getContextPath()%>/configurator/doubleOrHalveWeight','${group}', '${provider.url}', '${HALVE_WEIGHT}')">
                                                        半权
                                                    </a>
                                                </li>
                                            </ul>
                                        </div>
                                    </td>
                                    <td class="wrapTd td-20-percent itemContent">
                                        <c:set var="ONLINE_OPERATE" value="0"/>
                                        <c:set var="OFFLINE_OPERATE" value="1"/>
                                        <div class="btn-group btn-group-sm">
                                            <c:if test="${provider.params[DISABLED_KEY]=='false'}">
                                                <button type="button" class="btn btn-success btn-sm dropdown-toggle"
                                                        data-toggle="dropdown">
                                                    在线
                                                    <span class="caret"></span>
                                                </button>
                                            </c:if>
                                            <c:if test="${provider.params[DISABLED_KEY]=='true'}">
                                                <button type="button" class="btn btn-danger btn-sm dropdown-toggle"
                                                        data-toggle="dropdown">
                                                    下线
                                                    <span class="caret"></span>
                                                </button>
                                            </c:if>
                                            <ul class="dropdown-menu ulMenu" role="menu">
                                                <li>
                                                    <a id="btn_enabled_${row.index}"
                                                       onclick="changeOnlineState('btn_offline_${row.index}','<%=request.getContextPath()%>/configurator/providerOnlineOrOffline','${group}','${provider.url}','${ONLINE_OPERATE}')">
                                                        上线
                                                    </a>
                                                </li>
                                                <li>
                                                    <a id="btn_disabled_${row.index}"
                                                       onclick="changeOnlineState('btn_online_${row.index}','<%=request.getContextPath()%>/configurator/providerOnlineOrOffline','${group}','${provider.url}','${OFFLINE_OPERATE}')">
                                                        下线
                                                    </a>
                                                </li>

                                            </ul>
                                        </div>
                                    </td>

                                    <td class="wrapTd td-15-percent itemContent">

                                        <a class="btn btn-default btn-sm"
                                           href="<%=request.getContextPath()%>/configurator/showConfigurator?group=${group}&serviceGroup=${provider.serviceGroup}&version=${provider.version}&service=${provider.intefaze}&address=${provider.address}&zkId=${zkid}">动态配置</a>
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
</body>
</html>