/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.webClient.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * simple servlet to handle redirects of /zimbra/user to /service/user. 
 *
 */
public class UserRedirectServlet extends HttpServlet
{
    public static final String SERVICE_URL = "/service/home"; 
    
    public void doGet (HttpServletRequest req, HttpServletResponse resp) throws IOException 
    {
        String qs = req.getQueryString();
        if (qs != null && !qs.equals("")) {
            resp.sendRedirect(SERVICE_URL+req.getPathInfo()+"?"+req.getQueryString());
        } else {
            resp.sendRedirect(SERVICE_URL+req.getPathInfo());
        }
    }    
}
