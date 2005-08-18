/**
* @class ZaStatusView displays status page
* @contructor ZaStatusView
* @param parent
* @param app
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaStatusView(parent, app) {
	this._app = app;
	DwtTabView.call(this, parent, "ZaStatusView");
	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	this._servicesPage = new ZaStatusServicesPage(this, app);
	this.addTab("Services", this._servicesPage);		
	
	this.setScrollStyle(DwtControl.AUTO);
}

ZaStatusView.prototype = new DwtTabView;
ZaStatusView.prototype.constructor = ZaStatusView;

ZaStatusView.prototype.toString = 
function() {
	return "ZaStatusView";
}

