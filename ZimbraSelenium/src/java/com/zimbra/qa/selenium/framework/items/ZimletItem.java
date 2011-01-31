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
	
	/**
	 * Create a new ZimletItem object
	 */
	public ZimletItem() {
	}

	public ZimletItem(String name) {
		gName = name;
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

		
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(ZimletItem.class.getSimpleName()).append('\n');
		sb.append("Name: ").append(getName()).append('\n');
		return (sb.toString());
	}

	
	// BEGIN: Core Zimlet methods
	
	// LinkedIn Zimlet
    private volatile static ZimletItem ZimletLinkedIn = null;
    public static ZimletItem getLinkedinZimlet() {
        if( ZimletLinkedIn == null ) {
            synchronized(ZimletLinkedIn){
            	if ( ZimletLinkedIn == null ) {
	            	ZimletLinkedIn = new ZimletItem();
	            	ZimletLinkedIn.setName("LinkedIn");
	            	ZimletLinkedIn.setImage("ImgLinkedinZimletIcon");
            	}
            }
        }
        return ZimletLinkedIn;

	}
    
	// WebEx Zimlet
    private volatile static ZimletItem ZimletWebEx = null;
    public static ZimletItem getWebExZimlet() {
        if( ZimletWebEx == null ) {
            synchronized(ZimletWebEx){
            	if ( ZimletWebEx == null ) {
            		ZimletWebEx = new ZimletItem();
            		ZimletWebEx.setName("LinkedIn");
            		ZimletWebEx.setImage("ImgLinkedinZimletIcon");
            	}
            }
        }
        return ZimletWebEx;

	}
	
	// END: Core Zimlet methods
	
 
    
    
}
