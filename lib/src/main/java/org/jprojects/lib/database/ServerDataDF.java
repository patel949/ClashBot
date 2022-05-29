package org.jprojects.lib.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jprojects.lib.Main;

public class ServerDataDF {
	private static Connection connection;
	private static Map<String, String> queries;
	
	private static final String SCHEMA = "identify";
	private static final String TABLE_NAME = "iden003_discord_server_data";
	private static boolean isInit = false;
	
	static {
		isInit = true;
		connection = DatabaseConnection.getDatabaseConnection().getConnection();
		queries = new HashMap<String,String>();
		queries.put("getServerDataByID", 
				"SELECT * FROM " + ServerDataDF.SCHEMA + "." + ServerDataDF.TABLE_NAME + " a WHERE IDEN001_DISCORD_ID=?");
		queries.put("addNewServerDataToDatabase", 
				"INSERT INTO " + ServerDataDF.SCHEMA + "." + ServerDataDF.TABLE_NAME + " (IDEN001_DISCORD_ID, IDEN003_PREFIX, IDEN003_DEFAULT_CHANNEL, IDEN003_WAR_MESSAGE_1, IDEN003_WAR_MESSAGE_2) VALUES(?,?,?,?,?)");
	}
	
	private static final HashMap<String, ServerDataDF> cache = new HashMap<String, ServerDataDF>();
	private static final Object lock = new Object(); //I don't need anything fancy.
	String serverID;
	String prefix = ".";
	String defaultChannel = "0";
	String warMessage1 = "War is ending in an hour and a half. Please get an attack in!";
	String warMessage2 = "War is ending in half an hour. please finish all attacks!";
	
	
	
	private ServerDataDF(String serverID) {
		this.serverID = serverID;
	}

	public static ServerDataDF getServer(String serverID) {
		ServerDataDF cached = null;
		synchronized(lock) {
			cached = cache.get(serverID);
		}
		if (cached != null)
			return cached;
		
		ServerDataDF newServer = read(serverID);
		
		if (newServer == null) {
			newServer = new ServerDataDF(serverID);
			newServer.setDefaultChannel(Main.getJDA().getGuildById(serverID).getDefaultChannel().getId());
			newServer.write();
		}
		
		//cache it
		synchronized (lock) {
			cache.put(serverID, newServer);
		}
		
		//return it
		System.out.println("Server ID#" + serverID + " loaded.");
		return newServer;
	}
	
	public String getWarReminder(int reminderNumber) {
		if (reminderNumber > 2 || reminderNumber <= 0)
			return "";
		return reminderNumber == 1 ? warMessage1 : warMessage2;
	}
	
	public void setWarReminder(int reminderNumber, String reminder) {
		if (reminderNumber == 1)
			warMessage1 = reminder;
		if (reminderNumber == 2)
			warMessage2 = reminder;
	}
	
	//TODO from here on.
	private static ServerDataDF read(String serverID) {
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(queries.get("getServerDataByID"));
			ps.setString(1, serverID);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			if (!rs.first()) {
				System.out.println("No server found in database.");
				return null;
			}
			
			ServerDataDF sd = new ServerDataDF(serverID);
			sd.prefix = rs.getString(2);
			sd.defaultChannel = rs.getString(3);
			sd.warMessage1 = rs.getString(4);
			sd.warMessage2 = rs.getString(5);
			
			return sd;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean write() {
		ServerDataDF sd = read(serverID);
		if (sd == null)
			return writeNew();
		
		List<String> changed = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE " + ServerDataDF.SCHEMA + "." + ServerDataDF.TABLE_NAME + " SET");
		if (this.prefix != sd.prefix) {
			sb.append(" IDEN003_PREFIX=?,");
			changed.add(this.prefix);
		}
		if (this.defaultChannel != sd.defaultChannel) {
			sb.append(" IDEN003_DEFAULT_CHANNEL=?,");
			changed.add(this.defaultChannel);
		}
		if (this.warMessage1 != sd.warMessage1) {
			sb.append(" IDEN003_WAR_MESSAGE_1=?,");
			changed.add(this.warMessage1);
		}
		if (this.warMessage2 != sd.warMessage2) {
			sb.append(" IDEN003_WAR_MESSAGE_2=?,");
			changed.add(this.warMessage2);
		}
		if (!changed.isEmpty())
			sb.setLength(sb.length()-1);
		
		sb.append(" WHERE IDEN001_DISCORD_ID=?");
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(sb.toString());
			
			for (int i = 0; i < changed.size(); i++) {
				ps.setString(i+1,changed.get(i));
			}
			ps.setString(changed.size()+1, this.serverID);
			ps.execute();
			return ps.getUpdateCount() == 1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean writeNew() {
		try {
			PreparedStatement ps = connection.prepareStatement(queries.get("addNewServerDataToDatabase"));
			ps.setString(1, serverID);
			ps.setString(2, prefix);
			ps.setString(3, defaultChannel);
			ps.setString(4, warMessage1);
			ps.setString(5, warMessage2);
			ps.execute();
			return ps.getUpdateCount() == 1;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean setPrefix(String newPrefix) {
		//set prefix
		prefix = newPrefix;
		
		return write();
	}
	
	public String getPrefix() {
		return prefix;
	}

	public boolean setDefaultChannel(String string) {
		defaultChannel = string;
		return write();
	}
	
	public long getDefaultChannel() {
		return Long.parseLong(defaultChannel);
	}
}
