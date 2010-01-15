/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.container;

import org.jivesoftware.util.ParamUtils;
import org.jivesoftware.wildfire.XMPPServer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Servlet is used for retrieval of plugin icons.
 *
 * @author Derek DeMoro
 */
public class PluginIconServlet extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String pluginName = ParamUtils.getParameter(request, "plugin");
        PluginManager pluginManager = XMPPServer.getInstance().getPluginManager();
        Plugin plugin = pluginManager.getPlugin(pluginName);
        if (plugin != null) {
            // Try looking for PNG file first then default to GIF.
            File icon = new File(pluginManager.getPluginDirectory(plugin), "logo_small.png");
            boolean isPng = true;
            if (!icon.exists()) {
                icon = new File(pluginManager.getPluginDirectory(plugin), "logo_small.gif");
                isPng = false;
            }
            if (icon.exists()) {
                // Clear any empty lines added by the JSP declaration. This is required to show
                // the image in resin!
                response.reset();
                if (isPng) {
                    response.setContentType("image/png");
                }
                else {
                    response.setContentType("image/gif");
                }
                InputStream in = null;
                OutputStream ost = null;
                try {
                    in = new FileInputStream(icon);
                    ost = response.getOutputStream();

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) >= 0) {
                        ost.write(buf, 0, len);
                    }
                    ost.flush();
                }
                catch (IOException ioe) {

                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Exception e) {
                        }
                    }
                    if (ost != null) {
                        try {
                            ost.close();
                        }
                        catch (Exception e) {
                        }
                    }
                }
            }
        }
    }
}
