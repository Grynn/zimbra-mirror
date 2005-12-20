/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
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
function ZaDLXFormView (parent, app) {
	ZaTabView.call(this, parent, app,"ZaDLXFormView");
	this.initForm(ZaDistributionList.myXModel,this.getMyXForm());
	this._localXForm.addListener(DwtEvent.XFORMS_FORM_DIRTY_CHANGE, new AjxListener(app.getDistributionListController(), ZaDLController.prototype.handleXFormChange));
	this._localXForm.addListener(DwtEvent.XFORMS_VALUE_ERROR, new AjxListener(app.getDistributionListController(), ZaDLController.prototype.handleXFormChange));	
}

ZaDLXFormView.prototype = new ZaTabView();
ZaDLXFormView.prototype.constructor = ZaDLXFormView;
ZaTabView.XFormModifiers["ZaDLXFormView"] = new Array();

ZaDLXFormView.prototype.getTitle = 
function () {
	return ZaMsg.DL_view_title;
}

/**
* method of an XFormItem
**/
ZaDLXFormView.removeAllMembers = function(event) {
	var form = this.getForm();
	form.getInstance().removeAllMembers();
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
		form.refresh();	
	}
};

/**
* method of an XFormItem
**/
ZaDLXFormView.srchButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	fieldObj.searchAccounts(evt);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.fwdPoolButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	var currentPageNum = this.getInstanceValue("/poolPagenum");
	this.setInstanceValue(currentPageNum+1,"/poolPagenum");
	fieldObj.searchAccounts(evt);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.backPoolButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	var currentPageNum = this.getInstanceValue("/poolPagenum")-1;
	this.setInstanceValue(currentPageNum,"/poolPagenum");
	fieldObj.searchAccounts(evt);
}

/**
* method of an XFormItem
**/
ZaDLXFormView.fwdMemButtonHndlr = 
function(evt) {
	var fieldObj = this.getForm().parent;
	var currentPageNum = this.getInstanceValue("/memPagenum")+1;
	this.setInstanceValue(currentPageNum,"/memPagenum");
	this.getInstance().getMembers(true, ZaDistributionList.MEMBER_QUERY_LIMIT);
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
	this.getInstance().getMembers(true, ZaDistributionList.MEMBER_QUERY_LIMIT);
	this.getForm().refresh();
}
/**
* method of the XForm
**/
ZaDLXFormView.getMemberSelection = 
function () {
	var memberItem = this.getItemsById("members")[0];
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
	var list = this.getForm().getItemsById("members")[0].widget.getList();
	if (list != null) {
		return ( list.size() > 0);
	}
	return false;
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
	return (this.getInstance().poolPagenum < this.getInstance().poolNumPages);
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnablePoolBackButton = function () {
	return (this.getInstance().poolPagenum > 1);
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableMemForwardButton = function () {
	return (this.getInstance().memPagenum < this.getInstance().memNumPages);
};

/**
* method of the XForm
**/
ZaDLXFormView.shouldEnableMemBackButton = function () {
	return (this.getInstance().memPagenum > 1);
};

/**
* method of the XForm
**/
ZaDLXFormView.addListToMemberList = function (list) {
	if (this.getInstance().addMembers(list)) {
		this.refresh();
	}
	this.getItemsById("members")[0].widget.setSelectedItems(list);
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
	var pool = form.getInstance().memberPool;
	ZaDLXFormView.addListToMemberList.call(form, pool);
};

/**
 * method of an XFormItem
**/
ZaDLXFormView.addFreeFormAddressToMembers = function (event) {
 	var form = this.getForm();
	// get the current value of the textfied
 	var val = form.get("optionalAdd");
 	var values = val.split(/[\r\n,;]+/);
	var cnt = values.length;
 	var members = new Array();
	for (var i = 0; i < cnt; i++) {
		members.push(new ZaDistributionListMember(values[i]));
	}
	ZaDLXFormView.addListToMemberList.call(form, members);
	form.getInstance().optionalAdd = null;
};

ZaDLXFormView.prototype.setObject = 
function (entry) {
	this._containedObject = entry.clone();
	
	if(!entry[ZaModel.currentTab])
		this._containedObject[ZaModel.currentTab] = "1";
	else
		this._containedObject[ZaModel.currentTab] = entry[ZaModel.currentTab];
		
	this._localXForm.setInstance(this._containedObject);	
}

ZaDLXFormView.prototype.searchAccounts = 
function (ev) {
	try {
		var  searchQueryHolder = new ZaSearchQuery(ZaSearch.getSearchByNameQuery(this._containedObject["query"]), [ZaSearch.ACCOUNTS,ZaSearch.DLS,ZaSearch.ALIASES], false, "",null,10);
		var result = ZaSearch.searchByQueryHolder(searchQueryHolder, this._containedObject["poolPagenum"], ZaAccount.A_name, null, this._app);
		if(result.list) {
			this._containedObject.memberPool = result.list.getArray();
		}
		this._containedObject.poolNumPages = result.numPages;
		this._localXForm.refresh();

	} catch (ex) {
		// Only restart on error if we are not initialized and it isn't a parse error
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			this._app.getCurrentController()._handleException(ex, "ZaDLXFormView.prototype.searchAccounts", null, (this._inited) ? false : true);
		} else {
			this.popupErrorDialog(ZaMsg.queryParseError, ex);
			this._searchField.setEnabled(true);	
		}
	}
}



ZaDLXFormView.myXFormModifier = function(xFormObject) {	
	var sourceHeaderList = new Array();
	sourceHeaderList[0] = new ZaListHeaderItem("type", ZaMsg.ALV_Type_col, null, 34, true, "objectClass", true, true);
	sourceHeaderList[1] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, null, true, ZaAccount.A_name, true, true);
	//idPrefix, label, iconInfo, width, sortable, sortField, resizeable, visible
	sourceHeaderList[2] = new ZaListHeaderItem(ZaAccount.A_displayname, ZaMsg.ALV_DspName_col, null, 100, true,ZaAccount.A_displayname, true, true);

	var membersHeaderList = new Array();
	membersHeaderList[0] = new ZaListHeaderItem(ZaAccount.A_name, ZaMsg.ALV_Name_col, null, null, true, ZaAccount.A_name, true, true);

	xFormObject.tableCssStyle = "width:100%;overflow:auto;";
	xFormObject.cssClass="ZaDLView";
	xFormObject.numCols=5;
	xFormObject.X_showBorder = true;
	xFormObject.colSizes = [10,"auto", 20, "auto", 10];
	xFormObject.itemDefaults = {
			_INPUT_: { cssClass:"inputBorder" },
			_TEXTAREA_: {cssClass: "inputBorder"},
			_TEXTFIELD_: {cssClass: "inputBorder", containerCssStyle:"width:100%"}, 
			_DWT_BUTTON: {forceUpdate: true}
	    };
	    
	xFormObject.items = [
	   	{type:_SPACER_, height:10, colSpan:"*" },
		{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", 
			items: [
				{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
					items: [
						{type:_AJX_IMAGE_, src:"Group_32", label:null, rowSpan:2},
						{type:_OUTPUT_, ref:"name", label:null,cssClass:"AdminTitle", rowSpan:2},
						{type:_OUTPUT_, ref:"id", label:ZaMsg.NAD_ZimbraID},
						{type:_OUTPUT_, ref:"zimbraMailStatus", label:ZaMsg.NAD_AccountStatus}
					]
				}
			],
			cssStyle:"padding-top:5px; padding-bottom:5px"
		},
		{type:_TAB_BAR_, choices:
				[ 
					{value:1, label:ZaMsg.DLXV_TabMembers}, 
					{value:2, label:ZaMsg.DLXV_TabNotes}
				], 
			ref: ZaModel.currentTab, colSpan:"*"
		},
		{type:_SWITCH_, colSpan:"*", numCols:5,
			items:[
				{type:_CASE_,  relevant:"instance[ZaModel.currentTab] == 1", colSpan:"*", numCols:5,
				  items:[
					 {type:_CELLSPACER_, width:10 },
					 {type:_GROUP_, colSpan:1, width:"100%", colSizes:[70,"auto"],
						items:[	
							{ref:"name", type:_EMAILADDR_, xmsgName:ZaMsg.NAD_AccountName, label: ZaMsg.DLXV_LabelListName, 
								onChange:ZaTabView.onFormFieldChanged, forceUpdate:true, tableCssStyle:"width:100%", inputWidth:"100%"
							},
						    {ref: "description", type:_TEXTFIELD_, label: ZaMsg.DLXV_LabelDescription, width:"100%",
						    	onChange:ZaTabView.onFormFieldChanged
						    },
							{ref: "zimbraMailStatus", type:_CHECKBOX_, trueValue:"enabled", falseValue:"disabled", 
								label:ZaMsg.DLXV_LabelEnabled, cssStyle:"padding-left:0px", onChange:ZaTabView.onFormFieldChanged
							},						    
						    {type:_OUTPUT_, value:ZaMsg.DLXV_LabelListMembers, width:"100%", colSpan:"*", cssClass:"xform_label_left", 
								cssStyle:"padding-left:0px"},
					        {type:_SPACER_, height:"3"},
							{ref:"members", type:_DWT_LIST_, colSpan:"*", height:"338", width:"100%", cssClass: "DLTarget", 
								widgetClass:ZaDLListView, headerList:null},
					        {type:_SPACER_, height:"8"},
						    {type:_GROUP_, colSpan:2, width:"100%", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5], 
								items:[
									{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:80, 
									   relevant:"ZaDLXFormView.shouldEnableRemoveAllButton.call(item)",
									   onActivate:"ZaDLXFormView.removeAllMembers.call(this,event)",
									   relevantBehavior:_DISABLE_},
									{type:_CELLSPACER_},
									{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:80, id:"removeButton",
								      onActivate:"ZaDLXFormView.removeMembers.call(this,event)",
								      relevant:"ZaDLXFormView.shouldEnableMemberListButtons.call(this)",
								      relevantBehavior:_DISABLE_},
									{type:_CELLSPACER_},
									{type:_DWT_BUTTON_, label:ZaMsg.Back, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis", 	
										onActivate:"ZaDLXFormView.backMemButtonHndlr.call(this,event)", 
										relevantBehavior:_DISABLE_, relevant:"ZaDLXFormView.shouldEnableMemBackButton.call(this)"
								    },								       
									{type:_CELLSPACER_},
									{type:_DWT_BUTTON_, label:ZaMsg.Forward, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",	
										onActivate:"ZaDLXFormView.fwdMemButtonHndlr.call(this,event)", 
										relevantBehavior:_DISABLE_, relevant:"ZaDLXFormView.shouldEnableMemForwardButton.call(this)"
								    },								       
									{type:_CELLSPACER_}									
								]
							}
					    ]
				    },
				    {type:_CELLSPACER_, width:20},
				    {type:_RADIO_GROUPER_, colSpan:1, numCols:2, colSizes:[50, "100%"], label:ZaMsg.DLXV_GroupLabelAddMembers,
						items:[			      
					       {type:_GROUP_, label:ZaMsg.DLXV_LabelFind, colSpan:"*", numCols:2, colSizes:["70%","30%"],tableCssStyle:"width:100%", 
							   items:[
									{type:_INPUT_, ref:ZaSearch.A_query, width:"100%",
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
									},
								]
					       },
					       {type:_SPACER_, height:"5"},
						   {ref:"memberPool", type:_DWT_LIST_, height:"200", width:"100%",  colSpan:"*", cssClass: "DLSource", 
						   		forceUpdate: true, widgetClass:ZaDLListView, headerList:sourceHeaderList},
					       {type:_SPACER_, height:"5"},
					       {type:_GROUP_, width:"100%", colSpan:"*", numCols:8, colSizes:[85,5, 85,"100%",80,5,80,5],
							items: [
							   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, width:80,
								onActivate:"ZaDLXFormView.addAddressesToMembers.call(this,event)",
								relevant:"ZaDLXFormView.shouldEnableMemberPoolListButtons.call(this)",
								relevantBehavior:_DISABLE_},
							   {type:_CELLSPACER_},
							   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:80,
								onActivate:"ZaDLXFormView.addAllAddressesToMembers(this,event)",
								relevant:"ZaDLXFormView.shouldEnableAddAllButton.call(this)",
								relevantBehavior:_DISABLE_},
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.Back, width:75, id:"backButton", icon:"LeftArrow", disIcon:"LeftArrowDis",
									relevantBehavior:_DISABLE_, relevant:"ZaDLXFormView.shouldEnablePoolBackButton.call(this)",
									onActivate:"ZaDLXFormView.backPoolButtonHndlr.call(this,event)"
								},								       
								{type:_CELLSPACER_},
								{type:_DWT_BUTTON_, label:ZaMsg.Forward, width:75, id:"fwdButton", icon:"RightArrow", disIcon:"RightArrowDis",
								 	relevantBehavior:_DISABLE_, relevant:"ZaDLXFormView.shouldEnablePoolForwardButton.call(this)",
									onActivate:"ZaDLXFormView.fwdPoolButtonHndlr.call(this,event)"									
								},								       
								{type:_CELLSPACER_}	
							  ]
					       },
					       
					       {type:_TOP_GROUPER_, label:ZaMsg.DLXV_GroupLabelEnterAddressBelow, colSpan:"*", items:[]},
					       {ref:"optionalAdd", type:_TEXTAREA_, colSpan:"*", width:"100%", height:98},
					       {type:_SPACER_, height:"5"},
					       {type:_GROUP_, colSpan:"*", numCols:2, width:"100%", colSizes:[80,"100%"],
								items: [
									{type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromFreeForm, width:"100%",
										onActivate:"ZaDLXFormView.addFreeFormAddressToMembers.call(this,event)",
										relevant:"ZaDLXFormView.shouldEnableFreeFormButtons.call(this)",
										relevantBehavior:_DISABLE_
									},
								   {type:_OUTPUT_, value:"Separate addresses with comma or return", align:"right"}
								]
					       },					 
						]
				    },
				    {type:_CELLSPACER_, width:10 }
				]
				},
				{type:_CASE_, relevant:"instance[ZaModel.currentTab] == 2", colSizes:[10, "auto"], colSpan:"*",
					items:[
					    {type:_SPACER_, height:5},
					    {type:_SPACER_, height:5},
					    {type:_CELLSPACER_, width:10 },
					    {type: _OUTPUT_, value:ZaMsg.DLXV_LabelNotes, cssStyle:"align:left"},
					    {type:_CELLSPACER_, width:10 },
					    {ref: "notes", type:_TEXTAREA_, width:"90%", height:"400", labelCssStyle:"vertical-align: top",
					    	onChange:ZaTabView.onFormFieldChanged
					    }
					]
				 }
			]
		}
	];
};
ZaTabView.XFormModifiers["ZaDLXFormView"].push(ZaDLXFormView.myXFormModifier);