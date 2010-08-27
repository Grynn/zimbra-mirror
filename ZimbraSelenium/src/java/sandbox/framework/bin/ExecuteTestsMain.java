package sandbox.framework.bin;

import org.apache.log4j.BasicConfigurator;
import org.testng.TestNG;

import sandbox.projects.sand.tests.multithreaded.MultithreadedClass01;
import sandbox.projects.sand.tests.multithreaded.MultithreadedClass02;
import sandbox.projects.sand.tests.skip.TestSkipException;

public class ExecuteTestsMain {

	// TestNG configurations
	public static final String TEST_NG_SERIAL			= "classes";
	public static final String TEST_NG_PARALLEL_CLASSES	= "classes";
	public static final String TEST_NG_PARALLEL_METHODS	= "methods";
	
	public String		TestNgOutputFoldername	= "sandbox-output";
	public int			TestNgVerbosity			= 10;
	public String		TestNgParallel			= TEST_NG_SERIAL;
	public int			TestNgThreadCount		= 5;
	public Class<?>[]	TestNgTestClasses		=
							{
								TestSkipException.class,
								MultithreadedClass01.class,
								MultithreadedClass02.class
							};
	

	
	
	public ExecuteTestsMain() {
		
	}
	
	public void execute() {
		TestNG ng = new TestNG();
	
		ng.setTestClasses(TestNgTestClasses);

		ng.setOutputDirectory(TestNgOutputFoldername);
		
		ng.setVerbose(TestNgVerbosity);
		
		if ( TestNgParallel != TEST_NG_SERIAL ) { 
			ng.setParallel(TestNgParallel);
			ng.setThreadCount(TestNgThreadCount);
		}
		
		ng.run();
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();

		ExecuteTestsMain harness = new ExecuteTestsMain();
		harness.execute();
		
	}

}
