angular.module('CCARApp.controllers', ['CCARApp.services', 'CCARApp.directives', 'textAngular'])

.controller('mainCtrl', function($scope, $state, $http, $stateParams, SharedService , RiskAggregateService , AlertDashboardService) {
	$scope.constants = Constant;
	$scope.userName = userName;
	$scope.viewTitle=Constant.ASSIMILATOR_TAB;
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
			case Constant.ALERT_TAB : $scope.viewTitle=Constant.ALERT_TAB;
							$state.go('landing.home');
							break;
			case Constant.ASSIMILATOR_TAB : $scope.viewTitle=Constant.ASSIMILATOR_TAB;
							$state.go('landing.risk');
							break;
			case Constant.DASHBOARD_TAB : $scope.viewTitle=Constant.DASHBOARD_TAB;
							$state.go('landing.dashboardSelector');
							break;
			case Constant.MERGE_AGGREGATOR_TAB : $scope.viewTitle=Constant.MERGE_AGGREGATOR_TAB;
							$state.go('landing.mergeAggregator');
							break;
			case Constant.ASSOCIATOR_TAB: $scope.viewTitle=Constant.ASSOCIATOR_TAB;
							$state.go('landing.aggregate');
							break;
			case Constant.SIMULATOR_TAB: $scope.viewTitle=Constant.SIMULATOR_TAB;
							$state.go('landing.simulator');
							break;
			case Constant.NEW_SIMULATOR_TAB: $scope.viewTitle=Constant.NEW_SIMULATOR_TAB;
							$state.go('landing.newsimulator');
							break;
			case Constant.GLOSSARY: $scope.viewTitle=Constant.GLOSSARY;
							$state.go('landing.glossary');
							break;
			case Constant.REPORT_TAB : $scope.viewTitle=Constant.REPORT_TAB;
							$state.go('landing.report');
							break;
			case Constant.DATASOURCE_TAB : $scope.viewTitle=Constant.DATASOURCE_TAB;
							$state.go('landing.datasource');
							break;
			case Constant.TEAM_TAB : $scope.viewTitle=Constant.TEAM_TAB;
							$state.go('landing.user');
							break;
			case Constant.SERVICE_TAB : $scope.viewTitle=Constant.SERVICE_TAB;
							$state.go('landing.combinedview');
							break;
			case Constant.ARCHIVE_TAB : $scope.viewTitle=Constant.ARCHIVE_TAB;
							$state.go('landing.pageArchive');
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

.controller('homeCtrl', function($scope, $state, $stateParams, SharedService) {

	$scope.onMenuPress = function(menuObj){
		SharedService.CurrentMenu = menuObj;
		$state.go('landing.common');
	}

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

.controller('dashboardSelectorCtrl', function($scope, $state, $compile, SharedService, MockService, NgAnimateService){
	$scope.initialize = function(){
		SharedService.localHeading = null;
		$scope.animation = NgAnimateService.animations[2];
		$scope.dashboardTemplates = [];		
		SharedService.getDashboards().then(function(data){
			NgAnimateService.lazyLoadItems($scope.dashboardTemplates, data);
		});
	}

	$scope.goTo = function(temp){
		SharedService.currentDashboard = temp;
		if(temp.type === $scope.constants.DASHBOARD.LIQUIDITY){
			$state.go("landing.dashboardGraphProfile");
			return;
		}
		$state.go("landing.dashboardProfile");
	}

	$scope.initialize();
})

.controller('dashboardProfileCtrl', function($scope, $state, $stateParams, $timeout, SharedService, MockService) {

	$scope.initialize = function(){			
		$scope.currentDate = SharedService.getCurrentDate();
		$scope.currentDashboard = SharedService.currentDashboard;
		$scope.hasDownloadSelected = false;

	  	$scope.logs = {};

	    $scope.defaults = {
	        orientation: ['top'],
	        autoResize: [true, false],
	        showCurrentTime: [false],
	        showCustomTime: [false],
	        showMajorLabels: [true, false],
	        showMinorLabels: [false],
	        align: ['left', 'center', 'right'],
	        stack: [true, false],

	        moveable: [false],
	        zoomable: [true],
	        selectable: [true],
	        editable: [false]
	    };

	    $scope.options = {
	        align: 'center', // left | right (String)
	        autoResize: true, // false (Boolean)
	        editable: false,
	        selectable: true,
	        height: '70vh',
	        margin: {
	           axis: 100,
	           item: 15
	        },
	        orientation: 'bottom',
	        showCurrentTime: true,
	        showCustomTime: false,
	        showMajorLabels: true,
	        showMinorLabels: true
	    };

		SharedService.getSingleSeriesData( $scope.currentDashboard.type ).then(function(data){
			$scope.heading = {title: data.name};
			items = data.data.data;
	    	setCurrentStatus(items);
	    	setContent(items);
	    	$scope.data = { items: items };
		});	
	}	

    var DAY = 24 * 60 * 60 * 1000;
    var items = null;

    function setCurrentStatus(items){
    	var currentDate = $scope.currentDate.month + "." + $scope.currentDate.day + "." + $scope.currentDate.year;
    	var currentStatus = _.find(items, function(i){ return (i.category === Constant.MILESTONE_TYPE.REGULATORY) || (i.category === Constant.MILESTONE_TYPE.ENTERPRISE); });
    	if(currentStatus){
    		currentStatus.content = 'Current Status(' + currentDate + ')';
    		currentStatus.start = currentDate;
    	}
    } 

    function setContent(items){
    	angular.forEach(items, function(item){
    		var text = item.content;
    		item.name = text;
    		var icon = Constant.MILESTONE_FLAG.DEFAULT;
    		switch(item.category){
    			case Constant.MILESTONE_TYPE.REGULATORY: 
    			case Constant.MILESTONE_TYPE.ENTERPRISE:    				
    			case Constant.MILESTONE_TYPE.FRY14:    
    			case Constant.MILESTONE_TYPE.MODEL:   		    				
    				icon = Constant.MILESTONE_FLAG.CURRENT_STATUS;
    				break;
    			case Constant.MILESTONE_TYPE.MEETING:     				
    				icon = Constant.MILESTONE_FLAG.MEETING;
    				break;
    			case Constant.MILESTONE_TYPE.EVENT: 
    				icon = Constant.MILESTONE_FLAG.EVENT;
    				break;
    			case Constant.MILESTONE_TYPE.REPORT: 
    				icon = Constant.MILESTONE_FLAG.REPORT;
    				break;
    			case Constant.MILESTONE_TYPE.DRY_RUN: 
    				icon = Constant.MILESTONE_FLAG.DRY_RUN;
    				break;
    			case Constant.MILESTONE_TYPE.HEAT_MAP: 
    				icon = Constant.MILESTONE_FLAG.HEAT_MAP;
    				break;
    			case Constant.MILESTONE_TYPE.CLARIFICATION_INFO: 
    				icon = Constant.MILESTONE_FLAG.CLARIFICATION_INFO;
    				break;
    			case Constant.MILESTONE_TYPE.TO_DO: 
    				icon = Constant.MILESTONE_FLAG.TO_DO;
    				break;
    			case Constant.MILESTONE_TYPE.ESCALATION: 
    				icon = Constant.MILESTONE_FLAG.ESCALATION;
    				break;
    			case Constant.MILESTONE_TYPE.SCHEDULE: 
    			case Constant.MILESTONE_TYPE.SUB_SCHEDULE: 
    				icon = Constant.MILESTONE_FLAG.SCHEDULE;
    				break;
    			case Constant.MILESTONE_TYPE.MODEL_REQUEST: 
    				icon = Constant.MILESTONE_FLAG.MODEL_REQUEST;
    				break;
    			case Constant.MILESTONE_TYPE.DATA_REQUEST: 
    				icon = Constant.MILESTONE_FLAG.DATA_REQUEST;
    				break;
    			case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.INITIATION: 
    				icon = Constant.MILESTONE_FLAG.MODEL_LIFECYCLE.INITIATION;
    				break;
    			case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.DEVELOPMENT: 
    				icon = Constant.MILESTONE_FLAG.MODEL_LIFECYCLE.DEVELOPMENT;
    				break;
    			case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.IMPLEMENTATION: 
    				icon = Constant.MILESTONE_FLAG.MODEL_LIFECYCLE.IMPLEMENTATION;
    				break;
    			case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.USE: 
    				icon = Constant.MILESTONE_FLAG.MODEL_LIFECYCLE.USE;
    				break;
    			case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.MONITORING: 
    				icon = Constant.MILESTONE_FLAG.MODEL_LIFECYCLE.MONITORING;
    				break;
    			case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.CHANGE: 
    				icon = Constant.MILESTONE_FLAG.MODEL_LIFECYCLE.CHANGE;
    				break;
    			case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.DECOMMISSION: 
    				icon = Constant.MILESTONE_FLAG.MODEL_LIFECYCLE.DECOMMISSION;
    				break;
    			case Constant.MILESTONE_TYPE.DATA_REQUEST_LIFECYCLE.SUBMITTED: 
    				icon = Constant.MILESTONE_FLAG.DATA_REQUEST_LIFECYCLE.SUBMITTED;
    				break;
    			case Constant.MILESTONE_TYPE.DATA_REQUEST_LIFECYCLE.PENDING: 
    				icon = Constant.MILESTONE_FLAG.DATA_REQUEST_LIFECYCLE.PENDING;
    				break;
    			case Constant.MILESTONE_TYPE.DATA_REQUEST_LIFECYCLE.APPROVED: 
    				icon = Constant.MILESTONE_FLAG.DATA_REQUEST_LIFECYCLE.APPROVED;
    				break;
    			case Constant.MILESTONE_TYPE.DATA_REQUEST_LIFECYCLE.REVIEWED: 
    				icon = Constant.MILESTONE_FLAG.DATA_REQUEST_LIFECYCLE.REVIEWED;
    				break;
    			case Constant.MILESTONE_TYPE.DATA_REQUEST_LIFECYCLE.COMPLETED: 
    				icon = Constant.MILESTONE_FLAG.DATA_REQUEST_LIFECYCLE.COMPLETED;
    				break;
    			default:
    				icon = Constant.MILESTONE_FLAG.DEFAULT;
    				break;
    		}
    		item.content = icon + "<br/>" + text;
    	});
    }      
    
    $scope.onSelect = function (obj) {
        var selObj = _.findWhere(items, {id: obj.items[0]});
        if($scope.hasDownloadSelected){
        	$scope.changeRingCursor();
        	$scope.viewFile();
        	return;
        }
        switch(selObj.category){
        	case Constant.MILESTONE_TYPE.MODEL_REQUEST:
        		SharedService.currentDashboard = {type:selObj.name, family:Constant.DASHBOARD.MODEL_REQUEST};
        		SharedService.localHeading = selObj.name;
				$scope.initialize();
				break;
			case Constant.MILESTONE_TYPE.DATA_REQUEST:
        		SharedService.currentDashboard = {type:Constant.DASHBOARD.DATA_REQUEST, family:Constant.DASHBOARD.DATA_REQUEST};
        		SharedService.localHeading = selObj.name;
				$scope.initialize();
				break;
        	case Constant.MILESTONE_TYPE.REGULATORY:  
        	case Constant.MILESTONE_TYPE.ENTERPRISE:
        	case Constant.MILESTONE_TYPE.FRY14:            		
        	case Constant.MILESTONE_TYPE.INITIAL_SUBMISSION:
        	case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.INITIATION:
        	case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.DEVELOPMENT:
        	case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.IMPLEMENTATION:
        	case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.USE:
        	case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.MONITORING:
        	case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.CHANGE:
        	case Constant.MILESTONE_TYPE.MODEL_LIFECYCLE.DECOMMISSION:
        		SharedService.STATUS_VIEW = { title:selObj.name || selObj.content, statusID:selObj.id, dashboardType:$scope.currentDashboard.type };
        		$state.go("landing.statusReport");
        		break;
        	case Constant.MILESTONE_TYPE.DRY_RUN: 
        		SharedService.STATUS_VIEW = { title:selObj.name || selObj.content, statusID:selObj.id, dashboardType:$scope.currentDashboard.type };
        		$state.go("landing.status");
        		break;
        	case Constant.MILESTONE_TYPE.REPORT:
        	case Constant.MILESTONE_TYPE.MEETING:
        	case Constant.MILESTONE_TYPE.EVENT:
        		$scope.selMilestone = selObj;
        		$timeout(function(){
					$('#expandedViewModal').modal('show');
        		}, 200);
        		break;
        	case Constant.MILESTONE_TYPE.HEAT_MAP:
        		$scope.selMilestone = selObj;
        		$state.go("landing.heatMap");
        		break;  
        	case Constant.MILESTONE_TYPE.SUB_SCHEDULE:
        	case Constant.MILESTONE_TYPE.DATA_REQUEST_LIFECYCLE.SUBMITTED:
        	case Constant.MILESTONE_TYPE.DATA_REQUEST_LIFECYCLE.PENDING:
        	case Constant.MILESTONE_TYPE.DATA_REQUEST_LIFECYCLE.APPROVED:
        	case Constant.MILESTONE_TYPE.DATA_REQUEST_LIFECYCLE.REVIEWED:
        	case Constant.MILESTONE_TYPE.DATA_REQUEST_LIFECYCLE.COMPLETED:
        		$scope.selMilestone = selObj;
        		SharedService.STATUS_VIEW = { title:selObj.name || selObj.content, statusID:selObj.id, dashboardType:$scope.currentDashboard.type, returnState: $state.current.name };
        		$state.go("landing.scheduleTable");     		
        		break;
        }       
    };

    $scope.onClick = function (props) {
        alert('Click');
    };

    $scope.onDoubleClick = function (props) {
        alert('DoubleClick');
    };

    $scope.rightClick = function (props) {
        alert('Right click!');
        props.event.preventDefault();
    };

    $scope.events = {
        rangechange: $scope.onRangeChange,
        rangechanged: $scope.onRangeChanged,
        onload: $scope.onLoaded,
        select: $scope.onSelect,
        click: $scope.onClick,
        doubleClick: $scope.onDoubleClick,
        contextmenu: $scope.rightClick
    };

    $scope.goPreviousScreen = function(){
    	if(SharedService.localHeading){
    		SharedService.currentDashboard = {type:Constant.DASHBOARD.MODEL};
        	SharedService.localHeading = null;
			$scope.initialize();
			return;
    	}
		$state.go('landing.dashboardSelector');
	}

	$scope.changeRingCursor = function(){
		var classes = $('#graphDiv').attr('class');
		if(classes.indexOf('download-cursor')!=-1){
			$scope.hasDownloadSelected = false;
			$('#graphDiv').removeClass('download-cursor');
			$('.item.box').removeClass('download-cursor');
			$('.fa').removeClass('download-cursor');
			$('#impactBtn').removeClass('text-green');
		}
		else{
			$scope.hasDownloadSelected = true;
			$('#graphDiv').addClass('download-cursor');
			$('.item.box').addClass('download-cursor');
			$('.fa').addClass('download-cursor');
			$('#impactBtn').addClass('text-green');
		}
	}

	$scope.viewFile = function(){
		var name = "Data Module";
		var type = "xlsx";
      	window.open("downloadFileByName/" + name + "/" + type);
    }

	$scope.initialize();
})

.controller('dashboardGraphProfileCtrl', function($scope, $rootScope, $state, $stateParams, $timeout, SharedService, MockService) {
	$scope.initialize = function () {
		$scope.currentDashboard = SharedService.currentDashboard;
		$scope.heading = {title: $scope.currentDashboard.name};
		$scope.isMapProfile = true;
		$scope.options = {
			handlerData: { click : "clickMap", scope : $scope }
		}
		if(!$scope.mapData){
			$rootScope.loader = true;
			$.getJSON('https://www.highcharts.com/samples/data/jsonp.php?filename=world-population-density.json&callback=?', function (data) {
				$scope.mapData = data;
				$rootScope.loader = false;
			});
		}
		$scope.gridOptions = {
	        enableSorting: true,
	        enableFiltering: true,
	        enableRowSelection: true,
	        multiSelect: false,
	        enableRowHeaderSelection: false,
	        selectionRowHeaderWidth: 0,
	        onRegisterApi: function( gridApi ) {
	            $scope.gridApi = gridApi;
	         }
        };
	}

	$scope.clickNode = function (nodeId) {
		if(nodeId)
			alert(nodeId);  
	}

	$scope.selectedCountry = function(selected) {
	    if (selected) {
	    	if($scope.isMapProfile){
	        	$scope.map.zoomTo(selected.originalObject.code);
	    	}
	        else{
	        	SharedService.getLiquidityFilters(selected.originalObject.code).then(function (data) {
					$scope.filters = data;
				});
	        }
	        return selected;
	    }
    };
	
	/*$scope.$watch('selectedCountry.originalObject', function (newVal) {
		if(newVal){
			if($scope.isMapProfile){
				$scope.map.zoomTo(newVal.code);
			}
			else{
				SharedService.getLiquidityFilters(newVal.code).then(function (data) {
					$scope.filters = data;
				});
			}
		}
	})*/

	$scope.clickMap = function (obj) {
		SharedService.getLiquidityFilters(obj.code).then(function (data) {
			$scope.filters = data;
			$('#filterModal').modal('show');
		});

		/*$scope.isMapProfile = false;
		$scope.options = {
			labelField:'name',
			nodeShape: 'image',
			handlerData: { click : $scope.clickNode, scope : $scope },
			nodeImageMap: SharedService.nodeImageMap,
			nodeImageField: "type",
			hier: true
	    };
	    $scope.graphData = MockService.liquidityProfile;*/
	}

	$scope.searchByFilters = function () {		
		var filters = _.map($scope.filters, function (filter,  key) { return {filterVariable: filter.filterVariable, type: filter.type, value: filter.value, prefix: filter.prefix} });
		SharedService.getLiquidityDataByFilters(filters).then(function (data) {
			$scope.isMapProfile = false;
			if(data.outputFormat === "table")
				$scope.gridOptions.data = data.data;
			console.log(data);
			$('#filterModal').modal('hide');
		});
	}

	$scope.goPreviousScreen = function(){
		if($scope.isMapProfile)
			$state.go('landing.dashboardSelector');
		else
			$scope.initialize();
	}

	$scope.initialize();
})

.controller('statusCtrl', function($scope, $state, $compile, $timeout, $stateParams, SharedService, MockService) {
	$scope.initialize = function( ){
		$scope.heading = SharedService.STATUS_VIEW;
	//	$scope.tableData = MockService.dashboardTable;
		SharedService.getAlldashboardTableData( Constant.WIDGET_NAMES.SCHEDULE_TABLE ).then(function(tableData){
			$scope.tableData = tableData
		});

		$scope.layoutGraphData = MockService.StatusData;

		$scope.cfgObj = { 	
			Title:'', 
			YAxisTitle: 'DE Completion', 
			IsStacked: true, 
			GraphType: 'stack', 
			handlerData: { columnClick: "onColumnClick", scope : $scope }
		};

		drawGraph($('#graphContainer'), $scope.layoutGraphData, $scope.cfgObj);
	}

	$scope.onColumnClick = function (obj){
		$("#expandedViewModal").modal('hide');
		SharedService.SCHEDULE_VIEW = {title: obj.currentTarget.category, scheduleID:obj.currentTarget.category.replace("Schedule ",'')};
		$state.go("landing.schedule");
	}

	function drawGraph(container, data, cfgObj) {
		var chartData = {GraphType: cfgObj.GraphType, data: data};
		var c = new GRAPH.PlotWrapper(container, cfgObj);
		c.draw(chartData);
		return c;
	}

	$scope.goPreviousScreen = function(){
		$state.go("landing.dashboardProfile");
	}

	$scope.expandGraph = function(){
		$scope.modalHead = "Status Graph";
		$("#expandedViewModal").modal('show');		
		$timeout(function(){
			drawGraph($("#expandedViewModal .modal-body"), $scope.layoutGraphData, $scope.cfgObj);
		}, 400);		
	}
	$scope.expandTable = function(){
		$scope.modalHead = "Status Table";
		var container = $("#expandedViewModal .modal-body");
		container.addClass('zero-padding');
		var template = $('<table class="table table-striped table-condenced"><thead><th colspan="6" class="cell-blue text-center border-white">Schedule Overview</th><th colspan="{{tableData.columns.length - 6}}" class="cell-blue text-center">Progress to First Submission</th></thead><thead><th colspan="6" class="cell-brown text-center border-white"></th><th colspan="{{tableData.columns.length - 6}}" class="cell-brown text-center border-white">Progress</th></thead><thead><th class="text-center text-middle border-white" ng-repeat="head in tableData.columns">{{head}}</th></thead><tbody><tr ng-repeat="row in tableData.rows"><td ng-repeat="def in row" class="cell-{{def.color}} text-center text-middle border-white">{{def.name}}</td></tr></tbody></table>');
		container.html(template);
		$compile(container.contents())($scope);
		$("#expandedViewModal").modal('show');	
	}

	$(".panel-3d").hover(function(e){
		$(this).find(".sticky-icon").show(500);
	}, function(e){
		$(this).find(".sticky-icon").hide(500);
	});

	$scope.initialize();
})

.controller('scheduleCtrl', function($scope, $state, $rootScope, $stateParams, $timeout, SharedService, MockService) {
	/*
	$scope.initialize = function(){
		$scope.heading = SharedService.SCHEDULE_VIEW;
		//$scope.tableData = MockService.ScheduleData.series[SharedService.SCHEDULE_VIEW.scheduleID].outstanding.tableData;
		$scope.scheduleData = MockService.ScheduleData;
		SharedService.getAlldashboardTableData( Constant.WIDGET_NAMES.DE_COMPLETION_OUTSTANDING ).then(function(tableData){
			$scope.tableData = tableData
			SharedService.getAlldashboardTableData( Constant.WIDGET_NAMES.DE_COMPLETION_OUTSTANDING_CHART ).then(function(chartData){
				$scope.chartData = chartData;
				$timeout(function(){
					drawChart($('#graphContainer0'), 'stack', $scope.chartData, $scope.cfgObj);
				}, 100);
			});	
		});
		$scope.cfgObj = { 	
			Title:'Business Segments', 
			YAxisTitle: 'Outstanding Balance', 
			IsStacked: true, 
			GraphType: 'stack', 
			handlerData: { columnClick: "onColumnClick", scope : $scope }
		};
		MockService.pageIdx = !MockService.pageIdx?0:MockService.pageIdx;
	}

	$scope.onTabClick = function(idx){
		$scope.tableData = {};
		//var layoutGraphData= {	'series': MockService.ScheduleData[idx].graphData.series, 'categories': MockService.ScheduleData[idx].graphData.categories};

		var widgetName = idx == 0 ? Constant.WIDGET_NAMES.DE_COMPLETION_OUTSTANDING : Constant.WIDGET_NAMES.DE_COMPLETION_OBLIGOR;
		SharedService.getAlldashboardTableData( widgetName ).then(function(tableData){
			$scope.tableData = tableData
			SharedService.getAlldashboardTableData( Constant.WIDGET_NAMES.DE_COMPLETION_OUTSTANDING_CHART ).then(function(chartData){
				$scope.chartData = chartData;
				$timeout(function(){
					drawChart($('#graphContainer' + idx), 'stack', $scope.chartData, $scope.cfgObj);
				}, 100);
			});
		});
			
	}

	$scope.activePrimaryIdx = 0;

	$scope.getPrimaryClassName = function( idx ){ 
		if( $scope.activePrimaryIdx == idx ){
			return 'navActive';
		}
		else{
			return '';
		}
	}	

	function drawChart(container, type, data, cfgObj) {
		var chartData = {GraphType: type, data: data};
		var c = new GRAPH.PlotWrapper( container , $scope.cfgObj);
		c.draw(chartData);
		return c;
	}

	$scope.showDetail = function(){
		SharedService.hideAllToolTips();
		$state.go("landing.gapdetails");
	}

	$scope.goPreviousScreen = function(){
		$state.go("landing.status");
	}

	$scope.doc = new DOC.Document($('#toCapture'));

	$scope.capturePage = function(){
		$scope.doc.capture().then(function(canvas){
			if(!MockService.archivePages)
				MockService.archivePages = [];
			var img = {id: MockService.pageIdx++, data: canvas.toDataURL("image/jpg")};
			MockService.archivePages.push(img);
		});		
	}

	$scope.initialize();
	*/
	
	
	
	
	
	$scope.initialize = function(){
		$scope.heading = SharedService.STATUS_VIEW.title;
		$scope.reports = [];
		$scope.doc = new DOC.Document($('#toCapture'));
		SharedService.getAllSeriesData( SharedService.STATUS_VIEW.widget ).then(function(data){
			$scope.reports = data;
		});
	}

	$scope.capturePage = function(){
		$rootScope.loader = true;
		$scope.doc.capture().then(function(canvas){
			if(!MockService.archivePages)
				MockService.archivePages = [];
			var img = {id: MockService.pageIdx++, data: canvas.toDataURL("image/jpg")};
			MockService.archivePages.push(img);
			$rootScope.loader = false;
		});		
	}

	$scope.showDetail = function(){
		SharedService.hideAllToolTips();
		$state.go("landing.gapdetails");
	}

	$scope.expandView = function(report){
		$scope.modalHead = report.name;
		$scope.report = report;
		var template = $('<div style="width:88%;height:66vh;" high-chart options="report.options" data="report.data"></div><div class="container-fluid margin-top-5"><i>{{report.description}}</i></div>');
		var container = $('#expandedViewModal .modal-body');
		container.html(template);
		$compile(container.contents())($scope);
		$('#expandedViewModal').modal('show');
	}

	$scope.drillDown = function(report){
		SharedService.currentReport = report;		
		$state.go('landing.ewgIssue');
	}

	$scope.goPreviousScreen = function(){
		$state.go('landing.statusReport');
	}

	$scope.initialize();
})

.controller('gapDetailsCtrl', function($scope, $state, $stateParams, SharedService, MockService) {
	$scope.initialize = function(){
		SharedService.getAlldashboardTableData( Constant.WIDGET_NAMES.GAP_DETAILS ).then(function(tableData){
			$scope.tableData = tableData
		});
		$scope.heading = {title: 'Field Gaps (Detail)'};
	}

	$scope.goPreviousScreen = function(){
		$state.go("landing.schedule");
	}

	$scope.initialize();
})

.controller('analysisCtrl', function($scope, $state, $stateParams, SharedService) {
	$scope.heading = SharedService.heading[1];
	var a = event.target;
})

.controller('commonCtrl', function($scope, $state, $stateParams, SharedService) {
	$scope.currentMenu = SharedService.CurrentMenu;
	$scope.heading = {title:SharedService.CurrentMenu.label};
})

.controller('RDACtrl', function($scope, $state, $http, $stateParams, graphService , SharedService) {
	$scope.iniitialize = function( ){
		$scope.unusedNodes = new vis.DataSet();
		$scope.breadCrumbs = [{'state':'Selection'}];
		$scope.breadCrumbsMap = {};
		$scope.currentNode = null;
		$scope.selNodes = {};
		$scope.heading = {title:"Risk Graph"}
		$scope.network = null;
		$scope.nodes = [];
		$scope.edges = [];
		$scope.aggregateState = null;
		$scope.viz = null;
		var graphData = graphService.transfromToVisFmt( SharedService.selRiskList );
		$scope.nodes = graphData.nodes ;
		$scope.edges = graphData.edges ;
		$scope.viz = new GRAPH.Viz ( $('#graphDiv') ,
		{
			labelField:'label' ,
			isRandom : true,
			edgeLabelField : 'relType',
			edgeColorMap : $scope.edgeColorMap,
			nodeImageSetter : graphService.nodeImageSetter,
			nodeShape: 'image',
            nodeImageMap: SharedService.graphImageMap,
            nodeImageField: 'image',
			handlerData: { click : $scope.clickNode , doubleClick : $scope.doubleClikNode , scope : $scope}
		});
		$scope.viz.initialize( graphData );
	}

	$scope.goPreviousScreen = function(){
		$state.go('landing.risk');
	}

	$scope.goNextScreen = function(){
		$state.go('landing.chart');
	}

	$scope.options = {
        edges: {
        },
        stabilize: true,
        physics: {barnesHut: {gravitationalConstant: 1, centralGravity: 0, springConstant: 0}},
        smoothCurves: {dynamic:false, type: "continuous",roundness:0}
    };

    $scope.isPresent = function(idx,obj){
    	if(obj.from == idx)
    		return $scope.network.nodes[obj.to] != undefined;
    	else
    		return $scope.network.nodes[obj.from] != undefined;
    }

    $scope.isAllBaseNodeSelected = function(){
    	for(var n in SharedService.selRiskList.vertices)
    	{
    		if($scope.selNodes[SharedService.selRiskList.vertices[n].id] != true)
    			return false;
    	}
    	return true;
    }

    $scope.confirmSaveAggregate = function(){
    	$('#confirmSaveModal').modal('show');
    }

    $scope.fetchGraphName = function(){
    	$('#inputModal').modal('show');
    }

    $scope.saveAggregate = function(){
    	$('#inputModal').modal('hide');
    	var graphName = $scope.graphName;
    	var tempNodeList = [];
    	var tempEdgesList = [];
    	var selectedNodeObjList = new vis.DataSet();
    	var selectedEdgeObjList = new vis.DataSet();
    	$scope.nodes.forEach( function( obj,key ){
    		var id = obj.id;
    		if($scope.selNodes[id] != true)
    			tempNodeList.push(obj);
    		else
    			selectedNodeObjList.add ( obj );
    	});


    	for( var i in tempNodeList )
			$scope.nodes.remove(tempNodeList[i].id);
		selectedEdgeObjList = $scope.viz.findEdgesforNodes( $scope.selNodes );
		var aggregateObj = { name : graphName , nodes : selectedNodeObjList , edges : selectedEdgeObjList };
		aggregateObj.baseNodeList = [];

		angular.forEach( SharedService.selRiskList.vertices , function( nodeObj  , idx){
			if( nodeObj.baseNode == "true"){
				aggregateObj.baseNodeList.push( { name : nodeObj.name });
			}
		});

		SharedService.saveAggregate(aggregateObj).then(function(data){
			if(data){
				SharedService.aggregateList.push(aggregateObj);
			}
		});
    }

	$scope.fetchData = function(nodeId){
		graphData = graphService.getGraphData(nodeId).then( function( graphData ){
			$scope.gridDataModel = graphData.gridData;
			var nodes =  graphData.visData.nodes ;
			var edges = graphData.visData.edges;
			$scope.viz.addChildNodes( nodes , nodeId);
			$scope.viz.addConnections( edges , nodeId);
			$scope.showModal = false;
		});

	}
	$scope.clickNode  = function( nodeId ){
		if( !nodeId ) return;


		var clickNode = $scope.viz.findNodeById( nodeId );
		if( clickNode == undefined || clickNode == null)
			return;

		if( $scope.nodes.get( nodeId ) == undefined)
			$scope.nodes.add( clickNode );
		if( $scope.selNodes[nodeId]  ){
			$scope.collapseNode( nodeId );
		}
		else{
			//$scope.viz.updateNodeColor( nodeId , Constant.COLOR.AQUA);
			$scope.viz.updateNodeImage( nodeId , "selected");
			$scope.selNodes[nodeId] = $scope.selNodes[nodeId] || false;
			if( $scope.selNodes[nodeId] == false ){
				$scope.breadCrumbs.push({'state':clickNode.label});
				$scope.breadCrumbsMap[clickNode.id] = $scope.breadCrumbs.length - 1;
				$scope.selNodes[nodeId] =true;
				$scope.aggregateState =	$scope.isAllBaseNodeSelected();
				$scope.fetchData( nodeId );
			}
		}
		$scope.currentSelNode = clickNode;
	}

	$scope.getNodeDescription = function(){
		if($scope.currentSelNode && $scope.currentSelNode.definition){
			$scope.dsViewerHead = $scope.currentSelNode.name + " : Definition";
			$scope.allColumnStr = $scope.currentSelNode.definition;
			$('#dsViewer').modal('show');
		}
	}

	$scope.closeDsViewer = function(){
		$scope.currentSelNode = null;
		$('#dsViewer').modal('hide');
	}

	$scope.collapseNode = function( nodeId ){
		var remNodes = $scope.viz.deleteNodeById(nodeId);
		remNodes.push( nodeId );
		angular.forEach( remNodes , function( nodeId  , idx){

			if( $scope.nodes.get( nodeId ))
				$scope.nodes.remove( nodeId );

			if( $scope.breadCrumbsMap[ nodeId ] ){
				var idx =  $scope.breadCrumbsMap[ nodeId ];
				$scope.breadCrumbs.splice( idx , 1);
			}
		});

		$scope.selNodes[nodeId] = false;
		$scope.viz.updateNodeColor( nodeId , Constant.COLOR.GREEN);
	}

	$scope.edgeColorMap = function( fromNode, toNode, relType ){
		if(relType == GRAPH.Viz.RELATIONSHIPS.ISA){
			return Constant.COLOR.ORANGE;
		}
		return Constant.COLOR.GREEN;
	}

	$scope.searchNode = function(){
		$scope.viz.selectNodeByName($scope.searchText);
		$scope.searchText = "";
	}

	$scope.iniitialize();
})

.controller('chartCtrl', function($scope, $state, $http, $stateParams, graphService , SharedService){
	$scope.heading = {title:"Risk Aggregation"}
	$scope.chartData = {};

	$scope.goPreviousScreen = function(){
		$state.go('landing.graph');
	}

	var chartCfg = {
		fillColor: "rgba(42,60,86,0.6)",
        strokeColor: "rgba(42,60,86,0.6)",
        highlightFill: "rgba(42,60,86,1)",
        highlightStroke: "rgba(42,60,86,1)"
	}

	SharedService.invokeService( 'InitialChartData' ).then(function(chartData){
		chartCfg.data = chartData.data;
		$scope.chartData.labels = chartData.labels;
		$scope.chartData.datasets = [chartCfg];
		$scope.iniitialize();
	});


  	$scope.iniitialize = function( ){
		$scope.chartData = new Chart(document.getElementById("chartDiv").getContext("2d")).Bar($scope.chartData);
	}
})

.controller('aggCtrl',function($scope, $state, $http, $stateParams, $timeout, graphService , SharedService, MockService){
	$scope.iniitialize = function( ){
		$scope.filters = SharedService.filters;
		$scope.doEnable = false;
		$scope.primaryNav = SharedService.primaryNav[1];
		$scope.selEdges = [];
		$scope.selectedAggregate = [];
		$scope.operate = {};
		$scope.savedPathList = [];
		$scope.currentAggregate = null;
		$scope.nodeInfo = {values:[]};
		$scope.deleteViews = false;
		$scope.currentAggregateData = null;
		$scope.collapseSlide = {left: false, right: false};
		$scope.collapseClasses = {left: "col-xs-2 menu-back slide-container", center: "col-xs-8",right: "col-xs-2 menu-back"};
		

		var config = {
			"check_callback" : true,
			"plugins": ["contextmenu", "sort", "wholerow", "types"],
			"handlerData": {}
		}

		$scope.jsTree = new TREE.JsTree ( $('#jstree') , config );
		$scope.jsTree.draw( [] );

		SharedService.getConfigurationList("AGGREGATION").then(function(data){
			$scope.savedAggregateList = data;
			for( var i in data ){
				$scope.savedPathList.push( { id : i , name : data[i]});
			}

		});

		$scope.datasources = MockService.DataSources;
		$scope.sorceMap = {};

		$scope.viz = new GRAPH.Viz ( $('#savedGraph') ,
		{
			labelField:'label' , isRandom : true,  edgeLabelField : 'relType', selectedEdgeColor : Constant.COLOR.AQUA,
			//nodeColorMap : $scope.getNodeColor,
			nodeImageSetter : graphService.nodeImageSetter,
			edgeColorMap : $scope.edgeColorMap,
			handlerData: { click : $scope.clickNode , doubleClick : $scope.doubleClikNode , select : $scope.onSelect, scope : $scope},
			nodeShape: 'image',
            nodeImageMap: SharedService.graphImageMap,
            nodeImageField: 'image'
		});
	}

	//new functions -->
    
	/*$scope.openModalToSave = function(){
		$('#inputModal').modal('show');
	}

	$scope.onAggregateSelect = function (aggregateId) {
			if($scope.operate[aggregateId]){
	   			$scope.selectedAggregate.push(aggregateId);
	   			console.log($scope.selectedAggregate);
			}else{
				$scope.selectedAggregate = _.reject($scope.selectedAggregate, function(idx){
					return idx === aggregateId;
				});
			}
	}
	$scope.saveAggregate = function(){
    	$('#inputModal').modal('hide');
    	var graphName = $scope.graphName;
    	var tempNodeList = [];
    	var tempEdgesList = [];
    	var selectedNodeObjList = new vis.DataSet();
    	var selectedEdgeObjList = new vis.DataSet();
    	$scope.nodes.forEach( function( obj,key ){
    		var id = obj.id;
    		if($scope.selNodes[id] != true)
    			tempNodeList.push(obj);
    		else
    			selectedNodeObjList.add ( obj );
    	});


    	for( var i in tempNodeList )
			$scope.nodes.remove(tempNodeList[i].id);
		selectedEdgeObjList = $scope.viz.findEdgesforNodes( $scope.selNodes );
		var aggregateObj = { name : graphName , nodes : selectedNodeObjList , edges : selectedEdgeObjList };
		aggregateObj.baseNodeList = [];

		angular.forEach( SharedService.selRiskList.vertices , function( nodeObj  , idx){
			if( nodeObj.baseNode == "true"){
				aggregateObj.baseNodeList.push( { name : nodeObj.name });
			}
		});

		SharedService.saveAggregate(aggregateObj).then(function(data){
			if(data){
				SharedService.aggregateList.push(aggregateObj);
			}
		});
    }*/



	$scope.leftSlideToggle = function(){
		$scope.collapseSlide.left = !$scope.collapseSlide.left;
		scaleSlides();
	}

	$scope.rightSlideToggle = function(){
		$scope.collapseSlide.right = !$scope.collapseSlide.right;
		scaleSlides();
	}

	function scaleSlides(){
		var slide = $scope.collapseSlide;
		var classes = $scope.collapseClasses;
		if(slide.left && !slide.right){
			classes.left = "hidden";
			classes.center = "col-xs-10";
			classes.right = "col-xs-2 menu-back";
		}
		else if(!slide.left && slide.right){
			classes.left = "col-xs-2 menu-back";
			classes.center = "col-xs-10";
			classes.right = "hidden";
		}
		else if(slide.left && slide.right){
			classes.left = "hidden";
			classes.center = "col-xs-12";
			classes.right = "hidden";
		}
		else{
			classes.left = "col-xs-2 menu-back";
			classes.center = "col-xs-8";
			classes.right = "col-xs-2 menu-back";
		}
		$timeout(function(){
			if($scope.graphData)
				$scope.viz.initialize( $scope.graphData );
		}, 10);
	}

	$scope.edgeColorMap = function( fromNode, toNode, relType ){
		if(fromNode && toNode){
			var edgeName = formEdgeTitle(fromNode.name, toNode.name, relType);
			if(_.contains($scope.CurrentAggrMapping.mappableEdges, edgeName)){
				var obj = {};
				obj.name = edgeName;
				obj.from = fromNode;
				obj.to = toNode;
				obj.selTables = [];
				if(_.findWhere($scope.selEdges, {name: obj.name}) == undefined){
					$scope.selEdges.push( obj );
				}
				return Constant.COLOR.AQUA;
			}
		}
		if(relType == GRAPH.Viz.RELATIONSHIPS.ISA){
			return Constant.COLOR.ORANGE;
		}
		return Constant.COLOR.GREEN;
	}

	/*$scope.getNodeColor = function( obj ){
		obj.containsValue = obj.containsValue || 'false';
		if( obj.isReport )
			return Constant.COLOR.ORANGE;
		if( obj.containsValue == 'true' )
			return Constant.COLOR.GREEN;
	}*/

	$scope.clean = function(){
		SharedService.selEdges = [];
		$scope.selEdges = [];
	}

	$scope.setDisableMapBtn = function(){
		if($scope.selEdges.length > 0)
			return false;
		return true;
	}

	$scope.showSavedGraph = function(path , e){
		$scope.clean();
		$scope.CurrentAggrMapping = {mappableEdges:[]};
		var aggrId = path.id;
		$scope.currentAggregate = path;
		$scope.doEnable = false;
		SharedService.getAggregateMapping($scope.currentAggregate.name).then(function(mapping){
			if(mapping.length > 0)
				$scope.CurrentAggrMapping = angular.fromJson( mapping[0].details );

			SharedService.getConfigurationInfo(aggrId).then(function(data){
				data = angular.fromJson(data);
				$scope.currentAggregateData = data;
				data.replace("name", "label")
				data = angular.fromJson(data);
				$scope.graphData = { nodes : data.vertices , edges : data.connecions };
				SharedService.currentBaseNodeList = data.baseNodeList;
				$scope.viz.initialize( $scope.graphData );
			});
		});

		$(e.currentTarget).parent().parent().children().removeClass('active');
		$(e.currentTarget).parent().addClass('active');
	}

	$scope.deleteSavedGraph = function(path , e){
		$scope.currentPathObj = path;
        $('#confirmModal').modal('show');
	}

	$scope.removeGraphFromDB = function(){
		var idx = _.indexOf($scope.savedPathList,$scope.currentPathObj);
		SharedService.deleteAggregate($scope.currentPathObj.name, $scope.deleteViews).then( function(){
			$scope.savedPathList.splice( idx , 1);
			$scope.deleteViews = false;
			$('#confirmModal').modal('hide');
		});
	}

	$scope.clickNode  = function( nodeId ){
		if(nodeId){
			var node = $scope.viz.findNodeById(nodeId);
			if($scope.doEnable){
				if( node.containsValue == 'true'){
					$('#filterModal').modal('show');
					//$scope.dataNode = node;
				}
			}
			if( node.isPolicy ){
				if(node.policyLink){
					window.open(node.policyLink, "", "width=950, height=600");
				}
			}
			$scope.currentSelNode = node;
		}
	}

	$scope.getNodeDescription = function(){
		if($scope.currentSelNode && $scope.currentSelNode.definition){
			$scope.dsViewerHead = $scope.currentSelNode.name + " : Definition";
			$scope.allColumnStr = $scope.currentSelNode.definition;
			$('#dsViewer').modal('show');
		}
	}

	$scope.viewDefinitionLink = function(){
		window.open($scope.currentSelNode.definitionLink);
	}

	$scope.closeDsViewer = function(){
		$scope.currentSelNode = null;
		$('#dsViewer').modal('hide');
	}

	$scope.saveDataNodeCfg = function(){
		$('#filterModal').modal('hide');
		SharedService.saveDataNodeCfg($scope.currentSelNode.name, $scope.currentAggregate.name, $scope.nodeInfo).then(function(data){
			if(data){
				//$scope.viz.updateNodeColor( $scope.currentSelNode.id , Constant.COLOR.AQUA);
				$scope.viz.updateNodeImage( $scope.currentSelNode.id , "selected" );
			}
		});
	}

	$scope.onSelect = function( edgeObj , fromNode , toNode ){
		$scope.selectedEdge = {fromNode: fromNode, toNode: toNode, relType: edgeObj.label};
		if($scope.doEnable){
			var func = $scope.viz.isEdgeSelected(edgeObj.id) ? 'unSelectEdge' : 'selectEdge';
			$scope.viz[func]( edgeObj.id );
			var fNode = $scope.viz.network.nodes[ fromNode.id ];
			if( func == 'selectEdge' ){
				fNode.included = true;
				var ed = convertToMapEdgeObj( edgeObj , fromNode , toNode );
				if(!$scope.CurrentAggrMapping.mappableEdges)
					$scope.CurrentAggrMapping.mappableEdges = [];
				if(_.indexOf($scope.CurrentAggrMapping.mappableEdges, ed.name) == -1){
					$scope.selEdges.push(ed);
					$scope.CurrentAggrMapping.mappableEdges.push(ed.name);
				}
			}
			else{
				fNode.included = false;
				edgeObj.name = formEdgeTitle(fromNode.name, toNode.name, edgeObj.label);
				$scope.selEdges = _.reject($scope.selEdges, function(obj){ return obj.name == edgeObj.name; });
				$scope.CurrentAggrMapping.mappableEdges = _.reject($scope.CurrentAggrMapping.mappableEdges, function(name){ return  name == edgeObj.name; });
			}
			$scope.viz.updateNodeImage( fromNode.id);
		}
	}

	$scope.getDataSourceDetailsOfEdge = function(){
		var edge = $scope.selectedEdge;
		var edgeName = edge.fromNode.name + "-" + edge.relType + "->" + edge.toNode.name;
		SharedService.GetConfigurationDetailsByName( edgeName ).then(function( data ){
			$scope.currentEdge = edge;
			if(data[0]){
				data[0].details = angular.fromJson( data[0].details );
				var columns = data[0].details.columns;
				$scope.dsViewerHead = "Database columns";
				$scope.allColumnStr = "";
				angular.forEach(columns, function(col){
					if($scope.allColumnStr != "")
						$scope.allColumnStr = $scope.allColumnStr + " + ";
					$scope.allColumnStr = $scope.allColumnStr + col.schemaName + "." + col.tableName + "." + col.name;
				});
				$('#dsViewer').modal('show');
			}
		});
	}

	$scope.addNodeValue = function(){
		if( $scope.dataNodeValue != undefined ){
			$scope.nodeInfo.values.push( angular.copy($scope.dataNodeValue) );
			$scope.dataNodeValue = undefined;
		}
		else
			alert( "Put a value!" );
	}

	$scope.removeValue = function( idx ){
		$scope.nodeInfo.values.splice( idx, 1 );
	}

	$scope.saveSelectedEdges = function(){
		SharedService.saveAggregateMapping([], $scope.CurrentAggrMapping.mappableEdges, $scope.currentAggregate.name).then(function(data){
			if(data){
				SharedService.selEdges = $scope.selEdges;
				SharedService.hideAllToolTips();
				$state.go("landing.mapdatasource");
			}
		});
	}

	$scope.loadTree = function(){
		if($scope.currentSelNode){
			SharedService.getAllLogicalViewsByNodeId( $scope.currentSelNode.id, $scope.currentAggregate.name ).then(function(views){
				if(views.length == 0){
					$scope.jsTree.loadData( [] );
					alert("No view available.");
					return;
				}
				else{
					$scope.views = views;
					$scope.jsTree.loadData( convertToTreeData(views) );
				}
			});
		}
	}

	function convertToTreeData(views){
		var treeData = [];
		angular.forEach(views, function(v){
			var viewDetails = angular.fromJson( v.details );
			var treeNode = {text: viewDetails.name, children: []};
			if(viewDetails.columns){
				angular.forEach(viewDetails.columns, function(a){
					var child = {text: a.name};
					treeNode.children.push( child );
				});
			}
			treeData.push(treeNode);
		});
		return treeData;
	}

	function convertToLogicalView(views){
		var logicalViewCfg = {nodeName: $scope.currentSelNode.name, aggregateName: $scope.currentAggregate.name, views: []};
		angular.forEach(views, function(obj){
			obj.details = angular.fromJson(obj.details);
			var view = {nodeName: obj.name, name: obj.details.name, shortName: SharedService.getShortName(obj.details.name), attributes: obj.details.columns, rootClass: obj.details.rootClass, logical: true};
			angular.forEach(view.attributes, function(attr){
				attr.logical = true;
				attr.viewCfgName = view.nodeName;
			});
			logicalViewCfg.views.push( view );
		});
		return logicalViewCfg;
	}

	function convertToDbView(views){
		var aggrViewConfig = {name: $scope.currentAggregate.name, nodeName: $scope.currentSelNode.name, views: [], state: $state.current.name};
		angular.forEach(views, function(v){
			var viewInfo = angular.fromJson( v.details );
			var view = {edgeName: v.name, name: viewInfo.name, shortName: SharedService.getShortName(viewInfo.name), cardinals: viewInfo.cardinals, attributes: viewInfo.columns, references: viewInfo.references};
			aggrViewConfig.views.push( view );
		});
		return aggrViewConfig;
	}
	$scope.saveGraphPosition = function(){  //Save Graph Position
		$scope.viz.storeNetworkPosition();
		$scope.currentAggregateData = angular.fromJson($scope.currentAggregateData);
		var aggregateObj = {
				name : $scope.currentAggregate.name ,
				nodes : $scope.viz.graphData.nodes  ,
				edges : $scope.viz.graphData.edges ,
				baseNodeList : $scope.currentAggregateData.baseNodeList
				};

		SharedService.saveAggregate( aggregateObj ).then( function( data ){
			alert("Position Saved");
		});
	}

	$scope.getAllViewsBySelNode = function(){  //shows logical views
		SharedService.currentSelNode = $scope.currentSelNode;
		if($scope.views){
			SharedService.logicalViewCfg = convertToLogicalView( $scope.views );
			$scope.views = null;
			SharedService.hideAllToolTips();
			$state.go("landing.logicalview");
		}
		else if($scope.currentSelNode){
			SharedService.getAllLogicalViewsByNodeId( $scope.currentSelNode.id, $scope.currentAggregate.name ).then(function(views){
				if(views.length == 0){
					alert("No view available.");
					return;
				}
				else{
					SharedService.logicalViewCfg = convertToLogicalView( views );
					SharedService.hideAllToolTips();
					$state.go("landing.logicalview");
				}
			});
		}
	}

	/*$scope.getAllViewsByAggregate = function(){  //shows dbviews
		if($scope.currentSelNode){
			SharedService.getDBViewsOfANode( $scope.currentSelNode.id ).then(function(views){
				if(views.length == 0){
					alert("No view available.");
					return;
				}
				SharedService.aggrViewConfig = convertToDbView( views );
				SharedService.hideAllToolTips();
				$state.go('landing.aggregatedbview');
			});
		}
		else{
			alert("Select a node");
			return;
		}
	}*/

	$scope.getAllViewsByAggregate = function(){  //shows dbviews & logical views
		if($scope.currentSelNode){
			SharedService.getDBViewsOfANode( $scope.currentSelNode.id ).then(function(views){

				SharedService.aggrViewConfig = null;
				if(views.length > 0){
					SharedService.aggrViewConfig = convertToDbView( views );
				}
				if(!SharedService.aggrViewConfig)
					SharedService.aggrViewConfig = {name: $scope.currentAggregate.name, nodeName: $scope.currentSelNode.name, views: [], state: $state.current.name};

				SharedService.getAllLogicalViewsByNodeId( $scope.currentSelNode.id, $scope.currentAggregate.name ).then(function(views){
					if(views.length > 0){
						var logicalViewCfg = convertToLogicalView( views );
						SharedService.aggrViewConfig.views = _.union( SharedService.aggrViewConfig.views, logicalViewCfg.views );
					}
					SharedService.hideAllToolTips();
					if(SharedService.aggrViewConfig.views.length == 0){
						alert("No view available.");
						return;
					}
					$state.go('landing.aggregatedbview');

				});


			});
		}
		else{
			alert("Select an entity");
			return;
		}
	}

	function convertToMapEdgeObj( edgeObj , fromNode , toNode ){
		var obj = {};
		obj.id = edgeObj.id;
		obj.name = formEdgeTitle(fromNode.name, toNode.name, edgeObj.label);
		obj.from = fromNode;
		obj.to = toNode;
		obj.selTables = [];
		return obj;
	}

	function formEdgeTitle(fromNodeName, toNodeName, relType){
		return fromNodeName + "-" + relType + "->" + toNodeName;
	}

	$scope.searchNode = function(){
		$scope.viz.selectNodeByName($scope.searchText);
		$scope.searchText = "";
	}

	$scope.clean();
	$scope.iniitialize();
})

.controller('aggregatedbviewCtrl',function($scope, $state, $http, $stateParams, graphService , SharedService, MockService){
	$scope.iniitialize = function( ){
		$scope.mapper = new RDAMAPPER.Mapper ( 'mapperWindow', {"OnArrowDraw":$scope.onArrowDraw, "onArrowClick":$scope.onArrowClick} );
		$scope.aggrViewConfig = SharedService.aggrViewConfig;
		$scope.dbViews = $scope.aggrViewConfig.views;
		$scope.mappableViews = [];
		$scope.rel = {};
		$scope.doEnable = false;
		$scope.selColumns = [];
		$scope.references = [];
		$scope.cardinals = SharedService.cardinals;
		$scope.cardinal = {};
		if($scope.aggrViewConfig.state == Constant.STATE_LOGICALVIEW){
			loadLogicalViewMapping();
		}
	}

	function loadLogicalViewMapping(){
		SharedService.GetConfigurationDetailsByName( $scope.aggrViewConfig.nodeName + "_" + $scope.aggrViewConfig.name ).then(function(logicalViewCfg){
			if(logicalViewCfg){
				var details = angular.fromJson(logicalViewCfg[0].details);
				var tableNames = _.uniq(_.pluck(details.columns, 'tableName'));
				angular.forEach(tableNames, function(t){
					var table = _.findWhere($scope.dbViews, {name: t});
					replaceTableNameByViewname( table );
					angular.forEach(table.attributes, function(a){
						var attr = _.findWhere(details.columns, {name: a.name, tableName: a.tableName});
						if(attr)
							a.isChecked = true;
					});
					$scope.mappableViews.push( table );
				});
				setTimeout(function(){
					setLogicalViewRelations(details.references);
				}, 10);
				$scope.viewName = details.name;
				$scope.rootClass = details.rootClass;
				$scope.selColumns = details.columns;
			}
		});
	}

	function setLogicalViewRelations(references){
		$scope.references = references;
		angular.forEach(references, function(ref){
			var fromPanel = _.findWhere($scope.mapper.getPanels(), {name: ref.columnFrom.tableName});
			var toPanel = _.findWhere($scope.mapper.getPanels(), {name: ref.columnTo.tableName});
			var pnt = {start: fromPanel.topLeft, end: toPanel.topLeft};
			$scope.mapper.currentArrow = fromPanel.initializeArrow( pnt );
			$scope.mapper.currentArrow.setSource( fromPanel );
			$scope.mapper.currentArrow.setTarget( toPanel );
			fromPanel.addArrow( $scope.mapper.currentArrow );
			fromPanel.addTarget( toPanel );
			toPanel.addSource( fromPanel );
			$scope.mapper.configurePnt( fromPanel, toPanel );
		});
	}

	$scope.onDbViewDrop = function(ui){
		var element = ui.draggable;
		var viewObj = _.findWhere( $scope.dbViews, {name: element.attr('id')} );
		replaceTableNameByViewname( viewObj );
		if(!_.findWhere($scope.mappableViews, {name: viewObj.name})){
			angular.forEach(viewObj.attributes, function(attr){
				if(attr.isChecked)
					attr.isChecked = false;
			});
			$scope.mappableViews.push( viewObj );
		}
	}

	function replaceTableNameByViewname( viewObj ){
		angular.forEach(viewObj.attributes, function(col){
			col.tableName = viewObj.name;
			col.edgeName = viewObj.edgeName;
		});
	}

	$scope.onDragStop = function(currPosition, paneIdx){
		var currentTable = _.findWhere($scope.mappableViews, {index: paneIdx});
		var currentPane = _.find($scope.mapper.getPanels(), function(obj){return obj.extraInfo.index == paneIdx});
		currentTable.topLeft = currentPane.topLeft = new Coordinate(currPosition.left, currPosition.top);
	}

	$scope.doReference = function(){
		$scope.doEnable = true;
		$scope.mapper.doReference();
	}

	$scope.onAttrCheck = function(attr){
		if(attr.isChecked){
			$scope.selColumns.push(attr);
		}
		else{
			var attrTmp = _.findWhere($scope.selColumns,{name:attr.name });
			var idx = _.indexOf($scope.selColumns, attrTmp);
			$scope.selColumns.splice(idx, 1);
		}
	}

	$scope.onArrowDraw = function(fromPanel, toPanel){
		$scope.fromPanel = fromPanel;
		$scope.toPanel = toPanel;
		$scope.doEnable = false;
		$scope.rel = {};
		$('#relationshipModal').modal('show');
	}

	$scope.onArrowClick = function(){
		$('#relationshipModal').modal('show');
	}

	$scope.addRelation = function(){
		if($scope.rel.name){
			var colFrom = angular.copy($scope.rel.from);
			var colTo = angular.copy($scope.rel.to);
			var obj = {name: $scope.rel.name, columnFrom: colFrom, columnTo: colTo,cardinality : $scope.rel.cardinality};
			var res = existAt($scope.references, obj);
			if(res.status){
				$scope.references.splice(res.idx, 1);
				$scope.references.push(obj);
			}
			else{
				$scope.references.push(obj);
			}
			changeRelColoumnColor();
			$scope.rel = {};
		}
		else{
		    $("#relName").popover();
		}
		$('#relationshipModal').modal('hide');
	}

	$scope.onRelationClick = function(rel){
		$scope.fromPanel = _.findWhere($scope.dbViews, {name: rel.columnFrom.tableName});
		$scope.toPanel = _.findWhere($scope.dbViews, {name: rel.columnTo.tableName});
		$scope.rel.from = _.findWhere($scope.fromPanel.attributes, {name: rel.columnFrom.name});
		$scope.rel.to = _.findWhere($scope.toPanel.attributes, {name: rel.columnTo.name});
		$scope.rel.name = rel.name;
		$scope.rel.cardinality = rel.cardinality;
	}

	$scope.removeRelation = function(idx){
		$scope.references.splice(idx, 1);
	}

	$scope.avoidNullValue = function(trVals){
		angular.forEach(trVals, function(td){
			td = (td == null ? "Empty" : td);
		});
	}

	function changeRelColoumnColor(){
		var fromIdx = _.indexOf($scope.fromPanel.attributes, $scope.rel.from);
		var toIdx = _.indexOf($scope.toPanel.attributes, $scope.rel.to);
		var color = SharedService.getRandomColor(0.5);
		var fromUiPane = $('#mapperWindow').find("div[index=" + $scope.fromPanel.extraInfo.index + "]");
		var toUiPane = $('#mapperWindow').find("div[index=" + $scope.toPanel.extraInfo.index + "]");
		fromUiPane.find(".list-group-item").eq(fromIdx).css('background', color);
		toUiPane.find(".list-group-item").eq(toIdx).css('background', color);
	}

	function existAt(refArr, refObj){
		var data = {status:false};
		angular.forEach(refArr, function(obj, idx){
			if(_.isEqual(obj.columnFrom, refObj.columnFrom) && _.isEqual(obj.columnTo, refObj.columnTo)){
				data.status = true;
				data.idx = idx;
				return;
			}
		});
		return data;
	}

	$scope.cleanView = function(){
		$scope.mappableViews = [];
		$scope.rel = {};
		$scope.doEnable = false;
		$scope.selColumns = [];
		$scope.references = [];
		$scope.fromPanel = null;
		$scope.toPanel = null;
		angular.forEach($scope.mapper.getPanels(), function(pane){
			pane.deleteArrow();
		});
		$scope.mapper.panels = [];
	}

	$scope.goPreviousScreen = function(){
		$state.go($scope.aggrViewConfig.state);
	}

	$scope.viewTable = function(v){
		if(v.logical){
			$scope.viewName =  v.name;
			SharedService.GetDataFromLogicalView( v.nodeName ).then(function(data){
				if(data.status){
					showGrid(data.data);
					$('#logicalViewModal').modal('show');
					setTimeout( function(){
						$('#lvGrid').w2grid( 'refresh' );
					},500);
				}
				else{
					alert("Data not found.");
				}
			});
		}
		else{
			var edgeName =  v.edgeName ;
			SharedService.fetchEdgeView( edgeName ).then(function(data){
				if(data.status){
					$scope.rdbViewData = data.data;
					$('#aggdBview').modal('show');
				}
				else{
					alert("Data not found.");
				}
			});
		}
	}

	$scope.closeTable = function(v){
		SharedService.deleteConfigurationDetailsByName(v.edgeName).then(function(data){
			if(data){
				$scope.dbViews = _.reject($scope.dbViews, function(obj){ return obj.edgeName == v.edgeName; });
			}
		});
	}

	$scope.closeTemoraryTable = function(v){
		var idx = _.indexOf($scope.mappableViews, v);
		$scope.mappableViews.splice(idx, 1);
	}

	$scope.saveView = function(){
		$('#dbViewModal').modal('hide');
		if($scope.viewName){
			var rootCls = '';
			if( $scope.references.length > 0 )
				rootCls = $scope.rootClass || $scope.references[0].columnFrom.tableName;
			else
				rootCls = $scope.selColumns[0].tableName;
			var rdbData = {
				name: $scope.aggrViewConfig.nodeName + "_" + $scope.aggrViewConfig.name,
				details: angular.toJson({
					name: $scope.viewName, cardinals: "",
					rootClass : rootCls,
					columns: $scope.selColumns,
					references: $scope.references
				})
			};
			SharedService.SaveLogicalViewOne(rdbData).then(function(data){
				if(!data)
					alert("Saving failed.");
			});
		}
	}

	function convertFromPaneObj(tabObj, paneObj){
    	tabObj.arrow = paneObj.getArrow();
    	tabObj.target = paneObj.getTarget();
    	tabObj.origins = paneObj.getOrigins();
    }

	$scope.iniitialize();
})

.controller('logicalviewCtrl',function($scope, $state, $http, $stateParams, graphService , SharedService, MockService){
	$scope.iniitialize = function(){
		$scope.logicalViewCfg = SharedService.logicalViewCfg;
		$scope.rdbViewData = {};
		$scope.selectedPanels = [];
		$scope.baseNodeList = SharedService.currentBaseNodeList;
		$scope.baseAggrNode = [];
		$scope.baseNodeMap = SharedService.baseNodeMap ;
		$scope.baseColumns = [];
	}

	$scope.onBaseNodeChange = function(v){
		$scope.baseColumns = $scope.baseNodeMap[$scope.baseNodeName] || [];
	}

	$scope.viewTable = function(v){
		$scope.viewName =  v.name;
		SharedService.GetDataFromLogicalView( v.nodeName ).then(function(data){
			if(data.status){
				if( data.tree == false ){
					showGrid(data.data);
					$('#logicalViewModal').modal('show');
					setTimeout( function(){
						$('#lvGrid').w2grid( 'refresh' );
					},500);
				}else{
					var rec = { name: 'Records', text : 'Records', children: data.data.children};
					var config = {
							"check_callback" : true,
							"plugins": ["sort", "wholerow", "types"],
							"handlerData" : { "scope": $scope, "onBaseNodeClick": "baseNodeClick", "onSelectNode": "onSelectNode", "onDeselectNode": "onDeselectNode"}
						}
					$scope.jsTree = new TREE.JsTree ( $('#lvGrid') , config );

					//$scope.jsTree.draw(rec);
					$scope.jsTree.loadData( rec );
					$('#logicalViewModal').modal('show');
				}
			}
			else{
				alert("Data not found.");
			}
		});
	}

	function convertToDbView(views, nodeName, aggrName){
		var aggrViewConfig = {name: aggrName, nodeName: nodeName, views: [], state: $state.current.name};
		angular.forEach(views, function(v){
			var viewInfo = angular.fromJson( v.details );
			var view = {edgeName: v.name, name: viewInfo.name, shortName: SharedService.getShortName(viewInfo.name), cardinals: viewInfo.cardinals, attributes: viewInfo.columns, references: viewInfo.references};
			aggrViewConfig.views.push( view );
		});
		return aggrViewConfig;
	}

	$scope.editTable = function(v){
		var nodeName = v.nodeName.split("_")[0];
		var aggrName = v.nodeName.split("_")[1];
		$scope.currentSelNode = SharedService.currentSelNode;
		SharedService.getDBViewsOfANode( $scope.currentSelNode.id ).then(function(views){
			if(views.length == 0){
				alert("No view available.");
				return;
			}
			SharedService.aggrViewConfig = convertToDbView( views, nodeName, aggrName );
			SharedService.hideAllToolTips();
			$state.go('landing.aggregatedbview');
		});
	}

	$scope.closeModal = function( modalId){
		$('#lvGrid').w2grid( 'destroy' );
		$('#'+modalId).modal('hide');
	}
	$scope.closeTable = function(v){
		SharedService.deleteConfigurationDetailsByName(v.nodeName).then(function(data){
			if(data){
				$scope.logicalViewCfg.views = _.reject($scope.logicalViewCfg.views, function(obj){ return obj.name == v.name; });
			}
		});
	}

	$scope.avoidNullValue = function(trVals){
		angular.forEach(trVals, function(td){
			td = (td == null ? "Empty" : td);
		});
	}

	$scope.goPreviousScreen = function(){
		$state.go("landing.aggregate");
	}

	$scope.getRootClass = function(){
		var viewNames = [];
		$scope.rootClasses = [];
		$scope.baseAggrNode = null;
		$scope.baseNodeName = null;
		if($scope.selectedPanels.length == 0){
			alert("No view selected!");
			return;
		}
		angular.forEach($scope.selectedPanels, function(v){
			viewNames.push( v.nodeName );
			if(_.indexOf($scope.rootClasses, v.rootClass) == -1)
				$scope.rootClasses.push( {rootCls: v.rootClass, viewName: v.name} );
		});
		$scope.requestObj = {views: viewNames};
		$('#rootClassModal').modal('show');
	}

	$scope.getCombinedView = function(){
		$('#rootClassModal').modal('hide');
		$scope.showSaveButton = true;
		SharedService.getCombinedLogicalView($scope.requestObj).then(function(data){
			if(data.status){
				var rec = { name: 'Records', text : 'Records', children: data.data.children};
				var config = {
						"check_callback" : true,
						"plugins": ["sort", "wholerow", "types"],
						"handlerData" : { "scope": $scope, "onBaseNodeClick": "baseNodeClick", "onSelectNode": "onSelectNode", "onDeselectNode": "onDeselectNode"}
					}
				$scope.jsTree = new TREE.JsTree ( $('#lvGrid') , config );

				//$scope.jsTree.draw(rec);
				$scope.jsTree.loadData( rec );
				$('#logicalViewModal').modal('show');
			}
				/*showGrid(data.data);
				$('#logicalViewModal').modal('show');
				setTimeout( function(){
					$('#lvGrid').w2grid( 'refresh' );
				},500);
			}
			else{
				alert("Data not found.");
			}*/
			
		});
	}

	
	$scope.getCombinedViewName = function(){
		$scope.viewName = null;
		$scope.showSaveButton = false;
		$('#logicalViewModal').modal('hide');
		$('#nameModal').modal('show');
	}

	$scope.saveCombinedView = function(){
		if($scope.viewName && $scope.description){
			SharedService.saveCombinedView( $scope.viewName, $scope.description, $scope.requestObj ).then( function(data){
				if(data){
					$scope.viewName = null;
					$scope.description = null;
					$('#nameModal').modal('hide');
				}
			});
		}
		else{
			alert("Enter all fields!");
		}
	}

	$scope.iniitialize();
})

.controller('mapdatasourceCtrl',function($scope, $state, $http, $stateParams, graphService , SharedService, MockService){
	$scope.iniitialize = function( ){
		$scope.mappableEdges = SharedService.mappableEdges;
		$scope.mappedNodes = [];
		$scope.selEdges = SharedService.selEdges;
		SharedService.getConfigurationList("DATASOURCE").then(function(data){
			var datasrcNodes = buildInitialNodes(data);
			var config = {
				"check_callback" : true,
				"plugins": ["checkbox", "contextmenu", "sort", "search", "wholerow", "types"],
				"checkAtLeafOnly" : true,
				"handlerData" : { "scope": $scope, "onBaseNodeClick": "baseNodeClick", "onSelectNode": "onSelectNode", "onDeselectNode": "onDeselectNode"}
			}
			$scope.jsTree = new TREE.JsTree ( $('#jstree') , config );

			$scope.jsTree.draw(datasrcNodes);
		});
	}

	$scope.getEdgeShortName = function( edge ){
		edge.shortName = SharedService.getShortName(edge.name, 25);
	}

	$scope.baseNodeClick = function(e, nodeData){
		var node = nodeData.node;
		if(node.original.isBase){
			$scope.jsTree.removeChildren(node);
			SharedService.getConfigurationInfo(node.id).then(function(data){
				var datasrcCfg = angular.fromJson(angular.fromJson(data));
				SharedService.currentDatasrc = datasrcCfg;

				SharedService.dataSourceLookUp({name : datasrcCfg.name}).then(function(data){
					var childData = data.children;
					$scope.jsTree.addChildren(childData, node.id);
					angular.forEach($scope.currentEdge.selTables, function(tab){
						$scope.jsTree.selectNodeById(tab.id);
					});
				});
			});
		}
	}

	$scope.onSelectNode = function(e, nodeData){
		if(!$scope.currentEdge){
			alert("Select an edge.");
			return;
		}
		if(nodeData.node.original.type == "table"){
			var tab = convertToTableObj(nodeData.node);
			if( !_.findWhere($scope.currentEdge.selTables, {id: tab.id}) )
				$scope.currentEdge.selTables.push(tab);
		}
	}

	$scope.onDeselectNode = function(e, nodeData){
		if(nodeData.node.original.type == "table"){
			$scope.currentEdge.selTables = _.reject($scope.currentEdge.selTables, function(obj){ return obj.id == nodeData.node.id; });
		}
	}

	$scope.exploreEdge = function(edge , e){
		SharedService.GetConfigurationDetailsByName( edge.name ).then(function( data ){
			$scope.currentEdge = edge;
			if(data[0]){
				data[0].details = angular.fromJson( data[0].details );
				$scope.currentEdge.selTables = reConstructSelTables( data[0].details );
			}
			$scope.jsTree.refresh();
			$(e.currentTarget).parent().parent().children().removeClass('active');
			$(e.currentTarget).parent().addClass('active');
		});
	}

	function reConstructSelTables( details ){
		var tables = [];
		var tableNames = _.uniq(_.pluck(details.columns, 'tableName'));
		angular.forEach( tableNames, function(tableName){
			var columns = _.where(details.columns, {tableName: tableName});
			var obj = {name: tableName, attributes: columns};
			tables.push( obj );
		});
		return tables;
	}

	function convertToTableObj(node){
		var obj = {id: node.id, name: node.text, shortName: SharedService.getShortName( node.text )};
		obj.attributes = [];
		angular.forEach(node.children, function(c){
			var chNode = $scope.jsTree.getNodeById(c);
			obj.attributes.push({name: chNode.text, shortName: SharedService.getShortName( chNode.text ), path: chNode.original.path});
		});

		return obj;
	}

	function buildInitialNodes(datasrcObj){
		var datasrcArr = [];
		angular.forEach(datasrcObj, function(name, key){
			var obj = {"id": key, "text": name, "isBase": true, "type": "datasource", "children": [{}]};
			datasrcArr.push(obj);
		});
		return datasrcArr;
	}

	$scope.closeTemoraryTable = function(v){
		$scope.currentEdge.selTables = _.reject($scope.currentEdge.selTables, function(obj){ return obj.id == v.id; });
		$scope.jsTree.deSelectNodeById(v.id);
	}

	$scope.closeTable = function( v ){
		SharedService.deleteConfigurationDetailsByName($scope.currentEdge.name).then(function(data){
			if(data){
				$scope.closeTemoraryTable(v);
			}
		});
	}

	$scope.goPreviousScreen = function(){
		$state.go("landing.aggregate");
	}

	$scope.goNext = function(){
		SharedService.currentEdge = $scope.currentEdge;
		$state.go("landing.mapdatasourcerelation");
	}

	$scope.iniitialize();
})

.controller('mapdatasourcerelationCtrl', function($scope, $state, $http, $stateParams, graphService , SharedService, MockService){
	$scope.iniitialize = function( ){
		$scope.mapper = new RDAMAPPER.Mapper ( 'mapperWindow', {"OnArrowDraw":$scope.onArrowDraw, "onArrowClick":$scope.onArrowClick} );
		$scope.currentEdge = SharedService.currentEdge;
		$scope.currentDatasrc = configCardinal(SharedService.currentDatasrc);
		$scope.currentTables = $scope.currentEdge.selTables;
		$scope.rel = {};
		$scope.doEnable = false;
		var mappedEdge = _.findWhere(SharedService.mappedEdges, {name: $scope.currentEdge.name}) || {};
		$scope.selColumns = mappedEdge.selColumns || [];
		$scope.references = mappedEdge.references || [];

		angular.forEach($scope.selColumns, function(col){
			var tab = _.findWhere($scope.currentTables, {name: col.tableName});
			if(tab){
				var attr = _.findWhere(tab.attributes, {name: col.name});
				attr.isChecked = true;
			}
		});
	}

	function clearAttrChecks(tabs){
		angular.forEach(tabs, function(tb){
			angular.forEach(tb.attributes, function(attr){
				attr.isChecked = false;
			});
		});
		return tabs;
	}

	$scope.doReference = function(){
		$scope.doEnable = true;
		$scope.mapper.doReference();
	}

	$scope.onDragStop = function(currPosition, paneIdx){
		var currentTable = _.findWhere($scope.currentTables, {index: paneIdx});
		var currentPane = _.find($scope.mapper.getPanels(), function(obj){return obj.extraInfo.index == paneIdx});
		currentTable.topLeft = currentPane.topLeft = new Coordinate(currPosition.left, currPosition.top);
	}

	$scope.onAttrCheck = function(attr){
		if(attr.isChecked){
			//var selAttr = _.findWhere($scope.selColumns, {name: attr.name});
			if(attr.path){
				//if(!selAttr)
					$scope.selColumns.push(configColumnPath(attr));
			}
			else{
				//if(!selAttr)
					$scope.selColumns.push(attr);
			}
		}
		else{
			var attrTmp = _.findWhere($scope.selColumns,{name:attr.name });
			var idx = _.indexOf($scope.selColumns, attrTmp);
			//var idx = _.indexOf($scope.selColumns, attr);
			$scope.selColumns.splice(idx, 1);
		}
	}

	function configColumnPath(attr){
		var obj = {};
		var values = [];
		if(attr.path){
			values= attr.path.split("/");
			obj={'dsName':values[0], 'schemaName': values[1], 'tableName': values[2], 'name': values[3]};
		}
		return obj;
	}

	function configCardinal(datasrc){
		var id;
		var obj = {};
		if(datasrc){
			obj = {driver: datasrc.driver, url: datasrc.connectionstr, userId: datasrc.userid, pwd: datasrc.password};
		}
		return obj;
	}

	$scope.onArrowDraw = function(fromPanel, toPanel){
		$scope.fromPanel = fromPanel;
		$scope.toPanel = toPanel;
		$scope.doEnable = false;
		$('#arrowDropModal').modal('show');
	}

	$scope.onArrowClick = function(){

	}

	$scope.saveRelation = function(){
		$('#arrowDropModal').modal('hide');
		var colFrom = configColumnPath($scope.rel.from);
		var colTo = configColumnPath($scope.rel.to);
		var obj = {columnFrom: colFrom, columnTo: colTo};
		var res = existAt($scope.references, obj);
		if(res.status){
			$scope.references.splice(res.idx, 1);
			$scope.references.push(obj);
		}
		else{
			$scope.references.push(obj);
		}
		changeRelColoumnColor();
		$scope.rel = {};
	}

	function changeRelColoumnColor(){
		var fromIdx = _.indexOf($scope.fromPanel.attributes, $scope.rel.from);
		var toIdx = _.indexOf($scope.toPanel.attributes, $scope.rel.to);
		var color = SharedService.getRandomColor(0.5);
		var fromUiPane = $('#mapperWindow').find("div[index=" + $scope.fromPanel.extraInfo.index + "]");
		var toUiPane = $('#mapperWindow').find("div[index=" + $scope.toPanel.extraInfo.index + "]");
		fromUiPane.find(".list-group-item").eq(fromIdx).css('background', color);
		toUiPane.find(".list-group-item").eq(toIdx).css('background', color);
	}

	function existAt(refArr, refObj){
		var data = {status:false};
		angular.forEach(refArr, function(obj, idx){
			if((obj.columnFrom.tableName == refObj.columnFrom.tableName) && (obj.columnTo.tableName == refObj.columnTo.tableName)){
				data.status = true;
				data.idx = idx;
				return;
			}
		});
		return data;
	}

	$scope.goPreviousScreen = function(){
		finalize();
		$state.go("landing.mapdatasource");
	}

	$scope.getView = function(){
		SharedService.requestData = {name: $scope.selColumns[0].dsName, columns: $scope.selColumns, references: $scope.references};
		SharedService.fetchViewRdb(SharedService.requestData).then(function(data){
			if(data.status){
				SharedService.rdbViewData = data.data;
				finalize();
				$state.go('landing.mapdatasourceview');
			}
			else{
				alert(data.errMessage);
			}
		});
	}

	$scope.closeTemoraryTable = function(v){
		$scope.currentTables = _.reject($scope.currentTables, function(obj){ return obj.index == v.index; });
		$scope.mapper.deletePanelByIndex(v.index);
	}

	function finalize(){
		var edge = _.findWhere(SharedService.mappedEdges, {name: $scope.currentEdge.name});
		if(!edge){
			SharedService.mappedEdges.push({name: $scope.currentEdge.name, selColumns: angular.copy($scope.selColumns), references: angular.copy($scope.references)});
		}
		else{
			edge.selColumns = angular.copy($scope.selColumns);
			edge.references = angular.copy($scope.references);
		}
		if($scope.mapper.getPanels().length > 0){
			angular.forEach($scope.mapper.getPanels(), function(obj){
				if(obj.extraInfo.index){
					var tab = _.findWhere($scope.currentTables, {"index" : obj.extraInfo.index});
					convertFromPaneObj(tab, obj);
				}
			});
		}
		$scope.selColumns = [];
		$scope.references = [];
	}

	function convertFromPaneObj(tabObj, paneObj){
    	tabObj.arrows = paneObj.getArrows();
    	tabObj.targets = paneObj.getTargets();
    	tabObj.sources = paneObj.getSources();
    }

	$scope.iniitialize();
})

.controller('mapdatasourceviewCtrl',function($scope, $state, $http, $stateParams, graphService , SharedService, MockService){
	$scope.iniitialize = function(){
		$scope.currentEdge = SharedService.currentEdge;
		$scope.rdbViewData = SharedService.rdbViewData;
		$scope.requestData = SharedService.requestData;
	}

	$scope.goPreviousScreen = function(){
		$state.go("landing.mapdatasourcerelation");
	}

	$scope.avoidNullValue = function(trVals){
		angular.forEach(trVals, function(td){
			td = (td == null ? "Empty" : td);
		})
	}

	$scope.saveView = function(){
		$('#dbViewModal').modal('hide');
		if($scope.viewName){
			$scope.requestData.name = $scope.viewName;
			var rdbData = {name: $scope.currentEdge.name, details: angular.toJson($scope.requestData)};
			SharedService.saveDbView(rdbData).then(function(data){
				if(!data)
					alert("Saving failed.");
			});
		}
	}

	$scope.iniitialize();
})

.controller('reportCtrl',function($scope, $state, $http, $stateParams, SharedService){
	$scope.initialize = function(){
		$scope.$parent.currentState = $state.current.name;
		$scope.reports = [];
		SharedService.getConfigurationList("REPORT").then(function(data){
			for( var i in data)
				$scope.reports.push( { id : i , name : data[i]} );
		});
		SharedService.pages = [];
		SharedService.pageId = 1;
		$scope.recycleIsVisible = false;
	}

	$scope.creatReport = function(){
		SharedService.currentReport = undefined;
		$state.go('landing.aggregateGroup.pagebuilder');
	}

	$scope.reportOnClick = function(reportId){
		SharedService.getConfigurationInfo(reportId).then(function(data){
			var report = angular.fromJson(angular.fromJson(data));
			SharedService.pages = report.pages;
			SharedService.pageId = report.pages.length + 1;
			SharedService.currentReport = report;
			$state.go('landing.aggregateGroup.pagebuilder');
		});
	}

	$scope.reportOnPreview = function(reportId){
		SharedService.getConfigurationInfo(reportId).then(function(data){
			var report = angular.fromJson(angular.fromJson(data));
			SharedService.currentReport = report;
			$state.go('reportPreview');
		});
	}

	$scope.removeItem = function(){
        var idx = _.indexOf($scope.reports, $scope.currentObject);
        SharedService.deleteConfiguration($scope.currentObject.id).then(function(data){
        	if(data){
        		$scope.reports.splice(idx, 1);
        	}
        });
        $('#confirmModal').modal('hide');
        $scope.recycleIsVisible = false;
    }

	$scope.initialize();
})

.controller('aggGrpCtrl',function($scope, $state, $http, $stateParams, graphService , SharedService){
	$scope.currentState = $state.current.name;
	$scope.aggregateGroupList = SharedService.aggregateGroupList;
	$scope.aggregateList = SharedService.aggregateList;
	$scope.currentName = '';
	$scope.currentTools = SharedService.pageBuilderTools;
	$scope.currentReport = SharedService.currentReport;
	$scope.pages = SharedService.pages;

	$scope.onPageDrop = function(){
		$scope.$apply(function(){
			$scope.pages.push({id: SharedService.pageId++});
		});
	}

	$scope.pageOnClick = function(page){
		SharedService.currentPage = page;
		$state.go('landing.aggregateGroup.layoutbuilder');
	}

	$scope.getReportName = function(){
		if($scope.currentReport){
			$scope.currentReport.pages = $scope.pages;
			$scope.updateReport();
			return;
		}
		$('#reportName').modal('show');
	}

	$scope.updateReport = function(){
		SharedService.saveReport($scope.currentReport).then(function(data){
			if(data){
				$state.go('landing.report');
			}
		});
	}

	$scope.saveReport = function(){
		$('#reportName').modal('hide');
		angular.forEach($scope.pages, function(page){
			if(page.layout){
				angular.forEach(page.layout.cells, function(cell){
					cell.cellElement = null;
				});
			}
		});

		var report = {id: SharedService.reportId++, name: $scope.reportName, pages: $scope.pages};
		SharedService.saveReport(report).then(function(data){
			if(data){
				$state.go('landing.report');
			}
		});
	}
})

.controller('pagebuilderCtrl',function($scope, $state, $http, $stateParams, graphService , SharedService){
	$scope.$parent.currentState = $state.current.name;
	$scope.$parent.currentTools = SharedService.pageBuilderTools;

	$scope.$parent.goPreviousScreen = function(){
		$state.go("landing.report");
	}
})

.controller('layoutbuilderCtrl',function($scope, $state, $http, $stateParams, SharedService){

	$scope.initialize = function(){
		$scope.$parent.currentTools = SharedService.layoutBuilderTools;
		$scope.$parent.currentState = $state.current.name;
		$scope.currentPage = SharedService.currentPage;
		$scope.savedAggregateList = [];
		$scope.layOutMode = '';
		$scope.selGraph = {};
		$scope.graphTypes = SharedService.reportGraphTypes;
		//$scope.layoutGraphData = SharedService.layoutGraphData;

		SharedService.getConfigurationList("AGGREGATION").then(function(data){
			$scope.savedAggregateList = data;
		});

		if($scope.currentPage.layout){
			$scope.layOutMode = $scope.currentPage.layout.name;
		}
	}

	$scope.getCurrentData = function(idx ){
		if($scope.currentPage.layout){
			return $scope.currentPage.layout.cells[idx].graphCfg;
		}
	}

	function createLayoutCell(clsName){
		var arr = [];
		var size = 1;
		switch(clsName){
			case '2by2': size = 4;
						 break;

			case '1by2':
			case '2by1': size = 3;
						 break;

			case '1by1': size = 2;
						 break;
		}

		for( var i = 0 ; i < size ; i++)
			arr.push( {index:i} );
		return arr;
	}

	$scope.onLayoutDrop = function(ui){
		var clsNames = ui.draggable.context.className.split(' ');
        var clsName = clsNames[1];
		$scope.layOutMode = clsName;
		$scope.currentPage.layout = {name: clsName};
		$scope.currentPage.layout.cells = createLayoutCell(clsName);
	}

	$scope.onAggrDrop = function( ui , cellIdx , element){
		$scope.selGraph = {};
		$scope.dropCellIdx = cellIdx;
		$scope.currentPage.layout.cells[cellIdx].cellElement = element;
		$('#AggrGrpModal').modal('show');
	}

	$scope.onImgDrop = function( ui , cellIdx , element){
		$scope.selImg = {};
		$scope.dropCellIdx = cellIdx;
		$scope.currentPage.layout.cells[cellIdx].cellElement = element;
		SharedService.getConfigurationList('IMPACT_GRAPH_IMAGE').then(function(data){
			if(data){
				$scope.impactImages = _.map(data, function(val, key){ return {'id' : key, 'name' : val}; });
				$('#ImpactImageModal').modal('show');
			}
		});
	}

	$scope.plotImpactImage = function(){
		$('#ImpactImageModal').modal('hide');
		var idx = $scope.dropCellIdx;
		var cellObj = $scope.currentPage.layout.cells[idx];
		cellObj.imgElement = $scope.selImg;
		var element = cellObj.cellElement;
		SharedService.getConfigurationInfo($scope.selImg.id).then(function(data){
			if(data){
				data = angular.fromJson( data );
				createImageElement(data, element);
			}
		});
	}

	function createImageElement(base64, element) {
		var height = element.height();
		var width = element.width();

		var imgWrapper;
		var ratio;
		var img = $("<img/>").attr("src", base64);
		var h = img[0].height;
		var w = img[0].width;

		if(w > width)
            ratio = width / w;
        else
        	ratio = w / width;

        if(ratio > 0.75){
        	img.css("height", height);
        	imgWrapper = $("<center></center>");
        }
        else{
        	img.css("width", width);
        	imgWrapper = $("<div></div>").css({'display': 'table-cell', 'vertical-align': 'middle'});
        	element.css('display', 'table');
        }

		imgWrapper.html(img);
		element.html(imgWrapper);
	}

	/*$scope.onAggrChk = function(currAggr){
		console.log($scope.selGraph.graphData);
	}*/

	$scope.initiateIndex = function(aggr, idx){
		aggr.index = idx;
	}

	$scope.saveAggregateGroup = function(){
		var name = $scope.selGraph.name;
		var idx = $scope.dropCellIdx;
		var cellObj = $scope.currentPage.layout.cells[idx];
		cellObj.name = name;
		var element = cellObj.cellElement;
		$('#AggrGrpModal').modal('hide');
		$(element).html('<div class="graphHead"></div><div class="graphContainer"></div>');

		SharedService.getChartData(idx).then(function(data){
			var layoutGraphData = data;
			var cfgObj = {Title: $scope.selGraph.name, IsStacked: true};
			cellObj.graphCfg = {name: $scope.selGraph.name, type: $scope.selGraph.type.name, config: cfgObj};
			var chart = drawGraph(element, $scope.selGraph.type.name, layoutGraphData, cfgObj);
		});
	}

	function drawGraph(element, type, data, cfgObj){
		var chartData = {GraphType: type, data: data};
		console.log(data);
		var c = new GRAPH.PlotWrapper( $(element).find('.graphContainer') , cfgObj);
		c.draw(chartData);
		return c;
	}

	$scope.$parent.goPreviousScreen = function(){
		$state.go("landing.aggregateGroup.pagebuilder");
	}

	$scope.onCellDraw = function(element, idx){
		var graphCfg = $scope.currentPage.layout.cells[idx].graphCfg;
		var imgElement = $scope.currentPage.layout.cells[idx].imgElement;
		var graphDesc = $scope.currentPage.layout.cells[idx].graphDesc;

		if(graphCfg){
			SharedService.getChartData(idx).then(function(data){
				var layoutGraphData = data;
				$(element).html('<div class="graphContainer"></div>');
				drawGraph(element, graphCfg.type, layoutGraphData, graphCfg.config);
			});
		}
		if(graphDesc){
			$(element).append('<div class="graphDesc"></div>');
			$(element).find('.graphDesc').append(graphDesc);
			textHeight = $(element).find('.graphDesc').css('height').replace('px', '');
		}
		if(imgElement){
			SharedService.getConfigurationInfo(imgElement.id).then(function(data){
				if(data){
					data = angular.fromJson( data );
					element.html(createImageElement(data, element));
				}
			});
		}
	}

	$scope.openTextEditor = function(idx){
		$scope.currIdx = idx;
		$scope.graphDesc = $scope.currentPage.layout.cells[idx].graphDesc;
		$('#textEditorModal').modal('show');
	}

	$scope.saveGraphDesc = function(){
		$('#textEditorModal').modal('hide');
		$scope.currentPage.layout.cells[$scope.currIdx].graphDesc = $scope.graphDesc;
	}

	$scope.initialize();
})

.controller('userCtrl',function($scope, $state, $http, $stateParams, SharedService){
	var Rels = null;
	$scope.initialize = function(){
		$scope.viz = {};
		Rels = GRAPH.Viz.RELATIONSHIPS;
		$scope.$parent.currentState = $state.current.name;
		$scope.$parent.currentTools = SharedService.userBuilderTools;
		$scope.users = {vertices: [], connections: []};
		SharedService.getAllUsers().then(function(data){
			var allUser = angular.fromJson(data);
			$scope.allUsers = _.reject(allUser, function(u){ return u.userid == 'root'; });
			angular.forEach($scope.allUsers, function(aUser){
				aUser.active = JSON.parse(aUser.active);
				createVizData(aUser);
			});
			drawGraph();
		});
		//$scope.users = SharedService.users;
	}
	$scope.clean = function(){
		SharedService.currentUserInfo = undefined;
	}

	$scope.onUserDrop = function(ui){
		$scope.clean();
		var clsNames = ui.draggable.context.className.split(' ');
        var clsName = clsNames[1];
		if(clsName == "user"){
			SharedService.currentUser = null;
			SharedService.currentUserInfo = null;
			$state.go("landing.userdetail");
		}
		else
			$state.go("landing.usergroupdetail");
	}

	function drawGraph(){
        $scope.viz = new GRAPH.Viz ( $('#graphDiv') , { labelField:'name' , nodeColor : 'lightblue' , edgeLabelField : 'relType', handlerData: { click : nodeClick }, nodeShape : 'image', nodeImageMap : SharedService.nodeImageMap, nodeImageField : "status", extraInfo : "extraInfo"} );
        $scope.viz.initialize( {nodes: $scope.users.vertices , edges : $scope.users.connections });
    }

    function nodeClick(nodeId){
    	if(nodeId){
	    	var selNode = $scope.viz.findNodeById(nodeId);
	    	var user = $scope.viz.getExtraInfo(nodeId);
	    	SharedService.currentUser = selNode;
	    	SharedService.currentUserInfo = user;
			if(user.type == "user")
				$state.go("landing.userdetail");
			else
				$state.go("landing.usergroupdetail");
		}
    }

    $scope.saveUserGraph = function(){
    	SharedService.saveUserGraph($scope.users).then(function(data){
    		if(data){
    			alert(data);
    		}
    	});
    }

    function createVizData(userInfo){
		var newUser = createNode(userInfo)		//$scope.userInfo = modal ng-model
		newUser.extraInfo.groups = $scope.selectedGroups;
		$scope.users.vertices.push(newUser);
		angular.forEach(newUser.extraInfo.groups, function(grpId){
			var group = _.findWhere($scope.users.vertices, {"id": grpId});
			group.extraInfo.users.push(newUser.id);

			var edge = createEdge(grpId, newUser.id, "hasA");
			$scope.users.connections.push(edge);
		});
	}

	function createNode(userDetail){
		var id = userDetail.userid + userDetail.password;
		userDetail.type = "user";
		userDetail.groups = [];
        var node = {"id": id , "name": userDetail.name, "status": userDetail.type, "extraInfo": userDetail};
        return node;
    }

    function createEdge(from, to, relType){
        var edge = {"from": from, "to": to, "relType": relType};
        return edge;
    }

	$scope.initialize();
})

.controller('userDetailCtrl',function($scope, $state, $http, $stateParams, SharedService){
	$scope.initialize = function(){
		$scope.$parent.currentState = $state.current.name;
		$scope.user = SharedService.currentUser || {};
		$scope.userInfo = SharedService.currentUserInfo || {};
		$scope.users = SharedService.users;
		$scope.onlyUsers = _.where($scope.users.vertices, {status: 'user'});
		$scope.onlyGroups = _.where($scope.users.vertices, {status: 'usergroup'});
		$scope.selectedGroups = angular.copy($scope.userInfo.groups) || [];
		$scope.rejctedGroups = [];
		setChkBoxes();
	}

	$scope.clean = function(){
		SharedService.currentUser = undefined;
		SharedService.currentUserInfo = undefined;
	}

	$scope.saveUser = function(){
		if(!SharedService.currentUser){
			SharedService.createUser($scope.userInfo).then(function(data) {
				if(data){
					$scope.goPreviousScreen();
				}
			});
		}
		else{
			$scope.userInfo.groups = $scope.selectedGroups;
			angular.forEach($scope.rejctedGroups, function(grpId){
				var group = _.findWhere($scope.users.vertices, {"id": grpId});
				var idx = _.indexOf(group.extraInfo.users, $scope.user.id);
				group.extraInfo.users.splice(idx, 1);

				var edge = _.findWhere($scope.users.connections, {"from": grpId, "to": $scope.user.id});
				if(edge){
					idx = _.indexOf($scope.users.connections, edge);
					$scope.users.connections.splice(idx, 1);
				}
			});
			angular.forEach($scope.userInfo.groups, function(grpId){
				var group = _.findWhere($scope.users.vertices, {"id": grpId});
				var idx = _.indexOf(group.extraInfo.users, $scope.user.id);
				if(idx == -1)
					group.extraInfo.users.push($scope.user.id);

				var edge = _.findWhere($scope.users.connections, {"from": grpId, "to": $scope.user.id});
				if(!edge){
					edge = createEdge(grpId, $scope.user.id, "hasA");
					$scope.users.connections.push(edge);
				}
			});
			$scope.goPreviousScreen();
		}
		$scope.clean();
	}

	$scope.deleteUser = function(userId){
		SharedService.deleteUser(userId).then(function(data){
			if(data){
				$scope.goPreviousScreen();
			}
		});
	}

	$scope.goPreviousScreen = function(){
		$state.go("landing.user");
	}

	$scope.onGroupChk = function(group){
		if(group.isChecked){
			$scope.selectedGroups.push(group.id);
		}
		else{
			var idx = _.indexOf($scope.selectedGroups, group.id);
			if(idx != -1)
				$scope.selectedGroups.splice(idx, 1);
			$scope.rejctedGroups.push(group.id);
		}
	}

	function setChkBoxes(){
		angular.forEach($scope.onlyGroups, function(grp){
			var idx = _.indexOf($scope.userInfo.groups, grp.id);
			if(idx != -1){
				grp.isChecked = true;
			}
			else{
				grp.isChecked = false;
			}
		});
	}

	$scope.initialize();
})

.controller('userGroupDetailCtrl',function($scope, $state, $http, $stateParams, SharedService){
	$scope.initialize = function(){
		$scope.$parent.currentState = $state.current.name;
		$scope.user = SharedService.currentUser || {};
		$scope.userInfo = SharedService.currentUserInfo || {};
		$scope.users = SharedService.users;
		$scope.onlyUsers = _.where($scope.users.vertices, {status: 'user'});
		$scope.onlyGroups = _.where($scope.users.vertices, {status: 'usergroup'});
		$scope.selectedGroups = angular.copy($scope.userInfo.groups) || [];
		$scope.rejctedGroups = [];
		$scope.selectedUsers = angular.copy($scope.userInfo.users) || [];
		$scope.rejctedUsers = [];
		setChkBoxes();
	}

	$scope.clean = function(){
		SharedService.currentUser = undefined;
		SharedService.currentUserInfo = undefined;
	}

	$scope.saveUser = function(){
		if(!SharedService.currentUser){
			var newUser = createNode($scope.userInfo)		//$scope.userInfo = modal ng-model
			newUser.extraInfo.groups = $scope.selectedGroups;
			newUser.extraInfo.users = $scope.selectedUsers;
			$scope.users.vertices.push(newUser);
			angular.forEach(newUser.extraInfo.groups, function(grpId){
				var group = _.findWhere($scope.users.vertices, {"id": grpId});
				group.extraInfo.users.push(newUser.id);

				var edge = createEdge(grpId, newUser.id, "hasA");
				$scope.users.connections.push(edge);
			});
			angular.forEach(newUser.extraInfo.users, function(usrId){
				var user = _.findWhere($scope.users.vertices, {"id": usrId});
				user.extraInfo.groups.push(newUser.id);

				var edge = createEdge(newUser.id, usrId, "hasA");
				$scope.users.connections.push(edge);
			});
		}
		else{
			$scope.userInfo.groups = $scope.selectedGroups;
			$scope.userInfo.users = $scope.selectedUsers;
			angular.forEach($scope.rejctedGroups, function(grpId){
				var group = _.findWhere($scope.users.vertices, {"id": grpId});
				var idx = _.indexOf(group.extraInfo.users, $scope.user.id);
				group.extraInfo.users.splice(idx, 1);

				var edge = _.findWhere($scope.users.connections, {"from": grpId, "to": $scope.user.id});
				if(edge){
					idx = _.indexOf($scope.users.connections, edge);
					$scope.users.connections.splice(idx, 1);
				}
			});
			angular.forEach($scope.userInfo.groups, function(grpId){
				var group = _.findWhere($scope.users.vertices, {"id": grpId});
				var idx = _.indexOf(group.extraInfo.users, $scope.user.id);
				if(idx == -1)
					group.extraInfo.users.push($scope.user.id);

				var edge = _.findWhere($scope.users.connections, {"from": grpId, "to": $scope.user.id});
				if(!edge){
					edge = createEdge(grpId, $scope.user.id, "hasA");
					$scope.users.connections.push(edge);
				}
			});

			angular.forEach($scope.rejctedUsers, function(usrId){
				var user = _.findWhere($scope.users.vertices, {"id": usrId});
				var idx = _.indexOf(user.extraInfo.groups, $scope.user.id);
				user.extraInfo.groups.splice(idx, 1);

				var edge = _.findWhere($scope.users.connections, {"from": $scope.user.id, "to": usrId});
				if(edge){
					idx = _.indexOf($scope.users.connections, edge);
					$scope.users.connections.splice(idx, 1);
				}
			});
			angular.forEach($scope.userInfo.users, function(usrId){
				var user = _.findWhere($scope.users.vertices, {"id": usrId});
				var idx = _.indexOf(user.extraInfo.groups, $scope.user.id);
				if(idx == -1)
					user.extraInfo.groups.push($scope.user.id);

				var edge = _.findWhere($scope.users.connections, {"from": $scope.user.id, "to": usrId});
				if(!edge){
					edge = createEdge($scope.user.id, usrId, "hasA");
					$scope.users.connections.push(edge);
				}
			});
		}
		$scope.clean();
		$scope.goPreviousScreen();
	}

	$scope.goPreviousScreen = function(){
		$state.go("landing.user");
	}

	$scope.onGroupChk = function(group){
		if(group.isChecked){
			$scope.selectedGroups.push(group.id);
		}
		else{
			var idx = _.indexOf($scope.selectedGroups, group.id);
			if(idx != -1)
				$scope.selectedGroups.splice(idx, 1);
			$scope.rejctedGroups.push(group.id);
		}
	}

	$scope.onUserChk = function(user){
		if(user.isChecked){
			$scope.selectedUsers.push(user.id);
		}
		else{
			var idx = _.indexOf($scope.selectedUsers, user.id);
			if(idx != -1)
				$scope.selectedUsers.splice(idx, 1);
			$scope.rejctedUsers.push(user.id);
		}
	}

	function createNode(userDetail){
		var id = userDetail.userid + userDetail.password;
		userDetail.type = "usergroup";
		userDetail.users = [];
		userDetail.groups = [];
        var node = {"id": id , "name": userDetail.name, "status": userDetail.type, "extraInfo": userDetail};
        return node;
    }

    function createEdge(from, to, relType){
        var edge = {"from": from, "to": to, "relType": relType};
        return edge;
    }

	function setChkBoxes(){
		angular.forEach($scope.onlyGroups, function(grp){
			var idx = _.indexOf($scope.userInfo.groups, grp.id);
			if(idx != -1){
				grp.isChecked = true;
			}
			else{
				grp.isChecked = false;
			}
		});
		angular.forEach($scope.onlyUsers, function(usr){
			var idx = _.indexOf($scope.userInfo.users, usr.id);
			if(idx != -1){
				usr.isChecked = true;
			}
			else{
				usr.isChecked = false;
			}
		});
	}

	$scope.initialize();
})

.controller('datasourceCtrl',function($scope, $state, $http, $stateParams, SharedService, MockService){
	$scope.initialize = function(){
		$scope.$parent.currentState = $state.current.name;
		$scope.dataSourceList = [];
		SharedService.getConfigurationList("DATASOURCE").then(function(data){
			for( var i in data)
				$scope.dataSourceList.push( { id : i , name : data[i]} );
		});
		$scope.datasrcTypes = MockService.DataSourceTypes;
		$scope.datasrcDrivers = MockService.datasrcDrivers;
	}
	$scope.value2 =true;
	$scope.value1 =true;
	$scope.addDS = false;


	$scope.datasrcchng = function(){
		debugger;
		if($scope.datasource.type == "db"){
			$scope.value2 =true;
			$scope.value1 =false;
			$scope.disableSave = false;
		}
		if($scope.datasource.type == "excel"){
			$scope.value1 =true;
			$scope.value2 =false;
			$scope.disableSave = false;
		}

	}


	$scope.clean = function(){
		$scope.datasource = {};
		$scope.disableSave = true;
	}

	$scope.addDatasrc = function() {
		$scope.addDS = true;
		$scope.datasource = {};
		$('#datasrcModal').modal('show');
	}

	$scope.getDatasrcInfo = function(dsObj){
		SharedService.getConfigurationInfo(dsObj.id).then(function(data){
			$scope.datasource = angular.fromJson(angular.fromJson(data));
			$scope.datasrcchng();
			$('#datasrcModal').modal('show');
		});
	}

	$scope.saveDatasrc = function(){
		//if($scope.datasource.type=="")
		SharedService.saveDatasource($scope.datasource).then(function(data){
			if(data && $scope.addDS){
				$scope.datasource.id = data;
				$scope.dataSourceList.push( angular.copy( $scope.datasource ));
				$scope.clean();
				$scope.addDS = false;
			}

		});
        $('#datasrcModal').modal('hide');
    }

    $scope.removeDatasrc = function(datasrc){
        $scope.currentDSObj = datasrc;
        $('#confirmModal').modal('show');
    }

    $scope.removeDataSource = function(){
    	var idx = _.indexOf($scope.dataSourceList,$scope.currentDSObj);
    	SharedService.deleteConfiguration($scope.currentDSObj.id).then(function(data){
    		if(data){
    			$scope.dataSourceList.splice( idx , 1);
    		}
    	});
    	$('#confirmModal').modal('hide');
    }

    $scope.testConnection = function(){
    	SharedService.testDatasourceConn($scope.datasource).then(function(data){
    		var response = data;
    		if(response.status){
    			alert("Connection succeded");
    			$scope.disableSave = false;
    			return;
    		}
    		alert(response.errorMessage);
    	});
    }

	$scope.clean();
	$scope.initialize();
})

.controller('reportPreviewCtrl',function($scope, $state, $http, $stateParams, SharedService, MockService){
	$scope.initialize = function(){
		$scope.currentReport = SharedService.currentReport;
		$scope.pages = $scope.currentReport.pages;
		$scope.pageIdx = 0;
	}

	$scope.onCellDraw = function(element, idx){
		var cellObj = $scope.pages[$scope.pageIdx].layout.cells[idx];
		var graphCfg = cellObj.graphCfg;
		var graphDesc = cellObj.graphDesc;
		var imgElement = cellObj.imgElement;
		var eleHeight = $(element).css('height').replace('px', '');
		var graphHeight = 0;
		var textHeight = 0;

		if(graphCfg){
			SharedService.getChartData(idx).then(function(data){
				var layoutGraphData = data;
				$(element).prepend('<div class="graphContainer"></div>');
				var chart = drawGraph(element, graphCfg.type, layoutGraphData, graphCfg.config);
				cellObj.graphSvg = chart.svgData;
				var h = eleHeight - textHeight - 20;
				$(element).find('.graphContainer').css('height', h+'px');
			});
		}
		if(graphDesc){
			$(element).append('<div class="graphDesc"></div>');
			$(element).find('.graphDesc').append(graphDesc);
			textHeight = $(element).find('.graphDesc').css('height').replace('px', '');
		}
		if(imgElement){
			SharedService.getConfigurationInfo(imgElement.id).then(function(data){
				if(data){
					data = angular.fromJson( data );
					element.html(createImageElement(data, element));
				}
			});
		}

		var i = JSON.parse(idx) + 1;
		if($scope.pages[$scope.pageIdx].layout.cells.length == i){
			$scope.pageIdx++;
		}
	}

	function createImageElement(base64, element) {
		var height = element.height();
		var width = element.width();

		var imgWrapper;
		var ratio;
		var img = $("<img/>").attr("src", base64);
		var h = img[0].height;
		var w = img[0].width;

		if(w > width)
            ratio = width / w;
        else
        	ratio = w / width;

        if(ratio > 0.75){
        	img.css("height", height);
        	imgWrapper = $("<center></center>");
        }
        else{
        	img.css("width", width);
        	imgWrapper = $("<div></div>").css({'display': 'table-cell', 'vertical-align': 'middle'});
        	element.css('display', 'table');
        }

		imgWrapper.html(img);
		element.html(imgWrapper);
	}

	$scope.goPreviousScreen = function(){
		$state.go('landing.report');
	}

	$scope.downloadReport = function(){
		SharedService.generateReportPdf($scope.currentReport).then(function(data){
			SharedService.downloadReport(data.reportId);
		});
	}

	function drawGraph(element, type, data, cfgObj){
		var chartData = {GraphType: type, data: data};
		console.log(data);
		var c = new GRAPH.PlotWrapper( $(element).find('.graphContainer') , cfgObj);
		c.draw(chartData);
		return c;
	}

	$scope.initialize();
})

.controller('combinedviewCtrl',function($scope, $state, $http, $stateParams, SharedService, MockService){
	$scope.initialize = function(){
		var config = {
			"check_callback" : true,
			"plugins": ["sort", "wholerow", "types"],
			"handlerData" : { "scope": $scope, "onBaseNodeClick": "baseNodeClick", "onSelectNode": "onSelectNode", "onDeselectNode": "onDeselectNode"}
		}
		$scope.jsTree = new TREE.JsTree ( $('#lvGrid') , config );
		$scope.combinedViews = [];
		SharedService.getCombinedViews().then(function(data){
			if(data){
				angular.forEach(data, function(v){
					var details = angular.fromJson( v.details );
					var link = $scope.BaseURL + Constant.LOGICAL_VIEW_JSON_FUNCTION + '/' + v.name;
					$scope.combinedViews.push({name: v.name, description: details.description, requestCfg: details.requestCfg, link: link});
				});
			}
		});
	}

	$scope.getCombinedView = function(v){
		$scope.viewName = v.name;
		SharedService.getCombinedLogicalView(v.requestCfg).then(function(data){
			if(data.status){
				var rec = { name: 'Records', text : 'Records', children: data.data.children};
				$scope.jsTree.loadData( rec );
				$('#logicalViewModal').modal('show');
			}
		});
	}

	$scope.confirmRemove = function(v){
		$scope.currentCombinedView = v;
        $('#confirmModal').modal('show');
	}

    $scope.removeCombinedView = function(){
    	SharedService.deleteConfigurationDetailsByName($scope.currentCombinedView.name).then(function(data){
			if(data){
				$scope.combinedViews = _.reject($scope.combinedViews, function(obj){ return obj.name == $scope.currentCombinedView.name; });
			}
		});
    	$('#confirmModal').modal('hide');
    }

    $scope.goLink = function(link){
    	window.open(link, "", "width=900, height=450");
    }

	$scope.initialize();
})

.controller('simulateCtrl',function($scope, $state, $http, $stateParams, SharedService, RiskAggregateService, MockService){
	$scope.initialize = function(){
		$scope.viewTitle = Constant.SIMULATOR_TAB;
		$scope.baseNode = {};
		$scope.simulateOptions = MockService.simulateOptions;
		RiskAggregateService.loadInitialState().then( function( data ) {
			$scope.dashboardRiskMenu = data;
		});
	}

	$scope.goRiskGraph = function(){
		if($scope.baseNode.node){
			SharedService.graphData = {vertices: [$scope.baseNode.node]};
			$state.go('landing.simulatorGraph');
		}
		else{
			alert("Select a node!");
		}
	}

	$scope.initialize();
})

.controller('simulategraphCtrl',function($scope, $state, $http, $stateParams, $timeout, SharedService, MockService, graphService){
	$scope.initialize = function(){
		$scope.viewTitle = "Impact Simulator";
		$scope.breadCrumbs = [{'state':'Selection'}];
		$scope.impactRequest = {impactDirection: 0};
		$scope.impactPathRepo = [];
		$scope.isEdgeLabelVisible = true;

		var graphData = graphService.transfromToVisFmt( SharedService.graphData );

		$scope.vizCfg = {
			labelField:'label',
			isRandom : true,
			edgeLabelField : 'relType',
			edgeColorMap : $scope.edgeColorMap,
			nodeImageSetter : graphService.nodeImageSetter,
			nodeShape: 'image',
            nodeImageMap: SharedService.graphImageMap,
            nodeImageField: 'image',
			handlerData: { click : $scope.clickNode , select : $scope.onVizSelect , doubleClick : $scope.doubleClikNode , scope : $scope}
		};
		$scope.viz = new GRAPH.Viz ( $('#graphDiv') , $scope.vizCfg);

		$scope.viz.initialize( graphData );

		$scope.impactVizCfg = {
			labelField:'label' ,
			isRandom : true,
			edgeLabelField : 'relType',
			selectedEdgeColor : Constant.COLOR.AQUA,
			nodeImageSetter : graphService.levelImageSetter,
			edgeColorMap : $scope.edgeColorMap,
			handlerData: { click : $scope.impactClickNode , select : $scope.onSelect , scope : $scope},
			nodeShape: 'image',
            nodeImageMap: SharedService.graphImageMap,
            nodeImageField: 'image'
		}
		$scope.impactViz = new GRAPH.Viz ( $('#impact') , $scope.impactVizCfg);

		/*$scope.impactWeightedViz = new GRAPH.Viz ( $('#impactWeight') ,
		{
			labelField:'label' , isRandom : true,  edgeLabelField : 'relType', selectedEdgeColor : Constant.COLOR.AQUA,
			nodeImageSetter : graphService.levelImageSetter,
			edgeColorMap : $scope.edgeColorMap,
			handlerData: { click : $scope.impactClickNode , scope : $scope},
			nodeShape: 'image',
            nodeImageMap: SharedService.graphImageMap,
            nodeImageField: 'image'
		});	*/

		$scope.chartCfg = {Title: "Level vs No. of Nodes", IsStacked: true, handlerData: {scope: $scope, columnClick: "onGraphClick"}};
	}

	$scope.onSelect = function( edgeObj , fromNode , toNode ){
		var func = $scope.impactViz.isEdgeSelected(edgeObj.id) ? 'unSelectEdge' : 'selectEdge';
		//$scope.impactViz[func]( edgeObj.id );
		if( func == 'selectEdge' ){
			$scope.selectedEdge = {id: edgeObj.id, fromNode: fromNode.name, toNode: toNode.name, relType: edgeObj.label};
		}
	}

	$scope.onVizSelect = function( edgeObj , fromNode , toNode ){
		$scope.selectedVizEdge = {fromNode: fromNode, toNode: toNode, relType: edgeObj.label};
	}

	$scope.getDataSourceDetailsOfEdge = function(){
		var edge = $scope.selectedVizEdge;
		var edgeName = edge.fromNode.name + "-" + edge.relType + "->" + edge.toNode.name;
		SharedService.GetConfigurationDetailsByName( edgeName ).then(function( data ){
			$scope.currentEdge = edge;
			if(data[0]){
				data[0].details = angular.fromJson( data[0].details );
				var columns = data[0].details.columns;
				$scope.dsViewerHead = "Database columns";
				$scope.allColumnStr = "";
				angular.forEach(columns, function(col){
					if($scope.allColumnStr != "")
						$scope.allColumnStr = $scope.allColumnStr + " + ";
					$scope.allColumnStr = $scope.allColumnStr + col.schemaName + "." + col.tableName + "." + col.name;
				});
				$('#dsViewer').modal('show');
			}
		});
	}

	$scope.getNodeDescription = function(){
		if($scope.currentSelNode && $scope.currentSelNode.definition){
			$scope.dsViewerHead = $scope.currentSelNode.name + " : Definition";
			$scope.allColumnStr = $scope.currentSelNode.definition;
			$('#dsViewer').modal('show');
		}
	}

	$scope.closeDsViewer = function(){
		$scope.currentSelNode = null;
		$('#dsViewer').modal('hide');
	}

	$scope.searchNode = function(){
		$scope.viz.selectNodeByName($scope.searchText);
		$scope.searchText = "";
	}

	$scope.searchImpactNode = function(){
		$scope.impactViz.selectNodeByName($scope.searchText);
		$scope.searchText = "";
	}

	$scope.clickNode  = function( nodeId ){
		if( !nodeId ) return;

		var cursor = $('#graphDiv').css('cursor');
		if(cursor != 'auto' && cursor != 'default'){
			$scope.impactNote = null;
			$scope.changeRingCursor();
			$scope.impactRequest = {vertexId: nodeId, impactDirection:0};
			$('#levelModal').modal('show');
			return;
		}

		var clickNode = $scope.viz.findNodeById( nodeId );
		if( clickNode == undefined || clickNode == null) return;

		var inBreadCrumbs = _.findWhere($scope.breadCrumbs, {'state': clickNode.label});
		if(!inBreadCrumbs){
			$scope.breadCrumbs.push({'state':clickNode.label});
			$scope.fetchData(nodeId)
		}
		$scope.currentSelNode = clickNode;
	}

	$scope.fetchData = function( nodeId ){
		graphService.getGraphData(nodeId).then( function( graphData ){
			$scope.gridDataModel = graphData.gridData;
			var nodes =  graphData.visData.nodes ;
			var edges = graphData.visData.edges;
			$scope.viz.addChildNodes( nodes , nodeId);
			$scope.viz.addConnections( edges , nodeId);
		});
	}

	$scope.goPreviousScreen = function(){
		$state.go('landing.simulator');
	}

	$scope.changeRingCursor = function(){
		var classes = $('#graphDiv').attr('class');
		if(classes.indexOf('ring-cursor')!=-1){
			$('#graphDiv').removeClass('ring-cursor');
			$('#impactBtn').removeClass('text-red');
		}
		else{
			$('#graphDiv').addClass('ring-cursor');
			$('#impactBtn').addClass('text-red');
		}
	}

	$scope.getImpactedNodes = function(){
		getImpactedNodes = [];
		$scope.impactPathRepo = [];
		$scope.currentImpactNode = null;
		//requestImpactNodes();
		requestWeightedImpactNodes();
	}

	$scope.levelUp = function(){
		if($scope.eventName != "PATH_BW_NODES"){
			levelUp();
			return;
		}
		$scope.eventName = "LEVEL_UP";
		$scope.confirmMsg = Constant.MESSAGE.LEVEL_UP_CONF;
		$('#impactModal').modal('hide');
		$('#confirmModal').modal('show');
	}

	function levelUp(){
		$scope.impactRequest.level++;
		//requestImpactNodes();
		$scope.impactPathRepo = [];
		requestWeightedImpactNodes();
	}

	$scope.levelDown = function(){
		if($scope.eventName != "PATH_BW_NODES"){
			levelDown();
			return;
		}
		$scope.eventName = "LEVEL_DOWN";
		$scope.confirmMsg = Constant.MESSAGE.LEVEL_DOWN_CONF;
		$('#impactModal').modal('hide');
		$('#confirmModal').modal('show');
	}

	function levelDown () {
		if($scope.impactRequest.level>1){
			$scope.impactRequest.level--;
			//requestImpactNodes();
			$scope.impactPathRepo = [];
			requestWeightedImpactNodes();
		}
	}

	function requestImpactNodes(){
		if($scope.impactRequest.level){
			SharedService.getImpactedNodes($scope.impactRequest).then(function(data){
				if(data){
					$('#levelModal').modal('hide');
					if(data.graphData.vertices.length > 1){
						$scope.impactedGraphData = graphService.transfromToVisFmt( data.graphData );
						$scope.impactedChartData = data.chartData;
						$timeout(function(){
							drawGraph($scope.impactedGraphData);
							drawChart($('#chartDiv'), 'column', convertToChartData($scope.impactedChartData), $scope.chartCfg);
						},100);
						$('#impactModal').modal('show');
					}
					else{
						alert("No impacts!");
					}
				}
			});
		}
	}

	function requestWeightedImpactNodes(){
		if($scope.impactRequest.level){
			SharedService.getWeightedImpactGraph($scope.impactRequest).then(function(data){
				if(data){
					$('#levelModal').modal('hide');
					if(data.graphData.vertices.length > 1){
						$scope.rawImpactedGraphData = data.graphData;
						$scope.impactedGraphData = graphService.transfromToVisFmt( data.graphData, false );
						$scope.impactedChartData = data.chartData;
						$timeout(function(){
							drawGraph($scope.impactedGraphData);
							drawChart($('#chartDiv'), 'column', convertToChartData($scope.impactedChartData), $scope.chartCfg);
						},100);
						$('#impactModal').modal('show');
					}
					else{
						$scope.impactRequest.level--;
						alert("No impacts!");
					}
				}
			});
		}
	}

	$scope.impactClickNode = function( nodeId ){
		if( !nodeId ) return;

		$scope.currentImpactNode = $scope.impactViz.findNodeById( nodeId );
	}

	$scope.showPathBetweenNodes = function(){
		$('#impactDirection').modal('hide');

		$scope.eventName = "PATH_BW_NODES";
		$scope.rawImpactedGraphData.connecions = _.reject($scope.rawImpactedGraphData.connecions, function(obj){return (obj.from == $scope.impactRequest.vertexId) && (obj.to == $scope.currentImpactNode.id)});
		SharedService.getAllPossiblePathsBetweenTwoNodes($scope.impactRequest.vertexId, $scope.currentImpactNode.id, $scope.impactRequest.level, $scope.impactRequest.impactDirection).then(function(data){
			if(data){
				var disabledGraph = graphService.transfromToVisFmt( $scope.rawImpactedGraphData, true );
				$scope.impactPathRepo.push(graphService.transfromToVisFmt( data.graphData, false ));
				var newGraph = { nodes: new vis.DataSet(), edges: new vis.DataSet() };
				angular.forEach($scope.impactPathRepo, function(path){
					angular.forEach(path.nodes._data, function(node, id){
						disabledGraph.nodes.remove( id );
					});

					newGraph.nodes._data = _.extend(newGraph.nodes._data, path.nodes._data);
					newGraph.edges._data = _.extend(newGraph.edges._data, path.edges._data);
				});

				newGraph.nodes._data = _.extend(newGraph.nodes._data, disabledGraph.nodes._data);
				newGraph.edges._data = _.extend(newGraph.edges._data, disabledGraph.edges._data);

				$timeout(function(){
					drawGraph(newGraph);
				}, 100);
			}
		});
	}

	$scope.captureGraph = function () {
		$('#impactModal').modal('hide');
		$('#nameModal').modal('show');
	}

	$scope.saveImpactPath = function(){
		if($scope.impactPathName != null && $scope.impactPathName.trim() != ""){
			$('#nameModal').modal('hide');
			$('#impactModal').modal('show');
			var imageData = $('#impact canvas')[0].toDataURL("image/png", 1);
			if(imageData){
				var impactNode = $scope.viz.findNodeById( $scope.impactRequest.vertexId );
				SharedService.saveImpactImageData( $scope.impactPathName, imageData ).then(function(data){
					if(data)
						alert('Saved Successfully');
				});
			}
		}
	}

	$scope.getAllImpactImages = function () {
		SharedService.getAllConfigurationDetails(Constant.IMPACT.IMPACT_GRAPH_IMAGE).then(function(data){
			console.log(data);
		});
	}

	$scope.onConfirmation = function(){
		$("#confirmModal").modal('hide');
		if($scope.eventName === "LEVEL_UP")
			levelUp();
		else if($scope.eventName === "LEVEL_DOWN")
			levelDown();
	}

	$scope.onRejection = function(){
		$scope.eventName = "PATH_BW_NODES";
		$("#confirmModal").modal('hide');
		$("#impactModal").modal('show');
	}

	/*function requestBothImpactNodes(){
		if($scope.impactRequest.level){
			//SharedService.getImpactedNodes($scope.impactRequest).then(function(data){
			SharedService.mergedConnectedGraphAndWeightedGraph($scope.impactRequest).then(function(data){
				if(data){
					$('#levelModal').modal('hide');
					$scope.connectedGraph = angular.fromJson(data.connectedGraph);
					$scope.weightedGraph = angular.fromJson(data.weightedGraph);
					if($scope.weightedGraph.graphData.vertices.length > 1){
						$scope.weightedGraphData = graphService.transfromToVisFmt( $scope.weightedGraph.graphData );
						//$scope.connectedGraphData = graphService.transfromToVisFmt( $scope.connectedGraph.graphData );
						$scope.chartData = convertToChartData($scope.weightedGraph.chartData);
						$timeout(function(){
							drawWeightedGraph($scope.weightedGraphData);
							//drawGraph($scope.connectedGraphData);
							drawChart($('#chartDiv'), 'column', $scope.chartData, $scope.chartCfg);
						},100);
						$('#impactModal').modal('show');
					}
					else{
						alert("No impacts!");
					}
				}
			});
		}
	}*/

	$scope.getLegendCount = function(){
		return _.range($scope.impactRequest.level + 1);
		//return [0, $scope.impactRequest.level];
	}

	$scope.getAllViewsBySelNode = function(){  //shows logical views
		$('#impactModal').modal('hide');
		$timeout(function(){
			SharedService.getImpactLogicalViews( {id: $scope.currentImpactNode.id, nodeName: $scope.currentImpactNode.name} ).then(function(views){
				views = angular.fromJson( views );
				if(views.length == 0){
					alert("No logical view available for " + $scope.currentImpactNode.name);
					$('#impactModal').modal('show');
					return;
				}
				else{
					$scope.logicalViewCfg = convertToLogicalView( views );
					SharedService.hideAllToolTips();
					$('#logicalViewModal').modal('show');
				}
			});
		}, 500);
	}

	$scope.viewTable = function(v){
		$('#logicalViewModal').modal('hide');
		$scope.viewName =  v.name;
		SharedService.GetDataFromLogicalView( v.nodeName ).then(function(data){
			if(data.status){
				if( data.tree == false ){
					showGrid(data.data);
					$('#viewTableModal').modal('show');
					setTimeout( function(){
						$('#lvGrid').w2grid( 'refresh' );
					},500);
				}else{
					var rec = { name: 'Records', text : 'Records', children: data.data.children};
					var config = {
							"check_callback" : true,
							"plugins": ["sort", "wholerow", "types"],
							"handlerData" : { "scope": $scope, "onBaseNodeClick": "baseNodeClick", "onSelectNode": "onSelectNode", "onDeselectNode": "onDeselectNode"}
						}
					$scope.jsTree = new TREE.JsTree ( $('#lvGrid') , config );

					//$scope.jsTree.draw(rec);
					$scope.jsTree.loadData( rec );
					$('#viewTableModal').modal('show');
				}
			}
			else{
				alert("Data not found.");
			}
		});
	}

	function drawGraph(data){
		$timeout(function(){
			$scope.impactViz.initialize( data );
			if($scope.isEdgeLabelVisible){
	    		$scope.impactViz.network.defaultOptions.edges.hideEdgeLabel = false;
	    	}
	    	else{
	    		$scope.impactViz.network.defaultOptions.edges.hideEdgeLabel = true;
	    	}
	    	$scope.impactViz.network._redraw();
		},300);
    }

    function drawWeightedGraph(data){
		$timeout(function(){
			$scope.impactWeightedViz.initialize( data );
		},300);
    }

    function drawChart(element, type, data, cfgObj){
        var chartData = {GraphType: type, data: data};
        var c = new GRAPH.PlotWrapper( element , cfgObj);
        c.draw(chartData);
        return c;
    }

    function convertToChartData(graphData){
		var obj = {series:[{name:"No. of Nodes", data: []}], categories: []};
    	angular.forEach(graphData, function(arr, key){
          	obj.series[0].data.push(arr.length);
          	obj.categories.push(Constant.LEVEL_PREFIX + key);
    	});
        return obj;
    }

    function convertToLogicalView(views){
		var logicalViewCfg = {nodeName: $scope.currentImpactNode.name, views: []};
		angular.forEach(views, function(obj){
			obj.details = angular.fromJson(obj.details);
			var view = {nodeName: obj.name, name: obj.details.name, shortName: SharedService.getShortName(obj.details.name), attributes: obj.details.columns, rootClass: obj.details.rootClass, logical: true};
			angular.forEach(view.attributes, function(attr){
				attr.logical = true;
				attr.viewCfgName = view.nodeName;
			});
			logicalViewCfg.views.push( view );
		});
		return logicalViewCfg;
	}

	//Tab control
	$('#myTab a').click(function (e) {
        if($(this).parent('li').hasClass('active')){
            $( $(this).attr('href') ).hide();
        }
        else {
            e.preventDefault();
            $(this).tab('show');
        }
    });

    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
      if(e.target.innerHTML == "SIMULATE IMPACT"){
      	$scope.isEdgeLabelVisible = true;
      	drawGraph($scope.impactedGraphData);
    	$(".legend-container").show();
    	$(".impact-minus, .impact-plus, .impact-level-counter, .impact-expand, .impact-capture, .impact-switch, .impact-search").show();
      }
      else{
      	$(".legend-container").hide();
      	$(".impact-minus, .impact-plus, .impact-level-counter, .impact-expand, .impact-capture, .impact-switch, .impact-search").hide();
   	  	drawChart($('#chartDiv'), 'column', convertToChartData($scope.impactedChartData), $scope.chartCfg);
   	  }
    });
    //Tab control

    $scope.onGraphClick = function(target){
    	$scope.level = target.currentTarget.category;
    	$scope.level = $scope.level.replace(Constant.LEVEL_PREFIX, "");
    	$scope.levelNodes = $scope.impactedChartData[$scope.level];
    	$('#impactModal').modal('hide');
    	$('#levelNodesModal').modal('show');
    }

    $scope.back = function(){
    	$('#xmlViewer').modal('hide');
    	$('#levelNodesModal').modal('hide');
    	$('#impactModal').modal('show');
    }

    $scope.backToImpact = function(){
    	$('#logicalViewModal').modal('hide');
    	$('#impactModal').modal('show');
    }

    $scope.backToLogical = function(){
    	$('#viewTableModal').modal('hide');
    	$('#logicalViewModal').modal('show');
    }

    $scope.dropUp = function(){
    	$scope.impactRequest.impactDirection = 1;
    	var impactNode = $scope.viz.findNodeById( $scope.impactRequest.vertexId );
    	$scope.impactNote = "Increase in " + impactNode.name;
    }

    $scope.dropIn = function(){
    	$scope.impactRequest.impactDirection = -1;
    	var impactNode = $scope.viz.findNodeById( $scope.impactRequest.vertexId );
    	$scope.impactNote = "Drop in " + impactNode.name;
    }

    $scope.toggleSimulatorEdgeLabel = function(){
    	$scope.isEdgeLabelVisible = !$scope.isEdgeLabelVisible;
    	if($scope.isEdgeLabelVisible){
    		$scope.viz.network.defaultOptions.edges.hideEdgeLabel = false;
    	}
    	else{
    		$scope.viz.network.defaultOptions.edges.hideEdgeLabel = true;
    	}
    	$scope.viz.network._redraw();
    }

    $scope.toggleImpactEdgeLabel = function(){
    	$scope.isEdgeLabelVisible = !$scope.isEdgeLabelVisible;
    	if($scope.isEdgeLabelVisible){
    		$scope.impactViz.network.defaultOptions.edges.hideEdgeLabel = false;
    	}
    	else{
    		$scope.impactViz.network.defaultOptions.edges.hideEdgeLabel = true;
    	}
    	$scope.impactViz.network._redraw();
    }

    /*$('#impactModal').on('shown.bs.modal', function() {
		$scope.isEdgeLabelVisible = true;
	});*/

	$('#impactModal').on('hidden.bs.modal', function() {
		//$scope.isEdgeLabelVisible = true;
		if($scope.isEdgeLabelVisible){
    		$scope.viz.network.defaultOptions.edges.hideEdgeLabel = false;
    	}
    	else{
    		$scope.viz.network.defaultOptions.edges.hideEdgeLabel = true;
    	}
		$scope.viz.network._redraw();
	});

	$scope.initialize();
})

.controller('newsimulateCtrl',function($scope, $state, $http, $stateParams, SharedService, RiskAggregateService, MockService){
	$scope.initialize = function(){
		$scope.request = {
			rootNode : null,
			destinationNodes : []
		}
		SharedService.getSimulatorMockNodes().then( function( data ) {
			if(data){
				$scope.rootNodes = data.rootNodes;
				$scope.destinationNodes = data.destinationNodes;
			}
		});
	}

	$scope.onDestinationNodeSelect = function( destNode ){
		if( destNode.selected )
			$scope.request.destinationNodes.push( destNode.id );
		else
			$scope.request.destinationNodes = _.without( $scope.request.destinationNodes, destNode.id );
	}

	$scope.doNext = function(){
		SharedService.getImpactLevelForGivenNodes($scope.request).then(function(data){
			if(data){
				SharedService.graphData = data;
				$state.go('landing.newsimulatorGraph');
			}
		});
	}
	$scope.initialize();
})

.controller('newsimulategraphCtrl',function($scope, $state, $http, $stateParams, $timeout, SharedService, MockService, graphService){
	$scope.initialize = function(){
		var graphData = graphService.transfromToVisFmt( SharedService.graphData );
		$scope.rootNode = _.findWhere(SharedService.graphData.vertices, {"level" : "999"});

		$scope.vizCfg = {
			labelField:'label',
			isRandom : true,
			edgeLabelField : 'relType',
			edgeColorMap : $scope.edgeColorMap,
			nodeImageSetter : graphService.levelImageSetter,
			nodeShape: 'image',
            nodeImageMap: SharedService.graphImageMap,
            nodeImageField: 'image',
			handlerData: { click : $scope.clickNode , scope : $scope}
		};

		$scope.impactVizCfg = {
			labelField:'label' ,
			isRandom : true,
			edgeLabelField : 'relType',
			selectedEdgeColor : Constant.COLOR.AQUA,
			nodeImageSetter : graphService.levelImageSetter,
			edgeColorMap : $scope.edgeColorMap,
			handlerData: { click : $scope.impactClickNode , scope : $scope},
			nodeShape: 'image',
            nodeImageMap: SharedService.graphImageMap,
            nodeImageField: 'image'
		};

		$scope.viz = new GRAPH.Viz ( $('#graphDiv') , $scope.vizCfg);
		$scope.impactViz = new GRAPH.Viz ( $('#impactDiv') , $scope.impactVizCfg);

		$scope.viz.initialize( graphData );
	}

	$scope.clickNode  = function( nodeId ){
		if( !nodeId ) return;
		var clickNode = $scope.viz.findNodeById( nodeId );
		if( clickNode.level == "999" ) clickNode = $scope.currentSelNode = undefined;
		if( clickNode == undefined || clickNode == null ) return;
		$scope.currentSelNode = clickNode;
	}

	$scope.searchNode = function(){
		$scope.viz.selectNodeByName($scope.searchText);
		$scope.searchText = "";
	}

	$scope.searchImpactNode = function(){
		$scope.impactViz.selectNodeByName($scope.searchText);
		$scope.searchText = "";
	}

	$scope.doNext = function(){
		if( $scope.currentSelNode ){
			SharedService.getAllPossiblePathsBetweenTwoNodes($scope.rootNode.id, $scope.currentSelNode.id, 0, 0).then(function(data){
				if(data){
					var graphData = graphService.transfromToVisFmt( data.graphData );
					$('#impactModal').modal('show');
					$timeout(function(){
						$scope.impactViz.initialize( graphData );
					},300);
				}
			});
		}
	}

	$scope.edgeColorMap = function( fromNode, toNode, relType, edge ){
		var color = Constant.COLOR.GREEN;
		var level = parseInt(edge.level);
		switch(level){
			case 1: color = Constant.COLOR.LIGHTGREEN; break;
			case 2: color = Constant.COLOR.YELLOW; break;
			case 3: color = Constant.COLOR.CREAM; break;
			case 4: color = Constant.COLOR.ORANGE; break;
			case 5: color = Constant.COLOR.PINK; break;
			case 6: color = Constant.COLOR.LIGHTBROWN; break;
			case 7: color = Constant.COLOR.OLIVE; break;
			case 8: color = Constant.COLOR.PURPLE; break;
			case 9: color = Constant.COLOR.BROWN; break;
			case 10: color = Constant.COLOR.RED; break;
			case 100: color = Constant.COLOR.GRAY; break;
			case 999: color = Constant.COLOR.BLACK; break;
			default : color = Constant.COLOR.GREEN; break;
		}
		return color;
	}

	$scope.getAllViewsBySelNode = function(){  //shows logical views
		$('#impactModal').modal('hide');
		$timeout(function(){
			SharedService.getImpactLogicalViews( {id: $scope.currentImpactNode.id, nodeName: $scope.currentImpactNode.name} ).then(function(views){
				views = angular.fromJson( views );
				if(views.length == 0){
					alert("No logical view available for " + $scope.currentImpactNode.name);
					$('#impactModal').modal('show');
					return;
				}
				else{
					$scope.logicalViewCfg = convertToLogicalView( views );
					SharedService.hideAllToolTips();
					$('#logicalViewModal').modal('show');
				}
			});
		}, 500);
	}

	$scope.impactClickNode = function( nodeId ){
		if( !nodeId ) return;
			$scope.currentImpactNode = $scope.impactViz.findNodeById( nodeId );
	}

	$scope.backToImpact = function(){
    	$('#logicalViewModal').modal('hide');
    	$('#impactModal').modal('show');
    }

    $scope.captureGraph = function () {
		$('#impactModal').modal('hide');
		$('#nameModal').modal('show');
	}

	$scope.saveImpactPath = function(){
		if($scope.impactPathName != null && $scope.impactPathName.trim() != ""){
			$('#nameModal').modal('hide');
			$('#impactModal').modal('show');
			var imageData = $('#impactDiv canvas')[0].toDataURL("image/png", 1);
			if(imageData){
				//var impactNode = $scope.viz.findNodeById( $scope.impactRequest.vertexId );
				SharedService.saveImpactImageData( $scope.impactPathName, imageData ).then(function(data){
					if(data)
						alert('Saved Successfully');
				});
			}
		}
	}

    $scope.goPreviousScreen = function(){
    	$state.go('landing.newsimulator');
    }

	$scope.initialize();
})

.controller('glossaryCtrl', function($scope, $state, $compile, $timeout, SharedService, MockService){
	$scope.initialize = function(){
		$scope.isGenericView = true;
		$scope.glossarySets = [];
		$scope.selectedDbCfgs = [];
		$scope.datasources = [];

		SharedService.getJsonForvisualizationReasonerGraph().then(function(data){
			if(data){
				$scope.glossarySets = data.glossarySets;
				$scope.rules = data.rules;
				$scope.graphData = {nodes: data.graphData.vertices, edges: []};
				$scope.connectedTables = data.db;
				//$scope.connectedTables = MockService.glossaryData.db;
			}
		});
		$(".chip").removeClass('active-nav');
	}

	$scope.clickNode= function(nodeId){
		if(nodeId){
			var node = $scope.viz.findNodeById(nodeId);
			exploreConcept(node);
		}
	};

	$scope.exploreDB = function(dbRow){
		SharedService.hideAllToolTips();
		$scope.isGenericView = false;
		SharedService.getLineageGraphByDB(dbRow).then(function(data){
			if(data){
				console.log(data);
				$scope.graphData = {nodes: data.graphData.vertices, edges: []};
				$scope.glossarySets = data.glossarySets;
				$scope.rules = data.rules;
				$scope.connectedTables = data.db;
				//$scope.connectedTables = MockService.glossaryData.db;
				$scope.selectedConceptNodeName = "";
			}
		});
		$('#expandedViewModal').modal('hide');
	}

	$scope.exploreRule = function(rule){
		clearSelection(rule, $scope.rules);
		SharedService.hideAllToolTips();
		$scope.isGenericView = false;
		SharedService.getJsonForvisualizationReasonerGraphById(rule.id).then(function(data){
			if(data){
				console.log(data);
				$scope.graphData = {nodes: data.graphData.vertices, edges: []};
				$scope.glossarySets = data.glossarySets;
				//$scope.rules = data.rules;
				$scope.connectedTables = data.db;
				//$scope.connectedTables = MockService.glossaryData.db;
				$scope.selectedConceptNodeName = "";
			}
		});
		$('#expandedViewModal').modal('hide');
	}

	$scope.exploreGlossary = function(glossary, glossarySet){
		clearSelection(glossary, null, glossarySet);
		SharedService.hideAllToolTips();
		$scope.isGenericView = false;
		SharedService.getLineageGraphByGlossaryId(glossary.id).then(function(data){
			if(data){
				$scope.graphData = {nodes: data.graphData.vertices, edges: []};
				//$scope.glossarySets = data.glossarySets;
				$scope.rules = data.rules;
				$scope.connectedTables = data.db;
				//$scope.connectedTables = MockService.glossaryData.db;
				$scope.selectedConceptNodeName = "";
			}
		});
		$('#expandedViewModal').modal('hide');
	}

	function exploreConcept(conceptNode){
		SharedService.hideAllToolTips();
		$scope.isGenericView = false;
		SharedService.getLineageGraphByConceptId(conceptNode.id).then(function(data){
			if(data){
				//$scope.graphData = {nodes: data.graphData.vertices, edges: []};
				$scope.glossarySets = data.glossarySets;
				$scope.rules = data.rules;
				$scope.connectedTables = data.db;
				//$scope.connectedTables = MockService.glossaryData.db;
				$scope.selectedConceptNodeName = conceptNode;
			}
		});
		$(e.currentTarget).parent().siblings().children().removeClass('active-nav');
		$(e.currentTarget).addClass('active-nav');
		$('#expandedViewModal').modal('hide');
	}

	function getRulewiseGlossary(ruleWiseGlossary){
		SharedService.hideAllToolTips();
		var glossarySets = {};
		SharedService.getJsonForvisualizationReasonerGraph().then(function(data){
			if(data){
				var allGlossarySets = data.glossarySets;
				angular.forEach(ruleWiseGlossary, function(val, key){
					var arr = [];
					var allSets = allGlossarySets[key];
					angular.forEach(val, function(id){
						var obj = _.findWhere(allSets, {id: id});
						arr.push( obj );
					});
					glossarySets[key] = arr;
				});
			}
		});

		return glossarySets;
	}

	$scope.searchNode = function(){
		$scope.viz.selectNodeByName($scope.searchText);
		$scope.searchText = "";
	}

	$scope.expandRegulation = function(){
		$scope.modalHead = "Regulations";
		var template = $('<div class="col-md-3 zero-padding" ng-repeat="rule in rules | filter: searchText"><div class="chip"><div class="chip-image" ng-click="exploreRule(rule)"><div ng-class="{ribbon:rule.isSelected}"></div><div class="ellipsis-text" style="width:15vw">{{rule.name}}</div></div><div class="chip-content"><div ng-click="viewInfo(rule)"><i class="fa fa-info icon-sm"></i></div><div ng-click="exploreRule(rule)"><i class="fa fa-play icon-sm"></i></div></div></div><div ng-if="getCornerVisibility(rule)" class="corner-green"><i class="fa corner-icon" ng-class="{\'fa-file-text-o\': rule.isInformation, \'fa-book\':rule.isPolicy, \'fa-user\':rule.isPerson, \'fa-asterisk\':rule.isModel, \'fa-users\':rule.isCommittee, \'fa-clipboard\':rule.isReport, \'fa-cog\': rule.isSystem, \'fa-database\': rule.isData}"></i></div></div>');
		var container = $('#expandedViewModal .modal-body');
		container.html(template);
		$compile(container.contents())($scope);
		$('#expandedViewModal').modal('show');
	}
	$scope.expandConcept = function(){
				
	}
	$scope.expandGlossarySet = function(title, currentSet){
		$scope.modalHead = title;
		$scope.currentSet = currentSet;
		var template = $('<div class="col-md-3 zero-padding" ng-repeat="glossary in currentSet | filter: searchText"><div class="chip"><div class="chip-image" ng-click="exploreGlossary(glossary, glossarySets)"><div ng-class="{ribbon:glossary.isSelected}"></div><div class="ellipsis-text" style="width:15vw">{{glossary.name}}</div></div><div class="chip-content"><div ng-click="viewInfo(glossary)"><i class="fa fa-info icon-sm"></i></div><div ng-click="exploreGlossary(glossary, glossarySets)"><i class="fa fa-play icon-sm"></i></div></div></div></div>');
		var container = $('#expandedViewModal .modal-body');
		container.html(template);
		$compile(container.contents())($scope);
		$('#expandedViewModal').modal('show');
	}
	$scope.expandConnectedTable = function(){
		$scope.modalHead = "Connected Tables";
		var template = $('<table class="table table-striped table-bordered table-condensed"><thead><th></th><th>Database</th><th>Table</th><th>column</th><th class="text-center"><span ng-if="selectedDbCfgs.length > 0" class="glyphicon glyphicon-eye-open" ng-click="selectFilters()"></span></th></thead><tbody><tr ng-repeat="dbCfg in connectedTables | filter: searchText"><td><input type="checkbox" ng-model="dbCfg.isSelected" ng-change="selectDbCfg(dbCfg)"></td><td>{{dbCfg.dbname}}</td><td>{{dbCfg.table}}</td><td>{{dbCfg.column}}</td><td class="text-center"><span class="glyphicon glyphicon-play" ng-click="exploreDB(dbCfg)"></span></td></tr></tbody></table>');
		var container = $('#expandedViewModal .modal-body');
		container.html(template);
		$compile(container.contents())($scope);
		$('#expandedViewModal').modal('show');
	}
	$scope.selectDbCfg = function(dbCfg){
		if(dbCfg.isSelected){
			$scope.selectedDbCfgs.push(dbCfg);
		} else{
			$scope.selectedDbCfgs = _.reject($scope.selectedDbCfgs, function(t){ 
				return t.column === dbCfg.column && t.table === dbCfg.table && t.dbname === dbCfg.dbname; 
			});
		}
	}
	$scope.selectFilters = function(){
		$('#expandedViewModal').modal('hide');
		$scope.modalHead = "Filter Configuration";
		if($scope.datasources.length == 0){
			SharedService.getConfigurationList("DATASOURCE").then(function(data){
				if(data){
					for( var i in data)
						$scope.datasources.push( { id : i , name : data[i]} );
					$('#dsViewer').modal('show');
				}
			});
		}
		else
			$('#dsViewer').modal('show');
	}
	$scope.viewDataOfSelectedColumns = function(){
		$scope.modalHead = "Table Data";
		var container = $('#expandedViewModal .modal-body');
		var request = {};
		request.name = $scope.selDs || "";
		request.columns = configDbColumns($scope.selectedDbCfgs);
		request.fixedClause = $scope.queryWhereClause || "";
		request.references = null;
		SharedService.getLineageDbData(request).then(function(data){
			if(data.status){
				$('#dsViewer').modal('hide');
				$scope.table = data.data;
				var template = $('<table class="table table-striped table-condensed table-bordered"><thead><th ng-repeat="col in table.columnNames">{{col}}</th></thead><tbody><tr ng-repeat="row in table.tableData"><td ng-repeat="val in row">{{val}}</td></tr></tbody></table>');
				container.html(template);
				$compile(container.contents())($scope);
				$('#expandedViewModal').modal('show');
			}
			else{
				alert("Could not capture data!")
			}
		});
	}
	function configDbColumns(dbCfgs){
		var columns = [];
		angular.forEach(dbCfgs, function(aCfg){
			var cfg = {dsName: $scope.selDs, schemaName: aCfg.dbname, tableName: aCfg.table, name: aCfg.column};
			columns.push(cfg);
		})
		return columns;
	}

	$scope.viewInfo = function(item){
		$('#expandedViewModal').modal('hide');
		if(!item){
			$scope.itemInfo = $scope.selectedConceptNodeName.des;
		}
		else{
			$scope.itemInfo = item.des || "";
		}
		$scope.modalHead = "Information";
		$('#infoModal').modal('show');
	}

	function clearSelection(currItem, items, listItems){
		if(items){	
			angular.forEach(items, function(i){
				if(i.isSelected){
					i.isSelected = false;
				}
			});
		}
		if(listItems){
			angular.forEach(listItems, function(l){
				angular.forEach(l, function(i){
					if(i.isSelected){
						i.isSelected = false;
					}
				});
			});
		}
		if(currItem){
			currItem.isSelected = true;
		}
	}

	$(".panel-3d").hover(function(e){
		$(this).find(".sticky-icon").show(500);
	}, function(e){
		$(this).find(".sticky-icon").hide(500);
	});

	$scope.getCornerVisibility = function(item){
		return item.isReport || item.isPolicy || item.isPerson || item.isModel || item.isCommittee || item.isInformation || item.isSystem || item.isData;
	}

	$scope.initialize();
})

.controller('margeAggCtrl', function($scope, $state, SharedService, MockService, graphService){
	$scope.initialize = function(){
		$scope.isDisbleAggrName = true;
		$scope.savedPathList = [];
		$scope.childAggrCache = [];
		/*SharedService.getConfigurationList("AGGREGATION").then(function(data){
			$scope.savedAggregateList = data;
			for( var i in data ){
				addAggregates(i, data[i], );
			}
		});*/
		SharedService.getClusteredConfigurationNames().then(function(data){
			if(data){
				var aggregates = data[$scope.constants.AGGREGATION];
				var compositAggregates = data[$scope.constants.COMPOSITEAGGREGATION];
				for( var i in aggregates ){
					addAggregates(i, aggregates[i], false);
				}
				for( var i in compositAggregates ){
					addAggregates(i, compositAggregates[i], true);
				}
			}
		});
		$scope.viz = new GRAPH.Viz ( $('#savedGraph') ,
		{
			labelField:'label' , isRandom : true,  edgeLabelField : 'relType', selectedEdgeColor : Constant.COLOR.AQUA,
			nodeImageSetter : graphService.nodeImageSetter,
			edgeColorMap : $scope.edgeColorMap,
			handlerData: { click : $scope.clickNode , doubleClick : $scope.doubleClikNode , select : $scope.onSelect, scope : $scope},
			nodeShape: 'image',
            nodeImageMap: SharedService.graphImageMap,
            nodeImageField: 'image'
		});
	}

	function addAggregates(id, title, isComposed){
		if(isComposed)
			$scope.savedPathList.push( {id: id, title: title, class:"aggr", img:"assets/images/aggregatorOnCircle.png", isComposed: isComposed});
		else
			$scope.savedPathList.push( {id: id, title: title, class:"aggr", img:"assets/images/associator.png", isComposed: isComposed});
	}

	$scope.onAggregateDrop = function( ui , cellIdx , element){
		var idx = $(ui.draggable[0]).attr('index');
		var targetAggregate = $scope.savedPathList[idx];
		$scope.currentAggregate = targetAggregate;
		
		if(_.indexOf($scope.childAggrCache, targetAggregate.title) != -1) return;
		if(targetAggregate.isComposed){
			SharedService.getAggrCfgDetailsByNameAndType(targetAggregate.title, $scope.constants.COMPOSITEAGGREGATION).then(function(data){
				if(data){
					angular.forEach(data, function(name){
						if(_.indexOf($scope.childAggrCache, name) == -1)
							$scope.childAggrCache.push(name);
					});
					getAggregateData(targetAggregate);
				}
			});
		}
		else{
			$scope.childAggrCache.push(targetAggregate.title);
			getAggregateData(targetAggregate);
		}		
		clearCurrentSelectedNode();
	}

	function getAggregateData(targetAggregate){
		if($scope.childAggrCache.length == 1){
			SharedService.getConfigurationInfo(targetAggregate.id).then(function(data){
				data = angular.fromJson(data);
				$scope.currentAggregateData = data;
				data.replace("name", "label")
				data = angular.fromJson(data);
				$scope.graphData = { nodes : data.vertices , edges : data.connecions };
				SharedService.currentBaseNodeList = data.baseNodeList;
				$scope.viz.initialize( $scope.graphData );
			});
		}
		else{
			SharedService.assimilatorComposingByNames($scope.childAggrCache).then(function(data){
				if(data){
					$scope.graphData = { nodes : data.vertices , edges : data.connecions };
					$scope.viz.initialize( $scope.graphData );
				}
			});
		}
	}

	$scope.cleanCache = function(){
		$scope.childAggrCache = [];
		$scope.graphData = {nodes: [], edges: []};
		$scope.viz.initialize( $scope.graphData );
	}

	$scope.openSaveAsModal = function(){
		$scope.combineAggrName = "";
		angular.forEach($scope.childAggrCache, function(name){
			$scope.combineAggrName = $scope.combineAggrName + "_" + name;
		});
		$scope.combineAggrName = $scope.combineAggrName.substring(1);
		$('#saveAsModal').modal('show');
	}

	$scope.saveMerging = function(){
		SharedService.saveComposedAggregators($scope.combineAggrName, $scope.childAggrCache).then(function(data){
			if(data){
				$('#saveAsModal').modal('hide');
			}
		});
	}

	$scope.removeSubGraph = function(aggrName){
		if($scope.childAggrCache.length > 1){
			$scope.childAggrCache = _.reject($scope.childAggrCache, function(aName){return aName === aggrName;});
			SharedService.assimilatorComposingByNames($scope.childAggrCache).then(function(data){
				if(data){
					$scope.graphData = { nodes : data.vertices , edges : data.connecions };
					$scope.viz.initialize( $scope.graphData );
				}
			});
			clearCurrentSelectedNode();
		}
	}

	function clearCurrentSelectedNode(){
		$scope.currentSelNode = undefined;
	}

	$scope.clickNode  = function( nodeId ){
		if(nodeId){
			var node = $scope.viz.findNodeById(nodeId);
			if( node.isPolicy ){
				if(node.policyLink){
					window.open(node.policyLink, "", "width=950, height=600");
				}
			}
			$scope.currentSelNode = node;
		}
	}

	$scope.getAllViewsBySelNode = function(){  //shows logical views
		SharedService.currentSelNode = $scope.currentSelNode;
		if($scope.currentSelNode){
			SharedService.getAllLogicalViewsForAggregatorNode( $scope.currentSelNode.id ).then(function(views){
				if(views.length == 0){
					alert("No view available.");
					return;
				}
				else{
					SharedService.logicalViewCfg = convertToLogicalView( views );
					SharedService.hideAllToolTips();
					$state.go("landing.logicalview");
				}
			});
		}
	}

	function convertToLogicalView(views){
		var logicalViewCfg = {nodeName: $scope.currentSelNode.name, aggregateName: $scope.currentAggregate.name, views: []};
		angular.forEach(views, function(obj){
			obj.details = angular.fromJson(obj.details);
			var view = {nodeName: obj.name, name: obj.details.name, shortName: SharedService.getShortName(obj.details.name), attributes: obj.details.columns, rootClass: obj.details.rootClass, logical: true};
			angular.forEach(view.attributes, function(attr){
				attr.logical = true;
				attr.viewCfgName = view.nodeName;
			});
			logicalViewCfg.views.push( view );
		});
		return logicalViewCfg;
	}

	$scope.initialize();
})

.controller('statusReportCtrl', function($scope, $rootScope, $state, $compile, SharedService, MockService, NgAnimateService){
	$scope.initialize = function(){
		$scope.heading = SharedService.STATUS_VIEW;
		$scope.reports = [];
		$scope.doc = new DOC.Document($('#toCapture'));
		var compName = "";
		var reportType = "";
		$scope.dashboardType = $scope.heading.dashboardType;
		switch($scope.dashboardType){
			case Constant.DASHBOARD.REGULATORY:
				compName = Constant.WIDGET_NAMES.REGULATORY;
				break;
			case Constant.DASHBOARD.ENTERPRISE:
				compName = Constant.WIDGET_NAMES.ENTERPRISE;
				break;
			case Constant.DASHBOARD.FRY14:
				compName = Constant.WIDGET_NAMES.FRY14;
				break;
			case Constant.DASHBOARD.MODEL_REQUEST:
				compName = $scope.heading.title;
				reportType = Constant.WIDGET_NAMES.GRAPH;
				break;
			default:
				compName = $scope.dashboardType + "-" + $scope.heading.title;
				reportType = Constant.WIDGET_NAMES.GRAPH;
				break;
		}
		SharedService.getAllSeriesData( compName, reportType ).then(function(data){
			$scope.reports = data;
		});
	}

	$scope.capturePage = function(){
		$rootScope.loader = true;
		$scope.doc.capture().then(function(canvas){
			if(!MockService.archivePages)
				MockService.archivePages = [];
			var img = {id: MockService.pageIdx++, data: canvas.toDataURL("image/jpg")};
			MockService.archivePages.push(img);
			$rootScope.loader = false;
		});		
	}

	$scope.showDetail = function(){
		SharedService.hideAllToolTips();
		$state.go("landing.gapdetails");
	}

	$scope.expandView = function(report){
		$scope.modalHead = report.name;
		$scope.report = report;
		var template = $('<div style="width:88%;height:66vh;" high-chart options="report.options" data="report.data"></div><div class="container-fluid margin-top-5"><i>{{report.description}}</i></div>');
		var container = $('#expandedViewModal .modal-body');
		container.html(template);
		$compile(container.contents())($scope);
		$('#expandedViewModal').modal('show');
	}

	$scope.drillDown = function(report){
		SharedService.currentReport = report;		
		if(report.key === "FRY-14Q"){
			SharedService.STATUS_VIEW.widget = "FRY-14Q-Schedule";
			$state.go('landing.schedule');    //to be replaced by statusReport
			return;
		}

		switch($scope.dashboardType){
			case Constant.DASHBOARD.REGULATORY:
				$state.go('landing.gapdetails');
				break;
			case Constant.DASHBOARD.ENTERPRISE:
			case Constant.DASHBOARD.FRY14:
			case Constant.DASHBOARD.MODEL:
			default:
				$state.go('landing.ewgIssue');
				break;
		}		
	}

	$scope.goPreviousScreen = function(){
		$state.go('landing.dashboardProfile');
	}

	$scope.initialize();
})

.controller('statusProfileCtrl', function($scope, $state, $compile, SharedService, MockService, NgAnimateService){
	$scope.initialize = function(){
		$scope.currentReport = SharedService.currentreport;
		$scope.schedules = [];
		$scope.animation = NgAnimateService.animations[2];
		$scope.heading = {title: $scope.currentReport.name};
		$scope.tableData = MockService.dashboardTable;

		SharedService.getAllSeriesData( $scope.currentReport.name + "-" + Constant.WIDGET_NAMES.SCHEDULE ).then(function(data){
			NgAnimateService.lazyLoadItems($scope.schedules, data);
		});
	}

	$scope.goPreviousScreen = function(){
		$state.go('landing.statusReport');
	}

	$scope.viewSchedule = function(schedule){
		$state.go('landing.gapdetails');
	}

	$scope.expandView = function(schedule){
		$scope.modalHead = schedule.name;
		$scope.schedule = schedule;
		var template = $('<div class="container-fluid"><div ng-repeat="metric in schedule.metrics"><label>{{metric.name}}</label><div progress-bar=\'"min":0,"max":100,"value":{{metric.value}}\'></div></div></div><div class="container-fluid margin-top-5"><i>{{schedule.description}}</i></div>');
		var container = $('#expandedViewModal .modal-body');
		container.html(template);
		$compile(container.contents())($scope);
		$('#expandedViewModal').modal('show');
	}

	$scope.initialize();
})

.controller('pageArchiveCtrl', function($scope, $state, $compile, SharedService, MockService, NgAnimateService){
	$scope.initialize = function(){
		$scope.animation = NgAnimateService.animations[2];
		$scope.pages = [];
		NgAnimateService.lazyLoadItems($scope.pages, angular.copy(MockService.archivePages));
		$scope.report = {};
		$scope.selectedPages = [];
	}

	$scope.viewPage = function(page){
		$scope.page = page;
		$('#pageViewModal').modal('show');
	}

	$scope.selectPage = function(page){
		$scope.page = page;
		if(!page.isChecked){
			$('#pageDetailModal').modal('show');
			page.isChecked = true;
			$scope.selectedPages.push($scope.page);
		}
		else{
			page.isChecked = false;
			$scope.selectedPages = _.reject($scope.selectedPages, function(p){return $scope.page.id === p.id});
		}
	}

	$scope.setReportConf = function(){
		if($scope.selectedPages.length > 0){
			$('#reportDownloadModal').modal('show');
		} else{
			alert("Select a page!");
		}
	}

	$scope.downloadPdf = function(){
		$('#reportDownloadModal').modal('hide');
		var doc = new DOC.Document(null, $scope.report);
		doc.createMuliPageDoc($scope.selectedPages);
	}

	$scope.resizeImg = function(op){
		var img = $('.page-detail-thumb img');
		var w = img.width();
		if(op === '+'){
			img.width(w + 100);
		} else {
			img.width(w - 100);
		}
	}

	$scope.initialize();
})

.controller('ewgReportCtrl', function($scope, $state, $rootScope, $compile, $timeout, SharedService, MockService, NgAnimateService){
	$scope.initialize = function(){
		$scope.heading = SharedService.STATUS_VIEW;
		$scope.reports = [];
		$scope.doc = new DOC.Document($('#toCapture'));
		SharedService.getAllSeriesData( Constant.WIDGET_NAMES.REPORT_EDM ).then(function(data){
			$scope.reports = data;
		});
	}

	$scope.capturePage = function(){
		$rootScope.loader = true;
		$scope.doc.capture().then(function(canvas){
			if(!MockService.archivePages)
				MockService.archivePages = [];
			var img = {id: MockService.pageIdx++, data: canvas.toDataURL("image/jpg")};
			MockService.archivePages.push(img);
			$rootScope.loader = false;
		});		
	}

	$scope.showDetail = function(){
		SharedService.hideAllToolTips();
		$state.go("landing.gapdetails");
	}

	$scope.expandView = function(report){
		$scope.modalHead = report.name;
		$scope.report = report;
		var template = $('<div style="width:88%;height:66vh;" high-chart options="report.options" data="report.data"></div><div class="container-fluid margin-top-5"><i>{{report.description}}</i></div>');
		var container = $('#expandedViewModal .modal-body');
		container.html(template);
		$compile(container.contents())($scope);
		$('#expandedViewModal').modal('show');
	}

	$scope.drillDown = function(report){
		SharedService.currentReport = report;
		
		$state.go('landing.ewgIssue');
	}

	$scope.goPreviousScreen = function(){
		$state.go('landing.dashboardProfile');
	}

	$scope.initialize();
})

.controller('ewgIssueCtrl', function($scope, $state, $rootScope, $compile, $timeout, SharedService, MockService, NgAnimateService){
	$scope.initialize = function(){
		var report = SharedService.currentReport;
		$scope.heading = report.name;
		var issueType = report.key + "-Table";
		$scope.doc = new DOC.Document($('#toCapture'));
		SharedService.getAlldashboardTableData( issueType ).then(function(tableData){
			$scope.tableData = tableData
		});
	}

	$scope.capturePage = function(){
		$rootScope.loader = true;
		$scope.doc.capture().then(function(canvas){
			if(!MockService.archivePages)
				MockService.archivePages = [];
			var img = {id: MockService.pageIdx++, data: canvas.toDataURL("image/jpg")};
			MockService.archivePages.push(img);
			$rootScope.loader = false;
		});		
	}

	$scope.goPreviousScreen = function(){
		$state.go('landing.statusReport');
	}

	$scope.initialize();
})

.controller('heatMapCtrl', function($scope, $state, $compile, $timeout, SharedService, MockService, NgAnimateService){
	$scope.initialize = function(){
		$scope.heading = "Heat Map";
		//$scope.tableData = MockService.dashboardTable;
		SharedService.getAlldashboardTableData( "HeatMapTable" ).then(function(tableData){
			$scope.tableData = tableData
		});
	}

	$scope.getDetails = function(rowIndex, colIndex){
		var colName = $scope.tableData.columns[colIndex];
		var rowName = $scope.tableData.rows[rowIndex][0].name;
		SharedService.getAllHeatMapTableData(colName, rowName).then(function(data){
			$scope.reports = data;
			$('#expandedViewModal').modal('show');		
		});
	}

	$scope.goPreviousScreen = function(){
		$state.go('landing.dashboardProfile');
	}

	$scope.initialize();
})

.controller('scheduleTableCtrl', function($scope, $state, $rootScope, $compile, $timeout, SharedService, MockService, NgAnimateService){
	$scope.initialize = function(){
		$scope.statusView = SharedService.STATUS_VIEW;
		//$scope.tableData = MockService.dashboardTable;
		$scope.statusView = $scope.statusView || {"title":"Data Request Completed"};
		$scope.doc = new DOC.Document($('#toCapture'));
		SharedService.getAlldashboardTableData( $scope.statusView.title ).then(function(tableData){
			$scope.tableData = tableData
		});
	}

	$scope.capturePage = function(){
		$rootScope.loader = true;
		$scope.doc.capture().then(function(canvas){
			if(!MockService.archivePages)
				MockService.archivePages = [];
			var img = {id: MockService.pageIdx++, data: canvas.toDataURL("image/jpg")};
			MockService.archivePages.push(img);
			$rootScope.loader = false;
		});		
	}

	$scope.getDetails = function(rowIndex){
		var row = $scope.tableData.rows[rowIndex];
		var col1 = row[0];
		if(!col1.expanded){
			SharedService.getAlldashboardTableData($scope.statusView.title + "-" + col1.name).then(function(data){
				if(_.isEmpty(data)){
					alert("No data found!!!");
					return;
				}
				var rows = data.rows;
				insertRowsByIndex(rows, rowIndex+1);
				col1.expanded = true;
			});
		} else {
			collapseRows(rowIndex + 1);
			col1.expanded = false;
		}
	}

	function insertRowsByIndex(rows, index){
		angular.forEach(rows, function(row, idx){
			angular.forEach(row, function(col){
				col.alignRight = true;
			});
			$scope.tableData.rows.splice(index+idx, 0, row);
		});
	}

	function collapseRows(index){
		var flag = true;
		$scope.tableData.rows = _.reject($scope.tableData.rows, function(r, idx){			
			if(idx >= index){
				if(!r[0].alignRight) 
					flag = false;
				if(flag)
					return r[0].alignRight;
			}
		});
	}

	$scope.goPreviousScreen = function(){
		$state.go($scope.statusView.returnState);
	}

	$scope.initialize();
});