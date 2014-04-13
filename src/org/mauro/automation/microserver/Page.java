package org.mauro.automation.microserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mauro.automation.LogUtils;
import org.mauro.automation.microserver.exception.PageException;
import org.mauro.automation.microserver.sessions.SessionData;
import org.mauro.automation.microserver.sessions.SessionManager;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class Page implements HttpHandler {
	/* those are shared between all Page! */
	private static final Logger log = LogUtils.loggerForThisClass();

	/**
	 * please do NOT override
	 */
	@Override
	public void handle(HttpExchange t) throws IOException {
		long time = System.currentTimeMillis();
		try {
			workIt(t);
		} catch (Throwable e) {
			log.log(Level.SEVERE, "handle exception", e);
			t.close(); // don't make client hang if we get some bad thing
		}
		time = System.currentTimeMillis() - time;
		// log.info(getBasePath()+" creation time: "+time);
	}

	private void workIt(HttpExchange t) {
		int code = 200; // all fine
		Map<String, String> values = new TreeMap<>();

		String redirectLocation;
		try {
			getValues(t, values);
			redirectLocation = values.get("Location");
		} catch (PageException e) {
			log.log(Level.SEVERE, "Exception loading GET parameter GET", e);
			
			redirectLocation = e.getRedirect();
			code = e.getCode();
		}

		SessionData session = SessionManager.fromCookie(t);
		
		ByteArrayOutputStream response = new ByteArrayOutputStream();
		

		String requestMethod = t.getRequestMethod();

		String url[] = null;
		url = extrapolateRequest(t.getRequestURI().toString());

		switch (requestMethod) {
		case "POST":
			try {
				postValues(t, values);

				executePost(url, values, t, session);

				code = 302;
				if (redirectLocation == null) {
					redirectLocation = t.getRequestURI().toString();
				}
			} catch (PageException e) {
				log.log(Level.SEVERE, "page exception", e);
				redirectLocation = e.getRedirect();
				code = e.getCode();
			}
		case "GET":
			try {
				byte[] html = getBody(url, values, t, session);
				if (html != null)
					response.write(html);
				else {
					response.write((getBasePath() + " risposta null").getBytes("utf-8"));
				}
			} catch (IOException e) {
				log.log(Level.SEVERE, "error elaborating get", e);
				code = 500; // 500 Internal Server Error
			} catch (PageException e) {
				log.log(Level.SEVERE, "page exception", e);
				redirectLocation = e.getRedirect();
				code = e.getCode();
			}
			break;
		default:
			code = 405; // 405 Method Not Allowed
		}

		Headers responseHeaders = t.getResponseHeaders();

		if (redirectLocation != null) {
			responseHeaders.add("Location", redirectLocation);
		}

		try {
			t.sendResponseHeaders(code, response.size());
			try (OutputStream os = t.getResponseBody()) {
				os.write(response.toByteArray());
			} catch (IOException e) {
				log.log(Level.WARNING, "exception writing data to client", e);
			}
		} catch (IOException e) {
			log.log(Level.WARNING, "exception writing headers to client", e);
		}
	}

	private void getValues(HttpExchange t, Map<String, String> values) throws PageException {
		String body = t.getRequestURI().toASCIIString();

		int mark = body.indexOf("?");
		if (mark < 0) {
			return;
		}
		body = body.substring(mark + 1); // remove ? and everything before

		try {
			int in = body.indexOf("="), end;
			String parm, value;
			while (in > 0) {

				parm = URLDecoder.decode(body.substring(0, in), "utf-8");
				end = body.indexOf("&");

				if (end >= 0) {
					value = body.substring(in + 1, end);
					body = body.substring(end + 1);
				} else {
					value = body.substring(in + 1);
					body = "";
				}

				value = URLDecoder.decode(value, "utf-8");
				// System.out.println("parametro get: " + parm + " valore: " + value);

				values.put(parm, value);

				in = body.indexOf("=");
			}
		} catch (UnsupportedEncodingException e) {
			throw new PageException(406);
		}
	}

	private void postValues(HttpExchange t, Map<String, String> values) throws PageException {

		try (Scanner s = new Scanner(t.getRequestBody())) {
			String body = s.useDelimiter("\\A").next();

			int in = body.indexOf("="), end;
			String parm, value;
			while (in > 0) {

				parm = URLDecoder.decode(body.substring(0, in), "utf-8");

				end = body.indexOf("&", in + 1);

				if (end >= 0) {
					value = body.substring(in + 1, end);
					body = body.substring(end + 1);
				} else {
					value = body.substring(in + 1);
					body = "";
				}
				value = URLDecoder.decode(value, "utf-8");

				//System.out.println("parametro post: " + parm + " valore: " + value);

				values.put(parm, value);

				in = body.indexOf("=");
			}
		} catch (NoSuchElementException e) {
			throw new PageException(400);
		} catch (UnsupportedEncodingException e) {
			throw new PageException(406);
		}
	}

	private String[] extrapolateRequest(String uri) {
		String ris[] = new String[0];
		// System.out.println("elaboro0 "+uri);

		String basePath = getBasePath();
		if (!basePath.endsWith("/")) {
			basePath += "/";
		}

		if (uri.startsWith(basePath)) {
			if (uri.length() == basePath.length()) {
				return ris;
			}
			// System.out.println("elaboro1 "+uri);
			int mark = uri.indexOf("?");
			if (mark < 0) {
				uri = uri.substring(basePath.length());
			} else {
				uri = uri.substring(basePath.length(), mark);
			}
			// System.out.println("elaboro2 "+uri);
			if (!uri.endsWith("/")) {
				uri = uri + "/";
			}
			// System.out.println("elaboro3 "+uri);
			ris = uri.split("/");
			// System.out.println("elaboro4 "+ Arrays.toString(ris)+" size "+ris.length );
		}
		return ris;
	}

	/*
	 * 
	 * HERE YOU FIND OVERRIDABLE METHOD
	 */

	/**
	 * this method return the absolute START path of the page. Please remember starting and ending /, and that all sub-path will be handled!
	 * 
	 * @return
	 */
	public abstract String getBasePath();

	/**
	 * execute post is called before getBody() in the case of user post. If successfully, client will get a 302 and be redirect to same page (or "redirect" field in get or post) If not, getBody() is called and it should show again the form so the user will insert it again
	 * 
	 * @param url
	 *            array of path called, excluded base path
	 * @param valori
	 *            map of get and post value, if same key, post wins over get
	 * @param t
	 * @param session 
	 * @return true if post was successfully (or cannot be done and user must be redirected somewhere), false
	 */
	public abstract void executePost(String url[], Map<String, String> valori, HttpExchange t, SessionData session) throws PageException;

	/**
	 * This method has to return the body of the response, and also take care of setting appropriate header in the HttpExchange
	 * 
	 * @param url
	 *            array of path called, excluded base path
	 * @param valori
	 *            map of get and post value, if same key, post wins over get
	 * @param t
	 * @param session 
	 * @return
	 * @throws PageException
	 */
	public abstract byte[] getBody(String url[], Map<String, String> valori, HttpExchange t, SessionData session) throws PageException;
}
