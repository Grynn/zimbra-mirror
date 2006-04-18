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
	this._tabGroupChangeListenerObj = new AjxListener(this, this._tabGrpChangeListener);
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
	tabGroup.addFocusChangeListener(this._tabGroupChangeListenerObj);
	this.grabFocus(tabGroup.getFocusMember());
	this._currTabGroup = tabGroup;	
}

DwtKeyboardMgr.prototype.popTabGroup =
function() {
	if (!this._keyboardHandlingInited)
		throw DwtKeyboardMgr.KEYMAP_NOT_REGISTERED;
		
	var tabGroup = this._tabGrpStack.pop();
	
	if (tabGroup)
		tabGroup.removeChangeListener(this._tabGroupChangeListenerObj);
	
	if (this._tabGrpStack.length > 0)
		DwtKeyboardMgr.grabFocus(this._tabGrpStack[this._tabGrpStack.length-1]);
	this._currTabGroup = tabGroup;
	return tabGroup;	
}

DwtKeyboardMgr.prototype.setTabGroup =
function(tabGroup) {
	if (!this._keyboardHandlingInited)
		throw DwtKeyboardMgr.KEYMAP_NOT_REGISTERED;
	
	var otg = this.popTabGroup();
	this.pushTabGroup(tabGroup);
	this._currTabGroup = tabGroup;
	return otg;
}



/**
 * Sets the focus to <focusObj>.
 * 
 * @param focusObj Object to which to set focus
 */ 
DwtKeyboardMgr.prototype.grabFocus =
function(focusObj) {
	//DBG.println( GRAB FOCUS");
	if (!this._keyboardHandlingInited || this._focusObj == focusObj)
		return;

	this._currTabGroup.setFocusMember(focusObj, true);
	this._doGrabFocus(focusObj);
}

// Document this. Will also need a push and pop focus frames for things
// like dialogs etc
DwtKeyboardMgr.prototype._doGrabFocus =
function(focusObj) {
DBG.println("_doGrabFocus");
	if (!focusObj)
		return;
		
	if (focusObj instanceof DwtControl) {
		DBG.println("focusObj is instance of DwtControl: " + focusObj);
		/* If the current focus of obj and the one grabbing focus are both DwtControls
		 * then we need to simulate a blur on the control losing focus */
		if (this._dwtCtrlHasFocus && this._focusObj instanceof DwtControl) {
			DwtKeyboardMgr._onBlurHdlr();
			this._dwtCtrlHasFocus = true;
		}
			
		this._focusObj = focusObj;
		
		/* If a DwtControl already has focus, then we need to manually call
		 * DwtKeyboardMgr._onFocusHdlr to simulate focus since calling the focus()
		 * method on the input field does nothing*/
		if (this._dwtCtrlHasFocus) {
			DwtKeyboardMgr._onFocusHdlr()
		} else {
			this._kbFocusField.focus();
		}
	} else {
		// dealing with a type of HTML input field
		DBG.println("focusObj is instance of input field: " + focusObj);
		DBG.println("oldFocusObj is: " + this._focusObj);
		this._oldFocusObj = this._focusObj;
		this._focusObj = focusObj;
		focusObj.focus();
	}
}

/**
* Return true if the specified component currently has focus
*
* @param ctrl  [DwtControl] Object for which to check focus
*/
DwtKeyboardMgr.prototype.dwtControlHasFocus =
function(ctrl) {
	if (!this._keyboardHandlingInited)
		return false;
		
	return (this._dwtCtrlHasFocus && this._focusObj == obj);
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
	kbff.tabIndex = 0;
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
	kbMgr._dwtCtrlHasFocus = true;
	var focusObj = kbMgr._focusObj;
	if (focusObj != null && focusObj._focus != null && (typeof focusObj._focus == "function"))
		focusObj._focus();			
}

DwtKeyboardMgr._onBlurHdlr =
function(ev) {
	DBG.println("DwtKeyboardMgr._onBlurHdlr");
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	var focusObj;
	
	// Got to play the trick with HTML elements which get focus before blur is called
	// on the old focus object. (see _grabFocus)
	if (kbMgr._oldFocusObj == null) {
		focusObj = kbMgr._focusObj;
	} else {
		focusObj = kbMgr._oldFocusObj;
		kbMgr._oldFocusObj = null;
	}
	
	if (focusObj != null && focusObj._blur != null && (typeof focusObj._blur == "function"))
		focusObj._blur();		
	kbMgr._dwtCtrlHasFocus = false;	
};

// Currently not being used
DwtKeyboardMgr._keyUpHdlr =
function(ev) {
	DBG.println("KU HDLR");
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = kev.keyCode;
	
	if (kbMgr._kbEventStatus != DwtKeyboardMgr._KEYSEQ_NOT_HANDLED) {
		DBG.println("KE BEING BLOCKED IN KU HDLR");
		kev._stopPropagation = true;
		kev._returnValue = false;
		kev.setToDhtmlEvent(ev);
		return false;
	 } 
}

DwtKeyboardMgr._keyPressHdlr =
function(ev) {
	DBG.println("KP HDLR");
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = kev.keyCode;
	
	DBG.println("KP HDLR CHECKING");
	DBG.println("STATUS: " + kbMgr._kbEventStatus);
	
	if (kbMgr._kbEventStatus != DwtKeyboardMgr._KEYSEQ_NOT_HANDLED) {
		DBG.println("KE BEING BLOCKED IN KP HDLR");
		kev._stopPropagation = true;
		kev._returnValue = false;
		kev.setToDhtmlEvent(ev);
		return false;
	 } 
}

/*
 * There are a number of edge focus  cases that we must handle because of the way
 * tabbing works in the browser, and because of user actions:
 * 
 * Case 1
 * ------
 * User tabs in from address bar. w/FF we get no indication of this. Focus will
 * go to the next input field as seen by the browser
 * 
 * Solution: Make our hidden input field the first tab item. On focus get the last
 * element that had focus from the tab group and set focus to that
 * 
 * Case 2
 * ------
 * User clicks in an input that is part of the tab group hierarchy. 
 * 
 * Solution: When the user types into such a field we will detect the key event and
 * set the tab groups current focus member appropraitely
 * 
 * Case 3
 * ------
 * User clicks in an input that is not part of the tab group hierarchy
 * 
 * Solution: Not much we can do here except ignore events. This is really not a good
 * thing as all visible elements should be part of the tabbing hierarchy
 * 
 * Case 4
 * ------
 * User clicks in an external input field (e.g. the browser address bar)
 * 
 * Solution: we actually don't have to do anything here as case 1 should adress
 * the situation when the user clicks/tabs back into elements we control
 */


/* This method does a focus check. If a DwtControl does not have focus, then we
 * are dealing with an input element. If this element is not the current focus object
 * then try and set it to the current focus object (case 2). If the object is not in the
 * tag group hierarchy return false indicating that we should leave all events
 * related to it alone (case 3)
 */
DwtKeyboardMgr._syncFocus =
function(kbMgr, obj) {
	if (!kbMgr._dwtCtrlHasFocus) {
		DBG.println("CONTROL NOT FOCUS: _focusObj: " + kbMgr._focusObj + " - obj: " + obj);
		if (kbMgr._focusObj != obj) {
			DBG.println("NOT THE SAME");
			if (kbMgr._currTabGroup.setFocusMember(obj))
				kbMgr._focusObj = obj;
			else
				return false;
		}
	}
	return true;
}

DwtKeyboardMgr._keyDownHdlr =
function(ev) {
	var shell = DwtShell.getShell(window)
	var kbMgr = shell.getKeyboardMgr();
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = kev.keyCode;
	
	// Popdown any tooltip
	shell.getToolTip().popdown();
	
	// Sync up focus if needed
	var focusInTGMember = DwtKeyboardMgr._syncFocus(kbMgr, kev.target);
	
	if (focusInTGMember) 
		DBG.println("Object is in tab hierarchy");
			
	/* The first thing we care about is the tab key since we want to manage
	 * focus based on the tab groups. 
	 * 
	 * If the tab hit happens in the currently
	 * focused obj, the go to the next/prev element in the tab group. 
	 * 
	 * If the tab happens in an element that is in the tab group hierarchy, but that 
	 * element is not the currently focus element in the tab hierarchy (e.g. the user
	 * clicked in it and we didnt detect it) then sync the tab group's current focus 
	 * element and handle the tab
	 * 
	 * If the tab happens in an object not under the tab group hierarchy, then set
	 * focus to the current focus object in the tab hierarchy i.e. grab back control
	 */
	 if (keyCode == DwtKeyMapMgr.TAB_KEYCODE) {
	 	DBG.println("TAB HIT!");
	 	// If the tab hit is in an element
		if (focusInTGMember) {
		 	if (!kev.shiftKey)
		 		kbMgr._currTabGroup.getNextFocusMember();
		 	else
		 		kbMgr._currTabGroup.getPrevFocusMember();
	 	} else {
	 		DBG.println("RESETTING TO FIRST");
	 		// For tab to the first element in the tab group
	 		kbMgr._currTabGroup.resetFocusMember();
	 	}
		DBG.println("SENTINEL");	
		kbMgr._kbEventStatus = DwtKeyboardMgr._KEYSEQ_HANDLED;
		kev._stopPropagation = true;
		kev._returnValue = false;
		kev.setToDhtmlEvent(ev);
		return false;
	 }
	 
	/* If the focus object is a DwtControl, then set the value of the keyboard
	 * focus field to "" so that it doesn't overflow or get crufty
	 */
	if (kbMgr._dwtCtrlHasFocus) 
		kbMgr._kbFocusField.value = "";
	 
	/* Filter out the following keys: Alt, Shift, Ctrl. Also filter out
	 * alphanumeric keys if the target of the key event is an input field
	 * or a text area and there is no pending sequence in play and the key
	 * is alphanumeric or a punctuation key */
	//var tagName = (kev.target) ? kev.target.tagName.toLowerCase() : null;
	//DBG.println("KEYCODE: " + keyCode + " - tagName: " + tagName);
	if (DwtKeyMapMgr.isModifier(keyCode)
		|| (!kbMgr._dwtCtrlHasFocus 
			&& kbMgr._killKeySeqTimedActionId == -1 && !kev.ctrlKey && !kev.altKey
			&& DwtKeyMapMgr.isUsableTextInputValue(keyCode))) {
		DBG.println("valid input field data");
		kbMgr._kbEventStatus = DwtKeyboardMgr._KEYSEQ_NOT_HANDLED;
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
	
	/* If a DWT component has "focus", then dispatch to that component
	 * if the component handles the event, then stop, else hand it off
	 * the global handler if one is registered */
	var handled = DwtKeyboardMgr._KEYSEQ_NOT_HANDLED;
	var obj = (kbMgr._dwtCtrlHasFocus) ? kbMgr._focusObj : null;
	//DBG.println("Focus Object: " + obj.toString());
	if (obj != null && (obj instanceof DwtControl)) {
		handled = kbMgr._dispatchKeyEvent(obj, obj.toString(), kev);
	}
	 	
	if (handled == DwtKeyboardMgr._KEYSEQ_NOT_HANDLED && kbMgr._globalKeyActionHdlr != null) {
		DBG.println("GLOBAL HANDLER CALLED");
		handled = kbMgr._dispatchKeyEvent(kbMgr._globalKeyActionHdlr, 
							kbMgr._globalKeyActionHdlr.getKeyMapNameToUse(), kev);
		DBG.println("GLOBAL HANDLER RETURNED: " + handled);
	}
	
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
	if (actionCode == DwtKeyMapMgr.NOT_A_TERMINAL) {
		DBG.println("SCHEDULING KILL SEQUENCE ACTION");
		/* setup a timed action to kill the key sequence in the event
		 * the user does not press another key in the allotted time */
		this._killKeySeqTimedActionId = 
			AjxTimedAction.scheduleAction(this._killKeySeqTimedAction, 1000);
		return DwtKeyboardMgr._KEYSEQ_PENDING;
		
	} else if (actionCode != null) {
		DBG.println("HANDLING ACTION: " + actionCode);
		return (hdlr.handleKeyAction(actionCode, ev)) 
			? DwtKeyboardMgr._KEYSEQ_HANDLED : DwtKeyboardMgr._KEYSEQ_NOT_HANDLED;
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

DwtKeyboardMgr.prototype._tabGrpChangeListener =
function(ev) {
	DBG.println("_tabGrpChangeListener: focus changed. grabbing focus");
	this._doGrabFocus(ev.newFocusMember);
	DBG.println("_tabGrpChangeListener: focus GRABBED");
}
