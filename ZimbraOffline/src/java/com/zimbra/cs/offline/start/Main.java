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
package com.zimbra.cs.offline.start;

import java.lang.reflect.Method;

public class Main {
	public static void main(String[] args) {
		
		//The first argument must be the class name of the jetty starter
		if (args.length == 0) {
			System.err.println("Main launcher class name expected as first argument.");
			System.exit(1);
		}
		
		try {
			Class<?> starter = Class.forName(args[0]);
			Method main = starter.getMethod("main", new Class[] {String[].class});
			String[] newArgs;
			if (args.length > 1) {
				newArgs = new String[args.length - 1];
				System.arraycopy(args, 1, newArgs, 0, args.length - 1);
			} else {
				newArgs = new String[] {};
			}
			main.invoke(null, (Object)(newArgs));
		} catch (Exception x) {
			x.printStackTrace(System.err);
			System.exit(2);
		}
	}
}
