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
