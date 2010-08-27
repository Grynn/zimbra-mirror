package sandbox.projects.sand.tests.multithreaded;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class AbsMultithreadedTest {
	public static Logger logger = LogManager.getLogger(AbsMultithreadedTest.class);
	
	public AbsMultithreadedTest() {
		logger.info("new AbsMultithreadedTest");
	}
	
	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			logger.warn("Caught InterruptedException during sleep.  Ignoring.");
		}
	}
}
