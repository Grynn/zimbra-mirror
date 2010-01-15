/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
/**
* @param app {ZaApp}
**/
ZaClientCmdHandler = function() {
	this._settings = new Object();
}

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
			//var arg = Number(argv[1]);
			var level =argv[1];
			if (level) {
				alert("Setting Debug to level:" + level);
				DBG.setDebugLevel(level);
			} else {
				alert("Invalid debug level");
			}
		}
	} else if (argv[0] && argv[0].toLowerCase() == "domainsrch") {
		ZaDomain.MAXSEARCHRESULTS = argv[1];
		alert("Setting domain search limit to:" + argv[1]);
        ZaApp.getInstance().searchDomains();
    } else if (argv[0] && argv[0].toLowerCase() == "pagesize") {
		ZaSettings.RESULTSPERPAGE = argv[1];
		alert("Setting page size to:" + argv[1]);
    }
}
