/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010, 2012 VMware, Inc.
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
* @class
* This static class enables entities (for example, {@link DwtDialog}s) to be dragged around within
* an application window. The code is basically the same as in dom-drag.js from www.youngpup.net
*
* @author Ross Dargahi
* 
* @private
*/
DwtDraggable = function() {
}

DwtDraggable.dragEl = null;

/**
 * Initializes dragging for <code>dragEl</code>
 * 
 * @param {HTMLElement} dragEl 	the element being dragged, can also be a handle e.g. the
 * 		title bar in a dialog
 * @param {HTMLElement} [rootEl]	the actual element that will be moved. This will be a
 * 		parent element of <i>dragEl</i>
 * @param {number} [minX] 	the minimum x coord to which we can drag
 * @param {number} [maxX] 	the maximum x coord to which we can drag
 * @param {number} [minY] 	the minimum y coord to which we can drag
 * @param {number} [maxY] 	the maximum x coord to which we can drag
 * @param {AjxCallback} dragStartCB	the callback that is called when dragging is started
 * @param {AjxCallback}dragCB		the callback that is called when dragging
 * @param {AjxCallback}dragEndCB	the callback that is called when dragging is ended
 * @param {boolean} [swapHorizRef]	if <code>true</code>, then mouse motion to the right will move element left
 * @param {boolean} [swapVertRef]		if <code>true</code>, then mouse motion to the bottom will move element up
 * @param {function} [fXMapper] 		the function that overrides this classes x coordinate transformations
 * @param {function} [fYMapper] 		the function that overrides this classes y coordinate transformations
 *
 */
DwtDraggable.init = 
function(dragEl, rootEl, minX, maxX, minY, maxY, dragStartCB, dragCB, dragEndCB, 
		 swapHorizRef, swapVertRef, fXMapper, fYMapper) {
	dragEl.onmousedown = DwtDraggable.__start;

	dragEl.__hMode = swapHorizRef ? false : true;
	dragEl.__vMode = swapVertRef ? false : true;

	dragEl.__root = (rootEl && rootEl != null) ? rootEl : dragEl ;

	if (dragEl.__hMode && isNaN(parseInt(dragEl.__root.style.left))) 
		dragEl.__root.style.left = "0px";
	if (dragEl.__vMode && isNaN(parseInt(dragEl.__root.style.top))) 
		dragEl.__root.style.top = "0px";
		
	if (!dragEl.__hMode && isNaN(parseInt(dragEl.__root.style.right))) 
		dragEl.__root.style.right = "0px";
	if (!dragEl.__vMode && isNaN(parseInt(dragEl.__root.style.bottom))) 
		dragEl.__root.style.bottom = "0px";

	dragEl.__minX = (typeof minX != 'undefined') ? minX : null;
	dragEl.__minY = (typeof minY != 'undefined') ? minY : null;
	dragEl.__maxX = (typeof maxX != 'undefined') ? maxX : null;
	dragEl.__maxY = (typeof maxY != 'undefined') ? maxY : null;

	dragEl.__xMapper = fXMapper ? fXMapper : null;
	dragEl.__yMapper = fYMapper ? fYMapper : null;

	dragEl.__root.onDragStart = dragStartCB
	dragEl.__root.onDragEnd = dragEndCB
	dragEl.__root.onDrag = dragCB;
};

/**
 * Sets the minimum and maximum drag boundries
 * 
 * @param {HTMLElement} dragEl Element being dragged, can also be a handle e.g. the
 * 		title bar in a dialog
 * @param {number} minX 	the minimum x coordinate
 * @param {number} maxX 	the maximum x coordinate
 * @param {number} minY 	the minimum y coordinate
 * @param {number} maxY 	the maximum y coordinate
 */
DwtDraggable.setDragBoundaries =
function (dragEl ,minX, maxX, minY, maxY) {
	if (dragEl != null) {
		if (minX != null) dragEl.__minX = minX;
		if (maxX != null) dragEl.__maxX = maxX;
		if (minY != null) dragEl.__minY = minY;
		if (maxY != null) dragEl.__maxY = maxY;
	}
};

/** @private */
DwtDraggable.__start =
function(e)	{
	var dragEl = DwtDraggable.dragEl = this;
	e = DwtDraggable.__fixE(e);
	var x = parseInt(dragEl.__hMode ? dragEl.__root.style.left : dragEl.__root.style.right );
	var y = parseInt(dragEl.__vMode ? dragEl.__root.style.top  : dragEl.__root.style.bottom);
	if (dragEl.__root.onDragStart)
		dragEl.__root.onDragStart.run([x, y]);

	dragEl.__lastMouseX = e.clientX;
	dragEl.__lastMouseY = e.clientY;

	if (dragEl.__hMode) {
		if (dragEl.__minX != null)	
			dragEl.__minMouseX = e.clientX - x + dragEl.__minX;
		if (dragEl.__maxX != null)
			dragEl.__maxMouseX = dragEl.__minMouseX + dragEl.__maxX - dragEl.__minX;
	} else {
		if (dragEl.__minX != null)
			dragEl.__maxMouseX = -dragEl.__minX + e.clientX + x;
		if (dragEl.__maxX != null)
			dragEl.__minMouseX = -dragEl.__maxX + e.clientX + x;
	}

	if (dragEl.__vMode) {
		if (dragEl.__minY != null)
			dragEl.__minMouseY = e.clientY - y + dragEl.__minY;
		if (dragEl.__maxY != null)
			dragEl.__maxMouseY = dragEl.__minMouseY + dragEl.__maxY - dragEl.__minY;
	} else {
		if (dragEl.__minY != null)
			dragEl.__maxMouseY = -dragEl.__minY + e.clientY + y;
		if (dragEl.__maxY != null)
			dragEl.__minMouseY = -dragEl.__maxY + e.clientY + y;
	}

	document.onmousemove = DwtDraggable.__drag;
	document.onmouseup = DwtDraggable.__end;

	return false;
};

/** @private */
DwtDraggable.__drag =
function(e)	{
	e = DwtDraggable.__fixE(e);
	var dragEl = DwtDraggable.dragEl;

	var ey	= e.clientY;
	var ex	= e.clientX;
	var x = parseInt(dragEl.__hMode ? dragEl.__root.style.left : dragEl.__root.style.right );
	var y = parseInt(dragEl.__vMode ? dragEl.__root.style.top  : dragEl.__root.style.bottom);
	var nx, ny;

	if (!dragEl.__xMapper) {
		if (dragEl.__minX != null)
			ex = dragEl.__hMode ? Math.max(ex, dragEl.__minMouseX) : Math.min(ex, dragEl.__maxMouseX);
		if (dragEl.__maxX != null)
			ex = dragEl.__hMode ? Math.min(ex, dragEl.__maxMouseX) : Math.max(ex, dragEl.__minMouseX);
		nx = x + ((ex - dragEl.__lastMouseX) * (dragEl.__hMode ? 1 : -1));
	} else {
		nx = dragEl.__xMapper(x, ex);
	}

	if (!dragEl.__yMapper) {
		if (dragEl.__minY != null)
			ey = dragEl.__vMode ? Math.max(ey, dragEl.__minMouseY) : Math.min(ey, dragEl.__maxMouseY);
		if (dragEl.__maxY != null)
			ey = dragEl.__vMode ? Math.min(ey, dragEl.__maxMouseY) : Math.max(ey, dragEl.__minMouseY);
		ny = y + ((ey - dragEl.__lastMouseY) * (dragEl.__vMode ? 1 : -1));
	} else {
		ny = dragEl.__yMapper(y, ey);
	}

	DwtDraggable.dragEl.__root.style[dragEl.__hMode ? "left" : "right"] = nx + "px";
	DwtDraggable.dragEl.__root.style[dragEl.__vMode ? "top" : "bottom"] = ny + "px";
	DwtDraggable.dragEl.__lastMouseX = ex;
	DwtDraggable.dragEl.__lastMouseY = ey;

	if (DwtDraggable.dragEl.__root.onDrag)
		DwtDraggable.dragEl.__root.onDrag.run([nx, ny]);
		
	return false;
};

/** @private */
DwtDraggable.__end =
function() {
	document.onmousemove = null;
	document.onmouseup   = null;
	if (DwtDraggable.dragEl.__root.onDragEnd)
		DwtDraggable.dragEl.__root.onDragEnd.run([parseInt(DwtDraggable.dragEl.__root.style[DwtDraggable.dragEl.__hMode ? "left" : "right"]), 
											 	  parseInt(DwtDraggable.dragEl.__root.style[DwtDraggable.dragEl.__vMode ? "top" : "bottom"])]);
	DwtDraggable.dragEl = null;
};

/** @private */
DwtDraggable.__fixE =
function(e) {
	if (typeof e == 'undefined')
		e = window.event;
	if (!AjxEnv.isWebKitBased) {
		if (typeof e.layerX == 'undefined')
			e.layerX = e.offsetX;
		if (typeof e.layerY == 'undefined')
			e.layerY = e.offsetY;
	}
	return e;
};
