<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>提供者详细信息</title>
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
        <div class="col-md-10" style="min-height: 500px">
            <div class="panel panel-default" style="min-height: 500px">
                <div class="panel-heading">
                    <h3 class="panel-title">提供者详细信息</h3>
                </div>

                <div class="panel-body">
                    <div style="min-height: 450px">
                        <c:if test="${provider==null}">
                            该provider已被注销！
                        </c:if>
                        <c:if test="${provider!=null}">
                            <c:set var="DISABLED_KEY" value="disabled" />
                            <table class="table table-striped">
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        服务名
                                    </td>
                                    <td class="wrapTd">
                                            ${provider.intefaze}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        服务地址
                                    </td>
                                    <td class="wrapTd">
                                        <c:out value=" ${provider.url}" escapeXml="true"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        机器名
                                    </td>
                                    <td class="wrapTd">
                                        ${provider.hostNameAndPort}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        机器ip
                                    </td>
                                    <td class="wrapTd">
                                            ${provider.address}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        方法列表
                                    </td>
                                    <td class="wrapTd">
                                            ${provider.methods}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        状态
                                    </td>
                                    <td class="wrapTd">
                                        <c:if test="${provider.enabled==false}">
                                            <c:if test="${provider.params[DISABLED_KEY]=='false'}">
                                                <b style="color: green;">在线</b>
                                            </c:if>
                                            <c:if test="${provider.params[DISABLED_KEY]=='true'}">
                                                <b style="color: red">下线</b>
                                            </c:if>
                                        </c:if>
                                        <c:if test="${provider.enabled==true}">
                                            下线
                                        </c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        权重
                                    </td>
                                    <td class="wrapTd">
                                            ${provider.weight}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        应用
                                    </td>
                                    <td class="wrapTd">
                                            ${provider.application}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        Dubbo版本
                                    </td>
                                    <td class="wrapTd">
                                            ${provider.dubboVersion}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        进程号
                                    </td>
                                    <td class="wrapTd">
                                            ${provider.pid}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        是否动态
                                    </td>
                                    <td class="wrapTd">
                                            ${provider.dynamic}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        impl
                                    </td>
                                    <td class="wrapTd">
                                            ${provider.serviceGroup}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="wrapTd title-15percent-td">
                                        version
                                    </td>
                                    <td class="wrapTd">
                                            ${provider.version}
                                    </td>
                                </tr>
                            </table>
                        </c:if>
                    </div>

                </div>
            </div>
        </div>

    </div>

</div>
</body>
</html>