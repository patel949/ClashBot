package org.jprojects.lib.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jprojects.lib.util.Pair;

public class DiscordToClashDF {
	
	private static DiscordToClashDF discordToClashDF = new DiscordToClashDF();
	
	private Connection connection;
	private Map<String, String> queries;
	
	
	private static final boolean ADD = true;
	private static final boolean REMOVE = !ADD;
	private static final boolean GET_CLASH = true;
	private static final boolean GET_DISCORD = !GET_CLASH;
	
	private static final String SUBSCRIBER = "B";
	private static final String OWNER = "O";
	private static final String SERVER = "V";
	
	private static final String SCHEMA = "identify";
	private static final String TABLE_NAME = "iden001_discord_to_clash";
	
	private DiscordToClashDF() {
		connection = DatabaseConnection.getDatabaseConnection().getConnection();
		queries = new HashMap<String,String>();
		queries.put("getClashByDiscord", 
				"SELECT IDEN001_CLASH_ID FROM " + DiscordToClashDF.SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " a WHERE a.IDEN001_TYPE=? AND IDEN001_DISCORD_ID=?");
		queries.put("getDiscordByClash", 
				"SELECT IDEN001_DISCORD_ID FROM " + DiscordToClashDF.SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " a WHERE a.IDEN001_TYPE=? AND IDEN001_CLASH_ID=?");
		queries.put("getDiscordByClashMultiType",
				"SELECT IDEN001_DISCORD_ID FROM " + DiscordToClashDF.SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " a WHERE a.IDEN001_TYPE IN (?) AND IDEN001_CLASH_ID=?");
		queries.put("addDiscordClashRelationship",
				"INSERT INTO " + DiscordToClashDF.SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " (IDEN001_DISCORD_ID, IDEN001_CLASH_ID, IDEN001_TYPE) VALUES (?,?,?)");
		queries.put("removeDiscordClashRelationship", 
				"DELETE FROM " + SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " WHERE IDEN001_DISCORD_ID=? AND IDEN001_CLASH_ID=? AND IDEN001_TYPE=?");
		queries.put("getIdPairsByType", 
				"SELECT IDEN001_DISCORD_ID, IDEN001_CLASH_ID FROM " + DiscordToClashDF.SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " a WHERE a.IDEN001_TYPE=?");
		queries.put("checkIfRelationshipExists",
				"SELECT * FROM " + DiscordToClashDF.SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " WHERE IDEN001_DISCORD_ID=? AND IDEN001_CLASH_ID=? AND IDEN001_TYPE=?");
	}
	
	public static DiscordToClashDF getDiscordtoClashDF() {
		return discordToClashDF;
	}
	
	/*
	 * Gets clash id by discord id or discord id by clash id.
	 * All found matches will be returned in a list. 
	 * In the case of no matches, an empty list will be returned.
	 */
	private List<String> getClashOrDiscord(String clashOrDiscordID, String type, boolean getClash) {
		List<String> accounts = new ArrayList<String>();
		try {
			PreparedStatement ps = connection.prepareStatement(
					queries.get((getClash == DiscordToClashDF.GET_CLASH) ? "getClashByDiscord" : "getDiscordByClash"));
			ps.setString(1, type);
			ps.setString(2, clashOrDiscordID);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			if (rs.first())
				while (!rs.isAfterLast()) {
					accounts.add(rs.getString(1));
					rs.next();
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return accounts;
	}
	
	private boolean addOrRemoveDiscordClashRelationship(String discordUser, String clashId, String type, boolean add) {
		try {
			PreparedStatement ps = connection.prepareStatement(
					queries.get((add == DiscordToClashDF.ADD ? "add" : "remove") + "DiscordClashRelationship"));
			ps.setString(1, discordUser);
			ps.setString(2, clashId);
			ps.setString(3, type);
			int row = ps.executeUpdate();
			return row != 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private List<Pair<String, String>> getPairsForType(String type) {
		List<Pair<String,String>> pairs = new ArrayList<Pair<String,String>>();
		try {
			PreparedStatement ps = connection.prepareStatement(
					queries.get("getIdPairsByType"));
			ps.setString(1, type);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			if (rs.first())
				while (!rs.isAfterLast()) {
					pairs.add(new Pair<String,String>(rs.getString(1),rs.getString(2)));
					rs.next();
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pairs;
	}
	
	private boolean recordExists(String discordID, String clashID, String type) {
		try {
			PreparedStatement ps = connection.prepareStatement(
					queries.get("checkIfRelationshipExists"));
			ps.setString(1, discordID);
			ps.setString(2, clashID);
			ps.setString(3, type);
			ps.execute();
			return ps.getResultSet().first();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean serverRelationshipExists(String discordID, String clashID) {
		return recordExists(discordID, clashID, DiscordToClashDF.SERVER);
	}
	
	public boolean subscriberRelationshipExists(String discordID, String clashID) {
		return recordExists(discordID, clashID, DiscordToClashDF.SUBSCRIBER);
	}
	
	public boolean ownerRelationshipExists(String discordID, String clashID) {
		return recordExists(discordID, clashID, DiscordToClashDF.OWNER);
	}
	
	public List<String> getSubscriptionsByDiscordUser(String discordUser) {
		return this.getClashOrDiscord(discordUser, DiscordToClashDF.SUBSCRIBER, DiscordToClashDF.GET_CLASH);
	}
	
	public List<String> getOwnedAccountsByDiscordUser(String discordUser) {
		return this.getClashOrDiscord(discordUser, DiscordToClashDF.OWNER, DiscordToClashDF.GET_CLASH);
	}
	
	public List<String> getClansByDiscordServer(String discordServer) {
		return this.getClashOrDiscord(discordServer, DiscordToClashDF.SERVER, DiscordToClashDF.GET_CLASH);
	}
	
	public List<String> getSubscribersForClashID(String clashID) {
		return this.getClashOrDiscord(clashID, DiscordToClashDF.SUBSCRIBER, DiscordToClashDF.GET_DISCORD);
	}
	
	public List<String> getOwnersForClashID(String clashID) {
		return this.getClashOrDiscord(clashID, DiscordToClashDF.OWNER, DiscordToClashDF.GET_DISCORD);
	}
	
	public boolean addSubscriptionToDiscordUser(String discordUser, String clashId) {
		return this.addOrRemoveDiscordClashRelationship(discordUser, clashId, DiscordToClashDF.SUBSCRIBER, DiscordToClashDF.ADD);
	}
	
	public boolean removeSubscriptionFromDiscordUser(String discordUser, String clashId) {
		return this.addOrRemoveDiscordClashRelationship(discordUser, clashId, DiscordToClashDF.SUBSCRIBER, DiscordToClashDF.REMOVE);
	}
	
	public boolean addOwnedAccountToDiscordUser(String discordUser, String clashId) {
		return this.addOrRemoveDiscordClashRelationship(discordUser, clashId, DiscordToClashDF.OWNER, DiscordToClashDF.ADD);
	}
	
	public boolean removeOwnedAccountFromDiscordUser(String discordUser, String clashId) {
		return this.addOrRemoveDiscordClashRelationship(discordUser, clashId, DiscordToClashDF.OWNER, DiscordToClashDF.REMOVE);
	}
	
	public boolean AddClanToDiscordServer(String discordServer, String clan) {
		return this.addOrRemoveDiscordClashRelationship(discordServer, clan, DiscordToClashDF.SERVER, DiscordToClashDF.ADD);
	}
	
	public boolean removeClanFromDiscordServer(String discordServer, String clan) {
		return this.addOrRemoveDiscordClashRelationship(discordServer, clan, DiscordToClashDF.SERVER, DiscordToClashDF.REMOVE);
	}
	
	public List<Pair<String, String>> getDiscordClashServerPairs() {
		return getPairsForType("V");
	}
}
