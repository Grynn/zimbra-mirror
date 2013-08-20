/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
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
 * 
 * @private
 */
DwtKeyEvent = function() {
	DwtUiEvent.call(this, true);
	this.reset(true);
}


DwtKeyEvent.KEY_END_OF_TEXT =  0x03;
DwtKeyEvent.KEY_TAB = 0x09;
DwtKeyEvent.KEY_RETURN = 0x0D;
DwtKeyEvent.KEY_ENTER = 0x0D;
DwtKeyEvent.KEY_ESCAPE = 0x1B;

// FF on Mac reports keyCode of 0 for many shifted keys
DwtKeyEvent.MAC_FF_CODE = {};
DwtKeyEvent.MAC_FF_CODE["~"] = 192;
DwtKeyEvent.MAC_FF_CODE["!"] = 49;
DwtKeyEvent.MAC_FF_CODE["@"] = 50;
DwtKeyEvent.MAC_FF_CODE["#"] = 51;
DwtKeyEvent.MAC_FF_CODE["$"] = 52;
DwtKeyEvent.MAC_FF_CODE["%"] = 53;
DwtKeyEvent.MAC_FF_CODE["^"] = 54;
DwtKeyEvent.MAC_FF_CODE["&"] = 55;
DwtKeyEvent.MAC_FF_CODE["*"] = 56;
DwtKeyEvent.MAC_FF_CODE["("] = 57;
DwtKeyEvent.MAC_FF_CODE[")"] = 48;
DwtKeyEvent.MAC_FF_CODE["-"] = 189;
DwtKeyEvent.MAC_FF_CODE["_"] = 189;
DwtKeyEvent.MAC_FF_CODE["+"] = 187;
DwtKeyEvent.MAC_FF_CODE["|"] = 220;
DwtKeyEvent.MAC_FF_CODE[":"] = 186;
DwtKeyEvent.MAC_FF_CODE["<"] = 188;
DwtKeyEvent.MAC_FF_CODE[">"] = 190;
DwtKeyEvent.MAC_FF_CODE["?"] = 191;

DwtKeyEvent.prototype = new DwtUiEvent;
DwtKeyEvent.prototype.constructor = DwtKeyEvent;

DwtKeyEvent.prototype.toString =
function() {
	return "DwtKeyEvent";
}

DwtKeyEvent.isKeyEvent =
function(ev) {
	return (ev.type.search(/^key/i) != -1);
}

DwtKeyEvent.isKeyPressEvent =
function(ev) {
	return (AjxEnv.isIE && ev.type == "keydown") || (ev.type == "keypress");
}

DwtKeyEvent.prototype.reset =
function(dontCallParent) {
	if (!dontCallParent)
		DwtUiEvent.prototype.reset.call(this);
	this.keyCode = 0;
	this.charCode = 0;
}

DwtKeyEvent.prototype.isCommand =
function(ev) {
	return AjxEnv.isMac && this.metaKey || this.ctrlKey;
}

DwtKeyEvent.prototype.setFromDhtmlEvent =
function(ev, obj) {
	ev = DwtUiEvent.prototype.setFromDhtmlEvent.apply(this, arguments);
	if (!ev) { return; }
	this.charCode = ev.charCode || ev.keyCode;
	this.keyCode = ev.keyCode;
}

/**
 * Simple function to return key code from a key event. The code is in keyCode for keydown/keyup.
 * Gecko puts it in charCode for keypress.
 */
DwtKeyEvent.getCharCode =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	var key = AjxEnv.isSafari ? ev.keyCode : (ev.charCode || ev.keyCode);
	if (key == 0 && AjxEnv.isMac && AjxEnv.isGeckoBased && ev.type == "keyup" && DwtKeyEvent._geckoCode) {
		// if Mac Gecko, return keyCode saved from keypress event
		key = DwtKeyEvent._geckoCode;
		DwtKeyEvent._geckoCode = null;
	}
	return key;
}

DwtKeyEvent.copy =
function(dest, src) {
	DwtUiEvent.copy(dest, src);
	dest.charCode = src.charCode;
	dest.keyCode = src.keyCode;
}

/**
 * Workaround for the bug where Mac Gecko returns a keycode of 0 for many shifted chars for
 * keydown and keyup. Since it returns a char code for keypress, we save it so that the
 * ensuing keyup can pick it up.
 *
 * FF2 returns keycode 0 for: ~ ! @ # $ % ^ & * ( ) - _ + | : < > ? Alt-anything
 * FF3 returns keycode 0 for: ~ _ | : < > ?
 *
 * FF2 returns incorrect keycode for Ctrl plus any of: 1 2 3 4 5 6 7 8 9 0 ; ' , . /
 *
 * https://bugzilla.mozilla.org/show_bug.cgi?id=448434
 *
 * @param ev
 */
DwtKeyEvent.geckoCheck =
function(ev) {

	ev = DwtUiEvent.getEvent(ev);
	if (ev.type == "keypress") {
		DwtKeyEvent._geckoCode = null;
		if (AjxEnv.isMac && AjxEnv.isGeckoBased) {
			var ch = String.fromCharCode(ev.charCode);
			DwtKeyEvent._geckoCode = DwtKeyEvent.MAC_FF_CODE[ch];
		}
	}
};
