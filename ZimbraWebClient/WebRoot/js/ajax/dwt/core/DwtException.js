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
DwtException = function(msg, code, method, detail) {
	if (arguments.length === 0) {return;}
	AjxException.call(this, msg, code, method, detail);
}

DwtException.prototype = new AjxException();
DwtException.prototype.constructor = DwtException;

/**
 * This method returns this class' name. Subclasses will
 * override this method to return their own name
 * 
 * @return class name
 * @type String
 */
DwtException.prototype.toString = 
function() {
	return "DwtException";
};

/** Invalid parent exception code
 * @type number */
DwtException.INVALIDPARENT = -1;

/** Invalid operation exception code
 * @type number */
DwtException.INVALID_OP = -2;

/** Internal error exception code
 * @type number */
DwtException.INTERNAL_ERROR = -3;

/** Invalid parameter exception code
 * @type number */
DwtException.INVALID_PARAM = -4;