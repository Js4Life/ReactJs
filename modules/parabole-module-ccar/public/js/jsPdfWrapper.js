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
		NORMAL : 'normal',
		A4 : [841.89, 595.28]
	};

	function defaultConfig(cfg){
		cfg = cfg || {};
		return {
			orientation : cfg.orientation || 'l',
			unit : cfg.unit || 'px',
			format : cfg.format || constants.A4, //for a4
			name : cfg.name ? cfg.name + '.pdf' : 'report.pdf',
			hasCover : cfg.hasCover || false,
			title : cfg.title || "",
			subtitle : cfg.subtitle || "",
			header : cfg.header || "",
			footer : cfg.footer || ""
		};
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

	function resetImgAspectRatio(imgData, pageWidth, pageHeight) {	
		var img = new Image();
		img.src = imgData;
		var canvas = document.createElement('canvas'),
        ctx = canvas.getContext('2d');
        var srcWidth = img.width;
		var srcHeight = img.height;
		var ratio = pageWidth / srcWidth;
		canvas.width = srcWidth*ratio;
	    canvas.height = srcHeight*ratio;
	    ctx.drawImage(img, 0, 0, canvas.width, canvas.height);	    
		return canvas;
	}

	function splitImageByPageSize(img, cfg){
		var docWidth = img.width;
		var docHeight = img.height;
		var heightOfOnePiece = cfg.format[1];
		imgList = [];

		for(var x = 0; x <= docHeight; x = x + heightOfOnePiece) {	        
            var canvas = document.createElement('canvas');
            canvas.width = docWidth;
            canvas.height = heightOfOnePiece;
            var context = canvas.getContext('2d');
            context.drawImage(img, 0, x, docWidth, heightOfOnePiece, 0, 0, canvas.width, canvas.height);
            var currentPiece = canvas.toDataURL();
			imgList.push(currentPiece);
	    }	
		return imgList;
	}

	Doc.prototype = {
		createDoc : function(element, width, height){
			var cfg = this.config;
			var doc = null;
			this.capture(element, width, height).then(function(canvas){
				doc = new jsPDF(cfg.orientation, cfg.unit, cfg.format);
				doc.addImage(canvas, constants.JPEG, 0, 0, cfg.format[0], cfg.format[1]);
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
			var pageCounter = 1;
			if(cfg.hasCover){
				this.createCoverPage(doc);
			}
			angular.forEach(pages, function(page, idx){
				if(idx != 0)
					doc.addPage();				
				var img = resetImgAspectRatio(page.data, contentX, contentY);
				imgPieces = splitImageByPageSize(img, cfg);
				if(imgPieces.length > 0){
					angular.forEach(imgPieces, function(aPiece, pIdx){
						if(pIdx != 0)
							doc.addPage();
						if(page.comments && imgPieces.length == pIdx+1){
							addComments(doc, cfg, page.comments);
							doc.addImage(aPiece, constants.JPEG, left, top, contentX, contentY-top);
						} else{
							doc.addImage(aPiece, constants.JPEG, left, top, contentX, contentY);
						}
						doc.page = pageCounter++;
						if(page.heading && pIdx == 0){
							addPageHeading(doc, cfg, page.heading);
						}
						addHeader(doc, cfg);
						addFooter(doc, cfg);
					});
				}
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