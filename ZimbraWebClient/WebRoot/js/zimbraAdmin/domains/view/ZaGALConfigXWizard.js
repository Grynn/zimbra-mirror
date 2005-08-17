function ZaGALConfigXWizard (parent, app) {
	this._app=app;
	ZaXWizardDialog.call(this, parent, null, ZaMsg.NCD_GALConfigTitle, "550px", "300px");
	this.stepChoices = [
		{label:ZaMsg.TABT_GALMode, value:1},
		{label:ZaMsg.TABT_GALonfiguration, value:2}, 
		{label:ZaMsg.TABT_GALonfiguration, value:3},		
		{label:ZaMsg.TABT_GALonfigSummary, value:4},
		{label:ZaMsg.TABT_TestGalConfig, value:5},
		{label:ZaMsg.TABT_GalTestResult, value:6}	
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
}

ZaGALConfigXWizard.prototype = new ZaXWizardDialog;
ZaGALConfigXWizard.prototype.constructor = ZaGALConfigXWizard;

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
	
	this._containedObject[ZaModel.currentStep] = 1;
	this._localXForm.setInstance(this._containedObject);	
}


ZaGALConfigXWizard.prototype.generateLDAPUrl = 
function () {
	var ldapURL = "";
	if(this._containedObject.attrs[ZaDomain.A_GALUseSSL] == "TRUE") {
		ldapURL +="ldaps://";
	} else {
		ldapURL +="ldap://";
	}
	ldapURL +=this._containedObject.attrs[ZaDomain.A_GALServerName];
	ldapURL +=":";
	ldapURL +=this._containedObject.attrs[ZaDomain.A_GALServerPort];
	ldapURL +="/";
	this._containedObject.attrs[ZaDomain.A_GalLdapURL] = ldapURL;
}

/**
* static change handlers for the form
**/
ZaGALConfigXWizard.onGALServerTypeChange =
function (value, event, form) {
	if(value == "ad") {
		form.getInstance().attrs[ZaDomain.A_GalLdapFilter] = "ad";
	} else {
		form.getInstance().attrs[ZaDomain.A_GalLdapFilter] = "";
	}
	this.setInstanceValue(value);	
}

ZaGALConfigXWizard.onUseSSLChange =
function (value, event, form) {
	if(value == "TRUE") {
		form.getInstance().attrs[ZaDomain.A_GALServerPort] = 636;
	} else {
		form.getInstance().attrs[ZaDomain.A_GALServerPort] = 389;
	}
	this.setInstanceValue(value);
	form.parent.generateLDAPUrl();
}


ZaGALConfigXWizard.onGALServerChange = 
function (value, event, form) {
//	form.getInstance().attrs[ZaDomain.A_GALServerName] = value;
	this.setInstanceValue(value);
	form.parent.generateLDAPUrl();
}

ZaGALConfigXWizard.onGALServerPortChange = 
function (value, event, form) {
//	form.getInstance().attrs[ZaDomain.A_GALServerPort] = value;
	this.setInstanceValue(value);
	form.parent.generateLDAPUrl();
}


ZaGALConfigXWizard.onGalModeChange = 
function (value, event, form) {
	this.setInstanceValue(value);
	if(value != "zimbra") {
		form.getInstance().attrs[ZaDomain.A_GalLdapFilter] = "";
		if(!form.getInstance().attrs[ZaDomain.A_GALServerType]) {
			form.getInstance().attrs[ZaDomain.A_GALServerType] = "ldap";
		}
		if(!form.getInstance().attrs[ZaDomain.A_GalLdapURL]) {
			form.getInstance().attrs[ZaDomain.A_GALServerPort] = 389;
			form.getInstance().attrs[ZaDomain.A_GalLdapURL] = "";			
			form.getInstance().attrs[ZaDomain.A_GALUseSSL] = "FALSE";
			form.getInstance().attrs[ZaDomain.A_GALServerName] = "";
			form.getInstance().attrs[ZaDomain.A_UseBindPassword] = "TRUE";
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
	if(arg instanceof AjxException || arg instanceof AjxCsfeException || arg instanceof AjxSoapException) {
		this._containedObject[ZaDomain.A_GALTestResultCode] = arg.code;
		this._containedObject[ZaDomain.A_GALTestMessage] = arg.detail;
	} else {
		this._containedObject[ZaDomain.A_GALTestResultCode] = arg.getBody().firstChild.firstChild.firstChild.nodeValue;
		if(this._containedObject[ZaDomain.A_GALTestResultCode] != ZaDomain.Check_OK) {
			this._containedObject[ZaDomain.A_GALTestMessage] = arg.getBody().firstChild.childNodes[1].firstChild.nodeValue;		
		}
	}
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	this.goPage(6);
}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaGALConfigXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setText(DwtMsg._next);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

ZaGALConfigXWizard.prototype.goPrev =
function () {
	if(this._containedObject[ZaModel.currentStep] == 6) {
		//skip 5th step
		this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_GALTestSettings);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this.goPage(4);
	} else {
		this._button[DwtWizardDialog.NEXT_BUTTON].setText(DwtMsg._next);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		if(this._containedObject[ZaModel.currentStep] == 2) {
			//disable PREV button on the first step
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		}
		this.goPage(this._containedObject[ZaModel.currentStep]-1);
	}
}

ZaGALConfigXWizard.prototype.goNext = 
function() {
	if(this._containedObject[ZaModel.currentStep] == 1 && this._containedObject.attrs[ZaDomain.A_GalMode]==ZaDomain.GAL_Mode_internal) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		this.goPage(this._containedObject[ZaModel.currentStep] + 1);
	} else if(this._containedObject[ZaModel.currentStep] == 3) {
		//clear the password if the checkbox is unchecked
		if(this._containedObject.attrs[ZaDomain.A_UseBindPassword]=="FALSE") {
			this._containedObject.attrs[ZaDomain.A_GalLdapBindPassword] = null;
			this._containedObject.attrs[ZaDomain.A_GalLdapBindPasswordConfirm] = null;
			this._containedObject.attrs[ZaDomain.A_GalLdapBindDn] = null;
		}
		//check that passwords match
		if(this._containedObject.attrs[ZaDomain.A_GalLdapBindPassword]!=this._containedObject.attrs[ZaDomain.A_GalLdapBindPasswordConfirm]) {
			this._app.getCurrentController().popupMsgDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
			return false;
		}
		//change next button to "test"
		this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_GALTestSettings);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this.goPage(4);
	} else if(this._containedObject[ZaModel.currentStep] == 4) {
 		this.testSetings();
		this.goPage(5);
		this._button[DwtWizardDialog.NEXT_BUTTON].setText(DwtMsg._next);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		this.goPage(this._containedObject[ZaModel.currentStep] + 1);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	}
}
ZaGALConfigXWizard.prototype.getMyXForm = 
function () {
	var xFormObject = {
		items: [
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
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 2 && instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal", relevantBehavior:_HIDE_,
						items: [
							{ref:ZaDomain.A_GALServerType, type:_OSELECT1_, label:ZaMsg.Domain_GALServerType, labelLocation:_LEFT_, choices:this.GALServerTypes, onChange:ZaGALConfigXWizard.onGALServerTypeChange},
							{ref:ZaDomain.A_GALServerName, type:_INPUT_, label:ZaMsg.Domain_GALServerName, labelLocation:_LEFT_, onChange:ZaGALConfigXWizard.onGALServerChange},					
							{ref:ZaDomain.A_GALServerPort, type:_INPUT_, label:ZaMsg.Domain_GALServerPort, labelLocation:_LEFT_,onChange:ZaGALConfigXWizard.onGALServerPortChange},
							{ref:ZaDomain.A_GALUseSSL, type:_CHECKBOX_, label:ZaMsg.Domain_GALUseSSL, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaGALConfigXWizard.onUseSSLChange,labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaDomain.A_GalLdapFilter, type:_TEXTAREA_, width:380, height:100, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, textWrapping:"soft", relevant:"instance.attrs[ZaDomain.A_GALServerType] == 'ldap'", relevantBehavior:_DISABLE_},
							{ref:ZaDomain.A_GalLdapSearchBase, type:_TEXTAREA_, width:380, height:50, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_, textWrapping:"soft"}
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 3 && instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal", relevantBehavior:_HIDE_,
						items: [
							{ref:ZaDomain.A_UseBindPassword, type:_CHECKBOX_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE",labelCssClass:"xform_label", align:_LEFT_},
							{ref:ZaDomain.A_GalLdapBindDn, type:_INPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
							{ref:ZaDomain.A_GalLdapBindPassword, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_},
							{ref:ZaDomain.A_GalLdapBindPasswordConfirm, type:_SECRET_, label:ZaMsg.Domain_GalLdapBindPasswordConfirm, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_DISABLE_}							
						]			
					},				
					{type:_CASE_, relevant:"(instance[ZaModel.currentStep] == 2 && instance.attrs[ZaDomain.A_GalMode]==ZaDomain.GAL_Mode_internal)", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_GAL_Config_Complete}
						]
					}, 
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 4", relevantBehavior:_HIDE_,
						items: [
							{ref:ZaDomain.A_GalMode, type:_OUTPUT_, label:ZaMsg.Domain_GalMode, choices:this.GALModes},
							{ref:ZaDomain.A_GalMaxResults, type:_OUTPUT_, label:ZaMsg.NAD_GalMaxResults},
							{type:_SWITCH_, 
								items: [
									{type:_CASE_, relevant:"instance.attrs[ZaDomain.A_GalMode]!=ZaDomain.GAL_Mode_internal", relevantBehavior:_HIDE_,
										items: [
											{ref:ZaDomain.A_GALServerType, type:_OUTPUT_, label:ZaMsg.Domain_GALServerType, choices:this.GALServerTypes, labelLocation:_LEFT_},
											{ref:ZaDomain.A_GALServerName, type:_OUTPUT_, label:ZaMsg.Domain_GALServerName, labelLocation:_LEFT_},					
											{ref:ZaDomain.A_GALServerPort, type:_OUTPUT_, label:ZaMsg.Domain_GALServerPort, labelLocation:_LEFT_},
											{ref:ZaDomain.A_GALUseSSL, type:_OUTPUT_, label:ZaMsg.Domain_GALUseSSL, labelLocation:_LEFT_},
											{ref:ZaDomain.A_GalLdapFilter, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapFilter, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_GALServerType] == 'ldap'", relevantBehavior:_HIDE_},
											{ref:ZaDomain.A_GalLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapSearchBase, labelLocation:_LEFT_},
											{ref:ZaDomain.A_GalLdapURL, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapURL, labelLocation:_LEFT_},
											{ref:ZaDomain.A_UseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_UseBindPassword, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE"},
											{ref:ZaDomain.A_GalLdapBindDn, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapBindDn, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_HIDE_},
//											{ref:ZaDomain.A_GalLdapBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_GalLdapBindPassword, labelLocation:_LEFT_, relevant:"instance.attrs[ZaDomain.A_UseBindPassword] == 'TRUE'", relevantBehavior:_HIDE_},
											{ref:ZaDomain.A_GALSampleQuery, type:_INPUT_, label:ZaMsg.Domain_GALSampleSearchName, labelLocation:_LEFT_, labelWrap:true, cssStyle:"width:100px;"}
										]
									}
								]
							}					
						]
					},
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 5", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_GALTestingInProgress}
						]	
					}, 
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 6", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_,value:ZaMsg.Domain_GALTestResults},
							{type:_SWITCH_,
								items: [
									{type:_CASE_, relevant:"instance[ZaDomain.A_GALTestResultCode] == ZaDomain.Check_OK",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Domain_GALTestSuccessful}
										]
									},
									{type:_CASE_, relevant:	"instance[ZaDomain.A_GALTestResultCode] != ZaDomain.Check_OK",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Domain_GALTestFailed},
											{type:_OUTPUT_, ref:ZaDomain.A_GALTestResultCode, label:ZaMsg.Domain_GALTestResult, choices:this.TestResultChoices},
											{type:_TEXTAREA_, ref:ZaDomain.A_GALTestMessage, label:ZaMsg.Domain_GALTestMessage, height:100}
										]
									}
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

