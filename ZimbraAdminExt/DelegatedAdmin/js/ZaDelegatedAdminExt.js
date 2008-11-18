/**
* This is used to add the extension views to the main admin console UI
*
*
**/


if (AjxEnv.hasFirebug) console.debug("Loaded ZaDelegatedAdminExt.js");

function ZaDelegatedAdminExt () {} ;

ZaSettings.RIGHTS_ENABLED = true ;
ZaEvent.S_RIGHT = ZaEvent.EVENT_SOURCE_INDEX++;
ZaItem.RIGHT = "right" ;

ZaZimbraAdmin._RIGHTS = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._RIGHTS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;

if(ZaOverviewPanelController.treeModifiers)  {
	ZaOverviewPanelController.treeModifiers.push(ZaRight.rightsOvTreeModifier);
}

ZaApp.prototype.getRightViewController =
function(viewId) {
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaRightViewController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.getRightsListController =
function() {
	if (this._controllers[ZaZimbraAdmin._RIGHTS_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._RIGHTS_LIST_VIEW] = new ZaRightsListViewController(this._appCtxt, this._container, this);
	}
	return this._controllers[ZaZimbraAdmin._RIGHTS_LIST_VIEW];
}
