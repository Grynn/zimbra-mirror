// TODO - Be more thorough about checked whether setSelected() and setActioned()
// actually did anything before doing notifying listeners
function DwtTree(parent, style, className, posStyle) {

	if (arguments.length == 0) return;
	className = className || "DwtTree";
	DwtComposite.call(this, parent, className, posStyle);

	if (style == null) {
		this._style = DwtTree.SINGLE_STYLE;
	} else {
		if (style == DwtTree.CHECKEDITEM_STYLE)
			style |= DwtTree.SINGLE_STYLE;
		this._style = style;
	}
	this._selectedItems = new AjxVector();
	this._selEv = new DwtSelectionEvent(true);
}

DwtTree.prototype = new DwtComposite;
DwtTree.prototype.constructor = DwtTree;

DwtTree.prototype.toString = 
function() {
	return "DwtTree";
}

DwtTree.SINGLE_STYLE = 1;
DwtTree.MULTI_STYLE = 2;
DwtTree.CHECKEDITEM_STYLE = 4;

DwtTree.ITEM_SELECTED = 0;
DwtTree.ITEM_DESELECTED = 1;
DwtTree.ITEM_CHECKED = 2;
DwtTree.ITEM_ACTIONED = 3;
DwtTree.ITEM_DBL_CLICKED = 4;

DwtTree.ITEM_EXPANDED = 1;
DwtTree.ITEM_COLLAPSED = 2;

DwtTree.prototype.addSelectionListener = 
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
}

DwtTree.prototype.removeSelectionListener = 
function(listener) {
	this.removeListener(DwtEvent.SELECTION, listener);    	
}

DwtTree.prototype.addTreeListener = 
function(listener) {
	this.addListener(DwtEvent.TREE, listener);
}

DwtTree.prototype.removeTreeListener = 
function(listener) {
	this.removeListener(DwtEvent.TREE, listener);    		
}

DwtTree.prototype.getItemCount =
function() {
	return this._children.size();
}

DwtTree.prototype.getItems =
function() {
	return this._children.getArray();
}

DwtTree.prototype.deselectAll =
function() {
	var a = this._selectedItems.getArray();
	var sz = this._selectedItems.size();
	for (var i = 0; i < sz; i++) {
		a[i]._setSelected(false);
	}
	if (sz > 0)
		this._notifyListeners(DwtEvent.SELECTION, this._selectedItems.getArray(), DwtTree.ITEM_DESELECTED, null, this._selEv);
	this._selectedItems.removeAll();
}

DwtTree.prototype.getSelection =
function() {
	return this._selectedItems.getArray();
}

DwtTree.prototype.setSelection =
function(treeItem, skipNotify) {
	// Remove currently selected items from the selection list. if <treeItem> is in that list, then note it and return
	// after we are done processing the selected list
	var a = this._selectedItems.getArray();
	var sz = this._selectedItems.size();
	var da;
	var j = 0;
	var alreadySelected = false;
	for (var i = 0; i < sz; i++) {
		if (a[i] == treeItem) 
			alreadySelected = true;
		else {
			a[i]._setSelected(false);
			this._selectedItems.remove(a[i]);
			if (da == null)
				da = new Array();
			da[j++] = a[i];
		}
	}

	if (da && !skipNotify)
		this._notifyListeners(DwtEvent.SELECTION, da, DwtTree.ITEM_DESELECTED, null, this._selEv);

	if (alreadySelected)
		return;
	this._selectedItems.add(treeItem);
	
	// Expand all parent nodes, and then set item selected
	var parent = treeItem.parent
	while(parent instanceof DwtTreeItem) {
		parent.setExpanded(true);
		parent = parent.parent;
	}
	if (treeItem._setSelected(true) && !skipNotify)
		this._notifyListeners(DwtEvent.SELECTION, [treeItem], DwtTree.ITEM_SELECTED, null, this._selEv);
}

DwtTree.prototype.getSelectionCount =
function() {
	return this._selectedItems.size();
}

DwtTree.prototype._addChild =
function(child) {}

DwtTree.prototype.addSeparator =
function() {
	var sep = this.getDocument().createElement("div");
//	sep.className = "horizSep";
	sep.className = "vSpace";
	this.getHtmlElement().appendChild(sep);
}

DwtTree.prototype._addItem =
function(item, index) {
	this._children.add(item, index);
	var thisHtmlElement = this.getHtmlElement();
	var numChildren = thisHtmlElement.childNodes.length;
	if (index == null || index > numChildren) {
		thisHtmlElement.appendChild(item.getHtmlElement());
	} else {
		thisHtmlElement.insertBefore(item.getHtmlElement(), thisHtmlElement.childNodes[index]);	
	}
}

DwtTree.prototype._removeChild =
function(child) {
	this._children.remove(child);
	this._selectedItems.remove(child);
	this.getHtmlElement().removeChild(child.getHtmlElement());
}

DwtTree.prototype._deselect =
function(item) {
	if (this._selectedItems.contains(item)) {
		this._selectedItems.remove(item);
		item._setSelected(false);
		this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_DESELECTED, null, this._selEv);
	}
}

DwtTree.prototype._isCheckedStyle =
function() {
	return ((this._style & DwtTree.CHECKEDITEM_STYLE) != 0);
}

DwtTree.prototype._itemActioned =
function(item, ev) {
	if (this._actionedItem) {
		this._actionedItem._setActioned(false);
		this._notifyListeners(DwtEvent.SELECTION, [this._actionedItem], DwtTree.ITEM_DESELECTED, ev, this._selEv);
	}
	this._actionedItem = item;
	item._setActioned(true);
	this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_ACTIONED, ev, this._selEv);
}

DwtTree.prototype._itemChecked =
function(item, ev) {
	this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_CHECKED, ev, this._selEv);
}

DwtTree.prototype._itemClicked =
function(item, ev) {
	var i;
	var a = this._selectedItems.getArray();
	var numSelectedItems = this._selectedItems.size();
	if (this._style & DwtTree.SINGLE_STYLE || (!ev.shiftKey && !ev.ctrlKey)) {
		if (numSelectedItems > 0) {
			for (i = 0; i < numSelectedItems; i++)
				a[i]._setSelected(false);
			// Notify listeners of deselection
			this._notifyListeners(DwtEvent.SELECTION, this._selectedItems.getArray(), DwtTree.ITEM_DESELECTED, ev, this._selEv);
			this._selectedItems.removeAll();
		}
		this._selectedItems.add(item);
		if (item._setSelected(true))
			this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_SELECTED, ev, this._selEv);
	} else {
		if (ev.ctrlKey) {
			if (this._selectedItems.contains(item)) {
				this._selectedItems.remove(item);
				item._setSelected(false);
				this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_DESELECTED, ev, this._selEv);
			} else {
				this._selectedItems.add(item);
				if (item._setSelected(true))
					this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_SELECTED, ev, this._selEv);
			}
		} else {
			// SHIFT KEY
		}
	}
}

DwtTree.prototype._itemDblClicked = 
function(item, ev) {
	this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_DBL_CLICKED, ev, this._selEv);
}

DwtTree.prototype._itemExpanded =
function(item, ev) {
	this._notifyListeners(DwtEvent.TREE, [item], DwtTree.ITEM_EXPANDED, ev, DwtShell.treeEvent);
}

DwtTree.prototype._itemCollapsed =
function(item, ev) {
	var i;
	if (ev)
		this._notifyListeners(DwtEvent.TREE, [item], DwtTree.ITEM_COLLAPSED, ev, DwtShell.treeEvent);
	var setSelection = false;
	var a = this._selectedItems.getArray();
	var numSelectedItems = this._selectedItems.size();
	var da;
	var j = 0;
	for (i = 0; i < numSelectedItems; i++) {
		if (a[i]._isChildOf(item)) {
			setSelection = true;
			if (da == null)
				da = new Array();
			da[j++] = a[i];
			a[i]._setSelected(false);
			this._selectedItems.remove(a[i]);
		}		
	}

	if (da)
		this._notifyListeners(DwtEvent.SELECTION, da, DwtTree.ITEM_DESELECTED, ev, this._selEv);

	if (setSelection && !this._selectedItems.contains(item)) {
		this._selectedItems.add(item);
		if (item._setSelected(true))
			this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_SELECTED, ev, this._selEv);
	}
}

DwtTree.prototype._notifyListeners =
function(listener, items, detail, srcEv, destEv) {
	if (this.isListenerRegistered(listener)) {
		if (srcEv)
			DwtUiEvent.copy(destEv, srcEv);
		destEv.items = items;
		if (items.length == 1)
			destEv.item = items[0];
		destEv.detail = detail;
		this.notifyListeners(listener, destEv);
	}
}