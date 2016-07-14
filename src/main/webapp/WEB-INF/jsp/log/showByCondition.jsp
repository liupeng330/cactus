<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>操作日志查询</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/datetimepicker.css" rel="stylesheet">
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
                    <h3 class="panel-title">操作日志查询</h3>
                </div>
                <div class="panel-body" style="text-align: center">
                    <input id="rootPath" type="hidden" value="<%=request.getContextPath()%>"/>

                    <div style="padding-top: 30px">
                        <form action="<%=request.getContextPath()%>/log/showByCondition" method="post">
                            <table style="border-color: #ffffff;width: 100%">
                                <tr>
                                    <td style="text-align: right;">
                                        应用：
                                        <input id="group" style="width:150px" type="text"
                                               name="group"
                                               value="${group}"
                                               autocomplete="off"
                                               placeholder="请输入要查询的应用"/>
                                    </td>
                                    <td style="text-align: right;">
                                        服务：
                                    </td>
                                    <td style="text-align: left;">
                                        <input id="service" style="width:150px" type="text"
                                               name="service"
                                               value="${service}"
                                               autocomplete="off"
                                               placeholder="请输入要查询的服务名"/>

                                    </td>
                                </tr>
                                <tr>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                </tr>
                                <tr>
                                    <td style="text-align: right;">
                                        操作起始时间：<input name="fromTime" type="text" id="date01" value="${fromTime}"
                                                      style="width: 150px;"/>
                                    </td>
                                    <td style="text-align: right;">
                                        操作结束时间：
                                    </td>
                                    <td style="text-align: left;">
                                        <input name="toTime" type="text" id="date02" value="${toTime}"
                                               style="width: 150px;"/>
                                    </td>
                                </tr>
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
                </div>
                <div>
                    <br/>
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
                <c:if test="${list!=null}">
                    <ul class="pagination pull-right">
                        <c:if test="${list.pageNumBeginIndex==1}">
                            <li class="disabled"><a href="#"> << </a></li>
                        </c:if>
                        <c:if test="${list.pageNumBeginIndex!=1}">
                            <li>
                                <a href="<%=request.getContextPath()%>/log/showByCondition?group=${group}&service=${service}&fromTime=${fromTime}&toTime=${toTime}&pageNum=${list.pageNumBeginIndex-1}">
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
                                    <a href="<%=request.getContextPath()%>/log/showByCondition?group=${group}&service=${service}&fromTime=${fromTime}&toTime=${toTime}&pageNum=${index}">${index}</a>
                                </li>
                            </c:if>
                        </c:forEach>
                        <c:if test="${list.pageNumEndIndex==list.totalPageNum}">
                            <li class="disabled"><a href="#"> >> </a></li>
                        </c:if>
                        <c:if test="${list.pageNumEndIndex!=list.totalPageNum}">
                            <li>
                                <a href="<%=request.getContextPath()%>/log/showByCondition?group=${group}&service=${service}&fromTime=${fromTime}&toTime=${toTime}&pageNum=${list.pageNumEndIndex+1}">
                                    >> </a>
                            </li>
                        </c:if>
                    </ul>
                </c:if>
            </div>
        </div>
    </div>

</div>
</div>
<script src="<%=request.getContextPath()%>/resource/js/bootstrap-datetimepicker.min.js"></script>
<script src="<%=request.getContextPath()%>/resource/js/bootstrap-datetimepicker.zh-CN.js"></script>
<script>
    $(document).ready(function () {

        $('#date01').datetimepicker({
            language: 'zh-CN',
            format: 'yyyy-mm-dd hh:ii:ss',
            todayBtn: true,
            autoclose: true,
            todayHighlight: true,
            pickTime: true,
            hourStep: 1,
            minuteStep: 15,
            secondStep: 30,
            inputMask: true,
            pickDate: true
        }).on('changeDate', function () {
                    $(this).blur();
                });
        $('#date02').datetimepicker({
            language: 'zh-CN',
            format: 'yyyy-mm-dd hh:ii:ss',
            todayBtn: true,
            autoclose: true,
            todayHighlight: true,
            pickTime: true,
            hourStep: 1,
            minuteStep: 15,
            secondStep: 30,
            inputMask: true,
            pickDate: true
        }).on('changeDate', function () {
                    $(this).blur();
                });

        showHint('group', function () {
            return '/search/showGroupHint'
        }, function (value) {
            return {group: value}
        });
        showHint('service', function () {
            return '/search/showServiceHint'
        }, function (value) {
            return {service: value}
        });
    });
</script>
</body>
</html>