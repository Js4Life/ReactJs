var app = angular.module('Skylink', []);

app.constant('NavigationConfig', {
  links: [{
    name: 'Demos',
    desc: 'Write a simple Skylink Web Application with these simple demos to get you started',
    icon: 'fa-columns',
    page: 'demo',
    section: [{
      title: null,
      links: [{
        name: 'Demos Libary',
        page: 'demo-list',
        section: []
      }, {
        name: 'Try getaroom.io',
        page: 'https://getaroom.io',
        section: []
      }]
    }]
  }, {
    name: 'Documention',
    desc: 'See the full documentation for more details on each objects',
    icon: 'fa-code',
    page: 'doc',
    section: [{
      title: 'Documentation',
      links: [{
        name: 'Web Client SDK',
        page: 'doc-web',
        section: []
      }, {
        name: 'REST API',
        page: 'doc-rest',
        section: [{
          title: null,
          links: [{
            name: 'Current Room API',
            page: 'https://temasys.atlassian.net/wiki/display/TPD/Skylink+REST+API',
            section: []
          }, {
            name: 'Platform API',
            page: 'https://temasys.atlassian.net/wiki/display/TPD/Skylink+REST+API',
            section: []
          }]
        }]
      }]
    }, {
      title: 'Configuration',
      links: [{
        name: 'WebRTC Stack Features',
        page: 'config-stack',
        section: [{
          title: null,
          links: [{
            name: 'Configure TURN / STUN in Client SDK',
            page: 'config-stack-turn',
            section: []
          }, {
            name: 'Configure MCU Feature for Client SDK',
            page: 'config-stack-mcu',
            section: []
          }, {
            name: 'Configure SIP Feature for Client SDK',
            page: 'config-stack-sip',
            section: []
          }, {
            name: 'Configure Recording Feature for Client SDK',
            page: 'config-stack-record',
            section: []
          }]
        }]
      }, {
        name: 'Application Key Features',
        page: 'config-app',
        section: [{
          title: null,
          links: [{
            name: 'Integrate Privileged Key Feature for Client SDK',
            page: 'config-app-privilege',
            section: []
          }, {
            name: 'Integrate Realm Key Space Feature for Client SDK',
            page: 'config-app-space',
            section: []
          }]
        }]
      }]
    }]
  }, {
    name: 'Support',
    desc: 'Report a bug or request a new feature in support',
    icon: 'fa-bug',
    page: 'support',
    section: [{
      title: null,
      links: [{
        name: 'Report a Bug',
        page: 'https://developer.temasys.com.sg/support',
        section: []
      }, {
        name: 'Contributing',
        page: 'https://github.com/Temasys/SkylinkJS/blob/master/CONTRIBUTING.md',
        section: []
      }]
    }]
  }, {
    name: 'Extensions',
    desc: 'Download the Skylink Browser Extensions or the IE / Safari Plugin',
    icon: 'fa-plug',
    page: 'ext',
    section: [{
      title: 'Browser Extensions',
      links: [{
        name: 'Firefox Extension',
        page: 'ext-browser-ff',
        section: []
      }, {
        name: 'Chrome Extension',
        page: 'ext-browser-chrome',
        section: []
      }]
    }, {
      title: 'IE / Safari WebRTC Plugin',
      links: [{
        name: 'Download Free',
        page: 'http://confluence.temasys.com.sg/display/TWPP/WebRTC+Plugins',
        section: []
      }, {
        name: 'Debugging the Plugin from Client SDK',
        page: 'ext-plugin-debug',
        section: []
      }, {
        name: 'How to integrate screensharing with Plugin',
        page: 'ext-plugin-screensharing',
        section: []
      }]
    }, {
      title: 'Client Dependencies',
      links: [{
        name: 'Socket.io-client',
        page: 'http://socket.io/download/',
        section: []
      }, {
        name: 'AdapterJS',
        page: 'https://github.com/Temasys/AdapterJS',
        section: []
      }]
    }]
  }]
});

app.controller('DocController', function ($scope, $location, NavigationConfig) {
  $scope.page = 'templates/pages/main.html';

  $scope.$watch(function () {
    return window.location.search;
  }, function () {
    var data = {};
    var url = window.location.search;

    if (url.indexOf('?') === 0) {
      var parse = url.split('?')[1];
      var parts = parse.split('&');

      for (var i = 0; i < parts.length; i++) {
        if (parts[i]) {
          var items = parts[i].split('=');
          data[items[0]] = items[1];
        }
      }
    }

    $scope.page = 'templates/pages/' + (data.page || 'main') + '.html';
    console.log('Loaded page', $scope.page, data);
  });

  $scope.menu = NavigationConfig.links;
});

app.directive('navbartop', function(NavigationConfig) {
  'use strict';
  return {
    transclude: true,
    replace: true,
    restrict: 'A,E',
    templateUrl: 'templates/components/navbar-top.html',
    link: function ($scope, $elm, $attrs) {
      $scope.menu = NavigationConfig.links;
    }
  };
});

app.directive('navbarside', function() {
  'use strict';
  return {
    transclude: true,
    replace: true,
    restrict: 'A,E',
    templateUrl: 'templates/components/navbar-side.html',
    link: function ($scope, $elm, $attrs) {}
  };
});

app.directive('navbartopitem', function ($sce) {
  'use strict';
  return {
    transclude: true,
    replace: true,
    restrict: 'A,E',
    scope: {
      data: '='
    },
    templateUrl: 'templates/components/navbar-top-item.html',
    link: function ($scope, $elm, $attrs) {
      //$scope.data = $attrs.data;
      $scope.content = '';

      var populateItem = function (data) {
        var page = data.page.indexOf('http') !== 0 ? '?page=' + data.page : data.page;
        var html = '<a href="' + (data.section.length === 0 ? page : '') +
          '">' + data.name + '</a>';

        if (data.section.length) {
          html += '<ul class="dropdown">' +
            '<li class="title back js-generated">' +
            '<h5><a href="javascript:void(0)">Back</a></h5></li>';

          for (var i = 0; i < data.section.length; i++) {
            var sectionItem = data.section[i];

            if (sectionItem.title !== null) {
              html += '<li><label>' + sectionItem.title + '</label></li>';
            }

            for (var j = 0; j < sectionItem.links.length; j++) {
              var linkItem = sectionItem.links[j];
              html += '<li class="' + (linkItem.section.length > 0 ? 'has-dropdown not-click' : '') + '">' +
                populateItem(sectionItem.links[j]) + '</li>';
            }

            if (sectionItem.title !== null) {
              html += '<li class="divider"></li>';
            }
          }

          html += '</ul>';
        }

        return html;
      };

      $scope.content = $sce.trustAsHtml( populateItem($scope.data) );

      $(document).foundation();
    }
  };
});