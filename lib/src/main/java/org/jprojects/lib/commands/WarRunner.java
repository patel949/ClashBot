package org.jprojects.lib.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class WarRunner implements Runnable {
	public static HashMap<Long, List<WarRunner>> warRunners = new HashMap<Long, List<WarRunner>>();
	
	String[] msgs;
	List<User> users;
	int ctr;
	MessageChannel channel;
	boolean shouldRun = true;
	Long guildID;
	
	public static List<WarRunner> getRunnersForServer(Long guild) {
		synchronized (warRunners) {
			return warRunners.getOrDefault(guild, new ArrayList<WarRunner>());
		}
	}
	
	public WarRunner(Long guild, MessageChannel channel, String[] messages, List<User> userList) {
		msgs = messages;
		ctr = 0;
		this.channel = channel;
		guildID = guild;
		
		users = new ArrayList<User>();
		users.addAll(userList);
		users.addAll(userList); //two attacks each.
		
		//update the list of warRunners.
		synchronized (warRunners) {
			List<WarRunner> guildRunners = warRunners.getOrDefault(guild, new ArrayList<WarRunner>());
			guildRunners.add(this);
			warRunners.put(guild, guildRunners);
		}
	}
	
	public boolean attack(User user) {
		synchronized (warRunners) {
			return users.remove(user);
		}
	}
	
	@Override
	public void run() {
		synchronized (warRunners) {
			//If we run into an invalid configuration at any point, remove from list of warRunners.
			
			//if message send is in some way invalid, don't send anything.
			if (msgs.length == 0 || !shouldRun || channel == null) {
				//invalid configuration
				warRunners.getOrDefault(guildID, new ArrayList<WarRunner>()).remove(this);
				return;
			}
			
			//if we've gone through all the messages, we're done.
			if (ctr > msgs.length) {
				warRunners.getOrDefault(guildID, new ArrayList<WarRunner>()).remove(this);
				return;
			}
			
			//build message
			StringBuilder sb = new StringBuilder();
			Set<User> yetToAttack = new HashSet<User>(users);
			for (User user : yetToAttack)
					sb.append(user.getAsMention() + " ");
			sb.append(msgs[ctr++]);
			
			//send message
			channel.sendMessage(sb.toString()).queue();
		}
	}
	
}
