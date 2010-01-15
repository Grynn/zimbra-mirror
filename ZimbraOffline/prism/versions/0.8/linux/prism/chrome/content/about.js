/* ***** BEGIN LICENSE BLOCK *****
/* Zimbra Collaboration Suite Server
/* Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
/* 
/* The contents of this file are subject to the Zimbra Public License
/* Version 1.3 ("License"); you may not use this file except in
/* compliance with the License.  You may obtain a copy of the License at
/* http://www.zimbra.com/license.
/* 
/* Software distributed under the License is distributed on an "AS IS"
/* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK ***** */

const Cc = Components.classes;
const Ci = Components.interfaces;

addEventListener("load", onload, false);

function onload(aEvent)
{
  if (aEvent.target != document)
    return;

  var bundle = Cc["@mozilla.org/intl/stringbundle;1"].getService(Ci.nsIStringBundleService);
  bundle = bundle.createBundle("chrome://webrunner/locale/brand/brand.properties");

  var appInfo = Cc["@mozilla.org/xre/app-info;1"].getService(Ci.nsIXULAppInfo);

  var version = document.getElementById("version");
  version.value = bundle.GetStringFromName("brandFullName") + " " + appInfo.version;

  var userAgent = document.getElementById("useragent");
  userAgent.value = navigator.userAgent;

  document.documentElement.getButton("accept").focus();
}
