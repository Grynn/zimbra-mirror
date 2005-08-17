/**
* @class ZaGlobalStatsController 
* @contructor ZaGlobalStatsController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
function ZaGlobalStatsController(appCtxt, container, app) {

	ZaController.call(this, appCtxt, container, app);
}

ZaGlobalStatsController.prototype = new ZaController();
ZaGlobalStatsController.prototype.constructor = ZaGlobalStatsController;

ZaGlobalStatsController.STATUS_VIEW = "ZaGlobalStatsController.STATUS_VIEW";

ZaGlobalStatsController.prototype.show = 
function() {
    if (!this._appView) {
		this._contentView = new ZaGlobalStatsView(this._container, this._app);
		this._appView = this._app.createView(ZaGlobalStatsController.STATUS_VIEW, [this._contentView]);
	}
	this._app.pushView(ZaGlobalStatsController.STATUS_VIEW);
	this._app.setCurrentController(this);
}

/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaGlobalStatsController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
}