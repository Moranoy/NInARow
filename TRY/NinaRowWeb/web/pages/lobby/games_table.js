// '.tbl-content' consumed little space for vertical scrollbar, scrollbar width depend on browser/os/platfrom. Here calculate the scollbar width .
$(window).on("load resize ", function() {
    var scrollWidth = $('.tbl-content').width() - $('.tbl-content table').width();
    $('.tbl-header-games').css({'padding-right':scrollWidth});
    $('.tbl-header-users').css({'padding-right':scrollWidth});
}).resize();