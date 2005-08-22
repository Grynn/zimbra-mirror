/*
***** BEGIN LICENSE BLOCK *****
Version: ZAPL 1.1

The contents of this file are subject to the Zimbra AJAX Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of the
License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
ANY KIND, either express or implied. See the License for the specific language governing rights
and limitations under the License.

The Original Code is: Zimbra AJAX Toolkit.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

//
//	DWT SHIM -- makes simple objects for all of the DWT things needed in the XForms test
//


//
//	browser sniffing
//
function LsEnv(){}
LsEnv.agt = navigator.userAgent.toLowerCase();
LsEnv.isIE = (LsEnv.agt.indexOf("msie") != -1 && LsEnv.agt.indexOf("opera") == -1);
LsEnv.isSafari = (LsEnv.agt.indexOf("safari") != -1);
LsEnv.isMoz = (LsEnv.agt.indexOf("mozilla") != -1 && !LsEnv.isSafari && !LsEnv.isIE);



//
//	utility functions
//
function LsUtil() {}
LsUtil.FLOAT_RE = /^[+\-]?((\d+(\.\d*)?)|((\d*\.)?\d+))([eE][+\-]?\d+)?$/;
LsUtil.isString = function(aThing) { 
	return typeof aThing == 'string' || LsUtil.isInstance(aThing, String);
}
LsUtil.isNumber = function(aThing) { 
	return typeof aThing == 'number' || LsUtil.isInstance(aThing, Number);
}
LsUtil.isObject = function(aThing) { 
	return typeof aThing == 'object' || LsUtil.isInstance(aThing, Object);
}
LsUtil.isArray = function(aThing) { 
	return LsUtil.isInstance(aThing, Array); 
}
LsUtil.isInstance = function(aThing, aClass) { 
	return aThing instanceof aClass; 
}



//
//	DwtComposite -- base class of the form
//
function DwtComposite() {}
var CP = DwtComposite.prototype;

CP.getHtmlElement = function(){}
CP.notifyListeners = function(){}
CP.addListener = function() {}
CP.removeListener = function () {}



//
//	event handling
//
function DwtEvent() {}
function DwtXFormsEvent() {}

DwtEvent.XFORMS_READY = "xforms-ready";
DwtEvent.XFORMS_DISPLAY_UPDATED = "xforms-display-updated";
DwtEvent.XFORMS_VALUE_CHANGED = "xforms-value-changed";

/*

//
//	event listening (used by OSelect)
//
function LsCore() {}

LsCore.addListener = function (eventSource, eventName, action) {
	eventSource[eventName] = action;
}

LsCore.removeListener = function (eventSource, eventName, action) {
	eventSource[eventName] = null;
	if (!LsEnv.isIE) delete eventSource[eventName];
}

*/

//
//	debugging
//

var DBG = {
	messages : []
};
//if (window.Components) {
//	DBG.console = Components.classes['@mozilla.org/consoleservice;1'].getService(Components.interfaces.nsIConsoleService);
//}

DBG.println = function () {
	arguments.join = this.messages.join;
	var message = arguments.join("");
	this.messages.push(message);
//	if (DBG.console) DBG.console.logStringMessage(message);
}

DBG.clear = function() {
	DBG.messages = [];
	XFG.getEl("debug").innerHTML = "<button onclick='DBG.clear()'>Clear<\/button><BR><pre>";
}

DBG.getMessages = function () {
	return this.messages.join("<BR>");
}



//
//	contacts things
//
function LmContactsBaseView() {}






var DwtMsg = {};
var LsMsg = {};
