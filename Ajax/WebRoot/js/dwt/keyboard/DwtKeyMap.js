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
* This class provides the basic keyboard mappings for Dwt components. It is also
* the base class for application keymaps. 
* 
* @author Ross Dargahi
*/
function DwtKeyMap(subclassInit) {
	if (subclassInit) 
		return;
		
	/* Always specify Control, then Alt, then Shift. All Chars must be upper case
	 */
	this._map = {};
	
	this._map["DwtBaseDialog"] = {
		"Enter":			DwtKeyMap.ENTER
	};

	this._map["DwtButton"] = {
		"Enter":			DwtKeyMap.SELECT_CURRENT,
		"ArrowDown":		DwtKeyMap.SELECT_SUBMENU	
	};
	
	this._map["DwtListView"] = {
		"Space":			DwtKeyMap.SELECT_CURRENT,
			
		"Ctrl+Space":		DwtKeyMap.ADD_SELECT_CURRENT,
		"Ctrl+`":			DwtKeyMap.ADD_SELECT_CURRENT, // Mac FF
			
		"ArrowDown":		DwtKeyMap.SELECT_NEXT,
		"Shift+ArrowDown":	DwtKeyMap.ADD_SELECT_NEXT,
		"Ctrl+ArrowDown":	DwtKeyMap.NEXT,
		"ArrowUp":			DwtKeyMap.SELECT_PREV,
		"Shift+ArrowUp":	DwtKeyMap.ADD_SELECT_PREV,
		"Ctrl+ArrowUp":		DwtKeyMap.PREV,
		"Enter":			DwtKeyMap.DBLCLICK,
		"Ctrl+Enter":		DwtKeyMap.ACTION,
		"Ctrl+M":			DwtKeyMap.ACTION  // Mac FF
	};
	
	this._map["DwtMenu"] = {
		"Esc":				DwtKeyMap.CANCEL,
		"Enter":			DwtKeyMap.SELECT_CURRENT,	
		"ArrowDown":		DwtKeyMap.SELECT_NEXT,
		"ArrowUp":			DwtKeyMap.SELECT_PREV,
		"ArrowLeft":		DwtKeyMap.SELECT_PARENTMENU,
		"ArrowRight":		DwtKeyMap.SELECT_SUBMENU
	};
};


DwtKeyMap.deserialize =
function(keymap) {
	alert("DwtKeyMap.deserialize: NOT IMPLEMENTED");
}

DwtKeyMap.serialize =
function(keymap) {
	alert("DwtKeyMap.serialize: NOT IMPLEMENTED");
}

// Key names

DwtKeyMap.CTRL			= "Ctrl+";
DwtKeyMap.ALT			= "Alt+";
DwtKeyMap.SHIFT			= "Shift+";

DwtKeyMap.ARROW_DOWN	= "ArrowDown";
DwtKeyMap.ARROW_LEFT	= "ArrowLeft";
DwtKeyMap.ARROW_RIGHT	= "ArrowRight";
DwtKeyMap.ARROW_UP		= "ArrowUp";
DwtKeyMap.BACKSPACE		= "Backspace";
DwtKeyMap.DELETE		= "Del";
DwtKeyMap.END			= "End";
DwtKeyMap.ENTER			= "Enter";
DwtKeyMap.ESC			= "Esc";
DwtKeyMap.HOME			= "Home";
DwtKeyMap.PAGE_DOWN		= "PgDown";
DwtKeyMap.PAGE_UP		= "PgUp";
DwtKeyMap.SPACE			= "Space";

// Key map action code contants. If providing your own key map then make
// your codes positive integers
var i = -1;

DwtKeyMap.ACTION				= i--;
DwtKeyMap.ADD_SELECT_CURRENT	= i--;
DwtKeyMap.ADD_SELECT_NEXT		= i--;
DwtKeyMap.ADD_SELECT_PREV		= i--;
DwtKeyMap.CANCEL				= i--;
DwtKeyMap.DBLCLICK				= i--;
DwtKeyMap.DONE					= i--;
DwtKeyMap.NEXT					= i--;
DwtKeyMap.PREV					= i--;
DwtKeyMap.SELECT_CURRENT		= i--;
DwtKeyMap.SELECT_NEXT			= i--;
DwtKeyMap.SELECT_PREV			= i--;
DwtKeyMap.SELECT_SUBMENU		= i--;
DwtKeyMap.SELECT_PARENTMENU		= i--;

delete i;

DwtKeyMap.SEP = ","; // Key separator
DwtKeyMap.INHERIT = "INHERIT"; // Inherit keyword.

DwtKeyMap.prototype.getMap =
function() {
	return this._map;
}
