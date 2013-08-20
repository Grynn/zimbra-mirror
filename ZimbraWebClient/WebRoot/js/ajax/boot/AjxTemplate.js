/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
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
 * Default constructor.
 * @constructor
 * @class
 * This class contains utility functions for using templates.
 * 
 * @author Andy Clark
 */
AjxTemplate = function() {};

//
// Data
//

AjxTemplate._templates = {};
AjxTemplate._stack = [];

//
// Public functions
//

/**
 * Sets the base path.
 * 
 * @param	{string}	basePath		the base path
 */
AjxTemplate.setBasePath = function(basePath) {
    AjxTemplate._basePath = basePath;
};
/**
 * Sets the extension.
 * 
 * @param	{string}	extension		the extension
 */
AjxTemplate.setExtension = function(extension) {
    AjxTemplate._extension = extension;
};

AjxTemplate.register = function(name, func, params, authoritative) {
    if (!authoritative && AjxTemplate._templates[name] &&
        AjxTemplate._templates[name].authoritative) {
        return;
    }
    AjxTemplate._templates[name] = {
        name: name, func: func, params: params || {}, authoritative: authoritative 
    };
};

AjxTemplate.getTemplate = function(name) {
    var template = AjxTemplate._templates[name];
    return template && template.func;
};

AjxTemplate.getParams = function(name) {
    var template = AjxTemplate._templates[name];
    return template && template.params;
};

/**
 * Expands the template.
 * 
 * @param	{string}		name		the template name
 * @param	{array}			[data]		the template date
 * @param	{array}			[buffer]	the buffer to use for template content
 * @return	{string}	the template content		
 */
AjxTemplate.expand = function(name, data, buffer) {
	// allow template text to come from document
	if (!AjxTemplate._templates[name] && AjxTemplate.compile) {
		var el = document.getElementById(name);
		if (el) {
			// NOTE: In all major browsers (IE, FF, Saf) the value property
			//       of the textarea will be the literal text of the content.
			//       Using the innerHTML will escape the HTML content which
			//       is not desirable.
			var isTextArea = el.nodeName.toUpperCase() == "TEXTAREA";
			AjxTemplate.compile(name, true, true, isTextArea ? el.value : el.innerHTML);
		}
	}

    var pkg = AjxTemplate.__name2Package(name);
    var id = name.replace(/^[^#]*#?/, "");
    if (id) {
        name = [pkg, id].join("#");
    }

    AjxTemplate.require(pkg);

    var hasBuffer = Boolean(buffer);
    buffer = buffer || [];
    var func = AjxTemplate.getTemplate(name);
    if (func) {
        try {
            AjxTemplate._stack.push(pkg);
            var params = AjxTemplate.getParams(name);
            func(name, params, data, buffer);
	    }
        catch (e) {
	    	buffer.push(this.__formatError(name, e));
	    }
        finally {
            AjxTemplate._stack.pop();
        }
    } else {
    	buffer.push(this.__formatError(name, "template not found"));
    }

    return hasBuffer ? buffer.length : buffer.join("");
};

/**
 * Force load of template.
 * 
 * @return <code>true</code> if the template is defined
 * 
 * @private
 */
AjxTemplate.require = function(name) {
	AjxPackage.require({
		name: AjxTemplate.__name2Package(name),
		basePath: AjxTemplate._basePath,
		extension: AjxTemplate._extension
	});
	return AjxTemplate.getTemplate(name) != null;
};

// set innerHTML of a DOM element with the results of a template expansion
// TODO: have some sort of actual error reporting
AjxTemplate.setContent = function(element, name, data) {
	if (typeof element == "string") {
		element = document.getElementById(element);
	}
	if (element == null) return;
	var html = AjxTemplate.expand(name, data);
	element.innerHTML = html;
};

AjxTemplate.__name2Package = function(name) {
	var pkg = name.replace(/#.*$/, "");
	if (name.match(/^#/) && AjxTemplate._stack.length > 0) {
	    pkg = AjxTemplate._stack[AjxTemplate._stack.length - 1];
	}
	return pkg;
};

// temporary API for handling logic errors in templates
//	may change to more robust solution later
AjxTemplate.__formatError = function(templateName, error) {
	return "Error in template '" + templateName + "': " + error;	
};