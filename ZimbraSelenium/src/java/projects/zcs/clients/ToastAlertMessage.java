package projects.zcs.clients;

import org.testng.Assert;

import framework.core.SelNGBase;

public class ToastAlertMessage extends SelNGBase{
	public String zGetMsg() {
		return selenium.getText("id=z_toast");
	}
	
	public boolean zAlertMsgExists(String expectedMsg, String customMsg) {
	    String actMsg = "";
		for(int i =0; i < 15; i++) {
		    	actMsg = zGetMsg();
			if(actMsg.indexOf(expectedMsg) >=0)
				return true;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Assert.assertTrue(false, customMsg + "\nActual("+actMsg+") didnt contain Expected("+expectedMsg+")");
		return false;
	}	
}
