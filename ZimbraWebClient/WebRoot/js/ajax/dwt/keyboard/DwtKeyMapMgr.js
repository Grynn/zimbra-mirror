/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
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
 * @param {DwtKeyMap}	keyMap the keymap
 *
 * @private
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
 * @param {boolean}		forceActionCode		if <code>true</code>, then if the key sequence contains both
 * 											a submap and an action code, then return the action code.
 * 											If this parameter is false or omitted, then
 * 											{@link DwtKeyMapMgr.NOT_A_TERMINAL} will be returned for
 * 											a key sequence that contains both a submap and an action code.
 * 
 * @return {string|number}	the action code for the provided key map name, null if there is no action code
 * 		or {@link DwtKeyMapMgr.NOT_A_TERMINAL} if the key sequence is an intermediate
 * 		node in the key map (i.e. has a submap)
 * 
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
            //if keyMap not available then return the inherited keyMap.
            return inherited == DwtKeyMapMgr.NOT_A_TERMINAL ? DwtKeyMapMgr.NOT_A_TERMINAL : ( binding.actionCode || inherited );
		} else {
			return DwtKeyMapMgr.NOT_A_TERMINAL;
		}
	} else {
		return this.__getInheritedActionCode(keySeq, mapping, forceActionCode);
	}
};

/**
 * Returns the action for the given map and key sequence.
 * 
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
	var action = this._map[mapName][oldKeySeq];
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

DwtKeyMapMgr.prototype.removeArg =
function(mapName, action) {
	delete this._args[mapName][action];
};

DwtKeyMapMgr.prototype.getArg =
function(mapName, action) {
	return this._args[mapName] ? this._args[mapName][action] : null;
};

/**
 * Reloads a given keymap
 * 
 * @param {string} mapName Name of the keymap to reload
 */
DwtKeyMapMgr.prototype.reloadMap =
function(mapName) {
	this._fsas[mapName] = DwtKeyMapMgr.__buildFSA({}, this._map[mapName], mapName);
};

/**
 * Returns a list of maps that the given map inherits from.
 *
 * @param {string} mapName Name of the keymap to reload
 */
DwtKeyMapMgr.prototype.getAncestors =
function(mapName, list) {
    list = list || [];
    var subMap = this._fsas[mapName];
    var parents = subMap && subMap.inherit;
    if (parents && parents.length) {
        for (var i = 0; i < parents.length; i++) {
            list.push(parents[i]);
            list = this.getAncestors(parents[i], list);
        }
    }
    return list;
};

/**
 * Returns true if the given element accepts text input.
 * 
 * @param element	[Element]		DOM element
 */
DwtKeyMapMgr.isInputElement =
function(element) {
	if (!element) { return false; }
	// Check designMode in case we're in an HTML editor iframe
	var dm = element.ownerDocument ? element.ownerDocument.designMode : null;
	if (dm && (dm.toLowerCase() == "on")) { return true; }

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

/**
 * Returns true if the given key event has a modifier which makes it nonprintable.
 * 
 * @param ev	[Event]		key event
 */
DwtKeyMapMgr.hasModifier =
function(ev) {
	return (ev.altKey || ev.ctrlKey || ev.metaKey);
};
