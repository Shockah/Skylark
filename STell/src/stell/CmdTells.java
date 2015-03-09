package stell;

import java.util.ListIterator;
import scommands.CommandStack;
import scommands.TextCommand;
import shocky3.MultilineString;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class CmdTells extends TextCommand {
	protected final Plugin pluginTell;
	
	public CmdTells(Plugin plugin) {
		super(plugin, "tells");
		pluginTell = plugin;
	}
	
	public String call(GenericUserMessageEvent e, String input, CommandStack stack) {
		ListIterator<Tell> lit = pluginTell.tells.listIterator();
		MultilineString str = new MultilineString();
		while (lit.hasNext()) {
			Tell tell = lit.next();
			if (tell.matches(e.<Bot>getBot().manager, e.getUser())) {
				String[] spl = tell.buildMessage().split("\\n");
				str.add(spl);
				Tell.removeDB(plugin, tell);
				lit.remove();
			}
		}
		return str.toString();
	}
}