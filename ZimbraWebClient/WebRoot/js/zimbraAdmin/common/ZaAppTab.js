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

//function ZaAppTab(parent, app, label, icon, width, height, closable, selected, id) {
function ZaAppTab(parent, app, params) {
	if (arguments.length == 0) return ;
	this._app = app ;
	this._origClassName = "ImgAppTab" ;
	DwtButton.call(this, parent, null, this._origClassName , Dwt.ABSOLUTE_STYLE);	
	//clean the DwtButton event listeners
	this.removeListener(DwtEvent.ONMOUSEOVER, this._mouseOverListenerObj);
	
	//build the Tab UI
	this._tabId = params.id || this._app._currentViewId ;
	
	var w = params.width || parent.getTabWidth() ;
	var h = params.height || parent.getTabHeight() ;
	this.setSize(w, h) ;
		
	if (params.label) 	{
		this.setText (params.label);
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
	this._hoverClassName = this._origClassName + "Hover" ;
	this._selectedClassName = this._origClassName + "Sel" ;
	this._triggeredClassName = this._selectedClassName ;
	
	var hoverListener = new AjxListener(this, ZaAppTab.prototype._hoverListenr) ;
	this.addListener(DwtEvent.ONMOUSEOVER, hoverListener) ;
	
	var selListener = new AjxListener(this, ZaAppTab.prototype._selListener);
	this.addSelectionListener(selListener) ;
	
	var mouseoutListener = new AjxListener(this, ZaAppTab.prototype._mouseoutListener) ;
	this.addListener (DwtEvent.ONMOUSEOUT, mouseoutListener);
	
	parent.addTab(this, true);
	
	this._selected = params.selected;
	if (this._selected) {
		parent.selectTab(this) ;
	}
}

ZaAppTab.prototype = new DwtButton;
ZaAppTab.prototype.constructor = ZaAppTab;

ZaAppTab.DEFAULT_HEIGHT = 22 ;
ZaAppTab.DEFAULT_WIDTH = 100 ;

ZaAppTab.prototype.getAppView =
function () {
	return this._app.getViewById (this._tabId)[ZaAppViewMgr.C_APP_CONTENT] ;
}

ZaAppTab.prototype._hoverListenr =
function (ev) {
	//DBG.println(AjxDebug.DBG1, "ZaAppTab.prototype._hoverListenr") ;
	if (! this._selected ) {
		this.__setClassName (this._hoverClassName) ;
		this.setCursor ("pointer") ;
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
	this.restoreOrginState() ;
}

ZaAppTab.prototype.setSelectState =
function () {
	this._selected = true ;
	this.__setClassName (this._selectedClassName);
	this.setCursor ("default") ;
	this.removeListener(DwtEvent.ONMOUSEUP, this._mouseUpListenerObj);
	
	var viewId = this.getTabId () ;
	if (viewId != this._app._currentViewId) {
		this._app.pushView (viewId);
	}	
	if (this._closeCell) {
		AjxImg.setImage (this._closeCell, "Close") ;
	}
}

ZaAppTab.prototype.setUnselectState =
function () {
	this._selected = false ;
	this.__setClassName (this._origClassName) ;
	this.setCursor ("pointer") ;
	this.addListener(DwtEvent.ONMOUSEUP, this._mouseUpListenerObj);
	
	if (this._closeCell) {
		AjxImg.setImage (this._closeCell, "CloseDis") ;
	}
}

ZaAppTab.prototype.restoreOrginState =
function () {
	if (this._selected) {
		this.__setClassName (this._selectedClassName) ;
	}else {
		this.__setClassName (this._origClassName) ;
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
	}
	
	var tabW = this.getW ();
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
	var obj = DwtUiEvent.getDwtObjFromEvent(ev); //obj is ZaAppTab instance
}

ZaAppTab._closeCellMouseUpHdlr =
function (ev) {
	//close the tab and the view
	var obj = DwtUiEvent.getDwtObjFromEvent(ev); 
	var app = obj._app ;
	var tabViewId = obj.getTabId () ;
	var cc = app.getControllerById (tabViewId) ;
	
	//check whether the closing view is hidden or visible
	if (tabViewId == app._currentViewId) { //visible
		cc.closeButtonListener(ev); //Tab handling is in the view controller's close button listener
	}else{ //hidden 
		//TODO what if it is dirty?
		cc.closeButtonListener(ev, true, ZaAppTab.prototype.closeHiddenTab, obj ) ;
	}
	//obj._app.popView();
	
	//remove from the Tab Group
	//obj.parent.removeTab (obj, true) ;
	
	//dispose the tab
	//obj.dispose () ;
	
	//may need to switch to the next tab
	//obj.parent.selectTab (obj.parent.getTabById (obj._app._currentViewId));
}

ZaAppTab.prototype.closeHiddenTab =
function () {
	var app = this._app ;
	var tabViewId = this.getTabId () ;
	
	//Make sure the nextTab is selected. 
	// It is useful when user close a dirty hidden tab and the select action is invoked.
	if (tabViewId == app._currentViewId) {
		app.popView();
	}else{
		this.parent.removeTab (this, true) ;
		//dispose the view and remove the controller
		app.disposeView (tabViewId) ;
	}
	
}

ZaAppTab._closeCellMouseOverHdlr =
function (ev) {
	//DBG.println(AjxDebug.DBG1, "Over the close button ....") ;
	var obj = DwtUiEvent.getDwtObjFromEvent(ev);
	AjxImg.setImage (obj._closeCell, "Close") ;
	obj.setCursor("pointer");
	obj._mainToolTip = obj.getToolTipContent ();
	obj.setToolTipContent (ZaMsg.ALTBB_Close_tt) ;
}

ZaAppTab._closeCellMouseOutHdlr =
function (ev) {
	var obj = DwtUiEvent.getDwtObjFromEvent(ev);
	if (obj.isSelected()) {
		AjxImg.setImage (obj._closeCell, "Close") ;
	}else{
		AjxImg.setImage (obj._closeCell, "CloseDis") ;
	}
	obj.setCursor("default");
	obj.setToolTipContent (obj._mainToolTip) ;
}






