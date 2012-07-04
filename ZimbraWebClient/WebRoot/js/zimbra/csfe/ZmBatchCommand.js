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
 * @overview
 * This file contains the batch command class.
 */

/**
 * Creates an empty batch command. Use the {@link #add} method to add commands to it,
 * and {@link #run} to invoke it.
 * @class
 * This class represent a batch command, which is a collection of separate 
 * requests. Each command is a callback with a method, arguments, and (usually) an
 * object on which to call the method. Normally, when the command is run, it creates
 * a SOAP document or JSON object which it hands to the app controller's <code>sendRequest()</code>
 * method. It may also pass a response callback and/or an error callback.
 * <p>
 * Instead of calling sendRequest(), the command should hand the batch command its SOAP
 * document or JSON object, response callback, and error callback. The last argument that
 * the command receives is a reference to the batch command; that's how it knows it's in batch mode.
 * </p>
 * <p>
 * After all commands have been added to the batch command, call its run() method. That will
 * create a BatchRequest out of the individual commands' requests and send it to the
 * server. Each subrequest gets an ID. When the BatchResponse comes back, it is broken into
 * individual responses. If a response indicates success (it is a <code>*Response</code>), the corresponding
 * response callback is called with the result. If the response is a fault, the corresponding
 * error callback is called with the exception.
 * </p>
 * <p>
 * A command does not have to be the method that generates a SOAP document or JSON object.
 * It can be higher-level. Just make sure that the reference to the batch command gets passed down to it.
 * </p>
 * @author Conrad Damon
 * 
 * @param {Boolean}	continueOnError	if <code>true</code>, the batch request continues processing when a subrequest fails (defaults to <code>true</code>)
 * @param {String}	accountName		the account name to run this batch command as.
 * @param {Boolean}	useJson			if <code>true</code>, send JSON rather than XML
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

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
ZmBatchCommand.prototype.toString =
function() {
	return "ZmBatchCommand";
};

//
// Data
//

ZmBatchCommand.prototype._sensitive = false;
ZmBatchCommand.prototype._noAuthToken = false;

//
// Constants
//
ZmBatchCommand.STOP = "stop";
ZmBatchCommand.CONTINUE = "continue";

//
// Public methods
//

/**
 * Sets the sensitive flag. This indicates that this batch command
 * contains a request with sensitive data. Note: There is no way to unset
 * this value for the batch command.
 * 
 * @param	{Boolean}	sensitive		<code>true</code> to set command as sensitive
 */
ZmBatchCommand.prototype.setSensitive = function(sensitive) {
	this._sensitive = this._sensitive || sensitive;
};

/**
 * Sets the noAuthToken flag.
 *
 * @param	{Boolean}	noAuthToken		<code>true</code> to send command with noAuthToken
 */
ZmBatchCommand.prototype.setNoAuthToken = function(noAuthToken) {
	this._noAuthToken = noAuthToken;
};

/**
 * Checks if the command is sensitive.
 * 
 * @return	{Boolean}	<code>true</code> if the command is sensitive
 */
ZmBatchCommand.prototype.isSensitive = function() {
	return this._sensitive;
};

/**
 * Adds a command to the list of commands to run as part of this batch request.
 * 
 * @param {AjxCallback}	cmd		the command
 */
ZmBatchCommand.prototype.add =
function(cmd) {
	this._cmds.push(cmd);
};

/**
 * Gets the number of commands that are part of this batch request.
 * 
 * @return	{int}	the size
 */
ZmBatchCommand.prototype.size =
function() {
	return this.curId || this._cmds.length;
};

/**
 * Runs the batch request. For each individual request, either a response or an
 * error callback will be called.
 * 
 * @param {AjxCallback}		callback		the callback to run after entire batch request has completed
 * @param {AjxCallback}		errorCallback	the error callback called if anything fails.
 *										The error callbacks arguments are all
 *										of the exceptions that occurred. Note:
 *										only the first exception is passed if
 *										this batch command's onError is set to
 *										stop.
 */
ZmBatchCommand.prototype.run =
function(callback, errorCallback) {

	// Invoke each command so that it hands us its SOAP doc, response callback,
	// and error callback
	for (var i = 0; i < this._cmds.length; i++) {
		var cmd = this._cmds[i];
		cmd.run(this);
		this.curId++;
	}

	var params = {
		sensitive:		this._sensitive,
        noAuthToken:	this._noAuthToken,
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
		if (size && this._requests.length) {
			for (var i = 0; i < size; i++) {
				var request = this._requests[i];
				request.requestId = i;
				var methodName = ZmCsfeCommand.getMethodName(request);
				if (!batchRequest[methodName]) {
					batchRequest[methodName] = [];
				}
				request[methodName].requestId = i;
				batchRequest[methodName].push(request[methodName]);
			}
			params.jsonObj = jsonObj;
		}
	}
	else {
		var batchSoapDoc = AjxSoapDoc.create("BatchRequest", "urn:zimbra");
		batchSoapDoc.setMethodAttribute("onerror", this._onError);
		// Add each command's request element to the BatchRequest, and set its ID
		var size = this.size();
		if (size > 0) {
			for (var i = 0; i < size; i++) {
				var soapDoc = this._requests[i];
				var reqEl = soapDoc.getMethod();
				reqEl.setAttribute("requestId", i);
				var node = batchSoapDoc.adoptNode(reqEl);
				batchSoapDoc.getMethod().appendChild(node);
			}
			params.soapDoc = batchSoapDoc;
		}
	}

	// Issue the BatchRequest *but* only when there's something to request
	if (params.jsonObj || params.soapDoc) {
		appCtxt.getAppController().sendRequest(params);
	}
	else if (callback) {
		callback.run();
	}
};

/**
 * @private
 */
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

/**
 * Adds the given command parameters to the batch command, as part of a command's
 * invocation. Should be called by a function that was added via {@link #add} earlier; that
 * function should pass the request object.
 * 
 * @param {AjxSoapDoc|Object}	request		a SOAP document or JSON object with the command's request
 * @param {AjxCallback}	respCallback	the next callback in chain for async request
 * @param {AjxCallback}		errorCallback	the callback to run if there is an exception
 * 
 * @see		#add
 */
ZmBatchCommand.prototype.addRequestParams =
function(request, respCallback, errorCallback) {
	this._requests[this.curId] = request;
	this._respCallbacks[this.curId] = respCallback;
	this._errorCallbacks[this.curId] = errorCallback;
};

/**
 * Adds the given command parameters to the batch command, as part of a command's
 * invocation. Should be called without a previous {@link #add} command, when the request
 * object can immediately generate its request object.
 * 
 * @param {AjxSoapDoc|object}	request		a SOAP document or JSON object with the command's request
 * @param {AjxCallback}	respCallback	the next callback in chain for async request
 * @param {AjxCallback}	errorCallback	the callback to run if there is an exception
 * 
 * @see		#add
 */
ZmBatchCommand.prototype.addNewRequestParams =
function(request, respCallback, errorCallback) {
    this.addRequestParams(request, respCallback, errorCallback);
    this.curId++;
};

/**
 * Each type of request will return an array of <code>*Response</code> elements. There may also be
 * an array of Fault elements. Each element has an ID, so we can match it to its
 * response or error callback, and run whichever is appropriate.
 * 
 * @private
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
