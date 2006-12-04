/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
 * Creates an empty batch command. Use its run() method to add commands to it.
 * @constructor
 * @class
 * This class represent a batch command, which is a collection of separate SOAP
 * requests. Each command is a callback with a method, arguments, and (usually) an
 * object on which to call the method. Normally, when the command is run, it creates
 * a soap document which it hands to the app controller's sendRequest() method. It may
 * also pass a response callback and/or an error callback.
 * <p>
 * Instead of calling sendRequest(), the command should hand the batch command its SOAP
 * document, response callback, and error callback. The last argument that the command
 * receives is a reference to the batch command; that's how it knows it's in batch mode.
 * </p><p>
 * After all commands have been added to the batch command, call its run() method. That will
 * create a BatchRequest out of the individual commands' SOAP documents and send it to the
 * server. Each subrequest gets an ID. When the BatchResponse comes back, it is broken into
 * individual responses. If a response indicates success (it is a *Response), the corresponding
 * response callback is called with the result. If the response is a fault, the corresponding
 * error callback is called with the exception.
 * </p><p>
 * A command does not have to be the method that generates a SOAP document. It can be higher-level.
 * Just make sure that the reference to the batch command gets passed down to it.
 * </p>
 * @author Conrad Damon
 * 
 * @param appCtxt			[ZmAppCtxt]	the app context
 * @param continueOnError	[boolean]*	if true, the batch request continues processing
 * 										when a subrequest fails (defaults to true)
 */
function ZmBatchCommand(appCtxt, continueOnError) {
	
	this._appCtxt = appCtxt;
	this._continue = (continueOnError === false) ? "stop" : "continue";

    this.curId = 0;
    this._cmds = [];
	this._soapDocs = [];
	this._respCallbacks = [];
	this._errorCallbacks = [];
	this._execFrames = [];
};

ZmBatchCommand.prototype.toString =
function() {
	return "ZmBatchCommand";
};

/**
 * Adds a command to the list of commands to run as part of this batch request.
 * 
 * @param cmd	[AjxCallback]	a command
 */
ZmBatchCommand.prototype.add =
function(cmd) {
	this._cmds.push(cmd);
};

ZmBatchCommand.prototype.size =
function() {
	return this.curId;
};

/**
 * Issues the batch request. For each individual request, either a response or an
 * error callback will be called.
 * 
 * @param callback	[AjxCallback]*		callback to run after entire batch request has completed
 */
ZmBatchCommand.prototype.run =
function(callback) {

	// Invoke each command so that it hands us its SOAP doc, response callback, and
	// error callback
    for (var i = 0; i < this._cmds.length; i++) {
		var cmd = this._cmds[i];
		cmd.run(this);
        this.curId++;
	}

	// bug fix #9086 - Safari has bugs with appendChild :(
	if (AjxEnv.isSafari) {
		this.runSafari(callback);
		return;
	}

	// Create the BatchRequest
	var batchSoapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
	batchSoapDoc.setMethodAttribute("onerror", this._continue);

	// Add each command's request element to the BatchRequest, and set its ID
    var size = this.size();
    for (var i = 0; i < size; i++) {
		var soapDoc = this._soapDocs[i];
		var reqEl = soapDoc.getMethod();
		reqEl.setAttribute("id", i);
		var node = batchSoapDoc.adoptNode(reqEl);
		batchSoapDoc.getMethod().appendChild(node);
	}
	
	// Issue the BatchRequest
	var respCallback = new AjxCallback(this, this._handleResponseRun, [callback]);
	this._appCtxt.getAppController().sendRequest({soapDoc:batchSoapDoc, asyncMode:true, callback:respCallback});
};

ZmBatchCommand.prototype.runSafari =
function(callback) {
	this._responseCount = 0;
	var runCallback = new AjxCallback(this, this._handleResponseRunSafari, [callback]);

    var size = this.size();
    for (var i = 0; i < size; i++) {
		var soapDoc = this._soapDocs[i];
		var reqEl = soapDoc.getMethod();
		reqEl.setAttribute("id", i);

		this._appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:runCallback});
	}
};

ZmBatchCommand.prototype._handleResponseRun =
function(callback, result) {
	var batchResponse = result.getResponse();
	if (!batchResponse.BatchResponse) {
		DBG.println(AjxDebug.DBG1, "Missing batch response!");
		return;
	}
	for (var method in batchResponse.BatchResponse) {
		this._processResponse(method, batchResponse.BatchResponse[method]);
	}
	if (callback) {
		callback.run(result);
	}
};

ZmBatchCommand.prototype._handleResponseRunSafari =
function(callback, result) {
	var resp = result.getResponse();
	for (var i in resp) {
		this._processResponse(i, resp[i]);
	}

	// only run the final callback once all async requests have returned
    var size = this.size();
    if (++this._responseCount == size && callback) {
		callback.run(result);
	}
};

/**
 * Adds the given command parameters to the batch command, as part of a command's
 * invocation.
 * 
 * @param soapDoc		[AjxSoapDoc]	a SOAP document with the command's request
 * @param callback		[AjxCallback]*	next callback in chain for async request
 * @param errorCallback	[Object]*		callback to run if there is an exception
 * @param execFrame		[AjxCallback]*	the calling method, object, and args
 */
ZmBatchCommand.prototype.addRequestParams =
function(soapDoc, respCallback, errorCallback, execFrame) {
	this._soapDocs[this.curId] = soapDoc;
	this._respCallbacks[this.curId] = respCallback;
	this._errorCallbacks[this.curId] = errorCallback;
	this._execFrames[this.curId] = execFrame;
};

ZmBatchCommand.prototype.addNewRequestParams =
function(soapDoc, respCallback, errorCallback, execFrame) {
    this.addRequestParams(soapDoc, respCallback, errorCallback, execFrame);
    this.curId++;
};

/*
 * Each type of request will return an array of *Response elements. There may also be
 * an array of Fault elements. Each element has an ID, so we can match it to its
 * response or error callback, and run whichever is appropriate.
 */
ZmBatchCommand.prototype._processResponse =
function(method, response) {
	if (!(response instanceof Array)) {
		response = [response];
	}
	for (var i = 0; i < response.length; i++) {
		var resp = response[i];
		var data = {};
		data[method] = resp;
		var id = resp.id;
		if (method == "Fault") {
			var execFrame = this._execFrames[id];
			if (this._errorCallbacks[id]) {
				var ex = ZmCsfeCommand.faultToEx(resp, "ZmBatchCommand.prototype.run");
				var handled = this._errorCallbacks[id].run(ex);
				if (!handled && execFrame) {
					this._appCtxt.getAppController()._handleException(ex, execFrame);
				}
			} else if (execFrame) {
				this._appCtxt.getAppController()._handleException(ex, execFrame);
			}
		} else {
			if (this._respCallbacks[id]) {
				var result = new ZmCsfeResult(data);
				this._respCallbacks[id].run(result, resp);
			}
		}
	}
};
