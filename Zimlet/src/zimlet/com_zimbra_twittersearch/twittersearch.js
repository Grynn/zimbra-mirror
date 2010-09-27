/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/*
* @author Raja Rao DV (rrao@zimbra.com)
*/


function com_zimbra_twittersearch_handlerObject() {
};

com_zimbra_twittersearch_handlerObject.prototype = new ZmZimletBase();
com_zimbra_twittersearch_handlerObject.prototype.constructor = com_zimbra_twittersearch_handlerObject;

var TwitterSearchTooltipZimlet = com_zimbra_twittersearch_handlerObject;

/**
 * This method is called by Email Zimlet notifying this Zimlet(TwitterSearch) to add TwitterSearch slide to the tooltip
 */
TwitterSearchTooltipZimlet.prototype.onEmailHoverOver =
function(emailZimlet) {
	emailZimlet.addSubscriberZimlet(this, false);
	this.emailZimlet = emailZimlet;
	this._addSlide();
};

TwitterSearchTooltipZimlet.prototype._addSlide =
function() {
	var tthtml = this._getTooltipBGHtml();
	var selectCallback =  new AjxCallback(this, this._handleSlideSelect);
	
	this._slide = new EmailToolTipSlide(tthtml, true, "TwitterSearchZimletIcon", selectCallback);
	this.emailZimlet.slideShow.addSlide(this._slide);
	this._slide.setCanvasElement(document.getElementById("twitterSearchZimlet_searchResultsDiv"));
	this._addSearchHandlers();
};

TwitterSearchTooltipZimlet.prototype._handleSlideSelect =
function() {
	if(this._slide.loaded) {
		return;
	}
	document.getElementById("twitterSearchZimlet_MainDiv").style.height = document.getElementById(this._slide.id).offsetHeight;
	this._setSearchFieldValue(this._getDefaultQuery());
	this._searchTwitter();
	if(this._slide) {
		this._slide.loaded = true;	
	}
};

TwitterSearchTooltipZimlet.prototype._getDefaultQuery = function() {
	var name = "";
	if(this.emailZimlet.fullName != "") {
		name = this.emailZimlet.fullName;
	}
	if(name != "") {
		return name;
	}
	var tmpArry = this.emailZimlet.emailAddress.split("@");
	var part1 = tmpArry[0] ?  tmpArry[0] : "";
	var part2 = "";
	if(tmpArry.length == 2) {
		var tmpArry2 = tmpArry[1].split(".");
		part2 = tmpArry2[0]? tmpArry2[0] : "";		
	}
	if(name != "") {
		return ["\"", name, "\"", " OR ", part1, " OR ", part2].join("");
	} else {
		return [part1, " OR ", part2].join("");
	}
};

TwitterSearchTooltipZimlet.prototype._searchTwitter =
function() {
	this._slide.setInfoMessage(this.getMessage("searching"));
	var q = this._getSearchFieldValue();
	var url = "http://search.twitter.com/search.json";
	var params = ["q", "=", AjxStringUtil.urlComponentEncode(q)].join(""); 
	var entireurl = [url, "?", params].join(""); 

	var encodedEntireurl = AjxStringUtil.urlComponentEncode(entireurl); 
	var proxyUrl = [ZmZimletBase.PROXY, encodedEntireurl].join(""); 
	AjxRpc.invoke(null, proxyUrl, null, new AjxCallback(this, this._twitterSearchCallback), true);
};

TwitterSearchTooltipZimlet.prototype._twitterSearchCallback =
function(response) {
	if (!response.success) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("Twitter Error: " + response.text, ZmStatusView.LEVEL_WARNING, null, transitions);
		return;
	}
	var text = response.text;
	var jsonObj = eval("(" + text + ")");
	this._appendTwitterSearchResult(jsonObj);
};

TwitterSearchTooltipZimlet.prototype._appendTwitterSearchResult =
function(jsonObj) {
	var items = jsonObj.results;

	var len = items.length;
	if(len == 0) {
		this._slide.setErrorMessage(this.getMessage("noResultsFound"));
		return;
	}
	var allRows = [];
	for(var j = 0; j < len; j++) {
		var tweetItem = items[j];
		allRows.push(this._getTwitterRowHtml(tweetItem));
	}
	document.getElementById("twitterSearchZimlet_searchResultsDiv").innerHTML = allRows.join("");
};

TwitterSearchTooltipZimlet.prototype._getTwitterRowHtml =
function(tweetItem) {
	var subs = {
		imgUrl: tweetItem.profile_image_url,
		fromUser: tweetItem.from_user,
		tweet: tweetItem.text
	};
	return AjxTemplate.expand("com_zimbra_twittersearch.templates.TwitterSearch#RowItem", subs);
};

TwitterSearchTooltipZimlet.prototype._setSearchFieldValue =
function(val) {
	document.getElementById("twitterSearchZimlet_seachField").value = val;
};

TwitterSearchTooltipZimlet.prototype._getSearchFieldValue =
function() {
	return document.getElementById("twitterSearchZimlet_seachField").value;
};

TwitterSearchTooltipZimlet.prototype._getTooltipBGHtml =
function() {
	return AjxTemplate.expand("com_zimbra_twittersearch.templates.TwitterSearch#Frame");
};

TwitterSearchTooltipZimlet.prototype._setTooltipSticky =
function(sticky) {
	if(sticky) {
		this.emailZimlet.tooltip._poppedUp = false;//set this to make tooltip sticky
	} else {
		this.emailZimlet.tooltip._poppedUp = true;
	}
};

TwitterSearchTooltipZimlet.prototype._setTooltipNotSticky =
function() {
	this.emailZimlet.tooltip._poppedUp = true;
};

TwitterSearchTooltipZimlet.prototype._addSearchHandlers =
function() {
	document.getElementById("twitterSearchZimlet_MainDiv").onmouseover =  AjxCallback.simpleClosure(this._setTooltipSticky, this, true);
	document.getElementById("twitterSearchZimlet_MainDiv").onmouseout =  AjxCallback.simpleClosure(this._setTooltipSticky, this, false);
	var btn = new DwtButton({parent:this.emailZimlet.getShell()});
	btn.setText("Search");
	btn.setImage("TwitterSearchZimletIcon");
	btn.addSelectionListener(new AjxListener(this, this._searchTwitter));
	document.getElementById("twitterSearchZimlet_seachBtnCell").appendChild(btn.getHtmlElement());
};
