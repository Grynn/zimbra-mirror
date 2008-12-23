package com.zimbra.cs.nginx;

public class DomainExternalRouteInfo extends LookupEntry {
    
    private String mPop3Port;
    private String mPop3SSLPort;
    private String mImapPort;
    private String mImapSSLPort;
    private String mPop3Hostname;
    private String mPop3SSLHostname;
    private String mImapHostname;
    private String mImapSSLHostname;
    
    DomainExternalRouteInfo(String domainName,
                            String pop3Port,
                            String pop3SSLPort,
                            String imapPort,
                            String imapSSLPort,
                            String pop3Hostname,
                            String pop3SSLHostname,
                            String imapHostname,
                            String imapSSLHostname) {
        super(domainName);
        
        mPop3Port        = pop3Port;
        mPop3SSLPort     = pop3SSLPort;
        mImapPort        = imapPort;
        mImapSSLPort     = imapSSLPort;
        mPop3Hostname    = pop3Hostname;
        mPop3SSLHostname = pop3SSLHostname;
        mImapHostname    = imapHostname;
        mImapSSLHostname = imapSSLHostname;
    }

    String getHostname(String proto) {
        if (NginxLookupExtension.NginxLookupHandler.POP3.equalsIgnoreCase(proto))
            return mPop3Hostname;
        else if (NginxLookupExtension.NginxLookupHandler.POP3_SSL.equalsIgnoreCase(proto))
            return mPop3SSLHostname;
        else if (NginxLookupExtension.NginxLookupHandler.IMAP.equalsIgnoreCase(proto))
            return mImapHostname;
        else if (NginxLookupExtension.NginxLookupHandler.IMAP_SSL.equalsIgnoreCase(proto))
            return mImapSSLHostname;
        else
            return null;
    }
    
    String getPort(String proto) {
        if (NginxLookupExtension.NginxLookupHandler.POP3.equalsIgnoreCase(proto))
            return mPop3Port;
        else if (NginxLookupExtension.NginxLookupHandler.POP3_SSL.equalsIgnoreCase(proto))
            return mPop3SSLPort;
        else if (NginxLookupExtension.NginxLookupHandler.IMAP.equalsIgnoreCase(proto))
            return mImapPort;
        else if (NginxLookupExtension.NginxLookupHandler.IMAP_SSL.equalsIgnoreCase(proto))
            return mImapSSLPort;
        else
            return null;
    }

}

