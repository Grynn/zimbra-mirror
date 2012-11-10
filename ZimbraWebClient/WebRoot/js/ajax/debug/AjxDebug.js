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
 * Creates a new debug window. The document inside is not kept open.  All the
 * output goes into a single &lt;div&gt; element.
 * @constructor
 * @class
 * This class pops up a debug window and provides functions to send output there
 * in various ways. The output is continuously appended to the bottom of the
 * window. The document is left unopened so that the browser doesn't think it's
 * continuously loading and keep its little icon flailing forever. Also, the DOM
 * tree can't be manipulated on an open document. All the output is added to the
 * window by appending it the DOM tree. Another method of appending output is to
 * open the document and use document.write(), but then the document is left open.
 * <p>
 * Any client that uses this class can turn off debugging by changing the first
 * argument to the constructor to {@link AjxDebug.NONE}.
 *
 * @author Conrad Damon
 * @author Ross Dargahi
 *
 * @param {constant}	level	 	debug level for the current debugger (no window will be displayed for a level of NONE)
 * @param {string}		name 		the name of the window (deprecated)
 * @param {boolean}		showTime	if <code>true</code>, display timestamps before debug messages
 * @param {constant}	target		output target (AjxDebug.TGT_WINDOW | AjxDebug.TGT_CONSOLE)
 *
 * @private
 */
AjxDebug = function(params) {

	if (arguments.length == 0) {
		params = {};
	}
	else if (typeof arguments[0] == "number") {
		params = {level:arguments[0], name:arguments[1], showTime:arguments[2]};
	}

	this._showTime = params.showTime;
	this._target = params.target || AjxDebug.TGT_WINDOW;
	this._showTiming = false;
	this._startTimePt = this._lastTimePt = 0;
	this._dbgWindowInited = false;

	this._msgQueue = [];
	this._isPrevWinOpen = false;
	this.setDebugLevel(params.level);
};

/**
 * Defines "no debugging" level.
 */
AjxDebug.NONE = 0; // no debugging (window will not come up)
/**
 * Defines "minimal" debugging level.
 */
AjxDebug.DBG1 = 1; // minimal debugging
/**
 * Defines "moderate" debugging level.
 */
AjxDebug.DBG2 = 2; // moderate debugging
/**
 * Defines "all" debugging level.
 */
AjxDebug.DBG3 = 3; // anything goes

// log output targets
AjxDebug.TGT_WINDOW		= "window";
AjxDebug.TGT_CONSOLE	= "console";

// holds log output in memory so we can show it to user if requested; hash of arrays by type
AjxDebug.BUFFER		= {};
AjxDebug.BUFFER_MAX	= {};

// Special log types. These can be used to make high-priority log info available in prod mode.
// To turn off logging for a type, set its BUFFER_MAX to 0.
AjxDebug.DEFAULT_TYPE	= "debug";		// regular DBG messages
AjxDebug.RPC			= "rpc";		// for troubleshooting "Out of RPC cache" errors
AjxDebug.NOTIFY			= "notify";		// for troubleshooting missing new mail
AjxDebug.EXCEPTION		= "exception";	// JS errors
AjxDebug.CALENDAR		= "calendar";	// for troubleshooting calendar errors
AjxDebug.REPLY			= "reply";		// bug 56308
AjxDebug.SCROLL			= "scroll"; 	// bug 55775
AjxDebug.BAD_JSON		= "bad_json"; 	// bug 57066
AjxDebug.PREFS			= "prefs";		// bug 60942
AjxDebug.PROGRESS       = "progress";	// progress dialog
AjxDebug.REMINDER       = "reminder";   // bug 60692
AjxDebug.TAG_ICON       = "tagIcon";    // bug 62155
AjxDebug.DATA_URI       = "dataUri";    // bug 64693
AjxDebug.MSG_DISPLAY	= "msgDisplay";	// bugs 68599, 69616

AjxDebug.BUFFER_MAX[AjxDebug.DEFAULT_TYPE]	= 0;	// this one can get big due to object dumps
AjxDebug.BUFFER_MAX[AjxDebug.RPC]			= 200;
AjxDebug.BUFFER_MAX[AjxDebug.NOTIFY]		= 400;
AjxDebug.BUFFER_MAX[AjxDebug.EXCEPTION]		= 100;
AjxDebug.BUFFER_MAX[AjxDebug.CALENDAR]		= 400;
AjxDebug.BUFFER_MAX[AjxDebug.REPLY]			= 400;
AjxDebug.BUFFER_MAX[AjxDebug.SCROLL]		= 100;
AjxDebug.BUFFER_MAX[AjxDebug.BAD_JSON]		= 200;
AjxDebug.BUFFER_MAX[AjxDebug.PREFS] 		= 200;
AjxDebug.BUFFER_MAX[AjxDebug.REMINDER]		= 200;
AjxDebug.BUFFER_MAX[AjxDebug.TAG_ICON]		= 200;
AjxDebug.BUFFER_MAX[AjxDebug.PROGRESS]		= 200;
AjxDebug.BUFFER_MAX[AjxDebug.DATA_URI]		= 200;
AjxDebug.BUFFER_MAX[AjxDebug.MSG_DISPLAY]	= 200;

AjxDebug.MAX_OUT = 25000; // max length capable of outputting an XML msg

AjxDebug._CONTENT_FRAME_ID	= "AjxDebug_CF";
AjxDebug._LINK_FRAME_ID		= "AjxDebug_LF";
AjxDebug._BOTTOM_FRAME_ID	= "AjxDebug_BFI";
AjxDebug._BOTTOM_FRAME_NAME	= "AjxDebug_BFN";

/**
 * Returns a string representation of the object.
 * 
 * @return	{string}		a string representation of the object
 */
AjxDebug.prototype.toString =
function() {
	return "AjxDebug";
};

AjxDebug.prototype.setTitle =
function(title) {
	if (this._document && !AjxEnv.isIE) {
		this._document.title = title;
	}
};

/**
 * Set debug level. May open or close the debug window if moving to or from level {@link AjxDebug.NONE}.
 *
 * @param {constant}	level	 	debug level for the current debugger
 */
AjxDebug.prototype.setDebugLevel =
function(level) {

	this._level = parseInt(level) || level;
	this._enable(this._level != AjxDebug.NONE);
};

/**
 * Gets the current debug level.
 * 
 * @return	{constant}	the debug level
 */
AjxDebug.prototype.getDebugLevel =
function() {
	return this._level;
};

/**
 * Prints a debug message. Any HTML will be rendered, and a line break is added.
 *
 * @param {constant}	level	 	debug level for the current debugger
 * @param {string}	msg		the text to display
 */
AjxDebug.prototype.println =
function(level, msg, linkName) {
	
	if (!this._isWriteable()) { return; }

	try {
		var result = this._handleArgs(arguments);
		if (!result) { return; }

		msg = result.args.join("");
		var eol = (this._target != AjxDebug.TGT_CONSOLE) ? "<br>" : "";
		this._add({msg:this._timestamp() + msg + eol, linkName:result.linkName, level:level});
	} catch (ex) {
		// do nothing
	}
};

/**
 * Checks if debugging is disabled.
 * 
 * @return	{boolean}		<code>true</code> if disabled
 */
AjxDebug.prototype.isDisabled =
function () {
	return !this._enabled;
};

/**
 * Prints an object into a table, with a column for properties and a column for values. Above the table is a header with the object
 * class and the CSS class (if any). The properties are sorted (numerically if they're all numbers). Creating and appending table
 * elements worked in Mozilla but not IE. Using the insert* methods works for both. Properties that are function
 * definitions are skipped.
 *
 * @param {constant}	level	 	debug level for the current debugger
 * @param {object}	obj		the object to be printed
 * @param {boolean}	showFuncs		if <code>true</code>, show props that are functions
 */
AjxDebug.prototype.dumpObj =
function(level, obj, showFuncs, linkName) {
	if (!this._isWriteable()) { return; }

	var result = this._handleArgs(arguments);
	if (!result) { return; }

	obj = result.args[0];
	if (!obj) { return; }

	showFuncs = result.args[1];
	this._add({obj:obj, linkName:result.linkName, showFuncs:showFuncs, level:level});
};

/**
 * Dumps a bunch of text into a &lt;textarea&gt;, so that it is wrapped and scrollable. HTML will not be rendered.
 *
 * @param {constant}	level	 	debug level for the current debugger
 * @param {string}	text		the text to output as is
 */
AjxDebug.prototype.printRaw =
function(level, text, linkName) {
	if (!this._isWriteable()) { return; }

	var result = this._handleArgs(arguments);
	if (!result) { return; }

	this._add({obj:result.args[0], isRaw:true, linkName:result.linkName, level:level});
};

/**
 * Pretty-prints a chunk of XML, doing color highlighting for different types of nodes.
 *
 * @param {constant}	level	 	debug level for the current debugger
 * @param {string}	text		some XML
 * 
 * TODO: fix for printing to console
 */
AjxDebug.prototype.printXML =
function(level, text, linkName) {
	if (!this._isWriteable()) { return; }

	var result = this._handleArgs(arguments);
	if (!result) { return; }

	text = result.args[0];
	if (!text) { return; }

	// skip generating pretty xml if theres too much data
	if (text.length > AjxDebug.MAX_OUT) {
		this.printRaw(text);
		return;
	}
	this._add({obj:text, isXml:true, linkName:result.linkName, level:level});
};

/**
 * Reveals white space in text by replacing it with tags.
 *
 * @param {constant}	level	 	debug level for the current debugger
 * @param {string}	text		the text to be displayed
 */
AjxDebug.prototype.display =
function(level, text, linkName) {
	if (!this._isWriteable()) { return; }

	var result = this._handleArgs(arguments);
	if (!result) { return; }

	text = result.args[0];
	text = text.replace(/\r?\n/g, '[crlf]');
	text = text.replace(/ /g, '[space]');
	text = text.replace(/\t/g, '[tab]');
	this.printRaw(level, text, linkName);
};

/**
 * Turn the display of timing statements on/off.
 *
 * @param {boolean}	on			if <code>true</code>, display timing statements
 * @param {string}	msg		the message to show when timing is turned on
 */
AjxDebug.prototype.showTiming =
function(on, msg) {
	this._showTiming = on;
	if (on) {
		this._enable(true);
	}
	var state = on ? "on" : "off";
	var text = "Turning timing info " + state;
	if (msg) {
		text = text + ": " + msg;
	}

	var debugMsg = new DebugMessage({msg:text});
	this._addMessage(debugMsg);
	this._startTimePt = this._lastTimePt = new Date().getTime();
};

/**
 * Displays time elapsed since last time point.
 *
 * @param {string}	msg		the text to display with timing info
 * @param {boolean}	restart	if <code>true</code>, set timer back to zero
 */
AjxDebug.prototype.timePt =
function(msg, restart) {
	if (!this._showTiming || !this._isWriteable()) { return; }

	if (restart) {
		this._startTimePt = this._lastTimePt = new Date().getTime();
	}
	var now = new Date().getTime();
	var elapsed = now - this._startTimePt;
	var interval = now - this._lastTimePt;
	this._lastTimePt = now;

	var spacer = restart ? "<br/>" : "";
	msg = msg ? " " + msg : "";
	var text = [spacer, "[", elapsed, " / ", interval, "]", msg].join("");
	var html = "<div>" + text + "</div>";

	// Add the message to our stack
	this._addMessage(new DebugMessage({msg:html}));
	return interval;
};

AjxDebug.prototype.getContentFrame =
function() {
	if (this._contentFrame) {
		return this._contentFrame;
	}
	if (this._debugWindow && this._debugWindow.document) {
		return this._debugWindow.document.getElementById(AjxDebug._CONTENT_FRAME_ID);
	}
	return null;
};

AjxDebug.prototype.getLinkFrame =
function(noOpen) {
	if (this._linkFrame) {
		return this._linkFrame;
	}
	if (this._debugWindow && this._debugWindow.document) {
		return this._debugWindow.document.getElementById(AjxDebug._LINK_FRAME_ID);
	}
	if (!noOpen) {
		this._openDebugWindow();
		return this.getLinkFrame(true);
	}
	return null;
};

// Private methods

AjxDebug.prototype._enable =
function(enabled) {

	this._enabled = enabled;
	if (this._target == AjxDebug.TGT_WINDOW) {
		if (enabled) {
			if (!this._dbgName) {
				this._dbgName = "AjxDebugWin_" + location.hostname.replace(/\./g,'_');
			}
			if (this._debugWindow == null || this._debugWindow.closed) {
				this._openDebugWindow();
			}
		} else {
			if (this._debugWindow) {
				this._debugWindow.close();
				this._debugWindow = null;
			}
		}
	}
};

AjxDebug.prototype._isWriteable =
function() {
	if (this.isDisabled()) {
		return false;
	}
	if (this._target == AjxDebug.TGT_WINDOW) {
		return (!this._isPaused && this._debugWindow && !this._debugWindow.closed);
	}
	return true;
};

AjxDebug.prototype._getHtmlForObject =
function(obj, params) {

	params = params || {};
	var html = [];
	var idx = 0;

	if (obj === undefined) {
		html[idx++] = "<span>Undefined</span>";
	} else if (obj === null) {
		html[idx++] = "<span>NULL</span>";
	} else if (AjxUtil.isBoolean(obj)) {
		html[idx++] = "<span>" + obj + "</span>";
	} else if (AjxUtil.isNumber(obj)) {
		html[idx++] = "<span>" + obj +"</span>";
	} else {
		if (params.isRaw) {
			html[idx++] = this._timestamp();
			html[idx++] = "<textarea rows='25' style='width:100%' readonly='true'>";
			html[idx++] = obj;
			html[idx++] = "</textarea><p></p>";
		} else if (params.isXml) {
			var xmldoc = new AjxDebugXmlDocument;
			var doc = xmldoc.create();
			// IE bizarrely throws error if we use doc.loadXML here (bug 40451)
			if (doc && ("loadXML" in doc)) {
				doc.loadXML(obj);
				html[idx++] = "<div style='border-width:2px; border-style:inset; width:100%; height:300px; overflow:auto'>";
				html[idx++] = this._createXmlTree(doc, 0, {"authToken":true});
				html[idx++] = "</div>";
			} else {
				html[idx++] = "<span>Unable to create XmlDocument to show XML</span>";
			}
		} else {
			html[idx++] = "<div style='border-width:2px; border-style:inset; width:100%; height:300px; overflow:auto'><pre>";
			html[idx++] = this._dump(obj, true, params.showFuncs, {"ZmAppCtxt":true, "authToken":true});
			html[idx++] = "</div></pre>";
		}
	}
	return html.join("");
};

// Pretty-prints a Javascript object
AjxDebug.prototype._dump =
function(obj, recurse, showFuncs, omit) {

	return AjxStringUtil.prettyPrint(obj, recurse, showFuncs, omit);
};

/**
 * Marshals args to public debug functions. In general, the debug level is an optional
 * first arg. If the first arg is a debug level, check it and then strip it from the args.
 * The last argument is an optional name for the link from the left panel.
 *
 * Returns an object with the link name and a list of the arguments (other than level and
 * link name).
 *
 * @param {array}	args				an arguments list
 *
 * @private
 */
AjxDebug.prototype._handleArgs =
function(args) {

	// don't output anything if debugging is off, or timing is on
	if (this._level == AjxDebug.NONE || this._showTiming || args.length == 0) { return; }

	// convert args to a true Array so they're easier to deal with
	var argsArray = new Array(args.length);
	for (var i = 0; i < args.length; i++) {
		argsArray[i] = args[i];
	}

	var result = {args:null, linkName:null};

	// remove link name from arg list if present - check if last arg is *Request or *Response
	var origLen = argsArray.length;
	if (argsArray.length > 1) {
		var lastArg = argsArray[argsArray.length - 1];
		if (lastArg && lastArg.indexOf && (lastArg.indexOf("DebugWarn") != -1 || ((lastArg.indexOf(" ") == -1) && (/Request|Response$/.test(lastArg))))) {
			result.linkName = lastArg;
			argsArray.pop();
		}
	}

	// check level if provided, strip it from args; level is either a number, or 1-8 lowercase letters/numbers
	var userLevel = null;
	var firstArg = argsArray[0];
	var gotUserLevel = (typeof firstArg == "number" || ((origLen > 1) && firstArg.length <= 8 && /^[a-z0-9]+$/.test(firstArg)));
	if (gotUserLevel) {
		userLevel = firstArg;
		argsArray.shift();
	}
	if (userLevel && (AjxDebug.BUFFER_MAX[userLevel] == null)) {
		if (typeof this._level == "number") {
			if (typeof userLevel != "number" || (userLevel > this._level)) { return; }
		} else {
			if (userLevel != this._level) { return; }
		}
	}
	result.args = argsArray;

	return result;
};

AjxDebug.prototype._openDebugWindow =
function(force) {
	var name = AjxEnv.isIE ? "_blank" : this._dbgName;
	this._debugWindow = window.open("", name, "width=600,height=400,resizable=yes,scrollbars=yes");

	if (this._debugWindow == null) {
		this._enabled = false;
		return;
	}

	this._enabled = true;
	this._isPrevWinOpen = this._debugWindow.debug;
	this._debugWindow.debug = true;

	try {
		this._document = this._debugWindow.document;
		this.setTitle("Debug");

		if (!this._isPrevWinOpen) {
			this._document.write(
				"<html>",
					"<head>",
						"<script>",
							"function blank() {return [",
								"'<html><head><style type=\"text/css\">',",
									"'P, TD, DIV, SPAN, SELECT, INPUT, TEXTAREA, BUTTON {',",
											"'font-family: Tahoma, Arial, Helvetica, sans-serif;',",
											"'font-size:11px;}',",
									"'.Content {display:block;margin:0.25em 0em;}',",
									"'.Link {cursor: pointer;color:blue;text-decoration:underline;white-space:nowrap;width:100%;}',",
									"'.DebugWarn {color:red;font-weight:bold;}',",
									"'.Run {color:black; background-color:red;width:100%;font-size:18px;font-weight:bold;}',",
									"'.RunLink {display:block;color:black;background-color:red;font-weight:bold;white-space:nowrap;width:100%;}',",
								"'</style></head><body></body></html>'].join(\"\");}",
						"</script>",
					"</head>",
					"<frameset cols='125, *'>",
						"<frameset rows='*,40'>",
							"<frame name='", AjxDebug._LINK_FRAME_ID, "' id='", AjxDebug._LINK_FRAME_ID, "' src='javascript:parent.parent.blank();'>",
							"<frame name='", AjxDebug._BOTTOM_FRAME_NAME, "' id='", AjxDebug._BOTTOM_FRAME_ID, "' src='javascript:parent.parent.blank();' scrolling=no frameborder=0>",
						"</frameset>",
						"<frame name='", AjxDebug._CONTENT_FRAME_ID, "' id='", AjxDebug._CONTENT_FRAME_ID, "' src='javascript:parent.blank();'>",
					"</frameset>",
				"</html>"
			);
			this._document.close();
			
			var ta = new AjxTimedAction(this, AjxDebug.prototype._finishInitWindow);
			AjxTimedAction.scheduleAction(ta, 2500);
		} else {
			this._finishInitWindow();

			this._contentFrame = this._document.getElementById(AjxDebug._CONTENT_FRAME_ID);
			this._linkFrame = this._document.getElementById(AjxDebug._LINK_FRAME_ID);
			this._createLinkNContent("RunLink", "NEW RUN", "Run", "NEW RUN");

			this._attachHandlers();

			this._dbgWindowInited = true;
			// show any messages that have been queued up, while the window loaded.
			this._showMessages();
		}
	} catch (ex) {
		if (this._debugWindow) {
			this._debugWindow.close();
		}
		this._openDebugWindow(true);
	}
};

AjxDebug.prototype._finishInitWindow =
function() {
	try {
		this._contentFrame = this._debugWindow.document.getElementById(AjxDebug._CONTENT_FRAME_ID);
		this._linkFrame = this._debugWindow.document.getElementById(AjxDebug._LINK_FRAME_ID);

		var frame = this._debugWindow.document.getElementById(AjxDebug._BOTTOM_FRAME_ID);
		var doc = frame.contentWindow.document;
		var html = [];
		var i = 0;
		html[i++] = "<table><tr><td><button id='";
		html[i++] = AjxDebug._BOTTOM_FRAME_ID;
		html[i++] = "_clear'>Clear</button></td><td><button id='";
		html[i++] = AjxDebug._BOTTOM_FRAME_ID;
		html[i++] = "_pause'>Pause</button></td></tr></table>";
		if (doc.body) {
			doc.body.innerHTML = html.join("");
		}
	}
	catch (ex) {
		// IE chokes on the popup window on cold start-up (when IE is started
		// for the first time after system reboot). This should not prevent the
		// app from running and should not bother the user
	}

	if (doc) {
		this._clearBtn = doc.getElementById(AjxDebug._BOTTOM_FRAME_ID + "_clear");
		this._pauseBtn = doc.getElementById(AjxDebug._BOTTOM_FRAME_ID + "_pause");
	}

	this._attachHandlers();
	this._dbgWindowInited = true;
	this._showMessages();
};

AjxDebug.prototype._attachHandlers =
function() {
	// Firefox allows us to attach an event listener, and runs it even though
	// the window with the code is gone ... odd, but nice. IE, though will not
	// run the handler, so we make sure, even if we're  coming back to the
	// window, to attach the onunload handler. In general reattach all handlers
	// for IE
	var unloadHandler = AjxCallback.simpleClosure(this._unloadHandler, this);
	if (AjxEnv.isIE) {
		this._unloadHandler = unloadHandler;
		this._debugWindow.attachEvent('onunload', unloadHandler);
	}
	else {
		this._debugWindow.onunload = unloadHandler;
	}

	if (this._clearBtn) {
		this._clearBtn.onclick = AjxCallback.simpleClosure(this._clear, this);
	}
	if (this._pauseBtn) {
		this._pauseBtn.onclick = AjxCallback.simpleClosure(this._pause, this);
	}
};

/**
 * Scrolls to the bottom of the window. How it does that depends on the browser.
 *
 * @private
 */
AjxDebug.prototype._scrollToBottom =
function() {
	var contentFrame = this.getContentFrame();
	var contentBody = contentFrame ? contentFrame.contentWindow.document.body : null;
	var linkFrame = this.getLinkFrame();
	var linkBody = linkFrame ? linkFrame.contentWindow.document.body : null;

	if (contentBody && linkBody) {
		contentBody.scrollTop = contentBody.scrollHeight;
		linkBody.scrollTop = linkBody.scrollHeight;
	}
};

/**
 * Returns a timestamp string, if we are showing them.
 * @private
 */
AjxDebug.prototype._timestamp =
function() {
	return this._showTime ? this._getTimeStamp() + ": " : "";
};

AjxDebug.prototype.setShowTimestamps =
function(show) {
	this._showTime = show;
};

/**
 * This function takes an XML node and returns an HTML string that displays that node
 * the indent argument is used to describe what depth the node is at so that
 * the HTML code can create a nice indentation.
 * 
 * @private
 */
AjxDebug.prototype._createXmlTree =
function (node, indent, omit) {
	if (node == null) { return ""; }

	var str = "";
	var len;
	switch (node.nodeType) {
		case 1:	// Element
			str += "<div style='color: blue; padding-left: 16px;'>&lt;<span style='color: DarkRed;'>" + node.nodeName + "</span>";

			if (omit && omit[node.nodeName]) {
				return str + "/&gt;</div>";
			}

			var attrs = node.attributes;
			len = attrs.length;
			for (var i = 0; i < len; i++) {
				str += this._createXmlAttribute(attrs[i]);
			}

			if (!node.hasChildNodes()) {
				return str + "/&gt;</div>";
			}
			str += "&gt;<br />";

			var cs = node.childNodes;
			len = cs.length;
			for (var i = 0; i < len; i++) {
				str += this._createXmlTree(cs[i], indent + 3, omit);
			}
			str += "&lt;/<span style='color: DarkRed;'>" + node.nodeName + "</span>&gt;</div>";
			break;

		case 9:	// Document
			var cs = node.childNodes;
			len = cs.length;
			for (var i = 0; i < len; i++) {
				str += this._createXmlTree(cs[i], indent, omit);
			}
			break;

		case 3:	// Text
			if (!/^\s*$/.test(node.nodeValue)) {
				var val = node.nodeValue.replace(/</g, "&lt;").replace(/>/g, "&gt;");
				str += "<span style='color: WindowText; padding-left: 16px;'>" + val + "</span><br />";
			}
			break;

		case 7:	// ProcessInstruction
			str += "&lt;?" + node.nodeName;

			var attrs = node.attributes;
			len = attrs.length;
			for (var i = 0; i < len; i++) {
				str += this._createXmlAttribute(attrs[i]);
			}
			str+= "?&gt;<br />"
			break;

		case 4:	// CDATA
			str = "<div style=''>&lt;![CDATA[<span style='color: WindowText; font-family: \"Courier New\"; white-space: pre; display: block; border-left: 1px solid Gray; padding-left: 16px;'>" +
				  node.nodeValue +
				  "</span>]" + "]></div>";
			break;

		case 8:	// Comment
			str = "<div style='color: blue; padding-left: 16px;'>&lt;!--<span style='white-space: pre; font-family: \"Courier New\"; color: Gray; display: block;'>" +
				  node.nodeValue +
				  "</span>--></div>";
			break;

		case 10:
				str = "<div style='color: blue; padding-left: 16px'>&lt;!DOCTYPE " + node.name;
				if (node.publicId) {
					str += " PUBLIC \"" + node.publicId + "\"";
					if (node.systemId)
						str += " \"" + node.systemId + "\"";
				}
				else if (node.systemId) {
					str += " SYSTEM \"" + node.systemId + "\"";
				}
				str += "&gt;</div>";

				// TODO: Handle custom DOCTYPE declarations (ELEMENT, ATTRIBUTE, ENTITY)
				break;

		default:
			this._inspect(node);
	}

	return str;
};

AjxDebug.prototype._createXmlAttribute =
function(a) {
	return [" <span style='color: red'>", a.nodeName, "</span><span style='color: blue'>=\"", a.nodeValue, "\"</span>"].join("");
};

AjxDebug.prototype._inspect =
function(obj) {
	var str = "";
	for (var k in obj) {
		str += "obj." + k + " = " + obj[k] + "\n";
	}
	window.alert(str);
};

AjxDebug.prototype._add =
function(params) {

	params.extraHtml = params.obj && this._getHtmlForObject(params.obj, params);

	// Add the message to our stack
    this._addMessage(new DebugMessage(params));
};

AjxDebug.prototype._addMessage =
function(msg) {
	this._msgQueue.push(msg);
	this._showMessages();
};

AjxDebug.prototype._showMessages =
function() {

	switch (this._target) {
		case AjxDebug.TGT_WINDOW:
			this._showMessagesInWindow();
			break;
		case AjxDebug.TGT_CONSOLE:
			this._showMessagesInConsole();
	}
	this._addMessagesToBuffer();
	this._msgQueue = [];
};

AjxDebug.prototype._showMessagesInWindow =
function() {

	if (!this._dbgWindowInited) {
		// For now, don't show the messages-- assuming that this case only
		// happens at startup, and many messages will be written
		return;
	}
	try {
		if (this._msgQueue.length > 0) {
			var contentFrame = this.getContentFrame();
			var linkFrame = this.getLinkFrame();
			if (!contentFrame || !linkFrame) { return; }

			var contentFrameDoc = contentFrame.contentWindow.document;
			var linkFrameDoc = linkFrame.contentWindow.document;
			var now = new Date();
			for (var i = 0, len = this._msgQueue.length; i < len; ++i ) {
				var msg = this._msgQueue[i];
				var linkLabel = msg.linkName;
				var contentLabel = [msg.message, msg.extraHtml].join("");
				this._createLinkNContent("Link", linkLabel, "Content", contentLabel, now);
			}
		}

		this._scrollToBottom();
	} catch (ex) {}
};

AjxDebug.prototype._addMessagesToBuffer =
function() {

	var eol = (this._target == AjxDebug.TGT_CONSOLE) ? "<br>" : "";
	for (var i = 0, len = this._msgQueue.length; i < len; ++i ) {
		var msg = this._msgQueue[i];
		AjxDebug._addMessageToBuffer(msg.type, msg.message + msg.extraHtml + eol);
	}
};

AjxDebug._addMessageToBuffer =
function(type, msg) {

	type = type || AjxDebug.DEFAULT_TYPE;
	var max = AjxDebug.BUFFER_MAX[type];
	if (max > 0) {
		var buffer = AjxDebug.BUFFER[type] = AjxDebug.BUFFER[type] || [];
		while (buffer.length >= max) {
			buffer.shift();
		}
		buffer.push(msg);
	}
};

AjxDebug.prototype._showMessagesInConsole =
function() {

	if (!window.console) { return; }

	var now = new Date();
	for (var i = 0, len = this._msgQueue.length; i < len; ++i ) {
		var msg = this._msgQueue[i];
		if (window.console && window.console.log) {
			window.console.log(AjxStringUtil.stripTags(msg.message + msg.extraHtml));
		}
	}
};

AjxDebug.prototype._getTimeStamp =
function(date) {
	if (!AjxDebug._timestampFormatter) {
		AjxDebug._timestampFormatter = new AjxDateFormat("HH:mm:ss.SSS");
	}
	date = date || new Date();
	return AjxStringUtil.htmlEncode(AjxDebug._timestampFormatter.format(date), true);
};

AjxDebug.prototype._createLinkNContent =
function(linkClass, linkLabel, contentClass, contentLabel, now) {

	var linkFrame = this.getLinkFrame();
	if (!linkFrame) { return; }

	now = now || new Date();
	var timeStamp = ["[", this._getTimeStamp(now), "]"].join("");
	var id = "Lnk_" + now.getTime();

	// create link
	if (linkLabel) {
		var linkFrameDoc = linkFrame.contentWindow.document;
		var linkEl = linkFrameDoc.createElement("DIV");
		linkEl.className = linkClass;
		linkEl.innerHTML = [linkLabel, timeStamp].join(" - ");
		linkEl._targetId = id;
		linkEl._dbg = this;
		linkEl.onclick = AjxDebug._linkClicked;

		var linkBody = linkFrameDoc.body;
		linkBody.appendChild(linkEl);
	}

	// create content
	var contentFrameDoc = this.getContentFrame().contentWindow.document;
	var contentEl = contentFrameDoc.createElement("DIV");
	contentEl.className = contentClass;
	contentEl.id = id;
	contentEl.innerHTML = contentLabel;

	contentFrameDoc.body.appendChild(contentEl);

	// always show latest
	this._scrollToBottom();
};

AjxDebug._linkClicked =
function() {
	var contentFrame = this._dbg.getContentFrame();
	var el = contentFrame.contentWindow.document.getElementById(this._targetId);
	var y = 0;
	while (el) {
		y += el.offsetTop;
		el = el.offsetParent;
	}

	contentFrame.contentWindow.scrollTo(0, y);
};

AjxDebug.prototype._clear =
function() {
	this.getContentFrame().contentWindow.document.body.innerHTML = "";
	this.getLinkFrame().contentWindow.document.body.innerHTML = "";
};

AjxDebug.prototype._pause =
function() {
	this._isPaused = !this._isPaused;
	this._pauseBtn.innerHTML = this._isPaused ? "Resume" : "Pause";
};

AjxDebug.prototype._unloadHandler =
function() {
	if (!this._debugWindow) { return; } // is there anything to do?

	// detach event handlers
	if (AjxEnv.isIE) {
		this._debugWindow.detachEvent('onunload', this._unloadHandler);
	} else {
		this._debugWindow.onunload = null;
	}
};

AjxDebug.println =
function(type, msg) {
	AjxDebug._addMessageToBuffer(type, msg + "<br>");
};

AjxDebug.dumpObj =
function(type, obj) {
	AjxDebug._addMessageToBuffer(type, "<pre>" + AjxStringUtil.prettyPrint(obj, true) + "</pre>");
};

/**
 *
 * @param {hash}	params			hash of params:
 * @param {string}	methodNameStr	SOAP method, eg SearchRequest or SearchResponse
 * @param {boolean}	asyncMode		true if request made asynchronously
 */
AjxDebug.logSoapMessage =
function(params) {

	if (params.methodNameStr == "NoOpRequest" || params.methodNameStr == "NoOpResponse") { return; }

	var ts = AjxDebug._getTimeStamp();
	var msg = ["<b>", params.methodNameStr, params.asyncMode ? "" : " (SYNCHRONOUS)" , " - ", ts, "</b>"].join("");
	for (var type in AjxDebug.BUFFER) {
		if (type == AjxDebug.DEFAULT_TYPE) { continue; }
		AjxDebug.println(type, msg);
	}
	if (window.DBG) {
        // Link is written here:
        var linkName = params.methodNameStr;
        if (!params.asyncMode) {
            linkName = "<span class='DebugWarn'>SYNCHRONOUS </span>" + linkName;
        }
        window.DBG.println(window.DBG._level, msg, linkName);
	}
};

AjxDebug._getTimeStamp =
function(date) {
	return AjxDebug.prototype._getTimeStamp.apply(null, arguments);
};

AjxDebug.getDebugLog =
function(type) {

	type = type || AjxDebug.DEFAULT_TYPE;
	var buffer = AjxDebug.BUFFER[type];
	return buffer ? buffer.join("") : "";
};

/**
 * Simple wrapper for log messages.
 * @private
 */
DebugMessage = function(params) {

	params = params || {};
	this.message = params.msg || "";
	this.type = params.type || null;
	this.category = params.category || "";
	this.time = params.time || (new Date().getTime());
	this.extraHtml = params.extraHtml || "";
	this.linkName = params.linkName;
	this.type = (params.level && typeof(params.level) == "string") ? params.level : AjxDebug.DEFAULT_TYPE;
};
