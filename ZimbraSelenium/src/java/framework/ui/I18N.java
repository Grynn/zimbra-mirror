package framework.ui;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.util.HarnessException;

/**
 * A class that contains all Zimbra translations
 * @author Matt Rhoades
 *
 */
public class I18N {
	private static Logger logger = LogManager.getLogger(I18N.class);

	// TODO: Need to split this class into the respective projects
	//
	// For instance, unless you are testing admin console, you don't
	// need the ZaMsg bundle.
	//
	// Removing unused bundles should reduce number of conflicts in the keys
	//
	
	
	protected final String ResourceBundleAjxMsg 	= "AjxMsg";
	protected final String ResourceBundleI18nMsg	= "I18nMsg";
	protected final String ResourceBundleZaMsg 		= "ZaMsg";
	protected final String ResourceBundleZbMsg 		= "ZbMsg";
	protected final String ResourceBundleZhMsg 		= "ZhMsg";
	protected final String ResourceBundleZmMsg 		= "ZmMsg";
	protected final String ResourceBundleZsMsg 		= "ZsMsg";
	protected final String ResourceBundleZMsg 		= "ZMsg";
	
	/**
	 * A mapping of msg filename to resource bundles
	 */
	protected Map<String, ResourceBundle> bundles = null;
	
	public I18N(String locale) {
		this(getLocaleFromString(locale));
	}
	
	public I18N(Locale locale) {
		bundles = new HashMap<String, ResourceBundle>();
		bundles.put("AjxMsg", 	ResourceBundle.getBundle(ResourceBundleAjxMsg, locale));
		bundles.put("I18nMsg",	ResourceBundle.getBundle(ResourceBundleI18nMsg, locale));
		bundles.put("ZaMsg", 	ResourceBundle.getBundle(ResourceBundleZaMsg, locale));
		bundles.put("ZbMsg", 	ResourceBundle.getBundle(ResourceBundleZbMsg, locale));
		bundles.put("ZhMsg", 	ResourceBundle.getBundle(ResourceBundleZhMsg, locale));
		bundles.put("ZmMsg", 	ResourceBundle.getBundle(ResourceBundleZmMsg, locale));
		bundles.put("ZsMsg", 	ResourceBundle.getBundle(ResourceBundleZsMsg, locale));
		bundles.put("ZMsg", 	ResourceBundle.getBundle(ResourceBundleZMsg, locale));
	}

	/**
	 * Get the first matching translation for the given key
	 * @param key
	 * @return
	 * @throws HarnessException
	 */
	public String getString(String key) throws HarnessException {
		
		for (String id : bundles.keySet() ) {
			String value = getString(id, key);
			if ( value != null ) {
				return (value);
			}
		}

		throw new HarnessException("Unable to find localization for "+ key);
		
	}
	
	/**
	 * Get the translation for the given key using the given bundle id
	 * @param bundlename
	 * @param key
	 * @return
	 * @throws HarnessException 
	 */
	public String getString(String bundlename, String key) throws HarnessException {
		
		if ( !bundles.containsKey(bundlename) )
			throw new HarnessException("Unknown bundle ID " + bundlename);

		ResourceBundle bundle = bundles.get(bundlename);
		
		// Make sure the bundle has the key
		if ( !bundle.containsKey(key) ) {
			return (null); // key not found in this bundle
		}
		
		// Return the value
		String value = bundle.getString(key);
		logger.info(String.format("Localization: %s (key, value) = (%s, %s)", bundlename, key, value));		
		return (value);

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
    private static Locale getLocaleFromString(String localeString)
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
    
//	public static void main(String[] args) throws HarnessException {
//    	BasicConfigurator.configure();
//    	
//    	I18N l10n = new I18N(Locale.CHINESE);
//    	logger.info("AjxMsg formatCalDate : "+ l10n.getString("formatCalDate"));
//    	logger.info("I18nMsg currencyCode : "+ l10n.getString("currencyCode"));
//    	logger.info("ZaMsg favIconUrl : "+ l10n.getString("favIconUrl"));
//    	logger.info("ZbMsg ClientNameLong : "+ l10n.getString("ClientNameLong"));
//    	logger.info("ZhMsg aboveQuotedText : "+ l10n.getString("aboveQuotedText"));
//    	logger.info("ZmMsg above : "+ l10n.getString("above"));
//    	logger.info("ZMsg EMPTY_RESPONSE : "+ l10n.getString("EMPTY_RESPONSE"));
//    	logger.info("ZsMsg calendarSubjectCancelled : "+ l10n.getString("calendarSubjectCancelled"));
//	}
    
}
