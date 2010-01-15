/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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

function _initAmazon() {
	function _queryAmazon(ctxt, q, domain) {
		var searchIndex;

		if (domain == "awsmusic") {
			searchIndex = "Music";
		} else if (domain == "awsbooks") {
			searchIndex = "Books";
		}

		var q_url = ctxt.getConfig("amznUrl");
		var args = { Service: "AWSECommerceService", 
					 Operation: "ItemSearch", 
					 SearchIndex: searchIndex, 
					 ResponseGroup: "Request,Small", 
					 Version: "2004-11-10" };
		args.SubscriptionId = ctxt.getConfig("amazonKey");
		args.Keywords = AjxStringUtil.urlEncode(q);
		var sep = "?";
		for (var arg in args) {
			q_url = q_url + sep + arg + "=" + args[arg];
			sep = "&";
		}
		return {"url":q_url, "req":null}
	};

	var amzn = new Object();
	amzn.label = "Amazon Music";
	amzn.id = "awsmusic";
	amzn.icon = "Amazon-panelIcon";
	amzn.xsl = "amazon/amazon.xsl";
	amzn.queryAmazon = _queryAmazon;
	amzn.getRequest = 
		function (ctxt, q) { return this.queryAmazon(ctxt, q, this.id) };
		
	Com_Zimbra_Xslt.registerService(amzn);

	amzn = new Object();
	amzn.label = "Amazon Books";
	amzn.id = "awsbooks";
	amzn.icon = "Amazon-panelIcon";
	amzn.xsl = "amazon/amazon.xsl";
	amzn.queryAmazon = _queryAmazon;
	amzn.getRequest = 
		function (ctxt, q) { return this.queryAmazon(ctxt, q, this.id) };
		
	Com_Zimbra_Xslt.registerService(amzn);
};

_initAmazon();
