package org.jprojects.scapi;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import Core.Enitiy.clan.ClanModel;
import Core.Enitiy.player.Player;
import Core.exception.ClashAPIException;

public class ScapiClanAF {
	private ScapiClanAF() {
	}
	
	private static ScapiClanAF instance = new ScapiClanAF();
	public static ScapiClanAF getInstance() {			
		return instance;
	}	
	
	//Get player name or NULL if player does not exist
	public String getClanNameFromTag(String clanTag)  {
		//quick fail for simple cases:
		if( clanTag.length() < 2 
				||  clanTag.charAt(0) != '#')
			return null;
		try {
			CompletableFuture<ClanModel> future = JClashManager.getJClash().getClan(clanTag);
			ClanModel clan = future.get();
			return clan.getName();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClashAPIException e) {
			// TODO Auto-generated catch block
			if (e instanceof Core.exception.NotFoundException)
				return null; //don't bother printing stack trace, this will happen.
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//close enough for now.
	public boolean clanExists(String playerTag) {
		return getClanNameFromTag(playerTag) != null;
	}
}
