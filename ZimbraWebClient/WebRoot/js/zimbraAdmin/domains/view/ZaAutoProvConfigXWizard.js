/**
 * Created by IntelliJ IDEA.
 * User: qinan
 * Date: 7/21/11
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */

ZaAutoProvConfigXWizard = function(parent) {

	ZaXWizardDialog.call(this, parent, null, ZaMsg.NAD_AutoProvConfigTitle,"700px", "350px","ZaAutoProvConfigXWizard", null, ZaId.DLG_AUTPROV_CONFIG);

	this.TAB_INDEX = 0;	

	ZaAutoProvConfigXWizard.AUTOPROV_TYPE_STEP = ++this.TAB_INDEX;
	ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_LAZY_STEP = ++this.TAB_INDEX;
	ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_LDAP_STEP = ++this.TAB_INDEX;
	ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_OTHER_STEP = ++this.TAB_INDEX;
    ZaAutoProvConfigXWizard.AUTOPROV_SUMMARY_STEP = ++this.TAB_INDEX;
	ZaAutoProvConfigXWizard.AUTOPROV_COMPLETE_STEP = ++this.TAB_INDEX;
	
	this.stepChoices = [
		{label:ZaMsg.NAD_AutoProvision_Setting, value:ZaAutoProvConfigXWizard.AUTOPROV_TYPE_STEP},
		{label:ZaMsg.NAD_AutoProvision_Setting, value:ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_LAZY_STEP},
		{label:ZaMsg.NAD_AutoProvision_Setting, value:ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_LDAP_STEP},
		{label:ZaMsg.NAD_AutoProvision_Setting, value:ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_OTHER_STEP},
        {label:ZaMsg.NAD_AutoProvision_Setting, value:ZaAutoProvConfigXWizard.AUTOPROV_SUMMARY_STEP},
		{label:ZaMsg.DomainConfigComplete, value:ZaAutoProvConfigXWizard.AUTOPROV_COMPLETE_STEP}
	];
	
	this.initForm(ZaDomain.myXModel,this.getMyXForm());		
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaAutoProvConfigXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaAutoProvConfigXWizard.prototype.handleXFormChange));	
	this.lastErrorStep=0;	
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_domains/auto_provision_settings.htm?locid="+AjxEnv.DEFAULT_LOCALE;
}

ZaAutoProvConfigXWizard.prototype = new ZaXWizardDialog;
ZaAutoProvConfigXWizard.prototype.constructor = ZaAutoProvConfigXWizard;
ZaXDialog.XFormModifiers["ZaAutoProvConfigXWizard"] = new Array();


ZaAutoProvConfigXWizard.prototype.handleXFormChange = 
function () {
	if(this._localXForm.hasErrors()) {
		if(this.lastErrorStep < this._containedObject[ZaModel.currentStep])
			this.lastErrorStep=this._containedObject[ZaModel.currentStep];
	} else {
		this.lastErrorStep=0;
	}
	this.changeButtonStateForStep(this._containedObject[ZaModel.currentStep]);	
}

ZaAutoProvConfigXWizard.prototype.changeButtonStateForStep = 
function(stepNum) {
	if(this.lastErrorStep == stepNum) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		if(stepNum>1)
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
	} else {

		if(stepNum == ZaAutoProvConfigXWizard.AUTOPROV_TYPE_STEP) {
			//this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);		
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		} else if (stepNum == ZaAutoProvConfigXWizard.AUTOPROV_SUMMARY_STEP) {
			//this._button[DwtWizardDialog.NEXT_BUTTON].setText(ZaMsg.Domain_AuthTestSettings);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		} else if(stepNum == ZaAutoProvConfigXWizard.AUTOPROV_COMPLETE_STEP) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		} else {
			//this._button[DwtWizardDialog.NEXT_BUTTON].setText(AjxMsg._next);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
		}

	}
}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaAutoProvConfigXWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this.changeButtonStateForStep(ZaAutoProvConfigXWizard.AUTOPROV_TYPE_STEP);

}

ZaAutoProvConfigXWizard.prototype.goPage =
function(pageNum) {
	ZaXWizardDialog.prototype.goPage.call(this, pageNum);
	this.changeButtonStateForStep(pageNum);
}

ZaAutoProvConfigXWizard.prototype.goPrev =
function () {
    if(this._containedObject[ZaModel.currentStep] == ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_LDAP_STEP) {
        if(!this._containedObject[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled]
            || this._containedObject[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled] == "FALSE")
           this.goPage(ZaAutoProvConfigXWizard.AUTOPROV_TYPE_STEP);
        else
            this.goPage(this._containedObject[ZaModel.currentStep]-1);
    } else
        this.goPage(this._containedObject[ZaModel.currentStep]-1);
}

ZaAutoProvConfigXWizard.prototype.goNext = 
function() {
    if(this._containedObject[ZaModel.currentStep] == ZaAutoProvConfigXWizard.AUTOPROV_TYPE_STEP) {
        if((!this._containedObject[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled]
                    || this._containedObject[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled] == "FALSE")
                && (!this._containedObject[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled]
                    || this._containedObject[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled] == "FALSE")
                && (!this._containedObject[ZaDomain.A2_zimbraAutoProvModeMANUALEnabled]
                    || this._containedObject[ZaDomain.A2_zimbraAutoProvModeMANUALEnabled] == "FALSE")) {
            ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_AUTOPROV_MODE);
            return;
        }
        if(!this._containedObject[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled]
            || this._containedObject[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled] == "FALSE")
            this.goPage(ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_LDAP_STEP);
        else
            this.goPage(this._containedObject[ZaModel.currentStep]+1);
    } else if(this._containedObject[ZaModel.currentStep] == ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_LAZY_STEP) {
        if((!this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechLDAPEnabled]
                    || this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechLDAPEnabled] == "FALSE")
                && (!this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechPREAUTHEnabled]
                    || this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechPREAUTHEnabled] == "FALSE")
                && (!this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechKRB5Enabled]
                    || this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechKRB5Enabled] == "FALSE")
                && (!this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechSPNEGOEnabled]
                    || this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechSPNEGOEnabled] == "FALSE")) {
            ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_AUTOPROV_LAZYAUTH);
            return;
        } else
            this.goPage(this._containedObject[ZaModel.currentStep]+1);
    } else if(this._containedObject[ZaModel.currentStep] == ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_LDAP_STEP) {
        var isError = false;
        var errorMsg = "";
        if(!this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapURL]
                || this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapURL] == "") {
            isError = true;
            errorMsg = AjxMessageFormat.format(ZaMsg.ERROR_AUTOPROV,ZaMsg.MSG_zimbraAutoProvLdapURL);
        } else if(!this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapAdminBindDn]
                || this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapAdminBindDn] == "") {
            isError = true;
            errorMsg = AjxMessageFormat.format(ZaMsg.ERROR_AUTOPROV,ZaMsg.MSG_zimbraAutoProvLdapAdminBindDn);
        } else if(!this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapAdminBindPassword]
                || this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapAdminBindPassword] == "") {
            isError = true;
            errorMsg = AjxMessageFormat.format(ZaMsg.ERROR_AUTOPROV,ZaMsg.MSG_zimbraAutoProvLdapAdminBindPassword);
        } else if(!this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapSearchBase]
                || this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapSearchBase] == "") {
            isError = true;
            errorMsg = AjxMessageFormat.format(ZaMsg.ERROR_AUTOPROV,ZaMsg.MSG_zimbraAutoProvLdapSearchBase);
        }
        if(!isError && this._containedObject[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled] == "TRUE") {
            if(!this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapSearchFilter]
                    || this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapSearchFilter] == "") {
                isError = true;
                errorMsg = AjxMessageFormat.format(ZaMsg.ERROR_AUTOPROV,ZaMsg.MSG_zimbraAutoProvLdapSearchFilter);
            } else if(!this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapBindDn]
                    || this._containedObject.attrs[ZaDomain.A_zimbraAutoProvLdapBindDn] == "") {
                isError = true;
                errorMsg = AjxMessageFormat.format(ZaMsg.ERROR_AUTOPROV,ZaMsg.MSG_zimbraAutoProvLdapBindDn);
            }
        }
        if(isError) {
            ZaApp.getInstance().getCurrentController().popupErrorDialog(errorMsg);
            return;
        }
        this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode] = [];
        if(this._containedObject[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled] == "TRUE")
            this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode].push("EAGER");
        if(this._containedObject[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled] == "TRUE")
            this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode].push("LAZY");
        if(this._containedObject[ZaDomain.A2_zimbraAutoProvModeMANUALEnabled] == "TRUE")
            this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode].push("MANUAL");

        this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech] = [];
        if(this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechLDAPEnabled] == "TRUE")
            this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech].push("LDAP");
        if(this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechPREAUTHEnabled] == "TRUE")
            this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech].push("PREAUTH");
        if(this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechKRB5Enabled] == "TRUE")
            this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech].push("KRB5");
        if(this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechSPNEGOEnabled] == "TRUE")
            this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech].push("SPNEGO");
        this.goPage(ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_OTHER_STEP);
    } else if(this._containedObject[ZaModel.currentStep] == ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_OTHER_STEP) {
        var isError = false;
        var errorMsg = "";
        if(!isError && this._containedObject[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled] == "TRUE") {
            if(!this._containedObject.attrs[ZaDomain.A_zimbraAutoProvBatchSize]
                    || this._containedObject.attrs[ZaDomain.A_zimbraAutoProvBatchSize] == "") {
                isError = true;
                errorMsg = AjxMessageFormat.format(ZaMsg.ERROR_AUTOPROV,ZaMsg.MSG_zimbraAutoProvBatchSize);
            }
        }
        if(isError) {
            ZaApp.getInstance().getCurrentController().popupErrorDialog(errorMsg);
            return;
        } else  this.goPage(ZaAutoProvConfigXWizard.AUTOPROV_SUMMARY_STEP);
    } else
        this.goPage(this._containedObject[ZaModel.currentStep]+1);
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaDomain object to display
**/
ZaAutoProvConfigXWizard.prototype.setObject =
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

	this.setTitle(ZaMsg.NAD_AutoProvConfigTitle + " (" + entry.name + ")");

    this._containedObject[ZaModel.currentStep] = ZaAutoProvConfigXWizard.AUTOPROV_TYPE_STEP;
    this._containedObject[ZaDomain.A2_zimbraAutoProvServerList] = ZaApp.getInstance().getServerList().getArray();
    this._containedObject[ZaDomain.A2_zimbraAutoProvSelectedServerList] = new AjxVector ();
    for(var i = 0; i < this._containedObject[ZaDomain.A2_zimbraAutoProvServerList].length; i++) {
        var server = this._containedObject[ZaDomain.A2_zimbraAutoProvServerList][i];
        var scheduledDomains = server.attrs[ZaServer.A_zimbraAutoProvScheduledDomains];
        for(var j = 0; scheduledDomains && j < scheduledDomains.length; j++) {
            if(scheduledDomains[j] == this._containedObject.name) {
               this._containedObject[ZaDomain.A2_zimbraAutoProvSelectedServerList].add(server.name);
                server["checked"] = true;
            }
        }
    }
    if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode]) {
        if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode] instanceof Array) {
            for(var mode = 0; mode < this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode].length; mode ++){
                if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode][mode] == "EAGER")
                   this._containedObject[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled] = "TRUE";
                else if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode][mode] == "LAZY")
                   this._containedObject[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled] = "TRUE";
                else if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode][mode] == "MANUAL")
                   this._containedObject[ZaDomain.A2_zimbraAutoProvModeMANUALEnabled] = "TRUE";
            }
        } else {
            if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode] == "EAGER")
               this._containedObject[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled] = "TRUE";
            else if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode] == "LAZY")
               this._containedObject[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled] = "TRUE";
            else if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvMode] == "MANUAL")
               this._containedObject[ZaDomain.A2_zimbraAutoProvModeMANUALEnabled] = "TRUE";
        }
    }

    if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech]) {
        if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech] instanceof Array) {
            for(var mode = 0; mode < this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech].length; mode ++){
                if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech][mode] == "LDAP")
                   this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechLDAPEnabled] = "TRUE";
                else if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech][mode] == "PREAUTH")
                   this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechPREAUTHEnabled] = "TRUE";
                else if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech][mode] == "KRB5")
                   this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechKRB5Enabled] = "TRUE";
                else if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech][mode] == "SPNEGO")
                   this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechSPNEGOEnabled] = "TRUE";
            }
        } else {
                if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech] == "LDAP")
                   this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechLDAPEnabled] = "TRUE";
                else if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech] == "PREAUTH")
                   this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechPREAUTHEnabled] = "TRUE";
                else if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech] == "KRB5")
                   this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechKRB5Enabled] = "TRUE";
                else if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAuthMech] == "SPNEGO")
                   this._containedObject[ZaDomain.A2_zimbraAutoProvAuthMechSPNEGOEnabled] = "TRUE";
        }
    }
    if(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAttrMap]
            && !(this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAttrMap] instanceof Array)) {
        this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAttrMap]
                = [this._containedObject.attrs[ZaDomain.A_zimbraAutoProvAttrMap]];

    }
	this._localXForm.setInstance(this._containedObject);	
}

/**
* XForm definition
**/

ZaAutoProvConfigXWizard.myXFormModifier = function(xFormObject) {
	xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices,valueChangeEventSources:[ZaModel.currentStep]},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},				
			{type:_SWITCH_,width:680,
				items:[
					{type:_CASE_, numCols:2,colSizes:["220px","430px"],	caseKey:ZaAutoProvConfigXWizard.AUTOPROV_TYPE_STEP,
						items:[
                            { type: _DWT_ALERT_,
                                containerCssStyle: "padding-bottom:0px",
                                style: DwtAlert.INFO,
                                iconVisible: false,
                                content: ZaMsg.MSG_NOTE_zimbraAutoProvMode,
                                visibilityChecks:[],
                                colSpan:"2"
                            },
                            {type: _SPACER_, height: 10 },
                            {type:_GROUP_, numCols:2, label:ZaMsg.LBL_zimbraAutoProvMode,
                                labelCssStyle:"vertical-align:top",
                                labelLocation:_LEFT_,
                                colSizes:["20px","250px"],
                                items: [
                                    {ref:ZaDomain.A2_zimbraAutoProvModeEAGEREnabled, type:_CHECKBOX_,
                                        label:ZaMsg.LBL_zimbraAutoProvModeEAGER,
                                        trueValue:"TRUE", falseValue:"FALSE",labelLocation:_RIGHT_
                                    },
                                    {ref:ZaDomain.A2_zimbraAutoProvModeLAZYEnabled, type:_CHECKBOX_,
                                        label:ZaMsg.LBL_zimbraAutoProvModeLAZY,
                                        trueValue:"TRUE", falseValue:"FALSE",labelLocation:_RIGHT_
                                    },
                                    {ref:ZaDomain.A2_zimbraAutoProvModeMANUALEnabled, type:_CHECKBOX_,
                                        label:ZaMsg.LBL_zimbraAutoProvModeMANUAL,
                                        trueValue:"TRUE", falseValue:"FALSE",labelLocation:_RIGHT_
                                    }
                                ]
                            }

						]
					},
					{type:_CASE_, numCols:2,colSizes:["220px","430px"],	caseKey:ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_LAZY_STEP,
                        visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,[XForm.checkInstanceValue,ZaDomain.A2_zimbraAutoProvModeLAZYEnabled,"TRUE"]],
						items:[
                            { type: _DWT_ALERT_,
                                containerCssStyle: "padding-bottom:0px",
                                style: DwtAlert.INFO,
                                iconVisible: true,
                                content: ZaMsg.MSG_NOTE_zimbraAutoProvAuthMech,
                                visibilityChecks:[],
                                colSpan:"2"
                            },
                            {type: _SPACER_, height: 10 },
                            {type:_GROUP_, numCols:2, label:ZaMsg.LBL_zimbraAutoProvAuthMech,
                                labelLocation:_LEFT_, colSizes:["20px","150px"],labelCssStyle:"vertical-align:top",
                                nowrap:false,labelWrap:true,
                                visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_zimbraAutoProvModeLAZYEnabled,"TRUE"]],
                                visibilityChangeEventSources:[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled],
                                items: [
                                    {ref:ZaDomain.A2_zimbraAutoProvAuthMechLDAPEnabled, type:_CHECKBOX_,
                                        label:ZaMsg.LBL_zimbraAutoProvAuthMechLDAP,
                                        trueValue:"TRUE", falseValue:"FALSE",labelLocation:_RIGHT_
                                    },
                                    {ref:ZaDomain.A2_zimbraAutoProvAuthMechPREAUTHEnabled, type:_CHECKBOX_,
                                        label:ZaMsg.LBL_zimbraAutoProvAuthMechPREAUTH,
                                        trueValue:"TRUE", falseValue:"FALSE",labelLocation:_RIGHT_
                                    },
                                    {ref:ZaDomain.A2_zimbraAutoProvAuthMechKRB5Enabled, type:_CHECKBOX_,
                                        label:ZaMsg.LBL_zimbraAutoProvAuthMechKRB5,
                                        trueValue:"TRUE", falseValue:"FALSE",labelLocation:_RIGHT_
                                    },
                                    {ref:ZaDomain.A2_zimbraAutoProvAuthMechSPNEGOEnabled, type:_CHECKBOX_,
                                        label:ZaMsg.LBL_zimbraAutoProvAuthMechSPNEGO,
                                        trueValue:"TRUE", falseValue:"FALSE",labelLocation:_RIGHT_
                                    }
                                ]
                            }
						]
					},
					{type:_CASE_, numCols:2, colSizes:["220px","430px"], caseKey:ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_LDAP_STEP,
						visibilityChecks:[Case_XFormItem.prototype.isCurrentTab],
						items: [
                            {type:_GROUP_, numCols:6, label:"   ", labelLocation:_LEFT_,
                                visibilityChecks: [],
                                visibilityChangeEventSources:[],
                                items: [
                                    {type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"35px"},
                                    {type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthLDAPServerName, width:"200px"},
                                    {type:_OUTPUT_, label:null, labelLocation:_NONE_, value:" ", width:"5px"},
                                    {type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthLDAPServerPort,  width:"40px"},
                                    {type:_OUTPUT_, label:null, labelLocation:_NONE_, value:ZaMsg.Domain_AuthLDAPUseSSL, width:"80px"}
                                ]
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapURL, type:_LDAPURL_, label:ZaMsg.LBL_zimbraAutoProvLdapURL,
                                ldapSSLPort:"636",ldapPort:"389",
                                labelLocation:_LEFT_,
                                label: ZaMsg.LBL_zimbraAutoProvLdapURL
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapStartTlsEnabled, type:_CHECKBOX_,
                                label:ZaMsg.LBL_zimbraAutoProvLdapStartTlsEnabled,
                                trueValue:"TRUE", falseValue:"FALSE",labelLocation:_RIGHT_
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapAdminBindDn, type:_INPUT_,
                                label:ZaMsg.LBL_zimbraAutoProvLdapAdminBindDn, labelLocation:_LEFT_,
                                enableDisableChecks:[],
                                enableDisableChangeEventSources:[]
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapAdminBindPassword, type:_SECRET_,
                                label:ZaMsg.LBL_zimbraAutoProvLdapAdminBindPassword, labelLocation:_LEFT_
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapSearchFilter, type:_TEXTAREA_, width:380, height:40,
                                label:ZaMsg.LBL_zimbraAutoProvLdapSearchFilter, labelLocation:_LEFT_,
                                textWrapping:"soft"
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapSearchBase, type:_TEXTAREA_, width:380, height:40,
                                label:ZaMsg.LBL_zimbraAutoProvLdapSearchBase, labelLocation:_LEFT_,
                                textWrapping:"soft"
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapBindDn, type:_INPUT_,
                                label:ZaMsg.LBL_zimbraAutoProvLdapBindDn, labelLocation:_LEFT_
                            },
                            {type: _SPACER_, height: 10 }
						]						
					},
					{type:_CASE_, numCols:2,colSizes:["220px","430px"],	caseKey:ZaAutoProvConfigXWizard.AUTOPROV_CONFIG_OTHER_STEP,
						items:[
                            { type: _DWT_ALERT_,
                                containerCssStyle: "padding-bottom:0px",
                                style: DwtAlert.INFO,
                                iconVisible: false,
                                content: ZaMsg.MSG_NOTE_otherConfigStep,
                                visibilityChecks:[],
                                colSpan:"2"
                            },
                            {type: _SPACER_, height: 10 },
                            {ref:ZaDomain.A_zimbraAutoProvAccountNameMap, type:_TEXTFIELD_,
                                label:ZaMsg.LBL_zimbraAutoProvAccountNameMap, labelLocation:_LEFT_,
                                width:250, onChange:ZaDomainXFormView.onFormFieldChanged
                            },
                            {ref:ZaDomain.A_zimbraAutoProvAttrMap, type:_REPEAT_,
                                label:ZaMsg.LBL_zimbraAutoProvAttrMap, repeatInstance:"", showAddButton:true,
                                showRemoveButton:true,
                                    addButtonLabel:ZaMsg.NAD_Add,
                                    showAddOnNextRow:true,
                                    removeButtonLabel:ZaMsg.NAD_Remove,
                                    items: [
                                        {ref:".", type:_TEXTFIELD_, label:null,
                                        enableDisableChecks:[], visibilityChecks:[],
                                        onChange:ZaDomainXFormView.onFormFieldChanged}
                                    ]
                            },
                            {ref:ZaDomain.A_zimbraAutoProvNotificationFromAddress, type:_TEXTFIELD_,
                                label:ZaMsg.LBL_zimbraAutoProvNotificationFromAddress, labelLocation:_LEFT_,
                                width:250, onChange:ZaDomainXFormView.onFormFieldChanged
                            },
                            {ref:ZaDomain.A_zimbraAutoProvBatchSize, type:_TEXTFIELD_, label:ZaMsg.LBL_zimbraAutoProvBatchSize,
                                autoSaveValue:true, labelLocation:_LEFT_,
                                visibilityChecks: [[XForm.checkInstanceValue,ZaDomain.A2_zimbraAutoProvModeEAGEREnabled,"TRUE"]],
                                visibilityChangeEventSources:[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled],
                                cssClass:"admin_xform_number_input"
                            },
                            {ref:ZaDomain.A2_zimbraAutoProvPollingInterval, type:_LIFETIME_,
                                colSizes:["80px","100px","*"],
                                label:ZaMsg.LBL_zimbraAutoProvPollingInterval, labelLocation:_LEFT_,
                                visibilityChecks: [[XForm.checkInstanceValue,ZaDomain.A2_zimbraAutoProvModeEAGEREnabled,"TRUE"]],
                                visibilityChangeEventSources:[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled]
                            },
                            {type: _DWT_LIST_, ref: ZaDomain.A2_zimbraAutoProvServerList,  width: 250, height: 50,
                                label:ZaMsg.LBL_zimbraAutoProvServerList,
                                labelLocation:_LEFT_,   labelCssStyle:"vertical-align:top",
                                nowrap:false,labelWrap:true,
                                forceUpdate: true, widgetClass: ZaServerOptionList,
                                multiselect: true, preserveSelection: true,
                                visibilityChecks: [[XForm.checkInstanceValue,ZaDomain.A2_zimbraAutoProvModeEAGEREnabled,"TRUE"]],
                                visibilityChangeEventSources:[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled],
                                onSelection: ZaAutoProvConfigXWizard.filterSelectionListener
                            }
						]
					},
					{type:_CASE_, numCols:2,colSizes:["220px","430px"],	caseKey:ZaAutoProvConfigXWizard.AUTOPROV_SUMMARY_STEP,
						items: [
							{type:_OUTPUT_, value:ZaMsg.NAD_Prov_ConfigSummary, align:_CENTER_, colSpan:"*"},
							{type:_SPACER_, height:10},

                            {type:_OUTPUT_, value:ZaMsg.LBL_zimbraAutoProvMode, align:_LEFT_, colSpan:"*",cssStyle:"padding-left: 120px;"},
                            {type:_GROUP_, numCols:2, label:null,
                                cssStyle:"padding-left:150px;", colSpan:"*",
                                items: [
                                    {ref:ZaDomain.A2_zimbraAutoProvModeEAGEREnabled, type:_OUTPUT_,
                                        label:ZaMsg.LBL_zimbraAutoProvModeEAGER + ":",
                                        choices:ZaModel.BOOLEAN_CHOICES,
                                        labelLocation:_LEFT_
                                    },
                                    {ref:ZaDomain.A2_zimbraAutoProvModeLAZYEnabled, type:_OUTPUT_,
                                        label:ZaMsg.LBL_zimbraAutoProvModeLAZY + ":",
                                        choices:ZaModel.BOOLEAN_CHOICES,
                                        labelLocation:_LEFT_
                                    },
                                    {ref:ZaDomain.A2_zimbraAutoProvModeMANUALEnabled, type:_OUTPUT_,
                                        label:ZaMsg.LBL_zimbraAutoProvModeMANUAL + ":",
                                        choices:ZaModel.BOOLEAN_CHOICES,
                                        labelLocation:_LEFT_
                                    }
                                ]
                            },
                            {type: _SPACER_, height: 10 },
                            {type:_OUTPUT_, value:ZaMsg.LBL_zimbraAutoProvAuthMech, align:_LEFT_, colSpan:"*",
                                visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_zimbraAutoProvModeLAZYEnabled,"TRUE"]],
                                visibilityChangeEventSources:[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled],
                                 cssStyle:"padding-left: 120px;"},
                            {type:_GROUP_, numCols:2,
                                cssStyle:"padding-left:200px;", colSpan:"*",
                                visibilityChecks:[[XForm.checkInstanceValue,ZaDomain.A2_zimbraAutoProvModeLAZYEnabled,"TRUE"]],
                                visibilityChangeEventSources:[ZaDomain.A2_zimbraAutoProvModeLAZYEnabled],
                                items: [
                                    {ref:ZaDomain.A2_zimbraAutoProvAuthMechLDAPEnabled, type:_OUTPUT_,
                                        label:ZaMsg.LBL_zimbraAutoProvAuthMechLDAP + ":",
                                        choices:ZaModel.BOOLEAN_CHOICES,
                                        labelLocation:_LEFT_
                                    },
                                    {ref:ZaDomain.A2_zimbraAutoProvAuthMechPREAUTHEnabled, type:_OUTPUT_,
                                        label:ZaMsg.LBL_zimbraAutoProvAuthMechPREAUTH + ":",
                                        choices:ZaModel.BOOLEAN_CHOICES,
                                        labelLocation:_LEFT_
                                    },
                                    {ref:ZaDomain.A2_zimbraAutoProvAuthMechKRB5Enabled, type:_OUTPUT_,
                                        label:ZaMsg.LBL_zimbraAutoProvAuthMechKRB5 + ":",
                                        choices:ZaModel.BOOLEAN_CHOICES,
                                        labelLocation:_LEFT_
                                    },
                                    {ref:ZaDomain.A2_zimbraAutoProvAuthMechSPNEGOEnabled, type:_OUTPUT_,
                                        label:ZaMsg.LBL_zimbraAutoProvAuthMechSPNEGO + ":",
                                        choices:ZaModel.BOOLEAN_CHOICES,
                                        labelLocation:_LEFT_
                                    }
                                ]
                            },
                            {type: _SPACER_, height: 10 },
                            {ref:ZaDomain.A_zimbraAutoProvLdapURL, type:_OUTPUT_, label:ZaMsg.LBL_zimbraAutoProvLdapURL,
                                labelLocation:_LEFT_,
                                label: ZaMsg.LBL_zimbraAutoProvLdapURL
                            },

                            {ref:ZaDomain.A_zimbraAutoProvLdapStartTlsEnabled, type:_OUTPUT_,
                                label:ZaMsg.LBL_zimbraAutoProvLdapStartTlsEnabled + ":",
                                choices:ZaModel.BOOLEAN_CHOICES,labelLocation:_LEFT_
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapAdminBindDn, type:_OUTPUT_,
                                label:ZaMsg.LBL_zimbraAutoProvLdapAdminBindDn, labelLocation:_LEFT_
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapAdminBindPassword, type:_OUTPUT_,
                                getDisplayValue:function() {
                                    var val = ZaItem.formatServerTime(this.getInstanceValue());
                                    if(!val)
                                        return ZaMsg.LBL_setPassword;
                                    else
                                        return ZaMsg.LBL_unsetPassword;
                                },
                                label:ZaMsg.LBL_zimbraAutoProvLdapAdminBindPassword, labelLocation:_LEFT_
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapSearchFilter, type:_OUTPUT_,
                                label:ZaMsg.LBL_zimbraAutoProvLdapSearchFilter, labelLocation:_LEFT_
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapSearchBase, type:_OUTPUT_,
                                label:ZaMsg.LBL_zimbraAutoProvLdapSearchBase, labelLocation:_LEFT_
                            },
                            {ref:ZaDomain.A_zimbraAutoProvLdapBindDn, type:_OUTPUT_,
                                label:ZaMsg.LBL_zimbraAutoProvLdapBindDn, labelLocation:_LEFT_
                            },
                            {ref:ZaDomain.A_zimbraAutoProvBatchSize, type:_OUTPUT_, label:ZaMsg.LBL_zimbraAutoProvBatchSize,
                                autoSaveValue:true, labelLocation:_LEFT_,
                                visibilityChecks: [[XForm.checkInstanceValue,ZaDomain.A2_zimbraAutoProvModeEAGEREnabled,"TRUE"]],
                                visibilityChangeEventSources:[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled]
                            },
                            {ref:ZaDomain.A2_zimbraAutoProvPollingInterval, type:_OUTPUT_,
                                label:ZaMsg.LBL_zimbraAutoProvPollingInterval, labelLocation:_LEFT_,
                                visibilityChecks: [[XForm.checkInstanceValue,ZaDomain.A2_zimbraAutoProvModeEAGEREnabled,"TRUE"]],
                                visibilityChangeEventSources:[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled]

                            },
                            {ref:ZaDomain.A2_zimbraAutoProvSelectedServerList, type:_OUTPUT_,
                                getDisplayValue:function() {
                                    var val = this.getInstanceValue();
                                    if(!val)
                                        return ZaMsg.LBL_setPassword;
                                    else  {
                                        return val.getArray().join(",");
                                    }
                                },
                                label:ZaMsg.LBL_zimbraAutoProvServerList, labelLocation:_LEFT_,
                                visibilityChecks: [[XForm.checkInstanceValue,ZaDomain.A2_zimbraAutoProvModeEAGEREnabled,"TRUE"]],
                                visibilityChangeEventSources:[ZaDomain.A2_zimbraAutoProvModeEAGEREnabled]
                            }
						]
					},
					{type:_CASE_, caseKey:ZaAutoProvConfigXWizard.AUTOPROV_COMPLETE_STEP,
						items: [
							{type:_OUTPUT_, value:ZaMsg.Domain_Auth_Config_Complete}
						]
					}
				]
			}
		];
}
ZaXDialog.XFormModifiers["ZaAutoProvConfigXWizard"].push(ZaAutoProvConfigXWizard.myXFormModifier);

ZaAutoProvConfigXWizard.filterSelectionListener =
function (value) {
	var targetEl = value.target ;

	if (targetEl.type && targetEl.type == "checkbox") {
		var item = targetEl.value ;
		var form = this.getForm ();
		var instance = form.getInstance ();
		var checkedFiltersVector = null ;

        checkedFiltersVector = instance[ZaDomain.A2_zimbraAutoProvSelectedServerList];
		if (targetEl.checked) {
			checkedFiltersVector.remove(item);
		}else{
			checkedFiltersVector.add(item);

		}
	}
}

/////////////////////////////
/*
ZaServerOptionList = function(parent,className) {
	DwtListView.call(this, parent, null);//, Dwt.ABSOLUTE_STYLE);
}

ZaServerOptionList.prototype = new DwtListView;
ZaServerOptionList.prototype.constructor = ZaServerOptionList;

ZaServerOptionList.prototype.toString =
function() {
	return "ZaServerOptionList";
}

ZaServerOptionList.prototype._createItemHtml =
function(item) {
	var html = new Array(10);
	var	div = document.createElement("div");
	div[DwtListView._STYLE_CLASS] = "Row";
	div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
	div.className = div[DwtListView._STYLE_CLASS];
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);

	var idx = 0;
    var checked = "";
    if(item.checked) checked = "checked";
	html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'><tr><td width=20>"
	html[idx++] = "<input type=checkbox value='" + item + "' " + checked + "/></td>" ;
	html[idx++] = "<td>"+ item + "</td></tr></table>";

	div.innerHTML = html.join("");
	return div;
}
*/