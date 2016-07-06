function Coordinate( x,y )
{
	this.X = x;
	this.Y = y;
}

Coordinate.prototype = 
{
	setX : function( val )
	{
		this.X = val;
	},
	setY : function( val )
	{
		this.Y = val;
	},
	getX : function()
	{
		return this.X;
	},
	getY : function()
	{
		return this.Y;
	}
}