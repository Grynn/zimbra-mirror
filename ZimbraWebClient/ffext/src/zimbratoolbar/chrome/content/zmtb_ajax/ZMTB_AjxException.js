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
 * @constructor
 * @class
 * Base class for all exceptions in the toolkit
 * 
 * @author Ross Dargahi
 * 
 * @param {string} msg Human readable message (optional)
 * @param {string|number} code Any error or fault code (optional)
 * @param {string} method Name of the method throwing the exception (optional)
 * @param {string} detail Any additional detail (optional)
 */

function ZMTB_AjxException(msg, code, method, detail) {
	if (arguments.length == 0) return;
	
	/** Human readable message if applicable
	 * @type string*/
	this.msg = msg;
	
	/** error or fault code if applicable
	 * @type string|number*/
	this.code = code;
	
	/** Name of the method throwing the exception if applicable
	 * @type string*/
	this.method = method;
	
	/** Any additional detail
	 * @type string*/
	this.detail = detail;
}

/**
 * This method returns this class' name. Subclasses will
 * override this method to return their own name
 * 
 * @return class name
 * @type String
 */
ZMTB_AjxException.prototype.toString = 
function() {
	return "ZMTB_AjxException";
}

/**
 * @return A string representing the state of the exception
 * @type string
 */
ZMTB_AjxException.prototype.dump = 
function() {
	return "ZMTB_AjxException: msg="+this.msg+" code="+this.code+" method="+this.method+" detail="+this.detail;
}

/** Invalid parent exception code
 * @type string */
ZMTB_AjxException.INVALIDPARENT 			= "ZMTB_AjxException.INVALIDPARENT";

/** Invalid operation exception code
 * @type string */
ZMTB_AjxException.INVALID_OP 			= "ZMTB_AjxException.INVALID_OP";

/** Internal error exception code
 * @type string */
ZMTB_AjxException.INTERNAL_ERROR 		= "ZMTB_AjxException.INTERNAL_ERROR";

/** Invalid parameter to method/operation exception code
 * @type string */
ZMTB_AjxException.INVALID_PARAM 			= "ZMTB_AjxException.INVALID_PARAM";

/** Unimplemented method called exception code
 * @type string */
ZMTB_AjxException.UNIMPLEMENTED_METHOD 	= "ZMTB_AjxException.UNIMPLEMENTED_METHOD";

/** Network error exception code
 * @type string */
ZMTB_AjxException.NETWORK_ERROR 			= "ZMTB_AjxException.NETWORK_ERROR";

/** Out or RPC cache exception code
 * @type string */
ZMTB_AjxException.OUT_OF_RPC_CACHE		= "ZMTB_AjxException.OUT_OF_RPC_CACHE";

/** Unsupported operation code
 * @type string */
ZMTB_AjxException.UNSUPPORTED 			= "ZMTB_AjxException.UNSUPPORTED";

/** Unknown error exception code
 * @type string */
ZMTB_AjxException.UNKNOWN_ERROR 			= "ZMTB_AjxException.UNKNOWN_ERROR";

/** Operation cancelled exception code
 * @type string */
ZMTB_AjxException.CANCELED				= "ZMTB_AjxException.CANCELED";
