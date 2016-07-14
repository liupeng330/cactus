function getSidebarUrl() {
    var sidebarUrl = [];
    $('ul.main-menu li a').each(function () {
        sidebarUrl.push($($(this))[0].href);
    });
    return sidebarUrl;
}

function inList(list, item) {
    var i = 0;
    var isExist = false;
    for (i = 0; i < list.length; i++) {
        if (item == list[i]) {
            isExist = true;
        }
    }
    return isExist;
}

$(document).ready(function () {
    $('ul.main-menu li a').each(function () {
        var curUrl = String(window.location);
        if(inList(getSidebarUrl(), curUrl)) {
            sessionStorage.lastUrl = curUrl;
        }
    });

    $('ul.main-menu li a').each(function () {
        var thisUrl = $($(this))[0].href;
        if(thisUrl == sessionStorage.lastUrl) {
            $(this).parent().addClass('active');
        }
    });

    //animating menus on hover
    $('ul.main-menu li a:not(.nav-header)').hover(function () {
            $(this).animate({'margin-left': '+=5'}, 300);
        },
        function () {
            $(this).animate({'margin-left': '-=5'}, 300);
        });

});
