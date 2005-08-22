function ZaAuthConfigXWizard (parent, app) {
	ZaXWizardDialog.call(this, parent, null, ZaMsg.NCD_AuthConfigTitle, "550px", "300px");

	this.stepChoices = [
		{label:ZaMsg.TABT_AuthMode, value:1},				
		{label:ZaMsg.TABT_AuthSettings, value:2},						
		{label:ZaMsg.TABT_TestAuthSettings, value:3},				
		{label:ZaMsg.TABT_AuthTestResult, value:4}
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
}

ZaAuthConfigXWizard.prototype = new ZaXWizardDialog;
ZaAuthConfigXWizard.prototype.constructor = ZaAuthConfigXWizard;

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
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(DwtMsg._next);
			if(this._containedObject.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_zimbra) {
				this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			} else {
				this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);		
			}
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		} else if (stepNum == 2) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_AuthTestSettings);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		} else if(stepNum == 3) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(DwtMsg._next);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		} else if(stepNum == 4) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(DwtMsg._next);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		}
	}
}
ZaAuthConfigXWizard.prototype.generateLDAPUrl = 
function () {
	var ldapURL = "";
	if(this._containedObject.attrs[ZaDomain.A_AuthLDAPUseSSL] == "TRUE") {
		ldapURL +="ldaps://";
	} else {
		ldapURL +="ldap://";
	}
	ldapURL +=this._containedObject.attrs[ZaDomain.A_AuthLDAPServerName];
	ldapURL +=":";
	ldapURL +=this._containedObject.attrs[ZaDomain.A_AuthLDAPServerPort];
	ldapURL +="/";
	this._containedObject.attrs[ZaDomain.A_AuthLdapURL] = ldapURL;
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
	if(arg instanceof AjxException || arg instanceof ZmCsfeException || arg instanceof AjxSoapException) {
		this._containedObject[ZaDomain.A_AuthTestResultCode] = arg.code;
		this._containedObject[ZaDomain.A_AuthTestMessage] = arg.detail;
	} else {
		this._containedObject[ZaDomain.A_AuthTestResultCode] = arg.getBody().firstChild.firstChild.firstChild.nodeValue;
		if(this._containedObject[ZaDomain.A_AuthTestResultCode] != ZaDomain.Check_OK) {
			this._containedObject[ZaDomain.A_AuthTestMessage] = arg.getBody().firstChild.childNodes[1].firstChild.nodeValue;		
			this._containedObject[ZaDomain.A_AuthComputedBindDn] = arg.getBody().firstChild.lastChild.firstChild.nodeValue;		
		}
	}
	/*
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	*/
	this.goPage(4);
}

/**
* Eevent handlers for form items
**/
ZaAuthConfigXWizard.onAuthMechChange = 
function (value, event, form) {
	this.setInstanceValue(value);
	if(value == ZaDomain.AuthMech_ldap) {
		if(!form.getInstance().attrs[ZaDomain.A_AuthLdapUserDn]) {
			form.getInstance().attrs[ZaDomain.A_AuthLdapUserDn] = "%u,%D";
		}
	} 
	if(value == ZaDomain.AuthMech_ldap || value == ZaDomain.AuthMech_ad) {
		form.getInstance().attrs[ZaDomain.A_AuthLDAPServerPort] = 389;
		form.getInstance().attrs[ZaDomain.A_AuthLDAPUseSSL] = "FALSE";
	}
	if(value == ZaDomain.AuthMech_ad) {
		if(!form.getInstance().attrs[ZaDomain.A_AuthADDomainName]) {
			form.getInstance().attrs[ZaDomain.A_AuthADDomainName] = form.getInstance().attrs[ZaDomain.A_domainName];
		}
	}
	form.parent.changeButtonStateForStep(1);
	/*
	if(value == ZaDomain.AuthMech_zimbra) {
		form.parent._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		form.parent._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	} else {
		form.parent._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		form.parent._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);		
	}*/
}

ZaAuthConfigXWizard.onLDAPPortChange = 
function (value, event, form) {
	this.setInstanceValue(value);
	form.parent.generateLDAPUrl();
	
}

ZaAuthConfigXWizard.onLDAPServerChange = 
function (value, event, form) {
	this.setInstanceValue(value);	
	form.parent.generateLDAPUrl();
}

ZaAuthConfigXWizard.onLDAPUseSSLChange = 
function (value, event, form) {
	if(value == "TRUE") {
		form.getInstance().attrs[ZaDomain.A_AuthLDAPServerPort] = 636;
	} else {
		form.getInstance().attrs[ZaDomain.A_AuthLDAPServerPort] = 389;
	}	
	this.setInstanceValue(value);	
	form.parent.generateLDAPUrl();
}
/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaAuthConfigXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this.changeButtonStateForStep(1);
	/*
	this._button[DwtWizardDialog.NEXT_BUTTON].setText(DwtMsg._next);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);		
	
	if(this._containedObject.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_zimbra) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);		
	} else {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);				
	}	*/
}

ZaAuthConfigXWizard.prototype.goPage =
function(pageNum) {
	ZaXWizardDialog.prototype.goPage.call(this, pageNum);
	this.changeButtonStateForStep(pageNum);
}

ZaAuthConfigXWizard.prototype.goPrev =
function () {
	if(this._containedObject[ZaModel.currentStep] == 4) {
		//skip 3rd step
		/*this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_AuthTestSettings);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);*/
		this.goPage(2);
	} else {
		this._button[DwtWizardDialog.NEXT_BUTTON].setText(DwtMsg._next);
		if(this._containedObject[ZaModel.currentStep] == 2) {
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		}
		this.goPage(this._containedObject[ZaModel.currentStep]-1);
	}
}

ZaAuthConfigXWizard.prototype.goNext = 
function() {
	if(this._containedObject[ZaModel.currentStep] == 1) {
		//change next button to "test"
		/*this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_AuthTestSettings);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);*/
		this.goPage(2);
	} else if(this._containedObject[ZaModel.currentStep] == 2) {
 		this.testSetings();
		this.goPage(3);
/*		this._button[DwtWizardDialog.NEXT_BUTTON].setText(DwtMsg._next);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);*/
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
	
	this._containedObject[ZaModel.currentStep] = 1;
	this._localXForm.setInstance(this._containedObject);	
}

/**
* XForm definition
**/
ZaAuthConfigXWizard.prototype.getMyXForm = 
function () {
	var xFormObject = {
		items:[
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},				
			{type:_SWITCH_,
				items:[
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 1", relevantBehavior:_HIDE_,
						items:[
							{type:_OSELECT1_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs, ref:ZaDomain.A_AuthMech, onChange:ZaAuthConfigXWizard.onAuthMechChange},
							{type:_SWITCH_,
								items: [
									{type:_CASE_, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad",
										items:[
											{ref:ZaDomain.A_AuthLDAPServerName, type:_INPUT_, label:ZaMsg.Domain_AuthADServerName, labelLocation:_LEFT_, onChange:ZaAuthConfigXWizard.onLDAPServerChange},
											{ref:ZaDomain.A_AuthADDomainName, type:_INPUT_, label:ZaMsg.Domain_AuthADDomainName, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthLDAPServerPort, type:_INPUT_, label:ZaMsg.Domain_AuthADServerPort, labelLocation:_LEFT_, onChange:ZaAuthConfigXWizard.onLDAPPortChange},
											{ref:ZaDomain.A_AuthLDAPUseSSL, type:_CHECKBOX_, label:ZaMsg.Domain_AuthADUseSSL, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaAuthConfigXWizard.onLDAPUseSSLChange,labelCssClass:"xform_label", align:_LEFT_}
										]
									},
									{type:_CASE_, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap",
										items:[
											{ref:ZaDomain.A_AuthLDAPServerName, type:_INPUT_, label:ZaMsg.Domain_AuthLDAPServerName, labelLocation:_LEFT_, onChange:ZaAuthConfigXWizard.onLDAPServerChange},
											{ref:ZaDomain.A_AuthLDAPServerPort, type:_INPUT_, label:ZaMsg.Domain_AuthLDAPServerPort, labelLocation:_LEFT_, onChange:ZaAuthConfigXWizard.onLDAPServerChange},							
											{ref:ZaDomain.A_AuthLDAPUseSSL, type:_CHECKBOX_, label:ZaMsg.Domain_AuthLDAPUseSSL, labelLocation:_LEFT_,trueValue:"TRUE", falseValue:"FALSE", onChange:ZaAuthConfigXWizard.onLDAPUseSSLChange,labelCssClass:"xform_label", align:_LEFT_},
											{ref:ZaDomain.A_AuthLdapUserDn, type:_INPUT_, label:ZaMsg.Domain_AuthLdapUserDn, labelLocation:_LEFT_},
//											{ref:ZaDomain.A_AuthLdapURL, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_},
											{type:_OUTPUT_, value:ZaMsg.NAD_DomainsAuthStr, colSpan:2},												
											
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
					{type:_CASE_, numCols:2, relevant:"instance[ZaModel.currentStep] == 2 && instance.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_zimbra", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_Auth_ConfigSummary, align:_CENTER_, colSpan:"*"}, 
							{type:_SPACER_, height:10},
							{ref:ZaDomain.A_AuthMech, type:_OUTPUT_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs, alignment:_LEFT_},
							{type:_SWITCH_, useParentTable:true,
								items: [
									{type:_CASE_, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad", useParentTable:true,
										items:[
											{ref:ZaDomain.A_AuthLDAPServerName, type:_OUTPUT_, label:ZaMsg.Domain_AuthADServerName, labelCssClass:"xform_label_left"},
											{ref:ZaDomain.A_AuthADDomainName, type:_OUTPUT_, label:ZaMsg.Domain_AuthADDomainName, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthLDAPServerPort, type:_OUTPUT_, label:ZaMsg.Domain_AuthADServerPort, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthLDAPUseSSL, type:_OUTPUT_, label:ZaMsg.Domain_AuthADUseSSL, labelWrap:true, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES}
										]
									},
									{type:_CASE_, relevant:"instance.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap", useParentTable:true,
										items:[
											{ref:ZaDomain.A_AuthLDAPServerName, type:_OUTPUT_, label:ZaMsg.Domain_AuthLDAPServerName, labelLocation:_LEFT_},
											{ref:ZaDomain.A_AuthLDAPServerPort, type:_OUTPUT_, label:ZaMsg.Domain_AuthLDAPServerPort, labelLocation:_LEFT_},							
											{ref:ZaDomain.A_AuthLDAPUseSSL, type:_OUTPUT_, label:ZaMsg.Domain_AuthLDAPUseSSL, labelLocation:_LEFT_,choices:ZaModel.BOOLEAN_CHOICES},
											{ref:ZaDomain.A_AuthLdapUserDn, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapUserDn, labelLocation:_LEFT_},
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
					{type:_CASE_, relevant:"instance[ZaModel.currentStep] == 3 && instance.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_zimbra", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_,value:ZaMsg.Domain_AuthTestingInProgress}
						]
					},
					{type:_CASE_,  numCols:1, relevant:"instance[ZaModel.currentStep] == 4 && instance.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_zimbra", relevantBehavior:_HIDE_,
						items: [
							{type:_OUTPUT_,value:ZaMsg.Domain_AuthTestResults, alignment:_CENTER_},
							{type:_SWITCH_,
								items: [
									{type:_CASE_, relevant:"instance[ZaDomain.A_AuthTestResultCode] == ZaDomain.Check_OK",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Domain_AuthTestSuccessful, alignment:_CENTER_}
										]
									},
									{type:_CASE_, relevant:	"instance[ZaDomain.A_AuthTestResultCode] != ZaDomain.Check_OK",
										items: [
											{type:_OUTPUT_, value:ZaMsg.Domain_AuthTestFailed, alignment:_CENTER_},
											{type:_OUTPUT_, ref:ZaDomain.A_AuthTestResultCode, label:ZaMsg.Domain_AuthTestResultCode, choices:this.TestResultChoices, alignment:_LEFT_},
											{type:_OUTPUT_, ref:ZaDomain.A_AuthComputedBindDn, label:ZaMsg.Domain_AuthComputedBindDn, alignment:_LEFT_},
											{type:_TEXTAREA_, ref:ZaDomain.A_AuthTestMessage, label:ZaMsg.Domain_AuthTestMessage, height:150, width:200, alignment:_LEFT_}
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

