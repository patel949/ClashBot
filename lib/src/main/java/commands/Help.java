package commands;

import java.util.HashMap;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help extends Command {
	private Help() {
	}
	
	private static Help instance = null;
	public static Help getInstance() {
		if (instance == null)
			instance = new Help();
		return instance;
	}

	//get a list of "hooks"
	@Override
	public String[] getHooks() {
		String[] hooks = {"help"};
		return hooks;
	}
	
	private HashMap<String, Command> commands = null;
	
	@Override
	public void execute(MessageReceivedEvent e, String[] command) {
		if (commands == null) {
			commands = new HashMap<String, Command>();
			fillCommandTable(commands);
		}
		if (command.length < 2) {
			getHelp(e.getChannel());
			return;
		}
		
		Command cmd = commands.getOrDefault(command[1], this);
		cmd.getHelp(e.getChannel());
	}
	
	@Override
	public void getHelp(MessageChannel response) {
		StringBuilder r = new StringBuilder("Please use one of the following: ");
		for (String key : commands.keySet()) {
			r.append("\"");
			r.append(key);
			r.append("\" ");
		}
		response.sendMessage(r.toString()).queue();
	}
}
