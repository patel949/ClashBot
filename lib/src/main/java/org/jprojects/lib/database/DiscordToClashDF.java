package org.jprojects.lib.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jprojects.lib.constants.BOTConstants;
import org.jprojects.lib.util.Pair;

public class DiscordToClashDF {
	
	private static DiscordToClashDF discordToClashDF = new DiscordToClashDF();
	
	private Map<String, String> queries;
	
	
	private static final boolean ADD = true;
	private static final boolean REMOVE = !ADD;
	private static final boolean GET_CLASH = true;
	private static final boolean GET_DISCORD = !GET_CLASH;
	
	
	private static final String TABLE_NAME = "iden001_discord_to_clash";
	
	private DiscordToClashDF() {
		queries = new HashMap<String,String>();
		queries.put("getIdPairsByType", 
				"SELECT IDEN001_DISCORD_ID, IDEN001_CLASH_ID FROM " + BOTConstants.IDENTITY_SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " WHERE IDEN001_TYPE='" + BOTConstants.SERVER_RELATION + "'");
		queries.put("getUsersSubscriptionsOnServer", 
				"SELECT IDEN001_CLASH_ID FROM " + BOTConstants.IDENTITY_SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " WHERE IDEN001_TYPE='" + BOTConstants.SUBSCRIBER_RELATION + "' AND IDEN001_DISCORD_USR_ID=? AND IDEN001_DISCORD_ID=?");
		queries.put("getUsersSubscriptionsOwnsOnServer", 
				"SELECT IDEN001_DISCORD_USR_ID FROM " + BOTConstants.IDENTITY_SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " WHERE IDEN001_TYPE=? AND IDEN001_CLASH_ID=? AND IDEN001_DISCORD_ID=?");
		queries.put("getClashByDiscord", 
				"SELECT IDEN001_CLASH_ID FROM " + BOTConstants.IDENTITY_SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " WHERE IDEN001_TYPE='"+BOTConstants.SERVER_RELATION+"' AND IDEN001_DISCORD_ID=?");
		queries.put("getDiscordByClash", 
				"SELECT IDEN001_DISCORD_ID FROM " + BOTConstants.IDENTITY_SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " WHERE IDEN001_TYPE='"+BOTConstants.SERVER_RELATION+"' AND IDEN001_CLASH_ID=?");
		queries.put("getMonitoredClans",
				"SELECT IDEN001_CLASH_ID FROM " + BOTConstants.IDENTITY_SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " where IDEN001_TYPE=" + BOTConstants.SERVER_RELATION);
		queries.put("checkIfRecordExists",
				"SELECT * FROM " + BOTConstants.IDENTITY_SCHEMA + "." + DiscordToClashDF.TABLE_NAME + " WHERE IDEN001_DISCORD_ID=? AND IDEN001_DISCORD_USR_ID=? AND IDEN001_CLASH_ID=? AND IDEN001_TYPE=?");
	
	}
	
	public List<Pair<String, String>> getDiscordClashServerPairs() {
		List<Pair<String,String>> pairs = new ArrayList<Pair<String,String>>();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			connection = DatabaseConnectionPool.getDatabaseConnectionPool().getConnection();
			ps = connection.prepareStatement(
					queries.get("getIdPairsByType"));
			ps.execute();
			rs = ps.getResultSet();
			if (rs.first())
				while (!rs.isAfterLast()) {
					pairs.add(new Pair<String,String>(rs.getString(1),rs.getString(2)));
					rs.next();
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return pairs;
	}
	
	public Set<String> getAllMonitoredClans() {
		Set<String> clans = new HashSet<String>();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			connection = DatabaseConnectionPool.getDatabaseConnectionPool().getConnection();
			ps = connection.prepareStatement(
					queries.get("getMonitoredClans"));
			ps.execute();
			rs = ps.getResultSet();
			if (rs.first())
				while (!rs.isAfterLast()) {
					clans.add(rs.getString(1));
					rs.next();
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return clans;
	}
	
	public List<String> getUsersOwnedAccounts(String userDiscordID) {
		return getUsersSubscriptionsOwnsOnServer(userDiscordID, "0",BOTConstants.OWNER_RELATION);
	}
	
	public List<String> getUsersSubscriptionsOnServer(String userDiscordID, String serverDiscordID) {
		return getUsersSubscriptionsOwnsOnServer(userDiscordID, serverDiscordID, BOTConstants.SUBSCRIBER_RELATION);
	}
	
	private List<String> getUsersSubscriptionsOwnsOnServer(String userDiscordID, String serverDiscordID, String type) {
		List<String> accounts = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = DatabaseConnectionPool.getDatabaseConnectionPool().getConnection();
			ps = connection.prepareStatement(
					queries.get("getUsersSubscriptionsOwnsOnServer"));
			ps.setString(1, type);
			ps.setString(2, userDiscordID);
			ps.setString(3, serverDiscordID);
			ps.execute();
			rs = ps.getResultSet();
			if (rs.first())
				while (!rs.isAfterLast()) {
					accounts.add(rs.getString(1));
					rs.next();
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return accounts;
	}
	
	public List<String> getUsersSubscribedToClashAccountOnServer(String clashID, String serverDiscordID) {
		List<String> accounts = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = DatabaseConnectionPool.getDatabaseConnectionPool().getConnection();
			ps = connection.prepareStatement(
					queries.get("getUsersSubscribedToClashAccountOnServer"));
			ps.setString(1, clashID);
			ps.setString(2, serverDiscordID);
			ps.execute();
			rs = ps.getResultSet();
			if (rs.first())
				while (!rs.isAfterLast()) {
					accounts.add(rs.getString(1));
					rs.next();
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return accounts;
	}
	
	private List<String> getClashOrDiscord(String clashOrDiscordID, boolean getClash) {
		List<String> accounts = new ArrayList<String>();
		PreparedStatement ps = null;
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DatabaseConnectionPool.getDatabaseConnectionPool().getConnection();
			ps = connection.prepareStatement(
					queries.get((getClash == DiscordToClashDF.GET_CLASH) ? "getClashByDiscord" : "getDiscordByClash"));
			ps.setString(1, clashOrDiscordID);
			ps.execute();
			rs = ps.getResultSet();
			if (rs.first())
				while (!rs.isAfterLast()) {
					accounts.add(rs.getString(1));
					rs.next();
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return accounts;
	}
	

	public List<String> getClansByDiscordServer(String discordServer) {
		return this.getClashOrDiscord(discordServer, DiscordToClashDF.GET_CLASH);
	}

	public List<String> getServersForClan(String clan) {
		return this.getClashOrDiscord(clan, DiscordToClashDF.GET_DISCORD);
	}
	
	private int addRemoveRecord(String discordServerID, String discordUserID, String clashID, String type, boolean add) {
		int returnCode = recordExists(discordServerID, discordUserID, clashID, type);
		if (returnCode == BOTConstants.SQL_OK)
			return BOTConstants.SQL_FAILED_RECORD_EXISTS;
		Connection connection = null;
		PreparedStatement ps = null;
		int rows = 0;
		try {
			connection = DatabaseConnectionPool.getDatabaseConnectionPool().getConnection();
			ps = connection.prepareStatement(
					queries.get((add == DiscordToClashDF.ADD ? "add" : "remove") + "DiscordClashRelationship"));
			ps.setString(1, discordServerID);
			ps.setString(2, discordUserID);
			ps.setString(3, clashID);
			ps.setString(4, type);
			rows = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return BOTConstants.SQL_FAILED_SQL_EXCEPTION;
			
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		//If we modified something, it was a success.
		if (rows > 0) return BOTConstants.SQL_OK;
		//If we didn't and it was an add, the record already exists.
		else if (add == DiscordToClashDF.ADD) return BOTConstants.SQL_FAILED_RECORD_EXISTS;
		//If we didn't and it was a remove, the record didn't exist.
		else return BOTConstants.SQL_FAILED_NOT_FOUND;
			
	}
	
	public int addClashServerToDiscordServer(String discordId, String clashId) {
		return addRemoveRecord(discordId, "0", clashId, BOTConstants.SERVER_RELATION, DiscordToClashDF.ADD);
	}
	
	public int removeClashServerFromDiscordServer(String discordId, String clashId) {
		return addRemoveRecord(discordId, "0", clashId, BOTConstants.SERVER_RELATION, DiscordToClashDF.REMOVE);
	}
	
	public int addSubscriberByDiscordServerAndUser(String server, String user, String clashId) {
		return addRemoveRecord(server, user, clashId, BOTConstants.SUBSCRIBER_RELATION, DiscordToClashDF.ADD);
	}
	
	public int removeSubscriberByDiscordServerAndUser(String server, String user, String clashId) {
		return addRemoveRecord(server, user, clashId, BOTConstants.SUBSCRIBER_RELATION, DiscordToClashDF.REMOVE);
	}
	
	//TODO add a check to make sure they actually own the account at some point
	public int addOwnerByDiscordServerAndUser(String server, String user, String clashId) {
		return addRemoveRecord(server, user, clashId, BOTConstants.OWNER_RELATION, DiscordToClashDF.ADD);
	}
	
	public int removeOwnerByDiscordServerAndUser(String server, String user, String clashId) {
		return addRemoveRecord(server, user, clashId, BOTConstants.OWNER_RELATION, DiscordToClashDF.REMOVE);
	}
	
	
	public int recordExists(String discordServerID, String discordUserID, String clashID, String type) {
		if (discordServerID == null)
			discordServerID = "0";
		if (discordUserID == null)
			discordUserID = "0";
		if (clashID == null)
			clashID = "0";
		if (type == null) {
			if (discordUserID.equals("0")) {
				type = "B";
			} else {
				type = "V";
			}
		}
		boolean found = false;
		PreparedStatement ps = null;
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = DatabaseConnectionPool.getDatabaseConnectionPool().getConnection();
			ps = connection.prepareStatement(
					queries.get("checkIfRecordExists"));
			ps.setString(1, discordServerID);
			ps.setString(2, discordUserID);
			ps.setString(3, clashID);
			ps.setString(4, type);
			ps.execute();
			rs = ps.getResultSet();
			found = rs.first();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return BOTConstants.SQL_FAILED_SQL_EXCEPTION;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		if (found) return BOTConstants.SQL_OK;
		
		else return BOTConstants.SQL_FAILED_NOT_FOUND;
	}

	public static DiscordToClashDF getDiscordtoClashDF() {
		// TODO Auto-generated method stub
		return DiscordToClashDF.discordToClashDF;
	}
}
