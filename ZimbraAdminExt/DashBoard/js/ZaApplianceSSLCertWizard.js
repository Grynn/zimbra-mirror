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
//ZaApplianceSSLCertWizard
function ZaApplianceSSLCertWizard (parent) {
    var w = "500px" ;
    if (AjxEnv.isIE) {
        w = "550px" ;
    }
    ZaXWizardDialog.call(this, parent, null, com_zimbra_dashboard.CERT_WIZARD_title, w, "300px","ZaApplianceSSLCertWizard");

	this.stepChoices = [
		{label:com_zimbra_dashboard.CERT_WIZARD_TABT_useroption, value:ZaApplianceSSLCertWizard.STEP_USER_OPTION},
		{label:com_zimbra_dashboard.CERT_WIZARD_TABT_gencsr, value:ZaApplianceSSLCertWizard.STEP_GEN_CSR},
		{label:com_zimbra_dashboard.CERT_WIZARD_TABT_uploadCert, value:ZaApplianceSSLCertWizard.STEP_UPLOAD_CERT},
		{label:com_zimbra_dashboard.CERT_WIZARD_TABT_installCert, value:ZaApplianceSSLCertWizard.STEP_INSTALL_CERT},
		{label:com_zimbra_dashboard.CERT_WIZARD_TABT_downloadCSR, value:ZaApplianceSSLCertWizard.STEP_DOWNLOAD_CSR},
		{label:com_zimbra_dashboard.CERT_WIZARD_TABT_reviewCSR, value:ZaApplianceSSLCertWizard.STEP_CSR_CONFIRM}		
	];
		
	//this._lastStep = this.stepChoices.length;
	this.uploadInputs = {} ;
	this.uploadResults = null ;
	//this.initForm(null,this.getMyXForm());	
	this.initForm(ZaApplianceSSLCert.myXModel,this.getMyXForm());	
   
	this._localXForm.setController(ZaApp.getInstance());	
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaApplianceSSLCertWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaApplianceSSLCertWizard.prototype.handleXFormChange));	
	this._helpURL = ZaApplianceSSLCertWizard.helpURL;
}

// -1 : No status, 0: Install succeed, >0 : Install Failed (different number is different error)
ZaApplianceSSLCertWizard.INSTALL_STATUS = -1;
ZaApplianceSSLCertWizard.STEP_INDEX = 1 ;
ZaApplianceSSLCertWizard.STEP_USER_OPTION = ZaApplianceSSLCertWizard.STEP_INDEX ++ ;
ZaApplianceSSLCertWizard.STEP_GEN_CSR = ZaApplianceSSLCertWizard.STEP_INDEX ++ ;
ZaApplianceSSLCertWizard.STEP_UPLOAD_CERT = ZaApplianceSSLCertWizard.STEP_INDEX ++ ;
ZaApplianceSSLCertWizard.STEP_INSTALL_CERT = ZaApplianceSSLCertWizard.STEP_INDEX ++ ;
ZaApplianceSSLCertWizard.STEP_DOWNLOAD_CSR = ZaApplianceSSLCertWizard.STEP_INDEX ++ ;
ZaApplianceSSLCertWizard.STEP_CSR_CONFIRM = ZaApplianceSSLCertWizard.STEP_INDEX ++ ;

ZaApplianceSSLCertWizard.prototype = new ZaXWizardDialog;
ZaApplianceSSLCertWizard.prototype.constructor = ZaApplianceSSLCertWizard;
ZaXDialog.XFormModifiers["ZaApplianceSSLCertWizard"] = new Array();
ZaApplianceSSLCertWizard.helpURL = location.pathname + "help/admin/html/tools/installing_certificates.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
ZaApplianceSSLCertWizard.prototype.handleXFormChange = 
function () {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if(this._localXForm.hasErrors()) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		if (cStep == ZaApplianceSSLCertWizard.STEP_INSTALL_CERT ) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		}
		
		if (cStep == ZaApplianceSSLCertWizard.STEP_DOWNLOAD_CSR ) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(AjxMsg._finish);
		}
		
		if (this._containedObject[ZaApplianceSSLCert.A_type_csr]) {
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(AjxMsg._finish);
		}else{
			this._button[DwtWizardDialog.FINISH_BUTTON].setText(com_zimbra_dashboard.CERT_INSTALL_BUTTON_text);			
		}
	}
}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaApplianceSSLCertWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);		
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

ZaApplianceSSLCertWizard.prototype._uploadCallback =
function (status, uploadResults) {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	if ((status == AjxPost.SC_OK) && (uploadResults != null) && (uploadResults.length > 0)) {
		this.uploadResults = {
			cert: {},
			rootCA: {},
			intermediateCA: []
		}
		
		//validate the uploadResults
		for (var i=0; i < uploadResults.length ; i ++) {
			var v = uploadResults[i] ;
			
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
		this.goPage(ZaApplianceSSLCertWizard.STEP_INSTALL_CERT);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);		
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	} else {
		// handle errors during attachment upload.
		var msg = AjxMessageFormat.format(com_zimbra_dashboard.UploadCertErrorMsg, status);
		ZaApp.getInstance().getCurrentController().popupErrorDialog(msg + com_zimbra_dashboard.ErrorTryAgain, null, null, true);		
	}	
}

ZaApplianceSSLCertWizard.prototype.getCertTypeFromUploadInputs = function (filename) {
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
ZaApplianceSSLCertWizard.prototype.getUploadFrameId =
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

ZaApplianceSSLCertWizard.prototype.getUploadManager = 
function() { 
	return this._uploadManager;
};

/**
 * @params uploadManager is the AjxPost object
 */
ZaApplianceSSLCertWizard.prototype.setUploadManager = 
function(uploadManager) {
	this._uploadManager = uploadManager;
};


ZaApplianceSSLCertWizard.prototype.goNext = 
function() {
	var cStep = this._containedObject[ZaModel.currentStep] ;
	var type ; //type of the self| comm
	if (this._containedObject[ZaApplianceSSLCert.A_type_csr] || this._containedObject[ZaApplianceSSLCert.A_type_comm] ) {
		type = "comm" ;
	}else if (this._containedObject[ZaApplianceSSLCert.A_type_self]) {
		type = "self" ;
	}
	
	var nextStep;
	if (cStep == ZaApplianceSSLCertWizard.STEP_USER_OPTION) {
		this._containedObject.initCSR(ZaApplianceSSLCert.getCSR(ZaApp.getInstance(), this._containedObject[ZaApplianceSSLCert.A_target_server], type)) ;
		if (this._containedObject[ZaApplianceSSLCert.A_type_csr] 
			|| this._containedObject[ZaApplianceSSLCert.A_type_self]) {
		 	nextStep = ZaApplianceSSLCertWizard.STEP_GEN_CSR ;
		} else if (this._containedObject[ZaApplianceSSLCert.A_type_comm]) {
			nextStep = ZaApplianceSSLCertWizard.STEP_CSR_CONFIRM ;
			if (!this._containedObject[ZaApplianceSSLCert.A_csr_exists]) {
				this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			}
		}
		this.goPage(nextStep) ;
	}else if (cStep == ZaApplianceSSLCertWizard.STEP_GEN_CSR) {
		//validate the CN and SubjectAltNames
		var cn = this._containedObject.attrs[ZaApplianceSSLCert.A_commonName];
		var cn_regEx = /^[A-Za-z0-9\-\_\*]{1,}(\.[A-Za-z0-9\-\_]{1,}){0,}(\.[A-Za-z0-9\-\_]{2,}){1,}$/;
		var san_regEx = cn_regEx;
		if (cn ==null || cn.match(cn_regEx) == null){
			//show error msg
			ZaApp.getInstance().getCurrentController().popupErrorDialog(
					AjxMessageFormat.format(com_zimbra_dashboard.CERT_CN_INVALID, cn || "Current CN "));
			return false;
		}
		
		var subjAltNames = this._containedObject.attrs[ZaApplianceSSLCert.A_subject_alt];
		for (var i=0; i < subjAltNames.length; i ++) {
			if (subjAltNames[i].match(san_regEx) == null){
				ZaApp.getInstance().getCurrentController().popupErrorDialog(	
					AjxMessageFormat.format(com_zimbra_dashboard.CERT_SUBJ_ALT_NAME_INVALID, subjAltNames[i]||"SubjectAltName " + i));
				return false;
			}
		}
		
		if (this._containedObject[ZaApplianceSSLCert.A_type_csr]) {
		 	nextStep = ZaApplianceSSLCertWizard.STEP_DOWNLOAD_CSR ;
		} else if (this._containedObject[ZaApplianceSSLCert.A_type_self]) {
			nextStep = ZaApplianceSSLCertWizard.STEP_INSTALL_CERT ;
		}
		try {
			if ((!this._containedObject[ZaApplianceSSLCert.A_csr_exists]) || (this._containedObject[ZaApplianceSSLCert.A_force_new_csr] == 'TRUE')){
				if (!this._containedObject[ZaApplianceSSLCert.A_type_self]) {
                    ZaApplianceSSLCert.genCSR (ZaApp.getInstance(), this._containedObject.attrs, type, true,
                            this._containedObject[ZaApplianceSSLCert.A_target_server], this._containedObject[ZaApplianceSSLCert.A_keysize]) ;
                }
			}
		}catch (ex) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_dashboard.genCSRError, ex, true) ;		
		}
		this.goPage(nextStep) ;
	}else if (cStep == ZaApplianceSSLCertWizard.STEP_UPLOAD_CERT) {
		nextStep = ZaApplianceSSLCertWizard.STEP_INSTALL_CERT ;
		if (this._containedObject[ZaApplianceSSLCert.A_type_comm]) {
			//1. check if the file name are valid and exists
			//No same file name is allowed due to the server limitation - server only return the filename
			var formEl = document.getElementById(ZaApplianceSSLCertWizard.CertUploadFormId);
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
					var v = ZaApplianceSSLCertWizard.getFileName(inputEls[i].value) ;
					if (v != null && v.length != 0) {
						if (ZaUtil.findValueInArray(filenameArr, v) != -1) {
							ZaApp.getInstance().getCurrentController().popupErrorDialog (
									AjxMessageFormat.format(com_zimbra_dashboard.dupFileNameError,[v])
							);
							return ;
						}
						filenameArr.push (v);
					}
					
					if ( n == "certFile") {
						if (v == null ||  v.length == 0) {
							ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_dashboard.noCertFileError);
							return ;
						}else{
							this.uploadInputs["certFile"] = v ;
						}
					}else if (n == "rootCA") {
						if (v == null || v.length == 0 ) {
							ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_dashboard.noRootCAError);
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
				um.execute(certUploadCallback, document.getElementById (ZaApplianceSSLCertWizard.CertUploadFormId));
				return ; //allow the callback to handle the wizard buttons
			}catch (err) {
				ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_dashboard.certFileNameError) ;
				return ;
			}			
		}else if (this._containedObject[ZaApplianceSSLCert.A_type_self]) {
			this.goPage(nextStep) ;
		}else {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_dashboard.certTypeError) ;	
		}
	}else if (cStep == ZaApplianceSSLCertWizard.STEP_CSR_CONFIRM) {
		nextStep = ZaApplianceSSLCertWizard.STEP_UPLOAD_CERT;
		this.goPage(nextStep) ;
	}
	
	if (nextStep == ZaApplianceSSLCertWizard.STEP_INSTALL_CERT || nextStep == ZaApplianceSSLCertWizard.STEP_DOWNLOAD_CSR) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	}
	
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);
}

//TODO: move it to ZaUtil
ZaApplianceSSLCertWizard.getFileName = function (fullPath) {
		if (fullPath == null) return null ;
		
		var lastIndex = 0;
		if (AjxEnv.isWindows) {
			lastIndex = fullPath.lastIndexOf("\\") ;
		}else{
			lastIndex = fullPath.lastIndexOf("/") ;			
		}

		return fullPath.substring(lastIndex + 1) ;
}

ZaApplianceSSLCertWizard.prototype.goPrev = 
function() {
	var cStep = this._containedObject[ZaModel.currentStep];
	var prevStep ;
	if (cStep == ZaApplianceSSLCertWizard.STEP_DOWNLOAD_CSR) {
		prevStep = ZaApplianceSSLCertWizard.STEP_GEN_CSR;
	} else if (cStep == ZaApplianceSSLCertWizard.STEP_GEN_CSR
		|| cStep == ZaApplianceSSLCertWizard.STEP_CSR_CONFIRM) {		
		prevStep = ZaApplianceSSLCertWizard.STEP_USER_OPTION;
	} else if (cStep == ZaApplianceSSLCertWizard.STEP_UPLOAD_CERT ) {
		prevStep = ZaApplianceSSLCertWizard.STEP_CSR_CONFIRM;
	} else if ( cStep == ZaApplianceSSLCertWizard.STEP_INSTALL_CERT ){
		if (this._containedObject[ZaApplianceSSLCert.A_type_comm]) {
			prevStep = ZaApplianceSSLCertWizard.STEP_UPLOAD_CERT;
		} else if (this._containedObject[ZaApplianceSSLCert.A_type_self]) {
			prevStep = ZaApplianceSSLCertWizard.STEP_GEN_CSR;
		}
	} else if (cStep == ZaApplianceSSLCertWizard.STEP_USER_OPTION) {
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
ZaApplianceSSLCertWizard.prototype.setObject =
function(entry) {
	this._containedObject = new Object();	
	this._containedObject = entry ;
	
	this._containedObject[ZaModel.currentStep] = ZaApplianceSSLCertWizard.STEP_USER_OPTION;
    if (this._containedObject [ZaApplianceSSLCert.A_keysize] == null) {
        this._containedObject [ZaApplianceSSLCert.A_keysize] = "2048" ;    
    }
	this._localXForm.setInstance(this._containedObject);
}

//ZaApplianceSSLCertWizard.CertUploadAttachmentInputId = Dwt.getNextId();
ZaApplianceSSLCertWizard.CertUploadFormId = Dwt.getNextId();
ZaApplianceSSLCertWizard.addIntermediateCADivId = Dwt.getNextId ();

ZaApplianceSSLCertWizard.getUploadFormHtml =
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
	html[idx++] = ZaApplianceSSLCertWizard.CertUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'>" ; 
	html[idx++] = "<div><table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;
	html[idx++] = "<colgroup><col width=100/><col width='*' /><col width=50 /></colgroup>";
	
	html[idx++] = "<tbody><tr><td>" + com_zimbra_dashboard.CERT_upload_comm_cert + "</td>";
	html[idx++] = "<td><input type=file  name='certFile' size='40'></input></td><td></td></tr>";

	html[idx++] = "<tr><td>" + com_zimbra_dashboard.CERT_upload_root_CA + "</td>";
	html[idx++] = "<td><input type=file  name='rootCA' size='40'></input></td><td></td></tr>";

//	html[idx++] = "<tr>" + ZaApplianceSSLCertWizard.getIntermediaCAUploadInput() + "</tr>";

	html[idx++] = "</tbody></table></div>";
	
	//add intermediat El
	html[idx++] = "<div>" + ZaApplianceSSLCertWizard.getIntermediaCAUploadInput() +"</div>";
	
	html[idx++] = "<div id='" + ZaApplianceSSLCertWizard.addIntermediateCADivId  + "' width='100%' align='center'>" ;
	html[idx++] = "<span style='color: blue; text-decoration: underline; cursor: default;padding-left: 3px;' " +
					" onmouseout='this.style.cursor=\"default\"' " +
					" onmouseover='this.style.cursor=\"pointer\"'" +
				    " onclick='ZaApplianceSSLCertWizard.addIntermediateCAInput(this);' >" +
				    com_zimbra_dashboard.ADD_IntermediateCA_Label + "</span>";
	html[idx++] = "</div>" ;	
			    
	html[idx++] = "</form></div>";
	
	return html.join("");
}

ZaApplianceSSLCertWizard.getIntermediaCAUploadInput = function () {
	var html = [];
	var idx = 0;
	html[idx++] = "<table border=0 cellspacing=0 cellpadding=2 style='table-layout: fixed;'> " ;
	html[idx++] = "<colgroup><col width=100/><col width='*' /><col width=50 /></colgroup>";
	
	html[idx++] = "<tbody><tr>" ;
	html[idx++] = "<td>" + com_zimbra_dashboard.CERT_upload_intermediate_ca + "</td>";
	html[idx++] = "<td><input type=file  name='intermediateCA' size='40'></input></td>"; 
	html[idx++] = "<td><span style='padding-left:5px; color: blue; text-decoration: underline; cursor: default;' " +
					" onmouseout='this.style.cursor=\"default\"' " +
					" onmouseover='this.style.cursor=\"pointer\"'" +
				    " onclick='ZaApplianceSSLCertWizard.removeIntermediaCAInput(this);' >" +
				    com_zimbra_dashboard.Remove_IntermediateCA_Label + "</span></td>";
	html[idx++] = "</tr></tbody></table>";
	return html.join("");	
} 

ZaApplianceSSLCertWizard.addIntermediateCAInput = function (addSpanEl) {
	var formEl = document.getElementById (ZaApplianceSSLCertWizard.CertUploadFormId) ;
	var newNode = document.createElement("div");
	newNode.innerHTML = ZaApplianceSSLCertWizard.getIntermediaCAUploadInput ();
	formEl.insertBefore(newNode, document.getElementById(ZaApplianceSSLCertWizard.addIntermediateCADivId));
}

ZaApplianceSSLCertWizard.removeIntermediaCAInput = function (removeSpanEl) {
	var formEl = document.getElementById (ZaApplianceSSLCertWizard.CertUploadFormId) ;
	var rowEl = removeSpanEl.parentNode.parentNode ;
	var intermediaCADivEl = rowEl.parentNode.parentNode.parentNode ;
	formEl.removeChild(intermediaCADivEl) ;	
}


ZaApplianceSSLCertWizard.onRepeatRemove = 
function (index, form) {
	var list = this.getInstanceValue();
	if (list == null || typeof(list) == "string" || index >= list.length || index<0) return;
	list.splice(index, 1);
}

ZaApplianceSSLCertWizard.myXFormModifier = function(xFormObject) {		
	var cases = new Array();
			
	var case_user_options = {type:_CASE_, numCols:2, colSizes:["25px","*"], 
            tabGroupKey:ZaApplianceSSLCertWizard.STEP_USER_OPTION, caseKey:ZaApplianceSSLCertWizard.STEP_USER_OPTION,
			align:_LEFT_, valign:_TOP_, cssStyle:"padding-left:50px;"};
			
	var case_user_options_items = [
		{ type:_OUTPUT_, colSpan: 2, value: com_zimbra_dashboard.CERT_select_option},
				{ type:_SPACER_, height: 10},
				{ type:_RADIO_,   groupname: "install_type", ref: ZaApplianceSSLCert.A_type_self,
					label:  com_zimbra_dashboard.CERT_self_signed , enableLabelFor: true,
                    visibilityChecks:[],
                    enableDisableChecks:[],
                        labelLocation:_RIGHT_ , align: _LEFT_ ,
						//TODO: Change it on the XFormItem level
						onChange: function (value, event, form) {
							value = true ;
							this.setInstanceValue (value) ;
							this.setInstanceValue (!value, ZaApplianceSSLCert.A_type_comm ) ;
							this.setInstanceValue (!value, ZaApplianceSSLCert.A_type_csr) ;
						},
						updateElement:function (newValue) {
							this.getElement().checked = (newValue == true);
						}
					},
				{ type:_RADIO_,  groupname: "install_type", ref: ZaApplianceSSLCert.A_type_csr,
					label: com_zimbra_dashboard.CERT_gen_csr, enableLabelFor: true,
                    visibilityChecks:[],
                    enableDisableChecks:[],
                    labelLocation:_RIGHT_ , align: _LEFT_ ,
						//TODO: Change it on the XFormItem level
						onChange: function (value, event, form) {
							value = true ;
							this.setInstanceValue (value) ;
							this.setInstanceValue (!value, ZaApplianceSSLCert.A_type_self ) ;
							this.setInstanceValue (!value, ZaApplianceSSLCert.A_type_comm ) ;
						},
						updateElement:function (newValue) {
							this.getElement().checked = (newValue == true);
						}
					},	
				{ type:_RADIO_,  groupname: "install_type", ref: ZaApplianceSSLCert.A_type_comm,
                    visibilityChecks:[],
                    enableDisableChecks:[],
                    updateElement:function (newValue) {
						this.getElement().checked = (newValue == true);
					},
					onChange: function (value, event, form) {
						value = true ;
						this.setInstanceValue (value) ;
						this.setInstanceValue (!value, ZaApplianceSSLCert.A_type_self ) ;
						this.setInstanceValue (!value, ZaApplianceSSLCert.A_type_csr) ;
					},
					label: com_zimbra_dashboard.CERT_comm_signed, enableLabelFor: true,
					labelLocation:_RIGHT_ , align: _LEFT_} ,
                 { type:_SPACER_, height: 10}
    ];
	case_user_options.items = case_user_options_items;
	cases.push(case_user_options);

	var case_gen_csr = {type:_CASE_, numCols:2, colSizes:["200px","*"], 
		tabGroupKey:ZaApplianceSSLCertWizard.STEP_GEN_CSR, caseKey:ZaApplianceSSLCertWizard.STEP_GEN_CSR,
		align:_LEFT_, valign:_TOP_};
		
	var case_gen_csr_items = [
		{type: _DWT_ALERT_, colSpan:2,
                visibilityChecks:["instance[ZaApplianceSSLCert.A_csr_exists] == true "],
                containerCssStyle: "width:400px;",
				style: DwtAlert.WARNING, iconVisible: false,
				content: com_zimbra_dashboard.CSR_EXISTS_WARNING 
		 }, 
		{type: _GROUP_ , colSpan:2, numCols: 2, colSizes:["150px","300px"],
			  items :[
				{ref: ZaApplianceSSLCert.A_force_new_csr, type: _CHECKBOX_ , label: com_zimbra_dashboard.FORCE_NEW_CSR , 
					visibilityChecks:[" instance[ZaApplianceSSLCert.A_csr_exists] == true "],
                    onChange: function (value, event, form) {
						this.setInstanceValue (value) ;
						form.parent._containedObject.modifySubjectAltNames();
						form.refresh();
					},
					trueValue:"TRUE", falseValue:"FALSE", msgName:com_zimbra_dashboard.FORCE_NEW_CSR },

                  {ref: ZaApplianceSSLCert.A_keysize,type:_OSELECT1_,
					label:com_zimbra_dashboard.CERT_keysize, 
					labelLocation:_LEFT_,
					choices:ZaApplianceSSLCert.KEY_SIZE_CHOICES,
                    visibilityChecks:[],
                    enableDisableChecks:[] },
				{ ref: ZaApplianceSSLCert.A_commonName, type:_TEXTFIELD_, width: 150,
					visibilityChecks:[],  bmolsnr:true,
                    enableDisableChecks:[ZaApplianceSSLCertWizard.isCSRFieldsEnabled],
				    enableDisableChangeEventSources:[ZaApplianceSSLCert.A_csr_exists, ZaApplianceSSLCert.A_force_new_csr],
                    label: com_zimbra_dashboard.CERT_INFO_CN},
				{ ref: ZaApplianceSSLCert.A_use_wildcard_server_name, type:_CHECKBOX_, 
						visibilityChecks:[],
                        enableDisableChecks:[ZaApplianceSSLCertWizard.isCSRFieldsEnabled],
				        enableDisableChangeEventSources:[ZaApplianceSSLCert.A_csr_exists, ZaApplianceSSLCert.A_force_new_csr],
                        label: com_zimbra_dashboard.Use_Wildcard_Server_Name,
						onChange: function (value, event, form) {
							this.setInstanceValue (value) ;
							if (value) {
                                var wildCardSN = ZaApplianceSSLCert.getWildCardServerName(this.getInstanceValue(ZaApplianceSSLCert.A_commonName)) ;
								this.setInstanceValue( wildCardSN,	ZaApplianceSSLCert.A_commonName ) ;
							}
 						}
				},	
				{ ref: ZaApplianceSSLCert.A_countryName, type:_TEXTFIELD_, width: 150, 
                    visibilityChecks:[],
                    enableDisableChecks:[ZaApplianceSSLCertWizard.isCSRFieldsEnabled],
                    enableDisableChangeEventSources:[ZaApplianceSSLCert.A_csr_exists, ZaApplianceSSLCert.A_force_new_csr],
                    label: com_zimbra_dashboard.CERT_INFO_C},
				{ ref: ZaApplianceSSLCert.A_state, type:_TEXTFIELD_, width: 150, 
                    visibilityChecks:[],
                    enableDisableChecks:[ZaApplianceSSLCertWizard.isCSRFieldsEnabled],
                    enableDisableChangeEventSources:[ZaApplianceSSLCert.A_csr_exists, ZaApplianceSSLCert.A_force_new_csr],
					label: com_zimbra_dashboard.CERT_INFO_ST},
				{ ref: ZaApplianceSSLCert.A_city, type:_TEXTFIELD_, width: 150, 
                    visibilityChecks:[],
                    enableDisableChecks:[ZaApplianceSSLCertWizard.isCSRFieldsEnabled],
                    enableDisableChangeEventSources:[ZaApplianceSSLCert.A_csr_exists, ZaApplianceSSLCert.A_force_new_csr],
					label: com_zimbra_dashboard.CERT_INFO_L},
				{ ref: ZaApplianceSSLCert.A_organization, type:_TEXTFIELD_, width: 150,
                    visibilityChecks:[],
                    enableDisableChecks:[ZaApplianceSSLCertWizard.isCSRFieldsEnabled],
                    enableDisableChangeEventSources:[ZaApplianceSSLCert.A_csr_exists, ZaApplianceSSLCert.A_force_new_csr],
					label: com_zimbra_dashboard.CERT_INFO_O},
				{ ref: ZaApplianceSSLCert.A_organizationUnit, type:_TEXTFIELD_, width: 150,
                    visibilityChecks:[],
                    enableDisableChecks:[ZaApplianceSSLCertWizard.isCSRFieldsEnabled],
                    enableDisableChangeEventSources:[ZaApplianceSSLCert.A_csr_exists, ZaApplianceSSLCert.A_force_new_csr],
					label: com_zimbra_dashboard.CERT_INFO_OU},
				 { ref: ZaApplianceSSLCert.A_subject_alt,
                     visibilityChecks:[],
                     enableDisableChecks:[ZaApplianceSSLCertWizard.isCSRFieldsEnabled],
                     enableDisableChangeEventSources:[ZaApplianceSSLCert.A_csr_exists, ZaApplianceSSLCert.A_force_new_csr],
					type:_REPEAT_,
					label:com_zimbra_dashboard.CERT_INFO_SubjectAltName,
					labelLocation:_LEFT_, 
					labelCssStyle:"vertical-align: top; padding-top: 3px;",
					addButtonLabel:com_zimbra_dashboard.NAD_Add, 
					align:_LEFT_,
					repeatInstance:"", 
					showAddButton:true, 
					showRemoveButton:true,
                    addButtonWidth: 50,
                    removeButtonWidth: 50,
                    //showAddOnNextRow:true,
					alwaysShowAddButton:true,
					removeButtonLabel:com_zimbra_dashboard.NAD_Remove,								
					items: [
						{ref:".", type:_TEXTFIELD_, 
                        visibilityChecks:[],
                        enableDisableChecks:[ZaApplianceSSLCertWizard.isCSRFieldsEnabled],
                        enableDisableChangeEventSources:[ZaApplianceSSLCert.A_csr_exists, ZaApplianceSSLCert.A_force_new_csr],
                        onChange:function (value, event, form) {
							this.setInstanceValue(value);
						},
						width:"150px"}
					]
				}			
			]
		},
		
		{ type:_SPACER_ , height: 10 },
		{ type: _GROUP_, colSpan: "*", items: [
		 		{type: _OUTPUT_, value: com_zimbra_dashboard.CERT_SubjectAlt_Note }
		 	]
		 }	
	];	
	
	case_gen_csr.items = case_gen_csr_items;
	cases.push(case_gen_csr);

	var case_upload_cert={type:_CASE_, numCols:1, colSizes:["450px"],
					tabGroupKey:ZaApplianceSSLCertWizard.STEP_UPLOAD_CERT, caseKey:ZaApplianceSSLCertWizard.STEP_UPLOAD_CERT,
                    items: [
						{ type:_GROUP_, id: "CertUpload", 
                            visibilityChecks:["instance[ZaApplianceSSLCert.A_type_comm] == true"],
                            colSpan: 2, numCols: 1, colSizes: "*", items : [
								{ type:_OUTPUT_, value: com_zimbra_dashboard.CERT_uploadTitle, align: _LEFT_},
								{ type:_OUTPUT_, value: ZaApplianceSSLCertWizard.getUploadFormHtml() } ,
								{ type:_SPACER_ , height: 10 }
							]
						},
						{ type:_SPACER_ , height: 10 }
						
					]
				};
	cases.push(case_upload_cert);
	
	var case_install_cert = {type:_CASE_, numCols:2, colSizes:["150px", "300px"],
                    tabGroupKey:ZaApplianceSSLCertWizard.STEP_INSTALL_CERT, caseKey:ZaApplianceSSLCertWizard.STEP_INSTALL_CERT,
					align:_LEFT_, valign:_TOP_};
	var case_install_certItems = [	
			{type:_OUTPUT_, colSpan: 2, 
                visibilityChecks:["instance[ZaApplianceSSLCert.A_type_self] == true"],
                value: com_zimbra_dashboard.CERT_installTitle },
			{ type:_SPACER_ , height: 10 },
			{type:_TEXTFIELD_, ref: ZaApplianceSSLCert.A_validation_days ,			
				//Validation_days is not required for comm install
				visibilityChecks:["instance[ZaApplianceSSLCert.A_type_self] == true"],
                label: com_zimbra_dashboard.CERT_validate_days
			}
		];	
	
	case_install_cert.items = case_install_certItems;
	cases.push(case_install_cert);
	
	var case_download_csr = 
		{type:_CASE_, numCols:1, colSizes:["*"],
            tabGroupKey:ZaApplianceSSLCertWizard.STEP_DOWNLOAD_CSR, caseKey:ZaApplianceSSLCertWizard.STEP_DOWNLOAD_CSR,
			align:_LEFT_, valign:_TOP_ , 
			items :[
				{ type:_OUTPUT_, value: com_zimbra_dashboard.CSR_download_msg_1 },
				{ type:_SPACER_ , height: 10 },
				{ type:_OUTPUT_, value:"<a href='adminres?action=getCSR' onclick='ZaZimbraAdmin.unloadHackCallback();'> "
										+ com_zimbra_dashboard.CSR_download_msg_2 + "</a> "},
				{ type:_SPACER_ , height: 10 }
			]
		}		
	
	cases.push (case_download_csr) ;
	
	var case_csr_confirm = 		
		{type:_CASE_, numCols:1, colSizes:["*"],
            tabGroupKey:ZaApplianceSSLCertWizard.STEP_CSR_CONFIRM, caseKey:ZaApplianceSSLCertWizard.STEP_CSR_CONFIRM,
			align:_LEFT_, valign:_TOP_ , 
			items :[
				{type: _GROUP_ , colSpan:2, numCols: 2, colSizes:["200px","350px"],
				visibilityChecks:["instance[ZaApplianceSSLCert.A_csr_exists] == true"],
                items :[
						{ type: _OUTPUT_, value: com_zimbra_dashboard.CSR_REVIEW, colSpan: 2 },
						{ type:_SPACER_, height:10},
						{ ref: ZaApplianceSSLCert.A_commonName, type:_OUTPUT_, width: 150, 
							label: com_zimbra_dashboard.CERT_INFO_CN},
						{ ref: ZaApplianceSSLCert.A_countryName, type:_OUTPUT_, width: 150, 
							label: com_zimbra_dashboard.CERT_INFO_C},
						{ ref: ZaApplianceSSLCert.A_state, type:_OUTPUT_, width: 150, 
							label: com_zimbra_dashboard.CERT_INFO_ST},
						{ ref: ZaApplianceSSLCert.A_city, type:_OUTPUT_, width: 150, 
							label: com_zimbra_dashboard.CERT_INFO_L},
						{ ref: ZaApplianceSSLCert.A_organization, type:_OUTPUT_, width: 150, 
							label: com_zimbra_dashboard.CERT_INFO_O},
						{ ref: ZaApplianceSSLCert.A_organizationUnit, type:_OUTPUT_, width: 150, 
							label: com_zimbra_dashboard.CERT_INFO_OU},
						{ ref: ZaApplianceSSLCert.A_subject_alt, 
							type:_REPEAT_,
							label:com_zimbra_dashboard.CERT_INFO_SubjectAltName,
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
					visibilityChecks:["!instance[ZaApplianceSSLCert.A_csr_exists] "],
                    cssStyle:"padding-left:50px;", items: [
						{type: _DWT_ALERT_, colSpan:2,
                            visibilityChecks:["!instance[ZaApplianceSSLCert.A_csr_exists] "],
                            containerCssStyle: "width:400px;",
							style: DwtAlert.WARNING, iconVisible: false,
							content: com_zimbra_dashboard.CSR_NON_EXISTS_WARNING 
				 		},
				 		{type:_SPACER_, height:10},
				 		{type: _OUTPUT_, value: com_zimbra_dashboard.CSR_NON_EXISTS_MSG }
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
ZaXDialog.XFormModifiers["ZaApplianceSSLCertWizard"].push(ZaApplianceSSLCertWizard.myXFormModifier);

ZaApplianceSSLCertWizard.isCSRFieldsEnabled = function () {
    var isCSRExist = this.getInstanceValue(ZaApplianceSSLCert.A_csr_exists)  ;
    var isForceNewCSR = this.getInstanceValue(ZaApplianceSSLCert.A_force_new_csr)  ;

	return ( (!isCSRExist) || (isForceNewCSR == "TRUE")) ;
}
   
