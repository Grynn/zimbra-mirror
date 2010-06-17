/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaListView
* @constructor ZaListView
* @param parent
* @ className
* @ posStyle
* @ headerList
* Abstract class list views. All the List views in the Admin panel extend this class.
* @author Greg Solovyev
**/
ZaListView = function(parent, className, posStyle, headerList, view) {
	if (arguments.length == 0) return;
	var id = view ? DwtId.getListViewId(view) : null;
	DwtListView.call(this, {parent:parent, className:className, posStyle:posStyle,
							headerList:headerList, view:view, id:id});

	//bug: 18787
	//Set the ListView Div DwtControl.SCROLL(overflow: auto) And the Rows Dwt.VISIBLE
    //In this way, the view of lists can be controlled by the scroll of the list view 
    // At the same time, no list row content will be hidden 
	this.setScrollStyle(DwtControl.SCROLL);
	if (this._listDiv) this._listDiv.style.overflow = "visible";  
}

ZaListView.prototype = new DwtListView;
ZaListView.prototype.constructor = ZaListView;

ZaListView.prototype.toString = 
function() {
	return "ZaListView";
}

ZaListView.ITEM_FLAG_CLICKED = DwtListView._LAST_REASON + 1;

// default implementation
ZaListView.prototype._createItemHtml = function(item) {
	DwtListView.prototype._createItemHtml.call(this,item);
}

ZaListView.prototype.getTitle =
function () {
	return	"";
}

ZaListView.prototype.getTabToolTip =
function () {
	return	this.getTitle ();
}

ZaListView.prototype.getTabIcon = 
function () {
	return "" ;
}

ZaListView.prototype.getTabTitle =
function () {
	return this.getTitle() ;
}

ZaListView.prototype._mouseOverAction =
function(ev, div) {
	var _type = this._getItemData(div,"type");
	if (_type == DwtListView.TYPE_HEADER_ITEM) {
		if(this._headerList[this._getItemData(div,"index")]._sortable) {
			div.className = "DwtListView-Column DwtListView-ColumnHover";		
			this.setToolTipContent(AjxMessageFormat.format(ZaMsg.LST_ClickToSort_tt, [this._headerList[this._getItemData(div,"index")].getLabel()]));	
		} else {
			this.setToolTipContent(null);
		}
	} else if (_type == DwtListView.TYPE_HEADER_SASH) {
		div.style.cursor = AjxEnv.isIE ? "col-resize" : "e-resize";
    } else if (_type == DwtListView.TYPE_LIST_ITEM){
		var item = this.getItemFromElement(div);
		if (item && item.getToolTip) {
			var tt_content = "" ;
			try {	
				 //if (AjxEnv.hasFirebug) console.log("Item: " + item.name) ;
				 tt_content = item.getToolTip() ;
			}catch (e) {
				 tt_content = e.msg ;
			}
			this.setToolTipContent(tt_content);
        }
    }
	return true;
}


ZaListView.prototype._mouseOutAction = 
function(mouseEv, div) {
	var _type = this._getItemData(div,"type");
	if (_type == DwtListView.TYPE_HEADER_ITEM) {
		if(this._headerList[this._getItemData(div,"index")]._sortable) {
			div.className = (div.id != this._currentColId) ? "DwtListView-Column" : "DwtListView-Column DwtListView-ColumnActive"
		}
	}else if (_type == DwtListView.TYPE_HEADER_SASH) {
		div.style.cursor = "auto";
	}
	return true;
}

ZaListView.prototype._setListEvent =
function (ev, listEv, clickedEl) {
	DwtListView.prototype._setListEvent.call(this, ev, listEv, clickedEl);
	var parts = ev.target.id.split(DwtId.SEP);
	listEv.field = parts && parts[2];
	return true;
}

ZaListView.prototype._sortColumn = 
function(columnItem, bSortAsc) {
	if (bSortAsc) {
		this._list.sort(ZaItem.compareNamesAsc);
	} else {
    	this._list.sort(ZaItem.compareNamesDesc);
	}
	this.setUI();
}

ZaListView.prototype._setNoResultsHtml =
function () {
	if (ZaSearch.TOO_MANY_RESULTS_FLAG ){
		var htmlArr = new Array(3);
		var idx = 0;
	
		htmlArr[idx++] = "<table width='100%' cellspacing='0' cellpadding='1'><tr><td class='NoResults'><br>";
		htmlArr[idx++] = ZaMsg.TooManyResults;
		htmlArr[idx++] = "</td></tr></table>";
	
		var	div = document.createElement("div");
		div.innerHTML = htmlArr.join("");
		this._addRow(div);
	}else{
		DwtListView.prototype._setNoResultsHtml.call (this) ;
	}
}


ZaListView.prototype.setHeaderList = function(headerList) {
	this._headerList = headerList;
	this.headerColCreated = false;
}

ZaListHeaderItem = function(idPrefix, text, iconInfo, width, sortable, sortField, resizeable, visible) {
	DwtListHeaderItem.call(this, {field:idPrefix, text:text, icon:iconInfo, width:width, sortable:sortable,
								  resizeable:resizeable, visible:visible});
	this._sortField = sortField;	
	this._initialized = false;
}

ZaListHeaderItem.prototype = new DwtListHeaderItem;
ZaListHeaderItem.prototype.constructor = ZaListHeaderItem;


ZaListHeaderItem.prototype.getSortField = 
function() {
	return this._sortField;
}

ZaListHeaderItem.prototype.getLabel = 
function () {
	return this._label;
}