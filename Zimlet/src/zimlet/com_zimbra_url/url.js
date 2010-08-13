/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

function Com_Zimbra_Url() {
}

Com_Zimbra_Url.prototype = new ZmZimletBase();
Com_Zimbra_Url.prototype.constructor = Com_Zimbra_Url;

Com_Zimbra_Url.prototype.init =
function() {
	
	this._disablePreview = this.getBoolConfig("disablePreview",true);

	this._alexaId = this.getConfig("alexaThumbnailId");	
	if (this._alexaId) {
		this._alexaId = AjxStringUtil.trim(this._alexaId);
		// console.log("Found Alexa ID: %s", this._alexaId);
		this._alexaKey = AjxStringUtil.trim(this.getConfig("alexaThumbnailKey"));
		// console.log("Found Alexa Key: %s", this._alexaKey);
	}
	Com_Zimbra_Url.REGEXES = [];
	//populate regular expressions
	var s = this.getConfig("ZIMLET_CONFIG_REGEX_VALUE");
	if(s){
		var r = new RegExp(s,"gi");
		if(r)
		Com_Zimbra_Url.REGEXES.push(r);
	}

	if (/^\s*true\s*$/i.test(this.getConfig("supportUNC"))) {
		s = this.getConfig("ZIMLET_UNC_REGEX_VALUE");
		var r = new RegExp(s,"gi");
		if(r)
		Com_Zimbra_Url.REGEXES.push(r);
	}

};

// Const
//Com_Zimbra_Url.THUMB_URL = "http://pthumbnails.alexa.com/image_server.cgi?id=" + document.domain + "&url=";
Com_Zimbra_Url.THUMB_URL = "http://images.websnapr.com/?url=";
Com_Zimbra_Url.THUMB_SIZE = 'width="200" height="150"';

Com_Zimbra_Url.prototype.match =
function(line, startIndex) {
	for (var i = 0; i < Com_Zimbra_Url.REGEXES.length; i++) {
		
		var re = Com_Zimbra_Url.REGEXES[i];
		re.lastIndex = startIndex;
		var m = re.exec(line);
		if (!m) {
			continue;
		}
		var last = m[0].charAt(m[0].length - 1);
		if (last == '.' || last == "," || last == '!' || (last == ')' && m[0].indexOf('(') == -1)) {
			var m2 = {index: m.index };
			m2[0] = m[0].substring(0, m[0].length - 1);
			return m2;
		} else {
			return m;
		}
	}
};

Com_Zimbra_Url.prototype._getHtmlContent =
function(html, idx, obj, context) {

	var escapedUrl = obj.replace(/\"/g, '\"').replace(/^\s+|\s+$/g, "");
	if (escapedUrl.substr(0, 4) == 'www.') {
		escapedUrl = "http://" + escapedUrl;
	}
	if (escapedUrl.indexOf("\\\\") == 0) {
		obj.isUNC = true;
		escapedUrl = "file://" + escapedUrl;
	}
	escapedUrl = escapedUrl.replace(/\\/g, '/');

	var link = "<a target='_blank' href='" + escapedUrl; // Default link to use when ?app= fails

	if (escapedUrl.split(/[\?#]/)[0] == ("" + window.location).split(/[\?#]/)[0]) {
		var paramStr = escapedUrl.substr(escapedUrl.indexOf("?"));
		if (paramStr) {
			var params = AjxStringUtil.parseQueryString(escapedUrl);
			if (params) {
				var app = params.app;
				if (app && app.length > 0) {
					app = app.toUpperCase();
					if (appCtxt.getApp(ZmApp[app])) {
						link = "<a href='javascript:top.appCtxt.getAppController().activateApp(top.ZmApp." + app + ", null, null);";
					}
				}
			}
		}
	}
	html[idx++] = link;
	html[idx++] = "'>";
	html[idx++] = AjxStringUtil.htmlEncode(obj);
	html[idx++] = "</a>";
	return idx;
};

Com_Zimbra_Url.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {

	var url = obj.replace(/^\s+|\s+$/g, "");
	if (/^\s*true\s*$/i.test(this.getConfig("stripUrls"))) {
		url = url.replace(/[?#].*$/, "");
	}
	if (url.indexOf("\\\\") == 0) {
		url = "file:" + url;
	}
	url = url.replace(/\\/g, '/');

	if (this._disablePreview || url.indexOf("file://") == 0) {  // local files
		this._showUrlThumbnail(url, canvas);
	} else if (this._alexaId) {
		this._showAlexaThumbnail(url, canvas);
	} else {
		// Pre-load placeholder image
		(new Image()).src = this.getResource('blank_pixel.gif');
		this._showFreeThumbnail(url, canvas);
	}
};

Com_Zimbra_Url.prototype.clicked = function(){
	var tooltip = DwtShell.getShell(window).getToolTip();
	if (tooltip) {
		tooltip.popdown();
	}
	return true;
};

Com_Zimbra_Url.prototype._showUrlThumbnail = function(url, canvas){
	canvas.innerHTML = "<b>URL:</b> " + AjxStringUtil.htmlEncode(decodeURI(url));
};

Com_Zimbra_Url.prototype._showFreeThumbnail = function(url, canvas) {
	var html = [];
	var i = 0;

	html[i++] = "<img src='";
	html[i++] = this.getResource("blank_pixel.gif");
	html[i++] = "' ";
	html[i++] = Com_Zimbra_Url.THUMB_SIZE;
	html[i++] = " style='background: url(";
	html[i++] = '"';
	html[i++] = Com_Zimbra_Url.THUMB_URL;
	html[i++] = url;
	html[i++] = '"';
	html[i++] = ")'/>";

	canvas.innerHTML = html.join("");
};

Com_Zimbra_Url.ALEXA_THUMBNAIL_CACHE = {};
Com_Zimbra_Url.ALEXA_CACHE_EXPIRES = 10 * 60 * 1000; // 10 minutes

Com_Zimbra_Url.prototype._showAlexaThumbnail = function(url, canvas) {
	canvas.innerHTML = [ "<table style='width: 200px; height: 150px; border-collapse: collapse' cellspacing='0' cellpadding='0'><tr><td align='center'>",
				 ZmMsg.fetchingAlexaThumbnail,
				 "</td></tr></table>" ].join("");

	// check cache first
	var cached = Com_Zimbra_Url.ALEXA_THUMBNAIL_CACHE[url];
	if (cached) {
		var diff = new Date().getTime() - cached.timestamp;
		if (diff < Com_Zimbra_Url.ALEXA_CACHE_EXPIRES) {
			// cached image should still be good, let's use it
			var html = [ "<img src='", cached.img, "' />" ].join("");
			canvas.firstChild.rows[0].cells[0].innerHTML = html;
			return;
		} else {
			// expired
			delete Com_Zimbra_Url.ALEXA_THUMBNAIL_CACHE[url];
		}
	}

	var now = new Date(), pad = Com_Zimbra_Url.zeroPad;
	var timestamp =
		pad(now.getUTCFullYear()  , 4) + "-" +
		pad(now.getUTCMonth() + 1 , 2) + "-" +
		pad(now.getUTCDate()	  , 2) + "T" +
		pad(now.getUTCHours()	  , 2) + ":" +
		pad(now.getUTCMinutes()	  , 2) + ":" +
		pad(now.getUTCSeconds()	  , 2) + ".000Z";
	// console.log("Timestamp: %s", timestamp);
	var signature = this._computeAlexaSignature(timestamp);
	// console.log("Computed signature: %s", signature);
	var args = {
		Service		: "AlexaSiteThumbnail",
		Action		: "Thumbnail",
		AWSAccessKeyId	: this._alexaId,
		Timestamp	: timestamp,
		Signature	: signature,
		Size		: "Large",
		Url		: url
	};
	var query = [];
	for (var i in args)
		query.push(i + "=" + AjxStringUtil.urlComponentEncode(args[i]));
	query = "http://ast.amazonaws.com/xino/?" + query.join("&");
	// console.log("Query URL: %s", query);
	this.sendRequest(null, query, null, new AjxCallback(this, this._alexaDataIn,
								[ canvas, url, query ]),
			 true);
};

Com_Zimbra_Url.prototype._computeAlexaSignature = function(timestamp) {
	return AjxSHA1.b64_hmac_sha1(this._alexaKey, "AlexaSiteThumbnailThumbnail" + timestamp)
		+ "=";		// guess what, it _has_ to end in '=' :-(
};

Com_Zimbra_Url.prototype._alexaDataIn = function(canvas, url, query, result) {
	var xml = AjxXmlDoc.createFromDom(result.xml);
	var res = xml.toJSObject(true /* drop namespace decls. */,
				 false /* keep case */,
				 true /* do attributes */);
	res = res.Response;
	if (res.ResponseStatus.StatusCode == "Success") {
		if (res.ThumbnailResult.Thumbnail.Exists == "true") {
			var html = [ "<img src='", res.ThumbnailResult.Thumbnail, "' />" ].join("");
			// console.log("HTML: %s", html);
			canvas.firstChild.rows[0].cells[0].innerHTML = html;

			// cache it
			Com_Zimbra_Url.ALEXA_THUMBNAIL_CACHE[url] = {
				img	  : res.ThumbnailResult.Thumbnail,
				timestamp : new Date().getTime()
			};
		} else
			this._showFreeThumbnail(url, canvas);
	} else
		this._showFreeThumbnail(url, canvas);
};

Com_Zimbra_Url.zeroPad = function(number, width) {
	var s = "" + number;
	while (s.length < width)
		s = "0" + s;
	return s;
};
