package org.mauro.automation.microserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class HttpServerBase {
	HttpServer server;
	
    public HttpServerBase() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    
    public void addPage(Page p){
    	server.createContext(p.getBasePath(), p);
    }

}