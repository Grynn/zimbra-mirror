/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2006, 2007, 2008, 2009 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
if (AjxEnv.hasFirebug) console.log("Start loading com_zimbra_license.js");

//ZaLicense
function ZaLicense() {
	ZaItem.call(this,  "ZaLicense");
	this._init();
	this.type = ZaItem.LICENSE ;
}
ZaLicense.prototype = new ZaItem ;
ZaLicense.prototype.constructor = ZaLicense ;

ZaSettings.IS_ZCS_NETWORK_VERSION = true ;

if (ZaOperation) ZaOperation.UPDATELICENSE = ++ZA_OP_INDEX;
ZaItem.LICENSE = "license";

ZaLicense.A_accountsLimit = "AccountsLimit";
ZaLicense.A_attachmentConversionEnabled = "AttachmentConversionEnabled";
ZaLicense.A_backupEnabled = "BackupEnabled";
ZaLicense.A_crossMailboxSearchEnabled = "CrossMailboxSearchEnabled";
ZaLicense.A_hierarchicalStorageManagementEnabled = "HierarchicalStorageManagementEnabled";
ZaLicense.A_iSyncAccountsLimit = "ISyncAccountsLimit";
ZaLicense.A_installType ="InstallType";
ZaLicense.A_issuedOn ="IssuedOn";
ZaLicense.A_issuedToEmail = "IssuedToEmail";
ZaLicense.A_issuedToName = "IssuedToName";
ZaLicense.A_licenseId = "LicenseId";
ZaLicense.A_MAPIConnectorAccountsLimit = "MAPIConnectorAccountsLimit";
ZaLicense.A_mobileSyncAccountsLimit = "MobileSyncAccountsLimit";
ZaLicense.A_mobileSyncEnabled = "MobileSyncEnabled";
ZaLicense.A_resellerName = "ResellerName" ;
ZaLicense.A_validFrom = "ValidFrom";
ZaLicense.A_validUntil = "ValidUntil";
ZaLicense.InstallStatusMsg = "LicenseInstallStatusMsg";
ZaLicense.Info_TotalAccounts = "TotalAccounts" ;
//ZaLicense.GlobalConfigTabIndex = 9;

ZaLicense.myXModel = {
	items: [ 
		{id: ZaLicense.A_accountsLimit, type: _STRING_, ref: "attrs/" + ZaLicense.A_accountsLimit},
		{id: ZaLicense.A_attachmentConversionEnabled, type: _STRING_, ref: "attrs/" + ZaLicense.A_attachmentConversionEnabled},
		{id: ZaLicense.A_backupEnabled, type: _STRING_, ref: "attrs/" + ZaLicense.A_backupEnabled },
		{id: ZaLicense.A_crossMailboxSearchEnabled, type: _STRING_, ref: "attrs/" + ZaLicense.A_crossMailboxSearchEnabled },
		{id: ZaLicense.A_hierarchicalStorageManagementEnabled, type: _STRING_, ref: "attrs/" + ZaLicense.A_hierarchicalStorageManagementEnabled },	
		{id: ZaLicense.A_iSyncAccountsLimit, type: _STRING_, ref: "attrs/" + ZaLicense.A_iSyncAccountsLimit },
		{id: ZaLicense.A_installType, type: _STRING_, ref: "attrs/" + ZaLicense.A_installType },
		{id: ZaLicense.A_issuedOn, type: _STRING_, ref: "attrs/" + ZaLicense.A_issuedOn },
		{id: ZaLicense.A_issuedToEmail, type: _STRING_, ref: "attrs/" + ZaLicense.A_issuedToEmail },
		{id: ZaLicense.A_issuedToName, type: _STRING_, ref: "attrs/" + ZaLicense.A_issuedToName },		
		{id: ZaLicense.A_licenseId, type: _STRING_, ref: "attrs/" + ZaLicense.A_licenseId },
		{id: ZaLicense.A_MAPIConnectorAccountsLimit, type: _STRING_, ref: "attrs/" + ZaLicense.A_MAPIConnectorAccountsLimit },
		{id: ZaLicense.A_mobileSyncEnabled, type: _STRING_, ref: "attrs/" + ZaLicense.A_mobileSyncEnabled},
		{id: ZaLicense.A_mobileSyncAccountsLimit, type: _STRING_, ref: "attrs/" + ZaLicense.A_mobileSyncAccountsLimit },
		{id: ZaLicense.A_resellerName, type: _STRING_, ref: "attrs/" + ZaLicense.A_resellerName },
		{id: ZaLicense.A_validFrom, type: _STRING_, ref: "attrs/" + ZaLicense.A_validFrom },
		{id: ZaLicense.A_validUntil, type: _STRING_, ref: "attrs/" + ZaLicense.A_validUntil },
		{id: ZaLicense.InstallStatusMsg, type: _STRING_, ref: ZaLicense.InstallStatusMsg}
	]
}

ZaLicense.prototype.init = function (resp) {
	this.initFromJS (resp.license[0]) ;
	this.initInfo (resp.info[0]) ;
}

ZaLicense.prototype.initFromJS = 
function (obj) {
	if(!obj)
		return;
	
	this.attrs = new Object();
	if(obj.attr) {
		var len = obj.attr.length;
		for(var ix = 0; ix < len; ix++) {
			if(!this.attrs[[obj.attr[ix].name]]) {
				this.attrs[[obj.attr[ix].name]] = obj.attr[ix]._content;
			} else {
				/*
				if(!(this.attrs[[obj.attr[ix].name]] instanceof Array)) {
					this.attrs[[obj.attr[ix].name]] = [this.attrs[[obj.attr[ix].name]]];
				} */
				this.attrs[[obj.attr[ix].name]].push(obj.attr[ix]._content);
			}
		}
	}
}

ZaLicense.prototype.initInfo = 
function (obj) {
	if (!obj) return ;

	var info = {};
	ZaLicense.prototype.initFromJS.call(info, obj);
	this.info = info.attrs ;
}

ZaLicense.prototype.getServerUTCTime = function () {
	var serverTime = this.info.ServerTime ;
	//must use the UTC date format
	var formatter = new AjxDateFormat("yyyyMMddHHmmss'Z'");
	var serverDateTime = new Date();
	serverDateTime.setTime(serverTime);
	var serverUTCdateTime = formatter.format (serverDateTime) ;
	
	if (AjxEnv.hasFirebug) console.log ("Server Time is " + serverUTCdateTime) ;
	return serverUTCdateTime ;
}

ZaLicense.getLicenseInfo = 
function (insObj, form, callback) {
	var soapDoc = AjxSoapDoc.create("GetLicenseRequest", "urn:zimbraAdmin", null);
	var getLicenseCmd = new ZmCsfeCommand ();
	var params = {};
	params.soapDoc = soapDoc; 

	callback = callback || new AjxCallback (insObj, ZaLicense.getLicenseCallback, form);
	params.asyncMode = true;
	params.callback = callback ;
	getLicenseCmd.invoke(params);
	
}

ZaLicense.getLicenseCallback =
function (form, resp) {
	var adminObj = form.parent._appCtxt._appController ;
	try {
		if (resp._isException) {
			throw new AjxException(com_zimbra_license.LI_GETLICENSE_ERROR_MSG,  AjxException.UNKNOWN_ERROR, "ZaLicense.getLicenseCallback", resp._data.msg) ;
		}
		var getLicenseResp = resp._data.Body.GetLicenseResponse ;
		if (getLicenseResp.license) {
			if (! this._license) {
				this._license = new ZaLicense();
			}
			
			this._license.init (getLicenseResp);
			
			var validateFromUTCDateTime = this._license.attrs[ZaLicense.A_validFrom] ;
			//refresh the license status
			ZaLicense.setLicenseStatus(adminObj) ;
			var instance = form.getInstance();
			ZaLicense.addLicense(instance, this._license);
			instance[ZaLicense.InstallStatusMsg] = ZaLicenseInstallWizard.getInstallMsg();
			
			form.setInstance(instance) ;
			ZaLicenseInstallWizard.INSTALL_STATUS = -1;
		}
		
		DBG.println("Set the tab by current license information");
	}catch (ex){
		ZaLicenseInstallWizard.INSTALL_STATUS = -1;
		adminObj.popupErrorDialog(ex.msg, ex, true);
	}
} 

//add the license attributes into the current instance
ZaLicense.addLicense =
function (instance , license){
	//parse the license date string
	license.attrs[ZaLicense.A_validFrom] = ZaLicense.getLocaleString(license.attrs[ZaLicense.A_validFrom])  ;
	license.attrs[ZaLicense.A_validUntil] = ZaLicense.getLocaleString(license.attrs[ZaLicense.A_validUntil]) ;
	license.attrs[ZaLicense.A_issuedOn] = ZaLicense.getLocaleString(license.attrs[ZaLicense.A_issuedOn])  ;
	
	//parse the account limit string
	license.attrs[ZaLicense.A_MAPIConnectorAccountsLimit] = ZaLicense.getAccountNumber(license.attrs[ZaLicense.A_MAPIConnectorAccountsLimit]);
	license.attrs[ZaLicense.A_accountsLimit] = ZaLicense.getAccountNumber(license.attrs[ZaLicense.A_accountsLimit]);
	license.attrs[ZaLicense.A_iSyncAccountsLimit] = ZaLicense.getAccountNumber(license.attrs[ZaLicense.A_iSyncAccountsLimit]);
	license.attrs[ZaLicense.A_mobileSyncAccountsLimit] = ZaLicense.getAccountNumber(license.attrs[ZaLicense.A_mobileSyncAccountsLimit]);
	
	//add the info sections 
	if (license.info[ZaLicense.Info_TotalAccounts] >= 0){
		license.attrs[ZaLicense.A_accountsLimit] += " " 
			+ AjxMessageFormat.format(com_zimbra_license.LI_ACCOUNTS_USED,[license.info[ZaLicense.Info_TotalAccounts]]);
	}else if (license.info[ZaLicense.Info_TotalAccounts] == -1){ //bug 23101 - user counting is not finish yet
		license.attrs[ZaLicense.A_accountsLimit] += " " + com_zimbra_license.LI_ACCOUNT_COUNTING ;
	}else{
		license.attrs[ZaLicense.A_accountsLimit] += " " + com_zimbra_license.LI_ACCOUNT_COUNT_ERROR ;
	}
	
	if (!instance){
		instance = {};
		instance.attrs = license.attrs ;
	}else{		
		if (!instance.attrs) {
			instance.attrs = {} ;
		}
		for(var name in license.attrs) {
			instance.attrs[name] = license.attrs[name];
		}	
	}	
}

ZaLicense.EXPIRED = false ;
ZaLicense.WILL_EXPIRE = false ;
ZaLicense.NOT_YET_VALID = false ;

//set the low left status message
ZaLicense.setLicenseStatus =
function (adminObj){
	var callback = new AjxCallback(adminObj, ZaLicense.setLicenseStatusCallback, adminObj) ;
	ZaLicense.getLicenseInfo(adminObj, null, callback);
}

ZaLicense.setLicenseStatusCallback =
function (adminObj, resp) {
	try {
		if (resp._isException) {
			//TODO, check the LicenseException and set the status
			ZaLicense.setNoLicenseStatusMsg(adminObj);
			throw new AjxException(com_zimbra_license.LI_GETLICENSE_ERROR_MSG,  AjxException.UNKNOWN_ERROR, "ZaLicense.setLicenseStatusCallback", resp._data.msg) ;
			
			//throw new Error(resp._data.msg) ;
		}
		var getLicenseResp = resp._data.Body.GetLicenseResponse ;
		if (getLicenseResp.license) {
			if (! this._license) {
				this._license = new ZaLicense();
			}
			this._license.init(getLicenseResp);
			ZaLicense.setLicenseStatusMsg (adminObj, this._license) ;
		}
	}catch (ex){
		adminObj.popupErrorDialog(ex.msg, ex, true);
	}
	
}
ZaLicense.setNoLicenseStatusMsg = 
function (adminObj) {
	var result = { 
			message: "", 
			className: "" 
		};
	result.message = com_zimbra_license.LI_NOLICENSE;
	result.className = "consoleLicenseExpired" ;
	adminObj._statusBox.getHtmlElement().className = result.className;
	adminObj.setStatusMsg(result.message);
}
/*
 * for trial licenses, the trial expriation date should still be displayed in the lower left at all times 	 
 * for any license, no dialog should pop up on login if the license is 31+ days from expiring 	 
 * for any license, a dialog should pop up on login if the license is 30 days or less from expiring, or has expired
 */
ZaLicense.setLicenseStatusMsg =
function (adminObj, license) {
	var result = { 
			message: "", 
			className: "" 
		};
	
	var expirationTime = (ZaLicense.parseLicenseDateTime(license.attrs [ZaLicense.A_validUntil])).getTime();
	var _1day = 24 * 60 * 60 * 1000 
	var _30days = 30 * _1day ;
	var now = (new Date());
	var expiredDays ;
	var daysLeft ;
	
	if (expirationTime <= now) {
		expiredDays = Math.ceil(( now - expirationTime)/_1day);
		ZaLicense.EXPIRED = true;
		ZaLicense.WILL_EXPIRE = false;
	}else {
		daysLeft = Math.ceil((expirationTime - now)/_1day);
		if (daysLeft <= 30){
			ZaLicense.WILL_EXPIRE = true;
			ZaLicense.EXPIRED = false;
		}else{
			ZaLicense.WILL_EXPIRE = false;
			ZaLicense.EXPIRED = false ;
		}
	}
	
	//check for the license not yet valid error
	var serverUTCdateTime = license.getServerUTCTime() ; 
	var validFromUTCdateTime = license.attrs [ ZaLicense.A_validFrom ] ;
	if (serverUTCdateTime.toLowerCase() <  validFromUTCdateTime) { 
		ZaLicense.NOT_YET_VALID = true ;
	}else{
		ZaLicense.NOT_YET_VALID = false ;
	}
	
	var isRegularLicense = (license.attrs[ZaLicense.A_installType] == "regular")
	var trialText = isRegularLicense ? "" : "trial" ;
	
	if (ZaLicense.NOT_YET_VALID) {
		result.message = com_zimbra_license.licenseNotYetValid; 
		result.className = "licenseNotYetValid" ;
		adminObj.popupErrorDialog(AjxMessageFormat.format(com_zimbra_license.ERROR_LICENSE_NOT_YET_VALID,
				 [ZaLicense.getLocaleString(serverUTCdateTime), 
				 ZaLicense.getLocaleString(validFromUTCdateTime) ])) ;
	}else if (ZaLicense.EXPIRED) {
		result.message = AjxMessageFormat.format(com_zimbra_license.consoleLicenseExpired,[trialText, expiredDays]);
		result.className = "licenseExpired" ;
		
		//diaplay the popup dialog
		adminObj.popupErrorDialog(result.message) ;
	}else if (ZaLicense.WILL_EXPIRE || (! isRegularLicense)) {
		result.message = AjxMessageFormat.format(com_zimbra_license.licenseWillExpire ,[trialText, daysLeft]);
		result.className = "licenseWillExpire";
		if (ZaLicense.WILL_EXPIRE) {
			adminObj.popupErrorDialog(result.message) ;
		}
	}
	
	adminObj._statusBox.getHtmlElement().className = result.className;
	adminObj.setStatusMsg(result.message);
}

//parse the account number -1 to unlimited

ZaLicense.getAccountNumber =
function (serverStr){
	if (serverStr == "-1"){
		return com_zimbra_license.LI_UNLIMITED ;
	}else {
		return serverStr ;
	}
}

ZaLicense.getLocaleString =
function (serverStr) {
	return ZaLicense.parseLicenseDateTime(serverStr).toLocaleString();	
}

//Sample license time: 20060617053000Z (UTC)
ZaLicense.parseLicenseDateTime = 
function(serverStr) {
	if (serverStr == null) return null;

	var d = new Date();
	var yyyy = parseInt(serverStr.substr(0,4), 10);
	var MM = parseInt(serverStr.substr(4,2), 10);
	var dd = parseInt(serverStr.substr(6,2), 10);
	d.setFullYear(yyyy);
	d.setMonth(MM - 1);
	d.setMonth(MM - 1); // DON'T remove second call to setMonth (see bug #3839)
	d.setDate(dd);
	ZaLicense.parseLicenseTime(serverStr, d);
	return d;
};

//Sample license time: 20060617053000Z (UTC)
ZaLicense.parseLicenseTime = 
function(serverStr, date) {
	var hh = parseInt(serverStr.substr(8,2), 10);
	var mm = parseInt(serverStr.substr(10,2), 10);
	var ss = parseInt(serverStr.substr(12,2), 10);
	if (serverStr.charAt(14) == 'Z') {
		mm += AjxTimezone.getOffset(AjxTimezone.DEFAULT, date);
	}
	date.setHours(hh, mm, ss, 0);
	return date;
};

//1. modify the model ZaGlobalConfig
if(ZaGlobalConfig) {
	DBG.println(AjxDebug.DBG1,"Modify ZaGlobalConfig XModel");	
	//define the variables
	ZaGlobalConfig.A_zimbraFeatureLicenseEnabled= "zimbraFeatureLicenseEnabled";
		
	ZaGlobalConfig.myXModel.items.push(
		{id:ZaGlobalConfig.A_zimbraFeatureLicenseEnabled, type:_COS_ENUM_, 
			ref:"attrs/"+ZaGlobalConfig.A_zimbraFeatureLicenseEnabled, 
			choices:ZaModel.BOOLEAN_CHOICES}
			);			
	ZaGlobalConfig.myXModel.items = ZaGlobalConfig.myXModel.items.concat (ZaLicense.myXModel.items);
}


if (ZaController.initToolbarMethods["ZaGlobalConfigViewController"]) {
    ZaGlobalConfigViewController.initLicenseToolbarMethod = function () {
        this._toolbarOperations[ZaOperation.UPDATELICENSE] =
            new ZaOperation(ZaOperation.UPDATELICENSE, ZaMsg.TBB_UpdateLicense, ZaMsg.ALTBB_UpdateLicense_tt, "UpdateLicense", "UpdateLicense",
			new AjxListener(this, this.updateLicenseButtonListener));
		this._toolbarOrder.push(ZaOperation.UPDATELICENSE);
    }

    ZaController.initToolbarMethods["ZaGlobalConfigViewController"].push(ZaGlobalConfigViewController.initLicenseToolbarMethod);
}


/**
 * License install wizard will be launched when user click the update license button
 */
ZaGlobalConfigViewController.prototype.updateLicenseButtonListener =
function (ev){
	//alert("Bingo, the license installation wizard will be launched");
	try {
		this._license = new ZaLicense();
		if(!ZaApp.getInstance().dialogs["licenseInstallWizard"])
			ZaApp.getInstance().dialogs["licenseInstallWizard"] = new ZaLicenseInstallWizard(this._container, ZaApp.getInstance());	

		ZaApp.getInstance().dialogs["licenseInstallWizard"].setObject(this._license);
		ZaApp.getInstance().dialogs["licenseInstallWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaGlobalConfigViewController.prototype.updateLicenseButtonListener", null, false);
	}
}

GlobalConfigXFormView.switchTab = 
function (value, event, form) {
	var controller = ZaApp.getInstance().getCurrentController();
	var updateLicenseButton= controller._toolbar.getButton(ZaOperation.UPDATELICENSE);
	
	//9 -license tab		
	if (updateLicenseButton) {
		updateLicenseButton.setEnabled((value == ZaLicense.GlobalConfigTabIndex) ? true : false);
		// it doesn't hurt to show the diabled Upgrade License button.
//        var divEl = updateLicenseButton.getHtmlElement();
		if (value == ZaLicense.GlobalConfigTabIndex) {
			//divEl.style.visibility = "visible";
			
			//send the getLicense request if no license information is current available
			var instance = form.getInstance();
			if (! instance.attrs[ZaLicense.A_licenseId]) {
				ZaLicense.getLicenseInfo(this, form) ;
				//ZaLicense.addLicense (instance , controller._license);
				//form.setInstance(instance);
			}
		}
	}
	this.setInstanceValue(value);
	return value
};

/*
if(ZaSettings) {
    ZaSettings.CONFIG_LICENSE_TAB = "configLicenseTab" ;
    ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.CONFIG_LICENSE_TAB, label: com_zimbra_license.UI_Comp_configLicenseTab });
} */


//2. modify the xform GlobalConfigXFormView
if(ZaTabView.XFormModifiers["GlobalConfigXFormView"]) {
	DBG.println(AjxDebug.DBG1,"Adding function to modify GlobalConfig XForm");
	ZaLicense.GlobalConfigXFormModifier = function(xFormObject) {

	    if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CONFIG_LICENSE_TAB] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
            //insert the tab value
            var tabBar = xFormObject.items[1] ;
            ZaLicense.GlobalConfigTabIndex = ++this.TAB_INDEX;
            tabBar.choices.push({value:ZaLicense.GlobalConfigTabIndex, label:com_zimbra_license.NAD_Tab_License}); //value 8 is HSM feature tab
            tabBar.onChange = GlobalConfigXFormView.switchTab;
            //insert the tab views
            var licenseTabView = {
                type:_ZATABCASE_, /*relevant:"instance[ZaModel.currentTab] == "+ZaLicense.GlobalConfigTabIndex,*/caseKey:ZaLicense.GlobalConfigTabIndex,
                        colSizes:["300px","*"],
                        items:[
                            {type: _SPACER_, height: 10},
                            //license file installation successful status, need to define relavant variable
                            {type: _OUTPUT_, ref: ZaLicense.InstallStatusMsg, colSpan: "2",
                                    width: "600px", align: _CENTER_, cssStyle: "border: solid thin",
                                    visibilityChecks:[function() {return ZaLicenseInstallWizard.INSTALL_STATUS >= 0; }]
                                    //relevant: "ZaLicenseInstallWizard.INSTALL_STATUS >= 0",
                                    //relevantBehavior: _HIDE_
                            },
                            //title
                            {type: _OUTPUT_, value: com_zimbra_license.LI_INFO_TITLE , colSpan: "2", width: "600px", align: _CENTER_ },
                            //Customer name
                            { type:_OUTPUT_, ref: ZaLicense.A_issuedToName, label: com_zimbra_license.LB_company_name, align: _LEFT_},
                            { type:_OUTPUT_, ref: ZaLicense.A_installType, label: com_zimbra_license.LB_license_type, align: _LEFT_ },
                            { type:_OUTPUT_, ref: ZaLicense.A_licenseId, label: com_zimbra_license.LB_license_id, align: _LEFT_ },
                            { type:_OUTPUT_, ref: ZaLicense.A_issuedOn, label: com_zimbra_license.LB_issue_date, align: _LEFT_ },
                            { type:_OUTPUT_, ref: ZaLicense.A_validFrom, label: com_zimbra_license.LB_effective_date, align: _LEFT_ },
                            { type:_OUTPUT_, ref: ZaLicense.A_validUntil, label: com_zimbra_license.LB_expiration_date, align: _LEFT_ },
                            { type:_OUTPUT_, ref: ZaLicense.A_accountsLimit, label: com_zimbra_license.LB_account_limit, align: _LEFT_}
    //						{ type:_OUTPUT_, ref: ZaLicense.A_MAPIConnectorAccountsLimit, label: com_zimbra_license.LB_mapi_limit,  align: _LEFT_  },
    //						{ type:_OUTPUT_, ref: ZaLicense.A_mobileSyncAccountsLimit, label: com_zimbra_license.LB_mobile_limit, align: _LEFT_ },
    //						{ type:_OUTPUT_, ref: ZaLicense.A_iSyncAccountsLimit, label: com_zimbra_license.LB_isync_limit, align: _LEFT_ }
                        ]
            };

            xFormObject.items[2].items.push(licenseTabView);
        }
    }

	ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(ZaLicense.GlobalConfigXFormModifier);
}


//ZaLicenseInstallWizard

function ZaLicenseInstallWizard (parent) {
	ZaXWizardDialog.call(this, parent, null, com_zimbra_license.LIW_title, "500px", "300px","ZaLicenseInstallWizard");

	this.stepChoices = [
		{label:com_zimbra_license.LIW_TABT_upload, value:1},
		{label:com_zimbra_license.LIW_uploadTitle, value:2}
	];
	
	this._lastStep = this.stepChoices.length;
	this.attId = null ;
	//this.initForm(null,this.getMyXForm());	
	this.initForm(ZaLicense.myXModel,this.getMyXForm());	
	//this.license = new ZaLicense(app);
   
	this._localXForm.setController(ZaApp.getInstance());	
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaLicenseInstallWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaLicenseInstallWizard.prototype.handleXFormChange));	
	this._helpURL = ZaLicenseInstallWizard.helpURL;
}

// -1 : No status, 0: Install succeed, >0 : Install Failed (different number is different error)
ZaLicenseInstallWizard.INSTALL_STATUS = -1;

ZaLicenseInstallWizard.prototype = new ZaXWizardDialog;
ZaLicenseInstallWizard.prototype.constructor = ZaLicenseInstallWizard;
ZaXDialog.XFormModifiers["ZaLicenseInstallWizard"] = new Array();
ZaLicenseInstallWizard.helpURL = location.pathname + "help/admin/html/managing_global_settings/updating_your_zimbra_license.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
ZaLicenseInstallWizard.prototype.handleXFormChange = 
function () {
	if(this._localXForm.hasErrors()) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	}
}

ZaLicenseInstallWizard.getInstallMsg =
function () {
	if (ZaLicenseInstallWizard.INSTALL_STATUS == 0) {
		return com_zimbra_license.LI_INSTALL_STATUS_0 ;
	}else if (ZaLicenseInstallWizard.INSTALL_STATUS == 1){
		return com_zimbra_license.LI_INSTALL_STATUS_1;
	}else{
		return "";
	}
}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaLicenseInstallWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.FINISH_BUTTON].setText(com_zimbra_license.LIW_INSTALL_BUTTON_text);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

ZaLicenseInstallWizard.prototype.finishWizard = 
function() {
	try {	
		// Basically, it will do two things:
		//1) install the license
		//2) Upon the successful install, the license tab information will be updated
		ZaLicenseInstallWizard.INSTALL_STATUS = 0;
		var soapDoc = AjxSoapDoc.create("InstallLicenseRequest", "urn:zimbraAdmin", null);
		var contentElement = soapDoc.set("content", "");
		contentElement.setAttribute("aid", this.attId );
		var installLicenseCmd = new ZmCsfeCommand();
		var params = new Object();
		params.soapDoc = soapDoc;	
		var callback = new AjxCallback(this, this.installCallback);
		params.asyncMode = true;
		params.callback = callback;		
		installLicenseCmd.invoke(params);	
		
		this.popdown();		
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaLicenseInstallWizard.prototype.finishWizard", null, false);
	}
}

ZaLicenseInstallWizard.getCause = 
function (detailMsg) {
	var causeBy = /Caused by:\s*com.zimbra.cs.license.LicenseException:\s*(.*)/;
	
	var result = detailMsg.match(causeBy);
	if (result != null) {
    	return result [1] ;
	}else{
		return detailMsg ;
	}
}

ZaLicenseInstallWizard.prototype.installCallback =
function (resp){
	var controller = ZaApp.getInstance().getCurrentController();
	var xform = controller._view._localXForm;
	try {
		if (resp._isException) {
			var detailMsg = resp._data.msg ;			
			throw new AjxException(com_zimbra_license.LI_INSTALL_STATUS_1 + ": " + ZaLicenseInstallWizard.getCause(detailMsg), "ZaLicenseInstallWizard.prototype.installCallback", AjxException.UNKNOWN_ERROR, detailMsg) ;
			//throw new Error(resp._data.msg) ;
		}else{
			var installResponse = resp._data.Body.InstallLicenseResponse ;
			if (installResponse) {
				//update the license info tab
				ZaLicense.getLicenseInfo(this, xform);
			}else{
				throw new AjxException(com_zimbra_license.LIW_ERROR_0, "installCallback", AjxException.UNKNOWN_ERROR) ;
				//throw new AjxException ();
			}
		}
	}catch (ex){
		ZaLicenseInstallWizard.INSTALL_STATUS = 1;
		xform.getItemsById (ZaLicense.InstallStatusMsg)[0].setInstanceValue(ex.msg);
		xform.refresh();
		ZaLicenseInstallWizard.INSTALL_STATUS = -1; //reset hte install_status
		//alert(ex);
		controller.popupErrorDialog(ex.msg, ex, true);
		//_handleException(ex, "ZaLicenseInstallWizard.prototype.installCallback", null, false);
	}
}

ZaLicenseInstallWizard.prototype._uploadCallback =
function (status, attId) {
	DBG.println(AjxDebug.DBG1, "License File Upload: status = " + status + ", attId = " + attId);
	if ((status == AjxPost.SC_OK) && (attId != null)) {
		this.attId = attId ;
		//go to the next page
		this.goPage(this._containedObject[ZaModel.currentStep] + 1);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);		
		if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		}	
	} else {
		// handle errors during attachment upload.
		var msg = AjxMessageFormat.format(com_zimbra_license.UploadLicenseErrorMsg, status);
		ZaApp.getInstance().getCurrentController().popupErrorDialog(msg + com_zimbra_license.ErrorTryAgain, null, null, true);		
	}	
}

//upload the file
ZaLicenseInstallWizard.prototype.getUploadFrameId =
function() {
	if (!this._uploadManagerIframeId) {
		var iframeId = Dwt.getNextId();
		var html = [ "<iframe name='", iframeId, "' id='", iframeId,
			     "' src='", (AjxEnv.isIE && location.protocol == "https:") ? appContextPath+"/public/blank.html" : "javascript:\"\"",
			     "' style='position: absolute; top: 0; left: 0; visibility: hidden'></iframe>" ];
		var div = document.createElement("div");
		div.innerHTML = html.join("");
		document.body.appendChild(div.firstChild);
		this._uploadManagerIframeId = iframeId;
	}
	return this._uploadManagerIframeId;
};

ZaLicenseInstallWizard.prototype.getUploadManager = 
function() { 
	return this._uploadManager;
};

/**
 * @params uploadManager is the AjxPost object
 */
ZaLicenseInstallWizard.prototype.setUploadManager = 
function(uploadManager) {
	this._uploadManager = uploadManager;
};


ZaLicenseInstallWizard.prototype.goNext = 
function() {
	DBG.println("Go Next");
	if (this._containedObject[ZaModel.currentStep] == 1) {
		//1. check if the file name are valid and exists
		//2. Upload the files
		DBG.println("Start uploading the file");
		this.setUploadManager(new AjxPost(this.getUploadFrameId()));
		var licenseUploadCallback = new AjxCallback(this, this._uploadCallback);
		var um = this.getUploadManager() ; 
		window._uploadManager = um;
		try {
			um.execute(licenseUploadCallback, document.getElementById (ZaLicenseInstallWizard.LicenseUploadFormId));
		}catch (err) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_license.licenseFileNameError) ;
		}
	} else{
		this.goPage(this._containedObject[ZaModel.currentStep] + 1);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);		
		if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		}	
	}
	
}

ZaLicenseInstallWizard.prototype.goPrev = 
function() {
	DBG.println("Go Previous");
	if (this._containedObject[ZaModel.currentStep] == 2) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	}
	
	if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	}
	this.goPage(this._containedObject[ZaModel.currentStep] - 1);
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaLicense object to display
**/
ZaLicenseInstallWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject.attrs = new Object();

	for (var a in entry.attrs) {
		this._containedObject.attrs[a] = entry.attrs[a];
	}
	
	this._containedObject[ZaModel.currentStep] = 1;
	this._localXForm.setInstance(this._containedObject);
}

ZaLicenseInstallWizard.LicenseUploadAttachmentInputId = Dwt.getNextId();
ZaLicenseInstallWizard.LicenseUploadFormId = Dwt.getNextId();
ZaLicenseInstallWizard.getUploadFormHtml =
function (){
	//var uri = location.protocol + "//" + document.domain + appContextPath 
	//							+ "/../service/upload";
	var uri = appContextPath + "/../service/upload";
	DBG.println("upload uri = " + uri);
	var html = new Array();
	var idx = 0;
	html[idx++] = "<div style='overflow:auto'><form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaLicenseInstallWizard.LicenseUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'><input id='";
	html[idx++] = ZaLicenseInstallWizard.LicenseUploadAttachmentInputId;
	html[idx++] = "' type=file  name='licenseFile' size='50'></input>";
	/*add the hidden admin input
	html[idx++] = "<input type=hidden name='admin' value='1' />"; */
	html[idx++] = "</form></div>";
	
	return html.join("");
}

ZaLicenseInstallWizard.myXFormModifier = function(xFormObject) {		
	var cases = new Array();
	var case1 = {type:_CASE_, numCols:1, /*relevant:"instance[ZaModel.currentStep] == 1",*/ caseKey:1, align:_LEFT_, valign:_TOP_};
	//upload components
	var case1Items = [
		{ type:_OUTPUT_, value: com_zimbra_license.LIW_uploadTitle, align: _LEFT_},
		{ type:_OUTPUT_, value: ZaLicenseInstallWizard.getUploadFormHtml() }
		];	
	
	case1.items = case1Items;
	cases.push(case1);
	
	var case2={type:_CASE_, numCols:2, colSizes:["200px","*"], /*relevant:"instance[ZaModel.currentStep] == 2",*/ caseKey:2,
					items: [
						{ type:_OUTPUT_, value: com_zimbra_license.LIW_uLicenseInfo_title, colSpan: "2", width: "400px", align: _LEFT_ },
						/*
						{ type:_SPACER_, height: 5},
						{ type:_OUTPUT_, value: "Customer name: ", align: _RIGHT_  },
						{ type:_OUTPUT_, value: "ABC", align: _LEFT_},
						{ type:_OUTPUT_, value: "License Type: ", align: _RIGHT_ },
						{ type:_OUTPUT_, value: "Trial", align: _LEFT_ },						
						{ type:_OUTPUT_, value: "Notes: ", align: _RIGHT_ },
						{ type:_OUTPUT_, value: "From Var EFZ ", align: _LEFT_ },						
						{ type:_OUTPUT_, value: "SKU: ", align: _RIGHT_ },
						{ type:_OUTPUT_, value: "1234567 ", align: _LEFT_ },						
						{ type:_OUTPUT_, value: "Number of Users: ", align: _RIGHT_ },
						{ type:_OUTPUT_, value: "50 ", align: _LEFT_ },						
						{ type:_OUTPUT_, value: "Issue Date: ", align: _RIGHT_ },
						{ type:_OUTPUT_, value: "May 1, 2006", align: _LEFT_ },						
						{ type:_OUTPUT_, value: "Expiration Date: ", align: _RIGHT_ },
						{ type:_OUTPUT_, value: "April 30, 2007 ", align: _LEFT_ }, */
						{ type:_SPACER_, height: 10},
						{ type:_OUTPUT_, value: com_zimbra_license.LIW_uLicenseConfirmation, colSpan: "2", width: "400px", align: _LEFT_ }			
					]
				};
	cases.push(case2);

	xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices,valueChangeEventSources:[ZaModel.currentStep]},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:450, align:_LEFT_, valign:_TOP_, items:cases}
		];
};
ZaXDialog.XFormModifiers["ZaLicenseInstallWizard"].push(ZaLicenseInstallWizard.myXFormModifier);

ZaLicense.changeActionsStateMethod =
function () {
    if(this._toolbarOperations[ZaOperation.UPDATELICENSE]) {
        this._toolbarOperations[ZaOperation.UPDATELICENSE].enabled = false;
    }
}
ZaController.changeActionsStateMethods["ZaGlobalConfigViewController"].push(ZaLicense.changeActionsStateMethod);


if (AjxEnv.hasFirebug) console.log("Loaded com_zimbra_license.js");
