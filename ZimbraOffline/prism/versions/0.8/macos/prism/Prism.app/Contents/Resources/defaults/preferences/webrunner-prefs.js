pref("toolkit.defaultChromeURI", "chrome://webrunner/content/webrunner.xul");  // - main xul window
pref("browser.chromeURL", "chrome://webrunner/content/webrunner.xul");         // - allow popup windows to open

pref("general.useragent.extra.prism", "prism/zdesktop");

/* debugging prefs */
pref("browser.dom.window.dump.enabled", true);
pref("javascript.options.showInConsole", true);
pref("javascript.options.strict", true);
pref("nglayout.debug.disable_xul_cache", false);
pref("nglayout.debug.disable_xul_fastload", false);

/* default security dialogs like firefox */
pref("security.warn_entering_secure.show_once", false);
pref("security.warn_leaving_secure.show_once", false);
pref("security.warn_submit_insecure.show_once", false);

/* disable warnings when opening external links */
pref("network.protocol-handler.warn-external.http", false);
pref("network.protocol-handler.warn-external.https", false);
pref("network.protocol-handler.warn-external.ftp", false);

/* download manager */
pref("browser.download.useDownloadDir", true);
pref("browser.download.folderList", 0);
pref("browser.download.manager.showAlertOnComplete", true);
pref("browser.download.manager.showAlertInterval", 2000);
pref("browser.download.manager.retention", 2);
pref("browser.download.manager.showWhenStarting", true);
pref("browser.download.manager.useWindow", true);
pref("browser.download.manager.closeWhenDone", true);
pref("browser.download.manager.openDelay", 2000);
pref("browser.download.manager.focusWhenStarting", false);
pref("browser.download.manager.flashCount", 2);

/* download alerts */
pref("alerts.slideIncrement", 1);
pref("alerts.slideIncrementTime", 10);
pref("alerts.totalOpenTime", 6000);
pref("alerts.height", 50);

/* password manager */
pref("signon.rememberSignons", true);
pref("signon.expireMasterPassword", false);
pref("signon.SignonFileName", "signons.txt");

pref("layout.spellcheckDefault", 1);

pref("dom.max_script_run_time", 20);

pref("app.update.enabled", true);
pref("app.update.auto", true);
pref("app.update.mode", 3);
pref("app.update.timer", 600000);
pref("app.update.interval", 86400);
pref("app.update.nagTimer.download", 86400);
pref("app.update.nagTimer.restart", 86400);
pref("app.update.idletime", 60);
pref("app.update.lastUpdateDate.background-update-timer", 0);

pref("app.update.silent", false);
pref("app.update.log.all", true);
pref("app.update.url.manual", "http://www.zimbra.com/products/desktop.html");
pref("app.update.url.details", "http://www.zimbra.com/products/desktop.html");
pref("app.update.url", "http://www.zimbra.com/update/zdesktop/%VERSION%/%BUILD_ID%/%OS%/update.xml");

pref("browser.dom.window.dump.enabled", true);
