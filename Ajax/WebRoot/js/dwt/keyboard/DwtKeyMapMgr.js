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
* The Keymap manager is responsible for building a FSA from a keymap and
* providing methods for resolving key sequences against that map.
* @constructor
* @class
* 
* @author Ross Dargahi
* @param keyMap [Object, optional]
*
* @throws
*	DwtKeyMapMgrException
*/
function DwtKeyMapMgr(keyMap) {
	if (!DwtKeyMapMgr._inited) {
		DwtKeyMapMgr._initUsKeyCodeMap();
		DwtKeyMapMgr._inited = true;
	}
		
	var map = keyMap.getMap();
	
	// Builds key mapping FSA for each mapping
	this._fsas = {};
	for (var key in map) {
		DBG.println(AjxDebug.DBG3, "======== Processing Map Name: " + key);
		try {
			var newFSA = DwtKeyMapMgr._buildFSA({}, map[key], key);
			this._fsas[key] = newFSA;
		} catch (ex) {
			alert("EX: " + ex._msg + " - " + ex._keySeqStr);
		}	
	}

	DBG.dumpObj(AjxDebug.DBG3, this._fsas);
};

DwtKeyMapMgr.NOT_A_TERMINAL = -999;
DwtKeyMapMgr.TAB_KEYCODE = 9;

DwtKeyMapMgr._KEYCODES = []; // Keycode map
DwtKeyMapMgr._inited = false; // Initialize flag

DwtKeyMapMgr.prototype.getActionCode =
function (keySeq, mappingName) {
	//DBG.println("Getting action code for: " + keySeq.join("") + " in map: " + mappingName);
	var mapping =  this._fsas[mappingName];
	
	// TODO If there is no such mapping then consider this a terminal?
	if (!mapping)
		return null;
					
	var keySeqLen = keySeq.length;
	var tmpFsa = mapping;
	var key;
	for (var j = 0; j < keySeqLen && tmpFsa; j++) {
		key = keySeq[j];

		if (!tmpFsa[key])
			break;
		
		if (j < keySeqLen - 1)
			tmpFsa = tmpFsa[key].subMap;
	}
	
	if (!tmpFsa) {
		// This is essentially an illegal condition.
		DBG.println("tmpFsa is null. mapping name: " + mappingName + ", key: " + key 
					+ " - keyseq: " + keySeq);
		return null;
	} else if (tmpFsa[key]) {
		// Found the action code the mapping
		return (tmpFsa[key].actionCode != null) ? tmpFsa[key].actionCode
												 : DwtKeyMapMgr.NOT_A_TERMINAL;
	} else if (mapping.INHERIT != null) {
		var inherited = mapping.INHERIT;
		/* In the case of multiple inheritence, the INHERIT attribute will be an
		 * array, else it will be a string */
		if (typeof(inherited) == "string") {
			//DBG.println("Inheriting from: " + inherited);
			return this.getActionCode(keySeq, inherited);			
		} else {
			var actionCode = null;
			var len = inherited.length;
			for (var i = 0; i < len; i++) {
				//DBG.println("(MULTI) Inheriting from: " + inherited[i]);
				actionCode = this.getActionCode(keySeq, inherited[i]);
				if (actionCode != null)
					return actionCode;
			}		
		}
	} else {
		// No match
		return null;
	}
}

DwtKeyMapMgr.prototype.keyCode2Char =
function(keyCode) {
	return DwtKeyMapMgr._KEYCODES[keyCode];
}

DwtKeyMapMgr._initUsKeyCodeMap =
function() {
	DwtKeyMapMgr._KEYCODES[18]  = DwtKeyMap.ALT;
	DwtKeyMapMgr._KEYCODES[40]  = DwtKeyMap.ARROW_DOWN;
	DwtKeyMapMgr._KEYCODES[37]  = DwtKeyMap.ARROW_LEFT;
	DwtKeyMapMgr._KEYCODES[39]  = DwtKeyMap.ARROW_RIGHT;
	DwtKeyMapMgr._KEYCODES[38]  = DwtKeyMap.ARROW_UP;
	DwtKeyMapMgr._KEYCODES[8]   = DwtKeyMap.BACKSPACE;
	DwtKeyMapMgr._KEYCODES[17]  = DwtKeyMap.CTRL;
	DwtKeyMapMgr._KEYCODES[46]  = DwtKeyMap.DELETE;
	DwtKeyMapMgr._KEYCODES[35]  = DwtKeyMap.END;
	DwtKeyMapMgr._KEYCODES[13]  = DwtKeyMap.ENTER;
	DwtKeyMapMgr._KEYCODES[27]  = DwtKeyMap.ESC;
	DwtKeyMapMgr._KEYCODES[34]  = DwtKeyMap.PAGE_DOWN;
	DwtKeyMapMgr._KEYCODES[33]  = DwtKeyMap.PAGE_UP;
	DwtKeyMapMgr._KEYCODES[16]  = DwtKeyMap.SHIFT;
	DwtKeyMapMgr._KEYCODES[32]  = DwtKeyMap.SPACE;
	DwtKeyMapMgr._KEYCODES[9]   = DwtKeyMap.TAB;
	
	// Function keys
	for (var i = 112; i < 124; i++) 
		DwtKeyMapMgr._KEYCODES[i] = "F" + (i - 111);
	
	// Take advantage of the fact that keycode for capital letters are the 
	// same as the charcode values i.e. ASCII code
	for (var i = 65; i < 91; i++)
		DwtKeyMapMgr._KEYCODES[i] = String.fromCharCode(i);

	// Numbers 0 - 9
	for (var i = 48; i < 58; i++)
		DwtKeyMapMgr._KEYCODES[i] = String.fromCharCode(i);
		
	// punctuation
	DwtKeyMapMgr._KEYCODES[222] = "'";
	DwtKeyMapMgr._KEYCODES[189] = "-";
	DwtKeyMapMgr._KEYCODES[188] = ",";
	DwtKeyMapMgr._KEYCODES[190] = ".";
	DwtKeyMapMgr._KEYCODES[191] = "/";
	DwtKeyMapMgr._KEYCODES[186] = ";";
	DwtKeyMapMgr._KEYCODES[219] = "[";
	DwtKeyMapMgr._KEYCODES[220] = "\\";
	DwtKeyMapMgr._KEYCODES[221] = "]";
	DwtKeyMapMgr._KEYCODES[192] = "`";
	DwtKeyMapMgr._KEYCODES[187] = "=";	
	
	// Setup the "is" methods
	DwtKeyMapMgr.isAlpha = DwtKeyMapMgr._isAlphaUs;
	DwtKeyMapMgr.isNumeric = DwtKeyMapMgr._isNumericUs;
	DwtKeyMapMgr.isAlphanumeric = DwtKeyMapMgr._isAlphanumericUs;
	DwtKeyMapMgr.isPunctuation = DwtKeyMapMgr._isPunctuationUs;
	DwtKeyMapMgr.isUsableTextInputValue = DwtKeyMapMgr.isUsableTextInputValueUs;
	DwtKeyMapMgr.isModifier = DwtKeyMapMgr._isModifierUs;
}

DwtKeyMapMgr._isAlphaUs = 
function(keyCode) {
	if (keyCode > 64 && keyCode < 91)
		return true;
}

DwtKeyMapMgr._isNumericUs = 
function(keyCode) {
	if (keyCode > 47 && keyCode < 58)
		return true;
}

DwtKeyMapMgr._isAlphanumericUs = 
function(keyCode) {
	return (DwtKeyMapMgr._isNumericUs(keyCode) || DwtKeyMapMgr._isAlphaUs(keyCode));
}

DwtKeyMapMgr._isPunctuationUs = 
function(keyCode) {
	switch (keyCode) {
		case 186: // ;
		case 187: // =
		case 188: // ,
		case 189: // -
		case 190: // .
		case 191: // /
		case 192: // `
		case 219: // [
		case 220: // \
		case 221: // ]
		case 222: // '
			return true;
		default:
			return false;		
	}
}

DwtKeyMapMgr._isModifierUs = 
function(keyCode) {
	switch (keyCode) {
		case 16: // Shift
		case 17: // Ctrl
		case 18: // Alt
			return true;
			
		default:
			return false;
	}
}


DwtKeyMapMgr.isUsableTextInputValueUs =
function(keyCode) {
	if (DwtKeyMapMgr._isAlphanumericUs(keyCode) || DwtKeyMapMgr._isPunctuationUs(keyCode) 
		|| DwtKeyMapMgr._isAlphanumericUs(keyCode))
		return true;
		
	switch (keyCode) {
		case 37:
		case 39:
		case 8:
		case 45:
		case 46:
		case 35:
		case 13:
		case 32:
			return true;
			
		default:
			return false;
	}	
}

DwtKeyMapMgr._buildFSA =
function(fsa, mapping, mapName) {
	for (var i in mapping) {
		// DBG.println(AjxDebug.DBG3, "_buildFSA - keySeq: " + i);
		// If this map is inheriting from another map, then set up the inheritence
		 if (i == DwtKeyMap.INHERIT) {
			//DBG.println(AjxDebug.DBG3, "Inheriting from: " + mapping[i]);
			fsa.INHERIT = mapping[i];
			continue;
		}
		 
		var keySeq = i.split(DwtKeyMap.SEP);
		var keySeqLen = keySeq.length;
		var tmpFsa = fsa;
		for (var j = 0; j < keySeqLen; j++) {
			var key = keySeq[j];
			//DBG.println(AjxDebug.DBG3, "Processing: " + key);
			
			/* If we have not visited this key before we will need to create a
			 * new _DwtKeyMapMgrItem object */
			if (!tmpFsa[key])
				tmpFsa[key] = new _DwtKeyMapMgrItem();

			if (j == keySeqLen - 1) {
				/* If this key has a submap, then it is illegal for it to also 
				 * have an action code. Throw an exception!*/
				 if (tmpFsa[key].subMap)
				 	throw new DwtKeyMapMgrException("Attempting to add action code to non-terminal key sequence. Map name: " + mapName, 
				 			DwtKeyMapMgrException.NON_TERM_HAS_ACTION, "DwtKeyMapMgr._buildFSA[1]", keySeq);
				
				/* We are at the last key in the sequence so we can bind the
				 * action code to it */
				//DBG.println(AjxDebug.DBG3, "BINDING: " + mapping[i]);
				tmpFsa[key].actionCode = mapping[i];
			} else {
				/* If this key has an action code, then it is illegal for it to also 
				 * have a submap. Throw an exception! */
				 if (tmpFsa[key].actionCode) {
				 	var tmp = new Array(j)
				 	for (var k = 0; k <= j; k++)
				 		tmp[k] = keySeq[k];
				 	throw new DwtKeyMapMgrException("Attempting to add submap to terminal key sequence. Map name " + mapName,
				 		DwtKeyMapMgrException.TERM_HAS_SUBMAP, "DwtKeyMapMgr._buildFSA[2]", tmp.join(""));
				}
				
				/* We have more keys in the sequence. If our subMap is null,
				 * then we need to create it to hold the new key sequences */
				if (!tmpFsa[key].subMap)
					tmpFsa[key].subMap = new Object();
				tmpFsa = tmpFsa[key].subMap;
			}			
		}
	}
	return fsa;
};

/** Helper class
 * @private
 */
function _DwtKeyMapMgrItem() {
	this.actionCode = null;
	this.subMap = null;
};