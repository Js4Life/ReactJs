var GRAPH = (function( wrapObj ){

	var defaultVizOptions = {
	           stabilize: true,
	           physics: {barnesHut: {gravitationalConstant: 1, centralGravity: 0, springConstant: 0}},
	           smoothCurves: {dynamic:false, type: "continuous", roundness:0}
	    };

	    var hierVizOptions = {physics: {hierarchicalRepulsion: {nodeDistance: 168}},hierarchicalLayout: {nodeSpacing: 132}};

	    defaultVizOptions = {physics: {barnesHut: {enabled: false}, repulsion: {springLength: 250, springConstant: 0.056, nodeDistance: 175, damping: 0.5}},
	                        smoothCurves: {dynamic:false, type: "continuous",roundness:0}}
      var groupVizOptions = {
        physics: {
          stabilization: true,
          /*barnesHut: {
            enabled: true,
            springConstant: 0.05,
            springLength: 100,
            avoidOverlap: 1
          }*/
        },
        smoothCurves: {dynamic:false, type: "continuous", roundness:0}
      }                  
                          
    function Graph( container , cfg){
           this.container = container;
           this.config = cfg ;
           this.network = null;
           this.graphData = null;
           this.defaultNodeColor = this.config.nodeColor || Constant.COLOR.GREEN;
           this.defaultEdgeColor = this.config.edgeColor || Constant.COLOR.GREEN;
           this.selEdgeColor = this.config.selectedEdgeColor || Constant.COLOR.AQUA;
           this.config.nodeShape = this.config.nodeShape || 'dot';
           this.callBackScope = this.config.handlerData.scope || window;
           this.nodeExtraInfo = {};
           this.edgeExtraInfo = {};
           this.unusedConnections = [];
           this.connectedNodes ={};
           this.connectedEdges ={};
           this.vistedMap = {};
           if( cfg.hier)
        	   this.vizOptions = hierVizOptions;
           else
        	   this.vizOptions = defaultVizOptions;
           if(cfg.springLength)
             this.vizOptions.physics.repulsion.springLength = cfg.springLength;
           if(cfg.group)
              this.vizOptions = groupVizOptions;
    };

    
    function createAVisNode( scope , obj){

           var config = scope.config;

           var id = obj[config.idField];
           var nodeObj = { id : id };
           scope.vistedMap[id] = false;
           //copy all properties
           for( var name in obj )
             nodeObj[name] = obj[name];

           nodeObj.label = obj[config.labelField];
           nodeObj.shape = obj.shape || scope.config.nodeShape || 'image';
           nodeObj.color = obj.color || scope.defaultNodeColor;
           config.nodeColorMap = config.nodeColorMap || [];
           var callBackScope = config.handlerData.scope || window;

           if( angular.isFunction( config.nodeColorMap ))
             nodeObj.color = config.nodeColorMap.call(callBackScope,obj);
           else if( config.nodeColorMap )
             nodeObj.color = obj.color || config.nodeColorMap[obj[config.nodeColorField]] || scope.defaultNodeColor;
           if( config.nodeShape == 'image')
              nodeObj.image = config.nodeImageMap[obj[config.nodeImageField]];
           if( config.isRandom ){
             nodeObj.x = obj.x;//Math.floor((Math.random() * 500) + 1);
             nodeObj.y = obj.y;//Math.floor((Math.random() * 500) + 1);
             nodeObj.allowedToMoveX = true;
             nodeObj.allowedToMoveY = true;
           }
           if( config.extraInfo )
             scope.nodeExtraInfo[nodeObj.id] = obj[config.extraInfo];
           return nodeObj;
    }

    function convertFromVisNode( scope , visNode){

           var nodeObj = {};
           var config = scope.config;

           nodeObj[config.idField] = visNode.id ;
           nodeObj[config.labelField] = visNode.label ;

           if( config.extraInfo ){
             nodeObj[config.extraInfo] = scope.nodeExtraInfo[visNode.id];
             nodeObj[config.nodeImageField] = scope.nodeExtraInfo[visNode.id].type;
           }
           if(config.group){
              nodeObj.group = visNode.group;
           }
           if(visNode.shape){
              nodeObj.shape = visNode.shape;
           }

           nodeObj.allowedToMoveX = visNode.allowedToMoveX;
           nodeObj.allowedToMoveY = visNode.allowedToMoveY;
           nodeObj.x = visNode.x;
           nodeObj.y = visNode.y;

           return nodeObj;
    }


    function createAVisEdge( scope , obj){
           var con =  {};
           var config = scope.config;
           con.to = obj[config.toField];
           con.from = obj[config.fromField];

           config.edgeColorMap = config.edgeColorMap || [];
           var callBackScope = config.handlerData.scope || window;
           con.style = config.connectorStyle;
           if(!config.group){
              if( angular.isFunction( config.edgeColorMap ))
                con.color = obj.color || config.edgeColorMap.call(callBackScope, scope.findNodeById(obj.from),scope.findNodeById(obj.to),obj.relType);
              else
                con.color = obj.color || config.edgeColor || scope.defaultEdgeColor;
           } else {
              if(obj.color)
                con.color = obj.color;
           }
           if(obj.width)
              con.width = obj.width;
           
            con.label = obj[config.edgeLabelField];
           
           if( scope.findNodeById( con.to ) == undefined
              || scope.findNodeById( con.from ) == undefined ){
                scope.unusedConnections.push( con );
                return null;
           }
           return con;
    }

    function createInitialData( data, scope){

           scope.graphData = {nodes:new vis.DataSet(),edges:new vis.DataSet()};
           angular.forEach( data.nodes , function( obj , key ) {
              scope.graphData.nodes.add( createAVisNode(scope ,obj));
           });
           angular.forEach( data.edges , function( obj , key ) {
              addConnectionWrapper (scope , obj );
           });
    }

    function addConnectionWrapper( scope , obj , parentId ){
          var edge = createAVisEdge( scope , obj );
          if( edge != null ){
            scope.graphData.edges.add( edge ) ;
            if( parentId == undefined ){
              parentId = edge.from;
              updateNodeNetwork( scope , parentId , edge.to );
            }
            //add to the network
            updateEdgeNetwork( scope , parentId , edge.id);
          }
    }

    function updateNodeNetwork( scope ,  parNodeId , childnodeId ){
          if( scope.connectedNodes[parNodeId] == undefined )
              scope.connectedNodes[parNodeId] = [];
          scope.connectedNodes[parNodeId].push( {id:childnodeId , visited : false} );
    }
    function updateEdgeNetwork( scope , parNodeId , edgeId ){
          if( scope.connectedEdges[parNodeId] == undefined )
              scope.connectedEdges[parNodeId] = [];
          scope.connectedEdges[parNodeId].push( edgeId );
    }

    function drawUnusedConnections ( scope ){
        var newUnusedConns = [];
        angular.forEach( scope.unusedConnections , function( con , idx ){
            if( scope.findNodeById( con.to ) != undefined
              && scope.findNodeById( con.from ) != undefined ){
                scope.graphData.edges.add(con);
              }
            else
                newUnusedConns.push (con);
        });
        scope.unusedConnections = newUnusedConns;
    }
    Graph.RELATIONSHIPS = { ISA : 'isA' , HASA : 'hasA' , RELATESTO : 'relatesTo' };

    Graph.prototype = {

           initialize : function(data){
                var options = this.config.options || this.vizOptions;

                this.config.idField = this.config.idField || 'id';
                this.config.toField = this.config.toField || 'to';
                this.config.fromField = this.config.fromField || 'from';
                this.config.labelField = this.config.labelField || 'label';
                this.config.connectorStyle = this.config.connectorStyle || 'arrow';
                this.config.edgeLabelField = this.config.edgeLabelField || 'label';

                createInitialData( data, this );
                options.allowedToMoveX = options.allowedToMoveY = true;
                this.network = new vis.Network( $(this.container)[0] , this.graphData , options);
                var handlerObj = this.config.handlerData;
                var scope = this;
                //Click Event
                if( handlerObj.click ){
                    this.network.on('click', function(properties) {
                        handlerObj.click.call( handlerObj.scope , properties.nodes[0]);
                    });
                }
                if( handlerObj.select ){
                     this.network.on('select', function(properties) {
                      if( properties.edges.length > 0 ){
                        var nodes = scope.findNodesForEdge(  properties.edges[0] );
                        handlerObj.select.call( handlerObj.scope , scope.findEdgeById(properties.edges[0])
                            ,nodes[0], nodes[1]);
                      }
                    });
                }

           },

           selectEdge : function( edgeId ) {
            var edgeObj = this.findEdgeById( edgeId );
            this.setEdgeExtraInfo( edgeObj.id , 'selected', true);
            this.updateEdgeColor( edgeObj.id ,this.selEdgeColor )
           },
           unSelectEdge : function( edgeId ) {
            var edgeObj = this.findEdgeById( edgeId );
            this.setEdgeExtraInfo( edgeObj.id , 'selected', false);
            this.updateEdgeColor( edgeObj.id ,this.defaultEdgeColor )
           },
           isEdgeSelected : function( edgeId ){
            var edgeObj = this.findEdgeById( edgeId );
            var info = this.getEdgeExtraInfo( edgeId );
            return info.selected;
           },
           getAllNodes : function() {
              var nodeArr = [];
              var scope = this;
              this.graphData.nodes.forEach( function (obj,key){
                nodeArr.push( convertFromVisNode( scope , obj) );
              });
              return nodeArr;
           },
           getExtraInfo : function( nodeId ){
                return this.nodeExtraInfo[nodeId] || {};
           },
           getEdgeExtraInfo : function( edgeId ){
               if( this.edgeExtraInfo[edgeId] == undefined)
                 this.edgeExtraInfo[edgeId] = {};
               return this.edgeExtraInfo[edgeId];
           },
           setExtraInfo : function( nodeId , propName , value ){
                var extraInfo = this.getExtraInfo(nodeId);
                extraInfo[propName] = value;
           },
           setEdgeExtraInfo : function( edgeId , propName , value ){
               var extraInfo = this.getEdgeExtraInfo(edgeId);
               extraInfo[propName] = value;
           },

           updateNodeColor : function( nodeId , color){
                var visNode = this.network.nodes[nodeId];
                visNode.options.color.background = visNode.options.color.highlight.background = color;
                this.network._redraw();
           },
           updateNodeImage : function( nodeId , image){
                var visNode = this.network.nodes[nodeId];
                //visNode.options.image = this.config.nodeImageMap[image];
                visNode.fontStrokeColor = '#FF0000';
                this.network._redraw();
           },
           selectNode : function( nodeId ){
                var visNode = this.network.nodes[nodeId];
                visNode.selected = true;
                this.network._redraw();
           },
           clearSelectedNodes : function(){
                var selectedNodes = _.filter(this.network.nodes, function(aNode){ return aNode.selected == true; });
                angular.forEach(selectedNodes, function(aNode){
                  aNode.selected = false;
                });
           },
           updateEdgeColor : function( edgeId , color){
               var visEdge = this.network.edges[edgeId];
               visEdge.options.color.color = visEdge.options.color.highlight = color;
               this.network._redraw();
           },
           addChildNodes : function(  nodes , parNodeId , relType){
                var scope = this;
                angular.forEach(nodes , function(obj,key){
                     var id = obj[scope.config.idField];
                     var newNode ;
                     newNode =  scope.findNodeById(id);
                     if( newNode == undefined || newNode == null){
                        newNode = createAVisNode(scope , obj);
                        scope.graphData.nodes.add(newNode);
                     }
                     if( parNodeId )
                      updateNodeNetwork( scope, parNodeId , newNode.id);//addConnectionWrapper (scope , { from : parNodeId , to : newNode.id} );
                     if( relType ){
                        var obj = {};
                        obj[scope.config.toField] = id;
                        obj[scope.config.fromField] = parNodeId;
                        obj[scope.config.edgeLabelField] = relType;
                        addConnectionWrapper(scope , obj , parNodeId);
                     }
                });
                drawUnusedConnections(scope);
           },
           addConnections : function( connections  , parNodeId ){
                var scope = this;
                angular.forEach(connections , function(obj,key){
                    addConnectionWrapper (scope , obj , parNodeId);
                });
           },
           findNodeById : function ( nodeId){
                return this.graphData.nodes.get( nodeId );
           },
           deleteNodeById : function( nodeId , deleteSelf){
                var childNodes = [];
                this.vistedMap[nodeId] = true;
                childNodes = this.findConnectedNodes( nodeId , childNodes)
                for( var i  in childNodes){
                  this.graphData.nodes.remove(childNodes[i]);
                  //this.graphData.edges.remove(childNodes[i].edgeId);
                }
                if( deleteSelf )
                  this.graphData.nodes.remove(nodeId);
                for( var i in this.vistedMap)
                  this.vistedMap[i] = false;
                return childNodes;
           },
           findConnectedNodes : function( nodeId , nodeArr){
                var conNodes = this.connectedNodes[nodeId];
                if( conNodes == undefined )
                  return nodeArr;
                for(var i in conNodes){
                  var nodeObj = conNodes[i];
                    var nodeId = nodeObj.id;
                    if( this.vistedMap[nodeId] != undefined && this.vistedMap[nodeId] == false){
                      nodeArr.push( nodeId );
                      this.vistedMap[nodeId] = true;
                      this.findConnectedNodes( nodeId , nodeArr);
                    }
                }
                return nodeArr;
           },
           findEdgesforNodes : function( selNodes ){
                //var edges = new vis.DataSet();
                var edges = [];
                this.graphData.edges.forEach( function (aEdge, key){
                    var edge = {};
                    edge.id = aEdge.id;
                    edge.to = aEdge.to;
                    edge.from = aEdge.from;
                    edge.relType = aEdge.label;
                    edge.weight = 1;
                    edges.push( edge );
                });
                return edges;
           },
           findNodesForEdge : function( edgeId ){
             var edgeObj = this.graphData.edges.get( edgeId );
             if( edgeObj )
               return [ this.findNodeById(edgeObj.from) , this.findNodeById(edgeObj.to) ];
           },
           deleteNodesAndEdgesNotIn : function( nodes , edges ){

                this.graphData.nodes.forEach( function(){

                });

                this.graphData.edges.forEach( function(){

                });

           },
           updateLabel : function(nodeId , label){
                this.graphData.nodes.update({id: nodeId, label: label});
                this.network._redraw();
           },
           getChildNodesById : function(nodeId){
                return this.connectedNodes[nodeId];
           },

           findEdgeById : function( edgeId ){
             return this.graphData.edges.get( edgeId );
           },
           
           storeNetworkPosition : function(){
             this.network.storePosition();
           },

           selectNodeByName : function(name){
              var nodes = _.values(this.graphData.nodes._data);
              var targetNodes = _.filter(nodes, function(aNode){ 
                var str = aNode.name.toUpperCase();
                subStr = name.toUpperCase();
                if(str.indexOf(subStr) != -1)
                  return aNode; 
              });
              var _this = this;
              this.clearSelectedNodes();
              angular.forEach(targetNodes, function(aNode){
                _this.selectNode(aNode.id);
              });              
           },

           redraw : function(){
              this.network._redraw();
           }
    };

    wrapObj = { Viz : Graph };
    return wrapObj;

 })(GRAPH|| {});