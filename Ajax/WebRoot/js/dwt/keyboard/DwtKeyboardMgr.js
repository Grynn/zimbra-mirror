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
* This class is reponsible for managing keyboard events. This includes dispatching
* keyboard events, as well as managing focus and tab groups. It is at the heart of the
* Dwt keyboard navigation framework.
* 
* <i>DwtKeyboardMgr</i> is responsible for intercepting key strokes and translating
* them into actions which it then dispatches to the component with focus. It is
* also reponsible for interecepting tab/shift+tab in order to nagivate focus according
* to the tab groups that are registered with it. 
* 
* A <i>DwtShell</i> instantiates it's own <i>DwtKeyboardMgr</i> at construction
* The keyboard manager may then be retrieved via the a shell's <code>getKeyboardMgr()</code>
* function. i.e. developers do not have to directly intantiate a keyboard manager.
* Once a handle to the shell's keyboard manager is retrieved, then the user is free
* to register keymaps and handlers with the keyboard manager
* 
* @author Ross Dargahi
*
* @see DwtShell
* @see DwtTabGroup
* @see DwtKeyMap
* @see DwtKeyMapMgr
*/
function DwtKeyboardMgr() {
	/**@private*/
	this.__tabGrpStack = [];
	/**@private*/
	this.__tabGroupChangeListenerObj = new AjxListener(this, this.__tabGrpChangeListener);
	/**@private*/
	this.__kbEventStatus = DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	/**@private*/
	this.__keyTimeout = 1000;	
	/**@private*/
	this.__currTabGroup  = null;
};

/** This constant is thrown as an exeption
 * @type String
 */
DwtKeyboardMgr.KEYMAP_NOT_REGISTERED = "KEYMAP NOT REGISTERED";

/**@private*/
DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED = 1;
/**@private*/
DwtKeyboardMgr.__KEYSEQ_HANDLED = 2;
/**@private*/
DwtKeyboardMgr.__KEYSEQ_PENDING = 3;

/**
 * @return return a string version of the class' name
 * @type String
 */
DwtKeyboardMgr.prototype.toString = 
function() {
	return "DwtKeyboardMgr";
}

/**
 * Pushes <code>tabGroup</code> onto the stack and makes it the active tab group.
 * 
 * @param {DwtTabGroup} tabGroup tab group to push onto the stack
 * 
 * @see #popTabGroup
 */
DwtKeyboardMgr.prototype.pushTabGroup =
function(tabGroup) {
	if (!this.__keyboardHandlingInited)
		throw DwtKeyboardMgr.KEYMAP_NOT_REGISTERED;
		
	this.__tabGrpStack.push(tabGroup);
	var focusMember = tabGroup.getFocusMember();
	if (!focusMember)
		focusMember = tabGroup.resetFocusMember(true);
	tabGroup.addFocusChangeListener(this.__tabGroupChangeListenerObj);
	this.grabFocus(focusMember);
	this.__currTabGroup = tabGroup;	
};

/**
 * Pops the current tab group off the top of the tab group stack. The previous 
 * tab group (if there is one) then becomes the current tab group.
 * 
 * @param {DwtTabGroup} tabGroup Tab group to pop. If supplied, then the tab group
 * 		stack is searched for the tab group and it is removed. If null, then the
 * 		top of the tab group stack is popped
 * 
 * @return the popped tab group, or null if there is one or less tab groups
 * @type DwtTabGroup
 */
DwtKeyboardMgr.prototype.popTabGroup =
function(tabGroup) {
	if (!this.__keyboardHandlingInited)
		throw DwtKeyboardMgr.KEYMAP_NOT_REGISTERED;
	
	if (this.__tabGrpStack.length <= 1)
		return null;
	
	/* If we are popping a tab group that is not on the top of the stack then 
	 * we need to find it and remove it. */
	if (tabGroup && this.__tabGrpStack[this.__tabGrpStack.length-1] != tabGroup) {
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
		 * the stack and transfer its blockHandling state to the item on top of it.
		 * Else we are dealing with the topmost item on the stack so handle it 
		 * as a simple pop */
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
		currTg = this.__tabGrpStack[this.__tabGrpStack.length-1];
		var focusMember = currTg.getFocusMember();
		if (!focusMember)
			focusMember = currTg.resetFocusMember(true);
		if (focusMember)
			this.grabFocus(focusMember);
	}
	this.__currTabGroup = currTg;

	return tabGroup;
};

/**
 * Replaces the current tab group with <code>tabGroup</code>
 * 
 * @param {DwtTagGroup} tabGroup Tab group to use
 * 
 * @return old tab group
 * @type DwtTabGroup
 */
DwtKeyboardMgr.prototype.setTabGroup =
function(tabGroup) {
	if (!this.__keyboardHandlingInited)
		throw DwtKeyboardMgr.KEYMAP_NOT_REGISTERED;
	
	var otg = this.popTabGroup();
	this.pushTabGroup(tabGroup);
	return otg;
};

/**
 * Sets the focus to <code>focusObj</code>.
 * 
 * @param {HTMLInputElement|DwtControl} focusObj Object to which to set focus
 */ 
DwtKeyboardMgr.prototype.grabFocus =
function(focusObj) {
	//DBG.println(AjxDebug.DBG3, "GRAB FOCUS");
	if (!this.__keyboardHandlingInited)
		return;

	/* We may not be using tab groups, so be prepared for that case */
	if (this.__currTabGroup)
		this.__currTabGroup.setFocusMember(focusObj, false, true);
		
	this.__doGrabFocus(focusObj);
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
	if (!this.__keyboardHandlingInited)
		return false;
		
	return (this.__dwtCtrlHasFocus && this.__focusObj == control);
};

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
 * 
 * @param {function} hdlr Handler function. This method should have the following
 * 		signature <code>Boolean hdlr(Int actionCode DwtKeyEvent event);</code>
 * 
 * @see DwtKeyEvent
 */
DwtKeyboardMgr.prototype.registerGlobalKeyActionHandler =
function(hdlr) {
	this.__globalKeyActionHdlr = hdlr;
};

/**
* This method is used to register a keymap with the shell. A keymap typically
* is a subclass of <i>DwtKeyMap</i> and defines the mappings from key sequences to
* actions
*
* @param {DwtKeyMap} keyMap keyMap to be registered
* 
* @see DwtKeyMap
**/
DwtKeyboardMgr.prototype.registerKeyMap =
function(keyMap) {
	// Setup Keyboard handling not initialized, then initialize it
	if (!this.__keyboardHandlingInited)
		this.__initKeyboardHandling();
	this.__keyMapMgr = new DwtKeyMapMgr(keyMap);
};

/**
 * Sets the timout (in milliseconds) between key presses for handling multi-keypress
 * sequences
 * 
 * @param {number} timout Timout in milliseconds
 */
DwtKeyboardMgr.prototype.setKeyTimeout =
function(timeout) {
	this.__keyTimeout = timeout;
}

/**
 * @private
 */
DwtKeyboardMgr.prototype.__initKeyboardHandling =
function() {
	DBG.println(AjxDebug.DBG3, "Initializing Keyboard Handling");
	Dwt.setHandler(document, DwtEvent.ONKEYDOWN, DwtKeyboardMgr.__keyDownHdlr);
	Dwt.setHandler(document, DwtEvent.ONKEYUP, DwtKeyboardMgr.__keyUpHdlr);
	Dwt.setHandler(document, DwtEvent.ONKEYPRESS, DwtKeyboardMgr.__keyPressHdlr);

;	/* Create our keyboard focus field. This is a dummy input field that will take text
	 * input for keyboard shortcuts */
	var kbff = this._kbFocusField = document.createElement("input");
	kbff.type = "text";
	kbff.tabIndex = 0;
	kbff.style.position = Dwt.ABSOLUTE_STYLE;
	kbff.style.top = kbff.style.left = Dwt.LOC_NOWHERE;
	kbff.onblur = DwtKeyboardMgr.__onBlurHdlr;
	kbff.onfocus = DwtKeyboardMgr.__onFocusHdlr;
	document.body.appendChild(kbff);
	
	this.__killKeySeqTimedAction = new AjxTimedAction(this, this.__killKeySequenceAction);
	this.__killKeySeqTimedActionId = -1;
	this.__keySequence = new Array();

	this.__keyboardHandlingInited = true;
};

/** 
 * Document this. Will also need a push and pop focus frames for things
 * like dialogs etc
 * @private
 */
DwtKeyboardMgr.prototype.__doGrabFocus =
function(focusObj) {
	if (!focusObj)
		return;
		
	if (focusObj instanceof DwtControl) {
		//DBG.println("focusObj is instance of DwtControl: " + focusObj);
		/* If the current focus of obj and the one grabbing focus are both DwtControls
		 * then we need to simulate a blur on the control losing focus */
		if (this.__dwtCtrlHasFocus && this.__focusObj instanceof DwtControl) {
			DwtKeyboardMgr.__onBlurHdlr();
			this.__dwtCtrlHasFocus = true;
		}
			
		this.__focusObj = focusObj;
		
		/* If a DwtControl already has focus, then we need to manually call
		 * DwtKeyboardMgr.__onFocusHdlr to simulate focus since calling the focus()
		 * method on the input field does nothing*/
		if (this.__dwtCtrlHasFocus) {
			DwtKeyboardMgr.__onFocusHdlr()
		} else {
			this._kbFocusField.focus();
		}
	} else {
		// dealing with a type of HTML input field
		if (this.__focusObj instanceof DwtControl)
			this.__oldFocusObj = this.__focusObj;
		this.__focusObj = focusObj;
		focusObj.focus();
	}
};

/**
 * @private
 */
DwtKeyboardMgr.__onFocusHdlr =
function(ev) {
	//DBG.println(AjxDebug.DBG3, "DwtKeyboardMgr.__onFocusHdlr");
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	kbMgr.__dwtCtrlHasFocus = true;
	var focusObj = kbMgr.__focusObj;
	if (focusObj != null && focusObj.__doFocus != null && (typeof focusObj.__doFocus == "function"))
		focusObj.__doFocus();			
};

/**
 * @private
 */
DwtKeyboardMgr.__onBlurHdlr =
function(ev) {
	//DBG.println(AjxDebug.DBG3, "DwtKeyboardMgr.__onBlurHdlr");
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	var focusObj;
	
	// Got to play the trick with HTML elements which get focus before blur is called
	// on the old focus object. (see _grabFocus)
	var focusObj =  (kbMgr.__oldFocusObj == null) ? kbMgr.__focusObj
					: kbMgr.__oldFocusObj;
	
	if (focusObj != null && focusObj.__doBlur != null && (typeof focusObj.__doBlur == "function"))
		focusObj.__doBlur();
		
	/* FIXME The code below that is commented out fixes a bug that surfaces if you tab in
	 * the address bar or search field in FF. The bug is that depending on where
	 * you had focus, you could get a visual artifact (temporary). However, the code
	 * broke the fact that when focus leaves the browser window, then returns, then
	 * if a DwtControl had focus it will not get the appropriate highlight
	 */	
	//kbMgr.__oldFocusObj = kbMgr.__focusObj = null;
	kbMgr.__oldFocusObj = null;
	kbMgr.__dwtCtrlHasFocus = false;	
};


/**
 * @private
 */
DwtKeyboardMgr.__keyUpHdlr =
function(ev) {
	//DBG.println(AjxDebug.DBG1, "DwtKeyboardMgr.__keyUpHdlr");
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = kev.keyCode;
	
	if (kbMgr.__kbEventStatus != DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED) {
		//DBG.println(AjxDebug.DBG1, "DwtKeyboardMgr.__keyUpHdlr: KEY UP BLOCKED");
		kev._stopPropagation = true;
		kev._returnValue = false;
		kev.setToDhtmlEvent(ev);
		return false;
	 } 
};

/**
 * @private
 */
DwtKeyboardMgr.__keyPressHdlr =
function(ev) {
	//DBG.println(AjxDebug.DBG1, "DwtKeyboardMgr.__keyPressHdlr");
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = kev.keyCode;
	
	if (kbMgr.__kbEventStatus != DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED) {
		//DBG.println(AjxDebug.DBG1, "DwtKeyboardMgr.__keyPressHdlr: KEY PRESS BLOCKED");
		kev._stopPropagation = true;
		kev._returnValue = false;
		kev.setToDhtmlEvent(ev);
		return false;
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
 * tag group hierarchy return false indicating that we should leave all events
 * related to it alone (case 3)
 * 
 * @private
 */
DwtKeyboardMgr.__syncFocus =
function(kbMgr, obj) {
	if (!kbMgr.__dwtCtrlHasFocus) {
		//DBG.println(AjxDebug.DBG1, "CONTROL NOT FOCUS: _focusObj: " + kbMgr.__focusObj + " - obj: " + obj);
		if (kbMgr.__focusObj != obj) {
			//DBG.println(AjxDebug.DBG1, "FOCUS OBJECT NOT THE TAB OBJECT FOCUS");
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
	var shell = DwtShell.getShell(window);
	var kbMgr = shell.getKeyboardMgr();
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = kev.keyCode;

	// Popdown any tooltip
	shell.getToolTip().popdown();
	
	// Sync up focus if needed
	var focusInTGMember = DwtKeyboardMgr.__syncFocus(kbMgr, kev.target);
	
	/*if (!focusInTGMember) {
		DBG.println(AjxDebug.DBG3, "Object is not in tab hierarchy");
	}*/
			
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
	 	if (kbMgr.__currTabGroup) {
		 	// If a menu is popped up then don't act on the Tab
		 	if (!DwtMenu.menuShowing()) {
			 	DBG.println(AjxDebug.DBG3, "Tab");
			 	// If the tab hit is in an element or if the current tab group has
			 	// a focus member
				if (focusInTGMember || kbMgr.__currTabGroup.getFocusMember()) {
				 	if (!kev.shiftKey)
				 		kbMgr.__currTabGroup.getNextFocusMember(true);
				 	else
				 		kbMgr.__currTabGroup.getPrevFocusMember(true);
			 	} else {
			 		//DBG.println(AjxDebug.DBG1, "RESETTING TO FIRST");
			 		// If there is no current focus member, then reset
			 		kbMgr.__currTabGroup.resetFocusMember(true);
			 	}
		 	}
			kbMgr.__kbEventStatus = DwtKeyboardMgr.__KEYSEQ_HANDLED;
			kev._stopPropagation = true;
			kev._returnValue = false;
			kev.setToDhtmlEvent(ev);
			return false;
	 	} else {
	 		// No tab groups registered. Let the browser deal with tabs
			kbMgr.__kbEventStatus = DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
			kev._stopPropagation = false;
			kev._returnValue = true;
			kev.setToDhtmlEvent(ev);
			return true;	 		
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
	 
	/* If the focus object is a DwtControl, then set the value of the keyboard
	 * focus field to "" so that it doesn't overflow or get crufty
	 */
	if (kbMgr.__dwtCtrlHasFocus) 
		kbMgr._kbFocusField.value = "";
	 
	/* Filter out the following keys: Alt, Shift, Ctrl. Also filter out
	 * alphanumeric keys if the target of the key event is an input field
	 * or a text area and there is no pending sequence in play and the key
	 * is alphanumeric or a punctuation key */
	/* TODO not all inputs accept the same values (e.g. text vs radio etc) so 
	 * we need to differentiate. Should change isUsableTextInputValue(keyCode) to 
	 * isUsableInputValue(keyCode, inputType) where inputType is the type of input*/
	// Note that FF on the mac has an issue reporting the ALT+<keycode> it
	// always ends up reporting undefined for the <keycode>. For this reason I
	// have added Ctrl analogs below	
	/* TODO translate Ctl on FF Mac into Alt */ 
	if (DwtKeyMapMgr.isModifier(keyCode)
		|| (!kbMgr.__dwtCtrlHasFocus 
			&& kbMgr.__killKeySeqTimedActionId == -1 && !kev.ctrlKey && !kev.altKey
			&& DwtKeyMapMgr.isUsableTextInputValue(keyCode))) {
		//DBG.println(AjxDebug.DBG3, "valid input field data");
		kbMgr.__kbEventStatus = DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
		kev._stopPropagation = false;
		kev._returnValue = true;
		kev.setToDhtmlEvent(ev);
		return true;
	}
	 
	/* Cancel any pending time action to kill the keysequence */
	if (kbMgr.__killKeySeqTimedActionId != -1) {
		AjxTimedAction.cancelAction(kbMgr.__killKeySeqTimedActionId);
		kbMgr.__killKeySeqTimedActionId = -1;
	}
		
 	var key = "";
	
	if (kev.ctrlKey)
		key += DwtKeyMap.CTRL;
		
	if (kev.altKey)
		key += DwtKeyMap.ALT;
		
	if (kev.shiftKey)
		key += DwtKeyMap.SHIFT;
	
	kbMgr.__keySequence[kbMgr.__keySequence.length] = key + kbMgr.__keyMapMgr.keyCode2Char(keyCode);

	DBG.println(AjxDebug.DBG3, "KEYCODE: " + keyCode + " - KEY SEQ: " + kbMgr.__keySequence.join(""));
	
	/* If a DWT component has "focus", then dispatch to that component
	 * if the component handles the event, then stop, else hand it off
	 * the global handler if one is registered */
	var handled = DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	var obj = (kbMgr.__dwtCtrlHasFocus) ? kbMgr.__focusObj : null;
	kbMgr.__kbEventStatus = handled = kbMgr.__handleKeyEvent(obj, kev);
	switch (handled) {
		case DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED:
			kbMgr.__keySequence.length = 0;
			kev._stopPropagation = false;
			kev._returnValue = true;
			kev.setToDhtmlEvent(ev);
			return true;
			break;
			
		case DwtKeyboardMgr.__KEYSEQ_HANDLED:
			kbMgr.__keySequence.length = 0;
		case DwtKeyboardMgr.__KEYSEQ_PENDING:
			kev._stopPropagation = true;
			kev._returnValue = false;
			kev.setToDhtmlEvent(ev);
			return false;
			break;
	}
};

/**
 * Tries to get the key event handled, first by the object with focus, then
 * by the global handler (if it's not blocked).
 * 
 * @private
 */
DwtKeyboardMgr.prototype.__handleKeyEvent =
function(obj, kev, mapName, forceActionCode) {
	var handled = DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	if (obj != null && (obj instanceof DwtControl)) {
		mapName = mapName ? mapName : obj.getKeyMapName ? obj.getKeyMapName() : obj.toString();
		DBG.println(AjxDebug.DBG3, "object " + obj.toString() + " dispatching to map: " + mapName);
		handled = this.__dispatchKeyEvent(obj, mapName, kev, forceActionCode);
	}

	if ((handled == DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED) && this.__globalKeyActionHdlr &&
		!(this.__currTabGroup && this.__currTabGroup.isGlobalHandlingBlocked())) {
		DBG.println(AjxDebug.DBG3, "Dispatching to global map: " + this.__globalKeyActionHdlr.getKeyMapNameToUse());
		handled = this.__dispatchKeyEvent(this.__globalKeyActionHdlr, 
							this.__globalKeyActionHdlr.getKeyMapNameToUse(), kev, forceActionCode);
		//DBG.println(AjxDebug.DBG1, "GLOBAL HANDLER RETURNED: " + handled);
	}

	return handled;
};

/**
 * Handles event dispatching
 * 
 * @private
 */
DwtKeyboardMgr.prototype.__dispatchKeyEvent = 
function(hdlr, mapName, ev, forceActionCode) {
	var actionCode = this.__keyMapMgr.getActionCode(this.__keySequence, mapName, forceActionCode);
	if (actionCode == DwtKeyMapMgr.NOT_A_TERMINAL) {
		DBG.println(AjxDebug.DBG3, "SCHEDULING KILL SEQUENCE ACTION");
		/* setup a timed action to redispatch/kill the key sequence in the event
		 * the user does not press another key in the allotted time */
		this.__hdlr = hdlr;
		this.__mapName = mapName;
		this.__ev = ev;
		this.__killKeySeqTimedActionId = 
			AjxTimedAction.scheduleAction(this.__killKeySeqTimedAction, this.__keyTimeout);
		return DwtKeyboardMgr.__KEYSEQ_PENDING;	
	} else if (actionCode != null) {
		/* It is possible that the component may not handle a valid action
		 * particulary actions defined in the global map */
		DBG.println(AjxDebug.DBG3, "HANDLING ACTION: " + actionCode);
		return (hdlr.handleKeyAction(actionCode, ev)) ? DwtKeyboardMgr.__KEYSEQ_HANDLED
													   : DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	} else {	
		DBG.println(AjxDebug.DBG3, "TERMINAL W/O ACTION CODE");
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
	DBG.println(AjxDebug.DBG3, "KILLING KEY SEQUENCE: " + this.__mapName);
	this.__handleKeyEvent(this.__hdlr, this.__ev, this.__mapName, true);
	this.__killKeySeqTimedActionId = -1;
	this.__keySequence.length = 0;
};

/**
 * @private
 */
DwtKeyboardMgr.prototype.__tabGrpChangeListener =
function(ev) {
	this.__doGrabFocus(ev.newFocusMember);
};
