<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>显示消费者</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/configAddParam-14-12-24.js"></script>
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
                    <h3 class="panel-title">显示消费者</h3>
                </div>
                <ul class="nav nav-tabs">
                    <li>
                        <a href="<%=request.getContextPath()%>/provider/showProviders?group=${group}&ipAndPort=${ipAndPort}&serviceKey=${serviceKey}&zkId=${zkid}">提供者</a>
                    </li>
                    <li class="active"><a href="#">消费者</a></li>
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
                        <div style="color: red"><b> 屏蔽操作：</b></div>屏蔽操作是指屏蔽掉该consumer对provider的调用，如果调用接口有返回值，将返回null
                        <br/>
                        <br/>
                        <div style="color: green"><b> 容错操作：</b></div>容错操作是指该consumer对provider的调用将被容错，当出现超时、没有provider等情况时，如果调用接口有返回值，将返回null；但provider方直接抛出的异常不会被容错
                        <br/>
                        <br/>
                        <table class="table table-striped">
                            <tr>
                                <th class="td-65-percent ver-align-middle">消费者</th>
                                <th class="td-10-percent ver-align-middle">
                                    <div class="btn-group btn-group-sm">
                                        <button id="stateText" type="button"
                                                class="btn btn-default btn-sm dropdown-toggle"
                                                data-toggle="dropdown">
                                            状态
                                            <span class="caret"></span>
                                        </button>
                                        <ul class="dropdown-menu ulMenu" role="menu">
                                            <li>
                                                <a id="btn_doubleWeight_${row.index}"
                                                   onclick="showByState('fail:return+null',this)">
                                                    容错
                                                </a>
                                            </li>
                                            <li>
                                                <a id="btn_halveWeight_${row.index}" type="button"
                                                   onclick="showByState('force:return+null',this)">
                                                    屏蔽
                                                </a>
                                            </li>
                                            <li>
                                                <a id="btn_halveWeight_${row.index}" type="button"
                                                   onclick="showByState('',this)">
                                                    正常
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
                                <th class="td-25-percent ver-align-middle">操作</th>
                            </tr>
                            <c:set var="MOCK_KEY_ON_CONFIG_URL" value="newMockState"/>
                            <c:forEach items="${list}" var="consumer" varStatus="row">
                                <tr state="${consumer.params[MOCK_KEY_ON_CONFIG_URL]}">
                                    <td class="wrapTd td-65-percent">
                                        <a href="<%=request.getContextPath()%>/consumer/showDetail?group=${group}&encodeUrl=${consumer.encodeUrl}">${consumer.hostName}</a>
                                    </td>
                                    <td class="wrapTd td-10-percent">
                                        <c:if test="${consumer.mock!=''}">
                                            <c:if test="${consumer.mock=='true' or consumer.mock=='return+null'}">
                                                <b style="color: green">默认容错,无法进行设置</b>
                                            </c:if>
                                        </c:if>
                                        <c:if test="${consumer.mock==''}">
                                            <c:if test="${consumer.params[MOCK_KEY_ON_CONFIG_URL]==''}">
                                                <b style="color: blue">正常</b>
                                            </c:if>
                                            <c:if test="${consumer.params[MOCK_KEY_ON_CONFIG_URL]=='force:return+null'}">
                                                <b style="color: red">已屏蔽</b>
                                            </c:if>
                                            <c:if test="${consumer.params[MOCK_KEY_ON_CONFIG_URL]=='fail:return+null'}">
                                                <b style="color: green">已容错</b>
                                            </c:if>
                                        </c:if>
                                    </td>
                                    <td class="wrapTd td-25-percent">
                                        <c:set var="SHIELD" value="force:return+null"/>
                                        <c:set var="FAILOVER" value="fail:return+null"/>
                                        <c:set var="NONEMOCK" value="nonemock"/>
                                        <c:if test="${consumer.mock==''}">
                                            <button id="btn_shield_${row.index}" class="btn btn-danger btn-sm"
                                                    data-loading-text="正在屏蔽..."
                                                    onclick="updateConfigWithServiceKey('btn_shield_${row.index}','<%=request.getContextPath()%>/configurator/setMock','${group}','${consumer.serviceKey}','${consumer.address}','${consumer.application}','${SHIELD}','${zkid}')">
                                                屏蔽
                                            </button>
                                            <button id="btn_failover_${row.index}" class="btn btn-success btn-sm"
                                                    data-loading-text="正在容错..."
                                                    onclick="updateConfigWithServiceKey('btn_failover_${row.index}','<%=request.getContextPath()%>/configurator/setMock','${group}','${consumer.serviceKey}','${consumer.address}','${consumer.application}','${FAILOVER}','${zkid}')">
                                                容错
                                            </button>
                                            <button id="btn_recover_${row.index}" class="btn btn-primary btn-sm"
                                                    data-loading-text="正在恢复..."
                                                    onclick="updateConfigWithServiceKey('btn_recover_${row.index}','<%=request.getContextPath()%>/configurator/setMock','${group}','${consumer.serviceKey}','${consumer.address}','${consumer.application}','${NONEMOCK}','${zkid}')">
                                                恢复
                                            </button>
                                        </c:if>
                                        <c:if test="${consumer.mock!=''}">
                                            <button id="btn_shield_${row.index}" class="btn btn-danger btn-sm disabled"
                                                    data-loading-text="正在屏蔽...">
                                                屏蔽
                                            </button>
                                            <button id="btn_failover_${row.index}"
                                                    class="btn btn-success btn-sm disabled"
                                                    data-loading-text="正在容错...">
                                                容错
                                            </button>
                                            <button id="btn_recover_${row.index}"
                                                    class="btn btn-primary btn-sm disabled"
                                                    data-loading-text="正在恢复...">
                                                恢复
                                            </button>
                                        </c:if>
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