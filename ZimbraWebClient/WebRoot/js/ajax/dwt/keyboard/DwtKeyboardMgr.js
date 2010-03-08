/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Creates an empty keyboard manager. Tab groups must be added for it to do
 * anything.
 * @constructor
 * @class
 * This class is responsible for managing keyboard events. That includes dispatching
 * keyboard events, as well as managing focus and tab groups. It is at the heart of the
 * Dwt keyboard navigation framework.
 * <p>
 * <i>DwtKeyboardMgr</i> intercepts key strokes and translates
 * them into actions which it then dispatches to the component with focus. If the key
 * stroke is a TAB (or Shift-TAB), then focus is moved based on the current tab group.
 * </p><p>
 * A <i>DwtShell</i> instantiates its own <i>DwtKeyboardMgr</i> at construction.
 * The keyboard manager may then be retrieved via the shell's <code>getKeyboardMgr()</code>
 * function. Once a handle to the shell's keyboard manager is retrieved, then the user is free
 * to add tab groups and register keymaps and handlers with the keyboard manager.
 * </p><p>
 * Focus is managed among a stack of tab groups. The TAB button will move the focus within the
 * current tab group. When a non-TAB is received, we first check if the control can handle it.
 * In general, control key events simulate something the user could do with the mouse, and change
 * the state/appearance of the control. For example, ENTER on a DwtButton simulates a button
 * press. If the control does not handle the key event, the event is handed to the application,
 * which handles it based on its current state. The application key event handler is in a sense
 * global, since it does not matter which control received the event.
 * </p><p>
 * At any given time there is a default handler, which is responsible for determining what
 * action is associated with a particular key sequence, and then taking it. A handler should support
 * the following methods:
 * 		getKeyMapName()		returns the name of the map that defines shortcuts for this handler
 * 		handleKeyAction()	performs the action associated with a shortcut
 * 		handleKeyEvent()	(optional) override; handler solely responsible for handling event
 * </p>
 *
 * @author Ross Dargahi
 *
 * @see DwtShell
 * @see DwtTabGroup
 * @see DwtKeyMap
 * @see DwtKeyMapMgr
 */
DwtKeyboardMgr = function(shell) {
	DwtKeyboardMgr.__shell = shell;
	this.__tabGrpStack = [];
	this.__defaultHandlerStack = [];
	this.__tabGroupChangeListenerObj = new AjxListener(this, this.__tabGrpChangeListener);
	this.__kbEventStatus = DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	this.__keyTimeout = 750;
	this.__currTabGroup = null;
	this.__currDefaultHandler = null;

	this._clearRepeatAction = new AjxTimedAction(null, function() { DwtKeyboardMgr.__keyCode = null; });
	this._clearRepeatActionId = -1;
};

/**@private*/
DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED	= 1;
/**@private*/
DwtKeyboardMgr.__KEYSEQ_HANDLED		= 2;
/**@private*/
DwtKeyboardMgr.__KEYSEQ_PENDING		= 3;
/**@private*/
DwtKeyboardMgr.__KEYSEQ_REPEAT		= 4;

DwtKeyboardMgr.FOCUS_FIELD_ID = "kbff";

/**
 * Returns true if the event may be a shortcut from within an input (text input or
 * textarea). Since printable characters are echoed, the shortcut must be nonprintable:
 * 
 * 		- Alt or Ctrl or Meta plus another key
 * 		- Esc
 * 		- Enter within a text input (but not a textarea)
 * 
 * @param ev	[DwtKeyEvent]		key event
 */
DwtKeyboardMgr.isPossibleInputShortcut =
function(ev) {
    var target = DwtUiEvent.getTarget(ev);
    return (!DwtKeyMap.IS_MODIFIER[ev.keyCode] &&
			(ev.keyCode == 27 || DwtKeyMapMgr.hasModifier(ev)) ||
			(target && target.nodeName.toUpperCase() == "INPUT" && (ev.keyCode == 13 || ev.keyCode == 3)));
};

/**
 * @return returns the class name
 * @type String
 */
DwtKeyboardMgr.prototype.toString = 
function() {
	return "DwtKeyboardMgr";
};

/**
 * Pushes <code>tabGroup</code> onto the stack and makes it the active tab group.
 * 
 * @param 	tabGroup	[DwtTabGroup]	tabGroup tab group to push onto the stack
 * 
 * @see		popTabGroup
 */
DwtKeyboardMgr.prototype.pushTabGroup =
function(tabGroup) {
//	DBG.println("kbnav", "PUSH tab group " + tabGroup.__name);
	if (!this.__keyboardHandlingInited || !tabGroup) { return; }
		
	this.__tabGrpStack.push(tabGroup);
	this.__currTabGroup = tabGroup;
	var focusMember = tabGroup.getFocusMember();
	if (!focusMember) {
		focusMember = tabGroup.resetFocusMember(true);
	}
	if (!focusMember) {
//		DBG.println("kbnav", "DwtKeyboardMgr.pushTabGroup: tab group " + tabGroup.__name + " has no members!");
		return;
	}
	tabGroup.addFocusChangeListener(this.__tabGroupChangeListenerObj);
	this.grabFocus(focusMember);
};

/**
 * Pops the current tab group off the top of the tab group stack. The previous 
 * tab group (if there is one) then becomes the current tab group.
 * 
 * @param {DwtTabGroup} tabGroup Tab group to pop. If supplied, then the tab group
 * 		stack is searched for the tab group and it is removed. If null, then the
 * 		top tab group is popped.
 * 
 * @return the popped tab group, or null if there is one or less tab groups
 * @type DwtTabGroup
 */
DwtKeyboardMgr.prototype.popTabGroup =
function(tabGroup) {
	if (!this.__keyboardHandlingInited) { return; }
	if (!tabGroup) return;
//	DBG.println("kbnav", "POP tab group " + tabGroup.__name);
	
	// we never want an empty stack
	if (this.__tabGrpStack.length <= 1) {
		return null;
	}
	
	/* If we are popping a tab group that is not on the top of the stack then 
	 * we need to find it and remove it. */
	if (tabGroup && this.__tabGrpStack[this.__tabGrpStack.length - 1] != tabGroup) {
		var a = this.__tabGrpStack;
		var len = a.length;
		for (var i = len - 1; i >= 0; i--) {
			if (tabGroup == a[i]) {
				a[i].dump();
				break;
			}
		}
		
		/* If there is no match in the stack for tabGroup, then simply return null,
		 * else if the match is not the top item on the stack, then remove it from 
		 * the stack. Else we are dealing with the topmost item on the stack so handle it 
		 * as a simple pop. */
		if (i < 0) { // No match
			return null;
		} else if (i != len - 1) { // item is not on top
			// Remove tabGroup
			a.splice(i, 1);
			return tabGroup;
		}
	} 

	var tabGroup = this.__tabGrpStack.pop();
	tabGroup.removeFocusChangeListener(this.__tabGroupChangeListenerObj);
	
	var currTg = null;
	if (this.__tabGrpStack.length > 0) {
		currTg = this.__tabGrpStack[this.__tabGrpStack.length - 1];
		var focusMember = currTg.getFocusMember();
		if (!focusMember) {
			focusMember = currTg.resetFocusMember(true);
		}
		if (focusMember) {
			this.grabFocus(focusMember);
		}
	}
	this.__currTabGroup = currTg;

	return tabGroup;
};

/**
 * Replaces the current tab group with <code>tabGroup</code>
 * 
 * @param {DwtTabGroup} tabGroup Tab group to use
 * 
 * @return old tab group
 * @type DwtTabGroup
 */
DwtKeyboardMgr.prototype.setTabGroup =
function(tabGroup) {
	if (!this.__enabled || !this.__keyboardHandlingInited) { return; }
	
	var otg = this.popTabGroup();
	this.pushTabGroup(tabGroup);
	return otg;
};

DwtKeyboardMgr.prototype.pushDefaultHandler =
function(handler) {
	if (!this.__enabled || !this.__keyboardHandlingInited || !handler) { return; }
//	DBG.println("kbnav", "PUSH default handler: " + handler);
		
	this.__defaultHandlerStack.push(handler);
	this.__currDefaultHandler = handler;
};

DwtKeyboardMgr.prototype.popDefaultHandler =
function() {
//	DBG.println("kbnav", "POP default handler");
	// we never want an empty stack
	if (!this.__keyboardHandlingInited || (this.__defaultHandlerStack.length <= 1)) { return; }

//	DBG.println("kbnav", "Default handler stack length: " + this.__defaultHandlerStack.length);
	var handler = this.__defaultHandlerStack.pop();
	this.__currDefaultHandler = this.__defaultHandlerStack[this.__defaultHandlerStack.length - 1];
//	DBG.println("kbnav", "Default handler is now: " + this.__currDefaultHandler);

	return handler;
};

/**
 * Sets the focus to <code>focusObj</code>.
 * 
 * @param {HTMLInputElement|DwtControl} focusObj Object to which to set focus
 */ 
DwtKeyboardMgr.prototype.grabFocus =
function(focusObj) {
	if (!this.__enabled) { return; }
	if (!this.__keyboardHandlingInited) {
		return;
	}
	if (!focusObj) return;

	/* We may not be using tab groups, so be prepared for that case */
	if (this.__currTabGroup) {
		this.__currTabGroup.setFocusMember(focusObj, false, true);
	}
		
	this.__doGrabFocus(focusObj);
};

DwtKeyboardMgr.prototype.inputGotFocus =
function(inputCtrl) {

	this.__focusObj = inputCtrl;
	this.__dwtInputCtrl = true;
	this.__dwtCtrlHasFocus = false;
	if (this.__currTabGroup) {
		this.__currTabGroup.setFocusMember(inputCtrl.getTabGroupMember(), false, true);
	}
};

/**
 * Returns the object that has focus
 *
 * @return {HTMLInputElement|DwtControl} Object with focus
 */
DwtKeyboardMgr.prototype.getFocusObj =
function(focusObj) {
	return this.__focusObj;
};

/**
* Return true if the specified component currently has keyboard focus
*
* @param {DwtControl} control Object for which to check focus
* 
* @return true if the <code>control</code> has keyboard focus, else false
* @type Boolean
*/
DwtKeyboardMgr.prototype.dwtControlHasFocus =
function(control) {
	if (!this.__enabled) { return false; }
	if (!this.__keyboardHandlingInited) {
		return false;
	}
		
	return (this.__dwtCtrlHasFocus && this.__focusObj == control);
};

/** 
 * This method is used to register an application key handler. If registered, this
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
 * 
 * @param 	hdlr	[function]		hdlr Handler function. This method should have the following
 * 									signature <code>Boolean hdlr(Int actionCode DwtKeyEvent event);</code>
 * 
 * @see DwtKeyEvent
 */
DwtKeyboardMgr.prototype.registerDefaultKeyActionHandler =
function(hdlr) {
	if (!this.__enabled) { return; }
	this.__defaultKeyActionHdlr = hdlr;
};

/**
* This method is used to register a keymap with the shell. A keymap typically
* is a subclass of <i>DwtKeyMap</i> and defines the mappings from key sequences to
* actions.
*
* @param {DwtKeyMap} keyMap keyMap to be registered
* 
* @see DwtKeyMap
**/
DwtKeyboardMgr.prototype.registerKeyMap =
function(keyMap) {
	if (!this.__checkStatus()) { return; }
	this.__keyMapMgr = new DwtKeyMapMgr(keyMap);
};

/**
 * Sets the timeout (in milliseconds) between key presses for handling multi-keypress
 * sequences
 * 
 * @param 	timeout		[Integer]	timeout Timeout in milliseconds
 */
DwtKeyboardMgr.prototype.setKeyTimeout =
function(timeout) {
	this.__keyTimeout = timeout;
};

// Clears the key sequence. The next key event will begin a new one.
DwtKeyboardMgr.prototype.clearKeySeq =
function() {
	this.__killKeySeqTimedActionId = -1;
	this.__keySequence.length = 0;
};

/**
 * Enables/disables keyboard nav.
 * 
 * @param 	enabled		[boolean]	if true, enable keyboard nav
 */
DwtKeyboardMgr.prototype.enable =
function(enabled) {
	DBG.println(AjxDebug.DBG2, "keyboard nav enabled: " + enabled);
	this.__enabled = enabled;
	if (enabled){
		this.__checkStatus();	// make sure we're initialized
		Dwt.setHandler(document, DwtEvent.ONKEYDOWN, DwtKeyboardMgr.__keyDownHdlr);
		Dwt.setHandler(document, DwtEvent.ONKEYUP, DwtKeyboardMgr.__keyUpHdlr);
		Dwt.setHandler(document, DwtEvent.ONKEYPRESS, DwtKeyboardMgr.__keyPressHdlr);
	} else {
		Dwt.clearHandler(document, DwtEvent.ONKEYDOWN);
		Dwt.clearHandler(document, DwtEvent.ONKEYUP);
		Dwt.clearHandler(document, DwtEvent.ONKEYPRESS);
	}
};

DwtKeyboardMgr.prototype.isEnabled =
function() {
	return this.__enabled;
};

/**
 * @private
 */
DwtKeyboardMgr.prototype.__initKeyboardHandling =
function() {
	DBG.println(AjxDebug.DBG3, "Initializing Keyboard Handling");

	/* Create our keyboard focus field. This is a dummy input field that will take text
	 * input for keyboard shortcuts. */
//	var kbff = this._kbFocusField = document.createElement("input");
	var kbff = this._kbFocusField = document.createElement("textarea");
	kbff.id = DwtKeyboardMgr.FOCUS_FIELD_ID;
//	kbff.type = "text";
	kbff.tabIndex = 0;
	kbff.style.position = Dwt.ABSOLUTE_STYLE;
	kbff.style.top = kbff.style.left = Dwt.LOC_NOWHERE;
	kbff.onblur = DwtKeyboardMgr.__onBlurHdlr;
	kbff.onfocus = DwtKeyboardMgr.__onFocusHdlr;
	document.body.appendChild(kbff);
	
	this.__killKeySeqTimedAction = new AjxTimedAction(this, this.__killKeySequenceAction);
	this.__killKeySeqTimedActionId = -1;
	this.__keySequence = [];

	this.__keyboardHandlingInited = true;
};

/**
 * @private
 */
DwtKeyboardMgr.prototype.__checkStatus =
function() {
	if (!this.__enabled) {
		return false;
	}
	if (!this.__keyboardHandlingInited) {
		this.__initKeyboardHandling();
	}
	return true;
};

/** 
 * Sets keyboard focus to the given object.
 *
 * @private
 */
DwtKeyboardMgr.prototype.__doGrabFocus =
function(focusObj) {

	if (!focusObj) { return; }
	
	var dwtInputCtrl = (Dwt.instanceOf(focusObj, "DwtInputField") ||
						Dwt.instanceOf(focusObj, "DwtHtmlEditor") ||
						Dwt.instanceOf(focusObj, "DwtCheckbox") ||
						Dwt.instanceOf(focusObj, "DwtRadioButton") ||
						Dwt.instanceOf(focusObj, "ZmAdvancedHtmlEditor"));
//	DBG.println("kbnav", "DwtKeyboardMgr._doGrabFocus: " + focusObj);
	if (dwtInputCtrl || !(focusObj instanceof DwtControl)) {
		// dealing with an input field
		if (this.__focusObj instanceof DwtControl && !this.__dwtInputCtrl) {
			// ctrl -> input
			this.__oldFocusObj = this.__focusObj;
		}
		this.__focusObj = focusObj;
		this.__dwtInputCtrl = dwtInputCtrl;
		var el = dwtInputCtrl ? focusObj.getInputElement() : focusObj;
		// IE throws JS error if you try to focus a disabled or invisible input
		if ((!AjxEnv.isIE && focusObj.focus) ||
			(AjxEnv.isIE && focusObj.focus && !el.disabled && Dwt.getVisible(el))) {
			// ignore exception - IE sometimes still throws error, don't know why
			try {
				focusObj.focus();
			} catch(ex) {}
		}
	} else {
		/* If the current focus of obj and the one grabbing focus are both DwtControls
		 * then we need to simulate a blur on the control losing focus */
		if (this.__dwtCtrlHasFocus && (this.__focusObj instanceof DwtControl)) {
			// ctrl -> ctrl: blur old ctrl
			DwtKeyboardMgr.__onBlurHdlr();
			this.__dwtCtrlHasFocus = true;	// reset
		}
			
		this.__focusObj = focusObj;
		this.__dwtInputCtrl = false;
		
		/* If a DwtControl already has focus, then we need to manually call
		 * DwtKeyboardMgr.__onFocusHdlr to simulate focus since calling the focus()
		 * method on the input field does nothing. */
		if (this.__dwtCtrlHasFocus) {
			// ctrl -> ctrl: tell newly focused ctrl it got focus
			DwtKeyboardMgr.__onFocusHdlr();
		} else {
			DwtKeyboardMgr.__onFocusHdlr();
			// input -> ctrl: set browser focus to keyboard input field
			this._kbFocusField.focus();
		}
	}
};

/**
 * Focus handler for keyboard focus input field.
 * @private
 */
DwtKeyboardMgr.__onFocusHdlr =
function(ev) {

	var kbMgr = DwtKeyboardMgr.__shell.getKeyboardMgr();
	kbMgr.__dwtCtrlHasFocus = true;
	var focusObj = kbMgr.__focusObj;
//	DBG.println("kbnav", "DwtKeyboardMgr: ONFOCUS - " + focusObj);
	if (focusObj && focusObj.__doFocus) {
		focusObj.__doFocus();
	}
};

/**
 * Blur handler for keyboard focus input field.
 * @private
 */
DwtKeyboardMgr.__onBlurHdlr =
function(ev) {

	var kbMgr = DwtKeyboardMgr.__shell.getKeyboardMgr();

	// Got to play the trick with HTML elements which get focus before blur is
	// called on the old focus object. (see _grabFocus)
	var focusObj = kbMgr.__oldFocusObj || kbMgr.__focusObj;
//	DBG.println("kbnav", "DwtKeyboardMgr: ONBLUR - " + focusObj);
	if (focusObj && focusObj.__doBlur) {
		focusObj.__doBlur();
	}
		
	/* FIXME The code below that is commented out fixes a bug that surfaces if you tab in
	 * the address bar or search field in FF. The bug is that depending on where
	 * you had focus, you could get a visual artifact (temporary). However, the code
	 * broke the fact that when focus leaves the browser window, then returns, then
	 * if a DwtControl had focus it will not get the appropriate highlight.
	 */	
//	kbMgr.__oldFocusObj = kbMgr.__focusObj = null;
	kbMgr.__oldFocusObj = null;
	kbMgr.__dwtCtrlHasFocus = false;	
};


/**
 * @private
 */
DwtKeyboardMgr.__keyUpHdlr =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
//	DBG.println("kbnav", "keyup: " + ev.keyCode);

	// clear saved repeating key
	DwtKeyboardMgr.__keyCode = null;
	if (this._clearRepeatActionId != -1) {
		AjxTimedAction.cancelAction(this._clearRepeatActionId);
		this._clearRepeatActionId = -1;
	}
	
	if (AjxEnv.isMac && AjxEnv.isGeckoBased && ev.keyCode == 0) {
		return DwtKeyboardMgr.__keyDownHdlr(ev);
	} else {
		return DwtKeyboardMgr.__handleKeyEvent(ev);
	}
};

/**
 * @private
 */
DwtKeyboardMgr.__keyPressHdlr =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
//	DBG.println("kbnav", "keypress: " + (ev.keyCode || ev.charCode));
	if (DwtKeyboardMgr.__keyCode && AjxEnv.isGeckoBased) {
//		DBG.println("kbnav", "Gecko: calling keydown on keypress event");
		return DwtKeyboardMgr.__keyDownHdlr(ev);
	} else {
		return DwtKeyboardMgr.__handleKeyEvent(ev);
	}
};

/**
 * @private
 */
DwtKeyboardMgr.__handleKeyEvent =
function(ev) {

	if (DwtKeyboardMgr.__shell._blockInput) { return false; }

	if (ev.type == "keypress") {
		DwtKeyEvent.geckoCheck(ev);
	}

	ev = DwtUiEvent.getEvent(ev, this);
//	DBG.println("kbnav", [ev.type, ev.keyCode, ev.charCode, ev.which].join(" / "));
	var kbMgr = DwtKeyboardMgr.__shell.getKeyboardMgr();
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);

	if (kbMgr.__kbEventStatus != DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED) {
		return kbMgr.__processKeyEvent(ev, kev, false);
	}
};

/*
 * There are a number of focus cases that we must handle because of the way
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
 * set the tab groups current focus member appropriately
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

/**
 * This method does a focus check. If a DwtControl does not have focus, then we
 * are dealing with an input element. If this element is not the current focus object
 * then try and set it to the current focus object (case 2). If the object is not in the
 * tab group hierarchy return false indicating that we should leave all events
 * related to it alone (case 3).
 * 
 * @private
 */
DwtKeyboardMgr.__syncFocus =
function(kbMgr, obj) {
//	DBG.println("kbnav", "sync focus ----- obj: " + obj + ", __focusObj: " + kbMgr.__focusObj + ", __dwtCtrlHasFocus: "
//			+ kbMgr.__dwtCtrlHasFocus + ", __dwtInputCtrl: " + kbMgr.__dwtInputCtrl + ", __oldFocusObj: " + kbMgr.__oldFocusObj);
	/* (BUG 8588) If obj is not the hidden input field (it's some other input field), and if
	 * a control has focus, then we have a focus mismatch and we need to blur the
	 * control that thinks it has focus. That can happen due to the way focus
	 * can be set in input fields. */ 
	if ((obj != kbMgr._kbFocusField) && kbMgr.__dwtCtrlHasFocus) {
//		DBG.println("kbnav", "FOCUS MISMATCH! focus obj: " + kbMgr.__focusObj + ", obj: " + obj);
//		DwtKeyboardMgr.__onBlurHdlr();
		kbMgr._kbFocusField.focus();
	}
	
//	DBG.println("kbnav", "DwtKeyboardMgr.__syncFocus: focus obj: " + kbMgr.__focusObj + " - obj: " + obj);
	if (!kbMgr.__dwtCtrlHasFocus) {
		// DwtInputField DwtHtmlEditor
		if ((obj != kbMgr.__focusObj) && !kbMgr.__dwtInputCtrl) {
//			DBG.println("kbnav", "Focus out of sync, resetting");
			if (kbMgr.__currTabGroup && kbMgr.__currTabGroup.setFocusMember(obj)) {
				kbMgr.__focusObj = obj;
				kbMgr.__oldFocusObj = null;
			} else {
				return false;
			}
		}
	}
	return true;
};

/**
 * @private
 */
DwtKeyboardMgr.__keyDownHdlr =
function(ev) {
	if (DwtKeyboardMgr.__shell._blockInput) { return false; }
	ev = DwtUiEvent.getEvent(ev, this);
//	DBG.println("kbnav", [ev.type, ev.keyCode, ev.charCode, ev.which].join(" / "));
	var kbMgr = DwtKeyboardMgr.__shell.getKeyboardMgr();
	if (!kbMgr || !kbMgr.__checkStatus()) { return false; }
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = DwtKeyboardMgr.__keyCode || DwtKeyEvent.getCharCode(ev);
	var isRepeat = (DwtKeyboardMgr.__keyCode != null);
//	DBG.println("kbnav", "keydown: " + keyCode + " -------- " + ev.target);
//	DBG.println("kbnav", "saved key code: " + DwtKeyboardMgr.__keyCode + " (" + isRepeat + ")");

	// Popdown any tooltip
	DwtKeyboardMgr.__shell.getToolTip().popdown();
	
	// Sync up focus if needed
	var focusInTGMember = DwtKeyboardMgr.__syncFocus(kbMgr, kev.target);
	
	if (!focusInTGMember) {
//		DBG.println("kbnav", "Object is not in tab hierarchy");
	}
			
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
	 	if (kbMgr.__currTabGroup && !DwtKeyMapMgr.hasModifier(kev)) {
		 	// If a menu is popped up then don't act on the Tab
		 	if (!DwtMenu.menuShowing()) {
//			 	DBG.println("kbnav", "Tab");
			 	// If the tab hit is in an element or if the current tab group has
			 	// a focus member
				if (focusInTGMember || kbMgr.__currTabGroup.getFocusMember()) {
				 	if (!kev.shiftKey) {
//				 		kbMgr.__currTabGroup.dump("kbnav");
				 		kbMgr.__currTabGroup.getNextFocusMember(true);
				 	} else {
				 		kbMgr.__currTabGroup.getPrevFocusMember(true);
				 	}
			 	} else {
//			 		DBG.println("kbnav", "DwtKeyboardMgr.__keyDownHdlr: no current focus member, resetting to first in tab group");
			 		// If there is no current focus member, then reset
			 		kbMgr.__currTabGroup.resetFocusMember(true);
			 	}
		 	}
		 	return kbMgr.__processKeyEvent(ev, kev, false, DwtKeyboardMgr.__KEYSEQ_HANDLED);
	 	} else {
	 		// No tab groups registered, or Alt or Ctrl was down. Let the browser handle it.
		 	return kbMgr.__processKeyEvent(ev, kev, true, DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED);
	 	}
	 } else if (kbMgr.__currTabGroup && !focusInTGMember && AjxEnv.isGecko && kev.target instanceof HTMLHtmlElement) {
	 	/* With FF we focus get set to the <html> element when tabbing in
	 	 * from the address or search fields. What we want to do is capture
	 	 * this here and reset the focus to the first element in the tabgroup
	 	 * 
	 	 * TODO Verify this trick is needed/works with IE/Safari
	 	 */
		kbMgr.__currTabGroup.resetFocusMember(true);
	 }
	 
	// If the focus object is a DwtControl, then clear the keyboard focus field
	if (kbMgr.__dwtCtrlHasFocus) {
		kbMgr._kbFocusField.value = "";
	}
	 
	// Filter out modifier keys. If we're in an input field, filter out legitimate input.
	// (A shortcut from an input field must use a modifier key.)
	if (DwtKeyMap.IS_MODIFIER[keyCode] || (!kbMgr.__dwtCtrlHasFocus && (kbMgr.__killKeySeqTimedActionId == -1) &&
		DwtKeyMapMgr.isInputElement(kev.target) && !DwtKeyboardMgr.isPossibleInputShortcut(kev))) {

	 	return kbMgr.__processKeyEvent(ev, kev, true, DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED);
	}
	 
	/* Cancel any pending time action to kill the keysequence */
	if (kbMgr.__killKeySeqTimedActionId != -1) {
		AjxTimedAction.cancelAction(kbMgr.__killKeySeqTimedActionId);
		kbMgr.__killKeySeqTimedActionId = -1;
	}
		
 	var parts = [];
	if (kev.altKey) 	{ parts.push(DwtKeyMap.ALT); }
	if (kev.ctrlKey) 	{ parts.push(DwtKeyMap.CTRL); }
 	if (kev.metaKey) 	{ parts.push(DwtKeyMap.META); }
	if (kev.shiftKey) 	{ parts.push(DwtKeyMap.SHIFT); }
	parts.push(keyCode);
	kbMgr.__keySequence[kbMgr.__keySequence.length] = parts.join(DwtKeyMap.JOIN);

//	DBG.println("kbnav", "KEYCODE: " + keyCode + " - KEY SEQ: " + kbMgr.__keySequence.join(""));
	
	var handled = DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;

	// First see if the control that currently has focus can handle the key event
	var obj = kbMgr.__focusObj;
	if (obj && (obj.handleKeyAction) && (kbMgr.__dwtCtrlHasFocus || kbMgr.__dwtInputCtrl || (obj.hasFocus && obj.hasFocus()))) {
//		DBG.println("kbnav", obj + " has focus: " + obj.hasFocus());
		handled = kbMgr.__dispatchKeyEvent(obj, kev, false, isRepeat);
		while ((handled == DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED) && obj.parent && obj.parent.getKeyMapName) {
			obj = obj.parent;
			handled = kbMgr.__dispatchKeyEvent(obj, kev, false, isRepeat);
		}
	}

	// If the currently focused control didn't handle the event, hand it to the default key
	// event handler
	if ((handled == DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED) && kbMgr.__currDefaultHandler &&
		!(kbMgr.__currTabGroup && kbMgr.__currTabGroup.isDefaultHandlingBlocked())) {
		handled = kbMgr.__dispatchKeyEvent(kbMgr.__currDefaultHandler, kev, false, isRepeat);
	}

	kbMgr.__kbEventStatus = handled;
	var propagate = (handled == DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED);

	if (handled != DwtKeyboardMgr.__KEYSEQ_PENDING) {
		kbMgr.clearKeySeq();
	}

	if (handled == DwtKeyboardMgr.__KEYSEQ_REPEAT) {
//		DBG.println("kbnav", "saving repeatable keyCode " + keyCode);
		DwtKeyboardMgr.__keyCode = keyCode;
		kbMgr._clearRepeatActionId = AjxTimedAction.scheduleAction(kbMgr._clearRepeatAction, 150);
	}

	return kbMgr.__processKeyEvent(ev, kev, propagate);
};

/**
 * Handles event dispatching
 * 
 * @private
 */
DwtKeyboardMgr.prototype.__dispatchKeyEvent = 
function(hdlr, ev, forceActionCode, isRepeat) {

	if (hdlr && hdlr.handleKeyEvent) {
		var handled = hdlr.handleKeyEvent(ev);
		return handled ? DwtKeyboardMgr.__KEYSEQ_HANDLED : DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	}
	var mapName = (hdlr && hdlr.getKeyMapName) ? hdlr.getKeyMapName() : null;
	if (!mapName) {
		return DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	}
//	DBG.println("kbnav", "DwtKeyboardMgr.__dispatchKeyEvent: handler " + hdlr.toString() + " handling " + this.__keySequence + " for map: " + mapName);
	var actionCode = this.__keyMapMgr.getActionCode(this.__keySequence, mapName, forceActionCode);
	if (actionCode == DwtKeyMapMgr.NOT_A_TERMINAL) {
//		DBG.println("kbnav", "scheduling action to kill key sequence");
		/* setup a timed action to redispatch/kill the key sequence in the event
		 * the user does not press another key in the allotted time */
		this.__hdlr = hdlr;
		this.__mapName = mapName;
		this.__ev = ev;
		this.__killKeySeqTimedActionId = AjxTimedAction.scheduleAction(this.__killKeySeqTimedAction, this.__keyTimeout);
		return DwtKeyboardMgr.__KEYSEQ_PENDING;	
	} else if (actionCode != null) {
		/* It is possible that the component may not handle a valid action
		 * particulary actions defined in the default map */
//		DBG.println("kbnav", "DwtKeyboardMgr.__dispatchKeyEvent: handling action: " + actionCode);
		if (!hdlr.handleKeyAction) {
			return DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
		}

		// Handle first instance of a key that supports auto-repeat. For Gecko, we switch to
		// calling the handler on keypress events, since it sends one on the initial press and
		// on the repeats. For non-Gecko, we call the handler on keydown, since the repeats send
		// that event. Also, keys like the up/down arrows are considered "special" and don't send
		// keypress.
		if (!isRepeat && this.__keyMapMgr.repeats(mapName, actionCode)) {
//			DBG.println("kbnav", mapName + "." + actionCode + " repeats");
			if (AjxEnv.isGeckoBased) {
				return DwtKeyboardMgr.__KEYSEQ_REPEAT;
			} else {
				var result = hdlr.handleKeyAction(actionCode, ev);
				return result ? DwtKeyboardMgr.__KEYSEQ_REPEAT : DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
			}
		}

		var result = hdlr.handleKeyAction(actionCode, ev);
		return result ? DwtKeyboardMgr.__KEYSEQ_HANDLED : DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	} else {	
//		DBG.println("kbnav", "DwtKeyboardMgr.__dispatchKeyEvent: no action code for " + this.__keySequence);
		return DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	}
};

/**
 * This method will reattempt to handle the event in the case that the intermediate
 * node in the keymap may have an action code associated with it.
 * 
 * @private
 */
DwtKeyboardMgr.prototype.__killKeySequenceAction =
function() {
//	DBG.println("kbnav", "DwtKeyboardMgr.__killKeySequenceAction: " + this.__mapName);
	this.__dispatchKeyEvent(this.__hdlr, this.__ev, true);
	this.clearKeySeq();
};

/**
 * @private
 */
DwtKeyboardMgr.prototype.__tabGrpChangeListener =
function(ev) {
	this.__doGrabFocus(ev.newFocusMember);
};

/**
 * @private
 */
DwtKeyboardMgr.prototype.__processKeyEvent =
function(ev, kev, propagate, status) {
	if (status) {
		this.__kbEventStatus = status;
	}
	kev._stopPropagation = !propagate;
	kev._returnValue = propagate;
	kev.setToDhtmlEvent(ev);
//	DBG.println("kbnav", "key event returning: " + propagate);
	return propagate;
};
