/**
 * The container for all the Application tabs. It will include all the operations at the
 * tab group level, including add a tab, remove a tab, switch tab, move tab position, 
 * resize tabs, 
 * 
 * 
*/

function ZaAppTabGroup (parent, app, parentElId) {
	if (arguments.length == 0) return;
	DwtComposite.call(this, parent, "ZaAppTabGroup", Dwt.ABSOLUTE_STYLE);	
	this._created = false ;
	this._app = app ;
	this._mainTab = null;
	this._currentTab = null ;
	
	this._createUI(parentElId) ;
}

ZaAppTabGroup.prototype = new DwtComposite();
ZaAppTabGroup.prototype.constructor = ZaAppTabGroup;

//Global Varible to keep all the tab instances
ZaAppTabGroup._TABS = new AjxVector() ;

ZaAppTabGroup.prototype._createUI =
function (parentElId) {
	if (this._created) {
		return ;
	}else {
		if (parentElId) {
			this.reparentHtmlElement ( parentElId );
		}
		
		//create the main tab
		var tabParams = {
			closable: false,
			selected: true
		}
		//this._mainTab = new ZaAppTab (this, this._app, null, null,	null, null, false, true);
		this._mainTab = new ZaAppTab (this, this._app, tabParams);
		this._currentTab = this._mainTab ;
		/*
		if (mainTab) {
			this.addTab(mainTab, false);
		}*/
			
		this._created = true ;
		this._app.setTabGroup (this) ;
	}
}

//TODO this method is called when the browser is resized.
//Need to handle the tab size properly. And  may need to change the label accordingly
ZaAppTabGroup.prototype.resetTabSizes =
function (){
	var tabW = this.getTabWidth();
	var tabH = this.getTabHeight() ;
	var nextX = 0;
	
	if (AjxEnv.isIE) {
		var y = -6 ;
	}else{
		var y = -9 ;
	}
	for (var i=0; i < ZaAppTabGroup._TABS.size(); i++) {
		var cTab = ZaAppTabGroup._TABS.get(i) ;
		var w = tabW ;
		if (cTab._closable) {
			w += 20 ;
		}
		cTab.setBounds (nextX, y, w, tabH) ; 
		
		cTab.resetLabel ();
		
		//7 is left-margin (3) + left-border (2) + right-border (2)
		nextX = nextX + w + 7;
	}
}

ZaAppTabGroup.prototype.getTabWidth =
function () {
	var tabWidth ;
	var tabMinWidth = 50;
	var tabMaxWidth = ZaAppTab.DEFAULT_WIDTH;
	var groupWidth = this.getW () ;
	
	if (groupWidth > 0) {
		if (groupWidth > tabMinWidth) {
			var numOfTabs = ZaAppTabGroup._TABS.size() ;
			var avgTabWidth = groupWidth / (numOfTabs <= 0 ? 1 : numOfTabs) ;
			if (avgTabWidth >= tabMinWidth && avgTabWidth <= tabMaxWidth) {
				tabWidth = avgTabWidth ;
			}else if (avgTabWidth > tabMaxWidth) {
				tabWidth = tabMaxWidth ;
			}else if (avgTabWidth < tabMinWidth) {
				tabWidth = tabMinWidth ;
			}
			
		}else {
			tabWidth = groupWidth ;
		}	
	}else{
		tabWidth = ZaAppTab.DEFAULT_WIDTH ;
	}
	
	return tabWidth ;
}

ZaAppTabGroup.prototype.addTab = 
function (tab, resize) {
	ZaAppTabGroup._TABS.add(tab);
	if (resize) {
		this.resetTabSizes();
	}
}

ZaAppTabGroup.prototype.removeTab =
function (tab, resize) {
	ZaAppTabGroup._TABS.remove (tab) ;
	tab.dispose () ;
	if (resize) {
		this.resetTabSizes ();
	}
}

ZaAppTabGroup.prototype.removeCurrentTab =
function (resize) {
	var cTab = this.getCurrentTab () ;
	this.removeTab(cTab, resize) ;
	//select the next active Tab
	this.selectTab (this.getTabById (cTab._app._currentViewId)) ; 
}
	
ZaAppTabGroup.prototype.selectTab =
function (tab) {
	if (this.getCurrentTab() == tab) return ;
	
	for (var i=0; i < ZaAppTabGroup._TABS.size(); i++) {
		var cTab = ZaAppTabGroup._TABS.get(i) ;
		if (cTab == tab) {
			cTab.setSelectState();
			this._currentTab = cTab ;
		}else if (cTab.isSelected()){
			cTab.setUnselectState ();
		}	
	}
}

ZaAppTabGroup.prototype.getTabById = 
function (id) {	
	for (var i=0; i < ZaAppTabGroup._TABS.size(); i++) {
		var cTab = ZaAppTabGroup._TABS.get(i) ;
		if (cTab.getTabId() == id) {
			return cTab ;
		}
	}
}
	
ZaAppTabGroup.prototype.getTabHeight =
function () {
	var h = this.getH ();	
	if (h > 0) {
		return h ;
	}else{
		return ZaAppTab.DEFAULT_HEIGHT ;
	}
} 

ZaAppTabGroup.prototype.getCurrentTab =
function () {
	return this._currentTab ;
}

ZaAppTabGroup.prototype.getMainTab =
function () {
	return this._mainTab ;
}

ZaAppTabGroup.prototype.size =
function () {
	return ZaAppTabGroup._TABS.size() ;
}

/*
 * Used to find the existing tab of an item, so we won't open duplicated tab for the same item
 */
ZaAppTabGroup.prototype.getTabByItemId =
function (itemId) {
	for (var i=0; i < ZaAppTabGroup._TABS.size(); i++) {
		var tab = ZaAppTabGroup._TABS.get(i) ;
		var v = tab.getAppView() ;
		if (v && v._containedObject && v._containedObject.id) {
			if (itemId == v._containedObject.id) {
				return tab ;
			}
		}
	}	
	
}