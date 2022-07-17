package org.jprojects.lib;

import java.util.Timer;

import javax.security.auth.login.LoginException;

import org.jprojects.lib.constants.BOTConstants;
import org.jprojects.lib.scheduled.WarChecker;
import org.jprojects.scapi.JClashManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {
	
	private static JDA jda;
	private static String TOKEN = Token.getDiscordToken(); //Token hidden on git
	
	public static void main(String[] args) {
		if (BOTConstants.DEBUG)
			System.out.println("/n**********/nStarting bot in DEVELOPMENT MODE/n**********");
		System.out.println("Using " + BOTConstants.IDENTITY_SCHEMA + " schema for DB Identity Queries.");
		
		//Start JDA
		try {
			jda = JDABuilder.createDefault(TOKEN)
					.addEventListeners(MessageListener.getMessageListener())
					.build();
			jda.awaitReady();
			System.out.println("Successfully built JDA");
		} catch (LoginException e) {
			System.out.println("Authentication failed:");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Check your Asynch Thread usage..");
			e.printStackTrace();
		}
		
		//Start Clash API
		JClashManager.getJClash();
		
		//Start WarChecker
		Timer timer = new Timer(true); //Daemon
		WarChecker warChecker = new WarChecker();
		timer.schedule(warChecker, 1000, 1000 * 60 * 60 * 8); //every 1 minute for now -> every 8 hours in prod.
		
	}
	
	public static JDA getJDA() {
		return jda;
	}
}
