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


function AjxException(msg, code, method, detail) {
	if (arguments.length == 0) return;
	this.msg = msg;
	this.code = code;
	this.method = method;
	this.detail = detail;
}

AjxException.prototype.toString = 
function() {
	return "AjxException";
}

AjxException.prototype.dump = 
function() {
	return "AjxException: msg="+this.msg+" code="+this.code+" method="+this.method+" detail="+this.detail;
}
AjxException.INVALIDPARENT 			= "AjxException.INVALIDPARENT";
AjxException.INVALID_OP 			= "AjxException.INVALID_OP";
AjxException.INTERNAL_ERROR 		= "AjxException.INTERNAL_ERROR";
AjxException.INVALID_PARAM 			= "AjxException.INVALID_PARAM";
AjxException.UNIMPLEMENTED_METHOD 	= "AjxException.UNIMPLEMENTED_METHOD";
AjxException.NETWORK_ERROR 			= "AjxException.NETWORK_ERROR";
AjxException.OUT_OF_RPC_CACHE		= "AjxException.OUT_OF_RPC_CACHE";
AjxException.UNSUPPORTED 			= "AjxException.UNSUPPORTED";
AjxException.UNKNOWN_ERROR 			= "AjxException.UNKNOWN_ERROR";
AjxException.CANCELED				= "AjxException.CANCELED";
