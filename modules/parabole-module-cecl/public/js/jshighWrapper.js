var GRAPH = (function( wrapObj ){
 
    function Graph( container , cfg){
           this.container = container;
           this.config = cfg ;
           this.plot = null;
           this.graphConfig = initDefaults(this.config);
           this.svgData = '';
    };

    var renderers = { 'PIE' : 'PieRenderer' , 'bar' : 'BarRenderer'}
    var rendererOptions ={ barMargin: 30 , highlightMouseDown: true , fillToZero: true , showDataLabels: true}
    var pointLabels = { show: true }

    function initDefaults(config)
    {
      var obj = {};
      if( config.IsStacked )
        obj.chart = createChartConfig( config );

      createTitles( config , obj);
      obj.legend = {
          layout: 'horizontal',
          align: 'bottom',
          verticalAlign: 'bottom',
          borderWidth: 0
      };
      obj.chart = {events:{}};
      if( config.height ){
        obj.chart.height = config.height;
      }

      if(config.navigation){
        navigation: {
            buttonOptions: {
                enabled: config.navigation
            }
        }
      }

      obj.plotOptions = {series: {cursor: 'pointer', point: {events: {}}}};
      if(config.handlerData){
          if(config.handlerData.click){
              obj.chart.events.click = config.handlerData.scope[config.handlerData.click];
          }
          if(config.handlerData.columnClick){
            obj.plotOptions.series.point.events.click = config.handlerData.scope[config.handlerData.columnClick];
          }
      }
      return obj;
    }

    function createChartConfig(cfgObj){
      var chartObj = {};
      chartObj.type = cfgObj.GraphType || 'column';
      return chartObj;
    }

    function createTitles(cfgObj , obj){
        obj.title = { text : cfgObj.Title};
      obj.subtitle = { text : cfgObj.Subtitle || '' };
      return;
    }

    Graph.prototype = {
      draw : function( data ){
        var cfgObj = this.graphConfig;
        var drawData = data;
        var range = 0;
        var interval = null;
        
        data = drawData.data;

        if( data.series[0] && data.series[0].data )
          range = data.series[0].data.length;

        if( range <= 100 )
          interval = 1;
        else if( range > 100 && range <= 500 )
          interval = 10;
        else if( range > 500 && range <= 1000 )
          interval = 25;
        else if( range > 1000 && range <= 2000 )
          interval = 50;
        else if( range > 2000 && range <= 5000 )
          interval = 200;
        else
          interval = 500;

        cfgObj.xAxis={categories : data.categories};

        cfgObj.xAxis={
          categories : data.categories,
          tickInterval: interval,
          title: {
            text: data.xAxis ? data.xAxis.title : ''
          }
        };
        
        cfgObj.yAxis = {           
              title: 
              {
                  text: this.config.YAxisTitle
              }
         };

        cfgObj.colors = [
            '#ff0000',
            '#FFC200',
            '#22b40b',
            '#16630a',
            '#0000ff'
        ];

        if( drawData.GraphType == 'stack' ){
            cfgObj.plotOptions.column = {
                  stacking: 'percent'
              };
            cfgObj.chart.type = 'column';
            /*cfgObj.chart.options3d = {
                enabled: true,
                alpha: 15,
                beta: 15,
                viewDistance: 25,
                depth: 40
            };*/
            cfgObj.chart.marginTop = 40;
            cfgObj.chart.marginRight = 40;
        }

        cfgObj.series = data.series;
        if( drawData.GraphType != 'stack' )
          for( var i in cfgObj.series )
            cfgObj.series[i].type = drawData.GraphType;

        var chart = $(this.container).highcharts( cfgObj );
        this.svgData = $(this.container).highcharts().getSVG();          
      }
    }
    wrapObj.PlotWrapper = Graph;
    return wrapObj;
})(GRAPH || {});