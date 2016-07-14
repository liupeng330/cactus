<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>显示所有机器名</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/operateMachine-14-12-24.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/showTable-2-18.js"></script>
</head>
<body>

<div class="container">
    <%@ include file="../include/header.jsp" %>
    <div class="row">
        <%@ include file="../include/sidebar.jsp" %>
        <%@ include file="../include/constantUtil.jsp" %>
        <div class="col-md-10" style="min-height: 500px">
            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">显示所有机器名</h3>
                </div>
                <div class="panel-body">
                    <div style="min-height: 450px">
                        <%@ include file="../include/modal.jsp" %>
                        <button id="btn_batchOnline" class="btn btn-success btn-sm" onclick="batchOnlineOrOffline('btn_batchOnline','${group}','${ONLINE}')">上线选中机器</button>
                        <button id="btn_batchOffline" class="btn btn-danger btn-sm" onclick="batchOnlineOrOffline('btn_batchOffline','${group}','${OFFLINE}')">下线选中机器</button>
                        <br/>
                        <br/>
                        <input id="rootPath" type="hidden" value="<%=request.getContextPath()%>"/>
                        <table class="table table-striped">
                            <tr>
                                <th class="td-10-percent ver-align-middle">
                                    <input id="checkAll" type="checkbox" onclick="selectAll()" />全选
                                </th>
                                <th class="td-60-percent ver-align-middle">
                                    机器名
                                </th>
                                <th class="td-15-percent ver-align-middle">
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
                                               onclick="showByState('2',this)">
                                                所有在线
                                            </a>
                                        </li>
                                        <li>
                                            <a id="btn_halveWeight_${row.index}" type="button"
                                               onclick="showByState('1',this)">
                                                所有下线
                                            </a>
                                        </li>
                                        <li>
                                            <a id="btn_halveWeight_${row.index}" type="button"
                                               onclick="showByState('0',this)">
                                                部分下线
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
                            <c:forEach items="${list}" var="hostnameAndParam" varStatus="row">
                                <tr state="${hostnameAndParam.right}">
                                    <td class="wrapTd td-10-percent">
                                        <input id="id_${hostnameAndParam.left.hostNameAndPort}" data="${hostnameAndParam.left.address}" type="checkbox" onclick="changeSelectedAllBtnState('id_${hostnameAndParam.left.hostNameAndPort}')"/>
                                    </td>
                                    <td class="wrapTd td-60-percent">
                                        <a href="<%=request.getContextPath()%>/serviceSign/showServiceSign?group=${group}&ipAndPort=${hostnameAndParam.left.address}">${hostnameAndParam.left.hostNameAndPort}</a>
                                    </td>
                                    <td class="wrapTd td-15-percent">
                                       <c:if test="${hostnameAndParam.right==0}">
                                            <b style="color: blue">部分服务下线</b>
                                       </c:if>
                                        <c:if test="${hostnameAndParam.right==1}">
                                            <b style="color: red">所有服务下线</b>
                                        </c:if>
                                        <c:if test="${hostnameAndParam.right==2}">
                                            <b style="color: green">所有服务在线</b>
                                        </c:if>
                                    </td>
                                    <td class="wrapTd td-15-percent">
                                        <button id="btn_online_${row.index}" class="btn btn-success btn-sm" data-loading-text="正在上线..." onclick="onlineOrOffline('btn_online_${row.index}','${group}','${hostnameAndParam.left.address}','${ONLINE}')">上线</button>
                                        <button id="btn_offline_${row.index}" class="btn btn-danger btn-sm"  data-loading-text="正在下线..." onclick="onlineOrOffline('btn_offline_${row.index}','${group}','${hostnameAndParam.left.address}','${OFFLINE}')">下线</button>
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