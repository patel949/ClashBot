package lib;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {
	
	private static String TOKEN = "NDAzMzg0MTUyMDI1NDY0ODMy.WmAOxA.eqNwV3HMt7tNWkK55q-0wH4ChWk";//"NDczNTkyNDM2NTUyMjM3MDkx.W195Uw.HyllAo3tYYBC5DacDqsieY86Eik";
	
	public static void main(String[] args) {
		try {
			JDA jda = JDABuilder.createDefault(TOKEN)
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
}
