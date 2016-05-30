var GRAPH = (function( wrapObj ){
 
    function Graph( container , cfg){
           this.container = container;
           this.config = cfg ;
           this.network = null;
           this.graphData = null;
           this.defaultNodeColor = this.config.nodeColor || 'green';
           this.defaultEdgeColor = this.config.edgeColor || 'green';
           this.config.nodeShape = this.config.nodeShape || 'dot';
           this.nodeExtraInfo = {};
           this.unusedConnections = [];
    };
    
    var defaultVizOptions = {
           stabilize: true,
           physics: {barnesHut: {gravitationalConstant: 1, centralGravity: 0, springConstant: 0}},
           smoothCurves: {dynamic:false, type: "continuous",roundness:0}
    };
    
    function createAVisNode( scope , obj){
  
           var nodeObj = {};
           var config = scope.config;
           
           nodeObj.id = obj[config.idField];
           nodeObj.label = obj[config.labelField];
           nodeObj.shape = scope.config.nodeShape;
           nodeObj.color = scope.defaultNodeColor;
           if( config.nodeColorMap )
             nodeObj.color = config.nodeColorMap[config.nodeColorField] || scope.defaultNodeColor;
             
           if( config.isRandom ){
             node.x = Math.floor((Math.random() * 500) + 1);
             node.y = Math.floor((Math.random() * 500) + 1);
             node.allowedToMoveX = true;
             node.allowedToMoveY = true;
           }
           if( config.extraInfo ){
             var obj  = {};
             for( var i in config.extraInfo)
                obj[config.extraInfo[i]] = obj[config.extraInfo[i]];
             scope.nodeExtraInfo[nodeObj.id] = obj;
           }
           return nodeObj;
    }
    function createAVisEdge( scope , obj){
           var con =  {};
           var config = scope.config;
           con.to = obj[config.toField];
           con.from = obj[config.fromField];
           con.color = config.edgeColor || scope.defaultEdgeColor;
           con.label = obj[config.edgeLabelField];
           con.style = config.connectorStyle;
           if( scope.findNodeById( con.to ) == undefined 
              || scope.findNodeById( con.from ) == undefined ){
              scope.unusedConnections.push( con );
            return null;
           }
           else
            return con;
    }
    function createInitialData( data, scope){
             
           var nodes = new vis.DataSet();
           angular.forEach( data.nodes , function( obj , key ) {
                nodes.add( createAVisNode(scope ,obj));
           });
           var edges = new vis.DataSet();
           angular.forEach( data.edges , function( obj , key ) {
           var edge = createAVisEdge( scope , obj );
           if( edge != null )
                edges.add( edge ) ;
           });
           return {nodes: nodes , edges: edges };
    }
    
    function addUnusedConnections( scope , con ){
          if( scope.findNodeById( con.to ) == undefined 
              || scope.findNodeById( con.from ) == undefined )
            scope.unusedConnections.push( con );
          else
            scope.graphData.edges.add( con );
    }
       
    Graph.prototype = {
           
           initialize : function(data){
                var options = this.config.options || defaultVizOptions;
             
                this.config.idField = this.config.idField || 'id';
                this.config.toField = this.config.toField || 'to';
                this.config.fromField = this.config.fromField || 'from';
                this.config.labelField = this.config.labelField || 'label';
                this.config.connectorStyle = this.config.connectorStyle || 'arrow';
                this.config.edgeLabelField = this.config.edgeLabelField || 'label';
                this.graphData = createInitialData( data, this );
                                
                this.network = new vis.Network( $(this.container)[0] , this.graphData , options);
                var handlerObj = this.config.handlerData;
                //Click Event
                if( handlerObj.click ){
                    this.network.on('click', function(properties) {
                        handlerObj.click.call( handlerObj.scope , properties.nodes[0]);
                    });
                }
           },
           getExtraInfo : function( nodeId ){
                return this.nodeExtraInfo[nodeId];
           },
           updateNodeColor : function( nodeId , color){
                var visNode = $scope.network.nodes[nodeId];
                visNode.options.color.background = visNode.options.color.highlight.background = color;
           },
           addChildNodes : function(  nodes , parNodeId){
                var scope = this;
                 angular.forEach(nodes , function(obj,key){
                    var newNode = createAVisNode(scope , obj);
                    scope.graphData.nodes.add(newNode);
                    if( parNodeId ){
                      scope.graphData.edges.add( 
                          createAVisEdge(scope , { from : parNodeId , to : newNode.id}));
                    }
                });
           },
           addConnections : function( connections ){
                var scope = this;
                angular.forEach(connections , function(obj,key){
                    var edge = createAVisEdge(scope , obj);
                    if( edge != null )
                      scope.graphData.edges.add( edge );
                });
           },

           findNodeById : function ( nodeId){
                return this.graphData.nodes.get( nodeId );
           }

           
    };
    wrapObj = { Viz : Graph };
    return wrapObj;
 
 })(GRAPH|| {});
