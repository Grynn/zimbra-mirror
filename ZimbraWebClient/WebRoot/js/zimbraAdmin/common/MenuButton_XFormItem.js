/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010 VMware, Inc.
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
* @class defines XFormItem type  _MENU_BUTTON_
* Adapts a DwtButton with a drop-down menu of choices to work with the XForm
* @constructor
* @author Greg Solovyev
**/
MenuButton_XFormItem = function() {}
XFormItemFactory.createItemType("_MENU_BUTTON_", "menu_button", MenuButton_XFormItem, Dwt_Button_XFormItem);
MenuButton_XFormItem.prototype.constructWidget = function () {
	var widget = Dwt_Button_XFormItem.prototype.constructWidget.call(this);
	var opList = this.getNormalizedValues();
	if (opList && opList.length) {
		var menu = new ZaPopupMenu(widget, null,null, opList);
		widget.setMenu(menu);
	}
	return widget;
}