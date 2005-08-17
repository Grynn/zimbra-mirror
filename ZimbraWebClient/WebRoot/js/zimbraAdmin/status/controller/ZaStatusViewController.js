/**
* @class ZaStatusViewController 
* @contructor ZaStatusViewController
* @param appCtxt
* @param container
* @param app
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaStatusViewController(appCtxt, container, app) {

	ZaController.call(this, appCtxt, container, app);
}

ZaStatusViewController.prototype = new ZaController();
ZaStatusViewController.prototype.constructor = ZaStatusViewController;

ZaStatusViewController.STATUS_VIEW = "ZaStatusViewController.STATUS_VIEW";

ZaStatusViewController.prototype.show = 
function() {
    if (!this._appView) {
//		this._toolbar = new ZaStatusToolBar(this._container);
		this._contentView = new ZaStatusView(this._container, this._app);
		this._appView = this._app.createView(ZaStatusViewController.STATUS_VIEW, [this._contentView]);
	}
	this._app.pushView(ZaStatusViewController.STATUS_VIEW);
	this._app.setCurrentController(this);
}


/**
* @param nextViewCtrlr - the controller of the next view
* Checks if it is safe to leave this view. Displays warning and Information messages if neccesary.
**/
ZaStatusViewController.prototype.switchToNextView = 
function (nextViewCtrlr, func, params) {
	func.call(nextViewCtrlr, params);
}