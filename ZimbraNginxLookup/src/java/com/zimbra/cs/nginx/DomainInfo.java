package com.zimbra.cs.nginx;

/*
 * virtual IP to domain name mapping
 */
class DomainInfo extends LookupEntry {
    
    private String mDomainName;
    
    DomainInfo(String virtualIP, String domainName) {
        super(virtualIP);
        mDomainName = domainName;
    }
    
    String getDomainName() {
        return mDomainName;
    }
}
