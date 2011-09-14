package com.zimbra.qa.selenium.framework.util.performance;

/**
 * A PerfToken is used to track one instance of measuring the browser performance
 * @author Matt Rhoades
 *
 */
public class PerfToken {
	
	private static int c = 1;
	
	private int t;
	
	public PerfToken() {
		t = c++;
	}
	
	public String toString() {
		return (""+ t);
	}

}
