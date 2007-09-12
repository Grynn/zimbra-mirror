package org.jivesoftware.wildfire.audit;

import org.jivesoftware.wildfire.StreamID;
import org.jivesoftware.wildfire.StreamIDFactory;
import org.jivesoftware.wildfire.spi.BasicStreamIDFactory;

/**
 * Factory for producing audit stream IDs. We use a factory so that
 * audit information can be identified using an appropriate storage
 * key (typically a long for RDBMS).
 *
 * @author Iain Shigeoka
 */
public class AuditStreamIDFactory implements StreamIDFactory {

    private BasicStreamIDFactory factory = new BasicStreamIDFactory();

    public AuditStreamIDFactory() {
    }

    public StreamID createStreamID() {
        return factory.createStreamID();
    }
}
