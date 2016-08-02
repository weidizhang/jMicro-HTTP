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

See src/io/github/weidizhang/tester/Main.java