var RDAMAPPER = (function( wrapObj ){
	function RdaMapper(containerId, handler){
		this.container = $('#' + containerId);
		this.handler = handler;
		this.paper = new Raphael( containerId, this.container.width(), this.container.height() );
		this.setupEventHandles();
		this.doDraw = false;
		this.panels = [];
		this.fromPanel = null;	
		this.toPanel = null;	
		this.currentArrow = null;
	}

	RdaMapper.prototype = {
		drawPanel : function(name, items, w, h, topLeft, extraInfo){
			var width = w || 120;
			var height = h || 120;
			var panel = new ListBuilder(name, items, this.container, this.paper, width, height, topLeft, extraInfo, this.handler);
			this.panels.push(panel);
			return panel;
		},
		getPanels : function(){
			return this.panels;
		},
		getRelativeToMapper : function( x , y )
		{
		   var relX = x + this.container.offset().left;	
		   var relY = y + this.container.offset().top;	
		   return new Coordinate( relX , relY);
		},
		getRelativeToPage : function( x , y )
		{
		   var relX = x - this.container.offset().left;	
		   var relY = y - this.container.offset().top;	
		   return new Coordinate( relX , relY);
		},
		setupEventHandles : function(){
			var _this = this;
			this.container.on('mousedown', doDown);
			this.container.on('mouseup',doUp);
			this.container.on('mousemove',doMove );

			function doDown(e, ui){				
				var relPnt = _this.getRelativeToPage(e.pageX ,  e.pageY);
				_this.fromPanel = _this.chkPntInPanel( relPnt.getX() ,  relPnt.getY() );
				if( _this.doDraw && _this.fromPanel ){
					var pane = _this.fromPanel;
					var startPnt = new Coordinate(pane.topLeft.getX() + pane.getWidth()/2, pane.topLeft.getY() + pane.getHeight()/2);
					_this.container.css('cursor', 'nw-resize');
					_this.currentArrow = new RaphaelArrow( startPnt , startPnt , _this.paper, _this.handler );
					_this.fromPanel.addArrow( _this.currentArrow );
				}				
			}

			function doMove(e){
				var relPnt = _this.getRelativeToPage(e.pageX ,  e.pageY);				
				var endPnt = null;
				var startPnt = null;
				if(!_this.doDraw){
					if(_this.fromPanel){
						angular.forEach(_this.fromPanel.getTargets(), function(obj, idx){
							_this.currentArrow = _this.fromPanel.arrows[idx];
							_this.configurePnt(_this.fromPanel, obj);
						});						
						angular.forEach(_this.fromPanel.getSources(), function(obj, idx){
							_this.currentArrow = _.find(obj.arrows, function(a){return a.target.name == _this.fromPanel.name});
							_this.configurePnt(obj, _this.fromPanel);
						});					 
					}
				}
				else{
					_this.toPanel = _this.chkPntInPanel( relPnt.getX() ,  relPnt.getY() ) || null;
					if(_this.toPanel){
						var pane = _this.toPanel;
						endPnt = new Coordinate(pane.topLeft.getX() + pane.getWidth()/2, pane.topLeft.getY() + pane.getHeight()/2);
					}
					else{
						endPnt = new Coordinate(e.pageX - (e.pageX - e.offsetX), e.pageY - (e.pageY - e.offsetY));
					}
					if(_this.fromPanel){
						_this.currentArrow.updatePosition({end: endPnt});
					}
				}
			}

			function doUp(e){				
				var relPnt = _this.getRelativeToPage(e.pageX ,  e.pageY);
				_this.toPanel = _this.chkPntInPanel( relPnt.getX() ,  relPnt.getY() ) || null;
				if(_this.doDraw){

					if(_this.toPanel && !_.isEqual(_this.fromPanel, _this.toPanel)){
						_this.doDraw = false;
						_this.configurePnt(_this.fromPanel, _this.toPanel);
						_this.fromPanel.addTarget( _this.toPanel );
						_this.toPanel.addSource( _this.fromPanel );
						_this.currentArrow.setSource( _this.fromPanel );
						_this.currentArrow.setTarget( _this.toPanel );
						_this.handler.OnArrowDraw(_this.fromPanel, _this.toPanel);
					}
					else if(_this.fromPanel){
						_this.fromPanel.deleteArrow();
					}
				}
				
				_this.container.css('cursor', 'default');
			}
		},
		chkPntInPanel : function( x , y )	
		{
			var o = null;
			$.each( this.panels , function( i , obj )
			{
				if( obj.inPanelArea( x , y ) )
				{
					o = obj;
				}
			});	
			return o;
		},
		configurePnt : function( fromObj, toObj ){
			// var fromTopLeft = this.getRelativeToPage(fromObj.topLeft.getX(), fromObj.topLeft.getY());
			// var toTopLeft = this.getRelativeToPage(toObj.topLeft.getX(), toObj.topLeft.getY());

			var fromTopLeft = fromObj.topLeft;
			var toTopLeft = toObj.topLeft;

			var dl = fromTopLeft.getX();
			var dt = fromTopLeft.getY();
			var bl = toTopLeft.getX();
			var bt = toTopLeft.getY();
			var w = fromObj.getWidth();
			var h = fromObj.getHeight();
			//var h = 38;
			var x1, x2, y1, y2;
			
			if( (dl+w) < bl )				//left
			{
				x1 = bl;
				y1 = bt+(h/2);
				
				if( (dt+h) < bt )
				{
					x2 = dl+(w/2);
					y2 = dt+h;
				}
				else if( dt > (bt+h) )
				{
					x2 = dl+(w/2);
					y2 = dt;
				}
				else
				{
					x2 = dl+w;
					y2 = dt+(h/2);
				}
			}
			else if( ((dt+h) < bt) && (dl < (bl+w)) )		//up
			{
				x1 = bl+(w/2);
				y1 = bt;
				
				x2 = dl+(w/2);
				y2 = dt+h; 
			}
			else if( (dt > (bt+h)) && (dl < (bl+w)) )		//down
			{
				x1 = bl+(w/2);
				y1 = bt+h;
				
				x2 = dl+(w/2);
				y2 = dt;
			}
			else				//right
			{
				x1 = bl+w;
				y1 = bt+(h/2);
				
				if( (dt+h) < bt )
				{
					x2 = dl+(w/2);
					y2 = dt+h;
				}
				else if( dt > (bt+h) )
				{
					x2 = dl+(w/2);
					y2 = dt;
				}
				else
				{
					x2 = dl;
					y2 = dt+(h/2);
				}
			} 
			var stPnt = new Coordinate(x2,y2);
			var enPnt = new Coordinate(x1,y1);		
			this.currentArrow.updatePosition( { start : stPnt , end : enPnt } );
		},
		doReference : function(){
			this.doDraw = true;
			this.fromPanel = null;	
			this.toPanel = null;	
		},
		getPaneTopLeft : function(paneW, paneH){
			var containerW = this.container.width();
			var margin = 40;
			var tmpL = margin;
			var tmpT = margin;
			angular.forEach(this.panels, function(pane){
				tmpL = tmpL + pane.getWidth() + margin;
				if((tmpL + paneW) >= containerW){
					tmpT = tmpT + pane.getHeight() + margin;
					tmpL = margin;
				}
			});
			return new Coordinate(tmpL, tmpT);
		},
		deletePanelByIndex : function(index){
			this.panels = _.reject(this.panels, function(obj){ return obj.extraInfo.index == index; });
		}
	}
	wrapObj.Mapper = RdaMapper;
    return wrapObj;
})(RDAMAPPER || {})