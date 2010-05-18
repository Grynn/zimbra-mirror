package framework.util;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author Suresh Daniel
 */

public class TimeAndDate {
	
	
	    public static void main(String[] args) throws ParseException {
	       GregorianCalendar thisday  = new GregorianCalendar();
	       thisday.add(GregorianCalendar.DATE, 80);
	       Date d = thisday.getTime();
	       DateFormat df = DateFormat.getDateInstance();
	       String s = df.format(d);
	       System.out.println("80 days from now will be " + s);
	       getOffsetDate("2");
	       appendTodaysDate("Subject");
	       convertDateFormat("MM/dd/yyyy","04/02/2008","yyyyMMdd");
	    }

	    public static String getOffsetDate(String days) {
		       GregorianCalendar thisday  = new GregorianCalendar();
		       int n = Integer.parseInt(days);
		       thisday.add(GregorianCalendar.DATE, n);
		       Date d = thisday.getTime();
			   DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		       //DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT);
		       String s = df.format(d);
		       System.out.println(n + " days from now will be " + s);
		       return s;
	    }
	    
	    public static String getOffsetTime (String offsetTime) {
	    	   GregorianCalendar thisday  = new GregorianCalendar();
		       int n = Integer.parseInt(offsetTime);
		       thisday.add(GregorianCalendar.HOUR, n);
		       Date d = thisday.getTime();
			   DateFormat df = new SimpleDateFormat("HH:mm a");
		       String s = df.format(d);
		       System.out.println(n + " time from now will be " + s);
		       return s;
	    }
	    
	    public static String appendTodaysDate(String str) {
		       GregorianCalendar thisday  = new GregorianCalendar();
		       Date d = thisday.getTime();
			   DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		       String s = df.format(d);
		       System.out.println(str + " " + s);
		       return str + " " + s;
	    }
	    
	    public static String addTodaysDate(String str) {
		       GregorianCalendar thisday  = new GregorianCalendar();
		       Date d = thisday.getTime();
			   DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		       String s = df.format(d);
		       System.out.println(str + " " + s);
		       return str + " " + s;
	    }
	    
	    public static String convertDateFormat(String currentFormat, String dateStr, String expectedFormat) throws ParseException {
	    	 DateFormat formatter = new SimpleDateFormat(currentFormat);
	         Date date = (Date)formatter.parse(dateStr);
	         formatter = new SimpleDateFormat(expectedFormat);
	         String s = formatter.format(date);
	         System.out.println(s);
	         return s;
	    }
}
