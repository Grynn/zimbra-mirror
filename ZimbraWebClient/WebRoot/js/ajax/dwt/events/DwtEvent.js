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
 * @class
 * This class is the base class of all DWT events.
 * 
 * @param {Boolean} __init 	a dummy parameter used for class initialization
 * 
 * @author Ross Dargahi
 * @author Conrad Damon
 * 
 */
DwtEvent = function(__init) {
	if (arguments.length == 0) return;
	/**
	 * The Dwt object that generated the event
	 * @type DwtControl
	 */
	this.dwtObj = null;
}

/**
 * Returns a string representation of the object.
 * 
 * @return		{String}		a string representation of the object
 */
DwtEvent.prototype.toString = 
function() {
	return "DwtEvent";
}

// native browser events - value is the associated DOM property
/**
 * Browser onchange event
 * @type String
 */
DwtEvent.ONCHANGE = "onchange";

/** Browser onclick event
 * @type String
 */
DwtEvent.ONCLICK = "onclick";

/** Browser oncontextmenu event
 * @type String
 */
DwtEvent.ONCONTEXTMENU = "oncontextmenu";

/** Double click event (ondblclick) event
 * @type String
 */
DwtEvent.ONDBLCLICK = "ondblclick";

/** Browser onfocus event
 * @type String
 */
DwtEvent.ONFOCUS = "onfocus";

/** Browser onblug event
 * @type String
 */
DwtEvent.ONBLUR = "onblur";

/** Browser onkeydown event
 * @type String
 */
DwtEvent.ONKEYDOWN = "onkeydown";

/** Browser onkeypress event
 * @type String
 */
DwtEvent.ONKEYPRESS = "onkeypress";

/** Browser onkeyup event
 * @type String
 */
DwtEvent.ONKEYUP = "onkeyup";

/** Browser onmousedown event
 * @type String
 */
DwtEvent.ONMOUSEDOWN = "onmousedown";

/** Browser onmouseenter event (IE Only) - reported only for the element
 * @type String
 */
DwtEvent.ONMOUSEENTER = "onmouseenter";

/** Browser onmouseleave event (IE Only) - reported only for the element
 * @type String
 */
DwtEvent.ONMOUSELEAVE = "onmouseleave";

/** Browser onmousemove event
 * @type String
 */
DwtEvent.ONMOUSEMOVE = "onmousemove";

/** Browser onmouseout event - reported for element and children
 * @type String
 */
DwtEvent.ONMOUSEOUT = "onmouseout";

/** Browser onmouseover event - reported for element and children
 * @type String
 */
DwtEvent.ONMOUSEOVER = "onmouseover";

/** Browser onmouseup event
 * @type String
 */
DwtEvent.ONMOUSEUP = "onmouseup";

/** Browser onmousewheel event
 * @type String
 */
DwtEvent.ONMOUSEWHEEL = "onmousewheel";

/** Browser onselectstart event
 * @type String
 */
DwtEvent.ONSELECTSTART = "onselectstart";

/** Browser onscroll event
 * @type String
 */
DwtEvent.ONSCROLL = "onscroll";

// semantic events
/** Action event. An example is right-clicking on a list item or tree item
 * generally brings up a context menu
 * @type String
 */
DwtEvent.ACTION	= "ACTION";

/** Control event. Control events are fired by resizing or repositioning
 * <i>DwtControl</i>s
 * @type String
 * @see DwtControl
 */
DwtEvent.CONTROL = "CONTROL";		// resize

/** Date Range events are fired by the {@link DwtCalendar} widget. This event is
 * fired when the date range of the calendar widget changes
 * @type String
 * @see DwtCalendar
 */
DwtEvent.DATE_RANGE	= "DATE_RANGE";

/** The dispose event is fired when the {@link DwtControl#dispose} method of a control is
 * called.
 * @type String
 * @see DwtControl
 * @see DwtControl#dispose
 */
DwtEvent.DISPOSE = "DISPOSE";

/** The enter event is fired when the enter key is pressed
 * @type String
 * @private
 */
DwtEvent.ENTER = "ENTER";			// enter/return key

/** This event is fired when the mouse hovers over a control for a certain
 * period of time
 * @type String
 */
DwtEvent.HOVEROVER = "HOVEROVER";

/** This event is fired when the mouse stops hovering over a control
 * @type String
 */
DwtEvent.HOVEROUT = "HOVEROUT";

/** The popdown event is fired when a item (such as a menu) is popped down
 * @type String
 * @see DwtMenu
 */
DwtEvent.POPDOWN = "POPDOWN";

/** The popup event is fired when a item (such as a menu) is popped up
 * @type String
 * @see DwtMenu*/
DwtEvent.POPUP = "POPUP";

/** The selection event is fired when controls are selected. This generally means
 * that there has been a "left mouse button click" in the control (e.g. a button, or
 * list item, or tree node
 * @type String
 */
DwtEvent.SELECTION = "SELECTION";		// left-click


/** A tree event is fired when a tree node is expanded or collapsed
 * @type String
 * @see DwtTree
 */
DwtEvent.TREE = "TREE";

/** State change events are fired when some intrinsic state of a widget changes. For
 * example it may be the font style of some text in {@link DwtHtmlEditor} changed
 * @type String
 */
DwtEvent.STATE_CHANGE	= "STATE_CHANGE";

/** The tab event is fired when the tab key is pressed
 * @type String
 * @private
 */
DwtEvent.TAB = "TAB";

// XForms
DwtEvent.XFORMS_READY				= "xforms-ready";
DwtEvent.XFORMS_DISPLAY_UPDATED		= "xforms-display-updated";
DwtEvent.XFORMS_VALUE_CHANGED		= "xforms-value-changed";
DwtEvent.XFORMS_FORM_DIRTY_CHANGE	= "xforms-form-dirty-change";
DwtEvent.XFORMS_CHOICES_CHANGED		= "xforms-choices-changed";
DwtEvent.XFORMS_VALUE_ERROR			= "xforms-value-error";
DwtEvent.XFORMS_INSTANCE_CHANGED 	= "xforms-instance-cahnged"; //fires when a new instance is applied to the form
// Convenience lists
/** Array of key event types
 * @type Array
 */
DwtEvent.KEY_EVENTS = [DwtEvent.ONKEYDOWN, DwtEvent.ONKEYPRESS, DwtEvent.ONKEYUP];

/** Array of mouse event types
 * @type Array
 */
DwtEvent.MOUSE_EVENTS = [DwtEvent.ONCONTEXTMENU, DwtEvent.ONDBLCLICK, DwtEvent.ONMOUSEDOWN,
						 DwtEvent.ONMOUSEMOVE, DwtEvent.ONMOUSEUP, DwtEvent.ONSELECTSTART,
						 DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT];
