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
* @class DeleteAcctsPgrsDlg
* @contructor DeleteAcctsPgrsDlg
* @author Greg Solovyev
* @param parent
* param app
**/


DeleteAcctsPgrsDlg = function(parent,  app, w, h) {
	if (arguments.length == 0) return;
	this._app = app;
	this._standardButtons = [DwtDialog.OK_BUTTON];
	var helpButton = new DwtDialog_ButtonDescriptor(ZaXWizardDialog.HELP_BUTTON, ZaMsg.TBB_Help, DwtDialog.ALIGN_LEFT, new AjxCallback(this, this._helpButtonListener));
	var abortButton = new DwtDialog_ButtonDescriptor(DeleteAcctsPgrsDlg.ABORT_BUTTON, ZaMsg.NAD_AbortDeleting, DwtDialog.ALIGN_RIGHT, new AjxCallback(this, this.abortDeletingAccounts));		
	this._extraButtons = [helpButton, abortButton];
	ZaXDialog.call(this, parent, app, null, ZaMsg.NAD_DeletingAccTitle, w, h);
	this._containedObject = [];
//	this._deletedAccounts = [];
	this._currentIndex = 0;
	this._currentAccount = null;
	this.initForm(DeleteAcctsPgrsDlg.myXModel,this.getMyXForm());
	this._pollHandler = null;	
	this._aborted = false;
    this._helpURL = DeleteAcctsPgrsDlg.helpURL ;

}

DeleteAcctsPgrsDlg._ERROR_MSG = "errorMsg";
DeleteAcctsPgrsDlg._STATUS = "status";
DeleteAcctsPgrsDlg._DELETED_ACCTS = "deletedAccounts";
DeleteAcctsPgrsDlg.helpURL = location.pathname + "adminhelp/html/WebHelp/managing_accounts/provisioning_accounts.htm";

DeleteAcctsPgrsDlg.myXModel = {
	getDeletedAccounts: function (model, instance) {
		return instance.deletedAccounts;
	},
	setDeletedAccounts: function (value, instance, parentValue, ref) {
		instance.deletedAccounts = value;
	},
	items: [
		{ref:DeleteAcctsPgrsDlg._STATUS, type:_STRING_},
		{ref:DeleteAcctsPgrsDlg._ERROR_MSG, type:_STRING_},
		{ref:DeleteAcctsPgrsDlg._DELETED_ACCTS, type:_LIST_,setter:"set", setterScope:_MODEL_, getter: "getDeletedAccounts", getterScope:_MODEL_}
	]
}
DeleteAcctsPgrsDlg.prototype = new ZaXDialog;
DeleteAcctsPgrsDlg.prototype.constructor = DeleteAcctsPgrsDlg;
DeleteAcctsPgrsDlg.ABORT_BUTTON = ++DwtDialog.LAST_BUTTON;

/**
* @method setObject sets the object contained in the view
**/
DeleteAcctsPgrsDlg.prototype.setObject =
function(entry) {
	this._containedObject = entry;
	var obj = new Object();
	obj[DeleteAcctsPgrsDlg._DELETED_ACCTS] = [];
	obj[DeleteAcctsPgrsDlg._STATUS] = "";
	obj[DeleteAcctsPgrsDlg._ERROR_MSG] = null;
	this._localXForm.setInstance(obj);
	this._button[DeleteAcctsPgrsDlg.ABORT_BUTTON].setEnabled(false);		
	this._button[DwtDialog.OK_BUTTON].setEnabled(true);	
}


DeleteAcctsPgrsDlg.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
}


DeleteAcctsPgrsDlg.prototype.abortDeletingAccounts = 
function(evt) {
	try {
		var acc = this._containedObject[this._currentIndex];
		//cancelling the command does not prevent the account from being deleted BUG: 5452
		/*if(acc && acc.deleteCommand)
			acc.deleteCommand.cancel();
			
		*/
		this._button[DeleteAcctsPgrsDlg.ABORT_BUTTON].setEnabled(false);
		var obj = this._localXForm.getInstance();
//		this._app.getAccountListController().fireRemovalEvent(obj[DeleteAcctsPgrsDlg._DELETED_ACCTS]);			
		AjxTimedAction.cancelAction(this._pollHandler);
		this._pollHandler = null;
		this._aborted = true;
		this._localXForm.getInstance().status = ZaMsg.NAD_DeletingCancelled;
		this._localXForm.refresh();
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex, "DeleteAcctsPgrsDlg.abortDeletingAccounts", null, false);
	}
}

DeleteAcctsPgrsDlg.prototype.startDeletingAccounts = 
function(evt) {
	try {
		this._aborted = false;
		this.pollAction = new AjxTimedAction(this, this.deleteOneAccount);		
		this._currentIndex=0;
		var obj = new Object();
		obj[DeleteAcctsPgrsDlg._STATUS] = ZaMsg.NAD_DeleteAccStatus + " " + this._containedObject[this._currentIndex][ZaAccount.A_name];
		obj[DeleteAcctsPgrsDlg._DELETED_ACCTS] = new Array();
		this._localXForm.setInstance(obj);
		this._pollHandler = AjxTimedAction.scheduleAction(this.pollAction, "50");		
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex, "DeleteAcctsPgrsDlg.startDeletingAccounts", null, false);	
	}
}

DeleteAcctsPgrsDlg.prototype.deleteOneAccountCallback = 
function (result) {
	var obj = this._localXForm.getInstance();
	var stopForError = false;
	var ex = result.getException();
	if(ex) {
		//ignore  ZmCsfeException.ACCT_NO_SUCH_ACCOUNT, we are removing them anyway
		if(ex.code != ZmCsfeException.ACCT_NO_SUCH_ACCOUNT)
			stopForError = true;
	}
	if(stopForError) {
		//stop deleting
	/*	var strBuffer = new AjxBuffer(ZaMsg.FAILED_DELETE_ACCOUNT, " ",this._containedObject[this._currentIndex][ZaAccount.A_name],"<br>",
			ZaMsg.ERROR_CODE, " ", result.getException().code, "<br>",
			ZaMsg.ERROR_MESSAGE, " ",result.getException().msg, "<br>",
			ZaMsg.ERROR_DETAILS, " ",result.getException().detail, "<br>");
		obj[DeleteAcctsPgrsDlg._ERROR_MSG] = strBuffer.toString();*/
		AjxTimedAction.cancelAction(this._pollHandler);	
		this._pollHandler = null;	
//		this._localXForm.setInstance(obj);	
		this._localXForm.refresh();	
		this._app.getCurrentController()._handleException(ex, "DeleteAcctsPgrsDlg.prototype.deleteOneAccountCallback", null, false);		
	} else {
		obj[DeleteAcctsPgrsDlg._DELETED_ACCTS].push(this._containedObject[this._currentIndex]);
		this._currentIndex++;
		if((this._currentIndex < this._containedObject.length) && !this._aborted) {
			obj.status = ZaMsg.NAD_DeleteAccStatus + " " + this._containedObject[this._currentIndex][ZaAccount.A_name];
			this._pollHandler = AjxTimedAction.scheduleAction(this.pollAction, "50");				
		} else {
			//done
			this._button[DeleteAcctsPgrsDlg.ABORT_BUTTON].setEnabled(false);			
			this._app.getCurrentController().fireRemovalEvent(obj[DeleteAcctsPgrsDlg._DELETED_ACCTS]);						
			AjxTimedAction.cancelAction(this._pollHandler);	
			this._pollHandler = null;
			if(!this._aborted) {
				obj.status = ZaMsg.NAD_FinishedDeletingAccounts;
			}
		}
//		this._localXForm.setInstance(obj);		
		this._localXForm.refresh();
	}
}

DeleteAcctsPgrsDlg.prototype.deleteOneAccount = 
function () {
	var callback = new AjxCallback(this, this.deleteOneAccountCallback);
	this._containedObject[this._currentIndex].remove(callback);
	this._button[DeleteAcctsPgrsDlg.ABORT_BUTTON].setEnabled(true);		
}


DeleteAcctsPgrsDlg.prototype.getMyXForm = 
function() {	
	var sourceHeaderList = new Array();
	var sortable = 1;
	sourceHeaderList[0] = new ZaListHeaderItem("type", ZaMsg.ALV_Type_col, null, 34, sortable++, "objectClass", true, true);
	sourceHeaderList[1] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_NameAddress_col, null, null, sortable++, ZaAccount.A_name, true, true);
	//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
//	sourceHeaderList[2] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, 100, sortable++,ZaAccount.A_displayname, true, true);

	var xFormObject = {
		numCols:2, colSizes:[140,140], height:"300px",width:"390px",align:_CENTER_,cssStyle:"text-align:center",
		items:[
			{ type: _DWT_ALERT_,
				  style: DwtAlert.INFORMATION,
				  iconVisible: true, 
				  content: null,
				  ref:DeleteAcctsPgrsDlg._STATUS,align:_CENTER_, valign:_MIDDLE_,colSpan:"*",width:"90%",
				  relevant:"instance[DeleteAcctsPgrsDlg._ERROR_MSG] == null",relevantBehavior:_HIDE_
			},
			{ type: _DWT_ALERT_,
				  style: DwtAlert.CRITICAL,
				  iconVisible: true, 
				  content: null,
				  ref:DeleteAcctsPgrsDlg._ERROR_MSG, align:_CENTER_, valign:_MIDDLE_,colSpan:"*", width:"90%",
				  relevant:"instance[DeleteAcctsPgrsDlg._ERROR_MSG] != null",relevantBehavior:_HIDE_				  
			},			
			{type:_SPACER_, height:"5"},	
			{type:_OUTPUT_,value:ZaMsg.NAD_DeletedAccounts,colSpan:"*", colSpan:"*",align:_LEFT_},
		   	{type:_SPACER_, height:"10"},
		   	{ref:DeleteAcctsPgrsDlg._DELETED_ACCTS, type:_DWT_LIST_, height:"180", width:"100%",colSpan:"*",  cssClass: "DLSource", 
				forceUpdate: true, widgetClass:ZaAccMiniListView, headerList:sourceHeaderList, hideHeader: false
			}			
		]		
	}
	return xFormObject;
}
