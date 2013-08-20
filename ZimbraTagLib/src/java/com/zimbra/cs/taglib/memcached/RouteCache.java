/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
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

package com.zimbra.cs.taglib.memcached;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.memcached.MemcachedMap;
import com.zimbra.common.util.memcached.MemcachedSerializer;
import com.zimbra.common.util.memcached.ZimbraMemcachedClient;
import com.zimbra.cs.taglib.ZJspSession;

public class RouteCache {

    private static RouteCache sTheInstance = new RouteCache();
    private MemcachedMap<RouteCacheKey, String> mMemcachedLookup;

    public static RouteCache getInstance() { return sTheInstance; }

    RouteCache() {
        ZimbraMemcachedClient memcachedClient = MemcachedConnector.getClient();
        RouteSerializer serializer = new RouteSerializer();
        mMemcachedLookup = new MemcachedMap<RouteCacheKey, String>(memcachedClient, serializer, false);
    }

    private static class RouteSerializer implements MemcachedSerializer<String> {
        @Override
        public Object serialize(String value) {
            return value;
        }

        @Override
        public String deserialize(Object obj) throws ServiceException {
            return (String) obj;
        }
    }

    public String get(String accountId) throws ServiceException {
        RouteCacheKey key = new RouteCacheKey(ZJspSession.isProtocolModeHttps() ?  "https" : "http", accountId);
        return mMemcachedLookup.get(key);
    }

    public void put(String accountId, String route) throws ServiceException {
        RouteCacheKey key = new RouteCacheKey(ZJspSession.isProtocolModeHttps() ?  "https" : "http", accountId);
        mMemcachedLookup.put(key, route);
    }
}
