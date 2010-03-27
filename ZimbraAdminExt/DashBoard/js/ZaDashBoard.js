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
