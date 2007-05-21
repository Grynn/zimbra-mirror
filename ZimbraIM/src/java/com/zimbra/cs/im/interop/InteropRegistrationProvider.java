package com.zimbra.cs.im.interop;

import java.io.IOException;
import java.util.Map;

import org.xmpp.packet.JID;

public interface InteropRegistrationProvider {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    
    public Map<String, String> getIMGatewayRegistration(JID userJID, Interop.ServiceName service) throws IOException;
    
    public void putIMGatewayRegistration(JID userJID, Interop.ServiceName service, Map<String, String> data) throws IOException;
}
