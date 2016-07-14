<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="<%=request.getContextPath()%>/resource/js/charisma-1-24.js"></script>
<style type="text/css">
    .nav-pills>li>a {
        border-radius: 0px
    }

    .nav-pills>li.active>a, .nav-pills>li.active>a:hover, .nav-pills>li.active>a:focus {
        color: #ffffff;
        background-color: #9b9b9b
    }

    a span {
        padding-right: 10px;
    }

    li a {
        color: #000000;
    }
</style>
<div class="col-md-2">
    <div class="panel panel-default">
        <ul class="nav nav-pills nav-stacked main-menu" style="min-height: 500px;background-color: #ffffff">
            <li>
                <a href="<%=request.getContextPath()%>/"><span class="glyphicon glyphicon-home"></span> 首页 </a>

                <div class="nav nav-divider"></div>
            </li>
            <li>
                <a href="<%=request.getContextPath()%>/group/showGroup?showByOwner=true"><span
                        class="glyphicon glyphicon-user"></span>我的应用</a>

                <div class="nav nav-divider"></div>
            </li>
            <li>
                <a href="<%=request.getContextPath()%>/group/showGroup">
                    <span class="glyphicon glyphicon-tag"></span>所有应用
                </a>

                <div class="nav nav-divider"></div>
            </li>
            <li>
                <a href="<%=request.getContextPath()%>/group/showGroup?showMachine=true">
                    <span class="glyphicon glyphicon-tags"></span>所有机器
                </a>

                <div class="nav nav-divider"></div>
            </li>
            <li>
                <a href="<%=request.getContextPath()%>/special/showSpecials">
                    <span class="glyphicon glyphicon-tags"></span>特殊Group
                </a>

                <div class="nav nav-divider"></div>
            </li>
            <li>
                <a href="<%=request.getContextPath()%>/search/searchConfigOrRouter?pathType=4">
                    <span class="glyphicon glyphicon-wrench"></span>动态配置查询
                </a>

                <div class="nav nav-divider"></div>
            </li>
            <li>
                <a href="<%=request.getContextPath()%>/search/searchConfigOrRouter?pathType=3">
                    <span class="glyphicon glyphicon-book"></span>路由规则查询
                </a>

                <div class="nav nav-divider"></div>
            </li>
            <li>
                <a href="<%=request.getContextPath()%>/log/showByName">
                    <span class="glyphicon glyphicon-eye-open"></span>我的操作日志
                </a>

                <div class="nav nav-divider"></div>
            </li>
            <li>
                <a href="<%=request.getContextPath()%>/log/showByConditionRender">
                    <span class="glyphicon glyphicon-filter"></span>操作日志查询
                </a>

                <div class="nav nav-divider"></div>
            </li>
            <li>
                <a href="<%=request.getContextPath()%>/zkcluster/showPage">
                    <span class="glyphicon glyphicon-cloud"></span>机房地址
                </a>

                <div class="nav nav-divider"></div>
            </li>
        </ul>
    </div>
</div>