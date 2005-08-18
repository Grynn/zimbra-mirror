function ZaSearchToolBar(parent, posStyle) {

	ZaToolBar.call(this, parent, null, posStyle, "SearchToolBar");

	this._searchField = new ZaSearchField(this, "SearchTBSearchField", 48);
	var h1 = this._searchField.getSize().y;
		
	this.setSize(DwtControl.DEFAULT, Math.max(this._searchField.getSize().y, this.computeHeight()));
}

ZaSearchToolBar.prototype = new ZaToolBar;
ZaSearchToolBar.prototype.constructor = ZaSearchToolBar;

ZaSearchToolBar.prototype.toString = 
function() {
	return "ZaSearchToolBar";
}

ZaSearchToolBar.prototype.addSelectionListener =
function(buttonId, listener) {
	// Don't allow listeners on the search by button since we only want listeners registered
	// on its menu items
	if (buttonId != ZaSearchToolBar.SEARCHFOR_BUTTON)
		ZaToolBar.prototype.addSelectionListener.call(this, buttonId, listener);
}


ZaSearchToolBar.prototype.getSearchField =
function() {
	return this._searchField;
}
