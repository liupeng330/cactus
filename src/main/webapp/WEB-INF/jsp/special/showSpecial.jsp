<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>显示所有特殊Group</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/resource/css/table.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/modalService-8-20.js"></script>
</head>
<body>

<div class="container">
    <%@ include file="../include/header.jsp" %>
    <div class="row">
        <%@ include file="../include/sidebar.jsp" %>
        <%@ include file="../include/modal.jsp" %>
        <div class="col-md-10">
            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">特殊Group集群</h3>
                </div>
                <div class="panel-body">
                    <div style="min-height: 450px">
                        <div class="alert alert-info"
                             style="background-color:#d9edf7;background-image:-webkit-gradient(linear,left 0,left 100%,from(#d9edf7),to(#d9edf7))"
                             role="alert">
                            <button type="button" class="close" data-dismiss="alert"><span
                                    aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                            特殊的group是指，在dubbo配置中将group属性配置为多级目录，如：<br/>
                            <strong> &lt;dubbo:registry id="zk1" address="zookeeper://127.0.0.1:2181"
                                group="special/test"/&gt; </strong><br/>
                            当您的group配置与上述配置类似时，则需要在此处添加特殊Group，输入的group须为第一个"/"前的内容，如上述的"special"。<br/>
                            添加之后，以special/开头的group会被自动搜索到。<br/>
                            ps: 添加及删除操作都需要几分钟才能生效
                        </div>
                        <form class="form-inline">
                            <div class="form-group">
                                <input class="form-control" style="width:300px" type="text" id="path"
                                       placeholder="输入group集群前缀"/>
                            </div>
                            <button type="button" id="addSpecial" class="btn btn-default">添加</button>
                        </form>
                        <br/>

                        <table class="table table-striped">
                            <tr>
                                <th>GROUP集群</th>
                                <th>操作</th>
                            </tr>
                            <c:forEach items="${specialList}" var="groupItem">
                                <tr>
                                    <td class="wrapTd td-80-percent">
                                        ${groupItem}
                                    </td>
                                    <td class="wrapTd td-20-percent">
                                        <a class="btn btn-danger btn-sm" name="deleteSpecial"
                                           data="${groupItem}">删除</a>
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
<script>
    $("#addSpecial").click(function () {
        showModalAndBind("确定添加？（处理节点关系需要一定时间，添加成功后1分钟可搜索到相应的服务）", function () {
            $.post("/special/add", {path: $("#path").val()}, function (result) {
                console.log(JSON.stringify(result));
                if (result.status == 0) {
                    alert(result.data);
                } else if (result.message != undefined) {
                    alert(result.message);
                } else if (result.indexOf("没有权限")) {
                    alert("您没有权限执行此操作");
                }
                location.reload();
            });
        });
    });
    $("[name='deleteSpecial']").click(function () {
        var cluster = $(this).attr("data");
        showModalAndBind("确定删除？", function () {
            $.post("/special/delete", {cluster: cluster}, function (result) {
                console.log(JSON.stringify(result));
                if (result.status == 0) {
                    alert(result.data);
                } else if (result.message != undefined) {
                    alert(result.message);
                } else if (result.indexOf("没有权限")) {
                    alert("您没有权限执行此操作");
                }
                location.reload();
            });
        });
    });
</script>
</html>