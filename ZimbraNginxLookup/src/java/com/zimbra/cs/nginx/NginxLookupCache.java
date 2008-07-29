package com.zimbra.cs.nginx;

import java.util.List;

import org.apache.commons.collections.map.LRUMap;

public class NginxLookupCache<E extends LookupEntry> {
    
    private LRUMap mCache;
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
        mCache = new LRUMap(maxItems);
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
