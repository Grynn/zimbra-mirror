/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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
* XFormItem class: "dynselect_domain_part"
* A select box with asynchronous autocomplete capability
* @class DynSelectDomainPart_XFormItem
* @constructor DynSelectDomainPart_XFormItem
* @author Greg Solovyev
**/
DynSelectDomainPart_XFormItem = function() {}
XFormItemFactory.createItemType("_DYNSELECT_DOMAIN_PART_", "dynselect_domain_part", DynSelectDomainPart_XFormItem, DynSelect_XFormItem);
DynSelectDomainPart_XFormItem.prototype.handleKeyPressDelay = function (event,value) {
	if(!this.dataFetcherObject && this.dataFetcherClass !=null && this.dataFetcherMethod !=null) {
		this.dataFetcherObject = new this.dataFetcherClass(this.getForm().getController());
	}
	if(!this.dataFetcherObject)
		return;
		
	var callback = new AjxCallback(this, this.changeChoicesCallback);
	this.dataFetcherMethod.call(this.dataFetcherObject, value, event, callback);
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
			if (AjxEnv.hasFirebug) console.log("EmailAddr_XFormItem setting value to "+val);
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
	{type:_DYNSELECT_DOMAIN_PART_, ref:".", labelLocation:_NONE_, relevantBehavior:_HIDE_, 
	 	choices:EmailAddr_XFormItem.domainChoices,editable:true,
	 	relevant:"ZaSettings.DOMAINS_ENABLED",dataFetcherMethod:ZaSearch.prototype.dynSelectSearchDomains,
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
			//bug: 14250, change the instance value here also even if the whole email address is invalid
			this.getParentItem().setInstanceValue (val) ;
			this.getForm().itemChanged(this.getParentItem(), val, event);
		}	/*,
		keyUp:function(newValue,ev) {
			if(!(ev.keyCode==XFG.ARROW_LEFT || ev.keyCode==XFG.ARROW_RIGHT)) {
				//DBG.println(AjxDebug.DBG1, "EmailAddr_XFormItem.keyUp handled key code "+ ev.keyCode +" char code " + (new Date()).getTime());
				var n = "";
				if(newValue)
					n = String(newValue).replace(/([\\\\\\*\\(\\)])/g, "\\$1");
					
				var query = "(zimbraDomainName="+n+"*)";
				var app = this.getForm().parent._app ; 
				app._domainQuery = query ;
				//initialize the searchDomains action
				
				if (this.keyPressDelayHdlr != null) {
					AjxTimedAction.cancelAction(this.keyPressDelayHdlr);					
				}
				
				this._acInterval = DynSelect_XFormItem.LOAD_PAUSE;
				this._acActionId = -1;
				this._acAction = new AjxTimedAction(app, app.scheduledSearchDomains, this);
				
				this.keyPressDelayHdlr = AjxTimedAction.scheduleAction (this._acAction, this._acInterval);
				
				//this.getForm().getController().searchDomains(query);
				EmailAddr_XFormItem.choicesDirty = true ;
			}
		}*/
	}
];

//reset the domainchoices for the domain list menu, bug 12495
EmailAddr_XFormItem.resetDomainLists =
function (force) {
	if (force || EmailAddr_XFormItem.choicesDirty) {
		DBG.println(AjxDebug.DBG3, "Reset the domain lists ....") ;
		this._app.searchDomains();
		 EmailAddr_XFormItem.choicesDirty = false ;
	}
}
