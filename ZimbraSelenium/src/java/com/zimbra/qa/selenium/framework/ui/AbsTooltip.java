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
public abstract class AbsTooltip extends AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsTooltip.class);


	/**
	 * Create this Tooltip object that exists in the specified page
	 * @param application
	 */
	protected AbsTooltip(AbsApplication application) {		
		super(application);

		logger.info("new " + this.getClass().getCanonicalName());
		
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
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();

}
