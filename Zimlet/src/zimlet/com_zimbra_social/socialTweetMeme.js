/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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

//Author: Raja Rao DV (rrao@zimbra.com)

function com_zimbra_socialTweetMeme(zimlet) {
	this.zimlet = zimlet;
}

com_zimbra_socialTweetMeme.prototype.getTweetmemeCategories =
function() {
	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode("http://api.tweetmeme.com/stories/categories.json");
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this._tweetMemeCallback), true);
};

com_zimbra_socialTweetMeme.prototype._tweetMemeCallback =
function(response) {
	var jsonObj = this.zimlet._extractJSONResponse(null, this.zimlet.getMessage("tweetMemeError"), response);
	if(jsonObj.error) {
		if(appCtxt.getCurrentAppName().indexOf("social") > 0) {//dont show error unless in social tab
			return;
		}
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("tweetMemeError") + jsonObj.error, ZmStatusView.LEVEL_WARNING);
		return;
	}
	var cats = jsonObj.categories;
	this.allTweetMemeCats = new Array();
	this.allTweetMemeCats.push({query:"__MOST_POPULAR__", name:"Most Popular"});
	this.allTweetMemeCats.push({query:"__MOST_RECENT__", name:"Most Recent"});

	for (var i = 0; i < cats.length; i++) {
		this.allTweetMemeCats.push({query:cats[i].name, name:cats[i].display});
	}
	if (this.zimlet.preferences.social_pref_tweetmemePopularIsOn) {
		for (var i = 0; i < 1; i++) {
			var folder = this.allTweetMemeCats[i];
			var tableId = this.zimlet._showCard({headerName:folder.name, type:"TWEETMEME", autoScroll:false});
			this.tweetMemeSearch({query:folder.query, tableId:tableId});
		}
	}
	this.zimlet._updateAllWidgetItems({updateTweetMemeTree:true});
};

com_zimbra_socialTweetMeme.prototype.tweetMemeSearch =
function(params) {
	var query = params.query;
	var url = "";
	if (query == "__MOST_POPULAR__")
		url = "http://api.tweetmeme.com/stories/popular.json?";
	else if (query == "__MOST_RECENT__")
		url = "http://api.tweetmeme.com/stories/recent.json";
	else
		url = "http://api.tweetmeme.com/stories/popular.json?category=" + AjxStringUtil.urlComponentEncode(params.query);

	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this._tweetMemeSearchCallback, params), true);
};
com_zimbra_socialTweetMeme.prototype._tweetMemeSearchCallback =
function(params, response) {
	var jsonObj = this.zimlet._extractJSONResponse(params.tableId, this.zimlet.getMessage("tweetMemeError"), response);
	if(jsonObj.stories) {
		jsonObj = jsonObj.stories;
	}
	this.zimlet.createCardView({tableId:params.tableId, items:jsonObj, type:"TWEETMEME"});
};