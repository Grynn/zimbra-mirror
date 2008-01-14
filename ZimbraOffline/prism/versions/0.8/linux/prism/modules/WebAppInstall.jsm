/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is WebRunner.
 *
 * The Initial Developer of the Original Code is Mozilla Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Mark Finkle <mark.finkle@gmail.com>, <mfinkle@mozilla.com>
 *   Cesar Oliveira <a.sacred.line@gmail.com>
 *
 * ***** END LICENSE BLOCK ***** */

const Cc = Components.classes;
const Ci = Components.interfaces;

const PR_WRONLY = 0x02;
const PR_CREATE_FILE = 0x08;
const PR_TRUNCATE = 0x20;

EXPORTED_SYMBOLS = ["WebAppInstall"];

function WebAppInstall()
{
}

WebAppInstall.prototype = {
  createApplication : function(params) {
    var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);

    // Creating a webapp install requires an ID
    if (params.hasOwnProperty("id") == true && params.id.length > 0) {
      var xulRuntime = Cc["@mozilla.org/xre/app-info;1"].getService(Ci.nsIXULRuntime);
      var iconExt = "";
      var os = xulRuntime.OS.toLowerCase();
      if (os == "winnt")
        iconExt = ".ico";
      else if (os == "linux")
        iconExt = ".xpm";
      else if (os == "darwin")
        iconExt = ".icns";

      // Now we will build the webapp folder in the profile
      var appSandbox = dirSvc.get("ProfD", Ci.nsIFile);
      appSandbox.append("webapps");
      appSandbox.append(params.id);
      if (appSandbox.exists())
        appSandbox.remove(true);

      var appINI = appSandbox.clone();
      appINI.append("webapp.ini");
      appINI.create(Ci.nsIFile.NORMAL_FILE_TYPE, 0600);

      // Save the params to an INI file
      var cmd = "[Parameters]\n";
      cmd += "id=" + params.id + "\n";
      cmd += "uri=" + params.uri + "\n";
      cmd += "icon=" + params.icon + "\n";
      cmd += "status=" + params.status + "\n";
      cmd += "location=" + params.location + "\n";
      cmd += "sidebar=" + params.sidebar + "\n";
      cmd += "navigation=" + params.navigation + "\n";

      var stream = Cc['@mozilla.org/network/file-output-stream;1'].createInstance(Ci.nsIFileOutputStream);
      stream.init(appINI, PR_WRONLY | PR_CREATE_FILE | PR_TRUNCATE, 0600, 0);
      stream.write(cmd, cmd.length);
      stream.close();

      // Create a default icon
      var iconName = params.icon + iconExt;
      var appIcon = appSandbox.clone();

      appIcon.append("icons");
      appIcon.append("default");

      var defaultIcon = dirSvc.get("resource:app", Ci.nsIFile);
      defaultIcon.append("chrome");
      defaultIcon.append("icons");
      defaultIcon.append("default");
      defaultIcon.append(iconName);
      defaultIcon.copyTo(appIcon, "");
    }
  },

  createShortcut : function(name, id, icon, location) {
    var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);
    var target = dirSvc.get("resource:app", Ci.nsIFile);

    var appIcon = dirSvc.get("ProfD", Ci.nsIFile);
    appIcon.append("webapps");
    appIcon.append(id);
    appIcon.append("icons");
    appIcon.append("default");

    var xulRuntime = Cc["@mozilla.org/xre/app-info;1"].getService(Ci.nsIXULRuntime);
    var os = xulRuntime.OS.toLowerCase();
    if (os == "winnt") {
      target.append("prism.exe");
      appIcon.append(icon + ".ico");
      this._createShortcutWindows(target, name, id, appIcon, location);
    }
    else if (os == "linux") {
      target.append("prism");
      appIcon.append(icon + ".xpm");
      this._createShortcutLinux(target, name, id, appIcon, location);
    }
    else if (os == "darwin") {
      var targetAdj = target.parent;
      targetAdj.append("MacOS");
      targetAdj.append("xulrunner");
      appIcon.append(icon + ".icns");
      this._createShortcutMac(targetAdj, name, id, appIcon, location);
    }
  },

  _createShortcutWindows : function(target, name, id, icon, location) {
    var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);

    var locations = location.split(",");

    var programs = dirSvc.get("Progs", Ci.nsIFile);
    programs.append("Web Apps");
    if (!programs.exists())
      programs.create(Ci.nsIFile.DIRECTORY_TYPE, 0755);

    var quicklaunch = dirSvc.get("AppData", Ci.nsIFile);
    quicklaunch.append("Microsoft");
    quicklaunch.append("Internet Explorer");
    quicklaunch.append("Quick Launch");

    var file = dirSvc.get("TmpD", Ci.nsIFile);
    file.append("shortcut.vbs");
    if (file.exists())
      file.remove(false);
    file.create(Ci.nsIFile.NORMAL_FILE_TYPE, 0600);

    var cmd = "Set oWsh = CreateObject(\"WScript.Shell\")\n";
    if (locations.indexOf("desktop") > -1) {
      cmd += "sLocation = oWsh.SpecialFolders(\"Desktop\")\n";
      cmd += "Set oShortcut = oWsh.CreateShortcut(sLocation & \"\\" + name + ".lnk\")\n";
      cmd += "oShortcut.TargetPath = \"" + target.path + "\"\n";
      cmd += "oShortcut.Arguments = \"-webapp " + id + "\"\n";
      cmd += "oShortcut.IconLocation = \"" + icon.path + "\"\n";
      cmd += "oShortcut.Save\n"
    }
    if (locations.indexOf("programs") > -1 && programs.exists()) {
      cmd += "sLocation = oWsh.SpecialFolders(\"Programs\") & \"\\Web Apps\"\n";
      cmd += "Set oShortcut = oWsh.CreateShortcut(sLocation & \"\\" + name + ".lnk\")\n";
      cmd += "oShortcut.TargetPath = \"" + target.path + "\"\n";
      cmd += "oShortcut.Arguments = \"-webapp " + id + "\"\n";
      cmd += "oShortcut.IconLocation = \"" + icon.path + "\"\n";
      cmd += "oShortcut.Save\n"
    }
    if (locations.indexOf("quicklaunch") > -1 && quicklaunch.exists()) {
      cmd += "sLocation = \"" + quicklaunch.path + "\"\n";
      cmd += "Set oShortcut = oWsh.CreateShortcut(sLocation & \"\\" + name + ".lnk\")\n";
      cmd += "oShortcut.TargetPath = \"" + target.path + "\"\n";
      cmd += "oShortcut.Arguments = \"-webapp " + id + "\"\n";
      cmd += "oShortcut.IconLocation = \"" + icon.path + "\"\n";
      cmd += "oShortcut.Save\n"
    }

    var stream = Cc['@mozilla.org/network/file-output-stream;1'].createInstance(Ci.nsIFileOutputStream);
    stream.init(file, PR_WRONLY | PR_CREATE_FILE | PR_TRUNCATE, 0600, 0);
    stream.write(cmd, cmd.length);
    stream.close();

    file.launch();
  },

  _createShortcutLinux : function(target, name, id, icon, location) {
    var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);

    var file = dirSvc.get("Desk", Ci.nsIFile);
    file.append(name + ".desktop");
    if (file.exists())
      file.remove(false);
    file.create(Ci.nsIFile.NORMAL_FILE_TYPE, 0600);

    var cmd = "[Desktop Entry]\n";
    cmd += "Name=" + name + "\n";
    cmd += "Type=Application\n";
    cmd += "Comment=Web Application\n";
    cmd += "Exec=" + target.path + " -webapp " + id + "\n";
    cmd += "Icon=" + icon.path + "\n";

    var stream = Cc['@mozilla.org/network/file-output-stream;1'].createInstance(Ci.nsIFileOutputStream);
    stream.init(file, PR_WRONLY | PR_CREATE_FILE | PR_TRUNCATE, 0600, 0);
    stream.write(cmd, cmd.length);
    stream.close();
  },

  _createShortcutMac : function(target, name, id, icon, location) {
    var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);
    var xre = dirSvc.get("XREExeF", Ci.nsIFile);

    var locations = location.split(",");

    if (locations.indexOf("desktop") > -1) {
      var desk = dirSvc.get("Desk", Ci.nsIFile);
      this._createBundle(target, name, id, icon, desk);
    }
    if (locations.indexOf("applications") > -1) {
      var apps = dirSvc.get("LocApp", Ci.nsIFile);
      //apps.append("Web Apps");
      if (!apps.exists())
        apps.create(Ci.nsIFile.DIRECTORY_TYPE, 0755);
      this._createBundle(target, name, id, icon, apps);
    }
    if (locations.indexOf("dock") > -1) {
      // ???
    }
  },

  _createBundle : function(target, name, id, icon, location) {
    var contents =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
    "<plist version=\"1.0\">\n" +
    "<dict>\n" +
    "<key>CFBundleExecutable</key>\n" +
    "<string>" + name + "</string>\n" +
    "<key>CFBundleIconFile</key>\n" +
    "<string>" + icon.leafName + "</string>\n" +
    "</dict>\n" +
    "</plist>";

    location.append(name + ".app");
    if (location.exists())
      location.remove(true);
    location.create(Ci.nsIFile.DIRECTORY_TYPE, 0755);

    location.append("Contents");
    location.create(Ci.nsIFile.DIRECTORY_TYPE, 0755);

    var info = location.clone();
    info.append("Info.plist");
    var stream = Cc['@mozilla.org/network/file-output-stream;1'].createInstance(Ci.nsIFileOutputStream);
    stream.init(info, PR_WRONLY | PR_CREATE_FILE | PR_TRUNCATE, 0600, 0);
    stream.write(contents, contents.length);
    stream.close();

    var resources = location.clone();
    resources.append("Resources");
    resources.create(Ci.nsIFile.DIRECTORY_TYPE, 0755);
    icon.copyTo(resources, icon.leafName);

    var macos = location.clone();
    macos.append("MacOS");
    macos.create(Ci.nsIFile.DIRECTORY_TYPE, 0755);

    var cmd = "#!/bin/sh\n" + target.path + " -webapp " + id;
    var script = macos.clone();
    script.append(name);
    stream.init(script, PR_WRONLY | PR_CREATE_FILE | PR_TRUNCATE, 0755, 0);
    stream.write(cmd, cmd.length);
    stream.close();
  }
}
