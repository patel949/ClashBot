package org.jprojects.lib.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jprojects.lib.Token;

import com.mysql.cj.xdevapi.SqlResult;

public class DatabaseConnection {
	
	private static DatabaseConnection database = new DatabaseConnection();
	
	private Connection conn;
	
	private DatabaseConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        conn = null;
 
        try {
            // connect way #1
            String url1 = "jdbc:mysql://localhost:3306/";
            String user = Token.getDBUsername();
            String password = Token.getDBPassword();
 
            conn = DriverManager.getConnection(url1, user, password);
            if (conn == null) {
                System.out.println("Failed to Connect to the database.");
                return;
            }
 
        } catch (SQLException ex) {
            System.out.println("An error occurred while attempting to connect to the database:");
            ex.printStackTrace();
        }
	}
	
	public static DatabaseConnection getDatabaseConnection() {
		return database;
	}
	
	
	public Connection getConnection() {
		return conn;
	}
	
}