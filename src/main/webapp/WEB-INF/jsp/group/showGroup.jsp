<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>显示所有应用</title>
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
                    <h3 class="panel-title">显示应用</h3>
                </div>
                <div class="panel-body">
                    <div style="min-height: 450px">
                        <form class="form-inline" action="<%=request.getContextPath()%>/group/showGroup" method="get">
                            <div class="form-group">
                                <input type="hidden" name="showMachine" value="${showMachine}"/>
                                <input type="hidden" name="showByOwner" value="${showByOwner}"/>
                                <input type="hidden" name="shouldSearch" value="true"/>
                                <input id="searchGroup" class="form-control" style="width:300px" type="text"
                                       name="group"
                                       autocomplete="off" placeholder="请输入要搜索的应用" value="${group}"/>
                            </div>
                            <button type="submit" class="btn btn-default">搜索</button>
                        </form>
                        <br/>
                        <table class="table table-striped">
                            <tr>
                                <th>应用</th>
                                <th>操作</th>
                            </tr>
                            <c:forEach items="${list.datas}" var="groupItem">
                                <tr>
                                    <td class="wrapTd td-80-percent">
                                        <a href="<%=request.getContextPath()%>/distribute/distributeFromGroup?group=${groupItem}&showMachine=${showMachine}&machine=${machine}">${groupItem}</a>
                                    </td>
                                    <td class="wrapTd td-20-percent">
                                        <a class="btn btn-info btn-sm"
                                           href="<%=request.getContextPath()%>/group/showRelationship?group=${groupItem}">显示依赖关系</a>
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
                                <a href="<%=request.getContextPath()%>/group/showGroup?group=${group}&showMachine=${showMachine}&showByOwner=${showByOwner}&shouldSearch=${shouldSearch}&pageNum=${list.pageNumBeginIndex-1}">
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
                                    <a href="<%=request.getContextPath()%>/group/showGroup?group=${group}&showMachine=${showMachine}&showByOwner=${showByOwner}&shouldSearch=${shouldSearch}&pageNum=${index}">${index}</a>
                                </li>
                            </c:if>
                        </c:forEach>
                        <c:if test="${list.pageNumEndIndex==list.totalPageNum}">
                            <li class="disabled"><a href="#"> >> </a></li>
                        </c:if>
                        <c:if test="${list.pageNumEndIndex!=list.totalPageNum}">
                            <li>
                                <a href="<%=request.getContextPath()%>/group/showGroup?group=${group}&showMachine=${showMachine}&showByOwner=${showByOwner}&shouldSearch=${shouldSearch}&pageNum=${list.pageNumEndIndex+1}">
                                    >> </a>
                            </li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>

    </div>
    <script type="text/javascript">

        showHint('searchGroup', function () {
            return '/search/showGroupHint'
        }, function (value) {
            return {group: $('#searchGroup').val()}
        });
    </script>
</div>
</body>
</html>