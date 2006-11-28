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

function AjxTemplate() {}

//
// Data
//

AjxTemplate._templates = {};

//
// Public functions
//

AjxTemplate.register = function(name, func) {
    AjxTemplate._templates[name] = func;
};

AjxTemplate.getTemplate = function(name) {
    return AjxTemplate._templates[name];
};

AjxTemplate.expand = function(name, data, buffer) {
    var pkg = name.replace(/#.*$/, "");
    var id = name.replace(/^[^#]*#?/, "");
    if (id) {
        name = [pkg, id].join("#");
    }

    AjxPackage.require(pkg);

    var hasBuffer = Boolean(buffer);
    buffer = buffer || [];
    var func = AjxTemplate._templates[name];
    if (func) {
        func(data, buffer);
    } else {
    	buffer.push(name);
    }

    return hasBuffer ? buffer.length : buffer.join("");
};

// set innerHTML of a DOM element with the results of a template expansion
AjxTemplate.setContent = function(element, name, data) {
	if (typeof element == "string") {
		element = document.getElementById(element);
	}
	if (element == null) return;
	var html = AjxTemplate.expand(name, data);
	element.innerHTML = html;
}