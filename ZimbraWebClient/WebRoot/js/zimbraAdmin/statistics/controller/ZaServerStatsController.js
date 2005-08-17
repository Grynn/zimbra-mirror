/**
* @class ZaServerStatsController 
* @contructor ZaServerStatsController
* @param appCtxt
* @param container
* @param app
* @author Greg Solovyev
**/
function ZaServerStatsController(appCtxt, container, app) {

	ZaController.call(this, appCtxt, container, app);
}

ZaServerStatsController.prototype = new ZaController();
ZaServerStatsController.prototype.constructor = ZaServerStatsController;

ZaServerStatsController.STATUS_VIEW = "ZaServerStatsController.STATUS_VIEW";

ZaServerStatsController.prototype.show = 
function(item) {
    if (!this._appView) {
		this._contentView = new ZaServerStatsView(this._container);
		this._appView = this._app.createView(ZaServerStatsController.STATUS_VIEW, [this._contentView]);
	}
	this._app.pushView(ZaServerStatsController.STATUS_VIEW);
	this._app.setCurrentController(this);
	this._contentView.setObject(item);
}

/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaServerStatsController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
}