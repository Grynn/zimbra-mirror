package parallel;

import java.util.ArrayList;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class Driver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		XmlSuite xmlSuite = new XmlSuite();
		xmlSuite.setName("foo");
		xmlSuite.setThreadCount(6);
		xmlSuite.setParallel(XmlSuite.PARALLEL_METHODS);

		XmlTest xmlTest = new XmlTest(xmlSuite);
		xmlTest.setName("bar");
		
		ArrayList<XmlClass> classes = new ArrayList<XmlClass>();
		classes.add(new XmlClass("parallel.TestObject1"));
		classes.add(new XmlClass("parallel.TestObject2"));
		xmlTest.setXmlClasses(classes);
		
		ArrayList<XmlSuite> suites = new ArrayList<XmlSuite>();
		suites.add(xmlSuite);
		
		TestNG testNG = new TestNG();
		testNG.setXmlSuites(suites);
		testNG.run();
				
	}

}
