package com.zimbra.qa.selenium.framework.core;

/**
 * The DevEnvironment class defines whether the harness is running in
 * the Zimbra Dev Environment
 * @author Matt Rhoades
 *
 */
public class DevEnvironment {

	/**
	 * Configure the harness for the dev environment
	 * @param flag true - if in dev environment, false otherwise
	 */
	public static void setDevEnvironment(boolean flag) {
		getSingleton().isDevEnvironment = flag;
	}
	
	public static boolean isUsingDevEnvironment() {
		return (getSingleton().isDevEnvironment);
	}
	
	private boolean isDevEnvironment = false;
	
	/**
	 * Singleton
	 */

    private volatile static DevEnvironment singleton;
 
    private DevEnvironment() {
    }
 
    public static DevEnvironment getSingleton() {
        if(singleton==null) {
            synchronized(DevEnvironment.class) {
                if(singleton == null) {
                    singleton = new DevEnvironment();
                }
            }
        }
        return singleton;
    }
}
