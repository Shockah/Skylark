package sconsole.channel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.types.GenericChannelEvent;
import sconsole.ConsoleTab;
import sconsole.ConsoleViewSplitter;
import sconsole.ConsoleViewTab;
import sconsole.ConsoleViewTabs;
import sconsole.ConsoleViewTextarea;
import sconsole.IConsolePluginListener;
import shocky3.BotManager;
import shocky3.PluginInfo;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.OutActionEvent;
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
			ConsoleViewTextarea cva = new ConsoleViewTextarea(pluginConsole.thread);
			ConsoleViewChannelInput cvci = new ConsoleViewChannelInput(pluginConsole.thread, channel);
			
			cvs2.setMain(cvci, ConsoleViewSplitter.Side.Bottom);
			cvs2.setOff(cva);
			
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
	
	public ConsoleViewChannelUserList getConsoleViewChannelUserList(GenericChannelEvent<Bot> e) {
		return getConsoleViewChannelUserList(prepareChannelTab(e.getBot().manager, e.getChannel()));
	}
	public ConsoleViewChannelUserList getConsoleViewChannelUserList(BotManager manager, Channel channel) {
		return getConsoleViewChannelUserList(prepareChannelTab(manager, channel));
	}
	public ConsoleViewChannelUserList getConsoleViewChannelUserList(ConsoleTab tabChannel) {
		return (ConsoleViewChannelUserList)((ConsoleViewSplitter)tabChannel.view).main;
	}
	
	public ConsoleViewTextarea getConsoleViewTextarea(GenericChannelEvent<Bot> e) {
		return getConsoleViewTextarea(prepareChannelTab(e.getBot().manager, e.getChannel()));
	}
	public ConsoleViewTextarea getConsoleViewTextarea(BotManager manager, Channel channel) {
		return getConsoleViewTextarea(prepareChannelTab(manager, channel));
	}
	public ConsoleViewTextarea getConsoleViewTextarea(ConsoleTab tabChannel) {
		return (ConsoleViewTextarea)((ConsoleViewSplitter)((ConsoleViewSplitter)tabChannel.view).off).off;
	}
	
	protected void onMessage(MessageEvent<Bot> e) {
		getConsoleViewTextarea(e).lines.add(String.format("[%s] <%s> %s", format.format(new Date()), e.getUser().getNick(), Colors.removeFormattingAndColors(e.getMessage())));
	}
	protected void onOutMessage(OutMessageEvent<Bot> e) {
		getConsoleViewTextarea(e).lines.add(String.format("[%s] <%s> %s", format.format(new Date()), e.getUser().getNick(), Colors.removeFormattingAndColors(e.getMessage())));
	}
	
	protected void onAction(ActionEvent<Bot> e) {
		getConsoleViewTextarea(e).lines.add(String.format("[%s] *%s %s", format.format(new Date()), e.getUser().getNick(), Colors.removeFormattingAndColors(e.getMessage())));
	}
	protected void onOutAction(OutActionEvent<Bot> e) {
		getConsoleViewTextarea(e).lines.add(String.format("[%s] *%s %s", format.format(new Date()), e.getUser().getNick(), Colors.removeFormattingAndColors(e.getMessage())));
	}
	
	protected void onJoin(JoinEvent<Bot> e) {
		getConsoleViewTextarea(e).lines.add(String.format("[%s] %s has joined", format.format(new Date()), e.getUser().getNick()));
		getConsoleViewChannelUserList(e).markUpdate = true;
	}
	protected void onPart(PartEvent<Bot> e) {
		getConsoleViewTextarea(e).lines.add(String.format("[%s] %s has left", format.format(new Date()), e.getUser().getNick()));
		getConsoleViewChannelUserList(e).markUpdate = true;
	}
	protected void onQuit(QuitEvent<Bot> e) {
		for (Channel channel : e.getUser().getChannels()) {
			getConsoleViewTextarea(e.getBot().manager, channel).lines.add(String.format("[%s] %s has quit", format.format(new Date()), e.getUser().getNick()));
			getConsoleViewChannelUserList(e.getBot().manager, channel).markUpdate = true;
		}
	}
	protected void onKick(KickEvent<Bot> e) {
		getConsoleViewTextarea(e).lines.add(String.format("[%s] %s has kicked %s (%s)", format.format(new Date()), e.getUser().getNick(), e.getRecipient().getNick(), Colors.removeFormattingAndColors(e.getReason())));
		getConsoleViewChannelUserList(e).markUpdate = true;
	}
	
	protected void onNickChange(NickChangeEvent<Bot> e) {
		for (Channel channel : e.getUser().getChannels()) {
			getConsoleViewTextarea(e.getBot().manager, channel).lines.add(String.format("[%s] %s is now known as %s", format.format(new Date()), e.getOldNick(), e.getNewNick()));
			getConsoleViewChannelUserList(e.getBot().manager, channel).markUpdate = true;
		}
	}
}