package org.mauro.automation.microserver;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mauro.automation.LogUtils;
import org.mauro.automation.microserver.exception.PageException;
import org.mauro.automation.microserver.sessions.SessionData;
import org.mauro.automation.microserver.sessions.SessionManager;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public abstract class PageHtml extends Page {
	/*those are shared between all Page!*/
	private static final Logger log = LogUtils.loggerForThisClass();
	private static final File structBase = new File("structure.html");
	private static String htmlBase;
	private static long lastModBase = 0;
	
	/*those are page specific*/
	private String title = "Automation Workflow";
	
	private void loadBase() {
		long lastMod = structBase.lastModified();
		if (htmlBase == null || lastMod > lastModBase) {
			log.info("reloading structure.html, was "+lastModBase+" is "+lastMod);
			
			if (!structBase.exists()){
				htmlBase = "no structure.html found!";
				log.severe("no structure.html found!");
				return;
			}
				
			try {
				byte[] b = Files.readAllBytes(structBase.toPath());
				if (b != null) {
					htmlBase = new String(b);
					lastModBase = lastMod;
				}
			} catch (IOException e) {
				log.log(Level.SEVERE, "no valid structure.html readed!", e);
			}
		}
	}
	/**
	 * This method call getHtml() and include the result into structure.html, also add the creation time and some header to the HTTP response.
	 * Override this method if you want to send out non utf-8 text/html, like image, css, or simply you don't want your structure.html in the way, remember to call getHtml() if needed 
	 * @param url array of path called, excluded base path
	 * @param valori map of get and post value, if same key, post wins over get
	 * @param t
	 * @return
	 * @throws PageException
	 */
	public byte[] getBody(String url[], Map<String, String> valori, HttpExchange t, SessionData session) throws PageException{
		long time = System.currentTimeMillis();
		
		loadBase();
		
		long visitCount = countHowManyVisitUserDoes(session);		
		
		String response = htmlBase.replaceFirst("<!--HTML_TITLE-->", title).replaceFirst("<!--HTML_BODY-->", getHtml(url, valori, session));
		response = response.replace("<!--CREATION_TIME-->", "pagina creata in " + (System.currentTimeMillis() - time) + " ms, ed è stata visitata da te "+visitCount);
		
		Headers responseHeaders = t.getResponseHeaders();
		/*
		responseHeaders.add("Content-Type", "text/html;charset=utf-8");
		responseHeaders.add("Cache-Control", "no-cache, must-revalidate");
		responseHeaders.add("Pragma", "no-cache");
		responseHeaders.add("Expires", "0");
		*/
		if ( session.isNew() ){
			String cookie = SessionManager.toCookie(session, responseHeaders);
			if (cookie!=null){
				responseHeaders.add("Set-Cookie", cookie );
				log.log(Level.INFO, "setting cookie");
				session.setNew(false);
			}
		}

		try {
			return response.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			log.log(Level.SEVERE, "no valid session id", e);
		}
		return null;
	}
	
	private long countHowManyVisitUserDoes(SessionData session) {
		String value = session.getValue("r_number_"+getBasePath() );
		long val = 0;
		if (value != null){
			log.log(Level.FINEST, "found cookie "+value);
			try{
				val = Long.parseLong(value);
			}catch(NumberFormatException e){
				log.log(Level.WARNING, "bad cookie "+value, e);
			}
		}else{
			log.log(Level.FINE, "new cookie for "+getBasePath());
			/*
			for (Entry<String, String> entry : session.getValues().entrySet()){
				System.out.println("v: "+entry.getKey()+" \t"+entry.getValue());
			}
			*/
		}
		val++;
		session.setValue("r_number_"+getBasePath(), val+"");
		return val;
	}
	/**
	 * by default html page will redirect to GET method if POST is used.
	 * Override this method if you want to execute your own post, and remember to throw a PageException if you DON'T want the user redirected to GET.
	 * Also remember that this method should be used to do LOGIC, output has to be done into getHtml()
	 * For example, if form data is wrong, throw a new PageExeception(200) and then take care on getHtml to add user input and error handling.
	 * @param url array of path called, excluded base path
	 * @param valori map of get and post value, if same key, post wins over get
	 * @param t 
	 * @return true if post was successfully (or cannot be done and user must be redirected somewhere), false  
	 */
	@Override
	public void executePost(String[] url, Map<String, String> valori, HttpExchange t, SessionData session) throws PageException {
		//no POST to homepage, system will redirect to get
	}
	
	/**
	 * this method return the html to be included in the structure.html file at the special tag <!--HTML_BODY-->
	 * @param session 
	 * @return the page logic's html
	 */
	public abstract String getHtml(String url[], Map<String, String> valori, SessionData session) throws PageException;
}
