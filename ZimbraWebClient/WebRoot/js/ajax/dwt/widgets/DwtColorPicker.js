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
 * Creates a color picker displaying "Web safe" colors.
 * @constructor
 * @class
 * Instances of this class may be
 * used with {@link DwtMenu} to create a {@link DwtColorPicker} menu. Clicking on a color cell generates a
 * DwtSelectionEvent the detail attribute of which contains the color string associated
 * the cell on which the user clicked
 *
 *
 * @author Ross Dargahi
 * 
 * @param {DwtComposite}	parent		the parent widget
 * @param {string}	className	a CSS class
 * @param {constant}	posStyle	the positioning style
 * @param {string}	noFillLabel			the no-fill label
 * @param {boolean}	allowColorInput		if <code>true</code>, allow a text field to allow user to input their customized RGB value
 * 
 * @extends		DwtControl
 */
DwtColorPicker = function(parent, className, posStyle, noFillLabel, allowColorInput) {
	if (arguments.length == 0) return;
	className = className || "DwtColorPicker";
	DwtControl.call(this, {parent:parent, className:className, posStyle:posStyle});

	this._noFillLabel = noFillLabel;
    this._allowColorInput = allowColorInput ;
    this._createColorTable();
	this._registerEventHdlrs();
	//MOW:  this.setCursor("default");
}

DwtColorPicker.prototype = new DwtControl;
DwtColorPicker.prototype.constructor = DwtColorPicker;

// RE to parse out components out of a "rgb(r, g, b);" string
DwtColorPicker._RGB_RE = /rgb\(([0-9]{1,3}), ([0-9]{1,3}), ([0-9]{1,3})\)/;
DwtColorPicker._HEX_RE = /\#([0-9FCfc]{2})([0-9FCfc]{2})([0-9FCfc]{2})/;
DwtColorPicker._HEX_COMPLETE_RE = /\#([0-9ABCDEFabcdef]{2})([0-9ABCDEFabcdef]{2})([0-9ABCDEFabcdef]{2})/;

// Public methods

DwtColorPicker.prototype.toString = 
function() {
	return "DwtColorPicker";
}

/**
 * Adds a listener to be notified when the button is pressed.
 *
 * @param {AjxListener}	listener	a listener
 */
DwtColorPicker.prototype.addSelectionListener = 
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
}

/**
 * Removes a selection listener.
 *
 * @param {AjxListener}	listener	a listener
 */
DwtColorPicker.prototype.removeSelectionListener = 
function(listener) { 
	this.removeListener(DwtEvent.SELECTION, listener);
}

DwtColorPicker.prototype.dispose = 
function () {
	if (this._disposed) { return; }
	DwtControl.prototype.dispose.call(this);
}

DwtColorPicker.prototype._registerEventHdlrs =
function() {
	var table = this.getHtmlElement().firstChild;
	var rows = table.rows;
	var numRows = rows.length;

	for (var i = 0; i < numRows; i++) {
		var cells = rows[i].cells;
		var numCells = cells.length
		for (var j = 0; j < numCells; j++) {
			var cell = cells[j];
            if (rows[i].className == "ColorInput") {
                if (cell.className == "ColorInputField") {
                   //no event handler
                }else if (cell.className == "SetInputColor") {
                    Dwt.setHandler(cell, DwtEvent.ONMOUSEDOWN, DwtColorPicker._mouseDownHdlr);
                    Dwt.setHandler(cell, DwtEvent.ONMOUSEUP, DwtColorPicker._mouseUpHdlr);
                }
            }else{
                Dwt.setHandler(cell, DwtEvent.ONMOUSEDOWN, DwtColorPicker._mouseDownHdlr);
                Dwt.setHandler(cell, DwtEvent.ONMOUSEUP, DwtColorPicker._mouseUpHdlr);
                if (!AjxEnv.isIE) {
                    Dwt.setHandler(cell, DwtEvent.ONMOUSEOVER, DwtColorPicker._mouseOverHdlr);
                    Dwt.setHandler(cell, DwtEvent.ONMOUSEOUT, DwtColorPicker._mouseOutHdlr);
                } else {
                    Dwt.setHandler(cell, DwtEvent.ONMOUSEENTER, DwtColorPicker._mouseOverHdlr);
                    Dwt.setHandler(cell, DwtEvent.ONMOUSELEAVE, DwtColorPicker._mouseOutHdlr);
                }
                if (cell.className != "NoFill")
                    cell.style.border = "2px outset " + cell.style.backgroundColor;
            }
        }
	}
}

DwtColorPicker.prototype._createColorTable =
function() {
	this._tdId = Dwt.getNextId();
	var html = new Array(150);
	var i = 0;
	
	html[i++] = "<table cellpadding='0' cellspacing='0' border='0' align='center'>";
	if (this._noFillLabel)
		html[i++] = "<tr><td colspan='10' class='NoFill'>" + this._noFillLabel + "</td></tr>";
	html[i++] = "<tr>";
	html[i++] = "<td id='" + this._tdId + "#FFFFFF' style='background-color:#FFFFFF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FFCCCC' style='background-color:#FFCCCC' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FFCC99' style='background-color:#FFCC99' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FFFF99' style='background-color:#FFFF99' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FFFFCC' style='background-color:#FFFFCC' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#99FF99' style='background-color:#99FF99' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#99FFFF' style='background-color:#99FFFF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#CCFFFF' style='background-color:#CCFFFF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#CCCCFF' style='background-color:#CCCCFF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FFCCFF' style='background-color:#FFCCFF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "</tr><tr>";
	html[i++] = "<td id='" + this._tdId + "#CCCCCC' style='background-color:#CCCCCC' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FF6666' style='background-color:#FF6666' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FF9966' style='background-color:#FF9966' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FFFF66' style='background-color:#FFFF66' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FFFF33' style='background-color:#FFFF33' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#66FF99' style='background-color:#66FF99' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#33FFFF' style='background-color:#33FFFF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#66FFFF' style='background-color:#66FFFF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#9999FF' style='background-color:#9999FF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FF99FF' style='background-color:#FF99FF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "</tr><tr>";
	html[i++] = "<td id='" + this._tdId + "#C0C0C0' style='background-color:#C0C0C0' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FF0000' style='background-color:#FF0000' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FF9900' style='background-color:#FF9900' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FFCC66' style='background-color:#FFCC66' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FFFF00' style='background-color:#FFFF00' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#33FF33' style='background-color:#33FF33' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#66CCCC' style='background-color:#66CCCC' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#33CCFF' style='background-color:#33CCFF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#6666CC' style='background-color:#6666CC' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#CC66CC' style='background-color:#CC66CC' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "</tr><tr>";
	html[i++] = "<td id='" + this._tdId + "#999999' style='background-color:#999999' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#CC0000' style='background-color:#CC0000' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FF6600' style='background-color:#FF6600' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FFCC33' style='background-color:#FFCC33' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#FFCC00' style='background-color:#FFCC00' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#33CC00' style='background-color:#33CC00' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#00CCCC' style='background-color:#00CCCC' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#3366FF' style='background-color:#3366FF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#6633FF' style='background-color:#6633FF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#CC33CC' style='background-color:#CC33CC' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "</tr><tr>";
	html[i++] = "<td id='" + this._tdId + "#666666' style='background-color:#666666' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#990000' style='background-color:#990000' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#CC6600' style='background-color:#CC6600' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#CC9933' style='background-color:#CC9933' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#999900' style='background-color:#999900' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#009900' style='background-color:#009900' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#339999' style='background-color:#339999' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#3333FF' style='background-color:#3333FF' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#6600CC' style='background-color:#6600CC' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#993399' style='background-color:#993399' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "</tr><tr>";
	html[i++] = "<td id='" + this._tdId + "#333333' style='background-color:#333333' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#660000' style='background-color:#660000' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#993300' style='background-color:#993300' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#996633' style='background-color:#996633' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#666600' style='background-color:#666600' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#006600' style='background-color:#006600' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#336666' style='background-color:#336666' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#000099' style='background-color:#000099' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#333399' style='background-color:#333399' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#663366' style='background-color:#663366' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "</tr><tr>";
	html[i++] = "<td id='" + this._tdId + "#000000' style='background-color:#000000' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#330000' style='background-color:#330000' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#663300' style='background-color:#663300' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#663333' style='background-color:#663333' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#333300' style='background-color:#333300' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#003300' style='background-color:#003300' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#003333' style='background-color:#003333' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#000066' style='background-color:#000066' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#330099' style='background-color:#330099' width='12' height='14'><img height='1' width='1'/></td>";
	html[i++] = "<td id='" + this._tdId + "#330033' style='background-color:#330033' width='12' height='14'><img height='1' width='1'/></td>";
    if (this._allowColorInput) { //allow RGB input
        html[i++] = "<tr><td></td></tr></tr><tr class='ColorInput'>";
        html[i++] = "<td colspan=6 id='" + this._tdId +".ColorInputField' class='ColorInputField'><input type=text size=15 maxlength=6 ></input></td>" +
                    "<td colspan=4 id='" + this._tdId +".SetInputColor' class='SetInputColor'>" + AjxMsg.setColor + "</td>" ;
    }

    html[i++] = "</tr></table>";

    this.getHtmlElement().innerHTML = html.join("");
}

DwtColorPicker._mouseOverHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev, true);
	var target = mouseEv.target;
	if (target.nodeName.toLowerCase() == "img")
		target = target.parentNode;

	if (mouseEv.dwtObj._downTdId == target.id) {
		var tmp = target.style.backgroundColor;
		target.style.backgroundColor = mouseEv.dwtObj._swappedColor;
		mouseEv.dwtObj._swappedColor = tmp;
		target.style.borderStyle = "inset";
		target.style.borderColor = tmp;
	}

	this._stopPropagation = true;
	this._returnValue = false;
	mouseEv.setToDhtmlEvent(ev)
	return false;
}

DwtColorPicker._mouseOutHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev, true);
	var target = mouseEv.target;
	if (target.nodeName.toLowerCase() == "img")
		target = target.parentNode;
 
	if (mouseEv.dwtObj._downTdId == target.id) {
		var tmp = target.style.backgroundColor;
		target.style.backgroundColor = mouseEv.dwtObj._swappedColor;
		mouseEv.dwtObj._swappedColor = tmp;
		target.style.borderStyle = "outset";
		target.style.borderColor = tmp;
		mouseEv.dwtObj._downTdId = null;
	}

	this._stopPropagation = true;
	this._returnValue = false;
	mouseEv.setToDhtmlEvent(ev)
	return false;
}

DwtColorPicker._mouseDownHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev, true);
	var target = mouseEv.target;
	if (target.nodeName.toLowerCase() == "img")
		target = target.parentNode;

	// Make a depressed button color darker than the original color to get
	// a true depressed effect
	var colorStr = mouseEv.target.style.backgroundColor;
	var rgb;
	var r, g, b;
	
	mouseEv.dwtObj._downTdId = target.id;
	mouseEv.dwtObj._swappedColor = colorStr;
	// target.style.border = "2px inset " + colorStr;
	target.style.borderStyle = "inset";

	if ((target.className != "NoFill") && (target.className !=  "SetInputColor")) {
		// IE refuses to convert Hex 2 rgb
		if (colorStr.substr(0, 1) == "#") {
			rgb = colorStr.match(DwtColorPicker._HEX_RE);
			rgb[1] = DwtColorPicker._hexConv(rgb[1]); 
			rgb[2] = DwtColorPicker._hexConv(rgb[2]); 
			rgb[3] = DwtColorPicker._hexConv(rgb[3]); 
		} else {
			rgb = colorStr.match(DwtColorPicker._RGB_RE);
		}
	
		r = Math.max(Math.floor(rgb[1] - (rgb[1] * 0.25)), 0);
		g = Math.max(Math.floor(rgb[2] - (rgb[2] * 0.25)), 0);
		b = Math.max(Math.floor(rgb[3] - (rgb[3] * 0.25)), 0);
		colorStr = "rgb(" + r + "," + g + "," + b + ")";
		target.style.backgroundColor = colorStr;
	}
	
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev)
	return false;
}

DwtColorPicker._hexConv = 
function(hexStr) {
	if (hexStr == "00")
		return 0;
	else if (hexStr == "33")
		return 51;
	else if (hexStr == "66")
		return 102;
	else if (hexStr == "99")
		return 153;
	else if (hexStr.toUpperCase() == "CC")
		return 204;
	else if (hexStr.toUpperCase() == "FF")
		return 255;
	else if (hexStr.toUpperCase() == "C0")
		return 192;
}

DwtColorPicker._mouseUpHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev, true);
	var me = mouseEv.dwtObj;
	
	var target = mouseEv.target;
	if (target.nodeName.toLowerCase() == "img")
		target = target.parentNode;
	
	if (me._downTdId == target.id) {
		target.style.borderStyle = "outset";
		target.style.borderColor = mouseEv.dwtObj._swappedColor;
		target.style.backgroundColor = mouseEv.dwtObj._swappedColor;
	}
	
	if (me._downTdId == target.id) {
	
		// If our parent is a menu then we need to have it close
		if (me.parent instanceof DwtMenu)
			DwtMenu.closeActiveMenu();
	
		// Call Listeners on mouseEv.target.id
		if (me.isListenerRegistered(DwtEvent.SELECTION)) {
	    	var selEv = DwtShell.selectionEvent;
	    	DwtUiEvent.copy(selEv, mouseEv);
	    	selEv.item = me;
            if (target.id == (me._tdId +".SetInputColor")) {
                selEv.detail = me.getInputColor () ;
            } else {
                selEv.detail = mouseEv.target.id.substr(mouseEv.target.id.indexOf("#"));
            }
            me.notifyListeners(DwtEvent.SELECTION, selEv);
	    }
	}
	
	me._downTdId = null;
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev)
	return false;
}

/**
 * Gets the input color.
 * 
 * @return	{string}	the color (in hex) from the input color field
 */
DwtColorPicker.prototype.getInputColor = function () {
    var inputField = document.getElementById (this._tdId + ".ColorInputField").firstChild ;
    var inputColor = "#" + inputField.value ;
    if (inputColor.match(DwtColorPicker._HEX_COMPLETE_RE) != null) {
        return inputColor ;
    }else{
        return "";
    }
}


