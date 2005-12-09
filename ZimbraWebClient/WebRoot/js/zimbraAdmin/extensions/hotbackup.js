function ZaHotBackup() {

}
if(ZaGlobalConfig) {
	ZaGlobalConfig.A_zimbraComponentAvailable_hotbackup = "_"+ZaGlobalConfig.A_zimbraComponentAvailable+"_hotbackup";
}

ZaHotBackup.initToolbar = function () {
	//TODO: Move this code to an external file
	if(ZaSettings.ACCOUNTS_RESTORE_ENABLED) {
		var globalConf = this._app.getGlobalConfig();

    	if(globalConf && globalConf.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_hotbackup])
			this._toolbarOperations.push(new ZaOperation(ZaOperation.MAIL_RESTORE, ZaMsg.TBB_RestoreMailbox, ZaMsg.ACTBB_Restore_tt, "RestoreMailbox", "RestoreMailboxDis", new AjxListener(this, ZaAccountListController.prototype._restoreMailListener)));		
	}
}

if(ZaController.initToolbarMethods["ZaAccountListController"]) {
	ZaController.initToolbarMethods["ZaAccountListController"].push(ZaHotBackup.initToolbar);
}