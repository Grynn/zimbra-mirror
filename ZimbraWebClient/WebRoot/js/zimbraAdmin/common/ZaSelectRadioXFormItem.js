/**
*	_ZASELECT_RADIO_ form item type
**/
ZaSelectRadio_XFormItem = function () {}
XFormItemFactory.createItemType("_ZASELECT_RADIO_", "zaselect_radio", ZaSelectRadio_XFormItem, Composite_XFormItem);
ZaSelectRadio_XFormItem.prototype.numCols=2;
ZaSelectRadio_XFormItem.prototype.colSizes=["275px","275px"];
ZaSelectRadio_XFormItem.prototype.nowrap = false;
ZaSelectRadio_XFormItem.prototype.labelWrap = true;
ZaSelectRadio_XFormItem.prototype.items = [];
ZaSelectRadio_XFormItem.prototype.labelWidth = "275px";

ZaSelectRadio_XFormItem.prototype.initializeItems = function() {
	var selectRef = this.getInheritedProperty("selectRef");
	var radioBoxLabel1 = this.getInheritedProperty("radioBoxLabel1");
	var radioBoxLabel2 = this.getInheritedProperty("radioBoxLabel2");
	var choices = this.getInheritedProperty("choices");	

	var radioBox1 = {type:_RADIO_, groupname:this.id+"zimlet_select_check_grp"+selectRef,ref:".",
		label:radioBoxLabel1, labelLocation:_RIGHT_,
		elementChanged:function(elementValue,instanceValue, event) {
			if(elementValue==true) {
				this.getForm().itemChanged(this.getParentItem(), null, event);	
			}
		},
		updateElement:function(value) {
			this.getElement().checked = !value;
		}
		
	};
	
	var radioBox2 = {type:_RADIO_, groupname:this.id+"zimlet_select_check_grp"+selectRef,ref:".",
		label:radioBoxLabel2, labelLocation:_RIGHT_ ,
		updateElement:function(value) {
			this.getElement().checked = value;
		},
		elementChanged:function(elementValue,instanceValue, event) {

		}
	};
		
	var selectChck = {
		type:_OSELECT_CHECK_,
		choices:choices,
		colSpan:3,
		ref:selectRef,
		width:"275px",
		onChange:function (value, event, form) {
			if (this.getParentItem() && this.getParentItem().getParentItem() && this.getParentItem().getParentItem().getOnChangeMethod()) {
				return this.getParentItem().getParentItem().getOnChangeMethod().call(this, value, event, form);
			} else {
				return this.setInstanceValue(value);
			}
		},
		forceUpdate:true,
		updateElement:function(value) {
			OSelect_XFormItem.prototype.updateElement.call(this, value);
		},
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
		
	this.items = [radioBox1,radioBox2,{type:_CELLSPACER_,width:this.labelWidth},selectChckGrp];
	
	
	Composite_XFormItem.prototype.initializeItems.call(this);
}
