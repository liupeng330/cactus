<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>显示用户日志</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
</head>
<body>

<div class="container">
    <%@ include file="../include/header.jsp" %>
    <div class="row">
        <%@ include file="../include/sidebar.jsp" %>
        <div class="col-md-10">
            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">显示用户日志</h3>
                </div>
                <div class="panel-body">
                    <div style="min-height: 450px">
                        <table class="table table-striped">
                            <tr>
                                <th>用户</th>
                                <th>应用</th>
                                <th>服务</th>
                                <th>机器</th>
                                <th>日志</th>
                                <th>时间</th>
                            </tr>
                            <c:forEach items="${list.datas}" var="log">
                                <tr>
                                    <td class="wrapTd usernameTd">
                                            ${log.user.username}
                                    </td>
                                    <td class="wrapTd groupTd">
                                        ${log.group}
                                    </td>
                                    <td class="wrapTd logservicetd">
                                        ${log.service}
                                    </td>
                                    <td class="wrapTd hostnameTd">
                                        ${log.hostName}
                                    </td>
                                    <td class="wrapTd logmsgTd">
                                        <c:out value="${log.message}"/>
                                    </td>
                                    <td class="wrapTd dateTd">
                                        <fmt:formatDate value="${log.operateTime}" pattern="yyyy.MM.dd HH:mm:ss"
                                                        type="both" timeStyle="long" dateStyle="long"/>

                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                    <ul class="pagination pull-right">
                        <c:if test="${list.pageNumBeginIndex==1}">
                            <li class="disabled"><a href="#"> << </a></li>
                        </c:if>
                        <c:if test="${list.pageNumBeginIndex!=1}">
                            <li>
                                <a href="<%=request.getContextPath()%>/log/showByName?pageNum=${list.pageNumBeginIndex-1}">
                                    << </a></li>
                        </c:if>
                        <c:forEach var="index" begin="${list.pageNumBeginIndex}" step="1"
                                   end="${list.pageNumEndIndex}">

                            <c:if test="${index == list.currentPageNum}">
                                <li class="active">
                                    <a href="#">${index}</a>
                                </li>
                            </c:if>
                            <c:if test="${index != list.currentPageNum}">
                                <li>
                                    <a href="<%=request.getContextPath()%>/log/showByName?pageNum=${index}">${index}</a>
                                </li>
                            </c:if>
                        </c:forEach>
                        <c:if test="${list.pageNumEndIndex==list.totalPageNum}">
                            <li class="disabled"><a href="#"> >> </a></li>
                        </c:if>
                        <c:if test="${list.pageNumEndIndex!=list.totalPageNum}">
                            <li>
                                <a href="<%=request.getContextPath()%>/log/showByName?pageNum=${list.pageNumEndIndex+1}">
                                    >> </a>
                            </li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>

    </div>

</div>
</body>
</html>