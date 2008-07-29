package com.zimbra.cs.nginx;

public class ServerInfo extends LookupEntry {
    
    private String mHttpPort;
    private String mHttpAdminPort;
    private String mHttpPop3Port;
    private String mHttpPop3SSLPort;
    private String mHttpImapPort;
    private String mHttpImapSSLPort;
    
    ServerInfo(String serverName) {
        super(serverName);
    }
    
    void setHttpPort(String port) {
        mHttpPort = port;
    }
    
    void setHttpAdminPort(String port) {
        mHttpAdminPort = port;
    }
    
    void setPop3Port(String port) {
        mHttpPop3Port = port;
    }
    
    void setPop3SSLPort(String port) {
        mHttpPop3SSLPort = port;
    }
    
    void setImapPort(String port) {
        mHttpImapPort = port;
    }
    
    void setImapSSLPort(String port) {
        mHttpImapSSLPort = port;
    }
    
    String getPortForProto(String proto, boolean isZimbraAdmin) {
        if (NginxLookupExtension.NginxLookupHandler.IMAP.equalsIgnoreCase(proto))
            return mHttpImapPort;
        else if (NginxLookupExtension.NginxLookupHandler.IMAP_SSL.equalsIgnoreCase(proto))
            return mHttpImapSSLPort;
        else if (NginxLookupExtension.NginxLookupHandler.POP3.equalsIgnoreCase(proto))
            return mHttpPop3Port;
        else if (NginxLookupExtension.NginxLookupHandler.POP3_SSL.equalsIgnoreCase(proto))
            return mHttpPop3SSLPort;
        else if (NginxLookupExtension.NginxLookupHandler.HTTP.equalsIgnoreCase(proto)) {
            if (isZimbraAdmin) {
                return mHttpAdminPort;
            } else {
                return mHttpPort;
            }
        }
        
        return null;
    }
}

