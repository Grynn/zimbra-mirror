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
 
// widget types (used to prefix IDs)
DwtId.WIDGET_HDR			= "zh_";		// list view header
DwtId.WIDGET_HDR_TABLE		= "zht_";		// list view header table
DwtId.WIDGET_HDR_ICON		= "zhi_";		// list view header image
DwtId.WIDGET_HDR_LABEL		= "zhl_";		// list view header text
DwtId.WIDGET_HDR_ARROW		= "zha_";		// list view header dropdown arrow
DwtId.WIDGET_HDR_SASH		= "zhs_";		// sash between list view headers

/**
 * Returns an ID for an element within a list view header.
 * 
 * @param type		[constant]*		type of hdr element (DwtId.WIDGET_HDR*)
 * @param view		[constant]*		ID of owning view
 * @param hdr		[constant]*		header ID
 */
DwtId.getListViewHdrId =
function(type, view, hdr) {
	var prefix = [type, view.toLowerCase()].join("");
	return hdr ? [prefix, hdr].join("_") : prefix;
};

