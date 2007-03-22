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
 * This class is a collection of functions for defining packages and
 * loading them dynamically.
 * 
 * @author Andy Clark
 */
function AjxPackage() {}

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

AjxPackage.setBasePath = function(basePath) {
    AjxPackage._basePath = basePath;
};
AjxPackage.setExtension = function(extension) {
    AjxPackage._extension = extension;
};
AjxPackage.setQueryString = function(queryString) {
    AjxPackage._queryString = queryString;
};

/** Defines a package and returns true if this is the first definition. */
AjxPackage.define = function(name) {
    AjxPackage.__log("DEFINE "+name, "font-weight:bold;font-style:italic");
    name = AjxPackage.__package2path(name);
    if (!AjxPackage._packages[name]) {
        AjxPackage._packages[name] = true;
        return true;
    }
    return false;
};

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
 * @param name      [string]        Package name.
 * @param basePath  [string]        (Optional) Base path of URL to load. If
 *                                  not specified, uses the global base path.
 * @param extension [string]        (Optional) Filename extension of URL to
 *                                  load. If not specified, uses the global
 *                                  filename extension.
 * @param queryString [string]      (Optional) Query string appended to URL.
 *                                  If not specified, uses the global query
 *                                  string.
 * @param userName  [string]        (Optional) The username of the request.
 * @param password  [string]        (Optional) The password of the request.
 * @param callback  [AjxCallback]   (Optional) Callback to run.
 * @param forceSync [boolean]       (Optional) Overrides the load mode (if
 *                                  this method is called during an async
 *                                  load) and forces the requested package to
 *                                  be loaded synchronously.
 */
AjxPackage.require = function(nameOrParams) {
    var params = nameOrParams;
    if (typeof nameOrParams == "string") {
        params = { name: nameOrParams };
    }

    // is an array of names specified?
    var array = params.name;
    if (array instanceof Array) {
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
    if (AjxPackage._packages[name]) {
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
    
    // async load
    if (!callback && !params.forceSync && AjxPackage.__scripts.length > 0) {
        callback = AjxCallback.NOP;
    }
    if (callback && !params.forceSync && (!AjxEnv.isSafari || AjxEnv.isSafariNightly)) {
        var data = { name: name, path: path, callback: callback, scripts: [] };
        AjxPackage.__data[name] = data;
        if (AjxPackage.__scripts.length == 0) {
            AjxPackage.__scripts.push(data);
            AjxPackage.__doAsyncLoad(data);
        }
        else {
            var current = AjxPackage.__scripts[AjxPackage.__scripts.length - 1];
            current.scripts.push(data);
        }
    }

    // sync load
    else {
        var loadParams = {
            url: path,
            userName: params.userName,
            password: params.password 
        };
        var req = AjxLoader.load(loadParams);

        // evaluate source
        if (req.status == 200 || req.status == 0) {
            var text = req.responseText || "";
            AjxPackage.__requireEval(text);
        }
        else {
            AjxPackage.__log("error: "+req.status, "background-color:red");
        }

        // automatically define it
        AjxPackage.define(oname);

        if (callback) {
            callback.run();
        }
    }

};

AjxPackage.eval = function(text) {
    // eval in global scope (IE)
    if (window.execScript) {
        // NOTE: for IE
        window.execScript(text);
    }
    // eval in global scope (FF)
    else if (AjxEnv.isGeckoBased) {
        window.eval(text);
    }
    // insert script tag into head
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

AjxPackage.__doAsyncLoad = function(data) {
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

AjxPackage.__onAsyncLoadIE = function(script) {
    if (script.readyState == 'loaded') {
        AjxPackage.__onAsyncLoad();
    }
};

AjxPackage.__onAsyncLoad = function(name) {
    var current;
    while (current = AjxPackage.__scripts.pop()) {
        // push next scope
        if (current.scripts.length) {
            // NOTE: putting the current back on the stack before adding new scope
            AjxPackage.__scripts.push(current);
            current = current.scripts.shift()
            AjxPackage.__scripts.push(current);
            AjxPackage.__doAsyncLoad(current);
            return;
        }

        // automatically define it
        AjxPackage.define(current.name);

        // notify callback
        current.callback.run();
    }
};

AjxPackage.__requireEval = function(text) {
    AjxPackage.__depth++;
    AjxPackage.eval(text);
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