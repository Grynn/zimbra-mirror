/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Defines the Zimlet handler class.
 *   
 */
function com_zimbra_example_menuitemsjs_HandlerObject() {
}

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_menuitemsjs_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_menuitemsjs_HandlerObject.prototype.constructor = com_zimbra_example_menuitemsjs_HandlerObject;

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_menuitemsjs_HandlerObject.prototype.init =
function() {
	// do something
};

/**
 * This method gets called by the Zimlet framework when a context menu item is selected.
 * 
 * @param	itemId		the Id of selected menu item
 */
com_zimbra_example_menuitemsjs_HandlerObject.prototype.menuItemSelected =
function(itemId) {
	switch (itemId) {
		case "SOME_MENU_ITEM_ID1":
			window.open ("http://www.yahoo.com",
					"mywindow","menubar=1,resizable=1,width=800,height=600"); 
			break;
		case "SOME_MENU_ITEM_ID2":
			window.open ("http://sports.yahoo.com",
					"mywindow","menubar=1,resizable=1,width=800,height=600"); 
			break;
		default:
			// do nothing
			break;
	}

};
