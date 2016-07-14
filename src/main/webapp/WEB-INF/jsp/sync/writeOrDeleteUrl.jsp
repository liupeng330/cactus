<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resource/css/bootstrap.min.css">
    <link href="<%=request.getContextPath()%>/resource/css/bootstrap-theme.min.css" rel="stylesheet">
    <style type="text/css">
        .urlInput {
            width: 100%;
        }
    </style>
</head>
<body>
<div class="container">
    <form action="<%=request.getContextPath()%>/sync/writeOrDeleteUrlToZk" method="post">
        <br/>
        <br/>

        <div class="row">
            <div class="col-lg-2">
                group:
            </div>
            <div class="col-lg-10">
                <input class="urlInput" name="group" type="text"/>
            </div>
        </div>
        <br/>
        <br/>

        <div class="row">
            <div class="col-lg-2">
                serviceName:
            </div>
            <div class="col-lg-10">
                <input class="urlInput" name="serviceName" type="text"/>
            </div>
        </div>
        <br/>
        <br/>
        <div class="row">
            <div class="col-lg-2">
                类型
            </div>
            <div class="col-lg-10">
                <select name="operationType">
                    <option value="0">写URL</option>
                    <option value="1">删URL</option>
                </select>
            </div>
        </div>

        <br/>
        <br/>

        <div class="row">
            <div class="col-lg-2">
                zkId
            </div>
            <div class="col-lg-10">
                <input name="zkId" type="text"/>
            </div>
        </div>

        <br/>
        <br/>

        <p style="color: red">下面两项任填一项，不能同时填</p>

        <div class="row">
            <div class="col-lg-2">
                encode url:
            </div>
            <div class="col-lg-10">
                <input class="urlInput" name="encodeUrlStr" type="text"/>
            </div>
        </div>
        <br/>
        <br/>

        <div class="row">
            <div class="col-lg-2">
                decode url:
            </div>
            <div class="col-lg-10">
                <input class="urlInput" name="decodeUrlStr" type="text"/>
            </div>

        </div>
        <br/>
        <br/>
        <input type="submit" value="提交"/>
    </form>
</div>
</body>
</html>