package org.mauro.automation;

import java.util.logging.Logger;

public class LogUtils {

	/**
	 * weird but working from user Cowan at https://stackoverflow.com/questions/80692/java-logger-that-automatically-determines-callers-class-name
	 * 
	 * @return a Logger for this class. Use it static
	 */

	public static Logger loggerForThisClass() {
		// We use the third stack element; second is this method, first is .getStackTrace()
		StackTraceElement myCaller = Thread.currentThread().getStackTrace()[2];
		return Logger.getLogger(myCaller.getClassName());
	}

}
