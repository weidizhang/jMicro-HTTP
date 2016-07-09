/*
 * Part of jMicro-HTTP
 * 
 * @author Weidi Zhang
 */
package io.github.weidizhang.jmicrohttp;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class HTMLTemplate {
	
	public String messageError403(String path) {
		return messageError("403.html", path);
	}
	
	public String messageError404(String path) {
		return messageError("404.html", path);
	}
	
	public String messageDirListing(String path, File fileFromPath) {
		String response = messageError("directoryListing.html", path);
		
		ArrayList<File> folders = new ArrayList<File>();
		ArrayList<File> files = new ArrayList<File>();
		
		for (File file : fileFromPath.listFiles()) {
			if (file.isDirectory()) {
				folders.add(file);
			}
			else if (file.isFile()) {
				files.add(file);
			}
		}
		
		String foldersResponse = "";
		for (File folder : folders) {
			foldersResponse += "<tr><td><a href=\"" + path + folder.getName() + "\">" + folder.getName() + "</a></td><td>Directory</td><td>N/A</td></tr>";
		}
		
		String filesResponse = "";
		for (File file : files) {
			foldersResponse += "<tr><td><a href=\"" + path + file.getName() + "\">" + file.getName() + "</a></td><td>File</td><td>" + formatFileSize(file.length()) + "</td></tr>";
		}
		
		response = response.replace("{files}", foldersResponse + filesResponse);
		
		return response;
	}
	
	/* (modified) original formatFileSize method from bickster on StackOverflow */
	private String formatFileSize(long size) {
	    String hrSize = null;

	    double b = size;
	    double k = size / 1024.0;
	    double m = ((size / 1024.0) / 1024.0);
	    double g = (((size / 1024.0) / 1024.0) / 1024.0);

	    DecimalFormat dec = new DecimalFormat("0.00");
	    if (g > 1) {
	        hrSize = dec.format(g).concat(" GB");
	    }
	    else if (m > 1) {
	        hrSize = dec.format(m).concat(" MB");
	    }
	    else if (k > 1) {
	        hrSize = dec.format(k).concat(" KB");
	    }
	    else {
	        hrSize = dec.format(b).concat(" B");
	    }

	    return hrSize;
	}
	
	private String messageError(String file, String path) {
		String response = readLocalFile(file);
		response = response.replace("{path}", path);
		response = response.replace("{version}", HTTPServer.getVersion());
		
		return response;
	}	
	
	private String readLocalFile(String fileName) {
		String resultHtml = "";
		Scanner scanner = new Scanner(getClass().getResourceAsStream(fileName));
		
		while (scanner.hasNextLine()) {
			resultHtml += scanner.nextLine() + "\n";
		}
		
		scanner.close();
		
		return resultHtml;
	}
}
