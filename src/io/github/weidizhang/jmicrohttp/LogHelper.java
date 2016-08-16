package io.github.weidizhang.jmicrohttp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class LogHelper {
	
	private static Logger logger;
	
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$s] %5$s%6$s%n");
		logger = Logger.getLogger("jMicro-HTTP");
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	public static String getStringStackTrace(Exception e) {
		StringWriter strWriter = new StringWriter();
		PrintWriter prWrite = new PrintWriter(strWriter);
		e.printStackTrace(prWrite);
		
		return strWriter.toString();
	}
}
