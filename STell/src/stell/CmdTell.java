package stell;

import java.util.LinkedList;
import java.util.List;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import scommands.Command;
import shocky3.BotManager;
import shocky3.Shocky;
import sident.IdentHandler;

public class CmdTell extends Command {
	protected final Plugin pluginTell;
	
	public CmdTell(Plugin plugin) {
		super(plugin, "tell");
		pluginTell = plugin;
	}
	
	public void call(Shocky botApp, MessageEvent<PircBotX> e, String trigger, String args) {
		String[] spl = args.split("\\s");
		if (spl.length >= 2) {
			String target = spl[0];
			args = args.substring(target.length() + 1);
			
			String[] targetSpl = target.split("\\|");
			List<Pair<IdentHandler, String>> list = new LinkedList<>();
			String hasServer = null;
			boolean hasOther = false;
			for (String targetS : targetSpl) {
				if (targetS.indexOf(':') == -1) {
					targetS = String.format("%s:%s", Plugin.pluginIdent.handlerNick.id, targetS);
				}
				IdentHandler handler = Plugin.pluginIdent.getIdentHandlerFor(null, targetS);
				if (handler == null) return;
				if (handler.name.equals("server")) {
					hasServer = targetS.substring(targetS.indexOf(':') + 1);
				} else {
					hasOther = true;
				}
				list.add(new Pair<>(handler, targetS.substring(targetS.indexOf(':') + 1)));
			}
			
			if (!hasOther) {
				return;
			}
			BotManager manager = botApp.serverManager.byBot(e);
			if (hasServer == null) {
				hasServer = manager.name;
				list.add(new Pair<>(Plugin.pluginIdent.handlerServer, String.format("%s (%s)", manager.name, manager.host)));
			}
			
			BotManager managerReceiver = botApp.serverManager.byServerName(hasServer);
			for (Pair<IdentHandler, String> pair : list) {
				pair.set1(pair.get1().copy(managerReceiver));
			}
			
			Tell tell = Tell.create(manager, e.getUser(), list, args);
			pluginTell.tells.add(tell);
			e.getUser().send().notice("I'll pass that along.");
			Tell.writeDB(plugin, tell);
			Tell.updateIDDB(plugin);
		}
	}
}