/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010, 2013 Zimbra Software, LLC.
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
* Creates a key mapping.
* @constructor
* @class
* 
* @author Ross Dargahi
*/
ZaKeyMap = function() {
	DwtKeyMap.call(this);
	// Note that FF on the mac has an issue reporting the ALT+<keycode> it
	// always ends up reporting undefined for the <keycode>. For this reason I
	// have added Ctrl analogs below	
	this._map["ZaGlobal"] = {

			"Alt+Shift+D,0": ZaKeyMap.DBG_NONE,
			"Ctrl+Shift+D,0": ZaKeyMap.DBG_NONE, // Mac issue with Alt+Key

			"Alt+Shift+D,1": ZaKeyMap.DBG_1,
			"Ctrl+Shift+D,1": ZaKeyMap.DBG_1,

			"Alt+Shift+D,2": ZaKeyMap.DBG_2,
			"Ctrl+Shift+D,2": ZaKeyMap.DBG_2,

			"Alt+Shift+D,3": ZaKeyMap.DBG_3,
			"Ctrl+Shift+D,3": ZaKeyMap.DBG_3,

			"Alt+S":   ZaKeyMap.SAVE,
			"Ctrl+S": ZaKeyMap.SAVE,

			"Del":        ZaKeyMap.DEL,
			"Backspace":  ZaKeyMap.DEL, // MacBook keyboard
			"Esc":        ZaKeyMap.CANCEL,
			"ArrowRight": ZaKeyMap.NEXT_PAGE,
			"ArrowLeft":  ZaKeyMap.PREV_PAGE
	};
		
}

ZaKeyMap.prototype = new DwtKeyMap(true);
ZaKeyMap.prototype.constructor = ZaKeyMap;

// Key map action code contants
var i = 0;

ZaKeyMap.CANCEL = i++;
ZaKeyMap.DBG_NONE = i++;
ZaKeyMap.DBG_1 = i++;
ZaKeyMap.DBG_2 = i++;
ZaKeyMap.DBG_3 = i++;
ZaKeyMap.DEL = i++;
ZaKeyMap.NEXT_CONV = i++;
ZaKeyMap.NEXT_PAGE = i++;
ZaKeyMap.PREV_CONV = i++;
ZaKeyMap.PREV_PAGE = i++;
ZaKeyMap.SAVE = i++;


delete i;