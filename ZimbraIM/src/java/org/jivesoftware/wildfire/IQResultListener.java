package org.jivesoftware.wildfire;

import org.xmpp.packet.IQ;

/**
 * An IQResultListener will be invoked when a previously IQ packet sent by the server was answered.
 * Use {@link IQRouter#addIQResultListener(String, IQResultListener)} to add a new listener that
 * will process the answer to the IQ packet being sent. The listener will automatically be
 * removed from the {@link IQRouter} as soon as a reply for the sent IQ packet is received. The
 * reply can be of type RESULT or ERROR.
 *
 * @author Gaston Dombiak
 */
public interface IQResultListener {

    /**
     * Notification method indicating that a previously sent IQ packet has been answered.
     * The received IQ packet might be of type ERROR or RESULT.
     *
     * @param packet the IQ packet answering a previously sent IQ packet.
     */
    void receivedAnswer(IQ packet);
}
