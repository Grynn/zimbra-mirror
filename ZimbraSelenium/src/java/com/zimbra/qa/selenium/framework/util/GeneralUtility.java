package com.zimbra.qa.selenium.framework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.common.util.tar.TarEntry;
import com.zimbra.common.util.tar.TarInputStream;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;

/**
 * This class contains general utilities methods that can be used across the framework
 * @author Jeffry Hidayat
 *
 */
public class GeneralUtility {

   private static Logger logger = LogManager.getLogger(GeneralUtility.class);

   /**
    * EQ = Equals
    * NEQ = Not Equals
    * @author Jeffry Hidayat
    *
    */
   public enum WAIT_FOR_OPERAND {
      EQ, NEQ, CONTAINS
   }

   /**
    * Dynamically waits either for the correct status or timeout, either one, which is faster.
    * Example:
    * Object[] input = null;
    * DesktopInstallUtil test = new DesktopInstallUtil();
    * 
    * \\Below is the static method
    * GeneralUtility.waitFor("projects.desktop.core.DesktopInstallUtil", null, true, "counter", input, WAIT_FOR_OPERAND.EQ, new Integer(10), 10000, 1000);
    * 
    * \\Below is the non-static method
    * GeneralUtility.waitFor("projects.desktop.core.DesktopInstallUtil", test, false, "counter1", input, WAIT_FOR_OPERAND.EQ, new Integer(10), 10000, 1000);
    *
    *Where definition of those 2 methods are:
    * public static int counter() {
    *    _count++;
    *    return _count;
    * }
    * public int counter1() {
    *    _count += 2;
    *    return _count;
    * }
    * @param apiClassPath API Class path, this is ONLY needed if the API being called is static 
    * @param nonStaticObject Object, who is the owner of the API, this is ONLY needed if the API being called is non-static
    * @param isStaticApi Is it a static API
    * @param apiName Name of the API to be called
    * @param parameters Parameters of the API
    * @param operand EQ (equal) or NEQ (not equal)
    * @param comparingObject Status of the object to be waited for
    * @param timeout Timeout to be waited for
    * @param delayBetweenCheck Delay between check until the timeout is hit
    * @return Object from the last API call before exiting this method
    * @throws HarnessException
    */
   public static Object waitFor(String apiClassPath,
                                Object nonStaticObject,
                                boolean isStaticApi,
                                String apiName,
                                Object[] parameters,
                                WAIT_FOR_OPERAND operand,
                                Object comparingObject,
                                long timeout,
                                long delayBetweenCheck) throws HarnessException {
      logger.info("timeout for waitFor is: " + timeout);
      int iteration = 0;

      if (!isStaticApi && nonStaticObject == null) {
         throw new HarnessException("nonStaticObject cannot be null for non-static API");
      }

      if (timeout <= 0) {
         // Leave the iteration to 0
      } else {
         iteration = (int)(timeout / delayBetweenCheck);
         if (timeout % delayBetweenCheck != 0) {
            iteration++;
         }
      }

      logger.info("iteration: " + iteration);

      Method method = null;
      Object output = null;
      // Now handle the class not found
      try {
         Method [] methodList = null;
         if (isStaticApi) {
            methodList = Class.forName(apiClassPath).getMethods();
         } else {
            methodList = nonStaticObject.getClass().getMethods();
         }
         for (int i = 0; i < methodList.length; i++) {
            logger.debug("methodlist[" + i + "].getName: " + methodList[i].getName());
            if (methodList[i].getName().equals(apiName)) {
               try {
                  if (isStaticApi) {
                     output = methodList[i].invoke(Class.forName(apiClassPath), parameters);
                  } else {
                     output = methodList[i].invoke(nonStaticObject, parameters);
                  }
                  method = methodList[i];
                  logger.debug("Breaking...");
                  break;
               } catch (IllegalArgumentException e) {
                  logger.debug("Continue to find other method", e);
               } catch (InvocationTargetException ive) {
                  method = methodList[i];
                  logger.debug("Hit InvocationTargetException.  Method: "+ method, ive);
                  break;
               } catch (Exception e) {
            	   logger.warn(e);
               }
            }

            if (method != null) {
               break;
            }
         }

      } catch (Exception e) {
         logger.info("Class name that you enter: " + apiClassPath + " doesn't exist!", e);
      }

      if (method == null) {
         throw new HarnessException("No matched method name: " + apiName);
      }

      int i = 0;

      while (i < iteration && !_waitforObjectComparator(output, comparingObject, operand)) {
         SleepUtil.sleep(delayBetweenCheck);
         i++;
         logger.debug("Iteration: " + i);
         logger.debug("Output is: " + output);
         try {
            if (isStaticApi) {
               output = method.invoke(Class.forName(apiClassPath), parameters);
            } else {
               output = method.invoke(nonStaticObject, parameters);
            }
         } catch (Exception e) {
        	 logger.warn(e);
            //TODO:
        	 logger.warn(e);
         }
      }
      logger.info("Final Iteration is: " + i);
      logger.info("Final Output is: " + output);
      return output;
   }

   /**
    * This method is only compliant for waitFor method with goal to compare the objects based on
    * the passed operand
    * @param mainObject First object
    * @param compObject Second object
    * @param operand EQ(Equals) or NEQ(Not Equals)
    * @return true if both objects are matched according to the operand, otherwise false.
    * @throws HarnessException
    */
   private static boolean _waitforObjectComparator(Object mainObject, Object compObject, WAIT_FOR_OPERAND operand)
   throws HarnessException {
      logger.debug("waitForOperand is: " + operand.toString());
      logger.debug("mainObject is: " + mainObject);
      logger.debug("compObject is: " + compObject);
      switch (operand){
      case EQ:
         if (mainObject == null) {
            return compObject == null;
         } else {
            return mainObject.equals(compObject);
         }
      case NEQ:
         if (mainObject == null) {
            return compObject != null;
         } else {
            return !mainObject.equals(compObject);
         }
      case CONTAINS:
         if (mainObject == null || compObject == null) {
            return true;
         } else {
            return mainObject.toString().contains(compObject.toString());
         }
      default:
         throw new HarnessException("Unsupported WaitFor operand: " + operand);
      }
   }

   /**
    * Finds whether the specified windows task name is running or not
    * @param taskName Task's name to be queried
    * @return true, if the task is running, otherwise, false
    * @throws IOException
    * @throws InterruptedException
    * @throws HarnessException 
    */
   public static boolean findWindowsRunningTask(String taskName) throws IOException, InterruptedException, HarnessException {
      String output = CommandLine.cmdExecWithOutput("TASKLIST /FI \"IMAGENAME EQ " + taskName + "\"");
      logger.debug("output: " + output);
      if (output.contains(taskName)) {
         return true;
      } else {
         return false;
      }
   }

   /**
    * Dynamically wait for element to be present with default timeout 30 seconds
    * @param owner Page Object, which is the owner of the locator
    * @param locator Locator of the element
    * @throws HarnessException 
    * @return true if element is present, or false if element is not present when timeout is hit
    */
   public static boolean waitForElementPresent(Object owner, String locator) throws HarnessException {
      return waitForElementPresent(owner, locator, 30000);
   }

   /**
    * Dynamically wait for element to be present with specified timeout
    * @param owner Page Object, which is the owner of the locator
    * @param locator Locator of the element
    * @param timeout Timeout to be waited for
    * @return true if element is present, or false if element is not present when timeout is hit
    * @throws HarnessException
    */
   public static boolean waitForElementPresent(Object owner, String locator, long timeout) throws HarnessException {
      Object[] params = {locator};
      return (Boolean)GeneralUtility.waitFor(null, owner, false, "sIsElementPresent",
            params, WAIT_FOR_OPERAND.EQ, true, timeout, 1000);
   }

   /**
    * Do an HTTP Post with the given URL link
    * @param Url URL to do HTTP Post
    * @throws HarnessException
    */
   public static void doHttpPost(String Url) throws HarnessException {
      try {

         // Replace all the white space with "%20"
         Url = Url.replaceAll(" ", "%20");

         URL url = new URL(Url);
         URLConnection conn = url.openConnection();
         
         //Get the response
         BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         StringBuffer sb = new StringBuffer();
         String line;

         while ((line = rd.readLine()) != null)
         {
            sb.append(line);
         }
         rd.close();
         logger.info("HTTP POST information ==> " + sb.toString());
         

      } catch (IOException e) {
         throw new HarnessException("HTTP Post failed!");
      }
   }

   /**
    * Synchronizing ZD client to ZCS through SOAP for the same email address in provided account
    * @param account Account
    * @throws HarnessException
    */
   public static void syncDesktopToZcsWithSoap(ZimbraAccount account)
   throws HarnessException {
      syncDesktopToZcsWithSoap(account, account.EmailAddress);
   }

   /**
    * Synchronizing ZD client to ZCS through SOAP for the specified email address
    * @param account Account
    * @param emailAddressToBeSynced Email address to be synced
    * @throws HarnessException
    */
   public static void syncDesktopToZcsWithSoap(ZimbraAccount account,
         String emailAddressToBeSynced)
   throws HarnessException {
      if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
         Stafpostqueue sp = new Stafpostqueue();
         sp.waitForPostqueue();

         String request =
               "<SyncRequest xmlns=\"urn:zimbraOffline\"/>";

         account.soapSend(request,
               SOAP_DESTINATION_HOST_TYPE.CLIENT,
               emailAddressToBeSynced);

         sp.waitForPostqueue();

      }
   }

   /**
    * Un-tar the TGZ file
    * @param tarFile
    * @param dest
    * @throws HarnessException
    * @throws InterruptedException 
    */
   public static void untarBaseUpgradeFile(File tarFile, File dest) throws HarnessException, InterruptedException {  
      if (dest == null) {
         throw new HarnessException("dest cannot be null!");
      }

      if (!dest.exists()) {
         createDirectory(dest);
      }

      try {
         logger.info("tar file is: " + tarFile.getCanonicalPath());
         logger.info("dest path is: " + dest.getCanonicalPath());
         logger.debug("Initializing tarFileInputStream");
         FileInputStream tarFileInputStream = new FileInputStream(tarFile);
         
         logger.debug("Initializing gzipInputStream");
         GZIPInputStream gzipInputStream = new GZIPInputStream(tarFileInputStream);

         logger.debug("Initializing tarInputStream");
         TarInputStream tin = new TarInputStream(gzipInputStream);

         logger.debug("Getting the entries...");
         TarEntry tarEntry = tin.getNextEntry();  
         logger.debug("First tarEntry is: " + tarEntry.getName());
         while (tarEntry != null) {  
            File destPath = new File(dest.toString().trim() + File.separatorChar +
                  tarEntry.getName());  
            logger.info("destPath is: " + destPath);

            if (tarEntry.isDirectory()) {
               createDirectory(destPath);

            } else {
               FileOutputStream fout = new FileOutputStream(destPath);
               tin.copyEntryContents(fout);  
               fout.close();

            }  

            tarEntry = tin.getNextEntry();  

         }

         tin.close();
         tin = null;

      } catch (IOException ie) {
         String message = "Getting IO Exception while untarring the file from: " +
               tarFile + " to: " + dest;
         logger.info(message);
         logger.info(ie.getMessage());
         throw new HarnessException(message);
      }
   }

   /**
    * Creating a directory recursively depending on the existence of the parents
    * @param dir
    * @return
    * @throws HarnessException
    */
   public static boolean createDirectory(File dir) throws HarnessException {
      if (dir == null) {
         throw new HarnessException("dir cannot be null");
      }

      String [] dirNames = dir.toString().split(Character.toString(File.separatorChar));

      StringBuilder currentPath = new StringBuilder("");
      for (int i = 0; i < dirNames.length; i++) {
         currentPath.append(File.separatorChar).append(dirNames[i].trim());
         File currentDir = new File(currentPath.toString());
         boolean currentDirExists = currentDir.exists();

         if (!currentDirExists) {
            boolean result = currentDir.mkdir();
            if (!result) {
               return false;
            }
         }
      }

      return true;
   }

   /**
    * Delete the non-empty directory
    * @param path
    * @return
    */
   public static boolean deleteDirectory(File path) {
      if( path.exists() ) {
         File[] files = path.listFiles();
         logger.debug("Number of files to be deleted: " + files.length);
         try {
            for(int i = 0; i < files.length; i++) {
               if(files[i].isDirectory()) {
                  deleteDirectory(files[i]);
               }
               else {
                  logger.debug("Deleting: " + files[i].getCanonicalPath());
                  files[i].delete();
               }
            }
            
         } catch (IOException ie) {
            logger.debug("Ignoring IO Exception while deleting the file");
         }
       } else {
          logger.debug("path doesn't exist");
       }

       logger.debug("Now removing the top directory...");
       return( path.delete() );
   }
}
