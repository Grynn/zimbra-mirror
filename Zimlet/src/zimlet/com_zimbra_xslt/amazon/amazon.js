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
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
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
