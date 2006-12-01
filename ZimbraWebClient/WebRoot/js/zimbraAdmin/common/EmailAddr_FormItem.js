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
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* XFormItem class: "emailaddr (composite item)
* this item is used in the Admin UI to display email address fields like alias and account name
* @class EmailAddr_XFormItem
* @constructor EmailAddr_XFormItem
* @author Greg Solovyev
**/
function EmailAddr_XFormItem() {}
XFormItemFactory.createItemType("_EMAILADDR_", "emailaddr", EmailAddr_XFormItem, Composite_XFormItem);
EmailAddr_XFormItem.domainChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "name", "name");
EmailAddr_XFormItem.choicesDirty = false ;
EmailAddr_XFormItem.prototype.numCols = 4;
EmailAddr_XFormItem.prototype.nowrap = true;
EmailAddr_XFormItem.prototype.initializeItems = 
function () {
	this._inputWidth = this.getInheritedProperty("inputWidth");
	if (this._inputWidth == null) this._inputWidth = 200;
	this.items[0].width = this._inputWidth;

	Composite_XFormItem.prototype.initializeItems.call(this);
	try {
		if(this.getForm().parent._app) {
			this._domainPart = this.getForm().parent._app.getGlobalConfig().attrs[ZaGlobalConfig.A_zimbraDefaultDomainName]
		}
	} catch (ex) {
		this._domainPart = null;
	}
	if(this._domainPart == null) {
		if(EmailAddr_XFormItem.domainChoices) {
			if(EmailAddr_XFormItem.domainChoices._choiceObject.length >0) {
				if(EmailAddr_XFormItem.domainChoices._choiceObject[0]) {
					this._domainPart = EmailAddr_XFormItem.domainChoices._choiceObject[0].name;
				}	
			}
		}
	}
};

EmailAddr_XFormItem.prototype.items = [
	{type:_TEXTFIELD_,forceUpdate:true, ref:".", labelLocation:_NONE_,relevantBehavior:_PARENT_, cssClass:"admin_xform_name_input",
	 errorLocation:_PARENT_,
		getDisplayValue:function (itemVal) {
			var val = itemVal;
			if(val) {
				var emailChunks = val.split("@");

				if(emailChunks.length > 1 ) {
					val = emailChunks[0];
				} 
				
			} 

			if(val === null || val ===undefined)
				val = "";
				
			this.getParentItem()._namePart = val;
			return val;	
		},
		elementChanged:function(namePart, instanceValue, event) {
			var val = namePart + "@";
			if(this.getParentItem()._domainPart)
				val += this.getParentItem()._domainPart;

			this.getParentItem()._namePart = val;	
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}
	},
	{type:_OUTPUT_, value:"@"},
	{type:_OUTPUT_,ref:".",relevant:"!ZaSettings.DOMAINS_ENABLED", relevantBehavior:_HIDE_,
		choices:EmailAddr_XFormItem.domainChoices,
		getDisplayValue:function (itemVal){
			var val = null;
			if(itemVal) {
				var emailChunks = itemVal.split("@");
			
				if(emailChunks.length > 1 ) {
					val = emailChunks[1];
				} 
			}
			if(!val) {
//				if(this.getChoices() && this.getChoices()._choiceObject && this.getChoices()._choiceObject[0])
					val = this.getParentItem()._domainPart;
			}	
	//		this.getParentItem()._domainPart = val;
			
			return val;
		}	
	},
	{type:_OSELECT1_, ref:".", labelLocation:_NONE_, relevantBehavior:_HIDE_, 
	 choices:EmailAddr_XFormItem.domainChoices,editable:true,
	 relevant:"ZaSettings.DOMAINS_ENABLED",
	 errorLocation:_PARENT_,
		getDisplayValue:function (itemVal){
			var val = null;
			if(itemVal) {
				var emailChunks = itemVal.split("@");
			
				if(emailChunks.length > 1 ) {
					val = emailChunks[1];
				} 
			}
			if(!val) {
				if(!this.getParentItem()._domainPart) {
					if(this.getChoices() && this.getChoices()._choiceObject && this.getChoices()._choiceObject[0]) {
						val = this.getChoices()._choiceObject[0].name;
						this.getParentItem()._domainPart = val;
					}
				} else {
					val = this.getParentItem()._domainPart;
				}
			} else {
				this.getParentItem()._domainPart = val;
			}
			
			return val;
		},
		elementChanged:function(domainPart,instanceValue, event) {
			var val;
			var oldDomainPart = this.getParentItem()._domainPart;
			if(this.getParentItem()._namePart) {
				val = this.getParentItem()._namePart + "@" + domainPart;
			} else {
				val = "@" + domainPart;
			}
			if(domainPart) {
				var el = this.getDisplayElement();
				if (el) {
					if(this.getInheritedProperty("editable")) {
						el.value = domainPart;
					} else {
						el.innerHTML = domainPart;
					}
				}	
			}
			this.getParentItem()._domainPart = domainPart;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		},
		keyUp:function(newValue,ev) {
			var n = "";
			if(newValue)
				n = String(newValue).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
				
			var query = "(zimbraDomainName="+n+"*)";
			this.getForm().getController().searchDomains(query);
			EmailAddr_XFormItem.choicesDirty = true ;
		}
	}
];

//reset the domainchoices for the domain list menu, bug 12495
EmailAddr_XFormItem.resetDomainLists =
function (force) {
	if (force || EmailAddr_XFormItem.choicesDirty) {
		this._app.searchDomains();
		 EmailAddr_XFormItem.choicesDirty = false ;
	}
}
