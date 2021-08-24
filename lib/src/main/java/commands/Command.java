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
	
	public static void fillCommandTable(HashMap<String, Command> commands) {
		//Add all commands and hooks here.
		String[] hooks = Command.getInstance().getHooks();
		for (String hook : hooks)
			commands.put(hook, Command.getInstance());
		hooks = Manage.getInstance().getHooks();
		for (String hook : hooks)
			commands.putIfAbsent(hook, Manage.getInstance());
	}
	
	public void getHelp(MessageChannel response) {
		response.sendMessage("Try using \"ping\" and see what happens!").queue();
	}
	

	
	/*
	 * Enum that controls ranks used by the bot
	 */
	public enum userPerms {
		GUEST,
		MEMBER,
		ELDER,
		COLEADER,
		ADMIN,
		LEADER
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
		if (roles.stream().filter(role -> role.getName().equals("Admin")).count() > 0)
			return userPerms.ADMIN;
		if (roles.stream().filter(role -> role.getName().equals("Co-Leader")).count() > 0)
			return userPerms.COLEADER;
		if (roles.stream().filter(role -> role.getName().equals("Elder")).count() > 0)
			return userPerms.ELDER;
		if (roles.stream().filter(role -> role.getName().equalsIgnoreCase("Member")).count() > 0)
			return userPerms.MEMBER;
		
		return userPerms.GUEST;
	}
	
	public static void promoteMember(Member member, userPerms role) {
		if (role == userPerms.MEMBER) {
			member.getGuild().addRoleToMember(member, member.getGuild().getRolesByName("Member", false))
			
		}
	}
	
	public static void demoteMember(Member member, userPerms newRole) {
		
	}
	
}
