/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaAuthConfigXWizard
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaAuthConfigXWizard = function(parent) {

	ZaXWizardDialog.call(this, parent, null, ZaMsg.NCD_AuthConfigTitle,"700px", "350px","ZaAuthConfigXWizard", null, ZaId.DLG_AUTH_CONFIG);
	
	this.AuthMechs = [
		{label:ZaMsg.AuthMech_zimbra, value:ZaDomain.AuthMech_zimbra},
		{label:ZaMsg.AuthMech_ldap, value:ZaDomain.AuthMech_ldap},
		{label:ZaMsg.AuthMech_ad, value:ZaDomain.AuthMech_ad}		
	];

	this.TestResultChoices = [
		{label:ZaMsg.AuthTest_check_OK, value:ZaDomain.Check_OK},
		{label:ZaMsg.AuthTest_check_UNKNOWN_HOST, value:ZaDomain.Check_UNKNOWN_HOST},
		{label:ZaMsg.AuthTest_check_CONNECTION_REFUSED, value:ZaDomain.Check_CONNECTION_REFUSED},
		{label:ZaMsg.AuthTest_check_SSL_HANDSHAKE_FAILURE, value:ZaDomain.Check_SSL_HANDSHAKE_FAILURE},				
		{label:ZaMsg.AuthTest_check_COMMUNICATION_FAILURE, value:ZaDomain.Check_COMMUNICATION_FAILURE},
		{label:ZaMsg.AuthTest_check_AUTH_FAILED, value:ZaDomain.Check_AUTH_FAILED},
		{label:ZaMsg.AuthTest_check_AUTH_NOT_SUPPORTED, value:ZaDomain.Check_AUTH_NOT_SUPPORTED},
		{label:ZaMsg.AuthTest_check_NAME_NOT_FOUND, value:ZaDomain.Check_NAME_NOT_FOUND},
		{label:ZaMsg.AuthTest_check_INVALID_SEARCH_FILTER, value:ZaDomain.Check_INVALID_SEARCH_FILTER},
		{label:ZaMsg.AuthTest_check_FAILURE, value:ZaDomain.Check_FAILURE}												
	];
	this.TAB_INDEX = 0;	

	ZaAuthConfigXWizard.AUTH_CONFIG_STEP_1 = ++this.TAB_INDEX;
	ZaAuthConfigXWizard.AUTH_CONFIG_BIND_PWD_STEP = ++this.TAB_INDEX;
	ZaAuthConfigXWizard.AUTH_CONFIG_SUMMARY_STEP = ++this.TAB_INDEX;
	ZaAuthConfigXWizard.AUTH_TEST_STEP = ++this.TAB_INDEX;
	ZaAuthConfigXWizard.AUTH_TEST_RESULT_STEP = ++this.TAB_INDEX;
	ZaAuthConfigXWizard.CONFIG_COMPLETE_STEP = ++this.TAB_INDEX;
	
	this.stepChoices = [
		{label:ZaMsg.AuthSettings, value:ZaAuthConfigXWizard.AUTH_CONFIG_STEP_1},						
		{label:ZaMsg.AuthSettings, value:ZaAuthConfigXWizard.AUTH_CONFIG_BIND_PWD_STEP},								
		{label:ZaMsg.AuthSettings, value:ZaAuthConfigXWizard.AUTH_CONFIG_SUMMARY_STEP},										
		{label:ZaMsg.TestAuthSettings, value:ZaAuthConfigXWizard.AUTH_TEST_STEP},				
		{label:ZaMsg.AuthTestResult, value:ZaAuthConfigXWizard.AUTH_TEST_RESULT_STEP},
		{label:ZaMsg.DomainConfigComplete, value:ZaAuthConfigXWizard.CONFIG_COMPLETE_STEP}
	];
	
	this.initForm(ZaDomain.myXModel,this.getMyXForm());		
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaAuthConfigXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaAuthConfigXWizard.prototype.handleXFormChange));	
	this.lastErrorStep=0;	
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_domains/authentication_settings.htm?locid="+AjxEnv.DEFAULT_LOCALE;
}

ZaAuthConfigXWizard.prototype = new ZaXWizardDialog;
ZaAuthConfigXWizard.prototype.constructor = ZaAuthConfigXWizard;
ZaXDialog.XFormModifiers["ZaAuthConfigXWizard"] = new Array();


ZaAuthConfigXWizard.prototype.handleXFormChange = 
function () {
	if(this._localXForm.hasErrors()) {
		if(this.lastErrorStep < this._containedObject[ZaModel.currentStep])
			this.lastErrorStep=this._containedObject[ZaModel.currentStep];
	} else {
		this.lastErrorStep=0;
	}
	this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]);	
}

ZaAuthConfigXWizard.prototype.changeButtonStateForStep = 
function(stepNum) {
	if(this.lastErrorStep == stepNum) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		if(stepNum>1)
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	} else {
		if(stepNum == ZaAuthConfigXWizard.AUTH_CONFIG_STEP_1) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);		
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		} else if (stepNum == ZaAuthConfigXWizard.AUTH_CONFIG_SUMMARY_STEP) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_AuthTestSettings);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		} else if(stepNum == ZaAuthConfigXWizard.AUTH_TEST_STEP) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if(stepNum == ZaAuthConfigXWizard.CONFIG_COMPLETE_STEP) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		}
	}
}

ZaAuthConfigXWizard.prototype.testSetings =
function () {
	if(this._containedObject.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ad) {
		this._containedObject.attrs[ZaDomain.A_AuthLdapUserDn] = "%u@"+this._containedObject.attrs[ZaDomain.A_AuthADDomainName];
	}

	var callback = new AjxCallback(this, this.checkCallBack);
	ZaDomain.testAuthSettings(this._containedObject, callback);	
}

/**
* Callback function invoked by Asynchronous CSFE command when "check" call returns
**/
ZaAuthConfigXWizard.prototype.checkCallBack = 
function (arg) {
	if(!arg)
		return;
	if(arg.isException()) {
		this._containedObject[ZaDomain.A_AuthTestResultCode] = arg.getException().code;
		this._containedObject[ZaDomain.A_AuthTestMessage] = arg.getException().detail+"\n"+arg.getException().msg;
	} else {
		var response = arg.getResponse().Body.CheckAuthConfigResponse;
		this._containedObject[ZaDomain.A_AuthTestResultCode] = response.code[0]._content;
		if(this._containedObject[ZaDomain.A_AuthTestResultCode] != ZaDomain.Check_OK) {
			this._containedObject[ZaDomain.A_AuthTestMessage] = response.message[0]._content;		
			if(response.bindDn != null) {
				this._containedObject[ZaDomain.A_AuthComputedBindDn] = response.bindDn[0]._content;		
			} else {
				this._containedObject[ZaDomain.A_AuthComputedBindDn] = "";
			}
		}
	}		
	this.goPage(ZaAuthConfigXWizard.AUTH_TEST_RESULT_STEP);
}

/**
* Eevent handlers for form items
**/
ZaAuthConfigXWizard.onAuthMechChange = 
function (value, event, form) {
	this.setInstanceValue(value);

	if(value == ZaDomain.AuthMech_ad) {
		if(!form.getInstance().attrs[ZaDomain.A_AuthADDomainName]) {
			this.setInstanceValue(form.getInstance().attrs[ZaDomain.A_domainName], ZaDomain.A_AuthADDomainName);
			//form.getInstance().attrs[ZaDomain.A_AuthADDomainName] = form.getInstance().attrs[ZaDomain.A_domainName];
		}
	}
	//form.parent.changeButtonStateForStep(1);

}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaAuthConfigXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this.changeButtonStateForStep(ZaAuthConfigXWizard.AUTH_CONFIG_STEP_1);

}

ZaAuthConfigXWizard.prototype.goPage =
function(pageNum) {
	ZaXWizardDialog.prototype.goPage.call(this, pageNum);
	this.changeButtonStateForStep(pageNum);
}

ZaAuthConfigXWizard.prototype.goPrev =
function () {
	if(this._containedObject[ZaModel.currentStep] == ZaAuthConfigXWizard.AUTH_TEST_RESULT_STEP) {
		//skip ZaAuthConfigXWizard.AUTH_TEST_STEP step
		this.goPage(ZaAuthConfigXWizard.AUTH_CONFIG_SUMMARY_STEP);
	} else if (this._containedObject[ZaModel.currentStep] == ZaAuthConfigXWizard.AUTH_CONFIG_SUMMARY_STEP && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad) {
		this.goPage(ZaAuthConfigXWizard.AUTH_CONFIG_STEP_1);//skip ZaAuthConfigXWizard.AUTH_CONFIG_BIND_PWD_STEP step for Active Directory
	} else if(this._containedObject[ZaModel.currentStep] == ZaAuthConfigXWizard.CONFIG_COMPLETE_STEP && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_zimbra) {
		this.goPage(ZaAuthConfigXWizard.AUTH_CONFIG_STEP_1);
	} else {
		this.goPage(this._containedObject[ZaModel.currentStep]-1);
	}
}

ZaAuthConfigXWizard.prototype.goNext = 
function() {
	if(this._containedObject[ZaModel.currentStep] == ZaAuthConfigXWizard.AUTH_CONFIG_SUMMARY_STEP) {
 		this.testSetings();
		this.goPage(ZaAuthConfigXWizard.AUTH_TEST_STEP);
	} else if (this._containedObject[ZaModel.currentStep]==ZaAuthConfigXWizard.AUTH_CONFIG_STEP_1 && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad) {
		if(!this._containedObject.attrs[ZaDomain.A_AuthLdapURL]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_LDAP_URL_REQUIRED);
			return false;
		}	
		this.goPage(ZaAuthConfigXWizard.AUTH_CONFIG_SUMMARY_STEP);//skip ZaAuthConfigXWizard.AUTH_CONFIG_BIND_PWD_STEP step for Active Directory
	} else if(this._containedObject[ZaModel.currentStep]==ZaAuthConfigXWizard.AUTH_CONFIG_STEP_1 && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap) {
		var temp = this._containedObject.attrs[ZaDomain.A_AuthLdapURL].join(" ");
		if(this._containedObject.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled] == "TRUE") {
			//check that we don't have ldaps://
			if(temp.indexOf("ldaps://") > -1) {
				ZaApp.getInstance().getCurrentController().popupWarningDialog(ZaMsg.Domain_WarningStartTLSIgnored)
			}		
		}	
		this.goPage(ZaAuthConfigXWizard.AUTH_CONFIG_BIND_PWD_STEP);	 
	}  else if (this._containedObject[ZaModel.currentStep] == ZaAuthConfigXWizard.AUTH_CONFIG_BIND_PWD_STEP) {
			//clear the password if the checkbox is unchecked
		if(this._containedObject[ZaDomain.A_AuthUseBindPassword]=="FALSE") {
			this._containedObject.attrs[ZaDomain.A_AuthLdapSearchBindDn] = null;
			this._containedObject.attrs[ZaDomain.A_AuthLdapSearchBindPassword] = null;
			this._containedObject[ZaDomain.A_AuthLdapSearchBindPasswordConfirm] = null;
		}
		//check that passwords match
		if(this._containedObject.attrs[ZaDomain.A_AuthLdapSearchBindPassword]!=this._containedObject[ZaDomain.A_AuthLdapSearchBindPasswordConfirm]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
			return false;
		}
		this.goPage(ZaAuthConfigXWizard.AUTH_CONFIG_SUMMARY_STEP);
	} else if(this._containedObject[ZaModel.currentStep]==ZaAuthConfigXWizard.AUTH_CONFIG_STEP_1 && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_zimbra) {
		this.goPage(ZaAuthConfigXWizard.CONFIG_COMPLETE_STEP);
	} else {
		this.goPage(this._containedObject[ZaModel.currentStep]+1);
	}
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaDomain object to display
**/
ZaAuthConfigXWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();

	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
	
	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type ;
	this._containedObject.id = entry.id;
			
	if(entry.rights)
		this._containedObject.rights = entry.rights;

	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;

	
	this._containedObject[ZaDomain.A_AuthUseBindPassword] = entry[ZaDomain.A_AuthUseBindPassword];
	this.setTitle(ZaMsg.NCD_AuthConfigTitle + " (" + entry.name + ")");

    if (ZaSettings.isDomainAdmin && (entry.attrs[ZaDomain.A_zimbraAdminConsoleLDAPAuthEnabled] == "TRUE")
           && entry.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled] != "TRUE") {
        this._containedObject [ZaDomain.A2_allowClearTextLDAPAuth] = "FALSE" ;
    }

    this._containedObject[ZaModel.currentStep] = ZaAuthConfigXWizard.AUTH_CONFIG_STEP_1;
	this._localXForm.setInstance(this._containedObject);	
}

/**
* XForm definition
**/

ZaAuthConfigXWizard.myXFormModifier = function(xFormObject) {
    xFormObject.items = [
        {
            type: _OUTPUT_,
            colSpan: 2,
            valign: _TOP_,
            cssStyle: "white-space: normal",
            ref: ZaModel.currentStep,
            choices: this.stepChoices,
            valueChangeEventSources: [ZaModel.currentStep]
        },
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},				
			{type:_SWITCH_,width:650, valign:_TOP_, cssStyle: "white-space: normal",
				items:[
					{type:_CASE_, numCols:2,colSizes:["220px","430px"],	caseKey:ZaAuthConfigXWizard.AUTH_CONFIG_STEP_1,												
						items:[
						    {type:_OSELECT1_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs, ref:ZaDomain.A_AuthMech, onChange:ZaAuthConfigXWizard.onAuthMechChange,
						    	enableDisableChecks:[],visibilityChecks:[]
						    },
							{type:_GROUP_, colSpan:2,numCols:2,colSizes:["220px","430px"],
								visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ad]],
								visibilityChangeEventSources:[ZaDomain.A_AuthMech],
								items:[
								    {type:_SPACER_, height:5,colSpan:2},
									{ref:ZaDomain.A_AuthADDomainName, type:_TEXTFIELD_, label:ZaMsg.Domain_AuthADDomainName, labelLocation:_LEFT_,
										visibilityChecks:[],enableDisableChecks:[],bmolsnr:true
									},
									{type:_GROUP_, numCols:6, colSpan:2,label:"   ",labelLocation:_LEFT_,
										items: [
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthADServerName, width:"200px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthADServerPort,  width:"40px"},	
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthADUseSSL, width:"40px"}									
										]
									},											
									{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthADURL, 
										repeatInstance:"", showAddButton:true, showRemoveButton:true,  
										addButtonLabel:ZaMsg.Domain_AddURL, 
										removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,												
										showAddOnNextRow:true,											
										items: [
											{ref:".", type:_LDAPURL_, label:null, labelLocation:_NONE_,ldapSSLPort:"3269",ldapPort:"3268",
											visibilityChecks:[],enableDisableChecks:[]}
										]
									}											
								]
							},
							{type:_GROUP_, colSpan:2,numCols:2,colSizes:["220px","430px"],
								visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ldap]],
								visibilityChangeEventSources:[ZaDomain.A_AuthMech],										
								items:[
									{type:_GROUP_, numCols:6, colSpan:2,label:"   ",labelLocation:_LEFT_,
										items: [
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthLDAPServerName, width:"200px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthLDAPServerPort,  width:"40px"},	
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthLDAPUseSSL, width:"40px"}									
										]
									},										
									{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, repeatInstance:"", showAddButton:true, showRemoveButton:true,  
										addButtonLabel:ZaMsg.Domain_AddURL,
										removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,												
										showAddOnNextRow:true,											
										items: [
											{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"636",ldapPort:"389",  labelLocation:_NONE_,
											visibilityChecks:[],enableDisableChecks:[]}
										]
									},	
									{ref:ZaDomain.A_zimbraAuthLdapStartTlsEnabled, type:_CHECKBOX_, label:ZaMsg.Domain_AuthLdapStartTlsEnabled, onChange: ZaAuthConfigXWizard.startTlsEnabledChanged,
										 trueValue:"TRUE", falseValue:"FALSE",labelLocation:_RIGHT_
									},
									{ref:ZaDomain.A_AuthLdapSearchFilter, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_AuthLdapFilter, labelLocation:_LEFT_, textWrapping:"soft"},
									{ref:ZaDomain.A_AuthLdapSearchBase, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_AuthLdapSearchBase, labelLocation:_LEFT_, textWrapping:"soft"},
									{type:_OUTPUT_, value:ZaMsg.NAD_DomainsAuthStr, colSpan:2}
								]
							}
						]
					},
					{type:_CASE_, numCols:2, colSizes:["220px","430px"], caseKey:ZaAuthConfigXWizard.AUTH_CONFIG_BIND_PWD_STEP,
						visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ldap]],
						items: [
							{ref:ZaDomain.A_AuthUseBindPassword, type:_CHECKBOX_, label:ZaMsg.Domain_AuthUseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_,
							visibilityChecks:[],enableDisableChecks:[]},
							{ref:ZaDomain.A_AuthLdapSearchBindDn, type:_INPUT_, label:ZaMsg.Domain_AuthLdapBindDn, labelLocation:_LEFT_, 
								enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthUseBindPassword,"TRUE"]],
								enableDisableChangeEventSources:[ZaDomain.A_AuthUseBindPassword],
								visibilityChecks:[]
							},
							{ref:ZaDomain.A_AuthLdapSearchBindPassword, type:_SECRET_, label:ZaMsg.Domain_AuthLdapBindPassword, labelLocation:_LEFT_, 
								enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthUseBindPassword,"TRUE"]],
								enableDisableChangeEventSources:[ZaDomain.A_AuthUseBindPassword],
								visibilityChecks:[]
							},
							{ref:ZaDomain.A_AuthLdapSearchBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_AuthLdapBindPasswordConfirm, labelLocation:_LEFT_, 
								enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthUseBindPassword,"TRUE"]],
								enableDisableChangeEventSources:[ZaDomain.A_AuthUseBindPassword],
								visibilityChecks:[]
							}							
						]						
					},
					{type:_CASE_, numCols:2,colSizes:["220px","430px"],	caseKey:ZaAuthConfigXWizard.AUTH_CONFIG_SUMMARY_STEP,
						visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaNewDomainXWizard.isAuthMechNotZimbra],					
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_Auth_ConfigSummary, align:_CENTER_, colSpan:"*"}, 
							{type:_SPACER_, height:10},
							{ref:ZaDomain.A_AuthMech, type:_OUTPUT_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs, alignment:_LEFT_},
							{type:_GROUP_, useParentTable:true, 
								visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ad]],
								visibilityChangeEventSources:[ZaDomain.A_AuthMech],									 
								items:[
									{ref:ZaDomain.A_AuthADDomainName, type:_OUTPUT_, label:ZaMsg.Domain_AuthADDomainName, labelLocation:_LEFT_},
									{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false, 
										items:[
											{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
										]
									}											
								]
							},
							{type:_GROUP_, useParentTable:true,
								visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ldap]],
								visibilityChangeEventSources:[ZaDomain.A_AuthMech],										 
								items:[
									{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,visibilityChecks:[], 
										items:[
											{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_,visibilityChecks:[]}
										]
										
									},	
									{ref:ZaDomain.A_zimbraAuthLdapStartTlsEnabled, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapStartTlsEnabled, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES,visibilityChecks:[]},
									{ref:ZaDomain.A_AuthLdapSearchFilter, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapFilter, labelLocation:_LEFT_,visibilityChecks:[]},
									{ref:ZaDomain.A_AuthLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapSearchBase, labelLocation:_LEFT_,visibilityChecks:[]},
									{ref:ZaDomain.A_AuthUseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_AuthUseBindPassword, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES},											
									{ref:ZaDomain.A_AuthLdapSearchBindDn, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapBindDn, labelLocation:_LEFT_, 
										visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthUseBindPassword,"TRUE"]],
										visibilityChangeEventSources:[ZaDomain.A_AuthUseBindPassword]	
									}											
								]
							},
							{type:_SPACER_, height:10},
							{type:_OUTPUT_,value:ZaMsg.Domain_AuthProvideLoginPwd, align:_CENTER_, colSpan:"*",visibilityChecks:[]},
							{type:_TEXTFIELD_, label:ZaMsg.LBL_Domain_AuthTestUserName, ref:ZaDomain.A_AuthTestUserName, alignment:_LEFT_,visibilityChecks:[],enableDisableChecks:[]},
							{type:_SECRET_, label:ZaMsg.LBL_Domain_AuthTestPassword, ref:ZaDomain.A_AuthTestPassword, alignment:_LEFT_,visibilityChecks:[],enableDisableChecks:[]}
						]
					},
					{type:_CASE_, caseKey:ZaAuthConfigXWizard.AUTH_TEST_STEP,numCols:1,colSizes:["100%"], 
						visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaNewDomainXWizard.isAuthMechNotZimbra],
						visibilityChangeEventSources:[ZaModel.currentStep],					
						items: [
							{type:_DWT_ALERT_,content:ZaMsg.Domain_AuthTestingInProgress,style:DwtAlert.WARNING}
						]
					},
					{type:_CASE_, numCols:2,colSizes:["220px","430px"], caseKey:ZaAuthConfigXWizard.AUTH_TEST_RESULT_STEP,
						visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaNewDomainXWizard.isAuthMechNotZimbra],
						visibilityChangeEventSources:[ZaModel.currentStep],					
						items: [
							{type:_DWT_ALERT_, style:DwtAlert.INFORMATION, content:ZaMsg.Domain_AuthTestSuccessful, alignment:_CENTER_,colSpan:2,
								visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthTestResultCode,ZaDomain.Check_OK]],
								visibilityChangeEventSources:[ZaDomain.A_AuthTestResultCode]	
							},
							{type:_GROUP_, isTabGroup:false, deferred:false, colSpan:2, 
								visibilityChangeEventSources:[ZaDomain.A_AuthTestResultCode],
								visibilityChecks:[
									function () {
										return (this.getInstanceValue(ZaDomain.A_AuthTestResultCode) != ZaDomain.Check_OK);
									}
								],										
								items: [
									{type:_DWT_ALERT_, content:ZaMsg.Domain_AuthTestFailed, alignment:_CENTER_, colSpan:2, style:DwtAlert.CRITICAL},
									{type:_OUTPUT_, ref:ZaDomain.A_AuthTestResultCode, label:ZaMsg.LBL_Domain_AuthTestResultCode, choices:this.TestResultChoices},
									{type:_OUTPUT_, ref:ZaDomain.A_AuthComputedBindDn, label:ZaMsg.LBL_Domain_AuthComputedBindDn, 
										visibilityChangeEventSources:[ZaDomain.A_AuthMech],
										visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ad]]												
									},
									{type:_TEXTAREA_, ref:ZaDomain.A_AuthTestMessage, label:ZaMsg.LBL_Domain_AuthTestMessage, height:150, alignment:_LEFT_, width:"320px"}
								]
							}
						]
					},
					{type:_CASE_, caseKey:ZaAuthConfigXWizard.CONFIG_COMPLETE_STEP,
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_Auth_Config_Complete}
						]
					}
				]
			}
		];
}
ZaXDialog.XFormModifiers["ZaAuthConfigXWizard"].push(ZaAuthConfigXWizard.myXFormModifier);

ZaAuthConfigXWizard.startTlsEnabledChanged =  function (value, event, form) {
	this.setInstanceValue(value);
    var instance = form.getInstance () ;
    var ldapUrls = instance.attrs[ZaDomain.A_AuthLdapURL] ;
    var newUrls = [];
    if (ZaSettings.isDomainAdmin && (instance.attrs[ZaDomain.A_zimbraAdminConsoleLDAPAuthEnabled] == "TRUE")
               && instance.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled] != "TRUE") {
        //force ldaps protocol
        instance[ZaDomain.A2_allowClearTextLDAPAuth] = "FALSE" ;

        for (var i=0; i< ldapUrls.length; i++) {
            var ldapUrl = ldapUrls [i] ;
            if (ldapUrl == null || ldapUrl.length <=0) {
            }else {
                //force to use ldaps://
                ldapUrls[i] = ldapUrl.replace("ldap://", "ldaps://")  ;
                newUrls.push (ldapUrls[i]);
            }
        }

    }else{
        for (var i=0; i< ldapUrls.length; i++) {
            var ldapUrl = ldapUrls [i] ;
            //remove this empty item
            if (ldapUrl == null || ldapUrl.length <=0) {
                
            }else {
                newUrls.push (ldapUrls[i]);
            }
        }
        instance[ZaDomain.A2_allowClearTextLDAPAuth] = "TRUE" ;
    }

    instance.attrs[ZaDomain.A_AuthLdapURL] = newUrls ;
    form.refresh ();  
}
