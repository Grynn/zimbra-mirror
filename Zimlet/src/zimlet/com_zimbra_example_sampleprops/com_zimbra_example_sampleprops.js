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

com_zimbra_example_sampleprops_HandlerObject = function() {
};
com_zimbra_example_sampleprops_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_sampleprops_HandlerObject.prototype.constructor = com_zimbra_example_sampleprops_HandlerObject;


/**
 * This method is called by the Zimlet framework when a menu item is selected.
 * 
 */
com_zimbra_example_sampleprops_HandlerObject.prototype.menuItemSelected = 
function(itemId) {
	var str = this.getMessage("helloworld_status");
	switch (itemId) {
		case "sampleprops_menuItemId":
			appCtxt.getAppController().setStatusMsg(str);
			break;
	}
};