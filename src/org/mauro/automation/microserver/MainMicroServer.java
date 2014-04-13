package org.mauro.automation.microserver;

import org.mauro.automation.microserver.page.CssHandler;
import org.mauro.automation.microserver.page.FavIconHandler;
import org.mauro.automation.microserver.page.HomePage;

public class MainMicroServer {

	public static void main(String args[]){
		HttpServerBase baseServer;
		try {
			baseServer = new HttpServerBase();
			
			
			/* SPECIAL PAGE*/
			/*CSS*/
			baseServer.addPage( new CssHandler() );
			/*favIcon*/
			baseServer.addPage( new FavIconHandler() );
			/*base handler*/
			baseServer.addPage( new HomePage() );
			
			/*STANDARD PAGE*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
