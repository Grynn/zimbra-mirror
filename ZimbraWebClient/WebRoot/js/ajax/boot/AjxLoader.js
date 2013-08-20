/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Minimal wrapper around XHR, with no dependencies.
 * 
 * @author Andy Clark
 * 
 * @private
 */
AjxLoader = function() {}

//
// Data
//

AjxLoader.__createXHR;

if (window.XMLHttpRequest) {
    AjxLoader.__createXHR = function() { return new XMLHttpRequest(); };
}
else if (window.ActiveXObject) {
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
 * @param	{hash}		urlOrParams			a hash of parameters
 * @param {string}	urlOrParams.url       the URL to load
 * @param {string}	[urlOrParams.method]    the load method (e.g. "GET").
 *                                  If this parameter is not specified, then
 *                                  the value is determined by whether content
 *                                  has been specified: "POST" if specified,
 *                                  "GET" otherwise.
 * @param {hash}	[urlOrParams.headers]  the map of request headers to set.
 * @param {boolean}	[urlOrParams.async]     determines whether the request
 *                                  is asynchronous or synchronous. If this
 *                                  parameter is not specified, then the value
 *                                  is determined by whether a callback has
 *                                  been specified: async if a callback is
 *                                  specified, sync if no callback.
 * @param {string}	[urlOrParams.content]   the content to POST to URL. If not specified, the request method is GET.
 * @param {string}	[urlOrParams.userName]  the username of the request.
 * @param {string}	[urlOrParams.password]  the password of the request.
 * @param {AjxCallback}	[urlOrParams.callback]  the callback to run at end of load.
 */
AjxLoader.load = function(urlOrParams) {
    var params = urlOrParams;
    if (typeof urlOrParams == "string") {
        params = { url: urlOrParams };
    }

    var req = AjxLoader.__createXHR();
    var func = Boolean(params.callback) ? function() { AjxLoader._response(req, params.callback); } : null;
    var method = params.method || (params.content != null ? "POST" : "GET");
	
	if (func) {
	    req.onreadystatechange = func;
	}
    var async = params.async != null ? params.async : Boolean(func);
    req.open(method, params.url, async, params.userName, params.password);
    for (var name in params.headers) {
        req.setRequestHeader(name, params.headers[name]);
    }
    req.send(params.content || "");

    return req;
};

AjxLoader._response = function(req, callback) {
    if (req.readyState == 4) {
        callback.run(req);
    }
};
