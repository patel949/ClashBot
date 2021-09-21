package commands;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lib.ServerData;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class War extends Command {
	
	private final static int FIRST_REMINDER_DELTA = 90 * 60; //90 minutes
	private final static int SECOND_REMINDER_DELTA = 30 * 60; //30 minutes
	
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
		if (command.length < 2) {
			e.getChannel().sendMessage("To see help for the WAR command, use \"war help\"").queue();
			return;
		}
		
		/*
		 * WAR REMIND
		 * $ WAR REMIND <**h> <**m> <**s> <@user1> ... <@userN>
		 * 		Remind a list of users before the end of the war. Time specified is not the remind time
		 * 		but rather one and a half hours and half an hour before the remind time. 
		 * 		Time specified should be time until the official end of the war. 
		 */
		if (command[1].equalsIgnoreCase("remind")) {
			//1. You need to be at least a co-leader to set up war reminds.
			if (!Command.permissable(e.getMessage().getMember(), userPerms.COLEADER)) {
				e.getChannel().sendMessage("Sorry chief, you don't have permission to do that.").queue();
				return;
			}
			
			//2. We need at least one and at most three units of time. units of time should be 2-3 characters long
			//		specifically, one to two numbers followed by a letter.
			if (command.length < 3) {
				e.getChannel().sendMessage("Hey! you need to properly use the command - specify an amount of time and the users to remind.").queue();
				return;
			}
			if (!command[2].matches("^\\d(\\d?)[hmsHMS]$")) {
				e.getChannel().sendMessage("Usage: WAR REMIND <**h> <**m> <**s> <@user1> ... <@userN>").queue();
				return;
			}
			int timeSecs = 0;
			int currentTime = Integer.parseInt(command[2].substring(0, command[2].length()-1));
			char type = command[2].charAt(command[2].length()-1);
			if (type != 'S' && type != 's')
				currentTime *= 60;
			if (type == 'H' || type == 'h')
				currentTime *= 60;
			timeSecs = currentTime;
			
			if (command.length > 3 && command[3].matches("^\\d(\\d?)[hmsHMS]$")) {
				currentTime = Integer.parseInt(command[3].substring(0, command[3].length()-1));
				type = command[3].charAt(command[3].length()-1);
				if (type != 'S' && type != 's')
					currentTime *= 60;
				if (type == 'H' || type == 'h')
					currentTime *= 60;
				timeSecs += currentTime;
			}
			if (command.length > 4 && command[4].matches("^\\d(\\d?)[hmsHMS]$")) {
				currentTime = Integer.parseInt(command[4].substring(0, command[4].length()-1));
				type = command[4].charAt(command[4].length()-1);
				if (type != 'S' && type != 's')
					currentTime *= 60;
				if (type == 'H' || type == 'h')
					currentTime *= 60;
				timeSecs += currentTime;
			}
			
			//3. get the list of mentioned players - there should be at least one.
			if (e.getMessage().getMentionedUsers().size() == 0) {
				e.getChannel().sendMessage("You need to include a list of users to ping in the reminder!").queue();
				return;
			}
			
			//4. craft a message.
			ServerData sd = ServerData.getServer(e.getGuild().getIdLong());
			
			MessageChannel mc = null;
			if (sd.getDefaultChannel() == 0) {
				e.getChannel().sendMessage("You haven't set up a default channel, so I'll be using this one.").queue();
				mc = e.getChannel();
			} else {
				mc = e.getGuild().getTextChannelById(sd.getDefaultChannel());
			}			
			
			//5. Schedule the message.
			WarRunner runner = new WarRunner(e.getGuild().getIdLong(), mc, new String[] {sd.getWarReminder(1), sd.getWarReminder(2)},e.getMessage().getMentionedUsers());
			ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
			ses.schedule(runner, Math.max(timeSecs-FIRST_REMINDER_DELTA, 1), TimeUnit.SECONDS);
			ses.schedule(runner, Math.max(timeSecs-SECOND_REMINDER_DELTA,2), TimeUnit.SECONDS);
			
			e.getChannel().sendMessage("Great! I'll remind everyone a couple times before the end of the war!").queue();
			return;
			
		/*
		 * WAR ATTACK
		 * $ WAR ATTACK
		 * Signal that you used an attack in war. if you use two attacks, you don't need to 
		 */
		} else if (command[1].equalsIgnoreCase("Attack")) {
			
			List<WarRunner> clanWars = WarRunner.getRunnersForServer(e.getGuild().getIdLong());
			int ctr = 0;
			for (WarRunner wr : clanWars)
				if (wr.attack(e.getAuthor()))
					ctr++;
			
			e.getChannel().sendMessage("Found you in " + ctr + " clan" + (ctr == 0 ? "s" : (ctr == 1 ? " and noted that you used an attack." : "s, and marked each one with one attack."))).queue();
			return;
			
		} else if (command[1].equalsIgnoreCase("help")) {
			getHelp(e.getChannel());
		} else {
			e.getChannel().sendMessage("Didn't understand that. Try using 'war help'").queue();
		}
	}
	
	@Override
	public void getHelp(MessageChannel response) {
		response.sendMessage("Remind: set a new war-end time to have players reminded 30 and 90 minutes before the end.\n"
				+ "Attack: use one of two attacks. Once both attacks are used, you will not be reminded for this war.\n").queue();
	}
}