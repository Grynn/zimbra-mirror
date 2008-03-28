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
* @class ZaGALConfigXWizard
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaGALConfigXWizard = function(parent, app) {
	this._app=app;
	ZaXWizardDialog.call(this, parent,app, null, ZaMsg.NCD_GALConfigTitle, "550px", "300px","ZaGALConfigXWizard");
	this.stepChoices = [
		{label:ZaMsg.TABT_GALMode, value:1},
		{label:ZaMsg.TABT_GALConfiguration, value:2}, 
		{label:ZaMsg.TABT_GALConfiguration, value:3},		
		{label:ZaMsg.TABT_GALSyncConfiguration, value:4},
		{label:ZaMsg.TABT_GALSyncConfiguration, value:5},
		{label:ZaMsg.TABT_GALConfigSummary, value:6},		
		{label:ZaMsg.TABT_TestGalConfig, value:7},
		{label:ZaMsg.TABT_GalTestResult, value:8}	
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
	this.initForm(ZaDomain.myXModel,this.getMyXForm());		
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaGALConfigXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaGALConfigXWizard.prototype.handleXFormChange));	
	this.lastErrorStep=0;
	this._helpURL = location.pathname + "adminhelp/html/WebHelp/managing_domains/using_the_global_address_list_(gal).htm"
	
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
		if(stepNum == 1) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if (stepNum == 2) {
			//if internal - enable finish and disable next, its the last step
			if(this._containedObject.attrs[ZaDomain.A_GalMode]==ZaDomain.GAL_Mode_internal) {
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
				this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
			} else {			//else - enable next and disable finish
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
				this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			}
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		} else if(stepNum == 6) {
			//change next button to "test"
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_GALTestSettings);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		} else if(stepNum == 7) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if (stepNum == 8) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else if (stepNum == 5 || stepNum == 4) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
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
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();

	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
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

ZaGALConfigXWizard.prototype.testSetings =
function () {
	var callback = new AjxCallback(this, this.checkCallBack);
	ZaDomain.testGALSettings(this._containedObject, callback, this._containedObject[ZaDomain.A_GALSampleQuery]);	
}
/**
* Callback function invoked by Asynchronous CSFE command when "check" call returns
**/
ZaGALConfigXWizard.prototype.checkCallBack = 
function (arg) {
	if(!arg)
		return;
	if(arg.isException()) {
		var msg = [arg.getException().detail,arg.getException().msg,arg.getException().trace].join("\n");
		this._containedObject[ZaDomain.A_GALSearchTestResultCode] = arg.getException().code;
		this._containedObject[ZaDomain.A_GALSearchTestMessage] = msg;
		this._containedObject[ZaDomain.A_GALSearchTestSearchResults] = null;		

		this._containedObject[ZaDomain.A_GALSyncTestResultCode] = arg.getException().code;
		this._containedObject[ZaDomain.A_GALSyncTestMessage] = msg;
		this._containedObject[ZaDomain.A_GALSyncTestSearchResults] = null;	
	} else {
		var batchResp = arg.getResponse().Body.BatchResponse;
		if(batchResp) {
			var searchResponse;
			if(batchResp.CheckGalConfigResponse) {
				searchResponse = batchResp.CheckGalConfigResponse[0];
				this._containedObject[ZaDomain.A_GALSearchTestResultCode] = searchResponse.code[0]._content;	
				if(this._containedObject[ZaDomain.A_GALSearchTestResultCode] != ZaDomain.Check_OK) {
					this._containedObject[ZaDomain.A_GALSearchTestMessage] = searchResponse.message[0]._content;		
					this._containedObject[ZaDomain.A_GALSearchTestSearchResults] = null;			
				} else {
					this._containedObject[ZaDomain.A_GALTestSearchResults] = new Array();
					if(searchResponse.cn && searchResponse.cn.length) {
						var len = searchResponse.cn.length;
						for (var ix=0;ix<len;ix++) {
							var cnObject = new Object();
							if(searchResponse.cn[ix]._attrs) {
								for (var a in searchResponse.cn[ix]._attrs) {
									cnObject[a] = searchResponse.cn[ix]._attrs[a];
								}
								this._containedObject[ZaDomain.A_GALTestSearchResults].push(cnObject);						
							}
						}
					}
				}
				
				var syncResponse = batchResp.CheckGalConfigResponse[1];
				this._containedObject[ZaDomain.A_GALSyncTestResultCode] = syncResponse.code[0]._content;	
				if(this._containedObject[ZaDomain.A_GALSyncTestResultCode] != ZaDomain.Check_OK) {
					this._containedObject[ZaDomain.A_GALSyncTestMessage] = syncResponse.message[0]._content;		
					this._containedObject[ZaDomain.A_GALSyncTestSearchResults] = null;			
				}				
			}
		}
		if(batchResp.Fault) {
			var fault = batchResp.Fault[0];
			
			var faultCode = AjxStringUtil.getAsString(fault.Code.Value);
			var errorCode = AjxStringUtil.getAsString(fault.Detail.Error.Code);
			var msg = AjxStringUtil.getAsString(fault.Reason.Text);
			var trace="";
			if(fault.Detail.Error.Trace) {
				trace = fault.Detail.Error.Trace;
			}
				
			var errMsg = [errorCode,msg,trace].join("\n");
			if(searchResponse) {
				this._containedObject[ZaDomain.A_GALSyncTestResultCode] = errorCode;
				this._containedObject[ZaDomain.A_GALSyncTestMessage] = errMsg;
				this._containedObject[ZaDomain.A_GALSyncTestSearchResults] = null;
			} else {
				this._containedObject[ZaDomain.A_GALSearchTestResultCode] = errorCode;
				this._containedObject[ZaDomain.A_GALSearchTestMessage] = errMsg;
				this._containedObject[ZaDomain.A_GALSearchTestSearchResults] = null;
				
				this._containedObject[ZaDomain.A_GALSyncTestResultCode] = ZaDomain.Check_SKIPPED;
			}
		}
	}

	this.goPage(8);
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
	if(this._containedObject[ZaModel.currentStep] == 8) {
		//skip 7th step
		this.goPage(6);
	} else if (this._containedObject[ZaModel.currentStep] == 6 && this._containedObject.attrs[ZaDomain.A_GALSyncUseGALSearch]=="TRUE") {
		 this.goPage(4); //skip step 5 if using same settings for sync as for search
	} else {
		this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this.goPage(this._containedObject[ZaModel.currentStep]-1);

	}
}

ZaGALConfigXWizard.prototype.goNext = 
function() {
	if(this._containedObject[ZaModel.currentStep] == 2 && this._containedObject.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal) {	
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
	if(this._containedObject[ZaModel.currentStep] == 3) {
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
		this.goPage(4);
	} else if(this._containedObject[ZaModel.currentStep] == 4) { 
		if(this._containedObject.attrs[ZaDomain.A_GALSyncUseGALSearch]=="FALSE") {
			this.goPage(5);
		} else {
			this.goPage(6);
		}
	} else if(this._containedObject[ZaModel.currentStep] == 6) {
		if(!this._containedObject[ZaDomain.A_GALSampleQuery]) {
			this._app.getCurrentController().popupErrorDialog(ZaMsg.ERROR_SEARCH_TERM_REQUIRED);			
			return;
		}
 		this.testSetings();
		this.goPage(7);
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

ZaGALConfigXWizard.myXFormModifier = function(xFormObject) {
	var resultHeaderList = new Array();
	resultHeaderList[0] = new ZaListHeaderItem("email", ZaMsg.ALV_Name_col, null, "116px", null, "email", true, true);
	resultHeaderList[1] = new ZaListHeaderItem("fullName", ZaMsg.ALV_FullName_col, null, "116px", null, "fullName", true, true);
	
	xFormObject.items = [
		{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices},
		{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
		{type:_SPACER_,  align:_CENTER_, valign:_TOP_},				
		{type: _SWITCH_,
			items: [
				{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 1", relevantBehaviorBehavior:_HIDE_,
					items: [
						{ref:ZaDomain.A_GalMode, type:_OSELECT1_, label:ZaMsg.Domain_GalMode, labelLocation:_LEFT_, choices:this.GALModes, onChange:ZaGALConfigXWizard.onGalModeChange},
						{ref:ZaDomain.A_GalMaxResults, type:_INPUT_, label:ZaMsg.NAD_GalMaxResults, labelLocation:_LEFT_}
					]
				},
				{type:_CASE_, relevant:"(instance[ZaModel.currentStep] == 2 && instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal)", relevantBehavior:_HIDE_,
					items: [
						{ref:ZaDomain.A_GALServerType, type:_OSELECT1_, label:ZaMsg.Domain_GALServerType, labelLocation:_LEFT_, choices:this.GALServerTypes, onChange:ZaGALConfigXWizard.onGALServerTypeChange},
						{type:_GROUP_, numCols:6, colSpan:6,label:"   ",labelLocation:_LEFT_,
							items: [
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerName, width:"200px"},
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerPort,  width:"40px"},	
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALUseSSL, width:"60px"}									
							]
						},
						{ref:ZaDomain.A_GalLdapURL, type:_REPEAT_, label:ZaMsg.Domain_GalLdapURL+":", repeatInstance:"", showAddButton:true, showRemoveButton:true,  
							addButtonLabel:ZaMsg.Domain_AddURL, 
							removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,								
							showAddOnNextRow:true,							
							items: [
								{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"3269",ldapPort:"3268",  labelLocation:_NONE_}
							]
						},
						{ref:ZaDomain.A_GalLdapFilter, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, textWrapping:"soft", relevant:"instance.attrs[ZaDomain.A_GALServerType] == 'ldap'", relevantBehavior:_DISABLE_},
						{ref:ZaDomain.A_zimbraGalAutoCompleteLdapFilter, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_zimbraGalAutoCompleteLdapFilter, labelLocation:_LEFT_, textWrapping:"soft", relevant:"instance.attrs[ZaDomain.A_GALServerType] == ZaDomain.GAL_ServerType_ldap", relevantBehavior:_DISABLE_},						
						{ref:ZaDomain.A_GalLdapSearchBase, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_, textWrapping:"soft"}
					]
				},
				{type:_CASE_, relevant:"(instance[ZaModel.currentStep] == 3 && instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal)", relevantBehavior:_HIDE_,
					items: [
						{ref:ZaDomain.A_UseBindPassword, type:_CHECKBOX_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaDomain.A_GalLdapBindDn, type:_INPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
						{ref:ZaDomain.A_GalLdapBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
						{ref:ZaDomain.A_GalLdapBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPasswordConfirm, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_}							
					]			
				},				
				{type:_CASE_, relevant:"(instance[ZaModel.currentStep] == 2 && instance.attrs[ZaDomain.A_GalMode] == ZaDomain.GAL_Mode_internal)", relevantBehavior:_HIDE_,
					items: [
						{type:_OUTPUT_, value:ZaMsg.Domain_GAL_Config_Complete}
					]
				}, 
				{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 4", relevantBehavior:_HIDE_,
					items: [
						{ref:ZaDomain.A_GALSyncUseGALSearch, type:_CHECKBOX_, label:ZaMsg.Domain_GALSyncUseGALSearch, 
							labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",
							labelCssClass:"xform_label", align:_LEFT_,labelWrap:true,
							onChange:ZaGALConfigXWizard.onGALSyncChange
						},
						{ref:ZaDomain.A_GALSyncServerType, type:_OSELECT1_, label:ZaMsg.Domain_GALServerType, labelLocation:_LEFT_, 
							choices:this.GALServerTypes, onChange:ZaGALConfigXWizard.onGALSyncServerTypeChange,
							relevant:"ZaGALConfigXWizard.getGalSyncConfigSeparate.call(item)",
							relevantBehavior:_DISABLE_
						},
						{type:_GROUP_, numCols:6, colSpan:6,label:"   ",labelLocation:_LEFT_,
							relevant:"ZaGALConfigXWizard.getGalSyncConfigSeparate.call(item)",
							relevantBehavior:_DISABLE_,
							items: [
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerName, width:"200px"},
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},									
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALServerPort,  width:"40px"},	
								{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_GALUseSSL, width:"60px"}									
							]
						},
						{ref:ZaDomain.A_zimbraGalSyncLdapURL, type:_REPEAT_, label:ZaMsg.Domain_GalLdapURL+":", repeatInstance:"", showAddButton:true, showRemoveButton:true,
							relevant:"ZaGALConfigXWizard.getGalSyncConfigSeparate.call(item)",
							relevantBehavior:_DISABLE_,
							addButtonLabel:ZaMsg.Domain_AddURL, 
							removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,								
							showAddOnNextRow:true,							
							items: [
								{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"3269",ldapPort:"3268",  labelLocation:_NONE_}
							]
						},
						{ref:ZaDomain.A_zimbraGalSyncLdapFilter, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, textWrapping:"soft", 
							relevant:"ZaGALConfigXWizard.getGalSyncLdapFilterEnabled.call(item)",
							relevantBehavior:_DISABLE_
						},
						{ref:ZaDomain.A_zimbraGalSyncLdapSearchBase, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_, textWrapping:"soft", 
							relevant:"ZaGALConfigXWizard.getGalSyncConfigSeparate.call(item)",
							relevantBehavior:_DISABLE_							
						}						
					]
				},
				{type:_CASE_, relevant:"(instance[ZaModel.currentStep] == 5 && instance.attrs[ZaDomain.A_GalMode] != ZaDomain.GAL_Mode_internal && instance.attrs[ZaDomain.A_GALSyncUseGALSearch]=='FALSE')", relevantBehavior:_HIDE_,
					items: [
						{ref:ZaDomain.A_SyncUseBindPassword, type:_CHECKBOX_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
						{ref:ZaDomain.A_zimbraGalSyncLdapBindDn, type:_INPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_SyncUseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
						{ref:ZaDomain.A_zimbraGalSyncLdapBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_SyncUseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
						{ref:ZaDomain.A_GalSyncLdapBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPasswordConfirm, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_SyncUseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_}							
					]			
				},				
				{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 6", relevantBehavior:_HIDE_,width:"500px",
					items: [
						{ref:ZaDomain.A_GalMode, type:_OUTPUT_, label:ZaMsg.Domain_GalMode, choices:this.GALModes},
						{ref:ZaDomain.A_GalMaxResults, type:_OUTPUT_, label:ZaMsg.NAD_GalMaxResults},
						//search
						{type:_ZAWIZ_TOP_GROUPER_, relevant:"instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal", relevantBehavior:_HIDE_,
							useParentTable:false,
							numCols:2,colSpan:2,
							label:ZaMsg.Domain_GALSearchConfigSummary,
							items: [
								{ref:ZaDomain.A_GALServerType, type:_OUTPUT_, label:ZaMsg.Domain_GALServerType, choices:this.GALServerTypes, labelLocation:_LEFT_},
								{ref:ZaDomain.A_GalLdapURL, type:_REPEAT_, label:ZaMsg.Domain_GalLdapURL+":", labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
									items:[
										{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
									]
								},	
								{ref:ZaDomain.A_GalLdapFilter, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_GALServerType] == 'ldap'", relevantBehavior:_HIDE_,required:true},
								{ref:ZaDomain.A_GalLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_},
								{ref:ZaDomain.A_UseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaDomain.A_GalLdapBindDn, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_HIDE_}
							]
						},
						//sync
						{type:_ZAWIZ_TOP_GROUPER_, 
							label:ZaMsg.Domain_GALSyncConfigSummary,
							useParentTable:false,
							numCols:2,colSpan:2,
							items: [
								{ref:ZaDomain.A_GALSyncUseGALSearch, type:_OUTPUT_, label:ZaMsg.Domain_GALSyncUseGALSearch, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaDomain.A_GALSyncServerType, type:_OUTPUT_, label:ZaMsg.Domain_GALServerType, choices:this.GALServerTypes, labelLocation:_LEFT_,
								relevant:"(instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal && instance.attrs[ZaDomain.A_GALSyncUseGALSearch]=='FALSE')", relevantBehavior:_HIDE_},
								{ref:ZaDomain.A_zimbraGalSyncLdapURL, type:_REPEAT_, label:ZaMsg.Domain_GalLdapURL+":", labelLocation:_LEFT_,showAddButton:false, showRemoveButton:false,
									relevant:"(instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal && instance.attrs[ZaDomain.A_GALSyncUseGALSearch]=='FALSE')", relevantBehavior:_HIDE_,
									items:[
										{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_}
									]
								},	
								{ref:ZaDomain.A_zimbraGalSyncLdapFilter, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, 
									relevant:"( (instance.attrs[ZaDomain.A_GALServerType] == 'ldap') && (instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal && instance.attrs[ZaDomain.A_GALSyncUseGALSearch]=='FALSE'))", 
									relevantBehavior:_HIDE_,required:true},
								{ref:ZaDomain.A_zimbraGalSyncLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_,
									relevant:"(instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal && instance.attrs[ZaDomain.A_GALSyncUseGALSearch]=='FALSE')", relevantBehavior:_HIDE_
								},
								{ref:ZaDomain.A_SyncUseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",
									relevant:"(instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal && instance.attrs[ZaDomain.A_GALSyncUseGALSearch]=='FALSE')", relevantBehavior:_HIDE_								
								},
								{ref:ZaDomain.A_zimbraGalSyncLdapBindDn, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, 
									relevant:"( (instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE') && (instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal && instance.attrs[ZaDomain.A_GALSyncUseGALSearch]=='FALSE'))", 
									relevantBehavior:_HIDE_
								}
							]
						},
						{ref:ZaDomain.A_GALSampleQuery, type:_INPUT_, label:ZaMsg.Domain_GALSampleSearchName, labelLocation:_LEFT_, labelWrap:true, cssStyle:"width:100px;"}					
					]
				},
				{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 7", relevantBehavior:_HIDE_,
					items: [
						{type:_OUTPUT_, value:ZaMsg.Domain_GALTestingInProgress}
					]	
				}, 
				{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 8", relevantBehavior:_HIDE_,width:"500px",
					items: [
						{type:_ZAWIZ_TOP_GROUPER_, relevant:"instance[ZaDomain.A_GALSearchTestResultCode] == ZaDomain.Check_OK", 
							label:ZaMsg.Domain_GALSearchTestResults,
							numCols:2,
							items: [
								{type:_DWT_ALERT_,content:ZaMsg.Domain_GALSearchTestSuccessful,
									ref:null,
									colSpan:"2",
									iconVisible: false,
									align:_CENTER_,				
									style: DwtAlert.INFORMATION
								},										
								{type:_OUTPUT_, value:ZaMsg.Domain_GALSearchResult,  align:_CENTER_, colSpan:2, relevant:"(instance[ZaDomain.A_GALTestSearchResults]!=null && instance[ZaDomain.A_GALTestSearchResults].length>0)"},											
								{type:_SPACER_,  align:_CENTER_, valign:_TOP_, colSpan:"*"},	
								{ref: ZaDomain.A_GALTestSearchResults, type:_DWT_LIST_, height:"140px", width:"260px",colSpan:2,
			 				    	cssClass: "DLSource", forceUpdate: true, 
			 				    	widgetClass:ZaGalObjMiniListView, headerList:resultHeaderList,
			 				    	hideHeader:true
			 				    }
							]
						},
						{type:_ZAWIZ_TOP_GROUPER_, relevant:"instance[ZaDomain.A_GALSearchTestResultCode] != ZaDomain.Check_OK",
							label:ZaMsg.Domain_GALSearchTestResults,
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
						{type:_ZAWIZ_TOP_GROUPER_, relevant:"instance[ZaDomain.A_GALSyncTestResultCode] == ZaDomain.Check_OK", 
							label:ZaMsg.Domain_GALSyncTestResults,
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
						{type:_ZAWIZ_TOP_GROUPER_, relevant:"instance[ZaDomain.A_GALSyncTestResultCode] != ZaDomain.Check_OK",
							label:ZaMsg.Domain_GALSyncTestResults,
							numCols:2,						
							items: [
								{type:_DWT_ALERT_,content:ZaMsg.Domain_GALSyncTestFailed,
									ref:null,
									colSpan:"2",
									iconVisible: true,
									align:_CENTER_,				
									style: DwtAlert.WARNING,
									relevant:"(instance[ZaDomain.A_GALSyncTestResultCode] != ZaDomain.Check_SKIPPED)",
									relevantBehavior:_HIDE_
								},
								{type:_DWT_ALERT_,content:ZaMsg.Domain_GALSyncTestSkipped,
									ref:null,
									colSpan:"2",
									iconVisible: true,
									align:_CENTER_,				
									style: DwtAlert.WARNING,
									relevant:"(instance[ZaDomain.A_GALSyncTestResultCode] == ZaDomain.Check_SKIPPED)",
									relevantBehavior:_HIDE_
								},															
								{type:_OUTPUT_, ref:ZaDomain.A_GALSyncTestResultCode, label:ZaMsg.Domain_GALTestResult, choices:this.TestResultChoices,
									relevant:"(instance[ZaDomain.A_GALSyncTestResultCode] != ZaDomain.Check_SKIPPED)",
									relevantBehavior:_HIDE_
								},
								{type:_TEXTAREA_, ref:ZaDomain.A_GALSyncTestMessage, label:ZaMsg.Domain_GALTestMessage, height:"200px", width:"380px",
									relevant:"(instance[ZaDomain.A_GALSyncTestResultCode] != ZaDomain.Check_SKIPPED)",
									relevantBehavior:_HIDE_
								}
							]
						}
					]
				}
			]	
		}	
	];
}

ZaXDialog.XFormModifiers["ZaGALConfigXWizard"].push(ZaGALConfigXWizard.myXFormModifier);
