package projects.html.clients;

import framework.core.SelNGBase;
import framework.util.SleepUtil;

public class Util extends SelNGBase {

	public static void waitForElement(String locator) throws Exception{
		for(int i = 0; i < 40; i++) {
			SleepUtil.sleep(500);
			if(SelNGBase.selenium.get().isElementPresent(locator)) 
				break;
			
						
		}
	}
}
