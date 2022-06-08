package org.jprojects.lib.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import org.jprojects.lib.Token;

import com.mysql.cj.xdevapi.SqlResult;

public class DatabaseConnectionPool {
	
	private static DatabaseConnectionPool database = new DatabaseConnectionPool();

	private DataSource ds;
	private GenericObjectPool gPool = null;
	
	private DatabaseConnectionPool() {
		try {
    		Class.forName("com.mysql.cj.jdbc.Driver");
    	} catch (ClassNotFoundException e) {
    		System.err.println("Uh oh. JDBC Driver class not found.");
    		e.printStackTrace();
    	}
 
        // Creates an Instance of GenericObjectPool That Holds Our Pool of Connections Object!
        gPool = new GenericObjectPool();
        gPool.setMaxActive(5);
 
        // Creates a ConnectionFactory Object Which Will Be Use by the Pool to Create the Connection Object!
        ConnectionFactory cf = new DriverManagerConnectionFactory("jdbc:mysql://localhost:3306/", Token.getDBUsername(), Token.getDBPassword());
 
        // Creates a PoolableConnectionFactory That Will Wraps the Connection Object Created by the ConnectionFactory to Add Object Pooling Functionality!
        PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, gPool, null, null, false, true);

        ds = new PoolingDataSource(gPool);
        
        printDbStatus();
	}
	
	
 
    public GenericObjectPool getConnectionPool() {
        return gPool;
    }
 
    // This Method Is Used To Print The Connection Pool Status
    private void printDbStatus() {
        System.out.println("[DatabaseConnectionPool] Pool Status | Max.: " + getConnectionPool().getMaxActive() + "; Active: " + getConnectionPool().getNumActive() + "; Idle: " + getConnectionPool().getNumIdle());
    }
	public static DatabaseConnectionPool getDatabaseConnectionPool() {
		return database;
	}
	
	
	public Connection getConnection() {
		printDbStatus();
		Connection c;
		try {
			c = ds.getConnection();
			return c;
		} catch (SQLException e) {
			System.err.println("Error getting a connection: ");
			e.printStackTrace();
		}
		return null;
	}
	
}