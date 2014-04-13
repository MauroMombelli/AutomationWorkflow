package org.mauro.automation.microserver;

import org.mauro.automation.microserver.page.CssHandler;
import org.mauro.automation.microserver.page.FavIconHandler;
import org.mauro.automation.microserver.page.HomePage;
import org.mauro.automation.microserver.page.TestPost;

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
			
			/*post exampletest*/
			baseServer.addPage( new TestPost() );
			
			/*STANDARD PAGE*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
