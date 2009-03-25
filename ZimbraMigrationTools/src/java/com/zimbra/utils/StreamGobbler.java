/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.logging.Logger;
import java.util.logging.Level;

public class StreamGobbler implements Runnable
{
    private String name;
    private InputStream is;
    private Thread thread;
    private Logger str_logger;
    private volatile boolean bstop;
    private volatile boolean active;
    public StreamGobbler (String name, InputStream is,Logger logger)
    {
        this.name = name;
        this.is = is;
        str_logger=logger;
        bstop=false;
        active=false;
    }

    public boolean get_active()
    {
        return active;
    }

    public void start ()
    {
        thread = new Thread (this);
        thread.start ();
    }

    public void stop()
    {
        bstop=true;
    }

    private synchronized void writeLogger(String str)
    {
        if(str_logger!=null)
        {
            str_logger.log(Level.INFO,   str);
        }
    }

    public void run ()
    {
        try
        {
            active=true;
            InputStreamReader isr = new InputStreamReader (is);
            BufferedReader br = new BufferedReader (isr);

            while (true)
            {
                if(bstop) break;
                String s = br.readLine ();
                if (s == null) break;
                writeLogger("[" + name + "] "+s);
            }
            active=false;
        }
        catch (Exception ex)
        {
            str_logger.log(Level.INFO,"Problem reading stream " + name + "... :" + ex);
            str_logger.log(Level.INFO, ZCSUtils.stack2string(ex));
        }
    }

}

