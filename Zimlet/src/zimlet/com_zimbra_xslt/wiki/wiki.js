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

function _initWiki() {
	var wiki = new Object();
	wiki.label = "Wikipedia";
	wiki.id = "wikipedia";
	wiki.icon = "Wiki-panelIcon";
	wiki.xsl = "wiki/mediawiki.xsl";
	wiki.getRequest = 
		function (ctxt, q) {
			return {"url":ctxt.getConfig("wikipediaUrl") + AjxStringUtil.urlEncode(q), "req":null}
		};
		
	Com_Zimbra_Xslt.registerService(wiki);

	wiki = new Object();
	wiki.label = "Wiktionary";
	wiki.id = "wiktionary";
	wiki.icon = "Wiki-panelIcon";
	wiki.xsl = "wiki/mediawiki.xsl";
	wiki.getRequest = 
		function (ctxt, q) {
			return {"url":ctxt.getConfig("wiktionaryUrl") + AjxStringUtil.urlEncode(q), "req":null}
		};
		
	Com_Zimbra_Xslt.registerService(wiki);
};

_initWiki();
