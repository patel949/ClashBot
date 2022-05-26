package org.jprojects.lib;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {
	
	private static JDA jda;
	private static String TOKEN = Token.getDiscordToken(); //Token hidden on git
	
	public static void main(String[] args) {
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
	}
	
	public static JDA getJDA() {
		return jda;
	}
}
