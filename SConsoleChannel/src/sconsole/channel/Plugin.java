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
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.VoiceEvent;
import org.pircbotx.hooks.types.GenericChannelEvent;
import pl.shockah.Util;
import sconsole.ConsoleTab;
import sconsole.ConsoleViewSplitter;
import sconsole.ConsoleViewTab;
import sconsole.ConsoleViewTabs;
import sconsole.IConsolePluginListener;
import shocky3.BotManager;
import shocky3.PluginInfo;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.OutActionEvent;
import shocky3.pircbotx.event.OutMessageEvent;
import shocky3.pircbotx.event.QuitEvent2;

public class Plugin extends shocky3.ListenerPlugin implements IConsolePluginListener {
	public static final DateFormat format = new SimpleDateFormat("HH:mm:ss");
	public static final String[] colors = {
		Colors.BLUE, Colors.CYAN, Colors.GREEN,
		Colors.MAGENTA, Colors.RED, Colors.YELLOW
	};
	
	@Dependency protected static sconsole.Plugin pluginConsole;
	public Map<BotManager, ConsoleTab> tabsServer = Collections.synchronizedMap(new HashMap<BotManager, ConsoleTab>());
	public Map<ConsoleTab, Map<Channel, ConsoleViewSet>> tabsChannel = Collections.synchronizedMap(new HashMap<ConsoleTab, Map<Channel, ConsoleViewSet>>());
	
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
		while (pluginConsole.thread == null) {
			Util.sleep(50);
		}
		
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
	public ConsoleViewSet prepareChannelTab(GenericChannelEvent<Bot> e) {
		return prepareChannelTab(e.getBot().manager, e.getChannel());
	}
	public ConsoleViewSet prepareChannelTab(BotManager manager, Channel channel) {
		synchronized (tabsServer) {synchronized (tabsChannel) {
			return prepareChannelTab(prepareServerTab(manager), channel);
		}}
	}
	public ConsoleViewSet prepareChannelTab(ConsoleTab tabServer, Channel channel) {
		synchronized (tabsChannel) {
			if (tabsChannel.containsKey(tabServer) && tabsChannel.get(tabServer).containsKey(channel)) {
				return tabsChannel.get(tabServer).get(channel);
			}
			
			if (!tabsChannel.containsKey(tabServer)) {
				tabsChannel.put(tabServer, Collections.synchronizedMap(new HashMap<Channel, ConsoleViewSet>()));
			}
			Map<Channel, ConsoleViewSet> map = tabsChannel.get(tabServer);
			
			ConsoleViewChannelUserList cvcul = new ConsoleViewChannelUserList(pluginConsole.thread, channel);
			ConsoleViewSplitter cvs2 = new ConsoleViewSplitter(pluginConsole.thread);
			ConsoleViewChannelOutput cvco = new ConsoleViewChannelOutput(pluginConsole.thread);
			final ConsoleViewChannelInput cvci = new ConsoleViewChannelInput(pluginConsole.thread, channel);
			ConsoleViewSplitter cvs = new ConsoleViewSplitter(pluginConsole.thread){
				public void onFocus() {
					rect.thread.replaceFocus(cvci);
				}
			};
			
			cvs2.setMain(cvci, ConsoleViewSplitter.Side.Bottom);
			cvs2.setOff(cvco);
			
			cvs.setMain(cvcul, ConsoleViewSplitter.Side.Right);
			cvs.setOff(cvs2);
			
			ConsoleTab tab = new ConsoleTab(channel.getName(), cvs);
			ConsoleViewSet set = new ConsoleViewSet(tab, cvcul, cvci, cvco);
			cvcul.set = set;
			cvci.set = set;
			cvco.set = set;
			
			map.put(channel, set);
			((ConsoleViewTabs)((ConsoleViewSplitter)tabServer.view).main).tabs.add(tab);
			return set;
		}
	}
	
	protected void onMessage(MessageEvent<Bot> e) {
		prepareChannelTab(e).output.add(String.format("[%s] <%s%s%s> %s", format.format(new Date()), colors[Math.abs(e.getUser().getNick().hashCode() % colors.length)], e.getUser().getNick(), Colors.NORMAL, Colors.removeFormatting(e.getMessage())));
	}
	protected void onOutMessage(OutMessageEvent<Bot> e) {
		prepareChannelTab(e).output.add(String.format("[%s] <%s%s%s> %s", format.format(new Date()), colors[Math.abs(e.getUser().getNick().hashCode() % colors.length)], e.getUser().getNick(), Colors.NORMAL, Colors.removeFormatting(e.getMessage())));
	}
	
	protected void onAction(ActionEvent<Bot> e) {
		prepareChannelTab(e).output.add(String.format("[%s] *%s%s%s %s", format.format(new Date()), colors[Math.abs(e.getUser().getNick().hashCode() % colors.length)], e.getUser().getNick(), Colors.NORMAL, Colors.removeFormatting(e.getMessage())));
	}
	protected void onOutAction(OutActionEvent<Bot> e) {
		prepareChannelTab(e).output.add(String.format("[%s] *%s%s%s %s", format.format(new Date()), colors[Math.abs(e.getUser().getNick().hashCode() % colors.length)], e.getUser().getNick(), Colors.NORMAL, Colors.removeFormatting(e.getMessage())));
	}
	
	protected void onJoin(JoinEvent<Bot> e) {
		ConsoleViewSet set = prepareChannelTab(e);
		set.output.add(String.format("[%s] %s has joined", format.format(new Date()), e.getUser().getNick()));
		set.userlist.markUpdate = true;
	}
	protected void onPart(PartEvent<Bot> e) {
		ConsoleViewSet set = prepareChannelTab(e);
		set.output.add(String.format("[%s] %s has left", format.format(new Date()), e.getUser().getNick()));
		set.userlist.markUpdate = true;
	}
	protected void onQuit2(QuitEvent2<Bot> e) {
		for (Channel channel : e.getChannels()) {
			ConsoleViewSet set = prepareChannelTab(e.getBot().manager, channel);
			set.output.add(String.format("[%s] %s has quit", format.format(new Date()), e.getUser().getNick()));
			set.userlist.markUpdate = true;
		}
	}
	protected void onKick(KickEvent<Bot> e) {
		ConsoleViewSet set = prepareChannelTab(e);
		set.output.add(String.format("[%s] %s has kicked %s (%s)", format.format(new Date()), e.getUser().getNick(), e.getRecipient().getNick(), Colors.removeFormattingAndColors(e.getReason())));
		set.userlist.markUpdate = true;
	}
	
	protected void onNickChange(NickChangeEvent<Bot> e) {
		for (Channel channel : e.getUser().getChannels()) {
			ConsoleViewSet set = prepareChannelTab(e.getBot().manager, channel);
			set.output.add(String.format("[%s] %s is now known as %s", format.format(new Date()), e.getOldNick(), e.getNewNick()));
			set.userlist.markUpdate = true;
		}
	}
	
	protected void onVoice(VoiceEvent<Bot> e) {
		ConsoleViewSet set = prepareChannelTab(e);
		set.output.add(String.format("[%s] %s %s %s", format.format(new Date()), e.getUser().getNick(), e.hasVoice() ? "gives voice to" : "removes voice from", e.getRecipient().getNick()));
		set.userlist.markUpdate = true;
	}
	protected void onOp(OpEvent<Bot> e) {
		ConsoleViewSet set = prepareChannelTab(e);
		set.output.add(String.format("[%s] %s %s %s", format.format(new Date()), e.getUser().getNick(), e.isOp() ? "gives channel operator status to" : "removes channel operator status from", e.getRecipient().getNick()));
		set.userlist.markUpdate = true;
	}
}