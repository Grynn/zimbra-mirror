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
 * Distribution list controller 
 */
function ZaDLController (appCtxt, container, abApp, domain) {
	ZaController.call(this, appCtxt, container, abApp);
	this._domain = domain;
	this._createListeners();
	this._createToolbars();
	this.__internalId = AjxCore.assignId(this);
	this._helpURL = "/zimbraAdmin/adminhelp/html/OpenSourceAdminHelp/managing_accounts/provisioning_accounts.htm";
}

ZaDLController.prototype = new ZaController();
ZaDLController.prototype.constructor = ZaDLController;

ZaDLController.prototype.toString = function () {
	return "ZaDLController";
};

ZaDLController.MODE_NEW = 1;
ZaDLController.MODE_EDIT = 2;

//===============================================================
// initialization methods
//===============================================================
ZaDLController.prototype._createListeners = function () {
	this._listeners = {};
	this._listeners[ZaOperation.SAVE] = new AjxListener(this, this._saveListener);
	this._listeners[ZaOperation.CLOSE] = new AjxListener(this, this._cancelNewListener);
	this._listeners[ZaOperation.HELP] = new AjxListener(this, this._helpButtonListener);	
};

ZaDLController.prototype._createToolbars = function () {
	var ops = [
			   new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis",
							   this._listeners[ZaOperation.SAVE]),
			   new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Cancel, ZaMsg.DLTBB_Cancel_tt, "Close", "CloseDis",
							   this._listeners[ZaOperation.CLOSE]),
			   new ZaOperation(ZaOperation.NONE),
			   new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help",
			   				 this._listeners[ZaOperation.HELP])							
							   
			   ];
	this._toolbar = new ZaToolBar(this._container, ops);
};

//===============================================================
// rendering methods
//===============================================================
/**
 *
 */
ZaDLController.prototype.show = function(distributionList, mode) {
	this._viewMode = mode;
	this._setView(distributionList);
};


ZaDLController.prototype._updateOperations = 
function () {
	var ops, disOps;
	if (this._dlView.hasErrors()){
		ops = [ZaOperation.CANCEL];
		disOps = [ZaOperation.SAVE];
	} else {
		var instance = this._dlView.getInstance();
		if (instance.getMembersArray().length > 0 && instance.name != null) {
			ops = [ZaOperation.SAVE, ZaOperation.CLOSE];
		} else {
			ops =  [ZaOperation.CLOSE];
			disOps = [ZaOperation.SAVE];
		}
	}

	if (disOps != null) {
		this._toolbar.enable(disOps, false);
	} 
	if (ops != null) {
		this._toolbar.enable(ops, true);
	}
};

//===============================================================
// view accessor methods
//===============================================================

ZaDLController.prototype.getViewMode = function () {
	return this._viewMode;
};

/** 
 */
ZaDLController._elementsObject = {};
ZaDLController.prototype._getView = function (id, args) {
	var view = this._dlView;
	var toolbar = this._toolbar;
	if (view == null) {
		switch (id) {
		case ZaZimbraAdmin._DL_VIEW:
			this._app.getDomainList();
			var xModelObj = new XModel(ZaDLController.distributionListXModel);
			view = new XForm(this._getNewViewXForm(), xModelObj, args, this._container);
			var ls = new AjxListener(this, this._itemUpdatedListener);
			view.addListener(DwtEvent.XFORMS_VALUE_CHANGED, ls);

			ls = new AjxListener(this, this._itemErrorListener);
			view.addListener(DwtEvent.XFORMS_VALUE_ERROR, ls);

			view.setController(this);
			view.draw();

			view.getHtmlElement().style.position = "absolute";
			var controller = this;
			view.setData = function (dl) {
				dl.getMembers();
				var clone = dl.clone();
				if (clone.name == null) {
					clone.setMailStatus("enabled");
				}
				// yuck. This really shouldn't be hanging off the instance.
				clone.memberPool = [];
				clone[ZaModel.currentTab] = dl[ZaModel.currentTab];
				if (clone[ZaModel.currentTab] == null) {
					clone[ZaModel.currentTab] = "1";
				}
				view.getItemsById('members')[0].dirtyDisplay();
				view.getItemsById('searchText')[0].getElement().value = "";
				view.setInstance(clone);
				view.focusNext();
			};
			this._dlView = view;
			break;
		default:
			view = null;
			break;
		}
	}
	// if the view has a setData handler, and arguments were passed to us,
	// call it now
	if (view.setData != null && args != null) {
		view.setData(args);
	}
	var elements = ZaDLController._elementsObject;
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = toolbar;
	elements[ZaAppViewMgr.C_APP_CONTENT] = view;
	return elements;
};

ZaDLController.prototype._setView = function (args) {
	var id = ZaZimbraAdmin._DL_VIEW;
	// get the view from our stash
	var elements = this._getView(id, args);
	// get the app container from the app view manager
	var view = this._app.getAppViewMgr()._views[id];
	// if the app container from the app view manager is null,
	// create the app container
	if (view == null) {
		this._app.createView(id, elements);
	}

	this._app.pushView(id);
	// set our current view tracker.
	this._currentViewId = id;
	this._toolbar.enable([ZaOperation.SAVE], false);
};

ZaDLController.prototype.getControllerForView = function( viewId ) {
	return this;
};

//===============================================================
// button listener methods
//===============================================================
ZaDLController.prototype._saveListener = function (ev) {
	try { 
		var dl = this._dlView.getInstance();
		if (this.getViewMode() == ZaDLController.MODE_EDIT){
			dl.saveEdits();
		} else {
			dl.saveNew();
		}
		this._close(true);
	} catch (ex) {
		var handled = false;
		if (ex.code == ZmCsfeException.SVC_FAILURE) {
			// TODO -- make this a ZaMsg, and grab the member name out of the exception message.
			//
			if (ex.msg.indexOf("add failed") != -1){
				var m = ex.msg.replace(/system failure: /, "");
				this.popupErrorDialog(m, ex, true);		
				handled = true;
			}
		} else if (ex.code == ZmCsfeException.DISTRIBUTION_LIST_EXISTS) {
			this.popupErrorDialog(AjxStringUtil.resolve(ZaMsg.DLXV_ErrorDistributionListExists,[dl.name]), ex, true);		
			handled = true;
		}
		if (!handled) {
			this._handleException(ex, "ZaDLController.prototype._saveListener", null, false);
		}
	}
};

ZaDLController.prototype._cancelNewListener = function (ev) {
	// Cancel is only on the new screen, so go back to the list of
	// distribution lists
	this._close();
};

ZaDLController.prototype._close = function (refresh) {
	var acctListController = this._app.getAccountListController();
	if (refresh == true) {
		acctListController.search(acctListController.getQuery());
	} else {
		this._app.getAccountListController().show();
	}
};

ZaDLController.prototype._itemErrorListener = function (event) {
	this._itemUpdatedListener(event);
};

ZaDLController.prototype._itemUpdatedListener = function (event) {
	var model = event.formItem.getModelItem();
	if (model) {
		var field = model.id;
		// don't update the buttons if inconsequential fields are being updated
		if (field == "memberPool"  || field == "optionalAdd"){
			return;
		}
	}
	this._updateOperations();
};

ZaDLController.prototype.setQuery = function (query) {
	this._currentQuery = query;
};

//===============================================================
// Forms and form controller methods
//===============================================================
ZaDLController.moreItemId = "MORE_ITEM";
ZaDLController.prototype._setSearchResults = function (searchResults, appendResults) {

	var memberPoolItem = this._dlView.getItemsById("memberPool")[0];
	if (appendResults) {
		if (this._moreItem != null) {
			memberPoolItem.widget.removeItem(this._moreItem);
		}
	}
	var arr = searchResults.list.getArray();
	var tmpArr = new Array();
	var t;
	for (var i = 0 ; i < arr.length ; ++i) {
		t = new ZaDistributionListMember(arr[i].name);
		tmpArr.push(t);
	}

	var showMore = (searchResults.numPages > this._currentPageNum);
	if (showMore){
		var anchor = AjxBuffer.concat("<a href='javascript:' onclick='javascript:AjxCore.objectWithId(",
									  this.__internalId, ").fetchMore(", this._currentPageNum + 1,")'>More..</a>");
		this._moreItem = new String("");
		this._moreItem.id = ZaDLController.moreItemId;
		tmpArr.push(this._moreItem);
	}

	var instance = this._dlView.getInstance();
	if (appendResults) {
		memberPoolItem.appendItems(tmpArr);
		var list = memberPoolItem.widget.getList();
		instance.memberPool = list.getArray();
	} else {
		instance.memberPool = tmpArr;
	}

	this._dlView.refresh();
	// HACK -- Since the list view escapes strings, we have to set the anchor tag
	// ourselves, after the list has rendered
	if (showMore) {
		var div = document.getElementById(ZaDLController.moreItemId);
		div.innerHTML = anchor;
		// defeat the selection code for the list view
		div.onmousedown = ZaDLController.emptyHandler;
		div.onmouseup = ZaDLController.emptyHandler;
		div.ondblclick = ZaDLController.emptyHandler;
	}
};

ZaDLController.emptyHandler = function(event) {
	event = event? event: window.event;
	DwtUiEvent.setBehaviour(event, true, true);
};

ZaDLController.prototype.fetchMore = function () {
	this._search(this._currentQuery, this._currentPageNum + 1, true);
};

ZaDLController.prototype._searchListener = function (event, formItem) {
	var form = formItem.getForm();
	var searchText = form.getItemsById('searchText')[0].getElement().value;
	try {
		var searchQuery = new ZaSearchQuery(ZaSearch.getSearchByNameQuery(searchText), [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS], 
											this._domain, false);
		this._search(searchQuery, 1);
	} catch (ex) {
		// Only restart on error if we are not initialized and it isn't a parse error
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			this._handleException(ex, "ZaDLController.prototype._searchListener", null, (this._inited) ? false : true);
		} else {
			this.popupMsgDialog(ZaMsg.queryParseError, ex);
		}
	}	
};

ZaDLController.prototype._search = function (searchQuery, pagenum, appendResults) {
	this.setQuery(searchQuery);
	this._currentPageNum = pagenum;
	var results = ZaSearch.searchByQueryHolder(searchQuery, this._currentPageNum, null, null, this._app);
	this._setSearchResults(results, appendResults);
};


/**
 * Get the view form.
 */
ZaDLController.prototype._getNewViewXForm = function () {
    if (this._newXform == null) {
	this._newXform = {
	    X_showBorder:true,
	    numCols:5, 
	    cssClass:"ZaDLView", 
	    tableCssStyle: "width:100%;",
	    colSizes:[10,"auto", 20, "auto", 10],
	    itemDefaults:{
			_INPUT_: { cssClass:"inputBorder" },
			_TEXTAREA_: {cssClass: "inputBorder"},
			_TEXTFIELD_: {cssClass: "inputBorder", containerCssStyle:"width:100%"}, // containerClass is specifically for the first field in the list name
			_DWT_BUTTON: {forceUpdate: true}
	    },	    
	    items:[
		   {type:_SPACER_, height:10, colSpan:"*" },

			{type:_GROUP_, cssClass:"ZmSelectedHeaderBg", colSpan: "*", 
				items: [
					{type:_GROUP_,	numCols:4,colSizes:["32px","350px","100px","250px"],
						items: [
							{type:_AJX_IMAGE_, src:"Group_32", label:null, rowSpan:2},
							{type:_OUTPUT_, ref:"name", label:null,cssClass:"AdminTitle", rowSpan:2},
							{type:_OUTPUT_, ref:"id", label:ZaMsg.NAD_ZimbraID},
							{type:_OUTPUT_, ref:"zimbraMailStatus", label:ZaMsg.NAD_AccountStatus+":"}
						]
					}
				],
				cssStyle:"padding-top:5px; padding-bottom:5px"
			},

		   //{type:_OUTPUT_, colSpan:"*",cssClass:"ZmHead", value:ZaMsg.DLXV_Header, 
		   //   cssStyle:"background-color:lightgray; margin-bottom:5px;"},
				  // The colSizes are necessary for firefox to hold the position
			      // during the repositioning done in ZaAppViewMgr.pushView
			      {type:_TAB_BAR_, choices:[ {value:1, label:ZaMsg.DLXV_TabMembers}, {value:2, label:ZaMsg.DLXV_TabNotes}], ref: ZaModel.currentTab, colSpan:"*"},
			      {type:_SWITCH_, useParentTable: true, colSpan:"*", numCols:5,
				  items:[
					 {type:_CASE_, useParentTable:true, relevant:"instance[ZaModel.currentTab] == 1", colSpan:"*", numCols:5,
					  items:[
						 {type:_CELLSPACER_, width:10 },
 						    {type:_GROUP_, colSpan:1, width:"100%", colSizes:[70,"auto"],
 							 items:[	
									{ref:"name", type:_EMAILADDR_, xmsgName:ZaMsg.NAD_AccountName, label: ZaMsg.DLXV_LabelListName, 
									 forceUpdate:true, tableCssStyle:"width:100%", inputWidth:"100%"},

								   {ref: "description", type:_TEXTFIELD_, label: ZaMsg.DLXV_LabelDescription, width:"100%"},
								   {type:_OUTPUT_, value:ZaMsg.DLXV_LabelListMembers, width:"100%", colSpan:"*", cssClass:"xform_label_left", 
									cssStyle:"padding-left:0px"},
							       //{ref: "notes", type:_TEXTFIELD_, label: "Notes:", width:"100%"},
							       {type:_SPACER_, height:"3"},
									{ref:"members", type:_DWT_LIST_, colSpan:"*", cssClass: "DLTarget", widgetClass:ZaDLListView},
							       {type:_SPACER_, height:"8"},
								   {type:_GROUP_, colSpan:2, width:"100%", numCols:6, colSizes:[20,40,"100%",85,5, 85], 
									items:[
									  {ref: "zimbraMailStatus", type:_CHECKBOX_, trueValue:"enabled", falseValue:"disabled", label:ZaMsg.DLXV_LabelEnabled, 
									   cssStyle:"padding-left:0px"},
									  {type:_CELLSPACER_},
									  {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemoveAll, width:80, 
									   relevant:"(form.getController().shouldEnableRemoveAllButton())",
									   onActivate:"this.getFormController().removeAllMembers(event,this)",
									   relevantBehavior:_DISABLE_},
									  
									  {type:_CELLSPACER_},
									  {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonRemove, width:80, id:"removeButton",
									      onActivate:"this.getFormController().removeMembers(event,this)",
									      relevant:"(form.getController().shouldEnableMemberListButtons())",
									      relevantBehavior:_DISABLE_}
									 ]
							       }
							      ]
						    },
						    
						    {type:_CELLSPACER_, width:20},
						    
						    {type:_RADIO_GROUPER_, colSpan:1, numCols:2, colSizes:[50, "100%"], label:ZaMsg.DLXV_GroupLabelAddMembers,
							items:[			      
							       {type:_GROUP_, label:ZaMsg.DLXV_LabelFind, colSpan:"*", numCols:2, colSizes:["70%","30%"],tableCssStyle:"width:100%", 
								   items:[
									  {type:_INPUT_, id:"searchText", width:"100%",
									      elementChanged: function(elementValue,instanceValue, event) {
										  var charCode = event.charCode;
										  if (charCode == 13 || charCode == 3) {
										      this.getFormController()._searchListener(null, this);
										  } else {
										      this.getForm().itemChanged(this, elementValue, event);
										  }
									      }
									  },
									  {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonSearch, width:80,
									   onActivate:"this.getFormController()._searchListener(event,this)"},
									 ]
							       },
							       {type:_SPACER_, height:"5"},
								   {ref:"memberPool", type:_DWT_LIST_, colSpan:"*", cssClass: "DLSource", forceUpdate: true, widgetClass:ZaDLListView},
							       {type:_SPACER_, height:"5"},
							       {type:_GROUP_, width:"100%", colSpan:"*", numCols:4, colSizes:[85,5,85,"100%"],
									items: [
									   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromList, width:80,
										onActivate:"this.getFormController().addAddressToMembers(event, this)",
										relevant:"(form.getController().shouldEnableMemberPoolListButtons())",
										relevantBehavior:_DISABLE_},
									   {type:_CELLSPACER_},
									   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddAll, width:80,
										onActivate:"this.getFormController().addAllAddressesToMembers(event, this)",
										relevant:"(form.getController().shouldEnableAddAllButton())",
										relevantBehavior:_DISABLE_},
									   {type:_CELLSPACER_},
									  ]
							       },
							       
							       {type:_TOP_GROUPER_, label:ZaMsg.DLXV_GroupLabelEnterAddressBelow, colSpan:"*", items:[]},
							       {ref:"optionalAdd", type:_TEXTAREA_, colSpan:"*", width:"100%", height:98},
							       {type:_SPACER_, height:"5"},
							       {type:_GROUP_, colSpan:"*", numCols:2, width:"100%", colSizes:[80,"100%"],
								   items: [
									   {type:_DWT_BUTTON_, label:ZaMsg.DLXV_ButtonAddFromFreeForm, width:"100%",
										onActivate:"this.getFormController().addFreeFormAddressToMembers(event, this)",
										relevant:"form.getController().shouldEnableFreeFormButtons()",
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
					 {type:_CASE_, useParentTable:false, relevant:"instance[ZaModel.currentTab] == 2", colSizes:[10, "auto"], colSpan:"*",
					     items:[
						    {type:_SPACER_, height:5},
						    {type:_SPACER_, height:5},
						    {type:_CELLSPACER_, width:10 },
						    {type: _OUTPUT_, value:ZaMsg.DLXV_LabelNotes, cssStyle:"align:left"},
						    {type:_CELLSPACER_, width:10 },
						    {ref: "notes", type:_TEXTAREA_, width:"90%", height:"400", labelCssStyle:"vertical-align: top"}
						   ]
					 }
					 ]
			      },
		   /*{type:_CELLSPACER_, width:10 }*/
		   ]
	}
    }
    return this._newXform;
};


ZaDLController.prototype.removeMembers = function(event, formItem) {
	var form = formItem.getForm();
	var membersSelection = this._getMemberSelection();
	form.getInstance().removeMembers(membersSelection);
	form.refresh();	
	this._updateOperations();
};

ZaDLController.prototype.removeAllMembers = function(event, formItem) {
	var form = formItem.getForm();
	form.getInstance().removeAllMembers();
	form.refresh();
	this._updateOperations();
};

ZaDLController.prototype.shouldEnableMemberListButtons = function() {
	return (this._getMemberSelection().length > 0);
};

ZaDLController.prototype.shouldEnableRemoveAllButton = function() {
	if (this._dlView != null) {
		var list = this._dlView.getItemsById("members")[0].widget.getList();
		if (list != null) {
			return ( list.size() > 0);
		}
	}
	return false;
};

ZaDLController.prototype.shouldEnableAddAllButton = function() {
	if (this._dlView != null) {
		var list = this._dlView.getItemsById("memberPool")[0].widget.getList();
		if (list != null) {
			return ( list.size() > 0);
		}
	}
	return false;
};

ZaDLController.prototype.shouldEnableMemberPoolListButtons = function() {
	return (this._getMemberPoolSelection().length > 0);
};

ZaDLController.prototype.shouldEnableFreeFormButtons = function () {
	var optionalAdd = null;
	if (this._dlView != null) {
		optionalAdd = this._dlView.getInstance().optionalAdd;
	}
	return (optionalAdd != null && optionalAdd.length > 0);
};

ZaDLController.prototype._getMemberPoolItem = function () {
	if (this.$memberPoolItem == null) {
		this.$memberPoolItem = this._dlView.getItemsById("memberPool")[0];
	}
	return this.$memberPoolItem;
};

ZaDLController.prototype._getMemberItem = function () {
	if (this.$memberItem == null) {
		this.$memberItem = this._dlView.getItemsById("members")[0];
	}
	return this.$memberItem;
};

ZaDLController.prototype._getMemberPoolSelection = function () {
	if (this._dlView != null) {
		var memberPoolItem = this._getMemberPoolItem();
		return memberPoolItem.getSelection();
	}
	return [];
};

ZaDLController.prototype._getMemberSelection = function () {
	if (this._dlView != null) {
		var membersItem = this._getMemberItem();
		return membersItem.getSelection();
	} 
	return [];
};

ZaDLController.prototype.addAllAddressesToMembers = function (event, formItem) {
	var form = formItem.getForm();
	var pool = form.getInstance().memberPool;
	this._addListToMemberList(form, pool);
};

/**
 * Currently, this manages the data, redraws the whole list, and then sets 
 * the selection. 
 * TODO - change the routine to add only the necessary rows to the list view.
 * Same is true of addAllAddresses
 */
ZaDLController.prototype.addAddressToMembers = function (event, formItem) {
 	var form = formItem.getForm();
	var memberPoolSelection = this._getMemberPoolSelection();
	// get the current value of the textfied
	var val;
	this._addListToMemberList (form, memberPoolSelection);
};

/**
 */
ZaDLController._IDS = 0;
ZaDLController.prototype.addFreeFormAddressToMembers = function (event, formItem) {
 	var form = formItem.getForm();
	// get the current value of the textfied
 	var val = form.get("optionalAdd");
 	var values = val.split(/[\r\n,;]+/);
	var cnt = values.length;
 	var members = new Array();
	for (var i = 0; i < cnt; i++) {
		members.push(new ZaDistributionListMember(values[i]));
	}
//	var item = new ZaDistributionListMember(val);
	this._addListToMemberList(form, members);
	form.getInstance().optionalAdd = null;
};

ZaDLController.prototype._addListToMemberList = function (form, list) {
	if (form.getInstance().addMembers(list)) {
		form.refresh();
	}
	this._getMemberItem().widget.setSelectedItems(list);
	this._updateOperations();
	// This should happen by looking at the data, but, the list view has it's data updated ( in form.refresh )
	// and deletes it's selection. We keep track of the selection here, and poke the list view,
	// after the refresh, so the button knows nothing about the selection in the list.
	// We have to poke the button, and set it to enabled.
	form.getItemsById('removeButton')[0].widget.setEnabled(true);
};



ZaDLController._validEmailPattern = new RegExp(/^([a-zA-Z0-9_\-])+((\.)?([a-zA-Z0-9_\-])+)*@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
ZaDLController.distributionListXModel = {
	getMemberPool: function (model, instance) {
		return instance.memberPool;
	},
	setMemberPool: function (value, instance, parentValue, ref) {
		instance.memberPool = value;
	},
	// transform a vector into something the list view will be 
	// able to handle
	getMembersArray: function (model, instance) {
		var arr = instance.getMembersArray();
		var tmpArr = new Array();
		var tmp;
		for (var i = 0; i < arr.length; ++i ){
			tmp = arr[i];
			if (!AjxUtil.isObject(arr[i])){
				tmp = new ZaDistributionListMember(arr[i]);
			}
			tmpArr.push(tmp);
		}
		return tmpArr;
	},
	setMembersArray: function (value, instance, parentValue, ref) {
		instance.setMembers(value);
	},

	items: [
			// These three items really shouldn't be here. They are transient, and we really don't want to save
			// their state.
	{id: "memberPool", type:_LIST_, setter:"setMemberPool", setterScope:_MODEL_, getter: "getMemberPool", getterScope:_MODEL_},
	{id: "optionalAdd", type:_UNTYPED_},

	{id: "name", type:_STRING_, setter:"setName", setterScope: _INSTANCE_, required:true,
	 constraints: {type:"method", value:
				   function (value, form, formItem, instance) {
					   var parts = value.split('@');
					   if (parts[0] == null || parts[0] == ""){
						   // set the name, so that on refresh, we don't display old data.
						   throw ZaMsg.DLXV_ErrorNoListName;
					   } else {
						   var re = ZaDLController._validEmailPattern;
						   if (re.test(value)) {
							   return value;
						   } else {
							   throw ZaMsg.DLXV_ErrorInvalidListName;
						   }
					   }
				   }
		}
	},
	{id: "members", type:_LIST_, getter: "getMembersArray", getterScope:_MODEL_, setter: "setMembersArray", setterScope:_MODEL_},
	{id: "description", type:_STRING_, setter:"setDescription", setterScope:_INSTANCE_, getter: "getDescription", getterScope: _INSTANCE_},
	{id: "notes", type:_STRING_, setter:"setNotes", setterScope:_INSTANCE_, getter: "getNotes", getterScope: _INSTANCE_},
	{id: "zimbraMailStatus", type:_STRING_, setter:"setMailStatus", setterScope:_INSTANCE_, getter: "getMailStatus", getterScope: _INSTANCE_}
	]
};

