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
* XFormItem class: HOST PORT
* this item is used in the Admin UI to display Host name fields like Relay MTA host
* @class HostPort_XFormItem
* @constructor HostPort_XFormItem
* @author Greg Solovyev
**/
function HostPort_XFormItem() {}
XFormItemFactory.createItemType("_HOSTPORT_", "hostport", HostPort_XFormItem, Composite_XFormItem);
HostPort_XFormItem.prototype.numCols = 3;
HostPort_XFormItem.prototype.nowrap = true;
HostPort_XFormItem.prototype._serverPart = "";
HostPort_XFormItem.prototype._portPart = "";

HostPort_XFormItem.prototype.items = [
	{type:_TEXTFIELD_, width:"200px", forceUpdate:true, ref:".", labelLocation:_NONE_, label:null,relevantBehavior:_PARENT_,
		required:true,
		getDisplayValue:function (itemVal) {
			var val = "";
			if(itemVal) {
				var URLChunks = itemVal.split(":");
				if(URLChunks.length >= 2) {
					val = URLChunks[0];
				} else {
					val = itemVal;
				}
				this.getParentItem()._serverPart = val;
			} 
			return val;	
		},
		elementChanged:function(serverPart, instanceValue, event) {
			this.getParentItem()._serverPart = serverPart;
			var val = "";
			if(serverPart) {
				val = serverPart;
			}
			if(this.getParentItem()._portPart) {
				val += ":";
				val += this.getParentItem()._portPart;				
			}
			this.getForm().itemChanged(this.getParentItem(), val, event);
		},
		onClick: "Super_HostPort_XFormItem.handleClick",
		onMouseout: "Super_HostPort_XFormItem.handleMouseout"
	},
	{type:_OUTPUT_, width:"5px", labelLocation:_NONE_, label:null,relevantBehavior:_PARENT_,value:":"},
	{type:_TEXTFIELD_,width:"40px",forceUpdate:true, ref:".", labelLocation:_NONE_, label:null, relevantBehavior:_PARENT_, 
		getDisplayValue:function (itemVal) {
			var val = "";
			if(itemVal) {
				var URLChunks = itemVal.split(":");
				if(URLChunks.length == 2) {
					val = URLChunks[1];
				} else {
					val = "";
				}
				this.getParentItem()._portPart = val;
			} 
			return val;	
		},
		elementChanged:function(portPart, instanceValue, event) {
			this.getParentItem()._portPart = portPart;
			var val = "";
			if(this.getParentItem()._serverPart) {
				val = this.getParentItem()._serverPart;
			}
			if(portPart) {
				val +=":";
				val+=portPart;
			}
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}
	}
];
