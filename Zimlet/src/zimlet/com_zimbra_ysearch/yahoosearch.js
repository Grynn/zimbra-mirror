/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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

YahooSearch = function(parent, controller) {
	if (arguments.length == 0) { return; }
	this._controller = controller;

	if (this._controller._zimlet._settingPane) {
		// create composite to place the YahooSearch
		DwtComposite.call(this, {parent:parent, className:"YahooSearch", posStyle:Dwt.ABSOLUTE_STYLE, id:"YahooSearch"});

		this._searchElement = document.getElementById("YahooSearch");
		this._searchElement.innerHTML = '<iframe class="YahooSearch" name="YahooSearchFrame" id="YahooSearchFrame" frameborder="0"/>';
	}
};

YahooSearch.prototype = new DwtComposite;
YahooSearch.prototype.constructor = new YahooSearch;

//Yahoo Search
YahooSearch.prototype.searchYahoo =
function(query) {
	var searchUrl = ZmMsg["ysearchURL"];
	if(!searchUrl || searchUrl == "" || searchUrl == undefined){
		searchUrl = "http://search.yahoo.com";
	}
	if(query && query != "Search the Web...") {
		searchUrl += '/search?p='+query+'&fr=zim-maila', '_blank';
	} else {
		searchUrl += '/?fr=zim-maila';
	}

	if (this._controller._zimlet._settingPane) {
		frames['YahooSearchFrame'].location.href = searchUrl;
		frames['YahooSearchFrame'].focus();
	} else {
		window.open(searchUrl);
	}
};
