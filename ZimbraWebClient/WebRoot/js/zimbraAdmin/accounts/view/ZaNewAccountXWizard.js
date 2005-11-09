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

function ZaNewAccountXWizard (parent, app) {
	ZaXWizardDialog.call(this, parent, null, ZaMsg.NCD_NewAccTitle, "550px", "300px");
	this._app = app;
	this.accountStatusChoices = [ZaAccount.ACCOUNT_STATUS_ACTIVE, ZaAccount.ACCOUNT_STATUS_MAINTENANCE, ZaAccount.ACCOUNT_STATUS_LOCKED, ZaAccount.ACCOUNT_STATUS_CLOSED];		
	this.stepChoices = [
		{label:ZaMsg.TABT_GeneralPage, value:1},
		{label:ZaMsg.TABT_ContactInfo, value:2}, 
		{label:ZaMsg.TABT_Features, value:3},
		{label:ZaMsg.TABT_Preferences, value:4},
		{label:ZaMsg.TABT_Aliases, value:5},		
		{label:ZaMsg.TABT_Forwarding, value:6},				
		{label:ZaMsg.TABT_Advanced, value:7}						
	];
	this.initForm(ZaAccount.myXModel,this.getMyXForm());	
	this._localXForm.setController(this._app);	
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaNewAccountXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaNewAccountXWizard.prototype.handleXFormChange));	
	this._helpURL = "/zimbraAdmin/adminhelp/html/OpenSourceAdminHelp/managing_accounts/create_an_account.htm";		
}


ZaNewAccountXWizard.prototype = new ZaXWizardDialog;
ZaNewAccountXWizard.prototype.constructor = ZaNewAccountXWizard;

ZaNewAccountXWizard.prototype.handleXFormChange = 
function () {
	if(this._localXForm.hasErrors()) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		if(this._containedObject.attrs[ZaAccount.A_lastName] && this._containedObject[ZaAccount.A_name])
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	}
}

/*
ZaNewAccountXWizard.onNameFieldChanged = 
function (value, event, form) {
	if(value && value.length > 0) {
		form.parent._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	} else {
		form.parent._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	}
	this.setInstanceValue(value);
	return value;
}*/

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaNewAccountXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

ZaNewAccountXWizard.prototype.finishWizard = 
function() {
	try {
		
		if(!ZaAccount.checkValues(this._containedObject, this._app)) {
			return false;
		}
		var account = ZaAccount.create(this._containedObject, this._app);
		if(account != null) {
			//if creation took place - fire an DomainChangeEvent
			this._app.getAccountViewController()._fireAccountCreationEvent(account);
			this.popdown();		
		}
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex, "ZaNewAccountXWizard.prototype.finishWizard", null, false);
	}
}

ZaNewAccountXWizard.prototype.goNext = 
function() {
	if (this._containedObject[ZaModel.currentStep] == 1) {
		//check if passwords match
		if(this._containedObject.attrs[ZaAccount.A_password]) {
			if(this._containedObject.attrs[ZaAccount.A_password] != this._containedObject[ZaAccount.A2_confirmPassword]) {
				this._app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
				return false;
			}
		}
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		
	} else if(this._containedObject[ZaModel.currentStep] == 6) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	}
	this.goPage(this._containedObject[ZaModel.currentStep] + 1);
}

ZaNewAccountXWizard.prototype.goPrev = 
function() {
	if (this._containedObject[ZaModel.currentStep] == 2) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	} else if(this._containedObject[ZaModel.currentStep] == 7) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	}
	this.goPage(this._containedObject[ZaModel.currentStep] - 1);
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaAccount object to display
**/
ZaNewAccountXWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();

	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
	this._containedObject.name = "";

	this._containedObject.id = null;
		
	var cosList = this._app.getCosList().getArray();
	for(var ix in cosList) {
		if(cosList[ix].name == "default") {
			this._containedObject.attrs[ZaAccount.A_COSId] = cosList[ix].id;
			this._containedObject.cos = cosList[ix];
			break;
		}
	}

	if(!this._containedObject.cos) {
		this._containedObject.cos = cosList[0];
		this._containedObject.attrs[ZaAccount.A_COSId] = cosList[0].id;
	}	
	this._containedObject.attrs[ZaAccount.A_accountStatus] = ZaAccount.ACCOUNT_STATUS_ACTIVE;
	this._containedObject[ZaAccount.A2_autodisplayname] = "TRUE";
	this._containedObject[ZaAccount.A2_autoMailServer] = "TRUE";
	this._containedObject[ZaAccount.A2_confirmPassword] = null;
	this._containedObject[ZaModel.currentStep] = 1;
	this._containedObject.attrs[ZaAccount.A_zimbraMailAlias] = new Array();
//	var domainName = this._app._appCtxt.getAppController().getOverviewPanelController().getCurrentDomain();
	var domainName;
	if(!domainName) {
		//find out what is the default domain
		domainName = this._app.getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraDefaultDomainName];
		if(!domainName) {
			domainName = this._app.getDomainList().getArray()[0].name;
		}
	}
	this._containedObject[ZaAccount.A_name] = "@" + domainName;
	this._localXForm.setInstance(this._containedObject);
}

ZaNewAccountXWizard.onCOSChanged = 
function(value, event, form) {
	var cosList = form.getController().getCosList().getArray();
	var cnt = cosList.length;
	for(var i = 0; i < cnt; i++) {
		if(cosList[i].id == value) {
			form.getInstance().cos = cosList[i];
			break;
		}
	}
	this.setInstanceValue(value);
	return value;
}

ZaNewAccountXWizard.prototype.getMyXForm = function() {	
//	var domainName = this._app._appCtxt.getAppController().getOverviewPanelController().getCurrentDomain();
	var domainName;
	if(!domainName) {
		domainName = this._app.getDomainList().getArray()[0].name;
	}
	var emptyAlias = "@" + domainName;
	var xFormObject = {
		items: [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:500, align:_LEFT_, valign:_TOP_, 
				items:[
					{type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentStep] == 1", align:_LEFT_, valign:_TOP_, 
						items:[
							{ref:ZaAccount.A_name, type:_EMAILADDR_, msgName:ZaMsg.NAD_AccountName,label:ZaMsg.NAD_AccountName+":", labelLocation:_LEFT_},
							{ref:ZaAccount.A_COSId, type:_OSELECT1_, msgName:ZaMsg.NAD_ClassOfService+":",label:ZaMsg.NAD_ClassOfService+":", labelLocation:_LEFT_, choices:this._app.getCosListChoices(), onChange:ZaNewAccountXWizard.onCOSChanged},
							{ref:ZaAccount.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,label:ZaMsg.NAD_Password+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input"},														
							{ref:ZaAccount.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,label:ZaMsg.NAD_ConfirmPassword+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input"},
							{ref:ZaAccount.A_firstName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_FirstName,label:ZaMsg.NAD_FirstName+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input",
								elementChanged: function(elementValue,instanceValue, event) {
									if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
										ZaAccountXFormView.generateDisplayName(this.getInstance(), elementValue, this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials] );
									}
									this.getForm().itemChanged(this, elementValue, event);
								}
							},
							{ref:ZaAccount.A_initials, type:_TEXTFIELD_, msgName:ZaMsg.NAD_Initials,label:ZaMsg.NAD_Initials+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input",
								elementChanged: function(elementValue,instanceValue, event) {
									if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
										ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],elementValue);
									}
									this.getForm().itemChanged(this, elementValue, event);
								}
							},	
							{ref:ZaAccount.A_lastName, type:_TEXTFIELD_, msgName:ZaMsg.NAD_LastName,label:ZaMsg.NAD_LastName+":", labelLocation:_LEFT_, required:true, cssClass:"admin_xform_name_input",
								elementChanged: function(elementValue,instanceValue, event) {
									if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
										ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], elementValue ,this.getInstance().attrs[ZaAccount.A_initials]);
									}
									this.getForm().itemChanged(this, elementValue, event);
								}
							},
							{type:_GROUP_, numCols:3, nowrap:true,  msgName:ZaMsg.NAD_DisplayName,label:ZaMsg.NAD_DisplayName+":", labelLocation:_LEFT_,
								items: [
									{ref:ZaAccount.A_displayname, type:_TEXTFIELD_,	cssClass:"admin_xform_name_input",  label:null,
										relevant:"instance[ZaAccount.A2_autodisplayname] == \"FALSE\"",
										relevantBehavior:_DISABLE_
									},
									{ref:ZaAccount.A2_autodisplayname, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE",
										elementChanged: function(elementValue,instanceValue, event) {
											if(elementValue=="TRUE") {
												if(ZaAccountXFormView.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials])) {
													this.getForm().itemChanged(this, elementValue, event);
												}
											}
											this.getForm().itemChanged(this, elementValue, event);
										}
									}
								]
							},
							{ref:ZaAccount.A_accountStatus, type:_OSELECT1_, msgName:ZaMsg.NAD_AccountStatus,label:ZaMsg.NAD_AccountStatus+":", labelLocation:_LEFT_, choices:this.accountStatusChoices},
							{ref:ZaAccount.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,label:ZaMsg.NAD_Description+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input"},
							{type:_GROUP_, numCols:3, nowrap:true, label:ZaMsg.NAD_MailServer, labelLocation:_LEFT_,
								items: [
									{ ref: ZaAccount.A_mailHost, type: _OSELECT1_, label: null, choices: this._app.getServerListChoices2(), 
										relevant:"instance[ZaAccount.A2_autoMailServer]==\"FALSE\" && form.getController().getServerListChoices2().getChoices().values.length != 0",
										relevantBehavior:_DISABLE_
								  	},
									{ref:ZaAccount.A2_autoMailServer, type:_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE"}
								]
							}
						]
					}, 
					{type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentStep] == 2",
						items: [
							{ref:ZaAccount.A_telephoneNumber, type:_TEXTFIELD_, msgName:ZaMsg.NAD_telephoneNumber,label:ZaMsg.NAD_telephoneNumber+":", labelLocation:_LEFT_, width:150},
							{ref:ZaAccount.A_company, type:_TEXTFIELD_, msgName:ZaMsg.NAD_company,label:ZaMsg.NAD_company+":", labelLocation:_LEFT_, width:150},
							{ref:ZaAccount.A_orgUnit, type:_TEXTFIELD_, msgName:ZaMsg.NAD_orgUnit,label:ZaMsg.NAD_orgUnit+":", labelLocation:_LEFT_, width:150},														
							{ref:ZaAccount.A_office, type:_TEXTFIELD_, msgName:ZaMsg.NAD_office,label:ZaMsg.NAD_office+":", labelLocation:_LEFT_, width:150},
							{ref:ZaAccount.A_postalAddress, type:_TEXTFIELD_, msgName:ZaMsg.NAD_postalAddress,label:ZaMsg.NAD_postalAddress+":", labelLocation:_LEFT_, width:150},
							{ref:ZaAccount.A_city, type:_TEXTFIELD_, msgName:ZaMsg.NAD_city,label:ZaMsg.NAD_city+":", labelLocation:_LEFT_, width:150},
							{ref:ZaAccount.A_state, type:_TEXTFIELD_, msgName:ZaMsg.NAD_state,label:ZaMsg.NAD_state+":", labelLocation:_LEFT_, width:150},
							{ref:ZaAccount.A_zip, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zip,label:ZaMsg.NAD_zip+":", labelLocation:_LEFT_, width:150},
							{ref:ZaAccount.A_country, type:_TEXTFIELD_, msgName:ZaMsg.NAD_country,label:ZaMsg.NAD_country+":", labelLocation:_LEFT_, width:150}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 3",
						items: [
							{ref:ZaAccount.A_zimbraFeatureContactsEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureContactsEnabled,label:ZaMsg.NAD_FeatureContactsEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
							{ref:ZaAccount.A_zimbraFeatureCalendarEnabled,labelCssStyle:"width:150px;",type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureCalendarEnabled,label:ZaMsg.NAD_FeatureCalendarEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},														
							{ref:ZaAccount.A_zimbraFeatureTaggingEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureTaggingEnabled,label:ZaMsg.NAD_FeatureTaggingEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureAdvancedSearchEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureAdvancedSearchEnabled,label:ZaMsg.NAD_FeatureAdvancedSearchEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSavedSearchesEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureSavedSearchesEnabled,label:ZaMsg.NAD_FeatureSavedSearchesEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureConversationsEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureConversationsEnabled,label:ZaMsg.NAD_FeatureConversationsEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureChangePasswordEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureChangePasswordEnabled,label:ZaMsg.NAD_FeatureChangePasswordEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled,label:ZaMsg.NAD_FeatureInitialSearchPreferenceEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE"},
							{ref:ZaAccount.A_zimbraFeatureFiltersEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureFiltersEnabled,label:ZaMsg.NAD_FeatureFiltersEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureHtmlComposeEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled,label:ZaMsg.NAD_zimbraFeatureHtmlComposeEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
							{ref:ZaAccount.A_zimbraFeatureGalEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_FeatureGalEnabled,label:ZaMsg.NAD_FeatureGalEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraImapEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraImapEnabled,label:ZaMsg.NAD_zimbraImapEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraPop3Enabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPop3Enabled,label:ZaMsg.NAD_zimbraPop3Enabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"}								
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 4", 
						items :[
							{ref:ZaAccount.A_prefSaveToSent,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_prefSaveToSent,label:ZaMsg.NAD_prefSaveToSent+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraPrefMessageViewHtmlPreferred,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred,label:ZaMsg.NAD_zimbraPrefMessageViewHtmlPreferred+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraPrefComposeInNewWindow,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefComposeInNewWindow,label:ZaMsg.NAD_zimbraPrefComposeInNewWindow+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
							{ref:ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat,label:ZaMsg.NAD_zimbraPrefForwardReplyInOriginalFormat+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},														
							{ref:ZaAccount.A_zimbraPrefComposeFormat,labelCssStyle:"width:150px;", type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefComposeFormat,label:ZaMsg.NAD_zimbraPrefComposeFormat+":", labelLocation:_LEFT_},							
							{ref:ZaAccount.A_zimbraPrefAutoAddAddressEnabled,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled,label:ZaMsg.NAD_zimbraPrefAutoAddAddressEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
							{type:_SEPARATOR_},
							{ref:ZaAccount.A_zimbraPrefGroupMailBy,labelCssStyle:"width:150px;", type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefGroupMailBy,label:ZaMsg.NAD_zimbraPrefGroupMailBy+":", labelLocation:_LEFT_},							
							{ref:ZaAccount.A_zimbraPrefContactsPerPage,labelCssStyle:"width:150px;", type:_SUPER_TEXTFIELD_, msgName:ZaMsg.NAD_PrefContactsPerPage,label:ZaMsg.NAD_PrefContactsPerPage+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, cssClass:"admin_xform_number_input", valueLabel:null},							
							{ref:ZaAccount.A_zimbraPrefMailItemsPerPage,labelCssStyle:"width:150px;", type:_SUPER_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefMailItemsPerPage,label:ZaMsg.NAD_zimbraPrefMailItemsPerPage+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, cssClass:"admin_xform_number_input", valueLabel:null},
							{ref:ZaAccount.A_zimbraPrefMailInitialSearch,labelCssStyle:"width:150px;", type:_SUPER_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefMailInitialSearch,label:ZaMsg.NAD_zimbraPrefMailInitialSearch+":", labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, cssClass:"admin_xform_name_input", valueLabel:null},
							{ref:ZaAccount.A_zimbraPrefShowSearchString,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefShowSearchString,label:ZaMsg.NAD_zimbraPrefShowSearchString+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_alwaysShowMiniCal,label:ZaMsg.NAD_alwaysShowMiniCal+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
							{ref:ZaAccount.A_zimbraPrefCalendarUseQuickAdd,labelCssStyle:"width:150px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_useQuickAdd,label:ZaMsg.NAD_useQuickAdd+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
							{type:_SEPARATOR_},							
							{ref:ZaAccount.A_zimbraPrefNewMailNotificationEnabled,labelCssStyle:"width:150px;", type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled,label:ZaMsg.NAD_zimbraPrefNewMailNotificationEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaAccount.A_zimbraPrefNewMailNotificationAddress,labelCssStyle:"width:150px;", type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress,label:ZaMsg.NAD_zimbraPrefNewMailNotificationAddress+":", labelLocation:_LEFT_, cssClass:"admin_xform_name_input"},							
							{type:_SEPARATOR_},
							{ref:ZaAccount.A_prefMailSignatureEnabled,labelCssStyle:"width:150px;", type:_CHECKBOX_, msgName:ZaMsg.NAD_prefMailSignatureEnabled,label:ZaMsg.NAD_prefMailSignatureEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaAccount.A_zimbraPrefMailSignatureStyle,labelCssStyle:"width:150px;", type:_SUPER_SELECT1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraPrefMailSignatureStyle,label:ZaMsg.NAD_zimbraPrefMailSignatureStyle+":", labelLocation:_LEFT_},
							{ref:ZaAccount.A_prefMailSignature,labelCssStyle:"width:150px;", type:_TEXTAREA_, msgName:ZaMsg.NAD_prefMailSignature,label:ZaMsg.NAD_prefMailSignature+":", labelLocation:_LEFT_, labelCssStyle: "vertical-align:top"},
							{type:_SEPARATOR_},
							{ref:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled,labelCssStyle:"width:150px;", type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled,label:ZaMsg.NAD_zimbraPrefOutOfOfficeReplyEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaAccount.A_zimbraPrefOutOfOfficeReply,labelCssStyle:"width:150px;", type:_TEXTAREA_, msgName:ZaMsg.NAD_zimbraPrefOutOfOfficeReply,label:ZaMsg.NAD_zimbraPrefOutOfOfficeReply+":", labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", cssStyle:"width:120px"}
						]
					},
					{type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentStep] == 5",
						items: [
							{type:_OUTPUT_, value:ZaMsg.NAD_AccountAliases},
							{ref:ZaAccount.A_zimbraMailAlias, type:_REPEAT_, label:null, repeatInstance:emptyAlias, showAddButton:true, showRemoveButton:true, 
								addButtonLabel:ZaMsg.NAD_AddAlias, 
								showAddOnNextRow:true,
								removeButtonLabel:ZaMsg.NAD_RemoveAlias,
								items: [
									{ref:".", type:_EMAILADDR_, label:null}
								]
							}
						]
					},
					{type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentStep] == 6",
						items: [
							{type:_OUTPUT_, value:ZaMsg.NAD_AccountForwarding},
							{ref:ZaAccount.A_zimbraMailForwardingAddress, type:_REPEAT_, label:null, repeatInstance:emptyAlias, showAddButton:true, showRemoveButton:true, 
								addButtonLabel:ZaMsg.NAD_AddAddress, 
								showAddOnNextRow:true,
								removeButtonLabel:ZaMsg.NAD_RemoveAddress,
							
								items: [
									{ref:".", type:_TEXTFIELD_, label:null, width:250}
								]
							}
						]
					},
					{type:_CASE_, numCols:1, relevant:"instance[ZaModel.currentStep]==7", 
						items: [
							{type:_GROUP_, 
								items :[
									{ref:ZaAccount.A_zimbraAttachmentsBlocked,labelCssStyle:"width:160px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_RemoveAllAttachments,label:ZaMsg.NAD_RemoveAllAttachments+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},
									{ref:ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,labelCssStyle:"width:160px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_AttachmentsViewInHtmlOnly,label:ZaMsg.NAD_AttachmentsViewInHtmlOnly+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},	
									{ref:ZaAccount.A_zimbraAttachmentsIndexingEnabled,labelCssStyle:"width:160px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled,label:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"}
								]
							},
							{type:_SEPARATOR_, colSpan:"*"},
							{type:_GROUP_, 
								items: [
									{ref:ZaAccount.A_zimbraMailQuota,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailQuota,label:ZaMsg.NAD_MailQuota+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input", resetToSuperLabel:ZaMsg.NAD_ResetToCOS},
									{ref:ZaAccount.A_zimbraContactMaxNumEntries,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_ContactMaxNumEntries,label:ZaMsg.NAD_ContactMaxNumEntries+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input"}
								]
							},
							{type:_SEPARATOR_, colSpan:"*"},
							{type:_GROUP_, 
								items: [
									{ref:ZaAccount.A_zimbraMinPwdLength,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMinLength,label:ZaMsg.NAD_passMinLength+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraMaxPwdLength,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxLength,label:ZaMsg.NAD_passMaxLength+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraMinPwdAge,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMinAge,label:ZaMsg.NAD_passMinAge+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraMaxPwdAge,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passMaxAge,label:ZaMsg.NAD_passMaxAge+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraEnforcePwdHistory,labelCssStyle:"width:160px;", type:_SUPER_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_passEnforceHistory,label:ZaMsg.NAD_passEnforceHistory+":", labelLocation:_LEFT_, cssClass:"admin_xform_number_input"},
									{ref:ZaAccount.A_zimbraPasswordLocked,labelCssStyle:"width:160px;", type:_SUPER_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_PwdLocked,label:ZaMsg.NAD_PwdLocked+":", labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"}
								]
							},
							{type:_SEPARATOR_, colSpan:"*"},							
							{type:_GROUP_, 
								items: [
									{ref:ZaAccount.A_zimbraAuthTokenLifetime,labelCssStyle:"width:160px;", type:_SUPER_LIFETIME_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_AuthTokenLifetime,label:ZaMsg.NAD_AuthTokenLifetime+":",labelLocation:_LEFT_},								
									{ref:ZaAccount.A_zimbraMailMessageLifetime,labelCssStyle:"width:160px;", type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailMessageLifetime,label:ZaMsg.NAD_MailMessageLifetime+":",labelLocation:_LEFT_},
									{ref:ZaAccount.A_zimbraMailTrashLifetime,labelCssStyle:"width:160px;", type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailTrashLifetime,label:ZaMsg.NAD_MailTrashLifetime+":", labelLocation:_LEFT_},
									{ref:ZaAccount.A_zimbraMailSpamLifetime,labelCssStyle:"width:160px;", type:_SUPER_LIFETIME1_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_MailSpamLifetime,label:ZaMsg.NAD_MailSpamLifetime+":", labelLocation:_LEFT_}
								]
							},
							{type:_SEPARATOR_, colSpan:"*"},
							{type:_GROUP_, 
								items: [
									{ref:ZaAccount.A_isAdminAccount,labelCssStyle:"width:160px;", type:_CHECKBOX_, msgName:ZaMsg.NAD_IsAdmin,label:ZaMsg.NAD_IsAdmin+":",labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label"},								
									{ref:ZaAccount.A_zimbraPasswordMustChange,labelCssStyle:"width:160px;", type:_CHECKBOX_,  msgName:ZaMsg.NAD_MustChangePwd,label:ZaMsg.NAD_MustChangePwd+":",labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label"}
								]
							}
				
						]
					}
				]
			}
		]
	};
	return xFormObject;
};
