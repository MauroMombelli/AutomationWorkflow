package org.mauro.automation.microserver.page.crud;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mauro.automation.LogUtils;
import org.mauro.automation.db.StaticPool;
import org.mauro.automation.microserver.PageHtml;
import org.mauro.automation.microserver.exception.PageException;
import org.mauro.automation.microserver.sessions.SessionData;

import com.sun.net.httpserver.HttpExchange;


public class CrudPage extends PageHtml{
	private static final Logger log = LogUtils.loggerForThisClass();
	
	@Override
	public String getBasePath() {
		return "/crud/";
	}

	@Override
	public String getHtml(String[] url, Map<String, String> valori, SessionData session) throws PageException {
		
		StringBuilder response = new StringBuilder();
		switch(url.length){
		case 0:
			getAllTable(session);
			break;
		case 1:
			getTable(session, url[0]);
			break;
		case 2:
			getRow(session, url[0], url[1]);
			break;
		default:
			throw new PageException(404);
		}
		return response.toString();
	}

	private void getRow(SessionData userSession, String tableName, String tableId) {
		// TODO Auto-generated method stub
		
	}

	private void getTable(SessionData userSession, String tableName) {
		// TODO Auto-generated method stub
		
	}

	private void getAllTable(SessionData userSession) throws PageException {
		try ( Connection connection = StaticPool.getConnection() ){
			connection.prepareStatement("");
		} catch (SQLException e) {
			log.log(Level.SEVERE, "error getting connection from pool", e);
			throw new PageException(503);
		}
		
	}

	@Override
	public void executePost(String[] url, Map<String, String> valori, HttpExchange t) throws PageException {
		// TODO Auto-generated method stub
		
	}

}
