/**
The modification to the New Account Wizard and Account View
*/

ZaSettings.isYahooPA = true ;
function SMBAccount() {}

SMBAccount.TYPE_STARTER = "1" ;
SMBAccount.TYPE_STANDARD = "2" ;
SMBAccount.TYPE_PRO = "3" ;
SMBAccount.SUSPENDED_STATUS = "closed" ; //TODO: may need to change the status value

ZaAccount.Y_account_type_starter = "account_type_starter" ;
ZaAccount.Y_account_type_standard = "account_type_standard" ;
ZaAccount.Y_account_type_pro = "account_type_pro" ;
ZaAccount.Y_yahoo_id  = "yahoo_id" ;
ZaAccount.Y_notification_email = "notification_email" ;

ZaAccount.Y_PAMaxAccounts = "PAMaxAccounts" ;
ZaAccount.Y_PAAvailableAccounts = "PAAvailableAccount";

var yahoo_attrs_xmodel = [
        {id: ZaAccount.Y_yahoo_id, ref: "yahoo/" + ZaAccount.Y_yahoo_id , type: _STRING_, required: true},
        {id: ZaAccount.Y_account_type_starter, ref: "yahoo/" + ZaAccount.Y_account_type_starter , type: _ENUM_, defaultValue: true},
        {id: ZaAccount.Y_account_type_standard, ref: "yahoo/" + ZaAccount.Y_account_type_standard , type: _ENUM_, defaultValue: false},
        {id: ZaAccount.Y_account_type_pro, ref: "yahoo/" + ZaAccount.Y_account_type_pro , type: _ENUM_, defaultValue: false} ,
        {id: ZaAccount.Y_notification_email, ref: "yahoo/" + ZaAccount.Y_notification_email , type: _EMAIL_ADDRESS_},
        {id: ZaAccount.Y_PAMaxAccounts, ref: "yahoo/" + ZaAccount.Y_PAMaxAccounts , type: _NUMBER_},
        {id: ZaAccount.Y_PAAvailableAccounts, ref: "yahoo/" + ZaAccount.Y_PAAvailableAccounts , type: _NUMBER_}
    ];

if (ZaAccount.myXModel) {
    //console.log("Before SMBAccount Attributes: ZaAccount.myXModle has " + ZaAccount.myXModel.items.length + " items") ;
    ZaAccount.myXModel.items = ZaAccount.myXModel.items.concat(yahoo_attrs_xmodel) ;
    //console.log("After SMBAccount Attributes: ZaAccount.myXModle has " + ZaAccount.myXModel.items.length + " items") ;
}

SMBAccount.init = function () {
    this["yahoo"] = { } ;
    this["yahoo"][ZaAccount.Y_account_type_starter] = true ;
    this["yahoo"][ZaAccount.Y_account_type_standard] = false ;
    this["yahoo"][ZaAccount.Y_account_type_pro] = false ;
}

SMBAccount.setYahooAttrsValue = function (yahoo, soapDoc) {
    if (yahoo == null) return ;
    for (var aname in yahoo) {
        if (aname == ZaAccount.Y_notification_email) {
            var el = soapDoc.set("yahoo", yahoo[aname]) ;
            el.setAttribute("n", aname) ;
        }
    }
}

/**
 * Check the SMB Yahoo Attributes,
 * @param yahoo
 * @return true: no error found
 *          false: found error
 */
SMBAccount.checkValue = function (yahoo) {
    return true ;
    /** No any value is required for the PA admin
    if (yahoo != null
            && yahoo[ZaAccount.Y_notification_email] != null && yahoo[ZaAccount.Y_notification_email].indexOf("@") > 0) {
        return true ;
    }
    return false ;                               */
}

SMBAccount.setObject = function (entry) {
    if (entry.yahoo) {
        this._containedObject.yahoo = entry.yahoo ;
    }else{
        this._containedObject.yahoo = {};
    }

    //retrieve PA user account limit
    var usedAvailableAccounts = SMBAccount.getUsedAvailableAccounts (SMBAccount.TYPE_STARTER, this._app);
    this._containedObject["yahoo"][ZaAccount.Y_PAAvailableAccounts] = usedAvailableAccounts[1] ;
    this._containedObject["yahoo"][ZaAccount.Y_PAMaxAccounts] = usedAvailableAccounts[0] + usedAvailableAccounts[1] ;    

}

SMBAccount.getRadioRefByAccountType = function (type) {
    switch (type) {
        case SMBAccount.TYPE_STARTER:
            return ZaAccount.Y_account_type_starter ;
        case SMBAccount.TYPE_STANDARD :
            return ZaAccount.Y_account_type_standard ;
        case SMBAccount.TYPE_PRO :
            return ZaAccount.Y_account_type_pro ;
    }
}

SMBAccount.getAccountTypeChoicesByCosName  = function () {
    return [
        {value: SMBAccount.getCosValueByAccountType( SMBAccount.TYPE_STARTER), label : com_zimbra_yahoosmb.AccountTypeStarterPA },
        {value: SMBAccount.getCosValueByAccountType( SMBAccount.TYPE_STANDARD), label : com_zimbra_yahoosmb.AccountTypeStandard },
        {value: SMBAccount.getCosValueByAccountType( SMBAccount.TYPE_PRO), label : com_zimbra_yahoosmb.AccountTypePro }
    ]   ;
}


SMBAccount.getAccountTypeChoicesByCosId  = function (cosList) {

    return [
        {value: SMBAccount.getCosIdByAccountType( cosList, SMBAccount.TYPE_STARTER), label : com_zimbra_yahoosmb.AccountTypeStarterPA },
        {value: SMBAccount.getCosIdByAccountType( cosList, SMBAccount.TYPE_STANDARD), label : com_zimbra_yahoosmb.AccountTypeStandard },
        {value: SMBAccount.getCosIdByAccountType( cosList, SMBAccount.TYPE_PRO), label : com_zimbra_yahoosmb.AccountTypePro }
    ]   ;
}

SMBAccount.getCosValueByAccountType = function (type) {
    switch (type) {
        case SMBAccount.TYPE_STARTER:
            return "starter" ;
        case SMBAccount.TYPE_STANDARD :
            return "standard" ;
        case SMBAccount.TYPE_PRO :
            return "pro" ;
    }
}

SMBAccount.getCosIdByAccountType = function (cosList, type) {
    var cosName = SMBAccount.getCosValueByAccountType (type) ;
    var cosListArray = cosList.getArray () ;
    var cnt = cosListArray.length;
	for(var i = 0; i < cnt; i++) {
		if(cosListArray[i].name == cosName) {
			return cosListArray[i].id;
		}
	}
}

SMBAccount.setAccountTypeInstanceValue = function (value) {
    var acctTypeArr = [
           SMBAccount.TYPE_STARTER, SMBAccount.TYPE_STANDARD, SMBAccount.TYPE_PRO
        ] ;

    for (var i =0 ; i < acctTypeArr.length; i ++) {
        if (this.accountType == acctTypeArr [i]) {
            this.setInstanceValue(value,  SMBAccount.getRadioRefByAccountType(acctTypeArr [i])) ;
        }else{
            this.setInstanceValue(!value,  SMBAccount.getRadioRefByAccountType(acctTypeArr [i])) ;
        }
    }

}

SMBAccount.onYahooIdChanged = function (value, event, form){
    console.log("Yahoo ID Changed to " + value) ;
    this.setInstanceValue(value); //, "yahoo/" + SMBAccount.Y_yahoo_id) ;
}

SMBAccount.accountTypeChanged = function (value, event, form) {
    console.log("Changed Acount Type to " + this.accountType) ;

    //change the radio value
    var radioItem = this.items[0] ;
    if (!radioItem.getElement().checked) {
        radioItem.getElement().checked = true ;
    }

    //change the account type instance value
    SMBAccount.setAccountTypeInstanceValue.call (this, true);

    //change the cos id
    var cosValue = SMBAccount.getCosValueByAccountType (this.accountType);
    var app = form.parent._app ;
    var allCoses =  form.getController().getCosList().getArray () ;
    var newCos = null ;
    for (var i =0 ; i < allCoses.length; i ++) {
        if (cosValue == allCoses[i].name) {
            newCos = allCoses[i];
            break ;
        }
    }

    var instance = form.getInstance();
    instance.cos = newCos ;
    instance.attrs[ZaAccount.A_COSId] = newCos.id ;
	form.parent._isCosChanged = true ;
    form.refresh ();
}

if(ZaXDialog.XFormModifiers["ZaNewAccountXWizard"]) {
    SMBAccount.NewAccountWizXFormModifier = function(xFormObject) {
        //Account Type Selection  Group
        var accountTypeGroup =
            {type:_ZAWIZ_TOP_GROUPER_, label:com_zimbra_yahoosmb.NAD_AccountTypeGrouper,
                    id:"account_wiz_type_group",numCols:3,
                items:[
                    { type:_SMB_ACCOUNT_TYPE_RADIO_, accountType: SMBAccount.TYPE_STARTER,
                        isActive: true ,
                        onChange: SMBAccount.accountTypeChanged  },
                    { type:_SMB_ACCOUNT_TYPE_RADIO_, accountType: SMBAccount.TYPE_STANDARD,
                        isActive: false ,
                        onChange:  SMBAccount.accountTypeChanged},
                    { type:_SMB_ACCOUNT_TYPE_RADIO_, accountType: SMBAccount.TYPE_PRO,
                        isActive: false ,
                        onChange: SMBAccount.accountTypeChanged } ,


                    //The approaches to update the used accounts:
                    // 1) assign ref to be ZaAccount.A2_usedDomainAccounts which is changed when the new wizard is launched
                    //      and the getDisplayValue will use the properties value to change the dispaly value

                    { type: _OUTPUT_, ref:  ZaAccount.Y_PAAvailableAccounts, getDisplayValue: function () {
                            var instance = this.getForm().getInstance() ;
                            var availableAccounts = this.getInstanceValue () ;
                            var usedAccounts = instance["yahoo"][ZaAccount.Y_PAMaxAccounts] - availableAccounts ;
                            return  AjxMessageFormat.format(com_zimbra_yahoosmb.AccountsAvailable,
                                    [usedAccounts, availableAccounts]) ;
                        }
                    },

                    // 2) forceUpdate will rerun the getDisplayValue
                    /*{ type: _OUTPUT_, forceUpdate: true, getDisplayValue:function() {
                                return SMBAccount.getAvailableAccountsOutput(SMBAccount.TYPE_STARTER, this.getForm().parent._app);
                            }
                    },*/
                   { type: _OUTPUT_, value: AjxMessageFormat.format(com_zimbra_yahoosmb.AccountsAvailable, [0,0]) },
                   { type: _OUTPUT_, value: AjxMessageFormat.format(com_zimbra_yahoosmb.AccountsAvailable, [0,0]) }
                ]
            }

        //change General tab   - case1 in NewAccountXWizard
		var generalTab = xFormObject.items[3].items[0];
        var generalItems = generalTab.items ;
        var cnt = generalItems.length;
		for(var i = 0; i < cnt; i ++) {
			if(generalItems[i].id == "account_wiz_name_group" && generalItems[i].items) {
                //add the accountTypeGroup before the name group
                generalItems.splice(i, 0, accountTypeGroup);
				break;
			}
		}
    }

    ZaXDialog.XFormModifiers["ZaNewAccountXWizard"].push(SMBAccount.NewAccountWizXFormModifier);
}

if(ZaTabView.XFormModifiers["ZaAccountXFormView"]) {
    SMBAccount.AccountXFormModifier = function(xFormObject) {
        //Account Type Selection  Group
        var accountTypeGroup =
            {type:_TOP_GROUPER_, label:com_zimbra_yahoosmb.NAD_AccountTypeGrouper,
                    id:"account_type_group",numCols:3,
                items:[
                    { type:_SMB_ACCOUNT_TYPE_RADIO_, accountType: SMBAccount.TYPE_STARTER,
                        isActive: true ,
                        onChange: SMBAccount.accountTypeChanged  },
                    { type:_SMB_ACCOUNT_TYPE_RADIO_, accountType: SMBAccount.TYPE_STANDARD,
                        isActive: false ,
                        onChange:  SMBAccount.accountTypeChanged},
                    { type:_SMB_ACCOUNT_TYPE_RADIO_, accountType: SMBAccount.TYPE_PRO,
                        isActive: false ,
                        onChange: SMBAccount.accountTypeChanged } ,

                    { type: _OUTPUT_, ref: "yahoo/" + ZaAccount.Y_PAAvailableAccounts, getDisplayValue: function () {
                            var instance = this.getForm().getInstance() ;
                            var availableAccounts = this.getInstanceValue () ;
                            var usedAccounts = instance["yahoo"][ZaAccount.Y_PAMaxAccounts] - availableAccounts ;
                            return  AjxMessageFormat.format(com_zimbra_yahoosmb.AccountsAvailable,
                                    [usedAccounts, availableAccounts]) ;
                        }
                    },
                    { type: _OUTPUT_, value: AjxMessageFormat.format(com_zimbra_yahoosmb.AccountsAvailable, [0,0]) },
                    { type: _OUTPUT_, value: AjxMessageFormat.format(com_zimbra_yahoosmb.AccountsAvailable, [0,0]) } 
                ]
            }

        //change General tab   - case1 in AccountXFormView
		var generalTab = xFormObject.items[2].items[0];
        var generalItems = generalTab.items ;
        var cnt = generalItems.length;
		for(var i = 0; i < cnt; i ++) {
			if(generalItems[i].id == "account_form_name_group" && generalItems[i].items) {
                //add the accountTypeGroup before the name group
                generalItems.splice(i, 0, accountTypeGroup);
				break;
			}
		}
    }

    ZaTabView.XFormModifiers["ZaAccountXFormView"].push(SMBAccount.AccountXFormModifier);
}


SMBAccountType_Radio_XFormItem = function() {}
XFormItemFactory.createItemType("_SMB_ACCOUNT_TYPE_RADIO_", "smb_account_type_radio",
        SMBAccountType_Radio_XFormItem, Composite_XFormItem)

SMBAccountType_Radio_XFormItem.prototype.numCols=3;
SMBAccountType_Radio_XFormItem.prototype.colSizes=["20px","20px", "100px"];
SMBAccountType_Radio_XFormItem.prototype.items = [];

SMBAccountType_Radio_XFormItem.prototype.getIcon =  function (accountType) {
    switch (accountType) {
        case SMBAccount.TYPE_STARTER:
            return "Account" ;
        case SMBAccount.TYPE_STANDARD :
            return "Resource" ;
        case SMBAccount.TYPE_PRO :
            return "Group" ;
    }
}

SMBAccountType_Radio_XFormItem.prototype.getLabel =  function (accountType) {
   switch (accountType) {
        case SMBAccount.TYPE_STARTER:
            return com_zimbra_yahoosmb.AccountTypeStarterPA;
        case SMBAccount.TYPE_STANDARD :
            return com_zimbra_yahoosmb.AccountTypeStandard ;
        case SMBAccount.TYPE_PRO :
            return com_zimbra_yahoosmb.AccountTypePro ;
    }
}

SMBAccountType_Radio_XFormItem.prototype.initializeItems = function() {
    var acctType = this.getInheritedProperty("accountType");
    var isActive = this.getInheritedProperty("isActive");
    var grpName =  "___smb_account_type_grp" ;
    var radio = {ref: SMBAccount.getRadioRefByAccountType(acctType),   type:_RADIO_, groupname:grpName,
                    //value: (acctType == SMBAccount.TYPE_STARTER) ?  true :false, //default check starter type
                    relevant: isActive, relevantBehavior: _DISABLE_ ,
                    elementChanged:function(elementValue,instanceValue, event) {
                            this.getForm().itemChanged(this.getParentItem(), null, event);
                        },
                   updateElement:function(value) {
                        if (AjxEnv.hasFirebug) console.log("Updating the radio button " + acctType+ " value to " + value);
                        this.getElement().checked = value;
                    }
                };
    var icon =  {type:_AJX_IMAGE_, src: this.getIcon (acctType)} ;
    var label = {type:_OUTPUT_, value:  this.getLabel(acctType),
                    cssStyle: isActive ? "" : "color: #686357" , //#686357
                    onClick: function (event) {
                        if (isActive) {
                            this.getForm().itemChanged(this.getParentItem(), null, event);
                        }
                    }
                };

	this.items = [radio, icon, label];                  
    this.accountType = acctType ;
    
    Composite_XFormItem.prototype.initializeItems.call(this);
}

/**
 * Relevant behavior on the parent doesn't work very well
 * Disable/Enable the element when isRelevant
 */
/*
SMBAccountType_Radio_XFormItem.prototype.setElementEnabled = function (enable) {
    console.log("Disable the Account Type - Standard");
    var radioEl = null ;
    var iconEl = null ;
    var labelEl = null ;
   
    for (var i=0; i < this.items.length; i ++) {
        if (this.items[i].getType () == _RADIO_) {
            radioEl = this.items[i].getElement () ;
        }else if (this.items[i].getType () == _AJX_IMAGE_) {

        }else if (this.items[i].getType () == _OUTPUT_) {
            labelEl = this.items[i].getElement () ;
        }
    }

    if (radioEl & enable) {
        radioEl.style.display = "inline" ;
    }
} 

SMBAccountType_Radio_XFormItem.prototype.enableElement = function () {
    console.log("Enable the Account Type - Starter") ;
}   */

SMBAccount.getSMBDomainList = function (app) {
    var list = new ZaItemList(ZaDomain, app);
    //For now, only the current domain will be returned for the Yahoo SMB domain admin.
    //Might have more domains in the future.
    var resp = ZaDomain.getDomainRequest ("name", ZaSettings.myDomainName, app);
    if(resp != null) {
        list.loadFromJS(resp);
    }
	return list;
}

SMBAccount.getSMBDomain = function (name, app) {
    var domainList =  app.getDomainList ();
    if (domainList != null) {
        var domainArr = domainList.getArray () ;
        for (var i =0; i < domainArr.length; i ++) {
            if (name == domainArr[i]["name"]){
                return domainArr[i] ;                          
            }
        }
    }
}                                               

/**
 * return the used and available accounts output
 * @param accountType
 */

SMBAccount.getAvailableAccountsOutput = function (accountType, app) {
    var usedAvailable = [0,0];
    switch (accountType) {
        case SMBAccount.TYPE_STARTER:
            usedAvailable = SMBAccount.getUsedAvailableAccounts(SMBAccount.TYPE_STARTER, app) ;
            break ;
        case SMBAccount.TYPE_STANDARD :
        case SMBAccount.TYPE_PRO :
             break ;
    }

    return AjxMessageFormat.format(com_zimbra_yahoosmb.AccountsAvailable, usedAvailable);
}

/**
 *
 * @param accountType
 * @return [NumberOfUsedAccounts, NumberOfAvailableAccounts]
 */
SMBAccount.getUsedAvailableAccounts = function (accountType, app) {
    var dn =   ZaSettings.myDomainName ;
    var used = ZaSearch.getUsedDomainAccounts (dn, app.getCurrentController()) ;
    var totalAvailable =  SMBAccount.getSMBDomain(dn, app).attrs[ZaDomain.A_domainMaxAccounts];
    var left = totalAvailable - used  ;
    return [used, left];
}

SMBAccount.openHelpDesk =
function() {
    //TODO: 1) may need to use zimbraHelpDelegatedURL
    var url = "http://yahoo.com" ;  //this url needs to be changed in the future
    window.open(url);
}

SMBAccount.logOff = function () {
    //TODO: 1) it is better to be a internal call to the YIS
    //2) Use zimbraAdminLogoutURL
    var logoffUrl = "http://www.zimbra.com" ;
    
    ZmCsfeCommand.clearAuthToken();
	window.onbeforeunload = null;

	// NOTE: Mozilla sometimes handles UI events while the page is
	//       unloading which references classes and objects that no
	//       longer exist. So we put up the busy veil and reload
	//       after a short delay.
	var shell = DwtShell.getShell(window);
	shell.setBusy(true);
	/*
	var locationStr = location.protocol + "//" + location.hostname
            + ((location.port == '80') ? "" : ":" +location.port)
            + location.pathname
            //we want to add the query string as well
            + location.search;
    */
    var act = new AjxTimedAction(null, ZaZimbraAdmin.redir, [logoffUrl]);
	AjxTimedAction.scheduleAction(act, 100);
}

SMBAccount.getAllDomainAccounts = function (domainName) {
     var searchParams = {
          limit : 0 , //all
          type : [ZaSearch.ACCOUNTS] ,
          domain: domainName ,
          applyCos:  0,
          attrs: [ZaAccount.A_zimbraMailCatchAllAddress]
        }
    var resp =  ZaSearch.searchDirectory (searchParams).Body.SearchDirectoryResponse ;
    var list = new ZaItemList(null, null);
	list.loadFromJS(resp);
    return list.getArray() ;
}

SMBAccount.getCatchAllDomain = function (domainName) {
    return "@" + domainName ;
}

SMBAccount.getCatchAllChoices = function (domainName) {
    var accounts = SMBAccount.getAllDomainAccounts (domainName) ;
   var choices = [{value:"", label: com_zimbra_yahoosmb.L_none}] ;
    for (var i=0; i < accounts.length; i++) {
        choices.push ({ value: accounts[i].id, label: accounts[i].name}) ;
    }
    return choices ;                                           
}
                                                               
//find the catch all account for the domain
SMBAccount.getCatchAllAccount = function (domainName) {
   var accounts = SMBAccount.getAllDomainAccounts (domainName) ;
    for (var i=0; i < accounts.length; i++) {
        if (accounts [i].attrs[ZaAccount.A_zimbraMailCatchAllAddress] == SMBAccount.getCatchAllDomain(domainName)) {
            return accounts [i].id;
        }
   }

    return "" ;
}

//++++++++++Modify CatchAll +++++++++++++++++++++++++
ZaAccount.modifyCatchAll =
function (accountId, domainName) {
    if (accountId == null | accountId.length <= 0) {
        return ;
    }
    var soapDoc = AjxSoapDoc.create("ModifySmbAccountRequest", ZaZimbraAdmin.URN, null);
	soapDoc.set("id", accountId);
    var catchAllDomain = "" ;
    if (domainName == null || domainName.length == 0) {
        //remove the catchAll value from the account
        catchAllDomain = "" ;
    }else if (domainName.indexOf("@") != 0) {
        catchAllDomain = SMBAccount.getCatchAllDomain (domainName) ;
    }else {
        catchAllDomain = domainName ;
    }
    var el = soapDoc.set("a", catchAllDomain) ;

    el.setAttribute("n", ZaAccount.A_zimbraMailCatchAllAddress) ;

    var command = new ZmCsfeCommand();
	var params = new Object();
	params.soapDoc = soapDoc;
	command.invoke(params);
}


/********************************************************************************************
 * Confirmation Dialog
 */
function PACreateUserConfirmDialog (parent, app, w, h) {
    if (arguments.length == 0) return;
    this._app = app;
    this._standardButtons = [DwtDialog.OK_BUTTON];
    	
    ZaXDialog.call(this, parent, app, null, com_zimbra_yahoosmb.CreateAccountConfirm_Title, w, h,"PACreateUserConfirmDialog");

	this._containedObject = new ZaSearch();
	this.initForm(this.getMyXModel(),this.getMyXForm());
}

PACreateUserConfirmDialog.prototype = new ZaXDialog;
PACreateUserConfirmDialog.prototype.constructor = PACreateUserConfirmDialog;

//PACreateUserConfirmDialog.A_accountId = "id" ;
PACreateUserConfirmDialog.A_name = "name" ;
PACreateUserConfirmDialog.A_confirmURL = "url" ;
PACreateUserConfirmDialog.A_invitation = "invitation" ;
PACreateUserConfirmDialog.A_emailAddr = "emailAddr" ;

PACreateUserConfirmDialog.prototype.getMyXModel = function () {
     var xModel = {
         items: [
             {id: PACreateUserConfirmDialog.A_name, ref: PACreateUserConfirmDialog.A_name,
                 type: _STRING_ },
             {id: PACreateUserConfirmDialog.A_confirmURL, ref: PACreateUserConfirmDialog.A_confirmURL,
                 type: _STRING_ },
             {id: PACreateUserConfirmDialog.A_invitation, ref: PACreateUserConfirmDialog.A_invitation,
                              type: _STRING_ },
             {id: PACreateUserConfirmDialog.A_emailAddr, ref: PACreateUserConfirmDialog.A_emailAddr,
                              type: _STRING_ }
         ]
     }
    
     return xModel ;
}

PACreateUserConfirmDialog.prototype.getMyXForm =
function() {
	var xFormObject = {
		numCols:2,
		items:[
            {type:_GROUP_, numCols: 1, colSpan: "*", items :[
                { type: _OUTPUT_,  value: "<h2>" + com_zimbra_yahoosmb.ConfirmDlgTitle0 + "</h2>"},
                { type: _OUTPUT_,  value:  com_zimbra_yahoosmb.ConfirmDlgTitle1}
              ]
            },
            { type: _OUTPUT_, ref: PACreateUserConfirmDialog.A_name,
                    label: com_zimbra_yahoosmb.ConfirmDlgName },
            { type: _OUTPUT_, ref: PACreateUserConfirmDialog.A_emailAddr,
                    label:com_zimbra_yahoosmb.ConfirmDlgEmailAddr },
            { type: _OUTPUT_, ref: PACreateUserConfirmDialog.A_invitation,
                    label:com_zimbra_yahoosmb.ConfirmDlgInvitation },
            { type: _OUTPUT_, ref: PACreateUserConfirmDialog.A_confirmURL,
                    label:com_zimbra_yahoosmb.ConfirmDlgURL },
            { type: _SPACER_ },
            {type:_GROUP_, numCols: 1,  colSpan: "*", items :[
                { type: _OUTPUT_,  value: com_zimbra_yahoosmb.ConfirmDlgEndMsg }
               ]
            }
        ]
	}
	return xFormObject;
}


PACreateUserConfirmDialog.prototype.popup =
function (account) {
    var email = account.attrs[ZaAccount.A_mail] ;
    var url = "https://integrationServer/register/" + account.id ;
    var sentTo = account.yahoo [ZaAccount.Y_notification_email];
    var invitation ;
    if (sentTo!=null && sentTo.length > 0) {
       invitation = AjxMessageFormat.format(com_zimbra_yahoosmb.ConfirmDlgEmailSentTo, [sentTo]);
    }else{
       invitation = com_zimbra_yahoosmb.no_sent_to_invitation ; 
    }
    var name = account.attrs[ZaAccount.A_displayname];

    this._containedObject[PACreateUserConfirmDialog.A_name] = name  ;
    this._containedObject[PACreateUserConfirmDialog.A_confirmURL] = url;
    this._containedObject[PACreateUserConfirmDialog.A_invitation] = invitation ;
    this._containedObject[PACreateUserConfirmDialog.A_emailAddr] = email;

    this._localXForm.setInstance(this._containedObject);
	ZaXDialog.prototype.popup.call(this);
}


//-Send Notification Email Operation ----------------------------------------
ZaOperation.SEND_PA_ACCOUNT_NOTIFICATION = ++ZA_OP_INDEX ;

ZaAccountListController.prototype._sendNotificationButtonListener =
function (ev) {
   if(this._contentView.getSelectionCount() == 1) {
		var item = this._contentView.getSelection()[0];
		//launch the notification dialog
       if(!this._app.dialogs["sendInvitationDialog"]) {
            this._app.dialogs["sendInvitationDialog"] =
                new PASendNotificationDialog (this._app._container,  this._app,
                        "480px", "100px");
        }

       this._app.dialogs["sendInvitationDialog"].popup(item);
    }
}


/** *******************************************************************************************************
 * Send Notification Dialog
 */
function PASendNotificationDialog (parent, app, w, h) {
    if (arguments.length == 0) return;
    this._app = app;
    this._standardButtons = [DwtDialog.CANCEL_BUTTON];
    var sendButton = new DwtDialog_ButtonDescriptor(PASendNotificationDialog.SEND_BUTTON,
            com_zimbra_yahoosmb.Bt_send, DwtDialog.ALIGN_RIGHT,
            new AjxCallback(this, this.sendNotice));
    this._extraButtons = [sendButton];
    ZaXDialog.call(this, parent, app, null, com_zimbra_yahoosmb.SendNotificatioin_Title, w, h,"PASendNotificationDialog");

    this._containedObject = {} ;
this.initForm(this.getMyXModel(),this.getMyXForm());
}

PASendNotificationDialog.prototype = new ZaXDialog;
PASendNotificationDialog.prototype.constructor = PASendNotificationDialog;
PASendNotificationDialog.SEND_BUTTON = ++DwtDialog.LAST_BUTTON ;

//PASendNotificationDialog.A_accountId = "id" ;
PASendNotificationDialog.A_name = "name" ;
PASendNotificationDialog.A_confirmURL = "url" ;
PASendNotificationDialog.A_invitation = "invitation" ;
PASendNotificationDialog.A_emailAddr = "emailAddr" ;

PASendNotificationDialog.prototype.getMyXModel = function () {
 var xModel = {
     items: [
         {id: PASendNotificationDialog.A_name, ref: PASendNotificationDialog.A_name,
             type: _STRING_ },
         {id: PASendNotificationDialog.A_confirmURL, ref: PASendNotificationDialog.A_confirmURL,
             type: _STRING_ },
         {id: PASendNotificationDialog.A_invitation, ref: PASendNotificationDialog.A_invitation,
                          type: _EMAIL_ADDRESS_,  required: true },
         {id: PASendNotificationDialog.A_emailAddr, ref: PASendNotificationDialog.A_emailAddr,
                          type: _EMAIL_ADDRESS_ }
     ]
 }

 return xModel ;
}

PASendNotificationDialog.prototype.getMyXForm =
function() {
    var xFormObject = {
        numCols:2,
        items:[
            { type: _OUTPUT_, ref: PASendNotificationDialog.A_name,
                    label: com_zimbra_yahoosmb.ConfirmDlgName },
            { type: _OUTPUT_, ref: PASendNotificationDialog.A_emailAddr,
                    label:com_zimbra_yahoosmb.ConfirmDlgEmailAddr },
            { type: _OUTPUT_, ref: PASendNotificationDialog.A_confirmURL,
                    label:com_zimbra_yahoosmb.ConfirmDlgURL },
            { type: _TEXTFIELD_, ref: PASendNotificationDialog.A_invitation, width: 200, 
                    label:com_zimbra_yahoosmb.NotificationEmail }
        ]
    }
    return xFormObject;
}


PASendNotificationDialog.prototype.popup =
function (account) {
    var email = account.name ;
    var url = "https://integrationServer/register/" + account.id ;
    var name = account.attrs[ZaAccount.A_displayname];

    this._containedObject[PASendNotificationDialog.A_name] = name  ;
    this._containedObject[PASendNotificationDialog.A_confirmURL] = url;
    this._containedObject[PASendNotificationDialog.A_emailAddr] = email;
    this._containedObject.id = account.id ;
    this._containedObject[PASendNotificationDialog.A_invitation] = "" ;

    this._localXForm.setInstance(this._containedObject);
    ZaXDialog.prototype.popup.call(this);
}

PASendNotificationDialog.prototype.sendNotice = function () {
    this.popdown () ; //popdown the dialog first
    try {

		var soapDoc = AjxSoapDoc.create("SendInvitationRequest", ZaZimbraAdmin.URN, null);
	    soapDoc.set("id", this._containedObject.id);
        soapDoc.set("name", this._containedObject[PASendNotificationDialog.A_emailAddr]) ;
        soapDoc.set("sendto", this._containedObject[PASendNotificationDialog.A_invitation]);
        var csfeParams = new Object();
		csfeParams.soapDoc = soapDoc;
		var reqMgrParams = {} ;
		reqMgrParams.controller = this._app.getCurrentController();
		reqMgrParams.busyMsg = com_zimbra_yahoosmb.BUSY_SENDING_INVITATION ;

		resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.SendInvitationResponse;

    } catch (ex) {
		this._app.getCurrentController().popupErrorDialog(
                AjxMessageFormat.format(com_zimbra_yahoosmb.ex_send_invitation_failed,
                                [this._containedObject[PASendNotificationDialog.A_invitation]] ),
                ex);
		return null;
	}
}






