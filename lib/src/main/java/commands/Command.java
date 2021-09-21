package commands;

import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Command {
	
	//Not a true singleton. Superclass for other singletons.
	protected Command() {}
	private static Command instance = null;
	public static Command getInstance() {
		if (instance == null)
			instance = new Command();
		return instance;
	}	
	

	/*
	 * Add new commands here
	 */
	private static final Command[] commandsList = new Command[] {
			Command.getInstance(), 
			Manage.getInstance(), 
			Player.getInstance(),
			War.getInstance(),
	};

	public static void fillCommandTable(HashMap<String, Command> commands) {
		//Add all commands and hooks here.
		String[] hooks;
		for (Command cmd : commandsList) {
			hooks = cmd.getHooks();
			for (String hook : hooks)
				commands.put(hook, cmd);
		}
	}

	
	
	
	//Execute the command
	public void execute(MessageReceivedEvent event, String[] command) {
		if (event.isFromType(ChannelType.TEXT)) {
			event.getChannel().sendMessage("pong").queue();
		}
	}
	
	//get a list of "hooks"
	public String[] getHooks() {
		String[] hooks = {"ping"};
		return hooks;
	}

	//Display help for the command
	public void getHelp(MessageChannel response) {
		response.sendMessage("Try using \"ping\" and see what happens!").queue();
	}
	
	
	
	//some functions used by many of the commands:
	
	/*
	 * Enum that controls ranks used by the bot
	 */
	public enum userPerms {
		GUEST(0),
		MEMBER(1),
		ELDER(2),
		COLEADER(3),
		ADMIN(4),
		LEADER(5);

		int rank;
		userPerms(int rank) {
			this.rank = rank;
		}
		int getRank() {
			return rank;
		}
	}
	
	/*
	 * returns true if the user has at least the selected permissionLevel.
	 */
	public static boolean permissable(Member member, userPerms permissionLevel) {
		if (permissionLevel == userPerms.GUEST)
			return true; //everyone has this level for now.
		
		userPerms highestRole = getHighestRole(member);
		if (highestRole == userPerms.LEADER)
			return true;
		if (permissionLevel == userPerms.LEADER)
			return false;
		if (member.getRoles().stream().filter(role -> role.getName().equals("Admin")).count() > 0) //Highest role does not try admin.
			return true;
		if (permissionLevel == userPerms.ADMIN)
			return false;
		if (highestRole == userPerms.COLEADER)
			return true;
		if (permissionLevel == userPerms.COLEADER)
			return false;
		if (highestRole == userPerms.ELDER)
			return true;
		if (permissionLevel == userPerms.ELDER)
			return false;
		if (highestRole == userPerms.MEMBER)
			return true;
		
		return false;
		
	}
	
	public static userPerms getHighestRole(Member member) {
		
		List<Role> roles = member.getRoles();
		
		if (roles.stream().filter(role -> role.getName().equals("Leader")).count() > 0)
			return userPerms.LEADER;
		if (roles.stream().filter(role -> role.getName().equals("Co-Leader")).count() > 0)
			return userPerms.COLEADER;
		if (roles.stream().filter(role -> role.getName().equals("Elder")).count() > 0)
			return userPerms.ELDER;
		if (roles.stream().filter(role -> role.getName().equalsIgnoreCase("Member")).count() > 0)
			return userPerms.MEMBER;
		
		return userPerms.GUEST;
	}
	
	public static boolean isAdmin(Member member) {
		return (member.getRoles().stream().filter(role -> role.getName().equals("Admin")).count() > 0);
	}
	
	public static void promoteMember(Member member) {
		userPerms role = getHighestRole(member);
		
		if (role == userPerms.LEADER)
			return; //cannot promote from this role - it's the highest.
		Role addRole = null;
		Role remRole = null;
		if (role == userPerms.GUEST) {
			
			if (member.getGuild().getRolesByName("Guest", false).size() == 0)
				member.getGuild().createRole().setName("Guest").complete();
			remRole = member.getGuild().getRolesByName("Guest"    , false).get(0);
			
			if (member.getGuild().getRolesByName("Member", false).size() == 0)
				member.getGuild().createRole().setName("Member").complete();
			addRole = member.getGuild().getRolesByName("Member"   , false).get(0);
		
		} else if (role == userPerms.MEMBER) {
			
			if (member.getGuild().getRolesByName("Member", false).size() == 0)
				member.getGuild().createRole().setName("Member").complete();
			remRole = member.getGuild().getRolesByName("Member"   , false).get(0);
			
			if (member.getGuild().getRolesByName("Elder", false).size() == 0)
				member.getGuild().createRole().setName("Elder").complete();
			addRole = member.getGuild().getRolesByName("Elder"    , false).get(0);
		
		} else if (role == userPerms.ELDER) {
			if (member.getGuild().getRolesByName("Elder", false).size() == 0)
				member.getGuild().createRole().setName("Elder").complete();
			remRole = member.getGuild().getRolesByName("Elder"    , false).get(0);
			
			if (member.getGuild().getRolesByName("Co-Leader", false).size() == 0)
				member.getGuild().createRole().setName("Co-Leader").complete();
			addRole = member.getGuild().getRolesByName("Co-Leader", false).get(0);
			
		} else if (role == userPerms.COLEADER) {
			
			if (member.getGuild().getRolesByName("Co-Leader", false).size() == 0)
				member.getGuild().createRole().setName("Co-Leader").complete();
			remRole = member.getGuild().getRolesByName("Co-Leader", false).get(0);
			
			if (member.getGuild().getRolesByName("Leader", false).size() == 0)
				member.getGuild().createRole().setName("Leader").complete();
			addRole = member.getGuild().getRolesByName("Leader"   , false).get(0);
			
		}

		if (addRole != null)
			member.getGuild().addRoleToMember(member, addRole).complete();
		if (remRole != null)
			member.getGuild().removeRoleFromMember(member, remRole).complete();
	
	}
	
	public static void demoteMember(Member member) {
		userPerms role = getHighestRole(member);
		
		if (role == userPerms.GUEST)
			return; //can't demote further than this!
		
		Role addRole = null;
		Role remRole = null;
		if (role == userPerms.MEMBER) {
			
			if (member.getGuild().getRolesByName("Guest", false).size() == 0)
				member.getGuild().createRole().setName("Guest").complete();
			addRole = member.getGuild().getRolesByName("Guest"    , false).get(0);

			if (member.getGuild().getRolesByName("Member", false).size() == 0)
				member.getGuild().createRole().setName("Member").complete();
			remRole = member.getGuild().getRolesByName("Member"   , false).get(0);
			
		} else if (role == userPerms.ELDER) {
			
			if (member.getGuild().getRolesByName("Member", false).size() == 0)
				member.getGuild().createRole().setName("Member").complete();
			addRole = member.getGuild().getRolesByName("Member"   , false).get(0);

			if (member.getGuild().getRolesByName("Elder", false).size() == 0)
				member.getGuild().createRole().setName("Elder").complete();
			remRole = member.getGuild().getRolesByName("Elder"    , false).get(0);
			
		} else if (role == userPerms.COLEADER) {
			
			if (member.getGuild().getRolesByName("Elder", false).size() == 0)
				member.getGuild().createRole().setName("Elder").complete();
			addRole = member.getGuild().getRolesByName("Elder"    , false).get(0);

			if (member.getGuild().getRolesByName("Co-Leader", false).size() == 0)
				member.getGuild().createRole().setName("Co-Leader").complete();
			remRole = member.getGuild().getRolesByName("Co-Leader", false).get(0);
			
		} else if (role == userPerms.LEADER) {
			if (member.getGuild().getRolesByName("Co-Leader", false).size() == 0)
				member.getGuild().createRole().setName("Co-Leader").complete();
			addRole = member.getGuild().getRolesByName("Co-Leader", false).get(0);

			if (member.getGuild().getRolesByName("Leader", false).size() == 0)
				member.getGuild().createRole().setName("Leader").complete();
			remRole = member.getGuild().getRolesByName("Leader"   , false).get(0);
			
		}
		
		if (addRole != null)
			member.getGuild().addRoleToMember(member, addRole).complete();
		if (remRole != null)
			member.getGuild().removeRoleFromMember(member, remRole).complete();
	
	}
	
}
