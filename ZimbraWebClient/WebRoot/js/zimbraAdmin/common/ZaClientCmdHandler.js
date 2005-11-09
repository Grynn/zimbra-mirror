/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

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