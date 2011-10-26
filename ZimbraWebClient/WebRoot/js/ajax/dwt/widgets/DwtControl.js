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
 * @overview
 * This file contains a Dwt control.
 */

/**
 * Creates a control.
 * @class
 * This class is the root class of the Dwt component hierarchy. All
 * Dwt components either directly or indirectly inherit from this class.
 * <p>
 * A {@link DwtControl} may also be directly instantiated. In this case it is essentially
 * a div into which any content may be "drawn"
 * <p>
 * A control may be created in "deferred" mode, meaning that the UI portion of the control
 * will be created "Just In Time". This is useful for widgets which may want to defer construction
 * of elements (e.g. {@link DwtTreeItem}) until such time as is needed, in the interest of efficiency.
 * Note that if the control is a child of the shell, it won't become visible until its z-index is set.
 *
 * <h4>Events</h4><ul>
 * <li><i>DwtEvent.CONTROL</i></li>
 * <li><i>DwtEvent.DISPOSE</i></li>
 * <li><i>DwtEvent.HOVEROVER</i></li>
 * <li><i>DwtEvent.HOVEROUT</i></li>
 * <li><i>DwtEvent.ONCONTEXTMENU</i></li>
 * <li><i>DwtEvent.ONCLICK</i></li>
 * <li><i>DwtEvent.ONDBLCLICK</i></li>
 * <li><i>DwtEvent.ONFOCUS</i></li>
 * <li><i>DwtEvent.ONBLUR</i></li>
 * <li><i>DwtEvent.ONMOUSEDOWN</i></li>
 * <li><i>DwtEvent.ONMOUSEENTER</i></li>
 * <li><i>DwtEvent.ONMOUSELEAVE</i></li>
 * <li><i>DwtEvent.ONMOUSEMOVE</i></li>
 * <li><i>DwtEvent.ONMOUSEOUT</i></li>
 * <li><i>DwtEvent.ONMOUSEOVER</i></li>
 * <li><i>DwtEvent.ONMOUSEUP</i></li>
 * <li><i>DwtEvent.ONMOUSEWHEEL</i></li>
 * <li><i>DwtEvent.ONSELECTSTART</i></li>
 * </ul>
 *
 * @author Ross Dargahi
 * 
 * @param {hash}		params			a hash of parameters
 * @param	{DwtComposite}	parent		the parent widget, except in the case of {@link DwtShell}, the parent will be a control that is a subclass of {@link DwtComposite}
 * @param	{string}	className		the CSS class
 * @param	{constant}	posStyle		the positioning style (absolute, static, or relative). Defaults to {@link DwtControl.STATIC_STYLE}.
 * @param	{boolean}	deferred		if <code>true</code>, postpone initialization until needed
 * @param	{string}	id			an explicit ID to use for the control's HTML element. If not provided, defaults to an auto-generated ID.
 * @param	{string|HTMLElement}	parentElement the parent element
 * @param	{number}	index 		the index at which to add this control among parent's children
 *
 */
DwtControl = function(params) {

	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtControl.PARAMS);

	/**
	 * parent component. Read-Only
	 * 
	 * @private
	 */
	var parent = this.parent = params.parent;
	if (parent && !(parent instanceof DwtComposite)) {
		throw new DwtException("Parent must be a subclass of Composite", DwtException.INVALIDPARENT, "DwtControl");
	}

	/**
	 * the control's <i>DwtShell</i>
	 * @private
	 */
	this.shell = null;

	/**
	 * Data object used to store "client data" on the widget via the
	 * <code>setData</code> and <code>getData</code> methods
	 * 
	 * @type hash
	 * @private
	 */
	this._data = {};

	/**
	 * The event manager controls the mapping between event types and the registered listeners.
	 * @type AjxEventMgr
	 * @private
	 */
	this._eventMgr = new AjxEventMgr();

	/** true if the control is disposed, else false. The public api to this
	 * member is <code>isDisposed</code>.
	 * 
	 * @type boolean
	 * @private
	 */
	this._disposed = false;

 	if (!parent) { return; }

	/** CSS class name
	 * @type string
	 * @private
	 */
	this._className = params.className || "DwtControl";

	/**
	 * @private
	 */
	this.__posStyle = params.posStyle;

	/**
	 * id of the control's HTML element
	 * @type string
	 * @private
	 */
	if (params.id) {
		this._htmlElId = params.id;
	}

	/**
	 * @private
	 */
	this.__index = params.index;

	this.__parentElement = params.parentElement;

	/**
	 * enabled state of this control. Public APIs to this member are
	 * <code>getEnabled</code> and <code>setEnabled</code>.
	 * 
	 * @type boolean
	 * @private
	 */
	this._enabled = false;

	/**
	 * Indicates the drag state of the control. Valid values are:
	 * <ul>
	 * <li>DwtControl._NO_DRAG<li>
	 * <li>DwtControl._DRAGGING<li>
	 * <li>DwtControl._DRAG_REJECTED<li>
	 * </ul>
	 * 
	 * @type number
	 * @private
	 */
	this._dragging = null;

	/**
	 * Drag n drop icon. Valid when a drag and drop operation is occurring.
	 * 
	 * @type HTMLElement
	 * @private
	 */
	this._dndProxy = null;

	/**
	 * Flag indicating whether the control has keyboard focus or not.
	 * 
	 * @type boolean
	 * @private
	 */
	this._hasFocus = false;

	if (!params.deferred) {
		this.__initCtrl();
	}

	/**
	 * Hover over listener.
	 * 
	 * @type AjxListener
	 * @private
	 */
	this._hoverOverListener = new AjxListener(this, this.__handleHoverOver);

	/**
	 * Hover out listener.
	 * 
	 * @type AjxListener
	 * @private
	 */
	this._hoverOutListener = new AjxListener(this, this.__handleHoverOut);

	// turn this on to receive only the dblclick event (rather than click,
	// click, dblclick); penalty is that single click's timer must expire
	// before it is processed; useful if control has both single and double
	// click actions, and single click action is heavy
	this._dblClickIsolation = false;

	// set to true to ignore OVER and OUT mouse events between elements in the same control
	this._ignoreInternalOverOut = false;

	// override this control's default template
	this.TEMPLATE = params.template || this.TEMPLATE;
};

DwtControl.prototype.isDwtControl = true;
DwtControl.prototype.toString = function() { return "DwtControl"; };


DwtControl.PARAMS = ["parent", "className", "posStyle", "deferred", "id", "index", "template"];

DwtControl.ALL_BY_ID = {};


//
// Constants
//

// Display states
/**
 * Defines the "normal" display state.
 */
DwtControl.NORMAL = "";
/**
 * Defines the "active" display state.
 */
DwtControl.ACTIVE = "ZActive";
/**
 * Defines the "focused" display state.
 */
DwtControl.FOCUSED = "ZFocused";
/**
 * Defines the "disabled" display state.
 */
DwtControl.DISABLED = "ZDisabled";
/**
 * Defines the "hover" display state.
 */
DwtControl.HOVER = "ZHover";
/**
 * Defines the "selected" display state.
 */
DwtControl.SELECTED = "ZSelected";
/**
 * Defines the "default" display state.
 */
DwtControl.DEFAULT = "ZDefault";
/**
 * Defines the "error" display state.
 */
DwtControl.ERROR = "ZError";

DwtControl._RE_STATES = new RegExp(
    "\\b(" +
    [   DwtControl.ACTIVE,  DwtControl.FOCUSED,     DwtControl.DISABLED,
        DwtControl.HOVER,   DwtControl.SELECTED,    DwtControl.DEFAULT,
        DwtControl.ERROR
    ].join("|") +
    ")\\b", "g"
);

/*
 * Position styles
 * 
 */

/**
 * Defines the static position style.
 * 
 * @see  Dwt.STATIC_STYLE
 */
DwtControl.STATIC_STYLE = Dwt.STATIC_STYLE;

/**
 * Defines the absolute position style.
 * 
 * @see Dwt.ABSOLUTE_STYLE
 */
DwtControl.ABSOLUTE_STYLE = Dwt.ABSOLUTE_STYLE;

/**
 * Defines the relative position style.
 * 
 * @see Dwt.RELATIVE_STYLE
 */
DwtControl.RELATIVE_STYLE = Dwt.RELATIVE_STYLE;

/**
 * Defines the fixed position style.
 * 
 * @see Dwt.FIXED_STYLE
 */
DwtControl.FIXED_STYLE = Dwt.FIXED_STYLE;


/*
 * 
 * Overflow style
 * 
 */

/**
 * 
 * Defines clip on overflow.
 * 
 * @see Dwt.CLIP
 */
DwtControl.CLIP = Dwt.CLIP;

/**
 * Defines allow overflow to be visible.
 * 
 * @see Dwt.VISIBLE
 */
DwtControl.VISIBLE = Dwt.VISIBLE;

/**
 * Defines automatically create scrollbars if content overflows.
 * 
 * @see Dwt.SCROLL
 */
DwtControl.SCROLL = Dwt.SCROLL;

/**
 * Defines always have scrollbars whether content overflows or not.
 * 
 * @see Dwt.FIXED_SCROLL
 */
DwtControl.FIXED_SCROLL = Dwt.FIXED_SCROLL;


/**
 * Defines the default value for sizing/position methods.
 * 
 * @see Dwt.DEFAULT
 */
DwtControl.DEFAULT = Dwt.DEFAULT;

// DnD states
/**
 * Defines "no drag" in progress.
 * 
 * @private
 */
DwtControl._NO_DRAG = "NO_DRAG";

/**
 * Defines "drag" in progress.
 *
 * @private
 */
DwtControl._DRAGGING = "DRAGGING";

/**
 * Defines "drag rejected".
 * 
 * @private
 */
DwtControl._DRAG_REJECTED = "DRAG_REJECTED";

/**
 * Defines "drag threshold".
 * 
 * @private
 */
DwtControl.__DRAG_THRESHOLD = 3;

/**
 * Defines "tooltip threshold".
 *
 * @private
 */
DwtControl.__TOOLTIP_THRESHOLD = 5;

/**
 * @private
 */
DwtControl.__DND_HOVER_DELAY = 750;

/**
 * @private
 */
DwtControl.__controlEvent = new DwtControlEvent();

/**
 * Applies only if control has turned on _doubleClickIsolation (see above)
 * want to hit sweet spot where value is more than actual dbl click speed,
 * but as low as possible since it also the length of single click pause.
 * 
 * @private
 */
DwtControl.__DBL_CLICK_TIMEOUT = 300;

//
// Data
//

/**
 * @private
 */
DwtControl.prototype._displayState = "";

//
// Public methods
//

/**
 * Adds a control event listener for control events. Control events are essentially
 * resize and coordinate change events.
 *
 * @param {AjxListener} listener		the listener to be registered (may not be <code>null</code>)
 *
 * @see DwtControlEvent
 * @see #removeControlListener
 * @see #removeAllListeners
 */
DwtControl.prototype.addControlListener =
function(listener) {
	this.addListener(DwtEvent.CONTROL, listener);
};

/**
 * Removes a control event listener for control events. Control events are essentially
 * resize and coordinate change events.
 *
 * @param {AjxListener} listener		the listener to remove
 *
 * @see DwtControlEvent
 * @see #addControlListener
 * @see #removeAllListeners
 */
DwtControl.prototype.removeControlListener =
function(listener) {
	this.removeListener(DwtEvent.CONTROL, listener);
};

/**
 * Registers a dispose listener for control events. Dispose events are fired when
 * a control is "destroyed" via the {@link #dispose} call.
 *
 * @param {AjxListener} listener		the listener to be registered (may not be <code>null</code>)
 *
 * @see DwtDisposeEvent
 * @see #removeDisposeListener
 * @see #removeAllListeners
 * @see #dispose
 * @see #isDisposed
 */
DwtControl.prototype.addDisposeListener =
function(listener) {
	this.addListener(DwtEvent.DISPOSE, listener);
};

/**
 * Removes a dispose event listener for control events. Dispose events are fired when
 * a control is "destroyed" via the {@link #dispose} method call.
 *
 * @param {AjxListener} listener		the listener to remove
 *
 * @see DwtDisposeEvent
 * @see #addDisposeListener
 * @see #removeAllListeners
 * @see #dispose
 * @see #isDisposed
 */
DwtControl.prototype.removeDisposeListener =
function(listener) {
	this.removeListener(DwtEvent.DISPOSE, listener);
};

/**
 * Adds a listener to the control. The listener will be call when events
 * of type <code>eventType</code> fire.
 *
 * @param {string} eventType		the event type for which to listen (may not be <code>null</code>)
 * @param {AjxListener} listener	the listener to register (may not be <code>null</code>)
 * @param {number}		index		the index at which to add listener
 *
 * @see DwtEvent
 * @see #removeListener
 * @see #removeAllListeners
 * @see #notifyListeners
 */
DwtControl.prototype.addListener =
function(eventType, listener, index) {
	return this._eventMgr.addListener(eventType, listener, index);
};

/**
 * Removes a listener from the control.
 *
 * @param {string} eventType		the event type for which to listen (may not be <code>null</code>)
 * @param {AjxListener} listener	the listener to remove (may not be <code>null</code>)
 *
 * @see DwtEvent
 * @see #addListener
 * @see #removeAllListeners
 */
DwtControl.prototype.removeListener =
function(eventType, listener) {
	return this._eventMgr.removeListener(eventType, listener);
};

/**
 * Removes all listeners for a particular event type.
 *
 * @param {string} eventType		the event type (may not be <code>null</code>)
 * @return	{boolean}	<code>true</code> if all listeners are removed
 * 
 * @see DwtEvent
 * @see #addListener
 * @see #removeListener
 */
DwtControl.prototype.removeAllListeners =
function(eventType) {
	return this._eventMgr.removeAll(eventType);
};

/**
 * Checks if there are any listeners registered for a particular event type.
 *
 * @param {string} eventType		the event type (may not be <code>null</code>)
 *
 * @return {boolean}	<code>true</code> if there is an listener registered for the specified event type
 * @see DwtEvent
 */
DwtControl.prototype.isListenerRegistered =
function(eventType) {
	return this._eventMgr.isListenerRegistered(eventType);
};

/**
 * Notifies all listeners of type <code>eventType</code> with <code>event</code>.
 *
 * @param {string} eventType		the event type (may not be <code>null</code>)
 * @param {DwtEvent} event		the event
 */
DwtControl.prototype.notifyListeners =
function(eventType, event) {
	return this._eventMgr.notifyListeners(eventType, event);
};

/**
 * Disposes of the control. This method will remove the control from under the
 * control of its parent and release any resources associate with the component
 * it will also notify any event listeners on registered {@link DwtEvent.DISPOSE} event type.
 *
 * <p>
 * Subclasses may override this method to perform their own dispose functionality but
 * should generally call up to the parent method.
 *
 * @see #isDisposed
 * @see #addDisposeListener
 * @see #removeDisposeListener
 */
DwtControl.prototype.dispose =
function() {
	if (this._disposed) { return; }

	if (this.parent != null && this.parent instanceof DwtComposite) {
		this.parent.removeChild(this);
	}
	this._elRef = null;
	
	if (DwtControl.ALL_BY_ID) {
		DwtControl.ALL_BY_ID[this._htmlElId] = null;
		delete DwtControl.ALL_BY_ID[this._htmlElId];
	}

	this._disposed = true;
	var ev = new DwtDisposeEvent();
	ev.dwtObj = this;
	this.notifyListeners(DwtEvent.DISPOSE, ev);
    this._eventMgr.clearAllEvents();
};

/**
 * This method is deprecated. Please use "document" directly.
 * @deprecated
 * @private
 */
DwtControl.prototype.getDocument =
function() {
	return document;
};

/**
 * Gets the tab group member for this control. Tab group members can
 * be a native HTML form element, a {@link DwtControl}, or a {@link DwtTabGroup} (for more
 * complex or explicit tab-ordering.
 * 
 * @return	{DwtControl}	by default, returns this object
 */
DwtControl.prototype.getTabGroupMember = function() {
	return this;
};

/**
 * Gets the data associated with the specified key.
 *
 * @param {string} key		the key
 * @return {Object}		the associated data
 * 
 * @see #setData
 */
DwtControl.prototype.getData =
function(key) {
	return this._data[key];
};

/**
 * Sets the data for a given key. This method is useful for associating client data with a control.
 *
 * @param {string} key		the key
 * @param {Object} value	the data
 * 
 * @see #getData
 */
DwtControl.prototype.setData =
function(key, value) {
  this._data[key] = value;
};

/**
 * Checks if the control is disposed.
 * 
 * @return {boolean}	<code>true</code> if the control is in a disposed state; <code>false</code> otherwise
 *
 * @see #dispose
 * @see #addDisposeListener
 * @see #removeDisposeListener
 */
DwtControl.prototype.isDisposed =
function() {
	return this._isDisposed;
};

/**
 * Checks if the control is initialized. In general, a control will not be
 * initialized if it has been created in deferred mode and has not yet been initialized.
 * 
 * @return {boolean}	<code>true</code> if the control is in a initialized; <code>false</code> otherwise
 */
DwtControl.prototype.isInitialized =
function() {
	return this.__ctrlInited;
};

/**
 * This method is called to explicitly set keyboard focus to this component.
 * 
 */
DwtControl.prototype.focus =
function() {
	DwtShell.getShell(window).getKeyboardMgr().grabFocus(this);
    this.__doFocus();
};

DwtControl.prototype.blur = function() {
    this.__doBlur();
};

/**
 * Checks if this control has focus.
 * 
 * @return {boolean}	<code>true</code> if this control has keyboard focus; <code>false</code> otherwise
 */
DwtControl.prototype.hasFocus =
function() {
	return this._hasFocus;
};

/**
 * Handles key actions and is called by the keyboard navigation framework. Subclasses
 * should override this method to provide behavior for supported key actions.
 * 
 * @param	{DwtKeyMap}	actionCode	the key action code
 * @param	{DwtKeyEvent}	ev		the key event
 * @return	{boolean}	<code>true</code> if the event is handled; <code>false</code> otherwise
 * 
 * @private
 *
 */
DwtControl.prototype.handleKeyAction =
function(actionCode, ev) {
	return false;
};

/**
 * Re-parents the control within the component hierarchy. Unlike <i>reparentHtmlElement</i>
 * which re-parents the controls <i>div</i> within the DOM hierarchy, this method re-parents
 * the whole control.
 *
 * @param {DwtComposite} newParent 	the control's new parent
 * @param	{number}	index	the index
 * 
 * @see #reparentHtmlElement
 */
DwtControl.prototype.reparent =
function(newParent, index) {
	if (!this._checkState()) { return; }

	var htmlEl = this.getHtmlElement();
	this.parent.removeChild(this, true);
	DwtComposite._pendingElements[this._htmlElId] = htmlEl;
	newParent.addChild(this, index);
	this.parent = newParent;
	// TODO do we need a reparent event?
};

/**
 * Re-parents the HTML element of the control to the html element supplied as the
 * parameter to this method. Note this method only re-parents the control's <i>div</i>
 * element and does not affect the component hierarchy. To re-parent the control within
 * the component hierarchy, use the <i>reparent</i> method.
 *
 * @param {string|HTMLElement} htmlEl a string representing an element ID or an HTML element
 * @param {number} position 	the position to insert the element
 *
 * @see #reparent
 */
DwtControl.prototype.reparentHtmlElement =
function(htmlEl, position) {

	// If htmlEl is a string, then it is an ID so lookup the html element that
	// has the corresponding ID
	if (typeof htmlEl == "string") {
		htmlEl = document.getElementById(htmlEl);
	}
	if (!htmlEl) { return; }

	var el = this.getHtmlElement();
	if (position == null) {
		htmlEl.appendChild(el);
	} else if (typeof position == "object") {
		htmlEl.insertBefore(el, position);
	} else {
		if (htmlEl.childNodes[position]) {
			htmlEl.insertBefore(el, htmlEl.childNodes[position]);
		} else {
			htmlEl.appendChild(el);
		}
	}
};

/**
 * Sets the event handling function for a given event type. This method
 * should be used judiciously as it can lead to unexpected results (for example if
 * overriding the control's mouse handlers). This method calls through to <i>Dwt.setHandler</i>
 *
 * @param {string} eventType 	the event type (defined in {@see DwtEvent}) to override 
 * @param {function} hdlrFunc Event handler function
 *
 * @see DwtEvent
 */
DwtControl.prototype.setHandler =
function(eventType, hdlrFunc) {
	if (!this._checkState()) { return; }

	var htmlElement = this.getHtmlElement();
	Dwt.setHandler(htmlElement, eventType, hdlrFunc);
};

/**
 * Clears the event handling function for a given event type. This method
 * should be used judiciously as it can lead to unexpected results (for example if
 * overriding the control's mouse handlers)
 *
 * @param {string} eventType 	the event type (defined in {@see DwtEvent}) to override 
 *
 * @see DwtEvent
 */
DwtControl.prototype.clearHandler =
function(eventType) {
	if (!this._checkState()) { return; }

	var htmlElement = this.getHtmlElement();
	Dwt.clearHandler(htmlElement, eventType);
};

/**
 * Gets the bounds of the component. Bounds includes the location (not relevant for
 * statically position elements) and dimensions of the control (i.e. the <code>&lt;div&gt;</code> element).
 *
 * @return {DwtRectangle}		the control bounds
 *
 * @see DwtRectangle
 * @see #getSize
 * @see #getLocation
 * @see #getH
 * @see #getW
 * @see #getX
 * @see #getXW
 * @see #getY
 * @see #getYH
 * @see #setBounds
 * @see #setSize
 * @see #setLocation
 */
DwtControl.prototype.getBounds =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getBounds(this.getHtmlElement());
};

/**
 * Sets the bounds of a control. The position type of the control must
 * be absolute or else an exception is thrown. To omit setting a value set the
 * actual parameter value to <i>Dwt.DEFAULT</i>
 *
 * @param {number|string} x		the x coordinate of the element (for example: 10, "10px", Dwt.DEFAULT)
 * @param {number|string} y		the y coordinate of the element (for example: 10, "10px", Dwt.DEFAULT)
 * @param {number|string} width	the width of the element (for example: 100, "100px", "75%", Dwt.DEFAULT)
 * @param {number|string} height	the height of the element (for example: 100, "100px", "75%", Dwt.DEFAULT)
 *
 * @return {DwtControl}		this control
 *
 * @see DwtRectangle
 * @see #getBounds
 * @see #setSize
 * @see #setLocation
 * @see #getSize
 * @see #getLocation
 * @see #getH
 * @see #getW
 * @see #getX
 * @see #getXW
 * @see #getY
 * @see #getYH
 */
DwtControl.prototype.setBounds =
function(x, y, width, height) {
	if (!this._checkState()) { return; }

	var htmlElement = this.getHtmlElement();
	if (this.isListenerRegistered(DwtEvent.CONTROL)) {
		this.__controlEvent.reset(DwtControlEvent.RESIZE | DwtControlEvent.MOVE);
		var bds = Dwt.getBounds(htmlElement);
		this.__controlEvent.oldX = bds.x;
		this.__controlEvent.oldY = bds.y;
		this.__controlEvent.oldWidth = bds.width;
		this.__controlEvent.oldHeight = bds.height;
        //TODO: notifyListeners() called atleast 3 times. Should minimize the calls.
		this.setLocation(x, y);
		this.setSize(width, height);
		bds = Dwt.getBounds(htmlElement);
		this.__controlEvent.newX = bds.x;
		this.__controlEvent.newY = bds.y;
		this.__controlEvent.newWidth = bds.width;
		this.__controlEvent.newHeight = bds.height;
		this.__controlEvent.requestedWidth = width;
		this.__controlEvent.requestedHeight = height;
		this.notifyListeners(DwtEvent.CONTROL, this.__controlEvent);
	} else {
		this.setLocation(x, y);
		this.setSize(width, height);
	}

	return this;
}

/**
 * Gets the class name of this control. The class name may be set
 * when constructing the control. If it is not passed into the constructor, it
 * defaults to the control's class name. The class name is generally used as the
 * CSS class name for the control, although control's that change visual behaviour
 * based on state may append (or even use different) class names. See the documentation
 * of the specific component for details.
 *
 * @return {string}		the control class name
 *
 * @see #setClassName
 */
DwtControl.prototype.getClassName =
function() {
	return this._className;
};

/**
 * Sets the control class name. This also automatically sets the control CSS
 * class name (i.e. the control htmlElement class name). Subclasses of <i>DwtControl</i>
 * may override this method to perform a different behavior.
 *
 * @param {string} className		the new class name for the control
 *
 * @see #getClassName
 */
DwtControl.prototype.setClassName =
function(className) {
	if (!this._checkState()) { return; }

	this._className = className;
    var el = this.getHtmlElement();
    el.className = className;
    Dwt.addClass(el, this._displayState);
};

/**
 * Adds a class name to this control HTML element.
 *
 * @param {string} className		the class name to add
 */
DwtControl.prototype.addClassName =
function(className) {
	Dwt.addClass(this.getHtmlElement(), className);
};

/**
 * Removes a class name from this control's HTML element. Optionally adds a new class name, if specified.
 *
 * @param {string} delClass		the class to remove
 * @param {string} addClass		the class to add (may be <code>null</code>)
 */
DwtControl.prototype.delClassName =
function(delClass, addClass) {
	Dwt.delClass(this.getHtmlElement(), delClass, addClass);
};

/**
 * Conditionally adds or removes a class name to this control HTML element.
 * The class names are used exclusively, that is: when condition is true,
 * <code>classWhenTrue</code> is added and <code>classWhenFalse</code> is removed (if present and
 * specified).  When condition is false, <code>classWhenTrue</code> is removed and
 * <code>classWhenFalse</code> is added (again, if present and specified).
 *
 * @param {string} condition	the condition
 * @param {string} classWhenTrue	the class name to add when condition is <code>true</code>
 * @param {string} classWhenFalse	the class name to add when contition is <code>false</code> (may be <code>null</code>)
 */
DwtControl.prototype.condClassName = function(condition, classWhenTrue, classWhenFalse) {
	Dwt.condClass(this.getHtmlElement(), condition, classWhenTrue, classWhenFalse);
};

/**
 * Sets the display state.
 * 
 * @param	{Object}		state		the state
 */
DwtControl.prototype.setDisplayState =
function(state) {
    if (!this._enabled) state = DwtControl.DISABLED;

    if (arguments.length > 1) {
        var a = [];
        for (var i = 0; i < arguments.length; i++) {
            a.push(arguments[i]);
        }
        state = a.join(" ");
    }
    if (this._displayState != state) {
        this._displayState = state;
        Dwt.delClass(this.getHtmlElement(), DwtControl._RE_STATES, state);
    }
};

/**
* Shows an alert in the control. For example, to indicate that a new message has arrived.
*
* @param	{string}	alert		the alert
*/
DwtControl.prototype.showAlert =
function(alert) {
	if (alert && !this._alert) {
		this.delClassName(null, "ZAlert");
	} else if (!alert && this._alert) {
		this.delClassName("ZAlert", null);
	}
	this._alert = alert;
};

/**
* Checks if the control is showing an alert.
* 
* @return	{boolean}	<code>true</code> if showing an altert; <code>false</code> otherwise
*/
DwtControl.prototype.isAlertShown =
function() {
	return this._alert;
};

/**
 * @private
 */
DwtControl.prototype._createHtmlFromTemplate =
function(templateId, data) {
    // set html content
    this.getHtmlElement().innerHTML = AjxTemplate.expand(templateId, data);

    // set container class name, if needed
    var params = AjxTemplate.getParams(templateId);
    var className = params && params["class"];
    if (className) {
        className = [ this._className, className ].join(" ");
        this.setClassName(className);
    }
};

/**
 * Gets the control cursor.
 * 
 * @return {string}		the control cursor
 *
 * @see #setCursor
 */
DwtControl.prototype.getCursor =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getCursor(this.getHtmlElement());
};

/**
 * Sets the control cursor.
 *
 * @param {string} cursorName		the name of the new cursor
 *
 * @see #getCursor
 */
DwtControl.prototype.setCursor =
function(cursorName) {
	if (!this._checkState()) { return; }

	Dwt.setCursor(this.getHtmlElement(), cursorName);
};

/**
 * Gets the control drag source.
 * 
 * @return {DwtDragSource}		the control drag source or <code>null</code> for none
 *
 * @see #setDragSource
 */
DwtControl.prototype.getDragSource =
function() {
	return this._dragSource;
};

/**
 * Set the control drag source. The drag source binds the drag-and-drop system with
 * an application. Setting a control drag source makes the control "draggable".
 *
 * @param {DwtDragSource} dragSource		the control drag source
 *
 * @see #getDragSource
 */
DwtControl.prototype.setDragSource =
function(dragSource) {
	this._dragSource = dragSource;
	if (dragSource && !this._ctrlCaptureObj) {
		this.__initCapture();
		this._dndHoverAction = new AjxTimedAction(null, this.__dndDoHover);
	}
};

/**
 * Gets the control drop target.
 * 
 * @return	{DwtDropTarget}		the control drop target or <code>null</code> for none
 *
 * @see #setDropTarget
 */
DwtControl.prototype.getDropTarget =
function() {
	return this._dropTarget;
};

/**
 * Sets the drop target for the control. The drop target binds the drag-and-drop system with
 * an application. Setting a control drop target makes the control a potential drop
 * target within an application.
 *
 * @param {DwtDropTarget} dropTarget		the control drop target
 *
 * @see #getDropTarget
 */
DwtControl.prototype.setDropTarget =
function(dropTarget) {
	this._dropTarget = dropTarget;
};

/**
 * Gets the control drag box.
 *
 * @return {DwtDragBox}		the control drag box or <code>null</code> for none
 *
 * @see #setDragBox
 */
DwtControl.prototype.getDragBox =
function() {
	return this._dragBox;
};

/**
 * Set the control drag box. The drag box handles the display of a dotted rectangle
 * that is typically used to select items.
 *
 * @param {DwtDragBox} dragBox		the control drag box
 *
 * @see #getDragBox
 */
DwtControl.prototype.setDragBox =
function(dragBox) {
	this._dragBox = dragBox;
	if (dragBox && !this._ctrlCaptureObj) {
		this.__initCapture();
	}
};

DwtControl.prototype.__initCapture =
function(dragBox) {
	this._ctrlCaptureObj = new DwtMouseEventCapture({
		targetObj:		this,
		id:				"DwtControl",
		mouseOverHdlr:	DwtControl.__mouseOverHdlr,
		mouseDownHdlr:	DwtControl.__mouseDownHdlr,
		mouseMoveHdlr:	DwtControl.__mouseMoveHdlr,
		mouseUpHdlr:	DwtControl.__mouseUpHdlr,
		mouseOutHdlr:	DwtControl.__mouseOutHdlr
	});
};

/**
 * Gets the enabled state.
 * 
 * @return {boolean}		<code>true</code> if the control is enabled; <code>false</code> otherwise
 *
 * @see #setEnabled
 */
DwtControl.prototype.getEnabled =
function() {
	if (!this._checkState()) { return; }

	return this._enabled;
};

/**
 * Sets the control enabled state. If <code>setHtmlElement</code> is true, then
 * this method will also set the control HTML element disabled attribute.
 *
 * @param {boolean} enabled		<code>true</code> if the control is enabled
 * @param {boolean} setHtmlElement	<code>true</code> to set the control HTML element disabled attribute
 */
DwtControl.prototype.setEnabled =
function(enabled, setHtmlElement) {
	if (!this._checkState()) { return; }

	if (enabled != this._enabled) {
		this._enabled = enabled;
        this.setDisplayState(enabled ? DwtControl.NORMAL : DwtControl.DISABLED);
        if (setHtmlElement)
			this.getHtmlElement().disabled = !enabled;
	}
};

/**
 * Gets the ID of the control containing HTML element.
 *
 * @return {string} 	the ID of the control containing HTML element
 */
DwtControl.prototype.getHTMLElId =
function () {
	return this._htmlElId;
};

/**
 * Gets the control containing HTML element. By default this is a <code>div</code> element
 *
 * @return {HTMLElement}		the control containing HTML element
 */
DwtControl.prototype.getHtmlElement =
function() {
	if (!this._checkState()) { return; }

	var htmlEl = this._elRef || document.getElementById(this._htmlElId);
	if (htmlEl == null) {
		htmlEl = DwtComposite._pendingElements[this._htmlElId];
	} else if (!htmlEl._rendered) {
		delete DwtComposite._pendingElements[this._htmlElId];
		htmlEl._rendered = true;
	}
	return this._elRef = htmlEl;
};

/**
 * Returns the control associated with the given element, if any.
 * 
 * @param {Element}		htmlEl	an HTML element
 * @return	{DwtControl}		the control element or <code>null</code> for none
 */
DwtControl.fromElement =
function(htmlEl)  {
	return DwtControl.ALL_BY_ID && DwtControl.ALL_BY_ID[htmlEl.id];
};

/**
 * Returns the control associated with the given element ID, if any.
 * 
 * @param {string}		htmlElId	an HTML element Id
 * @return	{DwtControl}		the control element or <code>null</code> for none
 */
DwtControl.fromElementId =
function(htmlElId)  {
	return DwtControl.ALL_BY_ID && DwtControl.ALL_BY_ID[htmlElId];
};

/**
 * Finds a control and starts the search at the given element and works
 * up the element chain until it finds one with an ID that maps to a {@link DwtControl}.
 * 
 * @param {Element}		htmlEl	an HTML element
 * @return	{DwtControl}	the control or <code>null</code> for none
 */
DwtControl.findControl =
function(htmlEl)  {

	// FF 3.5 throws protection error if we dereference a chrome element, so bail
	if (AjxEnv.isFirefox3_5up && !AjxEnv.isFirefox3_6up) {
		var s = HTMLElement.prototype.toString.call(htmlEl);
		if (s == '[xpconnect wrapped native prototype]' || s == '[object XULElement]') { return null; }
	}

	try{
		while (htmlEl) {
			if (htmlEl.id && DwtControl.ALL_BY_ID && DwtControl.ALL_BY_ID[htmlEl.id]) {
				return DwtControl.ALL_BY_ID[htmlEl.id];
			}
			htmlEl = htmlEl.parentNode;
		}
	} catch(e) {
		//In some FF, we might get permission denied error. Ignore it.
	}
	return null;
};

/**
 * Returns the control associated with the given event. Starts with the
 * event target and works its way up the element chain until it finds one
 * with an ID that maps to a {@link DwtControl}.
 * 
 * @param {Event}		ev				the DHTML event
 * @param {boolean}		useRelatedTarget	if <code>true</code>, use element that was related to this event
 * @return {DwtControl}	the control or <code>null</code> for none
 */
DwtControl.getTargetControl =
function(ev, useRelatedTarget)  {
	var htmlEl = DwtUiEvent.getTarget(ev, useRelatedTarget);
	return htmlEl ? DwtControl.findControl(htmlEl) : null;
};

/**
 * Sets the control HTML element id attribute.
 *
 * @param {string} id 		the new element Id
 */
DwtControl.prototype.setHtmlElementId =
function(id) {
	if (this._disposed) { return; }

	if (this.__ctrlInited) {
		var htmlEl = this.getHtmlElement();
		if (!htmlEl._rendered) {
			delete DwtComposite._pendingElements[this._htmlElId];
			DwtComposite._pendingElements[id] = htmlEl;
		}
		else {
			delete DwtControl.ALL_BY_ID[this._htmlElId];
			DwtControl.ALL_BY_ID[id] = this;
		}
		htmlEl.id = id;
	}
	this._htmlElId = id;
};

/**
 * Gets the X coordinate of the control (if absolutely positioned).
 * 
 * @return {number}		the X coordinate of the control 
 *
 * @see #getBounds
 * @see #getSize
 * @see #getLocation
 * @see #getH
 * @see #getW
 * @see #getXW
 * @see #getY
 * @see #getYH
 * @see #setBounds
 * @see #setSize
 * @see #setLocation
 */
DwtControl.prototype.getX =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getLocation(this.getHtmlElement()).x;
};

/**
 * Gets the horizontal extent of the control (if absolutely positioned).
 * 
 * @return {number} 	the horizontal extent of the control
 *
 * @see #getBounds
 * @see #getSize
 * @see #getLocation
 * @see #getH
 * @see #getW
 * @see #getX
 * @see #getY
 * @see #getYH
 * @see #setBounds
 * @see #setSize
 * @see #setLocation
 */
DwtControl.prototype.getXW =
function() {
	if (!this._checkState()) { return; }

    var bounds = this.getBounds();
	return bounds.x+bounds.width;
};

/**
 * Gets the Y coordinate of the control (if it is absolutely positioned).
 * 
 * @return {number}		the Y coordinate of the control 
 *
 * @see #getBounds
 * @see #getSize
 * @see #getLocation
 * @see #getH
 * @see #getW
 * @see #getX
 * @see #getXW
 * @see #getYH
 * @see #setBounds
 * @see #setSize
 * @see #setLocation
 */
DwtControl.prototype.getY =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getLocation(this.getHtmlElement()).y;
};

/**
 * Gets the vertical extent of the control (if it is absolutely positioned).
 * 
 * @return {number}		the vertical extent of the control
 *
 * @see #getBounds
 * @see #getSize
 * @see #getLocation
 * @see #getH
 * @see #getW
 * @see #getX
 * @see #getXW
 * @see #getY
 * @see #setBounds
 * @see #setSize
 * @see #setLocation
 */
DwtControl.prototype.getYH =
function() {
	if (!this._checkState()) { return; }

    var bounds = this.getBounds();
	return bounds.y+bounds.height;
};

/**
 * Returns the positioning style
 */
DwtControl.prototype.getPosition =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getPosition(this.getHtmlElement());
};

/**
 * Sets the positioning style
 * 
 * @param 	{constant}	posStyle	positioning style (Dwt.*_STYLE)
 */
DwtControl.prototype.setPosition =
function(posStyle) {
	if (!this._checkState()) { return; }

	return Dwt.setPosition(this.getHtmlElement(), posStyle);
};

/**
 * Gets the location of the control.
 *
 * @return {DwtPoint}		the location of the control
 *
 * @see #getBounds
 * @see #getSize
 * @see #setLocation
 * @see #getH
 * @see #getW
 * @see #getX
 * @see #getXW
 * @see #getY
 * @see #setBounds
 * @see #setSize
 * @see Dwt
 */
DwtControl.prototype.getLocation =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getLocation(this.getHtmlElement());
};

/**
 * Sets the location of the control. The position style of the control must
 * be absolute or else an exception is thrown. To only set one of the coordinates,
 * pass in a value of <i>Dwt.DEFAULT</i> for the coordinate for which the value is
 * not to be set. Any <i>DwtEvent.CONTROL</i> listeners registered on the control
 * will be called.
 *
 * @param {number|string} x	the x coordinate of the element (for example: 10, "10px", Dwt.DEFAULT)
 * @param {number|string} y	the y coordinate of the element (for example: 10, "10px", Dwt.DEFAULT)
 *
 * @return {DwtControl}		this control
 *
 * @see #getBounds
 * @see #getSize
 * @see #getLocation
 * @see #getH
 * @see #getW
 * @see #getX
 * @see #getXW
 * @see #getY
 * @see #setBounds
 * @see #setSize
 * @see Dwt
 */
DwtControl.prototype.setLocation =
function(x, y) {
	if (!this._checkState()) { return; }

	if (this.isListenerRegistered(DwtEvent.CONTROL)) {
		var htmlElement = this.getHtmlElement();
		this.__controlEvent.reset(DwtControlEvent.MOVE);
		var loc = Dwt.getLocation(htmlElement);
		this.__controlEvent.oldX = loc.x;
		this.__controlEvent.oldY = loc.y;
		Dwt.setLocation(htmlElement, x, y);
		loc = Dwt.getLocation(htmlElement);
		this.__controlEvent.newX = loc.x;
		this.__controlEvent.newY = loc.y;
		this.notifyListeners(DwtEvent.CONTROL, this.__controlEvent);
	} else {
		Dwt.setLocation(this.getHtmlElement(), x, y);
	}
	return this;
};

/**
 * Gets the control scroll style. The scroll style determines the control
 * behavior when content overflows its div's boundaries. Possible values are:
 * <ul>
 * <li>{@link Dwt.CLIP} - Clip on overflow</li>
 * <li>{@link Dwt.VISIBLE} - Allow overflow to be visible</li>
 * <li>{@link Dwt.SCROLL} - Automatically create scrollbars if content overflows</li>
 * <li>{@link Dwt.FIXED_SCROLL} - Always have scrollbars whether content overflows or not</li>
 * </ul>
 *
 * @return {number}		the control scroll style
 */
DwtControl.prototype.getScrollStyle =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getScrollStyle(this.getHtmlElement());
};

/**
 * Sets the control scroll style. The scroll style determines the control's
 * behavior when content overflows its div's boundaries. Possible values are:
 * <ul>
 * <li>{@link Dwt.CLIP} - Clip on overflow</li>
 * <li>{@link Dwt.VISIBLE} - Allow overflow to be visible</li>
 * <li>{@link Dwt.SCROLL} - Automatically create scrollbars if content overflows</li>
 * <li>{@link Dwt.FIXED_SCROLL} - Always have scrollbars whether content overflows or not</li>
 * </ul>
 *
 * @param {int} scrollStyle		the control new scroll style
 */
DwtControl.prototype.setScrollStyle =
function(scrollStyle) {
	if (!this._checkState()) { return; }

	Dwt.setScrollStyle(this.getHtmlElement(), scrollStyle);
};

/**
 * Sets the control position. The position determines the control's
 * location within the context of which it was created. Possible values are:
 * <ul>
 * <li>{@link DwtControl.STATIC_STYLE} - Allow browser to control content flow</li>
 * <li>{@link DwtControl.ABSOLUTE_STYLE} - Allow content to be positioned relative to parent or body</li>
 * <li>{@link DwtControl.RELATIVE_STYLE} - Allow browser to control content flow but relative to parent</li>
 * </ul>
 *
 * @param {number} position		the control new position
 */
DwtControl.prototype.setPosition =
function(position) {
	if (!this._checkState()) { return; }

	if (position == DwtControl.STATIC_STYLE ||
		position == DwtControl.ABSOLUTE_STYLE ||
		position == DwtControl.RELATIVE_STYLE)
	{
		this.__posStyle = position;
		Dwt.setPosition(this.getHtmlElement(), position);
	}
};

/**
 * Gets the width of the control.
 * 
 * @return	{number}		the width of the control
 *
 * @see #getBounds
 * @see #getSize
 * @see #getLocation
 * @see #getH
 * @see #getX
 * @see #getXW
 * @see #getY
 * @see #getYH
 * @see #setBounds
 * @see #setSize
 * @see #setLocation
 */
DwtControl.prototype.getW =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getSize(this.getHtmlElement()).x;
};

/**
 * Gets the height of the control.
 * 
 * @return {number}	the height of the control
 *
 * @see #getBounds
 * @see #getSize
 * @see #getLocation
 * @see #getW
 * @see #getX
 * @see #getXW
 * @see #getY
 * @see #getYH
 * @see #setBounds
 * @see #setLocation
 * @see #setSize
 */
DwtControl.prototype.getH =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getSize(this.getHtmlElement()).y;
};

/**
 * Gets the size of the control. The x value of the returned point is the width
 * and the y is the height.
 * 
 * @return {DwtPoint}		the control size
 *
 * @see #getBounds
 * @see #getLocation
 * @see #getH
 * @see #getW
 * @see #getX
 * @see #getXW
 * @see #getY
 * @see #getYH
 * @see #setBounds
 * @see #setSize
 * @see #setLocation
 */
DwtControl.prototype.getSize =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getSize(this.getHtmlElement());
};

/**
 * Sets the size of the control
 *
 * @param {number|string} width	the width of the control (for example: 100, "100px", "75%", Dwt.DEFAULT)
 * @param {number|string} height	the height of the control (for example: 100, "100px", "75%", Dwt.DEFAULT)
 *
 * @return {DwtControl}	this control
 *
 * @see #getBounds
 * @see #getSize
 * @see #setLocation
 * @see #getH
 * @see #getW
 * @see #getX
 * @see #getXW
 * @see #getY
 * @see #getYH
 * @see #setBounds
 */
DwtControl.prototype.setSize =
function(width, height) {
	if (!this._checkState()) { return; }

	if (this.isListenerRegistered(DwtEvent.CONTROL)) {
		var htmlElement = this.getHtmlElement();
		this.__controlEvent.reset(DwtControlEvent.RESIZE);
		var sz = Dwt.getSize(htmlElement);
		this.__controlEvent.oldWidth = sz.x;
		this.__controlEvent.oldHeight = sz.y;
		Dwt.setSize(htmlElement, width, height);
		sz = Dwt.getSize(htmlElement);
		this.__controlEvent.newWidth = sz.x;
		this.__controlEvent.newHeight = sz.y;
		this.notifyListeners(DwtEvent.CONTROL, this.__controlEvent);
	} else {
		Dwt.setSize(this.getHtmlElement(), width, height);
	}
	return this;
};

/**
 * Gets the tooltip content (typically set using {@link #setToolTipContent}). Controls
 * that want to return dynamic tooltip content should override this method.
 *
 * @param {DwtEvent}	ev	the mouseover event
 * @return {string}		the tooltip content set for the control
 */
DwtControl.prototype.getToolTipContent =
function(ev) {
	if (this._disposed) { return; }

	return this.__toolTipContent;
};

/**
 * Sets tooltip content for the control. The content may be plain text or HTML.
 *
 * @param {string} text		the tooltip content
 */
DwtControl.prototype.setToolTipContent =
function(text) {
	if (this._disposed) { return; }

	this.__toolTipContent = text;
};

/**
 * Gets the visible state of the control. For example, the control HTML elements display style attribute is not "none".
 * 
 * @return {boolean}	if <code>true</code>, the control is visible
 *
 * @see Dwt#getVisibile
 */
DwtControl.prototype.getVisible =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getVisible(this.getHtmlElement());
};

/**
 * Sets the the visible state of the control HTML element. <i>Note: Gets style
 * "display: none", don't confuse with {@link setVisibility}).</i>
 *
 * @param {boolean} visible 	if <code>true</code>, the control should be displayed; if <code>false</code>, the control should not be displayed
 *
 * @see Dwt#setVisible
 */
DwtControl.prototype.setVisible =
function(visible) {
	if (!this._checkState()) { return; }

	Dwt.setVisible(this.getHtmlElement(), visible);
};

/**
 * Sets the visibility of the control HTML element.
 *
 * @param {boolean} visible		if <code>true</code> then the control is visible
 *
 * @see Dwt#setVisibility
 */
DwtControl.prototype.setVisibility =
function(visible) {
	if (!this._checkState()) { return; }

	Dwt.setVisibility(this.getHtmlElement(), visible);
};

/**
 * Gets the visibility of the control HTML element.
 * 
 * @return {boolean}	if <code>true</code>, the control is visible (i.e. the HTML elements visibility play style attribute is not "hidden")
 *
 * @see Dwt#getVisibility
 */
DwtControl.prototype.getVisibility =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getVisiblility(this.getHtmlElement());
};


/**
 * Gets the control z-index value.
 *
 * @return	{number}	the z-index value
 */
DwtControl.prototype.getZIndex =
function() {
	if (!this._checkState()) { return; }

	return Dwt.getZIndex(this.getHtmlElement());
};

/**
 * Sets the z-index for the control HTML element. Since z-index is only relevant among peer
 * elements, we make sure that all elements that are being displayed via z-index hang off the
 * main shell.
 *
 * @param {number} idx		the new z-index for this element
 */
DwtControl.prototype.setZIndex =
function(idx) {
	if (!this._checkState()) { return; }

	Dwt.setZIndex(this.getHtmlElement(), idx);
};

/**
 * Convenience function to toggle visibility using z-index. It uses the two lowest level
 * z-indexes ({@link Dwt.Z_VIEW} and {@link Dwt.Z_HIDDEN} respectively). Any further
 * stacking will have to use {@link #setZIndex} directly.
 *
 * @param {boolean} show		if <code>true</code>, show the element; <code>false</code> to hide the element
 *
 * @see #setZIndex
 */
DwtControl.prototype.zShow =
function(show) {
	this.setZIndex(show ? Dwt.Z_VIEW : Dwt.Z_HIDDEN);
};

/**
 * Sets the display.
 * 
 * @param	{string}	value		the display value
 */
DwtControl.prototype.setDisplay =
function(value) {
	if (!this._checkState()) { return; }

	Dwt.setDisplay(this.getHtmlElement(), value);
};

/**
 * Prevents selection on the specified element.
 * 
 * @param	{Element}	targetEl	the element
 */
DwtControl.prototype.preventSelection =
function(targetEl) {
	return !this.__isInputEl(targetEl);
};

/**
 * Prevents a context menu on the specified element.
 * 
 * @param	{Element}	targetEl	the element
 */
DwtControl.prototype.preventContextMenu =
function(targetEl) {
	return targetEl ? (!this.__isInputEl(targetEl)) : true;
};

/**
 * Sets the content of the control HTML element to the provided
 * content. Care should be taken when using this method as it can blow away all
 * the content of the control which can be particularly bad if the control is
 * a <i>DwtComposite</i> with children. Generally this method should be used
 * controls which are being directly instantiated and used as a canvas
 *
 * @param {string} content		the HTML content
 */
DwtControl.prototype.setContent =
function(content) {
	if (content)
		this.getHtmlElement().innerHTML = content;
};

/**
 * Clears the content of the control HTML element.
 * Care should be taken when using this method as it can blow away all
 * the content of the control which can be particularly bad if the control is
 * a {@link DwtComposite} with children. Generally this method should be used
 * controls which are being directly instantiated and used as a canvas.
 */
DwtControl.prototype.clearContent =
function() {
	this.getHtmlElement().innerHTML = "";
};

/**
 * Appends this control element to the specified element.
 *
 * @param {Element|string}	elemOrId  the DOM element or an element id
 */
DwtControl.prototype.appendElement =
function(elemOrId) {
    var el = AjxUtil.isString(elemOrId) ? document.getElementById(elemOrId) : elemOrId;
    if (el) {
        el.appendChild(this.getHtmlElement(), el);
    }
};

/**
 * Replaces the specified element with this control element.
 *
 * @param {Element|string}	elemOrId  the DOM element or an element id
 */
DwtControl.prototype.replaceElement =
function(elemOrId, inheritClass, inheritStyle) {
    var oel = AjxUtil.isString(elemOrId) ? document.getElementById(elemOrId) : elemOrId;
    if (oel) {
        var nel = this.getHtmlElement();
        oel.parentNode.replaceChild(nel, oel);
        this._replaceElementHook(oel, nel, inheritClass, inheritStyle);
    }
};

/**
 * This method is a hook for sub-classes that want to intercept the
 * inheriting of class and style when an element is replaced. By
 * default, the new will will inherit the class and style. In order
 * to prevent this behavior, you must pass in a <code>true</code>
 * or <code>false</code> value.
 * 
 * @private
 */
DwtControl.prototype._replaceElementHook =
function(oel, nel, inheritClass, inheritStyle) {
    if ((inheritClass == null || inheritClass) && oel.className) {
        Dwt.addClass(nel, oel.className);
    }
    if (inheritStyle == null || inheritStyle) {
        var style = oel.getAttribute("style") || oel.style;
        if (style) {
            if (AjxUtil.isString(style)) { // All non-IE browsers
                nel.setAttribute("style", [nel.getAttribute("style"),style].join(";"));
            } else {
                for (var attribute in style) {
                    if (style[attribute]) {
						try {
                        	nel.style[attribute] = style[attribute];
						} catch (e) {}
                    }
                }
            }
        }
    }
};

/**
 * This protected method is called by the keyboard navigate infrastructure when a control
 * gains focus. This method should be overridden by derived classes to provide
 * the visual behavior for the component losing focus
 *
 * @see #_focus
 * @see #_focusByMouseUpEvent
 * @see #focus
 * 
 * @private
 */
DwtControl.prototype._blur =
function() {
};

/**
 * This protected method should be overridden by derived classes to provide
 * behavior for the component gaining focus e.g. providing a border or
 * highlighting etc...
 *
 * @see #_blur
 * @see #_focusByMouseUpEvent
 * @see #focus
 * 
 * @private
 */
DwtControl.prototype._focus =
function() {
};

/**
 * This protected method is called from mouseUpHdl. Subclasses may override this method
 * if they have their own specialized focus management code.
 *
 * @see #_blur
 * @see #_focus
 * @see #focus
 * 
 * @private
 */
DwtControl.prototype._focusByMouseUpEvent =
function(ev)  {
 	if (this.getEnabled()) {
 		this.focus();
 	}
};

/**
 * This is for bug 11827.
 * 
 * TODO: we should remove _focusByMouseUpEvent and update all classes
 * that define it to use _focusByMouseDownEvent instead.
 * 
 * @private
 */
DwtControl.prototype._focusByMouseDownEvent =
function(ev) {
	this._duringFocusByMouseDown = true;
	this._focusByMouseUpEvent(ev);
	this._duringFocusByMouseDown = false;
};

/**
 * Returns the type of drag operation we are performing.
 *
 * @param mouseEv
 */
DwtControl.prototype._getDragOp =
function(mouseEv) {
	return mouseEv.ctrlKey ? Dwt.DND_DROP_COPY : Dwt.DND_DROP_MOVE;
};

/**
 * Subclasses may override this protected method to return an HTML element that will represent
 * the dragging icon. The icon must be created on the DwtShell widget. This means that the
 * icon must be a child of the shells HTML component If this method returns
 * null, it indicates that the drag failed. This method is called when a control is
 * being dragged and it has a valid drag source
 *
 * @return {HTMLElement}	the DnD dragging icon. This is typically a div element
 *
 * @see #_setDragProxyState
 * @see #_destroyDragProxy
 * @see #_isValidDragObject
 * @see #_dragEnter
 * @see #_dragOver
 * @see #_dragHover
 * @see #_dragLeave
 * @see #_drop
 * @see #setDragSource
 * @see DwtDropTarget
 * @see DwtDragSource
 * 
 * @private
 */
DwtControl.prototype._getDragProxy =
function(dragOp) {
	DBG.println(AjxDebug.DBG2, "DwtControl.prototype._getDragProxy");
	return null;
};

DwtControl.prototype.getDragSelectionBox =
function(dragOp) {

	if (!this._dragSelectionBox) {
		var box = this._dragSelectionBox = document.createElement("div");
		box.className = "dndSelectionBox";
		Dwt.setPosition(box, Dwt.ABSOLUTE_STYLE);
		this.shell.getHtmlElement().appendChild(box);
		Dwt.setZIndex(box, Dwt.Z_DND);
	}
	return this._dragSelectionBox;
};

/**
 * Subclasses may override this method to set the DnD icon properties based on whether drops are
 * allowed. The default implementation sets the class on the HTML element obtained
 * from <code>_getDragProxy</code> to DwtCssStyle.DROPPABLE if <code>dropAllowed</code> is true and
 * to DwtCssStyle.NOT_DROPPABLE if false
 *
 * @param {boolean} dropAllowed		if <code>true</code>, then dropping is allowed on the drop zone so set
 * 		DnD icon to the visually reflect this
 *
 * @see #_getDragProxy
 * @see #_destroyDragProxy
 * @see #_isValidDragObject
 * @see #_dragEnter
 * @see #_dragOver
 * @see #_dragHover
 * @see #_dragLeave
 * @see #_drop
 * @see #setDragSource
 * @see DwtDropTarget
 * @see DwtDragSource
 * 
 * @private
 */
DwtControl.prototype._setDragProxyState =
function(dropAllowed) {
	if (this._dndProxy) {
		Dwt.condClass(this._dndProxy, dropAllowed, DwtCssStyle.DROPPABLE, DwtCssStyle.NOT_DROPPABLE);
	}
};


/**
 * @private
 */
DwtControl.__junkIconId = 0;

/**
 * Subclasses may override this method to destroy the DnD icon HTML element
 *
 * @see #_getDragProxy
 * @see #_setDragProxyState
 * @see #_isValidDragObject
 * @see #_dragEnter
 * @see #_dragOver
 * @see #_dragHover
 * @see #_dragLeave
 * @see #_drop
 * @see #setDragSource
 * @see DwtDropTarget
 * @see DwtDragSource
 * 
 * @private
 */
DwtControl.prototype._destroyDragProxy =
function(icon) {
	if (icon) {
		// not sure why there is no parent node, but if there isn't one,
		// let's try and do our best to get rid of the icon
		if (icon.parentNode) {
			icon.parentNode.removeChild(icon);
		} else {
			// at least hide the icon, and change the id so we can't get it back later
			icon.style.zIndex = -100;
			icon.id = "DwtJunkIcon" + DwtControl.__junkIconId++;
			icon = null;
		}
	}
};

DwtControl.prototype.destroyDragSelectionBox =
function() {

	var box = this._dragSelectionBox;
	if (box && box.parentNode) {
		box.parentNode.removeChild(box);
	}
	this._dragSelectionBox = null;
};

/**
 * Subclasses may override this method to provide feedback as to whether a possibly
 * valid capture is taking place. For example, there are instances such as when a mouse
 * down happens on a scroll bar in a DwtListView that are reported in the context of
 * the DwtListView, but which are not really a valid mouse down i.e. on a list item. In
 * such cases this function would return false.
 *
 * @return {boolean}	<code>true</code> if the object is a valid drag object
 *
 * @see #_getDragProxy
 * @see #_setDragProxyState
 * @see #_destroyDragProxy
 * @see #_dragEnter
 * @see #_dragOver
 * @see #_dragHover
 * @see #_dragLeave
 * @see #_drop
 * @see #setDragSource
 * @see DwtDropTarget
 * @see DwtDragSource
 * 
 * @private
 */
 DwtControl.prototype._isValidDragObject =
 function(ev) {
 	return true;
 };

/**
 * _dragHover is called multiple times as the user hovers over
 * the control. _dragLeave is called when the drag operation exits the control.
 * _drop is called when the item is dropped on the target.
 */

 /**
  * This protected method is called when a drag operation enters a control. Subclasses
  * supporting drop targets should implement this method to visual indicate that they are a
  * drop target. This could be by changing the background etc. Note that it is the
  * responsibility of the drag source (the control being dragged) to change its icon state
  * to reflect whether the drop target is valid for the drag source
  *
  * @param {DwtMouseEvent} ev	the mouse event that is associated with the drag operation
  *
  * @see #_getDragProxy
  * @see #_setDragProxyState
  * @see #_destroyDragProxy
  * @see #_isValidDragObject
  * @see #_dragOver
  * @see #_dragHover
  * @see #_dragLeave
  * @see #_drop
  * @see #setDragSource
  * @see DwtDropTarget
  * @see DwtDragSource
  * 
  * @private
  */
DwtControl.prototype._dragEnter =
function(ev) {
};

 /**
  * This protected method is called multiple times as a dragged control crosses over this control
  * Subclasses supporting drop targets may implement this method for additional visual
  * indication, such as indicating "landing zones" in the control for drop operations
  *
  * @param {DwtMouseEvent} ev	the mouse event that is associated with the drag operation
  *
  * @see #_getDragProxy
  * @see #_setDragProxyState
  * @see #_destroyDragProxy
  * @see #_isValidDragObject
  * @see #_dragEnter
  * @see #_dragHover
  * @see #_dragLeave
  * @see #_drop
  * @see #setDragSource
  * @see DwtDropTarget
  * @see DwtDragSource
  * @private
  */
DwtControl.prototype._dragOver =
function(ev) {
};

 /**
  * This protected method is called every 750ms as an item hovers over this control
  * Subclasses supporting drop targets may implement this method for additional visual
  * indication or actions, such as expanding a collapsed tree node if the user hovers
  * over the node for a period of time.
  *
  * @param {DwtMouseEvent} ev	the mouse event that is associated with the drag operation
  *
  * @see #_getDragProxy
  * @see #_setDragProxyState
  * @see #_destroyDragProxy
  * @see #_isValidDragObject
  * @see #_dragEnter
  * @see #_dragHover
  * @see #_dragLeave
  * @see #_drop
  * @see #setDragSource
  * @see DwtDropTarget
  * @see DwtDragSource
  * @private
  */
DwtControl.prototype._dragHover =
function(ev) {
};

 /**
  * This protected method is called when the drag operation exits the control
  * Subclasses supporting drop targets should implement this method to reset the
  * visual to the default (i.e. reset the actions performed as part of the
  * <code>_dragEnter</code> method.
  *
  * @param {DwtMouseEvent} ev	the mouse event that is associated with the drag operation
  *
  * @see #_getDragProxy
  * @see #_setDragProxyState
  * @see #_destroyDragProxy
  * @see #_isValidDragObject
  * @see #_dragEnter
  * @see #_dragHover
  * @see #_drop
  * @see #setDragSource
  * @see DwtDropTarget
  * @see DwtDragSource
  * @private
  */
DwtControl.prototype._dragLeave =
function(ev) {
};


/**
  * This protected method is called when the a drop occurs on the control
  * Subclasses supporting drop targets may implement this method to provide a
  * visual indication that the drop succeeded (e.g. an animation such as flashing
  * the drop target).
  *
  * @param {DwtMouseEvent} ev	the mouse event that is associated with the drag operation
  *
  * @see #_getDragProxy
  * @see #_setDragProxyState
  * @see #_destroyDragProxy
  * @see #_isValidDragObject
  * @see #_dragEnter
  * @see #_dragHover
  * @see #_dragLeave
  * @see #setDragSource
  * @see DwtDropTarget
  * @see DwtDragSource
  * @private
  */
DwtControl.prototype._drop =
function(ev) {
};

/**
 * This convenience methods sets or clears the control's event handler for key
 * press events as defined by {@link DwtEvent.ONKEYPRESS}.
 *
 * @param {boolean} clear	if <code>true</code>, clear the keypress events handler
 * @private
 */
DwtControl.prototype._setKeyPressEventHdlr =
function(clear) {
	this._setEventHdlrs([DwtEvent.ONKEYPRESS], clear);
};

/**
 * This convenience methods sets or clears the control's event handlers for mouse
 * events as defined by <i>DwtEvent.MOUSE_EVENTS</i>
 *
 * @param {boolean} clear	if <code>true</code>, clear the mouse events handlers
 * @private
 */
DwtControl.prototype._setMouseEventHdlrs =
function(clear) {
	this._setEventHdlrs(DwtEvent.MOUSE_EVENTS, clear);
};

/**
 * This protected method will set or clear the event handlers for the provided array
 * of events.
 *
 * @param {array} events		an array of events for which to set or clear the
 * 		control's event handlers. The set of events supported by the control are:
 * 		<ul>
 * 		<li><i>DwtEvent.ONCONTEXTMENU</i></li>
 * 		<li><i>DwtEvent.ONCLICK</i></li>
 * 		<li><i>DwtEvent.ONDBLCLICK</i></li>
 * 		<li><i>DwtEvent.ONMOUSEDOWN</i></li>
 * 		<li><i>DwtEvent.ONMOUSEENTER</i></li>
 * 		<li><i>DwtEvent.ONMOUSELEAVE</i></li>
 * 		<li><i>DwtEvent.ONMOUSEMOVE</i></li>
 * 		<li><i>DwtEvent.ONMOUSEOUT</i></li>
 * 		<li><i>DwtEvent.ONMOUSEOVER</i></li>
 * 		<li><i>DwtEvent.ONMOUSEUP</i></li>
 * 		<li><i>DwtEvent.ONMOUSEWHEEL</i></li>
 * 		<li><i>DwtEvent.ONSELECTSTART</i></li>
 * 		<li><i>DwtEvent.ONKEYPRESS</i></li>
 * 		</ul>
 * @param {boolean} clear	if <code>true</code>, the event handlers are cleared for the set of events
 *
 * @see Dwt#setHandler
 * @see Dwt#clearHandler
 * @private
 */
DwtControl.prototype._setEventHdlrs =
function(events, clear) {
	if (!this._checkState()) { return; }

	var htmlElement = this.getHtmlElement();
	for (var i = 0; i < events.length; i++) {
		if (clear !== true) {
			Dwt.setHandler(htmlElement, events[i], DwtControl.__HANDLER[events[i]]);
		} else {
			Dwt.clearHandler(htmlElement, events[i]);
		}
	}
};

/**
 * @private
 */
DwtControl.prototype._setMouseEvents =
function() {
	// add custom mouse handlers to standard ones
	var mouseEvents = [DwtEvent.ONCONTEXTMENU, DwtEvent.ONCLICK, DwtEvent.ONDBLCLICK, DwtEvent.ONMOUSEDOWN,
					   DwtEvent.ONMOUSEMOVE, DwtEvent.ONMOUSEUP, DwtEvent.ONSELECTSTART];
	if (AjxEnv.isIE) {
		mouseEvents.push(DwtEvent.ONMOUSEENTER, DwtEvent.ONMOUSELEAVE);
	} else {
		mouseEvents.push(DwtEvent.ONMOUSEOVER, DwtEvent.ONMOUSEOUT);
	}
	this._setEventHdlrs(mouseEvents);
};

/**
 * Populates a fake mouse event in preparation for the direct call of a listener (rather
 * than via an event handler).
 * 
 * @param {DwtMouseEvent}	mev		the mouse event
 * @param {hash}	params		the hash of event properties
 * 
 * @see DwtUiEvent.copy
 * @private
 */
DwtControl.prototype._setMouseEvent =
function(mev, params) {
	mev.reset();
	params.ersatz = true;
	DwtUiEvent.copy(mev, params);
	mev.button = params.button;
};

/**
 * TODO
 * @private
 */
DwtControl.prototype._getStopPropagationValForMouseEv =
function(ev) {
	// overload me for dealing w/ browsers w/ weird quirks
	return true;
};

/**
 * TODO
 * @private
 */
DwtControl.prototype._getEventReturnValForMouseEv =
function(ev) {
	// overload me for dealing w/ browsers w/ weird quirks
	return false;
};


/**
 * Check the state of the control, if it is not disposed and is not initialized, then
 * as a side-effect it will initialize it (meaning it will create the HTML element
 * for the control and insert it into the DOM. This is pertinent for controls that
 * were created <i>deferred</i> (see the constructor documentation)
 *
 * @return {boolean}	<code>true</code> if the control is not disposed; <code>false</code> otherwise
 * @private
 */
DwtControl.prototype._checkState =
function() {
	if (this._disposed) { return false; }
	if (!this.__ctrlInited) {
		this.__initCtrl();
	}
	return true;
};

/**
 * Positions this control at the given point. If no location is provided, centers it
 * within the shell.
 *
 * @param {DwtPoint}	loc		the point at which to position this control
 * @private
 */
DwtControl.prototype._position =
function(loc) {
	this._checkState();
	var sizeShell = this.shell.getSize();
	var sizeThis = this.getSize();
	var x, y;
	if (!loc) {
		// if no location, go for the middle
		x = Math.round((sizeShell.x - sizeThis.x) / 2);
		y = Math.round((sizeShell.y - sizeThis.y) / 2);
	} else {
		x = loc.x;
		y = loc.y;
	}
	// try to stay within shell boundaries
	if ((x + sizeThis.x) > sizeShell.x) {
		x = sizeShell.x - sizeThis.x;
	}
	if ((y + sizeThis.y) > sizeShell.y) {
		y = sizeShell.y - sizeThis.y;
	}
	this.setLocation(x, y);
};

/**
 * Handles scrolling of a drop area for an object being dragged. The scrolling is based on proximity to
 * the top or bottom edge of the area (only vertical scrolling is done). The scrolling is done via a
 * looping timer, so that the scrolling is smooth and does not depend on additional mouse movement.
 *
 * @param {hash}	params		a hash of parameters
 * @param {Element}      params.container		the DOM element that may need to be scrolled
 * @param {number}      params.threshold		if mouse is within this many pixels of top or bottom of container,
 * 										check if scrolling is needed
 * @param {number}      params.amount		the number of pixels to scroll at each interval
 * @param {number}      params.interval		the number of milliseconds to wait before continuing to scroll
 * @param {string}      params.id			the ID for determining if we have moved out of container
 * @param {DwtEvent}	ev		the event
 * 
 * @private
 */
DwtControl._dndScrollCallback =
function(params, ev) {

	var container = params.container;
	if (!container) { return; }

	// stop scrolling if mouse has moved out of the scrolling area, or dnd object has been released;
	// a bit tricky because this callback is run as the mouse moves among objects within the scroll area,
	// so we need to see if mouse has moved from within to outside of scroll area
	var dwtObjId = ev.dwtObj && ev.dwtObj._dndScrollId;
	if (ev.type == "mouseup" || !dwtObjId || (params.id && dwtObjId != params.id)) {
		if (container._dndScrollActionId != -1) {
			AjxTimedAction.cancelAction(container._dndScrollActionId);
			container._dndScrollActionId = -1;
		}
		return;
	}

	container._scrollAmt = 0;
	if (container.clientHeight < container.scrollHeight) {
		var containerTop = Dwt.toWindow(container, 0, 0, null, null, DwtPoint.tmp).y;
		var realTop = containerTop + container.scrollTop;
		var scroll = container.scrollTop;
		var diff = ev.docY - realTop; // do we need to scroll up?
		// account for horizontal scrollbar
		var threshold = (container.clientWidth < container.scrollWidth) ? params.threshold + Dwt.SCROLLBAR_WIDTH :
																		  params.threshold;
		var scrollAmt = (diff <= threshold) ? -1 * params.amount : 0;
		if (scrollAmt == 0) {
			var containerH = Dwt.getSize(container, DwtPoint.tmp).y;
			var containerBottom = realTop + containerH;
			diff = containerBottom - ev.docY; // do we need to scroll down?
			scrollAmt = (diff <= threshold) ? params.amount : 0;
		}
		container._scrollAmt = scrollAmt;
		if (scrollAmt) {
			if (!container._dndScrollAction) {
				container._dndScrollAction = new AjxTimedAction(null, DwtControl._dndScroll, [params]);
				container._dndScrollActionId = -1;
			}
			// launch scrolling loop
			if (container._dndScrollActionId == -1) {
				container._dndScrollActionId = AjxTimedAction.scheduleAction(container._dndScrollAction, 0);
			}
		} else {
			// stop scrolling
			if (container._dndScrollActionId != -1) {
				AjxTimedAction.cancelAction(container._dndScrollActionId);
				container._dndScrollActionId = -1;
			}
		}
	}
};

/**
 * @private
 */
DwtControl._dndScroll =
function(params) {
	var container = params.container;
	var containerTop = Dwt.toWindow(container, 0, 0, null, null, DwtPoint.tmp).y;
	var containerH = Dwt.getSize(container, DwtPoint.tmp).y;
	var scroll = container.scrollTop;
	// if we are to scroll, make sure there is more scrolling to be done
	if ((container._scrollAmt < 0 && scroll > 0) || (container._scrollAmt > 0 && (scroll + containerH < container.scrollHeight))) {
		container.scrollTop += container._scrollAmt;
		container._dndScrollActionId = AjxTimedAction.scheduleAction(container._dndScrollAction, params.interval);
	}
};

/**
 * @private
 */
DwtControl.__keyPressHdlr =
function(ev) {
	var obj = obj ? obj : DwtControl.getTargetControl(ev);
	if (!obj) return false;

	if (obj.__hasToolTipContent()) {
		var shell = DwtShell.getShell(window);
		var manager = shell.getHoverMgr();
		manager.setHoverOutListener(obj._hoverOutListener);
		manager.hoverOut();
		obj.__tooltipClosed = false;
	}
};

/**
 * Returns true if the control has static tooltip content, or if it has overridden
 * getToolTipContent() to return dynamic content. Essentially, it means that this
 * control provides tooltips and will need to use the hover mgr.
 *
 * @private
 */
DwtControl.prototype.__hasToolTipContent =
function() {
	if (this._disposed) { return false; }
	return Boolean(this.__toolTipContent || (this.getToolTipContent != DwtControl.prototype.getToolTipContent));
};

/**
 * This "private" method is actually called by <i>DwtKeyboardMgr</i> to indicate
 * that the control is being blurred. Subclasses should override the <i>_blur</i>
 * method
 *
 * @private
 */
DwtControl.prototype.__doBlur =
function() {
	DBG.println("focus", "DwtControl.__doBlur for " + this.toString() + ", id: " + this._htmlElId);
	this._hasFocus = false;
	if (this.isListenerRegistered(DwtEvent.ONBLUR)) {
		var ev = DwtShell.focusEvent;
		ev.dwtObj = this;
		ev.state = DwtFocusEvent.BLUR;
		obj.notifyListeners(DwtEvent.ONBLUR, mouseEv);
	}
	this._blur();
};

/**
 * This "private" method is actually called by <i>DwtKeyboardMgr</i> to indicate
 * that the control is being focused. Subclasses should override the <i>_focus</i>
 * method
 *
 * @private
 */
DwtControl.prototype.__doFocus =
function() {
	DBG.println("focus", "DwtControl.__doFocus for " + this.toString() + ", id: " + this._htmlElId);
	this._hasFocus = true;
	if (this.isListenerRegistered(DwtEvent.ONFOCUS)) {
		var ev = DwtShell.focusEvent;
		ev.dwtObj = this;
		ev.state = DwtFocusEvent.FOCUS;
		obj.notifyListeners(DwtEvent.ONFOCUS, mouseEv);
	}
	this._focus();
};

/**
 * @private
 */
DwtControl.__clickHdlr =
function(ev) {

	try {

	return DwtControl.__mouseEvent(ev, DwtEvent.ONCLICK);

	} catch (ex) {
		AjxException.reportScriptError(ex);
	}
};

/**
 * @private
 */
DwtControl.__dblClickHdlr =
function(ev) {

	try {

	var obj = DwtControl.getTargetControl(ev);
	if (obj && obj._dblClickIsolation) {
		obj._clickPending = false;
		AjxTimedAction.cancelAction(obj._dblClickActionId);
	}
	return DwtControl.__mouseEvent(ev, DwtEvent.ONDBLCLICK);

	} catch (ex) {
		AjxException.reportScriptError(ex);
	}
};

/**
 * @private
 */
DwtControl.__mouseOverHdlr =
function(ev, evType) {

	try {

	// Check to see if a drag is occurring. If so, don't process the mouse
	// over events.
	var captureObj = (DwtMouseEventCapture.getId() == "DwtControl") ? DwtMouseEventCapture.getCaptureObj() : null;
	if (captureObj != null) {
		ev = DwtUiEvent.getEvent(ev);
		ev._stopPropagation = true;
		return false;
	}
	var obj = DwtControl.getTargetControl(ev);
	if (!obj) { return false; }
	evType = evType || DwtEvent.ONMOUSEOVER;
	if ((evType == DwtEvent.ONMOUSEOVER) && obj._ignoreInternalOverOut) {
		var otherObj = DwtControl.getTargetControl(ev, true);
		if (obj == otherObj) {
			return false;
		}
	}

	var mouseEv = DwtShell.mouseEvent;
	if (obj._dragging == DwtControl._NO_DRAG) {
		mouseEv.setFromDhtmlEvent(ev, obj);
		mouseEv.hoverStarted = false;	// don't handle hover if it has already begun
		if (obj.isListenerRegistered(evType)) {
			obj.notifyListeners(evType, mouseEv);
		}
		// Call the tooltip after the listeners to give them a
		// chance to change the tooltip text.
		if (obj.__hasToolTipContent(mouseEv) && !mouseEv.hoverStarted) {
			var shell = DwtShell.getShell(window);
			var manager = shell.getHoverMgr();
			if ((!manager.isHovering() || manager.getHoverObject() != obj) && !DwtMenu.menuShowing()) {
				manager.reset();
				manager.setHoverObject(obj);
				manager.setHoverOverData(mouseEv);
				manager.setHoverOverDelay(DwtToolTip.TOOLTIP_DELAY);
				manager.setHoverOverListener(obj._hoverOverListener);
				manager.hoverOver(mouseEv.docX, mouseEv.docY);
			}
		}
	}
	mouseEv._stopPropagation = true;
	mouseEv._returnValue = false;
	mouseEv.setToDhtmlEvent(ev);
	return false;

	} catch (ex) {
		AjxException.reportScriptError(ex);
	}
};

/**
 * @private
 */
DwtControl.__mouseEnterHdlr =
function(ev) {
	return DwtControl.__mouseOverHdlr(ev, DwtEvent.ONMOUSEENTER);
};

/**
 * @private
 */
DwtControl.__mouseDownHdlr =
function(ev) {

	try {

	var obj = DwtControl.getTargetControl(ev);
	if (!obj) { return false; }

	ev = DwtUiEvent.getEvent(ev);
	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev, obj);
	if (mouseEv.button == DwtMouseEvent.LEFT) {
		obj._focusByMouseDownEvent(ev);
		// reset our event - above call can set type to "blur" (at least in FF)
		mouseEv.setFromDhtmlEvent(ev, obj);
	}

	if (obj.__hasToolTipContent()) {
		var shell = DwtShell.getShell(window);
		var manager = shell.getHoverMgr();
		manager.setHoverOutListener(obj._hoverOutListener);
		manager.hoverOut();
	}

	// If we have a dragSource, then we need to start capturing mouse events
	if (obj._dragSource && (mouseEv.button == DwtMouseEvent.LEFT) && obj._isValidDragObject(mouseEv))	{
		try {
			obj._ctrlCaptureObj.capture();
		} catch (ex) {
			DBG.dumpObj(ex);
		}
		obj._dragOp = obj._getDragOp(mouseEv);
		obj.__dragStartX = mouseEv.docX;
		obj.__dragStartY = mouseEv.docY;
	}
	else if (obj._dragBox) {
		// We do mouse capture for drag boxes mostly because the mouseup can come from anywhere, and we
		// want to handle it, usually by destroying the box.
		if (obj._dragBox._setStart(mouseEv, obj)) {
			try {
				obj._ctrlCaptureObj.capture();
			} catch (ex) {
				DBG.dumpObj(ex);
			}
		}
	}

	return DwtControl.__mouseEvent(ev, DwtEvent.ONMOUSEDOWN, obj, mouseEv);

	} catch (ex) {
		AjxException.reportScriptError(ex);
	}
};

/**
 * @private
 */
DwtControl.__mouseMoveHdlr =
function(ev) {

	try {

	// Find the target control. If we're doing capture (DnD), we get it from the capture object.
	var captureObj = (DwtMouseEventCapture.getId() == "DwtControl") ? DwtMouseEventCapture.getCaptureObj() : null;
	var obj = captureObj ? captureObj.targetObj : DwtControl.getTargetControl(ev);
 	if (!obj) { return false; }

	// DnD hover cancel point
	if (obj.__dndHoverActionId != -1) {
		AjxTimedAction.cancelAction(obj.__dndHoverActionId);
		obj.__dndHoverActionId = -1;
	}

	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev, captureObj ? true : obj);

	// This following can happen during a DnD operation if the mouse moves
	// out the window. This seems to happen on IE only.
	if (mouseEv.docX < 0 || mouseEv.docY < 0) {
		mouseEv._stopPropagation = true;
		mouseEv._returnValue = false;
		mouseEv.setToDhtmlEvent(ev);
		return false;
	}

	// If we are not draggable or if we have not started dragging and are
	// within the Drag threshold then handle it as a move.
	var doingDnD = (obj._dragSource && captureObj &&
			(Math.abs(obj.__dragStartX - mouseEv.docX) >= DwtControl.__DRAG_THRESHOLD ||
			 Math.abs(obj.__dragStartY - mouseEv.docY) >= DwtControl.__DRAG_THRESHOLD));
	var doingDragBox = (captureObj && obj._dragBox && obj._dragBox._dragObj == obj);

	if (!doingDnD && !doingDragBox) {
		if (obj.__hasToolTipContent()) {
			var shell = DwtShell.getShell(window);
			var manager = shell.getHoverMgr();
			if (!manager.isHovering() && !obj.__tooltipClosed && !DwtMenu.menuShowing()) {
				// NOTE: mouseOver already init'd other hover settings
				// We do hoverOver() here since the mouse may have moved during
				// the delay, and we want to use latest x,y
				manager.hoverOver(mouseEv.docX, mouseEv.docY);
			} else {
				var deltaX = obj.__lastTooltipX ? Math.abs(mouseEv.docX - obj.__lastTooltipX) : null;
				var deltaY = obj.__lastTooltipY ? Math.abs(mouseEv.docY - obj.__lastTooltipY) : null;
				if ((deltaX != null && deltaX > DwtControl.__TOOLTIP_THRESHOLD) ||
					(deltaY != null && deltaY > DwtControl.__TOOLTIP_THRESHOLD)) {
					manager.setHoverOutListener(obj._hoverOutListener);
					manager.hoverOut();
					obj.__tooltipClosed = true; // prevent tooltip popup during moves in this object
				}
			}
		}
		return DwtControl.__mouseEvent(ev, DwtEvent.ONMOUSEMOVE, obj, mouseEv);
	} else {
		// If we are not dragging, try to begin a drag operation, which may be either DnD or drawing a box.
		if (obj._dragging == DwtControl._NO_DRAG) {
			if (obj._dragSource) {
				obj._dragOp = obj._dragSource._beginDrag(obj._dragOp, obj);
				if (obj._dragOp != Dwt.DND_DROP_NONE) {
					obj._dragging = DwtControl._DRAGGING;
					obj._dndProxy = obj._getDragProxy(obj._dragOp);
					Dwt.addClass(obj._dndProxy, "DwtDragProxy");
					if (!obj._dndProxy) {
						obj._dragging = DwtControl._DRAG_REJECTED;
					}
				} else {
					obj._dragging = DwtControl._DRAG_REJECTED;
				}
			}
			else if (obj._dragBox) {
				obj._dragging = DwtControl._DRAGGING;
				obj._dragBox._beginDrag(obj);
			}
		}

		if (obj._dragging != DwtControl._DRAG_REJECTED) {
			var targetObj = mouseEv.dwtObj;
			if (obj._dragSource) {
				var dropTarget = targetObj && targetObj._dropTarget;
				var lastTargetObj = obj.__lastTargetObj;
				if (targetObj) {
					// Set up the drag hover event. we will even let this item hover over itself as there may be
					// scenarios where that will hold true
					obj._dndHoverAction.args = [ targetObj ];
					obj.__dndHoverActionId = AjxTimedAction.scheduleAction(obj._dndHoverAction, DwtControl.__DND_HOVER_DELAY);
				}

				// See if the target will allow us to be dropped on it. We have to be an allowable type, and the
				// target's drop listener may perform additional checks. The DnD icon will typically turn green or
				// red to indicate whether a drop is allowed.
				if (targetObj && dropTarget && ((targetObj != obj) || dropTarget.hasMultipleTargets())) {
					if (targetObj != lastTargetObj || dropTarget.hasMultipleTargets()) {
						var data = obj._dragSource._getData();
						if (dropTarget._dragEnter(obj._dragOp, targetObj, data, mouseEv, obj._dndProxy)) {
							obj._setDragProxyState(true);
							obj.__dropAllowed = true;
							targetObj._dragEnter(mouseEv);
						} else {
							obj._setDragProxyState(false);
							obj.__dropAllowed = false;
						}
					} else if (obj.__dropAllowed) {
						targetObj._dragOver(mouseEv);
					}
				} else {
					obj._setDragProxyState(false);
				}

				// Tell the previous target that we're no longer being dragged over it.
				if (lastTargetObj && lastTargetObj != targetObj && lastTargetObj._dropTarget && lastTargetObj != obj) {
					// check if obj dragged out of scrollable container
					if (targetObj && !targetObj._dndScrollCallback && lastTargetObj._dndScrollCallback) {
						lastTargetObj._dndScrollCallback.run(mouseEv);
					}

					lastTargetObj._dragLeave(mouseEv);
					lastTargetObj._dropTarget._dragLeave();
				}

				obj.__lastTargetObj = targetObj;

				if ((targetObj != obj) && targetObj && targetObj._dndScrollCallback) {
					targetObj._dndScrollCallback.run(mouseEv);
				}

				// Move the DnD icon. We offset the location slightly so the icon doesn't receive the mousemove events.
				Dwt.setLocation(obj._dndProxy, mouseEv.docX + 2, mouseEv.docY + 2);
			}

			// We keep drawing a drag box as long as we're still over the owning object. We need to check its child
			// objects, and whether we're over the box itself (in case the user reverses direction).
			else if (obj._dragBox) {
				var evTarget = DwtUiEvent.getTarget(ev);
				if (targetObj && (Dwt.isAncestor(obj.getHtmlElement(), evTarget) || evTarget == obj._dragSelectionBox)) {
					obj._dragBox._dragMove(mouseEv, obj);
				}
			}

		} else {
			DwtControl.__mouseEvent(ev, DwtEvent.ONMOUSEMOVE, obj, mouseEv);
		}
		mouseEv._stopPropagation = true;
		mouseEv._returnValue = false;
		mouseEv.setToDhtmlEvent(ev);
		return false;
	}

	} catch (ex) {
		AjxException.reportScriptError(ex);
	}
};

/**
 * @private
 */
DwtControl.__mouseUpHdlr =
function(ev) {

	try {

	// Find the target control. If we're doing capture (DnD), we get it from the capture object.
	var captureObj = (DwtMouseEventCapture.getId() == "DwtControl") ? DwtMouseEventCapture.getCaptureObj() : null;
	var obj = captureObj ? captureObj.targetObj : DwtControl.getTargetControl(ev);
	if (!obj) { return false; }

	// DnD hover cancel point
	if (obj.__dndHoverActionId != -1) {
		AjxTimedAction.cancelAction(obj.__dndHoverActionId);
		obj.__dndHoverActionId = -1;
	}

	var mouseEv = DwtShell.mouseEvent;
	mouseEv.setFromDhtmlEvent(ev, captureObj ? true : obj);
	if (!(captureObj && (obj._dragSource || obj._dragBox))) {
		return DwtControl.__processMouseUpEvent(ev, obj, mouseEv);
	} else {
		captureObj.release();
		if (obj._dragging != DwtControl._DRAGGING) {
			obj._dragging = DwtControl._NO_DRAG;
			return DwtControl.__processMouseUpEvent(ev, obj, mouseEv);
		}
		if (obj._dragSource) {
			obj.__lastTargetObj = null;
			var targetObj = mouseEv.dwtObj;
			var dropTarget = targetObj && targetObj._dropTarget;
			// Perform the drop if the target has allowed it
			if (targetObj && dropTarget && obj.__dropAllowed && ((targetObj != obj) || dropTarget.hasMultipleTargets())) {
				targetObj._drop(mouseEv);
				dropTarget._drop(obj._dragSource._getData(), mouseEv);
				obj._dragSource._endDrag();
				obj._destroyDragProxy(obj._dndProxy);
				obj._dragging = DwtControl._NO_DRAG;
			} else {
				DwtControl.__badDrop(obj, mouseEv);
			}
			if (targetObj && targetObj._dndScrollCallback) {
				targetObj._dndScrollCallback.run(mouseEv);
			}
		}
		else if (obj._dragBox) {
			obj._dragBox._endDrag(obj);
		}
		mouseEv._stopPropagation = true;
		mouseEv._returnValue = false;
		mouseEv.setToDhtmlEvent(ev);
		return false;
	}

	} catch (ex) {
		AjxException.reportScriptError(ex);
	}
};

/**
 * Handles a bad DND drop operation by showing an animation of the icon flying
 * back to its origin.
 * 
 * @param obj		[DwtControl]	control that underlies drag operation
 * @param mouseEv	[DwtMouseEvent]	mouse event
 * @private
 */
DwtControl.__badDrop =
function(obj, mouseEv) {
	obj._dragSource._cancelDrag();
    var targetObj = mouseEv.dwtObj;
    if (targetObj) {
       targetObj._drop(mouseEv);
    }
	// The following code sets up the drop effect for when an
	// item is dropped onto an invalid target. Basically the
	// drag icon will spring back to its starting location.
	obj.__dragEndX = mouseEv.docX;
	obj.__dragEndY = mouseEv.docY;
	if (obj.__badDropAction == null) {
		obj.__badDropAction = new AjxTimedAction(obj, obj.__badDropEffect);
	}

	// Line equation is y = mx + c. Solve for c, and set up d (direction)
	var m = (obj.__dragEndY - obj.__dragStartY) / (obj.__dragEndX - obj.__dragStartX);
	obj.__badDropAction.args = [m, obj.__dragStartY - (m * obj.__dragStartX), (obj.__dragStartX - obj.__dragEndX < 0) ? -1 : 1];
	AjxTimedAction.scheduleAction(obj.__badDropAction, 0);
};

/**
 * Handle double clicks in isolation, if requested (if not, events are handled
 * normally). On the first click, we set a 'click pending' flag and start a timer.
 * If the timer expires before another click arrives, we process the single click.
 * If a double-click event arrives before the timer expires, then we process the
 * double-click event.
 * @private
 */
DwtControl.__processMouseUpEvent =
function(ev, obj, mouseEv) {
	var shell = DwtShell.getShell(window);
	var hoverMgr = shell.getHoverMgr();
	hoverMgr.ignoreHoverOverOnClick();

	if (obj._dblClickIsolation && mouseEv && (mouseEv.button == DwtMouseEvent.LEFT)) {
		if (obj._clickPending) {
			// wait for real dblclick event
			return false;
		} else {
			obj._clickPending = true;
			var ta = new AjxTimedAction(null, DwtControl.__timedClick, [ev, obj, mouseEv]);
			obj._dblClickActionId = AjxTimedAction.scheduleAction(ta, DwtControl.__DBL_CLICK_TIMEOUT);
			DwtUiEvent.setBehaviour(ev, true, false);
			obj._st = new Date();
			return false;
		}
	} else {
		obj._clickPending = false;
		return DwtControl.__mouseEvent(ev, DwtEvent.ONMOUSEUP, obj, mouseEv);
	}
};

DwtControl.__timedClick =
function(ev, obj, mouseEv) {
	obj._clickPending = false;
	DwtControl.__mouseEvent(ev, DwtEvent.ONMOUSEUP, obj, mouseEv);
};

/**
 * @private
 */
DwtControl.__mouseOutHdlr =
function(ev, evType) {

	try {

	var obj = DwtControl.getTargetControl(ev);
	if (!obj) { return false; }
	evType = evType || DwtEvent.ONMOUSEOUT;
	if ((evType == DwtEvent.ONMOUSEOUT) && obj._ignoreInternalOverOut) {
		var otherObj = DwtControl.getTargetControl(ev, true);
		if (obj == otherObj) {
			return false;
		}
	}

	if (obj.__hasToolTipContent()) {
		var shell = DwtShell.getShell(window);
		var manager = shell.getHoverMgr();
			manager.setHoverOutListener(obj._hoverOutListener);
			manager.hoverOut();
			obj.__tooltipClosed = false;
	}
	return DwtControl.__mouseEvent(ev, evType || DwtEvent.ONMOUSEOUT, obj);

	} catch (ex) {
		AjxException.reportScriptError(ex);
	}
};

/**
 * @private
 */
DwtControl.__mouseLeaveHdlr =
function(ev) {
	return DwtControl.__mouseOutHdlr(ev, DwtEvent.ONMOUSELEAVE);
};

/**
 * @private
 */
DwtControl.__mouseWheelHdlr =
function(ev) {

	try {

	var obj = DwtControl.getTargetControl(ev);
	if (!obj) return false;
	return DwtControl.__mouseEvent(ev, DwtEvent.ONMOUSEWHEEL, obj);

	} catch (ex) {
		AjxException.reportScriptError(ex);
	}
};

/**
 * @private
 */
DwtControl.__selectStartHdlr =
function(ev) {

	try {

	return DwtControl.__mouseEvent(ev, DwtEvent.ONSELECTSTART);

	} catch (ex) {
		AjxException.reportScriptError(ex);
	}
};

/**
 * Note: if there is also a mousedown handler, oncontextmenu is no longer sent, so be careful.
 *
 * @private
 */
DwtControl.__contextMenuHdlr =
function(ev) {

	try {

	// for Safari, we have to fake a right click
	if (AjxEnv.isSafari) {
		var obj = DwtControl.getTargetControl(ev);
		var prevent = obj ? obj.preventContextMenu() : true;
		if (prevent) {
			DwtControl.__mouseEvent(ev, DwtEvent.ONMOUSEDOWN);
			return DwtControl.__mouseEvent(ev, DwtEvent.ONMOUSEUP);
		}
	}
	return DwtControl.__mouseEvent(ev, DwtEvent.ONCONTEXTMENU);

	} catch (ex) {
		AjxException.reportScriptError(ex);
	}
};

/**
 * @private
 */
DwtControl.__mouseEvent =
function(ev, eventType, obj, mouseEv) {
	var obj = obj ? obj : DwtControl.getTargetControl(ev);
	if (!obj) { return false; }

	if (!mouseEv) {
		mouseEv = DwtShell.mouseEvent;
		mouseEv.setFromDhtmlEvent(ev, obj);
	}

	// By default, we halt event processing. Listeners may override
	var tn = mouseEv.target.tagName && mouseEv.target.tagName.toLowerCase();
	if (!tn || (tn != "input" && tn != "textarea" && tn != "a")) {
		mouseEv._stopPropagation = true;
		mouseEv._returnValue = false;
	} else {
		mouseEv._stopPropagation = false;
		mouseEv._returnValue = true;
	}

	// notify global listeners
	DwtEventManager.notifyListeners(eventType, mouseEv);

	// notify widget listeners
	if (obj.isListenerRegistered && obj.isListenerRegistered(eventType)) {
		obj.notifyListeners(eventType, mouseEv);
	}

	// publish our settings to the DOM
	mouseEv.setToDhtmlEvent(ev);
	return mouseEv._returnValue;
};

// need to populate this hash after methods are defined
/**
 * @private
 */
DwtControl.__HANDLER = {};
DwtControl.__HANDLER[DwtEvent.ONCONTEXTMENU] = DwtControl.__contextMenuHdlr;
DwtControl.__HANDLER[DwtEvent.ONCLICK] = DwtControl.__clickHdlr;
DwtControl.__HANDLER[DwtEvent.ONDBLCLICK] = DwtControl.__dblClickHdlr;
DwtControl.__HANDLER[DwtEvent.ONMOUSEDOWN] = DwtControl.__mouseDownHdlr;
DwtControl.__HANDLER[DwtEvent.ONMOUSEENTER] = DwtControl.__mouseEnterHdlr;
DwtControl.__HANDLER[DwtEvent.ONMOUSELEAVE] = DwtControl.__mouseLeaveHdlr;
DwtControl.__HANDLER[DwtEvent.ONMOUSEMOVE] = DwtControl.__mouseMoveHdlr;
DwtControl.__HANDLER[DwtEvent.ONMOUSEOUT] = DwtControl.__mouseOutHdlr;
DwtControl.__HANDLER[DwtEvent.ONMOUSEOVER] = DwtControl.__mouseOverHdlr;
DwtControl.__HANDLER[DwtEvent.ONMOUSEUP] = DwtControl.__mouseUpHdlr;
DwtControl.__HANDLER[DwtEvent.ONMOUSEWHEEL] = DwtControl.__mouseWheelHdlr;
DwtControl.__HANDLER[DwtEvent.ONSELECTSTART] = DwtControl.__selectStartHdlr;
DwtControl.__HANDLER[DwtEvent.ONKEYPRESS] = DwtControl.__keyPressHdlr;

/**
 * @private
 */
DwtControl.prototype.__initCtrl =
function() {
	this.shell = this.parent.shell || this.parent;
	// __internalId is for back-compatibility (was side effect of Dwt.associateElementWithObject)
	this._htmlElId = this.__internalId = this._htmlElId || Dwt.getNextId();
	var htmlElement = this._elRef = this._createElement(this._htmlElId);
	htmlElement.id = this._htmlElId; 
	if (DwtControl.ALL_BY_ID) {
		if (DwtControl.ALL_BY_ID[this._htmlElId]) {
			DBG.println(AjxDebug.DBG1, "Duplicate ID for " + this.toString() + ": " + this._htmlElId);
			this._htmlElId = htmlElement.id = this.__internalId = DwtId.makeId(this._htmlElId, Dwt.getNextId());
		}
		DwtControl.ALL_BY_ID[this._htmlElId] = this;
	}
	DwtComposite._pendingElements[this._htmlElId] = htmlElement;
	htmlElement.style.position = this.__posStyle || DwtControl.STATIC_STYLE;
	htmlElement.className = this._className;
	htmlElement.style.overflow = "visible";
	this._enabled = true;
	this.__controlEvent = DwtControl.__controlEvent;
	this._dragging = DwtControl._NO_DRAG;
	this.__ctrlInited = true;

	// Make sure this is the last thing we do
	this.parent.addChild(this, this.__index);
};

/**
 * Returns the container element to be used for this control.
 * <p>
 * <strong>Note:</strong>
 * The caller will overwrite the id of the returned element with the
 * specified id.
 *
 * @param id [string] The id of the container element.
 * @private
 */
DwtControl.prototype._createElement = function(id) {
	return document.createElement("DIV")
};

/**
 * @private
 */
DwtControl.prototype.__dndDoHover =
function(control) {
	//TODO Add allow hover?
	control._dragHover();
};

/**
 * This method is called when a drop happens on an invalid target. The code will
 * animate the Drag icon back to its source before destroying it via <code>_destroyDragProxy</code>
 * @private
 */
DwtControl.prototype.__badDropEffect =
function(m, c, d) {
	var usingX = (Math.abs(m) <= 1);
	// Use the bigger delta to control the snap effect
	var delta = usingX ? this.__dragStartX - this.__dragEndX : this.__dragStartY - this.__dragEndY;
    if (delta * d > 0 && !(this.__dragEndY == this.__dragStartY || this.__dragEndX == this.__dragStartX) ) {
		if (usingX) {
			this.__dragEndX += (30 * d);
			this._dndProxy.style.top = m * this.__dragEndX + c;
			this._dndProxy.style.left = this.__dragEndX;
		} else {
			this.__dragEndY += (30 * d);
			this._dndProxy.style.top = this.__dragEndY;
			this._dndProxy.style.left = (this.__dragEndY - c) / m;
		}
		AjxTimedAction.scheduleAction(this.__badDropAction, 0);
 	} else {
  		this._destroyDragProxy(this._dndProxy);
		this._dragging = DwtControl._NO_DRAG;
  	}
};

/**
 * Attempts to display a tooltip for this control, triggered by the cursor having been
 * over the control for a period of time. The tooltip may have already been set (if it's
 * a static tooltip). For dynamic tooltip content, the control implements getToolTipContent()
 * to return the content or a callback. It should return a callback if it makes an
 * async server call to get data.
 *
 * @private
 */
DwtControl.prototype.__handleHoverOver =
function(event) {

	if (this._eventMgr.isListenerRegistered(DwtEvent.HOVEROVER)) {
		this._eventMgr.notifyListeners(DwtEvent.HOVEROVER, event);
	}

	var mouseEv = event && event.object;
	var tooltip = this.getToolTipContent(mouseEv);
	var content, callback;
	if (!tooltip) {
		content = "";
	} else if (typeof(tooltip) == "string") {
		content = tooltip;
	} else if (tooltip instanceof AjxCallback) {
		callback = tooltip;
	} else if (typeof(tooltip) == "object") {
		content = tooltip.content;
		callback = tooltip.callback;
	}

	if (!content && callback && tooltip.loading) {
		content = AjxMsg.loading;
	}

	if (content) {
		this.__showToolTip(event, content);
	}

	if (callback) {
		var callback1 = new AjxCallback(this, this.__showToolTip, [event]);
		AjxTimedAction.scheduleAction(new AjxTimedAction(null, function() { callback.run(callback1); }), 0);
	}
};

/**
 * @private
 */
DwtControl.prototype.__showToolTip =
function(event, content) {

	if (!content) { return; }
    DwtControl.showToolTip(content, event.x, event.y);
	this.__lastTooltipX = event.x;
	this.__lastTooltipY = event.y;
	this.__tooltipClosed = false;
};

/**
 * @private
 */
DwtControl.prototype.__handleHoverOut =
function(event) {
	if (this._eventMgr.isListenerRegistered(DwtEvent.HOVEROUT)) {
		this._eventMgr.notifyListeners(DwtEvent.HOVEROUT, event);
	}
    DwtControl.hideToolTip();
	this.__lastTooltipX = null;
	this.__lastTooltipY = null;
};

/**
 * @private
 */
DwtControl.prototype.__isInputEl =
function(targetEl) {
	var bIsInput = false;
	if(!targetEl || !targetEl.tagName) {
		return bIsInput;
	}
	var tagName = targetEl.tagName.toLowerCase();
	var type = tagName == "input" ? targetEl.type.toLowerCase() : null;

	if (tagName == "textarea" || (type && (type == "text" || type == "password")))
		bIsInput = true;

	return bIsInput;
};


/**
 * onunload hacking
 * @private
 */
DwtControl.ON_UNLOAD =
function() {
	// break widget-element references
	var h = DwtControl.ALL_BY_ID, i;
	for (i in h) {
		h[i]._elRef = null;
	}
	DwtControl.ALL_BY_ID = null;
};

if (AjxEnv.isIE) {
	window.attachEvent("onunload", DwtControl.ON_UNLOAD);
} else {
	window.addEventListener("unload", DwtControl.ON_UNLOAD, false);
}

/**
 *  A helper method to show the toolTips.
 * @param content
 * @param x [Number] The x coordinate of the toolTip.
 * @param y [Number] The y coordinate of the toolTip.
 */
DwtControl.showToolTip =
function(content, x, y) {
	if (!content) { return; }
	var shell = DwtShell.getShell(window);
	var tooltip = shell.getToolTip();
	tooltip.setContent(content);
	tooltip.popup(x, y);
}

/**
 * A helper method to hide the toolTip.
 */
DwtControl.hideToolTip =
function() {
	var shell = DwtShell.getShell(window);
	var tooltip = shell.getToolTip();
	tooltip.popdown();
}
