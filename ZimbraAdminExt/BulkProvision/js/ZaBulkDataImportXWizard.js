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
* @class ZaBulkDataImportXWizard
* @contructor ZaBulkDataImportXWizard
* @param parent DwtShell
* @param entry ZaBulkProvision
* @author Greg Solovyev
**/
function ZaBulkDataImportXWizard (parent,entry) {
    var w = "650px" ;
    ZaXWizardDialog.call(this, parent, null, com_zimbra_bulkprovision.BulkDataImport_Wiz_title, w, (AjxEnv.isIE ? "330px" :"320px"),"ZaBulkDataImportXWizard");

	ZaBulkDataImportXWizard.xmlUploadFormId = Dwt.getNextId();
	this.stepChoices = [
	    {label:com_zimbra_bulkprovision.DataImport_Wizard_Intro, value:ZaBulkDataImportXWizard.STEP_INTRODUCTION},
	    {label:com_zimbra_bulkprovision.DataImport_Wizard_Options, value:ZaBulkDataImportXWizard.STEP_OPTIONS},
	    {label:com_zimbra_bulkprovision.DataImport_File_Upload, value:ZaBulkDataImportXWizard.STEP_FILE_UPLOAD},
	    {label:com_zimbra_bulkprovision.DataImport_Wizard_AcctPicker, value:ZaBulkDataImportXWizard.STEP_ACCT_PICKER},
	    {label:com_zimbra_bulkprovision.DataImport_IMAP_Info, value:ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS},
	    {label:com_zimbra_bulkprovision.DataImport_Review, value:ZaBulkDataImportXWizard.STEP_REVIEW},
	    {label:com_zimbra_bulkprovision.DataImport_Finish, value:ZaBulkDataImportXWizard.STEP_FINISH}		
	];
	
	this.yesNoChoices = [{value:"TRUE", label:ZaMsg.Yes}, {value:"FALSE", label:ZaMsg.No}];
	this.initForm(ZaBulkProvision.getMyXModel(),this.getMyXForm(entry),null);

  	this._helpURL = [location.pathname, ZaUtil.HELP_URL, ZaBulkDataImportXWizard.helpURL, "?locid=", AjxEnv.DEFAULT_LOCALE].join(""); 
  	
	
}
ZaBulkDataImportXWizard.STEP_INDEX = 1;
ZaBulkDataImportXWizard.STEP_INTRODUCTION = ZaBulkDataImportXWizard.STEP_INDEX++;
ZaBulkDataImportXWizard.STEP_OPTIONS = ZaBulkDataImportXWizard.STEP_INDEX++;
ZaBulkDataImportXWizard.STEP_FILE_UPLOAD = ZaBulkDataImportXWizard.STEP_INDEX++;
ZaBulkDataImportXWizard.STEP_ACCT_PICKER = ZaBulkDataImportXWizard.STEP_INDEX++;
ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS = ZaBulkDataImportXWizard.STEP_INDEX++;
ZaBulkDataImportXWizard.STEP_REVIEW = ZaBulkDataImportXWizard.STEP_INDEX++;
ZaBulkDataImportXWizard.STEP_FINISH = ZaBulkDataImportXWizard.STEP_INDEX++;

ZaBulkDataImportXWizard.prototype = new ZaXWizardDialog;
ZaBulkDataImportXWizard.prototype.constructor = ZaBulkDataImportXWizard;

ZaXDialog.XFormModifiers["ZaBulkDataImportXWizard"] = new Array();
ZaBulkDataImportXWizard.helpURL = "appliance/zap_importing_accounts.htm";

/**
* @method setObject sets the object contained in the view
* @param entry -  object to display
**/
ZaBulkDataImportXWizard.prototype.setObject =
function(entry) {
	this._containedObject = entry ;
	//this._containedObject[ZaModel.currentStep] = ZaBulkDataImportXWizard.STEP_INTRODUCTION;
    this._localXForm.setInstance(this._containedObject);
}

ZaBulkDataImportXWizard.prototype.startImportCallback = function(params, resp) {
	if(resp && resp.isException()) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);			
		ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaBulkDataImportXWizard.prototype.previewCallback");
	} else {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(false);
		this.goPage(ZaBulkDataImportXWizard.STEP_FINISH);
	}	
}

ZaBulkDataImportXWizard.prototype.previewCallback = function(params, resp) {
	if(resp && resp.isException()) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);			
		ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaBulkDataImportXWizard.prototype.previewCallback");
	} else {
		var response = resp.getResponse().Body.BulkIMAPDataImportResponse;
		var totalAccounts = 0;
		var idleAccounts = null;
		var finishedAccounts = null;
		var runningAccounts = null;
		var useAdminLogin = 0;
		if(response[ZaBulkProvision.A2_totalCount] && response[ZaBulkProvision.A2_totalCount][0] && response[ZaBulkProvision.A2_totalCount][0]._content) {
			totalAccounts = response[ZaBulkProvision.A2_totalCount][0]._content;
		}
		this._localXForm.setInstanceValue(totalAccounts,ZaBulkProvision.A2_totalCount);
		
		if(response[ZaBulkProvision.A2_runningCount] && response[ZaBulkProvision.A2_runningCount][0] && response[ZaBulkProvision.A2_runningCount][0]._content) {
			runningAccounts = response[ZaBulkProvision.A2_runningCount][0]._content;
			if(runningAccounts) {
				this._localXForm.setInstanceValue(runningAccounts,ZaBulkProvision.A2_runningCount);
			}
		}
		
		if(response[ZaBulkProvision.A2_idleCount] && response[ZaBulkProvision.A2_idleCount][0] && response[ZaBulkProvision.A2_idleCount][0]._content) {
			idleAccounts = response[ZaBulkProvision.A2_idleCount][0]._content;
			if(idleAccounts) {
				this._localXForm.setInstanceValue(idleAccounts,ZaBulkProvision.A2_idleCount);
			}
		}		
		
		if(response[ZaBulkProvision.A2_finishedCount] && response[ZaBulkProvision.A2_finishedCount][0] && response[ZaBulkProvision.A2_finishedCount][0]._content) {
			finishedAccounts = response[ZaBulkProvision.A2_finishedCount][0]._content;
			if(finishedAccounts) {
				this._localXForm.setInstanceValue(finishedAccounts,ZaBulkProvision.A2_finishedCount);
			}
		}				
		
		if(response[ZaBulkProvision.A2_IMAPHost] && response[ZaBulkProvision.A2_IMAPHost][0] && response[ZaBulkProvision.A2_IMAPHost][0]._content) {
			this._localXForm.setInstanceValue(response[ZaBulkProvision.A2_IMAPHost][0]._content,ZaBulkProvision.A2_IMAPHost);
		}		
		if(response[ZaBulkProvision.A2_IMAPPort] && response[ZaBulkProvision.A2_IMAPPort][0] && response[ZaBulkProvision.A2_IMAPPort][0]._content) {
			this._localXForm.setInstanceValue(response[ZaBulkProvision.A2_IMAPPort][0]._content,ZaBulkProvision.A2_IMAPPort);
		}			
		if(response[ZaBulkProvision.A2_connectionType] && response[ZaBulkProvision.A2_connectionType][0] && response[ZaBulkProvision.A2_connectionType][0]._content) {
			this._localXForm.setInstanceValue(response[ZaBulkProvision.A2_connectionType][0]._content,ZaBulkProvision.A2_connectionType);
		}
		if(response[ZaBulkProvision.A2_useAdminLogin] && response[ZaBulkProvision.A2_useAdminLogin][0] && response[ZaBulkProvision.A2_useAdminLogin][0]._content) {
			useAdminLogin = parseInt(response[ZaBulkProvision.A2_useAdminLogin][0]._content);
		}
		if(useAdminLogin > 0) {
			if(response[ZaBulkProvision.A2_IMAPAdminLogin] && response[ZaBulkProvision.A2_IMAPAdminLogin][0] && response[ZaBulkProvision.A2_IMAPAdminLogin][0]._content) {
				this._localXForm.setInstanceValue(response[ZaBulkProvision.A2_IMAPAdminLogin][0]._content,ZaBulkProvision.A2_IMAPAdminLogin);
			}
			if(response[ZaBulkProvision.A2_IMAPAdminPassword] && response[ZaBulkProvision.A2_IMAPAdminPassword][0] && response[ZaBulkProvision.A2_IMAPAdminPassword][0]._content) {
				this._localXForm.setInstanceValue(response[ZaBulkProvision.A2_IMAPAdminPassword][0]._content,ZaBulkProvision.A2_IMAPAdminPassword);
			}
			this._localXForm.setInstanceValue(1,ZaBulkProvision.A2_useAdminLogin);
		} else {
			this._localXForm.setInstanceValue(0,ZaBulkProvision.A2_useAdminLogin);
		}
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);			
		this.goPage(params.nextStep);
	}
}

ZaBulkDataImportXWizard.getFileName = function (fullPath) {
    if (fullPath == null) return null ;

    var lastIndex = 0;
    if (AjxEnv.isWindows) {
        lastIndex = fullPath.lastIndexOf("\\") ;
    }else{
        lastIndex = fullPath.lastIndexOf("/") ;
    }

    return fullPath.substring(lastIndex + 1) ;
}

//upload the file
ZaBulkDataImportXWizard.prototype.getUploadFrameId =
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

ZaBulkDataImportXWizard.prototype.getUploadManager =
function() {
	return this._uploadManager;
};

/**
 * @params uploadManager is the AjxPost object
 */
ZaBulkDataImportXWizard.prototype.setUploadManager =
function(uploadManager) {
	this._uploadManager = uploadManager;
};

ZaBulkDataImportXWizard.prototype.popup = function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
    if(this.prevCallback) {
    	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
    } else {
    	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
    }
    this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
}

ZaBulkDataImportXWizard.prototype.goNext = function() {
	var cStep = this._containedObject[ZaModel.currentStep];
	if(cStep == ZaBulkDataImportXWizard.STEP_OPTIONS && this._containedObject[ZaBulkProvision.A2_sourceType] == ZaBulkProvision.SOURCE_TYPE_XML &&
			this._containedObject[ZaBulkProvision.A2_sourceServerType] == ZaBulkProvision.MAIL_SOURCE_TYPE_IMAP) {
		this.goPage(ZaBulkDataImportXWizard.STEP_FILE_UPLOAD);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
	} else if(cStep == ZaBulkDataImportXWizard.STEP_OPTIONS && 
			this._containedObject[ZaBulkProvision.A2_sourceType] == ZaBulkProvision.SOURCE_TYPE_ZIMBRA &&
			this._containedObject[ZaBulkProvision.A2_sourceServerType] == ZaBulkProvision.MAIL_SOURCE_TYPE_IMAP) {
		this._containedObject[ZaBulkProvision.A2_activateSearch] = 1;
		this.goPage(ZaBulkDataImportXWizard.STEP_ACCT_PICKER);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
	} else if (cStep == ZaBulkDataImportXWizard.STEP_OPTIONS && this._containedObject[ZaBulkProvision.A2_sourceType] == ZaBulkProvision.SOURCE_TYPE_REUSE_XML &&
			this._containedObject[ZaBulkProvision.A2_sourceServerType] == ZaBulkProvision.MAIL_SOURCE_TYPE_IMAP) {
		
        try {
        	//generate a preview of options, skip STEP_IMAP_OPTIONS, because these options should be in the XML
        	this._containedObject[ZaBulkProvision.A2_op] = ZaBulkProvision.OP_PREVIEW;
        	this._containedObject[ZaBulkProvision.A2_sourceType] = ZaBulkProvision.SOURCE_TYPE_XML;
    		var callback = new AjxCallback(this, ZaBulkDataImportXWizard.prototype.previewCallback,{nextStep:ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS});
        	ZaBulkProvision.bulkDataIMport(this._containedObject,callback);
        } catch (ex) {
            this._app.getCurrentController()._handleException(ex) ;
            return ;
        }
		
	} else if(cStep == ZaBulkDataImportXWizard.STEP_INTRODUCTION && this._containedObject[ZaBulkProvision.A2_sourceServerType] == ZaBulkProvision.MAIL_SOURCE_TYPE_EXCHANGE) {  
		//open Exchange Migration wizard
		var prevCallback = new AjxCallback(this._app.getCurrentController(), 
					ZaBulkProvisionTasksController.prototype.bulkDataImportListener,
					["migrationWizard"]);
		ZaBulkProvisionTasksController.prototype.openMigrationWizard.call(this._app.getCurrentController(),{prevCallback:prevCallback,hideWiz:"bulkDataImportWizard"},null);
	} else if(cStep == ZaBulkDataImportXWizard.STEP_INTRODUCTION && this._containedObject[ZaBulkProvision.A2_sourceServerType] == ZaBulkProvision.MAIL_SOURCE_TYPE_IMAP &&
			this._containedObject[ZaBulkProvision.A2_provisionUsers] == "TRUE") {
		//open Account Import wizard
		var auxObj1 = new ZaBulkProvision ();
		auxObj1[ZaModel.currentStep] = ZaBulkDataImportXWizard.STEP_OPTIONS;
		auxObj1[ZaBulkProvision.A2_sourceServerType] = this._containedObject[ZaBulkProvision.A2_sourceServerType];
		auxObj1[ZaBulkProvision.A2_provisionUsers] = "FALSE";
		auxObj1[ZaBulkProvision.A2_importEmail] = "TRUE";
		auxObj1[ZaBulkProvision.A2_sourceType] = ZaBulkProvision.SOURCE_TYPE_ZIMBRA;
		auxObj1[ZaBulkProvision.A2_activateSearch] = 1;
		var finishCallback = new AjxCallback(this._app.getCurrentController(), 
					ZaBulkProvisionTasksController.prototype.bulkDataImportListener,
					["importAccountsWizard", auxObj1]);
		
		var auxObj2 = new ZaBulkProvision ();
		auxObj2[ZaModel.currentStep] = cStep;
		auxObj2[ZaBulkProvision.A2_sourceServerType] = this._containedObject[ZaBulkProvision.A2_sourceServerType];
		auxObj2[ZaBulkProvision.A2_provisionUsers] = this._containedObject[ZaBulkProvision.A2_provisionUsers];
		auxObj2[ZaBulkProvision.A2_importEmail] = this._containedObject[ZaBulkProvision.A2_importEmail];
		auxObj2[ZaBulkProvision.A2_sourceType] = this._containedObject[ZaBulkProvision.A2_sourceType];
		var prevCallback = new AjxCallback(this._app.getCurrentController(), 
				ZaBulkProvisionTasksController.prototype.bulkDataImportListener,
				["importAccountsWizard",auxObj2]);
		ZaBulkProvisionTasksController.prototype.openBulkProvisionDialog.call(this._app.getCurrentController(),{prevCallback:prevCallback,finishCallback:finishCallback,hideWiz:"bulkDataImportWizard"},null);
	} else if(cStep == ZaBulkDataImportXWizard.STEP_INTRODUCTION && this._containedObject[ZaBulkProvision.A2_sourceServerType] == ZaBulkProvision.MAIL_SOURCE_TYPE_IMAP &&
			this._containedObject[ZaBulkProvision.A2_provisionUsers] == "FALSE" && this._containedObject[ZaBulkProvision.A2_importEmail] == "TRUE") {
		this._containedObject[ZaBulkProvision.A2_sourceType] = ZaBulkProvision.SOURCE_TYPE_ZIMBRA;
		this.goPage(ZaBulkDataImportXWizard.STEP_OPTIONS);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
	} else if(cStep == ZaBulkDataImportXWizard.STEP_INTRODUCTION &&  this._containedObject[ZaBulkProvision.A2_provisionUsers] == "FALSE" &&
			this._containedObject[ZaBulkProvision.A2_importEmail] == "FALSE") {
		ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_PLEASE_SELECT_YES);
		return;
		
	} else if(cStep == ZaBulkDataImportXWizard.STEP_FILE_UPLOAD) {
		//if using a bulk file - upload the file, the callbacks will move to the next step
        //1. check if the file name are valid and exists
        //2. upload the file
        var formEl = document.getElementById(ZaBulkDataImportXWizard.xmlUploadFormId);
        var inputEls = formEl.getElementsByTagName("input") ;

        var filenameArr = [];
        for (var i=0; i < inputEls.length; i++){
            if (inputEls[i].type == "file") {
                var n = inputEls[i].name ;
                var v = ZaBulkDataImportXWizard.getFileName(inputEls[i].value) ;
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
            um.execute(xmlUploadCallback, document.getElementById (ZaBulkDataImportXWizard.xmlUploadFormId));
        }catch (err) {
            this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_no_bulk_file_specified) ;
        }
	} else if (cStep == ZaBulkDataImportXWizard.STEP_ACCT_PICKER) {
		if(AjxUtil.isEmpty(this._containedObject[ZaBulkProvision.A2_account])) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_MUST_SELECT_ACCOUNTS);
			return;
		}		
		this._containedObject[ZaBulkProvision.A2_useAdminLogin] = 1;
		this.goPage(ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS);
	} else if(cStep == ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS) {
    	//generate a preview of options, skip STEP_IMAP_OPTIONS, because these options should be in the XML
		if(this._containedObject[ZaBulkProvision.A2_sourceType] != ZaBulkProvision.SOURCE_TYPE_XML &&
			this._containedObject[ZaBulkProvision.A2_useAdminLogin] !=1 ) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.REQUIRED_TO_USE_ADMIN_CREDENTIALS);
			return;
		}
		
		if(this._containedObject[ZaBulkProvision.A2_useAdminLogin]==1) {
			if(AjxUtil.isEmpty(this._containedObject[ZaBulkProvision.A2_IMAPAdminLogin])) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_IMAP_ADMIN_LOGIN_REQUIRED);
				return;
			}
			if(AjxUtil.isEmpty(this._containedObject[ZaBulkProvision.A2_IMAPAdminPassword])) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_IMAP_ADMIN_PASSWORD_REQUIRED);
				return;
			}
			/**
			 * Check that passwords match
			 */
			if(this._containedObject[ZaBulkProvision.A2_IMAPAdminPassword] != this._containedObject[ZaBulkProvision.A2_IMAPAdminPasswordConfirm]) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_PASSWORDS_DONT_MATCH);
				return;
			}		
		}
		if(AjxUtil.isEmpty(this._containedObject[ZaBulkProvision.A2_IMAPHost])) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_IMAP_HOST_REQUIRED);
			return;
		}
		if(AjxUtil.isEmpty(this._containedObject[ZaBulkProvision.A2_IMAPPort])) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_IMAP_PORT_REQUIRED);
			return;
		}		
		if(AjxUtil.isEmpty(this._containedObject[ZaBulkProvision.A2_connectionType])) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_IMAP_CONNECTION_TYPE_REQUIRED);
			return;
		}		
		
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);			
    	this._containedObject[ZaBulkProvision.A2_op] = ZaBulkProvision.OP_PREVIEW;
		var callback = new AjxCallback(this, ZaBulkDataImportXWizard.prototype.previewCallback,{nextStep:ZaBulkDataImportXWizard.STEP_REVIEW});
    	ZaBulkProvision.bulkDataIMport(this._containedObject,callback);		
	} else if(cStep == ZaBulkDataImportXWizard.STEP_REVIEW) {
		var callback = new AjxCallback(this, ZaBulkDataImportXWizard.prototype.startImportCallback,{});
		this._containedObject[ZaBulkProvision.A2_op] = ZaBulkProvision.OP_START_IMPORT;
		ZaBulkProvision.bulkDataIMport(this._containedObject,callback);
	}
}

ZaBulkDataImportXWizard.prototype.goPrev =
	function() {
		var cStep = this._containedObject[ZaModel.currentStep] ;
		var prevStep ;
		if (cStep == ZaBulkDataImportXWizard.STEP_REVIEW) {
			prevStep = ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS;
	    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	    	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	    } else if (cStep == ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS) {
	    	if(this._containedObject[ZaBulkProvision.A2_sourceType] == ZaBulkProvision.SOURCE_TYPE_XML) {
				prevStep = ZaBulkDataImportXWizard.STEP_FILE_UPLOAD;	    		
	    	} else if(this._containedObject[ZaBulkProvision.A2_sourceType] == ZaBulkProvision.SOURCE_TYPE_ZIMBRA) {
	    		prevStep = ZaBulkDataImportXWizard.STEP_ACCT_PICKER;
	    	}
	    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	    	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	    } else if(cStep == ZaBulkDataImportXWizard.STEP_FILE_UPLOAD) {
	    	prevStep = ZaBulkDataImportXWizard.STEP_OPTIONS;
	    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	    	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	    } else if (cStep == ZaBulkDataImportXWizard.STEP_ACCT_PICKER) {
	    	prevStep = ZaBulkDataImportXWizard.STEP_OPTIONS;
	    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	    	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	    }  else if(cStep == ZaBulkDataImportXWizard.STEP_FINISH) {
			prevStep = ZaBulkDataImportXWizard.STEP_REVIEW;
	    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	    	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	    } else if(this.prevCallback && cStep == ZaBulkDataImportXWizard.STEP_INTRODUCTION) {
	    	this.prevCallback.run(null);
	    	return;
	    } else if(cStep == ZaBulkDataImportXWizard.STEP_OPTIONS) {
			prevStep = ZaBulkDataImportXWizard.STEP_INTRODUCTION;
	    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	    	if(this.prevCallback) {
	    		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	    	} else {
	    		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	    	}
	    } 
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
	    this.goPage(prevStep);
	}


ZaBulkDataImportXWizard.prototype._uploadCallback = function (status, attId) {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if (status == AjxPost.SC_OK) {
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
    		var callback = new AjxCallback(this, ZaBulkDataImportXWizard.prototype.previewCallback,{nextStep:ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS});
        	ZaBulkProvision.bulkDataIMport(this._containedObject,callback);
        } catch (ex) {
            this._app.getCurrentController()._handleException(ex) ;
            return ;
        }
	} else {
		// handle errors during attachment upload.
		var msg = AjxMessageFormat.format(com_zimbra_bulkprovision.error_upload_bulk, [status]);
		this._app.getCurrentController().popupErrorDialog(msg);
	}
}

ZaBulkDataImportXWizard.isAccountSourceLDAP = function() {
	var val = this.getModel().getInstanceValue(this.getInstance(),ZaBulkProvision.A2_sourceType);
	return (val == ZaBulkProvision.SOURCE_TYPE_LDAP || val == ZaBulkProvision.SOURCE_TYPE_AD)
}

ZaBulkDataImportXWizard.getUploadFormHtml = function (){
	var uri = appContextPath + "/../service/upload";
	var html = [];
	var idx = 0;
	html[idx++] = "<div style='height:50px;width: 500px; overflow:auto;'><form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaBulkDataImportXWizard.xmlUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'>" ;
	html[idx++] = "<div><table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;
	html[idx++] = "<tbody><tr><td width=65>" + com_zimbra_bulkprovision.XML_Upload_file + "</td>";
	html[idx++] = "<td><input type=file  name='xmlFile' size='45'></input></td></tr>";
	html[idx++] = "</tbody></table></div>";
	html[idx++] = "</form></div>";
	return html.join("");
}

/**
* method of an XFormItem
**/
ZaBulkDataImportXWizard.fwdPoolButtonHndlr = 
function(evt) {
	var currentPageNum = parseInt(this.getInstanceValue("/poolPagenum"));
	this.setInstanceValue(currentPageNum+1,"/poolPagenum");
	ZaBulkDataImportXWizard.srchButtonHndlr.call(this, evt);
}

/**
* method of an XFormItem
**/
ZaBulkDataImportXWizard.backPoolButtonHndlr = 
function(evt) {
	var currentPageNum = parseInt(this.getInstanceValue("/poolPagenum"))-1;
	this.setInstanceValue(currentPageNum,"/poolPagenum");
	ZaBulkDataImportXWizard.srchButtonHndlr.call(this, evt);
}

/**
* method of an XFormItem
**/
ZaBulkDataImportXWizard.srchButtonHndlr = function (ev) {
	var instance = this.getForm().getInstance();
	var formParent = this.getForm().parent;
	var params = {};
	params.types = [ZaSearch.ACCOUNTS];
	if(instance[ZaSearch.A_query]) {
		params.query = ZaSearch.getSearchByNameQuery (instance[ZaSearch.A_query]);
	} else {
		params.query = "";
	}
	params.limit = ZaSettings.RESULTSPERPAGE;
	if(!instance[ZaBulkProvision.A2_poolPagenum]) {
		instance[ZaBulkProvision.A2_poolPagenum] = 0;
	}
	params.offset = instance[ZaBulkProvision.A2_poolPagenum]*ZaSettings.RESULTSPERPAGE;
	params.attrs = [ZaItem.A_zimbraId,ZaAccount.A_uid,ZaAccount.A_displayname,ZaResource.A_zimbraCalResType].join(",");
	params.callback = new AjxCallback(formParent,ZaBulkDataImportXWizard.srchAccountsClbck);
	this.getModel().setInstanceValue(this.getInstance(),ZaBulkProvision.A2_activateSearch,0);
	//instance.activateSearch = 0;
	//this.getForm().setInstance(instance);
	ZaSearch.searchDirectory(params);
}

ZaBulkDataImportXWizard.srchAccountsClbck = function (resp) {
	var instance = this._localXForm.getInstance();
	try {
		if(!resp) {
			throw(new AjxException(ZaMsg.ERROR_EMPTY_RESPONSE_ARG, AjxException.UNKNOWN, "ZaBulkDataImportXWizard.srchAccountsClbck"));
		}
		if(resp.isException()) {
			throw(resp.getException());
		} else {
			var response = resp.getResponse().Body.SearchDirectoryResponse;
			var list = new ZaItemList(null);	
			list.loadFromJS(response);
			this._localXForm.setInstanceValue(list.getArray(),ZaBulkProvision.A2_accountPool);
			//instance[ZaBulkProvision.A2_accountPool] = list.getArray();
			var poolNumPages = Math.ceil(response.searchTotal/ZaSettings.RESULTSPERPAGE);
			this._localXForm.setInstanceValue(poolNumPages,ZaBulkProvision.A2_poolNumPages);
			//instance.poolNumPages = poolNumPages;
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaBulkDataImportXWizard.srchAccountsClbck");	
	}	
	
	this._localXForm.setInstanceValue(1,ZaBulkProvision.A2_activateSearch);
	//this._localXForm.setInstance(instance);	
}

ZaBulkDataImportXWizard.accPoolSelectionListener = function() {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaBulkProvision.A2_src_acct_selection_pool, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaBulkProvision.A2_src_acct_selection_pool, null);
	}	
}

ZaBulkDataImportXWizard.accTargetSelectionListener = function() {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaBulkProvision.A2_tgt_acct_selection_pool, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaBulkProvision.A2_tgt_acct_selection_pool, null);
	}	
}

ZaBulkDataImportXWizard.removeAllButtonHndlr = function (ev) {
	var form = this.getForm();
	var instance = form.getInstance();

	instance[ZaBulkProvision.A2_account] = new Array();
	var currentTargetList = instance[ZaBulkProvision.A2_account] ? instance[ZaBulkProvision.A2_account] : [];		
	if(currentTargetList.length > 0) {
		form.parent._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);		
	} else {
		form.parent._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);		
	}	
	this.getForm().setInstance(instance);	
}


ZaBulkDataImportXWizard.addAllButtonHndlr = function (ev) {
	var form = this.getForm();
	var instance = form.getInstance();
	var oldArr = instance[ZaBulkProvision.A2_account] ? instance[ZaBulkProvision.A2_account]  : [];
	var arr = instance[ZaBulkProvision.A2_accountPool];
	var arr2 = new Array();
	if(arr) {
		var cnt = arr.length;
		var oldCnt = oldArr.length;
		for(var ix=0; ix< cnt; ix++) {
			var found = false;
			for(var j = oldCnt-1;j>=0;j-- ) {
				if(oldArr[j].id == arr[ix].id) {
					found = true;
					break;
				}
			}
			if(!found)
				arr2.push(arr[ix]);
		}
	}
	arr2 = arr2.concat(oldArr);
	this.getModel().setInstanceValue(this.getInstance(), ZaBulkProvision.A2_account, arr2);
	this.getModel().setInstanceValue(this.getInstance(), ZaBulkProvision.A2_accountPool, new Array());
	//instance[ZaBulkProvision.A2_account] = arr2;
	//instance[ZaBulkProvision.A2_accountPool] = new Array();
	var instance = form.getInstance();
	var currentTargetList = instance[ZaBulkProvision.A2_account] ? instance[ZaBulkProvision.A2_account] : [];	
	if(currentTargetList.length > 0) {
		form.parent._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);		
	} else {
		form.parent._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);		
	}	
	//this.getForm().setInstance(instance);	
}

ZaBulkDataImportXWizard.addButtonHndlr = function (ev) {
	var form = this.getForm();
	var instance = form.getInstance();
	var sourceListItems = form.getItemsById(ZaBulkProvision.A2_accountPool);
	if(sourceListItems && (sourceListItems instanceof Array) && sourceListItems[0] && sourceListItems[0].widget) {
		var selection = sourceListItems[0].widget.getSelection();
		var currentTargetList = instance[ZaBulkProvision.A2_account] ? instance[ZaBulkProvision.A2_account] : [];
		var list = (selection instanceof AjxVector) ? selection.getArray() : (selection instanceof Array) ? selection : [selection];
		if(list) {
			list.sort(ZaItem.compareNamesDesc);		
			//currentTargetList.sort(ZaItem.compareNamesDesc);
			var tmpTargetList = [];
			var cnt2 = currentTargetList.length;			
			for(var i=0;i<cnt2;i++)				
				tmpTargetList.push(currentTargetList[i]);
				
			tmpTargetList.sort(ZaItem.compareNamesDesc);
			
			var tmpList = [];
			var cnt = list.length;
			for(var i=cnt-1; i>=0; i--) {
				var dup = false;
				cnt2 = tmpTargetList.length;				
				for(var j = cnt2-1; j >=0; j--) {
					if(list[i].name==tmpTargetList[j].name) {
						dup=true;
						tmpTargetList.splice(j,cnt2-j);
						break;
					}
				}
				if(!dup) {
					currentTargetList.push(list[i])
				}
			}
			this.getModel().setInstanceValue(this.getInstance(), ZaBulkProvision.A2_account, currentTargetList);
			//instance[ZaBulkProvision.A2_account] = currentTargetList;				
		}
	}
	if(currentTargetList.length > 0) {
		form.parent._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);		
	} else {
		form.parent._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);		
	}
	//this.getForm().setInstance(instance);	
}

ZaBulkDataImportXWizard.removeButtonHndlr = function (ev) {
	var form = this.getForm();
	var instance = form.getInstance();
	var targetListItems = form.getItemsById(ZaBulkProvision.A2_account);
	if(targetListItems && (targetListItems instanceof Array) && targetListItems[0] && targetListItems[0].widget) {
		var selection = targetListItems[0].widget.getSelection();
		
		var currentTargetList = instance[ZaBulkProvision.A2_account] ? instance[ZaBulkProvision.A2_account] : [];
		currentTargetList.sort(ZaItem.compareNamesDesc);
		var tmpTargetList = [];
		var list = (selection instanceof AjxVector) ? selection.getArray() : (selection instanceof Array) ? selection : [selection];
		if(list) {
			list.sort(ZaItem.compareNamesDesc);
			var cnt = list.length;
			var cnt2 = currentTargetList.length;
			for(var i=0;i<cnt2;i++)				
				tmpTargetList.push(currentTargetList[i]);
			
			for(var i=cnt-1; i>=0; i--) {
				var cnt2 = tmpTargetList.length;
				for(var j = cnt2-1; j >=0; j--) {
					if(list[i].name==tmpTargetList[j].name) {
						currentTargetList.splice(j,1);
						tmpTargetList.splice(j,cnt2-j);
					}
				}
			}
			this.getModel().setInstanceValue(this.getInstance(), ZaBulkProvision.A2_account, currentTargetList);
			//instance[ZaBulkProvision.A2_account] = currentTargetList;
		}
	}
	if(currentTargetList.length > 0) {
		form.parent._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);		
	} else {
		form.parent._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);		
	}
	//this.getForm().setInstance(instance);	
}


ZaBulkDataImportXWizard.addAllBtnEnabled = function () {
	return !AjxUtil.isEmpty(this.getInstanceValue(ZaBulkProvision.A2_accountPool));
}

ZaBulkDataImportXWizard.removeAllBtnEnabled = function () {
	return (this.instance && this.instance[ZaBulkProvision.A2_account] && this.instance[ZaBulkProvision.A2_account].length>0);
}

/**
* method of the XForm
**/
ZaBulkDataImportXWizard.forwardBtnEnabled = function () {
	return (parseInt(this.getInstanceValue(ZaBulkProvision.A2_poolPagenum)) < (parseInt(this.getInstanceValue(ZaBulkProvision.A2_poolNumPages))-1) && his.getInstanceValue(ZaBulkProvision.A2_activateSearch)==1);
};

/**
* method of the XForm
**/
ZaBulkDataImportXWizard.backBtnEnabled = function () {
	return (parseInt(this.getInstanceValue(ZaBulkProvision.A2_poolPagenum)) > 0 && this.getInstanceValue(ZaBulkProvision.A2_activateSearch)==1);
};

ZaBulkDataImportXWizard.myXFormModifier = function(xFormObject,entry) {
	var sourceHeaderList = new Array();
	var sortable=1;
	sourceHeaderList[0] = new ZaListHeaderItem("type", ZaMsg.ALV_Type_col, null, "34px", null, "objectClass", true, true);
	sourceHeaderList[1] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, "auto", null, ZaAccount.A_name, true, true);

	var cases = new Array();
	var case_intro = {type:_CASE_,numCols:2,colSizes:["250px","380px"],
		tabGroupKey:ZaBulkDataImportXWizard.STEP_OPTIONS,caseKey:ZaBulkDataImportXWizard.STEP_INTRODUCTION,
		items:[
		       {type:_DWT_ALERT_,colSpan:2,style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.DataImportWizardOverview,visibilityChecks:[]},
		       {ref:ZaBulkProvision.A2_sourceServerType, type:_OSELECT1_, label:com_zimbra_bulkprovision.SourceServerType,labelLocation:_LEFT_,visibilityChecks:[],enableDisableChecks:[]},
		       {type:_OSELECT1_,label:com_zimbra_bulkprovision.ImportAccountRecordsQ,ref:ZaBulkProvision.A2_provisionUsers,choices:this.yesNoChoices},
		       {type:_OSELECT1_,label:com_zimbra_bulkprovision.ImportAccountMailQ,ref:ZaBulkProvision.A2_importEmail,choices:this.yesNoChoices}
       ]
	};
	cases.push(case_intro);
	
	var case_options = {type:_CASE_,numCols:2,colSizes:["250px","380px"],
			tabGroupKey:ZaBulkDataImportXWizard.STEP_OPTIONS,caseKey:ZaBulkDataImportXWizard.STEP_OPTIONS,
			items:[
		       {type:_DWT_ALERT_,colSpan:2,style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.AccountListTypeNote,visibilityChecks:[]},
		       {type:_RADIO_, groupname:"source_selection_group",ref:ZaBulkProvision.A2_sourceType,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.PickAccounts,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.SOURCE_TYPE_ZIMBRA);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.SOURCE_TYPE_ZIMBRA,ZaBulkProvision.A2_sourceType);
					},visibilityChecks:[],enableDisableChecks:[]
		       },		       
		       {type:_RADIO_, groupname:"source_selection_group",ref:ZaBulkProvision.A2_sourceType,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.UploadAccountsInXML,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.SOURCE_TYPE_XML);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.SOURCE_TYPE_XML,ZaBulkProvision.A2_sourceType);
					},visibilityChecks:[],enableDisableChecks:[]
		       },		       
		       {type:_RADIO_, groupname:"source_selection_group",ref:ZaBulkProvision.A2_sourceType,bmolsnr:true,
					labelLocation:_RIGHT_,label:com_zimbra_bulkprovision.ReuseAccountsXML,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == ZaBulkProvision.SOURCE_TYPE_REUSE_XML);
					},
					elementChanged: function(elementValue,instanceValue, event) {
						this.setInstanceValue(ZaBulkProvision.SOURCE_TYPE_REUSE_XML,ZaBulkProvision.A2_sourceType);
					},visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A_aid]],enableDisableChecks:[], visibilityChangeEventSources:[ZaBulkProvision.A_aid]
		    	}
	       ]
		};
		cases.push(case_options);
	
	var case_file_upload = {type:_CASE_,numCols:2,colSizes:["250px","380px"],tabGroupKey:ZaBulkDataImportXWizard.STEP_FILE_UPLOAD,
			caseKey:ZaBulkDataImportXWizard.STEP_FILE_UPLOAD,
			items:[
			       {type:_OUTPUT_, value: ZaBulkDataImportXWizard.getUploadFormHtml(), colSpan:2}
			]
	};
	cases.push(case_file_upload);
	
	var case_acct_picker = {
			type:_CASE_,caseKey:ZaBulkDataImportXWizard.STEP_ACCT_PICKER,tabGroupKey:ZaBulkDataImportXWizard.STEP_ACCT_PICKER,numCols:2,colSizes:["250px","380px"],	
			items:[
				{type:_GROUP_, colSpan:2, numCols:3, width:"100%", colSizes:["170px","85px","170px"],cellspacing:"5px",
					items:[
						{type:_TEXTFIELD_, cssClass:"admin_xform_name_input",width:"160px", ref:ZaSearch.A_query, label:null,
					      elementChanged: function(elementValue,instanceValue, event) {
							  var charCode = event.charCode;
							  if (charCode == 13 || charCode == 3) {
								  ZaBulkDataImportXWizard.srchButtonHndlr.call(this);
							  } else {
							      this.getForm().itemChanged(this, elementValue, event);
							  }
				      		},
				      		visibilityChecks:[],enableDisableChecks:[]
						},
						{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonSearch, width:"80px",
						   onActivate:ZaBulkDataImportXWizard.srchButtonHndlr,align:_CENTER_,
							enableDisableChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_activateSearch,1]],
							enableDisableChangeEventSources:[ZaBulkProvision.A2_activateSearch]					   
						},
						{type:_OUTPUT_, value:com_zimbra_bulkprovision.AccountsForDataImport,visibilityChecks:[]},
	 				    {ref:ZaBulkProvision.A2_accountPool, type:_DWT_LIST_, height:"200px", width:"170px", 
	 				    	cssClass: "DLSource", bmolsnr:true,
	 				    	widgetClass:ZaAccMiniListView, headerList:sourceHeaderList,
	 				    	rowSpan:4,hideHeader:true,
	 				    	onSelection:ZaBulkDataImportXWizard.accPoolSelectionListener,
	 				    	visibilityChecks:[],enableDisableChecks:[]
	 				    },
						{type:_DWT_BUTTON_, label:AjxMsg.addAll, width:"80px",
						   onActivate:ZaBulkDataImportXWizard.addAllButtonHndlr,
							enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_accountPool]],
							enableDisableChangeEventSources:[ZaBulkProvision.A2_accountPool]						   
						},
	 				    {ref: ZaBulkProvision.A2_account, type:_DWT_LIST_, height:"200px", width:"170px", 
	 				    	cssClass: "DLSource", bmolsnr:true,
	 				    	widgetClass:ZaAccMiniListView, headerList:sourceHeaderList,
	 				    	rowSpan:5,hideHeader:true,
	 				    	onSelection:ZaBulkDataImportXWizard.accTargetSelectionListener,
	 				    	visibilityChecks:[],enableDisableChecks:[]
	 				    }, 
						{type:_DWT_BUTTON_, label:AjxMsg.add, width:"80px",
						   onActivate:ZaBulkDataImportXWizard.addButtonHndlr,bmolsnr:true,
						   enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_src_acct_selection_pool]],
						   enableDisableChangeEventSources:[ZaBulkProvision.A2_src_acct_selection_pool]
						}, 
						{type:_DWT_BUTTON_, label:AjxMsg.remove, width:"80px",
						   onActivate:ZaBulkDataImportXWizard.removeButtonHndlr,bmolsnr:true,
						   enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_tgt_acct_selection_pool]],
						   enableDisableChangeEventSources:[ZaBulkProvision.A2_tgt_acct_selection_pool]
						}, 
						{type:_DWT_BUTTON_, label:AjxMsg.removeAll, width:"80px",
						   onActivate:ZaBulkDataImportXWizard.removeAllButtonHndlr,
						   	enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_account]],
							enableDisableChangeEventSources:[ZaBulkProvision.A2_account]
						},
						{type:_GROUP_,numCols:3,colSizes:["80px","*","80px"], 
							items:[
								{type:_SPACER_, colSpan:3},
								{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, 
								   id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
								   onActivate:ZaBulkDataImportXWizard.backPoolButtonHndlr,align:_CENTER_,
								   enableDisableChecks:[ZaBulkDataImportXWizard.backBtnEnabled],
								   enableDisableChangeEventSources:[ZaBulkProvision.A2_poolPagenum,ZaBulkProvision.A2_activateSearch]							   
								},
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75,
								   id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis", 
								   onActivate:ZaBulkDataImportXWizard.fwdPoolButtonHndlr,align:_CENTER_,
								   enableDisableChecks:[ZaBulkDataImportXWizard.forwardBtnEnabled],
								   enableDisableChangeEventSources:[ZaBulkProvision.A2_poolPagenum,ZaBulkProvision.A2_activateSearch]
								}													
							]
						},
						{type:_CELLSPACER_},
						{type:_DWT_ALERT_,style: DwtAlert.INFORMATION, 
			 				iconVisible: true,
							content: ZaMsg.pleaseWaitSearching,width:"200px",
							align:_CENTER_,cssStyle:"position:relative;top:-200;left:125;opacity: 0.8",
							visibilityChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_activateSearch,0]],
							visibilityChangeEventSources:[ZaBulkProvision.A2_activateSearch]						
						}
					]
				}
			]
		};
	cases.push(case_acct_picker);
	var case_imap_options = {type:_CASE_, numCols:2, colSizes:["250px","380px"],tablGroupKey:ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS,caseKey:ZaBulkDataImportXWizard.STEP_IMAP_OPTIONS,
			items:[
			       {type:_TEXTFIELD_,label:com_zimbra_bulkprovision.IMAPHost, ref:ZaBulkProvision.A2_IMAPHost, visibilityChecls:[],enableDisableChecks:[]},
			       {type:_TEXTFIELD_,label:com_zimbra_bulkprovision.IMAPPort, ref:ZaBulkProvision.A2_IMAPPort, visibilityChecls:[],enableDisableChecks:[]},
			       {ref:ZaBulkProvision.A2_connectionType, type:_OSELECT1_, label:com_zimbra_bulkprovision.IMAPConnectionType,labelLocation:_LEFT_,
			    	   visibilityChecks:[],enableDisableChecks:[]
			       },
			       {ref:ZaBulkProvision.A2_useAdminLogin,  type:_CHECKBOX_,  
			    	   label:com_zimbra_bulkprovision.UseIMAPAdminCredentialsChkBx,trueValue:"1", falseValue:"0",visibilityChecks:[],enableDisableChecks:[]
			       },			       
			       {type:_TEXTFIELD_,label:com_zimbra_bulkprovision.IMAPAdminLogin, ref:ZaBulkProvision.A2_IMAPAdminLogin, visibilityChecls:[],
			    	   enableDisableChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_useAdminLogin,"1"]],
			    	   enableDisableChangeEventSources:[ZaBulkProvision.A2_useAdminLogin]
			       },
			       {type:_SECRET_,label:com_zimbra_bulkprovision.IMAPAdminPassword, ref:ZaBulkProvision.A2_IMAPAdminPassword, visibilityChecls:[],
			    	   enableDisableChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_useAdminLogin,"1"]],
			    	   enableDisableChangeEventSources:[ZaBulkProvision.A2_useAdminLogin]
			       },
			       {type:_SECRET_,label:com_zimbra_bulkprovision.IMAPAdminPasswordConfirm, ref:ZaBulkProvision.A2_IMAPAdminPasswordConfirm, visibilityChecls:[],
			    	   enableDisableChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_useAdminLogin,"1"]],
			    	   enableDisableChangeEventSources:[ZaBulkProvision.A2_useAdminLogin]
			       }
			]
	};
	cases.push(case_imap_options);

	
	var case_review = {type:_CASE_, numCols:2, colSizes:["250px","380px"],tablGroupKey:ZaBulkDataImportXWizard.STEP_REVIEW,caseKey:ZaBulkDataImportXWizard.STEP_REVIEW,
			items:[
			       {type:_OUTPUT_,label:com_zimbra_bulkprovision.TotalMailboxes, ref:ZaBulkProvision.A2_totalCount, visibilityChecks:[]},
			       //TODO: May want to show a warning saying that these accounts will be skipped
			       {type:_OUTPUT_,label:com_zimbra_bulkprovision.RunningMailboxes, ref:ZaBulkProvision.A2_runningCount, visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_runningCount]],
			    	   visibilityChangeEventSources:[ZaBulkProvision.A2_runningCount]
			       },
			       {type:_OUTPUT_,label:com_zimbra_bulkprovision.IdleMailboxes, ref:ZaBulkProvision.A2_idleCount, visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_idleCount]],
				    	   visibilityChangeEventSources:[ZaBulkProvision.A2_idleCount]
			       },
			       //TODO: May want to show a warning and ask if they want to re-run import on finished accounts
			       {type:_OUTPUT_,label:com_zimbra_bulkprovision.FinishedMaiboxes, ref:ZaBulkProvision.A2_finishedCount, visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaBulkProvision.A2_finishedCount]],
			    	   visibilityChangeEventSources:[ZaBulkProvision.A2_finishedCount]
			       },
			       {type:_OUTPUT_,label:com_zimbra_bulkprovision.IMAPHost, ref:ZaBulkProvision.A2_IMAPHost, visibilityChecls:[],enableDisableChecks:[]},
			       {type:_OUTPUT_,label:com_zimbra_bulkprovision.IMAPPort, ref:ZaBulkProvision.A2_IMAPPort, visibilityChecls:[],enableDisableChecks:[]},
			       {ref:ZaBulkProvision.A2_connectionType, type:_OUTPUT_, label:com_zimbra_bulkprovision.IMAPConnectionType,labelLocation:_LEFT_,
			    	   visibilityChecks:[],enableDisableChecks:[]
			       },
			       {ref:ZaBulkProvision.A2_useAdminLogin, type:_OUTPUT_,  
			    	   label:com_zimbra_bulkprovision.UseIMAPAdminCredentials,
			    	   getDisplayValue:function(val) {	return val=="1" ? ZaMsg.Yes : ZaMsg.No; 	}
			       },			       
			       {type:_OUTPUT_,label:com_zimbra_bulkprovision.IMAPAdminLogin, ref:ZaBulkProvision.A2_IMAPAdminLogin, visibilityChecls:[],
			    	   enableDisableChecks:[[XForm.checkInstanceValue,ZaBulkProvision.A2_useAdminLogin,"1"]],
			    	   enableDisableChangeEventSources:[ZaBulkProvision.A2_useAdminLogin]
			       },
			       {type:_DWT_ALERT_,colSpan:2,style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.ClickNextToStartImport,visibilityChecks:[]}
			]
	};
	cases.push(case_review);
	
	var case_finish = {type:_CASE_, numCols:2, colSizes:["250px","380px"],tablGroupKey:ZaBulkDataImportXWizard.STEP_FINISH,caseKey:ZaBulkDataImportXWizard.STEP_FINISH,
			items:[
			       {type:_DWT_ALERT_,colSpan:2,style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.DataImportStarted,visibilityChecks:[]}
			]
	};
	cases.push(case_finish);
    var contentW = 630;
    xFormObject.items = [
 			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep,
                choices:this.stepChoices, valueChangeEventSources:[ZaModel.currentStep]
 			},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:contentW, align:_LEFT_, valign:_TOP_, items:cases}
		];
	
}
ZaXDialog.XFormModifiers["ZaBulkDataImportXWizard"].push(ZaBulkDataImportXWizard.myXFormModifier);