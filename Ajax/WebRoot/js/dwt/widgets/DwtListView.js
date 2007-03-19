/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

function DwtListView(parent, className, posStyle, headerList, noMaximize) {

	if (arguments.length == 0) return;
	className = className || "DwtListView";
	DwtComposite.call(this, parent, className, posStyle);

	if (headerList) {
		var htmlElement = this.getHtmlElement();

		this._listColDiv = document.createElement("div");
		this._listColDiv.id = Dwt.getNextId();
		this._listColDiv.className = "DwtListView-ColHeader";
		htmlElement.appendChild(this._listColDiv);

		this._listDiv = document.createElement("div");
		this._listDiv.id = Dwt.getNextId();
		this._listDiv.className = "DwtListView-Rows";
		htmlElement.appendChild(this._listDiv);

		// setup vars needed for sorting
		this._bSortAsc = false;
		this._currentColId = null;
		this._sortingEnabled = true;
	} else {
		this.setScrollStyle(DwtControl.SCROLL); // auto scroll
	}
		
	this._setMouseEventHdlrs();

	this._listenerMouseOver = new AjxListener(this, this._mouseOverListener);
	this._listenerMouseOut = new AjxListener(this, this._mouseOutListener);
	this._listenerMouseDown = new AjxListener(this, this._mouseDownListener);
	this._listenerMouseUp = new AjxListener(this, this._mouseUpListener);
	this._listenerMouseMove = new AjxListener(this, this._mouseMoveListener);
	this._listenerDoubleClick = new AjxListener(this, this._doubleClickListener);
	this.addListener(DwtEvent.ONMOUSEOVER, this._listenerMouseOver);
	this.addListener(DwtEvent.ONMOUSEOUT, this._listenerMouseOut);
	this.addListener(DwtEvent.ONMOUSEDOWN, this._listenerMouseDown);
	this.addListener(DwtEvent.ONMOUSEUP, this._listenerMouseUp);
	this.addListener(DwtEvent.ONMOUSEMOVE, this._listenerMouseMove);
	this.addListener(DwtEvent.ONDBLCLICK, this._listenerDoubleClick);

	this._evtMgr = new AjxEventMgr();
	this._selectedItems = new AjxVector();
	this._selAnchor = null; 
	this._kbAnchor = null; 
	this._selEv = new DwtSelectionEvent(true);
	this._actionEv = new DwtListViewActionEvent(true);
	this._stateChangeEv = new DwtEvent(true);
	this._headerList = headerList;
	this._noMaximize = noMaximize;
	this._parentEl = this._headerList ? this._listDiv : this.getHtmlElement();
	
	this._list = null;
	this._offset = 0;
	this._headerColCreated = false;
	this._firstSelIndex = -1;
	this._tmpPoint = new DwtPoint(0, 0);

	this._clearRightSelAction = new AjxTimedAction(this, this._clearRightSel);

	this.setMultiSelect(true);
};

DwtListView.ITEM_SELECTED 		= 1;
DwtListView.ITEM_DESELECTED 	= 2;
DwtListView.ITEM_DBL_CLICKED 	= 3;
DwtListView._LAST_REASON 		= 3;

DwtListView._TOOLTIP_DELAY 		= 250;

DwtListView.HEADERITEM_HEIGHT 	= 23;
DwtListView.HEADERITEM_ARROW  	= "arr--";
DwtListView.HEADER_ID			= "crr--";
DwtListView.HEADERITEM_LABEL 	= "drr--";

DwtListView.TYPE_HEADER_ITEM 	= "1";
DwtListView.TYPE_LIST_ITEM 		= "2";
DwtListView.TYPE_HEADER_SASH 	= "3";

DwtListView.DEFAULT_LIMIT = 25;
DwtListView.MAX_REPLENISH_THRESHOLD = 10;
DwtListView.MIN_COLUMN_WIDTH = 10;
DwtListView.COL_MOVE_THRESHOLD = 3;

DwtListView._STYLE_CLASS				= "_sc";
DwtListView._SELECTED_STYLE_CLASS		= "_ssc";
DwtListView._SELECTED_DIS_STYLE_CLASS	= "_sdsc"
DwtListView._KBFOCUS_CLASS				= "_kfc";

DwtListView.prototype = new DwtComposite;
DwtListView.prototype.constructor = DwtListView;

DwtListView.prototype.toString = 
function() {
	return "DwtListView";
};

DwtListView.prototype.setEnabled = 
function(enabled) {
	DwtComposite.prototype.setEnabled.call(this, enabled);
	// always remove listeners to avoid adding listeners multiple times
	this.removeListener(DwtEvent.ONMOUSEOVER, this._listenerMouseOver);
	this.removeListener(DwtEvent.ONMOUSEOUT, this._listenerMouseOut);
	this.removeListener(DwtEvent.ONMOUSEDOWN, this._listenerMouseDown);
	this.removeListener(DwtEvent.ONMOUSEUP, this._listenerMouseUp);
	this.removeListener(DwtEvent.ONMOUSEMOVE, this._listenerMouseMove);
	this.removeListener(DwtEvent.ONDBLCLICK, this._listenerDoubleClick);
	// now re-add listeners, if needed
	if (enabled) {
		this.addListener(DwtEvent.ONMOUSEOVER, this._listenerMouseOver);
		this.addListener(DwtEvent.ONMOUSEOUT, this._listenerMouseOut);
		this.addListener(DwtEvent.ONMOUSEDOWN, this._listenerMouseDown);
		this.addListener(DwtEvent.ONMOUSEUP, this._listenerMouseUp);
		this.addListener(DwtEvent.ONMOUSEMOVE, this._listenerMouseMove);
		this.addListener(DwtEvent.ONDBLCLICK, this._listenerDoubleClick);
	}
	// modify selection classes
	var selection = this.getSelectedItems();
	if (selection) {
		var elements = selection.getArray();
		for (var i = 0; i < elements.length; i++) {
			var element = elements[i];
			element.className = enabled 
				? Dwt.getAttr(element, DwtListView._SELECTED_STYLE_CLASS) 
				: Dwt.getAttr(element, DwtListView._SELECTED_DIS_STYLE_CLASS);
		}
	}
};

DwtListView.prototype.createHeaderHtml = 
function(defaultColumnSort) {

	// does this list view have headers or have they already been created?
	if (!this._headerList || this._headerColCreated)
		return;
	
	var idx = 0;
	var htmlArr = new Array();
	this._headerTableId = DwtListView.HEADER_ID + Dwt.getNextId();

	htmlArr[idx++] = "<table id='";
	htmlArr[idx++] = this._headerTableId;
	htmlArr[idx++] = "' cellpadding=0 cellspacing=0 border=0 height=100%";
	htmlArr[idx++] = this._noMaximize ? ">" : " width=100%>";
	htmlArr[idx++] = "<tr>";
	var numCols = this._headerList.length;
	for (i = 0; i < numCols; i++) {
		var headerCol = this._headerList[i];
		if (!headerCol._visible)
			continue;
			
		htmlArr[idx++] = "<td id='";
		htmlArr[idx++] = headerCol._id;
		htmlArr[idx++] = "' class='";
		htmlArr[idx++] = headerCol._id == this._currentColId
			? "DwtListView-Column DwtListView-ColumnActive'"
			: "DwtListView-Column'";
		if (headerCol._width) {
			htmlArr[idx++] = " width=";
			htmlArr[idx++] = headerCol._width;
			if (headerCol._widthUnits)
				htmlArr[idx++] = headerCol._widthUnits;
		}
		htmlArr[idx++] = ">";
		// must add a div to force clipping :(
		htmlArr[idx++] = "<div";
		var headerColWidth = null;
		if (headerCol._width && headerCol._width != "auto") {
			headerColWidth = headerCol._width + 2
			if (headerCol._widthUnits)
				headerColWidth += headerCol._widthUnits;
		}
		if (!!headerColWidth) {
			htmlArr[idx++] = " style='overflow: hidden; width: ";
			htmlArr[idx++] = headerColWidth;
			htmlArr[idx++] = "'>";
		} else {
			htmlArr[idx++] = ">";
		}

		// add new table for icon/label/sorting arrow		
		htmlArr[idx++] = "<table border=0 cellpadding=0 cellspacing=0 width=100%><tr>";
		if (headerCol._iconInfo) {
			htmlArr[idx++] = "<td><center>";
			htmlArr[idx++] = AjxImg.getImageHtml(headerCol._iconInfo);
			htmlArr[idx++] = "</center></td>";
		}
			
		if (headerCol._label) {
			htmlArr[idx++] = "<td id='";
			htmlArr[idx++] = DwtListView.HEADERITEM_LABEL;
			htmlArr[idx++] = headerCol._id;
			htmlArr[idx++] = "'>&nbsp;";
			htmlArr[idx++] = headerCol._label;
			htmlArr[idx++] = "</td>";
		}
		
		if (headerCol._sortable) {
			var arrowIcon = this._bSortAsc ? "ColumnUpArrow" : "ColumnDownArrow";
			this._currentColId = headerCol._id;
			htmlArr[idx++] = "<td width=10 id='";
			htmlArr[idx++] = DwtListView.HEADERITEM_ARROW;
			htmlArr[idx++] = headerCol._id;
			htmlArr[idx++] = "'>";
			htmlArr[idx++] = headerCol._sortable == defaultColumnSort
				? AjxImg.getImageHtml(arrowIcon)
				: AjxImg.getImageHtml(arrowIcon, "visibility:hidden");
			htmlArr[idx++] = "</td>";
		}
		
		// ALWAYS add "sash" separators
		if (i < (numCols - 1)) {
			htmlArr[idx++] = "<td width=4>";
			htmlArr[idx++] = "<table align=right border=0 cellpadding=0 cellspacing=0 width=2 height=100%><tr>";
			htmlArr[idx++] = "<td class='DwtListView-Sash'><div style='width: 1px; height: ";
			htmlArr[idx++] = (DwtListView.HEADERITEM_HEIGHT-2);
			htmlArr[idx++] = "px; background-color: #8A8A8A'></div></td>";
			htmlArr[idx++] = "<td class='DwtListView-Sash'><div style='width: 1px; height: ";
			htmlArr[idx++] = (DwtListView.HEADERITEM_HEIGHT-2);
			htmlArr[idx++] = "px; background-color: #FFFFFF'></div></td>";
			htmlArr[idx++] = "</tr></table>";
			htmlArr[idx++] = "</td>";
		}

		htmlArr[idx++] = "</tr></table>";
		htmlArr[idx++] = "</div></td>";
	}
	htmlArr[idx++] = "</tr></table>";

	this._listColDiv.innerHTML = htmlArr.join("");

	// for each sortable column, sets its identifier
	for (var j = 0; j < this._headerList.length; j++) {
		var cell = document.getElementById(this._headerList[j]._id);
		if (cell == null) continue;
		
		var sortable = this._headerList[j]._sortable;
		if (sortable && sortable == defaultColumnSort)
			cell.className = "DwtListView-Column DwtListView-ColumnActive";

		var isResizeable = this._headerList[j]._resizeable;
		if (isResizeable) {
			// always get the sibling cell to the right
			var sashCell = cell.firstChild.firstChild.rows[0].lastChild;
			if (sashCell) {
				sashCell._type = DwtListView.TYPE_HEADER_SASH;
				sashCell._itemIndex = j + "--sash";
			}
		}

		cell._isSortable = sortable != null;
		cell._isResizeable = isResizeable;
		cell._type = DwtListView.TYPE_HEADER_ITEM;
		cell._itemIndex = j;
	}
	
	this._headerColCreated = true;
};

// this returns the index into the header list array for the given Id
DwtListView.prototype.getColIndexForId = 
function(headerId) {
	if (this._headerList) {
		for (var i = 0; i < this._headerList.length; i++) {
			if (this._headerList[i]._id.indexOf(headerId) != -1)
				return i;
		}
	}
	return -1;
};

/**
* Creates a list view out of the given vector of items. The derived class should override _createItemHtml()
* in order to display an item.
*
* @param list	a vector of items (AjxVector)
* @param defaultColumnSort	default column field to sort (optional)
*/
DwtListView.prototype.set =
function(list, defaultColumnSort) {
	this._selectedItems.removeAll();
	this.enableSorting(true);
	this._resetList();
	this._list = list;
	this._now = new Date();
	this.setUI(defaultColumnSort);
}

/**
* Renders the list view using the current list of items.
*
* @param defaultColumnSort		[string]	ID of column that represents default sort order
* @param noResultsOk			[boolean]*	if true, don't show "No Results" for empty list
*/
DwtListView.prototype.setUI =
function(defaultColumnSort, noResultsOk) {
	this.removeAll();
	this.createHeaderHtml(defaultColumnSort);
	this._renderList(this._list, noResultsOk);
};

DwtListView.prototype._renderList =
function(list, noResultsOk) {
	if (list instanceof AjxVector && list.size()) {
		var size = list.size();
		for (var i = 0; i < size; i++) {
			var item = list.get(i);
			var div = this._createItemHtml(item, this._now);
			if (div) {
				if (div instanceof Array) {
					for (var j = 0; j < div.length; j++){
						this._addRow(div[j]);
					}
				} else {
					this._addRow(div);
				}
			}
		}
	} else if (!noResultsOk) {
		this._setNoResultsHtml();
	}
};

DwtListView.prototype.addItems =
function(itemArray) {
	if (AjxUtil.isArray(itemArray)){
		if (!this._list) {
			this._list = new AjxVector();
		}
	
		// clear the "no results" message before adding!
		if (this._list.size() == 0) {
			this._resetList();
		}
		var currentSize = this._list.size();
		var vec = AjxVector.fromArray(itemArray);
		this._renderList(vec);
		this._list.addList(itemArray);
	}
};

DwtListView.prototype.addItem =
function(item, index, skipNotify) {
	if (!this._list)
		this._list = new AjxVector();
	
	// clear the "no results" message before adding!
	if (this._list.size() == 0)
		this._resetList();
	
	this._list.add(item, index);
	var div = this._createItemHtml(item, this._now);
	if (div) {
		if (div instanceof Array) {
			for (var j = 0; j < div.length; j++)
				this._addRow(div[j]);
		} else {
			this._addRow(div, index);
		}
	}
		
	if (!skipNotify && this._evtMgr.isListenerRegistered(DwtEvent.STATE_CHANGE)) {
		this._evtMgr.notifyListeners(DwtEvent.STATE_CHANGE, this._stateChangeEv);
	}
};

DwtListView.prototype.removeItem =
function(item, skipNotify) {
	var itemEl = this._getElFromItem(item);
	this._selectedItems.remove(itemEl);
	this._parentEl.removeChild(itemEl);
	this._list.remove(item);
		
	if (!skipNotify && this._evtMgr.isListenerRegistered(DwtEvent.STATE_CHANGE)) {
		this._evtMgr.notifyListeners(DwtEvent.STATE_CHANGE, this._stateChangeEv);
	}
};

DwtListView.prototype.removeLastItem =
function(skipNotify) {
	var last = this._list.get(this._list.size() - 1);
	this._list.remove(last);
	this._parentEl.removeChild(this._getElFromItem(last));
		
	if (!skipNotify && this._evtMgr.isListenerRegistered(DwtEvent.STATE_CHANGE)) {
		this._evtMgr.notifyListeners(DwtEvent.STATE_CHANGE, this._stateChangeEv);
	}
};

DwtListView.prototype.redrawItem = function(item) {
    var odiv = this._getElFromItem(item);
    if (odiv) {
        var ndiv = this._createItemHtml(item, true); // now???
        this.associateItemWithElement(item, ndiv);
        odiv.parentNode.replaceChild(ndiv, odiv);

        var selection = this.getSelectedItems().getArray();
        for (var i = 0; i < selection.length; i++) {
            var sitem = selection[i];
            if (sitem === item) {
                this.setSelectedItems([].concat(selection));
                break;
            }
        }
    }
};

DwtListView.prototype.reIndexColumn =
function(columnIdx, newIdx) {
	// do some sanity checks before continuing
	if (this._headerList == null) 
		return;
	var len = this._headerList.length;
	if (columnIdx < 0 || newIdx < 0 || columnIdx >= len || newIdx >= len || columnIdx == newIdx)
		return;

	// reindex the header list
	var temp = this._headerList.splice(columnIdx, 1);
	this._headerList.splice(newIdx, 0, temp[0]);
	
	// finally, relayout the list view (incl. header columns)
	this._relayout();
};

DwtListView.prototype.reSizeColumn = 
function(headerIdx, newWidth) {
	// TODO: do some (more?) sanity checks before changing the header width
	if (newWidth == this._headerList._width || newWidth < DwtListView.MIN_COLUMN_WIDTH)
		return;

	this._headerList[headerIdx]._width = newWidth;
	this._relayout();
};

// determine if col header needs padding to accomodate for scrollbars
DwtListView.prototype._resetColWidth =
function() {
	if (this._headerList == null)
		return;
	
	// dynamically get col idx for last column (b/c col may or may not be turned on)
	var count = this._headerList.length-1;
	var lastColIdx = null;
	while (lastColIdx == null && count >= 0) {
		if (this._headerList[count]._visible)
			lastColIdx = count;
		count--;
	}

	var lastCol = this._headerList[lastColIdx];
	var lastCell = document.getElementById(lastCol._id);
	var div = lastCell.firstChild;
	var scrollbarPad = 16;
	
	var headerWidth = this._listColDiv.clientWidth;
	var rowWidth = this._listDiv.clientWidth;
	
	if (headerWidth != rowWidth) {
		lastCell.style.width = div.style.width = lastCol._width != null && lastCol._width != "auto" 
			? (lastCol._width + scrollbarPad)
			: (lastCell.clientWidth + scrollbarPad);
	} else {
		lastCell.style.width = div.style.width = (lastCol._width || "");
	}
};

DwtListView.prototype.size =
function() {
	return this._list ? this._list.size() : 0;
};

DwtListView.prototype.setMultiSelect = 
function (enabled) {
	this._multiSelectEnabled = enabled;
};

DwtListView.prototype.isMultiSelectEnabled =
function () {
	return this._multiSelectEnabled;
};

// safari breaks w/ clicking on scrollbar in list views so we do this:
DwtListView.prototype._getPropagationForEvent = 
function() {
	if (AjxEnv.isSafari) {
		return false;
	} else {
		return DwtControl.prototype._getPropagationForEvent.call(this);
	}
};

// safari breaks w/ clicking on scrollbar in list views so we do this:
DwtListView.prototype._getReturnValueForEvent = 
function() {
	if (AjxEnv.isSafari) {
		return true;
	} else {
		return DwtControl.prototype._getReturnValueForEvent.call(this);
	}
};

DwtListView.prototype._addRow =
function(row, index) {
	// bug fix #1894 - check for childNodes length otherwise IE barfs
	var len = this._parentEl.childNodes.length;
	if (index != null && len > 0 && index != len)
		this._parentEl.insertBefore(row, this._parentEl.childNodes[index]);
	else
		this._parentEl.appendChild(row);
};

/**
* Renders a single item as a DIV element.
*
* Default implementation creates a simple div with the innerHTML set to 
* the string value of the item.
*/
DwtListView.prototype._createItemHtml = 
function(item, now, isDnDIcon) {
	var div = document.createElement("DIV");
	div.id = Dwt.getNextId();
	var rowClassName = AjxBuffer.concat(this._className, "Row");
	div[DwtListView._STYLE_CLASS] = AjxBuffer.concat("Row ", rowClassName);
	div[DwtListView._SELECTED_STYLE_CLASS] = AjxBuffer.concat("Row-", DwtCssStyle.SELECTED, " ", rowClassName);
	div[DwtListView._SELECTED_DIS_STYLE_CLASS] = AjxBuffer.concat("Row-", DwtCssStyle.SELECTED, "-" , DwtCssStyle.DISABLED, " ", rowClassName);
	div.className = div[DwtListView._STYLE_CLASS];
	if (typeof(item) == "object") {
		div.innerHTML = AjxStringUtil.htmlEncode(item.toString());
	} else {
		div.innerHTML = AjxStringUtil.htmlEncode(String(item));
	}
	this.associateItemWithElement(item, div, DwtListView.TYPE_LIST_ITEM);
	return div;
};

DwtListView.prototype._setNoResultsHtml = 
function() {
	var htmlArr = new Array(3);
	var idx = 0;

	htmlArr[idx++] = "<table width='100%' cellspacing='0' cellpadding='1'><tr><td class='NoResults'><br>";
	htmlArr[idx++] = AjxMsg.noResults;
	htmlArr[idx++] = "</td></tr></table>";

	var	div = document.createElement("div");
	div.innerHTML = htmlArr.join("");
	this._addRow(div);
};

DwtListView.prototype.addSelectionListener = 
function(listener) {
	this._evtMgr.addListener(DwtEvent.SELECTION, listener);
}

DwtListView.prototype.removeSelectionListener = 
function(listener) {
	this._evtMgr.removeListener(DwtEvent.SELECTION, listener);    	
}

DwtListView.prototype.addActionListener = 
function(listener) {
	this._evtMgr.addListener(DwtEvent.ACTION, listener);
}

DwtListView.prototype.removeActionListener = 
function(listener) {
	this._evtMgr.removeListener(DwtEvent.ACTION, listener);    	
}

DwtListView.prototype.addStateChangeListener =
function(listener) {
	this._evtMgr.addListener(DwtEvent.STATE_CHANGE, listener);
}

DwtListView.prototype.removeStateChangeListener =
function(listener) {
	this._evtMgr.removeListener(DwtEvent.STATE_CHANGE, listener);
}

DwtListView.prototype.removeAll =
function(skipNotify) {
	this._parentEl.innerHTML = "";
	this._selectedItems.removeAll();
	this._selAnchor = this._kbAnchor = null;
		
	if (!skipNotify && this._evtMgr.isListenerRegistered(DwtEvent.STATE_CHANGE)) {
		this._evtMgr.notifyListeners(DwtEvent.STATE_CHANGE, this._stateChangeEv);
	}
}

DwtListView.prototype.deselectAll =
function() {
	var a = this._selectedItems.getArray();
	var sz = this._selectedItems.size();
	for (var i = 0; i < sz; i++)
		a[i].className = Dwt.getAttr(a[i], DwtListView._STYLE_CLASS);
	this._selectedItems.removeAll();
	this._selAnchor = null;
	
	if (this._kbAnchor != null && this.hasFocus())
		this._kbAnchor.className += " " + Dwt.getAttr(this._kbAnchor, DwtListView._KBFOCUS_CLASS);
}

DwtListView.prototype.getDnDSelection =
function() {
	if (this._dndSelection instanceof AjxVector) {
		return this.getSelection();
	} else {
		return AjxCore.objectWithId(this._dndSelection);
	}
}

DwtListView.prototype.getSelection =
function() {
	var a = new Array();
	if (this._rightSelItems) {
		a.push(AjxCore.objectWithId(Dwt.getAttr(this._rightSelItems, "_itemIndex")));
	} else {
		var sa = this._selectedItems.getArray();
		var saLen = this._selectedItems.size();
		for (var i = 0; i < saLen; i++)
			a[i] = AjxCore.objectWithId(Dwt.getAttr(sa[i], "_itemIndex"));
	}
	return a;
}

DwtListView.prototype.getSelectedItems =
function() {
	return this._selectedItems;
}

DwtListView.prototype.setSelection =
function(item, skipNotify) {
	var el = this._getElFromItem(item);
	if (el) {
		var i;
		this._deselectAllSelectedItems();
		this._unmarkKbAnchorElement(true);
		this._selectedItems.add(el);
		this._selAnchor = this._kbAnchor = el;
		el.className = this.getEnabled() 
			? Dwt.getAttr(el, DwtListView._SELECTED_STYLE_CLASS) 
			: Dwt.getAttr(el, DwtListView._SELECTED_DIS_STYLE_CLASS);
			
		if (this.hasFocus())
			el.className += " " + Dwt.getAttr(el, DwtListView._KBFOCUS_CLASS);

		// reset the selected index
		this._firstSelIndex = this._list && this._list.size() > 0
			? this._list.indexOf(item) : -1;

		if (!skipNotify && this._evtMgr.isListenerRegistered(DwtEvent.SELECTION)) {
			var selEv = new DwtSelectionEvent(true);
			selEv.button = DwtMouseEvent.LEFT;
			selEv.target = el;
			selEv.item = AjxCore.objectWithId(Dwt.getAttr(el, "_itemIndex"));
			selEv.detail = DwtListView.ITEM_SELECTED;
			this._evtMgr.notifyListeners(DwtEvent.SELECTION, selEv);
		}	
	}
}

DwtListView.prototype._deselectAllSelectedItems =
function() {
	var a = this._selectedItems.getArray();
	var sz = this._selectedItems.size();
	for (i = 0; i < sz; i++) {
		a[i].className = Dwt.getAttr(a[i], DwtListView._STYLE_CLASS);
		if (this._kbAnchor == a[i] && this.hasFocus())
			a[i].className += " " + Dwt.getAttr(a[i], DwtListView._KBFOCUS_CLASS);
	}
	this._selectedItems.removeAll();
};

DwtListView.prototype.setSelectedItems =
function(selectedArray) {
	this._deselectAllSelectedItems();
	var i, sz, el;
	sz = selectedArray.length;
	for (i = 0; i < sz; ++i) {
		el = this._getElFromItem(selectedArray[i]);
		if (el) {
			el.className = this.getEnabled() 
				? Dwt.getAttr(el, DwtListView._SELECTED_STYLE_CLASS)
				: Dwt.getAttr(el, DwtListView._SELECTED_DIS_STYLE_CLASS);
			if (this._kbAnchor == el && this.hasFocus())
				el.className += " " + Dwt.getAttr(el, DwtListView._KBFOCUS_CLASS);
			this._selectedItems.add(el);
		}
	}
};

DwtListView.prototype.getSelectionCount =
function() {
	return this._rightSelItems ? 1 : this._selectedItems.size();
}

DwtListView.prototype._clearRightSel =
function() {
	if (this._rightSelItems) {
		this._rightSelItems.className = Dwt.getAttr(this._rightSelItems, DwtListView._STYLE_CLASS);
		this._rightSelItems = null;
	}
};

DwtListView.prototype.handleActionPopdown = 
function() {
	AjxTimedAction.scheduleAction(this._clearRightSelAction, 0);
};

DwtListView.prototype._getItemId =
function(item) {
	return item ? ([this._getViewPrefix(), item.id].join("")) : null;
}

DwtListView.prototype._getHeaderTableId = 
function() {
	return this._headerList ? this._headerTableId : null;
}

DwtListView.prototype._getElFromItem = 
function(item) {
	var childNodes = this._parentEl.childNodes;
	var len = childNodes.length;
	var comparisonId = this._getItemId(item);
	for (var i = 0; i < len; i++) {
		if (childNodes[i].id == comparisonId)
			return childNodes[i];
	}
	return null;
}

DwtListView.prototype._getItemIndex = 
function(item) {
	var list = this._list;
	var len = list.size();
	for (var i = 0; i < len; ++i) {
		if (list.get(i).id == item.id) {
			return i;
		}
	}
}

DwtListView.prototype.getItemFromElement =
function(element) {
	var itemIdx = Dwt.getAttr(element, "_itemIndex");
	if (itemIdx !== void 0) {
		if (Dwt.getAttr(element, "_type") == DwtListView.TYPE_LIST_ITEM)
			return AjxCore.objectWithId(itemIdx);
	}
	return null;
}

DwtListView.prototype._getViewPrefix = 
function() {
	return this._viewPrefix;
}

DwtListView.prototype.associateItemWithElement =
function(item, element, type, optionalId) {
	element.id = optionalId || this._getItemId(item);
	element._itemIndex = AjxCore.assignId(item);
	element._type = type;
}

/* Return true only if the event occurred in one of our Divs
 * See DwtControl for more info */
DwtListView.prototype._isValidDragObject =
function(ev) {
	var div = ev.target;
	div = Dwt.findAncestor(div, "_itemIndex");
	return (div != null);
}

DwtListView.prototype.dragSelect =
function(row) {
	// If we have something previously selected, try and remove the selection
	if (this._dragHighlight != null) {
		var oldRow = document.getElementById(this._dragHighlight);
		// only go forward if the row doesn't exist, or if the new selection
		// is different from the old selection.
		// In the case where a header item is dragged over, the row might be 
		// null or void.
		if (!row || row.id != oldRow.id){
			this._updateDragSelection(oldRow, false);
		}
	}
	// Don't try and select if we are over a header item
	if (!row || Dwt.getAttr(row, "_type") != DwtListView.TYPE_LIST_ITEM) return;
	
	// Try and select only if the new row is different from the currently
	// highlighted row.
	if (row.id != this._dragHighlight){
		this._dragHighlight = row.id;
		this._updateDragSelection(row, true);
	}
}

DwtListView.prototype.dragDeselect =
function(row) {
	if (this._dragHighlight) {
		var oldRow = document.getElementById(this._dragHighlight);
		this._updateDragSelection(oldRow, false);
		this._dragHighlight = null;
	}
}

DwtListView.prototype._updateDragSelection =
function(row, select) {
	if (!select) {
		row.className = row._dwtListViewOldClassName;
	} else {
		row._dwtListViewOldClassName = row.className;
		row.className = row.className + "-drag";
	}
}

DwtListView.prototype._mouseOverAction = 
function(mouseEv, div) {
	var type = Dwt.getAttr(div, "_type");
	if ((type == DwtListView.TYPE_HEADER_ITEM) && this._sortingEnabled && div._isSortable && this._headerClone == null) {
		div.className = "DwtListView-Column DwtListView-ColumnHover";
	} else if (type == DwtListView.TYPE_HEADER_SASH) {
		div.style.cursor = AjxEnv.isIE ? "col-resize" : "e-resize";
	} else if (type == DwtListView.TYPE_LIST_ITEM) {
		if (div._hoverStyleClass == null || div == this._rightSelItems) {
			div.hoverSet = false;
		} else {
			var selItems = this._selectedItems.getArray();
			div.hoverSet = true;
			for (var i = 0; i < selItems.length; i++) {
				if (div == selItems[i]) {
					div.hoverSet = false;
					break;
				}
			}
		}
		if (div.hoverSet)
			div.className += " " + div._hoverStyleClass;
	}

	return true;
}

DwtListView.prototype._mouseOutAction = 
function(mouseEv, div) {
	var type = Dwt.getAttr(div, "_type");
	if (type == DwtListView.TYPE_HEADER_ITEM && this._headerClone == null) {
		div.className = div.id != this._currentColId 
			? "DwtListView-Column" 
			: "DwtListView-Column DwtListView-ColumnActive";
	} else if (type == DwtListView.TYPE_HEADER_SASH) {
		div.style.cursor = "auto";
	} else if (type == DwtListView.TYPE_LIST_ITEM) {
		if (div._hoverStyleClass && div.hoverSet)
			div.className = Dwt.getAttr(div, DwtListView._STYLE_CLASS);
	}

	return true;
}

DwtListView.prototype._mouseOverListener = 
function(ev) {
	var div = ev.target;
	div = Dwt.findAncestor(div, "_itemIndex");
	if (!div)
		return;
	
	this._mouseOverAction(ev, div);
}

DwtListView.prototype._mouseOutListener = 
function(ev) {
	var div = ev.target;
	div = Dwt.findAncestor(div, "_itemIndex");
	if (!div)
		return;
	// NOTE: The DwtListView handles the mouse events on the list items
	//		 that have associated tooltip text. Therefore, we must
	//		 explicitly null out the tooltip content whenever we handle
	//		 a mouse out event. This will prevent the tooltip from
	//		 being displayed when we re-enter the listview even though
	//		 we're not over a list item.
	this.setToolTipContent(null);
	this._mouseOutAction(ev, div);
}

DwtListView.prototype._mouseMoveListener = 
function(ev) {
	if (this._clickDiv == null)
		return;

	var type = Dwt.getAttr(this._clickDiv, "_type");
	if (type == DwtListView.TYPE_HEADER_ITEM) {
		this._handleColHeaderMove(ev);
	} else if (type == DwtListView.TYPE_HEADER_SASH) {
		this._handleColHeaderResize(ev);
	}
}

DwtListView.prototype._mouseUpAction = 
function(mouseEv, div) {
	return true;
}

DwtListView.prototype._mouseDownListener = 
function(ev) {
	var div = ev.target;
	div = Dwt.findAncestor(div, "_itemIndex");

	if (div == null) {
		this._dndSelection = null;
	} else {
		this._clickDiv = div;

		if (Dwt.getAttr(div, "_type") != DwtListView.TYPE_LIST_ITEM) {
			this._dndSelection = null;
		} else {
			this._dndSelection = (this._selectedItems.contains(div)) 
				? this._selectedItems 
				: Dwt.getAttr(div, "_itemIndex");
		}
	}
	this._mouseDownAction(ev, div);
};

DwtListView.prototype._mouseDownAction = 
function(mouseEv, div) {
	return true;
};

DwtListView.prototype._mouseUpListener = 
function(ev) {
	var div = ev.target;
	div = Dwt.findAncestor(div, "_itemIndex");

	var wasDraggingCol = this._handleColHeaderDrop(ev);
	var wasDraggingSash = this._handleColSashDrop(ev);
	
	if (!div || div != this._clickDiv || wasDraggingCol || wasDraggingSash) 
	{
		delete this._clickDiv;
		return;
	}
	delete this._clickDiv;

	var type = Dwt.getAttr(div, "_type");
	if (this._headerList && type == DwtListView.TYPE_HEADER_ITEM) {
		if (div._isSortable && this._sortingEnabled && ev.button == DwtMouseEvent.LEFT) {
			this._columnClicked(div, ev);
		} else if (ev.button == DwtMouseEvent.RIGHT) {
			var actionMenu = this._getActionMenuForColHeader();
			if (actionMenu && actionMenu instanceof DwtMenu)
				actionMenu.popup(0, ev.docX, ev.docY);
		}
	} else if (type == DwtListView.TYPE_LIST_ITEM) {
		// set item selection, then hand off to derived class for handling
		if (ev.button == DwtMouseEvent.LEFT || ev.button == DwtMouseEvent.RIGHT)
			this._itemClicked(div, ev);
			
		if (!this._mouseUpAction(ev, div))
			return;
	}
}

DwtListView.prototype._doubleClickAction = 
function(mouseEv, div) {return true;}

DwtListView.prototype._doubleClickListener =
function(ev) {
	var div = ev.target;
	div = Dwt.findAncestor(div, "_itemIndex");

	if (!div) return;

	if (Dwt.getAttr(div, "_type") == DwtListView.TYPE_LIST_ITEM) {
		if (!this._doubleClickAction(ev, div))
			return;
		if (this._evtMgr.isListenerRegistered(DwtEvent.SELECTION)) {
			DwtUiEvent.copy(this._selEv, ev);
			this._selEv.item = this.getItemFromElement(div);
			this._selEv.detail = DwtListView.ITEM_DBL_CLICKED;
			this._evtMgr.notifyListeners(DwtEvent.SELECTION, this._selEv);
		}
	}
}

DwtListView.prototype.emulateDblClick = 
function(item) {
	var div = document.getElementById(this._getItemId(item));
	if (div)
		this._emulateDblClick(div);
}

DwtListView.prototype._emulateDblClick = 
function(target) {
	var mev = DwtShell.mouseEvent;
	mev.reset();
	mev.target = target;
	mev.button = DwtMouseEvent.LEFT;
	mev.ersatz = true;
	this._itemClicked(target, mev);
	this._doubleClickListener(mev);
}

DwtListView.prototype._selectItem =
function(next, addSelect) {
	// If there are no elements in the list, then bail
	if (!this.size())
		return;
		
	// if there is currently a selection anchor, then find the next/prev item
	// from the anchor
	var itemDiv;
	if (this._kbAnchor) {
		itemDiv = this._getSiblingElement(this._kbAnchor, next);
	} else {
		itemDiv = this._parentEl.firstChild;
	}

	this._scrollList(itemDiv);
	this._emulateSingleClick(itemDiv, DwtMouseEvent.LEFT, false, addSelect);
}

DwtListView.prototype._getSiblingElement =
function(element, next){
	var el = next ? element.nextSibling : element.previousSibling;
	return el ? el : element;
};

/**
 * This method will scroll the list to ensure that <code>itemDiv</code> is scrolled
 * into view. 
 * @private
 */
DwtListView.prototype._scrollList =
function(itemDiv) {
	// TODO might be able to cache some of these values
	var parentNode = itemDiv.parentNode;
	var point = this._tmpPoint;
	var itemDivTop = Dwt.getLocation(itemDiv, point).y;
	var parentTop = Dwt.getLocation(parentNode, point).y;
	
	var diff = itemDivTop - (parentNode.scrollTop + parentTop);
	if (diff < 0)
		parentNode.scrollTop += diff;
	else {
		var parentH = Dwt.getSize(parentNode, point).y;
		var itemDivH = Dwt.getSize(itemDiv, point).y;
		diff = (itemDivTop + itemDivH) - (parentTop + parentH + parentNode.scrollTop);
		if (diff > 0)
			parentNode.scrollTop += diff;		
	}
}

DwtListView.prototype._emulateSingleClick =
function(target, button, ctrl, shift, alt, docX, docY) {
	// Gotta do what mousedown listener does
	this._clickDiv = Dwt.findAncestor(target, "_itemIndex");
	var mev = DwtShell.mouseEvent;
	mev.reset();
	mev.target = target;
	mev.button = button;
	mev.docX = docX;
	mev.docY = docY;
	mev.shiftKey = shift;
	mev.altKey = alt;
	mev.ctrlKey = ctrl;
	mev.ersatz = true;
	this._mouseUpListener(mev);
}

DwtListView.prototype._setKbFocusElement =
function(next) {
	// If there are no elements in the list, then bail
	if (!this._list)
		return;
		
	var orig = this._kbAnchor;
	if (this._kbAnchor) {
		this._kbAnchor = this._getSiblingElement(this._kbAnchor, next);
	} else {
		this._kbAnchor = this._parentEl.firstChild;
	}

	if (this._kbAnchor != orig) {
		var selClass = Dwt.getAttr(orig, DwtListView._SELECTED_STYLE_CLASS)
		if (orig.className.indexOf(selClass) != -1)
			orig.className = selClass;
		else 
			orig.className = Dwt.getAttr(orig, DwtListView._STYLE_CLASS);
		this._kbAnchor.className += " " + Dwt.getAttr(this._kbAnchor, DwtListView._KBFOCUS_CLASS);
	}
	
	this._scrollList(this._kbAnchor);
}

DwtListView.prototype._itemSelected =
function(itemDiv, ev) {
	if (this._allowLeftSelection(itemDiv, ev, (ev) ? ev.button : null)) {
		/* Unmark the KB focus element. We need to do this because it is
		 * possible for this element to not be the same as the selection 
		 * anchor due to NEXT and PREV keyboard actions */
		this._unmarkKbAnchorElement(true);

		// clear out old left click selection(s)
		var numSelectedItems = this._selectedItems.size();
		var a = this._selectedItems.getArray();
		for (var i = 0; i < numSelectedItems; i++)
			a[i].className = Dwt.getAttr(a[i], DwtListView._STYLE_CLASS);
		this._selectedItems.removeAll();
		
		// save new left click selection
		this._selectedItems.add(itemDiv);
		
		this._selAnchor = this._kbAnchor = itemDiv;
		itemDiv.className = Dwt.getAttr(itemDiv, DwtListView._SELECTED_STYLE_CLASS);
		if (this.hasFocus())
			itemDiv.className += " " + Dwt.getAttr(itemDiv, DwtListView._KBFOCUS_CLASS);
			
		this._firstSelIndex = this._list 
					? this._list.indexOf(AjxCore.objectWithId(Dwt.getAttr(itemDiv, "_itemIndex"))) : -1;
		//DwtKeyboardMgr.grabFocus(this);
	}
}

DwtListView.prototype._itemClicked =
function(clickedEl, ev) {

	// always clear out old right click selection
	if (this._rightSelItems) {
		this._rightSelItems.className = Dwt.getAttr(this._rightSelItems, DwtListView._STYLE_CLASS);
		this._rightSelItems = null;
	}
	
	var numSelectedItems = this._selectedItems.size()

	if ((!ev.shiftKey && !ev.ctrlKey) || !this.isMultiSelectEnabled()) {
		// always reset detail if left/right click
		if (ev.button == DwtMouseEvent.LEFT || ev.button == DwtMouseEvent.RIGHT)
			this._selEv.detail = DwtListView.ITEM_SELECTED;
		
		// is this element currently in the selected items list?
		var bContained = this._selectedItems.contains(clickedEl);
	
		if (ev.button == DwtMouseEvent.LEFT) {
			this._itemSelected(clickedEl, ev);
		} else if (ev.button == DwtMouseEvent.RIGHT && !bContained) {
			// save right click selection	
			this._rightSelItems = clickedEl;
			clickedEl.className = Dwt.getAttr(clickedEl, DwtListView._SELECTED_STYLE_CLASS) + "-right";
			
			if (this._kbAnchor == clickedEl)
				clickedEl.className = " " + Dwt.getAttr(clickedEl, DwtListView._KBFOCUS_CLASS)
		}
		clickedEl.hoverSet = false;
	} else {	
		if (ev.ctrlKey) {
			if (this._selectedItems.contains(clickedEl)) {
				this._selectedItems.remove(clickedEl);
				clickedEl.className = Dwt.getAttr(clickedEl, DwtListView._STYLE_CLASS);
				this._selEv.detail = DwtListView.ITEM_DESELECTED;
			} else {
				this._selectedItems.add(clickedEl);
				clickedEl.className = Dwt.getAttr(clickedEl, DwtListView._SELECTED_STYLE_CLASS);
				clickedEl.hoverSet = false;
				this._selEv.detail = DwtListView.ITEM_SELECTED;
			}
			
			// Remove the keyboard hilite from the current anchor
			if (this._kbAnchor != clickedEl) {
				var kbAnchor = this._kbAnchor;
				var selClass = Dwt.getAttr(kbAnchor, DwtListView._SELECTED_STYLE_CLASS)
				if (kbAnchor.className.indexOf(selClass) != -1)
					kbAnchor.className = selClass;
				else 
					kbAnchor.className = Dwt.getAttr(kbAnchor, DwtListView._STYLE_CLASS);
			}
			
			// The element that was part of the ctrl action always becomes
			// the anchor since it gets focus
			this._selAnchor = this._kbAnchor = clickedEl;
			this._kbAnchor.className += " " + Dwt.getAttr(this._kbAnchor, DwtListView._KBFOCUS_CLASS);
			//DwtKeyboardMgr.grabFocus(this); // Will cause the kbAnchor element to get the right style
		} else { // SHIFT KEY
			// Adds to the selection to/from the current node to the selection anchor
			if (this._selAnchor == null)
				return;				
			var convEls = this._getChildren() || clickedEl.parentNode.childNodes;
			var numConvEls = convEls.length;
			var convEl;
			var state = 0;
			for (var i = 0; i < numConvEls; i++) {
				convEl = convEls[i];
				if (convEl == this._rightSelItems)
					this._rightSelItems = null;
				
				if (convEl == clickedEl) {
					/* Increment the state. 
					 * 0 - means we havent started
					 * 1 - means we are in selection range
					 * 2 - means we are out of selection range */
					state++;
				}
				var selStyleClass = Dwt.getAttr(convEl, DwtListView._SELECTED_STYLE_CLASS);
				if (convEl == this._selAnchor) {
					state++;
					if (convEl.className.indexOf(selStyleClass) == -1)
						this._selectedItems.add(convEl);

					convEl.className = selStyleClass;
					continue;
				}
				
				// If state == 0 or 2 (i.e. we are out of the selection range, 
				// we have to deselect the node. Else we select it
				if (state != 1 && convEl.className.indexOf(selStyleClass) != -1 && convEl != clickedEl) {
					convEl.className = Dwt.getAttr(convEl, DwtListView._STYLE_CLASS);
					this._selectedItems.remove(convEl);
				} else if (state == 1 || convEl == clickedEl) {
					if (convEl.className.indexOf(selStyleClass) == -1) {
						convEl.hoverSet = false;
						this._selectedItems.add(convEl);
					}
					convEl.className = selStyleClass;
				}
			}
			
			this._kbAnchor = clickedEl;
			this._kbAnchor.className += " " + Dwt.getAttr(this._kbAnchor, DwtListView._KBFOCUS_CLASS);
			//DwtKeyboardMgr.grabFocus(this); // Will cause the kbAnchor element to get the right style
			
			var newSelectedItems = this._selectedItems.size();
			if (numSelectedItems < newSelectedItems)
				this._selEv.detail = DwtListView.ITEM_SELECTED;
			else if (numSelectedItems > newSelectedItems)
				this._selEv.detail = DwtListView.ITEM_DESELECTED;
			else
				return;
		}
	}

	if (ev.button == DwtMouseEvent.LEFT && this._evtMgr.isListenerRegistered(DwtEvent.SELECTION)) {
		if (this._setListEvent(ev, this._selEv, clickedEl))
			this._evtMgr.notifyListeners(DwtEvent.SELECTION, this._selEv);
	} else if (ev.button == DwtMouseEvent.RIGHT && this._evtMgr.isListenerRegistered(DwtEvent.ACTION)) {
		if (this._setListEvent(ev, this._actionEv, clickedEl))
			this._evtMgr.notifyListeners(DwtEvent.ACTION, this._actionEv);
	}
}

/*
* Creates a list event from a mouse event. Returns true if it is okay to notify listeners.
* Subclasses may override to add more properties to the list event.
*
* @param	[DwtEvent]		mouse event
* @param	[DwtEvent]		list event (selection or action)
* @param	[element]		HTML element that received mouse click
*/
DwtListView.prototype._setListEvent =
function(ev, listEv, clickedEl) {
	DwtUiEvent.copy(listEv, ev);
	listEv.item = AjxCore.objectWithId(Dwt.getAttr(clickedEl, "_itemIndex"));
	return true;
};

DwtListView.prototype._columnClicked =
function(clickedCol, ev) {
	
	var list = this.getList();
	if (!list) return;
	var size = list.size();
	if (!size) return;

	var item = this._headerList[Dwt.getAttr(clickedCol, "_itemIndex")];
	// reset order by sorting preference
	this._bSortAsc = item._id == this._currentColId	? !this._bSortAsc : this._getDefaultSortbyForCol(item);
	
	// reset arrows as necessary
	this._setSortedColStyle(item._id);

	// call sorting callback if more than one item to sort
	if (size >= 1){
		this._sortColumn(item, this._bSortAsc);
	}
}

DwtListView.prototype._sortColumn = 
function(columnItem, bSortAsc) {
	// overload me
}

DwtListView.prototype._getActionMenuForColHeader = 
function() {
	// overload me if you want action menu for column headers
	return null;
}

DwtListView.prototype._getDefaultSortbyForCol = 
function(colHeader) {
	// by default, always return ascending
	return true;
}

DwtListView.prototype._allowLeftSelection =
function(clickedEl, ev, button) {
	// overload me (and return false) if you dont want to actually select clickedEl
	return true;
}

DwtListView.prototype._setSortedColStyle = 
function(columnId) {
	
	if (this._currentColId != null && columnId != this._currentColId) {
		// unset current column arrow
		oldArrowId = DwtListView.HEADERITEM_ARROW + this._currentColId;
		oldArrowCell = document.getElementById(oldArrowId);
		if (oldArrowCell && oldArrowCell.firstChild) {
			var imgEl = (AjxImg._mode == AjxImg.SINGLE_IMG) ? oldArrowCell.firstChild : oldArrowCell.firstChild.firstChild;
			if (imgEl)
				imgEl.style.visibility = "hidden";
		}
		
		// reset style for old sorted column
		var oldSortedCol = document.getElementById(this._currentColId);
		if (oldSortedCol)
			oldSortedCol.className = "DwtListView-Column";
	}
	this._currentColId = columnId;
			
	// set new column arrow
	var newArrowId = DwtListView.HEADERITEM_ARROW + columnId;
	var newArrowCell = document.getElementById(newArrowId);
	if (newArrowCell) {
		AjxImg.setImage(newArrowCell, this._bSortAsc ? "ColumnUpArrow" : "ColumnDownArrow");
		var imgEl = (AjxImg._mode == AjxImg.SINGLE_IMG) ? newArrowCell.firstChild : newArrowCell.firstChild.firstChild;
		if (imgEl)
			imgEl.style.visibility = "visible";
	}
	
	// set new column style
	var newSortedCol = document.getElementById(columnId);
	if (newSortedCol)
		newSortedCol.className = "DwtListView-Column DwtListView-ColumnActive";
}

DwtListView.prototype._resetList =
function() {
	this._resetModelList();
	this._resetListView();
};

DwtListView.prototype._resetModelList =
function() {
	// clear out old list to force GC
	if (this._list && this._list.size()) {
		this._list.removeAll();
	}
};

DwtListView.prototype._resetListView =
function() {
	// explicitly remove each child (setting innerHTML causes mem leak)
	var cDiv;
	while (this._parentEl.hasChildNodes()) {
		cDiv = this._parentEl.removeChild(this._parentEl.firstChild);
		AjxCore.unassignId(Dwt.getAttr(cDiv, "_itemIndex"));
	}
	this._selectedItems.removeAll();
	this._rightSelItems = null;
};

DwtListView.prototype._destroyDnDIcon =
function(icon) {
	var itemIdx = Dwt.getAttr(icon, "_itemIndex");
	if (itemIdx)
		AjxCore.unassignId(itemIdx);
	DwtControl.prototype._destroyDnDIcon.call(this,icon);
};

DwtListView.prototype._handleColHeaderMove = 
function(ev) {
	if (this._headerClone == null) {
		if (this._headerColX == null) {
			this._headerColX = ev.docX;
			return;
		} else {
			var threshold = Math.abs(this._headerColX - ev.docX);
			if (threshold < DwtListView.COL_MOVE_THRESHOLD)
				return;
		}
		
		// create a clone of the selected column to move
		this._headerClone = document.createElement("div");
		var size = Dwt.getSize(this._clickDiv);
		var width = AjxEnv.isIE ? size.x : size.x - 3;	// browser quirks
		var height = AjxEnv.isIE ? size.y : size.y - 5;
		Dwt.setSize(this._headerClone, width, height);
		Dwt.setPosition(this._headerClone, Dwt.ABSOLUTE_STYLE); 
		Dwt.setZIndex(this._headerClone, Dwt.Z_DND);
		Dwt.setLocation(this._headerClone, Dwt.DEFAULT, ev.docY);
		
		this._headerClone.className = this._clickDiv.className + " DndIcon";
		this._headerClone.innerHTML = this._clickDiv.innerHTML;
		this._clickDiv.className = "DwtListView-Column DwtListView-ColumnEmpty";
		
		// XXX: style hacks - improve this later
		this._headerClone.style.borderTop = "1px solid #777777";
		var labelCell = document.getElementById(DwtListView.HEADERITEM_LABEL + this._clickDiv.id);
		if (labelCell)
			labelCell.style.color = "white";
		
		//this._listColDiv.appendChild(this._headerClone);
		this.shell.getHtmlElement().appendChild(this._headerClone);
	} else {
		var target = Dwt.findAncestor(ev.target, "_itemIndex");
		if (target && Dwt.getAttr(target, "_type") == DwtListView.TYPE_HEADER_ITEM) {
			if (this._headerCloneTarget && this._headerCloneTarget == this._clickDiv)
				this._headerCloneTarget = null;
			else if (this._headerCloneTarget != target) {
				this._headerCloneTarget = target;
			}
		} else {
			this._headerCloneTarget = null;
		}
	}

	Dwt.setLocation(this._headerClone, ev.docX + 2);
}

DwtListView.prototype._handleColHeaderResize = 
function(ev) {

	if (this._headerSash == null) {
		this._headerSash = document.createElement("div");

		Dwt.setSize(this._headerSash, Dwt.DEFAULT, this.getSize().y);
		Dwt.setPosition(this._headerSash, Dwt.ABSOLUTE_STYLE); 
		Dwt.setZIndex(this._headerSash, Dwt.Z_DND);
		Dwt.setLocation(this._headerSash, Dwt.DEFAULT, 0);

		this._headerSash.className = "DwtListView-ColumnSash";
		this.getHtmlElement().appendChild(this._headerSash);
		
		// remember the initial x-position
		this._headerSashX = ev.docX;
	}
	
	// always update the sash's position
	var parent = this._getParentForColResize();
	var loc = Dwt.toWindow(parent.getHtmlElement(), 0 ,0);
	Dwt.setLocation(this._headerSash, ev.docX-loc.x);
}

DwtListView.prototype._handleColHeaderDrop = 
function(ev) {
	this._headerColX = null;

	if (this._headerClone == null || ev.button == DwtMouseEvent.RIGHT)
		return false;
	
	// did the user drop the column on a valid target?
	if (this._headerCloneTarget) {
		var divItemIdx = Dwt.getAttr(this._clickDiv, "_itemIndex");
		var tgtItemIdx = Dwt.getAttr(this._headerCloneTarget, "_itemIndex");
		this.reIndexColumn(divItemIdx, tgtItemIdx);
	}

	this._clickDiv.className = this._clickDiv.id != this._currentColId 
		? "DwtListView-Column" 
		: "DwtListView-Column DwtListView-ColumnActive";
		
	wasDraggingCol = true;
	var parent = this._headerClone.parentNode;
	if (parent) {
		parent.removeChild(this._headerClone);
	} else {
		DBG.println(AjxDebug.DBG1, "XXX: column header has no parent!");
	}
	delete this._headerClone;
	
	if (Dwt.getAttr(this._clickDiv, "_type") != DwtListView.TYPE_HEADER_ITEM) {
		// something is messed up! redraw the header
		var sortable = this._getSortableFromColId(this._currentColId);
		this._headerColCreated = false;
		this.createHeaderHtml(sortable);
	} else {
		// reset styles as necessary
		var labelCell = document.getElementById(DwtListView.HEADERITEM_LABEL + this._clickDiv.id);
		if (labelCell)
			labelCell.style.color = "black";
	}
		
	this._resetColWidth();

	// TODO: generate notification for column reorder

	return true;
}

DwtListView.prototype._handleColSashDrop = 
function(ev) {
	if (this._headerSash == null || ev.button == DwtMouseEvent.RIGHT)
		return false;
		
	// find out where the user dropped the sash and update column width
	var delta = ev.docX - this._headerSashX;

	var itemIdx = Dwt.getAttr(this._clickDiv, "_itemIndex");
	var suffixIdx = itemIdx.indexOf("--sash");
	var headerIdx = parseInt(itemIdx.substring(0, suffixIdx));
	if (headerIdx >= 0 && headerIdx < this._headerList.length) {
		var newWidth = null;
		if (this._headerList[headerIdx]._width && this._headerList[headerIdx]._width != "auto")
			newWidth = this._headerList[headerIdx]._width + delta;
		else {
			// lets actually adjust the next column since this one has a relative width
			var nextCol = this._headerList[headerIdx+1];
			if (nextCol && nextCol._width && nextCol._resizeable) {
				var cell = document.getElementById(nextCol._id);
				newWidth = cell ? Dwt.getSize(cell).x + delta : null;
			}
		}
		this.reSizeColumn(headerIdx, newWidth);
	} else {
		DBG.println("XXX: Bad header ID.");
	}
	
	var parent = this._headerSash.parentNode;
	if (parent)
		parent.removeChild(this._headerSash);
	delete this._headerSash;
	
	this._resetColWidth();
	
	return true;
}

DwtListView.prototype._relayout = 
function() {
	// force relayout of header column
	this._headerColCreated = false;
	var sortable = this._getSortableFromColId(this._currentColId);
	var sel = this.getSelection()[0];
	this.setUI(sortable);
	this.setSelection(sel, true);
}

// XXX: this could be optimized by saving the sortable everytime the sort column changes
DwtListView.prototype._getSortableFromColId = 
function(colId) {
	// helper function to find column that was last sorted
	var sortable = null;
	for (var i = 0; i < this._headerList.length; i++) {
		if (this._headerList[i]._id == colId) {
			sortable = this._headerList[i]._sortable;
			break;
		}
	}
	return sortable;
}

DwtListView.prototype._getParentForColResize = 
function() {
	// overload me to return a higher inheritance chain parent
	return this;
}

DwtListView.prototype.setSize =
function(width, height) {
	DwtComposite.prototype.setSize.call(this, width, height);
	this._sizeChildren(height);
}

DwtListView.prototype.setBounds =
function(x, y, width, height) {
	DwtComposite.prototype.setBounds.call(this, x, y, width, height);
	this._sizeChildren(height);
}

DwtListView.prototype._sizeChildren =
function(height) {
	if (this._listDiv && (height != Dwt.DEFAULT))
		Dwt.setSize(this._listDiv, Dwt.DEFAULT, height - DwtListView.HEADERITEM_HEIGHT);
}

// overload if parent element's children are not DIV's (i.e. div's w/in a table)
DwtListView.prototype._getChildren = 
function() {
	return null;
}

DwtListView.prototype.setSortByAsc = 
function(column, bSortByAsc) {
	if (!this._headerList)
		return;
		
	this._bSortAsc = bSortByAsc;
	var columnId = null;
	for (var i = 0; i < this._headerList.length; i++) {
		if (this._headerList[i]._sortable && this._headerList[i]._sortable == column) {
			columnId = this._headerList[i]._id;
			break;
		}
	}
	if (columnId)
		this._setSortedColStyle(columnId);
}

DwtListView.prototype.enableSorting = 
function(enabled) { 
	this._sortingEnabled = enabled;
};

DwtListView.prototype.getOffset = 
function() { 
	return this._offset;
}

DwtListView.prototype.setOffset = 
function(newOffset) { 
	this._offset = newOffset;
}

DwtListView.prototype.getNewOffset = 
function(bPageForward) {
	var limit = this.getLimit();
	var offset = bPageForward ? this._offset + limit : this._offset - limit;
	
	// normalize..
	if (offset < 0) 
		offset = 0;
	
	return offset;
}

DwtListView.prototype.getLimit = 
function() {
	// return the default limit value unless overloaded
	return DwtListView.DEFAULT_LIMIT;
}

DwtListView.prototype.getReplenishThreshold = 
function() {
	// return the default threshold value unless overloaded
	return DwtListView.MAX_REPLENISH_THRESHOLD;
}

DwtListView.prototype.getList = 
function() {
	return this._list;
}

// this method simply appends the given list to this current one
DwtListView.prototype.replenish = 
function(list) {
	this._list.addList(list);

	var size = list.size();
	for (var i = 0; i < size; i++) {
		var item = list.get(i);
		var div = this._createItemHtml(item, this._now);
		if (div)
			this._addRow(div);
	}
}

DwtListView.prototype._focus =
function() {
	DBG.println(AjxDebug.DBG2, "DwtListView: FOCUS");
	if (this._kbAnchor != null)
		this._kbAnchor.className += " " + Dwt.getAttr(this._kbAnchor, DwtListView._KBFOCUS_CLASS);
}

DwtListView.prototype._blur =
function() {
	DBG.println(AjxDebug.DBG2, "DwtListView: BLUR");
	this._unmarkKbAnchorElement();
}

/**
 * Removes the "focus style" from the current KB anchor.
 * 
 * @param clear		[boolean]*		if true, clear KB anchor
 */
DwtListView.prototype._unmarkKbAnchorElement =
function(clear) {
	if (this._kbAnchor != null) {
		if (this._selectedItems.contains(this._kbAnchor))
			this._kbAnchor.className = Dwt.getAttr(this._kbAnchor, DwtListView._SELECTED_STYLE_CLASS);
		else 
			this._kbAnchor.className = Dwt.getAttr(this._kbAnchor, DwtListView._STYLE_CLASS);
	}
	if (clear){
		this._kbAnchor = null;
	}
}

DwtListView.prototype.getKeyMapName = 
function() {
	return "DwtListView";
};

DwtListView.prototype.handleKeyAction =
function(actionCode, ev) {
	switch (actionCode) {
		case DwtKeyMap.SELECT:
			this._emulateSingleClick(this._kbAnchor, DwtMouseEvent.LEFT);
			break;
			
		case DwtKeyMap.SELECT_CURRENT:
			this._emulateSingleClick(this._kbAnchor, DwtMouseEvent.LEFT, true);
			break;
			
		case DwtKeyMap.SELECT_NEXT:
			this._selectItem(true);
			break;
			
		case DwtKeyMap.SELECT_PREV:
			this._selectItem(false);
			break;
			
		case DwtKeyMap.ADD_SELECT_NEXT:
			this._selectItem(true, true);
			break;
			
		case DwtKeyMap.ADD_SELECT_PREV:
			this._selectItem(false, true);
			break;
		
		case DwtKeyMap.SELECT_ALL:
			if (this._list && this._list.size()) {
				this.setSelectedItems(this._list.getArray());
			}
			break;
		
		case DwtKeyMap.SELECT_FIRST:
			var a = this._list.getArray();
			if (a && a.length > 1) {
				this.setSelection(a[0]);
			}
			this._scrollList(this._kbAnchor);
			break;

		case DwtKeyMap.SELECT_LAST:
			var a = this._list.getArray();
			if (a && a.length > 1) {
				this.setSelection(a[a.length - 1]);
			}
			this._scrollList(this._kbAnchor);
			break;

		case DwtKeyMap.PREV:
			this._setKbFocusElement(false);
			break;
			
		case DwtKeyMap.NEXT:
			this._setKbFocusElement(true);
			break;
			
		case DwtKeyMap.DBLCLICK:
			this.emulateDblClick(this.getItemFromElement(this._kbAnchor));
			break;
		
		case DwtKeyMap.ACTION:
			if (this._evtMgr.isListenerRegistered(DwtEvent.ACTION)) {
				var p = Dwt.toWindow(this._kbAnchor, 0, 0);
				var s = Dwt.getSize(this._kbAnchor);
				this._emulateSingleClick(this._kbAnchor, DwtMouseEvent.RIGHT,
						false, false, false, p.x + s.x / 4, p.y + s.y / 2);
			}
			break;
		
		default:
			return false;		
	}
	
	return true;
}

//////////////////////////////////////////////////////////////////////////////
// DwtListHeaderItem
// - This is a (optional) "container" class for DwtListView objects which 
//   want a column header to appear. Create a new DwtListViewItem for each 
//   column header you want to appear. Be sure to specify width values 
//   (otherwise, undefined is default)
//
// @id 			Some ID used internally (a GUID gets appended to ensure uniqueness)
// @label 		The text shown for the column
// @iconInfo 	The icon shown for the column
// @width 		The width of the column
// @sortable 	Flag indicating whether column is sortable
// @resizeable 	Flag indicating whether column can be resized
// @visible 	Flag indicating whether column is initially visible
// @name 		Description of column used if column headers have action menu 
//              - If not supplied, uses label value. 
//                This param is primarily used for columns w/ only an icon (no label)
//
// TODO - kill this class and make a static array in derived class describing 
//        column info (i.e. derived classes will be required to supply this!)
//////////////////////////////////////////////////////////////////////////////
function DwtListHeaderItem(id, label, iconInfo, width, sortable, resizeable, visible, name) {
	this._id = id + Dwt.getNextId();
	this._label = label;
	this._iconInfo = iconInfo;
	var w = parseInt(width);
	this._widthUnits = null;
	if(isNaN(w) || !w) {
		this._width = "auto";
	} else if(String(w).toString()==String(width).toString())	{
		this._width = w;
	} else {
		this._width = parseInt(String(width).substr(0,String(w).length));
		this._widthUnits = AjxStringUtil.getUnitsFromSizeString(width);
	}

	this._sortable = sortable;
	this._resizeable = resizeable;
	// only set visible if explicitly set to false
	this._visible = (visible !== false);
	this._name = name || label;
}
