/*
Author: Atanu Mallick
Dependencies: html2canvas, jsPDF
*/

var DOC = (function(wrapObj){
	var Doc = function(container, cfg){
		this.container = container;
		this.config = defaultConfig(cfg);
	};

	var constants = {
		MARGIN : 30,
		JPEG : 'JPEG',
		PAGE : 'Pg. ',
		ITALIC : 'italic',
		NORMAL : 'normal'
	};

	function defaultConfig(cfg){
		cfg = cfg || {};
		return {
			orientation : cfg.orientation || 'l',
			unit : cfg.unit || 'px',
			format : cfg.format || [841.89, 595.28], //for a4
			name : cfg.name ? cfg.name + '.pdf' : 'report.pdf',
			hasCover : cfg.hasCover || false,
			title : cfg.title || "",
			subtitle : cfg.subtitle || "",
			header : cfg.header || "",
			footer : cfg.footer || ""
		};
	}

	function addPage(doc, page){
		doc.addPage();
		doc.addImage(page.data, 'JPEG', 10, 10, cfg.format[0], cfg.format[1]);
		doc.page = idx+1;
	}

	function addHeader(doc, cfg){
		var left = constants.MARGIN;
		var top = 2*constants.MARGIN;
		var contentX = cfg.format[0]-constants.MARGIN;
		doc.setDrawColor(150);
		doc.setFontSize(12);
		doc.setFontType(constants.ITALIC);
		doc.line(left, top, contentX, top);
		if(cfg.header && cfg.header != ""){
			doc.text(left+15, top-10, cfg.header);
		}
	}

	function addFooter(doc, cfg){
		var left = constants.MARGIN;
		var contentX = cfg.format[0]-constants.MARGIN;
		var contentY = cfg.format[1]-2*constants.MARGIN;
		doc.setDrawColor(150);
		doc.line(left, contentY, contentX, contentY);
		doc.setFontSize(12);
		doc.setFontType(constants.ITALIC);
		doc.text(contentX-30, contentY+15, constants.PAGE + doc.page);
		if(cfg.footer && cfg.footer != ""){
			doc.text(left+15, contentY+15, cfg.footer);
		}
	}

	function addPageHeading(doc, cfg, text){
		var top = 2*constants.MARGIN;
		doc.setTextColor(50);
		doc.setFontSize(16);
		doc.setFontType(constants.NORMAL);
		var textWidth = doc.getStringUnitWidth(text) * doc.internal.getFontSize() / doc.internal.scaleFactor;
		var textOffset = (doc.internal.pageSize.width - textWidth) / 2;
		doc.text(textOffset, top-10, text);
	}

	function addComments(doc, cfg, text){
		var left = constants.MARGIN;
		var top = constants.MARGIN
		var contentX = cfg.format[0]-2*constants.MARGIN;
		var contentY = cfg.format[1]-3*constants.MARGIN;
		var splitTitle = doc.splitTextToSize(text, contentX);
		doc.setFontSize(12);
		doc.text(left, contentY, splitTitle);
	}

	function calculateAspectRatio(srcWidth, srcHeight, maxWidth, maxHeight) {
	    var ratio = Math.min(maxWidth / srcWidth, maxHeight / srcHeight);
	    return { width: srcWidth*ratio, height: srcHeight*ratio };
	}

	Doc.prototype = {
		createDoc : function(element, width, height){
			var cfg = this.config;
			var doc = null;
			this.capture(element, width, height).then(function(canvas){
				doc = new jsPDF(cfg.orientation, cfg.unit, cfg.format);
				doc.addImage(canvas, 'JPEG', 0, 0, cfg.format[0], cfg.format[1]);
	        	doc.save(cfg.name);
			});
		},
		createMuliPageDoc : function(pages){ 			//pages is array of {id: "id", data: "imagedata"}
			var cfg = this.config;
			var left = constants.MARGIN;
			var top = 2*constants.MARGIN;
			var contentX = cfg.format[0]-2*constants.MARGIN;
			var contentY = cfg.format[1]-4*constants.MARGIN;
			var doc = new jsPDF(cfg.orientation, cfg.unit, cfg.format);
			if(cfg.hasCover){
				this.createCoverPage(doc);
			}
			angular.forEach(pages, function(page, idx){
				if(idx != 0)
					doc.addPage();
				/*var imgDim = calculateAspectRatio(page.data.width, page.data.height, contentX, contentY);
				doc.addImage(page.data, constants.JPEG, left, top, imgDim.width, imgDim.height);*/
				if(page.comments){
					addComments(doc, cfg, page.comments);
					doc.addImage(page.data, constants.JPEG, left, top, contentX, contentY-top);
				} else{
					doc.addImage(page.data, constants.JPEG, left, top, contentX, contentY);
				}
				doc.page = idx+1;
				if(page.heading){
					addPageHeading(doc, cfg, page.heading);
				}
				addHeader(doc, cfg);
				addFooter(doc, cfg);
			});
			doc.save(cfg.name);
		},
		capture : function(element, width, height){
			element = element || this.container;
			width = width || null;
			height = height || null;
			return html2canvas(element,{
		    	imageTimeout: 1000,
		    	width: width,
		    	height: height
		    });
		},
		splitImageByPageSize : function(element, width, height){
			element = element || this.container;
			var imgWidth = element.width;
			var imgHeight = element.height;
			var imgList = [];

			if(imgHeight > height){
				for(var i = height ; i < imgHeight ; i = i + height){
					var canvas = $('<canvas/>').attr({'width': width, 'height': height});
					var ctx = canvas.getContext("2d");
					ctx.drawImage(element, 0, i, width, height, 0, 0, width, height);
					var img = canvas.toDataURL("image/jpg");
					imgList.push(img);
				}				
			} else{
				imgList.push(element);
			}
			return imgList;
		},
		addPages : function(pages){
			var _this = this;
			angular.forEach(pages, function(page, idx){
				var procPages = _this.splitImageByPageSize(page);
				if(procPages.length > 1)
					_this.addPages(procPages);

				doc.addImage(page.data, 'JPEG', 10, 10, cfg.format[0], cfg.format[1]);
				doc.addPage();
			});
		},
		createCoverPage : function(doc){
			var cfg = this.config;
			var left = constants.MARGIN;
			var top = constants.MARGIN;
			var contentX = cfg.format[0]-2*constants.MARGIN;
			var contentY = cfg.format[1]-2*constants.MARGIN;
			var splitTitle = doc.splitTextToSize(cfg.title, contentX - 2*left);
			doc.setFontSize(40);
			doc.text(6*left, 6*top, splitTitle);

			doc.setFontSize(16);			
			doc.setTextColor(100);			
			doc.text(6*left, 7*top, cfg.subtitle);

			doc.setDrawColor(255,0,0);
			doc.setFillColor(255,0,0);
			doc.rect(6*left-8, 6*top-21, 3, 25, 'FD'); 

			doc.setDrawColor(0,0,255);
			doc.rect(left, top, contentX, contentY);
			doc.rect(left+2, top+2, contentX-4, contentY-4);			
			doc.addPage();
		}
	};

	wrapObj.Document = Doc;
	return wrapObj;
})(DOC || {});