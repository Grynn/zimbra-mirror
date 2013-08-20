/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
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
function ZmCloudChatEvent() {
    this._listeners = [];
};
 
ZmCloudChatEvent.prototype = {
    attach : function (listener, id) {
        this._listeners.push({listener:listener, id:id});
    },
    notify : function (args) {
        for (var i = 0; i < this._listeners.length; i++) {
           // this._listeners[i](this._sender, args);
        	 this._listeners[i].listener(args);
        }
    },
    removeListener: function(id) {
    	   for (var i = 0; i < this._listeners.length; i++) {
        	 if(this._listeners[i].id == id) {
				 this._listeners.splice(i, 1);
			 }
        }
    },

    removeAllListeners: function() {
    	this._listeners = [];
    }
};
