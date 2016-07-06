function createGrid(cfgObj){
	cfgObj.show = {
	        toolbar: true,
	        footer: true
	    };
	cfgObj.searches = [];
	$.each( cfgObj.columns, function(idx,val){
		var obj = { field : val.field , caption : val.caption , type : 'text'};
		cfgObj.searches.push(obj);
	});
	return $('#lvGrid').w2grid( cfgObj);			
}

function showGrid ( cfgObj ) {
	var uuid = guid();
	cfgObj.name = uuid;
	var w2obj = gridMap[cfgObj.name];
	if( w2obj == undefined ){
		w2obj = createGrid(cfgObj);
		//gridMap[cfgObj.name] = w2obj;
	}else
		w2obj.add(cfgObj.records);
}

var guid = (function() {
	  function s4() {
	    return Math.floor((1 + Math.random()) * 0x10000)
	               .toString(16)
	               .substring(1);
	  }
	  return function() {
	    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
	           s4() + '-' + s4() + s4() + s4();
	  };
	})();

var gridMap = {};