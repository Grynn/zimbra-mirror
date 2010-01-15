/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.spi;

import org.jivesoftware.wildfire.StreamID;
import org.jivesoftware.wildfire.StreamIDFactory;
import java.util.Random;

/**
 * A basic stream ID factory that produces id's using java.util.Random
 * and a simple hex representation of a random int.
 *
 * @author Iain Shigeoka
 */
public class BasicStreamIDFactory implements StreamIDFactory {

    /**
     * The random number to use, someone with Java can predict stream IDs if they can guess the current seed *
     */
    Random random = new Random();

    public StreamID createStreamID() {
        return new BasicStreamID(Integer.toHexString(random.nextInt()));
    }

    public StreamID createStreamID(String name) {
        return new BasicStreamID(name);
    }

    private class BasicStreamID implements StreamID {
        String id;

        public BasicStreamID(String id) {
            this.id = id;
        }

        public String getID() {
            return id;
        }

        public String toString() {
            return id;
        }

        public int hashCode() {
            return id.hashCode();
        }
    }
}
