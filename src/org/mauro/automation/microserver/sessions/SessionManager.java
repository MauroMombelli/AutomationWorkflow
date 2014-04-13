package org.mauro.automation.microserver.sessions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mauro.automation.LogUtils;

import com.sun.net.httpserver.HttpExchange;

public class SessionManager {
	private static final Logger log = LogUtils.loggerForThisClass();
	
	public static SessionData fromCookie(HttpExchange t) {
		List<String> list = t.getRequestHeaders().get("Cookie");
		if (list != null){
			if (list.size() > 0){
				return new SessionData(false, stringToMap( list.get(0) ));
			}
		}
		return new SessionData(true);
	}

	public static String toCookie(SessionData session) {
		return mapToString( session.getValues() );
	}

	public static String mapToString(Map<String, String> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (Entry<String, String> entry : map.entrySet()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append("; ");
			}
			String value = entry.getValue();
			try {
				stringBuilder.append( URLEncoder.encode(entry.getKey(), "UTF-8") );
				stringBuilder.append("=");
				stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
			} catch (UnsupportedEncodingException e) {
				log.log(Level.SEVERE, "This method requires UTF-8 encoding support", e);
			}
		}

		return stringBuilder.toString();
	}

	public static Map<String, String> stringToMap(String input) {
		Map<String, String> map = new HashMap<String, String>();

		String[] nameValuePairs = input.split("; ");
		for (String nameValuePair : nameValuePairs) {
			String[] nameValue = nameValuePair.split("=");
			try {
				map.put(URLDecoder.decode(nameValue[0], "UTF-8"), nameValue.length > 1 ? URLDecoder.decode(nameValue[1], "UTF-8") : "");
			} catch (UnsupportedEncodingException e) {
				log.log(Level.SEVERE, "This method requires UTF-8 encoding support", e);
			}
		}

		return map;
	}
}
