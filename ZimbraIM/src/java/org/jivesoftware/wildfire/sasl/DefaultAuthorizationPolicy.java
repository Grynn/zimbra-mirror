package org.jivesoftware.wildfire.sasl;

import org.jivesoftware.wildfire.XMPPServer;

/**
 * This policy will authorize any principal that matches exactly the full
 * JID (REALM and server name must be the same if using GSSAPI) or any
 * principal that matches exactly the username (without REALM or server
 * name). This does exactly what users expect if not supplying a seperate
 * principal for authentication.
 *
 * @author Jay Kline
 */
public class DefaultAuthorizationPolicy extends AbstractAuthorizationPolicy
        implements AuthorizationProvider {

    public DefaultAuthorizationPolicy() {
    }

    /**
     * Returns true if the principal is explicity authorized to the JID
     *
     * @param username  The username requested.
     * @param principal The principal requesting the username.
     * @return true is the user is authorized to be principal
     */
    public boolean authorize(String username, String principal) {
        return (principal.equals(username) || 
                    principal.equals(username + "@" + XMPPServer.getInstance().getServerInfo().getDefaultName()));
    }

    /**
     * Returns the short name of the Policy
     *
     * @return The short name of the Policy
     */
    public String name() {
        return "Default Policy";
    }

    /**
     * Returns a description of the Policy
     *
     * @return The description of the Policy.
     */
    public String description() {
        return "This policy will authorize any principal that matches exactly the full " +
                "JID (REALM and server name must be the same if using GSSAPI) or any principal " +
                "that matches exactly the username (without REALM or server name). This does " +
                "exactly what users expect if not supplying a seperate principal for authentication.";
    }
}
    