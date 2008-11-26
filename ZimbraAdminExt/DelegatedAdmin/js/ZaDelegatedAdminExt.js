/**
* This is used to add the extension views to the main admin console UI
*
*
**/


if (AjxEnv.hasFirebug) console.debug("Loaded ZaDelegatedAdminExt.js");

function ZaDelegatedAdminExt () {} ;

ZaSettings.RIGHTS_ENABLED = true ;
ZaSettings.GRANTS_ENABLED = true ;
ZaEvent.S_RIGHT = ZaEvent.EVENT_SOURCE_INDEX++;
ZaEvent.S_GRANT = ZaEvent.EVENT_SOURCE_INDEX++;
ZaItem.RIGHT = "right" ;
ZaItem.GRANT = "grant" ;

ZaZimbraAdmin._RIGHTS = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._RIGHTS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;
ZaZimbraAdmin._GRANTS  = ZaZimbraAdmin.VIEW_INDEX++; 
ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW = ZaZimbraAdmin.VIEW_INDEX++;

if(ZaOverviewPanelController.treeModifiers)  {
	ZaOverviewPanelController.treeModifiers.push(ZaRight.rightsOvTreeModifier);
	ZaOverviewPanelController.treeModifiers.push(ZaGrant.grantsOvTreeModifier);
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


ZaApp.prototype.getGrantViewController =
function(viewId) {
	if (viewId && this._controllers[viewId] != null) {
		return this._controllers[viewId];
	}else{
		var c = this._controllers[viewId] = new ZaGrantViewController(this._appCtxt, this._container, this);
		return c ;
	}
}

ZaApp.prototype.getGlobalGrantListController =
function() {
	if (this._controllers[ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW] == null) {
		this._controllers[ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW] = new ZaGlobalGrantListViewController(this._appCtxt, this._container, this);
	}
	return this._controllers[ZaZimbraAdmin._GLOBAL_GRANTS_LIST_VIEW];
}

