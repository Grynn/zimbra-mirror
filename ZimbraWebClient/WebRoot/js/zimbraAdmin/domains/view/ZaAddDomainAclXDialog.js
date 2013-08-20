/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
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
* @class ZaAddDomainAclXDialog
* @contructor ZaAddDomainAclXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
ZaAddDomainAclXDialog = function(parent, w, h) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent,null, ZaMsg.Add_perms_title, w, h);
	this._containedObject = {acl:{r:0,w:0,i:0,d:0,a:0,x:0},name:"",gt:""};
	this.initForm(ZaDomain.aclXModel,this.getMyXForm());
}

ZaAddDomainAclXDialog.prototype = new ZaXDialog;
ZaAddDomainAclXDialog.prototype.constructor = ZaAddDomainAclXDialog;

ZaAddDomainAclXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:2,
		items:[
			{type:_OSELECT1_, ref:"gt", choices:[{value:ZaDomain.A_NotebookGroupACLs, label:ZaMsg.ACL_Grp},
				{value:ZaDomain.A_NotebookUserACLs,label:ZaMsg.ACL_User},
				{value:ZaDomain.A_NotebookDomainACLs,label:ZaMsg.ACL_Dom}
			],visibilityChecks:[],enableDisableChecks:[] },
			{type:_SWITCH_, items:[
				{type:_CASE_, 
					visibilityChecks:[[XForm.checkInstanceValue,"gt",ZaDomain.A_NotebookGroupACLs]],
					visibilityChangeEventSources:["gt"],
					items:[
						{ref:".", type:_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							toolTipContent:ZaMsg.tt_StartTypingDLName,
							forceUpdate:true,dataFetcherMethod:ZaSearch.prototype.dynSelectSearchGroups
						}						
					]
				},
				{type:_CASE_, 
					visibilityChecks:[[XForm.checkInstanceValue,"gt",ZaDomain.A_NotebookUserACLs]],
					visibilityChangeEventSources:["gt"],
					items:[
						{ref:".", type:_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							toolTipContent:ZaMsg.tt_StartTypingAccountName,
							forceUpdate:true,dataFetcherMethod:ZaSearch.prototype.dynSelectSearch,
							dataFetcherTypes:[ZaSearch.ACCOUNTS],
							dataFetcherAttrs:[ZaItem.A_zimbraId, ZaItem.A_cn, ZaAccount.A_name, ZaAccount.A_displayname, ZaAccount.A_mail]
						}						
					]
				},
				{type:_CASE_, 
					visibilityChecks:[[XForm.checkInstanceValue,"gt",ZaDomain.A_NotebookDomainACLs]],
					visibilityChangeEventSources:["gt"],
					items:[
						{ref:".", type:_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							toolTipContent:ZaMsg.tt_StartTypingDomainName,
							forceUpdate:true,dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains
						}
					]
				}		
			]}
		]		
	}
	return xFormObject;
}
