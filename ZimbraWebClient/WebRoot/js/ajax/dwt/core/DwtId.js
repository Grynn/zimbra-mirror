/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010, 2011 VMware, Inc.
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
 * @class
 * This class is responsible for providing unique, predictable IDs for HTML elements.
 * That way, code outside the client can locate particular elements.
 * <p>
 * Not every element that has an associated JS object will have a known ID. Those are
 * allocated only for elements it would be useful to locate: major components of the UI,
 * toolbars, buttons, views, menus, some menu items, and some selects.
 * <p>
 * There is a simple naming scheme for the IDs themselves. Each ID starts with a "z" followed
 * by one to a few letters that indicate the type of object (widget) represented by the element.
 * 
 * @author Conrad Damon
 */
 
DwtId = function() {}

// separator for parts used in constructing IDs - need to pick one that
// doesn't show up in any of the parts
DwtId.SEP = "__";

// widget types (used to prefix IDs)
/**
 * Defines the widget "list view".
 */
DwtId.WIDGET_LIST_VIEW		= "zl";			// list view
/**
 * Defines the widget "list view header".
 */
DwtId.WIDGET_HDR			= "zlh";		// list view header
/**
 * Defines the widget "list view header table".
 */
DwtId.WIDGET_HDR_TABLE		= "zlht";		// list view header table
/**
 * Defines the widget "list view header icon image".
 */
DwtId.WIDGET_HDR_ICON		= "zlhi";		// list view header image
/**
 * Defines the widget "list view header text".
 */
DwtId.WIDGET_HDR_LABEL		= "zlhl";		// list view header text
/**
 * Defines the widget "list view header dropdown arrow".
 */
DwtId.WIDGET_HDR_ARROW		= "zlha";		// list view header dropdown arrow
/**
 * Defines the widget "sash between list view headers".
 */
DwtId.WIDGET_HDR_SASH		= "zlhs";		// sash between list view headers
/**
 * Defines the widget "list view item".
 */
DwtId.WIDGET_ITEM			= "zli";		// list view item
/**
 * Defines the widget "list view item row".
 */
DwtId.WIDGET_ITEM_ROW		= "zlir";		// list view item row
/**
 * Defines the widget "list view item cell".
 */
DwtId.WIDGET_ITEM_CELL		= "zlic";		// list view item cell
/**
 * Defines the widget "list view item field".
 */
DwtId.WIDGET_ITEM_FIELD		= "zlif";		// list view item field

// list view modifiers
/**
 * Defines the list view "headers" modifier.
 */
DwtId.LIST_VIEW_HEADERS	= "headers";
/**
 * Defines the list view "rows" modifier.
 */
DwtId.LIST_VIEW_ROWS	= "rows";

DwtId.IFRAME = "iframe";

DwtId.DND_PLUS_ID		= "z__roundPlus";

/**
 * Joins the given arguments into an ID, excluding empty ones.
 * 
 * @private
 */
DwtId.makeId =
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
DwtId._makeId = DwtId.makeId;	// back-compatibility

/**
 * Gets an ID for a list view.
 * 
 * @param {constant}	context		the owning view identifier
 * @param {DwtId.LIST_VIEW_HEADERS|DwtId.LIST_VIEW_ROWS}	modifier	indicates element within list view (see <code>DwtId.LIST_VIEW*</code> constants)	
 * @return	{string}	the ID
 */
DwtId.getListViewId =
function(context, modifier) {
	return DwtId.makeId(DwtId.WIDGET_LIST_VIEW, context, modifier);
};

/**
 * Gets an ID for an element within a list view header.
 * 
 * @param {constant}	type		the type of hdr element (see <code>DwtId.WIDGET_HDR*</code> constants)
 * @param {constant}	context	the the ID of owning view
 * @param {constant}	hdr		the header ID
 * @return	{string}	the ID
 */
DwtId.getListViewHdrId =
function(type, context, hdr) {
	return DwtId.makeId(type, context, hdr);
};

/**
 * Gets an ID for an element associated with the display of an item in a list view.
 * 
 * @param {constant}	type		the type of item element (see <code>DwtId.WIDGET_ITEM*</code> constants)
 * @param {constant}	context		the ID of owning view
 * @param {string}	itemId	the item ID (typically numeric)
 * @param {constant}	field		the field identifier (for example, "su" for subject)
 * @return	{string}	the ID
 */
DwtId.getListViewItemId =
function(type, context, itemId, field) {
	return DwtId.makeId(type, context, itemId, field);
};

/**
 * Gets an ID for an IFRAME.
 * 
 * @param {constant}	context	the ID of owning {@link DwtIframe}
 * @return	{string}	the ID
 */
DwtId.getIframeId =
function(context) {
	return DwtId.makeId(context, DwtId.IFRAME);
};
