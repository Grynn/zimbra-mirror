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

function AjxLoader() {}

//
// Data
//

AjxLoader.__createXHR;

if (window.XMLHttpRequest) {
    AjxLoader.__createXHR = function() { return new XMLHttpRequest(); };
}
else if (ActiveXObject) {
    (function(){
        var vers = ["MSXML2.XMLHTTP.4.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP"];
        for (var i = 0; i < vers.length; i++) {
            try {
                new ActiveXObject(vers[i]);
                AjxLoader.__createXHR = function() { return new ActiveXObject(vers[i]); };
                break;
            }
            catch (e) {
                // ignore
            }
        }
    })();
}

//
// Static functions
//

AjxLoader.syncLoad = function(url, content, userName, password) {
    return AjxLoader.load(null, url, content, userName, password);
};

AjxLoader.asyncLoad = function(callback, url, content, userName, password) {
    return AjxLoader.load(callback, url, content, userName, password);
};

AjxLoader.load = function(callback, url, content, userName, password) {
    var req = AjxLoader.__createXHR();
    var func = Boolean(callback) ? function() { AjxLoader._response(req, callback); } : null;
    var method = content ? "POST" : "GET";
	
	if (func) {
	    req.onreadystatechange = func;
	}
    req.open(method, url, Boolean(func), userName, password);
    req.send(content);

    return req;
};

AjxLoader._response = function(req, callback) {
    if (req.readyState == 4) {
        callback.run(req);
    }
};
