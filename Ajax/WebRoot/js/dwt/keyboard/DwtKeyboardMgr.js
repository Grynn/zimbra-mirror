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
	this.__blockHandlingStack = [];
	/**@private*/
	this.__blockGlobalHandling = false;
	/**@private*/
	this.__tabGroupChangeListenerObj = new AjxListener(this, this.__tabGrpChangeListener);
	
	this.__currTabGroup  = new DwtTabGroup();
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
 * Blocks/unblocks actions being delivered to the global key action handler.
 * This is useful when global key action  handling is to be blocked, 
 * for example in the case of dialogs and menus
 * 
 * @param {boolean} block if true will block the global handling of key actions
 */
 DwtKeyboardMgr.prototype.blockGlobalHandling =
 function(block) {
 	this.__blockGlobalHandling = block;
 }
 
/**
 * Pushes <code>tabGroup</code> onto the stack and makes it the active tab group.
 * This method also pushes the current "block global handling" state on to the stack
 * 
 * @param {DwtTabGroup} tabGroup tab group to push onto the stack
 * @param {boolean} blockGlobalHandling if true, then key actions are not dispatched
 * 		to the global key action handler for this tab group. (optional)
 * 
 * @see #popTabGroup
 * @see #blockGlobalHandling
 */
DwtKeyboardMgr.prototype.pushTabGroup =
function(tabGroup, blockGlobalHandling) {
	if (!this.__keyboardHandlingInited)
		throw DwtKeyboardMgr.KEYMAP_NOT_REGISTERED;
		
	this.__tabGrpStack.push(tabGroup);
	this.__blockHandlingStack.push(this.__blockGlobalHandling);
	var focusMember = tabGroup.getFocusMember();
	if (!focusMember)
		focusMember = tabGroup.resetFocusMember(true);
	tabGroup.addFocusChangeListener(this.__tabGroupChangeListenerObj);
	this.grabFocus(focusMember);
	this.__currTabGroup = tabGroup;	
	
	if (blockGlobalHandling != null)
		this.__blockGlobalHandling = blockGlobalHandling;
};

/**
 * Pops the current tab group off the top of the tab group stack. The previous 
 * tab group (if there is one) then becomes the current tab group. The previous
 * value for "block global handling" is also reinstated
 * 
 * @return the popped tab group
 * @type DwtTabGroup
 */
DwtKeyboardMgr.prototype.popTabGroup =
function() {
	if (!this.__keyboardHandlingInited)
		throw DwtKeyboardMgr.KEYMAP_NOT_REGISTERED;
		
	var tabGroup = this.__tabGrpStack.pop();
	
	if (tabGroup)
		tabGroup.removeFocusChangeListener(this.__tabGroupChangeListenerObj);
	
	var currTg = null;
	var blockHandling = false;
	if (this.__tabGrpStack.length > 0) {
		currTg = this.__tabGrpStack[this.__tabGrpStack.length-1];
		blockHandling = this.__blockHandlingStack[this.__blockHandlingStack.length-1];
		var focusMember = currTg.getFocusMember();
		if (!focusMember)
			focusMember = currTg.resetFocusMember(true);
		if (focusMember)
			this.grabFocus(focusMember);
	}
	this.__crrTabGroup = currTg ? currTg : 	new DwtTabGroup();
	this.__blockGlobalHandling = blockHandling;
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
	this.__currTabGroup = tabGroup;
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
	//DBG.println(AjxDebug.DBG3, "_doGrabFocus");
	if (!focusObj)
		return;
		
	if (focusObj instanceof DwtControl) {
		//DBG.println(AjxDebug.DBG3, "focusObj is instance of DwtControl: " + focusObj);
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
	DBG.println(AjxDebug.DBG1, "DwtKeyboardMgr.__keyUpHdlr");
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = kev.keyCode;
	
	if (kbMgr._kbEventStatus != DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED) {
		DBG.println(AjxDebug.DBG1, "DwtKeyboardMgr.__keyUpHdlr: KEY UP BLOCKED");
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
	DBG.println(AjxDebug.DBG1, "DwtKeyboardMgr.__keyPressHdlr");
	var kbMgr = DwtShell.getShell(window).getKeyboardMgr();
	var kev = DwtShell.keyEvent;
	kev.setFromDhtmlEvent(ev);
	var keyCode = kev.keyCode;
	
	if (kbMgr._kbEventStatus != undefined && kbMgr._kbEventStatus != DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED) {
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
			if (kbMgr.__currTabGroup.setFocusMember(obj)) {
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
	var shell = DwtShell.getShell(window)
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
	 	// If a menu is popped up then don't act on the Tab
	 	if (!DwtMenu.menuShowing()) {
		 	DBG.println(AjxDebug.DBG3, "TAB HIT!");
		 	// If the tab hit is in an element or if the current tab group has
		 	// a focus member
			if (focusInTGMember || kbMgr.__currTabGroup.getFocusMember()) {
			 	if (!kev.shiftKey)
			 		kbMgr.__currTabGroup.getNextFocusMember(true);
			 	else
			 		kbMgr.__currTabGroup.getPrevFocusMember(true);
		 	} else {
		 		DBG.println(AjxDebug.DBG1, "RESETTING TO FIRST");
		 		// If there is no current focus member, then reset
		 		kbMgr.__currTabGroup.resetFocusMember(true);
		 	}
	 	}
		kbMgr._kbEventStatus = DwtKeyboardMgr.__KEYSEQ_HANDLED;
		kev._stopPropagation = true;
		kev._returnValue = false;
		kev.setToDhtmlEvent(ev);
		return false;
	 } else if (!focusInTGMember && AjxEnv.isGecko && kev.target instanceof HTMLHtmlElement) {
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
	if (DwtKeyMapMgr.isModifier(keyCode)
		|| (!kbMgr.__dwtCtrlHasFocus 
			&& kbMgr.__killKeySeqTimedActionId == -1 && !kev.ctrlKey && !kev.altKey
			&& DwtKeyMapMgr.isUsableTextInputValue(keyCode))) {
		//DBG.println(AjxDebug.DBG3, "valid input field data");
		kbMgr._kbEventStatus = DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
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

	DBG.println("KEYCODE: " + keyCode + " - KEY SEQ: " + kbMgr.__keySequence.join(""));
	
	/* If a DWT component has "focus", then dispatch to that component
	 * if the component handles the event, then stop, else hand it off
	 * the global handler if one is registered */
	var handled = DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	var obj = (kbMgr.__dwtCtrlHasFocus) ? kbMgr.__focusObj : null;

	if (obj != null && (obj instanceof DwtControl)) {
		var mapName = obj.toString();
		//DBG.println("Dispatching to map: " + mapName);
		handled = kbMgr.__dispatchKeyEvent(obj, mapName, kev);
	}
	
	if (!kbMgr.__blockGlobalHandling && handled == DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED && kbMgr.__globalKeyActionHdlr != null) {
		handled = kbMgr.__dispatchKeyEvent(kbMgr.__globalKeyActionHdlr, 
							kbMgr.__globalKeyActionHdlr.getKeyMapNameToUse(), kev);
		//DBG.println(AjxDebug.DBG1, "GLOBAL HANDLER RETURNED: " + handled);
	}
	
	kbMgr._kbEventStatus = handled;
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
 * @private
 */
DwtKeyboardMgr.prototype.__dispatchKeyEvent = 
function(hdlr, mapName, ev) {
	var actionCode = this.__keyMapMgr.getActionCode(this.__keySequence, mapName);
	if (actionCode == DwtKeyMapMgr.NOT_A_TERMINAL) {
		//DBG.println("SCHEDULING KILL SEQUENCE ACTION");
		/* setup a timed action to kill the key sequence in the event
		 * the user does not press another key in the allotted time */
		this.__killKeySeqTimedActionId = 
			AjxTimedAction.scheduleAction(this.__killKeySeqTimedAction, 1000);
		return DwtKeyboardMgr.__KEYSEQ_PENDING;	
	} else if (actionCode != null) {
		/* It is possible that the component may not handle a valid action
		 * particulary actions defined in the global map */
		//DBG.println("HANDLING ACTION: " + actionCode);
		return (hdlr.handleKeyAction(actionCode, ev)) ? DwtKeyboardMgr.__KEYSEQ_HANDLED
													   : DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	} else {	
		//DBG.println("TERMINAL W/O ACTION CODE");
		return DwtKeyboardMgr.__KEYSEQ_NOT_HANDLED;
	}
	
};

/**
 * @private
 */
DwtKeyboardMgr.prototype.__killKeySequenceAction =
function() {
	//DBG.println("KILLING KEY SEQUENCE");
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
