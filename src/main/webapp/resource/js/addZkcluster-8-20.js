function add(){
    var submitUrl = $('#rootPath').val()+"/zkcluster/addZkCluster" ;
    $('#add').button('loading');
    $.ajax({
        url: submitUrl,//发送请求地址
        type: "post",
        data: { //发送的数据串
            name:$('#name').val(),
            address:$('#address').val()

        },
        success: function (back) {
            $('#add').button('reset');
            if (back.status == 0) {
                alert('添加机房地址成功！');
                window.location.href = $('#rootPath').val()+"/zkcluster/showPage";
            } else {
                if (back.message == undefined) {
                    alert('您没有权限执行该操作！');
                } else {
                    alert(back.message);
                }
            }
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
            $('#add').button('reset');
            alert('出现网络错误或异常，请与管理员联系');
            console.log(XMLHttpRequest.status);
        }
    });
}