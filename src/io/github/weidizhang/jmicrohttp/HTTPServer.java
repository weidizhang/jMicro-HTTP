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
	private static String version = "1.0.0";
	
	public HTTPServer(int port, String directory, boolean enableDirListing) {		
		try {
			httpServer = HttpServer.create(new InetSocketAddress(port), 0);
			httpServer.createContext("/", new HTTPHandler(directory, enableDirListing));
			httpServer.setExecutor(null);
		} catch (IOException e) {
			Logger.logError(e, "initialization");
		}        
	}
	
	public void startServer() {
		httpServer.start();
		
		Logger.logNotice("Server started");
	}
	
	public void stopServer() {
		httpServer.stop(0);
		
		Logger.logNotice("Server stopped");
	}
	
	public static String getVersion() {
		return version;
	}
}