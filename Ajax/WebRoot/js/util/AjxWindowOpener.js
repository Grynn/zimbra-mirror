function LsWindowOpener () {
	
}

LsWindowOpener.PARAM_INSTANCE_ID = "id";
LsWindowOpener.HELPER_URL = "";
LsWindowOpener.QUESTION = "?";
LsWindowOpener.EQUALS = "=";
LsWindowOpener.QS_SEPARATOR = "&";
LsWindowOpener.PARAM_ASYNC = "async";

LsWindowOpener.getUrl = 
function (url) {
	if (!url || url == "") return "";

	var fullUrlArray = new Array();
	var idx = 0;
	
	fullUrlArray[idx++] = url;
	fullUrlArray[idx++] = LsWindowOpener.QUESTION;
	fullUrlArray[idx++] = LsWindowOpener.PARAM_INSTANCE_ID;
	fullUrlArray[idx++] = LsWindowOpener.EQUALS;
	fullUrlArray[idx++] = arguments[1];
	fullUrlArray[idx++] = LsWindowOpener.QS_SEPARATOR;
	fullUrlArray[idx++] = LsWindowOpener.PARAM_ASYNC;
	fullUrlArray[idx++] = LsWindowOpener.EQUALS;
	fullUrlArray[idx++] = arguments[2];
	return fullUrlArray.join("");
};

LsWindowOpener.openBlank = 
function(windowName, windowArgs, openedCallback, callingObject, asyncCallback) {

	return LsWindowOpener.open(LsWindowOpener.HELPER_URL, windowName, 
							   windowArgs, openedCallback, 
							   callingObject, asyncCallback);
};

LsWindowOpener.open = 
function(url, windowName, windowArgs, openedCallback, callingObject, asyncCallback) {
	var newWin;
	if (url && url != "") {
		var objWrapper = { obj: callingObject, callback: openedCallback };
		var async = asyncCallback || false;
		// only assign an id if we think there will be a callback.
		var id = url && url != "" ? LsCore.assignId(objWrapper) : -1;
		var localUrl = LsWindowOpener.getUrl(url, id, async);
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
			var ta = new LsTimedAction();
			ta.obj = callingObject;
			ta.method = openedCallback;
			LsTimedAction.scheduleAction(ta, 0);
		}
	}
	
	return newWin
};

LsWindowOpener.onWindowOpened = 
function (wrapperId) {
	var wrapper = LsCore.objectWithId(wrapperId);
	LsCore.unassignId(wrapperId);
	if (!wrapper.window.closed && wrapper.callback) {
		if (wrapper.obj) {
			wrapper.callback.call(wrapper.obj);
		} else {
			wrapper.callback();
		}
	}
};
