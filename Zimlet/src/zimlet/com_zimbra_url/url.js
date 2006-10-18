/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function Com_Zimbra_Url() {
}

Com_Zimbra_Url.prototype = new ZmZimletBase();
Com_Zimbra_Url.prototype.constructor = Com_Zimbra_Url;

Com_Zimbra_Url.prototype.init =
function() {
	// Pre-load placeholder image
	(new Image()).src = this.getResource('blank_pixel.gif');
};

// Const
//Com_Zimbra_Url.THUMB_URL = "http://pthumbnails.alexa.com/image_server.cgi?id=" + document.domain + "&url=";
Com_Zimbra_Url.THUMB_URL = "http://images.websnapr.com/?url=";
Com_Zimbra_Url.THUMB_SIZE = 'width="200" height="150"';

Com_Zimbra_Url.prototype.match =
function(line, startIndex) {
	this.RE.lastIndex = startIndex;
	var m = this.RE.exec(line);
	if (!m) {
		return null;
	}

	var last = m[0].charAt(m[0].length - 1);
	if (last == '.' || last == "," || last == '!') {
		var m2 = {index: m.index };
		m2[0] = m[0].substring(0, m[0].length - 1);
		return m2;
	} else {
		return m;
	}
};

Com_Zimbra_Url.prototype._getHtmlContent =
function(html, idx, obj, context) {
	var escapedUrl = obj.replace(/\"/g, '\"');
	if (escapedUrl.substr(0, 4) == 'www.') {
		escapedUrl = "http://" + escapedUrl + "/";
	}
	html[idx++] = "<a target='_blank' href='";
	html[idx++] = escapedUrl;
	html[idx++] = "'>";
	html[idx++] = AjxStringUtil.htmlEncode(obj);
	html[idx++] = "</a>";
	return idx;
};

Com_Zimbra_Url.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	var html = [];
	var i = 0;

	html[i++] = "<img src='";
	html[i++] = this.getResource("blank_pixel.gif");
	html[i++] = "' ";
	html[i++] = Com_Zimbra_Url.THUMB_SIZE;
	html[i++] = " style='background: url(";
	html[i++] = '"';
	html[i++] = Com_Zimbra_Url.THUMB_URL;
	html[i++] = obj;
	html[i++] = '"';
	html[i++] = ")'/>";

	canvas.innerHTML = html.join("");
};
