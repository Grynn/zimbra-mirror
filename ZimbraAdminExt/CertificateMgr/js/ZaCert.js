if (AjxEnv.hasFirebug) console.debug("Loaded ZaCert.js");

function ZaCert () {
	ZaItem.call(this,  "ZaCert");
	this._init();
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
//ZaCert.A_allserver = "allserver" ;
ZaCert.A_subject = "subject" ;
ZaCert.A_type = "type" ;
ZaCert.A_type_self = "self" ;
ZaCert.A_type_comm = "comm" ;
ZaCert.A_type_csr = "csr" ; //generate the csr only
ZaCert.A_csr_exists = "csr_exists" ;
ZaCert.A_keysize = "keysize" ;
ZaCert.A_force_new_csr = "force_new_csr" ; //only matters when the csr exists
ZaCert.A_target_server = "target_server" ;
ZaCert.A_subject_alt = "SubjectAltName";
ZaCert.A_use_wildcard_server_name = "user_wildcard_server_name";

ZaCert.ALL_SERVERS = "--- All Servers ---" ; //Don't modify it, it need to be consistent with server side value

ZaCert.TARGET_SERVER_CHOICES =  [
		{label:com_zimbra_cert_manager.lb_ALL_SERVERS, value: ZaCert.ALL_SERVERS }
		/*,
		{label: "test1.zimbra.com", value: "test1.zimbra.com" },
		{label: "test2.zimbra.com", value: "test2.zimbra.com" },
		{label: "admindev2.zimbra.com", value: "admindev2.zimbra.com" }*/
	];

ZaCert.KEY_SIZE_CHOICES = [ {label: "1024", value: "1024"},
                            {label: "2048", value: "2048"}] ;

/* All the cert rights require the system admin rights, so we have no need to expose the view for delegated admin
if(ZaSettings) {
	ZaSettings.CERTS_VIEW = "certsView";
	ZaSettings.ALL_UI_COMPONENTS.push({ value: ZaSettings.CERTS_VIEW, label: com_zimbra_cert_manager.UI_Comp_certsView });
	ZaSettings.OVERVIEW_TOOLS_ITEMS.push(ZaSettings.CERTS_VIEW);
}*/

//Init the ZaCert Object for the new Cert wizard
ZaCert.prototype.init = function (getCSRResp) {
	// 1. Check if CSR is generated, set the csr_exists = true 
	this.attrs = {};
	this.attrs [ZaCert.A_subject_alt] = [];
	this [ZaCert.A_type_self]  = true ;
	this [ZaCert.A_type_comm] = false ;
	this [ZaCert.A_type_csr] = false ;
    this [ZaCert.A_keysize] = "2048" ;
	this.initCSR(getCSRResp) ;
	this [ZaCert.A_validation_days] = ZaCert.DEFAULT_VALIDATION_DAYS ;
	this [ZaCert.A_force_new_csr]  = 'FALSE';
}

ZaCert.prototype.initCSR = function (getCSRResp) {
	if (getCSRResp) {
		var csr_exists = getCSRResp [ZaCert.A_csr_exists];
		if ( csr_exists && csr_exists == "1") {
			this[ZaCert.A_csr_exists] = true ;
		}else{
			this[ZaCert.A_csr_exists] = false ;
		}
		
		/*
		var isComm = getCSRResp ["isComm"];
		if (isComm && isComm == "1") {
			this [ZaCert.A_type_self]  = false ;
			this [ZaCert.A_type_comm] = true ;
			this [ZaCert.A_type_csr] = false ;
		}else{
			this [ZaCert.A_type_self]  = true ;
			this [ZaCert.A_type_comm] = false ;
			this [ZaCert.A_type_csr] = false ;
		}*/
		
		for (var key in getCSRResp) {
			var value = getCSRResp[key] ;
			if (value instanceof Array) {
				//array attributes
				if ((key == ZaCert.A_subject_alt) || (value.length > 1)) {
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
ZaCert.prototype.modifySubjectAltNames = function () {
	if (AjxEnv.hasFirebug) console.log("Enter ZaCert.prototype.modifySubjectAltNames ");
	
	var currentSubjAltNames = this.attrs[ZaCert.A_subject_alt] ;
	// only modify when a new CSR should be created.
	if ((!this[ZaCert.A_csr_exists]) || (this[ZaCert.A_force_new_csr] == 'TRUE')) { 
		if (AjxEnv.hasFirebug) console.log("Modifying SubjectAltNames ");
		if (this[ZaCert.A_target_server] == ZaCert.ALL_SERVERS) {
			for (var i=0; i < ZaCert.TARGET_SERVER_CHOICES.length; i ++) {
				if ((ZaCert.TARGET_SERVER_CHOICES[i].value != ZaCert.ALL_SERVERS) && //Not All Servers Value
				//the target server name doesn't exist in the current subjectAltName
				   (ZaUtil.findValueInArray(currentSubjAltNames, ZaCert.TARGET_SERVER_CHOICES[i].label) == -1)){
					
					//add this target server value to subject alt names
					if (AjxEnv.hasFirebug) console.log("Adding " + ZaCert.TARGET_SERVER_CHOICES[i].label);
					currentSubjAltNames.push(ZaCert.TARGET_SERVER_CHOICES[i].label);
				}			
			}
		}else{
			var targetServerName ;
			for (var i=0; i < ZaCert.TARGET_SERVER_CHOICES.length; i ++) {
				if (ZaCert.TARGET_SERVER_CHOICES[i].value == this[ZaCert.A_target_server]){
					targetServerName = ZaCert.TARGET_SERVER_CHOICES[i].label ;
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

ZaCert.certOvTreeModifier = function (tree) {
	var overviewPanelController = this ;
	if (!overviewPanelController) throw new Exception("ZaCert.certOvTreeModifier: Overview Panel Controller is not set.");
	
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
					ZaCert.TARGET_SERVER_CHOICES.push (
						{label: serverList[ix].name, value: serverList[ix].id }
					);
				}
			}
		} catch (ex) {
			overviewPanelController._handleException(ex, "ZaCert.certOvTreeModifier", null, false);
		}
		
		if(ZaOverviewPanelController.overviewTreeListeners) {
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._CERTS_SERVER_LIST_VIEW] = ZaCert.certsServerListTreeListener;		
			ZaOverviewPanelController.overviewTreeListeners[ZaZimbraAdmin._CERTS] = ZaCert.certsServerNodeTreeListener;
		}
	}
}
                         
//When the certs tree item is clicked
ZaCert.certsServerListTreeListener = function (ev) {
	if (AjxEnv.hasFirebug) console.log("Show the server lists ...") ;
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(
			ZaApp.getInstance().getCertsServerListController(),ZaCertsServerListController.prototype.show, ZaServer.getAll());
	} else {					
		ZaApp.getInstance().getCertsServerListController().show(ZaServer.getAll());
	}
}

//When the individul server node under the certs tree item is clicked
ZaCert.certsServerNodeTreeListener = function (ev) {
	var serverNodeId = ev.item.getData(ZaOverviewPanelController._OBJ_ID) ;
	if (AjxEnv.hasFirebug) console.log("Click the server node: " + serverNodeId) ;
	
	if(ZaApp.getInstance().getCurrentController()) {
		ZaApp.getInstance().getCurrentController().switchToNextView(
			ZaApp.getInstance().getCertViewController(),
			ZaCertViewController.prototype.show,
			[ZaCert.getCerts(ZaApp.getInstance(), serverNodeId), serverNodeId]);
	} else {
		ZaApp.getInstance().getCertViewController().show(
			ZaCert.getCerts(ZaApp.getInstance(), serverNodeId),
			serverNodeId);
	}
}

ZaCert.getCerts = function (app, serverId) {
	if (AjxEnv.hasFirebug) console.log("Getting certificates for server " + serverId) ;
	
	var soapDoc = AjxSoapDoc.create("GetCertRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("type", "all");
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	if (serverId != null) {
		soapDoc.getMethod().setAttribute("server", serverId);
	}else{
		if (AjxEnv.hasFirebug) console.log("Warning: serverId is missing for ZaCert.getCerts.") ;
	}
	
	try {
		var reqMgrParams = {} ;
		reqMgrParams.controller = app.getCurrentController();
		reqMgrParams.busyMsg = com_zimbra_cert_manager.BUSY_RETRIEVE_CERT ;
		resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GetCertResponse;
		return resp;
	}catch (ex) {
		app.getCurrentController()._handleException(ex, "ZaCert.getCerts", null, false);
	}
}

ZaCert.getCSR = function (app, serverId, type) {
	if (AjxEnv.hasFirebug) console.log("ZaCert.getCSR: Getting CSR for server: " + serverId) ;
	
	var soapDoc = AjxSoapDoc.create("GetCSRRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("type", type);
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	
	//if (serverId && serverId != ZaCert.ALL_SERVERS) {
	if (serverId != null){
		soapDoc.getMethod().setAttribute("server", serverId);
	}else{
		if (AjxEnv.hasFirebug) console.log("Warning: serverId is missing for ZaCert.getCSR.") ;
	}
	
	try {
		var reqMgrParams = {} ;
		reqMgrParams.controller = app.getCurrentController();
		reqMgrParams.busyMsg = com_zimbra_cert_manager.BUSY_GET_CSR ;
		resp = ZaRequestMgr.invoke(csfeParams, reqMgrParams ).Body.GetCSRResponse;
		return resp;	
	}catch (ex) {
		app.getCurrentController()._handleException(ex, "ZaCert.getCSR", null, false);
	}
}

ZaCert.genCSR = function (app, subject_attrs,  type, newCSR, serverId, keysize) {
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
		if (AjxEnv.hasFirebug) console.log("Warning: serverId is missing for ZaCert.genCSR.") ;
	}
	
	for (var n in subject_attrs) {
		if (n == ZaCert.A_subject_alt) {
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
		app.getCurrentController()._handleException(ex, "ZaCert.genCSR", null, false);
	}
}

ZaCert.installCert = function (app, params, serverId) {
	if (AjxEnv.hasFirebug) console.log("Installing certificates") ;
	var type = params.type ;
	var comm_cert = params.comm_cert ;
	var validation_days = params.validation_days ;
	var callback = params.callback ;
    var subject = params.subject ;
    var keysize = params.keysize ;
    //var allserver = 0 || params.allserver ;
	//if (AjxEnv.hasFirebug) console.log("allserver = " + allserver) ;
	
	var controller = app.getCurrentController();
	
	var certView = controller._contentView ;
	certView._certInstallStatus.setStyle (DwtAlert.INFORMATION) ;
	certView._certInstallStatus.setContent(com_zimbra_cert_manager.CERT_INSTALLING );
	certView._certInstallStatus.setDisplay(Dwt.DISPLAY_BLOCK) ;
	
	var soapDoc = AjxSoapDoc.create("InstallCertRequest", "urn:zimbraAdmin", null);
	soapDoc.getMethod().setAttribute("type", type);
	if (serverId != null) {
		soapDoc.getMethod().setAttribute("server", serverId);
	}else{
		if (AjxEnv.hasFirebug) console.log("Warning: serverId is missing for ZaCert.installCert.") ;
	}
	
	if (type == ZaCert.A_type_self || type == ZaCert.A_type_comm) {
		soapDoc.set(ZaCert.A_validation_days, validation_days);

		//soapDoc.set(ZaCert.A_allserver, allserver) ;
		if (type == ZaCert.A_type_comm) {
			//set the comm_cert element
			soapDoc.set("comm_cert", comm_cert);	
		}else if (type == ZaCert.A_type_self) {
            soapDoc.set(ZaCert.A_keysize, keysize) ;
        }
        //add the subject element and subjectAltNames element
        if (subject != null) {
            var subject_attrs = {} ;
            for (var n in subject) {
                if (n == ZaCert.A_subject_alt) {
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
		throw new AjxException (com_zimbra_cert_manager.UNKNOW_INSTALL_TYPE_ERROR, "ZaCert.installCert") ;		
	}
	
	var csfeParams = new Object();
	csfeParams.soapDoc = soapDoc;	
	var reqMgrParams = {} ;
	reqMgrParams.controller = app.getCurrentController();
	reqMgrParams.busyMsg = com_zimbra_cert_manager.BUSY_INSTALL_CERT ;
	if (callback) {
		csfeParams.callback = callback;
		csfeParams.asyncMode = true ;	
	}
	ZaRequestMgr.invoke(csfeParams, reqMgrParams ) ;
}

ZaCert.prototype.setTargetServer = function (serverId) {
		this[ZaCert.A_target_server] = serverId ;
}

ZaCert.launchNewCertWizard = function (serverId) {
	try {
		if(!ZaApp.getInstance().dialogs["certInstallWizard"])
			ZaApp.getInstance().dialogs["certInstallWizard"] = new ZaCertWizard (this._container);	
		
		this._cert = new ZaCert(ZaApp.getInstance());
		this._cert.setTargetServer (serverId);		
		this._cert.init() ;
		ZaApp.getInstance().dialogs["certInstallWizard"].setObject(this._cert);
		ZaApp.getInstance().dialogs["certInstallWizard"].popup();
	} catch (ex) {
		this._handleException(ex, "ZaCert.launchNewCertWizard", null, false);
	}
}

ZaCert.myXModel = {
	items: [
		{id: ZaCert.A_installStatus, type: _STRING_, ref: ZaCert.A_installStatus },
		{id: ZaCert.A_subject_alt, type: _LIST_, ref:"attrs/" + ZaCert.A_subject_alt, listItem:{type:_STRING_}},
		{id: ZaCert.A_target_server, type:_STRING_ , ref: ZaCert.A_target_server },
		{id: ZaCert.A_countryName, type: _STRING_, ref: "attrs/" + ZaCert.A_countryName, length: 2, pattern: /^\s*[a-zA-Z0-9\/\.\-\\_:\@\=\'\*]*$/ },
        {id: ZaCert.A_keysize, type: _STRING_, ref: ZaCert.A_keysize},
		{id: ZaCert.A_commonName, type: _STRING_, ref: "attrs/" + ZaCert.A_commonName },
		{id: ZaCert.A_state, type: _STRING_, ref: "attrs/" + ZaCert.A_state, pattern: /^\s*[a-zA-Z0-9\/\.\-\\_:\@\=\'\*]*$/ },
		{id: ZaCert.A_city, type: _STRING_, ref: "attrs/" + ZaCert.A_city, pattern: /^\s*[a-zA-Z0-9\/\.\-\\_:\@\=\'\*\s]*$/ },
		{id: ZaCert.A_organization, type: _STRING_, ref: "attrs/" + ZaCert.A_organization, pattern: /^\s*[a-zA-Z0-9\/\.\-\\_:\@\=\,\'\*\s]*$/ },
		{id: ZaCert.A_organizationUnit, type: _STRING_, ref: "attrs/" + ZaCert.A_organizationUnit, pattern: /^\s*[a-zA-Z0-9\/\.\-\\_:\@\=\,\'\*\s]*$/ },
		{id: ZaCert.A_validation_days, type: _NUMBER_, ref: ZaCert.A_validation_days, required: true },
		{id: ZaCert.A_type_comm, type: _ENUM_, ref: ZaCert.A_type_comm, choices:ZaModel.BOOLEAN_CHOICES1 },
		{id: ZaCert.A_type_self, type: _ENUM_, ref: ZaCert.A_type_self, choices:ZaModel.BOOLEAN_CHOICES1 },
		{id: ZaCert.A_type_csr, type: _ENUM_, ref: ZaCert.A_type_csr, choices:ZaModel.BOOLEAN_CHOICES1 },
		{id: ZaCert.A_csr_exists, type: _ENUM_, ref: ZaCert.A_csr_exists, choices:ZaModel.BOOLEAN_CHOICES1 },
		{id: ZaCert.A_force_new_csr, type: _ENUM_, ref: ZaCert.A_force_new_csr, choices:ZaModel.BOOLEAN_CHOICES }
	]
}

ZaCert.getWildCardServerName = function (serverName)  {
	var pattern = /^.*(\.[^\.]+\.[^\.]+)$/
	if (serverName) {
		var results =  serverName.match(pattern) ;
		if (results != null) {
			return "*" + results[1] ;
		}
	}
	
	return serverName ;
}
