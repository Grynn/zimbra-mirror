/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */


/**
* @class
* This static class provides basic image support by using CSS and background 
* images rather than &lt;img&gt; tags. 
* @author Conrad Damon
* @author Ross Dargahi
*/
AjxImg = function() {};

AjxImg.prototype = new Object;
AjxImg.prototype.constructor = null;

AjxImg._VIEWPORT_ID = "AjxImg_VP";

AjxImg.DISABLED = true;

/**
* This method will set the image for <i>parentEl</i>. <i>parentEl</i> should 
* only contain this image and no other children
*
* @param parentEl 		The parent element for the image
* @param imageName 		The name of the image.  The CSS class for the image will be "Img<imageName>".
* @param useParenEl 	If true will use the parent element as the root for the image and will not create an intermediate DIV
* @param _disabled		If true, will append " ZDisabledImage" to the CSS class for the image, 
*							which will make the image partly transparent
*/
AjxImg.setImage =
function(parentEl, imageName, useParentEl, _disabled) {
	var className = AjxImg.getClassForImage(imageName, _disabled);

	if (useParentEl) {
		parentEl.className = className;
	} else {
		if (parentEl.firstChild == null) {
			parentEl.innerHTML = className 
                                ? "<div class='" + className + "'></div>"
			        : "<div></div>";
   		} else {
			parentEl.firstChild.className = className;
		}
	}
};

AjxImg.setDisabledImage = function(parentEl, imageName, useParentEl) {
	return AjxImg.setImage(parentEl, imageName, useParentEl, true);
};

AjxImg.getClassForImage =
function(imageName, disabled) {
	var className = "Img" + imageName;
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
* Gets the "image" as an HTML string. 
*
* @param imageName		the image you want to render
* @param styleStr		optional style info e.g. "display:inline"
* @param attrStr		optional attributes eg. "id=X748"
* @param wrapInTable	surround the resulting code in a table
*/
AjxImg.getImageHtml = 
function(imageName, styleStr, attrStr, wrapInTable) {
	attrStr = attrStr || "";
	styleStr = styleStr ? (["style='", styleStr, "' "].join("")) : "";
	var pre = wrapInTable ? "<table style='display:inline' cellpadding=0 cellspacing=0 border=0><tr><td align=center valign=bottom>" : "";
	var post = wrapInTable ? "</td></tr></table>" : "";
	if (imageName) {
		return [pre, "<div class='", "Img", imageName, "' ", styleStr, " ", attrStr, "></div>", post].join("");
	}
	return [pre, "<div ", styleStr, " ", attrStr, "></div>", post].join("");
};

/**
* Gets the "image" as an HTML string.
*
* @param imageName		the image you want to render
* @param styleStr		optional style info e.g. "display:inline"
* @param attrStr		optional attributes eg. "id=X748"
* @param label			the text that follows this image
*/
AjxImg.getImageSpanHtml =
function(imageName, styleStr, attrStr, label) {
	var className = AjxImg.getClassForImage(imageName);

	var html = [];
	var i = 0;
	html[i++] = "<span style='white-space:nowrap'>";
	html[i++] = "<span class='";
	html[i++] = className;
	html[i++] = " inlineIcon'";
	html[i++] = styleStr ? ([" style='", styleStr, "' "].join("")) : "";
	html[i++] = attrStr ? ([" ", attrStr].join("")) : "";
	html[i++] = ">&nbsp;&nbsp;&nbsp;</span>";
	html[i++] = (label || "");
	html[i++] = "</span>";

	return html.join("");
};
