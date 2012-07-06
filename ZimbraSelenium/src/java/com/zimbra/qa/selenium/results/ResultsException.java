package com.zimbra.qa.selenium.results;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ResultsException extends Exception {
	protected static Logger logger = LogManager.getLogger(ResultsException.class);

	private static final long serialVersionUID = 7716078373482619354L;

	public ResultsException(String message) {
		super(message);
		logger.error(message, this);
	}

	public ResultsException(Throwable cause) {
		super(cause);
		logger.error(cause.getMessage(), cause);
	}

	public ResultsException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message, cause);
	}


}
