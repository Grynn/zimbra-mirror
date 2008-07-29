package com.zimbra.cs.nginx;

public abstract class LookupEntry {
    private String mKey;
    
    LookupEntry(String key) {
        mKey = key;
    }
    
    String getKey() {
        return mKey;
    }
}
