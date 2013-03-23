/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2009, 2010 VMware, Inc.
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

/*
 * This package consists of Ajax files needed to display the
 * login page and handle the login process.
 */
AjxPackage.require("ajax.core.AjxCore");
AjxPackage.require("ajax.core.AjxException");

AjxPackage.require("ajax.util.AjxUtil");
AjxPackage.require("ajax.util.AjxCookie");
AjxPackage.require("ajax.util.AjxVector");
AjxPackage.require("ajax.util.AjxTimedAction");
AjxPackage.require("ajax.util.AjxWindowOpener");
AjxPackage.require("ajax.util.AjxStringUtil");
AjxPackage.require("ajax.util.AjxText");
AjxPackage.require("ajax.util.AjxEmailAddress");

AjxPackage.require("ajax.xml.AjxXmlDoc");

AjxPackage.require("ajax.soap.AjxSoapException");
AjxPackage.require("ajax.soap.AjxSoapFault");
AjxPackage.require("ajax.soap.AjxSoapDoc");

AjxPackage.require("ajax.net.AjxRpcRequest");
AjxPackage.require("ajax.net.AjxRpc");

AjxPackage.require("ajax.debug.AjxDebug");
AjxPackage.require("ajax.debug.AjxDebugXmlDocument");

AjxPackage.require("ajax.events.AjxEvent");
AjxPackage.require("ajax.events.AjxEventMgr");

AjxPackage.require("ajax.dwt.core.Dwt");
