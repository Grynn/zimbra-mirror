
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
