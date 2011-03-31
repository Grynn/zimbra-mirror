/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * Creates a list view.
 * @constructor
 * @class
 * A list view presents a list of items as rows with fields (columns).
 * 
 * @author Parag Shah
 * @author Conrad Damon
 * 
 * @param {hash}	params		a hash of parameters
 * @param  {DwtComposite}     parent		the parent widget
 * @param {string}	className		the CSS class
 * @param {constant}	posStyle		the positioning style (see {@link DwtControl})
 * @param  {array}	headerList	a list of IDs for columns
 * @param {boolean}	noMaximize	if <code>true</code>, all columns are fixed-width (otherwise, one will
 * 											expand to fill available space)
 * @param  {constant}     view			the ID of view
 * 
 * @extends		DwtComposite
 */
DwtListView = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtListView.PARAMS);
	params.className = params.className || "DwtListView";
	DwtComposite.call(this, params);

	this._view = params.view || Dwt.getNextId();
	if (params.headerList) {
		var htmlElement = this.getHtmlElement();

        var html = new Array(50);
        var idx = 0;
        var headId = DwtId.getListViewId(this._view, DwtId.LIST_VIEW_HEADERS);
        var colId = DwtId.getListViewId(this._view, DwtId.LIST_VIEW_ROWS);
        html[idx++] = "<table width='100%' cellspacing='0' cellpadding='0'><tr><td ";
        html[idx++] = "id=" + headId;
        html[idx++] = "></td></tr><tr><td ";
        html[idx++] = "id=" + colId;
        html[idx++] = "></td></tr></table>";
        htmlElement.innerHTML = html.join("");

        var headHtml = document.getElementById(headId);
        this._listColDiv = document.createElement("div");
        this._listColDiv.id = DwtId.getListViewId(this._view, DwtId.LIST_VIEW_HEADERS);
        this._listColDiv.className = "DwtListView-ColHeader";
        headHtml.appendChild(this._listColDiv);

        var colHtml = document.getElementById(colId);
        this._listDiv = document.createElement("div");
        this._listDiv.id = DwtId.getListViewId(this._view, DwtId.LIST_VIEW_ROWS);
        this._listDiv.className = "DwtListView-Rows";
        colHtml.appendChild(this._listDiv);


		// setup vars needed for sorting
		this._bSortAsc = false;
		this._currentColId = null;
		this.sortingEnabled = true;
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
	this._headerList = params.headerList;
	this._noMaximize = params.noMaximize;
	this._parentEl = this._headerList ? this._listDiv : this.getHtmlElement();
	
	this._list = null;
	this.offset = 0;
	this.headerColCreated = false;
	this.multiSelectEnabled = true;
	this.firstSelIndex = -1;

	// the key is the HTML ID of the item's associated DIV; the value is an object
	// with information about that row
	this._data = {};

    // item classes
    this._rowClass = [ this._className, DwtListView.ROW_CLASS ].join("");
	var nc = this._normalClass = DwtListView.ROW_CLASS;
	this._selectedClass = [nc, DwtCssStyle.SELECTED].join("-");
	this._disabledSelectedClass = [this._selectedClass, DwtCssStyle.DISABLED].join("-");
	this._kbFocusClass = [nc, DwtCssStyle.FOCUSED].join("-");
	this._dndClass = [nc, DwtCssStyle.DRAG_PROXY].join("-");
	this._rightClickClass = [this._selectedClass, DwtCssStyle.ACTIONED].join("-");

    this._styleRe = this._getStyleRegex();
};

DwtListView.prototype = new DwtComposite;
DwtListView.prototype.constructor = DwtListView;


// Consts

DwtListView.PARAMS					= ["parent", "className", "posStyle", "headerList", "noMaximize"];
DwtListView.ITEM_SELECTED 			= 1;
DwtListView.ITEM_DESELECTED 		= 2;
DwtListView.ITEM_DBL_CLICKED 		= 3;
DwtListView._LAST_REASON 			= 3;
DwtListView._TOOLTIP_DELAY 			= 250;
DwtListView.HEADERITEM_HEIGHT 		= 24;
DwtListView.TYPE_HEADER_ITEM 		= "1";
DwtListView.TYPE_LIST_ITEM 			= "2";
DwtListView.TYPE_HEADER_SASH 		= "3";
DwtListView.DEFAULT_LIMIT			= 25;
DwtListView.MAX_REPLENISH_THRESHOLD	= 10;
DwtListView.MIN_COLUMN_WIDTH		= 20;
DwtListView.COL_MOVE_THRESHOLD		= 3;
DwtListView.ROW_CLASS				= "Row";
DwtListView.ROW_CLASS_ODD			= "RowEven";
DwtListView.ROW_CLASS_EVEN			= "RowOdd";

// property names for row DIV to store styles
DwtListView._STYLE_CLASS				= "_sc";
DwtListView._SELECTED_STYLE_CLASS		= "_ssc";
DwtListView._SELECTED_DIS_STYLE_CLASS	= "_sdsc";
DwtListView._KBFOCUS_CLASS				= "_kfc";


// Public methods

DwtListView.prototype.toString =
function() {
	return "DwtListView";
};

DwtListView.prototype.dispose =
function() {
	this._listColDiv = null;
	this._listDiv = null;
	this._parentEl = null;
	this._clickDiv = null;
	this._selectedItems = null;
	DwtComposite.prototype.dispose.call(this);
};

/**
 * Sets the enabled flag.
 * 
 * @param	{boolean}	enabled		if <code>true</code>, enable the list view
 */
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
            Dwt.delClass(elements[i], this._styleRe, enabled ? this._selectedClass : this._disabledSelectedClass);
		}
	}
};

DwtListView.prototype.createHeaderHtml =
function(defaultColumnSort) {
	// does this list view have headers or have they already been created?
	if (!this._headerList || this.headerColCreated) { return; }

	this._headerHash = {};
	this._headerIdHash = {};

	var idx = 0;
	var htmlArr = [];

	htmlArr[idx++] = "<table id='";
	htmlArr[idx++] = DwtId.getListViewHdrId(DwtId.WIDGET_HDR_TABLE, this._view);
	htmlArr[idx++] = "' cellpadding=0 cellspacing=0 border=0 height=100%";
	htmlArr[idx++] = this._noMaximize ? ">" : " width=100%>";
	htmlArr[idx++] = "<tr>";

	var numCols = this._headerList.length;
	for (var i = 0; i < numCols; i++) {
		var headerCol = this._headerList[i];
		var field = headerCol._field;
		headerCol._index = i;
		var id = headerCol._id = DwtId.getListViewHdrId(DwtId.WIDGET_HDR, this._view, field);

		this._headerHash[field] = headerCol;
		this._headerIdHash[id] = headerCol;

		if (headerCol._variable) {
			this._variableHeaderCol = headerCol;
		}

		if (headerCol._visible) {
			idx = this._createHeader(htmlArr, idx, headerCol, i, numCols, id, defaultColumnSort);
		}
	}
	htmlArr[idx++] = "</tr></table>";

	this._listColDiv.innerHTML = htmlArr.join("");

	// for each sortable column, sets its identifier
	var numResizeable = 0, resizeableCol;
	for (var j = 0; j < this._headerList.length; j++) {
		var headerCol = this._headerList[j];
		var cell = document.getElementById(headerCol._id);
		if (!cell) { continue; }

		if (headerCol._sortable && headerCol._field == defaultColumnSort) {
			cell.className = "DwtListView-Column DwtListView-ColumnActive";
		}

		if (headerCol._resizeable) {
			// always get the sibling cell to the right
			var sashId = DwtId.getListViewHdrId(DwtId.WIDGET_HDR_SASH, this._view, headerCol._field);
			var sashCell = document.getElementById(sashId);
			if (sashCell) {
				this.associateItemWithElement(headerCol, sashCell, DwtListView.TYPE_HEADER_SASH, sashId, {index:j});
			}
			numResizeable++;
			resizeableCol = headerCol;
		}
		this.associateItemWithElement(headerCol, cell, DwtListView.TYPE_HEADER_ITEM, headerCol._id, {index:j});
	}

	if (numResizeable == 1) {
		resizeableCol._resizeable = false;
	}

	this.headerColCreated = true;
};

DwtListView.prototype._createHeader =
function(htmlArr, idx, headerCol, i, numCols, id, defaultColumnSort) {

	var field = headerCol._field;

	htmlArr[idx++] = "<td id='";
	htmlArr[idx++] = id;
	htmlArr[idx++] = "' class='";
    var tmpClass = (id == this._currentColId) ? "DwtListView-Column DwtListView-ColumnActive"
		: "DwtListView-Column";
    tmpClass += headerCol._sortable ? "" : " DwtDefaultCursor";
    htmlArr[idx++] = tmpClass + "'";
	if (headerCol._width) {
		htmlArr[idx++] = " width=";
		htmlArr[idx++] = headerCol._width;
		if (headerCol._widthUnits) {
			htmlArr[idx++] = headerCol._widthUnits;
		}
    }
	htmlArr[idx++] = ">";
	// must add a div to force clipping :(
	htmlArr[idx++] = "<div";
	var headerColWidth = null;
	if (headerCol._width && headerCol._width != "auto") {
		//why we need to + 2 here ? It causes the misalign of the list items in IE
		if (AjxEnv.isIE) {
			headerColWidth = headerCol._width;
		} else {
			headerColWidth = headerCol._width + 2;
		}
		if (headerCol._widthUnits) {
			headerColWidth += headerCol._widthUnits;
		}
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
		var idText = ["id='", DwtId.getListViewHdrId(DwtId.WIDGET_HDR_ICON, this._view, field), "'"].join("");
		htmlArr[idx++] = "<td><center>";
		htmlArr[idx++] = AjxImg.getImageHtml(headerCol._iconInfo, null, idText);
		htmlArr[idx++] = "</center></td>";
	}

	if (headerCol._label) {
		htmlArr[idx++] = "<td id='";
		htmlArr[idx++] = DwtId.getListViewHdrId(DwtId.WIDGET_HDR_LABEL, this._view, field);
		htmlArr[idx++] = "' class='DwtListHeaderItem-label'>";
		htmlArr[idx++] = headerCol._label;
		htmlArr[idx++] = "</td>";
	}

	if (headerCol._sortable && !headerCol._noSortArrow) {
		var arrowIcon = this._bSortAsc ? "ColumnUpArrow" : "ColumnDownArrow";

		htmlArr[idx++] = "<td align=right style='padding-right:2px' width=100% id='";
		htmlArr[idx++] = DwtId.getListViewHdrId(DwtId.WIDGET_HDR_ARROW, this._view, field);
		htmlArr[idx++] = "'>";
		var isDefault = (field == defaultColumnSort);
		htmlArr[idx++] = AjxImg.getImageHtml(arrowIcon, isDefault ? null : "visibility:hidden");
		htmlArr[idx++] = "</td>";
		if (isDefault) {
			this._currentColId = id;
		}
	}

	// ALWAYS add "sash" separators
	if (i < (numCols - 1)) {
		htmlArr[idx++] = "<td width=6>";
		htmlArr[idx++] = "<table align=right border=0 cellpadding=0 cellspacing=0 width=6 height=100% id='";
		htmlArr[idx++] = DwtId.getListViewHdrId(DwtId.WIDGET_HDR_SASH, this._view, field);
		htmlArr[idx++] = "'><tr>";
		htmlArr[idx++] = "<td class='DwtListView-Sash'><div style='width: 1px; height: ";
		htmlArr[idx++] = (DwtListView.HEADERITEM_HEIGHT - 2);
		htmlArr[idx++] = "px; background-color: #8A8A8A;margin-left:2px'></div></td><td class='DwtListView-Sash'><div style='width: 1px; height: ";
		htmlArr[idx++] = (DwtListView.HEADERITEM_HEIGHT - 2);
		htmlArr[idx++] = "px; background-color: #FFFFFF;margin-right:2px'></div></td></tr></table>";
		htmlArr[idx++] = "</td>";
	}

	htmlArr[idx++] = "</tr></table>";
	htmlArr[idx++] = "</div></td>";

	return idx;
};

/**
 * Gets the index of the given item.
 *
 * @param	{Object}	item		the item
 * @return	{number}	the index or <code>null</code> if not found
 */
DwtListView.prototype.getItemIndex =
function(item) {
	var list = this._list;
	if (list) {
		var len = list.size();
		for (var i = 0; i < len; ++i) {
			if (list.get(i).id == item.id) {
				return i;
			}
		}
	}
	return null;
};

/**
 * Sets the size of the view.
 * 
 * @param	{number|string}	width		the width (for example: 100, "100px", "75%")
 * @param	{number|string}	height		the height (for example: 100, "100px", "75%")
 */
DwtListView.prototype.setSize =
function(width, height) {
	DwtComposite.prototype.setSize.call(this, width, height);
	this._sizeChildren(height);
};

/**
 * Gets the count of items in the list.
 * 
 * @return	{number}	the count of items
 */
DwtListView.prototype.size =
function() {
	return this._list ? this._list.size() : 0;
};

/**
 * Creates a list view out of the given vector of items. The derived class should override _createItemHtml()
 * in order to display an item.
 *
 * @param {AjxVector}	list			a vector of items
 * @param {number}	[defaultColumnSort]	the default column field to sort
 * @param {boolean}	noResultsOk		if <code>true</code>, do not show "No Results" for empty list
 */
DwtListView.prototype.set =
function(list, defaultColumnSort, noResultsOk) {
	if (this._selectedItems) {
		this._selectedItems.removeAll();
	}
	this._rightSelItem = null;
	this.sortingEnabled = true;
	this._resetList();
	this._list = list;
	this.setUI(defaultColumnSort, noResultsOk);
};

/**
 * Renders the list view using the current list of items.
 *
 * @param {string}	defaultColumnSort		the ID of column that represents default sort order
 * @param {boolean}	noResultsOk			if <code>true</code>, do not show "No Results" for empty list
 */
DwtListView.prototype.setUI =
function(defaultColumnSort, noResultsOk) {
	this.removeAll();
	this.createHeaderHtml(defaultColumnSort);
	this._renderList(this._list, noResultsOk);
};

DwtListView.prototype._renderList =
function(list, noResultsOk, doAdd) {
	if (list instanceof AjxVector && list.size()) {
		var now = new Date();
		var size = list.size();
		var htmlArr = [];
		for (var i = 0; i < size; i++) {
			var item = list.get(i);
			var div = this._createItemHtml(item, {now:now}, !doAdd, i);
			if (div) {
				if (div instanceof Array) {
					for (var j = 0; j < div.length; j++){
						this._addRow(div[j]);
					}
				} else if (div.tagName || doAdd) {
					this._addRow(div);
				} else {
					htmlArr.push(div);
				}
			}
		}
		if (htmlArr.length) {
			this._parentEl.innerHTML = htmlArr.join("");
		}
	} else if (!noResultsOk) {
		this._setNoResultsHtml();
	}
};

/**
 * Adds the items.
 * 
 * @param	{array}		itemArray		an array of items
 */
DwtListView.prototype.addItems =
function(itemArray) {
	if (AjxUtil.isArray(itemArray)) {
		if (!this._list) {
			this._list = new AjxVector();
		}

		// clear the "no results" message before adding!
		if (this._list.size() == 0) {
			this._resetList();
		}
		this._renderList(AjxVector.fromArray(itemArray), null, true);
		this._list.addList(itemArray);
	}
};

/**
 * Adds a row for the given item to the list view.
 *
 * @param {Object}	item			the data item
 * @param {number}	index			the index at which to add item to list and list view
 * @param {boolean}	skipNotify	if <code>true</code>, do not notify listeners
 * @param {number}	itemIndex		index at which to add item to list, if different
 * 									from the one for the list view
 */
DwtListView.prototype.addItem =
function(item, index, skipNotify, itemIndex) {
	if (!this._list) {
		this._list = new AjxVector();
	}

	// clear the "no results" message before adding!
	if (this._list.size() == 0) {
		this._resetList();
	}

	this._list.add(item, (itemIndex != null) ? itemIndex : index);
	var div = this._createItemHtml(item);
	if (div) {
		if (div instanceof Array) {
			for (var j = 0; j < div.length; j++) {
				this._addRow(div[j]);
			}
		} else {
			this._addRow(div, index);
		}
	}

	if (!skipNotify && this._evtMgr.isListenerRegistered(DwtEvent.STATE_CHANGE)) {
		this._evtMgr.notifyListeners(DwtEvent.STATE_CHANGE, this._stateChangeEv);
	}
};

/**
 * Removes a row for the given item to the list view.
 *
 * @param {Object}	item			the data item
 * @param {boolean}	skipNotify	if <code>true</code>, do not notify listeners
 * @param {boolean}	skipAlternation		if <code>true</code>, do not fix alternation
 */
DwtListView.prototype.removeItem =
function(item, skipNotify, skipAlternation) {

	var itemEl = this._getElFromItem(item);
	if (!itemEl) { return; }

	var altIndex = this._getRowIndex(item);	// get index before we remove row

	this._selectedItems.remove(itemEl);
	if (this._rightSelItem == itemEl) {
		this._rightSelItem = null;
	}
	if (this._kbAnchor == itemEl) {
		this._kbAnchor = null;
	}
	this._parentEl.removeChild(itemEl);
	if (this._list) {
		this._list.remove(item);
	}
	var id = itemEl.id;
	if (this._data[id]) {
		this._data[id] = null;
		delete this._data[id];
	}
	if (!skipAlternation) {
		this._fixAlternation(altIndex);
	}

	if (!skipNotify && this._evtMgr.isListenerRegistered(DwtEvent.STATE_CHANGE)) {
		this._evtMgr.notifyListeners(DwtEvent.STATE_CHANGE, this._stateChangeEv);
	}
};

DwtListView.prototype.redrawItem =
function(item) {
    var odiv = this._getElFromItem(item);
    if (odiv) {
        var ndiv = this._createItemHtml(item);
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

/**
 * Adds a selection listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtListView.prototype.addSelectionListener =
function(listener) {
	this._evtMgr.addListener(DwtEvent.SELECTION, listener);
};

/**
 * Removes a selection listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtListView.prototype.removeSelectionListener =
function(listener) {
	this._evtMgr.removeListener(DwtEvent.SELECTION, listener);
};

/**
 * Adds an action listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtListView.prototype.addActionListener =
function(listener) {
	this._evtMgr.addListener(DwtEvent.ACTION, listener);
};

/**
 * Removes an action listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtListView.prototype.removeActionListener =
function(listener) {
	this._evtMgr.removeListener(DwtEvent.ACTION, listener);
};

/**
 * Adds a state change listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtListView.prototype.addStateChangeListener =
function(listener) {
	this._evtMgr.addListener(DwtEvent.STATE_CHANGE, listener);
};

/**
 * Adds a state change listener.
 * 
 * @param	{AjxListener}	listener		the listener
 */
DwtListView.prototype.removeStateChangeListener =
function(listener) {
	this._evtMgr.removeListener(DwtEvent.STATE_CHANGE, listener);
};

/**
 * Removes all the items from the list.
 * 
 * @param {boolean}	skipNotify	if <code>true</code>, do not notify listeners
 */
DwtListView.prototype.removeAll =
function(skipNotify) {
	if (this._parentEl) {
		this._parentEl.innerHTML = "";
	}
	if (this._selectedItems) {
		this._selectedItems.removeAll();
	}
	this._rightSelItem = this._selAnchor = this._kbAnchor = null;

	if (!skipNotify && this._evtMgr.isListenerRegistered(DwtEvent.STATE_CHANGE)) {
		this._evtMgr.notifyListeners(DwtEvent.STATE_CHANGE, this._stateChangeEv);
	}
};

/**
 * Selects all items in the list.
 * 
 */
DwtListView.prototype.selectAll =
function() {
	if (this._list && this._list.size()) {
		this.setSelectedItems(this._list.getArray());
	}
};

/**
 * De-selects all items in the list.
 * 
 */
DwtListView.prototype.deselectAll =
function() {
	var a = this._selectedItems.getArray();
	var sz = this._selectedItems.size();
	for (var i = 0; i < sz; i++) {
        Dwt.delClass(a[i], this._styleRe);
    }
    this._selectedItems.removeAll();
	this._rightSelItem = this._selAnchor = null;

	if (this._kbAnchor != null && this.hasFocus()) {
		Dwt.addClass(this._kbAnchor, this._kbFocusClass);
	}
};

DwtListView.prototype.getDnDSelection =
function() {
	if (this._dndSelection instanceof AjxVector) {
		return this.getSelection();
	} else {
		return this.getItemFromElement(this._dndSelection);
	}
};

DwtListView.prototype.getSelection =
function() {
	var a = [];
	if (this._rightSelItem) {
		a.push(this.getItemFromElement(this._rightSelItem));
	} else if (this._selectedItems) {
        var sa = this._selectedItems.getArray();
		var saLen = this._selectedItems.size();
		for (var i = 0; i < saLen; i++) {
			a[i] = this.getItemFromElement(sa[i]);
		}
	}
	return a;
};

/**
 * Gets the selected items.
 * 
 * @return	{Array}	an array of selected items
 */
DwtListView.prototype.getSelectedItems =
function() {
	return this._selectedItems;
};

DwtListView.prototype.setSelection =
function(item, skipNotify) {

	if (!item) { return; }

	var el = this._getElFromItem(item);
	if (el) {
		if ((this._selectedItems.size() == 1) && (this._selectedItems.get(0) == el)) {
			return;
		}
		this.deselectAll();
		this._unmarkKbAnchorElement(true);
		this._selAnchor = this._kbAnchor = el;
		this.selectItem(item, this.getEnabled());

		// reset the selected index
		this.firstSelIndex = (this._list && this._list.size() > 0) ? this._list.indexOf(item) : -1;

		this._scrollList(el);

		if (!skipNotify && this._evtMgr.isListenerRegistered(DwtEvent.SELECTION)) {
			var selEv = new DwtSelectionEvent(true);
			selEv.button = DwtMouseEvent.LEFT;
			selEv.target = el;
			selEv.item = this.getItemFromElement(el);
			selEv.detail = DwtListView.ITEM_SELECTED;
			selEv.ersatz = true;
			this._evtMgr.notifyListeners(DwtEvent.SELECTION, selEv);
		}
	}
};

DwtListView.prototype.setMultiSelection =
function(clickedEl, bContained, ev) {
	if (bContained) {
		this._selectedItems.remove(clickedEl);
		Dwt.delClass(clickedEl, this._styleRe);		// , this._normalClass	MOW
		this._selEv.detail = DwtListView.ITEM_DESELECTED;
	} else {
		this._selectedItems.add(clickedEl, null, true);
		Dwt.delClass(clickedEl, this._styleRe, this._selectedClass);
		this._selEv.detail = DwtListView.ITEM_SELECTED;
	}

	// Remove the keyboard hilite from the current anchor
	if (this._kbAnchor && this._kbAnchor != clickedEl) {
		var kbAnchor = this._kbAnchor;
		var selClass = this._selectedClass;
		if (kbAnchor.className.indexOf(selClass) != -1) {
			Dwt.delClass(kbAnchor, this._styleRe, selClass);
		} else {
			Dwt.delClass(kbAnchor, this._styleRe);	// , this._normalClass MOW
		}
	}

	// The element that was part of the ctrl action always becomes the anchor
	// since it gets focus
	this._selAnchor = this._kbAnchor = clickedEl;
	Dwt.addClass(this._kbAnchor, this._kbFocusClass);
};

DwtListView.prototype.setSelectedItems =
function(selectedArray) {
	this.deselectAll();
	var sz = selectedArray.length, doSelect = this.getEnabled();
	for (var i = 0; i < sz; ++i) {
		this.selectItem(selectedArray[i], doSelect);
	}
};

/**
 * Selects or deselects a single item.
 * 
 * @param	{boolean}	selected		if <code>true</code>, select the item
 */
DwtListView.prototype.selectItem =
function(item, selected) {

	var el = this._getElFromItem(item);
	if (el) {
		Dwt.delClass(el, this._styleRe, selected ? this._selectedClass : this._disabledSelectedClass);
		if (this._kbAnchor == el && this.hasFocus()) {
			Dwt.addClass(el, this._kbFocusClass);
		}
		this._selectedItems.add(el);
	}
};

/**
 * Gets the selection count.
 * 
 * @return	{number}	the selection count
 */
DwtListView.prototype.getSelectionCount =
function() {
	return this._rightSelItem ? 1 : this._selectedItems.size();
};

DwtListView.prototype.handleActionPopdown =
function() {
	this._clearRightSel();
};

/**
 * Pairs an item with an element. As a side effect, provides a mechanism for storing
 * data about a particular element, referenced by its ID.
 *
 * @param {Object}	item		an item
 * @param {Element}	element	an HTML element
 * @param {constant}	[type=DwtListView.TYPE_LIST_ITEM]		a role that element has
 * @param {string}	[id]		the ID for element; if not provided, one is generated from the item
 * @param {hash}	[data]		any additional attributes to store
 * 
 * @private
 */
DwtListView.prototype.associateItemWithElement =
function(item, element, type, id, data) {
	id = id || this._getItemId(item);
	if (element) {
		element.id = id;
	}
	type = type || DwtListView.TYPE_LIST_ITEM;
	this._data[id] = {item:item, id:id, type:type};
	if (data) {
		for (var key in data) {
			this._data[id][key] = data[key];
		}
	}
	return id;
};

DwtListView.prototype.getItemFromElement =
function(el) {
	return this._getItemData(el, "item");
};

/**
 * Starts with an element and works its way up the element chain until it finds one
 * with an ID that maps to an item, then returns the associated item.
 *
 * @param {Element}	el	element to start with
 * @return	{Object}	the item
 */
DwtListView.prototype.findItem =
function(el)  {
	if (!el) { return; }
	var div = this.findItemDiv(el);
	return this._getItemData(div, "item");
};

/**
 * Starts with an element and works its way up the element chain until it finds one
 * with an ID that maps to an item.
 *
 * @param {Element}	el	the element to start with
 * @return	{Element}	the element
 */
DwtListView.prototype.findItemDiv =
function(el)  {
	if (!el) { return; }
	while (el && (el.id != this._htmlElId)) {
		if (el.id && this._data[el.id]) {
			return el;
		}
		el = el.parentNode;
	}
	return null;
};

/**
 * Gets the item associated with the given event. Starts with the
 * event target and works its way up the element chain until it finds one
 * with an ID that maps to an item.
 *
 * @param {DwtEvent}	ev				the event
 * @return	{Object}	the item
 */
DwtListView.prototype.getTargetItem =
function(ev)  {
	return this.findItem(DwtUiEvent.getTarget(ev));
};

/**
 * Gets the item DIV associated with the given event. Starts with the
 * event target and works its way up the element chain until it finds one
 * with an ID that maps to an item.
 *
 * @param {DwtEvent}	ev				the event
 * @return	{Object}	the item
 */
DwtListView.prototype.getTargetItemDiv =
function(ev)  {
	return this.findItemDiv(DwtUiEvent.getTarget(ev));
};

DwtListView.prototype.dragSelect =
function(row) {
	// If we have something previously selected, try and remove the selection
	if (this._dragHighlight) {
		var oldRow = document.getElementById(this._dragHighlight);
		// only go forward if the row doesn't exist, or if the new selection
		// is different from the old selection.
		// In the case where a header item is dragged over, the row might be
		// null or void.
		if (!row || (oldRow && (row.id != oldRow.id))) {
			this._updateDragSelection(oldRow, false);
		}
	}

	if (!row) { return; }

	// Don't try and select if we are over a header item
	if (this._getItemData(row, "type") != DwtListView.TYPE_LIST_ITEM) { return; }

	// Try and select only if the new row is different from the currently
	// highlighted row.
	if (row.id != this._dragHighlight) {
		this._dragHighlight = row.id;
		this._updateDragSelection(row, true);
	}
};

DwtListView.prototype.dragDeselect =
function(row) {
	if (this._dragHighlight) {
		var oldRow = document.getElementById(this._dragHighlight);
		this._updateDragSelection(oldRow, false);
		this._dragHighlight = null;
	}
};

DwtListView.prototype.scrollToItem =
function(item){
    var el = this._getElFromItem(item);
    if(el){
        this._listDiv.scrollTop = el.offsetTop;
    }
};

DwtListView.prototype.scrollToTop =
function() {
	this._listDiv.scrollTop = 0;
};

/**
 * Scrolls the list view up or down one page.
 *
 * @param {boolean}	up	if true, scroll up
 */
DwtListView.prototype.scrollPage =
function(up) {
	var el = this._parentEl;
	if (el.clientHeight >= el.scrollHeight) { return; }
	el.scrollTop = up ? Math.max(el.scrollTop - el.clientHeight, 0) :
				   		Math.min(el.scrollTop + el.clientHeight, el.scrollHeight - el.clientHeight);
};

DwtListView.prototype.setSortByAsc =
function(column, bSortByAsc) {
	if (!this._headerList) { return; }

	this._bSortAsc = bSortByAsc;
	var columnId = null;
	for (var i = 0; i < this._headerList.length; i++) {
		if (this._headerList[i]._sortable && this._headerList[i]._field == column) {
			columnId = this._headerList[i]._id;
			break;
		}
	}
	if (columnId) {
		this._setSortedColStyle(columnId);
	}
};

DwtListView.prototype.getNewOffset =
function(bPageForward) {
	var limit = this.getLimit();
	var offset = bPageForward ? (this.offset + limit) : (this.offset - limit);
	return (offset < 0) ? 0 : offset;
};

DwtListView.prototype.getLimit =
function() {
	// return the default limit value unless overloaded
	return DwtListView.DEFAULT_LIMIT;
};

DwtListView.prototype.getReplenishThreshold =
function() {
	// return the default threshold value unless overloaded
	return DwtListView.MAX_REPLENISH_THRESHOLD;
};

DwtListView.prototype.getList =
function() {
	return this._list;
};

// this method simply appends the given list to this current one
DwtListView.prototype.replenish =
function(list) {
	this._list.addList(list);

	var size = list.size();
	for (var i = 0; i < size; i++) {
		var item = list.get(i);
		var div = this._createItemHtml(item);
		if (div) {
			this._addRow(div);
		}
	}
};

DwtListView.prototype.getKeyMapName =
function() {
	return "DwtListView";
};

DwtListView.prototype.handleKeyAction =
function(actionCode, ev) {
	switch (actionCode) {
		case DwtKeyMap.SELECT:			this._emulateSingleClick({target:this._kbAnchor, button:DwtMouseEvent.LEFT, kbNavEvent:true}); break;
		case DwtKeyMap.SELECT_CURRENT:	this._emulateSingleClick({target:this._kbAnchor, button:DwtMouseEvent.LEFT, ctrlKey:true, kbNavEvent:true}); break;
		case DwtKeyMap.SELECT_NEXT:		this._selectItem(true, false, true); break;
		case DwtKeyMap.SELECT_PREV:		this._selectItem(false, false, true); break;
		case DwtKeyMap.ADD_SELECT_NEXT: this._selectItem(true, true, true); break;
		case DwtKeyMap.ADD_SELECT_PREV: this._selectItem(false, true, true); break;
		case DwtKeyMap.PREV:			this._setKbFocusElement(false); break;
		case DwtKeyMap.NEXT:			this._setKbFocusElement(true); break;
		case DwtKeyMap.DBLCLICK: {
			if (!this._kbAnchor) { break; }
			var anchorSelected = false;
			var a = this.getSelectedItems().getArray();
			for (var i = 0; i < a.length; i++) {
				if (a[i] == this._kbAnchor) {
					anchorSelected = true;
					break;
				}
			}
			if (anchorSelected) {
				this.emulateDblClick(this.getItemFromElement(this._kbAnchor), true);
			} else {
				this._emulateSingleClick({target:this._kbAnchor, button:DwtMouseEvent.LEFT, kbNavEvent:true});
			}
			break;
		}

		case DwtKeyMap.SELECT_ALL:
			this.selectAll();
			break;

		case DwtKeyMap.SELECT_FIRST:
		case DwtKeyMap.SELECT_LAST:
			var item = (actionCode == DwtKeyMap.SELECT_FIRST) ? this._getFirstItem() : this._getLastItem();
			if (item) {
				this.setSelection(item);
				this._scrollList(this._kbAnchor);
			}
			break;

		case DwtKeyMap.ACTION:
			if (this._evtMgr.isListenerRegistered(DwtEvent.ACTION)) {
				var p = Dwt.toWindow(this._kbAnchor, 0, 0);
				var s = Dwt.getSize(this._kbAnchor);
				var docX = p.x + s.x / 4;
				var docY = p.y + s.y / 2;
				this._emulateSingleClick({target:this._kbAnchor, button:DwtMouseEvent.RIGHT, docX:docX, docY:docY, kbNavEvent:true});
			}
			break;

		case DwtKeyMap.PAGE_UP:
		case DwtKeyMap.PAGE_DOWN:
			this.scrollPage(actionCode == DwtKeyMap.PAGE_UP);
			break;

		default:
			return false;
	}

	return true;
};

DwtListView.prototype.setMultiSelect =
function (enabled) {
	this.multiSelectEnabled = enabled;
};

DwtListView.prototype.isMultiSelectEnabled =
function () {
	return this.multiSelectEnabled;
};

// DO NOT REMOVE - used by xforms
DwtListView.prototype.setListDivHeight =
function (listViewHeight) {
	if (this._listDiv && this._listColDiv) {
		var headerHeight = Dwt.getSize (this._listColDiv).y ;
		//the 10px allows for the diff between container and list for all browsers and eliminates vertical unnecessary scrolls
		var listDivHeight = listViewHeight - headerHeight - 10; 
		Dwt.setSize(this._listDiv, Dwt.DEFAULT, listDivHeight);
	}
};


// Private methods

// normalClass is always present on a list row
DwtListView.prototype._getStyleRegex =
function() {
	return new RegExp("\\b(" + [this._disabledSelectedClass,
								this._selectedClass,
								this._kbFocusClass,
								this._dndClass,
								this._rightClickClass
							   ].join("|") +
					  ")\\b", "g");
};

DwtListView.prototype._addRow =
function(row, index) {

	if (!row || !this._parentEl) { return; }

	// bug fix #1894 - check for childNodes length otherwise IE barfs
	var len = this._parentEl.childNodes.length;
	if (index != null && len > 0 && index != len) {
		this._parentEl.insertBefore(row, this._parentEl.childNodes[index]);
	} else {
		this._parentEl.appendChild(row);
	}
	this._fixAlternation((index != null) ? index : len);
};

DwtListView.prototype._fixAlternation =
function(index) {

	var childNodes = this._parentEl.childNodes;
	if (!(childNodes && childNodes.length)) { return; }
	if (!(this._list && this._list.size())) { return; }

	var row = childNodes[index];
	if (!row) { return; }
	var odd = Boolean(index % 2);
	this._setAlternatingRowClass(row, odd);
	var sibling = row.nextSibling;
	while (sibling) {
		odd = !odd;
		this._setAlternatingRowClass(sibling, odd);
		sibling = sibling.nextSibling;
	}
};

DwtListView.prototype._setAlternatingRowClass =
function(row, odd) {
	var oclass = odd ? DwtListView.ROW_CLASS_ODD : DwtListView.ROW_CLASS_EVEN;
	var nclass = odd ? DwtListView.ROW_CLASS_EVEN : DwtListView.ROW_CLASS_ODD;
	Dwt.delClass(row, oclass, nclass);
};

/**
 * Renders a single item as a DIV element within a list view. The DIV will
 * contain a TABLE with a column for each field. Subclasses will want to
 * override supporting classes at their discretion. At the very least, they
 * will want to override _getCellContents(). Callers can pass
 * in arbitrary info via the params hash, and it will get passed to the
 * support functions.
 *
 * @param {Object}	item			the item to render
 * @param {hash}	params		a hash of optional parameters
 * @param {Date}      params.now			the current time
 * @param {boolean}      params.isDragProxy	if <code>true</code>, we are rendering a the row to be a drag proxy (dragged around the screen)
 * @param {Element}      params.div			the <code>div</code> to fill with content
 * @param {array}      params.headerList	a list of column headers
 * @param	{boolean}	asHtml
 * @param	{number}	idx
 * 
 * @private
 */
DwtListView.prototype._createItemHtml =
function(item, params, asHtml, count) {

	params = params || {};
	this._addParams(item, params, htmlArr, idx);
	var div;

	var htmlArr = [];
	var idx = 0;

	if (asHtml) {
		idx = this._getDivHtml(item, params, htmlArr, idx, count);
	} else {
		div = params.div || this._getDiv(item, params);
	}

	idx = this._getTable(htmlArr, idx, params);
	idx = this._getRow(htmlArr, idx, item, params);

	// Cells
	var headerList = params.headerList || this._headerList;
	if (headerList && headerList.length) {
		for (var colIdx = 0; colIdx < headerList.length; colIdx++) {
			if (!headerList[colIdx]._visible) { continue; }

			var field = headerList[colIdx]._field;
			idx = this._getCell(htmlArr, idx, item, field, colIdx, params);
		}
	} else {
		idx = this._getCell(htmlArr, idx, item, null, null, params);
	}

	htmlArr[idx++] = "</tr></table>";

	if (asHtml) {
		htmlArr[idx++] = "</div>";
		return htmlArr.join("");
	}

	div.innerHTML = htmlArr.join("");
	return div;
};

/**
 * Subclasses can override to add params to pass to functions below.
 *
 * @param {Object}	item			the item to render
 * @param {hash}	params		a hash of optional parameters
 * 
 * @private
 */
DwtListView.prototype._addParams = function(item, params) {};

/**
 * Returns the DIV that contains the item HTML, and sets up styles that will
 * be used to represent its selection state.
 *
 * @param {Object}	item			the item to render
 * @param {hash}	params		a hash of optional parameters
 * 
 * @private
 */
DwtListView.prototype._getDiv =
function(item, params) {

	var	div = document.createElement("div");

	if (params.isDragProxy && AjxEnv.isMozilla) {
		div.style.overflow = "visible";		// bug fix #3654 - yuck
	}

	div.className = this._getDivClass(params.divClass || this._normalClass, item, params);

	if (params.isDragProxy) {
		Dwt.setPosition(div, Dwt.ABSOLUTE_STYLE);
	}

	var id = params.isDragProxy ? this._getItemId(item) + "_dnd" : null;
	this.associateItemWithElement(item, div, null, id);

	return div;
};

/**
 * This is the "HTML" version of the routine above. Instead of returning a DIV
 * element, it returns HTML containing the DIV.
 *
 * @param {Object}	item		the item to render
 * @param {hash}	params	a hash of optional parameters
 * @param {array}	html		the array used to contain HTML code
 * @param {number}	idx		the index used to contain HTML code
 * @param {number}	count		the count of row currently being processed
 * 
 * @private
 */
DwtListView.prototype._getDivHtml =
function(item, params, html, idx, count) {

	html[idx++] = "<div class='";
	html[idx++] = this._getDivClass(params.divClass || this._normalClass, item, params);
	html[idx++] = " ";
	html[idx++] = (count % 2) ? DwtListView.ROW_CLASS_EVEN : DwtListView.ROW_CLASS_ODD;
	html[idx++] = "'";

	var style = [];
	if (params.isDragProxy && AjxEnv.isMozilla) {
		style.push("overflow:visible");		// bug fix #3654 - yuck
	}
	if (params.isDragProxy) {
		style.push("position:absolute");
	}
	if (style.length) {
		html[idx++] = " style='";
		html[idx++] = style.join(";");
		html[idx++] = "'";
	}

	var id = params.isDragProxy ? this._getItemId(item) + "_dnd" : null;
	html[idx++] = " id='";
	html[idx++] = this.associateItemWithElement(item, null, null, id);
	html[idx++] = "'>";

	return idx;
};

/**
 * Returns the name of the class to use for the DIV that contains the HTML for this item.
 * Typically, a modifier is added to a base class for certain types of rows. For example,
 * a row that is created to be dragged will get the class "Row-dnd".
 *
 * @param {string}	base		the name of base class
 * @param {Object}	item		the item to render
 * @param {hash}	params	a hash of optional parameters
 * 
 * @private
 */
DwtListView.prototype._getDivClass =
function(base, item, params) {
	return params.isDragProxy ? ([base, " ", base, "-", DwtCssStyle.DRAG_PROXY].join("")) : base;
};

/**
 * Creates the TABLE that holds the items.
 *
 * @param {array}	htmlArr	the array that holds lines of HTML
 * @param {number}	idx		the current line of array
 * @param {hash}	params	a hash of optional parameters
 * 
 * @private
 */
DwtListView.prototype._getTable =
function(htmlArr, idx, params) {
	htmlArr[idx++] = "<table cellpadding=0 cellspacing=0 border=0 width=";
	htmlArr[idx++] = !params.isDragProxy ? "100%>" : (this.getSize().x + ">");
	return idx;
};

/**
 * Creates a TR for the given item.
 *
 * @param {array}	htmlArr		the array that holds lines of HTML
 * @param {number}	idx		the current line of array
 * @param {object}	item		the item to render
 * @param {hash}	params	a hash of optional parameters
 * 
 * @private
 */
DwtListView.prototype._getRow =
function(htmlArr, idx, item, params) {
	var rowId = this._getRowId(item, params) || Dwt.getNextId();
	var className = this._getRowClass(item, params);
	htmlArr[idx++] = rowId ? ["<tr id='", rowId, "'"].join("") : "<tr";
	htmlArr[idx++] = className ? ([" class='", className, "'>"].join("")) : ">";
	return idx;
};

/**
 * Returns the class name for this item's TR.
 *
 * @param {Object}	item		the item to render
 * @param {hash}	params		a hash of optional parameters
 * 
 * @private
 */
DwtListView.prototype._getRowClass =
function(item, params) {
	return null;
};

/**
 * Returns the DOM ID to be used for this item's TR.
 *
 * @param {Object}	item		the item to render
 * @param {hash}	params		a hash of optional parameters
 * 
 * @private
 */
DwtListView.prototype._getRowId =
function(item, params) {
	return null;
};

/**
 * Creates a TD and its content for a single field of the given item. Subclasses
 * may override several dependent functions to customize the TD and its content.
 *
 * @param htmlArr	[array]		array that holds lines of HTML
 * @param idx		[int]		current line of array
 * @param item		[object]	item to render
 * @param field		[constant]	column identifier
 * @param colIdx	[int]		index of column (starts at 0)
 * @param params	[hash]*		hash of optional params
 * 
 * @private
 */
DwtListView.prototype._getCell =
function(htmlArr, idx, item, field, colIdx, params) {
	var cellId = this._getCellId(item, field, params);
	var idText = cellId ? [" id=", "'", cellId, "'"].join("") : "";
	var width = this._getCellWidth(colIdx, params);
	var widthText = width ? ([" width=", width].join("")) : (" width='100%'");
	var className = this._getCellClass(item, field, params);
	var classText = className ? [" class=", className].join("") : "";
	var alignValue = this._getCellAlign(colIdx, params);
	var alignText = alignValue ? [" align=", alignValue].join("") : "";
	var otherText = (this._getCellAttrText(item, field, params)) || "";
	var attrText = [idText, widthText, classText, alignText, otherText].join(" ");
	htmlArr[idx++] = "<td";
	htmlArr[idx++] = attrText ? (" " + attrText) : "";
	htmlArr[idx++] = ">";
	idx = this._getCellContents(htmlArr, idx, item, field, colIdx, params);
	htmlArr[idx++] = "</td>";

	return idx;
};

/**
 * Returns the width that should be used for the TD, based on the header setup.
 *
 * @param colIdx	[int]		index of column (starts at 0)
 * @param params	[hash]*		hash of optional params
 * 
 * @private
 */
DwtListView.prototype._getCellWidth =
function(colIdx, params) {
	if (colIdx == null) { return null; }
	// IE/Safari do not obey box model properly so we overcompensate :(
	var headerList = params.headerList || this._headerList;
	var width = headerList[colIdx]._width;
	if (width) {
		if (AjxEnv.isIE)		return (width + 2);
		if (AjxEnv.isSafari)	return (width + 5);
		return width;
	}
	return null;
};

DwtListView.prototype._getCellAlign =
function(colIdx, params) {
	if (colIdx == null) { return null; }
	// IE/Safari do not obey box model properly so we overcompensate :(
	var headerList = params.headerList || this._headerList;
	return headerList[colIdx]._align;
};

/**
 * Returns the DOM ID for the TD. The main reasons to provide an ID are to support
 * tooltips, and to be able to update cell content dynamically.
 *
 * @param item		[object]	item to render
 * @param field		[constant]	column identifier
 * @param params	[hash]*		hash of optional params
 * 
 * @private
 */
DwtListView.prototype._getCellId =
function(item, field, params) {
	return null;
};

/**
 * Returns the class to be used for the TD.
 *
 * @param item		[object]	item to render
 * @param field		[constant]	column identifier
 * @param params	[hash]*		hash of optional params
 * 
 * @private
 */
DwtListView.prototype._getCellClass =
function(item, field, params) {
	return null;
};

/**
 * Returns a string of any extra attributes to be used for the TD.
 *
 * @param item		[object]	item to render
 * @param field		[constant]	column identifier
 * @param params	[hash]*		hash of optional params
 * 
 * @private
 */
DwtListView.prototype._getCellAttrText =
function(item, field, params) {
	return null;
};

/**
 * Fills the TD with content. The default implementation converts the item
 * to a string and uses that.
 *
 * @param htmlArr	[array]		array that holds lines of HTML
 * @param idx		[int]		current line of array
 * @param item		[object]	item to render
 * @param field		[constant]	column identifier
 * @param colIdx	[int]		index of column (starts at 0)
 * @param params	[hash]*		hash of optional params
 * 
 * @private
 */
DwtListView.prototype._getCellContents =
function(htmlArr, idx, item, field, colIdx, params) {
	htmlArr[idx++] = item.toString ? item.toString() : item;
	return idx;
};

/**
 * Returns a DOM ID for the given field within the given item.
 *
 * @param item		[object]	item to render
 * @param field		[constant]	column identifier
 * 
 * @private
 */
DwtListView.prototype._getFieldId =
function(item, field) {
	return DwtId.getListViewItemId(DwtId.WIDGET_ITEM_FIELD, this._view, item.id, field);
};

/**
 * Returns the element that represents the given field of the given item.
 * Typically returns either a TD or an img DIV.
 *
 * @param item		[object]	item to render
 * @param field		[constant]	column identifier
 * 
 * @private
 */
DwtListView.prototype._getElement =
function(item, field) {
	return document.getElementById(this._getFieldId(item, field));
};

DwtListView.prototype._getDragProxy =
function(dragOp) {
	var dndSelection = this.getDnDSelection();
	if (!dndSelection) { return null; }

	var icon;
	var div;
	var roundPlusStyle;
	this._dndImg = null;

	if (!(dndSelection instanceof Array) || dndSelection.length == 1) {
		var item = (dndSelection instanceof Array) ? dndSelection[0] : dndSelection;
		icon = this._createItemHtml(item, {now:new Date(), isDragProxy:true});
		this._setItemData(icon, "origClassName", icon.className);
		Dwt.setPosition(icon, Dwt.ABSOLUTE_STYLE);

		roundPlusStyle = "position:absolute;top:18;left:-11;visibility:hidden";
	} else {
		// Create multi one
		icon = document.createElement("div");
		icon.className = "DragProxy";
		Dwt.setPosition(icon, Dwt.ABSOLUTE_STYLE);

		AjxImg.setImage(icon, "DndMultiYes_48");
		this._dndImg = icon;

		div = document.createElement("div");
		Dwt.setPosition(div, Dwt.ABSOLUTE_STYLE);
		var text = this.allSelected ? ZmMsg.all : dndSelection.length;
		div.innerHTML = "<table><tr><td class='DragProxyTextLabel'>"
						+ text + "</td></tr></table>";
		icon.appendChild(div);

		roundPlusStyle = "position:absolute;top:30;left:0;visibility:hidden";

		// The size of the Icon is envelopeImg.width + sealImg.width - 20, ditto for height
		Dwt.setBounds(icon, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE, 43 + 32 - 16, 36 + 32 - 20);
	}

	var imgHtml = AjxImg.getImageHtml("RoundPlus", roundPlusStyle, "id=" + DwtId.DND_PLUS_ID);
	if (!this._noDndPlusImage) {
		icon.appendChild(Dwt.parseHtmlFragment(imgHtml));
	}

	this.shell.getHtmlElement().appendChild(icon);

	// If we have multiple items selected, then we have our cool little dnd icon,
	// so position the text in the middle of the seal
	if (div) {
		var sz = Dwt.getSize(div);
		Dwt.setLocation(div, 16 + (32 - sz.x) / 2, 19 + (32 - sz.y) / 2);
	}

	Dwt.setZIndex(icon, Dwt.Z_DND);
	return icon;
};

DwtListView.prototype._setDragProxyState =
function(dropAllowed) {
	// If we are moving multiple items then set borders & icons, else delegate up
	// to DwtControl.prototype._setDragProxyState()
	if (this._dndImg) {
		AjxImg.setImage(this._dndImg, dropAllowed ? "DndMultiYes_48" : "DndMultiNo_48");
	} else if (this._dndProxy) {
		var addClass = dropAllowed ? DwtCssStyle.DROPPABLE : DwtCssStyle.NOT_DROPPABLE;
		var origClass = this._getItemData(this._dndProxy, "origClassName");
		this._dndProxy.className = [origClass, addClass].join(" ");
	}
};

DwtListView.prototype._setNoResultsHtml =
function() {
	var	div = document.createElement("div");
	var subs = {
		message: this._getNoResultsMessage(),
		type: this.type
	};
	div.innerHTML = AjxTemplate.expand("dwt.Widgets#DwtListView-NoResults", subs);
	this._addRow(div);
};

DwtListView.prototype._getNoResultsMessage =
function() {
	return AjxMsg.noResults;
};

DwtListView.prototype._clearRightSel =
function() {
	if (this._rightSelItem) {
		Dwt.delClass(this._rightSelItem, this._styleRe);	// , this._normalClass MOW
		this._rightSelItem = null;
	}
};

DwtListView.prototype._getItemId =
function(item) {
	return DwtId.getListViewItemId(DwtId.WIDGET_ITEM, this._view, (item && item.id) ? item.id : Dwt.getNextId());
};

DwtListView.prototype._getElFromItem =
function(item) {
	return Dwt.byId(this._getItemId(item));
};

// returns the index of the given item based on the position of the row
// in this list view that represents it
DwtListView.prototype._getRowIndex =
function(item) {
	var id = this._getItemId(item);
	var childNodes = this._parentEl.childNodes;
	for (var i = 0; i < childNodes.length; i++) {
		if (childNodes[i].id == id) {
			return i;
		}
	}
	return null;
};

/**
 * Returns data associated with the given element.
 * 
 * @param el		[Element]	an HTML element
 * @param field		[string]	key for desired data
 * @param id		[string]*	ID that overrides element ID (or if element is not provided)
 * 
 * @private
 */
DwtListView.prototype._getItemData =
function(el, field, id) {
	id = id || (el ? el.id : null);
	var data = this._data[id];
	return data ? data[field] : null;
};

/**
 * Sets data associated with the given element.
 * 
 * @param el		[Element]	an HTML element
 * @param field		[string]	key
 * @param value		[object]	value
 * @param id		[string]*	ID that overrides element ID (or if element is not provided)
 * 
 * @private
 */
DwtListView.prototype._setItemData =
function(el, field, value, id) {
	id = id || (el ? el.id : null);
	var data = this._data[id];
	if (data) {
		data[field] = value;
	}
};

// Return true only if the event occurred in one of our Divs. See DwtControl for more info
DwtListView.prototype._isValidDragObject =
function(ev) {
	return (this.getTargetItemDiv(ev) != null);
};

DwtListView.prototype._updateDragSelection =
function(row, select) {

	if (!row) { return; }
	
    if (!select) {
		row.className = this._getItemData(row, "origClassName");
	} else {
		this._setItemData(row, "origClassName", row.className);
		Dwt.delClass(row, this._styleRe, this._dndClass);
	}
};

DwtListView.prototype._mouseOverAction =
function(mouseEv, div) {
	var type = this._getItemData(div, "type");
	if (type == DwtListView.TYPE_HEADER_ITEM){
		var hdr = this.getItemFromElement(div);
		if (hdr && this.sortingEnabled && hdr._sortable && !this._headerClone) {
			div.className = "DwtListView-Column DwtListView-ColumnHover";
		}
	} else if (type == DwtListView.TYPE_HEADER_SASH) {
		div.style.cursor = AjxEnv.isIE ? "col-resize" : "e-resize";
	}

	return true;
};

DwtListView.prototype._mouseOutAction =
function(mouseEv, div) {
	var type = this._getItemData(div, "type");
	if (type == DwtListView.TYPE_HEADER_ITEM && !this._headerClone) {
		div.className = (div.id != this._currentColId)
			? "DwtListView-Column"
			: "DwtListView-Column DwtListView-ColumnActive";
        var hdr = this.getItemFromElement(div);
        if (!hdr._sortable)
            div.className += " DwtDefaultCursor";
	} else if (type == DwtListView.TYPE_HEADER_SASH) {
		div.style.cursor = "auto";
	}

	return true;
};

DwtListView.prototype._mouseOverListener =
function(ev) {
	var div = this.getTargetItemDiv(ev);
	if (!div) { return; }

	this._mouseOverAction(ev, div);
};

DwtListView.prototype._mouseOutListener =
function(ev) {
	var div = this.getTargetItemDiv(ev);
	if (!div) { return; }

	// NOTE: The DwtListView handles the mouse events on the list items
	//		 that have associated tooltip text. Therefore, we must
	//		 explicitly null out the tooltip content whenever we handle
	//		 a mouse out event. This will prevent the tooltip from
	//		 being displayed when we re-enter the listview even though
	//		 we're not over a list item.
	this.setToolTipContent(null);
	this._mouseOutAction(ev, div);
};

DwtListView.prototype._mouseMoveListener =
function(ev) {
	if (!this._clickDiv) { return; }

	var type = this._getItemData(this._clickDiv, "type");
	if (type == DwtListView.TYPE_HEADER_ITEM) {
		this._handleColHeaderMove(ev);
	} else if (type == DwtListView.TYPE_HEADER_SASH) {
		this._handleColHeaderResize(ev);
	}
};

DwtListView.prototype._mouseDownListener =
function(ev) {
	var div = this.getTargetItemDiv(ev);

	if (!div) {
		this._dndSelection = null;
	} else {
		this._clickDiv = div;
		if (this._getItemData(div, "type") != DwtListView.TYPE_LIST_ITEM) {
			this._dndSelection = null;
		} else {
			this._dndSelection = (this._selectedItems.contains(div)) ? this._selectedItems : div;
		}
	}
	this._mouseDownAction(ev, div);
};

DwtListView.prototype._mouseUpListener =
function(ev) {
	var div = this.getTargetItemDiv(ev);

	var wasDraggingCol = this._handleColHeaderDrop(ev);
	var wasDraggingSash = this._handleColSashDrop(ev);

	if (!div || div != this._clickDiv || wasDraggingCol || wasDraggingSash) {
		delete this._clickDiv;
		this._mouseUpAction(ev, div);
		return;
	}
	delete this._clickDiv;

	var type = this._getItemData(div, "type");
	if (this._headerList && type == DwtListView.TYPE_HEADER_ITEM) {
		if (ev.button == DwtMouseEvent.LEFT) {
			this._columnClicked(div, ev);
		} else if (ev.button == DwtMouseEvent.RIGHT) {
			var actionMenu = this._getActionMenuForColHeader();
			if (actionMenu && actionMenu instanceof DwtMenu) {
				actionMenu.popup(0, ev.docX, ev.docY);
			}
		}
	} else if (type == DwtListView.TYPE_LIST_ITEM) {
		// set item selection, then hand off to derived class for handling
		if (ev.button == DwtMouseEvent.LEFT || ev.button == DwtMouseEvent.RIGHT) {
			this._itemClicked(div, ev);
		}
	}
	this._mouseUpAction(ev, div);
};

// allow subclasses to set props on mouse event
DwtListView.prototype._mouseDownAction = function(mouseEv, div) {};
DwtListView.prototype._mouseUpAction = function(mouseEv, div) {};

DwtListView.prototype._doubleClickAction =
function(mouseEv, div) {return true;};

DwtListView.prototype._doubleClickListener =
function(ev) {
	var div = this.getTargetItemDiv(ev);
	if (!div) { return; }

	var type = this._getItemData(div, "type");
	if (type == DwtListView.TYPE_LIST_ITEM) {
		if (!this._doubleClickAction(ev, div)) {
			return;
		}
		if (this._evtMgr.isListenerRegistered(DwtEvent.SELECTION)) {
			DwtUiEvent.copy(this._selEv, ev);
			this._selEv.item = this.getItemFromElement(div);
			this._selEv.detail = DwtListView.ITEM_DBL_CLICKED;
			this._evtMgr.notifyListeners(DwtEvent.SELECTION, this._selEv);
		}
	}
};

DwtListView.prototype.emulateDblClick =
function(item, kbNavEvent) {
	var div = document.getElementById(this._getItemId(item));
	if (div) {
		var mev = new DwtMouseEvent();
		this._setMouseEvent(mev, {target:div, button:DwtMouseEvent.LEFT});
		mev.kbNavEvent = kbNavEvent;
		this._itemClicked(div, mev);
		this._doubleClickListener(mev);
	}
};

DwtListView.prototype._selectItem =
function(next, addSelect, kbNavEvent) {
	// If there are no elements in the list, then bail
	if (!this.size()) { return; }

	// if there is currently a selection anchor, then find the next/prev item
	// from the anchor
	var itemDiv = (this._kbAnchor)
		? this._getSiblingElement(this._kbAnchor, next)
		: this._parentEl.firstChild;

	this._scrollList(itemDiv);
	this._emulateSingleClick({target:itemDiv, button:DwtMouseEvent.LEFT, shiftKey:addSelect, kbNavEvent:kbNavEvent});
};

DwtListView.prototype._getSiblingElement =
function(element, next) {
	if (!element) { return null; }

	var el = next ? element.nextSibling : element.previousSibling;
	while (this._hasHiddenRows && el && !Dwt.getVisible(el)) {
		el = next ? el.nextSibling : el.previousSibling;
	}
	return (!el || (this._hasHiddenRows && !Dwt.getVisible(el))) ? element : el;
};

/**
 * This method will scroll the list to ensure that <code>itemDiv</code> is
 * scrolled into view.
 * 
 * @private
 */
DwtListView.prototype._scrollList =
function(itemDiv) {
	DwtControl._scrollIntoView(itemDiv, itemDiv.parentNode);
};

DwtListView.prototype._setRowHeight =
function() {
	if (!this._rowHeight) {
		var row = this._parentEl.firstChild;
		this._rowHeight = row && Dwt.getSize(row).y;
	}
};

DwtListView.prototype._emulateSingleClick =
function(params) {
	this._clickDiv = this.findItemDiv(params.target);
	var mev = new DwtMouseEvent();
	this._setMouseEvent(mev, params);
	mev.kbNavEvent = params.kbNavEvent;
	this.notifyListeners(DwtEvent.ONMOUSEUP, mev);
};

/**
 * Sets the anchor row for selection and keyboard nav.
 *
 * @private
 *
 * @param {boolean|Element}		next	row to make anchor, or if true, move anchor
 * 										to next row
 */
DwtListView.prototype._setKbFocusElement =
function(next) {
	// If there are no elements in the list, then bail
	if (!this._list) { return; }

	var orig = this._kbAnchor;
	if (next && next !== true) {
		this._kbAnchor = next;
	} else if (this._kbAnchor) {
		this._kbAnchor = this._getSiblingElement(this._kbAnchor, next);
	} else {
		this._kbAnchor = this._parentEl.firstChild;
	}

	if (this._kbAnchor != orig) {
		if (orig) {
			var selClass = this._selectedClass;
			if (orig.className.indexOf(selClass) != -1) {
				Dwt.delClass(orig, this._styleRe, selClass);
			} else {
				Dwt.delClass(orig, this._styleRe);		// , this._normalClass		MOW
			}
		}
		Dwt.addClass(this._kbAnchor, this._kbFocusClass);
	}

	if (this._kbAnchor) {
		this._scrollList(this._kbAnchor);
	}
};

DwtListView.prototype._itemSelected =
function(itemDiv, ev) {
	if (this._allowLeftSelection(itemDiv, ev, ev && ev.button)) {
		/* Unmark the KB focus element. We need to do this because it is
		 * possible for this element to not be the same as the selection
		 * anchor due to NEXT and PREV keyboard actions */
		this._unmarkKbAnchorElement(true);

		// clear out old left click selection(s)
		this.deselectAll();

		// save new left click selection
		this._selectedItems.add(itemDiv);

		this._selAnchor = this._kbAnchor = itemDiv;
		Dwt.delClass(itemDiv, this._styleRe, this._selectedClass);
		if (this.hasFocus()) {
			Dwt.addClass(itemDiv, this._kbFocusClass);
		}

		var item = this.getItemFromElement(itemDiv);
		this.firstSelIndex = (this._list && item) ? this._list.indexOf(item) : -1;
		//DwtKeyboardMgr.grabFocus(this);
	}
};

DwtListView.prototype._itemClicked =
function(clickedEl, ev) {

	// always clear out old right click selection
	if (this._rightSelItem) {
		Dwt.delClass(this._rightSelItem, this._styleRe);	// , this._normalClass	MOW
		this._rightSelItem = null;
	}

	var numSelectedItems = this._selectedItems.size();
	var bContained = this._selectedItems.contains(clickedEl);

	if ((!ev.shiftKey && !ev.ctrlKey) || !this.multiSelectEnabled) {
		// always reset detail if left/right click
		if (ev.button == DwtMouseEvent.LEFT || ev.button == DwtMouseEvent.RIGHT) {
			this._selEv.detail = DwtListView.ITEM_SELECTED;
		}

		if (ev.button == DwtMouseEvent.LEFT) {
			this._itemSelected(clickedEl, ev);
		}
		else if (ev.button == DwtMouseEvent.RIGHT && !bContained &&
				this._evtMgr.isListenerRegistered(DwtEvent.ACTION))
		{
			// save right click selection
			this._rightSelItem = clickedEl;
            Dwt.delClass(clickedEl, this._styleRe, this._rightClickClass);

			if (this._kbAnchor == clickedEl) {
				Dwt.addClass(clickedEl, this._kbFocusClass);
			}
		}
	} else {
		if (ev.ctrlKey) {
			this.setMultiSelection(clickedEl, bContained, ev);
		} else { // SHIFT KEY
			// Adds to the selection to/from the current node to the selection anchor
			if (!this._selAnchor) { return; }
			var els = this._getChildren() || clickedEl.parentNode.childNodes;
			var numEls = els.length;
			var el;
			var state = 0;
			for (var i = 0; i < numEls; i++) {
				el = els[i];
				if (el == this._rightSelItem) {
					this._rightSelItem = null;
				}

				if (el == clickedEl) {
					/* Increment the state.
					 * 0 - means we havent started
					 * 1 - means we are in selection range
					 * 2 - means we are out of selection range */
					state++;
				}
				var selStyleClass = this._selectedClass;
				if (el == this._selAnchor) {
					state++;
					if (el.className.indexOf(selStyleClass) == -1) {
						this._selectedItems.add(el);
					}
					Dwt.delClass(el, this._styleRe, selStyleClass);
					continue;
				}

				// If state == 0 or 2 (i.e. we are out of the selection range,
				// we have to deselect the node. Else we select it
				if (state != 1 && el.className.indexOf(selStyleClass) != -1 && el != clickedEl) {
					Dwt.delClass(el, this._styleRe);		// , this._normalClass	MOW
					this._selectedItems.remove(el);
				} else if (state == 1 || el == clickedEl) {
					if (el.className.indexOf(selStyleClass) == -1) {
						this._selectedItems.add(el);
					}
					Dwt.delClass(el, this._styleRe, selStyleClass);
				}
			}

			this._kbAnchor = clickedEl;
			Dwt.addClass(this._kbAnchor, this._kbFocusClass);
			//DwtKeyboardMgr.grabFocus(this); // Will cause the kbAnchor element to get the right style

			var newSelectedItems = this._selectedItems.size();
			if (numSelectedItems < newSelectedItems) {
				this._selEv.detail = DwtListView.ITEM_SELECTED;
			} else if (numSelectedItems > newSelectedItems) {
				this._selEv.detail = DwtListView.ITEM_DESELECTED;
			} else {
				return;
			}
		}
	}

	if (ev.button == DwtMouseEvent.LEFT && this._evtMgr.isListenerRegistered(DwtEvent.SELECTION)) {
		if (this._setListEvent(ev, this._selEv, clickedEl)) {
			this._evtMgr.notifyListeners(DwtEvent.SELECTION, this._selEv);
		}
	} else if (ev.button == DwtMouseEvent.RIGHT && this._evtMgr.isListenerRegistered(DwtEvent.ACTION)) {
		if (this._setListEvent(ev, this._actionEv, clickedEl)) {
			this._evtMgr.notifyListeners(DwtEvent.ACTION, this._actionEv);
		}
	}
};

/**
 * Creates a list event from a mouse event. Returns true if it is okay to notify listeners.
 * Subclasses may override to add more properties to the list event.
 *
 * @param	[DwtEvent]		mouse event
 * @param	[DwtEvent]		list event (selection or action)
 * @param	[element]		HTML element that received mouse click
 * 
 * @private
 */
DwtListView.prototype._setListEvent =
function(ev, listEv, clickedEl) {
	DwtUiEvent.copy(listEv, ev);
	listEv.kbNavEvent = ev.kbNavEvent;
	listEv.item = this.findItem(clickedEl);
	return true;
};

DwtListView.prototype._columnClicked =
function(clickedCol, ev) {
	var hdr = this.getItemFromElement(clickedCol);
	if (!(hdr._sortable && this.sortingEnabled)) { return; }

	var list = this.getList();
	var size = list ? list.size() : null;
	var customQuery = this._columnHasCustomQuery(hdr);
	if (!size && !customQuery) { return; }

	// reset order by sorting preference
	this._bSortAsc = (hdr._id == this._currentColId) ? !this._bSortAsc : this._getDefaultSortbyForCol(hdr);

	// reset arrows as necessary
	this._setSortedColStyle(hdr._id);

	// call sorting callback if more than one item to sort
	if (size >= 1 || customQuery) {
		this._sortColumn(hdr, this._bSortAsc);
	}
};

DwtListView.prototype._columnHasCustomQuery =
function(columnItem) {
	// overload me
	return false;
};

DwtListView.prototype._sortColumn =
function(columnItem, bSortAsc) {
	// overload me
};

DwtListView.prototype._getActionMenuForColHeader =
function() {
	// overload me if you want action menu for column headers
	return null;
};

DwtListView.prototype._getDefaultSortbyForCol = 
function(colHeader) {
	// by default, always return ascending
	return true;
};

DwtListView.prototype._allowLeftSelection =
function(clickedEl, ev, button) {
	// overload me (and return false) if you dont want to actually select clickedEl
	return true;
};

DwtListView.prototype._setSortedColStyle = 
function(columnId) {
	
	if (this._currentColId && (columnId != this._currentColId)) {
		// unset current column arrow
		var headerCol = this._headerIdHash[this._currentColId];
		if (headerCol && !headerCol._noSortArrow) {
			var field = headerCol._field;
			var oldArrowId = DwtId.getListViewHdrId(DwtId.WIDGET_HDR_ARROW, this._view, field);
			var oldArrowCell = document.getElementById(oldArrowId);
			if (oldArrowCell && oldArrowCell.firstChild) {
				var imgEl = (AjxImg._mode == AjxImg.SINGLE_IMG) ? oldArrowCell.firstChild : oldArrowCell.firstChild.firstChild;
				if (imgEl) {
					imgEl.style.visibility = "hidden";
				}
			}
		}

		// reset style for old sorted column
		var oldSortedCol = document.getElementById(this._currentColId);
		if (oldSortedCol) {
			oldSortedCol.className = "DwtListView-Column";
		}
	}
	this._currentColId = columnId;
	var headerCol = this._headerIdHash[this._currentColId];

	// set new column arrow
	if (!headerCol._noSortArrow) {
		var field = headerCol._field;
		var newArrowId = DwtId.getListViewHdrId(DwtId.WIDGET_HDR_ARROW, this._view, field);
		var newArrowCell = document.getElementById(newArrowId);
		if (newArrowCell) {
			AjxImg.setImage(newArrowCell, this._bSortAsc ? "ColumnUpArrow" : "ColumnDownArrow");
			var imgEl = (AjxImg._mode == AjxImg.SINGLE_IMG) ? newArrowCell.firstChild : newArrowCell.firstChild.firstChild;
			if (imgEl) {
				imgEl.style.visibility = "visible";
			}
		}
	}
	
	// set new column style
	var newSortedCol = document.getElementById(columnId);
	if (newSortedCol) {
		newSortedCol.className = "DwtListView-Column DwtListView-ColumnActive";
	}
};

DwtListView.prototype._resetList =
function() {
	// clear out old list to force GC
	if (this._list && this._list.size()) {
		this._list.removeAll();
	}
	this._resetListView();
};

DwtListView.prototype._resetListView =
function() {
	// explicitly remove each child (setting innerHTML causes mem leak)
	var cDiv;
	while (this._parentEl && this._parentEl.hasChildNodes()) {
		var cDiv = this._parentEl.removeChild(this._parentEl.firstChild);
		this._data[cDiv.id] = null;
	}
	if (this._selectedItems) {
		this._selectedItems.removeAll();
	}
	this._rightSelItem = null;
};

DwtListView.prototype._destroyDragProxy =
function(icon) {
	this._data[icon.id] = null;
	DwtControl.prototype._destroyDragProxy.call(this, icon);
};

DwtListView.prototype._handleColHeaderMove = 
function(ev) {
	if (!this._headerClone) {
		if (!this._headerColX) {
			this._headerColX = ev.docX;
			return;
		} else {
			var threshold = Math.abs(this._headerColX - ev.docX);
			if (threshold < DwtListView.COL_MOVE_THRESHOLD) { return; }
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
		
		this._headerClone.className = this._clickDiv.className + " DragProxy";
		this._headerClone.innerHTML = this._clickDiv.innerHTML;
		this._clickDiv.className = "DwtListView-Column DwtListView-ColumnEmpty";
		
		// XXX: style hacks - improve this later
		this._headerClone.style.borderTop = "1px solid #777777";

		var headerCol = this._headerIdHash[this._clickDiv.id];
		var field = headerCol._field;
		var hdrLabelId = DwtId.getListViewHdrId(DwtId.WIDGET_HDR_LABEL, this._view, field);
		var labelCell = document.getElementById(hdrLabelId);
		if (labelCell) {
			labelCell.style.color = "#FFFFFF";
		}
		this.shell.getHtmlElement().appendChild(this._headerClone);
	} else {
		var div = this.getTargetItemDiv(ev);
		var type = this._getItemData(div, "type");
		if (type == DwtListView.TYPE_HEADER_ITEM) {
			if (this._headerCloneTarget && (this._headerCloneTarget == this._clickDiv)) {
				this._headerCloneTarget = null;
			} else if (this._headerCloneTarget != div) { 
				this._headerCloneTarget = div;
			}
		} else {
			this._headerCloneTarget = null;
		}
	}

	if (this._headerClone) {
		Dwt.setLocation(this._headerClone, ev.docX + 2);
	}
};

DwtListView.prototype._handleColHeaderResize = 
function(ev) {
	if (!this._headerSash) {
		this._headerSash = document.createElement("div");

		Dwt.setSize(this._headerSash, Dwt.DEFAULT, this.getSize().y);
		Dwt.setPosition(this._headerSash, Dwt.ABSOLUTE_STYLE); 
		Dwt.setZIndex(this._headerSash, Dwt.Z_DND);
		var sashLoc = this._getHeaderSashLocation();
		this._headerSashFudgeX = sashLoc.x;
		Dwt.setLocation(this._headerSash, Dwt.DEFAULT, sashLoc.y);

		this._headerSash.className = "DwtListView-ColumnSash";
		this.getHtmlElement().appendChild(this._headerSash);
		
		// remember the initial x-position
		this._headerSashX = ev.docX;
	}
	
	// always update the sash's position
	var parent = this._getParentForColResize();
	var loc = Dwt.toWindow(parent.getHtmlElement(), 0 ,0);
	Dwt.setLocation(this._headerSash, (ev.docX - loc.x) + this._headerSashFudgeX);
};

DwtListView.prototype._getHeaderSashLocation =
function() {
	if (!this._tmpPoint) {
		this._tmpPoint = new DwtPoint();
	}
	this._tmpPoint.x = 0;
	this._tmpPoint.y = 0;
	return this._tmpPoint;
};

DwtListView.prototype._handleColHeaderDrop = 
function(ev) {
	this._headerColX = null;

	if (this._headerClone == null || ev.button == DwtMouseEvent.RIGHT) { return false; }
	
	// did the user drop the column on a valid target?
	if (this._headerCloneTarget) {
		var divItemIdx = this._getItemData(this._clickDiv, "index");
		var tgtItemIdx = this._getItemData(this._headerCloneTarget, "index");
		this._reIndexColumn(divItemIdx, tgtItemIdx);
	}

	this._clickDiv.className = (this._clickDiv.id != this._currentColId)
		? "DwtListView-Column" : "DwtListView-Column DwtListView-ColumnActive";

	// clean up
	var parent = this._headerClone.parentNode;
	if (parent) {
		parent.removeChild(this._headerClone);
	}
	delete this._headerClone;

	var data = this._data[this._clickDiv.id];
	if (data.type != DwtListView.TYPE_HEADER_ITEM) {
		// something is messed up! redraw the header
		var headerCol = this._headerIdHash[this._currentColId];
		var sortField = headerCol._sortable ? headerCol._field : null;
		this.headerColCreated = false;
		this.createHeaderHtml(sortField);
	} else {
		// reset styles as necessary
		var headerCol = this._headerIdHash[this._clickDiv.id];
		var hdrLabelId = DwtId.getListViewHdrId(DwtId.WIDGET_HDR_LABEL, this._view, headerCol._field);
		var labelCell = document.getElementById(hdrLabelId);
		if (labelCell) {
			labelCell.style.color = "#000000";
		}
	}

	// force all relative widths to be static
	for (var i = 0; i < this._headerList.length; i++) {
		this._headerList[i]._width = this._calcRelativeWidth(i);
	}

	this._resetColWidth();

	return true;
};

DwtListView.prototype._reIndexColumn =
function(columnIdx, newIdx) {
	// do some sanity checks before continuing
	if (!this._headerList) { return; }
	var len = this._headerList.length;
	if (columnIdx < 0 || newIdx < 0 || columnIdx >= len || newIdx >= len || columnIdx == newIdx) { return; }

	// reindex the header list
	var temp = this._headerList.splice(columnIdx, 1);
	this._headerList.splice(newIdx, 0, temp[0]);

	// finally, relayout the list view (incl. header columns)
	this._relayout();
};

/**
 * Per bug #15853, the change in column width will remove width from the last
 * column unless the change makes the width of the last column less than
 * MIN_COLUMN_WIDTH.
 *
 * @param ev
 * 
 * @private
 */
DwtListView.prototype._handleColSashDrop =
function(ev) {
	if (this._headerSash == null || ev.button == DwtMouseEvent.RIGHT) {	return false; }
		
	// destroy the sash
	var parent = this._headerSash.parentNode;
	if (parent) {
		parent.removeChild(this._headerSash);
	}
	delete this._headerSash;

	// force all relative widths to be static
	for (var i = 0; i < this._headerList.length; i++) {
		this._headerList[i]._width = this._calcRelativeWidth(i);
	}

	// find out where the user dropped the sash and update column width
	var headerIdx = this._getItemData(this._clickDiv, "index");
	if (headerIdx == null) { return false; }

	var delta = ev.docX - this._headerSashX;

	var fcol = this._headerList[headerIdx];

	var col1 = fcol;
	var col2;// = this._variableHeaderCol;
	var resized = [];

	if (delta < 0) {
		if ((col1 == col2) || !col2) {
			col2 = this._getNextResizeableColumnHeader(col1);
		}
		if (!col2) return false;
		//delta =    - Math.min(fcol._width - DwtListView.MIN_COLUMN_WIDTH, -delta);
		delta = Math.max(DwtListView.MIN_COLUMN_WIDTH - fcol._width, delta);
		fcol._width = Math.max(fcol._width + delta, DwtListView.MIN_COLUMN_WIDTH);
		col2._width = Math.max(this._calcRelativeWidth(col2._index) - delta, DwtListView.MIN_COLUMN_WIDTH);
		resized.push(fcol, col2);
		
	} else if (delta > 0) {

		var remain = delta;
		while (remain > 0) {
			if ((col1 == col2) || !col2) {
				col2 = this._getNextResizeableColumnHeader(col1, [], false);
			}
			//if (!col2) return false;
			if (!col2) {
				delta -= remain;
				break;
			}
			var col2width = this._calcRelativeWidth(col2._index);
			var room = col2width - DwtListView.MIN_COLUMN_WIDTH;
			
			if (remain > room) { // There column is too small to be fully resized
				remain -= room;
				col2width = DwtListView.MIN_COLUMN_WIDTH;
			} else { // The column is not too small; all the requested delta may be taken from this column
				col2width -= remain;
				remain = 0;
			}
			col2._width = col2width;
			resized.push(col2);
			col1 = col2;
		}
	
		fcol._width = Math.max(fcol._width + delta, DwtListView.MIN_COLUMN_WIDTH);
		resized.push(fcol);

	}

	var col = this._getNextResizeableColumnHeader(-1, resized, true);
	if (col) {
		col._width = "auto";
	}

	this._relayout();
	this._resetColWidth();

	return true;
};

DwtListView.prototype._calcRelativeWidth =
function(headerIdx) {
	var column = this._headerList[headerIdx];
	if (!column._width || (column._width && column._width == "auto")) {
		var cell = document.getElementById(column._id);
		// UGH: clientWidth is 5px more than HTML-width (4px for IE)
		return (cell) ? (cell.clientWidth - (AjxEnv.isIE ? 4 : 5)) : null;
	}
	return column._width;
};

// This method will add padding to the *last* column depending on whether
// scrollbars are shown or not.
DwtListView.prototype._resetColWidth =
function() {

	if (!this.headerColCreated) { return; }

	var lastColIdx = this._getLastColumnIndex();
    if (lastColIdx) {
        var lastCol = this._headerList[lastColIdx];
        var lastCell = document.getElementById(lastCol._id);
		if (lastCell) {
			var div = lastCell.firstChild;
			var scrollbarPad = 16;

			var headerWidth = this._listColDiv.clientWidth;
			var rowWidth = this._listDiv.clientWidth;

			if (headerWidth != rowWidth) {
				lastCell.style.width = div.style.width = (lastCol._width != null && lastCol._width != "auto")
					? (lastCol._width + scrollbarPad)
					: (lastCell.clientWidth + scrollbarPad);
			} else {
				lastCell.style.width = div.style.width = (lastCol._width || "");
			}
		}
    }
};

/**
 * Dynamically get column index for last column b/c columns may or may not be
 * visible.
 */
DwtListView.prototype._getLastColumnIndex =
function() {
	var lastColIdx = null;
	if (this._headerList) {
		var count = this._headerList.length - 1;
		while (lastColIdx == null && count >= 0) {
			if (this._headerList[count]._visible) {
				lastColIdx = count;
			}
			count--;
		}
	}
	return lastColIdx;
};

/**
 * Returns the index of the next resizeable (and visible) column after the one
 * with the given index. If it doesn't find one to the right, starts over at the
 * first column.
 *
 * @param start		[int]		index of reference column
 * @param exclude	[array]		list of indices to exclude
 * 
 * @private
 */
DwtListView.prototype._getNextResizeableColumnIndex =
function(start, exclude, wrap) {

	exclude = exclude ? AjxUtil.arrayAsHash(exclude) : {};
	exclude[start] = true;
	if (this._headerList) {
		for (var i = start + 1; i < this._headerList.length; i++) {
			var col = this._headerList[i];
			if (exclude[i]) { continue; }
			if (col._visible && col._resizeable) {
				return i;
			}
		}
		if (wrap) {
			for (var i = 0; i < start; i++) {
				if (exclude[i]) { continue; }
				var col = this._headerList[i];
				if (col._visible && col._resizeable) {
					return i;
				}
			}
		}
	}
	return null;
};

DwtListView.prototype._getNextResizeableColumnHeader =
function(start, exclude, wrap) {
	var index = this._getNextResizeableColumnIndex(start._index, exclude, wrap);
	return (index !== null) ? this._headerList[index] : false;
}

DwtListView.prototype._relayout =
function() {
	// force relayout of header column
	this.headerColCreated = false;
	var headerCol = this._headerIdHash[this._currentColId];
	var sortField = (headerCol && headerCol._sortable) ? headerCol._field : null;
	var sel = this.getSelection()[0];
	this.setUI(sortField);
	this.setSelection(sel, true);
};

DwtListView.prototype._getParentForColResize = 
function() {
	// overload me to return a higher inheritance chain parent
	return this;
};

DwtListView.prototype._sizeChildren =
function(height) {
	if (this.headerColCreated && this._listDiv && (height != Dwt.DEFAULT)) {
		Dwt.setSize(this._listDiv, Dwt.DEFAULT, height - DwtListView.HEADERITEM_HEIGHT);
		return true;
	} else {
		return false;
	}
};

// overload if parent element's children are not DIV's (i.e. div's w/in a table)
DwtListView.prototype._getChildren = 
function() {
	return null;
};

DwtListView.prototype._focus =
function() {
	if (this.size() == 0) { return; }

	if (this._kbAnchor) {
		Dwt.addClass(this._kbAnchor, this._kbFocusClass);
	} else {
		this._setKbFocusElement();
	}
};

DwtListView.prototype._blur =
function() {
	this._unmarkKbAnchorElement();
};

/**
 * Removes the "focus style" from the current KB anchor.
 * 
 * @param clear		[boolean]*		if true, clear KB anchor
 */
DwtListView.prototype._unmarkKbAnchorElement =
function(clear) {
	if (this._kbAnchor) {
		if (this._selectedItems.contains(this._kbAnchor)) {
			Dwt.delClass(this._kbAnchor, this._styleRe, this._selectedClass);
		} else {
			Dwt.delClass(this._kbAnchor, this._styleRe);	// , this._normalClass		MOW
		}
	}
	if (clear) {
		this._kbAnchor = null;
	}
};

DwtListView.prototype._getFirstItem =
function() {
	var a = this._list.getArray();
	if (a && a.length > 1) {
		return a[0];
	}
	return null;
};

DwtListView.prototype._getLastItem =
function() {
	var a = this._list.getArray();
	if (a && a.length > 1) {
		return a[a.length - 1];
	}
	return null;
};

/**
 * DwtListHeaderItem
 * This is a (optional) "container" class for DwtListView objects which want a
 * column header to appear. Create a new DwtListViewItem for each column header
 * you want to appear. Be sure to specify width values (otherwise, undefined is
 * default)
 *
 * @param params		[hash]		hash of params:
 *        field			[int]		identifier for this column
 *        text	 		[string]*	text shown for the column
 *        icon	 		[string]*	icon shown for the column
 *        width 		[int]*		width of the column
 *        sortable 		[boolean]*	flag indicating whether column is sortable
 *        resizeable 	[boolean]*	flag indicating whether column can be resized
 *        visible 		[boolean]*	flag indicating whether column is initially visible
 *        name 			[string]*	description of column used if column headers have action menu
 * 									- if not supplied, uses label value. This param is
 *									primarily used for columns w/ only an icon (no label).
 *        align			[int]		alignment style of label
 *        noRemove		[boolean]*	flag indicating whether this column can be removed (overrides visible flag)
 *        view			[constant]	ID of owning view
 *        noSortArrow	[boolean]*	if true, do not show up/down sort arrow in column
 *        
 * @private
 */
DwtListHeaderItem = function(params) {

	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtListView.PARAMS);

	this._field = params.field;
	this._label = params.text;
	this._iconInfo = params.icon;
	this._sortable = params.sortable;
	this._noSortArrow = params.noSortArrow;
	this._resizeable = params.resizeable;
	this._visible = (params.visible !== false); // default to visible
	this._name = params.name || params.text;
	this._align = params.align;
	this._noRemove = params.noRemove;
	// width:
	var w = parseInt(params.width);
	if (isNaN(w) || !w) {
		this._width = "auto";
		this._variable = true;
		this._resizeable = true;
	} else if (String(w) == String(params.width)) {
		this._width = w;
	} else {
		this._width = parseInt(String(params.width).substr(0, String(w).length));
		this._widthUnits = AjxStringUtil.getUnitsFromSizeString(params.width);
	}
};

DwtListHeaderItem.PARAMS = ["id", "text", "icon", "width", "sortable", "resizeable", "visible", "name", "align", "noRemove", "view"];

DwtListHeaderItem.sortCompare =
function(a, b) {
	return a._index < b._index ? -1 : (a._index > b._index ? 1 : 0);
};

DwtListHeaderItem.prototype.toString =
function() {
	return "DwtListHeaderItem";
};
