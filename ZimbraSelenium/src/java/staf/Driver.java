package staf;

public class Driver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		projects.html.bin.ExecuteTests.WorkingDirectory = "/p4/matt/main/ZimbraSelenium";
		projects.zcs.bin.ExecuteTests.WorkingDirectory = "/p4/matt/main/ZimbraSelenium";
		StafIntegration staf = new StafIntegration();
		staf.invokeHarnessMethod("html", "debugSuite".split(","));
	}

}
