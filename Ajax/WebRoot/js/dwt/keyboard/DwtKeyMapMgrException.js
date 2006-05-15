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
* The Keymap manager exception class.
* 
* @author Ross Dargahi
*
* @see DwtKeyMapMgr
*/
function DwtKeyMapMgrException(msg, code, method, keySeqStr) {
	DwtException.call(this, msg, code, method, detail);
	this._keySeqStr = keySeqStr;
}

DwtKeyMapMgrException.prototype = new DwtException;
DwtKeyMapMgrException.prototype.constructor = DwtKeyMapMgrException;

DwtKeyMapMgrException.NON_TERM_HAS_ACTION = 1;
DwtKeyMapMgrException.TERM_HAS_SUBMAP = 2;

DwtException.prototype.toString = 
function() {
	return "DwtKeyMapMgrException";
}
