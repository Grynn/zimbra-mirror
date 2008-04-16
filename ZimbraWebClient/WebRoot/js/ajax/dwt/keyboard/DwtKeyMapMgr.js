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
 * Creates and initializes a manager for the given keymap.
 * @constructor
 * @class
 * A keymap manager parses the keymap into a form that is easily used for 
 * translating key codes into actions. It also provides some static methods
 * that map the available keyboard to key codes, and which qualify certain
 * keys as punctuation, etc.
 * 
 * @author Ross Dargahi
 *
 * @param keyMap [Object]		a keymap
 *
 */
DwtKeyMapMgr = function(keyMap) {
	var map = this._map = keyMap.getMap();
	this._args = keyMap._args;
	
	// build FSA for each mapping
	this._fsas = {};
	for (var key in map) {
		DBG.println(AjxDebug.DBG3, "building FSA for key: " + key);
		this._fsas[key] = DwtKeyMapMgr.__buildFSA({}, map[key], key);
	}
	DBG.dumpObj(AjxDebug.DBG3, this._fsas);
};

DwtKeyMapMgr.NOT_A_TERMINAL = -999;
DwtKeyMapMgr.TAB_KEYCODE = 9;

DwtKeyMapMgr._KEYCODES = [];	// Keycode map

DwtKeyMapMgr.prototype.toString =
function() {
	return "DwtKeyMapMgr";
};

/**
 * This method will attempt to look up the action code for a given key sequence in
 * a given key map. 
 * 
 * @param {string}		keySeq				key sequence to lookup
 * @param {string}		mappingName			keymap name in which to search
 * @param {boolean}		forceActionCode		if true, then if the key sequence contains both
 * 											a submap and an action code, then return the action code.
 * 											If this parameter is false or omitted, then
 * 											<i>DwtKeyMapMgr.NOT_A_TERMINAL</i> will be returned for
 * 											a key sequence that contains both a submap and an action code.
 * 
 * @return The action code for the provided key map name, null if there is no action code
 * 		or <i>DwtKeyMapMgr.NOT_A_TERMINAL</i> if the key sequence is an intermediate
 * 		node in the key map (i.e. has a submap)
 * @type string|number
 */
DwtKeyMapMgr.prototype.getActionCode =
function(keySeq, mappingName, forceActionCode) {
	//DBG.println(AjxDebug.DBG3, "Getting action code for: " + keySeq + " in map: " + mappingName);
	var mapping =  this._fsas[mappingName];

	if (!mapping) {
		DBG.println(AjxDebug.DBG3, "No keymap for: " + mappingName);
		return null;
	}

	var keySeqLen = keySeq.length;
	var tmpFsa = mapping;
	var key;
	for (var j = 0; j < keySeqLen && tmpFsa; j++) {
		key = keySeq[j];

		if (!tmpFsa || !tmpFsa[key]) break;

		if (j < keySeqLen - 1) {
			tmpFsa = tmpFsa[key].subMap;
		}
	}

	if (tmpFsa && tmpFsa[key]) {
		var binding = tmpFsa[key];
		/* If the binding does not have a submap, then it must have an action code
		 * so return it. Else if the binding does not have an action code (i.e. it
		 * has a submap only) or if forceActionCode is false, then return DwtKeyMapMgr.NOT_A_TERMINAL
		 * since we are to behave like an intermediate node. Else return the action code. */
		if (!binding.subMap || forceActionCode) {
			var inherited = this.__getInheritedActionCode(keySeq, mapping, forceActionCode);
			return inherited == DwtKeyMapMgr.NOT_A_TERMINAL ? DwtKeyMapMgr.NOT_A_TERMINAL : binding.actionCode;
		} else {
			return DwtKeyMapMgr.NOT_A_TERMINAL;
		}
	} else {
		return this.__getInheritedActionCode(keySeq, mapping, forceActionCode);
	}
};

/**
 * Converts the given keycode to a character
 * 
 * @param {number} keyCode Keycode to convert
 * 
 * @return character representation of the keyCode
 * @type string
 */
DwtKeyMapMgr.prototype.keyCode2Char =
function(keyCode) {
	return DwtKeyMapMgr._KEYCODES[keyCode];
};

/**
 * Returns the action for the given map and key sequence.
 */
DwtKeyMapMgr.prototype.getAction =
function(mapName, keySeq) {
	return this._map[mapName][keySeq];
};

/**
 * Returns the key sequences associated with the given map and action.
 */
DwtKeyMapMgr.prototype.getKeySequences =
function(mapName, action) {
	var keySeqs = [];
	for (var ks in this._map[mapName]) {
		if (this._map[mapName][ks] == action) {
			keySeqs.push(ks);
		}
	}
	return keySeqs;
};

/**
 * Allow the programmatic setting of a key sequence mapping for a given map
 * 
 * @param {string} 			mapName map name to affect
 * @param {string} 			keySeq the key sequence to set
 * @param {string|number} action the action code for the key sequence
 */
DwtKeyMapMgr.prototype.setMapping =
function(mapName, keySeq, action) {
	this._map[mapName][keySeq] = action;
};

/**
 * Allow the programatting removal of a key sequence mapping for a given map
 * 
 * @param {string} mapName map name to affect
 * @param {string} keySeq the key sequence to remove
 */
DwtKeyMapMgr.prototype.removeMapping =
function(mapName, keySeq) {
	delete this._map[mapName][keySeq];
};

/**
 * Replace the key sequence for a given action in a keymap 
 * 
 * @param {string} mapName map name to affect
 * @param {string} oldKeySeq the key sequence to replace
 * @param {string} newKeySeq the new key sequence
 */
DwtKeyMapMgr.prototype.replaceMapping =
function(mapName, oldKeySeq, newKeySeq) {
	var action =this._map[mapName][oldKeySeq];
	if (!action) return;
	this.removeMapping(mapName, oldKeySeq);
	this.setMapping(mapName, newKeySeq, action);
};

DwtKeyMapMgr.prototype.setArg =
function(mapName, action, arg) {
	if (!this._args[mapName]) {
		this._args[mapName] = {};
	}
	this._args[mapName][action] = arg;
};

DwtKeyMapMgr.prototype.getArg =
function(mapName, action) {
	return this._args[mapName] ? this._args[mapName][action] : null;
};

/**
 * Reloads a given keymap
 * 
 * @param {string} mapName Name of the keymap to releoad
 */
DwtKeyMapMgr.prototype.reloadMap =
function(mapName) {
	this._fsas[mapName] = DwtKeyMapMgr.__buildFSA({}, this._map[mapName], mapName);
};

DwtKeyMapMgr._isAlphaUs = 
function(keyCode) {
	return (keyCode > 64 && keyCode < 91);
};

DwtKeyMapMgr._isNumericUs = 
function(keyCode) {
	return (keyCode > 47 && keyCode < 58);
};

DwtKeyMapMgr._isAlphanumericUs = 
function(keyCode) {
	return (DwtKeyMapMgr._isNumericUs(keyCode) || DwtKeyMapMgr._isAlphaUs(keyCode));
};

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
};

DwtKeyMapMgr._isModifierUs = 
function(keyCode) {
	switch (keyCode) {
		case 16: // Shift
		case 17: // Ctrl
		case 18: // Alt
		case 91: // Meta (Apple)
			return true;
			
		default:
			return false;
	}
};

DwtKeyMapMgr.isUsableTextInputValueUs =
function(keyCode, element) {

	if (!DwtKeyMapMgr.__isInputElement(element)) return false;
	
	if (DwtKeyMapMgr._isAlphanumericUs(keyCode) || DwtKeyMapMgr._isPunctuationUs(keyCode)) return true;
		
	switch (keyCode) {
		case 37:
		case 39:
		case 8:
		case 45:
		case 46:
		case 35:
		case 32:
			return true;

		case 3:
		case 13:
			var tag = element.tagName.toUpperCase();
			return (tag != "INPUT");
			
		default:
			return false;
	}	
};

DwtKeyMapMgr.__isInputElement =
function(element) {
	// Are we in an HTML editor? If so, target for IE is <body>, for FF is <html>
	var dm = element.ownerDocument ? element.ownerDocument.designMode : null;
	if (dm && (dm.toLowerCase() == "on")) return true;

	var tag = element.tagName.toUpperCase();
	return (tag == "INPUT" || tag == "TEXTAREA");
};

DwtKeyMapMgr.__buildFSA =
function(fsa, mapping, mapName) {
	for (var i in mapping) {
		// check for inheritance from other maps (in CSV list)
		if (i == DwtKeyMap.INHERIT) {
			fsa.inherit = mapping[i].split(/\s*,\s*/);
			continue;
		}
		 
		var keySeq = i.split(DwtKeyMap.SEP);
		var keySeqLen = keySeq.length;
		var tmpFsa = fsa;
		for (var j = 0; j < keySeqLen; j++) {
			var key = keySeq[j];
			//DBG.println(AjxDebug.DBG3, "Processing: " + key);
			
			if (!tmpFsa[key]) {
				tmpFsa[key] = {};	// first time visiting this key
			}

			if (j == keySeqLen - 1) {
				/* We are at the last key in the sequence so we can bind the
				 * action code to it */
				//DBG.println(AjxDebug.DBG3, "BINDING: " + mapping[i]);
				tmpFsa[key].actionCode = mapping[i];
			} else {
				/* We have more keys in the sequence. If our subMap is null,
				 * then we need to create it to hold the new key sequences */
				if (!tmpFsa[key].subMap) {
					tmpFsa[key].subMap = {};
					//DBG.println(AjxDebug.DBG3, "NEW SUBMAP");
				}
					
				tmpFsa = tmpFsa[key].subMap;
			}			
		}
	}
	return fsa;
};

DwtKeyMapMgr.prototype.__getInheritedActionCode =
function(keySeq, mapping, forceActionCode) {
	if (mapping.inherit && mapping.inherit.length) {
		var actionCode = null;
		var len = mapping.inherit.length;
		for (var i = 0; i < len; i++) {
			DBG.println(AjxDebug.DBG3, "checking inherited map: " + mapping.inherit[i]);
			actionCode = this.getActionCode(keySeq, mapping.inherit[i], forceActionCode);
			if (actionCode != null) {
				return actionCode;
			}
		}
	}
	return null;
};

(function() {
	DwtKeyMapMgr._KEYCODES[18]  = DwtKeyMap.ALT;
	DwtKeyMapMgr._KEYCODES[40]  = DwtKeyMap.ARROW_DOWN;
	DwtKeyMapMgr._KEYCODES[37]  = DwtKeyMap.ARROW_LEFT;
	DwtKeyMapMgr._KEYCODES[39]  = DwtKeyMap.ARROW_RIGHT;
	DwtKeyMapMgr._KEYCODES[38]  = DwtKeyMap.ARROW_UP;
	DwtKeyMapMgr._KEYCODES[8]   = DwtKeyMap.BACKSPACE;
	DwtKeyMapMgr._KEYCODES[188] = DwtKeyMap.COMMA;
	DwtKeyMapMgr._KEYCODES[186] = DwtKeyMap.SEMICOLON;
	DwtKeyMapMgr._KEYCODES[59]  = DwtKeyMap.SEMICOLON;
	DwtKeyMapMgr._KEYCODES[17]  = DwtKeyMap.CTRL;
	DwtKeyMapMgr._KEYCODES[46]  = DwtKeyMap.DELETE;
	DwtKeyMapMgr._KEYCODES[35]  = DwtKeyMap.END;
	DwtKeyMapMgr._KEYCODES[13]  = DwtKeyMap.ENTER;
	DwtKeyMapMgr._KEYCODES[27]  = DwtKeyMap.ESC;
	DwtKeyMapMgr._KEYCODES[36]  = DwtKeyMap.HOME;
	DwtKeyMapMgr._KEYCODES[91]  = DwtKeyMap.META;
	DwtKeyMapMgr._KEYCODES[34]  = DwtKeyMap.PAGE_DOWN;
	DwtKeyMapMgr._KEYCODES[33]  = DwtKeyMap.PAGE_UP;
	DwtKeyMapMgr._KEYCODES[16]  = DwtKeyMap.SHIFT;
	DwtKeyMapMgr._KEYCODES[32]  = DwtKeyMap.SPACE;
	DwtKeyMapMgr._KEYCODES[9]   = DwtKeyMap.TAB;
	DwtKeyMapMgr._KEYCODES[220] = DwtKeyMap.BACKSLASH;
	
	// Function keys
	for (var i = 112; i < 124; i++) {
		DwtKeyMapMgr._KEYCODES[i] = "F" + (i - 111);
	}
	
	// Take advantage of the fact that keycode for capital letters are the 
	// same as the charcode values i.e. ASCII code
	for (var i = 65; i < 91; i++) {
		DwtKeyMapMgr._KEYCODES[i] = String.fromCharCode(i);
	}

	// Numbers 0 - 9
	for (var i = 48; i < 58; i++) {
		DwtKeyMapMgr._KEYCODES[i] = String.fromCharCode(i);
	}
		
	// punctuation
	DwtKeyMapMgr._KEYCODES[222] = "'";
	DwtKeyMapMgr._KEYCODES[189] = "-";
	DwtKeyMapMgr._KEYCODES[190] = ".";
	DwtKeyMapMgr._KEYCODES[191] = "/";
	DwtKeyMapMgr._KEYCODES[186] = ";";
	DwtKeyMapMgr._KEYCODES[219] = "[";
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
})();
