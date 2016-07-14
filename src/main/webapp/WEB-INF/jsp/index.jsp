<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>首页</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <style type="text/css">
        .num {
            font-size: 20px;
            color: red;
            font-weight: bold;
        }
    </style>
</head>
<body>

<div class="container">
    <%@ include file="include/header.jsp" %>
    <div class="row">
        <%@ include file="include/sidebar.jsp" %>
        <div class="col-md-10">

            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">首页</h3>
                </div>
                <div class="panel-body" style="text-align: center">
                    <div style="padding-top: 130px">
                        <p style="color: red;font-size: x-small">
                            <%--注意：不支持通配符查询，只有owner才有操作权限，其他人为读权限--%>
                        </p>

                        <form class="form-inline" role="form" action="<%=request.getContextPath()%>/search/searchInfo"
                              method="post">
                            <div class="form-group">
                                <input id="index-search-input" class="form-control" style="width:300px" type="text"
                                       name="data"
                                       autocomplete="off" placeholder="请输入要搜索的内容"/>
                            </div>
                            <div class="form-group">
                                <select id="index-search-type" class="form-control" name="searchType">
                                    <option value="0" selected="selected">服务</option>
                                    <option value="1">应用</option>
                                    <option value="2">机器</option>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-default">搜索</button>
                        </form>

                        <div style="font-size: 15px;margin-top: 20px;">

                            当前共有应用：
                            <span class="num">${statCount.allAppCount}</span>
                            &nbsp; 个，服务：
                            <span class="num">
                                ${statCount.allServiceCount}
                            </span>

                            &nbsp; 个，provider：

                                 <span class="num">
                                     ${statCount.allProvider}
                                 </span>

                            &nbsp; 个，consumer：

                            <span class="num">
                                ${statCount.allConsumer}
                            </span>

                            &nbsp; 个

                        </div>
                    </div>

                </div>
            </div>
        </div>

    </div>
</div>
<script type="text/javascript">

    showHint('index-search-input', function () {
        var type = $('#index-search-type').val();
        var searchPathMap = {
            0: "/search/showServiceHint",
            1: "/search/showGroupHint",
            2: "/search/showMachineHint"
        };
        console.log("path is" + searchPathMap[type].toString());
        return searchPathMap[type].toString();
    }, function (paramValue) {
        var type = $('#index-search-type').val();
        var typeMap = {
            0: "service",
            1: "group",
            2: "machine"
        };
        var param = '{"' + typeMap[type] + '":"' + paramValue + '"}';
        return $.parseJSON(param);
    });

</script>
</body>
</html>