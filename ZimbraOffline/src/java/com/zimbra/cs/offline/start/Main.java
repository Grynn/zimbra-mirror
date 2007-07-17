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
			main.invoke(null, (Object)(new String[] {}));
		} catch (Exception x) {
			x.printStackTrace(System.err);
			System.exit(2);
		}
	}
}
