package org.jivesoftware.wildfire;

import org.xmpp.packet.Message;
import org.dom4j.Element;

import java.util.Date;

/**
 * Subclass of Message that keeps the date when the offline message was stored in the database.
 * The creation date and the user may be used as a unique identifier of the offline message.
 *
 * @author Gaston Dombiak
 */
public class OfflineMessage extends Message {

    private Date creationDate;

    public OfflineMessage(Date creationDate, Element element) {
        super(element);
        this.creationDate = creationDate;
    }

    /**
     * Returns the date when the offline message was stored in the database.
     *
     * @return the date the offline message was stored.
     */
    public Date getCreationDate() {
        return creationDate;
    }
}
