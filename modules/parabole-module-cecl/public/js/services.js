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
        'GetJsonForAllLinege' : 'getJsonForAllLinege',
        'GetJsonForAllLinegeAgainstNodeId' : 'getJsonForAllLinegeAgainstNodeId',
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
        'GetJsonForAllLinegeAgainstNodeIdwithAdaptiveLearning' : 'getJsonForAllLinegeAgainstNodeIdwithAdaptiveLearning',
        'GetJsonForAllLinegewithAdaptiveLearning' : 'getJsonForAllLinegewithAdaptiveLearning',
        'GetLineageDbData' : 'getLineageDbData',
        'GetFilteredDataByCompName' : 'getFilteredDataByCompName',
        'GetMultiFilteredDataByCompName' : 'getMultiFilteredDataByCompName',
        'GetFunctionalAreasByProducts' : 'getFunctionalAreasByProducts',
        'GetGraphByConceptUri' : 'getRelatedVerticesByUri',
        'GetDescriptionByUri' : 'getDescriptionByUri',
        'GetParagraphsByConcept' : 'getParagraphsByConcept',
        'GetParagraphsBySubsection' : 'getParagraphsBySubsection',
        'StartContentParser' : 'startContentParser',
        'StartOntologyParser' : 'startOntologyParser',
        'AddChecklist' : 'addChecklist',
        'GetChecklistByConceptAndComponent' : 'getChecklistByConceptAndComponent',
        'GetChecklistByNode' : 'getChecklistByNode',
        'GetChecklistByParagraphId' : 'getChecklistByParagraphId',
        'GetChecklistByMultiParagraphId' : 'getChecklistByMultiParagraphId',
        'AddAnswer' : 'addAnswer',
        'SaveParagraphTags' : 'saveParagraphTags',
        'GetParagraphTags' : 'getParagraphTags'
    };
    SharedService.chartDataMap = {
        '0' : 'getHardCodedResponse/chartData1',
        '1' : 'getHardCodedResponse/chartData2',
        '2' : 'getHardCodedResponse/chartData3',
        '3' : 'getHardCodedResponse/chartData4'
    };
    SharedService.nodeImageMap = {
        "user" : "ceclassets/images/user48.png",
        "usergroup" : "ceclassets/images/users48.png",
        "datasource" : "ceclassets/images/datasource24.png",
        "db" : "ceclassets/images/db.png",
        "glossary" : "ceclassets/images/glossary24.png",
        "rule" : "ceclassets/images/rule.png",
        "database" : "ceclassets/images/database60.png",
        "column" : "ceclassets/images/column24.png",
        "table" : "ceclassets/images/table24.png",
        "concept" : "ceclassets/images/concept.png",
        "selectedRule" : "ceclassets/images/selectedRule.png",
        "selectedConcept" : "ceclassets/images/selectedConcept.png",
        "selectedDb" : "ceclassets/images/selectedDb.png",
        "selectedGlossary" : "ceclassets/images/selectedGlossary.png",
        "selectedUser" : "ceclassets/images/selectedUser.png",
        "unSelectedRule" : "ceclassets/images/unSelectedRule.png",
        "unSelectedConcept" : "ceclassets/images/unSelectedConcept.png",
        "unSelectedDb" : "ceclassets/images/unSelectedDb.png",
        "unSelectedGlossary" : "ceclassets/images/unSelectedGlossary.png",
        "unSelectedUser" : "ceclassets/images/unSelectedUser.png"
    };
    SharedService.graphImageMap = {
        "report" : "ceclassets/images/graph_report.png",
        "selected" : "ceclassets/images/sky_dot.png",
        "data" : "ceclassets/images/graph_data.png",
        "policy" : "ceclassets/images/graph_policy.png",
        "model" : "ceclassets/images/graph_model.png",
        "committee" : "ceclassets/images/graph_committee.png",
        "information" : "ceclassets/images/graph_info.png",
        "information_disable" : "ceclassets/images/graph_info_disable.png",
        "person" : "ceclassets/images/user48.png",
        "system" : "ceclassets/images/graph_system.png",
        "default" : "ceclassets/images/blue_dot.png",
        "concept" : "ceclassets/images/concept.png",
        "related concept" : "ceclassets/images/concept.png",
        "paragraph" : "ceclassets/images/graph_paragraph.png"
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
        {"id": "1" , "label":"ceclassets/images/document-browser.png", "title" : Constant.DOCUMENT_BROWSER_TAB},
        {"id": "2" , "label":"ceclassets/images/impact.png", "title" : Constant.IMPACT_TAB},
        {"id": "3" , "label":"ceclassets/images/regulation.png", "title" : Constant.REGULATION_TAB}
   ];
    SharedService.layoutGraphData = [
                                        ["Jan-13", 11],["Feb-13", 9], ["March-13", 15], ["July-13", 12]
                                    ];
    SharedService.pageBuilderTools = [
        {title:"Page", type:"1", class:"page", img:"rdaassets/images/page.png"}
    ];
    SharedService.layoutBuilderTools = [
        {title:"2 By 2", type:"1", class:"2by2", img:"rdaassets/images/2by2.png"},
        {title:"1 by 2 ", class:"1by2", type:"1", img:"rdaassets/images/1by2.png"},
        {title:"2 By 1", class:"2by1", type:"1", img:"rdaassets/images/2by1.png"},
        {title:"1 By 1", class:"1by1", type:"1", img:"rdaassets/images/1by1.png"},
        {title:"Aggregate", class:"aggr", type:"1", img:"rdaassets/images/aggregate.png"},
        {title:"Impact Path", class:"img", type:"1", img:"rdaassets/images/image.png"}
    ];
    SharedService.userBuilderTools = [
        {title:"User", type:"1", class:"user", img:"rdaassets/images/user48.png"},
        {title:"User Group ", class:"user-grp", type:"1", img:"rdaassets/images/users48.png"}
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

    SharedService.getJsonForAllLinege = function(name){
        var sendObj = {'inputName': name};
        return SharedService.invokeService('GetJsonForAllLinege', sendObj, 'post');
    }

    SharedService.getJsonForAllLinegeAgainstNodeId = function(name, node){
        var sendObj = {'inputName': name, 'node': node };
        return SharedService.invokeService('GetJsonForAllLinegeAgainstNodeId', sendObj, 'post');
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

    SharedService.getLineageDbData = function(sendObj){
        return SharedService.invokeService('GetLineageDbData', sendObj, 'post');
    }

    SharedService.getJsonForAllLinegewithAdaptiveLearning  = function(inData, filename){
        var sendObj = {"data": inData};
        return SharedService.invokeService('GetJsonForAllLinegewithAdaptiveLearning', sendObj, 'post');
    }
    
    SharedService.getJsonForAllLinegeAgainstNodeIdwithAdaptiveLearning = function(inData){
        var sendObj = {"data": inData};
        return SharedService.invokeService('GetJsonForAllLinegeAgainstNodeIdwithAdaptiveLearning', sendObj, 'post');
    }

    SharedService.getFilteredDataByCompName = function(compName, filterStr){
        var sendObj = {"compName": compName, "filterStr": filterStr};
        return SharedService.invokeService('GetFilteredDataByCompName', sendObj, 'post');
    }

    SharedService.getMultiFilteredDataByCompName = function(compName, filters){
        var sendObj = {"compName": compName, "filters": filters};
        return SharedService.invokeService('GetMultiFilteredDataByCompName', sendObj, 'post');
    }

    SharedService.getFunctionalAreasByProducts = function(compName, products){
        var sendObj = {"compName": compName, "products": products};
        return SharedService.invokeService('GetFunctionalAreasByProducts', sendObj, 'post');
    }

    SharedService.getGraphByConceptUri = function(uri){
        var sendObj = {"uri": uri};
        return SharedService.invokeService('GetGraphByConceptUri', sendObj, 'post');
    }

    SharedService.getDescriptionByUri = function (uri) {
        var sendObj = {"uriStr": uri};
        return SharedService.invokeService('GetDescriptionByUri', sendObj, 'post');
    }

    SharedService.startContentParser = function () {
        return SharedService.invokeService('StartContentParser');
    }

    SharedService.startOntologyParser = function () {
        return SharedService.invokeService('StartOntologyParser');
    }

    SharedService.getParagraphsByConcept = function (concept) {
        var sendObj = {"concept": concept};
        return SharedService.invokeService('GetParagraphsByConcept', sendObj, 'post');
    }

    SharedService.getParagraphsBySubsection = function (subSectionId) {
        var sendObj = {"subSectionId": subSectionId};
        return SharedService.invokeService('GetParagraphsBySubsection', sendObj, 'post');
    }

    SharedService.addChecklist = function (checkList) {
        var sendObj = {"checkList": checkList};
        return SharedService.invokeService('AddChecklist', sendObj, 'post');
    }

    SharedService.getChecklistByConceptAndComponent = function (conceptName, componentType, componentName) {
        var sendObj = {"conceptName": conceptName, "componentType": componentType, "componentName": componentName};
        return SharedService.invokeService('GetChecklistByConceptAndComponent', sendObj, 'post');
    }

    SharedService.getChecklistByNode = function (nodeType, nodeName) {
        var sendObj = {"nodeType": nodeType, "nodeName": nodeName};
        return SharedService.invokeService('GetChecklistByNode', sendObj, 'post');
    }

    SharedService.getChecklistByParagraphId = function (paragraphId) {
        var sendObj = {"paragraphId": paragraphId};
        return SharedService.invokeService('GetChecklistByParagraphId', sendObj, 'post');
    }

    SharedService.addAnswer = function (answers) {
        var sendObj = {"answers": answers};
        return SharedService.invokeService('AddAnswer', sendObj, 'post');
    }

    SharedService.getChecklistByMultiParagraphId = function (paragraphIds) {
        var sendObj = {"paragraphIds": paragraphIds};
        return SharedService.invokeService('getChecklistByMultiParagraphId', sendObj, 'post');
    }

    SharedService.saveParagraphTags = function (paraTags) {
        var sendObj = {"paraTags": paraTags};
        return SharedService.invokeService('SaveParagraphTags', sendObj, 'post');
    }

    SharedService.saveParagraphTags = function (paraTags) {
        var sendObj = {"paraTags": paraTags};
        return SharedService.invokeService('SaveParagraphTags', sendObj, 'post');
    }

    SharedService.getParagraphTags = function (paraIds) {
        var sendObj = {"paraIds": paraIds};
        return SharedService.invokeService('GetParagraphTags', sendObj, 'post');
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

    graphService.getRelatedNodes = function( idx ){
        var defer = $q.defer();
        SharedService.invokeService( 'LoadChildNodes' , idx).then( function( data ){
            defer.resolve(data);
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

    MockService.glossaryInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

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

    MockService.datasrcData = [
        {
            'name' : 'Database1',
            'type' : 'database',
            'children' : [
               {
                    'name' : 'Table1',
                    'type' : 'table',
                    'children' : [
                        {
                            'name' : 'Column1',
                            'type' : 'column'
                        },
                        {
                            'name' : 'Column2',
                            'type' : 'column'
                        },
                        {
                            'name' : 'Column3',
                            'type' : 'column'
                        }
                    ]
               },
               {
                    'name' : 'Table2',
                    'type' : 'table'
                }
             ]
        },
        {
            'name' : 'Database2',
            'type' : 'database',
            'children' : [
               {
                    'name' : 'Table1',
                    'type' : 'table'
               },
               {
                    'name' : 'Table2',
                    'type' : 'table'
               }
             ]
        }
    ];

    MockService.selEdges = [{name: "A-B", selTables: [{name: "test1", attributes: [{name: "col1"}, {name: "col2"}, {name: "col3"}]}, {name: "test2", attributes: [{name: "col1"}, {name: "col2"}]}]}];

    MockService.mockImpactData = {
        "connecions":[
             {
                "to":278,
                "relType":"has",
                "from":356
             },
             {
                "to":118,
                "relType":"isInputTo",
                "from":278
             }
        ],
        "vertices":[
             {
                "baseNode":"true",
                "id":356,
                "level":0,
                "name":"Portfolio",
                "isABusinessComponent":"true"
             },
             {
                "id":278,
                "level":1,
                "name":"ExposureAtDefault"
             },
             {
                "id":118,
                "level":2,
                "name":"CreditRWAModel",
                "isModel":"true"
             }
        ]
    };

    MockService.simulateOptions = [{id:1, name:"Without Data"}, {id:2, name:"With Data"}];

  /*  MockService.rules = [
        {
            id: 1,
            name: "Rule1",
            knowledgeData: {
                vertices : [
                    {
                        "id":356,
                        "level":0,
                        "name":"Portfolio",
                        "color":"red"
                    },
                    {
                        "id":239,
                        "level":1,
                        "name":"Report",
                        "color":"green"
                    },
                    {
                        "id":211,
                        "level":0,
                        "name":"Person",
                        "color":"red"
                    },
                    {
                        "id":115,
                        "level":1,
                        "name":"customer",
                        "color":"green"
                    },
                    {
                        "id":110,
                        "level":0,
                        "name":"Member",
                        "color":"green"
                    }
                ]
            },
            glossaryData: {
                "Set-1" : ["s11", "s14", "s16"],
                "Set-2" : ["s22", "s25"],
                "Set-3" : ["s31", "s33"]
            }
        },
        {
            id: 2,
            name: "Rule2",
            knowledgeData: {
                vertices : [
                    {
                        "id":111,
                        "level":0,
                        "name":"Product",
                        "color":"red"
                    },
                    {
                        "id":123,
                        "level":1,
                        "name":"Facility",
                        "color":"green"
                    },
                    {
                        "id":211,
                        "level":0,
                        "name":"Person",
                        "color":"red"
                    },
                    {
                        "id":412,
                        "level":2,
                        "name":"System",
                        "color":"green"
                    },
                    {
                        "id":110,
                        "level":0,
                        "name":"Member",
                        "color":"red"
                    }
                ]
            },
            glossaryData: {
                "Set-1" : ["s11", "s12"],
                "Set-2" : ["s22", "s24", "s25"],
                "Set-3" : ["s31", "s32", "s33", "s34"]
            }
        }
    ];*/

  /*  MockService.glossarySets = {
        "Set-1" : [
            {id: "s11", name: "glossary-1"},
            {id: "s12", name: "glossary-2"},
            {id: "s13", name: "glossary-3"},
            {id: "s14", name: "glossary-4"},
            {id: "s15", name: "glossary-5"},
            {id: "s16", name: "glossary-6"}
        ],
        "Set-2" : [
            {id: "s21", name: "glossary-1"},
            {id: "s22", name: "glossary-2"},
            {id: "s23", name: "glossary-3"},
            {id: "s24", name: "glossary-4"},
            {id: "s25", name: "glossary-5"},
            {id: "s26", name: "glossary-6"}
        ],
        "Set-3" : [
            {id: "s31", name: "glossary-1"},
            {id: "s32", name: "glossary-2"},
            {id: "s33", name: "glossary-3"},
            {id: "s34", name: "glossary-4"},
            {id: "s35", name: "glossary-5"},
            {id: "s36", name: "glossary-6"}
        ]
    };*/

/*    MockService.graphData = {
        vertices : [
            {
                "id":356,
                "level":0,
                "name":"Portfolio",
                "color":"green"
            },
            {
                "id":239,
                "level":1,
                "name":"Report",
                "color":"green"
            },
            {
                "id":211,
                "level":0,
                "name":"Person",
                "color":"green"
            },
            {
                "id":115,
                "level":1,
                "name":"customer",
                "color":"green"
            },
            {
                "id":110,
                "level":0,
                "name":"Member",
                "color":"red"
            },
            {
                "id":111,
                "level":0,
                "name":"Product",
                "color":"green"
            },
            {
                "id":123,
                "level":1,
                "name":"Facility",
                "color":"red"
            },
            {
                "id":412,
                "level":2,
                "name":"System",
                "color":"red"
            }
        ]
    };*/

    MockService.TimelineData = [
                                {id: 1, content: 'Current Status', start: "10.20.2015"},
                                {id: 2, content: 'Initial Submission', start: "10.25.2015"},
                                {id: 3, content: 'Dry Run', start: "10.31.2015"},
                                {id: 4, content: 'Final Submission', start: "11.8.2015"}
                            ];

    MockService.StatusData = {'series': {
                    1:[
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
                    2:[
                        {
                            name: ' Started',
                            data: [ 20, 37, 16, 58, 40, 10, 35, 97.4 ]
                        },
                        {
                            name: ' Identified',
                            data: [ 30, 67, 56, 28, 20, 10, 35, 97.4 ]
                        },
                        {
                            name: 'In Complete',
                            data: [ 60, 17, 26, 28, 40, 10, 35, 97.4 ]
                        }
                    ]
                },

        'categories':{
                      1:[
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
                        ],
                      2:[
                            'Schedule N',
                            'Schedule O',
                            'Schedule P',
                            'Schedule Q',
                            'Schedule R',
                            'Schedule S',
                            'Schedule T',
                            'Schedule U'
                      ]
        }};


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
                                            'columns': [
                                                            { colName: 'PD Grade'},
                                                            { colName: '100% Completion'},
                                                            { colName: '< 75% Completion'},
                                                            { colName: '< 50% Completion'},
                                                            { colName: '< 20% Completion'}
                                            ],
                                            'rows':[
                                                {'grade':'11', '100p':'4.0','75p':'2.4','50p':'8.7','20p':'2.4'},
                                                {'grade':'11', '100p':'1.0','75p':'6.0','50p':'2.3','20p':'4.2'},
                                                {'grade':'11', '100p':'0.4','75p':'5.6','50p':'9.0','20p':'8.4'},
                                                {'grade':'11', '100p':'5.2','75p':'8.0','50p':'5.5','20p':'10.0'},
                                                {'grade':'11', '100p':'9.4','75p':'10.0','50p':'3.0','20p':'6.8'},
                                                {'grade':'11', '100p':'7.4','75p':'7.3','50p':'10.5','20p':'3.8'},
                                                {'grade':'11', '100p':'2.1','75p':'3.4','50p':'11','20p':'9.0'},
                                                {'grade':'Total', '100p':'28.5','75p':'41.0','50p':'50.0','20p':'44.8'}
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
                                            'columns': [
                                                            { colName: 'PD Grade'},
                                                            { colName: '100% Completion'},
                                                            { colName: '< 75% Completion'},
                                                            { colName: '< 50% Completion'},
                                                            { colName: '< 20% Completion'}
                                            ],
                                            'rows':[
                                                {'grade':'12', '100p':'4.0','75p':'2.4','50p':'8.7','20p':'2.4'},
                                                {'grade':'12', '100p':'1.0','75p':'6.0','50p':'2.3','20p':'4.2'},
                                                {'grade':'12', '100p':'0.4','75p':'5.6','50p':'9.0','20p':'8.4'},
                                                {'grade':'12', '100p':'5.2','75p':'8.0','50p':'5.5','20p':'10.0'},
                                                {'grade':'12', '100p':'9.4','75p':'10.0','50p':'3.0','20p':'6.8'},
                                                {'grade':'12', '100p':'7.4','75p':'7.3','50p':'10.5','20p':'3.8'},
                                                {'grade':'12', '100p':'2.1','75p':'3.4','50p':'11','20p':'9.0'},
                                                {'grade':'Total', '100p':'28.5','75p':'41.0','50p':'50.0','20p':'44.8'}
                                            ]
                                        }

                                }];
                                            

    MockService.GapData = {
                            'columns': [
                                            { colName: 'FRB Field#'},
                                            { colName: 'Field Name'},
                                            { colName: 'Status'},
                                            { colName: 'Remediation/Disclosure'}
                            ],
                            'rows':[
                                {'frb':'1', 'name':'Customer Id 1', 'status':'Transformation required 1', 'remediation':''},
                                {'frb':'10', 'name':'Obligor Internal Risk Rating 1', 'status':'Complex transformation required 1', 'remediation':''},
                                {'frb':'11', 'name':'TIN 1', 'status':'Complex transformation required 1', 'remediation':''},
                                {'frb':'12', 'name':'Stock Exchange 1', 'status':'Transformation required 1', 'remediation':''},
                                {'frb':'13', 'name':'Ticker Symbol 1', 'status':'Transformation required 1', 'remediation':''}
                            ]
                        };

    MockService.liquidityProfile = {
        nodes: [
            {id: 1, name: "Bank", type: "default", level: 1},
            {id: 2, name: "Subsidiary-1", type: "default", level: 2},
            {id: 3, name: "Subsidiary-2", type: "default", level: 2},
            {id: 4, name: "Branch-1", type: "default", level: 3},
            {id: 5, name: "Branch-2", type: "default", level: 3},
            {id: 6, name: "Branch-3", type: "default", level: 3},
            {id: 7, name: "Branch-4", type: "default", level: 3},
            {id: 8, name: "Branch-5", type: "default", level: 3}
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

    MockService.CeclBaseNodes = [
        {"name": "Topic", "id": "Topic", "idx": 0, "data":{}},{"name": "Sub-Topic", "id": "Sub-Topic", "idx": 1, "data":{}},{"name": "Section", "id": "Section", "idx": 2, "data":{}},{"name": "Paragraph", "id": "Paragraph", "idx": 3, "data":{}},{"name": "Concept", "id": "FASB Concept", "idx": 4, "data":{}}
    ];

    MockService.ParaTagOptions = ["Rule", "Information", "Explanation"];

    MockService.CeclChildNodeDetails = {
        "Amortized Cost Basis" : "The amortized cost basis is the amount at which a financing receivable or investment is originated or acquired, adjusted for applicable accrued interest, accretion, or amortization of premium, discount, and net deferred fees or costs, collection of cash,  writeoffs, foreign exchange, and fair value hedge accounting adjustments",
        "Effective Interest Rate" : "The rate of return implicit in the financial asset, that is, the contractual interest rate adjusted for any net deferred fees or costs, premium, or discount existing at the origination or acquisition of the financial asset",
        "Holding Gain or Loss" : "The net change in fair value of a security. The holding gain or loss does not include dividend or interest income recognized but not yet received, writeoffs, or the allowance for credit losses",
        "Remeasurement Event" : "A remeasurement (new basis) event is an event identified in other authoritative accounting literature, other than the measurement of an impairment under Topic 321 or credit loss under Topic 326 that requires a financial instrument to be remeasured to its fair value at the time of the event but does not require that financial instrument to be reported at fair value continually with the change in fair value recognized in earnings. Examples of remeasurement events are business combinations and significant modifications of debt as discussed in paragraph 470-50-40-6",
        "Accretion" : "Accretion is asset and earnings growth due to business expansion, and it can occur through a company's internal growth or by way of mergers and acquisitions. Accretion is also used to account for a capital gain when an investor buys a bond at a discount and holds the bond until maturity",
        "Collectibility Of Receivables" : "The Company maintains allowances for doubtful accounts for estimated losses resulting from the inability of its customers to make required payments. The Company reviews a customer’s credit history before extending credit as deemed necessary, after considering the client and the size and duration of the assignment. The Company establishes an allowance for doubtful accounts based upon factors surrounding the credit risk of specified customers, historical trends, past due balances and other information. The Company considers an account past due based on the contractual payment terms. The Company has demonstrated the ability to make reasonable and reliable estimates; however, if the financial condition of the Company’s customers was to deteriorate, resulting in an impairment of their ability to make payments, additional allowances may be required."

    }

    return MockService;
})

.factory('VisDataSet', function () {
        'use strict';
        return function (data, options) {
            // Create the new dataSets
            return new vis.DataSet(data, options);
        };
    });
