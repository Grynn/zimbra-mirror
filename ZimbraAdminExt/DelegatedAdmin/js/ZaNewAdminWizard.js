/**
The new Admin account/group wizard to provide a easy way to
        create admin accounts or groups
        */


ZaItem.NEW_ADMIN = "newAdmin" ;
ZaNewAdmin = function () {
    ZaItem.call (this, "ZaNewAdmin") ;
    this._init () ;
    this.type = ZaItem.NEW_ADMIN ;
    this.attrs = {} ;
    this.attrs[ZaAccount.A_zimbraIsDelegatedAdminAccount] = "TRUE" ;
}

ZaNewAdmin.prototype = new ZaItem ;
ZaNewAdmin.prototype.constructor = ZaNewAdmin ;

ZaNewAdmin.A_admin_type = "new_admin_type" ;
ZaNewAdmin.A_default_domain_admin_grp = "default_da_grp" ;
ZaNewAdmin.A_proposedGrantsList = "proposedGrantsList" ;
//ZaNewAdminWizard.A_proposedGrantsListCheckbox = "proposedGrantsListCheckbox" ;

ZaNewAdmin.getMyXModel = function () {
	var modelItems = [
           { id: ZaNewAdmin.A_admin_type, type: _STRING_, //choices: ZaNewAdmin.getNewAdminChoices (),
               ref: ZaNewAdmin.A_admin_type },
           { id: "id", type:_STRING_, ref:"id"},
           { id: "type", type:_STRING_, ref:"type"},
           { id: ZaAccount.A_name, type:_STRING_, ref:ZaAccount.A_name},
           { id: ZaAccount.A_password, type:_STRING_, ref:"attrs/" + ZaAccount.A_password},
           { id: ZaAccount.A2_confirmPassword, type:_STRING_, ref: ZaAccount.A2_confirmPassword},
           { id: ZaAccount.A_zimbraPasswordMustChange, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES,
                    ref:"attrs/" + ZaAccount.A_zimbraPasswordMustChange},
           { id: ZaAccount.A_zimbraIsAdminAccount, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/" + ZaAccount.A_zimbraIsAdminAccount},
           { id:ZaAccount.A_zimbraIsDelegatedAdminAccount, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES,
            ref:"attrs/"+ZaAccount.A_zimbraIsDelegatedAdminAccount},
           ZaAccount.adminRolesModelItem,  ZaAccount.adminAccountModelItem,
           { id:ZaDistributionList.A_isAdminGroup, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES,
                ref:"attrs/" + ZaDistributionList.A_isAdminGroup},
           {id: ZaNewAdmin.A_default_domain_admin_grp, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, 
               ref: ZaNewAdmin.A_default_domain_admin_grp},
            ZaNewAdmin.getProposedGrantsListItem () ,
           ZaTargetPermission.grantListItem ,
           ZaUIComponent.UIComponentsItem,
           ZaUIComponent.InheritedUIComponentsItem
	];
	try {
		if(ZaPosixAccount) {
			modelItems.push({id:ZaPosixAccount.A_gidNumber,type:_NUMBER_,ref:"attrs/"+ZaPosixAccount.A_gidNumber, required:true});
			modelItems.push({id:ZaPosixAccount.A_homeDirectory,type:_STRING_,ref:"attrs/"+ZaPosixAccount.A_homeDirectory, required:true});
			modelItems.push({id:ZaPosixAccount.A_uidNumber,type:_NUMBER_, defaultValue:1000,ref:"attrs/"+ZaPosixAccount.A_uidNumber, required:true});
			modelItems.push({id:ZaPosixAccount.A_loginShell,type:_STRING_,ref:"attrs/"+ZaPosixAccount.A_loginShell, required:true});
			modelItems.push({id:ZaPosixAccount.A_gecos,type:_STRING_,ref:"attrs/"+ZaPosixAccount.A_gecos});		
		}
		if(ZaSamAccount) {
			modelItems.push({id:ZaSamAccount.A_isSpecialNTAccount,type:_NUMBER_, defaultValue:0,ref:ZaSamAccount.A_isSpecialNTAccount});
			modelItems.push({id:ZaSamAccount.A_sambaDomainSID,type:_STRING_,ref:ZaSamAccount.A_sambaDomainSID});				
					
			modelItems.push({id:ZaSamAccount.A_sambaSID,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaSID, required:true});			
			modelItems.push({id:ZaSamAccount.A_sambaAcctFlags,type:_STRING_, defaultValue:0,ref:"attrs/"+ZaSamAccount.A_sambaAcctFlags});	
			modelItems.push({id:ZaSamAccount.A_sambaBadPasswordCount,type:_NUMBER_, defaultValue:0,ref:"attrs/"+ZaSamAccount.A_sambaBadPasswordCount});
			modelItems.push({id:ZaSamAccount.A_sambaBadPasswordTime,type:_NUMBER_, defaultValue:0,ref:"attrs/"+ZaSamAccount.A_sambaBadPasswordTime});		
			modelItems.push({id:ZaSamAccount.A_sambaDomainName,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaDomainName});		
			modelItems.push({id:ZaSamAccount.A_sambaHomeDrive,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaHomeDrive});		
			modelItems.push({id:ZaSamAccount.A_sambaHomePath,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaHomePath});			
			modelItems.push({id:ZaSamAccount.A_sambaKickoffTime,type:_NUMBER_,ref:"attrs/"+ZaSamAccount.A_sambaKickoffTime});		
			modelItems.push({id:ZaSamAccount.A_sambaLMPassword,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaLMPassword});				
			modelItems.push({id:ZaSamAccount.A_sambaLogoffTime,type:_NUMBER_,ref:"attrs/"+ZaSamAccount.A_sambaLogoffTime});						
			modelItems.push({id:ZaSamAccount.A_sambaLogonHours,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaLogonHours});								
			modelItems.push({id:ZaSamAccount.A_sambaLogonScript,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaLogonScript});			
			modelItems.push({id:ZaSamAccount.A_sambaLogonTime,type:_NUMBER_,ref:"attrs/"+ZaSamAccount.A_sambaLogonTime});		
			modelItems.push({id:ZaSamAccount.A_sambaMungedDial,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaMungedDial});
			modelItems.push({id:ZaSamAccount.A_sambaNTPassword,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaNTPassword});				
			modelItems.push({id:ZaSamAccount.A_sambaPasswordHistory,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaPasswordHistory});		
			modelItems.push({id:ZaSamAccount.A_sambaPrimaryGroupSID,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaPrimaryGroupSID});		
			modelItems.push({id:ZaSamAccount.A_sambaProfilePath,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaProfilePath});		
			modelItems.push({id:ZaSamAccount.A_sambaPwdCanChange,type:_NUMBER_,ref:"attrs/"+ZaSamAccount.A_sambaPwdCanChange});		
			modelItems.push({id:ZaSamAccount.A_sambaPwdLastSet,type:_NUMBER_,ref:"attrs/"+ZaSamAccount.A_sambaPwdLastSet});		
			modelItems.push({id:ZaSamAccount.A_sambaPwdMustChange,type:_NUMBER_,ref:"attrs/"+ZaSamAccount.A_sambaPwdMustChange});			
			modelItems.push({id:ZaSamAccount.A_sambaUserWorkstations,type:_STRING_,ref:"attrs/"+ZaSamAccount.A_sambaUserWorkstations});
		}
	} catch (ex) {
		//
	}
   return {items:modelItems};
}

ZaNewAdmin.getProposedGrantsListItem = function () {
    var proposedGrantsListItem = ZaUtil.deepCloneObject (ZaTargetPermission.grantListItem) ;
    proposedGrantsListItem.id = ZaNewAdmin.A_proposedGrantsList ;
    proposedGrantsListItem.ref = ZaNewAdmin.A_proposedGrantsList ;
    return proposedGrantsListItem ;
}

ZaNewAdmin.createAdmin = function (tmpObj) {
    var soapDoc ;
    var controller =  ZaApp.getInstance().getCurrentController() ;

    if (tmpObj[ZaNewAdmin.A_admin_type] == ZaItem.ACCOUNT) {
        //creat admin account
        soapDoc = AjxSoapDoc.create("CreateAccountRequest", ZaZimbraAdmin.URN, null);
        soapDoc.set(ZaAccount.A_name, tmpObj.name);
        if(tmpObj.attrs[ZaAccount.A_password] && tmpObj.attrs[ZaAccount.A_password].length > 0)
            soapDoc.set(ZaAccount.A_password, tmpObj.attrs[ZaAccount.A_password]);
        if ( tmpObj.attrs[ZaAccount.A_zimbraPasswordMustChange] && tmpObj.attrs[ZaAccount.A_zimbraPasswordMustChange] == "TRUE") {
           var attr = soapDoc.set("a", tmpObj.attrs[ZaAccount.A_zimbraPasswordMustChange]);
           attr.setAttribute("n", ZaAccount.A_zimbraPasswordMustChange) ;
        }
        for(var aname in tmpObj.attrs) {
			if(aname == ZaAccount.A_zimbraIsAdminAccount || aname == ZaAccount.A_zimbraIsDelegatedAdminAccount || aname == ZaAccount.A_password || aname == ZaAccount.A_zimbraMailAlias || aname == ZaItem.A_objectClass || aname == ZaAccount.A2_mbxsize || aname == ZaAccount.A_mail) {
				continue;
			}	
           	var attr = soapDoc.set("a", tmpObj.attrs[aname]);
           	attr.setAttribute("n", aname) ;        	
        }
        //add the admin attribute
        if (tmpObj.attrs[ZaAccount.A_zimbraIsAdminAccount]
                && tmpObj.attrs[ZaAccount.A_zimbraIsAdminAccount] == "TRUE") {
            var attr = soapDoc.set("a", tmpObj.attrs[ZaAccount.A_zimbraIsAdminAccount]);
            attr.setAttribute("n", ZaAccount.A_zimbraIsAdminAccount) ;
        } else {
            var attr = soapDoc.set("a", "TRUE");
            attr.setAttribute("n", ZaAccount.A_zimbraIsDelegatedAdminAccount) ;
        }
    } else if (tmpObj[ZaNewAdmin.A_admin_type] == ZaItem.DL) {
        //create admin group
        soapDoc = AjxSoapDoc.create("CreateDistributionListRequest", ZaZimbraAdmin.URN, null);
            soapDoc.set(ZaAccount.A_name, tmpObj.name);
        var attr = soapDoc.set("a", "TRUE");
            attr.setAttribute("n", ZaDistributionList.A_isAdminGroup) ;
    } else {
        controller.popupErrorDialog(com_zimbra_delegatedadmin.ERROR_INVALID_ADMIN_TYPE) ;
        return false ;
    }
    
    var csfeParams = new Object();
    csfeParams.soapDoc = soapDoc;
    var reqMgrParams = {} ;
    reqMgrParams.controller = controller;
    reqMgrParams.busyMsg = com_zimbra_delegatedadmin.BUSY_CREATE_ADMIN ;
    try {
        var respBody = ZaRequestMgr.invoke(csfeParams, reqMgrParams).Body ;
        var createdAdmin ;
        if (tmpObj[ZaNewAdmin.A_admin_type] == ZaItem.ACCOUNT) {
            createdAdmin = respBody.CreateAccountResponse.account[0] ;
        } else if (tmpObj[ZaNewAdmin.A_admin_type] == ZaItem.DL) {
            createdAdmin = respBody.CreateDistributionListResponse.dl[0] ;
        } else {
                throw (new AjxException(com_zimbra_delegatedadmin.ERROR_CREATE_ADMIN,
                        AjxException.SERVER_ERROR, "ZaNewAdmin.createAdmin", "Unknow object type" ));
        }

        if (createdAdmin.name && tmpObj.name
             && createdAdmin.name.toLowerCase() == tmpObj.name.toLowerCase()) {    //account name is case insensitive
                 tmpObj.name = createdAdmin.name ;
                 tmpObj.id = createdAdmin.id ;
                 tmpObj.type = tmpObj[ZaNewAdmin.A_admin_type] ;
        } else {
            throw (new AjxException(com_zimbra_delegatedadmin.ERROR_CREATE_ADMIN,
                    AjxException.SERVER_ERROR, "ZaNewAdmin.createAdmin" ));
        }

        if (tmpObj[ZaAccount.A2_memberOf] && tmpObj[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList]
                && tmpObj[ZaAccount.A2_memberOf][ZaAccount.A2_directMemberList].length > 0)
            ZaAccountMemberOfListView.addMemberList (tmpObj, createdAdmin) ;    

        //load the effective rights for the current admin
        tmpObj.loadEffectiveRights ("id", tmpObj.id, false) ;

        return true ;
    }catch (ex) {
        controller._handleException(ex, "ZaNewAdmin.createAdmin", null, false);
    }
    return false ;
}

ZaNewAdmin.modifyAdmin = function (tmpObj) {
    var soapDoc ;
    var controller =  ZaApp.getInstance().getCurrentController() ;

    if (tmpObj[ZaNewAdmin.A_admin_type] == ZaItem.ACCOUNT) {
        //creat admin account
        soapDoc = AjxSoapDoc.create("ModifyAccountRequest", ZaZimbraAdmin.URN, null);
    } else if (tmpObj[ZaNewAdmin.A_admin_type] == ZaItem.DL) {
        //create admin group
        soapDoc = AjxSoapDoc.create("ModifyDistributionListRequest", ZaZimbraAdmin.URN, null);
    } else {
        controller.popupErrorDialog(com_zimbra_delegatedadmin.ERROR_INVALID_ADMIN_TYPE) ;
        return false ;
    }

    soapDoc.set("id", tmpObj.id);

    //modify the zimbraAdminConsoleUIComponents
    if (tmpObj.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents]
            && tmpObj.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents].length > 0) {
        for (var i=0; i < tmpObj.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents].length; i ++) {
            var attr = soapDoc.set("a", tmpObj.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents][i]);
            attr.setAttribute("n", ZaAccount.A_zimbraAdminConsoleUIComponents) ;
        }
    }

    var csfeParams = new Object();
    csfeParams.soapDoc = soapDoc;
    var reqMgrParams = {} ;
    reqMgrParams.controller = controller;
    reqMgrParams.busyMsg = com_zimbra_delegatedadmin.BUSY_SETTING_UI_COMP ;
    try {
        var respBody = ZaRequestMgr.invoke(csfeParams, reqMgrParams).Body ;
        if (tmpObj[ZaNewAdmin.A_admin_type] == ZaItem.ACCOUNT) {
            if (respBody.ModifyAccountResponse.account[0].name.toLowerCase() == tmpObj.name.toLowerCase()) {
                return true ;
            }
        } else if (tmpObj[ZaNewAdmin.A_admin_type] == ZaItem.DL) {
            if (respBody.ModifyDistributionListResponse.dl[0].name.toLowerCase() == tmpObj.name.toLowerCase()) {
                return true ;
            }
        }

        controller.popupErrorDialog(com_zimbra_delegatedadmin.ERROR_SETTING_UI_COMP) ;
    }catch (ex) {
        controller._handleException(ex, "ZaNewAdmin.modifyAdmin", null, false);
    }
    return false ;
}


ZaNewAdminWizard = function (parent) {

    var helpButton = new DwtDialog_ButtonDescriptor(ZaXWizardDialog.HELP_BUTTON, ZaMsg.TBB_Help, DwtDialog.ALIGN_LEFT, new AjxCallback(this, this._helpButtonListener));
    var nextButton = new DwtDialog_ButtonDescriptor(ZaXWizardDialog.NEXT_BUTTON, AjxMsg._next, DwtDialog.ALIGN_RIGHT, new AjxCallback(this, this.goNext));
    var prevButton = new DwtDialog_ButtonDescriptor(ZaXWizardDialog.PREV_BUTTON, AjxMsg._prev, DwtDialog.ALIGN_RIGHT, new AjxCallback(this, this.goPrev));
    var finishButton = new DwtDialog_ButtonDescriptor(ZaXWizardDialog.FINISH_BUTTON, AjxMsg._finish, DwtDialog.ALIGN_RIGHT, new AjxCallback(this, this.finishWizard));
    var skipButton = new DwtDialog_ButtonDescriptor(ZaNewAdminWizard.SKIP_BUTTON, com_zimbra_delegatedadmin.btSkip, DwtDialog.ALIGN_RIGHT, new AjxCallback(this, this.skipWizard));
    var extraButtons = [helpButton,prevButton,nextButton,skipButton, finishButton];

    ZaXWizardDialog.call(this, parent, null, com_zimbra_delegatedadmin.title_new_admin_wizard,
            (AjxEnv.isIE ? "620px" : "570px"), (AjxEnv.isIE ? "330px" :"320px"),
            "ZaNewAdminWizard", extraButtons);

    this.newAdminTypesChoices = [
        {value: ZaItem.ACCOUNT, label: com_zimbra_delegatedadmin.type_account },
        {value: ZaItem.DL, label: com_zimbra_delegatedadmin.IsAdminGroup}
    ];

    this.stepChoices = [
        {label: com_zimbra_delegatedadmin.NA_Wizard_pick_admin_type, value: ZaNewAdminWizard.STEP_START },
        {label: com_zimbra_delegatedadmin.NA_Wizard_new_admin_acct, value: ZaNewAdminWizard.STEP_NEW_ACCOUNT },
        {label: com_zimbra_delegatedadmin.NA_Wizard_new_admin_dl, value: ZaNewAdminWizard.STEP_NEW_GROUP },
        {label: com_zimbra_delegatedadmin.NA_Wizard_set_permission, value: ZaNewAdminWizard.STEP_PERMISSION },
        {label: com_zimbra_delegatedadmin.NA_Wizard_set_permission, value: ZaNewAdminWizard.STEP_PROPOSED_GRANTS },
        {label: com_zimbra_delegatedadmin.NA_Wizard_config_ui, value: ZaNewAdminWizard.STEP_UI_COMPONENTS },
        {label: com_zimbra_delegatedadmin.NA_Wizard_finish_summary, value: ZaNewAdminWizard.STEP_FINISH }
    ];
    this.initForm (ZaNewAdmin.getMyXModel(), this.getMyXForm()) ;
    this._localXForm.setController(ZaApp.getInstance());
    this._helpURL = location.pathname + ZaUtil.HELP_URL + "da_process/admin_wizard.htm?locid="+AjxEnv.DEFAULT_LOCALE;
}

ZaNewAdminWizard.prototype = new ZaXWizardDialog;
ZaNewAdminWizard.prototype.constructor = ZaNewAdminWizard;
ZaXDialog.XFormModifiers["ZaNewAdminWizard"] = [] ;
ZaNewAdminWizard.SKIP_BUTTON = ++DwtDialog.LAST_BUTTON ;

ZaNewAdminWizard.STEP_INDEX = 1;
ZaNewAdminWizard.STEP_START = ZaNewAdminWizard.STEP_INDEX ++ ;
ZaNewAdminWizard.STEP_NEW_ACCOUNT = ZaNewAdminWizard.STEP_INDEX ++ ;
ZaNewAdminWizard.STEP_NEW_GROUP = ZaNewAdminWizard.STEP_INDEX ++ ;
ZaNewAdminWizard.STEP_UI_COMPONENTS = ZaNewAdminWizard.STEP_INDEX ++ ;
ZaNewAdminWizard.STEP_PROPOSED_GRANTS = ZaNewAdminWizard.STEP_INDEX ++ ;
ZaNewAdminWizard.STEP_PERMISSION = ZaNewAdminWizard.STEP_INDEX ++ ;
ZaNewAdminWizard.STEP_FINISH = ZaNewAdminWizard.STEP_INDEX ++ ;

//add the new admin button to the new menu list
ZaNewAdminWizard.initToolbarMethod = function () {
    //Need to test if current admin has the right to create an admin account
    var newMenu = this._toolbarOperations[ZaOperation.NEW_MENU] ;
    if (newMenu) {
        this._newAdminListener = new AjxListener(this, ZaAccountListController.prototype._newAdminListener);

        var menuOpList = newMenu.menuOpList ;
        menuOpList.push(new ZaOperation(
                ZaOperation.NEW_WIZARD, com_zimbra_delegatedadmin.ADMINBB_New_menuItem,
                com_zimbra_delegatedadmin.ADMINBB_New_tt, "DomainAdminUser", "DomainAdminUserDis", this._newAdminListener));
    }
}
ZaController.initToolbarMethods["ZaAccountListController"].push(ZaNewAdminWizard.initToolbarMethod);

ZaAccountListController.prototype._newAdminListener =
function(ev) {
	try {
		EmailAddr_XFormItem.resetDomainLists.call(this) ;
		var newAdmin = new ZaNewAdmin();
		newAdmin.getAttrs = {all:true};
		ZaApp.getInstance().dialogs["newAdminWizard"] = new ZaNewAdminWizard(this._container);

		ZaApp.getInstance().dialogs["newAdminWizard"].setObject(newAdmin);
		ZaApp.getInstance().dialogs["newAdminWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaAccountListController.prototype._newAdminListener", null, false);
	}
}

ZaNewAdminWizard.prototype.popup = function () {
    ZaXDialog.prototype.popup.call (this);
    this.goPage (ZaNewAdminWizard.STEP_START) ;
}

ZaNewAdminWizard.prototype.goPage = function (pageKey) {
    ZaXWizardDialog.prototype.goPage.call(this, pageKey) ;
    var prev = next = finish = cancel = true ;
    var skip = false ;
    if (pageKey == ZaNewAdminWizard.STEP_START) {
		prev = false;
        finish = false ;
    } else if (pageKey == ZaNewAdminWizard.STEP_NEW_ACCOUNT
                    || pageKey == ZaNewAdminWizard.STEP_NEW_GROUP) {
            //no change
    } else if (pageKey == ZaNewAdminWizard.STEP_UI_COMPONENTS) {
        prev = false ;
    } else if ( pageKey == ZaNewAdminWizard.STEP_PERMISSION ) {
        cancel = false ;
    } else if ( pageKey == ZaNewAdminWizard.STEP_FINISH) {
        next  =  false ;
    } else if (pageKey == ZaNewAdminWizard.STEP_PROPOSED_GRANTS) {
        skip = true ;
        this._localXForm.setInstanceValue(this._containedObject[ZaNewAdmin.A_proposedGrantsList], ZaNewAdmin.A_proposedGrantsList);
    }

	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(prev);
    this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(next);
    this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(finish);
    this._button[DwtDialog.CANCEL_BUTTON].setEnabled(cancel);

    this._button[ZaNewAdminWizard.SKIP_BUTTON].setVisible (skip) ;
    this._button[ZaNewAdminWizard.SKIP_BUTTON].setEnabled (skip) ;
}

/**
 *
 * @param params
 *          index: the index of the grant in the proposed grants
 *          msgs: the status message in the proposed grants status dialog
 */
ZaNewAdminWizard.prototype.configProposedGrants = function (params) {
    var grant = this._containedObject[ZaNewAdmin.A_proposedGrantsList][params.index] ;
    params.msgs.push (AjxMessageFormat.format(com_zimbra_delegatedadmin.msg_proposed_grants_start,
            [ZaNewAdminWizard.getProposedGrantMsg(grant, params)])) ;
    if (this.isGrantGranted (grant)) {
        params.msgs.push (com_zimbra_delegatedadmin.msg_proposed_grants_skipped) ;
        params.index ++ ;
        if (params.index >= this._containedObject[ZaNewAdmin.A_proposedGrantsList].length) {
            params.msgs.push (com_zimbra_delegatedadmin.msg_proposed_grants_done) ;
        } else {
            this.configProposedGrants(params) ;
        }
    } else {
        var callback = new AjxCallback (this, this.configProposedGrantsCallback, [params]) ;
        ZaGrant.grantMethod (grant, callback) ;
    }

    ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"].setMessage(params.msgs.join(""));
}

ZaNewAdminWizard.prototype.configProposedGrantsCallback =
function (params, resp) {
    if (!resp || resp.isException()) {
        params.msgs.push ("<font color='red'>" + com_zimbra_delegatedadmin.msg_proposed_grants_failed + "</font>") ;
    } else {
        params.msgs.push (com_zimbra_delegatedadmin.msg_proposed_grants_granted) ;
        if (!this._containedObject[ZaGrant.A2_grantsList])  {
            this._containedObject[ZaGrant.A2_grantsList] = [];
        }
        this._containedObject[ZaGrant.A2_grantsList].push(
                ZaUtil.deepCloneObject(this._containedObject[ZaNewAdmin.A_proposedGrantsList][params.index])) ;
        this._localXForm.setInstanceValue(this._containedObject[ZaGrant.A2_grantsList],
                ZaGrant.A2_grantsList); 
    }

    params.index ++ ;
    if (params.index >= this._containedObject[ZaNewAdmin.A_proposedGrantsList].length) {
        params.msgs.push (com_zimbra_delegatedadmin.msg_proposed_grants_done) ;
        ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"].setMessage (params.msgs.join(""));
    } else {
        ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"].setMessage (params.msgs.join(""));
        this.configProposedGrants(params) ;
    }
}

ZaNewAdminWizard.getProposedGrantMsg = function (grant, params) {
    if (!params.index) params.index = 0 ;
    var number = params.index + 1; 
    var msg = number + ") " + grant[ZaGrant.A_target] + "/" + grant[ZaGrant.A_target_type] + "/";
    if (grant[ZaGrant.A_deny]) {
        msg += "-" ;
    }

    if (grant[ZaGrant.A_canDelegate]) {
        msg += "+" ;
    }

    msg += grant[ZaGrant.A_right] ;

    return msg ;
}

ZaNewAdminWizard.prototype.goPrev =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	var prevStep ;
	if (cStep == ZaNewAdminWizard.STEP_NEW_ACCOUNT
            || cStep == ZaNewAdminWizard.STEP_NEW_GROUP) {
		prevStep = ZaNewAdminWizard.STEP_START ;
    }else if (cStep == ZaNewAdminWizard.STEP_PERMISSION) {
        if (this._containedObject [ZaNewAdmin.A_proposedGrantsList] &&
                    this._containedObject [ZaNewAdmin.A_proposedGrantsList].length > 0){
		    prevStep = ZaNewAdminWizard.STEP_PROPOSED_GRANTS ;
        } else {
            prevStep =  ZaNewAdminWizard.STEP_UI_COMPONENTS ;
        }
    }else if (cStep == ZaNewAdminWizard.STEP_PROPOSED_GRANTS) {
        prevStep = ZaNewAdminWizard.STEP_UI_COMPONENTS ;
    }else if (cStep == ZaNewAdminWizard.STEP_FINISH) {
        prevStep = ZaNewAdminWizard.STEP_PERMISSION ;            
    }

    this.goPage(prevStep);
}

ZaNewAdminWizard.prototype.goNext =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	var nextStep ;
	if (cStep == ZaNewAdminWizard.STEP_START) {
        if (this._containedObject[ZaNewAdmin.A_admin_type] == ZaItem.ACCOUNT) {
            nextStep = ZaNewAdminWizard.STEP_NEW_ACCOUNT ;
            this._containedObject[ZaNewAdmin.A_default_domain_admin_grp] = "FALSE" ;
        } else if  (this._containedObject[ZaNewAdmin.A_admin_type] == ZaItem.DL) {
            nextStep = ZaNewAdminWizard.STEP_NEW_GROUP ;
            this._containedObject[ZaNewAdmin.A_default_domain_admin_grp] = "TRUE" ;
        }
    } else if (cStep == ZaNewAdminWizard.STEP_NEW_ACCOUNT
            || cStep == ZaNewAdminWizard.STEP_NEW_GROUP) {
        if (this.createNewAdmin()) {
            nextStep = ZaNewAdminWizard.STEP_UI_COMPONENTS ;
            //get the inherited ui components
            ZaUIComponent.accountObjectModifer.call(this) ;

            //init the zimbraAdminConsoleUIComponents
            if (this._containedObject[ZaNewAdmin.A_default_domain_admin_grp] == "TRUE") {
                this._containedObject.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] =
                        ZaUtil.cloneArray (ZaNewAdminWizard.LEGACY_DA_VIEW) ;
            } else {
                this._containedObject.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] = [];
            }

        } else {
            return false ;
        }
    } else if (cStep == ZaNewAdminWizard.STEP_UI_COMPONENTS ) {
        if (ZaNewAdmin.modifyAdmin(this._containedObject)) {
            //set the proposed grants value
            this.setProposedGrants () ;
        
            if (this._containedObject [ZaNewAdmin.A_proposedGrantsList] &&
                    this._containedObject [ZaNewAdmin.A_proposedGrantsList].length > 0){
                nextStep = ZaNewAdminWizard.STEP_PROPOSED_GRANTS ;
            }else{
                nextStep = ZaNewAdminWizard.STEP_PERMISSION ;
            }
        }else {
            return false ;
        }
    } else if (cStep == ZaNewAdminWizard.STEP_PROPOSED_GRANTS) {
        //TODO: Create the proposed grants
        nextStep = ZaNewAdminWizard.STEP_PERMISSION ;
         //create the proposedGrants at the step_permission page
//        if (pageKey == ZaNewAdminWizard.STEP_PERMISSION) {
        if (this._containedObject[ZaNewAdmin.A_proposedGrantsList]
                && this._containedObject[ZaNewAdmin.A_proposedGrantsList].length > 0) {
            if (!ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"]){
                ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"] =
                    new ZaMsgDialog(ZaApp.getInstance().getAppCtxt().getShell());

                ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"].setSize (600, 120);
            }
            var params = {} ;
            params.msgs = [] ;
            params.msgs.push (com_zimbra_delegatedadmin.msg_proposed_grants_created) ;
            var dialog = ZaApp.getInstance().dialogs["createProposedGrantsListStatusDialog"] ;
            dialog.setMessage (params.msgs.join("")) ;
            dialog.popup ();

            params.index = 0 ;
            this.configProposedGrants(params) ;
        }
//        }
    }else if (cStep == ZaNewAdminWizard.STEP_PERMISSION ) {
        nextStep = ZaNewAdminWizard.STEP_FINISH ;
    } 

    this.goPage(nextStep);
}

ZaNewAdminWizard.prototype.isLegacyDAView = function () {
    var selectedViews = this._containedObject.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] ;
    if (selectedViews && selectedViews.length == ZaNewAdminWizard.LEGACY_DA_VIEW.length) {
        for (var i = 0; i < selectedViews.length; i ++ ) {
            if (ZaUtil.findValueInArray( ZaNewAdminWizard.LEGACY_DA_VIEW, selectedViews [i] ) < 0) {
                return false ;
            }
        }

        return true ;
    }

    return false ;
}

ZaNewAdminWizard.LEGACY_DA_VIEW =  [
                    ZaSettings.ACCOUNT_LIST_VIEW ,
                    ZaSettings.DL_LIST_VIEW,
                    ZaSettings.ALIAS_LIST_VIEW,
                    ZaSettings.RESOURCE_LIST_VIEW  ,
                    ZaSettings.SAVE_SEARCH
//                    ZaSettings.DOMAIN_LIST_VIEW
                ];

ZaNewAdminWizard.getDefaultDARights = function (object) {
    var tmpGrantsList = [] ;
    if (object != null) {
        var domainAdminRight = {} ;
        domainAdminRight [ZaGrant.A_grantee] = object [ZaAccount.A_name] ;
        domainAdminRight [ZaGrant.A_grantee_id] = object.id ;
        domainAdminRight [ZaGrant.A_grantee_type] =  ZaGrant.getGranteeTypeByItemType (object.type) ;
        domainAdminRight [ZaGrant.A_right] = "domainAdminConsoleRights" ;
        domainAdminRight [ZaGrant.A_right_type] = "combo" ;
        domainAdminRight [ZaGrant.A_target] = ZaAccount.getDomain (object.name) ;
        domainAdminRight [ZaGrant.A_target_type] = ZaItem.DOMAIN ;
        tmpGrantsList.push (domainAdminRight)  ;

        var domainAdminZimletRight = {} ;
        domainAdminZimletRight [ZaGrant.A_grantee] = object [ZaAccount.A_name] ;
        domainAdminZimletRight [ZaGrant.A_grantee_id] = object.id ;
        domainAdminZimletRight [ZaGrant.A_grantee_type] = ZaGrant.getGranteeTypeByItemType (object.type) ;
        domainAdminZimletRight [ZaGrant.A_right] = "domainAdminZimletRights" ;
        domainAdminZimletRight [ZaGrant.A_right_type] = "combo" ;
        domainAdminZimletRight [ZaGrant.A_target] = ZaGrant.GLOBAL_TARGET_NAME;
        domainAdminZimletRight [ZaGrant.A_target_type] = ZaItem.GLOBAL_GRANT ;
        tmpGrantsList.push(domainAdminZimletRight)  ;
    }
    return tmpGrantsList ;

}
ZaNewAdminWizard.prototype.setProposedGrants = function () {
    var proposedGrantsList = this._containedObject [ZaNewAdmin.A_proposedGrantsList] = [] ;
    var tmpGrantsList = [] ;
    if ((this._containedObject [ZaNewAdmin.A_default_domain_admin_grp] == "TRUE")   
        && this.isLegacyDAView())
    {
        tmpGrantsList =  ZaNewAdminWizard.getDefaultDARights (this._containedObject)  ;
        /*
        var domainAdminRight = {} ;
        domainAdminRight [ZaGrant.A_grantee] = this._containedObject [ZaAccount.A_name] ;
        domainAdminRight [ZaGrant.A_grantee_id] = this._containedObject.id ;
        domainAdminRight [ZaGrant.A_grantee_type] = ZaGrant.GRANTEE_TYPE.grp ;
        domainAdminRight [ZaGrant.A_right] = "domainAdminConsoleRights" ;
        domainAdminRight [ZaGrant.A_right_type] = "combo" ;
        domainAdminRight [ZaGrant.A_target] = ZaAccount.getDomain (this._containedObject.name) ;
        domainAdminRight [ZaGrant.A_target_type] = ZaItem.DOMAIN ;
        tmpGrantsList.push (domainAdminRight)  ;

        var domainAdminZimletRight = {} ;
        domainAdminZimletRight [ZaGrant.A_grantee] = this._containedObject [ZaAccount.A_name] ;
        domainAdminZimletRight [ZaGrant.A_grantee_id] = this._containedObject.id ;
        domainAdminZimletRight [ZaGrant.A_grantee_type] = ZaGrant.GRANTEE_TYPE.grp ;
        domainAdminZimletRight [ZaGrant.A_right] = "domainAdminZimletRights" ;
        domainAdminZimletRight [ZaGrant.A_right_type] = "combo" ;
        domainAdminZimletRight [ZaGrant.A_target] = ZaGrant.GLOBAL_TARGET_NAME;
        domainAdminZimletRight [ZaGrant.A_target_type] = ZaItem.GLOBAL_GRANT ;
        tmpGrantsList.push(domainAdminZimletRight)  ;   */

   }else{
        //not the legacy da view, so we will assign the rights based on the view
        var selectedViews = this._containedObject.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] ;

        if (selectedViews && selectedViews.length > 0) {
            for (var i = 0; i < selectedViews.length; i ++ ) {
                if (ZaSettings.VIEW_RIGHTS[selectedViews[i]] != null) {
                    var viewRight = {} ;
                    viewRight [ZaGrant.A_right] = ZaSettings.VIEW_RIGHTS[selectedViews[i]] ;

                    if (viewRight [ZaGrant.A_right] == null) {
                        continue ; //this view doesn't need any right;
                    }

                    viewRight [ZaGrant.A_grantee] = this._containedObject [ZaAccount.A_name] ;
                    viewRight [ZaGrant.A_grantee_id] = this._containedObject.id  ;
                    if (this._containedObject.type == ZaItem.ACCOUNT) {
                       viewRight [ZaGrant.A_grantee_type] = ZaGrant.GRANTEE_TYPE.usr ;
                    } else if (this._containedObject.type == ZaItem.DL) {
                       viewRight [ZaGrant.A_grantee_type] = ZaGrant.GRANTEE_TYPE.grp ;
                    }

                    viewRight [ZaGrant.A_right_type] = "combo" ;

                    if (selectedViews[i] == ZaSettings.ACCOUNT_LIST_VIEW
                        || selectedViews[i] == ZaSettings.DL_LIST_VIEW
                        || selectedViews[i] == ZaSettings.ALIAS_LIST_VIEW
                        || selectedViews[i] == ZaSettings.RESOURCE_LIST_VIEW
                        || selectedViews[i] == ZaSettings.DOMAIN_LIST_VIEW
                        || selectedViews[i] == ZaSettings.SAVE_SEARCH     
                       ) {
                        //we use minimum set rights principle to guess what the target is, here we use the domain the admin belongs to
                        viewRight [ZaGrant.A_target] = ZaAccount.getDomain (this._containedObject.name) ;
                        viewRight [ZaGrant.A_target_type] = ZaItem.DOMAIN ;
                    } else if (selectedViews[i] == ZaSettings.COS_LIST_VIEW
                        || selectedViews[i] == ZaSettings.SERVER_LIST_VIEW
                        || selectedViews[i] == ZaSettings.ZIMLET_LIST_VIEW
                        || selectedViews[i] == ZaSettings.ADMIN_ZIMLET_LIST_VIEW
                        || selectedViews[i] == ZaSettings.GLOBAL_CONFIG_VIEW
                        || selectedViews[i] == ZaSettings.GLOBAL_STATUS_VIEW
                        || selectedViews[i] == ZaSettings.MAILQ_VIEW
                        || selectedViews[i] == ZaSettings.XMBX_SEARCH_VIEW 
                        || selectedViews[i] == ZaSettings.BACKUP_VIEW    ) {
                        //here the target are mostly global
                        viewRight [ZaGrant.A_target] = ZaGrant.GLOBAL_TARGET_NAME;
                        viewRight [ZaGrant.A_target_type] = ZaItem.GLOBAL_GRANT ;
                    } else {
                        //this view doesn't need a right or can't decide the target name, so just skip
                        continue ;
                    }

                    tmpGrantsList.push(viewRight) ;
                }
            }
        }
   }
    
   for (var i =0 ; i < tmpGrantsList.length; i ++) {
        if (!this.isGrantGranted(tmpGrantsList[i]))  {
            proposedGrantsList.push(tmpGrantsList[i]) ;
        }
   }
}

ZaNewAdminWizard.prototype.isGrantGranted = function (grant) {
    var currentGrantList = this._containedObject[ZaGrant.A2_grantsList] ;
    if (currentGrantList && currentGrantList.length > 0) {
        for (var i = 0; i < currentGrantList.length; i ++ ) {
            var cGrant = currentGrantList[i] ;
            var compKeys = [ZaGrant.A_grantee, 
                           ZaGrant.A_target, ZaGrant.A_target_type,
                           ZaGrant.A_right ] ;
            var isExist = i ;
            for (var j =0; j < compKeys.length; j ++) {
                var k = compKeys[j] ;
                var cv =  cGrant[k] ;
                var v = grant[k] ;

               if (cv != v) {
                    isExist = -1 ;
                    break ;
                }
            }

            if (isExist >= 0) {
                return true ;
            }
        }
    }

    return false ;
}


ZaNewAdminWizard.prototype.createNewAdmin = function () {
   //check if the account exists already
   if (ZaSearch.isAccountExist.call(this, {name: this._containedObject[ZaAccount.A_name], popupError: true})) {
       return false ;
   }
   //check if passwords match
   if(this._containedObject.attrs[ZaAccount.A_password]) {
       if(this._containedObject.attrs[ZaAccount.A_password] != this._containedObject[ZaAccount.A2_confirmPassword]) {
           ZaApp.getInstance().getCurrentController().popupErrorDialog(ZaMsg.ERROR_PASSWORD_MISMATCH);
           return false;
       }
   }

   //Everything looks good, create account now
   return ZaNewAdmin.createAdmin(this._containedObject)  ;
}


ZaNewAdminWizard.prototype.finishWizard = function () {
    var cStep = this._containedObject[ZaModel.currentStep] ;
    var isNewAdminCreated = false ;
    if (cStep == ZaNewAdminWizard.STEP_NEW_ACCOUNT
            || cStep == ZaNewAdminWizard.STEP_NEW_GROUP) {
        //create the account
        isNewAdminCreated = this.createNewAdmin () ;
    } else if (cStep == ZaNewAdminWizard.STEP_PERMISSION ) {
        //do thing since the permissions are saved already
        isNewAdminCreated = true ;
    } else if (cStep == ZaNewAdminWizard.STEP_UI_COMPONENTS ) {
        isNewAdminCreated = true ;
        //save the UI components by modifyAccount or modifyDL
        ZaNewAdmin.modifyAdmin(this._containedObject) ; 
    } else if (cStep == ZaNewAdminWizard.STEP_FINISH) {
        isNewAdminCreated = true ;        
    } else if (cStep == ZaNewAdminWizard.STEP_PROPOSED_GRANTS) {
        isNewAdminCreated = true;
        //TODO create the proposed grants
    }

    if (isNewAdminCreated) {
        //refresh the account list view.
        if (this._containedObject.type == ZaItem.ACCOUNT) {
            ZaApp.getInstance().getAccountListController().fireCreationEvent(this._containedObject);
        }else if (this._containedObject.type == ZaItem.DL) {
            ZaApp.getInstance().getDistributionListController().fireChangeEvent(this._containedObject);
        }
        this.popdown () ;
    }
}

ZaNewAdminWizard.prototype.skipWizard = function () {
    var cStep = this._containedObject[ZaModel.currentStep] ;
    //skip button should only be enabled and visible at the STEP_PROPOSED_GRANTS
    var nextStep ;
	if (cStep == ZaNewAdminWizard.STEP_PROPOSED_GRANTS) {
        nextStep = ZaNewAdminWizard.STEP_PERMISSION ;
        this.goPage(nextStep);
    }
}

ZaNewAdminWizard.prototype.setObject = function (entry)  {
    this._containedObject = {};
    this._containedObject.attrs = {} ; 

    for (var a in entry) {
        if (a == "attrs") {
            for (var a in entry.attrs) {
                this._containedObject.attrs[a] = entry.attrs[a];
            }
        }else{
            this._containedObject[a] = entry [a] ;
        }
    }

    if (!this._containedObject[ZaNewAdmin.A_admin_type]) {
        this._containedObject[ZaNewAdmin.A_admin_type] = ZaItem.ACCOUNT ;        
    }

    this._containedObject[ZaModel.currentStep] = ZaNewAdminWizard.STEP_START ;
    this._localXForm.setInstance (this._containedObject) ;   
}

ZaNewAdminWizard.myXFormModifier = function (xFormObject) {
    var cases = [] ;
    var case_start_pick_account_group = {
        type: _CASE_,  numCols:2, colSizes:["200px","300px"],
        tabGroupKey:ZaNewAdminWizard.STEP_START, caseKey:ZaNewAdminWizard.STEP_START,
        items: [
            { ref: ZaNewAdmin.A_admin_type, type: _OSELECT1_,
                label: com_zimbra_delegatedadmin.Label_admin_type , labelLocation:_LEFT_, 
                visibilityChecks:[],
                enableDisableChecks:[],
                choices: this.newAdminTypesChoices } ,
            { type : _SPACER_, height: 20 },
            { type : _OUTPUT_, colSpan: 2,value: com_zimbra_delegatedadmin.Help_new_administrator }
        ]

    };
    cases.push (case_start_pick_account_group) ;

    var case_account = {
        type: _CASE_,  numCols:1, 
        tabGroupKey:ZaNewAdminWizard.STEP_NEW_ACCOUNT, caseKey:ZaNewAdminWizard.STEP_NEW_ACCOUNT,
        items: [
        {type:_ZAWIZGROUP_,items:[
           {ref:ZaAccount.A_name, type:_EMAILADDR_,    required:true,
                 msgName:ZaMsg.NAD_AccountName,label:ZaMsg.NAD_AccountName,
                 labelLocation:_LEFT_,forceUpdate:true,
                visibilityChecks:[],
                enableDisableChecks:[]
                //TODO: may need the onchange method
//                onChange: ZaAccount.setDomainChanged
            },
            {ref:ZaAccount.A_password, type:_SECRET_, msgName:ZaMsg.NAD_Password,
				label:ZaMsg.NAD_Password, labelLocation:_LEFT_,
                visibilityChecks:[],
                enableDisableChecks:[],
                cssClass:"admin_xform_name_input"
			},
			{ref:ZaAccount.A2_confirmPassword, type:_SECRET_, msgName:ZaMsg.NAD_ConfirmPassword,
				label:ZaMsg.NAD_ConfirmPassword, labelLocation:_LEFT_,
                visibilityChecks:[],
                enableDisableChecks:[],
                cssClass:"admin_xform_name_input"
			},
			{ref:ZaAccount.A_zimbraPasswordMustChange,  type:_CHECKBOX_,
                visibilityChecks:[],
                enableDisableChecks:[],
                msgName:ZaMsg.NAD_MustChangePwd,label:ZaMsg.NAD_MustChangePwd,trueValue:"TRUE", falseValue:"FALSE"},
            {ref:ZaAccount.A_zimbraIsAdminAccount, type:_CHECKBOX_,
                msgName:ZaMsg.NAD_IsSystemAdminAccount,label:ZaMsg.NAD_IsSystemAdminAccount,
                visibilityChecks:[],
                enableDisableChecks:[],
                elementChanged :
                function(elementValue,instanceValue, event) {
                    if(elementValue == "TRUE") {
                        this.setInstanceValue("FALSE", ZaAccount.A_zimbraIsDelegatedAdminAccount);

                    } else{
                        this.setInstanceValue("TRUE", ZaAccount.A_zimbraIsDelegatedAdminAccount);                        
                    }
                    this.getForm().itemChanged(this, elementValue, event);
                    this.getForm().parent._button[DwtWizardDialog.NEXT_BUTTON].setEnabled((!(elementValue && elementValue == "TRUE")));
                },
                bmolsnr:true, trueValue:"TRUE", falseValue:"FALSE"
            },
            ZaAccount.getAdminRolesItem ()               
        ]
    }]};
    
    try {
	    if(ZaPosixAccount && zimbra_posixaccount_ext && zimbra_posixaccount_ext) {
	    	zimbra_posixaccount_ext.ACC_WIZ_GROUP.items[0].choices = ZaApp.getInstance().getPosixGroupIdListChoices(true);
			case_account.items.push(zimbra_posixaccount_ext.ACC_WIZ_GROUP);
	    }     
	    if(ZaSamAccount && Zambra && ZaSamAccount.ACC_WIZ_GROUP) {
	    	ZaSamAccount.ACC_WIZ_GROUP.items[0].choices = ZaApp.getInstance().getSambaDomainSIDListChoices();
	    	case_account.items.push(ZaSamAccount.ACC_WIZ_GROUP);
	    }
    } catch (ex) {
    	//
    }
    cases.push (case_account) ;
    
    var case_group = {
            type: _CASE_,
            tabGroupKey:ZaNewAdminWizard.STEP_NEW_GROUP, caseKey:ZaNewAdminWizard.STEP_NEW_GROUP,
            items: [
                    {ref:ZaAccount.A_name, type:_EMAILADDR_,
                        label:ZaMsg.LBL_LabelListName,
                        labelLocation:_LEFT_,forceUpdate:true,
                        visibilityChecks:[],
                        enableDisableChecks:[]
                        //TODO: may need the onchange method
//              onChange: ZaAccount.setDomainChanged
                    } ,
                {ref: ZaNewAdmin.A_default_domain_admin_grp ,  type:_CHECKBOX_, visibilityChecks:[],
                enableDisableChecks:[],label:com_zimbra_delegatedadmin.LB_Default_admin_group,trueValue:"TRUE", falseValue:"FALSE" },

                ZaAccount.getAdminRolesItem ()   
            ]
    }
    cases.push (case_group) ;

    var case_permission = {
            type: _CASE_,  numCols: 1, 
            tabGroupKey:ZaNewAdminWizard.STEP_PERMISSION, caseKey:ZaNewAdminWizard.STEP_PERMISSION ,
            items:[
                {type: _GROUP_, numCols: 2 , width: 530, colSizes:[100, "*"],items: [
                    {type:_OUTPUT_, ref: ZaAccount.A_name , label: com_zimbra_delegatedadmin.Label_grantee_name}
                  ]
                }
            ]
    }
    case_permission.items = case_permission.items.concat(
            ZaTargetPermission.getGrantsListXFormItem ({width: 530, height: 200, by: ZaGrant.A_grantee}));
    cases.push (case_permission) ;

    var case_ui_comp = {
            type: _CASE_,  numCols: 1,
            tabGroupKey:ZaNewAdminWizard.STEP_UI_COMPONENTS, caseKey:ZaNewAdminWizard.STEP_UI_COMPONENTS,
            items:[
                    {type: _GROUP_, numCols:2, colSizes:[200, "*"],items: [
                        {type: _DWT_ALERT_, content: com_zimbra_delegatedadmin.msg_admin_created,
                            colSpan: "*",
                            visibilityChecks:[],
                           containerCssStyle: "width:500px;",
                           style: DwtAlert.INFORMATION, iconVisible: false
                        },                    
                        {type:_OUTPUT_, ref: ZaAccount.A_name , label: ZaMsg.NAD_AccountName},
                        {type:_SPACER_, height: 10}    
                        ]
                    }
                ]
    } ;
    case_ui_comp.items = case_ui_comp.items.concat (
            ZaUIComponent.getUIComponentsXFormItem(220));
    cases.push (case_ui_comp) ;

    var case_proposed_grants = {
        type: _CASE_,  numCols: 1,
        tabGroupKey:ZaNewAdminWizard.STEP_PROPOSED_GRANTS, caseKey:ZaNewAdminWizard.STEP_PROPOSED_GRANTS ,
        items:[
                {type:_OUTPUT_, ref: ZaAccount.A_name , valueChangeEventSources:[ZaAccount.A_name],
                    getDisplayValue: function (newValue) {
                        return  AjxMessageFormat.format(
                                com_zimbra_delegatedadmin.Help_proposed_grantsList,[newValue]) ;
                    }
                } ,
                { type: _SPACER_ , height: 10 },    
                {
                    ref: ZaNewAdmin.A_proposedGrantsList, id: ZaNewAdmin.A_proposedGrantsList, type: _DWT_LIST_,
// doesn't work in list view:   valueChangeEventSources:[ZaNewAdmin.A_proposedGrantsList], 
                    width:530, height: 200,
                    cssClass: "DLSource", widgetClass: ZaGrantsListView,
                    headerList: ZaGrantsListView._getHeaderList (530, ZaGrant.A_grantee),
                    hideHeader: false ,
//                    onSelection:ZaGrantsListView.grantSelectionListener,
                    multiselect: true
                } 
        ]
    }
    cases.push (case_proposed_grants) ;

    var case_finish = {
            type: _CASE_,
            tabGroupKey:ZaNewAdminWizard.STEP_FINISH, caseKey:ZaNewAdminWizard.STEP_FINISH,
            items: [
                {ref: ZaNewAdmin.A_admin_type,  type: _OUTPUT_,
                    valueChangeEventSources:[ZaNewAdmin.A_admin_type],
                    getDisplayValue: function (newValue) {
                        var instance = this.getInstance () ;
                        var name = instance.name ;
                        var viewName ;

                        if (instance [ ZaNewAdmin.A_admin_type] == ZaItem.ACCOUNT) {
                            viewName = com_zimbra_delegatedadmin.Help_new_admin_summary_account ;
                        } else if (instance [ ZaNewAdmin.A_admin_type] == ZaItem.DL) {
                            viewName = com_zimbra_delegatedadmin.Help_new_admin_summary_dl ;
                        }

                        return  AjxMessageFormat.format(
                                com_zimbra_delegatedadmin.Help_new_admin_summary,[name, viewName]) ;
                    }
                }
            ]
    }
    cases.push (case_finish) ;

    xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep,
                choices:this.stepChoices, valueChangeEventSources:[ZaModel.currentStep]},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width: (AjxEnv.isIE ? 580: 550) , align:_LEFT_,
                valign:_TOP_, items:cases}
		];
}
ZaXDialog.XFormModifiers["ZaNewAdminWizard"].push(ZaNewAdminWizard.myXFormModifier);

//TODO : create a ZaProposedGrantsList to allow adding checkbox to the list



