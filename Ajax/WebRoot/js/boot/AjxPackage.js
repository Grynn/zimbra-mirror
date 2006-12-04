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
 * loading them dynamicall.
 */
function AjxPackage() {}

//
// Data
//

AjxPackage._packages = {};
AjxPackage._extension = ".js";

AjxPackage.__depth = 0;

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
    AjxPackage.__log("define "+name, "font-weight:bold");
    name = AjxPackage.__package2path(name);
    if (!AjxPackage._packages[name]) {
        AjxPackage._packages[name] = true;
        return true;
    }
    return false;
};

AjxPackage.undefine = function(name) {
    AjxPackage.__log("undefine "+name, "font-weight:bold;font-style:italic");
    name = AjxPackage.__package2path(name);
    if (AjxPackage._packages[name]) {
        delete AjxPackage._packages[name];
    }
};

AjxPackage.require = function(name, basePath, extension, userName, password) {
    // see if it's already loaded
    var oname = name;
    AjxPackage.__log("require "+name, "font-style:italic");
    name = AjxPackage.__package2path(name);
    if (AjxPackage._packages[name]) return;

    // automatically define it
    AjxPackage.define(oname);

    // load it
    basePath = basePath || AjxPackage._basePath || window.contextPath;
    extension = extension || AjxPackage._extension; 
    var pathParts = [basePath, "/", name, extension];
    if (AjxPackage._queryString) {
        pathParts.push("?",AjxPackage._queryString);
    }
    var path = pathParts.join("");
    
    AjxPackage.__log("loading "+path);
    var req = AjxLoader.syncLoad(path, null, userName, password);

    // evaluate source
    if (req.status == 200) {
        var text = req.responseText || "";
        AjxPackage.__requireEval(text);
    }
    else {
        AjxPackage.__log("error: "+req.status, "background-color:red");
    }
};

//
// Private functions
//

AjxPackage.__package2path = function(name) {
    return name.replace(/\./g, "/").replace(/\*$/, "__all__");
};

AjxPackage.__requireEval = function(text) {
    AjxPackage.__depth++;
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
            var h = document.createElement("HEAD");
            document.documentElement.appendChild(h);
            heads = [ h ];
        }
        heads[0].appendChild(e);
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
/***/
AjxPackage.__log = function(s, style) {}
/***/