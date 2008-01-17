package org.jivesoftware.wildfire;

import org.xmpp.packet.JID;

/**
 * This class handles intra-cloud routing of users. 
 */
public interface LocationManager {
    public boolean isLocal(JID jid);
    public boolean isRemote(JID jid);
}
