package com.zimbra.qa.selenium.results;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.dom4j.DocumentException;

import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ByteUtil;

public class ResultsCore {
	private static Logger logger = LogManager.getLogger(ResultsCore.class);
	
	public enum BugState {
		UNCONFIRMED,
		NEW,
		ASSIGNED,
		REOPENED,
		RESOLVED,
		VERIFIED,
		CLOSED
	}
	
	public ResultsCore() throws IOException {
		logger.info("new ResultsCore");
		
	}
	
	private static File findFile(String filename, File directory) {
		
		// If directory is a file, check if it matches
		if ( directory.isFile() ) {
			if ( filename.equals(directory.getName()) ) {
				// Found it!
				return (directory);
			}
			// Not it
			return (null);
		}
		
		// Check all the directory contents
		for (File f : directory.listFiles()) {
			
			File found = findFile(filename, f);
			if ( found != null ) {
				// Found it!
				return (found);
			}
			
		}
		
		// Not found
		return (null);
	}

	private Map<String, Boolean> getResults(File root) throws UnsupportedEncodingException, IOException, DocumentException {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		
		// Find the folder containing the "testng-results.xml" file
		File results = findFile("testng-results.xml", root);
		if ( results == null ) {
			logger.error("Unable to find testng-results.xml in "+ root.getAbsolutePath());
			return (map);
		}
		
		// Open the testng-results.xml file and convert to Element
        String docStr = new String(ByteUtil.getContent(results), "utf-8");    	
        Element docElement = Element.parseXML(docStr);
        
        for (Element eClass : getElementsFromPath(docElement, "//class")) {
        	String clazz = eClass.getAttribute("name", "undefined");
        	
        	for (Element eTestmethod : getElementsFromPath(eClass, "//test-method")) {
        		if ( eTestmethod.getAttribute("is-config", "false").equals("true") )
        			continue; // skip common* methods

        		String method = eTestmethod.getAttribute("name", "undefined");
        		Boolean status = eTestmethod.getAttribute("status", "FAIL").equals("PASS");
        		map.put(clazz + "." + method, status);
        	}
        }
        
        return (map);
	}
	
	private static final List<File> paths = new ArrayList<File>() {
		private static final long serialVersionUID = -4098010628463819580L;
		{
			add(new File("/opt/qa/testlogs/BugReports"));
			add(new File("T:\\BugReports"));
			add(new File("C:\\BugReports"));
		}};

	private void getBugzillaData(Map<String, BugState> status, Map<String, List<String>> testcase, Map<String, String> contact) throws IOException {

		// Find where the database files are located
		File path = null;
		for (File f : paths) {
			if ( f.exists() ) {
				path = f;
				break;
			}
		}
		
		if ( path == null ) {
			logger.error("Unable to open the database path");
			return;
		}
		
		File bugStatusFile = new File(path, "bugStatus.txt");
		File bugTestcaseFile = new File(path, "bugTestcase.txt");
		File bugQAContactFile = new File(path, "bugQaContact.txt");

		BufferedReader reader = null;
		String line;
		
		try {
			
			reader = new BufferedReader(new FileReader(bugStatusFile));
			while ( (line=reader.readLine()) != null ) {

				// Example: 50208	RESOLVED
				String[] values = line.split("\\s");
				if ( values.length != 2 ) {
					logger.warn("bugStatus: invalid line: "+ line);
					continue;
				}
				
				String bugid = values[0];
				BugState bugState = BugState.valueOf(values[1]);
							
				status.put(bugid, bugState);
				logger.debug("bugStatus: put "+ line);
				
			}
			
		} finally {
			if ( reader != null )
				reader.close();
			reader = null;
		}

		try {
			
			reader = new BufferedReader(new FileReader(bugTestcaseFile));
			while ( (line=reader.readLine()) != null ) {
	
				// Example: genesis/data/zmstatctl/basic.rb	29149 40782
				String[] values = line.split("\\s");
				if ( values.length <= 1 ) {
					logger.warn("bugTestcase: invalid line: "+ line);
					continue;
				}
				
				String bugtestcase = values[0];
				values = line.replace(bugtestcase, "").split("\\s");
				
				testcase.put(bugtestcase, Arrays.asList(values));
				logger.debug("bugTestcase: put "+ line);

			}
			
		} finally {
			if ( reader != null )
				reader.close();
			reader = null;
		}
		
		try {
			
			reader = new BufferedReader(new FileReader(bugQAContactFile));
			while ( (line=reader.readLine()) != null ) {
				
				// Example: 42337	sarang@zimbra.com

				String[] values = line.split("\\s");
				if ( values.length != 2 ) {
					logger.warn("bugQAContact: invalid line: "+ line);
					continue;
				}
				
				String bugid = values[0];
				String bugcontact = values[1];
				
				contact.put(bugid, bugcontact);
				logger.debug("bugQAContact: put "+ line);

			}
			
		} finally {
			if ( reader != null )
				reader.close();
			reader = null;
		}
		
		

	}
	
	private List<ReportItem> correlateData(Map<String, Boolean> results, Map<String, BugState> bugStatus, Map<String, List<String>> bugTestcase, Map<String, String> bugContact) {
		List<ReportItem> items = new ArrayList<ReportItem>();


		for (Map.Entry<String, Boolean> entry : results.entrySet() ) {
			String tcID = entry.getKey();
			Boolean tcResult = entry.getValue();
			
			logger.debug("Processing tc: "+ tcID + " result: "+ (tcResult ? "PASS" : "FAIL"));

			ReportItem item = new ReportItem();
			item.TestCaseID = tcID;
			item.TestCaseResult = tcResult;

			if ( tcResult ) {

				// Loop through the passing test cases
				// If no bug is associated, skip it
				// If a bug is associated and status is OPEN, it needs followup
				// If a bug is associated and status is CLOSED, it doesn't need followup

				if ( !bugTestcase.containsKey(tcID) ) {
					logger.debug("Passing TC ("+ tcID +") is not associated with a bug.  Skipping.");
					continue;
				}

				for (String id : bugTestcase.get(tcID)) {

					if (!bugStatus.containsKey(id) ) {
						logger.error("Unable to determine status tc("+ tcID +") ids("+ bugTestcase.get(tcID) +")");
						continue;
					}

					BugState state = bugStatus.get(id);
					if ( state == BugState.VERIFIED || state == BugState.CLOSED ) {
						item.BugID = id;
						item.BugStatus = state;
						item.NeedsFollowUp = false;
						item.BugOwner = (bugContact.containsKey(id) ? bugContact.get(id) : "None");
						// Keep searching in case another bug ID needs followup
					} else {
						item.BugID = id;
						item.BugStatus = state;
						item.NeedsFollowUp = true;
						item.BugOwner = (bugContact.containsKey(id) ? bugContact.get(id) : "None");
						break; // This bug needs followup, all done here.
					}

				}

			} else {

				// Loop through the failing test cases
				// If no bug is associated, set as NEW for followup
				// If a bug is associated and status is OPEN, it doesn't need followup
				// If a bug is associated and status is CLOSED, it does need followup


				if ( !bugTestcase.containsKey(tcID) ) {

					// Failure without existing bug
					item.NeedsFollowUp = true;
					item.BugID = null;
					item.BugOwner = null;

				} else {

					for (String id : bugTestcase.get(tcID)) {

						if (!bugStatus.containsKey(id) ) {
							logger.error("Unable to determine status tc("+ tcID +") ids("+ bugTestcase.get(tcID) +")");
							continue;
						}

						BugState state = bugStatus.get(id);
						if ( state == BugState.NEW || state == BugState.ASSIGNED || state == BugState.REOPENED ) {
							item.BugID = id;
							item.BugStatus = state;
							item.NeedsFollowUp = false;
							item.BugOwner = (bugContact.containsKey(id) ? bugContact.get(id) : "None");
							break; // Found the tracking bug, no need to go further
						} else {
							item.BugID = id;
							item.BugStatus = state;
							item.NeedsFollowUp = true;
							item.BugOwner = (bugContact.containsKey(id) ? bugContact.get(id) : "None");
							// Keep searching in case another bug ID needs followup
						}

					}

				}

			}

			items.add(item);
		}


		return (items);
	}
	
	private String writeReportEntry(ReportItem item) {
		
		StringBuilder sb = new StringBuilder();
		
		if ( item.NeedsFollowUp ) {
			sb.append("* ");
		}
		
		if ( item.BugID == null ) {
			sb.append("NEW ");
		} else {
			sb.append(item.BugID).append(' ');
			sb.append(item.BugStatus).append(' ');
		}
		
		sb.append(item.TestCaseID.replace("com.zimbra.qa.selenium.projects.", "...")).append(' ');
		
		if ( item.BugID == null ) {
			sb.append("-- http://bugzilla.zimbra.com/enter_bug.cgi");
		} else {
			sb.append("-- http://bugzilla.zimbra.com/show_bug.cgi?id=").append(item.BugID).append(' ');
		}
		
		if ( item.BugOwner != null )
			sb.append("( ").append(item.BugOwner).append(" )");
		
		return (sb.toString());

	}
	
	
	/**
	 * Compare a ReportItem to another
	 * <p>
	 * Sort according to:<br>
	 * 1. Needs Follow UP<br>
	 * 2. Bug state<br>
	 * 3. Bug ID<br>
	 * <p>
	 * @author Matt Rhoades
	 *
	 */
	private class ReportItemComparator implements Comparator<ReportItem> {
		
		private static final int LessThan = -1;
		private static final int EqualTo = 0;
		private static final int GreaterThan = 1;
		
		@Override
		public int compare(ReportItem a, ReportItem b) {
			
			// NeedsFollowUp bugs are first
			if ( a.NeedsFollowUp && !b.NeedsFollowUp )
				return (LessThan);
			
			if ( !a.NeedsFollowUp && b.NeedsFollowUp )
				return (GreaterThan);
			
			// NeedsFollowUp are equal (either both need it or both don't)
			
			// If one of the items doesn't have a bug ID, then it is first
			if ( a.BugID == null && b.BugID != null )
				return (LessThan);
			
			if ( a.BugID != null && b.BugID == null )
				return (GreaterThan);

			if ( a.BugID == null && b.BugID == null )
				return (EqualTo);

			// If BugStatus is not equal, return based on Status order, i.e. UNCONFIRMED, NEW, ASSIGNED, REOPENED, RESOLVED, VERIFIED, CLOSED
			if (a.BugStatus != b.BugStatus) {
				return (a.BugStatus.compareTo(b.BugStatus));
			}
			
			// BugStatus are equal
			
			// Return based on bug ID
			return (Integer.valueOf(a.BugID).compareTo(Integer.valueOf(b.BugID)));
		}
		
	}

	private void writeReport(File root, List<ReportItem> items) throws IOException {
		
		// Sort the items
		Collections.sort(items, new ReportItemComparator());

		// Create the BugReport.txt file as a log4j logger
		String filename = root.getAbsolutePath() + "/BugReports/BugReport.txt";
		Layout layout = new PatternLayout("%m%n");
		FileAppender appender = new FileAppender(layout, filename, false);
		Logger report = LogManager.getLogger("report");
		report.setLevel(Level.INFO);
		report.addAppender(appender);
		
		report.info("Automated bug report");
		report.info("");
		report.info("");
		report.info("Date: " + new Date());
		report.info("");
		report.info("Bug Reports:");
		report.info("(Items with an asterisk are out of sync and need follow up)");
		report.info("");


		report.info("");
		report.info("FAILED test cases:");
		for (ReportItem item : items) {
			
			if ( item.TestCaseResult == Boolean.FALSE ) {
				report.info(writeReportEntry(item));
			}
			
		}
		
		report.info("");
		report.info("PASSED test cases:");
		for (ReportItem item : items) {
			
			if ( item.TestCaseResult == Boolean.TRUE ) {
				report.info(writeReportEntry(item));
			}
			
		}
		
		report.info("");
		report.info("");
		
		report.removeAllAppenders();
		
	}
	
	
	public void execute(File testngRoot) throws UnsupportedEncodingException, IOException, DocumentException {
		if ( testngRoot == null ) {
			logger.error("testngRoot cannot be null");
			return;
		}
		
		if ( !testngRoot.exists() ) {
			logger.error(testngRoot.getAbsoluteFile() +" does not exist!");
			return;
		}
			
		logger.info("Processing "+ testngRoot.getAbsolutePath() +" ...");
		
		
		/*
		 * Build a list of passed and failed test cases, based
		 * on the TestNG XML files
		 */
		Map<String, Boolean> results = getResults(testngRoot);
		
		/*
		 * Build Maps of the bug data, ID vs TestCase vs QAContact
		 */
		Map<String, BugState> bugStatus = new HashMap<String, BugState>();
		Map<String, List<String>> bugTestcase = new HashMap<String, List<String>>();
		Map<String, String> bugContact = new HashMap<String, String>();
		getBugzillaData(bugStatus, bugTestcase, bugContact);
		
		/*
		 * Correlate the Pass/Fail results with the bugzilla content
		 */
		List<ReportItem> reportItems = correlateData(results, bugStatus, bugTestcase, bugContact);
		
		/*
		 * Write the bug report to a text file
		 */
		writeReport(testngRoot, reportItems);
		
		logger.info("Done!");
        
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


	private static class ReportItem {
		public String TestCaseID = "undetermined";
		public Boolean TestCaseResult = Boolean.FALSE;
		
		public String BugID = null;
		public BugState BugStatus = BugState.NEW;
		public String BugOwner = "None";
		public boolean NeedsFollowUp = true;
	}
	
}
