/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010 VMware, Inc.
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
 * Support for a generic login page, error dialog,
 * splash screen, and for sending requests to the server.
 */
AjxPackage.require("zimbra.csfe.ZmBatchCommand");
AjxPackage.require("zimbra.csfe.ZmCsfeCommand");
AjxPackage.require("zimbra.csfe.ZmCsfeException");
AjxPackage.require("zimbra.csfe.ZmCsfeResult");
AjxPackage.require("zimbra.common.ZmBaseSplashScreen");
AjxPackage.require("zimbra.common.ZmErrorDialog");
AjxPackage.require("zimbra.common.ZLoginFactory");
