/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Resets the AjxPost object.
 * @constructor
 * @class
 * This singleton class makes an HTTP POST to the server and receives the response, passing returned data
 * to a callback. This class is used to upload files from the client browser to the server using the file
 * upload feature of POST.
 *
 * @param	{string}	iframeId		the iframe ID
 * 
 * @author Conrad Damon
 * 
 * @private
 */
AjxPost = function(iframeId) {
	this._callback = null;
	this._iframeId = iframeId;
}


// Globals

AjxPost._reqIds = 0;
AjxPost._outStandingRequests = new Object();


// Consts 

// Common HttpServletResponse error codes
// - see full list: http://java.sun.com/products/servlet/2.2/javadoc/javax/servlet/http/HttpServletResponse.html
AjxPost.SC_CONTINUE					= 100;
AjxPost.SC_OK						= 200;
AjxPost.SC_ACCEPTED 				= 202;
AjxPost.SC_NO_CONTENT 				= 204;
AjxPost.SC_BAD_REQUEST				= 400;
AjxPost.SC_UNAUTHORIZED				= 401;
AjxPost.SC_REQUEST_TIMEOUT			= 408;
AjxPost.SC_CONFLICT					= 409;
AjxPost.SC_REQUEST_ENTITY_TOO_LARGE = 413;
AjxPost.SC_INTERNAL_SERVER_ERROR	= 500;
AjxPost.SC_BAD_GATEWAY 				= 502;
AjxPost.SC_SERVICE_UNAVAILABLE		= 503;


// Public methods

/**
* Submits the form.
*
* @param callback		function to return to after the HTTP response is received
* @param formId			DOM ID of the form
*/
AjxPost.prototype.execute =
function(callback, form, optionalTimeout) {
	// bug fix #7361
	var tags = form.getElementsByTagName("input");
	var inputs = new Array();
	for (var i = 0; i < tags.length; i++) {
		var tag = tags[i];
		if (tag.type == "file") {
			inputs.push(tag);
			continue;
		}
		// clean up form from previous posts
		if (tag.name && tag.name.match(/^filename\d+$/)) {
			tag.parentNode.removeChild(tag);
			i--; // list is live, so stay on same index
			continue;
		}
	}

    this._addHiddenFileNames(inputs);

	form.target = this._iframeId;
	this._callback = callback;
	var req = new AjxPostRequest(form);
	var failureAction = new AjxTimedAction(this, this._onFailure, [req.id]);
	var timeout = optionalTimeout? optionalTimeout: 5000;
	AjxPost._outStandingRequests[req.id] = req;
	try {
		req.send(failureAction, timeout);
	} catch (ex) {
		if (AjxEnv.isIE) {
			if (ex.number == -2147024891) { // 0x80070005: E_ACCESSDENIED (Couldn't open file)
				throw new AjxException(ZmMsg.uploadErrorAccessDenied, ex.number);
			}
		}
		throw ex;
	}
};

AjxPost.prototype._addHiddenFileNames =
function(inputs){
    var m = 0;
    for (var i = 0; i < inputs.length; i++) {
        var fileInput = inputs[i];
        if(fileInput.files && fileInput.files.length > 1){
            var files = fileInput.files, fileStr=[];
            for(var j=0; j<files.length; j++){
               var f = files[j];
               fileStr.push(f.name || f.fileName);
            }
            this._addHiddenFileName(inputs[i], fileStr.join('\n'), ++m);
        }else{
            this._addHiddenFileName(inputs[i], inputs[i].value, ++m);
        }
    }

};

AjxPost.prototype._addHiddenFileName =
function(inputField, fileName, index){
    var hidden = document.createElement("input");
    hidden.type = "hidden";
    hidden.name = "filename" + (index);
    hidden.value = fileName;
    inputField.parentNode.insertBefore(hidden, inputField);

};


// Private methods

AjxPost.prototype._onFailure =
function (reqId){
	var req = AjxPost._outStandingRequests[reqId];
	req.cancel();
	delete AjxPost._outStandingRequests[reqId];
	if (this._callback) {
		this._callback.run([404]);
		this._callback = null;
	}
};



/**
* Processes the HTTP response from the form post. The server needs to make sure this function is
* called and passed the appropriate args. Something like the following should do the trick:
* <code>
*        out.println("<html><head></head><body onload=\"window.parent._uploadManager.loaded(" + results +");\"></body></html>");
* </code>
*
* @param status		an HTTP status
* @param id			the id for any attachments that were uploaded
*/
AjxPost.prototype.loaded =
function(status, reqId, id) {
	//alert(document.getElementById(this._iframeId).contentWindow.document.documentElement.innerHTML);
	var req = AjxPost._outStandingRequests[reqId];
	if (req && !req.hasBeenCancelled()) {
		req.cancelTimeout();
	}
	delete AjxPost._outStandingRequests[reqId];
	if (this._callback) {
		this._callback.run(status, id);
		this._callback = null;
	}
};

/**
 * @class
 * 
 * @private
 */
AjxPostRequest = function(form) {
	this.id = AjxPost._reqIds++;
	this._cancelled = false;
	this._form = form;
	var inp = form.elements.namedItem("requestId");
	if (!inp) {
		inp = form.ownerDocument.createElement('input');
		inp.type = "hidden";
		inp.name = "requestId";
	}
	inp.value = this.id;
	form.appendChild(inp);
};

AjxPostRequest.prototype.send =
function(failureAction, timeout) {
	this._form.submit();
};

AjxPostRequest.prototype.hasBeenCancelled =
function() {
	return this._cancelled;
};

AjxPostRequest.prototype.cancelTimeout =
function() {
	AjxTimedAction.cancelAction(this._timeoutId);
};

AjxPostRequest.prototype.cancel =
function() {
	this._cancelled = true;
};
