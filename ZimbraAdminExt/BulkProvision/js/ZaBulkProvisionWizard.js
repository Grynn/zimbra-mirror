/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
//ZaBulkProvisionWizard
function ZaBulkProvisionWizard (parent, app) {
    var w = "550px" ;
    if (AjxEnv.isIE) {
        w = "600px" ;
    }
    ZaXWizardDialog.call(this, parent, null, com_zimbra_bulkprovision.BP_Wizard_title,
                                w, (AjxEnv.isIE ? "330px" :"320px"),"ZaBulkProvisionWizard");

	this.stepChoices = [
		{label:com_zimbra_bulkprovision.BP_Wizard_upload, value:ZaBulkProvisionWizard.STEP_UPLOAD_CSV},
		{label:com_zimbra_bulkprovision.BP_Wizard_provision, value:ZaBulkProvisionWizard.STEP_PROVISION},
		{label:com_zimbra_bulkprovision.BP_Wizard_summary, value:ZaBulkProvisionWizard.STEP_SUMMARY}
	];

	this.uploadInputs = {} ;
	this.uploadResults = null ;

    this.initForm(ZaBulkProvision.getMyXModel(),this.getMyXForm());

	this._localXForm.setController(this._app);
  	this._helpURL = ZaBulkProvisionWizard.helpURL;
}

// -1 : No status, 0: Install succeed, >0 : Install Failed (different number is different error)
ZaBulkProvisionWizard.INSTALL_STATUS = -1;
ZaBulkProvisionWizard.STEP_INDEX = 1 ;
ZaBulkProvisionWizard.STEP_UPLOAD_CSV = ZaBulkProvisionWizard.STEP_INDEX ++ ;
ZaBulkProvisionWizard.STEP_PROVISION = ZaBulkProvisionWizard.STEP_INDEX ++ ;
ZaBulkProvisionWizard.STEP_SUMMARY = ZaBulkProvisionWizard.STEP_INDEX ++ ;

ZaBulkProvisionWizard.prototype = new ZaXWizardDialog;
ZaBulkProvisionWizard.prototype.constructor = ZaBulkProvisionWizard;

ZaXDialog.XFormModifiers["ZaBulkProvisionWizard"] = new Array();
ZaBulkProvisionWizard.helpURL = location.pathname + "help/admin/html/managing_accounts/how_to_provision_multiple_accounts.htm?locid=" + AjxEnv.DEFAULT_LOCALE;

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/

ZaBulkProvisionWizard.prototype.popup =
function (loc) {
    ZaXWizardDialog.prototype.popup.call(this, loc);

	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
}


ZaBulkProvisionWizard.prototype._uploadCallback =
function (status, uploadResults) {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if (console && console.log)
		console.log("Provisioning File Upload: status = " + status);
	if ((status == AjxPost.SC_OK) && (uploadResults != null) && (uploadResults.length > 0)) {
    	var v = uploadResults[0] ;
        if (v.aid != null && v.aid.length > 0) {
           this._containedObject [ZaBulkProvision.A_csv_aid] =  v.aid ;
        } else {
           this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_upload_csv_no_aid);
           return ;
        }
        //File is uploaded successfully
        try {
            var resp = ZaBulkProvision.getBulkProvisionAccounts(this._app, this._containedObject );
            if (resp.aid == this._containedObject[ZaBulkProvision.A_csv_aid]) {
                this._containedObject[ZaBulkProvision.A_provision_accounts] =
                                                ZaBulkProvision.initProvisionAccounts (resp.accounts) ;
                this._containedObject[ZaBulkProvision.A_isValidCSV] = resp[ZaBulkProvision.A_isValidCSV] ;
            }else{
                throw new AjxException(com_zimbra_bulkprovision.error_unmatching_aid) ;
            }
        }catch (ex) {
            if (ex.code == "bulkprovision.BP_TOO_MANY_ACCOUNTS")  {
                this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.ERROR_TOO_MANY_ACCOUNTS, ex);
            }else{
                this._app.getCurrentController()._handleException(ex) ;
            }
            return ;
        }
        this.goPage(ZaBulkProvisionWizard.STEP_PROVISION);
	} else {
		// handle errors during attachment upload.
		var msg = AjxMessageFormat.format(com_zimbra_bulkprovision.error_upload_csv, [status]);
		this._app.getCurrentController().popupErrorDialog(msg);
	}
}

//upload the file
ZaBulkProvisionWizard.prototype.getUploadFrameId =
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

ZaBulkProvisionWizard.prototype.getUploadManager =
function() {
	return this._uploadManager;
};

/**
 * @params uploadManager is the AjxPost object
 */
ZaBulkProvisionWizard.prototype.setUploadManager =
function(uploadManager) {
	this._uploadManager = uploadManager;
};

ZaBulkProvisionWizard.prototype.getProvisionStatusDialog = function () {
    if (!this._provisionStatusDialog) {
        this._provisionStatusDialog = new ZaBulkProvisionStatusDialog (
                this.parent, this._app);
        this._provisionStatusDialog.addPopupListener(new AjxListener(this, this.statusDialogPopupListener)) ;
    }

    return this._provisionStatusDialog ;
}

ZaBulkProvisionWizard.prototype.createAccountCallback =  function ( account, result) {
    var i =  this._currentCreateAccountIndex ;

    var accounts = this._containedObject[ZaBulkProvision.A_provision_accounts] ;
    var statusDialogCreatedAccounts =  this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_createdAccounts] ;
    var statusDialog = this.getProvisionStatusDialog() ;
    
    if (result._isException)  {
        var ex = result._data ;
        statusDialogCreatedAccounts[i][ZaBulkProvision.A2_status] = com_zimbra_bulkprovision.BUSY_FAILED ;
        accounts[i][ZaBulkProvision.A2_status] = com_zimbra_bulkprovision.BUSY_FAILED + " (" + accounts[i].accountName  + ") " 
                                        +  ( ex.msg  ? ": " + ex.msg : "") ;
        accounts[i][ZaBulkProvision.A2_isValid] = "FALSE" ;
    } else {
        var resp = result._data.Body.CreateAccountResponse ;
        if (resp && resp.account && resp.account[0].id) {
            statusDialogCreatedAccounts[i][ZaBulkProvision.A2_status] = com_zimbra_bulkprovision.SUCCEEDED ;
            this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_createdAccounts].push(
                    account[ZaBulkProvision.A2_accountName]) ;

            accounts[i][ZaBulkProvision.A2_status] = com_zimbra_bulkprovision.SUCCEEDED ;
            accounts[i][ZaBulkProvision.A2_isToProvision] = false ;     //succeed, don't try to create the second time
            accounts[i][ZaBulkProvision.A2_isValid] = "TRUE" ;

            try {
                //set the password now
                var sp_soapDoc = AjxSoapDoc.create("SetPasswordRequest", ZaZimbraAdmin.URN, null);
                sp_soapDoc.set("id", resp.account[0].id) ;
                sp_soapDoc.set("newPassword", account.password) ;
                var sp_Params = { soapDoc: sp_soapDoc }
                ZaRequestMgr.invoke(sp_Params, {} ) ;

                //enforce must change password,  mustChangePassword is revoked by the setPasswordRequest
                var ma_soapDoc = AjxSoapDoc.create("ModifyAccountRequest", ZaZimbraAdmin.URN, null);
                ma_soapDoc.set("id", resp.account[0].id);
                var ma_attr = ma_soapDoc.set("a", "TRUE") ;
                ma_attr.setAttribute("n", ZaAccount.A_zimbraPasswordMustChange) ;
                var ma_params = {soapDoc: ma_soapDoc};
                ZaRequestMgr.invoke(ma_params, {}) ;
            }catch (ex) {
                accounts[i][ZaBulkProvision.A2_status] += ex.message ;
            }
        } else {
            accounts[i][ZaBulkProvision.A2_isValid] = "FALSE" ;
        }
    }

    statusDialog.setObject(this._provisionStatusObject ) ;
    
    this._currentCreateAccountIndex ++ ;
    this.createAccounts ();
}

ZaBulkProvisionWizard.prototype.createAccounts = function () {
    var statusDialogCreatedAccounts =  this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_createdAccounts] ;
    var statusDialog = this.getProvisionStatusDialog() ;
    var accounts = this._containedObject[ZaBulkProvision.A_provision_accounts] ;
    
    var totalNumberOfAccounts = accounts.length ;
    var i =  this._currentCreateAccountIndex  ;

    if (i < totalNumberOfAccounts  && (!statusDialog._aborted))  {
        //create account  this._currentCreateAccountIndex
        var account = accounts[i] ;
        this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_currentStatus] = AjxMessageFormat.format (
                               com_zimbra_bulkprovision.BUSY_CREATE_ACCOUNTS, [account[ZaBulkProvision.A2_accountName]]);
        statusDialog.setObject(this._provisionStatusObject ) ;

        statusDialogCreatedAccounts[i] = {} ;
        statusDialogCreatedAccounts[i][ZaBulkProvision.A2_accountName] = account[ZaBulkProvision.A2_accountName] ;
        
        if (account[ZaBulkProvision.A2_isToProvision]) {
            var soapDoc = AjxSoapDoc.create("CreateAccountRequest", ZaZimbraAdmin.URN, null);
            soapDoc.set(ZaAccount.A_name, account.accountName);
//              we set the password using setPasswordRequest
//                soapDoc.set(ZaAccount.A_password, account.password);

            //set the account attributes
            var attrs = {} ;
            /*
            if ((this._containedObject[ZaBulkProvision.A_mustChangePassword] == "TRUE")
                    || (account[ZaBulkProvision.A_mustChangePassword] == "TRUE")){
                attrs [ZaAccount.A_zimbraPasswordMustChange] = "TRUE" ;
            } */
            //always change the password
            attrs [ZaAccount.A_zimbraPasswordMustChange] = "TRUE" ;

            if (account[ZaBulkProvision.A2_displayName]) {
                attrs [ZaAccount.A_displayname] = account[ZaBulkProvision.A2_displayName] ;
            }

            for (var aname in attrs) {
                var attr = soapDoc.set("a", attrs[aname]) ;
                attr.setAttribute("n", aname) ;
            }

            var csfeParams = new Object();
            csfeParams.soapDoc = soapDoc;
            csfeParams.asyncMode = true ;
            csfeParams.callback = new AjxCallback(this, this.createAccountCallback, [account]);
            var reqMgrParams = {} ;
            ZaRequestMgr.invoke(csfeParams, reqMgrParams );
        }else{
//            com_zimbra_bulkprovision.BUSY_FAILED =  com_zimbra_bulkprovision.SKIP ;
            statusDialogCreatedAccounts[i][ZaBulkProvision.A2_status] = com_zimbra_bulkprovision.SKIPPED ;
            statusDialog.setObject( this._provisionStatusObject ) ;
            this._currentCreateAccountIndex ++ ;
            this.createAccounts () ;
        }
    } else if (statusDialog._aborted && i < totalNumberOfAccounts) {
        if (this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_currentStatus] != com_zimbra_bulkprovision.ABORTED) {
            this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_currentStatus] =  com_zimbra_bulkprovision.ABORTED ;
            //update the status view
            statusDialog.setObject(this._provisionStatusObject ) ;
        }
        //update the wizard account creation status
        accounts[i][ZaBulkProvision.A2_status] = com_zimbra_bulkprovision.ABORTED ;
        this._currentCreateAccountIndex ++ ;
        this.createAccounts () ;
    } else {
        //Done with create accounts
        this._currentCreateAccountIndex = 0 ;
        if (!statusDialog._aborted)  {
            this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_currentStatus] =  com_zimbra_bulkprovision.DONE ;
             //update the status view
            statusDialog.setObject(this._provisionStatusObject ) ;
        }
        
        this._endTime = new Date ();
        if (console && console.log) console.log("End provision accounts: " + this._endTime.toUTCString());
        var total = this._endTime.getTime () - this._startTime.getTime () ;
        if (console && console.log) console.log("Total Time (ms): "  + total) ;
        //update the status now
        ZaBulkProvision.updateBulkProvisionStatus (this._app, this._containedObject) ;
        var nextStep = ZaBulkProvisionWizard.STEP_SUMMARY ;
        this.goPage(nextStep) ;

        //update account list view
        this._app.getAccountListController().fireCreationEvent(accounts);
    }
}

ZaBulkProvisionWizard.prototype.statusDialogPopupListener = function (ev) {
    this._startTime = new Date ();
    if (console && console.log) console.log("Start provisiong accounts: " + this._startTime.toUTCString());

    var statusDialogCreatedAccounts =  this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_createdAccounts] ;
    var statusDialog = this.getProvisionStatusDialog() ;
    var accounts = this._containedObject[ZaBulkProvision.A_provision_accounts] ;
    
    this._currentCreateAccountIndex = 0;
    this.createAccounts ();
}

//it should be called once the CSV file is uploaded
ZaBulkProvisionWizard.prototype.checkLicenseAccountLimit = function () {
    var callback = new AjxCallback (this, this.updateLicenseAccountLimit) ;
    ZaLicense.getLicenseInfo (null, null, callback) ;
}

ZaBulkProvisionWizard.prototype.updateLicenseAccountLimit = function (resp) {
    var accountLimit = -1 ;
    if (!resp._isException) {
        var getLicenseResp = resp._data.Body.GetLicenseResponse ;
        if (getLicenseResp.license) {
            var licenseObj = new ZaLicense();
            licenseObj.init(getLicenseResp);
            var usedAccounts = parseInt (licenseObj.info[ZaLicense.Info_TotalAccounts]) ;
            var licenseLimit = parseInt (licenseObj.attrs[ZaLicense.A_accountsLimit]) ;
            if (licenseLimit != -1) { //-1 is unlimited
                if (usedAccounts != -1) {
                    accountLimit = licenseLimit - usedAccounts ;
                } else {
                    accountLimit = licenseLimit ;
                }
            }
        }
    }

    this._containedObject[ZaBulkProvision.A2_accountLimit] = accountLimit ;
}

ZaBulkProvisionWizard.prototype.goNext =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if (console && console.log)
		console.log("Current Step: " + cStep + ", Now Go Next ...");

	var nextStep;
	if (cStep == ZaBulkProvisionWizard.STEP_UPLOAD_CSV) {
        //1. check if the file name are valid and exists
        //2. upload the file
        var formEl = document.getElementById(ZaBulkProvisionWizard.csvUploadFormId);
        var inputEls = formEl.getElementsByTagName("input") ;

        var filenameArr = [];
        for (var i=0; i < inputEls.length; i++){
            if (inputEls[i].type == "file") {
                var n = inputEls[i].name ;
                var v = ZaBulkProvisionWizard.getFileName(inputEls[i].value) ;
                if ( n == "csvFile") {
                    if (v == null || v.length <= 0) {
                        this._app.getCurrentController().popupErrorDialog (
                            com_zimbra_bulkprovision.error_no_csv_file_specified
                        );
                        return ;
                    }

                    //valid csv file, ready to upload
                    break ;
                }
            }
        }

        //2. Upload the files
        this.setUploadManager(new AjxPost(this.getUploadFrameId()));
        var csvUploadCallback = new AjxCallback(this, this._uploadCallback);
        var um = this.getUploadManager() ;
        window._uploadManager = um;
        try {
            um.execute(csvUploadCallback, document.getElementById (ZaBulkProvisionWizard.csvUploadFormId));
        }catch (err) {
            this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_no_csv_file_specified) ;
        }

         //3. Update the account limit information
        this.checkLicenseAccountLimit() ;
        
        return ; //allow the callback to handle the wizard buttons
    }else if (cStep == ZaBulkProvisionWizard.STEP_PROVISION) {
	    //create the accounts now, it is a sychronous action with status updated
        var controller = this._app.getCurrentController() ;
//        var busyMsg = com_zimbra_bulkprovision.BUSY_START_PROVISION_ACCOUNTS ;
        var statusDialog = this.getProvisionStatusDialog() ;
        this._provisionStatusObject = {} ;
        this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_currentStatus] = com_zimbra_bulkprovision.BUSY_START_PROVISION_ACCOUNTS ;
        this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_createdAccounts] = [] ;
        //statusDialog.setMessage(busyMsg) ;
        statusDialog.setObject( this._provisionStatusObject) ;
      
        statusDialog.popup() ;
    }
}

ZaBulkProvisionWizard.prototype.goPrev =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	var prevStep ;
	if (cStep == ZaBulkProvisionWizard.STEP_SUMMARY) {
		prevStep = ZaBulkProvisionWizard.STEP_PROVISION ;
    }else if (cStep == ZaBulkProvisionWizard.STEP_PROVISION) {
		prevStep = ZaBulkProvisionWizard.STEP_UPLOAD_CSV ;
    }

    this.goPage(prevStep);
}

ZaBulkProvisionWizard.prototype.goPage = function (pageKey) {
    ZaXWizardDialog.prototype.goPage.call(this, pageKey) ;
    var prev = next = finish = true ;
    if (pageKey == ZaBulkProvisionWizard.STEP_UPLOAD_CSV) {
		prev = false;
        finish = false ;
    } else if (pageKey == ZaBulkProvisionWizard.STEP_PROVISION) {
        finish = false ;
        if (this._containedObject[ZaBulkProvision.A_isValidCSV] == "FALSE") {
            next = false ;
        }
        this._localXForm.setInstanceValue (
              this._containedObject[ZaBulkProvision.A_provision_accounts], ZaBulkProvision.A_provision_accounts) ;
    } else if (pageKey == ZaBulkProvisionWizard.STEP_SUMMARY) {
        next = false ;
        this._localXForm.setInstanceValue (
              this._containedObject[ZaBulkProvision.A_provision_accounts], ZaBulkProvision.A_provision_accounts) ;
        this._localXForm.setInstanceValue (
                      this._containedObject[ZaBulkProvision.A_csv_aid], ZaBulkProvision.A_csv_aid) ;
    }

	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(prev);
    this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(next);
    this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(finish);
}

ZaBulkProvisionWizard.getFileName = function (fullPath) {
    if (fullPath == null) return null ;

    var lastIndex = 0;
    if (AjxEnv.isWindows) {
        lastIndex = fullPath.lastIndexOf("\\") ;
    }else{
        lastIndex = fullPath.lastIndexOf("/") ;
    }

    return fullPath.substring(lastIndex + 1) ;
}

/**
* @method setObject sets the object contained in the view
* @param entry -  object to display
**/
ZaBulkProvisionWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();
	this._containedObject = entry ;

	this._containedObject[ZaModel.currentStep] = ZaBulkProvisionWizard.STEP_UPLOAD_CSV;
	this._containedObject[ZaBulkProvision.A_mustChangePassword] = "TRUE" ;
    this._localXForm.setInstance(this._containedObject);
}

ZaBulkProvisionWizard.csvUploadFormId = Dwt.getNextId();

ZaBulkProvisionWizard.getUploadFormHtml =
function (){
	//var uri = location.protocol + "//" + document.domain + appContextPath
	//							+ "/../service/upload";
	//need the full content of the response.
	//200,'1',[{"filename":"zimbra.crt","aid":"0466544c-1372-4cc3-ad8e-b1ff570dccca:85f82c13-6381-4c84-8915-bfbe515fdbd4","ct":"application/x-x509-ca-cert"},{"filename":"mycert.crt","aid":"0466544c-1372-4cc3-ad8e-b1ff570dccca:2321870e-1229-4359-a9c7-f11222a50042","ct":"application/x-x509-ca-cert"}]

	var uri = appContextPath + "/../service/upload?fmt=extended";
	var html = [];
	var idx = 0;
	html[idx++] = "<div style='height:50px;width: 500px; overflow:auto;'><form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaBulkProvisionWizard.csvUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'>" ;
	html[idx++] = "<div><table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;
	//html[idx++] = "<colgroup><col width=50 /><col width='*' /><col width=50 /></colgroup>";

	html[idx++] = "<tbody><tr><td width=65>" + com_zimbra_bulkprovision.CSV_Upload_file + "</td>";
	html[idx++] = "<td><input type=file  name='csvFile' size='45'></input></td></tr>";

	html[idx++] = "</tbody></table></div>";

	html[idx++] = "</form></div>";

	return html.join("");
}

ZaBulkProvisionWizard.myXFormModifier = function(xFormObject) {
	var cases = new Array();

	var case_upload_csv = {type:_CASE_, numCols:2, colSizes:["200px","*"],
        tabGroupKey:ZaBulkProvisionWizard.STEP_UPLOAD_CSV, caseKey:ZaBulkProvisionWizard.STEP_UPLOAD_CSV,
					items: [

						{ type:_GROUP_, id: "CSVUpload",
							colSpan: 2, numCols: 1, colSizes: "*", items : [
								{ type:_OUTPUT_, value: com_zimbra_bulkprovision.CSV_uploadTitle, align: _LEFT_},
								{ type:_SPACER_ , height: 10 },
                                { type:_OUTPUT_, value: ZaBulkProvisionWizard.getUploadFormHtml() } ,
                                /* always change the password
                                { type: _GROUP_, numCols: 2,items: [
                                        { type: _CHECKBOX_, ref: ZaBulkProvision.A_mustChangePassword,
                                            label:com_zimbra_bulkprovision.CKB_mustChangePasswd, labelLocation:_RIGHT_,
                                            trueValue:"TRUE", falseValue:"FALSE",
                                            align: _LEFT_
                                        }
                                    ]
                                }, */
                                { type:_SPACER_ , height: 10 } ,
                                { type:_OUTPUT_, value: com_zimbra_bulkprovision.CSV_uploadNotes } 
                            ]
						}
					]
				};
	cases.push(case_upload_csv);

	var case_provision = {type:_CASE_, numCols:2, colSizes:["200px", "*"], 
        tabGroupKey:ZaBulkProvisionWizard.STEP_PROVISION, caseKey:ZaBulkProvisionWizard.STEP_PROVISION,
					align:_LEFT_, valign:_TOP_};

    var bpAccountsListHeader = [] ;
    var i = 0 ;

    bpAccountsListHeader[i++] = new ZaListHeaderItem(ZaBulkProvision.A2_accountName, ZaMsg.ALV_Name_col, null, 150, null, null, true, true);
    bpAccountsListHeader[i++] = new ZaListHeaderItem(ZaBulkProvision.A2_displayName, ZaMsg.ALV_DspName_col, null, 150, null, null, true, true);
    bpAccountsListHeader[i++] = new ZaListHeaderItem(ZaBulkProvision.A2_password, com_zimbra_bulkprovision.ALV_Password_col, null, 100, null, null, true, true);
    bpAccountsListHeader[i++] = new ZaListHeaderItem(ZaBulkProvision.A2_status, com_zimbra_bulkprovision.ALV_Stauts_col, null, null, null, null, true, true);
    

    var case_provision_items = [
            {type:_GROUP_, colSpan: 2, numCols: 1,
                visibilityChecks: ["instance[ZaBulkProvision.A_isValidCSV] == 'TRUE'"],
                visibilityChangeEventSources: [ZaBulkProvision.A_isValidCSV],
                items: [
                    {type:_OUTPUT_, colSpan: 2,  value: com_zimbra_bulkprovision.BP_wizard_upload_status}
                 ]
            },
        //relevant doesn't apply on the _OUTPUT_ item, must use _GROUP_ to make the relevant work  
            {type:_GROUP_, colSpan: 2, numCols: 1,
                visibilityChecks: ["instance[ZaBulkProvision.A_isValidCSV] == 'FALSE'"],
                visibilityChangeEventSources: [ZaBulkProvision.A_isValidCSV],
                items: [
                    {type:_OUTPUT_, value: com_zimbra_bulkprovision.BP_wizard_csv_invalid}
                ]
            },
            { type:_SPACER_ , height: 5 } ,
             //provision account lists
            {type:_GROUP_, colSpan: 2, numCols: 1,
                  visibilityChecks:[],
                  visibilityChangeEventSources: [ZaBulkProvision.A_provision_accounts],
                items: [
                    {
                        ref:ZaBulkProvision.A_provision_accounts, type: _DWT_LIST_,
                        height:(AjxEnv.isIE ? 270 : 260), width:525,
                        forceUpdate: true,
                        cssClass: "DLSource",
                        widgetClass: ZaBulkProvisionAccountsListView,
                        headerList:bpAccountsListHeader, hideHeader: false
                    }
                ]
            }
        ];

	case_provision.items = case_provision_items;
	cases.push(case_provision);

	var case_summary =
		{type:_CASE_, numCols:1, colSizes:["*"],
            tabGroupKey:ZaBulkProvisionWizard.STEP_SUMMARY, caseKey:ZaBulkProvisionWizard.STEP_SUMMARY,
			align:_LEFT_, valign:_TOP_ ,
			items :[
				{ type:_OUTPUT_, value: com_zimbra_bulkprovision.summary_download },
				{ type:_SPACER_ , height: 10 },
			    { type:_OUTPUT_, ref: ZaBulkProvision.A_csv_aid,
                    valueChangeEventSources:[ZaBulkProvision.A_csv_aid],
                    getDisplayValue: function (newValue) {
                        return "<a target='_blank' href='/service/afd/?action=getBP&aid=" + newValue
                            + "' onclick='ZaZimbraAdmin.unloadHackCallback();'> "
										+ com_zimbra_bulkprovision.download_csv + "</a> "
                    }
                },
                { type:_SPACER_ , height: 10 },
                {type:_GROUP_, colSpan: "*", numCols: 1,
                    items: [
                        { ref:ZaBulkProvision.A_provision_accounts, type:_DWT_LIST_, height:(AjxEnv.isIE ? 270 : 260), width:525,
                            forceUpdate: true, cssClass: "DLSource",
                            widgetClass: ZaBulkProvisionAccountsListView,
                            headerList:bpAccountsListHeader, hideHeader: false
                        }
                    ]
                }
            ]
		}

	cases.push (case_summary) ;

	
    var contentW = 530 ;
    if (AjxEnv.isIE) {
        var contentW = 580 ;
    }
    xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep,
                choices:this.stepChoices, valueChangeEventSources:[ZaModel.currentStep]},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:contentW, align:_LEFT_, valign:_TOP_, items:cases}
		];
};
ZaXDialog.XFormModifiers["ZaBulkProvisionWizard"].push(ZaBulkProvisionWizard.myXFormModifier);

ZaBulkProvisionWizard.downloadBPStatus = function () {
    var form = this.getForm() ;
    var instance = form.getInstance () ;
    
    return ;
}
