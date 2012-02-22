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
 * Creates an iframe.
 * @class
 * This class represents a simple event proxy. Creates an IFRAME, inserts the given html into it and
 * forwards any events to the parent widget, translating mouse coordinates in
 * between.
 *
 * @param {hash}	params		a hash of parameters
 * @param	{DwtComposite}		params.parent		the parent 
 * @param {string}	params.html 	the HTML code to be inserted in the IFRAME.  There will be
 *   slight modifications to it (i.e. the margins and paddings of the HTML
 *   element will be set to 0, also any margins for BODY).
 * @param {boolean}		[params.noscroll=false] 	if <code>true</code>, do not show the scroll bars
 * @param {constant}	params.posStyle		the position style (see {@link DwtControl})
 * @param {AjxCallback}	params.processHtmlCallback		the callback that will be called
 *   immediately after the HTML code was inserted. A reference to the document object will be passed
 * @param {boolean}	params.useKbMgmt	if <code>true</code>, participate in keyboard management
 * 
 * @author Mihai Bazon
 * 
 * @extends		DwtControl
 */
DwtIframe = function(params) {
	params.posStyle = params.posStyle || DwtControl.STATIC_STYLE;
	params.className = params.className || "DwtIframe";
	DwtControl.call(this, params);
	this._styles = params.styles;
	this._noscroll = params.noscroll;
	this._iframeID = params.id ? DwtId.getIframeId(params.id) : Dwt.getNextId();
	this._onLoadHandler = params.onload;	
	this._processHtmlCallback = params.processHtmlCallback;
	this._hidden = params.hidden;
	if (!this._createFrame(params.html)) {
		this.initFailed = true;
		return;	// this object is still returned
	}
	
	if (params.useKbMgmt) {
		var iframe = this.getIframe();
		var idoc = Dwt.getIframeDoc(iframe);
		var doc = AjxEnv.isIE ? idoc : iframe.contentWindow;
		Dwt.setHandler(doc, DwtEvent.ONKEYDOWN, DwtKeyboardMgr.__keyDownHdlr);
		Dwt.setHandler(doc, DwtEvent.ONKEYUP, DwtKeyboardMgr.__keyUpHdlr);
		Dwt.setHandler(doc, DwtEvent.ONKEYPRESS, DwtKeyboardMgr.__keyPressHdlr);
	}
};

DwtIframe.prototype = new DwtControl;
DwtIframe.prototype.constructor = DwtIframe;

DwtIframe.prototype.isDwtIframe = true;
DwtIframe.prototype.toString = function() { return "DwtIframe"; };

/**
 * Gets the iframe.
 * 
 * @return	{Element}	the iframe
 */
DwtIframe.prototype.getIframe = function() {
	return document.getElementById(this._iframeID);
};

/**
 * Gets the iframe window document.
 * 
 * @return	{Document}		the document
 */
DwtIframe.prototype.getDocument = function() {
	return this.getIframe().contentWindow.document;
};

/// Forwards events to the parent widget
DwtIframe.prototype._rawEventHandler = function(ev) {
	var iframe = this.getIframe();
	var win = iframe.contentWindow;
	if (AjxEnv.isIE) {
		ev = win.event;
	}

	var dw;
	// This probably sucks.
	if (/mouse|context|click|select/i.test(ev.type)) {
		dw = new DwtMouseEvent(true);
	}
	else {
		dw = new DwtUiEvent(true);
	}
	dw.setFromDhtmlEvent(ev);

	// Notify since the manager doesn't know about events in the iframe's document
	if (ev.type == "mousedown" || ev.type == "mousewheel") {
		DwtOutsideMouseEventMgr.forwardEvent(ev);
	}

	// HACK! who would have know.. :-(
	// perhaps we need a proper mapping
	var type = dw.type.toLowerCase();
	if (!/^on/.test(type)) {
		type = "on" + type;
	}
	// translate event coordinates
	var pos = this.getLocation();

	// What I can tell for sure is that we don't want the code below for IE
	// and we want it for Gecko, but I can't be sure of other browsers..
	// Let's assume they follow Gecko.  Seems mostly a trial and error
	// process :(
	if (!AjxEnv.isIE) {
		var doc = win.document;
		var sl = doc.documentElement.scrollLeft || ( doc.body ? doc.body.scrollLeft : 0); 
		var st = doc.documentElement.scrollTop || ( doc.body ? doc.body.scrollTop : 0 );
		pos.x -= sl;
		pos.y -= st;
	}

	dw.docX += pos.x;
	dw.docY += pos.y;
	dw.elementX += pos.x;
	dw.elementY += pos.y;

//   	window.status = dw.type + " doc(" + dw.docX + ", " + dw.docY + ") " +
//   		" element(" + dw.elementX + ", " + dw.elementY + ") " +
//  		" stopPropagation: " + dw._stopPropagation + ", " +
//  		" returnValue: " + dw._returnValue;

	var capture = DwtMouseEventCapture.getCaptureObj();
	capture = capture && dw.button != DwtMouseEvent.RIGHT; // ignore capture if it's right-click
	if (AjxEnv.isIE || (AjxEnv.isSafari && !AjxEnv.isChrome) || !capture) {
		// go for Dwt events
		DwtEventManager.notifyListeners(type, dw);
		this.parent.notifyListeners(type, dw);
	} else {
		// Satisfy object that holds the mouse capture.

		// the following is DOM2, not supported by IE
		var fake = document.createEvent("MouseEvents");
		fake.initMouseEvent(ev.type,
				    true, // can bubble
				    true, // cancellable
				    document.defaultView, // the view
				    0, // event detail ("click count")
				    ev.screenX, // screen X
				    ev.screenY, // screen Y
				    dw.docX, // clientX, but translated to page
				    dw.docY, // clientY, translated
				    ev.ctrlKey, // key status...
				    ev.altKey,
				    ev.shiftKey,
				    ev.metaKey,
				    ev.button,
				    ev.relatedTarget);
		document.body.dispatchEvent(fake);
		// capture[DwtIframe._captureEvents[dw.type]](fake);
	}

	dw.setToDhtmlEvent(ev);
	return dw._returnValue;
};

// map event names to the handler name in a DwtMouseEventCapture object
// DwtIframe._captureEvents = { mousedown : "_mouseDownHdlr",
// 			     mousemove : "_mouseMoveHdlr",
// 			     mouseout  : "_mouseOutHdlr",
// 			     mouseover : "_mouseOverHdlr",
// 			     mouseup   : "_mouseUpHdlr" };

DwtIframe._forwardEvents = [ DwtEvent.ONCHANGE,
			     DwtEvent.ONCLICK,
			     DwtEvent.ONCONTEXTMENU,
			     DwtEvent.ONDBLCLICK,
			     DwtEvent.ONFOCUS,
			     DwtEvent.ONKEYDOWN,
			     DwtEvent.ONKEYPRESS,
			     DwtEvent.ONKEYUP,
			     DwtEvent.ONMOUSEDOWN,
			     DwtEvent.ONMOUSEENTER,
			     DwtEvent.ONMOUSELEAVE,
			     DwtEvent.ONMOUSEMOVE,
			     DwtEvent.ONMOUSEOUT,
			     DwtEvent.ONMOUSEOVER,
			     DwtEvent.ONMOUSEUP,
			     DwtEvent.ONSELECTSTART ];

DwtIframe.prototype._createFrame = function(html) {
	var myId = this.getHTMLElId();

	// this is an inner function so that we can access the object (self).
	// it shouldn't create a memory leak since it doesn't directly "see"
	// the iframe variable (it's protected below)
	function rawHandlerProxy(ev) {
		var myElement = document.getElementById(myId);
		var self = DwtControl.findControl(myElement);		
		return self._rawEventHandler(ev);
	};

	// closure: protect the reference to the iframe node here.
	return (function() {
		var iframe, tmp = [], i = 0, idoc;
		var myElement = document.getElementById(myId);
		var self = DwtControl.findControl(myElement);

		if (!self) {
			return false;
		}
		
		tmp[i++] = "<iframe";
		if (self._noscroll) {
			tmp[i++] = " scrolling='no'";
		}
		if (self._hidden) {
			tmp[i++] = " style='visibility:hidden'";
		}
		tmp[i++] = " frameborder='0' width='100%' id='";
		tmp[i++] = self._iframeID;
		tmp[i++] = "' name='"+ self._iframeID + "'";
		if(self._onLoadHandler){
			tmp[i++] = " onload='" + self._onLoadHandler + "'";
		}
		tmp[i++] = " src='javascript:\"\";' ></iframe>";
		self.setContent(tmp.join(''));

		// Bug 7523: @import url() lines will make Gecko report
		// document.body is undefined until "onload" (unacceptable) so
		// we drop these lines now.
		html = html.replace(/(<style[^>]*>)[\s\t\u00A0]*((.|\n)*?)[\s\t\u00A0]*<\x2fstyle>/mgi,
				    function(s, p1, p2) {
					    return p1 + p2.replace(/@import.*?(;|[\s\t\u00A0]*$)/gi, "") + "</style>";
				    });

		iframe = self.getIframe();
		idoc = Dwt.getIframeDoc(iframe);
		idoc.open();
		if (self._styles) {
			idoc.write([ "<style type='text/css'>", self._styles, "</style>" ].join(""));
		}
		idoc.write(html);
		idoc.close();
		// if we're not giving a break, we can safely do any postprocessing
		// here.  I.e. if we want to drop backgroundImage-s, it's safe to do it
		// here because the browser won't have a chance to load them.
		if (self._processHtmlCallback) {
			self._processHtmlCallback.run(idoc);
		}

		// if we have margins, the translated coordinates won't be OK.
		// it's best to remove them.  THE way to have some spacing is
		// to set padding on the body element.
		tmp = idoc.documentElement.style;
		tmp.margin = tmp.padding = "0";
		if (idoc.body) {
			idoc.body.style.margin = "0";
		}

		// assign event handlers
		tmp = DwtIframe._forwardEvents;
		if (!AjxEnv.isIE) {
			idoc = iframe.contentWindow;
		}
		for (i = tmp.length; --i >= 0;) {
			idoc[tmp[i]] = rawHandlerProxy;
		}

		// catch browser context menus
		// idoc[DwtEvent.ONCONTEXTMENU] = DwtShell._preventDefaultPrt;
		
		return true;
	})();
};

DwtIframe.prototype._resetEventHandlers = function() {
	var self = this;

	// this is an inner function so that we can access the object (self).
	// it shouldn't create a memory leak since it doesn't directly "see"
	// the iframe variable (it's protected below)
	function rawHandlerProxy(ev) { return self._rawEventHandler(ev); };

	// closure: protect the reference to the iframe node here.
	(function() {
		var iframe, tmp = [], i = 0, idoc;
		iframe = self.getIframe();
		idoc = Dwt.getIframeDoc(iframe);

		// assign event handlers
		tmp = DwtIframe._forwardEvents;
		if (!AjxEnv.isIE)
			idoc = iframe.contentWindow;
			
			
		for (i = tmp.length; --i >= 0;){
			idoc[tmp[i]] = rawHandlerProxy;
		}

		// catch browser context menus
		// idoc[DwtEvent.ONCONTEXTMENU] = DwtShell._preventDefaultPrt;
	})();
};

DwtIframe.prototype.setSrc =
function(src){

    src = src || 'javascript:\"\";'
    var iframe = this.getIframe();
    iframe.src = src;
};

DwtIframe.prototype.setIframeContent =
function(html){    
    var iDoc = this.getDocument();
    iDoc.body.innerHTML = html;
};
