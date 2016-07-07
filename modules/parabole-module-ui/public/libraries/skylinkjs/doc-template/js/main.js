$(document).ready(function() {
  /*$('body').on('click', '.toggle-sidebar', function () {
    $('body').toggleClass('sidebar-toggled')
  });

  $(window).scroll(function(){
    var scrollTop = $(this).scrollTop();
    var menuScrollTop = $('.scroll-row').scrollTop();

    // set the documentation
    if (menuScrollTop !== -1) {
      if (scrollTop > menuScrollTop + 150) {
        if (!$('#classdocs').hasClass('top-links-fixed')) {
          $('.top-links').addClass('top-links-fixed');
        }
      } else {
        $('.top-links').removeClass('top-links-fixed');
      }
    }
  });*/

  prettyPrint();

  $(document).foundation();
});
