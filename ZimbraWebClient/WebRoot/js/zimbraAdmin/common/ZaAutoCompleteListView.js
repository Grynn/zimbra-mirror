/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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
* Creates a new autocomplete list. The list isn't populated or displayed until some
* autocompletion happens. Takes a data class and loader, so that when data is needed (it's
* loaded lazily), the loader can be called on the data class.
* @constructor
* @class
* This class implements autocomplete functionality. It has two main parts: matching data based
* on keystroke events, and displaying/managing the list of matches. This class is theoretically
* neutral concerning the data that gets matched (as long as its class has an autocompleteMatch()
* method), and the field that it's being called from.
* <p>
* The data class's autocompleteMatch() method should returns a list of matches, where each match is
* an object with the following properties:</p>
* <table border="1">
* <tr><td>data</td><td>the object being matched</td></tr>
* <tr><td>text</td><td>the text to display for this object in the list</td></tr>
* <tr><td>[key1]</td><td>a string that may be used to replace the typed text<td></tr>
* <tr><td>[keyN]</td><td>a string that may be used to replace the typed text<td></tr>
* </table>
* </p><p>
* The data class will also need a method isUniqueValue(str), which returns true if the given string
* maps to a single match.
* </p><p>
* The calling client also specifies the key in the match result for the string that will be used
* to replace the typed text (also called the "completion string"). For example, the completion 
* string for matching contacts could be a full address, or just the email.
* </p>
* 
* @author Conrad Damon
* @param parent				the element that created this list
* @param className			CSS class
* @param dataLoaderClass			the class that has the data loader
* @param dataLoaderMethod	a method of dataLoaderClass that returns data to match against
* @param matchValue			name of field in match result to use for completion
* @param inputFieldElement	(HTMLTextAreaElement) the input field element which autocomplete is used for
* @param locCallback		callback into client to get desired location of autocomplete list
* @param compCallback		callback into client to notify it that completion happened
* @param separator			separator (gets added to the end of a match)
*/
var i = 1 ;
//ZaSettings.AC_TIMER_INTERVAL = i ++;

ZaAutoCompleteListView = function(params) {

	var className = params.className ? params.className : "autoCompleteList";
	DwtComposite.call(this, params.parent, className, DwtControl.ABSOLUTE_STYLE);
	
//	this._appCtxt = this.shell.getData(ZaAppCtxt.LABEL);
	var app = null;
	try {
		this.shell.getData(ZaAppCtxt.LABEL).getApp();
	}catch (e){
		DBG.println(e.message);
	}
	var _dataLoaderClass = params.dataLoaderClass;
	this._dataLoaderObject = new _dataLoaderClass();
	this._dataLoaderMethod = params.dataLoaderMethod;
	this._dataLoading = false;
	this._data = null ;
	this._matchValue = params.matchValue ? params.matchValue : "inputFieldCompleteValue";
	this._matchText = params.matchText ? params.matchText : "matchListFieldText" ;
	this._inputFieldXFormItem = params.inputFieldXFormItem ;
	this._inputFieldXForm = this._inputFieldXFormItem.getForm() ;
	this._inputFieldElement = this._inputFieldXFormItem.getElement() ;
	this._locCallback = params.locCallback;
	this._compCallback = params.compCallback;
	this._separator = (params.separator != null) ? params.separator : ";";
	
	// mouse event handling
	this._setMouseEventHdlrs();
	this.addListener(DwtEvent.ONMOUSEDOWN, new AjxListener(this, this._mouseDownListener));
	this.addListener(DwtEvent.ONMOUSEOVER, new AjxListener(this, this._mouseOverListener));
	this._addSelectionListener(new AjxListener(this, this._listSelectionListener));
	this._outsideListener = new AjxListener(this, this._outsideMouseDownListener);

	// only trigger matching after a sufficient pause
	this._acInterval = 300 ;
	this._acAction = new AjxTimedAction(null, this._autocompleteAction);
	this._acActionId = -1;

	// for managing focus on Tab in Firefox
	if (AjxEnv.isFirefox) {
		this._focusAction = new AjxTimedAction(null, this._focus);
	}

	this._internalId = AjxCore.assignId(this);
	this._numChars = 0;
	this._matches = new AjxVector();
	this._done = new Object();
	this.setVisible(false);
	
	//set the handler to the input field element
	this.handle(this._inputFieldElement);
}

ZaAutoCompleteListView.prototype = new DwtComposite;

// map of characters that are completion characters
ZaAutoCompleteListView.DELIMS = [',', ';', '\n', '\r', '\t'];
ZaAutoCompleteListView.IS_DELIM = new Object();
for (var i = 0; i < ZaAutoCompleteListView.DELIMS.length; i++)
	ZaAutoCompleteListView.IS_DELIM[ZaAutoCompleteListView.DELIMS[i]] = true;

// Public static methods

/**
* "onkeydown" handler for catching Tab and Esc keys. We don't want to let the browser
* handle this event for those (which it will do before we get the keyup event).
*
* @param ev		the key event
*/
ZaAutoCompleteListView.onKeyDown =
function(ev) {
	DBG.println(AjxDebug.DBG3, "onKeyDown");
	var element = DwtUiEvent.getTargetWithProp(ev, "id");
	var aclv = element && AjxCore.objectWithId(element._acListViewId);
	if (aclv) {
		aclv._inputLength = element.value.length;
	}
	var key = DwtKeyEvent.getCharCode(ev);
	return (key == DwtKeyEvent.KEY_TAB || key == DwtKeyEvent.KEY_ESCAPE) ? ZaAutoCompleteListView.onKeyUp(ev) : true;
}

/**
* "onkeyup" handler for performing autocompletion. The reason it's an "onkeyup" handler is that neither 
* "onkeydown" nor "onkeypress" arrives after the form field has been updated.
*
* @param ev		the key event
*/
ZaAutoCompleteListView.onKeyUp =
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	if (ev.type == "keyup")
		DBG.println(AjxDebug.DBG3, "onKeyUp");
	var element = DwtUiEvent.getTargetWithProp(ev, "id");
	var aclv = AjxCore.objectWithId(element._acListViewId);
	
	var id = element.id;
	var key = DwtKeyEvent.getCharCode(ev);
	// Tab/Esc handled in keydown for IE
	if (AjxEnv.isIE && ev.type == "keyup" && (key == 9 || key == 27))
		return true;
	var value = element.value;
	DBG.println(AjxDebug.DBG3, ev.type + " event, key = " + key + ", value = " + value);
	ev.inputLengthChanged = (value.length != aclv._inputLength);

	var inputFieldOldValue;
	if (aclv._inputFieldXFormItem) {
		DBG.println(AjxDebug.DBG1, "Set the inputField " + aclv._inputFieldXFormItem["refPath"] + " value: " + value) ;
		inputFieldOldValue = aclv._inputFieldXFormItem.getInstanceValue();
		var onChangeMethod = aclv._inputFieldXFormItem.getOnChangeMethod();
		if(onChangeMethod != null && typeof(onChangeMethod) == "function") {
			onChangeMethod.call(aclv._inputFieldXFormItem,value,ev,aclv._inputFieldXForm);
		} else {
			aclv._inputFieldXFormItem.setInstanceValue(value);
		}
	}

	// reset timer on any address field key activity
	if (aclv._acActionId != -1) {
		AjxTimedAction.cancelAction(aclv._acActionId);
		aclv._acActionId = -1;
	}
	
	// Figure out what this handler should return. If it returns true, the browser will
	// handle the key event. That usually means it just echoes the typed character, but
	// it could do something like change focus (eg tab). We let the browser handle input
	// characters, and anything weird that we don't want to deal with. The only keys we
	// don't let the browser handle are ones that control the features of the autocomplete
	// list.

	if (key == 16 || key == 17 || key == 18) // SHIFT, ALT, or CTRL
		return true;
	if (ev.altKey || ev.ctrlKey) // ALT and CTRL combos
		return true;
	// if the field is empty, clear the list
	if (!value) {
		aclv.reset();
		return true;
	}
	if (key == 37 || key == 39) // left/right arrow key
		return true;
	// Pass tab through if there's no list (will transfer focus)
	if ((key == 9) && !aclv.size())
		return true;

	if (ev.inputLengthChanged || (key == 3 || key == 9 || key == 13))
		aclv._numChars++;

	// if the user types a single delimiting character with the list showing, do completion
	var isDelim = (aclv.getVisible() && (aclv._numChars == 1) && 
				   ((key == 3 || key == 9 || key == 13) || (!ev.shiftKey && (key == 59 || key == 186 || key == 188))));

	DBG.println(AjxDebug.DBG3, "numChars = " + aclv._numChars + ", key = " + key + ", isDelim: " + isDelim);
	if (isDelim || (key == 27 || key == 38 || key == 40)) {
		aclv.handleAction(key, isDelim);
		// In Firefox, focus shifts on Tab even if we return false (and stop propagation and prevent default),
		// so make sure the focus stays in this element.
		if (AjxEnv.isFirefox && key == 9) {
			aclv._focusAction.args = [ element ];
			AjxTimedAction.scheduleAction(aclv._focusAction, 0);
		}
		DwtUiEvent.setBehaviour(ev, true, false);
		return false;
	}

	// skip if it's some weird character
	if (!ev.inputLengthChanged && 
		(key != 3 && key != 13 && key != 9 && key != 8 && key != 46))
		return true;

	// regular input, schedule autocomplete
	var ev1 = new DwtKeyEvent();
	DwtKeyEvent.copy(ev1, ev);
	ev1.aclv = aclv;
	ev1.element = element;
	aclv._acAction.obj = aclv;
	aclv._acAction.args = [ ev1 ];
	DBG.println(AjxDebug.DBG2, "scheduling autocomplete");
	aclv._acActionId = AjxTimedAction.scheduleAction(aclv._acAction, aclv._acInterval);
	//fire the xform changed event
	var inputFieldNewValue = inputFieldOldValue;
	if (aclv._inputFieldXFormItem){
		inputFieldNewValue = aclv._inputFieldXFormItem.getInstanceValue();
	}
	
	if (aclv._inputFieldXForm){
		aclv._inputFieldXForm.setIsDirty(true, aclv._inputFieldXFormItem ) ;
		aclv._inputFieldXForm.notifyListeners(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new DwtXFormsEvent(aclv._inputFieldXForm, aclv._inputFieldXFormItem,true));
		if(inputFieldNewValue != inputFieldOldValue){
			aclv._inputFieldXForm.notifyListeners(DwtEvent.XFORMS_VALUE_CHANGED, new DwtXFormsEvent(aclv._inputFieldXForm, aclv._inputFieldXFormItem,true));
		}
	}
	
	return true;
}

// Public methods

ZaAutoCompleteListView.prototype.toString = 
function () {
	return "ZaAutoCompleteListView";
}

/**
* Adds autocompletion to the given field by setting key event handlers.
*
* @param element		an HTML element
*/
ZaAutoCompleteListView.prototype.handle =
function(element) {
	element._acListViewId = this._internalId;
	Dwt.setHandler(element, DwtEvent.ONKEYDOWN, ZaAutoCompleteListView.onKeyDown);
	Dwt.setHandler(element, DwtEvent.ONKEYUP, ZaAutoCompleteListView.onKeyUp);
}

/**
* Autocompletion of addresses. Should be called by a handler for a keyboard event.
*
* @param element	the element (some sort of text field) doing autocomplete
* @param loc		where to popup the list, if appropriate
*/
ZaAutoCompleteListView.prototype.autocomplete =
function(element, loc) {

	this.reset(); // start fresh
	this._element = element; // for updating element later
	this._loc = loc;
	var text = element.value;

	this._autocomplete(text);
}

/**
* Resets the state of the autocomplete list.
*/
ZaAutoCompleteListView.prototype.reset =
function() {
	this._matches.removeAll();
	this.show(false);
}

/**
* Checks the given key to see if it's used to control the autocomplete list in some way.
* If it does, the action is taken and the key won't be echoed into the input area.
*
* The following keys are action keys:
*	38 40		up/down arrows (list selection)
*	27			escape (hide list)
*
* The following keys are delimiters (trigger completion):
*	3 13		return
*	9			tab
*	59 186		semicolon
*	188			comma
*
* @param key		a numeric key code
* @param isDelim	true if a single delimiter key was typed
*/
ZaAutoCompleteListView.prototype.handleAction =
function(key, isDelim) {
	DBG.println(AjxDebug.DBG2, "autocomplete handleAction for key " + key + " / " + isDelim);

	if (isDelim) {
		this._update();
	} else if (key == 38 || key == 40) {
		// handle up and down arrow keys
		var idx = this._getSelectedIndex();
		var size = this.size();
		if (size <= 1) return;
		var newIdx;
		if (key == 40 && (idx < size - 1)) {
			newIdx = idx + 1;
			this._setSelected(newIdx);
		} else if (key == 38 && (idx > 0)) {
			newIdx = idx - 1;
			this._setSelected(newIdx);
		}
	} else if (key == 27) {
		this.reset(); // ESC hides the list
	}
}

// Private methods

// Called as a timed action, after a sufficient pause in typing within an address field.
ZaAutoCompleteListView.prototype._autocompleteAction =
function(ev) {
	try {
		DBG.println(AjxDebug.DBG2, "performing autocomplete");
		var element = ev.element;
		var aclv = ev.aclv;
		aclv._acActionId = -1; // so we don't try to cancel
		aclv._numChars = 0;

		if (this._locCallback) {	
			var loc = this._locCallback.run(ev);
			aclv.autocomplete(element, loc);
		}
	} catch (ex) {
		DBG.println("Session expired? No controller to handle exception. Cannot autocomplete w/o contact list.");
	}
}

/**
* Displays the current matches in a popup list, selecting the first.
*
* @param show	whether to display the list
* @param loc	where to display the list
*/
ZaAutoCompleteListView.prototype.show =
function(show, loc) {
	DBG.println(AjxDebug.DBG3, "autocomplete show: " + show);
	if (show) {
		this._popup(loc);
	} else {
		this._popdown();
	}
}

// Private methods

// Finds the next chunk of text in a string that we should try to autocomplete, by reading
// until it hits some sort of address delimiter (or runs out of text)
ZaAutoCompleteListView.prototype._nextChunk =
function(text, start) {
	while (text.charAt(start) == ' ')	// ignore leading space
		start++;
	for (var i = start; i < text.length; i++) {
		var c = text.charAt(i);
		if (ZaAutoCompleteListView.IS_DELIM[c])
			return {text: text, str: text.substring(start, i), start: start, end: i, delim: true};
	}
	return {text: text, str: text.substring(start, i), start: start, end: i, delim: false};
}

// Looks for matches for a string and either displays them in a list, or does the completion
// immediately (if the string was followed by a delimiter). The chunk object that we get has
// information that allows us to do the replacement if we are performing completion.
ZaAutoCompleteListView.prototype._autocomplete =
function(str) {
	// if string is empty or already a delimited address, no reason to look for matches
	if (!(str && str.length) || (this._done[str]))
		return;

	// do matching
	this._removeAll();
	if (!this._dataLoading) {
		var callback = new AjxCallback(this, this.dataLoadedCallback);
		this._dataLoading = true;
		this._dataLoaderMethod.call (this._dataLoaderObject, str, callback);
	}	
}

// Replaces a string within some text from the selected address match.
ZaAutoCompleteListView.prototype._complete =
function(text) {
	DBG.println(AjxDebug.DBG3, "complete: selected is " + this._selected);
	var match = this._getSelected();
	if (!match)	return;

	var start = this._start;
	var end = hasDelim ? this._end + 1 : this._end;
	DBG.println(AjxDebug.DBG2, "update replace range: " + start + " - " + end);
	var value = match[this._matchValue];
//	var newText = [text.substring(0, start), value, this._separator, text.substring(end, text.length)].join("");
	this._done[value] = true;
	DBG.display(AjxDebug.DBG2, newText);
	return {text: newText, start: start + value.length + this._separator.length, match: match};
}

// Resets the value of an element to the given text.
ZaAutoCompleteListView.prototype._updateField =
function(match) {
	var el = this._element;
	el.value = match[this._matchValue];
	el.focus();
	this.reset();
	this._inputFieldXFormItem.setInstanceValue(match[this._matchValue]);
	if(this._inputFieldXForm){	
		this._inputFieldXForm.setIsDirty(true, this._inputFieldXFormItem) ;
        	this._inputFieldXForm.notifyListeners(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new DwtXFormsEvent(this._inputFieldXForm, this._inputFieldXFormItem,true));
		this._inputFieldXForm.notifyListeners(DwtEvent.XFORMS_VALUE_CHANGED, new DwtXFormsEvent(this._inputFieldXForm, this._inputFieldXFormItem,true));
	}
	if (this._compCallback)
		this._compCallback.run(match, this._inputFieldXFormItem);
}

// Updates the element with the currently selected match.
ZaAutoCompleteListView.prototype._update =
function() {
	var match = this._getSelected();
	if (!match)	return;	
	this._updateField(match);
}

// Listeners

// MOUSE_DOWN selects a match and performs an update. Note that we don't wait for
// a corresponding MOUSE_UP event.
ZaAutoCompleteListView.prototype._mouseDownListener = 
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	var div = DwtUiEvent.getTarget(ev);
	if (!div || div._pos == null)
		return;
	if (ev.button == DwtMouseEvent.LEFT) {
		this._setSelected(div._pos);
		if (this.isListenerRegistered(DwtEvent.SELECTION)) {
	    	var selEv = DwtShell.selectionEvent;
	    	DwtUiEvent.copy(selEv, ev);
	    	selEv.match = div._match;
	    	selEv.detail = 0;
	    	this.notifyListeners(DwtEvent.SELECTION, selEv);
	    	return true;
	    }		
	}
}

// Mouse over selects a match
ZaAutoCompleteListView.prototype._mouseOverListener = 
function(ev) {
	ev = DwtUiEvent.getEvent(ev);
	var div = DwtUiEvent.getTarget(ev);
	if (!div || div._pos == null)
		return;
	this._setSelected(div._pos);
}

// Seems like DwtComposite should define this method
ZaAutoCompleteListView.prototype._addSelectionListener = 
function(listener) {
	this._eventMgr.addListener(DwtEvent.SELECTION, listener);
}

ZaAutoCompleteListView.prototype._listSelectionListener = 
function(ev) {
	this._update();
};

// Layout

// Creates the list and its member elements based on the matches we have. Each match becomes a 
// DIV. The first match is automatically selected.
ZaAutoCompleteListView.prototype._set =
function(sel) {
	var thisHtmlElement = this.getHtmlElement();
	thisHtmlElement.innerHTML = "";
	var len = this._matches.size();
	for (var i = 0; i < len; i++) {
		var match = this._matches.get(i);
		if (match){
			var div = document.createElement("div");		
			div._pos = i;
			div[DwtListView._STYLE_CLASS] = "Row";
			div[DwtListView._SELECTED_STYLE_CLASS] = div[DwtListView._STYLE_CLASS] + "-" + DwtCssStyle.SELECTED;
                        div[DwtListView._STYLE_CLASS] = "ZaAutoCompleteListRow";
			div.className = div[DwtListView._STYLE_CLASS];
			div.innerHTML = AjxStringUtil.htmlEncode (match[this._matchText]);
			thisHtmlElement.appendChild(div);
		}
	}
	this._selected = sel || 0;
	this._setSelected(this._selected);
}

// Displays the list
ZaAutoCompleteListView.prototype._popup = 
function(loc) {
	this.setLocation(loc.x, loc.y);
	this.setSize (Dwt.getSize(this._inputFieldElement).x - 2) ; //set width only, 2 is the border-width
	this.setVisible(true);
	this.setZIndex(Dwt.Z_DIALOG_MENU);
	ZaAutoCompleteListView._activeAcList = this;
	DwtEventManager.addListener(DwtEvent.ONMOUSEDOWN, ZaAutoCompleteListView._outsideMouseDownListener);
	this.shell._setEventHdlrs([DwtEvent.ONMOUSEDOWN]);
	this.shell.addListener(DwtEvent.ONMOUSEDOWN, this._outsideListener);
}

// Hides the list
ZaAutoCompleteListView.prototype._popdown = 
function() {
	this.setZIndex(Dwt.Z_HIDDEN);
	this.setVisible(false);
	ZaAutoCompleteListView._activeAcList = null;
	DwtEventManager.removeListener(DwtEvent.ONMOUSEDOWN, ZaAutoCompleteListView._outsideMouseDownListener);
	this.shell._setEventHdlrs([DwtEvent.ONMOUSEDOWN], true);
	this.shell.removeListener(DwtEvent.ONMOUSEDOWN, this._outsideListener);
}

// Selects a match by changing its CSS class
ZaAutoCompleteListView.prototype._setSelected =
function(sel) {
	DBG.println(AjxDebug.DBG3, "setting selected index to " + sel);
	var children = this.getHtmlElement().childNodes;
	if (!children) return;

	var len = children.length;
	for (var i = 0; i < len; i++) {
		var div = children[i];
		var curStyle = div.className;
		if (i == sel && curStyle != div[DwtListView._SELECTED_STYLE_CLASS]) {
			div.className = div[DwtListView._SELECTED_STYLE_CLASS];
		} else if (curStyle != div[DwtListView._STYLE_CLASS]) {
			div.className = div[DwtListView._STYLE_CLASS];
		}
	}
	this._selected = sel;
}

// Miscellaneous

// Adds a match to the internal list of matches
ZaAutoCompleteListView.prototype._append =
function(match) {
	this._matches.add(match);
}

// Clears the internal list of matches
ZaAutoCompleteListView.prototype._removeAll =
function() {
	this._matches.removeAll();
	var htmlElement = this.getHtmlElement();
	while (htmlElement.hasChildNodes())
		htmlElement.removeChild(htmlElement.firstChild);
}

// Returns the number of matches
ZaAutoCompleteListView.prototype.size =
function() {
	return this._matches.size();
}

// Returns the index of the currently selected match
ZaAutoCompleteListView.prototype._getSelectedIndex =
function() {
	return this._selected;
}

// Returns the currently selected match
ZaAutoCompleteListView.prototype._getSelected =
function() {
	return this._matches.get(this._selected);
}

/**
 * This method is called by this._dataLoaderObject when the data arrives
 * @param list - parsed array of data 
 */
ZaAutoCompleteListView.prototype.dataLoadedCallback = function (list) {
	if (list && list.length > 0) {
		var len = list.length;
		DBG.println(AjxDebug.DBG2, "found " + len + " match" + len > 1 ? "es" : "");
		for (var i = 0; i < len; i++) {
			var match = list[i];
			this._append(match);
		}
	} else {
		this._dataLoading = false;
		return;
	}
	
	this._set(); // populate the list view

	// show the list (unless we're doing completion)
	this.show(true, this._loc);
	this._dataLoading = false;
}
// Force the focus to the element
ZaAutoCompleteListView.prototype._focus =
function(htmlEl) {
	htmlEl.focus();
}

ZaAutoCompleteListView._outsideMouseDownListener =
function(ev) {
	var curList = ZaAutoCompleteListView._activeAcList;
    if (curList.getVisible()) {
		var obj = DwtControl.getTargetControl(ev);
		if (obj != curList) {
			curList.show(false);
			ev._stopPropagation = false;
			ev._returnValue = true;
		}
	}
};
