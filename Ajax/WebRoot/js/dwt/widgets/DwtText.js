function DwtText(parent, className, posStyle) {

	if (arguments.length == 0) return;
	className = className || "DwtText";
	DwtControl.call(this, parent, className, posStyle);
}

DwtText.prototype = new DwtControl;
DwtText.prototype.constructor = DwtText;

DwtText.prototype.toString = 
function() {
	return "DwtText";
}

DwtText.prototype.setText =
function(text) {
	if (!this._textNode) {
		 this._textNode = this.getDocument().createTextNode(text);
		 this.getHtmlElement().appendChild(this._textNode);
	} else {
		this._textNode.data = text;
	}
}

DwtText.prototype.getText =
function() {
	return this._textNode.data;
}

DwtText.prototype.getTextNode =
function() {
	return this._textNode;
}
