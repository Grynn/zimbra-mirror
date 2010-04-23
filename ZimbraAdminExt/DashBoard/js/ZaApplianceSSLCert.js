if (AjxEnv.hasFirebug) console.debug("Loaded ZaApplianceSSLCert.js");

function ZaApplianceSSLCert () {
	ZaItem.call(this,  "ZaApplianceSSLCert");
	this._init();
	this.type = ZaItem.CERT ;
}

ZaItem.CERT = "certificate" ;
ZaApplianceSSLCert.prototype = new ZaItem ;
ZaApplianceSSLCert.prototype.constructor = ZaApplianceSSLCert ;
if (ZaOperation) ZaOperation.INSTALL_ZCS_CERTIFICATE = ++ZA_OP_INDEX;

ZaApplianceSSLCert.DEFAULT_VALIDATION_DAYS = 365 ;
ZaApplianceSSLCert.A_countryName = "C" ;
ZaApplianceSSLCert.A_commonName = "CN" ;
ZaApplianceSSLCert.A_state = "ST" ;
ZaApplianceSSLCert.A_city = "L" ;
ZaApplianceSSLCert.A_organization = "O" ;
ZaApplianceSSLCert.A_organizationUnit = "OU" ;
ZaApplianceSSLCert.A_validation_days = "validation_days";
ZaApplianceSSLCert.A_validation_days_ro = "validation_days_ro";
ZaApplianceSSLCert.A_notBefore = "notBefore";
ZaApplianceSSLCert.A_notAfter = "notAfter";
//ZaApplianceSSLCert.A_allserver = "allserver" ;
ZaApplianceSSLCert.A_subject = "subject" ;
ZaApplianceSSLCert.A_type = "type" ;
ZaApplianceSSLCert.A_type_self = "self" ;
ZaApplianceSSLCert.A_type_comm = "comm" ;
ZaApplianceSSLCert.A_type_csr = "csr" ; //generate the csr only
ZaApplianceSSLCert.A_csr_exists = "csr_exists" ;
ZaApplianceSSLCert.A_keysize = "keysize" ;
ZaApplianceSSLCert.A_force_new_csr = "force_new_csr" ; //only matters when the csr exists
ZaApplianceSSLCert.A_target_server = "target_server" ;
ZaApplianceSSLCert.A_subject_alt = "SubjectAltName";
ZaApplianceSSLCert.A_use_wildcard_server_name = "user_wildcard_server_name";
ZaApplianceSSLCert.A_issuer = "issuer";
ZaApplianceSSLCert.A_type = "type";

ZaApplianceSSLCert.ALL_SERVERS = "--- All Servers ---" ; //Don't modify it, it need to be consistent with server side value

ZaApplianceSSLCert.TARGET_SERVER_CHOICES =  [
		{label:com_zimbra_cert_manager.lb_ALL_SERVERS, value: ZaApplianceSSLCert.ALL_SERVERS }
	];

ZaApplianceSSLCert.KEY_SIZE_CHOICES = [ {label: "1024", value: "1024"},
                            {label: "2048", value: "2048"}] ;

//Init the ZaApplianceSSLCert Object for the new Cert wizard
ZaApplianceSSLCert.prototype.init = function (getCSRResp) {
	// 1. Check if CSR is generated, set the csr_exists = true 
	this.attrs = {};
	this.attrs [ZaApplianceSSLCert.A_subject_alt] = [];
	this [ZaApplianceSSLCert.A_type_self]  = true ;
	this [ZaApplianceSSLCert.A_type_comm] = false ;
	this [ZaApplianceSSLCert.A_type_csr] = false ;
    this [ZaApplianceSSLCert.A_keysize] = "2048" ;
	this.initCSR(getCSRResp) ;
	this [ZaApplianceSSLCert.A_validation_days] = ZaApplianceSSLCert.DEFAULT_VALIDATION_DAYS ;
	this [ZaApplianceSSLCert.A_force_new_csr]  = 'FALSE';
}

ZaApplianceSSLCert.prototype.initCSR = function (getCSRResp) {
	if (getCSRResp) {
		var csr_exists = getCSRResp [ZaApplianceSSLCert.A_csr_exists];
		if ( csr_exists && csr_exists == "1") {
			this[ZaApplianceSSLCert.A_csr_exists] = true ;
		}else{
			this[ZaApplianceSSLCert.A_csr_exists] = false ;
		}
		
		/*
		var isComm = getCSRResp ["isComm"];
		if (isComm && isComm == "1") {
			this [ZaApplianceSSLCert.A_type_self]  = false ;
			this [ZaApplianceSSLCert.A_type_comm] = true ;
			this [ZaApplianceSSLCert.A_type_csr] = false ;
		}else{
			this [ZaApplianceSSLCert.A_type_self]  = true ;
			this [ZaApplianceSSLCert.A_type_comm] = false ;
			this [ZaApplianceSSLCert.A_type_csr] = false ;
		}*/
		
		for (var key in getCSRResp) {
			var value = getCSRResp[key] ;
			if (value instanceof Array) {
				//array attributes
				if ((key == ZaApplianceSSLCert.A_subject_alt) || (value.length > 1)) {
					this.attrs[key] = [] ;
					for (var i=0; i < value.length; i ++) {
						if (value[i]._content.length > 0) {//non empty value
							this.attrs[key].push (value[i]._content) ;
						}
					}
				}else{ 
					this.attrs[key] = value[0]._content ;
				}
			}
		}
	}
	
	//modify the Subject Alt Name based on the 	target server choices
	this.modifySubjectAltNames();
}

//this function should be called when the CSR creation wizard is changed or shown
ZaApplianceSSLCert.prototype.modifySubjectAltNames = function () {
	if (AjxEnv.hasFirebug) console.log("Enter ZaApplianceSSLCert.prototype.modifySubjectAltNames ");
	
	var currentSubjAltNames = this.attrs[ZaApplianceSSLCert.A_subject_alt] ;
	// only modify when a new CSR should be created.
	if ((!this[ZaApplianceSSLCert.A_csr_exists]) || (this[ZaApplianceSSLCert.A_force_new_csr] == 'TRUE')) { 
		if (AjxEnv.hasFirebug) console.log("Modifying SubjectAltNames ");
		if (this[ZaApplianceSSLCert.A_target_server] == ZaApplianceSSLCert.ALL_SERVERS) {
			for (var i=0; i < ZaApplianceSSLCert.TARGET_SERVER_CHOICES.length; i ++) {
				if ((ZaApplianceSSLCert.TARGET_SERVER_CHOICES[i].value != ZaApplianceSSLCert.ALL_SERVERS) && //Not All Servers Value
				//the target server name doesn't exist in the current subjectAltName
				   (ZaUtil.findValueInArray(currentSubjAltNames, ZaApplianceSSLCert.TARGET_SERVER_CHOICES[i].label) == -1)){
					
					//add this target server value to subject alt names
					if (AjxEnv.hasFirebug) console.log("Adding " + ZaApplianceSSLCert.TARGET_SERVER_CHOICES[i].label);
					currentSubjAltNames.push(ZaApplianceSSLCert.TARGET_SERVER_CHOICES[i].label);
				}			
			}
		}else{
			var targetServerName ;
			for (var i=0; i < ZaApplianceSSLCert.TARGET_SERVER_CHOICES.length; i ++) {
				if (ZaApplianceSSLCert.TARGET_SERVER_CHOICES[i].value == this[ZaApplianceSSLCert.A_target_server]){
					targetServerName = ZaApplianceSSLCert.TARGET_SERVER_CHOICES[i].label ;
				}
			}
				//add this target server value to subject alt names
			
			if ((targetServerName != null) && 
				//the target server name doesn't exist in the current subjectAltName
				 (ZaUtil.findValueInArray(currentSubjAltNames, targetServerName) == -1)){
				if (AjxEnv.hasFirebug) console.log("Adding " + targetServerName);
				currentSubjAltNames.push(targetServerName);
			}
		}
	}
}

ZaApplianceSSLCert.certOvTreeModifier = function (tree) {
	var overviewPanelController = this ;
	if (!overviewPanelController) throw new Exception("ZaApplianceSSLCert.certOvTreeModifier: Overview Panel Controller is not set.");
	
	if(ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CERTS_VIEW] || ZaSettings.ENABLED_UI_COMPONENTS[ZaSettings.CARTE_BLANCHE_UI]) {
		overviewPanelController._certTi = new DwtTreeItem({parent:overviewPanelController._toolsTi,className:"AdminTreeItem"});
		overviewPanelController._certTi.setText(com_zimbra_cert_manager.OVP_certs);
		overviewPanelController._certTi.setImage("OverviewCertificate"); //TODO: Use Cert icons
		overviewPanelController._certTi.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._CERTS_SERVER_LIST_VIEW);	
		
		//add the server nodes
		try {
			var serverList = overviewPanelController._app.getServerList().getArray();
			if(serverList && serverList.length) {
				var cnt = serverList.length;
				for(var ix=0; ix< cnt; ix++) {
					var ti1 = new DwtTreeItem({parent:overviewPanelController._certTi,className:"AdminTreeItem"});			
					ti1.setText(serverList[ix].name);	
					ti1.setImage("Server");
					ti1.setData(ZaOverviewPanelController._TID, ZaZimbraAdmin._CERTS);
					ti1.setData(ZaOverviewPanelController._OBJ_ID, serverList[ix].id);
					ZaApplianceSSLCert.TARGET_SERVER_CHOICES.push (
						{label: serverList[ix].name, value: serverList[ix].id }
					);
				}
			}
		} catch (ex) {
			overviewPanelController._handleException(ex, "ZaApplianceSSLCert.certOvTreeModifier", null, false);
		}
		
		if(ZaOverviewPanelController.overviewTreeListeners) {
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._CERTS_SERVER_LIST_VIEW] = ZaApplianceSSLCert.certsServerListTreeListener;		
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._CERTS] = ZaApplianceSSLCert.certsServerNodeTreeListener;
		}
	}
}
                         
//When the certs tree item is clicked
ZaApplianceSSLCert.certsServerListTreeListener = function (ev) {
	if (AjxEnv.hasFirebug) console.log("Show the server lists ...") ;
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(
			ZaApp.getInstance().getCertsServerListController(),ZaApplianceSSLCertsServerListController.prototype.show, ZaServer.getAll());
	} else {					
		ZaApp.getInstance().getCertsServerListController().show(ZaServer.getAll());
	}
}

//When the individul server node under the certs tree item is clicked
ZaApplianceSSLCert.certsServerNodeTreeListener = function (ev) {
	var serverNodeId = ev.item.getData(ZaOverviewPanelController._OBJ_ID) ;
	if (AjxEnv.hasFirebug) console.log("Click the server node: " + serverNodeId) ;
	
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(
			ZaApp.getInstance().getCertViewController(),
			ZaApplianceSSLCertViewController.prototype.show,
			[ZaApplianceSSLCert.getCerts(ZaApp.getInstance(), serverNodeId), serverNodeId]);
	} else {
		ZaApp.getInstance().getCertViewController().show(
			ZaApplianceSSLCert.getCerts(ZaApp.getInstance(), serverNodeId),
			serverNodeId);
	}
}

ZaApplianceSSLCert.getCerts = function (serverId) {
	if (AjxEnv.hasFirebug) console.log("Getting certificates for server " + serverId) ;
	
	var soapDoc = AjxSoapDoc.create("GetCertRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("type", "all");
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	if (serverId != null) {
		soapDoc.getMethod().setAttribute("server", serverId);
	}else{
		if (AjxEnv.hasFirebug) console.log("Warning: serverId is missing for ZaApplianceSSLCert.getCerts.") ;
	}
	
	try {
		var reqMgrParams = {} ;
		reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
		reqMgrParams.busyMsg = com_zimbra_dashboard.BUSY_RETRIEVE_CERT ;
		reqMgrParams.showBusy = true;
		var resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GetCertResponse;
		var certsArray = [];
		if(resp && resp.cert) {			
			var cnt = resp.cert.length;
			for(var i=0;i<cnt;i++) {
				if(resp.cert[i]) {
					var certObj = {};
					if(resp.cert[i][ZaApplianceSSLCert.A_issuer] && resp.cert[i][ZaApplianceSSLCert.A_issuer][0] && resp.cert[i][ZaApplianceSSLCert.A_issuer][0]._content) {
						certObj[ZaApplianceSSLCert.A_issuer] = resp.cert[i][ZaApplianceSSLCert.A_issuer][0]._content;
					}
					if(resp.cert[i][ZaApplianceSSLCert.A_subject] && resp.cert[i][ZaApplianceSSLCert.A_subject][0] && resp.cert[i][ZaApplianceSSLCert.A_subject][0]._content) {
						certObj[ZaApplianceSSLCert.A_subject] = resp.cert[i][ZaApplianceSSLCert.A_subject][0]._content;
					}
					if(resp.cert[i][ZaApplianceSSLCert.A_subject_alt] && resp.cert[i][ZaApplianceSSLCert.A_subject_alt][0] && resp.cert[i][ZaApplianceSSLCert.A_subject_alt][0]._content) {
						certObj[ZaApplianceSSLCert.A_subject_alt] = resp.cert[i][ZaApplianceSSLCert.A_subject_alt][0]._content;
					}
					if(resp.cert[i][ZaApplianceSSLCert.A_type]) {
						certObj[ZaApplianceSSLCert.A_type] = resp.cert[i][ZaApplianceSSLCert.A_type];
					}
					if(resp.cert[i][ZaApplianceSSLCert.A_notBefore] && resp.cert[i][ZaApplianceSSLCert.A_notBefore][0] && resp.cert[i][ZaApplianceSSLCert.A_notBefore][0]._content) {
						certObj[ZaApplianceSSLCert.A_notBefore] = resp.cert[i][ZaApplianceSSLCert.A_notBefore][0]._content;
					}
					if(resp.cert[i][ZaApplianceSSLCert.A_notAfter] && resp.cert[i][ZaApplianceSSLCert.A_notAfter][0] && resp.cert[i][ZaApplianceSSLCert.A_notAfter][0]._content) {
						certObj[ZaApplianceSSLCert.A_notAfter] = resp.cert[i][ZaApplianceSSLCert.A_notAfter][0]._content;
					}	
					if(certObj[ZaApplianceSSLCert.A_notBefore] && certObj[ZaApplianceSSLCert.A_notAfter]) {
						certObj[ZaApplianceSSLCert.A_validation_days] = AjxMessageFormat.format(com_zimbra_dashboard.CertValidationDaysPattern, [certObj[ZaApplianceSSLCert.A_notBefore],certObj[ZaApplianceSSLCert.A_notAfter]]);
					}
				}
				certsArray.push(certObj);
			}
			return certsArray;
		}
	}catch (ex) {
		ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaApplianceSSLCert.getCerts", null, false);
	}
}

ZaApplianceSSLCert.getCSR = function (app, serverId, type) {
	if (AjxEnv.hasFirebug) console.log("ZaApplianceSSLCert.getCSR: Getting CSR for server: " + serverId) ;
	
	var soapDoc = AjxSoapDoc.create("GetCSRRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("type", type);
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	
	//if (serverId && serverId != ZaApplianceSSLCert.ALL_SERVERS) {
	if (serverId != null){
		soapDoc.getMethod().setAttribute("server", serverId);
	}else{
		if (AjxEnv.hasFirebug) console.log("Warning: serverId is missing for ZaApplianceSSLCert.getCSR.") ;
	}
	
	try {
		var reqMgrParams = {} ;
		reqMgrParams.controller = app.getCurrentController();
		reqMgrParams.busyMsg = com_zimbra_cert_manager.BUSY_GET_CSR ;
		resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GetCSRResponse;
		return resp;	
	}catch (ex) {
		app.getCurrentController()._handleException(ex, "ZaApplianceSSLCert.getCSR", null, false);
	}
}

ZaApplianceSSLCert.genCSR = function (app, subject_attrs,  type, newCSR, serverId, keysize) {
	if (AjxEnv.hasFirebug) console.log("Generating certificates") ;
	var soapDoc = AjxSoapDoc.create("GenCSRRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("type", type);
	soapDoc.getMethod().setAttribute("keysize", keysize) ;
    if (newCSR) {
		soapDoc.getMethod().setAttribute("new", "1");
	}else{
		soapDoc.getMethod().setAttribute("new", "0");		
	}
	
	if (serverId != null) {
		soapDoc.getMethod().setAttribute("server", serverId);
	}else{
		if (AjxEnv.hasFirebug) console.log("Warning: serverId is missing for ZaApplianceSSLCert.genCSR.") ;
	}
	
	for (var n in subject_attrs) {
		if (n == ZaApplianceSSLCert.A_subject_alt) {
			var subjectAlts = subject_attrs[n] ;
			if (( subjectAlts instanceof Array) && (subjectAlts.length > 0)){
				for (var i=0; i < subjectAlts.length; i ++) {
					soapDoc.set(n, subject_attrs[n][i]);
				}
			}
		}else{
			soapDoc.set(n, subject_attrs[n]) ;
		}
	}
	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	try {
		var reqMgrParams = {} ;
		reqMgrParams.controller = app.getCurrentController();
		reqMgrParams.busyMsg = com_zimbra_cert_manager.BUSY_GENERATE_CSR ;
		resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GenCSRResponse;
		return resp;
	}catch (ex) {
		app.getCurrentController()._handleException(ex, "ZaApplianceSSLCert.genCSR", null, false);
	}
}

ZaApplianceSSLCert.installCert = function (params, serverId) {
	if (AjxEnv.hasFirebug) console.log("Installing certificates") ;
	var type = params.type ;
	var comm_cert = params.comm_cert ;
	var validation_days = params.validation_days ;
	var callback = params.callback ;
    var subject = params.subject ;
    var keysize = params.keysize ;
		
	var soapDoc = AjxSoapDoc.create("InstallCertRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("type", type);
	soapDoc.getMethod().setAttribute("server", serverId);
	if (type == ZaApplianceSSLCert.A_type_self || type == ZaApplianceSSLCert.A_type_comm) {
		soapDoc.set(ZaApplianceSSLCert.A_validation_days, validation_days);

		//soapDoc.set(ZaApplianceSSLCert.A_allserver, allserver) ;
		if (type == ZaApplianceSSLCert.A_type_comm) {
			//set the comm_cert element
			soapDoc.set("comm_cert", comm_cert);	
		}else if (type == ZaApplianceSSLCert.A_type_self) {
            soapDoc.set(ZaApplianceSSLCert.A_keysize, keysize) ;
        }
        //add the subject element and subjectAltNames element
        if (subject != null) {
            var subject_attrs = {} ;
            for (var n in subject) {
                if (n == ZaApplianceSSLCert.A_subject_alt) {
                   var subjectAlts = subject[n] ;
                    if (( subjectAlts instanceof Array) && (subjectAlts.length > 0)){
                        for (var i=0; i < subjectAlts.length; i ++) {
                            soapDoc.set(n, subjectAlts[i]);
                        }
                    }
                }else{
                    subject_attrs [n] = subject[n] ;
                }
            }
            soapDoc.set("subject", subject_attrs);
        }
    }else {
		throw new AjxException (com_zimbra_dashboard.UNKNOW_INSTALL_TYPE_ERROR, "ZaApplianceSSLCert.installCert") ;		
	}
	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	var reqMgrParams = {} ;
	reqMgrParams.controller = ZaApp.getInstance().getCurrentController();
	reqMgrParams.busyMsg = com_zimbra_cert_manager.BUSY_INSTALL_CERT ;
	reqMgrParams.showBusy = true;
	if (callback) {
		csfeParams.callback = callback;
		csfeParams.asyncMode = true ;	
	}
	ZaRequestMgr.invoke(csfeParams, reqMgrParams ) ;
}

ZaApplianceSSLCert.prototype.setTargetServer = function (serverId) {
		this[ZaApplianceSSLCert.A_target_server] = serverId ;
}

ZaApplianceSSLCert.launchNewCertWizard = function (serverId) {
	try {
		if(!ZaApp.getInstance().dialogs["certInstallWizard"])
			ZaApp.getInstance().dialogs["certInstallWizard"] = new ZaApplianceSSLCertWizard (this._container, ZaApp.getInstance()) ;	
		
		this._cert = new ZaApplianceSSLCert(ZaApp.getInstance());
		this._cert.setTargetServer (serverId);		
		this._cert.init() ;
		ZaApp.getInstance().dialogs["certInstallWizard"].setObject(this._cert);
		ZaApp.getInstance().dialogs["certInstallWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaApplianceSSLCert.launchNewCertWizard", null, false);
	}
}

ZaApplianceSSLCert.myXModel = {
	items: [
		{id: ZaApplianceSSLCert.A_installStatus, type: _STRING_, ref: ZaApplianceSSLCert.A_installStatus },
		{id: ZaApplianceSSLCert.A_subject_alt, type: _LIST_, ref:"attrs/" + ZaApplianceSSLCert.A_subject_alt, listItem:{type:_STRING_}},
		{id: ZaApplianceSSLCert.A_target_server, type:_STRING_ , ref: ZaApplianceSSLCert.A_target_server },
		{id: ZaApplianceSSLCert.A_countryName, type: _STRING_, ref: "attrs/" + ZaApplianceSSLCert.A_countryName, length: 2, pattern: /^\s*[a-zA-Z0-9\/\.\-\\_:\@\=\'\*]*$/ },
        {id: ZaApplianceSSLCert.A_keysize, type: _STRING_, ref: ZaApplianceSSLCert.A_keysize},
		{id: ZaApplianceSSLCert.A_commonName, type: _STRING_, ref: "attrs/" + ZaApplianceSSLCert.A_commonName },
		{id: ZaApplianceSSLCert.A_state, type: _STRING_, ref: "attrs/" + ZaApplianceSSLCert.A_state, pattern: /^\s*[a-zA-Z0-9\/\.\-\\_:\@\=\'\*]*$/ },
		{id: ZaApplianceSSLCert.A_city, type: _STRING_, ref: "attrs/" + ZaApplianceSSLCert.A_city, pattern: /^\s*[a-zA-Z0-9\/\.\-\\_:\@\=\'\*\s]*$/ },
		{id: ZaApplianceSSLCert.A_organization, type: _STRING_, ref: "attrs/" + ZaApplianceSSLCert.A_organization, pattern: /^\s*[a-zA-Z0-9\/\.\-\\_:\@\=\,\'\*\s]*$/ },
		{id: ZaApplianceSSLCert.A_organizationUnit, type: _STRING_, ref: "attrs/" + ZaApplianceSSLCert.A_organizationUnit, pattern: /^\s*[a-zA-Z0-9\/\.\-\\_:\@\=\,\'\*\s]*$/ },
		{id: ZaApplianceSSLCert.A_validation_days, type: _NUMBER_, ref: ZaApplianceSSLCert.A_validation_days, required: true },
		{id: ZaApplianceSSLCert.A_validation_days_ro, type: _NUMBER_, ref: ZaApplianceSSLCert.A_validation_days},
		{id: ZaApplianceSSLCert.A_type_comm, type: _ENUM_, ref: ZaApplianceSSLCert.A_type_comm, choices:ZaModel.BOOLEAN_CHOICES1 },
		{id: ZaApplianceSSLCert.A_type_self, type: _ENUM_, ref: ZaApplianceSSLCert.A_type_self, choices:ZaModel.BOOLEAN_CHOICES1 },
		{id: ZaApplianceSSLCert.A_type_csr, type: _ENUM_, ref: ZaApplianceSSLCert.A_type_csr, choices:ZaModel.BOOLEAN_CHOICES1 },
		{id: ZaApplianceSSLCert.A_csr_exists, type: _ENUM_, ref: ZaApplianceSSLCert.A_csr_exists, choices:ZaModel.BOOLEAN_CHOICES1 },
		{id: ZaApplianceSSLCert.A_force_new_csr, type: _ENUM_, ref: ZaApplianceSSLCert.A_force_new_csr, choices:ZaModel.BOOLEAN_CHOICES },
		{id:ZaApplianceSSLCert.A_issuer, type:_STRING_, ref:ZaApplianceSSLCert.A_issuer},
		{id:ZaApplianceSSLCert.A_type, type:_STRING_, ref:ZaApplianceSSLCert.A_type}
	]
}

ZaApplianceSSLCert.getWildCardServerName = function (serverName)  {
	var pattern = /^.*(\.[^\.]+\.[^\.]+)$/
	if (serverName) {
		var results =  serverName.match(pattern) ;
		if (results != null) {
			return "*" + results[1] ;
		}
	}
	
	return serverName ;
}

ZaApplianceSSLCert.getCause = 
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