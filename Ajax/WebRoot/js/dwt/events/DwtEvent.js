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

DwtEvent.BUTTON_PRESSED = "BUTTON_PRESSED";

DwtEvent.XFORMS_READY = "xforms-ready";
DwtEvent.XFORMS_DISPLAY_UPDATED = "xforms-display-updated";
DwtEvent.XFORMS_VALUE_CHANGED = "xforms-value-changed";
DwtEvent.XFORMS_FORM_DIRTY_CHANGE = "xforms-form-dirty-change";
DwtEvent.XFORMS_CHOICES_CHANGED = "xforms-choices-changed";
DwtEvent.XFORMS_VALUE_ERROR = "xforms-value-error";
