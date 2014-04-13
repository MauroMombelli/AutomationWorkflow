package org.mauro.automation.microserver.page;

import java.util.Map;

import org.mauro.automation.microserver.PageHtml;
import org.mauro.automation.microserver.exception.PageException;
import org.mauro.automation.microserver.sessions.SessionData;

import com.sun.net.httpserver.HttpExchange;

public class TestPost extends PageHtml {
	//private static final Logger log = LogUtils.loggerForThisClass(); 
	
	@Override
	public String getBasePath() {
		return "/testPost/";
	}

	@Override
	public String getHtml(String[] url, Map<String, String> valori, SessionData session) throws PageException {
		StringBuilder b = new StringBuilder();
		b.append("<form method='post'><frameset>");
		
		b.append("<input type='text' name='testTxt' value='"+session.getValue("testTxt")+"'>");
		b.append("<input type='submit' name='btn' value='send'>");
		
		b.append("</frameset></form>");
		
		return b.toString();
		//throw new PageException(404);
	}
	
	@Override
	public void executePost(String[] url, Map<String, String> valori, HttpExchange t, SessionData session) throws PageException {
		String test = valori.get("testTxt");
		System.out.println("testTxt: "+test);
		session.setValue("testTxt", test);
	}

}
