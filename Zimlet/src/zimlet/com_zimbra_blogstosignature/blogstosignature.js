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
 * @author Raja Rao DV rrao@zimbra.com
 */

/**
 * Constructor
 */
function com_zimbra_blogstosignature_HandlerObj() {
}

com_zimbra_blogstosignature_HandlerObj.prototype = new ZmZimletBase();
com_zimbra_blogstosignature_HandlerObj.prototype.constructor = com_zimbra_blogstosignature_HandlerObj;

/**
 * Simplify Zimlet handler name.
 */
var BlogsToSignatureZimlet = com_zimbra_blogstosignature_HandlerObj;

BlogsToSignatureZimlet.prototype.init =
function() {
	this.metaData = appCtxt.getActiveAccount().metaData;
	this._checkDateAndUpdateFeeds();
};


/**
 * Sends Appointment Summary if the date is different
 */
BlogsToSignatureZimlet.prototype._checkDateAndUpdateFeeds =
function() {
	var feedLastUpdateDate = this.getUserProperty("BlogsToSignatureZimlet_FeedDate");
	this._signatureLabel = this._zimletContext.getConfig("signatureLabel");
	this._todayStr = this._getTodayStr();
	if (!feedLastUpdateDate || feedLastUpdateDate != this._todayStr){
		this.rssFeedUrl = this._zimletContext.getConfig("rssFeed");
		this._invoke();
	} else {
		this._getRecentPostsMetaData();
	}
};

/**
 * Appends extra signature information. Called by the framework.
 * 
 * @param	{array}	buffer		a buffer to append to
 */
BlogsToSignatureZimlet.prototype.appendExtraSignature =
function(buffer) {
	buffer.push(this._getARecentBlogPost());
};

BlogsToSignatureZimlet.prototype._getARecentBlogPost =
function() {
	if(!this.blogsInArray) {
		this.blogsInArray = [];
		for(var title in this.blogToSignatureZimletRecentPosts) {
			this.blogsInArray.push({title:title, link:this.blogToSignatureZimletRecentPosts[title]});
		}
	}
	var randomNumber = Math.floor(Math.random() * this.blogsInArray.length);
	var obj = this.blogsInArray[randomNumber];	
	var title = obj.title.length > 80 ? obj.title.substring(0, 80) + "...":  obj.title;
	return  [this._signatureLabel, " ", title," ", this._shortenUrl(obj.link)].join("");
};

BlogsToSignatureZimlet.prototype._invoke =
function() {
	var feedUrl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(this.rssFeedUrl);
	AjxRpc.invoke(null, feedUrl, null, new AjxCallback(this, this._reponseHandler), true);
};

BlogsToSignatureZimlet.prototype._reponseHandler =
function(reponse) {
	var items = "";
	try {
		items = reponse.xml.getElementsByTagName("item");
	} catch(e) {//there was some expn getting feed
		this._showErrorMsg(e);
		return;
	}
	this.blogToSignatureZimletRecentPosts = new Array();
	var len = items.length > 5 ? 5 : items.length;//use only 5 latest blog posts

	for (var i = 0; i < len; i++) {
		try {
			var title = "";
			var link = "";
			var titleObj = items[i].getElementsByTagName("title")[0].firstChild;
			var linkObj = items[i].getElementsByTagName("link")[0].firstChild;
			if (titleObj.textContent) {
				title = titleObj.textContent;
				link = linkObj.textContent;
			} else if (titleObj.text) {
				title = titleObj.text;
				link = linkObj.text;
			}
			this.blogToSignatureZimletRecentPosts[title] = link;
			
		} catch(e) {//print some exception
			this._showErrorMsg(e);
			return;
		}
	}
	this.metaData.set("blogToSignatureZimletRecentPosts", this.blogToSignatureZimletRecentPosts, null, new AjxCallback(this, this._saveRecentPostsHandler));
};

BlogsToSignatureZimlet.prototype._shortenUrl =
function(longUrl) {
	var url = "http://api.bit.ly/shorten?"
			+ "version=" + AjxStringUtil.urlComponentEncode("2.0.1")
			+ "&longUrl=" + AjxStringUtil.urlComponentEncode(longUrl)
			+ "&login=" + AjxStringUtil.urlComponentEncode("zimbra")
			+ "&apiKey=" + AjxStringUtil.urlComponentEncode("R_20927271403ca63a07c25d17edc32a1d");

	var shortUrl = "";
	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	var response  = AjxRpc.invoke(null, entireurl, null, null, false);
	try { 
		var text = eval("(" + response.text + ")");
		 shortUrl = text.results[longUrl].shortUrl;
	}catch (e) {
		shortUrl = longUrl;
	}
	return shortUrl;
};

/**
 * Handle saving recent posts
 */
BlogsToSignatureZimlet.prototype._saveRecentPostsHandler =
function() {
	this.setUserProperty("BlogsToSignatureZimlet_FeedDate", this._todayStr, true);
};

/**
 * Gets cached recent blog post
 *
 * @param {AjxCallback} postCallback  a callback
 */
BlogsToSignatureZimlet.prototype._getRecentPostsMetaData =
function() {
	this.metaData.get("blogToSignatureZimletRecentPosts", null, new AjxCallback(this, this._handleRecentPostsMetaData));
};

/**
 * Handles recent post metadata
 *
 * @param {AjxCallback} postCallback  a callback
 * @param {object} result	 the response
 */
BlogsToSignatureZimlet.prototype._handleRecentPostsMetaData =
function(result) {
	this.blogToSignatureZimletRecentPosts = null; //nullify old data
	try {
		this.blogToSignatureZimletRecentPosts = result.getResponse().BatchResponse.GetMailboxMetadataResponse[0].meta[0]._attrs;
	} catch(ex) {
		this._showErrorMessage(ex);
	}
};
/*
 * -------------------------------------
 * Supporting functions
 * -------------------------------------
 */

/**
 * Gets today as a string.
 * 
 * @return	{string}	today as a string
 */
BlogsToSignatureZimlet.prototype._getTodayStr =
function() {
	var todayDate = new Date();
	var todayStart = new Date(todayDate.getFullYear(), todayDate.getMonth(), todayDate.getDate());
	return this._formatDate(todayStart.getMonth() + 1, todayStart.getDate(), todayStart.getFullYear());
};

/**
 * Formats the date.
 * 
 * @param	{string}	month		the month
 * @param	{string}	day		the day
 * @param	{string}	year		the year
 * @return	{string}	the formatted date
 */
BlogsToSignatureZimlet.prototype._formatDate =
function(month, day, year) {
	var fString = [];
	var ds = I18nMsg.formatDateShort.toLowerCase();
	var arry = [];
	arry.push({name:"m", indx:ds.indexOf("m")});
	arry.push({name:"y", indx:ds.indexOf("y")});
	arry.push({name:"d", indx:ds.indexOf("d")});
	var sArry = arry.sort(BlogsToSignatureZimlet_sortTimeObjs);
	for (var i = 0; i < sArry.length; i++) {
		var name = sArry[i].name;
		if (name == "m") {
			fString.push(month);
		} else if (name == "y") {
			fString.push(year);
		} else if (name == "d") {
			fString.push(day);
		}
	}
	return fString.join("/");
};

/**
 * Sorts time objects based on index
 * 
 * @param	{hash}	a A hash
 * @param  {string} a.name  first letter of month/year/date
 * @param  {int} a.indx  index of month/year/date
 * @param	{hash}	b A hash
 * @param  {string} b.name  first letter of month/year/date
 * @param  {int} b.indx  index of month/year/date
 * @return	{hash}	sorted objects
 */
function BlogsToSignatureZimlet_sortTimeObjs(a, b) {
	var x = parseInt(a.indx);
	var y = parseInt(b.indx);
	return ((x > y) ? 1 : ((x < y) ? -1 : 0));
}

BlogsToSignatureZimlet.prototype._showErrorMsg =
function(msg) {
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.reset();
	msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE);
	msgDialog.popup();
};
