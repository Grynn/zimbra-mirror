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

ZaMsg.COS_view_title = com_zimbra_dashboard.COS_view_title;
ZaMsg.COSTBB_New_tt = com_zimbra_dashboard.COSTBB_New_tt;
ZaMsg.COSTBB_Edit_tt = com_zimbra_dashboard.COSTBB_Edit_tt;
ZaMsg.COSTBB_Delete_tt = com_zimbra_dashboard.COSTBB_Delete_tt;
ZaMsg.COSTBB_Duplicate_tt = com_zimbra_dashboard.COSTBB_Duplicate_tt;
ZaMsg.COSTBB_Save_tt = com_zimbra_dashboard.COSTBB_Save_tt;
ZaMsg.Search_view_title = com_zimbra_dashboard.Search_view_title;
ZaMsg.NAD_ResetToCOS = com_zimbra_dashboard.NAD_ResetToCOS;
ZaMsg.Domain_DefaultCOS = com_zimbra_dashboard.Domain_DefaultCOS;