package com.zimbra.qa.selenium.framework.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
      EQ, NEQ
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
            methodList = Class.forName(apiClassPath).getDeclaredMethods();
         } else {
            methodList = nonStaticObject.getClass().getDeclaredMethods();
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
                  logger.debug("Continue to find other method");
                  e.printStackTrace();
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }

            if (method != null) {
               break;
            }
         }

      } catch (Exception e) {
         e.printStackTrace();
         logger.info("Class name that you enter: " + apiClassPath + " doesn't exist!");
      }

      if (method == null) {
         throw new HarnessException("No matched method name: " + apiName);
      }

      int i = 0;
            
      while (i < iteration && !_waitforObjectComparator(output, comparingObject, operand)) {
         i++;
         logger.debug("Iteration: " + i);
         logger.debug("Output is: " + output.toString());
         try {
            if (isStaticApi) {
               output = method.invoke(Class.forName(apiClassPath), parameters);
            } else {
               output = method.invoke(nonStaticObject, parameters);
            }
         } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         SleepUtil.sleep(delayBetweenCheck);
      }
      logger.info("Final Output is: " + output.toString());
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
      switch (operand){
      case EQ:
         return mainObject.equals(compObject);
      case NEQ:
         return !mainObject.equals(compObject);
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
    */
   public static boolean findWindowsRunningTask(String taskName) throws IOException, InterruptedException {
      String output = CommandLine.cmdExecWithOutput("TASKLIST /FI \"IMAGENAME EQ " + taskName + "\"");
      logger.debug("output: " + output);
      if (output.contains(taskName)) {
         return true;
      } else {
         return false;
      }
   }
}
