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

ZaNewAdmin.getMyXModel = function () {
   return {
       items: [
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
           ZaTargetPermission.grantListItem ,
           ZaUIComponent.UIComponentsItem,
           ZaUIComponent.InheritedUIComponentsItem
       ]
   }
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

        if (createdAdmin.name == tmpObj.name) {
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
            if (respBody.ModifyAccountResponse.account[0].name == tmpObj.name) {
                return true ;
            }
        } else if (tmpObj[ZaNewAdmin.A_admin_type] == ZaItem.DL) {
            if (respBody.ModifyDistributionListResponse.dl[0].name == tmpObj.name) {
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
    ZaXWizardDialog.call(this, parent, null, com_zimbra_delegatedadmin.title_new_admin_wizard, 
            (AjxEnv.isIE ? "620px" : "570px"), (AjxEnv.isIE ? "330px" :"320px"),"ZaNewAdminWizard");

    this.newAdminTypesChoices = [
        {value: ZaItem.ACCOUNT, label: com_zimbra_delegatedadmin.type_account },
        {value: ZaItem.DL, label: com_zimbra_delegatedadmin.NAD_IsAdminGroup}
    ];

    this.stepChoices = [
        {label: com_zimbra_delegatedadmin.NA_Wizard_pick_admin_type, value: ZaNewAdminWizard.STEP_START },
        {label: com_zimbra_delegatedadmin.NA_Wizard_new_admin_acct, value: ZaNewAdminWizard.STEP_NEW_ACCOUNT },
        {label: com_zimbra_delegatedadmin.NA_Wizard_new_admin_dl, value: ZaNewAdminWizard.STEP_NEW_GROUP },
        {label: com_zimbra_delegatedadmin.NA_Wizard_set_permission, value: ZaNewAdminWizard.STEP_PERMISSION },
        {label: com_zimbra_delegatedadmin.NA_Wizard_config_ui, value: ZaNewAdminWizard.STEP_UI_COMPONENTS },
        {label: com_zimbra_delegatedadmin.NA_Wizard_finish_summary, value: ZaNewAdminWizard.STEP_FINISH }
    ];
    this.initForm (ZaNewAdmin.getMyXModel (), this.getMyXForm ()) ;
    this._localXForm.setController(ZaApp.getInstance());
    this._helpURL = location.pathname + ZaUtil.HELP_URL + "da_process/admin_wizard.htm?locid="+AjxEnv.DEFAULT_LOCALE;
}

ZaNewAdminWizard.prototype = new ZaXWizardDialog;
ZaNewAdminWizard.prototype.constructor = ZaNewAdminWizard;
ZaXDialog.XFormModifiers["ZaNewAdminWizard"] = [] ;

ZaNewAdminWizard.STEP_INDEX = 1;
ZaNewAdminWizard.STEP_START = ZaNewAdminWizard.STEP_INDEX ++ ;
ZaNewAdminWizard.STEP_NEW_ACCOUNT = ZaNewAdminWizard.STEP_INDEX ++ ;
ZaNewAdminWizard.STEP_NEW_GROUP = ZaNewAdminWizard.STEP_INDEX ++ ;
ZaNewAdminWizard.STEP_PERMISSION = ZaNewAdminWizard.STEP_INDEX ++ ;
ZaNewAdminWizard.STEP_UI_COMPONENTS = ZaNewAdminWizard.STEP_INDEX ++ ;
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
                com_zimbra_delegatedadmin.ADMINBB_New_tt, "Account", "AccountDis", this._newAdminListener));
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
    if (pageKey == ZaNewAdminWizard.STEP_START) {
		prev = false;
        finish = false ;
    } else if (pageKey == ZaNewAdminWizard.STEP_NEW_ACCOUNT
                    || pageKey == ZaNewAdminWizard.STEP_NEW_GROUP) {


    } else if (pageKey == ZaNewAdminWizard.STEP_PERMISSION) {
        cancel = prev = false ;
    } else if ( pageKey == ZaNewAdminWizard.STEP_UI_COMPONENTS ) {
        cancel = false ;
    } else if ( pageKey == ZaNewAdminWizard.STEP_FINISH) {
        next  = cancel = false ;
    }

	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(prev);
    this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(next);
    this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(finish);
    this._button[DwtDialog.CANCEL_BUTTON].setEnabled(cancel);

}

ZaNewAdminWizard.prototype.goPrev =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	var prevStep ;
	if (cStep == ZaNewAdminWizard.STEP_NEW_ACCOUNT
            || cStep == ZaNewAdminWizard.STEP_NEW_GROUP) {
		prevStep = ZaNewAdminWizard.STEP_START ;
    }else if (cStep == ZaNewAdminWizard.STEP_UI_COMPONENTS) {
		prevStep = ZaNewAdminWizard.STEP_PERMISSION ;
    }else if (cStep == ZaNewAdminWizard.STEP_FINISH) {
        prevStep = ZaNewAdminWizard.STEP_UI_COMPONENTS ;            
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
        } else if  (this._containedObject[ZaNewAdmin.A_admin_type] == ZaItem.DL) {
            nextStep = ZaNewAdminWizard.STEP_NEW_GROUP ;
        }
    } else if (cStep == ZaNewAdminWizard.STEP_NEW_ACCOUNT
            || cStep == ZaNewAdminWizard.STEP_NEW_GROUP) {

        if (this.createNewAdmin()) {
            nextStep = ZaNewAdminWizard.STEP_PERMISSION ;
        } else {
            return false ;
        }
    } else if (cStep == ZaNewAdminWizard.STEP_PERMISSION ) {

        nextStep = ZaNewAdminWizard.STEP_UI_COMPONENTS ;
        //init the zimbraAdminConsoleUIComponents
        this._containedObject.attrs[ZaAccount.A_zimbraAdminConsoleUIComponents] = [];
        //get the inherited ui components
        ZaUIComponent.accountObjectModifer.call(this) ;
    } else if (cStep == ZaNewAdminWizard.STEP_UI_COMPONENTS ) {
        if (ZaNewAdmin.modifyAdmin(this._containedObject)) {
            nextStep = ZaNewAdminWizard.STEP_FINISH ;
        }else{
            return false ;
        }
    } 

    this.goPage(nextStep);
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
        type: _CASE_,  numCols:2, height: 250,
        tabGroupKey:ZaNewAdminWizard.STEP_NEW_ACCOUNT, caseKey:ZaNewAdminWizard.STEP_NEW_ACCOUNT,
        items: [
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
    }
    cases.push (case_account) ;
    
    var case_group = {
            type: _CASE_,
            tabGroupKey:ZaNewAdminWizard.STEP_NEW_GROUP, caseKey:ZaNewAdminWizard.STEP_NEW_GROUP,
            items: [
                    {ref:ZaAccount.A_name, type:_EMAILADDR_,
                        label:ZaMsg.DLXV_LabelListName + ": ",
                        labelLocation:_LEFT_,forceUpdate:true,
                        visibilityChecks:[],
                        enableDisableChecks:[]
                        //TODO: may need the onchange method
//              onChange: ZaAccount.setDomainChanged
                    }
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
                        {type:_OUTPUT_, ref: ZaAccount.A_name , label: ZaMsg.NAD_AccountName},
                        {type:_SPACER_, height: 10}    
                        ]
                    }
                ]
    } ;
    case_ui_comp.items = case_ui_comp.items.concat (
            ZaUIComponent.getUIComponentsXFormItem(220));
    cases.push (case_ui_comp) ;

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




