package sample.oauth.provider;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.util.memcached.MemcachedSerializer;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import sample.oauth.provider.core.SampleZmOAuthProvider;

/**
 */
public class OAuthAccessorSerializer implements MemcachedSerializer<OAuthAccessor> {

    public Object serialize(OAuthAccessor value) {

        String consumer_key = (String) value.consumer.getProperty("name");
        String token_secret = value.tokenSecret;
        String callback = (String) value.getProperty(OAuth.OAUTH_CALLBACK);
        String user = (String) value.getProperty("user");
        String authorized;
        if (value.getProperty("authorized") != null) {
            authorized = value.getProperty("authorized").toString();
        } else {
            authorized = null;
        }
        String zauthtoken = (String) value.getProperty("ZM_AUTH_TOKEN");
        String verifier = (String) value.getProperty(OAuth.OAUTH_VERIFIER);

        String result = "consumer_key:" + consumer_key + ",token_secret:" + token_secret + //
                ",callback:" + callback + ",user:" + user + ",authorized:" + authorized + //
                ",zauthtoken:" + zauthtoken + ",verifier:" + verifier;
        //return value.encode().toString();

        ZimbraLog.extensions.debug("put value: " + result + "  into memcache.");

        return result;
    }

    public OAuthAccessor deserialize(Object obj) throws ServiceException {

        String value = (String) obj;
        ZimbraLog.extensions.debug("get value: " + value + "  from memcache.");
        String consumer_key = value.substring(0, value.indexOf(",token_secret")).substring(13);
        String token_secret = value.substring(value.indexOf(",token_secret"), value.indexOf(",callback")).substring(14);
        String callback = value.substring(value.indexOf(",callback"), value.indexOf(",user")).substring(10);
        String user = value.substring(value.indexOf(",user"), value.indexOf(",authorized")).substring(6);
        String authorized = value.substring(value.indexOf(",authorized"), value.indexOf(",zauthtoken")).substring(12);
        String zauthtoken = value.substring(value.indexOf(",zauthtoken"), value.indexOf(",verifier")).substring(12);
        String verifier = value.substring(value.indexOf(",verifier")).substring(10);

        ZimbraLog.extensions.debug("consumer_key:" + consumer_key);
        ZimbraLog.extensions.debug("callback:" + callback);
        ZimbraLog.extensions.debug("user:" + user);
        ZimbraLog.extensions.debug("authorized:" + authorized);
        ZimbraLog.extensions.debug("zauthtoken:" + zauthtoken);
        ZimbraLog.extensions.debug("verifier:" + verifier);

        try {
            OAuthConsumer consumer = SampleZmOAuthProvider.getConsumer(consumer_key);
            OAuthAccessor accessor = new OAuthAccessor(consumer);
            accessor.tokenSecret = token_secret;
            accessor.setProperty(OAuth.OAUTH_CALLBACK, callback);

            if (!user.equals("null")) {
                accessor.setProperty("user", user);
            }

            if (authorized.equalsIgnoreCase(Boolean.FALSE.toString())) {
                accessor.setProperty("authorized", Boolean.FALSE);
            } else if (authorized.equalsIgnoreCase(Boolean.TRUE.toString())) {
                accessor.setProperty("authorized", Boolean.TRUE);
            }

            if (!zauthtoken.equals("null")) {
                accessor.setProperty("ZM_AUTH_TOKEN", zauthtoken);
            }

            if (!verifier.equals("null")) {
                accessor.setProperty(OAuth.OAUTH_VERIFIER, verifier);
            }


            return accessor;

        } catch (Exception e) {
            //need more hack here for hadnling IOException properly
            throw ServiceException.FAILURE("IOException", e);
        }
    }
}
