/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


function AjxWindowOpener () {
	
}

AjxWindowOpener.PARAM_INSTANCE_ID = "id";
AjxWindowOpener.HELPER_URL = "";
AjxWindowOpener.QUESTION = "?";
AjxWindowOpener.EQUALS = "=";
AjxWindowOpener.QS_SEPARATOR = "&";
AjxWindowOpener.PARAM_ASYNC = "async";

AjxWindowOpener.getUrl = 
function (url) {
	if (!url || url == "") return "";

	var fullUrlArray = new Array();
	var idx = 0;
	
	fullUrlArray[idx++] = url;
	fullUrlArray[idx++] = AjxWindowOpener.QUESTION;
	fullUrlArray[idx++] = AjxWindowOpener.PARAM_INSTANCE_ID;
	fullUrlArray[idx++] = AjxWindowOpener.EQUALS;
	fullUrlArray[idx++] = arguments[1];
	fullUrlArray[idx++] = AjxWindowOpener.QS_SEPARATOR;
	fullUrlArray[idx++] = AjxWindowOpener.PARAM_ASYNC;
	fullUrlArray[idx++] = AjxWindowOpener.EQUALS;
	fullUrlArray[idx++] = arguments[2];
	return fullUrlArray.join("");
};

AjxWindowOpener.openBlank = 
function(windowName, windowArgs, openedCallback, callingObject, asyncCallback) {

	return AjxWindowOpener.open(AjxWindowOpener.HELPER_URL, windowName, 
							   windowArgs, openedCallback, 
							   callingObject, asyncCallback);
};

AjxWindowOpener.open = 
function(url, windowName, windowArgs, openedCallback, callingObject, asyncCallback) {
	var newWin;
	if (url && url != "") {
		var objWrapper = { obj: callingObject, callback: openedCallback };
		var async = asyncCallback || false;
		// only assign an id if we think there will be a callback.
		var id = url && url != "" ? AjxCore.assignId(objWrapper) : -1;
		var localUrl = AjxWindowOpener.getUrl(url, id, async);
		newWin = window.open(localUrl, windowName, windowArgs);
		// EMC: This is some magic that I don't understand. For some custom IE browsers 
		// browser, opening a new debug window, seems to call window.open in 
		// an infinite loop. This line seems to prevent that behavior. So what
		// ever you do ....
		// DON'T REMOVE THIS LINE
		window.status = "opening ...";
		objWrapper.window = newWin;
	} else {
		newWin = window.open("", windowName, windowArgs);
		if (openedCallback) {
			var ta = new AjxTimedAction(callingObject, openedCallback);
			AjxTimedAction.scheduleAction(ta, 0);
		}
	}
	
	return newWin
};

AjxWindowOpener.onWindowOpened = 
function (wrapperId) {
	var wrapper = AjxCore.objectWithId(wrapperId);
	AjxCore.unassignId(wrapperId);
	if (!wrapper.window.closed && wrapper.callback) {
		if (wrapper.obj) {
			wrapper.callback.call(wrapper.obj);
		} else {
			wrapper.callback();
		}
	}
};
