/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.offline;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.offline.OfflineLC;

public class RemoteAuthCache {
	
	private static class AuthData {
		String password;
	    String token;
	    long expires;
	    long lastFail;
	}
	
	private static Map<String, AuthData> sAccountAuthMap = Collections.synchronizedMap(new HashMap<String, AuthData>());
	
	public static String getAuthToken(Account account) {
		String accountId = account.getId();
		String password = account.getAttr(OfflineProvisioning.A_offlineRemotePassword);
		synchronized (sAccountAuthMap) {
			AuthData authData = sAccountAuthMap.get(accountId);
			if (authData != null) {
				if (authData.token != null && authData.expires > System.currentTimeMillis() && authData.password.equals(password))
					return authData.token;
				else {
					authData.token = null;
					authData.expires = 0;
				}
			}
		}
		return null;
	}
	
	public static boolean reauthOK(Account account) {
		String accountId = account.getId();
		String password = account.getAttr(OfflineProvisioning.A_offlineRemotePassword);
		synchronized (sAccountAuthMap) {
			AuthData authData = sAccountAuthMap.get(accountId);
			if (authData == null || !authData.password.equals(password) || System.currentTimeMillis() - authData.lastFail > OfflineLC.zdesktop_reauth_delay.longValue())
				return true;
		}
		return false;
	}
	
	public static void setAuthToken(Account account, String token, long expires) {
		String accountId = account.getId();
		AuthData authData = new AuthData();
		authData.password = account.getAttr(OfflineProvisioning.A_offlineRemotePassword);
		authData.token = token;
		authData.expires = expires;
		synchronized (sAccountAuthMap) {
			sAccountAuthMap.put(accountId, authData);
		}
	}
	
	public static void authFailed(Account account) {
		String accountId = account.getId();
		AuthData authData = new AuthData();
		authData.password = account.getAttr(OfflineProvisioning.A_offlineRemotePassword);
		authData.lastFail = System.currentTimeMillis();
		synchronized (sAccountAuthMap) {
			sAccountAuthMap.put(accountId, authData);
		}
	}
}
