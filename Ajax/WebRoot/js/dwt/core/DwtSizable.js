/*
* ***** BEGIN LICENSE BLOCK *****
* Version: MPL 1.1
*
* The contents of this file are subject to the Mozilla Public
* License Version 1.1 ("License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.zimbra.com/license
*
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
* the License for the specific language governing rights and limitations
* under the License.
*
* The Original Code is: Zimbra AJAX Toolkit.
*
* The Initial Developer of the Original Code is Zimbra, Inc.
* Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
* All Rights Reserved.
*
* Contributor(s):
*
* ***** END LICENSE BLOCK *****
*/

function DwtSizable() {}

/**
* @param control        the DwtControl that can be moved/dragged
* @param rootControl    the DwtControl that will actually be moved
* @param threshX        mimimum number of X pixels before we move (default 1)
* @param threshY        mimimum number of X pixels before we move (default 1)
* @param callbackFunc   callback function to veto move
* @param callbackObj    object for callback
*/
DwtSizable.init = 
function(control, rootControl, threshX, threshY, callbackFunc, callbackObj) {

    var ctxt = control._movableContext = {};
    ctxt._rootControl = rootControl;
    var htmlElement = control.getHtmlElement();
    
   	htmlElement.style.cursor = "se-resize";
	ctxt._threshX = (threshX > 0) ? threshX : 1;
	ctxt._threshY = (threshY > 0) ? threshY : 1;

	ctxt._captureObj = new DwtMouseEventCapture(control, DwtSizable._mouseOverHdlr,
			DwtSizable._mouseDownHdlr, DwtSizable._mouseMoveHdlr, 
			DwtSizable._mouseUpHdlr, DwtSizable._mouseOutHdlr);
	control.setHandler(DwtEvent.ONMOUSEDOWN, DwtSizable._mouseDownHdlr);
	control.setHandler(DwtEvent.ONMOUSEOVER, DwtSizable._mouseOverHdlr);
	control.setHandler(DwtEvent.ONMOUSEOUT, DwtSizable._mouseOutHdlr);
	ctxt._callbackFunc = callbackFunc;
	ctxt._callbackObj = callbackObj;	
}

DwtSizable.STATE_SIZE_START = 1;
DwtSizable.STATE_MOVING = 2;
DwtSizable.STATE_SIZE_END = 3;

DwtSizable._mouseOverHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtSizable._mouseDownHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	if (mouseEv.button != DwtMouseEvent.LEFT) {
		DwtUiEvent.setBehaviour(ev, true, false);
		return false;
	}
	var control = mouseEv.dwtObj;
	if (control && control._movableContext) {
        var ctxt = control._movableContext;
        	if (ctxt._callbackFunc != null) {
        		ctxt._captureObj.capture();
        		ctxt._startDoc = {x: mouseEv.docX, y: mouseEv.docY};
        		ctxt._startSize = ctxt._rootControl.getSize();
            DwtSizable._doCallback(ctxt,{start: ctxt._startCoord, state: DwtSizable.STATE_SIZE_START});
        	}
   	}
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtSizable._doCallback =
function(ctxt, data) {
	if (ctxt._callbackObj != null)
		return ctxt._callbackFunc.call(ctxt._callbackObj, data);
	else 
		return ctxt._callbackFunc(data);
}

DwtSizable._mouseMoveHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	
	var control = DwtMouseEventCapture.getTargetObj();
    var ctxt = control._movableContext;
	    
	deltaX = mouseEv.docX - ctxt._startDoc.x;
	deltaY = mouseEv.docY - ctxt._startDoc.y;
    
	if (Math.abs(deltaX) >= ctxt._threshX || Math.abs(deltaY) >= ctxt._threshY) {
	    var data = DwtSizable._doCallback(ctxt,
	         {delta: {x: deltaX, y: deltaY}, start: ctxt._startSize, state: DwtSizable.STATE_SIZING});
		// If movement happened, then shift our location by the actual amount of movement
		if (data.delta.x != 0 || data.delta.y != 0) {
        		ctxt._rootControl.setSize(ctxt._startSize.x + data.delta.x, ctxt._startSize.y + data.delta.y);
		}
	}
		
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtSizable._mouseUpHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	if (mouseEv.button != DwtMouseEvent.LEFT) {
		DwtUiEvent.setBehaviour(ev, true, false);
		return false;
	}
	
	var ctxt = DwtMouseEventCapture.getTargetObj()._movableContext;
	if (ctxt) {
        	if (ctxt._callbackFunc != null)
        		DwtMouseEventCapture.getCaptureObj().release();
        DwtSizable._doCallback(ctxt,{start: ctxt._startCoord, state: DwtSizable.STATE_SIZE_END});
	}
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtSizable._mouseOutHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}
