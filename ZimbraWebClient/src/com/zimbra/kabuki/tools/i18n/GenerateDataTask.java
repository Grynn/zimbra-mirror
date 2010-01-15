/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

import java.io.File;
import org.apache.tools.ant.*;

public class GenerateDataTask
	extends Task {

	//
	// Data
	//

	// required

	private File destdir = null;
	private String basename = "I18nMsg";
	private boolean client = true;
	private boolean server = false;

	//
	// Public methods
	//

	// required

	public void setDestDir(File dir) {
		this.destdir = dir;
	}

	public void setBaseName(String basename) {
		this.basename = basename;
	}

	public void setClient(boolean generate) {
		this.client = generate;
	}

	public void setServer(boolean generate) {
		this.server = generate;
	}

	//
	// Task methods
	//

	public void execute() throws BuildException {

		// check required arguments
		if (destdir == null) {
			throw new BuildException("destination directory required -- use destdir attribute");
		}
		if (!destdir.exists()) {
			throw new BuildException("destination directory doesn't exist");
		}
		if (!destdir.isDirectory()) {
			throw new BuildException("destination must be a directory");
		}

		// build argument list
		String[] argv = {
			this.client ? "-c" : "-C", this.server ? "-s" : "-S",
			"-d", destdir.getAbsolutePath(), "-b", basename
		};

		// run program
		try {
			System.out.print("GenerateData");
			for (String arg : argv) {
				System.out.print(' ');
				System.out.print(arg);
			}
			System.out.println();
			GenerateData.main(argv);
		}
		catch (Exception e) {
			throw new BuildException(e);
		}

	} // execute()

} // class GenerateDataTask