if(console && console.log) console.debug("Loaded zimbra_cert.js");

if (ZaOperation) ZaOperation.INSTALLCERT = ++ZA_OP_INDEX;
ZaItem.CERT = "cert" ;

if(ZaOverviewPanelController.treeModifiers)
	ZaOverviewPanelController.treeModifiers.push(ZaCert.certOvTreeModifier);

ZaZimbraAdmin._CERTS = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._CERTS_SERVER_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;

ZaApp.prototype.getCertViewController =
function(viewId) {
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaCertViewController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.getCertsServerListController =
function() {
	if (this._controllers[ZaZimbraAdmin._CERTS_SERVER_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._CERTS_SERVER_LIST_VIEW] = new ZaCertsServerListController(this._appCtxt, this._container, this);
	}
	return this._controllers[ZaZimbraAdmin._CERTS_SERVER_LIST_VIEW];
}