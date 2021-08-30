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
	
	
	//get the value of the class, used to determine seniority among
	//commands - newer commands have authority over older commands.
	//This function should return YYYYMMDD as an integer 
	public int getClassValue() {
		return 20210416;
	}

	//Identify your name/version to an alternate class to help that
	//class decide who has seniority
	public String getClassName() {
		return "default";
	}
	
	//Determine if you have more right to execute 
	public boolean shouldExecute(String command, String otherName, int classValue) {
		return getClassValue() >= classValue;
	}
	
	//Determine if you can concurrently execute
	public boolean canConcurrentlyExecute(String command) {
		return false;
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
	/*
	 * Add new commands here, following the example for Manage command.
	 */
	public static void fillCommandTable(HashMap<String, Command> commands) {
		//Add all commands and hooks here.
		String[] hooks = Command.getInstance().getHooks();
		for (String hook : hooks)
			commands.put(hook, Command.getInstance());
		
		
		hooks = Manage.getInstance().getHooks();
		for (String hook : hooks)
			commands.putIfAbsent(hook, Manage.getInstance());
		
		
		hooks = Player.getInstance().getHooks();
		for (String hook : hooks)
			commands.putIfAbsent(hook, Player.getInstance());
	}
	
	public void getHelp(MessageChannel response) {
		response.sendMessage("Try using \"ping\" and see what happens!").queue();
	}
	

	
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
		if (highestRole == userPerms.ADMIN)
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
			remRole = member.getGuild().getRolesByName("Guest"    , false).get(0);
			addRole = member.getGuild().getRolesByName("Member"   , false).get(0);
		} else if (role == userPerms.MEMBER) {
			remRole = member.getGuild().getRolesByName("Member"   , false).get(0);
			addRole = member.getGuild().getRolesByName("Elder"    , false).get(0);
		} else if (role == userPerms.ELDER) {
			remRole = member.getGuild().getRolesByName("Elder"    , false).get(0);
			addRole = member.getGuild().getRolesByName("Co-Leader", false).get(0);
		} else if (role == userPerms.COLEADER) {
			remRole = member.getGuild().getRolesByName("Co-Leader", false).get(0);
			addRole = member.getGuild().getRolesByName("Leader"   , false).get(0);
		}
		member.getGuild().addRoleToMember(member, addRole);
		member.getGuild().removeRoleFromMember(member, remRole);
	
	}
	
	public static void demoteMember(Member member) {
		userPerms role = getHighestRole(member);
		
		if (role == userPerms.GUEST)
			return; //can't demote further than this!
		
		Role addRole = null;
		Role remRole = null;
		if (role == userPerms.MEMBER) {
			addRole = member.getGuild().getRolesByName("Guest"    , false).get(0);
			remRole = member.getGuild().getRolesByName("Member"   , false).get(0);
		} else if (role == userPerms.ELDER) {
			addRole = member.getGuild().getRolesByName("Member"   , false).get(0);
			remRole = member.getGuild().getRolesByName("Elder"    , false).get(0);
		} else if (role == userPerms.COLEADER) {
			addRole = member.getGuild().getRolesByName("Elder"    , false).get(0);
			remRole = member.getGuild().getRolesByName("Co-Leader", false).get(0);
		} else if (role == userPerms.LEADER) {
			addRole = member.getGuild().getRolesByName("Co-Leader", false).get(0);
			remRole = member.getGuild().getRolesByName("Leader"   , false).get(0);
		}
		member.getGuild().addRoleToMember(member, addRole);
		member.getGuild().removeRoleFromMember(member, remRole);
	
	}
	
}
