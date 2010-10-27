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
var bundle = Cc["@mozilla.org/intl/stringbundle;1"].getService(Ci.nsIStringBundleService);
bundle = bundle.createBundle("chrome://webapp/locale/webapp.properties");

function preload() {
  return serverCheck();
}

function shutdown() {
  stopServer();
  return true;
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
      zdesktopServer.append("cscript.exe");

      var appRoot = WebAppProperties.getAppRoot();
      var zdesktopRoot = appRoot.parent;
      var zdctl = zdesktopRoot.clone();
      zdctl.append("bin");
      zdctl.append("zdctl-wrapper.vbs");
      args = [zdctl.path, verb];
    }
    else if (os == "darwin" || os == "linux") {
      var appRoot = WebAppProperties.getAppRoot();
      var zdesktopRoot = appRoot.parent;
      zdesktopServer = zdesktopRoot.clone();
      zdesktopServer.append("bin");
      zdesktopServer.append("zdesktop");
      args = [verb];
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

function getPort(uri) {
  var p1 = uri.indexOf("127.0.0.1:");
  if (p1 > 0) {
    p1 = p1 + 10;
    var p2 = uri.indexOf("/", p1);
    return uri.substring(p1, p2);
  } else {
    return "";
  }
}

function reloadWebAppIni(iniFile) {
  var oldUri = WebAppProperties.uri;
  var oldPort = getPort(oldUri);
  WebAppProperties.readINI(iniFile);
  var newUri = WebAppProperties.uri; 
  var newPort = getPort(newUri);

  WebAppProperties.uri = oldPort != "" && oldPort != newPort ? 
    oldUri.replace(":" + oldPort + "/", ":" + newPort + "/") : oldUri;

  var prefs = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
  if (prefs) {
     prefs.setCharPref("capability.principal.codebase.p1.id", "http://127.0.0.1:" + newPort);
     prefs.setCharPref("prism.protocol.mailto", newUri + "&mailto=%s");
  }
}

function serverCheck() {
  var startTime = null;
  var threadManager = Cc["@mozilla.org/thread-manager;1"].getService(Ci.nsIThreadManager);
  var appRoot = WebAppProperties.getAppRoot();
  var iniFile = appRoot.clone();
  iniFile.append("webapp.ini");
  do {
    // update Uri
    reloadWebAppIni(iniFile);
    
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
  } while(Date.now() - startTime < 60000);

  // Give up
  window.alert(bundle.GetStringFromName("StartServerFailed"));
  
  var appStartup = Cc["@mozilla.org/toolkit/app-startup;1"].getService(Ci.nsIAppStartup);
  appStartup.quit(appStartup.eForceQuit);
  
  return false;
}

function load() {
  if ("platform" in window) {
    var xulRuntime = Cc["@mozilla.org/xre/app-info;1"].getService(Ci.nsIXULRuntime);
    var os = xulRuntime.OS.toLowerCase();

    window.platform.icon().behavior = Ci.nsIApplicationIcon.HIDE_ON_CLOSE;

    window.platform.icon().menu.addMenuItem("about", bundle.GetStringFromName("AboutDesktop"), function(){window.platform.showAbout();});
    window.platform.icon().menu.addMenuItem("checkForUpdates", bundle.GetStringFromName("CheckUpdates"), function(){checkForUpdates();});
    if (os == "winnt") {
      window.platform.icon().menu.addMenuItem("quitApp", bundle.GetStringFromName("Quit"), function(){quitApp();});
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
  window.platform.quit();
}

function shutdownService() {
  if (window.confirm(bundle.GetStringFromName("ShutdownConfirm"))) {
    if (!stopServer()) {
      window.alert(bundle.GetStringFromName("StopServerFailed"));
    }
    quitApp();
  }
}
