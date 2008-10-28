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
* @class ZaDLXFormView
* @contructor
* @param parent
* @param app
* @author Greg Solovyev
**/
ZaDLXFormView = function(parent) {
	ZaTabView.call(this, parent, "ZaDLXFormView");
	this.dlStatusChoices = [
		{value:"enabled", label:ZaMsg.DL_Status_enabled}, 
		{value:"disabled", label:ZaMsg.DL_Status_disabled}
	];
	this.TAB_INDEX = 0;
	this.initForm(ZaDistributionList.myXModel,this.getMyXForm());
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(this, ZaDLXFormView.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(this, ZaDLXFormView.prototype.handleXFormChange));	
}

ZaDLXFormView.prototype = new ZaTabView();
ZaDLXFormView.prototype.constructor = ZaDLXFormView;
ZaTabView.XFormModifiers["ZaDLXFormView"] = new Array();

ZaDLXFormView.prototype.getTitle = 
function () {
	return ZaMsg.DL_view_title;
}

ZaDLXFormView.prototype.getTabIcon =
function () {
	return "Group" ;
}

ZaDLXFormView.prototype.handleXFormChange = function (ev) {
	if(ev && this._localXForm.hasErrors()) { 
		ZaApp.getInstance().getCurrentController()._toolbar.getButton(ZaOperation.SAVE).setEnabled(false);
	}
}

ZaDLXFormView.membersSelectionListener =    
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_membersSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_membersSelected, null);
	}
}

ZaDLXFormView.nonmemberSelectionListener =    
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_nonmembersSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_nonmembersSelected, null);
	}
}

ZaDLXFormView.memberPoolSelectionListener =    
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_memberPoolSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_memberPoolSelected, null);
	}
}

ZaDLXFormView.directMemberSelectionListener =    
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_directMemberSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_directMemberSelected, null);
	}
}

ZaDLXFormView.indirectMemberSelectionListener =    
function (ev) {
	var arr = this.widget.getSelection();
	if(arr && arr.length) {
		arr.sort();
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_indirectMemberSelected, arr);
	} else {
		this.getModel().setInstanceValue(this.getInstance(), ZaDistributionList.A2_indirectMemberSelected, null);
	}
}

/**
* method of an XFormItem
**/
ZaDLXFormView.removeAllMembers = function(event) {
	var form = this.getForm();
	form.getInstance().removeAllMembers();
	form.parent.setDirty(true);
	form.refresh();
};

/**
* method of an XFormItem
**/
ZaDLXFormView.removeMembers = function(event) {
	var form = this.getForm();
	var membersSelection = ZaDLXFormView.getMemberSelection.call(form);
	if(membersSelection.length) {
		form.getInstance().removeMembers(membersSelection);
		form.parent.setDirty(true);
		form.refresh();	
	}
};

/**
* method of an XFormItem
**/
ZaDLXFormView.srchButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	fieldObj.searchAccounts(null, true);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.fwdPoolButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	var currentPageNum = this.getInstanceValue("/poolPagenum");
	this.setInstanceValue(currentPageNum+1,"/poolPagenum");
	fieldObj.searchAccounts(null, true);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.backPoolButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	var currentPageNum = this.getInstanceValue("/poolPagenum")-1;
	this.setInstanceValue(currentPageNum,"/poolPagenum");
	fieldObj.searchAccounts(null, true);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.fwdMemButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	var currentPageNum = this.getInstanceValue("/memPagenum")+1;
	this.setInstanceValue(currentPageNum,"/memPagenum");
	this.getInstance().getMembers(ZaDistributionList.MEMBER_QUERY_LIMIT);
	this.getForm().refresh();
}

/**
* method of an XFormItem
**/
ZaDLXFormView.backMemButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	var currentPageNum = this.getInstanceValue("/memPagenum")-1;
	this.setInstanceValue(currentPageNum,"/memPagenum");
	this.getInstance().getMembers(ZaDistributionList.MEMBER_QUERY_LIMIT);
	this.getForm().refresh();
}
/**
* method of the XForm
**/
ZaDLXFormView.getMemberSelection = 
function () {
	var memberItem = this.getItemsById(ZaDistributionList.A2_members )[0];
	var membersSelection = null;
	if(memberItem) {
		var membersSelection = memberItem.getSelection();
	}	
		
	if(membersSelection) {
		return membersSelection;
	} else {
		return [];
	}	
}
/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableMemberListButtons = function() {
	return (ZaDLXFormView.getMemberSelection.call(this).length>0);
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableRemoveAllButton = function() {
	return (!AjxUtil.isEmpty(this.getInstanceValue(ZaDistributionList.A2_members)));
	/*var list = this.getForm().getItemsById(ZaDistributionList.A2_members)[0].widget.getList();
	if (list != null) {
		return ( list.size() > 0);
	}
	return false;*/
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableAddAllButton = function() {
	var list = this.getItemsById("memberPool")[0].widget.getList();
	if (list != null) {
		return ( list.size() > 0);
	}
	return false;
};

/**
* method of the XForm
**/
ZaDLXFormView.getMemberPoolSelection = 
function () {
	var memberItem = this.getItemsById("memberPool")[0];
	var membersSelection = null;
	if(memberItem) {
		var membersSelection = memberItem.getSelection();
	}	
		
	if(membersSelection) {
		return membersSelection;
	} else {
		return [];
	}	
}

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableMemberPoolListButtons = function() {
	return (ZaDLXFormView.getMemberPoolSelection.call(this).length>0);
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableFreeFormButtons = function () {
	var optionalAdd = this.getInstance().optionalAdd;
	return (optionalAdd && optionalAdd.length > 0);
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnablePoolForwardButton = function () {
	return (this.getInstanceValue(ZaDistributionList.A2_poolPagenum) < this.getInstanceValue(ZaDistributionList.A2_poolNumPages));
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnablePoolBackButton = function () {
	return (this.getInstanceValue(ZaDistributionList.A2_poolPagenum) > 1);
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableMemForwardButton = function () {
	return (this.getInstanceValue(ZaDistributionList.A2_memPagenum) < this.getInstanceValue(ZaDistributionList.A2_memNumPages));
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableMemBackButton = function () {
	return (this.getInstanceValue(ZaDistributionList.A2_memPagenum) > 1);
};

/**
* method of the XForm
**/
ZaDLXFormView.addListToMemberList = function (list) {
	if (this.getInstance().addMembers(list)) {
		this.refresh();
	}
	this.getItemsById(ZaDistributionList.A2_members )[0].widget.setSelectedItems(list);
	this.getItemsById('removeButton')[0].widget.setEnabled(true);
	this.parent.setDirty(true);
};

/**
 * method of an XFormItem
 * Currently, this manages the data, redraws the whole list, and then sets 
 * the selection. 
 * TODO - change the routine to add only the necessary rows to the list view.
 * Same is true of addAllAddresses
 */
ZaDLXFormView.addAddressesToMembers = function (event) {
 	var form = this.getForm();
	var memberPoolSelection = ZaDLXFormView.getMemberPoolSelection.call(form);
	ZaDLXFormView.addListToMemberList.call(form, memberPoolSelection);
};

/**
 * method of an XFormItem
**/
ZaDLXFormView.addAllAddressesToMembers = function (event) {
	var form = this.getForm();
	var pool = form.get("memberPool");
	ZaDLXFormView.addListToMemberList.call(form, pool);
};

/**
 * method of an XFormItem
**/
ZaDLXFormView.addFreeFormAddressToMembers = function (event) {
 	var form = this.getForm();
	// get the current value of the textfied
 	var val = form.get("optionalAdd");
 	if(!val)
 		return;
 		
 	var values = val.split(/[\r\n,;]+/);
	var cnt = values.length;
 	var members = new Array();
 	var stdEmailRegEx = /([^\<\;]*)\<([^\>]+)\>/ ;
	for (var i = 0; i < cnt; i++) {
		var tmpval = AjxStringUtil.trim(values[i],true);
		var result ;
		if (tmpval) {
			if ((result = stdEmailRegEx.exec(tmpval)) != null) {
				tmpval = result[2];
			}
			
			/*if(!AjxUtil.EMAIL_SHORT_RE.test(tmpval) ) {*/
			if(tmpval.lastIndexOf ("@")!=tmpval.indexOf ("@")) {
				//how error msg
				ZaApp.getInstance().getCurrentController().popupErrorDialog(AjxMessageFormat.format(ZaMsg.WARNING_DL_INVALID_EMAIL,[values[i]]),null,null,DwtMessageDialog.WARNING_STYLE);
				return false;
			}
			members.push(new ZaDistributionListMember(tmpval));
		}
	}
	ZaDLXFormView.addListToMemberList.call(form, members);
	form.getInstance().optionalAdd = null;
};

ZaDLXFormView.prototype.setObject = 
function (entry) {
	this._containedObject = entry.clone();
	this._containedObject.type = entry.type ;
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);	
	
	this.updateTab();
}

ZaDLXFormView.prototype.searchAccounts = 
function (orderby, isascending) {
	try {
		orderby = (orderby !=null) ? orderby : ZaAccount.A_name;
		
		var  searchQueryHolder = new ZaSearchQuery(ZaSearch.getSearchByNameQuery(this._containedObject["query"]), [ZaSearch.ACCOUNTS,ZaSearch.DLS,ZaSearch.ALIASES], false, "",null,10);
		var result = ZaSearch.searchByQueryHolder(searchQueryHolder, this._containedObject["poolPagenum"], orderby, isascending);
		if(result.list) {
			this._containedObject.memberPool = result.list.getArray();
		}
		this._containedObject.poolNumPages = result.numPages;
		this._localXForm.setInstance(this._containedObject);

	} catch (ex) {
		// Only restart on error if we are not initialized and it isn't a parse error
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
//			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDLXFormView.prototype.searchAccounts", null, (this._inited) ? false : true);
			ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaDLXFormView.prototype.searchAccounts", null, false );
		} else {
			this.popupErrorDialog(ZaMsg.queryParseError, ex);
			this._searchField.setEnabled(true);	
		}
	}
}


ZaDLXFormView.myXFormModifier = function(xFormObject) {	
	var sourceHeaderList = new Array();
	var sortable=1;
	sourceHeaderList[0] = new ZaListHeaderItem("type", ZaMsg.ALV_Type_col, null, "34px", sortable++, "objectClass", true, true);
	sourceHeaderList[1] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, "200px", sortable++, ZaAccount.A_name, true, true);
	//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	sourceHeaderList[2] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, null, sortable++,ZaAccount.A_displayname, true, true);
	//sourceHeaderList[3] = new ZaListHeaderItem(null, null, null, "10px", null, null, false, true);
	var membersHeaderList = new Array();
	membersHeaderList[0] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, "100%", sortable++, ZaAccount.A_name, true, true);

	var directMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.DIRECT);
	var indirectMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.INDIRECT);
	var nonMemberOfHeaderList = new ZaAccountMemberOfsourceHeaderList(ZaAccountMemberOfsourceHeaderList.NON);
    
    this.tabChoices = new Array();
	
	var _tab1 = ++this.TAB_INDEX;
	var _tab2 = ++this.TAB_INDEX;	
	var _tab3 = ++this.TAB_INDEX;	
	var _tab4 = ++this.TAB_INDEX;	
	
	this.tabChoices.push({value:_tab1, label:ZaMsg.DLXV_TabMembers});
	this.tabChoices.push({value:_tab2, label:ZaMsg.DLXV_TabNotes});
	this.tabChoices.push({value:_tab3, label:ZaMsg.TABT_MemberOf});
    this.tabChoices.push({value:_tab4, label:ZaMsg.TABT_Aliases});
    	
	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	xFormObject.numCols=5;
	xFormObject.colSizes = [10,"auto", 20, "auto", 10];
	xFormObject.itemDefaults = {
			_INPUT_: { cssClass:"inputBorder" },
			_TEXTAREA_: {cssClass: "inputBorder"},
			_TEXTFIELD_: {cssClass: "inputBorder", containerCssStyle:"width:100%"}
	    };
	    
	xFormObject.items = [
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", id:"xform_header", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
					items: [
						{type:_AJX_IMAGE_, src:"Group_32", label:null, rowSpan:2},
						{type:_OUTPUT_, ref:"name", label:null,cssClass:"AdminTitle", rowSpan:2},
						{type:_OUTPUT_, ref:"id", label:ZaMsg.NAD_ZimbraID},
						{type:_OUTPUT_, ref:"zimbraMailStatus", label:ZaMsg.NAD_AccountStatus,
								choices: this.dlStatusChoices
						}
					]
				}
			],
			cssStyle:"padding-top:5px; padding-bottom:5px"
		},
		{type:_TAB_BAR_, choices:this.tabChoices,
			ref: ZaModel.currentTab, colSpan:"*",cssClass:"ZaTabBar", id:"xform_tabbar"
		},
		{type:_SWITCH_,
			items:[
				{type:_ZATABCASE_,  caseKey:_tab1,  numCols:2,  colSizes: ["50%","50%"], //["50%","47%", "3%"],
				  items:[
					 {type:_GROUP_, width: "98%", numCols: 1,  //colSizes:[10,120,"auto",20],
						items:[	
 						    //{type:_CELLSPACER_, width:10, rowSpan:9},
 						    {type:_SPACER_, height:"5"}, 						    
 						    {type: _GROUP_, width: "98%", numCols: 2, colSizes:[100, "*"], items: [
 						    		{ref:ZaAccount.A_name, type:_EMAILADDR_, msgName:ZaMsg.DLXV_LabelListName, label: ZaMsg.DLXV_LabelListName +":", forceUpdate:true, tableCssStyle: "width:100", inputWidth:"100",
        								id:"dl_name_field"
        							},
        						    {ref:ZaAccount.A_displayname, type:_TEXTFIELD_, label:ZaMsg.NAD_DisplayName+":", msgName:ZaMsg.NAD_DisplayName,width:"100%",
        						    	cssClass:"admin_xform_name_input", align: _LEFT_
        						    },							
        						    {ref:ZaAccount.A_description, type:_TEXTFIELD_, label: ZaMsg.DLXV_LabelDescription+":",msgName: ZaMsg.DLXV_LabelDescription, width:"100%",
        						    	cssClass:"admin_xform_name_input"
        						    },
        							{ref: "zimbraMailStatus", type:_CHECKBOX_, trueValue:"enabled", falseValue:"disabled", align:_LEFT_,
        								label:ZaMsg.DLXV_LabelEnabled, msgName:ZaMsg.DLXV_LabelEnabled, labelLocation:_LEFT_,
        								labelCssClass:"xform_label", cssStyle:"padding-left:0px"
        							},	
	       							{ref:ZaAccount.A_zimbraHideInGal, type:_CHECKBOX_, trueValue:"TRUE", falseValue:"FALSE", align:_LEFT_,
        								label:ZaMsg.NAD_zimbraHideInGal, msgName:ZaMsg.NAD_zimbraHideInGal, labelLocation:_LEFT_,labelCssClass:"xform_label", cssStyle:"padding-left:0px"
        							}
    							]
							},
					        {type:_SPACER_, height:"3"},
					        {type:_OUTPUT_, value:ZaMsg.DLXV_LabelListMembers,  cssClass:"xform_label_left",
        						width: AjxEnv.isIE ? 100 : 94, cssStyle:"text-align: right;"
        					},
							{ref:ZaDistributionList.A2_members , type:_DWT_LIST_, height:"338", width:"98%", 
								cssClass: "DLTarget", cssStyle:"margin-left: 5px; ",
								widgetClass:ZaAccMiniListView, headerList:membersHeaderList,hideHeader:true,
								onSelection:ZaDLXFormView.membersSelectionListener
							},
					        {type:_SPACER_, height:"8"},
						    {type:_GROUP_, width:"98%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
								items:[
									{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:80, 
									   enableDisableChecks:[ZaDLXFormView.shouldEnableRemoveAllButton],
									   enableDisableChangeEventSources:[ZaDistributionList.A2_members],
									   onActivate:"ZaDLXFormView.removeAllMembers.call(this,event)"
									 },
									{type:_CELLSPACER_},
									{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:80, id:"removeButton",
								      onActivate:"ZaDLXFormView.removeMembers.call(this,event)",
								       enableDisableChangeEventSources:[ZaDistributionList.A2_membersSelected],
								      enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_membersSelected]]
								    },
									{type:_CELLSPACER_},
									{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
										onActivate:"ZaDLXFormView.backMemButtonHndlr.call(this,event)", 
										enableDisableChangeEventSources:[ZaDistributionList.A2_members],
										enableDisableChecks:[ZaDLXFormView.shouldEnableMemBackButton]
								    },								       
									{type:_CELLSPACER_},
									{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
										onActivate:"ZaDLXFormView.fwdMemButtonHndlr.call(this,event)",
										enableDisableChangeEventSources:[ZaDistributionList.A2_members], 
										enableDisableChecks:[ZaDLXFormView.shouldEnableMemForwardButton]
								    },								       
									{type:_CELLSPACER_}									
								]
							}
					    ]
				    },
					{type:_ZARIGHT_GROUPER_, numCols:1, width: "100%", label:ZaMsg.DLXV_GroupLabelAddMembers,	
						items:[			      
					       {type:_GROUP_, numCols:3, width:"98%", 
							   items:[
									{type:_TEXTFIELD_, cssClass:"admin_xform_name_input", ref:ZaSearch.A_query, label:ZaMsg.DLXV_LabelFind,
								      elementChanged: function(elementValue,instanceValue, event) {
										  var charCode = event.charCode;
										  if (charCode == 13 || charCode == 3) {
										      ZaDLXFormView.srchButtonHndlr.call(this);
										  } else {
										      this.getForm().itemChanged(this, elementValue, event);
										  }
							      		}
									},
									{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonSearch, width:80,
									   onActivate:ZaDLXFormView.srchButtonHndlr
									}
								]
					       },
					       {type:_SPACER_, height:"5"},
						   {ref:ZaDistributionList.A2_memberPool, type:_DWT_LIST_, height:"200", width:"100%", cssClass: "DLSource", 
						   		forceUpdate: true, widgetClass:ZaAccMiniListView, headerList:sourceHeaderList,hideHeader:false,
						   		onSelection:ZaDLXFormView.memberPoolSelectionListener
						   	},
					       {type:_SPACER_, height:"5"},
					       {type:_GROUP_, width:"98%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5],
							items: [
							   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, width:80,
								onActivate:"ZaDLXFormView.addAddressesToMembers.call(this,event)",
								enableDisableChangeEventSources:[ZaDistributionList.A2_memberPoolSelected],
								enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_memberPoolSelected]]
								},
							   {type:_CELLSPACER_},
							   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:80,
								onActivate:"ZaDLXFormView.addAllAddressesToMembers.call(this,event)",
								enableDisableChangeEventSources:[ZaDistributionList.A2_memberPool],
								enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_memberPool]],
								},
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
									enableDisableChecks:[ZaDLXFormView.shouldEnablePoolBackButton],
									enableDisableChangeEventSources:[ZaDistributionList.A2_poolPagenum],
									onActivate:"ZaDLXFormView.backPoolButtonHndlr.call(this,event)"
								},								       
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
								 	enableDisableChecks:[ZaDLXFormView.shouldEnablePoolForwardButton],
								 	enableDisableChangeEventSources:[ZaDistributionList.A2_poolPagenum],
									onActivate:"ZaDLXFormView.fwdPoolButtonHndlr.call(this,event)"									
								},								       
								{type:_CELLSPACER_}	
							  ]
					       },
					       
					       {type:_OUTPUT_, value:ZaMsg.DLXV_GroupLabelEnterAddressBelow},
					       {ref:"optionalAdd", type:_TEXTAREA_,width:"98%", height:98},
					       {type:_SPACER_, height:"5"},
					       {type:_GROUP_, numCols:2, width:"98%", colSizes:[80,"auto"],
								items: [
									{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromFreeForm, width:"100%",
										onActivate:"ZaDLXFormView.addFreeFormAddressToMembers.call(this,event)",
										enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_optionalAdd]],
										enableDisableChangeEventSources:[ZaDistributionList.A2_optionalAdd]									
									},
								   {type:_OUTPUT_, value:"Separate addresses with comma or return", align:"right"}
								]
					       }				       
						]
				    }
				  ]
				},
				{type:_ZATABCASE_, caseKey:_tab2, colSizes:[10, "auto"], colSpan:"*",
					items:[
					    {type:_SPACER_, height:5},
					    {type:_SPACER_, height:5},
					    {type:_CELLSPACER_, width:10 },
					    {type: _OUTPUT_, value:ZaMsg.DLXV_LabelNotes, cssStyle:"align:left"},
					    {type:_CELLSPACER_, width:10 },
					    {ref:ZaAccount.A_notes, type:_TEXTAREA_, width:"90%", height:"400", labelCssStyle:"vertical-align: top"}
					]
				 },
				 //MemberOf Tab
				{type:_ZATABCASE_, numCols:2, colSpan:"*", caseKey:_tab3, colSizes: ["50%", "50%"],
					items: [
						//layout rapper around the direct/indrect list						
						{type: _GROUP_, width: "98%", numCols: 1, //colSizes: ["auto", 20],
							items: [
							    {type:_SPACER_, height:"5"}, 							
								//direct member group
								{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "96%",  //height: 400,
									items:[
										{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
									   		items: [
												{type:_OUTPUT_, value:ZaMsg.Account_DirectGroupLabel, cssClass:"RadioGrouperLabel"},
												{type:_CELLSPACER_}
											]
										},
										{ref: ZaAccount.A2_directMemberList, type: _S_DWT_LIST_, width: "100%", height: 200,
											cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
											headerList: directMemberOfHeaderList, defaultColumnSortable: 0,
											onSelection:ZaDLXFormView.directMemberSelectionListener,
											forceUpdate: true 
										},
										{type:_SPACER_, height:"5"},
										{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
											items:[
												{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:80, 
											      enableDisableChangeEventSources:[ZaDistributionList.A2_directMemberList],
											      enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_directMemberList]],
												  onActivate:"ZaAccountMemberOfListView.removeAllGroups.call(this,event, ZaAccount.A2_directMemberList)"
												},
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:80, id:"removeButton",
											      onActivate:"ZaAccountMemberOfListView.removeGroups.call(this,event, ZaAccount.A2_directMemberList)",
											      enableDisableChangeEventSources:[ZaDistributionList.A2_directMemberSelected],
											      enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_directMemberSelected]]
											    },
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
													onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_directMemberList]],
													enableDisableChangeEventSources:[ZaAccount.A2_directMemberList + "_offset"]													 
											    },								       
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
													onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_directMemberList)", 
													enableDisableChangeEventSources:[ZaAccount.A2_directMemberList + "_offset"],
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_directMemberList]]
											    },								       
												{type:_CELLSPACER_}									
											]
										}		
									]
								},		
								{type:_SPACER_, height:"10"},	
								//indirect member group
								{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "98%", //colSizes:["auto"], height: "48%",
									items:[
										{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
									   		items: [
												{type:_OUTPUT_, value:ZaMsg.Account_IndirectGroupLabel, cssClass:"RadioGrouperLabel"},
												{type:_CELLSPACER_}
											]
										},
										{ref: ZaAccount.A2_indirectMemberList, type: _S_DWT_LIST_, width: "100%", height: 200,
											cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
											headerList: indirectMemberOfHeaderList, defaultColumnSortable: 0,
											onSelection:ZaDLXFormView.indirectMemberSelectionListener,
											forceUpdate: true 
										}	,
										{type:_SPACER_, height:"5"},
										{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
											items:[
												{type:_CELLSPACER_},
												{type:_CELLSPACER_},
												{type:_CELLSPACER_},
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
													onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
													enabeDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton,ZaAccount.A2_indirectMemberList]],
													enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList+"_offset"]
											    },								       
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
													onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_indirectMemberList)", 
													enabeDisableChecks:[[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_indirectMemberList]],
													enableDisableChangeEventSources:[ZaAccount.A2_indirectMemberList+"_offset"]
											    },								       
												{type:_CELLSPACER_}									
											]
										}
									]
								}
							]
						},
						//non member group
						//layout rapper around the elements						
						{type: _GROUP_, width: "98%", numCols: 1, //colSizes: ["auto", 20],
							items: [
							    {type:_SPACER_, height:"5"}, 							
								{type:_GROUP_, numCols:1, cssClass: "RadioGrouperBorder", width: "96%", //colSizes:["auto"], height: "98%",
									items:[
										{type:_GROUP_,  numCols:2, colSizes:["auto", "auto"],
									   		items: [
												{type:_OUTPUT_, value:ZaMsg.DL_NonGroupLabel, width: AjxEnv.isIE ? "248px": null, cssClass:"RadioGrouperLabel"},
												{type:_CELLSPACER_}
											]
										},
										{type:_GROUP_, numCols:3, colSizes:[30, "auto",85], width:"98%", 
										   items:[
												{ref:"query", type:_TEXTFIELD_, width:"100%", cssClass:"admin_xform_name_input",  label:ZaMsg.DLXV_LabelFind,
											      elementChanged: function(elementValue,instanceValue, event) {
													  var charCode = event.charCode;
													  if (charCode == 13 || charCode == 3) {
													      ZaAccountMemberOfListView.prototype.srchButtonHndlr.call(this);
													  } else {
													      this.getForm().itemChanged(this, elementValue, event);
													  }
										      		}
												},
												{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonSearch, width:80,
												   onActivate:ZaAccountMemberOfListView.prototype.srchButtonHndlr
												},
												{ref: ZaAccount.A2_showSameDomain, type: _CHECKBOX_, align:_RIGHT_, 												
														label:null,labelLocation:_NONE_, trueValue:"TRUE", falseValue:"FALSE",
														visibilityChecks:[
															function() {
																return ZaSettings.DOMAINS_ENABLED;
															}
														]
												},										
												{type:_OUTPUT_, value:ZaMsg.NAD_SearchSameDomain,colSpan:2}
											]
								         },
								        {type:_SPACER_, height:"5"},
										{ref: ZaAccount.A2_nonMemberList, type: _S_DWT_LIST_, width: "100%", height: 440,
											cssClass: "DLSource", widgetClass: ZaAccountMemberOfListView, 
											headerList: nonMemberOfHeaderList, defaultColumnSortable: 0,
											onSelection:ZaDLXFormView.nonmemberSelectionListener,
											//createPopupMenu: 
											forceUpdate: true },
											
										{type:_SPACER_, height:"5"},	
										//add action buttons
										{type:_GROUP_, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5],
											items: [
											   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, width:80,
												onActivate:"ZaAccountMemberOfListView.addGroups.call(this,event, ZaAccount.A2_nonMemberList)",
												enableDisableChangeEventSources:[ZaDistributionList.A2_nonmembersSelected],
												enableDisableChecks:[[XForm.checkInstanceValueNotEmty,ZaDistributionList.A2_nonmembersSelected]]
											   },
											   {type:_CELLSPACER_},
											   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:80,
												onActivate:"ZaAccountMemberOfListView.addAllGroups.call(this,event, ZaAccount.A2_nonMemberList)",
												enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList],
												enableDisableChecks:[[XForm.checkInstanceValueNotEmty, ZaAccount.A2_nonMemberList]]
											   },
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Previous, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
													enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList+"_offset"],
													enableDisableChecks:[[ZaAccountMemberOfListView.shouldEnableBackButton, ZaAccount.A2_nonMemberList]],
													onActivate:"ZaAccountMemberOfListView.backButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"
												},								       
												{type:_CELLSPACER_},
												{type:_DWT_BUTTON_, label:ZaMsg.Next, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
												 	enableDisableChangeEventSources:[ZaAccount.A2_nonMemberList + "_offset"],
												 	enableDisableChecks:[ZaAccountMemberOfListView.shouldEnableForwardButton,ZaAccount.A2_nonMemberList],
													onActivate:"ZaAccountMemberOfListView.fwdButtonHndlr.call(this,event, ZaAccount.A2_nonMemberList)"									
												},								       
												{type:_CELLSPACER_}	
											  ]
									    }								
									]
								}
							]
						}
					]
				},
				//DL Alias
				{type:_ZATABCASE_, width:"100%", numCols:1, colSizes:["auto"],
					caseKey:_tab4,
					items: [
						{type:_ZA_TOP_GROUPER_, borderCssClass:"LowPadedTopGrouperBorder",
							 width:"100%", numCols:1,colSizes:["auto"],
							label:ZaMsg.NAD_EditDLAliasesGroup,
							items :[
								{ref:ZaAccount.A_zimbraMailAlias, type:_DWT_LIST_, height:"200", width:"350px", 
									forceUpdate: true, preserveSelection:false, multiselect:true,cssClass: "DLSource", 
									headerList:null,onSelection:ZaAccountXFormView.aliasSelectionListener
								},
								{type:_GROUP_, numCols:5, width:"350px", colSizes:["100px","auto","100px","auto","100px"], 
									cssStyle:"margin-bottom:10px;padding-bottom:0px;margin-top:10px;pxmargin-left:10px;margin-right:10px;",
									items: [
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Delete,width:"100px",
											onActivate:"ZaAccountXFormView.deleteAliasButtonListener.call(this);",id:"deleteAliasButton",
											enableDisableChecks:[ZaAccountXFormView.isDeleteAliasEnabled],
											enableDisableChangeEventSources:[ZaAccount.A2_alias_selection_cache]
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.TBB_Edit,width:"100px",
											enableDisableChangeEventSources:[ZaAccount.A2_alias_selection_cache],
											enableDisableChecks:[ZaAccountXFormView.isEditAliasEnabled],
											onActivate:"ZaAccountXFormView.editAliasButtonListener.call(this);",id:"editAliasButton"
										},
										{type:_CELLSPACER_},
										{type:_DWT_BUTTON_, label:ZaMsg.NAD_Add,width:"100px",
											onActivate:"ZaAccountXFormView.addAliasButtonListener.call(this);"
										}
									]
								}
							]
						}
					]
				}//END of DL Alias
			]
		}
	]
};

ZaTabView.XFormModifiers["ZaDLXFormView"].push(ZaDLXFormView.myXFormModifier);
