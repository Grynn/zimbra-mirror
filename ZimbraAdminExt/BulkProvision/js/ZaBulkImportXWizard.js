/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaBulkImportXWizard
* @contructor ZaBulkImportXWizard
* @param parent DwtShell
* @param entry ZaBulkProvision
* @author Greg Solovyev
**/
function ZaBulkImportXWizard (parent, entry) {
    var w = "650px" ;
    ZaXWizardDialog.call(this, parent, null, com_zimbra_bulkprovision.BP_Wizard_title,
                                w, (AjxEnv.isIE ? "330px" :"320px"),"ZaBulkImportXWizard");

	this.stepChoices = [
	    {label:com_zimbra_bulkprovision.BP_Wizard_chooseAction, value:ZaBulkImportXWizard.STEP_CHOOSE_ACTION},
	    {label:com_zimbra_bulkprovision.BP_Wizard_upload, value:ZaBulkImportXWizard.STEP_UPLOAD_FILE},
	    {label:com_zimbra_bulkprovision.BP_Wizard_bulkProvOptions, value:ZaBulkImportXWizard.STEP_PROV_OPTIONS},
	    {label:com_zimbra_bulkprovision.BP_Wizard_exchangeOptions, value:ZaBulkImportXWizard.STEP_EXCHANGE_INFO},
	    {label:com_zimbra_bulkprovision.BP_Wizard_ldapOptions, value:ZaBulkImportXWizard.STEP_LDAP_INFO},
	    {label:com_zimbra_bulkprovision.BP_Wizard_download, value:ZaBulkImportXWizard.STEP_DOWNLOAD_FILE},		
		{label:com_zimbra_bulkprovision.BP_Wizard_provision, value:ZaBulkImportXWizard.STEP_PROVISION},
		{label:com_zimbra_bulkprovision.BP_Wizard_summary, value:ZaBulkImportXWizard.STEP_SUMMARY}
	];

	ZaBulkImportXWizard.STATUS_LABELS = {};
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_IDLE] = com_zimbra_bulkprovision.ProvisioningStatusIdle;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_STARTED] = com_zimbra_bulkprovision.ProvisioningStatusStarted;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_STARTING] = com_zimbra_bulkprovision.ProvisioningStatusStarting;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_CREATING_ACCOUNTS] = com_zimbra_bulkprovision.ProvisioningStatusCreatingAccounts;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_FINISHED] = com_zimbra_bulkprovision.ProvisioningStatusFinished;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_ABORT] = com_zimbra_bulkprovision.ProvisioningStatusAborting;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_ABORTED] = com_zimbra_bulkprovision.ProvisioningStatusAborted;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_ERROR] = com_zimbra_bulkprovision.ProvisioningStatusError;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_NOT_RUNNING] = com_zimbra_bulkprovision.ProvisioningStatusNotRunning;
	
	this.pollAction = new AjxTimedAction(this, this.getImportStatus);
	this._pollHandler = null;
	
    this.initForm(ZaBulkProvision.getMyXModel(),this.getMyXForm(entry),null);

  	this._helpURL = ZaBulkImportXWizard.helpURL;
}
ZaBulkImportXWizard.POLL_INTERVAL = 500;
// -1 : No status, 0: Install succeed, >0 : Install Failed (different number is different error)
ZaBulkImportXWizard.INSTALL_STATUS = -1;
ZaBulkImportXWizard.STEP_INDEX = 1;
ZaBulkImportXWizard.STEP_CHOOSE_ACTION = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_UPLOAD_FILE = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_PROV_OPTIONS = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_EXCHANGE_INFO = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_LDAP_INFO = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_DOWNLOAD_FILE = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_PROVISION = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_SUMMARY = ZaBulkImportXWizard.STEP_INDEX++;

ZaBulkImportXWizard.prototype = new ZaXWizardDialog;
ZaBulkImportXWizard.prototype.constructor = ZaBulkImportXWizard;

ZaXDialog.XFormModifiers["ZaBulkImportXWizard"] = new Array();
ZaBulkImportXWizard.helpURL = location.pathname + "help/admin/html/managing_accounts/how_to_provision_multiple_accounts.htm?locid=" + AjxEnv.DEFAULT_LOCALE;

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/

ZaBulkImportXWizard.prototype.popdown = 
function () {
	if(this._pollHandler) {
		//stop polling
		AjxTimedAction.cancelAction(this._pollHandler);
	}
	DwtDialog.prototype.popdown.call(this);
}

ZaBulkImportXWizard.prototype.popup =
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	try {
		var status = ZaBulkProvision.iSTATUS_NOT_RUNNING;
		var resp = ZaBulkProvision.getImportStatus();
		if(resp.Body && resp.Body.BulkImportAccountsResponse) {
			var response = resp.Body.BulkImportAccountsResponse;
			if(response && response[ZaBulkProvision.A2_status] && response[ZaBulkProvision.A2_status][0] && response[ZaBulkProvision.A2_status][0]._content) {
				status = parseInt(response[ZaBulkProvision.A2_status][0]._content);
				this.processBulkImportResponse(response);
			}			
		}
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex);
	}
	if(status == ZaBulkProvision.iSTATUS_NOT_RUNNING) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
	} else {
		this.goPage(ZaBulkImportXWizard.STEP_PROVISION);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(false);
	}
    
    this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
}

ZaBulkImportXWizard.prototype._uploadCallback =
function (status, attId) {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if (status == AjxPost.SC_OK) {
    	
        if (attId != null && attId.length > 0) {
           this._containedObject [ZaBulkProvision.A_aid] =  attId;
           var callback = new AjxCallback(this, ZaBulkImportXWizard.prototype.importCallback,{action:this._containedObject[ZaBulkProvision.A2_provAction]});
           ZaBulkProvision.importAccountsFromFile(this._containedObject,callback);
        } else {
    		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
    		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
    		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
    		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
    		this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_upload_bulk_no_aid, null, null, true);
    		return ;
        }
	} else {
		// handle errors during attachment upload.
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		var msg = AjxMessageFormat.format(com_zimbra_bulkprovision.error_upload_bulk, [status]);
		this._app.getCurrentController().popupErrorDialog(msg, null, null, true);
	}
}

//upload the file
ZaBulkImportXWizard.prototype.getUploadFrameId =
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

ZaBulkImportXWizard.prototype.getUploadManager =
function() {
	return this._uploadManager;
};

/**
 * @params uploadManager is the AjxPost object
 */
ZaBulkImportXWizard.prototype.setUploadManager =
function(uploadManager) {
	this._uploadManager = uploadManager;
};

ZaBulkImportXWizard.prototype.getProvisionStatusDialog = function () {
    if (!this._provisionStatusDialog) {
        this._provisionStatusDialog = new ZaBulkProvisionStatusDialog (
                this.parent, this._app);
        this._provisionStatusDialog.addPopupListener(new AjxListener(this, this.statusDialogPopupListener)) ;
    }

    return this._provisionStatusDialog ;
}

ZaBulkImportXWizard.prototype.getImportStatus = function () {
	var callback = new AjxCallback(this, ZaBulkImportXWizard.prototype.importCallback,{action:this._containedObject[ZaBulkProvision.A2_provAction]});
	ZaBulkProvision.getImportStatus(callback);	
}

ZaBulkImportXWizard.prototype.generateBulkFileCallback = 
function(params,resp) {
	try {
		if(resp && resp.isException()) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaBulkImportXWizard.prototype.generateBulkFileCallback");
		} else {
			var response = resp.getResponse().Body.GenerateBulkProvisionFileFromLDAPResponse;
			if(response.fileToken && response.fileToken[0] && response.fileToken[0]._content) {
				var format = "";
				if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_MIG_XML) {
					format = ZaBulkProvision.FILE_FORMAT_MIGRATION_XML;
				} else if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_BULK_XML) {
					format = ZaBulkProvision.FILE_FORMAT_BULK_XML;
				} else if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_BULK_CSV) {
					format = ZaBulkProvision.FILE_FORMAT_BULK_CSV;
				}
				this._localXForm.setInstanceValue(
						AjxMessageFormat.format("{0}//{1}:{2}/service/afd/?action=getBulkFile&fileID={3}&fileFormat={4}",
								[location.protocol,location.hostname,location.port,response.fileToken[0]._content,format]),
								ZaBulkProvision.A2_generatedFileLink);
				this.goPage(ZaBulkImportXWizard.STEP_DOWNLOAD_FILE);
				this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
				this._button[DwtDialog.CANCEL_BUTTON].setEnabled(false);
			}
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaBulkImportXWizard.prototype.generateBulkFileCallback");	
	}
		
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);	
};

ZaBulkImportXWizard.prototype.processBulkImportResponse = function(response) {
	var status = 0;
	if(response[ZaBulkProvision.A2_status] && response[ZaBulkProvision.A2_status][0] && response[ZaBulkProvision.A2_status][0]._content) {
		status = parseInt(response[ZaBulkProvision.A2_status][0]._content);
		if(status == ZaBulkProvision.iSTATUS_STARTED || status == ZaBulkProvision.iSTATUS_CREATING_ACCOUNTS || status == ZaBulkProvision.iSTATUS_ABORT) {	
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		} else {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		}
		this._localXForm.setInstanceValue(status,ZaBulkProvision.A2_status);
	}
	var totalCount = 0;
	var errorCount = 0;
	var provisionedCount = 0;
	var skippedCount = 0;
	var fileToken = null;
	if(response[ZaBulkProvision.A2_totalCount] && response[ZaBulkProvision.A2_totalCount][0] && response[ZaBulkProvision.A2_totalCount][0]._content) {
		totalCount = parseInt(response[ZaBulkProvision.A2_totalCount][0]._content);
	}
	if(response[ZaBulkProvision.A2_errorCount] && response[ZaBulkProvision.A2_errorCount][0] && response[ZaBulkProvision.A2_errorCount][0]._content) {
		errorCount = parseInt(response[ZaBulkProvision.A2_errorCount][0]._content);
	}
	if(response[ZaBulkProvision.A2_provisionedCount] && response[ZaBulkProvision.A2_provisionedCount][0] && response[ZaBulkProvision.A2_provisionedCount][0]._content) {
		provisionedCount = parseInt(response[ZaBulkProvision.A2_provisionedCount][0]._content);
	}		
	if(response[ZaBulkProvision.A2_skippedCount] && response[ZaBulkProvision.A2_skippedCount][0] && response[ZaBulkProvision.A2_skippedCount][0]._content) {
		skippedCount = parseInt(response[ZaBulkProvision.A2_skippedCount][0]._content);
	}	
	if(response[ZaBulkProvision.A2_fileToken] && response[ZaBulkProvision.A2_fileToken][0] && response[ZaBulkProvision.A2_fileToken][0]._content) {
		fileToken = response[ZaBulkProvision.A2_fileToken][0]._content; 
		this._localXForm.setInstanceValue(fileToken,ZaBulkProvision.A2_fileToken);
	}
	this._localXForm.setInstanceValue(totalCount,ZaBulkProvision.A2_totalCount);
	this._localXForm.setInstanceValue(errorCount,ZaBulkProvision.A2_errorCount);
	this._localXForm.setInstanceValue(provisionedCount,ZaBulkProvision.A2_provisionedCount);
	this._localXForm.setInstanceValue(skippedCount,ZaBulkProvision.A2_skippedCount);
	if(totalCount > 0 && (provisionedCount>0 || skippedCount>0 || errorCount>0)) {
		this._localXForm.setInstanceValue( Math.round(100*(provisionedCount+skippedCount+errorCount)/totalCount)+"%",ZaBulkProvision.A2_progress)
	} else {
		this._localXForm.setInstanceValue("0%",ZaBulkProvision.A2_progress);
	}
	if(status == ZaBulkProvision.iSTATUS_ABORT) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(false);
	}
	
	if(status == ZaBulkProvision.iSTATUS_STARTING || status == ZaBulkProvision.iSTATUS_CREATING_ACCOUNTS || status == ZaBulkProvision.iSTATUS_FINISHED || status == ZaBulkProvision.iSTATUS_ABORTED || status == ZaBulkProvision.iSTATUS_ERROR) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(false);		
	}
	var errorsFileLink = null;
	var sucessFileLink = null;
	if(status == ZaBulkProvision.iSTATUS_FINISHED || status == ZaBulkProvision.iSTATUS_ABORTED || status == ZaBulkProvision.iSTATUS_ERROR) {
		sucessFileLink = AjxMessageFormat.format("{0}//{1}:{2}/service/afd/?action=getBulkFile&fileID={3}&fileFormat=reportcsv",
						[location.protocol,location.hostname,location.port,fileToken]);
						
		if(errorCount>0) {
			errorsFileLink = AjxMessageFormat.format("{0}//{1}:{2}/service/afd/?action=getBulkFile&fileID={3}&fileFormat=errorscsv",
					[location.protocol,location.hostname,location.port,fileToken]);
		}
	}
	this._localXForm.setInstanceValue(sucessFileLink,ZaBulkProvision.A2_completedAccountsFileLink);
	this._localXForm.setInstanceValue(errorsFileLink,ZaBulkProvision.A2_failedAccountsFileLink);
	
	if(status == ZaBulkProvision.iSTATUS_IDLE || status == ZaBulkProvision.iSTATUS_STARTING || status == ZaBulkProvision.iSTATUS_STARTED || status == ZaBulkProvision.iSTATUS_CREATING_ACCOUNTS || status == ZaBulkProvision.iSTATUS_ABORT) {
		/**
		 * schedule next poll
		 */
		this._pollHandler = AjxTimedAction.scheduleAction(this.pollAction, ZaBulkImportXWizard.POLL_INTERVAL);		
	} else {
		if(this._pollHandler) {
			AjxTimedAction.cancelAction(this._pollHandler);
			this._pollHandler = null;
		}
	}	
}

ZaBulkImportXWizard.isProvisioningNoteVisible = function() {
	var status = this.getInstanceValue(ZaBulkProvision.A2_status);
	return (status == ZaBulkProvision.iSTATUS_STARTED || status == ZaBulkProvision.iSTATUS_CREATING_ACCOUNTS);
}

ZaBulkImportXWizard.prototype.importCallback = function(params,resp) {
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	this._button[DwtDialog.CANCEL_BUTTON].setEnabled(false);
	try {
		if(resp && resp.isException()) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaBulkImportXWizard.prototype.importCallback");
		} else {
			var response = resp.getResponse().Body.BulkImportAccountsResponse;
			this.processBulkImportResponse(response);
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaBulkImportXWizard.prototype.generateBulkFileCallback");	
	}
}

ZaBulkImportXWizard.prototype.goNext =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;

	if(cStep == ZaBulkImportXWizard.STEP_CHOOSE_ACTION) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
		if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_CSV || 
				this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_XML) {
			this.goPage(ZaBulkImportXWizard.STEP_UPLOAD_FILE);
		} else {
			this.goPage(ZaBulkImportXWizard.STEP_PROV_OPTIONS);
		}
	} else if(cStep == ZaBulkImportXWizard.STEP_PROV_OPTIONS) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		if(this._containedObject[ZaBulkProvision.A2_generatePassword] == "FALSE") {
			/**
			 * Check that passwords match
			 */
			if(this._containedObject[ZaBulkProvision.A2_confirmPassword] != this._containedObject[ZaBulkProvision.A2_password]) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_PASSWORDS_DONT_MATCH);
				return;
			}
		}
		if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_MIG_XML) {
			this.goPage(ZaBulkImportXWizard.STEP_EXCHANGE_INFO);
		} else {
			this.goPage(ZaBulkImportXWizard.STEP_LDAP_INFO);
		}
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
	} else if(cStep == ZaBulkImportXWizard.STEP_EXCHANGE_INFO) { 
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		/**
		 * Check that passwords match
		 */
		if(this._containedObject[ZaBulkProvision.A2_ZimbraAdminPassword] != this._containedObject[ZaBulkProvision.A2_ZimbraAdminPasswordConfirm]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_PASSWORDS_DONT_MATCH);
			return;
		}		
		/**
		 * exch mig wizard requires <domaion> in <ZimbraServer>
		 */
		if(!this._containedObject[ZaBulkProvision.A2_TargetDomainName]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.MUST_SELECT_TARGET_DOMAIN);
			return;	
		}
		
		/**
		 * Set defaults
		 */
		
/*		if(!this._containedObject[ZaBulkProvision.A2_GalLdapSearchBase] && typeof this._containedObject[ZaBulkProvision.A2_SourceDomainName] == "string") {
			var searchBase = "OU=Users,dc=" + this._containedObject[ZaBulkProvision.A2_SourceDomainName].replace(".",",dc=");
			this._localXForm.setInstanceValue(searchBase,ZaBulkProvision.A2_GalLdapSearchBase);
		}
		
		if(!this._containedObject[ZaBulkProvision.A2_GalLdapBindDn] && typeof this._containedObject[ZaBulkProvision.A2_SourceDomainName] == "string") {
			var bindDN = "CN=administrator,OU=Users,dc=" + this._containedObject[ZaBulkProvision.A2_SourceDomainName].replace(".",",dc=");
			this._localXForm.setInstanceValue(bindDN,ZaBulkProvision.A2_GalLdapBindDn);
		}
*/		
		this.goPage(ZaBulkImportXWizard.STEP_LDAP_INFO);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
				
	} else if(cStep == ZaBulkImportXWizard.STEP_LDAP_INFO) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		/**
		 * Check that passwords match
		 */
		if(this._containedObject[ZaBulkProvision.A2_GalLdapBindPassword] != this._containedObject[ZaBulkProvision.A2_GalLdapConfirmBindPassword]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_PASSWORDS_DONT_MATCH);
			return;
		}		
		if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_MIG_XML || 
				this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_BULK_XML ||
				this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_BULK_CSV) {

			/**
			 * Generate the file and launch a callback when file is ready
			 */
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
			var callback = new AjxCallback(this, ZaBulkImportXWizard.prototype.generateBulkFileCallback,{action:this._containedObject[ZaBulkProvision.A2_provAction]});
			ZaBulkProvision.generateBulkProvisionFile(this._containedObject,callback);
		} else if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_LDAP) {
			/**
			 * Start import
			 */
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
			this._localXForm.setInstanceValue(ZaBulkProvision.iSTATUS_STARTING,ZaBulkProvision.A2_status);
			this.goPage(ZaBulkImportXWizard.STEP_PROVISION);
			var callback = new AjxCallback(this, ZaBulkImportXWizard.prototype.importCallback,{action:this._containedObject[ZaBulkProvision.A2_provAction]});
			ZaBulkProvision.importAccountsFromLDAP(this._containedObject,callback);
		}
	} else if (cStep == ZaBulkImportXWizard.STEP_UPLOAD_FILE) {
        //1. check if the file name are valid and exists
        //2. upload the file
        var formEl = document.getElementById(ZaBulkImportXWizard.csvUploadFormId);
        var inputEls = formEl.getElementsByTagName("input") ;

        var filenameArr = [];
        for (var i=0; i < inputEls.length; i++){
            if (inputEls[i].type == "file") {
                var n = inputEls[i].name ;
                var v = ZaBulkImportXWizard.getFileName(inputEls[i].value) ;
                if ( n == "bulkFile") {
                    if (v == null || v.length <= 0) {
                        this._app.getCurrentController().popupErrorDialog (
                            com_zimbra_bulkprovision.error_no_bulk_file_specified
                        );
                        return ;
                    }

                    break ;
                }
            }
        }

        //2. Upload the file
        this.setUploadManager(new AjxPost(this.getUploadFrameId()));
        var csvUploadCallback = new AjxCallback(this, this._uploadCallback);
        var um = this.getUploadManager() ;
        window._uploadManager = um;
		/**
		 * Start import
		 */
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
		this._localXForm.setInstanceValue(ZaBulkProvision.iSTATUS_STARTING,ZaBulkProvision.A2_status);
        try {
            um.execute(csvUploadCallback, document.getElementById (ZaBulkImportXWizard.csvUploadFormId));
        }catch (err) {
            this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_no_bulk_file_specified) ;
        }
		this.goPage(ZaBulkImportXWizard.STEP_PROVISION);
        return ; //allow the callback to handle the wizard buttons
    } 
}

ZaBulkImportXWizard.prototype.goPrev =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	var prevStep ;
	
	if (cStep == ZaBulkImportXWizard.STEP_UPLOAD_FILE || cStep == ZaBulkImportXWizard.STEP_PROV_OPTIONS) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);			
		prevStep = ZaBulkImportXWizard.STEP_CHOOSE_ACTION ;
    } else if (cStep == ZaBulkImportXWizard.STEP_LDAP_INFO) {
    	if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_MIG_XML) {
    		prevStep = ZaBulkImportXWizard.STEP_EXCHANGE_INFO;
    	} else {
    		prevStep = ZaBulkImportXWizard.STEP_PROV_OPTIONS;
    	}
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
    } else if (cStep == ZaBulkImportXWizard.STEP_EXCHANGE_INFO) {
    	prevStep = ZaBulkImportXWizard.STEP_PROV_OPTIONS;
    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
    } else if(cStep == ZaBulkImportXWizard.STEP_DOWNLOAD_FILE || cStep == ZaBulkImportXWizard.STEP_PROVISION) {
    	if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_CSV ||
    			this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_XML) {
    		prevStep = ZaBulkImportXWizard.STEP_UPLOAD_FILE;
    	} else {
    		prevStep = ZaBulkImportXWizard.STEP_LDAP_INFO;
    	}
    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
    }
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
    this.goPage(prevStep);
}

ZaBulkImportXWizard.prototype.goPage = function (pageKey) {
    ZaXWizardDialog.prototype.goPage.call(this, pageKey) ;
}

ZaBulkImportXWizard.getFileName = function (fullPath) {
    if (fullPath == null) return null ;

    var lastIndex = 0;
    if (AjxEnv.isWindows) {
        lastIndex = fullPath.lastIndexOf("\\") ;
    }else{
        lastIndex = fullPath.lastIndexOf("/") ;
    }

    return fullPath.substring(lastIndex + 1) ;
}

/**
* @method setObject sets the object contained in the view
* @param entry -  object to display
**/
ZaBulkImportXWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject = entry ;

	this._containedObject[ZaModel.currentStep] = ZaBulkImportXWizard.STEP_CHOOSE_ACTION;
	this._containedObject[ZaBulkProvision.A_mustChangePassword] = "TRUE";
    this._localXForm.setInstance(this._containedObject);
}

ZaBulkImportXWizard.csvUploadFormId = Dwt.getNextId();

ZaBulkImportXWizard.getUploadFormHtml =
function (fileType){
	ZaBulkImportXWizard.attachmentInputId = Dwt.getNextId();	
	var uri = appContextPath + "/../service/upload";
	var html = [];
	var idx = 0;
	html[idx++] = "<div style='height:50px;width: 500px; overflow:auto;'>";
	html[idx++] = "<table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;
	//html[idx++] = "<colgroup><col width=50 /><col width='*' /><col width=50 /></colgroup>";

	html[idx++] = "<tbody><tr><td width=65>" + ((fileType == ZaBulkProvision.ACTION_IMPORT_XML) ? com_zimbra_bulkprovision.XML_Upload_file : com_zimbra_bulkprovision.CSV_Upload_file) + "</td>";
	html[idx++] = "<td>";
	html[idx++] = "<form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaBulkImportXWizard.csvUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'>" ;
	html[idx++] = "<input type=file  name='bulkFile' size='45' id='";
	html[idx++] = ZaBulkImportXWizard.attachmentInputId;
	html[idx++] = "'></input></form></td></tr>";
	html[idx++] = "</tbody></table>";
	html[idx++] = "</div>";

	return html.join("");
}

ZaBulkImportXWizard.myXFormModifier = function(xFormObject,entry) {
	var cases = new Array();

	var case_choose_action = {type:_CASE_,numCols:2,colSizes:["100px","*"],
		tabGroupKey:ZaBulkImportXWizard.STEP_CHOOSE_ACTION,caseKey:ZaBulkImportXWizard.STEP_CHOOSE_ACTION,
		items:[
		       {type:_DWT_ALERT_, style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.SelectAction1,visibilityChecks:[]},
		       {type:_RADIO_, groupname:"action_selection",ref:ZaBulkProvision.A2_provAction,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.ActionImportFromLDAP,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.ACTION_IMPORT_LDAP);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.ACTION_IMPORT_LDAP,ZaBulkProvision.A2_provAction);
					},visibilityChecks:[],enableDisableChecks:[]
		       },		       
		       {type:_RADIO_, groupname:"action_selection",ref:ZaBulkProvision.A2_provAction,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.ActionImportFromSCV,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.ACTION_IMPORT_CSV);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.ACTION_IMPORT_CSV,ZaBulkProvision.A2_provAction);
					},visibilityChecks:[],enableDisableChecks:[]
		       },
		       {type:_RADIO_, groupname:"action_selection",ref:ZaBulkProvision.A2_provAction,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.ActionImportFromXML,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.ACTION_IMPORT_XML);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.ACTION_IMPORT_XML,ZaBulkProvision.A2_provAction);
					},visibilityChecks:[],enableDisableChecks:[]
		       },
		       {type:_DWT_ALERT_, style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.SelectAction2,visibilityChecks:[]},
		       {type:_RADIO_, groupname:"action_selection",ref:ZaBulkProvision.A2_provAction,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.ActionGenerateMigXML,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.ACTION_GENERATE_MIG_XML);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.ACTION_GENERATE_MIG_XML,ZaBulkProvision.A2_provAction);
					},visibilityChecks:[],enableDisableChecks:[]
		       },
		       {type:_RADIO_, groupname:"action_selection",ref:ZaBulkProvision.A2_provAction,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.ActionGenerateBulkXML,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.ACTION_GENERATE_BULK_XML);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.ACTION_GENERATE_BULK_XML,ZaBulkProvision.A2_provAction);
					},visibilityChecks:[],enableDisableChecks:[]
		       },
		       {type:_RADIO_, groupname:"action_selection",ref:ZaBulkProvision.A2_provAction,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.ActionGenerateBulkCSV,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.ACTION_GENERATE_BULK_CSV);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.ACTION_GENERATE_BULK_CSV,ZaBulkProvision.A2_provAction);
					},visibilityChecks:[],enableDisableChecks:[]
		       }
       ]
	};
	cases.push(case_choose_action);
	
	/**
	 * Enter options for provisioning
	 */
	var case_prov_options = {
		type:_CASE_, numCols:2, colSizes:["250px","*"],tabGroupKey:ZaBulkImportXWizard.STEP_PROV_OPTIONS, caseKey:ZaBulkImportXWizard.STEP_PROV_OPTIONS,
		items:[
		       	{type:_DWT_ALERT_, style:DwtAlert.INFO,iconVisible:false,content:com_zimbra_bulkprovision.ProvOptionsNote,visibilityChecks:[],enableDisableChecks:[]},
		       	{type:_RADIO_, groupname:"account_password_option",ref:ZaBulkProvision.A2_generatePassword,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.GenerateRandomPassword, bmolsnr:true,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == "TRUE");
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue("TRUE",ZaBulkProvision.A2_generatePassword);
					},visibilityChecks:[],enableDisableChecks:[]
				},
				{type:_TEXTFIELD_,ref:ZaBulkProvision.A2_genPasswordLength,label:com_zimbra_bulkprovision.GeneratedPasswordLength,
				   visibilityChecks:[],enableDisableChangeEventSources:[ZaBulkProvision.A2_generatePassword],
				   enableDisableChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_generatePassword,"TRUE"]],
				   cssClass:"admin_xform_number_input"
				},
		       	{type:_DWT_ALERT_, style:DwtAlert.WARNING,iconVisible:false,content:com_zimbra_bulkprovision.GeneratePasswordsNote,visibilityChecks:[],enableDisableChecks:[]},				
				{type:_RADIO_, groupname:"account_password_option",ref:ZaBulkProvision.A2_generatePassword, bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.UseSamePassword,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == "FALSE");
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue("FALSE",ZaBulkProvision.A2_generatePassword);
					},visibilityChecks:[],enableDisableChecks:[]
				},
		       	{ref:ZaBulkProvision.A2_password, type:_SECRET_, 
					label:com_zimbra_bulkprovision.PasswordToUse, labelLocation:_LEFT_, 
					cssClass:"admin_xform_name_input",visibilityChecks:[],
					enableDisableChangeEventSources:[ZaBulkProvision.A2_generatePassword],
					enableDisableChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_generatePassword,"FALSE"]]
				},
				{ref:ZaBulkProvision.A2_confirmPassword, type:_SECRET_,
					label:com_zimbra_bulkprovision.PasswordToUseConfirm, labelLocation:_LEFT_, 
					cssClass:"admin_xform_name_input",visibilityChecks:[],
					enableDisableChangeEventSources:[ZaBulkProvision.A2_generatePassword],
					enableDisableChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_generatePassword,"FALSE"]]
				},
				{ref:ZaBulkProvision.A_mustChangePassword,  type:_CHECKBOX_,  
					label:com_zimbra_bulkprovision.RequireChangePassword,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[]
				},
				{type:_GROUP_,visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_provAction,ZaBulkProvision.ACTION_GENERATE_MIG_XML]],
					numCols:2, colSizes:["250px","*"],colSpan:2,
					items:[
						{ref:ZaBulkProvision.A2_importMails,  type:_CHECKBOX_,  
							label:com_zimbra_bulkprovision.MigImportMails,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[]
						},
						{ref:ZaBulkProvision.A2_importContacts,  type:_CHECKBOX_,  
							label:com_zimbra_bulkprovision.MigImportContacts,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[]
						},
						{ref:ZaBulkProvision.A2_importTasks,  type:_CHECKBOX_,  
							label:com_zimbra_bulkprovision.MigImportTasks,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[]
						},
						{ref:ZaBulkProvision.A2_importCalendar,  type:_CHECKBOX_,  
							label:com_zimbra_bulkprovision.MigImportCalendar,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[]
						},
						{ref:ZaBulkProvision.A2_importDeletedItems,  type:_CHECKBOX_,  
							label:com_zimbra_bulkprovision.MigImportDeletedItems,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[]
						},
						{ref:ZaBulkProvision.A2_importJunk,  type:_CHECKBOX_,  
							label:com_zimbra_bulkprovision.MigImportJunk,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[]
						},
						{ref:ZaBulkProvision.A2_ignorePreviouslyImported,  type:_CHECKBOX_,  
							label:com_zimbra_bulkprovision.MigIgnorePreviouslyImported,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[]
						},
						{ref:ZaBulkProvision.A2_InvalidSSLOk,  type:_CHECKBOX_,  
							label:com_zimbra_bulkprovision.MigInvalidSSLOk,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[]
						}						
					]
				}

		]
	};
	cases.push(case_prov_options);
	
	/**
	 * Enter options specific to Exchange Migration
	 */
	
	var case_exchange_options = {
			type:_CASE_, numCols:2,colSizes:["250px","*"], tabGroupKey:ZaBulkImportXWizard.STEP_EXCHANGE_INFO, caseKey:ZaBulkImportXWizard.STEP_EXCHANGE_INFO,
			items:[
			       	{type:_DWT_ALERT_, style:DwtAlert.INFO,iconVisible:false,content:com_zimbra_bulkprovision.ZimbraAdminPasswordNote,
			       		visibilityChecks:[],enableDisableChecks:[],colSpan:2
			       	},
			       	{ref:ZaBulkProvision.A2_TargetDomainName, type:_DYNSELECT_,
						label:com_zimbra_bulkprovision.TargetDomainName,
						toolTipContent:ZaMsg.tt_StartTypingDomainName,
						dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains,
						dataFetcherClass:ZaSearch,editable:true,
						visibilityChecks:[],
						visibilityChangeEventSources:[],
						enableDisableChecks:[]
					},
					{ref:ZaBulkProvision.A2_ZimbraAdminLogin, type:_OUTPUT_, label:com_zimbra_bulkprovision.A2_ZimbraAdminLogin, labelLocation:_LEFT_, 
						enableDisableChecks:[],visibilityChecks:[]				
					},
					{ref:ZaBulkProvision.A2_ZimbraAdminPassword, type:_SECRET_, label:com_zimbra_bulkprovision.A2_ZimbraAdminPassword, labelLocation:_LEFT_, 
						enableDisableChecks:[],visibilityChecks:[]				
					},
					{ref:ZaBulkProvision.A2_ZimbraAdminPasswordConfirm, type:_SECRET_, label:com_zimbra_bulkprovision.A2_ZimbraAdminPasswordConfirm, labelLocation:_LEFT_, 
						enableDisableChecks:[],visibilityChecks:[]				
					},
					{ref:ZaBulkProvision.A2_provisionUsers,  type:_CHECKBOX_,  
						label:com_zimbra_bulkprovision.A2_provisionUsers,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[]
					},					
					{type:_DWT_ALERT_, style:DwtAlert.INFO,iconVisible:false,content:com_zimbra_bulkprovision.MAPIINfoNote,
			       		visibilityChecks:[],enableDisableChecks:[],colSpan:2
			       	},
					{ref:ZaBulkProvision.A2_MapiProfile, type:_TEXTFIELD_, label:com_zimbra_bulkprovision.A2_MapiProfile, labelLocation:_LEFT_, 
						enableDisableChecks:[],visibilityChecks:[]				
					},
					{ref:ZaBulkProvision.A2_MapiServer, type:_TEXTFIELD_, label:com_zimbra_bulkprovision.A2_MapiServer, labelLocation:_LEFT_, 
						enableDisableChecks:[],visibilityChecks:[]				
					},
					{ref:ZaBulkProvision.A2_MapiLogonUserDN, type:_TEXTFIELD_, width:"380px", label:com_zimbra_bulkprovision.A2_MapiLogonUserDN, labelLocation:_LEFT_, 
						enableDisableChecks:[],visibilityChecks:[]				
					}

			]
	};
	cases.push(case_exchange_options);
	/**
	 * Enter LDAP info for generating bulk file or direct import
	 */
	var case_ldap_info = {
		type:_CASE_, numCols:2, colSizes:["250px","*"],tabGroupKey:ZaBulkImportXWizard.STEP_LDAP_INFO, caseKey:ZaBulkImportXWizard.STEP_LDAP_INFO,
		items:[
		       	{type:_DWT_ALERT_, style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.LdapInfoStepNote},
				{ref:ZaBulkProvision.A2_createDomains,  type:_CHECKBOX_,  
					label:com_zimbra_bulkprovision.A2_createDomains,trueValue:"TRUE", falseValue:"FALSE",
					visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_provAction,ZaBulkProvision.ACTION_IMPORT_LDAP]],enableDisableChecks:[]
				},
		       	{ref:ZaBulkProvision.A2_maxResults, type:_TEXTFIELD_,cssClass:"admin_xform_number_input", 
		       		label:com_zimbra_bulkprovision.LDAPMaxResults, labelLocation:_LEFT_,labelWrap:true,
		       		visibilityChecks:[],enableDisableChecks:[]
		       	},
		       	{type:_GROUP_, numCols:6, colSpan:2,label:"   ",labelLocation:_LEFT_,
					visibilityChecks:[],
					items: [
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerName, width:"200px"},
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerPort,  width:"40px"},	
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALUseSSL, width:"*"}									
					]
				},		       
				{ref:ZaBulkProvision.A2_GalLdapURL, type:_LDAPURL_, label:com_zimbra_bulkprovision.LDAPUrl,ldapSSLPort:"3269",ldapPort:"3268",  labelLocation:_LEFT_,
					visibilityChecks:[],enableDisableChecks:[]
				},
				{ref:ZaBulkProvision.A2_GalLdapBindDn, type:_TEXTFIELD_, width:"380px", label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, 
					enableDisableChecks:[],visibilityChecks:[],bmolsnr:true				
				},
				{ref:ZaBulkProvision.A2_GalLdapBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, 
					enableDisableChecks:[],visibilityChecks:[]				
				},
				{ref:ZaBulkProvision.A2_GalLdapConfirmBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPasswordConfirm, labelLocation:_LEFT_, 
					enableDisableChecks:[],visibilityChecks:[]				
				},				
				{ref:ZaBulkProvision.A2_GalLdapFilter, type:_TEXTAREA_, width:380, height:40, 
					label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, 
					enableDisableChecks:[],visibilityChecks:[]
				},
				{ref:ZaBulkProvision.A2_GalLdapSearchBase, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapSearchBase, 
					labelLocation:_LEFT_, enableDisableChecks:[],visibilityChecks:[],bmolsnr:true
				}					
		]
	};
	cases.push(case_ldap_info);
	
	/**
	 * Download generated bulk file
	 */
	var case_download_file = {
		type:_CASE_, numCols:2, colSizes:["250px","*"], tabGroupKey:ZaBulkImportXWizard.STEP_DOWNLOAD_FILE, 
		caseKey:ZaBulkImportXWizard.STEP_DOWNLOAD_FILE,
		items:[
		       {type:_DWT_ALERT_, style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.ClickToDownloadGeneratedFile},
		       {type:_DATA_URL_,labelLocation:_NONE_,label:com_zimbra_bulkprovision.GeneratedBulkProvisionFileLink,ref:ZaBulkProvision.A2_generatedFileLink}
		]
	};
	cases.push(case_download_file);
	
	/**
	 * Upload bulk file
	 */
	var case_upload_file = {type:_CASE_, numCols:2, colSizes:["250px","*"],
        tabGroupKey:ZaBulkImportXWizard.STEP_UPLOAD_FILE, caseKey:ZaBulkImportXWizard.STEP_UPLOAD_FILE,
					items: [
						{ref:ZaBulkProvision.A2_createDomains,  type:_CHECKBOX_,  
							label:com_zimbra_bulkprovision.A2_createDomains,trueValue:"TRUE", falseValue:"FALSE",
							visibilityChecks:[],enableDisableChecks:[]
						},

						{ type:_GROUP_, id: "BulkProvUpload",
							colSpan: 2, numCols: 1, colSizes: "*", items : [
								{ type:_OUTPUT_, ref:ZaBulkProvision.A2_provAction, align: _LEFT_,
									getDisplayValue:function(val) {
										var fileType = "CSV"	
										if(val == ZaBulkProvision.ACTION_IMPORT_XML) {
											fileType = "XML";	
										}
										return AjxMessageFormat.format(com_zimbra_bulkprovision.UploadFileTitle,[fileType]);
									},bmolsnr:true
								},
								{ type:_SPACER_ , height: 10 },
                                { type:_OUTPUT_, 
									getDisplayValue:function(val) {
										return ZaBulkImportXWizard.getUploadFormHtml(val);
									},
									ref:ZaBulkProvision.A2_provAction,bmolsnr:true
								},
                                { type:_SPACER_ , height: 10 } ,
                                { type:_OUTPUT_, 
                                	getDisplayValue:function(val) {
										if(val == ZaBulkProvision.ACTION_IMPORT_XML) {
											return com_zimbra_bulkprovision.XML_uploadNotes;
										} else {
											return com_zimbra_bulkprovision.CSV_uploadNotes;
										}
									},
									ref:ZaBulkProvision.A2_provAction,bmolsnr:true
								} 
                            ]
						}
					]
				};
	cases.push(case_upload_file);

	var case_provision = {
		type:_CASE_, numCols:2, colSizes:["250px", "*"], 
		tabGroupKey:ZaBulkImportXWizard.STEP_PROVISION, caseKey:ZaBulkImportXWizard.STEP_PROVISION,
		items:[
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_status,label:com_zimbra_bulkprovision.ProcessStatus,
		    	   	getDisplayValue:function(val) {
		    	   		return ZaBulkImportXWizard.STATUS_LABELS[val];
		       		},bmolsnr:true
		       },
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_totalCount,label:com_zimbra_bulkprovision.TotalAccounts,bmolsnr:true},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_provisionedCount,label:com_zimbra_bulkprovision.ProvisionedAccounts,bmolsnr:true},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_skippedCount,label:com_zimbra_bulkprovision.Skipped,bmolsnr:true},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_errorCount,label:com_zimbra_bulkprovision.FailedAccounts,bmolsnr:true},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_progress,label:com_zimbra_bulkprovision.Progress,bmolsnr:true},
		       {type:_DWT_ALERT_, style:DwtAlert.WARNING,iconVisible:false,content:com_zimbra_bulkprovision.ProvisioningStatusNote,
					visibilityChecks:[ZaBulkImportXWizard.isProvisioningNoteVisible],
					visibilityChangeEventSources:[ZaBulkProvision.A2_status],
					enableDisableChecks:[],colSpan:2
		       },
		       {type:_DWT_ALERT_, style:DwtAlert.INFO,iconVisible:false,content:com_zimbra_bulkprovision.ProvisioningSuccessReportsNote,
					visibilityChangeEventSources:[ZaBulkProvision.A2_status],
					visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_status,ZaBulkProvision.iSTATUS_FINISHED]],colSpan:2
		       }, 
		       {type:_DWT_ALERT_, style:DwtAlert.CRITICAL,iconVisible:false,content:com_zimbra_bulkprovision.ProvisioningFailedReportsNote,
					visibilityChangeEventSources:[ZaBulkProvision.A2_status],
					visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_status,ZaBulkProvision.iSTATUS_ERROR]],colSpan:2
		       },
		       {type:_DWT_ALERT_, style:DwtAlert.WARNING,iconVisible:false,content:com_zimbra_bulkprovision.ProvisioningWithErrorsReportsNote,
					visibilityChangeEventSources:[ZaBulkProvision.A2_status,ZaBulkProvision.A2_errorCount],
					visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_status,ZaBulkProvision.iSTATUS_FINISHED],
					                     [XForm.checkInstanceValueNot,ZaBulkProvision.A2_errorCount,0]],colSpan:2
		       },
		       {type:_DWT_ALERT_, style:DwtAlert.WARNING,iconVisible:false,content:com_zimbra_bulkprovision.ProvisioningAbortedReportsNote,
					visibilityChangeEventSources:[ZaBulkProvision.A2_status],
					visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_status,ZaBulkProvision.iSTATUS_ABORTED]],colSpan:2
		       },
		       {type:_DATA_URL_,labelLocation:_NONE_,label:com_zimbra_bulkprovision.DownloadSuccessReportLink,
		    	   ref:ZaBulkProvision.A2_completedAccountsFileLink,bmolsnr:true,
		    	   visibilityChangeEventSources:[ZaBulkProvision.A2_completedAccountsFileLink],
		    	   visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_completedAccountsFileLink]]
		       },
		       {type:_DATA_URL_,labelLocation:_NONE_,label:com_zimbra_bulkprovision.DownloadErrorReportLink,
			    	   ref:ZaBulkProvision.A2_failedAccountsFileLink,bmolsnr:true,
			    	   visibilityChangeEventSources:[ZaBulkProvision.A2_failedAccountsFileLink],
			    	   visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_failedAccountsFileLink]]
		       }
		    	   
		]
	};

	cases.push(case_provision);

	var case_summary =
		{type:_CASE_, numCols:1, colSizes:["*"],
            tabGroupKey:ZaBulkImportXWizard.STEP_SUMMARY, caseKey:ZaBulkImportXWizard.STEP_SUMMARY,
			align:_LEFT_, valign:_TOP_ ,
			items :[
				{ type:_OUTPUT_, value: com_zimbra_bulkprovision.summary_download }
            ]
		}

	cases.push (case_summary) ;

	
    var contentW = 630 ;
    xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep,
                choices:this.stepChoices, valueChangeEventSources:[ZaModel.currentStep]},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:contentW, align:_LEFT_, valign:_TOP_, items:cases}
		];
};
ZaXDialog.XFormModifiers["ZaBulkImportXWizard"].push(ZaBulkImportXWizard.myXFormModifier);

ZaBulkImportXWizard.downloadBPStatus = function () {
    var form = this.getForm() ;
    var instance = form.getInstance () ;
    
    return ;
}
