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
			//First, get the level of the current player.
			boolean promoteToLeader = Command.permissable(e.getMember(), Command.userPerms.ADMIN); //Admin and Leader are nearly identical
			boolean promoteToColeader = promoteToLeader || Command.permissable(e.getMember(), Command.userPerms.COLEADER);
			boolean promoteToElder = promoteToColeader || Command.permissable(e.getMember(), Command.userPerms.ELDER);
			
			//If you can't even promote someone to elder, quit now.
			if (!promoteToElder)
				return;
			
			//For each mentioned player, either promote or fail to promote.
			List<Member> players = e.getMessage().getMentionedMembers();
			for (Member player : players) {
				//Check the current role of the player.
				userPerms p = Command.getHighestRole(player);
				if (p == userPerms.GUEST) {
					e.getGuild().addRoleToMember(player, e.getGuild().getRolesByName("",false).get(0));
				}
					
			}
		}
		
		else if (command[1].equalsIgnoreCase("Demote")) {
			List<Member> players = e.getMessage().getMentionedMembers();
		}
	}
}
