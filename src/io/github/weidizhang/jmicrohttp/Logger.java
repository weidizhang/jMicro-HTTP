/*
 * Part of jMicro-HTTP
 * 
 * @author Weidi Zhang
 */
package io.github.weidizhang.jmicrohttp;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
	
	public static void logNotice(String msg) {
		System.out.println("[" + getCurrentTime() + "] jMicro-HTTP - Notice: " + msg);
	}
	
	public static void logError(String msg) {
		System.err.println("[" + getCurrentTime() + "] jMicro-HTTP - An error occurred (" + msg + ")");
	}
	
	public static void logError(Exception e, String description) {
		logError(description);
		e.printStackTrace();
	}
	
	private static String getCurrentTime() {
		return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(Calendar.getInstance().getTime());
	}
}
