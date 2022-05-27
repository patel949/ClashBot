package org.jprojects.scapi;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import Core.Enitiy.player.Player;
import Core.exception.ClashAPIException;

public class ScapiPlayerAF {
	private ScapiPlayerAF() {
	}
	
	private static ScapiPlayerAF instance = new ScapiPlayerAF();
	public static ScapiPlayerAF getInstance() {			
		return instance;
	}	
	
	//Get player name or NULL if player does not exist
	public String getPlayerNameFromTag(String playerTag)  {
		//quick fail for simple cases:
		if( playerTag.length() < 2 
				||  playerTag.charAt(0) != '#')
			return null;
		try {
			CompletableFuture<Player> future = JClashManager.getJClash().getPlayer(playerTag);
			Player player = future.get();
			return player.getName();
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
	public boolean playerExists(String playerTag) {
		return getPlayerNameFromTag(playerTag) != null;
	}
}
