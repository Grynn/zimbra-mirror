/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.framework.util;

import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * A utility that delays execution
 * 
 * The class can sleep for a specified duration.
 * 
 * Preferably, test cases should use the defined Small, Medium, Long, 
 * and Very Long delays.
 * 
 * @author Matt Rhoades
 *
 */
public class SleepUtil {
	private static Logger logger = LogManager.getLogger(SleepUtil.class);

	/// Public methods
	public static int SleepGranularity = 1000;
	
	public static void sleep(long millis) {
		
		Date start = new Date();
		
		try {
		
			long target = millis;
			long total = 0; // The total milliseconds slept in this method
			
			try {
				
				while (millis > 0) {
					
					if ( millis >= SleepGranularity) {
						logger.info("Sleep: "+ SleepGranularity +" milliseconds ... ("+ total +"/"+ target +")");
						Thread.sleep(SleepGranularity);
						total += SleepGranularity;
					} else {
						logger.info("Sleep: "+ millis +" milliseconds ... ("+ millis +"/"+ target +")");
						Thread.sleep(millis);
						total += millis;
					}
					
					millis -= SleepGranularity;
				}
	
			} catch (InterruptedException e) {
				logger.warn("Sleep was interuppted", e);
			}
			
		} finally {
			SleepMetrics.RecordSleep((new Throwable()).getStackTrace(), millis, start, new Date());
		}
	}
	
	/**
	 * Sleep a 500 msec
	 */
	public static void sleepVerySmall() {
		sleep(ZimbraSeleniumProperties.getIntProperty("very_small_wait", 500));
	}
	
	/**
	 * Sleep a 1000 msec
	 */
	public static void sleepSmall() {
		sleep(ZimbraSeleniumProperties.getIntProperty("small_wait", 1000));
	}
	
	/**
	 * Sleep a 2000 msec
	 */
	public static void sleepMedium() {
		sleep(ZimbraSeleniumProperties.getIntProperty("medium_wait", 2000));
	}
	
	/**
	 * Sleep a 4000 msec
	 */
	public static void sleepLong() {
		sleep(ZimbraSeleniumProperties.getIntProperty("long_wait", 4000));
	}
	
	/**
	 * Sleep a 10,000 msec
	 */
	public static void sleepVeryLong() {
		sleep(ZimbraSeleniumProperties.getIntProperty("very_long_wait", 10000));
	}
	
	
}
