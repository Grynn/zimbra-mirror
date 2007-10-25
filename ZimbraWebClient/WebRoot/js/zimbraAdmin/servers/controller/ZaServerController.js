/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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
* @class ZaServerController controls display of a single Server
* @contructor ZaServerController
* @param appCtxt
* @param container
* @param abApp
* @author Greg Solovyev
**/

ZaServerController = function(appCtxt, container,app) {
	ZaXFormViewController.call(this, appCtxt, container,app,"ZaServerController");
	this._UICreated = false;
	this._helpURL = location.pathname + "adminhelp/html/WebHelp/managing_servers/managing_servers.htm";				
	this._toolbarOperations = new Array();
	this.deleteMsg = ZaMsg.Q_DELETE_SERVER;	
	this.objType = ZaEvent.S_SERVER;	
	this.tabConstructor = ZaServerXFormView ;
}

ZaServerController.prototype = new ZaXFormViewController();
ZaServerController.prototype.constructor = ZaServerController;

ZaController.initToolbarMethods["ZaServerController"] = new Array();
ZaController.setViewMethods["ZaServerController"] = new Array();
ZaXFormViewController.preSaveValidationMethods["ZaServerController"] = new Array();
/**
*	@method show
*	@param entry - isntance of ZaServer class
*/
ZaServerController.prototype.show = 
function(entry) {
	if (! this.selectExistingTabByItemId(entry.id)){
		this._setView(entry, true);
		//this.setDirty(false);
	}
}


ZaServerController.prototype.setEnabled = 
function(enable) {
	//this._view.setEnabled(enable);
}

/**
* Adds listener to modifications in the contained ZaServer 
* @param listener
**/
ZaServerController.prototype.addServerChangeListener = 
function(listener) {
	this._evtMgr.addListener(ZaEvent.E_MODIFY, listener);
}

/**
* Removes listener to modifications in the controlled ZaServer 
* @param listener
**/
ZaServerController.prototype.removeServerChangeListener = 
function(listener) {
	this._evtMgr.removeListener(ZaEvent.E_MODIFY, listener);    	
}


/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaServerController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	if(this._view.isDirty()) {
		//parameters for the confirmation dialog's callback 
		var args = new Object();		
		args["params"] = params;
		args["obj"] = nextViewCtrlr;
		args["func"] = func;
		//ask if the user wants to save changes			
		this._app.dialogs["confirmMessageDialog"] = this._app.dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);					
		this._app.dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_SAVE_CHANGES, DwtMessageDialog.INFO_STYLE);
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, this.validateChanges, this, args);		
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.NO_BUTTON, this.discardAndGoAway, this, args);		
		this._app.dialogs["confirmMessageDialog"].popup();
	} else {
		ZaController.prototype.switchToNextView.call(this, nextViewCtrlr, func, params);
	}
}



/**
* @method initToolbarMethod
* This method creates ZaOperation objects 
* All the ZaOperation objects are added to this._toolbarOperations array which is then used to 
* create the toolbar for this view.
* Each ZaOperation object defines one toolbar button.
* Help button is always the last button in the toolbar
**/
ZaServerController.initToolbarMethod = 
function () {
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.SERTBB_Save_tt, "Save", "SaveDis", new AjxListener(this, this.saveButtonListener)));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.DOWNLOAD_SERVER_CONFIG, ZaMsg.TBB_DownloadConfig, ZaMsg.SERTBB_DownloadConfig_tt, "DownloadServerConfig", "DownloadServerConfig", new AjxListener(this, this.downloadConfigButtonListener)));	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.SERTBB_Close_tt, "Close", "CloseDis", new AjxListener(this, this.closeButtonListener)));    	
}
ZaController.initToolbarMethods["ZaServerController"].push(ZaServerController.initToolbarMethod);

/**
*	@method setViewMethod 
*	@param entry - isntance of ZaDomain class
*/
ZaServerController.setViewMethod =
function(entry) {
	entry.load();
	if(!this._UICreated) {
		this._createUI();
	} 
//	this._app.pushView(ZaZimbraAdmin._SERVER_VIEW);
	this._app.pushView(this.getContentViewId());
	this._view.setDirty(false);
	this._view.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
}
ZaController.setViewMethods["ZaServerController"].push(ZaServerController.setViewMethod);

/**
* @method _createUI
**/
ZaServerController.prototype._createUI =
function () {
	this._contentView = this._view = new this.tabConstructor(this._container, this._app);

	this._initToolbar();
	//always add Help button at the end of the toolbar
	this._toolbarOperations.push(new ZaOperation(ZaOperation.NONE));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener)));							
	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations);		
	
	var elements = new Object();
	elements[ZaAppViewMgr.C_APP_CONTENT] = this._view;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
    //this._app.createView(ZaZimbraAdmin._SERVER_VIEW, elements);
    var tabParams = {
		openInNewTab: true,
		tabId: this.getContentViewId()
	}
	this._app.createView(this.getContentViewId(), elements, tabParams) ;
	this._UICreated = true;
	this._app._controllers[this.getContentViewId ()] = this ;
}

ZaServerController.prototype._saveChanges =
function () {
	var obj = this._view.getObject();
	this._currentObject.modify(obj);
	this._view.setDirty(false);	
	this.fireChangeEvent(this._currentObject);
	return true;
}

ZaServerController.prototype.validateMTA =
function (params) {
	var obj = this._view.getObject();
	if((!obj.attrs[ZaServer.A_SmtpHostname] || obj.attrs[ZaServer.A_SmtpHostname] == "") && (this._currentObject.attrs[ZaServer.A_SmtpHostname] != null && this._currentObject.attrs[ZaServer.A_SmtpHostname] != "")) {
		if(this._app.dialogs["confirmMessageDialog"])
			this._app.dialogs["confirmMessageDialog"].popdown();
			
		this._app.dialogs["confirmMessageDialog"] = this._app.dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.CANCEL_BUTTON], this._app);	
		this._app.dialogs["confirmMessageDialog"].setMessage(AjxMessageFormat.format(ZaMsg.WARNING_RESETING_SMTP_HOST,[obj.cos.attrs[ZaServer.A_SmtpHostname],obj.cos.attrs[ZaServer.A_SmtpHostname]]),  DwtMessageDialog.WARNING_STYLE);
		var args;
		var callBack = ZaServerController.prototype.runValidationStack;
		if(!params || !params["func"]) {
			args = null;
		} else {
			args = params;		
		}
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, callBack, this, args);		
		this._app.dialogs["confirmMessageDialog"].popup();		
	} else {
		this.runValidationStack(params);
	}
}
ZaXFormViewController.preSaveValidationMethods["ZaServerController"].push(ZaServerController.prototype.validateMTA);

ZaServerController.prototype.validateVolumeChanges = 
function (params) {
	var obj = this._view.getObject();
	if(obj[ZaServer.A_RemovedVolumes] && obj[ZaServer.A_RemovedVolumes].length > 0 ) {
		if(this._app.dialogs["confirmMessageDialog"])
			this._app.dialogs["confirmMessageDialog"].popdown();
			
		this._app.dialogs["confirmMessageDialog"] = this._app.dialogs["confirmMessageDialog"] = new ZaMsgDialog(this._view.shell, null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], this._app);	
		this._app.dialogs["confirmMessageDialog"].setMessage(ZaMsg.Q_DELETE_VOLUMES,  DwtMessageDialog.WARNING_STYLE);
		var args;
		var callBack = ZaServerController.prototype.runValidationStack;
		if(!params || !params["func"]) {
			args = null;
		} else {
			args = params;		
		}
		this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.YES_BUTTON, callBack, this, args);		
		this._app.dialogs["confirmMessageDialog"].popup();		
	} else {
		this.runValidationStack(params);
	}
}
ZaXFormViewController.preSaveValidationMethods["ZaServerController"].push(ZaServerController.prototype.validateVolumeChanges);

ZaServerController.changeProxyPorts = function () {
	if(this._app.dialogs["confirmMessageDialog"]) {
		var obj = this._app.dialogs["confirmMessageDialog"].getObject();
		if(obj) {
			if(obj.selectedChoice == 0) {
				//change
				this._view.getObject().attrs[obj.fieldRef] = obj.defVal;
			} else if (obj.selectedChoice == 2) {
				//do not change and disable service
				this._view.getObject().attrs[ZaServer.A_zimbraMailProxyServiceEnabled] = false;
			}
		}
	}
	ZaServerController.prototype.runValidationStack.call(this);
}
ZaServerController.prototype.validateImapBindPort =
function (params) {
	var obj = this._view.getObject();
 	var tmpObj = {selectedChoice:0, choice1Label:"",choice2Label:"",choice3Label:"",warningMsg:"",fieldRef:""};

	if( (obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] != this._currentObject.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] && obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] == true)	
	) {
		if ((obj.attrs[ZaServer.A_zimbraImapBindPort] != ZaServer.DEFAULT_IMAP_PORT_ZCS && (obj.attrs[ZaServer.A_zimbraImapBindPort] != null)) || 
			(obj.attrs[ZaServer.A_zimbraImapBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraImapBindPort] != ZaServer.DEFAULT_IMAP_PORT_ZCS))
			 ) {
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.IMAP_Port,obj.attrs[ZaServer.A_zimbraImapBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.IMAP_Port,ZaServer.DEFAULT_IMAP_PORT_ZCS]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.IMAP_Port,obj.attrs[ZaServer.A_zimbraImapBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;		
			tmpObj.fieldRef = ZaServer.A_zimbraImapBindPort;
			tmpObj.defVal = ZaServer.DEFAULT_IMAP_PORT_ZCS;
			ZaXFormViewController.showPortWarning.call(this, params,tmpObj);
		} else {
			this.runValidationStack(params);
		}
	} else {
		this.runValidationStack(params);
	}
}
ZaXFormViewController.preSaveValidationMethods["ZaServerController"].push(ZaServerController.prototype.validateImapBindPort);

ZaServerController.prototype.validateImapSSLBindPort =
function (params) {
	var obj = this._view.getObject();
 	var tmpObj = {selectedChoice:0, choice1Label:"",choice2Label:"",choice3Label:"",warningMsg:"",fieldRef:""};

	if( (obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] != this._currentObject.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] && obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] == true)	
	) {
		if ((obj.attrs[ZaServer.A_ImapSSLBindPort] != ZaServer.DEFAULT_IMAP_SSL_PORT_ZCS && (obj.attrs[ZaServer.A_ImapSSLBindPort] != null)) || (obj.attrs[ZaServer.A_ImapSSLBindPort] == null && (obj.cos.attrs[ZaServer.A_ImapSSLBindPort] != ZaServer.DEFAULT_IMAP_SSL_PORT_ZCS))) { 
			tmpObj.defVal = ZaServer.DEFAULT_IMAP_SSL_PORT_ZCS;
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.IMAP_Port,obj.attrs[ZaServer.A_ImapSSLBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.IMAP_SSLPort,ZaServer.DEFAULT_IMAP_SSL_PORT_ZCS]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.IMAP_SSLPort,obj.attrs[ZaServer.A_ImapSSLBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;				
			tmpObj.fieldRef = ZaServer.A_ImapSSLBindPort;
			ZaXFormViewController.showPortWarning.call(this, params,tmpObj);
		} else {
			this.runValidationStack(params);
		}
	} else {
		this.runValidationStack(params);
	}
}
ZaXFormViewController.preSaveValidationMethods["ZaServerController"].push(ZaServerController.prototype.validateImapSSLBindPort);

ZaServerController.prototype.validatePop3BindPort =
function (params) {
	var obj = this._view.getObject();
 	var tmpObj = {selectedChoice:0, choice1Label:"",choice2Label:"",choice3Label:"",warningMsg:"",fieldRef:""};

	if( (obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] != this._currentObject.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] && obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] == true)	
	) {
		if ((obj.attrs[ZaServer.A_zimbraPop3BindPort] != ZaServer.DEFAULT_POP3_PORT_ZCS && (obj.attrs[ZaServer.A_zimbraPop3BindPort] != null)) || (obj.attrs[ZaServer.A_zimbraPop3BindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraPop3BindPort] != ZaServer.DEFAULT_POP3_PORT_ZCS))) {
			tmpObj.defVal = ZaServer.DEFAULT_POP3_PORT_ZCS;
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.NAD_POP_Port,obj.attrs[ZaServer.A_zimbraPop3BindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.NAD_POP_Port,ZaServer.DEFAULT_POP3_PORT_ZCS]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.NAD_POP_Port,obj.attrs[ZaServer.A_zimbraPop3BindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;	
			tmpObj.fieldRef = ZaServer.A_zimbraPop3BindPort;	
			ZaXFormViewController.showPortWarning.call(this, params,tmpObj);
		} else {
			this.runValidationStack(params);
		}
	} else {
		this.runValidationStack(params);
	}
}
ZaXFormViewController.preSaveValidationMethods["ZaServerController"].push(ZaServerController.prototype.validatePop3BindPort);

ZaServerController.prototype.validatePop3SSLBindPort =
function (params) {
	var obj = this._view.getObject();
 	var tmpObj = {selectedChoice:0, choice1Label:"",choice2Label:"",choice3Label:"",warningMsg:"",fieldRef:""};

	if( (obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] != this._currentObject.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] && obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] == true)	
	) {
		 if ((obj.attrs[ZaServer.A_zimbraPop3SSLBindPort] != ZaServer.DEFAULT_POP3_SSL_PORT_ZCS && (obj.attrs[ZaServer.A_zimbraPop3SSLBindPort] != null)) || (obj.attrs[ZaServer.A_zimbraPop3SSLBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraPop3SSLBindPort] != ZaServer.DEFAULT_POP3_SSL_PORT_ZCS))) {
			tmpObj.defVal = ZaServer.DEFAULT_POP3_SSL_PORT_ZCS;			
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.NAD_POP_SSL_Port,obj.attrs[ZaServer.A_zimbraPop3SSLBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.NAD_POP_SSL_Port,ZaServer.DEFAULT_POP3_SSL_PORT_ZCS]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.NAD_POP_SSL_Port,obj.attrs[ZaServer.A_zimbraPop3SSLBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;				
			tmpObj.fieldRef = ZaServer.A_zimbraPop3SSLBindPort;	
			ZaXFormViewController.showPortWarning.call(this, params,tmpObj);
		} else {
			this.runValidationStack(params);
		}
	} else {
		this.runValidationStack(params);
	}
}
ZaXFormViewController.preSaveValidationMethods["ZaServerController"].push(ZaServerController.prototype.validatePop3SSLBindPort);

ZaServerController.prototype.validateImapProxyBindPort =
function (params) {
	var obj = this._view.getObject();
 	var tmpObj = {selectedChoice:0, choice1Label:"",choice2Label:"",choice3Label:"",warningMsg:"",fieldRef:""};

	if( (obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] != this._currentObject.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] && obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] == true)	
	) {
		 if ((obj.attrs[ZaServer.A_zimbraImapProxyBindPort] != ZaServer.DEFAULT_IMAP_PORT && (obj.attrs[ZaServer.A_zimbraImapProxyBindPort] != null)) || (obj.attrs[ZaServer.A_zimbraImapProxyBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraImapProxyBindPort] != ZaServer.DEFAULT_IMAP_PORT))) {
			tmpObj.defVal = ZaServer.DEFAULT_IMAP_PORT;						
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.IMAP_Proxy_Port,obj.attrs[ZaServer.A_zimbraImapProxyBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.IMAP_Proxy_Port,ZaServer.DEFAULT_IMAP_PORT]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.IMAP_Proxy_Port,obj.attrs[ZaServer.A_zimbraImapProxyBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;	
			tmpObj.fieldRef = ZaServer.A_zimbraImapProxyBindPort;			
			ZaXFormViewController.showPortWarning.call(this, params,tmpObj);
		} else {
			this.runValidationStack(params);
		}
	} else {
		this.runValidationStack(params);
	}
}
ZaXFormViewController.preSaveValidationMethods["ZaServerController"].push(ZaServerController.prototype.validateImapProxyBindPort);


ZaServerController.prototype.validateImapSSLProxyBindPort =
function (params) {
	var obj = this._view.getObject();
 	var tmpObj = {selectedChoice:0, choice1Label:"",choice2Label:"",choice3Label:"",warningMsg:"",fieldRef:""};

	if( (obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] != this._currentObject.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] && obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] == true)	
	) {
		if ((obj.attrs[ZaServer.A_zimbraImapSSLProxyBindPort] != ZaServer.DEFAULT_IMAP_SSL_PORT && (obj.attrs[ZaServer.A_zimbraImapSSLProxyBindPort] != null)) || (obj.attrs[ZaServer.A_zimbraImapSSLProxyBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraImapSSLProxyBindPort] != ZaServer.DEFAULT_IMAP_SSL_PORT))) {
			tmpObj.defVal = ZaServer.DEFAULT_IMAP_SSL_PORT;									
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.IMAP_SSL_Proxy_Port,obj.attrs[ZaServer.A_zimbraImapSSLProxyBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.IMAP_SSL_Proxy_Port,ZaServer.DEFAULT_IMAP_SSL_PORT]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.IMAP_SSL_Proxy_Port,obj.attrs[ZaServer.A_zimbraImapSSLProxyBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;				
			tmpObj.fieldRef = ZaServer.A_zimbraImapSSLProxyBindPort;		
			ZaXFormViewController.showPortWarning.call(this, params,tmpObj);	
		} else {
			this.runValidationStack(params);
		}
	} else {
		this.runValidationStack(params);
	}
}
ZaXFormViewController.preSaveValidationMethods["ZaServerController"].push(ZaServerController.prototype.validateImapSSLProxyBindPort);


ZaServerController.prototype.validatePop3ProxyBindPort =
function (params) {
	var obj = this._view.getObject();
 	var tmpObj = {selectedChoice:0, choice1Label:"",choice2Label:"",choice3Label:"",warningMsg:"",fieldRef:""};

	if( (obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] != this._currentObject.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] && obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] == true)	
	) {
		if ((obj.attrs[ZaServer.A_zimbraPop3ProxyBindPort] != ZaServer.DEFAULT_POP3_PORT && (obj.attrs[ZaServer.A_zimbraPop3ProxyBindPort] != null)) || (obj.attrs[ZaServer.A_zimbraPop3ProxyBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraPop3ProxyBindPort] != ZaServer.DEFAULT_POP3_PORT))) {
			tmpObj.defVal = ZaServer.DEFAULT_POP3_PORT;												
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.NAD_POP_proxy_Port,obj.attrs[ZaServer.A_zimbraPop3ProxyBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.NAD_POP_proxy_Port,ZaServer.DEFAULT_POP3_PORT]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.NAD_POP_proxy_Port,obj.attrs[ZaServer.A_zimbraPop3ProxyBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;				
			tmpObj.fieldRef = ZaServer.A_zimbraPop3ProxyBindPort;		
			ZaXFormViewController.showPortWarning.call(this, params,tmpObj);
		} else {
			this.runValidationStack(params);
		}
	} else {
		this.runValidationStack(params);
	}
}
ZaXFormViewController.preSaveValidationMethods["ZaServerController"].push(ZaServerController.prototype.validatePop3ProxyBindPort);

ZaServerController.prototype.validatePop3SSLProxyBindPort =
function (params) {
	var obj = this._view.getObject();
 	var tmpObj = {selectedChoice:0, choice1Label:"",choice2Label:"",choice3Label:"",warningMsg:"",fieldRef:""};

	if( (obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] != this._currentObject.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] && obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] == true)	
	) {
		if ((obj.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort] != ZaServer.DEFAULT_POP3_SSL_PORT && (obj.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort] != null)) || (obj.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort] != ZaServer.DEFAULT_POP3_SSL_PORT))) {
			tmpObj.defVal = ZaServer.DEFAULT_POP3_SSL_PORT;															
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.NAD_POP_SSL_proxy_Port,obj.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.NAD_POP_SSL_proxy_Port,ZaServer.DEFAULT_POP3_SSL_PORT]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.NAD_POP_SSL_proxy_Port,obj.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;	
			tmpObj.fieldRef = ZaServer.A_zimbraPop3SSLProxyBindPort;			
			ZaXFormViewController.showPortWarning.call(this, params,tmpObj);
		} else {
			this.runValidationStack(params);
		}
	} else {
		this.runValidationStack(params);
	}
}
ZaXFormViewController.preSaveValidationMethods["ZaServerController"].push(ZaServerController.prototype.validatePop3SSLProxyBindPort);
/*
ZaServerController.prototype.validateImapBindPort =
function (params) {
	var obj = this._view.getObject();
	var showWarning=false;
	var msg = "";
 	var tmpObj = {selectedChoice:0, choice1Label:"",choice2Label:"",choice3Label:"",warningMsg:"",fieldRef:""};

	if( (obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] != this._currentObject.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] && obj.attrs[ZaServer.A_zimbraMailProxyServiceEnabled] == true)	
	) {
		if( ((obj.attrs[ZaServer.A_zimbraImapBindPort] != ZaServer.DEFAULT_IMAP_PORT_ZCS && (obj.attrs[ZaServer.A_zimbraImapBindPort] != null)) || 
			(obj.attrs[ZaServer.A_zimbraImapBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraImapBindPort] != ZaServer.DEFAULT_IMAP_PORT_ZCS))
			) && this.validated[ZaServer.A_zimbraImapBindPort]==false) {
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.IMAP_Port,obj.attrs[ZaServer.A_zimbraImapBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.IMAP_Port,ZaServer.DEFAULT_IMAP_PORT_ZCS]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.IMAP_Port,obj.attrs[ZaServer.A_zimbraImapBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;		
			tmpObj.fieldRef = ZaServer.A_zimbraImapBindPort;
			tmpObj.defVal = ZaServer.DEFAULT_IMAP_PORT_ZCS;
			showWarning = true;
		} else if (((obj.attrs[ZaServer.A_ImapSSLBindPort] != ZaServer.DEFAULT_IMAP_SSL_PORT_ZCS && (obj.attrs[ZaServer.A_ImapSSLBindPort] != null)) || (obj.attrs[ZaServer.A_ImapSSLBindPort] == null && (obj.cos.attrs[ZaServer.A_ImapSSLBindPort] != ZaServer.DEFAULT_IMAP_SSL_PORT_ZCS)))  && this.validated[ZaServer.A_ImapSSLBindPort]==false) { 
			tmpObj.defVal = ZaServer.DEFAULT_IMAP_SSL_PORT_ZCS;
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.IMAP_Port,obj.attrs[ZaServer.A_ImapSSLBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.IMAP_SSLPort,ZaServer.DEFAULT_IMAP_SSL_PORT_ZCS]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.IMAP_SSLPort,obj.attrs[ZaServer.A_ImapSSLBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;				
			tmpObj.fieldRef = ZaServer.A_ImapSSLBindPort;			
			showWarning = true;
		} else if (((obj.attrs[ZaServer.A_zimbraPop3BindPort] != ZaServer.DEFAULT_POP3_PORT_ZCS && (obj.attrs[ZaServer.A_zimbraPop3BindPort] != null)) || (obj.attrs[ZaServer.A_zimbraPop3BindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraPop3BindPort] != ZaServer.DEFAULT_POP3_PORT_ZCS))) && this.validated[ZaServer.A_zimbraPop3BindPort]==false) {
			tmpObj.defVal = ZaServer.DEFAULT_POP3_PORT_ZCS;
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.NAD_POP_Port,obj.attrs[ZaServer.A_zimbraPop3BindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.NAD_POP_Port,ZaServer.DEFAULT_POP3_PORT_ZCS]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.NAD_POP_Port,obj.attrs[ZaServer.A_zimbraPop3BindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;	
			tmpObj.fieldRef = ZaServer.A_zimbraPop3BindPort;			
			showWarning = true;
		} else if (((obj.attrs[ZaServer.A_zimbraPop3SSLBindPort] != ZaServer.DEFAULT_POP3_SSL_PORT_ZCS && (obj.attrs[ZaServer.A_zimbraPop3SSLBindPort] != null)) || (obj.attrs[ZaServer.A_zimbraPop3SSLBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraPop3SSLBindPort] != ZaServer.DEFAULT_POP3_SSL_PORT_ZCS))) && this.validated[ZaServer.A_zimbraPop3SSLBindPort]==false) {
			tmpObj.defVal = ZaServer.DEFAULT_POP3_SSL_PORT_ZCS;			
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.NAD_POP_SSL_Port,obj.attrs[ZaServer.A_zimbraPop3SSLBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.NAD_POP_SSL_Port,ZaServer.DEFAULT_POP3_SSL_PORT_ZCS]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.NAD_POP_SSL_Port,obj.attrs[ZaServer.A_zimbraPop3SSLBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;				
			tmpObj.fieldRef = ZaServer.A_zimbraPop3SSLBindPort;			
			showWarning = true;
		} else if (((obj.attrs[ZaServer.A_zimbraImapProxyBindPort] != ZaServer.DEFAULT_IMAP_PORT && (obj.attrs[ZaServer.A_zimbraImapProxyBindPort] != null)) || (obj.attrs[ZaServer.A_zimbraImapProxyBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraImapProxyBindPort] != ZaServer.DEFAULT_IMAP_PORT))) && this.validated[ZaServer.A_zimbraImapProxyBindPort]==false) {
			tmpObj.defVal = ZaServer.DEFAULT_IMAP_PORT;						
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.IMAP_Proxy_Port,obj.attrs[ZaServer.A_zimbraImapProxyBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.IMAP_Proxy_Port,ZaServer.DEFAULT_IMAP_PORT]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.IMAP_Proxy_Port,obj.attrs[ZaServer.A_zimbraImapProxyBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;	
			tmpObj.fieldRef = ZaServer.A_zimbraImapProxyBindPort;			
			showWarning = true;
		} else if (((obj.attrs[ZaServer.A_zimbraImapSSLProxyBindPort] != ZaServer.DEFAULT_IMAP_SSL_PORT && (obj.attrs[ZaServer.A_zimbraImapSSLProxyBindPort] != null)) || (obj.attrs[ZaServer.A_zimbraImapSSLProxyBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraImapSSLProxyBindPort] != ZaServer.DEFAULT_IMAP_SSL_PORT))) && this.validated[ZaServer.A_zimbraImapSSLProxyBindPort]==false) {
			tmpObj.defVal = ZaServer.DEFAULT_IMAP_SSL_PORT;									
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.IMAP_SSL_Proxy_Port,obj.attrs[ZaServer.A_zimbraImapSSLProxyBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.IMAP_SSL_Proxy_Port,ZaServer.DEFAULT_IMAP_SSL_PORT]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.IMAP_SSL_Proxy_Port,obj.attrs[ZaServer.A_zimbraImapSSLProxyBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;				
			tmpObj.fieldRef = ZaServer.A_zimbraImapSSLProxyBindPort;			
			showWarning = true;
		} else if (((obj.attrs[ZaServer.A_zimbraPop3ProxyBindPort] != ZaServer.DEFAULT_POP3_PORT && (obj.attrs[ZaServer.A_zimbraPop3ProxyBindPort] != null)) || (obj.attrs[ZaServer.A_zimbraPop3ProxyBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraPop3ProxyBindPort] != ZaServer.DEFAULT_POP3_PORT))) && this.validated[ZaServer.A_zimbraPop3ProxyBindPort]==false) {
			tmpObj.defVal = ZaServer.DEFAULT_POP3_PORT;												
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.NAD_POP_proxy_Port,obj.attrs[ZaServer.A_zimbraPop3ProxyBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.NAD_POP_proxy_Port,ZaServer.DEFAULT_POP3_PORT]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.NAD_POP_proxy_Port,obj.attrs[ZaServer.A_zimbraPop3ProxyBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;				
			tmpObj.fieldRef = ZaServer.A_zimbraPop3ProxyBindPort;			
			showWarning = true;
		} else if (((obj.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort] != ZaServer.DEFAULT_POP3_SSL_PORT && (obj.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort] != null)) || (obj.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort] == null && (obj.cos.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort] != ZaServer.DEFAULT_POP3_SSL_PORT))) && this.validated[ZaServer.A_zimbraPop3SSLProxyBindPort]==false) {
			tmpObj.defVal = ZaServer.DEFAULT_POP3_SSL_PORT;															
			tmpObj.warningMsg = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning,[ZaMsg.NAD_POP_SSL_proxy_Port,obj.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort]]);
			tmpObj.choice1Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP1,[ZaMsg.NAD_POP_SSL_proxy_Port,ZaServer.DEFAULT_POP3_SSL_PORT]);
			tmpObj.choice2Label = AjxMessageFormat.format(ZaMsg.Server_WrongPortWarning_OP2,[ZaMsg.NAD_POP_SSL_proxy_Port,obj.attrs[ZaServer.A_zimbraPop3SSLProxyBindPort]]);			
			tmpObj.choice3Label = ZaMsg.Server_WrongPortWarning_OP3;	
			tmpObj.fieldRef = ZaServer.A_zimbraPop3SSLProxyBindPort;			
			showWarning = true;
		}
		
	}
	if(showWarning) {
		ZaXFormViewController.showPortWarning.call(this, params,tmpObj);
	} else {
		this.runValidationStack(params);
	}
}
ZaXFormViewController.preSaveValidationMethods["ZaServerController"].push(ZaServerController.prototype.validateProxyPorts);
*/

ZaXFormViewController.showPortWarning = function (params, instanceObj) {
	if(this._app.dialogs["confirmMessageDialog"])
		this._app.dialogs["confirmMessageDialog"].popdown();
		
	this._app.dialogs["confirmMessageDialog"] = new ZaProxyPortWarningXDialog(this._app.getAppCtxt().getShell(), this._app,"550px", "150px",ZaMsg.Server_WrongPortWarningTitle);	
	this._app.dialogs["confirmMessageDialog"].setObject(instanceObj);
	this._app.dialogs["confirmMessageDialog"].registerCallback(DwtDialog.OK_BUTTON, ZaServerController.changeProxyPorts, this, null);
	var args;
	if(!params || !params["func"]) {
		args = null;
	} else {
		args = params;		
	}
	this._app.dialogs["confirmMessageDialog"].popup();		
}
/**
* handles "save" button click
* calls modify on the current ZaServer
**/
ZaServerController.prototype.saveButtonListener =
function(ev) {
	try {
		this.validateChanges();
		
	} catch (ex) {
		//if exception thrown - don' go away
		this._handleException(ex, "ZaServerController.prototype.saveButtonListener", null, false);
	}
}
/**
* handles "download" button click. Launches file download in a new window
**/
ZaServerController.prototype.downloadConfigButtonListener = 
function(ev) {
	window.open(["/service/collectconfig/?host=",this._currentObject.attrs[ZaServer.A_ServiceHostname]].join(""));
}
