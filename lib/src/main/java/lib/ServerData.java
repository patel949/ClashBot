package lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;

//We will need some data structures for the bot to use, but they'll be
//on a server-by server basis. To begin setting up these datas, let's
//use simple data structures, for now.
//First, each server gets their own structure, but we don't want to have to
//load from disk on every call. that is slow. We don't have any meaningful
//memory constraints, because this is designed to be a small bot.
//TODO: FOR LARGER USE: Optimize memory usage/cache servers
public class ServerData implements Serializable {
	/**
	 * Auto-generated Serial Version.
	 */
	private static final long serialVersionUID = -5430559849286571516L;
	private static final Object lock = new Object(); //I don't need anything fancy.
	String prefix = "/";
	String filepath;
	long defaultChannel = 0;
	HashSet<String> clans = new HashSet<String>(); //keeps track of clans
	
	List<String> warMessages = new ArrayList<String>();
	
	
	//Keep track of user aliases.
	HashMap<String,List<Long>> alias_to_id = new HashMap<String,List<Long>>();
	HashMap<Long,List<String>> id_to_alias = new HashMap<Long,List<String>>();
	
	//TODO: Keep track of **aliases** in each clan
	
	private ServerData(String filepath) {
		this.filepath = filepath;
		
		//defaults
		warMessages.add("War is ending in an hour and a half. Please get an attack in!");
		warMessages.add("War is ending in half an hour. please finish all attacks!");
	}

	public static ServerData getServer(Long serverID) {
		String path = "servers\\" + serverID + ".dat";
		File f = new File(Paths.get(path).toAbsolutePath().toString());
		if (!f.exists()) {
			ServerData newServer = new ServerData(path);
			newServer.setDefaultChannel(Main.getJDA().getGuildById(serverID).getDefaultChannel().getIdLong());
			newServer.write();
			return newServer;
		}
		return read(path);
	}
	
	public String getWarReminder(int reminderNumber) {
		if (reminderNumber > warMessages.size() || reminderNumber <= 0)
			return "";
		return warMessages.get(reminderNumber-1);
	}
	
	public void setWarReminder(int reminderNumber, String reminder) {
		warMessages.set(reminderNumber-1, reminder);
	}
	
	private static ServerData read(String filepath) {
		synchronized (lock) {
			try (
				FileInputStream fileIn = new FileInputStream(filepath);
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				){
				
				ServerData data = (ServerData) objectIn.readObject();
				System.out.println("The server was successfully read.");
				return data;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("The server was not read.");
			return null;
		}
	}
	
	private boolean write() {
		synchronized (lock) {
			File directory = new File(Paths.get("servers\\").toAbsolutePath().toString());
		    if (! directory.exists()){
		        directory.mkdir();
		    }
			 try {
				 	
		            FileOutputStream fileOut = new FileOutputStream(this.filepath);
		            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
		            objectOut.writeObject(this);
		            objectOut.close();
		            System.out.println("The server was succesfully written to a file");
		            return true;
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
			 return false;
		}
	}
	
	public boolean setPrefix(String newPrefix) {
		//set prefix
		prefix = newPrefix;
		
		return write();
	}
	
	public boolean addAlias(Long id, String alias) {
		List<Long> idList = alias_to_id.getOrDefault(alias, new ArrayList<Long>());
		idList.add(id);
		alias_to_id.put(alias, idList);
		
		List<String> aliasList = id_to_alias.getOrDefault(id, new ArrayList<String>());
		aliasList.add(alias);
		id_to_alias.put(id, aliasList);
		
		return write();
	}
	
	public boolean removeAlias(Long id, String alias) {
		List<Long> idList = alias_to_id.getOrDefault(alias, new ArrayList<Long>());
		boolean removed = idList.remove(id);
		alias_to_id.put(alias, idList);
		
		List<String> aliasList = id_to_alias.getOrDefault(id, new ArrayList<String>());
		removed |= aliasList.remove(alias);
		id_to_alias.put(id, aliasList);
		
		return removed && write();
	}
	
	public List<String> getAlias(Long id) {
		return id_to_alias.get(id);
	}
	
	public List<Long> getMemberFromAlias(String alias) {
		return alias_to_id.get(alias);
	}
	
	public String getPrefix() {
		return prefix;
	}

	public boolean setDefaultChannel(long channelID) {
		defaultChannel = channelID;
		return write();
	}
	
	public long getDefaultChannel() {
		return defaultChannel;
	}
	
	public boolean addClan(String string) {
		boolean ret = clans.add(string);
		write();
		return ret;
	}

	public boolean removeClan(String string) {
		boolean ret = clans.remove(string);
		write();
		return ret;
	}
	
	public int getNumClans() {
		return clans.size();
	}
	
	/*
	 * Returns true if discord only has one clan.
	 */
	public boolean clansSingleton() {
		return clans.size() == 1;
	}
	
	public boolean isWarring(String clanName) {
		//TODO: build this function
		return false;
	}
	
	public List<String> getClans() {
		return new ArrayList<String>(clans);
	}
	
	public boolean clanExists(String clanName) {
		return clans.contains(clanName);
			
	}
	
	public void startWar(String clanName) {
		//TODO: build this function
	}
	
	public void shiftWarEnd(String clanName, long milliseconds) {
		//TODO: build this function
	}
	
	public String getClanFromPlayer(Long playerID, String playerName) {
		return null;
	}
	
	public boolean addToWar(Long user, String alias) {
		return true;
	}
	
	public boolean removeFromWar(Long user, String alias) {
		return true;
	}
	
	public boolean attackWar(Long user, String alias) {
		return false;
	}
	
	
}
