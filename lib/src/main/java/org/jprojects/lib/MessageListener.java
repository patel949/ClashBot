package org.jprojects.lib;

//import java.util.Arrays;
import java.util.HashMap;

import org.jprojects.lib.commands.Command;

/*
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
*/
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


//We will use the singleton pattern here for now.
//I don't foresee a reason to have multiple messageListeners. 
//One may help with synchronization if it becomes necessary.
public class MessageListener extends ListenerAdapter{
	
	private HashMap<String, Command> commandMap = new HashMap<String, Command>();
	private static MessageListener instance = null;
	
	
	public static MessageListener getMessageListener() {
		if (instance == null)
			instance = new MessageListener();
		return instance;
	}
	
	private MessageListener() {
		System.out.println("Initializing ClanManager...");
		
		//Add and adjust commands from one central place, so when we get new addons, we only need to 
		//change one file. Hopefully eventually 0 files!
		Command.fillCommandTable(commandMap);

	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		//When we receive a message, we need to check to see if it starts with our prefix.
		if (event.getMessage() != null) {
			String message = event.getMessage().getContentDisplay().toLowerCase();
			String prefix = ServerData.getServer(event.getGuild().getIdLong()).getPrefix();
			if (message.startsWith(prefix)) {
				String[] words = message.substring(prefix.length()).split(" ");
				String command = words[0];
				if (command.equals("") && words.length > 1) {
					command = words[1]; //we will allow a space.
					String[] temp = new String[words.length-1];
					System.arraycopy(words, 1, temp, 0, temp.length);
					words = temp;
				}
				if (commandMap.containsKey(command))
					commandMap.get(command).execute(event,words);
				else
					unknownCommand(event);
			}
		}
			
	}
	
	public void unknownCommand(MessageReceivedEvent event) {
		event.getChannel().sendMessage("I didn't recongize that command.").queue();
		System.err.println("Unrecognized command: \"" + event.getMessage().getContentDisplay() + "\"");
	}
}
