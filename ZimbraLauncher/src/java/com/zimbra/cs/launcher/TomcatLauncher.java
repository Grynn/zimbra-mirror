/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.launcher;

import java.lang.reflect.Method;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class TomcatLauncher {

	private static Class mBootstrapClass;
	private static Method mMainMethod;
	
	public static void stop() {
		try {
			Object args = new String[] { "stopd" };
			mMainMethod.invoke(null, args);
		} catch (Exception e) {
			System.out.println("[tomcat launcher] exception occurred during stop: " + e);
			e.printStackTrace();
		}
		System.exit(0);
	}

	public static void start() {
		try {
			Object args = new String[] { "startd" };
			mMainMethod.invoke(null, args);
			Thread.sleep(Long.MAX_VALUE);
		} catch (Exception e) {
			System.out.println("[tomcat launcher] exception occurred during start: " + e);
			e.printStackTrace(System.out);
		}
		stop();  // In case our sleep got interrupted...
	}

	public static void main(String[] args) throws Exception {
		mBootstrapClass = Class.forName("org.apache.catalina.startup.Bootstrap");
		mMainMethod = mBootstrapClass.getMethod("main", (new String[0]).getClass());

		SignalHandler termHandler = new SignalHandler() {
			public void handle(Signal sig) {
				System.out.println("[tomcat launcher] got signal " + sig + " invoking stop");
				stop();
			}
		};
		
		Signal.handle(new Signal("TERM"), termHandler);
		
		start();
	}
}
