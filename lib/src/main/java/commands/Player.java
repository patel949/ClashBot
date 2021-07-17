package commands;

import java.util.List;

import commands.Command.userPerms;
import lib.ServerData;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Player extends Command {
	private Player() {
	}
	
	private static Player instance = null;
	public static Player getInstance() {
		if (instance == null)
			instance = new Player();
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
		
		if (command.length < 2) {
			e.getChannel().sendMessage("uh.. what did you want to do here?").queue();
			return;
		}
		
		if (command[1].equalsIgnoreCase("Promote")) {
			List<Member> players = e.getMessage().getMentionedMembers();
			for (Member player : players) {
				//Check the current 
			}
		}
		
		else if (command[1].equalsIgnoreCase("Demote")) {
			List<Member> players = e.getMessage().getMentionedMembers();
		}
	}
}
