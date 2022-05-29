package org.jprojects.lib.scheduled;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jprojects.lib.commands.WarRunner;
import org.jprojects.lib.database.DiscordToClashDF;
import org.jprojects.lib.database.ServerDataDF;
import org.jprojects.lib.util.Pair;
import org.jprojects.scapi.ScapiWarAF;

import net.dv8tion.jda.api.entities.MessageChannel;

public class WarChecker extends TimerTask {
	private final static int FIRST_REMINDER_DELTA = 90 * 60; //90 minutes
	private final static int SECOND_REMINDER_DELTA = 30 * 60; //30 minutes
	
	//Map clash's clan ID to last known _end_ time of war.
	private static Map<String, String> scheduled = new ConcurrentHashMap<String, String>();
	
	//TODO find a way to get this to run for CWL
	public void run() {
		System.out.println("Checking for wars...");
		System.out.println((new Date()).toString());
		
		//Get list of subscriptions
		List<Pair<String, String>> pairs = DiscordToClashDF.getDiscordtoClashDF().getDiscordClashServerPairs();
		
		//change to set of clan IDs
		Set<String> clanIds = new HashSet<String>();
		for (Pair<String, String> pair : pairs) {
			clanIds.add(pair.getSecond());
		}
		
		//For each ID, 
		for (String clanId : clanIds) {
			System.out.println("Checking " + clanId + "... ");
			//check if there is an ongoing war.
			if (ScapiWarAF.getInstance().isInWar(clanId)) {
				//if in a war state, see if we know about the end date.
				String knownEndTime = scheduled.getOrDefault(clanId, "");
				if (ScapiWarAF.getInstance().getWarEndTime(clanId) == knownEndTime)
					continue;
				
				//if it's a new end date, we need to set some reminders.
				long timeSecs = ScapiWarAF.getInstance().getRemainingWarTimeSeconds(clanId);
				
				//get list of Discord Servers for clanId:
				Set<String> discordServers = new HashSet<String>();
				for (Pair<String, String> pair : pairs) {
					if (clanId.equals(pair.getSecond()))
						discordServers.add(pair.getFirst());
				}
				//4. craft a message.
				for (String server : discordServers) {
					ServerDataDF sd = ServerDataDF.getServer(server);
						
					
					//5. Schedule the message.
					System.out.println(" * Creating a WarRunner to run first in " + Math.max(timeSecs-FIRST_REMINDER_DELTA, 1) + " and then " + Math.max(timeSecs-SECOND_REMINDER_DELTA,2) + " seconds.");
					WarRunner runner = new WarRunner(Long.parseLong(server), clanId, sd.getDefaultChannel(), new String[] {sd.getWarReminder(1), sd.getWarReminder(2)});
					ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
					ses.schedule(runner, Math.max(timeSecs-FIRST_REMINDER_DELTA, 1), TimeUnit.SECONDS);
					ses.schedule(runner, Math.max(timeSecs-SECOND_REMINDER_DELTA,2), TimeUnit.SECONDS);
				}
			}
		}
		
		
		//If clan is in a war state and end time not in scheduled map,
		//schedule new runners.
		
		
	}
}
