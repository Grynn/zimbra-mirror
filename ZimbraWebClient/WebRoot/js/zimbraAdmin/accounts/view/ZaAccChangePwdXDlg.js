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
* @class ZaAccChangePwdXDlg
* @contructor ZaAccChangePwdXDlg
* @author Greg Solovyev
* @param parent
* param app
**/
ZaAccChangePwdXDlg = function(parent,   w, h) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.CANCEL_BUTTON,DwtDialog.OK_BUTTON];
	ZaXDialog.call(this, parent, null, ZaMsg.CHNP_Title, w, h,"ZaAccChangePwdXDlg");
	this.initForm(ZaAccount.myXModel,this.getMyXForm());
	this._helpURL = [location.pathname, ZaUtil.HELP_URL, ZaAccChangePwdXDlg.helpURL, "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
    //this._helpURL = ZaAccChangePwdXDlg.helpURL;
}

ZaAccChangePwdXDlg.prototype = new ZaXDialog;
ZaAccChangePwdXDlg.prototype.constructor = ZaAccChangePwdXDlg;
ZaAccChangePwdXDlg.helpURL = "passwords/setting_passwords.htm";

ZaAccChangePwdXDlg.prototype.getPassword = 
function() {
	return this._localXForm.getInstance().attrs[ZaAccount.A_password];
}

ZaAccChangePwdXDlg.prototype.getConfirmPassword = 
function() {
	return this._localXForm.getInstance()[ZaAccount.A2_confirmPassword];
}

ZaAccChangePwdXDlg.prototype.getMustChangePassword = 
function() {
	return this._localXForm.getInstance().attrs[ZaAccount.A_zimbraPasswordMustChange];
}

ZaAccChangePwdXDlg.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:2,
		items:[
			{type:_GROUP_,isTabGroup:true,
			items:[
			{ref:ZaAccount.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,
				label:ZaMsg.NAD_Password, labelLocation:_LEFT_, 
				cssClass:"admin_xform_name_input",visibilityChecks:[],enableDisableChecks:[]
			},
			{ref:ZaAccount.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,
				label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_, 
				cssClass:"admin_xform_name_input",visibilityChecks:[],enableDisableChecks:[]
			},
			{ref:ZaAccount.A_zimbraPasswordMustChange,  type:_CHECKBOX_,  
				msgName:ZaMsg.NAD_MustChangePwd,label:ZaMsg.NAD_MustChangePwd,trueValue:"TRUE", falseValue:"FALSE"}
			]
		} ]
	}
	return xFormObject;
}
