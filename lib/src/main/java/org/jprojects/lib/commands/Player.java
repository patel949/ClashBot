package org.jprojects.lib.commands;

import java.util.ArrayList;
import java.util.List;

import org.jprojects.lib.database.ServerDataDF;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
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
		String[] hooks = {"player"};
		return hooks;
	}
	
	@Override
	public void execute(MessageReceivedEvent e, String[] command) {
		
		if (command.length < 2) {
			e.getChannel().sendMessage("uh.. what did you want to do here?").queue();
			return;
		}
		
		if (command[1].equalsIgnoreCase("Promote")) {
			//Get caller's role
			userPerms promoterRole = Command.getHighestRole(e.getMember());
			boolean isAdmin = Command.isAdmin(e.getMember()); 
			
			//For each mentioned player, either promote or fail to promote.
			List<Member> players = e.getMessage().getMentionedMembers();
			if (players.size() == 0) {
				players = new ArrayList<Member>();
				players.add(e.getMember());
			}
			
			//For each player to be promoted:
			for (Member player : players) {
				//get player's current role
				userPerms p = Command.getHighestRole(player);
				
				//if caller's role is higher than player's role, promote:
				if (p.getRank() < promoterRole.getRank() || isAdmin) {
					Command.promoteMember(player);
					e.getChannel().sendMessage(player.getEffectiveName() + " was promoted, congrats!").queue();
				} else {
					e.getChannel().sendMessage(player.getEffectiveName() + " was not promoted - insufficient permission.").queue();
				}
			}
		}
		
		else if (command[1].equalsIgnoreCase("Demote")) {
			System.out.println("Demoting...");
			userPerms demoterRole = Command.getHighestRole(e.getMember());
			boolean isAdmin = Command.isAdmin(e.getMember());
			
			List<Member> players = e.getMessage().getMentionedMembers();
			if (players.size() == 0) {
				players = new ArrayList<Member>();
				players.add(e.getMember());
			}
			
			System.out.println("Total members for demotion: " + players.size());
			
			//For each player to be demoted:
			for (Member player : players) {
				//get player's current role
				userPerms p = Command.getHighestRole(player);
				
				//if caller's role is higher than player's role, demote:
				if (p.getRank() < demoterRole.getRank() || isAdmin) {
					Command.demoteMember(player);
					e.getChannel().sendMessage(player.getEffectiveName() + " was demoted.").queue();
				} else {
					e.getChannel().sendMessage(player.getEffectiveName() + " was not demoted - insufficient permission.").queue();
				}
				
			}
		}
		
		else if (command[1].equalsIgnoreCase("help")) {
			getHelp(e.getChannel());
		}
		
		else {
			e.getChannel().sendMessage("I didn't understand that. Try using \"player help\"").queue();
		}
	}
	
	@Override
	public void getHelp(MessageChannel response) {
		response.sendMessage("Promote: promotes the mentioned user(s).\n"
				+ "Demote: demotes the mentioned user(s).").queue();
	}
}
