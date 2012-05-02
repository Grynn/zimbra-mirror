/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ReindexMailboxXDialog
* @contructor ReindexMailboxXDialog
* @author Greg Solovyev
* @param parent
* param w (width)
* param h (height)
**/
ReindexMailboxXDialog = function(parent,   w, h) {
	if (arguments.length == 0) return;
	ZaXDialog.call(this, parent,  null, ZaMsg.Reindex_Title, null,null);
	this.initForm(ZaReindexMailbox.myXModel,this.getMyXForm());
	this._button[DwtDialog.OK_BUTTON].setToolTipContent(ZaMsg.Reindex_Mbx_tt);
	this._containedObject = new ZaReindexMailbox();
	this.pollAction = new AjxTimedAction(this, this.getReindexStatus);
	this._pollHandler = null;
	
	this._helpURL = ReindexMailboxXDialog.helpURL;		
}

ReindexMailboxXDialog.prototype = new ZaXDialog;
ReindexMailboxXDialog.prototype.constructor = ReindexMailboxXDialog;
ReindexMailboxXDialog.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/re-indexing_a_mailbox.htm?locid="+AjxEnv.DEFAULT_LOCALE;

ReindexMailboxXDialog.isStartEnabled = function () {
	return (this.getInstanceValue(ZaReindexMailbox.A_status) != "running" && this.getInstanceValue(ZaReindexMailbox.A_status) != "started")	
}

ReindexMailboxXDialog.isAbortEnabled = function () {
	return (this.getInstanceValue(ZaReindexMailbox.A_status) == "running" || this.getInstanceValue(ZaReindexMailbox.A_status) == "started")	
}

ReindexMailboxXDialog.isStatusNotError = function () {
	return (this.getInstanceValue(ZaReindexMailbox.A_status) != "error");
}
ReindexMailboxXDialog.prototype.popup = 
function () {
	DwtDialog.prototype.popup.call(this);
	//get status
	this._localXForm.setInstance(this._containedObject);
	if(this._containedObject.mbxId) {
		var callback = new AjxCallback(this, ReindexMailboxXDialog.prototype.getReindexStatusCallBack);
		ZaAccount.getReindexStatus(this._containedObject.mbxId,callback);
		//ZaAccount.parseReindexResponse(ZaAccount.getReindexStatus(this._containedObject.mbxId),this._containedObject);
	}
		
	
	//this._localXForm.refresh();
/*	if(this._containedObject.status == "running" || this._containedObject.status == "started") {
		// schedule next poll
		this._pollHandler = AjxTimedAction.scheduleAction(this.pollAction, this._containedObject.pollInterval);		
	} else if(this._pollHandler) {
		//stop polling
		AjxTimedAction.cancelAction(this._pollHandler);
	}
	*/
}

ReindexMailboxXDialog.prototype.popdown = 
function () {
	if(this._pollHandler) {
		//stop polling
		AjxTimedAction.cancelAction(this._pollHandler);
	}
	DwtDialog.prototype.popdown.call(this);
}

ReindexMailboxXDialog.abortReindexMailbox = 
function(evt) {
	try {
		var instance = this.getInstance();
		//abort outstanding status requests
		if(this.getForm().parent.asynCommand)
			this.getForm().parent.asynCommand.cancel();
			
		var callback = new AjxCallback(this.getForm().parent, ReindexMailboxXDialog.prototype.getReindexStatusCallBack);			
		ZaAccount.abortReindexMailbox(instance.mbxId,callback);
		this.getForm().refresh();
	} catch (ex) {
		this.getForm().getController().getCurrentController()._handleException(ex, "ReindexMailboxXDialog.abortReindexMailbox", null, false);
	}
}

ReindexMailboxXDialog.startReindexMailbox = 
function(evt) {
	try {
		var instance = this.getInstance();
		var callback = new AjxCallback(this.getForm().parent, ReindexMailboxXDialog.prototype.getReindexStatusCallBack);		
		ZaAccount.startReindexMailbox(instance.mbxId,callback);
		this.getForm().refresh();	
	} catch (ex) {
		this.getForm().getController().getCurrentController()._handleException(ex, "ReindexMailboxXDialog.startReindexMailbox", null, false);	
	}
}

ReindexMailboxXDialog.prototype.getReindexStatusCallBack = 
function (resp) {
	ZaAccount.parseReindexResponse(resp,this._containedObject,this._localXForm);
	if((this._containedObject.status == "running" || this._containedObject.status == "started") && this.isPoppedUp()) {
		// schedule next poll
		this._pollHandler = AjxTimedAction.scheduleAction(this.pollAction, this._containedObject.pollInterval);		
	} else if(this._pollHandler) {
		//stop polling
		AjxTimedAction.cancelAction(this._pollHandler);
		this._pollHandler = null;		
	}
	
	//this._localXForm.setInstance(this._containedObject);
	//this._localXForm.refresh();	
}

ReindexMailboxXDialog.prototype.getReindexStatus = 
function () {
	var callback = new AjxCallback(this, this.getReindexStatusCallBack);
	this.asynCommand = ZaAccount.getReindexStatus(this._containedObject.mbxId, callback);
	
}

ReindexMailboxXDialog.prototype.getMyXForm = 
function() {	
	var xFormObject = {
		numCols:2, height:"300px",align:_CENTER_,cssStyle:"text-align:center",
		items:[
			{ type: _DWT_ALERT_,
			  style: DwtAlert.WARNING,
			  iconVisible: true, 
			  content: ZaMsg.WARNING_REINDEX,
			  colSpan:"*",
			  align:_CENTER_,
			  valign:_TOP_
			},
			{type: _DWT_ALERT_,
			 style: DwtAlert.CRITICAL,
			 iconVisible: true, 
			 content: null,
			 ref:ZaReindexMailbox.A_resultMsg,
			 visibilityChangeEventSources:[ZaReindexMailbox.A_status],
			 visibilityChecks:[[XForm.checkInstanceValue,ZaReindexMailbox.A_status,"error"],[XForm.checkInstanceValueNotEmty,ZaReindexMailbox.A_resultMsg]],
			 valueChangeEventSources:[ZaReindexMailbox.A_resultMsg],			  
		  	 align:_CENTER_,
		  	 colSpan:"*"
			},	
			{type:_TEXTAREA_,
				visibilityChangeEventSources:[ZaReindexMailbox.A_status],
				visibilityChecks:[[XForm.checkInstanceValue,ZaReindexMailbox.A_status,"error"],[XForm.checkInstanceValueNotEmty,ZaReindexMailbox.A_errorDetail]],
				valueChangeEventSources:[ZaReindexMailbox.A_errorDetail],
				ref:ZaReindexMailbox.A_errorDetail, 
				label:ZaMsg.FAILED_REINDEX_DETAILS,
				height:"100px", width:"200px",
				colSpan:"*"
			},
			{type:_DWT_ALERT_, 
				ref:ZaReindexMailbox.A_progressMsg,content: null,
				colSpan:"*",
 				iconVisible: true,
				align:_CENTER_,				
				style: DwtAlert.INFORMATION,bmolsnr:true
			},
			{type:_DWT_PROGRESS_BAR_, label:ZaMsg.ReindexMbx_Progress,
				maxValue:null,
				maxValueRef:ZaReindexMailbox.A_numTotal, 
				ref:ZaReindexMailbox.A_numDone,
				valign:_CENTER_,
				align:_CENTER_,	
				wholeCssClass:"progressbar",
				progressCssClass:"progressused",bmolsnr:true
			},		
			{type:_SPACER_,
				visibilityChecks:[ReindexMailboxXDialog.isStatusNotError],
				visibilityChangeEventSources:[ZaReindexMailbox.A_status], 
				height:"150px", width:"490px",colSpan:"*"
			},
			{type:_GROUP_, colSpan:"*", numCols:5, width:appNewUI?"490px":"460px",cssStyle:"text-align:center; overflow:hidden", align:_CENTER_, items: [
				{type:_SPACER_, width:"100px", colSpan:1},
				{type:_DWT_BUTTON_, 
					onActivate:"ReindexMailboxXDialog.startReindexMailbox.call(this)", label:ZaMsg.NAD_ACC_Start_Reindexing, 
					enableDisableChecks:[ReindexMailboxXDialog.isStartEnabled],
					enableDisableChangeEventSources:[ZaReindexMailbox.A_status],
					visibilityChecks:[],					
					valign:_BOTTOM_,width:"100px"
				},
				{type:_SPACER_, width:appNewUI?"90px":"60px", colSpan:1},
				{type:_DWT_BUTTON_, 
					onActivate:"ReindexMailboxXDialog.abortReindexMailbox.call(this)", label:ZaMsg.NAD_ACC_Abort_Reindexing, 
					enableDisableChecks:[ReindexMailboxXDialog.isAbortEnabled],
					enableDisableChangeEventSources:[ZaReindexMailbox.A_status],
					visibilityChecks:[],					
					valign:_BOTTOM_,width:"100px"				
				},
				{type:_SPACER_, width:"100px", colSpan:1}
			]}
		]		
	}
	return xFormObject;
}
