/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
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
function FindProxyForURL(url, host)
{
// variable strings to return
var proxy_yes = "PROXY localhost:4444";
var proxy_no = "DIRECT";
if (shExpMatch(url, "*selenium-server*")) { return proxy_yes; }

// Dont Proxy anything else
return proxy_no;
}