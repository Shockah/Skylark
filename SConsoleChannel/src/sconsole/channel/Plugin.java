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
import sconsole.ConsoleViewSplitter;
import sconsole.ConsoleViewTab;
import sconsole.ConsoleViewTabs;
import sconsole.IConsolePluginListener;
import shocky3.BotManager;
import shocky3.PluginInfo;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.OutMessageEvent;

public class Plugin extends shocky3.ListenerPlugin implements IConsolePluginListener {
	public static final DateFormat format = new SimpleDateFormat("HH:mm:ss");
	
	@Dependency protected static sconsole.Plugin pluginConsole;
	public Map<BotManager, ConsoleTab> tabsServer = Collections.synchronizedMap(new HashMap<BotManager, ConsoleTab>());
	public Map<ConsoleTab, Map<Channel, ConsoleTab>> tabsChannel = Collections.synchronizedMap(new HashMap<ConsoleTab, Map<Channel, ConsoleTab>>());
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		pluginConsole.add(this);
	}
	
	public void onConsoleEnabled() {
		new Thread(){
			public void run() {
				synchronized (tabsServer) {synchronized (tabsChannel) {
					synchronized (botApp.serverManager.botManagers) {for (BotManager manager : botApp.serverManager.botManagers) {
						synchronized (manager.bots) {
							if (manager.inAnyChannels()) {
								for (Bot bot : manager.bots) {
									for (Channel channel : bot.getUserBot().getChannels()) {
										prepareChannelTab(manager, channel);
									}
								}
							}
						}
					}}
				}}
			}
		}.start();
	}
	public void onConsoleDisabled() {}
	
	public ConsoleTab prepareServerTab(BotManager manager) {
		synchronized (tabsServer) {
			if (tabsServer.containsKey(manager)) {
				return tabsServer.get(manager);
			}
			
			ConsoleViewSplitter cvs = new ConsoleViewSplitter(pluginConsole.thread);
			ConsoleViewTabs cvts = new ConsoleViewTabs(pluginConsole.thread);
			ConsoleViewTab cvt = new ConsoleViewTab(pluginConsole.thread);
			
			cvts.view = cvt;
			cvt.view = cvts;
			
			cvs.setMain(cvts, ConsoleViewSplitter.Side.Bottom);
			cvs.setOff(cvt);
			
			ConsoleTab tab = new ConsoleTab(manager.name, cvs);
			tabsServer.put(manager, tab);
			pluginConsole.thread.viewTabs.tabs.add(tab);
			return tab;
		}
	}
	public ConsoleTab prepareChannelTab(BotManager manager, Channel channel) {
		synchronized (tabsServer) {synchronized (tabsChannel) {
			return prepareChannelTab(prepareServerTab(manager), channel);
		}}
	}
	public ConsoleTab prepareChannelTab(ConsoleTab tabServer, Channel channel) {
		synchronized (tabsChannel) {
			if (tabsChannel.containsKey(tabServer) && tabsChannel.get(tabServer).containsKey(channel)) {
				return tabsChannel.get(tabServer).get(channel);
			}
			
			if (!tabsChannel.containsKey(tabServer)) {
				tabsChannel.put(tabServer, Collections.synchronizedMap(new HashMap<Channel, ConsoleTab>()));
			}
			Map<Channel, ConsoleTab> map = tabsChannel.get(tabServer);
			
			ConsoleViewSplitter cvs = new ConsoleViewSplitter(pluginConsole.thread);
			ConsoleViewChannelUserList cvcul = new ConsoleViewChannelUserList(pluginConsole.thread, channel);
			ConsoleViewSplitter cvs2 = new ConsoleViewSplitter(pluginConsole.thread);
			ConsoleViewChannel cvc = new ConsoleViewChannel(pluginConsole.thread);
			ConsoleViewChannelInput cvci = new ConsoleViewChannelInput(pluginConsole.thread, channel);
			
			cvs2.setMain(cvci, ConsoleViewSplitter.Side.Bottom);
			cvs2.setOff(cvc);
			
			cvcul.view = cvci;
			cvci.view = cvcul;
			
			cvs.setMain(cvcul, ConsoleViewSplitter.Side.Right);
			cvs.setOff(cvs2);
			
			ConsoleTab tab = new ConsoleTab(channel.getName(), cvs);
			map.put(channel, tab);
			((ConsoleViewTabs)((ConsoleViewSplitter)tabServer.view).main).tabs.add(tab);
			return tab;
		}
	}
	
	public ConsoleViewChannel getConsoleViewChannel(BotManager manager, Channel channel) {
		return getConsoleViewChannel(prepareChannelTab(manager, channel));
	}
	public ConsoleViewChannel getConsoleViewChannel(ConsoleTab tabChannel) {
		return (ConsoleViewChannel)((ConsoleViewSplitter)((ConsoleViewSplitter)tabChannel.view).off).off;
	}
	
	protected void onMessage(MessageEvent<Bot> e) {
		ConsoleViewChannel view = getConsoleViewChannel(e.getBot().manager, e.getChannel());
		view.lines.add(String.format("[%s] <%s> %s", format.format(new Date()), e.getUser().getNick(), Colors.removeFormattingAndColors(e.getMessage())));
	}
	
	protected void onOutMessage(OutMessageEvent<Bot> e) {
		ConsoleViewChannel view = getConsoleViewChannel(e.getBot().manager, e.getChannel());
		view.lines.add(String.format("[%s] <%s> %s", format.format(new Date()), e.getUser().getNick(), Colors.removeFormattingAndColors(e.getMessage())));
	}
	
	protected void onJoin(JoinEvent<Bot> e) {
		ConsoleViewChannel view = getConsoleViewChannel(e.getBot().manager, e.getChannel());
		view.lines.add(String.format("[%s] %s joined.", format.format(new Date()), e.getUser().getNick()));
	}
	
	protected void onPart(PartEvent<Bot> e) {
		ConsoleViewChannel view = (ConsoleViewChannel)prepareChannelTab(e.getBot().manager, e.getChannel()).view;
		view.lines.add(String.format("[%s] %s left.", format.format(new Date()), e.getUser().getNick()));
	}
}