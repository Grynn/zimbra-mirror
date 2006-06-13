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
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
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
	this._containedObject = entry;
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
			{ref:ZaDomain.A_NotebookTemplateDir, type:_TEXTFIELD_, label:ZaMsg.Domain_NotebookTemplateDir, labelLocation:_LEFT_},
			{ref:ZaDomain.A_NotebookTemplateFolder, type:_TEXTFIELD_, label:ZaMsg.Domain_NotebookTemplateFolder, labelLocation:_LEFT_},
			{ref:ZaDomain.A_NotebookAccountName, type:_EMAILADDR_, label:ZaMsg.Domain_NotebookAccountName, labelLocation:_LEFT_},						
			{ref:ZaDomain.A_NotebookAccountPassword, type:_SECRET_, label:ZaMsg.Domain_NotebookAccountPassword, labelLocation:_LEFT_},
			{ref:ZaDomain.A_NotebookAccountPassword2, type:_SECRET_, label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_}																											
		]		
	}
	return xFormObject;
}
