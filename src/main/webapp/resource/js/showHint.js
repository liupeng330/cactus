function showHint(inputId, loadUrlFunc, loadParamFunc) {
    $('#'+inputId).autocomplete({
        source:function(query,process){
            $.post(loadUrlFunc(), loadParamFunc(query), function (respData) {
                if (respData.status == 0) {
                    console.log(respData.data);
                    return process(respData.data);
                } else {
                    console.log(respData.message);
                }
            });
        },
        formatItem:function(item){
            return item;
        },
        setValue:function(item){
            return {'data-value':item,'real-value':item};
        }
    });

}
