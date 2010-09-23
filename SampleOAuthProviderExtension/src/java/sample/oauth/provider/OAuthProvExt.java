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

package sample.oauth.provider;

import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.extension.ExtensionDispatcherServlet;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;

import com.zimbra.cs.service.AuthProvider;

public class OAuthProvExt implements ZimbraExtension {

	static {
    }
    
    public void init() throws ServiceException {    
    	ExtensionDispatcherServlet.register(this, new RequestTokenHandler());
    	ExtensionDispatcherServlet.register(this, new AuthorizationHandler());
    	ExtensionDispatcherServlet.register(this, new AccessTokenHandler());
    	
    	AuthProvider.register(new ZimbraAuthProviderForOAuth());
    	AuthProvider.refresh();
    }

    public void destroy() {
    	ExtensionDispatcherServlet.unregister(this);
    }
    
    public String getName() {
    	return "oauth";
    }

}
