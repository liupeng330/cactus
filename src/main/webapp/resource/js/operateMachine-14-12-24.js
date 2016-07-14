function selectAll() {
    var checkState = $('#checkAll').prop('checked');
    console.log(checkState);
    $("input[id^='id']").prop('checked', checkState);
}

function loadAndRelocate(rootPath, group, serviceKey, zkId) {
    var serviceGroup;
    var version;
    var intefaze;
    var indexOfSlash = serviceKey.indexOf('/');
    if (indexOfSlash != -1) {
        serviceGroup = serviceKey.substring(0, indexOfSlash);
        serviceKey = serviceKey.substring(indexOfSlash + 1);
    } else {
        serviceGroup = "";
    }

    var indexOfColon = serviceKey.indexOf(':');
    if (indexOfColon != -1) {
        version = serviceKey.substring(indexOfColon + 1);
        serviceKey = serviceKey.substring(0, indexOfColon);
    } else {
        version = "";
    }
    intefaze = serviceKey;
    var address = loadSelectedData(",");
    if (address.trim() != '') {
        window.location = rootPath + "/configurator/" + "addPageRender?group=" + group + "&serviceGroup="
            + serviceGroup + "&version=" + version + "&service=" + intefaze + "&zkId=" + zkId + "&address=" + address;
    } else {
        alert('请先选择至少一个提供者！');
    }
}

function loadSelectedData(splitter) {
    var data = '';
    $("input[id^='id']").each(function () {
            if ($(this).prop('checked')) {
                data = data + $(this).attr('data') + splitter;
            }
        }
    );
    return data.substring(0, data.length - splitter.length);
}

function changeSelectedAllBtnState(checkBoxId) {
    if ((!$('#' + checkBoxId).prop('checked')) && $('#checkAll').prop('checked')) {
        $('#checkAll').prop('checked', false);
    }
}

function rollbackFunction() {
}




var ONLINE_OPERATE = 0;
var OFFLINE_OPERATE = 1;

function getOnlineOperateText(operateType) {
    if (operateType == ONLINE_OPERATE) {
        return "上线";
    } else {
        return "下线";
    }
}

function onlineOrOffline(btnId, group, address, operateType) {
    showModalAndBind('确定' + getOnlineOperateText(operateType) + '?', function () {
        if (operateType == ONLINE_OPERATE) {
            doOnline(btnId, group, address);
        } else {
            doOffline(btnId, group, address);
        }
    });
}

function doOnline(btnId, group, address) {
    var submitUrl = $('#rootPath').val() + "/configurator/batchOnlineOrOffline";
    submit(submitUrl, group, address, ONLINE_OPERATE);
}

function doOffline(btnId, group, address) {
    var submitUrl = $('#rootPath').val() + "/configurator/batchOnlineOrOffline";
    submit(submitUrl, group, address, OFFLINE_OPERATE);
}

function batchOnlineOrOffline(btnId, group, operateType) {
    if (operateType == ONLINE_OPERATE) {
        var submitUrl = $('#rootPath').val() + "/configurator/batchOnlineOrOffline";
    } else {
        var submitUrl = $('#rootPath').val() + "/configurator/batchOnlineOrOffline";
    }
    var addresses = loadSelectedData('#$');
    if (addresses.trim() == '') {
        alert('请至少选择一台机器！');
    } else {
        showModalAndBind('确定' + getOnlineOperateText(operateType) + '?', function () {
            submit(submitUrl, group, addresses, operateType);
        });
    }
}

function submit(url, group, addresses, operationType) {
    $.ajax({
        url: url,//发送请求地址
        type: "post",
        data: { //发送的数据串
            group: group,
            addresses: addresses,
            operationType: operationType
        },
        success: function (back) {
            dealBackMessageAndReload(back);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            dealFailResult(XMLHttpRequest);
        }
    });
}

