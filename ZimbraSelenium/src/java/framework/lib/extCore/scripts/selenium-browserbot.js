/*
* Copyright 2004 ThoughtWorks, Inc
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*
*/

/*
* This script provides the Javascript API to drive the test application contained within
* a Browser Window.
* TODO:
*    Add support for more events (keyboard and mouse)
*    Allow to switch "user-entry" mode from mouse-based to keyboard-based, firing different
*          events in different modes.
*/

// The window to which the commands will be sent.  For example, to click on a
// popup window, first select that window, and then do a normal click command.
var BrowserBot = function(topLevelApplicationWindow) {
    this.topWindow = topLevelApplicationWindow;
    this.topFrame = this.topWindow;
    this.baseUrl=window.location.href;
	this.dontFailOnTimeout = false; //used for -ve testing
    // the buttonWindow is the Selenium window
    // it contains the Run/Pause buttons... this should *not* be the AUT window
    this.buttonWindow = window;
    this.currentWindow = this.topWindow;
    this.currentWindowName = null;
    this.allowNativeXpath = true;
    this.xpathLibrary = 'ajaxslt' // change to "javascript-xpath" for the newer, faster engine

    // We need to know this in advance, in case the frame closes unexpectedly
    this.isSubFrameSelected = false;

    this.altKeyDown = false;
    this.controlKeyDown = false;
    this.shiftKeyDown = false;
    this.metaKeyDown = false;
	this.rightClick = false;

    this.modalDialogTest = null;
    this.recordedAlerts = new Array();
    this.recordedConfirmations = new Array();
    this.recordedPrompts = new Array();
    this.openedWindows = {};
    this.nextConfirmResult = true;
    this.nextPromptResult = '';
    this.newPageLoaded = false;
    this.pageLoadError = null;

    this.shouldHighlightLocatedElement = false;

    this.uniqueId = "seleniumMarker" + new Date().getTime();
    this.pollingForLoad = new Object();
    this.permDeniedCount = new Object();
    this.windowPollers = new Array();
    // DGF for backwards compatibility
    this.browserbot = this;

    var self = this;

    objectExtend(this, PageBot.prototype);
    this._registerAllLocatorFunctions();

    this.recordPageLoad = function(elementOrWindow) {
        LOG.debug("Page load detected");
        try {
            if (elementOrWindow.location && elementOrWindow.location.href) {
                LOG.debug("Page load location=" + elementOrWindow.location.href);
            } else if (elementOrWindow.contentWindow && elementOrWindow.contentWindow.location && elementOrWindow.contentWindow.location.href) {
                LOG.debug("Page load location=" + elementOrWindow.contentWindow.location.href);
            } else {
                LOG.debug("Page load location unknown, current window location=" + this.getCurrentWindow(true).location);
            }
        } catch (e) {
            LOG.error("Caught an exception attempting to log location; this should get noticed soon!");
            LOG.exception(e);
            self.pageLoadError = e;
            return;
        }
        self.newPageLoaded = true;
    };

    this.isNewPageLoaded = function() {
        if (this.pageLoadError) {
            LOG.error("isNewPageLoaded found an old pageLoadError");
            var e = this.pageLoadError;
            this.pageLoadError = null;
            throw e;
        }
        return self.newPageLoaded;
    };

	this._browserName  = this.zGetBrowserName();//set browsername


	this.selNGResult = ""; //will  contain result if we get/exists/notexists etc *with* timeout-decoration

	this.totalDivs_CSV = "";
	this.totalDivsCount = 0;

	this.totalElements_CSV = "";
	this.verifyingObjIds_CSV = "";
	this.timetaken_CSV = "";

	this._scannedDivsCount = 0;
	this.scannedDivs_CSV = "";

	this.headers_CSV = "";
	this.headerName = "";

	this.weight_CSV = "";

};


BrowserBot.prototype.formalizeHTMLTag = function(tag) {
	//returns browser specific tag(either lowecase/uppercase)
	if(this._browserName.indexOf("IE") >= 0) //if Internet Explorer, make all the tags lowecase
		return tag.toUpperCase();
	else
		return tag.toLowerCase();
}

// DGF PageBot exists for backwards compatibility with old user-extensions
var PageBot = function(){};

BrowserBot.createForWindow = function(window, proxyInjectionMode) {
    var browserbot;
    LOG.debug('createForWindow');
    LOG.debug("browserName: " + browserVersion.name);
    LOG.debug("userAgent: " + navigator.userAgent);
    if (browserVersion.isIE) {
        browserbot = new IEBrowserBot(window);
    }
    else if (browserVersion.isKonqueror) {
        browserbot = new KonquerorBrowserBot(window);
    }
    else if (browserVersion.isOpera) {
        browserbot = new OperaBrowserBot(window);
    }
    else if (browserVersion.isSafari) {
        browserbot = new SafariBrowserBot(window);
    }
    else {
        // Use mozilla by default
        browserbot = new MozillaBrowserBot(window);
    }
    // getCurrentWindow has the side effect of modifying it to handle page loads etc
    browserbot.proxyInjectionMode = proxyInjectionMode;
    browserbot.getCurrentWindow();    // for modifyWindow side effect.  This is not a transparent style
    return browserbot;
};

// todo: rename?  This doesn't actually "do" anything.
BrowserBot.prototype.doModalDialogTest = function(test) {
    this.modalDialogTest = test;
};

BrowserBot.prototype.cancelNextConfirmation = function(result) {
    this.nextConfirmResult = result;
};

BrowserBot.prototype.setNextPromptResult = function(result) {
    this.nextPromptResult = result;
};

BrowserBot.prototype.hasAlerts = function() {
    return (this.recordedAlerts.length > 0);
};

BrowserBot.prototype.relayBotToRC = function(s) {
    // DGF need to do this funny trick to see if we're in PI mode, because
    // "this" might be the window, rather than the browserbot (e.g. during window.alert) 
    var piMode = this.proxyInjectionMode;
    if (!piMode) {
        if (typeof(selenium) != "undefined") {
            piMode = selenium.browserbot && selenium.browserbot.proxyInjectionMode;
        }
    }
    if (piMode) {
        this.relayToRC("selenium." + s);
    }
};

BrowserBot.prototype.relayToRC = function(name) {
        var object = eval(name);
        var s = 'state:' + serializeObject(name, object) + "\n";
        sendToRC(s,"state=true");
}

BrowserBot.prototype.resetPopups = function() {
    this.recordedAlerts = [];
    this.recordedConfirmations = [];
    this.recordedPrompts = [];
}

BrowserBot.prototype.getNextAlert = function() {
    var t = this.recordedAlerts.shift();
    if (t) { 
        t = t.replace(/\n/g, " ");  // because Selenese loses \n's when retrieving text from HTML table
    }
    this.relayBotToRC("browserbot.recordedAlerts");
    return t;
};

BrowserBot.prototype.hasConfirmations = function() {
    return (this.recordedConfirmations.length > 0);
};

BrowserBot.prototype.getNextConfirmation = function() {
    var t = this.recordedConfirmations.shift();
    this.relayBotToRC("browserbot.recordedConfirmations");
    return t;
};

BrowserBot.prototype.hasPrompts = function() {
    return (this.recordedPrompts.length > 0);
};

BrowserBot.prototype.getNextPrompt = function() {
    var t = this.recordedPrompts.shift();
    this.relayBotToRC("browserbot.recordedPrompts");
    return t;
};

/* Fire a mouse event in a browser-compatible manner */

BrowserBot.prototype.triggerMouseEvent = function(element, eventType, canBubble, clientX, clientY, button) {
	
    clientX = clientX ? clientX : 0;
    clientY = clientY ? clientY : 0;

    LOG.debug("triggerMouseEvent assumes setting screenX and screenY to 0 is ok");
    var screenX = 0;
    var screenY = 0;
	if(this.rightClick)
		button=2;
    canBubble = (typeof(canBubble) == undefined) ? true : canBubble;
    if (element.fireEvent && element.ownerDocument && element.ownerDocument.createEventObject) { //IE
        var evt = createEventObject(element, this.controlKeyDown, this.altKeyDown, this.shiftKeyDown, this.metaKeyDown);
        evt.detail = 0;
        evt.button = button ? button : 1; // default will be the left mouse click ( http://www.javascriptkit.com/jsref/event.shtml )
        evt.relatedTarget = null;
        if (!screenX && !screenY && !clientX && !clientY && !this.controlKeyDown && !this.altKeyDown && !this.shiftKeyDown && !this.metaKeyDown) {
            element.fireEvent('on' + eventType);
        }
        else {
            evt.screenX = screenX;
            evt.screenY = screenY;
            evt.clientX = clientX;
            evt.clientY = clientY;

            // when we go this route, window.event is never set to contain the event we have just created.
            // ideally we could just slide it in as follows in the try-block below, but this normally
            // doesn't work.  This is why I try to avoid this code path, which is only required if we need to
            // set attributes on the event (e.g., clientX).
            try {
                window.event = evt;
            }
            catch(e) {
                // getting an "Object does not support this action or property" error.  Save the event away
                // for future reference.
                // TODO: is there a way to update window.event?

                // work around for http://jira.openqa.org/browse/SEL-280 -- make the event available somewhere:
                selenium.browserbot.getCurrentWindow().selenium_event = evt;
            }
            element.fireEvent('on' + eventType, evt);
        }
    }
    else {
        var evt = document.createEvent('MouseEvents');
        if (evt.initMouseEvent)
        {
            // see http://developer.mozilla.org/en/docs/DOM:event.button and
            // http://developer.mozilla.org/en/docs/DOM:event.initMouseEvent for button ternary logic logic
            //Safari
            evt.initMouseEvent(eventType, canBubble, true, document.defaultView, 1, screenX, screenY, clientX, clientY,
                this.controlKeyDown, this.altKeyDown, this.shiftKeyDown, this.metaKeyDown, button ? button : 0, null);
        }
        else {
            LOG.warn("element doesn't have initMouseEvent; firing an event which should -- but doesn't -- have other mouse-event related attributes here, as well as controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown");
            evt.initEvent(eventType, canBubble, true);

            evt.shiftKey = this.shiftKeyDown;
            evt.metaKey = this.metaKeyDown;
            evt.altKey = this.altKeyDown;
            evt.ctrlKey = this.controlKeyDown;
            if(button)
            {
              evt.button = button;
            }
        }
        element.dispatchEvent(evt);
    }
}

BrowserBot.prototype._windowClosed = function(win) {
    var c = win.closed;
    if (c == null) return true;
    return c;
};

BrowserBot.prototype._modifyWindow = function(win) {
    // In proxyInjectionMode, have to suppress LOG calls in _modifyWindow to avoid an infinite loop
    if (this._windowClosed(win)) {
        if (!this.proxyInjectionMode) {
            LOG.error("modifyWindow: Window was closed!");
        }
        return null;
    }
    if (!this.proxyInjectionMode) {
        LOG.debug('modifyWindow ' + this.uniqueId + ":" + win[this.uniqueId]);
    }
    if (!win[this.uniqueId]) {
        win[this.uniqueId] = 1;
        this.modifyWindowToRecordPopUpDialogs(win, this);
    }
    // In proxyInjection mode, we have our own mechanism for detecting page loads
    if (!this.proxyInjectionMode) {
        this.modifySeparateTestWindowToDetectPageLoads(win);
    }
    if (win.frames && win.frames.length && win.frames.length > 0) {
        for (var i = 0; i < win.frames.length; i++) {
            try {
                this._modifyWindow(win.frames[i]);
            } catch (e) {} // we're just trying to be opportunistic; don't worry if this doesn't work out
        }
    }
    return win;
};

BrowserBot.prototype.selectWindow = function(target) {
    if (!target || target == "null") {
        this._selectTopWindow();
        return;
    }
    var result = target.match(/^([a-zA-Z]+)=(.*)/);
    if (!result) {
        try {
            this._selectWindowByName(target);
        }
        catch (e) {
            this._selectWindowByTitle(target);
        }
        return;
    }
    locatorType = result[1];
    locatorValue = result[2];
    if (locatorType == "title") {
        this._selectWindowByTitle(locatorValue);
    }
    // TODO separate name and var into separate functions
    else if (locatorType == "name") {
        this._selectWindowByName(locatorValue);
    } else if (locatorType == "var") {
        this._selectWindowByName(locatorValue);
    } else {
        throw new SeleniumError("Window locator not recognized: " + locatorType);
    }
};

BrowserBot.prototype._selectTopWindow = function() {
    this.currentWindowName = null;
    this.currentWindow = this.topWindow;
    this.topFrame = this.topWindow;
    this.isSubFrameSelected = false;
}

BrowserBot.prototype._selectWindowByName = function(target) {
    this.currentWindow = this.getWindowByName(target, false);
    this.topFrame = this.currentWindow;
    this.currentWindowName = target;
    this.isSubFrameSelected = false;
}

BrowserBot.prototype._selectWindowByTitle = function(target) {
    var windowName = this.getWindowNameByTitle(target);
    if (!windowName) {
        this._selectTopWindow();
    } else {
        this._selectWindowByName(windowName);
    }
}

BrowserBot.prototype.selectFrame = function(target) {
    if (target.indexOf("index=") == 0) {
        target = target.substr(6);
        var frame = this.getCurrentWindow().frames[target];
        if (frame == null) {
            throw new SeleniumError("Not found: frames["+index+"]");
        }
        if (!frame.document) {
            throw new SeleniumError("frames["+index+"] is not a frame");
        }
        this.currentWindow = frame;
        this.isSubFrameSelected = true;
    }
    else if (target == "relative=up" || target == "relative=parent") {
        this.currentWindow = this.getCurrentWindow().parent;
        this.isSubFrameSelected = (this._getFrameElement(this.currentWindow) != null);
    } else if (target == "relative=top") {
        this.currentWindow = this.topFrame;
        this.isSubFrameSelected = false;
    } else {
        var frame = this.findElement(target);
        if (frame == null) {
            throw new SeleniumError("Not found: " + target);
        }
        // now, did they give us a frame or a frame ELEMENT?
        var match = false;
        if (frame.contentWindow) {
            // this must be a frame element
            if (browserVersion.isHTA) {
                // stupid HTA bug; can't get in the front door
                target = frame.contentWindow.name;
            } else {
                this.currentWindow = frame.contentWindow;
                this.isSubFrameSelected = true;
                match = true;
            }
        } else if (frame.document && frame.location) {
            // must be an actual window frame
            this.currentWindow = frame;
            this.isSubFrameSelected = true;
            match = true;
        }

        if (!match) {
            // neither, let's loop through the frame names
            var win = this.getCurrentWindow();

            if (win && win.frames && win.frames.length) {
                for (var i = 0; i < win.frames.length; i++) {
                    if (win.frames[i].name == target) {
                        this.currentWindow = win.frames[i];
                        this.isSubFrameSelected = true;
                        match = true;
                        break;
                    }
                }
            }
            if (!match) {
                throw new SeleniumError("Not a frame: " + target);
            }
        }
    }
    // modifies the window
    this.getCurrentWindow();
};

BrowserBot.prototype.doesThisFrameMatchFrameExpression = function(currentFrameString, target) {
    var isDom = false;
    if (target.indexOf("dom=") == 0) {
        target = target.substr(4);
        isDom = true;
    } else if (target.indexOf("index=") == 0) {
        target = "frames[" + target.substr(6) + "]";
        isDom = true;
    }
    var t;
    try {
        eval("t=" + currentFrameString + "." + target);
    } catch (e) {
    }
    var autWindow = this.browserbot.getCurrentWindow();
    if (t != null) {
        try {
            if (t.window == autWindow) {
                return true;
            }
            if (t.window.uniqueId == autWindow.uniqueId) {
                return true;
               }
            return false;
        } catch (permDenied) {
            // DGF if the windows are incomparable, they're probably not the same...
        }
    }
    if (isDom) {
        return false;
    }
    var currentFrame;
    eval("currentFrame=" + currentFrameString);
    if (target == "relative=up") {
        if (currentFrame.window.parent == autWindow) {
            return true;
        }
        return false;
    }
    if (target == "relative=top") {
        if (currentFrame.window.top == autWindow) {
            return true;
        }
        return false;
    }
    if (currentFrame.window == autWindow.parent) {
        if (autWindow.name == target) {
            return true;
        }
        try {
            var element = this.findElement(target, currentFrame.window);
            if (element.contentWindow == autWindow) {
                return true;
            }
        } catch (e) {}
    }
    return false;
};

BrowserBot.prototype.openLocation = function(target) {
    // We're moving to a new page - clear the current one
    var win = this.getCurrentWindow();
    LOG.debug("openLocation newPageLoaded = false");
    this.newPageLoaded = false;

    this.setOpenLocation(win, target);
};

BrowserBot.prototype.openWindow = function(url, windowID) {
    if (url != "") {
        url = absolutify(url, this.baseUrl);
    }
    if (browserVersion.isHTA) {
        // in HTA mode, calling .open on the window interprets the url relative to that window
        // we need to absolute-ize the URL to make it consistent
        var child = this.getCurrentWindow().open(url, windowID);
        selenium.browserbot.openedWindows[windowID] = child;
    } else {
        this.getCurrentWindow().open(url, windowID);
    }
};

BrowserBot.prototype.setIFrameLocation = function(iframe, location) {
    iframe.src = location;
};

BrowserBot.prototype.setOpenLocation = function(win, loc) {
    loc = absolutify(loc, this.baseUrl);
    if (browserVersion.isHTA) {
        var oldHref = win.location.href;
        win.location.href = loc;
        var marker = null;
        try {
            marker = this.isPollingForLoad(win);
            if (marker && win.location[marker]) {
                win.location[marker] = false;
            }
        } catch (e) {} // DGF don't know why, but this often fails
    } else {
        win.location.href = loc;
    }
};

BrowserBot.prototype.getCurrentPage = function() {
    return this;
};

BrowserBot.prototype.modifyWindowToRecordPopUpDialogs = function(windowToModify, browserBot) {
    var self = this;

    windowToModify.seleniumAlert = windowToModify.alert;

    windowToModify.alert = function(alert) {
        browserBot.recordedAlerts.push(alert);
        self.relayBotToRC.call(self, "browserbot.recordedAlerts");
    };

    windowToModify.confirm = function(message) {
        browserBot.recordedConfirmations.push(message);
        var result = browserBot.nextConfirmResult;
        browserBot.nextConfirmResult = true;
        self.relayBotToRC.call(self, "browserbot.recordedConfirmations");
        return result;
    };

    windowToModify.prompt = function(message) {
        browserBot.recordedPrompts.push(message);
        var result = !browserBot.nextConfirmResult ? null : browserBot.nextPromptResult;
        browserBot.nextConfirmResult = true;
        browserBot.nextPromptResult = '';
        self.relayBotToRC.call(self, "browserbot.recordedPrompts");
        return result;
    };

    // Keep a reference to all popup windows by name
    // note that in IE the "windowName" argument must be a valid javascript identifier, it seems.
    var originalOpen = windowToModify.open;
    var originalOpenReference;
    if (browserVersion.isHTA) {
        originalOpenReference = 'selenium_originalOpen' + new Date().getTime();
        windowToModify[originalOpenReference] = windowToModify.open;
    }

    var isHTA = browserVersion.isHTA;

    var newOpen = function(url, windowName, windowFeatures, replaceFlag) {
        var myOriginalOpen = originalOpen;
        if (isHTA) {
            myOriginalOpen = this[originalOpenReference];
        }
        var openedWindow = myOriginalOpen(url, windowName, windowFeatures, replaceFlag);
        LOG.debug("window.open call intercepted; window ID (which you can use with selectWindow()) is \"" +  windowName + "\"");
        if (windowName!=null) {
            openedWindow["seleniumWindowName"] = windowName;
        }
        selenium.browserbot.openedWindows[windowName] = openedWindow;
        return openedWindow;
    };

    if (browserVersion.isHTA) {
        originalOpenReference = 'selenium_originalOpen' + new Date().getTime();
        newOpenReference = 'selenium_newOpen' + new Date().getTime();
        var setOriginalRef = "this['" + originalOpenReference + "'] = this.open;";

        if (windowToModify.eval) {
            windowToModify.eval(setOriginalRef);
            windowToModify.open = newOpen;
        } else {
            // DGF why can't I eval here?  Seems like I'm querying the window at a bad time, maybe?
            setOriginalRef += "this.open = this['" + newOpenReference + "'];";
            windowToModify[newOpenReference] = newOpen;
            windowToModify.setTimeout(setOriginalRef, 0);
        }
    } else {
        windowToModify.open = newOpen;
    }
};

/**
 * Call the supplied function when a the current page unloads and a new one loads.
 * This is done by polling continuously until the document changes and is fully loaded.
 */
BrowserBot.prototype.modifySeparateTestWindowToDetectPageLoads = function(windowObject) {
    // Since the unload event doesn't fire in Safari 1.3, we start polling immediately
    if (!windowObject) {
        LOG.warn("modifySeparateTestWindowToDetectPageLoads: no windowObject!");
        return;
    }
    if (this._windowClosed(windowObject)) {
        LOG.info("modifySeparateTestWindowToDetectPageLoads: windowObject was closed");
        return;
    }
    var oldMarker = this.isPollingForLoad(windowObject);
    if (oldMarker) {
        LOG.debug("modifySeparateTestWindowToDetectPageLoads: already polling this window: " + oldMarker);
        return;
    }

    var marker = 'selenium' + new Date().getTime();
    LOG.debug("Starting pollForLoad (" + marker + "): " + windowObject.location);
    this.pollingForLoad[marker] = true;
    // if this is a frame, add a load listener, otherwise, attach a poller
    var frameElement = this._getFrameElement(windowObject);
    // DGF HTA mode can't attach load listeners to subframes (yuk!)
    var htaSubFrame = this._isHTASubFrame(windowObject);
    if (frameElement && !htaSubFrame) {
        LOG.debug("modifySeparateTestWindowToDetectPageLoads: this window is a frame; attaching a load listener");
        addLoadListener(frameElement, this.recordPageLoad);
        frameElement[marker] = true;
        frameElement["frame"+this.uniqueId] = marker;
	LOG.debug("dgf this.uniqueId="+this.uniqueId);
	LOG.debug("dgf marker="+marker);
	LOG.debug("dgf frameElement['frame'+this.uniqueId]="+frameElement['frame'+this.uniqueId]);
frameElement[this.uniqueId] = marker;
LOG.debug("dgf frameElement[this.uniqueId]="+frameElement[this.uniqueId]);
    } else {
        windowObject.location[marker] = true;
        windowObject[this.uniqueId] = marker;
        this.pollForLoad(this.recordPageLoad, windowObject, windowObject.document, windowObject.location, windowObject.location.href, marker);
    }
};

BrowserBot.prototype._isHTASubFrame = function(win) {
    if (!browserVersion.isHTA) return false;
    // DGF this is wrong! what if "win" isn't the selected window?
    return this.isSubFrameSelected;
}

BrowserBot.prototype._getFrameElement = function(win) {
    var frameElement = null;
    var caught;
    try {
        frameElement = win.frameElement;
    } catch (e) {
        caught = true;
    }
    if (caught) {
        // on IE, checking frameElement in a pop-up results in a "No such interface supported" exception
        // but it might have a frame element anyway!
        var parentContainsIdenticallyNamedFrame = false;
        try {
            parentContainsIdenticallyNamedFrame = win.parent.frames[win.name];
        } catch (e) {} // this may fail if access is denied to the parent; in that case, assume it's not a pop-up

        if (parentContainsIdenticallyNamedFrame) {
            // it can't be a coincidence that the parent has a frame with the same name as myself!
            var result;
            try {
                result = parentContainsIdenticallyNamedFrame.frameElement;
                if (result) {
                    return result;
                }
            } catch (e) {} // it was worth a try! _getFrameElementsByName is often slow
            result = this._getFrameElementByName(win.name, win.parent.document, win);
            return result;
        }
    }
    LOG.debug("_getFrameElement: frameElement="+frameElement); 
    if (frameElement) {
        LOG.debug("frameElement.name="+frameElement.name);
    }
    return frameElement;
}

BrowserBot.prototype._getFrameElementByName = function(name, doc, win) {
    var frames;
    var frame;
    var i;
    frames = doc.getElementsByTagName("iframe");
    for (i = 0; i < frames.length; i++) {
        frame = frames[i];        
        if (frame.name === name) {
            return frame;
        }
    }
    frames = doc.getElementsByTagName("frame");
    for (i = 0; i < frames.length; i++) {
        frame = frames[i];        
        if (frame.name === name) {
            return frame;
        }
    }
    // DGF weird; we only call this function when we know the doc contains the frame
    LOG.warn("_getFrameElementByName couldn't find a frame or iframe; checking every element for the name " + name);
    return BrowserBot.prototype.locateElementByName(win.name, win.parent.document);
}
    

/**
 * Set up a polling timer that will keep checking the readyState of the document until it's complete.
 * Since we might call this before the original page is unloaded, we first check to see that the current location
 * or href is different from the original one.
 */
BrowserBot.prototype.pollForLoad = function(loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker) {
    LOG.debug("pollForLoad original (" + marker + "): " + originalHref);
    try {
        if (this._windowClosed(windowObject)) {
            LOG.debug("pollForLoad WINDOW CLOSED (" + marker + ")");
            delete this.pollingForLoad[marker];
            return;
        }

        var isSamePage = this._isSamePage(windowObject, originalDocument, originalLocation, originalHref, marker);
        var rs = this.getReadyState(windowObject, windowObject.document);

        if (!isSamePage && rs == 'complete') {
            var currentHref = windowObject.location.href;
            LOG.debug("pollForLoad FINISHED (" + marker + "): " + rs + " (" + currentHref + ")");
            delete this.pollingForLoad[marker];
            this._modifyWindow(windowObject);
            var newMarker = this.isPollingForLoad(windowObject);
            if (!newMarker) {
                LOG.debug("modifyWindow didn't start new poller: " + newMarker);
                this.modifySeparateTestWindowToDetectPageLoads(windowObject);
            }
            newMarker = this.isPollingForLoad(windowObject);
            var currentlySelectedWindow;
            var currentlySelectedWindowMarker;
            currentlySelectedWindow =this.getCurrentWindow(true);
            currentlySelectedWindowMarker = currentlySelectedWindow[this.uniqueId];

            LOG.debug("pollForLoad (" + marker + ") restarting " + newMarker);
            if (/(TestRunner-splash|Blank)\.html\?start=true$/.test(currentHref)) {
                LOG.debug("pollForLoad Oh, it's just the starting page.  Never mind!");
            } else if (currentlySelectedWindowMarker == newMarker) {
                loadFunction(currentlySelectedWindow);
            } else {
                LOG.debug("pollForLoad page load detected in non-current window; ignoring (currentlySelected="+currentlySelectedWindowMarker+", detection in "+newMarker+")");
            }
            return;
        }
        LOG.debug("pollForLoad continue (" + marker + "): " + currentHref);
        this.reschedulePoller(loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker);
    } catch (e) {
        LOG.debug("Exception during pollForLoad; this should get noticed soon (" + e.message + ")!");
        //DGF this is supposed to get logged later; log it at debug just in case
        //LOG.exception(e);
        this.pageLoadError = e;
    }
};

BrowserBot.prototype._isSamePage = function(windowObject, originalDocument, originalLocation, originalHref, marker) {
    var currentDocument = windowObject.document;
    var currentLocation = windowObject.location;
    var currentHref = currentLocation.href

    var sameDoc = this._isSameDocument(originalDocument, currentDocument);

    var sameLoc = (originalLocation === currentLocation);

    // hash marks don't meant the page has loaded, so we need to strip them off if they exist...
    var currentHash = currentHref.indexOf('#');
    if (currentHash > 0) {
        currentHref = currentHref.substring(0, currentHash);
    }
    var originalHash = originalHref.indexOf('#');
    if (originalHash > 0) {
        originalHref = originalHref.substring(0, originalHash);
    }
    LOG.debug("_isSamePage: currentHref: " + currentHref);
    LOG.debug("_isSamePage: originalHref: " + originalHref);

    var sameHref = (originalHref === currentHref);
    var markedLoc = currentLocation[marker];

    if (browserVersion.isKonqueror || browserVersion.isSafari) {
        // the mark disappears too early on these browsers
        markedLoc = true;
    }

    // since this is some _very_ important logic, especially for PI and multiWindow mode, we should log all these out
    LOG.debug("_isSamePage: sameDoc: " + sameDoc);
    LOG.debug("_isSamePage: sameLoc: " + sameLoc);
    LOG.debug("_isSamePage: sameHref: " + sameHref);
    LOG.debug("_isSamePage: markedLoc: " + markedLoc);

    return sameDoc && sameLoc && sameHref && markedLoc
};

BrowserBot.prototype._isSameDocument = function(originalDocument, currentDocument) {
    return originalDocument === currentDocument;
};


BrowserBot.prototype.getReadyState = function(windowObject, currentDocument) {
    var rs = currentDocument.readyState;
    if (rs == null) {
       if ((this.buttonWindow!=null && this.buttonWindow.document.readyState == null) // not proxy injection mode (and therefore buttonWindow isn't null)
       || (top.document.readyState == null)) {                                               // proxy injection mode (and therefore everything's in the top window, but buttonWindow doesn't exist)
            // uh oh!  we're probably on Firefox with no readyState extension installed!
            // We'll have to just take a guess as to when the document is loaded; this guess
            // will never be perfect. :-(
            if (typeof currentDocument.getElementsByTagName != 'undefined'
                    && typeof currentDocument.getElementById != 'undefined'
                    && ( currentDocument.getElementsByTagName('body')[0] != null
                    || currentDocument.body != null )) {
                if (windowObject.frameElement && windowObject.location.href == "about:blank" && windowObject.frameElement.src != "about:blank") {
                    LOG.info("getReadyState not loaded, frame location was about:blank, but frame src = " + windowObject.frameElement.src);
                    return null;
                }
                LOG.debug("getReadyState = windowObject.frames.length = " + windowObject.frames.length);
                for (var i = 0; i < windowObject.frames.length; i++) {
                    LOG.debug("i = " + i);
                    if (this.getReadyState(windowObject.frames[i], windowObject.frames[i].document) != 'complete') {
                        LOG.debug("getReadyState aha! the nested frame " + windowObject.frames[i].name + " wasn't ready!");
                        return null;
                    }
                }

                rs = 'complete';
            } else {
                LOG.debug("pollForLoad readyState was null and DOM appeared to not be ready yet");
            }
        }
    }
    else if (rs == "loading" && browserVersion.isIE) {
        LOG.debug("pageUnloading = true!!!!");
        this.pageUnloading = true;
    }
    LOG.debug("getReadyState returning " + rs);
    return rs;
};

/** This function isn't used normally, but was the way we used to schedule pollers:
 asynchronously executed autonomous units.  This is deprecated, but remains here
 for future reference.
 */
BrowserBot.prototype.XXXreschedulePoller = function(loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker) {
    var self = this;
    window.setTimeout(function() {
        self.pollForLoad(loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker);
    }, 500);
};

/** This function isn't used normally, but is useful for debugging asynchronous pollers
 * To enable it, rename it to "reschedulePoller", so it will override the
 * existing reschedulePoller function
 */
BrowserBot.prototype.XXXreschedulePoller = function(loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker) {
    var doc = this.buttonWindow.document;
    var button = doc.createElement("button");
    var buttonName = doc.createTextNode(marker + " - " + windowObject.name);
    button.appendChild(buttonName);
    var tools = doc.getElementById("tools");
    var self = this;
    button.onclick = function() {
        tools.removeChild(button);
        self.pollForLoad(loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker);
    };
    tools.appendChild(button);
    window.setTimeout(button.onclick, 500);
};

BrowserBot.prototype.reschedulePoller = function(loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker) {
    var self = this;
    var pollerFunction = function() {
        self.pollForLoad(loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker);
    };
    this.windowPollers.push(pollerFunction);
};

BrowserBot.prototype.runScheduledPollers = function() {
    LOG.debug("runScheduledPollers");
    var oldPollers = this.windowPollers;
    this.windowPollers = new Array();
    for (var i = 0; i < oldPollers.length; i++) {
        oldPollers[i].call();
    }
    LOG.debug("runScheduledPollers DONE");
};

BrowserBot.prototype.isPollingForLoad = function(win) {
    var marker;
    var frameElement = this._getFrameElement(win);
    var htaSubFrame = this._isHTASubFrame(win);
    if (frameElement && !htaSubFrame) {
	marker = frameElement["frame"+this.uniqueId];
    } else {
        marker = win[this.uniqueId];
    }
    if (!marker) {
        LOG.debug("isPollingForLoad false, missing uniqueId " + this.uniqueId + ": " + marker);
        return false;
    }
    if (!this.pollingForLoad[marker]) {
        LOG.debug("isPollingForLoad false, this.pollingForLoad[" + marker + "]: " + this.pollingForLoad[marker]);
        return false;
    }
    return marker;
};

BrowserBot.prototype.getWindowByName = function(windowName, doNotModify) {
    LOG.debug("getWindowByName(" + windowName + ")");
    // First look in the map of opened windows
    var targetWindow = this.openedWindows[windowName];
    if (!targetWindow) {
        targetWindow = this.topWindow[windowName];
    }
    if (!targetWindow && windowName == "_blank") {
        for (var winName in this.openedWindows) {
            // _blank can match selenium_blank*, if it looks like it's OK (valid href, not closed)
            if (/^selenium_blank/.test(winName)) {
                targetWindow = this.openedWindows[winName];
                var ok;
                try {
                    if (!this._windowClosed(targetWindow)) {
                        ok = targetWindow.location.href;
                    }
                } catch (e) {}
                if (ok) break;
            }
        }
    }
    if (!targetWindow) {
        throw new SeleniumError("Window does not exist. If this looks like a Selenium bug, make sure to read http://selenium-core.openqa.org/reference.html#openWindow for potential workarounds.");
    }
    if (browserVersion.isHTA) {
        try {
            targetWindow.location.href;
        } catch (e) {
            targetWindow = window.open("", targetWindow.name);
            this.openedWindows[targetWindow.name] = targetWindow;
        }
    }
    if (!doNotModify) {
        this._modifyWindow(targetWindow);
    }
    return targetWindow;
};

/**
 * Find a window name from the window title.
 */
BrowserBot.prototype.getWindowNameByTitle = function(windowTitle) {
    LOG.debug("getWindowNameByTitle(" + windowTitle + ")");

    // First look in the map of opened windows and iterate them
    for (var windowName in this.openedWindows) {
        var targetWindow = this.openedWindows[windowName];

        // If the target window's title is our title
        try {
            // TODO implement Pattern Matching here
            if (!this._windowClosed(targetWindow) &&
                targetWindow.document.title == windowTitle) {
                return windowName;
            }
        } catch (e) {
            // You'll often get Permission Denied errors here in IE
            // eh, if we can't read this window's title,
            // it's probably not available to us right now anyway
        }
    }
    
    try {
        if (this.topWindow.document.title == windowTitle) {
            return "";
        }
    } catch (e) {} // IE Perm denied

    throw new SeleniumError("Could not find window with title " + windowTitle);
};

BrowserBot.prototype.getCurrentWindow = function(doNotModify) {
    if (this.proxyInjectionMode) {
        return window;
    }
    var testWindow = this.currentWindow;
    if (!doNotModify) {
        this._modifyWindow(testWindow);
        LOG.debug("getCurrentWindow newPageLoaded = false");
        this.newPageLoaded = false;
    }
    testWindow = this._handleClosedSubFrame(testWindow, doNotModify);
    return testWindow;
};

BrowserBot.prototype._handleClosedSubFrame = function(testWindow, doNotModify) {
    if (this.proxyInjectionMode) {
        return testWindow;
    }

    if (this.isSubFrameSelected) {
        var missing = true;
        if (testWindow.parent && testWindow.parent.frames && testWindow.parent.frames.length) {
            for (var i = 0; i < testWindow.parent.frames.length; i++) {
                if (testWindow.parent.frames[i] == testWindow) {
                    missing = false;
                    break;
                }
            }
        }
        if (missing) {
            LOG.warn("Current subframe appears to have closed; selecting top frame");
            this.selectFrame("relative=top");
            return this.getCurrentWindow(doNotModify);
        }
    } else if (this._windowClosed(testWindow)) {
        var closedError = new SeleniumError("Current window or frame is closed!");
        closedError.windowClosed = true;
        throw closedError;
    }
    return testWindow;
};

BrowserBot.prototype.highlight = function (element, force) {
    if (force || this.shouldHighlightLocatedElement) {
        try {
            highlight(element);
        } catch (e) {} // DGF element highlighting is low-priority and possibly dangerous
    }
    return element;
}

BrowserBot.prototype.setShouldHighlightElement = function (shouldHighlight) {
    this.shouldHighlightLocatedElement = shouldHighlight;
}

/*****************************************************************/
/* BROWSER-SPECIFIC FUNCTIONS ONLY AFTER THIS LINE */


BrowserBot.prototype._registerAllLocatorFunctions = function() {
    // TODO - don't do this in the constructor - only needed once ever
    this.locationStrategies = {};
    for (var functionName in this) {
        var result = /^locateElementBy([A-Z].+)$/.exec(functionName);
        if (result != null) {
            var locatorFunction = this[functionName];
            if (typeof(locatorFunction) != 'function') {
                continue;
            }
            // Use a specified prefix in preference to one generated from
            // the function name
            var locatorPrefix = locatorFunction.prefix || result[1].toLowerCase();
            this.locationStrategies[locatorPrefix] = locatorFunction;
        }
    }

    /**
     * Find a locator based on a prefix.
     */
    this.findElementBy = function(locatorType, locator, inDocument, inWindow) {
        var locatorFunction = this.locationStrategies[locatorType];
        if (! locatorFunction) {
            throw new SeleniumError("Unrecognised locator type: '" + locatorType + "'");
        }
        return locatorFunction.call(this, locator, inDocument, inWindow);
    };

    /**
     * The implicit locator, that is used when no prefix is supplied.
     */
    this.locationStrategies['implicit'] = function(locator, inDocument, inWindow) {
        if (locator.startsWith('//')) {
            return this.locateElementByXPath(locator, inDocument, inWindow);
        }
        if (locator.startsWith('document.')) {
            return this.locateElementByDomTraversal(locator, inDocument, inWindow);
        }
        return this.locateElementByIdentifier(locator, inDocument, inWindow);
    };
}

BrowserBot.prototype.getDocument = function() {
    return this.getCurrentWindow().document;
}

BrowserBot.prototype.getTitle = function() {
    var t = this.getDocument().title;
    if (typeof(t) == "string") {
        t = t.trim();
    }
    return t;
}

BrowserBot.prototype.getCookieByName = function(cookieName, doc) {
    if (!doc) doc = this.getDocument();
    var ck = doc.cookie;
    if (!ck) return null;
    var ckPairs = ck.split(/;/);
    for (var i = 0; i < ckPairs.length; i++) {
        var ckPair = ckPairs[i].trim();
        var ckNameValue = ckPair.split(/=/);
        var ckName = decodeURIComponent(ckNameValue[0]);
        if (ckName === cookieName) {
            return decodeURIComponent(ckNameValue[1]);
        }
    }
    return null;
}

BrowserBot.prototype.getAllCookieNames = function(doc) {
    if (!doc) doc = this.getDocument();
    var ck = doc.cookie;
    if (!ck) return [];
    var cookieNames = [];
    var ckPairs = ck.split(/;/);
    for (var i = 0; i < ckPairs.length; i++) {
        var ckPair = ckPairs[i].trim();
        var ckNameValue = ckPair.split(/=/);
        var ckName = decodeURIComponent(ckNameValue[0]);
        cookieNames.push(ckName);
    }
    return cookieNames;
}

BrowserBot.prototype.deleteCookie = function(cookieName, domain, path, doc) {
    if (!doc) doc = this.getDocument();
    var expireDateInMilliseconds = (new Date()).getTime() + (-1 * 1000);
    var cookie = cookieName + "=deleted; ";
    if (path) {
        cookie += "path=" + path + "; ";
    }
    if (domain) {
        cookie += "domain=" + domain + "; ";
    }
    cookie += "expires=" + new Date(expireDateInMilliseconds).toGMTString();
    LOG.debug("Setting cookie to: " + cookie);
    doc.cookie = cookie;
}

/** Try to delete cookie, return false if it didn't work */
BrowserBot.prototype._maybeDeleteCookie = function(cookieName, domain, path, doc) {
    this.deleteCookie(cookieName, domain, path, doc);
    return (!this.getCookieByName(cookieName, doc));
}
    

BrowserBot.prototype._recursivelyDeleteCookieDomains = function(cookieName, domain, path, doc) {
    var deleted = this._maybeDeleteCookie(cookieName, domain, path, doc);
    if (deleted) return true;
    var dotIndex = domain.indexOf(".");
    if (dotIndex == 0) {
        return this._recursivelyDeleteCookieDomains(cookieName, domain.substring(1), path, doc);
    } else if (dotIndex != -1) {
        return this._recursivelyDeleteCookieDomains(cookieName, domain.substring(dotIndex), path, doc);
    } else {
        // No more dots; try just not passing in a domain at all
        return this._maybeDeleteCookie(cookieName, null, path, doc);
    }
}

BrowserBot.prototype._recursivelyDeleteCookie = function(cookieName, domain, path, doc) {
    var slashIndex = path.lastIndexOf("/");
    var finalIndex = path.length-1;
    if (slashIndex == finalIndex) {
        slashIndex--;
    }
    if (slashIndex != -1) {
        deleted = this._recursivelyDeleteCookie(cookieName, domain, path.substring(0, slashIndex+1), doc);
        if (deleted) return true;
    }
    return this._recursivelyDeleteCookieDomains(cookieName, domain, path, doc);
}

BrowserBot.prototype.recursivelyDeleteCookie = function(cookieName, domain, path, win) {
    if (!win) win = this.getCurrentWindow();
    var doc = win.document;
    if (!domain) {
        domain = doc.domain;
    }
    if (!path) {
        path = win.location.pathname;
    }
    var deleted = this._recursivelyDeleteCookie(cookieName, "." + domain, path, doc);
    if (deleted) return;
    // Finally try a null path (Try it last because it's uncommon)
    deleted = this._recursivelyDeleteCookieDomains(cookieName, "." + domain, null, doc);
    if (deleted) return;
    throw new SeleniumError("Couldn't delete cookie " + cookieName);
}

/*
 * Finds an element recursively in frames and nested frames
 * in the specified document, using various lookup protocols
 */
BrowserBot.prototype.findElementRecursive = function(locatorType, locatorString, inDocument, inWindow) {

    var element = this.findElementBy(locatorType, locatorString, inDocument, inWindow);
    if (element != null) {
        return element;
    }

    for (var i = 0; i < inWindow.frames.length; i++) {
        element = this.findElementRecursive(locatorType, locatorString, inWindow.frames[i].document, inWindow.frames[i]);

        if (element != null) {
            return element;
        }
    }
};

/*
* Finds an element on the current page, using various lookup protocols
*/
BrowserBot.prototype.findElementOrNull = function(locator, win) {

	//Zimbra hack to support multiple cached compose view
	if(/z[bv]__COMPOSE/.test(locator)) {
		locator = this.getZDisPlayedObjectIdOrNull(locator);
		if(locator == null)
			return null;
	}

    var locatorType = 'implicit';
    var locatorString = locator;

    // If there is a locator prefix, use the specified strategy
    var result = locator.match(/^([A-Za-z]+)=(.+)/);
    if (result) {
        locatorType = result[1].toLowerCase();
        locatorString = result[2];
    }

    if (win == null) {
        win = this.getCurrentWindow();
    }
    var element = this.findElementRecursive(locatorType, locatorString, win.document, win);

    if (element != null) {
        return this.browserbot.highlight(element);
    }

    // Element was not found by any locator function.
    return null;
};

//hack checks zm_compose1 .. zv_compose2.. zv_compose10 and reconstructs proper id for the button, editfield
BrowserBot.prototype.getZDisPlayedObjectIdOrNull = function(origLocator)  {
	var part1 = origLocator.match(/z[bv]__COMPOSE/)[0];
	var part0 = origLocator.split(part1)[0];
	var indxAndpart2 = origLocator.split(part1)[1];
	var arry = indxAndpart2.match(/[0-9]+/);
	var indx =0;
	if(arry != null)
		indx = arry[0];

	var part2 =  indxAndpart2.substring(indx.length, indxAndpart2.length);
	var win = this.getCurrentWindow();
	var inDocument = win.document;
	for(var i=1; i< 10; i++) {
		var el = inDocument.getElementById("zv__COMPOSE"+i);
		if( el != null) {
			if(parseInt(el.style.top.replace("px")) > 1)
				return part0 + part1+ i + part2;
		}
	}

	return null;
};


BrowserBot.prototype.findElement = function(locator, win) {
    var element = this.findElementOrNull(locator, win);
    if (element == null) throw new SeleniumError("Element " + locator + " not found");
    return element;
}
BrowserBot.prototype.verifyZButton = function(locator) {
	if (this.findZButton(locator) == null)
		return false;
	else
		return true;
}



BrowserBot.prototype.zGetBrowserName = function()  {
	try{
		var win = this.browserbot.getCurrentWindow();
	} catch(e) {
		return "";
	}
	var agent = navigator.userAgent;
	var browserName = "";
	if (agent.indexOf("Firefox/") >= 0){
		browserName = "FF " + agent.split("Firefox/")[1];
		var tmp = browserName.split(" ");
		browserName = tmp[0]+ " "+ tmp[1];
		
	} else if (agent.indexOf("MSIE") >= 0) {
		var arry = agent.split(";");
		for (var t = 0; t < arry.length; t++) {
			if (arry[t].indexOf("MSIE") >= 0) {
				browserName = arry[t];
				break;
			}
		}
	} else if (agent.indexOf("Safari") >= 0) {
		var arry = agent.split("/");
		for (var t = 0; t < arry.length; t++) {
			if (arry[t].indexOf("Safari") >= 0) {
				browserName = arry[t];
				break;
			}
		}
	}
	if(agent.indexOf("Safari")>=0 && agent.indexOf("Chrome") >=0)
		browserName = "Chrome";
	

	return browserName;
}

BrowserBot.prototype.getShellChildNodes = function() {
		var win = this.getCurrentWindow();
	var inDocument = win.document;
	if(inDocument.getElementById("z_shell") != undefined) //zimbraajax
		return inDocument.getElementById("z_shell").childNodes;
	else if(inDocument.getElementById("DWT1") != undefined){//compose new window
		var newwindowelement = inDocument.getElementById("DWT1");
		if(newwindowelement.className == "MainShell")
			return newwindowelement.childNodes;
		else
			return null;
	} else 
		return null;

}

BrowserBot.prototype.getFormAnchors_html = function() {
	var form = this.getForm_html();
	if(form == null)
		return null;

	return form.getElementsByTagName("a");


}

BrowserBot.prototype.findZTabs_html = function(locator, panel, objNumber) {
	var _counter = 0;
	var reqNumber = 1;
	var tab = null;
	if(objNumber != undefined && objNumber != "")
		 reqNumber = parseInt(objNumber);

	var win = this.getCurrentWindow();
	var inDocument = win.document;
	var tds =  inDocument.getElementsByTagName("td");	
	for(var i=0; i< tds.length; i++) {
		var td =  tds[i];
		if((td.className == "Tab TabNormal" || td.className == "Tab TabSelected") && td.innerHTML.indexOf(locator)>=0){
				_counter++;
				if(_counter == reqNumber) {
					tab = td;
					break;
				}
					
		}
	}
	
	if(tab == null)
		return null;

	var anch = tab.getElementsByTagName("a");
	if (anch.length == 0)
		return null;

	return anch[0];


}

BrowserBot.prototype.getButtons_html = function() {
	var form = this.getForm_html();
	var arry = new Array();
	if(form == null)
		return null;

	var tables =  form.getElementsByTagName("table");
	for(var i=0; i< tables.length; i++) {
		var tbl =  tables[i];
		if(tbl.parentNode.className == "TbTop" ||tbl.parentNode.className == "TbBottom" || tbl.className == "ZOptionsSectionMain"){
				arry.push(tbl);
		}
	}
	
	var buttons = new Array();
	var inputBtns = new Array();
	for(var j=0;j < arry.length; j++) {
		var toolbar = arry[j];

		var objs = toolbar.getElementsByTagName('a');
		for(var k=0; k< objs.length; k++) {
				buttons.push(objs[k]);
		}
		var inputs =  toolbar.getElementsByTagName('input');
		for(var k=0; k< inputs.length; k++) {
			if(inputs[k].type== "submit")
				buttons.push(inputs[k]);
		}

	}
	return buttons;


}
BrowserBot.prototype.getForm_html = function() {
	var win = this.getCurrentWindow();
	var inDocument = win.document;
	var frms =  inDocument.getElementsByTagName("form");
	for(var i=0; i< frms.length; i++) {
		var htm = frms[i].innerHTML;
		if(htm.indexOf("class=\"TbTop\"") > 0  || htm.indexOf("name=\"zform\"") > 0 || htm.indexOf("class=TbTop") > 0 || htm.indexOf("name=zform") > 0){
			return frms[i];
		}
	}
	return null;

}

BrowserBot.prototype.getFormClass_html = function(reqClass, objNumber) {
	var form = this.getForm_html();
	if(form == null)
		return null;

	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);

	var itms = form.getElementsByTagName("*");
	for(var i=0; i< itms.length; i++) {
		if(itms[i].className.indexOf(reqClass) >=0){
			_counter++;
			if(_counter == reqNumber)
				return itms[i];
		}
	}
	return null;

}


BrowserBot.prototype.findZCalView = function() {

	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;

	var check1 = "((parseInt(testElement.style.zIndex) == 300" + 
			"|| (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == \"block\"))"+		
			"&& (testElement.className.indexOf(\"ZmCalViewMgr\") >= 0))";

	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		var testElementHtml = testElement.innerHTML;
		if (eval("(" + check1 + ")")) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
			
				if ((testElement.className.indexOf("calendar_view") >= 0) && (testElement.style.left == ("0px")) 
						&&(testElement.style.top == ("0px"))) {
							return testElement;
				} 
			}
		}
	}
	return null;
}



BrowserBot.prototype.findZAppt_html = function(locator, panel, objNumber) {
		if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);

	var win = this.getCurrentWindow();
	var inDocument = win.document;
	var tbls =  inDocument.getElementsByTagName("table");
	for(var i=0; i< tbls.length; i++) {
		var tbl = tbls[i];
		if((tbl.className == "ZhCalDayAppt" ||tbl.className.indexOf("ZhCalDayAllDayAppt") >=0
			|| tbl.className.indexOf("ZhCalMonthAllDayAppt") >=0 || tbl.className == "ZhCalDayApptNew") && tbl.innerHTML.indexOf(locator) >=0){
			_counter++;
			if(reqNumber == _counter)
				return tbl;
		}
	}
	_counter = 0;
	var divs =  inDocument.getElementsByTagName("div");
	for(var i=0; i< divs.length; i++) {
		var div = divs[i];
		if(div.className.indexOf("ZhCalDayAppt") >=0 && div.innerHTML.indexOf(locator) >=0){
			_counter++;
			if(reqNumber == _counter)
				return div;
		}
	}
	
	return null;
}

BrowserBot.prototype.findZcalGrid_html = function(locator, action, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);


	var win = this.getCurrentWindow();
	var inDocument = win.document;
	var grid = null;
	var tbls =  inDocument.getElementsByTagName("table");
	for(var i=0; i< tbls.length; i++) {
		var tbl = tbls[i];
		if(tbl.className == "ZhCalDayGrid" || tbl.className == "ZhCalMonthTable"){
			grid = tbl;
			break;
		}
	}
	if(grid == null)
			return null;

	//get allRows and HdrCells
	this._getAllRowsAndHdrCells(grid);
	if(this._hdrRowCells == null || this._allRows == null)
		return null;
	
	var isMonthView = false;
	if(grid.className == "ZhCalMonthTable")
		isMonthView = true;
		
	var apptRows = new Array();
	for(var i=0; i<  this._allRows.length; i++) {
		var tr =  this._allRows[i];
		if(tr.nodeName == "#text")
			continue;
		try{
			var ht = tr.innerHTML;
			if((ht.indexOf("ZhCalDayHour")>=0 || ht.indexOf("ZhCalAllDayDS")>=0 || ht.indexOf("ZhCalMonthAppt")>=0 ||  ht.indexOf("ZhCalMonthAllDayAppt")>=0 )  && ht.indexOf(locator)>=0){
				apptRows.push(tr);				
			}
		}catch(e) {}
	}

	if(apptRows.length ==0)
		return null;
	var result = "";
	var cellNum = 0;
	for(var i=0; i< apptRows.length; i++) {//go through all rows with locator
		var tr = apptRows[i];
		var tds = tr.childNodes;
		var tme = "N/A";
		for(var j=0; j< tds.length; j++) {//go through all cells
			var cell =  tds[j];
			if(cell.nodeName == "#text"){
				continue;
			}

			if(cell.className.indexOf("ZhCalDayHour")>=0){//get time
				if (cell.textContent){
					tme = cell.textContent;
				}else if(cell.innerText){
					tme = cell.innerText;
				}
			}

			var ht = cell.innerHTML;
			if(ht == undefined){
				continue;
			}
			var dateInMonthCell = "";
			if(ht.indexOf(locator) >=0) {
				
				if(isMonthView){//get the date
					var tdMs = cell.getElementsByTagName("td");
					for(var n=0;n<tdMs.length;n++){
						if(tdMs[n].className.indexOf("ZhCalDOM") >=0){
							if (tdMs[n].textContent){
								dateInMonthCell = tdMs[n].textContent;
							}else if(tdMs[n].innerText){
								dateInMonthCell = tdMs[n].innerText;
							}
						}
					}
				}
				
				var hour = cell.rowSpan/4;//get the hour
				if(hour <0.5)//ignore all those < half an hour.
					hour = "N/A";

				var cNum  = 0;
				if(isMonthView){
					cNum = (j-1)/2;
				} else{
					cNum = j;
				}
				var hdr = this._hdrRowCells[cNum];
				if (hdr.textContent){
					result = result+ hour+ "_"+ tme + "_"+ hdr.textContent+" "+dateInMonthCell+";";
				}	else if (hdr.innerText){
					result = result +  hour+ "_"+ tme + "_"+ hdr.innerText+" "+dateInMonthCell+";";
				}
			}
		}

	}

	this._hdrRowCells  = null;
	this._allRows = null;
	if(result == "")
		return null;

	result =  result.replace(/\r|\n|\r\n/g, "");	
	if(action =="getCount")
		return result.split(";").length-1;

	if(action == "getDT")
		return result;
}

BrowserBot.prototype._getAllRowsAndHdrCells = function(grid) {
	this._hdrRowCells  = null;
	this._allRows = null;
	var tempArry = new Array();
	if(grid.className == "ZhCalDayGrid"){
		var trs =  grid.getElementsByTagName("tr");
		for(var i=0; i< trs.length; i++) {
			var tr = trs[i];
			if(tr.className == "ZhCalMonthHeaderRow"){
				this._allRows = tr.parentNode.childNodes;
				this._hdrRowCells = tr.childNodes;
				break;
			}
		}
	} else if(grid.className == "ZhCalMonthTable") {
		var parent = grid.parentNode.childNodes;
		var hdr = null;
		for(var i=0;i<parent.length;i++){
			if(parent[i].className == "ZhCalMonthHeaderTable"){
					hdr = parent[i];
					break;
			}
		}
		var tds =hdr.getElementsByTagName("td");
		var cnt=0;
		for(var i=0; i< tds.length; i++) {
			var td = tds[i];
			if(td.className == "ZhCalMonthHeaderCellsText"){
				tempArry[cnt] = td;
				cnt++;
			}
		}
		this._hdrRowCells  = tempArry;
		this._allRows= grid.getElementsByTagName("tbody")[0].childNodes;
	}

}

BrowserBot.prototype.findZAppt = function(locator, panel, objNumber) {
		if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);

	var calView = this.findZCalView();
	if(calView == null)
		return null;

	var div1 = calView.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
			var testElement = div1[j];
			if (((testElement.className.indexOf("appt-selected") >= 0)||	(testElement.className == ("appt"))) 
					&& (testElement.innerHTML.indexOf(locator) >= 0)) {
					_counter++;
					if(reqNumber == _counter)
						return testElement;
			} 
	}
}

BrowserBot.prototype.findZCalGrid = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	this._calHeader = new Array();
	this._calRows = new Array();
	var calView = this.findZCalView();
	if(calView == null)
		return null;

	var div1 = calView.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
		var testElement = div1[j];
		if (testElement.className.indexOf("calendar_heading_day") >= 0) {
				if (testElement.textContent)
					var actTxt = testElement.textContent;
				else if (testElement.innerText)
					var actTxt = testElement.innerText;

				this._calHeader[j] = actTxt;
				this._calHeaderWidth = testElement.style.width;

		}
	}
	var td = calView.getElementsByTagName("TD");
	for (var j = 0; j < td.length; j++) {
		var testElement = td[j];
		if (testElement.className.indexOf("calendar_grid_body_time_td") >= 0) {
				if (testElement.textContent)
					var actTxt = testElement.textContent;
				else if (testElement.innerText)
					var actTxt = testElement.innerText;

				this._calRows[j] = actTxt;
				this._calRowHeight = testElement.style.height;
		}
	}

	
}

BrowserBot.prototype.findZFeatureMenu = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);

	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	var innerTxt = "";
	if(browserVersion.isIE)
		innerTxt = "innerText";
	else
		innerTxt = "textContent";
	if(panel == undefined || panel == "") {
		var check1 = "((parseInt(testElement.style.zIndex) == 300" + 
				 "|| (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == \"block\"))"+		
				"&& (testElement."+innerTxt+".indexOf(locator) >= 0))";
	} else if(panel == "dialog"){
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700 " +
			" && (testElement.className.indexOf(\"Dialog\") >= 0) "+
			" && (testElement."+innerTxt+".indexOf(locator) >= 0))";
	} else if(panel.indexOf("__dialogByName__")>=0){
				var dlgName = panel.replace("__dialogByName__", ""); 
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700" + 
		" && (testElement.className.indexOf(\"Dialog\") >= 0)" + 
		" && (testElement."+innerTxt+".indexOf(\""+dlgName+"\") >= 0)" +
		" && (testElement."+innerTxt+".indexOf(locator) >= 0))";
	} else {
		return null;
	}
	var rowFound = false;
	var potentialRowArray = new Array();
	var rowCnt = 0;
	var row = "";
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		var testElementHtml = testElement.innerHTML;
	
		if (eval("(" + check1 + ")")) {	
			var tr1 = testElement.getElementsByTagName("TR");
			for (var j = 0; j < tr1.length; j++) {
				var testElement = tr1[j];
				var testElementHtml = testElement.innerHTML;
				var selectIndx = testElementHtml.indexOf("ZSelectAutoSizingContainer") ;
				var trIndx =  testElementHtml.toLowerCase().indexOf("<tr");
				if (testElement.textContent)
					var inTxt = testElement.textContent;
				else if (testElement.innerText)
						var inTxt = testElement.innerText;
				var txtIndx =  inTxt.indexOf(locator);

				if (selectIndx >= 0 && (trIndx == -1 || trIndx > selectIndx)  && txtIndx == 0){
					row = testElement;
					rowFound = true;
					break;
				} else if((selectIndx >= 0) && (selectIndx > trIndx)  && txtIndx >= 0) {
					potentialRowArray[rowCnt] = testElement;
					rowCnt++;
				}	

			}

		}
		if(rowFound)
			break;
	}

	if(rowFound)
		return this._getFeatureMenuFromRow(locator, row, reqNumber);
	else if(potentialRowArray.length >0 )
			return this._getFeatureMenuFromRow(locator, potentialRowArray[potentialRowArray.length-1], reqNumber);
	else
		return null;
};
//private
BrowserBot.prototype._getFeatureMenuFromRow = function(locator, row, objNumber) {
	var _counter = 1;
			//simple hack to skip one menu if the menu's name is middle of the row
			var rowHtml = row.innerHTML;
			var locatorIndx = rowHtml.indexOf(locator);
			var menuIndx = rowHtml.indexOf("ZSelectAutoSizingContainer");
			if(locatorIndx > menuIndx)
				objNumber++;
		var div1 = row.getElementsByTagName("div");
		for (var k = 0; k < div1.length; k++) {
				var testElement = div1[k];
					if (testElement.className.indexOf("ZSelectAutoSizingContainer") >= 0){
						if(objNumber == _counter)
							return testElement;
						else
							_counter++;
					}
		}
		return null;
}


BrowserBot.prototype.findZFolder_html = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	var ignoreFolderHdr = false;
	if(locator.indexOf("::ignoreFolderHdr") > 0) {
		locator = locator.replace("::ignoreFolderHdr","");
		ignoreFolderHdr	= true;
	}

	var _edit = false;
	var _expand = false;
	var _collapse = false;
	var _check = false;
	var _uncheck = false;
	if(locator.indexOf("_edit") >=0){
		locator = locator.replace("_edit","");
		_edit = true;
	} else if(locator.indexOf("_expand") >=0){
		locator = locator.replace("_expand","");
		_expand = true;
	} else if(locator.indexOf("_collapse") >=0){
		locator = locator.replace("_collapse","");
		_collapse = true;
	} else if(locator.indexOf("_check") >=0){
		locator = locator.replace("_check","");
		_check = true;
	} else if(locator.indexOf("_uncheck") >=0){
		locator = locator.replace("_uncheck","");
		_uncheck = true;
	}

	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != ""){
		var reqNumber = parseInt(objNumber);
	}

	var rows = null;
	var win = this.getCurrentWindow();
	var inDocument = win.document;
	var tds =  inDocument.getElementsByTagName("td");
	for(var i=0; i< tds.length; i++) {
		var td = tds[i];
		if(td.className == "Overview" || td.className == "List"){
			var htm = td.innerHTML;
			if((htm.indexOf("TreeHeaderRow") >=0) || htm.indexOf("class=\"Folder\"") >=0  || htm.indexOf("class=Folder") >=0 || htm.indexOf("CalendarFolder.gif")>=0 || htm.indexOf("TaskList.gif")>=0) {
				rows = tds[i].getElementsByTagName("tr");
				break;
			}
		} 
	}

	if(rows == null)
		return null;


	for(var i=0; i< rows.length; i++) {
		var testElement = rows[i];
		if (testElement.textContent)
			var actTxt = testElement.textContent;
		else if (testElement.innerText)
			var actTxt = testElement.innerText;

		if(actTxt.indexOf(locator) >=0){
			if(ignoreFolderHdr){
				if(testElement.innerHTML.indexOf("Header") >0)
					continue;
			}
			_counter++;
			if(reqNumber == _counter){//found the row
				var tmp = testElement.getElementsByTagName("a");
				for(var j=0; j < tmp.length;j++){
					var a= tmp[j];
					if(!_collapse && !_expand && !_edit && !_uncheck && !_check && a.innerHTML.indexOf(locator)>0)
						return a;
					 else if(_collapse && a.innerHTML.indexOf("ImgNodeExpanded")>0)
						return a;
					else if(_expand && a.innerHTML.indexOf("ImgNodeCollapsed")>0)
						return a;
					else if(_check && a.innerHTML.indexOf("ImgTaskCheckbox")>0)
						return a;	
					else if(_uncheck && a.innerHTML.indexOf("ImgTask")>0)
						return a;							
					else if(_edit && a.parentNode.className == "ZhTreeEdit")//return edit-link
						return a;
				}
			}
		}
	}
	return null;
};


BrowserBot.prototype.findZButton_html = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);


	var elements = this.getButtons_html();
	if(elements == null)
		return null;


	for(var i=0; i< elements.length; i++) {
		var testElement = elements[i];
		if(testElement.tagName.toLowerCase() == "input")
			var actTxt = testElement.value;
		else if (testElement.textContent)
			var actTxt = testElement.textContent;
		else if (testElement.innerText)
				var actTxt = testElement.innerText;

		if(actTxt.indexOf(locator) >=0){
			_counter++;
			if(reqNumber == _counter){
				return testElement;
			}
		}
	}
	return null;
};

BrowserBot.prototype.findZButton = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);

	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;

	if(panel == undefined || panel == "") {
		var check1 = "((parseInt(testElement.style.zIndex) == 300" + 
				 "|| (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == \"block\"))"+		
				"&& (testElement.innerHTML.indexOf(locator) >= 0))";
	} else if(panel == "dialog"){
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700 " +
			" && (testElement.className.indexOf(\"Dialog\") >= 0) "+
			" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else if(panel.indexOf("__dialogByName__")>=0){
				var dlgName = panel.replace("__dialogByName__", ""); 
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700" + 
		" && (testElement.className.indexOf(\"Dialog\") >= 0)" + 
		" && (testElement.innerHTML.indexOf(\""+dlgName+"\") >= 0)" +
		" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else {
		return null;
	}

	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		var testElementHtml = testElement.innerHTML;
		if (eval("(" + check1 + ")")) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (((testElement.className.indexOf("ZToolbarButton") >= 0)
						|| (testElement.className.indexOf("DwtToolbarButton") >= 0)
						|| (testElement.className.indexOf("ZButton ") >= 0) 
					|| ((testElement.className.indexOf("ZSelectAutoSizingContainer") >= 0) && (testElement.className.indexOf("ZHasDropDown") >= 0))
				
					) && (testElement.innerHTML.indexOf(locator) >= 0)) {
						if(locator.indexOf("Img") >=0) {
							_counter++;
							if(reqNumber == _counter)
								return testElement;
						} else {
							if (testElement.textContent)
								var actTxt = testElement.textContent;
							else if (testElement.innerText)
								var actTxt = testElement.innerText;
							//do perfect match or if its menu, do starts-with
							if(actTxt == locator || ((actTxt.indexOf(locator) ==0) &&(testElement.className.indexOf("ZSelectAutoSizingContainer") >= 0))) {
								_counter++;
								if(reqNumber == _counter)
									return testElement;
							}
						}
				} 
			}

		}
	}
	return null;
};

BrowserBot.prototype.findZListItem_html = function(locator, panel, objNumber, listNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	var _counter = 0;
	var reqNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqNumber = parseInt(objNumber);


	var elements = this.getFormClass_html("List");
	if(elements == null)
		return null;

	var rows = elements.getElementsByTagName("tr");
	for(var i=0; i< rows.length; i++) {
		var testElement = rows[i];
		if (testElement.textContent)
			var actTxt = testElement.textContent;
		else if (testElement.innerText)
				var actTxt = testElement.innerText;

		if(actTxt.indexOf(locator) >=0){
			_counter++;
			if(reqNumber == _counter){
				return testElement;
			}
		}
	}
	return null;
};

BrowserBot.prototype.findZLinkInListItem = function(linkObj, linkName) {
	var anchrs = linkObj.getElementsByTagName("a");
	for(var i = 0; i < anchrs.length; i++) {
		if(anchrs[i].innerHTML == linkName)
			return anchrs[i];
	}
	return null;
};

BrowserBot.prototype.findZListItem = function(locator, panel, objNumber, listNumber) {

	var win = this.getCurrentWindow();
	var newBtn = true;
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	var reqObjNumber = 1;
	var reqListNumber = 1;
	if(objNumber != undefined && objNumber != "")
		var reqObjNumber = parseInt(objNumber);
	if(listNumber != undefined && listNumber != "")
		var reqListNumber = parseInt(listNumber);

	var list = "";
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		var testElementInnerHtml = testElement.innerHTML;
		
		if  (
			(parseInt(testElement.style.zIndex) >= 300 || (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == "block")) 
			&& (testElementInnerHtml.indexOf("DwtListView")>=0 ||testElementInnerHtml.indexOf("ZmColListDiv")>=0 ||testElementInnerHtml.indexOf("ZmFilterListView")>=0 
			|| testElementInnerHtml.indexOf("DwtChooserListView")>=0 || testElementInnerHtml.indexOf("ZmContactSimpleView") >= 0
			|| testElement.className == "DwtListView")){

			if(testElement.className == "DwtListView" ){//special-case when the entire page is a listview
				list = testElement;
				break;
			}
			var div1 = testElement.getElementsByTagName("DIV");
			var counter =1;

			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				var cls = testElement.className;
				if(cls=="DwtListView" || cls.indexOf("DwtChooserListView") >=0 
					|| cls.indexOf("ZmFilterListView") >=0 || cls.indexOf("ZmContactSimpleView") >=0
					||  cls.indexOf("ZmColListDiv") >=0){
					if(counter == reqListNumber) {
						list = testElement;
						break;
					} else if(counter < reqListNumber) {
						counter++;
					}
				}
			}
		}

		if(list != "")
			break;
	}

		if(list == "")
			return null;
		var counter =1;
		var div2 = list.getElementsByTagName("DIV");
		for (var k = 0; k < div2.length; k++) {
			var testElement = div2[k];
			var innerTxt = "";
			testElement.textContent ? innerTxt = testElement.textContent :  innerTxt = testElement.innerText;

			if ((testElement.className.indexOf("Row ") >= 0) && (innerTxt.indexOf(locator) >= 0)) {
				if(counter == reqObjNumber) {
					return testElement;
				} else if(counter < reqObjNumber) {
					counter++;
				}
					
			} 
		}

	return null;
};

BrowserBot.prototype.documentCore =  function(locator, action, linkName){	
	var row = this.findZDocumentTOCRow(locator);
	var element = null;
	if (linkName == "")
		linkName = locator;

	if(row == null)
		return null;

	var lnk = row.getElementsByTagName("a");
	for(var j=0;j<lnk.length;j++) {
		if(lnk[j].innerHTML.indexOf(linkName) >=0) {
			var element = lnk[j];
			break;
		}
	}
	if(element == null)
		return null;
	return	this.actOnZElement(element, action, locator); 
}


BrowserBot.prototype.findZDocumentTOCRow = function(locator) {

		var frame = this.findZIframeByView("ZmNotebookPageView");
		if(frame == null)
			return null;
	
		var rowElements = frame.getElementsByTagName("TR");
		for (var i = 0; i < rowElements.length; i++) {
			var testElement = rowElements[i];
			if (testElement.className.indexOf("zmwiki-dotLine") >= 0 && testElement.innerHTML.indexOf(locator) >0 ) {
				return testElement;
			}
		}
		return null;

};

BrowserBot.prototype.findZEditor = function( panel, param1) {
	return this.findZIframeOrTextArea("ZmHtmlEditor", panel, param1);
}
BrowserBot.prototype.findZMsgBody = function( panel, param1) {
	return this.findZIframeOrTextArea("MsgBody", panel, param1);
}
BrowserBot.prototype.findZMsgHeader = function(locator, panel, objNumber) {
	if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.innerHTML.indexOf("MsgHeaderTable") >= 0)) {
			var div1 = testElement.getElementsByTagName("table");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (testElement.className.indexOf("MsgHeaderTable") >= 0) {
					return testElement;
				}
			}
	
		}
	}
}


//returns the first iframe thats visible based on the view
BrowserBot.prototype.findZIframeByView = function(locator) {
	if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	var win = this.getCurrentWindow();
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;

	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.className.indexOf(locator) >= 0)) {
				var editorObjIframes = testElement.getElementsByTagName("iframe");
				if(editorObjIframes.length >0) {
					for(var n=0; n< editorObjIframes.length; n++) {
						var frame = editorObjIframes[0];
						if(!frame.style.display)
							return frame.contentWindow.document.body;
						else if(frame.style.display != "hidden")
							return frame.contentWindow.document.body;
					}
				}
			}
	}
	return null;
}
BrowserBot.prototype.findZIframeOrTextArea = function(locator, panel, param1) {

	if(locator.indexOf("=")>0) {	
		return (this.findElementOrNull(locator));
	}
	var win = this.getCurrentWindow();
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;


	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.innerHTML.indexOf(locator) >= 0)) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (testElement.className.indexOf(locator) >= 0) {
					var editorObjs = testElement.getElementsByTagName("textarea");
					var editorObjIframes = testElement.getElementsByTagName("iframe");
					if(editorObjs.length == 1) {
						if(editorObjs[0].style.display != "none") 
							return editorObjs[0];
					}
					if(editorObjIframes.length >0) {
						for(var n=0; n< editorObjIframes.length; n++) {
							var frame = editorObjIframes[0];
							if(!frame.style.display)
								return frame.contentWindow.document.body;
							else if(frame.style.display != "hidden")
								return frame.contentWindow.document.body;
						}
						//return	editorObjIframes[0].contentWindow.document.body;
					}
				}
			}
	
		}
	}		
}

BrowserBot.prototype.findZIframeOrTextArea_html = function(locator, panel, param1) {
	var form = this.getForm_html();
	var arry = new Array();

	if(form == null)
		return null;

	try{
		var iframe = this.getCurrentWindow().document.getElementById("body_editor");
		if(iframe){
			return iframe.contentWindow.document.body;
		}
	} catch(e){}
	


	var textareas =  form.getElementsByTagName("textarea");
	for(var i=0; i< textareas.length; i++) {
		var ta =  textareas[i];
		try{
			if(ta.className == "MsgCompose" && ta.style.display != "hidden" && ta.type != "hidden"){
				return ta;
			}
		}catch(e) {}
	}
	return null;
	
	
}

BrowserBot.prototype.editorCore_html = function(locator, action, data, panel, objNumber) {
	var element = this.findZIframeOrTextArea_html(locator, panel, objNumber);

	if(element == null && action == "notexist")
		return true;
	else if(element == null)
		return false;

	if(action == "type") {
		 this.replaceText(element, data);
		 return this.setSelNGResultAndReturn(true);
	}  else
		return	this.actOnZElement(element, action, locator); 
}

BrowserBot.prototype.editorCore = function(locator, action, data, panel, objNumber) {
	var element = this.findZEditor(locator, panel, objNumber);
	if(element == null && action == "notexist")
		return true;
	else if(element == null)
		return false;
	
	if(action == "type") {
		 this.replaceText(element, data);
		 return this.setSelNGResultAndReturn(true);
	}  else
		return	this.actOnZElement(element, action, locator); 
}

BrowserBot.prototype.msgBodyCore = function(locator, action, data, panel, objNumber) {

	var element = this.findZMsgBody(locator, panel, objNumber);
	
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	
		return	this.actOnZElement(element, action, locator); 
}
BrowserBot.prototype.msgHeaderCore = function(locator, action, data, panel, objNumber) {
	var element = this.findZMsgHeader(locator, panel, objNumber);
	
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	
		return	this.actOnZElement(element, action, locator); 
}


BrowserBot.prototype.textAreaCore = function(objName, action, data, panel, objNumber) {
		return this.formObjCore(objName, "textarea", "textarea", action, data, panel, objNumber);
}
BrowserBot.prototype.radioBtnCore = function(objName, action, data, panel, objNumber) {
		return this.formObjCore(objName, "input", "radio", action, data, panel, objNumber);
}
BrowserBot.prototype.checkBoxCore = function(objName, action, data, panel, objNumber) {
	return this.formObjCore(objName, "input", "checkbox", action, data, panel, objNumber);
}
BrowserBot.prototype.editFieldCore = function(objName, action, data, panel, objNumber) {
	return this.formObjCore(objName, "input", "text", action, data, panel, objNumber);
}
BrowserBot.prototype.browseFileFieldCore = function(objName, action, data, panel, objNumber) {
	return this.formObjCore(objName, "input", "file", action, data, panel, objNumber);
}
BrowserBot.prototype.pwdFieldCore = function(objName, action, data, panel, objNumber) {
	return this.formObjCore(objName, "input", "password", action, data, panel, objNumber);
}
BrowserBot.prototype.calenadarCheckBoxCore = function(objName, action, data, panel, objNumber) {
	return this.formObjCore(objName, "input", "checkbox", action, data, panel, objNumber);
}

BrowserBot.prototype.formObjCore = function(objName, objTag, objType, action, data, panel, objNumber) {
	var element = this.findZFormObject(objName, objTag, objType, panel, objNumber);
	
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	
	if(action == "type") {
		 this.replaceText(element, data);
		 return true;
	}  else if(action == "checked"){ 
		return this.setSelNGResultAndReturn(element.checked);
	} else if(action == "gettext") {
		var val = element.value;
		if(element.value == "")
				val = "<blank>";
			return this.setSelNGResultAndReturn(val);
	}else
		return	this.actOnZElement(element, action, objName); 
}

BrowserBot.prototype.isHtmlClient = function() {
	if(this.baseUrl.indexOf("/h/") >=0)
		return true;
	else
		return false;
}

BrowserBot.prototype.findZFormObject = function(objName, objTag, objType, panel, objNumber ) {
	if(objName.indexOf("=")>0 && objName.indexOf("*")==-1) {	
		var html_objNumber = 1;
		if(panel != undefined && panel != ""){//panel has objNumber(yuck)
			html_objNumber = parseInt(panel);
		}

		if(html_objNumber >1){//if we need to find obj by number but its using id= or name=
			return this.findZFormObjectsMultipleElements_html(objName, objTag, objType, html_objNumber);
		}else {
			return (this.findElementOrNull(objName));
		}
	}
	var win = this.getCurrentWindow();
	var inDocument = win.document;
	if(objName.indexOf("=")>0 && objName.indexOf("*") >0) {	
		objName = objName.replace("*", "").replace("id=","");
		var objs = inDocument.getElementsByTagName(objTag);
		for(var i =0; i< objs.length; i++) {
			var tmpObj = objs[i];
			if((tmpObj.id).indexOf(objName) > 0 && tmpObj.type == objType) {
				return tmpObj;
			}
		}
		return null;
	}

	var startsWith = false;
	var ignoreInnerRow = false;
	if(objName.indexOf("::labelStartsWith") > 0) {
		objName = objName.replace("::labelStartsWith","");
		startsWith	= true;
	} else if(objName.indexOf("::fieldLabelIsAnObject") > 0) {
		objName = objName.replace("::fieldLabelIsAnObject", "");
		ignoreInnerRow = true;
	}
		
	var newBtn = true;
	var rowFound = false;
	var typeFlg = true;
	var mainDiv = "";
	var innerTxt = "";
	if(browserVersion.isIE)
		innerTxt = "innerText";
	else
		innerTxt = "textContent";

	var rowsWithObj = new Array();
	if(!objNumber) {
		objNumber = 1;
	}
	var form =  null;
	if(this.isHtmlClient()){
		form = this.getForm_html();
	}

	if(!form){
		var loginpage =false;
	    // Loop through all elements, looking for ones that have 
	// a value === our expected value
	//var divElements = inDocument.getElementsByTagName("DIV");
	if(inDocument.getElementById("z_shell") != undefined) { //zimbraajax
		var divElements = inDocument.getElementById("z_shell").childNodes;
	} else if(inDocument.getElementById("ZloginPanel") != undefined) {//login page
				 mainDiv = inDocument.getElementById("ZloginPanel");
				loginpage = true;
	} else if(inDocument.getElementById("DWT1") != undefined) {//compose new window
		var newwindowelement = inDocument.getElementById("DWT1");
		if(newwindowelement.className == "MainShell")
			var divElements =  newwindowelement.childNodes;
		else
			return null;
	} else {
		return null;
	}

	if(!loginpage) {
		if(panel == undefined || panel == "") {
			var check1 = "(parseInt(testElement.style.zIndex) == 300 "
			+ " && (testElement.innerHTML.indexOf(\"skin_outer\") == -1) "
			+ " &&(testElement."+innerTxt+".indexOf(objName) >= 0))";
		} else if(panel == "dialog"){
			var check1 =  "(parseInt(testElement.style.zIndex) >= 700 " +
				" && (testElement.className.indexOf(\"Dialog\") >= 0) "+
				" && (testElement."+innerTxt+".indexOf(objName) >= 0))";
		} else if(panel.indexOf("__dialogByName__")>=0){
						var dlgName = panel.replace("__dialogByName__", ""); 
			var check1 =  "(parseInt(testElement.style.zIndex) >= 700" + 
				" && (testElement.className.indexOf(\"Dialog\") >= 0)" + 
				" && (testElement."+innerTxt+".indexOf(\""+dlgName+"\") >= 0)" +
				" && (testElement."+innerTxt+".indexOf(objName) >= 0))";
		} else {
			return null;
		}
			for (var i = 0; i < divElements.length; i++) {
				var testElement = divElements[i];
				if (eval("(" +check1+ ")")) {
					mainDiv = testElement;
					break;
				}
			}
		}
	}

	if(!objTag) 
		objTag = this.formalizeHTMLTag("input");
	else
		objTag = this.formalizeHTMLTag(objTag);


	if(form != null)
		mainDiv = form;

	if(mainDiv == "") {
		return null;
	}
	    // Loop through all elements, looking for ones that have 
	// a value === our expected value
	var formalizedTr = this.formalizeHTMLTag("tr");
	var rowEls = mainDiv.getElementsByTagName(formalizedTr);
	for (var i = 0; i < rowEls.length; i++) {
		var actTxt = "";
		var rowObj = rowEls[i];
		var inhtml = (rowObj.innerHTML);
		if (rowObj.textContent)
			 actTxt = rowObj.textContent;
		else if (rowObj.innerText)
			 actTxt = rowObj.innerText;

		var objNameIndx = actTxt.indexOf(objName);
		var innerRowsLen = rowObj.getElementsByTagName(formalizedTr).length
		var innerRowIndx = inhtml.indexOf(formalizedTr);
		var fldrHeaderIndx = inhtml.indexOf("overviewHeader");
		var tagIndx = inhtml.indexOf(objTag);
		//if startsWith is required, makesure the first-letter of the row matches the locator
		if(startsWith)
			 var objNameBool =  (objNameIndx == 0);
		else
			 var objNameBool =  (objNameIndx >= 0);
		if (objNameBool && (tagIndx >= 0) && (fldrHeaderIndx== -1) && (innerRowIndx == -1 || innerRowsLen == 0 || innerRowIndx > tagIndx || (ignoreInnerRow && innerRowIndx > objNameIndx))) {
			if((objType != "text") && (objType != "textarea")) //if its not edit, makesure we have a radio/checkbox
					var typeFlg = (inhtml.indexOf(objType) >= 0);
					
				if(typeFlg) {
					rowsWithObj.push(rowObj);
					rowFound = true;
				}
		}
	}

//if nothing was found.. see if there is a row with obj AND internal-row..
	if(!rowFound) {
		for (var j = 0; j < rowEls.length; j++) {
			var rowObj = rowEls[j];
			var inhtml = rowObj.innerHTML;
			if (rowObj.textContent)
				var actTxt = rowObj.textContent;
			else if (rowObj.innerText)
				var actTxt = rowObj.innerText;

			if ((actTxt.indexOf(objName) >= 0) && (inhtml.indexOf(objTag) >= 0)){
				if((objType != "text") && (objType != "textarea")) //if its not edit, makesure we have a radio/checkbox
					var typeFlg = (inhtml.indexOf(objType) >= 0);
					
				if(typeFlg) {
					rowsWithObj.push(rowObj);
					rowFound = true;
				}
			}
		}

	}
		//use the last-row as the correct/required row(if object# is not specified)
	//if(objNumber <= rowsWithObj.length)//assumption: one row has only one object
	//	var rowObj  = rowsWithObj[objNumber-1];
	//else
		//var rowObj  = rowsWithObj[rowsWithObj.length-1];
			var objCounter = 0;
	var prevRowObjCounter = 0;
	if(rowFound) {
				for(var i=0; i< rowsWithObj.length; i++) {			
			var rowObj  = rowsWithObj[i];
			var formObjs = this.getAllFormObjs([rowObj], objTag, objType);
			objCounter = objCounter + formObjs.length;
			if (objNumber == objCounter) {
				return formObjs[objCounter-prevRowObjCounter-1];
			} else if (objNumber < objCounter) {
				return	 this._getFormObjsInRow_MultipleObjs(formObjs, rowObj, objName, objTag, objType, objNumber-prevRowObjCounter);
			} 
			prevRowObjCounter = prevRowObjCounter +formObjs.length;
		}
	}
	return null;
};

BrowserBot.prototype.getAllFormObjs = function(rowsWithObj, objTag, objType) {
		var arry = new Array();
		objTag = this.formalizeHTMLTag(objTag);
		for(var k = 0; k < rowsWithObj.length; k++) {
			 var tmp = rowsWithObj[k].getElementsByTagName(objTag);
			 for( var i=0; i < tmp.length; i++) {
				 var formObj = tmp[i];
				 if(!formObj.type) {
					 if((objType == "text") || (objType == "textarea"))
							 arry.push(tmp[i]);
				 } else if(formObj.type == objType) {
					 arry.push(tmp[i]);
				 }
			 }
		}

		return arry;
}

BrowserBot.prototype._getFormObjsInRow_MultipleObjs = function(formObjs, rowObj, objName, objTag, objType, objNumber) {
	var allNodes = rowObj.getElementsByTagName("*");
	var ParsePattern = "";
	var previousTxt = "";
	var objCnt = 0;
	var txtCount = 0;
	var preObjCnt = 0;
	var foundcount = 0;
	objTag = objTag.toLowerCase();
	for (var j = 0; j < allNodes.length; j++) {
		var someEl = allNodes[j];
		var nval = "";
		//get node name
		var nname = someEl.nodeName.toLowerCase();
		//get node text
			if (someEl.textContent)
				 nval = someEl.textContent;
			else if (someEl.innerText)
				 nval = someEl.innerText;

		if (nname == objTag) {
			ParsePattern = ParsePattern + "O";
			objCnt = objCnt + 1;
			previousTxt = "O";
		} else if (nval.indexOf(objName) == 0 || nval.indexOf(objName) == 1) {
			if (previousTxt != "F") {
				ParsePattern = ParsePattern + "F";
				foundcount = foundcount + 1;
			}
			preObjCnt = objCnt;
			previousTxt = "F";
			txtCount = txtCount + 1;
		} else if (nval.length > 1) {
			if (previousTxt != "T") {
				ParsePattern = ParsePattern + "T";
			}
			previousTxt = "T";
			txtCount = txtCount + 1;
		}

		if ((txtCount == objCnt) && (ParsePattern.indexOf("F") >= 0) && (ParsePattern.indexOf("O") >= 0))
			break;

	}
	if (foundcount > 1 && foundcount == objCnt)
		return formObjs[objCnt - 1];
	else if ((ParsePattern.indexOf("O") == 0) && (ParsePattern.indexOf("TOOF") >= 0) && (ParsePattern.indexOf("F") >= 0))
		return formObjs[preObjCnt - 1];
	else if (objCnt >= (preObjCnt + objNumber - 1) && (ParsePattern.indexOf("F") >= 0))
		return formObjs[preObjCnt + objNumber - 1];
	else if (objCnt >= (preObjCnt + objNumber - 1))
		return formObjs[objNumber];
	else
		return null;


}

BrowserBot.prototype.findZIconButton = function(locator) {

if(locator.indexOf("=")>0) {	
	return (this.findElementOrNull(locator));
}
	var win = this.getCurrentWindow();
	var newBtn = true;
	var inDocument = win.document;
	    // Loop through all elements, looking for ones that have 
	// a value === our expected value
	//var divElements = inDocument.getElementsByTagName("DIV");
	try {
		var divElements = inDocument.getElementById("z_shell").childNodes;
	} catch(e) {
		return null;
	}
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) == 300 &&
		    (testElement.className.indexOf("DwtControl") == -1) &&
		    ((testElement.className.indexOf("ZToolbar") >= 0) || (testElement.className.indexOf("ZmAppToolBar") >= 0))) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if ((testElement.className.indexOf("ZToolbarButton") >= 0) && (testElement.innerHTML.indexOf(locator) >= 0)) {
					return testElement;
				}
			}

		}
	}
	return null;
};


BrowserBot.prototype.findZButtonInDlg = function(buttonName, dialogName) {

if(buttonName.indexOf("=")>0) {	
	return (this.findElementOrNull(buttonName));
}
	var divElements = (this.findZDialog(dialogName)).getElementsByTagName("DIV");
	for (var j = 0; j < divElements.length; j++) {
		var testElement = divElements[j];
		if ((testElement.className.indexOf("ZButton") >= 0) && (testElement.innerHTML.indexOf(buttonName) >= 0))
			return testElement;
	}
	return null;
};


BrowserBot.prototype.findZTab = function(locator, panel, objNumber) {

if(locator.indexOf("=")>0) {	
	return (this.findElementOrNull(locator));
}
var tabCount = 0;
var reqNumber = 1;
if(objNumber != undefined && objNumber != "")
	var reqNumber = parseInt(objNumber);

	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	if(panel == undefined || panel == "") {
		var check1 ="((parseInt(testElement.style.zIndex) == 300 " + 
				 "|| (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == \"block\"))"+
				" && (testElement.innerHTML.indexOf(locator) >= 0) && " +
		    "(testElement.className.indexOf(\"ZToolbar\") == -1))";
	} else if(panel == "dialog"){
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700 " +
			" && (testElement.className.indexOf(\"Dialog\") >= 0) "+
			" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else if(panel.indexOf("__dialogByName__")>=0){
				var dlgName = panel.replace("__dialogByName__", ""); 
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700" + 
		" && (testElement.className.indexOf(\"Dialog\") >= 0)" + 
		" && (testElement.innerHTML.indexOf(\""+dlgName+"\") >= 0)" +
		" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else {
		return null;
	}

	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
				if (eval("(" + check1 + ")"))  {
			
			var div1 = testElement.getElementsByTagName("DIV");
			
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (((testElement.className.indexOf("Button") >= 0) || (testElement.className.indexOf("ZTab ") >= 0)) && (testElement.innerHTML.indexOf(locator) >= 0)) {
					tabCount++;
					if(reqNumber == tabCount)
						return testElement;
				}
			}

		}
	}
	return null;
};

BrowserBot.prototype.findZAppTab = function(locator) {

if(locator.indexOf("=")>0) {	
	return (this.findElementOrNull(locator));
}
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.innerHTML.indexOf(locator) >= 0)) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if ((testElement.className.indexOf("Button") >= 0)
						&& (testElement.innerHTML.indexOf(locator) >= 0)
						&& (testElement.className.indexOf("ZToolbar") == -1)
						&& (testElement.className.indexOf("ZAppTab") >= 0))
				{

					return testElement;
			}
			}

		}
	}

	return null;
};


BrowserBot.prototype.folderCollapseBtnCore = function(locator, action, panel, param1) {
	var element;
	var testElement = this.findZFolder(locator, panel, param1);
	
	if(testElement == null) 
		return false;
	var div1 = testElement.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
		var testElement = div1[j];
		if(testElement.className == "ImgNodeExpanded") {
			var element = testElement;
			break;	
		}
    }
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
}

BrowserBot.prototype.folderCheckboxCore = function(locator, action, panel, param1) {
	var element;
	var testElement = this.findZFolder(locator, panel, param1);
	if(testElement == null) 
		return false;
	var div1 = testElement.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
		var testElement = div1[j];
		if(testElement.className == "ZTreeItemCheckbox") {
			var element = testElement;
			break;	
		}
    }

	if(element == null && action == "notexist"){
		return this.setSelNGResultAndReturn(true);
	} else if(element == null) {
		return false;
	} else {
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
	}
};

BrowserBot.prototype.folderExpandBtnCore = function(locator, action, panel, param1) {
	var element;
	var testElement = this.findZFolder(locator, panel, param1);
	
	if(testElement == null) 
		return false;
	var div1 = testElement.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
		var testElement = div1[j];
		if(testElement.className == "ImgNodeCollapsed") {
			var element = testElement;
			break;	
		}
    }

	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
}

BrowserBot.prototype.apptCore = function(locator, action, panel, param1) {
		var element = this.findZAppt(locator, panel, param1);

	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		
}

BrowserBot.prototype.apptCore_html = function(locator, action, panel, param1) {
		var element = this.findZAppt_html(locator, panel, param1);

	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		
}
BrowserBot.prototype.calGridCore_html = function(locator, action, panel, param1) {
		var elementOrValue = this.findZcalGrid_html(locator, action, panel, param1);

	if(elementOrValue == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(elementOrValue == null)
		return false;
	else if(action == "getCount" || action == "getDT")
		return this.setSelNGResultAndReturn(elementOrValue);
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		
}



BrowserBot.prototype.folderCore = function(locator, action, panel, param1) {
	var element = this.findZFolder(locator, panel, param1);
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	 else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		

}

BrowserBot.prototype.featureMenuCore = function(locator, action, panel, param1) {
		var element = this.findZFeatureMenu(locator, panel, param1);
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		
}

BrowserBot.prototype.buttonCore_html = function(locator, action, panel, objNumber, param2) {
	var element = this.findZButton_html(locator, panel, objNumber);
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator, "", param2); //for click, rtclick, shiftclick etc
		
}

BrowserBot.prototype.tabCore_html = function(locator, action, panel, objNumber, param2) {
	var element = this.findZTabs_html(locator, panel, objNumber);
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator, "", param2); //for click, rtclick, shiftclick etc
		
}

BrowserBot.prototype.folderCore_html = function(locator, action, panel, objNumber, param2) {
	var element = this.findZFolder_html(locator, panel, objNumber);
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator, "", param2); //for click, rtclick, shiftclick etc
		
}



BrowserBot.prototype.buttonCore = function(locator, action, panel, objNumber, param2) {
	var element = this.findZButton(locator, panel, objNumber);
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator, "", param2); //for click, rtclick, shiftclick etc
		
}
BrowserBot.prototype.tabCore = function(locator, action, panel, param1) {
		var element = this.findZAppTab(locator, panel, param1);

	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		
}

BrowserBot.prototype.getMenuDownArrowOfZObj = function(testElement, locator) {

	if(locator.indexOf("=")>=0) {
	var	testElement = this._locateMenuArrowOfIdObj(testElement);
		if(testElement == null)
			return null;
	}
	var div1 = testElement.getElementsByTagName("DIV");
	for (var j = 0; j < div1.length; j++) {
		var testElement = div1[j];
		if(testElement.className.indexOf("ImgSelectPullDownArrow")>=0) {
			return testElement;
		}
	}
	return null;
	

}
BrowserBot.prototype._locateMenuArrowOfIdObj = function(testElement) {
		//we are using id to locate the button, walk up until we find pulldownArrow class, then drill down
	testElement = testElement.parentNode;
	var divElement = null;
	do {
			if(testElement.innerHTML.indexOf("ImgSelectPullDownArrow")>=0 && testElement.tagName.toLowerCase().indexOf("div")>=0) {
				divElement = testElement;
				break;	
			}
	} while (testElement = testElement.parentNode);

	return divElement;

}
BrowserBot.prototype.buttonMenuCore = function(locator, action, panel, param1) {
		var element;
	var testElement = this.findZButton(locator);
		if(testElement == null) 
		return false;
	var arrowElement = this.getMenuDownArrowOfZObj(testElement, locator);

	if(arrowElement == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(arrowElement == null)
		return false;
	else 
		return	this.actOnZElement(arrowElement, action, locator, "true"); //for click, rtclick, shiftclick etc
		
}

BrowserBot.prototype.tabCore = function(locator, action, panel, param1) {
	var element = this.findZTab(locator, panel, param1);
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		

}

BrowserBot.prototype.listItemCore_html = function(locator, action, panel, objNumber, listNumber) {
	var actOnLabel = false;//if true, finds the internal td/span object with the locator
	if(locator.indexOf("::actOnLabel") > 0) {
		locator = locator.replace("::actOnLabel", "");
		actOnLabel = true;
	}
	var element = this.findZListItem_html(locator, panel, objNumber, listNumber);
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;

	var elementInnerHTML = element.innerHTML;	
	if(action == "isUnread") {
		if(elementInnerHTML.indexOf("Unread") >= 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	} else if(action == "isRead") {
		if(elementInnerHTML.indexOf("class=\"Unread\"") >= 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	} else if(action == "isSelected") {
		if(elementInnerHTML.indexOf("Row-selected") >= 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	}  else if(action == "isTagged") {

		if(elementInnerHTML.indexOf("ImgTag") >= 0)
			return this.setSelNGResultAndReturn(true);
		else 
			return this.setSelNGResultAndReturn(false);
	}  else if(action == "isNotTagged") {
		if(elementInnerHTML.indexOf("ImgTag") >= 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	}  else if(action == "hasAttachment") {
		if(elementInnerHTML.indexOf("ImgAttachment") >= 0)
			return this.setSelNGResultAndReturn(true);
		else 
			return this.setSelNGResultAndReturn(false);
	}  else if(action == "hasNoAttachment") {
		if(elementInnerHTML.indexOf("ImgAttachment") >= 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	}  else if(action == "isFlagged"  ) {
		if(elementInnerHTML.indexOf("ImgFlagRed") >= 0)
			return this.setSelNGResultAndReturn(true);
		else 
			return this.setSelNGResultAndReturn(false);
	}  else if(action == "isNotFlagged") {
		if(elementInnerHTML.indexOf("class=\"ImgFlagRed\"") >= 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	}  else if(action == "hasHighPriority") {
		if(elementInnerHTML.indexOf("ImgPriorityHigh_list") >= 0)
			return this.setSelNGResultAndReturn(true);
		else 
			return this.setSelNGResultAndReturn(false);
	}  else if(action == "hasLowPriority" ) {
		if(elementInnerHTML.indexOf("ImgPriorityLow_list") >= 0)
			return this.setSelNGResultAndReturn(true);
		else 
			return this.setSelNGResultAndReturn(false);
	} else if((action == "selectchkbox") || (action == "ischecked") || (action == "isunchecked")) {
			var element = element.getElementsByTagName("input")[0];
			if(element == null && action == "notexist")
				return this.setSelNGResultAndReturn(true);
			else if(element == null)
				false;
			else if(action == "selectchkbox")
				return	this.actOnZElement(element, "click", locator); //change action to click
			else if (action == "ischecked") 
					return this.setSelNGResultAndReturn(element.checked);
			else if (action == "isunchecked"){
					return this.setSelNGResultAndReturn(!element.checked);
			}
	} else if(actOnLabel) {
			var validSpanFound = false;
			var td1 = element.getElementsByTagName("TD");
			for (var j = 0; j < td1.length; j++) {
				var testElement = td1[j];
				if(testElement.innerHTML.indexOf(locator)>=0 && testElement.innerHTML.toLowerCase().indexOf("<td")==-1) {
					//check if there is a valid-span element, if so, return that.
					var span = testElement.getElementsByTagName("SPAN");
					for (var k = 0; k < span.length; k++) {
						var spanEl = span[k];
						if(spanEl.innerHTML.indexOf(locator) >=0) {
							var element = spanEl;
							validSpanFound = true;
							break;	
						}
					}

					//if there are no valid span elements, then use the td element
					if(!validSpanFound)
						var element = testElement;


					break;//from the outer for loop	
	
				}
			}

			if(element == null && action == "notexist")
				return this.setSelNGResultAndReturn(true);
			else if(element == null)
				return false;
			else
				return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
	} else
		 return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc

}

BrowserBot.prototype.listItemCore = function(locator, action, panel, objNumber, listNumber) {
	var actOnLabel = false;//if true, finds the internal td/span object with the locator
	if(locator.indexOf("::actOnLabel") > 0) {
		locator = locator.replace("::actOnLabel", "");
		actOnLabel = true;
	}
	var element = this.findZListItem(locator, panel, objNumber, listNumber);
	if(action.indexOf("clickLink=")!= -1) {
		if(element != null) {
			var linkName = action.replace("clickLink=","");
			action = "click";
			element = this.findZLinkInListItem(element, linkName);
		}
	}
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	var elementInnerHTML = element.innerHTML;
	
	if(action == "isUnread") {
		if(elementInnerHTML.indexOf("Unread") >= 0)
			return this.setSelNGResultAndReturn(true);
		else 
			return this.setSelNGResultAndReturn(false);
	} else if(action == "isRead") {
		if(elementInnerHTML.indexOf("class=\"Unread\"") >= 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	} else if(action == "isSelected") {
		if(elementInnerHTML.indexOf("Row-selected") >= 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	}  else if(action == "isTagged") {

		if(elementInnerHTML.indexOf("ImgTag") >= 0)
			return this.setSelNGResultAndReturn(true);
		else 
			return this.setSelNGResultAndReturn(false);
	}  else if(action == "isNotTagged") {
		if(elementInnerHTML.indexOf("class=\"ImgTag") >= 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	}  else if(action == "hasAttachment") {
		if(elementInnerHTML.indexOf("class=\"ImgAttachment\"") >= 0)
			return this.setSelNGResultAndReturn(true);
		else 
			return this.setSelNGResultAndReturn(false);
	}  else if(action == "hasNoAttachment") {
		if(elementInnerHTML.indexOf("class=\"ImgAttachment\"") >= 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	}  else if(action == "isFlagged") {
		if(elementInnerHTML.indexOf("ImgFlagRed") >= 0)
			return this.setSelNGResultAndReturn(true);
		else 
			return this.setSelNGResultAndReturn(false);
	}  else if(action == "isNotFlagged") {
		if(elementInnerHTML.indexOf("class=\"ImgFlagRed\"") >= 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	}  else if(action == "hasHighPriority") {
		if(elementInnerHTML.indexOf("class=\"ImgPriorityHigh_list\"") >= 0)
			return this.setSelNGResultAndReturn(true);
		else 
			return this.setSelNGResultAndReturn(false);
	}  else if(action == "hasLowPriority") {
		if(elementInnerHTML.indexOf("class=\"ImgPriorityLow_list\"") >= 0)
			return this.setSelNGResultAndReturn(true);
		else 
			return this.setSelNGResultAndReturn(false);
	} else if((action == "expand") || (action == "collapse")) {
			if(action == "expand")
				var cls =  "ImgNodeCollapsed";
			else
				var cls =  "ImgNodeExpanded";

			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if(testElement.className == cls) {
					var element = testElement;
					break;	
				}
			}

			if(element == null && action == "notexist")
				return this.setSelNGResultAndReturn(true);
			else if(element == null)
				return false;
			else
				return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
	} else if(actOnLabel) {
			var validSpanFound = false;
			var td1 = element.getElementsByTagName("TD");
			for (var j = 0; j < td1.length; j++) {
				var testElement = td1[j];
				if(testElement.innerHTML.indexOf(locator)>=0 && testElement.innerHTML.toLowerCase().indexOf("<td")==-1) {
					//check if there is a valid-span element, if so, return that.
					var span = testElement.getElementsByTagName("SPAN");
					for (var k = 0; k < span.length; k++) {
						var spanEl = span[k];
						if(spanEl.innerHTML.indexOf(locator) >=0) {
							var element = spanEl;
							validSpanFound = true;
							break;	
						}
					}

					//if there are no valid span elements, then use the td element
					if(!validSpanFound)
						var element = testElement;


					break;//from the outer for loop	
	
				}
			}

			if(element == null && action == "notexist")
				return this.setSelNGResultAndReturn(true);
			else if(element == null)
				return false;
			else
				return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc	
	} else
		 return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc

}
BrowserBot.prototype.htmlMenuCore_html = function(locator, action, itemToSelect, itemNumber, menuNumber) {
	var element = this.findZFormObjectsMultipleElements_html(locator,"select", null, menuNumber);
	var itemsStr = "";

	var matchPartialText = false;
	if(itemToSelect.indexOf(".*") > 0) {
		itemToSelect = itemToSelect.replace(".*","");
		matchPartialText = true;
	}

	var reqNumber = 1;
	var _counter = 0;
	if(itemNumber != undefined && itemNumber != "")
		 reqNumber = parseInt(itemNumber);

	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else if(action == "click"){
		triggerEvent(element, 'focus', false);
	    var changed = false;
		var reqSelected = false;
		var totalItems  =  element.options.length;
		
		for (var i = 0; i < totalItems; i++) {
			var option = element.options[i];
			var actText = option.text;
			if (option.selected && (itemToSelect != actText || (matchPartialText && actText.indexOf(itemToSelect)== -1))) {
				option.selected = false;
				changed = true;
			}
			else if (!option.selected && (itemToSelect == actText || (matchPartialText && actText.indexOf(itemToSelect)>=0))) {
				_counter++;
				if(_counter == reqNumber) {
					option.selected = true;
					changed = true;					
				} else{
					option.selected = false;
					changed = true;
				}

			}
		}

		if (changed) {
			triggerEvent(element, 'change', true);
		}
		
		if(reqSelected)
			return this.setSelNGResultAndReturn(true);
		else
			return this.setSelNGResultAndReturn(false);

	} else if ( action=="getCount"){
		return this.setSelNGResultAndReturn(element.options.length);
	} else if(action == "getAllItems") {
		var itmsName = "";
		var totalItems  =  element.options.length;
		for (var i = 0; i < totalItems; i++) {
			var option = element.options[i];
			actText = option.text;
			if(itmsName == "")
				itmsName = itmsName + actText;
			else
				itmsName = itmsName + "::" + actText;
		}
		return this.setSelNGResultAndReturn(itmsName);

	} else if(action == "getSelected") {
		var totalItems  =  element.options.length;
		for (var i = 0; i < totalItems; i++) {
			var option = element.options[i];
			if(option.selected) {
					return this.setSelNGResultAndReturn(option.text);
			}

		}
	} else
		 return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc

}

BrowserBot.prototype.menuItemCore = function(locator, action, panel, param1) {
	var element = this.findZMenuItem(locator, panel, param1);
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else
		 return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc

}

BrowserBot.prototype.dialogCore = function(locator, action, panel, param1) {
	var element = this.findZDialog(locator, panel, param1);
	if(element == null && action == "notexist")
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;


		if(action =="getmessage") {
			var el = element.getElementsByTagName("*");
			for (var j = 0; j < el.length; j++) {
				var testElement = el[j];
				if(testElement.className.indexOf("DwtMsgArea")>=0 || testElement.className.indexOf("DwtConfirmDialogQuestion")>=0) {
					if (testElement.textContent)
						return testElement.textContent;
					else if (testElement.innerText)
						return testElement.innerText;
				}
			 }
			
		} else if(action == "getalltxt"){
			if (testElement.textContent)
				return testElement.textContent;
			else if (testElement.innerText)
				return testElement.innerText; 
		} else {
			 return	this.actOnZElement(element, action, locator); //for click, rtclick, shiftclick etc
		}
		return "Could not retrieve message. Check BrowserBot.prototype.dialogCore";
}

BrowserBot.prototype.findZMenuItem = function(menuItem, panel, param1) {
if(menuItem.indexOf("=")>0) {	
	return (this.findElementOrNull(menuItem));
}
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 500 && (testElement.className.indexOf("Menu") >= 0) && (testElement.innerHTML.indexOf(menuItem) >= 0)) {
			var div1 = testElement.getElementsByTagName("DIV");

			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (
					(	(testElement.className.indexOf("ZMenuItem") >= 0)
						||  (testElement.className.indexOf("ZSelectMenuItem") >= 0)
					) 
					&& (testElement.innerHTML.indexOf(menuItem) >= 0)
					) {
					if (testElement.textContent)
						var actText = testElement.textContent;
					else if (testElement.innerText)
						var actText = testElement.innerText;
					
					if(actText.indexOf("[") > 0)//strip text of any shortcut keys
						actText = actText.substring(0, actText.indexOf("["));


					if(actText == menuItem || actText == (menuItem +" "))
						return testElement;
			}
		}
		}

	}
	return null;

}
BrowserBot.prototype.zGetBrowserUserAgent = function()  {
	return  this.setSelNGResultAndReturn(navigator.userAgent);
}
BrowserBot.prototype.zGetZimbraVersion = function(param1, param2, param3, param4, param5, param6)  {
	var win = this.getCurrentWindow();
	 return this.setSelNGResultAndReturn(win.appCtxt.getSettings().getSetting("SERVER_VERSION").value);
}

BrowserBot.prototype.verifyZTable = function(tableId) {
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		var zIndx = parseInt(testElement.style.zIndex);
		if (!isNaN(zIndx) && (zIndx >= 300) && (testElement.innerHTML.indexOf(tableId) >= 0)) {
			var tblElements = testElement.getElementsByTagName("TABLE");
			for (var j = 0; j < tblElements.length; j++) {
				var testElement = tblElements[j];
				try {
					if (testElement.id.indexOf(tableId) >= 0) {
						return true;
					}
				} catch(e) {
				}
			}
		}
	}
	return false;
}

BrowserBot.prototype._getInnerMostElement = function(element, elementName) {
	var children = element.childNodes;
	for (var i = 0; i < children.length; i++) {
		var child = children[i];
		if (child.textContent.indexOf(elementName) >= 0 && child.childNodes.length > 0) {
			this._getInnerMostElement(child, elementName);
		} else if (child.textContent.indexOf(elementName) >= 0 && child.childNodes.length == 0) {
			return child;
		}
	}


	return null;
}

BrowserBot.prototype.verifyZview = function(viewName) {
	var className;
	switch (viewName) {
		case "Message":
			className = "ZmTradView";
			break;
		case "Conversation":
			className = "ZmConvDoublePaneView";
			break;
		case "Mail Compose":
			className = "ZmComposeView";
			break;
		case "Appointment Compose":
			className = "ZmApptComposeView";
			break;

		case "List":
			className = "ZmContactSplitView";
			break;
		case "Card":
			className = "ZmContactCardsView";
			break;
		case "Notebook Compose":
			className = "ZmPageEditView";
			break;
	}

	var win = this.getCurrentWindow();
	var inDocument = win.document;
	    // Loop through all elements, looking for ones that have 
	// a value === our expected value
	//var divElements = inDocument.getElementsByTagName("DIV");
	try {
		var divElements = inDocument.getElementById("z_shell").childNodes;
	} catch(e) {
		return null;
	}
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.className.indexOf(className) >= 0))
			return true;
	}
	return false;
};

BrowserBot.prototype.findZApp = function(locator) {

if(locator.indexOf("=")>0) {	
	return (this.findElementOrNull(locator));
}
	var win = this.getCurrentWindow();
	var inDocument = win.document;
	    // Loop through all elements, looking for ones that have 
	// a value === our expected value
	//var divElements = inDocument.getElementsByTagName("DIV");
	try {
		var divElements = inDocument.getElementById("z_shell").childNodes;
	} catch(e) {
		return null;
	}
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) == 300 && (testElement.className.indexOf("ZmAppChooser") >= 0) &&
		    (testElement.innerHTML.indexOf(locator) >= 0)) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if ((testElement.className.indexOf("ZButton") >= 0) && (testElement.innerHTML.indexOf(locator) >= 0))
					return testElement;
			}			

		}
	}
	return null;
};




BrowserBot.prototype.verifyZDisplayed = function(locatorWithZIndx) {
	var element = this.findElementOrNull(locatorWithZIndx);
	if (element != null && (element.style.zIndex >=300 || (parseInt(element.style.zIndex) == 100 && element.style.display == "block")) ) {
		return this.setSelNGResultAndReturn(true);
	} else
		return false;

}

BrowserBot.prototype.msgZHdrBodyCore_html = function(locator, action) {
	var element = this.findZElementByClassOrId_html(locator);
	if(element == null && (action == "notexist" || action == "notdisplayed")){
		return this.setSelNGResultAndReturn(true);
	}else if(element == null){
		return false;
	} else if(action == "gethtml"){
			return this.setSelNGResultAndReturn(element.innerHTML);
	}

	if(locator == "MsgBody") {
		if(element.innerHTML.indexOf("iframe")>0){//if msg is an iframe, get the body
			var iframe = element.getElementsByTagName("iframe")[0];
			try{
				if(iframe.contentDocument)  {
					element =iframe.contentDocument.body;
				} else if(iframe.contentWindow)  {
					var doc =iframe.contentWindow.document;
					element = doc.getElementsByTagName("body")[0];

				}
			} catch(e) {

			}
		}
	}

	if(element == null || element == undefined)
		 return false;

	return	this.actOnZElement(element, action); 
	
}

BrowserBot.prototype.miscZObjectCore_html = function(locator, action, panel, objNumber, param2) {
	var element = this.findZElementByClassOrId_html(locator, panel, objNumber);
	if(element == null && (action == "notexist" || action == "notdisplayed"))
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else 
		return	this.actOnZElement(element, action); 
	
}


BrowserBot.prototype.findZFormObjectsMultipleElements_html = function(locatorWithNameOrId, objTag, objType, objNumber) {
	var reqNumber = 1;
	var _counter = 0;
	if(objNumber != undefined && objNumber != ""){//panel has objNumber(yuck)
			reqNumber = parseInt(objNumber);
		}
	var mainForm = this.getForm_html();
	if(mainForm == null)
		return null;

	locatorWithNameOrId = locatorWithNameOrId.replace("id=","").replace("name=","");
	var formObjs =  mainForm.getElementsByTagName(objTag);
	for(var i=0; formObjs.length; i++) {
		var obj = formObjs[i];
		try{
			if(objType) {//objType is passed as null for html-menus(<select>)
				if(objType != obj.type)
					continue;
			}
			if(obj.id == locatorWithNameOrId || obj.name == locatorWithNameOrId){
				_counter++;
				if(_counter == reqNumber) {
					return obj;						
				}
			}
		}catch(e) {}
		
	}
	
	return null;
	
}

BrowserBot.prototype.findZElementByClassOrId_html = function(locator, panel, objNumber) {
	var element = this.getForm_html();
	if(element == null)
		return null;
	var temp = locator.split("/");
	for(var j=0; j<temp.length; j++) {
		var innerClassOrId = temp[j];
		if(innerClassOrId.indexOf("*") >=0)
			innerClassOrId = innerClassOrId.replace("*", "");
		if(j == temp.length-1)//last element
			var element =  this._getInnerObjFromMainObj_html(element, innerClassOrId, objNumber);
		else
			var element =  this._getInnerObjFromMainObj_html(element, innerClassOrId, 1);
		if(element == null)
			break;
	}
	return element;
	
}


BrowserBot.prototype.miscZObjectCore = function(classNameOridWithZIndx, action, panel,useXY, xyValue) {
	//if classname is passed, if one of the classname matches, it returns true
	//action can be click,dblClick, displayed
	//you can also enter class1/class2OrId/class3OrId where, class1orId has zIndex, and class2OrId is an innerElement thats
	//within class1. finally class3OrId is further down and within class2OrId.
	//	usage in java: str = obj.zMiscObj.zExistsDontWait("ZmApptComposeView ZWidget/*tzoneSelect/ZSelectAutoSizingContainer ZHasDropDown");

	var class2OrId = "";
	var hasInnerClass = false;
	var class1OrId = "";

	if(classNameOridWithZIndx.indexOf("/")>0) {
		var temp = classNameOridWithZIndx.split("/");
		class1OrId  = temp[0];
		if(class1OrId.indexOf("*") >=0)
			class1OrId = class1OrId.replace("*", "");
		
		hasInnerClass = true;

	} else {
			class1OrId = classNameOridWithZIndx;
			if(class1OrId.indexOf("*") >=0)
				class1OrId = class1OrId.replace("*", "");

	}
	var divElements = this.getShellChildNodes();
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if ((testElement.className.indexOf(class1OrId)>=0 || testElement.id.indexOf(class1OrId)>=0) 
			&& (testElement.style.zIndex >=300 || (parseInt(testElement.style.zIndex) == 100 && testElement.style.display == "block"))
			&& testElement.style.top != "0px") {
			var element = testElement;
			break;
		}
	}
		
	//get the internal class if required..
	var origElement = element;
	if(element != null && hasInnerClass){
		var temp = classNameOridWithZIndx.split("/");
		for(var j=1;j<temp.length;j++) {
			var innerClassOrId = temp[j];
			if(innerClassOrId.indexOf("*") >=0)
				innerClassOrId = innerClassOrId.replace("*", "");

			var element =  this._getInnerObjFromMainObj(element, innerClassOrId);
			if(element == null)
				break;
		}
	}

	if(element == null && (action == "notexist" || action == "notdisplayed"))
		return this.setSelNGResultAndReturn(true);
	else if(element == null)
		return false;
	else {
		if(useXY != "")
			action = action + "_addXY";

		return	this.actOnZElement(element, action, class2OrId, useXY,xyValue); 
	}
	
}

BrowserBot.prototype._getInnerObjFromMainObj_html = function(mainObj, reqClassOrIdElement, objNumber) {
		var reqNumber = 1;
		var _counter = 0;
		if(objNumber != undefined && objNumber != "")
			var reqNumber = parseInt(objNumber);

		var els = mainObj.getElementsByTagName("*");
		for (var j = 0; j < els.length; j++) {	
			var testElement = els[j];
			if(testElement.className == reqClassOrIdElement || testElement.id.indexOf(reqClassOrIdElement) >=0) {
				_counter++;
				if(_counter == reqNumber) {
					return testElement;						
				}
			}			
		}
		return null;
}

BrowserBot.prototype._getInnerObjFromMainObj = function(mainObj, reqClassOrIdElement) {
		//try with div..
		var div1 = mainObj.getElementsByTagName("*");
		var element = null;//reset
		for (var j = 0; j < div1.length; j++) {	
			var testElement = div1[j];
				if((testElement.className.indexOf(reqClassOrIdElement) >=0 || testElement.id.indexOf(reqClassOrIdElement) >=0) &&
					 testElement.style.visibility != "hidden") {
				var element = testElement;
				break;
			}
			
		}
		if(element == null){
			//try using td..
			var td1 = mainObj.getElementsByTagName("TD");
			var element = null;//reset
			for (var j = 0; j < td1.length; j++) {	
				var testElement = td1[j];
				if((testElement.className.indexOf(reqClassOrIdElement) >=0 || testElement.id.indexOf(reqClassOrIdElement) >=0) &&
					 testElement.style.visibility != "hidden"){
					var element = testElement;
					break;
				}			
			}
		}
	return element;
}


BrowserBot.prototype.findZFolder = function(locator, panel, param1) {
if(locator.indexOf("=")>0) {	
	return (this.findElementOrNull(locator));
	}
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;

	if(panel == undefined || panel == "") {
		var check1 = "(parseInt(testElement.style.zIndex) == 300 && (testElement.className.indexOf(\"ZmOverview\") >= 0) &&(testElement.innerHTML.indexOf(locator) >= 0))";
	} else if(panel == "dialog"){
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700 " +
			" && (testElement.className.indexOf(\"Dialog\") >= 0) "+
			" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else if(panel.indexOf("__dialogByName__")>=0){
		var dlgName = panel.replace("__dialogByName__", ""); 
		var check1 =  "(parseInt(testElement.style.zIndex) >= 700" + 
		" && (testElement.className.indexOf(\"Dialog\") >= 0)" + 
		" && (testElement.innerHTML.indexOf(\""+dlgName+"\") >= 0)" +
		" && (testElement.innerHTML.indexOf(locator) >= 0))";
	} else {
		return null;
	}
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (eval("(" +check1+ ")")) {
			var div1 = testElement.getElementsByTagName("DIV");
			for (var j = 0; j < div1.length; j++) {
				var testElement = div1[j];
				if (testElement.className == "DwtTreeItem" || testElement.className.indexOf("DwtTreeItem ") >= 0 || testElement.className.indexOf("DwtTreeItem-selected") >= 0 
					) {
					if (testElement.textContent)
						var actText = testElement.textContent;
					else if (testElement.innerText)
						var actText = testElement.innerText;					
					if(actText == locator || (actText.indexOf(locator)==0 && actText.indexOf("(")>0))
						return testElement;
				}
			}

		}
	}
	return null;
};

BrowserBot.prototype._getView = function() {

	var win = this.getCurrentWindow();
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) == 300 && (testElement.className == "ZmConvDoublePaneView")) {
			return testElement;
		}
	}
	return null;
};


BrowserBot.prototype.verify_msgBdyInHyb = function(msgText)  {
	return this._verifyMsgBody(msgText, "hybrid");
}

BrowserBot.prototype.verify_msgBdyInConv = function(msgText)  {
	return this._verifyMsgBody(msgText, "conversation");
}

BrowserBot.prototype._verifyMsgBody = function(msgText, view)  {
	var win = this.getCurrentWindow();
	var inDocument = win.document;
	var msgBodyid = "";
	var viewObj  = "";
	if(view == "conversation") {
		viewObj= this.findElementOrNull("id=zv|CV");
		msgBodyid ="zv|CV|MSG";
	} else if(view =="hybrid") {
		viewObj= this.findElementOrNull("id=zv|CLV");
		msgBodyid = "zv|CLV|MSG";
	} else
		return false;

	//check if the view is displayed
	if(viewObj.style.zIndex <300)
		return false;
	
	//check if the message with the correct text exist
	try {
		var iframeMsgBody = inDocument.getElementById(msgBodyid).getElementsByTagName("iframe");
	} catch(e) {
		return false;
	} 
	if(iframeMsgBody.length == 0)
		return false;

	var iframeHTML = iframeMsgBody[0].contentWindow.document.body.innerHTML;

	if(iframeHTML.indexOf(msgText) >=0) 
		return this.setSelNGResultAndReturn(true);
	else
		return false;
}

BrowserBot.prototype._getViewRowList = function() {
	var viewDivs = this._getView().getElementsByTagName("DIV");
	for (var i = 0; i < viewDivs.length; i++) {
		var testElement = viewDivs[i];
		if (testElement.className == "DwtListView-Rows") {
			return testElement;
		}
	}
	return null;
};

BrowserBot.prototype.storeViewHTML = function() {
	this._storedViewHTML = this._getView().innerHTML;
};

BrowserBot.prototype.appendChildToView = function() {
	var p = this.getCurrentWindow().document.createElement("p");
	p.id = "testObjID";
	this._getViewRowList().appendChild(p);
};

BrowserBot.prototype.verifyViewHasNoChild = function() {
	var inDocument = this.getCurrentWindow().document;
	if (inDocument.getElementById("testObjID") != null) {
		LOG.info("verifyViewHasNoChild called, obj exists");
		return false;
	} else {
		LOG.info("verifyViewHasNoChild called, obj NOT exists");
		var v = this._getViewRowList();
		this.appendChildToView();//appends child to convView(just to make sure we wait until the list is displayed)
		return true;
	}
};


BrowserBot.prototype.verifyNewView = function() {

	return ( this._storedViewHTML != this._getView().innerHTML) ? true : false;

};

BrowserBot.prototype.findZDialog = function(dialogName, panel, param1) {

if(dialogName.indexOf("=")>0) {	
	return (this.findElementOrNull(dialogName));
}

	var win = this.getCurrentWindow();
	var t1 = true;
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 500 && (testElement.className.indexOf("Dialog") >= 0) &&
		    ((dialogName == undefined) || (dialogName == "") || (dialogName != undefined && testElement.innerHTML.indexOf(dialogName) >= 0))) {
			return testElement;

		}
	}
	return null;
};


BrowserBot.prototype.closeDlgIfExists = function(dlgNameCommaBtnName) {
	//this function should be called using waitDecorator(with some timeout), that way, waitDecorator
	//	keeps calling this function until timout(or this returns true), consequently providing closeDlgIfExists
	var arry = dlgNameCommaBtnName.split(",");
	var buttonName, dialogName;
	(arry[0] != undefined) ? dialogName = arry[0] : dialogName = "";
	(arry[1] != undefined) ? buttonName = arry[1] : buttonName = "";


	if (!this.verifyZDialog(dialogName)) {//make sure dlg exists
		return false;
	} else {
		var element = this.browserbot.findZButtonInDlg(buttonName, dialogName);
		this.browserbot.clickZElement(element);
		return true;
	}

}
BrowserBot.prototype.verifyZDialog = function(dialogName) {
	if (this.findZDialog(dialogName))
		return true;
	else
		return false;
};

BrowserBot.prototype.verifyZText = function(text) {

	var win = this.getCurrentWindow();
	var t1 = true;
	var divElements = this.getShellChildNodes();
	if(divElements == null)
		return null;
	
	for (var i = 0; i < divElements.length; i++) {
		var testElement = divElements[i];
		if (parseInt(testElement.style.zIndex) >= 300 && (testElement.innerHTML.indexOf(text) >= 0)) {
			return true;
		}
	}
	return false;
};
/**
 * In non-IE browsers, getElementById() does not search by name.  Instead, we
 * we search separately by id and name.
 */
BrowserBot.prototype.locateElementByIdentifier = function(identifier, inDocument, inWindow) {
    return BrowserBot.prototype.locateElementById(identifier, inDocument, inWindow)
            || BrowserBot.prototype.locateElementByName(identifier, inDocument, inWindow)
            || null;
};

/**
 * Find the element with id - can't rely on getElementById, coz it returns by name as well in IE..
 */
BrowserBot.prototype.locateElementById = function(identifier, inDocument, inWindow) {
    var element = inDocument.getElementById(identifier);
    if (element && element.id === identifier) {
        return element;
    }
    else if (browserVersion.isIE || browserVersion.isOpera) {
        // SEL-484
        var xpath = '//*[@id=' + identifier.quoteForXPath() + ']';
        return BrowserBot.prototype
            .locateElementByXPath(xpath, inDocument, inWindow);
    }
    else {
        return null;
    }
};

/**
 * Find an element by name, refined by (optional) element-filter
 * expressions.
 */
BrowserBot.prototype.locateElementByName = function(locator, document, inWindow) {
    var elements = document.getElementsByTagName("*");

    var filters = locator.split(' ');
    filters[0] = 'name=' + filters[0];

    while (filters.length) {
        var filter = filters.shift();
        elements = this.selectElements(filter, elements, 'value');
    }

    if (elements.length > 0) {
        return elements[0];
    }
    return null;
};

/**
 * Finds an element using by evaluating the specfied string.
 */
BrowserBot.prototype.locateElementByDomTraversal = function(domTraversal, document, window) {

    var browserbot = this.browserbot;
    var element = null;
    try {
        element = eval(domTraversal);
    } catch (e) {
        return null;
    }

    if (!element) {
        return null;
    }

    return element;
};
BrowserBot.prototype.locateElementByDomTraversal.prefix = "dom";

/**
 * Evaluates an xpath on a document, and returns a list containing nodes in the
 * resulting nodeset. The browserbot xpath methods are now backed by this
 * function. A context node may optionally be provided, and the xpath will be
 * evaluated from that context.
 *
 * @param xpath       the xpath to evaluate
 * @param inDocument  the document in which to evaluate the xpath.
 * @param opts        (optional) An object containing various flags that can
 *                    modify how the xpath is evaluated. Here's a listing of
 *                    the meaningful keys:
 *
 *                     contextNode: 
 *                       the context node from which to evaluate the xpath. If
 *                       unspecified, the context will be the root document
 *                       element.
 *
 *                     namespaceResolver:
 *                       the namespace resolver function. Defaults to null.
 *
 *                     xpathLibrary:
 *                       the javascript library to use for XPath. "ajaxslt" is
 *                       the default. "javascript-xpath" is newer and faster,
 *                       but needs more testing.
 *
 *                     allowNativeXpath:
 *                       whether to allow native evaluate(). Defaults to true.
 *
 *                     ignoreAttributesWithoutValue:
 *                       whether it's ok to ignore attributes without value
 *                       when evaluating the xpath. This can greatly improve
 *                       performance in IE; however, if your xpaths depend on
 *                       such attributes, you can't ignore them! Defaults to
 *                       true.
 *
 *                     returnOnFirstMatch:
 *                       whether to optimize the XPath evaluation to only
 *                       return the first match. The match, if any, will still
 *                       be returned in a list. Defaults to false.
 */
function eval_xpath(xpath, inDocument, opts)
{
    if (!opts) {
        var opts = {};
    }
    var contextNode = opts.contextNode
        ? opts.contextNode : inDocument;
    var namespaceResolver = opts.namespaceResolver
        ? opts.namespaceResolver : null;
    var xpathLibrary = opts.xpathLibrary
        ? opts.xpathLibrary : null;
    var allowNativeXpath = (opts.allowNativeXpath != undefined)
        ? opts.allowNativeXpath : true;
    var ignoreAttributesWithoutValue = (opts.ignoreAttributesWithoutValue != undefined)
        ? opts.ignoreAttributesWithoutValue : true;
    var returnOnFirstMatch = (opts.returnOnFirstMatch != undefined)
        ? opts.returnOnFirstMatch : false;

    // Trim any trailing "/": not valid xpath, and remains from attribute
    // locator.
    if (xpath.charAt(xpath.length - 1) == '/') {
        xpath = xpath.slice(0, -1);
    }
    // HUGE hack - remove namespace from xpath for IE
    if (browserVersion && browserVersion.isIE) {
        xpath = xpath.replace(/x:/g, '')
    }


    // When using the new and faster javascript-xpath library,
    // we'll use the TestRunner's document object, not the App-Under-Test's document.
    // The new library only modifies the TestRunner document with the new 
    // functionality.
    if (xpathLibrary == 'javascript-xpath') {
        documentForXpath = document;
    } else {
        documentForXpath = inDocument;
    }
    var results = [];
    
    // Use document.evaluate() if it's available
    if (allowNativeXpath && documentForXpath.evaluate) {
        try {
            // Regarding use of the second argument to document.evaluate():
            // http://groups.google.com/group/comp.lang.javascript/browse_thread/thread/a59ce20639c74ba1/a9d9f53e88e5ebb5
            var xpathResult = documentForXpath
                .evaluate((contextNode == inDocument ? xpath : '.' + xpath),
                    contextNode, namespaceResolver, 0, null);
        }
        catch (e) {
            throw new SeleniumError("Invalid xpath: " + extractExceptionMessage(e));
        }
        finally{
            if (xpathResult == null) {
                // If the result is null, we should still throw an Error.
                throw new SeleniumError("Invalid xpath: " + xpath); 
            }
        }
        var result = xpathResult.iterateNext();
        while (result) {
            results.push(result);
            result = xpathResult.iterateNext();
        }
        return results;
    }

    // If not, fall back to slower JavaScript implementation
    // DGF set xpathdebug = true (using getEval, if you like) to turn on JS XPath debugging
    //xpathdebug = true;
    var context;
    if (contextNode == inDocument) {
        context = new ExprContext(inDocument);
    }
    else {
        // provide false values to get the default constructor values
        context = new ExprContext(contextNode, false, false,
            contextNode.parentNode);
    }
    context.setCaseInsensitive(true);
    context.setIgnoreAttributesWithoutValue(ignoreAttributesWithoutValue);
    context.setReturnOnFirstMatch(returnOnFirstMatch);
    var xpathObj;
    try {
        xpathObj = xpathParse(xpath);
    }
    catch (e) {
        throw new SeleniumError("Invalid xpath: " + extractExceptionMessage(e));
    }
    var xpathResult = xpathObj.evaluate(context);
    if (xpathResult && xpathResult.value) {
        for (var i = 0; i < xpathResult.value.length; ++i) {
            results.push(xpathResult.value[i]);
        }
    }
    return results;
}

/**
 * Finds an element identified by the xpath expression. Expressions _must_
 * begin with "//".
 */
BrowserBot.prototype.locateElementByXPath = function(xpath, inDocument, inWindow) {
    var results = eval_xpath(xpath, inDocument, {
        returnOnFirstMatch          : true,
        ignoreAttributesWithoutValue: this.ignoreAttributesWithoutValue,
        allowNativeXpath            : this.allowNativeXpath,
        xpathLibrary                : this.xpathLibrary,
        namespaceResolver           : this._namespaceResolver
    });
    return (results.length > 0) ? results[0] : null;
};

BrowserBot.prototype._namespaceResolver = function(prefix) {
    if (prefix == 'html' || prefix == 'xhtml' || prefix == 'x') {
        return 'http://www.w3.org/1999/xhtml';
    } else if (prefix == 'mathml') {
        return 'http://www.w3.org/1998/Math/MathML';
    } else {
        throw new Error("Unknown namespace: " + prefix + ".");
    }
}

/**
 * Returns the number of xpath results.
 */
BrowserBot.prototype.evaluateXPathCount = function(xpath, inDocument) {
    var results = eval_xpath(xpath, inDocument, {
        ignoreAttributesWithoutValue: this.ignoreAttributesWithoutValue,
        allowNativeXpath            : this.allowNativeXpath,
        xpathLibrary                : this.xpathLibrary,
        namespaceResolver           : this._namespaceResolver
    });
    return results.length;
};

/**
 * Finds a link element with text matching the expression supplied. Expressions must
 * begin with "link:".
 */
BrowserBot.prototype.locateElementByLinkText = function(linkText, inDocument, inWindow) {
    var links = inDocument.getElementsByTagName('a');
    for (var i = 0; i < links.length; i++) {
        var element = links[i];
        if (PatternMatcher.matches(linkText, getText(element))) {
            return element;
        }
    }
    return null;
};
BrowserBot.prototype.locateElementByLinkText.prefix = "link";

/**
 * Returns an attribute based on an attribute locator. This is made up of an element locator
 * suffixed with @attribute-name.
 */
BrowserBot.prototype.findAttribute = function(locator) {
    // Split into locator + attributeName
    var attributePos = locator.lastIndexOf("@");
    var elementLocator = locator.slice(0, attributePos);
    var attributeName = locator.slice(attributePos + 1);

    // Find the element.
    var element = this.findElement(elementLocator);

    // Handle missing "class" attribute in IE.
    if (browserVersion.isIE && attributeName == "class") {
        attributeName = "className";
    }

    // Get the attribute value.
    var attributeValue = element.getAttribute(attributeName);
    
    // IE returns an object for the "style" attribute
    if (attributeName == 'style' && typeof(attributeValue) != 'string') {
        attributeValue = attributeValue.cssText;
    }

    return attributeValue ? attributeValue.toString() : null;
};

/*
* Select the specified option and trigger the relevant events of the element.
*/
BrowserBot.prototype.selectOption = function(element, optionToSelect) {
    triggerEvent(element, 'focus', false);
    var changed = false;
    for (var i = 0; i < element.options.length; i++) {
        var option = element.options[i];
        if (option.selected && option != optionToSelect) {
            option.selected = false;
            changed = true;
        }
        else if (!option.selected && option == optionToSelect) {
            option.selected = true;
            changed = true;
        }
    }

    if (changed) {
        triggerEvent(element, 'change', true);
    }
};

/*
* Select the specified option and trigger the relevant events of the element.
*/
BrowserBot.prototype.addSelection = function(element, option) {
    this.checkMultiselect(element);
    triggerEvent(element, 'focus', false);
    if (!option.selected) {
        option.selected = true;
        triggerEvent(element, 'change', true);
    }
};

/*
* Select the specified option and trigger the relevant events of the element.
*/
BrowserBot.prototype.removeSelection = function(element, option) {
    this.checkMultiselect(element);
    triggerEvent(element, 'focus', false);
    if (option.selected) {
        option.selected = false;
        triggerEvent(element, 'change', true);
    }
};

BrowserBot.prototype.checkMultiselect = function(element) {
    if (!element.multiple)
    {
        throw new SeleniumError("Not a multi-select");
    }

};

BrowserBot.prototype.replaceText = function(element, stringValue) {
    triggerEvent(element, 'focus', false);
    triggerEvent(element, 'select', true);
    var maxLengthAttr = element.getAttribute("maxLength");
    var actualValue = stringValue;
    if (maxLengthAttr != null) {
        var maxLength = parseInt(maxLengthAttr);
        if (stringValue.length > maxLength) {
            actualValue = stringValue.substr(0, maxLength);
        }
    }

    if (getTagName(element) == "body") {
        if (element.ownerDocument && element.ownerDocument.designMode) {
            var designMode = new String(element.ownerDocument.designMode).toLowerCase();
            if (designMode = "on") {
                // this must be a rich text control!
                element.innerHTML = actualValue;
            }
        }
    } else {
        element.value = actualValue;
    }
    // DGF this used to be skipped in chrome URLs, but no longer.  Is xpcnativewrappers to blame?
    try {
        triggerEvent(element, 'change', true);
    } catch (e) {}
};

BrowserBot.prototype.clickElement = function(element, clientX, clientY) {
       this._fireEventOnElement("click", element, clientX, clientY);
};
BrowserBot.prototype.submit = function(formElement) {
    var actuallySubmit = true;
    this._modifyElementTarget(formElement);
    if (formElement.onsubmit) {
        if (browserVersion.isHTA) {
            // run the code in the correct window so alerts are handled correctly even in HTA mode
            var win = this.browserbot.getCurrentWindow();
            var now = new Date().getTime();
            var marker = 'marker' + now;
            win[marker] = formElement;
            win.setTimeout("var actuallySubmit = "+marker+".onsubmit();" +
                "if (actuallySubmit) { " +
                    marker+".submit(); " +
                    "if ("+marker+".target && !/^_/.test("+marker+".target)) {"+
                        "window.open('', "+marker+".target);"+
                    "}"+
                "};"+
                marker+"=null", 0);
            // pause for up to 2s while this command runs
            var terminationCondition = function () {
                return !win[marker];
            }
            return Selenium.decorateFunctionWithTimeout(terminationCondition, 2000);
        } else {
            actuallySubmit = formElement.onsubmit();
            if (actuallySubmit) {
                formElement.submit();
                if (formElement.target && !/^_/.test(formElement.target)) {
                    this.browserbot.openWindow('', formElement.target);
                }
            }
        }
    } else {
        formElement.submit();
    }
}



BrowserBot.prototype.actOnZElement = function(element, action, locator, useXY,param2) {
	var x = 10;
	var y = 10;
	var tagName = element.tagName.toLowerCase();
	var isZObjHTMLObj = false;
	var isZObjButWithid = false;
	if(useXY == "true"){
		var tmp = this.getAnyBrowserCoordinates(element).split(",");
		x = parseInt(tmp[0]);
		y= parseInt(tmp[1]);
	}

	if(locator != undefined) {
		//if(locator.indexOf("=")>=0) {
			if("tddiv".indexOf(tagName) == -1)  {//like editfield/chkbox/anchor etc
				isZObjHTMLObj = true;
			} else//zimbra obj but has id=DWT123 locator
				isZObjButWithid = true;
		//}
	}
	if(action.indexOf("_addXY")>=0) {
		action = action.replace("_addXY", "");
			var tmp =param2.split(",");
			x= x+ parseInt(tmp[0]);
			y= y+ parseInt(tmp[1]);
	}
	if((action ==  "click") || (action ==  "clickAndGetPerf")) {
		
		if(isZObjHTMLObj) {//for html buttons, ignore td and div
			this.clickElement(element);
			return true;
		}		
		this._fireEventOnElement("mousedown", element, x, y);
		this._fireEventOnElement("mouseup", element, x, y);
	}else if(action ==  "rtclick") {
		var xy = this.getCoordinates(element);
		x = xy[0];
		y = xy[1];	
		this.rightClick = true;
		this._fireEventOnElement("mousedown", element, x, y);
		this._fireEventOnElement("mouseup", element, x, y);
 		this.rightClick = false;
	} else if(action == "dblclick") {
		this._fireEventOnElement("dblclick", element, x, y);
		this._fireEventOnElement("mousedown", element, x, y);
		this._fireEventOnElement("dblclick", element, x, y);
		this._fireEventOnElement("mouseup", element, x, y);
	} else if(action ==  "mouseover") {
		this._fireEventOnElement("mouseover", element, x, y);
	} else if (action == "shiftclick"){
		this.shiftKeyDown = true;
		this._fireEventOnElement("mousedown", element, x, y);
		this._fireEventOnElement("mouseup", element, x, y);
		this.shiftKeyDown = false;
	} else if (action == "ctrlclick"){
		this.controlKeyDown = true;
		this._fireEventOnElement("mousedown", element, x, y);
		this._fireEventOnElement("mouseup", element, x, y);
		this.controlKeyDown = false;
	} else if (action == "exists") {
		if(!isZObjHTMLObj && !isZObjButWithid)
			return this.setSelNGResultAndReturn(true);
		else
			return this.setSelNGResultAndReturn(this.isZObjVisible(element));
	} else if (action == "notexist") {
		if(!isZObjHTMLObj && !isZObjButWithid)
			return this.setSelNGResultAndReturn(false);
		else//if we are dealing with zimbraObj, then verify zindex etc b4 saying anything
			return this.setSelNGResultAndReturn(!this.isZObjVisible(element));

	} else if(action == "wait") {
		return this.setSelNGResultAndReturn(true);
	} else  if(action == "enabled") {
		if(element.className.indexOf("ZDisabled") > 0 || element.innerHTML.indexOf("ZDisabled") > 0)
			return this.setSelNGResultAndReturn(false);
		else 
			return this.setSelNGResultAndReturn(true);
	} else if(action == "disabled") {
		if(element.className.indexOf("ZDisabled") > 0 || element.innerHTML.indexOf("ZDisabled") > 0)
			return this.setSelNGResultAndReturn(true);
		else
			return this.setSelNGResultAndReturn(false);
	} else if(action == "gettext") {			
			if (element.textContent)
				return this.setSelNGResultAndReturn(element.textContent);
			else if (element.innerText)
				return this.setSelNGResultAndReturn(element.innerText); 
			else if (element.value)
				return this.setSelNGResultAndReturn(element.value); 
	}  else if(action == "gethtml") {			
			return this.setSelNGResultAndReturn(element.innerHTML); 
	}else if(action == "getcoord") {
		return this.setSelNGResultAndReturn(this.getAnyBrowserCoordinates(element));

	}
	return this.setSelNGResultAndReturn(true);

};

//set this.SelNGResult with the retVal(as string) and return the result without any modification
BrowserBot.prototype.setSelNGResultAndReturn = function(retVal){
	this.selNGResult = "" +retVal;
	return retVal;
}
BrowserBot.prototype.getAnyBrowserCoordinates = function(element){
		var win = this.getCurrentWindow();
		if(browserVersion.isIE) {
			var tmp =this.getIECoordinates().split(",");
			var iex = parseInt(tmp[0]);
			var iey = parseInt(tmp[1]);
			var box = element.getBoundingClientRect();
			var boxx = box.left;
			var boxy = box.top;
			return (boxx+ iex)+ "," + (boxy + iey);
		} else {
			var xy = this.getCoordinates(element);
			x = xy[0];
			y = xy[1];	
			if(this._browserName == "Chrome")//google chrome's y axis is about 120 less(especially in popup-mode)
				y= y+85;

			//10 is manually added by getcoordinates function for other actions(remove that)
			return (x-10) +"," + (y - 10 + win.outerHeight - win.innerHeight);
		}
}

BrowserBot.prototype.getIECoordinates = function(){

	var win = this.getCurrentWindow();
	var inDocument = win.document;
  var w, h, offW, offH, diffW, diffH;
  var fixedW = 800;
  var fixedH = 600;
  var ieDiffWidth = 0;
  var ieDiffHeight =0;

  if (inDocument.all) {
    offW = inDocument.body.offsetWidth;
    offH = inDocument.body.offsetHeight;
    win.resizeTo(fixedW, fixedH);
    diffW = inDocument.body.offsetWidth  - offW;
    diffH = inDocument.body.offsetHeight - offH;
    w = fixedW - diffW;
    h = fixedH - diffH;
    ieDiffWidth  = w - offW;
    ieDiffHeight = h - offH;
    win.resizeTo(w, h);
  }
  return ieDiffWidth + "," + ieDiffHeight;
}
BrowserBot.prototype.getCoordinates = function(obj) {
	var curleft = curtop = 10;
	if (obj.offsetParent) {
		do {
			curleft += obj.offsetLeft;
			curtop += obj.offsetTop;
		} while (obj = obj.offsetParent);
	}
	return [curleft,curtop];
}

BrowserBot.prototype.isZObjVisible = function(obj) {
	if(this.baseUrl.indexOf("/h/") >=0)//html client
		return true;

	do {
		try{
			if((parseInt(obj.style.zIndex) >= 300) || (parseInt(obj.style.zIndex) == 100 && obj.style.display == "block"))
				return true;
		} catch(e) {}
	} while (obj = obj.parentNode);
	return false;
}
BrowserBot.prototype.clickZElement = function(element) {
      this.actOnZElement(element, "click");
};
BrowserBot.prototype.doubleclickZElement = function(element) {
	this.actOnZElement(element, "dblclick");
};
BrowserBot.prototype.rightClickZElement = function(element) {
	this.actOnZElement(element, "rtclick");
};


BrowserBot.prototype.doubleClickElement = function(element, clientX, clientY) {
       this._fireEventOnElement("dblclick", element, clientX, clientY);
};

// The contextmenu event is fired when the user right-clicks to open the context menu
BrowserBot.prototype.contextMenuOnElement = function(element, clientX, clientY) {
       this._fireEventOnElement("contextmenu", element, clientX, clientY);
};

BrowserBot.prototype._modifyElementTarget = function(element) {
    if (element.target) {
        if (element.target == "_blank" || /^selenium_blank/.test(element.target) ) {
            var tagName = getTagName(element);
            if (tagName == "a" || tagName == "form") {
                var newTarget = "selenium_blank" + Math.round(100000 * Math.random());
                LOG.warn("Link has target '_blank', which is not supported in Selenium!  Randomizing target to be: " + newTarget);
                this.browserbot.openWindow('', newTarget);
                element.target = newTarget;
            }
        }
    }
}


BrowserBot.prototype._handleClickingImagesInsideLinks = function(targetWindow, element) {
    var itrElement = element;
    while (itrElement != null) {
        if (itrElement.href) {
            targetWindow.location.href = itrElement.href;
            break;
        }
        itrElement = itrElement.parentNode;
    }
}

BrowserBot.prototype._getTargetWindow = function(element) {
    var targetWindow = element.ownerDocument.defaultView;
    if (element.target) {
        targetWindow = this._getFrameFromGlobal(element.target);
    }
    return targetWindow;
}

BrowserBot.prototype._getFrameFromGlobal = function(target) {

    if (target == "_self") {
        return this.getCurrentWindow();
    }
    if (target == "_top") {
        return this.topFrame;
    } else if (target == "_parent") {
        return this.getCurrentWindow().parent;
    } else if (target == "_blank") {
        // TODO should this set cleverer window defaults?
        return this.getCurrentWindow().open('', '_blank');
    }
    var frameElement = this.findElementBy("implicit", target, this.topFrame.document, this.topFrame);
    if (frameElement) {
        return frameElement.contentWindow;
    }
    var win = this.getWindowByName(target);
    if (win) return win;
    return this.getCurrentWindow().open('', target);
}


BrowserBot.prototype.bodyText = function() {
    if (!this.getDocument().body) {
        throw new SeleniumError("Couldn't access document.body.  Is this HTML page fully loaded?");
    }
    return getText(this.getDocument().body);
};

BrowserBot.prototype.getAllButtons = function() {
    var elements = this.getDocument().getElementsByTagName('input');
    var result = [];

    for (var i = 0; i < elements.length; i++) {
        if (elements[i].type == 'button' || elements[i].type == 'submit' || elements[i].type == 'reset') {
            result.push(elements[i].id);
        }
    }

    return result;
};


BrowserBot.prototype.getAllFields = function() {
    var elements = this.getDocument().getElementsByTagName('input');
    var result = [];

    for (var i = 0; i < elements.length; i++) {
        if (elements[i].type == 'text') {
            result.push(elements[i].id);
        }
    }

    return result;
};

BrowserBot.prototype.getAllLinks = function() {
    var elements = this.getDocument().getElementsByTagName('a');
    var result = [];

    for (var i = 0; i < elements.length; i++) {
        result.push(elements[i].id);
    }

    return result;
};

function isDefined(value) {
    return typeof(value) != undefined;
}

BrowserBot.prototype.goBack = function() {
    this.getCurrentWindow().history.back();
};

BrowserBot.prototype.goForward = function() {
    this.getCurrentWindow().history.forward();
};

BrowserBot.prototype.close = function() {
    if (browserVersion.isChrome || browserVersion.isSafari || browserVersion.isOpera) {
        this.topFrame.close();
    } else {
        this.getCurrentWindow().eval("window.top.close();");
    }
};

BrowserBot.prototype.refresh = function() {
    this.getCurrentWindow().location.reload(true);
};

/**
 * Refine a list of elements using a filter.
 */
BrowserBot.prototype.selectElementsBy = function(filterType, filter, elements) {
    var filterFunction = BrowserBot.filterFunctions[filterType];
    if (! filterFunction) {
        throw new SeleniumError("Unrecognised element-filter type: '" + filterType + "'");
    }

    return filterFunction(filter, elements);
};

BrowserBot.filterFunctions = {};

BrowserBot.filterFunctions.name = function(name, elements) {
    var selectedElements = [];
    for (var i = 0; i < elements.length; i++) {
        if (elements[i].name === name) {
            selectedElements.push(elements[i]);
        }
    }
    return selectedElements;
};

BrowserBot.filterFunctions.value = function(value, elements) {
    var selectedElements = [];
    for (var i = 0; i < elements.length; i++) {
        if (elements[i].value === value) {
            selectedElements.push(elements[i]);
        }
    }
    return selectedElements;
};

BrowserBot.filterFunctions.index = function(index, elements) {
    index = Number(index);
    if (isNaN(index) || index < 0) {
        throw new SeleniumError("Illegal Index: " + index);
    }
    if (elements.length <= index) {
        throw new SeleniumError("Index out of range: " + index);
    }
    return [elements[index]];
};

BrowserBot.prototype.selectElements = function(filterExpr, elements, defaultFilterType) {

    var filterType = (defaultFilterType || 'value');

    // If there is a filter prefix, use the specified strategy
    var result = filterExpr.match(/^([A-Za-z]+)=(.+)/);
    if (result) {
        filterType = result[1].toLowerCase();
        filterExpr = result[2];
    }

    return this.selectElementsBy(filterType, filterExpr, elements);
};

/**
 * Find an element by class
 */
BrowserBot.prototype.locateElementByClass = function(locator, document) {
    return elementFindFirstMatchingChild(document,
            function(element) {
                return element.className == locator
            }
            );
}

/**
 * Find an element by alt
 */
BrowserBot.prototype.locateElementByAlt = function(locator, document) {
    return elementFindFirstMatchingChild(document,
            function(element) {
                return element.alt == locator
            }
            );
}

/**
 * Find an element by css selector
 */
BrowserBot.prototype.locateElementByCss = function(locator, document) {
    var elements = cssQuery(locator, document);
    if (elements.length != 0)
        return elements[0];
    return null;
}


/*****************************************************************/
/* BROWSER-SPECIFIC FUNCTIONS ONLY AFTER THIS LINE */

function MozillaBrowserBot(frame) {
    BrowserBot.call(this, frame);
}
objectExtend(MozillaBrowserBot.prototype, BrowserBot.prototype);

function KonquerorBrowserBot(frame) {
    BrowserBot.call(this, frame);
}
objectExtend(KonquerorBrowserBot.prototype, BrowserBot.prototype);

KonquerorBrowserBot.prototype.setIFrameLocation = function(iframe, location) {
    // Window doesn't fire onload event when setting src to the current value,
    // so we set it to blank first.
    iframe.src = "about:blank";
    iframe.src = location;
};

KonquerorBrowserBot.prototype.setOpenLocation = function(win, loc) {
    // Window doesn't fire onload event when setting src to the current value,
    // so we just refresh in that case instead.
    loc = absolutify(loc, this.baseUrl);
    loc = canonicalize(loc);
    var startUrl = win.location.href;
    if ("about:blank" != win.location.href) {
        var startLoc = parseUrl(win.location.href);
        startLoc.hash = null;
        var startUrl = reassembleLocation(startLoc);
    }
    LOG.debug("startUrl="+startUrl);
    LOG.debug("win.location.href="+win.location.href);
    LOG.debug("loc="+loc);
    if (startUrl == loc) {
        LOG.debug("opening exact same location");
        this.refresh();
    } else {
        LOG.debug("locations differ");
        win.location.href = loc;
    }
    // force the current polling thread to detect a page load
    var marker = this.isPollingForLoad(win);
    if (marker) {
        delete win.location[marker];
    }
};

KonquerorBrowserBot.prototype._isSameDocument = function(originalDocument, currentDocument) {
    // under Konqueror, there may be this case:
    // originalDocument and currentDocument are different objects
    // while their location are same.
    if (originalDocument) {
        return originalDocument.location == currentDocument.location
    } else {
        return originalDocument === currentDocument;
    }
};

function SafariBrowserBot(frame) {
    BrowserBot.call(this, frame);
}
objectExtend(SafariBrowserBot.prototype, BrowserBot.prototype);

SafariBrowserBot.prototype.setIFrameLocation = KonquerorBrowserBot.prototype.setIFrameLocation;
SafariBrowserBot.prototype.setOpenLocation = KonquerorBrowserBot.prototype.setOpenLocation;


function OperaBrowserBot(frame) {
    BrowserBot.call(this, frame);
}
objectExtend(OperaBrowserBot.prototype, BrowserBot.prototype);
OperaBrowserBot.prototype.setIFrameLocation = function(iframe, location) {
    if (iframe.src == location) {
        iframe.src = location + '?reload';
    } else {
        iframe.src = location;
    }
}

function IEBrowserBot(frame) {
    BrowserBot.call(this, frame);
}
objectExtend(IEBrowserBot.prototype, BrowserBot.prototype);

IEBrowserBot.prototype._handleClosedSubFrame = function(testWindow, doNotModify) {
    if (this.proxyInjectionMode) {
        return testWindow;
    }

    try {
        testWindow.location.href;
        this.permDenied = 0;
    } catch (e) {
        this.permDenied++;
    }
    if (this._windowClosed(testWindow) || this.permDenied > 4) {
        if (this.isSubFrameSelected) {
            LOG.warn("Current subframe appears to have closed; selecting top frame");
            this.selectFrame("relative=top");
            return this.getCurrentWindow(doNotModify);
        } else {
            var closedError = new SeleniumError("Current window or frame is closed!");
            closedError.windowClosed = true;
            throw closedError;
        }
    }
    return testWindow;
};

IEBrowserBot.prototype.modifyWindowToRecordPopUpDialogs = function(windowToModify, browserBot) {
    BrowserBot.prototype.modifyWindowToRecordPopUpDialogs(windowToModify, browserBot);

    // we will call the previous version of this method from within our own interception
    oldShowModalDialog = windowToModify.showModalDialog;

    windowToModify.showModalDialog = function(url, args, features) {
        // Get relative directory to where TestRunner.html lives
        // A risky assumption is that the user's TestRunner is named TestRunner.html
        var doc_location = document.location.toString();
        var end_of_base_ref = doc_location.indexOf('TestRunner.html');
        var base_ref = doc_location.substring(0, end_of_base_ref);
        var runInterval = '';
        
        // Only set run interval if options is defined
        if (typeof(window.runOptions) != 'undefined') {
            runInterval = "&runInterval=" + runOptions.runInterval;
        }
            
        var testRunnerURL = "TestRunner.html?auto=true&singletest=" 
            + escape(browserBot.modalDialogTest)
            + "&autoURL=" 
            + escape(url) 
            + runInterval;
        var fullURL = base_ref + testRunnerURL;
        browserBot.modalDialogTest = null;

        // If using proxy injection mode
        if (this.proxyInjectionMode) {
            var sessionId = runOptions.getSessionId();
            if (sessionId == undefined) {
                sessionId = injectedSessionId;
            }
            if (sessionId != undefined) {
                LOG.debug("Invoking showModalDialog and injecting URL " + fullURL);
            }
            fullURL = url;
        }
        var returnValue = oldShowModalDialog(fullURL, args, features);
        return returnValue;
    };
};

IEBrowserBot.prototype.modifySeparateTestWindowToDetectPageLoads = function(windowObject) {
    this.pageUnloading = false;
    var self = this;
    var pageUnloadDetector = function() {
        self.pageUnloading = true;
    };
    windowObject.attachEvent("onbeforeunload", pageUnloadDetector);
    BrowserBot.prototype.modifySeparateTestWindowToDetectPageLoads.call(this, windowObject);
};

IEBrowserBot.prototype.pollForLoad = function(loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker) {
    LOG.debug("IEBrowserBot.pollForLoad: " + marker);
    if (!this.permDeniedCount[marker]) this.permDeniedCount[marker] = 0;
    BrowserBot.prototype.pollForLoad.call(this, loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker);
    if (this.pageLoadError) {
        if (this.pageUnloading) {
            var self = this;
            LOG.debug("pollForLoad UNLOADING (" + marker + "): caught exception while firing events on unloading page: " + this.pageLoadError.message);
            this.reschedulePoller(loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker);
            this.pageLoadError = null;
            return;
        } else if (((this.pageLoadError.message == "Permission denied") || (/^Access is denied/.test(this.pageLoadError.message)))
                && this.permDeniedCount[marker]++ < 8) {
            if (this.permDeniedCount[marker] > 4) {
                var canAccessThisWindow;
                var canAccessCurrentlySelectedWindow;
                try {
                    windowObject.location.href;
                    canAccessThisWindow = true;
                } catch (e) {}
                try {
                    this.getCurrentWindow(true).location.href;
                    canAccessCurrentlySelectedWindow = true;
                } catch (e) {}
                if (canAccessCurrentlySelectedWindow & !canAccessThisWindow) {
                    LOG.debug("pollForLoad (" + marker + ") ABORTING: " + this.pageLoadError.message + " (" + this.permDeniedCount[marker] + "), but the currently selected window is fine");
                    // returning without rescheduling
                    this.pageLoadError = null;
                    return;
                }
            }

            var self = this;
            LOG.debug("pollForLoad (" + marker + "): " + this.pageLoadError.message + " (" + this.permDeniedCount[marker] + "), waiting to see if it goes away");
            this.reschedulePoller(loadFunction, windowObject, originalDocument, originalLocation, originalHref, marker);
            this.pageLoadError = null;
            return;
        }
        //handy for debugging!
        //throw this.pageLoadError;
    }
};

IEBrowserBot.prototype._windowClosed = function(win) {
    try {
        var c = win.closed;
        // frame windows claim to be non-closed when their parents are closed
        // but you can't access their document objects in that case
        if (!c) {
            try {
                win.document;
            } catch (de) {
                if (de.message == "Permission denied") {
                    // the window is probably unloading, which means it's probably not closed yet
                    return false;
                }
                else if (/^Access is denied/.test(de.message)) {
                    // rare variation on "Permission denied"?
                    LOG.debug("IEBrowserBot.windowClosed: got " + de.message + " (this.pageUnloading=" + this.pageUnloading + "); assuming window is unloading, probably not closed yet");
                    return false;
                } else {
                    // this is probably one of those frame window situations
                    LOG.debug("IEBrowserBot.windowClosed: couldn't read win.document, assume closed: " + de.message + " (this.pageUnloading=" + this.pageUnloading + ")");
                    return true;
                }
            }
        }
        if (c == null) {
            LOG.debug("IEBrowserBot.windowClosed: win.closed was null, assuming closed");
            return true;
        }
        return c;
    } catch (e) {
        LOG.debug("IEBrowserBot._windowClosed: Got an exception trying to read win.closed; we'll have to take a guess!");

        if (browserVersion.isHTA) {
            if (e.message == "Permission denied") {
                // the window is probably unloading, which means it's not closed yet
                return false;
            } else {
                // there's a good chance that we've lost contact with the window object if it is closed
                return true;
            }
        } else {
            // the window is probably unloading, which means it's not closed yet
            return false;
        }
    }
};

/**
 * In IE, getElementById() also searches by name - this is an optimisation for IE.
 */
IEBrowserBot.prototype.locateElementByIdentifer = function(identifier, inDocument, inWindow) {
    return inDocument.getElementById(identifier);
};

SafariBrowserBot.prototype.modifyWindowToRecordPopUpDialogs = function(windowToModify, browserBot) {
    BrowserBot.prototype.modifyWindowToRecordPopUpDialogs(windowToModify, browserBot);

    var originalOpen = windowToModify.open;
    /*
     * Safari seems to be broken, so that when we manually trigger the onclick method
     * of a button/href, any window.open calls aren't resolved relative to the app location.
     * So here we replace the open() method with one that does resolve the url correctly.
     */
    windowToModify.open = function(url, windowName, windowFeatures, replaceFlag) {

        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/")) {
            return originalOpen(url, windowName, windowFeatures, replaceFlag);
        }

        // Reduce the current path to the directory
        var currentPath = windowToModify.location.pathname || "/";
        currentPath = currentPath.replace(/\/[^\/]*$/, "/");

        // Remove any leading "./" from the new url.
        url = url.replace(/^\.\//, "");

        newUrl = currentPath + url;

        var openedWindow = originalOpen(newUrl, windowName, windowFeatures, replaceFlag);
        LOG.debug("window.open call intercepted; window ID (which you can use with selectWindow()) is \"" +  windowName + "\"");
        if (windowName!=null) {
            openedWindow["seleniumWindowName"] = windowName;
        }
        return openedWindow;
    };
};

MozillaBrowserBot.prototype._fireEventOnElement = function(eventType, element, clientX, clientY) {
    var win = this.getCurrentWindow();
    triggerEvent(element, 'focus', false);

    // Add an event listener that detects if the default action has been prevented.
    // (This is caused by a javascript onclick handler returning false)
    // we capture the whole event, rather than the getPreventDefault() state at the time,
    // because we need to let the entire event bubbling and capturing to go through
    // before making a decision on whether we should force the href
    var savedEvent = null;

    element.addEventListener(eventType, function(evt) {
        savedEvent = evt;
    }, false);

    this._modifyElementTarget(element);

    // Trigger the event.
    this.browserbot.triggerMouseEvent(element, eventType, true, clientX, clientY);

    if (this._windowClosed(win)) {
        return;
    }

    // Perform the link action if preventDefault was set.
    // In chrome URL, the link action is already executed by triggerMouseEvent.
    if (!browserVersion.isChrome && savedEvent != null && !savedEvent.getPreventDefault()) {
        var targetWindow = this.browserbot._getTargetWindow(element);
        if (element.href) {
            targetWindow.location.href = element.href;
        } else {
            this.browserbot._handleClickingImagesInsideLinks(targetWindow, element);
        }
    }

};


OperaBrowserBot.prototype._fireEventOnElement = function(eventType, element, clientX, clientY) {
    var win = this.getCurrentWindow();
    triggerEvent(element, 'focus', false);

    this._modifyElementTarget(element);

    // Trigger the click event.
	LOG.debug("in _fireEventOnElement eventType:" + eventType);
    this.browserbot.triggerMouseEvent(element, eventType, true, clientX, clientY);

    if (this._windowClosed(win)) {
        return;
    }

};


KonquerorBrowserBot.prototype._fireEventOnElement = function(eventType, element, clientX, clientY) {
    var win = this.getCurrentWindow();
    triggerEvent(element, 'focus', false);

    this._modifyElementTarget(element);

    if (element[eventType]) {
        element[eventType]();
    }
    else {
        this.browserbot.triggerMouseEvent(element, eventType, true, clientX, clientY);
    }

    if (this._windowClosed(win)) {
        return;
    }

};

SafariBrowserBot.prototype._fireEventOnElement = function(eventType, element, clientX, clientY) {
    triggerEvent(element, 'focus', false);
    var wasChecked = element.checked;

    this._modifyElementTarget(element);

    // For form element it is simple.
    if (element[eventType]) {
        element[eventType]();
    }
    // For links and other elements, event emulation is required.
    else {
        var targetWindow = this.browserbot._getTargetWindow(element);
        // todo: deal with anchors?
        this.browserbot.triggerMouseEvent(element, eventType, true, clientX, clientY);

    }

};

SafariBrowserBot.prototype.refresh = function() {
    var win = this.getCurrentWindow();
    if (win.location.hash) {
        // DGF Safari refuses to refresh when there's a hash symbol in the URL
        win.location.hash = "";
        var actuallyReload = function() {
            win.location.reload(true);
        }
        window.setTimeout(actuallyReload, 1);
    } else {
        win.location.reload(true);
    }
};

IEBrowserBot.prototype._fireEventOnElement = function(eventType, element, clientX, clientY) {
    var win = this.getCurrentWindow();
    triggerEvent(element, 'focus', false);

    var wasChecked = element.checked;

    // Set a flag that records if the page will unload - this isn't always accurate, because
    // <a href="javascript:alert('foo'):"> triggers the onbeforeunload event, even thought the page won't unload
    var pageUnloading = false;
    var pageUnloadDetector = function() {
        pageUnloading = true;
    };
    win.attachEvent("onbeforeunload", pageUnloadDetector);
    this._modifyElementTarget(element);
    if (element[eventType]) {
        element[eventType]();
    }
    else {
        this.browserbot.triggerMouseEvent(element, eventType, true, clientX, clientY);
    }


    // If the page is going to unload - still attempt to fire any subsequent events.
    // However, we can't guarantee that the page won't unload half way through, so we need to handle exceptions.
    try {
        win.detachEvent("onbeforeunload", pageUnloadDetector);

        if (this._windowClosed(win)) {
            return;
        }

        // Onchange event is not triggered automatically in IE.
        if (isDefined(element.checked) && wasChecked != element.checked) {
            triggerEvent(element, 'change', true);
        }

    }
    catch (e) {
        // If the page is unloading, we may get a "Permission denied" or "Unspecified error".
        // Just ignore it, because the document may have unloaded.
        if (pageUnloading) {
            LOG.logHook = function() {
            };
            LOG.warn("Caught exception when firing events on unloading page: " + e.message);
            return;
        }
        throw e;
    }
};