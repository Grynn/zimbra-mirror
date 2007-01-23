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

function DwtListEditView(parent, className, posStyle, headerList, noMaximize) {
	if (arguments.length == 0) return;
	DwtListView.call(this, parent, className, posStyle, headerList, noMaximize);
};

DwtListEditView.prototype = new DwtListView;
DwtListEditView.prototype.constructor = DwtListEditView;

DwtListEditView.prototype.toString =
function() {
	return "DwtListEditView";
};


DwtListEditView.prototype._mouseOverAction =
function(ev, div) {
	var type = Dwt.getAttr(div, "_type");
	var id = ev.target.id || div.id;

	if (id && type == DwtListView.TYPE_LIST_ITEM) {
		this._activeWidget = this._getInlineWidget(id);
		if (this._activeWidget) {
			this._setMouseOut();
			this._showActiveWidget(true, div, id);
		}
	}

	return DwtListView.prototype._mouseOverAction.call(this, ev, div);
};

DwtListEditView.prototype._itemClicked =
function(clickedEl, ev) {
	// if widget exists for current cell, and different cell clicked,
	// hide widget for current cell and do nothing
	// if same cell clicked and not engaged, pass click to widget and do nothing
	// otherwise, if no widget, do nothing

	DwtListView.prototype._itemClicked.call(this, clickedEl, ev);
};

DwtListEditView.prototype._getInlineWidget =
function(id) {
	// do nothing. This method should be overloaded by derived class.
};

DwtListEditView.prototype._setMouseOut =
function() {
	if (!this._activeWidget)
		return;

	if (this._activeWidget instanceof DwtControl) {
		this._activeWidget.addListener(DwtEvent.ONMOUSEOUT, this._activeWidgetMouseOut);
	} else if (this._activeWidget.tagName) {
		this._activeWidget.onmouseout = AjxCallback.simpleClosure(this._activeWidgetMouseOut, this);
	}
};

DwtListEditView.prototype._showActiveWidget =
function(show, element, id) {
	if (!this._activeWidget)
		return;

	var bounds = this._getBoundsForCell(element, id);
	if (bounds) {
		var activeEl = (this._activeWidget instanceof DwtControl)
			? this._activeWidget.getHtmlElement() : this._activeWidget;

		Dwt.setVisibility(activeEl, show);
		if (show) {
			Dwt.setPosition(activeEl, Dwt.ABSOLUTE_STYLE);
			Dwt.setBounds(activeEl, bounds.x, bounds.y, bounds.width, Dwt.DEFAULT);

			this._setValueForActiveWidget(activeEl, id);
		}
	}
};

DwtListEditView.prototype._getBoundsForCell =
function(element, id) {
	var cell = document.getElementById(id);
	var bounds = cell ? Dwt.getBounds(cell) : null;

	// fudge factor for selected item
	var selection = bounds ? this.getSelection() : null;
	if (selection && (selection.length > 1 || selection[0] != this.getItemFromElement(element)))
		bounds.y -= 2;

	return bounds;
};

DwtListEditView.prototype._activeWidgetMouseOut =
function(ev) {
	if (this._activeWidget instanceof DwtControl) {
		this._activeWidget.setVisibility(false);
	} else if (this._activeWidget.tagName) {
		Dwt.setVisibility(this._activeWidget, false);
	}
};

DwtListEditView.prototype._setValueForActiveWidget =
function(activeEl, id) {
	// overload me to set the value for the active element
};
