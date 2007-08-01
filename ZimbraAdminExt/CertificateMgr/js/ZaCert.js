if (AjxEnv.hasFirebug) console.debug("Loaded ZaCert.js");

function ZaCert (app) {
	ZaItem.call(this, app, "ZaCert");
	this._init(app);
	this.type = ZaItem.CERT ;
}

ZaItem.CERT = "certificate" ;
ZaCert.prototype = new ZaItem ;
ZaCert.prototype.constructor = ZaCert ;

ZaCert.DEFAULT_VALIDATION_DAYS = 365 ;
ZaCert.A_countryName = "C" ;
ZaCert.A_commonName = "CN" ;
ZaCert.A_state = "ST" ;
ZaCert.A_city = "L" ;
ZaCert.A_organization = "O" ;
ZaCert.A_organizationUnit = "OU" ;
ZaCert.A_validation_days = "validation_days" ;
ZaCert.A_subject = "subject" ;
ZaCert.A_type = "type" ;
ZaCert.A_type_self = "self" ;
ZaCert.A_type_comm = "comm" ;
ZaCert.A_csr_exists = "csr_exists" ;
ZaCert.A_force_new_csr = "force_new_csr" ; //only matters when the csr exists

//Init the ZaCert Object for the new Cert wizard
ZaCert.prototype.init = function (getCSRResp) {
	// 1. Check if CSR is generated, set the csr_exists = true 
	this.attrs = {};
	if (getCSRResp) {
		var csr_exists = getCSRResp [ZaCert.A_csr_exists];
		if ( csr_exists && csr_exists == "1") {
			this[ZaCert.A_csr_exists] = true ;
		}else{
			this[ZaCert.A_csr_exists] = false ;
		}
		
		var isComm = getCSRResp ["isComm"];
		if (isComm && isComm == "1") {
			this [ZaCert.A_type_self]  = false ;
			this [ZaCert.A_type_comm] = true ;
		}else{
			this [ZaCert.A_type_self]  = true ;
			this [ZaCert.A_type_comm] = false ;
		}
		
		for (var key in getCSRResp) {
			if (getCSRResp[key] instanceof Array) {
				this.attrs[key] = getCSRResp[key][0]._content ;
			}
		}
	}	
	
	this [ZaCert.A_validation_days] = ZaCert.DEFAULT_VALIDATION_DAYS ;
	this [ZaCert.A_force_new_csr]  = 'FALSE';
}

ZaCert.certOvTreeModifier = function (tree) {
	if(ZaSettings.TOOLS_ENABLED) {
		this._certTi = new DwtTreeItem(this._toolsTi);
		this._certTi.setText(ZaMsg.OVP_certs);
		this._certTi.setImage("Backup"); //TODO: Use Cert icons
		this._certTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._CERTS);	
		
		if(ZaOverviewPanelController.overviewTreeListeners) {
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._CERTS] = ZaCert.certsTreeListener;
		}
	}
}

ZaCert.certsTreeListener = function (ev) {
	if(this._app.getCurrentController()) {
		this._app.getCurrentController().switchToNextView(
			this._app.getCertViewController(),ZaCertViewController.prototype.show, ZaCert.getCerts(this._app));
	} else {					
		this._app.getCertViewController().show(ZaCert.getCerts(this._app));
	}
}

ZaCert.getCerts = function (app) {
	if (AjxEnv.hasFirebug) console.log("Geting certificates") ;
	
	var soapDoc = AjxSoapDoc.create("GetCertRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("certtype", "all");
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	var reqMgrParams = {} ;
	reqMgrParams.controller = app.getCurrentController();
	reqMgrParams.busyMsg = ZaMsg.BUSY_RETRIEVE_CERT ;
	resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GetCertResponse;
	return resp;
	
}

ZaCert.getCSR = function (app) {
	if (AjxEnv.hasFirebug) console.log("Geting CSR") ;
	
	var soapDoc = AjxSoapDoc.create("GetCSRRequest", "urn:zimbraAdmin", null);
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	var reqMgrParams = {} ;
	reqMgrParams.controller = app.getCurrentController();
	reqMgrParams.busyMsg = ZaMsg.BUSY_GET_CSR ;
	resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GetCSRResponse;
	return resp;	
}

ZaCert.genCSR = function (app, subject_attrs, forceNewCSR) {
	if (AjxEnv.hasFirebug) console.log("Generating certificates") ;
	var soapDoc = AjxSoapDoc.create("GenCSRRequest", "urn:zimbraAdmin", null);
	if (forceNewCSR && forceNewCSR == 'TRUE') {
		soapDoc.getMethod().setAttribute("new", "1");
	}else{
		soapDoc.getMethod().setAttribute("new", "0");		
	}
	
	for (var n in subject_attrs) {
		soapDoc.set(n, subject_attrs[n]) ;
	}
	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	var reqMgrParams = {} ;
	reqMgrParams.controller = app.getCurrentController();
	reqMgrParams.busyMsg = ZaMsg.BUSY_GENERATE_CSR ;
	resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GenCSRResponse;
	return resp;
}

ZaCert.installCert = function (app, type, validation_days, attId, callback) {
	if (AjxEnv.hasFirebug) console.log("Installing certificates") ;
	var soapDoc = AjxSoapDoc.create("InstallCertRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("type", type);
	
	if (type == ZaCert.A_type_self || type == ZaCert.A_type_comm) {
		soapDoc.set(ZaCert.A_validation_days, validation_days);	
		if (type == ZaCert.A_type_comm) {
			soapDoc.set("aid", attId);	
		}
	}else {
		throw new Exeption ("Unknow installation type") ;		
	}
	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	var reqMgrParams = {} ;
	reqMgrParams.controller = app.getCurrentController();
	reqMgrParams.busyMsg = ZaMsg.BUSY_INSTALL_CERT ;
	if (callback) {
		csfeParams.callback = callback;
		csfeParams.asyncMode = true ;	
	}
	ZaRequestMgr.invoke(csfeParams, reqMgrParams ) ;
}



ZaCert.myXModel = {
	items: [
		{id: ZaCert.A_installStatus, type: _STRING_, ref: ZaCert.A_installStatus },
		{id: ZaCert.A_countryName, type: _STRING_, ref: "attrs/" + ZaCert.A_countryName, length: 2},
		{id: ZaCert.A_commonName, type: _STRING_, ref: "attrs/" + ZaCert.A_commonName },
		{id: ZaCert.A_state, type: _STRING_, ref: "attrs/" + ZaCert.A_state },
		{id: ZaCert.A_city, type: _STRING_, ref: "attrs/" + ZaCert.A_city },
		{id: ZaCert.A_organization, type: _STRING_, ref: "attrs/" + ZaCert.A_organization },
		{id: ZaCert.A_organizationUnit, type: _STRING_, ref: "attrs/" + ZaCert.A_organizationUnit },
		{id: ZaCert.A_validation_days, type: _NUMBER_, ref: ZaCert.A_validation_days, required: true },
		{id: ZaCert.A_type_comm, type: _ENUM_, ref: ZaCert.A_type_comm, choices:ZaModel.BOOLEAN_CHOICES1 },
		{id: ZaCert.A_type_self, type: _ENUM_, ref: ZaCert.A_type_self, choices:ZaModel.BOOLEAN_CHOICES1 },
		{id: ZaCert.A_csr_exists, type: _ENUM_, ref: ZaCert.A_csr_exists, choices:ZaModel.BOOLEAN_CHOICES1 },
		{id: ZaCert.A_force_new_csr, type: _ENUM_, ref: ZaCert.A_force_new_csr, choices:ZaModel.BOOLEAN_CHOICES }
	]
}