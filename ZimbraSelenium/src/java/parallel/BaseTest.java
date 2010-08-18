package parallel;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BaseTest {
	private static Logger logger = LogManager.getLogger(BaseTest.class);
	
	private static int counter = 0;
	protected synchronized int myCounter() {
		return (++counter);
	}
	
	protected int baseID = 0;
	public BaseTest() {
		baseID = myCounter();
		logger.info("New BaseTest instance "+ baseID);
	}

	
	static {
		// Configure log4j
		BasicConfigurator.configure();
	}

}
