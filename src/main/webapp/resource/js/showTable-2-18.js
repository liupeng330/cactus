function resetAll() {
    $('table.table tr').each(function () {
        $(this).css('display', '');
    });
}

function showByState(state, textObj) {
    $('#stateText').html(textObj.innerHTML + '<span class="caret"></span>');
    resetAll();
    $('table.table tr').each(function () {
        console.log($(this).attr('state'));
        if (state != 'all' && $(this).attr('state') != state && $(this).attr('state') != undefined) {
            $(this).css('display', 'none');
        }
    });
}

function showByCondition() {
    $('table tr').each(function () {
        $(this).css('display', '');
    });
    var app = $('#app').val().trim();
    var ip = $('#ip').val().trim();
    var method = $('#method').val().trim();
    console.log(app + "|" + ip + "|" + method);
    $("span[id^='app']").each(function () {
        var index = $(this).attr('id').split('_')[1];
        console.log("index is " + index);
        if (isMatch($(this).html(), app)) {
            var ipId = 'ip_' + index;
            if (isMatch($('#' + ipId).html(), ip)) {
                var methodId = 'method_' + index;
                if (!isInMethods($('#' + methodId).html(), method)) {
                    hidden(index);
                }
            } else {
                hidden(index);
            }
        } else {
            hidden(index);
        }
    });
}

function hidden(index) {
    $('#ip_tr_' + index).css('display', 'none');
    $('#prop_tr_' + index).css('display', 'none');
    var shouldHide = true;
    var itemIndex = $('#ip_tr_' + index).attr('data');
    console.log("itemIndex is " + itemIndex);
    $('#consumer_' + itemIndex + ' tbody tr').each(function () {
        if ($(this).css('display') != 'none') {
            shouldHide = false;
        }
    });
    if (shouldHide) {
        $('#item_' + itemIndex).css('display', 'none');
        $('#item_space_' + itemIndex).css('display', 'none');
    }
}

function isInMethods(domValue, value) {
    var methods = domValue.split(',');
    var i=0;
    var isContains = false;
    for(i=0; i<methods.length; i++) {
        if(isMatch(methods[i], value)){
            isContains = true;
            break;
        }
    }
    return isContains;
}

function isMatch(domValue, value) {
    console.log("domValue is " + domValue + " | value is " + value + " | dom==value is " + (domValue.trim() == value.trim()));
    if (value == '') {
        return true;
    }
    return domValue.trim() == value.trim();

}

