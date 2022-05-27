package org.jprojects.scapi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;

import Core.JClash;
import Core.Enitiy.clan.ClanModel;
import Core.Enitiy.clanwar.ClanWarMember;
import Core.Enitiy.clanwar.WarInfo;
import Core.Enitiy.player.Player;
import Core.exception.ClashAPIException;
import org.jprojects.lib.Token;
import org.jprojects.lib.database.DiscordToClashDF;

//Base Facade to connect to 
public class ScapiWarAF {
	private ScapiWarAF() {
	}
	
	private static ScapiWarAF instance = new ScapiWarAF();
	public static ScapiWarAF getInstance() {			
		return instance;
	}
	
	/*
	public static void main(String[] args) {
		new ScapiWarBF().hasNotAttacked("#UYOPRJJR");
	}*/
	
	public void getWarInfo(String warTag) throws IOException, InterruptedException, ExecutionException {
		CompletableFuture<WarInfo> future = JClashManager.getJClash().getCurrentWar(warTag);
		WarInfo war = future.get();
		
	}
	
	public boolean isPublicWarlog(String clanTag) {
		try {
			CompletableFuture<ClanModel> future = JClashManager.getJClash().getClan(clanTag);
			ClanModel clan = future.get();
			return clan.isWarLogPublic();
		} catch (InterruptedException | ExecutionException | ClashAPIException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public String getWarEndTime(String clanTag) {
		try {
			CompletableFuture<WarInfo> future = JClashManager.getJClash().getCurrentWar(clanTag);
			WarInfo info = future.get();
			Date now = new Date();
			return info.getEndTime();
			
		} catch (IOException | InterruptedException | ExecutionException e) {
			System.out.println("Error getting current war for " + clanTag);
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isInWar(String clanTag) {
		try {
			CompletableFuture<WarInfo> future = JClashManager.getJClash().getCurrentWar(clanTag);
			WarInfo info = future.get();
			Date now = new Date();
			return (info.getPreparationStartTimeAsDate().before(now) && info.getEndTimeAsDate().after(now));
			
		} catch (IOException | InterruptedException | ExecutionException | ParseException e) {
			System.out.println("Error getting current war for " + clanTag);
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isWarDay(String clanTag) {
		try {
			CompletableFuture<WarInfo> future = JClashManager.getJClash().getCurrentWar(clanTag);
			WarInfo info = future.get();
			Date now = new Date();
			return (info.getStartTimeAsDate().before(now) && info.getEndTimeAsDate().after(now));
			
		} catch (IOException | InterruptedException | ExecutionException | ParseException e) {
			System.out.println("Error getting current war for " + clanTag);
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isPrepDay(String clanTag) {
		try {
			CompletableFuture<WarInfo> future = JClashManager.getJClash().getCurrentWar(clanTag);
			WarInfo info = future.get();
			Date now = new Date();
			return (info.getPreparationStartTimeAsDate().before(now) && info.getStartTimeAsDate().after(now));
			
		} catch (IOException | InterruptedException | ExecutionException | ParseException e) {
			System.out.println("Error getting current war for " + clanTag);
			e.printStackTrace();
			return false;
		}
	}
	
	public List<String> hasNotAttacked(String clanTag) {
		List<String> userIds = new ArrayList<String>();
		try {
			Date now = new Date(); //before we call the future.
			CompletableFuture<WarInfo> future = JClashManager.getJClash().getCurrentWar(clanTag);
			WarInfo info = future.get();
			List<ClanWarMember> potential = info.getClan().getWarMembers();
			//if before war day or user has not attacked:
			for (ClanWarMember c : potential) {
				int attacksPerformed = c.getAttacks() == null ? 0 : c.getAttacks().size();
				if (info.getStartTimeAsDate().before(now) || attacksPerformed < 2) {
					System.out.println(c.getName() + " has " + (2 - attacksPerformed) + " attacks left. ");
					userIds.add(c.getTag());
				} else {
					System.out.println(c.getName() + " used all attacks. ");
				}
			}
		} catch (InterruptedException | ExecutionException | ParseException | ClashAPIException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userIds;
	}
	
	public long getRemainingWarTimeSeconds(String clanTag) {
		try {
			CompletableFuture<WarInfo> future = JClashManager.getJClash().getCurrentWar(clanTag);
			WarInfo info = future.get();
			Date then = info.getEndTimeAsDate();
			Date now = new Date();
			long difference = then.getTime() - now.getTime();
			return difference / 1000;
			
		} catch (IOException | InterruptedException | ExecutionException | ParseException e) {
			System.out.println("Error getting current war for " + clanTag);
			e.printStackTrace();
			return -1;
		}
	}
}
