package seleniumserver;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.core.SeleniumService;
import framework.util.HarnessException;

public class ExecuteServiceMain {
	private static Logger logger = LogManager.getLogger(ExecuteServiceMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		
		logger.info("Starting ...");
		
		try {
			SeleniumService.getInstance().startSeleniumServer();
		} catch (HarnessException e) {
			logger.error("Unable to start.", e);
			return;
		}
		
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

}
