/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * This class is a collection of functions for defining packages and
 * loading them dynamically.
 * 
 * @author Andy Clark
 * 
 * @private
 */
AjxPackage = function() {}

//
// Constants
//

/**
 * Defines the "XHR SYNC" method.
 */
AjxPackage.METHOD_XHR_SYNC = "xhr-sync";
/**
 * Defines the "XHR ASYNC" method.
 */
AjxPackage.METHOD_XHR_ASYNC = "xhr-async";
/**
 * Defines the "SCRIPT TAG" method.
 */
AjxPackage.METHOD_SCRIPT_TAG = "script-tag";

AjxPackage.DEFAULT_SYNC = AjxPackage.METHOD_XHR_SYNC;
AjxPackage.DEFAULT_ASYNC = AjxEnv.isIE ? AjxPackage.METHOD_XHR_ASYNC : AjxPackage.METHOD_SCRIPT_TAG;

//
// Data
//

AjxPackage._packages = {};
AjxPackage._extension = ".js";

AjxPackage.__depth = 0;
AjxPackage.__scripts = [];
AjxPackage.__data = {};

//
// Static functions
//

/**
 * Sets the base path.
 * 
 * @param	{string}	basePath		the base path
 */
AjxPackage.setBasePath = function(basePath) {
    AjxPackage._basePath = basePath;
};
/**
 * Sets the extension.
 * 
 * @param	{string}	extension		the extension
 */
AjxPackage.setExtension = function(extension) {
    AjxPackage._extension = extension;
};
/**
 * Sets the query string.
 * 
 * @param	{string}	queryString		the query string
 */
AjxPackage.setQueryString = function(queryString) {
    AjxPackage._queryString = queryString;
};

/**
 * Checks if the specified package has been defined.
 * 
 * @param	{string}	name		the package name
 * @return	{boolean}	<code>true</code> if the package is defined
 */
AjxPackage.isDefined = function(name) {
	return Boolean(AjxPackage._packages[name]);
};

/**
 * Defines a package and returns true if this is the first definition.
 * 
 * @param	{string}	name		the package name
 * @return	{boolean}	<code>true</code> if this is the first package definition
 */
AjxPackage.define = function(name) {
    AjxPackage.__log("DEFINE "+name, "font-weight:bold;font-style:italic");
    name = AjxPackage.__package2path(name);
    if (!AjxPackage._packages[name]) {
        AjxPackage._packages[name] = true;
        return true;
    }
    return false;
};

/**
 * Undefines a package.
 * 
 * @param	{string}	name		the package name
 */
AjxPackage.undefine = function(name) {
    AjxPackage.__log("UNDEFINE "+name, "font-weight:bold;font-style:italic");
    name = AjxPackage.__package2path(name);
    if (AjxPackage._packages[name]) {
        delete AjxPackage._packages[name];
    }
};

/**
 * This function ensures that the specified module is loaded and available
 * for use. If already loaded, this function returns immediately. If not,
 * then this function will load the necessary code, either synchronously
 * or asynchronously depending on whether the <tt>callback</tt> or
 * <tt>forceSync</tt> parameters are specified.
 * <p>
 * It can be called with either a package name string or a parameters object.
 *
 * @param	{hash}		nameOrParams		a hash of parameters
 * @param {string}	name      		the package name
 * @param {string}	[basePath]	the base path of URL to load. If
 *                                  not specified, uses the global base path.
 * @param {string}	[extension] 	the filename extension of URL to
 *                                  load. If not specified, uses the global
 *                                  filename extension.
 * @param {string}	[queryString] the query string appended to URL.
 *                                  If not specified, uses the global query
 *                                  string.
 * @param {string}	[userName]  The username of the request
 * @param {string}	[password]  The password of the request
 * @param {AjxCallback}	[callback] the callback to run
 * @param {constant}	[method]    	the loading method for the package (see <code>METHOD_*</code> constants)
 * @param {boolean}	[forceSync] 	overrides the load mode (if
 *                                  this method is called during an async
 *                                  load) and forces the requested package to
 *                                  be loaded synchronously.
 * @param {boolean}	[forceReload=false]    specifies whether the package is reloaded even if already defined
 */
AjxPackage.require = function(nameOrParams) {
    var params = nameOrParams;
    if (typeof nameOrParams == "string") {
        params = { name: nameOrParams };
    }

    // is an array of names specified?
    var array = params.name;
    if (array instanceof Array) {
        // NOTE: This is to avoid a silent problem: when the caller expects
        //       the array of names to be left unchanged upon return. Because
        //       we call <code>shift</code> on the array, it modifies the
        //       original list so the caller would see an empty array after
        //       calling this function.
        if (!array.internal) {
            array = [].concat(array);
            array.internal = true;
            params.name = array;
        }

        var name = array.shift();

        // if more names, use callback to trigger next
        if (array.length > 0) {
            var ctor = new Function();
            ctor.prototype = params;
            ctor.prototype.constructor = ctor;

            var nparams = new ctor();
            nparams.name = name;
            nparams.callback = new AjxCallback(null, AjxPackage.__requireNext, params);

            AjxPackage.require(nparams);
            return;
        }

        // continue
        params.name = name;
    }

    // see if it's already loaded
    var oname = params.name;
    var name = AjxPackage.__package2path(oname);

    var callback = params.callback;
    if (typeof callback == "function") {
        callback = new AjxCallback(callback);
    }
    var cb = callback ? " (callback)" : "";
    var loaded = AjxPackage._packages[name] ? " LOADED" : "";
    var mode = AjxPackage.__scripts.length ? " (async, queueing...)" : "";
    AjxPackage.__log(["REQUIRE \"",oname,"\"",cb,loaded,mode].join(""));

    var reload = params.forceReload != null ? params.forceReload : false;
    if (AjxPackage._packages[name] && !reload) {
        if (callback) {
            callback.run();
        }
        return;
    }

    // assemble load url
    var basePath = params.basePath || AjxPackage._basePath || window.contextPath;
    var extension = params.extension || AjxPackage._extension;
    var queryString = params.queryString || AjxPackage._queryString;

    var pathParts = [basePath, "/", name, extension];
    if (queryString) {
        pathParts.push("?",queryString);
    }
    var path = pathParts.join("");

    // load
    var method = params.method || (params.callback ? AjxPackage.DEFAULT_ASYNC : AjxPackage.DEFAULT_SYNC);

    var isSync = method == AjxPackage.METHOD_XHR_SYNC || params.forceSync;
    var isAsync = !isSync;

    var data = {
        name: name,
        path: path,
        method: method,
        async: isAsync,
        callback: callback || AjxCallback.NOP,
        scripts: isAsync ? [] : null
    };

    if (isSync || AjxPackage.__scripts.length == 0) {
        AjxPackage.__doLoad(data);
    }
    else {
        var current = AjxPackage.__scripts[AjxPackage.__scripts.length - 1];
        data.method = current.method;
        data.async = current.async;
        data.scripts = [];
        if (callback) {
            // NOTE: This code is here to protect against interleaved async
            //       requests. If a second async request is made before the
            //       the first one is completely processed, the second request
            //       is added to the first request's stack and is processed
            //       as normal. This prevents the second request's callback
            //       from being called. Therefore, we chain the new callback
            //       to the original callback to ensure that they both get
            //       called.
            var top = AjxPackage.__scripts[0];
            top.callback = new AjxCallback(AjxPackage.__chainCallbacks, [top.callback, callback]);
            data.callback = AjxCallback.NOP;
        }
        current.scripts.push(data);
    }
};

AjxPackage.eval = function(text) {
    // eval in global scope (IE)
    if (window.execScript) {
        // NOTE: for IE
        window.execScript(text);
    }
    // eval in global scope (FF, Opera, WebKit)
    else if (AjxEnv.indirectEvalIsGlobal) {
        var evl=window.eval;
        evl(text);
    }
    // insert script tag into head
    // Note: if any scripts are still loading, this will not run immediately!
    else {
        var e = document.createElement("SCRIPT");
        var t = document.createTextNode(text);
        e.appendChild(t);

        var heads = document.getElementsByTagName("HEAD");
        if (heads.length == 0) {
            // NOTE: Safari doesn't automatically insert <head>
            heads = [ document.createElement("HEAD") ];
            document.documentElement.appendChild(heads[0]);
        }
        heads[0].appendChild(e);
    }
};

//
// Private functions
//

AjxPackage.__package2path = function(name) {
    return name.replace(/\./g, "/").replace(/\*$/, "__all__");
};

AjxPackage.__requireNext = function(params) {
    // NOTE: Both FF and IE won't eval the next loaded code unless we
    //       first return to the UI loop. So we use a timeout to kick
    //       off the next load.
    var func = AjxCallback.simpleClosure(AjxPackage.require, null, params);
    setTimeout(func, AjxEnv.isIE ? 10 : 0);
};

AjxPackage.__doLoad = function(data) {
    if (data.async) {
        AjxPackage.__doAsyncLoad(data);
    }
    else {
        AjxPackage.__doXHR(data);
    }
};

AjxPackage.__doAsyncLoad = function(data, force) {
    AjxPackage.__data[name] = data;
    if (force || AjxPackage.__scripts.length == 0) {
        AjxPackage.__scripts.push(data);
        if (data.method == AjxPackage.METHOD_SCRIPT_TAG) {
            AjxPackage.__doScriptTag(data);
        }
        else {
            AjxPackage.__doXHR(data);
        }
    }
    else {
        var current = AjxPackage.__scripts[AjxPackage.__scripts.length - 1];
        current.scripts.push(data);
    }
};

AjxPackage.__doScriptTag = function(data) {
    // create script element
    var script = document.createElement("SCRIPT");
    script.type = "text/javascript";
    script.src = data.path;

    // attach handler
    if (AjxEnv.isIE) {
        var handler = AjxCallback.simpleClosure(AjxPackage.__onAsyncLoadIE, null, script);
        script.attachEvent("onreadystatechange", handler);
    }
    else {
        var handler = AjxCallback.simpleClosure(AjxPackage.__onAsyncLoad, null, data.name);
        script.addEventListener("load", handler, true);
    }

    // insert element
    var heads = document.getElementsByTagName("HEAD");
    if (!heads || heads.length == 0) {
        // NOTE: Safari doesn't automatically insert <head>
        heads = [ document.createElement("HEAD") ];
        document.documentElement.appendChild(heads[0]);
    }
    heads[0].appendChild(script);
};

AjxPackage.__doXHR = function(data) {
    var callback = data.async ? new AjxCallback(null, AjxPackage.__onXHR, [data]) : null;
    var loadParams = {
        url: data.path,
        userName: data.userName,
        password: data.password,
        async: data.async,
        callback: callback
    };
    var req = AjxLoader.load(loadParams);
    if (!data.async) {
        AjxPackage.__onXHR(data, req);
    }
};

AjxPackage.__onXHR = function(data, req) {
    // evaluate source
    if (req.status == 200 || req.status == 0) {
        AjxPackage.__requireEval(req.responseText || "");
    }
    else {
        AjxPackage.__log("error: "+req.status, "background-color:red");
    }

    // continue
    if (data.async) {
        AjxPackage.__onAsyncLoad();
    }
    else {
        AjxPackage.__onLoad(data);
    }
};

AjxPackage.__onAsyncLoadIE = function(script) {
    if (script.readyState == 'loaded') {
        AjxPackage.__onAsyncLoad();
    }
};

AjxPackage.__onAsyncLoad = function() {
    var current;
    while (current = AjxPackage.__scripts.pop()) {
        // push next scope
        if (current.scripts.length) {
            // NOTE: putting the current back on the stack before adding new scope
            AjxPackage.__scripts.push(current);
            current = current.scripts.shift()
            AjxPackage.__scripts.push(current);
            AjxPackage.__doAsyncLoad(current, true);
            return;
        }
        AjxPackage.__onLoad(current);
    }
};

AjxPackage.__onLoad = function(data) {
    AjxPackage.define(data.name);
    if (data.callback) {
        try {
            data.callback.run();
        }
        catch (e) {
            AjxPackage.__log("error on callback: "+e,"color:red");
        }
    }
};

AjxPackage.__requireEval = function(text) {
    AjxPackage.__depth++;
    try {
        AjxPackage.eval(text);
    }
    catch (e) {
        AjxPackage.__log("error on eval: "+e,"color:red");
    }
    AjxPackage.__depth--;
};

/***
AjxPackage.__win = open("about:blank", "AjxPackageLog"+(new Date().getTime()));
AjxPackage.__win.document.write("<h3>AjxPackage Log</h3>");

AjxPackage.__log = function(s, style) {
    // AjxDebug
//    if (!window.AjxDebug) {
//        var msgs = AjxPackage.__msgs || (AjxPackage.__msgs = []);
//        msgs.push(s);
//        return;
//    }
//
//    if (AjxPackage.__msgs) {
//        AjxPackage.__DBG = new AjxDebug(AjxDebug.DBG1, "AjxPackage");
//        for (var i = 0; i < AjxPackage.__msgs.length; i++) {
//            AjxPackage.__DBG.println(AjxDebug.DBG1, AjxPackage.__msgs[i]);
//        }
//        delete AjxPackage.__msgs;
//    }
//    AjxPackage.__DBG.println(AjxDebug.DBG1, s);

    // new window
    var doc = AjxPackage.__win.document;
    var div = doc.createElement("DIV");
    style = ["padding-left:",AjxPackage.__depth,"em;",style||""].join("");
    div.setAttribute("style", style);
    div.innerHTML = s.replace(/&/g,"&amp;").replace(/</g,"&lt;");
    doc.body.appendChild(div);    
};
/***
AjxPackage.__log = function(s, style) {
    console.log(s);
};
/***/
AjxPackage.__log = function(s, style) {
	// NOTE: This assumes a debug window has been created and assigned
	//       to the global variable "DBG".
//	if (window.DBG) { DBG.println(AjxDebug.DBG1, "PACKAGE: " + s); }
//	if (window.console) { console.log(s); }
}
/***/

AjxPackage.__alertStack = function(title) {
    var a = [];
    if (title) a.push(title, "\n\n");
    for (var i = AjxPackage.__scripts.length - 1; i >= 0; i--) {
        var script = AjxPackage.__scripts[i];
        a.push(script.name," (",Boolean(script.callback),")","\n");
        if (script.scripts) {
            for (var j = 0; j < script.scripts.length; j++) {
                var subscript = script.scripts[j];
                a.push("  ",subscript.name," (",Boolean(subscript.callback),")","\n");
            }
        }
    }
    alert(a.join(""));
};

AjxPackage.__chainCallbacks = function(callback1, callback2) {
    if (callback1) callback1.run();
    if (callback2) callback2.run();
};