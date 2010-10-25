package framework.util;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.Assert;

public class ZAssert {
	private static Logger logger = LogManager.getLogger(ZAssert.class);
	
    private static int TotalCountTests = 0;
    private static int TotalCountPass = 0;

    private static int CountTests;
    private static int CountPass;

    
    public static void resetCounts() {
    	CountTests=0;
    	CountPass=0;
    }
    
	public static void assertTrue(boolean condition, String message) {
		TotalCountTests++;
		CountTests++;
		
		logger.info(String.format("%s -- (%s == %s) [%s]", "assertTrue", condition, true, message));
        try
        {
            Assert.assertTrue(condition, message);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;

	}

	public static void assertEquals(Object actual, Object expected, String message) {
		TotalCountTests++;
		CountTests++;
		
		logger.info(String.format("%s -- (%s == %s) [%s]", "assertEquals", actual, expected, message));
        try
        {
        	Assert.assertEquals(actual, expected, message);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;

	}

	public static void assertGreaterThan(int actual, int expected, String message) {
		TotalCountTests++;
		CountTests++;
		
		logger.info(String.format("%s -- (%s > %s) [%s]", "assertGreaterThan", actual, expected, message));
        try
        {
        	if ( actual <= expected ) {
        		throw new AssertionError(actual +" was not greather than " + expected);
        	}
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
	}

	public static void assertGreaterThanEqualTo(int actual, int expected, String message) {
		TotalCountTests++;
		CountTests++;
		
		logger.info(String.format("%s -- (%s >= %s) [%s]", "assertGreaterThanEqualTo", actual, expected, message));
        try
        {
        	if ( actual < expected ) {
        		throw new AssertionError(actual +" was not greather than or equal to " + expected);
        	}
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
	}

	public static void assertLessThan(int actual, int expected, String message) {
		TotalCountTests++;
		CountTests++;
		
		logger.info(String.format("%s -- (%s < %s) [%s]", "assertLessThan", actual, expected, message));
        try
        {
        	if ( actual >= expected ) {
        		throw new AssertionError(actual +" was not less than " + expected);
        	}
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
	}

	public static void assertLessThanEqualTo(int actual, int expected, String message) {
		TotalCountTests++;
		CountTests++;
		
		logger.info(String.format("%s -- (%s < %s) [%s]", "assertLessThanEqualTo", actual, expected, message));
        try
        {
        	if ( actual > expected ) {
        		throw new AssertionError(actual +" was not less than or equal to " + expected);
        	}
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
	}

	public static void assertNotNull(Object object, String message) {

		TotalCountTests++;
		CountTests++;
		
		logger.info(String.format("%s -- (%s != null) [%s]", "assertNotNull", object, message));
        try
        {
        	Assert.assertNotNull(object, message);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
		
	}

	public static void assertContains(Collection<?> collection, Object object, String message) {

		TotalCountTests++;
		CountTests++;
		
		logger.info(String.format("%s -- (collection contains %s) [%s]", "assertContains", object, message));
        try
        {
        	boolean contains = collection.contains(object);
        	Assert.assertTrue(contains, message);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
		
	}

	public static void assertMatches(String pattern, String input, String message) {
		assertMatches(Pattern.compile(pattern), input, message);
	}
	
	public static void assertMatches(Pattern pattern, String input, String message) {

		TotalCountTests++;
		CountTests++;
		
		logger.info(String.format("%s -- (%s matches %s) [%s]", "assertMatches", pattern.toString(), input, message));
        try
        {
        	Matcher m = pattern.matcher(input);
        	Assert.assertTrue(m.matches(), message);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
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
	
		TotalCountTests++;
		CountTests++;
		
		logger.info(String.format("%s -- (%s contains %s) [%s]", "assertStringContains", actual, substring, message));
        try
        {
        	boolean contains = actual.contains(substring);
        	Assert.assertTrue(contains, message);
        }
        catch (AssertionError e)
        {
        	logger.error(e.getMessage(), e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;

	}

}
