/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
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
* XFormItem class: "dynselect_domain_part"
* A select box with asynchronous autocomplete capability
* @class DynSelectDomainPart_XFormItem
* @constructor DynSelectDomainPart_XFormItem
* @author Greg Solovyev
**/
DynSelectDomainPart_XFormItem = function() {}
XFormItemFactory.createItemType("_DYNSELECT_DOMAIN_PART_", "dynselect_domain_part", DynSelectDomainPart_XFormItem, DynSelect_XFormItem);
DynSelectDomainPart_XFormItem.prototype.inputSize = 35 ;
DynSelectDomainPart_XFormItem.prototype.handleKeyPressDelay = function (event,value) {
	if(!this.dataFetcherObject && this.dataFetcherClass !=null && this.dataFetcherMethod !=null) {
		this.dataFetcherObject = new this.dataFetcherClass(this.getForm().getController());
	}
	if(!this.dataFetcherObject)
		return;
		
	var callback = new AjxCallback(this, this.changeChoicesCallback);
	this.dataFetcherMethod.call(this.dataFetcherObject, {value:value, event:event, callback:callback});
	var val = "";
	if(this.getParentItem()._namePart) {
		val = this.getParentItem()._namePart;
	}
	val +="@";
	val +=value;
	this.getForm().itemChanged(this, val, event);	
}

/**
* XFormItem class: "emailaddr (composite item)
* this item is used in the Admin UI to display email address fields like alias and account name
* @class EmailAddr_XFormItem
* @constructor EmailAddr_XFormItem
* @author Greg Solovyev
**/
EmailAddr_XFormItem = function() {}
XFormItemFactory.createItemType("_EMAILADDR_", "emailaddr", EmailAddr_XFormItem, Composite_XFormItem);
EmailAddr_XFormItem.domainChoices = new XFormChoices([], XFormChoices.OBJECT_LIST, "name", "name");
EmailAddr_XFormItem.choicesDirty = false ;
EmailAddr_XFormItem.prototype.numCols = 4;
EmailAddr_XFormItem.prototype.nowrap = true;
EmailAddr_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
EmailAddr_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
EmailAddr_XFormItem.prototype.initializeItems = 
function () {
	this._inputWidth = this.getInheritedProperty("inputWidth");
	if (this._inputWidth == null) this._inputWidth = 200;
	this.items[0].width = this._inputWidth;

	Composite_XFormItem.prototype.initializeItems.call(this);
	try {
		
		this._domainPart = ZaSettings.myDomainName;
		
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
    this._oldDomainPart = this._domainPart ; //initialization time, old domain and current domain are the same ;		
};

EmailAddr_XFormItem.prototype.items = [
	{type:_TEXTFIELD_,forceUpdate:true, ref:".", labelLocation:_NONE_,cssClass:"admin_xform_name_input",
	 visibilityChecks:[],
	 enableDisableChecks:[],
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
            this.getParentItem()._namePart = namePart;
            var val = namePart + "@";
			if(this.getParentItem()._domainPart)
				val += this.getParentItem()._domainPart;
            
            this.getForm().itemChanged(this.getParentItem(), val, event);
			//if(window.console && window.console.log) console.log("EmailAddr_XFormItem setting value to "+val);
		}
	},
	{type:_OUTPUT_, value:"@"},
	{type:_DYNSELECT_DOMAIN_PART_, ref:".", labelLocation:_NONE_,  
	 	choices:EmailAddr_XFormItem.domainChoices,
	 	editable:true,
        bmolsnr:true,
        toolTipContent:ZaMsg.tt_StartTypingDomainName,
         //visibilityChecks:[EmailAddr_XFormItem.isNonSaticDomain],
	 	visibilityChecks:[],
	 	enableDisableChecks:[],
	 	dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains,
		dataFetcherClass:ZaSearch,
	 	errorLocation:_PARENT_,
		getDisplayValue:function (itemVal){
			var val = null;
			if(itemVal) {
				var emailChunks = itemVal.split("@");
			
				if(emailChunks.length > 1 ) {
					val = emailChunks[1];
				} else
					val = itemVal;
			}
			if(!val) {
				if(!this.getParentItem()._domainPart) {
					if(this.getChoices() && this.getChoices()._choiceObject && this.getChoices()._choiceObject[0]) {
						val = this.getChoices()._choiceObject[0].name;
						this.getParentItem()._domainPart = val;
					}
				} else {
					//val = this.getParentItem()._domainPart;
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
            this.getParentItem()._oldDomainPart = oldDomainPart ;
            //bug: 14250, change the instance value here also even if the whole email address is invalid
			//this.getParentItem().setInstanceValue (val) ;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}	
	}
];

EmailAddr_XFormItem.prototype.resetEditedState = function () {
	this.items[2].edited = false;
}
//reset the domainchoices for the domain list menu, bug 12495
EmailAddr_XFormItem.resetDomainLists =
function (force) {
	if (force || EmailAddr_XFormItem.choicesDirty) {
		DBG.println(AjxDebug.DBG3, "Reset the domain lists ....") ;
		ZaApp.getInstance().searchDomains();
		 EmailAddr_XFormItem.choicesDirty = false ;
	}
}

EmailAddr_XFormItem.prototype.getOldDomainPart = function () {
    return this._oldDomainPart ;
}

EmailAddr_XFormItem.prototype.getCurrentDomainPart = function () {
    return this._domainPart ;
}

/*bug 49662. reset the email-address error status when the _namePart from non-null to null
 *when add alias name. We don't think this is a  error*/
EmailAddr_XFormItem.prototype.clearNameNullError = function () {
    if(this._namePart == ""){
	if(this.hasError()){
	    this.clearError();
            this.setInstanceValue("");
            var form = this.getForm();
            var event = new DwtXFormsEvent(form, this, "");
            form.notifyListeners(DwtEvent.XFORMS_VALUE_CHANGED, event);
	    form.setIsDirty(true, this);
	    return true;
	}
    }
    
    return false;
}
