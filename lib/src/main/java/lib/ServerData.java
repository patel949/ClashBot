package lib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
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
//TODO: FOR LARGER USE: Optimize memory usage!
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
	
	private ServerData(String filepath) {
		this.filepath = filepath;
	}

	public static ServerData getServer(String serverID) {
		String path = "servers\\" + serverID + ".dat";
		File f = new File(Paths.get(path).toAbsolutePath().toString());
		if (!f.exists()) {
			ServerData newServer = new ServerData(path);
			newServer.write();
			return newServer;
		}
		return read(path);
	}

	public boolean setDefaultChannel(long channelID) {
		defaultChannel = channelID;
		return write();
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
	
	public String getPrefix() {
		return prefix;
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
		return clans.size() == 0;
	}
	
	
}
