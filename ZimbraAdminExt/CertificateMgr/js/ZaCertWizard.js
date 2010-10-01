/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
//ZaCertWizard
function ZaCertWizard (parent) {
    var w = "500px" ;
    if (AjxEnv.isIE) {
        w = "550px" ;
    }
    ZaXWizardDialog.call(this, parent, null, com_zimbra_cert_manager.CERT_WIZARD_title, w, "300px","ZaCertWizard");

	this.stepChoices = [
		{label:com_zimbra_cert_manager.CERT_WIZARD_TABT_selectServer, value:ZaCertWizard.STEP_SELECT_SERVER},
		{label:com_zimbra_cert_manager.CERT_WIZARD_TABT_useroption, value:ZaCertWizard.STEP_USER_OPTION},
		{label:com_zimbra_cert_manager.CERT_WIZARD_TABT_gencsr, value:ZaCertWizard.STEP_GEN_CSR},
		{label:com_zimbra_cert_manager.CERT_WIZARD_TABT_uploadCert, value:ZaCertWizard.STEP_UPLOAD_CERT},
		{label:com_zimbra_cert_manager.CERT_WIZARD_TABT_installCert, value:ZaCertWizard.STEP_INSTALL_CERT},
		{label:com_zimbra_cert_manager.CERT_WIZARD_TABT_downloadCSR, value:ZaCertWizard.STEP_DOWNLOAD_CSR},
		{label:com_zimbra_cert_manager.CERT_WIZARD_TABT_reviewCSR, value:ZaCertWizard.STEP_CSR_CONFIRM}		
	];
		
	//this._lastStep = this.stepChoices.length;
	this.uploadInputs = {} ;
	this.uploadResults = null ;
	//this.initForm(null,this.getMyXForm());	
	this.initForm(ZaCert.myXModel,this.getMyXForm());	
   
	this._localXForm.setController(ZaApp.getInstance());	
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaCertWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaCertWizard.prototype.handleXFormChange));	
	this._helpURL = ZaCertWizard.helpURL;
}

// -1 : No status, 0: Install succeed, >0 : Install Failed (different number is different error)
ZaCertWizard.INSTALL_STATUS = -1;
ZaCertWizard.STEP_INDEX = 1 ;
ZaCertWizard.STEP_SELECT_SERVER = ZaCertWizard.STEP_INDEX ++ ;
ZaCertWizard.STEP_USER_OPTION = ZaCertWizard.STEP_INDEX ++ ;
ZaCertWizard.STEP_GEN_CSR = ZaCertWizard.STEP_INDEX ++ ;
ZaCertWizard.STEP_UPLOAD_CERT = ZaCertWizard.STEP_INDEX ++ ;
ZaCertWizard.STEP_INSTALL_CERT = ZaCertWizard.STEP_INDEX ++ ;
ZaCertWizard.STEP_DOWNLOAD_CSR = ZaCertWizard.STEP_INDEX ++ ;
ZaCertWizard.STEP_CSR_CONFIRM = ZaCertWizard.STEP_INDEX ++ ;

ZaCertWizard.prototype = new ZaXWizardDialog;
ZaCertWizard.prototype.constructor = ZaCertWizard;
ZaXDialog.XFormModifiers["ZaCertWizard"] = new Array();
ZaCertWizard.helpURL = location.pathname + "help/admin/html/tools/installing_certificates.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
ZaCertWizard.prototype.handleXFormChange = 
function () {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if(this._localXForm.hasErrors()) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		if (cStep == ZaCertWizard.STEP_SELECT_SERVER) {
			if (this._containedObject[ZaCert.A_target_server]) {
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled (true) ;
			}
		}
		
		if (cStep == ZaCertWizard.STEP_INSTALL_CERT ) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		}
		
		if (cStep == ZaCertWizard.STEP_DOWNLOAD_CSR ) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(AjxMsg._finish);
		}
		
		if (this._containedObject[ZaCert.A_type_csr]) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(AjxMsg._finish);
		}else{
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(com_zimbra_cert_manager.CERT_INSTALL_BUTTON_text);			
		}
	}
}

ZaCertWizard.getInstallMsg =
function () {
	if (ZaCertWizard.INSTALL_STATUS == 0) {
		return com_zimbra_cert_manager.CERT_INSTALL_STATUS_0 ;
	}else if (ZaCertWizard.INSTALL_STATUS == 1){
		return com_zimbra_cert_manager.CERT_INSTALL_STATUS_1;
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
	if (this._containedObject[ZaCert.A_target_server]) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);	
	}else{
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
	}
	
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
		var params = {
			type: type,
			validation_days: validationDays,
			comm_cert: this.uploadResults,
            subject: this._containedObject.attrs,
            keysize: this._containedObject.keysize,
            //allserver: (this._containedObject[ZaCert.A_target_server] == ZaCert.ALL_SERVERS) ? 1 : 0,
			callback: callback 
		}
		ZaCert.installCert (ZaApp.getInstance(), params, this._containedObject[ZaCert.A_target_server]  ) ;
			
		this.popdown();	
			
	} catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaCertWizard.prototype.finishWizard", null, false);
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
	var controller = ZaApp.getInstance().getCurrentController();
	var statusElement = controller._contentView._certInstallStatus ;
	
	try {
		if (resp._isException) {
			var detailMsg = resp._data.msg ;			
			throw new AjxException(com_zimbra_cert_manager.CERT_INSTALL_STATUS_1 + ": " + ZaCertWizard.getCause(detailMsg), "ZaCertWizard.prototype.installCallback", AjxException.UNKNOWN_ERROR, detailMsg) ;
			//throw new Error(resp._data.msg) ;
		}else{
			var installResponse = resp._data.Body.InstallCertResponse ;
			if (installResponse) {
				//update the cert info tab
				ZaCertWizard.INSTALL_STATUS = 0;
								
				statusElement.setStyle (DwtAlert.INFORMATION) ;
				statusElement.setContent(com_zimbra_cert_manager.CERT_INSTALL_STATUS_0);
				if (controller instanceof ZaCertViewController) {
					controller.show(ZaCert.getCerts(ZaApp.getInstance(), this._containedObject[ZaCert.A_target_server]), 
									this._containedObject[ZaCert.A_target_server]) ;
				}
				ZaCertWizard.INSTALL_STATUS = -1;
			}else{
				throw new AjxException(com_zimbra_cert_manager.CERT_INSTALL_STATUS_1, "installCallback", AjxException.UNKNOWN_ERROR) ;
				//throw new AjxException ();
			}
		}
	}catch (ex){
		ZaCertWizard.INSTALL_STATUS = 1;
		statusElement .setContent(ex.msg);
		statusElement .setStyle (DwtAlert.CRITICAL) ;
		if (controller instanceof ZaCertViewController) {
			controller.show(ZaCert.getCerts(ZaApp.getInstance())) ;
		}
		ZaCertWizard.INSTALL_STATUS = -1; //reset hte install_status
		//alert(ex);
		controller.popupErrorDialog(ex.msg, ex, true);
		//_handleException(ex, "ZaCertWizard.prototype.installCallback", null, false);
	}
}

ZaCertWizard.prototype._uploadCallback =
function (status, uploadResults) {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if(window.console && window.console.log) 
		console.log("Cert File Upload: status = " + status);
	if ((status == AjxPost.SC_OK) && (uploadResults != null) && (uploadResults.length > 0)) {
		this.uploadResults = {
			cert: {},
			rootCA: {},
			intermediateCA: []
		}
		
		//validate the uploadResults
		for (var i=0; i < uploadResults.length ; i ++) {
			var v = uploadResults[i] ;
			
			//1. content type validation - ct
      // comment this out as these are coming back as application/octet-stream
			//if (v.ct != "application/x-x509-ca-cert") {
				//ZaApp.getInstance().getCurrentController().popupErrorDialog (
					//com_zimbra_cert_manager.invalidContentType + ": " + v.filename
				//);
				//return ;
			//}
			
			var certType = this.getCertTypeFromUploadInputs(v.filename) ;
			if (certType == "certFile") {
				this.uploadResults.cert = {
					aid: v.aid,
					filename: v.filename
				}
			}else if (certType == "rootCA") {
				this.uploadResults.rootCA = {
					aid: v.aid,
					filename: v.filename
				}
			}else if (certType == "intermediateCA"){
				this.uploadResults.intermediateCA.push ({
					aid: v.aid,
					filename: v.filename
				}); 
			}
		}
		
		//go to the next page, it is always the installation wizard
		this.goPage(ZaCertWizard.STEP_INSTALL_CERT);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);		
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	} else {
		// handle errors during attachment upload.
		var msg = AjxMessageFormat.format(com_zimbra_cert_manager.UploadCertErrorMsg, status);
		ZaApp.getInstance().getCurrentController().popupErrorDialog(msg + com_zimbra_cert_manager.ErrorTryAgain, null, null, true);		
	}	
}

ZaCertWizard.prototype.getCertTypeFromUploadInputs = function (filename) {
	for (var n in this.uploadInputs) {
		var v = this.uploadInputs[n] ;
		if (n == "intermediateCA" && v != null) {
			for (var i=0; i < v.length; i ++)
			if (filename = v[i]) {
				return n ;
			}
		}else{
			if (filename == v) {
				return n ;
			}
		}
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
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if(window.console && window.console.log) 
		console.log("Current Step: " + cStep + ", Now Go Next ...");
	var type ; //type of the self| comm
	if (this._containedObject[ZaCert.A_type_csr] || this._containedObject[ZaCert.A_type_comm] ) {
		type = "comm" ;
	}else if (this._containedObject[ZaCert.A_type_self]) {
		type = "self" ;
	}
	
	var nextStep;
	if (cStep == ZaCertWizard.STEP_SELECT_SERVER) {
        this._localXForm.setInstanceValue( this._containedObject[ZaCert.A_target_server], ZaCert.A_target_server)
		nextStep = ZaCertWizard.STEP_USER_OPTION ;
		this.goPage(nextStep) ;
	}else if (cStep == ZaCertWizard.STEP_USER_OPTION) {
		this._containedObject.initCSR(
			ZaCert.getCSR(ZaApp.getInstance(), this._containedObject[ZaCert.A_target_server], type)) ;
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
	}else if (cStep == ZaCertWizard.STEP_GEN_CSR) {
		//validate the CN and SubjectAltNames
		var cn = this._containedObject.attrs[ZaCert.A_commonName];
		var cn_regEx = /^[A-Za-z0-9\-\_\*]{1,}(\.[A-Za-z0-9\-\_]{1,}){0,}(\.[A-Za-z0-9\-\_]{2,}){1,}$/;
		var san_regEx = cn_regEx;
		if (cn ==null || cn.match(cn_regEx) == null){
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(
					AjxMessageFormat.format(com_zimbra_cert_manager.CERT_CN_INVALID, cn || "Current CN "));
			return false;
		}
		
		var subjAltNames = this._containedObject.attrs[ZaCert.A_subject_alt];
		for (var i=0; i < subjAltNames.length; i ++) {
			if (subjAltNames[i].match(san_regEx) == null){
				ZaApp.getInstance().getCurrentController().popupErrorDialog(	
					AjxMessageFormat.format(com_zimbra_cert_manager.CERT_SUBJ_ALT_NAME_INVALID, subjAltNames[i]||"SubjectAltName " + i));
				return false;
			}
		}
		
		if (this._containedObject[ZaCert.A_type_csr]) {
		 	nextStep = ZaCertWizard.STEP_DOWNLOAD_CSR ;
		} else if (this._containedObject[ZaCert.A_type_self]) {
			nextStep = ZaCertWizard.STEP_INSTALL_CERT ;
		}
		try {
			if ((!this._containedObject[ZaCert.A_csr_exists]) || (this._containedObject[ZaCert.A_force_new_csr] == 'TRUE')){
				if (!this._containedObject[ZaCert.A_type_self]) {
                    ZaCert.genCSR (ZaApp.getInstance(), this._containedObject.attrs, type, true,
                            this._containedObject[ZaCert.A_target_server], this._containedObject[ZaCert.A_keysize]) ;
                } else {
                    if(window.console && window.console.log) console.log("Self-Signed certificate, skip the CSR generation.") ;                    
                }
			}else{
				if(window.console && window.console.log) console.log("Previous CSR exists, skip the CSR generation.") ;
			}
		}catch (ex) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_cert_manager.genCSRError, ex, true) ;		
		}
		this.goPage(nextStep) ;
	}else if (cStep == ZaCertWizard.STEP_UPLOAD_CERT) {
		nextStep = ZaCertWizard.STEP_INSTALL_CERT ;
		if (this._containedObject[ZaCert.A_type_comm]) {
			//1. check if the file name are valid and exists
			//No same file name is allowed due to the server limitation - server only return the filename
			var formEl = document.getElementById(ZaCertWizard.CertUploadFormId);
			var inputEls = formEl.getElementsByTagName("input") ;
			
			this.uploadInputs = {
				certFile : null ,
				rootCA : null ,
				intermediateCA: [] 
			};
			
			var filenameArr = [];
			for (var i=0; i < inputEls.length; i++){				
				if (inputEls[i].type == "file") {
					var n = inputEls[i].name ;
					var v = ZaCertWizard.getFileName(inputEls[i].value) ;
					if (v != null && v.length != 0) {
						if (ZaUtil.findValueInArray(filenameArr, v) != -1) {
							ZaApp.getInstance().getCurrentController().popupErrorDialog (
								com_zimbra_cert_manager.dupFileNameError + v
							);
							return ;
						}
						filenameArr.push (v);
					}
					
					if ( n == "certFile") {
						if (v == null ||  v.length == 0) {
							ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_cert_manager.noCertFileError);
							return ;
						}else{
							this.uploadInputs["certFile"] = v ;
						}
					}else if (n == "rootCA") {
						if (v == null || v.length == 0 ) {
							ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_cert_manager.noRootCAError);
							return ;
						}else{
							this.uploadInputs["rootCA"] = v ;							
						}
					}else if (inputEls[i].name == "intermediateCA") {
						if (v != null && v.length != 0) {
							this.uploadInputs["intermediateCA"].push (v);
						}
					}
				}
			}
			
			//2. Upload the files
			DBG.println("Start uploading the file");
			this.setUploadManager(new AjxPost(this.getUploadFrameId()));
			var certUploadCallback = new AjxCallback(this, this._uploadCallback);
			var um = this.getUploadManager() ; 
			window._uploadManager = um;
			try {
				um.execute(certUploadCallback, document.getElementById (ZaCertWizard.CertUploadFormId));
				return ; //allow the callback to handle the wizard buttons
			}catch (err) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_cert_manager.certFileNameError) ;
				return ;
			}			
		}else if (this._containedObject[ZaCert.A_type_self]) {
			this.goPage(nextStep) ;
		}else {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_cert_manager.certTypeError) ;	
		}
	}else if (cStep == ZaCertWizard.STEP_CSR_CONFIRM) {
		nextStep = ZaCertWizard.STEP_UPLOAD_CERT;
		this.goPage(nextStep) ;
	}
	
	if (nextStep == ZaCertWizard.STEP_INSTALL_CERT || nextStep == ZaCertWizard.STEP_DOWNLOAD_CSR) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	}
	
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
}

//TODO: move it to ZaUtil
ZaCertWizard.getFileName = function (fullPath) {
		if (fullPath == null) return null ;
		
		var lastIndex = 0;
		if (AjxEnv.isWindows) {
			lastIndex = fullPath.lastIndexOf("\\") ;
		}else{
			lastIndex = fullPath.lastIndexOf("/") ;			
		}

		return fullPath.substring(lastIndex + 1) ;
}

ZaCertWizard.prototype.goPrev = 
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if(window.console && window.console.log) 
		console.log("Current Step: " + cStep + ", Now Go Previous ...");
	var prevStep ;
	if (cStep == ZaCertWizard.STEP_DOWNLOAD_CSR) {
		prevStep = ZaCertWizard.STEP_GEN_CSR ;
	}else if (cStep == ZaCertWizard.STEP_GEN_CSR
		|| cStep == ZaCertWizard.STEP_CSR_CONFIRM) {		
		prevStep = ZaCertWizard.STEP_USER_OPTION ;
	}else if (cStep == ZaCertWizard.STEP_UPLOAD_CERT ) {
		prevStep = ZaCertWizard.STEP_CSR_CONFIRM ;
	}else if ( cStep == ZaCertWizard.STEP_INSTALL_CERT ){
		if (this._containedObject[ZaCert.A_type_comm]) {
			prevStep = ZaCertWizard.STEP_UPLOAD_CERT ;
		}else if (this._containedObject[ZaCert.A_type_self]) {
			prevStep = ZaCertWizard.STEP_GEN_CSR ;
		}
	}else if (cStep == ZaCertWizard.STEP_USER_OPTION) {
		prevStep = ZaCertWizard.STEP_SELECT_SERVER ;
	}
	
	if (prevStep == ZaCertWizard.STEP_SELECT_SERVER) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	}
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	
	this.goPage(prevStep);
}

/**
* @method setObject sets the object contained in the view
* @param entry -  object to display
**/
ZaCertWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();	
	this._containedObject = entry ;
	
	this._containedObject[ZaModel.currentStep] = ZaCertWizard.STEP_SELECT_SERVER;
    if (this._containedObject [ZaCert.A_keysize] == null) {
        this._containedObject [ZaCert.A_keysize] = "2048" ;    
    }
	this._localXForm.setInstance(this._containedObject);
}

//ZaCertWizard.CertUploadAttachmentInputId = Dwt.getNextId();
ZaCertWizard.CertUploadFormId = Dwt.getNextId();
ZaCertWizard.addIntermediateCADivId = Dwt.getNextId ();

ZaCertWizard.getUploadFormHtml =
function (){
	//var uri = location.protocol + "//" + document.domain + appContextPath 
	//							+ "/../service/upload";
	//need the full content of the response.
	//200,'1',[{"filename":"zimbra.crt","aid":"0466544c-1372-4cc3-ad8e-b1ff570dccca:85f82c13-6381-4c84-8915-bfbe515fdbd4","ct":"application/x-x509-ca-cert"},{"filename":"mycert.crt","aid":"0466544c-1372-4cc3-ad8e-b1ff570dccca:2321870e-1229-4359-a9c7-f11222a50042","ct":"application/x-x509-ca-cert"}]
	
	var uri = appContextPath + "/../service/upload?fmt=extended";
	var html = [];
	var idx = 0;
	html[idx++] = "<div style='overflow:auto'><form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaCertWizard.CertUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'>" ; 
	html[idx++] = "<div><table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;
	html[idx++] = "<colgroup><col width=100/><col width='*' /><col width=50 /></colgroup>";
	
	html[idx++] = "<tbody><tr><td>" + com_zimbra_cert_manager.CERT_upload_comm_cert + "</td>";
	html[idx++] = "<td><input type=file  name='certFile' size='40'></input></td><td></td></tr>";

	html[idx++] = "<tr><td>" + com_zimbra_cert_manager.CERT_upload_root_CA + "</td>";
	html[idx++] = "<td><input type=file  name='rootCA' size='40'></input></td><td></td></tr>";

//	html[idx++] = "<tr>" + ZaCertWizard.getIntermediaCAUploadInput() + "</tr>";

	html[idx++] = "</tbody></table></div>";
	
	//add intermediat El
	html[idx++] = "<div>" + ZaCertWizard.getIntermediaCAUploadInput() +"</div>";
	
	html[idx++] = "<div id='" + ZaCertWizard.addIntermediateCADivId  + "' width='100%' align='center'>" ;
	html[idx++] = "<span style='color: blue; text-decoration: underline; cursor: default;padding-left: 3px;' " +
					" onmouseout='this.style.cursor=\"default\"' " +
					" onmouseover='this.style.cursor=\"pointer\"'" +
				    " onclick='ZaCertWizard.addIntermediateCAInput(this);' >" +
				    com_zimbra_cert_manager.ADD_IntermediateCA_Label + "</span>";
	html[idx++] = "</div>" ;	
			    
	html[idx++] = "</form></div>";
	
	return html.join("");
}

ZaCertWizard.getIntermediaCAUploadInput = function () {
	var html = [];
	var idx = 0;
	html[idx++] = "<table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;
	html[idx++] = "<colgroup><col width=100/><col width='*' /><col width=50 /></colgroup>";
	
	html[idx++] = "<tbody><tr>" ;
	html[idx++] = "<td>" + com_zimbra_cert_manager.CERT_upload_intermediate_ca + "</td>";
	html[idx++] = "<td><input type=file  name='intermediateCA' size='40'></input></td>"; 
	html[idx++] = "<td><span style='padding-left:5px; color: blue; text-decoration: underline; cursor: default;' " +
					" onmouseout='this.style.cursor=\"default\"' " +
					" onmouseover='this.style.cursor=\"pointer\"'" +
				    " onclick='ZaCertWizard.removeIntermediaCAInput(this);' >" +
				    com_zimbra_cert_manager.Remove_IntermediateCA_Label + "</span></td>";
	html[idx++] = "</tr></tbody></table>";
	return html.join("");	
} 

ZaCertWizard.addIntermediateCAInput = function (addSpanEl) {
	var formEl = document.getElementById (ZaCertWizard.CertUploadFormId) ;
	var newNode = document.createElement("div");
	newNode.innerHTML = ZaCertWizard.getIntermediaCAUploadInput ();
	formEl.insertBefore(newNode, document.getElementById(ZaCertWizard.addIntermediateCADivId));
}

ZaCertWizard.removeIntermediaCAInput = function (removeSpanEl) {
	var formEl = document.getElementById (ZaCertWizard.CertUploadFormId) ;
	var rowEl = removeSpanEl.parentNode.parentNode ;
	var intermediaCADivEl = rowEl.parentNode.parentNode.parentNode ;
	formEl.removeChild(intermediaCADivEl) ;	
}


ZaCertWizard.onRepeatRemove = 
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
}

ZaCertWizard.myXFormModifier = function(xFormObject) {		
	var cases = new Array();
	
	var case_select_server = {
			type:_CASE_, numCols:2, colSizes:["100px","*"],
            tabGroupKey:ZaCertWizard.STEP_SELECT_SERVER, caseKey:ZaCertWizard.STEP_SELECT_SERVER,
			align:_LEFT_, valign:_TOP_, cssStyle:"padding-left:50px;"
		};
	
	var case_select_server_items = [
				{ type:_SPACER_, height: 10},
				{ type:_OUTPUT_, colSpan: 2, value: com_zimbra_cert_manager.CERT_select_server},
				{ type:_SPACER_, height: 10},
				{ ref:ZaCert.A_target_server, type:_OSELECT1_,
					label:com_zimbra_cert_manager.CERT_server_name, 
					labelLocation:_LEFT_, 
					choices:ZaCert.TARGET_SERVER_CHOICES,
                    visibilityChecks:[],
                    enableDisableChecks:[],
                    editable: true
				}
				/*
				,
				{ type:_CHECKBOX_, ref: ZaCert.A_target_all_servers  , label: com_zimbra_cert_manager.all_servers}
		*/
		];
	case_select_server.items = case_select_server_items;
	cases.push(case_select_server);
		
	var case_user_options = {type:_CASE_, numCols:2, colSizes:["25px","*"], 
            tabGroupKey:ZaCertWizard.STEP_USER_OPTION, caseKey:ZaCertWizard.STEP_USER_OPTION,
			align:_LEFT_, valign:_TOP_, cssStyle:"padding-left:50px;"};
			
	var case_user_options_items = [
				{	type: _GROUP_, numCols:2, colSpan: "*", colSizes:["75px","*"], items: [
						{ type:_SPACER_, height: 10},
						{ type:_OUTPUT_ , ref: ZaCert.A_target_server, 
							labelLocation:_LEFT_ , labelCssStyle: "text-align: left" ,
                            bmolsnr: true,
                            getDisplayValue : function () {
                                for (var i=0; i <ZaCert.TARGET_SERVER_CHOICES.length; i++ ) {
                                 if (ZaCert.TARGET_SERVER_CHOICES[i].value == this.getInstanceValue ()){
                                     return ZaCert.TARGET_SERVER_CHOICES[i].label ; 
                                 }
                                }
                            },
							label: com_zimbra_cert_manager.lb_target_server, choices:ZaCert.TARGET_SERVER_CHOICES}
					]
				},
				{ type:_SPACER_, height: 10},
				{ type:_OUTPUT_, colSpan: 2, value: com_zimbra_cert_manager.CERT_select_option},
				{ type:_SPACER_, height: 10},
				{ type:_RADIO_,   groupname: "install_type", ref: ZaCert.A_type_self,
					label:  com_zimbra_cert_manager.CERT_self_signed , enableLabelFor: true,
                    visibilityChecks:[],
                    enableDisableChecks:[],
                        labelLocation:_RIGHT_ , align: _LEFT_ ,
						//TODO: Change it on the XFormItem level
						onChange: function (value, event, form) {
							value = true ;
							if(window.console && window.console.log) console.log("Self Install - onChange value = " + value) ;
							this.setInstanceValue (value) ;
							this.setInstanceValue (!value, ZaCert.A_type_comm ) ;
							this.setInstanceValue (!value, ZaCert.A_type_csr) ;
						},
						updateElement:function (newValue) {
							if(window.console && window.console.log) console.log("Self Install - UpdateElement newValue = " + newValue) ;
							this.getElement().checked = (newValue == true);
						}
					},
				{ type:_RADIO_,  groupname: "install_type", ref: ZaCert.A_type_csr,
					label: com_zimbra_cert_manager.CERT_gen_csr, enableLabelFor: true,
                    visibilityChecks:[],
                    enableDisableChecks:[],
                    labelLocation:_RIGHT_ , align: _LEFT_ ,
						//TODO: Change it on the XFormItem level
						onChange: function (value, event, form) {
							value = true ;
							if(window.console && window.console.log) console.log("Gen CSR - onChange value = " + value) ;
							this.setInstanceValue (value) ;
							this.setInstanceValue (!value, ZaCert.A_type_self ) ;
							this.setInstanceValue (!value, ZaCert.A_type_comm ) ;
						},
						updateElement:function (newValue) {
							if(window.console && window.console.log) console.log("Gen CSR Install - UpdateElement newValue = " + newValue) ;
							this.getElement().checked = (newValue == true);
						}
					},	
				{ type:_RADIO_,  groupname: "install_type", ref: ZaCert.A_type_comm,
                    visibilityChecks:[],
                    enableDisableChecks:[],
                    updateElement:function (newValue) {
						if(window.console && window.console.log) console.log("Comm Install - UpdateElement newValue = " + newValue) ;
						this.getElement().checked = (newValue == true);
					},
					onChange: function (value, event, form) {
						value = true ;
						if(window.console && window.console.log) console.log("Comm Install - onChange value = " + value) ;
						this.setInstanceValue (value) ;
						this.setInstanceValue (!value, ZaCert.A_type_self ) ;
						this.setInstanceValue (!value, ZaCert.A_type_csr) ;
					},
					label: com_zimbra_cert_manager.CERT_comm_signed, enableLabelFor: true,
					labelLocation:_RIGHT_ , align: _LEFT_} ,
                 { type:_SPACER_, height: 10}
    ];
	case_user_options.items = case_user_options_items;
	cases.push(case_user_options);

	var case_gen_csr = {type:_CASE_, numCols:2, colSizes:["200px","*"], 
		tabGroupKey:ZaCertWizard.STEP_GEN_CSR, caseKey:ZaCertWizard.STEP_GEN_CSR,
		align:_LEFT_, valign:_TOP_};
		
	var case_gen_csr_items = [
		{type: _DWT_ALERT_, colSpan:2,
                visibilityChecks:["instance[ZaCert.A_csr_exists] == true "],
                containerCssStyle: "width:400px;",
				style: DwtAlert.WARNING, iconVisible: false,
				content: com_zimbra_cert_manager.CSR_EXISTS_WARNING 
		 }, 
		{type: _GROUP_ , colSpan:2, numCols: 2, colSizes:["150px","300px"],
			  items :[
				{	type: _GROUP_, numCols:2, colSpan: "*", colSizes:["150px","*"], items: [
						{ type:_SPACER_, height: 10},
						{ type:_OUTPUT_ , ref: ZaCert.A_target_server, 
							labelLocation:_LEFT_ ,
                             bmolsnr: true,
                            getDisplayValue : function () {
                                for (var i=0; i <ZaCert.TARGET_SERVER_CHOICES.length; i++ ) {
                                 if (ZaCert.TARGET_SERVER_CHOICES[i].value == this.getInstanceValue ()){
                                     return ZaCert.TARGET_SERVER_CHOICES[i].label ;
                                 }
                                }
                            },
							label: "Target Server: ", choices:ZaCert.TARGET_SERVER_CHOICES}
					]
				},
				{ref: ZaCert.A_force_new_csr, type: _CHECKBOX_ , label: com_zimbra_cert_manager.FORCE_NEW_CSR , 
					visibilityChecks:[" instance[ZaCert.A_csr_exists] == true "],
                    enableDisableChecks:[" instance[ZaCert.A_csr_exists] == true "],
                    enableDisableChangeEventSources:[ZaCert.A_csr_exists],
                    onChange: function (value, event, form) {
						this.setInstanceValue (value) ;
						form.parent._containedObject.modifySubjectAltNames();
						form.refresh();
					},
					trueValue:"TRUE", falseValue:"FALSE", msgName:com_zimbra_cert_manager.FORCE_NEW_CSR },

                  {ref: ZaCert.A_keysize,type:_OSELECT1_,
					label:com_zimbra_cert_manager.CERT_keysize, 
					labelLocation:_LEFT_,
					choices:ZaCert.KEY_SIZE_CHOICES,
                    visibilityChecks:[],
                    enableDisableChecks:[ZaCertWizard.isCSRFieldsEnabled],
				    enableDisableChangeEventSources:[ZaCert.A_csr_exists, ZaCert.A_force_new_csr] },
				{ ref: ZaCert.A_commonName, type:_TEXTFIELD_, width: 150,
					visibilityChecks:[],  bmolsnr:true,
                    enableDisableChecks:[ZaCertWizard.isCSRFieldsEnabled],
				    enableDisableChangeEventSources:[ZaCert.A_csr_exists, ZaCert.A_force_new_csr],
                    label: com_zimbra_cert_manager.CERT_INFO_CN},
				{ ref: ZaCert.A_use_wildcard_server_name, type:_CHECKBOX_, 
						visibilityChecks:[],
                        enableDisableChecks:[ZaCertWizard.isCSRFieldsEnabled],
				        enableDisableChangeEventSources:[ZaCert.A_csr_exists, ZaCert.A_force_new_csr],
                        label: com_zimbra_cert_manager.Use_Wildcard_Server_Name,
						onChange: function (value, event, form) {
							if(window.console && window.console.log) console.log("use wildcard: " + value) ;
							this.setInstanceValue (value) ;
							if (value) {
								if(window.console && window.console.log) console.log("Set the wildcard server name") ;
                                var wildCardSN = ZaCert.getWildCardServerName(this.getInstanceValue(ZaCert.A_commonName)) ;
								this.setInstanceValue( wildCardSN,	ZaCert.A_commonName ) ;
							}
 						}
				},	
				{ ref: ZaCert.A_countryName, type:_TEXTFIELD_, width: 150, 
                    visibilityChecks:[],
                    enableDisableChecks:[ZaCertWizard.isCSRFieldsEnabled],
                    enableDisableChangeEventSources:[ZaCert.A_csr_exists, ZaCert.A_force_new_csr],
                    label: com_zimbra_cert_manager.CERT_INFO_C},
				{ ref: ZaCert.A_state, type:_TEXTFIELD_, width: 150, 
                    visibilityChecks:[],
                    enableDisableChecks:[ZaCertWizard.isCSRFieldsEnabled],
                    enableDisableChangeEventSources:[ZaCert.A_csr_exists, ZaCert.A_force_new_csr],
					label: com_zimbra_cert_manager.CERT_INFO_ST},
				{ ref: ZaCert.A_city, type:_TEXTFIELD_, width: 150, 
                    visibilityChecks:[],
                    enableDisableChecks:[ZaCertWizard.isCSRFieldsEnabled],
                    enableDisableChangeEventSources:[ZaCert.A_csr_exists, ZaCert.A_force_new_csr],
					label: com_zimbra_cert_manager.CERT_INFO_L},
				{ ref: ZaCert.A_organization, type:_TEXTFIELD_, width: 150,
                    visibilityChecks:[],
                    enableDisableChecks:[ZaCertWizard.isCSRFieldsEnabled],
                    enableDisableChangeEventSources:[ZaCert.A_csr_exists, ZaCert.A_force_new_csr],
					label: com_zimbra_cert_manager.CERT_INFO_O},
				{ ref: ZaCert.A_organizationUnit, type:_TEXTFIELD_, width: 150,
                    visibilityChecks:[],
                    enableDisableChecks:[ZaCertWizard.isCSRFieldsEnabled],
                    enableDisableChangeEventSources:[ZaCert.A_csr_exists, ZaCert.A_force_new_csr],
					label: com_zimbra_cert_manager.CERT_INFO_OU},
				 { ref: ZaCert.A_subject_alt,
                     visibilityChecks:[],
                     enableDisableChecks:[ZaCertWizard.isCSRFieldsEnabled],
                     enableDisableChangeEventSources:[ZaCert.A_csr_exists, ZaCert.A_force_new_csr],
					type:_REPEAT_,
					label:com_zimbra_cert_manager.CERT_INFO_SubjectAltName,
					labelLocation:_LEFT_, 
					labelCssStyle:"vertical-align: top; padding-top: 3px;",
					addButtonLabel:com_zimbra_cert_manager.NAD_Add, 
					align:_LEFT_,
					repeatInstance:"", 
					showAddButton:true, 
					showRemoveButton:true,
                    addButtonWidth: 50,
                    removeButtonWidth: 50,
                    //showAddOnNextRow:true,
					alwaysShowAddButton:true,
					removeButtonLabel:com_zimbra_cert_manager.NAD_Remove,								
					items: [
						{ref:".", type:_TEXTFIELD_, 
                        visibilityChecks:[],
                        enableDisableChecks:[ZaCertWizard.isCSRFieldsEnabled],
                        enableDisableChangeEventSources:[ZaCert.A_csr_exists, ZaCert.A_force_new_csr],
                        onChange:function (value, event, form) {
							this.setInstanceValue(value);
						},
						width:"150px"}
					]//,
					//onRemove:ZaCertWizard.onRepeatRemove
				}			
			]
		},
		
		{ type:_SPACER_ , height: 10 },
		{ type: _GROUP_, colSpan: "*", items: [
		 		{type: _OUTPUT_, value: com_zimbra_cert_manager.CERT_SubjectAlt_Note }
		 	]
		 }	
	];	
	
	case_gen_csr.items = case_gen_csr_items;
	cases.push(case_gen_csr);

	var case_upload_cert={type:_CASE_, numCols:1, colSizes:["450px"],
					tabGroupKey:ZaCertWizard.STEP_UPLOAD_CERT, caseKey:ZaCertWizard.STEP_UPLOAD_CERT,
                    items: [
						{	type: _GROUP_, numCols:2, colSpan: "*", colSizes:["200px","*"], items: [
								{ type:_SPACER_, height: 10},
								{ type:_OUTPUT_ , ref: ZaCert.A_target_server, 
									labelLocation:_LEFT_ , 
									bmolsnr: true,
                                    getDisplayValue : function () {
                                        for (var i=0; i <ZaCert.TARGET_SERVER_CHOICES.length; i++ ) {
                                         if (ZaCert.TARGET_SERVER_CHOICES[i].value == this.getInstanceValue ()){
                                             return ZaCert.TARGET_SERVER_CHOICES[i].label ;
                                         }
                                        }
                                    },
                                    label: com_zimbra_cert_manager.lb_target_server,
                                    choices:ZaCert.TARGET_SERVER_CHOICES}
							]
						},
						{ type:_SPACER_ , height: 10 },
						{ type:_GROUP_, id: "CertUpload", 
                            visibilityChecks:["instance[ZaCert.A_type_comm] == true"],
                            colSpan: 2, numCols: 1, colSizes: "*", items : [
								{ type:_OUTPUT_, value: com_zimbra_cert_manager.CERT_uploadTitle, align: _LEFT_},
								{ type:_OUTPUT_, value: ZaCertWizard.getUploadFormHtml() } ,
								{ type:_SPACER_ , height: 10 }
							]
						},
						{ type:_SPACER_ , height: 10 }
						
					]
				};
	cases.push(case_upload_cert);
	
	var case_install_cert = {type:_CASE_, numCols:2, colSizes:["150px", "300px"],
                    tabGroupKey:ZaCertWizard.STEP_INSTALL_CERT, caseKey:ZaCertWizard.STEP_INSTALL_CERT,
					align:_LEFT_, valign:_TOP_};
	var case_install_certItems = [
			{ type:_OUTPUT_ , ref: ZaCert.A_target_server, 
				labelLocation:_LEFT_ , 
				bmolsnr: true,
                getDisplayValue : function () {
                    for (var i=0; i <ZaCert.TARGET_SERVER_CHOICES.length; i++ ) {
                     if (ZaCert.TARGET_SERVER_CHOICES[i].value == this.getInstanceValue ()){
                         return ZaCert.TARGET_SERVER_CHOICES[i].label ;
                     }
                    }
                },
                label: com_zimbra_cert_manager.lb_target_server,
                choices:ZaCert.TARGET_SERVER_CHOICES},
			{ type:_SPACER_, height: 10},	
			{type:_OUTPUT_, colSpan: 2, 
                visibilityChecks:["instance[ZaCert.A_type_self] == true"],
                value: com_zimbra_cert_manager.CERT_installTitle },
			{ type:_SPACER_ , height: 10 },
			{type:_TEXTFIELD_, ref: ZaCert.A_validation_days ,			
				//Validation_days is not required for comm install
				visibilityChecks:["instance[ZaCert.A_type_self] == true"],
                label: com_zimbra_cert_manager.CERT_validate_days
			}
		];	
	
	case_install_cert.items = case_install_certItems;
	cases.push(case_install_cert);
	
	var case_download_csr = 
		{type:_CASE_, numCols:1, colSizes:["*"],
            tabGroupKey:ZaCertWizard.STEP_DOWNLOAD_CSR, caseKey:ZaCertWizard.STEP_DOWNLOAD_CSR,
			align:_LEFT_, valign:_TOP_ , 
			items :[
				{	type: _GROUP_, numCols:2, colSpan: "*", colSizes:["75px","*"], items: [
						{ type:_SPACER_, height: 10},
						{ type:_OUTPUT_ , ref: ZaCert.A_target_server, 
							labelLocation:_LEFT_ , labelCssStyle: "text-align: left" , 
							bmolsnr: true,
                            getDisplayValue : function () {
                                for (var i=0; i <ZaCert.TARGET_SERVER_CHOICES.length; i++ ) {
                                 if (ZaCert.TARGET_SERVER_CHOICES[i].value == this.getInstanceValue ()){
                                     return ZaCert.TARGET_SERVER_CHOICES[i].label ;
                                 }
                                }
                            },
							label: com_zimbra_cert_manager.lb_target_server,
                            choices:ZaCert.TARGET_SERVER_CHOICES}
					]
				},
				{ type:_OUTPUT_, value: com_zimbra_cert_manager.CSR_download_msg_1 },
				{ type:_SPACER_ , height: 10 },
				{ type:_OUTPUT_, value:"<a href='adminres?action=getCSR' onclick='ZaZimbraAdmin.unloadHackCallback();'> "
										+ com_zimbra_cert_manager.CSR_download_msg_2 + "</a> "},
				{ type:_SPACER_ , height: 10 }
			]
		}		
	
	cases.push (case_download_csr) ;
	
	var case_csr_confirm = 		
		{type:_CASE_, numCols:1, colSizes:["*"],
            tabGroupKey:ZaCertWizard.STEP_CSR_CONFIRM, caseKey:ZaCertWizard.STEP_CSR_CONFIRM,
			align:_LEFT_, valign:_TOP_ , 
			items :[
				{type: _GROUP_ , colSpan:2, numCols: 2, colSizes:["200px","350px"],
				visibilityChecks:["instance[ZaCert.A_csr_exists] == true"],
                items :[
						{	type: _GROUP_, numCols:2, colSpan: "*", colSizes:["200px","*"], items: [
								{ type:_SPACER_, height: 10},
								{ type:_OUTPUT_ , ref: ZaCert.A_target_server, 
									labelLocation:_LEFT_ , labelCssStyle: "text-align: left" , 
									bmolsnr: true,
                                    getDisplayValue : function () {
                                        for (var i=0; i <ZaCert.TARGET_SERVER_CHOICES.length; i++ ) {
                                         if (ZaCert.TARGET_SERVER_CHOICES[i].value == this.getInstanceValue ()){
                                             return ZaCert.TARGET_SERVER_CHOICES[i].label ;
                                         }
                                        }
                                    },
                                    label: com_zimbra_cert_manager.lb_target_server,
                                    choices:ZaCert.TARGET_SERVER_CHOICES}
							]
						},
						{ type: _OUTPUT_, value: com_zimbra_cert_manager.CSR_REVIEW, colSpan: 2 },
						{ type:_SPACER_, height:10},
						{ ref: ZaCert.A_commonName, type:_OUTPUT_, width: 150, 
							label: com_zimbra_cert_manager.CERT_INFO_CN},
						{ ref: ZaCert.A_countryName, type:_OUTPUT_, width: 150, 
							label: com_zimbra_cert_manager.CERT_INFO_C},
						{ ref: ZaCert.A_state, type:_OUTPUT_, width: 150, 
							label: com_zimbra_cert_manager.CERT_INFO_ST},
						{ ref: ZaCert.A_city, type:_OUTPUT_, width: 150, 
							label: com_zimbra_cert_manager.CERT_INFO_L},
						{ ref: ZaCert.A_organization, type:_OUTPUT_, width: 150, 
							label: com_zimbra_cert_manager.CERT_INFO_O},
						{ ref: ZaCert.A_organizationUnit, type:_OUTPUT_, width: 150, 
							label: com_zimbra_cert_manager.CERT_INFO_OU},
						{ ref: ZaCert.A_subject_alt, 
							type:_REPEAT_,
							label:com_zimbra_cert_manager.CERT_INFO_SubjectAltName,
							labelLocation:_LEFT_, 
							labelCssStyle:"vertical-align: top;",
							align:_LEFT_,
							repeatInstance:"", 
							showAddButton:false, 
							showRemoveButton:false,             
                            items: [
								{ref:".", type:_OUTPUT_, width:"150px"}
							]
						}	
					]
				},
				{type: _GROUP_ , colSpan:2, numCols: 1, colSizes:["400px"],
					visibilityChecks:["!instance[ZaCert.A_csr_exists] "],
                    cssStyle:"padding-left:50px;", items: [
						{type: _DWT_ALERT_, colSpan:2,
                            visibilityChecks:["!instance[ZaCert.A_csr_exists] "],
                            containerCssStyle: "width:400px;",
							style: DwtAlert.WARNING, iconVisible: false,
							content: com_zimbra_cert_manager.CSR_NON_EXISTS_WARNING 
				 		},
				 		{type:_SPACER_, height:10},
				 		{type: _OUTPUT_, value: com_zimbra_cert_manager.CSR_NON_EXISTS_MSG }
			 		]
				}
			]
		}
	
		cases.push (case_csr_confirm) ;

    var contentW = 450 ;
    if (AjxEnv.isIE) {
        contentW = 500 ;
    }
    xFormObject.items = [
			{type:_OUTPUT_, colSpan:2, align:_CENTER_, valign:_TOP_, ref:ZaModel.currentStep,
                choices:this.stepChoices, valueChangeEventSources:[ZaModel.currentStep]},
			{type:_SEPARATOR_, align:_CENTER_, valign:_TOP_},
			{type:_SPACER_,  align:_CENTER_, valign:_TOP_},
			{type:_SWITCH_, width:contentW, align:_LEFT_, valign:_TOP_, items:cases}
		];
};
ZaXDialog.XFormModifiers["ZaCertWizard"].push(ZaCertWizard.myXFormModifier);

ZaCertWizard.isCSRFieldsEnabled = function () {
    var isCSRExist = this.getInstanceValue(ZaCert.A_csr_exists)  ;
    var isForceNewCSR = this.getInstanceValue(ZaCert.A_force_new_csr)  ;

	return ( (!isCSRExist) || (isForceNewCSR == "TRUE")) ;
}
   
