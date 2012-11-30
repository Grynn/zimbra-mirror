package com.zimbra.qa.selenium.framework.ui;

import org.apache.log4j.*;

import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * A <code>AbsTooltip</code> object represents a popup tooltip
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsBubble extends AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsBubble.class);


	/**
	 * A locator to this bubble item
	 */
	protected String myLocator = null;
	
	/**
	 * The displayed text inside this bubble (email address, Display Name, etc.)
	 */
	private String myDisplayText = null;
	

	/**
	 * Create this Tooltip object that exists in the specified page
	 * @param application
	 */
	protected AbsBubble(AbsApplication application) {		
		super(application);

		logger.info("new " + this.getClass().getCanonicalName());
		
	}
	
	/**
	 * Parse the bubble object (i.e. set content and other properties) based
	 * on a locator that points to the specific bubble.
	 * @param bubbleLocator
	 * @return
	 * @throws HarnessException
	 */
	public abstract AbsBubble parseBubble(String bubbleLocator) throws HarnessException;

	public void setMyDisplayText(String myDisplayText) {
		this.myDisplayText = myDisplayText;
	}

	public String getMyDisplayText() {
		return myDisplayText;
	}

	/**
	 * Take an action (left click, hover, etc) on this bubble
	 * @param action
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zItem(Action action) throws HarnessException {
		throw new HarnessException("implement me " + action);
	}
	
	/**
	 * Take an action (right click, etc) and option (add to contacts, etc) on this bubble
	 * @param action
	 * @param option
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zItem(Action action, Button option) throws HarnessException {
		throw new HarnessException("implement me " + action + " " + option);
	}
	
	/**
	 * Take an action (right click, etc) and option (find, etc) and suboption (recieved from) on this bubble
	 * @param action
	 * @param option
	 * @param subOption
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zItem(Action action, Button option, Button subOption) throws HarnessException {
		throw new HarnessException("implement me " + action + " " + option);
	}
	
	/**
	 * Determine if the tooltip is currently visible
	 * @return
	 * @throws HarnessException
	 */
	public boolean zIsActive() throws HarnessException
	{
		logger.info(myPageName() + " zIsVisible()");
		
		if ( this.myLocator != null ) {
			
			boolean present = this.sIsElementPresent(this.myLocator);
			if ( !present )
				return (false);
			
			boolean visible = this.zIsVisiblePerPosition(this.myLocator, 0, 0);
			if ( !visible )
				return (false);

		}
		
		
		return (true);
		
	}

	
	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();

	
	/**
	 * Create a string description of this bubble (for logging purposes)
	 * @return
	 */
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(AbsBubble.class.getSimpleName()).append('\n');
		sb.append("Locator: ").append(this.myLocator).append('\n');
		sb.append("Display: ").append(this.myDisplayText).append('\n');
		return (sb.toString());
	}

}
