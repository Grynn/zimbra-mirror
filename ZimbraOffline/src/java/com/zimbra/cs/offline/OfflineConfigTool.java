/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class OfflineConfigTool {

	public static void setPort(String dataRoot, int port) {
		setPortInLocalConfig(dataRoot, port);
		setPortInWebAppIni(dataRoot, port);
    }
    
	private static void setPortInLocalConfig(String dataRoot, int port) {
		String path = dataRoot + "/conf/localconfig.xml";
		try {
			String text = "";
			String line;
			
			BufferedReader in = new BufferedReader(new FileReader(path));
			boolean done = false;
			while ((line = in.readLine()) != null) {
				text = text + line + "\n";
				if (!done && line.indexOf("zimbra_admin_service_port") > 0) {
					text = text + "    <value>" + Integer.toString(port) + "</value>\n";
					in.readLine();
					done = true;
				}
			}
			in.close();
			
			writeToFile(path, text);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}		
	}
	
	private static void setPortInWebAppIni(String dataRoot, int port) {
		String path = dataRoot + "/zdesktop.webapp/webapp.ini";
		try {
			String text = "";
			String line;
			
			BufferedReader in = new BufferedReader(new FileReader(path));
			boolean done = false;
			while ((line = in.readLine()) != null) {
				if (!done && line.startsWith("uri=http://")) {
			        int pos = line.indexOf("http://");
			        if (pos > 0) {
			            pos += 7;
			            int sc, sl;
			            sc = line.indexOf(':', pos);
			            sl = line.indexOf('/', pos);
			            if (sc > 0 && sl > 0 && sl > sc)
			                line = line.substring(0, sc + 1) + Integer.toString(port) + line.substring(sl);
			        }
					done = true;
				}
				text = text + line + "\n";
			}
			in.close();
			
			writeToFile(path, text);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}		
	}
	
    private static void writeToFile(String path, String text) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            out.write(text);
            out.close();
        } catch (IOException e) {
			System.out.println(e.getMessage());
        }    	
    }
}
