package stell;

import java.util.ListIterator;
import scommands.Command;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdTells extends Command {
	protected final Plugin pluginTell;
	
	public CmdTells(Plugin plugin) {
		super(plugin, "tells");
		pluginTell = plugin;
	}
	
	public String call(GenericUserMessageEvent e, String trigger, String args, boolean chain) {
		ListIterator<Tell> lit = pluginTell.tells.listIterator();
		while (lit.hasNext()) {
			Tell tell = lit.next();
			if (tell.matches(e.<Bot>getBot().manager, e.getUser())) {
				String[] spl = tell.buildMessage().split("\\n");
				if (!chain)
					for (String s : spl)
						e.getUser().send().notice(s);
				Tell.removeDB(plugin, tell);
				lit.remove();
			}
		}
		return "";
	}
}