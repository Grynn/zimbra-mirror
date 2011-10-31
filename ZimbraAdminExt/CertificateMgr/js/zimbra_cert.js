if(window.console && window.console.log) console.log("Loaded zimbra_cert.js");
if(ZaSettings && ZaSettings.EnabledZimlet["com_zimbra_cert_manager"]){
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
}

if(ZaTabView.XFormModifiers["ZaHomeXFormView"]) {


    ZaHomeXFormView.onInstallCertficate = function (ev) {
        var certServerList = ZaApp.getInstance().getCertsServerListController();
        var serverList = ZaServer.getAll();
        if (serverList.size() > 0) {
            var lastServer = serverList.getVector().getLast();
            var certServerList = ZaApp.getInstance().getCertsServerListController();
            ZaCert.launchNewCertWizard.call (certServerList, lastServer.id) ;
        }
    }

    ZaCert.HomeXFormModifier = function(xFormObject) {
        var setupItem = xFormObject.items[0].items[0].items[5];
        var labelItem = setupItem.headerLabels;
        var contentItem = setupItem.contentItems;
        var index;
        for (var index = 0; index < labelItem.length; index ++ ) {
            if (labelItem[index] == ZaMsg.LBL_HomeGetStared) {
                break;
            }
        }
        if (index != labelItem.length) {
            var content = contentItem[index];
            content[2] = {value:ZaMsg.LBL_HomeInstallCert, onClick: ZaHomeXFormView.onInstallCertficate};
        }

    }

    ZaTabView.XFormModifiers["ZaHomeXFormView"].push(ZaCert.HomeXFormModifier);
}
