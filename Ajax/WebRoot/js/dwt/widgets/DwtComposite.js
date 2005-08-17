function DwtComposite(parent, className, posStyle, deferred) {

	if (arguments.length == 0) return;
	className = className || "DwtComposite";
	DwtControl.call(this, parent, className, posStyle, deferred);

	this._children = new LsVector();
	this._updating = false;
}

DwtComposite.prototype = new DwtControl;
DwtComposite.prototype.constructor = DwtComposite;

// Pending elements hash (i.e. elements that have not yet been realized)
DwtComposite._pendingElements = new Object();

DwtComposite.prototype.toString = 
function() {
	return "DwtComposite";
}

DwtComposite.prototype.dispose =
function() {
	if (this._disposed) return;
DBG.println(LsDebug.DBG3, "DwtComposite.prototype.dispose: " + this.toString() + " - " + this._htmlElId);
	var sz = this._children.size();
	if (sz > 0) {
		// Dup the array since disposing the children will result in _removeChild
		// being called which will modify the array
		var a = this._children.getArray().slice(0);
		for (var i = 0; i < sz; i++) {
			if (a[i].dispose)
				a[i].dispose();
		}
	}		
	DwtControl.prototype.dispose.call(this);
}

DwtComposite.prototype.getChildren =
function() {
	return this._children.getArray().slice(0);
}

DwtComposite.prototype.getNumChildren =
function() {
	return this._children.size();
}

DwtComposite.prototype.removeChildren =
function() {
	var a = this._children.getArray();
	while (this._children.size() > 0)
		a[0].dispose();
}

DwtComposite.prototype.clear =
function() {
	this.removeChildren();
	this.getHtmlElement().innerHTML = "";
}

DwtComposite.prototype._addChild =
function(child) {
	this._children.add(child);
	this.getHtmlElement().appendChild(child.getHtmlElement());
}

DwtComposite.prototype._removeChild =
function(child) {
	DBG.println(LsDebug.DBG3, "DwtComposite.prototype._removeChild: " + child._htmlElId + " - " + child.toString());
	// Make sure that the child is initialized. Certain children (such as DwtTreeItems)
	// can be created in a deferred manner (i.e. they will only be initialized if they
	// are required to be visible
	if (child.isInitialized()) {
		this._children.remove(child);
		// Sometimes children are nested in arbitrary HTML so we elect to remove them
		// in this fashion rather than use this.getHtmlElement().removeChild(child.getHtmlElement()
		var childHtmlEl = child.getHtmlElement();
		if (childHtmlEl && childHtmlEl.parentNode)
			childHtmlEl.parentNode.removeChild(childHtmlEl);
	}
}

DwtComposite.prototype._update = function() {}
