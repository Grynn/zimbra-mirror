/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaToolBar
* @contructor
* generic class that serves the purpose of creating any toolbar in the admin UI
* @param parent parent object
* @param opList array of ZaOperation objects
**/
ZaToolBar = function(parent, opList,btnOrder,posStyle,className, contextId) { 
	if (arguments.length == 0) return;
	className = className || "ZaToolBar";
	posStyle = posStyle || DwtControl.ABSOLUTE_STYLE;

	this._barViewId = contextId;
	DwtToolBar.call(this, {parent:parent, className:className, posStyle:posStyle, id: ZaId.getToolbarId(this._barViewId)});
	this._opList = opList ;
    	this._btnOrder = btnOrder ;
    	this._buttons = new Object(); //all the buttons on the toolbar
    	this.addMoreActions() ;
	this._btnList = new Array();
    	this.init () ;
}

ZaToolBar.VIEW_DATA = "ZaToolBar.VIEW";

ZaToolBar.prototype = new DwtToolBar;
ZaToolBar.prototype.constructor = ZaToolBar;

ZaToolBar.prototype.toString = 
function() {
	return "ZaToolBar";
}

ZaToolBar.prototype.init = function () {
    var opList = this._opList ;
    var btnOrder = this._btnOrder ;
    if(!AjxUtil.isEmpty(btnOrder) && opList) {
		var cnt = btnOrder.length;
		for(var ix = 0; ix < cnt; ix++) {
			if(opList[btnOrder[ix]] && opList[btnOrder[ix]] instanceof ZaOperation) {
				if(opList[btnOrder[ix]].id == ZaOperation.SEP) {
					this._createSeparator();
				} else if (opList[btnOrder[ix]].id == ZaOperation.NONE) {
					this.addFiller();
				} else if (opList[btnOrder[ix]].id == ZaOperation.LABEL) {
					this._createLabel(opList[btnOrder[ix]].labelId, opList[btnOrder[ix]].imageId, opList[btnOrder[ix]].caption, opList[btnOrder[ix]].disImageId, opList[btnOrder[ix]].tt, true, opList[btnOrder[ix]].className);
				} else {
					this._createButton(opList[btnOrder[ix]].id, opList[btnOrder[ix]].imageId, opList[btnOrder[ix]].caption, opList[btnOrder[ix]].disImageId, opList[btnOrder[ix]].tt, true, opList[btnOrder[ix]].className, opList[btnOrder[ix]].type, opList[btnOrder[ix]].menuOpList);
					
					if(opList[btnOrder[ix]].listener) {
						this.addSelectionListener(opList[btnOrder[ix]].id, opList[btnOrder[ix]].listener);
					}
				}
			}
		}
	} else if(opList) {
		for(var ix in opList) {
			if(opList[ix] instanceof ZaOperation) {
				if(opList[ix].id == ZaOperation.SEP) {
					this._createSeparator();
				} else if (opList[ix].id == ZaOperation.NONE) {
					this.addFiller();
				} else if (opList[ix].id == ZaOperation.LABEL) {
					this._createLabel(opList[ix].labelId, opList[ix].imageId, opList[ix].caption, opList[ix].disImageId, opList[ix].tt, true, opList[ix].className);
				} else {
					this._createButton(opList[ix].id, opList[ix].imageId, opList[ix].caption, opList[ix].disImageId, opList[ix].tt, true, opList[ix].className, opList[ix].type, opList[ix].menuOpList);

					this.addSelectionListener(opList[ix].id, opList[ix].listener);
				}
			}
		}
	}
}

ZaToolBar.prototype.addSelectionListener =
function(buttonId, listener) {
	this._buttons[buttonId].addSelectionListener(listener);
}

ZaToolBar.prototype.removeSelectionListener =
function(buttonId, listener) {
	this._buttons[buttonId].removeSelectionListener(listener);
}

ZaToolBar.prototype.getButton =
function(buttonId) {
	return this._buttons[buttonId];
}

ZaToolBar.prototype.setData = 
function(buttonId, key, data) {
	this._buttons[buttonId].setData(key, data);
}

/**
* Enables/disables buttons.
*
* @param ids		a list of button IDs
* @param enabled	whether to enable the buttons
*/
ZaToolBar.prototype.enable =
function(ids, enabled) {
	if (!(ids instanceof Array))
		ids = [ids];
	for (var i = 0; i < ids.length; i++)
		if (this._buttons[ids[i]])
			this._buttons[ids[i]].setEnabled(enabled);
}

ZaToolBar.prototype.enableAll =
function(enabled) {
	for (var i in this._buttons) {
		this._buttons[i].setEnabled(enabled);
	}
}

ZaToolBar.prototype.computeHeight =
function(enabled) {
	var h = 0;
	for (var i in this._buttons) {
		h = Math.max(h, this._buttons[i].getSize().y);
	}
	return h;
}

ZaToolBar.prototype.setSize =
function(width, height) {
	var sz = this.getSize();
	if (width != sz.x || height != sz.y) {
		DwtToolBar.prototype.setSize.apply(this, arguments);
		this._checkSize(width,height);
	}
};

ZaToolBar.prototype._checkSize = function(width, height, hideText, showMoreActions) {
    var hideText = hideText || false ; //default false
    var showMoreActions = showMoreActions || true ; // default true ;
    var moreActionsButton = this._buttons [ZaOperation.MORE_ACTIONS] ;
	var el = this.getHtmlElement();
	if(!el) {
		return;
	}
	
	for (var i in this._buttons) {
		var b = this._buttons[i];
		if (!b) { continue; }		
		//reset text
        if(b._toggleText) {
			b.setText(b._toggleText);
			b._toggleText = null;
		}
        //reset visibility
        if (b == moreActionsButton) {
            b.setVisible(false) ;
        }else{
            b.setVisible (true) ;
        }
	}
    
	var offset = el.firstChild.offsetWidth;
	if(offset > width) {
	        var totalVisibleButtonWidth = this.getAlwaysVisibleButtonWidths () ;
		if (showMoreActions) {
        	    moreActionsButton.setVisible(true) ;
	            totalVisibleButtonWidth += moreActionsButton.getW () ; //more actions button size
        	}

		var btnList = null;
		if(!AjxUtil.isEmpty(this._btnOrder))
			btnList = this._btnOrder;
		else btnList = this._btnList;

		var cnt = btnList.length;
		for(var ix = 0; ix < cnt; ix++) {
                        var b = this._buttons[btnList[ix]];
                        if (!b || !b.getVisible()) { continue; }
                        var text = b.getText();
                        if(text && hideText) {
                                b._toggleText = text;
                                b.setText("");
                        }

                        if (showMoreActions) {
                                var w = b.getW() ;
                                totalVisibleButtonWidth += w ;

                                if (totalVisibleButtonWidth >= width) { //width overflow
                                        if (b != moreActionsButton) {
                                                b.setVisible(false) ; // hide the overflow button
                                        } else {  //more actions button is visible now, we can break
                                                break ;
                                        }
                                }
                        }

                        offset = el.firstChild.offsetWidth;
                        if(offset <= width) {
                                break;
                        }

		}
	}

    //set visiblity and enable the more actions popup menu items
    this.enableMoreActionsMenuItems()  ;
}

ZaToolBar.prototype._createButton =
function(buttonId, imageId, text, disImageId, toolTip, enabled, className, type, menuOpList) {
	if (!className)
		className = "DwtToolbarButton"
	var b = this._buttons[buttonId] = new ZaToolBarButton({
			parent:this, 
			className:className, 
			id:ZaId.getButtonId(this._barViewId,ZaOperation.getStringName(buttonId))
	});
	if (imageId)
		b.setImage(imageId);
	if (text)
		b.setText(text);
	if (toolTip)
		b.setToolTipContent(toolTip);
	b.setEnabled((enabled) ? true : false);
	b.setData("_buttonId", buttonId);

	if (type == ZaOperation.TYPE_MENU) {
		var menu = new ZaPopupMenu(b, null,null, menuOpList, this._barViewId, ZaId.MENU_DROP);
		b.setMenu(menu,true);
	}
	this._btnList.push(buttonId);
	return b;
}


ZaToolBar.prototype._createLabel =
function(buttonId, imageId, text, disImageId, toolTip, enabled, className, style) {

	var b = this._buttons[buttonId] = new ZaToolBarLabel(this, null, className);
	if (imageId)
		b.setImage(imageId);
	if (text)
		b.setText(text);
	if (toolTip)
		b.setToolTipContent(toolTip);
	b.setEnabled((enabled) ? true : false);
	b.setData("_buttonId", buttonId);

	return b;
}
ZaToolBar.prototype._createSeparator =
function() {
	var ctrl = new DwtControl(this);
	var html = ZaToolBar.getSeparatorHtml ();
	ctrl.setContent(html);
}

ZaToolBar.getSeparatorHtml =
function () {
	//return "<table><tr><td class=\"ImgAppToolbarSectionSep\" height=20px width=3px> </td></tr></table>";
	return "<div class=\"vertSep\"/>";
}

ZaToolBar.prototype._buttonId =
function(button) {
	return button.getData("_buttonId");
}

/**
 * Insert the "More Operations" button in the toolbar due to the limited size of the toolbar
 * @param index: the position the "More Operations" button should be
 */
ZaToolBar.prototype.addMoreActions =
function () {
    if (AjxUtil.isEmpty (this._btnOrder) || AjxUtil.isEmpty (this._opList)) {
        return ; //no need to add the more actions since there is no action.
    }
    this._moreActionsMenuList = [] ; //the menu list for the More Actions button.
    var index = -1 ;    
    //insert the moreActions button before the ZaOperation.NONE
    for (var i =0; i < this._btnOrder.length; i ++)  {
        if (this._btnOrder [i] == ZaOperation.NONE ) {
            index = i ;
            break ;
        } else { //duplicate the operation.
            var op =  this._opList [this._btnOrder [i]] ;
            if (op != null) {
                this._moreActionsMenuList.push (ZaOperation.duplicate(op)) ;
            }
        }
    }
    this._opList[ZaOperation.MORE_ACTIONS] =
        new ZaOperation(ZaOperation.MORE_ACTIONS, ZaMsg.TBB_MoreActions, ZaMsg.TBB_MoreActions_tt,
                "", "",
                //null, //Need to have popup listener
                new AjxListener(this, this._moreActionsButtonListener),
                ZaOperation.TYPE_MENU,this._moreActionsMenuList );
    this._opList[ZaOperation.MORE_ACTIONS].enabled = false ;
    this._opList[ZaOperation.MORE_ACTIONS].visible = false ;

    if (index == -1) { //there is no ZaOperation.NONE, append at the end of the array
        index =  this._btnOrder.length ;
    }

    this._btnOrder.splice (index, 0, ZaOperation.MORE_ACTIONS) ;
}

/*
 The buttons after ZaOperation.NONE are always visible
 */
ZaToolBar.prototype.getAlwaysVisibleButtonWidths = function () {
    var w = 0 ;
    var isAfterNoneOp = false ;
    for (var i =0; i < this._btnOrder.length; i ++)  {
        var b = this._buttons [this._btnOrder [i]] ;
        if (this._btnOrder [i] == ZaOperation.NONE ) {
            isAfterNoneOp = true ;
        }
        
        /*
         Handle for LABEL, label uses labelid to index, but button uses id.
         */ 
        if (this._btnOrder [i] == ZaOperation.LABEL) { 
            b = this._buttons[ZaOperation.SEARCH_RESULT_COUNT]; 
        }
  
        if (isAfterNoneOp) {
            if (this._btnOrder [i] == ZaOperation.SEP){
               w += 5; // for seperator, we give it a fixed value.
            }
            
            if (b && b.getVisible()) {
                w += b.getW () ;
            }
        }
     }

    return w ;
}
                    
ZaToolBar.prototype.enableMoreActionsMenuItems = function () {
    var moreActionsButton = this._buttons [ZaOperation.MORE_ACTIONS] ;
    var moreActionMenu ;
    if (moreActionsButton) {
        moreActionMenu = moreActionsButton.getMenu () ;
        moreActionMenu.popdown () ;
    }
    
    if (moreActionsButton && moreActionsButton.getVisible()) {
        for (var i =0; i < this._btnOrder.length; i ++)  {
            var opId = this._btnOrder [i] ;
            if (this._btnOrder [i] == ZaOperation.MORE_ACTIONS) {
                break ;
            }

            var toolbarButton = this._buttons[opId] ;
            if (!toolbarButton) { continue ;} ;
            var visiblity = toolbarButton.getVisible() ;
            var menuItem = moreActionMenu.getMenuItem (opId) ; //menu item operation id is the same as the toolbar button id
            menuItem.setVisible(!visiblity) ;
            menuItem.setEnabled (toolbarButton.getEnabled()) ;
        }
    }
}

ZaToolBar.prototype._moreActionsButtonListener = function (ev) {
    var moreActionsButton = this._buttons [ZaOperation.MORE_ACTIONS] ;
    var moreActionMenu ;
    if (moreActionsButton) {
        moreActionMenu = moreActionsButton.getMenu () ;
        if (moreActionMenu.isPoppedUp()) {
            moreActionMenu.popdown () ;
        } else {
            moreActionsButton.popup() ;
        }
    }
}
