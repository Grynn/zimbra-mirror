/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaZimletDeployXWizard
* @contructor ZaZimletDeployXWizard
* @param ZaApp parent
* @param ZaApp app
* this is the wizard dialog for deployig a zimlet or an admin extension
* @author Greg Solovyev
**/
ZaZimletDeployXWizard = function(parent) {
	ZaXWizardDialog.call(this, parent,null, ZaMsg.ZMLT_DeployZimletWizardTitle, "550px", "300px","ZaZimletDeployXWizard", null, ZaId.DLG_ZIM_DEPLOY);
	this._app = ZaApp.getInstance();
	this.stepChoices = [
		{label:ZaMsg.ZMLT_UploadFileStep_Title, value:1},
		{label:ZaMsg.ZMLT_Deploying_Title, value:2}
	];	
	this.currentPageNum = 0;
	this.initForm(ZaZimlet.myXModel,this.getMyXForm());	
	this._helpURL = ZaZimletDeployXWizard.helpURL;	
	this.pollInterval = 500;
	this.pollAction = new AjxTimedAction(this, this.getDeploymentStatus);
	this._pollHandler = null;		
}
ZaZimletDeployXWizard.helpURL = location.pathname + ZaUtil.HELP_URL + "zimlets/setting_up_zimlets_in_zcs.htm?locid="+AjxEnv.DEFAULT_LOCALE;
ZaZimletDeployXWizard.prototype = new ZaXWizardDialog;
ZaZimletDeployXWizard.prototype.constructor = ZaZimletDeployXWizard;
ZaZimletDeployXWizard.prototype.miniType = 2;
ZaXDialog.XFormModifiers["ZaZimletDeployXWizard"] = new Array();
ZaZimletDeployXWizard.ZimletUploadFormId = null;
ZaZimletDeployXWizard.ZimletUploadAttachmentInputId = null;	

ZaZimletDeployXWizard.prototype.getUploadFormHtml =
function (){
	ZaZimletDeployXWizard.ZimletUploadFormId = Dwt.getNextId();
	ZaZimletDeployXWizard.ZimletUploadAttachmentInputId = Dwt.getNextId();	
	//var uri = location.protocol + "//" + document.domain + appContextPath 
	//							+ "/../service/upload";
	var uri = appContextPath + "/../service/upload";
	DBG.println("upload uri = " + uri);
	var html = new Array();
	var idx = 0;
	html[idx++] = "<div style='overflow:hidden'><form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaZimletDeployXWizard.ZimletUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'><input id='";
	html[idx++] = ZaZimletDeployXWizard.ZimletUploadAttachmentInputId;
	html[idx++] = "' type=file  name='zimletFile' size='50' onChange=\"ZaZimletDeployXWizard.changeDeployBtnState(this,event||window.event,'" +this.getHTMLElId() +"')\"></input>";
	html[idx++] = "</form></div>";
	return html.join("");
}

ZaZimletDeployXWizard.changeDeployBtnState = function (obj, ev, DwtObjId) {
	var wiz = DwtControl.ALL_BY_ID[DwtObjId];
	if(wiz) {
		if(obj.value) {
			wiz.getButton(DwtWizardDialog.NEXT_BUTTON).setEnabled(true);
		} else {
			wiz.getButton(DwtWizardDialog.NEXT_BUTTON).setEnabled(false);
		}
	}
}

ZaZimletDeployXWizard.prototype.handleXFormChange =
function () {
    if(this._containedObject[ZaModel.currentStep]==2){
        this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
    }
}
/**
* @method setObject sets the object contained in the view
* @param entry - ZaRestore object to display
**/
ZaZimletDeployXWizard.prototype.setObject =
function(entry) {
	this._containedObject = entry;
	this._containedObject[ZaModel.currentStep] = entry[ZaModel.currentStep] || 1;
	this._containedObject[ZaZimlet.A_attachmentId] = entry[ZaZimlet.A_attachmentId] || null;
	this._containedObject[ZaZimlet.A_deployStatus] = entry[ZaZimlet.A_deployStatus] || null;
	this._containedObject[ZaZimlet.A_statusMsg] = entry[ZaZimlet.A_statusMsg] || null;
	this._localXForm.setInstance(this._containedObject);		
}
/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaZimletDeployXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
	this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.ZMLT_DeployZimlet);		
}

ZaZimletDeployXWizard.prototype.getUploadFrameId =
function() {
	if (!this._uploadManagerIframeId) {
		var iframeId = Dwt.getNextId();
		var html = [ "<iframe name='", iframeId, "' id='", iframeId,
			     "' src='", (AjxEnv.isIE && location.protocol == "https:") ? appContextPath+"/public/blank.html" : "javascript:\"\"",
			     "' style='position: absolute; top: 0; left: 0; visibility: hidden'></iframe>" ];
		var div = document.createElement("div");
		div.innerHTML = html.join("");
		document.body.appendChild(div.firstChild);
		this._uploadManagerIframeId = iframeId;
	}
	return this._uploadManagerIframeId;
};

ZaZimletDeployXWizard.prototype.getUploadManager = 
function() { 
	return this._uploadManager;
};

/**
 * @params uploadManager is the AjxPost object
 */
ZaZimletDeployXWizard.prototype.setUploadManager = 
function(uploadManager) {
	this._uploadManager = uploadManager;
};

ZaZimletDeployXWizard.prototype.goNext = 
function() {
	var inputElement = document.getElementById(ZaZimletDeployXWizard.ZimletUploadAttachmentInputId);
	if(inputElement && inputElement.value) {
		this.setUploadManager(new AjxPost(this.getUploadFrameId()));
		var zimletUploadCallback = new AjxCallback(this, this.uploadCallback);
		var um = this.getUploadManager() ; 
		window._uploadManager = um;
		try {
			um.execute(zimletUploadCallback, document.getElementById (ZaZimletDeployXWizard.ZimletUploadFormId));
		} catch (ex) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ZMLT_zimletFileNameError) ;
		}
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);	
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		ZaXWizardDialog.prototype.goNext.call(this);
	} else {
		ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ZMLT_zimletFileNameError) ;
	}
}

ZaZimletDeployXWizard.prototype.goPrev = 
function() {
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);	
	var instance = this._localXForm.getInstance();
	instance[ZaZimlet.A_attachmentId] = null;
	instance[ZaZimlet.A_deployStatus] = null;
	instance[ZaZimlet.A_statusMsg] = null;	
	this._localXForm.setInstance(instance);
	ZaXWizardDialog.prototype.goPrev.call(this);
}

/**
* member of  ZaXWizardDialog
* closes the wizard dialog
**/
ZaZimletDeployXWizard.prototype.popdown = 
function () {
	if(this._pollHandler)
		AjxTimedAction.cancelAction(this._pollHandler);		
		
	ZaXWizardDialog.prototype.popdown.call(this);
}

ZaZimletDeployXWizard.prototype.uploadCallback = function (status, attId) {
	try {
		var instance = this._localXForm.getInstance();
		if ((status == AjxPost.SC_OK) && (attId != null)) {
			instance[ZaZimlet.A_attachmentId] = attId;
			instance[ZaZimlet.A_statusMsg] = ZaMsg.ZMLT_UploadZimletSuccessMsg;
			ZaZimlet.deploy( {action:ZaZimlet.ACTION_DEPLOY_ALL,attId:attId,flushCache:instance[ZaZimlet.A_flushCache]},new AjxCallback(this, this.deployZimletClbck));			
		} else {
			// handle errors during attachment upload.
			instance[ZaZimlet.A_deployStatus] = ZaZimlet.STATUS_FAILED;
			instance[ZaZimlet.A_statusMsg] = AjxMessageFormat.format(ZaMsg.ZMLT_UploadZimletErrorMsg, status);
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaZimletDeployXWizard.uploadCallback");	
	}	
	this._localXForm.setInstance(instance);	
}

ZaZimletDeployXWizard.prototype.getDeploymentStatus = function () {
	try {
		var instance = this._localXForm.getInstance();
		ZaZimlet.deploy({action:ZaZimlet.ACTION_DEPLOY_STATUS,attId:instance[ZaZimlet.A_attachmentId]},new AjxCallback(this, this.deployZimletClbck));					
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaZimletDeployXWizard.getDeploymentStatus");	
	}	

}

ZaZimletDeployXWizard.prototype.deployZimletClbck = function (resp) {
	var instance = this._localXForm.getInstance();
	try {
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaZimletDeployXWizard.deployZimletClbck"));
		}
		if(resp.isException()) {
			throw(resp.getException());
		} else {
			var done = true;
			var hasErrors = false;
			var response = resp.getResponse().Body.DeployZimletResponse;
			var list = new Array();	
			var msgLines = [];
			var progress = response[ZaZimlet.A_progress];
			if(progress) {
				if(!(progress instanceof Array))
					progress = [progress];
				
				var cnt = progress.length;
				for(var i = 0; i < cnt; i++) {
					if(progress[i].status == ZaZimlet.STATUS_PENDING) {
						done = false;
						msgLines.push(AjxMessageFormat.format(ZaMsg.ZMLT_DeployProgres,[progress[i].server,ZaMsg.ZMLT_StatusPending]))						
					} else if (progress[i].status == ZaZimlet.STATUS_FAILED) {
						hasErrors = true;
						msgLines.push(AjxMessageFormat.format(ZaMsg.ZMLT_DeployProgresFail, [progress[i].server,progress[i].error]))						
					} else if (progress[i].status == ZaZimlet.STATUS_SUCCEEDED) {
						msgLines.push(AjxMessageFormat.format(ZaMsg.ZMLT_DeployProgres, [progress[i].server,ZaMsg.ZMLT_StatusSuccess]))						
					}
				}

				instance[ZaZimlet.A_progress] = msgLines.join("<br/>");

				if(hasErrors) {
					instance[ZaZimlet.A_deployStatus]=ZaZimlet.STATUS_FAILED;
					instance[ZaZimlet.A_statusMsg] = ZaMsg.ZMLT_failedDeployZimlet;
				} else if(done && !hasErrors) {
					instance[ZaZimlet.A_deployStatus]=ZaZimlet.STATUS_SUCCEEDED;
					instance[ZaZimlet.A_statusMsg] = ZaMsg.ZMLT_DeployZimletComplete;	
				} 
				if(!done) {
					//schedule another request
					this._pollHandler = AjxTimedAction.scheduleAction(this.pollAction, this.pollInterval);		
				} else {
					AjxTimedAction.cancelAction(this._pollHandler);
					ZaApp.getInstance().getCurrentController().fireCreationEvent(new ZaZimlet());
				}
			}
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaZimletDeployXWizard.deployZimletClbck");	
	}	
	this._localXForm.setInstance(instance);	
}

ZaZimletDeployXWizard.progressIsNotFailed = function () {
	return (this.getInstanceValue(ZaZimlet.A_deployStatus) != ZaZimlet.STATUS_FAILED);	
}

ZaZimletDeployXWizard.myXFormModifier = function(xFormObject) {
	var case1 = {
		type:_CASE_, numCols:2,caseKey:1,align:_LEFT_,valign:_TOP_,width:"520px",cssStyle:"overflow:hidden",
		items: [
			{ type:_OUTPUT_, value: ZaMsg.ZMLT_uploadTitle, align: _LEFT_,colSpan:2},
			{ type:_OUTPUT_, value: this.getUploadFormHtml() ,colSpan:2,width:"480px"},
			{type:_SPACER_, colSpan:2},
			{ ref:ZaZimlet.A_flushCache, type:_CHECKBOX_,align:_RIGHT_,subLabel:"",label:ZaMsg.ZMLT_flushCache,labelLocation:_RIGHT_,visibilityChecks:[], enableDisableChecks:[]},
			{type:_SPACER_, colSpan:2}
		]
	};
	var case2 = {
		type:_CASE_, numCols:1,caseKey:2, width:"400px",		
		items:[
			{type:_GROUP_, numCols:1,  
				items: [
					{ type: _DWT_ALERT_,style: DwtAlert.CRITICAL,
					  iconVisible: true, 
					  content: null,width:"400px",
					  ref:ZaZimlet.A_statusMsg,
					  visibilityChecks:[[XForm.checkInstanceValue,ZaZimlet.A_deployStatus,ZaZimlet.STATUS_FAILED]],
					  visibilityChangeEventSources:[ZaZimlet.A_deployStatus],
					  align:_CENTER_
					},						
					{type:_DWT_ALERT_,style: DwtAlert.INFORMATION, 
		 				iconVisible: true,
						content: null,width:"400px",
						ref:ZaZimlet.A_statusMsg,
						visibilityChecks:[ZaZimletDeployXWizard.progressIsNotFailed],
						visibilityChangeEventSources:[ZaZimlet.A_deployStatus],					
						align:_CENTER_
					},						
					{type:_DWT_ALERT_,style: DwtAlert.INFORMATION, 
		 				iconVisible: true,
						content: null,width:"400px",
						ref:ZaZimlet.A_progress,
						visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaZimlet.A_progress]],
						visibilityChangeEventSources:[ZaZimlet.A_progress],
						align:_CENTER_
					}					
				]
			}
		]		
	};
	xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:450, align:_LEFT_, valign:_TOP_, items:[case1,case2]}
		];	
}
ZaXDialog.XFormModifiers["ZaZimletDeployXWizard"].push(ZaZimletDeployXWizard.myXFormModifier);
