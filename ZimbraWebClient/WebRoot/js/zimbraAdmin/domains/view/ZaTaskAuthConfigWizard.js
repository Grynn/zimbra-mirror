/**
 * Created by IntelliJ IDEA.
 * User: qinan
 * Date: 9/23/11
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */


ZaTaskAuthConfigWizard = function(parent) {

	ZaXWizardDialog.call(this, parent, null, ZaMsg.NCD_AuthConfigTitle,"700px", "350px","ZaTaskAuthConfigWizard", null, ZaId.DLG_AUTH_CONFIG);

	this.AuthMechs = [
		{label:"<b>" + ZaMsg.AuthMech_zimbra + "</b>", value:ZaDomain.AuthMech_zimbra},
		{label:"<b>" + ZaMsg.AuthMech_ldap + "</b>", value:ZaDomain.AuthMech_ldap},
		{label:"<b>" + ZaMsg.AuthMech_ad + "</b>", value:ZaDomain.AuthMech_ad}
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

    ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_0 = ++this.TAB_INDEX;
	ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_1 = ++this.TAB_INDEX;
	ZaTaskAuthConfigWizard.AUTH_CONFIG_BIND_PWD_STEP = ++this.TAB_INDEX;
	ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP = ++this.TAB_INDEX;
	ZaTaskAuthConfigWizard.AUTH_TEST_STEP = ++this.TAB_INDEX;
	ZaTaskAuthConfigWizard.AUTH_TEST_RESULT_STEP = ++this.TAB_INDEX;
    ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP = ++this.TAB_INDEX;
    ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_1 = ++this.TAB_INDEX;
    ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_2 = ++this.TAB_INDEX;
	ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP = ++this.TAB_INDEX;

	this.stepChoices = [
        {label:ZaMsg.AuthSettings, value:ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_0},
		{label:ZaMsg.AuthSettings, value:ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_1},
		{label:ZaMsg.AuthSettings, value:ZaTaskAuthConfigWizard.AUTH_CONFIG_BIND_PWD_STEP},
		{label:ZaMsg.AuthSettings, value:ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP},
		{label:ZaMsg.TestAuthSettings, value:ZaTaskAuthConfigWizard.AUTH_TEST_STEP},
		{label:ZaMsg.AuthTestResult, value:ZaTaskAuthConfigWizard.AUTH_TEST_RESULT_STEP},
        {label:ZaMsg.AuthSetting_Spnego, value:ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP},
        {label:ZaMsg.AuthSetting_Spnego, value:ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_1},
        {label:ZaMsg.AuthSetting_SpnegoDomain, value:ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_2},
		{label:ZaMsg.DomainConfigComplete, value:ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP}
	];

	this.initForm(ZaDomain.myXModel,this.getMyXForm());
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaTaskAuthConfigWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaTaskAuthConfigWizard.prototype.handleXFormChange));
	this.lastErrorStep=0;
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_domains/authentication_settings.htm?locid="+AjxEnv.DEFAULT_LOCALE;
}

ZaTaskAuthConfigWizard.prototype = new ZaXWizardDialog;
ZaTaskAuthConfigWizard.prototype.constructor = ZaTaskAuthConfigWizard;
ZaXDialog.XFormModifiers["ZaTaskAuthConfigWizard"] = new Array();

if(ZaDomain) {
    ZaDomain.A2_zimbraSpnegoAuthRealm = "zimbraSpnegoAuthRealm";
    ZaDomain.A2_zimbraSpnegoAuthErrorURL = "zimbraSpnegoAuthErrorURL";
    ZaDomain.A2_zimbraSpnegoAuthEnabled = "zimbraSpnegoAuthEnabled";
    ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled = "zimbraSpnegoGlobalAuthEnabled";
    ZaDomain.A2_zimbraSpnegoAuthSummary = "zimbraSpnegoAuthSummary";
    ZaDomain.A2_zimbraSpnegoApplyFor = "zimbraSpnegoApplyFor";
    ZaDomain.A2_zimbraSpnegoAuthPrincipal = "zimbraSpnegoAuthPrincipal";
    ZaDomain.A2_zimbraSpnegoAuthTargetName = "zimbraSpnegoAuthTargetName";
    if(ZaDomain.myXModel) {
        ZaDomain.myXModel.items.push(
            {id:ZaDomain.A2_zimbraSpnegoApplyFor, ref:ZaDomain.A2_zimbraSpnegoApplyFor, type: _STRING_},
            {id:ZaDomain.A2_zimbraSpnegoAuthPrincipal, ref:ZaDomain.A2_zimbraSpnegoAuthPrincipal, type: _STRING_},
            {id:ZaDomain.A2_zimbraSpnegoAuthRealm, ref: ZaDomain.A2_zimbraSpnegoAuthRealm, type: _STRING_ },
            {id:ZaDomain.A2_zimbraSpnegoAuthTargetName, ref: ZaDomain.A2_zimbraSpnegoAuthTargetName, type: _STRING_ },
            {id:ZaDomain.A2_zimbraSpnegoAuthErrorURL, ref: ZaDomain.A2_zimbraSpnegoAuthErrorURL, type: _STRING_ },
            {id:ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled, ref:ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
            {id:ZaDomain.A2_zimbraSpnegoAuthEnabled, ref:ZaDomain.A2_zimbraSpnegoAuthEnabled, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
            {id:ZaDomain.A2_zimbraSpnegoAuthSummary, ref:ZaDomain.A2_zimbraSpnegoAuthSummary, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES}
        );
    }
}

ZaMsg.AuthSetting_Spnego = "SPNEGO Global and Server Setting";
ZaMsg.AuthSetting_SpnegoDomain = "SPNEGO Domain Setting";
ZaMsg.SpnegoGlobalConfig = "Change Global SPNEGO Setting ...";
ZaMsg.SpnegoServerConfig = "Change Server SPNEGO Setting ...";
ZaMsg.SpnegoDomainConfig = "Change Domain SPNEGO Setting ...";

ZaMsg.EnableSpnegoGlobal = "Enable SPNEGO authentication";

ZaTaskAuthConfigWizard.prototype.handleXFormChange =
function () {
	if(this._localXForm.hasErrors()) {
		if(this.lastErrorStep < this._containedObject[ZaModel.currentStep])
			this.lastErrorStep=this._containedObject[ZaModel.currentStep];
	} else {
		this.lastErrorStep=0;
	}
	this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]);
}

ZaTaskAuthConfigWizard.prototype.changeButtonStateForStep =
function(stepNum) {
    this._button[DwtWizardDialog.PREV_BUTTON].setText(AjxMsg._prev);
    this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
    this._button[DwtDialog.CANCEL_BUTTON].setVisible(true);
    this._button[ZaXWizardDialog.HELP_BUTTON].setVisible(true);
    this._button[DwtWizardDialog.FINISH_BUTTON].setVisible(true);

	if(this.lastErrorStep == stepNum) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		if(stepNum>1)
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	} else {

		if(stepNum == ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_0) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		} else if (stepNum == ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP) {
			//this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_AuthTestSettings);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		//} else if(stepNum == ZaTaskAuthConfigWizard.AUTH_TEST_STEP) {
			//this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			//this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			//this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
			//this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
        } else if(stepNum == ZaTaskAuthConfigWizard.AUTH_TEST_RESULT_STEP) {

            this._button[DwtWizardDialog.PREV_BUTTON].setText(AjxMsg.cancel);
            this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg.ok);
			this._button[DwtDialog.CANCEL_BUTTON].setVisible(false);
            this._button[ZaXWizardDialog.HELP_BUTTON].setVisible(false);
            this._button[DwtWizardDialog.FINISH_BUTTON].setVisible(false);
		} else if(stepNum == ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else if(stepNum == ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_1) {
            this._button[DwtWizardDialog.PREV_BUTTON].setText(AjxMsg.cancel);
            this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg.ok);
			this._button[DwtDialog.CANCEL_BUTTON].setVisible(false);
            this._button[ZaXWizardDialog.HELP_BUTTON].setVisible(false);
            this._button[DwtWizardDialog.FINISH_BUTTON].setVisible(false);
		} else {
			this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		}
	}
}

ZaTaskAuthConfigWizard.prototype.testSetings =
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
ZaTaskAuthConfigWizard.prototype.checkCallBack =
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
	this.goPage(ZaTaskAuthConfigWizard.AUTH_TEST_RESULT_STEP);
}

/**
* Eevent handlers for form items
**/
ZaTaskAuthConfigWizard.onAuthMechChange =
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
ZaTaskAuthConfigWizard.prototype.popup =
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this.changeButtonStateForStep(ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_0);

}

ZaTaskAuthConfigWizard.prototype.goPage =
function(pageNum) {
	ZaXWizardDialog.prototype.goPage.call(this, pageNum);
	this.changeButtonStateForStep(pageNum);
}

ZaTaskAuthConfigWizard.prototype.goPrev =
function () {
	if(this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.AUTH_TEST_RESULT_STEP) {
		//skip ZaTaskAuthConfigWizard.AUTH_TEST_STEP step
		this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP);
	} else if (this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad) {
		this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_1);//skip ZaTaskAuthConfigWizard.AUTH_CONFIG_BIND_PWD_STEP step for Active Directory
	} else if(this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_zimbra) {
		this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_0);
	} else if(this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP
            && (this._containedObject[ZaDomain.A2_zimbraSpnegoAuthEnabled]!="TRUE"
            || this._containedObject.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_ad)) {
		this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP);
	} else if(this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP
            && (this._containedObject[ZaDomain.A2_zimbraSpnegoAuthEnabled]=="TRUE"
            && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad)) {
		this.goPage(ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP);
	} else if(this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP) {
		this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP);
	} else {
		this.goPage(this._containedObject[ZaModel.currentStep]-1);
	}
}

ZaTaskAuthConfigWizard.prototype.goNext =
function() {
	if(this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP) {
 		//this.testSetings();
		//this.goPage(ZaTaskAuthConfigWizard.AUTH_TEST_STEP);
        if(this._containedObject[ZaDomain.A2_zimbraSpnegoAuthEnabled]=="TRUE"
            && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad)
            this.goPage(ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP);
        else
            this.goPage(ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP);
	} else if (this._containedObject[ZaModel.currentStep]==ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_1 && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad) {
		if(!this._containedObject.attrs[ZaDomain.A_AuthLdapURL]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_LDAP_URL_REQUIRED);
			return false;
		}
		this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP);//skip ZaTaskAuthConfigWizard.AUTH_CONFIG_BIND_PWD_STEP step for Active Directory
	} else if(this._containedObject[ZaModel.currentStep]==ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_1 && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap) {
		var temp = this._containedObject.attrs[ZaDomain.A_AuthLdapURL].join(" ");
		if(this._containedObject.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled] == "TRUE") {
			//check that we don't have ldaps://
			if(temp.indexOf("ldaps://") > -1) {
				ZaApp.getInstance().getCurrentController().popupWarningDialog(ZaMsg.Domain_WarningStartTLSIgnored)
			}
		}
		this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_BIND_PWD_STEP);
	}  else if (this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.AUTH_CONFIG_BIND_PWD_STEP) {
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
		this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP);
	} else if(this._containedObject[ZaModel.currentStep]==ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_0 && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_zimbra) {
		this.goPage(ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP);
	} else if(this._containedObject[ZaModel.currentStep]==ZaTaskAuthConfigWizard.AUTH_TEST_RESULT_STEP
            && (this._containedObject[ZaDomain.A2_zimbraSpnegoAuthEnabled]!="TRUE"
            || this._containedObject.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_ad)) {
		this.goPage(ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP);
	} else if(this._containedObject[ZaModel.currentStep]==ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP) {
		this.goPage(this._containedObject[ZaModel.currentStep]+2);
	} else {
		this.goPage(this._containedObject[ZaModel.currentStep]+1);
	}
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaDomain object to display
**/
ZaTaskAuthConfigWizard.prototype.setObject =
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

    this._containedObject[ZaModel.currentStep] = ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_0;
	this._localXForm.setInstance(this._containedObject);
}

/**
* XForm definition
**/

ZaTaskAuthConfigWizard.myXFormModifier = function(xFormObject) {
	xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices,valueChangeEventSources:[ZaModel.currentStep]},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_,width:580, valign:_TOP_,
				items:[
					{type:_CASE_, numCols:2,colSizes:["60px","430px"],	caseKey:ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_0,
						items:[
                            {type:_OUTPUT_, value:"<b>Authentication mode for this domain</b>",
                                colSpan:"*", cssStyle:"padding-left:10px;padding-top:10px;"
                            },
                            {type: _SPACER_, height: 10 },
							{ref:ZaDomain.A_AuthMech, type:_RADIO_,groupname:"auth_mechanism_group",
								label:"<b>" + ZaMsg.AuthMech_zimbra + "</b>", labelLocation:_RIGHT_,
								updateElement:function (value) {
									this.getElement().checked = (value == ZaDomain.AuthMech_zimbra);
								},
								elementChanged: function(elementValue,instanceValue, event) {
									this.getForm().itemChanged(this, ZaDomain.AuthMech_zimbra, event);
								},
								visibilityChecks:[],enableDisableChecks:[]
							},
                            {type: _GROUP_, colSpan:2, numCols:2, colSizes: ["80px", "*" ],
                                items :[
                                    {type:_OUTPUT_, label:"" , value:"Help text"},
                                    {type:_OUTPUT_, label:"", value:"Help text 2"}
                                ]
                            },
                            {type: _SPACER_, height: 15 },
							{ref:ZaDomain.A_AuthMech, type:_RADIO_,groupname:"auth_mechanism_group",
								label:"<b>" + ZaMsg.AuthMech_ad + "</b>", labelLocation:_RIGHT_,
								updateElement:function (value) {
									this.getElement().checked = (value == ZaDomain.AuthMech_ad);
								},
								elementChanged: function(elementValue,instanceValue, event) {
									this.getForm().itemChanged(this, ZaDomain.AuthMech_ad, event);
								},
								visibilityChecks:[],enableDisableChecks:[]
							},
                            {type: _GROUP_, colSpan:2, numCols:2, colSizes: ["80px", "*" ],
                                items :[
                                    {type:_OUTPUT_, label:"" , value:"Help text"},
                                    {type:_OUTPUT_, label:"", value:"Help text 2"},
                                    {ref:ZaDomain.A2_zimbraSpnegoAuthEnabled, type:_CHECKBOX_, label:"<b>" + ZaMsg.NAD_Enable_spnego + "</b>",
                                        //onChange: ZaTaskAuthConfigWizard.startTlsEnabledChanged,
                                        subLabel:"",
                                        trueValue:"TRUE", falseValue:"FALSE",labelLocation:_RIGHT_, align:_RIGHT_,
                                        enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ad]],
                                        enableDisableChangeEventSources:[ZaDomain.A_AuthMech]
                                    }
                                ]
                            },
                            {type: _SPACER_, height: 15 },
							{ref:ZaDomain.A_AuthMech, type:_RADIO_,groupname:"auth_mechanism_group",
								label:"<b>" + ZaMsg.AuthMech_ldap + "</b>",  labelLocation:_RIGHT_,
								updateElement:function (value) {
									this.getElement().checked = (value == ZaDomain.AuthMech_ldap);
								},
								elementChanged: function(elementValue,instanceValue, event) {
									this.getForm().itemChanged(this, ZaDomain.AuthMech_ldap, event);
								},
								visibilityChecks:[],enableDisableChecks:[]
							},
                            {type: _GROUP_, colSpan:2, numCols:2, colSizes: ["80px", "*" ],
                                items :[
                                    {type:_OUTPUT_, label:"" , value:"Help text"},
                                    {type:_OUTPUT_, label:"", value:"Help text 2"}
                                ]
                            }
                        ]
                    },
					{type:_CASE_, numCols:2,colSizes:["150px","430px"],	caseKey:ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_1,
                        cellpadding:10,
						items:[
						    //{type:_OUTPUT_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs, ref:ZaDomain.A_AuthMech, //onChange:ZaTaskAuthConfigWizard.onAuthMechChange,
                            //    labelCssStyle:"text-align:left;",
						    //	enableDisableChecks:[],visibilityChecks:[]
						    //},
							{type:_GROUP_, colSpan:2,numCols:2,colSizes:["150px","430px"],
								visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ad]],
								visibilityChangeEventSources:[ZaDomain.A_AuthMech],
								items:[
                                    {type:_OUTPUT_, value:"<b>Active Diectory Setting</b>",  colSpan:"*"
                                        //colSpan:"*", cssStyle:"padding-left:10px;padding-top:10px;"
                                    },
								    {type:_SPACER_, height:20,colSpan:2},
									{ref:ZaDomain.A_AuthADDomainName, type:_TEXTFIELD_, label:ZaMsg.Domain_AuthADDomainName, labelLocation:_LEFT_,
                                        labelCssStyle:"text-align:left;padding-left:20px;",
										visibilityChecks:[],enableDisableChecks:[],bmolsnr:true
									},
                                    {type:_SPACER_, height:10,colSpan:2},
                                    {type:_OUTPUT_, label:"AD Server", value:" ", labelCssStyle:"text-align:left;padding-left:20px;"},
									{type:_GROUP_, numCols:6, colSpan:2,label:null,labelLocation:_LEFT_, containerCssStyle:"padding-left:20px;",
										items: [
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthADServerName, width:"200px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthADServerPort,  width:"40px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthADUseSSL, width:"40px"}
										]
									},
									{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, colSpan:"*",containerCssStyle:"padding-left:20px;",//label:ZaMsg.Domain_AuthADURL,
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
							{type:_GROUP_, colSpan:2,numCols:2,colSizes:["150px","430px"],
								visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ldap]],
								visibilityChangeEventSources:[ZaDomain.A_AuthMech],
								items:[
                                    {type:_OUTPUT_, value:"<b>External LDAP Setting</b>",  colSpan:"*"
                                        //colSpan:"*", cssStyle:"padding-left:10px;padding-top:10px;"
                                    },
                                    {type:_SPACER_, height:20,colSpan:2},
                                    {type:_OUTPUT_, value:"LDAP Setting",  colSpan:"*"
                                    },
									{type:_GROUP_, numCols:6, colSpan:2,label:null,labelLocation:_LEFT_,
										items: [
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthLDAPServerName, width:"200px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthLDAPServerPort,  width:"40px"},
											{type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthLDAPUseSSL, width:"40px"}
										]
									},
									{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, colSpan:"*",cssStyple:"padding-left:10px;",//label:ZaMsg.Domain_AuthLdapURL, repeatInstance:"", showAddButton:true, showRemoveButton:true,
										addButtonLabel:ZaMsg.Domain_AddURL,
										removeButtonLabel:ZaMsg.Domain_REPEAT_REMOVE,
										showAddOnNextRow:true,
										items: [
											{ref:".", type:_LDAPURL_, label:null,ldapSSLPort:"636",ldapPort:"389",  labelLocation:_NONE_,
											visibilityChecks:[],enableDisableChecks:[]}
										]
									},
                                    {type:_SPACER_, height:10,colSpan:2},
									{ref:ZaDomain.A_zimbraAuthLdapStartTlsEnabled, type:_CHECKBOX_, label:ZaMsg.Domain_AuthLdapStartTlsEnabled, onChange: ZaTaskAuthConfigWizard.startTlsEnabledChanged,
										 trueValue:"TRUE", falseValue:"FALSE"//,labelLocation:_RIGHT_, subLabel:"", align:_RIGHT_
									},
                                    {type:_SPACER_, height:10,colSpan:2},
									{ref:ZaDomain.A_AuthLdapSearchFilter, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_AuthLdapFilter,
                                        labelCssStyle:"text-align:left;",
                                        labelLocation:_LEFT_, textWrapping:"soft"},
                                    {type:_SPACER_, height:10,colSpan:2},
									{ref:ZaDomain.A_AuthLdapSearchBase, type:_TEXTAREA_, width:380, height:40, label:ZaMsg.Domain_AuthLdapSearchBase,
                                        labelCssStyle:"text-align:left;",
                                        labelLocation:_LEFT_, textWrapping:"soft"},
                                    {type:_SPACER_, height:20,colSpan:2},
									{type:_OUTPUT_, value:ZaMsg.NAD_DomainsAuthStr, colSpan:2}
								]
							}
						]
					},
					{type:_CASE_, numCols:2, colSizes:["150px","430px"], caseKey:ZaTaskAuthConfigWizard.AUTH_CONFIG_BIND_PWD_STEP,
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
					{type:_CASE_, numCols:2,colSizes:["150px","430px"],	caseKey:ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP,
						visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaNewDomainXWizard.isAuthMechNotZimbra],
						items: [
                            {type:_OUTPUT_, value:"<b>Active Diectory Setting</b>",  colSpan:"*"
                                //colSpan:"*", cssStyle:"padding-left:10px;padding-top:10px;"
                            },
							{type:_SPACER_, height:10},
							{ref:ZaDomain.A_AuthMech, type:_OUTPUT_, label:ZaMsg.Domain_AuthMech, choices:this.AuthMechs, alignment:_LEFT_,
                                labelCssStyle:"text-align:left;padding-left:20px;"},
							{type:_GROUP_, useParentTable:true,
								visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthMech,ZaDomain.AuthMech_ad]],
								visibilityChangeEventSources:[ZaDomain.A_AuthMech],
								items:[
									{ref:ZaDomain.A_AuthADDomainName, type:_OUTPUT_, label:ZaMsg.Domain_AuthADDomainName, labelLocation:_LEFT_,
                                        labelCssStyle:"text-align:left;padding-left:20px;"},
									{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,showAddButton:false,
                                        showRemoveButton:false, labelCssStyle:"text-align:left;padding-left:20px;",
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
									{ref:ZaDomain.A_AuthLdapURL, type:_REPEAT_, label:ZaMsg.Domain_AuthLdapURL, labelLocation:_LEFT_,
                                        labelCssStyle:"text-align:left;padding-left:20px;",showAddButton:false, showRemoveButton:false,visibilityChecks:[],
										items:[
											{type:_OUTPUT_, ref:".", label:null,labelLocation:_NONE_,visibilityChecks:[]}
										]

									},
									{ref:ZaDomain.A_zimbraAuthLdapStartTlsEnabled, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapStartTlsEnabled, labelLocation:_LEFT_,
                                        labelCssStyle:"text-align:left;padding-left:20px;",choices:ZaModel.BOOLEAN_CHOICES,visibilityChecks:[]},
									{ref:ZaDomain.A_AuthLdapSearchFilter, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapFilter, labelLocation:_LEFT_,
                                        labelCssStyle:"text-align:left;padding-left:20px;",visibilityChecks:[]},
									{ref:ZaDomain.A_AuthLdapSearchBase, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapSearchBase, labelLocation:_LEFT_,
                                        labelCssStyle:"text-align:left;padding-left:20px;",visibilityChecks:[]},
									{ref:ZaDomain.A_AuthUseBindPassword, type:_OUTPUT_, label:ZaMsg.Domain_AuthUseBindPassword, labelLocation:_LEFT_,
                                        labelCssStyle:"text-align:left;padding-left:20px;",choices:ZaModel.BOOLEAN_CHOICES},
									{ref:ZaDomain.A_AuthLdapSearchBindDn, type:_OUTPUT_, label:ZaMsg.Domain_AuthLdapBindDn, labelLocation:_LEFT_,
                                        labelCssStyle:"text-align:left;padding-left:20px;",
										visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A_AuthUseBindPassword,"TRUE"]],
										visibilityChangeEventSources:[ZaDomain.A_AuthUseBindPassword]
									}
								]
							},
							{type:_SPACER_, height:20},
							{type:_OUTPUT_,value:ZaMsg.Domain_AuthProvideLoginPwd, align:_LEFT_, colSpan:"*",visibilityChecks:[]},
                            {type:_SPACER_, height:10},
							{type:_TEXTFIELD_, label:ZaMsg.LBL_Domain_AuthTestUserName, ref:ZaDomain.A_AuthTestUserName, alignment:_LEFT_,
                                labelCssStyle:"text-align:left;padding-left:20px;",visibilityChecks:[],enableDisableChecks:[]},
							{type:_SECRET_, label:ZaMsg.LBL_Domain_AuthTestPassword, ref:ZaDomain.A_AuthTestPassword, alignment:_LEFT_,
                                labelCssStyle:"text-align:left;padding-left:20px;",visibilityChecks:[],enableDisableChecks:[]},
                            {type:_GROUP_, numCols:3, colSpan:"*", colSizes:["220px","100px","auto"],
                                cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;margin-left:10px;margin-right:10px;",
                                items: [
                                    {type:_CELLSPACER_},
                                    {type:_DWT_BUTTON_, label:ZaMsg.Domain_AuthTestSettings,width:"100px",
                                        //enableDisableChecks:[[ZaItem.hasWritePermission,ZaAccount.A_zimbraForeignPrincipal]],
                                        onActivate:"ZaTaskAuthConfigWizard.autoTestSetting.call(this);"
                                    }
                                ]
                            }
						]
					},
					{type:_CASE_, caseKey:ZaTaskAuthConfigWizard.AUTH_TEST_STEP,numCols:1,colSizes:["100%"],
						visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaNewDomainXWizard.isAuthMechNotZimbra],
						visibilityChangeEventSources:[ZaModel.currentStep],
						items: [
							{type:_DWT_ALERT_,content:ZaMsg.Domain_AuthTestingInProgress,style:DwtAlert.WARNING}
						]
					},
					{type:_CASE_, numCols:2,colSizes:["150px","430px"], caseKey:ZaTaskAuthConfigWizard.AUTH_TEST_RESULT_STEP,
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
					{type:_CASE_, caseKey:ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP,colSpan:"*", numCols:1,
						items: [
                            {type:_OUTPUT_, value:"<b>SPNEGO Global Setting</b>",  colSpan:"*"
                                //colSpan:"*", cssStyle:"padding-left:10px;padding-top:10px;"
                            },
                            {type:_OUTPUT_, value:"Global setting for SPNEGO much be configured prior to using this feature.",  colSpan:"*",
                                cssStyle:"padding-left:20px;padding-top:10px;"
                            },
                            {type:_SPACER_, height:10, colSpan:"*"},
                            {type:_OUTPUT_, value:"Global SPNEGO Setting is empty",
                                cssStyle:"padding-left:20px;padding-top:10px;"
                            },
                            {type:_OUTPUT_, label: null, value: ZaMsg.SpnegoGlobalConfig,
                                onClick:ZaTaskAuthConfigWizard.changeSpnegoGlobalConfig,
                                containerCssStyle:"color:blue;cursor:pointer;padding-left:20px;padding-top:10px;"
                            },
                            {type:_SPACER_, height:30, colSpan:"*"},
                            {type:_OUTPUT_, value:"<b>SPNEGO Server Setting</b>",  colSpan:"*"
                                //colSpan:"*", cssStyle:"padding-left:10px;padding-top:10px;"
                            },
                            {type:_OUTPUT_, value:"Server setting for SPNEGO much be configured prior to using this feature.",  colSpan:"*",
                                cssStyle:"padding-left:20px;padding-top:10px;"
                            },
                            {type:_SPACER_, height:10, colSpan:"*"},
                            {type:_OUTPUT_, value:"Server",
                                cssStyle:"padding-left:20px;padding-top:10px;"
                            },
                            {type:_OUTPUT_, label: null, value: ZaMsg.SpnegoServerConfig,
                                onClick:ZaTaskAuthConfigWizard.changeSpnegoServerConfig,
                                containerCssStyle:"color:blue;cursor:pointer;padding-left:20px;padding-top:10px;"
                            }
/*
                            ,
                            {type:_SPACER_, height:20, colSpan:"*"},
                            {type:_OUTPUT_, value:"<b>SPNEGO Domain Setting</b>",  colSpan:"*"
                                //colSpan:"*", cssStyle:"padding-left:10px;padding-top:10px;"
                            },
                            {type:_OUTPUT_, value:"Server setting for SPNEGO much be configured prior to using this feature.",  colSpan:"*",
                                cssStyle:"padding-left:20px;padding-top:10px;"
                            },
                            {type:_SPACER_, height:10, colSpan:"*"},
                            {type:_OUTPUT_, value:"Domain",
                                cssStyle:"padding-left:20px;padding-top:10px;"
                            },
                            {type:_OUTPUT_, label: null, value: ZaMsg.SpnegoDomainConfig,
                                onClick:ZaTaskAuthConfigWizard.changeSpnegoDomainConfig,
                                containerCssStyle:"color:blue;cursor:pointer;padding-left:20px;padding-top:10px;"
                            }
*/
						]
					},
					{type:_CASE_, caseKey:ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_1,
						items: [
							{type:_GROUP_, colSpan:2,numCols:2,colSizes:["150px","430px"],
								visibilityChecks:[[ZaTaskAuthConfigWizard.checkSpnegoApplyType,ZaDomain.A2_zimbraSpnegoApplyFor,ZaItem.GLOBAL_CONFIG]],
								visibilityChangeEventSources:[ZaDomain.A2_zimbraSpnegoApplyFor],
								items:[
                                        {type:_OUTPUT_, value:"Single Sign-On using SPNEGO", colSpan:2},
                                        {ref: ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled, type: _CHECKBOX_,
                                            label:ZaMsg.EnableSpnegoGlobal, width: "200px", subLabel:"",
                                            labelLocation:_RIGHT_, align:_RIGHT_,
                                            //labelCssStyle:"text-align:left;padding-left:20px;",
                                            trueValue: "TRUE", falseValue: "FALSE"
                                        },
                                        {ref: ZaDomain.A2_zimbraSpnegoAuthRealm,
                                            type: _TEXTFIELD_, width: "200px",
                                            label: ZaMsg.LBL_zimbraSpnegoAuthRealm, labelCssStyle:"text-align:left;padding-left:20px;",
                                            enableDisableChangeEventSources:[ZaDomain.A2_zimbraSpnegoAuthEnabled],
                                            enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_zimbraSpnegoAuthEnabled,'TRUE']]
                                        },
                                        {ref: ZaDomain.A2_zimbraSpnegoAuthErrorURL,
                                            type: _TEXTFIELD_, width: "200px",
                                            label: ZaMsg.LBL_zimbraSpnegoAuthErrorURL, labelCssStyle:"text-align:left;padding-left:20px;",
                                            enableDisableChangeEventSources:[ZaDomain.A2_zimbraSpnegoAuthEnabled],
                                            enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_zimbraSpnegoAuthEnabled,'TRUE']]
                                        }
                                ]
                            },
							{type:_GROUP_, colSpan:2,numCols:2,colSizes:["150px","430px"],
								visibilityChecks:[[ZaTaskAuthConfigWizard.checkSpnegoApplyType,ZaDomain.A2_zimbraSpnegoApplyFor,ZaItem.SERVER]],
								visibilityChangeEventSources:[ZaDomain.A2_zimbraSpnegoApplyFor],
								items:[
                                        {type:_OUTPUT_, value:"Server Configuration", colSpan:2},
                                        {ref:ZaDomain.A2_zimbraSpnegoAuthPrincipal, type:_TEXTFIELD_,
                                            labelCssStyle:"text-align:left;padding-left:20px;",
                                            label:ZaMsg.NAD_MTA_SpnegoAuthPrincipal, width: "20em",
                                            onChange: ZaServerXFormView.onFormFieldChanged
                                        },
                                        {ref:ZaDomain.A2_zimbraSpnegoAuthTargetName, type:_TEXTFIELD_,
                                            labelCssStyle:"text-align:left;padding-left:20px;",
                                            label:ZaMsg.NAD_MTA_SpnegoAuthTargetName, width: "20em",
                                            onChange: ZaServerXFormView.onFormFieldChanged
                                        }
                                ]
                            },
							{type:_GROUP_, colSpan:2,numCols:2,colSizes:["150px","430px"],
								visibilityChecks:[[ZaTaskAuthConfigWizard.checkSpnegoApplyType,ZaDomain.A2_zimbraSpnegoApplyFor,ZaItem.DOMAIN]],
								visibilityChangeEventSources:[ZaDomain.A2_zimbraSpnegoApplyFor],
								items:[
                                        {type:_OUTPUT_, value:"Domain Configuration"}
                                ]
                            }
						]
					},
					{type:_CASE_, caseKey:ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_2,
						items: [
							{type:_OUTPUT_, value:"Domain Setting Here"}
						]
					},
					{type:_CASE_, caseKey:ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP,
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_Auth_Config_Complete}
						]
					}
				]
			}
		];
}
ZaXDialog.XFormModifiers["ZaTaskAuthConfigWizard"].push(ZaTaskAuthConfigWizard.myXFormModifier);

ZaTaskAuthConfigWizard.startTlsEnabledChanged =  function (value, event, form) {
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

ZaTaskAuthConfigWizard.autoTestSetting = function() {
    var parent = this.getForm().parent;
    //parent.goNext();
    parent.testSetings();
    parent.goPage(ZaTaskAuthConfigWizard.AUTH_TEST_STEP);
}

ZaTaskAuthConfigWizard.checkSpnegoApplyType = function(refPath,val) {
    var instance = this.getInstance();
    return (instance[refPath] == val);

}

ZaTaskAuthConfigWizard.changeSpnegoGlobalConfig = function() {
    var instance = this.getInstance();
    instance[ZaDomain.A2_zimbraSpnegoApplyFor] = ZaItem.GLOBAL_CONFIG;
    var parent = this.getForm().parent;
    parent.goPage(ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_1);
}

ZaTaskAuthConfigWizard.changeSpnegoServerConfig = function() {
    var instance = this.getInstance();
    instance[ZaDomain.A2_zimbraSpnegoApplyFor] = ZaItem.SERVER;
    var parent = this.getForm().parent;
    parent.goPage(ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_1);
}

ZaTaskAuthConfigWizard.changeSpnegoDomainConfig = function() {
    var instance = this.getInstance();
    instance[ZaDomain.A2_zimbraSpnegoApplyFor] = ZaItem.DOMAIN;
    var parent = this.getForm().parent;
    parent.goPage(ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_1);
}