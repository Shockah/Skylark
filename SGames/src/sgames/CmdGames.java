package sgames;

import java.util.LinkedList;
import java.util.List;
import org.pircbotx.User;
import scommands.Command;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdGames extends Command {
	public final Plugin pluginGames;
	
	public CmdGames(Plugin plugin) {
		super(plugin, "games", "game");
		pluginGames = plugin;
	}
	
	public String call(GenericUserMessageEvent e, String trigger, String args, boolean chain) {
		String[] spl = args.split("\\s");
		if (spl.length != 0) {
			if (spl[0].equalsIgnoreCase("stats") && spl.length >= 2) {
				User user1 = e.getUser();
				User user2 = e.getBot().getUserChannelDao().getUser(spl[1]);
				
				Scores scores = pluginGames.getFor(user1, user2);
				int wins = 0, loses = 0;
				if (spl.length == 3) {
					wins = scores.wins.containsKey(spl[2].toLowerCase()) ? scores.wins.get(spl[2].toLowerCase()) : 0;
					loses = scores.loses.containsKey(spl[2].toLowerCase()) ? scores.loses.get(spl[2].toLowerCase()) : 0;
				} else {
					wins = scores.totalWins();
					loses = scores.totalLoses();
				}
				
				e.getChannel().send().message(String.format("%s %d : %d %s", user1.getNick(), wins, loses, user2.getNick()));
			} else if (spl[0].equalsIgnoreCase("challenge") && spl.length >= 3) {
				String game = spl[1].toLowerCase();
				User user = e.getUser();
				List<User> users = new LinkedList<>();
				for (int i = 2; i < spl.length; i++) {
					User user2 = e.getBot().getUserChannelDao().getUser(spl[i]);
					if (user2 != null) users.add(user2);
				}
				
				if (pluginGames.areBusy(user, users)) {
					if (!chain) e.respond("Users are busy.");
				} else {
					
				}
			}
		}
		return "";
	}
}