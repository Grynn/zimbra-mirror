/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
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

ZaOverviewPanel = function(parent, className, posStyle) {
	DwtComposite.call(this, parent, className, posStyle);
	this.addControlListener(new AjxListener(this, this._panelControlListener));
	this._createFolderTree();
	this._layout();
}

ZaOverviewPanel.prototype = new DwtComposite();
ZaOverviewPanel.constructor = ZaOverviewPanel;

ZaOverviewPanel._MIN_FOLDERTREE_SIZE = 100;

ZaOverviewPanel.prototype.toString = 
function() {
	return "ZaOverviewPanel";
}

ZaOverviewPanel.prototype.getFolderTree =
function() {
	return this._tree;
}

ZaOverviewPanel.prototype._createFolderTree =
function() {
	this._treePanel = new DwtComposite(this, "OverviewTreePanel", DwtControl.ABSOLUTE_STYLE);
	this._tree = new DwtTree(this._treePanel, DwtTree.SINGLE_STYLE, "OverviewTree" , DwtControl.ABSOLUTE_STYLE);
}
	
ZaOverviewPanel.prototype._layout =
function() {
	var opSz = this.getSize();
//	opSz.x+=100;
	var h = opSz.y;
//	h = (h > ZaOverviewPanel._MIN_FOLDERTREE_SIZE) ? h : ZaOverviewPanel._MIN_FOLDERTREE_SIZE;
	
	this._treePanel.setBounds(0, 0, opSz.x, h);
//	var tfBds = this._treePanel.getBounds();
}

ZaOverviewPanel.prototype._panelControlListener =
function(ev) {
	this._layout();
}
