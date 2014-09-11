package scommands;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import pl.shockah.Pair;
import pl.shockah.json.JSONObject;
import scommands.CommandProvider.EPriority;
import shocky3.PluginInfo;
import shocky3.Shocky;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class Plugin extends shocky3.ListenerPlugin {
	@Dependency protected static sident.Plugin pluginIdent;
	
	protected JSONObject j = null;
	public final DefaultCommandProvider provider;
	protected List<CommandProvider> providers = Collections.synchronizedList(new LinkedList<CommandProvider>());
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
		provider = new DefaultCommandProvider(this);
	}
	
	public void add(CommandProvider... cps) {
		synchronized (providers) {for (CommandProvider cp : cps) {
			if (!providers.contains(cp)) {
				providers.add(cp);
			}
		}}
	}
	public void remove(CommandProvider... cps) {
		synchronized (providers) {for (CommandProvider cp : cps) {
			providers.remove(cp);
		}}
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
	
	protected void onMessage(MessageEvent<Bot> e) {
		handleCommands(new GenericUserMessageEvent<>(e));
	}
	protected void onPrivateMessage(PrivateMessageEvent<Bot> e) {
		handleCommands(new GenericUserMessageEvent<>(e));
	}
	protected void onNotice(NoticeEvent<Bot> e) {
		handleCommands(new GenericUserMessageEvent<>(e));
	}
	
	 public void handleCommands(GenericUserMessageEvent<Bot> e) {
		String msg = e.getMessage();
		String[] spl = botApp.settings.getStringForChannel(e.getChannel(), this, "characters").split(" ");
		for (String s : spl) {
			if (msg.startsWith(s)) {
				msg = msg.substring(s.length());
				String trigger = msg.split("\\s")[0].toLowerCase();
				String args = msg.equals(trigger) ? "" : msg.substring(trigger.length() + 1).trim();
				
				ICommand cmd = findCommand(botApp, e, trigger, args);
				if (cmd != null) {
					cmd.call(botApp, e, trigger, args);
				}
				break;
			}
		}
	}
	
	public ICommand findCommand(Shocky botApp, GenericUserMessageEvent<Bot> e, String trigger, String args) {
		List<Pair<ICommand, CommandProvider.EPriority>> list = new LinkedList<>();
		synchronized (providers) {for (CommandProvider cp : providers) {
			cp.provide(list, botApp, e, trigger, args);
		}}
		
		if (!list.isEmpty()) {
			Collections.sort(list, new Comparator<Pair<ICommand, CommandProvider.EPriority>>(){
				public int compare(Pair<ICommand, EPriority> p1, Pair<ICommand, EPriority> p2) {
					return Integer.compare(p2.get2().value, p1.get2().value);
				}
			});
			
			return list.get(0).get1();
		}
		return null;
	}
}