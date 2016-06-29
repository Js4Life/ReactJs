angular
    .module('RDAApp', [ 'ui.router', 'ngAnimate', 'RDAApp.controllers' ])

    .config(function($stateProvider, $urlRouterProvider, $compileProvider) {
          $compileProvider
              .imgSrcSanitizationWhitelist(/^\s*(https?|ftp|mailto|content|file|data):/);

          $stateProvider.state('landing', {
            url : '/landing',
            templateUrl : 'landing',
            controller : 'landingCtrl'
          })

          .state('landing.home', {
            url : '/home',
            templateUrl : 'home',
            controller : 'homeCtrl'
          })

          .state('landing.risk', {
            url : '/risk-aggregation',
            templateUrl : 'riskAggregation',
            controller : 'riskCtrl'
          })

          .state('landing.dashboardProfile', {
            url : '/dashboard-profile',
            templateUrl : 'dashboardProfile',
            controller : 'dashboardProfileCtrl'
          })

          .state('landing.dashboardGraphProfile', {
            url : '/dashboard-graph-profile',
            templateUrl : 'dashboardGraphProfile',
            controller : 'dashboardGraphProfileCtrl'
          })

          .state('landing.status', {
            url : '/status',
            templateUrl : 'status',
            controller : 'statusCtrl'
          })

          .state('landing.schedule', {
            url : '/schedule',
            templateUrl : 'schedule',
            controller : 'scheduleCtrl'
          })

          .state('landing.gapdetails', {
            url : '/gapdetails',
            templateUrl : 'gapdetails',
            controller : 'gapDetailsCtrl'
          })

          .state('landing.mergeAggregator', {
            url : '/aggregator',
            templateUrl : 'aggregator',
            controller : 'margeAggCtrl'
          })

          .state('landing.common', {
            url : '/view',
            templateUrl : 'graph',
            controller : 'commonCtrl'
          })

          .state('landing.analysis', {
            url : '/analysisArea',
            templateUrl : 'analysisArea',
            controller : 'analysisCtrl'
          })

          .state('landing.graph', {
            url : '/graph',
            templateUrl : 'graph',
            controller : 'RDACtrl'
          })

          .state('landing.chart', {
            url : '/chart',
            templateUrl : 'chart',
            controller : 'chartCtrl'
          })

          .state('landing.aggregate', {
            url : '/aggregate',
            templateUrl : 'riskaggregate',
            controller : 'aggCtrl'
          })

          .state('landing.simulator', {
            url : '/simulator',
            templateUrl : 'simulator',
            controller : 'simulateCtrl'
          })

          .state('landing.simulatorGraph', {
            url : '/simulatorGraph',
            templateUrl : 'simulatorGraph',
            controller : 'simulategraphCtrl'
          })


          .state('landing.newsimulator', {
            url : '/newsimulator',
            templateUrl : 'newsimulator',
            controller : 'newsimulateCtrl'
          })

          .state('landing.newsimulatorGraph', {
            url : '/newsimulatorGraph',
            templateUrl : 'newsimulatorGraph',
            controller : 'newsimulategraphCtrl'
          })


          .state('landing.aggregatedbview', {
            url : '/aggregatedbview',
            templateUrl : 'aggregateDbView',
            controller : 'aggregatedbviewCtrl'
          })

          .state('landing.logicalview', {
            url : '/logicalview',
            templateUrl : 'logicalView',
            controller : 'logicalviewCtrl'
          })

          .state('landing.mapdatasource', {
            url : '/mapdatasource',
            templateUrl : 'mapDatasource',
            controller : 'mapdatasourceCtrl'
          })

          .state('landing.mapdatasourcerelation', {
            url : '/mapdatasourcerelation',
            templateUrl : 'mapDatasourceRelation',
            controller : 'mapdatasourcerelationCtrl'
          })

          .state('landing.mapdatasourceview', {
            url : '/mapdatasourceview',
            templateUrl : 'mapDatasourceView',
            controller : 'mapdatasourceviewCtrl'
          })

          .state('landing.aggregateGroup',{
              url: '/aggregateGroup',
              templateUrl: 'aggregateGroup',
              controller: 'aggGrpCtrl'
           })

          .state('landing.report',{
              url: '/report',
              templateUrl: 'reportBuilder',
              controller: 'reportCtrl'
          })

          .state('landing.aggregateGroup.pagebuilder',{
              url: '/pagebuilder',
              templateUrl: 'pageBuilder',
              controller: 'pagebuilderCtrl'
          })

          .state('landing.aggregateGroup.layoutbuilder',{
            url: '/layoutbuilder',
            templateUrl: 'layoutBuilder',
            controller: 'layoutbuilderCtrl'
          })
          
          .state('landing.datasource',{
              url: '/datasource',
              templateUrl: 'datasourceBuilder',
              controller: 'datasourceCtrl'
          })
          
          .state('landing.user',{
              url: '/user',
              templateUrl: 'userBuilder',
              controller: 'userCtrl'
          })
          
          .state('landing.userdetail',{
              url: '/userdetail',
              templateUrl: 'userDetail',
              controller: 'userDetailCtrl'
          })
          
          .state('landing.usergroupdetail',{
              url: '/usergroupdetail',
              templateUrl: 'userGroupDetail',
              controller: 'userGroupDetailCtrl'
          })

          .state('reportPreview',{
              url: '/reportPreview',
              templateUrl: 'reportPreview',
              controller: 'reportPreviewCtrl'
          })

          .state('landing.combinedview',{
              url: '/combinedview',
              templateUrl: 'combinedView',
              controller: 'combinedviewCtrl'
          })

          .state('landing.glossary', {
            url : '/glossary',
            templateUrl : 'glossary',
            controller : 'glossaryCtrl'
          })

          .state('landing.statusProfile', {
            url : '/status-profile',
            templateUrl : 'statusProfile',
            controller : 'statusProfileCtrl'
          })

          .state('landing.statusReport', {
            url : '/status-report',
            templateUrl : 'statusReport',
            controller : 'statusReportCtrl'
          })

          .state('landing.dashboardSelector', {
            url : '/dashboard-selector',
            templateUrl : 'dashboardSelector',
            controller : 'dashboardSelectorCtrl'
          })

          .state('landing.pageArchive', {
            url : '/page-archive',
            templateUrl : 'pageArchive',
            controller : 'pageArchiveCtrl'
          })

          .state('landing.ewgReport', {
            url : '/ewg-report',
            templateUrl : 'ewgReport',
            controller : 'ewgReportCtrl'
          })

          .state('landing.ewgIssue', {
            url : '/ewg-issue',
            templateUrl : 'ewgIssue',
            controller : 'ewgIssueCtrl'
          })

          .state('landing.heatMap', {
            url : '/heat-map',
            templateUrl : 'heatMap',
            controller : 'heatMapCtrl'
          })

          .state('landing.scheduleTable', {
            url : '/schedule-table',
            templateUrl : 'scheduleTable',
            controller : 'scheduleTableCtrl'
          });

    $urlRouterProvider.otherwise('/landing/dashboard-selector');
  });