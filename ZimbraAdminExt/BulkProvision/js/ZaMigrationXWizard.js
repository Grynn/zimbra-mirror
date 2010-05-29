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
* @class ZaMigrationXWizard
* @contructor ZaMigrationXWizard
* @param parent DwtShell
* @param entry ZaBulkProvision
* @author Greg Solovyev
**/
function ZaMigrationXWizard (parent, entry) {
    var w = "650px" ;
    ZaXWizardDialog.call(this, parent, null, com_zimbra_bulkprovision.BP_Exch_Mig_Wiz_Title,
                                w, (AjxEnv.isIE ? "330px" :"320px"),"ZaMigrationXWizard");

	this.stepChoices = [
	    {label:com_zimbra_bulkprovision.BP_Wizard_chooseAction, value:ZaMigrationXWizard.STEP_INTRODUCTION},
	    {label:com_zimbra_bulkprovision.BP_Wizard_bulkProvOptions, value:ZaMigrationXWizard.STEP_PROV_OPTIONS},
	    {label:com_zimbra_bulkprovision.BP_Wizard_exchangeOptions, value:ZaMigrationXWizard.STEP_EXCHANGE_INFO},
	    {label:com_zimbra_bulkprovision.BP_Wizard_ldapOptions, value:ZaMigrationXWizard.STEP_LDAP_INFO},
	    {label:com_zimbra_bulkprovision.BP_Wizard_ldapOptions, value:ZaMigrationXWizard.STEP_REVIEW},
	    {label:com_zimbra_bulkprovision.BP_Wizard_download, value:ZaMigrationXWizard.STEP_DOWNLOAD_FILE}		
	];
		
    this.initForm(ZaBulkProvision.getMyXModel(),this.getMyXForm(entry),null);

  	this._helpURL = ZaMigrationXWizard.helpURL;
}
ZaMigrationXWizard.POLL_INTERVAL = 500;
// -1 : No status, 0: Install succeed, >0 : Install Failed (different number is different error)
ZaMigrationXWizard.INSTALL_STATUS = -1;
ZaMigrationXWizard.STEP_INDEX = 1;
ZaMigrationXWizard.STEP_INTRODUCTION = ZaMigrationXWizard.STEP_INDEX++;
ZaMigrationXWizard.STEP_PROV_OPTIONS = ZaMigrationXWizard.STEP_INDEX++;
ZaMigrationXWizard.STEP_EXCHANGE_INFO = ZaMigrationXWizard.STEP_INDEX++;
ZaMigrationXWizard.STEP_LDAP_INFO = ZaMigrationXWizard.STEP_INDEX++;
ZaMigrationXWizard.STEP_REVIEW = ZaMigrationXWizard.STEP_INDEX++;
ZaMigrationXWizard.STEP_DOWNLOAD_FILE = ZaMigrationXWizard.STEP_INDEX++;

ZaMigrationXWizard.prototype = new ZaXWizardDialog;
ZaMigrationXWizard.prototype.constructor = ZaMigrationXWizard;

ZaXDialog.XFormModifiers["ZaMigrationXWizard"] = new Array();
ZaMigrationXWizard.helpURL = location.pathname + "help/admin/html/managing_accounts/how_to_provision_multiple_accounts.htm?locid=" + AjxEnv.DEFAULT_LOCALE;

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/

ZaMigrationXWizard.prototype.popdown = 
function () {
	if(this._pollHandler) {
		//stop polling
		AjxTimedAction.cancelAction(this._pollHandler);
	}
	DwtDialog.prototype.popdown.call(this);
}

ZaMigrationXWizard.prototype.popup =
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
    this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
}

ZaMigrationXWizard.prototype.generateBulkFileCallback = 
function(params,resp) {
	try {
		if(resp && resp.isException()) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaMigrationXWizard.prototype.generateBulkFileCallback");
		} else {
			var response = resp.getResponse().Body.GenerateBulkProvisionFileFromLDAPResponse;
			if(response.fileToken && response.fileToken[0] && response.fileToken[0]._content) {
				this._localXForm.setInstanceValue(
						AjxMessageFormat.format("{0}//{1}:{2}/service/afd/?action=getBulkFile&fileID={3}&fileFormat={4}",
								[location.protocol,location.hostname,location.port,response.fileToken[0]._content,ZaBulkProvision.FILE_FORMAT_MIGRATION_XML]),
								ZaBulkProvision.A2_generatedFileLink);
				this.goPage(ZaMigrationXWizard.STEP_DOWNLOAD_FILE);
				this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
				this._button[DwtDialog.CANCEL_BUTTON].setEnabled(false);
			}
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaMigrationXWizard.prototype.generateBulkFileCallback");	
	}
		
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);	
};

ZaMigrationXWizard.prototype.previewCallback = function(params,resp) {
	try {
		if(resp && resp.isException()) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			ZaApp.getInstance().getCurrentController()._handleException(resp.getException(), "ZaMigrationXWizard.prototype.previewCallback");
		} else {
			var response = resp.getResponse().Body.BulkImportPreviewResponse;
			var status = 0;
			var accountCount = 0;
			var domainCount = 0;
			var skippedDomainCount = 0;
			var skippedCount = 0;
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
			
			this.goPage(ZaMigrationXWizard.STEP_REVIEW);
			
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
			
			this._localXForm.setInstanceValue(totalCount,ZaBulkProvision.A2_totalCount);
			this._localXForm.setInstanceValue(provisionedCount,ZaBulkProvision.A2_provisionedCount);
			this._localXForm.setInstanceValue(domainCount,ZaBulkProvision.A2_provisionedCount);
			this._localXForm.setInstanceValue(skippedCount,ZaBulkProvision.A2_domainCount);
			
		}
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaMigrationXWizard.prototype.previewCallback");
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
	}
}

ZaMigrationXWizard.prototype.goNext =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;

	if(cStep == ZaMigrationXWizard.STEP_INTRODUCTION) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
		if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_CSV || 
				this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_XML) {
			this.goPage(ZaMigrationXWizard.STEP_UPLOAD_FILE);
		} else {
			this.goPage(ZaMigrationXWizard.STEP_PROV_OPTIONS);
		}
	} else if(cStep == ZaMigrationXWizard.STEP_PROV_OPTIONS) {
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
			this.goPage(ZaMigrationXWizard.STEP_EXCHANGE_INFO);
		} else {
			this.goPage(ZaMigrationXWizard.STEP_LDAP_INFO);
		}
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
	} else if(cStep == ZaMigrationXWizard.STEP_EXCHANGE_INFO) { 
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
		
		if(!this._containedObject[ZaBulkProvision.A2_GalLdapSearchBase] && typeof this._containedObject[ZaBulkProvision.A2_SourceDomainName] == "string") {
			var searchBase = "cn=Users,dc=" + this._containedObject[ZaBulkProvision.A2_SourceDomainName].replace(".",",dc=");
			this._localXForm.setInstanceValue(searchBase,ZaBulkProvision.A2_GalLdapSearchBase);
		}
		
		if(!this._containedObject[ZaBulkProvision.A2_GalLdapBindDn] && typeof this._containedObject[ZaBulkProvision.A2_SourceDomainName] == "string") {
			var bindDN = "cn=administrator,cn=Users,dc=" + this._containedObject[ZaBulkProvision.A2_SourceDomainName].replace(".",",dc=");
			this._localXForm.setInstanceValue(bindDN,ZaBulkProvision.A2_GalLdapBindDn);
		}
		
		this.goPage(ZaMigrationXWizard.STEP_LDAP_INFO);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
				
	} else if(cStep == ZaMigrationXWizard.STEP_LDAP_INFO) {
		/**
		 * Check that passwords match
		 */
		if(this._containedObject[ZaBulkProvision.A2_GalLdapBindPassword] != this._containedObject[ZaBulkProvision.A2_GalLdapConfirmBindPassword]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_PASSWORDS_DONT_MATCH);
			return;
		}		
		
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
		
		var callback = new AjxCallback(this, ZaMigrationXWizard.prototype.previewCallback,{});
		ZaBulkProvision.generateBulkProvisionPreview(this._containedObject,callback);

	} else if (cStep == ZaMigrationXWizard.STEP_REVIEW) {
		/**
		 * Generate the file and launch a callback when file is ready
		 */
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
		
		var callback = new AjxCallback(this, ZaMigrationXWizard.prototype.generateBulkFileCallback,{action:this._containedObject[ZaBulkProvision.A2_provAction]});
		ZaBulkProvision.generateBulkProvisionFile(this._containedObject,callback);
    } 
}

ZaMigrationXWizard.prototype.goPrev =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	var prevStep ;
	
	if (cStep == ZaMigrationXWizard.STEP_UPLOAD_FILE || cStep == ZaMigrationXWizard.STEP_PROV_OPTIONS) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);			
		prevStep = ZaMigrationXWizard.STEP_CHOOSE_ACTION ;
    } else if (cStep == ZaMigrationXWizard.STEP_LDAP_INFO) {
    	if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_GENERATE_MIG_XML) {
    		prevStep = ZaMigrationXWizard.STEP_EXCHANGE_INFO;
    	} else {
    		prevStep = ZaMigrationXWizard.STEP_PROV_OPTIONS;
    	}
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
    } else if (cStep == ZaMigrationXWizard.STEP_EXCHANGE_INFO) {
    	prevStep = ZaMigrationXWizard.STEP_PROV_OPTIONS;
    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
    } else if(cStep == ZaMigrationXWizard.STEP_DOWNLOAD_FILE || cStep == ZaMigrationXWizard.STEP_PROVISION) {
    	if(this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_CSV ||
    			this._containedObject[ZaBulkProvision.A2_provAction] == ZaBulkProvision.ACTION_IMPORT_XML) {
    		prevStep = ZaMigrationXWizard.STEP_UPLOAD_FILE;
    	} else {
    		prevStep = ZaMigrationXWizard.STEP_LDAP_INFO;
    	}
    	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
    }
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtDialog.CANCEL_BUTTON].setEnabled(true);
    this.goPage(prevStep);
}

ZaMigrationXWizard.prototype.goPage = function (pageKey) {
    ZaXWizardDialog.prototype.goPage.call(this, pageKey) ;
}

ZaMigrationXWizard.getFileName = function (fullPath) {
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
ZaMigrationXWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject = entry ;

	this._containedObject[ZaModel.currentStep] = ZaMigrationXWizard.STEP_CHOOSE_ACTION;
	this._containedObject[ZaBulkProvision.A_mustChangePassword] = "TRUE";
    this._localXForm.setInstance(this._containedObject);
}

ZaMigrationXWizard.csvUploadFormId = Dwt.getNextId();

ZaMigrationXWizard.getUploadFormHtml =
function (fileType){
	ZaMigrationXWizard.attachmentInputId = Dwt.getNextId();	
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
	html[idx++] = ZaMigrationXWizard.csvUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'>" ;
	html[idx++] = "<input type=file  name='bulkFile' size='45' id='";
	html[idx++] = ZaMigrationXWizard.attachmentInputId;
	html[idx++] = "'></input></form></td></tr>";
	html[idx++] = "</tbody></table>";
	html[idx++] = "</div>";

	return html.join("");
}

ZaMigrationXWizard.myXFormModifier = function(xFormObject,entry) {
	var cases = new Array();


	
	/**
	 * Enter options for provisioning
	 */
	var case_prov_options = {
		type:_CASE_, numCols:2, colSizes:["250px","*"],tabGroupKey:ZaMigrationXWizard.STEP_PROV_OPTIONS, caseKey:ZaMigrationXWizard.STEP_PROV_OPTIONS,
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
				{ref:ZaBulkProvision.A2_provisionUsers,  type:_CHECKBOX_,  
					label:com_zimbra_bulkprovision.A2_provisionUsers,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[]
				},				
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
	};
	cases.push(case_prov_options);
	
	/**
	 * Enter options specific to Exchange Migration
	 */
	
	var case_exchange_options = {
			type:_CASE_, numCols:2,colSizes:["250px","*"], tabGroupKey:ZaMigrationXWizard.STEP_EXCHANGE_INFO, caseKey:ZaMigrationXWizard.STEP_EXCHANGE_INFO,
			items:[
			       	{ref:ZaBulkProvision.A2_TargetDomainName, type:_DYNSELECT_,
						label:com_zimbra_bulkprovision.TargetDomainName,
						toolTipContent:ZaMsg.tt_StartTypingDomainName,
						dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains,
						dataFetcherClass:ZaSearch,editable:true,
						visibilityChecks:[],
						visibilityChangeEventSources:[],
						enableDisableChecks:[]
					},
			       	{type:_DWT_ALERT_, style:DwtAlert.INFO,iconVisible:false,content:com_zimbra_bulkprovision.ZimbraAdminPasswordNote,
			       		visibilityChecks:[],enableDisableChecks:[],colSpan:2
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
		type:_CASE_, numCols:2, colSizes:["250px","*"],tabGroupKey:ZaMigrationXWizard.STEP_LDAP_INFO, caseKey:ZaMigrationXWizard.STEP_LDAP_INFO,
		items:[
		       	{type:_DWT_ALERT_, style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.LdapInfoStepNote},
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
	 * Review options before generating the file
	 */

	var case_review = {
		type:_CASE_, numCols:2, colSizes:["250px", "*"], 
		tabGroupKey:ZaMigrationXWizard.STEP_REVIEW, caseKey:ZaMigrationXWizard.STEP_REVIEW,
		items:[
		       {type:_DWT_ALERT_, style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.ReviewStepNote},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_domainCount,label:com_zimbra_bulkprovision.ReviewDomainCount,bmolsnr:true},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_skippedDomainCount,label:com_zimbra_bulkprovision.ReviewTotalAccounts,bmolsnr:true},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_totalCount,label:com_zimbra_bulkprovision.ReviewAccountCount,bmolsnr:true},
		       {type:_OUTPUT_,ref:ZaBulkProvision.A2_skippedAccountCount,label:com_zimbra_bulkprovision.ReviewSkippedAccountCount,bmolsnr:true},
		       {ref:ZaBulkProvision.A2_TargetDomainName, type:_OUTPUT_,	label:com_zimbra_bulkprovision.TargetDomainName,visibilityChecks:[]},
		       {ref:ZaBulkProvision.A2_ZimbraAdminLogin, type:_OUTPUT_, label:com_zimbra_bulkprovision.A2_ZimbraAdminLogin, labelLocation:_LEFT_, 
		    	   enableDisableChecks:[],visibilityChecks:[]				
		       },
		       {ref:ZaBulkProvision.A2_provisionUsers,  type:_OUTPUT_,  
		    	   label:com_zimbra_bulkprovision.A2_provisionUsers,visibilityChecks:[],enableDisableChecks:[],
		    	   getDisplayValue:function(val) {	return val=="TRUE" ? ZaMsg.Yes : ZaMsg.No; 	}
		       },
		       {ref:ZaBulkProvision.A2_importMails,  type:_OUTPUT_,  
					label:com_zimbra_bulkprovision.MigImportMails,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[],
					getDisplayValue:function(val) {	return val=="TRUE" ? ZaMsg.Yes : ZaMsg.No; 	}		       
		       },
		       {ref:ZaBulkProvision.A2_importContacts,  type:_OUTPUT_,  
		    	   label:com_zimbra_bulkprovision.MigImportContacts,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[],
		    	   getDisplayValue:function(val) {	return val=="TRUE" ? ZaMsg.Yes : ZaMsg.No; 	}
		       },
		       {ref:ZaBulkProvision.A2_importTasks,  type:_OUTPUT_,  
					label:com_zimbra_bulkprovision.MigImportTasks,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[],
					getDisplayValue:function(val) {
				   		return val=="TRUE" ? ZaMsg.Yes : ZaMsg.No;
				   }
		       },
		       {ref:ZaBulkProvision.A2_importCalendar,  type:_OUTPUT_,  
					label:com_zimbra_bulkprovision.MigImportCalendar,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[],
					getDisplayValue:function(val) {
				   		return val=="TRUE" ? ZaMsg.Yes : ZaMsg.No;
				   }
		       },
		       {ref:ZaBulkProvision.A2_importDeletedItems,  type:_OUTPUT_,  
					label:com_zimbra_bulkprovision.MigImportDeletedItems,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[],
					getDisplayValue:function(val) {	return val=="TRUE" ? ZaMsg.Yes : ZaMsg.No; 	}
		       },
		       {ref:ZaBulkProvision.A2_importJunk,  type:_OUTPUT_,  
					label:com_zimbra_bulkprovision.MigImportJunk,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[],
					getDisplayValue:function(val) {	return val=="TRUE" ? ZaMsg.Yes : ZaMsg.No; 	}
		       },
		       {ref:ZaBulkProvision.A2_ignorePreviouslyImported,  type:_OUTPUT_,  
					label:com_zimbra_bulkprovision.MigIgnorePreviouslyImported,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[],
					getDisplayValue:function(val) {	return val=="TRUE" ? ZaMsg.Yes : ZaMsg.No; 	}
		       },
		       {ref:ZaBulkProvision.A2_InvalidSSLOk,  type:_OUTPUT_,  
		    	   label:com_zimbra_bulkprovision.MigInvalidSSLOk,trueValue:"TRUE", falseValue:"FALSE",visibilityChecks:[],enableDisableChecks:[],
		    	   getDisplayValue:function(val) {	return val=="TRUE" ? ZaMsg.Yes : ZaMsg.No; 	}
		       }		       
		       {ref:ZaBulkProvision.A2_MapiProfile, type:_OUTPUT_, label:com_zimbra_bulkprovision.A2_MapiProfile, labelLocation:_LEFT_, 
					enableDisableChecks:[],visibilityChecks:[]				
		       },
		       {ref:ZaBulkProvision.A2_MapiServer, type:_OUTPUT_, label:com_zimbra_bulkprovision.A2_MapiServer, labelLocation:_LEFT_, 
					enableDisableChecks:[],visibilityChecks:[]				
		       },
		       {ref:ZaBulkProvision.A2_MapiLogonUserDN, type:_OUTPUT_, width:"380px", label:com_zimbra_bulkprovision.A2_MapiLogonUserDN, labelLocation:_LEFT_, 
					enableDisableChecks:[],visibilityChecks:[]				
		       }		       
		    	   
		]
	};

	cases.push(case_review);	
	/**
	 * Download generated bulk file
	 */
	var case_download_file = {
		type:_CASE_, numCols:2, colSizes:["250px","*"], tabGroupKey:ZaMigrationXWizard.STEP_DOWNLOAD_FILE, 
		caseKey:ZaMigrationXWizard.STEP_DOWNLOAD_FILE,
		items:[
		       {type:_DWT_ALERT_, style:DwtAlert.INFO, iconVisible:false, content:com_zimbra_bulkprovision.ClickToDownloadGeneratedFile},
		       {type:_DATA_URL_,labelLocation:_NONE_,label:com_zimbra_bulkprovision.GeneratedBulkProvisionFileLink,ref:ZaBulkProvision.A2_generatedFileLink}
		]
	};
	cases.push(case_download_file);
	

	
    var contentW = 630 ;
    xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep,
                choices:this.stepChoices, valueChangeEventSources:[ZaModel.currentStep]},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:contentW, align:_LEFT_, valign:_TOP_, items:cases}
		];
};
ZaXDialog.XFormModifiers["ZaMigrationXWizard"].push(ZaMigrationXWizard.myXFormModifier);

ZaMigrationXWizard.downloadBPStatus = function () {
    var form = this.getForm() ;
    var instance = form.getInstance () ;
    
    return ;
}
