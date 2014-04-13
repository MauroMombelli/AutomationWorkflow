package org.mauro.automation.microserver.page;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Logger;

import org.mauro.automation.LogUtils;
import org.mauro.automation.microserver.Page;
import org.mauro.automation.microserver.exception.PageException;

import com.sun.net.httpserver.Headers;

public class CssHandler extends Page{
	private static final Logger log = LogUtils.loggerForThisClass();

	@Override
	public String getBasePath() {
		return "/css/";
	}
	
	@Override
	public byte[] getBody(String[] uri, Map<String, String> valori, com.sun.net.httpserver.HttpExchange t) throws PageException{
		log.finest("requested css");
		
		String path = t.getRequestURI().toString();
		if (!path.startsWith(getBasePath())){
			throw new PageException(404, null);
		}
		//System.out.println("Actual received path; "+uri);
		path = path.substring(1);
		//System.out.println("Calculated path; "+uri);
		File f = new File( path );
		//System.out.println("File path; "+f.getAbsolutePath());
		if ( f.getName().endsWith(".css") && f.exists() ){
			byte[] b;
			try {
				b = Files.readAllBytes(f.toPath());
				if (b!=null){
					Headers responseHeaders = t.getResponseHeaders();
					responseHeaders.add("Content-Type", "text/css; charset=utf-8"); //Content-Type: text/css; charset=utf-8
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
	public void executePost(String[] url, Map<String, String> valori, com.sun.net.httpserver.HttpExchange t)throws PageException {
		//no POST to CSS, WTF! do nothing, system will redirect to get
	}

}
