/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.kabuki.tools.i18n;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class ComparePropsTask 
	extends Task {
    
    //
    // Data
    //

    // required
    
    private String sourceFilename = null;
    private List targetFilesets = new LinkedList();
    
    //
    // Public methods
    //
    
    // required
    
    public Object createSourceFile() {
    	return new SourceFile();
    }
    
    public FileSet createTargetFileset() {
    	FileSet fileset = new FileSet();
    	this.targetFilesets.add(fileset);
        return fileset;
    }
    
    //
    // Task methods
    //
    
    public void execute() throws BuildException {

        // check required arguments 
        if (this.sourceFilename == null) {
            throw new BuildException("source filename required");
        }
        if (this.targetFilesets.size() == 0) {
        	throw new BuildException("no target filesets specified");
        }
        
        // read source properties
        Properties sourceProps = new Properties();
        try {
            File sourceFile = new File(this.sourceFilename);
            System.out.println("Source file: "+sourceFile);

            InputStream sourceIn = new FileInputStream(sourceFile);
            sourceProps.load(sourceIn);
        }
        catch (FileNotFoundException e) {
        	throw new BuildException("source file doesn't exist");
        }
        catch (IOException e) {
        	throw new BuildException("error loading source file");
        }
        
        // process files in filesets
        Project project = this.getProject();
        
        Iterator filesets = this.targetFilesets.iterator();
        while (filesets.hasNext()) {
        	FileSet fileset = (FileSet)filesets.next();
        	DirectoryScanner scanner = fileset.getDirectoryScanner(project);
        	File basedir = scanner.getBasedir();
        	String[] filenames = scanner.getIncludedFiles();
        	for (int i = 0; i < filenames.length; i++) {
        		try {
            		File targetFile = new File(basedir, filenames[i]);
        			System.out.println("Target file: "+targetFile);

        			InputStream targetIn = new FileInputStream(targetFile);
        			Properties targetProps = new Properties();
        			targetProps.load(targetIn);
        			
        			compare(sourceProps, targetProps);
        		}
        		catch (FileNotFoundException e) {
        			System.err.println("warning: target file doesn't exist");
        		}
        		catch (IOException e) {
        			System.err.println("warning: error loading target file");
        		}
        	}
        }
        
    } // execute()

    // Static methods
    
    static void compare(Properties source, Properties target) {
    	List missing = new LinkedList();
    	List duplicate = new LinkedList();
    	List obsolete = new LinkedList();
    	
    	// check for missing properties and duplicate values 
    	Enumeration sourceKeys = source.propertyNames();
    	while (sourceKeys.hasMoreElements()) {
    		String sourceKey = (String)sourceKeys.nextElement();
    		String sourceValue = source.getProperty(sourceKey);
    		String targetValue = target.getProperty(sourceKey);
    		if (targetValue == null) {
    			missing.add(sourceKey);
    		}
    		else if (targetValue.equals(sourceValue)) {
    			duplicate.add(sourceKey);
    		}
    	}
    	
    	// check for obsolete properties
    	Enumeration targetKeys = target.propertyNames();
    	while (targetKeys.hasMoreElements()) {
    		String targetKey = (String)targetKeys.nextElement();
    		String sourceValue = source.getProperty(targetKey);
    		if (sourceValue == null) {
    			obsolete.add(targetKey);
    		}
    	}
    	
    	// print results
    	int missingCount = missing.size();
    	int duplicateCount = duplicate.size();
    	int obsoleteCount = obsolete.size();

    	if (missingCount == 0 && duplicateCount == 0 && obsoleteCount == 0) {
    		System.out.print("  OK");
    		return;
    	}

    	print("missing", missing);
    	print("duplicate", duplicate);
    	print("obsolete", obsolete);
    	System.out.println("  Summary: "+missingCount+" missing, "+duplicateCount+" duplicate, "+obsoleteCount+" obsolete");
    }
    
    static void print(String header, List list) {
    	Collections.sort(list);
    	Iterator iter = list.iterator();
    	if (iter.hasNext()) {
    		System.out.println("  "+list.size()+" "+header);
	    	while (iter.hasNext()) {
	    		System.out.println("    "+iter.next());
	    	}
    	}
    }
    
    // Classes
    
    public class SourceFile {
		public void setFile(String filename) {
			ComparePropsTask.this.sourceFilename = filename;
		}
    }
    
} // class ComparePropsTask