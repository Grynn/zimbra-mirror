package com.zimbra.qa.selenium.framework.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SleepMetrics {
	private static Logger logger = LogManager.getLogger(SleepMetrics.class);

	/**
	 * A list of class + methods being tracked
	 */
	private static List<SleepEntry> metrics = new ArrayList<SleepEntry>();
	
	/**
	 * Record to the logger the current sleep data report.
	 */
	public static void report() {
		
		logger.info("SleepMetrics:");
		
		Collections.sort(metrics);
		
		for (SleepEntry entry : metrics) {
			logger.info(entry.toString());
		}
		
	}
	
	/**
	 * Record the amount of 'sleep' time
	 * @param stack Current stack trace (i.e. (new Throwable()).getStackTrace())
	 * @param delay The desired Sleep(msec) delay
	 * @param start The start time of Sleep()
	 * @param finish The finish time of Sleep() (i.e in case interrupted)
	 */
	public static void RecordSleep(StackTraceElement[] stack, long delay, Date start, Date finish) {
		
		for ( StackTraceElement e : stack ) {
			
			// Skip any non-zimbra classes
			if ( !e.getClassName().startsWith("com.zimbra.qa.selenium") ) {
				continue;
			}
			
			// Skip any 'Abstract' methods - walk up the chain to find the real class
			if ( e.getClassName().contains(".Abs") ) {
				continue;
			}
			
			// Skip the SleepUtil class, since all sleep goes through that
			if ( e.getClassName().startsWith("com.zimbra.qa.selenium.framework.util.SleepUtil") ) {
				continue;
			}

			RecordSleep(e, delay, finish.getTime() - start.getTime());
			return;
		}
	}
	

	/**
	 * Record the amount of 'sleep' time
	 * @param e The stack element that requested the sleep (i.e. the class + method)
	 * @param delay The desired Sleep(msec) delay
	 * @param actual The actual time of Sleep() (i.e in case interrupted)
	 */
	public static void RecordSleep(StackTraceElement e, long delay, long actual) {
		SleepEntry entry = getEntry( e.getClassName(), e.getMethodName() );
		if ( entry == null ) {
			entry = new SleepEntry(e.getClassName(), e.getMethodName(), actual);
			metrics.add(entry);
		} else {
			entry.addDelay(actual);
		}
		
	}
	
	/**
	 * Record the amount of 'processing' time
	 * @param stack Current stack trace (i.e. (new Throwable()).getStackTrace())
	 * @param start The start time of processing
	 * @param finish The finish time of processing
	 */
	public static void RecordProcessing(StackTraceElement[] stack, Date start, Date finish) {
		
		for ( StackTraceElement e : stack ) {
			
			// Skip any non-zimbra classes
			if ( !e.getClassName().startsWith("com.zimbra.qa.selenium") ) {
				continue;
			}
			
			// Skip any 'Abstract' methods - walk up the chain to find the real class
			if ( e.getClassName().contains(".Abs") ) {
				continue;
			}

			RecordProcessing(e, finish.getTime() - start.getTime());
			return;
		}
	}
	
	/**
	 * Record the amount of 'processing' time
	 * @param e The stack element that processed (i.e. the class + method)
	 * @param actual The total time (msec) that was spent processing
	 */
	public static void RecordProcessing(StackTraceElement e, long actual) {
		SleepEntry entry = getEntry( e.getClassName(), e.getMethodName() );
		if ( entry == null ) {
			entry = new SleepEntry(e.getClassName(), e.getMethodName(), actual);
			metrics.add(entry);
		} else {
			entry.addDelay(actual);
		}
	}
	
	/**
	 * Search the list of entries for this class + method
	 * @param clazz
	 * @param method
	 * @return The existing entry if present, or null if not-present
	 */
	private static SleepEntry getEntry(String clazz, String method) {
		for (SleepEntry e : metrics ) {
			if ( e.className.equals(clazz) && e.methodName.equals(method) ) {
				return (e);
			}
		}
		return (null);
	}
	
	
	private static class SleepEntry implements Comparable<SleepEntry> {
		private static Logger logger = LogManager.getLogger(SleepMetrics.class);

		public String className;
		public String methodName;
		public long totalDelay;
		
		public SleepEntry(String clazz, String method, long delay) {
			className = clazz;
			methodName = method;
			totalDelay = delay;
			
			logger.info("Entry: "+ className +"."+ methodName +"() - "+ delay +"/"+ totalDelay);
		}
		
		public void addDelay(long delay) {
			totalDelay += delay;
			
			logger.info("Entry: "+ className +"."+ methodName +"() - "+ delay +"/"+ totalDelay);
		}
		
		public String toString() {
			
			// "Entry: com.zimbra.qa.selenium.class.method() - 1234 msec"
			
			StringBuilder sb = new StringBuilder("Entry: ");
			sb.append(className).append('.').append(methodName).append("() - ").append(totalDelay).append(" msec");
			return (sb.toString());
		}

		@Override
		public int compareTo(SleepEntry that) {
			final int BEFORE = -1;
			final int EQUAL = 0;
			final int AFTER = 1;
			
			if (that == null) {
				return (BEFORE);
			}
			
			if ( this == that ) {
				return (EQUAL);
			}
			
			if ( this.totalDelay > that.totalDelay ) {
				return (BEFORE);
			}
			
			if ( this.totalDelay < that.totalDelay ) {
				return (AFTER);
			}
			
			return (EQUAL);
		}
		
	}
}
