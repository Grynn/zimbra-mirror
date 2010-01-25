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
function com_zimbra_example_paneldragsource_HandlerObject() {
}

/**
 * Makes the Zimlet class a subclass of ZmZimletBase.
 *
 */
com_zimbra_example_paneldragsource_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_example_paneldragsource_HandlerObject.prototype.constructor = com_zimbra_example_paneldragsource_HandlerObject;

/**
 * This method gets called by the Zimlet framework when the zimlet loads.
 *  
 */
com_zimbra_example_paneldragsource_HandlerObject.prototype.init =
function() {
	// do something
};


/**
 * This method gets called by the Zimlet framework when an item or items are dropped on the panel.
 * 
 * @param	obj		the dropped object
 */
com_zimbra_example_paneldragsource_HandlerObject.prototype.doDrop =
function(obj) {

	var type = obj.TYPE;
	switch(type) {
		case "ZmAppt": {
			// do something with ZmAppt
			break;
		}
		case "ZmContact": {
			// do something with ZmContact
			break;
		}
		case "ZmConv": {
			// do something with ZmConv
			break;
		}
		case "ZmMailMsg": {
			// do something with ZmMailMsg
			break;
		}
	}

};

