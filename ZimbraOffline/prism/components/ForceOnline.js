const Cc = Components.classes;
const Ci = Components.interfaces;

Components.utils.import("resource://gre/modules/XPCOMUtils.jsm");

function ForceOnline()
{
}

ForceOnline.prototype = {
  classDescription: "ForceOnline",
  classID: Components.ID("{1a63c05f-caba-4b26-8d2e-f70d4ccc1e97}"),
  contractID: "@zimbra.com/force-online;1",
  
  QueryInterface: XPCOMUtils.generateQI([Ci.nsIObserver]),

  _xpcom_categories: [{ category: "app-startup", service: true }],
  
  observe : function(aSubject, aTopic, aData) {
    switch (aTopic) {
      case "app-startup":
        var obsService = Cc["@mozilla.org/observer-service;1"].
                         getService(Ci.nsIObserverService);
        obsService.addObserver(this, "network:offline-status-changed", false);
        obsService.addObserver(this, "profile-change-net-teardown", false);
        break;
      case "profile-change-net-teardown":
        var obsService = Cc["@mozilla.org/observer-service;1"].
                         getService(Ci.nsIObserverService);
        obsService.removeObserver(this, "network:offline-status-changed");
        break;
      case "network:offline-status-changed":
        if (aData == "offline") {
          var ioService = Cc["@mozilla.org/network/io-service;1"].
                          getService(Ci.nsIIOService);
          ioService.offline = false;
        }
        break;
    }
  }
};

function NSGetModule(compMgr, fileSpec) {
  return XPCOMUtils.generateModule([ForceOnline]);
}
