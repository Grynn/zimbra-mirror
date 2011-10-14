package com.zimbra.qa.selenium.framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.*;

/**
 * A <code>AbsTooltip</code> object represents a popup tooltip
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsTooltip {
	protected static Logger logger = LogManager.getLogger(AbsTooltip.class);

	protected AbsTab MyTab = null;
	
	/**
	 * Create this Tooltip object that exists in the specified page
	 * @param application
	 */
	protected AbsTooltip(AbsTab tab) {		
		logger.info("new " + this.getClass().getCanonicalName());
		
		MyTab = tab;
	}
	
	/**
	 * Get the text contents of the tooltip
	 * @return
	 * @throws HarnessException
	 */
	public abstract String zGetContents() throws HarnessException;
	
	/**
	 * Determine if the tooltip is currently visible
	 * @return
	 * @throws HarnessException
	 */
	public abstract boolean zIsActive() throws HarnessException;

	/**
	 * Wait for this tooltip to become active
	 * @throws HarnessException
	 */
	public void zWaitForActive() throws HarnessException {
		long millis = 10000;
		
		if ( zIsActive() ) {
			return; // Page became active
		}
		
		do {
			SleepUtil.sleep(SleepUtil.SleepGranularity);
			millis = millis - SleepUtil.SleepGranularity;
			if ( zIsActive() ) {
				return; // Page became active
			}
		} while (millis > SleepUtil.SleepGranularity);
		
		SleepUtil.sleep(millis);
		if ( zIsActive() ) {
			return;	// Page became active
		}

		throw new HarnessException("Page never became active");

	}
	
	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();

}
