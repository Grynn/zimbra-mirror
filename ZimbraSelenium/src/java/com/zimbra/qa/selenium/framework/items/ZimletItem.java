/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.*;


/**
 * @author Matt Rhoades
 *
 */
public class ZimletItem  {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	

	
	protected String gName = "undefined";
	protected String gImage = "undefined";
	protected String gLocator = "undefined";
	
	/**
	 * Create a new ZimletItem object
	 */
	public ZimletItem() {
	}

	public void setName(String name) {
		gName = name;
	}
	
	public String getName() {
		return (gName);
	}

	public void setImage(String image) {
		gImage = image;
	}
	
	public String getImage() {
		return (gImage);
	}

	public void setLocator(String locator) {
		gLocator = locator;
	}

	public String getLocator() {
		return (gLocator);
	}
	
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(ZimletItem.class.getSimpleName()).append('\n');
		sb.append("Name: ").append(getName()).append('\n');
		sb.append("Image: ").append(getImage()).append('\n');
		sb.append("Locator: ").append(getLocator()).append('\n');
		return (sb.toString());
	}

	
	// BEGIN: Core Zimlet methods
	
	// LinkedIn Zimlet
    private static ZimletItem ZimletLinkedIn = null;
    public static ZimletItem getLinkedinZimlet() {
    	if( ZimletLinkedIn == null ) {
    		ZimletLinkedIn = new ZimletItem();
    		ZimletLinkedIn.setName("LinkedIn");
    		ZimletLinkedIn.setImage("ImgLinkedinZimletIcon");
    	}
    	return ZimletLinkedIn;
    }
    
	// WebEx Zimlet
    private static ZimletItem ZimletWebEx = null;
    public static ZimletItem getWebExZimlet() {
    	if( ZimletWebEx == null ) {
    		ZimletWebEx = new ZimletItem();
    		ZimletWebEx.setName("WebEx");
    		ZimletWebEx.setImage("ImgWEBEX-panelIcon");
    	}
    	return ZimletWebEx;
    }

	
	// END: Core Zimlet methods
	
 
    
    
}
