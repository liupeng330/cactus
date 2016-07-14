<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>您传递的参数有误</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
    <div class="jumbotron" style="background:#CC9900;color: #ffffff ">
        <h1>您传递的参数有误，无法访问该页面！</h1>

    </div>
    <dt>您可以：</dt>
    <dd><a href="javascript:history.go(-1);" class="back">返回至刚才的页面</a>
</div>

</body>

</html>