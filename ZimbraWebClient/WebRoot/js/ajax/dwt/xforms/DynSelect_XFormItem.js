/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * XFormItem class: "dynselect"
 * @constructor DynSelect_XFormItem
 * @class DynSelect_XFormItem
 * A select box with asynchronous autocomplete capability
 * 
 * 
 * @author Greg Solovyev
 *
 * @private
 *
 */
DynSelect_XFormItem = function() {}
XFormItemFactory.createItemType("_DYNSELECT_", "dynselect", DynSelect_XFormItem, OSelect1_XFormItem);
DynSelect_XFormItem.prototype.dataFetcherClass = null;
DynSelect_XFormItem.prototype.dataFetcherMethod = null;
DynSelect_XFormItem.prototype.dataFetcherObject = null;
DynSelect_XFormItem.prototype.dataFetcherTypes = null;
DynSelect_XFormItem.prototype.dataFetcherAttrs = null;
DynSelect_XFormItem.prototype.dataFetcherDomain = null;
DynSelect_XFormItem.prototype.bmolsnr = true;
DynSelect_XFormItem.prototype.emptyText = "";
DynSelect_XFormItem.prototype.cssClass = "dynselect";
DynSelect_XFormItem.prototype.edited = false;
DynSelect_XFormItem.LOAD_PAUSE = AjxEnv.isIE ? 500 : 250;	// delay between chunks
DynSelect_XFormItem.prototype.initFormItem = function () {
	// if we're dealing with an XFormChoices object...
	var choices  = this.getChoices();
	if(choices instanceof Array) {
		choices =  new XFormChoices(choices,XFormChoices.SIMPLE_LIST);
	}
	if(!choices)
		choices = new XFormChoices([], XFormChoices.OBJECT_LIST, "name", "name");
		
	this.setChoices(choices); 
//	this.choices = new XFormChoices([], XFormChoices.OBJECT_LIST, "name", "name");
	//	...set up to receive notification when its choices change
	var listener = new AjxListener(this, this.choicesChangeLsnr);
	this.choices.addListener(DwtEvent.XFORMS_CHOICES_CHANGED, listener);
	this.dataFetcherClass = this.getInheritedProperty("dataFetcherClass");
	this.dataFetcherMethod = this.getInheritedProperty("dataFetcherMethod");
	this.dataFetcherTypes = this.getInheritedProperty("dataFetcherTypes");	
	this.dataFetcherAttrs = this.getInheritedProperty("dataFetcherAttrs");
	this.dataFetcherDomain = this.getInheritedProperty("dataFetcherDomain");
	this.dataFetcherObject = null;
	if(!this.dataFetcherMethod) {
		this.dataFetcherMethod = DynSelect_XFormItem.fetchDataDefault;
		this.dataFetcherObject = this;
	}
}
DynSelect_XFormItem.prototype.changeChoicesCallback = 
function (data, more, total) {
	var choices;
	choices = this.choices ? this.choices : this.getChoices();
	
	if(!choices)
		return;
	choices.setChoices(data);
	choices.dirtyChoices();

		
		
	if(AjxUtil.isEmpty(data)) {
		this.hideMenu();
	} else {
		if(!this.menuUp)
			this.showMenu();	
	}
}

DynSelect_XFormItem.fetchDataDefault = function (callArgs) {
	var callback = callArgs["callback"];
	callback.run(this.choices.getChoiceObject(), false, null);
}

DynSelect_XFormItem.prototype.onKeyUp = function(value, event) {
	var lastTypeTime = new Date().getTime();
	this._lastTypeTime = lastTypeTime;
	if (window.console && window.console.log) window.console.log("onKeyUp " + value + " @ "+lastTypeTime);
	this.edited = true;
	this.hideNote();
	if(event.keyCode==XFG.ARROW_UP) {
		if(!this.menuUp)
			this.showMenu();
		
		this.hilitePreviousChoice(event);
		this.isSelecting = true;
		return;
	} 
	
	if(event.keyCode==XFG.ARROW_DOWN) {
		if(!this.menuUp)
			this.showMenu();
			
		this.hiliteNextChoice(event);
		this.isSelecting = true;
		return;
	} 
	
		

	if(this.isSelecting && this.menuUp && event.keyCode==DwtKeyEvent.KEY_ENTER && this.__currentHiliteItem != null && this.__currentHiliteItem != undefined) {
		var value = this.getNormalizedValues()[this.__currentHiliteItem];
		if(value != null && value != undefined) {
			this.setValue(value, true, event);
			this.hideMenu();
			return;
		}
	} else if (this.menuUp && event.keyCode==DwtKeyEvent.KEY_ENTER) {
		this.hideMenu();
		return;
	}
	this.isSelecting = false;	
	
	var method = this.getKeyUpMethod();
	if(method) {
		method.call(this, value, event);
	} else {

		var key = DwtKeyEvent.getCharCode(event);
		// don't fire off another if we've already set one up unless this is an ENTER key
		if (!AjxUtil.isEmpty(this.keyPressDelayHdlr)) {
			AjxTimedAction.cancelAction(this.keyPressDelayHdlr);
			this.keyPressDelayHdlr = null;
		}
		
		var form = this.getForm();
		var evt = new DwtKeyEvent();
		evt.setFromDhtmlEvent(event);
	
		if (key == DwtKeyEvent.KEY_TAB) {
			DwtUiEvent.setBehaviour(event, true, false);
			return false;
		} else if(!(event.keyCode==XFG.ARROW_RIGHT || event.keyCode==XFG.ARROW_LEFT || event.keyCode == DwtKeyEvent.KEY_ESCAPE)) {  			
			var action = new AjxTimedAction(this, this.handleKeyPressDelay, [evt, value,lastTypeTime]);
			this.keyPressDelayHdlr = AjxTimedAction.scheduleAction(action, DynSelect_XFormItem.LOAD_PAUSE);
		}		
	}

}

DynSelect_XFormItem.prototype.resetChoices = function () {
	var choices = this.getChoices();
	choices.setChoices([]);
	choices.dirtyChoices();
	
	if(!this.dataFetcherObject && this.dataFetcherClass !=null && this.dataFetcherMethod !=null) {
			this.dataFetcherObject = new this.dataFetcherClass(this.getForm().getController());
	} else if(this.getInheritedProperty("dataFetcherInstance")) {
		this.dataFetcherObject = this.getInstance();
	}	
}


DynSelect_XFormItem.prototype.handleKeyPressDelay = function (event,value,lastTypeTime) {
	var currTime = new Date().getTime();
	if (window.console && window.console.log) window.console.log("handleKeyPressDelay " + value + " @ " + currTime);
	this.keyPressDelayHdlr = null;
	var val = this.preProcessInput(value);
	
	if(lastTypeTime == this._lastTypeTime) {
		this.getForm().itemChanged(this, val, event);
	} else {
		if (window.console && window.console.log) window.console.log("typing faster than retreiving data");
		return;
	}		
	if(!this.dataFetcherObject && this.dataFetcherClass !=null && this.dataFetcherMethod !=null) {
		this.dataFetcherObject = new this.dataFetcherClass(this.getForm().getController());
	} else if(this.getInheritedProperty("dataFetcherInstance")) {
		this.dataFetcherObject = this.getInstance();
	}	
	
	if(!this.dataFetcherObject)
		return;
			
	var callback = new AjxCallback(this, this.changeChoicesCallback);
	var searchByProcessedValue = this.getInheritedProperty("searchByProcessedValue");
	var callArgs = {event:event, callback:callback, extraLdapQuery:null, form:this.getForm(), types:(this.dataFetcherTypes ? this.dataFetcherTypes : null),
		attrs:(this.dataFetcherAttrs ? this.dataFetcherAttrs : null), domain:(this.dataFetcherDomain ? this.dataFetcherDomain : null)};
	if(searchByProcessedValue && !AjxUtil.isEmpty(val))	{
		callArgs["value"] = val;
		this.dataFetcherMethod.call(this.dataFetcherObject, callArgs);
	} else if (!AjxUtil.isEmpty(value)){
		callArgs["value"] = value;	
		this.dataFetcherMethod.call(this.dataFetcherObject, callArgs);
	}
}

DynSelect_XFormItem.prototype.outputHTML = function (HTMLoutput) {
	var id = this.getId();
	var ref = this.getFormGlobalRef() + ".getItemById('"+ id + "')";	
	var inputHtml;
	var inputSize = this.getInheritedProperty("inputSize");
	var inputWidth = this.getInheritedProperty("inputWidth");
	var inputWidthString = inputWidth ? "style='width:" + inputWidth + "'" : (inputSize ? "size="+inputSize : "")
	inputHtml = ["<input type=text id=", id, "_display class=", this.getDisplayCssClass(), " value='VALUE' ", 
				" onchange=\"",ref, ".onValueTyped(this.value, event||window.event)\"",
				" onkeyup=\"",ref, ".onKeyUp(this.value, event||window.event)\"", inputWidthString,
				this.getMouseoutHandlerHTML(),
				">"].join("");

	HTMLoutput.append(
		"<div id=", id, this.getCssString(),
			" onclick=\"", this.getFormGlobalRef(), ".getItemById('",this.getId(),"').onClick(event)\"",
			" onselectstart=\"return false\"",
			">",
			"<table ", this.getTableCssString(), ">", 
				"<tr><td width=100%>",inputHtml,"</td>",
				"</tr>", 
			"</table>", 
		"</div>"
	);
 	this.edited = false;
}

DynSelect_XFormItem.prototype.getMouseoutHandlerHTML =
function () {
	var formId = this.getFormGlobalRef(), 
		itemId = this.getId();
	
	var onMouseoutAction = "";
	
	var onMouseoutFunc = this.getInheritedProperty("onMouseout") ;
	onMouseoutAction = AjxBuffer.concat(" onmouseout=\"", onMouseoutFunc || "XFormItem.prototype.hideInputTooltip" , 
						".call(" ,   this.getGlobalRef(), ", event );\" ");
						
	return AjxBuffer.concat( onMouseoutAction );	
}

DynSelect_XFormItem.prototype.onClick = function(event) {
	var choices = this.getNormalizedChoices();
	if(!this.edited && this.getInheritedProperty("editable")) {
		this.showInputTooltip(event);
	} else {
		if(choices && choices.values && choices.values.length) {
			this.showMenu();
		}
	}
	if(AjxUtil.isEmpty(this.getInstanceValue()) && this._enabled) {
		var el = this.getDisplayElement();
		el.value = "";
		el.className = this.getDisplayCssClass();
	}

}

DynSelect_XFormItem.prototype.getArrowElement = function () {
	return null;
}

DynSelect_XFormItem.prototype.preProcessInput = function (value) {
	var preProcessMethod = this.getPreProcessMethod();
	var val = value;
	if(preProcessMethod)
		val = preProcessMethod.call(this, value, this.getForm());
		
	return val;
}

DynSelect_XFormItem.prototype.getPreProcessMethod = function () {
	return this.cacheInheritedMethod("inputPreProcessor","inputPreProcessor","value, form");
}

DynSelect_XFormItem.prototype.updateElement = function (newValue) {
	if (this.getMultiple() && newValue != null && newValue.indexOf(",") > -1) {
		newValue = newValue.split(",");
		for (var i = 0; i < newValue.length; i++) {
			newValue[i] = this.getChoiceLabel(newValue[i]);
		}
	} else {
		newValue = this.getChoiceLabel(newValue);
	}
	
	var el = this.getDisplayElement();

	if (el) {
		if(AjxUtil.isEmpty(newValue) && this._enabled && !this.edited) {
			var emptyText = this.getInheritedProperty("emptyText");
			if(!AjxUtil.isEmpty(emptyText)) {
				newValue = emptyText;
				el.className = this.getDisplayCssClass() + "_empty";
			}		
		} else if(this._enabled) {
			el.className = this.getDisplayCssClass();
		}
		if(window.console && window.console.log) console.log("updating element with value: " + newValue + " over " + el.value);
		el.value = newValue;
	}
	
	if(AjxUtil.isEmpty(newValue)) {
		if(!this.dataFetcherObject && this.dataFetcherClass !=null && this.dataFetcherMethod !=null) {
			this.dataFetcherObject = new this.dataFetcherClass(this.getForm().getController());
		}
		if(!this.dataFetcherObject)
			return;
	}	
}

DynSelect_XFormItem.prototype.setElementEnabled = function(enabled) {
	this._enabled = enabled;
	var el = this.getForm().getElement(this.getId());
	if (!el || !el.getElementsByTagName || !el.getElementsByTagName("table")[0])
		return;
        var table = el.getElementsByTagName("table")[0];
	if(enabled) {
		if(AjxUtil.isEmpty(this.getInstanceValue()) && !AjxUtil.isEmpty(this.getInheritedProperty("emptyText"))) {
			this.getDisplayElement().className = this.getDisplayCssClass() + "_empty";
			this.getDisplayElement().value = this.getInheritedProperty("emptyText");
		} else {
			this.getDisplayElement().className = this.getDisplayCssClass();
		}
		
		this.getForm().getElement(this.getId()).className = this.cssClass;
		table.className = this.getTableCssClass();
		this.getDisplayElement().disabled=false;
		
	} else {
		this.getDisplayElement().className = this.getDisplayCssClass() + "_disabled";
		var el = this.getArrowElement();
		if(el)
			AjxImg.setImage(el, "SelectPullDownArrowDis");
			
		this.getForm().getElement(this.getId()).className = this.cssClass + "_disabled";
		table.className = this.getTableCssClass()+"_disabled";
		this.getDisplayElement().disabled=true;
	}
}
