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
 * The Original Code is: Zimbra Collaboration Suite Web Client
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
* @class ZaNewDomainXWizard
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
function ZaNewDomainXWizard (parent, app) {
	this._app=app;
	ZaXWizardDialog.call(this, parent,app, null, ZaMsg.NDD_Title, "550px", "300px");

	this.stepChoices = [
		{label:ZaMsg.TABT_GeneralPage, value:1},
		{label:ZaMsg.TABT_GALMode, value:2},
		{label:ZaMsg.TABT_GALonfiguration, value:3}, 
		{label:ZaMsg.TABT_GALonfiguration, value:4},		
		{label:ZaMsg.TABT_GALonfigSummary, value:5},
		{label:ZaMsg.TABT_TestGalConfig, value:6},
		{label:ZaMsg.TABT_GalTestResult, value:7},		
		{label:ZaMsg.TABT_AuthMode, value:8},				
		{label:ZaMsg.TABT_AuthSettings, value:9},						
		{label:ZaMsg.TABT_AuthSettings, value:10},								
		{label:ZaMsg.TABT_AuthSettings, value:11},								
		{label:ZaMsg.TABT_TestAuthSettings, value:12},				
		{label:ZaMsg.TABT_AuthTestResult, value:13},
		{label:ZaMsg.TABT_DomainConfigComplete, value:14}		
	];
		
	this.GALModes = [
		{label:ZaMsg.GALMode_internal, value:ZaDomain.GAL_Mode_internal},
		{label:ZaMsg.GALMode_external, value:ZaDomain.GAL_Mode_external}, 
		{label:ZaMsg.GALMode_both, value:ZaDomain.GAL_Mode_both}
  	];
  	this.GALServerTypes = [
		{label:ZaMsg.GALServerType_ldap, value:ZaDomain.GAL_ServerType_ldap},
		{label:ZaMsg.GALServerType_ad, value:ZaDomain.GAL_ServerType_ad} 
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
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaNewDomainXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaNewDomainXWizard.prototype.handleXFormChange));	
	this.lastErrorStep=0;
	this._helpURL = "/zimbraAdmin/adminhelp/html/WebHelp/managing_domains/creating_a_domain.htm";			
}

ZaNewDomainXWizard.prototype = new ZaXWizardDialog;
ZaNewDomainXWizard.prototype.constructor = ZaNewDomainXWizard;

ZaNewDomainXWizard.prototype.handleXFormChange = 
function () {
	if(this._localXForm.hasErrors()) {
		if(this.lastErrorStep < this._containedObject[ZaModel.currentStep])
			this.lastErrorStep=this._containedObject[ZaModel.currentStep];
	} else {
		this.lastErrorStep=0;
	}
	this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]);	
}

ZaNewDomainXWizard.prototype.changeButtonStateForStep = 
function(stepNum) {
	if(this.lastErrorStep == stepNum) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		if(stepNum>1)
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	} else {
		if(stepNum == 1) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if (stepNum == 2) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		} else if(stepNum == 5) {
			//change next button to "test"
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_GALTestSettings);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if(stepNum == 6) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if (stepNum == 11) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_GALTestSettings);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if(stepNum == 12) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if(stepNum == 14) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		}
	}
}
/**
* @method setObject sets the object contained in the view
* @param entry - ZaDomain object to display
**/
ZaNewDomainXWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();

	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
	
	this._containedObject[ZaModel.currentStep] = 1;
	this._localXForm.setInstance(this._containedObject);	
}

/**
* static change handlers for the form
**/
ZaNewDomainXWizard.onGALServerTypeChange =
function (value, event, form) {
	if(value == "ad") {
		form.getInstance().attrs[ZaDomain.A_GalLdapFilter] = "ad";
	} else {
		form.getInstance().attrs[ZaDomain.A_GalLdapFilter] = "";
	}
	this.setInstanceValue(value);	
}


ZaNewDomainXWizard.onGalModeChange = 
function (value, event, form) {
	this.setInstanceValue(value);
	if(value != "zimbra") {
		form.getInstance().attrs[ZaDomain.A_GalLdapFilter] = "";
		if(!form.getInstance().attrs[ZaDomain.A_GALServerType]) {
			form.getInstance().attrs[ZaDomain.A_GALServerType] = "ldap";
		}
		if(!form.getInstance().attrs[ZaDomain.A_GalLdapSearchBase]) {
			if(form.getInstance().attrs[ZaDomain.A_domainName]) {
				var parts = form.getInstance().attrs[ZaDomain.A_domainName].split(".");
				var szSearchBase = "";
				var coma = "";
				for(var ix in parts) {
					szSearchBase += coma;
				 	szSearchBase += "dc=";
				 	szSearchBase += parts[ix];
					var coma = ",";
				}
				form.getInstance().attrs[ZaDomain.A_GalLdapSearchBase] = szSearchBase;
			}
		}
	}
}

ZaNewDomainXWizard.prototype.testGALSettings =
function () {
	var callback = new AjxCallback(this, this.checkGALCallBack);
	ZaDomain.testGALSettings(this._containedObject, callback, this._containedObject[ZaDomain.A_GALSampleQuery]);	
}

/**
* Callback function invoked by Asynchronous CSFE command when "check" call returns
**/
ZaNewDomainXWizard.prototype.checkGALCallBack = 
function (arg) {
	if(arg instanceof AjxException || arg instanceof ZmCsfeException || arg instanceof AjxSoapException) {
		this._containedObject[ZaDomain.A_GALTestResultCode] = arg.code;
		this._containedObject[ZaDomain.A_GALTestMessage] = arg.detail;
		this._containedObject[ZaDomain.A_GALTestSearchResults] = null;	
	} else {
		this._containedObject[ZaDomain.A_GALTestResultCode] = arg.getBody().firstChild.firstChild.firstChild.nodeValue;
		if(this._containedObject[ZaDomain.A_GALTestResultCode] != ZaDomain.Check_OK) {
			this._containedObject[ZaDomain.A_GALTestMessage] = arg.getBody().firstChild.childNodes[1].firstChild.nodeValue;		
			this._containedObject[ZaDomain.A_GALTestSearchResults] = null;				
		} else {
			//parse returned contacts
			this._containedObject[ZaDomain.A_GALTestSearchResults] = new Array();				
			var childNodes = arg.getBody().firstChild.childNodes;
			if(childNodes) {
				var cnt = childNodes.length;
				for(var i = 0; i < cnt; i ++) {
					if(childNodes[i].nodeName!="cn")
						continue;
						
					var attrNodes = childNodes[i].childNodes;
					if(attrNodes) {
						var cnObject = new Object();
						var countAttrs = attrNodes.length;
						for(var ix =0; ix < countAttrs; ix++) {
							var attrNode = attrNodes[ix];
							if (attrNode.nodeName != 'a') continue;
							if(attrNode.firstChild != null) {
								cnObject[attrNode.getAttribute("n")] = attrNode.firstChild.nodeValue;
							}
						}
						if(countAttrs)
							this._containedObject[ZaDomain.A_GALTestSearchResults].push(cnObject);
					}						
				}
			}		
		}
	}
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this.goPage(7);
}

/**
* Eevent handlers for form items
**/
ZaNewDomainXWizard.onAuthMechChange = 
function (value, event, form) {
	this.setInstanceValue(value);
	if(value == ZaDomain.AuthMech_ad) {
		if(!form.getInstance().attrs[ZaDomain.A_AuthADDomainName]) {
			form.getInstance().attrs[ZaDomain.A_AuthADDomainName] = form.getInstance().attrs[ZaDomain.A_domainName];
		}
	}
}

ZaNewDomainXWizard.prototype.testAuthSettings =
function () {
	if(this._containedObject.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ad) {
		this._containedObject.attrs[ZaDomain.A_AuthLdapUserDn] = "%u@"+this._containedObject.attrs[ZaDomain.A_AuthADDomainName];
	}

	var callback = new AjxCallback(this, this.checkAuthCallBack);
	ZaDomain.testAuthSettings(this._containedObject, callback);	
}

/**
* Callback function invoked by Asynchronous CSFE command when "check" call returns
**/
ZaNewDomainXWizard.prototype.checkAuthCallBack = 
function (arg) {
	if(arg instanceof AjxException || arg instanceof ZmCsfeException || arg instanceof AjxSoapException) {
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
			//this._containedObject[ZaDomain.A_AuthComputedBindDn] = arg.getBody().firstChild.lastChild.firstChild.nodeValue;		
		}
	}
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	this.goPage(13);
}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaNewDomainXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

ZaNewDomainXWizard.prototype.goPrev =
function () {
	if(this._containedObject[ZaModel.currentStep] == 7) {
		//skip 6th step
		this.goPage(5);
		this.changeButtonStateForStep(5);
	} else if (this._containedObject[ZaModel.currentStep] == 8 && this._containedObject.attrs[ZaDomain.A_GalMode]==ZaDomain.GAL_Mode_internal) {
		this.goPage(2);
		this.changeButtonStateForStep(2);		
	} else if (this._containedObject[ZaModel.currentStep] == 13) {
		//skip 12th step
		this.goPage(11);
		this.changeButtonStateForStep(11);		
	} else if(this._containedObject[ZaModel.currentStep] == 14) {
		if(this._containedObject.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_zimbra) {
			this.goPage(8); //skip all auth configuration
			this.changeButtonStateForStep(8);
		} else {
			this.goPage(13);
			this.changeButtonStateForStep(13);
		}
	} else {
		this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
		if(this._containedObject[ZaModel.currentStep] == 2) {
			this.changeButtonStateForStep(1);
		} else {
			this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]-1);
		}
		this.goPage(this._containedObject[ZaModel.currentStep]-1);
	}
}

ZaNewDomainXWizard.prototype.goNext = 
function() {
	if(this._containedObject[ZaModel.currentStep] == 3 && this._containedObject.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal) {	
		//check that Filter is provided and at least one server
		if(!this._containedObject.attrs[ZaDomain.A_GalLdapFilter]) {
			this._app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_SEARCH_FILTER_REQUIRED);			
			return;
		}
		if(!this._containedObject.attrs[ZaDomain.A_GalLdapURL] || this._containedObject.attrs[ZaDomain.A_GalLdapURL].length < 1) {
			this._app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_LDAP_URL_REQUIRED);					
			return;
		}
	} 
	if (this._containedObject[ZaModel.currentStep] == 1) {
		this._containedObject.attrs[ZaDomain.A_AuthADDomainName] = this._containedObject.attrs[ZaDomain.A_domainName];
		this.changeButtonStateForStep(2);
		this.goPage(2);		
	} else if(this._containedObject[ZaModel.currentStep] == 2 && this._containedObject.attrs[ZaDomain.A_GalMode]==ZaDomain.GAL_Mode_internal) {
		this.changeButtonStateForStep(8);
		this.goPage(8);
	} else if(this._containedObject[ZaModel.currentStep] == 4) {
		//clear the password if the checkbox is unchecked
		if(this._containedObject.attrs[ZaDomain.A_UseBindPassword]=="FALSE") {
			this._containedObject.attrs[ZaDomain.A_GalLdapBindPassword] = null;
			this._containedObject.attrs[ZaDomain.A_GalLdapBindPasswordConfirm] = null;
			this._containedObject.attrs[ZaDomain.A_GalLdapBindDn] = null;
		}
		//check that passwords match
		if(this._containedObject.attrs[ZaDomain.A_GalLdapBindPassword]!=this._containedObject.attrs[ZaDomain.A_GalLdapBindPasswordConfirm]) {
			this._app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
			return false;
		}
		//change next button to "test"
		this.goPage(5);
		this.changeButtonStateForStep(5);		
	} else if(this._containedObject[ZaModel.currentStep] == 5) {
		if(!this._containedObject[ZaDomain.A_GALSampleQuery]) {
			this._app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_SEARCH_TERM_REQUIRED);			
			return;
		}	
		this.goPage(6);
 		this.testGALSettings();
		this.changeButtonStateForStep(6);
	} else if (this._containedObject[ZaModel.currentStep] == 8) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		if(this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_zimbra) {
			this.goPage(14);		
			this.changeButtonStateForStep(14);
		} else {
			this.goPage(9);
			this.changeButtonStateForStep(9);			
		}
	} else if (this._containedObject[ZaModel.currentStep] == 10) {
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
		this.goPage(11);
		this.changeButtonStateForStep(11);
	} else if(this._containedObject[ZaModel.currentStep] == 11) {
		this.goPage(12);
 		this.testAuthSettings();
		this.changeButtonStateForStep(12);
	} else {
		this.goPage(this._containedObject[ZaModel.currentStep] + 1);
		this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]);
	}
}

ZaNewDomainXWizard.prototype.getMyXForm = 
function () {
	var xFormObject = {
		items: [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},		
			{type: _SWITCH_,
				items: [
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 1", relevantBehavior:_HIDE_,
						items: [
							{ref:ZaDomain.A_domainName, type:_TEXTFIELD_, label:ZaMsg.Domain_DomainName,labelLocation:_LEFT_, width:200},
							{ref:ZaDomain.A_description, type:_TEXTFIELD_, label:ZaMsg.NAD_Description, labelLocation:_LEFT_, width:250},
							{ref:ZaDomain.A_domainDefaultCOSId, type:_OSELECT1_, 
								label:ZaMsg.Domain_DefaultCOS, labelLocation:_LEFT_, 
								choices:this._app.getCosListChoices()
							},							
							{ref:ZaDomain.A_notes, type:_TEXTAREA_, label:ZaMsg.NAD_Notes, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", width:250}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 2", relevantBehavior:_HIDE_,
						items: [
							{ref:ZaDomain.A_GalMode, type:_OSELECT1_, label:ZaMsg.Domain_GalMode, labelLocation:_LEFT_, choices:this.GALModes, onChange:ZaNewDomainXWizard.onGalModeChange},
							{ref:ZaDomain.A_GalMaxResults, type:_TEXTFIELD_, label:ZaMsg.NAD_GalMaxResults, labelLocation:_LEFT_}					
						]
					},
					{type:_CASE_, numCols:2, relevant:"instance[ZaModel.currentStep] == 3 && instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal", relevantBehavior:_HIDE_,
						items: [
							{ref:ZaDomain.A_GALServerType, type:_OSELECT1_, label:ZaMsg.Domain_GALServerType, labelLocation:_LEFT_, choices:this.GALServerTypes, onChange:ZaNewDomainXWizard.onGALServerTypeChange},
							{type:_GROUP_, numCols:6, colSpan:6,label:"   ",labelLocation:_LEFT_,
								items: [
									{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
									{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerName, width:"200px"},
									{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
									{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerPort,  width:"40px"},	
									{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALUseSSL, width:"40px"}									
								]
							},
							{ref:ZaDomain.A_GalLdapURL, type:_REPEAT_, label:ZaMsg.Domain_GalLdapURL, repeatInstance:"", showAddButton:true, showRemoveButton:true,  
								addButtonLabel:ZaMsg.Domain_AddURL, 
								removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,
								showAddOnNextRow:true,
								items: [
									{ref:".", type:_LDAPURL_, label:null, labelLocation:_NONE_}
								]
							},
							{ref:ZaDomain.A_GalLdapFilter, type:_TEXTAREA_, width:380, height:100, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_GALServerType] == 'ldap'", relevantBehavior:_DISABLE_},
							{ref:ZaDomain.A_GalLdapSearchBase, type:_TEXTAREA_, width:380, height:100, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 4 && instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal", relevantBehavior:_HIDE_,
						items: [
							{ref:ZaDomain.A_UseBindPassword, type:_CHECKBOX_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaDomain.A_GalLdapBindDn, type:_TEXTFIELD_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
							{ref:ZaDomain.A_GalLdapBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
							{ref:ZaDomain.A_GalLdapBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPasswordConfirm, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_}														
						]			
					}, 
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 5", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_GAL_ConfigSummary}, 
							{ref:ZaDomain.A_GalMode, type:_OUTPUT_, label:ZaMsg.Domain_GalMode, choices:this.GALModes},
							{ref:ZaDomain.A_GalMaxResults, type:_OUTPUT_, label:ZaMsg.NAD_GalMaxResults},
							{type:_SWITCH_, 
								items: [
									{type:_CASE_, relevant:"instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal", relevantBehavior:_HIDE_,
										items: [
											{ref:ZaDomain.A_GALServerType, type:_OUTPUT_, label:ZaMsg.Domain_GALServerType, choices:this.GALServerTypes, labelLocation:_LEFT_},
/*											
											{ref:ZaDomain.A_GALServerName, type:_OUTPUT_, label:ZaMsg.Domain_GALServerName, labelLocation:_LEFT_},					
											{ref:ZaDomain.A_GALServerPort, type:_OUTPUT_, label:ZaMsg.Domain_GALServerPort, labelLocation:_LEFT_},
											{ref:ZaDomain.A_GALUseSSL, type:_OUTPUT_, label:ZaMsg.Domain_GALUseSSL, labelLocation:_LEFT_},
*/
											{ref:ZaDomain.A_GalLdapURL, type:_REPEAT_, label:ZaMsg.Domain_GalLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
												items:[
													{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
												]
											},											
											{ref:ZaDomain.A_GalLdapFilter, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_GALServerType] == 'ldap'", relevantBehavior:_HIDE_},
											{ref:ZaDomain.A_GalLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_},
										//	{ref:ZaDomain.A_GalLdapURL, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapURL, labelLocation:_LEFT_},
											{ref:ZaDomain.A_UseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE"},
											{ref:ZaDomain.A_GalLdapBindDn, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_HIDE_},
										//	{ref:ZaDomain.A_GalLdapBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
											{ref:ZaDomain.A_GALSampleQuery, type:_TEXTFIELD_, label:ZaMsg.Domain_GALSampleSearchName, labelLocation:_LEFT_, labelWrap:true, cssStyle:"width:100px;"}
										]
									}
								]
							}					
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 6", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_GALTestingInProgress}
						]	
					}, 
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 7", relevantBehavior:_HIDE_,
						items: [
							{type:_SWITCH_,
								items: [
									{type:_CASE_, relevant:"instance[ZaDomain.A_GALTestResultCode] == ZaDomain.Check_OK", numCols:2,
										items: [
											{type:_OUTPUT_, value:ZaMsg.Domain_GALTestSuccessful, colSpan:2},
											{type:_OUTPUT_, value:ZaMsg.Domain_GALSearchResult,  align:_CENTER_, colSpan:2},											
											{type:_SPACER_,  align:_CENTER_, valign:_TOP_, colSpan:"*"},	
											{type:_REPEAT_, ref:ZaDomain.A_GALTestSearchResults, colSpan:2, label:null, showAddButton:false, showRemoveButton:false,  
												items: [
													{ref:"email", type:_OUTPUT_, label:ZaMsg.ALV_Name_col+":"},
													{ref:"fullName", type:_OUTPUT_, label:ZaMsg.ALV_FullName_col+":"}
												]
											}
										]
									},
									{type:_CASE_, relevant:	"instance[ZaDomain.A_GALTestResultCode] != ZaDomain.Check_OK",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Domain_GALTestFailed, colSpan:2},
											{type:_OUTPUT_, ref:ZaDomain.A_GALTestResultCode, label:ZaMsg.Domain_GALTestResult, choices:this.TestResultChoices},
											{type:_TEXTAREA_, ref:ZaDomain.A_GALTestMessage, label:ZaMsg.Domain_GALTestMessage, height:100,width:"380px"}
										]
									}
								]
							}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 8", relevantBehavior:_HIDE_,
						items:[
							{type:_OSELECT1_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs, ref:ZaDomain.A_AuthMech, onChange:ZaNewDomainXWizard.onAuthMechChange}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 9", relevantBehavior:_HIDE_,												
						items:[
							{type:_SWITCH_,
								items: [
									{type:_CASE_, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad",
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
											{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, repeatInstance:"", showAddButton:true, showRemoveButton:true,  
												addButtonLabel:ZaMsg.Domain_AddURL, 
												showAddOnNextRow:true,	
												removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,																						
												items: [
													{ref:".", type:_LDAPURL_, label:null, labelLocation:_NONE_}
												]
											}									
										]
									},
									{type:_CASE_, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap",
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
												showAddOnNextRow:true,												
												removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,
												items: [
													{ref:".", type:_LDAPURL_, label:null, labelLocation:_NONE_}
												]
											},
											{ref:ZaDomain.A_AuthLdapSearchFilter, type:_TEXTAREA_, width:380, height:100, label:ZaMsg.Domain_AuthLdapFilter, labelLocation:_LEFT_, textWrapping:"soft"},
											{ref:ZaDomain.A_AuthLdapSearchBase, type:_TEXTAREA_, width:380, height:50, label:ZaMsg.Domain_AuthLdapSearchBase, labelLocation:_LEFT_, textWrapping:"soft"},
											{type:_OUTPUT_, value:ZaMsg.NAD_DomainsAuthStr, colSpan:2}
										]
									},
									{type:_CASE_, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_zimbra",
										items:[
											{type:_OUTPUT_, value:ZaMsg.Domain_Auth_Config_Complete}
										]
									}
								]
							}
						]
					},
					{type:_CASE_, numCols:2, relevant:"instance[ZaModel.currentStep] == 10 && instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap", relevantBehavior:_HIDE_,
						items: [
							{ref:ZaDomain.A_AuthUseBindPassword, type:_CHECKBOX_, label:ZaMsg.Domain_AuthUseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaDomain.A_AuthLdapSearchBindDn, type:_INPUT_, label:ZaMsg.Domain_AuthLdapBindDn, labelLocation:_LEFT_, relevant:"instance[ZaDomain.A_AuthUseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
							{ref:ZaDomain.A_AuthLdapSearchBindPassword, type:_SECRET_, label:ZaMsg.Domain_AuthLdapBindPassword, labelLocation:_LEFT_, relevant:"instance[ZaDomain.A_AuthUseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
							{ref:ZaDomain.A_AuthLdapSearchBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_AuthLdapBindPasswordConfirm, labelLocation:_LEFT_, relevant:"instance[ZaDomain.A_AuthUseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_}							
						]						
					},
					{type:_CASE_, numCols:2, relevant:"instance[ZaModel.currentStep] == 11 && instance.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_zimbra", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_Auth_ConfigSummary, align:_CENTER_, colSpan:"*"}, 
							{type:_SPACER_, height:10},
							{ref:ZaDomain.A_AuthMech, type:_OUTPUT_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs, alignment:_LEFT_},
							{type:_SWITCH_, useParentTable:true,
								items: [
									{type:_CASE_, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad", useParentTable:true,
										items:[
											{ref:ZaDomain.A_AuthADDomainName, type:_OUTPUT_, label:ZaMsg.Domain_AuthADDomainName, labelLocation:_LEFT_},
/*											
											{ref:ZaDomain.A_AuthLDAPServerName, type:_OUTPUT_, label:ZaMsg.Domain_AuthADServerName, labelCssClass:"xform_label_left"},											
											{ref:ZaDomain.A_AuthLDAPServerPort, type:_OUTPUT_, label:ZaMsg.Domain_AuthADServerPort, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthLDAPUseSSL, type:_OUTPUT_, label:ZaMsg.Domain_AuthADUseSSL, labelWrap:true, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES}
*/
											{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
												items:[
													{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
												]
											}											
										]
									},
									{type:_CASE_, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap", useParentTable:true,
										items:[
/*										
											{ref:ZaDomain.A_AuthLDAPServerName, type:_OUTPUT_, label:ZaMsg.Domain_AuthLDAPServerName, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthLDAPServerPort, type:_OUTPUT_, label:ZaMsg.Domain_AuthLDAPServerPort, labelLocation:_LEFT_},							
											{ref:ZaDomain.A_AuthLDAPUseSSL, type:_OUTPUT_, label:ZaMsg.Domain_AuthLDAPUseSSL, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES},
*/
											{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
												items:[
													{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
												]
											},											
//											{ref:ZaDomain.A_AuthLdapURL, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthLdapSearchFilter, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapFilter, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapSearchBase, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthUseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_AuthUseBindPassword, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES},											
											{ref:ZaDomain.A_AuthLdapSearchBindDn, type:_INPUT_, label:ZaMsg.Domain_AuthLdapBindDn, labelLocation:_LEFT_, relevant:"instance[ZaDomain.A_AuthUseBindPassword] == 'TRUE'", relevantBehavior:_HIDE_}											
										]
									}
								]
							},
							{type:_SPACER_, height:10},
							{type:_OUTPUT_,value:ZaMsg.Domain_AuthProvideLoginPwd, align:_CENTER_, colSpan:"*"},
							{type:_INPUT_, label:ZaMsg.Domain_AuthTestUserName, ref:ZaDomain.A_AuthTestUserName, alignment:_LEFT_},
							{type:_SECRET_, label:ZaMsg.Domain_AuthTestPassword, ref:ZaDomain.A_AuthTestPassword, alignment:_LEFT_}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 12 && instance.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_zimbra", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_,value:ZaMsg.Domain_AuthTestingInProgress}
						]
					},
					{type:_CASE_,  numCols:1, relevant:"instance[ZaModel.currentStep] == 13 && instance.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_zimbra", relevantBehavior:_HIDE_,
						items: [
//							{type:_OUTPUT_,value:ZaMsg.Domain_AuthTestResults, alignment:_CENTER_},
							{type:_SWITCH_,
								items: [
									{type:_CASE_, relevant:"instance[ZaDomain.A_AuthTestResultCode] == ZaDomain.Check_OK",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Domain_AuthTestSuccessful, alignment:_CENTER_}
										]
									},
									{type:_CASE_, relevant:	"instance[ZaDomain.A_AuthTestResultCode] != ZaDomain.Check_OK",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Domain_AuthTestFailed, alignment:_CENTER_, colSpan:2, label:null},
											{type:_OUTPUT_, ref:ZaDomain.A_AuthTestResultCode, label:ZaMsg.Domain_AuthTestResultCode, choices:this.TestResultChoices, alignment:_LEFT_},
											{type:_OUTPUT_, ref:ZaDomain.A_AuthComputedBindDn, label:ZaMsg.Domain_AuthComputedBindDn, alignment:_LEFT_, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad", relevantBehavior:_HIDE_},
											{type:_TEXTAREA_, ref:ZaDomain.A_AuthTestMessage, label:ZaMsg.Domain_AuthTestMessage, height:150, alignment:_LEFT_, width:"320px"}
										]
									}
								]
							}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 14", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_Config_Complete}
						]
					}										
				]	
			}
	
		]
	};
	return xFormObject;
};

