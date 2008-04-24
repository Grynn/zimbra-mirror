function preload() {
  return serverCheck();
}

function startServer() {
  var xulRuntime = Cc["@mozilla.org/xre/app-info;1"].getService(Ci.nsIXULRuntime);
  var os = xulRuntime.OS.toLowerCase();

  try {
    var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);

    var zdesktopServer = null;
    var args = null;
    if (os == "winnt") {
      var systemDir = dirSvc.get("SysD", Ci.nsIFile);
      var zdesktopServer = systemDir.clone();
      zdesktopServer.append("net.exe");
      args = ["start", "Zimbra Desktop Service"];
    }
    else if (os == "linux") {
      var appRoot = WebAppProperties.getAppRoot();
      var zdesktopRoot = appRoot.parent;
      zdesktopServer = zdesktopRoot.clone();
      zdesktopServer.append("zdesktop");
      args = ["start"];
    }
    else if (os == "darwin") {
      zdesktopServer = Cc["@mozilla.org/file/local;1"].createInstance(Components.interfaces.nsILocalFile);
      zdesktopServer.initWithPath("/bin");
      zdesktopServer.append("launchctl");
      args = ["start", "com.zimbra.zdesktop"];
    }

    var process = Cc["@mozilla.org/process/util;1"].createInstance(Ci.nsIProcess);
    process.init(zdesktopServer);
    process.run(false, args, args.length);
  }
  catch(e) {
    // Couldn't start the server so give up
    return false;
  }
  
  return true;
}

function serverCheck() {
  var startTime = null;
  var threadManager = Cc["@mozilla.org/thread-manager;1"].getService(Ci.nsIThreadManager);
  do {
    // Check whether the server is running
    var req = new XMLHttpRequest();
    req.open('GET', WebAppProperties.uri, false);
    try {
      req.send(null);
      if (req.status == 200)
        return true;
    }
    catch(e) {
    }

    if (!startTime) {
      // Keep trying until the server is available or 5 seconds have elapsed
      startTime = Date.now();
      if (!startServer())
        break;
    }
    
    // Pump events so the UI stays responsive
    threadManager.currentThread.processNextEvent(true);
  }while(Date.now() - startTime < 5000);

  // Give up
  // L10N?
  window.alert("Couldn't start Zimbra Desktop server, giving up.");
  
  var appStartup = Cc["@mozilla.org/toolkit/app-startup;1"].getService(Ci.nsIAppStartup);
  appStartup.quit(appStartup.eForceQuit);
  
  return false;
}
