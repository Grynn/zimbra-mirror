/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZAPL 1.1
 * 
 * The contents of this file are subject to the Zimbra AJAX Public
 * License Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra AJAX Toolkit.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function DwtEvent(init) {
	if (arguments.length == 0) return;
	this.dwtObj = null;
}

DwtEvent.prototype.toString = 
function() {
	return "DwtEvent";
}

DwtEvent.ONDBLCLICK = "ONDBLCLICK";
DwtEvent.ONMOUSEDOWN = "ONMOUSEDOWN";
DwtEvent.ONMOUSEUP = "ONMOUSEUP";
DwtEvent.ONMOUSEMOVE = "ONMOUSEMOVE";
DwtEvent.ONMOUSEOUT = "ONMOUSEOUT";
DwtEvent.ONMOUSELEAVE = "ONMOUSELEAVE";
DwtEvent.ONMOUSEOVER = "ONMOUSEOVER";
DwtEvent.ONMOUSEENTER = "ONMOUSEENTER";
DwtEvent.ONSELECTSTART = "ONSELECTSTART";
DwtEvent.ONCONTEXTMENU = "ONCONTEXTMENU";
DwtEvent.ONCHANGE = "ONCHANGE";
DwtEvent.ONFOCUS = "ONFOCUS";

DwtEvent.CONTROL = "CONTROL";
DwtEvent.DISPOSE = "DISPOSE";
DwtEvent.SELECTION = "SELECTION";
DwtEvent.ACTION = "ACTION";
DwtEvent.TREE = "TREE";
DwtEvent.POPDOWN = "POPDOWN";
DwtEvent.DATE_RANGE = "DATE_RANGE";
DwtEvent.STATE_CHANGE = "STATE_CHANGE";
DwtEvent.TAB = "TAB";
DwtEvent.ENTER = "ENTER";
DwtEvent.HOVEROVER = "HOVEROVER";
DwtEvent.HOVEROUT = "HOVEROUT";

DwtEvent.BUTTON_PRESSED = "BUTTON_PRESSED";

DwtEvent.XFORMS_READY = "xforms-ready";
DwtEvent.XFORMS_DISPLAY_UPDATED = "xforms-display-updated";
DwtEvent.XFORMS_VALUE_CHANGED = "xforms-value-changed";
DwtEvent.XFORMS_FORM_DIRTY_CHANGE = "xforms-form-dirty-change";
DwtEvent.XFORMS_CHOICES_CHANGED = "xforms-choices-changed";
DwtEvent.XFORMS_VALUE_ERROR = "xforms-value-error";
