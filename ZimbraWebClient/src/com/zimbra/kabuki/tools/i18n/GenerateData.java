/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */


package com.zimbra.kabuki.tools.i18n;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This class retrieves i18n information (e.g. month/weekday translations,
 * formatting patterns, etc.) and generates properties files for all the
 * locales known to Java in the JVM in which this program is run.
 * <p>
 * The <code>Locale.US</code> locale is assumed to be the default language.
 * This means that after the i18n information is gathered, the en_US data
 * is merged into the en data. Also, when the resources are written out,
 * the en data is saved without the language suffix. For example, if the
 * basename is "I18nMsg", then the en data will be saved as
 * "I18nMsg.properties" instead of "I18nMsg_en.properties". This is to
 * ensure that English is the default regardless of what locale the JVM
 * is running under.  
 * <p>
 * <strong>Note:</strong>
 * In order for the timezone information to be useful to the 
 * server, only specific timezones are queried and their identifiers are 
 * mapped to the identifiers expected by the server. In the future, the
 * server may change to support the Java identifiers removing the need
 * for such a mapping.
 */
public class GenerateData {
    
    //
    // Constants
    //
    
	private static int[] DATE_STYLES = { 
        DateFormat.SHORT, 
        DateFormat.MEDIUM, 
        DateFormat.LONG, 
        DateFormat.FULL 
    };
	
	private static String[] MONTHS = {
		"Jan", "Feb", "Mar", "Apr", "May", "Jun",
		"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
	};
	private static String[] WEEKDAYS = {
		"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"
	};
	
	private static final Locale DEFAULT_LOCALE = Locale.US;
	private static final Locale[] LOCALES = Locale.getAvailableLocales();
    
	//
	// Data
	//
	
	protected File dir;
	protected String basename;
	protected boolean client;
	protected boolean server;
	
	//
	// Public methods
	//
	
	public void setDirName(String dirname) {
	    File dir = new File(dirname);
	    if (!dir.isDirectory()) {
	        throw new IllegalArgumentException("not a directory");
	    }
	    this.dir = dir;
	} // setsetDirNameDir(String)
	
	public void setBaseName(String basename) {
	    this.basename = basename;
	} // setBaseName(String)

	public void setClient(boolean generate) {
		this.client = generate;
	}

	public void setServer(boolean generate) {
		this.server = generate;
	}

	public void generate() throws Exception {
        // generate properties for the available locales
        Map<Locale,Properties> map = new HashMap<Locale,Properties>();
		if (this.client) {
			for (Locale locale : LOCALES) {
				//System.out.println(locale);
				Properties props = generateClient(locale);
				map.put(locale, props);
			}
		}
		if (this.server) {
			Properties props = map.get(Locale.ENGLISH);
			if (props == null) {
				map.put(Locale.ENGLISH, props = new Properties());
			}
			generateServer(props);
		}

		// merge en and en_US and make it default
        Properties en = map.get(Locale.ENGLISH);
        Properties enUS = map.get(Locale.US);
		if (enUS != null) {
			Iterator pnames = enUS.keySet().iterator();
			while (pnames.hasNext()) {
				String pname = (String)pnames.next();
				String pvalue = enUS.getProperty(pname);
				en.put(pname, pvalue);
			}
		}
		map.remove(Locale.ENGLISH);
        map.put(Locale.US, en);
        
        // remove duplicates
		for (Locale locale : map.keySet()) {
			Properties props = map.get(locale);
            List<Properties> chain = getPropertyChain(map, locale);
            
            Iterator names = props.keySet().iterator();
            while (names.hasNext()) {
                String name = (String)names.next();
                String value = (String)props.get(name);
                
                if (isDuplicate(chain, name, value)) {
                    names.remove();
                }
            }
        }
        
        // save properties files
        Date date = new Date();
        for (Locale locale : map.keySet()) {
            Properties properties = map.get(locale);
            String suffix = locale.equals(DEFAULT_LOCALE) ? "" : "_" + locale; 
            File file = new File(this.dir, this.basename+suffix+".properties");
            PrintStream out = new PrintStream(new FileOutputStream(file));
            store(properties, out, locale.toString()+" generated on "+date);
            out.close();
        }
	} // generate()

    //
    // MAIN
    //
    
    public static void main(String[] argv) throws Exception {
        String dirname = ".";
        String basename = "I18nMsg";
		boolean client = true;
		boolean server = true;

		for (int i = 0; i < argv.length; i++) {
            String arg = argv[i];
            if (arg.equals("-d")) {
                dirname = argv[++i];
                continue;
            }
            if (arg.equals("-b")) {
                basename = argv[++i];
                continue;
            }
			if (arg.equals("-c") || arg.equals("-C")) {
				client = arg.equals("-c");
				continue;
			}
			if (arg.equals("-s") || arg.equals("-S")) {
				server = arg.equals("-s");
				continue;
			}
			if (arg.equals("-h")) {
                System.out.println("usage: java "+GenerateData.class.getName()+" (options)");
                System.out.println();
                System.out.println("options:");
                System.out.println("  -d dirname   Output directory.");
                System.out.println("  -b basename  Output file base name.");
				System.out.println("  -c | -s      Output client or server messages.");
				System.out.println("  -C | -S      Do NOT output client or server messages.");
				System.out.println("  -h           Help.");
                System.exit(1);
            }
        }
        
        GenerateData generator = new GenerateData();
        generator.setDirName(dirname);
        generator.setBaseName(basename);
		generator.setClient(client);
		generator.setServer(server);
		generator.generate();
        
    } // main(String[])

    //
    // Private static methods
    //
    
    private static Properties generateClient(Locale locale) {
        Properties props = new Properties();
        generateCalendarNames(props, locale);
        generateDateTimeFormats(props, locale);
        generateNumberFormats(props, locale);
        return props;
    } // generateClient(Locale):Properties

	private static Properties generateServer(Properties props) {
		props = props == null ? new Properties() : props;
		generateLocaleNames(props);
		return props;
	} // generateServer(Locale):Properties

    private static void store(Properties props, PrintStream out, String header) throws IOException {
        
        // sort keys
        Set keySet = props.keySet();
        List keyList = new LinkedList(keySet);
        Collections.sort(keyList);
        
        // save properties
        if (header != null) {
            out.print("# ");
            out.println(header);
        }
        Iterator iter = keyList.iterator();
        while (iter.hasNext()) {
            String pname = (String)iter.next();
            String pvalue = props.getProperty(pname);
            out.print(pname);
            out.print(" = ");
            int len = pvalue.length();
            for (int i = 0; i < len; i++) {
                int c = pvalue.charAt(i);
                switch (c) {
                    case '\\': out.print("\\\\"); break;
                    case '\r': out.print("\\r"); break;
                    case '\n': out.print("\\n"); break;
                    case '\t': out.print("\\t"); break;
                    default: {
                        if (c < 32 || c > 127) {
                            out.print("\\u");
                            String hex = Integer.toString(c, 16);
                            for (int j = hex.length(); j < 4; j++) {
                                out.print('0');
                            }
                            out.print(hex);
                        }
                        else {
                            out.print((char)c);
                        }
                    }
                }
            }
            out.println();
        }
        
    } // store(Properties,OutputStream,String)
    
    // Generation methods

	private static void generateLocaleNames(Properties props) {
		for (int i = 0; i < LOCALES.length; i++) {
            Locale LOCALE = LOCALES[i];
			String languageCode = LOCALE.getLanguage();
			String countryCode = toSuffix(LOCALE.getCountry());
			String variantCode = toSuffix(LOCALE.getVariant());
			String localeCode = languageCode + countryCode + variantCode;
            props.setProperty(localeCode, LOCALE.getDisplayName(LOCALE));
		}
	} // generateLocaleNames(Properties)

    private static void generateCalendarNames(Properties props, Locale locale) {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat mediumMonthFormatter = new SimpleDateFormat("MMM", locale);
        SimpleDateFormat longMonthFormatter = new SimpleDateFormat("MMMM", locale);
        for (int i = 0; i < 12; i++) {
            calendar.set(Calendar.MONTH, i);
            Date date = calendar.getTime();
            props.setProperty("month"+MONTHS[i]+"Medium", mediumMonthFormatter.format(date));
            props.setProperty("month"+MONTHS[i]+"Long", longMonthFormatter.format(date));
        }
        SimpleDateFormat mediumWeekdayFormatter = new SimpleDateFormat("EEE", locale);
        SimpleDateFormat longWeekdayFormatter = new SimpleDateFormat("EEEE", locale);
        calendar = Calendar.getInstance(locale);        
        for (int i = 0; i < 7; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, i);
            Date date = calendar.getTime();
            props.setProperty("weekday"+WEEKDAYS[i]+"Medium", mediumWeekdayFormatter.format(date));
            props.setProperty("weekday"+WEEKDAYS[i]+"Long", longWeekdayFormatter.format(date));
        }
       int firstDayOfWeek = calendar.getFirstDayOfWeek();
       props.setProperty("firstDayOfWeek", Integer.toString(firstDayOfWeek));
    } // generateCalendarNames(Properties,Locale)
    
    private static void generateDateTimeFormats(Properties props, Locale locale) {
        for (int style : DATE_STYLES) {
            props.setProperty("formatDate"+toStyle(style), getDateFormat(locale, style));
        }
        for (int style : DATE_STYLES) {
            props.setProperty("formatTime"+toStyle(style), getTimeFormat(locale, style));
        }
        String shortDateFormat = getDateFormat(locale, DateFormat.SHORT);
        String shortTimeFormat = getTimeFormat(locale, DateFormat.SHORT);
        String shortDateTimeFormat = getDateTimeFormat(locale, DateFormat.SHORT, DateFormat.SHORT);
        int shortDateIndex = shortDateTimeFormat.indexOf(shortDateFormat);
        int shortTimeIndex = shortDateTimeFormat.indexOf(shortTimeFormat);
        int[] indexes = new int[2];
        int[] lengths = new int[2];
        String[] params = new String[2];
        if (shortDateIndex < shortTimeIndex) {
            indexes[0] = shortDateIndex;
            indexes[1] = shortTimeIndex;
            lengths[0] = shortDateFormat.length();
            lengths[1] = shortTimeFormat.length();
            params[0] = "{0}";
            params[1] = "{1}";
        }
        else {
            indexes[0] = shortTimeIndex;
            indexes[1] = shortDateIndex;
            lengths[0] = shortTimeFormat.length();
            lengths[1] = shortDateFormat.length();
            params[0] = "{1}";
            params[1] = "{0}";
        }
		int place = 0;
		StringBuffer format = new StringBuffer();
		for (int i = 0; i < indexes.length; i++) {
		    int index = indexes[i];
		    if (place < index) {
		        format.append(shortDateTimeFormat.substring(place, index));
		    }
		    format.append(params[i]);
		    place = index + lengths[i];
		}
		if (place < shortDateTimeFormat.length()) {
		    format.append(shortDateTimeFormat.substring(place));
		}
		props.setProperty("formatDateTime", format.toString());
		
		DateFormat eraFormatter = new SimpleDateFormat("G", locale);
		Calendar era = Calendar.getInstance(locale);
		props.setProperty("eraAD", eraFormatter.format(era.getTime()));
		era.set(Calendar.ERA, GregorianCalendar.BC);
		props.setProperty("eraBC", eraFormatter.format(era.getTime()));
		DateFormat ampmFormatter = new SimpleDateFormat("a", locale);
		Calendar calendar = Calendar.getInstance(locale);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		props.setProperty("periodAm", ampmFormatter.format(calendar.getTime()));
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		props.setProperty("periodPm", ampmFormatter.format(calendar.getTime()));
    } // generateDateTimeFormats(Properties,Locale)
    
    private static void generateNumberFormats(Properties props, Locale locale) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        props.setProperty("formatNumber", toPattern(NumberFormat.getNumberInstance(locale)));
        props.setProperty("formatNumberCurrency", toPattern(currencyFormatter));
        props.setProperty("formatNumberInteger", toPattern(NumberFormat.getIntegerInstance(locale)));
        props.setProperty("formatNumberPercent", toPattern(NumberFormat.getPercentInstance(locale)));
        Currency currency = currencyFormatter.getCurrency();
        props.setProperty("currencyCode", currency.getCurrencyCode());
        props.setProperty("currencySymbol", currency.getSymbol());
    	DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
    	props.setProperty("numberNaN", symbols.getNaN());
    	props.setProperty("numberInfinity", symbols.getInfinity());
    	props.setProperty("numberZero", Character.toString(symbols.getZeroDigit()));
    	props.setProperty("numberSignMinus", Character.toString(symbols.getMinusSign()));
    	props.setProperty("numberSignPercent", Character.toString(symbols.getPercent()));
    	props.setProperty("numberSignPerMill", Character.toString(symbols.getPerMill()));
    	props.setProperty("numberSeparatorDecimal", Character.toString(symbols.getDecimalSeparator()));
    	props.setProperty("numberSeparatorGrouping", Character.toString(symbols.getGroupingSeparator()));
    	props.setProperty("numberSeparatorMoneyDecimal", Character.toString(symbols.getMonetaryDecimalSeparator()));
    } // generateNumberFormats(Properties,Locale)
    
    // Other methods
    
    private static List<Properties> getPropertyChain(Map<Locale,Properties> map, Locale locale) {
        List<Properties> chain = new LinkedList<Properties>();
        
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (variant != null && variant.length() > 0) {
            Properties props = map.get(new Locale(language, country));
            if (props != null) {
                chain.add(props);
            }
        }
        if (country != null && country.length() > 0) {
            Properties props = map.get(new Locale(language));
            if (props != null) {
                chain.add(props);
            }
        }
        if (!locale.equals(DEFAULT_LOCALE)) {
            chain.add(map.get(DEFAULT_LOCALE));
        }
        return chain;
    }
    
    private static boolean isDuplicate(List<Properties> chain, String name, String value) {
        for (Properties props : chain) {
            String pvalue = (String)props.get(name);
            if (pvalue != null) {
                return pvalue.equals(value);
            }
        }
        return false;
    }
    
    // Convenience methods

	/***
	private static String toString(Collection collection) {
        StringBuffer str = new StringBuffer();
        Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            str.append(iter.next());
            str.append(' ');
        }
        return str.toString();
    }
	/***/

	private static String getDateFormat(Locale locale, int style) {
        return toPattern(DateFormat.getDateInstance(style, locale)); 
    }
    private static String getTimeFormat(Locale locale, int style) {
        return toPattern(DateFormat.getTimeInstance(style, locale)); 
    }
    private static String getDateTimeFormat(Locale locale, int dateStyle, int timeStyle) {
        return toPattern(DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale)); 
    }

	public static String toStyle(int style) {
		switch (style) {
			case DateFormat.SHORT: return "Short";
			case DateFormat.MEDIUM: return "Medium";
			case DateFormat.LONG: return "Long";
			case DateFormat.FULL: return "Full";
		}
		// Note: should never get here!
		return "Unknown";
	}

	public static String toPattern(DateFormat formatter) {
		try {
			SimpleDateFormat simpleDateFormatter = (SimpleDateFormat)formatter;
			return simpleDateFormatter.toPattern();
		}
		catch (Exception e) {
			return "???";
		}
	}

	public static String toPattern(NumberFormat formatter) {
		try {
			DecimalFormat decimalFormatter = (DecimalFormat)formatter;
			return decimalFormatter.toPattern();
		}
		catch (Exception e) {
			return "???";
		}
	}

	public static String toCamel(String s) {
	    if (s == null || s.length() == 0) {
	        return "";	        
		}
	    s = s.toLowerCase();
	    return Character.toUpperCase(s.charAt(0))+s.substring(1);
	}
	
	public static String toSuffix(String s) {
	    return s == null || s.length() == 0 ? "" : "_"+s;
	}

} // class GenerateI18nData
