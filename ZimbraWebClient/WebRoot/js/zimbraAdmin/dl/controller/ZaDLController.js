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
 * The Original Code is: Zimbra Collaboration Suite.
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
}

ZaDLController.prototype = new ZaController();
ZaDLController.prototype.constructor = ZaDLController;

ZaDLController.prototype.toString = function () {
	return "ZaDLController";
};

ZaDLController.NEW_DL_VIEW = "ZaDLController.NEW_DISTRIBUTION_LIST_VIEW";
ZaDLController.MODE_NEW = 1;
ZaDLController.MODE_EDIT = 2;

//===============================================================
// initialization methods
//===============================================================
ZaDLController.prototype._createListeners = function () {
	this._listeners = {};
	this._listeners[ZaOperation.SAVE] = new AjxListener(this, this._saveListener);
	this._listeners[ZaOperation.CLOSE] = new AjxListener(this, this._cancelNewListener);
};

ZaDLController.prototype._createToolbars = function () {
	var ops = [
			   new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis",
							   this._listeners[ZaOperation.SAVE]),
			   new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Cancel, ZaMsg.DLTBB_Cancel_tt, "Close", "CloseDis",
							   this._listeners[ZaOperation.CLOSE])
			   ];
	this._toolbar = new ZaToolBar(this._container, ops);
};

//===============================================================
// rendering methods
//===============================================================

ZaDLController.prototype.getDummyDistributionLists = function () {
	var tmp = [
			   new ZaDistributionList(this._app, "one", "one@zimbra.com", ["foo@goo.com", "fuddy@duddy.com", "boo@hoo.com", "bob@marley.com"], "Some one description"),
			   new ZaDistributionList(this._app, "two", "two@zimbra.com",["foo@goo.com", "fuddy@duddy.com", "boo@hoo.com", "bob@marley.com"], "Some two description"),
			   new ZaDistributionList(this._app, "three", "three@zimbra.com", ["foo@goo.com", "fuddy@duddy.com", "boo@hoo.com", "bob@marley.com"],  "Some three description"),
			   new ZaDistributionList(this._app, "four", "four@zimbra.com",["foo@goo.com", "fuddy@duddy.com", "boo@hoo.com", "bob@marley.com"], "Some four description"),
			   new ZaDistributionList(this._app, "five", "five@zimbra.com",["foo@goo.com", "fuddy@duddy.com", "boo@hoo.com", "bob@marley.com"], "Some five description"),
			   new ZaDistributionList(this._app, "six", "six@zimbra.com",["foo@goo.com", "fuddy@duddy.com", "boo@hoo.com", "bob@marley.com"], "Some six description"),
			   ];
	return tmp;
};

/**
 *
 */
ZaDLController.prototype.show = function(distributionList, mode) {
	this._viewMode = mode;
	distributionList.memberPool = [];
	this._setView(distributionList);
};


ZaDLController.prototype._updateOperations = 
function (optionalOps, optionalDisOps) {
	var disOps = (optionalDisOps != null)? optionalDisOps : null;
	var ops = (optionalOps != null)? optionalOps : null;
	// if both ops, and disOps have not been specified, use our standard rules
	if (ops == null && disOps == null) {
		ops = [ZaOperation.CLOSE];
		disOps = [ZaOperation.SAVE];
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
 * view factory method
 */
ZaDLController.prototype._getView = function (id, args) {
	var view = this._dlView;
	var toolbar = this._toolbar;
	if (view == null) {
		switch (id) {
		case ZaDLController.NEW_DL_VIEW:
			var xModelObj = new XModel(ZaDLController.distributionListXModel);
			view = new XForm(this._getNewViewXForm(), xModelObj, args, this._container);
			var ls = new AjxListener(this, this._itemUpdatedListener);
			view.addListener(DwtEvent.XFORMS_VALUE_CHANGED, ls);
			view.setController(this);
			view.draw();
			view.getHtmlElement().style.position = "absolute";
			var controller = this;
			view.setData = function (dl) {
				// data is probably a ZaItem that doesn't have dl details
				//if (controller._viewMode == ZaDLController.MODE_EDIT) dl.getMembers();
				dl.getMembers();
				view.setInstance(dl);
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
	var elements = {};
	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = toolbar;
	elements[ZaAppViewMgr.C_APP_CONTENT] = view;
	return elements;
};

ZaDLController.prototype._setView = function (args) {
	var id = ZaDLController.NEW_DL_VIEW;
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
	this._updateOperations();
};

ZaController.prototype.getControllerForView = function( viewId ) {
	return this;
};

//===============================================================
// button listener methods
//===============================================================
ZaDLController.prototype._saveListener = function (ev) {
	//DBG.println("SAVE NOT IMPLEMENTED");
	try { 
		var dl = this._dlView.getInstance();
		if (this.getViewMode() == ZaDLController.MODE_EDIT){
			dl.saveEdits();
		} else {
			dl.saveNew();
		}
		this._close();
	} catch (ex) {
		this._handleException(ex, "ZaDLController.prototype._saveListener", null, false);
	}
};

ZaDLController.prototype._cancelNewListener = function (ev) {
	// Cancel is only on the new screen, so go back to the list of
	// distribution lists
	this._close();
};

ZaDLController.prototype._close = function () {
	this._app.getAccountListController().show();	
};

ZaDLController.prototype._itemUpdatedListener = function (event) {
	var form = event.form;
	if (form.hasErrors()){
		this._updateOperations();
	} else {
		var ops = [ZaOperation.SAVE, ZaOperation.CANCEL];
		this._updateOperations(ops);
	}
};

ZaDLController.prototype.setQuery = function (query) {
	this._currentQuery = query;
};

//===============================================================
// Forms and form controller methods
//===============================================================
ZaDLController.prototype._setSearchResults = function (searchResults) {

	var arr = searchResults.list.getArray();
	var tmpArr = new Array();
	var t;
	for (var i = 0 ; i < arr.length ; ++i) {
		t = new String(arr[i].name);
		t.id = arr[i].id;
		tmpArr.push(t);
	}
	this._dlView.getInstance().memberPool = tmpArr;
	this._dlView.refresh();
};

ZaDLController.prototype._searchListener = function (event, formItem) {
	var btn = event.item;
	var form = formItem.getForm();
	//var searchTextItem = form.getItemsById("searchText")[0];
	var instance = form.getInstance();
	var searchText = instance.searchText;
	try {
		// TODO -- paging of results ....
		var searchQuery = new ZaSearchQuery(ZaSearch.getSearchByNameQuery(searchText), [ZaSearch.ALIASES,ZaSearch.DLS,ZaSearch.ACCOUNTS], 
											this._domain, false);
		this.setQuery(searchQuery);
		var results = ZaSearch.searchByQueryHolder(searchQuery, 1, null, null, this._app);
		this._setSearchResults(results);
		form.refresh();
	} catch (ex) {
		// Only restart on error if we are not initialized and it isn't a parse error
		if (ex.code != ZmCsfeException.MAIL_QUERY_PARSE_ERROR) {
			this._handleException(ex, "ZaDLController.prototype._searchListener", null, (this._inited) ? false : true);
		} else {
			this.popupMsgDialog(ZaMsg.queryParseError, ex);
		}
	}	
};

/**
 * Get the view form.
 */
ZaDLController.prototype._getNewViewXForm = function () {
    if (this._newXform == null) {
	this._newXform = {
	    X_showBorder:1,
	    numCols:3, 
	    cssClass:"ZaDLView", 
	    colSizes:[10,"100%", 10],
	    itemDefaults:{
			_INPUT_: { cssClass:"inputBorder" },
			_TEXTAREA_: {cssClass: "inputBorder"},
			_TEXTFIELD_: {cssClass: "inputBorder"},
			_DWT_BUTTON: {forceUpdate: true}
	    },	    
	    items:[
		   {type:_SPACER_, height:10, colSpan:"*" },
		   {type:_CELLSPACER_, width:10 },
		   
		   {type:_GROUP_, width:"100%", numCols:3, tableCssStyle:"width:100%",colSizes:["auto",20,"auto"],
		       items:[
			      {type:_OUTPUT_, colSpan:"*",cssClass:"ZmHead", value:"&nbsp;Manage Distribution List", 
				   cssStyle:"background-color:lightgray; margin-bottom:5px;"},
				  // The colSizes are necessary for firefox to hold the position
				  // during the repositioning done in ZmAppViewMgr.pushView
			      {type:_GROUP_, colSpan:1, width:"100%", colSizes:[100,"auto"],
				   items:[		
					 {ref: "name", type:_TEXTFIELD_, label: "List name", width:"100%"},
					 {type:_SPACER_, height:"3"},
					 {ref:"members", type:_DWT_LIST_, colSpan:"*", cssClass: "DLTarget"},
					 {type:_SPACER_, height:"5"},
					 {type:_GROUP_, colSpan:2, width:"100%", numCols:4, colSizes:["100%",85,5, 85], 
					     items:[
						    {type:_CELLSPACER_},
						    {type:_DWT_BUTTON_, label:"Remove All", width:80, relevant:"(form.getController().shouldEnableRemoveAllButton())",
							onActivate:"this.getFormController().removeAllMembers(event,this)",
							 relevantBehavior:_DISABLE_},
						    
						    {type:_CELLSPACER_},
						    {type:_DWT_BUTTON_, label:"Remove", width:80,
							onActivate:"this.getFormController().removeMembers(event,this)",
							relevant:"(form.getController().shouldEnableMemberListButtons())",
							relevantBehavior:_DISABLE_}
						   ]
					 }
					]
			      },
			      
			      {type:_CELLSPACER_, width:20},
			      
			      {type:_RADIO_GROUPER_, colSpan:1, numCols:2, colSizes:[50, "100%"], label:"Add Members to this list",
				  items:[			      
					 {type:_GROUP_, label:"Find:", colSpan:"*", numCols:2, colSizes:["70%","30%"],tableCssStyle:"width:100%", 
					     items:[
						    {type:_INPUT_, ref:"searchText", width:"100%",
							elementChanged: function(elementValue,instanceValue, event) {
							    var charCode = event.charCode;
							    if (charCode == 13 || charCode == 3) {
								this.getFormController().search();
							    } else {
								this.getForm().itemChanged(this, elementValue, event);
							    }
							}
						    },
						    {type:_DWT_BUTTON_, label:"Search", width:80,
							onActivate:"this.getFormController()._searchListener(event,this)"},
						   ]
					 },
					 {type:_SPACER_, height:"5"},
					 {ref:"memberPool", type:_DWT_LIST_, colSpan:"*", cssClass: "DLSource"},
					 {type:_SPACER_, height:"5"},
					 {type:_GROUP_, width:"100%", colSpan:"*", numCols:4, colSizes:[85,5,85,"100%"],
					     items: [
						     {type:_DWT_BUTTON_, label:"Add", width:80,
							 onActivate:"this.getFormController().addAddressToMembers(event, this)",
							 relevant:"(form.getController().shouldEnableMemberPoolListButtons())",
							 relevantBehavior:_DISABLE_},
						     {type:_CELLSPACER_},
						     {type:_DWT_BUTTON_, label:"Add All", width:80,
							 onActivate:"this.getFormController().addAllAddressesToMembers(event, this)",
							 relevant:"(form.getController().shouldEnableAddAllButton())",
							 relevantBehavior:_DISABLE_},
						     {type:_CELLSPACER_},
						    ]
					 },
					 
					 {type:_TOP_GROUPER_, label:"Or enter addresses below:", colSpan:"*", items:[]},
					 {ref:"optionalAdd", type:_TEXTAREA_, colSpan:"*", width:"100%", height:98},
					 {type:_SPACER_, height:"5"},
					 {type:_GROUP_, colSpan:"*", numCols:2, width:"100%", colSizes:[80,"100%"],
					     items: [
						     {type:_DWT_BUTTON_, label:"Add", width:"100%",
							 onActivate:"this.getFormController().addFreeFormAddressToMembers(event, this)",
							 relevant:"form.getController().shouldEnableFreeFormButtons()",
							 relevantBehavior:_DISABLE_
						     },
						     {type:_OUTPUT_, value:"Separate addresses with comma or return", align:"right"}
						    ]
					 },					 
					]
			      }
			     ]
		   },
		   {type:_CELLSPACER_, width:10 }
		  ]
	}
    }
    return this._newXform;
};

ZaDLController.prototype.removeMembers = function(event, formItem) {
	var form = formItem.getForm();
	var members = form.getInstance().getMembers();
	var membersSelection = this._getMemberSelection();
	for (var i = 0; i < membersSelection.length ; ++i) {
		members.remove(membersSelection[i]);
	}
	form.refresh();	
};

ZaDLController.prototype.removeAllMembers = function(event, formItem) {
	var form = formItem.getForm();
	form.getInstance().setMembers();
	form.refresh();
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

ZaDLController.prototype._getMemberPoolSelection = function () {
	if (this._dlView != null) {
		var memberPoolItem = this._dlView.getItemsById("memberPool")[0];
		return memberPoolItem.getSelection();
	}
	return [];
};

ZaDLController.prototype._getMemberSelection = function () {
	if (this._dlView != null) {
		var membersItem = this._dlView.getItemsById("members")[0];
		return membersItem.getSelection();
	} 
	return [];
};

ZaDLController.prototype.addAllAddressesToMembers = function (event, formItem) {
	var form = formItem.getForm();
	var instance = form.getInstance();
	var pool = instance.memberPool;
	var members = instance.getMembers();
	members.merge(members.size(), pool);
	form.refresh();
};

ZaDLController.prototype.addAddressToMembers = function (event, formItem) {
 	var form = formItem.getForm();
	var instance = formItem.getForm().getInstance();
	var members = instance.getMembers();
	var memberPoolSelection = this._getMemberPoolSelection();
	// get the current value of the textfied
	var val;
	if (memberPoolSelection != null) {
		for (var i = 0; i < memberPoolSelection.length; ++i) {
			members.add(memberPoolSelection[i]);
		}		
		form.refresh();	
	}
};
/**
 */
ZaDLController._IDS = 0;
ZaDLController.prototype.addFreeFormAddressToMembers = function (event, formItem) {
 	var form = formItem.getForm();
	var instance = formItem.getForm().getInstance();
	var members = instance.getMembers();
	// get the current value of the textfied
 	var val = form.get("optionalAdd");
 	var item = new String(val);
	item.id = "ZADLV_"+ ZaDLController._IDS++
 	//item.id = "id_" + val;
	members.add(item);
	instance.optionalAdd = null;
	form.refresh();
};

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
			if (!AjxUtil.isObject(arr[i])){
				tmp = new String(arr[i]);
			} else {
				tmp = arr[i];
			}
			if (tmp.id == null) {
				tmp.id = "ZADLV_"+ ZaDLController._IDS++;
			}
			tmpArr.push(tmp);
		}
		return tmpArr;
	},
	setMembersArray: function (value, instance, parentValue, ref) {
		instance.setMembers(value);
	},

	items: [
	{id: "name", type:_STRING_, setter:"setName", setterScope: _INSTANCE_},
	{id: "members", type:_LIST_, getter: "getMembersArray", getterScope:_MODEL_, setter: "setMembersArray", setterScope:_MODEL_},
	{id: "memberPool", type:_LIST_, setter:"setMemberPool", getterScope:_MODEL_},
	{id: "optionalAdd", type:_UNTYPED_},
	{id: "searchText", type:_STRING_}
	]
};
