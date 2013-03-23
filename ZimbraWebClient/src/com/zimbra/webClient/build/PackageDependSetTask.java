/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2009, 2010 VMware, Inc.
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

import java.io.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class PackageDependSetTask
extends org.apache.tools.ant.taskdefs.DependSet {

	//
	// Data
	//

	protected Exception packageDependsEx;

	//
	// Public methods
	//

	/** All of the work is done in DependsList#setFiles. */
	public DependsList createSrcDependsList() {
		return new DependsList();
	}

	//
	// Task methods
	//

	public void execute() throws BuildException {
		if (this.packageDependsEx != null) {
			throw new BuildException(this.packageDependsEx);
		}
		super.execute();
	}

	//
	// Classes
	//

	public class DependsList {

		// Data

		private File dir;

		// Public methods

		public void setDir(File dir) {
			this.dir = dir;
		}

		public void setFiles(String files) {
			File baseDir = PackageDependSetTask.this.getProject().getBaseDir();

			String[] filenames = files.split(",");
			for (String filename : filenames) {
				// always consider this file part of the dependencies
				FileList filelist = new FileList();
				filelist.setDir(this.dir);
				filelist.setFiles(filename);
				PackageDependSetTask.this.addSrcfilelist(filelist);

				// is there anything more to do?
				File dependsFile = new File(this.dir, filename.trim());
				if (!dependsFile.exists()) {
					continue;
				}

				// add all dependencies
				try {
					BufferedReader in = new BufferedReader(new FileReader(dependsFile));
					String srcfilename;
					while ((srcfilename = in.readLine()) != null) {
						if (srcfilename.trim().length() == 0) continue;

						filelist = new FileList();
						File srcfile = new File(srcfilename);
						if (srcfile.isAbsolute()) {
							filelist.setDir(srcfile.getParentFile());
							filelist.setFiles(srcfile.getName());
						}
						else {
							filelist.setDir(baseDir);
							filelist.setFiles(srcfilename);
						}
						PackageDependSetTask.this.addSrcfilelist(filelist);
					}
				}
				catch (IOException ex) {
					PackageDependSetTask.this.packageDependsEx = ex;
				}
			}
		} // setList(String)

	} // class DependsList

} // class PackageDependSetTask