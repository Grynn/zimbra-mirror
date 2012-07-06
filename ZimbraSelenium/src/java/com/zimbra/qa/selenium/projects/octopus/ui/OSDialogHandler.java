package com.zimbra.qa.selenium.projects.octopus.ui;
/*
 * @author:Hrushikesh Amdekar
 * This class can be used to write the Dialog Handling functions on OS system like (MAC, windows etc.)
 * Control identifiers can be added to static class ControlID for any new dialog which needs to be handled.
 * Check out the java API documentation of Ldtp for details.
 * http://ldtp.freedesktop.org/javadoc/
 * 
 */
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.cobra.ldtp.Ldtp;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OperatingSystem;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

public class OSDialogHandler {

	private String osType = null;
	private String BrowserType = null;
	public browserType bType = null;

	protected static Logger logger = LogManager.getLogger(OSDialogHandler.class);

	public enum browserType{
		IE,FF,CHROME
	}
	public static class ControlID
	{   // Control Identifiers for WinLDTP script. Required identifiers can be added to this class for other dialog boxes.
		public static String CHROME_UPLOAD_DIALOG_TITLE ="dlgOpen";
		public static String CHROME_UPLOAD_FILE_PATH_COMBO = "txtFilename";
		public static String CHROME_UPLOAD_OPEN_BUTTON="btnOpen3";
		public static String FF_UPLOAD_DIALOG_TITLE ="dlgFileUpload";
		public static String FF_UPLOAD_FILE_PATH_COMBO="txtFilename";
		public static String FF_UPLOAD_OPEN_BUTTON="btnOpen3";
		public static String IE_UPLOAD_DIALOG_TITLE = "dlgChooseFiletoUpload";
		public static String IE_UPLOAD_FILE_PATH_COMBO="txtFilename";
		public static String IE_UPLOAD_OPEN_BUTTON="btnOpen3";

	}


	public OSDialogHandler()
	{
		//Constructor to initialize the OSDialogHandlerClass.
		osType= OperatingSystem.getOSType().toString();
		BrowserType= ZimbraSeleniumProperties.getStringProperty("browser");
		SetBrowserType();

	}

	public String getOSType()
	{
		return this.osType;
	}
	public void SetBrowserType()
	{ // Methods sets the browser type as set in config file for selenium Execution.
		if(this.BrowserType.contains("firefox"))
		{
			bType=browserType.FF;
		}else if(this.BrowserType.contains("explore")||(this.BrowserType.contains("iexplore")))
		{
			bType=browserType.IE;
		}else if(this.BrowserType.contains("chrome"))
		{
			bType=browserType.CHROME;
		}
	}
	private void handleUploadDialog(String DialogId, String ComboId, String ButtonId, String FileName, String browser) throws HarnessException
	{

		//Initialize winLDTP instance for required browser window
		Ldtp l1 = new Ldtp(browser);

		//Get entire window list
		String[] win =l1.getWindowList();

		boolean windowFound = false;
		// Search for required window
		for(int j=0;j<=win.length;j++)
		{
			if(windowFound==false){
				logger.info("Window Name: "+win[j]);
				if(win[j].contains(browser)){
					logger.info("Window Name: "+win[j]);
					// Get the specific instance of browser window opened for octopus
					if(win[j].contains("VMware Octopus")){
						// set the new window name
						l1.setWindowName(win[j]);
						//get object list under required browser window instance.
						String [] obj = l1.getObjectList();
						//Search for required dialog window
						for(int i=0;i<obj.length;i++)
						{
							logger.info("Dialog Name: "+obj[i]);
							if(obj[i].contains(DialogId))
							{
								windowFound=true;
								l1.setWindowName(obj[i]);
								logger.info("Window Name: "+l1.getWindowName());
								//set the required values and click
								l1.setTextValue(ComboId, FileName);

								l1.click(ButtonId);
								// Wait till dialog disappears
								l1.waitTillGuiNotExist();

								break;
							}
						}
					}
				}
			}
		}
		if(!windowFound)
		{
			throw new HarnessException("Window you are trying to find does not exists. Please check if window exists");
		}
	}
	// Method calls winLDTP functions to upload a required file on octopus web page. Browser and OS type is set at the time of instance creation.
	public void uploadFileUsingDialog(String fileName) throws HarnessException{
		if(osType=="WINDOWS"){
			switch(bType){
			case IE:
				handleUploadDialog(ControlID.IE_UPLOAD_DIALOG_TITLE, ControlID.IE_UPLOAD_FILE_PATH_COMBO, ControlID.IE_UPLOAD_OPEN_BUTTON, fileName, "Explorer");
				break;
			case FF:
				handleUploadDialog(ControlID.FF_UPLOAD_DIALOG_TITLE, ControlID.FF_UPLOAD_FILE_PATH_COMBO, ControlID.FF_UPLOAD_OPEN_BUTTON, fileName, "Firefox");
				break;
			case CHROME:
				handleUploadDialog(ControlID.CHROME_UPLOAD_DIALOG_TITLE, ControlID.CHROME_UPLOAD_FILE_PATH_COMBO, ControlID.CHROME_UPLOAD_OPEN_BUTTON, fileName, "Chrome");
			}
		}else if(osType=="MAC"){
			//TO DO: implement me
			throw new HarnessException("Implement Me");
		}

	}

}
