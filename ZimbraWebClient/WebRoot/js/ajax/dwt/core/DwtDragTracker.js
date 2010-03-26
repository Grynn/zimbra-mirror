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
 * 
 * @private
 */
DwtDragTracker = function() {}

/**
 * Initializes the tracker.
 * 
 * @param {DwtControl}	control        the control that can be moved/dragged
 * @param {number}	[threshX=1]        the minimum number of X pixels before we move
 * @param {number}	[threshY=1]        the minimum number of Y pixels before we move
 * @param {function}	callbackFunc   callback function
 * @param callbackObj    object for callback
 * @param	userData		the user data
 * 
 */
DwtDragTracker.init = 
function(control, style, threshX, threshY, callbackFunc, callbackObj, userData) {

    var ctxt = control._dragTrackerContext = {};
    var htmlElement = control.getHtmlElement();
    
    if (style) htmlElement.style.cursor = style;
    
   	ctxt.style = style;
	ctxt.threshX = (threshX > 0) ? threshX : 1;
	ctxt.threshY = (threshY > 0) ? threshY : 1;
	ctxt.data = { delta: {}, userData: userData};

	ctxt.captureObj = new DwtMouseEventCapture({
		targetObj:control,
		mouseOverHdlr:DwtDragTracker._mouseOverHdlr,
		mouseDownHdlr:DwtDragTracker._mouseDownHdlr,
		mouseMoveHdlr:DwtDragTracker._mouseMoveHdlr,
		mouseUpHdlr:DwtDragTracker._mouseUpHdlr,
		mouseOutHdlr:DwtDragTracker._mouseOutHdlr
	});

	control.setHandler(DwtEvent.ONMOUSEDOWN, DwtDragTracker._mouseDownHdlr);
	control.setHandler(DwtEvent.ONMOUSEOVER, DwtDragTracker._mouseOverHdlr);
	control.setHandler(DwtEvent.ONMOUSEOUT, DwtDragTracker._mouseOutHdlr);
	ctxt.callbackFunc = callbackFunc;
	ctxt.callbackObj = callbackObj;	
}

DwtDragTracker.STYLE_NONE = "auto";
DwtDragTracker.STYLE_MOVE = "move";
DwtDragTracker.STYLE_RESIZE_NORTHWEST = "nw-resize";
DwtDragTracker.STYLE_RESIZE_NORTH = "n-resize";
DwtDragTracker.STYLE_RESIZE_NORTHEAST = "ne-resize";
DwtDragTracker.STYLE_RESIZE_WEST = "w-resize";
DwtDragTracker.STYLE_RESIZE_EAST = "e-resize";
DwtDragTracker.STYLE_RESIZE_SOUTHWEST = "sw-resize";
DwtDragTracker.STYLE_RESIZE_SOUTH = "s-resize";
DwtDragTracker.STYLE_RESIZE_SOUTHEAST = "se-resize";

DwtDragTracker.STATE_START = 1;
DwtDragTracker.STATE_DRAGGING = 2;
DwtDragTracker.STATE_END = 3;

DwtDragTracker._mouseOverHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtDragTracker._mouseDownHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev, true);	
	if (mouseEv.button != DwtMouseEvent.LEFT) {
		DwtUiEvent.setBehaviour(ev, true, false);
		return false;
	}
	var control = mouseEv.dwtObj;
	if (control && control._dragTrackerContext) {
        var ctxt = control._dragTrackerContext;
        	if (ctxt.callbackFunc != null) {
				ctxt.oldCapture = DwtMouseEventCapture.getCaptureObj();
				if (ctxt.oldCapture) {
					ctxt.oldCapture.release();
				}
        		ctxt.captureObj.capture();
        		ctxt.data.startDoc = {x: mouseEv.docX, y: mouseEv.docY};
        		ctxt.data.state = DwtDragTracker.STATE_START;
             DwtDragTracker._doCallback(ctxt, mouseEv);
        	}
   	}
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtDragTracker._doCallback =
function(ctxt, mouseEv) {
	ctxt.data.mouseEv = mouseEv;
	if (ctxt.callbackObj != null)
		ctxt.callbackFunc.call(ctxt.callbackObj, ctxt.data);
	else 
		ctxt.callbackFunc(ctxt.data);
	ctxt.data.mouseEv = null;
}

DwtDragTracker._mouseMoveHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	
	var control = DwtMouseEventCapture.getTargetObj();
    var ctxt = control._dragTrackerContext;
    var data = ctxt.data;
	    
	data.delta.x = mouseEv.docX - data.startDoc.x;
	data.delta.y = mouseEv.docY - data.startDoc.y;
	
	if (Math.abs(data.delta.x) >= ctxt.threshX || Math.abs(data.delta.y) >= ctxt.threshY) {
        data.prevState = data.state;
        data.state = DwtDragTracker.STATE_DRAGGING;
	    DwtDragTracker._doCallback(ctxt, mouseEv);
	}
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtDragTracker._mouseUpHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	if (mouseEv.button != DwtMouseEvent.LEFT) {
		DwtUiEvent.setBehaviour(ev, true, false);
		return false;
	}
	
	var ctxt = DwtMouseEventCapture.getTargetObj()._dragTrackerContext;
	if (ctxt) {
        	if (ctxt.callbackFunc != null)
        		DwtMouseEventCapture.getCaptureObj().release();
			if (ctxt.oldCapture) {
				ctxt.oldCapture.capture();
				ctxt.oldCapture = null;
			}
        	ctxt.data.state = DwtDragTracker.STATE_END;
        DwtDragTracker._doCallback(ctxt, mouseEv);
	}
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtDragTracker._mouseOutHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	
	mouseEv.setFromDhtmlEvent(ev);
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}
