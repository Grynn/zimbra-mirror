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

function DwtMovable() {}

DwtMovable.init = 
function(control, rootEl, threshX, threshY, callbackFunc, callbackObj) {

   control._rootEl = rootEl;
   var htmlElement = control.getHtmlElement();
    
   	htmlElement.style.cursor = "move";
	control._threshX = (threshX > 0) ? threshX : 1;
	control._threshY = (threshY > 0) ? threshY : 1;

	control._captureObj = new DwtMouseEventCapture(control, DwtMovable._mouseOverHdlr,
			DwtMovable._mouseDownHdlr, DwtMovable._mouseMoveHdlr, 
			DwtMovable._mouseUpHdlr, DwtMovable._mouseOutHdlr);
	control.setHandler(DwtEvent.ONMOUSEDOWN, DwtMovable._mouseDownHdlr);
	control.setHandler(DwtEvent.ONMOUSEOVER, DwtMovable._mouseOverHdlr);
	control.setHandler(DwtEvent.ONMOUSEOUT, DwtMovable._mouseOutHdlr);
	control._callbackFunc = callbackFunc;
	control._callbackObj = callbackObj;	
}

DwtMovable._mouseOverHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtMovable._mouseDownHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	if (mouseEv.button != DwtMouseEvent.LEFT) {
		DwtUiEvent.setBehaviour(ev, true, false);
		return false;
	}
	var control = mouseEv.dwtObj;
	if (control._callbackFunc != null) {
		control._captureObj.capture();
		control._startDoc = {x: mouseEv.docX, y: mouseEv.docY};
		control._startCoord = control._rootEl.getLocation();
	}
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtMovable._mouseMoveHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	
	var control = DwtMouseEventCapture.getTargetObj();
	
	deltaX = mouseEv.docX - control._startDoc.x;
	deltaY = mouseEv.docY - control._startDoc.y;

	if (Math.abs(deltaX) >= control._threshX || Math.abs(deltaY) >= control._threshY) {
		if (control._callbackObj != null)
			delta = control._callbackFunc.call(control._callbackObj, {x: deltaX, y: deltaY});
		else 
			delta = control._callbackFunc({x: deltaX, y: deltaY});
		// If movement happened, then shift our location by the actual amount of movement
		if (delta.x != 0 || delta.y != 0) {
        		control._rootEl.setLocation(control._startCoord.x + delta.x, control._startCoord.y + delta.y);
		}
	}
		
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtMovable._mouseUpHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);	
	if (mouseEv.button != DwtMouseEvent.LEFT) {
		DwtUiEvent.setBehaviour(ev, true, false);
		return false;
	}
	
	if (DwtMouseEventCapture.getTargetObj()._callbackFunc != null)
		DwtMouseEventCapture.getCaptureObj().release();
		
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}

DwtMovable._mouseOutHdlr =
function(ev) {
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev);
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;	
}
