package com.zimbra.qa.selenium.framework.util;

import java.util.Collection;
import java.util.regex.*;

import org.apache.log4j.*;
import org.testng.Assert;

import com.zimbra.qa.selenium.framework.core.ExecuteHarnessMain;
import com.zimbra.qa.selenium.framework.core.Repository;


public class ZAssert {
	private static Logger logger = LogManager.getLogger(ZAssert.class);
	

    private static int TotalCountTests = 0;
    private static int TotalCountPass = 0;

    private static int CountTests;
    private static int CountPass;

	protected static final Logger tracer = LogManager.getLogger(ExecuteHarnessMain.TraceLoggerName);
    private static void trace(String message) {
		tracer.trace(message);
    }
    
    public static void resetCounts() {
    	CountTests=0;
    	CountPass=0;
    }
    
	public static void assertTrue(boolean condition, String message) {
		trace(message);

		// Add counts
		TotalCountTests++;
		CountTests++;
		message = "assertTrue: " + message;
		// Build a 'standard' detailed message
		String details = String.format("%s -- (%s == %s) [%s]", "assertTrue", condition, true, message);
		
		// Log it
		logger.info(details);
		
        try
        {
        	// Execute the Assert base method (if available)
            Assert.assertTrue(condition, details);
            Repository.testCaseVerification(message, String.valueOf(condition),
                  String.valueOf(true), true);
        }
        catch (AssertionError e)
        {
        	// On failure, log the error
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(condition),
               String.valueOf(true), false);
            throw e;
        }
        
        // Add "pass" counts
        CountPass++; TotalCountPass++;

	}

	public static void assertFalse(boolean condition, String message) {
		trace(message);
	
		// Add counts
		TotalCountTests++;
		CountTests++;
		message = "assertFalse: " + message;
		// Build a 'standard' detailed message
		String details = String.format("%s -- (%s == %s) [%s]", "assertFalse", condition, false, message);
		
		// Log it
		logger.info(details);
		
        try
        {
        	// Execute the Assert base method (if available)
            Assert.assertFalse(condition, details);
            Repository.testCaseVerification(message, String.valueOf(condition),
                  String.valueOf(false), true);
        }
        catch (AssertionError e)
        {
        	// On failure, log the error
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(condition),
               String.valueOf(false), false);
            throw e;
        }
        
        // Add "pass" counts
        CountPass++; TotalCountPass++;

	}

	public static void assertEquals(Object actual, Object expected, String message) {
		trace(message);

		TotalCountTests++;
		CountTests++;
		message = "assertEquals: " + message;
		String details = String.format("%s -- (%s == %s) [%s]", "assertEquals", actual, expected, message);
		logger.info(details);
		
        try
        {
        	Assert.assertEquals(actual, expected, details);
        	Repository.testCaseVerification(message, String.valueOf(actual),
               String.valueOf(expected), true);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(actual),
               String.valueOf(expected), false);
            throw e;
        }
        
        CountPass++; TotalCountPass++;

	}

	public static void assertGreaterThan(int actual, int expected, String message) {
		trace(message);

		TotalCountTests++;
		CountTests++;
		message = "assertGreaterThan: " + message;
		String details = String.format("%s -- (%s > %s) [%s]", "assertGreaterThan", actual, expected, message);
		logger.info(details);
		
        try
        {
        	if ( actual <= expected ) {
        	   Repository.testCaseVerification(message, String.valueOf(true),
        	         String.valueOf(true), false);
        		throw new AssertionError(details);
        	}
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(true),
               String.valueOf(true), false);
            throw e;
        }

        Repository.testCaseVerification(message, String.valueOf(true),
              String.valueOf(true), true);
        CountPass++; TotalCountPass++;
	}

	public static void assertGreaterThanEqualTo(int actual, int expected, String message) {
		trace(message);

		TotalCountTests++;
		CountTests++;
		message = "assertGreaterThanEqualTo: " + message;
		String details = String.format("%s -- (%s >= %s) [%s]", "assertGreaterThanEqualTo", actual, expected, message);
		logger.info(details);
		
        try
        {
        	if ( actual < expected ) {
        	   Repository.testCaseVerification(message, String.valueOf(true),
        	         String.valueOf(true), false);
        		throw new AssertionError(details);
        	}
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(true),
               String.valueOf(true), false);
            throw e;
        }
        Repository.testCaseVerification(message, String.valueOf(true),
              String.valueOf(true), true);
        CountPass++; TotalCountPass++;
	}

	public static void assertLessThan(int actual, int expected, String message) {
		trace(message);

		TotalCountTests++;
		CountTests++;
		message = "assertLessThan: " + message;
		String details = String.format("%s -- (%s < %s) [%s]", "assertLessThan", actual, expected, message);
		logger.info(details);
		
        try
        {
        	if ( actual >= expected ) {
        	   Repository.testCaseVerification(message, String.valueOf(true),
                String.valueOf(true), false);
        		throw new AssertionError(details);
        	}
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(true),
               String.valueOf(true), false);
            throw e;
        }
        Repository.testCaseVerification(message, String.valueOf(true),
              String.valueOf(true), true);
        CountPass++; TotalCountPass++;
	}

	public static void assertLessThanEqualTo(int actual, int expected, String message) {
		trace(message);

		TotalCountTests++;
		CountTests++;
		message = "assertLessThanEqualTo: " + message;
		String details = String.format("%s -- (%s < %s) [%s]", "assertLessThanEqualTo", actual, expected, message);
		logger.info(details);
		
        try
        {
        	if ( actual > expected ) {
        	   Repository.testCaseVerification(message, String.valueOf(true),
                String.valueOf(true), false);
        		throw new AssertionError(details);
        	}
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(true),
               String.valueOf(true), false);
            throw e;
        }
        Repository.testCaseVerification(message, String.valueOf(true),
              String.valueOf(true), true);
        CountPass++; TotalCountPass++;
	}

	public static void assertNull(Object object, String message) {
		trace(message);


		TotalCountTests++;
		CountTests++;
		message = "assertNull: " + message;
		String details = String.format("%s -- (%s == null) [%s]", "assertNull", object, message);
		logger.info(details);
		
        try
        {
        	Assert.assertNull(object, details);
        	Repository.testCaseVerification(message, String.valueOf(object),
               "null", true);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(object),
               "null", false);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
		
	}


	public static void assertNotNull(Object object, String message) {
		trace(message);


		TotalCountTests++;
		CountTests++;
		message = "assertNotNull: " + message;
		String details = String.format("%s -- (%s != null) [%s]", "assertNotNull", object, message);
		logger.info(details);
		
        try
        {
        	Assert.assertNotNull(object, details);
        	Repository.testCaseVerification(message, String.valueOf(object),
               "null", true);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
		
	}

	public static void assertContains(Collection<?> collection, Object object, String message) {
		trace(message);

		TotalCountTests++;
		CountTests++;
		message = "assertContains: " + message;
		String details = String.format("%s -- (collection contains %s) [%s]", "assertContains", object, message);
		logger.info(details);
		
        try
        {
        	boolean contains = collection.contains(object);
        	Assert.assertTrue(contains, details);
        	Repository.testCaseVerification(message, String.valueOf(collection),
               String.valueOf(object), true);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(collection),
               String.valueOf(object), false);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
		
	}

	public static void assertMatches(String pattern, String input, String message) {
		assertMatches(Pattern.compile(pattern), input, message);
	}
	
	public static void assertMatches(Pattern pattern, String input, String message) {
		trace(message);

		TotalCountTests++;
		CountTests++;
		message = "assertMatches" + message;
		String details = String.format("%s -- (%s matches %s) [%s]", "assertMatches", pattern.toString(), input, message);
		logger.info(details);
		
        try
        {
        	Matcher m = pattern.matcher(input);
        	Assert.assertTrue(m.matches(), details);
        	Repository.testCaseVerification(message, String.valueOf(pattern),
               String.valueOf(input), true);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(pattern),
               String.valueOf(input), false);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
		
	}

	/**
	 * Verify that "actual" contains "substring"
	 * @param actual the actual text
	 * @param substring the substring that should be cotained in actual
	 * @param message the logging message
	 */
	public static void assertStringContains(String actual, String substring, String message) {
		trace(message);

		TotalCountTests++;
		CountTests++;
		message = "assertStringContains: " + message;
		String details = String.format("%s -- (%s contains %s) [%s]", "assertStringContains", actual, substring, message);
		logger.info(details);
		
        try
        {
        	boolean contains = actual.contains(substring);
        	Assert.assertTrue(contains, details);
        	Repository.testCaseVerification(message, String.valueOf(actual),
               String.valueOf(substring), true);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(actual),
               String.valueOf(substring), false);
            throw e;
        }
        
        CountPass++; TotalCountPass++;

	}

	/**
	 * Verify that "actual" does not contain "substring"
	 * @param actual the actual text
	 * @param substring the substring that should be cotained in actual
	 * @param message the logging message
	 */
	public static void assertStringDoesNotContain(String actual, String substring, String message) {
		trace(message);

		TotalCountTests++;
		CountTests++;
		message = "assertStringDoesNotContain: " + message;
		String details = String.format("%s -- (%s does not contain %s) [%s]", "assertStringDoesNotContain", actual, substring, message);
		logger.info(details);
		
        try
        {
        	boolean contains = actual.contains(substring);
        	Assert.assertFalse(contains, details);
        	Repository.testCaseVerification(message, String.valueOf(actual),
               String.valueOf(substring), true);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
        	Repository.testCaseVerification(message, String.valueOf(actual),
               String.valueOf(substring), false);
            throw e;
        }
        
        CountPass++; TotalCountPass++;

	}

}
