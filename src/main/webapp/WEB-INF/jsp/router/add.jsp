<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>添加路由规则</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/configAddParam-14-12-24.js"></script>
    <style type="text/css">
        .addressInput {
            width: 300px;
        }
        .width285{
            width: 285px;
        }
    </style>

</head>
<body style="margin: 0px">

<div class="container">
<%@ include file="../include/header.jsp" %>
<%@ include file="../include/constantUtil.jsp" %>
<div class="row">
<%@ include file="../include/sidebar.jsp" %>
<div class="col-md-10" style="min-height: 500px">
<div class="panel panel-default" style="min-height: 500px">
<div class="panel-heading">
    <h3 class="panel-title">添加路由规则</h3>
</div>
<div class="panel-body">
<div style="min-height: 450px">
<%@ include file="../include/modal.jsp" %>
<input id="rootPath" type="hidden" value="<%=request.getContextPath()%>"/>
<input id="group" type="hidden" value="${serviceSign.group}"/>
<input id="id" type="hidden" value="${id}"/>

<div class="row">
    <div class="col-md-2">
        路由名称
    </div>
    <div class="col-md-10">
        <c:if test="${id!=EMPTY_ID}">
            <input id="routerName" name="name" type="text" value="${params['name']}" disabled="disabled"/>
        </c:if>
        <c:if test="${id==EMPTY_ID}">
            <input id="routerName" name="name" type="text" value="${params['name']}"/>
        </c:if>
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
        服务名
    </div>
    <div class="col-md-10">
        ${serviceSign.serviceInterface}
        <input id="serviceKey" name="serviceKey" type="hidden" value="${serviceSign.serviceKey}"/>
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
        ${serviceSign.serviceGroup}
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
        ${serviceSign.version}
    </div>
</div>

<input id="priority" name="priority" type="hidden" value="0"/>
<input id="zkId" name="zkId" type="hidden" value="${zkid}"/>
<%--<div class="row">--%>
<%--<div class="col-md-12">--%>
<%--&nbsp;--%>
<%--</div>--%>
<%--</div>--%>
<%--<div class="row">--%>
<%--<div class="col-md-2">--%>
<%--优先级--%>
<%--</div>--%>
<%--<div class="col-md-10">--%>
<%--<input id="priority" name="priority" type="text" value="0"/>--%>
<%--</div>--%>
<%--</div>--%>
<hr/>
<div class="row">
    <div class="col-md-2">
        匹配条件
    </div>
    <div class="col-md-5">
        匹配
    </div>
    <div class="col-md-5">
        不匹配
    </div>
</div>
<div class="row">
    <div class="col-md-12">
        &nbsp;
    </div>
</div>
<div class="row">
    <div class="col-md-2">
        应用
    </div>
    <div class="col-md-5">
        <input id="matchApp" class="width285" name="matchApp" type="text" value="${params['consumerMatchApp']}"/>
        <input type="button" class="btn btn-success btn-sm" value="选择应用"
               onclick="selectParamsRender('matchApp', 'clsMatchApp', ',')"/>
        <c:forEach items="${applicationList}" var="application">
            <input type="hidden" class="clsMatchApp" value="${application}" data="${application}"/>
        </c:forEach>
    </div>
    <div class="col-md-5">
        <input id="mismatchApp" class="width285" name="mismatchApp" type="text" value="${params['consumerMismatchApp']}"/>
        <input type="button" class="btn btn-success btn-sm" value="选择应用"
               onclick="selectParamsRender('mismatchApp', 'clsMismatchApp', ',')"/>
        <c:forEach items="${applicationList}" var="application">
            <input type="hidden" class="clsMismatchApp" value="${application}" data="${application}"/>
        </c:forEach>
    </div>
</div>
<div class="row">
    <div class="col-md-12">
        &nbsp;
    </div>
</div>
<div class="row">
    <div class="col-md-2">
        消费者IP地址
    </div>
    <div class="col-md-5">
        <input id="consumerMatchIp" class="addressInput" name="consumerMatchIp" type="text"
               value="${params['consumerMatchIp']}"/>
        <input type="button" class="btn btn-success btn-sm" value="选择IP"
               onclick="selectParamsRender('consumerMatchIp', 'clsConsumerMatchIp', ',')"/>
        <c:forEach items="${consumerList}" var="consumer">
            <input class="clsConsumerMatchIp" type="hidden" value="${consumer.left}"
                   data="${consumer.left}(${consumer.right})"/>
        </c:forEach>
    </div>
    <div class="col-md-5">
        <input id="consumerMismatchIp" class="addressInput" name="consumerMismatchIp" type="text"
               value="${params['consumerMismatchIp']}"/>
        <input type="button" class="btn btn-success btn-sm" value="选择IP"
               onclick="selectParamsRender('consumerMismatchIp', 'clsConsumerMismatchIp', ',')"/>
        <c:forEach items="${consumerList}" var="consumer">
            <input class="clsConsumerMismatchIp" type="hidden" value="${consumer.left}"
                   data="${consumer.left}(${consumer.right})"/>
        </c:forEach>
    </div>
</div>
<div class="row">
    <div class="col-md-12">
        &nbsp;
    </div>
</div>
<div class="row">
    <div class="col-md-2">
        方法名
    </div>
    <div class="col-md-5">
        <input id="matchMethods" class="width285" name="matchMethods" type="text" value="${params['consumerMatchMethod']}"/>
        <input type="button" class="btn btn-success btn-sm" value="选择方法"
               onclick="selectParamsRender('matchMethods', 'clsMatchMethods', ',')"/>
        <c:forEach items="${methodList}" var="method">
            <input type="hidden" class="clsMatchMethods" value="${method}" data="${method}"/>
        </c:forEach>
    </div>
    <div class="col-md-5">
        <input id="misMatchMethods" class="width285" name="misMatchMethods" type="text" value="${params['consumerMismatchMethod']}"/>
        <input type="button" class="btn btn-success btn-sm" value="选择方法"
               onclick="selectParamsRender('misMatchMethods', 'clsMisMatchMethods', ',')"/>
        <c:forEach items="${methodList}" var="method">
            <input type="hidden" class="clsMisMatchMethods" value="${method}" data="${method}"/>
        </c:forEach>
    </div>
</div>


<hr/>
<div class="row">
    <div class="col-md-2">
        过滤条件
    </div>
    <div class="col-md-5">
        匹配
    </div>
    <div class="col-md-5">
        不匹配
    </div>
</div>
<div class="row">
    <div class="col-md-12">
        &nbsp;
    </div>
</div>
<div class="row">
    <div class="col-md-2">
        IP
    </div>
    <div class="col-md-5">
        <input id="providerMatchIp" class="addressInput" name="providerMatchIp" type="text"
               value="${params['providerMatchIp']}"/>
        <input type="button" class="btn btn-success btn-sm" value="选择IP"
               onclick="selectParamsRender('providerMatchIp', 'clsProviderMatchIp', ',')"/>
        <c:forEach items="${ipList}" var="ipAndHost">
            <input type="hidden" class="clsProviderMatchIp" value="${ipAndHost.left}"
                   data="${ipAndHost.left}(${ipAndHost.right})"/>
        </c:forEach>
    </div>
    <div class="col-md-5">
        <input id="providerMismatchIp" class="addressInput" name="providerMatchIp" type="text"
               value="${params['providerMismatchIp']}"/>
        <input type="button" class="btn btn-success btn-sm" value="选择IP"
               onclick="selectParamsRender('providerMismatchIp', 'clsProviderMismatchIp', ',')"/>
        <c:forEach items="${ipList}" var="ipAndHost">
            <input type="hidden" class="clsProviderMismatchIp" value="${ipAndHost.left}"
                   data="${ipAndHost.left}(${ipAndHost.right})"/>
        </c:forEach>
    </div>
</div>
<div class="row">
    <div class="col-md-12">
        &nbsp;
    </div>
</div>
<div class="row">
    <div class="col-md-2">
        端口：
    </div>
    <div class="col-md-5">
        <input id="providerMatchPort" class="width285" name="providerMatchPort" type="text" value="${params['providerMatchPort']}"/>
        <input type="button" class="btn btn-success btn-sm" value="选择端口"
               onclick="selectParamsRender('providerMatchPort', 'clsProviderMatchPort', ',')"/>
        <c:forEach items="${portList}" var="port">
            <input type="hidden" class="clsProviderMatchPort" value="${port}" data="${port}"/>
        </c:forEach>
    </div>
    <div class="col-md-5">
        <input id="providerMismatchPort" class="width285" name="providerMatchPort" type="text"
               value="${params['providerMismatchPort']}"/>
        <input type="button" class="btn btn-success btn-sm" value="选择端口"
               onclick="selectParamsRender('providerMismatchPort', 'clsProviderMismatchPort', ',')"/>
        <c:forEach items="${portList}" var="port">
            <input type="hidden" class="clsProviderMismatchPort" value="${port}" data="${port}"/>
        </c:forEach>

    </div>
</div>
<div class="row">
    <div class="col-md-12">&nbsp;</div>
</div>
<div class="row">
    <div class="col-md-10"></div>
    <button id="btn_saveRouter" type="button" class="btn  btn-primary" data-loading-text="正在保存..."
            onclick="submitRoute('<%=request.getContextPath()%>/router/add')">
        保存
    </button>
    <button class="btn btn-info" onclick="redirectToPreview('<%=request.getContextPath()%>/router/previewWithParam')">
        路由预览
    </button>
</div>
</div>
</div>
</div>

</div>

</div>
</body>
</html>