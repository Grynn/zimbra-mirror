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
* .
* @constructor
* @class
* This class is reponsible for managing keyboard events. This includes dispatching
* keyboard events, as well as managing focus and tab groups
* 
* @author Ross Dargahi
*
*/
function DwtKeyboardMgr() {
	this._tabGrpStack = new Array();
	this._tagGroupChangeListenerObj = new AjxListener(this._tabGrpChangeListener);
}

DwtKeyboardMgr.KEYMAP_NOT_REGISTERED = "KEYMAP NOT REGISTERED";

// Private constants
DwtKeyboardMgr._KEYSEQ_NOT_HANDLED = 1;
DwtKeyboardMgr._KEYSEQ_HANDLED = 2;
DwtKeyboardMgr._KEYSEQ_PENDING = 3;

DwtKeyboardMgr.prototype.pushTabGroup =
function(tabGroup) {
	if (!this._keyboardHandlingInited)
		throw DwtKeyboardMgr.KEYMAP_NOT_REGISTERED;
		
	this._tabGrpStack.push(tabGroup);
	tabGroup.addChangeListener(this._tagGroupChangeListenerObj);
	DwtKeyboardMgr.grabFocus(tabGroup.getFocusMember());
	
}

DwtKeyboardMgr.prototype.popTabGroup =
function() {
	if (!this._keyboardHandlingInited)
		throw DwtKeyboardMgr.KEYMAP_NOT_REGISTERED;
		
	var tabGroup = this._tabGrpStack.pop();
	tabGroup.removeChangeListener(this._tagGroupChangeListenerObj);
	
	if (this._tabGrpStack.length > 0)
		DwtKeyboardMgr.grabFocus(this._tabGrpStack[this._tabGrpStack.length-1]);
	
	return tabGroup;	
}

DwtKeyboardMgr.prototype.setTabGroup =
function(tabGroup) {
	if (!this._keyboardHandlingInited)
		throw DwtKeyboardMgr.KEYMAP_NOT_REGISTERED;
		
	var otg = this.popTabGroup();
	this.pushTabGroup(tabGroup);
	return otg;
}



// Document this. Will also need a push and pop focus frames for things
// like dialogs etc
DwtKeyboardMgr.grabFocus =
function(focusObj) {
	//DBG.println( GRAB FOCUS");
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	if (!kbMgr._keyboardHandlingInited)
		return;
		
	if (focusObj instanceof DwtControl) {
		kbMgr._focusObj = focusObj;
		kbMgr._kbFocusField.focus();
	} else {
		// dealing with a type of HTML input field
		focusObj.focus();
	}
}

/**
* Return true if the specified component currently has focus
*
* @param obj  [object] Object for which to check focus
*/
DwtKeyboardMgr.objectHasFocus =
function(obj) {
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	if (!kbMgr._keyboardHandlingInited)
		return false;
		
	return (kbMgr._haveFocus && kbMgr._focusObj == obj);
}

/** 
* This method is used to register a global key handler. If registered, this
* handler must support the following methods:
* <ul>
* <li> getKeyMapName: This method returns a string representing the key map 
* to be used for looking up actions
* <li> handleKeyAction: This method should handle the key action and return
* true if it handled it else false. handleKeyAction has two formal parameters
*    <ul>
*    <li> actionCode: The action code to be handled
*    <li> ev: DwtKeyEvent corresponding to the last key event in the sequence
*    </ul>
* </ul>
**/
DwtKeyboardMgr.prototype.registerGlobalKeyActionHandler =
function(hdlr) {
	this._globalKeyActionHdlr = hdlr;
}

/**
* This method is used to register a keymap with the shell. A keymap typically
* is a subclass of DwtKeyMap and defines the mappings from key sequences to
* actions
*
* keyMap [DwtKeyMap] Keymap for the application
**/
DwtKeyboardMgr.prototype.registerKeyMap =
function(keyMap) {
	// Setup Keyboard handling not initialized, then initialize it
	if (!this._keyboardHandlingInited)
		this._initKeyboardHandling();
	this._keyMapMgr = new DwtKeyMapMgr(keyMap);
}


DwtKeyboardMgr.prototype._initKeyboardHandling =
function() {
	DBG.println("INITIALIZING KeyboardHandling");
	Dwt.setHandler(document, DwtEvent.ONKEYDOWN, DwtKeyboardMgr._keyDownHdlr);
//	Dwt.setHandler(document, DwtEvent.ONKEYUP, DwtKeyboardMgr._keyUpHdlr);
	Dwt.setHandler(document, DwtEvent.ONKEYPRESS, DwtKeyboardMgr._keyPressHdlr);
	/* Create our keyboard focus field. This is a dummy input field that will take text
	 * input for keyboard shortcuts */
	var kbff = this._kbFocusField = document.createElement("input");
	kbff.type = "text";
	kbff.style.position = Dwt.ABSOLUTE_STYLE;
	kbff.style.top = kbff.style.left = Dwt.LOC_NOWHERE;
	kbff.onblur = DwtKeyboardMgr._onBlurHdlr;
	kbff.onfocus = DwtKeyboardMgr._onFocusHdlr;
	document.body.appendChild(kbff);

	this._killKeySeqTimedAction = new AjxTimedAction(this, this._killKeySequenceAction);
	this._killKeySeqTimedActionId = -1;
	this._keySequence = new Array();

	this._keyboardHandlingInited = true;
};

DwtKeyboardMgr._onFocusHdlr =
function(ev) {
	DBG.println("DwtKeyboardMgr._onFocusHdlr");
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	kbMgr._haveFocus = true;
	var focusObj = kbMgr._focusObj;
	if (focusObj != null && focusObj._focus != null && (typeof focusObj._focus == "function"))
		focusObj._focus();			
}

DwtKeyboardMgr._onBlurHdlr =
function(ev) {
	DBG.println("DwtKeyboardMgr._onBlurHdlr");
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	var focusObj = kbMgr._focusObj;
	if (focusObj != null && focusObj._blur != null && (typeof focusObj._blur == "function"))
		focusObj._blur();		
	kbMgr._haveFocus = false;	
};

// Currently not being used
DwtKeyboardMgr._keyUpHdlr =
function(ev) {
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = kev.keyCode;
	
	if (keyCode == DwtKeyMapMgr.TAB_KEYCODE) {
		kev._stopPropagation = true;
		kev._returnValue = false;
		kev.setToDhtmlEvent(ev);
		return false;
	 } 
}

DwtKeyboardMgr._keyPressHdlr =
function(ev) {
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = kev.keyCode;
	
	if (kbMgr._kbEventStatus != DwtKeyboardMgr._KEYSEQ_NOT_HANDLED) {
		DBG.println("KE BEING BLOCKED IN KP HDLR");
		kev._stopPropagation = true;
		kev._returnValue = false;
		kev.setToDhtmlEvent(ev);
		return false;
	 } 
}


DwtKeyboardMgr._keyDownHdlr =
function(ev) {
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = kev.keyCode;
	
	
	/* The first thing we care about is the tab key since we want to manage
	 * focus based on the tab groups
	 */
	 if (keyCode == DwtKeyMapMgr.TAB_KEYCODE) {
	 	DBG.println("TAB HIT!");
/*	 	if (!kev.shiftKey)
	 		kbMgr.grabFocus(kbMgr._curTabGroup.nextTabItem());
	 	else
	 		kbMgr.grabFocus(kbMgr._curTabGroup.prevTabItem());
*/
		kbMgr._kbEventStatus = DwtKeyboardMgr._KEYSEQ_NOT_HANDLED;
		kev._stopPropagation = true;
		kev._returnValue = false;
		kev.setToDhtmlEvent(ev);
		return false;
	 } 
	 
	/* If the focus object is a DwtControl, then set the value of the keyboard
	 * focus field to "" so that it doesn't overflow or get crufty
	 */
	if (kbMgr._focusObj instanceof DwtControl) 
		kbMgr._kbFocusField.value = "";
	 
	/* Filter out the following keys: Alt, Shift, Ctrl. Also filter out
	 * alphanumeric keys if the target of the key event is an input field
	 * or a text area and there is no pending sequence in play and the key
	 * is alphanumeric or a punctuation key */
	var tagName = (kev.target) ? kev.target.tagName.toLowerCase() : null;
	DBG.println("KEYCODE: " + keyCode + " - tagName: " + tagName);
	if (DwtKeyMapMgr.isModifier(keyCode)
		|| (!kbMgr._haveFocus 
			&& kbMgr._killKeySeqTimedActionId == -1 && !kev.ctrlKey && !kev.altKey
			&& DwtKeyMapMgr.isUsableTextInputValue(keyCode))) {
		kev._stopPropagation = false;
		kev._returnValue = true;
		kev.setToDhtmlEvent(ev);
		return true;
	}
	 
	/* Cancel any pending time action to kill the keysequence */
	if (kbMgr._killKeySeqTimedActionId != -1) {
		AjxTimedAction.cancelAction(kbMgr._killKeySeqTimedActionId);
		kbMgr._killKeySeqTimedActionId = -1;
	}
		
 	var key = "";
	
	if (kev.ctrlKey)
		key += DwtKeyMap.CTRL;
		
	if (kev.altKey)
		key += DwtKeyMap.ALT;
		
	if (kev.shiftKey)
		key += DwtKeyMap.SHIFT;
	
	kbMgr._keySequence[kbMgr._keySequence.length] = key + kbMgr._keyMapMgr.keyCode2Char(keyCode);

	DBG.println("KEYCODE: " + keyCode + " - KEY SEQ: " + kbMgr._keySequence.join(""));

	var obj = (kbMgr._haveFocus) ? kbMgr._focusObj : null;
	var handled = DwtKeyboardMgr._KEYSEQ_NOT_HANDLED;
	
	/* If a DWT component has "focus", then dispatch to that component
	 * if the component handles the event, then stop, else hand it off
	 * the global handler if one is registered */
	//DBG.println("Focus Object: " + obj.toString());
	if (obj != null && (obj instanceof DwtControl)) {
		handled = kbMgr._dispatchKeyEvent(obj, obj.toString(), kev);
	}
	 	
	DBG.println("handled: " + handled);
	if (handled == DwtKeyboardMgr._KEYSEQ_NOT_HANDLED && kbMgr._globalKeyActionHdlr != null)
		handled = kbMgr._dispatchKeyEvent(kbMgr._globalKeyActionHdlr, 
							kbMgr._globalKeyActionHdlr.getKeyMapNameToUse(), kev);
	
	kbMgr._kbEventStatus = handled;
	switch (handled) {
		case DwtKeyboardMgr._KEYSEQ_NOT_HANDLED:
			kbMgr._keySequence.length = 0;
			kev._stopPropagation = false;
			kev._returnValue = true;
			kev.setToDhtmlEvent(ev);
			return true;
			break;
			
		case DwtKeyboardMgr._KEYSEQ_HANDLED:
			kbMgr._keySequence.length = 0;
		case DwtKeyboardMgr._KEYSEQ_PENDING:
			kev._stopPropagation = true;
			kev._returnValue = false;
			kev.setToDhtmlEvent(ev);
			return false;
			break;
	}
};

DwtKeyboardMgr.prototype._dispatchKeyEvent = 
function(hdlr, mapName, ev) {
	actionCode = this._keyMapMgr.getActionCode(this._keySequence, mapName);
	terminal = this._keyMapMgr.isTerminal(this._keySequence, mapName);
	if (actionCode != null) {
		DBG.println("HANDLING ACTION");
		return (hdlr.handleKeyAction(actionCode, ev)) 
			? DwtKeyboardMgr._KEYSEQ_HANDLED : DwtKeyboardMgr._KEYSEQ_NOT_HANDLED;
	} else if (!terminal) {
		DBG.println("SCHEDULING KILL SEQUENCE ACTION");
		/* setup a timed action to kill the key sequence in the event
		 * the user does not press another key in the allotted time */
		this._killKeySeqTimedActionId = 
			AjxTimedAction.scheduleAction(this._killKeySeqTimedAction, 1000);
		return DwtKeyboardMgr._KEYSEQ_PENDING;
	} else {	
		DBG.println("TERMINAL W/O ACTION CODE");
		return DwtKeyboardMgr._KEYSEQ_NOT_HANDLED;
	}
};

DwtKeyboardMgr.prototype._killKeySequenceAction =
function() {
	//DBG.println("KILLING KEY SEQUENCE");
	this._killKeySeqTimedActionId = -1;
	this._keySequence.length = 0;
};
