function DwtHtmlEditorStateEvent(init) {
	if (arguments.length == 0) return;
	DwtEvent.call(this, true);
	this.reset();
}

DwtHtmlEditorStateEvent.prototype = new DwtEvent;
DwtHtmlEditorStateEvent.prototype.constructor = DwtHtmlEditorStateEvent;

DwtHtmlEditorStateEvent.prototype.toString = 
function() {
	return "DwtHtmlEditorStateEvent";
}

DwtHtmlEditorStateEvent.prototype.reset =
function() {
	this.isBold = null;
	this.isItalic = null;
	this.isUnderline = null;
	this.isStrikeThru = null;
	this.isSuperscript = null;
	this.isSubscript = null;
	this.isOrderedList = null;
	this.isNumberedList = null;
	this.fontName = null;
	this.fontSize = null;
	this.style = null;
	this.backgroundColor = null;
	this.color = null;
	this.justification = null;
	this.direction = null;
}
