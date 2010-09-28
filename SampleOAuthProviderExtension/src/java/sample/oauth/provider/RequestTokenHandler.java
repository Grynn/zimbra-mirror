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

package sample.oauth.provider;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.extension.ExtensionHttpHandler;
import com.zimbra.cs.extension.ZimbraExtension;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;
import sample.oauth.provider.core.SampleZmOAuthProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Request token request handler for zimbra extension
 * 
 * @author Yutaka Obuchi
 */
public class RequestTokenHandler extends ExtensionHttpHandler {

	
    public void init(ZimbraExtension ext) throws ServiceException{
        super.init(ext);    
    }
    
    public String getPath() {
        return super.getPath() + "/req_token";
    }
    
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	ZimbraLog.extensions.debug("RequestTokenHandler doGet requested!");
        processRequest(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
    	ZimbraLog.extensions.debug("RequestTokenHandler doPost requested!");
        processRequest(request, response);
    }
        
    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            OAuthMessage oAuthMessage = OAuthServlet.getMessage(request, null);
            
            OAuthConsumer consumer = SampleZmOAuthProvider.getConsumer(oAuthMessage);
            
            OAuthAccessor accessor = new OAuthAccessor(consumer);
            SampleZmOAuthProvider.VALIDATOR.validateReqTokenMessage(oAuthMessage, accessor);
            
            
            // generate request_token and secret
            SampleZmOAuthProvider.generateRequestToken(accessor);
            
            response.setContentType("text/plain");
            OutputStream out = response.getOutputStream();
            OAuth.formEncode(OAuth.newList("oauth_token", accessor.requestToken,
                                           "oauth_token_secret", accessor.tokenSecret,
                                           OAuth.OAUTH_CALLBACK_CONFIRMED,"true"),
                             out);
            out.close();
            
        } catch (Exception e){
            SampleZmOAuthProvider.handleException(e, request, response, true);
        }
        
    }

    private static final long serialVersionUID = 1L;

}
