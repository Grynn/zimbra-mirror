/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaEditAliasXDialog
* @contructor ZaEditAliasXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
ZaEditFwdAddrXDialog = function(parent,  w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent, null, title, w, h);
    this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/forwarding_mail.htm?locid="+AjxEnv.DEFAULT_LOCALE;
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
            {type:_GROUP_,isTabGroup:true, items: [ //allows tab key iteration
                {ref:ZaAccount.A_name, type:_TEXTFIELD_, label:ZaMsg.Enter_EmailAddr,width:230,visibilityChecks:[],enableDisableChecks:[]}
                ]
            }
        ]
	};
	return xFormObject;
}
