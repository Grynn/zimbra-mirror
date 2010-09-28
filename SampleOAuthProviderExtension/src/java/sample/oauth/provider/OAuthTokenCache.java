/*
 * Copyright 2010 Yutaka Obuchi
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

package sample.oauth.provider;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.util.memcached.MemcachedMapPlusPutWithExtraParam;
import com.zimbra.common.util.memcached.MemcachedSerializer;
import com.zimbra.common.util.memcached.ZimbraMemcachedClient;
import com.zimbra.cs.memcached.MemcachedConnector;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import sample.oauth.provider.core.SampleZmOAuthProvider;

public class OAuthTokenCache {
    
    private static OAuthTokenCache sTheInstance = new OAuthTokenCache();
    
    private MemcachedMapPlusPutWithExtraParam<OAuthTokenCacheKey, OAuthAccessor> mMemcachedLookup;

    public static OAuthTokenCache getInstance() { return sTheInstance; }
    
    public static final String REQUEST_TOKEN_TYPE = "req_token";
    public static final String ACCESS_TOKEN_TYPE = "access_token";
    
    public static final int OAUTH_TOKEN_EXPIRY = 300;
    

    OAuthTokenCache() {
        ZimbraMemcachedClient memcachedClient = MemcachedConnector.getClient();
        OAuthAccessorSerializer serializer = new OAuthAccessorSerializer();
        mMemcachedLookup = new MemcachedMapPlusPutWithExtraParam<OAuthTokenCacheKey, OAuthAccessor>(memcachedClient, serializer); 
    }

    private static class OAuthAccessorSerializer implements MemcachedSerializer<OAuthAccessor> {
        
        public Object serialize(OAuthAccessor value) {
        	
        	String consumer_key = (String) value.consumer.getProperty("name");
        	String token_secret = value.tokenSecret;
        	String callback = (String) value.getProperty(OAuth.OAUTH_CALLBACK);
        	String user = (String) value.getProperty("user");
        	String authorized;
        	if(value.getProperty("authorized")!=null){
        		authorized = ((Boolean) value.getProperty("authorized")).toString();
        	}else{
        		authorized = null;
        	}
            String zauthtoken = (String) value.getProperty("ZM_AUTH_TOKEN");
            String verifier = (String) value.getProperty(OAuth.OAUTH_VERIFIER);
            
            String result = "consumer_key:" + consumer_key + ",token_secret:" + token_secret + //
                            ",callback:" + callback + ",user:" + user + ",authorized:" + authorized + //
                            ",zauthtoken:" + zauthtoken + ",verifier:" + verifier;
            //return value.encode().toString();
            
            ZimbraLog.extensions.debug("put value: "+result+"  into memcache.");
            
        	return result;
        }

        public OAuthAccessor deserialize(Object obj) throws ServiceException{
            
        	String value = (String) obj;
        	ZimbraLog.extensions.debug("get value: "+value+"  from memcache.");
        	String consumer_key = value.substring(0,value.indexOf(",token_secret")).substring(13);
        	String token_secret = value.substring(value.indexOf(",token_secret"),value.indexOf(",callback")).substring(14);
        	String callback = value.substring(value.indexOf(",callback"),value.indexOf(",user")).substring(10);
        	String user = value.substring(value.indexOf(",user"),value.indexOf(",authorized")).substring(6); 
            String authorized = value.substring(value.indexOf(",authorized"),value.indexOf(",zauthtoken")).substring(12);
            String zauthtoken = value.substring(value.indexOf(",zauthtoken"),value.indexOf(",verifier")).substring(12);
            String verifier = value.substring(value.indexOf(",verifier")).substring(10);
        	
            ZimbraLog.extensions.debug("consumer_key:"+consumer_key);
            ZimbraLog.extensions.debug("callback:"+callback);
            ZimbraLog.extensions.debug("user:"+user);
            ZimbraLog.extensions.debug("authorized:"+authorized);
            ZimbraLog.extensions.debug("zauthtoken:"+zauthtoken);
            ZimbraLog.extensions.debug("verifier:"+verifier);
            
        	try{
        	OAuthConsumer consumer = SampleZmOAuthProvider.getConsumer(consumer_key);
        	OAuthAccessor accessor = new OAuthAccessor(consumer);
        	accessor.tokenSecret = token_secret;
        	accessor.setProperty(OAuth.OAUTH_CALLBACK, callback);
        	
        	if(!user.equals("null")){
        		accessor.setProperty("user", user);
        	}
        	
        	if(authorized.equalsIgnoreCase(Boolean.FALSE.toString())){
        		accessor.setProperty("authorized", Boolean.FALSE);
        	}else if(authorized.equalsIgnoreCase(Boolean.TRUE.toString())){
        		accessor.setProperty("authorized", Boolean.TRUE);
        	}
        	
        	if(!zauthtoken.equals("null")){
        		accessor.setProperty("ZM_AUTH_TOKEN", zauthtoken);
        	}
            
        	if(!verifier.equals("null")){
        		accessor.setProperty(OAuth.OAUTH_VERIFIER, verifier);
        	}
        	
        	
            return accessor;
        	
        	}catch(Exception e){
        		//need more hack here for hadnling IOException properly
           	 	throw ServiceException.FAILURE("IOException",e);
           	}
        }
    }
    
    private OAuthAccessor get(OAuthTokenCacheKey key) throws ServiceException {
        return mMemcachedLookup.get(key);
    }
    
    private void put(OAuthTokenCacheKey key, OAuthAccessor accessor) throws ServiceException {
        mMemcachedLookup.put(key, accessor,OAUTH_TOKEN_EXPIRY,ZimbraMemcachedClient.DEFAULT_TIMEOUT);
    }
    
    public static OAuthAccessor get(String consumer_token,String token_type) throws ServiceException {
    	
    	String key_prefix = null;
    	
    	if(token_type == OAuthTokenCache.ACCESS_TOKEN_TYPE){
    		key_prefix = OAuthTokenCacheKey.ACCESS_TOKEN_PREFIX;
        }else if (token_type == OAuthTokenCache.REQUEST_TOKEN_TYPE){
        	key_prefix = OAuthTokenCacheKey.REQUEST_TOKEN_PREFIX;
        }else{
        	//it is error, then we need do something
        }
    	
    	OAuthTokenCacheKey key = new OAuthTokenCacheKey(consumer_token,key_prefix);
        ZimbraLog.extensions.debug("get type: "+token_type+" token from memcache with key: "+key.getKeyPrefix()+key.getKeyValue()+".");
        
        OAuthAccessor cache = sTheInstance.get(key);
        
        if(cache!=null){
        	if(token_type == OAuthTokenCache.ACCESS_TOKEN_TYPE){
        		cache.accessToken = consumer_token;
        		cache.requestToken = null;
        	}else{
        		cache.requestToken = consumer_token;
        		cache.accessToken = null;
        	}
        }
        return cache;
    }
    
    public static void put(OAuthAccessor accessor,String token_type) throws ServiceException {
        String consumer_token = null;
        String key_prefix = null;
    	
        
        if(token_type == OAuthTokenCache.ACCESS_TOKEN_TYPE){
    		consumer_token = accessor.accessToken;
    		
    		if(accessor.requestToken != null){
    		//	consumer_token = accessor.requestToken;
    			OAuthTokenCacheKey removable_key = new OAuthTokenCacheKey(accessor.requestToken,OAuthTokenCacheKey.REQUEST_TOKEN_PREFIX);
    	        
    			ZimbraLog.extensions.debug("remove type: req_token token from memcache with key: "+removable_key.getKeyPrefix()+removable_key.getKeyValue()+".");
    	        
    	    	sTheInstance.remove(removable_key);
    		}
    		key_prefix = OAuthTokenCacheKey.ACCESS_TOKEN_PREFIX;
        }else if (token_type == OAuthTokenCache.REQUEST_TOKEN_TYPE){
        	consumer_token = accessor.requestToken;
        	key_prefix = OAuthTokenCacheKey.REQUEST_TOKEN_PREFIX;
        }else{
        	//it is error, then we need 
        }
        
        
        OAuthTokenCacheKey key = new OAuthTokenCacheKey(consumer_token,key_prefix);
        
        ZimbraLog.extensions.debug("put type: "+token_type+" token into memcache with key: "+key.getKeyPrefix()+key.getKeyValue()+".");
        
        
        // if no effective ACL, return an empty ACL
        //if (acl == null)
        //    acl = new ACL();
        sTheInstance.put(key, accessor);
    }

    private void remove(OAuthTokenCacheKey key) throws ServiceException{
    	
    	mMemcachedLookup.remove(key);
    }
    
    public static void remove(String consumer_token, String token_type) throws ServiceException{
    	String key_prefix = null;
    	
    	if(token_type == OAuthTokenCache.ACCESS_TOKEN_TYPE){
    		key_prefix = OAuthTokenCacheKey.ACCESS_TOKEN_PREFIX;
        }else if (token_type == OAuthTokenCache.REQUEST_TOKEN_TYPE){
        	key_prefix = OAuthTokenCacheKey.REQUEST_TOKEN_PREFIX;
        }else{
        	//it is error, then we need 
        }
    	
    	OAuthTokenCacheKey key = new OAuthTokenCacheKey(consumer_token,key_prefix);
    	
    	ZimbraLog.extensions.debug("remove type: "+token_type+" token from memcache with key: "+key.getKeyValue()+".");
        
    	sTheInstance.remove(key);
    }
    
}
