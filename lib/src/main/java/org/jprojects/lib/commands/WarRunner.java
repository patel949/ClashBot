package org.jprojects.lib.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jprojects.lib.Main;
import org.jprojects.lib.database.DiscordToClashDF;
import org.jprojects.scapi.ScapiWarAF;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class WarRunner implements Runnable {
	public static HashMap<Long, List<WarRunner>> warRunners = new HashMap<Long, List<WarRunner>>();
	
	String[] msgs;
	int ctr;
	long channel = 0;
	boolean shouldRun = true;
	Long guildID;
	String clanID;
	
	public static List<WarRunner> getRunnersForServer(Long guild) {
		synchronized (warRunners) {
			return warRunners.getOrDefault(guild, new ArrayList<WarRunner>());
		}
	}
	
	public WarRunner(Long guild, String clan, long channel, String[] messages) {
		msgs = messages;
		ctr = 0;
		this.channel = channel;
		guildID = guild;
		clanID = clan;
		
		//update the list of warRunners.
		synchronized (warRunners) {
			List<WarRunner> guildRunners = warRunners.getOrDefault(guild, new ArrayList<WarRunner>());
			guildRunners.add(this);
			warRunners.put(guild, guildRunners);
		}
	}
	
	@Override
	public void run() {
		synchronized (warRunners) {
			
			//if we shouldn't run, don't
			if (ctr >= msgs.length || !shouldRun) {
				//invalid configuration
				warRunners.getOrDefault(guildID, new ArrayList<WarRunner>()).remove(this);
				return;
			}
			
			
			//Get all users that are discord members of <guildId> AND clanmembers of <clanId> AND have notifs enabled AND have not attacked.
			
			//clashers that have not attacked
			List<String> clashUserIds = ScapiWarAF.getInstance().hasNotAttacked(clanID);
			
			//fish out the guild
			Guild guild = Main.getJDA().getGuildById(guildID);
			guild.getMemberById(channel);
			guild.getDefaultChannel();
			
			//convert clashers to userIds
			Set<String> discordUserIds = new HashSet<String>();
			for (String clashUser : clashUserIds) {
				discordUserIds.addAll(DiscordToClashDF.getDiscordtoClashDF().getSubscribersForClashID(clashUser));
			}
			
			//convert IDs to members
			Set<Member> members = new HashSet<Member>();
			for (String discordUser : discordUserIds) {
				Member newMember = guild.getMemberById(discordUser);
				if (newMember != null)
					members.add(newMember);
			}
			
			TextChannel c = guild.getTextChannelById(channel);
			if (c == null)
				c = guild.getDefaultChannel();
			
			
			//send an individual ping to each user
			for (Member pingMe : members) {
				c.sendMessage(pingMe.getAsMention() + " " + msgs[ctr++]).queue();
			}
		}
	}
	
}
