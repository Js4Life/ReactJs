function RaphaelArrow( startPnt ,endPnt, paper, handler)
{
	this.start = startPnt;
	this.end = endPnt;
	this.paper = paper;
	this.handler = handler;
	this.arrowElements = [];
	this.source = null;
	this.target = null;
}

/* NPhase */
RaphaelArrow.prototype = 
{
	drawArrow : function()
	{
		var size = 7;
		var x1 = this.start.getX() , y1 = this.start.getY();
		var x2 = this.end.getX() , y2 = this.end.getY();
		var angle = Math.atan2(x1-x2,y2-y1);
		angle = (angle / (2 * Math.PI)) * 360;		
		var arrowPath = this.paper.path("M" + x2 + " " + y2 + " L" + (x2  - size) + " " + (y2  - size) + " L" + (x2  - size)  + " " + (y2  + size) + " L" + x2 + " " + y2 )
							  .attr("fill","black")
							  .rotate((90+angle),x2,y2);
		var linePath = this.paper.path("M" + x1 + " " + y1 + " L" + x2 + " " + y2).attr("stroke-width", "3");

		linePath.mouseover(function(){
            this.attr({'cursor':'pointer'});
        }).click(this.handler.onArrowClick);	
		return [linePath,arrowPath];
	},	
	updatePosition : function( pntDef)
	{
		if( pntDef.start!= undefined )
			this.start = pntDef.start;
		if( pntDef.end != undefined )
			this.end = pntDef.end;
		 this.deleteArrow();	
		this.arrowElements = this.drawArrow();
	},	
	deleteArrow : function()
	{
		$.each( this.arrowElements , function( i , o )
		{
			o.remove();
		});
		this.arrowElements = [];
	},
	getSource : function( listObj ){
		return this.source;
	},
	setSource : function( listObj ){
		this.source = listObj;
	},
	getTarget : function( listObj ){
		return this.target;
	},
	setTarget : function( listObj ){
		this.target = listObj;
	}
}