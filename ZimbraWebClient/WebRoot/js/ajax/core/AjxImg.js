/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */


/**
 * @class
 * This static class provides basic image support by using CSS and background 
 * images rather than &lt;img&gt; tags.
 *  
 * @author Conrad Damon
 * @author Ross Dargahi
 * 
 * @private
 */
AjxImg = function() {};

AjxImg.prototype = new Object;
AjxImg.prototype.constructor = null;

AjxImg._VIEWPORT_ID = "AjxImg_VP";

AjxImg.DISABLED = true;

AjxImg.RE_COLOR = /^(.*?),color=(.*)$/;

/**
 * This method will set the image for <i>parentEl</i>. <i>parentEl</i> should 
 * only contain this image and no other children
 *
 * @param parentEl 		the parent element for the image
 * @param imageName 		the name of the image.  The CSS class for the image will be "Img&lt;imageName&gt;".
 * @param useParenEl 	if <code>true</code> will use the parent element as the root for the image and will not create an intermediate DIV
 * @param _disabled		if <code>true</code>, will append " ZDisabledImage" to the CSS class for the image,
 * @param {array}       classes             array of class names to be applied to this image
 *							which will make the image partly transparent
 */
AjxImg.setImage =
function(parentEl, imageName, useParentEl, _disabled, classes) {
	
	if (!parentEl) { return; }
	
	classes = classes || [];
	var origImageName = imageName;
    var color, m = imageName && imageName.match(AjxImg.RE_COLOR);
	if (m) {
		imageName = m && m[1];
		color = m && m[2];
	}

	var className = AjxImg.getClassForImage(imageName, _disabled);
	if (useParentEl) {
		classes.push(className);
		parentEl.className = classes.join(" ");
		return;
	}
	var id = parentEl.firstChild && parentEl.firstChild.id;
        
	var overlayName = className+"Overlay";
	var maskName = className+"Mask";
	if (color && window.AjxImgData && AjxImgData[overlayName] && AjxImgData[maskName]) {
		color = (color.match(/^\d$/) ? ZmOrganizer.COLOR_VALUES[color] : color) ||
				ZmOrganizer.COLOR_VALUES[ZmOrganizer.ORG_DEFAULT_COLOR];
		parentEl.innerHTML = AjxImg.getImageHtml(origImageName, null, id ? "id='"+id+"'" : null, false, _disabled);
		return;
	}

	if (parentEl.firstChild == null || parentEl.firstChild.nodeName.toLowerCase() != "div") {
		var html = [], i = 0;
		html[i++] = "<div ";
		if (id) {
			html[i++] = " id='";
			html[i++] = id;
			html[i++] = "' ";
		}
		if (className) {
			classes.push(className);
		}
		html[i++] = AjxUtil.getClassAttr(classes);
		html[i++] = "></div>";
		parentEl.innerHTML = html.join("");
		return;
	} else if (AjxEnv.isIE) {
		parentEl.firstChild.innerHTML = "";
	}
	if (className) {
		classes.push(className);
	}
	parentEl.firstChild.className = classes.join(" ");
};

AjxImg.setDisabledImage = function(parentEl, imageName, useParentEl, classes) {
	return AjxImg.setImage(parentEl, imageName, useParentEl, true, classes);
};

AjxImg.getClassForImage =
function(imageName, disabled) {
	var className = imageName ? "Img" + imageName : "";
	if (disabled) className += " ZDisabledImage";
	return className;
};

AjxImg.getImageClass =
function(parentEl) {
	return parentEl.firstChild ? parentEl.firstChild.className : parentEl.className;
};

AjxImg.getImageElement =
function(parentEl) {
	return parentEl.firstChild ? parentEl.firstChild : parentEl;
};

AjxImg.getParentElement =
function(imageEl) {
	return imageEl.parentNode;
};

/**
 * Returns the HTML needed to display the given image.
 *
 * @param {string}		imageName		the image you want to render
 * @param {string}		styles			optional style info (for example, "display:inline")
 * @param {string}		attrStr			optional attributes (for example, "id=X748")
 * @param {boolean}		wrapInTable		if true, wrap the HTML in a TABLE
 * @param {boolean}		disabled		if true, show image as disabled
 * @param {array}       classes     array of class names to be applied to this image
 * 
 * @return	{string}	the image string
 */
AjxImg.getImageHtml = 
function(imageName, styles, attrStr, wrapInTable, disabled, classes) {

	styles = styles || "";
	var styleStr = styles ? " style='" + styles + "'" : "";
	attrStr = attrStr ? " " + attrStr : "";
	classes = classes || [];

	var pre = wrapInTable ? "<table style='display:inline' cellpadding=0 cellspacing=0 border=0><tr><td align=center valign=bottom>" : "";
    var html = "";
	var post = wrapInTable ? "</td></tr></table>" : "";

	if (imageName) {
        var color, m = imageName.match(AjxImg.RE_COLOR);
        if (m) {
            imageName = m && m[1];
            color = m && m[2];
        }

        var className = AjxImg.getClassForImage(imageName, disabled);
        var overlayName = className + "Overlay";
        var maskName = className + "Mask";
        if (color && window.AjxImgData && AjxImgData[overlayName] && AjxImgData[maskName]) {
            color = (color.match(/^\d$/) ? ZmOrganizer.COLOR_VALUES[color] : color) ||
                    ZmOrganizer.COLOR_VALUES[ZmOrganizer.ORG_DEFAULT_COLOR];

            var overlay = AjxImgData[overlayName], mask = AjxImgData[maskName];
            if (AjxEnv.isIE && !AjxEnv.isIE9up) {
                var clip = "";
                var size = [
                    "width:", overlay.w, ";",
                    "height:", overlay.h, ";"
                ].join("");
                var location = [
                    "top:", mask.t, ";",
                    "left:", mask.l, ";"
                ].join("");
                if (typeof document.documentMode != 'undefined') { //IE8 is the first one to define this. IE8 can lie when in compat mode, so we need to really know it's it.
                    clip = [
                        'clip:rect(',
                        (-1 * mask.t) - 1, 'px,',
                        overlay.w - 1, 'px,',
                        (mask.t * -1) + overlay.h - 1, 'px,',
                        overlay.l, 'px);'
                    ].join('');
                }
                var filter = 'filter:mask(color=' + color + ');';
                html = [
                    // NOTE: Keep in sync with output of ImageMerger.java.
                    "<div class='IEImage' style='*display:inline;zoom:1;position:relative;overflow:hidden;", size, styles, "' ", attrStr,">",
                        "<div class='IEImageMask' style='overflow:hidden;position:relative;", size, "'>",
                            "<img src='", mask.f, "?v=", window.cacheKillerVersion, "' border=0 style='position:absolute;", location, clip, filter, "'>",
                        "</div>",
                        "<div class='IEImageOverlay ", overlayName, "' style='", size, ";position:absolute;top:0;left:0;'></div>",
                    "</div>"
                ].join("");
            }
			else if (AjxEnv.isIE9up) {
					color = color.replace("#","");
					var className = AjxImg.getClassForImage(imageName + "_" + color, disabled);
	                classes.push("Img" + imageName + "_" + color);
					html = [
						"<div ", AjxUtil.getClassAttr(classes), styleStr, attrStr, "></div>"
					].join("");
			}
            else {
                if (!overlay[color]) {
                    var width = overlay.w, height = overlay.h;

                    var canvas = document.createElement("CANVAS");
                    canvas.width = width;
                    canvas.height = height;

                    var ctx = canvas.getContext("2d");

                    ctx.save();
                    ctx.clearRect(0,0,width,height);

                    ctx.save();
	                var imgId = attrStr;
	                if (!imgId) {
		                imgId = Dwt.getNextId("CANVAS_IMG_");  //create an imgId in case we need to update the img.src for an element without an id
		                attrStr = " id='" + imgId + "'";
	                }
	                else {
		                var match = attrStr.match(/id=[\"\']([^\"\']+)[\"\']+/);
		                if (match && match.length > 1) {
			                imgId = match[1]; //extract the ID value
		                }
		                AjxDebug.println(AjxDebug.TAG_ICON, "imgId = " + imgId);
	                }
	                var maskElement = document.getElementById(maskName);
	                var overlayElement = document.getElementById(overlayName);
	                if (!maskElement.complete || !overlayElement.complete) {
		                AjxDebug.println(AjxDebug.TAG_ICON, "mask status = " + maskElement.complete + " for " + imgId);
		                AjxDebug.println(AjxDebug.TAG_ICON, "overlay status = " + overlayElement.complete + " for " + imgId);
						var maskImg = new Image();
						maskImg.onload = function() {
							AjxDebug.println(AjxDebug.TAG_ICON, "mask image loaded");
							var overlayImg = new Image();
							overlayImg.onload = function() {
								AjxImg._drawCanvasImage(ctx, maskImg, overlayImg, mask, overlay, color, width, height)
								AjxDebug.println(AjxDebug.TAG_ICON, "overlay image loaded");
								var el = document.getElementById(imgId);
								if (el) {
									AjxDebug.println(AjxDebug.TAG_ICON, "element found for id = " + imgId);
									el.src = canvas.toDataURL();
									overlay[color] = canvas.toDataURL(); //only save if successful
								}
								else {
									AjxDebug.println(AjxDebug.TAG_ICON, "no element found for id = " + imgId);
								}
							}
							overlayImg.src = document.getElementById(overlayName).src;
	                    }
	                    maskImg.src = document.getElementById(maskName).src;
	                }
	                else {
		                //image already downloaded
		                AjxImg._drawCanvasImage(ctx, maskElement, overlayElement, mask, overlay, color, width, height);
		                overlay[color] = canvas.toDataURL();
	                }
                }

                html = [
                    "<img src='", overlay[color], "'"," border=0 ", AjxUtil.getClassAttr(classes), styleStr, attrStr, ">"
                ].join("");
            }
        }
        else {
	        classes.push("Img" + imageName);
            html = [
                "<div ", AjxUtil.getClassAttr(classes), styleStr, attrStr, "></div>"
            ].join("");
        }
	}
    else {
        html = [
            "<div", styleStr, attrStr, "></div>"
        ].join("");
    }
	return pre || post ? [pre,html,post].join("") : html;
};

/**
 * Gets the "image" as an HTML string.
 *
 * @param imageName		     the image you want to render
 * @param imageStyleStr      optional style info (for example, "display:inline")
 * @param attrStr		     optional attributes (for example, "id=X748")
 * @param label			     the text that follows this image
 * @param containerClassName class to use instead of the default inlineIcon class
 * @return	{string}	     the image string
 */
AjxImg.getImageSpanHtml =
function(imageName, imageStyleStr, attrStr, label, containerClassName) {
    containerClassName = containerClassName || "inlineIcon";
	var html = [
        "<span style='white-space:nowrap'>",
        "<span class='",
        containerClassName,
        "'>",
        AjxImg.getImageHtml(imageName, imageStyleStr, attrStr),
        (label || ""),
        "</span>",
        "</span>"
    ];

	return html.join("");
};

/**
 * Helper method to draw the image using both the mask image and the overlay image
 * 
 * @param ctx  {Object} canvas context
 * @param maskImg   {HtmlElement} mask image object
 * @param overlayImg {HtmlElement} overlay image object
 * @param mask  {Object} mask object
 * @param overlay {Object} overlay object
 * @param color {String} color for fill
 * @param width {int} width
 * @param height {int} height
 * 
 * @private
 */
AjxImg._drawCanvasImage = 
function(ctx, maskImg, overlayImg, mask, overlay, color, width, height) {
	ctx.drawImage(maskImg, mask.l, mask.t);
	ctx.globalCompositeOperation = "source-out";
	ctx.fillStyle = color;
	ctx.fillRect(0, 0, width, height);
	ctx.restore();
	ctx.drawImage(overlayImg, overlay.l, overlay.t);
	ctx.restore();	
};