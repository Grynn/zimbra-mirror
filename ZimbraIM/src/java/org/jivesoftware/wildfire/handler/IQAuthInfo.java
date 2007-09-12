package org.jivesoftware.wildfire.handler;

import org.jivesoftware.wildfire.auth.UnauthorizedException;

/**
 * Information for controlling the authentication options for the server.
 *
 * @author Iain Shigeoka
 */
public interface IQAuthInfo {

    /**
     * Returns true if anonymous authentication is allowed.
     *
     * @return true if anonymous logins are allowed
     */
    public boolean isAnonymousAllowed();

    /**
     * Changes the server's support for anonymous authentication.
     *
     * @param isAnonymous True if anonymous logins should be allowed.
     * @throws UnauthorizedException If you don't have permission to adjust this setting
     */
    public void setAllowAnonymous(boolean isAnonymous) throws UnauthorizedException;
}