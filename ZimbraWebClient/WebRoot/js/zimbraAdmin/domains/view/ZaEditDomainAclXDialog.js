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
* @class ZaEditDomainAclXDialog
* @contructor ZaEditDomainAclXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
ZaEditDomainAclXDialog = function(parent,w, h) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent,null, ZaMsg.Edit_perms_title, w, h);
	this._containedObject = {acl:{r:0,w:0,i:0,d:0,a:0,x:0},name:"",gt:""};
	this.initForm(ZaDomain.aclXModel,this.getMyXForm());
}

ZaEditDomainAclXDialog.prototype = new ZaXDialog;
ZaEditDomainAclXDialog.prototype.constructor = ZaEditDomainAclXDialog;

ZaEditDomainAclXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:2,
		items:[
			{type:_SWITCH_, items:[
				{type:_CASE_,
					visibilityChecks:[[XForm.checkInstanceValue,"gt",ZaDomain.A_NotebookGroupACLs]],
					visibilityChangeEventSources:["gt"],
				 	
					items:[
						{ref:".", type:_STATIC_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							forceUpdate:true,dataFetcherMethod:ZaSearch.prototype.dynSelectSearchGroups
						}						
					]
				},
				{type:_CASE_, 
					visibilityChecks:[[XForm.checkInstanceValue,"gt",ZaDomain.A_NotebookUserACLs]],
					visibilityChangeEventSources:["gt"],
					
					items:[
						{ref:".", type:_STATIC_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
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
						{ref:".", type:_STATIC_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							forceUpdate:true,dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains
						}					
					]
				},
				{type:_CASE_, 
					visibilityChecks:[[XForm.checkInstanceValue,"gt",ZaDomain.A_NotebookAllACLs]],
					visibilityChangeEventSources:["gt"],
					
					items:[
						{ref:"acl", type:_ACL_, label:ZaMsg.ACL_All,labelLocation:_LEFT_,
						visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false}}						
					]
				},								
				{type:_CASE_, 
					visibilityChecks:[[XForm.checkInstanceValue,"gt",ZaDomain.A_NotebookPublicACLs]],
					visibilityChangeEventSources:["gt"],					
					
					items:[
						{ref:"acl", type:_ACL_, visibleBoxes:{r:true,w:false,a:false,i:false,d:false,x:false},
						label:ZaMsg.ACL_Public,labelLocation:_LEFT_}						
					]
				}				
			]}
		]		
	}
	return xFormObject;
}
