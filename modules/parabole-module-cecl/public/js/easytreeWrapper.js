var TREE = (function(wrapObj){
    function Tree( container , cfg ){
        this.container = container;
        this.config = cfg;
        this.treeConfig = initDefaults(this.config);
        this.easyTree = {};
    }

    function initDefaults(config)
    {
        var obj = {};
        obj.enableDnd = config.enableDnd || false;
        obj.slidingTime = config.slidingTime || 300;
        obj.minOpenLevels = config.minOpenLevels || 0;
        obj.ordering = config.ordering || null;
        obj.disableIcons = config.disableIcons || false;
        obj.allowActivate = config.allowActivate || false;
        if(config.handlerData){
            if(config.handlerData.onNodeDrop)
                obj.dropped = config.handlerData.onNodeDrop;
            if(config.handlerData.onOpenLazyNode)
                obj.openLazyNode = config.handlerData.onOpenLazyNode;
        }
        return obj;
    }

    function convertToAnEasyNode(scope, node){        
        var cfg = scope.config;
        var obj = {};
        obj.text = node[cfg.labelField];
        if(node[cfg.idField])
            obj.id = node[cfg.idField];
        if(node.type)
            obj.type = node.type;
        if(node.path)
            obj.path = node.path;
        if(node.isLazy)
            obj.isLazy = node.isLazy;
        if(node[cfg.extraInfo])
            obj.extraInfo = node[cfg.extraInfo];
        obj.iconUrl = cfg.nodeImageMap[node[cfg.nodeImageField]];
        if(node[scope.config.childField]){
            obj.children = [];
            convertToEasyData(scope, node[scope.config.childField], obj.children);
        }
        //obj.isExpanded = true;

        return obj;
    }

    function convertFromAnEasyNode(scope, node){
        var cfg = scope.config;
        var obj = {};
        obj[cfg.labelField] = node.text;
        obj[cfg.idField] = node.id;
        if(node.type)
            obj.type = node.type;
        if(node.path)
            obj.path = node.path;
        if(node.extraInfo)
            obj[cfg.extraInfo] = node.extraInfo;
        if(node.children){
            obj[scope.config.childField] = [];
            convertFromEasyData(scope, node.children, obj[scope.config.childField]);
        }

        return obj;
    }

    function convertToEasyData(scope, data, easyData){
        angular.forEach(data, function(obj, idx){
            var node = convertToAnEasyNode(scope, obj);
            easyData.push(node);
            
        });        
    }

    function convertFromEasyData(scope, easyData, data){
        angular.forEach(easyData, function(obj, idx){
            var node = convertFromAnEasyNode(scope, obj);
            data.push(node);
        });        
    }

    Tree.prototype = {
        draw : function( data ){
            var easyData = [];
            convertToEasyData(this, data, easyData);
            this.treeConfig.data = easyData;
            this.easyTree = $(this.container).easytree( this.treeConfig );
        },
        getAllNodes : function(){
            var data = [];
            var easyData = this.easyTree.getAllNodes();
            convertFromEasyData(this, easyData, data)
            return data;
        },
        getNodeById : function(nodeId){
            var node = this.easyTree.getNode(nodeId);
            return convertFromAnEasyNode(this, node);
        },
        addNodes : function(source, targetId){              //add array of nodes to a specific parent
            var _this = this;
            angular.forEach(source, function(obj){
                var node = convertToAnEasyNode(_this, obj);
                _this.easyTree.addNode(node, targetId);
            });          
        },
        removeNodeById : function(nodeId){
            this.easyTree.removeNode(nodeId);
        },
        activateNodeById : function(nodeId){
            this.easyTree.activateNode(nodeId);
        },
        rebuildTree : function(nodes){
          this.easyTree.rebuildTree(nodes);
        }
    }

    wrapObj.easyTree = Tree;
    return wrapObj;

})(TREE || {});