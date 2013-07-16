/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 VMware, Inc.
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
* @class ZaNewDomainXWizard
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaNewDomainXWizard = function(parent, entry) {
	ZaXWizardDialog.call(this, parent, null, ZaMsg.NDD_Title, "700px", "350px","ZaNewDomainXWizard", null, ZaId.DLG_NEW_DOMAIN);
	
	this.TAB_INDEX = 0;
	
	ZaNewDomainXWizard.GENERAL_STEP = ++this.TAB_INDEX;
	ZaNewDomainXWizard.GALMODE_STEP = ++this.TAB_INDEX;
	ZaNewDomainXWizard.GAL_CONFIG_STEP_2 = ++this.TAB_INDEX;
	ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_1 = ++this.TAB_INDEX;
	ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_2 = ++this.TAB_INDEX;
	ZaNewDomainXWizard.GAL_CONFIG_SUM_STEP = ++this.TAB_INDEX;
	ZaNewDomainXWizard.GAL_TEST_RESULT_STEP = ++this.TAB_INDEX;
	ZaNewDomainXWizard.SYNC_CONFIG_SUM_STEP = ++this.TAB_INDEX;
	ZaNewDomainXWizard.SYNC_TEST_RESULT_STEP = ++this.TAB_INDEX;
    ZaNewDomainXWizard.AUTH_URL_STEP = ++this.TAB_INDEX;
	ZaNewDomainXWizard.AUTH_MODE_STEP = ++this.TAB_INDEX;
	ZaNewDomainXWizard.AUTH_CONFIG_STEP_2 = ++this.TAB_INDEX;
	ZaNewDomainXWizard.AUTH_CONFIG_SUM_STEP = ++this.TAB_INDEX;
	ZaNewDomainXWizard.AUTH_TEST_RESULT_STEP = ++this.TAB_INDEX;
	ZaNewDomainXWizard.VHOST_STEP = ++this.TAB_INDEX;
	ZaNewDomainXWizard.ADVANCED_STEP = ++this.TAB_INDEX;
    ZaNewDomainXWizard.FEATURE_STEP = ++this.TAB_INDEX;
	ZaNewDomainXWizard.CONFIG_COMPLETE_STEP = ++this.TAB_INDEX;
	
	this.stepChoices = [
		{label:ZaMsg.TABT_GeneralPage, value:ZaNewDomainXWizard.GENERAL_STEP},
		{label:ZaMsg.GALConfiguration, value:ZaNewDomainXWizard.GALMODE_STEP},
		{label:ZaMsg.GALDnConfiguration, value:ZaNewDomainXWizard.GAL_CONFIG_STEP_2},
		{label:ZaMsg.GALSyncConfiguration, value:ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_1},
		{label:ZaMsg.GALSyncDnConfiguration, value:ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_2},
		{label:ZaMsg.GALConfigSummary, value:ZaNewDomainXWizard.GAL_CONFIG_SUM_STEP},
		{label:ZaMsg.GalTestResult, value:ZaNewDomainXWizard.GAL_TEST_RESULT_STEP},	
		{label:ZaMsg.SyncConfigSummary, value:ZaNewDomainXWizard.SYNC_CONFIG_SUM_STEP},
		{label:ZaMsg.SyncTestResult, value:ZaNewDomainXWizard.SYNC_TEST_RESULT_STEP},
        {label:ZaMsg.DomainSSO, value:ZaNewDomainXWizard.AUTH_URL_STEP},
		{label:ZaMsg.AuthMode, value:ZaNewDomainXWizard.AUTH_MODE_STEP},										
		{label:ZaMsg.AuthSettings, value:ZaNewDomainXWizard.AUTH_CONFIG_STEP_2},								
		{label:ZaMsg.AuthSettingsSummary, value:ZaNewDomainXWizard.AUTH_CONFIG_SUM_STEP},												
		{label:ZaMsg.AuthTestResult, value:ZaNewDomainXWizard.AUTH_TEST_RESULT_STEP},
		{label:ZaMsg.Domain_Tab_VirtualHost, value:ZaNewDomainXWizard.VHOST_STEP},
		{label:ZaMsg.Domain_Tab_Advanced, value:ZaNewDomainXWizard.ADVANCED_STEP},		
        {label:ZaMsg.TABT_Feature, value:ZaNewDomainXWizard.FEATURE_STEP},
		{label:ZaMsg.DomainConfigComplete, value:ZaNewDomainXWizard.CONFIG_COMPLETE_STEP}		
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
		
	this.initForm(ZaDomain.myXModel,this.getMyXForm(entry), null);		
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaNewDomainXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaNewDomainXWizard.prototype.handleXFormChange));	
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_CHANGED, new AjxListener(this, ZaNewDomainXWizard.prototype.handleXFormChange));
	this.lastErrorStep=0;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_domains/creating_a_domain.htm?locid="+AjxEnv.DEFAULT_LOCALE;
}

ZaNewDomainXWizard.prototype = new ZaXWizardDialog;
ZaNewDomainXWizard.prototype.constructor = ZaNewDomainXWizard;
ZaXDialog.XFormModifiers["ZaNewDomainXWizard"] = new Array();

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
		if(stepNum>ZaNewDomainXWizard.GENERAL_STEP)
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	} else {
		if(stepNum == ZaNewDomainXWizard.GENERAL_STEP) {
            if (this._containedObject.attrs[ZaDomain.A_domainName])
			    this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
            else
			    this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else if (stepNum == ZaNewDomainXWizard.GALMODE_STEP) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
            if(this.checkGALAccount())
			    this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
            else
			    this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else if(stepNum == ZaNewDomainXWizard.GAL_CONFIG_SUM_STEP) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else if(stepNum == ZaNewDomainXWizard.AUTH_TEST_STEP) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if(stepNum == ZaNewDomainXWizard.CONFIG_COMPLETE_STEP) {
            this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		} else {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		}
	}

    if( this._containedObject.attrs[ZaDomain.A_domainName] &&
        this._containedObject.attrs[ZaDomain.A_domainName].length > 0 &&
        this.lastErrorStep != stepNum &&
        this.checkGALAccount()) {
        this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
    } else {
        this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
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
	this._containedObject.attrs[ZaDomain.A_zimbraDomainStatus] = ZaDomain.DOMAIN_STATUS_ACTIVE;
	
	this._containedObject[ZaDomain.A2_isTestingGAL] = entry[ZaDomain.A2_isTestingGAL] || 0;
	this._containedObject[ZaDomain.A2_isTestingSync] = entry[ZaDomain.A2_isTestingSync] || 0;
	this._containedObject[ZaDomain.A2_isTestingAuth] = entry[ZaDomain.A2_isTestingAuth] || 0;

	this._containedObject[ZaDomain.A2_create_gal_acc] = entry[ZaDomain.A2_create_gal_acc] ||"TRUE";
    this._containedObject[ZaDomain.A2_gal_sync_accounts_set] = [];
    for(var i in entry[ZaDomain.A2_gal_sync_accounts_set]){
        if (entry[ZaDomain.A2_gal_sync_accounts_set][i]) {
            this._containedObject[ZaDomain.A2_gal_sync_accounts_set][i] = {};
	        this._containedObject[ZaDomain.A2_gal_sync_accounts_set][i][ZaDomain.A2_new_gal_sync_account_name]=entry[ZaDomain.A2_gal_sync_accounts_set][i][ZaDomain.A2_new_gal_sync_account_name];
	        this._containedObject[ZaDomain.A2_gal_sync_accounts_set][i][ZaDomain.A2_new_internal_gal_ds_name]=entry[ZaDomain.A2_gal_sync_accounts_set][i][ZaDomain.A2_new_internal_gal_ds_name];
	        this._containedObject[ZaDomain.A2_gal_sync_accounts_set][i][ZaDomain.A2_new_external_gal_ds_name]=entry[ZaDomain.A2_gal_sync_accounts_set][i][ZaDomain.A2_new_external_gal_ds_name];
	        this._containedObject[ZaDomain.A2_gal_sync_accounts_set][i][ZaDomain.A2_new_internal_gal_polling_interval] = entry[ZaDomain.A2_gal_sync_accounts_set][i][ZaDomain.A2_new_internal_gal_polling_interval] || "1d";
	        this._containedObject[ZaDomain.A2_gal_sync_accounts_set][i][ZaDomain.A2_new_external_gal_polling_interval] = entry[ZaDomain.A2_gal_sync_accounts_set][i][ZaDomain.A2_new_external_gal_polling_interval] || "1d";
        }
    }

	if(entry.rights)
		this._containedObject.rights = entry.rights;

	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;

    if(entry._uuid)
        this._containedObject._uuid = entry._uuid;

	this._containedObject[ZaModel.currentStep] = entry[ZaModel.currentStep] || 1;
	this._localXForm.setInstance(this._containedObject);	
}

/**
* static change handlers for the form
**/
ZaNewDomainXWizard.onGALServerTypeChange =
function (value, event, form) {
	if(value == "ad") {
		form.getModel().setInstanceValue(form.getInstance(),ZaDomain.A_GalLdapFilter,"ad");
		form.getModel().setInstanceValue(form.getInstance(),ZaDomain.A_zimbraGalAutoCompleteLdapFilter,"adAutoComplete");		
	} else {
		form.getModel().setInstanceValue(form.getInstance(),ZaDomain.A_GalLdapFilter,"");
		form.getModel().setInstanceValue(form.getInstance(),ZaDomain.A_zimbraGalAutoCompleteLdapFilter,"(|(cn=%s*)(sn=%s*)(gn=%s*)(mail=%s*))");		
	}
	this.setInstanceValue(value);	
}



ZaNewDomainXWizard.onGalModeChange = 
function (value, event, form) {
	this.setInstanceValue(value);
	if(value != "zimbra") {
		form.getModel().setInstanceValue(form.getInstance(),ZaDomain.A_GalLdapFilter,"");
		if(AjxUtil.isEmpty(form.getModel().getInstanceValue(form.getInstance(),ZaDomain.A_GALServerType))) {
			form.getModel().setInstanceValue(form.getInstance(),ZaDomain.A_GALServerType,"ldap");
		}
		if(AjxUtil.isEmpty(form.getModel().getInstanceValue(form.getInstance(),ZaDomain.A_GalLdapSearchBase))) {
			if(!AjxUtil.isEmpty(form.getModel().getInstanceValue(form.getInstance(),ZaDomain.A_domainName))) {
				var parts = form.getModel().getInstanceValue(form.getInstance(),ZaDomain.A_domainName).split(".");
				var szSearchBase = "";
				var coma = "";
				for(var ix in parts) {
					szSearchBase += coma;
				 	szSearchBase += "dc=";
				 	szSearchBase += parts[ix];
					var coma = ",";
				}
				form.getModel().setInstanceValue(form.getInstance(),ZaDomain.A_GalLdapSearchBase,szSearchBase);
			}
		}
	}
}

ZaNewDomainXWizard.onGALSyncServerTypeChange =
function (value, event, form) {
	if(value == "ad") {
		form.getModel().setInstanceValue(form.getInstance(),ZaDomain.A_zimbraGalSyncLdapFilter,"ad");
	} 
	this.setInstanceValue(value);	
}

ZaNewDomainXWizard.onGALSyncChange =
function (value, event, form) {
	this.setInstanceValue(value);
	
	if(value=='FALSE') {
		if(form.getModel().getInstanceValue(form.getInstance(),ZaDomain.A_zimbraGalSyncLdapFilter) == "ad") {
			form.getModel().setInstanceValue(form.getInstance(),ZaDomain.A_GALSyncServerType,"ad");
		} else if(AjxUtil.isEmpty(form.getModel().getInstanceValue(form.getInstance(),ZaDomain.A_GALSyncServerType))) {
			form.getModel().setInstanceValue(form.getInstance(),ZaDomain.A_GALSyncServerType,"ldap");
		}
	} 
}

ZaNewDomainXWizard.testSyncSettings = 
function () {
	var instance = this.getInstance();
	this.getModel().setInstanceValue(instance,ZaDomain.A2_isTestingSync,1);
	var callback = new AjxCallback(this, ZaNewDomainXWizard.checkSyncConfigCallBack);
	ZaDomain.testSyncSettings(instance, callback);	
}

ZaNewDomainXWizard.checkSyncConfigCallBack = 
	function (arg) {
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

		this.getForm().parent.goPage(ZaNewDomainXWizard.SYNC_TEST_RESULT_STEP);
}

ZaNewDomainXWizard.testGALSettings =
function () {
	var instance = this.getInstance();
	this.getModel().setInstanceValue(instance,ZaDomain.A2_isTestingGAL,1);
	var callback = new AjxCallback(this, ZaNewDomainXWizard.checkGALConfigCallBack);
	ZaDomain.testGALSettings(instance, callback, instance[ZaDomain.A_GALSampleQuery]);		
}
/**
* Callback function invoked by Asynchronous CSFE command when "check" call returns
**/
ZaNewDomainXWizard.checkGALConfigCallBack = 
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

	this.getForm().parent.goPage(ZaNewDomainXWizard.GAL_TEST_RESULT_STEP);
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

ZaNewDomainXWizard.onCOSChanged = 
function(value, event, form) {
	if(ZaItem.ID_PATTERN.test(value))  {
		this.setInstanceValue(value);
	} else {
		var cos = ZaCos.getCosByName(value, form.parent._app);
		if(cos) {
			//value = form.getInstance()._defaultValues.id;
			value = cos.id;
		} 
	}
	this.setInstanceValue(value);
	return value;
}

ZaNewDomainXWizard.testAuthSettings =
function () {
	var instance = this.getInstance();
	if(instance.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ad) {
		this.getModel().setInstanceValue(instance,ZaDomain.A_AuthLdapUserDn,"%u@"+instance.attrs[ZaDomain.A_AuthADDomainName])
	}
	var callback = new AjxCallback(this, ZaNewDomainXWizard.checkAuthCallBack);
	ZaDomain.testAuthSettings(instance, callback);	
}

/**
* Callback function invoked by Asynchronous CSFE command when "check" call returns
**/
ZaNewDomainXWizard.checkAuthCallBack = 
function (arg) {

	if(!arg)
		return;
	var instance = this.getInstance();
	if(arg.isException()) {
		this.getModel().setInstanceValue(instance,ZaDomain.A_AuthTestResultCode,arg.getException().code);
		this.getModel().setInstanceValue(instance,ZaDomain.A_AuthTestMessage,arg.getException().detail+"\n"+arg.getException().msg);
	} else {
		var response = arg.getResponse().Body.CheckAuthConfigResponse;
		this.getModel().setInstanceValue(instance,ZaDomain.A_AuthTestResultCode,response.code[0]._content);
		if(instance[ZaDomain.A_AuthTestResultCode] != ZaDomain.Check_OK) {
			this.getModel().setInstanceValue(instance,ZaDomain.A_AuthTestMessage,response.message[0]._content);		
			if(response.bindDn != null) {
				this.getModel().setInstanceValue(instance,ZaDomain.A_AuthComputedBindDn,response.bindDn[0]._content);		
			} else {
				this.getModel().setInstanceValue(instance,ZaDomain.A_AuthComputedBindDn,"");
			}
		}
	}	
	this.getForm().parent.goPage(ZaNewDomainXWizard.AUTH_TEST_RESULT_STEP);
}


/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaNewDomainXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
    if (this._containedObject.attrs[ZaDomain.A_domainName])
	    this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
    else
	    this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
}

ZaNewDomainXWizard.prototype.goPrev =
function () {
	if (this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.AUTH_MODE_STEP) {
		this.goPage(ZaNewDomainXWizard.AUTH_URL_STEP);
		this.changeButtonStateForStep(ZaNewDomainXWizard.AUTH_URL_STEP);
	} else if (this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.AUTH_URL_STEP
            && this._containedObject.attrs[ZaDomain.A_zimbraGalMode]==ZaDomain.GAL_Mode_internal) {
		this.goPage(ZaNewDomainXWizard.GALMODE_STEP);
		this.changeButtonStateForStep(ZaNewDomainXWizard.GALMODE_STEP);
    } else if (this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.GAL_CONFIG_SUM_STEP) {
		if(this._containedObject.attrs[ZaDomain.A_GALSyncUseGALSearch]=="TRUE") {
			this.goPage(ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_1);
			this.changeButtonStateForStep(ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_1);
		} else {
			this.goPage(ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_2);
			this.changeButtonStateForStep(ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_2);
		}
	} else if(this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.VHOST_STEP) {
		if(this._containedObject.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_zimbra) {
			this.goPage(ZaNewDomainXWizard.AUTH_MODE_STEP); //skip all auth configuration
		} else {
			this.goPage(ZaNewDomainXWizard.AUTH_CONFIG_SUM_STEP);
		}
	} else if(this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.CONFIG_COMPLETE_STEP) {
        this.goPage(ZaNewDomainXWizard.FEATURE_STEP);
        this.changeButtonStateForStep(ZaNewDomainXWizard.FEATURE_STEP);
	} else if (this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.AUTH_CONFIG_SUM_STEP) {
		if(this._containedObject.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_zimbra) {
			this.goPage(ZaNewDomainXWizard.AUTH_MODE_STEP); //skip all auth configuration
		} else if(this._containedObject.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ad) {
			this.goPage(ZaNewDomainXWizard.AUTH_MODE_STEP);
		}  else if(this._containedObject.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ldap) {
			this.goPage(ZaNewDomainXWizard.AUTH_CONFIG_STEP_2);
		}
	} else {
		this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]-1);
		this.goPage(this._containedObject[ZaModel.currentStep]-1);
	}
}

ZaNewDomainXWizard.prototype.goNext = 
function() {
	if(this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.GALMODE_STEP) {

		if(!this.checkGALAccount()) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_CREATE_GAL_ACCOUNT);
				return;
        }

		if(this._containedObject.attrs[ZaDomain.A_zimbraGalMode]!=ZaDomain.GAL_Mode_internal) {	
			//check that Filter is provided and at least one server
			if(!this._containedObject.attrs[ZaDomain.A_GalLdapFilter]) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_SEARCH_FILTER_REQUIRED);
				return;
			}
			if(!this._containedObject.attrs[ZaDomain.A_GalLdapURL] || this._containedObject.attrs[ZaDomain.A_GalLdapURL].length < 1) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_LDAP_URL_REQUIRED);
				return;
			}
		} 
		
	}
	if (this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.GENERAL_STEP) {
		this._containedObject.attrs[ZaDomain.A_AuthADDomainName] = this._containedObject.attrs[ZaDomain.A_domainName];
		this.changeButtonStateForStep(ZaNewDomainXWizard.GALMODE_STEP);
		this.goPage(ZaNewDomainXWizard.GALMODE_STEP);		
	} else if(this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.GALMODE_STEP && this._containedObject.attrs[ZaDomain.A_zimbraGalMode]==ZaDomain.GAL_Mode_internal) {
		this.changeButtonStateForStep(ZaNewDomainXWizard.AUTH_URL_STEP);
		this.goPage(ZaNewDomainXWizard.AUTH_URL_STEP);
	} else if(this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.GAL_CONFIG_STEP_2) {
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
		this.goPage(ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_1);
		this.changeButtonStateForStep(ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_1);		
	} else if(this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_1) { 
		if(this._containedObject.attrs[ZaDomain.A_GALSyncUseGALSearch]=="FALSE") {
			this.goPage(ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_2);
			this.changeButtonStateForStep(ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_2);				
		} else {
			this.goPage(ZaNewDomainXWizard.GAL_CONFIG_SUM_STEP);
			this.changeButtonStateForStep(ZaNewDomainXWizard.GAL_CONFIG_SUM_STEP);	
		}
	} else if(this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.GAL_CONFIG_SUM_STEP) {
		this._localXForm.setInstanceValue(ZaDomain.Check_SKIPPED,ZaDomain.A_GALSearchTestResultCode);
		this.goPage(ZaNewDomainXWizard.GAL_TEST_RESULT_STEP);

	} else if(this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.SYNC_CONFIG_SUM_STEP) {
		this._localXForm.setInstanceValue(ZaDomain.Check_SKIPPED,ZaDomain.A_GALSyncTestResultCode);
		this.goPage(ZaNewDomainXWizard.SYNC_TEST_RESULT_STEP);
	} else if(this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_2) {
		//clear the password if the checkbox is unchecked
		if(this._containedObject.attrs[ZaDomain.A_SyncUseBindPassword]=="FALSE") {
			this._containedObject.attrs[ZaDomain.A_zimbraGalSyncLdapBindPassword] = null;
			this._containedObject.attrs[ZaDomain.A_GalSyncLdapBindPasswordConfirm] = null;
			this._containedObject.attrs[ZaDomain.A_zimbraGalSyncLdapBindDn] = null;
		}
		//check that passwords match
		if(this._containedObject.attrs[ZaDomain.A_zimbraGalSyncLdapBindPassword]!=this._containedObject.attrs[ZaDomain.A_GalSyncLdapBindPasswordConfirm]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
			return false;
		}
		this.goPage(ZaNewDomainXWizard.GAL_CONFIG_SUM_STEP);
		//this.changeButtonStateForStep(ZaNewDomainXWizard.GAL_CONFIG_SUM_STEP);		
	} else if (this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.AUTH_MODE_STEP) {
		if(this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_zimbra) {
			this.goPage(ZaNewDomainXWizard.VHOST_STEP);		
			this.changeButtonStateForStep(ZaNewDomainXWizard.VHOST_STEP);
		} else {
			if(!this._containedObject.attrs[ZaDomain.A_AuthLdapURL]) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_LDAP_URL_REQUIRED);
				return false;
			}			
			if(this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad) {
				var temp = this._containedObject.attrs[ZaDomain.A_AuthLdapURL].join(" ");
				if(this._containedObject.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled] == "TRUE") {
					//check that we don't have ldaps://
					if(temp.indexOf("ldaps://") > -1) {
						ZaApp.getInstance().getCurrentController().popupWarningDialog(ZaMsg.Domain_WarningStartTLSIgnored)
					}		
				}		
				this.goPage(ZaNewDomainXWizard.AUTH_CONFIG_SUM_STEP);
				this.changeButtonStateForStep(ZaNewDomainXWizard.AUTH_CONFIG_SUM_STEP);			
			} else {
				this.goPage(ZaNewDomainXWizard.AUTH_CONFIG_STEP_2);
				this.changeButtonStateForStep(ZaNewDomainXWizard.AUTH_CONFIG_STEP_2);			
			}			
		}
	}  else if (this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.AUTH_CONFIG_STEP_2) {
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
		this.goPage(ZaNewDomainXWizard.AUTH_CONFIG_SUM_STEP);
		//this.changeButtonStateForStep(ZaNewDomainXWizard.AUTH_CONFIG_SUM_STEP);
	} else if(this._containedObject[ZaModel.currentStep] == ZaNewDomainXWizard.AUTH_CONFIG_SUM_STEP) {
		this._localXForm.setInstanceValue(ZaDomain.Check_SKIPPED,ZaDomain.A_AuthTestResultCode);
		this.goPage(ZaNewDomainXWizard.AUTH_TEST_RESULT_STEP);
 		//this.testAuthSettings();
		//this.changeButtonStateForStep(ZaNewDomainXWizard.AUTH_TEST_STEP);
	} else {
		this.goPage(this._containedObject[ZaModel.currentStep] + 1);
		this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]);
	}
}

ZaNewDomainXWizard.getGalSyncLdapFilterEnabled = function () {
	var val1 = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_GALSyncUseGALSearch);
	var val2 = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_GALSyncServerType);
	return (val1 == 'FALSE' && val2=='ldap');	
}

ZaNewDomainXWizard.prototype.checkGALAccount = function () {

    var isCreated = this._containedObject[ZaDomain.A2_create_gal_acc] == "TRUE";
    if (!isCreated)
        return true;

    var GALAccountSet = this._containedObject[ZaDomain.A2_gal_sync_accounts_set];
    var isFound = false;
    var currentGALAccount;
    for (var i in GALAccountSet) {
        currentGALAccount= GALAccountSet[i];
        if (!currentGALAccount[ZaDomain.A2_new_gal_sync_account_name] || !currentGALAccount[ZaDomain.A_mailHost])
            continue;

        if((this._containedObject.attrs[ZaDomain.A_zimbraGalMode] == ZaDomain.GAL_Mode_internal ||
             this._containedObject.attrs[ZaDomain.A_zimbraGalMode] == ZaDomain.GAL_Mode_both)
             && currentGALAccount[ZaDomain.A2_new_internal_gal_ds_name]) {
            isFound = true;
            break;
        }

        if(this._containedObject.attrs[ZaDomain.A_zimbraGalMode] != ZaDomain.GAL_Mode_internal
             && currentGALAccount[ZaDomain.A2_new_external_gal_ds_name]) {
            isFound = true;
            break;
        }
    }
    return isFound;
}

ZaNewDomainXWizard.getGalSyncConfigSeparate = function () {
	var val1 = this.getModel().getInstanceValue(this.getInstance(),ZaDomain.A_GALSyncUseGALSearch);
	return (val1 == 'FALSE');	
}

ZaNewDomainXWizard.isDomainModeNotInternal = function () {
	return (this.getInstanceValue(ZaDomain.A_zimbraGalMode) !=ZaDomain.GAL_Mode_internal);
}

ZaNewDomainXWizard.isDomainModeNotExternal = function () {
	return (this.getInstanceValue(ZaDomain.A_zimbraGalMode) !=ZaDomain.GAL_Mode_external);
}

ZaNewDomainXWizard.isAuthMechNotZimbra = function () {
	return (this.getInstanceValue(ZaDomain.A_AuthMech) != ZaDomain.AuthMech_zimbra);
}

ZaNewDomainXWizard.myXFormModifier = function(xFormObject, entry) {
	var resultHeaderList = new Array();
	resultHeaderList[0] = new ZaListHeaderItem("email", ZaMsg.ALV_Name_col, null, "116px", null, "email", true, true);
	resultHeaderList[1] = new ZaListHeaderItem("fullName", ZaMsg.ALV_FullName_col, null, "auto", null, "fullName", true, true);
	var labelVisibility = {};
    labelVisibility[ZaNewDomainXWizard.GAL_CONFIG_STEP_2] = {
        checks:[ZaNewDomainXWizard.isDomainModeNotInternal],
        sources:[ZaDomain.A_zimbraGalMode]
    };
    labelVisibility[ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_1] = {
        checks:[ZaNewDomainXWizard.isDomainModeNotInternal],
        sources:[ZaDomain.A_zimbraGalMode]
    };
    labelVisibility[ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_2] = {
        checks:[[ZaNewDomainXWizard.isDomainModeNotInternal],
                [XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]
        ],
        sources:[ZaDomain.A_zimbraGalMode, ZaDomain.A_GALSyncUseGALSearch]
    };
    labelVisibility[ZaNewDomainXWizard.GAL_CONFIG_SUM_STEP] = {
        checks:[ZaNewDomainXWizard.isDomainModeNotInternal],
        sources:[ZaDomain.A_zimbraGalMode]
    };
    labelVisibility[ZaNewDomainXWizard.GAL_TEST_RESULT_STEP] = {
        checks:[ZaNewDomainXWizard.isDomainModeNotInternal],
        sources:[ZaDomain.A_zimbraGalMode]
    };
    labelVisibility[ZaNewDomainXWizard.SYNC_CONFIG_SUM_STEP] = {
        checks:[ZaNewDomainXWizard.isDomainModeNotInternal],
        sources:[ZaDomain.A_zimbraGalMode]
    };
    labelVisibility[ZaNewDomainXWizard.SYNC_TEST_RESULT_STEP] = {
        checks:[ZaNewDomainXWizard.isDomainModeNotInternal],
        sources:[ZaDomain.A_zimbraGalMode]
    };

    labelVisibility[ZaNewDomainXWizard.AUTH_CONFIG_STEP_2] = {
        checks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ldap]],
        sources:[ZaDomain.A_AuthMech]
    };
    labelVisibility[ZaNewDomainXWizard.AUTH_CONFIG_SUM_STEP] = {
        checks:[ZaNewDomainXWizard.isAuthMechNotZimbra],
        sources:[ZaDomain.A_AuthMech]
    };
    labelVisibility[ZaNewDomainXWizard.AUTH_TEST_RESULT_STEP] = {
        checks:[ZaNewDomainXWizard.isAuthMechNotZimbra],
        sources:[ZaDomain.A_AuthMech]
    };

	xFormObject.items = [
        {
            type: _OUTPUT_,
            colSpan: 2,
            valign: _TOP_,
            cssStyle:"white-space: normal",
            ref: ZaModel.currentStep,
            choices: this.stepChoices,
            valueChangeEventSources: [ZaModel.currentStep],
            labelVisibility: labelVisibility
        },
		{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
		{type:_SPACER_,  align:_CENTER_, valign:_TOP_},		
		{type: _SWITCH_,width:"100%", valign:_TOP_,
			items: [
				{type:_CASE_, caseKey:ZaNewDomainXWizard.GENERAL_STEP, colSizes:["200px","*"],numCols:2,
					items: [{type:_ZAWIZ_TOP_GROUPER_, colSpan:"*", label:ZaMsg.TABT_GeneralPage,
                        items:[
                            {ref:ZaDomain.A_domainName, type:_TEXTFIELD_, label:ZaMsg.Domain_DomainName,labelLocation:_LEFT_, required:true, width:200},
                            {ref:ZaDomain.A_zimbraPublicServiceHostname, type:_TEXTFIELD_, label:ZaMsg.Domain_zimbraPublicServiceHostname,labelLocation:_LEFT_, width:200},
                            {ref:ZaDomain.A_zimbraPublicServiceProtocol, type:_OSELECT1_, choices:ZaDomain.protocolChoices, label:ZaMsg.Domain_zimbraPublicServiceProtocol,labelLocation:_LEFT_},
                            {ref:ZaDomain.A_zimbraPublicServicePort, type:_TEXTFIELD_, label:ZaMsg.Domain_zimbraPublicServicePort,labelLocation:_LEFT_, width:100},
                            { type: _DWT_ALERT_,containerCssStyle: "padding-bottom:0px",style: DwtAlert.INFO,
                                    iconVisible: true,content: ZaMsg.Domain_InboundSMTPNote,colSpan:"*"},
                            {type:_GROUP_,colSpan:"2", colSizes:["*"],numCols:1, width:"100%", id:"dns_check_group",items:[

                                {ref: ZaDomain.A_zimbraDNSCheckHostname, type:_SUPERWIZ_TEXTFIELD_, textFieldWidth:200, colSizes:["200px", "250px", "*"],
                                    label:null,txtBoxLabel:ZaMsg.Domain_zimbraDNSCheckHostname, resetToSuperLabel:ZaMsg.NAD_ResetToGlobal}/*, {type:_CELLSPACER_}*/
                            ]},
                            {ref:ZaDomain.A_description, type:_TEXTFIELD_, label:ZaMsg.NAD_Description, labelLocation:_LEFT_, width:200},
                            {ref:ZaDomain.A_domainDefaultCOSId, type:_DYNSELECT_,
                                toolTipContent:ZaMsg.tt_StartTypingCOSName,
                                label:ZaMsg.Domain_DefaultCOS, labelLocation:_LEFT_,
                                onChange:ZaNewDomainXWizard.onCOSChanged,
                                dataFetcherMethod:ZaSearch.prototype.dynSelectSearchCoses,
                                choices:this.cosChoices,
                                dataFetcherClass:ZaSearch,
                                emptyText:ZaMsg.enterSearchTerm,
                                inputSize: 33,
                                editable:true,
                                getDisplayValue:function(newValue) {
                                    // dereference through the choices array, if provided
                                    //newValue = this.getChoiceLabel(newValue);
                                    if(ZaItem.ID_PATTERN.test(newValue)) {
                                        var cos = ZaCos.getCosById(newValue, this.getForm().parent._app);
                                        if(cos)
                                            newValue = cos.name;
                                    }
                                    if (newValue == null) {
                                        newValue = "";
                                    } else {
                                        newValue = "" + newValue;
                                    }
                                    return newValue;
                                }
                            },
                            {ref:ZaDomain.A_zimbraDomainStatus, type:_OSELECT1_, msgName:ZaMsg.NAD_DomainStatus,
                                label:ZaMsg.LBL_zimbraDomainStatus,
                                labelLocation:_LEFT_, choices:ZaDomain.domainStatusChoices
                            },
                            {ref:ZaDomain.A_notes, type:_TEXTAREA_, label:ZaMsg.NAD_Notes, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", width:250},
                            // help URL
                            {ref:ZaDomain.A_zimbraHelpAdminURL, type:_TEXTFIELD_, label:ZaMsg.Domain_zimbraHelpAdminURL,
                                msgName:ZaMsg.Domain_zimbraHelpAdminURL, labelLocation:_LEFT_, width:200},
                            {ref:ZaDomain.A_zimbraHelpDelegatedURL, type:_TEXTFIELD_, label:ZaMsg.Domain_zimbraHelpDelegatedURL,
                                msgName:ZaMsg.Domain_zimbraHelpDelegatedURL, labelLocation:_LEFT_, width:200}
                        ]}
                    ]
				},
				{type:_CASE_, caseKey:ZaNewDomainXWizard.GALMODE_STEP,numCols:2,colSizes:["220px","450px"],
					items: [
						{ref:ZaDomain.A_zimbraGalMode, type:_OSELECT1_, label:ZaMsg.Domain_GalMode, labelLocation:_LEFT_, choices:this.GALModes, onChange:ZaNewDomainXWizard.onGalModeChange},
						{ref:ZaDomain.A_zimbraGalMaxResults, type:_TEXTFIELD_, label:ZaMsg.LBL_zimbraGalMaxResults, msgName:ZaMsg.MSG_zimbraGalMaxResults, labelLocation:_LEFT_, width:100},
						{ref:ZaDomain.A2_create_gal_acc, type:_CHECKBOX_, label:ZaMsg.Domain_CreateGALSyncAccts, subLabel:"",
							labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",
							labelCssClass:"xform_label", align:_LEFT_,labelWrap:true,
							enableDisableChecks:[],visibilityChecks:[]
						},
                        {ref:ZaDomain.A2_gal_sync_accounts_set, type:_COLLAB_SELECT_, label:null, repeatInstance:"", showAddButton:true, showRemoveButton:true,
                            visibilityChangeEventSources:[ZaDomain.A2_create_gal_acc],
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"], [function() {
                                //A workaround to modify remove button visibility checking
                                //Keep at least 1 item. IZaf have only 1 item, hide the remove button
                                if (this.removeButton) {
                                    this.removeButton.visibilityChecks = [];
                                    this.removeButton.visibilityChecks.push(function() {
                                        return (this.getParentItem().getInstanceCount() > 1);
                                    });
                                }

                                //If no item exist, add 1 for better UE.
                                if (this.getInstanceValue().length == 0) {
                                    this.addRowButtonClicked(this.getParentItem().instanceNum);
                                }
                                return true;
                            }]],
							enableDisableChecks:[],
							colSpan:2,
							addButtonLabel:ZaMsg.Domain_GAL_Add,
							addButtonWidth: 220,
							number: 0,
							removeButtonLabel:ZaMsg.Domain_GAL_Remove,
							showAddOnNextRow: true,
                            showRemoveNextRow: false,
                            items:[
                                {type:_GROUP_, ref:".", numCols:1, width:"100%",
                                    visibilityChangeEventSources:[ZaDomain.A2_gal_sync_accounts_set],
                                    visibilityChecks:[function() {
                                        return ((this.instanceNum == 0) ||(this.instanceNum < this.getNumberToShow()) || (this.instanceNum < this.getInstanceCount()));
                                    }],
                                    items:[
                                    {type:_SPACER_, colSpan:"*"},
                                    {type:_GROUP_, ref:".", numCols:2, colSizes:["220px", "100%"], //use 100% to full fill the blank on the right hand side in IE
                                        width:"100%",
                                        enableDisableChecks:[],
                                        visibilityChangeEventSources:[ZaDomain.A2_gal_sync_accounts_set],
                                        visibilityChecks:[function() {
                                            var instanceNum = this.getParentItem().instanceNum;
                                            return ((instanceNum < this.getNumberToShow()) || (instanceNum < this.getInstanceCount()));
                                        }],
                                        items:[
                                        {type:_GROUP_, label:ZaMsg.Domain_GalSyncAccount, numCols:3, colSizes:["130px", "25px","auto"], colSpan:"1", ref: ".",
                                            items:[
                                                {ref:ZaDomain.A2_new_gal_sync_account_name, width:130, label:null, type:_TEXTFIELD_, visibilityChecks:[],enableDisableChecks:[]},
                                                {type:_OUTPUT_, value:"@", visibilityChecks:[],enableDisableChecks:[]},
                                                {type:_OUTPUT_,refPath:ZaDomain.A_domainName,label:null,align:_LEFT_, visibilityChecks:[],enableDisableChecks:[]}
                                            ],
                                            enableDisableChangeEventSources:[ZaDomain.A2_create_gal_acc],
                                            enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"]],
                                            required:true
                                        },
                                        {ref:ZaDomain.A_mailHost, type: _OSELECT1_, label:ZaMsg.NAD_MailServer,  choices: ZaApp.getInstance().getServerListChoices(), colSelect:true, required:true,
                                            width:300,
                                            enableDisableChangeEventSources:[ZaDomain.A2_create_gal_acc],
                                            enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"]],
                                            visibilityChecks:[]
                                        },
                                        {ref:ZaDomain.A2_new_internal_gal_ds_name, label:ZaMsg.Domain_InternalGALDSName, type:_TEXTFIELD_, required:true,
                                            visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
                                            visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotExternal],
                                            enableDisableChangeEventSources:[ZaDomain.A2_create_gal_acc],
                                            enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"]]
                                        },
                                        {ref:ZaDomain.A2_new_internal_gal_polling_interval,
                                            type:_LIFETIME_, label:ZaMsg.LBL_zimbraDataSourcePollingInterval_internal, labelLocation:_LEFT_,
                                            msgName:ZaMsg.MSG_zimbraDataSourcePollingInterval_internal,
                                            visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
                                            visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotExternal],
                                            enableDisableChangeEventSources:[ZaDomain.A2_create_gal_acc],
                                            enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"]]
                                        },
                                        {ref:ZaDomain.A2_new_external_gal_ds_name, label:ZaMsg.Domain_ExternalGALDSName, type:_TEXTFIELD_,
                                            visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
                                            visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal],
                                            enableDisableChangeEventSources:[ZaDomain.A2_create_gal_acc],
                                            enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"]]
                                        },
                                        {ref:ZaDomain.A2_new_external_gal_polling_interval,
                                            type:_LIFETIME_, label:ZaMsg.LBL_zimbraDataSourcePollingInterval_external, labelLocation:_LEFT_,
                                            msgName:ZaMsg.MSG_zimbraDataSourcePollingInterval_external,
                                            visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
                                            visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal],
                                            enableDisableChangeEventSources:[ZaDomain.A2_create_gal_acc],
                                            enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_create_gal_acc,"TRUE"]]
                                        }
                                    ]}
                            ]}
                        ]},
						{type:_GROUP_, colSpan:2,numCols:2,colSizes:["220px","430px"],
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode],
							visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal],
							cssStyle:"overflow:auto",
							items: [
								{ref:ZaDomain.A_GALServerType, type:_OSELECT1_, label:ZaMsg.Domain_GALServerType, labelLocation:_LEFT_, choices:this.GALServerTypes, onChange:ZaNewDomainXWizard.onGALServerTypeChange,
                                    enableDisableChecks:[],visibilityChecks:[]},
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
										{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"3269",ldapPort:"3268",  labelLocation:_NONE_, enableDisableChecks:[],visibilityChecks:[]}
									]
								},
								{ref:ZaDomain.A_GalLdapFilter, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, 
									enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALServerType,ZaDomain.GAL_ServerType_ldap]],
									enableDisableChangeEventSources:[ZaDomain.A_GALServerType]
									
								},
								{ref:ZaDomain.A_zimbraGalAutoCompleteLdapFilter, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_zimbraGalAutoCompleteLdapFilter, labelLocation:_LEFT_, 
									enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALServerType,ZaDomain.GAL_ServerType_ldap]],
									enableDisableChangeEventSources:[ZaDomain.A_GALServerType],bmolsnr:true
									
								},						
								{ref:ZaDomain.A_GalLdapSearchBase, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_,bmolsnr:true}
							]
						}
					]
				},

				{type:_CASE_, caseKey:ZaNewDomainXWizard.GAL_CONFIG_STEP_2, colSizes:["220px","430px"],numCols:2,
					visibilityChangeEventSources:[ZaModel.currentStep],
					visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaNewDomainXWizard.isDomainModeNotInternal],
					
					items: [
						{ref:ZaDomain.A_UseBindPassword, type:_CHECKBOX_, subLabel:"",label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_,
                            enableDisableChecks:[],visibilityChecks:[]},
						{ref:ZaDomain.A_GalLdapBindDn, type:_TEXTFIELD_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_,subLabel:"",
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_UseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_UseBindPassword]
							
						},
						{ref:ZaDomain.A_GalLdapBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_UseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_UseBindPassword]
							
						},
						{ref:ZaDomain.A_GalLdapBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPasswordConfirm, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_UseBindPassword,"TRUE"], [ZaItem.hasWritePermission, ZaDomain.A_GalLdapBindPassword]],
							enableDisableChangeEventSources:[ZaDomain.A_UseBindPassword],
                            visibilityChecks:[[ZaItem.hasReadPermission, ZaDomain.A_GalLdapBindPassword]]
						}														
					]			
				}, 
				{type:_CASE_, caseKey:ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_1,numCols:2,colSizes:["220px","430px"],
					items: [
						{ref:ZaDomain.A_GALSyncUseGALSearch, type:_CHECKBOX_, label:ZaMsg.Domain_GALSyncUseGALSearch, subLabel:"",
							labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",
							labelCssClass:"xform_label", align:_LEFT_,labelWrap:true,
							onChange:ZaNewDomainXWizard.onGALSyncChange,
                            enableDisableChecks:[],
                            visibilityChecks:[]
						},
						{ref:ZaDomain.A_GALSyncServerType, type:_OSELECT1_, label:ZaMsg.Domain_GALServerType, labelLocation:_LEFT_, 
							choices:this.GALServerTypes, onChange:ZaNewDomainXWizard.onGALSyncServerTypeChange,
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
							enableDisableChangeEventSources:[ZaDomain.A_GALSyncUseGALSearch],
                            enableDisableChecks:[],
                            visibilityChecks:[]
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
								{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"3269",ldapPort:"3268",  labelLocation:_NONE_, enableDisableChecks:[],visibilityChecks:[]}
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
								{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"636",ldapPort:"389",  labelLocation:_NONE_, enableDisableChecks:[],visibilityChecks:[]}
							]
						},
						{ref:ZaDomain.A_zimbraGalSyncLdapFilter, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, textWrapping:"soft", 
							enableDisableChecks:[ZaNewDomainXWizard.getGalSyncLdapFilterEnabled],
							enableDisableChangeEventSources:[ZaDomain.A_GALSyncUseGALSearch,ZaDomain.A_GALSyncServerType]
						},
						{ref:ZaDomain.A_zimbraGalSyncLdapSearchBase, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_, textWrapping:"soft", 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
							enableDisableChangeEventSources:[ZaDomain.A_GALSyncUseGALSearch]
						}						
					]
				},
				{type:_CASE_,numCols:2,colSizes:["220px","430px"], caseKey:ZaNewDomainXWizard.GAL_SYNC_CONFIG_STEP_2,
					visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaNewDomainXWizard.isDomainModeNotInternal,[XForm.checkInstanceValue,ZaDomain.A_GALSyncUseGALSearch,"FALSE"]],
					visibilityChangeEventSources:[ZaModel.currentStep],
					
					items: [
						{ref:ZaDomain.A_SyncUseBindPassword, type:_CHECKBOX_, subLabel:"", label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_,
                            enableDisableChecks:[],
                            visibilityChecks:[]
                        },
						{ref:ZaDomain.A_zimbraGalSyncLdapBindDn, type:_TEXTFIELD_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_SyncUseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_SyncUseBindPassword]
					
						},
						{ref:ZaDomain.A_zimbraGalSyncLdapBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_SyncUseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_SyncUseBindPassword]
					
						},
						{ref:ZaDomain.A_GalSyncLdapBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPasswordConfirm, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_SyncUseBindPassword,"TRUE"], [ZaItem.hasReadPermission, ZaDomain.A_zimbraGalSyncLdapBindPassword]],
							enableDisableChangeEventSources:[ZaDomain.A_SyncUseBindPassword],
                            visibilityChecks:[[ZaItem.hasReadPermission, ZaDomain.A_zimbraGalSyncLdapBindPassword]]

						}							
					]			
				},					
				{type:_CASE_, caseKey:ZaNewDomainXWizard.GAL_CONFIG_SUM_STEP,numCols:2,colSizes:["220px","430px"],
					items: [
						//search
						{type:_GROUP_,
							visibilityChecks:[ZaNewDomainXWizard.isDomainModeNotInternal,[XForm.checkInstanceValue,ZaDomain.A2_isTestingGAL,0]],
							visibilityChangeEventSources:[ZaDomain.A_zimbraGalMode,ZaDomain.A2_isTestingGAL],
							useParentTable:false,
							numCols:2,colSpan:2, width:"100%", colSizes:["220px","430px"],
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
								{ref:ZaDomain.A_GALSampleQuery, type:_INPUT_, label:ZaMsg.Domain_GALSampleSearchName, labelLocation:_LEFT_, labelWrap:true, cssStyle:"width:100px;", bmolsnr:true,
                                    visibilityChecks:[],enableDisableChecks:[]
                                },
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, 
									enableDisableChecks:[[XForm.checkInstanceValueNot,ZaDomain.A_GALSampleQuery," "],
									                     [XForm.checkInstanceValueNotEmty,ZaDomain.A_GALSampleQuery]],
									enableDisableChangeEventSources:[ZaDomain.A_GALSampleQuery],
									onActivate:"ZaNewDomainXWizard.testGALSettings.call(this)", 
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
				{type:_CASE_, caseKey:ZaNewDomainXWizard.GAL_TEST_RESULT_STEP,numCols:2,colSizes:["220px","430px"],
					items: [
						{type:_GROUP_, 
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_GALSearchTestResultCode,ZaDomain.Check_OK]] ,
							visibilityChangeEventSources:[ZaDomain.A_GALSearchTestResultCode],							
							numCols:2,  width: "100%",
                            colSpan:"2",
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
							numCols:2,  width: "100%",
                            colSpan:"2",
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
						}
					]
				},
				{
					type:_CASE_, caseKey:ZaNewDomainXWizard.SYNC_CONFIG_SUM_STEP,
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
									onActivate:"ZaNewDomainXWizard.testSyncSettings.call(this)", 
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
					type:_CASE_,caseKey:ZaNewDomainXWizard.SYNC_TEST_RESULT_STEP,
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
				{type:_CASE_, caseKey:ZaNewDomainXWizard.AUTH_URL_STEP,numCols:2,colSizes:["220px","430px"],
					items: [
                            { type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.Domain_URLSetting, colSpan:"*",
                                items :[
                                {ref: ZaDomain.A_zimbraAdminConsoleLoginURL, type:_TEXTFIELD_,
                                label:ZaMsg.Domain_zimbraAdminConsoleLoginURL, width:250
                                },
                                { ref: ZaDomain.A_zimbraAdminConsoleLogoutURL, type:_TEXTFIELD_,
                                label:ZaMsg.Domain_zimbraAdminConsoleLogoutURL  , width:250
                                }
                            ]}, /*
                            { type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_Kerberos_Configure, colSpan:"*",
                                items :[
                                {ref: ZaDomain.A_zimbraAuthKerberos5Realm, type:_TEXTFIELD_,
                                label:ZaMsg.LBL_zimbraAuthKerberos5Realm, width:250
                                }
                            ]},   */
                            { type:_ZAWIZ_TOP_GROUPER_, label: ZaMsg.NAD_AUTH_ClientConfigure, colSpan:"*",
                                  items:[
                                      { ref: ZaDomain.A_zimbraMailSSLClientCertPrincipalMap, labelCssStyle:"vertical-align:top", type:_TEXTAREA_,
                                        label:ZaMsg.NAD_zimbraMailSSLClientCertPrincipalMap, width:250
                                      },
                                      {type: _DWT_ALERT_, cssClass: "DwtTabTable", containerCssStyle: "padding-bottom:0px",
                                          style: DwtAlert.WARNING, iconVisible: false, content: ZaMsg.Alert_Ngnix,
                                          id:"xform_header_ngnix"
                                      },
                                      {type: _SPACER_, height: 10 },
                                      {ref:ZaDomain.A_zimbraReverseProxyClientCertMode, type:_SUPER_SELECT1_,
                                        colSizes:["250px","*"],
                                        label:ZaMsg.NAD_zimbraReverseProxyClientCertMode,
                                        labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS
                                      },
                                      {ref: ZaDomain.A_zimbraReverseProxyClientCertCA, type:_TEXTAREA_,
                                        label:ZaMsg.NAD_zimbraReverseProxyClientCertCA, width: 400
                                      }
                                  ]
                            },
                            { type:_ZAWIZ_TOP_GROUPER_, label: ZaMsg.NAD_WEBCLIENT_Configure, colSpan:"*",
                                  items:[
                                      { ref: ZaDomain.A_zimbraWebClientLoginURL,useParentTable: false,
                                        colSpan: 2,
                                        type:_SUPERWIZ_TEXTFIELD_, textFieldWidth: "250px",
                                        resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                                        msgName: ZaMsg.LBL_zimbraWebClientLoginURL,
                                        txtBoxLabel: ZaMsg.LBL_zimbraWebClientLoginURL
                                      },
                                      { ref: ZaDomain.A_zimbraWebClientLogoutURL,useParentTable: false,
                                        colSpan: 2,
                                        type:_SUPERWIZ_TEXTFIELD_, textFieldWidth: "250px",
                                        resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                                        msgName: ZaMsg.LBL_zimbraWebClientLogoutURL,
                                        txtBoxLabel: ZaMsg.LBL_zimbraWebClientLogoutURL
                                      },
                                      { ref: ZaDomain.A_zimbraWebClientLoginURLAllowedUA,
                                        label:ZaMsg.LBL_zimbraWebClientLoginURLAllowedUA,
                                        type:_SUPER_REPEAT_,
                                        resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                                        repeatInstance:"",
                                        colSizes:["250px", "150px"],
                                        addButtonLabel:ZaMsg.NAD_Add ,
                                        removeButtonLabel: ZaMsg.NAD_Remove,
                                        showAddButton:true,
                                        showRemoveButton:true,
                                        showAddOnNextRow:true,
                                        repeatItems: [
                                            {ref:".", type:_TEXTFIELD_,
                                            width: "150px"}
                                        ]
                                      },
                                      { ref: ZaDomain.A_zimbraWebClientLogoutURLAllowedUA,
                                        label:ZaMsg.LBL_zimbraWebClientLogoutURLAllowedUA,
                                        type:_SUPER_REPEAT_,
                                        resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                                        repeatInstance:"",
                                        colSizes:["250px", "150px"],
                                        addButtonLabel:ZaMsg.NAD_Add ,
                                        removeButtonLabel: ZaMsg.NAD_Remove,
                                        showAddButton:true,
                                        showRemoveButton:true,
                                        showAddOnNextRow:true,
                                        repeatItems: [
                                            {ref:".", type:_TEXTFIELD_,
                                            width: "150px"}
                                        ]
                                      },
                                      { ref: ZaDomain.A_zimbraWebClientLoginURLAllowedIP,
                                        label:ZaMsg.LBL_zimbraWebClientLoginURLAllowedIP,
                                        type:_SUPER_REPEAT_,
                                        resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                                        repeatInstance:"",
                                        colSizes:["250px", "150px"],
                                        addButtonLabel:ZaMsg.NAD_Add ,
                                        removeButtonLabel: ZaMsg.NAD_Remove,
                                        showAddButton:true,
                                        showRemoveButton:true,
                                        showAddOnNextRow:true,
                                        repeatItems: [
                                           {ref:".", type:_TEXTFIELD_,
                                            width: "150px"}
                                        ]
                                      },
                                      { ref: ZaDomain.A_zimbraWebClientLogoutURLAllowedIP,
                                        label:ZaMsg.LBL_zimbraWebClientLogoutURLAllowedIP,
                                        type:_SUPER_REPEAT_,
                                        resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                                        repeatInstance:"",
                                        colSizes:["250px", "150px"],
                                        addButtonLabel:ZaMsg.NAD_Add ,
                                        removeButtonLabel: ZaMsg.NAD_Remove,
                                        showAddButton:true,
                                        showRemoveButton:true,
                                        showAddOnNextRow:true,
                                        repeatItems: [
                                           {ref:".", type:_TEXTFIELD_,
                                            width: "150px"}
                                        ]
                                      },
                                      {
                                          ref: ZaDomain.A_zimbraForceClearCookies,
                                          useParentTable: false,
                                          colSpan: 2,
                                          type: _CHECKBOX_,
                                          trueValue: "TRUE",
                                          falseValue: "FALSE",
                                          resetToSuperLabel: ZaMsg.NAD_ResetToGlobal,
                                          msgName: ZaMsg.MSG_zimbraForceClearCookies,
                                          label: ZaMsg.LBL_zimbraForceClearCookies,
                                          labelLocation: _LEFT_
                                      }
                                  ]
                            }
					]
				},
				{type:_CASE_, caseKey:ZaNewDomainXWizard.AUTH_MODE_STEP, numCols:2,colSizes:["220px","450px"],					
					items:[
						{type:_OSELECT1_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs, ref:ZaDomain.A_AuthMech, onChange:ZaNewDomainXWizard.onAuthMechChange},
						{type:_GROUP_, numCols:2,colSizes:["220px","auto"],colSpan:2,
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ad]],
							visibilityChangeEventSources:[ZaDomain.A_AuthMech],
							items:[
								{ref:ZaDomain.A_AuthADDomainName, type:_TEXTFIELD_, label:ZaMsg.Domain_AuthADDomainName, labelLocation:_LEFT_,
                                    visibilityChecks:[],enableDisableChecks:[]
                                },
								{type:_GROUP_, numCols:6, /*colSpan:2,*/label:"   ",labelLocation:_LEFT_,
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
										{ref:".", type:_LDAPURL_, label:null, labelLocation:_NONE_,ldapSSLPort:"3269",ldapPort:"3268",
                                            visibilityChecks:[],enableDisableChecks:[]}
									]
								}									
							]
						},
						{type:_GROUP_, numCols:2,colSizes:["220px","auto"],colSpan:2,
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ldap]],
							visibilityChangeEventSources:[ZaDomain.A_AuthMech],
							items:[
								{type:_GROUP_, numCols:6, /*colSpan:2,*/label:"   ",labelLocation:_LEFT_,
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
										{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"636",ldapPort:"389", labelLocation:_NONE_,
                                            visibilityChecks:[],enableDisableChecks:[]
                                        }
									]
								},
								{ref:ZaDomain.A_zimbraAuthLdapStartTlsEnabled, type:_CHECKBOX_, label:ZaMsg.Domain_AuthLdapStartTlsEnabled, trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaDomain.A_AuthLdapSearchFilter, type:_TEXTAREA_, width:"30em", height:100, label:ZaMsg.Domain_AuthLdapFilter, labelLocation:_LEFT_, textWrapping:"soft"},
								{ref:ZaDomain.A_AuthLdapSearchBase, type:_TEXTAREA_,width:"30em", height:50, label:ZaMsg.Domain_AuthLdapSearchBase, labelLocation:_LEFT_, textWrapping:"soft"},
								{type:_OUTPUT_, value:ZaMsg.NAD_DomainsAuthStr, colSpan:2}
							]
						}
					]
				},
				{type:_CASE_, numCols:2,colSizes:["220px","auto"],caseKey:ZaNewDomainXWizard.AUTH_CONFIG_STEP_2,
					visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ldap]],
					items: [
						{ref:ZaDomain.A_AuthUseBindPassword, type:_CHECKBOX_, subLabel:"", label:ZaMsg.Domain_AuthUseBindPassword, labelLocation:_LEFT_,
                            trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_,
                            visibilityChecks:[],enableDisableChecks:[]
                        },
						{ref:ZaDomain.A_AuthLdapSearchBindDn, type:_TEXTFIELD_, label:ZaMsg.Domain_AuthLdapBindDn, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthUseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_AuthUseBindPassword]

						},
						{ref:ZaDomain.A_AuthLdapSearchBindPassword, type:_SECRET_, label:ZaMsg.Domain_AuthLdapBindPassword, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthUseBindPassword,"TRUE"]],
							enableDisableChangeEventSources:[ZaDomain.A_AuthUseBindPassword]

						},
						{ref:ZaDomain.A_AuthLdapSearchBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_AuthLdapBindPasswordConfirm, labelLocation:_LEFT_, 
							enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthUseBindPassword,"TRUE"],
                                                 [ZaItem.hasWritePermission, ZaDomain.A_AuthLdapSearchBindPassword]],
							enableDisableChangeEventSources:[ZaDomain.A_AuthUseBindPassword],
                            visibilityChecks:[[ZaItem.hasReadPermission, ZaDomain.A_AuthLdapSearchBindPassword]]
						}							
					]						
				},
				{type:_CASE_,  caseKey:ZaNewDomainXWizard.AUTH_CONFIG_SUM_STEP,
					visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaNewDomainXWizard.isAuthMechNotZimbra],
					items: [
						{type:_DWT_ALERT_,content:ZaMsg.Domain_AuthTestingInProgress,
							ref:null,
							colSpan:"2",
							iconVisible: true,
							align:_CENTER_,				
							style: DwtAlert.INFORMATION,
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_isTestingAuth,1]],
							visibilityChangeEventSources:[ZaDomain.A2_isTestingGAL]
						},
						{type:_GROUP_,numCols:2,colSizes:["220px","430px"],
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_isTestingAuth,0]],
							visibilityChangeEventSources:[ZaDomain.A2_isTestingAuth],	
							items:[
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
									{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
										items:[
											{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
										]
									},					
									{ref:ZaDomain.A_zimbraAuthLdapStartTlsEnabled, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapStartTlsEnabled, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES},						
									{ref:ZaDomain.A_AuthLdapSearchFilter, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapFilter, labelLocation:_LEFT_},
									{ref:ZaDomain.A_AuthLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapSearchBase, labelLocation:_LEFT_},
									{ref:ZaDomain.A_AuthUseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_AuthUseBindPassword, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES},											
									{ref:ZaDomain.A_AuthLdapSearchBindDn, type:_TEXTFIELD_, label:ZaMsg.Domain_AuthLdapBindDn, labelLocation:_LEFT_, 
										visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthUseBindPassword,"TRUE"]],
										visibilityChangeEventSources:[ZaDomain.A_AuthUseBindPassword]									
									}											
								]
							},
							{type:_SPACER_, height:10},
							{type:_OUTPUT_,value:ZaMsg.Domain_AuthProvideLoginPwd, align:_CENTER_, colSpan:"*"},
							{type:_TEXTFIELD_, label:ZaMsg.LBL_Domain_AuthTestUserName, ref:ZaDomain.A_AuthTestUserName, alignment:_LEFT_,
                                visibilityChecks:[],enableDisableChecks:[]
                            },
							{type:_SECRET_, label:ZaMsg.LBL_Domain_AuthTestPassword, ref:ZaDomain.A_AuthTestPassword, alignment:_LEFT_,
                                visibilityChecks:[],enableDisableChecks:[]
                            },
							{type:_CELLSPACER_},
							{type:_DWT_BUTTON_, 
								onActivate:"ZaNewDomainXWizard.testAuthSettings.call(this)", 
								label:ZaMsg.Domain_GALTestSettings, 
								enableDisableChecks:[],
								visibilityChecks:[],					
								valign:_BOTTOM_,width:"100px"
							}
						]}
					]
				},

				{type:_CASE_, numCols:1, caseKey: ZaNewDomainXWizard.AUTH_TEST_RESULT_STEP,colSizes:["380px","290px"],
					visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaNewDomainXWizard.isAuthMechNotZimbra],
					visibilityChangeEventSources:[ZaModel.currentStep],
					items: [
						{type:_DWT_ALERT_,
							colSpan:"2",
							iconVisible: false,
							align:_CENTER_,				
							style: DwtAlert.INFORMATION,
        						visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthTestResultCode,ZaDomain.Check_OK]],
							visibilityChangeEventSources:[ZaDomain.A_AuthTestResultCode],
							content:ZaMsg.Domain_AuthTestSuccessful, alignment:_CENTER_
						},
				    	{type:_DWT_ALERT_,content:ZaMsg.Domain_AuthTestSkipped,
							ref:null,
							colSpan:"2",
							iconVisible: true,
							align:_CENTER_,				
							style: DwtAlert.WARNING,
							visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthTestResultCode,ZaDomain.Check_SKIPPED]],
							visibilityChangeEventSources:[ZaDomain.A_AuthTestResultCode]									
				    	},
						{type:_GROUP_, 
							visibilityChangeEventSources:[ZaDomain.A_AuthTestResultCode],
							visibilityChecks:[[XForm.checkInstanceValueNot,ZaDomain.A_AuthTestResultCode,ZaDomain.Check_OK],
							                  [XForm.checkInstanceValueNot,ZaDomain.A_AuthTestResultCode,ZaDomain.Check_SKIPPED]
							                 ],
							items: [
								{type:_OUTPUT_, value:ZaMsg.Domain_AuthTestFailed, alignment:_CENTER_, colSpan:2, label:null},
								{type:_OUTPUT_, ref:ZaDomain.A_AuthTestResultCode, label:ZaMsg.Domain_AuthTestResultCode, choices:this.TestResultChoices, alignment:_LEFT_},
								{type:_OUTPUT_, ref:ZaDomain.A_AuthComputedBindDn, label:ZaMsg.Domain_AuthComputedBindDn, alignment:_LEFT_, 
									visibilityChangeEventSources:[ZaDomain.A_AuthMech],
									visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ad]]

								},
								{type:_TEXTAREA_, ref:ZaDomain.A_AuthTestMessage, label:ZaMsg.Domain_AuthTestMessage, height:150, alignment:_LEFT_, width:"320px"}
							]
						}
					
					]
				},
				{type:_CASE_, caseKey:ZaNewDomainXWizard.VHOST_STEP,
					items:[
						{type:_DWT_ALERT_,content:null,ref:ZaDomain.A_domainName,
							getDisplayValue: function (itemVal) {
								return AjxMessageFormat.format(ZaMsg.Domain_VH_Explanation,itemVal);
							},
							iconVisible: false,
							align:_CENTER_,				
							style: DwtAlert.INFORMATION
						},
						{ref:ZaDomain.A_zimbraVirtualHostname, type:_REPEAT_, label:null, repeatInstance:"", showAddButton:true, showRemoveButton:true, 
								addButtonLabel:ZaMsg.NAD_AddVirtualHost, 
								showAddOnNextRow:true,
								removeButtonLabel:ZaMsg.NAD_RemoveVirtualHost,
                                cssStyle:"text-align:center",
                                tableCssStyle:"margin-left:auto;margin-right:auto",
								items: [
									{ref:".", type:_TEXTFIELD_, label:null,width:220, visibilityChecks:[],enableDisableChecks:[]}
								]
						}
					]
				},
				{type:_CASE_, caseKey:ZaNewDomainXWizard.FEATURE_STEP,
					items: [
						{ type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_zimbraCalendarFeature,
                                  		  items :[
                                                  {ref:ZaDomain.A_zimbraFeatureCalendarReminderDeviceEmailEnabled,
                                                      type:_CHECKBOX_,
                                                      msgName:ZaMsg.LBL_zimbraFeatureCalendarReminderDeviceEmailEnabled,
                                                      label:ZaMsg.LBL_zimbraFeatureCalendarReminderDeviceEmailEnabled,
                                                      trueValue:"TRUE", falseValue:"FALSE"
                                                  }
                                         	 ]
                                		}

					]
				},
				{type:_CASE_, caseKey:ZaNewDomainXWizard.ADVANCED_STEP, numCols:1, 
					items: [
						{ type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.Domain_BC_ShareConf,
                            items :[
                                    { ref: ZaDomain.A_zimbraBasicAuthRealm,
                                          type: _SUPERWIZ_TEXTFIELD_, width: 250 ,
                                          resetToSuperLabel:ZaMsg.NAD_ResetToGlobal,
                                          txtBoxLabel: ZaMsg.Domain_zimbraBasicAuthRealm
                                    }
                                 ]
                            },
                        { type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.Domain_AD_EmailValidate,
							colSizes:["200px","*"], 
                            items :[
                                    {ref:ZaDomain.A_zimbraMailAddressValidationRegex, type:_REPEAT_,
                                        nowrap:false,labelWrap:true,
                                        label:ZaMsg.LBL_EmailValidate, repeatInstance:"", labelWrap:true,
                                        showAddButton:true, showRemoveButton:true,
                                        addButtonLabel:ZaMsg.NAD_AddRegex,
                                        showAddOnNextRow:true,
                                        removeButtonLabel:ZaMsg.NAD_RemoveRegex,
                                        items: [
                                            {ref:".", type:_TEXTFIELD_, label:null,width:250}
                                        ]
                                    }
						   ]
                        },
                        {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.Domain_QUOTA_Configuration,
                            colSizes:["200px","*"],
                            items:[
                                {ref:ZaDomain.A_zimbraMailDomainQuota, type:_TEXTFIELD_,
                                    label:ZaMsg.LBL_DomainQuota
                                },
                                {ref:ZaDomain.A_zimbraDomainAggregateQuota, type:_TEXTFIELD_,
                                    label:ZaMsg.LBL_DomainAggregateQuota
                                },
                                {ref:ZaDomain.A_zimbraDomainAggregateQuotaWarnPercent, type:_TEXTFIELD_,
                                    label:ZaMsg.LBL_DomainAggregateQuotaWarnPercent
                                },
                                {ref:ZaDomain.A_zimbraDomainAggregateQuotaWarnEmailRecipient, type:_TEXTFIELD_,
                                    label:ZaMsg.LBL_DomainAggregateQuotaWarnEmailRecipient
                                },
                                {ref:ZaDomain.A_zimbraDomainAggregateQuotaPolicy, type:_OSELECT1_,
                                   label:ZaMsg.LBL_DomainAggregateQuotaPolicy, labelLocation:_LEFT_
                                }
                            ]
                        }
									
					]
				},
				{type:_CASE_, caseKey:ZaNewDomainXWizard.CONFIG_COMPLETE_STEP,
					items: [
						{type:_OUTPUT_, value:ZaMsg.Domain_Config_Complete,cssStyle:"text-align:center"}
					]
				}										
			]	
		}
	];
}
ZaXDialog.XFormModifiers["ZaNewDomainXWizard"].push(ZaNewDomainXWizard.myXFormModifier);
