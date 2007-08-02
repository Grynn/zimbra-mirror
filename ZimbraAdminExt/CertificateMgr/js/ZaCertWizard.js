ZaMsg.BUSY_GET_CSR = "Getting the CSR ..." ;
ZaMsg.CERT_WIZARD_title = "Certificate Installation Wizard" ;
ZaMsg.CERT_WIZARD_TABT_gencsr = "Generate the Certificate Signing Request" ;
ZaMsg.CERT_WIZARD_TABT_uploadCert = "Upload the Certificate" ;
ZaMsg.CERT_WIZARD_TABT_installCert = "Install the Certificate";
ZaMsg.CERT_WIZARD_TABT_useroption = "Choose the Installation Option";
ZaMsg.CERT_WIZARD_TABT_downloadCSR = "Download the Certificate Signing Request" ;
ZaMsg.CERT_WIZARD_TABT_reviewCSR = "Review the Certificate Signing Request"
ZaMsg.CERT_INFO_CN = "Common Name: " ;
ZaMsg.CERT_INFO_OU = "Organizational Unit: " ;
ZaMsg.CERT_INFO_C = "Country Name: ";
ZaMsg.CERT_INFO_ST = "State/Province: " ;
ZaMsg.CERT_INFO_O = "Organization Name: " ;
ZaMsg.CERT_INFO_L = "City: " ;

ZaMsg.CERT_INFO_SUBJECT = "Subject: "

ZaMsg.CERT_select_option= "Please choose one of the following options : "
ZaMsg.CERT_self_signed = "Install the self-signed certificate" ;
ZaMsg.CERT_gen_csr = "Generate the CSR for the commerical certificate authorizer" ;
ZaMsg.CERT_comm_signed = "Install the commercially signed certificate" ;
ZaMsg.CERT_uploadTitle = "Please upload your commercially signed certificate: " ;
ZaMsg.CERT_validate_days = "Certificate Validation Days: "
ZaMsg.CERT_installTitle = "Click install to install the certificates" ;

ZaMsg.CERT_INSTALLING = "Installing the certificates ..." ;
ZaMsg.CERT_INSTALL_STATUS_0 = "Your certificate was installed successfully. You must restart your ZCS server to apply the changes." ;
ZaMsg.CERT_INSTALL_STATUS_1 = "Your certificate was not installed due to the error " ;
ZaMsg.CERT_INSTALL_BUTTON_text = "Install" ;

ZaMsg.UploadCertErrorMsg = "Upload status {0}: failed to upload the license file at this moment." ;
ZaMsg.genCSRError = "There was an error generating the CSR." ;
ZaMsg.certFileNameError = "Invalid file name. Use browse button to select a valid license file." ;
ZaMsg.certTypeError = "Please select the certificate type." ;

ZaMsg.CSR_EXISTS_WARNING = 'The following CSR exists already.' ;
ZaMsg.CSR_NON_EXISTS_WARNING = 'No valid CSR was found.' ;
ZaMsg.FORCE_NEW_CSR = "Force to generate a new CSR" ;
ZaMsg.CSR_NON_EXISTS_MSG = "To install a commercially signed certificate, you must create the CSR first. Please go back to choose \"Generate the CSR for the commerical certificate authorizer\" option to create the CSR." ;

ZaMsg.CSR_download_msg_1 = "In order to obtain a commercially signed certificate, you must download the generated CSR and submit it to your commercial certificate authorizer. Once you get the certificate, please restart the \"Certificate Installation Wizard\" and choose the option \"Install the commercially signed certificate\" to complete the certificate installation.";
ZaMsg.CSR_download_msg_2 = "Right click and select \"Save As ...\" to download the CSR" ;
ZaMsg.CSR_REVIEW = "Please review the current CSR information, then click next to upload your certificate signed by the commercial certificate authorizer."
ZaMsg.TBB_launch_cert_wizard = "Install Certificate" 
ZaMsg.TBB_launch_cert_wizard_tt = "Launch the certificate installation wizard" ;
//ZaCertWizard

function ZaCertWizard (parent, app) {
	ZaXWizardDialog.call(this, parent, app, null, ZaMsg.CERT_WIZARD_title, "500px", "300px","ZaCertWizard");

	this.stepChoices = [
		{label:ZaMsg.CERT_WIZARD_TABT_useroption, value:ZaCertWizard.STEP_USER_OPTION},
		{label:ZaMsg.CERT_WIZARD_TABT_gencsr, value:ZaCertWizard.STEP_GEN_CSR},
		{label:ZaMsg.CERT_WIZARD_TABT_uploadCert, value:ZaCertWizard.STEP_UPLOAD_CERT},
		{label:ZaMsg.CERT_WIZARD_TABT_installCert, value:ZaCertWizard.STEP_INSTALL_CERT},
		{label:ZaMsg.CERT_WIZARD_TABT_downloadCSR, value:ZaCertWizard.STEP_DOWNLOAD_CSR},
		{label:ZaMsg.CERT_WIZARD_TABT_reviewCSR, value:ZaCertWizard.STEP_CSR_CONFIRM},		
	];
	
	//this._lastStep = this.stepChoices.length;
	this.attId = null ;
	//this.initForm(null,this.getMyXForm());	
	this.initForm(ZaCert.myXModel,this.getMyXForm());	
	//this.license = new ZaLicense(app);
   
	this._localXForm.setController(this._app);	
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaCertWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaCertWizard.prototype.handleXFormChange));	
	this._helpURL = ZaCertWizard.helpURL;
}

// -1 : No status, 0: Install succeed, >0 : Install Failed (different number is different error)
ZaCertWizard.INSTALL_STATUS = -1;
ZaCertWizard.STEP_USER_OPTION = 1
ZaCertWizard.STEP_GEN_CSR = 2 ;
ZaCertWizard.STEP_UPLOAD_CERT = 3 ;
ZaCertWizard.STEP_INSTALL_CERT = 4 ;
ZaCertWizard.STEP_DOWNLOAD_CSR = 5 ;
ZaCertWizard.STEP_CSR_CONFIRM = 6 ;

ZaCertWizard.prototype = new ZaXWizardDialog;
ZaCertWizard.prototype.constructor = ZaCertWizard;
ZaXDialog.XFormModifiers["ZaCertWizard"] = new Array();
ZaCertWizard.helpURL = location.pathname + "adminhelp/html/WebHelp/managing_global_settings/updating_your_zimbra_license.htm";
ZaCertWizard.prototype.handleXFormChange = 
function () {
	if(this._localXForm.hasErrors()) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		if (this._containedObject[ZaModel.currentStep] == ZaCertWizard.STEP_INSTALL_CERT ) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		}
		
		if (this._containedObject[ZaModel.currentStep] == ZaCertWizard.STEP_DOWNLOAD_CSR ) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(AjxMsg._finish);
		}
		
		if (this._containedObject[ZaCert.A_type_csr]) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(AjxMsg._finish);
		}else{
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(ZaMsg.CERT_INSTALL_BUTTON_text);			
		}
	}
}

ZaCertWizard.getInstallMsg =
function () {
	if (ZaCertWizard.INSTALL_STATUS == 0) {
		return ZaMsg.CERT_INSTALL_STATUS_0 ;
	}else if (ZaCertWizard.INSTALL_STATUS == 1){
		return ZaMsg.CERT_INSTALL_STATUS_1;
	}else{
		return "";
	}
}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaCertWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

ZaCertWizard.prototype.finishWizard = 
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
		
		ZaCert.installCert (this._app, type, validationDays,
					this.attId, callback ) ;
			
		this.popdown();	
			
	} catch (ex) {
		this._app.getCurrentController()._handleException(ex, "ZaCertWizard.prototype.finishWizard", null, false);
	}
}

ZaCertWizard.getCause = 
function (detailMsg) {
	//TODO: get the cert related detail exceptions
	var causeBy = /Caused by:\s*com.zimbra.cs.license.LicenseException:\s*(.*)/;
	
	var result = detailMsg.match(causeBy);
	if (result != null) {
    	return result [1] ;
	}else{
		return detailMsg ;
	}
}

ZaCertWizard.prototype.installCallback =
function (resp){
	var controller = this._app.getCurrentController();
	var certView = controller._contentView ;
	try {
		if (resp._isException) {
			var detailMsg = resp._data.msg ;			
			throw new AjxException(ZaMsg.CERT_INSTALL_STATUS_1 + ": " + ZaCertWizard.getCause(detailMsg), "ZaCertWizard.prototype.installCallback", AjxException.UNKNOWN_ERROR, detailMsg) ;
			//throw new Error(resp._data.msg) ;
		}else{
			var installResponse = resp._data.Body.InstallCertResponse ;
			if (installResponse) {
				//update the cert info tab
				ZaCertWizard.INSTALL_STATUS = 0;
				certView._certInstallStatus.setStyle (DwtAlert.INFORMATION) ;
				certView._certInstallStatus.setContent(ZaMsg.CERT_INSTALL_STATUS_0);
				controller.show(ZaCert.getCerts(this._app)) ;
				ZaCertWizard.INSTALL_STATUS = -1;
			}else{
				throw new AjxException(ZaMsg.CERT_INSTALL_STATUS_1, "installCallback", AjxException.UNKNOWN_ERROR) ;
				//throw new AjxException ();
			}
		}
	}catch (ex){
		ZaCertWizard.INSTALL_STATUS = 1;
		certView._certInstallStatus.setContent(ZaMsg.CERT_INSTALL_STATUS_1 + ": " + ex.msg);
		certView._certInstallStatus.setStyle (DwtAlert.CRITICAL) ;
		controller.show(ZaCert.getCerts(this._app)) ;
		ZaCertWizard.INSTALL_STATUS = -1; //reset hte install_status
		//alert(ex);
		controller.popupErrorDialog(ex.msg, ex, true);
		//_handleException(ex, "ZaCertWizard.prototype.installCallback", null, false);
	}
}

ZaCertWizard.prototype._uploadCallback =
function (status, attId) {
	if (AjxEnv.hasFirebug) console.log("Cert File Upload: status = " + status + ", attId = " + attId);
	if ((status == AjxPost.SC_OK) && (attId != null)) {
		this.attId = attId ;
		
		//go to the next page, it is always the installation wizard
		this.goPage(ZaCertWizard.STEP_INSTALL_CERT);
		//this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);		
		
		if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		}	
	} else {
		// handle errors during attachment upload.
		var msg = AjxMessageFormat.format(ZaMsg.UploadCertErrorMsg, status);
		this._app.getCurrentController().popupErrorDialog(msg + ZaMsg.ErrorTryAgain, null, null, true);		
	}	
}

//upload the file
ZaCertWizard.prototype.getUploadFrameId =
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

ZaCertWizard.prototype.getUploadManager = 
function() { 
	return this._uploadManager;
};

/**
 * @params uploadManager is the AjxPost object
 */
ZaCertWizard.prototype.setUploadManager = 
function(uploadManager) {
	this._uploadManager = uploadManager;
};


ZaCertWizard.prototype.goNext = 
function() {
	if (AjxEnv.hasFirebug) console.log("Go Next");
	
	var nextStep;
	if (this._containedObject[ZaModel.currentStep] == ZaCertWizard.STEP_USER_OPTION) {
		if (this._containedObject[ZaCert.A_type_csr] 
			|| this._containedObject[ZaCert.A_type_self]) {
		 	nextStep = ZaCertWizard.STEP_GEN_CSR ;
		} else if (this._containedObject[ZaCert.A_type_comm]) {
			nextStep = ZaCertWizard.STEP_CSR_CONFIRM ;
			if (!this._containedObject[ZaCert.A_csr_exists]) {
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			}
		}
		this.goPage(nextStep) ;
	}else if (this._containedObject[ZaModel.currentStep] == ZaCertWizard.STEP_GEN_CSR) {
		if (this._containedObject[ZaCert.A_type_csr]) {
		 	nextStep = ZaCertWizard.STEP_DOWNLOAD_CSR ;
		} else if (this._containedObject[ZaCert.A_type_self]) {
			nextStep = ZaCertWizard.STEP_INSTALL_CERT ;
		}
		try {
			if ((!this._containedObject[ZaCert.A_csr_exists]) || (this._containedObject[ZaCert.A_force_new_csr] == 'TRUE')){
				ZaCert.genCSR (this._app, this._containedObject.attrs, this._containedObject[ZaCert.A_force_new_csr]) ;
			}else{
				if (AjxEnv.hasFirebug) console.log("Prevous CSR exists, skip the CSR generation.") ;
			}
		}catch (ex) {
			this._app.getCurrentController().popupErrorDialog(ZaMsg.genCSRError, ex, true) ;		
		}
		this.goPage(nextStep) ;
	}else if (this._containedObject[ZaModel.currentStep] == ZaCertWizard.STEP_UPLOAD_CERT) {
		nextStep = ZaCertWizard.STEP_INSTALL_CERT ;
		if (this._containedObject[ZaCert.A_type_comm]) {
			//1. check if the file name are valid and exists
			//2. Upload the files
			DBG.println("Start uploading the file");
			this.setUploadManager(new AjxPost(this.getUploadFrameId()));
			var certUploadCallback = new AjxCallback(this, this._uploadCallback);
			var um = this.getUploadManager() ; 
			window._uploadManager = um;
			try {
				um.execute(certUploadCallback, document.getElementById (ZaCertWizard.CertUploadFormId));
				// goPage is called in the callback
				// this.goPage(this._containedObject[ZaModel.currentStep] + 1) ;
			}catch (err) {
				this._app.getCurrentController().popupErrorDialog(ZaMsg.certFileNameError) ;
				return ;
			}			
		}else if (this._containedObject[ZaCert.A_type_self]) {
			this.goPage(nextStep) ;
		}else {
			this._app.getCurrentController().popupErrorDialog(ZaMsg.certTypeError) ;	
		}
	}else if (this._containedObject[ZaModel.currentStep] == ZaCertWizard.STEP_CSR_CONFIRM) {
		nextStep = ZaCertWizard.STEP_UPLOAD_CERT;
		this.goPage(nextStep) ;
	}
		
	/*			
	if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
		//this._button[DwtWizardDialog.CANCEL_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	} else {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	}*/
	
	if (nextStep == ZaCertWizard.STEP_INSTALL_CERT || nextStep == ZaCertWizard.STEP_DOWNLOAD_CSR) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	}
	
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
}

ZaCertWizard.prototype.goPrev = 
function() {
	DBG.println("Go Previous");
	var prevStep ;
	if (this._containedObject[ZaModel.currentStep] == ZaCertWizard.STEP_DOWNLOAD_CSR) {
		prevStep = ZaCertWizard.STEP_GEN_CSR ;
	}
	
	if (this._containedObject[ZaModel.currentStep] == ZaCertWizard.STEP_GEN_CSR
		|| this._containedObject[ZaModel.currentStep] == ZaCertWizard.STEP_CSR_CONFIRM) {		
		prevStep = ZaCertWizard.STEP_USER_OPTION ;
	}
	
	if (this._containedObject[ZaModel.currentStep] == ZaCertWizard.STEP_UPLOAD_CERT ) {
		prevStep = ZaCertWizard.STEP_CSR_CONFIRM ;
	}
	
	if ( this._containedObject[ZaModel.currentStep] == ZaCertWizard.STEP_INSTALL_CERT ){
		if (this._containedObject[ZaCert.A_type_comm]) {
			prevStep = ZaCertWizard.STEP_UPLOAD_CERT ;
		}else if (this._containedObject[ZaCert.A_type_self]) {
			prevStep = ZaCertWizard.STEP_GEN_CSR ;
		}
	}
	
	if (prevStep == ZaCertWizard.STEP_USER_OPTION) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	}
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	
	this.goPage(prevStep);
}

/**
* @method setObject sets the object contained in the view
* @param entry - ZaLicense object to display
**/
ZaCertWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();	
	this._containedObject = entry ;
	
	this._containedObject[ZaModel.currentStep] = 1;
	this._localXForm.setInstance(this._containedObject);
}

ZaCertWizard.CertUploadAttachmentInputId = Dwt.getNextId();
ZaCertWizard.CertUploadFormId = Dwt.getNextId();
ZaCertWizard.getUploadFormHtml =
function (){
	//var uri = location.protocol + "//" + document.domain + appContextPath 
	//							+ "/../service/upload";
	var uri = appContextPath + "/../service/upload";
	var html = new Array();
	var idx = 0;
	html[idx++] = "<div style='overflow:auto'><form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaCertWizard.CertUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'><input id='";
	html[idx++] = ZaCertWizard.CertUploadAttachmentInputId;
	html[idx++] = "' type=file  name='certFile' size='50'></input>";

	html[idx++] = "</form></div>";
	
	return html.join("");
}

ZaCertWizard.myXFormModifier = function(xFormObject) {		
	var cases = new Array();
	
	var case_user_options = {type:_CASE_, numCols:2, colSizes:["25px","*"], 
			relevant:"instance[ZaModel.currentStep] == ZaCertWizard.STEP_USER_OPTION", 
			align:_LEFT_, valign:_TOP_, cssStyle:"padding-left:50px;"};
			
	var case_user_options_items = [
				{ type:_SPACER_, height: 10},
				{ type:_OUTPUT_, colSpan: 2, value: ZaMsg.CERT_select_option},
				{ type:_SPACER_, height: 10},
				{ type:_RADIO_,  groupname: "install_type", ref: ZaCert.A_type_self,
					label: ZaMsg.CERT_self_signed, labelLocation:_RIGHT_ , align: _LEFT_ ,
						//TODO: Change it on the XFormItem level
						onChange: function (value, event, form) {
							this.setInstanceValue (value) ;
							this.setInstanceValue (!value, ZaCert.A_type_comm ) ;
							this.setInstanceValue (!value, ZaCert.A_type_csr) ;
						},
						updateElement:function (newValue) {
							this.getElement().checked = (newValue == true);
						}
					},
				{ type:_RADIO_,  groupname: "install_type", ref: ZaCert.A_type_csr,
					label: ZaMsg.CERT_gen_csr, labelLocation:_RIGHT_ , align: _LEFT_ ,
						//TODO: Change it on the XFormItem level
						onChange: function (value, event, form) {
							this.setInstanceValue (value) ;
							this.setInstanceValue (!value, ZaCert.A_type_self ) ;
							this.setInstanceValue (!value, ZaCert.A_type_comm ) ;
						},
						updateElement:function (newValue) {
							this.getElement().checked = (newValue == true);
						}
					},	
				{ type:_RADIO_,  groupname: "install_type", ref: ZaCert.A_type_comm,
					updateElement:function (newValue) {
						this.getElement().checked = (newValue == true);
					},
					onChange: function (value, event, form) {
						this.setInstanceValue (value) ;
						this.setInstanceValue (!value, ZaCert.A_type_self ) ;
						this.setInstanceValue (!value, ZaCert.A_type_csr) ;
					},
					label: ZaMsg.CERT_comm_signed, labelLocation:_RIGHT_ , align: _LEFT_}
	];
	case_user_options.items = case_user_options_items;
	cases.push(case_user_options);
			
	
	var case_gen_csr = {type:_CASE_, numCols:2, colSizes:["200px","*"], 
		relevant:"instance[ZaModel.currentStep] == ZaCertWizard.STEP_GEN_CSR", 
		align:_LEFT_, valign:_TOP_};
		
	var case_gen_csr_items = [
		{type: _DWT_ALERT_, colSpan:2, relevant: "instance[ZaCert.A_csr_exists] == true ",
				relevantBehavior: _HIDE_ , containerCssStyle: "width:400px;",
				style: DwtAlert.WARNING, iconVisible: false,
				content: ZaMsg.CSR_EXISTS_WARNING 
		 }, 
		{type: _GROUP_ , colSpan:2, numCols: 2, colSizes:["200px","*"],
			//relevant: " !instance[ZaCert.A_csr_exists] ||  (instance[ZaCert.A_force_new_csr] == 'TRUE') ",
			//relevantBehavior: _DISABLE_, 
			items :[
				{ ref: ZaCert.A_commonName, type:_TEXTFIELD_, width: 150, 
					relevant: " !instance[ZaCert.A_csr_exists] ||  (instance[ZaCert.A_force_new_csr] == 'TRUE') ",
					relevantBehavior: _DISABLE_, 
					label: ZaMsg.CERT_INFO_CN},
				{ ref: ZaCert.A_countryName, type:_TEXTFIELD_, width: 150, 
					relevant: " !instance[ZaCert.A_csr_exists] ||  (instance[ZaCert.A_force_new_csr] == 'TRUE') ",
					relevantBehavior: _DISABLE_, 
					label: ZaMsg.CERT_INFO_C},
				{ ref: ZaCert.A_state, type:_TEXTFIELD_, width: 150, 
					relevant: " !instance[ZaCert.A_csr_exists] ||  (instance[ZaCert.A_force_new_csr] == 'TRUE') ",
					relevantBehavior: _DISABLE_, 
					label: ZaMsg.CERT_INFO_ST},
				{ ref: ZaCert.A_city, type:_TEXTFIELD_, width: 150, 
					relevant: " !instance[ZaCert.A_csr_exists] ||  (instance[ZaCert.A_force_new_csr] == 'TRUE') ",
					relevantBehavior: _DISABLE_, 
					label: ZaMsg.CERT_INFO_L},
				{ ref: ZaCert.A_organization, type:_TEXTFIELD_, width: 150, 
					relevant: " !instance[ZaCert.A_csr_exists] ||  (instance[ZaCert.A_force_new_csr] == 'TRUE') ",
					relevantBehavior: _DISABLE_, 
					label: ZaMsg.CERT_INFO_O},
				{ ref: ZaCert.A_organizationUnit, type:_TEXTFIELD_, width: 150, 
					relevant: " !instance[ZaCert.A_csr_exists] ||  (instance[ZaCert.A_force_new_csr] == 'TRUE') ",
					relevantBehavior: _DISABLE_, 
					label: ZaMsg.CERT_INFO_OU}
			]
		},
		
		{ref: ZaCert.A_force_new_csr, type: _CHECKBOX_ , label: ZaMsg.FORCE_NEW_CSR , 
			relevant: " instance[ZaCert.A_csr_exists] == true ",
			relevantBehavior: _HIDE_, 
			trueValue:"TRUE", falseValue:"FALSE", msgName:ZaMsg.FORCE_NEW_CSR }
		
	];	
	
	case_gen_csr.items = case_gen_csr_items;
	cases.push(case_gen_csr);
	
	var case_upload_cert={type:_CASE_, numCols:2, colSizes:["200px","*"], 
					relevant:"instance[ZaModel.currentStep] == ZaCertWizard.STEP_UPLOAD_CERT ",
					items: [
						{ type:_SPACER_ , height: 10 },
						{ type:_GROUP_, id: "CertUpload", 
							relevant: "instance[ZaCert.A_type_comm] == true",
							relevantBehavior: _HIDE_,
							colSpan: 2, numCols: 1, colSizes: "*", items : [
								{ type:_OUTPUT_, value: ZaMsg.CERT_uploadTitle, align: _LEFT_},
								{ type:_OUTPUT_, value: ZaCertWizard.getUploadFormHtml() } ,
								{ type:_SPACER_ , height: 10 }
							]
						},
						{ type:_SPACER_ , height: 10 },
						
					]
				};
	cases.push(case_upload_cert);
	
	var case_install_cert = {type:_CASE_, numCols:2, colSizes:["200px", "*"], relevant:"instance[ZaModel.currentStep] == ZaCertWizard.STEP_INSTALL_CERT", 
					align:_LEFT_, valign:_TOP_};
	var case_install_certItems = [
			{type:_OUTPUT_, colSpan: 2, value: ZaMsg.CERT_installTitle },
			{type:_TEXTFIELD_, ref: ZaCert.A_validation_days ,			
				//Validation_days is required
				//relevant: "instance[ZaCert.A_type_self] == true", relevantBehavior: _HIDE_,
				label: ZaMsg.CERT_validate_days
			}
		];	
	
	case_install_cert.items = case_install_certItems;
	cases.push(case_install_cert);
	
	var case_download_csr = 
		{type:_CASE_, numCols:1, colSizes:["*"], 
			relevant:"instance[ZaModel.currentStep] == ZaCertWizard.STEP_DOWNLOAD_CSR", 
			align:_LEFT_, valign:_TOP_ , 
			items :[
				{ type:_OUTPUT_, value: ZaMsg.CSR_download_msg_1 },
				{ type:_SPACER_ , height: 10 },
				{ type:_OUTPUT_, value:"<a href='/zimbraAdmin/tmp/current.csr' target='_blank' onclick='ZaZimbraAdmin.unloadHackCallback();'> "
										+ ZaMsg.CSR_download_msg_2 + "</a> "},
				{ type:_SPACER_ , height: 10 }
			]
		}		
	
	cases.push (case_download_csr) ;
	
	var case_csr_confirm = 		
		{type:_CASE_, numCols:1, colSizes:["*"], 
			relevant:"instance[ZaModel.currentStep] == ZaCertWizard.STEP_CSR_CONFIRM", 
			align:_LEFT_, valign:_TOP_ , 
			items :[
				{type: _GROUP_ , colSpan:2, numCols: 2, colSizes:["200px","*"],
				relevant: " instance[ZaCert.A_csr_exists]  ",
				relevantBehavior: _HIDE_, 
				items :[
						{ type: _OUTPUT_, value: ZaMsg.CSR_REVIEW, colSpan: 2 },
						{ type:_SPACER_, height:10},
						{ ref: ZaCert.A_commonName, type:_OUTPUT_, width: 150, 
							label: ZaMsg.CERT_INFO_CN},
						{ ref: ZaCert.A_countryName, type:_OUTPUT_, width: 150, 
							label: ZaMsg.CERT_INFO_C},
						{ ref: ZaCert.A_state, type:_OUTPUT_, width: 150, 
							label: ZaMsg.CERT_INFO_ST},
						{ ref: ZaCert.A_city, type:_OUTPUT_, width: 150, 
							label: ZaMsg.CERT_INFO_L},
						{ ref: ZaCert.A_organization, type:_OUTPUT_, width: 150, 
							label: ZaMsg.CERT_INFO_O},
						{ ref: ZaCert.A_organizationUnit, type:_OUTPUT_, width: 150, 
							label: ZaMsg.CERT_INFO_OU}
					]
				},
				{type: _GROUP_ , colSpan:2, numCols: 1, colSizes:["*"],
					relevant: " ! instance[ZaCert.A_csr_exists]  ",
					relevantBehavior: _HIDE_, 
					cssStyle:"padding-left:50px;", items: [
						{type: _DWT_ALERT_, colSpan:2, relevant: " !instance[ZaCert.A_csr_exists] ",
							relevantBehavior: _HIDE_ , containerCssStyle: "width:400px;",
							style: DwtAlert.WARNING, iconVisible: false,
							content: ZaMsg.CSR_NON_EXISTS_WARNING 
				 		},
				 		{type:_SPACER_, height:10},
				 		{type: _OUTPUT_, value: ZaMsg.CSR_NON_EXISTS_MSG }
			 		]
				}
			]
		}
	
		cases.push (case_csr_confirm) ;
		
	xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep, choices:this.stepChoices},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:450, align:_LEFT_, valign:_TOP_, items:cases}
		];
};
ZaXDialog.XFormModifiers["ZaCertWizard"].push(ZaCertWizard.myXFormModifier);


