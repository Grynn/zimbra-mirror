package com.zimbra.webClient.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AdminServlet extends HttpServlet implements Servlet {

    private static final String ACTION_GETCSR = "getCSR" ;
    
    
    public AdminServlet() {
        super ();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doPost(req, resp);
    }

    /**
     * action:
     *  - getCSR - return the commerical CSR 
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action") ;
        
        if (action != null && action.equals(ACTION_GETCSR)) {
           String filename = req.getParameter("fname") ;
           if (filename == null || filename.length() <= 0) {
               filename = "current" ;
           }
            
            //      Set the headers.
            //set the Content-Type header to a nonstandard value to avoid the browser to do something automatically
           resp.setHeader("Expires", "Tue, 24 Jan 2000 20:46:50 GMT");
           resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
           resp.setContentType("application/x-download");
           resp.setHeader("Content-Disposition", "attachment; filename=" + filename + ".csr");
           
            String adminHomeDir = getServletContext().getRealPath("/");
            try {
                getCSRFile(adminHomeDir, resp.getOutputStream()) ;  
            }catch (Exception e){
                resp.sendError(resp.SC_NOT_FOUND) ;
            }
            return ;
        }
    }
    
    
    public static void getCSRFile(String adminHomeDir, OutputStream out)
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
