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
 * @class
 * @constructor
 * DwtTabView  - class for the tabbed view
 * DwtTabView manages the z-index of the contained tabs. 
 * 
 * @param params		[hash]				hash of params:
 *        parent		[DwtComposite] 		parent widget
 *        className		[string]*			CSS class
 *        posStyle		[constant]*			positioning style
 *        id			[string]*			an explicit ID to use for the control's HTML element
 * 
 * @author Greg Solovyev
 */
DwtTabView = function(params) {
	if (arguments.length == 0) return;
	params = Dwt.getParams(arguments, DwtListView.PARAMS);
	params.className = params.className || "ZTabView";
	params.posStyle = params.posStyle || DwtControl.ABSOLUTE_STYLE;
	DwtComposite.call(this, params);

	this._stateChangeEv = new DwtEvent(true);
	this._tabs = [];
	this._tabIx = 1;
    this._createHtml();

	var tabGroupId = [this.toString(), this._htmlElId].join("-")
	this._tabGroup = new DwtTabGroup(tabGroupId);
	this._tabGroup.addMember(this._tabBar);
};

DwtTabView.PARAMS = ["parent", "className", "posStyle"];

DwtTabView.prototype = new DwtComposite;
DwtTabView.prototype.constructor = DwtTabView;


// Constants

// Z-index consts for tabbed view contents are based on Dwt z-index consts
DwtTabView.Z_ACTIVE_TAB = Dwt.Z_VIEW+10;
DwtTabView.Z_HIDDEN_TAB = Dwt.Z_HIDDEN;
DwtTabView.Z_TAB_PANEL 	= Dwt.Z_VIEW+20;
DwtTabView.Z_CURTAIN 	= Dwt.Z_CURTAIN;

DwtTabView.prototype.TEMPLATE = "dwt.Widgets#ZTabView";


// Public methods

DwtTabView.prototype.toString =
function() {
	return "DwtTabView";
};

DwtTabView.prototype.addStateChangeListener =
function(listener) {
	this._eventMgr.addListener(DwtEvent.STATE_CHANGE, listener);
};

DwtTabView.prototype.removeStateChangeListener =
function(listener) {
	this._eventMgr.removeListener(DwtEvent.STATE_CHANGE, listener);
};

DwtTabView.prototype.getTabGroupMember = function() {
	return this._tabGroup;
};

/**
* @param title  -  text for the tab button
* @param tabView - instance of DwtTabViewPage or an AjxCallback that
*                  returns an instance of DwtTabViewPage
* @return - the key for the added tab. This key can be used to retreive the tab using @link getTab
* public method addTab. Note that this method does not automatically update the tabs panel.
**/
DwtTabView.prototype.addTab =
function (title, tabViewOrCallback, buttonId) {
	var tabKey = this._tabIx++;	

	// create tab entry
	this._tabs[tabKey] = {
		title: title,
		button: this._tabBar.addButton(tabKey, title, buttonId)
	};

	// add the page
	this.setTabView(tabKey, tabViewOrCallback);

	// show the first tab
	if (tabKey==1) {
		if (tabViewOrCallback instanceof AjxCallback) {
			tabViewOrCallback = tabViewOrCallback.run(tabKey);
		}
		if(tabViewOrCallback) {
			tabViewOrCallback.showMe();
		}
		this._currentTabKey = tabKey;		
		this.switchToTab(tabKey);
	}
	// hide all the other tabs
	else if (tabViewOrCallback && !(tabViewOrCallback instanceof AjxCallback)) {
		tabViewOrCallback.hideMe();
		Dwt.setVisible(tabViewOrCallback.getHtmlElement(), false);
	}

	this._tabBar.addSelectionListener(tabKey, new AjxListener(this, DwtTabView.prototype._tabButtonListener));

	return tabKey;
};

DwtTabView.prototype.enable =
function(enable) {
	for (var i in this._tabs) {
		var button = this._tabs[i].button;
		if (button) {
			button.setEnabled(enable);
		}
	}
};

DwtTabView.prototype.getCurrentTab =
function() {
	return this._currentTabKey;
};

DwtTabView.prototype.getNumTabs =
function() {
	return (this._tabs.length - 1);
};

/**
* @param tabKey  -  key for the tab, returned from @link addTab
* @return - the view tab (DwtTabViewpage) 
**/
DwtTabView.prototype.getTab =
function (tabKey) {
	return (this._tabs && this._tabs[tabKey])
		? this._tabs[tabKey]
		: null;
};

DwtTabView.prototype.getTabTitle =
function(tabKey) {
	return (this._tabs && this._tabs[tabKey])
		? this._tabs[tabKey]["title"]
		: null;
};

DwtTabView.prototype.getTabButton =
function(tabKey) {
	return (this._tabs && this._tabs[tabKey])
		? this._tabs[tabKey]["button"]
		: null;
};

DwtTabView.prototype.setTabView =
function(tabKey, tabView) {
	var tab = this.getTab(tabKey);
	tab.view = tabView;
	if (tabView && !(tabView instanceof AjxCallback)) {
		this._pageEl.appendChild(tabView.getHtmlElement());
		tabView._tabKey = tabKey;
		if (tabKey == this._currentTabKey) {
			var tabGroup = tabView.getTabGroupMember();
			this._tabGroup.replaceMember(tab.tabGroup, tabGroup);
			tab.tabGroup = tabGroup;
		}
	}
};

DwtTabView.prototype.getTabView =
function(tabKey) {
	var tab = this.getTab(tabKey);
	var tabView = tab && tab.view;
	if (tabView instanceof AjxCallback) {
		var callback = tabView;
		tabView = callback.run(tabKey);
		this.setTabView(tabKey, tabView);
		var size = this._getTabSize();
		tabView.setSize(size.x, size.y);
	}
	return tabView;
};

DwtTabView.prototype.switchToTab = 
function(tabKey) {
	var ntab = this.getTab(tabKey);
	if(ntab) {
		// remove old tab from tab-group
		var otab = this.getTab(this._currentTabKey);
		if (otab) {
			this._tabGroup.removeMember(otab.tabGroup);
		}
		// switch tab
		this._showTab(tabKey);
		this._tabBar.openTab(tabKey);
		// add new tab to tab-group
		if (!ntab.tabGroup && ntab.view) {
			ntab.tabGroup = ntab.view.getTabGroupMember();
		}
		this._tabGroup.addMember(ntab.tabGroup);
		// notify change
		if (this._eventMgr.isListenerRegistered(DwtEvent.STATE_CHANGE)) {
			this._eventMgr.notifyListeners(DwtEvent.STATE_CHANGE, this._stateChangeEv);
		}
	}
};

DwtTabView.prototype.setBounds =
function(x, y, width, height) {
	DwtComposite.prototype.setBounds.call(this, x, y, width, height);
	this._resetTabSizes(width, height);
};

DwtTabView.prototype.getActiveView =
function() {
	return this._tabs[this._currentTabKey].view;
};

DwtTabView.prototype.getKeyMapName =
function() {
	return "DwtTabView";
};

DwtTabView.prototype.resetKeyBindings =
function() {
	var kbm = this.shell.getKeyboardMgr();
	if (kbm.isEnabled()) {
		var kmm = kbm.__keyMapMgr;
		if (!kmm) { return; }
		var num = this.getNumTabs();
		var seqs = kmm.getKeySequences("DwtTabView", "GoToTab");
		for (var k = 0; k < seqs.length; k++) {
			var ks = seqs[k];
			for (var i = 1; i <= num; i++) {
				var newKs = ks.replace(/NNN/, i);
				kmm.setMapping("DwtTabView", newKs, "GoToTab" + i);
			}
		}
		kmm.reloadMap("DwtTabView");
	}
};

DwtTabView.prototype.handleKeyAction =
function(actionCode) {
	DBG.println(AjxDebug.DBG3, "DwtTabView.handleKeyAction");

	switch (actionCode) {

		case DwtKeyMap.NEXT_TAB:
			var curTab = this.getCurrentTab();
			if (curTab < this.getNumTabs()) {
				this.switchToTab(curTab + 1);
			}
			break;
			
		case DwtKeyMap.PREV_TAB:
			var curTab = this.getCurrentTab();
			if (curTab > 1) {
				this.switchToTab(curTab - 1);
			}
			break;
		
		default:
			// Handle action code like "GoToTab3"
			var m = actionCode.match(DwtKeyMap.GOTO_TAB_RE);
			if (m && m.length) {
				var idx = m[1];
				if ((idx <= this.getNumTabs()) && (idx != this.getCurrentTab())) {
					this.switchToTab(idx);
				}
			} else {
				return false;
			}
	}
	return true;
};


// Protected methods

DwtTabView.prototype._resetTabSizes =
function (width, height) {
	if (this._tabs && this._tabs.length) {
		for (var curTabKey in this._tabs) {
			var tabView = this._tabs[curTabKey].view;
			if (tabView && !(tabView instanceof AjxCallback)) {
				tabView.resetSize(width, height);
			}	
		}
	}		
};

DwtTabView.prototype._getTabSize =
function() {
	var size = this.getSize();
	var width = size.x || this.getHtmlElement().clientWidth;
	var height = size.y || this.getHtmlElement().clientHeight;
	var tabBarSize = this._tabBar.getSize();
	var tabBarHeight = tabBarSize.y || this._tabBar.getHtmlElement().clientHeight;

	return new DwtPoint(width, (height - tabBarHeight));
};

DwtTabView.prototype._createHtml =
function(templateId) {
    this._createHtmlFromTemplate(templateId || this.TEMPLATE, {id:this._htmlElId});
};

DwtTabView.prototype._createHtmlFromTemplate =
function(templateId, data) {
    DwtComposite.prototype._createHtmlFromTemplate.call(this, templateId, data);

    this._tabBarEl = document.getElementById(data.id+"_tabbar");
    this._tabBar = new DwtTabBar(this);
    this._tabBar.reparentHtmlElement(this._tabBarEl);
    this._pageEl = document.getElementById(data.id+"_page");
};

DwtTabView.prototype._showTab = 
function(tabKey) {
	if (this._tabs && this._tabs[tabKey]) {
		this._currentTabKey = tabKey;
		this._hideAllTabs();						// hide all the tabs
		var tabView = this.getTabView(tabKey);		// make this tab visible
        if (tabView) {
            this.applyCaretHack();
			tabView.setVisible(true);
            tabView.showMe();
        }
    }
};

DwtTabView.prototype._hideAllTabs = 
function() {
	if (this._tabs && this._tabs.length) {
		for (var curTabKey in this._tabs) {
			var tabView = this._tabs[curTabKey].view;
			if (tabView && !(tabView instanceof AjxCallback)) {
				tabView.hideMe();
				//this._tabs[curTabKey]["view"].setZIndex(DwtTabView.Z_HIDDEN_TAB);
				Dwt.setVisible(tabView.getHtmlElement(), false);
			}	
		}
	}
};

DwtTabView.prototype._tabButtonListener = 
function (ev) {
    this.switchToTab(ev.item.getData("tabKey"));
};


//
// Class
//

/**
* @class
* @constructor
* DwtTabViewPage abstract class for a page in a tabbed view
* tab pages are responsible for creating there own HTML and populating/collecting 
* data to/from any form fields that they display
**/
DwtTabViewPage = function(parent, className, posStyle) {
	if (arguments.length == 0) return;

	var clsName = className || "ZTabPage";
	var ps = posStyle || DwtControl.ABSOLUTE_STYLE;
	this._rendered = true; // by default UI creation is not lazy

	DwtPropertyPage.call(this, parent, clsName, ps);

    this._createHtml();
};

DwtTabViewPage.prototype = new DwtPropertyPage;
DwtTabViewPage.prototype.constructor = DwtTabViewPage;

DwtTabViewPage.prototype.TEMPLATE = "dwt.Widgets#ZTabPage";


// Public methods

DwtTabViewPage.prototype.toString =
function() {
	return "DwtTabViewPage";
};

DwtTabViewPage.prototype.getContentHtmlElement =
function() {
    return this._contentEl || this.getHtmlElement();
};

DwtTabViewPage.prototype.showMe =
function() {
	this.setZIndex(DwtTabView.Z_ACTIVE_TAB);
	if (this.parent.getHtmlElement().offsetHeight > 80) { 						// if parent visible, use offsetHeight
		this._contentEl.style.height=this.parent.getHtmlElement().offsetHeight-80;
	} else {
		var parentHeight = parseInt(this.parent.getHtmlElement().style.height);	// if parent not visible, resize page to fit parent
		var units = AjxStringUtil.getUnitsFromSizeString(this.parent.getHtmlElement().style.height);
		if (parentHeight > 80) {
			this._contentEl.style.height = (Number(parentHeight-80).toString() + units);
		}
	}

	if (this.parent.getHtmlElement().offsetWidth > 0) { 						// if parent visible, use offsetWidth
		this._contentEl.style.width=this.parent.getHtmlElement().offsetWidth;
	} else {
		this._contentEl.style.width = this.parent.getHtmlElement().style.width;	//if parent not visible, resize page to fit parent
	}
};

DwtTabViewPage.prototype.hideMe = 
function() {
	this.setZIndex(DwtTabView.Z_HIDDEN_TAB);
};

DwtTabViewPage.prototype.resetSize =
function(newWidth, newHeight) {
	if (this._rendered) {
		this.setSize(newWidth, newHeight);
	}
};


// Protected methods

DwtTabViewPage.prototype._createHtml =
function(templateId) {
    this._createHtmlFromTemplate(templateId || this.TEMPLATE, {id:this._htmlElId});
};

DwtTabViewPage.prototype._createHtmlFromTemplate =
function(templateId, data) {
    DwtPropertyPage.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._contentEl = document.getElementById(data.id+"_content") || this.getHtmlElement();
};


//
// Class
//

/**
* @class
* @constructor
* @param parent
* DwtTabBar 
**/
DwtTabBar = function(parent, tabCssClass, btnCssClass) {
	if (arguments.length == 0) return;

	this._buttons = [];
	this._btnStyle = btnCssClass || "ZTab"; 									// REVISIT: not used
	this._btnImage = null;
	this._currentTabKey = 1;
	var myClass = tabCssClass || "ZTabBar";

	DwtToolBar.call(this, {parent:parent, className:myClass, posStyle:DwtControl.STATIC_STYLE});
};

DwtTabBar.prototype = new DwtToolBar;
DwtTabBar.prototype.constructor = DwtTabBar;


// Constants

DwtTabBar.prototype.TEMPLATE = "dwt.Widgets#ZTabBar";


// Public methods

DwtTabBar.prototype.toString =
function() {
	return "DwtTabBar";
};

DwtTabBar.prototype.getCurrentTab =
function() {
	return this._currentTabKey;
};

DwtTabBar.prototype.addStateChangeListener =
function(listener) {
	this._eventMgr.addListener(DwtEvent.STATE_CHANGE, listener);
};

DwtTabBar.prototype.removeStateChangeListener = 
function(listener) {
	this._eventMgr.removeListener(DwtEvent.STATE_CHANGE, listener);
};

/**
* @param tabId - the id used to create tab button in @link DwtTabBar.addButton method
* @param listener - AjxListener
**/
DwtTabBar.prototype.addSelectionListener =
function(tabKey, listener) {
	this._buttons[tabKey].addSelectionListener(listener);
};

/**
* @param tabId - the id used to create tab button in @link DwtTabBar.addButton method
* @param listener - AjxListener
**/
DwtTabBar.prototype.removeSelectionListener =
function(tabKey, listener) {
	this._buttons[tabKey].removeSelectionListener(listener);
};

/**
* @param tabKey
* @param tabTitle
**/
DwtTabBar.prototype.addButton =
function(tabKey, tabTitle, id) {
	var b = this._buttons[tabKey] = new DwtTabButton(this, id);
	
	this._buttons[tabKey].addSelectionListener(new AjxListener(this, DwtTabBar._setActiveTab));

	if (this._btnImage != null) {
		b.setImage(this._btnImage);
	}

	if (tabTitle != null) {
		b.setText(tabTitle);
	}

	b.setEnabled(true);
	b.setData("tabKey", tabKey);

	if (parseInt(tabKey) == 1) {
		this.openTab(tabKey, true);
	}

	// make sure that new button is selected properly
    var sindex = this.__getButtonIndex(this._currentTabKey);
    if (sindex != -1) {
        var nindex = this.__getButtonIndex(tabKey);
        if (nindex == sindex + 1) {
            Dwt.addClass(b.getHtmlElement(), DwtTabBar.SELECTED_NEXT);
        }
    }

    return b;
};

/**
* @param tabKey
**/
DwtTabBar.prototype.getButton = 
function (tabKey) {
	return (this._buttons[tabKey])
		? this._buttons[tabKey]
		: null;
};

DwtTabBar.prototype.openTab = 
function(tabK, skipNotify) {
	this._currentTabKey = tabK;
    var cnt = this._buttons.length;

    for (var ix = 0; ix < cnt; ix ++) {
		if (ix==tabK) { continue; }

        var button = this._buttons[ix];
        if (button) {
            this.__markPrevNext(ix, false);
            button.setClosed();
        }
    }

    var button = this._buttons[tabK];
    if (button) {
		button.setOpen();
        this.__markPrevNext(tabK, true);
    }

	if (!skipNotify && this._eventMgr.isListenerRegistered(DwtEvent.STATE_CHANGE)) {
		this._eventMgr.notifyListeners(DwtEvent.STATE_CHANGE, this._stateChangeEv);
	}
};

/**
* Greg Solovyev 1/4/2005 
* changed ev.target.offsetParent.offsetParent to
* lookup for the table up the elements stack, because the mouse down event may come from the img elements 
* as well as from the td elements.
**/
DwtTabBar._setActiveTab =
function(ev) {
    var tabK;
    if (ev && ev.item) {
		tabK=ev.item.getData("tabKey");
    } else if (ev && ev.target) {
		var elem = ev.target;
	    while (elem.tagName != "TABLE" && elem.offsetParent )
	    	elem = elem.offsetParent;

		tabK = elem.getAttribute("tabKey");
		if (tabK == null)
			return false;
    } else {
		return false;
    }
    this.openTab(tabK);
};


//
// Class
//

/**
* @class
* @constructor
**/
DwtTabButton = function(parent, id) {
	DwtButton.call(this, {parent:parent, className:"ZTab", id:id});
};

DwtTabButton.prototype = new DwtButton;
DwtTabButton.prototype.constructor = DwtTabButton;

DwtTabButton.prototype.TEMPLATE = "dwt.Widgets#ZTab";


// Public methods

DwtTabButton.prototype.toString =
function() {
	return "DwtTabButton";
};

/**
* Changes the visual appearance to active tab and sets _isClosed to false
**/
DwtTabButton.prototype.setOpen = 
function() {
    this._isSelected = true;
    this.setDisplayState(DwtControl.SELECTED);
};

/**
* Changes the visual appearance to inactive tab and sets _isClosed to true
**/
DwtTabButton.prototype.setClosed = 
function() {
    this._isSelected = false;
    this.setDisplayState(DwtControl.NORMAL);
};

DwtTabButton.prototype.setDisplayState = function(state) {
    if (this._isSelected && state != DwtControl.SELECTED) {
        state = [ DwtControl.SELECTED, state ].join(" ");
    }
    DwtButton.prototype.setDisplayState.call(this, state);
};
