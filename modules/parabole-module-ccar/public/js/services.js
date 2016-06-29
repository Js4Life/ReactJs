angular.module('RDAApp.services', [])

.factory('SharedService', function($rootScope , $http, $q) {
    var SharedService = {};
    SharedService.ServiceMap = {
        'LoadBaseNodes' : 'getBaseNodes',
        'LoadDashboardData' : 'getHardCodedResponse/DasboardData',
        'LoadChildNodes'   : 'getRelatedNodes/',
        'SaveInitialRisk' : '',
        'UpdateRiskNode' : '',
        'InitialChartData' : 'getHardCodedResponse/chartData',
        'RegulatoryDashboardData' : 'getHardCodedResponse/regulatoryDashboard',
        'EnterpriseDashboardData' : 'getHardCodedResponse/enterpriseDashboard',
        'EwgReportData' : 'getHardCodedResponse/ewgReport',
        'EscalationIssue' : 'getHardCodedResponse/escalationIssue',
        'DataQualityIssue' : 'getHardCodedResponse/dataQualityIssue',
        'DataRationalizationIssue' : 'getHardCodedResponse/dataRationalizationIssue',
        'SaveReport' : 'saveReport',
        'SaveAggregation' : 'saveAggregation',
        'SaveNodeMap' : 'saveNodeMap',
        'SaveDataSource' : 'saveDataSource',
        'GetAllConfigurationDetails' : 'getAllConfigurationDetailsByType/',
        'GetConfigurationDetailsByName' : 'getConfigurationDetailsByName/',
        'GetAggregateConfigurationDetailsByName' : 'getAggregateConfigurationDetailsByName/',
        'GetConfigurationList' : 'getConfigurationNames/',
        'GetConfigurationInfo' : 'getConfiguration/',
        'TestDatasourceConn' : 'connectionChecker',
        'DataSourceLookUp' : 'dataSourceLookUp',
        'CreateUser' : 'createUser',
        'DeleteUser' : 'deleteUser/',
        'GetAllUsers' : 'getAllUsers',
        'ReportGen' : 'reportGenerator',
        'DeleteConfiguration' : 'deleteConfig',
        'GenerateReportPdf' : 'reportGenerator',
        'DownloadReport' : 'reportDownload/',
        'FetchViewRdb' : 'fetchViewRdb',
        'SaveDbView' : 'saveDbViewConfiguration',
        'SaveLogicalViewOne' : 'saveLogicalViewOne',
        'SaveAggregateMapping' : 'saveAggregateMapping',
        'GetDBViewsOfANode' : 'getDBViewsOfANode',
        'FetchEdgeView' : 'fetchEdgeViewData',
        'GetDataFromLogicalView' : 'getDataFromLogicalView',
        'DeleteConfigurationDetailsByName' : 'deleteConfigurationDetailsByName/',
        'FetchAllLogicalViewsByNodeId' : 'getLogicalViewsByNodeId',
        'GetCombinedLogicalView' : 'getCombinedLogicalView',
        'DeleteAggregate' : 'deleteAggregate',
        'SaveBaseNodeConfiguration' : 'saveBaseNodeConfiguration',
        'GetBaseNodesMapping' : 'getBaseNodesMapping',
        'GetAggregateRootName' : 'getAggregateRootName',
        'SaveCombinedView' : 'saveCombinedView',
        'GetCombinedViews' : 'getCombinedViews',
        'SaveDataNodeCfg' : 'saveDataNodeConfiguration',
        'GetImpactedNodes' : 'getImpactedNodes',
        'GetImpactLogicalViewsByNodeId' : 'getAllLogicalViewsByNodeId',
        'GetWeightedImpactGraph' : 'getImpactedVerticesEndToEndLevel',
        'MergedConnectedGraphAndWeightedGraph' : 'mergedConnectedGraphAndWeightedGraph',
        'GetAllPossiblePathsBetweenTwoNodes' : 'getPathBetweenTwoNodesFromImpactedVertices',
        'SaveImpactImageData' : 'saveImageData',
        'GetSimulatorMockNodes' : 'getHardCodedResponse/impactNodeSelection',
        'GetImpactLevelForGivenNodes' : 'getImpactLevelForGivenNodes',
        'GetJsonForvisualizationReasonerGraph' : 'getJsonForvisualizationReasonerGraph',
        'GetJsonForvisualizationReasonerGraphById' : 'getJsonForvisualizationReasonerGraphById',
        'GetLineageGraphByGlossaryId' : 'getLineageGraphByGlossaryId',
        'GetLineageGraphByConceptId' : 'getLineageGraphByConceptId',
        'GetLineageGraphByDB' : 'getLineageGraphByDB',
        'AssimilatorComposingByNames' : 'aggregateComposingUsingAggregateNames',
        'SaveComposedAggregators' : 'saveComposedAggregators',
        'GetClusteredConfigurationNames' : 'getClusteredConfigurationNames',
        'GetAggregateConfigurationDetailsByNameAndType' : 'getAggregateConfigurationDetailsByNameAndType',
        'GetFullLineageGraph' : 'getFullLineageGraph',
        'GetAllLogicalViewsForAggregatorNode' : 'findlogicalViewForNode',
        'GetAlldashboardTableData' : 'getAlldashboardTableData',
        'GetAllSeriesData' : 'getAllSeriesData',
        'GetSingleSeriesData' : 'getSingleSeriesData',
        'GetDashboards' : 'getLandingDataBasedOnAuthorization',
        'GetAllHeatMapTableData' : 'getAllHeatMapTableData',
        'DownloadFileByName' : 'downloadFileByName'
    };
    SharedService.chartDataMap = {
        '0' : 'getHardCodedResponse/chartData1',
        '1' : 'getHardCodedResponse/chartData2',
        '2' : 'getHardCodedResponse/chartData3',
        '3' : 'getHardCodedResponse/chartData4'
    };
    SharedService.nodeImageMap = {
        "user" : "/ccar/ccarassets/images/user48.png",
        "usergroup" : "/ccar/ccarassets/images/users48.png",
        "datasource" : "/ccar/ccarassets/images/datasource24.png",
        "database" : "/ccar/ccarassets/images/database60.png",
        "column" : "/ccar/ccarassets/images/column24.png",
        "table" : "/ccar/ccarassets/images/table24.png",
        "concept" : "/ccar/ccarassets/images/concept.png",
        "main-entity" : "/ccar/ccarassets/images/bank.png",
        "subsidiary" : "/ccar/ccarassets/images/bank-subsidiary.png",
        "branch" : "/ccar/ccarassets/images/bank-branch.png",
    };
    SharedService.graphImageMap = {
        "report" : "/ccar/ccarassets/images/graph_report.png",
        "selected" : "/ccar/ccarassets/images/sky_dot.png",
        "data" : "/ccar/ccarassets/images/graph_data.png",
        "policy" : "/ccar/ccarassets/images/graph_policy.png",
        "model" : "/ccar/ccarassets/images/graph_model.png",
        "committee" : "/ccar/ccarassets/images/graph_committee.png",
        "information" : "/ccar/ccarassets/images/graph_info.png",
        "information_disable" : "/ccar/ccarassets/images/graph_info_disable.png",
        "person" : "/ccar/ccarassets/images/user48.png",
        "system" : "/ccar/ccarassets/images/graph_system.png",
        "default" : "/ccar/ccarassets/images/blue_dot.png"
    };

    SharedService.mappableEdges = [];
    SharedService.selEdges = [];
    SharedService.mappedEdges = [];
    // SharedService.selColumns = [];
    // SharedService.references = [];
    SharedService.dashboardData=[];
    SharedService.aggregateList = [];
    SharedService.aggregateGroupList = [];

    SharedService.pageId = 1;
    SharedService.reportId = 1;
    SharedService.dashboardMenu = {};
    SharedService.reports = [];

    SharedService.pages = [];
    SharedService.reportGraphTypes = [{id: 1, name: "column"}, {id: 2, name: "pie"}, {id: 3, name: "line"}, {id: 4, name: "stack"}, {id: 5, name: "table"}];

    SharedService.users = {
        vertices:[],
        connections:[]
    }

    SharedService.dataSources = [];

    SharedService.title = 'RDA';
    SharedService.Colors = {
        accepted : 'dashboard-accepted',
        rejected : 'dashboard-rejected',
        warning : 'dashboard-warning',
        waiting : 'dashboard-waiting',
        default : 'dashboard-default',
        OK : '#70C6E2',
        DOWN: 'red'
    };
    SharedService.primaryNav = [
        /*{"id": "1" , "label":"/ccar/ccarassets/images/alert-64.png", "title" : Constant.ALERT_TAB},
        {"id": "1" , "label":"/ccar/ccarassets/images/risk-64.png", "title" : Constant.ASSIMILATOR_TAB},*/
        {"id": "11" , "label":"/ccar/ccarassets/images/dashboard.png", "title" : Constant.DASHBOARD_TAB},
        /*{"id": "2" , "label":"/ccar/ccarassets/images/list.png", "title" : Constant.ASSOCIATOR_TAB},
        {"id": "10" , "label":"/ccar/ccarassets/images/aggregator.png", "title" : Constant.MERGE_AGGREGATOR_TAB},
        {"id": "3" , "label":"/ccar/ccarassets/images/simulator.png", "title" : Constant.SIMULATOR_TAB},
        {"id": "8" , "label":"/ccar/ccarassets/images/newsimulator.png", "title" : Constant.NEW_SIMULATOR_TAB},
        {"id": "9" , "label":"/ccar/ccarassets/images/glossary.png", "title" : Constant.GLOSSARY},
        {"id": "4" , "label":"/ccar/ccarassets/images/mono-report.png", "title" : Constant.REPORT_TAB},*/
        {"id": "5" , "label":"/ccar/ccarassets/images/database.png", "title" : Constant.DATASOURCE_TAB},
        {"id": "6" , "label":"/ccar/ccarassets/images/user48.png", "title" : Constant.TEAM_TAB},
        /*{"id": "7" , "label":"/ccar/ccarassets/images/comb_view.png", "title" : Constant.SERVICE_TAB}*/
        {"id": "9" , "label":"/ccar/ccarassets/images/glossary.png", "title" : Constant.GLOSSARY},
        {"id": "12" , "label":"/ccar/ccarassets/images/archive.png", "title" : Constant.ARCHIVE_TAB}
   ];
    SharedService.layoutGraphData = [
                                        ["Jan-13", 11],["Feb-13", 9], ["March-13", 15], ["July-13", 12]
                                    ];
    SharedService.pageBuilderTools = [
        {title:"Page", type:"1", class:"page", img:"/ccar/ccarassets/images/page.png"}
    ];
    SharedService.layoutBuilderTools = [
        {title:"2 By 2", type:"1", class:"2by2", img:"/ccar/ccarassets/images/2by2.png"},
        {title:"1 by 2 ", class:"1by2", type:"1", img:"/ccar/ccarassets/images/1by2.png"},
        {title:"2 By 1", class:"2by1", type:"1", img:"/ccar/ccarassets/images/2by1.png"},
        {title:"1 By 1", class:"1by1", type:"1", img:"/ccar/ccarassets/images/1by1.png"},
        {title:"Aggregate", class:"aggr", type:"1", img:"/ccar/ccarassets/images/aggregate.png"},
        {title:"Impact Path", class:"img", type:"1", img:"/ccar/ccarassets/images/image.png"}
    ];
    SharedService.userBuilderTools = [
        {title:"User", type:"1", class:"user", img:"/ccar/ccarassets/images/user48.png"},
        {title:"User Group ", class:"user-grp", type:"1", img:"/ccar/ccarassets/images/users48.png"}
    ];
    SharedService.cardinals = [
        {id:0, name : '1-to-1'},
        {id:1, name : '1-to-N'},
        {id:2, name : 'N-to-1'}
    ];
    SharedService.filters = [
        {id:0, name : 'IN', type: 'MULTIVALUE'},
        {id:1, name : 'NOT IN', type: 'MULTIVALUE'},
        {id:2, name : 'EQUALS', type: 'SINGLEVALUE'}
    ];

    SharedService.sleep = function(milliseconds) {
      var start = new Date().getTime();
      for (var i = 0; i < 1e7; i++) {
        if ((new Date().getTime() - start) > milliseconds){
          break;
        }
      }
    }

    SharedService.getCurrentDate = function() {
      var today = new Date();
      var dd = today.getDate();
      var mm = today.getMonth() + 1;
      var yyyy = today.getFullYear();
      return {day:dd, month:mm, year:yyyy};  
    }

    SharedService.invokeService = function( serviceId , data , method){
        $rootScope.loader = true;
        //SharedService.sleep(1000);
        var serviceUrl = this.ServiceMap[serviceId];
        var defer = $q.defer();
        if( method == undefined ){
            if( data != undefined ) {
                serviceUrl += data;
            }
            $http.get(serviceUrl).success(function(data) {
                $rootScope.loader = false;
                defer.resolve( data );
            })
            .error(function(data, status){
                $rootScope.loader = false;
                alert('Error in GET Service Call: '+serviceUrl);
            });
        } else {
            $http.post(serviceUrl , data).success(function(data) {
                $rootScope.loader = false;
                defer.resolve( data );
            })
            .error(function(data, status){
                $rootScope.loader = false;
                alert('Error in POST Service Call: '+serviceUrl);
            });
        }
        return defer.promise;
    }

    SharedService.Shapes = {
        BUSINESS_COMPONENT : 'box',
        BUSINESS_AREA : 'circle'
    };
    SharedService.ExtraInfo = {};
    SharedService.URLMap = {
        '0' : { url : 'servicedata/initialRisk.html' },
        '204' : { url : 'servicedata/node1.html' },
        '34' : { url : 'servicedata/node2.html' },
        '192' : { url : 'servicedata/node3.html' },
        '188' : { url : 'servicedata/node4.html' },
        '14' : { url : 'servicedata/node5.html' }
    };
    SharedService.convertVizToCustom = function(aggregateObj){
        var customObject = {"vertices" : [] , "connecions" : []};
        angular.forEach(aggregateObj.nodes , function(obj,key){
        	var nodeObj = {};
        	for( var name in obj )
        		nodeObj[name] = obj[name];
        	nodeObj.id = obj.id;
        	nodeObj.name = obj.label;
            customObject.vertices.push(nodeObj);
        });
        angular.forEach(aggregateObj.edges , function(obj,key){
            customObject.connecions.push({"to" : obj.to , "from" : obj.from , "relType" : obj.relType || obj.label});
        });
        return customObject;
    }

    SharedService.getAggregateRoot = function( nodeId ){
    	return SharedService.invokeService('GetAggregateRootName', {'id' : nodeId} , 'post');
    }

    SharedService.fetchEdgeView = function(edgeName){
    	return SharedService.invokeService('FetchEdgeView', {'edgeName' : edgeName} , 'post');
    }

    SharedService.saveAggregate = function(aggregateObj){
    	var tmpList = aggregateObj.baseNodeList;
        var cusObj = SharedService.convertVizToCustom(aggregateObj);
        cusObj.baseNodeList = tmpList;
        var sendObj ={name: aggregateObj.name, details: angular.toJson(cusObj)};
        return SharedService.invokeService('SaveAggregation', sendObj, 'post');
    }


    SharedService.getChartData = function( idx ){
        var chartUrl = this.chartDataMap[idx];
        var defer = $q.defer();
        $http.get(chartUrl).success(function(data){
            defer.resolve( data );
        })
        .error(function(data, status){
            alert('Error in Chart URL '+chartUrl);
        });
        return defer.promise;
    }

    SharedService.getShortName = function(name, charCount){
        var tmpName = name;
        if(!charCount)
            charCount = 8;
        if(name.length > charCount){
            tmpName = name.substring(0, charCount) + "..";
        }
        return tmpName;
    }

    SharedService.hideAllToolTips = function(){
        $('[tooltip]').tooltip('hide');
    }

    SharedService.getRandomNumber = function(from, to){
        return Math.floor(Math.random() * to) + from;
    }

    SharedService.getRandomColor = function(opacity) {
        opacity = opacity || 1;
        return "rgba(" + SharedService.getRandomNumber(50,200) + "," + SharedService.getRandomNumber(50,200) + "," + SharedService.getRandomNumber(50,200) + "," + opacity + ")";
    }

    SharedService.saveReport = function(reportObj){
    	var cusObjJson = angular.toJson(reportObj);
        var sendObj ={name: reportObj.name, details: angular.toJson(reportObj)};
        return SharedService.invokeService('SaveReport', sendObj, 'post');
    }

    SharedService.testDatasourceConn = function(datasourceCfg){
        return SharedService.invokeService('TestDatasourceConn', datasourceCfg, 'post');
    }

    SharedService.dataSourceLookUp = function(datasourceCfg){
        return SharedService.invokeService('DataSourceLookUp', datasourceCfg, 'post');
    }

    SharedService.saveDatasource = function(datasrcObj){
        var sendObj ={name: datasrcObj.name, details: angular.toJson(datasrcObj)};
        return SharedService.invokeService('SaveDataSource', sendObj, 'post');
    }

    SharedService.deleteConfiguration = function(dataSrcObjId){
        var sendObj ={ 'cfgid' : dataSrcObjId };
        return SharedService.invokeService('DeleteConfiguration', sendObj, 'post');
    }

    SharedService.removePathFromDB = function(dataSrcObjId){
        var sendObj ={ 'cfgid' : dataSrcObjId };
        return SharedService.invokeService('RemoveSavedPath', sendObj, 'post');
    }

    SharedService.saveNodeMap = function(mapArr){
        var newMapArr = [];
        angular.forEach(mapArr, function(aMap){
            newMapArr.push({name: aMap.name, details: angular.toJson(aMap.mappings)})
        });
        return SharedService.invokeService('SaveNodeMap', newMapArr, 'post');
    }

    SharedService.getAllNodeMap = function(type){
        return SharedService.invokeService('GetAllConfigurationDetails', type);
    }

    SharedService.getConfigurationList = function(type){
        return SharedService.invokeService('GetConfigurationList', type);
    }
    SharedService.getConfigurationInfo = function(id){
        return SharedService.invokeService('GetConfigurationInfo', id);
    }

    SharedService.createUser = function(user){
        return SharedService.invokeService('CreateUser', user, 'post');
    }

    SharedService.deleteUser = function(userId){
        return SharedService.invokeService('DeleteUser', userId);
    }

    SharedService.getAllUsers = function(){
        return SharedService.invokeService('GetAllUsers');
    }

    SharedService.generateReportPdf = function(reportCfg){
        return SharedService.invokeService('GenerateReportPdf', reportCfg, 'post');
    }

    SharedService.downloadReport = function(reportId){
        var serviceUrl = this.ServiceMap['DownloadReport'];
        window.open(serviceUrl + reportId);
    }

    SharedService.fetchViewRdb = function(rdbJson){
        return SharedService.invokeService('FetchViewRdb', rdbJson, 'post');
    }

    SharedService.saveDbView = function(rdbJson){
        return SharedService.invokeService('SaveDbView', rdbJson, 'post');
    }

    SharedService.saveAggregateMapping = function(dataNodes, mappableEdges, aggregateName){
        var obj = {dataNodes: dataNodes, mappableEdges: mappableEdges};
        var sendObj = {name: aggregateName, details: angular.toJson( obj )};
        return SharedService.invokeService('SaveAggregateMapping', sendObj, 'post');
    }
    SharedService.getAggregateMapping = function(name){
        return SharedService.invokeService('GetAggregateConfigurationDetailsByName', name);
    }

    SharedService.getAllConfigurationDetails = function(type){
        return SharedService.invokeService('GetAllConfigurationDetails', type);
    }

    SharedService.getDBViewsOfANode = function(name){
        return SharedService.invokeService('GetDBViewsOfANode', {'name': name}, 'post');
    }

    SharedService.GetConfigurationDetailsByName = function(name){
        return SharedService.invokeService('GetConfigurationDetailsByName', name);
    }

    SharedService.SaveLogicalViewOne = function(viewJson){
        return SharedService.invokeService('SaveLogicalViewOne', viewJson, 'post');
    }

    SharedService.GetLogicalViews = function(type){
        return SharedService.invokeService('GetAllConfigurationDetails', type);
    }

    SharedService.GetDataFromLogicalView = function(viewName){
        return SharedService.invokeService('GetDataFromLogicalView', {'viewName' : viewName} , 'post');
    }

    SharedService.deleteConfigurationDetailsByName = function(name){
        return SharedService.invokeService('DeleteConfigurationDetailsByName', name);
    }

    SharedService.getAllLogicalViewsByNodeId = function(nodeId, aggregateName){
        var sendObj = {id: nodeId, aggrName: aggregateName};
        return SharedService.invokeService('FetchAllLogicalViewsByNodeId', sendObj, 'post');
    }

    SharedService.getCombinedLogicalView = function(sendObj){
        return SharedService.invokeService('GetCombinedLogicalView', sendObj, 'post');
    }

    SharedService.deleteAggregate = function(name, deleteViews){
        return SharedService.invokeService('DeleteAggregate', { name : name, deleteViews: deleteViews } , 'post');
    }

    SharedService.saveBaseNodeConfiguration = function(name, columns){
        return SharedService.invokeService('SaveBaseNodeConfiguration', { name : name, details : angular.toJson(columns) } , 'post');
    }

    SharedService.getBaseNodesMapping = function(){
        return SharedService.invokeService('GetBaseNodesMapping');
    }

    SharedService.saveCombinedView = function(name, description, requestCfg){
        var sendObj = {name: name, details: angular.toJson( {description: description, requestCfg: requestCfg} )};
        return SharedService.invokeService('SaveCombinedView', sendObj, 'post');
    }

    SharedService.getCombinedViews = function(){
        return SharedService.invokeService('GetCombinedViews');
    }

    SharedService.saveDataNodeCfg = function(nodeName, aggrName, details){
        var sendObj = {name: nodeName + "_" + aggrName, details: angular.toJson( details )};
        return SharedService.invokeService('SaveDataNodeCfg', sendObj, 'post');
    }

    SharedService.getImpactedNodes = function(sendObj){
        return SharedService.invokeService('GetImpactedNodes', sendObj, 'post');
    }

    SharedService.getImpactLogicalViews = function(sendObj){
        return SharedService.invokeService('GetImpactLogicalViewsByNodeId', sendObj, 'post');
    }

    SharedService.getWeightedImpactGraph = function(sendObj){
        return SharedService.invokeService('GetWeightedImpactGraph', sendObj, 'post');
    }

    SharedService.mergedConnectedGraphAndWeightedGraph = function(sendObj){
        return SharedService.invokeService('MergedConnectedGraphAndWeightedGraph', sendObj, 'post');
    }

    SharedService.getAllPossiblePathsBetweenTwoNodes = function(fromNode, toNode, level, impactDirection){
        var sendObj = {'fromNode': fromNode, 'toNode': toNode, 'level': level, 'impactDirection': impactDirection};
        return SharedService.invokeService('GetAllPossiblePathsBetweenTwoNodes', sendObj, 'post');
    }

    SharedService.saveImpactImageData = function(name, imageData){
        var sendObj = {name: name, details: imageData};
        return SharedService.invokeService('SaveImpactImageData', sendObj, 'post');
    }

    SharedService.getSimulatorMockNodes = function(){
        return SharedService.invokeService('GetSimulatorMockNodes');
    }

    SharedService.getImpactLevelForGivenNodes = function(sendObj){
        return SharedService.invokeService('GetImpactLevelForGivenNodes', sendObj, 'post');
    }

    SharedService.getJsonForvisualizationReasonerGraph = function(){
    	return SharedService.invokeService('GetJsonForvisualizationReasonerGraph');
    }

    SharedService.getJsonForvisualizationReasonerGraphById = function(id){
    	var sendObj = {'ruleId': id};
    	return SharedService.invokeService('GetJsonForvisualizationReasonerGraphById', sendObj, 'post');
    }

    SharedService.getLineageGraphByGlossaryId = function(id){
        var sendObj = {'glossaryId': id};
        return SharedService.invokeService('GetLineageGraphByGlossaryId', sendObj, 'post');
    }

    SharedService.getLineageGraphByConceptId = function(id){
        var sendObj = {'conceptId': id};
        return SharedService.invokeService('GetLineageGraphByConceptId', sendObj, 'post');
    }

    SharedService.getLineageGraphByDB = function(data){
        var sendObj = data;
        return SharedService.invokeService('GetLineageGraphByDB', sendObj, 'post');
    }

    SharedService.assimilatorComposingByNames = function(names){
    	return SharedService.invokeService('AssimilatorComposingByNames', names, 'post');
    }

    SharedService.saveComposedAggregators = function(name, aggregates){
        var sendObj = {"name": name, "aggregates": aggregates};
        return SharedService.invokeService('SaveComposedAggregators', sendObj, 'post');
    }

    SharedService.getClusteredConfigurationNames = function(){
        return SharedService.invokeService('GetClusteredConfigurationNames');
    }

    SharedService.getAggrCfgDetailsByNameAndType = function(name, type){
        var sendObj = {"name": name, "type": type};
        return SharedService.invokeService('GetAggregateConfigurationDetailsByNameAndType', sendObj, 'post');
    }

    SharedService.getFullLineageGraph = function(){
        return SharedService.invokeService('GetFullLineageGraph');
    }

    SharedService.getAllLogicalViewsForAggregatorNode = function(nodeId){
        var sendObj = {"id": nodeId};
        return SharedService.invokeService('GetAllLogicalViewsForAggregatorNode', sendObj, 'post');
    }

    SharedService.getAlldashboardTableData = function(compName){
        var sendObj = {"compName": compName};
        return SharedService.invokeService('GetAlldashboardTableData', sendObj, 'post');
    }

    SharedService.getAllSeriesData = function(compName, reportType){
        var sendObj = {"compName": compName, "reportType": reportType};
        return SharedService.invokeService('GetAllSeriesData', sendObj, 'post');
    }

    SharedService.getSingleSeriesData = function(compName){
        var sendObj = {"compName": compName};
        return SharedService.invokeService('GetSingleSeriesData', sendObj, 'post');
    }

    SharedService.getDashboards = function(){
        return SharedService.invokeService('GetDashboards');
    }

    SharedService.getAllHeatMapTableData = function(param1, param2){
        var sendObj = {"param1": param1, "param2": param2};
        return SharedService.invokeService('GetAllHeatMapTableData', sendObj, 'post');
    }

    SharedService.downloadFileByName = function(name, type){
        var sendObj = {"name": name, "type": type};
        return SharedService.invokeService('DownloadFileByName', sendObj, 'post');
    }

    return SharedService;
})

.factory('AlertDashboardService', function($rootScope, $http, $q , SharedService){
    var AlertDashboardService = {};
    AlertDashboardService.loadDashboardMenu = function(){
        return SharedService.invokeService( 'LoadDashboardData' );
    };
    AlertDashboardService.loadDashboardData = function( idx ){
        return SharedService.invokeService( 'LoadDashboardData' );
    };
    return AlertDashboardService;
})

.factory('RiskAggregateService', function($rootScope, $http, $q , SharedService){
    var RiskAggregateService = {};
    RiskAggregateService.loadInitialState = function(){
        return SharedService.invokeService( 'LoadBaseNodes' );
    };
    RiskAggregateService.saveInitialRisk = function( selRiskList ){
        return SharedService.invokeService( 'SaveInitialRisk' );
    };
    RiskAggregateService.updateRiskNode = function(){
        return SharedService.invokeService( 'UpdateRiskNode' );
    };
    return RiskAggregateService;
})

.factory('graphService', function($rootScope, $http, $q , SharedService){
    var graphService = {};
    graphService.transfromToVisFmt = function ( graphData, isDisabled ){
        var nodes = new vis.DataSet();
        var edges = new vis.DataSet();
        angular.forEach( graphData.vertices , function( obj , key ){
            var bkColor = SharedService.Colors[obj.status] || '#FFDB71';
            var node = obj;
            node.isDisabled = isDisabled;
            node.x = Math.floor((Math.random() * 500) + 1);
            node.y = Math.floor((Math.random() * 500) + 1);
            node.shape = SharedService.Shapes[obj.type] || 'dot';
            node.color = {background: bkColor};
            node.allowedToMoveX = true;
            node.allowedToMoveY = true;
            node.label = obj.name;
            node.radius = 20;
            if( obj.selected ) {
                node.color = {background:'#70C6E2'};
            }
            var isExist = _.findWhere(nodes._data, {id: node.id});
            if(!isExist)
                nodes.add( node ) ;
            SharedService.ExtraInfo[obj.id] = { url : obj.url};
        });
        angular.forEach( graphData.connecions , function( obj , key ){
            var bkColor = SharedService.Colors[obj.weight] || 'green';
            var con =  obj;
            con.color = isDisabled?'#ddd':bkColor;
            con.fontSize = 9;
            con.label = obj.relType;
            con.style = isDisabled?'dash-line':'arrow';
            edges.add( con );
        });
        return { nodes : nodes , edges : edges };
    }

    /*graphService.addSubGraph*/

    graphService.getGraphData = function( idx ){
        var defer = $q.defer();
        SharedService.invokeService( 'LoadChildNodes' , idx).then( function( data ){
            graphData = data;
            var visData = graphService.transfromToVisFmt( graphData );
            defer.resolve({visData : visData , gridData : graphData.statistics });
        });
        return defer.promise;
    }

    graphService.nodeImageSetter = function( obj ){
        obj.containsValue = obj.containsValue || 'false';
        if( obj.isReport )
            obj.image = Constant.IMPACT.REPORT;
        else if( obj.isPerson )
            obj.image = Constant.IMPACT.PERSON;
        else if( obj.isModel )
            obj.image = Constant.IMPACT.MODEL;
        else if( obj.isPolicy )
            obj.image = Constant.IMPACT.POLICY;
        else if( obj.isSystem )
            obj.image = Constant.IMPACT.SYSTEM;
        else if( obj.isCommittee )
            obj.image = Constant.IMPACT.COMMITTEE;
        else if( obj.isInformation )
            obj.image = Constant.IMPACT.INFORMATION;
        else if( obj.containsValue == 'true' )
            obj.image = Constant.IMPACT.DATA;
        else if( obj.isDisabled )
            obj.image = Constant.IMPACT.DISABLE;
        else
            obj.image = Constant.IMPACT.DEFAULT;
    }

    graphService.levelImageSetter = function( obj ){
        obj.containsValue = obj.containsValue || 'false';
        if( obj.isReport ){
            if(obj.isDisabled){
                obj.image = getImagePath(Constant.IMPACT.REPORT, "disable");return;
            }
            obj.image = getImagePath(Constant.IMPACT.REPORT, obj.level);
        }
        else if( obj.isPerson ){
            if(obj.isDisabled){
                obj.image = getImagePath(Constant.IMPACT.PERSON, "disable");return;
            }
            obj.image = getImagePath(Constant.IMPACT.PERSON, obj.level);
        }
        else if( obj.isModel ){
            if(obj.isDisabled){
                obj.image = getImagePath(Constant.IMPACT.MODEL, "disable");return;
            }
            obj.image = getImagePath(Constant.IMPACT.MODEL, obj.level);
        }
        else if( obj.isPolicy ){
            if(obj.isDisabled){
                obj.image = getImagePath(Constant.IMPACT.POLICY, "disable");return;
            }
            obj.image = getImagePath(Constant.IMPACT.POLICY, obj.level);
        }
        else if( obj.isSystem ){
            if(obj.isDisabled){
                obj.image = getImagePath(Constant.IMPACT.SYSTEM, "disable");return;
            }
            obj.image = getImagePath(Constant.IMPACT.SYSTEM, obj.level);
        }
        else if( obj.isCommittee ){
            if(obj.isDisabled){
                obj.image = getImagePath(Constant.IMPACT.COMMITTEE, "disable");return;
            }
            obj.image = getImagePath(Constant.IMPACT.COMMITTEE, obj.level);
        }
        else if( obj.isInformation ){
            if(obj.isDisabled){
                obj.image = Constant.IMPACT.INFORMATION_DISABLE;return;
            }
            obj.image = Constant.IMPACT.INFORMATION;
        }
        else if( obj.containsValue == 'true' ){
            if(obj.isDisabled){
                obj.image = getImagePath(Constant.IMPACT.DATA, "disable");return;
            }
            obj.image = getImagePath(Constant.IMPACT.DATA, obj.level);
        }
        else{
            if(obj.isDisabled){
                obj.image = getImagePath(Constant.IMPACT.DEFAULT, "disable");return;
            }
            if(obj.impactDirection == 1){
                obj.image = getImagePath(Constant.IMPACT.DEFAULT_UP, obj.level);return;
            }
            if(obj.impactDirection == -1){
                obj.image = getImagePath(Constant.IMPACT.DEFAULT_DOWN, obj.level);return;
            }
            obj.image = getImagePath(Constant.IMPACT.DEFAULT, obj.level);
        }
    }

    graphService.disableEdges = function (edges) {
        angular.forEach(edges, function (e) {
            e.color = '#ddd';
            e.style = 'dash-line';
        });
    }

    graphService.enableEdges = function (edges) {
        angular.forEach(edges, function (e) {
            e.color = 'green';
            e.style = 'arrow';
        });
    }

    function getImagePath(type, level){
        return Constant.IMPACT_IMG_PATH + type + "/" + level + ".png";
    }

    return graphService;
})

.factory('MockService', function($rootScope, $http, $q){
    var MockService = {};

    if(!MockService.pageIdx){
        MockService.pageIdx = 1;
    }

    MockService.dashboardTable = {
        "columns" : ["**Schedule", "Source", "Materiality", "Point Person", "# CDE", "# of Process / Data Gaps", "Identity Data", "Collect Data", "Link Data / Index", "Identity Gaps", "Tech Load to Report", "Validation results / Edit Checks", "Gap Remediation / Iterative Return+", "Final disclouser / Sign-off / Stage for Subbmission"],
        "rows" : [
            [
                {"name":"Schedule A8, A9: Retail", "color":"white"},
                {"name":"CCRR, RA, RO", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Frank G/Dmitry G", "color":"white"},
                {"name":"22", "color":"white"},
                {"name":"4", "color":"white"},
                {"name":"10/9", "color":"blue"},
                {"name":"10/15", "color":"blue"},
                {"name":"10/26", "color":"blue"},
                {"name":"11/2", "color":"blue"},
                {"name":"11/30", "color":"green"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"yellow"},
                {"name":"12/18", "color":"yellow"}
            ],
            [
                {"name":"Schedule B1, 2: Securities", "color":"white"},
                {"name":"Clearwater, Blackrock", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Damian Raitemeyer", "color":"white"},
                {"name":"22", "color":"white"},
                {"name":"5", "color":"white"},
                {"name":"10/16", "color":"blue"},
                {"name":"11/9", "color":"green"},
                {"name":"n/a", "color":"white"},
                {"name":"11/30", "color":"green"},
                {"name":"11/30", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"green"},
                {"name":"12/18", "color":"green"}
            ],
            [
                {"name":"Schedule H1: Wholesale Risk", "color":"white"},
                {"name":"CCRR, RA, RO", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Frank G/Dmitry G", "color":"white"},
                {"name":"154", "color":"white"},
                {"name":"36", "color":"white"},
                {"name":"10/9", "color":"blue"},
                {"name":"10/15", "color":"blue"},
                {"name":"10/26", "color":"blue"},
                {"name":"11/2", "color":"blue"},
                {"name":"11/30", "color":"green"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"yellow"},
                {"name":"12/18", "color":"yellow"}
            ],
            [
                {"name":"CR/t", "color":"white"},
                {"name":"", "color":"white"},
                {"name":"", "color":"white"},
                {"name":"", "color":"white"},
                {"name":"", "color":"white"},
                {"name":"", "color":"white"},
                {"name":"10/9", "color":"blue"},
                {"name":"10/15", "color":"blue"},
                {"name":"10/26", "color":"blue"},
                {"name":"11/30", "color":"green"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"white"},
                {"name":"12/18", "color":"yellow"},
                {"name":"12/18", "color":"yellow"}
            ],
            [
                {"name":"Schedule J: Fair Value Option / Held for Sale", "color":"white"},
                {"name":"Calculation A,H,K", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Frank G/Dmitry G", "color":"white"},
                {"name":"13", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"10/16", "color":"blue"},
                {"name":"11/9", "color":"green"},
                {"name":"n/a", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"yellow"},
                {"name":"12/18", "color":"yellow"}
            ],
            [
                {"name":"Schedule K: Supplemental", "color":"white"},
                {"name":"CCRR, RA, RO", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Frank G/Dmitry G", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"10/9", "color":"blue"},
                {"name":"10/15", "color":"blue"},
                {"name":"10/26", "color":"blue"},
                {"name":"11/2", "color":"blue"},
                {"name":"11/30", "color":"green"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"white"},
                {"name":"12/18", "color":"white"}
            ],
            [
                {"name":"Schedule: MSR Valuation", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Damian Raitemeyer", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"11/2", "color":"blue"},
                {"name":"11/9", "color":"yellow"},
                {"name":"n/a", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"white"},
                {"name":"12/18", "color":"white"}
            ],
            [
                {"name":"Schedule M: balances", "color":"white"},
                {"name":"Calculation A,H,K", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Frank G/Dmitry G", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"10/9", "color":"blue"},
                {"name":"10/15", "color":"blue"},
                {"name":"10/26", "color":"blue"},
                {"name":"n/a", "color":"white"},
                {"name":"11/30", "color":"green"},
                {"name":"12/18", "color":"white"},
                {"name":"12/18", "color":"white"},
                {"name":"12/18", "color":"white"}
            ],
            [
                {"name":"Schedule C1-3: Regulatory Capital Instruments", "color":"white"},
                {"name":"FinArch", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Peggy Huang", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"11/4", "color":"blue"},
                {"name":"11/20", "color":"green"},
                {"name":"n/a", "color":"white"},
                {"name":"11/30", "color":"green"},
                {"name":"11/30", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"white"},
                {"name":"12/18", "color":"white"}
            ],
            [
                {"name":"Schedule D1, 2,4,5,6: Regulatory Capital Transactions", "color":"white"},
                {"name":"FinArch", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Peggy Huang", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"11/4", "color":"blue"},
                {"name":"11/20", "color":"green"},
                {"name":"n/a", "color":"white"},
                {"name":"11/30", "color":"green"},
                {"name":"11/30", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"white"},
                {"name":"12/18", "color":"white"}
            ],
            [
                {"name":"Schedule E1-4: Operational Risk", "color":"white"},
                {"name":"Bwise", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Prasad Kodali", "color":"white"},
                {"name":"18", "color":"white"},
                {"name":"1", "color":"white"},
                {"name":"10/16", "color":"blue"},
                {"name":"11/20", "color":"green"},
                {"name":"n/a", "color":"white"},
                {"name":"11/30", "color":"green"},
                {"name":"11/30", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"green"},
                {"name":"12/18", "color":"green"}
            ],
            [
                {"name":"Schedule E5: Legal", "color":"white"},
                {"name":"Bwise", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Prasad Kodali", "color":"white"},
                {"name":"18", "color":"white"},
                {"name":"1", "color":"white"},
                {"name":"10/16", "color":"blue"},
                {"name":"11/20", "color":"green"},
                {"name":"n/a", "color":"white"},
                {"name":"11/30", "color":"green"},
                {"name":"11/30", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"white"},
                {"name":"12/18", "color":"white"}
            ],
            [
                {"name":"Schedule G1-3: PPNR", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"High", "color":"white"},
                {"name":"Damian Raitemeyer", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"TBD", "color":"white"},
                {"name":"11/2", "color":"red"},
                {"name":"11/9", "color":"red"},
                {"name":"11/27", "color":"red"},
                {"name":"11/30", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"11/30", "color":"white"},
                {"name":"12/18", "color":"white"},
                {"name":"12/18", "color":"white"}
            ]
        ]
    };

    MockService.glossaryData = {"glossarySets":{"set-1":[{"name":"Lines Of Business","id":1},{"name":"TotalAsset_ByCS_ByIT","id":2},{"name":"Sys_AccReceivable","id":3},{"name":"CIT_Trk_Seg_PD_B","id":4}]},"rules":[{"name":"CCAR1","id":1},{"name":"CCAR2","id":2}],"graphData":{"vertices":[{"color":"green","level":0,"name":"1","id":1},{"color":"green","level":0,"name":"2","id":2},{"color":"green","level":0,"name":"3","id":3},{"color":"green","level":0,"name":"4","id":4}]},"db":[{"dbname":"DB1","column":"Total Asset","table":"Asset"}]};

    MockService.DataSources = [
        {id:"d1", name:"dataSource1", type:"xls", connectionstr: "conn/abc1", userid: "xyz1", password: "12345"},
        {id:"d2", name:"dataSource2", type:"db", connectionstr: "conn/abc2", userid: "xyz2", password: "56789"}
    ]

    MockService.DataSourceTypes = [
        {id: "dt2", name: "db"},
        {id: "dt1", name: "excel"}
    ];

    MockService.datasrcDrivers = [
        {id: "dd2", name: "com.mysql.jdbc.Driver"},
        {id: "dd3", name: "com.denodo.vdp.jdbc.Driver"},
        {id: "dd4", name: "org.postgresql.Driver"},
        {id: "dd5", name: "oracle.jdbc.driver.OracleDriver"},
        {id: "dd6", name: "com.microsoft.sqlserver.jdbc.SQLServerDriver"}
    ];

    MockService.selEdges = [{name: "A-B", selTables: [{name: "test1", attributes: [{name: "col1"}, {name: "col2"}, {name: "col3"}]}, {name: "test2", attributes: [{name: "col1"}, {name: "col2"}]}]}];

    MockService.simulateOptions = [{id:1, name:"Without Data"}, {id:2, name:"With Data"}];

    MockService.StatusData = {'series': [
                        {
                            name: 'Not Started',
                            data: [ 20, 30, 15, 40, 40, 10, 50, 20, 60, 48, 68, 28, 26 ]
                        },
                        {
                            name: 'Gaps Identified',
                            data: [ 30, 40, 25, 20, 30, 40, 35, 30, 30, 22, 12, 12, 44 ]
                        },
                        {
                            name: 'Complete',
                            data: [ 50, 30, 60, 40, 30, 50, 15, 50, 10, 30, 20, 60, 30 ]
                        }
                    ],

                'categories':[
                            'Schedule A',
                            'Schedule B',
                            'Schedule C',
                            'Schedule D',
                            'Schedule E',
                            'Schedule F',
                            'Schedule G',
                            'Schedule H',
                            'Schedule I',
                            'Schedule J',
                            'Schedule K',
                            'Schedule L',
                            'Schedule M'
                        ]
                };


    MockService.ScheduleData = [
        {
            'name': "DE Completion (By outstanding balance in Bn USD)",
            'graphData':{
                'series': [
                    {
                        name: '100% Completion',
                        data: [ 20, 37, 16 ]
                    },
                    {
                        name: '< 75% Completion',
                        data: [ 30, 67, 56 ]
                    },
                    {
                        name: '< 50% Completion',
                        data: [ 60, 17, 26]
                    },
                    {
                        name: '< 20% Completion',
                        data: [ 60, 17, 26]
                    }
                ],
                'categories': [
                    'Corporate Finance',
                    'Equipment Finance',
                    'Commercial service'
                ]
            },
            'tableData':{
                'columns': ['PD Grade', '100% Completion', '< 75% Completion', '< 50% Completion', '< 20% Completion'],
                'rows':[
                    ['11', '4.0', '2.4', '8.7', '2.4'],
                    ['11', '1.0', '6.0', '2.3', '4.2'],
                    ['11', '0.4', '5.6', '9.0', '8.4'],
                    ['11', '5.2', '8.0', '5.5', '10.0'],
                    ['11', '9.4', '10.0', '3.0', '6.8'],
                    ['11', '7.4', '7.3', '10.5', '3.8'],
                    ['11', '2.1', '3.4', '11', '9.0'],
                    ['Total', '28.5', '41.0', '50.0', '44.8']
                ]
            }
        },
        { 
            'name': "DE Completion (By # of obligors)",
            'graphData':{
                'series': [
                    {
                        name: '100% Completion',
                        data: [ 50, 27, 16 ]
                    },
                    {
                        name: '< 75% Completion',
                        data: [ 20, 37, 36 ]
                    },
                    {
                        name: '< 50% Completion',
                        data: [ 10, 17, 16]
                    },
                    {
                        name: '< 20% Completion',
                        data: [ 20, 17, 56]
                    }
                ],
                'categories':[
                    'Corporate Finance',
                    'Equipment Finance',
                    'Commercial service'
                ]
            },
                    
            'tableData':{
                'columns': ['PD Grade', '100% Completion', '< 75% Completion', '< 50% Completion', '< 20% Completion'],
                'rows':[
                    ['12', '4.0', '2.4', '8.7', '2.4'],
                    ['12', '1.0', '6.0', '2.3', '4.2'],
                    ['12', '0.4', '5.6', '9.0', '8.4'],
                    ['12', '5.2', '8.0', '5.5', '10.0'],
                    ['12', '9.4', '10.0', '3.0', '6.8'],
                    ['12', '7.4', '7.3', '10.5', '3.8'],
                    ['12', '2.1', '3.4', '11', '9.0'],
                    ['Total', '28.5', '41.0', '50.0', '44.8']
                ]
            }
        }
    ];
                                            

    MockService.GapData = {
        'columns': ['FRB Field#', 'Field Name', 'Status', 'Remediation/Disclosure'],
        'rows':[
            ['1', 'Customer Id 1', 'Transformation required 1', ''],
            ['10', 'Obligor Internal Risk Rating 1', 'Complex transformation required 1', ''],
            ['11', 'TIN 1', 'Complex transformation required 1', ''],
            ['12', 'Stock Exchange 1', 'Transformation required 1', ''],
            ['13', 'Ticker Symbol 1', 'Transformation required 1', '']
        ]
    };

    MockService.dashboardTemplates = [
        {
            "name":"Finance & Regulatory Dashboard",
            "type":Constant.DASHBOARD.REGULATORY
        },
        {
            "name":"Enterprise Data Management Dashboard",
            "type":Constant.DASHBOARD.ENTERPRISE
        }
    ];

    MockService.liquidityProfile = {
        nodes: [
            {id: 1, name: "Bank", type: "main-entity", level: 1},
            {id: 2, name: "Subsidiary-1", type: "subsidiary", level: 2},
            {id: 3, name: "Subsidiary-2", type: "subsidiary", level: 2},
            {id: 4, name: "Branch-1", type: "branch", level: 3},
            {id: 5, name: "Branch-2", type: "branch", level: 3},
            {id: 6, name: "Branch-3", type: "branch", level: 3},
            {id: 7, name: "Branch-4", type: "branch", level: 3},
            {id: 8, name: "Branch-5", type: "branch", level: 3}
        ],
        edges: [
            {from: 1, to: 2},
            {from: 1, to: 3},
            {from: 2, to: 4},
            {from: 2, to: 5},
            {from: 3, to: 6},
            {from: 3, to: 7},
            {from: 3, to: 8}
        ]
    }

    return MockService;
})

.factory('VisDataSet', function () {
    'use strict';
    return function (data, options) {
        // Create the new dataSets
        return new vis.DataSet(data, options);
    };
})

.factory('NgAnimateService', function($rootScope, $timeout, $q) {
    var NgAnimateService = {};

        NgAnimateService.animations = [
            "toggle",
            "spin-toggle",
            "scale-fade",
            "scale-fade-in",
            "bouncy-scale-in",
            "flip-in",
            "slide-left",
            "slide-right",
            "slide-top",
            "slide-down",
            "bouncy-slide-left",
            "bouncy-slide-right",
            "bouncy-slide-top",
            "bouncy-slide-down",
            "rotate-in"
        ];

        NgAnimateService.getRandomAnimation = function () {
            var _this = this;
            var idx = _.random(0, _this.animations.length-1);
            return this.animations[idx];
        }

        NgAnimateService.lazyLoadItems = function (container, items) {
            angular.forEach(items, function (item, i) {
                $timeout(function () {
                    container.push(item);
                }, 100 * i);
            });
        }

    return NgAnimateService;
});
