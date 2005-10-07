/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

function ZaDLListView(parent, className, posStyle, headerList) {
	DwtListView.call(this, parent, className, posStyle, headerList);
}

ZaDLListView.prototype = new DwtListView;
ZaDLListView.prototype.constructor = ZaDLListView;

ZaDLListView.prototype.toString = function() {
	return "ZaDLListView";
};

ZaDLListView.prototype._setNoResultsHtml = function() {
	var buffer = new AjxBuffer();
	var	div = this.getDocument().createElement("div");
	
	buffer.append("<table width='100%' cellspacing='0' cellpadding='1'>",
				  "<tr><td class='NoResults'><br>&nbsp",
				  "</td></tr></table>");
	
	div.innerHTML = buffer.toString();
	this._addRow(div);
};

ZaDLListView.prototype._sortColumn = function (columnItem, bSortAsc){
	if (bSortAsc) {
		var comparator = function (a, b) {
			return (a < b)? 1 :((a > b)? -1 : 0);
		};
		this.getList().sort(comparator);
	} else {
		this.getList().sort();
	}
};

// Since the base class only notifies for itself, 
// we need to do this
ZaDLListView.prototype._itemClicked = function(clickedEl, ev) {
	ev = DwtUiEvent.getEvent(ev);
	DwtListView.prototype._itemClicked.call(this, clickedEl, ev);	
	if (ev.button == DwtMouseEvent.LEFT) {
		// setting up of the selEvent has occurred in the base class
		this._evtMgr.notifyListeners(DwtEvent.SELECTION, this._selEv);
	} else if (ev.button == DwtMouseEvent.RIGHT) {
		// setting up of the actionEv has occurred in the base class
		this._evtMgr.notifyListeners(DwtEvent.ACTION, this._actionEv);
	}
}
