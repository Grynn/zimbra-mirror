/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* Creates a layout manager from the given components.
* @constructor
* @class
* This class manages layout. The layout is divided into the following parts:
* <p><ul>
*  <li>banner: static; has a few account-related buttons</li>
*  <li>search bar: static; has buttons for various ways to search, including browse</li>
*  <li>overview panel: tree view of folders, tags, app links</li>
*  <li>sash: a thin moveable vertical bar for resizing the surrounding elements
*  <li>app container: the most dynamic area; displays app-specific toolbar and content</li>
* </ul></p>
* <p>
* Visibility is managed through Z indexes, which have constants in the following order:</p>
* <p>
* Z_HIDDEN, Z_CURTAIN, Z_VIEW, Z_TOOLTIP, Z_MENU, Z_VEIL, Z_DIALOG, Z_BUSY</p>
* <p>
* Since z-index matters only among peer elements, anything that we manage via z-index has to
* hang off the shell. To manage an app view, we create an app container that hangs off the shell
* and put the app view in there.</p>
* <p>
* The app container lays out the app elements in the desired style, for example, in a vertical
* layout. Different layout styles can be added here, and then specified when the app view is
* created.</p>
* <p>
* Some views are "volatile", which means they trigger browser bugs when we try to hide them. It happens
* with views that contain forms. In IE, SELECT fields don't obey z-index, and in Firefox, the cursor
* bleeds through.
*
* @author Conrad Damon
* @author Ross Dargahi
* @param shell			the outermost containing element
* @param banner			the banner
* @param controller		the app controller
*/
ZaAppViewMgr = function(shell, controller, hasSkin) {

	this._shell = shell;
	this._controller = controller;
	this._appCtxt = controller._appCtxt;
    this._shellSz = this._shell.getSize();
	this._shell.addControlListener(new AjxListener(this, this._shellControlListener));
	this._needBannerLayout = false;
	this._sashSupported = (window.skin && typeof window.skin.setTreeWidth == "function");
        this._sbSashSupported = (window.skin && typeof window.skin.setSBHeight == "function");
/*	this._sash = new DwtSash(this._shell, DwtSash.HORIZONTAL_STYLE, "AppSash-horiz", 5);
	this._sash.registerCallback(this._sashCallback, this);
*/	
	this._currentView = null;			// name of currently visible view
	this._views = new Object();			// hash that gives names to app views
	this._hidden = new Array();			// stack of views that aren't visible
	
	this._layoutStyle = new Object();	// hash matching view to layout style
	this._staleCallback = new Object(); // when topmost view is popped, allow underlying view to cleanup

	this._compList = new Array();		// list of component IDs
	this._components = new Object();	// component objects (widgets)
	this._htmlEl = new Object();		// their HTML elements
	this._containers = new Object();	// containers within the skin
	this._contBounds = new Object();	// bounds for the containers
	
	// view preemption
	this._pushCallback = new AjxCallback(this, this.pushView);
//	this._popCallback = new AjxCallback(this, this.popView);
	
/*	// hash matching layout style to their methods	
	this._layoutMethod = new Object();
	this._layoutMethod[ZaAppViewMgr.LAYOUT_VERTICAL] = this._appLayoutVertical;
*/	
}

ZaAppViewMgr.DEFAULT = -1;

// reasons the layout changes
ZaAppViewMgr.RESIZE = 1;
ZaAppViewMgr.BROWSE = 2;
ZaAppViewMgr.OVERVIEW = 3;

// visible margins (will be shell background color)
ZaAppViewMgr.TOOLBAR_SEPARATION = 0;	// below search bar
ZaAppViewMgr.COMPONENT_SEPARATION = 2;	// in app container

// layout styles
ZaAppViewMgr.LAYOUT_VERTICAL = 1;	// top to bottom, full width, last element gets remaining space

// used when coming back from pop shield callbacks
ZaAppViewMgr.PENDING_VIEW = "ZaAppViewMgr.PENDgING_VIEW";


// components
ZaAppViewMgr.C_BANNER					= "BANNER";
ZaAppViewMgr.C_SEARCH					= "SEARCH";
ZaAppViewMgr.C_SEARCH_BUILDER			= "SEARCH BUILDER";
ZaAppViewMgr.C_SEARCH_BUILDER_TOOLBAR	= "SEARCH BUILDER TOOLBAR";
ZaAppViewMgr.C_SEARCH_BUILDER_SASH     = "SEARCH BUILDER SASH";
ZaAppViewMgr.C_CURRENT_APP				= "CURRENT APP";
ZaAppViewMgr.C_APP_TABS					= "APP TABS" ;
ZaAppViewMgr.C_TREE						= "TREE";
//ZaAppViewMgr.C_TREE_FOOTER				= "TREE FOOTER";
ZaAppViewMgr.C_TOOLBAR_TOP				= "TOP TOOLBAR";
ZaAppViewMgr.C_APP_CONTENT				= "APP CONTENT";
ZaAppViewMgr.C_STATUS					= "STATUS";
ZaAppViewMgr.C_SASH						= "SASH";
ZaAppViewMgr.C_LOGIN_MESSAGE            = "LOGIN_MESSAGE" ;

// keys for getting container IDs
ZaAppViewMgr.CONT_ID_KEY = new Object();
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_BANNER]					= ZaSettings.SKIN_LOGO_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_SEARCH]					= ZaSettings.SKIN_SEARCH_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_SEARCH_BUILDER]			= ZaSettings.SKIN_SEARCH_BUILDER_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_SEARCH_BUILDER_TOOLBAR]	= ZaSettings.SKIN_SEARCH_BUILDER_TOOLBAR_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_SEARCH_BUILDER_SASH] = ZaSettings.SKIN_SEARCH_BUILDER_SASH_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_CURRENT_APP]			= ZaSettings.SKIN_CURRENT_APP_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_TREE]					= ZaSettings.SKIN_TREE_ID;
//ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_TREE_FOOTER]			= ZaSettings.SKIN_TREE_FOOTER_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_TOOLBAR_TOP]			= ZaSettings.SKIN_APP_TOP_TOOLBAR_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_APP_CONTENT]			= ZaSettings.SKIN_APP_MAIN_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_STATUS]					= ZaSettings.SKIN_STATUS_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_SASH]					= ZaSettings.SKIN_SASH_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_APP_TABS]				= ZaSettings.SKIN_APP_TABS_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_LOGIN_MESSAGE]	        = ZaSettings.SKIN_LOGIN_MSG_ID;

if (appNewUI) {
// components
ZaAppViewMgr.C_BANNER					= "BANNER";
ZaAppViewMgr.C_SEARCH					= "SEARCH";

ZaAppViewMgr.C_TREE_TOP				    = "TREE TOP";
ZaAppViewMgr.C_TREE						= "TREE";
ZaAppViewMgr.C_TREE_FOOTER				= "TREE FOOTER";

ZaAppViewMgr.C_APP_HEADER				= "APP HEADER";
ZaAppViewMgr.C_APP_CONTENT			    = "APP CONTENT";
ZaAppViewMgr.C_APP_FOOTER				= "APP FOOTER";

ZaAppViewMgr.C_TOOL_HEADER				= "TOOL HEADER";
ZaAppViewMgr.C_TOOL				        = "TOOL";
ZaAppViewMgr.C_TOOL_FOOTER				= "TOOL FOOTER";

// keys for getting container IDs
ZaAppViewMgr.CONT_ID_KEY = new Object();
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_BANNER]					= ZaSettings.SKIN_LOGO_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_SEARCH]					= ZaSettings.SKIN_SEARCH_ID;

ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_TREE_TOP]			    = ZaSettings.SKIN_TREE_TOP_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_TREE]					= ZaSettings.SKIN_TREE_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_TREE_FOOTER]			= ZaSettings.SKIN_TREE_FOOTER_ID;

ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_APP_HEADER]			    = ZaSettings.SKIN_APP_HEADER_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_APP_CONTENT]			= ZaSettings.SKIN_APP_MAIN_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_APP_FOOTER]			    = ZaSettings.SKIN_APP_MAIN_FOOTER_ID;

ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_TOOL_HEADER]			= ZaSettings.SKIN_TOOL_HEADER_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_TOOL]				    = ZaSettings.SKIN_TOOL_ID;
ZaAppViewMgr.CONT_ID_KEY[ZaAppViewMgr.C_TOOL_FOOTER]	        = ZaSettings.SKIN_TOOL_FOOTER_ID;
}

// Public methods
ZaAppViewMgr.prototype.toString = 
function() {
	return "ZaAppViewMgr";
}

ZaAppViewMgr.prototype.getShell = 
function() {
	return this._shell;
}

/**
* Returns the name of the app view currently being displayed.
*/
ZaAppViewMgr.prototype.getCurrentView =
function() {
	return this._currentView;
}

ZaAppViewMgr.prototype.getCurrentViewContent =
function() {
    var elements = this._views[this._currentView];
	var content = elements[ZaAppViewMgr.C_APP_CONTENT];
	return content;
}

ZaAppViewMgr.prototype.getViewContentById =
function(id) {
    var elements = this._views[id];
    if (!elements)
        return "";
	var content = elements[ZaAppViewMgr.C_APP_CONTENT];
	return content;
}
/**
* Creates an app view from the given components and puts it in an app container.
*
* @param viewName		the name of the view
* @param appName		the name of the owning app
* @param elements		an array of elements to display
* @return				the app view
*/
ZaAppViewMgr.prototype.createView =
function(viewId, elements) {
	this._views[viewId] = elements;
	this.addComponents(elements, false, true);
}

/**
* Makes the given view visible, pushing the previously visible one to the top of the
* hidden stack.
*
* @param viewId	the name of the app view to push
* @return			the id of the view that is displayed
*/
ZaAppViewMgr.prototype.pushView =
function(viewId) {
	// if same view, no need to go through hide/show
	if (viewId == this._currentView) {
		this._setTitle(viewId);
		return viewId;
	}

	this._setViewVisible(this._currentView, false);
	if (this._currentView && (this._currentView != viewId))
		this._hidden.push(this._currentView);

	this._removeFromHidden(viewId);
	var temp = this._lastView;
	this._lastView = this._currentView;
	this._currentView = viewId;

	this._setViewVisible(viewId, true);

	return viewId;
}

/**
* Hides the currently visible view, and makes the view on top of the hidden stack visible.
*
* @return		the id of the view that is displayed
*/
ZaAppViewMgr.prototype.popView =
function() {
	if (!this._currentView)
		throw new AjxException("no view to pop");

	this._setViewVisible(this._currentView,false);

	this._lastView = this._currentView;
	this._currentView = this._hidden.pop();

	if (!this._currentView)
		throw new AjxException("no view to show");
		
	this._removeFromHidden(this._currentView);

	this._setViewVisible(this._currentView, true);
	return this._currentView;
}

ZaAppViewMgr.prototype.removeHiddenView = 
function(viewId) {
	var cnt = this._hidden.length;
	for(var ix =0; ix< cnt;ix++) {
		if(this._hidden[ix] == viewId) {
			this._hidden.splice(ix,1);
		}
	}
}
/**
* Makes the given view visible, and clears the hidden stack.
*
* @param viewName	the name of a view
* @return			true if the view was set
*/
ZaAppViewMgr.prototype.setView =
function(viewName) {
//	DBG.println(AjxDebug.DBG1, "setView: " + viewName);
	var result = this.pushView(viewName);
    if (result)
		this._hidden = new Array();
	return result;
}

ZaAppViewMgr.prototype.addComponents =
function(components, doFit, noSetZ) {
	var list = new Array();
	for (var cid in components) {
		this._compList.push(cid);
		var comp = components[cid];
		this._components[cid] = comp;
		var htmlEl = comp.getHtmlElement();
		this._htmlEl[cid] = htmlEl;
		var contId = ZaSettings.get(ZaAppViewMgr.CONT_ID_KEY[cid]);
		var contEl = document.getElementById(contId);
		if(!contEl) {
			continue;
		}
		this._containers[cid] = contEl;
		if (Dwt.contains(contEl, htmlEl))
			throw new AjxException("element already added to container: " + cid);		
		Dwt.removeChildren(contEl);
		
		list.push(cid);
		
		if (!noSetZ)
			comp.zShow(true);

		if (cid == ZaAppViewMgr.C_SEARCH_BUILDER  || cid == ZaAppViewMgr.C_SEARCH_BUILDER_TOOLBAR ) {
			//this._components[ZaAppViewMgr.C_SEARCH_BUILDER_TOOLBAR].setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
			DBG.println(AjxDebug.DBG1, "Enforce Z-index to hidden " + cid) ;
			comp.zShow(false);
		}
                
        if (cid == ZaAppViewMgr.C_SEARCH_BUILDER_SASH){
                comp.zShow(false);
                if(this._sbSashSupported){
                     comp.registerCallback(this._sbAppSashCallback, this);
                }
                comp.setCursor("default");
        }
                
		if (cid == ZaAppViewMgr.C_SASH) {
			if (this._sashSupported){
				comp.registerCallback(this._appTreeSashCallback, this);
			}
			comp.setCursor("default");
		}
	}
	if (doFit)
		this._stickToGrid(list);
}
ZaAppViewMgr.prototype.showSearchBuilder =
function(visible) {
	DBG.println(AjxDebug.DBG1, "show search builder: " + visible);
	skin.showSearchBuilder(visible);
	this._components[ZaAppViewMgr.C_SEARCH_BUILDER_TOOLBAR].zShow(visible);
	this._components[ZaAppViewMgr.C_SEARCH_BUILDER].zShow(visible);
        this._components[ZaAppViewMgr.C_SEARCH_BUILDER_SASH].zShow(visible);

    if (visible) this._isAdvancedSearchBuilderDisplayed = true ;
   /* var list = [ZaAppViewMgr.C_SEARCH_BUILDER, ZaAppViewMgr.C_SEARCH_BUILDER_TOOLBAR,
                ZaAppViewMgr.C_LOGIN_MESSAGE,
                ZaAppViewMgr.C_CURRENT_APP, ZaAppViewMgr.C_APP_CHOOSER, ZaAppViewMgr.C_APP_TABS,
				ZaAppViewMgr.C_TREE,
				ZaAppViewMgr.C_TREE_FOOTER, ZaAppViewMgr.C_TOOLBAR_TOP, ZaAppViewMgr.C_APP_CONTENT];
	this._stickToGrid(list);*/
	this.fitAll();
	// search builder contains forms, and browsers have quirks around form fields and z-index
	if (!visible) {
		this._components[ZaAppViewMgr.C_SEARCH_BUILDER].setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
	}
};
ZaAppViewMgr.prototype.fitAll = function () {
    var list = [ZaAppViewMgr.C_SEARCH_BUILDER, ZaAppViewMgr.C_SEARCH_BUILDER_TOOLBAR, ZaAppViewMgr.C_SEARCH_BUILDER_SASH,
                ZaAppViewMgr.C_LOGIN_MESSAGE,
                ZaAppViewMgr.C_CURRENT_APP, /*ZaAppViewMgr.C_APP_CHOOSER,*/ ZaAppViewMgr.C_APP_TABS,
                ZaAppViewMgr.C_BANNER,
				ZaAppViewMgr.C_TREE,ZaAppViewMgr.C_SASH,
				/*ZaAppViewMgr.C_TREE_FOOTER,*/ ZaAppViewMgr.C_TOOLBAR_TOP, ZaAppViewMgr.C_APP_CONTENT];

    if (appNewUI) {
       list = [
            ZaAppViewMgr.C_BANNER,
            ZaAppViewMgr.C_SEARCH,

            ZaAppViewMgr.C_TREE_TOP	,
            ZaAppViewMgr.C_TREE	,
            ZaAppViewMgr.C_TREE_FOOTER ,

            ZaAppViewMgr.C_APP_HEADER,
            ZaAppViewMgr.C_APP_CONTENT,
            ZaAppViewMgr.C_APP_FOOTER,

            ZaAppViewMgr.C_TOOL_HEADER ,
            ZaAppViewMgr.C_TOOL ,
            ZaAppViewMgr.C_TOOL_FOOTER
       ];
    }
	this._stickToGrid(list);
}
ZaAppViewMgr.prototype._stickToGrid = 
function(components) {
	for (var i = 0; i < components.length; i++) {
		var cid = components[i];
		// don't resize logo image (it will tile) or reposition it (centered via style)
		//if (cid == ZaAppViewMgr.C_BANNER) continue;
		//DBG.println(AjxDebug.DBG3, "fitting to container: " + cid);
		var cont = this._containers[cid];
		if (cont) {
			var contBds = Dwt.getBounds(cont);
			var comp = this._components[cid];
			if (
				cid == ZaAppViewMgr.C_APP_CONTENT || 
				cid == ZaAppViewMgr.C_TOOLBAR_TOP  ) {
				// make sure we fit the component that's current
				var elements = this._views[this._currentView];
				comp = elements[cid];
			}
			if (comp && (comp.getZIndex() != Dwt.Z_HIDDEN)) {
                var y =  contBds.y ;
                var h =  contBds.height ;
                /*if (AjxEnv.isIE && (!this._isAdvancedSearchBuilderDisplayed)) {
                    //bug  22173: IE hacking. Seems that the banner image size screw the height in IE. Maybe a small banner image on IE is the final solution?
                    //Also the advanced Search Builder expand/collapse will also affect the display behavior. WEIRD! 
                    if ( cid == ZaAppViewMgr.C_TREE )  {
                        y += 8 ;
                        h -= 5 ;
                    }else if ( cid == ZaAppViewMgr.C_CURRENT_APP ) {
                        y += 5 ;
                    }
                }*/
				try {
                	comp.setBounds(contBds.x, y, contBds.width, h);
				} catch (ex) {
					ZaApp.getInstance().getCurrentController()._handleException(ex, "ZaAppViewMgr.prototype._stickToGrid", nul, false);
				}
                this._contBounds[cid] = contBds;

				//call the components resizeListener to rearrange the component layout
				if (comp._resizeListener) {
					comp._resizeListener();
				}
			}
		}
	}
	//this._debugShowMetrics(components);
}


// Removes a view from the hidden stack.
ZaAppViewMgr.prototype._removeFromHidden =
function(view) {
	var newHidden = new Array();
	for (var i = 0; i < this._hidden.length; i++)
		if (this._hidden[i] != view)
			newHidden.push(this._hidden[i]);
	this._hidden = newHidden;
}

// Listeners

// Handles shell resizing event.
ZaAppViewMgr.prototype._shellControlListener =
function(ev) {
	if (ev.oldWidth != ev.newWidth || ev.oldHeight != ev.newHeight) {
		this._shellSz.x = ev.newWidth;
		this._shellSz.y = ev.newHeight;
		var deltaWidth = ev.newWidth - ev.oldWidth;
		var deltaHeight = ev.newHeight - ev.oldHeight;
		DBG.println(AjxDebug.DBG1, "shell control event: dW = " + deltaWidth + ", dH = " + deltaHeight);
		if (this._isNewWindow) {
			
			//reset the tab group's tab size
			var tabs = this._views[this._currentView][ZaAppViewMgr.C_APP_TABS] ;
			if (tabs) 
				tabs.setSize(ev.newWidth, Dwt.DEFAULT);
				
			// reset width of top toolbar
			var topToolbar = this._views[this._currentView][ZaAppViewMgr.C_TOOLBAR_TOP];
			if (topToolbar)
				topToolbar.setSize(ev.newWidth, Dwt.DEFAULT);
				
				
			// make sure to remove height of top toolbar for height of app content
			var appContent = this._views[this._currentView][ZaAppViewMgr.C_APP_CONTENT];
			if (appContent)
				appContent.setSize(ev.newWidth, ev.newHeight - topToolbar.getH());
			
		} else {
			if (deltaHeight) {
				var list = [ZaAppViewMgr.C_APP_CHOOSER, ZaAppViewMgr.C_SASH, ZaAppViewMgr.C_APP_CONTENT,ZaAppViewMgr.C_TREE/*, ZaAppViewMgr.C_STATUS*/];
			    if (appNewUI) {
			       list = [
			            ZaAppViewMgr.C_TREE_TOP	,
			            ZaAppViewMgr.C_TREE	,
			            ZaAppViewMgr.C_TREE_FOOTER ,
			
			            ZaAppViewMgr.C_APP_HEADER,
			            ZaAppViewMgr.C_APP_CONTENT,
			            ZaAppViewMgr.C_APP_FOOTER,
			
			            ZaAppViewMgr.C_TOOL_HEADER ,
			            ZaAppViewMgr.C_TOOL ,
			            ZaAppViewMgr.C_TOOL_FOOTER
			       ];
			    }
				this._stickToGrid(list);
			}
			if (deltaWidth) {
				var list = [ZaAppViewMgr.C_BANNER, ZaAppViewMgr.C_APP_TABS, ZaAppViewMgr.C_LOGIN_MESSAGE,
							ZaAppViewMgr.C_TOOLBAR_TOP, ZaAppViewMgr.C_APP_CONTENT, 
							ZaAppViewMgr.C_SEARCH,
							ZaAppViewMgr.C_SEARCH_BUILDER, ZaAppViewMgr.C_SEARCH_BUILDER_TOOLBAR, ZaAppViewMgr.C_SEARCH_BUILDER_SASH];
    if (appNewUI) {
       list = [
            ZaAppViewMgr.C_BANNER,
            ZaAppViewMgr.C_SEARCH,

            ZaAppViewMgr.C_APP_HEADER,
            ZaAppViewMgr.C_APP_CONTENT,
            ZaAppViewMgr.C_APP_FOOTER,

            ZaAppViewMgr.C_TOOL_HEADER ,
            ZaAppViewMgr.C_TOOL ,
            ZaAppViewMgr.C_TOOL_FOOTER
       ];
    }
				this._stickToGrid(list);
			}
		}
	}
}


// Makes elements visible/hidden by locating them off- or onscreen and setting
// their z-index.
ZaAppViewMgr.prototype._setViewVisible =
function(viewId, show) {
	var elements = this._views[viewId];
	if (show) {
		var list = new Array();
		for (var cid in elements) {
			list.push(cid);
			elements[cid].zShow(true);
		}
		this._stickToGrid(list);
        ////// May be need to changed to app header...
        if (!appNewUI)
		    this._setTitle(viewId);
        else
            this._setCurrentBar(viewId);
	} else {
		for (var cid in elements) {
			elements[cid].setLocation(Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
			elements[cid].zShow(false);
		}
	}
}

ZaAppViewMgr.prototype._setTitle =
function(viewId) {
	var elements = this._views[viewId];
	var content = elements[ZaAppViewMgr.C_APP_CONTENT];
	var title = "" ;
	if (content && content.getTitle) {
		title = content.getTitle();
		if(this._components[ZaAppViewMgr.C_CURRENT_APP] && this._components[ZaAppViewMgr.C_CURRENT_APP].setCurrentAppLabel ) {
			this._components[ZaAppViewMgr.C_CURRENT_APP].setCurrentAppLabel (title);		
		}
		Dwt.setTitle(title ? ZabMsg.zimbraTitle + ": " + title : ZabMsg.zimbraTitle);
	}
}

ZaAppViewMgr.prototype._setCurrentBar =
function(viewId) {
    var view = this.getViewContentById(viewId);
    var viewController = ZaApp.getInstance().getControllerById(viewId);
	var popUpOperations = "" ;

    var typeImg = "";
	if (viewController && viewController.getPopUpOperation) {
        popUpOperations = viewController.getPopUpOperation();
	}

    if (view && view.getBarImage) {
        typeImg = view.getBarImage();
    }
    if(this._components[ZaAppViewMgr.C_APP_HEADER]) {
        if (this._components[ZaAppViewMgr.C_APP_HEADER].updateMenu)
		    this._components[ZaAppViewMgr.C_APP_HEADER].updateMenu (popUpOperations, viewController._popupOrder);
        if (this._components[ZaAppViewMgr.C_APP_HEADER].setTypeImg && typeImg)
            this._components[ZaAppViewMgr.C_APP_HEADER].setTypeImg(typeImg);

        var appBarOperations = [];
        if (viewController && viewController.getAppBarAction) {
           appBarOperations = viewController.getAppBarAction();
        }

        var appBarOrder = [];

        if (viewController && viewController.getAppBarOrder)
            appBarOrder = viewController.getAppBarOrder();

        if (this._components[ZaAppViewMgr.C_APP_HEADER].setActionButton)
            this._components[ZaAppViewMgr.C_APP_HEADER].setActionButton(appBarOperations, appBarOrder);
	}
}

// Handles app/tree movement. If you move the sash beyond the max or min width,
// pins to the respective width.
ZaAppViewMgr.prototype._appTreeSashCallback =
function(delta) {
	if (!window.skin) { return; }

	// ask skin for width of tree, rather than hard-coding name of tree div here
	var currentWidth = skin.getTreeWidth();
	if (!currentWidth) { return 0; }

	//DBG.println(AjxDebug.DBG3, "************ sash callback **************");
	//DBG.println(AjxDebug.DBG3, "delta = " + delta);
	//DBG.println(AjxDebug.DBG3, "shell width = " + this._shellSz.x);
	//DBG.println(AjxDebug.DBG3, "current width = " + currentWidth);

	// MOW: get the min/max sizes from the skin.hints
	if (!this.treeMinSize) {
		this.treeMinSize = window.skin.hints.tree.minWidth || 150;
		this.treeMaxSize = window.skin.hints.tree.maxWidth || 300;
	}

	// pin the resize to the minimum and maximum allowable
	if (currentWidth + delta > this.treeMaxSize) {
		delta = Math.max(0, this.treeMaxSize - currentWidth);
	}
	if (currentWidth + delta < this.treeMinSize) {
		delta = Math.min(0, this.treeMinSize - currentWidth);
	}

	// tell skin to resize the tree to keep the separation of tree/skin clean
	var newTreeWidth = currentWidth + delta;

	skin.setTreeWidth(newTreeWidth);

	// call fitAll() on timeout, so we dont get into a problem w/ sash movement code
	var me = this;
	setTimeout(function(){me.fitAll(true)},0);
	return delta;
};


ZaAppViewMgr.prototype._sbAppSashCallback = function(delta) {
	if (!window.skin) {
           return;
        }
 
        var currentHeight = skin.getSBHeight();
        if (!currentHeight){
           return 0;
        }
        
        if (!this.sbMinSize){
           this.sbMinSize = window.skin.hints.searchBuilder.minHeight || 50;
           this.sbMoveSize = currentHeight; //record the orginal height, the search builder is not allowed to big than its original height; 
        }
        
        if (currentHeight + delta > this.sbMoveSize){
            return 0;
        }

        if (currentHeight + delta < this.sbMinSize){
            delta = Math.min (0, this.sbMinSize - currentHeight);
        }

        var newSBHeight = currentHeight + delta; 
        skin.setSBHeight(newSBHeight);
        var me = this;
        setTimeout(function(){me.fitAll(true)}, 0);
        return delta;
}
