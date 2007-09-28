/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
/*
 * Copyright (C) 2006, The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Andy Clark
 */
AjxTemplate = function() {}

//
// Data
//

AjxTemplate._templates = {};
AjxTemplate._stack = [];

//
// Public functions
//

AjxTemplate.setBasePath = function(basePath) {
    AjxTemplate._basePath = basePath;
};
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

AjxTemplate.expand = function(name, data, buffer) {
    var pkg = name.replace(/#.*$/, "");
    if (name.match(/^#/) && AjxTemplate._stack.length > 0) {
        pkg = AjxTemplate._stack[AjxTemplate._stack.length - 1];
    }
    var id = name.replace(/^[^#]*#?/, "");
    if (id) {
        name = [pkg, id].join("#");
    }

    AjxPackage.require({
		name: pkg,
		basePath: AjxTemplate._basePath,
		extension: AjxTemplate._extension
	});

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

// set innerHTML of a DOM element with the results of a template expansion
// TODO: have some sort of actual error reporting
AjxTemplate.setContent = function(element, name, data) {
	if (typeof element == "string") {
		element = document.getElementById(element);
	}
	if (element == null) return;
	var html = AjxTemplate.expand(name, data);
	element.innerHTML = html;
}


// temporary API for handling logic errors in templates
//	may change to more robust solution later
AjxTemplate.__formatError = function(templateName, error) {
	return "Error in template '" + templateName + "': " + error;	
}