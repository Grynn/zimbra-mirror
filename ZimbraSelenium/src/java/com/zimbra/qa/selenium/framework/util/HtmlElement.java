package com.zimbra.qa.selenium.framework.util;

import java.io.IOException;
import java.util.regex.*;

import org.apache.log4j.*;
import org.htmlcleaner.*;


/**
 * An object representing an HTML document
 * <p>
 * It would be great to use com.zimbra.soap.Element, rather than defining
 * a class in the Selenium Harness.  However, Element does not allow
 * content + tags within elements:  "cannot set text on element with children".
 * <p>
 * If that issue is resolved with Element, then this HtmlElement class
 * should be converted to Element.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public class HtmlElement {
	protected static Logger logger = LogManager.getLogger(HtmlElement.class);

	
	/**
	 * Converts a String containing an HTML document to a HtmlElement
	 * @param source
	 * @return
	 */
	public static HtmlElement clean(String source) {
		
		// Use HtmlCleaner to clean up any HTML errors
		HtmlElement element = new HtmlElement(cleaner.clean(source));
		logger.debug("after clean: "+ element.prettyPrint());
		return (element);
		
	}

	/**
	 * Apply an xpath to this HTML document
	 * <p>
	 * This method uses ZAssert to check the element matches the specified arguments.
	 * <p>
	 * @param element The HTML document to parse
	 * @param xpath The xpath to apply (null uses the root node of the element)
	 * @param attribute If non-null, apply regex to the attribute value
	 * @param regex If non-null, match the regex to the node text or attribute value
	 * @param occurences The number of nodes that should match
	 * @throws HarnessException
	 */
	public static void evaluate(HtmlElement element, String xpath, String attribute, String compare, int occurences) throws HarnessException {
		if ( compare == null ) {
			// Cast the null string to a null pattern
			evaluate(element, xpath, attribute, (Pattern)null, occurences);
		} else {
			// "Quote" the string for literal match, then convert to Pattern object
			evaluate(element, xpath, attribute, Pattern.compile(Pattern.quote(compare)), occurences);
		}
	}
	
	/**
	 * Apply an xpath to this HTML document
	 * @param element The HTML document to parse
	 * @param xpath The xpath to apply (null uses the root node of the element)
	 * @param attribute If non-null, apply regex to the attribute value
	 * @param regex If non-null, match the regex to the node text or attribute value
	 * @param occurences The number of nodes that should match
	 * @throws HarnessException
	 */
	public static void evaluate(HtmlElement element, String xpath, String attribute, Pattern regex, int occurences) throws HarnessException {
		String message = String.format("xpath(%s), attr(%s), regex(%s), occurences(%d)", xpath, attribute, regex, occurences);
		
		try {
		
			Object[] nodes = null;
			if ( xpath == null ) {
				nodes = new TagNode[1];
				nodes[0] = element.Node;
			} else {
				nodes = element.Node.evaluateXPath(xpath);
			}
			
			int found = 0;
			String text = null;
			for (Object o : nodes) {
				
				if ( o instanceof TagNode ) {

					TagNode node = (TagNode)o;
					
					if ( attribute != null ) {
						
						if ( !node.hasAttribute(attribute) ) {
							// This node doesn't have the attribute, but keep checking other nodes in the list
							continue;
						}

						text = node.getAttributeByName(attribute);
						
					} else {
						
						// No attribute, then use the inner text (which could contain more elements)
						text = node.getText().toString();
						
					}

					if ( regex != null ) {
						
						if ( regex.matcher(text).matches() ) {
							found++;
						}
						
					} else {
					
						// Count the nodes
						found++;
						
					}
					
				} else {
					logger.info("Not sure what this is: "+ o);
				}
			}
			
			ZAssert.assertEquals(found, occurences, String.format("Evaluate: %s ... matched: %d", message, found));
			
		} catch (XPatherException e) {
			throw new HarnessException("Unable to evaluate node "+ message, e);
		}
	}
	
	/**
	 * Format the TagNode using indentation and spacing
	 * <p>
	 * See http://htmlcleaner.sourceforge.net/doc/org/htmlcleaner/PrettyHtmlSerializer.html
	 * <p>
	 * @param node
	 * @return
	 */
	protected static String nodeToString(TagNode node) {
		try {
			PrettyHtmlSerializer serializer = new PrettyHtmlSerializer(cleaner.getProperties());
			return (serializer.getAsString(node));
		} catch (IOException e) {
			logger.error("unable to parse my node", e);
		}
		return ("unable to parse my node");
	}
	
	/**
	 * An HTML cleaner.  Create valid XML from a specified HTML document.
	 */
	protected static HtmlCleaner cleaner = new HtmlCleaner();

	/**
	 * The TagNode object represented by this HtmlElement
	 */
	protected TagNode Node = null;
	
	/**
	 * Convert the specified TagNode to an HtmlElement
	 * @param node
	 */
	public HtmlElement(TagNode node) {
		Node = node;
	}
	
	/**
	 * Format the HtmlElement using indentation and spacing
	 * <p>
	 * See http://htmlcleaner.sourceforge.net/doc/org/htmlcleaner/PrettyHtmlSerializer.html
	 * <p>
	 * @param node
	 * @return
	 */
	public String prettyPrint() {
		return (HtmlElement.nodeToString(this.Node));
	}
}
