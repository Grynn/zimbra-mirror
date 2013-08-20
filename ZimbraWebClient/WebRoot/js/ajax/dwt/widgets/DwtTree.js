    /*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */


/**
 * Creates a Tree widget.
 * @constructor
 * @class
 * This class implements a tree widget. Tree widgets may contain one or more DwtTreeItems.
 *
 * @author Ross Dargahi
 * 
 * @param {hash}	params				a hash of parameters
 * @param  {DwtComposite}     params.parent			the parent widget
 * @param  {DwtTree.SINGLE_STYLE|DwtTree.MULTI_STYLE|DwtTree.CHECKEDITEM_STYLE}     params.style 	the tree style
 * @param  {string}     params.className				the CSS class
 * @param  {constant}     params.posStyle				the positioning style (see {@link DwtControl})
 * @param  {boolean}     params.isCheckedByDefault	default checked state if tree styles is "checked"
 * 
 * @extends		DwtComposite
 */
DwtTree = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtTree.PARAMS);
	params.className = params.className || "DwtTree";
	DwtComposite.call(this, params);

	var events = [DwtEvent.ONMOUSEDOWN, DwtEvent.ONMOUSEUP, DwtEvent.ONDBLCLICK];
	if (!AjxEnv.isIE) {
		events = events.concat([DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT]);
	}
	this._setEventHdlrs(events);

	var style = params.style;
	if (!style) {
		this._style = DwtTree.SINGLE_STYLE;
	} else {
		if (style == DwtTree.CHECKEDITEM_STYLE) {
			style |= DwtTree.SINGLE_STYLE;
		}
		this._style = style;
	}
	this.isCheckedStyle = ((this._style & DwtTree.CHECKEDITEM_STYLE) != 0);
	this.isCheckedByDefault = params.isCheckedByDefault;

	this._selectedItems = new AjxVector();
	this._selEv = new DwtSelectionEvent(true);
	this._selByClickEv = new DwtSelectionEvent(true);
	this._selByClickEv.clicked = true;
	this._selByEnterEv = new DwtSelectionEvent(true);
	this._selByEnterEv.enter = true;
};

DwtTree.PARAMS = ["parent", "style", "className", "posStyle"];

DwtTree.prototype = new DwtComposite;
DwtTree.prototype.constructor = DwtTree;

DwtTree.prototype.toString = 
function() {
	return "DwtTree";
};

/**
 * Defines the "single" style.
 */
DwtTree.SINGLE_STYLE = 1;
/**
 * Defines the "multi" style.
 */
DwtTree.MULTI_STYLE = 2;
/**
 * Defines the "checked-item" style.
 */
DwtTree.CHECKEDITEM_STYLE = 4;

DwtTree.ITEM_SELECTED = 0;
DwtTree.ITEM_DESELECTED = 1;
DwtTree.ITEM_CHECKED = 2;
DwtTree.ITEM_ACTIONED = 3;
DwtTree.ITEM_DBL_CLICKED = 4;

DwtTree.ITEM_EXPANDED = 1;
DwtTree.ITEM_COLLAPSED = 2;

/**
 * Gets the style.
 * 
 * @return	{constant}	the style
 */
DwtTree.prototype.getStyle =
function() {
	return this._style;
};

/**
 * Adds a selection listener.
 * 
 * @param	{AjxListener}	listener	the listener
 */
DwtTree.prototype.addSelectionListener = 
function(listener) {
	this.addListener(DwtEvent.SELECTION, listener);
};

/**
 * Removes a selection listener.
 * 
 * @param	{AjxListener}	listener	the listener
 */
DwtTree.prototype.removeSelectionListener = 
function(listener) {
	this.removeListener(DwtEvent.SELECTION, listener);    	
};

/**
 * Adds a tree listener.
 * 
 * @param	{AjxListener}	listener	the listener
 */
DwtTree.prototype.addTreeListener = 
function(listener) {
	this.addListener(DwtEvent.TREE, listener);
};

/**
 * Removes a selection listener.
 * 
 * @param	{AjxListener}	listener	the listener
 */
DwtTree.prototype.removeTreeListener = 
function(listener) {
	this.removeListener(DwtEvent.TREE, listener);
};

/**
 * Gets the tree item count.
 * 
 * @return	{number}	the item count
 */
DwtTree.prototype.getItemCount =
function() {
	return this.getItems().length;
};

/**
 * Gets the items.
 * 
 * @return	{array}	an array of {@link DwtTreeItem} objects
 */
DwtTree.prototype.getItems =
function() {
	return this._children.getArray();
};

/** Clears the tree items. */
DwtTree.prototype.clearItems = function() {
    var items = this.getItems();
    for (var i = 0; i < items.length; i++) {
        this.removeChild(items[i]);
    }
    this._getContainerElement().innerHTML = "";
};


/**
 * De-selects all items.
 * 
 */
DwtTree.prototype.deselectAll =
function() {
	var a = this._selectedItems.getArray();
	var sz = this._selectedItems.size();
	for (var i = 0; i < sz; i++) {
		if (a[i]) {
			a[i]._setSelected(false);
		}
	}
	if (sz > 0) {
		this._notifyListeners(DwtEvent.SELECTION, this._selectedItems.getArray(), DwtTree.ITEM_DESELECTED, null, this._selEv);
	}
	this._selectedItems.removeAll();
};

/**
 * Gets an array of selection items.
 * 
 * @return	{array}	an array of {@link DwtTreeItem} objects
 */
DwtTree.prototype.getSelection =
function() {
	return this._selectedItems.getArray();
};

DwtTree.prototype.setEnterSelection =
function(treeItem, kbNavEvent) {
	if (!treeItem) {
		return;
	}
	this._notifyListeners(DwtEvent.SELECTION, [treeItem], DwtTree.ITEM_SELECTED, null, this._selByEnterEv, kbNavEvent);
};


DwtTree.prototype.setSelection =
function(treeItem, skipNotify, kbNavEvent, noFocus) {
	if (!treeItem) { return; }

	// Remove currently selected items from the selection list. if <treeItem> is in that list, then note it and return
	// after we are done processing the selected list
	var a = this._selectedItems.getArray();
	var sz = this._selectedItems.size();
	var da;
	var j = 0;
	var alreadySelected = false;
	for (var i = 0; i < sz; i++) {
		if (a[i] == treeItem) {
			alreadySelected = true;
		} else {
			a[i]._setSelected(false);
			this._selectedItems.remove(a[i]);
			if (da == null) {
				da = new Array();
			}
			da[j++] = a[i];
		}
	}

	if (da && !skipNotify) {
		this._notifyListeners(DwtEvent.SELECTION, da, DwtTree.ITEM_DESELECTED, null, this._selEv, kbNavEvent);
	}

	if (alreadySelected) { return; }
	this._selectedItems.add(treeItem);

	// Expand all parent nodes, and then set item selected
	this._expandUp(treeItem);
	if (treeItem._setSelected(true, noFocus) && !skipNotify) {
		this._notifyListeners(DwtEvent.SELECTION, [treeItem], DwtTree.ITEM_SELECTED, null, this._selEv, kbNavEvent);
	}
};

DwtTree.prototype.getSelectionCount =
function() {
	return this._selectedItems.size();
};

DwtTree.prototype.addChild = function(child) {
    // HACK: Tree items are added via _addItem. But we need to keep
    // HACK: the original addChild behavior for other controls that
    // HACK: may be added to the tree view.
    if (child instanceof DwtTreeItem) return;
    DwtComposite.prototype.addChild.apply(this, arguments);
};

/**
 * Adds a separator.
 * 
 */
DwtTree.prototype.addSeparator =
function() {
	var sep = document.createElement("div");
	sep.className = "vSpace";
	this._getContainerElement().appendChild(sep);
};

// Expand parent chain from given item up to root
DwtTree.prototype._expandUp =
function(item) {
	var parent = item.parent;
	while (parent instanceof DwtTreeItem) {
		parent.setExpanded(true);
		parent.setVisible(true);
		parent = parent.parent;
	}
};

DwtTree.prototype._addItem =
function(item, index) {
	this._children.add(item, index);
	var thisHtmlElement = this._getContainerElement();
	var numChildren = thisHtmlElement.childNodes.length;
	if (index == null || index > numChildren) {
		thisHtmlElement.appendChild(item.getHtmlElement());
	} else {
		thisHtmlElement.insertBefore(item.getHtmlElement(), thisHtmlElement.childNodes[index]);	
	}
};

DwtTree.prototype._getContainerElement = DwtTree.prototype.getHtmlElement;

DwtTree.prototype.sort =
function(cmp) {
    var children = this.getItems();
    children.sort(cmp);
    var fragment = document.createDocumentFragment();
    AjxUtil.foreach(children, function(item, i){
        fragment.appendChild(item.getHtmlElement());
        item._index = i;
    });
    this._getContainerElement().appendChild(fragment);
};

DwtTree.prototype.removeChild =
function(child) {
	this._children.remove(child);
	this._selectedItems.remove(child);
    var childEl = child.getHtmlElement();
    if (childEl.parentNode) {
        childEl.parentNode.removeChild(childEl);
    }
};

/**
 * Returns the next (or previous) tree item relative to the currently selected item,
 * in top-to-bottom order as the tree appears visually. Items such as separators (and
 * possibly headers) that cannot be selected are skipped.
 * </p><p>
 * If there is no currently selected item, return the first or last item. If we go past
 * the beginning or end of the tree, return null.
 * </p><p>
 * For efficiency, a flattened list of the visible and selectable tree items is maintained.
 * It will be cleared on any change to the tree's display, then regenerated when it is
 * needed.
 *
 * @param {boolean}	next		if <code>true</code>, return next tree item; otherwise, return previous tree item
 * 
 * @private
 */
DwtTree.prototype._getNextTreeItem =
function(next) {

	var sel = this.getSelection();
	var curItem = (sel && sel.length) ? sel[0] : null;

	var nextItem = null, idx = -1;
	var list = this.getTreeItemList(true);
	if (curItem) {
		for (var i = 0, len = list.length; i < len; i++) {
			var ti = list[i];
			if (ti == curItem) {
				idx = next ? i + 1 : i - 1;
				break;
			}
		}
		nextItem = list[idx]; // if array index out of bounds, nextItem is undefined
	} else {
		// if nothing is selected yet, return the first or last item
		if (list && list.length) {
			nextItem = next ? list[0] : list[list.length - 1];
		}
	}
	return nextItem;
};

/**
 * Creates a flat list of this tree's items, going depth-first.
 *
 * @param {boolean}	visible		if <code>true</code>, only include visible/selectable items
 * @return	{array}	an array of {@link DwtTreeItem} objects
 */
DwtTree.prototype.getTreeItemList =
function(visible) {
	return this._addToList([], visible);
};

DwtTree.prototype._addToList =
function(list, visible, treeItem) {
	if (treeItem && !treeItem._isSeparator &&
		(!visible || (treeItem.getVisible() && treeItem._selectionEnabled))) {

		list.push(treeItem);
	}
	if (!treeItem || !visible || treeItem._expanded) {
		var parent = treeItem || this;
		var children = parent.getChildren ? parent.getChildren() : [];
		for (var i = 0; i < children.length; i++) {
			this._addToList(list, visible, children[i]);
		}
	}
	return list;
};

DwtTree.prototype._deselect =
function(item) {
	if (this._selectedItems.contains(item)) {
		this._selectedItems.remove(item);
		item._setSelected(false);
		this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_DESELECTED, null, this._selEv);
	}
};

DwtTree.prototype._itemActioned =
function(item, ev) {
	if (this._actionedItem) {
		this._actionedItem._setActioned(false);
		this._notifyListeners(DwtEvent.SELECTION, [this._actionedItem], DwtTree.ITEM_DESELECTED, ev, this._selEv);
	}
	this._actionedItem = item;
	item._setActioned(true);
	this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_ACTIONED, ev, this._selEv);
};

DwtTree.prototype._itemChecked =
function(item, ev) {
	this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_CHECKED, ev, this._selEv);
};

DwtTree.prototype._itemClicked =
function(item, ev) {
	var i;
	var a = this._selectedItems.getArray();
	var numSelectedItems = this._selectedItems.size();
	if (this._style & DwtTree.SINGLE_STYLE || (!ev.shiftKey && !ev.ctrlKey)) {
		if (numSelectedItems > 0) {
			for (i = 0; i < numSelectedItems; i++) {
				a[i]._setSelected(false);
			}
			// Notify listeners of deselection
			this._notifyListeners(DwtEvent.SELECTION, this._selectedItems.getArray(), DwtTree.ITEM_DESELECTED, ev, this._selByClickEv);
			this._selectedItems.removeAll();
		}
		this._selectedItems.add(item);
		if (item._setSelected(true)) {
			this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_SELECTED, ev, this._selByClickEv);
		}
	} else {
		if (ev.ctrlKey) {
			if (this._selectedItems.contains(item)) {
				this._selectedItems.remove(item);
				item._setSelected(false);
				this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_DESELECTED, ev, this._selByClickEv);
			} else {
				this._selectedItems.add(item);
				if (item._setSelected(true)) {
					this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_SELECTED, ev, this._selByClickEv);
				}
			}
		} else {
			// SHIFT KEY
		}
	}
};

DwtTree.prototype._itemDblClicked = 
function(item, ev) {
	this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_DBL_CLICKED, ev, this._selEv);
};

DwtTree.prototype._itemExpanded =
function(item, ev, skipNotify) {
	if (!skipNotify) {
		this._notifyListeners(DwtEvent.TREE, [item], DwtTree.ITEM_EXPANDED, ev, DwtShell.treeEvent);
	}
};

DwtTree.prototype._itemCollapsed =
function(item, ev, skipNotify) {
	var i;
	if (!skipNotify) {
		this._notifyListeners(DwtEvent.TREE, [item], DwtTree.ITEM_COLLAPSED, ev, DwtShell.treeEvent);
	}
	var setSelection = false;
	var a = this._selectedItems.getArray();
	var numSelectedItems = this._selectedItems.size();
	var da;
	var j = 0;
	for (i = 0; i < numSelectedItems; i++) {
		if (a[i]._isChildOf(item)) {
			setSelection = true;
			if (da == null) {
				da = new Array();
			}
			da[j++] = a[i];
			a[i]._setSelected(false);
			this._selectedItems.remove(a[i]);
		}		
	}

	if (da) {
		this._notifyListeners(DwtEvent.SELECTION, da, DwtTree.ITEM_DESELECTED, ev, this._selEv);
	}

	if (setSelection && !this._selectedItems.contains(item)) {
		if (item._setSelected(true)) {
			this._selectedItems.add(item);
			this._notifyListeners(DwtEvent.SELECTION, [item], DwtTree.ITEM_SELECTED, ev, this._selEv);
		}
	}
};

DwtTree.prototype._notifyListeners =
function(listener, items, detail, srcEv, destEv, kbNavEvent) {
	if (this.isListenerRegistered(listener)) {
		if (srcEv) {
			DwtUiEvent.copy(destEv, srcEv);
		}
		destEv.items = items;
		if (items.length == 1) {
			destEv.item = items[0];
		}
		destEv.detail = detail;
		destEv.kbNavEvent = kbNavEvent;
		this.notifyListeners(listener, destEv);
		if (listener == DwtEvent.SELECTION) {
			this.shell.notifyGlobalSelection(destEv);
		}
	}
};
