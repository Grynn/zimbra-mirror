/*
***** BEGIN LICENSE BLOCK *****
Version: ZPL 1.1

The contents of this file are subject to the Zimbra Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of
the License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY
OF ANY KIND, either express or implied. See the License for the specific language governing
rights and limitations under the License.

The Original Code is: Zimbra Collaboration Suite.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

package com.zimbra.webClient.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Forward extends ZCServlet
{
    
    public static final String DEFAULT_FORWARD_URL = 
	"/public/launchZimbraMail.jsp";
    private static final String PARAM_FORWARD_URL = "fu";
    
    public void doGet (HttpServletRequest req,
		       HttpServletResponse resp) {

	try {
            if (shouldRedirectUrl(req)){
                String redirectTo = getRedirectUrl(req);
                resp.sendRedirect(redirectTo);
                return;
            }
            
	    String url = getReqParameter(req, PARAM_FORWARD_URL,
                                         DEFAULT_FORWARD_URL);
	    String qs = req.getQueryString();
	    if (qs != null && !qs.equals("")){
		url = url + "?" + qs;
	    }
	    ServletContext sc = getServletConfig().getServletContext();
	    sc.getRequestDispatcher(url).forward(req, resp);
	} catch (Exception ex) {
	    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    ex.printStackTrace ();
	}
    }    
}
