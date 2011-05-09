/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * @constructor
 * @class
 * OSelect1_XFormItem class -- lightning fast SELECT type widget
 * @author Owen Williams, Greg Solovyev
 * 
 * @private
 */
OSelect1_XFormItem = function(){ this._enabled = true; }
XFormItemFactory.createItemType("_OSELECT1_", "oselect1", OSelect1_XFormItem, Select1_XFormItem);

OSelect1_XFormItem._mouseWheelEventAttached = false;
OSelect1_XFormItem._mouseWheelCurrentSelect;
OSelect1_XFormItem._mouseWheelHideMenu = function() {
	//DBG.println(AjxDebug.DBG1, "OSelect1_XFormItem._mouseWheelCurrentSelect.hideMenu hiding menu time " +  (new Date()).getTime());
	OSelect1_XFormItem._mouseWheelCurrentSelect.hideMenu();
};

// override the default SELECT type
//XFormItemFactory.registerItemType("_SELECT1_", "select1", OSelect1_XFormItem)
OSelect1_XFormItem.prototype.focusable = false;
OSelect1_XFormItem.prototype.cssClass = "oselect";
OSelect1_XFormItem.prototype.multiple = false;
OSelect1_XFormItem.prototype.writeElementDiv = false;
OSelect1_XFormItem.prototype.width = "auto";
OSelect1_XFormItem.prototype.editable = false;
OSelect1_XFormItem.prototype.menuUp = false;
OSelect1_XFormItem.prototype.noteUp = false;
OSelect1_XFormItem.prototype.inputSize = 25;
OSelect1_XFormItem.prototype.bmolsnr = true;
OSelect1_XFormItem.prototype.nowrap = false;
OSelect1_XFormItem.prototype.labelWrap = true;
//TODO: get showing check working for the normal SELECT, requires:
//		* separate notion of hilited row (for mouseover) and selected row(s)
//		* teach select1 that more than one value may be selected (same as select)
//		* convert OSELECT_CHECK to just use showCheck?
//		* does &radic; work everywhere?  Use an image?
OSelect1_XFormItem.prototype.showCheck = false;
OSelect1_XFormItem.prototype.checkHTML = "&radic;";
OSelect1_XFormItem.MENU_DIR_DOWN=1;
OSelect1_XFormItem.MENU_DIR_UP=2;
OSelect1_XFormItem.MENU_DIR_UNKNOWN=0;
OSelect1_XFormItem.NOTE_HEIGHT=22;
OSelect1_XFormItem.prototype.menuDirection = OSelect1_XFormItem.MENU_DIR_UNKNOWN;
OSelect1_XFormItem.prototype.visibilityChecks = [XFormItem.prototype.hasReadPermission];
OSelect1_XFormItem.prototype.enableDisableChecks = [XFormItem.prototype.hasWritePermission];

//	methods
OSelect1_XFormItem.prototype.initFormItem = function () {
	// if we're dealing with an XFormChoices object...
	var choices = this.getChoices();
	if (choices == null || choices.constructor != XFormChoices) return;

	//	...set up to receive notification when its choices change
	var listener = new AjxListener(this, this.choicesChangeLsnr);
	choices.addListener(DwtEvent.XFORMS_CHOICES_CHANGED, listener);
}

OSelect1_XFormItem.prototype.updateElement = function (newValue) {
	if (this.choicesAreDirty()) this.updateChoicesHTML();
	// hack: if this item can display multiple values and there's a comma in the value
	//		assume it's a list of values
	if (this.getMultiple() && newValue != null && newValue.indexOf(",") > -1) {
		newValue = newValue.split(",");
		for (var i = 0; i < newValue.length; i++) {
			newValue[i] = this.getChoiceLabel(newValue[i]);
		}
	} else {
		newValue = this.getChoiceLabel(newValue);
	}
	if (newValue == null) newValue = "";
	
	var el = this.getDisplayElement();

	if (el) {
		if(this.getInheritedProperty("editable")) {
			if((!newValue || newValue=="") && el.value != newValue) {
				var i=0;
			}
			el.value = newValue;
			//DBG.println(AjxDebug.DBG1, AjxBuffer.concat(this.getId(),".value = ",newValue));
			if(this.getElement() && el.offsetWidth && this.getElement().style)
				this.getElement().style.width = el.offsetWidth + 20;
				
		} else {
			el.innerHTML = newValue;
		}
		//el.readOnly = !this.getInheritedProperty("editable");
	}
}

OSelect1_XFormItem.prototype.getShowCheck = function () {
	return this.cacheInheritedProperty("showCheck", "$showCheck");
}

OSelect1_XFormItem.prototype.getCheckHTML = function () {
	return this.cacheInheritedProperty("checkHTML", "$checkHTML");
}


OSelect1_XFormItem.prototype.getMenuElementId = function () {
	return "___OSELECT_MENU___";
}
OSelect1_XFormItem.prototype.getMenuElement = function () {
	var id = this.getMenuElementId();
	var el = this.getElement(id);
	if (el == null) {
		el = this.createElement(id, null, "div", "MENU CONTENTS");
	}
	return el;
}

OSelect1_XFormItem.prototype.getNoteElementId = function () {
	return "___OSELECT_NOTE___";
}
OSelect1_XFormItem.prototype.getNoteElement = function () {
	var id = this.getNoteElementId();
	var el = this.getElement(id);
	if (el == null) {
		el = this.createElement(id, null, "div", "NOTE CONTENTS");
	}
	return el;
}
OSelect1_XFormItem.prototype.setError = function (message, childError) {
	if(window.console && window.console.log) console.log("Showing error note");
	this.showNote(message, this.getErrorNoteCssClass());
	this.__errorState = XFormItem.ERROR_STATE_ERROR;
}

OSelect1_XFormItem.prototype.clearError = function () {
	if(window.console && window.console.log) console.log("Hiding error note");
	this.hideNote();
	this.__errorState = XFormItem.ERROR_STATE_VALID;
}

OSelect1_XFormItem.prototype.showMenu = function() {
	if(!this._enabled)
		return;
	if(window.console && window.console.log) console.log("Showing menu");
	this.hideInputTooltip();
	
	if (AjxEnv.isIE && !OSelect1_XFormItem._mouseWheelEventAttached) {
		var form = this.getForm();
		var formElement = form.getHtmlElement();
		if (formElement.attachEvent) {
			formElement.attachEvent("onmousewheel", OSelect1_XFormItem._mouseWheelHideMenu);
			OSelect1_XFormItem._mouseWheelCurrentSelect = this;
			OSelect1_XFormItem._mouseWheelEventAttached = true;
		}
	}

	var menu = this.getMenuElement();
	if (menu == null) return; 

	menu.className = this.getMenuCssClass();
	menu.innerHTML = this.getChoicesHTML();	
	var bounds;
	//bounds = this.getBounds(this.getElement().childNodes[0]);
	if(this.getInheritedProperty("editable")) {
		bounds = this.getBounds(this.getDisplayElement());
	} else {
		bounds = this.getBounds(this.getElement());
	}
	var w =DwtShell.getShell(window).getSize();
	var wh = w.y;
	var WINDOW_GUTTER = 8;
	menu.style.left = parseInt(bounds.left);
	menu.style.top = parseInt(bounds.top) + parseInt(bounds.height) - 1;
	var choices = this.getNormalizedChoices();
	if(choices && choices.values) {
		menu.style.overflow="hidden";
        var visibleChoices = choices.values.length - choices.totalInvisibleChoices;
        menu.style.height = (19*visibleChoices)+3;
    }

	var value = this.getInstanceValue();
	var getDisplayValueMethod = this.getDisplayValueMethod();
	if (getDisplayValueMethod) {
		value = getDisplayValueMethod.call(this, value);
	}
	var selectedItemNum = this.getChoiceNum(value);
	this.__currentHiliteItem = selectedItemNum;
	this.hiliteChoice(selectedItemNum);
	menu.style.zIndex = Dwt.Z_HIDDEN;
	menu.style.display = "block";


	var mBounds = this.getBounds(menu);
	var menuHeight = mBounds.height;
	var menuTop = mBounds.top;
	if (AjxEnv.isIE) {
		if(this.getInheritedProperty("editable")) {
			menu.style.width = parseInt(bounds.width)+4;
			menu.getElementsByTagName("table")[0].style.width = parseInt(bounds.width) - 1;			
		} else {
			menu.style.width = parseInt(bounds.width)+2;
			menu.getElementsByTagName("table")[0].style.width = parseInt(bounds.width) - 1;
		}
	} else {
		if(this.getInheritedProperty("editable")) {
			menu.style.width = parseInt(bounds.width)-5;
			menu.getElementsByTagName("table")[0].style.width = parseInt(bounds.width) - 6;
		} else {
			menu.style.width = parseInt(bounds.width)-3;
			menu.getElementsByTagName("table")[0].style.width = parseInt(bounds.width) - 4;			
		}
	}
	if(menuHeight + menuTop > wh - WINDOW_GUTTER) {
		//menu does not fit downwards - check if it fits upwards
		if((bounds.top - menuHeight) > WINDOW_GUTTER) {
			//yes - it fits upwards
			
			menu.style.top = bounds.top - menuHeight;			
			menu.getElementsByTagName("table")[0].className = this.getChoiceTableCssClass();				
		} else {
			/*
			* menu is too big to expand either up or down 
			* make it expand wherever ther is more space and make it scrollable
			*/
			if(bounds.top > ((wh - WINDOW_GUTTER*2)/2) ) {
				//expand upwards
				menu.style.height = parseInt(bounds.top) - WINDOW_GUTTER;												
				menu.style.top = WINDOW_GUTTER;
				this.menuDirection = OSelect1_XFormItem.MENU_DIR_UP;
			} else {
				//expand downwards
				menu.style.top	= 	parseInt(menu.style.top)+2;				
				menu.style.height = wh-WINDOW_GUTTER-parseInt(menu.style.top);								
				this.menuDirection = OSelect1_XFormItem.MENU_DIR_DOWN;
			}

            if(!AjxEnv.isIE){
                menu.style.height = parseInt(menu.style.height) - 11;
            }
			menu.style.overflow="auto";	
			menu.getElementsByTagName("table")[0].className = this.getChoiceScrollTableCssClass();
			menu.getElementsByTagName("table")[0].width="100%";
		} 
	} else {
		menu.getElementsByTagName("table")[0].className = this.getChoiceTableCssClass();
	}
	menu.style.zIndex = 1000000;
	if (this.$hideListener == null) {
		this.$hideListener = new AjxListener(this, this.oMouseUp);
	}
	
	if (this.$outsideMouseDownListener == null) {
		this.$outsideMouseDownListener = new AjxListener(this, this.onOutsideMouseDown);
	}

	AjxCore.addListener(window, DwtEvent.ONMOUSEUP, this.$hideListener);
	AjxCore.addListener(document.body, DwtEvent.ONMOUSEDOWN, this.$outsideMouseDownListener);
	Dwt.setHandler(this.getForm().getHtmlElement(), DwtEvent.ONMOUSEWHEEL, this.$outsideMouseDownListener);	
	DwtEventManager.addListener(DwtEvent.ONMOUSEDOWN, this.$outsideMouseDownListener);	
	this.menuUp = true;
}

OSelect1_XFormItem.prototype.choicesChangeLsnr = function () {
	this._choiceDisplayIsDirty = true;
	delete this.$normalizedChoices;
	if(this.menuUp)
		this.showMenu();
}

OSelect1_XFormItem.prototype.redrawChoices = function () {
	var menu = this.getMenuElement();
	if (menu == null) return; 

	menu.innerHTML = this.getChoicesHTML();		
}

OSelect1_XFormItem.prototype.hideMenu = function () {
	if(!this.menuUp) {
		return;
	}	
	if(window.console && window.console.log) console.log("Hiding menu");
	// hide the menu on a timer so we don't have to deal with wierd selection bugs
	setTimeout(this.getFormGlobalRef()+".getElement('" + this.getMenuElementId() + "').style.display = 'none'", 10);

	AjxCore.removeListener(window, DwtEvent.ONMOUSEUP, this.$hideListener);
	AjxCore.removeListener(document.body, DwtEvent.ONMOUSEDOWN, this.$outsideMouseDownListener);
	Dwt.clearHandler(this.getForm().getHtmlElement(), DwtEvent.ONMOUSEWHEEL);
	DwtEventManager.removeListener(DwtEvent.ONMOUSEDOWN, this.$outsideMouseDownListener);
	if (AjxEnv.isIE &&  OSelect1_XFormItem._mouseWheelEventAttached) {
		var form = this.getForm();
		var formElement = form.getHtmlElement();
		if (formElement.detachEvent) {
			if (window.event != null) window.event.cancelBubble = true;
			formElement.detachEvent("onmousewheel", OSelect1_XFormItem._mouseWheelHideMenu);
			OSelect1_XFormItem._mouseWheelEventAttached = false;
			OSelect1_XFormItem._mouseWheelCurrentSelect = null;
		}
	}
	this.menuUp = false;
	this.hideNote();
}

OSelect1_XFormItem.prototype.moveMenuY = function (y, shorten, lengthen) {
	var menu = this.getMenuElement();
	if (menu == null) return; 
	var mBounds = this.getBounds(menu);
	var menuHeight = mBounds.height;
	var menuTop = mBounds.top;
	var newTop = parseInt(menuTop)+parseInt(y);
	var newBotton = parseInt(newTop)+parseInt(menuHeight);
	menu.style.top = newTop;
	//shorten the menu
	if(shorten) {
		menu.style.height = parseInt(menu.style.height)-Math.abs(parseInt(y));
	} else if(lengthen) {
		menu.style.height = parseInt(menu.style.height)+Math.abs(parseInt(y));
	}
	
}

OSelect1_XFormItem.prototype.showNote = function(noteText, noteClass) {
	var note = this.getNoteElement();
	if(!note == null) return;
	note.className = noteClass ? noteClass : this.getNoteCssClass();
	note.innerHTML = noteText;	
	note.style.display = "block";
	var bounds;
	if(this.getInheritedProperty("editable")) {
		bounds = this.getBounds(this.getDisplayElement());
	} else {
		bounds = this.getBounds(this.getElement());
	}
	//note.style.width = menu.style.width;
	note.style.left = bounds.left;
	if(this.menuDirection == OSelect1_XFormItem.MENU_DIR_UP) {
		note.style.top = bounds.top + bounds.height;
		//this.moveMenuY((-1)*OSelect1_XFormItem.NOTE_HEIGHT,true,false);
	} else {
		note.style.top = bounds.top - OSelect1_XFormItem.NOTE_HEIGHT;
		//this.moveMenuY(OSelect1_XFormItem.NOTE_HEIGHT,true,false);
	}
	note.style.zIndex = 1000000;
	this.noteUp = true;
}

OSelect1_XFormItem.prototype.hideNote = function() {
	if(!this.noteUp) return;
	var note = this.getNoteElement();
	if(!note == null) return;
	note.innerHTML = "";	
	note.style.display = "none";	
	note.style.zIndex = Dwt.Z_HIDDEN;
	this.noteUp = false;
}

OSelect1_XFormItem.prototype.oMouseUp = function (ev) {
	// hide the menu on a timer so we don't have to deal with wierd selection bugs
	ev = ev || window.event;
	var found = false;
    if (ev) {
		// figure out if we are over the menu that is up
		var htmlEl = DwtUiEvent.getTarget(ev);
		var inputId = this.getId()+"_display";
		var arrowId = this.getId() + "_arrow_button";
	//	DBG.println(AjxDebug.DBG1, AjxBuffer.concat("oMouseUp; htmlEl.nodeName=",htmlEl.nodeName," htmlEl.localName = ", htmlEl.nodeName));
		//check if the user clicked on the scrollbar
			if(htmlEl.localName == "scrollbar" && ( (htmlEl.parentNode && htmlEl.parentNode.id=="___OSELECT_MENU___") || (htmlEl.id && htmlEl.id=="___OSELECT_MENU___"))) { 
				found = true;
			} else if (htmlEl.id && htmlEl.id == "___OSELECT_MENU___"){
				found = true;
			} else if (htmlEl.id && htmlEl.id == inputId){
				found = true;
			} else if (htmlEl.id && htmlEl.id == arrowId){
				found = true;
			}
	}


	if(!found) {
		//DBG.println(AjxDebug.DBG1, "OSelect1_XFormItem.oMouseUp hiding menu time " +  (new Date()).getTime());
		this.hideMenu();
	}	
	return true;
}

OSelect1_XFormItem.prototype.onOutsideMouseDown = function (ev) {
	// hide the menu on a timer so we don't have to deal with wierd selection bugs
	ev = ev || window.event;
	var found = false;
	var htmlEl;
    if (ev) {
		// figure out if we are over the menu that is up
		htmlEl = DwtUiEvent.getTarget(ev);
		var inputId = this.getId()+"_display";
		var arrowId = this.getId() + "_arrow_button";
		if(htmlEl && htmlEl.attributes && htmlEl.attributes.length) {
			var cnt = htmlEl.attributes.length;
			for(var i = 0; i < cnt; i++) {
				if(htmlEl.attributes[i].name == "itemnum") {
					this.onChoiceClick(htmlEl.attributes[i].value, ev);
					found = true;
					break;
				}
			}
		}
		if(!found) {
		//	DBG.println(AjxDebug.DBG1, AjxBuffer.concat("onOutsideMouseDown; htmlEl.nodeName=", htmlEl.nodeName," htmlEl.localName = ", htmlEl.localName, " htmlEl.id=", htmlEl.id));
			//check if the user clicked on the scrollbar or on the input or on the arrow
			if(htmlEl.localName == "scrollbar" && ( (htmlEl.parentNode && htmlEl.parentNode.id=="___OSELECT_MENU___") || (htmlEl.id && htmlEl.id=="___OSELECT_MENU___"))) { 
				found = true;
			} else if (htmlEl.id && htmlEl.id == "___OSELECT_MENU___"){
				found = true;
			} else if (htmlEl.id && htmlEl.id == inputId) {
				found = true;				
			} else if (htmlEl.id && htmlEl.id == arrowId) {
				found = true;
			}
		}
		
	}
	if(!found) {
		//DBG.println(AjxDebug.DBG1, "OSelect1_XFormItem.onOutsideMouseDown hiding menu htmlEl id = " + htmlEl.id + " time " +  (new Date()).getTime());
		this.hideMenu();
	}	
	return true;
}

OSelect1_XFormItem.prototype.getBounds = function(anElement, containerElement) {
	var myBounds = new Object();
	myBounds.left = 0;
	myBounds.top = 0;
	myBounds.width = anElement.clientWidth;
	myBounds.height = anElement.offsetHeight;

	if(!containerElement) {
		containerElement = AjxEnv.isIE ? anElement.document.body : anElement.ownerDocument.body;
	}

	// account for the scrollbars if necessary
	var hasScroll = (anElement.scrollLeft !== void 0);
	var trace = anElement;

	while(trace !=null && trace != containerElement) {
		myBounds.left += trace.offsetLeft;
		myBounds.top += trace.offsetTop;

		var nextEl = trace.offsetParent;
		while (hasScroll && (trace != nextEl)) {
			myBounds.left -= trace.scrollLeft;
			myBounds.top -= trace.scrollTop;
			trace = AjxEnv.isIE ? nextEl : trace.parentNode;
		}
		trace = nextEl;
	}
	return myBounds;
};



// TAKE DIRECTLY FROM DWT_SELECT
OSelect1_XFormItem.prototype.onChoiceOver = function (itemNum, event) {
	if (this.__currentHiliteItem != null) this.dehiliteChoice(this.__currentHiliteItem);
	this.hiliteChoice(itemNum);
	this.__currentHiliteItem = itemNum;
}
OSelect1_XFormItem.prototype.onChoiceOut = function (itemNum, event) {
	if (this.__currentHiliteItem != null) this.dehiliteChoice(this.__currentHiliteItem);
	this.__currentHiliteItem = null;
}
OSelect1_XFormItem.prototype.onChoiceClick = function (itemNum, event) {
	this.choiceSelected(itemNum, false, event);
}

OSelect1_XFormItem.prototype.onChoiceDoubleClick = function (itemNum, event) {
	this.choiceSelected(itemNum, true, event);
}

OSelect1_XFormItem.prototype.onValueTyped = function(label, event) {	
	var value = this.getChoiceValue(label);
	if(window.console && window.console.log) console.log("onValueTyped called value: " + value);
	this.setValue(value, false, event);
}

OSelect1_XFormItem.prototype.onKeyUp = function(value, event) {

	if(event.keyCode==XFG.ARROW_UP) {
		if(!this.menuUp)
			this.showMenu();
		
		this.hilitePreviousChoice(event);
		this.isSelecting = true;
		return;
	} 
	
	if(event.keyCode==XFG.ARROW_DOWN) {
		if(!this.menuUp)
			this.showMenu();
			
		this.hiliteNextChoice(event);
		this.isSelecting = true;
		return;
	} 
		
	if(this.isSelecting && this.menuUp && event.keyCode==DwtKeyEvent.KEY_ENTER && this.__currentHiliteItem != null && this.__currentHiliteItem != undefined) {
		var value = this.getNormalizedValues()[this.__currentHiliteItem];
		if(value != null && value != undefined) {
//			DBG.println(AjxDebug.DBG1, "OSelect1_XFormItem.onKeyUp handled key code "+ event.keyCode +" char code " + (new Date()).getTime());
			this.setValue(value, true, event);
			this.hideMenu();
			return;
		}
	} 
	this.isSelecting = false;		
	var method = this.getKeyUpMethod();
	if(method)
		method.call(this, value, event);
}

OSelect1_XFormItem.prototype.getKeyUpMethod = function () {
	return this.cacheInheritedMethod("keyUp","$keyUp","elementValue, event");
}

OSelect1_XFormItem.prototype.choiceSelected = function (itemNum, clearOldValues, event) {
	this.onChoiceOut();
	//DBG.println(AjxDebug.DBG1, "OSelect1_XFormItem.choiceSelected hiding menu "+  (new Date()).getTime());
	this.hideMenu();
	var value = this.getNormalizedValues()[itemNum];
	var editable = this.getInheritedProperty("editable");
	if(editable) {
		this.getDisplayElement().value = this.getNormalizedLabels()[itemNum];
	}
	this.setValue(value, clearOldValues, event);
}

OSelect1_XFormItem.prototype.hiliteNextChoice = function() {
	var choices = this.getNormalizedChoices();
	if(!(choices && choices.values && choices.values.length>0)) {
		return;
	}
	
	if(this.__currentHiliteItem == null || this.__currentHiliteItem == undefined) {
		this.hiliteChoice(0);
	} else { 
		this.dehiliteChoice(this.__currentHiliteItem);
		if ((this.__currentHiliteItem+1) < choices.values.length) {
			this.__currentHiliteItem++;
		} else {
			this.__currentHiliteItem = 0;
		}
		this.hiliteChoice(this.__currentHiliteItem);
	}
}
	
OSelect1_XFormItem.prototype.hilitePreviousChoice = function() {
	var choices = this.getNormalizedChoices();
	if(!(choices && choices.values && choices.values.length>0)) {
		return;
	}
	
	if(this.__currentHiliteItem == null || this.__currentHiliteItem == undefined) {
		this.hiliteChoice(0);
	} else { 
		this.dehiliteChoice(this.__currentHiliteItem);
		
		if ((this.__currentHiliteItem-1) > -1) {
			this.__currentHiliteItem--;
		} else {
			this.__currentHiliteItem = choices.values.length-1;
		}
		this.hiliteChoice(this.__currentHiliteItem);
	}
}	


OSelect1_XFormItem.prototype.setValue = function (newValue, clearOldValues, event) {
	if(window.console && window.console.log) console.log("setValue called + " + newValue);
	var method = this.getElementChangedMethod();
	method.call(this, newValue, this.getInstanceValue(), event);
}

OSelect1_XFormItem.prototype.hiliteChoice = function (itemNum) {
	this.setChoiceCssClass(itemNum, this.getChoiceSelectedCssClass());
	if (this.getShowCheck() == true) {
		var els = this.getChoiceElements(itemNum);
		if (els) els[0].innerHTML = this.getCheckHTML();
	}
}

OSelect1_XFormItem.prototype.displayMouseOver = function () {
	if(!this._enabled)
		return;
}

OSelect1_XFormItem.prototype.displayMouseOut = function () {
	if(!this._enabled)
		return;
}

OSelect1_XFormItem.prototype.displayMouseDown = function () {
	if(!this._enabled)
		return;
}

OSelect1_XFormItem.prototype.dehiliteChoice = function(itemNum) {
	this.setChoiceCssClass(itemNum, this.getChoiceCssClass());
	if (this.getShowCheck() == true) {
		var els = this.getChoiceElements(itemNum);
		if (els) els[0].innerHTML = "&nbsp;";
	}
}


OSelect1_XFormItem.prototype.clearAllHilites = function () {
	for (var i = 0; i < this._normalizedValues.length; i++) {
		this.dehiliteChoice(i);
	}
}



OSelect1_XFormItem.prototype.setChoiceCssClass = function (itemNum, cssClass) {
	var els = this.getChoiceElements(itemNum);
	if (els) {
		els.className = cssClass;
	}
}

OSelect1_XFormItem.prototype.getArrowElement = function () {
	return this.getForm().getElement(this.getId() + "_arrow_button");
}

OSelect1_XFormItem.prototype.getDisplayElement = function () {
	return this.getElement(this.getId() + "_display");
}


OSelect1_XFormItem.prototype.getItemNumFromEvent = function (event) {
	var target = event.target || event.src;
	while (target) {
		if (target.id) {
			var itemNum = parseInt(target.id);
			if (isNaN(itemNum)) return -1;
			return itemNum;
		}
		target = target.parentNode;
	}
	return -1;
}

OSelect1_XFormItem.prototype.getChoiceElements = function (itemNum) {
	if (itemNum == null || itemNum == -1) return null;
	try {
		return this.getForm().getElement([this.getId(), "_choice_",itemNum].join(""));
	} catch (e) {
		return null;
	}
}


OSelect1_XFormItem.prototype.outputHTML = function (HTMLoutput) {
	var id = this.getId();
	var ref = this.getFormGlobalRef() + ".getItemById('"+ id + "')";	
	var inputHtml;
	var editable = this.getInheritedProperty("editable");
	if(editable) {
		var inputSize = this.getInheritedProperty("inputSize");		
		inputHtml = ["<input type=text id=", id, "_display class=", this.getDisplayCssClass(), " value='VALUE' ", 
					" onchange=\"",ref, ".onValueTyped(this.value, event||window.event)\"",
					" onmouseup=\"", ref, ".showMenu(this)\"",
					" onkeyup=\"",ref, ".onKeyUp(this.value, event||window.event)\"", "size=",inputSize,
					">"].join("");
	}
	
	if (this.getWidth() == "auto" && !editable) {
		var element = this.getElement("tempDiv");
		if(!element) 
			element = this.createElement("tempDiv", null, "div", "MENU CONTENTS");
		element.style.left = -1000;
		element.style.top = -1000;
		element.className = this.getMenuCssClass();
		element.innerHTML = this.getChoicesHTML();
		this._width = element.offsetWidth+20;
		element.innerHTML = "";
	}

	
	if(editable) {
		HTMLoutput.append(
			"<div id=", id, this.getCssString(),
				" onclick=\"", this.getFormGlobalRef(), ".getItemById('",this.getId(),"').showMenu(this)\"",
				" onselectstart=\"return false\"",
				">",
				"<table ", this.getTableCssString(), ">", 
					"<tr><td width=100%>",inputHtml,"</td>",
						"<td>", this.getArrowButtonHTML(),"</td>", 
					"</tr>", 
				"</table>", 
			"</div>"
		);
	} else {
		HTMLoutput.append(
			"<div id=", id, this.getCssString(),
				" onclick=\"", this.getFormGlobalRef(), ".getItemById('",this.getId(),"').showMenu(this)\"",
				" onselectstart=\"return false\"",
				"><table ", this.getTableCssString(), ">",
					"<tr><td width=100%><div id=", id, "_display class=", this.getDisplayCssClass(), ">VALUE</div></td>",
						"<td>", this.getArrowButtonHTML(),"</td>", 
					"</tr>", 
				"</table>", 
			"</div>"
		);	
	}
}

OSelect1_XFormItem.prototype.getArrowButtonHTML = function () {
	var ref = this.getFormGlobalRef() + ".getItemById('"+ this.getId()+ "')";
	return AjxBuffer.concat("<div id=", this.getId(), "_arrow_button",
	 " onmouseover=\"", ref, ".displayMouseOver();\"",
 	 " onmouseout=\"", ref, ".displayMouseOut();\"",
 	 " onmousedown=\"", ref, ".displayMouseDown();\"", 	 
 	 ">", AjxImg.getImageHtml("SelectPullDownArrow"), "</div>");
}

OSelect1_XFormItem.prototype.getTableCssClass = function () {
	return this.cssClass + "_table";
}
OSelect1_XFormItem.prototype.getDisplayCssClass = function () {
	return this.cssClass + "_display";
}

OSelect1_XFormItem.prototype.getMenuCssClass = function () {
	return this.cssClass + "_menu";
}
OSelect1_XFormItem.prototype.getChoiceTableCssClass = function () {
	return this.cssClass + "_choice_table";
}
OSelect1_XFormItem.prototype.getChoiceScrollTableCssClass = function () {
	return this.cssClass + "_choice_table_scrolled";
}

OSelect1_XFormItem.prototype.getChoiceCssClass = function () {
	return this.cssClass + "_choice";
}
OSelect1_XFormItem.prototype.getChoiceSelectedCssClass = function () {
	return this.cssClass + "_choice_selected";
}

OSelect1_XFormItem.prototype.getNoteCssClass = function () {
	return this.cssClass + "_note";
}

OSelect1_XFormItem.prototype.getErrorNoteCssClass = function () {
	return this.cssClass + "_error_note";
}

OSelect1_XFormItem.prototype.outputChoicesHTMLStart = function(html) {
	html.append("<table cellspacing=0 cellpadding=0 id=", this.getId(),"_menu_table class=", this.getChoiceTableCssClass(), ">");
	
}
OSelect1_XFormItem.prototype.outputChoicesHTMLEnd = function(html) {
	html.append("</table>");
}

OSelect1_XFormItem.prototype.getChoiceHTML = function (itemNum, value, label, cssClass) {
	var ref = this.getFormGlobalRef() + ".getItemById('"+ this.getId()+ "')";
	//try DIVs
	return AjxBuffer.concat("<tr><td><div id=\"", this.getId(), "_choice_", itemNum, "\" ", "class=", cssClass, 
			" onmouseover=\"",ref, ".onChoiceOver(", itemNum,", event||window.event)\"",
			" onmouseout=\"",ref, ".onChoiceOut(", itemNum,", event||window.event)\"",
			" onclick=\"",ref, ".onChoiceClick(", itemNum,", event||window.event)\"",
			" itemnum = '", itemNum, "'",">",label,	"</div></td></tr>");
	
}


// set up how disabling works for this item type
OSelect1_XFormItem.prototype.setElementEnabled = function(enabled) {
	this._enabled = enabled;
	var table = this.getForm().getElement(this.getId()).getElementsByTagName("table")[0];
	if(enabled) {
		this.getDisplayElement().className = this.getDisplayCssClass();
		var el = this.getArrowElement();
		if(el)
			AjxImg.setImage(el, "SelectPullDownArrow");
			
		this.getForm().getElement(this.getId()).className = this.cssClass;
		table.className = this.getTableCssClass();
		if(this.getInheritedProperty("editable")) {
			this.getDisplayElement().disabled=false;
		}
	} else {
		this.getDisplayElement().className = this.getDisplayCssClass() + "_disabled";
		var el = this.getArrowElement();
		/*if(el)
			AjxImg.setImage(el, "SelectPullDownArrowDis");
		*/	
		this.getForm().getElement(this.getId()).className = this.cssClass + "_disabled";
		table.className = this.getTableCssClass()+"_disabled";
		if(this.getInheritedProperty("editable")) {
			this.getDisplayElement().disabled=true;
		}
	}
}

//
//	OSelect class -- lightning fast SELECT type widget
//
OSelect_XFormItem = function() {}
XFormItemFactory.createItemType("_OSELECT_", "oselect", OSelect_XFormItem, OSelect1_XFormItem);

OSelect_XFormItem.prototype.focusable = false;
OSelect_XFormItem.prototype.multiple = true;
OSelect_XFormItem.prototype.writeElementDiv = true;
OSelect_XFormItem.prototype.overflow = "auto";
OSelect_XFormItem.prototype.cssStyle = "border:2px inset gray;";
OSelect_XFormItem.prototype.showCheck = false;

OSelect_XFormItem.prototype.outputHTML = function(html) {
	var it = this.getChoicesHTML();
	html.append(it);
}

OSelect_XFormItem.prototype.choicesChangeLsnr = function () {
	this._choiceDisplayIsDirty = true;
	delete this.$normalizedChoices;
	var element = this.getElement();
	if(element)
		element.innerHTML = this.getChoicesHTML();	
}

OSelect_XFormItem.prototype.outputChoicesHTMLStart = function(html) {
	html.append("<table id=", this.getId(),"_menu_table width=100% cellspacing=2 cellpadding=0>");
}
OSelect_XFormItem.prototype.outputChoicesHTMLEnd = function(html) {
	html.append("</table>");
}


OSelect_XFormItem.prototype.getMenuElementId = function () {
	return this.getId();
}

OSelect_XFormItem.prototype.updateElement = function (values) {
	var element = this.getElement();
	element.innerHTML = this.getChoicesHTML();

	if (values == null) return;	
	if(this.getMultiple()) {
		if (typeof values == "string") values = values.split(",");
		for (var i = 0; i < values.length; i++) {
			var itemNum = this.getChoiceNum(values[i]);
			if (itemNum != -1) this.hiliteChoice(itemNum);
		}
	} else {
		var itemNum = this.getChoiceNum(values);
		if (itemNum != -1) this.hiliteChoice(itemNum);
	}

    //updateEnabledDisabled() should run after the element is created.
    //OSelect_XFormItem updateElement will redraw the elements.
    //The redraw of the elements will screw the enable disable state of the element
    //So we update the enable disable state after the redraw.      
    this.updateEnabledDisabled();
}

OSelect_XFormItem.prototype.onChoiceOver = function (itemNum) {}
OSelect_XFormItem.prototype.onChoiceOut = function (itemNum) {}

OSelect_XFormItem.prototype.onChoiceClick = function (itemNum, event) {
	event = event || window.event;
	var clearOthers = true;
	var includeIntermediates = false;
	
	if(this.getMultiple()) {
		clearOthers = false;
		if (event.shiftKey) {
			includeIntermediates = true;
		}
	}
	
	this.choiceSelected(itemNum, clearOthers, includeIntermediates, event);
};

OSelect_XFormItem.prototype.choiceSelected = function (itemNum, clearOldValues, includeIntermediates, event) {
	if (includeIntermediates){
		this._selectionCursor = itemNum;
		if (this._selectionAnchor == null) {
			this._selectionAnchor = itemNum;
		}
	} else {
		this._selectionAnchor = itemNum;
		this._selectionCursor = itemNum;
	}

	var value = this.getNormalizedValues()[itemNum];
	this.setValue(value, clearOldValues, includeIntermediates, event);
}

OSelect_XFormItem.prototype.setValue = function (newValue, clearOldValues, includeIntermediates, event) {

	var newValues;
	if (clearOldValues) {
		if(this.getMultiple()) {
			if(newValue instanceof Array)
				newValues = newValue;
			else
				newValues = [newValue];
		} else {
			newValues = newValue;
		}
	} else {
		if (includeIntermediates) {
			newValues = [];
			var vals = this.getNormalizedValues();
			var start = this._selectionCursor;
			var dist = this._selectionAnchor - this._selectionCursor;
			if (dist < 0 ) {
				dist = this._selectionCursor - this._selectionAnchor;
				start = this._selectionAnchor;
			}
			for (var i = start; i <= start + dist; ++i) {
				newValues.push(vals[i]);
			}
		} else {
			var oldValues = this.getInstanceValue();
			if(typeof oldValues == "string") {
				newValues = oldValues;
			} else if(typeof oldValues =="object" || oldValues instanceof Array) {
				newValues = [];
				for(var a in oldValues) {
					newValues[a] = oldValues[a];
				}
			}
			if(newValues) {
				if (typeof newValues == "string") {
					if (newValues == "") 	
						newValues = [];
					else
						newValues = newValues.split(",");
				}
			} else {
				newValues = new Array();			
			}			
			
			var found = false;
			var i;
			for (i = 0; i < newValues.length; i++) {
				if (newValues[i] == newValue) {
					found = true;
					break;
				}
			}
			
			if (found) {
				newValues.splice(i, 1);
			} else {
				newValues.push(newValue);
			}
		}
		if(!newValues || (newValues.length == 1 && newValues[0] == "")) {
			newValues = []
		} 
		// if we have a modelItem which is a LIST type
		//	convert the output to the propert outputType
		var modelItem = this.getModelItem();
		if (modelItem && modelItem.getOutputType) {
			if (modelItem.getOutputType() == _STRING_) {
				newValues = newValues.join(modelItem.getItemDelimiter());
			}
		} else {
			// otherwise assume we should convert it to a comma-separated string
			newValues = newValues.join(",");
		}
	}
	this.getForm().itemChanged(this, newValues, event);
}

OSelect_XFormItem.prototype.setElementEnabled = function (enabled) {
	var choices = this.getNormalizedChoices();
	if(!choices)
		return;
	
	var values = choices.values;
	if(!values)
		return;
		
	var cnt = values.length;
	for(var i=0; i < cnt; i ++) {
		var chkbx = this.getElement(this.getId() + "_choiceitem_" + i);	
		if(chkbx) {
			if(enabled) {
				chkbx.className = this.getChoiceCssClass();
				chkbx.disabled = false;
			} else {
				chkbx.className = this.getChoiceCssClass() + "_disabled";
				chkbx.disabled = true;
			}
		} 
	}
};




OSelect_Check_XFormItem = function() {}
XFormItemFactory.createItemType("_OSELECT_CHECK_", "oselect_check", OSelect_Check_XFormItem, OSelect_XFormItem)
OSelect_Check_XFormItem.prototype.cssClass = "oselect_check";
OSelect_Check_XFormItem.prototype.getChoiceHTML = function (itemNum, value, label, cssClass) {
	var ref = this.getFormGlobalRef() + ".getItemById('"+ this.getId()+ "')";
	var id = this.getId();

/*
 	// Bug 44925
 	// The checkbox label is always on activated status. Since it is not a standard XForm component,
 	// it is not controlled by enableDisableChecks property.

	return AjxBuffer.concat(
		"<tr><td class=", cssClass, 
			" onmouseover=\"",ref, ".onChoiceOver(", itemNum,", event||window.event)\"",
			" onmouseout=\"",ref, ".onChoiceOut(", itemNum,", event||window.event)\"",
			" onclick=\"",ref, ".onChoiceClick(", itemNum,", event||window.event)\"",
			" ondblclick=\"",ref, ".onChoiceDoubleClick(", itemNum,", event||window.event)\"",
		">",
		"<table cellspacing=0 cellpadding=0><tr><td><input type=checkbox id='",id,"_choiceitem_",itemNum,"'></td><td>",
				label,                     //<--  the label is always on activated status
		"</td></tr></table></td></tr>"
	);
*/

	// The bugfixing for bug 44925
	// By checking the __isEnabled property via getIsEnabled(), the fixing can make the label working with
	// checkbox under same property: 
	//     1) both should be in grey(disabled) when the item is disabled, vise versa;
	//     2) both should be removed all the event handlers on element when it is disabled, vise versa.

        return AjxBuffer.concat(
                "<tr><td class=", cssClass,
                        (this.getIsEnabled())?(" onmouseover=\"" + ref + ".onChoiceOver(" + itemNum + ", event||window.event)\""):"",
                        (this.getIsEnabled())?(" onmouseout=\"" + ref +  ".onChoiceOut(" + itemNum + ", event||window.event)\""):"",
                        (this.getIsEnabled())?(" onclick=\"" + ref + ".onChoiceClick(" + itemNum + ", event||window.event)\""):"",
                        (this.getIsEnabled())?(" ondblclick=\"" + ref + ".onChoiceDoubleClick(" + itemNum + ", event||window.event)\""):"",
                ">",
                "<table cellspacing=0 cellpadding=0><tr><td><input type=checkbox id='",id,"_choiceitem_",itemNum,"'></td><td>",
                                (!this.getIsEnabled())?("<font color=\"#808080\">"):"", label,(!this.getIsEnabled())?("</font>"):"",
                "</td></tr></table></td></tr>"
        );

}


OSelect_Check_XFormItem.prototype.hiliteChoice = function (itemNum) {
	var chEl = this.getChoiceElements(itemNum);
	if(chEl) {
		var el = chEl[0];
		el.className = this.getChoiceSelectedCssClass();
	
		var checks = el.getElementsByTagName("input");
		if (checks) {
			checks[0].checked = true;
		}
	}
}

OSelect_Check_XFormItem.prototype.dehiliteChoice = function(itemNum) {
	var chEl = this.getChoiceElements(itemNum);
	if(chEl) {
		var el = chEl[0];
		el.className = this.getChoiceCssClass();

		var checks = el.getElementsByTagName("input");
		if (checks) {
			checks[0].checked = false;
		}
	}
}

OSelect_Check_XFormItem.prototype.getChoiceElements = function (itemNum) {
	if (itemNum == null || itemNum == -1) return null;
	try {
		return this.getForm().getElement(this.getId() + "_menu_table").rows[itemNum].getElementsByTagName("td");
	} catch (e) {
		return null;
	}
}

OSelect_Check_XFormItem.prototype.selectAll = function (ev) {
	var newValues = [];
	if(this.$normalizedChoices && this.$normalizedChoices.values) {
		var choices = this.$normalizedChoices.values;
		var cnt = choices.length;
		for(var i =0; i < cnt; i ++) {
			newValues.push(choices[i]);
		}
	}
	this.setValue(newValues,true,false,ev);
}

OSelect_Check_XFormItem.prototype.deselectAll = function (ev) {
	this.getForm().itemChanged(this, [], ev);
}

OSelect_Check_XFormItem.prototype.updateElement = function (values) {
	var element = this.getElement();
	element.innerHTML = this.getChoicesHTML();
	this.clearAllHilites();
	if (values) {	
		if(this.getMultiple()) {
			if (typeof values == "string") values = values.split(",");
			for (var i = 0; i < values.length; i++) {
				var itemNum = this.getChoiceNum(values[i]);
				if (itemNum != -1) this.hiliteChoice(itemNum);
			}
		} else {
			var itemNum = this.getChoiceNum(values);
			if (itemNum != -1) this.hiliteChoice(itemNum);
		}
	}
    this.updateEnabledDisabled();
}

OSelect_Check_XFormItem.prototype.clearAllHilites = function () {
	var choices = this.getNormalizedChoices();
	var cnt;
	if(choices.values) {
		cnt = choices.values.length;
		for(var i=0; i< cnt; i++) {
			this.dehiliteChoice(i);		
		}
	}
}

OSelect_DblCheck_XFormItem = function() {}
XFormItemFactory.createItemType("_OSELECT_DBL_CHECK_", "oselect_dbl_check", OSelect_DblCheck_XFormItem, OSelect_Check_XFormItem)

OSelect_DblCheck_XFormItem.prototype.onSubChoiceOver = function (itemNum) {}
OSelect_DblCheck_XFormItem.prototype.onSubChoiceOut = function (itemNum) {}

OSelect_DblCheck_XFormItem.prototype.onSubChoiceClick = function (itemNum, event) {
	event = event || window.event;
	var clearOthers = true;
	var includeIntermediates = false;
	
	if(this.getMultiple()) {
		clearOthers = false;
		if (event.shiftKey) {
			includeIntermediates = true;
		}
	}
	
	this.subChoiceSelected(itemNum, clearOthers, includeIntermediates, event);
};

OSelect_DblCheck_XFormItem.prototype.subChoiceSelected = function (itemNum, clearOldValues, includeIntermediates, event) {
	if (includeIntermediates){
		this._subSelectionCursor = itemNum;
		if (this._subSelectionAnchor == null) {
			this._subSselectionAnchor = itemNum;
		}
	} else {
		this._subSelectionAnchor = itemNum;
		this._subSelectionCursor = itemNum;
	}

	var value = this.getNormalizedValues()[itemNum];
	this.setSubValue(value, clearOldValues, includeIntermediates, event);
}

OSelect_DblCheck_XFormItem.prototype.setSubValue = function (newValue, clearOldValues, includeIntermediates, event) {
	var newValues;

	if (clearOldValues) {
		if(this.getMultiple()) {
			if(newValue instanceof Array)
				newValues = newValue;
			else
				newValues = [newValue];
		} else {
			newValues = newValue;
		}
	} else {
		if (includeIntermediates) {
			newValues = [];
			var vals = this.getNormalizedValues();
			var start = this._subSelectionCursor;
			var dist = this._subSelectionAnchor - this._subSelectionCursor;
			if (dist < 0 ) {
				dist = this._subSelectionCursor - this._subSelectionAnchor;
				start = this._subSelectionAnchor;
			}
			for (var i = start; i <= start + dist; ++i) {
				newValues.push(vals[i]);
			}
		} else {
			var oldValues = this.getInstanceValue(this.getInheritedProperty("subRef"));
			if(typeof oldValues == "string") {
				newValues = new String(oldValues);
			} else if(typeof oldValues =="object" || oldValues instanceof Array) {
				newValues = [];
				for(var a in oldValues) {
					newValues[a] = oldValues[a];
				}
			}
			if(newValues) {
				if (typeof newValues == "string") {
					if (newValues == "") 	
						newValues = [];
					else
						newValues = newValues.split(",");
				}
			} else {
				newValues = new Array();			
			}			
			
			var found = false;
			for (var i = 0; i < newValues.length; i++) {
				if (newValues[i] == newValue) {
					found = true;
					break;
				}
			}
			
			if (found) {
				newValues.splice(i, 1);
			} else {
				newValues.push(newValue);
			}
		}
		if(!newValues || (newValues.length == 1 && newValues[0] == "")) {
			newValues = []
		} 
		// if we have a modelItem which is a LIST type
		//	convert the output to the propert outputType
		var modelItem = this.getSubModelItem();
		if (modelItem && modelItem.getOutputType) {
			if (modelItem.getOutputType() == _STRING_) {
				newValues = newValues.join(modelItem.getItemDelimiter());
			}
		} else {
			// otherwise assume we should convert it to a comma-separated string
			newValues = newValues.join(",");
		}
	}
	this.getForm().subItemChanged(this, newValues, event);
}

OSelect_DblCheck_XFormItem.prototype.getSubLabel = function () {
	return this.getInheritedProperty("subLabel");	
}

OSelect_DblCheck_XFormItem.prototype.getChoiceHTML = function (itemNum, value, label, cssClass) {
	var ref = this.getFormGlobalRef() + ".getItemById('"+ this.getId()+ "')";
	var id = this.getId();
	var subLabel = this.getSubLabel();
	return AjxBuffer.concat(
		"<tr><td class=", cssClass, 
			" onmouseover=\"",ref, ".onChoiceOver(", itemNum,", event||window.event)\"",
			" onmouseout=\"",ref, ".onChoiceOut(", itemNum,", event||window.event)\"",
			" onclick=\"",ref, ".onChoiceClick(", itemNum,", event||window.event)\"",
			" ondblclick=\"",ref, ".onChoiceDoubleClick(", itemNum,", event||window.event)\">",
				"<table cellspacing=0 cellpadding=0><tr><td><input type=checkbox id='",id,"_choiceitem_",itemNum,"'></td><td>",
				label,
				"</td></tr></table>",
			"</td><td class=",cssClass,
				" onmouseover=\"",ref,".onSubChoiceOver(", itemNum, ", event||window.event)\"",
				" onmouseout=\"",ref, ".onSubChoiceOut(", itemNum, ", event||window.event)\"",
				" onclick=\"",ref, ".onSubChoiceClick(", itemNum, ", event||window.event)\"",
				" ondblclick=\"",ref, ".onSubChoiceDoubleClick(", itemNum, ".event||window.event)\">",
					"<table cellspacing=0 cellpadding=0><tr><td><input type=checkbox id='",id,"_subchoiceitem_",itemNum,"'></td><td>",
				subLabel,
				"</td></tr></table>",
		"</td></tr>"
	);
}

OSelect_DblCheck_XFormItem.prototype.hiliteChoice = function (itemNum) {
	var chEl = this.getChoiceElements(itemNum);
	if(chEl) {
		var el = chEl[0];
		el.className = this.getChoiceSelectedCssClass();
	
		var checks = el.getElementsByTagName("input");
		if (checks) {
			checks[0].checked = true;
			this.enableSubChoice(itemNum);
		}
	}
}

OSelect_DblCheck_XFormItem.prototype.dehiliteChoice = function(itemNum) {
	var chEl = this.getChoiceElements(itemNum);
	if(chEl) {
		var el = chEl[0];
		el.className = this.getChoiceCssClass();

		var checks = el.getElementsByTagName("input");
		if (checks) {
			checks[0].checked = false;
			this.dehiliteSubChoice(itemNum);
			this.disableSubChoice(itemNum);
		}
	}
}

OSelect_DblCheck_XFormItem.prototype.hiliteSubChoice = function (itemNum) {
	var chEl = this.getChoiceElements(itemNum);
	if(chEl) {
		var el = chEl[3];
		el.className = this.getChoiceSelectedCssClass();

		var checks = el.getElementsByTagName("input");
		if (checks) {
			checks[0].checked = true;
		}
	}
}

OSelect_DblCheck_XFormItem.prototype.dehiliteSubChoice = function(itemNum) {
	var chEl = this.getChoiceElements(itemNum);
	if(chEl) {
		var el = chEl[3];
		el.className = this.getChoiceCssClass();

		var checks = el.getElementsByTagName("input");
		if (checks) {
			checks[0].checked = false;
		}
	}
}

OSelect_DblCheck_XFormItem.prototype.disableSubChoice = function (itemNum) {
	var chEl = this.getChoiceElements(itemNum);
	if(chEl) {
		var el = chEl[3];
		el.className = this.getChoiceCssClass() + "_disabled";

		var checks = el.getElementsByTagName("input");
		if (checks) {
			checks[0].disabled = true;
		}
	}
}

OSelect_DblCheck_XFormItem.prototype.enableSubChoice = function (itemNum) {
	var chEl = this.getChoiceElements(itemNum);
	if(chEl) {
		var el = chEl[3];
		el.className = this.getChoiceCssClass();

		var checks = el.getElementsByTagName("input");
		if (checks) {
			checks[0].disabled = false;
		}
	}
}


OSelect_DblCheck_XFormItem.prototype.updateElement = function () {
	var element = this.getElement();
	element.innerHTML = this.getChoicesHTML();
	var values = this.getInstanceValue();
	this.clearAllHilites();
	if (values) {	
		if(this.getMultiple()) {
			if (typeof values == "string") values = values.split(",");
			for (var i = 0; i < values.length; i++) {
				var itemNum = this.getChoiceNum(values[i]);
				if (itemNum != -1) this.hiliteChoice(itemNum);
			}
		} else {
			var itemNum = this.getChoiceNum(values);
			if (itemNum != -1) this.hiliteChoice(itemNum);
		}
	}
	
	var subValues = this.getInstanceValue(this.getInheritedProperty("subRef"));
	if (subValues) {	
		if(this.getMultiple()) {
			if (typeof subValues == "string") subValues = subValues.split(",");
			for (var i = 0; i < subValues.length; i++) {
				var itemNum = this.getChoiceNum(subValues[i]);
				if (itemNum != -1) this.hiliteSubChoice(itemNum);
			}
		} else {
			var itemNum = this.getChoiceNum(values);
			if (itemNum != -1) this.hiliteSubChoice(itemNum);
		}
	}	
    this.updateEnabledDisabled();
}

OSelect_DblCheck_XFormItem.prototype.onSubChoiceDoubleClick = function (itemNum, event) {
	this.subChoiceSelected(itemNum, true, event);
}

OSelect_DblCheck_XFormItem.prototype.setElementEnabled = function (enabled) {
	var choices = this.getNormalizedChoices();
	if(!choices)
		return;
	
	var values = choices.values;
	if(!values)
		return;
		
	var cnt = values.length;
	for(var i=0; i < cnt; i ++) {
		var chkbx = this.getElement(this.getId() + "_choiceitem_" + i);
		var chkbxSub = this.getElement(this.getId() + "_subchoiceitem_" + i);	
		if(chkbx && chkbxSub) {
			if(enabled) {
				chkbx.className = this.getChoiceCssClass();
				chkbx.disabled = false;
//				chkbxSub.className = this.getChoiceCssClass();
//				chkbxSub.disabled = false;					
			} else {
				chkbx.className = this.getChoiceCssClass() + "_disabled";
				chkbx.disabled = true;
				chkbxSub.className = this.getChoiceCssClass() + "_disabled";
				chkbxSub.disabled = true;				
			}
		} 
	}
};

OSelect_DblCheck_XFormItem.prototype.deselectAll = function (ev) {
	this.getForm().subItemChanged(this, [], ev);
	this.getForm().itemChanged(this, [], ev);
}
