package sandbox.projects.sand.tests.multithreaded;

import org.testng.annotations.Test;

public class MultithreadedClass01 extends AbsMultithreadedTest {

	public MultithreadedClass01() {
		logger.info("new MultithreadedClass01");
	}
	
	@Test()
	public void testMethod01() {
		logger.info("MultithreadedClass01.testMethod01 ...");
		sleep(1000);
		logger.info("MultithreadedClass01.testMethod01 ... done");
	}

	@Test()
	public void testMethod02() {
		logger.info("MultithreadedClass01.testMethod02 ...");
		sleep(2000);
		logger.info("MultithreadedClass01.testMethod02 ... done");
	}

	@Test()
	public void testMethod03() {
		logger.info("MultithreadedClass01.testMethod02 ...");
		sleep(3000);
		logger.info("MultithreadedClass01.testMethod02 ... done");
	}

	@Test()
	public void testMethod04() {
		logger.info("MultithreadedClass01.testMethod02 ...");
		sleep(4000);
		logger.info("MultithreadedClass01.testMethod02 ... done");
	}

	@Test()
	public void testMethod05() {
		logger.info("MultithreadedClass01.testMethod02 ...");
		sleep(5000);
		logger.info("MultithreadedClass01.testMethod02 ... done");
	}


}
