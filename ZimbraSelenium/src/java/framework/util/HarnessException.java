package framework.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class HarnessException extends Exception {
	Logger logger = LogManager.getLogger(HarnessException.class);

	private static final long serialVersionUID = 4657095353247341818L;

	public HarnessException(String message) {
		super(message);
		logger.error(message);
	}

	public HarnessException(Throwable cause) {
		super(cause);
		logger.error(cause);
	}

	public HarnessException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message, cause);
	}

}
