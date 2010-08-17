package framework.util;

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
	
	public static void sleep(long millis) {
		try {
			logger.info("Sleep: "+ millis +" milliseconds ...");
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			logger.warn("Sleep was interuppted", e);
		}
	}
	
	public static void sleepSmall() {
		sleep(ZimbraSeleniumProperties.getIntProperty("small_wait", 1000));
	}
	
	public static void sleepMedium() {
		sleep(ZimbraSeleniumProperties.getIntProperty("medium_wait", 2000));
	}
	
	public static void sleepLong() {
		sleep(ZimbraSeleniumProperties.getIntProperty("long_wait", 4000));
	}
	
	public static void sleepVeryLong() {
		sleep(ZimbraSeleniumProperties.getIntProperty("very_long_wait", 10000));
	}
	
	
}
