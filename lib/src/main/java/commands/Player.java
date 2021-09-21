package commands;

import java.util.List;

import lib.ServerData;
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
			
			//For each player to be promoted:
			for (Member player : players) {
				//get player's current role
				userPerms p = Command.getHighestRole(player);
				
				//if caller's role is higher than player's role, promote:
				if (p.getRank() < promoterRole.getRank() || isAdmin)
					Command.promoteMember(player);
			}
		}
		
		else if (command[1].equalsIgnoreCase("Demote")) {
			userPerms demoterRole = Command.getHighestRole(e.getMember());
			boolean isAdmin = Command.isAdmin(e.getMember());
			
			List<Member> players = e.getMessage().getMentionedMembers();
			
			//For each player to be demoted:
			for (Member player : players) {
				//get player's current role
				userPerms p = Command.getHighestRole(player);
				
				//if caller's role is higher than player's role, demote:
				if (p.getRank() < demoterRole.getRank() || isAdmin)
					Command.demoteMember(player);
				
			}
		} 

		else if (command[1].equalsIgnoreCase("Alias")) {
			
			//We need two things to alias: a Member ID, and an Alias
			List<Member> mentioned = e.getMessage().getMentionedMembers();
			
			//Thing no.1
			if (mentioned.size() != 1) {
				e.getChannel().sendMessage("You must mention one discord user followed by one alias.").queue();
				return;
			}
			Member aliased = mentioned.get(0);
			
			//Thing no.2
			String[] split = e.getMessage().getContentDisplay().split(aliased.getEffectiveName());
			if (split.length != 2) {
				e.getChannel().sendMessage("You must mention one discord user followed by one alias.").queue();
				return;
			}
			String alias = split[1];
			
			//And now we can put everything in place.
			ServerData sd = ServerData.getServer(e.getGuild().getIdLong());
			sd.addAlias(aliased.getIdLong(), alias);
		}
		
		else if (command[1].equalsIgnoreCase("DeAlias")) {
			
			//We need two things to de-alias: a Member ID, and an Alias
			List<Member> mentioned = e.getMessage().getMentionedMembers();
			
			//Thing no.1
			if (mentioned.size() != 1) {
				e.getChannel().sendMessage("You must mention one discord user followed by one alias.").queue();
				return;
			}
			Member aliased = mentioned.get(0);
			
			//Thing no.2
			String[] split = e.getMessage().getContentDisplay().split(aliased.getEffectiveName());
			if (split.length != 2) {
				e.getChannel().sendMessage("You must mention one discord user followed by one alias.").queue();
				return;
			}
			String alias = split[1];
			
			//And now we can put everything in place.
			ServerData sd = ServerData.getServer(e.getGuild().getIdLong());
			sd.addAlias(aliased.getIdLong(), alias);
		}
		
		else if (command[1].equalsIgnoreCase("WhoIs")) {
			List<Member> mlist = e.getMessage().getMentionedMembers();
			if (!mlist.isEmpty() || command.length == 2) {
				
				//0. determine the target (we allow a self-use of whois)
				Member target = mlist.isEmpty() ? e.getMember() : mlist.get(0); //use first mentioned player unless none is mentioned, in which case use self.
				//1. get the server object:
				ServerData sd = ServerData.getServer(e.getGuild().getIdLong());
				
				//2. get the list of aliases:
				List<String> aliases = sd.getAlias(target.getIdLong());
				
				//3. Build list based on returned aliases, in a human-friendly format.
				StringBuilder output = new StringBuilder(mlist.get(0).getEffectiveName());
				if (aliases.isEmpty())
					output.append(" has no registered accounts.");
				else {
					output.append(" is also known as ");
					output.append(aliases.get(0));
					for (int i = 1; i < aliases.size()-1; i++) {
						output.append(", ");
						output.append(aliases.get(i));
					}
					if (aliases.size() > 1)
						output.append(" and " + aliases.get(aliases.size()-1)); //add the last member, assuming there is more than one.
				}
				
				//return result.
				e.getChannel().sendMessage(output).queue();
				return;
			}
			
			//get name.
			String screenName = command[2];
			if (command[2].equals("-u")) {
				screenName = command[3];
			}
			
			//get server object
			ServerData sd = ServerData.getServer(e.getGuild().getIdLong());
			
			//Get list of usernames with this screen name:
			List<Long> users = sd.getMemberFromAlias(screenName);
			
			/* Three possibilities:
			 * 	(A) Exactly one player matches the given description
			 *  (B) No one matches this in-game name.
			 *  (C) Multiple players have this in-game name
			 */
			
			//(A)
			if (users.size() == 1) {
				e.getChannel().sendMessage(screenName + " is likely " + e.getGuild().getMemberById(users.get(0)).getEffectiveName()).queue();
				return;
			}
			
			//(b)
			if (users.size() == 0) {
				e.getChannel().sendMessage("Unfortunately, " + screenName + " has not yet been paired with any Discord accounts.").queue();
				return;
			}
			
			//(c)
			//no if as this is the last catch-all choice.
			StringBuilder output = new StringBuilder("Multiple discord users have this screen name: " + users.get(0));
			for (int i = 1; i < users.size() - 1; i++) {
				output.append(", ");
				output.append(users.get(i));
			}
			output.append(" and "); //yep, no oxford comma!
			output.append(users.get(users.size()-1));
			
			e.getChannel().sendMessage(output).queue();
		}
		
		else if (command[1].equalsIgnoreCase("help")) {
			getHelp(e.getChannel());
		}
	}
	
	@Override
	public void getHelp(MessageChannel response) {
		response.sendMessage("Promote: promotes the mentioned user(s).\n"
				+ "Demote: demotes the mentioned user(s).\n"
				+ "Alias: .\n"
				+ "removeClan: remove an inactive or abandoned clan.").queue();
	}
}
