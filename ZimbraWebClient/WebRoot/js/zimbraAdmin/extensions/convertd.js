function ZaConvertD() {

}
if(ZaGlobalConfig) {
	ZaGlobalConfig.A_zimbraComponentAvailable_convertd = "_"+ZaGlobalConfig.A_zimbraComponentAvailable+"_convertd";
}

if(ZaTabView.XFormModifiers["GlobalConfigXFormView"]) {
	ZaConvertD.GlobalConfigXFormModifier = function (xFormObject) {
		var cnt = xFormObject.items.length;
		for(var i = 0; i <cnt; i++) {
			if(xFormObject.items[i].type=="switch") {
					xFormObject.items[i].items[1].items[0].items.splice(1,0,
				    { type: _GROUP_, useParentTable: true, //TODO: Move this code to an external file
						relevant: "instance.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd]", 
						relevantBehavior: _HIDE_,
						items: [
							{ ref: ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly, type: _CHECKBOX_, 
							  relevant: "instance.attrs[ZaGlobalConfig.A_zimbraAttachmentsBlocked] == 'FALSE'", 
							  relevantBehavior: _DISABLE_,
							  label: ZaMsg.NAD_Attach_ViewInHtml,
							  trueValue:"TRUE", falseValue:"FALSE", 
							  onChange: ZaTabView.onFormFieldChanged
							}
						]
				    });
			
			}
		}
	 }
	 ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(ZaConvertD.GlobalConfigXFormModifier);
}

if(ZaTabView.XFormModifiers["ZaCosXFormView"]) {
	ZaConvertD.CosXFormModifier = function (xFormObject) {
		var cnt = xFormObject.items.length;
		for(var i = 0; i <cnt; i++) {
			if(xFormObject.items[i].type=="switch") {
				xFormObject.items[i].items[4].items[0].items.splice(1,0,
				    				{ref:ZaCos.A_zimbraAttachmentsViewInHtmlOnly, type:_CHECKBOX_, //TODO: Move this code to an external file
										msgName:ZaMsg.NAD_AttachmentsViewInHtmlOnly,label:ZaMsg.NAD_AttachmentsViewInHtmlOnly, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_,
										relevant:"instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd]",
										relevantBehavior:_HIDE_
									});
				xFormObject.items[i].items[4].items[0].items.splice(1,0,
									{ref:ZaCos.A_zimbraAttachmentsIndexingEnabled, type:_CHECKBOX_, msgName:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled,label:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled, labelLocation:_LEFT_, trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label", align:_LEFT_,
										relevant:"instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd]",
										relevantBehavior:_HIDE_
									});
			
			}
		}
	 }
	 ZaTabView.XFormModifiers["ZaCosXFormView"].push(ZaConvertD.CosXFormModifier);
}

if(ZaTabView.XFormModifiers["ZaAccountXFormView"]) {
	ZaConvertD.AccountXFormModifier= function (xFormObject) {
		var cnt = xFormObject.items.length;
		var i = 0;
		for(i = 0; i <cnt; i++) {
			if(xFormObject.items[i].type=="switch") 
				break;
		}
		cnt = xFormObject.items[i].items.length;
		var j = 0;
		for(j = 0; j <cnt; j++) {
			if(xFormObject.items[i].items[j].id=="account_form_advanced_tab") {
				xFormObject.items[i].items[j].items[0].items.splice(1,0,
								{ref:ZaAccount.A_zimbraAttachmentsViewInHtmlOnly, type:_SUPER_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_AttachmentsViewInHtmlOnly,
									label:ZaMsg.NAD_AttachmentsViewInHtmlOnly, 
									labelLocation:_LEFT_, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,
									relevant:"instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd]",
									relevantBehavior:_HIDE_
								});
				xFormObject.items[i].items[j].items[0].items.splice(2,0,								
								{ref:ZaAccount.A_zimbraAttachmentsIndexingEnabled, type:_SUPER_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled,label:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled, labelLocation:_LEFT_, 
									trueValue:"TRUE", falseValue:"FALSE", onChange:ZaTabView.onFormFieldChanged,									
									relevant:"instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd]",
									relevantBehavior:_HIDE_
								});
				break;
			}
		}
		
	}
	ZaTabView.XFormModifiers["ZaAccountXFormView"].push(ZaConvertD.AccountXFormModifier);
}

if(ZaXDialog.XFormModifiers["ZaNewAccountXWizard"]) {
	ZaConvertD.AccountXWizFormModifier= function (xFormObject) {
		var cnt = xFormObject.items.length;
		var i = 0;
		for(i = 0; i <cnt; i++) {
			if(xFormObject.items[i].type=="switch") 
				break;
		}
		cnt = xFormObject.items[i].items.length;
		var j = 0;
		for(j = 0; j <cnt; j++) {
			if(xFormObject.items[i].items[j].id=="account_form_advanced_step") {
				xFormObject.items[i].items[j].items[0].items.splice(1,0,
								{ref:ZaAccount.A_zimbraAttachmentsViewInHtmlOnly, type:_SUPER_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_AttachmentsViewInHtmlOnly,
									label:ZaMsg.NAD_AttachmentsViewInHtmlOnly,labelLocation:_LEFT_, 
									labelCssStyle:"width:160px;",
									trueValue:"TRUE", falseValue:"FALSE", 
									relevant:"instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd]",
									relevantBehavior:_HIDE_
								});
				xFormObject.items[i].items[j].items[0].items.splice(2,0,								
								{ref:ZaAccount.A_zimbraAttachmentsIndexingEnabled, type:_SUPER_CHECKBOX_, 
									resetToSuperLabel:ZaMsg.NAD_ResetToCOS, msgName:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled,
									label:ZaMsg.NAD_zimbraAttachmentsIndexingEnabled, labelLocation:_LEFT_, 
									labelCssStyle:"width:160px;",
									trueValue:"TRUE", falseValue:"FALSE",
									relevant:"instance.globalConfig.attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd]",
									relevantBehavior:_HIDE_
								});
				break;
			}
		}
		
	}
	ZaXDialog.XFormModifiers["ZaNewAccountXWizard"].push(ZaConvertD.AccountXWizFormModifier);
}