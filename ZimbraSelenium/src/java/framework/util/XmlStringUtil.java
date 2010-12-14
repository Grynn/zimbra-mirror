package framework.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Utilities for managing XML strings
 * <p>
 * Basically, a wrapper around org.apache.commons.lang.StringEscapeUtils
 * <p>
 * @author Matt Rhoades
 *
 */
public class XmlStringUtil {
	private static Logger logger = LogManager.getLogger(XmlStringUtil.class);

	/**
	 * Convert a string to a string with XML entities
	 * @param source
	 * @return
	 */
	public static String escapeXml(String source) {
		logger.info("converting :"+ source);
		
		String converted = StringEscapeUtils.escapeXml(source);
		logger.info("converted: "+ converted);
		
		return (converted);
	}

	/**
	 * Convert a string with XML entities to a string without XML entities
	 * @param source
	 * @return
	 */
	public static String unescapeXml(String source) {
		return (StringEscapeUtils.unescapeXml(source));
	}

}
