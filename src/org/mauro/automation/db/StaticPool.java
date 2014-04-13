package org.mauro.automation.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mauro.automation.LogUtils;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class StaticPool {

	private static final Logger log = LogUtils.loggerForThisClass();
	
	private static final BoneCP connectionPool;

	static {

		try {
			// load the database driver (make sure this is in your classpath!)
			Class.forName("org.mariadb.jdbc.Driver");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Bad error loading the database driver, aborting", e);
			System.exit(-1);
		}

		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl("jdbc:mariadb://localhost:3306/test"); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
		config.setUsername("test");
		config.setPassword("test");
		config.setMinConnectionsPerPartition(5);
		config.setMaxConnectionsPerPartition(10);
		config.setPartitionCount(1);

		BoneCP tmpConnectionPool = null;
		try {
			tmpConnectionPool = new BoneCP(config);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Bad error setting up the database pool, aborting", e);
			System.exit(-1);
		}
		connectionPool = tmpConnectionPool; //trick needed bacause javac does not understand initialization inside try and catch, neither the System.exit()

		/*
		 * connection = connectionPool.getConnection(); // fetch a connection
		 * 
		 * if (connection != null){ System.out.println("Connection successful!"); Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS"); // do something with the connection. while(rs.next()){ System.out.println(rs.getString(1)); // should print out "1"' } }
		 */

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				connectionPool.shutdown(); // shutdown connection pool.
			}
		}));
	}
	
	public static Connection getConnection() throws SQLException{
		return connectionPool.getConnection();
	}
}
