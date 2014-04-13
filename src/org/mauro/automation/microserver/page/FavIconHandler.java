package org.mauro.automation.microserver.page;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Logger;

import org.mauro.automation.LogUtils;
import org.mauro.automation.microserver.Page;
import org.mauro.automation.microserver.exception.PageException;
import org.mauro.automation.microserver.sessions.SessionData;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class FavIconHandler extends Page{
	private static final Logger log = LogUtils.loggerForThisClass();
	
	@Override
	public String getBasePath() {
		return "/favicon.ico";
	}
	
	@Override
	public byte[] getBody(String[] uri, Map<String, String> valori, HttpExchange t, SessionData session) throws PageException {
		log.finest("requested favicon.ico");
		File f = new File( "favicon.ico" );
		if ( f.exists() ){
			byte[] b;
			try {
				b = Files.readAllBytes(f.toPath());
				if (b!=null){
					Headers responseHeaders = t.getResponseHeaders();
					responseHeaders.add("Content-Type", "image/ico");
					return b;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		throw new PageException(404);
	}

	@Override
	public void executePost(String[] url, Map<String, String> valori, HttpExchange t, SessionData session) throws PageException {
		//no POST to imageicon, WTF! do nothing, system will redirect to get
	}
}
