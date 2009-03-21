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

import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter 
{
    private String fqfile;
    private FileWriter filewriter;
    private final String separator=",";
    private final String lineSeparator = System.getProperty("line.separator");
    private boolean iquotesforempty;
    public CSVWriter(String file, boolean quotesforempty)
    {
        fqfile= file;
        iquotesforempty= quotesforempty;
        try
        {
            filewriter= new FileWriter(fqfile);
        }
        catch(IOException ioex)
        {
            ioex.printStackTrace();
        }
    }
    
    public void close()
    {
        try
        {
            filewriter.close();
        }
        catch(IOException ioex)
        {
            ioex.printStackTrace();
        }
    }
    
    public synchronized void write(String str)
    {
        try
        {
            if ((str==null)||(iquotesforempty)&&(!(str != null && str.length() == 0)))
                str="\"\"";
            filewriter.append(str);
            filewriter.append(separator);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public synchronized void writeln()
    {
        try
        {
            filewriter.append(lineSeparator);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        
    }
    
    public synchronized void writearr(String[] strarray)
    {
        int len= strarray.length;
        for (int i=0;i<len;i++)
        {
            try
            {
                filewriter.append(strarray[i]);
                if (i<(len-1))
                    filewriter.append(separator);
                else
                    writeln();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}   