/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.security.openid.consumer;

import com.zimbra.common.util.memcached.ZimbraMemcachedClient;
import com.zimbra.cs.memcached.MemcachedConnector;
import org.openid4java.consumer.AbstractNonceVerifier;

import java.util.Date;

/**
 */
public class MemcachedNonceVerifier extends AbstractNonceVerifier {

    private static final String KEY_PREFIX = "zmOpenidConsumerNonce:";
    private static ZimbraMemcachedClient memcachedClient = MemcachedConnector.getClient();

    public MemcachedNonceVerifier(int maxAgeSecs) {
        super(maxAgeSecs);
    }

    /**
     * Subclasses should implement this method and check if the nonce was seen before.
     * The nonce timestamp was verified at this point, it is valid and it is in the max age boudary.
     *
     * @param now The timestamp used to check the max age boudary.
     */
    @Override
    protected int seen(Date now, String opUrl, String nonce) {
        if (opUrl.equals(memcachedClient.get(KEY_PREFIX + nonce)))
            return SEEN;
        memcachedClient.put(KEY_PREFIX + nonce, opUrl, false);
        return OK;
    }
}
