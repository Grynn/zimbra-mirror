package org.jivesoftware.wildfire.event;

import org.jivesoftware.wildfire.user.User;

import java.util.Map;

/**
 * Interface to listen for group events. Use the
 * {@link UserEventDispatcher#addListener(UserEventListener)}
 * method to register for events.
 *
 * @author Matt Tucker
 */
public interface UserEventListener {

    /**
     * A user was created.
     *
     * @param user the user.
     * @param params event parameters.
     */
    public void userCreated(User user, Map params);

    /**
     * A user is being deleted.
     *
     * @param user the user.
     * @param params event parameters.
     */
    public void userDeleting(User user, Map params);

    /**
     * A user's name, email, or an extended property was changed.
     *
     * @param user the user.
     * @param params event parameters.
     */
    public void userModified(User user, Map params);
}