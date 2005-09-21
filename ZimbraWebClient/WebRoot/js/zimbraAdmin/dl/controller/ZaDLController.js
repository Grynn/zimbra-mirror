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
	this._views = {};
}

ZaDLController.prototype = new ZaController();
ZaDLController.prototype.constructor = ZaDLController;

ZaDLController.prototype.toString = function () {
	return "ZaDLController";
};

ZaDLController.ALL_DL_VIEW = "ZaDLController.ALL_DISTRIBUTION_LIST_VIEW";
ZaDLController.NEW_DL_VIEW = "ZaDLController.NEW_DISTRIBUTION_LIST_VIEW";
ZaDLController.EDIT_DL_VIEW = "ZaDLController.EDIT_DISTRIBUTION_LIST_VIEW";
ZaDLController._validViewIds = {};
ZaDLController._validViewIds[ZaDLController.ALL_DL_VIEW] = true;
ZaDLController._validViewIds[ZaDLController.NEW_DL_VIEW] = true;
ZaDLController._validViewIds[ZaDLController.EDIT_DL_VIEW] = true;

//===============================================================
// initialization methods
//===============================================================
ZaDLController.prototype._createListeners = function () {
	this._listeners = {};
	this._listeners[ZaOperation.SAVE] = new AjxListener(this, this._saveListener);
	this._listeners[ZaOperation.NEW] = new AjxListener(this, this._newListener);
	this._listeners[ZaOperation.EDIT] = new AjxListener(this, this._editListener);
	this._listeners[ZaOperation.DELETE] = new AjxListener(this, this._deleteListener);
	this._listeners[ZaOperation.CLOSE] = new AjxListener(this, this._cancelNewListener);
};

ZaDLController.prototype._createToolbars = function () {
	this._toolbars = {};
	var ops = [
			   new ZaOperation(ZaOperation.SAVE, ZaMsg.TBB_Save, ZaMsg.ALTBB_Save_tt, "Save", "SaveDis",
							   this._listeners[ZaOperation.SAVE]),
			   new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Cancel, ZaMsg.DLTBB_Cancel_tt, "Close", "CloseDis",
							   this._listeners[ZaOperation.CLOSE])
			   ];
	this._toolbars[ZaDLController.NEW_DL_VIEW] = this._toolbars[ZaDLController.EDIT_DL_VIEW] = new ZaToolBar(this._container, ops);

	ops = [
		   new ZaOperation(ZaOperation.NEW, ZaMsg.TBB_New, ZaMsg.DLTBB_New_tt, "New", "NewDis", this._listeners[ZaOperation.NEW]),
		   new ZaOperation(ZaOperation.EDIT, ZaMsg.TBB_Edit, ZaMsg.DLTBB_Edit_tt, "Edit", "EditDis", this._listeners[ZaOperation.EDIT]),
		   new ZaOperation(ZaOperation.DELETE, ZaMsg.TBB_Delete, ZaMsg.DLTBB_Delete_tt, "Delete", "DeleteDis", this._listeners[ZaOperation.DELETE])
		   ];

	this._toolbars[ZaDLController.ALL_DL_VIEW] = new ZaToolBar(this._container, ops);
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
ZaDLController.prototype.show = function(searchResult) {
	// currently we're showing the new screen, but it should jsut show a listing of all
	// distribution lists for the given domain.
	var searchResultArray = null;
	if (searchResult && searchResult.list != null) {
		searchResultArray = new Array()
		var arr = searchResult.list.getArray();
		var cnt = arr.length;
		for(var ix = 0; ix < cnt; ix++) {
			searchResultArray.push(arr[ix]);
		}
	}
	
	//searchResultArray = ["fuddy-duddy@zimbra.com","ui-team@zimbra.com", "culolulo@zimbra.com", "HackWilson@zimbra.com"];
	//this._setView(ZaDLController.NEW_DL_VIEW, searchResultArray);
	this._setView(ZaDLController.ALL_DL_VIEW, searchResultArray);
};

ZaDLController.prototype.getAllDistributionLists = function () {
	var soapDoc = AjxSoapDoc.create("GetAllDistributionListsRequest", "urn:zimbraAdmin", null);
	soapDoc.setMethodAttribute("domain", this._domain);
	soapDoc.setMethodAttribute("limit", "25");
	var resp = ZmCsfeCommand.invoke(soapDoc, null, null, null, false);
	// 	this.initFromDom(resp.firstChild);
};

ZaDLController.prototype._updateOperations = 
function (optionalOps, optionalDisOps) {
	var disOps = (optionalDisOps != null)? optionalDisOps : null;
	var ops = (optionalOps != null)? optionalOps : null;
	// if both ops, and disOps have not been specified, use our standard rules
	if (ops == null && disOps == null) {
		switch (this._currentViewId) {
			
		case ZaDLController.ALL_DL_VIEW:
			var cnt = this._views[this._currentViewId].getSelectionCount();
		
			if (cnt < 1) {
				disOps = [ZaOperation.EDIT, ZaOperation.DELETE];
				ops = [ZaOperation.NEW];
			} else if (cnt > 1) {
				ops = [ZaOperation.NEW, ZaOperation.DELETE];
				disOps = [ZaOperation.EDIT];
			} else {
				ops = [ZaOperation.NEW, ZaOperation.EDIT, ZaOperation.DELETE];
				disOps = null;
			}
			break;
		case ZaDLController.EDIT_DL_VIEW:
			// no break
		case ZaDLController.NEW_DL_VIEW:
			ops = [ZaOperation.CLOSE];
			disOps = [ZaOperation.SAVE];
			break;
		}
	}
	if (disOps != null) {
		this._toolbars[this._currentViewId].enable(disOps, false);
	} 
	if (ops != null) {
		this._toolbars[this._currentViewId].enable(ops, true);
	}
};

//===============================================================
// view accessor methods
//===============================================================
ZaDLController.prototype._isValidView = function (id) {
	return (ZaDLController._validViewIds[id] != null);
};

/** 
 * view factory method
 */
ZaDLController.prototype._getView = function (id, args) {
	if (this._isValidView(id)){
		var view = this._views[id];
		var toolbar = this._toolbars[id];
		if (view == null) {
			switch (id) {
			case ZaDLController.EDIT_DL_VIEW:
				// no break
				// args should be a distribution list
				args.editMode = true;
			case ZaDLController.NEW_DL_VIEW:
				var xModelObj = new XModel(ZaDLController.distributionListXModel);
				args.editMode = false;
				view = new XForm(this._getNewViewXForm(), xModelObj, args, this._container);
				var ls = new AjxListener(this, this._itemUpdatedListener);
				view.addListener(DwtEvent.XFORMS_VALUE_CHANGED, ls);
				view.setController(this);
				view.draw();
				view.getHtmlElement().style.position = "absolute";

// 				ZaDLController.memberPoolChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST, "name", "name");
// 				ZaDLController.memberChoices = new XFormChoices([], XFormChoices.SIMPLE_LIST, "name", "name");
				view.setData = function (dl) {
					// data is probably a ZaItem that doesn't have dl details
					dl.getMembers();
					//					ZaDLController.memberList.setChoices(dl.getMembersArray());
					view.setInstance(dl);
				};
				this._views[id] = view;
				this._dlView = view;
				break;
			case ZaDLController.ALL_DL_VIEW:
				// get the distribution lists, and render them
				// toolbar should have new, edit, delete, back and forward 
				//this.getAllDistributionLists();
				args = this.getDummyDistributionLists();
				view = new ZaDLListView(this._container, this._app);
				view.addSelectionListener(new AjxListener(this, this._listSelectionListener));
				this._views[id] = view;
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
	}
	return null;
};

ZaDLController.prototype._setView = function (id, args) {
	// get the view from our stash
	var elements = this._getView(id, args);
	// get the app container from the app view manager
	var view = this._app.getAppViewMgr()._views[id];
	// if the app container from the app view manager is null,
	// create the app container
	if (view == null) {
		var popCallback = new AjxCallback(this, this._popViewCallback);
		//this._app.getAppViewMgr().createView(id, this._app.getName(), [this._toolbar, newView], staleCallback);
		this._app.createView(id, elements, popCallback);
	}
	// if we are not the view currently visible,
	// then push our view onto the app view stack
	if (id != this._currentViewId) {
		this._app.pushView(id);
	}
	// set our current view tracker.
	this._currentViewId = id;
	this._updateOperations();
};

ZaDLController.prototype._popViewCallback = function () {
	this._currentViewId = null;
	return true;
};

ZaController.prototype.getControllerForView = function( viewId ) {
	return this;
};

//===============================================================
// button listener methods
//===============================================================
ZaDLController.prototype._saveListener = function (ev) {
	DBG.println("SAVE NOT IMPLEMENTED");
//  	var dl = this._views[ZaDLController.NEW_DL_VIEW].getInstance();
//  	dl.save();
};

ZaDLController.prototype._editListener = function (ev) {
	var selection = this._views[this._currentViewId].getSelection()[0];
	this._setView(ZaDLController.NEW_DL_VIEW, selection);
};

ZaDLController.prototype._deleteListener = function (ev) {
	DBG.println("DLController._deleteListener not implemented");
};

ZaDLController.prototype._cancelNewListener = function (ev) {
	// Cancel is only on the new screen, so go back to the list of
	// distribution lists
	//this._setView(ZaDLController.ALL_DL_VIEW);
	this._app.getAccountListController().show();
};

ZaDLController.prototype._newListener = function (ev) {
	this._setView(ZaDLController.NEW_DL_VIEW, null);
};

ZaDLController.prototype._listSelectionListener = function(ev) {
	if (ev.detail == DwtListView.ITEM_DBL_CLICKED) {
		if(ev.item) {
			this._setView(ZaDLController.NEW_DL_VIEW, ev.item);
		}
	} else {
		this._updateOperations();
	}
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
	this._views[this._currentViewId].getInstance().memberPool = tmpArr;
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
		this._memberPoolSelection = null;
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
 * Highly suspect function. The DwtAddRemove widget, doesn't really play the 
 * XForm game very well -- it doesn't just listen to model changes. It expects
 * the caller to poke the view with new data, at which time, the model gets changed
 * as well. Here, we're trying to get the data from the optional add box, to put
 * it into the target list.
 */
ZaDLController.prototype.addFreeFormAddressToMembers = function (event, formItem) {
 	var form = formItem.getForm();
	var instance = formItem.getForm().getInstance();
	var members = instance.getMembers();
	// get the current value of the textfied
 	var val = form.get("optionalAdd");
 	var item = new String(val);
 	item.id = "id_" + val;
	members.add(item);
	instance.optionalAdd = null;
	form.refresh();
};

ZaDLController._IDS = 0;
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
	{id: "name", type:_STRING_},
	{id: "members", type:_LIST_, getter: "getMembersArray", getterScope:_MODEL_, setter: "setMembersArray", setterScope:_MODEL_},
	{id: "memberPool", type:_LIST_, setter:"setMemberPool", getterScope:_MODEL_},
	{id: "optionalAdd", type:_UNTYPED_},
	{id: "searchText", type:_STRING_}
	]
};

dummyInstance = {
	list:[],
	list2:[],
	junk:"junk",
	optionalAdd:"EMPTY"
};
