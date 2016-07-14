<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>添加机器</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/jquery.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resource/js/addZkcluster-8-20.js"></script>
</head>
<body>

<div class="container">
    <%@ include file="../include/header.jsp" %>
    <div class="row">
        <%@ include file="../include/sidebar.jsp" %>
        <div class="col-md-10">

            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">添加机器</h3>
                </div>
                <div class="panel-body" style="text-align: center">
                    <input id="rootPath" type="hidden" value="<%=request.getContextPath()%>"/>
                    <div style="padding-top: 130px">
                        <div class="row">
                            <div class="col-md-2"></div>
                            <div class="form-inline">
                                <div class="col-md-8">
                                    机房名：<input id="name" class="form-control" style="width:300px" type="text"
                                               name="data"
                                               placeholder="请输入要添加的机房名字"/>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">&nbsp;</div>
                        </div>
                        <div class="row">
                            <div class="col-md-2"></div>
                            <div class="col-md-8">
                                <div class="form-inline">
                                    地&nbsp;&nbsp;址： <input id="address" class="form-control" style="width:300px" type="text"
                                               name="data"
                                               placeholder="请输入要添加的集群地址（域名或ip）列表"/>
                                </div>
                            </div>

                        </div>

                        <div class="row">
                            <div class="col-md-12">&nbsp;</div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                </div>
                            <div class="col-md-4">
                                <button id="add" type="button" data-loading-text="正在添加，这个过程将持续几分钟...." class="btn btn-default right" onclick="add()">添加</button>
                            </div>
                        </div>

                    </div>
                    <div>
                        <div class="row">
                            <div class="col-md-12" style="text-align: left;">已添加的机房</div>
                        </div>


                        <table class="table">
                            <c:forEach items="${list}" var="cluster">
                                <tr>
                                    <td>
                                            ${cluster.name}
                                    </td>
                                    <td>
                                            ${cluster.address}
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