/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.nginx;

import java.util.List;
import java.util.Map;

import com.zimbra.common.util.MapUtil;

public class NginxLookupCache<E extends LookupEntry> {
    
    private Map mCache;
    private long mRefreshTTL;

    static class CacheEntry<E extends LookupEntry> {
        long mLifetime;
        E mEntry;
        CacheEntry(E entry, long expires) {
            mEntry = entry;
            mLifetime = System.currentTimeMillis() + expires;
        }
        
        boolean isStale() {
            return mLifetime < System.currentTimeMillis();
        }
    }
    
    /**
     * @param maxItems
     * @param refreshTTL
     */
    public NginxLookupCache(int maxItems, long refreshTTL) {
        mCache = MapUtil.newLruMap(maxItems);
        mRefreshTTL = refreshTTL;
    }

    public synchronized void clear() {
        mCache.clear();
    }

    public synchronized void remove(String name) {
        mCache.remove(name);
    }
    
    public synchronized void remove(E entry) {
        if (entry != null) {
            mCache.remove(entry.getKey());
        }
    }
    
    public synchronized void put(E entry) {
        if (entry != null) {
            CacheEntry<E> cacheEntry = new CacheEntry<E>(entry, mRefreshTTL);
            mCache.put(entry.getKey(), cacheEntry);
        }
    }

    /*
    public synchronized void put(List<E> entries, boolean clear) {
        if (entries != null) {
            if (clear) clear();
            for (E e: entries)
                put(e);
        }
    }
    */
    
    @SuppressWarnings("unchecked")
    public synchronized E get(String key) {
        CacheEntry<E> ce = (CacheEntry<E>)mCache.get(key);
        if (ce != null) {
            if (mRefreshTTL != 0 && ce.isStale()) {
                remove(ce.mEntry);
                return null;
            } else {
                return ce.mEntry;
            }
        } else {
            return null;
        }
    }
}
