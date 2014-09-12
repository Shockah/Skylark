package sconsole.channel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;
import sconsole.ConsoleTab;
import sconsole.IConsolePluginListener;
import shocky3.BotManager;
import shocky3.PluginInfo;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.OutMessageEvent;

public class Plugin extends shocky3.ListenerPlugin implements IConsolePluginListener {
	public static final DateFormat format = new SimpleDateFormat("HH:mm:ss");
	
	@Dependency protected static sconsole.Plugin pluginConsole;
	
	public Map<BotManager, Map<String, ConsoleTab>> tabs = Collections.synchronizedMap(new HashMap<BotManager, Map<String, ConsoleTab>>());
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		pluginConsole.add(this);
	}
	
	public void onConsoleEnabled() {
		new Thread(){
			public void run() {
				synchronized (tabs) {
					synchronized (botApp.serverManager.botManagers) {for (BotManager manager : botApp.serverManager.botManagers) {
						synchronized (manager.bots) {
							if (manager.inAnyChannels()) {
								for (Bot bot : manager.bots) {
									for (Channel channel : bot.getUserBot().getChannels()) {
										if (!tabs.containsKey(manager)) {
											tabs.put(manager, Collections.synchronizedMap(new HashMap<String, ConsoleTab>()));
										}
										Map<String, ConsoleTab> managerTabs = tabs.get(manager);
										ConsoleTab tab = new ConsoleTab(channel.getName(), new ConsoleViewChannel(pluginConsole.thread));
										managerTabs.put(channel.getName(), tab);
										pluginConsole.thread.viewTabs.tabs.add(tab);
									}
								}
							}
						}
					}}
				}
			}
		}.start();
	}
	public void onConsoleDisabled() {}
	
	protected void onMessage(MessageEvent<Bot> e) {
		ConsoleViewChannel view = (ConsoleViewChannel)tabs.get(e.getBot().manager).get(e.getChannel().getName()).view;
		view.lines.add(String.format("[%s] <%s> %s", format.format(new Date()), e.getUser().getNick(), Colors.removeFormattingAndColors(e.getMessage())));
	}
	
	protected void onOutMessage(OutMessageEvent<Bot> e) {
		ConsoleViewChannel view = (ConsoleViewChannel)tabs.get(e.getBot().manager).get(e.getChannel().getName()).view;
		view.lines.add(String.format("[%s] <%s> %s", format.format(new Date()), e.getUser().getNick(), Colors.removeFormattingAndColors(e.getMessage())));
	}
	
	protected void onJoin(JoinEvent<Bot> e) {
		synchronized (tabs) {
			Map<String, ConsoleTab> managerTabs;
			BotManager manager = e.getBot().manager;
			
			if (e.getUser().getNick().equals(e.getBot().getUserBot().getNick())) {
				if (!tabs.containsKey(manager)) {
					tabs.put(manager, Collections.synchronizedMap(new HashMap<String, ConsoleTab>()));
				}
				managerTabs = tabs.get(manager);
				ConsoleTab tab = new ConsoleTab(e.getChannel().getName(), new ConsoleViewChannel(pluginConsole.thread));
				managerTabs.put(e.getChannel().getName(), tab);
				pluginConsole.thread.viewTabs.tabs.add(tab);
			}
			
			managerTabs = tabs.get(manager);
			ConsoleTab tab = managerTabs.get(e.getChannel().getName());
			ConsoleViewChannel view = (ConsoleViewChannel)tab.view;
			view.lines.add(String.format("[%s] %s joined.", format.format(new Date()), e.getUser().getNick()));
		}
	}
	
	protected void onPart(PartEvent<Bot> e) {
		Map<String, ConsoleTab> managerTabs;
		BotManager manager = e.getBot().manager;
		
		managerTabs = tabs.get(manager);
		ConsoleTab tab = managerTabs.get(e.getChannel().getName());
		ConsoleViewChannel view = (ConsoleViewChannel)tab.view;
		view.lines.add(String.format("[%s] %s left.", format.format(new Date()), e.getUser().getNick()));
		
		if (e.getUser().equals(e.getBot().getUserBot())) {
			pluginConsole.thread.viewTabs.tabs.remove(tab);
			managerTabs.remove(e.getChannel().getName());
		}
	}
}