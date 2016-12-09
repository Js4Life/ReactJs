angular
    .module('RDAApp', [ 'ui.router', 'ngAnimate', 'ngFileUpload', 'RDAApp.controllers', 'ui.grid', 'ui.grid.selection', 'ui.grid.exporter', 'ui.grid.moveColumns', 'ui.grid.resizeColumns'])

    .config(function($stateProvider, $urlRouterProvider, $compileProvider, $httpProvider) {
          $compileProvider
              .imgSrcSanitizationWhitelist(/^\s*(https?|ftp|mailto|content|file|data):/);

        $httpProvider.defaults.useXDomain = true;
        $httpProvider.defaults.withCredentials = true;
        delete $httpProvider.defaults.headers.common["X-Requested-With"];
        $httpProvider.defaults.headers.common["Accept"] = "application/json";
        $httpProvider.defaults.headers.common["Content-Type"] = "application/json";

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

          .state('landing.summary', {
              url : '/summary',
              templateUrl : 'summary'
          })

          .state('landing.complianceDashboard', {
              url : '/compliance-dashboard',
              templateUrl : 'complianceDashboard'/*,
              controller : 'complianceDashboardCtrl'*/
          })

          .state('landing.complianceDashboard.documentViewer', {
              templateUrl : 'checklistViewer',
              controller : 'homeCtrl'
          })

          .state('landing.complianceDashboard.checklistViewer', {
              url: '/:currentView',
              templateUrl : 'checklistViewer',
              controller : 'checklistViewerCtrl'
          })

          .state('landing.documentUploader', {
              url: '/documentUploader',
              templateUrl : 'documentUploader',
              controller : 'documentUploaderCtrl'
          });

          $urlRouterProvider.otherwise('/landing/regulation');
  });