package org.jivesoftware.wildfire;

import org.xmpp.packet.Packet;
import org.jivesoftware.wildfire.auth.UnauthorizedException;

/**
 * Delivers packets to locally connected streams. This is the opposite
 * of the packet transporter.
 *
 * @author Iain Shigeoka
 */
public interface PacketDeliverer {

    /**
     * Delivers the given packet based on packet recipient and sender. The
     * deliverer defers actual routing decisions to other classes.
     * <h2>Warning</h2>
     * Be careful to enforce concurrency DbC of concurrent by synchronizing
     * any accesses to class resources.
     *
     * @param packet the packet to route
     * @throws PacketException if the packet is null or the packet could not be routed.
     */
    public void deliver(Packet packet) throws UnauthorizedException, PacketException;
}
