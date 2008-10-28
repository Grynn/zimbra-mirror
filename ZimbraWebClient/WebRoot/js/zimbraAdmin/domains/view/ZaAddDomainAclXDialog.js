/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
			] },
			{type:_SWITCH_, items:[
				{type:_CASE_, 
					visibilityChecks:[[XForm.checkInstanceValue,"gt",ZaDomain.A_NotebookGroupACLs]],
					visibilityChangeEventSources:["gt"],
					//relevant:"instance.gt==ZaDomain.A_NotebookGroupACLs",
					items:[
						{ref:".", type:_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							forceUpdate:true,dataFetcherMethod:ZaSearch.prototype.dynSelectSearchGroups
						}						
					]
				},
				{type:_CASE_, 
					visibilityChecks:[[XForm.checkInstanceValue,"gt",ZaDomain.A_NotebookUserACLs]],
					visibilityChangeEventSources:["gt"],
					//relevant:"instance.gt==ZaDomain.A_NotebookUserACLs",
					items:[
						{ref:".", type:_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							forceUpdate:true,dataFetcherMethod:ZaSearch.prototype.dynSelectSearchAccounts
						}						
					]
				},
				{type:_CASE_, 
					visibilityChecks:[[XForm.checkInstanceValue,"gt",ZaDomain.A_NotebookDomainACLs]],
					visibilityChangeEventSources:["gt"],
					//relevant:"instance.gt==ZaDomain.A_NotebookDomainACLs",
					items:[
						{ref:".", type:_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							forceUpdate:true,dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains
						}
					]
				}		
			]}
		]		
	}
	return xFormObject;
}
