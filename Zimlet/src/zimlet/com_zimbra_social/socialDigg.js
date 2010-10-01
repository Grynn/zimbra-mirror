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

function com_zimbra_socialDigg(zimlet) {
	this.zimlet = zimlet;
}

com_zimbra_socialDigg.prototype.getDiggCategories =
function() {

	this.allDiggCats = new Array();
	this.allDiggCats.push({query:"Popular in 24hours", name:"Popular in 24hours"});
	this.allDiggCats.push({query:"technology", name:"technology"});
	this.allDiggCats.push({query:"science", name:"science"});
	this.allDiggCats.push({query:"sports", name:"sports"});
	this.allDiggCats.push({query:"world_business", name:"world_business"});
	this.allDiggCats.push({query:"entertainment", name:"entertainment"});
	this.allDiggCats.push({query:"videos", name:"videos"});
	this.allDiggCats.push({query:"Offbeat", name:"Offbeat"});

	if (this.zimlet.preferences.social_pref_diggPopularIsOn) {
		for (var i = 0; i < 1; i++) {
			var folder = this.allDiggCats[i];
			var tableId = this.zimlet._showCard({headerName:folder.name, type:"DIGG", autoScroll:false});
			this.diggSearch({query:folder.query, tableId:tableId});
		}
	}
	this.zimlet._updateAllWidgetItems({updateDiggTree:true});
};

com_zimbra_socialDigg.prototype.diggSearch =
function(params) {
	var query = params.query;
	var url = "";
	var tmp = new Date();
	var time = ((new Date(tmp.getFullYear(), tmp.getMonth(), tmp.getDate())).getTime() - 3600 * 24 * 1000) / 1000;
	var args = "min_promote_date=" + time + "&sort=digg_count-desc&appkey=http%3A%2F%2Fwww.zimbra.com&count=20&type=json";
	if (query == "Popular in 24hours")
		url = "http://services.digg.com/stories/popular?" + args;
	else
		url = "http://services.digg.com/stories/container/" + query + "/popular?" + args;

	var entireurl = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
	AjxRpc.invoke(null, entireurl, null, new AjxCallback(this, this._DiggSearchCallback, params), true);
};
com_zimbra_socialDigg.prototype._DiggSearchCallback =
function(params, response) {
	var text = response.text;
	if (!response.success) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("diggError") + text, ZmStatusView.LEVEL_WARNING, null, transitions);
		return;
	}
	var jsonObj = eval("(" + text + ")");
	this.zimlet.createCardView(params.tableId, jsonObj.stories, "DIGG");
};