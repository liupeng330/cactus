function reloadThisPage() {
    var redirectUrl = location.href;
    window.location = redirectUrl;
}

function dealBackMessageAndReload(back){
   dealBackMessageAndRedirect(back, function (){
      reloadThisPage();
   });
}

function resetYesButton(){
    $('#yes').html("确定");
    $('#yes').removeAttr('disabled');
}


function dealBackMessageAndRedirect(back,redirectFunction){
    console.log(JSON.stringify(back));
    if (back.status == 0) {
        clearAndHide();
        redirectFunction();
    } else {
        if (back.message == undefined) {
            $('#submitResult').html('您没有权限执行该操作！');
            resetYesButton();
            $('#yes').unbind('click').removeAttr('onclick').click(function () {
                clearAndHide();
            });
        } else {
            $('#submitResult').html(back.message);
            resetYesButton();
            $('#yes').unbind('click').removeAttr('onclick').click(function () {
                clearAndHide();
                redirectFunction();
            });
        }
    }
}

function dealFailResult(XMLHttpRequest){
    var responseText = XMLHttpRequest.responseText;
    var result = '';
    if (responseText.indexOf('没有权限访问') > 0) {
        result='您没有权限执行该操作！';
    } else {
        result='出现网络错误或异常，请与管理员联系';
    }
    $('#submitResult').html(result);
    resetYesButton();
    $('#yes').unbind('click').removeAttr('onclick').click(function () {
        clearAndHide();
    });
}

function clearAndHide() {
    $('#submitResult').html('');
    $('#warningModal').modal('hide');
    resetYesButton();
}

function preprocessModal(warningText){
    $('#confirmInfo').html(warningText);
    $('#warningModal').modal('show');
    $('#no').unbind('click').removeAttr('onclick').click(function (){
        clearAndHide();
    });
}

function showModalAndBind(warningText, bindMethod){
    preprocessModal(warningText);
    $('#yes').unbind('click').removeAttr('onclick').click(function (){
        $('#yes').html('正在处理...');
        $('#yes').prop('disabled','disabled');
        bindMethod();
    });
}