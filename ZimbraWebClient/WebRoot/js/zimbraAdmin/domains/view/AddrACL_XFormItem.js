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
* XFormItem class: _ADDR_ACL_
* this item is used in the Admin UI to display ACls for addresses (groups, accounts, etc)
* @class AddrACL_XFormItem
* @constructor AddrACL_XFormItem
* @author Greg Solovyev
**/
function AddrACL_XFormItem() {}
XFormItemFactory.createItemType("_ADDR_ACL_", "addracl", AddrACL_XFormItem, Composite_XFormItem);
AddrACL_XFormItem.prototype.numCols = 5;
AddrACL_XFormItem.prototype.nowrap = true;
AddrACL_XFormItem.prototype.initializeItems = function() {
	var changeMethod = this.getInheritedProperty("onChange");
	
	if(changeMethod) {
		this.items[0].onChange = changeMethod;
		this.items[1].onChange = changeMethod;		
	} else {
		this.items[0].onChange = null;
		this.items[1].onChange = null;		
	}	
	
	var visibleBoxes = this.getInheritedProperty("visibleBoxes");
	if(visibleBoxes)
		this.items[1].visibleBoxes = visibleBoxes;
		
	var dataFetcherMethod = this.getInheritedProperty("dataFetcherMethod");
	if(dataFetcherMethod)
		this.items[0].dataFetcherMethod = dataFetcherMethod;
	Composite_XFormItem.prototype.initializeItems.call(this);
}
AddrACL_XFormItem.prototype.items = [
	{type:_DYNSELECT_, width:"200px", inputSize:30, ref:"name", editable:true, forceUpdate:true,
		dataFetcherClass:ZaSearch,
		elementChanged:function(val,instanceValue, event) {
			var v = val;
			this.getForm().itemChanged(this, val, event);			
		}
	},
	{type:_ACL_, forceUpdate:true, ref:"acl", labelLocation:_NONE_, label:null}
];

