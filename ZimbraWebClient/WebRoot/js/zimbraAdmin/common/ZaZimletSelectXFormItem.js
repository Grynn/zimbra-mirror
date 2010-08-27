/**
*	_ZA_ZIMLET_SELECT_ form item type
**/
ZaZimletSelect_XFormItem = function() {}
XFormItemFactory.createItemType("_ZA_ZIMLET_SELECT_", "za_zimlet_select", ZaZimletSelect_XFormItem, OSelect_Check_XFormItem)
ZaZimletSelect_XFormItem.prototype.initializeItems = function() {
    ZaZimletSelect_XFormItem.prototype.choicePrefixes=[
	{value:"!",label:ZaMsg.mandatory},
	{value:"-",label:ZaMsg.disabled},
	{value:"+",label:ZaMsg.enabled}
    ];
    OSelect_Check_XFormItem.prototype.initializeItems.call(this);     
}

ZaZimletSelect_XFormItem.prototype.onSubChoiceClick = function (itemNum, event, prefix) {
	event = event || window.event;
	this.subChoiceSelected(itemNum, event, prefix);
};

ZaZimletSelect_XFormItem.prototype.onSubChoiceDoubleClick = function (itemNum, event, prefix) {
	this.subChoiceSelected(itemNum, true, event, prefix);
}

ZaZimletSelect_XFormItem.prototype.subChoiceSelected = function (itemNum, event, prefix) {
	var value = this.getNormalizedValues()[itemNum];
	this.setPrefix(value, event, prefix);
}


ZaZimletSelect_XFormItem.prototype.getPrefixIndex = function (prefixValue) {
	if(!this._prefixIndexes) {
		this._prefixIndexes = {};
		var prefixes = this.getInheritedProperty("choicePrefixes");
		var cnt=prefixes.length;
		for(var i=0; i<cnt; i++) {
			this._prefixIndexes[prefixes[i].value] = i;
		} 
	}
	return this._prefixIndexes[prefixValue];	
}

ZaZimletSelect_XFormItem.prototype.getNormalizedInstanceValue = function () {
	var value = this.getInstanceValue();
	var normalizedValue = new Array();
	//make sure the returned value is a copy of the actual value
	if(typeof value == "string") {
		normalizedValue = new String(value);
	} else if(typeof value =="object" || value instanceof Array) {
		for(var a in value) {
			normalizedValue[a] = value[a];
		}
	}
	//make sure the returned value is an array
	if(normalizedValue) {
		if (typeof normalizedValue == "string") {
			if (normalizedValue == "") 	
				normalizedValue = [];
			else
				normalizedValue = normalizedValue.split(",");
		}
	} 	
	
	//remove prefixes	
	var prefixes = this.getInheritedProperty("choicePrefixes");
	var numPrefixes = prefixes.length;
	var numValues = normalizedValue.length;
	for(var i=0;i<numValues;i++) {
		var hasPrefix=false;
		for(var j=0; j<numPrefixes;j++) {
			if(normalizedValue[i].substr(0,1)==prefixes[j].value) {
				normalizedValue[i] = {value:normalizedValue[i].substr(1),prefix:normalizedValue[i].substr(0,1)};
				hasPrefix=true;
				break;
			}
		}
		if(!hasPrefix) {
			normalizedValue[i] = {value:normalizedValue[i],prefix:""};
		}
	}
	return normalizedValue;	
}

ZaZimletSelect_XFormItem.prototype.setPrefix = function (newValue, event, prefix) {
	var normalizedValues = this.getNormalizedInstanceValue();
	var newValues = [];		
	var found = false;
	for (var i = 0; i < normalizedValues.length; i++) {
		if (normalizedValues[i].value == newValue) {
			normalizedValues[i].prefix = prefix;		
		}
		newValues.push([normalizedValues[i].prefix,normalizedValues[i].value].join(""));
	}
			
	if(!newValues || (newValues.length == 1 && newValues[0] == "")) {
		newValues = [];
	} 
	
	
	// if we have a modelItem which is a LIST type
	//	convert the output to the propert outputType
	var modelItem = this.getModelItem();
	if (modelItem && modelItem.getOutputType) {
		if (modelItem.getOutputType() == _STRING_) {
			newValues = newValues.join(modelItem.getItemDelimiter());
		}
	} else {
		// otherwise assume we should convert it to a comma-separated string
		newValues = newValues.join(",");
	}

	this.getForm().itemChanged(this, newValues, event);
}

ZaZimletSelect_XFormItem.prototype.setValue = function (newValue, clearOldValues, includeIntermediates, event) {
	var normalizedValues = this.getNormalizedInstanceValue();
	var newValues = [];		
	var found = false;
	var i;
	if(newValue instanceof Array || typeof newValue == "object") {
		for(a in newValue) {
			if(typeof newValue[a] == "string" && newValue[a].substr(0,1) != "+" && newValue[a].substr(0,1) != "-") {
				var prefix = "+";
				for (i = 0; i < normalizedValues.length; i++) {
					if (normalizedValues[i].value == newValue[a] && normalizedValues[i].prefix) {
						prefix = normalizedValues[i].prefix;
						normalizedValues.splice(i,1);
						break;					
					}
				}
				newValue[a] = prefix+newValue[a];
			}
			newValues.push(newValue[a]);
		}
	} else {
		for (i = 0; i < normalizedValues.length; i++) {
			if (normalizedValues[i].value == newValue) {
				found = true;
				break;					
			}
		}		
	
		if (found) {
			normalizedValues.splice(i, 1);
		} else {
			normalizedValues.push({value:newValue,prefix:"+"});
		}
	
		for (i = 0; i < normalizedValues.length; i++) {
			newValues.push(normalizedValues[i].prefix+normalizedValues[i].value);
		}
	}
		
	if(!newValues || (newValues.length == 1 && newValues[0] == "")) {
		newValues = []
	} 
	// if we have a modelItem which is a LIST type
	//	convert the output to the propert outputType
	var modelItem = this.getModelItem();
	if (modelItem && modelItem.getOutputType) {
		if (modelItem.getOutputType() == _STRING_) {
			newValues = newValues.join(modelItem.getItemDelimiter());
		}
	} else {
		// otherwise assume we should convert it to a comma-separated string
		newValues = newValues.join(",");
	}

	this.getForm().itemChanged(this, newValues, event);
}

ZaZimletSelect_XFormItem.prototype.getChoiceHTML = function (itemNum, value, label, cssClass) {
	var prefixes = this.getInheritedProperty("choicePrefixes");
	
	var ref = this.getFormGlobalRef() + ".getItemById('"+ this.getId()+ "')";
	var id = this.getId();
	var retVal = ["<tr><td class=", cssClass, 
			" onclick=\"",ref, ".onChoiceClick(", itemNum,", event||window.event)\"",
			" ondblclick=\"",ref, ".onChoiceDoubleClick(", itemNum,", event||window.event)\" id='",id,"_choice_",itemNum,"'>",
				"<table cellspacing=0 cellpadding=0><tr><td><input type=checkbox id='",id,"_choiceitem_",itemNum,"'></td><td>",
				label,
				"</td></tr></table></td>"];
	for(var i=0; i<prefixes.length; i++) {
		retVal.push("<td class=",cssClass,
				" onclick=\"",ref, ".onSubChoiceClick(", itemNum, ", event||window.event, '",prefixes[i].value,"')\"",
				" ondblclick=\"",ref, ".onSubChoiceDoubleClick(", itemNum, ".event||window.event, '",prefixes[i].value,"')\" id='",id,"_choice_",itemNum,"_prefix_",i,"'>",
					"<table cellspacing=0 cellpadding=0><tr><td><input type=radio autocomplete='off' id='",id,"_radiochoiceitem_",itemNum,"_prefix_",i,"' name='zazimletselect_",id,"_radiogrp_",itemNum,"'></td><td>",
				prefixes[i].label,
				"</td></tr></table></td>");
	}
	retVal.push("</tr>");
	return retVal.join("");
}

ZaZimletSelect_XFormItem.prototype.hiliteChoice = function (itemNum) {
	var chEl = this.getChoiceElements(itemNum);
	if(chEl) {
		var el = chEl[0];
		el.className = this.getChoiceSelectedCssClass();
	
		var checks = el.getElementsByTagName("input");
		if (checks) {
			checks[0].checked = true;
			this.enableSubChoice(itemNum);
		}
	}
}

ZaZimletSelect_XFormItem.prototype.dehiliteChoice = function(itemNum) {
	var chEl = this.getChoiceElements(itemNum);
	if(chEl) {
		var el = chEl[0];
		el.className = this.getChoiceCssClass();

		var checks = el.getElementsByTagName("input");
		if (checks) {
			checks[0].checked = false;
			this.disableSubChoice(itemNum);
		}
	}
}

ZaZimletSelect_XFormItem.prototype.hiliteSubChoice = function (itemNum, prefixIndex) {
	var id = [this.getId(),"_choice_",itemNum,"_prefix_",prefixIndex].join("");
	var chEl = this.getElement(id);
	if(chEl) {
		chEl.className = this.getChoiceSelectedCssClass();

		var checks = chEl.getElementsByTagName("input");
		if (checks) {
			checks[0].checked = true;
		}
	}
}

ZaZimletSelect_XFormItem.prototype.dehiliteSubChoice = function(itemNum, prefixIndex) {
	var id = [this.getId(),"_choice_",itemNum,"_prefix_",prefixIndex].join("");
	var chEl = this.getElement(id);
	if(chEl) {
		chEl.className = this.getChoiceCssClass();

		var checks = chEl.getElementsByTagName("input");
		if (checks) {
			checks[0].checked = false;
		}
	}
}

ZaZimletSelect_XFormItem.prototype.disableSubChoice = function (itemNum) {
	var prefixes = this.getInheritedProperty("choicePrefixes");
	for(var i=0; i<prefixes.length; i++) {
		var id = [this.getId(),"_choice_",itemNum,"_prefix_",i].join("");
		var chEl = this.getElement(id);
		if(chEl) {
			chEl.className = this.getChoiceCssClass() + "_disabled";

			var radios = chEl.getElementsByTagName("input");
			if (radios) {
				radios[0].disabled = true;
			}
		}
	}	
}

ZaZimletSelect_XFormItem.prototype.enableSubChoice = function (itemNum) {
	var prefixes = this.getInheritedProperty("choicePrefixes");
	for(var i=0; i<prefixes.length; i++) {
		var id = [this.getId(),"_choice_",itemNum,"_prefix_",i].join("");
		var chEl = this.getElement(id);
		if(chEl) {
			chEl.className = this.getChoiceCssClass();

			var radios = chEl.getElementsByTagName("input");
			if (radios) {
				radios[0].disabled = false;
			}
		}
	}
}


ZaZimletSelect_XFormItem.prototype.updateElement = function () {
	var element = this.getElement();
	
	element.innerHTML = this.getChoicesHTML();
	var normalizedValues = this.getNormalizedInstanceValue();
	this.clearAllHilites();
	if (normalizedValues) {	
		for (var i = 0; i < normalizedValues.length; i++) {
			var itemNum = this.getChoiceNum(normalizedValues[i].value);
			if (itemNum != -1) { 
				this.hiliteChoice(itemNum);
				if(normalizedValues[i].prefix != "") {
					this.hiliteSubChoice(itemNum,this.getPrefixIndex(normalizedValues[i].prefix));
				}
			} 
		}
	}
    this.updateEnabledDisabled();
}


ZaZimletSelect_XFormItem.prototype.setElementEnabled = function (enabled) {
	var choices = this.getNormalizedChoices();
	if(!choices)
		return;
	
	var values = choices.values;
	if(!values)
		return;
		
	var cnt = values.length;
	var prefixes = this.getInheritedProperty("choicePrefixes");
	var numPrefixes = prefixes.length;
			
	for(var i=0; i < cnt; i ++) {
		var chkbx = this.getElement([this.getId(),"_choiceitem_",i].join(""));
		var chkBoxTD = this.getElement([this.getId(),"_choice_",i].join(""));
		if(chkbx) {
			if(enabled) {
				chkBoxTD.className = this.getChoiceCssClass();
				chkbx.className = this.getChoiceCssClass();
				chkbx.disabled = false;
			} else {
				chkBoxTD.className = this.getChoiceCssClass() + "_disabled";
				chkbx.className = this.getChoiceCssClass() + "_disabled";
				chkbx.disabled = true;
				for(var j=0; j<numPrefixes; j++) {
					var id = [this.getId(),"_radiochoiceitem_",i,"_prefix_",j].join("");
					var TDid = [this.getId(),"_choice_",i,"_prefix_",j].join("");
					var radio = this.getElement(id);
					var tdEl = this.getElement(TDid);
					tdEl.className = this.getChoiceCssClass() + "_disabled";
					chkbx.className = this.getChoiceCssClass() + "_disabled";
					chkbx.disabled = true;					
				}				
			}
		} 
	}
};


/**
*	_ZA_ZIMLET_SELECT_COMBO_ form item type
**/
ZaZimletSelectCombo_XFormItem = function () {}
XFormItemFactory.createItemType("_ZA_ZIMLET_SELECT_COMBO_", "za_zimlet_select_combo", ZaZimletSelectCombo_XFormItem, Composite_XFormItem);
ZaZimletSelectCombo_XFormItem.prototype.numCols=1;
ZaZimletSelectCombo_XFormItem.prototype.colSizes=["*"];
ZaZimletSelectCombo_XFormItem.prototype.nowrap = false;
ZaZimletSelectCombo_XFormItem.prototype.labelWrap = true;
ZaZimletSelectCombo_XFormItem.prototype.items = [];

ZaZimletSelectCombo_XFormItem.prototype.initializeItems = function() {
	var selectRef = this.getInheritedProperty("selectRef");
	var choices = this.getInheritedProperty("choices");	
	var selectLabel = this.getInheritedProperty("selectLabel");
	var selectLabelLocation = this.getInheritedProperty("selectLabelLocation");
    var choicesWidth = this.getInheritedProperty ("choicesWidth") || "500px" ;
    
    var selectChck = {
		type:_ZA_ZIMLET_SELECT_,
		choices:choices,
		colSpan:4,
		ref:selectRef,
		label:selectLabel,
		labelLocation:selectLabelLocation,
		width:choicesWidth,
		bmolsnr:true,
		cssStyle:"margin-bottom:5px;margin-top:5px;border:2px inset gray;"				
	};
	
	var selectChckGrp = {
		type:_GROUP_,
		numCols:4,
		colSizes:["130px","5px","130px","*"],
		items:[
			selectChck,
			{type:_DWT_BUTTON_,label:ZaMsg.SelectAll,width:"120px",
				onActivate:function (ev) {
					var lstElement = this.getParentItem().items[0];
					if(lstElement) {
						lstElement.selectAll(ev);
					}
				}
			},
			{type:_CELLSPACER_,width:"5px"},
			{type:_DWT_BUTTON_,label:ZaMsg.DeselectAll,width:"120px",
				onActivate:function (ev) {
					var lstElement = this.getParentItem().items[0];
					if(lstElement) {
						lstElement.deselectAll(ev);
					}
				}
			},
			{type:_CELLSPACER_,width:"235px"},
		]
		
	}
		
	this.items = [selectChckGrp];
	
	
	Composite_XFormItem.prototype.initializeItems.call(this);
}
