var MAP = (function(wrapObj){
	function Map (container, cfg) {
		this.container = container;
		this.config = initDefault(cfg);
	}

	function initDefault (config) {
		config = config || {};		
		var events = {};
		if(config.handlerData){
		    if(config.handlerData.click){
		        events.click = function(){
		        	config.handlerData.scope[config.handlerData.click](this);
		        };		        
		    }
		}
		return {
		 	title : config.title ? {text: config.title} : "",
		 	mapNavigation: {
                enabled: true,
                enableDoubleClickZoomTo: config.doubleClickZoom || false,
                buttonOptions: {
                    verticalAlign: 'bottom'
                }
            },
            legend: {
                enabled: false
            },
            colorAxis: {
                min: 1,
                max: 1000,
                type: 'logarithmic'
            },
            tooltip: {
              formatter: function(){
                return  this.point.name;
              }
            },            
            plotOptions: {
              series: {
                point: {
                  events: events
                }
              }
            },
            series : [{
                data : null,
                mapData: Highcharts.maps['custom/world'],
                joinBy: ['iso-a2', 'code'],
                cursor: 'pointer',
                states: {
                    hover: {
                        color: '#BADA55'
                    }
                },
                dataLabels: {
                    enabled: true,
                    format: '{point.name}'
                }
            }]
		}  
	}

	Map.prototype = {
		drawMap : function (data) {
			var config = this.config;
			if(!config.series) return;
			var mapConfig = config.series[0];
			mapConfig.data = data;
			var map = $(this.container).highcharts( 'Map', config );
			return map;
		}
	}

	wrapObj.PlotMap = Map;
    return wrapObj;
})(MAP || {})