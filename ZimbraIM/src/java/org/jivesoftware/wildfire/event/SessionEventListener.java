package org.jivesoftware.wildfire.event;

import org.jivesoftware.wildfire.Session;

/**
 * Interface to listen for session events. Use the
 * {@link SessionEventDispatcher#addListener(SessionEventListener)}
 * method to register for events.
 *
 * @author Matt Tucker
 */
public interface SessionEventListener {

    /**
     * A session was created.
     *
     * @param session the session.
     */
    public void sessionCreated(Session session);    

    /**
     * A session was destroyed
     *
     * @param session the session.
     */
    public void sessionDestroyed(Session session);

    /**
     * An anonymous session was created.
     *
     * @param session the session.
     */
    public void anonymousSessionCreated(Session session);
    
    /**
     * An anonymous session was created.
     *
     * @param session the session.
     */
    public void anonymousSessionDestroyed(Session session);
}