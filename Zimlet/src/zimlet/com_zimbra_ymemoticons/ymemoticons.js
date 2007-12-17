/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2007 Zimbra, Inc.
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

function Com_Zimbra_YMEmoticons() {
	this.re = Com_Zimbra_YMEmoticons.REGEXP;
	this.hash = Com_Zimbra_YMEmoticons.SMILEYS;
};

Com_Zimbra_YMEmoticons.prototype = new ZmZimletBase;
Com_Zimbra_YMEmoticons.prototype.constructor = Com_Zimbra_YMEmoticons;

// Com_Zimbra_YMEmoticons.prototype.init = function() {
// 	// not sure it's wise to register it for now.
// //	ZmObjectManager.registerHandler("Com_Zimbra_YMEmoticons", "ymemoticons", this._zimletContext.priority);
// };

Com_Zimbra_YMEmoticons.prototype.match = function(line, startIndex) {
	this.re.lastIndex = startIndex;
	var m = this.re.exec(line);
	if (m) {
		m.context = this.hash[m[1].toLowerCase()];
		// preload
		var img = new Image();
		img.src = m.context.src;
		m.context.img = img;
	}
	return m;
};

Com_Zimbra_YMEmoticons.prototype.generateSpan = function(html, idx, obj, spanId, context) {

	var h = context.height / 2;
	var a = [ "<span style='height:",context.height,";width:",context.width,";padding:", h, "px ", context.width,
		  "px ", h, "px 0; background:url(", context.img.src, ") no-repeat 0 50%;'",
		  ' title="',
		  AjxStringUtil.xmlAttrEncode(context.text), ' - ',
		  AjxStringUtil.xmlAttrEncode(context.alt), '"',
		  "></span>" ];
	html[idx++] = a.join("");
	return idx;
};
