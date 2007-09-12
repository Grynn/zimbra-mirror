package org.jivesoftware.wildfire.user;

import org.xmpp.packet.JID;

/**
 * Interface to be implemented by components that are capable of returning the name of entities
 * when running as internal components.
 *
 * @author Gaston Dombiak
 */
public interface UserNameProvider {

    /**
     * Returns the name of the entity specified by the following JID.
     *
     * @param entity JID of the entity to return its name.
     * @return the name of the entity specified by the following JID.
     */
    abstract String getUserName(JID entity);
}
