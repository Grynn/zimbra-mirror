package com.zimbra.cs.offline.util;

import java.io.IOException;

/**
 * 
 * This class is a mere java launcher of any executable such as a shell script.
 * It exists so that we can generate Mac OS X app using Jar Bundler
 * 
 * @author jjzhuang
 *
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
