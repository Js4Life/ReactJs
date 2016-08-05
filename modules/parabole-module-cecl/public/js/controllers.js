angular.module('RDAApp.controllers', ['RDAApp.services', 'RDAApp.directives', 'textAngular'])

.controller('mainCtrl', function($scope, $state, $http, $stateParams, SharedService , RiskAggregateService , AlertDashboardService) {
	$scope.constants = Constant;
	$scope.userName = userName;
	$scope.breadCrumbs = [];
	$scope.userInfo = {user : userName };

	$scope.heading = SharedService.primaryNav[0];
	$scope.ColorMap = SharedService.Colors;

	$scope.activePrimaryIdx = 0;

	$scope.primaryNav = SharedService.primaryNav || [];

	$scope.initialize = function(){

		AlertDashboardService.loadDashboardMenu().then( function( data ) {
			$scope.dashboardAlertMenu = data;
		});
	}

	$scope.getPrimaryClassName = function( idx ){
		if( $scope.activePrimaryIdx == idx ){
			return 'active';
		}
		else{
			return '';
		}
	}

	$scope.goHome = function(){
	 	$state.go('landing.home');
 	}

	$scope.goDashboard = function(){
	 	$state.go('landing.goDashboard');
 	}

	$scope.goAnalysis = function(){
	 	$state.go('landing.analysis');
 	}

	$scope.goTo = function(obj, idx){
		$('.sidebar-nav').removeClass('active-nav');
		$('.sidebar-nav').eq(idx).addClass('active-nav');
		switch(obj)
		{
			case Constant.DOCUMENT_BROWSER_TAB : $scope.viewTitle = Constant.DOCUMENT_BROWSER_TAB;
				$state.go('landing.home');
				break;
			case Constant.IMPACT_TAB : $scope.viewTitle = Constant.IMPACT_TAB;
				$state.go('landing.impact');
				break;
			case Constant.REGULATION_TAB : $scope.viewTitle = Constant.REGULATION_TAB;
				$state.go('landing.regulation');
				break;
		}

	}

	$scope.right = function () {
		var outer = $('#outer');
	   	var leftPos = outer.scrollLeft();
	   	outer.animate({ scrollLeft: leftPos - 200 }, 800);
	};

	$scope.left = function () {
		var outer = $('#outer');
		var leftPos = outer.scrollLeft();
		outer.animate({ scrollLeft: leftPos + 200 }, 800);
	};

	setInterval(function(){
		var currDate = new Date();
		var yy = currDate.getFullYear();
		var hr = currDate.getHours();
		var min = currDate.getMinutes();
		var sec = currDate.getSeconds();
		var ampm = hr > 12 && (min > 0 || sec > 0) ? 'PM' : 'AM';
		hr = hr > 12 ? hr-12 : hr;
		hr = hr == 0 ? 12 : hr;

		currDate = currDate.toString();
		var dateStr = currDate.substring(0, currDate.indexOf(yy)) + hr + ":" + min + ":" + sec + " " + ampm;
		$scope.$apply(function(scope){
			$scope.currDate = dateStr;
		});
  	}, 1000);

	$scope.initialize();
})

.controller('landingCtrl', function($scope, $state, $stateParams, SharedService) {

})

.controller('riskCtrl', function($scope, $state, $stateParams, SharedService, RiskAggregateService) {
	$scope.iniitialize = function( ){
		$scope.heading = SharedService.primaryNav[0];
		$scope.cardinals = SharedService.cardinals;
		RiskAggregateService.loadInitialState().then( function( data ) {
			$scope.dashboardRiskMenu = data;
		});
		SharedService.getBaseNodesMapping().then( function(data){
			$scope.columnData = data;
			SharedService.baseNodeMap = data;
		});
		$scope.selRiskList = { "vertices" : [] };
		$scope.column = {};
	}

	$scope.setColumns = function(node){
		if($scope.columnData){
			var colArr = $scope.columnData[node.name ];
			if(colArr){
				node.columns = colArr;
			}
		}
	}

	$scope.goRiskGraph = function(){
		angular.forEach($scope.dashboardRiskMenu.vertices , function( obj , indx ){
			if(obj.selected==true){
				$scope.selRiskList.vertices.push(obj);
			}
		});

		SharedService.selRiskList = $scope.selRiskList;
		if($scope.selRiskList.vertices.length > 0)
			$state.go('landing.graph');
		else
			alert('Select atleast one entity!');
	}

	$scope.getNodeInfo = function(node){
		$scope.currentNode = node;
		if( !$scope.currentNode.columns ){
			$scope.currentNode.columns = [];
		}
		$('#nodeInfoModal').modal('show');
	}

	$scope.addNodeInfo = function(){
		if( $scope.column.name != undefined && $scope.column.cardinality != undefined ){
			$scope.currentNode.columns.push( $scope.column );
			$scope.column = {};
		}
		else
			alert( "Put all details please!" );
	}

	$scope.removeColumn = function( idx ){
		$scope.currentNode.columns.splice( idx, 1 );
	}

	$scope.saveBaseNodeConfiguration = function(){
		SharedService.saveBaseNodeConfiguration( $scope.currentNode.name, $scope.currentNode.columns ).then(function(data){
			if(data){
				$('#nodeInfoModal').modal('hide');
			}
		});
	}

	$scope.iniitialize();
})

.controller('homeCtrl', function($scope, $state, $timeout, $stateParams, SharedService, RiskAggregateService, graphService, MockService) {
	$scope.iniitialize = function( ){
		$scope.heading = SharedService.primaryNav[0];
		$scope.collapseSlide = {left: false};
		$scope.collapseClasses = {left: "col-xs-2 menu-back slide-container", center: "col-xs-10"};
		$scope.nodes = MockService.CeclBaseNodes;
		$scope.breads = [];
		$scope.showGraph = false;
		$scope.visOptions = {
			labelField:'name',
			handlerData: { click : $scope.clickNode, scope : $scope },
			nodeShape: 'image',
			nodeImageMap: SharedService.graphImageMap,
			nodeImageField: "type",
			hier: false
		};
		$scope.answers = {};
	}
	
	$scope.exploreNode = function (node, e) {
		$scope.breads = [];
		$scope.breads.push(node);
		$scope.searchText = "";
		/*graphService.getRelatedNodes(node.id).then( function( nodeDef ){
			$scope.childNodes = nodeDef.vertices;
		});*/
		SharedService.getFilteredDataByCompName("ceclBaseNodeDetails", node.id).then(function (data) {
			$scope.childNodes = data.data;
		});
		$(e.currentTarget).parent().children().removeClass('active');
		$(e.currentTarget).addClass('active');
	}

	$scope.leftSlideToggle = function(){
		$scope.collapseSlide.left = !$scope.collapseSlide.left;
		scaleSlides();
	}

	$scope.getNodeDetails = function (childNode, index) {
		$scope.currentNode = childNode;
		//$scope.currentNode.definition = MockService.CeclChildNodeDetails[$scope.currentNode.name] || null;
		$scope.getFilteredDataByCompName(childNode.name, childNode, index);
	}

	$scope.getFilteredDataByCompName = function (nodeName, currentNode, index) {
		if(!currentNode){
			currentNode = $scope.currentNode = _.findWhere($scope.childNodes, {"name": nodeName});
		}
		console.log(" Uri: " + currentNode.link);
		$scope.showGraph = false;
		var compName = "";
		switch (currentNode.type){
			case "Topic": 
				compName = "ceclTopicNodeDetails";
				SharedService.getFilteredDataByCompName(compName, nodeName).then(function (data) {
					$scope.childNodes = data.data;
					addBread();
				});
				break;
			case "Sub-Topic":
				compName = "ceclSubTopicNodeDetails";
				SharedService.getFilteredDataByCompName(compName, nodeName).then(function (data) {
					$scope.childNodes = data.data;
					addBread();
				});
				break;
			case "Section":
				compName = "ceclSectionNodeDetails";
				SharedService.getFilteredDataByCompName(compName, nodeName).then(function (data) {
					$scope.childNodes = data.data;
					addBread();
				});
				break;
			case "Paragraph":
				compName = "ceclParagraphNodeDetails";
				SharedService.getFilteredDataByCompName(compName, nodeName).then(function (data) {
					$scope.childNodes = data.data;
					addBread();
				});
				break;
			case "FASB Concept":
				compName = "ceclConceptNodeDetails";
				SharedService.getFilteredDataByCompName(compName, nodeName).then(function (data) {
					var nodes = data.data;
					$scope.rawNodeDetails = nodes;
					$scope.nodeDetails = _.groupBy(nodes, "type");
					getGraphByConceptUri();
					SharedService.getDescriptionByUri($scope.currentNode.link).then(function (description) {
						$scope.currentNode.description = description;
						$('#dsViewer').modal('show');
					});					
				});
				break;
		}
	}

	$scope.getGraph = function () {
		$scope.showGraph = !$scope.showGraph;
	}

	$scope.clickNode = function (nodeId) {
		if( !nodeId ) return;
		$scope.currentGraphNode = $scope.viz.findNodeById( nodeId );
		SharedService.getDescriptionByUri(nodeId).then(function (description) {
			$scope.currentGraphNode.desc = description;
			if($scope.currentGraphNode.desc.definition){
				$('#dsViewer').modal('hide');
				$('#definitionViewer').modal('show');
			}
		});
	}

	$scope.viewDefinitionLink = function(){
		window.open($scope.currentGraphNode.desc.definitionlink);
	}

	$scope.closeDsViewer = function(){
		$scope.currentGraphNodeDesc = null;
		$('#definitionViewer').modal('hide');
		$('#dsViewer').modal('show');
	}

	function getGraphByConceptUri() {
		var rootNode = {name: $scope.currentNode.name, id: $scope.currentNode.link, type: "concept"};
		$scope.graphData = {nodes: [rootNode], edges: []};
		angular.forEach($scope.nodeDetails, function (val, key) {
			angular.forEach(val, function (aNode, idx) {
				var node = {name: aNode.name, id: aNode.link, type: key.toLowerCase()};
				var edge = {from: rootNode.id, to: node.id};
				$scope.graphData.nodes.push(node);
				$scope.graphData.edges.push(edge);
			});
		});
	}

	function addBread() {
		var currentBread = $scope.breads[$scope.breads.length-1];
		if(currentBread.idx === 4) return;
		$scope.breads.push(_.findWhere(MockService.CeclBaseNodes, {"idx": currentBread.idx+1}));
	}

	function scaleSlides(){
		var slide = $scope.collapseSlide;
		var classes = $scope.collapseClasses;
		if(slide.left){
			classes.left = "hidden";
			classes.center = "col-xs-12";
		}
		else{
			classes.left = "col-xs-2 menu-back";
			classes.center = "col-xs-10";
		}
	}

	$scope.iniitialize();
	
	
	/*Checklist related code*/
	/*$scope.startContentParser = function () {
		SharedService.startContentParser().then(function (data) {
			if(data){
				alert("Success!");
			}
		});
	}

	$scope.getParagraphsByConcept = function () {
		SharedService.getParagraphsByConcept($scope.currentNode.name).then(function (data) {
			$scope.paragraphs = angular.fromJson(data.data);
			$('#dsViewer').modal('hide');
			$('#checkListModal').modal('show');
			console.log(data);
		});
	}

	$scope.closeCheckListModal = function () {
		$('#dsViewer').modal('show');
		$('#checkListModal').modal('hide');
	}*/
	/*End*/
	
	$scope.goChecklistBuilder = function () {
		$('#dsViewer').modal('hide');
		$timeout(function () {
			var currentConcept = angular.copy($scope.currentNode);
			var rawNodeDetails = _.reject($scope.rawNodeDetails, function (n) { return n.type === 'Related Concept'	});
			currentConcept.components = angular.copy(rawNodeDetails);
			SharedService.currentConcept = currentConcept;
			$state.go('landing.checklistBuilder');
		}, 200);
	}

	$scope.getCheckList = function (conceptName, componentType, componentName) {
		SharedService.getChecklistByConceptAndComponent(conceptName, componentType, componentName).then(function (data) {
			/*if(data.status) {
				$scope.checkList = data.data;
				$('#dsViewer').modal('hide');
				$('#checklistModal').modal('show');
			}*/
			var status = data.data.status;
			if(status.haveData) {
				$scope.checkList = data.data.questions;
				$('#dsViewer').modal('hide');
				$('#checklistModal').modal('show');
			}
		});
	}

	$scope.getChecklistByNode = function (node) {
		if(node.type === "Paragraph"){
			SharedService.getChecklistByParagraphId(node.name).then(function (data) {
				var status = data.data.status;
				if(status.haveData) {
					$scope.checkList = data.data.questions;
					$('#checklistModal1').modal('show');
				}
			});
		} else {
			SharedService.getChecklistByNode(node.type, node.name).then(function (data) {
				if(data.status) {
					$scope.checkList = data.data;
					$('#checklistModal1').modal('show');
				}
			});
		}
	}

	$scope.saveCheckList = function () {
		var checkedQuestions = _.omit($scope.answers, function(v) {return v;});
		var qIds = _.keys(checkedQuestions);
	}

	$scope.closeCheckListModal = function () {
		$('#dsViewer').modal('show');
		$('#checklistModal').modal('hide');
		clearAnswers();
	}
	
	$scope.getComplianceColorcode = function (val) {
		if(val > 99)
			return 'compliance-green';
		else if(val >= 90 && val <=99)
			return 'compliance-amber';
		else if(val >= 75 && val <=89)
			return 'compliance-red';
		else
			return 'compliance-gray';
	}

	function clearAnswers() {
		$scope.answers = {};
	}
})

.controller('impactCtrl', function($scope, $state, $stateParams, SharedService) {
	$scope.initialize = function () {
		$scope.user = {name: "Bruce Lloyd", role: "Accounts Manager"};    //{name: "Betsy Walters", role: "Branch Manager"}
		$scope.heading = SharedService.primaryNav[1];
		SharedService.getFilteredDataByCompName("impactProductsByRole", $scope.user.role).then(function (data) {
			var products = data.data;
			var prodList = [];
			angular.forEach(products, function (prod) {
				prodList.push(prod.type + " - " + prod.name);
			});
			SharedService.getFunctionalAreasByProducts("impactAreaByProduct", prodList).then(function (data) {
				$scope.allAreas = data.data;
				$scope.columns = data.columns;
				console.log(data);
			});
		});
	}
	
	$scope.getFunctionalAreaDetail = function (productName, areaType, areaName) {
		var filters = [
			{"name": "product", "value": productName},
			{"name": areaType, "value": areaName}
		];
		$scope.modalHead = areaName + " (" + productName + ")";
		var compName = "";
		switch (areaType){
			case "concept":
				compName = "dataelementByFuncArea";
				break;
			case "model":
				compName = "modelByFuncArea";
				break;
			case "policy":
				compName = "policyByFuncArea";
				break;
			case "report":
				compName = "reportByFuncArea";
				break;
		}
		SharedService.getMultiFilteredDataByCompName(compName, filters).then(function (data) {
			$scope.nodeElements = data.data;
			if($scope.nodeElements.length > 0){
				$('#dsViewer').modal('show');
			}
		});
	}

	$scope.initialize();
})

.controller('regulationCtrl', function($scope, $state, $stateParams, SharedService) {
	$scope.initialize = function () {
		$scope.regulations = ["CECL", "IFRS9"];
		$scope.user = {name: "Bruce Lloyd", role: "Accounts Manager"};
		$scope.heading = SharedService.primaryNav[2];
		SharedService.getFilteredDataByCompName("impactProductsByRole", $scope.user.role).then(function (data) {
			var products = data.data;
			var prodList = [];
			angular.forEach(products, function (prod) {
				prodList.push(prod.type + " - " + prod.name);
			});
			SharedService.getFunctionalAreasByProducts("impactAreaByProduct", prodList).then(function (data) {
				$scope.allAreas = data.data;
				$scope.columns = data.columns;
				console.log(data);
			});
		});
	}
	
	$scope.onSelectRegulation = function () {
		switch($scope.selRegulation){
			case $scope.regulations[0]:
				SharedService.getFilteredDataByCompName("impactProductsByRole", "Accounts Manager").then(function (data) {
					var products = data.data;
					var prodList = [];
					angular.forEach(products, function (prod) {
						prodList.push(prod.type + " - " + prod.name);
					});
					SharedService.getFunctionalAreasByProducts("impactAreaByProduct", prodList).then(function (data) {
						$scope.allAreas = data.data;
						$scope.columns = data.columns;
						console.log(data);
					});
				});
				break;
			case $scope.regulations[1]:
				SharedService.getFilteredDataByCompName("impactProductsByRole", "Branch Manager").then(function (data) {
					var products = data.data;
					var prodList = [];
					angular.forEach(products, function (prod) {
						prodList.push(prod.type + " - " + prod.name);
					});
					SharedService.getFunctionalAreasByProducts("impactAreaByProduct", prodList).then(function (data) {
						$scope.allAreas = data.data;
						$scope.columns = data.columns;
						console.log(data);
					});
				});
				break;
		}
	}

	$scope.getImpactedRegulation = function () {

	}

	$scope.getFunctionalAreaDetail = function (productName, areaType, areaName) {
		var filters = [
			{"name": "product", "value": productName},
			{"name": areaType, "value": areaName}
		];
		$scope.modalHead = areaName + " (" + productName + ")";
		var compName = "";
		switch (areaType){
			case "concept":
				compName = "dataelementByFuncArea";
				break;
			case "model":
				compName = "modelByFuncArea";
				break;
			case "policy":
				compName = "policyByFuncArea";
				break;
			case "report":
				compName = "reportByFuncArea";
				break;
		}
		SharedService.getMultiFilteredDataByCompName(compName, filters).then(function (data) {
			$scope.nodeElements = data.data;
			if($scope.nodeElements.length > 0){
				$('#dsViewer').modal('show');
			}
		});
	}

	$scope.initialize();
})

.controller('checklistBuilderCtrl', function($scope, $state, $stateParams, SharedService) {
	$scope.initialize = function () {
		$scope.heading = {title: "Checklist Builder"};
		$scope.question = {components: []};
		$scope.questions = [];
		$scope.currentQuestionCfg = {};
		$scope.multiSelectCfg = {
			idProp : "link",
			displayProp : "name",
			externalIdProp : ""
		}
		$scope.currentConcept = SharedService.currentConcept;
		SharedService.getParagraphsByConcept($scope.currentConcept.name).then(function (data) {
			$scope.paragraphs = angular.fromJson(data.data);
		});
	}

	$scope.selectParagraph = function (para, e) {
		$(e.currentTarget).parent().children().removeClass('active');
		$(e.currentTarget).addClass('active');
		if($scope.currentParagraph) {
			if ($scope.currentParagraph.id != para.id) {
				$scope.currentParagraph = para;
				$scope.questions = [];
			}
		} else{
			$scope.currentParagraph = para;
		}
	}

	$scope.addQuestion = function () {
		$scope.questions.push($scope.question);
		$scope.question = {components:[]};
	}

	$scope.saveQuestions = function () {
		$scope.currentQuestionCfg.paragraphId = $scope.currentParagraph.id;
		$scope.currentQuestionCfg.conceptName = $scope.currentConcept.name;
		$scope.currentQuestionCfg.questions = $scope.questions;
		SharedService.addChecklist($scope.currentQuestionCfg).then(function (data) {
			console.log(data.data);
			if(data.status){
				
			}
		});
	}

	$scope.goPreviousScreen = function () {
		$state.go('landing.home');
	}

	$scope.initialize();
});