/**
 * This requires an "owner" which is the object that owns the full set of items, implmenting:
 * getItemCount() to return the number of items
 * getItem(index) to return the item at a given index.
 * 
 * And optionally implementing
 * itemSelectionChanged(item, index, isSelected) which is called
 *         for each item that is selected or deselected
 * selectionChanged() which is called after a batch of items have
 *         been selected or deselected with select()
 *
 */
	
LsSelectionManager = function(anOwner) {
	this._owner = anOwner;
};

// -----------------------------------------------------------
// Constants
// -----------------------------------------------------------

// Actions for select()
LsSelectionManager.SELECT_ONE_CLEAR_OTHERS = 0;
LsSelectionManager.TOGGLE_ONE_LEAVE_OTHERS = 1;
LsSelectionManager.SELECT_TO_ANCHOR = 2;
LsSelectionManager.DESELECT_ALL = 3;
LsSelectionManager.SELECT_ALL = 4;

// -----------------------------------------------------------
// API Methods
// -----------------------------------------------------------

/**
 * returns an LsVector
 */
LsSelectionManager.prototype.getItems = function() {
	if (this._selectedItems == null) {
		this._selectedItems = this._createItemsCollection();
	}
	return this._selectedItems;
};

/**
 * returns the number of selected items
 */	
LsSelectionManager.prototype.getLength = function() {
	return this.getItems().length;
};
	
/**
 * returns the anchor, unless nothing is selected
 */
LsSelectionManager.prototype.getAnchor = function() {
	if (this._anchor == null) {
		var items = this.getItems();
		if (items.length > 0) {
			this._anchor = items[0];
		}
	}
	return this._anchor;
};
    
/**
 * The cursor probably changes when the users navigates with 
 * the keyboard. This returns the item that is currently the cursor,
 * and null if nothing is selected.
 */
LsSelectionManager.prototype.getCursor = function() {
	if (this._cursor == null) {
		this._cursor = this.getAnchor();
	}
	return this._cursor;
};
    
    
/**
 * Returns true if the given item is selected.
 */
LsSelectionManager.prototype.isSelected = function(item) {
	return this.getItems().binarySearch(item) != -1;
};
    
LsSelectionManager.prototype.selectOneItem = function(item) {
	this.select(item, LsSelectionManager.SELECT_ONE_CLEAR_OTHERS);
};
    
LsSelectionManager.prototype.toggleItem = function(item) {
	this.select(item, LsSelectionManager.TOGGLE_ONE_LEAVE_OTHERS);
};
	
LsSelectionManager.prototype.selectFromAnchorToItem = function(item) {
	this.select(item, LsSelectionManager.SELECT_TO_ANCHOR);
};
    
LsSelectionManager.prototype.deselectAll = function() {
	this.select(null, LsSelectionManager.DESELECT_ALL);
};
	
LsSelectionManager.prototype.selectAll = function() {
	this.select(null, LsSelectionManager.SELECT_ALL);
};
    
    
/**
 * This method will notify the owner of any changes by calling
 * itemSelectionChanged() (if the owner defines it) for each item whose
 * selection changes and also by calling selectionChanged() (if the
 * owner defines it) once at the end, if anything changed selection.
 *
 */
LsSelectionManager.prototype.select = function(item, action) {
	
	// Update the anchor and cursor, if necessary
	this._setAnchorAndCursor(item, action);
    
	// save off the old set of selected items
	var oldItems = this._selectedItems;
	var oldItemsCount = (oldItems == null) ? 0 : oldItems.length;
	
	// create a fresh set of selected items
	this._selectedItems = null;
	this._selectedItems = this._createItemsCollection();
	
	// Now update the selection
	var itemCount = this._owner.getItemCount();
	var needsSort = false;
	var selectionChanged = false;
	var selecting = false;
	for (var i = 0; i < itemCount; ++i) {
		var testItem = this._owner.getItem(i);
		var oldSelectionExists = this._isItemOldSelection(testItem, oldItems);
		var newSelectionExists = oldSelectionExists;
		
		switch (action) {
		case LsSelectionManager.SELECT_TO_ANCHOR:
			if (this._anchor == null) {
				// If we have no anchor, let it be the first item
				// in the list
				this._anchor = testItem;
			}
			var atEdge = (testItem == this._anchor || testItem == item);
			var changed = false;
			// mark the beginning of the selection for the iteration
			if (!selecting && atEdge) {
				selecting = true;
				changed = true;
			}
			newSelectionExists = selecting;
			// mark the end of the selection if we're there
			if ((!changed || this._anchor == item) 
				&& selecting && atEdge) {
				selecting = false;
			}

			break;
		case LsSelectionManager.SELECT_ONE_CLEAR_OTHERS:
			newSelectionExists = (testItem == item);
			break;
		case LsSelectionManager.TOGGLE_ONE_LEAVE_OTHERS:
			if (testItem == item) {
				newSelectionExists = !oldSelectionExists ;
			}
			break;
		case LsSelectionManager.DESELECT_ALL:
			newSelectionExists = false;
			break;
		case LsSelectionManager.SELECT_ALL:
			newSelectionExists = true;
			break;
		}

		if (newSelectionExists) {
			this._selectedItems.add(testItem);
			needsSort = (this._selectedItems.length > 1);
		}

		if ( newSelectionExists != oldSelectionExists) {
			// Something changed so notify the owner.
			if (this._owner.itemSelectionChanged != null) {
				this._owner.itemSelectionChanged(testItem, 
												 i, newSelectionExists);
			}
			selectionChanged = true;
		}
	}
	selectionChanged = selectionChanged || (oldItemsCount != 
											this._selectedItems.length);

	if (needsSort) this._selectedItems.sort();
	
	if (selectionChanged && this._owner.selectionChanged != null) {
		this._owner.selectionChanged(item);
	}
};

/**
 * Remove an item from the selection managers selected items
 * collection if it exists.
 */
LsSelectionManager.prototype.removeItem = function(item) {
	if (this._selectedItems) {
		var index = this._selectedItems.binarySearch(item);
		if (index > -1) this._selectedItems.removeAt(index);
	}
};

// -----------------------------------------------------------
// Internal Methods
// -----------------------------------------------------------
	
/**
 * Creates an array suitable for use as the sorted list of selected
 * items and returns it.
 */
LsSelectionManager.prototype._createItemsCollection = function() {
	return new LsVector();
};

LsSelectionManager.prototype._isItemOldSelection = function (testItem, oldItems) {
	var ret = false;
	if (oldItems) {
		var oldSelectionIndex = oldItems.binarySearch(testItem);
		if (oldSelectionIndex > -1) {
			oldItems.removeAt(oldSelectionIndex);
		}
		ret = (oldSelectionIndex != -1);
	}
	return ret;
};

LsSelectionManager.prototype._setAnchorAndCursor = function (item, action) {
	switch (action) {
	case LsSelectionManager.SELECT_TO_ANCHOR:
		this._cursor = item;
		break;
	case LsSelectionManager.SELECT_ONE_CLEAR_OTHERS:		
		this._anchor = item;
		this._cursor = item;
		break;
	case LsSelectionManager.TOGGLE_ONE_LEAVE_OTHERS:
		this._anchor = item;
		this._cursor = item;
		break;
	case LsSelectionManager.DESELECT_ALL:
		this._anchor = null;
		this._cursor = null;
		break;
	case LsSelectionManager.SELECT_ALL:
		return;
	}
};
