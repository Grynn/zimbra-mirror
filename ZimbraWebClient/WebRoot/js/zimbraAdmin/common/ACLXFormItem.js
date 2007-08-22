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
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* XFormItem class: "acl (composite item)
* this item is used in the Admin UI to display ACL fields like Notebook folder access rights
* @class ACLXFormItem
* @constructor ACLXFormItem
* @author Greg Solovyev
**/
ACLXFormItem = function() {}
XFormItemFactory.createItemType("_ACL_", "acl", ACLXFormItem, Composite_XFormItem);
ACLXFormItem.prototype.numCols = 6;
ACLXFormItem.prototype.nowrap = true;
ACLXFormItem.prototype.visibleBoxes = {r:true,w:true,a:true,i:true,d:true,x:true};
ACLXFormItem.prototype.initializeItems = 
function () {
	var visibleBoxes = this.getInheritedProperty("visibleBoxes");
	this.items = [];
	if(visibleBoxes.r)
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:ZaMsg.ACL_R, relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval["r"]==1);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var newVal = Object();

					if(instanceValue) {
						newVal["w"] = instanceValue["w"];
						newVal["a"] = instanceValue["a"];
						newVal["i"] = instanceValue["i"];
						newVal["d"] = instanceValue["d"];			
						newVal["x"] = instanceValue["x"];			
					} else {
						newVal = {r:0,w:0,i:0,d:0,a:0,x:0};
					}
					newVal["r"] = isChecked ? 1 : 0;
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			}
		);

	if(visibleBoxes.w)
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:ZaMsg.ACL_W, relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval["w"]==1);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var newVal = Object();
					if(instanceValue) {					
						newVal["r"] = instanceValue["r"];
						newVal["a"] = instanceValue["a"];
						newVal["i"] = instanceValue["i"];
						newVal["d"] = instanceValue["d"];			
						newVal["x"] = instanceValue["x"];			
					} else {
						newVal = {r:0,w:0,i:0,d:0,a:0,x:0};
					}
					newVal["w"] = isChecked ? 1 : 0;
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			}
		);

	if(visibleBoxes.d)
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:ZaMsg.ACL_D, relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval["d"]==1);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var newVal = Object();

					if(instanceValue) {										
						newVal["w"] = instanceValue["w"];
						newVal["a"] = instanceValue["a"];
						newVal["i"] = instanceValue["i"];
						newVal["r"] = instanceValue["r"];			
						newVal["x"] = instanceValue["x"];			
					} else {
						newVal = {r:0,w:0,i:0,d:0,a:0,x:0};
					}
					newVal["d"] = isChecked ? 1 : 0;
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			}
		);
		
	if(visibleBoxes.i)
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:ZaMsg.ACL_I, relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval["i"]==1);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var newVal = Object();

					if(instanceValue) {				
						newVal["w"] = instanceValue["w"];
						newVal["a"] = instanceValue["a"];
						newVal["r"] = instanceValue["r"];
						newVal["d"] = instanceValue["d"];			
						newVal["x"] = instanceValue["x"];			
					} else {
						newVal = {r:0,w:0,i:0,d:0,a:0,x:0};
					}
					newVal["i"] = isChecked ? 1 : 0;
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			}
		);		
		
	if(visibleBoxes.x)
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:ZaMsg.ACL_X, relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval["x"]==1);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var newVal = Object();
					if(instanceValue) {				
						newVal["w"] = instanceValue["w"];
						newVal["a"] = instanceValue["a"];
						newVal["i"] = instanceValue["i"];
						newVal["d"] = instanceValue["d"];			
						newVal["r"] = instanceValue["r"];			
					} else {
						newVal = {r:0,w:0,i:0,d:0,a:0,x:0};
					}
					newVal["x"] = isChecked ? 1 : 0;
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			}
		);	
		
	if(visibleBoxes.a)
		this.items.push(	
			{type:_CHECKBOX_,width:"40px",containerCssStyle:"width:40px", forceUpdate:true, ref:".", 
				labelLocation:_RIGHT_, label:ZaMsg.ACL_A, relevantBehavior:_PARENT_,
				getDisplayValue:function (itemval) {
					return (itemval && itemval["a"]==1);
				},
				elementChanged:function(isChecked, instanceValue, event) {
					var newVal = Object();
					if(instanceValue) {				
						newVal["w"] = instanceValue["w"];
						newVal["r"] = instanceValue["r"];
						newVal["i"] = instanceValue["i"];
						newVal["d"] = instanceValue["d"];			
						newVal["x"] = instanceValue["x"];			
					} else {
						newVal = {r:0,w:0,i:0,d:0,a:0,x:0};
					}
					newVal["a"] = isChecked ? 1 : 0;
					this.getForm().itemChanged(this.getParentItem(), newVal, event);
				}
			}
		);				
	Composite_XFormItem.prototype.initializeItems.call(this);
};

ACLXFormItem.prototype.items = [];

