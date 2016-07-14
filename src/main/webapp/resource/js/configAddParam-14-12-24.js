function selectParamsRender(bindId, itemClsName, splitter) {
    var body = '';
    $("." + itemClsName).each(function () {
        var data = $(this).attr("data");
        var value = $(this).val();
        body += '<div class="checkbox">'
            + '<label>'
            + '<input class="itemToSelect" type="checkbox" value="' + value + '">' + data
            + '</label>'
            + '</div>';
    });
    bindAndShow("", body, function () {
        var content = '';
        $(".itemToSelect:checked").each(function () {
            content += $(this).val() + splitter;
        });
        if (content != '') {
            $('#' + bindId).val(content.substring(0, content.length - 1));
        }
        $('#info-modal').modal('hide');
    });
}

function bindAndShow(title, body, yesMethod) {
    $('#info-modal-title').html(title);
    $('#info-modal-body').html(body);
    $('#info-modal').modal('show');
    $('#info-modal-no').unbind('click').removeAttr('onclick').click(function () {
        $('#info-modal').modal('hide');
    });
    $('#info-modal-yes').unbind('click').removeAttr('onclick').click(function () {
        yesMethod();
    });
}

function addParam() {
    addParamWithKeyAndValue('', '');
}

function addParamWithKeyAndValue(key, value) {
    var html = "<div class='removableField'><div class='row'> " +
        "<div class='row'>" +
        "<div class='col-md-12'>" +
        "&nbsp;" +
        "</div>" +
        "</div>" +
        "<div class='col-md-1'></div>" +
        "<div class='col-md-11'>" +
        "<div class='form-inline'>" +
        "参数名：<input name='key' class='form-control' style='width: 100px' type='text' value='" + key + "'/>" +
        "&nbsp; &nbsp; &nbsp;" +
        "参数值：<input name='value' class='form-control' style='width: 200px;' type='text' value='" + value + "'/>" +
        "&nbsp;" +
        "<input type='button' value='删除' class='btn btn-default' onclick='removeParentDiv(this)'>" +
        "</div>" +
        "</div>" +
        "</div></div>";
    $('#paramField').append(html);
}

function checkIsExist(list, value) {
    var isExist = false;
    var i = 0;
    for (i = 0; i < list.length; i++) {
        if (list[i] == value) {
            isExist = true;
            return isExist;
        }
    }
    return isExist;
}

function isEmpty(value) {
    if (value.trim() == '') {
        return true;
    }
    return false;
}

function selectAddress() {
    putSelectOptionToInput('providerList', 'hostname', ',');
}


function putSelectOptionToInput(selectId, input, splitter) {
    var selected = $('#' + selectId).val();
    var inputValue = $('#' + input).val();
    var origValue = inputValue.split(splitter);
    if (!checkIsExist(origValue, selected) && selected != '请选择') {
        if (isEmpty(inputValue)) {
            $('#' + input).val(selected);
        } else {
            $('#' + input).val(inputValue + splitter + selected);
        }
    }
}

function submitRoute(submitUrl) {
    showModalAndBind('确定保存？', function () {
        doSubmitRoute(submitUrl);
    });
}

function doSubmitRoute(submitUrl) {
    $.ajax({
        url: submitUrl,//发送请求地址
        type: "post",
        data: { //发送的数据串
            id: $('#id').val(),
            group: $('#group').val(),
            name: $('#routerName').val(),
            priority: $('#priority').val(),
            serviceKey: $('#serviceKey').val(),
            zkId:$('#zkId').val(),
            matchMethods: $('#matchMethods').val(),
            misMatchMethods: $('#misMatchMethods').val(),
            enabled: $('#enabled').val(),
            consumerMatchIp: $('#consumerMatchIp').val(),
            consumerMismatchIp: $('#consumerMismatchIp').val(),
            matchApp: $('#matchApp').val(),
            mismatchApp: $('#mismatchApp').val(),
            providerMatchIp: $('#providerMatchIp').val(),
            providerMismatchIp: $('#providerMismatchIp').val(),
            providerMatchPort: $('#providerMatchPort').val(),
            providerMismatchPort: $('#providerMismatchPort').val()
        },
        success: function (back) {
            dealBackMessageAndRedirect(back, function () {
                history.go(-1);
            });
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            dealFailResult(XMLHttpRequest);
        }
    });
}

function redirectToPreview(previewUrl) {
    var param = {
        group: $('#group').val(),
        name: $('#routerName').val(),
        priority: $('#priority').val(),
        serviceKey: $('#serviceKey').val(),
        zkId:$('#zkId').val(),
        matchMethods: $('#matchMethods').val(),
        misMatchMethods: $('#misMatchMethods').val(),
        enabled: $('#enabled').val(),
        consumerMatchIp: $('#consumerMatchIp').val(),
        consumerMismatchIp: $('#consumerMismatchIp').val(),
        matchApp: $('#matchApp').val(),
        mismatchApp: $('#mismatchApp').val(),
        providerMatchIp: $('#providerMatchIp').val(),
        providerMismatchIp: $('#providerMismatchIp').val(),
        providerMatchPort: $('#providerMatchPort').val(),
        providerMismatchPort: $('#providerMismatchPort').val() };
    var url = previewUrl + '?' + mergeParam(param);
    window.open(url, '');
}


function mergeParam(param) {
    var s = '';
    for (var key in param) {
        s += '&' + key + '=' + param[key];
    }
    s = s.substr(1);
    console.log(s);
    return s;
}


function loadAddress(inputId) {
    var hostnames = $(inputId).val().split(',');
    var addresses = '';
    var i = 0;
    for (i = 0; i < hostnames.length; i++) {
        if (hostnames[i].trim().length != 0) {
            var value = document.getElementById(hostnames[i]).value;
            addresses += (',' + value);
        }
    }
    return addresses.substring(1);
}

function getInvalidHostnames(inputId) {
    var hostnames = $(inputId).val().split(',');
    var invalidHostnames = [];
    for (i = 0; i < hostnames.length; i++) {
        if (hostnames[i].trim().length != 0) {
            if (document.getElementById(hostnames[i]) == null) {
                invalidHostnames.push(hostnames[i]);
            }
        }
    }
    return invalidHostnames;
}

function loadParamAndSubmit() {
    var params = '';
    var shouldSubmit = true;

    $('input[name="value"]').each(function () {
        var paramKey = $(this).prev().val().trim();
        var paramValue = $(this).val().trim();
        if (paramKey == '') {
            alert('参数不能为空！');
            shouldSubmit = false;
        } else if (paramValue == '') {
            alert('参数值不能为空！');
            shouldSubmit = false;
        }
        params += (paramKey + "=" + paramValue + "&");
    });
    var invalidHostnames = getInvalidHostnames('#hostname');
    if (invalidHostnames.length > 0) {
        shouldSubmit = false;
        alert('机器' + invalidHostnames + '不是该服务的provider!');
    }

    if (shouldSubmit) {
        showModalAndBind('确定保存？', function () {
            doLoadParamAndSubmit(params);
        });
    }
}

function doLoadParamAndSubmit(params) {
    params = params.substr(0, params.length - 1);
    $('#params').val(params);
    var submitUrl = $('#rootPath').val() + "/configurator/add";
    //console.log(params);
    $.ajax({
        url: submitUrl,//发送请求地址
        type: "post",
        data: { //发送的数据串
            id: $('#id').val(),
            zkId: $('#zkId').val(),
            name: $('#name').val(),
            group: $('#group').val(),
            serviceGroup: $('#serviceGroup').val(),
            addresses: loadAddress('#hostname'),
            service: $('#service').val(),
            version: $('#version').val(),
            configParams: $('#params').val()
        },
        success: function (back) {
            dealBackMessageAndRedirect(back, function () {
                history.go(-1);
            });
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            dealFailResult(XMLHttpRequest);
        }
    });
}

function loadSelectedGovernanceData() {
    var dataList = [];
    $("input[id^='id']").each(function () {
            if ($(this).prop('checked')) {
                var id = $(this).attr('id').split('_')[1];
                dataList.push(loadGovernanceData('#governanceData_' + id));
            }
        }
    );
    return dataList;
}

function getOperateText(submitUrl) {
    var index = submitUrl.lastIndexOf("/");
    var operateInfo = submitUrl.substring(index + 1, index + 2);
    if (operateInfo == '0') {
        return '启用';
    } else {
        return '禁用';
    }
}


function changeEnabledState(btnId, submitUrl) {
    showModalAndBind('确定' + getOperateText(submitUrl) + '?', function () {
        var governanceData = loadGovernanceData(transFormBtnId2GovernanceId(btnId));
        submitChangeEnableState(submitUrl, governanceData, btnId);
    });
}


function submitChangeEnableState(submitUrl, governanceData, btnId) {
    $.ajax({
        url: submitUrl,//发送请求地址
        contentType: 'application/json',
        dataType: 'json',
        type: "post",
        data: JSON.stringify(governanceData),
        success: function (back) {
            dealBackMessageAndReload(back);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $('#' + btnId).button('reset');
            dealFailResult(XMLHttpRequest);
        }
    });
}

function getOnlineOperateInfo(operationType) {
    console.log(operationType);
    if (operationType == '0') {
        return "上线";
    } else {
        return "下线";
    }
}

function changeOnlineState(btnId, submitUrl, group, url, operationType) {
    showModalAndBind('确定' + getOnlineOperateInfo(operationType) + '?', function () {
        updateOnlineState(btnId, submitUrl, group, url, operationType);
    });
}


function updateOnlineState(btnId, submitUrl, group, url, operationType) {
    $('#' + btnId).button('loading');
    $.ajax({
        url: submitUrl,//发送请求地址
        type: "post",
        data: { //发送的数据串
            group: group,
            url: url,
            operationType: operationType
        },
        success: function (back) {
            dealBackMessageAndReload(back);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $('#' + btnId).button('reset');
            dealFailResult(XMLHttpRequest);
        }
    });

}
var DOUBLE_WEIGHT = 1;
var HALVE_WEIGHT = 0;

function getUpdateWeightText(operateType) {
    if (operateType == DOUBLE_WEIGHT) {
        return "倍权";
    } else {
        return "半权";
    }
}

function updateWeight(btnId, submitUrl, group, url, operationType) {
    showModalAndBind('确定' + getUpdateWeightText(operationType) + '?', function () {
        updateConfigWithUrl(btnId, submitUrl, group, url, operationType);
    });
}

function updateConfigWithUrl(btnId, submitUrl, group, url, operationType) {
    $.ajax({
        url: submitUrl,//发送请求地址
        type: "post",
        data: { //发送的数据串
            group: group,
            url: url,
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

function updateMock(submitUrl, btnId) {
    showModalAndBind('确定保存？', function () {
        doUpdateMock(submitUrl, btnId);
    });
}

function doUpdateMock(submitUrl, btnId) {
    $.ajax({
        url: submitUrl,//发送请求地址
        type: "post",
        data: { //发送的数据串
            id: $('#id').val(),
            group: $('#group').val(),
            serviceKey: $('#serviceKey').val(),
            address: $('#address').val(),
            applicationName: $('#app').val(),
            mockType: $('#mockType').val(),
            zkId:$('#zkId').val()
        },
        success: function (back) {
            dealBackMessageAndRedirect(back, function () {
                history.go(-1);
            });
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            dealFailResult(XMLHttpRequest);
        }
    });
}

var SHIELD = 'force:return+null';
var FAILOVER = 'fail:return+null';
var NONEMOCK = 'nonemock';

function getMockOperateText(mockType) {
    if (mockType == SHIELD) {
        return '屏蔽';
    } else if (mockType == FAILOVER) {
        return '容错';
    } else {
        return '恢复';
    }
}

function updateConfigWithServiceKey(btnId, submitUrl, group, serviceKey, address, applicationName, mockType, zkId) {
    showModalAndBind('确定' + getMockOperateText(mockType) + '?', function () {
        doUpdateConfigWithServiceKey(btnId, submitUrl, group, serviceKey, address, applicationName, mockType, zkId);
    });
}

function doUpdateConfigWithServiceKey(btnId, submitUrl, group, serviceKey, address, applicationName, mockType, zkId) {
    $.ajax({
        url: submitUrl,//发送请求地址
        type: "post",
        data: { //发送的数据串
            group: group,
            serviceKey: serviceKey,
            address: address,
            applicationName: applicationName,
            mockType: mockType,
            zkId:zkId
        },
        success: function (back) {
            dealBackMessageAndReload(back);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            dealFailResult(XMLHttpRequest);
        }
    });
}

function removeParentDiv(element) {
    if ($('#paramField').find("input").length <= 3) {
        alert('至少要存在一个参数！');
    } else {
        $(element).parent().parent().parent().remove();
    }
}

function configLoadBalance() {
    showModalAndBind('确定保存？', function () {
        doConfigLoadBalance();
    });
}

function doConfigLoadBalance() {
    var submitUrl = $('#rootPath').val() + "/configurator/loadBalance";
    $.ajax({
        url: submitUrl,//发送请求地址
        type: "post",
        data: { //发送的数据串
            id: $('#id').val(),
            group: $('#group').val(),
            serviceGroup: $('#serviceGroup').val(),
            service: $('#service').val(),
            version: $('#version').val(),
            loadBalance: $('#loadBalance').val(),
            zkId:$('#zkId').val()
        },
        success: function (back) {
            dealBackMessageAndRedirect(back, function () {
                history.go(-1);
            });
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            dealFailResult(XMLHttpRequest);
        }
    });
}

function loadGovernanceData(divId) {
    var governanceData = {};
    $(divId).find('input').each(function () {
        governanceData[$(this).attr("name")] = $(this).val();
    });
    return governanceData;
}

function transFormBtnId2GovernanceId(btnId) {
    var rowIndex = btnId.split("_")[2];
    return "#governanceData_" + rowIndex;
}

function deleteUrl(btnId, submitUrl) {
    $('#confirmInfo').html('确定删除?');
    $('#warningModal').modal('show');
    $('#yes').unbind('click').removeAttr('onclick').click(function () {
        doDelete(btnId, submitUrl);
    });
    $('#no').unbind('click').removeAttr('onclick').click(function () {
        clearAndHide();
    });
}

function doDelete(btnId, submitUrl) {
    $.ajax({
        url: submitUrl,//发送请求地址
        contentType: 'application/json',
        dataType: 'json',
        type: "post",
        data: JSON.stringify(loadGovernanceData(transFormBtnId2GovernanceId(btnId))),
        success: function (back) {
            dealBackMessageAndReload(back);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            $('#' + btnId).button('reset');
            dealResult(XMLHttpRequest);
        }
    });
}

function dealResult(XMLHttpRequest) {
    var responseText = XMLHttpRequest.responseText;
    if (responseText.indexOf('没有权限访问') > 0) {
        alert('您没有权限执行该操作！');
    } else {
        alert('出现网络错误或异常，请与管理员联系');
    }
}

function setParamsToInput() {
    var params = $("#params").val();
    if (params.trim().length == 0) {
        addParam();
    } else {
        var paramList = params.split('|');
        var i = 0;
        for (i = 0; i < paramList.length; i++) {
            var keyAndValue = paramList[i].split('=');
            if (keyAndValue.length == 2) {
                addParamWithKeyAndValue(keyAndValue[0], keyAndValue[1]);
            }
        }
    }
}

function replace_overflow() {
    $(".over_c")
        .mouseenter(function () {
            $(this).css("overflow-x", "auto");
        })
        .mouseleave(function () {
            $(this).css("overflow", "hidden");
        });
}