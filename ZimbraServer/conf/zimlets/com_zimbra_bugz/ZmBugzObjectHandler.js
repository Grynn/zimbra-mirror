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

function ZmBugzObjectHandler(appCtxt) {
	ZmZimletBase.call(this, appCtxt, ZmBugzObjectHandler.TYPE);
}

ZmBugzObjectHandler.TYPE = "bugz";

ZmBugzObjectHandler.prototype = new ZmZimletBase();

ZmBugzObjectHandler.bug_re = /\bbug(?:zilla)?:?\s*#?(\d+)\b/g;

ZmBugzObjectHandler.bug_items = new Array(
	"bug_id",       "ID",
	"bug_status",   "Status",
	"priority",     "Priority",
	"bug_severity", "Severity",
	"product",      "Product",
	"component",    "Component",
	"version",      "Version",
	"reporter",     "Reporter",
	"assigned_to",  "Owner",
	"short_desc",   "Description");

ZmBugzObjectHandler.prototype.match =
function(line, startIndex) {
	ZmBugzObjectHandler.bug_re.lastIndex = startIndex;
	var match = ZmBugzObjectHandler.bug_re.exec(line);
	if (match) {
		match.context = match[1];
	}
	return match;
};

ZmBugzObjectHandler.prototype.toolTipPoppedUp =
function(spanElement, obj, context, canvas) {
	canvas.innerHTML = ZmBugzObjectHandler.generateTooltipText(context);
	var request = new AjxRpcRequest("bugzilla");
	var bug_url = this.getConfig("url")+context;
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlEncode(bug_url);
	request.invoke(null, url, null, new AjxCallback(this, ZmBugzObjectHandler._callback, context), true);
};

ZmBugzObjectHandler._callback =
function(args) {
	var resp = args[1].text;
	for (i = 0; i < ZmBugzObjectHandler.bug_items.length; i += 2) {
		// XXX maybe properly parse the xml into DOM
		var key = ZmBugzObjectHandler.bug_items[i];
		var item_s = resp.indexOf("<"+key+">");
		var item_e = resp.indexOf("</"+key+">");
		var val = "";
		if (item_s > 0 && item_e > 0) {
			val = "<b>"+ZmBugzObjectHandler.bug_items[i+1]+": </b>"+resp.substring(item_s+key.length+2, item_e);
			document.getElementById(ZmBugzObjectHandler.encodeId(args[0], key)).innerHTML=val;
		}
	}
};

ZmBugzObjectHandler.generateTooltipText =
function(obj) {
	var ret = "";
	for (i = 0; i < this.bug_items.length; i += 2) {
		var key = this.bug_items[i];
		var text = this.bug_items[i+1];
		ret += "<div id=\""+this.encodeId(obj, key)+"\"><b>"+text+": </b></div>";
	}
	return ret;
};

ZmBugzObjectHandler.encodeId =
function(obj, key) {
	return "bugz"+obj+"_"+key;
};