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
import java.net.*;
import java.util.logging.*;
//soap
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPElement;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

public class ZCSUtils
{
    private static Logger util_logger=null;
    public static synchronized String get_localhost_info()
    {
        String sret="";
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            sret= addr.getHostName();
            sret += "("+addr.getHostAddress()+")";
        }
        catch (Exception e)
        {
            log_msg(Level.INFO,"get_host_info Error: "+e.getMessage());
        }
        return sret;
    }    
    
    public static synchronized void set_logger(Logger logger)
    {
	util_logger=logger;
    }
	
    public static synchronized void log_msg(Level level, String msg)
    {
	if (util_logger!=null)
	        util_logger.log(level,msg);        
    }
    
    public static synchronized void dump_soap_message(String msg, SOAPMessage message)
    {
        try
        {
            ByteArrayOutputStream  byteArrayOutputString = new ByteArrayOutputStream();
            message.writeTo(byteArrayOutputString);
            String soapMessage = new String(byteArrayOutputString.toByteArray());
            log_msg(Level.INFO, msg+": "+soapMessage);          
        }
        catch(Exception e)
        {
            log_msg(Level.SEVERE,e.getMessage());
            log_msg(Level.SEVERE,stack2string(e));
        }
    }
    
    public static synchronized String StFindAttributeValue(Node nd, String element, String attr)
    {
        String retval=null; 
        try
        {
            SOAPElement msgElement = (SOAPElement)nd;
            String locname=msgElement.getLocalName();
            if (locname.compareTo(element)==0)
            {
                retval=msgElement.getAttribute(attr);
                return retval;
            }
                    
            if (nd.hasChildNodes())
            {
                NodeList ndlist= nd.getChildNodes();
                int ndlen=ndlist.getLength();
                for (int i=0;i<ndlen;i++)
                {
                    if(ndlist.item(i).hasChildNodes())
                    {
                        Node tnd= ndlist.item(i);
                        retval=StFindAttributeValue(tnd,element,attr);
                        if (retval !=null)
                            break;
                    }
                }
            }
        }
        catch(Exception e)
        {
            log_msg(Level.SEVERE,"FindNodeValue Exception: "+e.getMessage());
            log_msg(Level.SEVERE,stack2string(e));
        }
        return retval;
    }
    public static synchronized String StFindNodeValue(Node nd, String tofind)
    {
        String retval=null;
        try
        {
            SOAPElement msgElement = (SOAPElement)nd;
            String locname=msgElement.getLocalName();
            if (locname.compareTo(tofind)==0)
            {
                retval=msgElement.getValue();
                //log_msg("FOUND: "+tofind+" -->"+retval);
                return retval;
            }
                    
            if (nd.hasChildNodes())
            {
                NodeList ndlist= nd.getChildNodes();
                int ndlen=ndlist.getLength();
                for (int i=0;i<ndlen;i++)
                {
                    if(ndlist.item(i).hasChildNodes())
                    {
                        Node tnd= ndlist.item(i);
                        retval=StFindNodeValue(tnd,tofind);
                        if (retval !=null)
                            break;
                    }
                }
            }
        }
        catch(Exception e)
        {
            log_msg(Level.SEVERE,"FindNodeValue Exception: "+e.getMessage());
            log_msg(Level.SEVERE,stack2string(e));
        }
        return retval;
    }
        
    public synchronized void IterateNodes(Node nd)
    {
        try
        {
            SOAPElement msgElement = (SOAPElement)nd;
            System.out.println(msgElement.getLocalName()+": "+msgElement.getValue());
            if (nd.hasChildNodes())
            {
                NodeList ndlist= nd.getChildNodes();
                int ndlen=ndlist.getLength();
                for (int i=0;i<ndlen;i++)
                {
                    if(ndlist.item(i).hasChildNodes())
                    {
                        Node tnd= ndlist.item(i);
                        IterateNodes(tnd);
                    }
                }
            }
        }
        catch(Exception e)
        {
            log_msg(Level.SEVERE,"IterateNodes Exception: "+e.getMessage());
            log_msg(Level.SEVERE,stack2string(e));
        }
    }
    
    public static synchronized String FindDocumentNodeVal(Node nd, String toFind)
    {
        String ret=null;
        String locname=nd.getNodeName();
        String locval= nd.getNodeValue();
        if (locname.compareTo(toFind)==0)
        {
            ret= nd.getNodeValue();
            return ret;
        }
        
        if (nd.hasChildNodes())
        {
            NodeList ndlist= nd.getChildNodes();
            int ndlen=ndlist.getLength();
            for (int i=0;i<ndlen;i++)
            {
                locname=ndlist.item(i).getNodeName();
                locval = ndlist.item(i).getNodeValue();
                NamedNodeMap ndmap= ndlist.item(i).getAttributes();
                locval=ndlist.item(i).getTextContent();
                if (ndmap !=null)
                {
                    for (int j=0; j<ndmap.getLength();j++)
                    {
                        Node locnd=ndmap.item(j);
                        String ndname=locnd.getNodeName();
                    }
                }
                if (locname.compareTo(toFind)==0)
                {
                    ret= locval;
                    return ret;
                }
                if(ndlist.item(i).hasChildNodes())
                {
                    Node tnd= ndlist.item(i);
                    ret=FindDocumentNodeVal(tnd,toFind);
                    if (ret !=null)
                        break;
                }
            }
        }
        return ret;
    }
    
    public static synchronized String stack2string(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "------\r\n" + sw.toString() + "------\r\n";
        }
        catch(Exception e2) {
            return "bad stack2string"+e2.getMessage();
        }
    }
    
    public static synchronized String getCurrentDirectory()
    {
        String curdir=null;
        File dir1 = new File (".");
        try
        {
            curdir= dir1.getCanonicalPath();
        }
        catch(Exception ex)
        {
            ex.getMessage();
            ex.printStackTrace();
        }
        return curdir;
    }
    
    public static synchronized boolean check_dir(String dir)
    {
        boolean success=true;
        File file = new File(dir);
        if (!file.exists())
        {
            success = file.mkdirs();
        }
        return success;
    }
    
    public static synchronized void check_class_paths()
    {
        // the classpath
        System.out.println("JAVA CLASS PATH INFO: "+ System.getProperty( "java.class.path" )+"\n\n" );

        // extension directories whose jars are included on the classpath
        System.out.println("JAVA EXT DIRS: "+ System.getProperty( "java.ext.dirs" ) +"\n\n" );

        // low level classpath, includes system jars
        System.out.println("JAVA LIB PATH: "+ System.getProperty( "java.library.path" )+"\n\n"  );

        // character to separate (not terminate!) entries on the classpath, ; for Windows : for unix.
        System.out.println("JAVA PATH SEP: "+ System.getProperty( "path.separator" ) );
    }


    public static synchronized String FindFileByPrefix(String dirpath,String prefix)
    {
        String ret=null;
        File dir = new File(dirpath);
        String[] children = dir.list();
        if (children == null)
        {
            // Either dir does not exist or is not a directory
            return null;
        } else
        {
            for (int i=0; i<children.length; i++)
            {
                // Get filename of file or directory
                String filename = children[i];
                if (filename.startsWith(prefix))
                {
                    ret=filename;
                    break;
                }
            }
        }
        return ret;
    }

    public static synchronized boolean move_file_to(String srcdir, String destdir, String filename)
    {
        boolean ret=false;
        try
        {
            File source_file = new File(srcdir+"/"+filename);
            if(source_file.exists())
            {
                File ddir= new File(destdir);
                if (!ddir.exists())
                {
                    ddir.mkdirs();
                }
                File dest_file = new File(destdir+"/"+filename);
                if (dest_file.exists())
                {
                    dest_file.delete();
                }

                boolean moved = source_file.renameTo(dest_file);
                ret =moved;
            }
        }
        catch(Exception e)
        {
                 e.printStackTrace();
        }
        return ret;
    }

    public static synchronized boolean move_files_to(String srcdir, String destdir)
    {
        boolean ret=false;
        File dir = new File(srcdir);

        String[] children = dir.list();
        if (children == null)
        {
            // Either dir does not exist or is not a directory
            return false;
        } else
        {
            for (int i=0; i<children.length; i++)
            {
                // Get filename of file or directory
                String filename = children[i];
                ZCSUtils.check_dir(destdir);
                ret=move_file_to(srcdir,destdir, filename);
            }
        }
        return ret;
    }

     

    public static String GetSlashedDir(String dirpath)
    {
        String retval=dirpath;

        if (retval.charAt(retval.length()-1)!='/')
        {
            retval = retval+'/';
        }
        return retval;
    }
}
