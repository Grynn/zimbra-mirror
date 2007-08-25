/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
* @class
* This static class provides basic image support by using CSS and background 
* images rather than &lt;img&gt; tags. 
* @author Conrad Damon
* @author Ross Dargahi
*/
AjxImg = function() {
};

AjxImg.prototype = new Object;
AjxImg.prototype.constructor = null;

AjxImg._VIEWPORT_ID = "AjxImg_VP";

/**
* This method will set the image for <i>parentEl</i>. <i>parentEl</i> should 
* only contain this image and no other children
*
* @param parentEl 		The parent element for the image
* @param imageName 		The name of the image.  The CSS entry for the image will be "Img<imageName>".
* @param useParenEl 	If true will use the parent element as the root for the image and will not create an intermediate DIV
*/
AjxImg.setImage =
function(parentEl, imageName, useParentEl) {
	var className = AjxImg.getClassForImage(imageName);

	if (useParentEl) {
		parentEl.className = className;
	} else {
		if (parentEl.firstChild == null) {
			parentEl.innerHTML = className 
			   ? ["<div class='", className, "'></div>"].join("")
			   : "<div></div>";
   		} else {
			parentEl.firstChild.className = className;
		}
	}
};

AjxImg.getClassForImage =
function(imageName) {
	return ["Img", imageName].join("");
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
