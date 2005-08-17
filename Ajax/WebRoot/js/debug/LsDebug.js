/**
* Creates a new debug window. The document inside is not kept open.  All the output goes into a single &lt;div&gt; element.
* @constructor
* @class
* This class pops up a debug window and provides functions to send output there in various ways. The output is continuously
* appended to the bottom of the window. The document is left unopened so that the browser doesn't think it's continuously loading
* and keep its little icon flailing forever. Also, the DOM tree can't be manipulated on an open document. All the output is added
* to the window by appending it the DOM tree. Another method of appending output is to open the document and use document.write(),
* but then the document is left open.
* <p>
* Any client that uses this class can turn off debugging by changing the first argument to the constructor to LsDebug.NONE.</p>
*
* @author Conrad Damon
* @author Enrique Del Campo
* @param level	 	debug level for the current debugger (no window will be displayed for a level of NONE)
* @param name 		the name of the window. Defaults to "debug_" prepended to the calling window's URL.
* @param showTime	a boolean that toggles the display of timestamps before debug messages
*/
function LsDebug(level, name, showTime) {
	this._dbgName = "LsDebugWin_" + location.hostname.replace(/\./g,'_');
	this._level = level;
	this._showTime = showTime;
	this._enabled = (this._level != LsDebug.NONE);
	this._showTiming = false;
	this._startTimePt = 0;
	this._lastTimePt = 0;

	this._msgQueue = new Array();
	this._debugBoxId = LsDebug.DEBUG_BOX_ID;
	this._isPrevWinOpen = false;
	this._useDiv = false;
	if (!this._enabled) return;

	this._openContainer();
}


LsDebug.NONE = "DBG0"; // no debugging (window will not come up)
LsDebug.DBG1 = "DBG1"; // minimal debugging
LsDebug.DBG2 = "DBG2"; // moderate debugging
LsDebug.DBG3 = "DBG3"; // anything goes

// map from number to debug level
LsDebug.DBG = new Object();
LsDebug.DBG[0] = LsDebug.NONE;
LsDebug.DBG[1] = LsDebug.DBG1;
LsDebug.DBG[2] = LsDebug.DBG2;
LsDebug.DBG[3] = LsDebug.DBG3;

LsDebug.MAX_OUT = 25000; // max length capable of outputting

LsDebug.DEBUG_BOX_ID = "LsDebugBox";

LsDebug.prototype.toString = 
function() {
	return "LsDebug";
}

/**
* Set debug level. May open or close the debug window if moving to or from level NONE.
*
* @param level	 	debug level for the current debugger
*/
LsDebug.prototype.setDebugLevel = 
function(level) {
	if (level == this._level) return;

	this._level = level;
	if (level == LsDebug.NONE) {
		this._enabled = false;
		this._debugWindow.close();
		this._debugWindow = null;
	} else {
		this._enabled = true;
		if (this._debugWindow == null || this._debugWindow.closed) {
			this._openContainer();
		}
	}
}

LsDebug.prototype.setUseDiv =
function (useDiv){
	this._useDiv = useDiv;
	LsDebug.deleteWindowCookie();
	if (useDiv && this._debugWindow && !this._debugWindow.closed) {
		this._debugWindow.close();
	}
	this._enabled = true;
	this._openContainer();
};

LsDebug.prototype._getWindowName = 
function () {
	return this._dbgName;
};
/**
* Turn the display of timing statements on/off. Timing starts over any time it's turned on.
*
* @param on		whether to display timing statements
*/
LsDebug.prototype.showTiming = 
function(on, msg) {
	if (on)
		this._startTimePt = this._lastTimePt = 0;
	this._showTiming = on;
	if (on && msg)
		this.println(" ----- " + msg + " ----- ");
	this._startTimePt = this._lastTimePt = new Date().getTime();
}

/**
* Prints a debug message. Any HTML will be rendered, and a line break is added.
*
* @param level	 	debug level for the current debugger
* @param msg		the text to display
*/
LsDebug.prototype.println = 
function(level, msg) {
	if (this.isDisabled()) return;
	var args = this._handleArgs(arguments);
	if (!args) return;
	//msg = args[0];
	msg = args.join("");
	this._add(this._timestamp() + msg + "<br>");
};

LsDebug.prototype.isDisabled = 
function () {
	if (!this._useDiv){
		if (!this._enabled){
			return true;
		};
	} else {
		if (this._divContainer && this._divContainer.style.display == 'none'){
			return true;
		}
	}
};

LsDebug.prototype._getHtmlForObject = 
function (anObj, isXml, isRaw) {
	var html = new Array();
	var idx = 0;

	if (LsUtil.isUndefined(anObj)) {
		html[idx++] = "<span>Undefined</span>";
	} else if (LsUtil.isNull(anObj)) {
		html[idx++] = "<span>NULL</span>";
	} else if (LsUtil.isBoolean(anObj)) {
		html[idx++] = "<span>" + anObj + "</span>";
	} else if (LsUtil.isNumber(anObj)) {
		html[idx++] = "<span>" + anObj +"</span>";
	} else {
		if (isRaw) {
			html[idx++] = this._timestamp();
			html[idx++] = "<textarea rows='25' style='width:100%' readonly='true'>";
			html[idx++] = anObj;
			html[idx++] = "</textarea>";
			html[idx++] = "<p></p>";
		} else if (isXml) {
			var xmldoc = new LsDebugXmlDocument;
			var doc = xmldoc.create();
			doc.loadXML(anObj);
			html[idx++] = "<div style='border-width:2px; border-style:inset; width:100%; height:300px; overflow:auto'>";
			html[idx++] = this._createXmlTree(doc, 0);
			html[idx++] = "</div>";
		} else {
			html[idx++] = "<div style='border-width:2px; border-style:inset; width:100%; height:300px; overflow:auto'>";
			html[idx++] = "<pre>";
			html[idx++] = this._dump(anObj, true);
			html[idx++] = "</div>";
			html[idx++] = "</pre>";
		}
		html[idx++] = "<p>";
		html[idx++] = "<a href=\"javascript:;\" onclick='document.getElementById(\"" + this._debugBoxId + "\").innerHTML=\"\"'>Clear Debug Window</a>";
		html[idx++] = "</p>";

	}
	return html.join("");
};

// Pretty-prints a Javascript object
LsDebug.prototype._dump =
function(obj, recurse) {

	var indentLevel = 0;
	var showBraces = false;
	var stopRecursion = false;
	if (arguments.length > 2) {
		indentLevel = arguments[2];
		showBraces = arguments[3];
		stopRecursion = arguments[4];
	}

	if (LsUtil.isObject(obj)) {
		if (obj.toString() == "LmAppCtxt"){
			return "[LmAppCtxt]";
		}
		if (LsDebug._visited.contains(obj))
			return "[visited object]";
		else
			LsDebug._visited.add(obj);
	}	

	var indent = LsStringUtil.repeat(" ", indentLevel);
	var text = "";
	
	if (LsUtil.isUndefined(obj)) {
		text += "[undefined]";
	} else if (LsUtil.isNull(obj)) {
		text += "[null]";
	} else if (LsUtil.isBoolean(obj)) {
		text += obj ? "true" : "false";
	} else if (LsUtil.isString(obj)) {
		obj = obj.replace(/\r/g, "\\r");
		obj = obj.replace(/\n/g, "\\n");
		obj = obj.replace(/\t/g, "\\t");
		text += '"' + LsDebug._escapeForHTML(obj) + '"';
	} else if (LsUtil.isNumber(obj)) {
		text += obj;
	} else if (LsUtil.isObject(obj)) {
		var isArray = LsUtil.isArray(obj);
		if (stopRecursion) {
			text += isArray ? "[Array]" : obj.toString();
		} else {
			stopRecursion = !recurse;
			var keys = new Array();
			for (var i in obj)
				keys.push(i);

			isArray ? keys.sort(function(a,b) {return a - b;}) : keys.sort();	
	

			if (showBraces)
				text += isArray ? "[" : "{";
			for (var i = 0; i < keys.length; i++) {
				var key = keys[i];
				var nextObj = obj[key];
				var value = null;
				// 5/31/05 EMC:
				// For dumping events, and dom elements, though I may not want to
				// traverse the node, I do want to know what the attribute is.
				if (nextObj == window || nextObj == document || (!LsEnv.isIE && nextObj instanceof Node)){
					value = nextObj.toString();
				} 
				if ((typeof(nextObj) == "function")){
					if (this._showFuncs) {
						value = "[function]";
					} else {
						continue;
					}
				}

				if (i > 0) text += ",";
				text += "\n" + indent;
				if (value != null) {
					text += key + ": " + value;
				} else {
					text += key + ": " + this._dump(nextObj, recurse, indentLevel + 2, true, stopRecursion);
				}
			}
			if (i > 0)
				text += "\n" + LsStringUtil.repeat(" ", indentLevel - 1);
			if (showBraces)
				text += isArray ? "]" : "}";
		}
	}
	return text;
}

/**
* Prints an object into a table, with a column for properties and a column for values. Above the table is a header with the object
* class and the CSS class (if any). The properties are sorted (numerically if they're all numbers). Creating and appending table 
* elements worked in Mozilla but not IE. Using the insert* methods works for both. Properties that are function 
* definitions are skipped.
*
* @param level	 	debug level for the current debugger
* @param obj		the object to be printed
* @param showFuncs	whether to show props that are functions
*/
LsDebug.prototype.dumpObj = 
function(level, obj, showFuncs) {
	if (this.isDisabled())return;
	var args = this._handleArgs(arguments);
	if (!args) return;
	obj = args[0];
	if (!obj) return;
	this._showFuncs = args[1];

	LsDebug._visited = new LsVector();
	this._add(null, obj);
	this._showFuncs = null;
	
}

/**
* Dumps a bunch of text into a &lt;textarea&gt;, so that it is wrapped and scrollable. HTML will not be rendered.
*
* @param level	 	debug level for the current debugger
* @param text		the text to output as is
*/
LsDebug.prototype.printRaw = 
function(level, text) {
	if (this.isDisabled()) return;
	var args = this._handleArgs(arguments);
	if (!args) return;
	text = args[0];
	
	this._add(null,text, false, true);
}

/**
* Pretty-prints a chunk of XML, doing color highlighting for different types of nodes.

* @param level	 	debug level for the current debugger
* @param text		some XML
*/
LsDebug.prototype.printXML = 
function(level, text) {
	if (this.isDisabled()) return;
	var args = this._handleArgs(arguments);
	if (!args) return;
	text = args[0];
	if (!text) return;
	
	// skip generating pretty xml if theres too much data
	if (LsEnv.isSafari || text.length > LsDebug.MAX_OUT) {
		this.printRaw(text);
		return;
	}
	this._add(null, text, true, false);
}

/**
* Reveals white space in text by replacing it with tags.
*
* @param level	 	debug level for the current debugger
* @param text		the text to be displayed
*/
LsDebug.prototype.display =
function(level, text) {
	if (this.isDisabled()) return;
	var args = this._handleArgs(arguments);
	if (!args) return;
	text = args[0];

	text = text.replace(/\r?\n/g, '[crlf]');
	text = text.replace(/ /g, '[space]');
	text = text.replace(/\t/g, '[tab]');
	this.printRaw(level, text);
}

LsDebug.prototype.timePt =
function(msg) {
	if (!this._showTiming || !this._enabled || this._debugWindow.closed) return;
	
	var now = new Date().getTime();
	var elapsed = now - this._startTimePt;
	var interval = now - this._lastTimePt;
	this._lastTimePt = now;
	var text = "[" + elapsed + " / " + interval + "]";
	if (msg)
		text += " " + msg;
	html = "<div>" + text + "</div>";
	extraType = typeof(text);

    var myMsg = new DebugMessage(html);
	
    // Add the message to our stack
    this._addMessage(myMsg);
	return interval;
}

// If the first arg is a debug level, check it and then strip it.
LsDebug.prototype._handleArgs =
function(args) {
	if (this._level == LsDebug.NONE) return;
	
	var num1 = 0;
	var first = args[0];
	if (typeof first == "string" && first.indexOf("DBG") == 0) {
		num1 = Number(first.charAt(first.length - 1));
		var num2 = Number(this._level.charAt(this._level.length - 1));
		if (num1 > num2) return null;
	}

	var a = new Array(args.length);
	for (var i = 0; i < args.length; i++)
		a[i] = args[i];
	if (num1)
		a.shift();

	return a;
}

LsDebug.prototype._getCookieVal =
function (cookieName) {
	var myRE = cookieName  + "=([^;]+)";
	var myVals = document.cookie.match(new RegExp(myRE));
	var val = null;
	// Return the last value defined (if found)
	if (myVals && (myVals.length > 0)) {
		var valStr = myVals[myVals.length-1];
		if (valStr == "true") {
			val = true;
		} else if (valStr == "false") {
			val = false;
		} else {
			val = valStr;
		}
	}
	return val;
};

LsDebug.prototype._openContainer =
function () {
	this._enabled = true;
	if (!this._useDiv){ 
		this._openDebugWindow();
	} else {
		this._openDebugDiv();
	}
};

LsDebug.prototype._openDebugDiv = 
function() {
	this._initDiv();
};

LsDebug.prototype._openDebugWindow =
function() {
	// check if there is a debug window already open
	if (!this._useDiv){
		this._isPrevWinOpen = this._getCookieVal("LsDebugWinOpen");
		var winName = this._getWindowName();
		if (!this._isPrevWinOpen) {
			this._debugWindow = 
				LsWindowOpener.openBlank(
						winName, 
						"width=400,height=400,resizable=yes,scrollbars=yes", 
						this._initWindow, this);
		} else {
			this._debugWindow = 
			   window.open("" , winName,
					   "width=400,height=400,resizable=yes,scrollbars=yes");
			this._initWindow();
		}
	}
};

LsDebug.prototype._initDiv = 
function() {
	this._document = document;
	var container = this._divContainer = this._document.createElement("div");
	container.id = "LsDebugDivContainer";
	container.style.height = "300px";
	container.style.width = "300px";
	container.style.display = "block";
	container.style.position = "absolute";
	container.style.top = "0px";
	container.style.left = "0px";
	container.style.zIndex = 10000;
	container.style.backgroundColor = "white";
	var div = this._document.createElement("div");
	div.style.height="20px";
	div.style.width="100%";
	div.innerHTML = "<a href='javascript:;' onclick='LsDebug.closeDiv(this)'>Close</a>";
	container.appendChild(div);
	this._document.body.appendChild(container);

	this._debugBox = this._document.createElement("div");
	this._debugBox.style.overflow = "auto";
	this._debugBox.style.height = "98%";
	this._debugBox.style.width = "100%";
	this._debugBox.id = this._debugBoxId;
	container.appendChild(this._debugBox);
		
	LsDebug._divBuffer = document.createElement('div');
		
	this._showMessages();

};

LsDebug.closeDiv = function (anchor) {
	var container = anchor.parentNode.parentNode;
	container.style.display = 'none';
};

LsDebug._openErrors = 0;
LsDebug.prototype._initWindow =
function() {
	if (this._debugWindow == null) {
		this._enabled = false;
		return;
	}
	
	try {
		this._document = this._debugWindow.document;
		this._document.title = "Debug";

		if (!this._isPrevWinOpen) {
			this._document.body.innerHTML = "";
			
			this._debugBox = this._document.createElement("div");
			this._debugBox.id = this._debugBoxId;
			this._document.body.appendChild(this._debugBox);
			
			LsDebug._divBuffer = this._debugWindow.document.createElement('div');
			
			// If we're not using a div
			// Set a cookie telling ourselves that a debug window is already open
			document.cookie = "LsDebugWinOpen=true";
			
			// setup an onunload method
			if (!LsEnv.isIE) {
				this._debugWindow.onunload = LsDebug.unloadHandler;
				window.addEventListener('unload', LsDebug.myWindowUnloadHandler, 
										true);
			} else {
				this._debugWindow.attachEvent('onunload', LsDebug.unloadHandler);
				window.attachEvent = LsDebug.myWindowUnloadHandler;
			}
			
		} else {
			this._debugBox = this._document.getElementById(this._debugBoxId);
			LsDebug._divBuffer = this._debugWindow.document.createElement('div');
			var sepDiv = this._parseHtmlFragment("<div style='width:100%; border:1px solid red;'>Debugging new window</div>");
			this._debugBox.appendChild(sepDiv);
			// Firefox allows us to attach an event listener, and runs it even
			// though the window with the code is gone ... odd, but nice. IE,
			// though will not run the handler, so we make sure, event if we
			// are coming back to the window, to attach the onunload handler.
			if (LsEnv.isIE) {
				this._debugWindow.attachEvent('onunload', LsDebug.unloadHandler);
			}
		}
		// show any messages that have been queued up, while the window
		// loaded.
		this._showMessages();
	} catch (ex) {
		LsDebug.deleteWindowCookie();
		this._debugWindow.close();
		// If we've exceeded a certain number of errors,
		// let's just close the window, and bail.
		if (LsDebug._openErrors < 5) {
			LsDebug._openErrors++;
			this._openContainer();
		}
		return;
	}
}

LsDebug.myWindowUnloadHandler = function () {
	if (LsEnv.isNav) {
		DBG._debugWindow.onunload = null;
	} else {
		DBG._debugWindow.detachEvent('onunload', LsDebug.unloadHandler);
	}
};

LsDebug.unloadHandler = function () {
	try {
		window.LsDebug.deleteWindowCookie();
	} catch (ex) {
		// do nothing. This might be caused by the unload handler
		// firing while the window is changing domains.
	}
};

LsDebug.deleteWindowCookie = function () {
	LsDebug.deleteCookie("LsDebugWinOpen", false);
};

LsDebug.deleteCookie = function (cookieName, val) {
    var expiredDate = new Date('Fri, 31 Dec 1999 23:59:59 GMT');
	document.cookie = cookieName +"=" + val+ ";expires=" + 
	                   expiredDate.toGMTString();
};


LsDebug._nextId = 0;
LsDebug.getNextId =
function () {
	if (window.Dwt){
		return Dwt.getNextId();
	} else {
		return LsDebug._nextId++;
	}
};

/**
* Scrolls to the bottom of the window. How it does that depends on the browser.
*
* @private
*/
LsDebug.prototype._scrollToBottom = 
function() {
	LsEnv.isIE ? this._debugBox.scrollIntoView(false) :
	             this._debugWindow.scrollTo(0, this._document.body.offsetHeight);
}

/**
* Returns a timestamp string, if we are showing them.
* @private
*/
LsDebug.prototype._timestamp = 
function() {
	return this._showTime ? new Date().toLocaleTimeString() + ": " : "";
}

// this function takes an xml node and returns an html string that displays that node
// the indent argument is used to describe what depth the node is at so that
// the html code can create a nice indention
LsDebug.prototype._createXmlTree = 
function (node, indent) {

	if (node == null)
		return "";
	var str = "";
	
	switch (node.nodeType) {
		case 1:	// Element
			str += "<div style='color: blue; padding-left: 16px;'>&lt;<span style='color: DarkRed;'>" + node.nodeName + "</span>";
			
			var attrs = node.attributes;
			for (var i = 0; i < attrs.length; i++)
				str += this._createXmlAttribute(attrs[i]);
			
			if (!node.hasChildNodes())
				return str + "/&gt;</div>";
			
			str += "&gt;<br />";
			
			var cs = node.childNodes;
			for (var i = 0; i < cs.length; i++)
				str += this._createXmlTree(cs[i], indent + 3);
			
			str += "&lt;/<span style='color: DarkRed;'>" + node.nodeName + "</span>&gt;</div>";
			break;
	
		case 9:	// Document
			var cs = node.childNodes;
			for (var i = 0; i < cs.length; i++)
				str += this._createXmlTree(cs[i], indent);
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
			for (var i = 0; i < attrs.length; i++)
				str += this._createXmlAttribute(attrs[i]);
			
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
			//alert(node.nodeType + "\n" + node.nodeValue);
			this._inspect(node);
	}
	
	return str;
}

LsDebug.prototype._createXmlAttribute = 
function(a) {
	return " <span style='color: red'>" + a.nodeName + "</span><span style='color: blue'>=\"" + a.nodeValue + "\"</span>";
}

LsDebug.prototype._inspect = 
function(obj) {
	var str = "";
	for (var k in obj)
		str += "obj." + k + " = " + obj[k] + "\n";
	window.alert(str);
}

LsDebug.prototype._add = 
function (aMsg, extraInfo, isXml, isRaw){
	var extraType = typeof(extraInfo);
	if (LsUtil.isSpecified(extraInfo)) {
		extraInfo = this._getHtmlForObject(extraInfo, isXml, isRaw);
    }

    var myMsg = new DebugMessage(aMsg, null, null, 
								 null, extraInfo);
	
    // Add the message to our stack
    this._addMessage(myMsg);

};

LsDebug.prototype._addMessage = 
function (aMsg) {
	this._msgQueue[this._msgQueue.length] = aMsg;

	this._showMessages();
};

LsDebug.buf = new Array();

LsDebug.prototype._showMessages = 
function (retryNum) {
	if (!this._document) {
		// For now, don't show the messages-- assuming that
		// this case only happens at startup, and many 
		// messages will be written
		return;
	}
	var i = 0;
	var buf = LsDebug.buf;
	buf.length = 0;
	var idx = 0;
	var msg;
	for (i ; i < this._msgQueue.length ; ++i ) {
		msg = this._msgQueue[i];
		buf[idx++] = "<div>";
		buf[idx++] = msg.message;
		buf[idx++] = msg.eHtml;
		buf[idx++] = "</div>";
	}
	this._msgQueue.length = 0;
	if (buf.length > 0){
		var div = this._parseHtmlFragment(buf.join(""));
		if (!this.debugBox)
			this._debugBox = this._document.getElementById(this._debugBoxId);
		if (this._debugBox)		
			this._debugBox.appendChild(div);
	}
	this._scrollToBottom();
};

LsDebug._escapeForHTML = function(str){
	if (typeof(str) != 'string') return str;
	var s = str;
	s = s.replace(/\&/g, '&amp;');
	s = s.replace(/\</g, '&lt;');
	s = s.replace(/\>/g, '&gt;');
	s = s.replace(/\"/g, '&quot;');
	s = s.replace(/\xA0/g, '&nbsp;');	
	return s;
};

LsDebug.prototype._parseHtmlFragment = function (htmlStr, tagName) {
	var html = htmlStr;
	if (tagName && tagName == "TR"){
		html = "<table style='table-layout:fixed'>" + htmlStr + "</table>";
	}
	var div = LsDebug._divBuffer;
	div.innerHTML = html;
	if (tagName && tagName == "TR"){
		return div.firstChild.rows[0];
	} else {
		return div.firstChild;
	}
};


/**
 * Simple wrapper for log messages
 */
DebugMessage = function(aMsg, aType, aCategory, aTime, extraHtml) {
    this.message = (LsUtil.isSpecified(aMsg)) ? aMsg : '';
    this.type = aType ? aType : null;
    this.category = aCategory ? aCategory : '';
    this.time = aTime ? aTime : (new Date().getTime());
    this.eHtml = extraHtml;
};

