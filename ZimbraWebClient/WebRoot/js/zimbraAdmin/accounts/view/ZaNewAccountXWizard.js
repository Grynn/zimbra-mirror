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

ZaNewAccountXWizard = function(parent, entry) {
	ZaXWizardDialog.call(this, parent, null, ZaMsg.NCD_NewAccTitle, "720px", "300px","ZaNewAccountXWizard",null,ZaId.DLG_NEW_ACCT);
	this.accountStatusChoices = [
		{value:ZaAccount.ACCOUNT_STATUS_ACTIVE, label:ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_ACTIVE)},
		{value:ZaAccount.ACCOUNT_STATUS_CLOSED, label:ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_CLOSED)},
		{value:ZaAccount.ACCOUNT_STATUS_LOCKED, label: ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_LOCKED)},
        {value:ZaAccount.ACCOUNT_STATUS_PENDING, label: ZaAccount.getAccountStatusMsg (ZaAccount.ACCOUNT_STATUS_PENDING)},
        {value:ZaAccount.ACCOUNT_STATUS_MAINTENANCE, label:ZaAccount.getAccountStatusMsg(ZaAccount.ACCOUNT_STATUS_MAINTENANCE)}
	];

	this.initForm(ZaAccount.myXModel,this.getMyXForm(entry), null);	
  
	this._localXForm.setController();	
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaNewAccountXWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaNewAccountXWizard.prototype.handleXFormChange));	
	this._helpURL = ZaNewAccountXWizard.helpURL;
	
	this._domains = {} ;
}


ZaNewAccountXWizard.zimletChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
ZaNewAccountXWizard.themeChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST);
ZaNewAccountXWizard.prototype = new ZaXWizardDialog;
ZaNewAccountXWizard.prototype.constructor = ZaNewAccountXWizard;
ZaNewAccountXWizard.prototype.toString = function() {
    return "ZaNewAccountXWizard";
}
ZaXDialog.XFormModifiers["ZaNewAccountXWizard"] = new Array();
ZaNewAccountXWizard.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/create_an_account.htm?locid="+AjxEnv.DEFAULT_LOCALE;
ZaNewAccountXWizard.prototype.handleXFormChange = 
function () {
	if(this._localXForm.hasErrors()) {
		var isNeeded = true;
				
/*
 *Bug 49662 If it is alias step, we check the error'root. If the error is thrown
 *for the username is null, we reset this error's status. For emailaddr item's 
 *OnChange() function is called after item value validation. At the stage of
 *value validation, an error is thrown and OnChange can't be called. If we
 *modify the email-address's validation method, it will effect the first stage
 *of account creatin. So we reset the error status here
 */		
        	if(this._containedObject[ZaModel.currentStep] == ZaNewAccountXWizard.ALIASES_STEP){
			var args = arguments[0];
			if(args && args.formItem && (args.formItem.type == "emailaddr")){
				isNeeded = !args.formItem.clearNameNullError();
			}
		}

		if(isNeeded){
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
		}
	} else {
		if(this._containedObject.attrs[ZaAccount.A_lastName]
                && this._containedObject[ZaAccount.A_name].indexOf("@") > 0
                && ZaAccount.isAccountTypeSet(this._containedObject) 
                ) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
			if(this._containedObject[ZaModel.currentStep] != this._lastStep) {
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
			}
			if(this._containedObject[ZaModel.currentStep] != ZaNewAccountXWizard.GENERAL_STEP) {
				this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
			}			
		}
	}
}

//update the account type output with the right available/used account counts
//and not default account type choices displayed
ZaNewAccountXWizard.accountTypeItemId = "account_type_output_" + Dwt.getNextId();
ZaNewAccountXWizard.prototype.updateAccountType =
function ()  {                                                      
    var item = this._localXForm.getItemsById (ZaNewAccountXWizard.accountTypeItemId) [0] ;
    item.updateElement(ZaAccount.getAccountTypeOutput.call(item, true)) ;
}

ZaNewAccountXWizard.cosGroupItemId = "cos_grouper_" + Dwt.getNextId();
ZaNewAccountXWizard.prototype.updateCosGrouper =
function () {
    var item = this._localXForm.getItemsById (ZaNewAccountXWizard.cosGroupItemId) [0] ;
    item.items[0].setElementEnabled(true);
    item.updateElement() ;
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
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

ZaNewAccountXWizard.prototype.createDomainAndAccount = function(domainName) {
	try {
		var newDomain = new ZaDomain();
		newDomain.name=domainName;
		newDomain.attrs[ZaDomain.A_domainName] = domainName;
		var domain = ZaItem.create(newDomain,ZaDomain,"ZaDomain");
		if(domain != null) {
			ZaApp.getInstance().getCurrentController().closeCnfrmDelDlg();
			this.finishWizard();
		}
	} catch(ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaNewAccountXWizard.prototype.createDomainAndAccount", null, false);	
	}
}

ZaNewAccountXWizard.prototype.finishWizard = 
function() {
	try {
        if(this._containedObject.attrs[ZaAccount.A_password]) {
            if(this._containedObject.attrs[ZaAccount.A_password] != this._containedObject[ZaAccount.A2_confirmPassword]) {
                ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
                return false;
            }
        }
		
		if(!ZaAccount.checkValues(this._containedObject)) {
			return false;
		}
		var account = ZaItem.create(this._containedObject,ZaAccount,"ZaAccount");
		if(account != null) {
			//if creation took place - fire an change event
			ZaApp.getInstance().getAccountListController().fireCreationEvent(this._containedObject);
			this.popdown();
            ZaApp.getInstance().getAppCtxt().getAppController().setActionStatusMsg(AjxMessageFormat.format(ZaMsg.AccountCreated,[account.name]));
			//ZaApp.getInstance().getCurrentController().popupMsgDialog(AjxMessageFormat.format(ZaMsg.AccountCreated,[account.name]));
		}
	} catch (ex) {
		switch(ex.code) {		
			case ZmCsfeException.ACCT_EXISTS:
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_ACCOUNT_EXISTS);
			break;
			case ZmCsfeException.NO_SUCH_COS:
				ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.ERROR_NO_SUCH_COS,[this._containedObject.attrs[ZaAccount.A_COSId]]), ex);
		    break;
			case ZmCsfeException.ACCT_INVALID_PASSWORD:
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_INVALID, ex);
				ZaApp.getInstance().getAppCtxt().getErrorDialog().showDetail(true);
			break;
			case ZmCsfeException.NO_SUCH_DOMAIN:
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].setMessage(AjxMessageFormat.format(ZaMsg.CreateDomain_q,[ZaAccount.getDomain(this._containedObject.name)]), DwtMessageDialog.WARNING_STYLE);
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].registerCallback(DwtDialog.YES_BUTTON, this.createDomainAndAccount, this, [ZaAccount.getDomain(this._containedObject.name)]);		
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].registerCallback(DwtDialog.NO_BUTTON, ZaController.prototype.closeCnfrmDelDlg, ZaApp.getInstance().getCurrentController(), null);				
				ZaApp.getInstance().dialogs["confirmMessageDialog2"].popup();  				
			break;
			default:
				ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaNewAccountXWizard.prototype.finishWizard", null, false);
			break;		
		}
	}
}

ZaNewAccountXWizard.prototype.goNext = 
function() {
	if (this._containedObject[ZaModel.currentStep] == 1) {
		//check if passwords match
		if(this._containedObject.attrs[ZaAccount.A_password]) {
			if(this._containedObject.attrs[ZaAccount.A_password] != this._containedObject[ZaAccount.A2_confirmPassword]) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
				return false;
			}
		}
		//check if account exists
        if (ZaSearch.isAccountExist.call(this, {name: this._containedObject[ZaAccount.A_name], popupError: true})) {
            return false ;
        }
        this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
		
	} 
	this.goPage(this._containedObject[ZaModel.currentStep] + 1);
	if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	}	
}

ZaNewAccountXWizard.prototype.goPrev = 
function() {
	if (this._containedObject[ZaModel.currentStep] == 2) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	} else if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
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
	this._containedObject = new ZaAccount();
	this._containedObject.attrs = new Object();

	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
	this._containedObject.name = entry.name || "";

    if(entry._uuid) {
        this._containedObject._uuid = entry._uuid;
    }

	if(entry.rights) {
		this._containedObject.rights = entry.rights;
	} else {
		this._containedObject.rights = [];
	}
	if(this._containedObject.rights[ZaAccount.RENAME_ACCOUNT_RIGHT] === undefined)
		this._containedObject.rights[ZaAccount.RENAME_ACCOUNT_RIGHT] = true; //since this is a new account, we should be able to give it a name
	
	if(entry.setAttrs)
		this._containedObject.setAttrs = entry.setAttrs;
	
	if(entry.getAttrs)
		this._containedObject.getAttrs = entry.getAttrs;
		
	if(entry._defaultValues)
		this._containedObject._defaultValues = entry._defaultValues;

	this._containedObject.id = entry.id || null;

	this.cosChoices.setChoices([this._containedObject.cos]);
	this.cosChoices.dirtyChoices();

    this._containedObject.attrs[ZaAccount.A_accountStatus] =  ZaAccount.ACCOUNT_STATUS_ACTIVE;
	this._containedObject[ZaAccount.A2_autodisplayname] = entry[ZaAccount.A2_autodisplayname] || "TRUE";
	this._containedObject[ZaAccount.A2_autoMailServer] = entry[ZaAccount.A2_autoMailServer] || "TRUE";
	this._containedObject[ZaAccount.A2_autoCos] = entry[ZaAccount.A2_autoCos] || "TRUE";
	this._containedObject[ZaAccount.A2_confirmPassword] = entry[ZaAccount.A2_confirmPassword] || null;
	this._containedObject[ZaModel.currentStep] = entry[ZaModel.currentStep] || 1;
	this._containedObject.attrs[ZaAccount.A_zimbraMailAlias] = entry.attrs[ZaAccount.A_zimbraMailAlias] || new Array();
	this._containedObject[ZaAccount.A2_errorMessage] = entry[ZaAccount.A2_errorMessage] || "";
	var domainName;
	if(!domainName) {
		//find out what is the default domain
		try {
			domainName = ZaApp.getInstance().getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraDefaultDomainName];
		} catch (ex) {
			if(ex.code != ZmCsfeException.SVC_PERM_DENIED) {
				throw(ex);
			}
		} 

	}
	//this._containedObject.globalConfig = ZaApp.getInstance().getGlobalConfig();
	 
	if(!domainName) {
		domainName =  ZaSettings.myDomainName;
	}
	this._containedObject[ZaAccount.A_name] = "@" + domainName;
    if (entry[ZaAccount.A_name])
        this._containedObject[ZaAccount.A_name] = entry[ZaAccount.A_name];
	EmailAddr_XFormItem.domainChoices.setChoices([]);
	EmailAddr_XFormItem.domainChoices.dirtyChoices();

    var domainName = ZaAccount.getDomain (this._containedObject.name) ;
    try {
   	 	var domainObj = ZaDomain.getDomainByName(domainName) ;
    	this._containedObject[ZaAccount.A2_accountTypes] = domainObj.getAccountTypes () ;
    } catch (ex) {
    	if(ex.code == ZmCsfeException.SVC_PERM_DENIED) {
    		this._containedObject[ZaAccount.A2_errorMessage] = AjxMessageFormat.format(ZaMsg.CANNOT_CREATE_ACCOUNTS_IN_THIS_DOMAIN,[domainName]);
    		//ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.CANNOT_CREATE_ACCOUNTS_IN_THIS_DOMAIN,[domainName])	, ex);
    	} else {	
    		this._containedObject[ZaAccount.A2_errorMessage] = "";
	 		throw(ex);
		}
    }

    this._containedObject[ZaAccount.A2_memberOf] = ZaAccountMemberOfListView.cloneMemberOf(entry);

    //add the memberList page information
	this._containedObject[ZaAccount.A2_directMemberList + "_offset"] = entry[ZaAccount.A2_directMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_directMemberList + "_more"] = entry[ZaAccount.A2_directMemberList + "_more"];
	this._containedObject[ZaAccount.A2_indirectMemberList + "_offset"] = entry[ZaAccount.A2_indirectMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_indirectMemberList + "_more"] = entry[ZaAccount.A2_indirectMemberList + "_more"];
	this._containedObject[ZaAccount.A2_nonMemberList + "_offset"] = entry[ZaAccount.A2_nonMemberList + "_offset"];
	this._containedObject[ZaAccount.A2_nonMemberList + "_more"] = entry[ZaAccount.A2_nonMemberList + "_more"];
	if(entry.getAttrs[ZaAccount.A_zimbraAvailableSkin] || entry.getAttrs.all) {
		var skins = ZaApp.getInstance().getInstalledSkins();
		
		if(AjxUtil.isEmpty(skins)) {
			if(entry._defaultValues && entry._defaultValues.attrs && !AjxUtil.isEmpty(entry._defaultValues.attrs[ZaAccount.A_zimbraAvailableSkin])) {
				//if we cannot get all zimlets from domain either, just use whatever came in "defaults" which would be what the COS value is
				skins = entry._defaultValues.attrs[ZaAccount.A_zimbraAvailableSkin];
			} else {
				skins = [];
			}
		} else {
			if (AjxUtil.isString(skins))	 {
				skins = [skins];
			}
		}
		
		ZaNewAccountXWizard.themeChoices.setChoices(skins);
		ZaNewAccountXWizard.themeChoices.dirtyChoices();		
		
	}	

	if(entry.getAttrs[ZaAccount.A_zimbraZimletAvailableZimlets] || entry.getAttrs.all) {
		//get sll Zimlets
		var allZimlets = ZaZimlet.getAll("extension");

		if(!AjxUtil.isEmpty(allZimlets) && allZimlets instanceof ZaItemList || allZimlets instanceof AjxVector)
			allZimlets = allZimlets.getArray();

		if(AjxUtil.isEmpty(allZimlets)) {
			
			if(entry._defaultValues && entry._defaultValues.attrs && !AjxUtil.isEmpty(entry._defaultValues.attrs[ZaAccount.A_zimbraZimletAvailableZimlets])) {
				allZimlets = entry._defaultValues.attrs[ZaAccount.A_zimbraZimletAvailableZimlets];
			} else {
				allZimlets = [];
			}
			ZaNewAccountXWizard.zimletChoices.setChoices(allZimlets);
			ZaNewAccountXWizard.zimletChoices.dirtyChoices();
			
		} else {
			//convert objects to strings	
			var cnt = allZimlets.length;
			var _tmpZimlets = [];
			for(var i=0; i<cnt; i++) {
				var zimlet = allZimlets[i];
				_tmpZimlets.push(zimlet.name);
			}
			ZaNewAccountXWizard.zimletChoices.setChoices(_tmpZimlets);
			ZaNewAccountXWizard.zimletChoices.dirtyChoices();
		}
	}	

    if (domainObj && domainObj.attrs &&
        domainObj.attrs[ZaDomain.A_AuthMech] &&
        (domainObj.attrs[ZaDomain.A_AuthMech] != ZaDomain.AuthMech_zimbra) ) {
        this._containedObject[ZaAccount.A2_isExternalAuth] = true;
    } else {
        this._containedObject[ZaAccount.A2_isExternalAuth] = false;
    }

    //check the account type here
    this._localXForm.setInstance(this._containedObject);
    var nameFields = this._localXForm.getItemsById(ZaAccount.A_name);
    if(!AjxUtil.isEmpty(nameFields) && nameFields[0] && nameFields[0].resetEditedState)
		nameFields[0].resetEditedState();
}

ZaNewAccountXWizard.isAuthfromInternal =
function(domainName, attrName) {

	var acctName = null;
	if(attrName) {
        	var instance = this.getInstance();
        	if(instance)
                	acctName = this.getInstanceValue(attrName);

	}
	if(!acctName) acctName = domainName;
	return ZaAccountXFormView.isAuthfromInternal(acctName);
}


ZaNewAccountXWizard.isDomainLeftAccountsAlertVisible = function () {
	var val1 = this.getInstanceValue(ZaAccount.A2_domainLeftAccounts);
	var val2 = this.getInstanceValue(ZaAccount.A2_accountTypes);
	return (!AjxUtil.isEmpty(val1) && AjxUtil.isEmpty(val2));
}

ZaNewAccountXWizard.isAccountsTypeAlertInvisible = function () {
        var val = this.getInstanceValue(ZaAccount.A2_showAccountTypeMsg);
        return (AjxUtil.isEmpty(val));
}

ZaNewAccountXWizard.isAccountTypeGrouperVisible = function () {
	return !AjxUtil.isEmpty(this.getInstanceValue(ZaAccount.A2_accountTypes));
}

ZaNewAccountXWizard.isAccountTypeSet = function () {
	return !ZaAccount.isAccountTypeSet(this.getInstance());
}

ZaNewAccountXWizard.isAutoDisplayname = function () {
	return (this.getInstanceValue(ZaAccount.A2_autodisplayname)=="FALSE");
}

ZaNewAccountXWizard.isAutoCos = function () {
	return (this.getInstanceValue(ZaAccount.A2_autoCos)=="FALSE");
}


ZaNewAccountXWizard.isIMFeatureEnabled = function () {
	return (this.getInstanceValue(ZaAccount.A_zimbraFeatureIMEnabled) == "TRUE");
}

ZaNewAccountXWizard.isCalendarFeatureEnabled = function () {
	return this.getInstanceValue(ZaAccount.A_zimbraFeatureCalendarEnabled)=="TRUE";
}

ZaNewAccountXWizard.isMailForwardingEnabled = function () {
	return (this.getInstanceValue(ZaAccount.A_zimbraFeatureMailForwardingEnabled) == "TRUE");
}

ZaNewAccountXWizard.onCOSChanged = 
function(value, event, form) {
	if(ZaItem.ID_PATTERN.test(value))  {
		form.getInstance()._defaultValues = ZaCos.getCosById(value, form.parent._app);
		this.setInstanceValue(value);
	} else {
		form.getInstance()._defaultValues = ZaCos.getCosByName(value, form.parent._app);
		if(form.getInstance().cos) {
			//value = form.getInstance()._defaultValues.id;
			value = form.getInstance()._defaultValues.id;
		} 
	}
	this.setInstanceValue(value);
    form.parent._isCosChanged = true ;

    //if cos is changed,  update the account type information
    form.parent.updateAccountType();
    
    return value;
}

ZaNewAccountXWizard.getAccountNameInfoItem = function(){
	if(AjxUtil.isEmpty(ZaNewAccountXWizard.accountNameInfoPool)){
		ZaNewAccountXWizard.accountNameInfoPool = new Object();
		ZaNewAccountXWizard.accountNameInfoPool[ZaAccount.A_name] = {ref:ZaAccount.A_name, type:_EMAILADDR_,
					 msgName:ZaMsg.NAD_AccountName,label:ZaMsg.NAD_AccountName, bmolsnr:false,
                                        domainPartWidth:"100%",
                                        labelLocation:_LEFT_,onChange:ZaAccount.setDomainChanged,forceUpdate:true,
                                        enableDisableChecks:[],
                                        visibilityChecks:[]
                                },
		ZaNewAccountXWizard.accountNameInfoPool[ZaAccount.A_firstName] = {ref:ZaAccount.A_firstName, type:_TEXTFIELD_,
					msgName:ZaMsg.NAD_FirstName,label:ZaMsg.NAD_FirstName,
					labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150,
					elementChanged: function(elementValue,instanceValue, event) {
						if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
							ZaAccount.generateDisplayName.call(this, this.getInstance(), elementValue, this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials] );
						}
						this.getForm().itemChanged(this, elementValue, event);
					}
				};
		ZaNewAccountXWizard.accountNameInfoPool[ZaAccount.A_initials] = {ref:ZaAccount.A_initials, type:_TEXTFIELD_,
					msgName:ZaMsg.NAD_Initials,label:ZaMsg.NAD_Initials, labelLocation:_LEFT_,
					cssClass:"admin_xform_name_input", width:50,
					elementChanged: function(elementValue,instanceValue, event) {
						if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
							ZaAccount.generateDisplayName.call(this, this.getInstance(), this.getInstanceValue(ZaAccount.A_firstName), this.getInstanceValue(ZaAccount.A_lastName),elementValue);
						}
						this.getForm().itemChanged(this, elementValue, event);
					}
				};
		ZaNewAccountXWizard.accountNameInfoPool[ZaAccount.A_lastName] = {ref:ZaAccount.A_lastName, type:_TEXTFIELD_,
					msgName:ZaMsg.NAD_LastName,label:ZaMsg.NAD_LastName, labelLocation:_LEFT_,
					cssClass:"admin_xform_name_input", width:150,
					elementChanged: function(elementValue,instanceValue, event) {
						if(this.getInstance()[ZaAccount.A2_autodisplayname]=="TRUE") {
							ZaAccount.generateDisplayName.call(this, this.getInstance(),  this.getInstanceValue(ZaAccount.A_firstName), elementValue ,this.getInstanceValue(ZaAccount.A_initials));
						}
						this.getForm().itemChanged(this, elementValue, event);
					}
				};
		ZaNewAccountXWizard.accountNameInfoPool["ZaAccountDisplayInfoGroup"] = {type:_GROUP_, numCols:3, nowrap:true,
					width:200, msgName:ZaMsg.NAD_DisplayName,label:ZaMsg.NAD_DisplayName, labelLocation:_LEFT_,
                                        visibilityChecks:[[ZaItem.hasReadPermission,ZaAccount.A_displayname]],
                                        items: [
                                                {ref:ZaAccount.A_displayname, type:_TEXTFIELD_, label:null,     cssClass:"admin_xform_name_input", width:150,
                                                        enableDisableChecks:[ [XForm.checkInstanceValue,ZaAccount.A2_autodisplayname,"FALSE"],ZaItem.hasWritePermission],
                                                        enableDisableChangeEventSources:[ZaAccount.A2_autodisplayname],bmolsnr:true,
                                                        visibilityChecks:[]
                                                },
                                                {ref:ZaAccount.A2_autodisplayname, type:_WIZ_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE", subLabel:"",
                                                        elementChanged: function(elementValue,instanceValue, event) {
                                                                if(elementValue=="TRUE") {
                                                                        if(ZaAccount.generateDisplayName.call(this, this.getInstance(), this.getInstanceValue(ZaAccount.A_firstName), this.getInstanceValue(ZaAccount.A_lastName),this.getInstanceValue(ZaAccount.A_initials))) {
                                                                                this.getForm().parent.setDirty(true);
                                                                        }
                                                                }
                                                                this.getForm().itemChanged(this, elementValue, event);
                                                        },
                                                        enableDisableChecks:[[ZaItem.hasWritePermission,ZaAccount.A_displayname]],
                            visibilityChecks:[[ZaItem.hasWritePermission,ZaAccount.A_displayname]]

                                                }
                                        ]
                                },
		ZaNewAccountXWizard.accountNameInfoPool[ZaAccount.A_zimbraHideInGal]={ref:ZaAccount.A_zimbraHideInGal, type:_WIZ_CHECKBOX_,
				  			msgName:ZaMsg.LBL_zimbraHideInGal, subLabel:"", labelLocation:_RIGHT_,align:_RIGHT_,
				  			label:ZaMsg.LBL_zimbraHideInGal, trueValue:"TRUE", falseValue:"FALSE"
				},
		ZaNewAccountXWizard.accountNameInfoPool[ZaAccount.A_zimbraPhoneticFirstName] = {
					ref:ZaAccount.A_zimbraPhoneticFirstName, type:_TEXTFIELD_,
					msgName:ZaMsg.NAD_zimbraPhoneticFirstName,label:ZaMsg.NAD_zimbraPhoneticFirstName,
                                        labelLocation:_LEFT_, cssClass:"admin_xform_name_input",width:150
                                };
		ZaNewAccountXWizard.accountNameInfoPool[ZaAccount.A_zimbraPhoneticLastName] = {
                                        ref:ZaAccount.A_zimbraPhoneticLastName, type:_TEXTFIELD_,
                                        msgName:ZaMsg.NAD_zimbraPhoneticLastName,label:ZaMsg.NAD_zimbraPhoneticLastName,
                                        labelLocation:_LEFT_, cssClass:"admin_xform_name_input",width:150
                                };

	}

	var accountNameFormItems = new Array();
        var accountNameItemsOrders = new Array();
        if(ZaZimbraAdmin.isLanguage("ja")){
		accountNameItemsOrders = [ZaAccount.A_name, ZaAccount.A_zimbraPhoneticLastName, ZaAccount.A_lastName, ZaAccount.A_initials, ZaAccount.A_zimbraPhoneticFirstName, ZaAccount.A_firstName, "ZaAccountDisplayInfoGroup", ZaAccount.A_zimbraHideInGal];
        }
        else{
		accountNameItemsOrders = [ZaAccount.A_name, ZaAccount.A_firstName, ZaAccount.A_initials, ZaAccount.A_lastName,"ZaAccountDisplayInfoGroup", ZaAccount.A_zimbraHideInGal];
        }

        for(var i = 0; i < accountNameItemsOrders.length; i++){
                accountNameFormItems.push(ZaNewAccountXWizard.accountNameInfoPool[accountNameItemsOrders[i]]);
        }
        return accountNameFormItems;
}

ZaNewAccountXWizard.myXFormModifier = function(xFormObject, entry) {	
	var domainName = ZaSettings.myDomainName;

	var emptyAlias = "@" + domainName;
	var cases = new Array();

	this.stepChoices = [];
	this.TAB_INDEX = 0;	
	ZaNewAccountXWizard.GENERAL_STEP = ++this.TAB_INDEX;
	this.stepChoices.push({value:ZaNewAccountXWizard.GENERAL_STEP, label:ZaMsg.TABT_GeneralPage});
	this.cosChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "id", "name");
	var case1 = {type:_CASE_, tabGroupKey:ZaNewAccountXWizard.GENERAL_STEP, caseKey:ZaNewAccountXWizard.GENERAL_STEP, numCols:1,  align:_LEFT_, valign:_TOP_};
	var case1Items = [ 
		 {type: _DWT_ALERT_, ref: ZaAccount.A2_domainLeftAccounts,
                visibilityChecks:[ZaNewAccountXWizard.isDomainLeftAccountsAlertVisible],
                visibilityChangeEventSources:[ZaAccount.A2_domainLeftAccounts,ZaAccount.A2_accountTypes, ZaAccount.A_name],
			    bmolsnr:true,	
                containerCssStyle: "width:400px;",
				style: DwtAlert.WARNING, iconVisible: false
		 },
		{type: _DWT_ALERT_, ref: ZaAccount.A2_warningMessage,
                visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaAccount.A2_warningMessage]],
                visibilityChangeEventSources:[ZaAccount.A2_warningMessage],
                bmolsnr:true,
				containerCssStyle: "width:400px;",
				style: DwtAlert.WARNING, iconVisible: false
		 },
		{type: _DWT_ALERT_, ref: ZaAccount.A2_errorMessage,
                visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaAccount.A2_errorMessage]],
                visibilityChangeEventSources:[ZaAccount.A2_errorMessage],
                bmolsnr:true,
				containerCssStyle: "width:400px;",
				style: DwtAlert.CRITICAL, iconVisible: false
		 },
        //account types group
        {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_AccountTypeGrouper, id:"account_wiz_type_group",
                colSpan: "*", numCols: 1, colSizes: ["100%"],
                visibilityChecks:[ZaNewAccountXWizard.isAccountTypeGrouperVisible,ZaAccount.isShowAccountType],
                visibilityChangeEventSources:[ZaAccount.A2_accountTypes,ZaAccount.A_COSId,ZaAccount.A_name, ZaAccount.A2_showAccountTypeMsg],
                items: [
                    {type: _DWT_ALERT_, 
                    	visibilityChecks:[ZaNewAccountXWizard.isAccountTypeSet, ZaNewAccountXWizard.isAccountsTypeAlertInvisible],
                        visibilityChangeEventSources:[ZaAccount.A2_accountTypes,ZaAccount.A_COSId, ZaAccount.A_name, ZaAccount.A2_showAccountTypeMsg],
                        containerCssStyle: "width:400px;",
                        style: DwtAlert.CRITICAL, iconVisible: false ,
                        content: ZaMsg.ERROR_ACCOUNT_TYPE_NOT_SET
                    },
                    {type: _DWT_ALERT_, ref: ZaAccount.A2_showAccountTypeMsg,
                    	visibilityChecks:[[XForm.checkInstanceValueNotEmty,ZaAccount.A2_showAccountTypeMsg]],
                	visibilityChangeEventSources:[ZaAccount.A2_showAccountTypeMsg, ZaAccount.A_name],
                	bmolsnr:true,
                        containerCssStyle: "width:400px;",
                        style: DwtAlert.WARNING, iconVisible: false
                    },
                    { type: _OUTPUT_, id: ZaNewAccountXWizard.accountTypeItemId,
                        getDisplayValue: ZaAccount.getAccountTypeOutput,
                        valueChangeEventSources:[ZaAccount.A_name,ZaAccount.A_COSId,ZaAccount.A2_accountTypes,ZaAccount.A2_currentAccountType],
                        //center the elements
                        cssStyle: "width: 600px; margin-left: auto; margin-right: auto;"
                    }
               ]
        },
        {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_AccountNameGrouper, id:"account_wiz_name_group",numCols:2,
			items:ZaNewAccountXWizard.getAccountNameInfoItem(),
                displayLabelItem: true, headerLabelWidth:"100px",
                headerItems:[
                    {ref:ZaAccount.A_name, type:_EMAILADDR_,
					 msgName:ZaMsg.NAD_AccountName,
                                        labelLocation:_LEFT_,onChange:ZaAccount.setDomainChanged,forceUpdate:true,
                                        enableDisableChecks:[[ZaItem.hasRight,ZaAccount.RENAME_ACCOUNT_RIGHT]],
                                        visibilityChecks:[]
                     }
                ]
		}
	];
	if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry, 
		[ZaAccount.A_accountStatus, ZaAccount.A_COSId, ZaAccount.A_zimbraIsAdminAccount,ZaAccount.A_mailHost],[])) {
		var setupGroup = {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_AccountSetupGrouper, id:"account_wiz_setup_group", 
			numCols:2,colSizes:["200px","400px"],
			items: [
				{ref:ZaAccount.A_accountStatus, type:_OSELECT1_, msgName:ZaMsg.NAD_AccountStatus,
					label:ZaMsg.NAD_AccountStatus, 
					labelLocation:_LEFT_, choices:this.accountStatusChoices
				}
			],
            headerItems: [
                    {ref:ZaAccount.A_accountStatus, type:_OSELECT1_,
                        bmolsnr:true,
                        labelLocation:_LEFT_, choices:this.accountStatusChoices
                    }
            ], displayLabelItem: true, headerLabelWidth:"100px"
		}
		

		setupGroup.items.push(
			{type:_GROUP_, numCols:3, nowrap:true, label:ZaMsg.NAD_ClassOfService, labelLocation:_LEFT_,
				visibilityChecks:[[ZaItem.hasWritePermission,ZaAccount.A_COSId]],
				id: ZaNewAccountXWizard.cosGroupItemId,
				items: [
					{ref:ZaAccount.A_COSId, type:_DYNSELECT_,label: null, 
						inputPreProcessor:ZaAccountXFormView.preProcessCOS,
						toolTipContent:ZaMsg.tt_StartTypingCOSName,
						onChange:ZaAccount.setCosChanged,
						onClick:ZaController.showTooltip,
						emptyText:ZaMsg.enterSearchTerm,						
						enableDisableChecks:[[ZaNewAccountXWizard.isAutoCos], [ZaItem.hasWritePermission,ZaAccount.A_COSId]],
						enableDisableChangeEventSources:[ZaAccount.A2_autoCos],
						visibilityChecks:[],
						dataFetcherMethod:ZaSearch.prototype.dynSelectSearchCoses,
						choices:this.cosChoices,
						dataFetcherClass:ZaSearch,
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
					{ref:ZaAccount.A2_autoCos, type:_WIZ_CHECKBOX_,
						msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,
						trueValue:"TRUE", falseValue:"FALSE" , subLabel:"",
						elementChanged: function(elementValue,instanceValue, event) {
							if(elementValue=="TRUE") {
								ZaAccount.setDefaultCos(this.getInstance(), this.getForm().parent._app);	
							}
							this.getForm().itemChanged(this, elementValue, event);
						},
                        enableDisableChecks:[ [ZaItem.hasWritePermission,ZaAccount.A_COSId]],
						visibilityChecks:[]
					}
				]
			});
	
		setupGroup.items.push({ref:ZaAccount.A_zimbraIsAdminAccount, type:_WIZ_CHECKBOX_, labelLocation:_RIGHT_,align:_RIGHT_,subLabel:"",
								visibilityChecks:[[ZaItem.hasWritePermission,ZaAccount.A_zimbraIsAdminAccount]],
								msgName:ZaMsg.NAD_IsSystemAdminAccount,label:ZaMsg.NAD_IsSystemAdminAccount,
								bmolsnr:true, trueValue:"TRUE", falseValue:"FALSE"
							});
						
		setupGroup.items.push({type:_GROUP_, numCols:3, nowrap:true, label:ZabMsg.attrDesc_mailHost, labelLocation:_LEFT_,
							visibilityChecks:[[ZaItem.hasWritePermission,ZaAccount.A_mailHost]],
							items: [
								{ ref: ZaAccount.A_mailHost, type: _OSELECT1_, label: null, editable:false, choices: ZaApp.getInstance().getServerListChoices(), 
									enableDisableChecks:[ZaAccount.isAutoMailServer],
									enableDisableChangeEventSources:[ZaAccount.A2_autoMailServer],
									visibilityChecks:[],
									tableCssStyle: "height: 15px"
							  	},
								{ref:ZaAccount.A2_autoMailServer, type:_WIZ_CHECKBOX_, msgName:ZaMsg.NAD_Auto,label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE",
									visibilityChecks:[], labelLocation:_RIGHT_,align:_RIGHT_, subLabel:"",
									enableDisableChecks:[]
								}
							]
						});
						
		case1Items.push(setupGroup);
	}	
	var passwordGroup = {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_PasswordGrouper,id:"account_wiz_password_group", 
		numCols:2,visibilityChecks:[[XForm.checkInstanceValueNot,ZaAccount.A2_isExternalAuth,true]],
        visibilityChangeEventSources:[ZaAccount.A2_isExternalAuth],
		items:[
               	{ type: _DWT_ALERT_, containerCssStyle: "padding-bottom:0px",
                        style: DwtAlert.WARNING,iconVisible: false, 
                        content: ZaMsg.Alert_InternalPassword
                },
		{ref:ZaAccount.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,
			label:ZaMsg.NAD_Password, labelLocation:_LEFT_, 
			visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]], 
			cssClass:"admin_xform_name_input"
		},
		{ref:ZaAccount.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,
			label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_,
			visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]],  
			cssClass:"admin_xform_name_input"
		},
		{ref:ZaAccount.A_zimbraPasswordMustChange,  type:_WIZ_CHECKBOX_, labelLocation:_RIGHT_,align:_RIGHT_, subLabel:"",
			msgName:ZaMsg.NAD_MustChangePwd,label:ZaMsg.NAD_MustChangePwd,trueValue:"TRUE", falseValue:"FALSE",
			visibilityChecks:[], enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
		},
		{ref:ZaAccount.A_zimbraAuthLdapExternalDn,type:_TEXTFIELD_,width:256,
                                msgName:ZaMsg.NAD_AuthLdapExternalDn,label:ZaMsg.NAD_AuthLdapExternalDn, labelLocation:_LEFT_,			      align:_LEFT_, toolTipContent: ZaMsg.tt_AuthLdapExternalDn
		}
		]
	};
	case1Items.push(passwordGroup);														

    var new_acct_timezone_group = {
         type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_TimezoneGrouper, id: "account_wiz_timezone_group",
         visibilityChecks:[[ZaItem.hasWritePermission,ZaAccount.A_zimbraPrefTimeZoneId]],
         numCols: 2,
         items: [
                   /*
                {ref:"default_timezone", type:_CHECKBOX_, msgName:"default",
                    label:ZaMsg.NAD_Auto,labelLocation:_RIGHT_,trueValue:"TRUE", falseValue:"FALSE",
                    elementChanged: function(elementValue,instanceValue, event) {
                        if(elementValue=="TRUE") {
                            ZaAccount.generateDisplayName(this.getInstance(), this.getInstance().attrs[ZaAccount.A_firstName], this.getInstance().attrs[ZaAccount.A_lastName],this.getInstance().attrs[ZaAccount.A_initials]);
                        }
                        this.getForm().itemChanged(this, elementValue, event);
                    }
              }     */
            {ref:ZaAccount.A_zimbraPrefTimeZoneId, type:_SELECT1_, msgName:ZaMsg.LBL_zimbraPrefTimeZoneId,
                 label:ZaMsg.LBL_zimbraPrefTimeZoneId, labelLocation:_LEFT_ }
         ]
    }

    case1Items.push (new_acct_timezone_group) ;
    

	if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_description,ZaAccount.A_notes],[])) {
	    var notesGroup = {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_NotesGrouper, id:"account_wiz_notes_group",
			
			numCols:2,
		 	items:[
				{ref:ZaAccount.A_description, type:_INPUT_, msgName:ZaMsg.NAD_Description,
					label:ZaMsg.NAD_Description, labelLocation:_LEFT_, cssClass:"admin_xform_name_input",
					 visibilityChecks:[[ZaItem.hasWritePermission,ZaAccount.A_description]],
					 enableDisableChecks:[]
				},
				{ref:ZaAccount.A_notes, type:_TEXTAREA_, msgName:ZaMsg.NAD_Notes,
					label:ZaMsg.NAD_Notes, labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", width:"30em",
					visibilityChecks:[[ZaItem.hasWritePermission,ZaAccount.A_notes]],
					enableDisableChecks:[]
				}
			]
		};
	
		case1Items.push(notesGroup);
	}
	case1.items = case1Items;
	cases.push(case1);

	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.CONTACT_TAB_ATTRS, ZaAccountXFormView.CONTACT_TAB_RIGHTS)) {
		ZaNewAccountXWizard.CONTACT_STEP = ++this.TAB_INDEX;
		this.stepChoices.push({value:ZaNewAccountXWizard.CONTACT_STEP, label:ZaMsg.TABT_ContactInfo});
		var case2={type:_CASE_, caseKey:ZaNewAccountXWizard.CONTACT_STEP, tabGroupKey:ZaNewAccountXWizard.CONTACT_STEP, numCols:1, 
						items: [
							{type:_ZAWIZGROUP_, 
								items:[
									{ref:ZaAccount.A_telephoneNumber, type:_TEXTFIELD_, msgName:ZaMsg.NAD_telephoneNumber,label:ZaMsg.NAD_telephoneNumber, labelLocation:_LEFT_, width:250},
                                    {ref:ZaAccount.A_homePhone, type:_TEXTFIELD_, msgName:ZaMsg.NAD_homePhone,label:ZaMsg.NAD_homePhone, labelLocation:_LEFT_, width:250} ,
                                    {ref:ZaAccount.A_mobile, type:_TEXTFIELD_, msgName:ZaMsg.NAD_mobile,label:ZaMsg.NAD_mobile, labelLocation:_LEFT_, width:250} ,
                                    {ref:ZaAccount.A_pager, type:_TEXTFIELD_, msgName:ZaMsg.NAD_pager,label:ZaMsg.NAD_pager, labelLocation:_LEFT_, width:250} ,
                                    {ref:ZaAccount.A_facsimileTelephoneNumber, type:_TEXTFIELD_, msgName:ZaMsg.NAD_facsimileTelephoneNumber,label:ZaMsg.NAD_facsimileTelephoneNumber, labelLocation:_LEFT_, width:250}
								]
							},
							{type:_ZAWIZGROUP_, 
								items:[	
									{ref:ZaAccount.A_zimbraPhoneticCompany, type:_TEXTFIELD_, msgName:ZaMsg.NAD_zimbraPhoneticCompany, label:ZaMsg.NAD_zimbraPhoneticCompany, labelLocation:_LEFT_, width:250, visibilityChecks:[[ZaZimbraAdmin.isLanguage, "ja"]]},				
									{ref:ZaAccount.A_company, type:_TEXTFIELD_, msgName:ZaMsg.NAD_company,label:ZaMsg.NAD_company, labelLocation:_LEFT_, width:250} ,
									{ref:ZaAccount.A_title,  type:_TEXTFIELD_, msgName:ZaMsg.NAD_title,label:ZaMsg.NAD_title, labelLocation:_LEFT_, width:250}
									/*,
									{ref:ZaAccount.A_orgUnit, type:_TEXTFIELD_, msgName:ZaMsg.NAD_orgUnit,label:ZaMsg.NAD_orgUnit, labelLocation:_LEFT_, width:250},														
									{ref:ZaAccount.A_office, type:_TEXTFIELD_, msgName:ZaMsg.NAD_office,label:ZaMsg.NAD_office, labelLocation:_LEFT_, width:250}    */
								]
							},
							{type:_ZAWIZGROUP_, 
								items:ZaAccountXFormView.getAddressFormItemForDialog()
							}							
						]
					};
		cases.push(case2);
	}
	
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.ALIASES_TAB_ATTRS, ZaAccountXFormView.ALIASES_TAB_RIGHTS)) {
		ZaNewAccountXWizard.ALIASES_STEP = ++this.TAB_INDEX;
		this.stepChoices.push({value:ZaNewAccountXWizard.ALIASES_STEP, label:ZaMsg.TABT_Aliases});
		cases.push({type:_CASE_, tabGroupKey:ZaNewAccountXWizard.ALIASES_STEP, caseKey:ZaNewAccountXWizard.ALIASES_STEP, numCols:1, 
					items: [
						{type:_OUTPUT_, value:ZaMsg.NAD_AccountAliases},
						{ref:ZaAccount.A_zimbraMailAlias, type:_REPEAT_, label:null, repeatInstance:emptyAlias, showAddButton:true, 
							showRemoveButton:true, 
							addButtonLabel:ZaMsg.NAD_AddAlias, 
							showAddOnNextRow:true,
							removeButtonLabel:ZaMsg.NAD_RemoveAlias,
							removeButtonCSSStyle: "margin-left: 50px",
							visibilityChecks:[
								[ZaItem.hasWritePermission, ZaAccount.A_zimbraMailAlias]
							],
							items: [
								{ref:".", type:_EMAILADDR_, label:null, enableDisableChecks:[], 
									visibilityChecks:[
										[ZaItem.hasWritePermission, ZaAccount.A_zimbraMailAlias]
									]
								}
							]
						}
					]
				});								
	}

    if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.MEMBEROF_TAB_ATTRS, ZaAccountXFormView.MEMBEROF_TAB_RIGHTS)) {
        var directMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.DIRECT);
	    var indirectMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.INDIRECT, 150);
	    var nonMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.NON);
        ZaNewAccountXWizard.MEMBEROF_STEP = ++this.TAB_INDEX;
        this.stepChoices.push({value:ZaNewAccountXWizard.MEMBEROF_STEP, label:ZaMsg.TABT_MemberOf});
        var memberofCase = {type:_CASE_, caseKey:ZaNewAccountXWizard.MEMBEROF_STEP, tabGroupKey:ZaNewAccountXWizard.MEMBEROF_STEP,
                            numCols:2, colSizes: ["50%","50%"], id:"memberof_step",
                            items: [
                                //layout rapper around the direct/indrect list
                                {type: _GROUP_, width: "98%", numCols: 1, //colSizes: ["auto", 20],
                                    items: [
                                        //direct member group
                                        {type:_ZALEFT_GROUPER_, numCols:1, width: "100%",
                                            label:ZaMsg.Account_DirectGroupLabel,
                                            containerCssStyle: "padding-top:5px",
                                            items:[
                                                {ref: ZaAccount.A2_directMemberList, type: _S_DWT_LIST_, width: "98%", height: 208,
                                                    cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView,
                                                    headerList: directMemberOfHeaderList, defaultColumnSortable: 0,
                                                    onSelection:ZaAccountXFormView.directMemberOfSelectionListener,
                                                    forceUpdate: true }	,
                                                {type:_SPACER_, height:"5"},
                                                {type:_GROUP_, width:"100%", numCols:8, colSizes:[90,5,90, "auto",30,5, 30,5],
                                                    items:[
                                                        {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:90,
                                                            enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableAllButton,ZaAccount.A2_directMemberList]],
                                                            enableDisableChangeEventSources:[ZaAccount.A2_directMemberList],
                                                            onActivate:"ZaAccountMemberOfListView.removeAllGroups.call(this,event, ZaAccount.A2_directMemberList)"
                                                        },
                                                        {type:_CELLSPACER_, height:"100%"},
                                                        {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:90, id:"removeButton",
                                                            enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableAddRemoveButton,ZaAccount.A2_directMemberList]],
                                                            enableDisableChangeEventSources:[ZaAccount.A2_directMemberListSelected],
                                                            onActivate:"ZaAccountMemberOfListView.removeGroups.call(this,event, ZaAccount.A2_directMemberList)"
                                                        },
                                                        {type:_CELLSPACER_,height:"100%"},
                                                        {type:_DWT_BUTTON_, label:"", width:30, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
                                                            onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)",
                                                            enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_directMemberList]],
                                                            enableDisableChangeEventSources:[ZaAccount.A2_directMemberList +"_offset"]
                                                        },
                                                        {type:_CELLSPACER_, height:"100%"},
                                                        {type:_DWT_BUTTON_, label:"", width:30, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
                                                            onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)",
                                                            enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_directMemberList]],
                                                            enableDisableChangeEventSources:[ZaAccount.A2_directMemberList + "_more"]
                                                        },
                                                        {type:_CELLSPACER_, height:"100%"}
                                                    ]
                                                }
                                            ]
                                        },
                                        {type:_SPACER_, height:"5"},
                                        //indirect member group
                                        {type:_ZALEFT_GROUPER_, numCols:1,  width: "100%", label:ZaMsg.Account_IndirectGroupLabel,
                                            containerCssStyle: "padding-top:5px",
                                            items:[
                                                {ref: ZaAccount.A2_indirectMemberList, type: _S_DWT_LIST_, width: "98%", height: 208,
                                                    cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView,
                                                    headerList: indirectMemberOfHeaderList, defaultColumnSortable: 0,
                                                    onSelection:ZaAccountXFormView.indirectMemberOfSelectionListener,
                                                    forceUpdate: true }	,
                                                {type:_SPACER_, height:"5"},
                                                {type:_GROUP_, width:"100%", numCols:5, colSizes:["auto",30,5,30,5],
                                                    items:[
                                                        {type:_CELLSPACER_, height:"100%"},
                                                        {type:_DWT_BUTTON_, label:"", width:30, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
                                                            onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)",
                                                            enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_indirectMemberList]],
                                                            enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList + "_offset"]
                                                        },
                                                        {type:_CELLSPACER_, height:"100%"},
                                                        {type:_DWT_BUTTON_, label:"", width:30, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
                                                            onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)",
                                                            enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_indirectMemberList]],
                                                            enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList + "_more"]
                                                        },
                                                        {type:_CELLSPACER_, height:"100%"}
                                                    ]
                                                }
                                            ]
                                        }
                                    ]
                                },

                                {type: _GROUP_, width: "98%", numCols: 1, //colSizes: ["auto", 20],
					            items: [
                                    {type:_ZARIGHT_GROUPER_, numCols:1, width: "100%", label:ZaMsg.Account_NonGroupLabel,
                                        containerCssStyle: "padding-top:5px",
                                        items:[
                                            {type:_GROUP_, numCols:3, colSizes:["40", "auto", "80"], width:"98%",
                                               items:[
                                                    {ref:"query", type:_TEXTFIELD_, width:"100%", cssClass:"admin_xform_name_input",
                                                        nowrap:false,labelWrap:true,
                                                        label:ZaMsg.DLXV_LabelFind,
                                                        visibilityChecks:[],enableDisableChecks:[],
                                                        elementChanged: function(elementValue,instanceValue, event) {
                                                          var charCode = event.charCode;
                                                          if (charCode == 13 || charCode == 3) {
                                                              ZaAccountMemberOfListView.prototype.srchButtonHndlr.call(this);
                                                          } else {
                                                              this.getForm().itemChanged(this, elementValue, event);
                                                          }
                                                        }
                                                    },
                                                    {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonSearch, width:80,
                                                       onActivate:ZaAccountMemberOfListView.prototype.srchButtonHndlr
                                                    },
                                                    {ref: ZaAccount.A2_showSameDomain, type: _WIZ_CHECKBOX_, labelLocation:_RIGHT_,align:_RIGHT_, subLabel:"",
                                                            label:null,labelLocation:_NONE_, trueValue:"TRUE", falseValue:"FALSE",
                                                            visibilityChecks:[]
                                                    },
                                                    {type:_OUTPUT_, value:ZaMsg.NAD_SearchSameDomain,colSpan:2}
                                                ]
                                            },

                                            {ref: ZaAccount.A2_nonMemberList, type: _S_DWT_LIST_, width: "98%", height: 460,
                                                cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView,
                                                headerList: nonMemberOfHeaderList, defaultColumnSortable: 0,
                                                onSelection:ZaAccountXFormView.nonMemberOfSelectionListener,
                                                forceUpdate: true },

                                            {type:_SPACER_, height:"5"},
                                            //add action buttons
                                            {type:_GROUP_, width:"100%", numCols:8, colSizes:[90,5,90,"auto",30,5,30,5],
                                                items: [
                                                    {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, width:90,
                                                        enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableAddRemoveButton,ZaAccount.A2_nonMemberList]],
                                                        enableDisableChangeEventSources:[ZaAccount.A2_nonMemberListSelected],
                                                        onActivate:"ZaAccountMemberOfListView.addGroups.call(this,event, ZaAccount.A2_nonMemberList)"
                                                    },
                                                    {type:_CELLSPACER_, height:"100%"},
                                                    {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:90,
                                                        enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList],
                                                        enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableAllButton,ZaAccount.A2_nonMemberList]],
                                                        onActivate:"ZaAccountMemberOfListView.addAllGroups.call(this,event, ZaAccount.A2_nonMemberList)"
                                                    },
                                                    {type:_CELLSPACER_, height:"100%"},
                                                    {type:_DWT_BUTTON_, label:"", width:30, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
                                                        enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList + "_offset"],
                                                        enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_nonMemberList]],
                                                        onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"
                                                    },
                                                    {type:_CELLSPACER_, height:"100%"},
                                                    {type:_DWT_BUTTON_, label:"", width:30, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
                                                        enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList + "_more"],
                                                        enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_nonMemberList]],
                                                        onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"
                                                    },
                                                    {type:_CELLSPACER_, height:"100%"}
                                                  ]
                                            }
                                        ]
                                    }
				                 ]
                                },
                                {type: _GROUP_, width: "100%", items: [
                                        {type:_CELLSPACER_}
                                    ]
                                }
                            ]
				};
        cases.push(memberofCase);
    }
	var zimbraFeatureMailForwardingEnabledItem = 
				{ref:ZaAccount.A_zimbraFeatureMailForwardingEnabled, 
					msgName:ZaMsg.LBL_zimbraFeatureMailForwardingEnabled,
					trueValue:"TRUE", falseValue:"FALSE"
				}


	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.FORWARDING_TAB_ATTRS, ZaAccountXFormView.FORWARDING_TAB_RIGHTS)) {
		ZaNewAccountXWizard.FORWARDING_STEP = ++this.TAB_INDEX;		
		this.stepChoices.push({value:ZaNewAccountXWizard.FORWARDING_STEP, label:ZaMsg.TABT_Forwarding});

		cases.push({type:_CASE_, caseKey:ZaNewAccountXWizard.FORWARDING_STEP, tabGroupKey:ZaNewAccountXWizard.FORWARDING_STEP, numCols:2,colSizes:["250px","auto"], 
					id:"account_form_forwarding_step",
					items: [
						{
							ref:ZaAccount.A_zimbraFeatureMailForwardingEnabled,
							resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
							type:_SUPER_WIZ_CHECKBOX_, colSpan:2,
							checkBoxLabel:ZaMsg.LBL_zimbraFeatureMailForwardingEnabled,  
							trueValue:"TRUE", falseValue:"FALSE",
							colSizes:["250px","250px","auto"]
						},
						{ref:ZaAccount.A_zimbraPrefMailLocalDeliveryDisabled, 
							type:_WIZ_CHECKBOX_,
							msgName:ZaMsg.LBL_zimbraPrefMailLocalDeliveryDisabled,
							label:ZaMsg.LBL_zimbraPrefMailLocalDeliveryDisabled, 
							trueValue:"TRUE", falseValue:"FALSE"
						},
						{ref:ZaAccount.A_zimbraPrefMailForwardingAddress,width:250,
							labelCssClass:"xform_label",
							type:_TEXTFIELD_, msgName:ZaMsg.LBL_zimbraPrefMailForwardingAddress,
							label:ZaMsg.LBL_zimbraPrefMailForwardingAddress, labelLocation:_LEFT_, 
							cssClass:"admin_xform_name_input",
							nowrap:false,labelWrap:true, 
							enableDisableChecks:[ZaAccountXFormView.isMailForwardingEnabled],
							enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureMailForwardingEnabled, ZaAccount.A_COSId]
						},		
						{type:_SEPARATOR_,colSpan:2},
                        {type: _DWT_ALERT_, colSpan: 2,
                                            containerCssStyle: "padding:10px;padding-top: 0px; width:100%;",
                                            style: DwtAlert.WARNING,
                                            iconVisible: true,
                                            content: ZaMsg.Alert_Bouncing_Reveal_Hidden_Adds
                                        },    
                        {ref:ZaAccount.A_zimbraMailForwardingAddress, type:_REPEAT_,
							label:ZaMsg.NAD_EditFwdGroup, labelLocation:_LEFT_,labelCssClass:"xform_label",
							repeatInstance:emptyAlias, 
							showAddButton:true, showRemoveButton:true, 
							addButtonLabel:ZaMsg.NAD_AddAddress, 
							showAddOnNextRow:true,
							removeButtonLabel:ZaMsg.NAD_RemoveAddress,
							nowrap:false,labelWrap:true,
							items: [
								{ref:".", type:_TEXTFIELD_, label:null, width:250, enableDisableChecks:[],
								visibilityChecks:[[ZaItem.hasWritePermission, ZaAccount.A_zimbraMailForwardingAddress]]}
							]
						},
						{ref:ZaAccount.A_zimbraPrefCalendarForwardInvitesTo, type:_REPEAT_,
							label:ZaMsg.zimbraPrefCalendarForwardInvitesTo, labelLocation:_LEFT_,labelCssClass:"xform_label",
							repeatInstance:emptyAlias, 
							showAddButton:true, showRemoveButton:true, 
							addButtonLabel:ZaMsg.NAD_AddAddress, 
							showAddOnNextRow:true,
							removeButtonLabel:ZaMsg.NAD_RemoveAddress,
							nowrap:false,labelWrap:true,
							items: [
								{ref:".", type:_TEXTFIELD_, label:null, width:250, enableDisableChecks:[],
								visibilityChecks:[[ZaItem.hasWritePermission, ZaAccount.A_zimbraPrefCalendarForwardInvitesTo]]}
							]
						}						
					]
				});				
	};
		
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.FEATURE_TAB_ATTRS, ZaAccountXFormView.FEATURE_TAB_RIGHTS)) {
		ZaNewAccountXWizard.FEATURES_STEP = ++this.TAB_INDEX;		
		this.stepChoices.push({value:ZaNewAccountXWizard.FEATURES_STEP, label:ZaMsg.TABT_Features});
		var featuresCase = {type:_CASE_,caseKey:ZaNewAccountXWizard.FEATURES_STEP, tabGroupKey:ZaNewAccountXWizard.FEATURES_STEP,id:"account_form_features_step",
				numCols:1, width:"100%",
				items: [
					{ type: _DWT_ALERT_,
					  containerCssStyle: "padding-top:20px;width:400px;",
					  style: DwtAlert.WARNING,
					  iconVisible: false, 
					  content: ZaMsg.NAD_CheckFeaturesInfo
					}
				]
		}; 
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraFeatureMailEnabled,ZaAccount.A_zimbraFeatureContactsEnabled,
			ZaAccount.A_zimbraFeatureCalendarEnabled,ZaAccount.A_zimbraFeatureTasksEnabled,ZaAccount.A_zimbraFeatureTasksEnabled,
			/*ZaAccount.A_zimbraFeatureNotebookEnabled, */ ZaAccount.A_zimbraFeatureBriefcasesEnabled,ZaAccount.A_zimbraFeatureIMEnabled,
			ZaAccount.A_zimbraFeatureOptionsEnabled],[])) {
			featuresCase.items.push({type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_zimbraMajorFeature, id:"account_wiz_features_major", colSizes:["auto"],numCols:1,
						items:[	
							{ref:ZaAccount.A_zimbraFeatureMailEnabled,
								type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureMailEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureMailEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},	
							{ref:ZaAccount.A_zimbraFeatureContactsEnabled,type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureContactsEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureContactsEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE"},							
							{ref:ZaAccount.A_zimbraFeatureCalendarEnabled,
								type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureCalendarEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureCalendarEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},														
							{ref:ZaAccount.A_zimbraFeatureTasksEnabled,
								type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureTaskEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureTaskEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"},														
							//{ref:ZaAccount.A_zimbraFeatureNotebookEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureNotebookEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureNotebookEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureBriefcasesEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureBriefcasesEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureBriefcasesEnabled,  trueValue:"TRUE", falseValue:"FALSE"},							
							//{ref:ZaAccount.A_zimbraFeatureIMEnabled,
							//	type:_SUPER_WIZ_CHECKBOX_, 
							//	resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
							//	msgName:ZaMsg.LBL_zimbraFeatureIMEnabled,
							//	checkBoxLabel:ZaMsg.LBL_zimbraFeatureIMEnabled,  
							//	trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureOptionsEnabled,
								type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureOptionsEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureOptionsEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"}	
						]
					});
		};
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraFeatureTaggingEnabled,ZaAccount.A_zimbraFeatureSharingEnabled,
			ZaAccount.A_zimbraFeatureChangePasswordEnabled,ZaAccount.A_zimbraFeatureSkinChangeEnabled,ZaAccount.A_zimbraFeatureManageZimlets,
			//ZaAccount.A_zimbraFeatureHtmlComposeEnabled,ZaAccount.A_zimbraFeatureShortcutAliasesEnabled,
			ZaAccount.A_zimbraFeatureGalEnabled,ZaAccount.A_zimbraFeatureMAPIConnectorEnabled,ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled,
			ZaAccount.A_zimbraFeatureImportFolderEnabled, ZaAccount.A_zimbraFeatureExportFolderEnabled, ZaAccount.A_zimbraDumpsterEnabled],[])) {
			featuresCase.items.push({type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_zimbraGeneralFeature, id:"account_wiz_features_general",
						 colSizes:["auto"],numCols:1,
						items:[							
							{ref:ZaAccount.A_zimbraFeatureTaggingEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureTaggingEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureTaggingEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSharingEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureSharingEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureSharingEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureChangePasswordEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureChangePasswordEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureChangePasswordEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSkinChangeEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureSkinChangeEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureSkinChangeEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureManageZimlets, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureManageZimlets,checkBoxLabel:ZaMsg.LBL_zimbraFeatureManageZimlets, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureHtmlComposeEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureHtmlComposeEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureHtmlComposeEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},							
							/*{ref:ZaAccount.A_zimbraFeatureShortcutAliasesEnabled, 
								type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureShortcutAliasesEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureShortcutAliasesEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},*/
							{ref:ZaAccount.A_zimbraFeatureGalEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureGalEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureGalEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureMAPIConnectorEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureMAPIConnectorEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureMAPIConnectorEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureGalAutoCompleteEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureGalAutoCompleteEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureGalAutoCompleteEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureImportFolderEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureImportFolderEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureImportFolderEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                            {ref:ZaAccount.A_zimbraFeatureExportFolderEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureExportFolderEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureExportFolderEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraDumpsterEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraDumpsterEnabled,checkBoxLabel:ZaMsg.LBL_zimbraDumpsterEnabled,  trueValue:"TRUE", falseValue:"FALSE"}
						]
					});
			
		};		
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraFeatureMailPriorityEnabled,ZaAccount.A_zimbraFeatureFlaggingEnabled,
			ZaAccount.A_zimbraImapEnabled,ZaAccount.A_zimbraPop3Enabled,ZaAccount.A_zimbraFeatureImapDataSourceEnabled,
			ZaAccount.A_zimbraFeaturePop3DataSourceEnabled,ZaAccount.A_zimbraFeatureConversationsEnabled,ZaAccount.A_zimbraFeatureFiltersEnabled,
			ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled,ZaAccount.A_zimbraFeatureNewMailNotificationEnabled,ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, 
			ZaAccount.A_zimbraFeatureMailSendLaterEnabled,ZaAccount.A_zimbraFeatureIdentitiesEnabled,ZaAccount.A_zimbraFeatureReadReceiptsEnabled],[])) {
			featuresCase.items.push({type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_zimbraMailFeature, id:"account_wiz_features_mail",
						colSizes:["auto"],numCols:1,
					 	enableDisableChecks:[ZaAccountXFormView.isMailFeatureEnabled],
						enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureMailEnabled, ZaAccount.A_COSId],
						items:[													
							{ref:ZaAccount.A_zimbraFeatureMailPriorityEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureMailPriorityEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureMailPriorityEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureFlaggingEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureFlaggingEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureFlaggingEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraImapEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraImapEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraImapEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraPop3Enabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPop3Enabled,
								checkBoxLabel:ZaMsg.LBL_zimbraPop3Enabled,  trueValue:"TRUE", falseValue:"FALSE"},		
							{ref:ZaAccount.A_zimbraFeatureImapDataSourceEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraExternalImapEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraExternalImapEnabled,  trueValue:"TRUE", falseValue:"FALSE"},		
							{ref:ZaAccount.A_zimbraFeaturePop3DataSourceEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraExternalPop3Enabled,
								checkBoxLabel:ZaMsg.LBL_zimbraExternalPop3Enabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureMailSendLaterEnabled, type:_SUPER_WIZ_CHECKBOX_,
                                                                resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureMailSendLaterEnabled,
                                                                checkBoxLabel:ZaMsg.LBL_zimbraFeatureMailSendLaterEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
		
							{ref:ZaAccount.A_zimbraFeatureConversationsEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureConversationsEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureConversationsEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureFiltersEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureFiltersEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureFiltersEnabled,trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureOutOfOfficeReplyEnabled, type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureOutOfOfficeReplyEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureOutOfOfficeReplyEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureNewMailNotificationEnabled, 
								type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureNewMailNotificationEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureNewMailNotificationEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureMailPollingIntervalPreferenceEnabled, 
								type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureMailPollingIntervalPreferenceEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureMailPollingIntervalPreferenceEnabled, 
								trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureIdentitiesEnabled,
								type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								msgName:ZaMsg.LBL_zimbraFeatureIdentitiesEnabled,
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureIdentitiesEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"
							},
							{ref:ZaAccount.A_zimbraFeatureReadReceiptsEnabled,
								type:_SUPER_WIZ_CHECKBOX_, 
								resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
								checkBoxLabel:ZaMsg.LBL_zimbraFeatureReadReceiptsEnabled,  
								trueValue:"TRUE", falseValue:"FALSE"
							}
						]
					});
			
		};
		if  (ZAWizTopGrouper_XFormItem.isGroupVisible(
					entry,
					[ZaAccount.A_zimbraFeatureGroupCalendarEnabled,
						//ZaAccount.A_zimbraFeatureFreeBusyViewEnabled,
						ZaAccount.A_zimbraFeatureCalendarReminderDeviceEmailEnabled
					],
					[]
				)
			)
		{
			featuresCase.items.push(
				{type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_zimbraCalendarFeature, id:"account_wiz_features_calendar",
				 	colSizes:["auto"],numCols:1,
				 	enableDisableChecks:[ZaAccountXFormView.isCalendarFeatureEnabled],
					enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureCalendarEnabled,ZaAccount.A_COSId],
					items:[		
						{ref:ZaAccount.A_zimbraFeatureGroupCalendarEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureGroupCalendarEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureGroupCalendarEnabled, trueValue:"TRUE", falseValue:"FALSE"},
						//{ref:ZaAccount.A_zimbraFeatureFreeBusyViewEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureFreeBusyViewEnabled, checkBoxLabel:ZaMsg.LBL_zimbraFeatureFreeBusyViewEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                        {ref:ZaAccount.A_zimbraFeatureCalendarReminderDeviceEmailEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureCalendarReminderDeviceEmailEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureCalendarReminderDeviceEmailEnabled, trueValue:"TRUE", falseValue:"FALSE"}
					]
				}
			);
		};	
	//	if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraFeatureIMEnabled],[])) {
	//		featuresCase.items.push(
	//			{type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_zimbraIMFeature, id:"account_wiz_features_im",
	//				 	colSizes:["auto"],numCols:1,
	//					visibilityChecks:[ZaAccountXFormView.isIMFeatureEnabled],
	//					visibilityChangeEventSources:[ZaAccount.A_zimbraFeatureIMEnabled,ZaAccount.A_COSId],
	//					items:[			
	//						{ref:ZaAccount.A_zimbraFeatureInstantNotify, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureInstantNotify,checkBoxLabel:ZaMsg.LBL_zimbraFeatureInstantNotify, trueValue:"TRUE", falseValue:"FALSE"}
	//					]
	//			}
	//		);
	//	};

		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ //ZaAccount.A_zimbraFeatureAdvancedSearchEnabled,
			ZaAccount.A_zimbraFeatureAdvancedSearchEnabled,
			ZaAccount.A_zimbraFeatureSavedSearchesEnabled,
			ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled,
			ZaAccount.A_zimbraFeaturePeopleSearchEnabled
			],[])) {
			featuresCase.items.push(
				{type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_zimbraSearchFeature, id:"account_wiz_features_search",
					 colSizes:["auto"],numCols:1,
						items:[
							{ref:ZaAccount.A_zimbraFeatureAdvancedSearchEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureAdvancedSearchEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureAdvancedSearchEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureSavedSearchesEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureSavedSearchesEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureSavedSearchesEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeatureInitialSearchPreferenceEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeatureInitialSearchPreferenceEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeatureInitialSearchPreferenceEnabled, trueValue:"TRUE", falseValue:"FALSE"},
							{ref:ZaAccount.A_zimbraFeaturePeopleSearchEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraFeaturePeopleSearchEnabled,checkBoxLabel:ZaMsg.LBL_zimbraFeaturePeopleSearchEnabled, trueValue:"TRUE", falseValue:"FALSE"}
						]
				}
			);
		};
                if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraFeatureManageSMIMECertificateEnabled, ZaAccount.A_zimbraFeatureSMIMEEnabled],[])) {
                        featuresCase.items.push(
                                {type:_ZAWIZ_TOP_GROUPER_, label:ZaMsg.NAD_zimbraSMIMEFeature, id:"account_wiz_features_smime",
                                        colSizes:["auto"],numCols:1,                                        
                                        items:[
                                                {ref:ZaAccount.A_zimbraFeatureSMIMEEnabled, type:_SUPER_WIZ_CHECKBOX_,
                                                resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                                msgName:ZaMsg.LBL_zimbraFeatureSMIMEEnabled,
                                                checkBoxLabel:ZaMsg.LBL_zimbraFeatureSMIMEEnabled,
                                                trueValue:"TRUE", falseValue:"FALSE"},

                                                {ref:ZaAccount.A_zimbraFeatureManageSMIMECertificateEnabled, type:_SUPER_WIZ_CHECKBOX_,
 						resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
						msgName:ZaMsg.LBL_zimbraFeatureManageSMIMECertificateEnabled, 
						checkBoxLabel:ZaMsg.LBL_zimbraFeatureManageSMIMECertificateEnabled,  
						trueValue:"TRUE", falseValue:"FALSE"}
                                        ]
                                }
                        );
                };	

		cases.push(featuresCase);
	}



	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.PREFERENCES_TAB_ATTRS, ZaAccountXFormView.PREFERENCES_TAB_RIGHTS)) {
		ZaNewAccountXWizard.PREFS_STEP = ++this.TAB_INDEX;	
		this.stepChoices.push({value:ZaNewAccountXWizard.PREFS_STEP, label:ZaMsg.TABT_Preferences});

		var prefItems = [ ];
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraPrefImapSearchFoldersEnabled,ZaAccount.A_zimbraPrefShowSearchString,
			ZaAccount.A_zimbraPrefUseKeyboardShortcuts,ZaAccount.A_zimbraPrefMailInitialSearch,
			ZaAccount.A_zimbraPrefWarnOnExit,ZaAccount.A_zimbraPrefAdminConsoleWarnOnExit, ZaAccount.A_zimbraPrefShowSelectionCheckbox,
			ZaAccount.A_zimbraJunkMessagesIndexingEnabled,ZaAccount.A_zimbraPrefLocale],[])) {
			

			prefItems.push({type:_ZAWIZ_TOP_GROUPER_, id:"account_prefs_general",
                            label:ZaMsg.NAD_GeneralOptions,
							items :[
                                    {ref:ZaAccount.A_zimbraPrefImapSearchFoldersEnabled, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefImapSearchFoldersEnabled,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefImapSearchFoldersEnabled,  trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefShowSearchString, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefShowSearchString,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefShowSearchString,trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefUseKeyboardShortcuts, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZaMsg.LBL_zimbraPrefUseKeyboardShortcuts,
                                        trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefClientType, type:_SUPERWIZ_SELECT1_,
                                        colSizes:["300px", "*"],
                                        msgName:ZaMsg.MSG_zimbraPrefClientType,
                                        label:ZaMsg.LBL_zimbraPrefClientType, labelLocation:_LEFT_,
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS
                                    },
                                    {ref:ZaAccount.A_zimbraPrefMailInitialSearch, type:_SUPERWIZ_TEXTFIELD_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        msgName:ZaMsg.LBL_zimbraPrefMailInitialSearch,
                                        txtBoxLabel:ZaMsg.LBL_zimbraPrefMailInitialSearch,
                                        labelLocation:_LEFT_,
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS
                                    },
                                    {ref:ZaAccount.A_zimbraPrefWarnOnExit, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZaMsg.LBL_zimbraPrefWarnOnExit,
                                        labelWrap: true,trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefAdminConsoleWarnOnExit, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZabMsg.LBL_zimbraPrefAdminConsoleWarnOnExit,
                                        labelWrap: true,trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefShowSelectionCheckbox, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZaMsg.LBL_zimbraPrefShowSelectionCheckbox,
                                        trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraJunkMessagesIndexingEnabled, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,checkBoxLabel:ZaMsg.LBL_zimbraJunkMessagesIndexingEnabled,
                                        trueValue:"TRUE", falseValue:"FALSE"}	,
                                    {ref:ZaAccount.A_zimbraPrefLocale, type:_SUPERWIZ_SELECT1_, msgName:ZaMsg.LBL_zimbraPrefMailLocale,
                                        choices: ZaSettings.getLocaleChoices(),
                                        colSizes:["300px", "*"],
                                        label:ZaMsg.LBL_zimbraPrefLocale, labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS}
                                    ]
						});
		}
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraPrefMailItemsPerPage,ZaAccount.A_zimbraMaxMailItemsPerPage],[])) {				
			prefItems.push({type:_ZAWIZ_TOP_GROUPER_, id:"account_prefs_standard_client",borderCssClass:"LowPadedTopGrouperBorder",
							label:ZaMsg.NAD_MailOptionsStandardClient,
							items :[
								{ref:ZaAccount.A_zimbraMaxMailItemsPerPage, type:_SUPERWIZ_SELECT1_,
                                    colSizes:["300px", "*"],
									editable:true,inputSize:4,choices:[10,25,50,100,250,500,1000],
									msgName:ZaMsg.MSG_zimbraMaxMailItemsPerPage,label:ZaMsg.LBL_zimbraMaxMailItemsPerPage,
									labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null
								},								
								{ref:ZaAccount.A_zimbraPrefMailItemsPerPage, type:_SUPERWIZ_SELECT1_,
                                    colSizes:["300px", "*"],
									msgName:ZaMsg.MSG_zimbraPrefMailItemsPerPage,label:ZaMsg.LBL_zimbraPrefMailItemsPerPage,
									labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null
								}							
							]
			});
			prefItems.push({type: _SPACER_ , height: "10px" });
		}			
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraPrefMessageViewHtmlPreferred,ZaAccount.A_zimbraPrefDisplayExternalImages,ZaAccount.A_zimbraPrefMailToasterEnabled,
            ZaAccount.A_zimbraPrefMessageIdDedupingEnabled,
			ZaAccount.A_zimbraPrefGroupMailBy,ZaAccount.A_zimbraPrefMailDefaultCharset,ZaAccount.A_zimbraPrefItemsPerVirtualPage],[])) {				

			prefItems.push({type:_ZAWIZ_TOP_GROUPER_, id:"account_prefs_mail_general",
                            label:ZaMsg.NAD_MailOptions,
                            items: [
                                     {ref:ZaAccount.A_zimbraPrefMessageViewHtmlPreferred,
                                        type:_SUPER_WIZ_CHECKBOX_, colSpan:2,
                                        colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                        msgName:ZaMsg.LBL_zimbraPrefMessageViewHtmlPreferred,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefMessageViewHtmlPreferred,
                                        trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefDisplayExternalImages,
                                        type:_SUPER_WIZ_CHECKBOX_, colSpan:2,
                                        colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                        msgName:ZaMsg.LBL_zimbraPrefDisplayExternalImages,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefDisplayExternalImages,
                                        trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefGroupMailBy, type:_SUPERWIZ_SELECT1_,
                                        colSizes:["300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefGroupMailBy,
                                        label:ZaMsg.LBL_zimbraPrefGroupMailBy, labelLocation:_LEFT_
                                    },
                                    {ref:ZaAccount.A_zimbraPrefMailDefaultCharset, type:_SUPERWIZ_SELECT1_,
                                        colSizes:["300px", "*"],
                                        msgName:ZaMsg.LBL_zimbraPrefMailDefaultCharset,
                                        label:ZaMsg.LBL_zimbraPrefMailDefaultCharset, labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS},
                                    {ref:ZaAccount.A_zimbraPrefMailToasterEnabled,
                                        type:_SUPER_WIZ_CHECKBOX_, colSpan:2,
                                        colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                        msgName:ZaMsg.MSG_zimbraPrefMailToasterEnabled,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefMailToasterEnabled,
                                        trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefMessageIdDedupingEnabled,
                                        type:_SUPER_WIZ_CHECKBOX_, colSpan:2,
                                        colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                        msgName:ZaMsg.MSG_zimbraPrefMessageIdDedupingEnabled,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefMessageIdDedupingEnabled,
                                        trueValue:"TRUE", falseValue:"FALSE"},
{ref:ZaAccount.A_zimbraPrefItemsPerVirtualPage,
         type:_SUPERWIZ_TEXTFIELD_,
         colSizes:["200px", "*"],	colSpan:2,nowrap:false,labelWrap:true,
         resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
         msgName:ZaMsg.LBL_zimbraPrefItemsPerVirtualPage,
         txtBoxLabel:ZaMsg.LBL_zimbraPrefItemsPerVirtualPage      
 
}

                                ]
						});
		}
	
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraPrefMailPollingInterval,ZaAccount.A_zimbraMailMinPollingInterval,
			ZaAccount.A_zimbraPrefNewMailNotificationEnabled,ZaAccount.A_zimbraPrefNewMailNotificationAddress,ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled,
			ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled,ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration,
			ZaAccount.A_zimbraPrefOutOfOfficeReply,ZaAccount.A_zimbraPrefReadReceiptsToAddress,ZaAccount.A_zimbraPrefMailSendReadReceipts],[])) {				
			prefItems.push(				
						{type:_ZAWIZ_TOP_GROUPER_, id:"account_prefs_mail_receiving",
							label:ZaMsg.NAD_MailOptionsReceiving,
							items :[
								{ref:ZaAccount.A_zimbraPrefMailPollingInterval, type:_SUPERWIZ_LIFETIME_,
									colSizes:["200px","130px","170px","*"],
									msgName:ZaMsg.MSG_zimbraPrefMailPollingInterval,
									txtBoxLabel:ZaMsg.LBL_zimbraPrefMailPollingInterval, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:2,
									nowrap:false,labelWrap:true									
								},							
								{ref:ZaAccount.A_zimbraMailMinPollingInterval, 
									type:_SUPERWIZ_LIFETIME_, colSizes:["200px","130px","170px","*"],
									msgName:ZaMsg.MSG_zimbraMailMinPollingInterval,
									txtBoxLabel:ZaMsg.LBL_zimbraMailMinPollingInterval, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									colSpan:2,nowrap:false,labelWrap:true	
								},
								{ref:ZaAccount.A_zimbraPrefNewMailNotificationEnabled, 
									type:_WIZ_CHECKBOX_,
									msgName:ZaMsg.LBL_zimbraPrefNewMailNotificationEnabled,
									label:ZaMsg.LBL_zimbraPrefNewMailNotificationEnabled,
									trueValue:"TRUE", falseValue:"FALSE"
								},
								{ref:ZaAccount.A_zimbraPrefNewMailNotificationAddress, type:_TEXTFIELD_, 
									msgName:ZaMsg.MSG_zimbraPrefNewMailNotificationAddress,
									label:ZaMsg.LBL_zimbraPrefNewMailNotificationAddress, 
									labelLocation:_LEFT_,
									enableDisableChecks:[ZaAccountXFormView.isMailNotificationAddressEnabled],
									enableDisableChangeEventSources:[ZaAccount.A_zimbraPrefNewMailNotificationEnabled]
								},
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled, 
									type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraPrefOutOfOfficeReplyEnabled,label:ZaMsg.LBL_zimbraPrefOutOfOfficeReplyEnabled, trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeCacheDuration, 
									type:_SUPERWIZ_LIFETIME_, colSizes:["200px","130px","170px","*"],
									msgName:ZaMsg.LBL_zimbraPrefOutOfOfficeCacheDuration,
									txtBoxLabel:ZaMsg.LBL_zimbraPrefOutOfOfficeCacheDuration, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									colSpan:2,nowrap:false,labelWrap:true	
								},
								{ref:ZaAccount.A_zimbraPrefOutOfOfficeReply, type:_TEXTAREA_, 
									msgName:ZaMsg.LBL_zimbraPrefOutOfOfficeReply,
									label:ZaMsg.LBL_zimbraPrefOutOfOfficeReply, 
									labelLocation:_LEFT_, labelCssStyle:"vertical-align:top", width:"30em",
									enableDisableChecks:[ZaAccountXFormView.isOutOfOfficeReplyEnabled],
									enableDisableChangeEventSources:[ZaAccount.A_zimbraPrefOutOfOfficeReplyEnabled]
								},
								{ref:ZaAccount.A_zimbraPrefMailSendReadReceipts, 
									type:_SUPERWIZ_SELECT1_,
                                    colSizes:["300px", "*"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									label:ZaMsg.LBL_zimbraPrefMailSendReadReceipts, 
									nowrap:false,labelWrap:true,
									enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureReadReceiptsEnabled,"TRUE"],
										[ZaItem.hasWritePermission,ZaAccount.A_zimbraPrefMailSendReadReceipts]],
									enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureReadReceiptsEnabled]									
								},	
								{ref:ZaAccount.A_zimbraPrefReadReceiptsToAddress, type:_TEXTFIELD_, 
									label:ZaMsg.LBL_zimbraPrefReadReceiptsToAddress,
									nowrap:false,labelWrap:true,
									msgName:ZaMsg.LBL_zimbraPrefReadReceiptsToAddress,
									labelLocation:_LEFT_, cssClass:"admin_xform_name_input", width:150,
									enableDisableChecks:[[XForm.checkInstanceValue,ZaAccount.A_zimbraFeatureReadReceiptsEnabled,"TRUE"],
										[ZaItem.hasWritePermission,ZaAccount.A_zimbraPrefReadReceiptsToAddress]],
									enableDisableChangeEventSources:[ZaAccount.A_zimbraFeatureReadReceiptsEnabled]									
								}															
							]
						});
		}
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_prefSaveToSent,ZaAccount.A_zimbraAllowAnyFromAddress, 
			ZaAccount.A_zimbraAllowFromAddress],[])) {				
			prefItems.push({type:_ZAWIZ_TOP_GROUPER_, id:"account_prefs_mail_sending",borderCssClass:"LowPadedTopGrouperBorder",
							label:ZaMsg.NAD_MailOptionsSending,
							items :[
								{ref:ZaAccount.A_zimbraPrefSaveToSent,  
									colSpan:2, colSizes:["200px","300px","*"],
									type:_SUPER_WIZ_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefSaveToSent,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefSaveToSent,
									trueValue:"TRUE", falseValue:"FALSE"},
									
								{ref:ZaAccount.A_zimbraAllowAnyFromAddress,  
									colSpan:2, colSizes:["200px","300px","*"],
									type:_SUPER_WIZ_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraAllowAnyFromAddress,
									checkBoxLabel:ZaMsg.LBL_zimbraAllowAnyFromAddress,
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraAllowFromAddress,
									type:_REPEAT_,
									label:ZaMsg.LBL_zimbraAllowFromAddress,
									labelLocation:_LEFT_, 
									addButtonLabel:ZaMsg.NAD_AddAddress, 
									align:_LEFT_,
									repeatInstance:emptyAlias, 
									showAddButton:true, 
									showRemoveButton:true, 
									showAddOnNextRow:true, 
									removeButtonLabel:ZaMsg.NAD_RemoveAddress,								
									items: [
										{ref:".", type:_TEXTFIELD_, label:null, width:"200px", enableDisableChecks:[],visibilityChecks:[]}
									],
									nowrap:false,labelWrap:true,
									enableDisableChecks:[],
									visibilityChecks:[ZaAccountXFormView.isSendingFromAnyAddressDisAllowed,[ZaItem.hasWritePermission,ZaAccount.A_zimbraAllowFromAddress]],
									visibilityChangeEventSources:[ZaAccount.A_zimbraAllowAnyFromAddress, ZaAccount.A_zimbraAllowFromAddress, ZaAccount.A_COSId]										
								}
							]
						});
		}
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraPrefComposeInNewWindow,ZaAccount.A_zimbraPrefComposeFormat,
			ZaAccount.A_zimbraPrefHtmlEditorDefaultFontFamily,ZaAccount.A_zimbraPrefHtmlEditorDefaultFontSize,
			ZaAccount.A_zimbraPrefHtmlEditorDefaultFontColor,ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat,
			ZaAccount.A_zimbraPrefMailSignatureEnabled,/*ZaAccount.A_zimbraPrefMailSignatureStyle,*/
			ZaAccount.A_zimbraMailSignatureMaxLength,ZaAccount.A_zimbraPrefMailSignature,
			ZaAccount.A_zimbraPrefMandatorySpellCheckEnabled, ZaAccount.A_zimbraPrefAutoSaveDraftInterval],[])) {				
			prefItems.push({type:_ZAWIZ_TOP_GROUPER_, id:"account_prefs_mail_composing",borderCssClass:"LowPadedTopGrouperBorder",
							label:ZaMsg.NAD_MailOptionsComposing,
							items :[																										
								{ref:ZaAccount.A_zimbraPrefComposeInNewWindow, 
									colSpan:2,
									type:_SUPER_WIZ_CHECKBOX_,
									colSizes:["200px","300px","*"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefComposeInNewWindow,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefComposeInNewWindow,
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefComposeFormat, 
									type:_SUPERWIZ_SELECT1_,
                                    colSizes:["300px", "*"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefComposeFormat,
									label:ZaMsg.LBL_zimbraPrefComposeFormat},
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontFamily, type:_SUPERWIZ_SELECT1_,
                                    colSizes:["300px", "*"],
									msgName:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontFamily,label:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontFamily,
									labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null
								},
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontSize, type:_SUPERWIZ_SELECT1_,
                                    colSizes:["300px", "*"],
									msgName:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontSize, label:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontSize,
									labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, valueLabel:null
								},
								{ref:ZaAccount.A_zimbraPrefHtmlEditorDefaultFontColor, type:_SUPERWIZ_DWT_COLORPICKER_,
									msgName:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontColor, label:ZaMsg.LBL_zimbraPrefHtmlEditorDefaultFontColor,
									labelLocation:_LEFT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPrefForwardReplyInOriginalFormat, 
									colSpan:2,
									type:_SUPER_WIZ_CHECKBOX_, 
									colSizes:["200px", "300px", "*"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefForwardReplyInOriginalFormat,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefForwardReplyInOriginalFormat, 
									trueValue:"TRUE", falseValue:"FALSE"},
								{ref:ZaAccount.A_zimbraPrefMandatorySpellCheckEnabled, 
									colSpan:2,
									type:_SUPER_WIZ_CHECKBOX_, 
									colSizes:["200px", "300px", "*"],
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefMandatorySpellCheckEnabled,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefMandatorySpellCheckEnabled,
									trueValue:"TRUE", falseValue:"FALSE"},									
								{ref:ZaAccount.A_zimbraPrefMailSignatureEnabled,
									colSizes:["200px", "300px", "*"],
									type:_WIZ_CHECKBOX_, msgName:ZaMsg.LBL_zimbraPrefMailSignatureEnabled,
									label:ZaMsg.LBL_zimbraPrefMailSignatureEnabled,  
									trueValue:"TRUE", falseValue:"FALSE"},
								/*{ref:ZaAccount.A_zimbraPrefMailSignatureStyle, 
									//colSpan:2,								
									type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPrefMailSignatureStyle,
									checkBoxLabel:ZaMsg.LBL_zimbraPrefMailSignatureStyle,
									trueValue:"internet", falseValue:"outlook"
								},*/
								{ref:ZaAccount.A_zimbraMailSignatureMaxLength, type:_SUPERWIZ_TEXTFIELD_,
                                    colSpan:2,
									colSizes:["200px", "300px", "*"],
									txtBoxLabel:ZaMsg.LBL_zimbraMailSignatureMaxLength, 
									msgName:ZaMsg.MSG_zimbraMailSignatureMaxLength,
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPrefMailSignature, type:_TEXTAREA_,
									colSizes:["200px", "300px", "*"],
									msgName:ZaMsg.MSG_zimbraPrefMailSignature,
									label:ZaMsg.LBL_zimbraPrefMailSignature, labelLocation:_LEFT_, 
									labelCssStyle:"vertical-align:top", width:"30em",
									enableDisableChangeEventSources:[ZaAccount.A_zimbraPrefMailSignatureEnabled],
									enableDisableChecks:[ZaAccountXFormView.isMailSignatureEnabled]									
								},
                                {ref:ZaAccount.A_zimbraPrefAutoSaveDraftInterval, type:_SUPERWIZ_LIFETIME_,
                                    colSizes:["200px","80px","220px","*"],
                                    msgName:ZaMsg.MSG_zimbraPrefAutoSaveDraftInterval,
                                    txtBoxLabel:ZaMsg.LBL_zimbraPrefAutoSaveDraftInterval,
                                    resetToSuperLabel:ZaMsg.NAD_ResetToCOS,colSpan:2,
                                    nowrap:false,labelWrap:true
                                }
							]
						});
		}		
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraPrefAutoAddAddressEnabled,ZaAccount.A_zimbraPrefGalAutoCompleteEnabled],[])) {				
			prefItems.push(
						{type:_ZAWIZ_TOP_GROUPER_, id:"account_prefs_contacts_general",
                            label:ZaMsg.NAD_ContactsOptions,
							items :[
                                    {ref:ZaAccount.A_zimbraPrefAutoAddAddressEnabled, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2,
                                        colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                        msgName:ZaMsg.LBL_zimbraPrefAutoAddAddressEnabled,checkBoxLabel:ZaMsg.LBL_zimbraPrefAutoAddAddressEnabled,
                                        trueValue:"TRUE", falseValue:"FALSE"
                                    },
                                    {ref:ZaAccount.A_zimbraPrefGalAutoCompleteEnabled, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2,
                                        colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                        msgName:ZaMsg.LBL_zimbraPrefGalAutoCompleteEnabled,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefGalAutoCompleteEnabled,
                                        trueValue:"TRUE", falseValue:"FALSE"}
                                    ]
						});
		}
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraPrefTimeZoneId,ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime,
			ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal,ZaAccount.A_zimbraPrefCalendarUseQuickAdd,ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar,
			ZaAccount.A_zimbraPrefCalendarInitialView,ZaAccount.A_zimbraPrefCalendarFirstDayOfWeek,ZaAccount.A_zimbraPrefCalendarNotifyDelegatedChanges,
			ZaAccount.A_zimbraPrefCalendarApptVisibility,ZaAccount.A_zimbraPrefCalendarReminderSoundsEnabled,
			ZaAccount.A_zimbraPrefCalendarSendInviteDeniedAutoReply,ZaAccount.A_zimbraPrefCalendarAutoAddInvites,
			ZaAccount.A_zimbraPrefCalendarAllowForwardedInvite,ZaAccount.A_zimbraPrefCalendarReminderFlashTitle,
			ZaAccount.A_zimbraPrefCalendarAllowCancelEmailToSelf,ZaAccount.A_zimbraPrefCalendarToasterEnabled,
			ZaAccount.A_zimbraPrefCalendarShowPastDueReminders,ZaAccount.A_zimbraPrefAppleIcalDelegationEnabled],[])) {				
			prefItems.push(
						{type:_ZAWIZ_TOP_GROUPER_, id:"account_prefs_calendar_general",
                          label:ZaMsg.NAD_CalendarOptions,
							items :[
                                    {ref:ZaAccount.A_zimbraPrefTimeZoneId, type:_SUPERWIZ_SELECT1_,
                                        msgName:ZaMsg.LBL_zimbraPrefTimeZoneId, valueWidth: "280px",
                                        colSizes:["300px", "*"],
                                        label:ZaMsg.LBL_zimbraPrefTimeZoneId, labelLocation:_LEFT_,
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS},
                                    {ref:ZaAccount.A_zimbraPrefCalendarApptReminderWarningTime, type:_SUPERWIZ_SELECT1_,
                                        colSizes:["300px", "*"],
                                        msgName:ZaMsg.LBL_zimbraPrefCalendarApptReminderWarningTime,
                                        label:ZaMsg.LBL_zimbraPrefCalendarApptReminderWarningTime, labelLocation:_LEFT_,
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS},
                                    {ref:ZaAccount.A_zimbraPrefCalendarInitialView, type:_SUPERWIZ_SELECT1_,
                                        colSizes:["300px", "*"],
                                        msgName:ZaMsg.MSG_zimbraPrefCalendarInitialView,
                                        label:ZaMsg.LBL_zimbraPrefCalendarInitialView, labelLocation:_LEFT_,
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS
                                    },
                                    {ref:ZaAccount.A_zimbraPrefCalendarFirstDayOfWeek,
                                        type:_SUPERWIZ_SELECT1_,
                                        colSizes:["300px", "*"],
                                        msgName:ZaMsg.LBL_zimbraPrefCalendarFirstDayOfWeek,
                                        label:ZaMsg.LBL_zimbraPrefCalendarFirstDayOfWeek, labelLocation:_LEFT_,
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS
                                    },
                                    {ref:ZaAccount.A_zimbraPrefCalendarApptVisibility,
                                        type:_SUPERWIZ_SELECT1_,
                                        colSizes:["300px", "*"],
                                        msgName:ZaMsg.LBL_zimbraPrefCalendarApptVisibility,
                                        label:ZaMsg.LBL_zimbraPrefCalendarApptVisibility, labelLocation:_LEFT_,
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS
                                    },
                                    {ref:ZaAccount.A_zimbraPrefAppleIcalDelegationEnabled, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.MSG_zimbraPrefAppleIcalDelegationEnabled,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefAppleIcalDelegationEnabled,
                                        trueValue:"TRUE", falseValue:"FALSE",
                                        nowrap:false,labelWrap:true
                                    },
                                    {ref:ZaAccount.A_zimbraPrefCalendarShowPastDueReminders, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.MSG_zimbraPrefCalendarShowPastDueReminders,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarShowPastDueReminders,
                                        trueValue:"TRUE", falseValue:"FALSE",
                                        nowrap:false,labelWrap:true
                                    },
                                    {ref:ZaAccount.A_zimbraPrefCalendarToasterEnabled, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.MSG_zimbraPrefCalendarToasterEnabled,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarToasterEnabled,
                                        trueValue:"TRUE", falseValue:"FALSE",
                                        nowrap:false,labelWrap:true
                                    },
                                    {ref:ZaAccount.A_zimbraPrefCalendarAllowCancelEmailToSelf, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.MSG_zimbraPrefCalendarAllowCancelEmailToSelf,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarAllowCancelEmailToSelf,
                                        trueValue:"TRUE", falseValue:"FALSE",
                                        nowrap:false,labelWrap:true
                                    },
                                    {ref:ZaAccount.A_zimbraPrefCalendarAllowPublishMethodInvite, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.MSG_zimbraPrefCalendarAllowPublishMethodInvite,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarAllowPublishMethodInvite,
                                        trueValue:"TRUE", falseValue:"FALSE",
                                        nowrap:false,labelWrap:true
                                    },
                                    {ref:ZaAccount.A_zimbraPrefCalendarAllowForwardedInvite, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.MSG_zimbraPrefCalendarAllowForwardedInvite,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarAllowForwardedInvite,
                                        trueValue:"TRUE", falseValue:"FALSE",
                                        nowrap:false,labelWrap:true
                                    },
                                    {ref:ZaAccount.A_zimbraPrefCalendarReminderFlashTitle, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.MSG_zimbraPrefCalendarReminderFlashTitle,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarReminderFlashTitle,
                                        trueValue:"TRUE", falseValue:"FALSE",
                                        nowrap:false,labelWrap:true
                                    },
                                    {ref:ZaAccount.A_zimbraPrefCalendarReminderSoundsEnabled, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefCalendarReminderSoundsEnabled,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarReminderSoundsEnabled,
                                        trueValue:"TRUE", falseValue:"FALSE",
                                        nowrap:false,labelWrap:true
                                    },
                                    {ref:ZaAccount.A_zimbraPrefCalendarSendInviteDeniedAutoReply, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefCalendarSendInviteDeniedAutoReply,
                                        checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarSendInviteDeniedAutoReply,
                                        trueValue:"TRUE", falseValue:"FALSE",
                                        nowrap:false,labelWrap:true
                                    },
                                    {ref:ZaAccount.A_zimbraPrefCalendarAutoAddInvites, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefCalendarAutoAddInvites,checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarAutoAddInvites, trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefCalendarNotifyDelegatedChanges, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefCalendarNotifyDelegatedChanges,checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarNotifyDelegatedChanges, trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefCalendarAlwaysShowMiniCal, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefCalendarAlwaysShowMiniCal,checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarAlwaysShowMiniCal, trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefCalendarUseQuickAdd, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefCalendarUseQuickAdd,checkBoxLabel:ZaMsg.LBL_zimbraPrefCalendarUseQuickAdd, trueValue:"TRUE", falseValue:"FALSE"},
                                    {ref:ZaAccount.A_zimbraPrefUseTimeZoneListInCalendar, type:_SUPER_WIZ_CHECKBOX_,
                                        colSpan:2, colSizes:["200px", "300px", "*"],
                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.LBL_zimbraPrefUseTimeZoneListInCalendar,checkBoxLabel:ZaMsg.LBL_zimbraPrefUseTimeZoneListInCalendar,trueValue:"TRUE", falseValue:"FALSE"}
							]
						});							
		}			
		cases.push({type:_CASE_, caseKey:ZaNewAccountXWizard.PREFS_STEP, tabGroupKey:ZaNewAccountXWizard.PREFS_STEP, 
					numCols:1, width:"680", items :prefItems});
	}	

	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.SKIN_TAB_ATTRS, ZaAccountXFormView.SKIN_TAB_RIGHTS)) {
		ZaNewAccountXWizard.SKINS_STEP = ++this.TAB_INDEX;		
		this.stepChoices.push({value:ZaNewAccountXWizard.SKINS_STEP, label:ZaMsg.TABT_Themes});

		cases.push({type:_CASE_, caseKey:ZaNewAccountXWizard.SKINS_STEP, tabGroupKey:ZaNewAccountXWizard.SKINS_STEP, id:"account_form_themes_step", numCols:1, width:"100%",  
						items: [	
							{type:_GROUP_, 
								items:[
								  {ref:ZaAccount.A_zimbraPrefSkin, type:_SUPERWIZ_SELECT1_,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPrefSkin,label:ZaMsg.LBL_zimbraPrefSkin, labelLocation:_LEFT_, 
									choices:ZaNewAccountXWizard.themeChoices,
									visibilityChecks:[ZaAccountXFormView.gotSkins]                                     
                                  },
                                  {type:_OUTPUT_,ref:ZaAccount.A_zimbraPrefSkin,label:ZaMsg.LBL_zimbraPrefSkin, labelLocation:_LEFT_, 
                                 	  visibilityChecks:[ZaAccountXFormView.gotNoSkins]
                                  }                                  
								] 
							},
							{type:_SPACER_},
							{type:_SUPER_WIZ_SELECT_CHECK_,
								selectRef:ZaAccount.A_zimbraAvailableSkin, 
								ref:ZaAccount.A_zimbraAvailableSkin, 
								choices:ZaNewAccountXWizard.themeChoices,
								visibilityChecks:[Case_XFormItem.prototype.isCurrentTab,ZaAccountXFormView.gotSkins],
								visibilityChangeEventSources:[ZaModel.currentStep],
								caseKey:ZaNewAccountXWizard.SKINS_STEP, caseVarRef:ZaModel.currentStep,
								limitLabel:ZaMsg.NAD_LimitThemesTo
							},
							{type:_DWT_ALERT_,colSpan:2,style: DwtAlert.WARNING, iconVisible:true,
								visibilityChecks:[ZaAccountXFormView.gotNoSkins],
								value:ZaMsg.ERROR_CANNOT_FIND_SKINS_FOR_ACCOUNT
							}
						]
		});			
	}
	
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.ZIMLET_TAB_ATTRS, ZaAccountXFormView.ZIMLET_TAB_RIGHTS)) {
		ZaNewAccountXWizard.ZIMLETS_STEP = ++this.TAB_INDEX;			
		this.stepChoices.push({value:ZaNewAccountXWizard.ZIMLETS_STEP, label:ZaMsg.TABT_Zimlets});

		cases.push({type:_CASE_, caseKey:ZaNewAccountXWizard.ZIMLETS_STEP, tabGroupKey:ZaNewAccountXWizard.ZIMLETS_STEP, id:"account_form_zimlets_step", numCols:1, width:"100%", 
						items: [	
							{type:_ZAWIZGROUP_, numCols:1,colSizes:["auto"], 
								items: [
									{type:_SUPER_WIZ_ZIMLET_SELECT_,
										selectRef:ZaAccount.A_zimbraZimletAvailableZimlets, 
										ref:ZaAccount.A_zimbraZimletAvailableZimlets, 
										choices:ZaNewAccountXWizard.zimletChoices,
										limitLabel:ZaMsg.NAD_LimitZimletsTo
									}
								]
							}							
						]
		});			
	}
		
	if(ZaTabView.isTAB_ENABLED(entry,ZaAccountXFormView.ADVANCED_TAB_ATTRS, ZaAccountXFormView.ADVANCED_TAB_RIGHTS)) {
		ZaNewAccountXWizard.ADVANCED_STEP = ++this.TAB_INDEX;			
		this.stepChoices.push({value:ZaNewAccountXWizard.ADVANCED_STEP, label:ZaMsg.TABT_Advanced});
		advancedCaseItems = [];
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraAttachmentsBlocked],[])) {				
			advancedCaseItems.push({type:_ZAWIZ_TOP_GROUPER_, id:"account_attachment_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_AttachmentsGrouper,						
							items :[
								{ref:ZaAccount.A_zimbraAttachmentsBlocked, type:_SUPER_WIZ_CHECKBOX_,
                                    colSizes:["200px", "300px", "*"], colSpan:3,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_RemoveAllAttachments,
									checkBoxLabel:ZaMsg.NAD_RemoveAllAttachments, 
									trueValue:"TRUE", falseValue:"FALSE"
								}
							]
						});
		}
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraMailQuota,ZaAccount.A_zimbraContactMaxNumEntries,
			ZaAccount.A_zimbraQuotaWarnPercent,ZaAccount.A_zimbraQuotaWarnInterval,ZaAccount.A_zimbraQuotaWarnMessage],[])) {						
			advancedCaseItems.push({type:_ZAWIZ_TOP_GROUPER_, id:"account_quota_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_QuotaGrouper,						
							items: [
								{ref:ZaAccount.A_zimbraMailForwardingAddressMaxLength, type:_SUPERWIZ_TEXTFIELD_,
                                    colSizes:["200px", "300px", "*"], colSpan: 1,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraMailForwardingAddressMaxLength,
									txtBoxLabel:ZaMsg.LBL_zimbraMailForwardingAddressMaxLength, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraMailForwardingAddressMaxNumAddrs, type:_SUPERWIZ_TEXTFIELD_,
                                    colSizes:["200px", "300px", "*"], colSpan: 1,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraMailForwardingAddressMaxNumAddrs,
									txtBoxLabel:ZaMsg.LBL_zimbraMailForwardingAddressMaxNumAddrs, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},							
								{ref:ZaAccount.A_zimbraMailQuota, type:_SUPERWIZ_TEXTFIELD_,
                                    colSizes:["200px", "300px", "*"],colSpan: 1,
									txtBoxLabel:ZaMsg.LBL_zimbraMailQuota, msgName:ZaMsg.MSG_zimbraMailQuota,labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraContactMaxNumEntries, type:_SUPERWIZ_TEXTFIELD_,
                                    colSizes:["200px", "300px", "*"], colSpan: 1,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraContactMaxNumEntries,
									txtBoxLabel:ZaMsg.LBL_zimbraContactMaxNumEntries, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input"
								},
								{ref:ZaAccount.A_zimbraQuotaWarnPercent, type:_SUPERWIZ_TEXTFIELD_,
                                    colSizes:["200px", "300px", "*"], colSpan: 1,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.LBL_zimbraQuotaWarnPercent, msgName:ZaMsg.MSG_zimbraQuotaWarnPercent,labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraQuotaWarnInterval, type:_SUPERWIZ_LIFETIME_,
                                    colSizes:["200px", "80px", "220px", "*"], colSpan: 1,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									textFieldCssClass:"admin_xform_number_input", 
									txtBoxLabel:ZaMsg.LBL_zimbraQuotaWarnInterval, msgName:ZaMsg.MSG_zimbraQuotaWarnInterval,labelLocation:_LEFT_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraQuotaWarnMessage, type:_SUPERWIZ_TEXTAREA_,
                                    colSizes:["200px", "300px", "*"], colSpan: 1,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									txtBoxLabel:ZaMsg.LBL_zimbraQuotaWarnMessage, 
									msgName:ZaMsg.MSG_zimbraQuotaWarnMessage,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								}
							]
						});
		}

		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ 
								ZaAccount.A_zimbraDataSourceMinPollingInterval,
								ZaAccount.A_zimbraDataSourcePop3PollingInterval,
								ZaAccount.A_zimbraDataSourceImapPollingInterval,
								ZaAccount.A_zimbraDataSourceCalendarPollingInterval,
								ZaAccount.A_zimbraDataSourceRssPollingInterval,
								ZaAccount.A_zimbraDataSourceCaldavPollingInterval],[])) {						
			advancedCaseItems.push({type:_ZAWIZ_TOP_GROUPER_, id:"account_datasourcepolling_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_DataSourcePolling,						
							items: [
                                                                {ref:ZaAccount.A_zimbraDataSourceMinPollingInterval, type:_SUPERWIZ_LIFETIME_,
                                                                        colSizes:["200px","80px","220px","*"],
                                                                        msgName:ZaMsg.MSG_zimbraDataSourceMinPollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourceMinPollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                                                        nowrap:false,labelWrap:true
                                                                },
                                                                {ref:ZaAccount.A_zimbraDataSourcePop3PollingInterval, type:_SUPERWIZ_LIFETIME_,
                                                                        colSizes:["200px","80px","220px","*"],
                                                                        msgName:ZaMsg.MSG_zimbraDataSourcePop3PollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourcePop3PollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                                                        nowrap:false,labelWrap:true
                                                                },
                                                                {ref:ZaAccount.A_zimbraDataSourceImapPollingInterval, type:_SUPERWIZ_LIFETIME_,
                                                                        colSizes:["200px","80px","220px","*"],
                                                                        msgName:ZaMsg.MSG_zimbraDataSourceImapPollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourceImapPollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                                                        nowrap:false,labelWrap:true
                                                                },
                                                                {ref:ZaAccount.A_zimbraDataSourceCalendarPollingInterval, type:_SUPERWIZ_LIFETIME_,
                                                                        colSizes:["200px","80px","220px","*"],
                                                                        msgName:ZaMsg.MSG_zimbraDataSourceCalendarPollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourceCalendarPollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                                                        nowrap:false,labelWrap:true
                                                                },
                                                                {ref:ZaAccount.A_zimbraDataSourceRssPollingInterval, type:_SUPERWIZ_LIFETIME_,
                                                                        colSizes:["200px","80px","220px","*"],
                                                                        msgName:ZaMsg.MSG_zimbraDataSourceRssPollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourceRssPollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                                                        nowrap:false,labelWrap:true
                                                                },
                                                                {ref:ZaAccount.A_zimbraDataSourceCaldavPollingInterval, type:_SUPERWIZ_LIFETIME_,
                                                                        colSizes:["200px","80px","220px","*"],
                                                                        msgName:ZaMsg.MSG_zimbraDataSourceCaldavPollingInterval,
                                                                        txtBoxLabel:ZaMsg.LBL_zimbraDataSourceCaldavPollingInterval,
                                                                        resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                                                        nowrap:false,labelWrap:true
                                                                }

							]
						});
		}

				if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraProxyAllowedDomains],[])) {						
			          advancedCaseItems.push({type:_ZAWIZ_TOP_GROUPER_, id:"account_proxyalloweddomains_settings",colSizes:["200px","auto"],numCols:2,
							label:ZaMsg.NAD_ProxyAllowedDomains,
							items: [
              							{ ref: ZaAccount.A_zimbraProxyAllowedDomains,
                                               label:ZaMsg.LBL_zimbraProxyAllowedDomains,
                                               labelCssStyle:"vertical-align:top",
                                               type:_SUPER_REPEAT_,
                                               colSizes:["300px", "*"],
                                               resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                               repeatInstance:"",
                                               addButtonLabel:ZaMsg.NAD_ProxyAddAllowedDomain ,
                                               removeButtonLabel: ZaMsg.NAD_ProxyRemoveAllowedDomain,
                                               showAddButton:true,
                                               showRemoveButton:true,
                                               showAddOnNextRow:true,
                                               repeatItems: [
                                                  { ref:".", type:_TEXTFIELD_,
                                                    enableDisableChecks:[ZaItem.hasWritePermission] ,
                                                    visibilityChecks:[ZaItem.hasReadPermission],
                                                    width: "15em"
                                                  }
                                               ]
                                        }
							]
						});
		}
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraPasswordLocked,ZaAccount.A_zimbraMinPwdLength,
			ZaAccount.A_zimbraMaxPwdLength,ZaAccount.A_zimbraPasswordMinUpperCaseChars,ZaAccount.A_zimbraPasswordMinLowerCaseChars,
			ZaAccount.A_zimbraPasswordMinPunctuationChars,ZaAccount.A_zimbraPasswordMinNumericChars,ZaAccount.A_zimbraPasswordMinDigitsOrPuncs,
			ZaAccount.A_zimbraMinPwdAge,ZaAccount.A_zimbraMaxPwdAge,ZaAccount.A_zimbraEnforcePwdHistory],[])) {						
			advancedCaseItems.push({type:_ZAWIZ_TOP_GROUPER_,id:"account_password_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_PasswordGrouper,				
							items: [
						        { type: _DWT_ALERT_, containerCssStyle: "padding-bottom:0px",
						            style: DwtAlert.WARNING,iconVisible: false,
						            content: ZaMsg.Alert_InternalPassword, colSpan:3
						        },

								{ref:ZaAccount.A_zimbraPasswordLocked, 
									type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.NAD_PwdLocked,checkBoxLabel:ZaMsg.NAD_PwdLocked, 
									trueValue:"TRUE", falseValue:"FALSE",
                                    colSizes:["200px", "300px", "*"],
									visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraMinPwdLength, 
									type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraMinPwdLength,
									txtBoxLabel:ZaMsg.LBL_zimbraMinPwdLength, 
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
                                    colSizes:["200px", "300px", "*"],
									visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraMaxPwdLength, type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.MSG_zimbraMaxPwdLength,
									txtBoxLabel:ZaMsg.LBL_zimbraMaxPwdLength, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
                                    colSizes:["200px", "300px", "*"],
									visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraPasswordMinUpperCaseChars, 
									type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPasswordMinUpperCaseChars,
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordMinUpperCaseChars, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
                                    colSizes:["200px", "300px", "*"],
									visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraPasswordMinLowerCaseChars,
									type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPasswordMinLowerCaseChars,
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordMinLowerCaseChars, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
                                    colSizes:["200px", "300px", "*"],
									visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraPasswordMinPunctuationChars,  
									type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPasswordMinPunctuationChars,
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordMinPunctuationChars, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
                                    colSizes:["200px", "300px", "*"],
									visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraPasswordMinNumericChars, 
									type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPasswordMinNumericChars,
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordMinNumericChars, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
                                    colSizes:["200px", "300px", "*"],
									visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraPasswordMinDigitsOrPuncs, 
									type:_SUPERWIZ_TEXTFIELD_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraPasswordMinDigitsOrPuncs,
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordMinDigitsOrPuncs, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
                                    colSizes:["200px", "300px", "*"],
									visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
								},
																							
								{ref:ZaAccount.A_zimbraMinPwdAge, 
									type:_SUPERWIZ_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_passMinAge,txtBoxLabel:ZaMsg.LBL_passMinAge, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
                                    colSizes:["200px", "300px", "*"],
									visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraMaxPwdAge, 
									type:_SUPERWIZ_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_passMaxAge,txtBoxLabel:ZaMsg.LBL_passMaxAge, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
                                    colSizes:["200px", "300px", "*"],
									visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
								},
								{ref:ZaAccount.A_zimbraEnforcePwdHistory, 
									type:_SUPERWIZ_TEXTFIELD_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraEnforcePwdHistory,
									txtBoxLabel:ZaMsg.LBL_zimbraEnforcePwdHistory, labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input",
                                    colSizes:["200px", "300px", "*"],
									visibilityChecks:[],enableDisableChecks:[[ZaNewAccountXWizard.isAuthfromInternal, domainName,ZaAccount.A_name]]
								}
							]
						});
		}
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraPasswordLockoutEnabled,ZaAccount.A_zimbraPasswordLockoutMaxFailures,
			ZaAccount.A_zimbraPasswordLockoutDuration,ZaAccount.A_zimbraPasswordLockoutFailureLifetime],[])) {						
			advancedCaseItems.push({type:_ZAWIZ_TOP_GROUPER_, id:"password_lockout_settings",colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_FailedLoginGrouper,							
							items :[

								{ref:ZaAccount.A_zimbraPasswordLockoutEnabled, type:_SUPER_WIZ_CHECKBOX_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.LBL_zimbraPasswordLockoutEnabled,checkBoxLabel:ZaMsg.LBL_zimbraPasswordLockoutEnabled, 
                                    colSizes:["200px", "300px", "*"],colSpan:1, trueValue:"TRUE", falseValue:"FALSE"
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutMaxFailures, type:_SUPERWIZ_TEXTFIELD_, 
									enableDisableChecks: [[ZaAccountXFormView.isPasswordLockoutEnabled],[XForm.checkInstanceValue,ZaAccount.A_zimbraPasswordLockoutEnabled,"TRUE"]],
								 	enableDisableChangeEventSources:[ZaAccount.A_zimbraPasswordLockoutEnabled,ZaAccount.A_COSId, ZaAccount.A_zimbraPasswordLockoutEnabled],
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordLockoutMaxFailures,
									toolTipContent:ZaMsg.TTP_zimbraPasswordLockoutMaxFailuresSub,
									msgName:ZaMsg.MSG_zimbraPasswordLockoutMaxFailures,
									labelLocation:_LEFT_, 
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
                                    colSizes:["200px", "300px", "*"],colSpan:1
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutDuration, type:_SUPERWIZ_LIFETIME_, 
									enableDisableChecks: [[ZaAccountXFormView.isPasswordLockoutEnabled],[XForm.checkInstanceValue,ZaAccount.A_zimbraPasswordLockoutEnabled,"TRUE"]],
								 	enableDisableChangeEventSources:[ZaAccount.A_zimbraPasswordLockoutEnabled,ZaAccount.A_COSId, ZaAccount.A_zimbraPasswordLockoutEnabled],
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordLockoutDuration,
									toolTipContent:ZaMsg.TTP_zimbraPasswordLockoutDurationSub,
									msgName:ZaMsg.MSG_zimbraPasswordLockoutDuration,
									textFieldCssClass:"admin_xform_number_input",
                                    colSizes:["200px", "80px", "220px", "*"],colSpan:1,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS
								},
								{ref:ZaAccount.A_zimbraPasswordLockoutFailureLifetime, type:_SUPERWIZ_LIFETIME_, 
									enableDisableChecks: [[ZaAccountXFormView.isPasswordLockoutEnabled],[XForm.checkInstanceValue,ZaAccount.A_zimbraPasswordLockoutEnabled,"TRUE"]],
								 	enableDisableChangeEventSources:[ZaAccount.A_zimbraPasswordLockoutEnabled,ZaAccount.A_COSId, ZaAccount.A_zimbraPasswordLockoutEnabled],
									txtBoxLabel:ZaMsg.LBL_zimbraPasswordLockoutFailureLifetime,
									toolTipContent:ZaMsg.TTP_zimbraPasswordLockoutFailureLifetimeSub,
									msgName:ZaMsg.MSG_zimbraPasswordLockoutFailureLifetime,
									textFieldCssClass:"admin_xform_number_input", 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
									labelCssStyle:"white-space:normal;",
                                    colSizes:["200px", "80px", "220px", "*"],colSpan:1,
									nowrap:false,labelWrap:true
								}								
							]
						});
		}
		if(ZAWizTopGrouper_XFormItem.isGroupVisible(entry,[ZaAccount.A_zimbraAdminAuthTokenLifetime,ZaAccount.A_zimbraAuthTokenLifetime,
			ZaAccount.A_zimbraMailIdleSessionTimeout,ZaAccount.A_zimbraMailMessageLifetime,ZaAccount.A_zimbraMailTrashLifetime,ZaAccount.A_zimbraMailSpamLifetime],[])) {
			advancedCaseItems.push({type:_ZAWIZ_TOP_GROUPER_, colSizes:["auto"],numCols:1,
							label:ZaMsg.NAD_TimeoutGrouper,	id:"timeout_settings",
							items: [
								{ref:ZaAccount.A_zimbraAdminAuthTokenLifetime,
									type:_SUPERWIZ_LIFETIME_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraAdminAuthTokenLifetime,
									txtBoxLabel:ZaMsg.LBL_zimbraAdminAuthTokenLifetime,
									enableDisableChecks:[ZaAccountXFormView.isAdminAccount],
                                    colSizes:["200px", "80px", "220px", "*"],colSpan:1,
									enableDisableChangeEventSources:[ZaAccount.A_zimbraIsAdminAccount]
								},							
								{ref:ZaAccount.A_zimbraAuthTokenLifetime,
									type:_SUPERWIZ_LIFETIME_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraAuthTokenLifetime,
                                    colSizes:["200px", "80px", "220px", "*"],colSpan:1,
									txtBoxLabel:ZaMsg.LBL_zimbraAuthTokenLifetime},								
								{ref:ZaAccount.A_zimbraMailIdleSessionTimeout, 
									type:_SUPERWIZ_LIFETIME_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraMailIdleSessionTimeout,
                                    colSizes:["200px", "80px", "220px", "*"],colSpan:1,
									txtBoxLabel:ZaMsg.LBL_zimbraMailIdleSessionTimeout},
								{ref:ZaAccount.A_zimbraMailMessageLifetime, type:_SUPERWIZ_LIFETIME2_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraMailMessageLifetime,
                                    colSizes:["200px", "80px", "220px", "*"],colSpan:1,
									txtBoxLabel:ZaMsg.LBL_zimbraMailMessageLifetime},
								{ref:ZaAccount.A_zimbraMailTrashLifetime, type:_SUPERWIZ_LIFETIME1_,
                                    colSizes:["200px", "80px", "220px", "*"],colSpan:1,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.MSG_zimbraMailTrashLifetime,
									txtBoxLabel:ZaMsg.LBL_zimbraMailTrashLifetime},
								{ref:ZaAccount.A_zimbraMailSpamLifetime, type:_SUPERWIZ_LIFETIME1_,
                                    colSizes:["200px", "80px", "220px", "*"],colSpan:1,
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, 
									msgName:ZaMsg.MSG_zimbraMailSpamLifetime,
									txtBoxLabel:ZaMsg.LBL_zimbraMailSpamLifetime}
							]
						});
		}		
		cases.push({type:_CASE_, caseKey:ZaNewAccountXWizard.ADVANCED_STEP, tabGroupKey:ZaNewAccountXWizard.ADVANCED_STEP, id:"account_form_advanced_step", numCols:1, width:"100%", 
				items:advancedCaseItems});
	}
	this._lastStep = this.stepChoices.length;
	xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices, valueChangeEventSources:[ZaModel.currentStep]},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:680, align:_LEFT_, valign:_TOP_, items:cases}
		];
};
ZaXDialog.XFormModifiers["ZaNewAccountXWizard"].push(ZaNewAccountXWizard.myXFormModifier);
