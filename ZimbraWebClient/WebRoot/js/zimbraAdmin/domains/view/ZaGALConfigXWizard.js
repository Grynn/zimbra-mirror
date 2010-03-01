/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaGALConfigXWizard
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaGALConfigXWizard = function(parent, entry) {
	ZaXWizardDialog.call(this, parent, null, ZaMsg.NCD_GALConfigTitle, "700px", "350px","ZaGALConfigXWizard");

	this.TAB_INDEX = 0;
	
	ZaGALConfigXWizard.GALMODE_STEP = ++this.TAB_INDEX;
	//ZaGALConfigXWizard.GAL_CONFIG_STEP_1 = ++this.TAB_INDEX;
	ZaGALConfigXWizard.GAL_CONFIG_STEP_2 = ++this.TAB_INDEX;
	ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_1 = ++this.TAB_INDEX;
	ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_2 = ++this.TAB_INDEX;
	ZaGALConfigXWizard.GAL_CONFIG_SUM_STEP = ++this.TAB_INDEX;
	//ZaGALConfigXWizard.GAL_TEST_STEP = ++this.TAB_INDEX;
	ZaGALConfigXWizard.GAL_TEST_RESULT_STEP = ++this.TAB_INDEX;
	ZaGALConfigXWizard.SYNC_CONFIG_SUM_STEP = ++this.TAB_INDEX;
	ZaGALConfigXWizard.SYNC_TEST_RESULT_STEP = ++this.TAB_INDEX;
	ZaGALConfigXWizard.CONFIG_COMPLETE_STEP = ++this.TAB_INDEX;
	
	this.stepChoices = [
		{label:ZaMsg.GALMode, value:ZaGALConfigXWizard.GALMODE_STEP},
		//{label:ZaMsg.GALConfiguration, value:ZaGALConfigXWizard.GAL_CONFIG_STEP_1}, 
		{label:ZaMsg.GALConfiguration, value:ZaGALConfigXWizard.GAL_CONFIG_STEP_2},		
		{label:ZaMsg.GALSyncConfiguration, value:ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_1},
		{label:ZaMsg.GALSyncConfiguration, value:ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_2},
		{label:ZaMsg.GALConfigSummary, value:ZaGALConfigXWizard.GAL_CONFIG_SUM_STEP},		
//		{label:ZaMsg.TestGalConfig, value:ZaGALConfigXWizard.GAL_TEST_STEP},
		{label:ZaMsg.GalTestResult, value:ZaGALConfigXWizard.GAL_TEST_RESULT_STEP},
		{label:ZaMsg.SyncConfigSummary, value:ZaGALConfigXWizard.SYNC_CONFIG_SUM_STEP},
		{label:ZaMsg.SyncTestResult, value:ZaGALConfigXWizard.SYNC_TEST_RESULT_STEP},
		{label:ZaMsg.DomainConfigComplete, value:ZaGALConfigXWizard.CONFIG_COMPLETE_STEP}	
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

		
	this.initForm(ZaDomain.myXModel,this.getMyXForm(entry), null);		
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaGALConfigXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaGALConfigXWizard.prototype.handleXFormChange));	
	this.lastErrorStep=0;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_domains/using_the_global_address_list_(gal).htm?locid="+AjxEnv.DEFAULT_LOCALE
	
}

ZaGALConfigXWizard.prototype = new ZaXWizardDialog;
ZaGALConfigXWizard.prototype.constructor = ZaGALConfigXWizard;
ZaXDialog.XFormModifiers["ZaGALConfigXWizard"] = new Array();




ZaGALConfigXWizard.prototype.handleXFormChange = 
function () {
	if(this._localXForm.hasErrors()) {
		if(this.lastErrorStep < this._containedObject[ZaModel.currentStep])
			this.lastErrorStep=this._containedObject[ZaModel.currentStep];
	} else {
		this.lastErrorStep=0;
	}
	this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]);	
}

ZaGALConfigXWizard.prototype.changeButtonStateForStep = 
function(stepNum) {
	if(this.lastErrorStep == stepNum) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		if(stepNum>1)
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	} else {
		if (stepNum == ZaGALConfigXWizard.GALMODE_STEP) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		} else if(stepNum == ZaGALConfigXWizard.GAL_CONFIG_SUM_STEP) {
			//change next button to "test"
			//this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_GALTestSettings);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		} else if(stepNum == ZaGALConfigXWizard.GAL_TEST_STEP) {
			//this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if (stepNum == ZaGALConfigXWizard.GAL_TEST_RESULT_STEP) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else if (stepNum == ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_1 || stepNum == ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_2) {
			//this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		} else if(stepNum == ZaGALConfigXWizard.CONFIG_COMPLETE_STEP) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
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
ZaGALConfigXWizard.prototype.setObject =
function(entry) {
	this._containedObject = new ZaDomain();
	//this._containedObject.attrs = new Object();

	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}

	this._containedObject.name = entry.name;
	this._containedObject.type = entry.type ;
	this._containedObject.id = entry.id;
	this._containedObject[ZaDomain.A2_gal_sync_accounts] = entry[ZaDomain.A2_gal_sync_accounts];
	this._containedObject[ZaDomain.A2_create_gal_acc] = "TRUE";
	this._containedObject[ZaDomain.A2_isTestingGAL] = 0;
	this._containedObject[ZaDomain.A2_isTestingSync] = 0;
	if(entry.rights)
		this._containedObject.rights = entry.rights;

	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;

	
	this.setTitle(ZaMsg.NCD_GALConfigTitle + " (" + entry.name + ")");
	this._containedObject[ZaModel.currentStep] = 1;
	this._localXForm.setInstance(this._containedObject);	
}


/**
* static change handlers for the form
**/
ZaGALConfigXWizard.onGALServerTypeChange =
function (value, event, form) {
	if(value == "ad") {
		form.getInstance().attrs[ZaDomain.A_GalLdapFilter] = "ad";
		form.getInstance().attrs[ZaDomain.A_zimbraGalAutoCompleteLdapFilter] = "adAutoComplete";
	} else {
		//form.getInstance().attrs[ZaDomain.A_GalLdapFilter] = "";
		form.getInstance().attrs[ZaDomain.A_zimbraGalAutoCompleteLdapFilter] = "(|(cn=%s*)(sn=%s*)(gn=%s*)(mail=%s*))";
	}
	this.setInstanceValue(value);	
}

ZaGALConfigXWizard.onGALSyncServerTypeChange =
function (value, event, form) {
	if(value == "ad") {
		form.getInstance().attrs[ZaDomain.A_zimbraGalSyncLdapFilter] = "ad";
	} 
	this.setInstanceValue(value);	
}

ZaGALConfigXWizard.onGALSyncChange =
function (value, event, form) {
	this.setInstanceValue(value);
	var inst = form.getInstance();
	if(value=='FALSE') {
		if(inst.attrs[ZaDomain.A_zimbraGalSyncLdapFilter] == "ad") {
			inst.attrs[ZaDomain.A_GALSyncServerType] = "ad";
		} else if(!inst.attrs[ZaDomain.A_GALSyncServerType]) {
			inst.attrs[ZaDomain.A_GALSyncServerType] = "ldap";
		}
	} 
//	form.setInstance(inst);
}

ZaGALConfigXWizard.onGalModeChange = 
function (value, event, form) {
	this.setInstanceValue(value);
	if(value != "zimbra") {
		//form.getInstance().attrs[ZaDomain.A_GalLdapFilter] = "";
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

ZaGALConfigXWizard.testSyncSettings = 
function () {
	var instance = this.getInstance();
	this.getModel().setInstanceValue(instance,ZaDomain.A2_isTestingSync,1);
	var callback = new AjxCallback(this, ZaGALConfigXWizard.checkSyncConfigCallBack);
	ZaDomain.testSyncSettings(instance, callback);	
}

ZaGALConfigXWizard.checkSyncConfigCallBack = function (arg) {
		if(!arg)
			return;
		
		var instance = this.getInstance();
		this.getModel().setInstanceValue(instance,ZaDomain.A2_isTestingSync,0);

		if(arg.isException()) {
			var msg = [arg.getException().detail,arg.getException().msg,arg.getException().trace].join("\n");
			this.getModel().setInstanceValue(instance,ZaDomain.A_GALSyncTestResultCode,arg.getException().code);
			this.getModel().setInstanceValue(instance,ZaDomain.A_GALSyncTestMessage,msg);
		} else {
			var searchResponse = arg.getResponse().Body.CheckGalConfigResponse;
			if(searchResponse) {
				this.getModel().setInstanceValue(instance,ZaDomain.A_GALSyncTestResultCode,searchResponse.code[0]._content);	
				if(searchResponse.code[0]._content != ZaDomain.Check_OK) {
					this.getModel().setInstanceValue(instance,ZaDomain.A_GALSyncTestMessage,searchResponse.message[0]._content);
				}				
			}
		}

		this.getForm().parent.goPage(ZaGALConfigXWizard.SYNC_TEST_RESULT_STEP);
}

ZaGALConfigXWizard.testGALSettings =
function () {
	var instance = this.getInstance();
	this.getModel().setInstanceValue(instance,ZaDomain.A2_isTestingGAL,1);
	var callback = new AjxCallback(this, ZaGALConfigXWizard.checkGALConfigCallBack);
	ZaDomain.testGALSettings(instance, callback, instance[ZaDomain.A_GALSampleQuery]);		
}
/**
* Callback function invoked by Asynchronous CSFE command when "check" call returns
**/
ZaGALConfigXWizard.checkGALConfigCallBack = 
function (arg) {
	if(!arg)
		return;
	
	var instance = this.getInstance();
	this.getModel().setInstanceValue(instance,ZaDomain.A2_isTestingGAL,0);

	if(arg.isException()) {
		var msg = [arg.getException().detail,arg.getException().msg,arg.getException().trace].join("\n");
		this.getModel().setInstanceValue(instance,ZaDomain.A_GALSearchTestResultCode,arg.getException().code);
		this.getModel().setInstanceValue(instance,ZaDomain.A_GALSearchTestMessage,msg);
		this.getModel().setInstanceValue(instance,ZaDomain.A_GALTestSearchResults,null);
	} else {
		var searchResponse = arg.getResponse().Body.CheckGalConfigResponse;
		if(searchResponse) {
			this.getModel().setInstanceValue(instance,ZaDomain.A_GALSearchTestResultCode,searchResponse.code[0]._content); 
			if(searchResponse.code[0]._content != ZaDomain.Check_OK) {
				this.getModel().setInstanceValue(instance,ZaDomain.A_GALSearchTestMessage,searchResponse.message[0]._content);
				this.getModel().setInstanceValue(instance,ZaDomain.A_GALTestSearchResults,null);
			} else {
				var searchResults = new Array();
				if(searchResponse.cn && searchResponse.cn.length) {
					var len = searchResponse.cn.length;
					for (var ix=0;ix<len;ix++) {
						var cnObject = new Object();
						if(searchResponse.cn[ix]._attrs) {
							for (var a in searchResponse.cn[ix]._attrs) {
								cnObject[a] = searchResponse.cn[ix]._attrs[a];
							}
							searchResults.push(cnObject);						
						}
					}
				}
				this.getModel().setInstanceValue(instance,ZaDomain.A_GALTestSearchResults,searchResults);
			}
		}
	}

	this.getForm().parent.goPage(ZaGALConfigXWizard.GAL_TEST_RESULT_STEP);
}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaGALConfigXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

ZaGALConfigXWizard.prototype.goPage =
function(pageNum) {
	ZaXWizardDialog.prototype.goPage.call(this, pageNum);
	this.changeButtonStateForStep(pageNum);
}

ZaGALConfigXWizard.prototype.goPrev =
function () {
	if(this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.GAL_TEST_RESULT_STEP) {
		this.goPage(ZaGALConfigXWizard.GAL_CONFIG_SUM_STEP);
	} else if(this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.SYNC_TEST_RESULT_STEP) {
		this.goPage(ZaGALConfigXWizard.SYNC_CONFIG_SUM_STEP);
	} else if (this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.GAL_CONFIG_SUM_STEP && this._containedObject.attrs[ZaDomain.A_GALSyncUseGALSearch]=="TRUE") {
		 this.goPage(ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_1); 
	} else if (this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.CONFIG_COMPLETE_STEP) {
		if(this._containedObject.attrs[ZaDomain.A_zimbraGalMode]==ZaDomain.GAL_Mode_internal) {
			this.goPage(ZaGALConfigXWizard.GALMODE_STEP);
		} else {
			this.goPage(ZaGALConfigXWizard.SYNC_TEST_RESULT_STEP);
		}
	} else {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this.goPage(this._containedObject[ZaModel.currentStep]-1);
	}
}

ZaGALConfigXWizard.prototype.goNext = 
function() {
	if(this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.GALMODE_STEP && this._containedObject.attrs[ZaDomain.A_zimbraGalMode]==ZaDomain.GAL_Mode_internal) {
		this.goPage(ZaGALConfigXWizard.CONFIG_COMPLETE_STEP);
	} else if (this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.SYNC_TEST_RESULT_STEP) {
		this.goPage(ZaGALConfigXWizard.CONFIG_COMPLETE_STEP);
	} else if (this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.GAL_TEST_RESULT_STEP) {
		this.goPage(ZaGALConfigXWizard.SYNC_CONFIG_SUM_STEP);
	} else if(this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.GALMODE_STEP && this._containedObject.attrs[ZaDomain.A_zimbraGalMode]!=ZaDomain.GAL_Mode_internal) {	
		//check that Filter is provided and at least one server
		if(!this._containedObject.attrs[ZaDomain.A_GalLdapFilter]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_SEARCH_FILTER_REQUIRED);			
			return;
		}
		if(!this._containedObject.attrs[ZaDomain.A_GalLdapURL] || this._containedObject.attrs[ZaDomain.A_GalLdapURL].length < 1) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_LDAP_URL_REQUIRED);					
			return;
		}
		this.goPage(ZaGALConfigXWizard.GAL_CONFIG_STEP_2);
	} else if(this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.GAL_CONFIG_STEP_2) {
		//clear the password if the checkbox is unchecked
		if(this._containedObject.attrs[ZaDomain.A_UseBindPassword]=="FALSE") {
			this._containedObject.attrs[ZaDomain.A_GalLdapBindPassword] = null;
			this._containedObject.attrs[ZaDomain.A_GalLdapBindPasswordConfirm] = null;
			this._containedObject.attrs[ZaDomain.A_GalLdapBindDn] = null;
		}
		//check that passwords match
		if(this._containedObject.attrs[ZaDomain.A_GalLdapBindPassword]!=this._containedObject.attrs[ZaDomain.A_GalLdapBindPasswordConfirm]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
			return false;
		}
		this.goPage(ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_1);
	} else if(this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_1) { 
		if(this._containedObject.attrs[ZaDomain.A_GALSyncUseGALSearch]=="FALSE") {
			this.goPage(ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_2);
		} else {
			this.goPage(ZaGALConfigXWizard.GAL_CONFIG_SUM_STEP);
		}
	} else if(this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.GAL_CONFIG_SUM_STEP) {
		this._localXForm.setInstanceValue(ZaDomain.Check_SKIPPED,ZaDomain.A_GALSearchTestResultCode);
		this.goPage(ZaGALConfigXWizard.GAL_TEST_RESULT_STEP);
	} else if(this._containedObject[ZaModel.currentStep] == ZaGALConfigXWizard.SYNC_CONFIG_SUM_STEP) {
		this._localXForm.setInstanceValue(ZaDomain.Check_SKIPPED,ZaDomain.A_GALSyncTestResultCode);
		this.goPage(ZaGALConfigXWizard.SYNC_TEST_RESULT_STEP);
	} else {
		this.goPage(this._containedObject[ZaModel.currentStep] + 1);
	}
}

ZaGALConfigXWizard.getGalSyncLdapFilterEnabled = function () {
	var val1 = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_GALSyncUseGALSearch);
	var val2 = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_GALSyncServerType);
	return (val1 == 'FALSE' && val2=='ldap');	
}

ZaGALConfigXWizard.getGalSyncConfigSeparate = function () {
	var val1 = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_GALSyncUseGALSearch);
	return (val1 == 'FALSE');	
}

ZaGALConfigXWizard.myXFormModifier = function(xFormObject, entry) {
	var resultHeaderList = new Array();
	resultHeaderList[0] = new ZaListHeaderItem("email", ZaMsg.ALV_Name_col, null, "116px", null, "email", true, true);
	resultHeaderList[1] = new ZaListHeaderItem("fullName", ZaMsg.ALV_FullName_col, null, "auto", null, "fullName", true, true);
	
	xFormObject.items = [
		{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices,valueChangeEventSources:[ZaModel.currentStep]},
		{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
		{type:_SPACER_,  align:_CENTER_, valign:_TOP_},				
		{type: _SWITCH_,width:650,
			items: [
				{type:_CASE_, caseKey:ZaGALConfigXWizard.GALMODE_STEP,numCols:2,colSizes:["220px","430px"],
					items: [
						{ref:ZaDomain.A_zimbraGalMode, type:_OSELECT1_, label:ZaMsg.Domain_GalMode, labelLocation:_LEFT_, choices:this.GALModes, onChange:ZaGALConfigXWizard.onGalModeChange},
						{ref:ZaDomain.A_zimbraGalMaxResults, type:_TEXTFIELD_, label:ZaMsg.LBL_zimbraGalMaxResults, msgName:ZaMsg.MSG_zimbraGalMaxResults, labelLocation:_LEFT_},
						{type:_DWT_ALERT_,style: DwtAlert.WARNING,
							content:ZaMsg.WARNING_DOMAIN_DS_NOT_CONFIGRED,
							visibilityChecks:[[XForm.checkInstanceValueEmty,ZaDomain.A_zimbraGalAccountId]],colSpan:"*"
						},
						{ref:ZaDomain.A2_create_gal_acc, type:_CHECKBOX_, label:ZaMsg.Domain_UseGALSyncAccts, 
							labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",
							labelCssClass:"xform_label", align:_LEFT_,labelWrap:true,
							visibilityChecks:[[XForm.checkInstanceValueEmty,ZaDomain.A2_gal_sync_accounts],[ZaItem.hasReadPermission,ZaDomain.A_zimbraGalAccountId]],
							enableDisableChecks:[[ZaItem.hasRight,ZaDomain.RIGHT_CREATE_ACCOUNT],[ZaItem.hasRight,ZaDomain.RIGHT_ADMIN_LOGIN_AS]]							
						},						
						{type:_DYNSELECT_, label:ZaMsg.Domain_GalSyncAccount,
							width:250,
						 	ref:ZaDomain.A2_new_gal_sync_account_name,
							dataFetcherClass:ZaSearch,
							dataFetcherTypes:[ZaSearch.ACCOUNTS],
							dataFetcherAttrs:[ZaItem.A_zimbraId, ZaItem.A_cn, ZaAccount.A_name, ZaAccount.A_displayname, ZaAccount.A_mail,ZaItem.A_objectClass],
							dataFetcherMethod:ZaSearch.prototype.dynSelectSearch,
							dataFetcherDomain:entry.name,
							visibilityChecks:[[XForm.checkInstanceValueEmty,ZaDomain.A2_gal_sync_accounts],[ZaItem.hasReadPermission,ZaDomain.A_zimbraGalAccountId]],
							enableDisableChangeEventSources:[ZaDomain.A2_create_gal_acc],
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"],[ZaItem.hasRight,ZaDomain.RIGHT_CREATE_ACCOUNT],[ZaItem.hasRight,ZaDomain.RIGHT_ADMIN_LOGIN_AS]]
						},
						{ref:(ZaDomain.A2_gal_sync_accounts + "[0].name"), type:_OUTPUT_,label:ZaMsg.Domain_GalSyncAccount,
							visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaDomain.A2_gal_sync_accounts],[ZaItem.hasReadPermission,ZaDomain.A_zimbraGalAccountId]]
						},
						{ref:(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_zimbra_ds + ".name"), label:ZaMsg.Domain_InternalGALDSName, type:_OUTPUT_,
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
							visibilityChecks:[
								[ZaItem.hasReadPermission,ZaDomain.A_zimbraGalAccountId],
								ZaNewDomainXWizard.isDomainModeNotExternal,
								[XForm.checkInstanceValueNotEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_zimbra_ds)]
							]							
						},
						{ref:ZaDomain.A2_new_internal_gal_ds_name, label:ZaMsg.Domain_InternalGALDSName, type:_TEXTFIELD_,
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
							visibilityChecks:[
								[ZaItem.hasReadPermission,ZaDomain.A_zimbraGalAccountId],
								ZaNewDomainXWizard.isDomainModeNotExternal,
								[XForm.checkInstanceValueEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_zimbra_ds)]
							],
							enableDisableChangeEventSources:[ZaDomain.A2_create_gal_acc],
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"],[ZaItem.hasRight,ZaDomain.RIGHT_CREATE_ACCOUNT],[ZaItem.hasRight,ZaDomain.RIGHT_ADMIN_LOGIN_AS]]							
						},
						{ref:(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_zimbra_ds + ".attrs." + ZaDataSource.A_zimbraDataSourcePollingInterval),
							type:_LIFETIME_, label:ZaMsg.LBL_zimbraDataSourcePollingInterval_internal, labelLocation:_LEFT_,
							msgName:ZaMsg.MSG_zimbraDataSourcePollingInterval_internal,
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
							visibilityChecks:[
								[ZaItem.hasReadPermission,ZaDomain.A_zimbraGalAccountId],
								ZaNewDomainXWizard.isDomainModeNotExternal,
								[XForm.checkInstanceValueNotEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_zimbra_ds)]
							]
						},
						{ref:ZaDomain.A2_new_internal_gal_polling_interval, 
							type:_LIFETIME_, label:ZaMsg.LBL_zimbraDataSourcePollingInterval_internal, labelLocation:_LEFT_,
							msgName:ZaMsg.MSG_zimbraDataSourcePollingInterval_internal,
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
							visibilityChecks:[
								[ZaItem.hasReadPermission,ZaDomain.A_zimbraGalAccountId],
								ZaNewDomainXWizard.isDomainModeNotExternal,
								[XForm.checkInstanceValueEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_zimbra_ds)]
							],
							enableDisableChangeEventSources:[ZaDomain.A2_create_gal_acc],
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"],[ZaItem.hasRight,ZaDomain.RIGHT_ADMIN_LOGIN_AS]]													
						},
						{ref:(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_ldap_ds + ".name"), label:ZaMsg.Domain_ExternalGALDSName, type:_OUTPUT_,
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
							visibilityChecks:[
								[ZaItem.hasReadPermission,ZaDomain.A_zimbraGalAccountId],
								ZaNewDomainXWizard.isDomainModeNotInternal,
								[XForm.checkInstanceValueNotEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_ldap_ds)]
							]							
						},
						{ref:ZaDomain.A2_new_external_gal_ds_name, label:ZaMsg.Domain_ExternalGALDSName, type:_TEXTFIELD_,
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
							visibilityChecks:[
								[ZaItem.hasReadPermission,ZaDomain.A_zimbraGalAccountId],
								ZaNewDomainXWizard.isDomainModeNotInternal,
								[XForm.checkInstanceValueEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_ldap_ds)]
							], 
							enableDisableChangeEventSources:[ZaDomain.A2_create_gal_acc],
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"],[ZaItem.hasRight,ZaDomain.RIGHT_ADMIN_LOGIN_AS]]							
						},
						{ref:(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_ldap_ds + ".attrs." + ZaDataSource.A_zimbraDataSourcePollingInterval), 
							type:_LIFETIME_, label:ZaMsg.LBL_zimbraDataSourcePollingInterval_external, labelLocation:_LEFT_,
							msgName:ZaMsg.MSG_zimbraDataSourcePollingInterval_external,
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
							visibilityChecks:[
								[ZaItem.hasReadPermission,ZaDomain.A_zimbraGalAccountId],
								ZaNewDomainXWizard.isDomainModeNotInternal,
								[XForm.checkInstanceValueNotEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_ldap_ds)]
							]													
						},
						{ref:ZaDomain.A2_new_external_gal_polling_interval, 
							type:_LIFETIME_, label:ZaMsg.LBL_zimbraDataSourcePollingInterval_external, labelLocation:_LEFT_,
							msgName:ZaMsg.MSG_zimbraDataSourcePollingInterval_external,
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
							visibilityChecks:[
								[ZaItem.hasReadPermission,ZaDomain.A_zimbraGalAccountId],
								ZaNewDomainXWizard.isDomainModeNotInternal,
								[XForm.checkInstanceValueEmty,(ZaDomain.A2_gal_sync_accounts + "[0]." + ZaAccount.A2_ldap_ds)]
							],
							enableDisableChangeEventSources:[ZaDomain.A2_create_gal_acc],
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"],[ZaItem.hasRight,ZaDomain.RIGHT_ADMIN_LOGIN_AS]]
						},						
						{type:_GROUP_, colSpan:2,numCols:2,colSizes:["220px","430px"],
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
							visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal],
							cssStyle:"overflow:auto",
							items: [
								{ref:ZaDomain.A_GALServerType, type:_OSELECT1_, label:ZaMsg.Domain_GALServerType, labelLocation:_LEFT_, 
									choices:this.GALServerTypes, onChange:ZaGALConfigXWizard.onGALServerTypeChange
								},
								{type:_GROUP_, numCols:6, colSpan:6,label:"   ",labelLocation:_LEFT_,
									visibilityChecks:[[ZaItem.hasWritePermission,ZaDomain.A_GalLdapURL]],
									items: [
										{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
										{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerName, width:"200px"},
										{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
										{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerPort,  width:"40px"},	
										{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALUseSSL, width:"40px"}									
									]
								},
								{ref:ZaDomain.A_GalLdapURL, type:_REPEAT_, label:ZaMsg.Domain_GalLdapURL, repeatInstance:"", showAddButton:true, showRemoveButton:true,  
									visibilityChecks:[[ZaItem.hasWritePermission,ZaDomain.A_GalLdapURL]],
									addButtonLabel:ZaMsg.Domain_AddURL, 
									removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,
									showAddOnNextRow:true,
									items: [
										{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"3269",ldapPort:"3268",  labelLocation:_NONE_,
										visibilityChecks:[],enableDisableChecks:[]}
									]
								},
								{ref:ZaDomain.A_GalLdapFilter, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, 
									enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALServerType,ZaDomain.GAL_ServerType_ldap]],
									enableDisableChangeEventSources:[ZaDomain.A_GALServerType]
									
								},
								{ref:ZaDomain.A_zimbraGalAutoCompleteLdapFilter, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_zimbraGalAutoCompleteLdapFilter, labelLocation:_LEFT_, 
									enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALServerType,ZaDomain.GAL_ServerType_ldap]],
									enableDisableChangeEventSources:[ZaDomain.A_GALServerType]
									
								},						
								{ref:ZaDomain.A_GalLdapSearchBase, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_}
							]
						}
					]
				},
				{type:_CASE_, numCols:2,colSizes:["220px","430px"],
					caseKey:ZaGALConfigXWizard.GAL_CONFIG_STEP_2,
					visibilityChangeEventSources:[ZaModel.currentStep],
					visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaNewDomainXWizard.isDomainModeNotInternal],					
					items: [
						{ref:ZaDomain.A_UseBindPassword, type:_CHECKBOX_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaDomain.A_GalLdapBindDn, type:_INPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_UseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_UseBindPassword]							
						},
						{ref:ZaDomain.A_GalLdapBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_UseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_UseBindPassword]							
						},
						{ref:ZaDomain.A_GalLdapBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPasswordConfirm, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_UseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_UseBindPassword]							
						}							
					]			
				},				
				{type:_CASE_, numCols:2,colSizes:["220px","430px"],
					caseKey:ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_1,
					items: [
						{ref:ZaDomain.A_GALSyncUseGALSearch, type:_CHECKBOX_, label:ZaMsg.Domain_GALSyncUseGALSearch, 
							labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",
							labelCssClass:"xform_label", align:_LEFT_,labelWrap:true,
							onChange:ZaGALConfigXWizard.onGALSyncChange
						},
						{ref:ZaDomain.A_GALSyncServerType, type:_OSELECT1_, label:ZaMsg.Domain_GALServerType, labelLocation:_LEFT_, 
							choices:this.GALServerTypes, onChange:ZaGALConfigXWizard.onGALSyncServerTypeChange,
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
							enableDisableChangeEventSources:[ZaDomain.A_GALSyncUseGALSearch]
						},
						{type:_GROUP_, numCols:6, colSpan:6,label:"   ",labelLocation:_LEFT_,
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
							enableDisableChangeEventSources:[ZaDomain.A_GALSyncUseGALSearch],							
							items: [
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerName, width:"200px"},
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerPort,  width:"40px"},	
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALUseSSL, width:"60px"}									
							]
						},
						{ref:ZaDomain.A_zimbraGalSyncLdapURL, type:_REPEAT_, label:ZaMsg.LBL_Domain_GalLdapURL, repeatInstance:"", showAddButton:true, showRemoveButton:true,
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
							enableDisableChangeEventSources:[ZaDomain.A_GALSyncUseGALSearch],
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSyncServerType,ZaDomain.GAL_ServerType_ad]],
							visibilityChangeEventSources:[ZaDomain.A_GALSyncUseGALSearch,ZaDomain.A_GALSyncServerType],							
							addButtonLabel:ZaMsg.Domain_AddURL, 
							removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,								
							showAddOnNextRow:true,							
							items: [
								{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"3269",ldapPort:"3268",  labelLocation:_NONE_,visibilityChecks:[],enableDisableChecks:[]}
							]
						},
						{ref:ZaDomain.A_zimbraGalSyncLdapURL, type:_REPEAT_, label:ZaMsg.LBL_Domain_GalLdapURL, repeatInstance:"", showAddButton:true, showRemoveButton:true,
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSyncServerType,ZaDomain.GAL_ServerType_ldap]],
							visibilityChangeEventSources:[ZaDomain.A_GALSyncUseGALSearch,ZaDomain.A_GALSyncServerType],							
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
							enableDisableChangeEventSources:[ZaDomain.A_GALSyncUseGALSearch,ZaDomain.A_GALSyncServerType],								
							addButtonLabel:ZaMsg.Domain_AddURL, 
							removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,								
							showAddOnNextRow:true,							
							items: [
								{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"636",ldapPort:"389",  labelLocation:_NONE_,visibilityChecks:[],enableDisableChecks:[]}
							]
						},
						{ref:ZaDomain.A_zimbraGalSyncLdapFilter, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, textWrapping:"soft", 
							enableDisableChecks:[ZaGALConfigXWizard.getGalSyncLdapFilterEnabled],
							enableDisableChangeEventSources:[ZaDomain.A_GALSyncUseGALSearch, ZaDomain.A_GALSyncServerType]
						},
						{ref:ZaDomain.A_zimbraGalSyncLdapSearchBase, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_, textWrapping:"soft", 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
							enableDisableChangeEventSources:[ZaDomain.A_GALSyncUseGALSearch]								
						}						
					]
				},
				{type:_CASE_, numCols:2,colSizes:["220px","430px"],
					caseKey:ZaGALConfigXWizard.GAL_SYNC_CONFIG_STEP_2,
					visibilityChangeEventSources:[ZaModel.currentStep],
					visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaNewDomainXWizard.isDomainModeNotInternal,[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
					items: [
						{ref:ZaDomain.A_SyncUseBindPassword, type:_CHECKBOX_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaDomain.A_zimbraGalSyncLdapBindDn, type:_INPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_SyncUseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_SyncUseBindPassword]							
						},
						{ref:ZaDomain.A_zimbraGalSyncLdapBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_SyncUseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_SyncUseBindPassword]							
						},
						{ref:ZaDomain.A_GalSyncLdapBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPasswordConfirm, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_SyncUseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_SyncUseBindPassword]							
						}							
					]			
				},				
				{type:_CASE_, caseKey:ZaGALConfigXWizard.GAL_CONFIG_SUM_STEP,numCols:2,colSizes:["220px","430px"],
					items: [
						//search
						{type:_GROUP_,
							visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal,[XForm.checkInstanceValue,ZaDomain.A2_isTestingGAL,0]],
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode,ZaDomain.A2_isTestingGAL],
							useParentTable:false,
							numCols:2,colSpan:2,
							items: [
								{ref:ZaDomain.A_zimbraGalMode, type:_OUTPUT_, label:ZaMsg.Domain_GalMode, choices:this.GALModes,
									visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_isTestingGAL,0]],
									visibilityChangeEventSources:[ZaDomain.A2_isTestingGAL]	
								},
								{ref:ZaDomain.A_zimbraGalMaxResults, type:_OUTPUT_, label:ZaMsg.LBL_zimbraGalMaxResults,
									visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_isTestingGAL,0]],
									visibilityChangeEventSources:[ZaDomain.A2_isTestingGAL]
								},							        
								{ref:ZaDomain.A_GALServerType, type:_OUTPUT_, label:ZaMsg.Domain_GALServerType, choices:this.GALServerTypes, labelLocation:_LEFT_, bmolsnr:true},
								{ref:ZaDomain.A_GalLdapURL, type:_REPEAT_, label:ZaMsg.Domain_GalLdapURL+":", labelLocation:_LEFT_,showAddButton:false, bmolsnr:true, showRemoveButton:false,
									items:[
										{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_,bmolsnr:true}
									]
								},	
								{ref:ZaDomain.A_GalLdapFilter, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_,required:true, 
									visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALServerType,ZaDomain.GAL_ServerType_ldap]],
									visibilityChangeEventSources:[ZaDomain.GAL_ServerType_ldap], bmolsnr:true									
									
								},
								{ref:ZaDomain.A_GalLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_, bmolsnr:true},
								{ref:ZaDomain.A_UseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE", bmolsnr:true},
								{ref:ZaDomain.A_GalLdapBindDn, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, 
									visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_UseBindPassword,"TRUE"]],
									visibilityChangeEventSources:[ZaDomain.A_UseBindPassword], bmolsnr:true									
								},
								{ref:ZaDomain.A_GALSampleQuery, type:_INPUT_, label:ZaMsg.Domain_GALSampleSearchName, labelLocation:_LEFT_, labelWrap:true, cssStyle:"width:100px;", bmolsnr:true},								
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, 
									enableDisableChecks:[[XForm.checkInstanceValueNot,ZaDomain.A_GALSampleQuery," "],
									                     [XForm.checkInstanceValueNotEmty,ZaDomain.A_GALSampleQuery]],
									enableDisableChangeEventSources:[ZaDomain.A_GALSampleQuery],
									onActivate:"ZaGALConfigXWizard.testGALSettings.call(this)", 
									label:ZaMsg.Domain_GALTestSettings, 
									visibilityChecks:[],					
									valign:_BOTTOM_,width:"100px"
								}
							]
						},
						{type:_DWT_ALERT_,content:ZaMsg.Domain_GALTestingInProgress,
							ref:null,
							colSpan:"2",
							iconVisible: true,
							align:_CENTER_,				
							style: DwtAlert.INFORMATION,
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_isTestingGAL,1]],
							visibilityChangeEventSources:[ZaDomain.A2_isTestingGAL]
						}						
					]
				},
				{type:_CASE_, caseKey:ZaGALConfigXWizard.GAL_TEST_RESULT_STEP,numCols:2,colSizes:["220px","430px"],
					items: [
						{type:_GROUP_, 
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSearchTestResultCode,ZaDomain.Check_OK]] ,
							visibilityChangeEventSources:[ZaDomain.A_GALSearchTestResultCode],							
							numCols:2,
							items: [
								{type:_DWT_ALERT_,content:ZaMsg.Domain_GALSearchTestSuccessful,
									ref:null,
									colSpan:"2",
									iconVisible: false,
									align:_CENTER_,				
									style: DwtAlert.INFORMATION
								},										
								{type:_OUTPUT_, value:ZaMsg.Domain_GALSearchResult,  align:_CENTER_, colSpan:2, 
									visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaDomain.A_GALTestSearchResults]]
									
								},											
								{type:_SPACER_,  align:_CENTER_, valign:_TOP_, colSpan:"*"},	
								{ref: ZaDomain.A_GALTestSearchResults, type:_DWT_LIST_, height:"140px", width:"260px",colSpan:2,
			 				    	cssClass: "DLSource", forceUpdate: true, 
			 				    	widgetClass:ZaGalObjMiniListView, headerList:resultHeaderList,
			 				    	hideHeader:true
			 				    }
							]
						},
						{type:_GROUP_, 
							visibilityChecks:[[XForm.checkInstanceValueNot,ZaDomain.A_GALSearchTestResultCode,ZaDomain.Check_OK],
							                  [XForm.checkInstanceValueNot,ZaDomain.A_GALSearchTestResultCode,ZaDomain.Check_SKIPPED]],							
							visibilityChangeEventSources:[ZaDomain.A_GALSearchTestResultCode],						
							numCols:2,					
							items: [
							   {type:_DWT_ALERT_,content:ZaMsg.Domain_GALSearchTestFailed,
									ref:null,
									colSpan:"2",
									iconVisible: true,
									align:_CENTER_,				
									style: DwtAlert.WARNING
								},							
								{type:_OUTPUT_, ref:ZaDomain.A_GALSearchTestResultCode, label:ZaMsg.Domain_GALTestResult, choices:this.TestResultChoices},
								{type:_TEXTAREA_, ref:ZaDomain.A_GALSearchTestMessage, label:ZaMsg.Domain_GALTestMessage, height:"200px", width:"380px"}
							]
						},
						{type:_DWT_ALERT_,content:ZaMsg.Domain_GALSearchTestSkipped,
							ref:null,
							colSpan:"2",
							iconVisible: true,
							align:_CENTER_,				
							style: DwtAlert.WARNING,
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSearchTestResultCode,ZaDomain.Check_SKIPPED]],
							visibilityChangeEventSources:[ZaDomain.A_GALSearchTestResultCode]									
						},
					]
				},
				{
					type:_CASE_, caseKey:ZaGALConfigXWizard.SYNC_CONFIG_SUM_STEP,
					items:[
						{type:_DWT_ALERT_,content:ZaMsg.Domain_GALSyncTestingInProgress,
							ref:null,
							colSpan:"2",
							iconVisible: true,
							align:_CENTER_,				
							style: DwtAlert.INFORMATION,
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_isTestingSync,1]],
							visibilityChangeEventSources:[ZaDomain.A2_isTestingSync]
						},
						//sync
						{type:_GROUP_, 
							useParentTable:false,
							numCols:2,colSpan:2,
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_isTestingSync,0]],
							visibilityChangeEventSources:[ZaDomain.A2_isTestingSync],							
							items: [
								{ref:ZaDomain.A_zimbraGalMode, type:_OUTPUT_, label:ZaMsg.Domain_GalMode, choices:this.GALModes,
									visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_isTestingGAL,0]],
									visibilityChangeEventSources:[ZaDomain.A2_isTestingGAL]	
								},							        
								{ref:ZaDomain.A_GALSyncUseGALSearch, type:_OUTPUT_, label:ZaMsg.Domain_GALSyncUseGALSearch, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE", bmolsnr:true},
								{ref:ZaDomain.A_GALSyncServerType, type:_OUTPUT_, label:ZaMsg.Domain_GALServerType, choices:this.GALServerTypes, labelLocation:_LEFT_,
									visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal,[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
									visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode,ZaDomain.A_GALSyncUseGALSearch], bmolsnr:true									
								},
								{ref:ZaDomain.A_zimbraGalSyncLdapURL, type:_REPEAT_, label:ZaMsg.Domain_GalLdapURL+":", labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false, bmolsnr:true,
									visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal,[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
									visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode,ZaDomain.A_GALSyncUseGALSearch],									
									items:[
										{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_,bmolsnr:true}
									]
								},	
								{ref:ZaDomain.A_zimbraGalSyncLdapFilter, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_,required:true, 
									visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal,[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"],[XForm.checkInstanceValue,ZaDomain.A_GALServerType,ZaDomain.GAL_ServerType_ldap]],
									visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode,ZaDomain.A_GALSyncUseGALSearch,ZaDomain.A_GALServerType], bmolsnr:true									
								},
								{ref:ZaDomain.A_zimbraGalSyncLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_,
									visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal,[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
									visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode,ZaDomain.A_GALSyncUseGALSearch], bmolsnr:true								
								},
								{ref:ZaDomain.A_SyncUseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",
									visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal,[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
									visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode,ZaDomain.A_GALSyncUseGALSearch], bmolsnr:true									
								},
								{ref:ZaDomain.A_zimbraGalSyncLdapBindDn, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, 
									visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal,[XForm.checkInstanceValue,ZaDomain.A_UseBindPassword,"TRUE"],[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
									visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode,ZaDomain.A_GALSyncUseGALSearch,ZaDomain.A_UseBindPassword], bmolsnr:true									
								},
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, 
									onActivate:"ZaGALConfigXWizard.testSyncSettings.call(this)", 
									label:ZaMsg.Domain_GALTestSettings, 
									enableDisableChecks:[],
									visibilityChecks:[],					
									valign:_BOTTOM_,width:"100px"
								}								
							]
						}						
					]
				},
				{
					type:_CASE_,caseKey:ZaGALConfigXWizard.SYNC_TEST_RESULT_STEP,
					items:[
						{type:_GROUP_,
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSyncTestResultCode,ZaDomain.Check_OK]],
							visibilityChangeEventSources:[ZaDomain.A_GALSyncTestResultCode],							 
							numCols:2,
							items: [
								{type:_DWT_ALERT_,content:ZaMsg.Domain_GALSyncTestSuccessful,
									ref:null,
									colSpan:"*",
									iconVisible: false,
									align:_CENTER_,				
									style: DwtAlert.INFORMATION
								}
							]
						},
						{type:_GROUP_,
							visibilityChecks:[[XForm.checkInstanceValueNot,ZaDomain.A_GALSyncTestResultCode,ZaDomain.Check_OK]],
							visibilityChangeEventSources:[ZaDomain.A_GALSyncTestResultCode],								
							numCols:2,						
							items: [
								{type:_DWT_ALERT_,content:ZaMsg.Domain_GALSyncTestFailed,
									ref:null,
									colSpan:"2",
									iconVisible: true,
									align:_CENTER_,				
									style: DwtAlert.WARNING,
									visibilityChecks:[[XForm.checkInstanceValueNot,ZaDomain.A_GALSyncTestResultCode,ZaDomain.Check_SKIPPED]],
									visibilityChangeEventSources:[ZaDomain.A_GALSyncTestResultCode]									
								},
								{type:_DWT_ALERT_,content:ZaMsg.Domain_GALSyncTestSkipped,
									ref:null,
									colSpan:"2",
									iconVisible: true,
									align:_CENTER_,				
									style: DwtAlert.WARNING,
									visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSyncTestResultCode,ZaDomain.Check_SKIPPED]],
									visibilityChangeEventSources:[ZaDomain.A_GALSyncTestResultCode]									
								},															
								{type:_OUTPUT_, ref:ZaDomain.A_GALSyncTestResultCode, label:ZaMsg.Domain_GALTestResult, choices:this.TestResultChoices,
									visibilityChecks:[[XForm.checkInstanceValueNot,ZaDomain.A_GALSyncTestResultCode,ZaDomain.Check_SKIPPED]],
									visibilityChangeEventSources:[ZaDomain.A_GALSyncTestResultCode]											
								},
								{type:_TEXTAREA_, ref:ZaDomain.A_GALSyncTestMessage, label:ZaMsg.Domain_GALTestMessage, height:"200px", width:"380px",
									visibilityChecks:[ 
										function () {
											return ((this.getInstanceValue(ZaDomain.A_GALSyncTestResultCode) != ZaDomain.Check_SKIPPED) && (this.getInstanceValue(ZaDomain.A_GALSyncTestResultCode) !=ZaDomain.Check_OK)); 	
										}
									],
									visibilityChangeEventSources:[ZaDomain.A_GALSyncTestResultCode]											
								}
							]
						}					       
					 ]
				},
				{type:_CASE_, caseKey:ZaGALConfigXWizard.CONFIG_COMPLETE_STEP,
					items: [
						{type:_OUTPUT_, value:ZaMsg.Domain_GalConfig_Complete}
					]
				}
			]	
		}	
	];
}

ZaXDialog.XFormModifiers["ZaGALConfigXWizard"].push(ZaGALConfigXWizard.myXFormModifier);
