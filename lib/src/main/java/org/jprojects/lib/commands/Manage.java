package org.jprojects.lib.commands;


import java.util.ArrayList;
import java.util.List;

import org.jprojects.lib.database.ServerDataDF;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Manage extends Command {
	
	private Manage() {
	}
	
	private static Manage instance = null;
	public static Manage getInstance() {
		if (instance == null)
			instance = new Manage();
		return instance;
	}	

	//get a list of "hooks"
	@Override
	public String[] getHooks() {
		String[] hooks = {"manage"};
		return hooks;
	}
	
	@Override
	public void execute(MessageReceivedEvent e, String[] command) {
		if (!Command.permissable(e.getMessage().getMember(), userPerms.ADMIN)) {
			e.getChannel().sendMessage("Sorry chief, you don't have permission to use this command.").queue();
			return;
		}
		if (command.length < 2) {
			e.getChannel().sendMessage("uh.. what did you want to manage?").queue();
			return;
		}
		
		if (command[1].equalsIgnoreCase("setChannel")) {
			List<TextChannel> list = e.getMessage().getMentionedChannels();
			if (list.size() < 1) {
				list = new ArrayList<TextChannel>();
				list.add(e.getTextChannel());
			}
			e.getChannel().sendMessage("Okay! I'll set " + list.get(0).getAsMention() + " to be my default.").queue();
			ServerDataDF sd = ServerDataDF.getServer(e.getGuild().getId());
			sd.setDefaultChannel(list.get(0).getId());
		
		} else if (command[1].equalsIgnoreCase("setPrefix")) {
			if (command.length < 3) {
				e.getChannel().sendMessage("You've got to specify a prefix.").queue();
				return;
			}
			ServerDataDF sd = ServerDataDF.getServer(e.getGuild().getId());
			for (int i = 3; i < command.length; i++)
				command[2] += " " + command[i]; //If I thought this was going to be a common command I might consider a stringbuilder.
			sd.setPrefix(command[2]);
			e.getChannel().sendMessage("Okay, consider it done!").queue();
			
		} else if (command[1].equalsIgnoreCase("help")) {
			getHelp(e.getChannel());
		} else {
			e.getChannel().sendMessage("Didn't understand that. Try using 'manage help'").queue();
		}
	}
	
	@Override
	public void getHelp(MessageChannel response) {
		response.sendMessage("setChannel: set the default channel.\n"
				+ "setPrefix: set the default prefix.").queue();
	}
}
