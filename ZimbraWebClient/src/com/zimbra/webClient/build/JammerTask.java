/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
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
package com.zimbra.webClient.build;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;


import java.io.*;
import java.util.*;

public class JammerTask extends Task {
    /**
     * Directory that will map  to the webroot
     */
    private File webrootDir;
    /**
     * Name of the web application when deployed
     */
    private String webappName;
    /**
     * List of fileLists set in the build.xml file.
     */
    private Vector fileLists;
    /**
     * Destination target for this jammer task.
     */
    private File destFile;
    /**
     * List of File objects accumlated accross all files parsed.
     */
    private Vector fullFileList;
    
    /**
     * @constructor
     * No argument constructor.
     */
    public JammerTask() {
        // nothing for right now
        // probably need some initialization of default root directory
        // Default for directories, is the directory we are working in.
        this.webrootDir = new File(".");
        this.webappName = null;
        this.fileLists = new Vector();
        this.destFile = null;
        this.fullFileList = new Vector();
    }

    /**
     * Internal utility method
     * @param message - message to log.
     */
    private void logDebug(String message){
        this.log(message, Project.MSG_DEBUG);
    }
    
    /**
     * execute handles the task itself
     * 
     * @throws BuildException
     */
    public void execute() throws BuildException {
        this.logDebug("============= Jammer task START====================");
        try {
            // check for valid directories
            if (!this.webrootDir.isDirectory()) {
                throw new BuildException(this.webrootDir
                        + "is not a valid directory");
            }
            Jammer jammer = new Jammer(this.destFile, this, this.webrootDir.toString(), 
                                       this.webappName);
            // This should be an array of Files
            Object [] files = this.getIncludedFiles();
        
            for (int i = 0; i < files.length; ++i) {
                File file = (File)files[i];
                jammer.parse(file);
                jammer.concatFiles();
            }
        } catch (JammerException je){
            je.printStackTrace();
            throw new BuildException(je);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new BuildException(t);
        }
        this.logDebug("================ Jammer task DONE===================");

    }
    
    /**
     * returns an array of objects (Files) representing the files
     * collected while parsing all files.
     * @return
     */
    private Object [] getIncludedFiles() {
        // the full file list should be updated every time a file
        // list is added to the task
        Enumeration enm = this.fileLists.elements();
        FileList fileList = null;
        while (enm.hasMoreElements()){
            fileList = (FileList)enm.nextElement();
            String [] files = fileList.getFiles(this.getProject());
            File dir = fileList.getDir(this.getProject());
            File file = null;
            for (int i = 0 ; i < files.length; ++i){
                file = new File(dir, files[i]);
                this.fullFileList.add(file);
            }
        }

        return fullFileList.toArray();
    }

    // --------------------------------------------------------------
    // Access methods
    // --------------------------------------------------------------
    public void addFileList(FileList fileList){
        this.fileLists.add(fileList);
    }
    
    public File getDestFile() {
        return this.destFile;
    }

    public File getWebrootDir() {
        return this.webrootDir;
    }
    
    public void setDestFile (File destinationFile){
        this.destFile = destinationFile;
    }
    public void setPathToWebapp(String name) {
        this.webappName = name;
    }

    public void setWebrootDir(File dir) {
        this.webrootDir = dir;
    }
}
