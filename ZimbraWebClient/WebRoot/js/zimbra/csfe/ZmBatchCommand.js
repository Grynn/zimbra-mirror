/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
 * Creates an empty batch command. Use its add() method to add commands to it,
 * and run() to invoke it.
 * @constructor
 * @class
 * This class represent a batch command, which is a collection of separate 
 * requests. Each command is a callback with a method, arguments, and (usually) an
 * object on which to call the method. Normally, when the command is run, it creates
 * a SOAP document or JSON object which it hands to the app controller's sendRequest()
 * method. It may also pass a response callback and/or an error callback.
 * <p>
 * Instead of calling sendRequest(), the command should hand the batch command its SOAP
 * document or JSON object, response callback, and error callback. The last argument that
 * the command receives is a reference to the batch command; that's how it knows it's in batch mode.
 * </p><p>
 * After all commands have been added to the batch command, call its run() method. That will
 * create a BatchRequest out of the individual commands' requests and send it to the
 * server. Each subrequest gets an ID. When the BatchResponse comes back, it is broken into
 * individual responses. If a response indicates success (it is a *Response), the corresponding
 * response callback is called with the result. If the response is a fault, the corresponding
 * error callback is called with the exception.
 * </p><p>
 * A command does not have to be the method that generates a SOAP document or JSON object.
 * It can be higher-level. Just make sure that the reference to the batch command gets passed down to it.
 * </p>
 * @author Conrad Damon
 * 
 * @param continueOnError	[boolean]*	if true, the batch request continues processing
 * 										when a subrequest fails (defaults to true)
 * @param accountName		[string]	The account name to run this batch command as.
 * @param useJson			[boolean]	If true, send JSON rather than XML. Make sure that
 * 										commands return requests as JSON objects!
 */
ZmBatchCommand = function(continueOnError, accountName, useJson) {
	
	this._onError = (continueOnError === false) ? ZmBatchCommand.STOP : ZmBatchCommand.CONTINUE;
	this._accountName = accountName;
	this._useJson = useJson;

	this.curId = 0;
    this._cmds = [];
	this._requests = [];
	this._respCallbacks = [];
	this._errorCallbacks = [];
};

ZmBatchCommand.prototype.toString =
function() {
	return "ZmBatchCommand";
};

//
// Constants
//

ZmBatchCommand.STOP = "stop";
ZmBatchCommand.CONTINUE = "continue";

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
 * @param callback		[AjxCallback]*	callback to run after entire batch request has completed
 * @param errorCallback	[AjxCallback]*	Error callback called if anything fails.
 *										The error callbacks arguments are all
 *										of the exceptions that occured. Note:
 *										only the first exception is passed if
 *										this batch command's onError is set to
 *										stop.
 */
ZmBatchCommand.prototype.run =
function(callback, errorCallback) {

	// Invoke each command so that it hands us its SOAP doc, response callback, and
	// error callback
    for (var i = 0; i < this._cmds.length; i++) {
		var cmd = this._cmds[i];
		cmd.run(this);
        this.curId++;
	}

	var params = {
		asyncMode:		true,
		callback:		new AjxCallback(this, this._handleResponseRun, [callback, errorCallback]),
		errorCallback:	errorCallback,
		accountName:	this._accountName
	};

	// Create the BatchRequest
	if (this._useJson) {
		var jsonObj = {BatchRequest:{_jsns:"urn:zimbra", onerror:this._onError}};
		var batchRequest = jsonObj.BatchRequest;
	    var size = this.size();
	    if (size) {
		    for (var i = 0; i < size; i++) {
		    	var request = this._requests[i];
		    	request.requestId = i;
		    	var methodName = ZmCsfeCommand.getMethodName(request);
		    	if (!batchRequest[methodName]) {
		    		batchRequest[methodName] = [];
		    	}
		    	batchRequest[methodName].push(request[methodName]);
		    }
	    }
	    params.jsonObj = jsonObj;
	} else {
		var batchSoapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
		batchSoapDoc.setMethodAttribute("onerror", this._onError);
		// Add each command's request element to the BatchRequest, and set its ID
	    var size = this.size();
	    for (var i = 0; i < size; i++) {
			var soapDoc = this._requests[i];
			var reqEl = soapDoc.getMethod();
			reqEl.setAttribute("requestId", i);
			var node = batchSoapDoc.adoptNode(reqEl);
			batchSoapDoc.getMethod().appendChild(node);
		}
		params.soapDoc = batchSoapDoc;
	}

	// Issue the BatchRequest
	appCtxt.getAppController().sendRequest(params);
};

ZmBatchCommand.prototype._handleResponseRun =
function(callback, errorCallback, result) {
	var batchResponse = result.getResponse();
	if (!batchResponse.BatchResponse) {
		DBG.println(AjxDebug.DBG1, "Missing batch response!");
		return;
	}
	// NOTE: In case the order of the requests is significant, we process
	//       the responses in the same order.
	var responses = [];
	for (var method in batchResponse.BatchResponse) {
		if (method.match(/^_/)) continue;

		var methodResponses = batchResponse.BatchResponse[method];
		for (var i = 0; i < methodResponses.length; i++) {
			responses[methodResponses[i].requestId] = { method: method, resp: methodResponses[i] };
		}
	}
	var exceptions = [];
	for (var i = 0; i < responses.length; i++) {
		var response = responses[i];
		try {
			this._processResponse(response.method, response.resp);
		}
		catch (ex) {
			exceptions.push(ex);
			if (this._onError == ZmBatchCommand.STOP) {
				break;
			}
		}
	}
	if (exceptions.length > 0 && errorCallback) {
		errorCallback.run.apply(errorCallback, exceptions);
	}
	else if (callback) {
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
 * invocation. Should be called by a function that was added via add() earlier; that
 * function should pass the request object.
 * 
 * @param request		[AjxSoapDoc|object]	a SOAP document or JSON object with the command's request
 * @param callback		[AjxCallback]*		next callback in chain for async request
 * @param errorCallback	[Object]*			callback to run if there is an exception
 */
ZmBatchCommand.prototype.addRequestParams =
function(request, respCallback, errorCallback) {
	this._requests[this.curId] = request;
	this._respCallbacks[this.curId] = respCallback;
	this._errorCallbacks[this.curId] = errorCallback;
};

/**
 * Adds the given command parameters to the batch command, as part of a command's
 * invocation. Should be called without a previous add() command, when the request
 * object can immediately generate its request object.
 * 
 * @param request		[AjxSoapDoc|object]	a SOAP document or JSON object with the command's request
 * @param callback		[AjxCallback]*		next callback in chain for async request
 * @param errorCallback	[Object]*			callback to run if there is an exception
 */
ZmBatchCommand.prototype.addNewRequestParams =
function(request, respCallback, errorCallback) {
    this.addRequestParams(request, respCallback, errorCallback);
    this.curId++;
};

/*
 * Each type of request will return an array of *Response elements. There may also be
 * an array of Fault elements. Each element has an ID, so we can match it to its
 * response or error callback, and run whichever is appropriate.
 */
ZmBatchCommand.prototype._processResponse =
function(method, resp) {
	var id = resp.requestId;

	// handle error
	if (method == "Fault") {
		var ex = ZmCsfeCommand.faultToEx(resp, "ZmBatchCommand.prototype.run");
		if (this._errorCallbacks[id]) {
			var handled = this._errorCallbacks[id].run(ex);
			if (!handled) {
				appCtxt.getAppController()._handleException(ex);
			}
		}
		throw ex;
	}

	// process response callback
	if (this._respCallbacks[id]) {
		var data = {};
		data[method] = resp;
		var result = new ZmCsfeResult(data);
		this._respCallbacks[id].run(result, resp);
	}
};
