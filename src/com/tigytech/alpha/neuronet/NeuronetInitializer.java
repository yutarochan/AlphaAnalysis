package com.tigytech.alpha.neuronet;

import java.sql.Connection;
import java.sql.DriverManager;

import com.tigytech.alpha.constants.NeuronetConstants;

public class NeuronetInitializer {

	private static Connection conn;
	
	static {
		initializeConnection();
	}
	
	public static void initializeConnection() {
		if (!NeuronetConstants.IS_CONFIGURATION_SET)
			System.err.println("Cannot initiate Neuronet database connection. Configuration settings are not present.");
		else {
			String db_url = "jdbc:mysql://" + NeuronetConstants.db_server + ":" + NeuronetConstants.db_port + "/" + NeuronetConstants.db_name;
			
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(db_url, NeuronetConstants.db_username, NeuronetConstants.db_password);
			} catch (Exception e) {
				System.err.println("Error attempting to connect to " + NeuronetConstants.db_server + ". Check configuration file.");
				e.printStackTrace();
			}
		}
	}
	
	public static void closeConnection() {
	}
}