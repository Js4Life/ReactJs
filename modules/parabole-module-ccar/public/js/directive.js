angular.module('RDAApp.directives', [])

.directive('dashboardNav', function ($parse,  $compile) {
    return function (scope, element, attrs) {

        scope.$watch(attrs.dashboardNav, function (newVal) {
            if (newVal) {
                var data = [];
                angular.forEach( newVal , function( obj , key ){
                    data.push("<div class='col-lg-12' ng-click=goTo('"+ obj.title +"')> <img src='"+obj.label+"' style='margin:10px 10px 10px 10px;cursor:pointer;' /> </div>");
                })
                element.html(data);
                var d = $compile(element.contents())(scope);
            }
        })
    }
})

.directive('dashboardData', function ($parse,  $compile) {
    return function (scope, element, attrs) {

        scope.$watch(attrs.dashboardData, function (newVal) {
            if (newVal) {
                var data = [];
                if(newVal.length==1){
                    data.push("<div class='col-lg-12 well' >" + newVal[0].label + "</div>");
                }
                if(newVal.length % 2 == 0){
                    angular.forEach( newVal , function( obj , key ){
                        //data.push("<div class='col-lg-6 well' >" + obj.label + "</div>");
                        element.html(element.html()+"<div class='col-sm-6' ><div class='well'><table><tr><td  style='padding-right:15px;'><input type='checkbox' ng-model='"+obj.status+"' ng-change=selectedRisk('"+obj.id+"') /> </td> <td>"+ obj.label +"</td> </tr> </table> </div> </div>");
                    })
                }
                /*angular.forEach(data, function(obj){
                    element.html(element.html()+obj);
                })*/
                //element.html(data);
                var d = $compile(element.contents())(scope);
            }
        })
    }
})

.directive('navToggle', function ($parse,  $compile) {
    return function (scope, element, attrs) {
        element.on('click', function(event){
            var divWidth = element.next().css('width');
            var toggleNav = $(element.next()[0]);
            var toggleContent = $(element.parent().next().children()[0]);
            var nav = $(element.parent()[0]);
            if(divWidth == '10px'){
                toggleNav.animate({'width':'100%'},500);
                toggleContent.animate({'width':'100%'},500);
                nav.animate({'width':'4%'},500);  
            }
            else{
                toggleNav.animate({'width':'0px'},500);
                toggleContent.animate({'width':'100%'},500);
                nav.animate({'width':'2%'},500);
            }
        })
    }
})

.directive('timer', function ($parse) {
    return function (scope, element, attrs) {
        var cfgObj = angular.fromJson('{' + attrs.timer + '}');
        var hr = cfgObj.hr;
        var min = cfgObj.min;
        var sec = cfgObj.sec;

        $(function () {
            element.countdowntimer({
                hours   : hr,
                minutes : min,
                seconds : sec
            });
        });
    }
})

.directive('datepicker', function ($parse) {
    return function (scope, element, attrs) {
        var ngModel = $parse(attrs.ngModel);
        var cfgObj = angular.fromJson('{' + attrs.datepicker + '}');
        var fmt = cfgObj.format || 'yy/mm/dd';
        var ChangeYear = cfgObj.changeYear || false;
        var MinDate = null;
        var MaxDate = null;
        if (cfgObj.minDate == undefined)
            MinDate = 0;
        if (cfgObj.maxDate != undefined)
            MaxDate = cfgObj.maxDate;
        // $(function () {

        element.datepicker({
            inline: true,
            dateFormat: fmt,
            changeYear: ChangeYear,
            onSelect: function (dateText, inst) {
                scope.$apply(function (scope) {
                    // Change binded variable                         
                    ngModel.assign(scope, dateText);
                });
            },
            minDate: MinDate,
            maxDate: MaxDate
        });
        // });
    }
})

.directive('datValue', function ( $compile ){
    return function (scope, element, attrs) {
        var cfgObj = angular.fromJson(attrs.datValue);
        
        element.on('click', function(event) {
            
            element.parents().eq(3).children().children().eq(0)[0].innerHTML = cfgObj[0].id;
            element.parent().parent().parent().parent().children().eq(1).hide(1000);
            element.parent().parent().parent().parent().children().eq(0).show(600);
        })
        
        element.parent().parent().parent().parent().children().eq(0).on('click', function(event) {
            
            element.parent().parent().parent().parent().children().eq(1).attr('class', 'custScroll');
                $('.custScroll').perfectScrollbar({
                wheelSpeed: 7,
                wheelPropagation: false
            })
            element.parent().parent().parent().parent().children().eq(0).hide(1000);
            element.parent().parent().parent().parent().children().eq(1).show(600);
        })
    }
})

.directive('addClass', function ( $compile ){
    return function (scope, element, attrs) {
        
    }
})

.directive('dynamicContent', function ($parse, $compile) {
    return function (scope, element, attrs) {
        scope.$watch(attrs.dynamicContent, function (newVal) {
            if (newVal) {
                var data = newVal;
                element.html(data);
                var d = $compile(element.contents())(scope);
            }
        })
    }
})

.directive('modalDialog', function ($parse, $compile) {
    return function (scope, element, attrs) {
        var ngModel = $parse(attrs.ngModel);
        scope.$watch(attrs.ngModel, function (x, y, z) {
            if( x )
                $(element).modal('show');
            else
                $(element).modal('hide');
        });
    }
})

.directive('disableScroll', function ($parse, $compile) {
    return function (scope, element, attrs) {
        var ngModel = $parse(attrs.ngModel);
        scope.pointer = null;
        scope.scrollActivate = false;
        scope.$watch(attrs.ngModel, function (newObj) {
            var parentDiv = $(element).width()+300;
            var len = element.children().children().length;
            for(var i=0; i<len;i++){
                scope.pointer += $(element.children().children().eq(i)).width();
            }

            
            parentDiv = $(element).width();
            childDiv = $(element.children()).width();
            if(parentDiv < scope.pointer){
                scope.scrollActivate = true;
            }
        },true);
    }
})

.directive('vizGraph', function ($parse, $compile, SharedService) {
    return function (scope, element, attrs) {
        var cfgObj = attrs.vizGraph;
        var ngModel = $parse(attrs.ngModel);
        var config = cfgObj || {
            labelField:'name', isRandom : true,  edgeLabelField : 'relType', selectedEdgeColor : Constant.COLOR.AQUA,
            handlerData: { click : scope.clickNode, scope : scope },
            nodeShape: 'image',
            nodeImageMap: SharedService.nodeImageMap,
            nodeImageField: "type",
            //options: {physics: {hierarchicalRepulsion: {nodeDistance: 168}},hierarchicalLayout: {nodeSpacing: 132}}
        };

        scope.viz = new GRAPH.Viz ( $(element)[0], config );
        scope.$watch(attrs.ngModel, function (x, y, z) {
            if(x){
                scope.viz.initialize( x );
            }
        });
       
     }
})

.directive('visGraph', function ($parse, $compile, SharedService) {
    return {
        restrict: 'EA',
        scope: {
            data: '=',
            options: '='
        },
        link: function (scope, element, attrs) {
            var config = scope.options || {
                labelField:'name', isRandom : true,  edgeLabelField : 'relType', selectedEdgeColor : Constant.COLOR.AQUA,
                handlerData: { click : scope.clickNode, scope : scope },
                nodeShape: 'image',
                nodeImageMap: SharedService.nodeImageMap,
                nodeImageField: "type",
                hier: false
            };
            scope.$parent.viz = new GRAPH.Viz ( $(element)[0], config );
            scope.$watch('data', function (newVal) {
                if(!newVal) return;
                scope.$parent.viz.initialize( newVal );
            });
        }
    }
})

.directive('visTimeline', function () {
        return {
            restrict: 'EA',
            transclude: false,
            scope: {
                data: '=',
                options: '=',
                events: '='
            },
            link: function (scope, element, attrs) {
                var timelineEvents = [
                    'rangechange',
                    'rangechanged',
                    'timechange',
                    'timechanged',
                    'select',
                    'doubleClick',
                    'click',
                    'contextmenu'
                ];

                // Declare the timeline
                var timeline = null;

                scope.$watch('data', function () {
                    // Sanity check
                    if (scope.data == null) {
                        return;
                    }

                    // If we've actually changed the data set, then recreate the graph
                    // We can always update the data by adding more data to the existing data set
                    if (timeline != null) {
                        timeline.destroy();
                    }

                    // Create the timeline object
                    timeline = new vis.Timeline(element[0], scope.data.items, scope.data.groups, scope.options);

                    // Attach an event handler if defined
                    angular.forEach(scope.events, function (callback, event) {
                        if (timelineEvents.indexOf(String(event)) >= 0) {
                            timeline.on(event, callback);
                        }
                    });

                    // onLoad callback
                    if (scope.events != null && scope.events.onload != null &&
                        angular.isFunction(scope.events.onload)) {
                        scope.events.onload(timeline);
                    }
                    if(timeline != null){
                        timeline.setOptions(scope.options);
                    }
                });

                /*scope.$watchCollection('options', function (options) {
                    if (timeline == null) {
                        return;
                    }
                    timeline.setOptions(options);
                });*/
            }
        };
    })

.directive('toolbox', function ($parse, $compile) {
    return function (scope, element, attrs) {
        var cfgArr = angular.fromJson('{' + attrs.toolbox + '}');
        var panelMaxHeight = '80vh';

        element.attr('class', 'panel panel-default');
        var tmp = $('<div class="panel-body" style="max-height:' + panelMaxHeight +';overflow:hidden;"><div class="row" ng-repeat="tool in ' 
            + cfgArr.tools 
            + '"><div class="col-md-4"><img index="{{$index}}" class="drag-tool {{tool.class}}" src="{{tool.img}}" draggable=\'"helper":"clone"\'></img></div><div class="col-md-8 tool-text ellipsis-text" tooltip=\'"title":"{{tool.title}}","placement":"right"\'>{{tool.title}}</div></div>');

        element.append(tmp);
        $compile(element.contents())(scope);
    }
})

.directive('draggable', function ($parse, $compile) {
    return function (scope, element, attrs) {        
        var cfgObj = angular.fromJson('{' + attrs.draggable + '}');
        var config = {};
        config.containment = cfgObj.containment || null;
        config.helper = cfgObj.helper;
        config.handle = cfgObj.handle;
        if(cfgObj.appendTo)
            config.appendTo = cfgObj.appendTo;
        config.start = function(e, ui){

        }
        config.stop = function(e, ui){
            if(cfgObj.handler){
                var elemIdx = element.attr('index');
                scope[cfgObj.handler.onDragStop](ui.position, elemIdx);
            }
        }
        element.draggable(config);
    }
})

.directive('droppable', function ($parse, $compile) {
    return function (scope, element, attrs) {

        var cfgObj = angular.fromJson('{' + attrs.droppable + '}');
        var varName = attrs.layoutCell;
        //var idx = $(element).data('cell');
        var idx = $(element).attr('layout-cell');
        $(element).droppable({
            accept: cfgObj.accept,
            greedy: cfgObj.greedy || true,
            drop: function(event, ui) {
                var clsNames = ui.draggable.context.className.split(' ');
                var clsName = clsNames[1];
                if(cfgObj.dropHandler){
                    if(clsName == 'aggr')
                        scope[cfgObj.dropHandler['onAggrDrop']](ui , idx , element);
                    else if(clsName == 'img')
                        scope[cfgObj.dropHandler['onImgDrop']](ui , idx , element);
                    else
                        scope[cfgObj.dropHandler](ui , idx , element);
                }        
            }
        });
    }
})

.directive('gridLayout', function ($parse, $compile) {
    var htmlTmpl = {
        '2by2' : '<div class="container-fluid" style = "height:100%;"><div class="row" style = "height:50%;"><div class="col-xs-6 layout-style" layout-cell="0" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\'></div><div class="col-xs-6 layout-style" layout-cell="1" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\'></div></div><div class="row" style = "height:50%;"><div layout-cell="2" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\' class="col-xs-6 layout-style"></div><div layout-cell="3" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\' class="col-xs-6 layout-style"></div></div></div>',
        '1by2' : '<div class="row-fluid" style = "height:100%;"><div class="col-xs-6 layout-style" layout-cell="0" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\'></div><div class="col-xs-6" style = "height:100%"><div class="row layout-style" style = "height:50%" layout-cell="1" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\'></div><div class="row layout-style" style = "height:50%" layout-cell="2" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\'></div></div></div>',
        '2by1' : '<div class="row-fluid" style = "height:100%;"><div class="col-xs-6" style = "height:100%"><div class="row layout-style" style = "height:50%" layout-cell="0" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\'></div><div class="row layout-style" style = "height:50%" layout-cell="1" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\'></div></div><div class="col-xs-6 layout-style" layout-cell="2" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\'></div></div>',
        '1by1' : '<div class="container-fluid" style = "height:100%;"><div class="row layout-style" style = "height:50%;" layout-cell="0" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\'></div><div class="row layout-style" style = "height:50%;" layout-cell="1" droppable=\'"accept" : ".aggr, .editor, .img", "dropHandler" : {"onAggrDrop":"onAggrDrop", "onImgDrop":"onImgDrop"}\'></div></div>'
    };
    return function (scope, element, attrs) {
        scope.$watch(attrs.gridLayout, function (newVal, oldVal) {
            element.html('');
            var html = htmlTmpl[newVal];
            element.append($compile(html)(scope));
        });
    };
})

.directive('layoutCell', function ($parse, $compile, $timeout) {
    return function (scope, element, attrs) {
        var idx = attrs.layoutCell;
        
        $timeout(function() {
          scope.onCellDraw(element, idx);
        }, 10);

        $(element).bind('click', function(){
            scope.openTextEditor(idx);            
        });
    };
})

.directive('removable', function ($parse, $compile) {
    return function (scope, element, attrs) {
        var ngModel = scope[attrs.removable];

        element.attr('class', 'report-frame');
        
        element.draggable({
            containment: "parent",
            start: function( e, ui ) {
                scope.$apply(function(){
                    scope.$parent.recycleIsVisible = true;
                    scope.$parent.currentObject = ngModel;
                });  
            }, 
            stop: function( e, ui ) {
                scope.$apply(function(){
                    scope.$parent.recycleIsVisible = false;
                }); 
            } 
        });
    }
})

.directive('recyclebin', function ($parse, $compile) {
    return function (scope, element, attrs) {

        element.attr('class', 'recyclebin');
        
        element.droppable({
            accept: '.report-frame',
            drop: function(event, ui) {
                scope.$apply(function(){
                    $('#confirmModal').modal('show');
                });                            
            }
        });
    }
})

.directive('popover', function ($parse, $compile) {
    return function (scope, element, attrs) {
        var cfgObj = angular.fromJson('{' + attrs.popover + '}');
        
        element.popover({
            animation: cfgObj.animation || true,
            container: 'body',
            placement: cfgObj.placement || 'bottom',
            trigger: cfgObj.trigger || 'focus',
            content: cfgObj.content || "No text"
        });

        element.bind('click', function(){
            element.attr('class', 'btn btn-success');
            scope.doEnable = true;
        });

        scope.$watch('doEnable', function (newVal, oldVal) {
            if(!newVal)
                element.attr('class', 'btn btn-default');
        });
    }
})

.directive('menu', function ($parse, $compile) {
    return function (scope, element, attrs) {

        var cfgArr = angular.fromJson('{' + attrs.menu + '}');
        element.attr('class', 'list-group');
        var tmp = $('<a class="list-group-item pointer puff" ng-repeat="item in ' + cfgArr.menu + '" active=\'"default": "0", "index": "{{$index}}"\' href="{{item.actionurl}}"><span class="glyphicon glyphicon-chevron-right pull-right"></span>{{item.name}}</a>');
        element.append(tmp);
        $compile(element.contents())(scope);
    }
})

.directive('tablegen', function ($parse,  $compile) {
    return function (scope, element, attrs) {
        var cfgObj = angular.fromJson('{' + attrs.tablegen + '}'); 
        var tdObj = scope[ cfgObj.obj ];
        //tdObj = angular.fromJson( tdObj);
        if(  tdObj.charAt(0) == '['  ){
            var tdObj = eval(tdObj);
            var str = '<table class="table"><thead>';
            var colNames = [];              
            if( tdObj.length > 0 )
                for(var i in tdObj[0])
                    str += '<th>' + i + "</th>";
            str += '</thead><tbody>';
            for( var i in tdObj ){
                var rObj = tdObj[i];
                str += '<tr>';
                for( var p in rObj )
                    str += '<td>' + rObj[p] + '</td>';
                str += '</tr>';
            }
            str += '</tbody></table>';
            element.append( str );
        }else
            element.append(tdObj);
    }
})

.directive('active', function ($parse, $compile) {
    return function (scope, element, attrs) {
        var cfgObj = angular.fromJson('{' + attrs.active + '}');
        if(cfgObj.default == cfgObj.index){
            element.attr('class', 'list-group-item pointer puff active'); 
        }
        element.bind('click', function() {
            element.parent().children().attr('class', 'list-group-item pointer puff'); 
            element.attr('class', 'list-group-item pointer puff active');           
        });
              
    }
})

.directive('collapsible', function ($parse, $compile) {
    return function (scope, element, attrs) {
        var cfgObj = angular.fromJson('{' + attrs.collapsible + '}');
        var actObj = scope[cfgObj.actObj];
        var closeType = cfgObj.closeType;
        cfgObj.width = cfgObj.width || Constant.COLLAPSE_WIDTH ;
        var nameHead = actObj.shortName || actObj.name;
        element.attr('class', 'panel panel-default panel-collapsible');
        element.css({'width': cfgObj.width + 'px', 'position': 'relative', 'display':'inline-block', 'margin':'10px'});
        var elemHtml = '<div class="panel-heading"><h3 class="panel-title" tooltip=\'"title":"' + actObj.name + '"\'>' + nameHead + '</h3><span class="pull-right clickable collapsible"><i class="glyphicon glyphicon-chevron-up"></i></span>';
        if( cfgObj.closable )
            elemHtml += '<span class="pull-right closable" style="margin-right:10px;"><i class="glyphicon glyphicon-remove"></i></span>';
        if( cfgObj.editable )
            elemHtml += '<span class="pull-right editable" style="margin-right:10px;"><i class="glyphicon glyphicon-edit"></i></span>';
        if( cfgObj.viewable )
            elemHtml += '<span class="pull-right viewable" style="margin-right:10px;"><i class="glyphicon glyphicon-eye-open"></i></span>';
        elemHtml += '</div>';
        element.append($(elemHtml));
        if(cfgObj.type = Constant.COLLAPSE_LIST){
            if(cfgObj.hasCheck){
                tmp = $('<div id=' + actObj.id + ' class="panel-body"><ul class="list-group"><li class="list-group-item" ng-repeat="attr in ' + cfgObj.actObj + '.attributes"><input type="checkbox" ng-model="attr.isChecked" ng-change="onAttrCheck(attr)" style="margin-right:7px;" >{{attr.name}}</li></ul></div>');
            }
            else{
                tmp = $('<div id=' + actObj.id + ' class="panel-body" style="overflow:scroll"><ul class="list-group"><li class="list-group-item" ng-repeat="attr in ' + cfgObj.actObj + '.attributes">{{attr.name}}</li></ul></div>');
            }
            tmp.css({ 'height':Constant.COLLAPSE_HEIGHT-38 + 'px'}); 
        }        
        element.append(tmp);
        $compile(element.contents())(scope);
        element.find('.collapsible').bind('click', function(){
            if ($(this).hasClass('panel-collapsed')) {
                element.find('.panel-body').slideDown();
                $(this).removeClass('panel-collapsed');
                $(this).find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
            }
            else {
                element.find('.panel-body').slideUp();
                $(this).addClass('panel-collapsed');
                $(this).find('i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
            }
        });
        element.find('.viewable ').bind('click', function(){
            scope.viewTable( actObj );
        });
        element.find('.closable ').bind('click', function(){
            if(closeType == Constant.CLIENT_SIDE)
                scope.closeTemoraryTable( actObj );
            else
                scope.closeTable( actObj );
        });
        element.find('.editable ').bind('click', function(){
            scope.editTable( actObj );
        });
        element.find(".panel-body").bind('click', function(e){
            if(cfgObj.selectable){
                if(element.hasClass('active-panel')){
                    element.removeClass('active-panel');
                    scope.$parent.selectedPanels = _.reject(scope.$parent.selectedPanels, function(obj){ return obj.name == actObj.name; });
                }
                else{
                    element.addClass('active-panel');
                    if(!scope.$parent.selectedPanels)
                        scope.$parent.selectedPanels = [];
                    scope.$parent.selectedPanels.push(actObj);
                }
            }
        });
    }
})

.directive('collapselist', function ($parse, $compile, $timeout) {
    return function (scope, element, attrs) {
        $timeout(function(){
            var cfgObj = angular.fromJson('{' + attrs.collapselist + '}');
            var actObj = scope[cfgObj.actObj];
            var closeType = cfgObj.closeType;
            actObj.width = actObj.width || Constant.COLLAPSE_WIDTH;
            actObj.height = actObj.height || Constant.COLLAPSE_HEIGHT;
            actObj.index = element.attr('index');
            actObj.topLeft = actObj.topLeft || scope.mapper.getPaneTopLeft(actObj.width, actObj.height);
            var pane = scope.mapper.drawPanel(actObj.name, actObj.attributes, actObj.width, actObj.height, actObj.topLeft, {'index':actObj.index});
            if(actObj.arrows){
                angular.forEach( actObj.arrows, function(arrow){
                    var pnt = {start: arrow.start, end: arrow.end}
                    var arr = pane.initializeArrow( pnt );
                    pane.addArrow( arr );
                    arr.updatePosition( pnt );
                    arr.setSource( arrow.getSource() );
                    arr.setTarget( arrow.getTarget() );
                    pane.setSources( actObj.sources );
                    pane.setTargets( actObj.targets );
                });
            }
            if(actObj.sources){
                pane.setSources( actObj.sources )
            }
            var nameHead = actObj.shortName || actObj.name;
            element.attr('class', 'panel panel-default panel-collapsible');
            element.css({'width':actObj.width + 'px', 'top': actObj.topLeft.getY() + 'px', 'left': actObj.topLeft.getX() + 'px', 'position': 'absolute'});
            
            var elemHtml = '<div class="panel-heading"><h3 class="panel-title" tooltip=\'"title":"' + actObj.name + '"\'>' + nameHead + '</h3><span class="pull-right clickable"><i class="glyphicon glyphicon-chevron-up"></i></span>';
            if( cfgObj.closable )
                elemHtml += '<span class="pull-right closable" style="margin-right:10px;"><i class="glyphicon glyphicon-remove"></i></span>';
            if( cfgObj.viewable )
                elemHtml += '<span class="pull-right viewable" style="margin-right:10px;"><i class="glyphicon glyphicon-eye-open"></i></span>';
            elemHtml += '</div>';
            element.append(elemHtml);
            var tmp = $('<div class="panel-body"><ul class="list-group"><li class="list-group-item" ng-repeat="attr in ' + cfgObj.actObj + '.attributes"><input type="checkbox" ng-model="attr.isChecked" ng-change="onAttrCheck(attr)" style="margin-right:7px;" >{{attr.name}}</li></ul></div>');
            tmp.css({'width':actObj.width + 'px', 'height':actObj.height-38 + 'px'});        
            element.append(tmp);
            $compile(element.contents())(scope);

            element.find('.clickable').bind('click', function(){
                if ($(this).hasClass('panel-collapsed')) {
                    element.find('.panel-body').slideDown();
                    $(this).removeClass('panel-collapsed');
                    $(this).find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
                }
                else {
                    element.find('.panel-body').slideUp();
                    $(this).addClass('panel-collapsed');
                    $(this).find('i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
                }
            });

            element.find('.viewable ').bind('click', function(){
                scope.viewTable( actObj );
            });
            element.find('.closable ').bind('click', function(){
                if(closeType == Constant.CLIENT_SIDE)
                    scope.closeTemoraryTable( actObj );
                else
                    scope.closeTable( actObj );
            });
        }, 1);
    }
})

.directive('fileUpload', function ($parse) {
    return function (scope, element, attrs) {
        
        var reader = new FileReader();
        var ngModel = $parse(attrs.ngModel);
        var fname = '';
        var ftype = '';
        reader.onload = function (e) {
            scope.$apply(function (scope) {  
                var obj = { name: fname, mime: ftype, data :e.target.result }                     
                ngModel.assign(scope, obj);
            });
        }

        element.on('change', function() {
            reader.onloadend = function () {
                if (reader.result) {
                    console.log($(element).attr('src'));
                }
            };
            fname = element[0].files[0].name;
            ftype = element[0].files[0].type;
            reader.readAsDataURL(element[0].files[0]);
        });
    };
})

.directive('tooltip', function ($parse) {
    return function (scope, element, attrs) {
        var cfgObj = angular.fromJson('{' + attrs.tooltip + '}');

        element.tooltip({
            animation: cfgObj.animation || true,
            container: 'body',
            placement: cfgObj.placement || 'bottom',
            trigger: cfgObj.trigger || 'hover focus',
            title: cfgObj.title || "No text"
        });

        element.on('shown.bs.tooltip', function () {
            if(cfgObj.autoHideDelay){
                setTimeout(function () {
                    element.tooltip('hide');
                }, cfgObj.autoHideDelay);
            }           
        });
    }
})

.directive('loader', function () {
    return {
        restrict: 'E',
        replace:true,
        template: '<div class="loading"></div>',
        link: function (scope, element, attr) {
              scope.$watch('loader', function (val) {
                  if (val)
                      $(element).show();
                  else
                      $(element).hide();
              });
        }
    }
})

.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
                scope.$apply(function (){
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    }
})

.directive('progressBar', function($compile) {
    return function (scope, element, attrs) {
        var cfgObj = angular.fromJson('{' + attrs.progressBar + '}');
        var label = "";
        cfgObj.value = parseFloat(cfgObj.value).toFixed(1);
        var percentage = cfgObj.value / (cfgObj.max - cfgObj.min) * 100;

        element.addClass('progress');
        var tmp = $('<div class="progress-bar progress-bar-striped" ng-class="{\'progress-bar-success\' :' + percentage + '>=60,\'progress-bar-warning\' :' + percentage + '>=30 && ' + percentage + '<60,\'progress-bar-danger\' :' + percentage + '<30}" role="progressbar"></div>');
        tmp.css('width', percentage + '%');
        if(!element.hasClass('progress-bar-thin'))
            label = $('<span>' + cfgObj.value + '%</span>');
        else{
            scope.$parent.progressPercentage = parseFloat( percentage).toFixed(1) + '%';
        }
        tmp.append(label);
        element.append(tmp);
        $compile(element.contents())(scope);
    }
})

.directive('highChart', function () {
    'use strict';
    return {
        restrict: 'EA',
        transclude: false,
        scope: {
            data: '=',
            options: '='
        },
        link: function (scope, element, attr) {
            var chart = null;

            scope.$watch('data', function () {
                if (scope.data == null) {
                    return;
                }
                chart = new GRAPH.PlotWrapper(element[0], scope.options);
                chart.draw(scope.data);
            });
        }
    };
})

.directive('highMap', function () {
    return {
        restrict: 'EA',
        scope: {
            options: '=',
            data: '='
        },
        link: function (scope, element, attrs) {
            var config = scope.options || {
                title: "",
                handlerData: { click : scope.clickMap, scope : scope }
            };
            scope.$parent.$parent.map = new MAP.PlotMap( element[0], config );
            scope.$watch('data', function (newVal) {
                if(!newVal) return;
                scope.$parent.$parent.map.drawMap( newVal );
            });
        }
    }
});
