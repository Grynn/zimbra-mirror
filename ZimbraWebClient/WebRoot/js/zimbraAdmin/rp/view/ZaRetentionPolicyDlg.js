/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2012 VMware, Inc.
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
* @class ZaRetentionPolicyDlg
* @contructor ZaRetentionPolicyDlg
* @author Dongwei Feng
* @param parent
* param app
**/
ZaRetentionPolicyDlg = function(parent, w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.CANCEL_BUTTON,DwtDialog.OK_BUTTON];
	ZaXDialog.call(this, parent, null, title, w, h,"ZaRetentionPolicyDlg");
	this.initForm(ZaRetentionPolicy.myXModel,this.getMyXForm());
    this.setTitle(title);
	//this._helpURL = [location.pathname, ZaUtil.HELP_URL, ZaAccChangePwdXDlg.helpURL, "?locid=", AjxEnv.DEFAULT_LOCALE].join("");
}

ZaRetentionPolicyDlg.prototype = new ZaXDialog;
ZaRetentionPolicyDlg.prototype.constructor = ZaRetentionPolicyDlg;
//ZaNewUCServiceXDlg.helpURL = "passwords/setting_passwords.htm";

ZaRetentionPolicyDlg.prototype.isVisible = function (value) {
    var type = this.getForm().getInstanceValue(ZaRetentionPolicy.A2_type);
    return type == value;
}

ZaRetentionPolicyDlg.prototype.getMyXForm =
function() {	
	var xFormObject = {
		numCols:2,
		items:[
			{type:_GROUP_,isTabGroup:true,numCols:2,colSizes: ["150px","auto"],
                items:[
                    {ref:ZaRetentionPolicy.A2_name, type:_INPUT_, msgName:ZaMsg.LBL_Policy_Name,
                        label:ZaMsg.LBL_Policy_Name, labelLocation:_LEFT_, cssClass:"admin_xform_number_input", width: "300px"
                    },
                    {ref:ZaRetentionPolicy.A2_lifetime, type:_LONG_LIFETIME_,
                        msgName:ZaMsg.LBL_Policy_Retention,label:ZaMsg.LBL_Policy_Retention, labelLocation:_LEFT_,
                        visibilityChecks:[[ZaRetentionPolicyDlg.prototype.isVisible, ZaRetentionPolicy.TYPE_KEEP]]
                    },
                    {ref:ZaRetentionPolicy.A2_lifetime, type:_LONG_LIFETIME_,
                        msgName:ZaMsg.LBL_Policy_Purge,label:ZaMsg.LBL_Policy_Purge, labelLocation:_LEFT_,
                        visibilityChecks:[[ZaRetentionPolicyDlg.prototype.isVisible, ZaRetentionPolicy.TYPE_PURGE]]
                    }
                ]
		    }
        ]
	}
	return xFormObject;
}
