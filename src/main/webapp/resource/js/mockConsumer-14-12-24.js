/**
 * Created by zhenyu.nie on 2014/9/15.
 */

var resultStateFailCss = {color:"red"}

function submitInvoke(submitUrl) {
    var shouldSubmit = true;

    $('#result').val('');
    $('#callTime').html("");

    if ($('#ipAndPort').val().trim() == '') {
        shouldSubmit = false;
        alert('provider不能为空');
    } else if ($('#method').val().trim() == '') {
        shouldSubmit = false;
        alert('method不能为空');
    }

    if (shouldSubmit) {
        var  needPerformanceTest=$('#needPerformanceTest').val();
        if(needPerformanceTest==0)
        doInvoke(submitUrl);
        else
        doInvokePerformance(submitUrl);
    } else {
        $('#resultState').css(resultStateFailCss)
        $('#resultState').html("fail");
    }
}

function addZeroIfLessThanTen(text) {
    if (text < 10) {
        text = "0" + text;
    }
    return text;
}

function doInvoke(submitUrl) {
    $.ajax({
        url: submitUrl,//发送请求地址
        type: "post",
        data: { //发送的数据串
            group: $('#group').val(),
            ipAndPort: $('#ipAndPort').val(),
            service: $('#service').val(),
            method: $('#method').val(),
            serviceGroup: $('#serviceGroup').val(),
            version: $('#version').val(),
            zkId:$('#zkId').val(),
            parameter:$('#parameter').val(),
            needPerformanceTest:0,
            requestThreads:0,
            totalQps:0,
            requestPerThreds:0,
            totalTime:0,
            qpsPerThreads:0,
            pefTestData:"",
            dataSelectPolicy:0,

            timeOut:0},

        success: function (back) {
            if (back.status == 0) {
                $('#resultState').css({color:"#00FF00"})
                $('#resultState').html("success");
                $('#result').val(JSON.stringify(back.data.result));
                $('#callTime').html(back.data.elapsedTime + "ms");
            } else {
                $('#resultState').css(resultStateFailCss)
                $('#resultState').html("fail");
                $('#result').val(back.message);
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            dealFailResult(XMLHttpRequest);
        }
    });


}
function doInvokePerformance(submitUrl) {
    $.ajax({
        url: submitUrl,//发送请求地址
        type: "post",
        data: { //发送的数据串
            group: $('#group').val(),
            ipAndPort: $('#ipAndPort').val(),
            service: $('#service').val(),
            method: $('#method').val(),
            serviceGroup: $('#serviceGroup').val(),
            version: $('#version').val(),
            zkId:$('#zkId').val(),
            parameter:$('#parameter').val(),
            needPerformanceTest:1,
        requestThreads:$('#requestThreads').val(),
        totalQps:$('#totalQps').val(),
        requestPerThreds:$('#requestPerThreds').val(),
        totalTime:$('#totalTime').val(),
        qpsPerThreads:$('#qpsPerThreads').val(),
        timeOut:$('#timeOut').val(),
        pefTestData:$('#pefTestData').val(),
        dataSelectPolicy:$('#dataSelectPolicy').val()},
        success: function(back) {
            if (back.status == 0) {
                $('#resultState').css({color:"#00FF00"})
                $('#resultState').html("success");
                $('#result').val(JSON.stringify(back.data.result));
                $('#callTime').html(back.data.elapsedTime + "ms");
                alert("性能测试已经在远程开始，结果请稍后查收邮件");
            } else {
                $('#resultState').css(resultStateFailCss)
                $('#resultState').html("fail");
                $('#result').val(back.message);
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            dealFailResult(XMLHttpRequest);
        }
    });


}


