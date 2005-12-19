/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaDLController controls display of a single Distribution list
 * @author EMC
 * Distribution list controller 
 */
function ZaDLController (appCtxt, container, app) {
	ZaXFormViewController.call(this, appCtxt, container, app, "ZaDLController");
	this._UICreated = false;
	this._toolbarOperations = new Array();
	this._helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/managing_accounts/provisioning_accounts.htm";	
	this.deleteMsg = ZaMsg.Q_DELETE_DL;
	this.objType = ZaEvent.S_DL;	
}

ZaDLController.prototype = new ZaXFormViewController();
ZaDLController.prototype.constructor = ZaDLController;

ZaController.initToolbarMethods["ZaDLController"] = new Array();

ZaDLController.prototype.toString = function () {
	return "ZaDLController";
};

ZaDLController.prototype.handleXFormChange = function (ev) {
	if(ev && ev.form.hasErrors()) { 
		this._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
	}
}
ZaDLController.prototype.show = function(entry) {
    if (!this._UICreated) {
		this._createUI();
	} 	
	try {
		this._app.pushView(ZaZimbraAdmin._DL_VIEW);
		if(!entry.id) {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(false);  			
		} else {
			this._toolbar.getButton(ZaOperation.DELETE).setEnabled(true);  				
			entry.getMembers(true);
		}	
		this._view.setDirty(false);
		entry[ZaModel.currentTab] = "1"
		this._view.setObject(entry);
		this._currentObject = entry;
	} catch (ex) {
		this._handleException(ex, "ZaDLController.prototype.show", null, false);
	}	
};

ZaDLController.initToolbarMethod =
function () {
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener)));
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener)));    	
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.SEP));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.ALTBB_New_tt, "Group", "GroupDis", new AjxListener(this, this.newButtonListener)));   			    	
   	this._toolbarOperations.push(new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.ALTBB_Delete_tt,"Delete", "DeleteDis", new AjxListener(this, this.deleteButtonListener)));    	    	
}
ZaController.initToolbarMethods["ZaDLController"].push(ZaDLController.initToolbarMethod);

//private and protected methods
ZaDLController.prototype._createUI = 
function () {
	//create accounts list view
	// create the menu operations/listeners first	
	this._view = new ZaDLXFormView(this._container, this._app);

    this._initToolbar();
	//always add Help button at the end of the toolbar    
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));		

	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations);    
		
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
	this._app.createView(ZaZimbraAdmin._DL_VIEW, elements);

	this._removeConfirmMessageDialog = new ZaMsgDialog(this._app.getAppCtxt().getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);			
	this._UICreated = true;
}

ZaDLController.prototype._saveChanges = function () {
	try { 
		var dl = this._view.getObject();
		if (dl.id){
			dl.saveEdits();
		} else {
			dl.saveNew();
		}
		return true;		
	} catch (ex) {
		var handled = false;
		if (ex.code == ZmCsfeException.SVC_FAILURE) {
			// TODO -- make this a ZaMsg, and grab the member name out of the exception message.
			//
			if (ex.msg.indexOf("add failed") != -1){
				var m = ex.msg.replace(/system failure: /, "");
				this.popupErrorDialog(m, ex, true);		
				handled = true;
			}
		} else if (ex.code == ZmCsfeException.DISTRIBUTION_LIST_EXISTS) {
			this.popupErrorDialog(AjxStringUtil.resolve(ZaMsg.DLXV_ErrorDistributionListExists,[dl.name]), ex, true);		
			handled = true;
		}
		if (!handled) {
			this._handleException(ex, "ZaDLController.prototype._saveChanges", null, false);
		}
		return false;
	}
};
