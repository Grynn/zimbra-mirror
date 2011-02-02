/**
 * 
 */
package com.zimbra.qa.selenium.framework.items;

import java.util.Locale;

import org.apache.log4j.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;


/**
 * @author Matt Rhoades
 *
 */
public class ZimletItem  {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	protected boolean gPreferencesEnabled = false;
	protected String gPreferencesName = null;
	protected String gPreferencesDescription = null;
	
	protected String gFolderTreeName = null;
	protected String gFolderTreeImage = null;
	protected String gFolderTreeLocator = null;
	
	/**
	 * Create a new ZimletItem object
	 */
	public ZimletItem() {
	}

	


	/**
	 * Return the 'display' string for the specified localization key.
	 * <p>
	 * The harness uses the zimlet name to look-up the translation properties file
	 * @param key
	 * @return
	 */
	public String getTranslation(String key) {
		return (null);
	}
	
	public void setPreferencesEnabled(boolean enabled) {
		gPreferencesEnabled = enabled;
	}
	
	public boolean getPreferencesEnabled() {
		return (gPreferencesEnabled);
	}
	
	public void setPreferencesName(String name) {
		gPreferencesName = name;
	}
	
	public String getPreferencesName() {
		return (gPreferencesName);
	}
	
	public void setPreferencesDescription(String description) {
		gPreferencesDescription = description;
	}
	
	public String getPreferencesDescription() {
		return (gPreferencesDescription);
	}
	
	public void setFolderTreeName(String name) {
		gFolderTreeName = name;
	}
	
	public String getFolderTreeName() {
		return (gFolderTreeName);
	}

	public void setFolderTreeImage(String image) {
		gFolderTreeImage = image;
	}
	
	public String getFolderTreeImage() {
		return (gFolderTreeImage);
	}

	public void setFolderTreeLocator(String locator) {
		gFolderTreeLocator = locator;
	}

	public String getFolderTreeLocator() {
		return (gFolderTreeLocator);
	}
	
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(ZimletItem.class.getSimpleName()).append('\n');
		sb.append("Name: ").append(gPreferencesName).append('\n');
		sb.append("Description: ").append(gPreferencesDescription).append('\n');
		sb.append("Enabled: ").append(gPreferencesEnabled).append('\n');
		return (sb.toString());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((gFolderTreeName == null) ? 0 : gFolderTreeName.hashCode());
		result = prime
				* result
				+ ((gPreferencesName == null) ? 0 : gPreferencesName.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
//		if (getClass() != obj.getClass())
//			return false;
		if ( !(obj instanceof ZimletItem) )
			return false;
		
		ZimletItem other = (ZimletItem) obj;
		
		String thisFolderTreeName = this.getFolderTreeName();
		String thisPreferencesName = this.getPreferencesName();
		String otherFolderTreeName = other.getFolderTreeName();
		String otherPreferencesName = other.getPreferencesName();
		
		// If folder name are equal OR If preference name are equal, then objects are equal
		
		if ((otherFolderTreeName == null) && (otherPreferencesName == null) )
			return false; // Nothing to compare

		if ((thisFolderTreeName == null) && (thisPreferencesName == null) )
			return false; // Nothing to compare
			
		if (thisFolderTreeName != null)
			return (thisFolderTreeName.equals(otherFolderTreeName));
			
		if (thisPreferencesName != null)
			return (thisPreferencesName.equals(otherPreferencesName));
			
		// Log a warning with a stack trace
		logger.warn("How did we get here?", new Throwable());
		return (false);
	
	}

	public static class CoreZimletItem extends ZimletItem {

		/**
		 * The zimlet name, such as com_zimbra_email
		 */
		protected String zName = null;
		
		/**
		 * An object containing the localized strings
		 */
		protected I18N L10N = new I18N();
		

		protected CoreZimletItem(String name) {
			zName = name;
			L10N.zAddBundlename(zName);
			setLocale(Locale.ENGLISH); // Default
		}

		protected void setLocale(Locale locale) {
			logger.info("Zimlet: setLocale("+ locale +")");
			L10N.setLocale(locale);
		}
		
		protected void setLocale(ZimbraAccount account) {
			logger.info("Zimlet: setLocale("+ account.EmailAddress +")");
			setLocale(account.getLocalePreference());
		}
		
		protected void setLocale(AbsApplication app) {
			logger.info("Zimlet: setLocale("+ app.myApplicationName() +")");
			setLocale(app.zGetActiveAccount());
		}
		
		public String getPreferencesName() {
			return (L10N.zGetString("label"));
		}

		public String getPreferencesDescription() {
			return (L10N.zGetString("description"));
		}
		
		public String getFolderTreeName() {
			String key = "label";
			if ( zName.equals("com_zimbra_webex") )
				key = "WebExZimlet_label";
			return (L10N.zGetString(key));
		}

		public String prettyPrint() {
			StringBuilder sb = new StringBuilder();
			sb.append(ZimletItem.class.getSimpleName()).append('\n');
			sb.append("ID: ").append(zName).append('\n');
			sb.append("Name: ").append(getPreferencesName()).append('\n');
			sb.append("Description: ").append("unknown").append('\n');
			return (sb.toString());
		}

		
		// Attach Contacts Zimlet
		public static CoreZimletItem getAttachContactsZimlet(AbsApplication app) {
			return (getCoreZimlet("com_zimbra_attachcontacts", app));
		}

		// Email Attacher Zimlet
		public static CoreZimletItem getAttachMailZimlet(AbsApplication app) {
			return (getCoreZimlet("com_zimbra_attachmail", app));
		}

		// Date Zimlet
		public static CoreZimletItem getDateZimlet(AbsApplication app) {
			return (getCoreZimlet("com_zimbra_date", app));
		}

		// Drag and Drop Zimlet
		public static CoreZimletItem getDnDZimlet(AbsApplication app) {
			return (getCoreZimlet("com_zimbra_dnd", app));
		}

		// Email Attacher Zimlet
		public static CoreZimletItem getEmailZimlet(AbsApplication app) {
			return (getCoreZimlet("com_zimbra_email", app));
		}

		// LinkedIn Zimlet
		public static CoreZimletItem getLinkedInZimlet(AbsApplication app) {
			return (getCoreZimlet("com_zimbra_linkedin", app));
		}


		// Phone Zimlet
		public static CoreZimletItem getPhoneZimlet(AbsApplication app) {
			return (getCoreZimlet("com_zimbra_phone", app));
		}

		// Social Zimlet
		public static CoreZimletItem getSocialZimlet(AbsApplication app) {
			return (getCoreZimlet("com_zimbra_social", app));
		}

		// Search Highlighter Zimlet
		public static CoreZimletItem getSrchHighlighterZimlet(AbsApplication app) {
			return (getCoreZimlet("com_zimbra_srchhighlighter", app));
		}

		// URL Zimlet
		public static CoreZimletItem getUrlZimlet(AbsApplication app) {
			return (getCoreZimlet("com_zimbra_url", app));
		}

		// WebEx Zimlet
		public static CoreZimletItem getWebExZimlet(AbsApplication app) {
			return (getCoreZimlet("com_zimbra_webex", app));
		}


		// Helper method
		private static CoreZimletItem getCoreZimlet(String name, AbsApplication app) {
			CoreZimletItem zimlet = new CoreZimletItem(name);
			zimlet.setLocale(app);
			return (zimlet);
		}


	}

    
    
}
