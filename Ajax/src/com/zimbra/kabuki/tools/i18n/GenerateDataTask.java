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


package com.zimbra.kabuki.tools.i18n;

import java.io.File;
import org.apache.tools.ant.*;

public class GenerateDataTask
	extends Task {

	//
	// Data
	//

	// required

	private String dirname = null;
	private String basename = "I18nMsg";

	//
	// Public methods
	//

	// required

	public void setDestDir(String dirname) {
		this.dirname = dirname;
	}

	public void setBaseName(String basename) {
		this.basename = basename;
	}

	//
	// Task methods
	//

	public void execute() throws BuildException {

		// check required arguments
		if (dirname == null) {
			throw new BuildException("destination directory required -- use destdir attribute");
		}
		File dir = new File(dirname);
		if (!dir.exists()) {
			throw new BuildException("destination directory doesn't exist");
		}
		if (!dir.isDirectory()) {
			throw new BuildException("destination must be a directory");
		}

		// build argument list
		String[] argv = { "-d", dirname, "-b", basename };

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