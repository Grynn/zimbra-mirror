function ZaStatusToolBar(parent) {

	ZaToolBar.call(this, parent, null, null);

	this._createButton(ZaStatusToolBar.REFRESH_BUTTON, ZaImg.I_UNDO, ZaMsg.TBB_Refresh, null, ZaMsg.TBB_Refresh_tt, true);
	this._createSeparator();

	this._createButton(ZaStatusToolBar.BACK_BUTTON, ZaImg.I_BACK_ARROW, null,
	                            ZaImg.ID_BACK_ARROW, ZaMsg.Back, true);

	this._createButton(ZaStatusToolBar.FORWARD_BUTTON, ZaImg.I_FORWARD_ARROW, null,
	                            ZaImg.ID_FORWARD_ARROW, ZaMsg.Forward, true);

	this._createSeparator();
}

ZaStatusToolBar.REFRESH_BUTTON = 1;
ZaStatusToolBar.BACK_BUTTON = 2;
ZaStatusToolBar.FORWARD_BUTTON = 3;

ZaStatusToolBar.VIEW_DATA = "ZaStatusToolBar.VIEW";

ZaStatusToolBar.prototype = new ZaToolBar;
ZaStatusToolBar.prototype.constructor = ZaStatusToolBar;

ZaStatusToolBar.prototype.toString = 
function() {
	return "ZaStatusToolBar";
}
