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
 */

/**
 * Creates the action status view.
 * @class
 * This class represents the action status view.
 *
 * @param    {DwtControl}    parent        the parent
 * @param    {String}        className     the class name
 * @param    {constant}      posStyle      the position style
 * @param    {String}        id            the id
 *
 * @extends		DwtControl
 */
ZaActionStatusView = function(parent, className, posStyle, id) {

    DwtControl.call(this, {parent:parent, className:(className || "ZaActionStatus"), posStyle:posStyle, id:id});

    this._toast = this._standardToast = new ZaToast(this, ZaId.TOAST);
    this._statusQueue = [];
};

ZaActionStatusView.prototype = new DwtControl;
ZaActionStatusView.prototype.constructor = ZaActionStatusView;


// Constants
/**
 * Defines the "informational" status level.
 */
ZaActionStatusView.LEVEL_INFO             = 1;    // informational
/**
 * Defines the "warning" status level.
 */
ZaActionStatusView.LEVEL_WARNING          = 2;    // warning
/**
 * Defines the "critical" status level.
 */
ZaActionStatusView.LEVEL_CRITICAL         = 3;    // critical

ZaActionStatusView.MSG_PARAMS = ["msg", "level", "detail", "transitions", "toast", "force", "dismissCallback", "finishCallback"];

// Public methods

ZaActionStatusView.prototype.toString =
function() {
    return "ZaActionStatusView";
};

/**
 * Displays a status message.
 *
 * @param {String}    msg the message
 * @param {constant}    [level]         the level (see {@link ZaActionStatusView}<code>.LEVEL_</code> constants)
 * @param {String}    [detail]         the details
 * @param {String}    [transitions] the transitions (see {@link ZaToast})
 * @param {String}    [toast]     the toast control
 * @param {boolean}    [force]        force any displayed toasts out of the way
 * @param {AjxCallback}    [dismissCallback]    callback to run when the toast is dismissed (by another message using [force], or explicitly calling ZaActionStatusView.prototype.dismiss())
 * @param {AjxCallback}    [finishCallback]     callback to run when the toast finishes its transitions by itself (not when dismissed)
 */
ZaActionStatusView.prototype.setStatusMsg =
function(params) {
    params = Dwt.getParams(arguments, ZaActionStatusView.MSG_PARAMS);
    if (typeof params == "string") {
        params = { msg: params };
    }
    var work = {
        msg: params.msg,
        level: params.level || ZaActionStatusView.LEVEL_INFO,
        detail: params.detail,
        date: new Date(),
        transitions: params.transitions,
        toast: params.toast || this._standardToast,
        dismissCallback: (params.dismissCallback instanceof AjxCallback) ? params.dismissCallback : null,
        finishCallback: (params.finishCallback instanceof AjxCallback) ? params.finishCallback : null,
		dismissed: false
    };

	if (params.force) { // We want to dismiss ALL messages in the queue and display the new message
		for (var i=0; i<this._statusQueue.length; i++) {
			this._statusQueue[i].dismissed = true; // Dismiss all messages in the queue in turn, calling their dismissCallbacks along the way
		}
	}
    // always push so we know one is active
    this._statusQueue.push(work);
    if (!this._toast.isPoppedUp()) {
        this._updateStatusMsg();
    } else if (params.force) {
        this.dismissStatusMsg();
    }
};

ZaActionStatusView.prototype.nextStatus =
function() {
    if (this._statusQueue.length > 0) {
        this._updateStatusMsg();
        return true;
    }
    return false;
};

ZaActionStatusView.prototype.dismissStatusMsg =
function(all) {
	if (all) {
		for (var i=0; i<this._statusQueue.length; i++) {
			this._statusQueue[i].dismissed = true; // Dismiss all messages in the queue in turn, calling their dismissCallbacks along the way
		}
	}
    this._toast.dismiss();
};

// Static functions

/**
 * Gets the style class name based on status level.
 *
 * @param     {ZaActionStatusView}        work        the view
 * @return    {String}                          the class
 */
ZaActionStatusView.getClass =
function(work) {
    switch (work.level) {
        case ZaActionStatusView.LEVEL_CRITICAL:    return "ZaToastCrit";
        case ZaActionStatusView.LEVEL_WARNING:     return "ZaToastWarn";
        default:                             return "ZaToastInfo";
    }
};

/**
 * Gets the image based on status level.
 *
 * @param     {ZaActionStatusView}        work        the view
 * @return    {String}                          the image
 */
ZaActionStatusView.getImageHtml =
function(work) {
    switch (work.level) {
        case ZaActionStatusView.LEVEL_CRITICAL:    return "Critical";
        case ZaActionStatusView.LEVEL_WARNING:     return "Warning";
        default:                             return "Success";
    }
};


// Protected methods

ZaActionStatusView.prototype._updateStatusMsg =
function() {
    var work = this._statusQueue.shift();
    if (!work) { return; }
	if (work.dismissed) { // If preemptively dismissed, just run the callback and proceed to the next msg
		if (work.dismissCallback)
			work.dismissCallback.run();
		this.nextStatus();
	} else {
		var level = ZaActionStatusView.getClass(work);
		var icon = ZaActionStatusView.getImageHtml(work);

		this._toast = work.toast;
		this._toast.popup(level, work.msg, icon, null, work.transitions, work.dismissCallback, work.finishCallback);
	}
};


//
// ZaToast
//

/**
 * Creates the "toaster".
 * @class
 * This class represents the "toaster".
 *
 * @extends	DwtComposite
 */
ZaToast = function(parent, id) {
    if (arguments.length == 0) { return; }

    DwtComposite.call(this, {parent:parent.shell, className:"ZaToast", posStyle:Dwt.ABSOLUTE_STYLE, id:id});
    this._statusView = parent;
    this._createHtml();

    this._funcs = {};
    this._funcs["position"] = AjxCallback.simpleClosure(this.__position, this);
    this._funcs["show"] = AjxCallback.simpleClosure(this.__show, this);
    this._funcs["hide"] = AjxCallback.simpleClosure(this.__hide, this);
    this._funcs["pause"] = AjxCallback.simpleClosure(this.__pause, this);
    this._funcs["hold"] = AjxCallback.simpleClosure(this.__hold, this);
    this._funcs["idle"] = AjxCallback.simpleClosure(this.__idle, this);
    this._funcs["fade"] = AjxCallback.simpleClosure(this.__fade, this);
    this._funcs["fade-in"] = this._funcs["fade"];
    this._funcs["fade-out"] = this._funcs["fade"];
    this._funcs["slide"] = AjxCallback.simpleClosure(this.__slide, this);
    this._funcs["slide-in"] = this._funcs["slide"];
    this._funcs["slide-out"] = this._funcs["slide"];
    this._funcs["next"] = AjxCallback.simpleClosure(this.transition, this);
}
ZaToast.prototype = new DwtComposite;
ZaToast.prototype.constructor = ZaToast;
ZaToast.prototype.toString =
function() {
    return "ZaToast";
};

// Constants
/**
 * Defines the "fade" transition.
 */
ZaToast.FADE = { type: "fade" };
/**
 * Defines the "fade-in" transition.
 */
ZaToast.FADE_IN = { type: "fade-in" };
/**
 * Defines the "fade-out" transition.
 */
ZaToast.FADE_OUT = { type: "fade-out" };
/**
 * Defines the "slide" transition.
 */
ZaToast.SLIDE = { type: "slide" };
/**
 * Defines the "slide-in" transition.
 */
ZaToast.SLIDE_IN = { type: "slide-in" };
/**
 * Defines the "slide-out" transition.
 */
ZaToast.SLIDE_OUT = { type: "slide-out" };
/**
 * Defines the "pause" transition.
 */
ZaToast.PAUSE = { type: "pause" };
/**
 * Defines the "hold" transition.
 */
ZaToast.HOLD = { type: "hold" };
/**
 * Defines the "idle" transition.
 */
ZaToast.IDLE = {type: "idle" };
/**
 * Defines the "show" transition.
 */
ZaToast.SHOW = {type: "show" };

ZaToast.DEFAULT_TRANSITIONS = [ZaToast.FADE_IN, ZaToast.PAUSE, ZaToast.FADE_OUT];

ZaToast.DEFAULT_STATE = {};
ZaToast.DEFAULT_STATE["position"] = { location: "C" }; // center
ZaToast.DEFAULT_STATE["pause"] = { duration: 3200 };
ZaToast.DEFAULT_STATE["hold"] = {};
ZaToast.DEFAULT_STATE["fade"] = { duration: 100, multiplier: 1 };
ZaToast.DEFAULT_STATE["fade-in"] = { start: 0, end: 99, step: 10, duration: 200, multiplier: 1 };
ZaToast.DEFAULT_STATE["fade-out"] = { start: 99, end: 0, step: -10, duration: 200, multiplier: 1 };
ZaToast.DEFAULT_STATE["slide"] = { duration: 100, multiplier: 1 };
ZaToast.DEFAULT_STATE["slide-in"] = { start: -40, end: 0, step: 1, duration: 100, multiplier: 1 };
ZaToast.DEFAULT_STATE["slide-out"] = { start: 0, end: -40, step: -1, duration: 100, multiplier: 1 };

ZaToast.LEVEL_RE = /\b(ZaToastCrit|ZaToastWarn|ZaToastInfo)\b/g;
ZaToast.DISMISSABLE_STATES = [ZaToast.HOLD];

// Data

ZaToast.prototype.TEMPLATE = "admin.Widgets#ZaToast";


// Public methods

ZaToast.prototype.dispose =
function() {
    this._textEl = null;
    this._iconEl = null;
    this._detailEl = null;
    DwtComposite.prototype.dispose.call(this);
};

ZaToast.prototype.popup =
function(level, text, icon, loc, customTransitions, dismissCallback, finishCallback) {
    this.__clear();
    this._poppedUp = true;
    this._dismissed = false;
    this._dismissCallback = dismissCallback;
    this._finishCallback = finishCallback;

    // setup display
    var el = this.getHtmlElement();
    Dwt.delClass(el, ZaToast.LEVEL_RE, level || "ZaToastInfo")

    if (this._textEl) {
        this._textEl.innerHTML = text || "";
    }

    if (this._iconEl) {
        AjxImg.setImage(this._iconEl, icon, false);
    }

    // get transitions
    var location =  loc || "C" ;
    var transitions = customTransitions || ZaToast.DEFAULT_TRANSITIONS;

    transitions = [].concat( {type:"position", location:location}, transitions, {type:"hide"} );

    // start animation
    this._transitions = transitions;
    this.transition();
};

ZaToast.prototype.popdown =
function() {
    this.__clear();
    Dwt.setLocation(this.getHtmlElement(), Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
    this._poppedUp = false;
    if (!this._dismissed) {
        if (this._finishCallback)
            this._finishCallback.run();
    }
    this._dismissed = false;
};

ZaToast.prototype.isPoppedUp =
function() {
    return this._poppedUp;
};

ZaToast.prototype.transition =
function() {

    if (this._pauseTimer) {
        clearTimeout(this._pauseTimer);
        this._pauseTimer = null;
    }
    if (this._held) {
        this._held = false;
    }

    var transition = this._transitions && this._transitions.shift();
    if (!transition) {
        this._poppedUp = false;
        if (!this._statusView.nextStatus()) {
            this.popdown();
        }
        return;
    }

    var state = this._state = this._createState(transition);

    var el = this.getHtmlElement();
    //Dwt.setOpacity(el, state.opacity);
    Dwt.setLocation(el, state.x, state.y);

    this._funcs[transition.type || "next"]();
};

// Protected methods

ZaToast.prototype._createHtml =
function(templateId) {
    var data = { id: this._htmlElId };
    this._createHtmlFromTemplate(templateId || this.TEMPLATE, data);
    Dwt.setZIndex(this.getHtmlElement(), Dwt.Z_TOAST);
};

ZaToast.prototype._createHtmlFromTemplate =
function(templateId, data) {
    DwtComposite.prototype._createHtmlFromTemplate.call(this, templateId, data);
    this._textEl = document.getElementById(data.id+"_text");
    this._iconEl = document.getElementById(data.id+"_icon");
    this._detailEl = document.getElementById(data.id+"_detail");
};

ZaToast.prototype._createState =
function(transition) {
    var state = AjxUtil.createProxy(transition);
    var defaults = ZaToast.DEFAULT_STATE[state.type];
    for (var name in defaults) {
        if (!state[name]) {
            state[name] = defaults[name];
        }
    }
    var el = this.getHtmlElement();

    switch (state.type) {
        case "fade-in":
            Dwt.setOpacity(el, 0);
            Dwt.setLocation(el, null, null);
            state.value = state.start;
            break;
        case "fade-out":
        case "fade":
            Dwt.setLocation(el, null, null);
            state.value = state.start;
            break;
        case "slide-in":
        case "slide-out":
        case "slide":{
            Dwt.setLocation(el, null, -36);
            Dwt.setOpacity(el, 100);
            state.value = state.start;
            break;
        }
    }
    return state;
};

// Private methods

ZaToast.prototype.__clear =
function() {
    clearTimeout(this._actionId);
    clearInterval(this._actionId);
    this._actionId = -1;
};

// transition handlers

ZaToast.prototype.__position =
function() {
    var location = this._state.location || "C";
    var containerId;
    if (!appNewUI)
        containerId= "skin_container_app_tabs";
    else
        containerId = "skin_container_toaster";
    var el = this.getHtmlElement();
    var container = Dwt.byId(containerId) || this.shell.getHtmlElement();

    var bsize = Dwt.getSize(container);
    var tsize = Dwt.getSize(el);

    var x = (bsize.x - tsize.x) / 2;
    var y = (bsize.y - tsize.y) / 2;

    switch (location.toUpperCase()) {
        case 'N': y = 0-tsize.y; break;
        case 'S': y = bsize.y - tsize.y; break;
        case 'E': x = bsize.x - tsize.x; break;
        case 'W': x = 0; break;
        case 'NE': x = bsize.x - tsize.x; y = 0; break;
        case 'NW': x = 0; y = 0; break;
        case 'SE': x = bsize.x - tsize.x; y = bsize.y - tsize.y; break;
        case 'SW': x = 0; y = bsize.y - tsize.y; break;
        case 'C': default: /* nothing to do */ break;
    }

    var offset = Dwt.toWindow(container);
    x += offset.x;
    y += offset.y;

    x = (x>0) ? x : 0;
    y = (y>0) ? y : 0;
    Dwt.setLocation(el, x, y);

    this._funcs["next"]();
};

ZaToast.prototype.__show =
function() {
    var el = this.getHtmlElement();
    Dwt.setVisible(el, true);
    Dwt.setVisibility(el, true);
    this._funcs["next"]();
};

ZaToast.prototype.__hide =
function() {
    var el = this.getHtmlElement();
    Dwt.setLocation(el, Dwt.LOC_NOWHERE, Dwt.LOC_NOWHERE);
    this._funcs["next"]();
};

ZaToast.prototype.__pause =
function() {
    if (this._dismissed && ZaToast.__mayDismiss(ZaToast.PAUSE)) {
        this._funcs["next"]();
    } else {
        this._pauseTimer = setTimeout(this._funcs["next"], this._state.duration);
    }
};


/**
 * Hold the toast in place until dismiss() is called. If dismiss() was already called before this function (ie. during fade/slide in), continue immediately
 */
ZaToast.prototype.__hold =
function() {
    if (this._dismissed && ZaToast.__mayDismiss(ZaToast.HOLD)!=-1) {
        this._funcs["next"]();
    } else {
        this._held = true;
    }
};

ZaToast.__mayDismiss =
function(state) {
    return AjxUtil.indexOf(ZaToast.DISMISSABLE_STATES, state)!=-1;
};

/**
 * Dismiss (continue) a held or paused toast (Given that ZaToast.DISMISSABLE_STATES agrees). If not yet held or paused, those states will be skipped when they occur
 */
ZaToast.prototype.dismiss =
function() {
    if (!this._dismissed && this._poppedUp) {
        var doDismiss = (this._pauseTimer && ZaToast.__mayDismiss(ZaToast.PAUSE)) ||
            (this._held && ZaToast.__mayDismiss(ZaToast.HOLD));
        if (doDismiss) {
            this._funcs["next"]();
        }
        this._dismissed = true;
        if (this._dismissCallback instanceof AjxCallback) {
            this._dismissCallback.run();
        }
    }
};

ZaToast.prototype.__idle =
function() {
    if (!this._idleTimer) {
        this._idleTimer = new DwtIdleTimer(0, new AjxCallback(this, this.__idleCallback));
    } else {
        this._idleTimer.resurrect(0);
    }
};

ZaToast.prototype.__idleCallback =
function(idle) {
    if (!idle) {
        this.transition();
        this._idleTimer.kill();
    }
};

ZaToast.prototype.__move =
function() {
    // TODO
    this._funcs["next"]();
};

ZaToast.prototype.__fade =
function() {
    var opacity = this._state.value;
    var step = this._state.step;

    // NOTE: IE is slow re-rendering when adjusting opacity. So we try
    //       to do it in an IE-optimized way.
    if (AjxEnv.isIE) {
        if (AjxEnv.isIE5_5up) {
            try {
                var el = this.getHtmlElement();
                el.style.visibility = step > 0 ? "hidden" : "visible";

                var duration = this._state.duration / 1000;
                el.style.filter = "progid:DXImageTransform.Microsoft.Fade(duration="+duration+",overlap=1.0)";

                el.filters[0].Apply();
                el.style.visibility = step > 0 ? "visible" : "hidden";
                el.filters[0].Play();
            }
            catch (e) {
                DBG.println("error: "+e);
            }
        }
        setTimeout(this._funcs["next"], 0);
        return;
    }

    var isOver = step > 0 ? opacity >= this._state.end : opacity <= this._state.end;
    if (isOver) {
        opacity = this._state.end;
    }

    var el = this.getHtmlElement();
    Dwt.setOpacity(el, opacity);

    if (isOver) {
        this.__clear();
        setTimeout(this._funcs["next"], 0);
        return;
    }

    if (this._actionId == -1) {
        var duration = this._state.duration;
        var delta = duration / Math.abs(step);
        this._actionId = setInterval(this._funcs["fade"], delta);
    }

    this._state.value += step;
    this._state.step *= this._state.multiplier;
};

ZaToast.prototype.__slide =
function() {
    var top = this._state.value;
    var step = this._state.step;

    var isOver = step > 0 ? top >= this._state.end : top <= this._state.end;
    if (isOver) {
        top = this._state.end;
    }

    var el = this.getHtmlElement();
    //Dwt.setOpacity(el, opacity);
    Dwt.setLocation(el, null, top);
    //el.style.top = top+'px';


    if (isOver) {
        this.__clear();
        setTimeout(this._funcs["next"], 0);
        return;
    }

    if (this._actionId == -1) {
        var duration = this._state.duration;
        var delta = duration / Math.abs(step);
        this._actionId = setInterval(this._funcs["slide"], delta);
    }

    this._state.value += step;
    this._state.step *= this._state.multiplier;
};

