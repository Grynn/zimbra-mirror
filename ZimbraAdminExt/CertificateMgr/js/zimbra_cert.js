if (AjxEnv.hasFirebug) console.debug("Loaded zimbra_cert.js");

if (ZaOperation) ZaOperation.INSTALLCERT = ++ZA_OP_INDEX;
ZaItem.CERT = "cert" ;

if(ZaOverviewPanelController.treeModifiers)
	ZaOverviewPanelController.treeModifiers.push(ZaCert.certOvTreeModifier);

ZaZimbraAdmin._CERTS = ZaZimbraAdmin.VIEW_INDEX++;

ZaApp.prototype.getCertViewController =
function() {
	if (this._controllers[ZaZimbraAdmin._CERTS] == null)
		this._controllers[ZaZimbraAdmin._CERTS] = 
				new ZaCertViewController(this._appCtxt, this._container, this);
	return this._controllers[ZaZimbraAdmin._CERTS];
}