package org.mauro.automation.microserver.page;

import java.util.Map;
import java.util.logging.Logger;

import org.mauro.automation.LogUtils;
import org.mauro.automation.microserver.PageHtml;
import org.mauro.automation.microserver.exception.PageException;
import org.mauro.automation.microserver.sessions.SessionData;

public class HomePage extends PageHtml {
	private static final Logger log = LogUtils.loggerForThisClass();
	private static final String homepage = "<div>Benvenuto nella homepage!</div>"; 
	
	@Override
	public String getBasePath() {
		return "/";
	}

	@Override
	public String getHtml(String[] url, Map<String, String> valori, SessionData session) throws PageException {
		log.finest("requested homepage");
		if (url.length == 0)
			return homepage;
		throw new PageException(404);
	}

}
