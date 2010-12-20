package results;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.dom4j.DocumentException;

import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ByteUtil;

public class ResultsCore {
	private static Logger logger = LogManager.getLogger(ResultsCore.class);
	
	protected static Logger outputLogger = LogManager.getLogger("output");

	/**
	 * The testng-failed.xml file
	 */
	protected String ResultsXmlFile = null;
	
	/**
	 * Where to write the bug report output
	 */
	protected String ResultsOutputFile = null;
	
	public ResultsCore() throws IOException {
		logger.info("new ResultsCore");
		
		this.setResultsOutputFile("BugReports/BugReports.txt");		
	}

	protected void processResult(String classname, String methodname, String result) {

		// Test case name is the fully qualified name
		String testcasename = classname + "." + methodname;
		
		// TODO: Read the bugzilla database file to determine bugid, assigninee, etc.
		String bugid = "NEW";
		String status = "ASSIGNED";
		String assignee = "None";
		
		// TODO: Determine followup
		// If result == PASS and no bugid ... don't log
		if ( "PASS".equals(result) && "NEW".equals(bugid) )
			return;
		
		// If result == PASS and open bug ... need followup (followup = "* ")
		// If result == PASS and closed bug ... no followup (followup = "")
		// If result == FAIL and no bugid ... need followup
		// If result == FAIL and open bug ... no followup
		// If result == FAIL and closed bug ... need followup
		String followup = "* ";

		String url = "http://bugzilla.zimbra.com/show_bug.cgi?id=" + bugid;
		if ( "NEW".equals(bugid) ) {
			url = "http://bugzilla.zimbra.com/enter_bug.cgi";			
		}
		
		outputLogger.info(String.format("%s %s %s %s -- %s (%s)",
				followup,
				bugid,
				status,
				testcasename,
				url,
				assignee));

	}
	
	
	protected void processClasses(Element[] classes) {
		
		// Example:
		// <class name="com.zimbra.qa.selenium.projects.client.tests.mail.GetMail
		//  <test-method name="Test01" status="PASS"/>
		// </class>
		//
		
		// Iterate each class
		// Look for <test-methods/> with status="FAIL"
		//
		for (Element e: classes) {
			String classname = e.getAttribute("name", "undefined");
			Element[] eTestMethods = getElementsFromPath(e, "//test-method");
			for ( Element eTestMethod : eTestMethods ) {
				String methodname = eTestMethod.getAttribute("name", "undefined");
				String status = eTestMethod.getAttribute("status", "PASS");
			
				// Build the output file
				processResult(classname, methodname, status);

			}
		}
	}
	
	public void execute() throws UnsupportedEncodingException, IOException, DocumentException {

		// Convert the xml file to a document
        String docStr = new String(ByteUtil.getContent(new File(ResultsXmlFile)), "utf-8");    	
        Element root = Element.parseXML(docStr);
        
        // Find all the <class/> elements
        Element[] eClasses = getElementsFromPath(root, "//class");
        processClasses(eClasses);
        
	}
	
	public String getResultsXmlFile() {
		return ResultsXmlFile;
	}

	public void setResultsXmlFile(String resultsXmlFile) {
		ResultsXmlFile = resultsXmlFile;
	}

	public String getResultsOutputFile() {
		return ResultsOutputFile;
	}

	public void setResultsOutputFile(String resultsOutputFile) throws IOException {
		ResultsOutputFile = resultsOutputFile;
		
		outputLogger.removeAllAppenders();
		Appender appender = new FileAppender(new PatternLayout("%m%n"), ResultsOutputFile, false);
		outputLogger.addAppender(appender);

	}

    /**
     * Runs an XPath query on the specified element context and returns the results.
     */
    @SuppressWarnings("unchecked")
	protected Element[] getElementsFromPath(Element context, String path) {
		org.dom4j.Element d4context = context.toXML();
		org.dom4j.XPath xpath = d4context.createXPath(path);
		xpath.setNamespaceURIs(getURIs());
		org.dom4j.Node node;
		List dom4jElements = xpath.selectNodes(d4context);

		List<Element> zimbraElements = new ArrayList<Element>();
		Iterator iter = dom4jElements.iterator();
		while (iter.hasNext()) {
			node = (org.dom4j.Node)iter.next();
			if (node instanceof org.dom4j.Element) {
				Element zimbraElement = Element.convertDOM((org.dom4j.Element) node);
				zimbraElements.add(zimbraElement);
			}
		}

		Element[] retVal = new Element[zimbraElements.size()];
		zimbraElements.toArray(retVal);
		return retVal;
    }
	private static Map<String, String> mURIs = null;
	private static Map<String, String> getURIs() {
		if (mURIs == null) {
			mURIs = new HashMap<String, String>();
		}
		return mURIs;
	}


	
}
