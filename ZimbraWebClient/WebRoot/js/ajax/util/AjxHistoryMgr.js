/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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
/*
   Derived from "Really Simple History", by Brad Neuberg. Its copyright follows:

   Copyright (c) 2005, Brad Neuberg, bkn3@columbia.edu
   http://codinginparadise.org
   
   Permission is hereby granted, free of charge, to any person obtaining 
   a copy of this software and associated documentation files (the "Software"), 
   to deal in the Software without restriction, including without limitation 
   the rights to use, copy, modify, merge, publish, distribute, sublicense, 
   and/or sell copies of the Software, and to permit persons to whom the 
   Software is furnished to do so, subject to the following conditions:
   
   The above copyright notice and this permission notice shall be 
   included in all copies or substantial portions of the Software.
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
   OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
   IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
   CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
   THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * Initializes history support.
 * @constructor
 * @class
 * This singleton class provides support for handling history changes via the Back
 * and Forward buttons. Since an Ajax application represents a single URL, hitting
 * the Back button will ordinarily unload the app, which is usually not what the user
 * wants to do. Changing the hash value in the browser's location will affect the
 * browser history, but not the content. IE also uses a hidden iframe to track history.
 * <p>
 * The code below is a stripped-down version of Brad Neuberg's Really Simple History
 * (see copyright above). Support for history storage has been removed.</p>
 * 
 * @author Conrad Damon
 * 
 * TODO: - add enable()
 * 
 * @private
 */
AjxHistoryMgr = function() {

	this.currentLocation = null;			// Our current hash location, without the "#" symbol.
	this.listener = null;					// Our history change listener. */
	this.iframe = null;						// A hidden IFrame we use in Internet Explorer to detect history changes.
	this.ignoreLocationChange = null;		// Indicates to the browser whether to ignore location changes.

	// The amount of time in ms to wait between add requests. 
	this.WAIT_TIME = AjxEnv.isIE ? 400 : 200;

	this.currentWaitTime = 0;				// Time in ms to wait before calling setTimeout to add a location

	/** A variable to handle an important edge case in Internet
	Explorer. In IE, if a user manually types an address into
	their browser's location bar, we must intercept this by
	continuously checking the location bar with a timer 
	interval. However, if we manually change the location
	bar ourselves programmatically, when using our hidden
	iframe, we need to ignore these changes. Unfortunately,
	these changes are not atomic, so we surround them with
	the variable 'ieAtomicLocationChange', that if true,
	means we are programmatically setting the location and
	should ignore this atomic chunked change. */
	this.ieAtomicLocationChange = null;

	this._eventMgr = new AjxEventMgr();
	this._evt = new AjxEvent();

	this._initialize();
}

// Name of the file that has content for the iframe
AjxHistoryMgr.BLANK_FILE = "blankHistory.html";

// ID for the iframe
AjxHistoryMgr.IFRAME_ID = "DhtmlHistoryFrame";


/**
 * Adds a history change listener.
 */
AjxHistoryMgr.prototype.addListener =
function(listener) {
	return this._eventMgr.addListener(AjxEvent.HISTORY, listener);
};

/**
 * Removes a history change listener.
 */
AjxHistoryMgr.prototype.removeListener =
function(listener) {
	return this._eventMgr.removeListener(AjxEvent.HISTORY, listener);
};

/**
 * Most browsers require that we wait a certain amount of time before changing the
 * location, such as 200 milliseconds; rather than forcing external callers to use
 * window.setTimeout to account for this to prevent bugs, we internally handle this
 * detail by using a 'currentWaitTime' variable and have requests wait in line
 */
AjxHistoryMgr.prototype.add =
function(newLocation) {

	var self = this;
	var addImpl = function() {
		
		// indicate that the current wait time is now less
		if (self.currentWaitTime > 0) {
			self.currentWaitTime = self.currentWaitTime - self.WAIT_TIME;
		}
		
		// remove any leading hash symbols on newLocation
		newLocation = self._removeHash(newLocation);
		   
		// IE has a strange bug; if the newLocation
		// is the same as _any_ preexisting id in the
		// document, then the history action gets recorded
		// twice; throw a programmer exception if there is
		// an element with this ID
		if (AjxEnv.isIE) {
			if (document.getElementById(newLocation)) {
				throw new DwtException("AjxHistoryMgr: location has same ID as DOM element");
			}
		}

		// indicate to the browser to ignore this upcoming location change
		self.ignoreLocationChange = true;
		 
		// indicate to IE that this is an atomic location change block
		this.ieAtomicLocationChange = true;
		     
		// save this as our current location
		self.currentLocation = newLocation;
		   
		// change the browser location
		window.location.hash = newLocation;
		   
		// change the hidden iframe's location if on IE
		if (AjxEnv.isIE) {
			self.iframe.src = AjxHistoryMgr.BLANK_FILE + "?" + newLocation;
		}
		
		// end of atomic location change block for IE
		this.ieAtomicLocationChange = false;
	};		
		
	// queue up requests
	window.setTimeout(addImpl, this.currentWaitTime);
	   
	// indicate that the next request will have to wait for awhile
	this.currentWaitTime = this.currentWaitTime + self.WAIT_TIME;
};

/**
 * Returns the current hash value that is in the browser's
 * location bar, removing leading # symbols if they are present.
 */   
AjxHistoryMgr.prototype.getCurrentLocation =
function() {
	return this._removeHash(window.location.hash);
};

/**
 * Creates the DHTML history infrastructure.
 */
AjxHistoryMgr.prototype._initialize =
function() {

	// get our initial location
	var initialHash = this.getCurrentLocation();
	
	// save this as our current location
	this.currentLocation = initialHash;
	
	// write out a hidden iframe for IE and
	// set the amount of time to wait between add() requests
	if (AjxEnv.isIE) {
		DBG.println(AjxDebug.DBG2, "Creating iframe for IE: " + AjxHistoryMgr.BLANK_FILE);
		var html = [];
		var i = 0;
		html[i++] = "<iframe style='border: 0px; width: 1px; ";
		html[i++] = "height: 1px; position: absolute; bottom: 0px; ";
		html[i++] = "right: 0px; visibility: visible;' ";
		html[i++] = "id='" + AjxHistoryMgr.IFRAME_ID + "' ";
		html[i++] = "src='" + AjxHistoryMgr.BLANK_FILE + "?" + initialHash + "'>";
		html[i++] = "</iframe>";
		
		var htmlElement = document.createElement("div");
		document.body.appendChild(htmlElement);
		htmlElement.innerHTML = html.join("");
	}

	if (AjxEnv.isIE) {
		this.iframe = document.getElementById(AjxHistoryMgr.IFRAME_ID);
	}  

	// other browsers can use a location handler that checks
	// at regular intervals as their primary mechanism;
	// we use it for Internet Explorer as well to handle
	// an important edge case; see _checkLocation() for
	// details
	var self = this;
	var locationHandler = function() {
		self._checkLocation();
	};
	window.onhashchange = locationHandler;
};

/**
 * Checks if the browsers has changed location.  This is the primary history mechanism
 * for Firefox. For Internet Explorer, we use this to handle an important edge case:
 * if a user manually types in a new hash value into their Internet Explorer location
 * bar and press enter, we want to intercept this and notify any history listener.
 */
AjxHistoryMgr.prototype._checkLocation =
function() {
	// ignore any location changes that we made ourselves
	// for browsers other than Internet Explorer
	if (!AjxEnv.isIE && this.ignoreLocationChange) {
	   this.ignoreLocationChange = false;
	   return;
	}

	// if we are dealing with Internet Explorer
	// and we are in the middle of making a location
	// change from an iframe, ignore it
	if (!AjxEnv.isIE && this.ieAtomicLocationChange) {
	   return;
	}

	// get hash location
	var hash = this.getCurrentLocation();

	// see if there has been a change
	if (hash == this.currentLocation) { return; }
   
	// on Internet Explorer, we need to intercept users manually
	// entering locations into the browser; we do this by comparing
	// the browsers location against the iframes location; if they
	// differ, we are dealing with a manual event and need to
	// place it inside our history, otherwise we can return
	this.ieAtomicLocationChange = true;

	if (AjxEnv.isIE && this._getIFrameHash() != hash) {
	   this.iframe.src = AjxHistoryMgr.BLANK_FILE + "?" + hash;
	} else if (AjxEnv.isIE) {
	   // the iframe is unchanged
	   return;
	}
   
	// save this new location
	this.currentLocation = hash;
	
	this.ieAtomicLocationChange = false;
	
	// notify listeners of the change
	this._evt.data = hash;
	this._eventMgr.notifyListeners(AjxEvent.HISTORY, this._evt);
};

/**
 * Gets the current location of the hidden IFrames
 * that is stored as history. For Internet Explorer.
 */
AjxHistoryMgr.prototype._getIFrameHash =
function() {
	// get the new location
	var historyFrame = document.getElementById(AjxHistoryMgr.IFRAME_ID);
	var doc = historyFrame.contentWindow.document;
	var hash = new String(doc.location.search);
	
	if (hash.length == 1 && hash.charAt(0) == "?") {
		hash = "";
	} else if (hash.length >= 2 && hash.charAt(0) == "?") {
		hash = hash.substring(1);
	}
    
	return hash;
};
   
/**
 * Removes any leading hash that might be on a location.
 */
AjxHistoryMgr.prototype._removeHash =
function(hashValue) {
	if (hashValue == null || hashValue == undefined) {
	   return null;
	} else if (hashValue == "") {
	   return "";
	} else if (hashValue.length == 1 && hashValue.charAt(0) == "#") {
	   return "";
	} else if (hashValue.length > 1 && hashValue.charAt(0) == "#") {
	   return hashValue.substring(1);
	} else {
	   return hashValue;
	}
};
   
/**
 * For IE, says when the hidden iframe has finished loading.
 */
AjxHistoryMgr.prototype.iframeLoaded =
function(newLocation) {

	// ignore any location changes that we made ourselves
	if (this.ignoreLocationChange) {
	   this.ignoreLocationChange = false;
	   return;
	}
	
	// get the new location
	var hash = new String(newLocation.search);
	if (hash.length == 1 && hash.charAt(0) == "?") {
		hash = "";
	} else if (hash.length >= 2 && hash.charAt(0) == "?") {
		hash = hash.substring(1);
	}
	
	// move to this location in the browser location bar
	window.location.hash = hash;
	
	// notify listeners of the change
	this._evt.data = hash;
	this._eventMgr.notifyListeners(AjxEvent.HISTORY, this._evt);
};
