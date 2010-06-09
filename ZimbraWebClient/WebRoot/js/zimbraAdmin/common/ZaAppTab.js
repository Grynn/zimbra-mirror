/**
 * Used to create an application tab and its operations, such as new a  tab, 
 * close a tab, edit the tab label.
 * 
 * It will also remember the state of the tab: hidden/shown and dirty/clean.
 * @param parent - the tab group containing all the tabs.
 * 
 * @param params :
 *  	closable - whether the close icon and action should be added
 * 		selected - whether the newly created tab should be selected 
 *		id - the tabId used to identify an unique tab.
 * 		toolTip - the tooltip of the tab
*/

//ZaAppTab = function(parent,ZaApp.getInstance().label, icon, width, height, closable, selected, id) {
ZaAppTab = function(parent, params) {
	if (arguments.length == 0) return ;
	
//	this._origClassName = "ImgAppTab" ;
	DwtButton.call(this, parent, null, "ZaAppTabButton" , Dwt.ABSOLUTE_STYLE);	
	//clean the DwtButton event listeners
	this.removeListener(DwtEvent.ONMOUSEOVER, this._mouseOverListenerObj);
	
	//build the Tab UI
	this._tabId = params.id || ZaApp.getInstance()._currentViewId ;
	
	var w = params.width || parent.getTabWidth() ;
	var h = params.height || parent.getTabHeight() ;
	this.setSize(w, h) ;
		
	if (params.label) 	{
		this.setText (params.label);
		this._title = params.label ;
	}
	if (params.icon) this.setImage (params.icon);
	
	if (params.toolTip && params.toolTip.length > 0) {
		this.setToolTipContent (params.toolTip) ;
	}
		
	this._closable = false ;
	if (params.closable == true) {		
		this.addCloseCell() ;
	}
	
	//control the Tab behavior
	var selListener = new AjxListener(this, ZaAppTab.prototype._selListener);
	this.addSelectionListener(selListener) ;

	if (params.onOpen && params.onOpen instanceof AjxListener) {
		this.addSelectionListener(params.onOpen);
	}
	
	var mouseoutListener = new AjxListener(this, ZaAppTab.prototype._mouseoutListener) ;
	this.addListener (DwtEvent.ONMOUSEOUT, mouseoutListener);

	if (parent.addTab(this, true)){
		this._selected = params.selected;
		if (this._selected) {
			parent.selectTab(this) ;
		}
	}
	
	//add the popup menu
	this._addPopupMenu () ;
}

ZaAppTab.prototype = new DwtButton;
ZaAppTab.prototype.constructor = ZaAppTab;

ZaAppTab.DEFAULT_HEIGHT = 22 ;
ZaAppTab.DEFAULT_MAX_WIDTH = 200 ;
ZaAppTab.DEFAULT_MIN_WIDTH = 100 ;

ZaAppTab.prototype.getTitle =
function () {
	return this._title ;
}

ZaAppTab.prototype._addPopupMenu =
function () {
	this._popupOperations = [];
	
	//close the tab
	//if (this._closable) { //disable it instead of hiding it
	this._actionOpClose = new ZaOperation(ZaOperation.CLOSE_TAB, ZaMsg.tab_close, 
			null, null, null, new AjxListener(this, ZaAppTab.prototype.closeTab));
	this._popupOperations.push(this._actionOpClose);
	//}
	//close other tabs
	this._actionOpCloseOthers = new ZaOperation(ZaOperation.CLOSE_OTHER_TAB, ZaMsg.tab_close_others,
			null, null, null, new AjxListener(this, ZaAppTab.prototype.closeOtherTabs));
	this._popupOperations.push(this._actionOpCloseOthers) ;
	//close all tabs
	this._actionOpCloseAll = new ZaOperation(ZaOperation.CLOSE_ALL_TAB, ZaMsg.tab_close_all,
			null, null, null, new AjxListener(this, ZaAppTab.prototype.closeAllTabs));
	this._popupOperations.push(this._actionOpCloseAll) ;
	
	this._actionMenu =  new ZaPopupMenu(this, "ActionMenu", null, this._popupOperations);
	
	//add the popup menu related mouse listeners
	//right button click of the mouse
	var actionListener = new AjxListener (this, ZaAppTab.prototype._mouseRightClickListener) ;
	this.addListener(DwtEvent.ACTION, actionListener);
	
	this._tabMouseUpListener = new AjxListener (this, ZaAppTab.prototype._tabMouseupListener) ;
	this.addListener(DwtEvent.ONMOUSEUP, this._tabMouseUpListener);
}

ZaAppTab.prototype.getAppView =
function () {
	var view = ZaApp.getInstance().getViewById (this._tabId);
	if(view) {
		return view[ZaAppViewMgr.C_APP_CONTENT] ;
	} else {
		return null;
	}
	
}

ZaAppTab.prototype._selListener =
function (ev) {
	DBG.println(AjxDebug.DBG1, "ZaAppTab.prototype._selListenr") ;
	//this.setSelectState () ;
	this.parent.selectTab(this);
}

ZaAppTab.prototype._mouseoutListener =
function (ev) {
	//DBG.println(AjxDebug.DBG1, "ZaAppTab.prototype._mouseoutListenr") ;
	//console.debug("This is a mouse out action") ;
	this.restoreOrginState() ;
}

ZaAppTab.prototype._tabMouseupListener =
function (ev) {
	//if (console) console.debug("Tab Mouse Up") ;
	if (ev.button == DwtMouseEvent.RIGHT) {
		if (this.isListenerRegistered(DwtEvent.ACTION)) {				
				this.notifyListeners(DwtEvent.ACTION, ev);
		}
	}
}

ZaAppTab.prototype._mouseRightClickListener =
function (ev) {
	//if (console) console.debug("This is a right mouse action") ;
	var tabGroup = this.parent ;
	var tabs = tabGroup.getTabs() ;
	var n = tabs.size() ;
	if ( n <= 1) {
		this._actionMenu.enableAll(false) ;
	} else if ( n <= 2 && this != tabGroup.getMainTab()) {
		this._actionMenu.enable(ZaOperation.CLOSE_TAB, true) ;
		this._actionMenu.enable(ZaOperation.CLOSE_ALL_TAB, true) ;
		this._actionMenu.enable(ZaOperation.CLOSE_OTHER_TAB, false) ;
	}else{
		this._actionMenu.enableAll(true) ;
	}
	
	this._actionMenu.popup(0, ev.docX, ev.docY);
}

ZaAppTab.prototype.setSelectState =
function () {
	this._selected = true ;
    this.setDisplayState(DwtControl.SELECTED);
	this.setCursor ("default") ;
	this.removeListener(DwtEvent.ONMOUSEUP, this._mouseUpListenerObj);
	
	var viewId = this.getTabId () ;
	if (viewId != ZaApp.getInstance()._currentViewId) {
		ZaApp.getInstance().pushView (viewId);
	}	
	if (this._closeCell) {
		AjxImg.setImage (this._closeCell, "Close") ;
	}
}

ZaAppTab.prototype.setUnselectState =
function () {
	this._selected = false ;
    this.setDisplayState(DwtControl.NORMAL);
	this.setCursor ("pointer") ;
	this.addListener(DwtEvent.ONMOUSEUP, this._mouseUpListenerObj);
	
	if (this._closeCell) {
		AjxImg.setDisabledImage (this._closeCell, "Close");
	}
}

ZaAppTab.prototype.restoreOrginState =
function () {
	if (this._selected) {
        this.setDisplayState(DwtControl.SELECTED);
	}else {
        this.setDisplayState(DwtControl.NORMAL);
	}
}

ZaAppTab.prototype.setTabId =
function (id) {
	this._tabId = id ;
}

ZaAppTab.prototype.getTabId =
function () {
	return this._tabId ;
}

ZaAppTab.prototype.isSelected =
function () {
	return this._selected ;
}

//reset the label text
//l - new label text
ZaAppTab.prototype.resetLabel =
function(l){
	l = l || this.getText() ;
	
	if (! l) {
		return ;
	}else{
		this._title = l ;
	}
	
	//var tabW = this.getW (); //when the tab is hidden, getW () return 0
	var tabW = this.parent.getCurrentTabWidth (); //we need an internal variable to keep the width for the hidden tab
	if (this._closable) tabW -= 20 ;
	
	//assume 5.5px per letter	
	var maxNumberOfLetters = Math.floor((tabW - 30)/5.5) ;
	if (maxNumberOfLetters < l.length){ //set the new text
		this.setText(l.substring(0, (maxNumberOfLetters - 3)) + "...");
	}else {
		this.setText (l) ;
	}
}

ZaAppTab.prototype.addCloseCell =
function () {
	this._closable = true ;
	
	this._closeCell = this._row.insertCell (this._row.cells.length) ;
	this._closeCell.className = "Icon" ;
	AjxImg.setImage(this._closeCell, "Close") ;
	
	Dwt.setHandler(this._closeCell, DwtEvent.ONMOUSEDOWN, ZaAppTab._closeCellMouseDownHdlr);
	Dwt.setHandler(this._closeCell, DwtEvent.ONMOUSEUP, ZaAppTab._closeCellMouseUpHdlr);
	Dwt.setHandler(this._closeCell, DwtEvent.ONMOUSEOVER, ZaAppTab._closeCellMouseOverHdlr);
	Dwt.setHandler(this._closeCell, DwtEvent.ONMOUSEOUT, ZaAppTab._closeCellMouseOutHdlr);
}

ZaAppTab._closeCellMouseDownHdlr =
function (ev) {
	//DBG.println(AjxDebug.DBG1, "Close Button is clicked ....") ;
	var obj = DwtControl.getTargetControl(ev); //obj is ZaAppTab instance
}

ZaAppTab._closeCellMouseUpHdlr =
function (ev) {
	//close the tab and the view
	var obj = DwtControl.getTargetControl(ev); 
	obj.closeTab();
	/*

	var tabViewId = obj.getTabId () ;
	var cc = ZaApp.getInstance().getControllerById (tabViewId) ;
	
	//check whether the closing view is hidden or visible
	if (tabViewId == ZaApp.getInstance()._currentViewId) { //visible
		cc.closeButtonListener(ev); //Tab handling is in the view controller's close button listener
	}else{ //hidden 
		//TODO what if it is dirty?
		cc.closeButtonListener(ev, true, ZaAppTab.prototype.closeHiddenTab, obj ) ;
	}*/
}

ZaAppTab.prototype.closeTab =
function() {
	if (this._actionMenu && this._actionMenu.isPoppedUp()) {
		this._actionMenu.popdown();
	} 
	
	if (this._closable) {
		
		var tabViewId = this.getTabId () ;
		var cc = ZaApp.getInstance().getControllerById (tabViewId) ;
		
		//check whether the closing view is hidden or visible
		if (tabViewId == ZaApp.getInstance()._currentViewId) { //visible
			//if (AjxEnv.hasFirebug) console.debug("Close current tab " + this.getTitle() + " with ID " + tabViewId);
			cc.closeButtonListener(); //Tab handling is in the view controller's close button listener
		}else{ //hidden 
			//TODO what if it is dirty?
			//if (AjxEnv.hasFirebug) console.debug("Close hidden tab " + this.getTitle() + " with ID " + tabViewId );
			cc.closeButtonListener(null, true, ZaAppTab.prototype.closeHiddenTab, this ) ;
		}
	}
}

ZaAppTab.prototype.closeOtherTabs =
function () {
	
	if (this._actionMenu && this._actionMenu.isPoppedUp()) {
		this._actionMenu.popdown();
	}
	 
	var tabTitles = ZaAppTabGroup.getDirtyTabTitles() ;
	if ( tabTitles.length > 0 ){
		ZaApp.getInstance().getCurrentController().popupMsgDialog(
				AjxMessageFormat.format(ZaMsg.tab_close_warning, [tabTitles.join("<br />")]));
		return ;
	}else{
		if (ZaAppTabGroup.getDirtyTabTitles)
		var tabGroup = this.parent ;
		var tabs = tabGroup.getTabs() ;
		var closingTabs = [] ;
		for (var i=0; i < tabs.size(); i++) {
			var cTab = tabs.get(i) ;
			if ((cTab != this) && (cTab._closable)) {
				//close
				closingTabs.push (cTab) ;
			}
		}
		
		for (var j=0; j < closingTabs.length; j ++) {
			closingTabs[j].closeTab();
		}
		
		if (closingTabs.length > 0) {
			tabGroup.selectTab(this);
		}
	}
}

ZaAppTab.prototype.closeAllTabs =
function () {
	if (this._actionMenu && this._actionMenu.isPoppedUp()) {
		this._actionMenu.popdown();
	} 
	
	var tabTitles = ZaAppTabGroup.getDirtyTabTitles() ;
	if ( tabTitles.length > 0 ){
		ZaApp.getInstance().getCurrentController().popupMsgDialog(
				AjxMessageFormat.format(ZaMsg.tab_close_warning, [tabTitles.join("<br />")]));
		return ;
	}else{
		var tabGroup = this.parent ;
		var tabs = tabGroup.getTabs() ;
		var closingTabs = [] ;
		for (var i=0; i < tabs.size(); i++) {
			var cTab = tabs.get(i) ;
			if (cTab._closable) {
				//close
				closingTabs.push(cTab) ;
			}
		}
		
		for (var j=0; j < closingTabs.length; j ++) {
			//if (AjxEnv.hasFirebug) console.log("Closing tab " + closingTabs[j].getTabId()) ;
			closingTabs[j].closeTab();
		}
		
		if (closingTabs.length > 0 && tabs.size() > 0) {
			tabGroup.selectTab(tabs.get(0));
		}
	}
}

ZaAppTab.prototype.closeHiddenTab =
function () {

	var tabViewId = this.getTabId () ;
	
	//Make sure the nextTab is selected. 
	// It is useful when user close a dirty hidden tab and the select action is invoked.
	if (tabViewId == ZaApp.getInstance()._currentViewId) {
		ZaApp.getInstance().popView();
	}else{
		this.parent.removeTab (this, true) ;
		//dispose the view and remove the controller
		ZaApp.getInstance().disposeView (tabViewId,true) ;
	}
}

ZaAppTab._closeCellMouseOverHdlr =
function (ev) {
	//DBG.println(AjxDebug.DBG1, "Over the close button ....") ;
	var obj = DwtControl.getTargetControl(ev);
	AjxImg.setImage (obj._closeCell, "Close") ;
	obj.setCursor("pointer");
	obj._mainToolTip = obj.getToolTipContent ();
	obj.setToolTipContent (ZaMsg.ALTBB_Close_tt) ;
}

ZaAppTab._closeCellMouseOutHdlr =
function (ev) {
	var obj = DwtControl.getTargetControl(ev);
	if (obj.isSelected()) {
		AjxImg.setImage (obj._closeCell, "Close") ;
	}else{
		AjxImg.setDisabledImage (obj._closeCell, "Close");
	}
	obj.setCursor("default");
	obj.setToolTipContent (obj._mainToolTip) ;
}
ZaAppTab.prototype._createHtmlFromTemplate = function(templateId, data) {
    DwtButton.prototype._createHtmlFromTemplate.call(this, "admin.Widgets#ZaAppTab", data);
    this._row = document.getElementById(data.id+"_row");
};