package com.zimbra.qa.selenium.framework.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

	/**
    * Get value out of a specified element's name in XML file
    * @param xmlFile XML File to look at
    * @param elementName Element name, in which the value is wanted
    * @return (String) Element's value
	 * @throws IOException 
    */
   public static String parseXmlFile(String xmlFile, String elementName) throws IOException {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      String output = null;
      try {
         File file = new File(xmlFile);
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.parse(file);
         doc.getDocumentElement().normalize();
         NodeList nodeLst = doc.getDocumentElement().
               getElementsByTagName("key");
         for (int i = 0; i < nodeLst.getLength(); i++) {
            Node currentNode = nodeLst.item(i);
            Element currentElement = (Element)currentNode;
            String keyName = currentElement.getAttribute("name");
            if (!keyName.equals(elementName)) {
               continue;
            } else {
               Element value = (Element)currentElement.
                     getElementsByTagName("value").item(0);
               output = value.getChildNodes().item(0).getNodeValue();
               break;
            }
         }
      } catch(ParserConfigurationException pce) {
    	  logger.warn(pce);
      }catch(SAXException se) {
    	  logger.warn(se);
      }
      return output;
   }

}
