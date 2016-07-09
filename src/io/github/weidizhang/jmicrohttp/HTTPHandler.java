/*
 * Part of jMicro-HTTP
 * 
 * @author Weidi Zhang
 */
package io.github.weidizhang.jmicrohttp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class HTTPHandler implements HttpHandler {

	private File workingDir;
	private HTMLTemplate template = new HTMLTemplate();
	private boolean dirListing;
	
	public HTTPHandler(String directory, boolean enableDirListing) {
		workingDir = new File(directory);
		dirListing = enableDirListing;
		
		if (!workingDir.exists() || !workingDir.isDirectory()) {
			Logger.logError("specified directory does not exist");
		}
	}
	
	@Override
	public void handle(HttpExchange httpEx) {
		boolean useStringResponse = false;
		String response = "";
		byte[] responseBytes = {};
		
		int httpCode = 200;
		
		String requestedFilePath = httpEx.getRequestURI().toString();
		File requestedFile = new File(workingDir, requestedFilePath);
		
		if (requestedFile.exists()) {
			if (requestedFile.isDirectory()) {
				String requestLastChar = requestedFilePath.substring(requestedFilePath.length() - 1);
				
				if (!requestLastChar.equals("/")) {
					httpCode = 301;
					httpEx.getResponseHeaders().set("Location", requestedFilePath + "/");
					
					sendResponse(httpEx, httpCode, useStringResponse, response, responseBytes);
					return;
				}
				
				File indexFile = new File(requestedFile, "index.html");
				if (indexFile.exists()) {
					requestedFile = indexFile;
				}
			}
			
			if (requestedFile.isFile()) {			
				responseBytes = readFile(requestedFile.getAbsolutePath());
				
				String mimeType = getMimeType(requestedFile.getAbsolutePath());
				httpEx.getResponseHeaders().set("Content-Type", mimeType);
			}
			else {
				useStringResponse = true;
				if (dirListing) {
					response = template.messageDirListing(requestedFilePath, requestedFile);
				}
				else {
					httpCode = 403;
					response = template.messageError403(requestedFilePath);
				}
			}
		}
		else {
			useStringResponse = true;
			httpCode = 404;
			response = template.messageError404(requestedFilePath);
		}
		
		sendResponse(httpEx, httpCode, useStringResponse, response, responseBytes);
	}
	
	private void sendResponse(HttpExchange httpEx, int httpCode, boolean stringResponse, String responseStr, byte[] responseByte) {
		try {	        
			OutputStream outStream = httpEx.getResponseBody();
			
			if (stringResponse) {
				httpEx.sendResponseHeaders(httpCode, responseStr.length());
				outStream.write(responseStr.getBytes());
			}
			else {
				httpEx.sendResponseHeaders(httpCode, 0);
				outStream.write(responseByte, 0, responseByte.length);
			}
	        
			outStream.close();
		} catch (IOException e) {
			Logger.logError(e, "sending http response");
		}
	}
	
	private byte[] readFile(String path) {
		try {
			byte[] fileBytes = Files.readAllBytes(Paths.get(path));	
			
			return fileBytes;
		} catch (IOException e) {
			Logger.logError(e, "reading file");	
			
			return new byte[] {};
		}
	}
	
	private String getMimeType(String filePath) {	
		String mimeType = null;
		try {
			Path path = Paths.get(filePath);
			mimeType = Files.probeContentType(path);
		} catch (IOException e) {
			Logger.logError(e, "getting mime type");
		}
		
		if (mimeType == null || mimeType.equals("")) {
			mimeType = "text/plain";
		}
		
		return mimeType;
	}
}
