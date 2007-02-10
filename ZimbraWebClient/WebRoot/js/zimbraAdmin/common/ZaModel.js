/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
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


ZaModel.BOOLEAN_CHOICES= [{value:"TRUE", label:ZaMsg.Yes}, {value:"FALSE", label:ZaMsg.No}, {value:null, label:ZaMsg.No}];
ZaModel.BOOLEAN_CHOICES1= [{value:true, label:ZaMsg.Yes}, {value:false, label:ZaMsg.No}, {value:null, label:ZaMsg.No}];

ZaModel.COMPOSE_FORMAT_CHOICES = [{value:"text", label:ZaMsg.Text}, {value:"html", label:ZaMsg.HTML}];
ZaModel.GROUP_MAIL_BY_CHOICES = [{value:"conversation", label:ZaMsg.Conversation}, {value:"message", label:ZaMsg.Message}];
ZaModel.SIGNATURE_STYLE_CHOICES = [{value:"outlook", label:ZaMsg.No}, {value:"internet", label:ZaMsg.Yes}];
ZaModel.REMINDER_CHOICES = [{value:"0",label:ZaMsg.never},{value:1,label:"1"},{value:5,label:"5"},{value:10,label:"10"},{value:15,label:"15"},{value:20,label:"20"},{value:25,label:"25"},{value:30,label:"30"},{value:45,label:"45"},{value:50,label:"50"},{value:55,label:"55"},{value:60,label:"60"}];
ZaModel.ErrorCode = "code";
ZaModel.ErrorMessage = "error_message";
ZaModel.currentStep = "currentStep";
ZaModel.currentTab = "currentTab";

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


