<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html>
<head>
    <title>显示路由规则</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/table.css">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/configAddParam-14-12-24.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/operateMachine-14-12-24.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/showTable-2-18.js"></script>
    <style type="text/css">
        .appField {
            margin-left: 3%;
            width: 97%;
        }

        .methodField {
            margin-left: 3%;
            width: 97%;
        }

            /*.itemTable tbody:last-child {*/
            /*border-bottom: 2px solid white;*/
            /*}*/
    </style>
    <script type="text/javascript">
        function bindEnter(){
            var s=event.keyCode;
            if (s==13){
                showByCondition();
            }
        }
    </script>
</head>
<body onkeydown="bindEnter()">

<div class="container">
    <%@ include file="../include/header.jsp" %>
    <%@ include file="../include/constantUtil.jsp" %>
    <div class="row">
        <%@ include file="../include/sidebar.jsp" %>
        <div class="col-md-10" style="min-height: 500px">
            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">显示路由规则</h3>
                </div>
                <div class="panel-body">
                    <div style="min-height: 450px">
                        <fieldset>
                            <legend>路由规则：</legend>
                            <c:if test="${errorInfo != null}">
                                <p class="wrapTd" style="color: red">
                                    您输入的参数有误，无法进行路由预览，原因如下：<br/>
                                        ${errorInfo}
                                </p>
                            </c:if>
                            <p class="wrapTd" style="color: blue">
                                ${newRouter.rule}
                            </p>
                            <c:forEach items="${routerList}" var="router" varStatus="row">
                                <p class="wrapTd">
                                        ${router.rule}<br/>
                                </p>
                            </c:forEach>
                        </fieldset>
                        <hr/>
                        <form>
                            <table style="width: 100%;text-align: center;">
                                <tr>
                                    <td>
                                        application：
                                        <input id="app" type="text" placeholder="请输入application"/>
                                    </td>
                                    <td>
                                        ip：
                                        <input id="ip" type="text" placeholder="请输入ip"/>
                                    </td>
                                    <td>
                                        method：
                                        <input id="method" type="text" placeholder="请输入method"/>
                                        &nbsp;
                                        &nbsp;
                                        <input type="button" class="btn btn-default btn-sm" onclick="showByCondition()"
                                               value="查询"/>
                                    </td>
                                </tr>
                            </table>
                        </form>
                        <hr/>
                        <div class="row">
                            <div class="col-lg-6">
                                消费者
                            </div>
                            <div class="col-lg-1"></div>
                            <div class="col-lg-5">
                                提供者
                            </div>
                        </div>
                        <br/>
                        <table class="itemTable" style="width: 100%">
                            <c:forEach items="${invokeRelationList}" var="invokeRelation" varStatus="itemRow">
                                <tr id="item_${itemRow.index}">
                                    <td style="width: 50%;">
                                        <table id="consumer_${itemRow.index}" class="table table-bordered">
                                            <c:forEach items="${invokeRelation.consumerSignList}" var="consumerSign"
                                                       varStatus="consumerRow">
                                                <tr id="ip_tr_${itemRow.index}-${consumerRow.index}"
                                                    data="${itemRow.index}">
                                                    <td class="titleBg wrapTd">
                                                        ip(machine)：<span
                                                            id="ip_${itemRow.index}-${consumerRow.index}">${consumerSign.consumer.address}</span>
                                                        (${consumerSign.consumer.hostName})
                                                    </td>
                                                </tr>
                                                <tr id="prop_tr_${itemRow.index}-${consumerRow.index}">
                                                    <td>
                                                        <div class="appField wrapTd">
                                                            application：<span
                                                                id="app_${itemRow.index}-${consumerRow.index}">${consumerSign.consumer.application}</span>
                                                        </div>
                                                        <div class="methodField wrapTd">
                                                            method：<span
                                                                id="method_${itemRow.index}-${consumerRow.index}"> ${consumerSign.methodsStr}</span>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </td>
                                    <td class="text-align-center">
                                        =>
                                    </td>
                                    <td style="width:41.66666666666667%;">
                                        <div class="list-group">
                                            <c:if test="${invokeRelation.providerList== null || fn:length(invokeRelation.providerList) == 0}">
                                                <p class="list-group-item wrapTd">
                                                    <span style="color: red">无服务提供者</span>
                                                </p>
                                            </c:if>
                                            <c:forEach items="${invokeRelation.providerList}" var="provider">
                                                <p class="list-group-item wrapTd">
                                                    <span>${provider.address}(${provider.hostNameAndPort})</span>
                                                </p>
                                            </c:forEach>
                                        </div>
                                    </td>
                                </tr>
                                <tr id="item_space_${itemRow.index}">
                                    <td style="border-left: 2px solid white;border-right: 2px solid white;">

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