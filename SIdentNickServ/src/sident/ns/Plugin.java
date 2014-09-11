package sident.ns;

import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.WhoisEvent;
import shocky3.BotManager;
import shocky3.PluginInfo;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.AccountNotifyEvent;
import shocky3.pircbotx.event.ExtendedJoinEvent;

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
	
	protected void onWhois(WhoisEvent<Bot> e) {
		if (!e.getNick().equals("NickServ")) return;
		((NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id)).whois = e;
	}
	
	protected void onNotice(NoticeEvent<Bot> e) {
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
		if (!handler.isAvailable()) return;
		if (!e.getUser().getNick().equals("NickServ")) return;
		
		String[] spl = e.getMessage().split(" ");
		if (spl[1].equals("->") && (!spl[2].equals("0") && !spl[2].equals("*")) && spl[4].equals("3")) {
			handler.setAccount(spl[0], spl[2], false);
		} else {
			handler.setAccount(spl[0], null, false);
		}
	}
	
	protected void onNickChange(NickChangeEvent<Bot> e) {
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
		if (!handler.isAvailable()) return;
		String sold = e.getOldNick().toLowerCase();
		String snew = e.getNewNick().toLowerCase();
		if (handler.map.containsKey(sold)) {
			handler.map.put(snew, handler.map.get(sold));
			handler.map.remove(sold);
		}
	}
	
	protected void onQuit(QuitEvent<Bot> e) {
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
		if (!handler.isAvailable()) return;
		String nick = e.getUser().getNick().toLowerCase();
		if (handler.map.containsKey(nick)) {
			handler.map.remove(nick);
		}
	}
	
	protected void onJoin(JoinEvent<Bot> e) {
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
	
	protected void onPart(PartEvent<Bot> e) {
		BotManager manager = botApp.serverManager.byBot(e);
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(manager, identHandler.id);
		if (handler.isAvailable()) {
			boolean foundUser = false;
			L: for (PircBotX bot : manager.bots) {
				for (Channel channel : bot.getUserBot().getChannels()) {
					if (channel.getUsers().contains(e.getUser())) {
						foundUser = true;
						break L;
					}
				}
			}
			if (!foundUser) {
				String nick = e.getUser().getNick().toLowerCase();
				if (handler.map.containsKey(nick)) {
					handler.map.remove(nick);
				}
			}
		}
	}
	
	protected void onExtendedJoin(ExtendedJoinEvent<Bot> e) {
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
		handler.setAccount(e.getUser().getNick(), e.getAccount());
	}
	
	protected void onAccountNotify(AccountNotifyEvent<Bot> e) {
		NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
		handler.setAccount(e.getUser().getNick(), e.getAccount());
	}
	
	protected void onServerResponse(ServerResponseEvent<Bot> e) {
		if (e.getCode() == 315 || e.getCode() == 354) {
			NickServIdentHandler handler = (NickServIdentHandler)pluginIdent.getIdentHandlerFor(botApp.serverManager.byBot(e), identHandler.id);
			if (handler.requests != 0) {
				if (!handler.isAvailable()) return;
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