/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.webClient.servlet;

import com.zimbra.cs.servlet.ZimbraServlet;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.extension.ExtensionUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.service.ServiceException;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AdminServlet extends ZimbraServlet {

    private static final String ACTION_GETCSR = "getCSR" ;
//    private static final String ACTION_GETBP = "getBP" ;

    
    public AdminServlet() {
        super ();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doPost(req, resp);
    }

    /**
     * action:
     *  - getCSR - return the commerical CSR
     *  - getBP - return the bulk privisioin results
     *       * it also needs a required parameter "aid" - uploaded file attachment id
     *       * the status information is from the in-memory copy of the uploaded file contents
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action") ;
        
        //check the auth token
        AuthToken authToken = getAdminAuthTokenFromCookie(req, resp);
        if (authToken == null)
           return;

        try {
            if (action != null && action.equals(ACTION_GETCSR)) {
               String filename = req.getParameter("fname") ;
               if (filename == null || filename.length() <= 0) {
                   filename = "current" ;
               }

                //      Set the headers.
                //set the Content-Type header to a nonstandard value to avoid the browser to do something automatically
               resp.setHeader("Expires", "Tue, 24 Jan 2000 20:46:50 GMT");
//               resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
               resp.setContentType("application/x-download");
               resp.setHeader("Content-Disposition", "attachment; filename=" + filename + ".csr");

                String adminHomeDir = getServletContext().getRealPath("/");

                getCSRFile(adminHomeDir, resp.getOutputStream()) ;
                
                return ;

            }
        }catch (Exception e){
            ZimbraLog.webclient.error(e) ;
            resp.sendError(resp.SC_NOT_FOUND) ;
        }
    }
    
    
    private static void getCSRFile(String adminHomeDir, OutputStream out)
        throws FileNotFoundException, IOException {
        String csrFileName =  adminHomeDir + "/tmp/current.csr" ;
        //System.out.println("csr file = " + csrFileName) ;
        InputStream in = null;
        try {
          in = new BufferedInputStream(new FileInputStream(csrFileName));
          byte[  ] buf = new byte[1024];  // 1K buffer
          int bytesRead;
          while ((bytesRead = in.read(buf)) != -1) {
            out.write(buf, 0, bytesRead);
          }
        }
        finally {
          if (in != null) in.close(  );
        }
    }

}
