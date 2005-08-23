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


function ZaModel(init) {
 	if (arguments.length == 0) return;

	this._evtMgr = new AjxEventMgr();
}

ZaModel.prototype.toString = 
function() {
	return "ZaModel";
}

ZaModel.prototype.addChangeListener = 
function(listener) {
	return this._evtMgr.addListener(ZaEvent.L_MODIFY, listener);
}

ZaModel.prototype.removeChangeListener = 
function(listener) {
	return this._evtMgr.removeListener(ZaEvent.L_MODIFY, listener);    	
}

ZaModel.BOOLEAN_CHOICES= [{value:"TRUE", label:"Yes"}, {value:"FALSE", label:"No"}, {value:null, label:"No"}];
ZaModel.BOOLEAN_CHOICES1= [{value:1, label:"Yes"}, {value:0, label:"No"}, {value:null, label:"No"}];

ZaModel.COMPOSE_FORMAT_CHOICES = [{value:"text", label:"Text"}, {value:"html", label:"HTML"}];
ZaModel.GROUP_MAIL_BY_CHOICES = [{value:"conversation", label:"Conversation"}, {value:"message", label:"Message"}];
ZaModel.SIGNATURE_STYLE_CHOICES = [{value:"outlook", label:"Outlook"}, {value:"internet", label:"Internet"}];
ZaModel.ErrorCode = "code";
ZaModel.ErrorMessage = "error_message";
ZaModel.currentStep = "currentStep";
ZaModel.currentTab = "currentTab";