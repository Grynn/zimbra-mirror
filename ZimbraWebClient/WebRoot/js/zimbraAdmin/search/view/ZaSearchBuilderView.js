/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaSearchBuilderView
* @contructor ZaSearchBuilderView
* Class to create the advance search options panel view
* @author Charles Cao
**/

ZaSearchBuilderView = function(parent){
	DwtComposite.call(this, parent, "ZaSearchBuilderView", Dwt.ABSOLUTE_STYLE, true);		
	var visible = false ;
	this.zShow(visible);
	this._app = ZaApp.getInstance();
	
	this._option_next_x = 0;
	this._controller = ZaApp.getInstance().getSearchBuilderController () ;
	this.setScrollStyle(DwtControl.SCROLL);
}

ZaSearchBuilderView.prototype = new DwtComposite();
ZaSearchBuilderView.prototype.constructor = ZaSearchBuilderView;

ZaSearchBuilderView.prototype.toString = 
function() {
	return "ZaSearchBuilderView";
}

ZaSearchBuilderView.prototype.getNextOptionX = 
function (position) {
	if (position == null) {
		position = this._controller._option_views.length ;
	}
	if ( position <= 0 ){
		return ZaSearchOptionView.MARGIN; 
	}else{
		var prevOption = this._controller._option_views[position -1];
		return prevOption.getX () + prevOption.getW () + ZaSearchOptionView.MARGIN;
	}
}