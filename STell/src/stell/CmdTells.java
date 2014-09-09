package stell;

import java.util.ListIterator;
import scommands.Command;
import shocky3.Bot;
import shocky3.Shocky;
import shocky3.pircbotx.GenericUserMessageEvent;

public class CmdTells extends Command {
	protected final Plugin pluginTell;
	
	public CmdTells(Plugin plugin) {
		super(plugin, "tells");
		pluginTell = plugin;
	}
	
	public void call(Shocky botApp, GenericUserMessageEvent<Bot> e, String trigger, String args) {
		ListIterator<Tell> lit = pluginTell.tells.listIterator();
		while (lit.hasNext()) {
			Tell tell = lit.next();
			if (tell.matches(botApp.serverManager.byBot(e), e.getUser())) {
				String[] spl = tell.buildMessage().split("\\n");
				for (String s : spl) {
					e.getUser().send().notice(s);
				}
				Tell.removeDB(plugin, tell);
				lit.remove();
			}
		}
	}
}