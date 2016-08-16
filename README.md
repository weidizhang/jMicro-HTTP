# jMicro-HTTP

Created by Weidi Zhang

## License

Please read LICENSE.md to learn about what you can and cannot do with this source code.

## Description

jMicro-HTTP is a fully function HTTP server written in java.

It uses com.sun.net.httpserver.* as a base, which is the built in HTTP server, but is too basic to be functional for any real purpose by itself.


There is also support for PHP-CGI, though currently it is very basic. Headers are properly set and content is properly output, but reading GET or POST data
is not yet supported.

## Usage

Download the latest .jar release: https://github.com/weidizhang/jMicro-HTTP/releases

Run the jar using (command line):
```
java -jar jMicro-HTTP.jar
```

On first run, config.properties will be generated.

Edit this file, then run the jar again to start the server.