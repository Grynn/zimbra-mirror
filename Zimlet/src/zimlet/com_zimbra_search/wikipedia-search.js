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
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function Com_Zimbra_Search_Wikipedia(zimlet) {
	this.zimlet = zimlet;
	this.icon = "Wikipedia-Icon";
	this.label = "Search Wikipedia";
};

Com_Zimbra_Search_Wikipedia.prototype.getSearchFormHTML = function(query) {
	var zimlet = this.zimlet;
	var code = zimlet.getConfig("wikipedia-search-code");
	code = zimlet.xmlObj().replaceObj(ZmZimletContext.RE_SCAN_PROP, code, { query: query });
	return code;
};

Com_Zimbra_Search.registerHandler(Com_Zimbra_Search_Wikipedia);
