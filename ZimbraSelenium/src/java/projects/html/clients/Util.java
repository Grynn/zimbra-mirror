package projects.html.clients;

import framework.core.SelNGBase;

public class Util extends SelNGBase {

	public static void waitForElement(String locator) throws Exception{
		for(int i = 0; i < 40; i++) {
			Thread.sleep(500);
			if(SelNGBase.selenium.get().isElementPresent(locator)) 
				break;
			
						
		}
	}
}
