/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */


AjxWindowOpener = function() {
};


// consts used by frameOpenerHelper.jsp
AjxWindowOpener.HELPER_URL = "";
AjxWindowOpener.PARAM_INSTANCE_ID = "id";
AjxWindowOpener.PARAM_ASYNC = "async";


AjxWindowOpener.openBlank =
function(name, args, callback, async) {

	return AjxWindowOpener.open(AjxWindowOpener.HELPER_URL, name, args, callback, async);
};

AjxWindowOpener.open =
function(url, name, args, callback, async) {
	var newWin;
	if (url && url != "") {
		var async = async === true;
		var wrapper = { callback: callback };
		var id = AjxCore.assignId(wrapper);
		var localUrl = url && url != ""
			? (url + "?id=" + id + "&async=" + async)
			: "";
		newWin = wrapper.window = window.open(localUrl, name, args);
	} else {
		newWin = window.open("", name, args);
		if (callback) {
			var ta = new AjxTimedAction(callback.obj, callback.func, callback.args);
			AjxTimedAction.scheduleAction(ta, 0);
		}
	}
	
	return newWin
};

AjxWindowOpener.onWindowOpened = 
function(wrapperId) {
	var wrapper = AjxCore.objectWithId(wrapperId);
	AjxCore.unassignId(wrapperId);

	if (!wrapper.window.closed && wrapper.callback) {
		wrapper.callback.run();
	}
};
