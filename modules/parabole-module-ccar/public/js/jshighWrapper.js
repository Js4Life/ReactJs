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
      //if( config.IsStacked )
        obj.chart = createChartConfig( config );

      createTitles( config , obj);
      /*obj.legend = {
          layout: 'horizontal',
          align: 'bottom',
          verticalAlign: 'bottom',
          borderWidth: 0
      };*/
      if(obj.chart){
        obj.chart.events = {};
      } else{
        obj.chart = {events: {}};
      }
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
        
        if(drawData.hasOwnProperty("data")){
          data = drawData.data;
        } else {
          data = drawData;
        }        

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

        cfgObj.xAxis={
          categories : data.categories,
          tickInterval: interval,
          title: {
            text: data.xAxis ? data.xAxis.title : ''
          }
        };

        /*if(data.xAxis && (data.xAxis.labels == '' || data.xAxis.labels == null)){
          cfgObj.xAxis.labels = {
              formatter: function() {
                  return '';
              }
          }
        };*/
        
        cfgObj.yAxis = {           
              title: 
              {
                  text: data.yAxis ? data.yAxis.title : this.config.YAxisTitle
              },
              gridLineWidth: 1
         };

        cfgObj.colors = cfgObj.colors || ['#f45b5b','#f7a35c','#50B432','#0000ff','#7cb5ec','#434348','#8085e9','#DDDF00','#f15c80','#e4d354','#2b908f','#16630a','#91e8e1'];

        if( drawData.GraphType == 'stack' ){
            cfgObj.plotOptions.column = {
                  stacking: 'percent'
              };
            cfgObj.chart.type = 'column';
            cfgObj.chart.marginTop = 40;
            cfgObj.chart.marginRight = 40;
        }
        if( cfgObj.chart.type === 'pie' || cfgObj.chart.type === 'donut' ){
            cfgObj.plotOptions.pie = {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                showInLegend: true,
                innerSize: cfgObj.chart.type === 'donut' ? 100 : 0
            };
            cfgObj.chart.type = 'pie';
        }

        cfgObj.series = data.series;
        /*if( drawData.GraphType != 'stack')
          for( var i in cfgObj.series )
            cfgObj.series[i].type = drawData.GraphType;*/

        var chart = $(this.container).highcharts( cfgObj );
        this.svgData = $(this.container).highcharts().getSVG();          
      }
    }
    wrapObj.PlotWrapper = Graph;
    return wrapObj;
})(GRAPH || {});