angular
    .module('RDAApp', [ 'ui.router', 'ngAnimate', 'angularjs-dropdown-multiselect', 'RDAApp.controllers' ])

    .config(function($stateProvider, $urlRouterProvider, $compileProvider) {
          $compileProvider
              .imgSrcSanitizationWhitelist(/^\s*(https?|ftp|mailto|content|file|data):/);

          $stateProvider.state('landing', {
            url : '/landing',
            templateUrl : 'landing',
            controller : 'landingCtrl'
          })

          .state('landing.risk', {
              url : '/risk-aggregation',
              templateUrl : 'riskAggregation',
              controller : 'riskCtrl'
          })

          .state('landing.home', {
              url : '/home',
              templateUrl : 'home'
          })

          .state('landing.impact', {
              url : '/impact',
              templateUrl : 'impact',
              controller : 'impactCtrl'
          })

          .state('landing.regulation', {
              url : '/regulation',
              templateUrl : 'regulation',
              controller : 'regulationCtrl'
          })

          .state('landing.checklistBuilder', {
              url : '/checklist-builder',
              templateUrl : 'checklistBuilder',
              controller : 'checklistBuilderCtrl'
          })

          .state('landing.homeContainer', {
              url : '/home-container',
              templateUrl : 'homeContainer'
          })

          .state('landing.summery', {
              url : '/summery',
              templateUrl : 'summery'
          })

          .state('landing.complianceDashboard', {
              url : '/compliance-dashboard',
              templateUrl : 'complianceDashboard',
              controller : 'complianceDashboardCtrl'
          })

          .state('landing.complianceDashboard.documentViewer', {
              templateUrl : 'checklistViewer',
              controller : 'homeCtrl'
          })

          .state('landing.complianceDashboard.checklistViewer', {
              templateUrl : 'checklistViewer',
              controller : 'checklistViewerCtrl'
          });

          $urlRouterProvider.otherwise('/landing/home-container');
  });