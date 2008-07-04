/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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

function Com_Zimbra_Tracking() {
}

Com_Zimbra_Tracking.prototype = new ZmZimletBase();
Com_Zimbra_Tracking.prototype.constructor = Com_Zimbra_Tracking;

Com_Zimbra_Tracking.UPS = "1[zZ]\\s?\\w{3}\\s?\\w{3}\\s?\\w{2}\\s?\\w{4}\\s?\\w{3}\\s?\\w{1}";
Com_Zimbra_Tracking.FEDEX = "(\\d{12}|\\d{22})";
Com_Zimbra_Tracking.TRACKING = "\\b(?:" + Com_Zimbra_Tracking.UPS + "|" + Com_Zimbra_Tracking.FEDEX + ")\\b";
Com_Zimbra_Tracking.TRACKING_RE = new RegExp(Com_Zimbra_Tracking.TRACKING, "g");

Com_Zimbra_Tracking.prototype.match =
function(line, startIndex) {
	Com_Zimbra_Tracking.TRACKING_RE.lastIndex = startIndex;
	var m = Com_Zimbra_Tracking.TRACKING_RE.exec(line);
	if (m) {
		if (m[0].charAt(1) == 'z' || m[0].charAt(1) == 'Z') {
			m.context = "ups";
		} else {
			m.context = "fedex";
		}
	}
	return m;
};

Com_Zimbra_Tracking.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {
	var html;
	if (matchContext == 'ups') {
		html = "<b>UPS Tracking Number: </b>" + AjxStringUtil.htmlEncode(contentObjText);
	} else if (matchContext == 'fedex') {
		html = "<b>Fedex Tracking Number: </b>" + AjxStringUtil.htmlEncode(contentObjText);
	} else {
		html = "<b>Tracking Number: </b>" + AjxStringUtil.htmlEncode(contentObjText);
	}
	canvas.innerHTML = html;
};

Com_Zimbra_Tracking.prototype._getHtmlContent =
function(html, idx, tracking, context) {
	var t = tracking.replace(/\s/g, '');
	var url;
	if (context == 'ups') {
		url = "http://wwwapps.ups.com/WebTracking/processInputRequest?" + "sort_by=status&tracknums_displayed=1&TypeOfInquiryNumber=T&loc=en_US&InquiryNumber1=" + t + "&track.x=0&track.y=0";
	} else if (context == 'fedex') {
		url = "http://www.fedex.com/Tracking?tracknumbers=" + t;
	}

	if (url) {
		html[idx++] = '<a target="_blank" href="' + url + '">' + AjxStringUtil.htmlEncode(tracking) + '</a>';
	} else {
		html[idx++] = AjxStringUtil.htmlEncode(tracking);
	}
	return idx;
};