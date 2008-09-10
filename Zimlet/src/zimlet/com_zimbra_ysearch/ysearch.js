/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
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

Com_Zimbra_Ysearch = function() {

};
Com_Zimbra_Ysearch.prototype = new ZmZimletBase;
Com_Zimbra_Ysearch.prototype.constructor = Com_Zimbra_Ysearch;

Com_Zimbra_Ysearch.prototype.toString =
function() {
	return "Com_Zimbra_Ysearch";
};

Com_Zimbra_Ysearch.prototype.init =
function() {
	if (!appCtxt.get(ZmSetting.WEB_SEARCH_ENABLED)) {
		return;
	}
	
	//grab some settings
	this._settingAutocomplete = (this.getUserProperty("autocomplete") === true || this.getUserProperty("autocomplete") == "true");
	this._settingPane = (this.getUserProperty("pane") === true || this.getUserProperty("pane") == "true");
	this._settingTab = (this.getUserProperty("tab") === true || this.getUserProperty("tab") == "true");
	this._settingNav = (this.getUserProperty("nav") === true || this.getUserProperty("nav") == "true");

	window.skin._searchWebController = new YahooSearchController(this);

	//override the skin's searchWeb function so we can send searches to the iframe pane
	if (this._settingPane) {
		window.skin.searchWeb = function(what) {
			//create an app tab for Search
			if (this._searchWebController._zimlet._settingTab && !appCtxt.getAppController().getAppChooser().getButton("searchZimlet")) {
				window.skin._createSearchTab();
			}
			
			document.getElementById('skin_search_web_input').value = "";
			this._searchWebController.setView();
			this._searchWebController.getSearchView().searchYahoo(what); //run the search
			
			//select the app tab for Search
			if (this._searchWebController._zimlet._settingTab && appCtxt.getAppController().getAppChooser().getButton("searchZimlet")) {
				appCtxt.getAppController().getAppChooser().setSelected("searchZimlet");
			}
		};
	}
	
	//override the skin's searchWebKey function so we can autocomplete searches
	if (this._settingAutocomplete) {
		window.skin.searchWebKey = function(event, field) {
			var el = document.getElementById('search_web_autocomplete_container');
			var input = document.getElementById('skin_search_web_input');
			//set the x,y & width of the autocomplete popup
			if (input.offsetParent) {
				var inputPos = this._searchWebController._zimlet._findPos(document.getElementById('skin_search_web_input'));
				el.style.left = inputPos[0];
				el.style.top = inputPos[1] + input.offsetHeight;
				el.style.width = input.offsetWidth + document.getElementById('skin_container_links').offsetWidth;
			}

			//send the query when enter is pressed and no item is currently selected in the autocomplete popup
			if (!window.skin.oAutoComp._oCurItem) {
				event = event || window.event;
				var code = event.keyCode;
				if (code == 13) {
					skin.searchWeb(field.value);
				}
			}
			return true;
		};

		var el = document.getElementById('z_shell').appendChild(document.createElement('div'));
		el.id = 'search_web_autocomplete_container';
		
		//open the autocompleted search result in either the iframe pane or a new window
		window.skin.searchItemSelected = function(type, args) {
			//create an app tab for Search
			if (window.skin._searchWebController._zimlet._settingTab && !appCtxt.getAppController().getAppChooser().getButton("searchZimlet")) {
				window.skin._createSearchTab();
			}

			//use Yahoo's ClickUrl for the search (args[2][2])
			if (window.skin._searchWebController._zimlet._settingPane) {
				document.getElementById('skin_search_web_input').value = "";
				if (document.getElementById('YahooSearchFrame')) {
					window.skin._searchWebController.showView();
				} else {
					window.skin._searchWebController.setView();
				}
				frames['YahooSearchFrame'].location.href = args[2][2];
				frames['YahooSearchFrame'].focus();
			} else {
				document.getElementById('skin_search_web_input').value = "";
				window.open(args[2][2]);
			}

			//select the app tab for Search
			if (appCtxt.getAppController().getAppChooser().getButton("searchZimlet")) {
				appCtxt.getAppController().getAppChooser().setSelected("searchZimlet");
			}
		};
		
		//create the autocomplete for Yahoo search
		window.skin.searchWebAutocomplete = new function() {
		    this.oACDS = new YAHOO.widget.DS_ScriptNode("http://search.yahooapis.com/WebSearchService/V1/webSearch?appid=zimbra&output=json&results=10&fr=zim-maila", ["ResultSet.Result", "Title", "Url", "ClickUrl"]);
		
			this.oAutoComp = new YAHOO.widget.AutoComplete("skin_search_web_input","search_web_autocomplete_container", this.oACDS);
			this.oAutoComp.animVert = false;
			this.oAutoComp.autoHighlight = false;
			this.oAutoComp.formatResult = function(oResultItem, sQuery) {
				return "<em>" + oResultItem[0] + "</em><br/>" + oResultItem[1];
			};
			this.oAutoComp.itemSelectEvent.subscribe(window.skin.searchItemSelected);
			window.skin.oAutoComp = this.oAutoComp;
		};
	}

	//create the app tab for Search
	if (this._settingTab) {
		ZmApp.ICON["searchZimlet"] = "Globe";
		ZmApp.NAME["searchZimlet"] = "search";
		ZmMsg["searchZimlet"] = this.getMessage("searchTab");
		ZmApp.CHOOSER_TOOLTIP["searchZimlet"] = "searchZimletTooltip";
		ZmMsg["searchZimletTooltip"] = this.getMessage("searchTabTooltip");

		window.skin._createSearchTab = function() {
			appCtxt.getAppController().getAppChooser()._createButton("searchZimlet", true);
			var b = appCtxt.getAppController().getAppChooser().getButton("searchZimlet");
			b.addSelectionListener(window.skin._searchTabListener);
		};
		
		window.skin._searchTabListener = function() {
			if (document.getElementById('YahooSearchFrame')) {
				window.skin._searchWebController.showView();
			} else {
				window.skin._searchWebController.setView();
				window.skin._searchWebController.getSearchView().searchYahoo();
			}
			appCtxt.getAppController().getAppChooser().setSelected("searchZimlet");
		};
	}
};

// Called by the Zimbra framework when the panel item was double clicked
Com_Zimbra_Ysearch.prototype.doubleClicked = function() {
	this.singleClicked();
};

// Called by the Zimbra framework when the panel item was clicked
Com_Zimbra_Ysearch.prototype.singleClicked = function() {
	//open a new window with the search if web searching is disabled or the search pane is disabled
	if (!appCtxt.get(ZmSetting.WEB_SEARCH_ENABLED) || !this._settingPane) {
		var searchUrl = ZmMsg["ysearchURL"];
		if(!searchUrl || searchUrl == "" || searchUrl == undefined){
			searchUrl = "http://search.yahoo.com";
		}
		searchUrl += '/?fr=zim-maila';

		window.open(searchUrl);
		return;
	}
	
	//create the Serach tab if needed
	if (this._settingTab && !appCtxt.getAppController().getAppChooser().getButton("searchZimlet")) {
		window.skin._createSearchTab();
	}

	//create or show the iframe search pane
	if (document.getElementById('YahooSearchFrame')) {
		window.skin._searchWebController.showView();
	} else {
		window.skin._searchWebController.setView();
		window.skin._searchWebController.getSearchView().searchYahoo();
	}
	
	//select the search tab
	if (this._settingTab && appCtxt.getAppController().getAppChooser().getButton("searchZimlet")) {
		appCtxt.getAppController().getAppChooser().setSelected("searchZimlet");
	}
};

Com_Zimbra_Ysearch.prototype.menuItemSelected =
function(itemId) {
	if (itemId == "prefs") {
		this.createPropertyEditor();
	}
};

//Finds x,y position of the object on the page
Com_Zimbra_Ysearch.prototype._findPos =
function(obj) {
	var curleft = curtop = 0;
	if (obj.offsetParent) {
		do {
			curleft += obj.offsetLeft;
			curtop += obj.offsetTop;
		} while (obj = obj.offsetParent);
		return [curleft,curtop];
	}
};

/**
 * YahooSearchController
 * @param zimlet
 */
YahooSearchController = function(zimlet) {

	if (arguments.length == 0) { return; }
	this._zimlet = zimlet;

	if (this._zimlet._settingPane) {
		ZmController.call(this, appCtxt.getShell());

		this._listeners = {};
		this._listeners[ZmOperation.CLOSE] = new AjxListener(this, this._cancelListener);
		this._listeners[ZmOperation.GO_TO_URL] = new AjxListener(this, this._urlListener);
		this._listeners[ZmOperation.PAGE_BACK] = new AjxListener(this, this._backListener);
		this._listeners[ZmOperation.PAGE_FORWARD] = new AjxListener(this, this._forwardListener);
		this._listeners[ZmOperation.SEARCH] = new AjxListener(this, this._searchListener);
	}
};

YahooSearchController.prototype = new ZmController;
YahooSearchController.prototype.constructor = YahooSearchController;

// View
ZmId.VIEW_YSEARCH = "YAHOOSEARCH";

YahooSearchController.prototype.getSearchView =
function() {
	if (!this._searchView) {
		this._searchView = new YahooSearch(appCtxt.getShell(), this);
	}
	return this._searchView;
};

YahooSearchController.prototype.setView =
function(params) {
	this._initializeToolBar();
	this._toolbar.enableAll(true);
	this._createSearchView(params); // YahooSearchView
	this.showView(params);
};

YahooSearchController.prototype.showView =
function(params) {
	appCtxt.getAppViewMgr().pushView(ZmId.VIEW_YSEARCH);
	// fit to container, since the height and width needs to be set for this view
	appCtxt.getAppViewMgr()._fitToContainer([ZmAppViewMgr.C_APP_CONTENT]);
};

YahooSearchController.prototype.hideView =
function() {
	appCtxt.getAppViewMgr().popView(true, ZmId.VIEW_YSEARCH);
};

YahooSearchController.prototype._createView =
function() {
	var elements = {};
	elements[ZmAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
	elements[ZmAppViewMgr.C_APP_CONTENT] = this._searchView;
	appCtxt.getAppViewMgr().createView(ZmId.VIEW_YSEARCH, null, elements);
};

YahooSearchController.prototype._createSearchView =
function(params) {
	if (this._searchView) { return; }

	// Creating Search View
	this.getSearchView();
	this._createView();
};

YahooSearchController.prototype._initializeToolBar =
function() {
	if (this._toolbar) { return; }

	if (this._zimlet._settingNav) {
		var buttons = [
			ZmOperation.PAGE_BACK,
			ZmOperation.PAGE_FORWARD,
			ZmOperation.GO_TO_URL,
			ZmOperation.SEARCH,
			ZmOperation.CLOSE
		];
	} else {
		var buttons = [
			ZmOperation.GO_TO_URL,
			ZmOperation.SEARCH,
			ZmOperation.CLOSE
		];
	}
	this._toolbar = new ZmButtonToolBar({parent:appCtxt.getShell(), buttons:buttons, className:"ZmAppToolBar ImgSkin_Toolbar"});

	// add listeners to the operations
	for (var i = 0; i < this._toolbar.opList.length; i++) {
		var button = this._toolbar.opList[i];

		if (button == ZmOperation.GO_TO_URL) {
			var b = this._toolbar.getOp(button);
			b.setText(this._zimlet.getMessage("goToUrl"));
			b.setToolTipContent(this._zimlet.getMessage("goToUrlTooltip"));
		} else if (button == ZmOperation.PAGE_BACK) {
			var b = this._toolbar.getOp(button);
			b.setToolTipContent(this._zimlet.getMessage("back"));
		} else if (button == ZmOperation.PAGE_FORWARD) {
			var b = this._toolbar.getOp(button);
			b.setToolTipContent(this._zimlet.getMessage("forward"));
		} else if (button == ZmOperation.SEARCH) {
			var b = this._toolbar.getOp(button);
			b.setText(this._zimlet.getMessage("search"));
			b.setToolTipContent(this._zimlet.getMessage("searchTooltip"));
		}
		
		if (this._listeners[button]) {
			this._toolbar.addSelectionListener(button, this._listeners[button]);
		}
	}
};

// Listeners

YahooSearchController.prototype._cancelListener =
function(ev) {
	this.hideView();
};

YahooSearchController.prototype._urlListener =
function(ev) {
	var editorProps = [
		{ label 		 : "URL",
		  name           : "url",
		  type           : "string",
		  value          : "http://",
		  minLength      : 4,
		  maxLength      : 100
		}
	];
	var view = new DwtComposite(this._zimlet.getShell());
	var pe = new DwtPropertyEditor(view, true);
	pe.initProperties(editorProps);
	var dialog_args = {
		title : this._zimlet.getMessage("urlDialog"),
		view  : view
	};
	var dlg = this._zimlet._createDialog(dialog_args);
	pe.setFixedLabelWidth();
	pe.setFixedFieldWidth();
	dlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this._zimlet, function() {
		dlg.popdown();
		frames['YahooSearchFrame'].location.href = pe.getProperties().url; //search
		dlg.dispose();
		dlg = null;
	}));
	dlg.popup();
};

YahooSearchController.prototype._searchListener =
function(ev) {
	var editorProps = [
		{ label 		 : "Search",
		  name           : "search",
		  type           : "string",
		  value          : "",
		  minLength      : 1,
		  maxLength      : 100
		}
	];
	var view = new DwtComposite(this._zimlet.getShell());
	var pe = new DwtPropertyEditor(view, true);
	pe.initProperties(editorProps);
	var dialog_args = {
		title : this._zimlet.getMessage("searchDialog"),
		view  : view
	};
	var dlg = this._zimlet._createDialog(dialog_args);
	var controller = this;
	pe.setFixedLabelWidth();
	pe.setFixedFieldWidth();
	dlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this._zimlet, function() {
		dlg.popdown();
		controller.getSearchView().searchYahoo(pe.getProperties().search); //search
		dlg.dispose();
		dlg = null;
	}));
	dlg.popup();
};

YahooSearchController.prototype._backListener =
function(ev) {
	frames['YahooSearchFrame'].history.go(-1);
};

YahooSearchController.prototype._forwardListener =
function(ev) {
	frames['YahooSearchFrame'].history.go(1);
};
