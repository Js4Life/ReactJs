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
	$scope.initialize = function(){

	}

	$scope.goToStatusChecklist = function (e) {
		activeCurrentNav(e);
	}

	$scope.goToAlerts = function (e) {
		activeCurrentNav(e);
	}

	$scope.goToInbox = function (e) {
		activeCurrentNav(e);
	}

	$scope.goCompletion = function (e) {
		activeCurrentNav(e);
		$state.go('landing.summery');
	}

	$scope.goCompliance = function (e) {
		activeCurrentNav(e);
		$state.go('landing.complianceDashboard');
	}

	function activeCurrentNav(e) {
		$(e.currentTarget).parent().parent().children().removeClass('active');
		$(e.currentTarget).parent().addClass('active');
	}

	$scope.initialize();
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

.controller('homeCtrl', function($scope, $state, $rootScope, $timeout, $stateParams, SharedService, RiskAggregateService, graphService, MockService) {
	$scope.iniitialize = function( ){
		$scope.heading = SharedService.primaryNav[0];
		$scope.collapseSlide = {left: false};
		$scope.collapseClasses = {left: "col-xs-2 menu-back slide-container", center: "col-xs-10"};
		$scope.nodes = MockService.CeclBaseNodes;
		$scope.breads = SharedService.homeBreads || [];
		$scope.answers = {};
		$scope.currentColorCode = 'all';
        $scope.isGridView = true;
		$scope.leftSlideToggle();
        if($scope.breads.length > 0){
            $scope.onBreadClick($scope.breads[$scope.breads.length - 1]);
        } else {
            $scope.exploreNode();
        }

	}

	$rootScope.$on('PARENTSEARCHTEXT', function (event, data) {
		$scope.searchText = data;
	});

	$rootScope.$on('PARENTISGRIDVIEW', function (event, data) {
		$scope.isGridView = data;
	});

	$scope.exploreNode = function (nodeType, nodeId) {
		$scope.searchText = "";
		insertBread(nodeType, nodeId);
		switch (nodeType) {
			case "TOPIC" :
				SharedService.getSubtopicsByTopicId(nodeId).then(function (data) {
					if(data.status){
						$scope.childNodes = angular.fromJson(data.data);
					}
				});
				break;
			case "SUBTOPIC" :
				SharedService.getSectionsBySubtopicId(nodeId).then(function (data) {
					if(data.status){
						$scope.childNodes = angular.fromJson(data.data);
					}
				});
				break;
			case "SECTION" :
				SharedService.getParagraphsBySectionId(nodeId).then(function (data) {
					if(data.status){
						$scope.childNodes = angular.fromJson(data.data);
					}
				});
				break;
			case "PARAGRAPH" :
				SharedService.paragraphs = $scope.childNodes;
				SharedService.homeBreads = $scope.breads;
				var subSectionId = nodeId.substring(0, nodeId.lastIndexOf('-'));
				var compName = "ceclGenericComponentsByParagraph";
				SharedService.getFilteredDataByCompName(compName, subSectionId).then(function (comp) {
					var nodes = comp.data;
					var currentConcept = {};
					var rawNodeDetails = _.reject(nodes, function (n) {
						return n.type === 'Related Concept'
					});
					currentConcept.components = angular.copy(rawNodeDetails);
					SharedService.currentConcept = currentConcept;
					$state.go('landing.checklistBuilder');
				});
				break;
			default :
				SharedService.getAllTopics().then(function (data) {
					if(data.status){
						$scope.childNodes = angular.fromJson(data.data);
					}
				});
				break;
		}
	}

	function insertBread(nodeType, nodeId){
		if(!nodeType) {
			$scope.breads = [];
			var aBread = $scope.nodes[0];
			aBread.data = {type: "", id: ""};
			$scope.breads.push(aBread);
			return;
		} else if (nodeType === 'PARAGRAPH'){
			return;
		}
		var idx = _.findIndex($scope.nodes, function (n) {return n.id === nodeType});
		var aBread = $scope.nodes[idx+1];
		aBread.data = {type: nodeType, id: nodeId};
		$scope.breads.push(aBread);
	}

	$scope.onBreadClick = function (bread) {
		var idx = _.findIndex($scope.breads, function (b) {	return b.id === bread.id; });
		$scope.breads.splice(idx, $scope.breads.length-1);
		var breadData = bread.data;
		$scope.exploreNode(breadData.type, breadData.id);
	}

	$scope.leftSlideToggle = function(){
		$scope.collapseSlide.left = !$scope.collapseSlide.left;
		scaleSlides();
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

	$scope.getChecklistByNode = function (node) {
		$scope.currentNode = node;
		if(node.type === "Paragraph"){
			SharedService.getChecklistByParagraphId(node.name).then(function (data) {
				var status = data.data.status;
				if(status.haveData) {
					$scope.checkList = data.data.questions;
					$scope.answers = data.data.answers;
					$('#checklistModal1').modal('show');
				} else{
					toastr.warning('No checklist found for ' + node.name);
				}
			});
		} else {
			SharedService.getChecklistByNode(node.type, node.name).then(function (data) {
				var status = data.data.status;
				if(status.haveData) {
					$scope.checkList = data.data.questions;
					$scope.answers = data.data.answers;
					$('#checklistModal1').modal('show');
				} else{
					toastr.warning('No checklist found for ' + node.name);
				}
			});
		}
	}

	$scope.saveAnswers = function () {
		//var checkedQuestions = _.omit($scope.answers, function(v) {return !v;});
		//var qIds = _.keys(checkedQuestions);
		SharedService.addAnswer($scope.answers).then(function (data) {
			if(data.status){
				recalculateCompliance();
				$('#checklistModal').modal('hide');
				$('#checklistModal1').modal('hide');
				toastr.success('Saved Successfully..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	function recalculateCompliance() {
		var qCount = _.size($scope.checkList);
		var checkedQuestions = _.omit($scope.answers, function(v) {return !v;});
		var aCount = _.size(checkedQuestions);
		$scope.currentNode.compliance = Math.floor((aCount*100)/qCount);
	}

	$scope.closeCheckListModal = function () {
		$('#dsViewer').modal('show');
		$('#checklistModal').modal('hide');
		clearAnswers();
	}
	
	$scope.getComplianceColorcode = function (obj) {
		var val = obj.compliance || 0;
		if(val > 81) {
			obj.colorCode = "green";
		} else if(val >= 51 && val <=80) {
			obj.colorCode = "amber";
		} else if(val > 0 && val <=50) {
			obj.colorCode = "red";
		} else {
			obj.colorCode = "gray";
		}
		return $scope.isGridView ? ('bg-' + obj.colorCode) : ('text-' + obj.colorCode);
	}

	function clearAnswers() {
		$scope.answers = {};
	}

	$scope.startContentParser = function () {
		SharedService.startContentParser().then(function (data) {
			if(data){
				toastr.success('Document parsed successfully', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	$scope.startOntologyParser = function () {
		SharedService.startOntologyParser().then(function (data) {
			if(data){
				toastr.success('Ontology parsed successfully', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	$scope.setColorCode = function (colorCode) {
		$scope.currentColorCode = colorCode;
	}

	$scope.toggleView = function () {
        $scope.isGridView = !$scope.isGridView;
    }

	/*$scope.goChecklistBuilder = function () {
	 $('#dsViewer').modal('hide');
	 var compName = "ceclGenericComponentsByConcept";
	 SharedService.getFilteredDataByCompName(compName, $scope.currentNode.name).then(function (data) {
	 var nodes = data.data;
	 var currentConcept = angular.copy($scope.currentNode);
	 var rawNodeDetails = _.reject(nodes, function (n) { return n.type === 'Related Concept'	});
	 currentConcept.components = angular.copy(rawNodeDetails);
	 SharedService.currentConcept = currentConcept;
	 $state.go('landing.checklistBuilder');
	 });
	 }

	 $scope.getCheckList = function (conceptName, componentType, componentName) {
	 SharedService.getChecklistByConceptAndComponent(conceptName, componentType, componentName).then(function (data) {
	 var status = data.data.status;
	 if(status.haveData) {
	 $scope.checkList = data.data.questions;
	 $scope.answers = data.data.answers;
	 $('#dsViewer').modal('hide');
	 $('#checklistModal').modal('show');
	 } else{
	 toastr.warning('No checklist found for ' + componentName);
	 }
	 });
	 }*/

	/*$scope.getFilteredDataByCompName = function (nodeName, currentNode, index) {
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
					manageBreads(currentNode);
				});
				break;
			case "Sub-Topic":
				compName = "ceclSubTopicNodeDetails";
				SharedService.getFilteredDataByCompName(compName, nodeName).then(function (data) {
					$scope.childNodes = data.data;
					manageBreads(currentNode);
				});
				break;
			case "Section":
				compName = "ceclSectionNodeDetails";
				SharedService.getFilteredDataByCompName(compName, nodeName).then(function (data) {
					$scope.childNodes = data.data;
					manageBreads(currentNode);
				});
				break;
			case "Paragraph":
				var subSectionId = nodeName.substring(0, nodeName.lastIndexOf('-'));
				SharedService.getParagraphsBySubsection(subSectionId).then(function (data) {
					if(data.status){
						var paras = data.data;
						if(paras.length > 0) {
							SharedService.paragraphs = paras;
							var compName = "ceclGenericComponentsByParagraph";
							SharedService.getFilteredDataByCompName(compName, subSectionId).then(function (comp) {
								var nodes = comp.data;
								var currentConcept = angular.copy($scope.currentNode);
								var rawNodeDetails = _.reject(nodes, function (n) {
									return n.type === 'Related Concept'
								});
								currentConcept.components = angular.copy(rawNodeDetails);
								SharedService.currentConcept = currentConcept;
								SharedService.homeBreads = $scope.breads;
								$state.go('landing.checklistBuilder');
							});
						} else {
							toastr.warning('No related paragraphs found..', '', {"positionClass" : "toast-top-right"});
						}
					}
				});
				break;
			case "FASB Concept":
				compName = "ceclComponentsByConcept";
				SharedService.getFilteredDataByCompName(compName, nodeName).then(function (data) {
					var nodes = data.data;
					$scope.nodeDetails = _.groupBy(nodes, "type");
					getGraphByConceptUri();
					SharedService.getDescriptionByUri($scope.currentNode.link).then(function (description) {
						$scope.currentNode.description = description;
						$('#dsViewer').modal('show');
					});
				});
				break;
		}
	}*/
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

.controller('checklistBuilderCtrl', function($scope, $state, $stateParams, SharedService, MockService) {
	$scope.initialize = function () {
		$scope.heading = {title: "Checklist Builder"};
		$scope.question = {components: [], isMandatory: true};
		$scope.questions = [];
		$scope.currentQuestionCfg = {};
		$scope.multiSelectCfg = {
			idProp : "link",
			displayProp : "name",
			externalIdProp : ""
		}
		$scope.currentConcept = SharedService.currentConcept;
		$scope.currentParagraphs = [];
		$scope.paraTagOptions = MockService.ParaTagOptions;
		$scope.doParaTag = true;
        $scope.currentTag = 'all';
		$scope.paraTags = {};
		$scope.paragraphs = SharedService.paragraphs;
		setParagraphTags();
		
		$scope.masterComponentTypes = {
			'123-456' : "Comp Type 1",
			'234-567' : "Comp Type 2",
			'334-667' : "Comp Type 3",
			'434-767' : "Comp Type 4",
			'734-767' : "Comp Type 5",
			'834-767' : "Comp Type 6",
			'934-767' : "Comp Type 7",
			'034-767' : "Comp Type 8",
			'134-767' : "Comp Type 9",
		};
	}

	function setParagraphTags() {
		angular.forEach($scope.paragraphs, function (para) {
			if(para.tag){
				$scope.paraTags[para.elementID] = para.tag;
			}
		});
		if(Object.keys($scope.paraTags).length != $scope.paragraphs.length){
			$scope.doParaTag = true;
			toastr.info('Tag paragraphs..', '', {"positionClass" : "toast-top-right"});
		} else {
			$scope.enableChecklistBuilder();
			toastr.info('Select one or more paragraph..', '', {"positionClass" : "toast-top-right"});
		}
	}
	
	$scope.enableChecklistBuilder = function() {
        var tempParagraphs = [];
        angular.forEach($scope.paraTags, function (tag, elementID) {
            tempParagraphs.push(_.findWhere($scope.paragraphs, {"elementID":elementID}));
        });
        $scope.paragraphs = tempParagraphs;
        $scope.doParaTag = false;
    }

	$scope.selectParagraph = function (para) {
		para.isSelected = !para.isSelected;
		var hasPara = _.find($scope.currentParagraphs, function (p) { return p === para.elementID; });
		if(hasPara){
			$scope.currentParagraphs =  _.reject($scope.currentParagraphs, function(p){ return p === para.elementID; });
		} else {
			$scope.currentParagraphs.push(para.elementID);
		}
	}

	/*$scope.addQuestion = function () {
		toastr.info('Save or Add another question..', '', {"positionClass" : "toast-top-right"});
		$scope.questions.push($scope.question);
		$scope.question = {components:[], isMandatory: true};
	}

	$scope.saveQuestions = function () {
		$scope.currentQuestionCfg.paragraphId = $scope.currentParagraphs;
		$scope.currentQuestionCfg.conceptName = $scope.currentConcept.name;
		$scope.currentQuestionCfg.questions = $scope.questions;
		/!*SharedService.addChecklist($scope.currentQuestionCfg).then(function (data) {
			console.log(data.data);
			if(data.status){
				toastr.success('Saved Successfully..', '', {"positionClass" : "toast-top-right"});
				$scope.cleanQuestionEditor();
			}
		});*!/
	}*/
	
	$scope.addChecklist = function () {
		$('#checklistModal').modal('show');
	}
	
	$scope.getChecklistByParagraphs = function () {
		
	}

	$scope.cleanQuestionEditor = function () {
		$scope.currentParagraphs = [];
		$scope.questions = [];
		$scope.question = {components:[], isMandatory:true};
	}

	$scope.saveParaTags = function () {
        SharedService.saveParagraphTags($scope.paraTags).then(function (data) {
           if(data.status){
               toastr.success('Saved Successfully..', '', {"positionClass" : "toast-top-right"});
           } else {
               toastr.error('Error during Saving..', '', {"positionClass" : "toast-top-right"});
           }
        });
	}

	$scope.getParaTagClass = function (tag) {
        switch (tag) {
            case 'Rule':
                return 'bg-green';
                break;
            case 'Information':
                return 'bg-blue';
                break;
            case 'Explanation':
                return 'bg-amber';
                break;
        }
    }

    $scope.setColorCode = function (tag) {
        $scope.currentTag = tag;
    }
    
    $scope.selectComponentType = function (componentTypeId) {

	}
	
	$scope.saveChecklist = function () {
		
	}
	
	$scope.goPreviousScreen = function () {
		$state.go('landing.home');
	}

	$scope.initialize();
})

.controller('summeryCtrl', function($scope, $state, $stateParams, SharedService, MockService) {
    $scope.initialize = function () {
        $scope.heading = {"title": "Completion Dashboard"};
        $scope.options = {
            handlerData : {columnClick: "onColumnClick", scope: $scope},
            colors : ['#04de72', '#00bfff', '#ffb935', '#d2d2d2'],
            dataLabels : {enabled: true},
			legend : {enabled: false}
        }
		setData(MockService.ParagraphCategoryChartData);
    }

    function setData(data) {
		$scope.options.Title = data.title;
		$scope.data = data;
	}
    
    $scope.onColumnClick = function (obj) {
        var tagType = obj.currentTarget.category;
		var value = obj.currentTarget.y;

		switch (tagType){
			case 'Rule' :
				setData(MockService.RuleChartData);
				break;
			case 'Not Created' :
				getParagraphs();
				break;
			case('Created') :
				toastr.info('Feature coming soon..', '', {"positionClass" : "toast-top-right"});
				break;
		}
    }

    function getParagraphs() {
    	//method definition would be change
		SharedService.getParagraphsBySubsection('310-10-35-1').then(function (data) {
			if(data.status){
				var paras = data.data;
				if(paras.length > 0) {
					SharedService.paragraphs = paras;
					var compName = "ceclGenericComponentsByParagraph";
					SharedService.getFilteredDataByCompName(compName, '310-10-35-1').then(function (comp) {
						var nodes = comp.data;
						var currentConcept = {};
						var rawNodeDetails = _.reject(nodes, function (n) {
							return n.type === 'Related Concept'
						});
						currentConcept.components = angular.copy(rawNodeDetails);
						SharedService.currentConcept = currentConcept;
						$state.go('landing.checklistBuilder');
					});
				}
			}
		});
	}
    
    $scope.initialize();  
})

.controller('complianceDashboardCtrl', function($scope, $rootScope, $state, $stateParams, SharedService, MockService) {
	$scope.initialize = function () {
		$scope.heading = {"title": "Compliance Dashboard"};
		$scope.isGridView = true;
		$scope.currentView = 'SUMMERY';
		$scope.options = {
			handlerData : {columnClick: "onColumnClick", scope: $scope},
			colors : ['#04de72', '#00bfff', '#ffb935', '#d2d2d2'],
			dataLabels : {enabled: true},
			legend : {enabled: false}
		}
		setData(MockService.ChecklistComplianceChartData);
	}

	function setData(data) {
		$scope.options.Title = data.title;
		$scope.data = data;
	}

	$scope.onColumnClick = function (obj) {
		var tagType = obj.currentTarget.category;
		var value = obj.currentTarget.y;

		switch (tagType){
			case 'Not Complied' :
				getParagraphs();
				break;
			case('Complied') :
				toastr.info('Feature coming soon..', '', {"positionClass" : "toast-top-right"});
				break;
		}
	}

	$scope.goSummeryView = function () {
		$scope.currentView = 'SUMMERY';
	}

	$scope.goDocumentView = function () {
		$scope.currentView = 'DOCUMENT';
		$state.go('landing.complianceDashboard.documentViewer');
	}

	$scope.goConceptView = function () {
		$scope.currentView = SharedService.currentView = 'ALL_CONCEPT';
		$state.go('landing.complianceDashboard.checklistViewer', {currentView: $scope.currentView});
	}

	$scope.goComponetView = function () {
		$scope.currentView = SharedService.currentView = 'ALL_COMPONENT';
		$state.go('landing.complianceDashboard.checklistViewer', {currentView: $scope.currentView});
	}

	$scope.goBusinessSegmentView = function () {
		$scope.currentView = SharedService.currentView = 'BUSINESS_SEGMENT';
		$state.go('landing.complianceDashboard.checklistViewer', {currentView: $scope.currentView});
	}

	$scope.goIndustryImpact = function () {
		$scope.currentView = 'INDUSTRY_IMPACT';
		var compName = "industryImpact";
		SharedService.getFilteredDataByCompName(compName).then(function (data) {
			$scope.tableData = data.data;
		});
	}

	$scope.$watch('parentSearchText',function(newVal){
		$rootScope.$emit('PARENTSEARCHTEXT', newVal);
	});

	$scope.toggleView = function () {
		$scope.isGridView = !$scope.isGridView;
		$rootScope.$emit('PARENTISGRIDVIEW', $scope.isGridView);
	}

	$scope.initialize();
})

.controller('checklistViewerCtrl', function($scope, $rootScope, $state, $stateParams, SharedService, MockService){
	$scope.initialize = function () {
		$scope.isGridView = true;
		$scope.showGraph = false;
		$scope.currentColorCode = 'all';
		$scope.breads = [" "];
		$scope.visOptions = {
			labelField:'name',
			handlerData: { click : $scope.clickNode, scope : $scope },
			nodeShape: 'image',
			nodeImageMap: SharedService.graphImageMap,
			nodeImageField: "type",
			hier: false
		};
		$scope.exploreNode(SharedService.currentView);
	}

	$rootScope.$on('PARENTSEARCHTEXT', function (event, data) {
		$scope.searchText = data;
	});

	$rootScope.$on('PARENTISGRIDVIEW', function (event, data) {
		$scope.isGridView = data;
	});

	$scope.exploreNode = function (nodeType, nodeId) {
		$scope.searchText = "";
		$scope.showGraph = false;
		switch (nodeType) {
			case "ALL_CONCEPT":
				SharedService.getAllConcepts().then(function (data) {
					if(data.status) {
						$scope.childNodes = angular.fromJson(data.data);
					}
				});
				break;
			case "CONCEPT" :
				/*SharedService.getParagraphsByConceptId(nodeId).then(function (data) {
					if(data.status) {
						$scope.childNodes = angular.fromJson(data.data);
					}
				});*/
				$scope.currentNode = _.findWhere($scope.childNodes, {"elementID": nodeId});
				$scope.getComponentsByConceptName($scope.currentNode.name);
				break;
			case "ALL_COMPONENT" :
				SharedService.getAllComponents().then(function (data) {
					if(data.status) {
						$scope.childNodes = angular.fromJson(data.data);
					}
				});
				break;
			case "BUSINESS_SEGMENT" :
				var compName = "allBusinessSegments";
				SharedService.getFilteredDataByCompName(compName).then(function (data) {
					$scope.childNodes = data.data;
				});
				break;
		}
	}

	$scope.getComponentsByConceptName = function (nodeName) {
		var compName = "ceclComponentsByConcept";
		SharedService.getFilteredDataByCompName(compName, nodeName).then(function (data) {
			var nodes = data.data;
			$scope.nodeDetails = _.groupBy(nodes, "type");
			getGraphByConceptUri();
			SharedService.getDescriptionByUri($scope.currentNode.elementID).then(function (description) {
				$scope.currentNode.description = description;
				$('#dsViewer').modal('show');
			});
		});
	}

	function getGraphByConceptUri(currentNode) {
		var rootNode = {name: $scope.currentNode.name, id: $scope.currentNode.elementID, type: "concept"};
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

	$scope.getComplianceColorcode = function (obj) {
		var val = obj.compliance || 0;
		if(val > 81) {
			obj.colorCode = "green";
		} else if(val >= 51 && val <=80) {
			obj.colorCode = "amber";
		} else if(val > 0 && val <=50) {
			obj.colorCode = "red";
		} else {
			obj.colorCode = "gray";
		}
		return $scope.isGridView ? ('bg-' + obj.colorCode) : ('text-' + obj.colorCode);
	}

	$scope.setColorCode = function (colorCode) {
		$scope.currentColorCode = colorCode;
	}

	$scope.initialize();
});