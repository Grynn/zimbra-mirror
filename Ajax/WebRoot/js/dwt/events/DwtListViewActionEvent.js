function DwtListViewActionEvent() {

	DwtMouseEvent.call(this);
	this.field = null;
	this.item = null;
	this.detail = null;
}

DwtListViewActionEvent.prototype = new DwtMouseEvent;
DwtListViewActionEvent.prototype.constructor = DwtListViewActionEvent;

DwtListViewActionEvent.prototype.toString = 
function() {
	return "DwtListViewActionEvent";
}
