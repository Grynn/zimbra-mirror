/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
user_pref("spellchecker.dictionary", "@install.locale@");

user_pref("browser.cache.disk.capacity", "12288");
user_pref("browser.cache.memory.capacity", "3072");
user_pref("browser.sessionhistory.max_total_viewers", "0");
user_pref("config.trim_on_minimize", "true");
user_pref("geo.enabled", "false");
user_pref("keyword.enabled", "false");
user_pref("network.http.keep-alive-timeout", "600");
user_pref("network.http.max-connections-per-server", "6");
user_pref("network.http.max-persistent-connections-per-server", "4");
user_pref("network.http.request.max-start-delay", "1");
user_pref("network.http.pipelining", "true");
user_pref("network.http.pipelining.firstrequest", "true");
user_pref("network.http.pipelining.maxrequests", "3");
user_pref("network.prefetch-next", "false");

user_pref("capability.principal.codebase.p1.granted", "UniversalXPConnect UniversalBrowserRead UniversalBrowserWrite UniversalPreferencesRead UniversalPreferencesWrite UniversalFileRead");
user_pref("capability.principal.codebase.p1.subjectName", "");
user_pref("signed.applets.codebase_principal_support", true);

// let prism forget the last printer used so that the system default printer can get selected
user_pref("print.print_printer", "");

// set it to true to enable about:config access. use shift-f7 to toggle it.
user_pref("prism.shortcut.aboutConfig.enabled", true);

// disable quick-find
user_pref("accessibility.typeaheadfind", false);
user_pref("accessibility.typeaheadfind.flashBar", 0); 
