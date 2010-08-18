package framework.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.xml.XmlSuite;

import framework.core.SelNGBase;

public class SummaryReporter implements IReporter {

	private String dynSkippedTestMethods = "";
	private static String outputfolder ="";
	private static String appType = "";
    
    public SummaryReporter(String atype){
    	appType = atype;
    	outputfolder = ZimbraSeleniumProperties.getStringProperty("ZimbraLogRoot")+"/"+atype;
    }	
	public void generateReport(java.util.List<XmlSuite> xmlSuites,
			java.util.List<ISuite> suites, java.lang.String outputDirectory) {
		System.out.println(ZimbraSeleniumProperties.getStringProperty("locale"));
		int passed = 0;
		int failed = 0;
		int skipped = 0;
		for (int i = 0; i < xmlSuites.size(); i++) {
			XmlSuite xmlSuite = xmlSuites.get(i);
			ISuite suite = suites.get(i);

			System.out.println("Suite: " + xmlSuite.getName());
			for (ISuiteResult result : suite.getResults().values()) {
				ITestContext tc = result.getTestContext();

				passed = passed + tc.getPassedTests().size();
				failed = failed + tc.getFailedTests().size();
				skipped = skipped + tc.getSkippedTests().size();
			}

		}

		int dynamicallySkipped = getSkippedMethodsCount();
		String testdetails = "ran:"
				+ (passed + failed + skipped + dynamicallySkipped) + " passed:"
				+ passed + " failed:" + failed + " skipped:"
				+ (skipped + dynamicallySkipped) + " locale:"
				+ ZimbraSeleniumProperties.getStringProperty("locale") + " browser:"
				+ SelNGBase.currentBrowserName + " client:"
				+ System.getenv("COMPUTERNAME") + " server:"
				+ ZimbraSeleniumProperties.getStringProperty("server") + " zimbra:"
				+ ZimbraSeleniumProperties.zimbraGetVersionString();
		String subject = "SelNG-" + appType.toLowerCase()+" "+testdetails;
		String bodyfileXpPath = outputfolder;
		String lines = "\n--------------------------------------------\n";
		String uri = (bodyfileXpPath.replace("T:/",
				"http://tms.lab.zimbra.com/testlogs/")).replace("\\", "/");
		String body =  "--------------------------------------------\nHTML REPORT: " + lines + uri
				+ "/emailable-reportUTF8.html";
		body = body + "\n\n" + lines + "CONSOLE OUTPUT: " + lines + uri
				+ "/testresult.txt";
		body = body + "\n\n" + lines + "FULL REPORT: " + lines + uri;
		body = body
				+ "\n\n"
				+ lines
				+ "DYNAMICALLY SKIPPED TEST-METHODS: "
				+ lines
				+ "(skipped due to appln bugs in specific locales or browsers or due to script errors)\n"
				+ dynSkippedTestMethods;
		body = body + "\n\n" + lines + "TEST INFO: " + lines + testdetails;
		body = body + "\n\n" + lines + "RETRIED TESTS: " + lines
				+ "(Below tests have trigged retry, we need to fix them)"
				+ "\n" + getRetriedTestsInfo();

		Writer output = null;
		File file = new File(outputfolder+ "/subject.txt");

		try {
			output = new BufferedWriter(new FileWriter(file));
			output.write(subject);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		output = null;
		File bodyfile = new File(outputfolder+ "/body.txt");
		try {
			output = new BufferedWriter(new FileWriter(bodyfile));
			output.write(body);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// returns the number of methods that were dynamically skipped due to
	// bugs/or script issues
	private int getSkippedMethodsCount() {
		int count = 0;
		File skippedMethodsFile = new File(outputfolder+ "/skippedMethodsDueToConfig.txt");
		try {
			BufferedReader br = new BufferedReader(new BufferedReader(
					new InputStreamReader(new FileInputStream(
							skippedMethodsFile), "UTF8")));

			String str = null;
			while ((str = br.readLine()) != null) {
				dynSkippedTestMethods = dynSkippedTestMethods + "\n" + str;
				count++;
			}

			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	private String getRetriedTestsInfo() {
		String tmpstr = "";
		String str = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					outputfolder+ "/retriedTests.txt"));

			while ((tmpstr = in.readLine()) != null) {
				str = str + "\n" + (tmpstr.split("\\(")[0]);
			}
			in.close();
		} catch (IOException e) {
		}
		return str;
	}
}
