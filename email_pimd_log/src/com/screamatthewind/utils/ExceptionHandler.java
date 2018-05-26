package com.screamatthewind.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

	public static void handleException(String functionName, String functionData, Exception e, Class<?> clazz) {

		Logger logger = LoggerFactory.getLogger(clazz);

		Throwable cause = e.getCause();

		if (cause != null)
			logger.error("Function: " + functionName + ": " + cause.toString());
		else
			logger.error("Function: " + functionName);

		logger.error("Error Message: " + e.getMessage());
		logger.error(functionData);
		logger.error("Exception", new Exception());
		
		if (cause != null)
			EmailUtil.sendExceptionEmail(functionName + ": " + cause.toString(), e.getMessage());
		else
			EmailUtil.sendExceptionEmail(functionName, e.getMessage());
	}
}
