/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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
 * @author Andy Clark
 * 
 * @private
 */
AjxTemplate.compile = function(pkg, authoritative, define, templateText) {
	var name = AjxPackage.__package2path(pkg);
	var lines = templateText != null ? templateText : AjxLoader.load(name).reponseText;
	var buffer = [], offset = 0, first = true;

	AjxTemplate.__RE_TEMPLATE.lastIndex = 0;
	var m = AjxTemplate.__RE_TEMPLATE.exec(lines);
	if (m) {
		do {
			var attrs = AjxTemplate.__parseAttrs(m[1]);
			var body = m[2];
			if (attrs["xml:space"] != "preserve") {
                // bug 47973: IE doesn't support String.prototype.trim
                // NOTE: This was caused when the Java TemplateCompiler class was ported.
                body = body.replace(AjxTemplate.__RE_GT_LINESEP_LT, "><").replace(/^\s+|\s+$/,"");
			}

			var packageId = pkg;
			var templateId = attrs.id;
			// NOTE: Template ids can be specified absolutely (i.e.
			//       overriding the default package) if the id starts
			//       with a forward slash (/), or if the id contains
			//       a hash mark (#). This allows a template file to
			//       override both types of template files (i.e. a
			//       single template per file or multiple templates
			//       per file).
			if (templateId && (templateId.indexOf('#') != -1 || templateId.match(/^\//))) {
				if (templateId.indexOf('#') == -1) {
					templateId += "#";
				}
				packageId = templateId.replace(/#.*$/, "").replace(/^\//,"").replace(/\//g,'.');
				templateId = templateId.replace(/^.*#/, "");
			}
			var id = templateId ? packageId+"#"+templateId : packageId;

			var func = AjxTemplate.__convertLines(body);
			AjxTemplate.register(id, func, attrs, authoritative);

			if (first && define) {
				AjxPackage.define(packageId);
			}
			if (first) {
				first = false;
				AjxTemplate.register(packageId, func, attrs, authoritative);
			}
		} while (m = AjxTemplate.__RE_TEMPLATE.exec(lines));
	}
	else {
		if (define) {
			AjxPackage.define(pkg);
		}
		var func = AjxTemplate.__convertLines(lines);
		AjxTemplate.register(pkg, func, {}, authoritative);
	}
};

// template compilation utility

AjxTemplate.__RE_REPLACE = new RegExp([ "\\$\\{(.+?)\\}", "<\\$=(.+?)\\$>", "<\\$(.+?)\\$>" ].join("|"), "mg");
AjxTemplate.__RE_TEMPLATE = new RegExp("<template(.*?)>(.*?)</template>", "mg");
AjxTemplate.__RE_ATTR = new RegExp("\\s*(\\S+)\\s*=\\s*('[^']*'|\"[^\"]*\")", "mg");
AjxTemplate.__RE_PARAM_PART = new RegExp("([^\\(\\.]+)(\\(.*?\\))?\\.?", "g");
AjxTemplate.__RE_GT_LINESEP_LT = new RegExp([">", "\\s*\\n+\\s*", "<"].join(""), "mg");

AjxTemplate.__convertLines = function(lines) {
	var buffer = [], offset = 0;

	buffer[offset++] = "\tvar _hasBuffer = Boolean(buffer);";
	buffer[offset++] = "\tdata = (typeof data == \"string\" ? { id: data } : data) || {};";
	buffer[offset++] = "\tbuffer = buffer || [];";
	buffer[offset++] = "\tvar _i = buffer.length;";
	buffer[offset++] = "\n";

	AjxTemplate.__RE_REPLACE.lastIndex = 0;
	var m = AjxTemplate.__RE_REPLACE.exec(lines);
	if (m) {
		var head = 0;
		do {
			var tail = AjxTemplate.__RE_REPLACE.lastIndex - m[0].length;
			if (head < tail) {
				AjxTemplate.__printStringLines(buffer, lines.substring(head, tail));
			}
			var param = m[1];
			var inline = m[2];
			if (param) {
				offset = AjxTemplate.__printDataLine(buffer, param);
			}
			else if (inline) {
				offset = AjxTemplate.__printBufferLine(buffer, inline);
			}
			else {
				offset = AjxTemplate.__printLine(buffer, "\t", m[3].replace(/\n/g, "\n\t"), "\n");
			}
			head = AjxTemplate.__RE_REPLACE.lastIndex;
		} while (m = AjxTemplate.__RE_REPLACE.exec(lines));
		if (head < lines.length) {
			offset = AjxTemplate.__printStringLines(buffer, lines.substring(head));
		}
	}
	else {
		offset = AjxTemplate.__printStringLines(buffer, lines);
	}
	buffer[offset++] = "\n";

	buffer[offset++] = "\treturn _hasBuffer ? buffer.length : buffer.join(\"\");";

	return new Function("name,params,data,buffer",buffer.join(""));
};

AjxTemplate.__parseAttrs = function(s) {
	var attrs = {}, m;
	AjxTemplate.__RE_ATTR.lastIndex = 0;
	while (m = AjxTemplate.__RE_ATTR.exec(s)) {
		var value = m[2];
		attrs[m[1]] = value.substring(1, value.length - 1);
	}
	return attrs;
};

AjxTemplate.__printLine = function(buffer, s1 /* ..., sN */) {
	var offset = buffer.length;
	for (var i = 1; i < arguments.length; i++) {
		buffer[offset++] = arguments[i];
	}
	return offset;
};

AjxTemplate.__printStringLines = function(buffer, s1 /* ..., sN */) {
	var offset = buffer.length;
	for (var j = 1; j < arguments.length; j++) {
		var s = arguments[j];
		var lines = s.split("\n");
		for (var i = 0; i < lines.length; i++) {
			var line = lines[i];
			offset = AjxTemplate.__printStringLine(buffer, line, i < lines.length - 1 ? "\n" : "");
		}
	}
	return offset;
};

AjxTemplate.__printStringLine = function(buffer, s1 /* ..., sN */) {
	var offset = buffer.length;
	buffer[offset++] = "\tbuffer[_i++] = \"";
	for (var i = 1; i < arguments.length; i++) {
		offset = AjxTemplate.__printEscaped(buffer, arguments[i]);
	}
	buffer[offset++] = "\";";
	return offset;
};

AjxTemplate.__printDataLine = function(buffer, s) {
	var offset = buffer.length, m;
	buffer[offset++] = "\tbuffer[_i++] = data";
	AjxTemplate.__RE_PARAM_PART.lastIndex = 0;
	while (m = AjxTemplate.__RE_PARAM_PART.exec(s)) {
		buffer[offset++] = "[\"";
		buffer[offset++] = m[1];
		buffer[offset++] = "\"]";
		if (m[2] != null) {
			buffer[offset++] = m[2];
		}
	}
	buffer[offset++] = ";";
	return offset;
};

AjxTemplate.__printBufferLine = function(buffer, s1 /* ..., sN */) {
	var offset = buffer.length;
	buffer[offset++] = "\tbuffer[_i++] = ";
	for (var i = 1; i < arguments.length; i++) {
		buffer[offset++] = arguments[i];
	}
	buffer[offset++] = ";";
	return offset;
};

AjxTemplate.__printEscaped = function(buffer, s) {
	var offset = buffer.length;
	buffer[offset++] = s.replace(/\\/g,"\\\\").replace(/"/g,"\\\"").replace('\n',"\\n").replace('\r',"\\r").replace('\t',"\\t");
	return offset;
};
