var TREE = (function(wrapObj){
    function Tree( container , cfg ){
        this.container = container;
        this.config = cfg;
        this.treeConfig = initDefaults(this.config);
        this.callBackScope = this.config.handlerData.scope || window;
        this.jsTree = {};
    }

    function initDefaults(config)
    {
        var obj = {};
        var core = {};
        core.animation = config.animation || 0;
        core.check_callback = config.check_callback || false;
        core.themes = {"stripes": config.stripes || false};
        core.data = [];
        obj.checkbox = {"keep_selected_style" : false};
        obj.plugins = config.plugins || ["contextmenu", "checkbox", "dnd", "search", "state", "types", "wholerow"];
        obj.core = core;
        return obj;
    }

    function enableCheckAtLeafOnly( data ){
        angular.forEach(data, function(obj){
            if(obj.children){
                obj.a_attr = { class: "no_checkbox" };
                enableCheckAtLeafOnly( obj.children );
            }
            else{
                return;
            }
        });
    }

    function enableCheckAtTableOnly( data ){
        angular.forEach(data, function(obj){
            if(obj.type != "table"){
                obj.a_attr = { class: "no_checkbox" };
                enableCheckAtTableOnly( obj.children );
            }
            else if( obj.children ){
                enableCheckAtTableOnly( obj.children );
            }
            else{
                return
            }
        });
    }

    Tree.prototype = {
        draw : function( data ){
            if(this.config.checkAtLeafOnly){
                enableCheckAtTableOnly( data );
            }
            this.treeConfig.core.data = data;
            this.jsTree = $(this.container).jstree( this.treeConfig );
            this.jsTree.bind("open_node.jstree", this.callBackScope[this.config.handlerData.onBaseNodeClick]);
            this.jsTree.bind("select_node.jstree", this.callBackScope[this.config.handlerData.onSelectNode]);
            this.jsTree.bind("deselect_node.jstree", this.callBackScope[this.config.handlerData.onDeselectNode]);
        },
        addChildren : function( children, parentId ){
            var _this = this;
            enableCheckAtTableOnly( children )
            angular.forEach(children, function(node){
                _this.container.jstree('create_node', parentId, node, 'last');
            });
        },
        removeChildren : function( node ){
            this.container.jstree('delete_node', node.children);
            this.container.jstree('deselect_node', node);
        },
        getNodeById : function(nodeId){
            return this.container.jstree('get_node', nodeId);
        },
        refresh : function(){
            this.container.jstree('refresh');
        },
        destroy : function(){
            this.container.jstree('destroy');
        },
        selectNodeById : function(nodeId){
            this.container.jstree("select_node", nodeId); 
        },
        deSelectNodeById : function(nodeId){
            this.container.jstree("deselect_node", nodeId); 
        },
        loadData : function( data ){
            this.destroy();
            this.draw( data ); 
        }

    }

    wrapObj.JsTree = Tree;
    return wrapObj;

})(TREE || {});