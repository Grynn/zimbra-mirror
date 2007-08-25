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

/**
 * @class
 * This static class serves as a central location for registering and calling
 * public API methods. For example, the registration of a public API method for
 * pulling up the compose form might look something like this:
 * 
 *		AjxDispatcher.register("Compose", "Mail", new AjxCallback(this, this.doAction));
 *
 * and a client call of it like this:
 * 
 *		AjxDispatcher.run("Compose", {action:ZmOperation.NEW_MESSAGE, inNewWindow:false});
 * 
 * Registration will most likely need to happen in a constructor, when 'this' is
 * available, since you'll most likely want the function call to happen in that
 * object's context.
 * 
 * A package can also register a callback to be run once the package has loaded. One use case for
 * that is to register newly-defined classes as drop targets. There is a wrapper method around
 * AjxPackage.require() that will call that method and then run the post-load callback (if any):
 * 
 * 		AjxDispatcher.require("Calendar");
 * 
 * @author Conrad Damon
 */
AjxDispatcher = function() {}

// Table of API names, packages, and associated function calls
AjxDispatcher._registry = {};

// Table of package names and callbacks to run after loading (optional)
AjxDispatcher._package = {};

AjxDispatcher._preLoad	= null;
AjxDispatcher._postLoad	= null;
AjxDispatcher._loadFunctionsEnabled	= false;
AjxDispatcher._timedAction = null;

/**
 * @param pkg		[string]			name of package
 * @param callback	[AjxCallback]		callback to run after package has loaded
 */
AjxDispatcher.setPackageLoadFunction =
function(pkg, callback) {
	AjxDispatcher._package[pkg] = AjxDispatcher._package[pkg] || {};
	AjxDispatcher._package[pkg].callback = callback;
};

AjxDispatcher.setPreLoadFunction =
function(callback) {
	AjxDispatcher._preLoad = callback;
};

AjxDispatcher.setPostLoadFunction =
function(callback) {
	AjxDispatcher._postLoad = callback;
};

AjxDispatcher.enableLoadFunctions =
function(enable) {
	AjxDispatcher._loadFunctionsEnabled = enable;
};

AjxDispatcher.loaded =
function(pkg) {
	return (AjxDispatcher._package[pkg] && AjxDispatcher._package[pkg]._loaded);
};

/**
 * Registers an API method so that it may be called.
 * 
 * @param method	[string]			name of the API method
 * @param pkg		[string]			name of required package(s)
 * @param callback	[AjxCallback]		callback to run for this API call
 */
AjxDispatcher.registerMethod =
function(method, pkg, callback) {
	AjxDispatcher._registry[method] = {pkg:pkg, callback:callback};
};

/**
 * Calls the given API method with the given arguments. It can be passed any
 * number of arguments (provided after the API name), and they will be passed
 * to the function that gets called.
 * 
 * PS: You are in a maze of twisty callbacks, all alike.
 * 
 * @param method		[string]		name of the API method
 * @param async			[boolean]*		if true, load package asynchronously
 * @param callback		[AjxCallback]*	callback to run with results (must be present
 * 										if there are pre- or post-load functions)
 * @param preLoadOk		[boolean]*		if true, okay to run registered pre-load function
 */
AjxDispatcher.run =
function(params /*, arg1 ... argN */) {
	if (!params) { return; }
	var method, noLoad, async, callback, preLoadOk;
	if (typeof(params) == "string") {
		method = params;
        async = false;
		preLoadOk = false;
	} else {
		method = params.method;
		noLoad = params.noLoad;
		callback = params.callback;
        async = params.async != null ? params.async : Boolean(callback);
		preLoadOk = params.preLoadOk != null ? params.preLoadOk : (callback != null);
	}
	var item = AjxDispatcher._registry[method];
	if (!item) {
		// method hasn't been registered
		AjxPackage.__log("API method '" + method + "' not found");
		return;
	}
	AjxPackage.__log("Run method: " + method);
	var pkg = item.pkg;
	var args = [];
	for (var i = 1; i < arguments.length; ++i) {
		args.push(arguments[i]);
	}
	if (callback) {
		args.push(callback);
	}
	
	return AjxDispatcher.require(pkg, async, item.callback, args, preLoadOk);
};

/**
 * Loads the given package, and runs its requested post-load callback.
 * 
 * @param pkg				[string]		name of the API method
 * @param async				[boolean]*		if true, load package asynchronously
 * @param callback			[AjxCallback]*	callback to run after pkg load
 * @param args				[array]*		args to pass to callback
 * @param preLoadOk			[boolean]*		if true, okay to run registered pre-load function
 */
AjxDispatcher.require =
function(pkg, async, callback, args, preLoadOk) {
	if (!pkg) { return; }
	
	if (typeof(pkg) == "string") {
		pkg = [pkg];
	}
	var unloaded = [];
	for (var i = 0; i < pkg.length; i++) {
		var p = pkg[i];
		AjxDispatcher._package[p] = AjxDispatcher._package[p] || {};
		if (!AjxDispatcher._package[p]._loaded) {
			unloaded.push(p);
		}
	}
	if (unloaded.length == 0) {
		return AjxDispatcher._postLoadCallback(pkg, false, callback, args);
	} else {
		// need callback in order to run pre-load function
		if (preLoadOk && AjxDispatcher._loadFunctionsEnabled && AjxDispatcher._preLoad) {
			AjxPackage.__log("pre-load function");
			AjxDispatcher._timedAction = new AjxCallback(null, AjxDispatcher._continueRequire, [unloaded, async, callback, args]);
			AjxDispatcher._preLoad.run();
			window.setTimeout('AjxDispatcher._timedAction.run()', 0);
		} else {
			return AjxDispatcher._continueRequire(unloaded, async, callback, args);
		}
	}
};

AjxDispatcher._continueRequire =
function(pkg, async, callback, args) {
	var pkgString = pkg.join(", ");
	AjxPackage.__log("------------------------------------- Loading package: " + pkgString);
	if (async && callback) {
		var postLoadCallback = new AjxCallback(null, AjxDispatcher._postLoadCallback, [pkg, true, callback, args]);
		AjxPackage.require({name:pkg, callback:postLoadCallback});
	} else {
		var _st = new Date();
		for (var i = 0; i < pkg.length; i++) {
			AjxPackage.require(pkg[i]);
		}
		var _en = new Date();
		var t = _en.getTime() - _st.getTime();
		AjxPackage.__log("LOAD TIME for " + pkgString + ": " + t);

		return AjxDispatcher._postLoadCallback(pkg, true, callback, args);
	}
};

AjxDispatcher._postLoadCallback =
function(pkg, pkgWasLoaded, callback, args) {
    for (var i = 0; i < pkg.length; i++) {
        var pkgData = AjxDispatcher._package[pkg[i]];
        pkgData._loaded = true;
    }
    for (var i = 0; i < pkg.length; i++) {
		var pkgData = AjxDispatcher._package[pkg[i]];
		if (pkgWasLoaded && pkgData.callback && !pkgData.callbackDone) {
			pkgData.callbackDone = true;
			AjxPackage.__log("Running post-load package function for " + pkg[i]);
			pkgData.callback.run();
		}
	}
	if (pkgWasLoaded) {
		if (AjxDispatcher._loadFunctionsEnabled && AjxDispatcher._postLoad) {
			AjxDispatcher._postLoad.run();
		}
	}
	
	if (callback) {
 		return callback.run1(args);
	}
};
