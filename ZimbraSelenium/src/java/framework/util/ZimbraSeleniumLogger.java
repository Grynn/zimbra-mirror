//helper class for logging
package framework.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ZimbraSeleniumLogger {

	public static Logger mLog = Logger.getLogger(new GetCurClass().getCurrentClass());

	public static <T> void setmLog (Class <T> clazz) {
		if(clazz!=null)
		mLog = LogManager.getLogger(clazz);
	}

	private static class GetCurClass extends SecurityManager {
		private Class<?> getCurrentClass() {
			return getClassContext()[1];
		}
	}
}