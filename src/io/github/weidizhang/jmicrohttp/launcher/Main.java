package io.github.weidizhang.jmicrohttp.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import io.github.weidizhang.jmicrohttp.HTTPServer;
import io.github.weidizhang.jmicrohttp.LogHelper;

public class Main {

	private static String configFileLoc = "config.properties";
	private static Properties properties = new Properties();
	
	public static void main(String[] args) {
		File configFile = new File(configFileLoc);
		
		LogHelper.getLogger().info("Starting jMicro-HTTP v" + HTTPServer.getVersion());
		
		if (!configFile.exists()) {
			generateDefaultConfig();
		}
		else {
			loadConfigAndStart();
		}
	}
	
	private static void loadConfigAndStart() {
		try {
			FileInputStream inStream = new FileInputStream(configFileLoc);
			properties.load(inStream);
			
			int port = Integer.parseInt(properties.getProperty("Port"));
			String workingDir = properties.getProperty("RootDirectory");
			boolean enableDirListing = Boolean.parseBoolean(properties.getProperty("EnableDirectoryListing"));
			boolean verboseMode = Boolean.parseBoolean(properties.getProperty("Verbose"));
			String phpCgiFile = properties.getProperty("PHPCGIFile");
			
			if (phpCgiFile.trim().equals("")) {
				phpCgiFile = null;
			}
			
			HTTPServer jMicroHTTP = new HTTPServer(port, workingDir, enableDirListing, verboseMode, phpCgiFile);
			jMicroHTTP.startServer();
		} catch (Exception e) {
			LogHelper.getLogger().severe("Error loading config file and starting");
			LogHelper.getLogger().severe(LogHelper.getStringStackTrace(e));
		}
	}

	private static void generateDefaultConfig() {
		try {
			FileOutputStream outStream = new FileOutputStream(configFileLoc);
			
			properties.setProperty("Port", "80");
			properties.setProperty("RootDirectory", "./www/");
			properties.setProperty("EnableDirectoryListing", "true");
			properties.setProperty("Verbose", "true");
			properties.setProperty("PHPCGIFile", "");
			
			properties.store(outStream, null);
			
			LogHelper.getLogger().info("Default config file created at: " + configFileLoc);
			LogHelper.getLogger().info("Edit accordingly and then restart jMicro-HTTP");
			LogHelper.getLogger().info("Note that backslashes (\"\\\") must be escaped.");
		} catch (Exception e) {
			LogHelper.getLogger().severe("Error generating default config file");
			LogHelper.getLogger().severe(LogHelper.getStringStackTrace(e));
		}
	}
}
