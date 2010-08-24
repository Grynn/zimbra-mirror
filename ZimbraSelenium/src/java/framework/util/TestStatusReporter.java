package framework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;

import org.testng.ITestResult;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;

import framework.core.SelNGBase;

public class TestStatusReporter extends TestListenerAdapter {  
   private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s");
   private Calendar cl = Calendar.getInstance();
   private String productName = "";
   private String locale = "";
   private String serverName = "";   
   private String clientName = System.getenv("COMPUTERNAME");
   private PrintWriter    output=null;
   private ArrayList<String> failArray = new ArrayList();
   
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
	  postToReportServer(testName, "FAILED");	  
	  log("----------------------------- " + testName + " failed ----------------------------------------");
 	  failArray.add(tr.getTestClass().getName()+ "." + tr.getName()); 
  }

  @Override
  public void onTestSkipped(ITestResult tr) {
	  String testName = tr.getName();
	  postToReportServer(testName, "DIDNOT");	  
	  log("----------------------------- " + testName + " skipped ----------------------------------------");
          failArray.add(tr.getTestClass().getName()+ "." + tr.getName()); 
  }

  @Override
  public void onTestSuccess(ITestResult tr) {
	  String testName = tr.getName();
	  postToReportServer(testName, "PASSED");
	  log("----------------------------- " + testName + " passed ----------------------------------------");
  }
  
  public static void log(String string) {
	  System.out.println(string);
  }

  public  boolean postToReportServer(String tc_name, String result) {
	  //	  http://rainwarcloud-dx.corp.yahoo.com/Eragon/submitted.php?
	  //tc_name=test&product=Zimbra&build=123&intl=HK&date=%222008-10-11%2011:11:11%22&farm=qa60&acct_type=Free
	  
	  if(SelNGBase.suiteName.equals("debugSuite"))//ignore reporting when debugging
		 return false;

	  if(productName == "")
		  productName = "Zimbra-"+SelNGBase.appType.toLowerCase();
	  if(locale == "")
		  locale = ZimbraSeleniumProperties.getStringProperty("locale");
	  if(serverName == "")
		  serverName = ZimbraSeleniumProperties.getStringProperty("server");
	  
      String datetime =  sdf.format(cl.getTime());

	    try {
	        // Construct data
	        String data = URLEncoder.encode("tc_name", "UTF-8") + "=" + URLEncoder.encode(tc_name, "UTF-8");
	        data += "&" + URLEncoder.encode("product", "UTF-8") + "=" + URLEncoder.encode(productName, "UTF-8");
	        data += "&" + URLEncoder.encode("build", "UTF-8") + "=" + URLEncoder.encode(ZimbraSeleniumProperties.zimbraGetVersionString(), "UTF-8");
	        data += "&" + URLEncoder.encode("intl", "UTF-8") + "=" + URLEncoder.encode(locale, "UTF-8");
	        data += "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(datetime, "UTF-8");
	        data += "&" + URLEncoder.encode("browser", "UTF-8") + "=" + URLEncoder.encode(SelNGBase.currentBrowserName.trim(), "UTF-8");
	        data += "&" + URLEncoder.encode("host", "UTF-8") + "=" + URLEncoder.encode(clientName, "UTF-8");
	        data += "&" + URLEncoder.encode("farm", "UTF-8") + "=" + URLEncoder.encode(serverName, "UTF-8");
	        data += "&" + URLEncoder.encode("result", "UTF-8") + "=" + URLEncoder.encode(result, "UTF-8");
	        
	    
	        // Send data
	        URL url = new URL("http://qafe3.lab.zimbra.com/Eragon/submitted.php");
	        URLConnection conn = url.openConnection();
	        conn.setDoOutput(true);
	        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	        wr.write(data);
	        wr.flush();
	       System.out.println(data);
	        // Get the response
	        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line;
	        while ((line = rd.readLine()) != null) {
	            System.out.println(line);
	        }
	        wr.close();
	        rd.close();

	    } catch (Exception e) {
	    }
	    
	    return true;

	}
} 
