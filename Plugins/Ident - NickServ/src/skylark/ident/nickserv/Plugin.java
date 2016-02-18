package skylark.ident.nickserv;

import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import pl.shockah.Box;
import skylark.ident.IdentMethodFactory;
import skylark.old.BotManager;
import skylark.old.PluginInfo;
import skylark.old.pircbotx.Bot;
import skylark.old.pircbotx.event.AccountNotifyEvent;
import skylark.old.pircbotx.event.ExtendedJoinEvent;
import skylark.old.util.Synced;
import skylark.settings.Setting;

public class Plugin extends skylark.old.ListenerPlugin {
	public static final String
		TRUST_TIME_KEY = "TrustTime";
	
	public static final String
		IDENT_METHOD_ID = "nickserv",
		IDENT_METHOD_NAME = "NickServ account";
	
	@Dependency
	protected static skylark.settings.Plugin settingsPlugin;
	@Dependency
	protected static skylark.ident.Plugin identPlugin;
	
	protected Setting<Long> trustTimeSetting;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		trustTimeSetting = settingsPlugin.<Long>getSetting(this, TRUST_TIME_KEY);
		trustTimeSetting.putDefault(NickServIdentMethod.DEFAULT_TRUST_TIME);
		
		identPlugin.register(
			new IdentMethodFactory.Delegate(IDENT_METHOD_ID, IDENT_METHOD_NAME,
				(factory, manager) -> new NickServIdentMethod(this, manager, factory))
		);
	}
	
	protected void postLoad() {
		new Thread(() -> {
			Synced.forEach(botApp.serverManager.botManagers, manager -> {
				NickServIdentMethod method = identPlugin.getForClass(manager, NickServIdentMethod.class);
				if (method != null) {
					synchronized (manager.bots) {
						if (manager.inAnyChannels())
							if (method.isAvailable() && method.hasWhoX) {
								for (Bot bot : manager.bots)
									for (Channel channel : bot.getUserBot().getChannels())
										bot.sendRaw().rawLine(String.format("WHO %s %%na", channel.getName()));
							}
					}
				}
			});
		});
	}
	
	protected void onExtendedJoin(ExtendedJoinEvent e) {
		BotManager manager = e.<Bot>getBot().manager;
		NickServIdentMethod method = identPlugin.getForID(manager, IDENT_METHOD_ID);
		if (method != null && method.isAvailable())
			method.putIdentFor(e.getUser().getNick(), e.getAccount(), NickServIdentMethod.Source.ExtendedJoin);
	}
	
	protected void onAccountNotify(AccountNotifyEvent e) {
		BotManager manager = e.<Bot>getBot().manager;
		NickServIdentMethod method = identPlugin.getForID(manager, IDENT_METHOD_ID);
		if (method != null && method.isAvailable())
			method.putIdentFor(e.getUser().getNick(), e.getAccount(), NickServIdentMethod.Source.AccountNotify);
	}
	
	protected void onServerResponse(ServerResponseEvent e) {
		if (e.getCode() == 354) {
			NickServIdentMethod method = identPlugin.getForClass(e.<Bot>getBot().manager, NickServIdentMethod.class);
			if (method != null && method.isAvailable()) {
				List<String> list = e.getParsedResponse();
				if (list.size() == 3)
					method.putIdentFor(list.get(1), list.get(2), NickServIdentMethod.Source.WhoX);
			}
		}
	}
	
	protected void onNickChange(NickChangeEvent e) {
		NickServIdentMethod method = identPlugin.getForClass(e.<Bot>getBot().manager, NickServIdentMethod.class);
		if (method != null && method.isAvailable())
			method.userNickChanged(e.getOldNick(), e.getNewNick());
	}
	
	protected void onQuit(QuitEvent e) {
		NickServIdentMethod method = identPlugin.getForClass(e.<Bot>getBot().manager, NickServIdentMethod.class);
		if (method != null && method.isAvailable())
			method.userQuit(e.getUser());
	}
	
	protected void onJoin(JoinEvent e) {
		NickServIdentMethod method = identPlugin.getForClass(e.<Bot>getBot().manager, NickServIdentMethod.class);
		if (method != null && method.isAvailable()) {
			if (e.getBot().getUserBot().equals(e.getUser()))
				e.getBot().sendRaw().rawLine(String.format("WHO %s %%na", e.getChannel().getName()));
			else if (!method.hasExtendedJoin)
				method.retrieveFor(e.getUser());
		}
	}
	
	protected void onPart(PartEvent e) {
		BotManager manager = e.<Bot>getBot().manager;
		NickServIdentMethod method = identPlugin.getForClass(manager, NickServIdentMethod.class);
		if (method != null && method.isAvailable()) {
			Box<Boolean> foundUser = new Box<Boolean>(false);
			Synced.iterate(manager.bots, (bot, ith) -> {
				for (Channel channel : bot.getUserBot().getChannels()) {
					if (channel.getUsers().contains(e.getUser())) {
						foundUser.value = true;
						ith.stop();
						break;
					}
				}
			});
			if (!foundUser.value)
				method.userQuit(e.getUser());
		}
	}
}