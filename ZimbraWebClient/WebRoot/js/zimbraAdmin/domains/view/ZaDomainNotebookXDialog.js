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
* @class ZaDomainNotebookXDialog
* @contructor ZaDomainNotebookXDialog
* @author Greg Solovyev
* @param parent
* param app
**/
function ZaDomainNotebookXDialog(parent,  app, w, h) {
	if (arguments.length == 0) return;
	this._app = app;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];
	var helpButton = new DwtDialog_ButtonDescriptor(ZaXWizardDialog.HELP_BUTTON, ZaMsg.TBB_Help, DwtDialog.ALIGN_LEFT, new AjxCallback(this, this._helpButtonListener));
	this._extraButtons = [helpButton];	
	ZaXDialog.call(this, parent, app, null, ZaMsg.CreateNotebook_Title, w, h);
	this.initForm(ZaDomain.myXModel,this.getMyXForm());
}

ZaDomainNotebookXDialog.prototype = new ZaXDialog;
ZaDomainNotebookXDialog.prototype.constructor = ZaDomainNotebookXDialog;

ZaDomainNotebookXDialog.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
}

ZaDomainNotebookXDialog.prototype.setObject = function (entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();

	for (var a in entry.attrs) {
		if(entry.attrs[a] instanceof Array) {
			this._containedObject.attrs[a] = new Array();
			var cnt = entry.attrs[a].length;
			for(var ix = 0; ix < cnt; ix++) {
				this._containedObject.attrs[a][ix]=entry.attrs[a][ix];
			}
		} else {
			this._containedObject.attrs[a] = entry.attrs[a];
		}
	}
	this._containedObject[ZaDomain.A_AuthUseBindPassword] = entry[ZaDomain.A_AuthUseBindPassword];
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];

	this._containedObject[ZaDomain.A_NotebookTemplateFolder]=entry[ZaDomain.A_NotebookTemplateFolder];
	this._containedObject[ZaDomain.A_NotebookTemplateDir]=entry[ZaDomain.A_NotebookTemplateDir];	

/*	this._containedObject[ZaDomain.A_NotebookAllACLs] = new Object();
	for (var a in entry[ZaDomain.A_NotebookAllACLs]) {
		this._containedObject[ZaDomain.A_NotebookAllACLs][a] = entry[ZaDomain.A_NotebookAllACLs][a];
	}
	
	this._containedObject[ZaDomain.A_NotebookPublicACLs] = new Object();
	for (var a in entry[ZaDomain.A_NotebookPublicACLs]) {
		this._containedObject[ZaDomain.A_NotebookPublicACLs][a] = entry[ZaDomain.A_NotebookPublicACLs][a];
	}

	this._containedObject[ZaDomain.A_NotebookDomainACLs] = new Object();
	for (var a in entry[ZaDomain.A_NotebookDomainACLs]) {
		this._containedObject[ZaDomain.A_NotebookDomainACLs][a] = entry[ZaDomain.A_NotebookDomainACLs][a];
	}*/

	this._containedObject.notebookAcls = {};

	if(entry.notebookAcls) {
		for(var gt in entry.notebookAcls) {
			this._containedObject.notebookAcls[gt] = {r:0,w:0,i:0,d:0,a:0,x:0};
			for (var a in entry.notebookAcls[gt]) {
				this._containedObject.notebookAcls[gt][a] = entry.notebookAcls[gt][a];
			}
		}
	}	
			
	if(!this._containedObject[ZaDomain.A_NotebookAccountName] && this._containedObject.attrs[ZaDomain.A_domainName])
		this._containedObject[ZaDomain.A_NotebookAccountName] = ZaDomain.DEF_WIKI_ACC + "@" + this._containedObject.attrs[ZaDomain.A_domainName];
	this._localXForm.setInstance(this._containedObject);
}

ZaDomainNotebookXDialog.prototype.closeMe = 
function() {
	this.popdown();	
}

ZaDomainNotebookXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:2,
		items:[
			{ref:ZaDomain.A_NotebookAccountName, type:_TEXTFIELD_, label:ZaMsg.Domain_NotebookAccountName, labelLocation:_LEFT_, },						
			{ref:ZaDomain.A_NotebookAccountPassword, type:_SECRET_, label:ZaMsg.Domain_NotebookAccountPassword, labelLocation:_LEFT_, },
			{ref:ZaDomain.A_NotebookAccountPassword2, type:_SECRET_, label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_, },												
			/*{ref:ZaDomain.A_OverwriteTemplates, type:_CHECKBOX_, label:ZaMsg.Domain_OverwriteTemplates, labelLocation:_LEFT_,
				trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},						
			{ref:ZaDomain.A_NotebookTemplateDir, type:_TEXTFIELD_, label:ZaMsg.Domain_NotebookTemplateDir, labelLocation:_LEFT_,
				relevant:"instance[ZaDomain.A_OverwriteTemplates] == 'TRUE'", relevantBehavior:_DISABLE_},
			{ref:ZaDomain.A_NotebookTemplateFolder, type:_TEXTFIELD_, label:ZaMsg.Domain_NotebookTemplateFolder, labelLocation:_LEFT_, 
				relevant:"instance[ZaDomain.A_OverwriteTemplates] == 'TRUE'", relevantBehavior:_DISABLE_},*/
			/*{ref:ZaDomain.A_OverwriteNotebookACLs, type:_CHECKBOX_, label:ZaMsg.Domain_OverwriteNotebookACLs, labelLocation:_LEFT_,
				trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},*/
			{ref:ZaDomain.A_NotebookDomainACLs, type:_ACL_, label:ZaMsg.ACL_Dom,labelLocation:_LEFT_,
				relevant:"instance[ZaDomain.A_CreateNotebook] == 'TRUE'", relevantBehavior:_DISABLE_},
			{type:_SPACER_, height:10},
			{ref:ZaDomain.A_NotebookAllACLs, type:_ACL_, label:ZaMsg.ACL_All,labelLocation:_LEFT_,
				relevant:"instance[ZaDomain.A_CreateNotebook] == 'TRUE'", relevantBehavior:_DISABLE_},
			{type:_SPACER_, height:10},
			{ref:ZaDomain.A_NotebookPublicACLs, type:_ACL_, label:ZaMsg.ACL_Public,labelLocation:_LEFT_,
				visibleBoxes:{r:true,w:false,a:false,i:false,d:false,x:false},
				relevant:"instance[ZaDomain.A_CreateNotebook] == 'TRUE'", relevantBehavior:_DISABLE_}

		]		
	}
	return xFormObject;
}
