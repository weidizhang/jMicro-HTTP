package io.github.weidizhang.tester;

import io.github.weidizhang.jmicrohttp.HTTPServer;

public class Main {

	public static void main(String[] args) {
		HTTPServer jMicroHTTP = new HTTPServer(80, "D:/ServerTest", true, "C:/Users/Weidi Zhang/Documents/PHP/php-cgi.exe");
		jMicroHTTP.startServer();
	}
}
