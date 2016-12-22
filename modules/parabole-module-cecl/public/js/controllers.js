angular.module('RDAApp.controllers', ['RDAApp.services', 'RDAApp.directives', 'textAngular'])

.controller('mainCtrl', function($scope, $state, $http, $stateParams, SharedService , RiskAggregateService , AlertDashboardService) {
	$scope.constants = Constant;
	$scope.userName = userName;

	$scope.breadCrumbs = [];
	$scope.userInfo = {user : userName, role: role};


	/*to be deleted: for showing limited nfeatures*/
	$scope.showLimited = false;
	if(role === "FORDEMO"){
		$scope.showLimited = true;
	}
	/*end*/


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
				$state.go('landing.regulation');
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

	/*setInterval(function(){
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
  	}, 1000);*/

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
		$state.go('landing.summary');
	}

	$scope.goCompliance = function (e) {
		activeCurrentNav(e);
		$state.go('landing.complianceDashboard');
	}

    $scope.goParagraphRepo = function (e) {
        activeCurrentNav(e);
        $state.go('landing.paragraphRepository');
    }

	$scope.goFileUploader = function () {
		$state.go('landing.documentUploader');
	}
	
	$scope.loadConceptVsContextMap = function () {
		SharedService.saveConceptVsContextMap().then(function (data) {
			if(data.status){
				toastr.success('Loading successful..', '', {"positionClass" : "toast-top-right"});
			} else {
				toastr.error('Loading failed..', '', {"positionClass" : "toast-top-right"});
			}
		});
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
		$scope.currentRegulation = SharedService.curentRegulation || 'FASB';
		$scope.currentRegulationFile = angular.copy(SharedService.regulationFileName);
		SharedService.regulationFileName = null;
		$scope.collapseSlide = {left: false};
		$scope.collapseClasses = {left: "col-xs-2 menu-back slide-container", center: "col-xs-10"};
		if($scope.currentRegulation === 'FASB'){
			$scope.nodes = MockService.FasbBaseNodes;
		} else if($scope.currentRegulation === 'BASEL'){
			$scope.nodes = MockService.BaselBaseNodes;
		} else if($scope.currentRegulation === 'CFR'){
			$scope.nodes = MockService.CfrBaseNodes;
		} else if($scope.currentRegulation === 'BANKDOCUMENT'){
			$scope.nodes = MockService.BankBaseNodes;
		}

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
		configureGridOption();
		$scope.answers = {};
	}

	function configureGridOption() {
		$scope.gridOptions = {
			columnDefs: [
				{ field: 'BODY_TEXT', name: 'Checklist Item' },
				{ field: 'IS_CHECKED', name: 'Checked', cellTemplate: '<div class="text-center"><i ng-if="row.entity.IS_CHECKED" class="fa fa-check text-success" aria-hidden="true"></i><i ng-if="!row.entity.IS_CHECKED" class="fa fa-times text-danger" aria-hidden="true"></i></div>' },
				{ field: 'CREATED_BY', name: 'User' },
				{ field: 'UPDATED_BY', name: 'Updated By' },
				{ field: 'ATTACHMENTINFO', name: 'Has Evidence' },
				{ field: 'paragraphs', name: 'Paragraphs' },
				{ field: 'components', name: 'Component' },
				{ field: 'IS_MANDATORY', name: 'Mandatory', cellTemplate: '<div class="text-center"><i ng-if="row.entity.IS_MANDATORY" class="fa fa-check text-success" aria-hidden="true"></i><i ng-if="!row.entity.IS_MANDATORY" class="fa fa-times text-danger" aria-hidden="true"></i></div>' },
				{ field: 'STATE', name: 'Current State' }
			],
			enableSelectAll: false,
			exporterCsvFilename: 'download.csv',
			exporterPdfDefaultStyle: {fontSize: 9},
			exporterPdfTableStyle: {margin: [30, 30, 30, 30]},
			exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, color: 'blue'},
			exporterPdfHeader: { text: "My Header", style: 'headerStyle' },
			exporterPdfFooter: function ( currentPage, pageCount ) {
				return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
			},
			exporterPdfCustomFormatter: function ( docDefinition ) {
				docDefinition.styles.headerStyle = { fontSize: 16, bold: true, margin: [30, 30, 0, 10] };
				docDefinition.styles.footerStyle = { fontSize: 10, bold: false, margin: [30, 10, 0, 30] };
				return docDefinition;
			},
			exporterPdfOrientation: 'landscape',
			exporterPdfPageSize: 'A4',
			exporterPdfMaxGridWidth: 680,
			exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
			onRegisterApi: function(gridApi){
				$scope.gridApi = gridApi;

			}
		};
	}

	$scope.viewChecklistDetails = function () {
		var checklistIds = _.pluck($scope.checkList, 'id');
		SharedService.checklistDetailsByIds(checklistIds).then(function (data) {
			if(data.status){
				console.log(angular.fromJson(data.data));
				$scope.gridOptions.data = angular.fromJson(data.data);
				$('#checklistModal').modal('hide');
				$('#checklistDetailsModal').modal('show');
				$timeout( function() {
					$scope.gridApi.core.handleWindowResize();
				}, 500, 10);
			}
		});
	}
	$scope.exportCsv = function(){
		var gridElement = angular.element(document.querySelectorAll(".custom-csv-link-location"));
		$scope.gridApi.exporter.csvExport( "all", "all", gridElement );
	};
	$scope.exportPdf = function(){
		$scope.gridApi.exporter.pdfExport( "all", "all" );
	};

	$rootScope.$on('PARENTSEARCHTEXT', function (event, data) {
		$scope.searchText = data;
	});

	$rootScope.$on('PARENTISGRIDVIEW', function (event, data) {
		$scope.isGridView = data;
	});

	$scope.exploreNode = function (nodeType, nodeId) {
		$scope.searchText = "";
		insertBread(nodeType, nodeId);
		if($scope.currentRegulation === 'FASB') {
			switch (nodeType) {
				case "TOPIC" :
					SharedService.getSubtopicsByTopicId(nodeId).then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
				case "SUBTOPIC" :
					SharedService.getSectionsBySubtopicId(nodeId).then(function (data) {
						if (data.status) {
							$scope.childNodes = removeEmptyAndUnique(angular.fromJson(data.data));
						}
					});
					break;
				case "SECTION" :
					SharedService.getParagraphsBySectionId(nodeId).then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
				case "PARAGRAPH" :
					SharedService.hideAllToolTips();
					SharedService.paragraphs = $scope.childNodes;
					SharedService.homeBreads = $scope.breads;
					$state.go('landing.checklistBuilder');
					break;
				default :
					SharedService.getAllTopics().then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
			}
		} else if($scope.currentRegulation === 'BASEL') {
			switch (nodeType) {
				case "BASELTOPIC" :
					SharedService.getBaselSubtopicsByTopicId(nodeId).then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
				case "BASELSUBTOPIC" :
					SharedService.getBaselSectionsBySubtopicId(nodeId).then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
				case "BASELSECTION" :
					SharedService.getBaselParagraphsBySectionId(nodeId).then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
				case "BASELPARAGRAPH" :
					SharedService.hideAllToolTips();
					SharedService.paragraphs = _.where($scope.childNodes, {type: "BASELPARAGRAPH"});
					SharedService.homeBreads = $scope.breads;
					$state.go('landing.checklistBuilder');
					break;
				default :
					SharedService.getAllBaselTopics($scope.currentRegulationFile).then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
			}
		} else if($scope.currentRegulation === 'BANKDOCUMENT') {
			switch (nodeType) {
				case "BANKTOPIC" :
					SharedService.getBankSubtopicsByTopicId(nodeId).then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
				case "BANKSUBTOPIC" :
					SharedService.getBankSectionsBySubtopicId(nodeId).then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
				case "BANKSECTION" :
					SharedService.getBankParagraphsBySectionId(nodeId).then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
				case "BANKPARAGRAPH" :
					SharedService.hideAllToolTips();
					SharedService.paragraphs = _.where($scope.childNodes, {type: "BANKPARAGRAPH"});
					SharedService.homeBreads = $scope.breads;
					$state.go('landing.checklistBuilder');
					break;
				default :
					SharedService.getAllBankTopics($scope.currentRegulationFile).then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
			}
		} else if($scope.currentRegulation === 'CFR'){
			nodeId = nodeId ? nodeId : $scope.currentRegulationFile;
			switch (nodeType) {
				case "CFRPARAGRAPH" :
					SharedService.hideAllToolTips();
					SharedService.paragraphs = _.where($scope.childNodes, {type: "CFRPARAGRAPH"});
					SharedService.homeBreads = $scope.breads;
					$state.go('landing.checklistBuilder');
					break;

				default:
					SharedService.getAllChildrenByRootId(nodeId).then(function (data) {
						if (data.status) {
							$scope.childNodes = angular.fromJson(data.data);
						}
					});
					break;
			}
		}
	}

	function removeEmptyAndUnique(objList) {
		objList = _.reject(objList, function (obj) {
			return (_.isEmpty(obj) || (obj.elementID == null && obj.id == null));
		});
		var uniqueList = _.uniq(objList, function(item) {
			return item.elementID || item.id;
		});
		return uniqueList;
	}

	function insertBread(nodeType, nodeId){
		if(!nodeType) {
			$scope.breads = [];
			var aBread = $scope.nodes[0];
			aBread.data = {type: "", id: ""};
			$scope.breads.push(aBread);
			return;
		} else if (nodeType === 'PARAGRAPH' || nodeType === 'BASELPARAGRAPH' || nodeType === 'CFRPARAGRAPH' || nodeType === 'BANKPARAGRAPH'){
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
		SharedService.getChecklistByNodeId(node).then(function (data) {
			if(data.status){
				$scope.currentNode = node;
				$scope.checkList = removeEmptyAndUnique(angular.fromJson(data.data));
				if($scope.checkList.length > 0) {
					populateAnswers($scope.checkList);
					recalculateCompliance();
					$('#checklistModal').modal('show');
				} else
					toastr.warning('No Checklist available..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	function populateAnswers(allChecklists) {
		$scope.answers = {};
		angular.forEach(allChecklists, function (c) {
			$scope.answers[c.id] = c.isChecked || false;
		});
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
		if(obj && !$scope.showLimited) {
			var val = obj.compliance || 0;
			if (val > 81) {
				obj.colorCode = "green";
			} else if (val >= 51 && val <= 80) {
				obj.colorCode = "amber";
			} else if (val > 0 && val <= 50) {
				obj.colorCode = "red";
			} else {
				obj.colorCode = "gray";
			}
			return $scope.isGridView ? ('bg-' + obj.colorCode) : ('text-' + obj.colorCode);
		}
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
		$scope.heading = {"title": "Knowledge Repository"};
		SharedService.homeBreads = [];
		$scope.regulationFiles = null;
		$scope.currentGenre = 'all';
		SharedService.getRegulations().then(function (data) {
			if(data.status){
				$scope.regulations = data.data;
			}
		});
	}

	$scope.loadRegulation = function (reg) {
		/*SharedService.loadRegulation(reg.name).then(function (data) {

		});*/
		SharedService.curentRegulation = reg.key;
		if(reg.key === 'BASEL'){
			SharedService.getAllDocFileNamesByType('FILE').then(function(data){
				if(data.status){
					$scope.heading.title = "List of documents (" + reg.name + ")";
					$scope.regulationFiles = angular.fromJson(data.data);
				}
			});
		} else if(reg.key === 'CFR'){
			SharedService.getAllDocFileNamesByType('CFRFILE').then(function(data){
				if(data.status){
					$scope.heading.title = "List of documents (" + reg.name + ")";
					$scope.regulationFiles = angular.fromJson(data.data);
				}
			});
		} else if(reg.key === 'BANKDOCUMENT'){
            SharedService.getAllDocFileNamesByType('BANKFILE').then(function(data){
                if(data.status){
                    $scope.heading.title = "List of documents (" + reg.name + ")";
                    $scope.regulationFiles = angular.fromJson(data.data);
                }
            });
        } else {
			SharedService.hideAllToolTips();
			$state.go('landing.homeContainer');
		}
	}

	$scope.loadFileBasedRegulation = function (fileName) {
		SharedService.regulationFileName = fileName;
		SharedService.hideAllToolTips();
		$state.go('landing.homeContainer');
	}

	$scope.setGenre = function (genre) {
		$scope.currentGenre = genre;
	}

	$scope.goPreviousScreen = function () {
		$scope.regulationFiles = null;
	}

	$scope.initialize();
})

.controller('checklistBuilderCtrl', function($scope, $state, $stateParams, $timeout, Upload, SharedService, MockService) {
	$scope.initialize = function () {
		$scope.heading = {title: "Checklist Builder"};
		$scope.question = {components: [], isMandatory: true};
		$scope.questions = [];
		$scope.currentQuestionCfg = {};
		$scope.currentParagraphs = {};
		$scope.currentComponentTypes = {};
		$scope.paraTagOptions = MockService.ParaTagOptions;
		$scope.doParaTag = true;
        $scope.currentTag = 'all';
		$scope.paraTags = {};
		$scope.paragraphs = SharedService.paragraphs;
		setParagraphTags();
		$scope.masterComponentTypes = {};
		$scope.attachments = [];
		$scope.fileMimeTypes = SharedService.fileType;
		SharedService.getRegulations().then(function (data) {
			if(data.status){
				$scope.regulations = data.data;
			}
		});
		configureGridOption();
	}

	function configureGridOption() {
		$scope.gridOptions = {
			columnDefs: [
				{ field: 'isBase', name: 'Base Paragraph', width: "150", cellTemplate: '<div class="text-center"><i ng-if="row.entity.isBase" class="fa fa-check text-success" aria-hidden="Yes"></i><i ng-if="!row.entity.isBase" class="fa fa-times text-danger" aria-hidden="No"></i></div>' },
				{ field: 'regulation', name: 'Knowledge Repository', width: "200" },
				{ field: 'file', name: 'File', width: "200" },
				{ field: 'concepts', name: 'Related Concepts', width: "200" },
				{ field: 'bodyText', name: 'Paragraph', width: "*"}
			],
			enableSelectAll: false,
			exporterCsvFilename: 'download.csv',
			exporterPdfDefaultStyle: {fontSize: 9},
			exporterPdfTableStyle: {margin: [30, 30, 30, 30]},
			exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, color: 'blue'},
			exporterPdfHeader: { text: "My Header", style: 'headerStyle' },
			exporterPdfFooter: function ( currentPage, pageCount ) {
				return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
			},
			exporterPdfCustomFormatter: function ( docDefinition ) {
				docDefinition.styles.headerStyle = { fontSize: 16, bold: true, margin: [30, 30, 0, 10] };
				docDefinition.styles.footerStyle = { fontSize: 10, bold: false, margin: [30, 10, 0, 30] };
				return docDefinition;
			},
			exporterPdfOrientation: 'landscape',
			exporterPdfPageSize: 'A4',
			exporterPdfMaxGridWidth: 680,
			exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
			onRegisterApi: function(gridApi){
				$scope.gridApi = gridApi;

			}
		};
	}

	$scope.viewCsvExporter = function () {
		$scope.exportableParagraphs = [];
		var basePara = {
			isBase: true,
			regulation: getRegulationByParaType($scope.baseParagraph.type),
			file: $scope.baseParagraph.fromFileName || "FASB",
			bodyText: $scope.baseParagraph.bodyText,
			concepts: ""
		}
		$scope.exportableParagraphs.push(basePara);

		angular.forEach($scope.relateParagraphs, function (para) {
			var aPara = {
				isBase: false,
				regulation: getRegulationByParaType(para.type),
				file: para.fromFileName || "FASB",
				bodyText: para.bodyText,
				concepts: getConceptsByParaId(para.elementID)
			}
			$scope.exportableParagraphs.push(aPara);
		});
		$scope.gridOptions.data = $scope.exportableParagraphs;
		$('#checklistModal').modal('hide');
		$('#csvExporterModal').modal('show');
		$timeout( function() {
			$scope.gridApi.core.handleWindowResize();
		}, 500, 10);
	}
	$scope.closeCsvExporter = function () {
		$('#csvExporterModal').modal('hide');
		$('#checklistModal').modal('show');
	}
	$scope.exportCsv = function(){
		var gridElement = angular.element(document.querySelectorAll(".custom-csv-link-location"));
		$scope.gridApi.exporter.csvExport( "all", "all", gridElement );
	};

	function getConceptsByParaId(paraId) {
		var conceptStr = "";
		var concepts = $scope.relatedParaConcepts[paraId];
		if(concepts != null){
			angular.forEach(concepts, function (con) {
				if(conceptStr == "")
					conceptStr = con;
				else
					conceptStr = conceptStr + "," + con;
			});
		}
		return conceptStr;
	}
	function getRegulationByParaType(type) {
		switch (type){
			case "PARAGRAPH": return "FASB";
			case "BASELPARAGRAPH": return "BASEL";
			case "CFRPARAGRAPH": return "FEDERAL REGISTER";
			case "BANKPARAGRAPH": return "BANK DOCUMENT";
		}
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

	$scope.editTags = function () {
		SharedService.hideAllToolTips();
		$scope.doParaTag = true;
	}
	
	$scope.enableChecklistBuilder = function() {
		SharedService.hideAllToolTips();
        var tempParagraphs = [];
        angular.forEach($scope.paraTags, function (tag, elementID) {
            tempParagraphs.push(_.findWhere($scope.paragraphs, {"elementID":elementID}));
        });
        $scope.paragraphs = tempParagraphs;
        $scope.doParaTag = false;
    }

	/*$scope.selectParagraph = function (para) {
		para.isSelected = !para.isSelected;
		var hasPara = _.find($scope.currentParagraphs, function (p) { return p === para.elementID; });
		if(hasPara){
			$scope.currentParagraphs =  _.reject($scope.currentParagraphs, function(p){ return p === para.elementID; });
		} else {
			$scope.currentParagraphs.push(para.elementID);
		}
	}*/

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
	
	$scope.getChecklistByParagraphs = function () {
		var selectedParagraphs = _.pick($scope.currentParagraphs, function (val, key) { return val;	});
		selectedParagraphs = _.keys(selectedParagraphs);
		if(selectedParagraphs.length < 1) {
			toastr.warning('Select at least one paragraph..', '', {"positionClass": "toast-top-right"});
			return;
		}
		SharedService.getChecklistsByParagraphIds(selectedParagraphs).then(function (data) {
			if(data.status){
				$scope.checklist = removeEmptyAndUnique(transformToViewModel(angular.fromJson(data.data)));
			}
		});
	}

	function transformToViewModel(data) {
		var allChecklists = [];
		angular.forEach(data, function (checklistItem) {
			var obj = checklistItem["checklist"];
			obj.paragraphs = checklistItem["paragraphs"];
			obj.componentTypes = checklistItem["componentTypes"];
			allChecklists.push(obj);
		});
		return allChecklists;
	}

	function removeEmptyAndUnique(objList) {
		objList = _.reject(objList, function (obj) {
			return (_.isEmpty(obj) || obj.id == null);
		});
		var uniqueList = _.uniq(objList, function(item, key, id) {
			return item.id;
		});
		return uniqueList;
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

	$scope.selectParagraph = function (paraId) {
		var isSelected = $scope.currentParagraphs[paraId];
		if(isSelected){
			$scope.currentParagraphs[paraId] = false;
		} else {
			$scope.currentParagraphs[paraId] = true;
		}
	}
    
    $scope.selectComponentType = function (componentTypeId) {
		var isSelected = $scope.currentComponentTypes[componentTypeId];
		if(isSelected){
			$scope.currentComponentTypes[componentTypeId] = false;
		} else {
			$scope.currentComponentTypes[componentTypeId] = true;
		}
	}

	$scope.addChecklist = function (checklistItem) {
		$scope.relateParagraphs = [];
		$scope.relatedParaConcepts = {};
		$scope.checklistItem = checklistItem || {isMandatory : true};
		if(!checklistItem)
			$scope.currentComponentTypes = {};
		var selectedParagraphs = _.pick($scope.currentParagraphs, function (val, key) { return val;	});
		selectedParagraphs = _.keys(selectedParagraphs);
		if(selectedParagraphs.length < 1) {
			toastr.warning('Select at least one paragraph..', '', {"positionClass": "toast-top-right"});
			return;
		}
		SharedService.getComponentTypesByParagraphIds(selectedParagraphs).then(function (data) {
			if(data.status){
				$scope.masterComponentTypes = angular.fromJson(data.data);
				$('#checklistModal').modal('show');
			}
		});
	}

	$scope.getRelatedContexts = function (para) {
		$scope.baseParagraph = para;
		var defaultContext = {
			context_name : "Default",
			context_uri : "default"
		};
		SharedService.getRelatedContextsByParaId(para.elementID).then(function (data) {
			if(data.status){
				$scope.currentContexts = angular.fromJson(data.data);
				$scope.currentContexts.push(defaultContext);
				$('#checklistModal').modal('hide');
				$('#paraFilterModal').modal('show');
			} else {
				toastr.error('Error in finding contexts..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	$scope.getRelatedParagraphsByContext = function () {
		var selContexts = [];
		var selRegulations = [];
		var isDefaultSelected = false;
		angular.forEach($scope.currentContexts, function (obj) {
			if(obj.isChecked){
				selContexts.push(obj.context_uri);
				if(obj.context_uri === 'default')
					isDefaultSelected = true;
			}
		});
		angular.forEach($scope.regulations, function (obj) {
			if(obj.isChecked){
				selRegulations.push(obj.key);
			}
		});
		if(selContexts.length < 1){
			toastr.warning('Select a Context..', '', {"positionClass" : "toast-top-right"});
			return;
		}
		if(selRegulations.length < 1){
			toastr.warning('Select a Knowledge Repository..', '', {"positionClass" : "toast-top-right"});
			return;
		}
		if(!isDefaultSelected) {
			SharedService.getRelatedParagraphsByContexts(selContexts, selRegulations).then(function (data) {
				if (data.status) {
					$scope.relateParagraphs = angular.fromJson(data.data);
					$scope.relatedParaConcepts = angular.fromJson(data.concepts);
					$scope.getChecklistModal();
				}
			});
		} else {
			SharedService.getRelatedParagraphsById($scope.baseParagraph.elementID, $scope.baseParagraph.fromFileName).then(function (data) {
				if(data.status) {
					$scope.relateParagraphs = angular.fromJson(data.data);
					$scope.getChecklistModal();
				}
			});
		}
	}

	$scope.getChecklistModal = function () {
		$('#checklistModal').modal('show');
		$('#paraFilterModal').modal('hide');
	}
	
	$scope.saveChecklist = function () {
		$scope.checklistItem.paragraphs = $scope.currentParagraphs;
		$scope.checklistItem.componentTypes = $scope.currentComponentTypes;
		SharedService.saveOrUpdateCheckList($scope.checklistItem).then(function (data) {
			if(data.status){
				$('#checklistModal').modal('hide');
				toastr.success('Saved Successfully..', '', {"positionClass" : "toast-top-right"});
			} else {
				toastr.error('Error in Checklist Saving..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	$scope.editChecklist = function (c) {
		$scope.currentParagraphs = c.paragraphs;
		$scope.currentComponentTypes = c.componentTypes;
		$scope.getAttachmentsByChecklistId(c.id);
		$scope.addChecklist(c);
	}

	$scope.deleteChecklist = function (c) {
		SharedService.removeChecklistById(c.id).then(function (data) {
			if(data.status){
				$scope.checklist = _.reject($scope.checklist, function (aChecklist) {
					return aChecklist.id === c.id;
				});
				toastr.success('Removed Successfully..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	$scope.$watch('file', function (newVal) {
		if(newVal) {
			Upload.dataUrl($scope.file, true).then(function (dataUrl) {
				var fileData = {
					name: $scope.file.name,
					mime: $scope.file.type,
					data: dataUrl,
					checklistId: $scope.checklistItem.id
				}
				uploadAttachment(fileData);
			});
		}
	});

	$scope.addFile = function () {
		SharedService.hideAllToolTips();
		$scope.commentMode = false;
	}
	$scope.addComment = function () {
		SharedService.hideAllToolTips();
		$scope.commentMode = true;
	}

	$scope.saveComment = function () {
		var charLimit = 50;
		var fileData = {
			name : $scope.comment.length > charLimit ? $scope.comment.substring(0, charLimit) + "..." : $scope.comment,
			mime : "comment",
			data : $scope.comment,
			checklistId : $scope.checklistItem.id
		}
		uploadAttachment(fileData);
		$scope.commentMode = false;
	}

	function uploadAttachment(fileData) {
		SharedService.uploadAttachmentByChecklistId(fileData).then(function (data) {
			if (data.status) {
				fileData.id = data.data;
				//$scope.attachments.push(fileData);
				$scope.getAttachmentsByChecklistId($scope.checklistItem.id);
			}
		});
	}

	$scope.getAttachmentsByChecklistId = function (id) {
		SharedService.getAttachmentsByChecklistId(id).then(function (data) {
			if (data.status) {
				$scope.attachments = angular.fromJson(data.data);
			}
		});
	}

	$scope.deleteAttachmentById = function (id) {
		SharedService.deleteAttachmentById(id).then(function (data) {
			if(data.status){
				$scope.getAttachmentsByChecklistId($scope.checklistItem.id);
			}
		});
	}

	$scope.viewAttachment = function (at) {
		if($scope.fileMimeTypes[at.mime] === "comment"){
			SharedService.getCommentAttachmentById(at.data_id).then(function (data) {
				if(data.status){
					$scope.viewComment = data.data;
					$('#checklistModal').modal('hide');
					$('#commentViewer').modal('show');
				}
			});
		} else {
			window.open("downloadAttachmentById?id=" + at.data_id);
		}
	}

	$scope.onCommentViewerClose = function () {
		$('#commentViewer').modal('hide');
		$('#checklistModal').modal('show');
	}
	
	$scope.goPreviousScreen = function () {
		$state.go('landing.home');
	}

	$scope.initialize();
})

.controller('summaryCtrl', function($scope, $state, $stateParams, SharedService, MockService) {
    $scope.initialize = function () {
        $scope.heading = {"title": "Completion Dashboard"};
        $scope.options = {
            handlerData : {columnClick: "onColumnClick", scope: $scope},
            colors : ['#04de72', '#00bfff', '#ffb935', '#d2d2d2'],
            dataLabels : {enabled: true},
			legend : {enabled: false}
        }
		getChartData();
    }

    function convertToChartData(data) {
		var obj = {};
		obj.categories = [];
		obj.series = [{colorByPoint: true, data: []}];
		angular.forEach(data, function (v, k) {
			obj.categories.push(k);
			obj.series[0].data.push({y : parseInt(v)});
		});
		return obj;
	}

    function getChartData() {
		SharedService.getParagraphCountsByTags().then(function (data) {
			if(data.status){
				var rawData = angular.fromJson(data.data);
				var chartData = convertToChartData(rawData);
				$scope.options.Title = chartData.title = 'Paragraph Categories';
				$scope.data = chartData;
			}
		});
	}
    
    $scope.onColumnClick = function (obj) {
        var tagType = obj.currentTarget.category;
		var value = obj.currentTarget.y;

		switch (tagType){
			case 'Rule' :
				//setData(MockService.RuleChartData);
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

.controller('complianceDashboardCtrl', function($scope, $rootScope, $state, $timeout, $stateParams, SharedService, MockService) {
	$scope.initialize = function () {
		$scope.heading = {"title": "Compliance Dashboard"};
		$scope.isGridView = true;
		$scope.options = {
			handlerData : {columnClick: "onColumnClick", scope: $scope},
			colors : ['#04de72', '#00bfff', '#ffb935', '#d2d2d2'],
			dataLabels : {enabled: true},
			legend : {enabled: false}
		}
		$scope.goBusinessSegmentView();
	}

	function convertToChartData(data) {
		var obj = {};
		obj.categories = [];
		obj.series = [{colorByPoint: true, data: []}];
		angular.forEach(data, function (v, k) {
			obj.categories.push(k);
			obj.series[0].data.push({y : parseInt(v)});
		});
		return obj;
	}

	function initChecklistComplianceChart() {
		SharedService.getCompliedAndNotCompliedChecklistCounts().then(function (data) {
			if(data.status){
				var rawData = angular.fromJson(data.data);
				var chartData = convertToChartData(rawData);
				chartData.title = "Checklist Item Compliance";
				$scope.checklistComplianceOptions = initChartOptions({"title": chartData.title});
				$scope.checklistComplianceData = chartData;
			}
		});
	}
	function initComponentComplianceChart(data) {
		$scope.componentComplianceOptions = initChartOptions({"title": data.title});
		$scope.componentComplianceData = data;
	}
	function initPeriodicComplianceChart(data) {
		$scope.periodicComplianceOptions = initChartOptions({"title": data.title, "graphType": 'line'});
		$scope.periodicComplianceData = data;
	}

	function initChartOptions(op) {
		var option = {
			Title : op.title || " ",
			GraphType : op.graphType || 'column',
			handlerData : op.handlerData || {columnClick: "onColumnClick", scope: $scope},
			colors : op.colors || ['#04de72', '#00bfff', '#ffb935', '#d2d2d2'],
			dataLabels : op.dataLabels || {enabled: true},
			legend :  op.legend || {enabled: false}
		}
		return option;
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

	$('a[data-target="#summaryTab"]').on('shown.bs.tab', function (e) {      //On summary tab click
		$scope.currentView = 'SUMMARY';
		initChecklistComplianceChart();
		initComponentComplianceChart(MockService.ComponentComplianceChartData);
		initPeriodicComplianceChart(MockService.PeriodicComplianceChartData);
	})

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

	$scope.goProductView = function () {
		$scope.currentView = SharedService.currentView = 'ALL_PRODUCT';
		$state.go('landing.complianceDashboard.checklistViewer', {currentView: $scope.currentView});
	}

	$scope.goBusinessSegmentView = function () {
		$scope.currentView = SharedService.currentView = 'ALL_BUSINESS_SEGMENT';
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
	
	$scope.indutryPredicate = function (val) {
		return val['FASB Industry'];
	}

	$scope.initialize();
})

.controller('checklistViewerCtrl', function($scope, $rootScope, $state, $stateParams, $timeout, $http, SharedService, MockService, OntologyParserService){
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
		$scope.answers = {};
		configureGridOption();
		$scope.fileMimeTypes = SharedService.fileType;
	}

	function configureGridOption() {
		$scope.gridOptions = {
			columnDefs: [
                { field: 'BODY_TEXT', name: 'Checklist Item' },
                { field: 'IS_CHECKED', name: 'Checked', cellTemplate: '<div class="text-center"><i ng-if="row.entity.IS_CHECKED" class="fa fa-check text-success" aria-hidden="true"></i><i ng-if="!row.entity.IS_CHECKED" class="fa fa-times text-danger" aria-hidden="true"></i></div>' },
				{ field: 'CREATED_BY', name: 'User' },
				{ field: 'UPDATED_BY', name: 'Updated By' },
				{ field: 'ATTACHMENTINFO', name: 'Has Evidence' },
				{ field: 'paragraphs', name: 'Paragraphs' },
				{ field: 'components', name: 'Component' },
				{ field: 'IS_MANDATORY', name: 'Mandatory', cellTemplate: '<div class="text-center"><i ng-if="row.entity.IS_MANDATORY" class="fa fa-check text-success" aria-hidden="true"></i><i ng-if="!row.entity.IS_MANDATORY" class="fa fa-times text-danger" aria-hidden="true"></i></div>' },
				{ field: 'STATE', name: 'Current State' }
			],
			enableSelectAll: false,
			exporterCsvFilename: 'download.csv',
			exporterPdfDefaultStyle: {fontSize: 9},
			exporterPdfTableStyle: {margin: [30, 30, 30, 30]},
			exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, color: 'blue'},
			exporterPdfHeader: { text: "My Header", style: 'headerStyle' },
			exporterPdfFooter: function ( currentPage, pageCount ) {
				return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
			},
			exporterPdfCustomFormatter: function ( docDefinition ) {
				docDefinition.styles.headerStyle = { fontSize: 16, bold: true, margin: [30, 30, 0, 10] };
				docDefinition.styles.footerStyle = { fontSize: 10, bold: false, margin: [30, 10, 0, 30] };
				return docDefinition;
			},
			exporterPdfOrientation: 'landscape',
			exporterPdfPageSize: 'A4',
			exporterPdfMaxGridWidth: 680,
			exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
			onRegisterApi: function(gridApi){
				$scope.gridApi = gridApi;

			}
		};
	}

	$scope.viewChecklistDetails = function () {
		var checklistIds = _.pluck($scope.checkList, 'id');
		SharedService.checklistDetailsByIds(checklistIds, $scope.currentNode.elementID, $scope.currentNode.type).then(function (data) {
			if(data.status){
				$scope.gridOptions.data = angular.fromJson(data.data);
				$('#checklistModal').modal('hide');
				$('#checklistDetailsModal').modal('show');
				$timeout( function() {
					$scope.gridApi.core.handleWindowResize();
				}, 500, 10);
			}
		});
	}
	$scope.exportCsv = function(){
		var gridElement = angular.element(document.querySelectorAll(".custom-csv-link-location"));
		$scope.gridApi.exporter.csvExport( "all", "all", gridElement );
	};
	$scope.exportPdf = function(){
		$scope.gridApi.exporter.pdfExport( "all", "all" );
	};

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
				var compName = "ceclComponentsByConcept";
				SharedService.getFilteredDataByCompName(compName, nodeId).then(function (data) {
					$scope.nodeDetails = OntologyParserService.parseData(data.data);
					console.log($scope.nodeDetails);
					getGraphByConceptUri();
					SharedService.getDescriptionByUri($scope.currentNode.elementID).then(function (description) {
						$scope.currentNode.description = description;
						$('#dsViewer').modal('show');
					});
				});
				break;
			case "ALL_COMPONENT" :
				SharedService.getAllComponents().then(function (data) {
					if(data.status) {
						$scope.childNodes = angular.fromJson(data.data);
					}
				});
				break;
			case "COMPONENT" :
				/*$scope.currentNode = _.findWhere($scope.childNodes, {"elementID": nodeId});
				SharedService.getRelatedComponentsByComponent(nodeId).then(function (data) {
					prepareNodeDetails(angular.fromJson(data.data));
				});*/
				$scope.currentNode = _.findWhere($scope.childNodes, {"elementID": nodeId});
				var compName = "ceclComponentsByComponent";
				SharedService.getFilteredDataByCompName(compName, nodeId).then(function (data) {
					$scope.nodeDetails = OntologyParserService.parseData(data.data);
					console.log($scope.nodeDetails);
					getGraphByConceptUri();
					SharedService.getDescriptionByUri($scope.currentNode.elementID).then(function (description) {
						$scope.currentNode.description = description;
						$('#dsViewer').modal('show');
					});
				});
				break;
			case "ALL_BUSINESS_SEGMENT" :
				SharedService.getAllBusinessSegments().then(function (data) {
					$scope.childNodes = angular.fromJson(data.data);
				});
				break;
			case "BUSINESSSEGMENT" :
				/*$scope.currentNode = _.findWhere($scope.childNodes, {"elementID": nodeId});
				SharedService.getRelatedBusinessSegentsByBusinessSegment(nodeId).then(function (data) {
					prepareNodeDetails(angular.fromJson(data.data));
				});*/
				$scope.currentNode = _.findWhere($scope.childNodes, {"elementID": nodeId});
				var compName = "ceclComponentsBySegment";
				SharedService.getFilteredDataByCompName(compName, nodeId).then(function (data) {
					$scope.nodeDetails = OntologyParserService.parseData(data.data);
					console.log($scope.nodeDetails);
					getGraphByConceptUri();
					SharedService.getDescriptionByUri($scope.currentNode.elementID).then(function (description) {
						$scope.currentNode.description = description;
						$('#dsViewer').modal('show');
					});
				});
				break;
			case "ALL_PRODUCT" :
				SharedService.getAllProducts().then(function (data) {
					$scope.childNodes = angular.fromJson(data.data);
				});
				break;
			case "PRODUCT" :
				$scope.currentNode = _.findWhere($scope.childNodes, {"elementID": nodeId});
				var compName = "ceclComponentsByProduct";
				SharedService.getFilteredDataByCompName(compName, nodeId).then(function (data) {
					$scope.nodeDetails = OntologyParserService.parseData(data.data);
					console.log($scope.nodeDetails);
					getGraphByConceptUri();
					SharedService.getDescriptionByUri($scope.currentNode.elementID).then(function (description) {
						$scope.currentNode.description = description;
						$('#dsViewer').modal('show');
					});
				});
				break;
		}
	}

	function prepareNodeDetails(nodes, groupByField){
		var groupByField = groupByField || "type";
		nodes = _.reject(nodes, function (n) { return n.elementID === $scope.currentNode.elementID; });
		$scope.nodeDetails = _.groupBy(nodes, groupByField);
		getGraphByConceptUri();
		SharedService.getDescriptionByUri($scope.currentNode.elementID).then(function (description) {
			$scope.currentNode.description = description;
			$('#dsViewer').modal('show');
		});
	}

	function getGraphByConceptUri() {
		var rootNode = {name: $scope.currentNode.name, id: $scope.currentNode.elementID, type: $scope.currentNode.type.toLowerCase()};
		$scope.graphData = {nodes: [rootNode], edges: []};
		angular.forEach($scope.nodeDetails, function (val, key) {
			angular.forEach(val, function (aNode, idx) {
				var node = {name: aNode.name, id: aNode.id || aNode.name, type: aNode.type.toLowerCase()};
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
			} else {
				toastr.warning('No Description available..', '', {"positionClass" : "toast-top-right"});
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
		if(obj && !$scope.showLimited) {
			var val = obj.compliance || 0;
			if (val > 81) {
				obj.colorCode = "green";
			} else if (val >= 51 && val <= 80) {
				obj.colorCode = "amber";
			} else if (val > 0 && val <= 50) {
				obj.colorCode = "red";
			} else {
				obj.colorCode = "gray";
			}
			return $scope.isGridView ? ('bg-' + obj.colorCode) : ('text-' + obj.colorCode);
		}
	}

	$scope.setColorCode = function (colorCode) {
		$scope.currentColorCode = colorCode;
	}

	$scope.getChecklistByNode = function (node) {
		SharedService.getChecklistByNodeId(node).then(function (data) {
			if(data.status){
				$scope.currentNode = node;
				$scope.checkList = removeEmptyAndUnique(angular.fromJson(data.data));
				if($scope.checkList.length > 0) {
					populateAnswers($scope.checkList);
					recalculateCompliance();
					$('#dsViewer').modal('hide');
					$('#checklistModal').modal('show');
				} else
					toastr.warning('No Checklist available..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}
	function recalculateCompliance() {
		var qCount = _.size($scope.checkList);
		var checkedQuestions = _.omit($scope.answers, function(v) {return !v;});
		var aCount = _.size(checkedQuestions);
		$scope.currentNode.compliance = Math.floor((aCount*100)/qCount);
	}

	function removeEmptyAndUnique(objList) {
		objList = _.reject(objList, function (obj) {
			return (_.isEmpty(obj) || obj.id == null);
		});
		var uniqueList = _.uniq(objList, function(item, key, id) {
			return item.id;
		});
		return uniqueList;
	}
	function populateAnswers(allChecklists) {
		$scope.answers = {};
		angular.forEach(allChecklists, function (c) {
			$scope.answers[c.id] = c.isChecked || false;
		});
	}

	$scope.saveAnswers = function () {
		SharedService.addAnswer($scope.answers).then(function (data) {
			if(data.status){
				recalculateCompliance();
				$('#checklistModal').modal('hide');
				toastr.success('Saved Successfully..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	$scope.getAttachmentsByChecklistId = function (id) {
		SharedService.getAttachmentsByChecklistId(id).then(function (data) {
			if (data.status) {
				$scope.attachments = angular.fromJson(data.data);
				if($scope.attachments.length > 0){
					$('#checklistModal').modal('hide');
					$('#attachmentViewer').modal('show');
				} else {
					toastr.warning('No Evidence Found..', '', {"positionClass" : "toast-top-right"});
				}
			}
		});
	}

	$scope.viewAttachment = function (at) {
		if($scope.fileMimeTypes[at.mime] === "comment"){
			SharedService.getCommentAttachmentById(at.data_id).then(function (data) {
				if(data.status){
					$scope.viewComment = data.data;
					$('#attachmentViewer').modal('hide');
					$('#commentViewer').modal('show');
				}
			});
		} else {
			window.open("downloadAttachmentById?id=" + at.data_id);
		}
	}

	$scope.onCommentViewerClose = function () {
		$('#commentViewer').modal('hide');
		$('#attachmentViewer').modal('show');
	}

	$scope.onAttachmentViewerClose = function () {
		$('#attachmentViewer').modal('hide');
		$('#checklistModal').modal('show');
	}

	$scope.initialize();
})

.controller('documentUploaderCtrl', function ($scope, $state, $http, Upload, SharedService, MockService) {
	$scope.initialize = function(){
		$scope.fileConfig = {toc:{levels: []}, body:{}};
		$scope.documentGenres = MockService.documentGenres;
		SharedService.getRegulations().then(function (data) {
			if(data.status){
				$scope.regulations = data.data;
			}
		});
	}

	$scope.goConfigWindow = function (reg) {
		$scope.currentRegulation = reg;
		$scope.fileConfig = {toc:{levels: []}, body:{}, type: reg.key};
		if(reg.key === 'BASEL' || reg.key === 'BANKDOCUMENT'){
			getAllFeedFiles(reg.key);
		} else if(reg.key === 'CFR'){
			/*SharedService.getAllCfrDocuments().then(function (data) {
			 	$scope.cfrFiles = data;
			});*/
		} else if(reg.key === 'FASB'){
            getAllFeedFiles(reg.key);
        }
	}

	function getAllFeedFiles(regulation){
		SharedService.getAllFeedFiles(regulation).then(function (data) {
			if(data.status){
				$scope.uploadedFiles = angular.fromJson(data.data);
			}
		});
	}

	$scope.addLevel = function () {
		var aLevel = {level: $scope.fileConfig.toc.levels.length + 1};
		$scope.fileConfig.toc.levels.push(aLevel);
	}
	
	$scope.getConfig = function (fileId) {
		$scope.fileConfig = {toc:{levels: [], levelIdPrefix: fileId}, body:{}, name: fileId, type: $scope.currentRegulation.key};
		SharedService.getDocumentConfigById(fileId).then(function (data) {
			if(data.status){
				var config = angular.fromJson(data.data);
				if(config.length > 0){
					$scope.fileConfig = angular.fromJson(config[0].details);
                    $scope.fileConfig.body.hasParaIdentifier = $scope.fileConfig.body.hasParaIdentifier || false;
				} else {
					toastr.warning('No Configuration found. Please save a new one..', '', {"positionClass" : "toast-top-right"});
				}
			}
		});
	}

	$scope.saveConfig = function () {
		$scope.fileConfig.toc.levels = _.reject($scope.fileConfig.toc.levels, function (l) {
			return _.isUndefined(l.regex) || l.regex === "";
		});
		SharedService.saveDocumentConfig($scope.fileConfig).then(function (data) {
			if(data.status){
				$scope.fileConfig = {toc:{}, body:{}};
				toastr.success('Configuration saved successfully..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	$scope.runConfig = function () {
		SharedService.runConfig($scope.fileConfig).then(function(data){
			if(data.status){
				toastr.success('Parsing done successfully..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}
	
	$scope.confirmRemoveParsing = function () {
		$("#confirmationModal").modal("show");
	}

	$scope.removeParsing = function () {
		$("#confirmationModal").modal("hide");
		SharedService.removeParsing($scope.fileConfig.name).then(function(data){
			if(data.status){
				toastr.success('Removed successfully..', '', {"positionClass" : "toast-top-right"});
			} else {
				toastr.warning('Nothing to delete..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	$scope.$watch('file', function (newVal) {
		if(newVal) {
			Upload.dataUrl($scope.file, true).then(function (dataUrl) {
				var fileData = {
					name: $scope.file.name,
					mime: $scope.file.type,
					data: dataUrl
				}
				uploadAttachment(fileData);
			});
		}
	});

	function uploadAttachment(fileData) {
		fileData.regulation = $scope.currentRegulation.key;
		SharedService.writeDocument(fileData).then(function (data) {
			if(data.status){
				toastr.success('File uploaded successfully..', '', {"positionClass" : "toast-top-right"});
				getAllFeedFiles($scope.currentRegulation.key);
			} else {
				toastr.error('File uploading failed..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	$scope.goPreviousScreen = function () {
		$scope.currentRegulation = null;
	}
	
	$scope.initialize();
})

.controller('paragraphRepositoryCtrl', function($scope, $rootScope, $state, $timeout, $stateParams, SharedService, MockService) {
    $scope.initialize = function () {
        $scope.heading = {"title": "Paragraph Repository"};
        $scope.isGridView = true;
        $scope.options = {
            handlerData : {columnClick: "onColumnClick", scope: $scope},
            colors : ['#04de72', '#00bfff', '#ffb935', '#d2d2d2'],
            dataLabels : {enabled: true},
            legend : {enabled: false}
        }
        $scope.goBusinessSegmentView();
    }

    function convertToChartData(data) {
        var obj = {};
        obj.categories = [];
        obj.series = [{colorByPoint: true, data: []}];
        angular.forEach(data, function (v, k) {
            obj.categories.push(k);
            obj.series[0].data.push({y : parseInt(v)});
        });
        return obj;
    }

    function initChecklistComplianceChart() {
        SharedService.getCompliedAndNotCompliedChecklistCounts().then(function (data) {
            if(data.status){
                var rawData = angular.fromJson(data.data);
                var chartData = convertToChartData(rawData);
                chartData.title = "Checklist Item Compliance";
                $scope.checklistComplianceOptions = initChartOptions({"title": chartData.title});
                $scope.checklistComplianceData = chartData;
            }
        });
    }
    function initComponentComplianceChart(data) {
        $scope.componentComplianceOptions = initChartOptions({"title": data.title});
        $scope.componentComplianceData = data;
    }
    function initPeriodicComplianceChart(data) {
        $scope.periodicComplianceOptions = initChartOptions({"title": data.title, "graphType": 'line'});
        $scope.periodicComplianceData = data;
    }

    function initChartOptions(op) {
        var option = {
            Title : op.title || " ",
            GraphType : op.graphType || 'column',
            handlerData : op.handlerData || {columnClick: "onColumnClick", scope: $scope},
            colors : op.colors || ['#04de72', '#00bfff', '#ffb935', '#d2d2d2'],
            dataLabels : op.dataLabels || {enabled: true},
            legend :  op.legend || {enabled: false}
        }
        return option;
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

    $('a[data-target="#summaryTab"]').on('shown.bs.tab', function (e) {      //On summary tab click
        $scope.currentView = 'SUMMARY';
        initChecklistComplianceChart();
        initComponentComplianceChart(MockService.ComponentComplianceChartData);
        initPeriodicComplianceChart(MockService.PeriodicComplianceChartData);
    })

    $scope.goDocumentView = function () {
        $scope.currentView = 'DOCUMENT';
        $state.go('landing.paragraphRepository.documentViewer');
    }

    $scope.goConceptView = function () {
        $scope.currentView = SharedService.currentView = 'ALL_CONCEPT';
        $state.go('landing.paragraphRepository.paragraphViewer', {currentView: $scope.currentView});
    }

    $scope.goComponetView = function () {
        $scope.currentView = SharedService.currentView = 'ALL_COMPONENT';
        $state.go('landing.paragraphRepository.paragraphViewer', {currentView: $scope.currentView});
    }

    $scope.goProductView = function () {
        $scope.currentView = SharedService.currentView = 'ALL_PRODUCT';
        $state.go('landing.paragraphRepository.paragraphViewer', {currentView: $scope.currentView});
    }

    $scope.goBusinessSegmentView = function () {
        $scope.currentView = SharedService.currentView = 'ALL_BUSINESS_SEGMENT';
        $state.go('landing.paragraphRepository.paragraphViewer', {currentView: $scope.currentView});
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

    $scope.indutryPredicate = function (val) {
        return val['FASB Industry'];
    }

    $scope.initialize();
})

.controller('paragraphViewerCtrl', function($scope, $rootScope, $state, $stateParams, $timeout, $http, SharedService, MockService, OntologyParserService){
	$scope.initialize = function () {
		$scope.isGridView = true;
		$scope.showGraph = false;
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
		//configureGridOption();
	}

	/*function configureGridOption() {
		$scope.gridOptions = {
			columnDefs: [
				{ field: 'BODY_TEXT', name: 'Checklist Item' },
				{ field: 'IS_CHECKED', name: 'Checked', cellTemplate: '<div class="text-center"><i ng-if="row.entity.IS_CHECKED" class="fa fa-check text-success" aria-hidden="true"></i><i ng-if="!row.entity.IS_CHECKED" class="fa fa-times text-danger" aria-hidden="true"></i></div>' },
				{ field: 'CREATED_BY', name: 'User' },
				{ field: 'UPDATED_BY', name: 'Updated By' },
				{ field: 'ATTACHMENTINFO', name: 'Has Evidence' },
				{ field: 'paragraphs', name: 'Paragraphs' },
				{ field: 'components', name: 'Component' },
				{ field: 'IS_MANDATORY', name: 'Mandatory', cellTemplate: '<div class="text-center"><i ng-if="row.entity.IS_MANDATORY" class="fa fa-check text-success" aria-hidden="true"></i><i ng-if="!row.entity.IS_MANDATORY" class="fa fa-times text-danger" aria-hidden="true"></i></div>' },
				{ field: 'STATE', name: 'Current State' }
			],
			enableSelectAll: false,
			exporterCsvFilename: 'download.csv',
			exporterPdfDefaultStyle: {fontSize: 9},
			exporterPdfTableStyle: {margin: [30, 30, 30, 30]},
			exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, color: 'blue'},
			exporterPdfHeader: { text: "My Header", style: 'headerStyle' },
			exporterPdfFooter: function ( currentPage, pageCount ) {
				return { text: currentPage.toString() + ' of ' + pageCount.toString(), style: 'footerStyle' };
			},
			exporterPdfCustomFormatter: function ( docDefinition ) {
				docDefinition.styles.headerStyle = { fontSize: 16, bold: true, margin: [30, 30, 0, 10] };
				docDefinition.styles.footerStyle = { fontSize: 10, bold: false, margin: [30, 10, 0, 30] };
				return docDefinition;
			},
			exporterPdfOrientation: 'landscape',
			exporterPdfPageSize: 'A4',
			exporterPdfMaxGridWidth: 680,
			exporterCsvLinkElement: angular.element(document.querySelectorAll(".custom-csv-link-location")),
			onRegisterApi: function(gridApi){
				$scope.gridApi = gridApi;

			}
		};
	}

	$scope.viewChecklistDetails = function () {
		var checklistIds = _.pluck($scope.checkList, 'id');
		SharedService.checklistDetailsByIds(checklistIds, $scope.currentNode.elementID, $scope.currentNode.type).then(function (data) {
			if(data.status){
				$scope.gridOptions.data = angular.fromJson(data.data);
				$('#checklistModal').modal('hide');
				$('#checklistDetailsModal').modal('show');
				$timeout( function() {
					$scope.gridApi.core.handleWindowResize();
				}, 500, 10);
			}
		});
	}
	$scope.exportCsv = function(){
		var gridElement = angular.element(document.querySelectorAll(".custom-csv-link-location"));
		$scope.gridApi.exporter.csvExport( "all", "all", gridElement );
	};
	$scope.exportPdf = function(){
		$scope.gridApi.exporter.pdfExport( "all", "all" );
	};*/

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
				var compName = "ceclComponentsByConcept";
				SharedService.getFilteredDataByCompName(compName, nodeId).then(function (data) {
					$scope.nodeDetails = OntologyParserService.parseData(data.data);
					console.log($scope.nodeDetails);
					getGraphByConceptUri();
					SharedService.getDescriptionByUri($scope.currentNode.elementID).then(function (description) {
						$scope.currentNode.description = description;
						$('#dsViewer').modal('show');
					});
				});
				break;
			case "ALL_COMPONENT" :
				SharedService.getAllComponents().then(function (data) {
					if(data.status) {
						$scope.childNodes = angular.fromJson(data.data);
					}
				});
				break;
			case "COMPONENT" :
				/*$scope.currentNode = _.findWhere($scope.childNodes, {"elementID": nodeId});
				 SharedService.getRelatedComponentsByComponent(nodeId).then(function (data) {
				 prepareNodeDetails(angular.fromJson(data.data));
				 });*/
				$scope.currentNode = _.findWhere($scope.childNodes, {"elementID": nodeId});
				var compName = "ceclComponentsByComponent";
				SharedService.getFilteredDataByCompName(compName, nodeId).then(function (data) {
					$scope.nodeDetails = OntologyParserService.parseData(data.data);
					console.log($scope.nodeDetails);
					getGraphByConceptUri();
					SharedService.getDescriptionByUri($scope.currentNode.elementID).then(function (description) {
						$scope.currentNode.description = description;
						$('#dsViewer').modal('show');
					});
				});
				break;
			case "ALL_BUSINESS_SEGMENT" :
				SharedService.getAllBusinessSegments().then(function (data) {
					$scope.childNodes = angular.fromJson(data.data);
				});
				break;
			case "BUSINESSSEGMENT" :
				/*$scope.currentNode = _.findWhere($scope.childNodes, {"elementID": nodeId});
				 SharedService.getRelatedBusinessSegentsByBusinessSegment(nodeId).then(function (data) {
				 prepareNodeDetails(angular.fromJson(data.data));
				 });*/
				$scope.currentNode = _.findWhere($scope.childNodes, {"elementID": nodeId});
				var compName = "ceclComponentsBySegment";
				SharedService.getFilteredDataByCompName(compName, nodeId).then(function (data) {
					$scope.nodeDetails = OntologyParserService.parseData(data.data);
					console.log($scope.nodeDetails);
					getGraphByConceptUri();
					SharedService.getDescriptionByUri($scope.currentNode.elementID).then(function (description) {
						$scope.currentNode.description = description;
						$('#dsViewer').modal('show');
					});
				});
				break;
			case "ALL_PRODUCT" :
				SharedService.getAllProducts().then(function (data) {
					$scope.childNodes = angular.fromJson(data.data);
				});
				break;
			case "PRODUCT" :
				$scope.currentNode = _.findWhere($scope.childNodes, {"elementID": nodeId});
				var compName = "ceclComponentsByProduct";
				SharedService.getFilteredDataByCompName(compName, nodeId).then(function (data) {
					$scope.nodeDetails = OntologyParserService.parseData(data.data);
					console.log($scope.nodeDetails);
					getGraphByConceptUri();
					SharedService.getDescriptionByUri($scope.currentNode.elementID).then(function (description) {
						$scope.currentNode.description = description;
						$('#dsViewer').modal('show');
					});
				});
				break;
		}
	}

	function prepareNodeDetails(nodes, groupByField){
		var groupByField = groupByField || "type";
		nodes = _.reject(nodes, function (n) { return n.elementID === $scope.currentNode.elementID; });
		$scope.nodeDetails = _.groupBy(nodes, groupByField);
		getGraphByConceptUri();
		SharedService.getDescriptionByUri($scope.currentNode.elementID).then(function (description) {
			$scope.currentNode.description = description;
			$('#dsViewer').modal('show');
		});
	}

	function getGraphByConceptUri() {
		var rootNode = {name: $scope.currentNode.name, id: $scope.currentNode.elementID, type: $scope.currentNode.type.toLowerCase()};
		$scope.graphData = {nodes: [rootNode], edges: []};
		angular.forEach($scope.nodeDetails, function (val, key) {
			angular.forEach(val, function (aNode, idx) {
				var node = {name: aNode.name, id: aNode.id || aNode.name, type: aNode.type.toLowerCase()};
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
			} else {
				toastr.warning('No Description available..', '', {"positionClass" : "toast-top-right"});
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

	$scope.getParagraphByNode = function (node) {
		SharedService.getParagraphsByRootNodeId(node.elementID).then(function (data) {
			if(data.status){
				$scope.currentNode = node;
				$scope.paragraphs = angular.fromJson(data.data);
				if($scope.paragraphs.length > 0)
					$('#paraModal').modal("show");
				else
					toastr.warning('No Paragraph found..', '', {"positionClass" : "toast-top-right"});
			}
		});
	}

	$scope.getRegulationByParaType = function (paraType) {
		switch (paraType){
			case "PARAGRAPH": return "FASB";
			case "BASELPARAGRAPH": return "BASEL";
			case "CFRPARAGRAPH": return "FEDERAL REGISTER";
			case "BANKPARAGRAPH": return "BANK DOCUMENT";
		}
	}

	$scope.initialize();
});