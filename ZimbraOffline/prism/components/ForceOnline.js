/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
 
const Cc = Components.classes;
const Ci = Components.interfaces;

const ONLINE_STATUS = "online";
const OFFLINE_STATUS = "offline";

Components.utils.import("resource://gre/modules/XPCOMUtils.jsm");

function DummyEvent(type)
{
  this.type = type;
}

DummyEvent.prototype = {
  QueryInterface: XPCOMUtils.generateQI([Ci.nsIDOMEvent, Ci.nsIClassInfo, Ci.nsISecurityCheckedComponent]),

  // nsIClassInfo
  implementationLanguage: Ci.nsIProgrammingLanguage.JAVASCRIPT,
  flags: Ci.nsIClassInfo.DOM_OBJECT,

  getInterfaces: function getInterfaces(aCount) {
    var interfaces = [Ci.nsIDOMEvent,
                      Ci.nsISecurityCheckedComponent,
                      Ci.nsIClassInfo];
    aCount.value = interfaces.length;
    return interfaces;
  },

  getHelperForLanguage: function getHelperForLanguage(aLanguage) {
    return null;
  },

  //nsISecurityCheckedComponent
  canCallMethod: function canCallMethod(iid, methodName) {
    Components.utils.reportError(methodName);
    return "NoAccess";
  },

  canCreateWrapper: function canCreateWrapper(iid) {
    return "AllAccess";
  },

  canGetProperty: function canGetProperty(iid, propertyName) {
    Components.utils.reportError(propertyName);
    return "AllAccess";
  },

  canSetProperty: function canSetProperty(iid, propertyName) {
    Components.utils.reportError(propertyName);
    return "NoAccess";
  }
}

var gSingleton = null;
var ForceOnlineFactory = {
  createInstance: function af_ci(aOuter, aIID) {
    if (aOuter != null)
      throw Components.results.NS_ERROR_NO_AGGREGATION;

    if (gSingleton == null) {
      gSingleton = new ForceOnline();
    }

    return gSingleton.QueryInterface(aIID);
  }
}

function ForceOnline()
{
  this._listenerMap = [];
  this._ios = Cc["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
}

ForceOnline.prototype = {
  classDescription: "ForceOnline",
  classID: Components.ID("{1a63c05f-caba-4b26-8d2e-f70d4ccc1e97}"),
  contractID: "@zimbra.com/force-online;1",
  
  QueryInterface: XPCOMUtils.generateQI([Ci.nsIObserver, Ci.nsIDOMEventTarget, Ci.nsIProtocolProxyFilter, Ci.nsIClassInfo, Ci.nsISecurityCheckedComponent]),

  _xpcom_factory : ForceOnlineFactory,

  _xpcom_categories: [
    { category: "app-startup", service: true },
    { category: "JavaScript global constructor", entry: "ZimbraNetworkChecker" }
  ],
  
  // nsIClassInfo
  implementationLanguage: Ci.nsIProgrammingLanguage.JAVASCRIPT,
  flags: Ci.nsIClassInfo.DOM_OBJECT,

  getInterfaces: function getInterfaces(aCount) {
    var interfaces = [Ci.nsIObserver,
                      Ci.nsIDOMEventTarget,
                      Ci.nsIProtocolProxyFilter,
                      Ci.nsISecurityCheckedComponent,
                      Ci.nsIClassInfo];
    aCount.value = interfaces.length;
    return interfaces;
  },

  getHelperForLanguage: function getHelperForLanguage(aLanguage) {
    return null;
  },

  //nsISecurityCheckedComponent
  canCallMethod: function canCallMethod(iid, methodName) {
    Components.utils.reportError(methodName);
    return "AllAccess";
  },

  canCreateWrapper: function canCreateWrapper(iid) {
    return "AllAccess";
  },

  canGetProperty: function canGetProperty(iid, propertyName) {
    Components.utils.reportError(propertyName);
    return "AllAccess";
  },

  canSetProperty: function canSetProperty(iid, propertyName) {
    Components.utils.reportError(propertyName);
    return "AllAccess";
  },

  observe : function(aSubject, aTopic, aData) {
    switch (aTopic) {
      case "app-startup":
        var obsService = Cc["@mozilla.org/observer-service;1"].
                         getService(Ci.nsIObserverService);
        obsService.addObserver(this, "network:offline-status-changed", false);
        obsService.addObserver(this, "profile-change-net-teardown", false);
        obsService.addObserver(this, "profile-after-change", false);
        this._networkLinkService = Cc["@mozilla.org/network/network-link-service;1"].getService(Ci.nsINetworkLinkService);
        this._online = this._networkLinkService.isLinkUp;

        this._timer = Cc["@mozilla.org/timer;1"].createInstance(Ci.nsITimer);
        this._timer.init(this, 1000, Ci.nsITimer.TYPE_REPEATING_SLACK);
        break;
      case "profile-after-change":
        var proxyService = Cc["@mozilla.org/network/protocol-proxy-service;1"].getService(Ci.nsIProtocolProxyService);
        proxyService.registerFilter(this, 0);
        break;
      case "profile-change-net-teardown":
        var obsService = Cc["@mozilla.org/observer-service;1"].getService(Ci.nsIObserverService);
        obsService.removeObserver(this, "network:offline-status-changed");
        break;
      case "network:offline-status-changed":
        if (aData == "offline") {
          this._ios.offline = false;
        }
        break;
      case "timer-callback":
        var online = this._networkLinkService.isLinkUp;
        if (online != this._online) {
          this._online = online;
          this._dispatchNetworkStatusEvent(this._online ? ONLINE_STATUS : OFFLINE_STATUS);
        }
        break;
    }
  },
  
  applyFilter : function(ps, uri, proxy) {
    if (uri.host == "localhost" || uri.host == "127.0.0.1") {
      return null;
    }
    else {
      return proxy;
    }
  },
  
  addEventListener : function(type, listener, capture) {
    if (!(type in this._listenerMap)) {
      this._listenerMap[type] = [];
    }
    this._listenerMap[type].push(listener);

    var online = this._networkLinkService.isLinkUp;
    if ((type == ONLINE_STATUS && online) || (type == OFFLINE_STATUS && !online)) {
      this._dispatchNetworkStatusEvent(type);
    }
  },
  
  removeEventListener : function(type, listener, capture) {
    if (type in this._listenerMap) {
      var listeners = this._listenerMap[type];
      for (var i=0; i<listeners.length; i++) {
        if (listeners[i] == listener) {
          listeners.splice(i, 1);
          break;
        }
      }
    }
  },
  
  _dispatchNetworkStatusEvent : function(status) {
    if (status in this._listenerMap) {
      var listeners = this._listenerMap[status];
      for (var i=0; i<listeners.length; i++) {
        var event = new DummyEvent(status);
        event.currentTarget = listeners[i];
        listeners[i].handleEvent(event);
      }
    }
  }
};

function NSGetModule(compMgr, fileSpec) {
  return XPCOMUtils.generateModule([ForceOnline]);
}
