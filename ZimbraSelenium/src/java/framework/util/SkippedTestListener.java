package framework.util;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class SkippedTestListener implements ITestListener {
	private static Logger logger = LogManager.getLogger(SkippedTestListener.class);

	public static int skippedCount = 0;
		
	protected final String resultLoggerName = "SkippedTestListenerLog";
	protected Logger resultsLog;
	
	public SkippedTestListener(File outputFolder) {
		logger.info("New SkippedTestListener: "+ outputFolder.getAbsolutePath());

		try
		{
			// Create a new logger to write the skipped tests to
			resultsLog = LogManager.getLogger(resultLoggerName);
			
			String filename = outputFolder.getCanonicalPath() + "/SkippedTests.txt";
			PatternLayout layout = new PatternLayout("%m%n");
			FileAppender appender = new FileAppender(layout, filename, false);
			resultsLog.addAppender(appender);
			
			resultsLog.setLevel(Level.INFO);
		
		} catch (IOException e) {
			logger.error("Unable to write the skipped file results", e);
		} finally {

		}
	}
	
	@Override
	public void onTestSkipped(ITestResult result) {
		skippedCount++;
		resultsLog.info("Skipped: " + result.getTestClass().getName() + "." + result.getName() + " -- " + result.getThrowable().getMessage());
	}


	@Override
	public void onFinish(ITestContext context) {
	}

	@Override
	public void onStart(ITestContext context) {
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult context) {
	}

	@Override
	public void onTestFailure(ITestResult result) {
	}

	@Override
	public void onTestStart(ITestResult result) {
	}

	@Override
	public void onTestSuccess(ITestResult result) {
	}

}
