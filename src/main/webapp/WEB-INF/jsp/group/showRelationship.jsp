<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>应用依赖关系图</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link type="text/css" href="<%=request.getContextPath()%>/resource/css/base.css" rel="stylesheet"/>
    <link type="text/css" href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet"/>
    <link type="text/css" href="<%=request.getContextPath()%>/resource/css/Spacetree.css" rel="stylesheet"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>

    <!--[if IE]>
    <script language="javascript" type="text/javascript"
            src="<%=request.getContextPath()%>/resource/js/Extras/excanvas.js"></script><![endif]-->

    <!-- JIT Library File -->
    <script language="javascript" type="text/javascript"
            src="<%=request.getContextPath()%>/resource/js/jit.js"></script>

    <script language="javascript" type="text/javascript"
            src="<%=request.getContextPath()%>/resource/js/tree-14-11-25.js"></script>
</head>
<body>

<div class="container">
    <%@ include file="../include/header.jsp" %>
    <div class="row">
        <%@ include file="../include/sidebar.jsp" %>
        <div class="col-md-10">
            <div class="panel panel-default" style="min-height: 10px">
                <div class="panel-heading">
                    <h3 class="panel-title">应用依赖关系图</h3>
                </div>
                <div class="panel-body">
                    <div style="min-height: 10px">
                        <%--<c:forEach items="${groupRelationship.relyOn}" var="relyOn" varStatus="row">--%>
                            <%--<input id="o_${row.index}" value="${relyOn.key}" type="hidden"/>--%>
                            <%--<input id="app_o_${row.index}" value="${relyOn.value}" type="hidden"/>--%>
                        <%--</c:forEach>--%>
                        <%--<c:forEach items="${groupRelationship.relyMe.right}" var="relyMe" varStatus="row">--%>
                            <%--<input id="m_${row.index}" value="${relyMe.key}" type="hidden"/>--%>
                            <%--<input id="app_m_${row.index}" value="${relyMe.value}" type="hidden"/>--%>
                        <%--</c:forEach>--%>
                        <div id="id-list"></div>
                        <div id="relyOn"><h3 style="color: #66CC33">我的依赖(group依赖):</h3></div>
                        <div id="relyMeApp"><h3 style="color: #4084ff">依赖我的(application依赖):</h3></div>
                    </div>


                    <div id="log"></div>


                </div>
            </div>
        </div>

    </div>
    <script>
        $(document).ready(
                showRelationTree('relyOn', 'relyOnRoot', '${groupRelationship.group}', 'left','relyOnGroup','${groupRelationship.relyOn}', '#6C3', null),
                showRelationTree('relyMeApp', 'relyMeAppRoot', '${groupRelationship.group}', 'right','relyMeApp','${groupRelationship.relyMe.left}','#4084ff', queryAppConsumers)
        );
    </script>
</div>
</body>
</html>