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
    ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_2 = ++this.TAB_INDEX;
    ZaTaskAuthConfigWizard.EXTERNAL_LDAP_GROUP_STEP = ++this.TAB_INDEX;
	ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP = ++this.TAB_INDEX;

	this.stepChoices = [
        {label:ZaMsg.stepAuthModeForDomain, value:ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_0},
		{label:ZaMsg.stepAuthSetting, value:ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_1},
		{label:ZaMsg.stepAuthBindLDAP, value:ZaTaskAuthConfigWizard.AUTH_CONFIG_BIND_PWD_STEP},
		{label:ZaMsg.stepAuthSummary, value:ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP},
		{label:ZaMsg.TestAuthSettings, value:ZaTaskAuthConfigWizard.AUTH_TEST_STEP},
		{label:ZaMsg.AuthTestResult, value:ZaTaskAuthConfigWizard.AUTH_TEST_RESULT_STEP},
        {label:ZaMsg.AuthSetting_Spnego, value:ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP},
        {label:ZaMsg.AuthSetting_SpnegoDomain, value:ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_2},
        {label:ZaMsg.NAD_ExternalGroup_Setting, value:ZaTaskAuthConfigWizard.EXTERNAL_LDAP_GROUP_STEP},
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
ZaTaskAuthConfigWizard.prototype.cacheDialog = false;
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
    ZaDomain.A2_zimbraSpnegoUAAllBrowsers = "zimbraSpnegoUA_AllBrowsers";
    ZaDomain.A2_zimbraSpnegoUASupportedBrowsers = "zimbraSpnegoUA_SupportedBrowsers";
    ZaDomain.A2_zimbraSpnegoUACustomBrowsers = "zimbraSpnegoUA_CustomBrowsers";
    ZaDomain.A2_zimbraWebClientURLAllowedUA = "zimbraWebClientURLAllowedUA";
    ZaDomain.A2_zimbraSpnegoTargetServer = "zimbraSpnegoTargetServer";
    ZaDomain.A2_zimbraSpnegoGlobalSettingStatus = "zimbraSpnegoGlobalSettingStatus";

    ZaDomain.A_zimbraExternalGroupLdapSearchBase = "zimbraExternalGroupLdapSearchBase";
    ZaDomain.A_zimbraExternalGroupLdapSearchFilter = "zimbraExternalGroupLdapSearchFilter";
    ZaDomain.A_zimbraExternalGroupHandlerClass = "zimbraExternalGroupHandlerClass";
    ZaDomain.A_zimbraAuthMechAdmin = "zimbraAuthMechAdmin";
    ZaDomain.A2_zimbraExternalGroupLdapEnabled = "zimbraExternalGroupLdapEnabled";
    if(ZaDomain.myXModel) {
        ZaDomain.myXModel.items.push(
            {id:ZaDomain.A2_zimbraSpnegoApplyFor, ref:ZaDomain.A2_zimbraSpnegoApplyFor, type: _STRING_},
            {id:ZaDomain.A2_zimbraSpnegoGlobalSettingStatus, ref:ZaDomain.A2_zimbraSpnegoGlobalSettingStatus, type: _STRING_},
            {id:ZaDomain.A2_zimbraWebClientURLAllowedUA, ref:ZaDomain.A2_zimbraWebClientURLAllowedUA, type: _STRING_},
            {id:ZaDomain.A2_zimbraSpnegoTargetServer, type:_STRING_ , ref: ZaDomain.A2_zimbraSpnegoTargetServer},
            {id:ZaDomain.A2_zimbraSpnegoAuthPrincipal, ref:ZaDomain.A2_zimbraSpnegoAuthPrincipal, type: _STRING_},
            {id:ZaDomain.A2_zimbraSpnegoAuthRealm, ref: ZaDomain.A2_zimbraSpnegoAuthRealm, type: _STRING_ },
            {id:ZaDomain.A2_zimbraSpnegoAuthTargetName, ref: ZaDomain.A2_zimbraSpnegoAuthTargetName, type: _STRING_ },
            {id:ZaDomain.A2_zimbraSpnegoAuthErrorURL, ref: ZaDomain.A2_zimbraSpnegoAuthErrorURL, type: _STRING_ },
            {id:ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled, ref:ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
            {id:ZaDomain.A2_zimbraSpnegoAuthEnabled, ref:ZaDomain.A2_zimbraSpnegoAuthEnabled, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
            {id:ZaDomain.A2_zimbraSpnegoAuthSummary, ref:ZaDomain.A2_zimbraSpnegoAuthSummary, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
            {id:ZaDomain.A2_zimbraSpnegoUAAllBrowsers, ref:ZaDomain.A2_zimbraSpnegoUAAllBrowsers, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
            {id:ZaDomain.A2_zimbraSpnegoUASupportedBrowsers, ref:ZaDomain.A2_zimbraSpnegoUASupportedBrowsers, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES},
            {id:ZaDomain.A2_zimbraSpnegoUACustomBrowsers, ref:ZaDomain.A2_zimbraSpnegoUACustomBrowsers, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES}
        );
        ZaDomain.myXModel.items.push(
            {id:ZaDomain.A2_zimbraExternalGroupLdapEnabled, ref: ZaDomain.A2_zimbraExternalGroupLdapEnabled, type: _ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
            {id:ZaDomain.A_zimbraExternalGroupLdapSearchBase, type:_STRING_, ref:"attrs/" + ZaDomain.A_zimbraExternalGroupLdapSearchBase},
            {id:ZaDomain.A_zimbraExternalGroupLdapSearchFilter, type:_STRING_, ref:"attrs/" + ZaDomain.A_zimbraExternalGroupLdapSearchFilter},
            {id:ZaDomain.A_zimbraExternalGroupHandlerClass, type:_STRING_, ref:"attrs/" + ZaDomain.A_zimbraExternalGroupHandlerClass},
            {id:ZaDomain.A_zimbraAuthMechAdmin, type:_STRING_, ref:"attrs/" + ZaDomain.A_zimbraAuthMechAdmin}
        );
    }
    ZaTaskAuthConfigWizard.loadExtLdapGroup = function (entry) {
        if (!this.attrs[ZaDomain.A_zimbraExternalGroupHandlerClass]) {
            this.attrs[ZaDomain.A_zimbraExternalGroupHandlerClass] = "com.zimbra.cs.account.grouperhandle.ADGroupHandler";
        }
        if (!this.attrs[ZaDomain.A_zimbraAuthMechAdmin]) {
            this.attrs[ZaDomain.A_zimbraAuthMechAdmin] = ZaDomain.AuthMech_ad;
        }

        if (this.attrs[ZaDomain.A_AuthMech]!=ZaDomain.AuthMech_zimbra ) {
            this[ZaDomain.A2_zimbraExternalGroupLdapEnabled] = "TRUE";
        } else {
            this[ZaDomain.A2_zimbraExternalGroupLdapEnabled] = "FALSE";
        }
    }
    ZaItem.loadMethods["ZaDomain"].push(ZaTaskAuthConfigWizard.loadExtLdapGroup);
}

ZaDomain.TARGET_SERVER_CHOICES = [];

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
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
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
        if (this._containedObject.attrs[ZaDomain.A_AuthMech] == ZaDomain.AuthMech_ad)
            this.goPage(ZaTaskAuthConfigWizard.EXTERNAL_LDAP_GROUP_STEP);
        else
		    this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP);
    } else if (this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP) {
        if (this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_zimbra) {
            this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_0);
        } else if (this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ldap) {
            this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP);
        } else if (this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad) {
            if (this._containedObject[ZaDomain.A2_zimbraSpnegoAuthEnabled]!="TRUE") {
                if (this._containedObject[ZaDomain.A2_zimbraExternalGroupLdapEnabled]=="TRUE") {
                    this.goPage(ZaTaskAuthConfigWizard.EXTERNAL_LDAP_GROUP_STEP);
                } else {
                    this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP);
                }
            } else {
                if (this._containedObject[ZaDomain.A2_zimbraExternalGroupLdapEnabled]=="TRUE") {
                    this.goPage(ZaTaskAuthConfigWizard.EXTERNAL_LDAP_GROUP_STEP);
                } else {
                    this.goPage(ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_2);
                }
            }
        }
    } else if(this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_2) {
        this.goPage(ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP);
	} else if(this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP) {
		this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_SUMMARY_STEP);
	} else if (this._containedObject[ZaModel.currentStep] == ZaTaskAuthConfigWizard.EXTERNAL_LDAP_GROUP_STEP) {
        if (this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad && this._containedObject[ZaDomain.A2_zimbraSpnegoAuthEnabled]=="TRUE")
            this.goPage(ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_2);
        else
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
        else if(this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad)
            this.goPage(ZaTaskAuthConfigWizard.EXTERNAL_LDAP_GROUP_STEP);
        else
            this.goPage(ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP);
	} else if (this._containedObject[ZaModel.currentStep]==ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_1 && this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad) {
		if(!this._containedObject.attrs[ZaDomain.A_AuthLdapURL]) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_LDAP_URL_REQUIRED);
			return false;
		}
		this.goPage(ZaTaskAuthConfigWizard.AUTH_CONFIG_BIND_PWD_STEP);
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
        if (this._containedObject.attrs[ZaDomain.A_AuthMech]==ZaDomain.AuthMech_ad)
		    this.goPage(ZaTaskAuthConfigWizard.EXTERNAL_LDAP_GROUP_STEP);
        else
		    this.goPage(ZaTaskAuthConfigWizard.CONFIG_COMPLETE_STEP);
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

    if(entry[ZaDomain.A2_zimbraExternalGroupLdapEnabled])
        this._containedObject[ZaDomain.A2_zimbraExternalGroupLdapEnabled]= entry[ZaDomain.A2_zimbraExternalGroupLdapEnabled];

	this._containedObject[ZaDomain.A_AuthUseBindPassword] = entry[ZaDomain.A_AuthUseBindPassword];
	this.setTitle(ZaMsg.NCD_AuthConfigTitle + " (" + entry.name + ")");

    if (ZaSettings.isDomainAdmin && (entry.attrs[ZaDomain.A_zimbraAdminConsoleLDAPAuthEnabled] == "TRUE")
           && entry.attrs[ZaDomain.A_zimbraAuthLdapStartTlsEnabled] != "TRUE") {
        this._containedObject [ZaDomain.A2_allowClearTextLDAPAuth] = "FALSE" ;
    }

    var serverList = ZaApp.getInstance().getServerList().getArray();
    for(var ix = 0; ix < serverList.length; ix++) {
        ZaDomain.TARGET_SERVER_CHOICES.push (
            {label: serverList[ix].name, value: serverList[ix].id }
        );
    }
    if(serverList.length > 0)
        this._containedObject [ZaDomain.A2_zimbraSpnegoTargetServer] = serverList[0].id;
    if(this._containedObject.attrs[ZaDomain.A_zimbraWebClientLoginURLAllowedUA]
            && this._containedObject.attrs[ZaDomain.A_zimbraWebClientLogoutURLAllowedUA]
            && (this._containedObject.attrs[ZaDomain.A_zimbraWebClientLoginURLAllowedUA].join(";")
                == this._containedObject.attrs[ZaDomain.A_zimbraWebClientLogoutURLAllowedUA].join(";"))
            && (this._containedObject.attrs[ZaDomain.A_zimbraWebClientLoginURLAllowedUA].join(";")
                == ZaDomain.SPNEGO_SUPPORT_UA.join(";"))
     ) {
        this._containedObject[ZaDomain.A2_zimbraSpnegoUASupportedBrowsers] = "TRUE";
    } else this._containedObject[ZaDomain.A2_zimbraSpnegoUACustomBrowsers] = "TRUE";

    if(this._containedObject.attrs[ZaDomain.A_zimbraWebClientLoginURL]
            || this._containedObject.attrs[ZaDomain.A_zimbraWebClientLogoutURL]
            || (this._containedObject.attrs[ZaDomain.A_zimbraWebClientLoginURLAllowedUA]
            && this._containedObject.attrs[ZaDomain.A_zimbraWebClientLoginURLAllowedUA].length > 0))
       this._containedObject[ZaDomain.A2_zimbraSpnegoAuthEnabled] = "TRUE";

    var globalConfig = ZaApp.getInstance().getGlobalConfig();
    this._containedObject[ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled] = globalConfig.attrs[ZaGlobalConfig.A_zimbraSpnegoAuthEnabled];
    this._containedObject[ZaDomain.A2_zimbraSpnegoAuthErrorURL] = globalConfig.attrs[ZaGlobalConfig.A_zimbraSpnegoAuthErrorURL];
    this._containedObject[ZaDomain.A2_zimbraSpnegoAuthRealm] = globalConfig.attrs[ZaGlobalConfig.A_zimbraSpnegoAuthRealm];
    this._containedObject[ZaDomain.A2_zimbraSpnegoGlobalSettingStatus] = ZaTaskAuthConfigWizard.getGlobalSettingMsg(this._containedObject);

    this._containedObject[ZaModel.currentStep] = entry[ZaModel.currentStep] || ZaTaskAuthConfigWizard.AUTH_CONFIG_STEP_0;
    this._containedObject[ZaDomain.A2_zimbraSpnegoUAAllBrowsers] = entry[ZaDomain.A2_zimbraSpnegoUAAllBrowsers] || "FALSE";
    this._containedObject[ZaDomain.A2_zimbraSpnegoUASupportedBrowsers] = entry[ZaDomain.A2_zimbraSpnegoUASupportedBrowsers] || "FALSE";
    this._containedObject[ZaDomain.A2_zimbraSpnegoUACustomBrowsers] = entry[ZaDomain.A2_zimbraSpnegoUACustomBrowsers] || "FALSE";

    this._containedObject._uuid = entry._extid || entry._uuid;
    this._containedObject._editObject = entry._editObject;
	this._localXForm.setInstance(this._containedObject);
}



ZaTaskAuthConfigWizard.prototype.finishWizard =
function() {
	try {
		ZaDomain.modifyAuthSettings.call(this._containedObject._editObject,this._containedObject);
		ZaApp.getInstance().getDomainListController()._fireDomainChangeEvent(this._containedObject._editObject);
		this.popdown();
        ZaApp.getInstance().getDomainListController().notifyAllOpenTabs(this._containedObject._editObject);
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaTaskAuthConfigWizard.prototype.finishWizard", null, false);
	}
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
                            {type:_OUTPUT_, value:ZaMsg.authForDomainMsg,
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
                                    this.setInstanceValue("FALSE", ZaDomain.A2_zimbraExternalGroupLdapEnabled);
								},
								visibilityChecks:[],enableDisableChecks:[]
							},
                            {type: _GROUP_, colSpan:2, numCols:2, colSizes: ["80px", "*" ],
                                items :[
                                    {type:_OUTPUT_, label:"" , value:ZaMsg.domainAuthDlgInternalHelp}
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
                                    this.setInstanceValue("TRUE", ZaDomain.A2_zimbraExternalGroupLdapEnabled);
								},
								visibilityChecks:[],enableDisableChecks:[]
							},
                            {type: _GROUP_, colSpan:2, numCols:2, colSizes: ["80px", "*" ],
                                items :[
                                    {type:_OUTPUT_, label:"" , value:ZaMsg.domainAuthDlgADHelp},
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
                                    this.setInstanceValue("TRUE", ZaDomain.A2_zimbraExternalGroupLdapEnabled);
								},
								visibilityChecks:[],enableDisableChecks:[]
							},
                            {type: _GROUP_, colSpan:2, numCols:2, colSizes: ["80px", "*" ],
                                items :[
                                    {type:_OUTPUT_, label:"" , value:ZaMsg.domainAuthDlgExternalHelp}
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
                                    {type:_OUTPUT_, value:ZaMsg.authForADMsg,  colSpan:"*"
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
                                    {type:_OUTPUT_, value:ZaMsg.authForLDAPMsg,  colSpan:"*"
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
										 trueValue:"TRUE", falseValue:"FALSE"
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
						visibilityChecks:[[Case_XFormItem.prototype.isCurrentTab]],
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
                            {type:_OUTPUT_, value:ZaMsg.authSummaryMsg,  colSpan:"*"
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
                            {type:_OUTPUT_, value:ZaMsg.spnegoGlobalSettingTitle,  colSpan:"*"
                            },
                            {type:_OUTPUT_, value:ZaMsg.spnegoGlobalSettingMsg,  colSpan:"*",
                                cssStyle:"padding-left:20px;padding-top:10px;"
                            },
                            {type:_SPACER_, height:10, colSpan:"*"},
                            {type:_OUTPUT_, ref: ZaDomain.A2_zimbraSpnegoGlobalSettingStatus, bmolsnr:true,
                                cssStyle:"padding-left:20px;padding-top:10px;"
                            },
                            {type:_OUTPUT_, label: null, value: ZaMsg.SpnegoGlobalConfig,
                                onClick:ZaTaskAuthConfigWizard.changeSpnegoGlobalConfig,
                                containerCssStyle:"color:blue;cursor:pointer;padding-left:20px;padding-top:10px;"
                            },
                            {type:_SPACER_, height:30, colSpan:"*"},
                            {type:_OUTPUT_, value:ZaMsg.authForSpnegoSettingMsg,  colSpan:"*"
                                //colSpan:"*", cssStyle:"padding-left:10px;padding-top:10px;"
                            },
                            {type:_OUTPUT_, value:ZaMsg.spnegoServerSettingMsg,  colSpan:"*",
                                cssStyle:"padding-left:20px;padding-top:10px;"
                            },
                            {type:_SPACER_, height:10, colSpan:"*"},
                            {type:_GROUP_, numCols:2, colSpan:"*", colSizes:["80px","auto"],items:[
                                {ref:ZaDomain.A2_zimbraSpnegoTargetServer, type:_OSELECT1_,
                                    label:ZaMsg.spnegoTagetServer,
                                    labelLocation:_LEFT_,
                                    labelCssStyle:"text-align:left;padding-left:20px;",
                                    choices:ZaDomain.TARGET_SERVER_CHOICES,
                                    visibilityChecks:[],
                                    enableDisableChecks:[],
                                    editable: true
                                }
                            ]},
                            {type:_OUTPUT_, label: null, value: ZaMsg.SpnegoServerConfig,
                                onClick:ZaTaskAuthConfigWizard.changeSpnegoServerConfig,
                                containerCssStyle:"color:blue;cursor:pointer;padding-left:20px;padding-top:10px;"
                            }
						]
					},
					{type:_CASE_, caseKey:ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_2, colSizes:["200px", "*"],
						items: [
							{type:_OUTPUT_, value:ZaMsg.spnegoSettingSSOMsg, colSpan:2},
                            {type:_SPACER_, height:10, colSpan:"*"},
                            {ref:ZaDomain.A_zimbraVirtualHostname, type:_REPEAT_,
                                label:ZaMsg.Domain_Tab_VirtualHost, repeatInstance:"", showAddButton:true,
                                labelCssStyle:"text-align:left;vertical-align:top;padding-left:20px;",
                                showRemoveButton:true,
                                addButtonLabel:ZaMsg.NAD_AddVirtualHost,
                                showAddOnNextRow:true,
                                removeButtonLabel:ZaMsg.NAD_RemoveVirtualHost,
                                items: [
                                    {ref:".", type:_TEXTFIELD_, label:null, width:"150px",
                                        enableDisableChecks:[[ZaItem.hasWritePermission,ZaDomain.A_zimbraVirtualHostname]],
                                        visibilityChecks:[[ZaItem.hasReadPermission,ZaDomain.A_zimbraVirtualHostname]],
                                        onChange:ZaDomainXFormView.onFormFieldChanged}
                                ]
                            },
                            {type:_OUTPUT_, value:ZaMsg.spnegoVirtualHostMsg,
                                width:250, colSpan:"*",cssStyle:"padding-left:200px;"
                            },
                            {type:_SPACER_, height:15, colSpan:"*"},
                            {ref: ZaDomain.A_zimbraWebClientLoginURL,useParentTable: false,
                                colSizes:["275px","*"], colSpan: 2,
                                type:_TEXTFIELD_, width: "150px",
                                labelCssStyle:"text-align:left;padding-left:20px;",
                                msgName: ZaMsg.LBL_zimbraWebClientLoginURL,
                                label: ZaMsg.LBL_zimbraWebClientLoginURL,
                                onChange:ZaDomainXFormView.onFormFieldChanged
                            },
                            {type:_OUTPUT_, value:ZaMsg.spnegoWebClientLoginMsg,
                                width:250, colSpan:"*",cssStyle:"padding-left:200px;"
                            },
                            {ref: ZaDomain.A_zimbraWebClientLogoutURL,useParentTable: false,
                                colSizes:["275px","*"], colSpan: 2,
                                type:_TEXTFIELD_, width:"150px",
                                labelCssStyle:"text-align:left;padding-left:20px;",
                                msgName: ZaMsg.LBL_zimbraWebClientLogoutURL,
                                label: ZaMsg.LBL_zimbraWebClientLogoutURL,
                                onChange:ZaDomainXFormView.onFormFieldChanged
                            },
                            {type:_OUTPUT_, value:ZaMsg.spnegoWebClientLogoutMsg,
                                width:250, colSpan:"*",cssStyle:"padding-left:200px;"
                            },
                            {type:_SPACER_, height:15, colSpan:"*"},
                            {type:_OUTPUT_, value:ZaMsg.spnegoAllowedUASetting, colSpan:2, cssStyle:"padding-left:20px;"},
                            {type:_SPACER_, height:10, colSpan:"*"},
                            {type: _GROUP_,  id:"spnego_user_agent_settings",
                                numCols: 2, colSpan:2, colSizes:["200px", "*"],
                                items: [
                                    {ref:ZaDomain.A2_zimbraSpnegoUAAllBrowsers, type:_RADIO_, groupname:"user_agent_setting",
										msgName:ZaMsg.SpnegoSettingAllBrowsers,label:ZaMsg.SpnegoSettingAllBrowsers, labelLocation:_RIGHT_,
                                        labelCssClass:"xform_label_right",
										visibilityChecks:[],
										updateElement:function () {
                                            if(ZaTaskAuthConfigWizard.checkRadioEnableDisable(this.getInstance(),ZaDomain.A2_zimbraSpnegoUAAllBrowsers))
											    this.getElement().checked = "TRUE";
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaDomain.A2_zimbraSpnegoUACustomBrowsers);
											this.setInstanceValue("FALSE",ZaDomain.A2_zimbraSpnegoUASupportedBrowsers);
                                            this.setInstanceValue("TRUE",ZaDomain.A2_zimbraSpnegoUAAllBrowsers);

										}
									},
                                    {ref:ZaDomain.A2_zimbraSpnegoUASupportedBrowsers, type:_RADIO_, groupname:"user_agent_setting",
										msgName:ZaMsg.SpnegoSettingSupportedBrowsers,label:ZaMsg.SpnegoSettingSupportedBrowsers, labelLocation:_RIGHT_,
                                        labelCssClass:"xform_label_right",
										visibilityChecks:[],
										updateElement:function () {
                                            if(ZaTaskAuthConfigWizard.checkRadioEnableDisable(this.getInstance(),ZaDomain.A2_zimbraSpnegoUASupportedBrowsers))
											    this.getElement().checked = "TRUE";
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaDomain.A2_zimbraSpnegoUACustomBrowsers);
											this.setInstanceValue("FALSE",ZaDomain.A2_zimbraSpnegoUAAllBrowsers);
                                            this.setInstanceValue("TRUE",ZaDomain.A2_zimbraSpnegoUASupportedBrowsers);
										}
									},
                                    {ref:ZaDomain.A2_zimbraSpnegoUACustomBrowsers, type:_RADIO_, groupname:"user_agent_setting",
										msgName:ZaMsg.SpnegoSettingCustomBrowsers,label:ZaMsg.SpnegoSettingCustomBrowsers, labelLocation:_RIGHT_,
                                        labelCssClass:"xform_label_right",
										updateElement:function () {
                                            if(ZaTaskAuthConfigWizard.checkRadioEnableDisable(this.getInstance(),ZaDomain.A2_zimbraSpnegoUACustomBrowsers))
											    this.getElement().checked = "TRUE";
										},
										visibilityChecks:[],
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaDomain.A2_zimbraSpnegoUAAllBrowsers);
											this.setInstanceValue("FALSE",ZaDomain.A2_zimbraSpnegoUASupportedBrowsers);
                                            this.setInstanceValue("TRUE",ZaDomain.A2_zimbraSpnegoUACustomBrowsers);
										}
									},
                                    {type:_GROUP_, numCols:3, colSpan:"*", colSizes:["275px","80px","auto"],
                                        cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:0px;margin-left:10px;margin-right:10px;",
                                        items: [
                                            {type:_CELLSPACER_},
                                            {type:_DWT_BUTTON_, label:ZaMsg.spnegoAllowedUASetting_specify,width:"70px",
                                                enableDisableChangeEventSources:[ZaDomain.A2_zimbraSpnegoUACustomBrowsers],
                                                enableDisableChecks:[[XForm.checkInstanceValue,ZaDomain.A2_zimbraSpnegoUACustomBrowsers,'TRUE']],
                                                onActivate:"ZaTaskAuthConfigWizard.customAllowedUASetting.call(this);"
                                            }
                                        ]
                                    }
                                ]
							}
						]
					},
					{type:_CASE_, caseKey:ZaTaskAuthConfigWizard.EXTERNAL_LDAP_GROUP_STEP, colSizes:["200px", "*"],
						items: [
                            {type:_OUTPUT_, ref: ZaDomain.A_zimbraAuthMechAdmin, label: "Auth Mech for Admin:"},
                            {type:_TEXTAREA_, ref:ZaDomain.A_zimbraExternalGroupLdapSearchBase, label: "External Group LDAP Search Base:"},
                            {type:_TEXTAREA_, ref:ZaDomain.A_zimbraExternalGroupLdapSearchFilter, label: "External Group LDAP Search Filter:"},
                            {type:_TEXTFIELD_, ref:ZaDomain.A_zimbraExternalGroupHandlerClass, width:"100%", label :"External Group Handle Class Name:"}
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
    ZaTaskAuthConfigWizard.spnegoGlobalServerSetting(instance, ZaItem.GLOBAL_CONFIG);
}

ZaTaskAuthConfigWizard.changeSpnegoServerConfig = function() {
    var instance = this.getInstance();
    ZaTaskAuthConfigWizard.spnegoGlobalServerSetting(instance, ZaItem.SERVER);
}

ZaTaskAuthConfigWizard.changeSpnegoDomainConfig = function() {
    var instance = this.getInstance();
    instance[ZaDomain.A2_zimbraSpnegoApplyFor] = ZaItem.DOMAIN;
    var parent = this.getForm().parent;
    parent.goPage(ZaTaskAuthConfigWizard.SPNEGO_CONFIG_STEP_1);
}

ZaTaskAuthConfigWizard.checkRadioEnableDisable = function (instance, refPath) {
    if(!instance) return false;
    if(instance.attrs[ZaDomain.A_zimbraWebClientLoginURLAllowedUA]
            && instance.attrs[ZaDomain.A_zimbraWebClientLogoutURLAllowedUA]
            && (instance.attrs[ZaDomain.A_zimbraWebClientLoginURLAllowedUA].join(";")
                == instance.attrs[ZaDomain.A_zimbraWebClientLogoutURLAllowedUA].join(";"))
            && (instance.attrs[ZaDomain.A_zimbraWebClientLoginURLAllowedUA].join(";")
                == ZaDomain.SPNEGO_SUPPORT_UA.join(";"))
    ) {
        if(ZaDomain.A2_zimbraSpnegoUASupportedBrowsers == refPath)
            return true;
    } else  {
        if(ZaDomain.A2_zimbraSpnegoUACustomBrowsers == refPath)
            return true;
    }
    return false;

}

ZaTaskAuthConfigWizard.getGlobalSettingMsg = function (instance) {
    if((!instance[ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled]
            ||instance[ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled] == "FALSE")
        && !instance[ZaDomain.A2_zimbraSpnegoAuthErrorURL]
            && !instance[ZaDomain.A2_zimbraSpnegoAuthRealm])
        return ZaMsg.spnegoGlobalSettingEmpty;
    if(!instance[ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled]
            ||instance[ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled] == "FALSE")
        return ZaMsg.spnegoGlobalSettingUnenabled;
    else return ZaMsg.spnegoGlobalSettingUnempty;
}

ZaTaskAuthConfigWizard.customAllowedUASetting = function() {
    if(!ZaApp.getInstance().dialogs["spnegoUACustomDialog"]) {
        ZaApp.getInstance().dialogs["spnegoUACustomDialog"] = new ZaSpnegoUACustomDialog(
            ZaApp.getInstance().getAppCtxt().getShell(), "450px", "280px",ZaMsg.SpnegoSettingCustomDialogTitle );
        ZaApp.getInstance().dialogs["spnegoUACustomDialog"].registerCallback(
                DwtDialog.OK_BUTTON, ZaSpnegoUACustomDialog.updateSpnegoUASetting,
                this, ZaApp.getInstance().dialogs["spnegoUACustomDialog"]._localXForm );
    }
    var allowedUAs = this.getInstanceValue(ZaDomain.A_zimbraWebClientLoginURLAllowedUA);
    var obj = {};
    if(typeof allowedUAs == "string")
        obj[ZaDomain.A2_zimbraWebClientURLAllowedUA] = allowedUAs;
    else if(allowedUAs instanceof Array)
        obj[ZaDomain.A2_zimbraWebClientURLAllowedUA] = allowedUAs.join('\n');

    ZaApp.getInstance().dialogs["spnegoUACustomDialog"].setObject(obj);
    ZaApp.getInstance().dialogs["spnegoUACustomDialog"].popup();
}

ZaTaskAuthConfigWizard.spnegoGlobalServerSetting = function(obj, caller) {
    var instance = {};
    if(obj) instance = obj;
    var titleText = ZaMsg.SpnegoSettingGlobalDialogTitle;
    if(caller == ZaItem.GLOBAL_CONFIG) {
        instance[ZaDomain.A2_zimbraSpnegoApplyFor] = ZaItem.GLOBAL_CONFIG;
    } else if(caller == ZaItem.SERVER) {
        titleText = ZaMsg.SpnegoSettingServerDialogTitle;
        instance[ZaDomain.A2_zimbraSpnegoApplyFor] = ZaItem.SERVER;
        var serverList = ZaApp.getInstance().getServerList().getArray();
        var serverObj = null;
        for(var i = 0; i < serverList.length; i++) {
            if(serverList[i].id == instance[ZaDomain.A2_zimbraSpnegoTargetServer]) {
                serverObj = serverList[i];
                break;
            }
        }
        if(serverObj) {
            instance[ZaDomain.A2_zimbraSpnegoAuthPrincipal] = serverObj.attrs[ZaServer.A_zimbraSpnegoAuthPrincipal];
            instance[ZaDomain.A2_zimbraSpnegoAuthTargetName] = serverObj.attrs[ZaServer.A_zimbraSpnegoAuthTargetName];
        }
    }

    if(!ZaApp.getInstance().dialogs["updateGlobalServerDialog"]) {
        ZaApp.getInstance().dialogs["updateGlobalServerDialog"] = new ZaSpnegoGlobalServerDialog(
            ZaApp.getInstance().getAppCtxt().getShell(), "700px", "350px",titleText );
        ZaApp.getInstance().dialogs["updateGlobalServerDialog"].registerCallback(
                DwtDialog.OK_BUTTON, ZaSpnegoGlobalServerDialog.updateGlobalServerSetting,
                instance, ZaApp.getInstance().dialogs["updateGlobalServerDialog"]._localXForm );
    } else
        ZaApp.getInstance().dialogs["updateGlobalServerDialog"].setTitle(titleText);

    ZaApp.getInstance().dialogs["updateGlobalServerDialog"].setObject(instance);
    ZaApp.getInstance().dialogs["updateGlobalServerDialog"].popup();
}

ZaTaskAuthConfigWizard.clientUAChanged = function (value, event, form) {
    var instance = this.getInstance();
}

///////////  Custom UA dialog //////////////

ZaSpnegoUACustomDialog = function(parent,   w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];
	ZaXDialog.call(this, parent, "ZaSpnegoUACustomDialog", title, w, h,null,ZaId.DLG_AUTH_SPNEGO_UA);
	this._containedObject = {};
	this.initForm(ZaAlias.myXModel,this.getMyXForm());
    //this._helpURL = ZaSpnegoUACustomDialog.helpURL;
}

ZaSpnegoUACustomDialog.prototype = new ZaXDialog;
ZaSpnegoUACustomDialog.prototype.constructor = ZaSpnegoUACustomDialog;
//ZaSpnegoUACustomDialog.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_domain/spnego_configuration.htm?locid="+AjxEnv.DEFAULT_LOCALE;



ZaSpnegoUACustomDialog.prototype.getMyXForm =
function() {
	var xFormObject = {
		numCols:1,
		items:[
            {type:_GROUP_,numCols:1,colSizes:["100%"],items: [ //allows tab key iteration
                {type:_OUTPUT_, value:ZaMsg.SpnegoSettingCustomDialogHeader, cssStyle:"padding-bottom:20px;padding-top:10px;"},
                {ref:ZaDomain.A2_zimbraWebClientURLAllowedUA, type:_TEXTAREA_, width:450, height:100, align:_CENTER_
                },
                {type:_OUTPUT_, value:ZaMsg.SpnegoSettingCustomDialogHelp, cssStyle:"padding-bottom:10px;padding-top:20px;"}
            ]}
        ]
	};
	return xFormObject;
}

ZaSpnegoUACustomDialog.updateSpnegoUASetting = function() {
    if(ZaApp.getInstance().dialogs["spnegoUACustomDialog"])
        ZaApp.getInstance().dialogs["spnegoUACustomDialog"].popdown();
    var obj = ZaApp.getInstance().dialogs["spnegoUACustomDialog"].getObject();
    var allowedUA = [];
    if(obj[ZaDomain.A2_zimbraWebClientURLAllowedUA]) {
        allowedUA = obj[ZaDomain.A2_zimbraWebClientURLAllowedUA].split("\n");
    }
    this.setInstanceValue(allowedUA, ZaDomain.A_zimbraWebClientLoginURLAllowedUA);
    this.setInstanceValue(allowedUA, ZaDomain.A_zimbraWebClientLogoutURLAllowedUA);
}


///////////  Spnego Global & Server dialog //////////////



ZaSpnegoGlobalServerDialog = function(parent,   w, h, title) {
	if (arguments.length == 0) return;
	this._standardButtons = [DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON];
	ZaXDialog.call(this, parent, "ZaSpnegoUACustomDialog", title, w, h,null,ZaId.DLG_AUTH_SPNEGO);
	this._containedObject = {};
	this.initForm(ZaAlias.myXModel,this.getMyXForm());
    //this._helpURL = ZaSpnegoGlobalServerDialog.helpURL;
}

ZaSpnegoGlobalServerDialog.prototype = new ZaXDialog;
ZaSpnegoGlobalServerDialog.prototype.constructor = ZaSpnegoGlobalServerDialog;
//ZaSpnegoGlobalServerDialog.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_domain/spnego_configuration.htm?locid="+AjxEnv.DEFAULT_LOCALE;



ZaSpnegoGlobalServerDialog.prototype.getMyXForm =
function() {
	var xFormObject = {
		numCols:1,
		items:[
            {type:_GROUP_,numCols:1,colSizes:["100%"],items: [ //allows tab key iteration
                {type:_GROUP_, colSpan:2,numCols:2,colSizes:["150px","430px"],
                    visibilityChecks:[[ZaTaskAuthConfigWizard.checkSpnegoApplyType,ZaDomain.A2_zimbraSpnegoApplyFor,ZaItem.GLOBAL_CONFIG]],
                    visibilityChangeEventSources:[ZaDomain.A2_zimbraSpnegoApplyFor],
                    items:[
                            {type:_OUTPUT_, value:ZaMsg.spnegoGlobalSettingPageTitle, colSpan:2},
                            {type:_SPACER_, height:10, colSpan:"*"},
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
                            {type:_OUTPUT_, value:ZaMsg.spnegoServerSettingPageTitle, colSpan:2},
                            {type:_SPACER_, height:10, colSpan:"*"},
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
                }
            ]}
        ]
	};
	return xFormObject;
}

ZaSpnegoGlobalServerDialog.updateGlobalServerSetting = function() {
    if(ZaApp.getInstance().dialogs["updateGlobalServerDialog"])
        ZaApp.getInstance().dialogs["updateGlobalServerDialog"].popdown();
    else return;

    var attr = null;
    var soapDoc = null;
    var obj = ZaApp.getInstance().dialogs["updateGlobalServerDialog"].getObject();
    if(obj[ZaDomain.A2_zimbraSpnegoApplyFor] == ZaItem.GLOBAL_CONFIG) {
        var autoDlgForm = ZaApp.getInstance().dialogs["authWizard"]._localXForm;
        autoDlgForm.setInstanceValue(ZaTaskAuthConfigWizard.getGlobalSettingMsg(obj),
                ZaDomain.A2_zimbraSpnegoGlobalSettingStatus);

        soapDoc = AjxSoapDoc.create("ModifyConfigRequest", ZaZimbraAdmin.URN, null);

        if(obj[ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled]) {
            attr = soapDoc.set("a", obj[ZaDomain.A2_zimbraSpnegoGlobalAuthEnabled]);
            attr.setAttribute("n", ZaGlobalConfig.A_zimbraSpnegoAuthEnabled);
        }
        if(obj[ZaDomain.A2_zimbraSpnegoAuthRealm]) {
            attr = soapDoc.set("a", obj[ZaDomain.A2_zimbraSpnegoAuthRealm]);
            attr.setAttribute("n", ZaDomain.A2_zimbraSpnegoAuthRealm);
        }
        if(obj[ZaDomain.A2_zimbraSpnegoAuthErrorURL]) {
            attr = soapDoc.set("a", obj[ZaDomain.A2_zimbraSpnegoAuthErrorURL]);
            attr.setAttribute("n", ZaDomain.A2_zimbraSpnegoAuthErrorURL);
        }

        var params = new Object();
        params.soapDoc = soapDoc;
        var reqMgrParams = {
            controller : ZaApp.getInstance().getCurrentController(),
            busyMsg : ZaMsg.BUSY_GET_ALL_CONFIG
        }
	    ZaRequestMgr.invoke(params, reqMgrParams);

    } else if(obj[ZaDomain.A2_zimbraSpnegoApplyFor] == ZaItem.SERVER && obj[ZaDomain.A2_zimbraSpnegoTargetServer]) {
        soapDoc = AjxSoapDoc.create("ModifyServerRequest", ZaZimbraAdmin.URN, null);
        soapDoc.set("id", obj[ZaDomain.A2_zimbraSpnegoTargetServer]);
        if(obj[ZaDomain.A2_zimbraSpnegoAuthPrincipal]) {
            attr = soapDoc.set("a", obj[ZaDomain.A2_zimbraSpnegoAuthPrincipal]);
            attr.setAttribute("n", ZaDomain.A2_zimbraSpnegoAuthPrincipal);
        }
        if(obj[ZaDomain.A2_zimbraSpnegoAuthTargetName]) {
            attr = soapDoc.set("a", obj[ZaDomain.A2_zimbraSpnegoAuthTargetName]);
            attr.setAttribute("n", ZaDomain.A2_zimbraSpnegoAuthTargetName);
        }

		var params = new Object();
		params.soapDoc = soapDoc;
		var reqMgrParams = {
			controller : ZaApp.getInstance().getCurrentController(),
			busyMsg : ZaMsg.BUSY_MODIFY_SERVER
		}
		ZaRequestMgr.invoke(params, reqMgrParams);
    }

}