/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.util;

import java.io.IOException;

/**
 * This class is a mere java launcher of any executable such as a shell script.
 * It exists so that we can generate Mac OS X app using Jar Bundler
 */
public class ExternalProgramRunner {

	public static void main(String[] args) {
		if (args.length == 0) System.exit(1);
		try {
			Runtime.getRuntime().exec(args);
		} catch (IOException x) {
			System.exit(2);
		}
	}
}
