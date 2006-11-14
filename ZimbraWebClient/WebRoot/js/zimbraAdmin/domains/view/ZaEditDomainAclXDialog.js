/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaEditDomainAclXDialog
* @contructor ZaEditDomainAclXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
function ZaEditDomainAclXDialog(parent,  app, w, h) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];	
	ZaXDialog.call(this, parent, app, null, ZaMsg.Edit_perms_title, w, h);
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
				{type:_CASE_, relevant:"instance.gt==ZaDomain.A_NotebookGroupACLs",
					items:[
						{ref:".", type:_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							forceUpdate:true,dataFetcherMethod:ZaSearch.prototype.dynSelectSearchGroups
						}						
					]
				},
				{type:_CASE_, relevant:"instance.gt==ZaDomain.A_NotebookUserACLs",
					items:[
						{ref:".", type:_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							forceUpdate:true,dataFetcherMethod:ZaSearch.prototype.dynSelectSearchAccounts
						}						
					]
				},
				{type:_CASE_, relevant:"instance.gt==ZaDomain.A_NotebookDomainACLs",
					items:[
						{ref:".", type:_ADDR_ACL_, label:null, labelLocation:_NONE_,
							visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false},
							forceUpdate:true,dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains
						}					
					]
				},
				{type:_CASE_, relevant:"instance.gt==ZaDomain.A_NotebookAllACLs",
					items:[
						{ref:"acl", type:_ACL_, label:ZaMsg.ACL_All,labelLocation:_LEFT_,
						visibleBoxes:{r:true,w:true,a:false,i:true,d:true,x:false}}						
					]
				},								
				{type:_CASE_, relevant:"instance.gt==ZaDomain.A_NotebookPublicACLs",
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
