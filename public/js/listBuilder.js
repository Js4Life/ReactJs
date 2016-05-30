function ListBuilder(name, items, container, paper, w, h, topLeft, extraInfo, handler){
	this.name = name;
	this.attributes = items || [];
	this.container = container;
	this.width = w;
	this.height = h;
	this.topLeft = topLeft || new Coordinate(0, 0);
	this.extraInfo = extraInfo;
	this.handler = handler;
	this.paper = paper;
	this.arrows = [];
	this.targets = [];
	this.sources = [];
}

ListBuilder.prototype = {
	initializeArrow : function( pnt ){
		var arrow  = new RaphaelArrow( pnt.start , pnt.end , this.paper, this.handler );
		return arrow;
	},
	addArrow : function( arrow ){
		this.arrows.push( arrow );
	},
	/*updateArrow : function( pntObj )
	{
		if( this.arrow != null )
			this.arrow.updatePosition( pntObj );
	},*/
	deleteArrow : function()
	{
		if( this.arrows != null ){
			angular.forEach(this.arrows, function(arrow){
				arrow.deleteArrow( );
			});
		}
		this.arrows = [];
	},
	setTopLeft : function(  tl )
	{
		this.topLeft = tl;		
	},
	addTarget : function(  t )
	{
		if( this.targets.indexOf( t ) == - 1)
			this.targets.push( t );		
	},
	removeTarget : function( t )
	{
		var idx = this.targets.indexOf( t );
		if( idx != - 1)
			this.targets = this.targets.splice( idx , 1 );			
	},
	getTargets : function()
	{
		return this.targets;		
	},
	setTargets : function( t )
	{
		this.targets = t;		
	},

	addSource : function( o )
	{
		if( this.sources.indexOf( o ) == - 1)
			this.sources.push( o );	
	},
	removeSource : function( o )
	{
		var idx = this.sources.indexOf( o );
		if( idx != - 1)
			this.sources = this.sources.splice( idx , 1 );			
	},
	getSources : function()
	{
		return this.sources;		
	},
	setSources : function( o )
	{
		this.sources = o;		
	},
	getArrows : function()
	{
		return this.arrows;		
	},
	inPanelArea : function( upX , upY )
	{
		var clsX = this.topLeft.getX();
		var clsY = this.topLeft.getY();
		var wd = this.width;
		var ht = this.height;
		
		if( upX >= clsX && upX <= ( clsX + wd ) )
			if( upY >= clsY && upY <=  (clsY + ht) )
				return true;
		return false;		
	},
	getWidth : function()
	{
		return this.width;
	},
	getHeight : function()
	{
		return this.height;
	}
}