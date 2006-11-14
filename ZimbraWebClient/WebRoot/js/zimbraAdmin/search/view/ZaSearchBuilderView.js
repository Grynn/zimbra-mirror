/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
 
/**
* @class ZaSearchBuilderView
* @contructor ZaSearchBuilderView
* Class to create the advance search options panel view
* @author Charles Cao
**/

function ZaSearchBuilderView (parent, app){
	DwtComposite.call(this, parent, "ZaSearchBuilderView", Dwt.ABSOLUTE_STYLE, true);		
	var visible = false ;
	this.zShow(visible);
	this._app = app;
	
	this._option_next_x = 0;
	this._controller = this._app.getSearchBuilderController () ;
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