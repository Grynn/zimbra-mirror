function ZaClientCmdHandler(appCtxt) {
	this._appCtxt = appCtxt;
	this._settings = new Object();
}

ZaClientCmdHandler.DBG = new Object();
ZaClientCmdHandler.DBG[0] = AjxDebug.DBG_NONE;
ZaClientCmdHandler.DBG[1] = AjxDebug.DBG1;
ZaClientCmdHandler.DBG[2] = AjxDebug.DBG2;
ZaClientCmdHandler.DBG[3] = AjxDebug.DBG3;

ZaClientCmdHandler.prototype.execute =
function(argv) {
	if (argv[0] && argv[0].toLowerCase() == "debug") {
		if (!argv[1]) return;
		if (argv[1] == "t") {
			var on = DBG._showTiming;
			var newState = on ? "off" : "on";
			alert("Turning debug timing info " + newState);
			DBG.showTiming(!on);
		} else {
			var arg = Number(argv[1]);
			var level = ZaClientCmdHandler.DBG[arg];
			if (level) {
				alert("Setting Debug to level:" + level);
				DBG.setDebugLevel(level);
			} else {
				alert("Invalid debug level");
			}
		}
	} 
}