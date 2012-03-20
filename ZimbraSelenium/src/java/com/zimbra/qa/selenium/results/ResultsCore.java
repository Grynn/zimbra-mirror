package com.zimbra.qa.selenium.results;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.dom4j.DocumentException;

import com.zimbra.qa.selenium.results.BugStatus.BugState;

public class ResultsCore {
	private static Logger logger = LogManager.getLogger(ResultsCore.class);
	
		
	private int CountPass = 0;
	private int CountFail = 0;
		
	public ResultsCore() throws IOException {
		logger.info("new " + ResultsCore.class.getCanonicalName());
		
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

				for (String bugID : bugTestcase.get(tcID)) {

					if (!bugStatus.containsKey(bugID) ) {
						logger.error("Unable to determine status bug("+ bugID +") tc("+ tcID +")");
						continue;
					}

					BugState state = bugStatus.get(bugID);
					if ( state == BugState.VERIFIED || state == BugState.CLOSED ) {
						item.BugID = bugID;
						item.BugStatus = state;
						item.NeedsFollowUp = false;
						item.BugOwner = (bugContact.containsKey(bugID) ? bugContact.get(bugID) : "None");
						// Keep searching in case another bug ID needs followup
					} else {
						item.BugID = bugID;
						item.BugStatus = state;
						item.NeedsFollowUp = true;
						item.BugOwner = (bugContact.containsKey(bugID) ? bugContact.get(bugID) : "None");
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

					for (String bugID : bugTestcase.get(tcID)) {

						if (!bugStatus.containsKey(bugID) ) {
							logger.error("Unable to determine status bug("+ bugID +") tc("+ tcID +")");
							continue;
						}

						BugState state = bugStatus.get(bugID);
						if ( state == BugState.NEW || state == BugState.ASSIGNED || state == BugState.REOPENED ) {
							item.BugID = bugID;
							item.BugStatus = state;
							item.NeedsFollowUp = false;
							item.BugOwner = (bugContact.containsKey(bugID) ? bugContact.get(bugID) : "None");
							break; // Found the tracking bug, no need to go further
						} else {
							item.BugID = bugID;
							item.BugStatus = state;
							item.NeedsFollowUp = true;
							item.BugOwner = (bugContact.containsKey(bugID) ? bugContact.get(bugID) : "None");
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
//		private static final int EqualTo = 0;
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

			// If BugStatus is not equal, return based on Status order, i.e. UNCONFIRMED, NEW, ASSIGNED, REOPENED, RESOLVED, VERIFIED, CLOSED
			if (a.BugStatus != b.BugStatus) {
				return (a.BugStatus.compareTo(b.BugStatus));
			}
			
			// BugStatus are equal
			
			// Return based on Test Case
			return (a.TestCaseID.compareTo(b.TestCaseID));
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
				CountFail++;
			}
			
		}
		
		report.info("");
		report.info("PASSED test cases:");
		for (ReportItem item : items) {
			
			if ( item.TestCaseResult == Boolean.TRUE ) {
				report.info(writeReportEntry(item));
				CountPass++;
			}
			
		}
		
		report.info("");
		report.info("");
		
		report.removeAllAppenders();
		
	}
	
	
	public void execute(File testngRoot) throws UnsupportedEncodingException, IOException, DocumentException {
		
		
		/*
		 * Build a list of passed and failed test cases, based
		 * on the TestNG XML files
		 */
		Map<String, Boolean> results = TestCaseData.getStatusData(testngRoot);
		
		/*
		 * Build Maps of the bug data, ID vs TestCase vs QAContact
		 */
		Map<String, BugState> bugStatus = BugStatus.getStatusData();
		Map<String, List<String>> bugTestcase = BugTestcase.getTestcaseData();
		Map<String, String> bugContact = BugQAContact.getQAContactData();
		
		logger.info("Processing "+ testngRoot.getAbsolutePath() +" ...");

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
	
	public String getResultString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(BugStatus.getResultString()).append("\n");
		sb.append(BugTestcase.getResultString()).append("\n");
		sb.append(BugQAContact.getResultString()).append("\n");
		sb.append(TestCaseData.getResultString()).append("\n");
		
		sb.append("Report: Wrote\n");
		sb.append("\t").append(CountPass).append(" PASS results\n");
		sb.append("\t").append(CountFail).append(" FAIL results\n");
		
		return (sb.toString());
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
