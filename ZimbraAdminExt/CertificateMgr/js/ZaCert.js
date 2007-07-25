if (AjxEnv.hasFirebug) console.debug("Loaded ZaCert.js");

function ZaCert (app) {
	ZaItem.call(this, app, "ZaCert");
	this._init(app);
	this.type = ZaItem.CERT ;
}


ZaCert.prototype = new ZaItem ;
ZaCert.prototype.constructor = ZaCert ;

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
