/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/**
* @class ZaProxyPortWarningXDialog
* @contructor ZaProxyPortWarningXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
ZaProxyPortWarningXDialog = function(parent, w, h, title, instance) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent,null, title, w, h);
	this._containedObject = instance ? instance :  {selectedChoice:0, choice1Label:"",choice2Label:"",choice3Label:"",warningMsg:""};
	var objModel = {
		items:[
			{id:"selectedChoice", type:_NUMBER_,defaultValue:0},
			{id:"choice1Label", type:_STRING_,defaultValue:ZaMsg.Server_WrongPortWarning_OP1},
			{id:"choice2Label", type:_STRING_,defaultValue:ZaMsg.Server_WrongPortWarning_OP2},
			{id:"choice3Label", type:_STRING_,defaultValue:ZaMsg.Server_WrongPortWarning_OP3},	
			{id:"warningMsg", type:_STRING_,defaultValue:ZaMsg.Server_WrongPortWarning}
		]
	}
	this.initForm(objModel,this.getMyXForm(),this._containedObject);
}

ZaProxyPortWarningXDialog.prototype = new ZaXDialog;
ZaProxyPortWarningXDialog.prototype.constructor = ZaProxyPortWarningXDialog;

ZaProxyPortWarningXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:1,
		items:[
			{type:_ZAWIZGROUP_,numCols:2,colSizes:["100px","400px"],
				items:[
					{ type: _DWT_ALERT_,
					  containerCssStyle: "padding-bottom:0px",
					  style: DwtAlert.WARNING,
					  iconVisible: true, 
					  ref:"warningMsg",
					  forceUpdate: true
					},
					{type:_RADIO_LABEL_,forceUpdate: true, ref:"selectedChoice",labelRef:"choice1Label",label:"N/A",groupname:"proxyChoice", value:0},
					{type:_RADIO_LABEL_,forceUpdate: true, ref:"selectedChoice",labelRef:"choice2Label",label:"N/A",groupname:"proxyChoice", value:1},
					{type:_RADIO_LABEL_,forceUpdate: true, ref:"selectedChoice",labelRef:"choice3Label",label:"N/A",groupname:"proxyChoice", value:2}
				]
			}
		]
	};
	return xFormObject;
}
