package org.jivesoftware.wildfire.event;

import org.jivesoftware.wildfire.user.User;

import java.util.Map;

/**
 * An abstract adapter class for receiving user events. 
 * The methods in this class are empty. This class exists as convenience for creating listener objects.
 */
public class UserEventAdapter implements UserEventListener  {
    public void userCreated(User user, Map params) {
    }

    public void userDeleting(User user, Map params) {
    }

    public void userModified(User user, Map params) {
    }
}
