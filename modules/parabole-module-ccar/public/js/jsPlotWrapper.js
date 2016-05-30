var GRAPH = (function( wrapObj ){
 
    function Graph( container , cfg){
           this.container = container;
           this.config = cfg ;
           this.plot = null;
           initDefaults(this.config);
    };

    var renderers = { 'PIE' : 'PieRenderer' , 'BAR' : 'BarRenderer'}
    var rendererOptions ={ barMargin: 30 , highlightMouseDown: true , fillToZero: true , showDataLabels: true}
    var pointLabels = { show: true }

    function initDefaults(config)
    {
      if( config.GraphType == undefined )
        config.GraphType = 'BAR';
    }
    function getSeriesDefaultObj( cfgObj ){
      var seriesDef = { };
      var grType = cfgObj.GraphType || 'BAR';
      seriesDef.renderer = jQuery.jqplot[renderers[grType]];
      seriesDef.rendererOptions = rendererOptions;
      seriesDef.trendline = { show:false };
      if(cfgObj.showPoint)
        seriesDef.pointLabels = pointLabels;
      return seriesDef;
    }

    function getPieData ( data ){
      var dataArr = [];
      
      var outArr = [];
      for(var i in data.XAxis)
      {
        var inArr = [];
        inArr.push( data.XAxis[i] );
        inArr.push( data.YAxis[i] );
        outArr.push( inArr );
      }
      return [outArr];
    }

    Graph.prototype = {
            initialize : function(){

            },
            draw : function( data ){
              var cfgObj = {};
              cfgObj.axesDefaults = {
                  tickRenderer: $.jqplot.CanvasAxisTickRenderer ,
                  tickOptions: {
                    angle: 90,
                    fontSize: '10pt',
                    showGridLines: false
                  }
              };

              cfgObj.axes = {
                  // Use a category axis on the x axis and use our custom ticks.
                  xaxis: {
                      renderer: $.jqplot.CategoryAxisRenderer,
                      ticks: data.XAxis
                  },
                  // Pad the y axis just a little so bars can get close to, but
                  // not touch, the grid boundaries.  1.2 is the default padding.
                  yaxis: {
                      pad: 1.2,
                      tickOptions: {formatString: '%d' , angle: 0}

                  }
              };
              if( this.config.GraphType == 'PIE')
                cfgObj.legend = { location:'s', show : true , placement: 'outside',  rendererOptions: {numberRows: 1}};
              cfgObj.grid = {drawGridLines: false};
              cfgObj.seriesDefaults = getSeriesDefaultObj( this.config );
              var gData = data.YAxis;
              if( this.config.GraphType == 'PIE')
                gData = getPieData(data);
              this.plot = $.jqplot(this.container, gData , cfgObj);
            }
        }

        wrapObj.JQPlot = Graph;
    return wrapObj;
  })(GRAPH || {});