package scommands;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import pl.shockah.json.JSONObject;
import scommands.CommandProvider.EPriority;
import shocky3.PluginInfo;

public class Plugin extends shocky3.ListenerPlugin {
	protected JSONObject j = null;
	public final DefaultCommandProvider provider;
	protected List<CommandProvider> providers = new LinkedList<>();
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
		provider = new DefaultCommandProvider(this);
	}
	
	public void add(CommandProvider... cps) {
		for (CommandProvider cp : cps) {
			if (!providers.contains(cp)) {
				providers.add(cp);
			}
		}
	}
	public void remove(CommandProvider... cps) {
		for (CommandProvider cp : cps) {
			providers.remove(cp);
		}
	}
	
	protected void onLoad() {
		botApp.settings.add(this, "characters", ".");
		providers.clear();
		provider.list.clear();
		
		add(
			provider
		);
		provider.add(
			new CmdDie(this),
			new CmdPlugins(this)
		);
	}
	
	protected void onUnload() {
		provider.list.clear();
		providers.clear();
	}
	
	protected void onMessage(MessageEvent<PircBotX> e) {
		String msg = e.getMessage();
		String[] spl = botApp.settings.getStringForChannel(e.getChannel(), this, "characters").split(" ");
		for (String s : spl) {
			if (msg.startsWith(s)) {
				msg = msg.substring(s.length());
				String trigger = msg.split("\\s")[0].toLowerCase();
				String args = msg.equals(trigger) ? "" : msg.substring(trigger.length() + 1).trim();
				
				List<Pair<ICommand, CommandProvider.EPriority>> list = new LinkedList<>();
				for (CommandProvider cp : providers) {
					cp.provide(list, botApp, e, trigger, args);
				}
				
				if (!list.isEmpty()) {
					Collections.sort(list, new Comparator<Pair<ICommand, CommandProvider.EPriority>>(){
						public int compare(Pair<ICommand, EPriority> p1, Pair<ICommand, EPriority> p2) {
							return Integer.compare(p2.get2().value, p1.get2().value);
						}
					});
					
					ICommand cmd = list.get(0).get1();
					cmd.call(botApp, e, trigger, args);
				}
				break;
			}
		}
	}
}