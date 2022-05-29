package org.jprojects.lib.commands;


import java.util.ArrayList;
import java.util.List;

import org.jprojects.lib.commands.Command.userPerms;
import org.jprojects.lib.database.DiscordToClashDF;
import org.jprojects.scapi.ScapiClanAF;
import org.jprojects.scapi.ScapiPlayerAF;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Subscribe extends Command {
	
	private Subscribe() {
	}
	
	private static Subscribe instance = new Subscribe();
	public static Subscribe getInstance() {			
		return instance;
	}	
	
	//get a list of "hooks"
	@Override
	public String[] getHooks() {
		String[] hooks = {"subscribe"};
		return hooks;
	}
	
	@Override
	public void execute(MessageReceivedEvent e, String[] command) { 
		/*
		 * A few paths this can take:
		 * 
		 * 	- SUBSCRIBE <SELF> <ID> : no permissions needed.
		 *  - SUBSCRIBE <OTHER> <ID> : must be at least CO.
		 *  - SUBSCRIBE CLAN <ID> : must be at least CO.
		 */
		
		//Regardless, must have exactly 3 args:
		if (command.length < 3) {
			getHelp(e.getChannel());
			return;
		}
		
		if (command[1].equalsIgnoreCase("CLAN")) {
			executeForClan(e, command);
			return;
		}
		
		String clashName;
		if (e.getMessage().getMentionedUsers().size() != 1) {
			e.getChannel().sendMessage("Please list a single member to subscribe.").queue();
			return;
		}
		
		//if you are trying to subscribe someone else but are not at least coleader, don't.
		if ( (!e.getMessage().getMentionedUsers().get(0).equals(e.getAuthor())) && (!Command.permissable(e.getMessage().getMember(), userPerms.COLEADER)) ) {
			e.getChannel().sendMessage("You don't have permission to subscribe others.").queue();
			return;
		}
		
		String discordID = e.getMessage().getMentionedUsers().get(0).getId();
		String discordName = e.getMessage().getMentionedMembers().get(0).getEffectiveName();
		String clashID = command[command.length-1];

		if ((clashName = ScapiPlayerAF.getInstance().getPlayerNameFromTag(clashID) ) == null ) {
			e.getChannel().sendMessage("The clash player tag " + clashID + " appears to be invalid.").queue();
			return;
		}
		
		boolean success = DiscordToClashDF.getDiscordtoClashDF().addSubscriptionToDiscordUser(discordID, clashID);
		if (success)
			e.getChannel().sendMessage("Great! " + discordName + " is now subscribed to notifications for Clash player '" + clashName + "'").queue();
		else
			e.getChannel().sendMessage("Uh-oh, something went wrong, but I'm not quite sure what. If this happens again, contact the dev at dev@jprojects.org with the command you tried to use.").queue();
	}
	
	private void executeForClan(MessageReceivedEvent e, String[] command) {
		if ( !Command.permissable(e.getMessage().getMember(), userPerms.LEADER) )  {
			e.getChannel().sendMessage("You don't have permission to subscribe the Discord to a new clan.").queue();
			return;
		}
		
		String discordID = e.getGuild().getId();
		String clashID = command[command.length-1];
		String clashName = ScapiClanAF.getInstance().getClanNameFromTag(clashID);
		
		if (clashName == null) {
			e.getChannel().sendMessage("The clan tag " + clashID + " appears to be invalid.").queue();
			return;
		}
		
		//well, we have permission. we have a valid clan. we have a discord. log it.
		boolean success = DiscordToClashDF.getDiscordtoClashDF().AddClanToDiscordServer(discordID, clashID);
		if (success)
			e.getChannel().sendMessage("Great! This discord server is now subscribed to notifications for the clan '" + clashName + "'").queue();
		else
			e.getChannel().sendMessage("Uh-oh, something went wrong, but I'm not quite sure what. If this happens again, contact the dev at dev@jprojects.org with the command you tried to use.").queue();
	}
	
	@Override
	public void getHelp(MessageChannel response) {
		response.sendMessage("Usage: subscribe @<discord user> #<clash of clans player ID>\nOR\nsubscribe CLAN #<clan ID>").queue();
	}
}
