
/**
*	_ZA_CHECKBOX_LIST_ form item type
**/
ZaCheckBox_List_XFormItem = function () {}
XFormItemFactory.createItemType("_ZA_CHECKBOX_LIST_", "za_checkbox_list", ZaCheckBox_List_XFormItem, Composite_XFormItem);
ZaCheckBox_List_XFormItem.prototype.numCols=2;
ZaCheckBox_List_XFormItem.prototype.colSizes=["275px","275px"];
ZaCheckBox_List_XFormItem.prototype.nowrap = false;
ZaCheckBox_List_XFormItem.prototype.labelWrap = true;
ZaCheckBox_List_XFormItem.prototype.items = [];
ZaCheckBox_List_XFormItem.prototype.labelWidth = "275px";
ZaCheckBox_List_XFormItem.prototype.choicesWidth = "275px";

ZaCheckBox_List_XFormItem.prototype.initializeItems = function() {
	var selectRef = this.getInheritedProperty("selectRef");
	var choices = this.getInheritedProperty("choices");	
	var selectLabel = this.getInheritedProperty("selectLabel");
    var choicesWidth = AjxEnv.isIE? "275px": (this.getInheritedProperty ("choicesWidth") || "275px") ;
    
    var selectChck = {
		type:_OSELECT_CHECK_,
		choices:choices,
		colSpan:3,
		ref:selectRef,
		label:selectLabel,
		labelLocation:_TOP_,
		width:choicesWidth,
		bmolsnr:true,
		cssStyle:"margin-bottom:5px;margin-top:5px;border:2px inset gray;"				
	};
	
	var selectChckGrp = {
		type:_GROUP_,
		numCols:3,
		colSizes:["130px","15px","130px"],
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
			{type:_CELLSPACER_,width:"15px"},
			{type:_DWT_BUTTON_,label:ZaMsg.DeselectAll,width:"120px",
				onActivate:function (ev) {
					var lstElement = this.getParentItem().items[0];
					if(lstElement) {
						lstElement.deselectAll(ev);
					}
				}
			}
		]
		
	}
		
	this.items = [{type:_CELLSPACER_,width:this.labelWidth},selectChckGrp];
	
	
	Composite_XFormItem.prototype.initializeItems.call(this);
}
