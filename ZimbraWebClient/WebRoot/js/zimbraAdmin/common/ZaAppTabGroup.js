/**
 * The container for all the Application tabs. It will include all the operations at the
 * tab group level, including add a tab, remove a tab, switch tab, move tab position, 
 * resize tabs, 
 * 
 * 
*/

ZaAppTabGroup = function(parent,parentElId) {
	if (arguments.length == 0) return;
	DwtComposite.call(this,{
		parent:parent, 
		className:"ZaAppTabGroup", 
		posStyle:Dwt.ABSOLUTE_STYLE, 
		id:ZaId.getTabId(ZaId.TAB_GROUP,parentElId)
	});
	this._created = false ;

	this._mainTab = null;
	this._currentTab = null ;
	this._currentTabWidth = 0;
	
	this._createUI(parentElId) ;
		
	this._visibleStartTab = 0;
	this._numberOfVisibleTabs = 0 ;
}

ZaAppTabGroup.prototype = new DwtComposite();
ZaAppTabGroup.prototype.constructor = ZaAppTabGroup;

//Global Varible to keep all the tab instances
ZaAppTabGroup._TABS = new AjxVector() ;
ZaAppTabGroup.TAB_LIMIT  = 10;

ZaAppTabGroup.prototype.getTabs =
function () {
	return ZaAppTabGroup._TABS ;
}

ZaAppTabGroup.prototype._createUI =
function (parentElId) {
	if (this._created) {
		return ;
	}else {
		if (parentElId) {
			this.reparentHtmlElement ( parentElId );
		}
		//create the shift arrows
		
		this._leftArrow = new DwtComposite(this, null, DwtControl.ABSOLUTE_STYLE) ;
		AjxImg.setImage(this._leftArrow.getHtmlElement(), "LeftArrow");
		this._leftArrow.setDisplay("none");
		this._rightArrow = new DwtComposite(this, null, DwtControl.ABSOLUTE_STYLE) ;
		AjxImg.setImage (this._rightArrow.getHtmlElement(), "RightArrow");
		this._rightArrow.setDisplay("none");
		this.setArrowHandler ();
		
		/*
		this._leftArrow = new DwtButton (this, null, null, DwtControl.ABSOLUTE_STYLE) ;
		this._rightArrow = new DwtButton (this, null, null, DwtControl.ABSOLUTE_STYLE) ;
		this._leftArrow.setImage ("LeftArrow") ;
		this._rightArrow.setImage("RightArrow") ;
		var selListener = new AjxListener(this, ZaAppTabGroup.prototype._arrowSelListener);
		this._leftArrow.addSelectionListener(selListener) ;
		this._rightArrow.addSelectionListener(selListener) ;
		*/
		//create the main tab
		var tabParams = {
			closable: false,
			mainId: ZaId.TAB_MAIN,
			selected: true
		}
		//this._mainTab = new ZaAppTab (this,  null, null,	null, null, false, true);
		this._mainTab = new ZaAppTab (this,  tabParams);
		this._currentTab = this._mainTab ;
		/*
		if (mainTab) {
			this.addTab(mainTab, false);
		}*/
			
		this._created = true ;
		ZaApp.getInstance().setTabGroup (this) ;
	}
}

ZaAppTabGroup.prototype.setCurrentTabWidth =
function (w) {
	this._currentTabWidth = w ;
}

ZaAppTabGroup.prototype.getCurrentTabWidth =
function () {
	return this._currentTabWidth ;
}

ZaAppTabGroup.prototype.getArrowY =
function () {
	return -4;
}

//this method is called when the browser is resized.
ZaAppTabGroup.prototype._resizeListener =
function () {
	this.resetTabSizes(true);
}


//Need to handle the tab size properly. And  may need to change the label accordingly
ZaAppTabGroup.prototype.resetTabSizes =
function (shouldShift){
	var nextX = 0;
	var tabW = this.getTabWidth();
	//7 is left-margin (3) + left-border (2) + right-border (2)
	var w = tabW - 7;
	this.setCurrentTabWidth(w) ;
	
	var tabH = this.getTabHeight() ;
	var groupWidth = this.getW () ;
	//var groupWidth = this.getW () - 40 ; //40 is the size the left/right arrow
	
	if (this._leftArrow.getVisible()) {
		groupWidth -= 16;
		//groupWidth -= 3 ; //not exactly sure where this 3 from. But it should be deducted to show the tab properly
		nextX += 16 ;
	}
	
	if (this._rightArrow.getVisible()) {
		groupWidth -= 16;
	}
	var isShiftNeeded = false ;
	var indexOfHiddenSelectedTab ;	

    var y ;
 /*   if (AjxEnv.isIE) {
		if (ZaApp.getInstance()._appViewMgr._isAdvancedSearchBuilderDisplayed)  { //once the advancedSearchBuilder is displayed, the skin height got changed. (weird). Hack to make it displayed properly
            y = -11 ;
        }else{
            y = -4 ;
        }
    }*//*else{
		y = 0 ;
	}  */                                                                          
	for (var i=0; i < ZaAppTabGroup._TABS.size(); i++) {
		var cTab = ZaAppTabGroup._TABS.get(i) ;
				
		/*
		if (! cTab._closable) {
			w -= 20 ;
		}  */
				
		cTab.setBounds (nextX, y, w, tabH) ; 
		cTab.resetLabel (cTab.getTitle());
		
		if (nextX && groupWidth && ((nextX + w) > groupWidth)) {
			cTab.setVisible(false);
			if (cTab.isSelected() && shouldShift) { //if the selected tab is hidden, the shift action will be needed.
				isShiftNeeded = true ;
				indexOfHiddenSelectedTab = i ;
			}
		}else{
			cTab.setVisible (true) ;
			//this._numberOfVisibleTabs = i + 1 ; //record how many tabs are visible
		}
		//7 is left-margin (3) + left-border (2) + right-border (2)
		nextX = nextX + w + 7;
	}
	
	/*
	if (this._numberOfVisibleTabs 
			&& this._numberOfVisibleTabs < ZaAppTabGroup._TABS.size()
			&& (this._leftArrow.getVisible() == false
				|| this._rightArrow.getVisible() == false )
			) {
		this._leftArrow.setVisible (true);
		this._rightArrow.setVisible (true);
		this.resetTabSizes();
	}
	
	if (this._numberOfVisibleTabs 
			&& this._numberOfVisibleTabs >= ZaAppTabGroup._TABS.size()
			&& (this._leftArrow.getVisible()
				&& this._rightArrow.getVisible())) {
		this._leftArrow.setVisible (false);
		//this._leftArrow.setLocation(0);
		this._rightArrow.setVisible (false);
		//this._rightArrow.setLocation(groupWidth - 20);
		this.resetTabSizes();
	}*/
	
	if (this._leftArrow.getVisible()) {
		this._leftArrow.setLocation(0, this.getArrowY());
	}else{
		this._leftArrow.setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	}
	
	if (this._rightArrow.getVisible()) {
		this._rightArrow.setLocation(this.getW () - 16, this.getArrowY());
	}else{
		this._rightArrow.setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	}
	
	if (isShiftNeeded && shouldShift) { //expecially useful at the window resize
		var n ;
		if (indexOfHiddenSelectedTab < this._visibleStartTab) { //hidden on the left
			n = indexOfHiddenSelectedTab - this._visibleStartTab ;
		}else { //hidden on the right
		 	n = indexOfHiddenSelectedTab - (this._visibleStartTab + this._numberOfVisibleTabs - 1);
		}
		this.shift(n) ;
	}
}

/**
 * Shift the tab by n (move range of the tab index)
 * n > 0 ; shift right (click the right arrow)
 * n < 0 ; shift left (click the left arrow)
 * 
 */
ZaAppTabGroup.prototype.shift =
function (n) {
	if (! this._numberOfVisibleTabs) return ;
	var totalNoTabs = ZaAppTabGroup._TABS.size() ;
	//
	if (this._numberOfVisibleTabs + this._visibleStartTab + n > totalNoTabs) {
		n = totalNoTabs - this._numberOfVisibleTabs - this._visibleStartTab ;
	}
	
	if (!n) {
		return ;
	}else if (n >0) {
		this._leftArrow.setEnabled(true);
		AjxImg.setImage(this._leftArrow.getHtmlElement(), "LeftArrow");
	}else if (n < 0) {
		this._rightArrow.setEnabled (true) ;
		AjxImg.setImage(this._rightArrow.getHtmlElement(), "RightArrow");
	}
	var nextX = 0; 
	var groupWidth = this.getW () ;
	this._leftArrow.setLocation (nextX, this.getArrowY()) ;
	//20 is the width of the arrow image
	this._rightArrow.setLocation (groupWidth - 20, this.getArrowY()) ;
	nextX += 20 ;
	
	this._visibleStartTab += n ;
	for (var i=0; i < totalNoTabs; i++) {
	 	var cTab = ZaAppTabGroup._TABS.get(i) ;
	 	if ((i >= this._visibleStartTab) 
	 			&& (i <= (this._visibleStartTab + this._numberOfVisibleTabs -1))){
			cTab.setVisible (true) ;
			cTab.setLocation (nextX);
			if (i+1 == totalNoTabs) { //last tab is visible
				this._rightArrow.setEnabled(false) ;
				AjxImg.setImage(this._rightArrow.getHtmlElement(), "rightArrowDis");
			} 
			
			if ( i == 0){
				this._leftArrow.setEnabled (false) ;
				AjxImg.setImage(this._leftArrow.getHtmlElement(), "LeftArrowDis");
			}
			nextX += this.getCurrentTabWidth() + 7 ;
	 	}else{
	 		cTab.setVisible (false) ;
	 	} 
	}
	
	//this.resetTabSizes();
}

ZaAppTabGroup.prototype.setArrowHandler =
function () {
	/*
	this._leftArrow.addListener (DwtEvent.ONMOUSEOUT, new AjxListener(this._leftArrow, ZaAppTabGroup._arrowMouseOutHdlr));
	this._leftArrow.addListener (DwtEvent.ONMOUSEUP, new AjxListener(this._leftArrow, ZaAppTabGroup._arrowMouseUpHdlr));
	this._leftArrow.addListener (DwtEvent.ONMOUSEOVER, new AjxListener(this._leftArrow, ZaAppTabGroup._arrowMouseOverHdlr));
	this._leftArrow.addListener (DwtEvent.ONMOUSEDOWN, new AjxListener(this._leftArrow, ZaAppTabGroup._arrowMouseDownHdlr));
	
	
	this._rightArrow.addListener (DwtEvent.ONMOUSEOUT, new AjxListener(this, ZaAppTabGroup._arrowMouseOutHdlr));
	this._rightArrow.addListener (DwtEvent.ONMOUSEUP, new AjxListener(this, ZaAppTabGroup._arrowMouseUpHdlr));
	this._rightArrow.addListener (DwtEvent.ONMOUSEOVER, new AjxListener(this, ZaAppTabGroup._arrowMouseOverHdlr));
	this._rightArrow.addListener (DwtEvent.ONMOUSEDOWN, new AjxListener(this, ZaAppTabGroup._arrowMouseDownHdlr));
	*/
	//this._leftArrow.addSelectionListener(selListener) ;
	//this._rightArrow.addSelectionListener(selListener) ;
	
	
	Dwt.setHandler(this._leftArrow.getHtmlElement(), DwtEvent.ONMOUSEDOWN, ZaAppTabGroup._leftArrowMouseDownHdlr);
	Dwt.setHandler(this._leftArrow.getHtmlElement(), DwtEvent.ONMOUSEUP, ZaAppTabGroup._leftArrowMouseUpHdlr);
	Dwt.setHandler(this._leftArrow.getHtmlElement(), DwtEvent.ONMOUSEOVER, ZaAppTabGroup._arrowMouseOverHdlr);
	Dwt.setHandler(this._leftArrow.getHtmlElement(), DwtEvent.ONMOUSEOUT, ZaAppTabGroup._arrowMouseOutHdlr);
	
	Dwt.setHandler(this._rightArrow.getHtmlElement(), DwtEvent.ONMOUSEDOWN, ZaAppTabGroup._rightArrowMouseDownHdlr);
	Dwt.setHandler(this._rightArrow.getHtmlElement(), DwtEvent.ONMOUSEUP, ZaAppTabGroup._rightArrowMouseUpHdlr);
	Dwt.setHandler(this._rightArrow.getHtmlElement(), DwtEvent.ONMOUSEOVER, ZaAppTabGroup._arrowMouseOverHdlr);
	Dwt.setHandler(this._rightArrow.getHtmlElement(), DwtEvent.ONMOUSEOUT, ZaAppTabGroup._arrowMouseOutHdlr);
	
 }

/*
ZaAppTabGroup.prototype._arrowSelListener =
function (ev){
	DBG.println(AjxDebug.DBG1, "Arrow Selected ....") ;
}*/

ZaAppTabGroup._arrowMouseOverHdlr =
function (ev) {
	//DBG.println(AjxDebug.DBG1, "Mouse on arrow button is over ....") ;
	var obj = DwtControl.getTargetControl(ev);
	if (obj.getEnabled()) {
		obj.setCursor("pointer") ;
	}
}

ZaAppTabGroup._arrowMouseOutHdlr =
function (ev) {
	//DBG.println(AjxDebug.DBG1, "Mouse on arrow button is out ....") ;
	var obj = DwtControl.getTargetControl(ev);
	obj.setCursor("default") ;
}

ZaAppTabGroup._leftArrowMouseDownHdlr =
function (ev) {
	//DBG.println(AjxDebug.DBG1, "Mouse on arrow button is down ....") ;
	var obj = DwtControl.getTargetControl(ev); 
	AjxImg.setImage(this, "LeftArrowDis");
	if (obj.getEnabled()) {
		obj.parent.shift (-1);
	}
}

ZaAppTabGroup._rightArrowMouseDownHdlr =
function (ev) {
	//DBG.println(AjxDebug.DBG1, "Mouse on arrow button is down ....") ;
	var obj = DwtControl.getTargetControl(ev); 
	AjxImg.setImage(this, "rightArrowDis");
	if (obj.getEnabled()) {
		obj.parent.shift (1) ;
	}
}

ZaAppTabGroup._leftArrowMouseUpHdlr =
function (ev) {
	//DBG.println(AjxDebug.DBG1, "Mouse on arrow button is up ....") ;
	var obj = DwtControl.getTargetControl(ev); 
	
	if (obj.getEnabled()) {
		obj.setCursor("pointer");
		AjxImg.setImage(this, "LeftArrow");
	}
}

ZaAppTabGroup._rightArrowMouseUpHdlr =
function (ev) {
	//DBG.println(AjxDebug.DBG1, "Mouse on arrow button is up ....") ;
	var obj = DwtControl.getTargetControl(ev); 

	if (obj.getEnabled()) {
		obj.setCursor("pointer");
		AjxImg.setImage(this, "RightArrow");
	}
}

ZaAppTabGroup.prototype.getTabWidth =
function () {
	var tabWidth ;
	var tabMinWidth = ZaAppTab.DEFAULT_MIN_WIDTH;
	var tabMaxWidth = ZaAppTab.DEFAULT_MAX_WIDTH;
	var groupWidth = this.getW () ;
//	var groupWidth = this.getW () - 40; //40 is the size the left/right arrow
	
	
	if (this._leftArrow.getVisible()) {
		groupWidth -= 23; //the offset when the leftArrow is visible
	}
	
	if (this._rightArrow.getVisible()) {
		groupWidth -= 19; //16 length + 3 left-margin
	}
	
	if (groupWidth > 0) {
		if (groupWidth > tabMinWidth) {
			var numOfTabs = ZaAppTabGroup._TABS.size() ;
			this._numberOfVisibleTabs = numOfTabs ;
			var avgTabWidth = Math.floor(groupWidth / (numOfTabs <= 0 ? 1 : numOfTabs)) ;
			if (avgTabWidth >= tabMinWidth && avgTabWidth <= tabMaxWidth) {
				tabWidth = avgTabWidth ;
			}else if (avgTabWidth > tabMaxWidth) {
				tabWidth = tabMaxWidth ;
			}else if (avgTabWidth < tabMinWidth) {
				//too many tabs and can't be all visible
				//it should equal to the groupWidth/numberOfTabVisible.
				//tabWidth = tabMinWidth ;
								
				var numTabsVisible = this._numberOfVisibleTabs = Math.floor(groupWidth / tabMinWidth) ;
				
				tabWidth = Math.floor(groupWidth / numTabsVisible) ; 
				/*if (AjxEnv.hasFirebug) console.debug(   "groupWidth = " + groupWidth 
											+ " and number of tabs visible = " + numTabsVisible
											+ " tab width = " + tabWidth );*/
				//need to show the navigation arrows, so resize the tab width is required
				if ((!this._leftArrow.getVisible()) || (! this._rightArrow.getVisible())){
					this._leftArrow.setVisible (true);
					this._rightArrow.setVisible (true);
					tabWidth = this.getTabWidth();
				}
				
				return tabWidth ;
			}			
		}else {
			tabWidth = groupWidth ;
		}	
	}else{
		tabWidth = ZaAppTab.DEFAULT_MAX_WIDTH ;
	}
	
	//all the tabs are visible
	if (this._leftArrow.getVisible() ||  this._rightArrow.getVisible()) {
		this._leftArrow.setVisible (false);
		this._rightArrow.setVisible (false);
		tabWidth = this.getTabWidth();
	}
	
	return tabWidth ;
}

ZaAppTabGroup.prototype.addTab = 
function (tab, resize) {
	ZaAppTabGroup._TABS.add(tab);
	if (resize) {
		this.resetTabSizes();
	}
	return true;
	/*
	var cSize = ZaAppTabGroup._TABS.size () ;
	if (cSize >= ZaAppTabGroup.TAB_LIMIT) {
		ZaApp.getInstance().getCurrentController().popupMsgDialog(ZaMsg.too_many_tabs);
		ZaApp.getInstance().disposeView (tab.getTabId());
		tab.dispose();
		return false ;
	}else{
		ZaAppTabGroup._TABS.add(tab);
		if (resize) {
			this.resetTabSizes();
		}
		return true;
	}*/
}

ZaAppTabGroup.prototype.removeTab =
function (tab, resize) {
	if (tab == this._searchTab) {
		this._searchTab = null ;
		//need to reset the search list controller
		ZaApp.getInstance().getSearchListController().reset() ;
	}
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
	this.selectTab (this.getTabById (ZaApp.getInstance()._currentViewId)) ; 
}
	
ZaAppTabGroup.prototype.selectTab =
function (tab) {
	if (this.getCurrentTab() == tab) return ;
	
	for (var i=0; i < ZaAppTabGroup._TABS.size(); i++) {
		var cTab = ZaAppTabGroup._TABS.get(i) ;
		if (cTab == tab) {
			cTab.setSelectState();
			this._currentTab = cTab ;
			
			//check weather the shift action is needed
			if ( i < this._visibleStartTab) { //show the hidden tab on the left
				this.shift (i - this._visibleStartTab ) ;
			}else if (i > (this._numberOfVisibleTabs + this._visibleStartTab - 1)) {
				this.shift (i - (this._numberOfVisibleTabs + this._visibleStartTab - 1));
			}
			
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

ZaAppTabGroup.prototype.getSearchTab =
function () {
	if (this._searchTab) {
		return this._searchTab ;
	}else{
		var tabParams = {
			closable: true,
			selected: true
		}
		
		this._searchTab = new ZaAppTab (this,  tabParams);
		return this._searchTab ;
	}
}

ZaAppTabGroup.prototype.size =
function () {
	return ZaAppTabGroup._TABS.size() ;
}

/*
 * Used to find the existing tab of an item, so we won't open duplicated tab for the same item
 */
ZaAppTabGroup.prototype.getTabByItemId =
function (itemId, tabConstructor) {
	for (var i=0; i < ZaAppTabGroup._TABS.size(); i++) {
		var tab = ZaAppTabGroup._TABS.get(i) ;
		var v = tab.getAppView() ;
		if (v && v._containedObject && v._containedObject.id && v.constructor) {
			if (itemId == v._containedObject.id && (v.constructor==tabConstructor || !tabConstructor)) {
				return tab ;
			}
		}
	}
}

ZaAppTabGroup.getDirtyTabTitles =
function () {
	var dirtyTabTitles = [] ;
	for (var i=0; i < ZaAppTabGroup._TABS.size(); i++) {
		var tab = ZaAppTabGroup._TABS.get(i) ;
		var v = tab.getAppView() ;
		if (v && v.isDirty && v.isDirty()) {
			dirtyTabTitles.push(tab.getTitle());
		}
	}
	return dirtyTabTitles ;
}


