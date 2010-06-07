/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2006, 2007, 2008, 2009 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
function ZaConvertD() {

}
if(ZaGlobalConfig) {
	ZaGlobalConfig.A_zimbraComponentAvailable_convertd = "_"+ZaGlobalConfig.A_zimbraComponentAvailable+"_convertd";
	ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly = "zimbraAttachmentsViewInHtmlOnly";
	if(ZaGlobalConfig.myXModel) {
		ZaGlobalConfig.myXModel.items.push(	
		{ id:ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly, ref:"attrs/" + ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES}
		);
	}	
}
if(ZaCos) {
	ZaCos.A_zimbraAttachmentsViewInHtmlOnly = "zimbraAttachmentsViewInHtmlOnly";
	ZaCos.A_zimbraAttachmentsIndexingEnabled = "zimbraAttachmentsIndexingEnabled";	
	ZaCos.A_zimbraFeatureViewInHtmlEnabled = "zimbraFeatureViewInHtmlEnabled";	
	if(ZaCos.myXModel) {
		ZaCos.myXModel.items.push(
			{id:ZaCos.A_zimbraAttachmentsViewInHtmlOnly, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraAttachmentsViewInHtmlOnly}
		);
		ZaCos.myXModel.items.push(
			{id:ZaCos.A_zimbraFeatureViewInHtmlEnabled, type:_ENUM_, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraFeatureViewInHtmlEnabled}
		);		
		ZaCos.myXModel.items.push(
			{id:ZaCos.A_zimbraAttachmentsIndexingEnabled, choices:ZaModel.BOOLEAN_CHOICES, ref:"attrs/"+ZaCos.A_zimbraAttachmentsIndexingEnabled, type:_ENUM_}
		);		
	}
}

if(ZaAccount) {
	ZaAccount.A_zimbraAttachmentsViewInHtmlOnly = "zimbraAttachmentsViewInHtmlOnly";
	ZaAccount.A_zimbraAttachmentsIndexingEnabled = "zimbraAttachmentsIndexingEnabled";	
	ZaAccount.A_zimbraFeatureViewInHtmlEnabled = "zimbraFeatureViewInHtmlEnabled";
	if(ZaAccount.myXModel) {
		ZaAccount.myXModel.items.push(
			{id:ZaAccount.A_zimbraFeatureViewInHtmlEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraFeatureViewInHtmlEnabled, choices:ZaModel.BOOLEAN_CHOICES}
		);			
		ZaAccount.myXModel.items.push(
			{id:ZaAccount.A_zimbraAttachmentsViewInHtmlOnly, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraAttachmentsViewInHtmlOnly, choices:ZaModel.BOOLEAN_CHOICES}
		);
		ZaAccount.myXModel.items.push(
			{id:ZaAccount.A_zimbraAttachmentsIndexingEnabled, type:_COS_ENUM_, ref:"attrs/"+ZaAccount.A_zimbraAttachmentsIndexingEnabled, choices:ZaModel.BOOLEAN_CHOICES}
		);		
	}	
}

if(ZaTabView.XFormModifiers["GlobalConfigXFormView"]) {
	GlobalConfigXFormView.ATTACHMENTS_TAB_ATTRS.push(ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly);

	ZaConvertD.GlobalConfigXFormModifier = function (xFormObject) {
		var cnt = xFormObject.items.length;
		var i = 0;
		for(i = 0; i <cnt; i++) {
			if(xFormObject.items[i].type=="switch") 
				break;
		}
		cnt = xFormObject.items[i].items.length;
		var j = 0;		
		for(j = 0; j <cnt; j++) {
			if(xFormObject.items[i].items[j].id=="gs_form_attachment_tab") {
				var tmpItems = xFormObject.items[i].items[j].items;
				var cnt2 = tmpItems.length;
				for(var k=0; k<cnt2; k++) {
					if(tmpItems[k].id=="attachment_settings") {
						if(!tmpItems[k].visibilityChecks)
							tmpItems[k].visibilityChecks = [XFormItem.prototype.hasReadPermission];
							
						tmpItems[k].visibilityChecks.push(ZaConvertD.isConvertDUnAvailable);
						var xfObj1 = {ref:ZaGlobalConfig.A_zimbraAttachmentsBlocked, type:_RADIO_, groupname:"attachment_settings",
										msgName:ZaMsg.NAD_GlobalRemoveAllAttachments,label:ZaMsg.NAD_GlobalRemoveAllAttachments, labelLocation:_RIGHT_, 
										onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", align:_LEFT_,
										valueChangeEventSources:[ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly,ZaGlobalConfig.A_zimbraAttachmentsBlocked],
										visibilityChecks:[ZaConvertD.isConvertDAvailable],
										updateElement:function () {
											this.getElement().checked = (ZaConvertD.getGlobalAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_BLOCKED);
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("TRUE",ZaGlobalConfig.A_zimbraAttachmentsBlocked);
											this.setInstanceValue("FALSE",ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly);
											this.getForm().parent.setDirty(true);

										}
									};
						var xfObj2 = {ref:ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly, type:_RADIO_, groupname:"attachment_settings", 
										msgName:ZaMsg.NAD_GlobalAttachmentsViewInHtmlOnly,label:ZaMsg.NAD_GlobalAttachmentsViewInHtmlOnly, labelLocation:_RIGHT_, 
										onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", align:_LEFT_,
										visibilityChecks:[ZaConvertD.isConvertDAvailable],									
										valueChangeEventSources:[ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly,ZaGlobalConfig.A_zimbraAttachmentsBlocked],
										updateElement:function () {
											this.getElement().checked = (ZaConvertD.getGlobalAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_HTML_ONLY);
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaGlobalConfig.A_zimbraAttachmentsBlocked);
											this.setInstanceValue("TRUE",ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly);	
											this.getForm().parent.setDirty(true);																					

										}
									};		
						var xfObj3 = {ref:ZaGlobalConfig.A_zimbraAttachmentsBlocked, type:_RADIO_, groupname:"attachment_settings", 
										msgName:ZaMsg.NAD_GlobalAttachmentsViewCOS,label:ZaMsg.NAD_GlobalAttachmentsViewCOS, labelLocation:_RIGHT_, 
										onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", align:_LEFT_,
										visibilityChecks:[ZaConvertD.isConvertDAvailable],									
										valueChangeEventSources:[ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly,ZaGlobalConfig.A_zimbraAttachmentsBlocked],
										updateElement:function () {
											this.getElement().checked = (ZaConvertD.getGlobalAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_COS);
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaGlobalConfig.A_zimbraAttachmentsBlocked);
											this.setInstanceValue("FALSE",ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly);
											this.getForm().parent.setDirty(true);
										}
									};	
						var xfObj4 = { ref: ZaGlobalConfig.A_zimbraMtaBlockedExtensionWarnRecipient, type: _CHECKBOX_,
				  						label: ZaMsg.LBL_zimbraMtaBlockedExtensionWarnRecipient,
				  						trueValue:"TRUE", falseValue:"FALSE"
									};
						var attGroup = {type: _GROUP_,  id:"convertd_attachment_settings", width: "98%", 
										numCols: 2, colSpan:2, colSizes:[250, "*"], 
										items: [
											{ type: _GROUP_, label: ZaMsg.NAD_Attachment_Settings, labelCssStyle: "vertical-align:top",
										  		items: [ xfObj1,xfObj2,xfObj3,xfObj4],
										  		visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaGlobalConfig.A_zimbraAttachmentsBlocked],ZaConvertD.isConvertDAvailable],
										  		bmolsnr:true
											}
										]};
																
						tmpItems.splice(k,0,attGroup);
						break;
					}
					
				}
				break;
			
			}
		}		
	 }
	 ZaTabView.XFormModifiers["GlobalConfigXFormView"].push(ZaConvertD.GlobalConfigXFormModifier);
}
ZaConvertD.ATTACHMENTS_BLOCKED = 1;
ZaConvertD.ATTACHMENTS_HTML_ONLY = 2;
ZaConvertD.ATTACHMENTS_ORIGINAL_ONLY = 3;
ZaConvertD.ATTACHMENTS_HTML_AND_ORIGINGAL = 4;
ZaConvertD.ATTACHMENTS_COS = 4;
ZaConvertD.getAttachmentOptionVal = function() {
	var htmlOnly = this.getInstanceValue(ZaCos.A_zimbraAttachmentsViewInHtmlOnly);
	var blockAttach = this.getInstanceValue(ZaCos.A_zimbraAttachmentsBlocked);
	var htmlFeature = this.getInstanceValue(ZaCos.A_zimbraFeatureViewInHtmlEnabled);		
	if(blockAttach=="TRUE") {
		return ZaConvertD.ATTACHMENTS_BLOCKED;
	} else if(blockAttach=="FALSE" && htmlOnly=="TRUE") {
		return ZaConvertD.ATTACHMENTS_HTML_ONLY;
	} else if(blockAttach=="FALSE" && htmlOnly=="FALSE" && htmlFeature=="TRUE") {
		return ZaConvertD.ATTACHMENTS_HTML_AND_ORIGINGAL;
	} else if(blockAttach=="FALSE" && htmlOnly=="FALSE" && htmlFeature=="FALSE") {
		return ZaConvertD.ATTACHMENTS_ORIGINAL_ONLY;
	}
}

ZaConvertD.getGlobalAttachmentOptionVal = function() {
	var htmlOnly = this.getInstanceValue(ZaGlobalConfig.A_zimbraAttachmentsViewInHtmlOnly);
	var blockAttach = this.getInstanceValue(ZaGlobalConfig.A_zimbraAttachmentsBlocked);

	if(blockAttach=="TRUE") {
		return ZaConvertD.ATTACHMENTS_BLOCKED;
	} else if(blockAttach=="FALSE" && htmlOnly=="TRUE") {
		return ZaConvertD.ATTACHMENTS_HTML_ONLY;
	} else if(blockAttach=="FALSE" && htmlOnly=="FALSE") {
		return ZaConvertD.ATTACHMENTS_COS;
	} 
}

ZaConvertD.checkIfAttachmentOptionsOverwritten = function() {
	var rad1 = (this.getForm().getItemsById("attach_radio1")[0].getModelItem().getLocalValue(this.getInstance()) != null);
	var rad2 = (this.getForm().getItemsById("attach_radio2")[0].getModelItem().getLocalValue(this.getInstance()) != null);	
	var rad3 = (this.getForm().getItemsById("attach_radio3")[0].getModelItem().getLocalValue(this.getInstance()) != null);		
	var rad4 = (this.getForm().getItemsById("attach_radio4")[0].getModelItem().getLocalValue(this.getInstance()) != null);			
	return (rad1 || rad2 || rad3 || rad4);
}
ZaConvertD.resetAttachOptionsToCOS = function(ev) {
	this.setInstanceValue(null, ZaAccount.A_zimbraAttachmentsViewInHtmlOnly);
	this.setInstanceValue(null,ZaAccount.A_zimbraAttachmentsBlocked);
	this.setInstanceValue(null,ZaAccount.A_zimbraFeatureViewInHtmlEnabled);	
	//this.getForm().itemChanged(this, null, ev);
}

ZaConvertD.isConvertDAvailable = function () {
	return true; //since dlegated admins are likely o not have access to globalconfig, we will stop checking zimbraComponentAvailable for now
	//return ZaGlobalConfig.getInstance().attrs[ZaGlobalConfig.A_zimbraComponentAvailable_convertd];
}
		
ZaConvertD.isConvertDUnAvailable = function () { 
	return !ZaConvertD.isConvertDAvailable();
}

if(ZaTabView.XFormModifiers["ZaCosXFormView"]) {
	ZaCosXFormView.ADVANCED_TAB_ATTRS.push(ZaCos.A_zimbraAttachmentsViewInHtmlOnly);
	ZaCosXFormView.ADVANCED_TAB_ATTRS.push(ZaCos.A_zimbraFeatureViewInHtmlEnabled);
	ZaCosXFormView.ADVANCED_TAB_ATTRS.push(ZaCos.A_zimbraAttachmentsIndexingEnabled);	
	ZaConvertD.CosXFormModifier = function (xFormObject) {
		var cnt = xFormObject.items.length;
		var switchObj = null;
		for(i = 0; i <cnt; i++) {
			if(xFormObject.items[i].type=="switch") {
				switchObj = xFormObject.items[i];
				break;
			}
		}
		if(switchObj && switchObj.items) {
			var cnt = switchObj.items.length;
			for(i = 0; i <cnt; i++) {
				if(switchObj.items[i].id=="cos_form_advanced_tab") {
					var tmpItems = switchObj.items[i].items;
					var cnt2 = tmpItems.length;
					for(var j=0;j<cnt2;j++) {
						if(tmpItems[j].id == "cos_attachment_settings") {
							var attachmentItems = tmpItems[j].items;
							var cnt3 = attachmentItems.length;
							for(var k=0;k<cnt3;k++) {
								if(attachmentItems[k].ref==ZaCos.A_zimbraAttachmentsBlocked) {
									if(!attachmentItems[k].visibilityChecks)	
										attachmentItems[k].visibilityChecks = [XFormItem.prototype.hasReadPermission];
																	
									attachmentItems[k].visibilityChecks.push(ZaConvertD.isConvertDUnAvailable);
									var xfObj1 = {ref:ZaCos.A_zimbraAttachmentsBlocked, type:_RADIO_, groupname:"cos_attachment_settings", 
												msgName:ZaMsg.NAD_RemoveAllAttachments,label:ZaMsg.NAD_RemoveAllAttachments, 
												onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", 
												valueChangeEventSources:[ZaCos.A_zimbraAttachmentsViewInHtmlOnly,ZaCos.A_zimbraFeatureViewInHtmlEnabled,ZaCos.A_zimbraAttachmentsBlocked],
												visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
												updateElement:function () {
													this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_BLOCKED);
												},
												elementChanged: function(elementValue,instanceValue, event) {
													this.setInstanceValue("TRUE",ZaCos.A_zimbraAttachmentsBlocked);
													this.setInstanceValue("FALSE",ZaCos.A_zimbraFeatureViewInHtmlEnabled);											
													this.setInstanceValue("FALSE",ZaCos.A_zimbraAttachmentsViewInHtmlOnly);	
													this.getForm().parent.setDirty(true);												
												}
											};
									var xfObj2 = {ref:ZaCos.A_zimbraAttachmentsViewInHtmlOnly, type:_RADIO_, groupname:"cos_attachment_settings", 
												msgName:com_zimbra_convertd.NAD_AttachmentsViewInHtmlOnly,label:com_zimbra_convertd.NAD_AttachmentsViewInHtmlOnly, 
												onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", 
												valueChangeEventSources:[ZaCos.A_zimbraAttachmentsViewInHtmlOnly,ZaCos.A_zimbraFeatureViewInHtmlEnabled,ZaCos.A_zimbraAttachmentsBlocked],
												visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
												updateElement:function () {
													this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_HTML_ONLY);
												},
												elementChanged: function(elementValue,instanceValue, event) {
													this.setInstanceValue("FALSE",ZaCos.A_zimbraAttachmentsBlocked);
													this.setInstanceValue("TRUE",ZaCos.A_zimbraFeatureViewInHtmlEnabled);											
													this.setInstanceValue("TRUE",ZaCos.A_zimbraAttachmentsViewInHtmlOnly);																						
	
												}
											};		
									var xfObj3 = {ref:ZaCos.A_zimbraFeatureViewInHtmlEnabled, type:_RADIO_, groupname:"cos_attachment_settings", 
												msgName:com_zimbra_convertd.NAD_AttachmentsViewOrigOnly,label:com_zimbra_convertd.NAD_AttachmentsViewOrigOnly, 
												onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", 
												valueChangeEventSources:[ZaCos.A_zimbraAttachmentsViewInHtmlOnly,ZaCos.A_zimbraFeatureViewInHtmlEnabled,ZaCos.A_zimbraAttachmentsBlocked],
												visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
												updateElement:function () {
													this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_ORIGINAL_ONLY);
												},
												elementChanged: function(elementValue,instanceValue, event) {
													this.setInstanceValue("FALSE",ZaCos.A_zimbraAttachmentsBlocked);
													this.setInstanceValue("FALSE",ZaCos.A_zimbraFeatureViewInHtmlEnabled);											
													this.setInstanceValue("FALSE",ZaCos.A_zimbraAttachmentsViewInHtmlOnly);	
													this.getForm().parent.setDirty(true);																					
												}
											};	
									var xfObj4 = {ref:ZaCos.A_zimbraFeatureViewInHtmlEnabled, type:_RADIO_, groupname:"cos_attachment_settings", 
												msgName:com_zimbra_convertd.NAD_AttachmentsViewOrigAndHtml,label:com_zimbra_convertd.NAD_AttachmentsViewOrigAndHtml, 
												onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", 
												visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
												valueChangeEventSources:[ZaCos.A_zimbraAttachmentsBlocked,ZaCos.A_zimbraFeatureViewInHtmlEnabled,ZaCos.A_zimbraAttachmentsViewInHtmlOnly],
												updateElement:function () {
													this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_HTML_AND_ORIGINGAL);
												},
												elementChanged: function(elementValue,instanceValue, event) {
													this.setInstanceValue("FALSE",ZaCos.A_zimbraAttachmentsBlocked);
													this.setInstanceValue("TRUE",ZaCos.A_zimbraFeatureViewInHtmlEnabled);											
													this.setInstanceValue("FALSE",ZaCos.A_zimbraAttachmentsViewInHtmlOnly);
													this.getForm().parent.setDirty(true);																						
												}
											};					
									var xfObj5 = {ref:ZaCos.A_zimbraAttachmentsIndexingEnabled, type:_CHECKBOX_, 
											msgName:com_zimbra_convertd.NAD_zimbraAttachmentsIndexingEnabled,label:com_zimbra_convertd.NAD_zimbraAttachmentsIndexingEnabled,
											trueValue:"TRUE", falseValue:"FALSE", 
											onChange:ZaTabView.onFormFieldChanged,
											visibilityChecks:[XFormItem.prototype.hasReadPermission,ZaConvertD.isConvertDAvailable]
											};																												
	
									attachmentItems.splice(k,0,xfObj1,xfObj2,xfObj3,xfObj4,xfObj5);
									
									break;
								}
							}
							break;
						}
					}
					break;
				}
			}
		}
	 }
	 ZaTabView.XFormModifiers["ZaCosXFormView"].push(ZaConvertD.CosXFormModifier);
}

if(ZaTabView.XFormModifiers["ZaAccountXFormView"]) {
	ZaAccountXFormView.ADVANCED_TAB_ATTRS.push(ZaAccount.A_zimbraAttachmentsViewInHtmlOnly);
	ZaAccountXFormView.ADVANCED_TAB_ATTRS.push(ZaAccount.A_zimbraFeatureViewInHtmlEnabled);
	ZaAccountXFormView.ADVANCED_TAB_ATTRS.push(ZaAccount.A_zimbraAttachmentsIndexingEnabled);
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
				var tmpItems = xFormObject.items[i].items[j].items;
				var cnt2 = tmpItems.length;
				for(var k=0; k<cnt2; k++) {
					if(tmpItems[k].id=="account_attachment_settings" && tmpItems[k].items) {
						tmpItems[k].visibilityChecks = [[ZATopGrouper_XFormItem.isGroupVisible,
                        			[ZaAccount.A_zimbraAttachmentsBlocked,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,
                        			ZaAccount.A_zimbraFeatureViewInHtmlEnabled,ZaAccount.A_zimbraAttachmentsIndexingEnabled]]];
						tmpItems[k].numCols = 3;
						tmpItems[k].colSizes = ["275px","275px","*"];						
						var cnt3 = tmpItems[k].items.length;
						for(var l = 0; l< cnt3; l++) {
							if(tmpItems[k].items[l].ref==ZaAccount.A_zimbraAttachmentsBlocked) {
								if(!tmpItems[k].items[l].visibilityChecks)	
									tmpItems[k].items[l].visibilityChecks = [XFormItem.prototype.hasReadPermission];								
								
								tmpItems[k].items[l].visibilityChecks.push(ZaConvertD.isConvertDUnAvailable);
								var xfObj1 = {ref:ZaAccount.A_zimbraAttachmentsBlocked,id:"attach_radio1", type:_RADIO_,
										nowrap:false,labelWrap:true,width:"275px",
										msgName:ZaMsg.NAD_RemoveAllAttachments,label:ZaMsg.NAD_RemoveAllAttachments, labelLocation:_RIGHT_, 
										onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", 
										visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
										valueChangeEventSources:[ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,ZaAccount.A_zimbraFeatureViewInHtmlEnabled,ZaAccount.A_zimbraAttachmentsBlocked],
										updateElement:function () {
											Super_XFormItem.updateCss.call(this,1);
											this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_BLOCKED);
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsViewInHtmlOnly);	
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraFeatureViewInHtmlEnabled);											
											this.setInstanceValue("TRUE",ZaAccount.A_zimbraAttachmentsBlocked);
											this.getForm().parent.setDirty(true);

										}
									};
								var xfObj2 = {ref:ZaAccount.A_zimbraAttachmentsViewInHtmlOnly, id:"attach_radio2", type:_RADIO_, 
										nowrap:false,labelWrap:true,	width:"275px",									
										msgName:com_zimbra_convertd.NAD_AttachmentsViewInHtmlOnly,label:com_zimbra_convertd.NAD_AttachmentsViewInHtmlOnly, labelLocation:_RIGHT_, 
										onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", 
										visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
										valueChangeEventSources:[ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,ZaAccount.A_zimbraFeatureViewInHtmlEnabled,ZaAccount.A_zimbraAttachmentsBlocked],
										updateElement:function () {
											Super_XFormItem.updateCss.call(this,1);											
											this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_HTML_ONLY);
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsBlocked);
											this.setInstanceValue("TRUE",ZaAccount.A_zimbraFeatureViewInHtmlEnabled);											
											this.setInstanceValue("TRUE",ZaAccount.A_zimbraAttachmentsViewInHtmlOnly);
											this.getForm().parent.setDirty(true);																						

										}
									};		
								var xfObj3 = {ref:ZaAccount.A_zimbraFeatureViewInHtmlEnabled, id:"attach_radio3", type:_RADIO_, 
										nowrap:false,labelWrap:true,	width:"275px",									
										msgName:com_zimbra_convertd.NAD_AttachmentsViewOrigOnly,label:com_zimbra_convertd.NAD_AttachmentsViewOrigOnly, labelLocation:_RIGHT_, 
										onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", 
										visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
										valueChangeEventSources:[ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,ZaAccount.A_zimbraFeatureViewInHtmlEnabled,ZaAccount.A_zimbraAttachmentsBlocked],
										updateElement:function () {
											Super_XFormItem.updateCss.call(this,1);
											this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_ORIGINAL_ONLY);
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsBlocked);
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsViewInHtmlOnly);
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraFeatureViewInHtmlEnabled);
											this.getForm().parent.setDirty(true);	
										}
									};	
								var xfObj4 = {ref:ZaAccount.A_zimbraFeatureViewInHtmlEnabled, type:_RADIO_, id:"attach_radio4", 
										nowrap:false,labelWrap:true,	width:"275px",									
										msgName:com_zimbra_convertd.NAD_AttachmentsViewOrigAndHtml,label:com_zimbra_convertd.NAD_AttachmentsViewOrigAndHtml, labelLocation:_RIGHT_, 
										onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", 
										visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
										valueChangeEventSources:[ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,ZaAccount.A_zimbraFeatureViewInHtmlEnabled,ZaAccount.A_zimbraAttachmentsBlocked],
										updateElement:function () {
											Super_XFormItem.updateCss.call(this,1);
											this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_HTML_AND_ORIGINGAL);
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsBlocked);
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsViewInHtmlOnly);																						
											this.setInstanceValue("TRUE",ZaAccount.A_zimbraFeatureViewInHtmlEnabled);
											this.getForm().parent.setDirty(true);
										}
									};					
								var xfObj5 = {ref:ZaAccount.A_zimbraAttachmentsIndexingEnabled, type:_SUPER_CHECKBOX_, 
										nowrap:false,labelWrap:true,										
										msgName:com_zimbra_convertd.NAD_zimbraAttachmentsIndexingEnabled,
										checkBoxLabel:com_zimbra_convertd.NAD_zimbraAttachmentsIndexingEnabled,label:null,
										labelLocation:_NONE_,checkBoxLabelLocation:_RIGHT_, resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
										trueValue:"TRUE", falseValue:"FALSE", 
										onChange:ZaTabView.onFormFieldChanged,labelCssClass:"xform_label_right", 
										visibilityChecks:[XFormItem.prototype.hasReadPermission,ZaConvertD.isConvertDAvailable]
									};																												
								var sprAnchor = {	
											type:_DWT_BUTTON_, ref:".", label:ZaMsg.NAD_ResetToCOS,
											visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.checkIfAttachmentOptionsOverwritten],
											visibilityChangeEventSources:[ZaAccount.A_zimbraFeatureViewInHtmlEnabled,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,ZaAccount.A_zimbraAttachmentsBlocked],
											onActivate:ZaConvertD.resetAttachOptionsToCOS,
											onChange:ZaTabView.onFormFieldChanged,
											cssStyle:"width:150px",rowSpan:4
										};	
								tmpItems[k].items.splice(l,0,xfObj1,sprAnchor,xfObj2,xfObj3,xfObj4,xfObj5);								
								break;								
							}
						}
						

						break;
					}
				}
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
		var gotAdvanced = false;
		var gotFeatures = false;		
		for(j = 0; j <cnt; j++) {
			if(xFormObject.items[i].items[j].id=="account_form_advanced_step") {
				var tmpItems = xFormObject.items[i].items[j].items;
				var cnt2 = tmpItems.length;
				for(var k=0; k<cnt2; k++) {
					if(tmpItems[k].id=="account_attachment_settings" && tmpItems[k].items) {
						tmpItems[k].numCols = 3;
						tmpItems[k].colSizes = ["*","360px","*"];
						var cnt3 = tmpItems[k].items.length;
						for(var l = 0; l < cnt3; l++) {
							if(tmpItems[k].items[l].ref == ZaAccount.A_zimbraAttachmentsBlocked) {
								if(!tmpItems[k].items[l].visibilityChecks)
									tmpItems[k].items[l].visibilityChecks = [XFormItem.prototype.hasReadPermission];
								tmpItems[k].items[l].visibilityChecks.push(ZaConvertD.isConvertDUnAvailable);
								var xfObj1 = {ref:ZaAccount.A_zimbraAttachmentsBlocked,id:"attach_radio1", type:_RADIO_, 
										msgName:ZaMsg.NAD_RemoveAllAttachments,label:ZaMsg.NAD_RemoveAllAttachments, labelLocation:_RIGHT_, 
										labelCssClass:"xform_label_right",
										valueChangeEventSources:[ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,ZaAccount.A_zimbraFeatureViewInHtmlEnabled,ZaAccount.A_zimbraAttachmentsBlocked],
										visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
										updateElement:function () {
											Super_XFormItem.updateCss.call(this,1);
											this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_BLOCKED);
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("TRUE",ZaAccount.A_zimbraAttachmentsBlocked);
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraFeatureViewInHtmlEnabled);											
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsViewInHtmlOnly);	
											this.getForm().parent.setDirty(true);										
										}
									};
								var xfObj2 = {ref:ZaAccount.A_zimbraAttachmentsViewInHtmlOnly, id:"attach_radio2", type:_RADIO_, 
										msgName:com_zimbra_convertd.NAD_AttachmentsViewInHtmlOnly,label:com_zimbra_convertd.NAD_AttachmentsViewInHtmlOnly, labelLocation:_RIGHT_, 
										labelCssClass:"xform_label_right",
										valueChangeEventSources:[ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,ZaAccount.A_zimbraFeatureViewInHtmlEnabled,ZaAccount.A_zimbraAttachmentsBlocked],
										visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
										updateElement:function () {
											Super_XFormItem.updateCss.call(this,1);
											this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_HTML_ONLY);
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsBlocked);
											this.setInstanceValue("TRUE",ZaAccount.A_zimbraFeatureViewInHtmlEnabled);											
											this.setInstanceValue("TRUE",ZaAccount.A_zimbraAttachmentsViewInHtmlOnly);	
											this.getForm().parent.setDirty(true);																					
										}
									};		
								var xfObj3 = {ref:ZaAccount.A_zimbraFeatureViewInHtmlEnabled, id:"attach_radio3", type:_RADIO_, 
										msgName:com_zimbra_convertd.NAD_AttachmentsViewOrigOnly,label:com_zimbra_convertd.NAD_AttachmentsViewOrigOnly, labelLocation:_RIGHT_, 
										labelCssClass:"xform_label_right",
										valueChangeEventSources:[ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,ZaAccount.A_zimbraFeatureViewInHtmlEnabled,ZaAccount.A_zimbraAttachmentsBlocked],
										visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
										updateElement:function () {
											Super_XFormItem.updateCss.call(this,1);
											this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_ORIGINAL_ONLY);
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsBlocked);
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraFeatureViewInHtmlEnabled);											
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsViewInHtmlOnly);	
											this.getForm().parent.setDirty(true);																					
										}
									};	
								var xfObj4 = {ref:ZaAccount.A_zimbraFeatureViewInHtmlEnabled, type:_RADIO_, id:"attach_radio4", 
										msgName:com_zimbra_convertd.NAD_AttachmentsViewOrigAndHtml,label:com_zimbra_convertd.NAD_AttachmentsViewOrigAndHtml, labelLocation:_RIGHT_, 
										labelCssClass:"xform_label_right",
										valueChangeEventSources:[ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,ZaAccount.A_zimbraFeatureViewInHtmlEnabled,ZaAccount.A_zimbraAttachmentsBlocked],
										visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.isConvertDAvailable],
										updateElement:function () {
											Super_XFormItem.updateCss.call(this,1);
											this.getElement().checked = (ZaConvertD.getAttachmentOptionVal.call(this) == ZaConvertD.ATTACHMENTS_HTML_AND_ORIGINGAL);
										},
										elementChanged: function(elementValue,instanceValue, event) {
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsBlocked);
											this.setInstanceValue("TRUE",ZaAccount.A_zimbraFeatureViewInHtmlEnabled);											
											this.setInstanceValue("FALSE",ZaAccount.A_zimbraAttachmentsViewInHtmlOnly);	
											this.getForm().parent.setDirty(true);																					
										}
									};					
								var xfObj5 = {ref:ZaAccount.A_zimbraAttachmentsIndexingEnabled, type:_SUPER_CHECKBOX_, 
										msgName:com_zimbra_convertd.NAD_zimbraAttachmentsIndexingEnabled,
										checkBoxLabel:com_zimbra_convertd.NAD_zimbraAttachmentsIndexingEnabled,label:null,
										labelLocation:_NONE_,checkBoxLabelLocation:_RIGHT_,
										resetToSuperLabel:ZaMsg.NAD_ResetToCOS,
										trueValue:"TRUE", falseValue:"FALSE", 
										labelCssClass:"xform_label_right",
										visibilityChecks:[XFormItem.prototype.hasReadPermission,ZaConvertD.isConvertDAvailable]
									};																												
								var sprAnchor = {	
											type:_DWT_BUTTON_, ref:".", label:ZaMsg.NAD_ResetToCOS,
											visibilityChecks:[[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraAttachmentsBlocked],[XFormItem.prototype.hasReadPermission,ZaAccount.A_zimbraFeatureViewInHtmlEnabled],ZaConvertD.checkIfAttachmentOptionsOverwritten],
											visibilityChangeEventSources:[ZaAccount.A_zimbraFeatureViewInHtmlEnabled,ZaAccount.A_zimbraAttachmentsViewInHtmlOnly,ZaAccount.A_zimbraAttachmentsBlocked],
											onActivate:ZaConvertD.resetAttachOptionsToCOS,
											cssStyle:"width:100px",rowSpan:4
										};
								tmpItems[k].items.splice(l,0,xfObj1,sprAnchor,xfObj2,xfObj3,xfObj4,xfObj5);
								
								break;
							}
						}
						break;
		
					}
				}
				break;				
			}
		}
		
	}
	ZaXDialog.XFormModifiers["ZaNewAccountXWizard"].push(ZaConvertD.AccountXWizFormModifier);
}
