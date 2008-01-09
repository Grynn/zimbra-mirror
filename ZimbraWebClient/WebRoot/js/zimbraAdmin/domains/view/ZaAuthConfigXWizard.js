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
* @class ZaAuthConfigXWizard
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaAuthConfigXWizard = function(parent, app) {
	this._app=app;
	ZaXWizardDialog.call(this, parent,app, null, ZaMsg.NCD_AuthConfigTitle, "550px", "300px","ZaAuthConfigXWizard");

	this.stepChoices = [
		{label:ZaMsg.TABT_AuthMode, value:1},				
		{label:ZaMsg.TABT_AuthSettings, value:2},						
		{label:ZaMsg.TABT_AuthSettings, value:3},								
		{label:ZaMsg.TABT_AuthSettings, value:4},										
		{label:ZaMsg.TABT_TestAuthSettings, value:5},				
		{label:ZaMsg.TABT_AuthTestResult, value:6}
	];
	
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
	this.initForm(ZaDomain.myXModel,this.getMyXForm());		
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaAuthConfigXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaAuthConfigXWizard.prototype.handleXFormChange));	
	this.lastErrorStep=0;	
	this._helpURL = location.pathname + "adminhelp/html/WebHelp/managing_domains/authentication_settings.htm";
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
		if(stepNum == 1) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			if(this._containedObject.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_zimbra) {
				this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			} else {
				this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);		
			}
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		} else if (stepNum == 4) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_AuthTestSettings);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		} else if(stepNum == 5) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if(stepNum == 6) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
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
/*	if(arg instanceof AjxException || arg instanceof ZmCsfeException || arg instanceof AjxSoapException) {
		this._containedObject[ZaDomain.A_AuthTestResultCode] = arg.code;
		this._containedObject[ZaDomain.A_AuthTestMessage] = arg.detail;
	} else {
		this._containedObject[ZaDomain.A_AuthTestResultCode] = arg.getBody().firstChild.firstChild.firstChild.nodeValue;
		if(this._containedObject[ZaDomain.A_AuthTestResultCode] != ZaDomain.Check_OK) {
			this._containedObject[ZaDomain.A_AuthTestMessage] = arg.getBody().firstChild.childNodes[1].firstChild.nodeValue;		
			if(arg.getBody().firstChild.lastChild.firstChild != null) {
				this._containedObject[ZaDomain.A_AuthComputedBindDn] = arg.getBody().firstChild.lastChild.firstChild.nodeValue;		
			} else {
				this._containedObject[ZaDomain.A_AuthComputedBindDn] = "";
			}
		}
	}
*/
	this.goPage(6);
}

/**
* Eevent handlers for form items
**/
ZaAuthConfigXWizard.onAuthMechChange = 
function (value, event, form) {
	this.setInstanceValue(value);

	if(value == ZaDomain.AuthMech_ad) {
		if(!form.getInstance().attrs[ZaDomain.A_AuthADDomainName]) {
			form.getInstance().attrs[ZaDomain.A_AuthADDomainName] = form.getInstance().attrs[ZaDomain.A_domainName];
		}
	}
	form.parent.changeButtonStateForStep(1);

}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaAuthConfigXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this.changeButtonStateForStep(1);

}

ZaAuthConfigXWizard.prototype.goPage =
function(pageNum) {
	ZaXWizardDialog.prototype.goPage.call(this, pageNum);
	this.changeButtonStateForStep(pageNum);
}

ZaAuthConfigXWizard.prototype.goPrev =
function () {
	if(this._containedObject[ZaModel.currentStep] == 6) {
		//skip 5rd step
		this.goPage(4);
	} else if (this._containedObject[ZaModel.currentStep] == 4 && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad) {
		this.goPage(2);//skip 3rd step for Active Directory
	} else {
		/*this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
		if(this._containedObject[ZaModel.currentStep] == 2) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		}*/
		this.goPage(this._containedObject[ZaModel.currentStep]-1);
	}
}

ZaAuthConfigXWizard.prototype.goNext = 
function() {
	if(this._containedObject[ZaModel.currentStep] == 4) {
 		this.testSetings();
		this.goPage(5);
	} else if (this._containedObject[ZaModel.currentStep]==2 && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad) {
		if(!this._containedObject.attrs[ZaDomain.A_AuthLdapURL]) {
			this._app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_LDAP_URL_REQUIRED);
			return false;
		}	
		this.goPage(4);//skip 3rd step for Active Directory
	} else if (this._containedObject[ZaModel.currentStep] == 3) {
			//clear the password if the checkbox is unchecked
		if(this._containedObject[ZaDomain.A_AuthUseBindPassword]=="FALSE") {
			this._containedObject.attrs[ZaDomain.A_AuthLdapSearchBindDn] = null;
			this._containedObject.attrs[ZaDomain.A_AuthLdapSearchBindPassword] = null;
			this._containedObject[ZaDomain.A_AuthLdapSearchBindPasswordConfirm] = null;
		}
		//check that passwords match
		if(this._containedObject.attrs[ZaDomain.A_AuthLdapSearchBindPassword]!=this._containedObject[ZaDomain.A_AuthLdapSearchBindPasswordConfirm]) {
			this._app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
			return false;
		}
		this.goPage(4);
	}else {
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
	this._containedObject[ZaDomain.A_AuthUseBindPassword] = entry[ZaDomain.A_AuthUseBindPassword];
	this.setTitle(ZaMsg.NCD_AuthConfigTitle + " (" + entry.name + ")");
	this._containedObject[ZaModel.currentStep] = 1;
	this._localXForm.setInstance(this._containedObject);	
}

/**
* XForm definition
**/

ZaAuthConfigXWizard.myXFormModifier = function(xFormObject) {
	xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},				
			{type:_SWITCH_,
				items:[
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 1", relevantBehavior:_HIDE_,
						items:[
							{type:_OSELECT1_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs, ref:ZaDomain.A_AuthMech, onChange:ZaAuthConfigXWizard.onAuthMechChange}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 2", relevantBehavior:_HIDE_,												
						items:[
							{type:_SWITCH_,
								items: [
									{type:_CASE_, isTabGroup:false, deferred:false, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad",
										items:[
											{ref:ZaDomain.A_AuthADDomainName, type:_INPUT_, width:200,  label:ZaMsg.Domain_AuthADDomainName, labelLocation:_LEFT_},
											{type:_GROUP_, numCols:6, colSpan:6,label:"   ",labelLocation:_LEFT_,
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
													{ref:".", type:_LDAPURL_, label:null, labelLocation:_NONE_,ldapSSLPort:"3269",ldapPort:"3268"}
												]
											}											
										]
									},
									{type:_CASE_, isTabGroup:false, deferred:false, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap",
										items:[
											{type:_GROUP_, numCols:6, colSpan:6,label:"   ",labelLocation:_LEFT_,
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
													{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"636",ldapPort:"389",  labelLocation:_NONE_}
												]
											},	
											{ref:ZaDomain.A_AuthLdapSearchFilter, type:_TEXTAREA_, width:380, height:100, label:ZaMsg.Domain_AuthLdapFilter, labelLocation:_LEFT_, textWrapping:"soft"},
											{ref:ZaDomain.A_AuthLdapSearchBase, type:_TEXTAREA_, width:380, height:50, label:ZaMsg.Domain_AuthLdapSearchBase, labelLocation:_LEFT_, textWrapping:"soft"},
											{type:_OUTPUT_, value:ZaMsg.NAD_DomainsAuthStr, colSpan:2}
										]
									},
									{type:_CASE_, isTabGroup:false, deferred:false, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_zimbra",
										items:[
											{type:_OUTPUT_, value:ZaMsg.Domain_Auth_Config_Complete}
										]
									}
								]
							}
						]
					},
					{type:_CASE_, numCols:2, relevant:"instance[ZaModel.currentStep] == 3 && instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap", relevantBehavior:_HIDE_,
						items: [
							{ref:ZaDomain.A_AuthUseBindPassword, type:_CHECKBOX_, label:ZaMsg.Domain_AuthUseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaDomain.A_AuthLdapSearchBindDn, type:_INPUT_, label:ZaMsg.Domain_AuthLdapBindDn, labelLocation:_LEFT_, relevant:"instance[ZaDomain.A_AuthUseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
							{ref:ZaDomain.A_AuthLdapSearchBindPassword, type:_SECRET_, label:ZaMsg.Domain_AuthLdapBindPassword, labelLocation:_LEFT_, relevant:"instance[ZaDomain.A_AuthUseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
							{ref:ZaDomain.A_AuthLdapSearchBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_AuthLdapBindPasswordConfirm, labelLocation:_LEFT_, relevant:"instance[ZaDomain.A_AuthUseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_}							
						]						
					},
					{type:_CASE_, numCols:2, relevant:"instance[ZaModel.currentStep] == 4 && instance.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_zimbra", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_Auth_ConfigSummary, align:_CENTER_, colSpan:"*"}, 
							{type:_SPACER_, height:10},
							{ref:ZaDomain.A_AuthMech, type:_OUTPUT_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs, alignment:_LEFT_},
							{type:_SWITCH_, useParentTable:true,
								items: [
									{type:_CASE_, isTabGroup:false, deferred:false, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad", useParentTable:true,
										items:[
											{ref:ZaDomain.A_AuthADDomainName, type:_OUTPUT_, label:ZaMsg.Domain_AuthADDomainName, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false, 
												items:[
													{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
												]
											}											
										]
									},
									{type:_CASE_, isTabGroup:false, deferred:false, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap", useParentTable:true,
										items:[
											{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false, 
												items:[
													{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
												]
											},	
											{ref:ZaDomain.A_AuthLdapSearchFilter, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapFilter, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapSearchBase, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthUseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_AuthUseBindPassword, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES},											
											{ref:ZaDomain.A_AuthLdapSearchBindDn, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapBindDn, labelLocation:_LEFT_, relevant:"instance[ZaDomain.A_AuthUseBindPassword] == 'TRUE'", relevantBehavior:_HIDE_}											
										]
									}
								]
							},
							{type:_SPACER_, height:10},
							{type:_OUTPUT_,value:ZaMsg.Domain_AuthProvideLoginPwd, align:_CENTER_, colSpan:"*"},
							{type:_TEXTFIELD_, label:ZaMsg.Domain_AuthTestUserName+":", ref:ZaDomain.A_AuthTestUserName, alignment:_LEFT_},
							{type:_SECRET_, label:ZaMsg.Domain_AuthTestPassword+":", ref:ZaDomain.A_AuthTestPassword, alignment:_LEFT_}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 5 && instance.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_zimbra", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_,value:ZaMsg.Domain_AuthTestingInProgress}
						]
					},
					{type:_CASE_,  numCols:1, relevant:"instance[ZaModel.currentStep] == 6 && instance.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_zimbra", relevantBehavior:_HIDE_,
						items: [
							{type:_SWITCH_,
								items: [
									{type:_CASE_, isTabGroup:false, deferred:false, relevant:"instance[ZaDomain.A_AuthTestResultCode] == ZaDomain.Check_OK",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Domain_AuthTestSuccessful, alignment:_CENTER_}
										]
									},
									{type:_CASE_, isTabGroup:false, deferred:false, relevant:	"instance[ZaDomain.A_AuthTestResultCode] != ZaDomain.Check_OK",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Domain_AuthTestFailed, alignment:_CENTER_, colSpan:2, label:null},
											{type:_OUTPUT_, ref:ZaDomain.A_AuthTestResultCode, label:ZaMsg.Domain_AuthTestResultCode+":", choices:this.TestResultChoices, alignment:_LEFT_},
											{type:_OUTPUT_, ref:ZaDomain.A_AuthComputedBindDn, label:ZaMsg.Domain_AuthComputedBindDn+":", alignment:_LEFT_, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad", relevantBehavior:_HIDE_},
											{type:_TEXTAREA_, ref:ZaDomain.A_AuthTestMessage, label:ZaMsg.Domain_AuthTestMessage+":", height:150, alignment:_LEFT_, width:"320px"}
										]
									}
								]
							}
						]
					}
				]
			}
		];
}
ZaXDialog.XFormModifiers["ZaAuthConfigXWizard"].push(ZaAuthConfigXWizard.myXFormModifier);