package framework.util;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestStatusReporter extends TestListenerAdapter {  
   private PrintWriter    output=null;
   private ArrayList<String> failArray = new ArrayList<String>();
   
   public TestStatusReporter(String atype) throws Exception{     
		 output = new PrintWriter(new FileWriter(new File(ZimbraSeleniumProperties.getStringProperty("ZimbraLogRoot")+"/"+atype+ "/zimbraSelenium-failed.xml")));       
	  }	
	  
   private String getClass(String fullName){
		  return fullName.substring(0,fullName.lastIndexOf("."));
	  }
	  
   private String getTest(String fullName){
		  return fullName.substring(fullName.lastIndexOf(".")+1);
   }
	  
   @Override
   public void onFinish(ITestContext testContext)  {
			//write fail/skip tests in a file
			
		     String classStart="";
		     
			 for (int i=0 ; i < failArray.size(); i++) {
	             //TODO
				 String className=getClass(failArray.get(i));
				 String testName=getTest(failArray.get(i));
				 
				 if (!className.equals(classStart)) {
	                if (classStart.length() > 0) {
	                  output.println("</methods>");
	                  output.println("</class>");                
	                } 	
				    classStart=className;
	                                
	                output.println("<class name='" + className + "'>");
	                output.println("<methods>");
	                
				 }
				 
				 output.println("<include name='" + testName + "'/>");            	
	          }
			 output.println("</methods>");
             output.println("</class>");  
			 output.println("</classes>");
			 output.println("</test>");
			 output.println("</suite>");
			 		 
			 output.close();
								
  }
	  
  @Override
  public void onStart(ITestContext testContext) {
		output.println("<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">");
	    output.println("<suite name='SelNG'>");
	    output.println("<test name='Failed Tests' >");
	    output.println("<groups>");
	    output.println("  <run>");
	    output.println(" <include name=\"always\"/>");
	    output.println("<include name=\"smoke\"/>");
	    output.println("</run>");
	    output.println("</groups>");
	    output.println("<classes>");
  }


  @Override
  public void onTestStart(ITestResult tr) {
	  log("----------------------------- " + tr.getName() + " started ----------------------------------------");
  }
  
  @Override
  public void onTestFailure(ITestResult tr) {
	  String testName = tr.getName();
	  log("----------------------------- " + testName + " failed ----------------------------------------");
 	  failArray.add(tr.getTestClass().getName()+ "." + tr.getName()); 
  }

  @Override
  public void onTestSkipped(ITestResult tr) {
	  String testName = tr.getName();
	  log("----------------------------- " + testName + " skipped ----------------------------------------");
          failArray.add(tr.getTestClass().getName()+ "." + tr.getName()); 
  }

  @Override
  public void onTestSuccess(ITestResult tr) {
	  String testName = tr.getName();
	  log("----------------------------- " + testName + " passed ----------------------------------------");
  }
  
  public static void log(String string) {
	  System.out.println(string);
  }

} 
