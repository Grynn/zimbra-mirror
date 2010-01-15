/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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


package com.zimbra.kabuki.tools.i18n;

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
	private List<FileSet> targetFilesets = new LinkedList<FileSet>();

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
		File sourceFile;
		try {
			sourceFile = new File(this.sourceFilename);

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

		for (FileSet fileset : this.targetFilesets) {
			DirectoryScanner scanner = fileset.getDirectoryScanner(project);
			File basedir = scanner.getBasedir();
			String[] filenames = scanner.getIncludedFiles();
			for (String filename : filenames) {
				try {
					File targetFile = new File(basedir, filename);

					System.out.println("Source file: "+sourceFile.getName());
					System.out.println("Target file: "+targetFile.getName());

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
		List<String> missing = new LinkedList<String>();
		List<String> duplicate = new LinkedList<String>();
		List<String> obsolete = new LinkedList<String>();

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
		int sourceCount = source.size();
		int missingCount = missing.size();
		int duplicateCount = duplicate.size();
		int obsoleteCount = obsolete.size();
		int percent = sourceCount > 0 ? 100*(sourceCount-missingCount-duplicateCount)/sourceCount : 0;

		if (missingCount == 0 && duplicateCount == 0 && obsoleteCount == 0) {
			System.out.println("  OK");
			return;
		}

		print("Missing - source key not present in target", missing);
		print("Duplicate - target value identical to source", duplicate);
		print("Obsolete - target key not present in source", obsolete);
		System.out.println("  Summary: "+sourceCount+" source, "+missingCount+" missing, "+duplicateCount+" duplicate, "+obsoleteCount+" obsolete, approx "+percent+"% complete");
	}

	static void print(String header, List<String> list) {
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