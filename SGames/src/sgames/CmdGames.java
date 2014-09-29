package sgames;

import org.pircbotx.User;
import scommands.Command;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdGames extends Command {
	public final Plugin pluginGames;
	
	public CmdGames(Plugin plugin) {
		super(plugin, "games", "game");
		pluginGames = plugin;
	}
	
	public void call(GenericUserMessageEvent<Bot> e, String trigger, String args) {
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
			}
		}
	}
}