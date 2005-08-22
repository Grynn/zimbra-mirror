/*
***** BEGIN LICENSE BLOCK *****
Version: ZAPL 1.1

The contents of this file are subject to the Zimbra AJAX Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of the
License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
ANY KIND, either express or implied. See the License for the specific language governing rights
and limitations under the License.

The Original Code is: Zimbra AJAX Toolkit.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

/**
* Resets the AjxPost object.
* @constructor
* @class
* This singleton class makes an HTTP POST to the server and receives the response, passing returned data
* to a callback. The form should be within an IFRAME, which can then be filled by the callback as
* appropriate. This class is used to upload files from the client browser to the server using the file
* upload feature of POST.
*
* @author Conrad Damon
*/
function AjxPost() {
	this._container = null;
	this._callback = null;
}

AjxPost._reqIds = 0;
AjxPost._outStandingRequests = new Object();
/**
* Submits the form.
*
* @param container		the IFRAME element that holds the form
* @param callback		function to return to after the HTTP response is received
* @param formId			DOM ID of the form
*/
AjxPost.prototype.execute =
function(container, callback, formId, optionalTimeout) {
	this._container = container;
	this._callback = callback;
	var doc = AjxEnv.isIE ? container.Document : container.contentDocument;
	var form = doc.getElementById(formId);
	var req = new AjxPostRequest(form, doc);
	var failureAction = new AjxTimedAction();
	failureAction.method = this._onFailure;
	failureAction.obj = this;
	failureAction.params.add(req.id);
	var timeout = optionalTimeout? optionalTimeout: 5000;
	AjxPost._outStandingRequests[req.id] = req;
	req.send(failureAction, timeout);
}

AjxPost.prototype._onFailure =
function (reqId){
	var req = AjxPost._outStandingRequests[reqId];
	req.cancel();
	delete AjxPost._outStandingRequests[reqId];
	if (this._callback) {
		this._callback.run([404]);
		this._callback = null;
	}
}
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
	var req = AjxPost._outStandingRequests[reqId];
	if (req && !req.hasBeenCancelled()) {
		req.cancelTimeout();
	}
	delete AjxPost._outStandingRequests[reqId];
	if (this._callback) {
		this._callback.run([status, id]);
		this._callback = null;
	}
};

function AjxPostRequest (form, doc) {
	this.id = AjxPost._reqIds++;
	this._cancelled = false;
	this._form = form;
	var inp = doc.createElement('input');
	inp.type = "hidden";
	inp.name = "requestId";
	inp.value = this.id;
	//var input = Dwt.parseHtmlFragment("<input type='hidden' name='requestId' value='" + this.id + "'></input>");
	this._form.appendChild(inp);
}

AjxPostRequest.prototype.send = function (failureAction, timeout) {
	// Not sure what a fair timeout is for uploads, so for now,
	// we won't have a failed callback.
	//this._timeoutId = AjxTimedAction.scheduleAction(failureAction, timeout);
	this._form.submit();
};

AjxPostRequest.prototype.hasBeenCancelled = function () {
	return this._cancelled;
};

AjxPostRequest.prototype.cancelTimeout = function () {
	AjxTimedAction.cancelAction(this._timeoutId);
};

AjxPostRequest.prototype.cancel = function (){
	this._canceled = true;
};
