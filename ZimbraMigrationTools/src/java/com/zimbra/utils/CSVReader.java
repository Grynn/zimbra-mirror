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

import com.zimbra.zcsprov.ZmProvGenericException;

import java.io.*;

public class CSVReader
{
    private String ffqname;
    private EventNotifier eventnotifier;
    private long csvoffset;
    private long recs_number;
    private String separator;
    private final String commentPrefix="#";
    public void set_eventnotifier(EventNotifier en)
    {
        eventnotifier=en;
    }
    
    public CSVReader(String fqfname,long offset, long rec_numbers) throws FileNotFoundException
    {
        ffqname=fqfname;
        eventnotifier=null;
        csvoffset= offset;
        recs_number = rec_numbers;
        separator=",";
        //if -1, set it to highest long value
        if (rec_numbers==-1)
            recs_number=Long.MAX_VALUE;
        //check for config file existance 
        File f = new File(fqfname);
        if(!f.exists())
        {
            throw new FileNotFoundException(fqfname+" input file does not exists.");
        }
    }
    
    public void ProcessFile() throws ZmProvGenericException
    {
        long lineno=0;
        long processed_lines=0;
        BufferedReader br = null;
        FileReader fr=null;
        try 
        {
            fr=new FileReader(ffqname);
            br = new BufferedReader(fr);
            String line = null; 
            while ((line = br.readLine()) != null) 
            {
                //read if line is not comment or empty
                if (!line.startsWith(commentPrefix)&&(!(line != null && line.length() == 0)))
                {
                    lineno++;
                    //read if current lineno is greater than specified offset
                    if (lineno>=csvoffset)
                    {                      
                        //read if processed lines are less than specified records to read
                        if (processed_lines<recs_number)
                        {
                            line.trim();
                            String[] values = line.split(separator);
                            if(eventnotifier!=null)
                            {
                                eventnotifier.notifCUyevent(values);
                            }
                            else
                            {
                                throw new ZmProvGenericException("No notify event associated");
                            }
                            processed_lines++;
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                else
                {
                    //System.out.println(Level.INFO, "Line is either commented or Empty. No processing done.");
                }
            }
        }
        catch (FileNotFoundException ex) 
        {
            throw new ZmProvGenericException("ProcessFile"+": ("+ffqname + ") File Not found.");
        }
        catch (IOException ex) 
        {
            throw new ZmProvGenericException("ProcessFile: "+ex.getMessage());
        }
        catch (Exception e)
        {
            throw new ZmProvGenericException("ProcessFile: "+e.getMessage());

        }
        finally 
        {
            try 
            {
                if (br != null)
                    br.close();
                if (fr !=null)
                    fr.close();
            }
            catch (IOException ex) 
            {
               throw new ZmProvGenericException("ProcessFile final exception");
            }
        }
    }
}
