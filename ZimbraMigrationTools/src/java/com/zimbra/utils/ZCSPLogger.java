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

import java.io.*;
import java.text.*;
import java.util.logging.*;
import java.util.logging.Formatter;
import java.util.*;

public class ZCSPLogger
{
    private static Logger deflogger;
    private static boolean deflog_init=false;
    private static FileHandler dfh;
    private static final String default_loggername="ZCSPROV";
    private static final int log_max_size=100*1000000; // 100 Mb
    private static final int log_max_files=100;
    private String ilogpath;
    private HashMap<String,Logger> LoggerMap;
    private HashMap<String,FileHandler> FileHandlerMap;
    Handler hwndconsole;
    public ZCSPLogger(String logpath)
    {
        ilogpath=logpath;
        LoggerMap=new HashMap<String,Logger>();
        FileHandlerMap=new HashMap<String,FileHandler>();
        hwndconsole = new ConsoleHandler();
        hwndconsole.setFormatter(new ConsoleLogFormatter());
    }

    public synchronized Logger get_logger(String logname)
    {
        Logger tmplogger=null;
        try
        {
            if (LoggerMap.containsKey(logname))
            {
                return LoggerMap.get(logname);
            }
            FileHandler tmplfh;

            boolean append =true;
            String lgname = ilogpath+logname+"_%g.log";
            int limit = log_max_size;
            int numLogFiles = log_max_files;
            tmplfh = new FileHandler(lgname, limit, numLogFiles,append);
            tmplfh.setFormatter(new LogFormatter());
            tmplogger = Logger.getLogger(logname);
            tmplogger.addHandler(tmplfh);
            tmplogger.setUseParentHandlers(false);
            tmplogger.log(Level.INFO, "**********************************************************");
            tmplogger.log(Level.INFO, "Start Logging:");
            tmplogger.log(Level.INFO, "**********************************************************");
            tmplogger.addHandler(hwndconsole);            
            LoggerMap.put(logname,tmplogger);
            FileHandlerMap.put(logname,tmplfh);
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return tmplogger;
    }

    public synchronized void RemoveConsoleHwnd(String logname)
    {
        Logger tmpLogger=null;
        if (LoggerMap.containsKey(logname))
        {
            tmpLogger=LoggerMap.get(logname);
        }
        if (tmpLogger!=null)
        {
            tmpLogger.removeHandler(hwndconsole);
        }
    }

    public synchronized void AddConsoleHwnd(String logname)
    {
        Logger tmpLogger=null;
        if (LoggerMap.containsKey(logname))
        {
            tmpLogger=LoggerMap.get(logname);
        }
        if (tmpLogger!=null)
        {
            tmpLogger.addHandler(hwndconsole);
        }
    }

    public synchronized void close(String logname)
    {
        Logger tmpLogger=null;
        if (LoggerMap.containsKey(logname))
        {
            tmpLogger=LoggerMap.get(logname);
        }
        if (tmpLogger!=null)
        {
            FileHandler tmpFh= FileHandlerMap.get(logname);
            tmpLogger.removeHandler(tmpFh);
            tmpFh.close();
            tmpLogger=null;
        }
        hwndconsole.close();
    }

    public synchronized void closeAll()
    {
        Set<String> LoggerSet=LoggerMap.keySet();
        Iterator itr= LoggerSet.iterator();
        while (itr.hasNext())
        {
            String log_name=(String)itr.next();
            close(log_name);
        }
    }
    //returns default app logger
    public static synchronized Logger get_default_logger()
    {
        if (!deflog_init)
        {
            try 
            {
                boolean append =true;
                String fqlogpath=ZCSUtils.getCurrentDirectory()+"/logs";
                String lgname = "zcsprov%g.log";
                String fqnlog=  fqlogpath+"/"+lgname;
                ZCSUtils.check_dir(fqlogpath);
                int limit = log_max_size;
                int numLogFiles = log_max_files;
                dfh = new FileHandler(fqnlog, limit, numLogFiles,append);
                dfh.setFormatter(new LogFormatter());
                deflogger = Logger.getLogger(default_loggername);
                deflogger.addHandler(dfh);
                deflog_init=true;

                deflogger.log(Level.INFO, "**********************************************************");
                deflogger.log(Level.INFO, "Start Logging:");
                deflogger.log(Level.INFO, "**********************************************************");
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        return deflogger;
    }

    //close default logger
    public static synchronized void close_default_logger()
    {
        if (dfh!=null)
        {
            deflogger.removeHandler(dfh);
            dfh.close();
            dfh=null;
        }
    }
}


class LogFormatter extends Formatter 
{
    private static final DateFormat dtformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static final String lineSep = System.getProperty("line.separator");
    public String format(LogRecord record)
    {
        String loggerName = record.getLoggerName();
        if(loggerName == null) 
        {
                loggerName = "root";
        }
        StringBuilder output =null;
        output = new StringBuilder()
            .append("*****")
            //.append(loggerName)
            .append("[")
            .append(record.getLevel()).append('|')
            .append(Thread.currentThread().getName()).append(':')
            .append(Thread.currentThread().getId()).append("| ")
            .append(dtformat.format(new Date(record.getMillis())))
            .append("]: ")
            .append(record.getMessage()).append(' ')
            .append(lineSep);
        return output.toString();
    }
}

class ConsoleLogFormatter extends Formatter
{
    private static final DateFormat dtformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static final String lineSep = System.getProperty("line.separator");

    public String format(LogRecord record)
    {
        StringBuilder output = new StringBuilder()
                 .append("[")
                .append(record.getLevel()).append('|')
                .append(Thread.currentThread().getName()).append(':')
                .append(Thread.currentThread().getId()).append("| ")
                .append(dtformat.format(new Date(record.getMillis())))
                .append("]: ")
                .append(record.getMessage()).append(' ')
                .append(lineSep);
        return output.toString();
    }
}

