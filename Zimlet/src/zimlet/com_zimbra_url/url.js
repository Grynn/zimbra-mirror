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
	
	this._disablePreview = this.getBoolConfig("disablePreview", true);
	this._youtubePreview = this.getBoolConfig("youTubePreview", true);
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

// chars to ignore if they follow a URL, since they are unlikely to be part of it
Com_Zimbra_Url.IGNORE = AjxUtil.arrayAsHash([".", ",", ";", "!", "*", ":", "?", ")", "]", "}"]);

Com_Zimbra_Url.prototype.match =
function(line, startIndex) {

	for (var i = 0; i < Com_Zimbra_Url.REGEXES.length; i++) {
		
		var re = Com_Zimbra_Url.REGEXES[i];
		re.lastIndex = startIndex;
		var m = re.exec(line);
		if (!m) { continue; }

		var url = m[0];
		var last = url.charAt(url.length - 1);
		while (url.length && Com_Zimbra_Url.IGNORE[last]) {
			url = url.substring(0, url.length - 1);
			last = url.charAt(url.length - 1);
		}
		m[0] = url;
		return m;
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
	}
	else if (this._alexaId) {
		this._showAlexaThumbnail(url, canvas);
	}
	else {
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

//Begin YouTube section
Com_Zimbra_Url.YOUTUBE_LINK_PATTERN1 = "youtube.com/watch?";
Com_Zimbra_Url.YOUTUBE_LINK_PATTERN2 = "youtube.com/v/";
Com_Zimbra_Url.YOUTUBE_LINK_PATTERN3 = "youtu.be/";
Com_Zimbra_Url.YOUTUBE_FEED = "http://gdata.youtube.com/feeds/api/videos/@ID?alt=jsonc&v=2";
Com_Zimbra_Url.YOUTUBE_EMBED_URL = "www.youtube.com/embed/";
Com_Zimbra_Url.YOUTUBE_DEFAULT_THUMBNAIL = "http://img.youtube.com/vi/@ID.jpg";
Com_Zimbra_Url.PROTOCOL = location.protocol;
Com_Zimbra_Url.YOUTUBE_MAX_VIDEOS = 5;

/**
 * Get the gDATA feed so we can parse for title & thumbnail
 */
Com_Zimbra_Url.prototype._getYouTubeFeed =
function() {
	for (var youTubeId in this._youTubeHash) {
		var gDataUrl = Com_Zimbra_Url.YOUTUBE_FEED;
		gDataUrl = gDataUrl.replace("@ID", youTubeId);
		gDataUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(gDataUrl);
		var params = {
				url: gDataUrl,
				callback: new AjxCallback(this, this._parseYouTubeFeed, [youTubeId])
			};
		AjxLoader.load(params);
	}
};

/**
 * Parse the feed for thumbnail, title
 * @param youTubeId {String} 11 character id
 * @param req {HttpResponse} http response
 */
Com_Zimbra_Url.prototype._parseYouTubeFeed =
function(youTubeId, req) {
	if (req.status != 200) {
		DBG.println(AjxDebug.DBG1, "Error code in parsing YouTube Feed. Code = " + req.status);
		var thumbnail = Com_Zimbra_Url.YOUTUBE_DEFAULT_THUMBNAIL.replace("@ID", youTubeId);
		var title = "";
		this._youTubeHash[youTubeId] = {"thumbnail" : thumbnail, "title" : title};
		this._buildYouTubeImageHtml(youTubeId);
	}
	else {
		var json = eval("(" + req.responseText + ")");
		var thumbnail = json.data.thumbnail["sqDefault"];
		var title = json.data.title;
		this._youTubeHash[youTubeId] = {"thumbnail" : thumbnail, "title" : title};
		this._buildYouTubeImageHtml(youTubeId);
	}
};

/**
 * Extract YouTube video Id from URL.
 * @param url
 * @return {String} id YouTube video id
 */
Com_Zimbra_Url.prototype.getYouTubeId =
function(url) {
	var id = null;
	var index = url.indexOf(Com_Zimbra_Url.YOUTUBE_LINK_PATTERN1);
	if (index != -1) {
		var qs = AjxStringUtil.parseQueryString(url);
		if (qs && qs['v']) {
			id = qs['v'];
		}
	}
	else {
		index = url.indexOf(Com_Zimbra_Url.YOUTUBE_LINK_PATTERN2);
		if (index != -1) {
			id = AjxStringUtil.trim(url.substring(index + Com_Zimbra_Url.YOUTUBE_LINK_PATTERN2.length));
		}
		else {
			index = url.indexOf(Com_Zimbra_Url.YOUTUBE_LINK_PATTERN3);
			if (index != -1) {
				id = AjxStringUtil.trim(url.substring(index + Com_Zimbra_Url.YOUTUBE_LINK_PATTERN3.length));
			}
		}
	}
	return id;
};

/**
 * Display YouTube video using iframe API
 * @param youTubeId
 * @param msgId
 */
Com_Zimbra_Url.prototype._showYouTubeVideo =
function(youTubeId, msgId) {
	if (!youTubeId || !msgId) return;

	var title = this._youTubeHash[youTubeId].title;
	var el = document.getElementById("youtube-video_" + this._viewId + "_" + msgId);
	var iframeEl = document.getElementById("youtube-iframe_" + this._viewId + "_" + msgId);
	if (el) {
		if (!Dwt.getVisible(el)) {
			el.innerHTML = "<br/>" + this._showYouTubeEmbed(youTubeId, msgId);
			Dwt.setVisible(el, true);
			this._setYouTubeOpacity(youTubeId, msgId, true);
		}
		else {
			if (iframeEl && iframeEl.src == Com_Zimbra_Url.PROTOCOL + '//www.youtube.com/embed/' + youTubeId) {
				Dwt.setVisible(el, false); //toggle visiblity
				this._setYouTubeOpacity(youTubeId, msgId, false);
			}
			else {
				el.innerHTML = "<br/>" + this._showYouTubeEmbed(youTubeId, msgId);
				this._setYouTubeOpacity(youTubeId, msgId, true);
			}
		}
	}
};

/**
 * Setup youtube video using iframe API
 * @param youTubeId
 * @param msgId
 */
Com_Zimbra_Url.prototype._showYouTubeEmbed = 
function(youTubeId, msgId) {
	return '<iframe id="youtube-iframe_' + this._viewId + '_' + msgId +'" class="youtube-player" type="text/html"' +
			'width="640" '  +
			'height="385"'  +
			'src="' + Com_Zimbra_Url.PROTOCOL + "//" +  Com_Zimbra_Url.YOUTUBE_EMBED_URL + youTubeId + '?autoplay=1&rel=0" frameborder="0"></iframe>';
};

/**
 * Display thumbnail of youtube video
 * @param youTubeId
 */
Com_Zimbra_Url.prototype._showYouTubeThumbnail =
function(youTubeId) {
	return "<div class='thumb-wrapper'><div class='play'></div><img src='" + this._youTubeHash[youTubeId].thumbnail + "' border='2'></div>"; 			   
};

/**
 * Parse the mail message for youtube links matching a specified pattern
 * @param text
 */
Com_Zimbra_Url.prototype._getAllYouTubeLinks =
function(text) {
	if (!text) return null;
	var youTubeArr = text.match(/(\b(((http | https)\:\/\/)?(www\.)?((youtube\.com\/watch\?v=)|(youtube\.com\/watch\?.*\&v=)|(youtube\.com\/v\/)|(youtu\.be\/))((-)?[0-9a-zA-Z_-]+)?(&\w+=\w+)*)\b)/gi);
	var hash = {};
	var hashCount = 0;
	if (youTubeArr) {
		for (var i=0; i<youTubeArr.length; i++) {
			var id = this.getYouTubeId(youTubeArr[i]);
			if (!hash.hasOwnProperty(id)) {
				hash[id] = youTubeArr[i];
				hashCount++
			}
		}
		//quick check to see if hash is bigger than max before we do a loop through the hash
		if (hashCount > Com_Zimbra_Url.YOUTUBE_MAX_VIDEOS) {
			var tempHash = {};
			var size = 1;
			for (var key in hash) {
				if (size > Com_Zimbra_Url.YOUTUBE_MAX_VIDEOS){
					this._youTubeHitMax = true;
					return tempHash;
				}
				tempHash[key] = true;
				size++;
			}
			return tempHash;
		}
		return hash;
	}
	return null;
};

/**
 * Build the HTML of YouTube thumbnails
 * @param youTubeId
 */
Com_Zimbra_Url.prototype._buildYouTubeImageHtml =
function(youTubeId) {
	if (!this._youTubeHash || !youTubeId || document.getElementById("YOUTUBE_" + youTubeId + "_" + this._viewId + "_" + this._msgId)) return;
	
	var imgHtml = this._showYouTubeThumbnail(youTubeId);	
	var imgSpan = document.createElement("div");
	imgSpan.className = "youTubeImagePreview";
	imgSpan.innerHTML = imgHtml;
	imgSpan.id = "YOUTUBE_" + youTubeId + "_" + this._viewId + "_" + this._msgId;
	this._youTubeCtrlHash[this._msgId].getHtmlElement().appendChild(imgSpan);
	this._youTubeCtrlHash[this._msgId].setData(imgSpan.id, {youTubeId: youTubeId, msgId: this._msgId});
	var parentDiv = document.getElementById("youtube_" + this._viewId + "_" + this._msgId);
	var videoDiv = document.getElementById("youtube-video_" + this._viewId + "_" + this._msgId);
	if (parentDiv && videoDiv) {
		parentDiv.insertBefore(this._youTubeCtrlHash[this._msgId].getHtmlElement(), videoDiv);
		Dwt.setVisible(parentDiv, true);
	}
};

/**
 * Onclick handler for playing video.
 * @param ev
 */
Com_Zimbra_Url.prototype._onYouTubeClickListener =
function(ev) {
	if (ev && ev.target && ev.target.parentNode && ev.target.parentNode.parentNode) {
		var id = ev.target.parentNode.parentNode.id.replace("YOUTUBE_", "");
		id = id.replace("_" + this._viewId, "");
		var youTubeId = id.substring(0, 11);
		var msgId = id.substring(12);	
		if (youTubeId && msgId) {
			this._showYouTubeVideo(youTubeId, msgId);
		}
	}
};

Com_Zimbra_Url.prototype._onYouTubeMouseOver =
function(ev) {
	if (ev && ev.target && ev.target.tagName.toLowerCase() == "img") {
		ev.target.style.border = "2px solid white";
	}
};

Com_Zimbra_Url.prototype._onYouTubeMouseOut =
function(ev) {
   	if (ev && ev.target && ev.target.tagName.toLowerCase() == "img") {
		ev.target.style.border = "2px solid";
	}
};

Com_Zimbra_Url.prototype._setYouTubeOpacity =
function(youTubeId, msgId, opacity) {
	var img = document.getElementById("YOUTUBE_" + youTubeId + "_" + this._viewId + "_" + msgId);
	if (img && img.firstChild && opacity) {
		img = img.firstChild;
	    img.style.opacity = 0.4;
		img.style.filter = "alpha(opacity=40)";
		//clear the other images opacity
		for (var id in this._youTubeHash) {
			if (id != youTubeId) {
				img = document.getElementById("YOUTUBE_" + id + "_" + this._viewId + "_" + msgId);
				if (img && img.firstChild) {
					img.firstChild.style.opacity = "";
					img.firstChild.style.filter = "";
				}
			}
		}
	}
	else if(img && img.firstChild) {
		img = img.firstChild;
		img.style.opacity = "";
		img.style.filter = "";
	}
};

/**
 * handle youtube videos on conversation view
 * @param msg
 */
Com_Zimbra_Url.prototype.onConvView = 
function(msg) {
	this._isConv =true;
	this.renderYouTube(msg);
};

/**
 * handle youtube videos on message view
 * @param msg
 */
Com_Zimbra_Url.prototype.onMsgView = 
function(msg) {
	this._isConv = false;
	this.renderYouTube(msg);
}

Com_Zimbra_Url.prototype.renderYouTube =
function(msg) {
	if (!this._youtubePreview) return;
	this._youTubeHitMax = false; //reset
	this._youTubeHash = {}; //hash of youtube links
	this._youTubeCtrlHash = {};  //hash to keep track of video controls for conversation view

	var text = AjxStringUtil.getOriginalContent(msg.getBodyContent(), true); 
	this._youTubeHash = this._getAllYouTubeLinks(text);
	if (!this._youTubeHash) return;

	this._msgId = msg.id;
	this._viewId = appCtxt.getCurrentViewId();
	var view = appCtxt.getCurrentView().getItemView();
	var widgetId = this._isConv ? (view._msgViews && view._msgViews[this._msgId] && view._msgViews[this._msgId]._msgBodyDivId) : view._msgBodyDivId;
	var el = document.getElementById(widgetId);
	
	if (el) {
		var div = document.createElement("DIV");
		div.className = "video-panel";
		div.id = "youtube_" + this._viewId + "_" + this._msgId;
		div.style.display = "none"; //don't show until we're getting all the thumbnails
		var title = this.getMessage("youTubeTitle");
		if (this._youTubeHitMax) {
			title = this.getMessage("youTubeTitleMax").replace("{0}", Com_Zimbra_Url.YOUTUBE_MAX_VIDEOS);
		}
		div.innerHTML = "<h3 class='user_font_" + appCtxt.get(ZmSetting.FONT_NAME) +"'>" + title + "</h3>";
		
		var spaceDiv = document.createElement("div");
		spaceDiv.style.padding = "5px";
		el.appendChild(spaceDiv);
		el.appendChild(div);

		var videoDiv = document.createElement("div");
		videoDiv.id = "youtube-video_" + this._viewId + "_" + this._msgId;
		videoDiv.style.display = "none";
		videoDiv.className = "movie-player";
		div.appendChild(videoDiv);

		this._getYouTubeFeed();
		
		var youTubeCtrl = new DwtControl({parent:appCtxt.getShell()});
		youTubeCtrl.addListener(DwtEvent.ONMOUSEDOWN, this._onYouTubeClickListener.bind(this));
		youTubeCtrl.addListener(DwtEvent.ONMOUSEOVER, this._onYouTubeMouseOver.bind(this));
		youTubeCtrl.addListener(DwtEvent.ONMOUSEOUT, this._onYouTubeMouseOut.bind(this));
		youTubeCtrl.reparentHtmlElement(videoDiv);

		this._youTubeCtrlHash[this._msgId] = youTubeCtrl;
	}

};

Com_Zimbra_Url.CALENDAR_URL_EXTENSION = 'ics';

Com_Zimbra_Url.prototype.getActionMenu =
function(obj, span, context) {
    var uri = AjxStringUtil.parseURL(obj),
        fileName = uri.fileName,
        extension = fileName ? fileName.substring(fileName.lastIndexOf('.') + 1) : '';
    if(!appCtxt.get(ZmApp.SETTING[ZmId.APP_CALENDAR]) ||
        extension != Com_Zimbra_Url.CALENDAR_URL_EXTENSION) {
        return false;
    }
	if (this._zimletContext._contentActionMenu instanceof AjxCallback) {
		this._zimletContext._contentActionMenu = this._zimletContext._contentActionMenu.run();
	}
	// Set some global context since the parent Zimlet (Com_Zimbra_Date) will be called for
	// right click menu options, even though the getActionMenu will get called on the sub-classes.
	Com_Zimbra_Url._actionObject = obj;
	Com_Zimbra_Url._actionSpan = span;
	Com_Zimbra_Url._actionContext = context;
	return this._zimletContext._contentActionMenu;
};

Com_Zimbra_Url.prototype.menuItemSelected =
function(itemId, ev) {
	switch (itemId) {
		case "NEWCAL":		this._newCalListener(ev); break;
		case "GOTOURL":		this._goToUrlListener(); break;
	}
};

Com_Zimbra_Url.prototype._goToUrlListener =
function() {
    window.open(Com_Zimbra_Url._actionObject, "_blank");
};

Com_Zimbra_Url.prototype.getMainWindow =
function(appId) {
	return appCtxt.isChildWindow ? window.opener : window;
};

Com_Zimbra_Url.prototype._newCalListener =
function(ev) {
    AjxDispatcher.require(["CalendarCore", "Calendar"]);
    var oc = appCtxt.getOverviewController();
	var treeController = oc.getTreeController(ZmOrganizer.CALENDAR);

    var iCal = {
                url : Com_Zimbra_Url._actionObject
            };
    treeController._newListener(ev);
    var dialog = appCtxt.getNewCalendarDialog();
    dialog.setICalData(iCal);
};
