//ZaBulkProvisionWizard
function ZaBulkProvisionWizard (parent, app) {
    var w = "550px" ;
    if (AjxEnv.isIE) {
        w = "600px" ;
    }
    ZaXWizardDialog.call(this, parent, app, null, com_zimbra_bulkprovision.BP_Wizard_title,
                                w, "300px","ZaBulkProvisionWizard");

	this.stepChoices = [
		{label:com_zimbra_bulkprovision.BP_Wizard_upload, value:ZaBulkProvisionWizard.STEP_UPLOAD_CSV},
		{label:com_zimbra_bulkprovision.BP_Wizard_provision, value:ZaBulkProvisionWizard.STEP_PROVISION},
		{label:com_zimbra_bulkprovision.BP_Wizard_summary, value:ZaBulkProvisionWizard.STEP_SUMMARY}
	];

	this.uploadInputs = {} ;
	this.uploadResults = null ;

    this.initForm(ZaBulkProvision.getMyXModel(),this.getMyXForm());

	this._localXForm.setController(this._app);
    /*
    this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE,
            new AjxListener(this, ZaBulkProvisionWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR,
            new AjxListener(this, ZaBulkProvisionWizard.prototype.handleXFormChange));
	 */
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
ZaBulkProvisionWizard.helpURL = location.pathname + "help/admin/html/tools/bulk_provisioning.htm?locid=" + AjxEnv.DEFAULT_LOCALE;

/*
ZaBulkProvisionWizard.prototype.handleXFormChange =
function () {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if(this._localXForm.hasErrors()) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		if (cStep == ZaBulkProvisionWizard.STEP_UPLOAD_CSV) {
			if (this._containedObject[ZaCert.A_target_server]) {
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled (true) ;
			}
		}

		if (cStep == ZaBulkProvisionWizard.STEP_INSTALL_CERT ) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		}

		if (cStep == ZaBulkProvisionWizard.STEP_SUMMARY ) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(AjxMsg._finish);
		}

		if (this._containedObject[ZaCert.A_type_csr]) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(AjxMsg._finish);
		}else{
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(com_zimbra_bulkprovision.CERT_INSTALL_BUTTON_text);
		}
	}
}   */
/*
ZaBulkProvisionWizard.getInstallMsg =
function () {
	if (ZaBulkProvisionWizard.INSTALL_STATUS == 0) {
		return com_zimbra_bulkprovision.CERT_INSTALL_STATUS_0 ;
	}else if (ZaBulkProvisionWizard.INSTALL_STATUS == 1){
		return com_zimbra_bulkprovision.CERT_INSTALL_STATUS_1;
	}else{
		return "";
	}
}   */

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/

ZaBulkProvisionWizard.prototype.popup =
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	/*
    if (this._containedObject[ZaCert.A_csv]) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	}else{
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	} */

	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
}


/*
ZaBulkProvisionWizard.prototype.finishWizard =
function() {
	try {
		// Basically, it will do two things:
		//1) install the cert
		//2) Upon the successful install, the cert tab information will be updated
		var instance = this._localXForm.getInstance () ;
		var validationDays = instance[ZaCert.A_validation_days] ;

		var selfType = instance[ZaCert.A_type_self] ;
		var commType = instance[ZaCert.A_type_comm] ;
		var csrType = instance[ZaCert.A_type_csr] ;

		var contentElement =  null ;
		if (selfType) {
			type = ZaCert.A_type_self ;
		}else if (commType) {
			type = ZaCert.A_type_comm ;
		}else if (csrType){
			this.popdown();
			return ;
		}else{
			throw new Exeption ("Unknow installation type") ;
		}

		var callback = new AjxCallback(this, this.installCallback);
		var params = {
			type: type,
			validation_days: validationDays,
			comm_cert: this.uploadResults,
            subject: this._containedObject.attrs,
            //allserver: (this._containedObject[ZaCert.A_target_server] == ZaCert.ALL_SERVERS) ? 1 : 0,
			callback: callback
		}
		ZaCert.installCert (this._app, params, this._containedObject[ZaCert.A_target_server]  ) ;

		this.popdown();

	} catch (ex) {
		this._app.getCurrentController()._handleException(ex, "ZaBulkProvisionWizard.prototype.finishWizard", null, false);
	}
} */

ZaBulkProvisionWizard.prototype._uploadCallback =
function (status, uploadResults) {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	//if (AjxEnv.hasFirebug)
		console.log("Cert File Upload: status = " + status);
	if ((status == AjxPost.SC_OK) && (uploadResults != null) && (uploadResults.length > 0)) {
    	var v = uploadResults[0] ;
        if (v.aid != null && v.aid.length > 0) {
           this._containedObject [ZaBulkProvision.A_csv_aid] =  v.aid ;
        } else {
           this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_upload_csv_no_aid, null, null, true);
           return ;
        }
        //File is uploaded successfully
        try {
            var resp = ZaBulkProvision.getBulkProvisionAccounts(this._app, this._containedObject [ZaBulkProvision.A_csv_aid]);
            if (resp.aid == this._containedObject[ZaBulkProvision.A_csv_aid]) {
                this._containedObject[ZaBulkProvision.A_provision_accounts] =
                                                ZaBulkProvision.initProvisionAccounts (resp.accounts) ;
                this._containedObject[ZaBulkProvision.A_isValidCSV] = resp[ZaBulkProvision.A_isValidCSV] ;
            }else{
                throw new AjxException(com_zimbra_bulkprovision.error_unmatching_aid) ;
            }
        }catch (ex) {
            this._app.getCurrentController()._handleException(ex) ;
            return ;
        }
        this.goPage(ZaBulkProvisionWizard.STEP_PROVISION);
		
	} else {
		// handle errors during attachment upload.
		var msg = AjxMessageFormat.format(com_zimbra_bulkprovision.error_upload_csv, [status]);
		this._app.getCurrentController().popupErrorDialog(msg, null, null, true);
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
        /*
        var params = {
            parent: this.parent,
            title: com_zimbra_bulkprovision.title_provision
        } ; */

//        this._provisionStatusDialog = new DwtMessageDialog (params) ;
//        this._provisionStatusDialog.setSize (432, 250) ;
//        this._provisionStatusDialog.setScrollStyle(Dwt.SCROLL)
        this._provisionStatusDialog = new ZaBulkProvisionStatusDialog (
                this.parent, this._app);
    }

    return this._provisionStatusDialog ;
}

ZaBulkProvisionWizard.prototype.goNext =
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if (AjxEnv.hasFirebug)
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
            return ; //allow the callback to handle the wizard buttons
        }catch (err) {
            this._app.getCurrentController().popupErrorDialog(com_zimbra_bulkprovision.error_no_csv_file_specified) ;
            return ;
        }
	}else if (cStep == ZaBulkProvisionWizard.STEP_PROVISION) {
	    //create the accounts now, it is a sychronous action with status updated
        var startTime = new Date ();
        console.log("Start provisiong accounts: " + startTime.toUTCString());
        var controller = this._app.getCurrentController() ;
//        var busyMsg = com_zimbra_bulkprovision.BUSY_START_PROVISION_ACCOUNTS ;
        var statusDialog = this.getProvisionStatusDialog() ;
        this._provisionStatusObject = {} ;
        this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_currentStatus] = com_zimbra_bulkprovision.BUSY_START_PROVISION_ACCOUNTS ;
        this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_createdAccounts] = [] ;
        var statusDialogCreatedAccounts =  this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_createdAccounts] ;
        //statusDialog.setMessage(busyMsg) ;
        statusDialog.setObject( this._provisionStatusObject) ;
        statusDialog.popup() ;

        var accounts = this._containedObject[ZaBulkProvision.A_provision_accounts] ;
        for (var i = 0 ; i < accounts.length; i ++) {
            var account = accounts[i] ;
            /*
            busyMsg += "<br />" + AjxMessageFormat.format (
                                   com_zimbra_bulkprovision.BUSY_CREATE_ACCOUNTS, [account[ZaBulkProvision.A2_accountName]]) ;
            */
            this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_currentStatus] = AjxMessageFormat.format (
                                   com_zimbra_bulkprovision.BUSY_CREATE_ACCOUNTS, [account[ZaBulkProvision.A2_accountName]]); 

            statusDialogCreatedAccounts[i] = {} ;
            statusDialogCreatedAccounts[i][ZaBulkProvision.A2_accountName] = account[ZaBulkProvision.A2_accountName] ;

            if (account[ZaBulkProvision.A2_isToProvision]) {

                var soapDoc = AjxSoapDoc.create("CreateAccountRequest", ZaZimbraAdmin.URN, null);
                soapDoc.set(ZaAccount.A_name, account.accountName);
                soapDoc.set(ZaAccount.A_password, account.password);

                //set the account attributes
                var attrs = {} ;
                if ((this._containedObject[ZaBulkProvision.A_mustChangePassword] == "TRUE")
                        || (account[ZaBulkProvision.A_mustChangePassword] == "TRUE")){
                    attrs [ZaAccount.A_zimbraPasswordMustChange] = "TRUE" ;
                }
                
                if (account[ZaBulkProvision.A2_displayName]) {
                    attrs [ZaAccount.A_displayname] = account[ZaBulkProvision.A2_displayName] ; 
                }

                for (var aname in attrs) {
                    var attr = soapDoc.set("a", attrs[aname]) ;
                    attr.setAttribute("n", aname) ;
                }

                var csfeParams = new Object();
                csfeParams.soapDoc = soapDoc;
                var reqMgrParams = {} ;
                //No busy dialog for single command
                //reqMgrParams.controller = app.getCurrentController();
                //reqMgrParams.busyMsg = ZaMsg.BUSY_CREATE_ACCOUNTS ;
                try {
                    resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.CreateAccountResponse;
                    if (resp && resp.account && resp.account[0].id) {
//                        busyMsg += com_zimbra_bulkprovision.SUCCEEDED ;
                        statusDialogCreatedAccounts[i][ZaBulkProvision.A2_status] = com_zimbra_bulkprovision.SUCCEEDED ;
                        this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_createdAccounts].push(

                                account[ZaBulkProvision.A2_accountName]) ;
                        
                        accounts[i][ZaBulkProvision.A2_status] = com_zimbra_bulkprovision.SUCCEEDED ;
                        accounts[i][ZaBulkProvision.A2_isToProvision] = false ;     //succeed, don't try to create the second time
                        accounts[i][ZaBulkProvision.A2_isValid] = "TRUE" ;
                    } else {
                        accounts[i][ZaBulkProvision.A2_isValid] = "FALSE" ;
                    }
                }catch (ex) {
//                    busyMsg += com_zimbra_bulkprovision.BUSY_FAILED ;
                    statusDialogCreatedAccounts[i][ZaBulkProvision.A2_status] = com_zimbra_bulkprovision.BUSY_FAILED ;
                    accounts[i][ZaBulkProvision.A2_status] = com_zimbra_bulkprovision.BUSY_FAILED + ( ex.msg  ? ": " + ex.msg : "") ;
                    accounts[i][ZaBulkProvision.A2_isValid] = "FALSE" ;
                }
            }else{
                  com_zimbra_bulkprovision.BUSY_FAILED =  com_zimbra_bulkprovision.SKIP ;
//                busyMsg += com_zimbra_bulkprovision.SKIP ;
            }

//            statusDialog.setMessage(busyMsg) ;
            statusDialog.setObject(this._provisionStatusObject ) ;
        }

//        busyMsg += "<br />" + com_zimbra_bulkprovision.DONE ;
//        statusDialog.setMessage( busyMsg) ;
        this._provisionStatusObject [ZaBulkProvisionStatusDialog.A_currentStatus] =  com_zimbra_bulkprovision.DONE ;
        statusDialog.setObject(this._provisionStatusObject ) ;

        var endTime = new Date ();
        console.log("End provisiong accounts: " + endTime.toUTCString());
        var total = endTime.getTime () - startTime.getTime () ;
        
        console.log("Total Time (ms): "  + total) ; 
        //update the status now
        ZaBulkProvision.updateBulkProvisionStatus (this._app, this._containedObject) ;
        nextStep = ZaBulkProvisionWizard.STEP_SUMMARY ;
        this.goPage(nextStep) ;
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
    } else if (pageKey == ZaBulkProvisionWizard.STEP_SUMMARY) {
        next = false ;
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

	html[idx++] = "<tbody><tr><td>" + com_zimbra_bulkprovision.CSV_Upload_file + "</td>";
	html[idx++] = "<td><input type=file  name='csvFile' size='40'></input></td></tr>";

	html[idx++] = "</tbody></table></div>";

	html[idx++] = "</form></div>";

	return html.join("");
}

ZaBulkProvisionWizard.myXFormModifier = function(xFormObject) {
	var cases = new Array();

	var case_upload_csv = {type:_CASE_, numCols:2, colSizes:["200px","*"],
					relevant:"instance[ZaModel.currentStep] == ZaBulkProvisionWizard.STEP_UPLOAD_CSV ",
					items: [

						{ type:_GROUP_, id: "CSVUpload",
							colSpan: 2, numCols: 1, colSizes: "*", items : [
								{ type:_OUTPUT_, value: com_zimbra_bulkprovision.CSV_uploadTitle, align: _LEFT_},
								{ type:_SPACER_ , height: 10 },
                                { type:_OUTPUT_, value: ZaBulkProvisionWizard.getUploadFormHtml() } ,
                                { type: _GROUP_, numCols: 2,items: [
                                        { type: _CHECKBOX_, ref: ZaBulkProvision.A_mustChangePassword,
                                            label:com_zimbra_bulkprovision.CKB_mustChangePasswd, labelLocation:_RIGHT_,
                                            trueValue:"TRUE", falseValue:"FALSE",
                                            align: _LEFT_
                                        }
                                    ]
                                },
                                { type:_SPACER_ , height: 10 } ,
                                { type:_OUTPUT_, value: com_zimbra_bulkprovision.CSV_uploadNotes } 
                            ]
						}
					]
				};
	cases.push(case_upload_csv);

	var case_provision = {type:_CASE_, numCols:2, colSizes:["200px", "*"], 
                    relevant:"instance[ZaModel.currentStep] == ZaBulkProvisionWizard.STEP_PROVISION",
					align:_LEFT_, valign:_TOP_};

    var bpAccountsListHeader = [] ;
    var i = 0 ;
    //TODO: Add the checkbox
    // bpAccountsListHeader[i++] = new ZaListHeaderItem(ZaBulkProvision.A2_isToProvision, ZaMsg.ALV_Name_col, null, 100, null, null, true, true);
    bpAccountsListHeader[i++] = new ZaListHeaderItem(ZaBulkProvision.A2_accountName, ZaMsg.ALV_Name_col, null, 150, null, null, true, true);
    bpAccountsListHeader[i++] = new ZaListHeaderItem(ZaBulkProvision.A2_displayName, ZaMsg.ALV_DspName_col, null, 150, null, null, true, true);
    bpAccountsListHeader[i++] = new ZaListHeaderItem(ZaBulkProvision.A2_password, com_zimbra_bulkprovision.ALV_Password_col, null, 50, null, null, true, true);
    bpAccountsListHeader[i++] = new ZaListHeaderItem(ZaBulkProvision.A2_status, com_zimbra_bulkprovision.ALV_Stauts_col, null, null, null, null, true, true);
    

    var case_provision_items = [
            {type:_GROUP_, colSpan: 2, numCols: 1,
                relevant: "instance[ZaBulkProvision.A_isValidCSV] == 'TRUE'",
                relevantBehavior: _HIDE_,
                items: [
                {type:_OUTPUT_, colSpan: 2,  value: com_zimbra_bulkprovision.BP_wizard_upload_status}
             ]
            },
        //relevant doesn't apply on the _OUTPUT_ item, must use _GROUP_ to make the relevant work  
            {type:_GROUP_, colSpan: 2, numCols: 1,
                relevant: "instance[ZaBulkProvision.A_isValidCSV] == 'FALSE'",
                relevantBehavior: _HIDE_,
                items: [
                    {type:_OUTPUT_, value: com_zimbra_bulkprovision.BP_wizard_csv_invalid}
                ]
            },
            { type:_SPACER_ , height: 5 } ,
             //provision account lists
            {ref:ZaBulkProvision.A_provision_accounts, type:_DWT_LIST_, height:"250", width:"525px",
                forceUpdate: true, cssClass: "DLSource",
                widgetClass: ZaBulkProvisionAccountsListView,
                headerList:bpAccountsListHeader, hideHeader: false
            }
        ];

	case_provision.items = case_provision_items;
	cases.push(case_provision);

	var case_summary =
		{type:_CASE_, numCols:1, colSizes:["*"],
			relevant:"instance[ZaModel.currentStep] == ZaBulkProvisionWizard.STEP_SUMMARY",
			align:_LEFT_, valign:_TOP_ ,
			items :[
				{ type:_OUTPUT_, value: com_zimbra_bulkprovision.summary_download },
				{ type:_SPACER_ , height: 10 },
				/*
                { type:_OUTPUT_, value:"<a href='adminres?action=getBPSummary' onclick='ZaZimbraAdmin.unloadHackCallback();'> "
										+ com_zimbra_bulkprovision.download_csv + "</a> "},
				*/

                { type:_OUTPUT_, ref: ZaBulkProvision.A_csv_aid, getDisplayValue: function (newValue) {
                        return "<a target='_blank' href='/service/afd/?action=getBP&aid=" + newValue
                            + "' onclick='ZaZimbraAdmin.unloadHackCallback();'> "
										+ com_zimbra_bulkprovision.download_csv + "</a> "
                    }
                },
                 /*
                { type: _ANCHOR_,  isNewWindow: true, href: "adminres" ,
                    label: com_zimbra_bulkprovision.download_csv, labelLocation:_NONE_,
                    onActivate: ZaBulkProvisionWizard.downloadBPStatus
                }, */
                { type:_SPACER_ , height: 10 },
                { ref:ZaBulkProvision.A_provision_accounts, type:_DWT_LIST_, height:"250", width:"525px",
                    forceUpdate: true, cssClass: "DLSource",
                    widgetClass: ZaBulkProvisionAccountsListView,
                    headerList:bpAccountsListHeader, hideHeader: false
                }
            ]
		}

	cases.push (case_summary) ;

	
    var contentW = 530 ;
    if (AjxEnv.isIE) {
        var contentW = 580 ;
    }
    xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices},
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
