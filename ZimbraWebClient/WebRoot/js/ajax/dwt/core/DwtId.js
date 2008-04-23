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

/**
 * This class is responsible for providing unique, predictable IDs for HTML elements.
 * That way, code outside the client can locate particular elements.
 * 
 * Not every element that has an associated JS object will have a known ID. Those are
 * allocated only for elements it would be useful to locate: major components of the UI,
 * toolbars, buttons, views, menus, some menu items, and some selects.
 * 
 * There is a simple naming scheme for the IDs themselves. Each ID starts with a "z" followed
 * by one to a few letters that indicate the type of object (widget) represented by the element.
 * 
 * @author Conrad Damon
 */
 
DwtId = function() {}

// separator for parts used in constructing IDs - need to pick one that
// doesn't show up in any of the parts
DwtId.SEP = "|";

// widget types (used to prefix IDs)
DwtId.WIDGET_LIST_VIEW		= "zl";			// list view
DwtId.WIDGET_HDR			= "zlh";		// list view header
DwtId.WIDGET_HDR_TABLE		= "zlht";		// list view header table
DwtId.WIDGET_HDR_ICON		= "zlhi";		// list view header image
DwtId.WIDGET_HDR_LABEL		= "zlhl";		// list view header text
DwtId.WIDGET_HDR_ARROW		= "zlha";		// list view header dropdown arrow
DwtId.WIDGET_HDR_SASH		= "zlhs";		// sash between list view headers
DwtId.WIDGET_ITEM			= "zli";		// list view item
DwtId.WIDGET_ITEM_ROW		= "zlir";		// list view item row
DwtId.WIDGET_ITEM_CELL		= "zlic";		// list view item cell
DwtId.WIDGET_ITEM_FIELD		= "zlif";		// list view item field

// list view modifiers
DwtId.LIST_VIEW_HEADERS	= "headers";
DwtId.LIST_VIEW_ROWS	= "rows";

/**
 * Joins the given arguments into an ID, excluding empty ones.
 */
DwtId._makeId =
function() {
	var list = [];
	for (var i = 0; i < arguments.length; i++) {
		var arg = arguments[i];
		if (arg != null && arg != "") {
			list.push(arg);
		}
	}
	return list.join(DwtId.SEP);
};

/**
 * Returns an ID for a list view.
 * 
 * @param context	[const]		owning view identifier
 * @param modifier	[const]		indicates element within list view (DwtId.LIST_VIEW_*)	
 */
DwtId.getListViewId =
function(context, modifier) {
	return DwtId._makeId(DwtId.WIDGET_LIST_VIEW, context, modifier);
};

/**
 * Returns an ID for an element within a list view header.
 * 
 * @param type		[constant]		type of hdr element (DwtId.WIDGET_HDR*)
 * @param context	[constant]		ID of owning view
 * @param hdr		[constant]*		header ID
 */
DwtId.getListViewHdrId =
function(type, context, hdr) {
	return DwtId._makeId(type, context, hdr);
};

/**
 * Returns an ID for an element associated with the display of an item in a list view.
 * 
 * @param type		[const]		type of item element (DwtId.WIDGET_ITEM*)
 * @param context	[const]		ID of owning view
 * @param itemId	[string]	item ID (typically numeric)
 * @param field		[const]*	field identifier (eg "su" for subject)
 */
DwtId.getListViewItemId =
function(type, context, itemId, field) {
	return DwtId._makeId(type, context, itemId, field);
};
