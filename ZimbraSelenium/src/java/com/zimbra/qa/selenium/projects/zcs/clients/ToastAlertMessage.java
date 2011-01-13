package com.zimbra.qa.selenium.projects.zcs.clients;

import org.testng.Assert;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.util.SleepUtil;


public class ToastAlertMessage extends SelNGBase{
	public String zGetMsg() {
		return ClientSessionFactory.session().selenium().getText("id=z_toast");
	}
	
	public boolean zAlertMsgExists(String expectedMsg, String customMsg) {
	    String actMsg = "";
		for(int i =0; i < 15; i++) {
		    	actMsg = zGetMsg();
			if(actMsg.indexOf(expectedMsg) >=0)
				return true;
			SleepUtil.sleep(2000);
		}
		Assert.assertTrue(false, customMsg + "\nActual("+actMsg+") didnt contain Expected("+expectedMsg+")");
		return false;
	}	
}
