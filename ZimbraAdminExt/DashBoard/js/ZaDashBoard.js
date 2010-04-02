ZaDashBoard = function() {
	
}
ZaSettings.DASHBOARD_VIEW = "dashboard_view";

ZaApp.prototype.getDashBoardController =
function(viewId) {
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaDashBoardController(this._appCtxt, this._container, this);
		return c ;
	}
}
ZaDashBoard.settingsTab = "settingsTab";
ZaDashBoard.myXModel = {
	    items: [
	       {id:ZaDashBoard.settingsTab,type:_NUMBER_},
	       {id:ZaGlobalConfig.A_zimbraMtaRelayHost, ref:ZaGlobalConfig.A_zimbraMtaRelayHost, type:_LIST_, listItem:{ type: _HOSTNAME_OR_IP_, maxLength: 256 }}
	    ]
};