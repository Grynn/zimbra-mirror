package com.zimbra.qa.selenium.framework.ui;

import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * A class that contains Zimbra translations
 * @author Matt Rhoades
 *
 */
public class I18N {
	private static Logger logger = LogManager.getLogger(I18N.class);
	
	/*
	 * See http://wiki.zimbra.com/wiki/Translations
	 */
	
    // I18nMsg.properties:	Internationalization messages such as names of months, date and time formats, etc. 
	// AjxMsg.properties:	Messages used by the ajax toolkit. 
	// ZMsg.properties:		Common messages such as server error messages, etc. 
	// ZaMsg.properties:	Messages used by the Zimbra Admin web client. 
	// ZmMsg.properties:	Messages used by the Zimbra End User web client. 
	// ZhMsg.properties:	Messages used by the Zimbra End User basic web client. 
	// ZsMsg.properties:	Messages used by the Zimbra Server when automatically replying to appointment requests for locations and resources. (The language used for the outgoing messages is based on the server's default locale, not the client.) 

	public enum Catalog {
		I18nMsg, AjxMsg, ZMsg, ZaMsg, ZhMsg, ZmMsg, ZsMsg
	}
	
    public static final String CONTEXT_MENU_ITEM_NEW_FOLDER = "New Folder"; // TODO: ZmMsg: key: newFolder
    public static final String CONTEXT_MENU_ITEM_DELETE = "Delete"; // TODO: ZMsg: key: del (?)

	/**
	 * My current Locale
	 */
	protected Locale currentLocale = Locale.getDefault();
	
	/**
	 * A mapping of msg filename to resource bundles
	 */
	protected Map<String, ResourceBundle> bundles = new HashMap<String, ResourceBundle>();
	
	/**
	 * 
	 * @param locale
	 */
	public I18N() {
	}
	
	public Locale getLocale() {
		return (currentLocale);
	}
	
	public void setLocale(Locale locale) {
		
		currentLocale = locale;

		// Clear the bundles, so that the mapping is rebuilt the next time a key/value is requested
		for (Map.Entry<String, ResourceBundle> entry : bundles.entrySet()) {
			logger.info("bundle: set value to null for "+ entry.getKey());
			entry.setValue(null);
		}

	}
	
	public void zAddBundlename(Catalog catalog) {
		zAddBundlename(catalog.toString());
	}
	
	public void zAddBundlename(String catalog) {
		if ( !bundles.containsKey(catalog) ) {
			
			// Remember the new bundlename
			logger.info("bundle: intialize value to null for "+ catalog);
			bundles.put(catalog, null);

		}
	}

	/**
	 * Get the translation for the given key using the given bundle id
	 * @param bundlename
	 * @param key
	 * @return
	 * @throws HarnessException 
	 */
	public String zGetStringFromBundle(String catalog, String key) {
		
		if ( catalog == null )
			throw new NullPointerException("catalog was null");
		if ( key == null )
			throw new NullPointerException("key was null");

		// If any bundles are null, load them now
		checkBundles();
		
		// Get the specified bundle
		ResourceBundle bundle = bundles.get(catalog);
		if ( bundle == null )
			throw new NullPointerException("bundle was null");
		
		// Check if the bundle has the key
		if ( !bundle.containsKey(key) ) {
			return (null);
		}
		
		// Get the value from the bundle
		String value = bundle.getString(key);
		
		logger.info(String.format("Localization: %s (key, value) = (%s, %s)", catalog, key, value));
		
		return (value);
		
	}
	
	/**
	 * Get the first matching translation for the given key
	 * @param key
	 * @return
	 * @throws HarnessException
	 */
	public String zGetString(String key) {
				
		// If any bundles are null, load them now
		checkBundles();
		
		List<String> values = new ArrayList<String>();
		
		for (String catalog : bundles.keySet()) {
			String value = zGetStringFromBundle(catalog, key);
			if ( value != null )
				values.add(value);
		}
		
		if ( values.size() == 0) {
			logger.error("No match for key: "+ key);
			return (null);
		}
		
		if ( values.size() > 1) {
			logger.warn("Multiple bundles matched key: "+ key);
		}
		
		logger.info(String.format("Localization: (key, value) = (%s, %s)", key, values.get(0)));
		return (values.get(0));
		
	}
	
	
	

    /**
     * Rebuild bundles to include all bundles from bundlename, using the currentLocale
     */
    protected void checkBundles() {
    	
		// Rebuild any bundle that is null
    	for (Map.Entry<String, ResourceBundle> entry : bundles.entrySet()) {
    		if ( entry.getValue() == null ) {
    			entry.setValue(ResourceBundle.getBundle(entry.getKey(), currentLocale));
    		}
    	}

    }
    
    /**
     * http://www.java2s.com/Code/Java/Network-Protocol/GetLocaleFromString.htm
     * Convert a string based locale into a Locale Object.
     * Assumes the string has form "{language}_{country}_{variant}".
     * Examples: "en", "de_DE", "_GB", "en_US_WIN", "de__POSIX", "fr_MAC"
     *  
     * @param localeString The String
     * @return the Locale
     */
    public static Locale getLocaleFromString(String localeString)
    {
        if (localeString == null)
        {
            return null;
        }
        localeString = localeString.trim();
        if (localeString.toLowerCase().equals("default"))
        {
            return Locale.getDefault();
        }

        // Extract language
        int languageIndex = localeString.indexOf('_');
        String language = null;
        if (languageIndex == -1)
        {
            // No further "_" so is "{language}" only
            return new Locale(localeString, "");
        }
        else
        {
            language = localeString.substring(0, languageIndex);
        }

        // Extract country
        int countryIndex = localeString.indexOf('_', languageIndex + 1);
        String country = null;
        if (countryIndex == -1)
        {
            // No further "_" so is "{language}_{country}"
            country = localeString.substring(languageIndex+1);
            return new Locale(language, country);
        }
        else
        {
            // Assume all remaining is the variant so is "{language}_{country}_{variant}"
            country = localeString.substring(languageIndex+1, countryIndex);
            String variant = localeString.substring(countryIndex+1);
            return new Locale(language, country, variant);
        }
    }


}
