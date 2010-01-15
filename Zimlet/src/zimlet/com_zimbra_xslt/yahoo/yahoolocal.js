/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

function _initYahooLocal() {
	var yhoo = new Object();
	yhoo.label = "Yahoo Local";
	yhoo.id = "yahoolocal";
	yhoo.icon = "Yahoo-panelIcon";
	yhoo.xsl = "yahoo/yahoolocal.xsl";
	yhoo.getRequest = 
		function (ctxt, q) {
			var args = {};
			args.appid = ctxt.getConfig("ywsAppId");
			args.results = ctxt.getConfig("numResults");
			args.query = AjxStringUtil.urlEncode(q);

			var q_url;
			q_url = ctxt.getConfig("yhooLocalUrl");
			args.zip = ctxt.getConfig("zipcode");
			var sep = "?";
			for (var arg in args) {
				q_url = q_url + sep + arg + "=" + args[arg];
				sep = "&";
			}
			return {"url":q_url, "req":null}
		};
		
	Com_Zimbra_Xslt.registerService(yhoo);
};

_initYahooLocal();
