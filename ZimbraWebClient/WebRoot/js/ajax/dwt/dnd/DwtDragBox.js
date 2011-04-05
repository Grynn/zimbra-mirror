/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2011 Zimbra, Inc.
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
 * @constructor
 * @class
 * A drag box is registered with a control to indicate that the control supports the
 * presence of an elastic box created by clicking and dragging, and typically used to
 * select visual objects within a space.
 * <p>
 * Application developers instantiate {@link DwtDragBox} and register it with the control
 * which is to be draggable (via {@link DwtControl.setDragBox}). The
 * application should then register a listener with the {@link DwtDragBox}. This way
 * when drag events occur the application will be notified and may act on them 
 * accordingly.
 * </p>
 * 
 * @author Conrad Damon
 * 
 * @see DwtDragEvent
 * @see DwtControl
 * @see DwtControl#setDragBox
 */
DwtDragBox = function() {
	this.__evtMgr = new AjxEventMgr();
};

/** @private */
DwtDragBox.__DRAG_LISTENER = "DwtDragBox.__DRAG_LISTENER";

/** @private */
DwtDragBox.__dragEvent = new DwtDragEvent();

/**
 * Returns a string representation of this object.
 * 
 * @return {string}	a string representation of this object
 */
DwtDragBox.prototype.toString = 
function() {
	return "DwtDragBox";
};


/**
 * Registers a listener for <i>DwtDragEvent</i> events.
 *
 * @param {AjxListener} dragBoxListener Listener to be registered
 * 
 * @see DwtDragEvent
 * @see AjxListener
 * @see #removeDragListener
 */
DwtDragBox.prototype.addDragListener =
function(dragBoxListener) {
	this.__evtMgr.addListener(DwtDragBox.__DRAG_LISTENER, dragBoxListener);
};

/**
 * Removes a registered event listener.
 * 
 * @param {AjxListener} dragBoxListener Listener to be removed
 * 
 * @see AjxListener
 * @see #addDragListener
 */
DwtDragBox.prototype.removeDragListener =
function(dragBoxListener) {
	this.__evtMgr.removeListener(DwtDragBox.__DRAG_LISTENER, dragBoxListener);
};

// The following methods are called by DwtControl during the drag lifecycle 

DwtDragBox.prototype._setStart =
function(mouseEv, srcControl) {

	this._startX = mouseEv.docX;
	this._startY = mouseEv.docY;
	this._dragObj = DwtDragBox.__dragEvent.srcControl = srcControl;
	DwtDragBox.__dragEvent.action = DwtDragEvent.DRAG_INIT;
	DwtDragBox.__dragEvent.target = mouseEv.target;
	return (this.__evtMgr.notifyListeners(DwtDragBox.__DRAG_LISTENER, DwtDragBox.__dragEvent) !== false);
};

DwtDragBox.prototype._beginDrag =
function(srcControl) {

	srcControl._dragging = DwtControl._DRAGGING;
	DwtDragBox.__dragEvent.srcControl = srcControl;
	DwtDragBox.__dragEvent.action = DwtDragEvent.DRAG_START;
	this.__evtMgr.notifyListeners(DwtDragBox.__DRAG_LISTENER, DwtDragBox.__dragEvent);
};

DwtDragBox.prototype._dragMove =
function(mouseEv, srcControl) {

	var deltaX = mouseEv.docX - this._startX;
	var deltaY = mouseEv.docY - this._startY;
	var locX = (deltaX > 0) ? this._startX : mouseEv.docX;
	var locY = (deltaY > 0) ? this._startY : mouseEv.docY;

	var box = srcControl.getDragSelectionBox();
	Dwt.setLocation(box, locX, locY);
	Dwt.setSize(box, Math.abs(deltaX), Math.abs(deltaY));

	DwtDragBox.__dragEvent.srcControl = srcControl;
	DwtDragBox.__dragEvent.action = DwtDragEvent.DRAG_MOVE;
	this.__evtMgr.notifyListeners(DwtDragBox.__DRAG_LISTENER, DwtDragBox.__dragEvent);
};

DwtDragBox.prototype._endDrag =
function(srcControl) {

	srcControl._dragging = DwtControl._NO_DRAG;
	DwtDragBox.__dragEvent.action = DwtDragEvent.DRAG_END;
	if (!this.__evtMgr.notifyListeners(DwtDragBox.__DRAG_LISTENER, DwtDragBox.__dragEvent)) {
		srcControl.destroyDragSelectionBox();
	}
	this._dragObj = null;
};

/*
 *  return starting X position
 *  @return {int} starting X position
 */
DwtDragBox.prototype.getStartX =
function() {
    return this._startX;
};

/*  return starting Y position
 *  @return {int} starting Y position
 */
DwtDragBox.prototype.getStartY =
function() {
    return this._startY;
};