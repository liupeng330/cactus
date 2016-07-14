<%@ page import="com.qunar.corp.cactus.util.ConstantHelper" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="<%=request.getContextPath()%>/resource/js/bootstrap.autocomplete.js"></script>
<script src="<%=request.getContextPath()%>/resource/js/showHint.js"></script>
<nav class="navbar navbar-inverse" role="navigation">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="<%=request.getContextPath()%>/">Cactus</a>
    </div>
    <script type="text/javascript">
        function logout(logoutUrl) {
            $.ajax({
                url: 'https://qsso.corp.qunar.com/api/logout.php',//发送请求地址
                type: "get",
                dataType: 'jsonp',
                data: { //发送的数据串

                },
                success: function (ret) {
                    if (ret) {
                        window.location = logoutUrl;
                    }
                }
            });
        }
    </script>
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">

        <form class="navbar-form navbar-right" action="<%=request.getContextPath()%>/search/searchInfo" method="post"
              role="search">
            <div class="form-group">
                <input id="header-search-input" type="text" name="data" class="form-control" autocomplete="off"
                       placeholder="Search" value="${data}">
            </div>
            <div class="form-group">
                <select id="header-search-type" class="form-control" name="searchType">
                    <c:if test="${searchType == 0}">
                        <option value="0" selected="selected">服务</option>
                        <option value="1">应用</option>
                        <option value="2">机器</option>
                    </c:if>
                    <c:if test="${searchType == 1}">
                        <option value="0">服务</option>
                        <option value="1" selected="selected">应用</option>
                        <option value="2">机器</option>
                    </c:if>
                    <c:if test="${searchType == 2}">
                        <option value="0">服务</option>
                        <option value="1">应用</option>
                        <option value="2" selected="selected">机器</option>
                    </c:if>
                    <c:if test="${searchType != 0 and searchType != 1 and searchType!=2}">
                        <option value="0" selected="selected">服务</option>
                        <option value="1">应用</option>
                        <option value="2">机器</option>
                    </c:if>
                </select>
            </div>
            <button type="submit" class="btn btn-default">搜索</button>
        </form>
        <button class="btn btn-default navbar-btn navbar-right"
                onclick="logout('<%=request.getContextPath()%>/logout')">注销
        </button>

        <p class="navbar-text navbar-right">欢迎您：${username}&nbsp;&nbsp;</p>
    </div>
    <script type="text/javascript">
        showHint('header-search-input', function () {
            var type = $('#header-search-type').val();
            var searchPathMap = {
                0: "/search/showServiceHint",
                1: "/search/showGroupHint",
                2: "/search/showMachineHint"
            };
            console.log("path is" + searchPathMap[type].toString());
            return searchPathMap[type].toString();
        }, function (paramValue) {
            var type = $('#header-search-type').val();
            var typeMap = {
                0: "service",
                1: "group",
                2: "machine"
            };
            var param = '{"' + typeMap[type] + '":"' + paramValue + '"}';
            return $.parseJSON(param);
        });

    </script>
</nav>