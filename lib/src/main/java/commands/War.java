package commands;

import java.util.List;

import commands.Command.userPerms;
import lib.ServerData;
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
		if (!Command.permissable(e.getMessage().getMember(), userPerms.COLEADER)) {
			e.getChannel().sendMessage("Sorry chief, you don't have permission to use this command.").queue();
			return;
		}
		if (command.length < 2) {
			e.getChannel().sendMessage("To see help for the WAR command, use \"war help\"").queue();
			return;
		}
		
		if (command[1].equalsIgnoreCase("Start")) {
			//1. Do you have permission to start a war?
			if (!Command.permissable(e.getMessage().getMember(), userPerms.COLEADER)) {
				e.getChannel().sendMessage("Sorry chief, you don't have permission to do that.").queue();
				return;
			}
			
			//2. We are going to need the Server's Data at this point.
			ServerData sd = ServerData.getServer("" + e.getGuild().getIdLong());
			
			//3. If there are currently NO clans, we can't start a war.
			if (sd.getNumClans() == 0) {
				e.getChannel().sendMessage("This Discord hasn't set up any affiliated clans! try \"manage addClan\"").queue();
				return;
			}
			
			//4. If there are multiple clans, and no clan is specified, we cannot proceed.
			if (command.length < 3 && sd.getNumClans() > 1) {
				e.getChannel().sendMessage("You must specify a clan.");
				return;
			}
			
			String clanName;
			if (command.length < 3)
				clanName = sd.getClans().get(0);
			else if (command[2].equals("-c"))
				clanName = command[3];
			else
				clanName = command[2];
			
			//5. Is the provided clan name valid?
			if (!sd.clanExists(clanName)) {
				e.getChannel().sendMessage("Couldn't find clan \"" + clanName + "\" - check your spelling?");
				return;
			}
			
			//6. Has a war already been started?
			if (sd.isWarring(clanName)) {
				e.getChannel().sendMessage("Oops! There's already a war going on!").queue();
				return;
			}
			
			//7. At this point, I see no reason not to start a war.
			sd.startWar(clanName);
			e.getChannel().sendMessage("Great! A war has been declared! I will remind everyone that hasn't attacked at the 2.5 hour and 1 hour marks.").queue();
			return;
			
			
		} else if (command[1].equalsIgnoreCase("AdjustTime")) {
		//Add or remove time until end of war (adjusts time of 1h and 2.5h remaining timers)
		
		} else if (command[1].equalsIgnoreCase("Add")) {
		//Add a player account to the war
		
		} else if (command[1].equalsIgnoreCase("Remove")) {
		//Remove a player account from the war
		
		} else if (command[1].equalsIgnoreCase("EndIn")) {
		//Override the end time of the war to be the specified amount of time away
		
		} else if (command[1].equalsIgnoreCase("Attack")) {
		//Mark one of two attacks as used for the calling player
		
		} else if (command[1].equalsIgnoreCase("Remind")) {
		//Remind everyone in war to attack with a custom message
		
		} else if (command[1].equalsIgnoreCase("SetMode")) {
		//Set mode of current war to be CWL (8 days) or regular (2 days, default)
		
		} else if (command[1].equalsIgnoreCase("OptIn")) {
		//Be included in war by default
		
		} else if (command[1].equalsIgnoreCase("OptOut")) {
		//Be excluded in war by default
		
		} else if (command[1].equalsIgnoreCase("Pardon")) {
		//use by co and higher to remove one attack from a user
		
		} else if (command[1].equalsIgnoreCase("help")) {
			getHelp(e.getChannel());
		} else {
			e.getChannel().sendMessage("Didn't understand that. Try using 'war help'").queue();
		}
	}
	
	@Override
	public void getHelp(MessageChannel response) {
		response.sendMessage("setChannel: set the default channel.\n"
				+ "setPrefix: set the default prefix.\n"
				+ "addClan: Add another clan that uses the same discord.\n"
				+ "removeClan: remove an inactive or abandoned clan.").queue();
	}
}