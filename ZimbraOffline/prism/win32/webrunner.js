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
 *   Mark Finkle, <mark.finkle@gmail.com>, <mfinkle@mozilla.com>
 *   Wladimir Palant <trev@adblockplus.org>
 *   Sylvain Pasche <sylvain.pasche@gmail.com>
 *   Matthew Gertner <matthew.gertner@gmail.com>
 *
 * ***** END LICENSE BLOCK ***** */
 
const Cc = Components.classes;
const Ci = Components.interfaces;

Components.utils.import("resource://gre/modules/JSON.jsm");
Components.utils.import("resource://prism-runtime/modules/WebAppProperties.jsm");
Components.utils.import("resource://prism-runtime/modules/HostUI.jsm");

window.addEventListener("load", function() { WebRunner.startup(); }, false);

/**
 * Main application code.
 */
var WebRunner = {
  _ios : null,
  _tld : null,
  _xulWindow : null,
  _currentDomain : null,
  _windowCreator : null,

  _getBrowser : function() {
    return document.getElementById("browser_content");
  },

  _saveSettings : function() {
      var settings = {};
      settings.version = "1";

      // Pull out the window state
      settings.window = {};
      settings.window.state = window.windowState;
      if (window.windowState == window.STATE_NORMAL) {
        settings.window.screenX = window.screenX;
        settings.window.screenY = window.screenY;
        settings.window.width = window.outerWidth;
        settings.window.height = window.outerHeight;
      }

      settings.sidebar = {};
      settings.sidebar.visible = (document.getElementById("splitter_sidebar").getAttribute("state") == "open");
      settings.sidebar.width = document.getElementById("box_sidebar").width;

      // Save using JSON format
      if (WebAppProperties.hasOwnProperty("id")) {
        var json = JSON.toString(settings);
        var file = WebAppProperties.getAppRoot();
        file.append("localstore.json");
        FileIO.stringToFile(json, file);
      }
  },

  _loadSettings : function() {
    // Load using JSON format
    var settings;
    if (WebAppProperties.hasOwnProperty("id")) {
      var file = WebAppProperties.getAppRoot();
      file.append("localstore.json");
      if (file.exists()) {
        var json = FileIO.fileToString(file);
        settings = JSON.fromString(json);

        if (settings.window) {
          switch (settings.window.state) {
            case window.STATE_MAXIMIZED:
              window.maximize();
              break;
            case window.STATE_MINIMIZED:
              // Do nothing if window was closed minimized
              break;
            case window.STATE_NORMAL:
              window.moveTo(settings.window.screenX, settings.window.screenY);
              window.resizeTo(settings.window.width, settings.window.height);
              break;
          }
        }

        if (settings.sidebar) {
          document.getElementById("splitter_sidebar").setAttribute("state", settings.sidebar.visible ? "open" : "collapsed");
          document.getElementById("box_sidebar").width = settings.sidebar.width;
        }
      }
    }
  },

  _delayedStartup : function() {
    this._prepareWebAppScript();
  
    if (WebAppProperties.uri) {
      // Give the user script the chance to do additional processing before
      // the page loads
      if (WebAppProperties.script.preload) {
        if (!WebAppProperties.script.preload())
          // Preload failed so don't load the web app URI
          return;
      }
      
      // Show tray icon, if any, and default behavior to hide on minimize
      if (WebAppProperties.trayicon) {
        this.showTrayIcon();
        
        var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
        var icon = desktop.getApplicationIcon(this._getBrowser().contentWindow);
        icon.behavior = Ci.nsIApplicationIcon.HIDE_ON_MINIMIZE;
      }

      // Setup the resource:// substitution for the app's root directory
      var resourceProtocol = this._ios.getProtocolHandler("resource").QueryInterface(Ci.nsIResProtocolHandler);
      var appRootURI = this._ios.newFileURI(WebAppProperties.getAppRoot());
      resourceProtocol.setSubstitution("webapp", appRootURI);
      
      // Call the script's load() function once the page has finished loading
      if (WebAppProperties.script.load) {
        this._getBrowser().addEventListener("DOMContentLoaded", this._contentLoaded, true);
      }
      
      this._getBrowser().loadURI(WebAppProperties.uri, null, null);
    }
    
    this._loadSettings();
  },
  
  _contentLoaded : function(event) {
    var browser = WebRunner._getBrowser();
    // Don't fire for iframes
    if (event.target == browser.contentDocument) {
      browser.removeEventListener("DOMContentLoaded", WebRunner._contentLoaded, true);
      WebAppProperties.script.load();
    }
  },

  _processConfig : function() {
    // Process commandline parameters
    document.documentElement.setAttribute("id", WebAppProperties.icon);
    document.getElementById("toolbar_main").hidden = !WebAppProperties.location;
    document.getElementById("box_sidebar").hidden = !WebAppProperties.sidebar;
    document.getElementById("splitter_sidebar").hidden = !WebAppProperties.sidebar;
    document.getElementById("statusbar").setAttribute("collapsed", !WebAppProperties.status);

    if (!WebAppProperties.navigation) {
      // Remove navigation key from the document
      var keys = document.getElementsByTagName("key");
      for (var i=keys.length - 1; i>=0; i--)
        if (keys[i].className == "nav")
          keys[i].parentNode.removeChild(keys[i]);
    }

    // Default the name of the window to the webapp name
    document.title = WebAppProperties.name;
  },

  _handleWindowClose : function(event) {
    // Handler for clicking on the 'x' to close the window
    return this.shutdownQuery();
  },

  _popupShowing : function(aEvent) {
    var cut = document.getElementById("cmd_cut");
    var copy = document.getElementById("cmd_copy");
    var paste = document.getElementById("cmd_paste");
    var del = document.getElementById("cmd_delete");

    var isContentSelected = !document.commandDispatcher.focusedWindow.getSelection().isCollapsed;

    var target = document.popupNode;
    var isTextField = target instanceof HTMLTextAreaElement;
    if (target instanceof HTMLInputElement && (target.type == "text" || target.type == "password"))
      isTextField = true;

    var isTextSelectied= (isTextField && target.selectionStart != target.selectionEnd);

    cut.setAttribute("disabled", ((!isTextField || !isTextSelectied) ? "true" : "false"));
    copy.setAttribute("disabled", (((!isTextField || !isTextSelectied) && !isContentSelected) ? "true" : "false"));
    paste.setAttribute("disabled", (!isTextField ? "true" : "false"));
    del.setAttribute("disabled", (!isTextField ? "true" : "false"));

    var copylink = document.getElementById("menuitem_copylink");
    var copylinkSep = document.getElementById("menusep_copylink");
    if (target instanceof HTMLAnchorElement && target.href) {
      copylink.hidden = false;
      copylinkSep.hidden = false;
    }
    else {
      copylink.hidden = true;
      copylinkSep.hidden = true;
    }

    InlineSpellCheckerUI.clearSuggestionsFromMenu();
    InlineSpellCheckerUI.uninit();

    var separator = document.getElementById("menusep_spellcheck");
    separator.hidden = true;
    var addToDictionary = document.getElementById("menuitem_addToDictionary");
    addToDictionary.hidden = true;
    var noSuggestions = document.getElementById("menuitem_noSuggestions");
    noSuggestions.hidden = true;

    // if the document is editable, show context menu like in text inputs
    var win = target.ownerDocument.defaultView;
    if (win) {
      var isEditable = false;
      try {
        var editingSession = win.QueryInterface(Ci.nsIInterfaceRequestor)
                                .getInterface(Ci.nsIWebNavigation)
                                .QueryInterface(Ci.nsIInterfaceRequestor)
                                .getInterface(Ci.nsIEditingSession);
        isEditable = editingSession.windowIsEditable(win);
      }
      catch(ex) {
        // If someone built with composer disabled, we can't get an editing session.
      }
    }

    var editor = null;
    if (isTextField && !target.readOnly)
      editor = target.QueryInterface(Ci.nsIDOMNSEditableElement).editor;

    if (isEditable)
      editor = editingSession.getEditorForWindow(win);

    if (editor) {
      InlineSpellCheckerUI.init(editor);
      InlineSpellCheckerUI.initFromEvent(document.popupRangeParent, document.popupRangeOffset);

      var onMisspelling = InlineSpellCheckerUI.overMisspelling;
      if (onMisspelling) {
        separator.hidden = false;
        addToDictionary.hidden = false;
        var menu = document.getElementById("popup_content");
        var suggestions = InlineSpellCheckerUI.addSuggestionsToMenu(menu, addToDictionary, 5);
        noSuggestions.hidden = (suggestions > 0);
      }
    }
  },

  _tooltipShowing : function(aEvent) {
    var tooltipNode = document.tooltipNode;
    var canShow = false;
    if (tooltipNode.namespaceURI != "http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul") {
      const XLinkNS = "http://www.w3.org/1999/xlink";

      var titleText = null;
      var XLinkTitleText = null;
      var direction = tooltipNode.ownerDocument.dir;
      var defView = tooltipNode.ownerDocument.defaultView;

      while (defView && !titleText && !XLinkTitleText && tooltipNode) {
        if (tooltipNode.nodeType == Node.ELEMENT_NODE) {
          titleText = tooltipNode.getAttribute("title");
          XLinkTitleText = tooltipNode.getAttributeNS(XLinkNS, "title");
          direction = defView.getComputedStyle(tooltipNode, "").getPropertyValue("direction");
        }
        tooltipNode = tooltipNode.parentNode;
      }

      var tooltip = document.getElementById("tooltip_content");
      tooltip.style.direction = direction;

      for each (var text in [titleText, XLinkTitleText]) {
        if (text && /\S/.test(text)) {
          // Per HTML 4.01 6.2 (CDATA section), literal CRs and tabs should be
          // replaced with spaces, and LFs should be removed entirely.
          text = text.replace(/[\r\t]/g, ' ');
          text = text.replace(/\n/g, '');

          tooltip.setAttribute("label", text);
          canShow = true;
        }
      }
    }

    if (!canShow)
      aEvent.preventDefault();
  },

  _domTitleChanged : function(aEvent) {
    if (aEvent.target != this._getBrowser().contentDocument)
      return;

    document.title = aEvent.target.title;
  },

  _getBaseDomain : function(aUri) {
    try {
      if (aUri.host == "localhost") {
        return aUri.host;
      }
      else {
        return this._tld.getBaseDomain(aUri.QueryInterface(Ci.nsIURL));
      }
    }
    catch(e) {
      // Don't know how to get the domain for this URL
      return null;
    }
  },

  _isLinkExternal : function(aLink) {
    var isExternal;
    if (aLink instanceof HTMLAnchorElement) {
      if (aLink.target == "_self" || aLink.target == "_top") {
        isExternal = false;
      }
      else {
        isExternal = this._isURIExternal(this._ios.newURI(aLink.href, null, null));
      }
    }
    return isExternal;
  },

  _isURIExternal : function(aURI) {
    var linkDomain = this._getBaseDomain(aURI);
    // Can't use browser.currentURI since it causes reentrancy into the docshell.
    if (!linkDomain || (linkDomain == this._currentDomain))
      return false;
    else
      return true;
  },

  _dragOver : function(aEvent)
  {
    var dragService = Cc["@mozilla.org/widget/dragservice;1"].getService(Ci.nsIDragService);
    var dragSession = dragService.getCurrentSession();

    var supported = dragSession.isDataFlavorSupported("text/x-moz-url");
    if (!supported)
      supported = dragSession.isDataFlavorSupported("application/x-moz-file");

    if (supported)
      dragSession.canDrop = true;
  },

  _dragDrop : function(aEvent)
  {
    var dragService = Cc["@mozilla.org/widget/dragservice;1"].getService(Ci.nsIDragService);
    var dragSession = dragService.getCurrentSession();
    if (dragSession.sourceNode)
      return;

    var trans = Cc["@mozilla.org/widget/transferable;1"].createInstance(Ci.nsITransferable);
    trans.addDataFlavor("text/x-moz-url");
    trans.addDataFlavor("application/x-moz-file");

    var uris = [];
    for (var i=0; i<dragSession.numDropItems; i++) {
      var uri = null;

      dragSession.getData(trans, i);
      var flavor = {}, data = {}, length = {};
      trans.getAnyTransferData(flavor, data, length);
      if (data) {
        try {
          var str = data.value.QueryInterface(Ci.nsISupportsString);
        }
        catch(ex) {
        }

        if (str) {
          uri = this._ios.newURI(str.data.split("\n")[0], null, null);
        }
        else {
          var file = data.value.QueryInterface(Ci.nsIFile);
          if (file)
            uri = this._ios.newFileURI(file);
        }
      }

      if (uri)
        uris.push(uri);
    }

    if (WebAppProperties.script.dropFiles)
      WebAppProperties.script.dropFiles(uris);
  },

  _loadExternalURI : function(aURI) {
    var extps = Cc["@mozilla.org/uriloader/external-protocol-service;1"].getService(Ci.nsIExternalProtocolService);
    extps.loadURI(aURI, null);
  },

  _domClick : function(aEvent)
  {
    var link = aEvent.target;

    if (link instanceof HTMLAnchorElement && this._isLinkExternal(link)) {
      aEvent.stopPropagation();
    }
  },

  _domActivate : function(aEvent)
  {
    var link = aEvent.target;

    if (link instanceof HTMLAnchorElement && this._isLinkExternal(link)) {
      // We don't want to open external links in this process: do so in the
      // default browser.
      var resolvedURI = this._ios.newURI(link.href, null, null);

      this._loadExternalURI(resolvedURI);

      aEvent.preventDefault();
      aEvent.stopPropagation();
    }
  },

  _prepareWebAppScript : function()
  {
    // Initialize the platform glue
    var platform = Cc["@mozilla.org/platform-web-api;1"].createInstance(Ci.nsIPlatformGlue);
  
    HostUI._document = document;
    HostUI._window = window;
    
    WebAppProperties.script["XMLHttpRequest"] = Components.Constructor("@mozilla.org/xmlextras/xmlhttprequest;1");
    WebAppProperties.script["window"] = this._getBrowser().contentWindow;
    WebAppProperties.script["properties"] = WebAppProperties;
    WebAppProperties.script["host"] = HostUI;
  },

  startup : function()
  {
    this.clearCache();

    this._ios = Cc["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
    this._ios.offline = false; //force online even no network
    this._tld = Cc["@mozilla.org/network/effective-tld-service;1"].getService(Ci.nsIEffectiveTLDService);

    // Configure the window's chrome
    this._processConfig();

    if (!window.arguments || !window.arguments[0] || !(window.arguments[0] instanceof Ci.nsICommandLine)) {
      // Not the main window, so we're done
      return;
    }

    // Add handlers for the main page
    window.addEventListener("unload", function() { WebRunner.shutdown(); }, false);
    window.addEventListener("minimizing", function(event) { WebRunner.onMinimizing(event); }, false);
    window.addEventListener("closing", function(event) { WebRunner.onClosing(event); }, false);
    window.addEventListener("DOMActivate", function(event) { WebRunner.onActivate(event); }, false);

    var install = false;

    install = window.arguments[0].handleFlag("install-webapp", false);
    if (!install)
      install = (WebAppProperties.uri == null || WebAppProperties.name == null);

    // Hack to get the mime handler initialized correctly so the content handler dialog doesn't appear
    var hs = Cc["@mozilla.org/uriloader/handler-service;1"].getService(Ci.nsIHandlerService);
    var extps = Cc["@mozilla.org/uriloader/external-protocol-service;1"].getService(Ci.nsIExternalProtocolService);

    // Ensure login manager is up and running.
    Cc["@mozilla.org/login-manager;1"].getService(Ci.nsILoginManager);

    // Set the 'http' handler
    var httpHandler = extps.getProtocolHandlerInfo("http");
    httpHandler.preferredAction = Ci.nsIHandlerInfo.useSystemDefault;
    httpHandler.alwaysAskBeforeHandling = false;
    hs.store(httpHandler);

    // Set the 'https' handler
    var httpsHandler = extps.getProtocolHandlerInfo("https");
    httpsHandler.preferredAction = Ci.nsIHandlerInfo.useSystemDefault;
    httpsHandler.alwaysAskBeforeHandling = false;
    hs.store(httpsHandler);

    var self = this;

    this._xulWindow = window.QueryInterface(Ci.nsIInterfaceRequestor)
        .getInterface(Ci.nsIWebNavigation)
        .QueryInterface(Ci.nsIDocShellTreeItem)
        .treeOwner
        .QueryInterface(Ci.nsIInterfaceRequestor)
        .getInterface(Ci.nsIXULWindow);
        
    // Do we need to handle making a web application?
    if (install) {
      // If the install is successful, launch the webapp
      var allowLaunch = {value: true};
      window.openDialog("chrome://newapp/content/install-shortcut.xul", "install", "dialog=no,centerscreen", WebAppProperties, allowLaunch);

      // Hide the main window so it doesn't flash on the screen before closing
      this._xulWindow.QueryInterface(Ci.nsIBaseWindow).visibility = false;

      // Since we are installing, we need to close the application
      window.close();
    }

    // Hookup the browser window callbacks
    this._xulWindow.XULBrowserWindow = this;
    window.QueryInterface(Ci.nsIDOMChromeWindow).browserDOMWindow =
      new nsBrowserAccess(this._getBrowser());

    window.addEventListener("close", function(event) { self._handleWindowClose(event); }, false);

    var browser = this._getBrowser();
    browser.addEventListener("DOMTitleChanged", function(aEvent) { self._domTitleChanged(aEvent); }, true);
    browser.addEventListener("dragover", function(aEvent) { self._dragOver(aEvent); }, true);
    browser.addEventListener("dragdrop", function(aEvent) { self._dragDrop(aEvent); }, true);
    browser.webProgress.addProgressListener(this, Ci.nsIWebProgress.NOTIFY_ALL);

    // Remember the base domain of the web app
    if (WebAppProperties.uri) {
      var uriFixup = Cc["@mozilla.org/docshell/urifixup;1"].getService(Ci.nsIURIFixup);
      var uri = uriFixup.createFixupURI(WebAppProperties.uri, Ci.nsIURIFixup.FIXUP_FLAG_NONE);
      this._currentDomain = this._getBaseDomain(uri);
    }

    // Register ourselves as the default window creator so we can control handling of external links
    this._windowCreator = Cc["@mozilla.org/toolkit/app-startup;1"].getService(Ci.nsIWindowCreator);
    var windowWatcher = Cc["@mozilla.org/embedcomp/window-watcher;1"].getService(Ci.nsIWindowWatcher);
    windowWatcher.setWindowCreator(this);

    document.getElementById("popup_content").addEventListener("popupshowing", self._popupShowing, false);
    document.getElementById("tooltip_content").addEventListener("popupshowing", self._tooltipShowing, false);

    // Let osx make its app menu, then hide the window menu
    var mainMenu = document.getElementById("menu_main");
    if (mainMenu) {
      mainMenu.hidden = true;

      // Needed for linux or the menubar doesn't hide
      document.getElementById("menu_file").hidden = true;
    }

    setTimeout(function() { self._delayedStartup(); }, 0);
  },

  showTrayIcon : function() {
    var appIcon = WebAppProperties.getAppRoot();
    appIcon.append("icons");
    appIcon.append("default");
    appIcon.append(WebAppProperties.icon + ".ico");

    var ioService = Cc["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
    var iconUri = ioService.newFileURI(appIcon);

    var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
    var icon = desktop.getApplicationIcon(this._getBrowser().contentWindow);
    icon.title = document.title;
    icon.imageSpec = iconUri.spec;
  },

  showSplashScreen : function() {
    // Display the splash screen, if any
    if (WebAppProperties.splashscreen) {
      var ioService = Cc["@mozilla.org/network/io-service;1"].getService(Ci.nsIIOService);
      var splashFile = WebAppProperties.getAppRoot();
      splashFile.append(WebAppProperties.splashscreen);
      var splashUri = ioService.newFileURI(splashFile);
      document.getElementById("browser_content").setAttribute("src", splashUri.spec);
    }
  },

  shutdownQuery : function() {
    this._saveSettings();

    return true;
  },

  shutdown : function()
  {
    if (WebAppProperties.trayicon) {
      var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
      var icon = desktop.getApplicationIcon(this._getBrowser().contentWindow);
      icon.hide();
    }

    if (WebAppProperties.script.shutdown)
      WebAppProperties.script.shutdown();

    this.clearCache();
  },
  
  tryClose : function()
  {
    var contentViewer = this._xulWindow.docShell.contentViewer;
    if (contentViewer && !contentViewer.permitUnload()) {
      return false;
    }
  },
  
  onMinimizing : function(event)
  {
    var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
    var icon = desktop.getApplicationIcon(this._getBrowser().contentWindow);
    if (icon.behavior & Ci.nsIApplicationIcon.HIDE_ON_MINIMIZE) {
      this._xulWindow.QueryInterface(Ci.nsIBaseWindow).visibility = false;
    }
  },
  
  onClosing : function(event)
  {
    var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
    var icon = desktop.getApplicationIcon(this._getBrowser().contentWindow);
    if (icon.behavior & Ci.nsIApplicationIcon.HIDE_ON_CLOSE) {
      this._xulWindow.QueryInterface(Ci.nsIBaseWindow).visibility = false;
      event.preventDefault();
    }
  },
  
  onActivate : function(event)
  {
    this._xulWindow.QueryInterface(Ci.nsIBaseWindow).visibility = true;
    
    var desktop = Cc["@mozilla.org/desktop-environment;1"].getService(Ci.nsIDesktopEnvironment);
    desktop.setZLevel(window, Ci.nsIDesktopEnvironment.zLevelTop);
    
    window.QueryInterface(Ci.nsIDOMChromeWindow).restore();
  },

  toggleStatusbar : function()
  {
    var statusbar = document.getElementById("statusbar");
    var collapsed = statusbar.getAttribute("collapsed") == "true";
    statusbar.setAttribute("collapsed", collapsed ? "false" : "true");
  },

  clearCache : function()
  {
     var cacheService = Cc["@mozilla.org/network/cache-service;1"].getService(Ci.nsICacheService);
     try {
       cacheService.evictEntries(Ci.nsICache.STORE_ANYWHERE);
     } catch(ex) {}
  },

  doCommand : function(aCmd) {
    switch (aCmd) {
      case "cmd_cut":
      case "cmd_copy":
      case "cmd_paste":
      case "cmd_delete":
      case "cmd_selectAll":
        goDoCommand(aCmd);
        break;
      case "cmd_copylink":
        var target = document.popupNode;
        if (target instanceof HTMLAnchorElement && target.href) {
          var clipboard = Cc["@mozilla.org/widget/clipboardhelper;1"].getService(Ci.nsIClipboardHelper);
          clipboard.copyString(target.href);
        }
        break;
      case "cmd_print":
        PrintUtils.print();
        break;
      case "cmd_pageSetup":
        PrintUtils.showPageSetup();
        break;
      case "cmd_about":
        window.openDialog("chrome://webrunner/content/about.xul", "about", "centerscreen,modal", WebAppProperties);
        break;
      case "cmd_back":
        this._getBrowser().goBack();
        break;
      case "cmd_forward":
        this._getBrowser().goForward();
        break;
      case "cmd_home":
        this._getBrowser().loadURI(WebAppProperties.uri, null, null);
        break;
      case "cmd_reload":
        this._getBrowser().reload();
        break;
      case "cmd_close":
        if (this.shutdownQuery())
          close();
        break;
      case "cmd_quit":
        if (this.shutdownQuery())
          goQuitApplication();
        break;
      case "cmd_console":
        window.open("chrome://global/content/console.xul", "_blank", "chrome,extrachrome,dependent,menubar,resizable,scrollbars,status,toolbar");
        break;
      case "cmd_install":
        window.openDialog("chrome://newapp/content/install-shortcut.xul", "install", "centerscreen,modal", WebAppProperties);
        break;
      case "cmd_sb":
        this.toggleStatusbar();
        break;
      case "cmd_clearcache":
        this.clearCache();
        this._getBrowser().reload();
        break;
      case "cmd_addons":
        const EMTYPE = "Extension:Manager";

        var aOpenMode = "extensions";
        var wm = Cc["@mozilla.org/appshell/window-mediator;1"].getService(Ci.nsIWindowMediator);
        var needToOpen = true;
        var windowType = EMTYPE + "-" + aOpenMode;
        var windows = wm.getEnumerator(windowType);
        while (windows.hasMoreElements()) {
          var theEM = windows.getNext().QueryInterface(Ci.nsIDOMWindowInternal);
          if (theEM.document.documentElement.getAttribute("windowtype") == windowType) {
            theEM.focus();
            needToOpen = false;
            break;
          }
        }

        if (needToOpen) {
          const EMURL = "chrome://mozapps/content/extensions/extensions.xul?type=" + aOpenMode;
          const EMFEATURES = "chrome,dialog=no,resizable=yes";
          window.openDialog(EMURL, "", EMFEATURES);
        }
        break;
    }
  },

  attachDocument : function(aDocument) {
    var self = this;
    aDocument.addEventListener("click", function(aEvent) { self._domClick(aEvent); }, true);
    aDocument.addEventListener("DOMActivate", function(aEvent) { self._domActivate(aEvent); }, true);
  },

  // nsIXULBrowserWindow implementation to display link destinations in the statusbar
  setJSStatus: function() { },
  setJSDefaultStatus: function() { },
  setOverLink: function(aStatusText, aLink) {
    var statusbar = document.getElementById("status");
    statusbar.label = aStatusText;
  },

  // nsIWebProgressListener implementation to monitor activity in the browser.
  _requestsStarted: 0,
  _requestsFinished: 0,

  // This method is called to indicate state changes.
  onStateChange: function(aWebProgress, aRequest, aStateFlags, aStatus) {
    if (aStateFlags & Ci.nsIWebProgressListener.STATE_IS_REQUEST) {
      if (aStateFlags & Ci.nsIWebProgressListener.STATE_START) {
        this._requestsStarted++;
      }
      else if (aStateFlags & Ci.nsIWebProgressListener.STATE_STOP) {
        this._requestsFinished++;
      }

      if (WebAppProperties.status && this._requestsStarted > 1) {
        var value = (100 * this._requestsFinished) / this._requestsStarted;
        var progress = document.getElementById("progress");
        progress.setAttribute("mode", "determined");
        progress.setAttribute("value", value);
      }
    }

    if (WebAppProperties.status && (aStateFlags & Ci.nsIWebProgressListener.STATE_IS_NETWORK)) {
      var progress = document.getElementById("progress");
      if (aStateFlags & Ci.nsIWebProgressListener.STATE_START) {
        progress.hidden = false;
      }
      else if (aStateFlags & Ci.nsIWebProgressListener.STATE_STOP) {
        progress.hidden = true;
        this.onStatusChange(aWebProgress, aRequest, 0, "Done");
        this._requestsStarted = this._requestsFinished = 0;
      }
    }

    if (aStateFlags & Ci.nsIWebProgressListener.STATE_IS_DOCUMENT) {
      if (aStateFlags & Ci.nsIWebProgressListener.STATE_TRANSFERRING) {
        WebAppProperties.script["window"] = aWebProgress.DOMWindow;
      }
      else if (aStateFlags & Ci.nsIWebProgressListener.STATE_STOP) {
        var domDocument = aWebProgress.DOMWindow.document;
        this.attachDocument(domDocument);
      }
    }
  },

  // This method is called to indicate progress changes for the currently
  // loading page.
  onProgressChange: function(aWebProgress, aRequest, aCurSelf, aMaxSelf, aCurTotal, aMaxTotal) {
    if (WebAppProperties.status && this._requestsStarted == 1) {
      var progress = document.getElementById("progress");
      if (aMaxSelf == -1) {
        progress.setAttribute("mode", "undetermined");
      }
      else {
        var value = ((100 * aCurSelf) / aMaxSelf);
        progress.setAttribute("mode", "determined");
        progress.setAttribute("value", value);
      }
    }
  },

  // This method is called to indicate a change to the current location.
  onLocationChange: function(aWebProgress, aRequest, aLocation) {
    var urlbar = document.getElementById("urlbar");
    urlbar.value = aLocation.spec;

    var browser = this._getBrowser();
    var back = document.getElementById("cmd_back");
    var forward = document.getElementById("cmd_forward");

    back.setAttribute("disabled", !browser.canGoBack);
    forward.setAttribute("disabled", !browser.canGoForward);
  },

  // This method is called to indicate a status changes for the currently
  // loading page.  The message is already formatted for display.
  onStatusChange: function(aWebProgress, aRequest, aStatus, aMessage) {
    if (WebAppProperties.status) {
      var statusbar = document.getElementById("status");
      statusbar.setAttribute("label", aMessage);
    }
  },

  // This method is called when the security state of the browser changes.
  onSecurityChange: function(aWebProgress, aRequest, aState) {
    var security = document.getElementById("security");
    var browser = this._getBrowser();

    security.removeAttribute("label");
    switch (aState) {
      case Ci.nsIWebProgressListener.STATE_IS_SECURE | Ci.nsIWebProgressListener.STATE_SECURE_HIGH:
        security.setAttribute("level", "high");
        security.setAttribute("label", browser.contentWindow.location.host);
        break;
      case Ci.nsIWebProgressListener.STATE_IS_SECURE | Ci.nsIWebProgressListener.STATE_SECURE_MEDIUM:
        security.setAttribute("level", "med");
        security.setAttribute("label", browser.contentWindow.location.host);
        break;
      case Ci.nsIWebProgressListener.STATE_IS_SECURE | Ci.nsIWebProgressListener.STATE_SECURE_LOW:
        security.setAttribute("level", "low");
        security.setAttribute("label", browser.contentWindow.location.host);
        break;
      case Ci.nsIWebProgressListener.STATE_IS_BROKEN:
        security.setAttribute("level", "broken");
        break;
      case Ci.nsIWebProgressListener.STATE_IS_INSECURE:
      default:
        security.removeAttribute("level");
        break;
    }
  },

  createChromeWindow : function(parent, chromeFlags) {
    // Always use the app runner implementation
    return this._windowCreator.createChromeWindow(parent, chromeFlags);
  },

  createChromeWindow2 : function(parent, chromeFlags, contextFlags, uri, cancel) {
    if (uri && (uri.scheme != "chrome") && this._isURIExternal(uri)) {
      // Use default app to open external URIs
      this._loadExternalURI(uri);
      cancel.value = true;
    }
    else {
      return this._windowCreator.QueryInterface(Ci.nsIWindowCreator2).
        createChromeWindow2(parent, chromeFlags, contextFlags, uri, cancel);
    }
  },

  // We need to advertize that we support weak references.  This is done simply
  // by saying that we QI to nsISupportsWeakReference.  XPConnect will take
  // care of actually implementing that interface on our behalf.
  QueryInterface: function(aIID) {
    if (aIID.equals(Ci.nsIWebProgressListener) ||
        aIID.equals(Ci.nsISupportsWeakReference) ||
        aIID.equals(Ci.nsIXULBrowserWindow) ||
        aIID.equals(Ci.nsIWindowCreator) ||
        aIID.equals(Ci.nsIWindowCreator2) ||
        aIID.equals(Ci.nsISupports))
      return this;

    throw Components.results.NS_ERROR_NO_INTERFACE;
  }
};

function nsBrowserAccess(browser)
{
  this._browser = browser;
  this._platform = Cc["@mozilla.org/platform-web-api;1"].createInstance(Ci.nsIPlatformGlue);
}

nsBrowserAccess.prototype =
{
  QueryInterface : function(aIID)
  {
    if (aIID.equals(Ci.nsIBrowserDOMWindow) ||
        aIID.equals(Ci.nsISupports))
      return this;
    throw Components.results.NS_NOINTERFACE;
  },

  openURI : function(aURI, aOpener, aWhere, aContext)
  {
    // Check whether we have a JS callback for this URI
    var callback = {};
    var uriString = this._platform.getProtocolURI(aURI.spec, callback);
    if (callback.value) {
      callback.value.handleURI(aURI.spec);
      // Return a window to abort the load
      return this._browser.contentWindow;
    }

    // Drop through to default implementation
    return null;
  },

  isTabContentWindow : function(aWindow)
  {
    // Shouldn't ever get called
    throw Components.results.NS_ERROR_UNEXPECTED;
  }
}
