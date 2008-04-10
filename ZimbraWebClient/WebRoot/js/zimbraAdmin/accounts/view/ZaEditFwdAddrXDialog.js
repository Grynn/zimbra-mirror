/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaEditAliasXDialog
* @contructor ZaEditAliasXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
ZaEditFwdAddrXDialog = function(parent,  app, w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent, app, null, title, w, h);
    this._helpURL = location.pathname + "adminhelp/html/WebHelp/managing_accounts/forwarding_mail.htm";
    this._containedObject = {};
	this.initForm(ZaAlias.myXModel,this.getMyXForm());
}

ZaEditFwdAddrXDialog.prototype = new ZaXDialog;
ZaEditFwdAddrXDialog.prototype.constructor = ZaEditFwdAddrXDialog;

ZaEditFwdAddrXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:1,
		items:[
			{ref:ZaAccount.A_name, type:_TEXTFIELD_, label:ZaMsg.Enter_EmailAddr,width:230}
		]
	};
	return xFormObject;
}
