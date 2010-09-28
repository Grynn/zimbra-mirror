/*
 * Copyright 2009 Yutaka Obuchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Original is from example in OAuth Java library(http://oauth.googlecode.com/svn/code/java/)
// and modified for integratin with Zimbra

// Original's copyright and license terms
/*
 * Copyright 2007 AOL, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.oauth.provider.core;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;
import org.apache.commons.codec.digest.DigestUtils;
import sample.oauth.provider.OAuthTokenCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

/**
 * Utility methods for providers that store consumers, tokens and secrets in 
 * local cache (HashSet). Consumer key is used as the name, and its credentials are 
 * stored in HashSet.
 *
 * @author Yutaka Obuchi
 */
public class SampleZmOAuthProvider {

    public static final SimpleOAuthRevAValidator VALIDATOR = new SimpleOAuthRevAValidator();

    //private static final Map<String, OAuthConsumer> ALL_CONSUMERS 
    //                = Collections.synchronizedMap(new HashMap<String,OAuthConsumer>(10));
    
    private static final Collection<OAuthAccessor> ALL_TOKENS = new HashSet<OAuthAccessor>();

    
    public static synchronized OAuthConsumer getConsumer(
            OAuthMessage requestMessage)
            throws IOException, OAuthProblemException {
        
        return getConsumer(requestMessage.getConsumerKey());
    }
    
    public static synchronized OAuthConsumer getConsumer(
            String consumer_key)
            throws IOException, OAuthProblemException {

        String[] registeredConsumers;
        try {
            // TODO - need to lookup the domain first
            registeredConsumers = Provisioning.getInstance().getConfig().getMultiAttr(Provisioning.A_zimbraOAuthConsumerCredentials);
        } catch (ServiceException e) {
            throw new OAuthProblemException("token_rejected");
        }

        OAuthConsumer oAuthConsumer = null;
        for (String consumer : registeredConsumers) {
            String s[] = consumer.split(":");
            if (s.length >= 2 && s[0].equals(consumer_key)) {
                oAuthConsumer = new OAuthConsumer(null, consumer_key, s[1], null);
                oAuthConsumer.setProperty("name", consumer_key);
                oAuthConsumer.setProperty("description", s.length > 2 ? s[2] : "");
                break;
            }
        }

        if (oAuthConsumer == null) {
            throw new OAuthProblemException("token_rejected");
        }
        
        return oAuthConsumer;
    }
    
    /**
     * Get the access token and token secret for the given oauth_token. 
     */
    public static synchronized OAuthAccessor getAccessor(OAuthMessage requestMessage)
            throws IOException, OAuthProblemException,ServiceException {
        
        // try to load from memcache if not throw exception
        String consumer_token = requestMessage.getToken();
        OAuthAccessor accessor = null;
        //for (OAuthAccessor a : SampleZmOAuthProvider.ALL_TOKENS) {
        //    if(a.requestToken != null) {
        //        if (a.requestToken.equals(consumer_token)) {
        //            accessor = a;
        //            break;
        //        }
        //    } else if(a.accessToken != null){
        //        if (a.accessToken.equals(consumer_token)) {
        //            accessor = a;
        //            break;
        //        }
        //    }
        //}
        
        accessor = OAuthTokenCache.get(consumer_token, OAuthTokenCache.REQUEST_TOKEN_TYPE);
        if (accessor == null){
        	accessor = OAuthTokenCache.get(consumer_token, OAuthTokenCache.ACCESS_TOKEN_TYPE);
        }
        
        if(accessor == null){
            OAuthProblemException problem = new OAuthProblemException("token_expired");
            throw problem;
        }
        
        return accessor;
    }

    /**
     * Set the access token 
     */
    public static synchronized void markAsAuthorized(OAuthAccessor accessor, String userId, String zauthtoken)
            throws OAuthException {
        
        
        // first remove the accessor from cache
        //ALL_TOKENS.remove(accessor);
        
        accessor.setProperty("user", userId);   
        accessor.setProperty("authorized", Boolean.TRUE);
        accessor.setProperty("ZM_AUTH_TOKEN", zauthtoken);
        // update token in local cache
        //ALL_TOKENS.add(accessor);
        
        // add to memcache
        //OAuthTokenCache.put(accessor,"REQ");
    }
    

    /**
     * Generate a fresh request token and secret for a consumer.
     * 
     * @throws OAuthException
     */
    public static synchronized void generateRequestToken(
            OAuthAccessor accessor)
            throws OAuthException,ServiceException {

        // generate oauth_token and oauth_secret
        String consumer_key = (String) accessor.consumer.getProperty("name");
        // generate token and secret based on consumer_key
        
        // for now use md5 of name + current time as token
        String token_data = consumer_key + System.nanoTime();
        String token = DigestUtils.md5Hex(token_data);
        // for now use md5 of name + current time + token as secret
        String secret_data = consumer_key + System.nanoTime() + token;
        String secret = DigestUtils.md5Hex(secret_data);
        
        accessor.requestToken = token;
        accessor.tokenSecret = secret;
        accessor.accessToken = null;
        
        // add to the local cache
        //ALL_TOKENS.add(accessor);
        
        // add to memcache
        OAuthTokenCache.put(accessor,OAuthTokenCache.REQUEST_TOKEN_TYPE);
        
    }
    
    /**
     * Generate a fresh request token and secret for a consumer.
     * 
     * @throws OAuthException
     */
    public static synchronized void generateAccessToken(OAuthAccessor accessor)
            throws OAuthException,ServiceException {

        // generate oauth_token and oauth_secret
        String consumer_key = (String) accessor.consumer.getProperty("name");
        // generate token and secret based on consumer_key
        
        // for now use md5 of name + current time as token
        String token_data = consumer_key + System.nanoTime();
        String token = DigestUtils.md5Hex(token_data);
        // first remove the accessor from cache
        //ALL_TOKENS.remove(accessor);
        
        //accessor.requestToken = null;
        accessor.accessToken = token;
        
        // update token in local cache
        //ALL_TOKENS.add(accessor);
        
        // add to memcache
        OAuthTokenCache.put(accessor,OAuthTokenCache.ACCESS_TOKEN_TYPE);
    }

    public static void handleException(Exception e, HttpServletRequest request,
            HttpServletResponse response, boolean sendBody)
            throws IOException, ServletException {
        String realm = (request.isSecure())?"https://":"http://";
        realm += request.getLocalName();
        OAuthServlet.handleException(response, e, realm, sendBody); 
    }
    
    /**
     * Generate a verifier.
     * 
     * @throws OAuthException
     */
    public static synchronized void generateVerifier(
            OAuthAccessor accessor)
            throws OAuthException,ServiceException {

        // generate oauth_verifier
        String consumer_key = (String) accessor.consumer.getProperty("name");
        // generate token and secret based on consumer_key
        
        
        // for now use md5 of name + current time + token as secret
        String verifier_data = consumer_key + System.nanoTime() + accessor.requestToken;
        String verifier = DigestUtils.md5Hex(verifier_data);
        
        ZimbraLog.extensions.debug("generated verifier:"+verifier);
        accessor.setProperty(OAuth.OAUTH_VERIFIER, verifier);
        
        // add to the local cache
        //ALL_TOKENS.add(accessor);
        
        // add to memcache
        OAuthTokenCache.put(accessor,OAuthTokenCache.REQUEST_TOKEN_TYPE);
        
    }

}
