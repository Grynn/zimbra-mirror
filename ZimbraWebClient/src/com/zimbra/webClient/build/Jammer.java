/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2009, 2010 VMware, Inc.
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
package com.zimbra.webClient.build;

import org.apache.tools.ant.*;
import org.apache.tools.ant.util.*;
import java.io.*;
import java.util.Vector;
import java.util.regex.*;

/**
 * @author enrique
 * @class Jammer
 * 
 * Class to encapsulate a list of javascript files 
 * that needs to be concatenated into one.
 */
public class Jammer {

    private static final String DEFAULT_BEGIN_BLOCK = "<!-- BEGIN SCRIPT BLOCK";
    private static final String DEFAULT_END_BLOCK = "<!-- END SCRIPT BLOCK";
    private Vector fileList;
    private String beginBlockToken;
    private String endBlockToken;
    private File destFile;
    private Task ownerTask;
    private String webroot;
    private String webappName;
   
    /**
     * 
     * @param destinationFile - the end file, containing all other javascript files
     * @param proj - The ant project.
     * @param pathToWebroot - path to the webroot directory ( to resolve absolute urls).
     */
    public Jammer (File destinationFile, Task owner, String pathToWebroot, String webapp) {
        this.fileList = new Vector();
        this.beginBlockToken = DEFAULT_BEGIN_BLOCK;
        this.endBlockToken = DEFAULT_END_BLOCK;
        this.destFile = destinationFile;
        this.ownerTask = owner;
        this.webroot = pathToWebroot;
        this.webappName = webapp;
    }
    
    public void addFileToList (String fileName) {
        this.fileList.add(fileName);
    }
    
    public Object [] getFileList () {
        return this.fileList.toArray();
    }
    
    /**
     * Parses an entire file of basic html or jsp, pulling out scripts
     * inside of the script block region of that file.
     * 
     * @param file
     * @throws JammerException
     */
    public void parse(File file) throws JammerException {
        
        if (!file.exists()){
            throw new JammerException("File " + file + " does not exist");
        }

        if (file.getName().endsWith(".js")) {
            this.addFileToList(file.getAbsolutePath());
            return;
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        
            String fileContents = FileUtils.readFully(br, 1024);
            int startJamBlockIndex = 
                fileContents.indexOf(this.beginBlockToken);
            String filesString = null;
            int endJamBlockIndex = -1 ;
            if (startJamBlockIndex != -1){
                endJamBlockIndex = fileContents.indexOf(this.endBlockToken,
                                                        startJamBlockIndex);
                if (endJamBlockIndex != -1) {
                    filesString = fileContents.substring(startJamBlockIndex,
                                                         endJamBlockIndex);
                } else {
                    throw 
                        new JammerException(
                            file
                            + ": Jammed script block not terminated properly");
                }
                //bw = new BufferedWriter(new FileWriter (jspTempFile));
                //bw.write(fileContents.substring(0, startJamBlockIndex));
            } else {
                br.close();
                return;
            }
            this.parseScriptBlock(filesString);
        } catch (IOException ie){
            throw new JammerException(ie);
        } finally {
            try {
                if (br != null){
                    br.close();
                }
            } catch (IOException e) {
                // do nothing
            }
        }
    }
    
    /**
     * This parses a script block, and adds each script file to the internal file list.
     * @param scriptBlock - The string representing a block of script includes
     */
    public void parseScriptBlock(String scriptBlock) throws JammerException{
        if (scriptBlock != null) {
            LineReader reader = new LineReader(scriptBlock, 
                                               this.ownerTask, "</script>",
                                               this.webroot, 
                                               this.webappName);
            File scriptFile = reader.getNextFile();
            while (scriptFile != null) {
                if (scriptFile == null){
                    this.ownerTask.log("DIDNT GET SCRIPT FILE " + scriptFile,
                                       Project.MSG_DEBUG);
                } else if (!scriptFile.exists()){
                    //throw new JammerException(scriptFile + " does not exist");
                    this.ownerTask.log("Script file " + scriptFile + " does not exist", Project.MSG_DEBUG);
                } else {
                    this.addFileToList(scriptFile.getAbsolutePath());
                    //Now we have a handle to an existing file
                    // append the file to the destination file
                    //BufferedReader b = 
                    //    new BufferedReader(new FileReader(scriptFile));
                    // read the entire script file
                    //String script = fu.readFully(b);
                    // write the script file to our new jammed script
                    //destWriter.write("\n"+script);
                    //destWriter.flush();
                    //b.close();
                }
                scriptFile = reader.getNextFile();
            }
        }
    }
    
    /**
     * Concatenates the internal list of files accumulated to this point.
     * Note: The caller should probably have called parse @see #parse
     * or parseScriptBlock @see #parseSriptBlock, before calling this method.
     * @throws JammerException
     */
    public void concatFiles () throws JammerException{
        Object [] files = this.getFileList();
        BufferedWriter destWriter = null;
        try {
            if (!this.destFile.exists()){
                this.destFile.createNewFile();
            }
            destWriter = new BufferedWriter(new FileWriter(this.destFile));
            for (int i = 0; i < files.length; ++i) {
                File scriptFile = new File((String)files[i]);
                if (scriptFile.exists()){
                    BufferedReader b = 
                        new BufferedReader(new FileReader(scriptFile));
                    // read the entire script file
                    String script = FileUtils.readFully(b);
                    // write the script filee to our new jammed script
                    // We need the newline character for those pesky javascript
                    // writers who don't finish an assigned function statement
                    // with a semicolon.
                    destWriter.write('\n');
                    try {
                    	destWriter.write(script);
                    	destWriter.flush();
                    } catch (NullPointerException np) {
                    	this.ownerTask.log("ConcatFiles: " + scriptFile + " empty script", Project.MSG_DEBUG);
                    }
                    b.close();
                } else {
                    this.ownerTask.log("ConcatFiles: " + scriptFile + " does not exist", Project.MSG_DEBUG);
                }
            }
        } catch (IOException ie){
            throw new JammerException(ie);
        } finally {
            try {
                if (destWriter != null){
                    destWriter.close();
                }
            }catch (IOException e) {
                //do nothing
            }
        }
    }
    
    public String getTokenBlock () {
        return null;
    }
    
    public void setDestFile (File destinationFile){
        this.destFile = destinationFile;
    }
    
    public void setBeginBlockToken (String token){
        this.beginBlockToken = token;
    }
    
    public void setEndBlockToken (String token) {
        this.endBlockToken = token;
    }
    
    /**
     * Internal class able to parse a line in a script block.
     */
    class LineReader {
        private String rawString = null;
        private String[] lines = null;
        private int index = 0;
        private Task ownerTask = null;
        private String splitToken = null;
        private String webroot = null;
        private String webappName = null;
        
        private static final String DEFAULT_WEBAPP_NAME="zimbra";
        
        public LineReader(String filesString, Task owner, String splitToken,
                          String webroot, String webapp) {
            this.rawString = filesString;
            this.splitToken = splitToken;
            this.lines = filesString.split(splitToken);
            this.ownerTask = owner;
            this.webroot = webroot;
            this.webappName = (webapp == null)? DEFAULT_WEBAPP_NAME: webapp;
            this.index = 0;
        }

        public void reset() {
            this.index = 0;
        }

        /**
         * returns null if all files have been read
         * 
         * @return File
         */
        private static final String SCRIPT_BEGIN = "<script";
        private static final String SRC_BEGIN = "src=\"";
        private static final String QUOTE = "\"";
        private static final String QUERY_DELIM = "?";
        private static final String simpleJspRegex = "<%([^%]*)%>";
        private Pattern jspPattern = Pattern.compile(simpleJspRegex);
        private static final String contextPathRegex = "<%= contextPath %>";
        private Pattern contextPathPattern = Pattern.compile(contextPathRegex);

        public File getNextFile() {
            String scriptFileName = null;
            File scriptFile = null;
            if (this.index >= lines.length - 1) {
                return null;
            }
            String ln = lines[this.index++].trim();
            this.ownerTask.log("New line = " + ln, Project.MSG_DEBUG);
            int startScript = ln.indexOf(SCRIPT_BEGIN);
            if (startScript != -1) {
                int srcBegin = ln.indexOf(SRC_BEGIN, startScript);
                if (srcBegin != -1) {
                    // replace the context path with the webappName
                    Matcher m = contextPathPattern.matcher(ln);
                    ln = m.replaceAll(this.webappName);
                    // replace simple jsp stuff on the line
                    // if it gets any more complex than this
                    // this may not be the best solution.
                    m = jspPattern.matcher(ln);
                    ln = m.replaceAll("");
                    int srcEnd = ln.indexOf(QUERY_DELIM, srcBegin + 5);
                    if (srcEnd == -1){
                        srcEnd = ln.indexOf(QUOTE, srcBegin + 5 );
                    }
                    if (srcEnd != -1) {
                        scriptFileName = ln.substring(srcBegin + 5, srcEnd);
                        if (scriptFileName.startsWith("/")){
                            scriptFile = this.createWebappFile(scriptFileName);
                        } else {
                            // TODO
                        }
                    }
                }
                this.ownerTask.log("getting file " + scriptFile + "$$", 
                                   Project.MSG_DEBUG);
            }
            return scriptFile;
        }
        
        public File createWebappFile(String fileName) {
            String tempFileName = fileName;
            if (this.webappName != null) {
                tempFileName = fileName.replaceFirst(this.webappName, "");
            }
            return new File(this.webroot, tempFileName);
        }

    }

}
