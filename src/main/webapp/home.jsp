<%@ page import="java.net.URLEncoder" %>
<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Cactus</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Cactus">
    <meta name="author" content="Feythin.lau">

    <!-- The styles -->
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <style type="text/css">
        body {
            padding-bottom: 40px;
        }

        .sidebar-nav {
            padding: 9px 0;
        }

        .showcenter {
            margin-top: 200px;
        }

    </style>
    <link href="<%=request.getContextPath()%>/resource/css/charisma-app.css" rel="stylesheet">
    <!-- The fav icon -->
    <link rel="shortcut icon" href="resource/img/favicon.ico">
</head>
<script
        src="https://qsso.corp.qunar.com/lib/qsso-auth.js?t=<?=rand()?>">
</script>

<body bgcolor="#FFFFFF">
<script
        src="https://qsso.corp.qunar.com/lib/qsso-auth.js?t=<?=rand()?>">
</script>
<div align="center" style="margin-top:8%">
    <table style="text-align: center;">
        <tr>
            <td colspan="2" height="100px">
                <img src="<%=request.getContextPath()%>/resource/img/logo.png" border="0" alt="" />
            </td>
        </tr>
        <tr>
            <td height="40px"
                style="font-size: 23px; text-align: center; font-weight: bolder; letter-spacing: 2px;" >Cactus</td>
        </tr>
        <tr>
            <td style="font-size: 15px; text-align: center;">
                <button id="qsso-login" class="btn btn-lg btn-primary">QSSO登录</button>
            </td>
        </tr>
    </table>
</div>
<script>
    <%
        String back = request.getParameter("back");
        if (back == null){
    %>
    QSSO.attach('qsso-login', '<%=request.getContextPath()%>/login');
    <%
        }else{
    %>
    QSSO.attach('qsso-login', '<%=request.getContextPath()%>/login',{'back':'<%=back%>'});
    <%
    }
    %>
</script>
</body>

</html>