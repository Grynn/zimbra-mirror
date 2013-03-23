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
package com.zimbra.kabuki.tools.tz;

import java.io.*;
import java.text.MessageFormat;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

public class GenerateDataTask extends Task {

	//
	// Data
	//

	private File srcFile;
	private File destFile;

	//
	// Public methods
	//

	public void setSrc(File file) {
		this.srcFile = file;
	}

	public void setDest(File file) {
		this.destFile = file;
	}

	//
	// Task methods
	//

	public void execute() throws BuildException {

		antAssert(this.srcFile == null, "missing src attribute");
		antAssert(this.destFile == null, "missing dest attribute");

		antAssert(!this.srcFile.exists(), "file doesn't exist: ", this.srcFile);

		try {
			String[] args = {
				"-i", this.srcFile.getAbsolutePath(),
				"-o", this.destFile.getAbsolutePath()
			};

			System.out.print("GenerateData");
			for (String arg : args) {
				System.out.print(' ');
				System.out.print(arg);
			}
			System.out.println();

			GenerateData.main(args);
		}
		catch (Exception e) {
			throw new BuildException(e);
		}
	}

	//
	// Private static functions
	//

	private static void antAssert(boolean condition, String message, Object... args)
	throws BuildException {
		if (condition) {
			if (args.length > 0) {
				message = MessageFormat.format(message, args);
			}
			throw new BuildException(message);
		}
	}

} // class GenerateDataTask