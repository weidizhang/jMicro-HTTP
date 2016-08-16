/*
 * Part of jMicro-HTTP
 * 
 * @author Weidi Zhang
 */
package io.github.weidizhang.jmicrohttp;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class HTTPServer {
	
	private HttpServer httpServer;
	private static String version = "1.1.1";
		
	public HTTPServer(int port, String directory, boolean enableDirListing, boolean verboseMode, String phpCgiFile) {		
		try {
			httpServer = HttpServer.create(new InetSocketAddress(port), 0);
			httpServer.createContext("/", new HTTPHandler(directory, enableDirListing, verboseMode, phpCgiFile));
			httpServer.setExecutor(null);
		} catch (IOException e) {
			LogHelper.getLogger().severe("Error at initialization");
			LogHelper.getLogger().severe(LogHelper.getStringStackTrace(e));
		}        
	}
	
	public void startServer() {
		httpServer.start();
		
		LogHelper.getLogger().info("Server started");
	}
	
	public void stopServer() {
		httpServer.stop(0);
		
		LogHelper.getLogger().info("Server stopped");
	}
	
	public static String getVersion() {
		return version;
	}
}