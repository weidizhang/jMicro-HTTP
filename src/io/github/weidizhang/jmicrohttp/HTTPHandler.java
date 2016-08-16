/*
 * Part of jMicro-HTTP
 * 
 * @author Weidi Zhang
 */
package io.github.weidizhang.jmicrohttp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
	private String phpCgiFile;
	
	public HTTPHandler(String directory, boolean enableDirListing, String phpCgiFileLoc) {
		workingDir = new File(directory);
		dirListing = enableDirListing;
		phpCgiFile = phpCgiFileLoc;
		
		if (!workingDir.exists() || !workingDir.isDirectory()) {
			LogHelper.getLogger().warning("Specified root directory does not exist");
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
				
				File indexFileHtml = new File(requestedFile, "index.html");
				File indexFilePhp = new File(requestedFile, "index.php");
				
				if (indexFileHtml.exists()) {
					requestedFile = indexFileHtml;
				}
				else if (indexFilePhp.exists()) {
					requestedFile = indexFilePhp;				
				}
			}
			
			if (requestedFile.isFile()) {				
				if (!phpCgiFile.equals(null) && getFileExtension(requestedFile.getAbsolutePath()).equalsIgnoreCase("php")) {
					String phpResponse = getPhpCgiResponse(requestedFile.getAbsolutePath());
					
					int divideIndex = phpResponse.indexOf("\n\n");
					String phpHeaders = "";
					String phpBody = "";
					
					if (divideIndex > -1) {
						phpHeaders = phpResponse.substring(0, divideIndex);
						phpBody = phpResponse.substring(divideIndex + 1);
					}
					else {
						phpHeaders = phpResponse.trim();
					}
					
					String[] headersArray = phpHeaders.split("\n");
					for (String fullHeader : headersArray) {
						int headerDivide = fullHeader.indexOf(":");
						
						if (headerDivide > -1) {
							String headerName = fullHeader.substring(0, headerDivide);
							String headerContents = fullHeader.substring(headerDivide + 1).trim();
							
							if (headerName.equalsIgnoreCase("Status")) {
								String headerCode = headerContents;
								if (headerCode.indexOf(" ") > -1) {
									headerCode = headerCode.substring(0, headerCode.indexOf(" "));
									
									httpCode = Integer.parseInt(headerCode);
								}
							}
							
							httpEx.getResponseHeaders().set(headerName, headerContents);
						}
					}
									
					responseBytes = phpBody.getBytes();					
				}
				else {
					String mimeType = getMimeType(requestedFile.getAbsolutePath());
					responseBytes = readFile(requestedFile.getAbsolutePath());
					
					httpEx.getResponseHeaders().set("Content-Type", mimeType);
				}
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
			LogHelper.getLogger().severe("Error sending HTTP response");
			LogHelper.getLogger().severe(LogHelper.getStringStackTrace(e));
		}
	}
	
	private byte[] readFile(String path) {
		try {
			byte[] fileBytes = Files.readAllBytes(Paths.get(path));	
			
			return fileBytes;
		} catch (IOException e) {
			LogHelper.getLogger().severe("Error reading file being accessed");
			LogHelper.getLogger().severe(LogHelper.getStringStackTrace(e));
			
			return new byte[] {};
		}
	}
	
	private String getMimeType(String filePath) {	
		String mimeType = null;
		try {
			Path path = Paths.get(filePath);
			mimeType = Files.probeContentType(path);
		} catch (IOException e) {
			LogHelper.getLogger().severe("Error determining mime type");
			LogHelper.getLogger().severe(LogHelper.getStringStackTrace(e));
		}
		
		if (mimeType == null || mimeType.equals("")) {
			mimeType = "text/plain";
		}
		
		return mimeType;
	}
	
	private String getFileExtension(String filePath) {
		String[] fileParts = filePath.split("\\.");
		if (fileParts.length > 0) {
			return fileParts[fileParts.length - 1];
		}
		
		return "";
	}
	
	private String getPhpCgiResponse(String filePath) {
		try {
			String builtCmd = "\"" + phpCgiFile + "\" \"" + filePath + "\"";
			Process process = Runtime.getRuntime().exec(builtCmd);
			
			BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String output = "";
			while (true) {
				String line = bReader.readLine();
				if (line != null) {
					output += line + "\n";
				}
				else {
					break;
				}
			}
			
			bReader.close();
			
			return output.substring(0, output.length() - 1);
		} catch (IOException e) {
			LogHelper.getLogger().severe("Error getting response from PHP-CGI");
			LogHelper.getLogger().severe(LogHelper.getStringStackTrace(e));
			
			return "";
		}
	}
}
