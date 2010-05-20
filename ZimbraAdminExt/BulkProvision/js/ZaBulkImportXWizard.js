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
//ZaBulkImportXWizard
function ZaBulkImportXWizard (parent, entry) {
    var w = "650px" ;
    ZaXWizardDialog.call(this, parent, null, com_zimbra_bulkprovision.BP_Wizard_title,
                                w, (AjxEnv.isIE ? "330px" :"320px"),"ZaBulkImportXWizard");

	this.stepChoices = [
	    {label:com_zimbra_bulkprovision.BP_Wizard_chooseAction, value:ZaBulkImportXWizard.STEP_CHOOSE_ACTION},
	    {label:com_zimbra_bulkprovision.BP_Wizard_upload, value:ZaBulkImportXWizard.STEP_UPLOAD_FILE},
	    {label:com_zimbra_bulkprovision.BP_Wizard_bulkProvOptions, value:ZaBulkImportXWizard.STEP_PROV_OPTIONS},
	    {label:com_zimbra_bulkprovision.BP_Wizard_ldapOptions, value:ZaBulkImportXWizard.STEP_LDAP_INFO},
	    {label:com_zimbra_bulkprovision.BP_Wizard_download, value:ZaBulkImportXWizard.STEP_DOWNLOAD_FILE},		
		{label:com_zimbra_bulkprovision.BP_Wizard_provision, value:ZaBulkImportXWizard.STEP_PROVISION},
		{label:com_zimbra_bulkprovision.BP_Wizard_summary, value:ZaBulkImportXWizard.STEP_SUMMARY}
	];

	this.uploadInputs = {} ;
	this.uploadResults = null ;

	ZaBulkImportXWizard.STATUS_LABELS = {};
	
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_IDLE] = com_zimbra_bulkprovision.ProvisioningStatusIdle;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_STARTED] = com_zimbra_bulkprovision.ProvisioningStatusStarted;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_CREATING_ACCOUNTS] = com_zimbra_bulkprovision.ProvisioningStatusCreatingAccounts;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_FINISHED] = com_zimbra_bulkprovision.ProvisioningStatusFinished;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_ABORT] = com_zimbra_bulkprovision.ProvisioningStatusAborting;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_ABORTED] = com_zimbra_bulkprovision.ProvisioningStatusAborted;
	ZaBulkImportXWizard.STATUS_LABELS[ZaBulkProvision.iSTATUS_ERROR] = com_zimbra_bulkprovision.ProvisioningStatusError;
	
    this.initForm(ZaBulkProvision.getMyXModel(),this.getMyXForm(entry),null);

  	this._helpURL = ZaBulkImportXWizard.helpURL;
}

// -1 : No status, 0: Install succeed, >0 : Install Failed (different number is different error)
ZaBulkImportXWizard.INSTALL_STATUS = -1;
ZaBulkImportXWizard.STEP_INDEX = 1;
ZaBulkImportXWizard.STEP_CHOOSE_ACTION = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_UPLOAD_FILE = ZaBulkImportXWizard.STEP_INDEX++;
ZaBulkImportXWizard.STEP_PROV_OPTIONS = ZaBulkImportXWizard.STEP_INDEX++;
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

ZaBulkImportXWizard.prototype.popup =
function (loc) {
    ZaXWizardDialog.prototype.popup.call(this, loc);

	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
}


ZaBulkImportXWizard.prototype._uploadCallback =
function (status, uploadResults) {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if ((status == AjxPost.SC_OK) && (uploadResults != null) && (uploadResults.length > 0)) {
    	var v = uploadResults[0] ;
        if (v.aid != null && v.aid.length > 0) {
           this._containedObject [ZaBulkProvision.A_csv_aid] =  v.aid ;
        } else {
           this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_upload_csv_no_aid, null, null, true);
           return ;
        }
        //File is uploaded successfully
        try {
            var resp = ZaBulkProvision.getBulkProvisionAccounts(this._app, this._containedObject );
            if (resp.aid == this._containedObject[ZaBulkProvision.A_csv_aid]) {
                this._containedObject[ZaBulkProvision.A_provision_accounts] =
                                                ZaBulkProvision.initProvisionAccounts (resp.accounts) ;
                this._containedObject[ZaBulkProvision.A_isValidCSV] = resp[ZaBulkProvision.A_isValidCSV] ;
            }else{
                throw new AjxException(com_zimbra_bulkprovision.error_unmatching_aid) ;
            }
        }catch (ex) {
            if (ex.code == ZaBulkProvision.TOO_MANY_ACCOUNTS)  {
                this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_TOO_MANY_ACCOUNTS, ex, true);
            }else{
                this._app.getCurrentController()._handleException(ex) ;
            }
            return ;
        }
        this.goPage(ZaBulkImportXWizard.STEP_PROVISION);
	} else {
		// handle errors during attachment upload.
		var msg = AjxMessageFormat.format(com_zimbra_bulkprovision.error_upload_csv, [status]);
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

ZaBulkImportXWizard.prototype.generateBulkFileCallback = 
function(resp) {
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);	
	this._localXForm.setInstanceValue(ZaBulkProvision.A2_generatedFileLink,"");
};

ZaBulkImportXWizard.prototype.goNext =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;

	if(cStep == ZaBulkImportXWizard.STEP_CHOOSE_ACTION) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_CSV || 
				this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_XML) {
			this.goPage(ZaBulkImportXWizard.STEP_UPLOAD_FILE);
		} else {
			this.goPage(ZaBulkImportXWizard.STEP_PROV_OPTIONS);
		}
	} else if(cStep == ZaBulkImportXWizard.STEP_PROV_OPTIONS) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this.goPage(ZaBulkImportXWizard.STEP_LDAP_INFO);
	} else if(cStep == ZaBulkImportXWizard.STEP_LDAP_INFO) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_MIG_XML || 
				this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_BULK_XML ||
				this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_MIG_CSV) {

			this.goPage(ZaBulkImportXWizard.STEP_DOWNLOAD_FILE);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			/**
			 * Generate the file and launch a callback when file is ready
			 */
			var callback = new AjxCallback(this, this.generateBulkFileCallback);
			ZaBulkProvision.generateBulkProvisionFile(this._containedObject,callback);
		} else if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_LDAP) {
			/**
			 * Start import
			 */
			this.goPage(ZaBulkImportXWizard.STEP_PROVISION);
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
                            com_zimbra_bulkprovision.error_no_csv_file_specified
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
        try {
            um.execute(csvUploadCallback, document.getElementById (ZaBulkImportXWizard.csvUploadFormId));
        }catch (err) {
            this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_no_csv_file_specified) ;
        }
        return ; //allow the callback to handle the wizard buttons
    } else if (cStep == ZaBulkImportXWizard.STEP_PROVISION) {
    	this.goPage(ZaBulkImportXWizard.STEP_SUMMARY);
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
		prevStep = ZaBulkImportXWizard.STEP_PROV_OPTIONS ;
    } else if(cStep == ZaBulkImportXWizard.STEP_DOWNLOAD_FILE || cStep == ZaBulkImportXWizard.STEP_PROVISION) {
    	prevStep = ZaBulkImportXWizard.STEP_LDAP_INFO;
    }

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
function (){
	//var uri = location.protocol + "//" + document.domain + appContextPath
	//							+ "/../service/upload";
	//need the full content of the response.
	//200,'1',[{"filename":"zimbra.crt","aid":"0466544c-1372-4cc3-ad8e-b1ff570dccca:85f82c13-6381-4c84-8915-bfbe515fdbd4","ct":"application/x-x509-ca-cert"},{"filename":"mycert.crt","aid":"0466544c-1372-4cc3-ad8e-b1ff570dccca:2321870e-1229-4359-a9c7-f11222a50042","ct":"application/x-x509-ca-cert"}]

	var uri = appContextPath + "/../service/upload?fmt=extended";
	var html = [];
	var idx = 0;
	html[idx++] = "<div style='height:50px;width: 500px; overflow:auto;'><form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaBulkImportXWizard.csvUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'>" ;
	html[idx++] = "<div><table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;
	//html[idx++] = "<colgroup><col width=50 /><col width='*' /><col width=50 /></colgroup>";

	html[idx++] = "<tbody><tr><td width=65>" + com_zimbra_bulkprovision.CSV_Upload_file + "</td>";
	html[idx++] = "<td><input type=file  name='bulkFile' size='45'></input></td></tr>";

	html[idx++] = "</tbody></table></div>";

	html[idx++] = "</form></div>";

	return html.join("");
}

ZaBulkImportXWizard.myXFormModifier = function(xFormObject,entry) {
	var cases = new Array();

	var case_choose_action = {type:_CASE_,numCols:2,colSizes:["100px","*"],
		tabGroupKey:ZaBulkImportXWizard.STEP_CHOOSE_ACTION,caseKey:ZaBulkImportXWizard.STEP_CHOOSE_ACTION,
		items:[
		       {type:_DWT_ALERT_, style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.SelectAction,visibilityChecks:[]},
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
						this.getElement().checked = (newValue == ZaBulkProvision.ACTION_GENERATE_BULK_SCV);
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
		type:_CASE_, numCols:2, colSizes:["200px","*"],tabGroupKey:ZaBulkImportXWizard.STEP_PROV_OPTIONS, caseKey:ZaBulkImportXWizard.STEP_PROV_OPTIONS,
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
					numCols:2, colSizes:["200px","*"],colSpan:2,
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
	 * Enter LDAP info for generating bulk file or direct import
	 */
	var case_ldap_info = {
		type:_CASE_, numCols:2, colSizes:["200px","*"],tabGroupKey:ZaBulkImportXWizard.STEP_LDAP_INFO, caseKey:ZaBulkImportXWizard.STEP_LDAP_INFO,
		items:[
		       	{type:_DWT_ALERT_, style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.LdapInfoStepNote},
		       	{ref:ZaBulkProvision.A2_bulkImportDomainName, type:_DYNSELECT_,
					label:com_zimbra_bulkprovision.DomainName,
					toolTipContent:ZaMsg.tt_StartTypingDomainName,
					dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains,
					dataFetcherClass:ZaSearch,editable:true,
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
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerName, width:"200px"},
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerPort,  width:"40px"},	
						{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALUseSSL, width:"*"}									
					]
				},		       
				{ref:ZaBulkProvision.A2_GalLdapURL, type:_LDAPURL_, label:com_zimbra_bulkprovision.LDAPUrl,ldapSSLPort:"3269",ldapPort:"3268",  labelLocation:_LEFT_,
					visibilityChecks:[],enableDisableChecks:[]
				},
				{ref:ZaBulkProvision.A2_GalLdapBindDn, type:_INPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, 
					enableDisableChecks:[],visibilityChecks:[]				
				},
				{ref:ZaBulkProvision.A2_GalLdapBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, 
					enableDisableChecks:[],visibilityChecks:[]				
				},
				{ref:ZaBulkProvision.A2_GalLdapBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPasswordConfirm, labelLocation:_LEFT_, 
					enableDisableChecks:[],visibilityChecks:[]				
				},				
				{ref:ZaBulkProvision.A2_GalLdapFilter, type:_TEXTAREA_, width:380, height:40, 
					label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, 
					enableDisableChecks:[],visibilityChecks:[]
				},
				{ref:ZaBulkProvision.A2_GalLdapSearchBase, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapSearchBase, 
					labelLocation:_LEFT_, enableDisableChecks:[],visibilityChecks:[]
				}					
		]
	};
	cases.push(case_ldap_info);
	
	/**
	 * Download generated bulk file
	 */
	var case_download_file = {
		type:_CASE_, numCols:2, colSizes:["200px","*"], tabGroupKey:ZaBulkImportXWizard.STEP_DOWNLOAD_FILE, 
		caseKey:ZaBulkImportXWizard.STEP_DOWNLOAD_FILE,
		items:[
		       {type:_DWT_ALERT_, style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.ClickToDownloadGeneratedFile},
		       {type:_DATA_URL_,label:com_zimbra_bulkprovision.GeneratedBulkProvisionFileLink,ref:ZaBulkProvision.A2_generatedFileLink}
		]
	};
	cases.push(case_download_file);
	
	/**
	 * Upload bulk file
	 */
	var case_upload_file = {type:_CASE_, numCols:2, colSizes:["200px","*"],
        tabGroupKey:ZaBulkImportXWizard.STEP_UPLOAD_FILE, caseKey:ZaBulkImportXWizard.STEP_UPLOAD_FILE,
					items: [

						{ type:_GROUP_, id: "BulkProvUpload",
							colSpan: 2, numCols: 1, colSizes: "*", items : [
								{ type:_OUTPUT_, ref:ZaBulkProvision.A2_provAction, align: _LEFT_,
									getDisplayValue:function(val) {
										var fileType = "CSV"	
										if(val == ZaBulkProvision.ACTION_IMPORT_XML) {
											fileType = "XML";	
										}
										return AjxMessageFormat.format(com_zimbra_bulkprovision.UploadFileTitle,[fileType]);
									}
								},
								{ type:_SPACER_ , height: 10 },
                                { type:_OUTPUT_, value: ZaBulkImportXWizard.getUploadFormHtml() } ,
                                { type:_SPACER_ , height: 10 } ,
                                { type:_OUTPUT_, value: com_zimbra_bulkprovision.CSV_uploadNotes } 
                            ]
						}
					]
				};
	cases.push(case_upload_file);

	var case_provision = {
		type:_CASE_, numCols:2, colSizes:["200px", "*"], 
		tabGroupKey:ZaBulkImportXWizard.STEP_PROVISION, caseKey:ZaBulkImportXWizard.STEP_PROVISION,
		items:[
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_status,
		    	   	getDisplayValue:function(val) {
		    	   		return ZaBulkImportXWizard.STATUS_LABELS[val];
		       		}
		       },
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_totalCount,label:com_zimbra_bulkprovision.TotalAccounts},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_finishedCount,label:com_zimbra_bulkprovision.ProvisionedAccounts},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_errorCount,label:com_zimbra_bulkprovision.FailedAccounts},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_progress,label:com_zimbra_bulkprovision.Progress,
		    	   getDisplayValue:function(val) { return val+"%"; }
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

	
    var contentW = 530 ;
    if (AjxEnv.isIE) {
        var contentW = 580 ;
    }
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
