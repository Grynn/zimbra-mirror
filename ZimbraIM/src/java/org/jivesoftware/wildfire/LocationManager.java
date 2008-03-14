package org.jivesoftware.wildfire;

import org.jivesoftware.wildfire.user.UserNotFoundException;
import org.xmpp.packet.JID;

/**
 * This class handles intra-cloud routing of users. 
 */
public interface LocationManager {
    public boolean isLocal(JID jid) throws UserNotFoundException;
    public boolean isRemote(JID jid)  throws UserNotFoundException;
}
