/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
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
package com.zimbra.webClient.build;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.*;

import java.io.*;
import java.util.*;

public class TryTask {

	//
	// Data
	//

	private Sequential block;
	private List<CatchBlock> catchBlocks = new ArrayList<CatchBlock>();
	private Sequential finallyBlock;

	//
	// Public methods
	//

	public Sequential createBlock() {
		return this.block = new Sequential();
	}

	public CatchBlock createCatch() {
		CatchBlock block = new CatchBlock();
		this.catchBlocks.add(block);
		return block;
	}

	public Sequential createFinally() {
		return this.finallyBlock = new Sequential();
	}

	//
	// Task methods
	//

	public void execute() throws BuildException {
		BuildException ex = null;
		CatchBlock catchBlock = null;

		// execute main block
		if (this.block == null) {
			// TODO: log that nothing to do
		}
		else {
			try {
				this.block.execute();
			}
			catch (BuildException e) {
				ex = e;
			}
			catch (Throwable t) {
				ex = new BuildException(t);
			}
		}

		// find matching catch block
		if (ex != null) {
			Throwable t = ex.getCause();
			for (CatchBlock block : this.catchBlocks) {
				if (block.catches(t)) {
					catchBlock = block;
					break;
				}
			}
		}

		// handle exception and finally
		if (catchBlock != null) {
			catchBlock.execute();
		}
		if (this.finallyBlock != null) {
			this.finallyBlock.execute();
		}
		if (ex != null) {
			throw ex;
		}

	} // execute()

	//
	// Classes
	//

	static class CatchBlock extends Sequential {
		// Constants
		public static final String ANY = "*";
		// Data
		private Class exClass;
		// Public methods
		public void setType(String exceptionClassName) {
			// empty or "*" value == ANY
			exceptionClassName = exceptionClassName.trim();
			if (exceptionClassName.length() == 0 || exceptionClassName.equals(ANY)) {
				return;
			}

			// try literal name
			try {
				this.exClass = Class.forName(exceptionClassName);
			}
			catch (ClassNotFoundException e) {
				// assume java.lang exception
				try {
					this.exClass = Class.forName("java.lang."+exceptionClassName);
				}
				catch (ClassNotFoundException e2) {
					throw new BuildException(e2);
				}
			}

			// ensure that it's of type Throwable
			if (!this.exClass.isAssignableFrom(Throwable.class)) {
				this.exClass = null;
				throw new BuildException("Specified class does not derive from java.lang.Throwable");
			}
		}
		public String getType() {
			return exClass != null ? exClass.getName() : ANY;
		}
		public boolean isAny() {
			return getType().equals(ANY);
		}
		public boolean catches(Throwable t) {
			if (t == null) return isAny();
			return isAny() || t.getClass().isAssignableFrom(this.exClass);
		}
	} // class CatchBlock

} // class TryTask