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
 * The Original Code is: Zimbra Collaboration Suite.
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
* @class ReindexMailboxXDialog
* @contructor ReindexMailboxXDialog
* @author Greg Solovyev
* @param parent
* param w (width)
* param h (height)
**/
function ReindexMailboxXDialog(parent,  app, w, h) {
	if (arguments.length == 0) return;
	DwtDialog.call(this, parent, null, ZaMsg.Reindex_Title, [DwtDialog.OK_BUTTON]);
	this._button[DwtDialog.OK_BUTTON].setToolTipContent(ZaMsg.Reindex_Mbx_tt);
	this._app = app;
	this._localXForm = null;
	this._localXModel = null;
	this._drawn = false;
	this._containedObject = null;	

	if (!w) {
		this._contentW = "500px";
	} else {
		this._contentW = w;
	}
	
	if(!h) {
		this._contentH = "350px";
	} else {
		this._contentH = h;
	}		
	
	this._pageDiv = this.getDocument().createElement("div");
	this._pageDiv.className = "ZaXWizardDialogPageDiv";
	this._pageDiv.style.width = this._contentW;
	this._pageDiv.style.height = this._contentH;
	this._pageDiv.style.overflow = "auto";

	this._createContentHtml();
	this.initForm(ZaSearch.myXModel,this.getMyXForm());
	this._containedObject = new ZaReindexMailbox();
	this.pollAction = new AjxTimedAction();
	this.pollAction.obj = this;
	this.pollAction.method = this.getReindexStatus;
	this._pollHandler = null;
}

ReindexMailboxXDialog.prototype = new DwtDialog;
ReindexMailboxXDialog.prototype.constructor = ReindexMailboxXDialog;

/**
* public method _initForm
* @param xModelMetaData
* @param xFormMetaData
**/
ReindexMailboxXDialog.prototype.initForm = 
function (xModelMetaData, xFormMetaData) {
	if(xModelMetaData == null || xFormMetaData == null)
		throw new AjxException("Metadata for XForm and/or XModel are not defined", AjxException.INVALID_PARAM, "ZaXWizardDialog.prototype.initForm");
		
	this._localXModel = new XModel(xModelMetaData);
	this._localXForm = new XForm(xFormMetaData, this._localXModel, null, this);
	this._localXForm.setController(this._app.getCurrentController());
	this._localXForm.draw(this._pageDiv);	
	this._drawn = true;
}

ReindexMailboxXDialog.prototype.popup = 
function () {
	DwtDialog.prototype.popup.call(this);
	//get status
	
	if(this._containedObject.mbxId) {
		ZaAccount.parseReindexResponse(ZaAccount.getReindexStatus(this._containedObject.mbxId),this._containedObject);
	}
		
	this._localXForm.setInstance(this._containedObject);
	this._localXForm.refresh();
	if(this._containedObject.status == "running" || this._containedObject.status == "started") {
		// schedule next poll
		this._pollHandler = AjxTimedAction.scheduleAction(this.pollAction, this._containedObject.pollInterval);		
	} else if(this._pollHandler) {
		//stop polling
		AjxTimedAction.cancelAction(this._pollHandler);
	}
}

ReindexMailboxXDialog.prototype.popdown = 
function () {
	if(this._pollHandler) {
		//stop polling
		AjxTimedAction.cancelAction(this._pollHandler);
	}
	DwtDialog.prototype.popdown.call(this);
}

ReindexMailboxXDialog.prototype.getObject = 
function () {
	return this._containedObject;
}

/**
* @method setObject sets the object contained in the view
**/
ReindexMailboxXDialog.prototype.setObject =
function(entry) {
	this._containedObject = entry;
	this._localXForm.setInstance(this._containedObject);
}

ReindexMailboxXDialog.abortReindexMailbox = 
function(evt) {
	try {
		var instance = this.getInstance();
		//abort outstanding status requests
		if(this.getForm().parent.asynCommand)
			this.getForm().parent.asynCommand.cancel();
			
		ZaAccount.parseReindexResponse(ZaAccount.abortReindexMailbox(instance.mbxId),instance);

		if(instance.status == "running" || instance.status == "started") {
			// schedule next poll
			this.getForm().parent._pollHandler = AjxTimedAction.scheduleAction(this.getForm().parent.pollAction, instance.pollInterval);		
		} else if(this.getForm().parent._pollHandler) {
			//stop polling
			AjxTimedAction.cancelAction(this.getForm().parent._pollHandler);
			this.getForm().parent._pollHandler = null;
		}		
		this.getForm().refresh();
	} catch (ex) {
		this.getForm().getController()._handleException(ex, "ReindexMailboxXDialog.abortReindexMailbox", null, false);
	}
}

ReindexMailboxXDialog.startReindexMailbox = 
function(evt) {
	try {
		var instance = this.getInstance();
		ZaAccount.parseReindexResponse(ZaAccount.startReindexMailbox(instance.mbxId),instance);
		this.getForm().refresh();
	
		if(instance.status == "running" || instance.status == "started") {
			// schedule next poll
			this.getForm().parent._pollHandler = AjxTimedAction.scheduleAction(this.getForm().parent.pollAction, instance.pollInterval);		
		} else if(this.getForm().parent._pollHandler) {
			//stop polling
			AjxTimedAction.cancelAction(this.getForm().parent._pollHandler);
			this.getForm().parent._pollHandler = null;
		}
		//this.getForm().parent._pollHandler = AjxTimedAction.scheduleAction(this.getForm().parent.pollAction, instance.pollInterval);	
	} catch (ex) {
		this.getForm().getController()._handleException(ex, "ReindexMailboxXDialog.startReindexMailbox", null, false);	
	}
}

ReindexMailboxXDialog.prototype.getReindexStatusCallBack = 
function (resp) {
	ZaAccount.parseReindexResponse(resp,this._containedObject);
	if((this._containedObject.status == "running" || this._containedObject.status == "started") && this.isPoppedUp()) {
		// schedule next poll
		this._pollHandler = AjxTimedAction.scheduleAction(this.pollAction, this._containedObject.pollInterval);		
	} else if(this._pollHandler) {
		//stop polling
		AjxTimedAction.cancelAction(this._pollHandler);
		this._pollHandler = null;		
	}
	
	this._localXForm.setInstance(this._containedObject);
	this._localXForm.refresh();	
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
			{ type: _DWT_ALERT_,
			  style: DwtAlert.CRITICAL,
			  iconVisible: true, 
			  content: null,
			  ref:"resultMsg",
			  relevant:"instance.status == 'error'",
			  relevantBehavior:_HIDE_,
			  align:_CENTER_,
			  colSpan:"*"
			},	
			{type:_TEXTAREA_,
				relevant:"instance.status == 'error'", 
				ref:"errorDetail", 
				label:ZaMsg.FAILED_REINDEX_DETAILS,
				relevantBehavior:_HIDE_,
				height:"100px", width:"200px",
				colSpan:"*"
			},
			{type:_DWT_ALERT_, ref:"progressMsg",content: null,
				//relevant:"instance.status == 'running' || instance.status == 'started'",
				colSpan:"*",
				relevantBehavior:_HIDE_,
 				iconVisible: true,
				align:_CENTER_,				
				style: DwtAlert.INFORMATION
			},
			{type:_DWT_PROGRESS_BAR_, label:ZaMsg.ReindexMbx_Progress,
				maxValue:null,
				maxValueRef:"numTotal", 
				ref:"numDone",
				//relevant:"instance.status == 'running' || instance.status == 'started'",
				relevantBehavior:_HIDE_,
				valign:_CENTER_,
				align:_CENTER_,	
				wholeCssClass:"progressbar",
				progressCssClass:"progressused"
			},		
			{type:_SPACER_, 
				relevant:"instance.status != 'error'", 
				height:"150px", width:"500px",colSpan:"*"
			},			
			{type:_GROUP_, colSpan:"*", numCols:5, width:"490px",cssStyle:"text-align:center", align:_CENTER_, items: [	
				{type:_SPACER_, width:"100px", colSpan:1},
				{type:_DWT_BUTTON_, 
					onActivate:"ReindexMailboxXDialog.startReindexMailbox.call(this)", label:ZaMsg.NAD_ACC_Start_Reindexing, relevantBehavior:_DISABLE_, relevant:"instance.status != 'running' && instance.status != 'started'",
					valign:_BOTTOM_,width:"100px"
				},
				{type:_SPACER_, width:"90px", colSpan:1},
				{type:_DWT_BUTTON_, 
					onActivate:"ReindexMailboxXDialog.abortReindexMailbox.call(this)", label:ZaMsg.NAD_ACC_Abort_Reindexing, relevantBehavior:_DISABLE_, relevant:"instance.status == 'running' || instance.status == 'started'",
					valign:_BOTTOM_,width:"100px"				
				},
				{type:_SPACER_, width:"100px", colSpan:1}
			]}
		]		
	}
	return xFormObject;
}

ReindexMailboxXDialog.prototype._startPolling = 
function () {

}

ReindexMailboxXDialog.prototype._stopPolling = 
function () {

}

ReindexMailboxXDialog.prototype._createContentHtml =
function () {

	this._table = this.getDocument().createElement("table");
	this._table.border = 0;
	this._table.width=this._contentW;
	this._table.cellPadding = 0;
	this._table.cellSpacing = 0;
	Dwt.associateElementWithObject(this._table, this);
	this._table.backgroundColor = DwtCssStyle.getProperty(this.parent.getHtmlElement(), "background-color");
	
	var row2; //page
	var col2;
	row2 = this._table.insertRow(0);
	row2.align = "left";
	row2.vAlign = "middle";
	
	col2 = row2.insertCell(row2.cells.length);
	col2.align = "left";
	col2.vAlign = "middle";
	col2.noWrap = true;	
	col2.width = this._contentW;
	col2.appendChild(this._pageDiv);

	this._contentDiv.appendChild(this._table);
}

/**
* Override _addChild method. We need internal control over layout of the children in this class.
* Child elements are added to this control in the _createHTML method.
* @param child
**/
ReindexMailboxXDialog.prototype._addChild =
function(child) {
	this._children.add(child);
}
