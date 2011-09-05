/**
 * Created by IntelliJ IDEA.
 * User: mingzhang
 * Date: 5/30/11
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
Signature_XFormItem = function() {}
XFormItemFactory.createItemType("_SIGNATURE_", "signature", Signature_XFormItem, Composite_XFormItem);
Signature_XFormItem.prototype.numCols = 1;
Signature_XFormItem.prototype.nowrap = true;

Signature_XFormItem.A_zimbraSigatureName = "name";
Signature_XFormItem.A_zimbraSigatureContent = "content";
Signature_XFormItem.A_zimbraSigatureType = "type";

Signature_XFormItem.prototype.visibilityChecks = [ZaItem.hasReadPermission];
Signature_XFormItem.prototype.enableDisableChecks = [ZaItem.hasWritePermission];
Signature_XFormItem.prototype.initializeItems = function () {
    Composite_XFormItem.prototype.initializeItems.call(this);
}

Signature_XFormItem.typeChoice = [
    {value:"", label: ZaMsg.VALUE_NOT_SET},
    {value:"text/plain", label:ZaMsg.resSignaturePlainType},
    {value:"text/html", label:ZaMsg.resSignatureHTMLType}
];

Signature_XFormItem.prototype.items = [
	{type:_TEXTFIELD_, ref:".", width:"300px", labelLocation:_LEFT_, forceUpdate:true,label:ZaMsg.Dlg_SignatureName,
		//enableDisableChecks:false,
		labelCssStyle:"width:194px",
		getDisplayValue:function(itemVal) {

			if(itemVal && itemVal["name"])
				val = itemVal["name"];
			else {
				val = "";
			}
            return val;
		},

        elementChanged:function(nameVal, curVal, event) {
            var newVal = {};
            if(curVal == "" || !curVal)
                      curVal = {};
            newVal = ZaUtil.deepCloneObject(curVal);
			newVal["name"] = nameVal;

            this.getForm().itemChanged(this.getParentItem(), newVal, event);
        }

	},
	{type:_TEXTAREA_, width: "300px",  forceUpdate:true, ref:".", labelLocation:_LEFT_, label:ZaMsg.Dlg_SignatureContent,
		 labelCssStyle:"width:194px",
		getDisplayValue:function (itemVal) {
			var val;
			if(itemVal && itemVal["content"])
				val = itemVal["content"];
			else val = "";
			return val;
		},
		elementChanged:function(contentVal, curVal, event) {
            var newVal = {};
            if(curVal == "" || !curVal)
                    curVal = {};

            newVal = ZaUtil.deepCloneObject(curVal);
			newVal["content"] = contentVal;
			this.getForm().itemChanged(this.getParentItem(), newVal, event);
		}
	}/*,  Comment it but not delete for it might be used in future
    {ref:".", type:_OSELECT1_, label:ZaMsg.Dlg_SignatureType,
        msgName:ZaMsg.Dlg_SignatureType, labelLocation:_LEFT_,labelCssStyle:"width:194px",
        choices:Signature_XFormItem.typeChoice,
        getDisplayValue:function (itemVal) {
            var val;
            if(itemVal && itemVal["type"])
                val = itemVal["type"];
            else val = "";
            return val;
        },
        elementChanged:function(typeVal, curVal, event) {
            var newVal = {};
            if(curVal == "" || !curVal)
                    curVal = {};

            newVal = ZaUtil.deepCloneObject(curVal);
            newVal["type"] = typeVal;
            this.getForm().itemChanged(this.getParentItem(), newVal, event);
        }
    }*/
];
