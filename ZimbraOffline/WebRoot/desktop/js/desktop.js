var zd = {

	byId : function (id) {
		if (typeof id == "string") {
			var it = document.getElementById(id);
			if (!it && window.console) console.log("Couldn't find element "+id);
			return it;
		} else {
			return id;
		}
	},

	set : function(id, value) {
		var it = zd.byId(id);
		if (it) {
			if (it.tagName == "INPUT") {
				if (it.type == "checkbox" || it.type == "radio") {
					it.checked = (value == true || value == "true");
				} else {
					it.value = value;
				}
			} else if (it.tagName == "SELECT") {
			} else {
				it.innerHTML = value;
			}
		}
	},
	
	isValueEqual : function(id, value) {
	   var it = zd.byId(id);
	   if (it.value == value)
	       return true;
	   return false;
	},

	setHash : function(settings) {
		for (var prop in settings) {
			zd.set(prop, settings[prop]);
		}	
	},
	
	show : function(id) {
		var it = zd.byId(id), display = "block";
		if (this.isGecko()) {
			if      (it.tagName == "TD") display = "table-cell";
			else if (it.tagName == "TR") display = "table-row";
		}
		it.style.display = display;
	},

	
	showList : function(list) {
		for (var i = 0; i < list.length; i++) {
			zd.show(list[i]);
		}
	},
	
	hide : function(id) {
		zd.byId(id).style.display = "none";	
	},
	
	hideList : function(list) {
		for (var i = 0; i < list.length; i++) {
			zd.hide(list[i]);
		}
	},
	
	isShown : function(id) {
		var it = zd.byId(id);
		return (it.style.display == 'none');
	},
	
	toggle : function(id, show) {
		if (typeof show == "undefined")	show = zd.isShown(id);
		if (show) 	this.show(id);
		else 		this.hide(id);
	},

	togglePlatformNotice : function(id) {
	    id = id + (zd.isMac() ? "-Mac" : "-isWin");
	    zd.toggle(id);
	},
	
	enable : function(id) {
		var it = this.byId(id);
		it.disabled = false;
	},
	
	disable : function(id) {
		var it = this.byId(id);
		it.disabled = true;
	},
	
	isDisabled : function(id) {
	    var it = zd.byId(id);
        return it.disabled;
	},
	
	isChecked : function(id) {
	    var it = zd.byId(id);
        return it.checked;
	},
	

	_syncTimers : {},
	
	syncIdsOnTimer : function(field, id1, id2, etc) {
		var args = Array.prototype.slice.call(arguments, 0);

		var id = field.id;
		if (this._syncTimers[id]) return;
		
		function sync() {
			args[0] = field.value;
			zd.syncIds.apply(Ajax, args);
			delete zd._syncTimers[id];
		}
		
		this._syncTimers[id] = setTimeout(sync, 20);
		return true;
	},

	syncIds : function(newValue, id1, id2, etc) {
		var itemsToChange = {};
		for (var i = 1; i < arguments.length; i++) {
			var id = arguments[i];
			var it = zd.byId(id);
			if (it.getAttribute("changed") != "true") {
				itemsToChange[id] = newValue;
			}
		}
		zd.setHash(itemsToChange);
	},
	
	markElementAsManuallyChanged : function(element) {
		element.setAttribute("changed", "true");
		return false;
	},
	
	focusIn : function(fieldId) {
		try {
			var field = zd.byId(fieldId);
			field.focus();
		} catch (e) {}
	},
	

	isGecko : function() {
		return (navigator.userAgent.indexOf("Gecko") > -1);
	},
	
	isIE : function() {
		return (navigator.userAgent.indexOf("MSIE") > -1 && !window.opera);
	},
	
	isMac : function() {
		return (navigator.userAgent.indexOf("Macintosh") > -1);
	},
	
	isWin : function() {
		return !zd.isMac();
	},
	
	
	// debug stuff -- strip JSP tags out of HTML so we can display it
	stripJSPTagReplacementRE : /<%=([\s\S]*?)%>/gm,
	stripJSPTagRE 			 : /<%[\s\S]*?%>/gm,
	stripJSPTags : function (text) {
		text = text.replace(zd.stripJSPTagReplacementRE, "$1");
		text = text.replace(zd.stripJSPTagRE, "");
		return text;
	},


	// dead simple XmlHttpRequest code
	XHR : {
		_getXHRObject : function() {
			methods = [
				function(){return new XMLHttpRequest()}, 					// all except IE
				function(){return new ActiveXObject('Msxml2.XMLHTTP')},		// different versions of IE
				function(){return new ActiveXObject('Microsoft.XMLHTTP')},
				function(){return new ActiveXObject('Msxml2.XMLHTTP.4.0')}
			];
			for (var i = 0, xhrMethod; xhrMethod = methods[i++];) {
				try {
					var xhr = xhrMethod();
					// It worked! Replace the "get" function with the correct one and return the XHR.
					zd.XHR.getXHRObject = xhrMethod;
					return xhr;
				} catch (e) {}
			}
			throw new Error("zd._getXHRObject: Could not get XHR object for this browser");
		},
	
		// synchronous load of a file
		getFile : function(url, asXML, data) {
			var xhr = this._getXHRObject();
			xhr.open("GET", url, false);
			xhr.send(data);
			return asXML ? xhr.responseXML : xhr.responseText;
		}
	},

	
	//	Panel API
	//
	//	"Panels" are discrete screens, of which only one will be shown at a given time
	//	when you show a panel, any panel that was previously shown will be hidden automatically.
	//	Thus you only have to worry about what you want to display, not where you're coming from.
	//
	_currentPanel : null,
	showPanel : function(name) {
		if (this._currentPanel) this.hide(this._currentPanel);
		this.show(name);
		this._currentPanel = name;
	},
	
	hidePanel : function(name) {
		this.hide(name);
		this._currentPanel = null;
	},
	
	
	// minimal cookie API
	getCookie : function(name) {
		var cookies = document.cookie.split("; ");
		for (var i = 0; i < cookies.length; i++) {
			var cookie = cookies[i].split("=");
			if (cookie[0] == name) return unescape(cookie[1]);
		}
		return null;
	},
	
	setCookie : function(name, value) {
		document.cookie = (name + "=" + escape(value));
	},
	
	clearCookie : function(name) {
		this.setCookie(name,"");
	}
	
}