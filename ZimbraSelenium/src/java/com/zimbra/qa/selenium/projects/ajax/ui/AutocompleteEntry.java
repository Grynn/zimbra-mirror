package com.zimbra.qa.selenium.projects.ajax.ui;

import org.apache.log4j.*;

public class AutocompleteEntry {
	protected static Logger logger = LogManager.getLogger(AutocompleteEntry.class);

	public static class Icon {

		public static final Icon ImgGALContact = new Icon("ImgGALContact");
		
		protected String Name;
		protected Icon(String name) {
			Name = name;
		}
		
		public String toString() {
			return(Name);
		}
		
		public static Icon getIconFromImage(String image) {
			if ( image.equals("ImgGALContact") )
				return (ImgGALContact);
			else
				return (null);
		}
	}
	
	
	
	
	protected Icon MyIcon = null;
	protected String MyAddress = null;
	protected boolean MyHasForget = false;
	protected String MyLocator = null;

	public AutocompleteEntry(Icon icon, String address, boolean hasForget, String locator) {
		MyIcon = icon;
		MyAddress = address;
		MyHasForget = hasForget;
		MyLocator = locator;
		
		logger.info(prettyPrint());
	}
	
	public Icon getType() {
		return (MyIcon);
	}
	
	public boolean isType(Icon icon) {
		return ( MyIcon.equals(icon) );
	}
	
	public String getAddress() {
		return (MyAddress);
	}
	
	public boolean hasForget() {
		return (MyHasForget);
	}

	public String getLocator() {
		return (MyLocator);
	}
	
	public String toString() {
		return (MyAddress);
	}
	
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName()).append('\n');
		sb.append("Icon: ").append(MyIcon).append('\n');
		sb.append("Address: ").append(MyAddress).append('\n');
		sb.append("Forget: ").append(MyHasForget).append('\n');
		sb.append("Locator: ").append(MyLocator).append('\n');
		return (sb.toString());
	}

}
