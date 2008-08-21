/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */


/**
 * Creates a Header Tree Item.
 * @constructor
 * @class
 * This class implements a tree item widget.
 *
 * @author Dave Comfort
 *
 * @param params		[hash]				hash of params:
 *        parent		[DwtComposite] 		parent widget
 *        index 		[int]*				index at which to add this control among parent's children
 *        text 			[string]*			label text for the tree item
 *        imageInfo		[string]*			icon for the tree item
 *        deferred		[boolean]*			If true, postpone initialization until needed.
 *        className		[string]*			CSS class
 *        posStyle		[constant]*			positioning style
 *        forceNotifySelection	[boolean]*	force notify selection even if checked style
 *        forceNotifyAction		[boolean]*	force notify action even if checked style
 * 		  button		[hash]*				hash of data for showing a button in the item: image, tooltip, callback
 */
DwtHeaderTreeItem = function(params) {
	this.overview = params.overview;
	this._button = params.button;
	DwtTreeItem.call(this, params);
}

DwtHeaderTreeItem.prototype = new DwtTreeItem;
DwtHeaderTreeItem.prototype.constructor = DwtHeaderTreeItem;

DwtHeaderTreeItem.prototype.TEMPLATE = "dwt.Widgets#ZHeaderTreeItem";

DwtHeaderTreeItem.prototype.toString =
function() {
	return "DwtHeaderTreeItem";
};

DwtHeaderTreeItem.prototype._initialize =
function() {
	DwtTreeItem.prototype._initialize.apply(this, arguments);
	if (this._button) {
		this._headerButtonId = this._htmlElId + "_headerButton";
		var buttonEl = document.getElementById(this._headerButtonId);
		if (buttonEl) {
			buttonEl.className = "Img" + this._button.image;
			buttonEl.onclick = AjxCallback.simpleClosure(this._onclickHandler, this);
			var mouseOverListener = new AjxListener(this, this._mouseOverListener);
			var mouseOutListener = new AjxListener(this, this._mouseOutListener);
			this.addListener(DwtEvent.ONMOUSEOVER, mouseOverListener);
			this.addListener(DwtEvent.ONMOUSEENTER, mouseOverListener);
			this.addListener(DwtEvent.ONMOUSEOUT, mouseOutListener);
			this.addListener(DwtEvent.ONMOUSELEAVE, mouseOutListener);
		}
	}
};

DwtHeaderTreeItem.prototype._onclickHandler =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev, this);
	this._button.callback.run(mouseEv);
};

DwtHeaderTreeItem.prototype._mouseOverListener =
function(ev) {
	var el = DwtUiEvent.getTarget(ev);
	if (el && (el.id == this._headerButtonId)) {
		this.setToolTipContent(this._button.tooltip);
	}
};

DwtHeaderTreeItem.prototype._mouseOutListener =
function() {
	this.setToolTipContent(null);
};


