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
 * Minimal wrapper around XHR, with no dependencies.
 * 
 * @author Andy Clark
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

/**
 * This function uses XHR to load and return the contents at an arbitrary URL.
 * <p>
 * It can be called with either a URL string or a parameters object.
 *
 * @param url       [string]        URL to load.
 * @param content   [string]        (Optional) Content to POST to URL. If
 *                                  not specified, the request method is GET.
 * @param userName  [string]        (Optional) The username of the request.
 * @param password  [string]        (Optional) The password of the request.
 * @param callback  [AjxCallback]   (Optional) Callback to run at end of load.
 */
AjxLoader.load = function(urlOrParams) {
    var params = urlOrParams;
    if (typeof urlOrParams == "string") {
        params = { url: urlOrParams };
    }

    var req = AjxLoader.__createXHR();
    var func = Boolean(params.callback) ? function() { AjxLoader._response(req, params.callback); } : null;
    var method = params.content ? "POST" : "GET";
	
	if (func) {
	    req.onreadystatechange = func;
	}
    req.open(method, params.url, Boolean(func), params.userName, params.password);
    req.send(params.content || "");

    return req;
};

AjxLoader._response = function(req, callback) {
    if (req.readyState == 4) {
        callback.run(req);
    }
};
