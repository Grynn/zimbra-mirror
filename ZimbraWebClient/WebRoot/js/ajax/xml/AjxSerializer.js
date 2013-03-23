/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2009, 2010 VMware, Inc.
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

//
// Base class
//

AjxSerializer = function() {
    if (arguments.length == 0) return;
}

// Public methods

AjxSerializer.prototype.serialize = function(node) { throw "NOT IMPLEMENTED"; };

//
// Classes
//

/**
 * This class attempts to serialize a DOM document in the same format
 * as the Zimbra Server.
 * @class
 * @constructor
 * @private
 */
AjxJsonSerializer = function(minimize) {
    AjxSerializer.call(this, null);
    this._minimize = Boolean(minimize);
}
AjxJsonSerializer.prototype = new AjxSerializer;
AjxJsonSerializer.prototype.constructor = AjxJsonSerializer;

// Constants

AjxJsonSerializer.E_ATTRS = "_attrs";
AjxJsonSerializer.A_CONTENT = "_content";
AjxJsonSerializer.A_NAMESPACE = "_jsns";

// AjxSerializer methods

// TODO: handle namespaces
AjxJsonSerializer.prototype.serialize = function(node) {
    // gather elements and content
    var elems = {};
    var elemCount = 0;
    var content = null;
    for (var child = node.firstChild; child; child = child.nextSibling) {
        var type = child.nodeType;
        // elements
        if (type == 1) {
            var name = child.nodeName;
            if (!elems[name]) {
                elems[name] = [];
                elemCount++;
            }
            elems[name].push(child);
            continue;
        }
        // text and CDATA nodes
        if (type == 3 || type == 4) {
            if (!content) {
                content = [];
            }
            content.push(child.nodeValue);
            continue;
        }
    }

    // gather attributes
    var attrs = {};
    var attrCount = 0;
    if (node.attributes) {
        for (var i = 0; i < node.attributes.length; i++) {
            var attr = node.attributes[i];
            var name = attr.nodeName;
            var value = this.quote(attr.nodeValue);
            if (elems[name]) {
                attrs[name] = value;
                attrCount++;
            }
            else {
                elems[name] = value;
                elemCount++;
            }
        }
    }

    // escape content
    content = content && content.join("");
    if (content && content.match(/^\s*$/)) {
        content = null;
    }
    if (content) {
        content = this.quote(content);

        // do we only have content?
        if (attrCount == 0 && elemCount == 0) {
            return content;
        }
    }

    // is there anything to do?
    if (content == null && elemCount == 0 && attrCount == 0) {
//        return "null";
        return "{}";
    }

    // serialize content
    var a = [ "{" ];
    if (content) {
        a.push(this.quote(AjxJsonSerializer.A_CONTENT), ":", content);
        if (attrCount > 0 || elemCount > 0) {
            a.push(",");
        }
    }

    // serialize attributes
    if (attrCount > 0) {
        a.push(this.quote(AjxJsonSerializer.E_ATTRS), ": { ");
        var i = 0;
        for (var name in attrs) {
            if (i > 0) {
                a.push(",");
            }
            a.push(this.quote(name), ":", attrs[name]);
            i++;
        }
        a.push("}");
        if (elemCount > 0) {
            a.push(", ");
        }
    }

    // serialize elements
    var j = 0;
    for (var name in elems) {
        if (j > 0) {
            a.push(",");
        }
        var elem = elems[name];
        if (typeof elem == "string") {
            a.push(this.quote(name), ":", elem);
            j++;
            continue;
        }
        a.push(this.quote(name), ":");
        if (!this._minimize || elem.length > 1) {
            a.push("[");
        }
        for (var i = 0; i < elem.length; i++) {
            if (i > 0) {
                a.push(",");
            }
            a.push(this.serialize(elem[i]));
        }
        if (!this._minimize || elem.length > 1) {
            a.push("]");
        }
        j++;
    }
    a.push("}");

    return a.join("");
};

// convenience methods

AjxJsonSerializer.prototype.quote = function(s) {
    return [ '"', this.escape(s), '"' ].join("");
};

AjxJsonSerializer.prototype.escape = function(s) {
    return s.replace(/\\/g, "\\\\").replace(/"/g, "\\\"").replace(/\n/g, "\\n");
};