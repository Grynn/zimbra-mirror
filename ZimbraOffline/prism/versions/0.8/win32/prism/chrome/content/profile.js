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
 *   Wladimir Palant <trev@adblockplus.org>
 *   Mark Finkle <mark.finkle@gmail.com>, <mfinkle@mozilla.com>
 *   Cesar Oliveira <a.sacred.line@gmail.com>
 *
 * ***** END LICENSE BLOCK ***** */

/**
 * Constructs an nsISimpleEnumerator for the given array of items.
 */
function ArrayEnumerator(aItems) {
  this._items = aItems;
  this._nextIndex = 0;
}

ArrayEnumerator.prototype = {
  hasMoreElements: function()
  {
    return this._nextIndex < this._items.length;
  },
  getNext: function()
  {
    if (!this.hasMoreElements())
      throw Components.results.NS_ERROR_NOT_AVAILABLE;

    return this._items[this._nextIndex++];
  },
  QueryInterface: function(aIID)
  {
    if (Ci.nsISimpleEnumerator.equals(aIID) ||
        Ci.nsISupports.equals(aIID))
      return this;

    throw Components.results.NS_ERROR_NO_INTERFACE;
  }
};

/**
 * Directory provider that provides access to external chrome icons
 */
const NS_APP_CHROME_DIR_LIST = "AChromDL";

function IconProvider(aFolder) {
  this._folder = aFolder;
}

IconProvider.prototype = {
  getFile: function(prop, persistent) {
    return Components.results.NS_ERROR_FAILURE;
  },

  getFiles: function(prop, persistent) {
    if (prop == NS_APP_CHROME_DIR_LIST) {
      return new ArrayEnumerator([this._folder]);
    }
    return Components.results.NS_ERROR_FAILURE;
  },

  QueryInterface: function(iid) {
    if (iid.equals(Ci.nsIDirectoryServiceProvider) ||
        iid.equals(Ci.nsIDirectoryServiceProvider2) ||
        iid.equals(Ci.nsISupports))
    {
      return this;
    }
    throw Components.results.NS_ERROR_NO_INTERFACE;
  }
};


/**
 * Profile object provides access to web applications profile bundle.
 * It handles unpacking the bundle to the profile folder. Then it parses
 * the parameters and loads the script.
 */
function Profile(aCmdLine)
{
  if (!aCmdLine)
    return;

  var file = null;

  // Check for a webapp profile
  var webapp = aCmdLine.handleFlagWithParam("webapp", false);
  if (webapp) {
    // Check for a bundle first
    try {
      file = aCmdLine.resolveFile(webapp);
    }
    catch (ex) {
      // Ouch, not a file
      file = null;
    }

    // Do we have a valid file? or did it fail?
    if (!file || !file.exists()) {
      // Its not a bundle. look for an installed webapp
      var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);
      var appSandbox = dirSvc.get("ProfD", Ci.nsIFile);
      appSandbox.append("webapps");
      appSandbox.append(webapp);
      if (appSandbox.exists())
        file = appSandbox.clone();
    }
  }

  // Check for an OSX launch
  if (!file) {
    var uri = aCmdLine.handleFlagWithParam("url", false);
    if (uri) {
      uri = aCmdLine.resolveURI(uri);
      file = uri.QueryInterface(Ci.nsIFileURL).file;
    }
  }

  if (file && file.exists()) {
    // Bundles are files and need to be installed
    if (!file.isDirectory())
      file = this.install(file);

    this.init(file);
  }

  this.readCommandLine(aCmdLine);
}

Profile.prototype = {
  script : {},
  id : "",
  fileTypes : [],
  uri : null,
  icon : "app",
  status : false,
  location : false,
  sidebar : false,
  navigation : false,
  flags : ["id", "uri", "icon", "status", "location", "sidebar", "navigation"],

  setParameter: function(aName, aValue) {
    if (this.flags.indexOf(aName) == -1)
      return;

    if (typeof this[aName] == "boolean")
      aValue = (aValue.toLowerCase() == "true" || aValue.toLowerCase() == "yes");

    this[aName] = aValue;
  },

  readINI : function(aFile) {
    var iniFactory = Components.manager.getClassObjectByContractID("@mozilla.org/xpcom/ini-parser-factory;1", Ci.nsIINIParserFactory);
    var iniParser = iniFactory.createINIParser(aFile);

    var keys = iniParser.getKeys("Parameters");
    while (keys.hasMore()) {
      var key = keys.getNext();
      var value = iniParser.getString("Parameters", key);
      this.setParameter(key.toLowerCase(), value);
    }

    keys = iniParser.getKeys("FileTypes");
    while (keys.hasMore()) {
      var key = keys.getNext();
      var value = iniParser.getString("Parameters", key);
      var values = value.split(";");
      if (values.length == 4) {
        var type = {};
        type.name = values[0];
        type.extension = values[1];
        type.description = values[2];
        type.contentType = values[3];
        this.fileTypes.push(type);
      }
    }
  },

  init : function(aFile) {
    var appSandbox = aFile.clone();

    // Read the INI settings
    var appINI = appSandbox.clone();
    appINI.append("webapp.ini");
    if (appINI.exists())
      this.readINI(appINI);

    // Load the application script
    var appScript = appSandbox.clone();
    appScript.append("webapp.js");
    if (appScript.exists()) {
      var ios = Cc["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
      var appScriptURI = ios.newFileURI(appScript);

      var scriptLoader = Cc["@mozilla.org/moz/jssubscript-loader;1"].getService(Ci.mozIJSSubScriptLoader);
      scriptLoader.loadSubScript(appScriptURI.spec, this.script);
    }

    // Load the application style
    var appStyle = appSandbox.clone();
    appStyle.append("webapp.css");
    if (appStyle.exists()) {
      var ios = Cc["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
      var appStyleURI = ios.newFileURI(appStyle);

      var styleSheets = Cc["@mozilla.org/content/style-sheet-service;1"].getService(Ci.nsIStyleSheetService);
      styleSheets.loadAndRegisterSheet(appStyleURI, styleSheets.USER_SHEET);
    }

    // Initialize the icon provider
    var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);
    var iconProvider = new IconProvider(appSandbox);
    dirSvc.QueryInterface(Ci.nsIDirectoryService).registerProvider(iconProvider);
  },

  install : function(aFile) {
    var dirSvc = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties);

    try {
      var reader = Cc["@mozilla.org/libjar/zip-reader;1"].createInstance(Ci.nsIZipReader);
      reader.open(aFile);
      reader.test(null);

      // Extract the webapp.ini to a temp location so it can be parsed
      var tempINI = dirSvc.get("TmpD", Ci.nsIFile);
      tempINI.append("webapp.ini");
      tempINI.createUnique(Ci.nsIFile.NORMAL_FILE_TYPE, 0600);
      reader.extract("webapp.ini", tempINI);
      this.readINI(tempINI);
      tempINI.remove(false);

      // Creating a webapp install requires an ID
      if (this.id.length > 0) {
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
        appSandbox.append(this.id);
        if (appSandbox.exists())
          appSandbox.remove(true);

        // Make a copy so we can return it
        aFile = appSandbox.clone();

        var appINI = appSandbox.clone();
        appINI.append("webapp.ini");
        appINI.create(Ci.nsIFile.NORMAL_FILE_TYPE, 0600);
        reader.extract("webapp.ini", appINI);

        if (reader.hasEntry("webapp.js")) {
          var appScript = appSandbox.clone();
          appScript.append("webapp.js");
          appScript.create(Ci.nsIFile.NORMAL_FILE_TYPE, 0600);
          reader.extract("webapp.js", appScript);
        }

        // We check for an OS specific and common stylesheet,
        // defaulting to the OS specific sheet
        if (reader.hasEntry(os + "/webapp.css") || reader.hasEntry("webapp.css")) {
          var appStyle = appSandbox.clone();
          appStyle.append("webapp.css");
          appStyle.create(Ci.nsIFile.NORMAL_FILE_TYPE, 0600);
          if (reader.hasEntry(os + "/webapp.css"))
            reader.extract(os + "/webapp.css", appStyle);
          else
            reader.extract("webapp.css", appStyle);
        }

        var iconName = this.icon + iconExt;
        var appIcon = appSandbox.clone();
        appIcon.append("icons");
        appIcon.append("default");

        if (reader.hasEntry(iconName)) {
          appIcon.append(iconName);
          appIcon.create(Ci.nsIFile.NORMAL_FILE_TYPE, 0600);
          reader.extract(iconName, appIcon);
        }
        else {
          // webapp.ini doesn't have its own icon, so we substitute the
          // default icon instead
          var defaultIcon = dirSvc.get("resource:app", Ci.nsIFile);

          defaultIcon.append("chrome");
          defaultIcon.append("icons");
          defaultIcon.append("default");
          defaultIcon.append(iconName);

          defaultIcon.copyTo(appIcon, "");
        }
      }
    }
    catch (e) {
      Components.utils.reportError(e);
    }

    return aFile;
  },

  readCommandLine : function(aCmdLine) {
    for (var index in this.flags) {
      var key = this.flags[index];
      var value = aCmdLine.handleFlagWithParam(key, false);
      if (value != null)
        this.setParameter(key, value);
    }
  }
}
