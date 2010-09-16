package framework.util;

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
        	logger.error(e);
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
        	logger.error(e);
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
        	logger.error(e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
		
	}

	public static void assertContains(String sequence, String actual, String message) {

		TotalCountTests++;
		CountTests++;
		
		logger.info(String.format("%s -- (%s contains %s) [%s]", "assertContains", actual, sequence, message));
        try
        {
        	Assert.assertTrue(actual.contains(sequence), message);
        }
        catch (AssertionError e)
        {
        	logger.error(e);
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
        	logger.error(e);
            throw e;
        }
        
        CountPass++; TotalCountPass++;
		
	}

}
