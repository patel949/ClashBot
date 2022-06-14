package org.jprojects.lib.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jprojects.lib.Main;
import org.jprojects.lib.constants.BOTConstants;
import org.jprojects.lib.database.DiscordToClashDF;
import org.jprojects.lib.database.ServerDataDF;
import org.jprojects.scapi.ScapiClanAF;
import org.jprojects.scapi.ScapiPlayerAF;
import org.jprojects.scapi.ScapiWarAF;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class War extends Command {
	
	
	private War() {
	}
	
	private static War instance = null;
	public static War getInstance() {
		if (instance == null)
			instance = new War();
		return instance;
	}	

	//get a list of "hooks"
	@Override
	public String[] getHooks() {
		String[] hooks = {"war"};
		return hooks;
	}
	
	@Override
	public void execute(MessageReceivedEvent e, String[] command) {
		if (command.length < 4) {
			e.getChannel().sendMessage("To see help for the WAR command, use \"war help\"").queue();
			return;
		}
		
		/*
		 * WAR REMIND
		 * $ WAR REMIND <ALL/Clan Tag> <MESSAGE>
		 * 		IF there is a war currently going on, remind users in the war who have not attacked to attack.
		 */
		if (command[1].equalsIgnoreCase("remind")) {
			//1. You need to be at least a co-leader to set up war reminds.
			if (!Command.permissable(e.getMessage().getMember(), userPerms.COLEADER)) {
				e.getChannel().sendMessage("Sorry chief, you don't have permission to do that.").queue();
				return;
			}
			
			//Which clans are we contacting?
			List<String> clanIds = new ArrayList<String>();
			if (command[2].equalsIgnoreCase("ALL")) {
				//Add all clans by discord ID
				clanIds.addAll(DiscordToClashDF.getDiscordtoClashDF().getClansByDiscordServer(e.getGuild().getId()));
			} else {
				//Check if clan is subscribed to:
				if( DiscordToClashDF.getDiscordtoClashDF().recordExists(e.getGuild().getId(), null, command[2], null) == BOTConstants.SQL_OK)
					clanIds.add(command[2]);
				else {
					e.getChannel().sendMessage("It looks like you entered an invalid clan.").queue();
					return;
				}					
			}
			
			StringBuilder msg = new StringBuilder();
			for (int i = 3; i < command.length; i++)
				msg.append(command[i] + " ");
			
			for (String clanID : clanIds) {
				//3. Get the users that have subscriptions or ownerships that are also currently in the Discord.
				List<String> clashUserIds = ScapiWarAF.getInstance().hasNotAttacked(clanID);
				
				//fish out the guild
				Guild guild = e.getGuild();
				
				//convert clashers to userIds
				Set<String> discordUserIds = new HashSet<String>();
				for (String clashUser : clashUserIds) {
					discordUserIds.addAll(DiscordToClashDF.getDiscordtoClashDF().getUsersSubscribedToClashAccountOnServer(clashUser, guild.getId()));
				}
				
				//convert IDs to members
				Set<Member> members = new HashSet<Member>();
				for (String discordUser : discordUserIds) {
					Member newMember;
					newMember = guild.retrieveMemberById(discordUser).complete();
					if (newMember != null)
						members.add(newMember);
				}
				
				TextChannel c = guild.getTextChannelById(ServerDataDF.getServer(guild.getId()).getDefaultChannel());
				
				if (c == null)
					c = guild.getDefaultChannel();
				
				
				//send an individual ping to each user
				for (Member pingMe : members) {
					boolean plural = false;
					StringBuilder sb = new StringBuilder();
					for (String clashUser : clashUserIds) {
						if (DiscordToClashDF.getDiscordtoClashDF().recordExists(pingMe.getGuild().getId(), pingMe.getUser().getId(), clashUser, null) == BOTConstants.SQL_OK || DiscordToClashDF.getDiscordtoClashDF().recordExists(pingMe.getGuild().getId(), pingMe.getUser().getId(), clashUser, "O") == BOTConstants.SQL_OK) {
							if (sb.length() > 0) {
								plural = true;
								sb.append(", ");
							}
							sb.append(ScapiPlayerAF.getInstance().getPlayerNameFromTag(clashUser));
						}
							
					}
					c.sendMessage(pingMe.getAsMention() + " Reminder for account" + (plural ? "s " : " ") + sb.toString() + ": " + msg).queue();
				}
			}
			
		} else if (command[1].equalsIgnoreCase("help")) {
			getHelp(e.getChannel());
		} else {
			e.getChannel().sendMessage("Didn't understand that. Try using 'war help'").queue();
		}
	}
	
	@Override
	public void getHelp(MessageChannel response) {
		response.sendMessage("Remind: send out a message reminder to everyone who hasn't attacked in a subscribed clan.\n"
				+" remind <ALL/#CLANTAG> <message .... >/n").queue();
	}
}