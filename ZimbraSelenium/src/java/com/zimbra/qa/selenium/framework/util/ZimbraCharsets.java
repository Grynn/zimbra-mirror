package com.zimbra.qa.selenium.framework.util;

import java.util.*;

import org.apache.log4j.*;


public class ZimbraCharsets {
	protected static Logger logger = LogManager.getLogger(ZimbraCharsets.class);

	/**
	 * Supported sample charsets
	 * @author Matt Rhoades
	 *
	 */
	public static class ZCharset {
		
		public static final ZCharset UTF_8 = new ZCharset("UTF_8");	// http://en.wikipedia.org/wiki/UTF-8
		public static final ZCharset IEC_8859_1 = new ZCharset("IEC_8859_1");	// http://en.wikipedia.org/wiki/ISO/IEC_8859-1
		public static final ZCharset JIS_X_0202 = new ZCharset("JIS_X_0202");	// http://en.wikipedia.org/wiki/JIS_X_0202
		
		String Key = null;
		private ZCharset(String key) {
			Key = key;
		}
		public String toString() {
			return (Key);
		}
	}
	
	
	public static ZimbraCharsets getInstance() {
		if (Instance == null) {
			synchronized(ZimbraCharsets.class) {
				if ( Instance == null) {
					Instance = new ZimbraCharsets();
				}
			}
		}
		return (Instance);
	}

	/**
	 * Get the default example string for the specified charset
	 * @param charset
	 * @return
	 * @throws HarnessException
	 */
	public String getSample(ZCharset charset) throws HarnessException {
		return (samples.get(charset));
	}
	
	/**
	 * Get the charset samples.  Useful for dataprovider tests.
	 * @return
	 * @throws HarnessException
	 */
	public Object[] getSamples() throws HarnessException {
		return (samples.values().toArray());
	}

	/**
	 * Get the charset sample table.  Useful for dataprovider tests.<p>
	 * Each array item will be a ZCharset, String
	 * @return
	 * @throws HarnessException
	 */
	public Object[][] getSampleTable() throws HarnessException {
		Object[][] table = new Object[samples.size()][1];
		int i = 0;
		for(Map.Entry<ZCharset,String> entry : samples.entrySet()){
			table[i] = new Object[] { entry.getKey(), entry.getValue() };
			i++;
		}
		return (table);
	}

	/**
	 *  Sample strings
	 */
	private static final String sampleUTF_8 = "\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u004a\u004b\u0044\u004d\u004e\u004f";
	private static final String sampleIEC_8859_1 = "\u00c0\u00c1\u00c2\u00c3\u00c4\u00c5\u00c6\u00c7\u00c8\u00c9\u00ca\u00cb\u00cc\u00cd\u00ce\u00cf";
	private static final String sampleJIS_X_0202 = "\u3041\u3042\u3043\u3044\u3045\u3046\u3047\u3048\u3049\u304a\u304b\u304c\u304d\u304e\u304f";
	
	// A table of charset names to string values
	private Hashtable<ZCharset, String> samples = null;
	
	
	protected volatile static ZimbraCharsets Instance;

	protected ZimbraCharsets() {	
		logger.info("New "+ this.getClass().getCanonicalName());
		
		samples = new Hashtable<ZCharset, String>();
		
		samples.put(ZCharset.UTF_8, sampleUTF_8);
		samples.put(ZCharset.IEC_8859_1, sampleIEC_8859_1);
		samples.put(ZCharset.JIS_X_0202, sampleJIS_X_0202);
		
	}

	/**
	 * @param args
	 * @throws HarnessException 
	 */
	public static void main(String[] args) throws HarnessException {
		logger.info("Here!");
		
		logger.info("String: "+ ZimbraCharsets.getInstance().getSample(ZCharset.JIS_X_0202));
		
	}

}
