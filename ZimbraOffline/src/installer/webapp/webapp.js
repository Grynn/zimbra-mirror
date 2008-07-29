var bundle = Cc["@mozilla.org/intl/stringbundle;1"].getService(Ci.nsIStringBundleService);
bundle = bundle.createBundle("chrome://webapp/locale/webapp.properties");

function preload() {
  return serverCheck();
}

function startServer() {
  return startStopServer("start");
}

function stopServer() {
  return startStopServer("stop");
}

function startStopServer(verb) {
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
      args = [verb, "Yahoo! Zimbra Desktop Service"];
    }
    else if (os == "linux") {
      var appRoot = WebAppProperties.getAppRoot();
      var zdesktopRoot = appRoot.parent;
      zdesktopServer = zdesktopRoot.clone();
      zdesktopServer.append("zdesktop");
      args = [verb];
    }
    else if (os == "darwin") {
      zdesktopServer = Cc["@mozilla.org/file/local;1"].createInstance(Components.interfaces.nsILocalFile);
      zdesktopServer.initWithPath("/bin");
      zdesktopServer.append("launchctl");
      args = [verb, "com.zimbra.zdesktop"];
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
  } while(Date.now() - startTime < 30000);

  // Give up
  window.alert(bundle.GetStringFromName("StartServerFailed"));
  
  var appStartup = Cc["@mozilla.org/toolkit/app-startup;1"].getService(Ci.nsIAppStartup);
  appStartup.quit(appStartup.eForceQuit);
  
  return false;
}

function load() {
  if ("platform" in window) {
    window.platform.icon().behavior = Ci.nsIApplicationIcon.HIDE_ON_MINIMIZE | Ci.nsIApplicationIcon.HIDE_ON_CLOSE;

    var head = window.document.documentElement.firstChild;
    var command = window.document.createElement("command");
    head.appendChild(command);
    command.id = "about";
    command.setAttribute("label", bundle.GetStringFromName("AboutDesktop"));
    command.addEventListener("DOMActivate", function(event) {host.showAbout();}, false);
    window.platform.icon().menu.addMenuItem("about");

    command = window.document.createElement("command");
    head.appendChild(command);
    command.id = "checkForUpdates";
    command.setAttribute("label", bundle.GetStringFromName("CheckUpdates"));
    command.addEventListener("DOMActivate", function(event) {checkForUpdates();}, false);
    window.platform.icon().menu.addMenuItem("checkForUpdates");

    command = window.document.createElement("command");
    head.appendChild(command);
    command.id = "shutdownService";
    command.setAttribute("label", bundle.GetStringFromName("ShutdownService"));
    command.addEventListener("DOMActivate", function(event) {shutdownService();}, false);
    window.platform.icon().menu.addMenuItem("shutdownService");

    var xulRuntime = Cc["@mozilla.org/xre/app-info;1"].getService(Ci.nsIXULRuntime);
    var os = xulRuntime.OS.toLowerCase();
    if (os == "winnt") {
      command = window.document.createElement("command");
      head.appendChild(command);
      command.id = "quitApp";
      command.setAttribute("label", bundle.GetStringFromName("Quit"));
      command.addEventListener("DOMActivate", function(event) {quitApp();}, false);
      window.platform.icon().menu.addMenuItem("quitApp");
    }
  }
}

function checkForUpdates()
{
  var um = Cc["@mozilla.org/updates/update-manager;1"].getService(Components.interfaces.nsIUpdateManager);
  var prompter = Cc["@mozilla.org/updates/update-prompt;1"].createInstance(Components.interfaces.nsIUpdatePrompt);

  // If there's an update ready to be applied, show the "Update Downloaded"
  // UI instead and let the user know they have to restart the browser for
  // the changes to be applied. 
  if (um.activeUpdate && um.activeUpdate.state == "pending")
    prompter.showUpdateDownloaded(um.activeUpdate);
  else
    prompter.checkForUpdates();
}

function quitApp() {
  var appStartup = Cc["@mozilla.org/toolkit/app-startup;1"].getService(Ci.nsIAppStartup);
  appStartup.quit(appStartup.eAttemptQuit);
}

function shutdownService() {
  if (window.confirm(bundle.GetStringFromName("ShutdownConfirm"))) {
    if (!stopServer()) {
      window.alert(bundle.GetStringFromName("StopServerFailed"));
    }
    quitApp();
  }
}
