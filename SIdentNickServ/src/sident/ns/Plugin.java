package sident.ns;

import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.WhoisEvent;
import shocky3.BotManager;
import shocky3.PluginInfo;
import shocky3.pircbotx.AccountNotifyEvent;
import shocky3.pircbotx.ExtendedJoinEvent;

public class Plugin extends shocky3.ListenerPlugin {
	@Dependency protected static sident.Plugin pluginIdent;
	
	protected NickServIdentHandler identHandler = null;
	protected int requests = 0;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		pluginIdent.add(
			identHandler = new NickServIdentHandler(this)
		);
		
		for (BotManager manager : botApp.serverManager.botManagers) {
			NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(manager, identHandler.id);
			if (manager.inAnyChannels()) {
				if (handler.isAvailable() && handler.availableWHOX) {
					for (PircBotX bot : manager.bots) {
						for (Channel channel : bot.getUserBot().getChannels()) {
							handler.requests++;
							bot.sendRaw().rawLine(String.format("WHO %s %%na", channel.getName()));
						}
					}
				}
			}
		}
	}
	
	protected void onWhois(WhoisEvent<PircBotX> e) {
		if (!e.getNick().equals("NickServ")) return;
		((NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id)).whois = e;
	}
	
	protected void onNotice(NoticeEvent<PircBotX> e) {
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
		if (!handler.isAvailable()) return;
		if (!e.getUser().getNick().equals("NickServ")) return;
		
		String[] spl = e.getMessage().split(" ");
		if (spl[1].equals("->") && (!spl[2].equals("0") && !spl[2].equals("*")) && spl[4].equals("3")) {
			handler.setAccount(spl[0], spl[2]);
		} else {
			handler.setAccount(spl[0], null);
		}
	}
	
	protected void onNickChange(NickChangeEvent<PircBotX> e) {
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
		if (!handler.isAvailable()) return;
		String sold = e.getOldNick().toLowerCase();
		String snew = e.getNewNick().toLowerCase();
		if (handler.map.containsKey(sold)) {
			handler.map.put(snew, handler.map.get(sold));
			handler.map.remove(sold);
		}
	}
	
	protected void onQuit(QuitEvent<PircBotX> e) {
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
		if (!handler.isAvailable()) return;
		String nick = e.getUser().getNick().toLowerCase();
		if (handler.map.containsKey(nick)) {
			handler.map.remove(nick);
		}
	}
	
	protected void onJoin(JoinEvent<PircBotX> e) {
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
		if (handler.isAvailable() && handler.availableWHOX) {
			if (e.getBot().getUserBot().equals(e.getUser())) {
				handler.requests++;
				e.getBot().sendRaw().rawLine(String.format("WHO %s %%na", e.getChannel().getName()));
			} else if (!handler.availableExtendedJoin) {
				handler.requests++;
				e.getBot().sendRaw().rawLine(String.format("WHO %s %%na", e.getUser().getNick()));
			}
		}
	}
	
	protected void onExtendedJoin(ExtendedJoinEvent<PircBotX> e) {
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
		handler.setAccount(e.getUser().getNick(), e.getAccount());
	}
	
	protected void onAccountNotify(AccountNotifyEvent<PircBotX> e) {
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
		handler.setAccount(e.getUser().getNick(), e.getAccount());
	}
	
	protected void onServerResponse(ServerResponseEvent<PircBotX> e) {
		if (e.getCode() == 315 || e.getCode() == 354) {
			NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
			if (!handler.isAvailable()) return;
			if (handler.requests != 0) {
				if (e.getCode() == 315) {
					handler.onServerResponseEnd();
				} else if (e.getCode() == 354) {
					List<String> list = e.getParsedResponse();
					if (list.size() == 3) {
						handler.onServerResponseEntry(list.get(1), list.get(2));
					}
				}
			}
		}
	}
}