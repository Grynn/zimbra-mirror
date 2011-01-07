package com.zimbra.cs.security.openid.consumer;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.memcached.MemcachedKey;
import com.zimbra.common.util.memcached.MemcachedMap;
import com.zimbra.common.util.memcached.MemcachedSerializer;
import com.zimbra.cs.memcached.MemcachedConnector;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.UrlIdentifier;

import javax.servlet.http.HttpSession;
import java.net.URL;

/**
 * Cache for sharing DiscoveryInformation objects between servers.
 */
public class DiscoveryInformationCache {

    private MemcachedMap<DiscoveryInformationKey, DiscoveryInformation> cache;

    public DiscoveryInformationCache() {
        cache = new MemcachedMap<DiscoveryInformationKey, DiscoveryInformation>(MemcachedConnector.getClient(),
                                                                                new DiscoveryInformationSerializer());
    }

    public void put(HttpSession session, DiscoveryInformation discovered) throws ServiceException {
        cache.put(new DiscoveryInformationKey(session), discovered);
    }

    public DiscoveryInformation get(HttpSession session) throws ServiceException {
        return cache.get(new DiscoveryInformationKey(session));
    }

    class DiscoveryInformationKey implements MemcachedKey {

        private String keyStr;

        public DiscoveryInformationKey(HttpSession session) {
            keyStr = "DiscoveryInformation-" + session.getAttribute("server-id") + "-" + session.getId();
        }

        @Override
        public String getKeyPrefix() {
            return null;
        }

        @Override
        public String getKeyValue() {
            return keyStr;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DiscoveryInformationKey && keyStr.equals(((DiscoveryInformationKey) obj).keyStr);
        }
    }

    static class DiscoveryInformationSerializer implements MemcachedSerializer<DiscoveryInformation> {

        @Override
        public Object serialize(DiscoveryInformation value) throws ServiceException {
            return new StringBuilder().
                    append(value.getOPEndpoint()).append(",").
                    append(value.getVersion()).append(",").
                    append(value.getClaimedIdentifier() == null ? " " : value.getClaimedIdentifier()).append(",").
                    append(value.getDelegateIdentifier() == null ? " " : value.getDelegateIdentifier()).toString();
        }

        @Override
        public DiscoveryInformation deserialize(Object obj) throws ServiceException {
            String[] parts = ((String) obj).split(",");
            if (parts.length < 4)
                throw ServiceException.FAILURE("Invalid serialized value for DiscoveryInformation", null);
            try {
                return new DiscoveryInformation(new URL(parts[0]),
                                                " ".equals(parts[2]) ? null : new UrlIdentifier(parts[2]),
                                                " ".equals(parts[3]) ? null : parts[3],
                                                parts[1]);
            } catch (Exception e) {
                throw ServiceException.FAILURE("Error in instantiating DiscoveryInformation", e);
            }
        }
    }
}
