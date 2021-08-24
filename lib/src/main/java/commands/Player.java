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
			//For each player to be promoted:
			for (Member player : players) {
				//if member is self, say "you can't promote yourself." and move on.
				
				//Check the players's current rank
				
				//Check the requester's rank
				
				//if the requester has a higher rank than the player, promotion is okay.
				
				//If promotion is to leader, demote self.
				
				//otherwise, report that their rank is not high enough.
				
			}
		}
		
		else if (command[1].equalsIgnoreCase("Demote")) {
			List<Member> players = e.getMessage().getMentionedMembers();
			//For each player to be demoted:
			for (Member player : players) {
				//if member is self, say "you can't demote yourself." and move on.
				
				//Check the players's current rank
				
				//Check the requester's rank
				
				//if the requester has a higher rank than the player, demotion is okay.
				
				//else, report that their rank is not high enough.
				
			}
		}
	}
}
