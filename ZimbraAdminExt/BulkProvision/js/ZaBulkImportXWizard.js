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
    ZaXWizardDialog.call(this, parent, null, com_zimbra_bulkprovision.BP_Wizard_title_new,
                                w, (AjxEnv.isIE ? "330px" :"320px"),"ZaBulkImportXWizard");

    ZaBulkImportXWizard.xmlUploadFormId = Dwt.getNextId();
	this.stepChoices = [
	    {label:com_zimbra_bulkprovision.BP_Wizard_overview, value:ZaBulkImportXWizard.STEP_CHOOSE_ACTION},
	    {label:com_zimbra_bulkprovision.BP_Wizard_bulkProvOptions, value:ZaBulkImportXWizard.STEP_PROV_OPTIONS},
	    {label:com_zimbra_bulkprovision.BP_Wizard_ldapOptions, value:ZaBulkImportXWizard.STEP_LDAP_INFO},
	    {label:com_zimbra_bulkprovision.BP_Wizard_File_Upload, value:ZaBulkImportXWizard.STEP_FILE_UPLOAD},
	    {label:com_zimbra_bulkprovision.BP_Wizard_review, value:ZaBulkImportXWizard.STEP_REVIEW},		
		{label:com_zimbra_bulkprovision.BP_Wizard_provision, value:ZaBulkImportXWizard.STEP_PROVISION}
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

  	this._helpURL = [location.pathname, ZaUtil.HELP_URL, ZaBulkImportXWizard.helpURL, "?locid=", AjxEnv.DEFAULT_LOCALE].join(""); 
}
ZaBulkImportXWizard.POLL_INTERVAL = 500;
// -1 : No status, 0: Install succeed, >0 : Install Failed (different number is different error)
ZaBulkImportXWizard.INSTALL_STATUS = -1;
ZaBulkImportXWizard.STEP_INDEX = 1;
ZaBulkImportXWizard.STEP_CHOOSE_ACTION = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_PROV_OPTIONS = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_LDAP_INFO = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_FILE_UPLOAD = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_REVIEW = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_PROVISION = ZaBulkImportXWizard.STEP_INDEX++;

ZaBulkImportXWizard.prototype = new ZaXWizardDialog;
ZaBulkImportXWizard.prototype.constructor = ZaBulkImportXWizard;

ZaXDialog.XFormModifiers["ZaBulkImportXWizard"] = new Array();
ZaBulkImportXWizard.helpURL = "appliance/zap_importing_accounts.htm";

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
    if(this.prevCallback) {
    	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
    } else {
    	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
    }
}

ZaBulkImportXWizard.prototype.goNext =
function() {
	var cStep = this._containedObject[ZaModel.currentStep];

	if(cStep == ZaBulkImportXWizard.STEP_CHOOSE_ACTION) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
		if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_XML) {
			this.goPage(ZaBulkImportXWizard.STEP_FILE_UPLOAD);
		} else if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_AD
				|| this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_LDAP) {
			this.goPage(ZaBulkImportXWizard.STEP_PROV_OPTIONS);
		} else if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_ZIMBRA) {
			this._containedObject[ZaBulkProvision.A2_GalLdapBindDn] = "uid=zimbra,cn=admins,cn=zimbra";
			this._containedObject[ZaBulkProvision.A2_GalLdapFilter] = "(objectclass=zimbraAccount)";
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
		this.goPage(ZaBulkImportXWizard.STEP_LDAP_INFO);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
	} else if(cStep == ZaBulkImportXWizard.STEP_LDAP_INFO) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);

		/**
		 * Check that LDAP URL is not empty
		 */
		if(AjxUtil.isEmpty(this._containedObject[ZaBulkProvision.A2_GalLdapURL])) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_LDAP_URL_REQUIRED);
			return;
		}		

		/**
		 * Check that Bind DN is not empty
		 */
		if(AjxUtil.isEmpty(this._containedObject[ZaBulkProvision.A2_GalLdapBindDn])) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_BIND_DN_REQUIRED);
			return;
		}		


		/**
		 * Check that passwords match
		 */
		if(this._containedObject[ZaBulkProvision.A2_GalLdapBindPassword] != this._containedObject[ZaBulkProvision.A2_GalLdapConfirmBindPassword]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_PASSWORDS_DONT_MATCH);
			return;
		}		
		

		/**
		 * Check that LDAP filter is not empty
		 */
		if(AjxUtil.isEmpty(this._containedObject[ZaBulkProvision.A2_GalLdapFilter])) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_LDAP_FILTER_REQUIRED);
			return;
		}
		
		/**
		 * Check that LDAP search base is not empty
		 */
		if(AjxUtil.isEmpty(this._containedObject[ZaBulkProvision.A2_GalLdapSearchBase])) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_LDAP_BASE_REQUIRED);
			return;
		}		

		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);

		var callback = new AjxCallback(this, ZaBulkImportXWizard.prototype.previewCallback,{});
		ZaBulkProvision.generateBulkProvisionPreview(this._containedObject,callback);
	} else if(cStep == ZaBulkImportXWizard.STEP_FILE_UPLOAD) {
		//if using a bulk file - upload the file, the callbacks will move to the next step
        //1. check if the file name are valid and exists
        //2. upload the file
        var formEl = document.getElementById(ZaBulkImportXWizard.xmlUploadFormId);
        var inputEls = formEl.getElementsByTagName("input") ;

        var filenameArr = [];
        for (var i=0; i < inputEls.length; i++){
            if (inputEls[i].type == "file") {
                var n = inputEls[i].name ;
                var v = ZaBulkImportXWizard.getFileName(inputEls[i].value) ;
                if ( n == "xmlFile") {
                    if (v == null || v.length <= 0) {
                        this._app.getCurrentController().popupErrorDialog (
                            com_zimbra_bulkprovision.error_no_bulk_file_specified
                        );
                        return ;
                    }

                    //have a file, ready to upload
                    break ;
                }
            }
        }

        //2. Upload the files
        this.setUploadManager(new AjxPost(this.getUploadFrameId()));
        var xmlUploadCallback = new AjxCallback(this, this._uploadCallback);
        var um = this.getUploadManager() ;
        window._uploadManager = um;
        try {
    		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
    		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
    		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
    		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
            um.execute(xmlUploadCallback, document.getElementById (ZaBulkImportXWizard.xmlUploadFormId));
        } catch (err) {
    		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
    		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
    		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
    		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
            this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_no_bulk_file_specified) ;
        }
		
	} else if(cStep == ZaBulkImportXWizard.STEP_REVIEW) {
		/**
		 * Start import
		 */
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		if(this.finishCallback) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		} else {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		}
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
		this._localXForm.setInstanceValue(ZaBulkProvision.iSTATUS_STARTING,ZaBulkProvision.A2_status);
		this.goPage(ZaBulkImportXWizard.STEP_PROVISION);
		if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_XML) {
        	this._containedObject[ZaBulkProvision.A2_op] = ZaBulkProvision.OP_START_IMPORT;
        	this._containedObject[ZaBulkProvision.A2_sourceType] = ZaBulkProvision.SOURCE_TYPE_XML;			
    		var callback = new AjxCallback(this, ZaBulkImportXWizard.prototype.importFromFileCallback,{});
    		ZaBulkProvision.importAccountsFromFile(this._containedObject,callback);
		} else if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_AD
				|| this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_LDAP) {
			this.goPage(ZaBulkImportXWizard.STEP_FILE_UPLOAD);
			var callback = new AjxCallback(this, ZaBulkImportXWizard.prototype.importCallback,{action:this._containedObject[ZaBulkProvision.A2_provAction]});
			ZaBulkProvision.importAccountsFromLDAP(this._containedObject,callback);
		}
	} else if(cStep ==  ZaBulkImportXWizard.STEP_PROVISION) {
		if(this.finishCallback) {
			this.finishCallback.run(this._containedObject);
		}		
	}
}

ZaBulkImportXWizard.prototype.goPrev =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	var prevStep ;
	
	if (cStep == ZaBulkImportXWizard.STEP_PROV_OPTIONS ) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		if(!this.prevCallback) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		}
		prevStep = ZaBulkImportXWizard.STEP_CHOOSE_ACTION ;
    } else if (cStep == ZaBulkImportXWizard.STEP_LDAP_INFO) {
    	prevStep = ZaBulkImportXWizard.STEP_PROV_OPTIONS;
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
    } else if(cStep == ZaBulkImportXWizard.STEP_REVIEW) {
    	if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_XML) {
    		prevStep = ZaBulkImportXWizard.STEP_FILE_UPLOAD;
    	} else {
    		prevStep = ZaBulkImportXWizard.STEP_LDAP_INFO;
    	}
    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
    } else if(cStep == ZaBulkImportXWizard.STEP_PROVISION) {
   		prevStep = ZaBulkImportXWizard.STEP_REVIEW;
    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
    } else if(this.prevCallback && cStep == ZaBulkImportXWizard.STEP_CHOOSE_ACTION) {
    	this.prevCallback.run(this._containedObject);
    	return;
    }
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
    this.goPage(prevStep);
}

/**
 * server callbacks
 */
ZaBulkImportXWizard.prototype.previewCallback = function(params,resp) {
	try {
		if(resp && resp.isException()) {
			throw(resp.getException());
		} else {
			var response = resp.getResponse().Body.BulkImportAccountsResponse;
			if(!response) {
				response = resp.getResponse().Body.GenerateBulkProvisionFileFromLDAPResponse;
			}
			var accountCount = "0";
			var domainCount = "0";
			var skippedDomainCount = "0";
			var skippedCount = "0";
			if(response[ZaBulkProvision.A2_domainCount] && response[ZaBulkProvision.A2_domainCount][0] && response[ZaBulkProvision.A2_domainCount][0]._content) {
				domainCount = parseInt(response[ZaBulkProvision.A2_domainCount][0]._content);
			}				
			if(response[ZaBulkProvision.A2_skippedDomainCount] && response[ZaBulkProvision.A2_skippedDomainCount][0] && response[ZaBulkProvision.A2_skippedDomainCount][0]._content) {
				skippedDomainCount = parseInt(response[ZaBulkProvision.A2_skippedDomainCount][0]._content);
			}		
			if(response[ZaBulkProvision.A2_totalCount] && response[ZaBulkProvision.A2_totalCount][0] && response[ZaBulkProvision.A2_totalCount][0]._content) {
				totalCount = parseInt(response[ZaBulkProvision.A2_totalCount][0]._content);
			}
			if(response[ZaBulkProvision.A2_skippedAccountCount] && response[ZaBulkProvision.A2_skippedAccountCount][0] && response[ZaBulkProvision.A2_skippedAccountCount][0]._content) {
				skippedCount = parseInt(response[ZaBulkProvision.A2_skippedAccountCount][0]._content);
			}	
			
			this.goPage(ZaBulkImportXWizard.STEP_REVIEW);
			
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
			
			this._localXForm.setInstanceValue(domainCount,ZaBulkProvision.A2_domainCount);
			this._localXForm.setInstanceValue(skippedDomainCount,ZaBulkProvision.A2_skippedDomainCount);
			this._localXForm.setInstanceValue(totalCount,ZaBulkProvision.A2_totalCount);
			this._localXForm.setInstanceValue(skippedCount,ZaBulkProvision.A2_skippedAccountCount);
		}
	} catch (ex) {
		if(ex.code == ZaBulkProvision.BP_INVALID_SEARCH_FILTER) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(com_zimbra_bulkprovision.ERROR_INVALID_SEARCH_FILTER,[ex.msg]),ex);	
		} else if(ex.code == ZaBulkProvision.BP_NAMING_EXCEPTION) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(com_zimbra_bulkprovision.BP_NAMING_EXCEPTION,[ex.msg]),ex);	
		} else {
			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaBulkImportXWizard.prototype.previewCallback");
		}
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
	}
};

ZaBulkImportXWizard.prototype._uploadCallback =
function (status, attId) {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if(status == AjxPost.SC_OK) {
        if (attId != null && attId.length > 0) {
           this._containedObject [ZaBulkProvision.A_aid] =  attId;
        } else {
           this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_upload_bulk_no_aid);
           return ;
        }
        //File is uploaded successfully
        try {
        	//generate a preview of options, skip STEP_IMAP_OPTIONS, because these options should be in the XML
        	this._containedObject[ZaBulkProvision.A2_op] = ZaBulkProvision.OP_PREVIEW;
        	this._containedObject[ZaBulkProvision.A2_sourceType] = ZaBulkProvision.SOURCE_TYPE_XML;
    		var callback = new AjxCallback(this, ZaBulkImportXWizard.prototype.previewCallback,{});
    		ZaBulkProvision.importAccountsFromFile(this._containedObject,callback);
        } catch (ex) {
            this._app.getCurrentController()._handleException(ex) ;
            return ;
        }
	} else {
		// handle errors during attachment upload.
		var msg = AjxMessageFormat.format(com_zimbra_bulkprovision.error_upload_bulk, [status]);
		this._app.getCurrentController().popupErrorDialog(msg);
	}
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);	
};


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

ZaBulkImportXWizard.prototype.importFromFileCallback = function(params,resp) {
	if(this.finishCallback) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	} else {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	}
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	this._button[DwtDialog.CANCEL_BUTTON].setEnabled(false);
	try {
		if(resp && resp.isException()) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaBulkImportXWizard.prototype.importFromFileCallback");
		} else {
			var response = resp.getResponse().Body.BulkImportAccountsResponse;
			this.processBulkImportResponse(response);
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaBulkImportXWizard.prototype.importFromFileCallback");	
	}
};

ZaBulkImportXWizard.prototype.importCallback = function(params,resp) {
	if(this.finishCallback) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	} else {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	}

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
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaBulkImportXWizard.prototype.importCallback");	
	}
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
};

/**
 * Upload manager
 */
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

ZaBulkImportXWizard.getFileName = function (fullPath) {
    if (fullPath == null) return null ;

    var lastIndex = 0;
    if (AjxEnv.isWindows) {
        lastIndex = fullPath.lastIndexOf("\\") ;
    }else{
        lastIndex = fullPath.lastIndexOf("/") ;
    }

    return fullPath.substring(lastIndex + 1) ;
};

ZaBulkImportXWizard.csvUploadFormId = Dwt.getNextId();

ZaBulkImportXWizard.getUploadFormHtml =
function (fileType){
	ZaBulkImportXWizard.attachmentInputId = Dwt.getNextId();	
	var uri = appContextPath + "/../service/upload";
	var html = [];
	var idx = 0;
	html[idx++] = "<div style='height:50px;width: 500px; overflow:auto;'>";
	html[idx++] = "<table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;

	html[idx++] = "<tbody><tr><td width=65>" + com_zimbra_bulkprovision.XML_Upload_file + "</td>";
	html[idx++] = "<td>";
	html[idx++] = "<form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaBulkImportXWizard.xmlUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'>" ;
	html[idx++] = "<input type=file  name='bulkFile' size='45' id='";
	html[idx++] = ZaBulkImportXWizard.attachmentInputId;
	html[idx++] = "'></input></form></td></tr>";
	html[idx++] = "</tbody></table>";
	html[idx++] = "</div>";

	return html.join("");
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

/**
 * XForm helper methods
 */
ZaBulkImportXWizard.isActionSubGroupVisible = function() {
	var action = this.getInstanceValue(ZaBulkProvision.A2_provAction);
	return (action == ZaBulkProvision.ACTION_IMPORT_LDAP || action == ZaBulkProvision.ACTION_IMPORT_XML ||
			action == ZaBulkProvision.ACTION_IMPORT_CSV || action == ZaBulkProvision.ACTION_GENERATE_BULK_XML
			|| action == ZaBulkProvision.ACTION_GENERATE_BULK_CSV);
}

ZaBulkImportXWizard.isProvisioningNoteVisible = function() {
	var status = this.getInstanceValue(ZaBulkProvision.A2_status);
	return (status == ZaBulkProvision.iSTATUS_STARTED || status == ZaBulkProvision.iSTATUS_CREATING_ACCOUNTS);
};

ZaBulkImportXWizard.myXFormModifier = function(xFormObject,entry) {
	var cases = new Array();

	var case_choose_action = {type:_CASE_,numCols:2,colSizes:["100px","*"],
		tabGroupKey:ZaBulkImportXWizard.STEP_CHOOSE_ACTION,caseKey:ZaBulkImportXWizard.STEP_CHOOSE_ACTION,
		items:[
		       {type:_DWT_ALERT_,colSpan:2,style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.AccountImportWizardOverview,visibilityChecks:[]},
		       {type:_RADIO_, groupname:"action_selection_group",ref:ZaBulkProvision.A2_provAction,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.ActionImportAccountsFromAD,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.ACTION_IMPORT_AD);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.ACTION_IMPORT_AD,ZaBulkProvision.A2_provAction);
					},visibilityChecks:[],enableDisableChecks:[]
		       },		       
		       {type:_RADIO_, groupname:"action_selection_subgroup",ref:ZaBulkProvision.A2_provAction,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.ActionImportFromLDAP,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.ACTION_IMPORT_LDAP);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.ACTION_IMPORT_LDAP,ZaBulkProvision.A2_provAction);
					},visibilityChecks:[],enableDisableChecks:[]
		    	},
			    {type:_RADIO_, groupname:"action_selection_group",ref:ZaBulkProvision.A2_provAction,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.ActionImportFromZimbra,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.ACTION_IMPORT_ZIMBRA);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.ACTION_IMPORT_ZIMBRA,ZaBulkProvision.A2_provAction);
					},visibilityChecks:[],enableDisableChecks:[]
		       },		    	
			   {type:_RADIO_, groupname:"action_selection_group",ref:ZaBulkProvision.A2_provAction,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.ActionImportFromXML,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.ACTION_IMPORT_XML);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.ACTION_IMPORT_XML,ZaBulkProvision.A2_provAction);
					},visibilityChecks:[],enableDisableChecks:[]
		       },
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
		       	{type:_DWT_ALERT_, style:DwtAlert.WARNING,iconVisible:false,content:com_zimbra_bulkprovision.GeneratePasswordsNote,
					visibilityChecks:[],enableDisableChecks:[],
					visibilityChangeEventSources:[]
		       	},				
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
				}

		]
	};
	cases.push(case_prov_options);
	
	/**
	 * Enter LDAP info for generating bulk file or direct import
	 */
	var case_ldap_info = {
		type:_CASE_, numCols:2, colSizes:["250px","380px"],tabGroupKey:ZaBulkImportXWizard.STEP_LDAP_INFO, caseKey:ZaBulkImportXWizard.STEP_LDAP_INFO,
		items:[
		       	{type:_DWT_ALERT_, style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.LdapInfoStepNote},
				{ref:ZaBulkProvision.A2_createDomains,  type:_CHECKBOX_,  
					label:com_zimbra_bulkprovision.A2_createDomains,trueValue:"TRUE", falseValue:"FALSE",
					visibilityChecks:[],enableDisableChecks:[]
				},
		       	{ref:ZaBulkProvision.A2_maxResults, type:_TEXTFIELD_,cssClass:"admin_xform_number_input", 
		       		label:com_zimbra_bulkprovision.LDAPMaxResults, labelLocation:_LEFT_,labelWrap:true,
		       		visibilityChecks:[],enableDisableChecks:[]
		       	},
		       	{type:_GROUP_, numCols:6, colSpan:2,label:"   ",labelLocation:_LEFT_,
					visibilityChecks:[],
					items: [
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:com_zimbra_bulkprovision.LDAP_GALServerName, width:"200px"},
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:com_zimbra_bulkprovision.LDAP_GALServerPort,  width:"40px"},	
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:com_zimbra_bulkprovision.LDAP_GALUseSSL, width:"*"}									
					]
				},		       
				{ref:ZaBulkProvision.A2_GalLdapURL, type:_LDAPURL_, label:com_zimbra_bulkprovision.LDAPUrl,ldapSSLPort:"3269",ldapPort:"3268",  labelLocation:_LEFT_,
					visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_provAction,ZaBulkProvision.ACTION_IMPORT_AD]],enableDisableChecks:[],
					visibilityChangeEventSources:[ZaBulkProvision.A2_provAction]
				},
				{ref:ZaBulkProvision.A2_GalLdapURL, type:_LDAPURL_, label:com_zimbra_bulkprovision.LDAPUrl,ldapSSLPort:"636",ldapPort:"389",  labelLocation:_LEFT_,
					visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_provAction,ZaBulkProvision.ACTION_IMPORT_LDAP]],enableDisableChecks:[],
					visibilityChangeEventSources:[ZaBulkProvision.A2_provAction]
				},				
				{ref:ZaBulkProvision.A2_GalLdapURL, type:_LDAPURL_, label:com_zimbra_bulkprovision.LDAPUrl,ldapSSLPort:"636",ldapPort:"389",  labelLocation:_LEFT_,
					visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_provAction,ZaBulkProvision.ACTION_IMPORT_ZIMBRA]],enableDisableChecks:[],
					visibilityChangeEventSources:[ZaBulkProvision.A2_provAction]
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
	 * File upload
	 */
	var case_file_upload = {type:_CASE_,numCols:2,colSizes:["250px","380px"],tabGroupKey:ZaBulkImportXWizard.STEP_FILE_UPLOAD,
			caseKey:ZaBulkImportXWizard.STEP_FILE_UPLOAD,
			items:[
			       {type:_OUTPUT_, value: ZaBulkImportXWizard.getUploadFormHtml(), colSpan:2}
			]
	};
	cases.push(case_file_upload);

	/**
	 * Review options for provisioning
	 */
	var case_review = {
		type:_CASE_, numCols:2, colSizes:["200px","*"], caseKey:ZaBulkImportXWizard.STEP_REVIEW,
		items:[
		       {type:_DWT_ALERT_, style:DwtAlert.INFO,iconVisible:false,content:com_zimbra_bulkprovision.AccountImportReviewNote,visibilityChecks:[],enableDisableChecks:[]},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_domainCount,label:com_zimbra_bulkprovision.ReviewDomainCount,bmolsnr:true},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_skippedDomainCount,label:com_zimbra_bulkprovision.ReviewSkippedDomainCount,bmolsnr:true},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_totalCount,label:com_zimbra_bulkprovision.ReviewAccountCount,bmolsnr:true},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_skippedAccountCount,label:com_zimbra_bulkprovision.ReviewSkippedAccountCount,bmolsnr:true},
		       {ref:ZaBulkProvision.A2_generatePassword,  type:_OUTPUT_,  
		    	   label:com_zimbra_bulkprovision.RevGenerateRandomPassword,visibilityChecks:[],enableDisableChecks:[],
		    	   getDisplayValue:function(val) {	return val=="TRUE" ? ZaMsg.Yes : ZaMsg.No; 	}
		       },
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_genPasswordLength,label:com_zimbra_bulkprovision.GeneratedPasswordLength,
				   visibilityChecks:[],enableDisableChangeEventSources:[ZaBulkProvision.A2_generatePassword],
				   enableDisableChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_generatePassword,"TRUE"]]
			   },
		       {ref:ZaBulkProvision.A_mustChangePassword,  type:_OUTPUT_,  
		    	   label:com_zimbra_bulkprovision.RevRequireChangePassword,visibilityChecks:[],enableDisableChecks:[],
		    	   getDisplayValue:function(val) {	return val=="TRUE" ? ZaMsg.Yes : ZaMsg.No; 	}
		       }

		]
	};
	cases.push(case_review);
	
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
