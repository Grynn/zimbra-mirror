
function ZaApplianceLicenseWizard (parent) {
	ZaXWizardDialog.call(this, parent, null, com_zimbra_license.LIW_title, "500px", "300px","ZaApplianceLicenseWizard");

	this.stepChoices = [
		{label:com_zimbra_license.LIW_TABT_upload, value:1},
		{label:com_zimbra_license.LIW_uploadTitle, value:2}
	];
	
	this._lastStep = this.stepChoices.length;
	this.attId = null ;	
	this.initForm(ZaApplianceLicense.myXModel,this.getMyXForm());	
	this._localXForm.setController(ZaApp.getInstance());	
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaApplianceLicenseWizard.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaApplianceLicenseWizard.prototype.handleXFormChange));	
	this._helpURL = ZaApplianceLicenseWizard.helpURL;
}

ZaApplianceLicenseWizard.prototype = new ZaXWizardDialog;
ZaApplianceLicenseWizard.prototype.constructor = ZaApplianceLicenseWizard;
ZaXDialog.XFormModifiers["ZaApplianceLicenseWizard"] = new Array();
ZaApplianceLicenseWizard.helpURL = location.pathname + "help/admin/html/managing_global_settings/updating_your_zimbra_license.htm?locid=" + AjxEnv.DEFAULT_LOCALE;
ZaApplianceLicenseWizard.prototype.handleXFormChange = 
function () {
	if(this._localXForm.hasErrors()) {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	} else {
		this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
	}
}

/**
* Overwritten methods that control wizard's flow (open, go next,go previous, finish)
**/
ZaApplianceLicenseWizard.prototype.popup = 
function (loc) {
	ZaXWizardDialog.prototype.popup.call(this, loc);
	this._containedObject = new Object();
	this._containedObject[ZaApplianceLicense.InstallStatusCode] = 0;
	this._containedObject[ZaApplianceLicense.InstallStatusMsg] = "";
	this._containedObject[ZaModel.currentStep] = 1;
	this._localXForm.setInstance(this._containedObject);
	
	this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(false);
	this._button[DwtWizardDialog.FINISH_BUTTON].setText(com_zimbra_dashboard.LIW_INSTALL_BUTTON_text);
	this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);	
}

ZaApplianceLicenseWizard.prototype._uploadCallback =
function (status, attId) {
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
		var msg = AjxMessageFormat.format(com_zimbra_dashboard.UploadLicenseErrorMsg, status);
		ZaApp.getInstance().getCurrentController().popupErrorDialog(msg + com_zimbra_dashboard.ErrorTryAgain, null, null, true);		
	}	
}

//upload the file
ZaApplianceLicenseWizard.prototype.getUploadFrameId =
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

ZaApplianceLicenseWizard.prototype.getUploadManager = 
function() { 
	return this._uploadManager;
};

/**
 * @params uploadManager is the AjxPost object
 */
ZaApplianceLicenseWizard.prototype.setUploadManager = 
function(uploadManager) {
	this._uploadManager = uploadManager;
};


ZaApplianceLicenseWizard.prototype.goNext = 
function() {
	if (this._containedObject[ZaModel.currentStep] == 1) {
		//1. check if the file name are valid and exists
		//2. Upload the files
		DBG.println("Start uploading the file");
		this.setUploadManager(new AjxPost(this.getUploadFrameId()));
		var licenseUploadCallback = new AjxCallback(this, this._uploadCallback);
		var um = this.getUploadManager() ; 
		window._uploadManager = um;
		try {
			um.execute(licenseUploadCallback, document.getElementById (ZaApplianceLicenseWizard.LicenseUploadFormId));
		}catch (err) {
			ZaApp.getInstance().getCurrentController().popupErrorDialog(com_zimbra_dashboard.licenseFileNameError) ;
		}
	} else{
		this.goPage(this._containedObject[ZaModel.currentStep] + 1);
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(true);		
		if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
			this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(false);
			this._button[DwtWizardDialog.FINISH_BUTTON].setEnabled(true);
		}	
	}
	
};

ZaApplianceLicenseWizard.prototype.goPrev = 
function() {
	if (this._containedObject[ZaModel.currentStep] == 2) {
		this._button[DwtWizardDialog.PREV_BUTTON].setEnabled(false);
	}
	
	if(this._containedObject[ZaModel.currentStep] == this._lastStep) {
		this._button[DwtWizardDialog.NEXT_BUTTON].setEnabled(true);
	}
	this.goPage(this._containedObject[ZaModel.currentStep] - 1);
}

ZaApplianceLicenseWizard.LicenseUploadAttachmentInputId = Dwt.getNextId();
ZaApplianceLicenseWizard.LicenseUploadFormId = Dwt.getNextId();
ZaApplianceLicenseWizard.getUploadFormHtml =
function (){
	var uri = appContextPath + "/../service/upload";
	DBG.println("upload uri = " + uri);
	var html = new Array();
	var idx = 0;
	html[idx++] = "<div style='overflow:auto'><form method='POST' action='";
	html[idx++] = uri;
	html[idx++] = "' id='";
	html[idx++] = ZaApplianceLicenseWizard.LicenseUploadFormId;
	html[idx++] = "' enctype='multipart/form-data'><input id='";
	html[idx++] = ZaApplianceLicenseWizard.LicenseUploadAttachmentInputId;
	html[idx++] = "' type=file  name='licenseFile' size='50'></input>";
	html[idx++] = "</form></div>";
	
	return html.join("");
}

ZaApplianceLicenseWizard.myXFormModifier = function(xFormObject) {		
	var cases = new Array();
	var case1 = {type:_CASE_, numCols:1, caseKey:1, align:_LEFT_, valign:_TOP_};
	//upload components
	var case1Items = [
		{ type:_OUTPUT_, value: com_zimbra_dashboard.LIW_uploadTitle, align: _LEFT_},
		{ type:_OUTPUT_, value: ZaApplianceLicenseWizard.getUploadFormHtml() }
	];	
	
	case1.items = case1Items;
	cases.push(case1);
	
	var case2={type:_CASE_, numCols:2, colSizes:["200px","*"],caseKey:2,
		items: [
		        { type:_OUTPUT_, value: com_zimbra_dashboard.LIW_uLicenseInfo_title, colSpan: "2", width: "400px", align: _LEFT_ },
				{ type:_SPACER_, height: 10},
				{ type:_OUTPUT_, value: com_zimbra_dashboard.LIW_uLicenseConfirmation, colSpan: "2", width: "400px", align: _LEFT_ }			
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
ZaXDialog.XFormModifiers["ZaApplianceLicenseWizard"].push(ZaApplianceLicenseWizard.myXFormModifier);
