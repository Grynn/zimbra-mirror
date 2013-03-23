/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010 VMware, Inc.
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
* @class ZaEditFpXDialog
* @contructor ZaEditFpXDialog
* @author Charles Cao
* @param parent
* param app
**/
ZaEditFpXDialog = function(parent, w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];
	ZaXDialog.call(this, parent,null, title, w, h);
    this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_global_settings/making_free_busy_view__available_.htm?locid="+AjxEnv.DEFAULT_LOCALE ;
    //get the provider first
    ZaFp.getProviders();
    this._containedObject = {};
	this.initForm(ZaFp.getXModel(),this.getMyXForm());
}

ZaEditFpXDialog.prototype = new ZaXDialog;
ZaEditFpXDialog.prototype.constructor = ZaEditFpXDialog;

ZaEditFpXDialog.prototype.getMyXForm =
function() {
	var xFormObject = {
		numCols:1,
		items:[
            {type:_GROUP_,isTabGroup:true, items: [ //allows tab key iteration
                {ref:ZaFp.A_prefix, type:_OSELECT1_, choices: ZaFp.INTEROP_PROVIDER_CHOICES,
                    label:ZaMsg.Select_Interop_Provider, width:230,visibilityChecks:[],enableDisableChecks:[]} ,
                {ref:ZaFp.A_name, type:_TEXTFIELD_, label:ZaMsg.Enter_ForeignAccount,width:230,visibilityChecks:[],enableDisableChecks:[]}
		       ]
            }
        ]
    };
	return xFormObject;
}