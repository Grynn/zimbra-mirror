/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * 
 * @private
 */
AjxLeakDetector = function() {
	this._controls = [];
	this._closures = {}; // Map of id to { closure, args }
	this._closureReport = []; // Report that is created during dispose event, not actually reported till later.
	this._nextId = 1;
	this._addHooks();
};

/**
 * Executes a command. This is intended to be run by the client special search handler.
 * 
 * @param {string}	command 		"begin", "end", or "report"
 * @return {hash}	an object with 3 attributes: success, message, and details
 */
AjxLeakDetector.execute =
function(command) {
	var result = {
		success: false,
		message: "",
		details: ""
	};
	if (command == "begin") {
		result.success = AjxLeakDetector.begin();
		result.message = result.success ? "Leak detector started." : "Leak detector already started.";
	} else if (command == "end") {
		result.success = AjxLeakDetector.end();
		result.message = result.success ? "Leak detector stopped." : "Leak detector is not running.";
	} else if (command == "report" || command == "dispose") {
		if (command == "dispose") {
			var shell = DwtShell.getShell(window);
			shell.dispose(true);
			document.title = "Shell has been disposed";
		}
		var report = [];
		result.success = AjxLeakDetector.report(report);
		if (report.length) {
			DBG.println("Leak detector report.....");
			DBG.printRaw(report.join(""));
		}
		if (result.success) {
			result.message = report.length ? "Problems found. See debug window for details." : "No problems found";
		} else {
			result.message = "Leak detector is not running.";
		}
	} else {
		result.success = false;
		result.message = "Invalid argument, use (begin/end/report)";
	}
	return result;
};

AjxLeakDetector.begin =
function() {
	if (!AjxLeakDetector._instance) {
		AjxLeakDetector._instance = new AjxLeakDetector();
		return true;
	} else {
		return false;
	}
};

AjxLeakDetector.end =
function() {
	if (AjxLeakDetector._instance) {
		AjxLeakDetector._instance._removeHooks();
		AjxLeakDetector._instance = null;
		return true;
	} else {
		return false;
	}
};

AjxLeakDetector.report =
function(report) {
	if (AjxLeakDetector._instance) {
		AjxLeakDetector._instance._createReport(report);
		return true;
	} else {
		return false;
	}
};

AjxLeakDetector.prototype._addHooks =
function() {
	var self = this;

	// Hook into __initCtrl
	var oldInit = DwtControl.prototype.__initCtrl;
	DwtControl.prototype.__initCtrl = function() {
		self._controls.push(this);
		oldInit.call(this);
	};

	// Hook into dispose.
	var oldDispose = DwtControl.prototype.dispose;
	DwtControl.prototype.dispose = function() {
		var element = document.getElementById(this.getHTMLElId());
		oldDispose.call(this);
		self._postDisposeCheck(this, element);
	};

	// Hook into simple closure
	var oldClosure = AjxCallback.simpleClosure;
	AjxCallback.simpleClosure = function(func, obj) {
		var result = oldClosure.apply(null, arguments);
		result.__leakDetectorId = self._nextId++;
		var args = [];
		for (var i = 0, count = arguments.length; i < count; i++) {
			args[i] = arguments[i];
		}
		self._closures[result.__leakDetectorId] = {
			closure: result,
			args: args
		};
		return result;
	};

	// Create method for undoing this one.
	this._removeHooks = function() {
		DwtControl.prototype.__initCtrl = oldInit;
		DwtControl.prototype.dispose = oldDispose;
		AjxCallback.simpleClosure = oldClosure;
	};
};

AjxLeakDetector.prototype._createReport =
function(report) {
	for (var i = 0, count = this._controls.length; i < count; i++) {
		var control = this._controls[i];

		// If the control believes it is still in play, make sure the html element is too.
		if (!control._disposed) {
			var element = document.getElementById(control.getHTMLElId());
			if (!element) {
				this._log(report, "Detached html element", control);
			}
		}
		// If the control has been disposed, make sure it doesn't directly reference any html elements.
		else {
			var elementNames = null;
			for (var name in control) {
				var value = control[name];
				if (value && value.tagName) { // I'm using tagName!=null to detect that it's an element.
					elementNames = elementNames || [];
					elementNames.push(name);
				}
			}
			if (elementNames) {
				this._log(report, "Elements referenced by control: " + elementNames.join(", "), control);
			}
		}
	}
	for (var i = 0, count = this._closureReport.length; i < count; i++) {
		report.push(this._closureReport[i]);
	}
	if (report.length) {
		return report.join("");
	} else {
		return "Leak detector: no problems detected"; 
	}
};

AjxLeakDetector.prototype._log =
function(report, message, control, element) {
	report.push(message);
	report.push("\n ");
	var path = [control];
	while (control.parent) {
		path.push(control.parent);
		control = control.parent;
	}
	for (var i = path.length -  1; i >= 0; i--) {
		report.push(path[i].toString());
		if (i > 0) {
			report.push("->");
		}
	}
	this._logAttrs(report, path[0]);
	report.push("\n------------------------\n");
};

AjxLeakDetector.prototype._logAttrs =
function(report, control) {
	var attrMap = {
		"DwtLabel" : ["__text", "__imageInfo"]
	};

	var didIt = false;
	for (var className in attrMap) {
		if (Dwt.instanceOf(control, className)) {
			if (!didIt) {
				report.push("{\n");
			}
			var attrs = attrMap[className];
			for (var i = 0, count = attrs.length; i < count; i++) {
				report.push(" ");
				report.push(attrs[i]);
				report.push(": ")
				report.push(control[attrs[i]]);
				report.push("\n")
			}
			didIt = true;
		}
	}
	if (didIt) {
		report.push("}\n");
	}
};

AjxLeakDetector.prototype._postDisposeCheck =
function(control, element) {
	var report = [];
	if (!element) {
		this._log(this._closureReport, "Very bad: control's element not in DOM: " + report.join(""), control);
	} else {
		this._postDisposeElementCheck(report, element);
		if (report.length) {
			this._log(this._closureReport, "Suspicioius closure args in control: " + report.join(""), control);
		}
	}
};
AjxLeakDetector.prototype._postDisposeElementCheck =
function(report, element) {
	// Go thru all the element's properties looking for values that are simple closures.
	var handlers = null;
	for (var name in element) {
		var argNames = null;
		var value;
		try {
			value = element[name];
		} catch (e) {
			// Certain properties aren't readable in ff, probably harmless, but report it...	
			DBG.println("AjxLeakDetector: error accessing property: " + name);
		}
		if (value && value.__leakDetectorId) {
			var data = this._closures[value.__leakDetectorId];
			if (data) {
				// Loop over the args that were passed to the closure...
				var args = data.args;
				for (var i = 0, count = args.length; i < count; i++) {
					var arg = args[i];
					if (arg instanceof DwtControl) {
						argNames = argNames || [];
						argNames.push(arg.toString());
					} else if (arg.tagName) { // I'm using tagName!=null to detect that it's an element.
						argNames = argNames || [];
						argNames.push(arg.tagName);
					}
				}
			}
		}
		if (argNames) {
			handlers = handlers || [];
			handlers.push("  ");
			handlers.push(name);
			handlers.push("(");
			handlers.push(argNames.join(","));
			handlers.push(")\n");
		}
	}
	if (handlers) {
		report.push("The element ");
		report.push(element.tagName);
		report.push("#");
		report.push(element.id || "noId");
		report.push("has the following handlers that may cause cirular references: \n");
		report.push(handlers.join(""));
	}
	var children = element.childNodes;
	for (var i = 0, count = children.length; i < count; i++) {
		 this._postDisposeElementCheck(report, children[i]);
	}
};

