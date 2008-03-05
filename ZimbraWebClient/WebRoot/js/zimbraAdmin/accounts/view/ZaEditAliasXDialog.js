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
ZaEditAliasXDialog = function(parent,  app, w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent, app, null, title, w, h);
	this._containedObject = {};
	this.initForm(ZaAlias.myXModel,this.getMyXForm());
}

ZaEditAliasXDialog.prototype = new ZaXDialog;
ZaEditAliasXDialog.prototype.constructor = ZaEditAliasXDialog;

ZaEditAliasXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:1,
		items:[
			{ref:ZaAccount.A_name, type:_EMAILADDR_, label:null}
		]
	};
	return xFormObject;
}


/**
* @class ZaNewAliasXDialog
* @contructor ZaNewAliasXDialog
* @author Charles Cao
* @param parent
* param app
**/
ZaNewAliasXDialog = function(parent,  app, w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent, app, null, title, w, h);
	this._containedObject = {};
	this.initForm(ZaAlias.myXModel,this.getMyXForm());
    this._helpURL = ZaNewAliasXDialog.helpURL;
}

ZaNewAliasXDialog.prototype = new ZaXDialog;
ZaNewAliasXDialog.prototype.constructor = ZaNewAliasXDialog;
ZaNewAliasXDialog.helpURL = location.pathname + "adminhelp/html/WebHelp/managing_accounts/creating_a_mail_aliases.htm";

ZaNewAliasXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:1,
		items:[
			{ref:ZaAccount.A_name, type:_EMAILADDR_, label:ZaMsg.Alias_Dlg_label_alias},
			{ref:ZaAlias.A_targetAccount, type:_EMAILADDR_, label:ZaMsg.Alias_Dlg_label_target_acct}
		]
	};
	return xFormObject;
}
