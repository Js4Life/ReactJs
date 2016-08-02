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
              templateUrl : 'home',
              controller : 'homeCtrl'
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
          });

          $urlRouterProvider.otherwise('/landing/home');
  });