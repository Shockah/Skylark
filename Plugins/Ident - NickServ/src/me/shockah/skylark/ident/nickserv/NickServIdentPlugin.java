package me.shockah.skylark.ident.nickserv;

import java.util.List;
import me.shockah.skylark.Bot;
import me.shockah.skylark.event.AccountNotifyEvent;
import me.shockah.skylark.event.ExtendedJoinEvent;
import me.shockah.skylark.ident.IdentMethodFactory;
import me.shockah.skylark.ident.IdentPlugin;
import me.shockah.skylark.ident.IdentService;
import me.shockah.skylark.plugin.ListenerPlugin;
import me.shockah.skylark.plugin.PluginManager;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;

public class NickServIdentPlugin extends ListenerPlugin {
	@Dependency
	protected IdentPlugin identPlugin;
	
	private IdentMethodFactory factory;
	
	public NickServIdentPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		identPlugin.register(factory = new NickServIdentMethod.Factory());
	}
	
	@Override
	protected void onUnload() {
		identPlugin.unregister(factory);
	}
	
	public IdentMethodFactory getIdentMethodFactory() {
		return factory;
	}
	
	@Override
	protected void onNotice(NoticeEvent e) {
		if (!e.getUser().getNick().equals("NickServ"))
			return;
		
		Bot bot = e.getBot();
		IdentService service = bot.manager.getService(IdentService.class);
		NickServIdentMethod method = service.getMethod(NickServIdentMethod.class);
		if (method == null || !method.isAvailable())
			return;
		
		String[] spl = e.getMessage().split(" ");
		if (spl[1].equals("->") && (!spl[2].equals("0") && !spl[2].equals("*")) && spl[4].equals("3"))
			method.onNickServNotice(spl[0], spl[2]);
		else
			method.onNickServNotice(spl[0], null);
	}
	
	@Override
	protected void onAccountNotify(AccountNotifyEvent e) {
		Bot bot = e.getBot();
		IdentService service = bot.manager.getService(IdentService.class);
		NickServIdentMethod method = service.getMethod(NickServIdentMethod.class);
		if (method != null && method.isAvailable()) {
			method.onAccountNotify(e.getUser().getNick(), e.getAccount());
		}
	}
	
	@Override
	protected void onExtendedJoin(ExtendedJoinEvent e) {
		Bot bot = e.getBot();
		IdentService service = bot.manager.getService(IdentService.class);
		NickServIdentMethod method = service.getMethod(NickServIdentMethod.class);
		if (method != null && method.isAvailable())
			method.onExtendedJoin(e.getUser().getNick(), e.getAccount());
	}
	
	@Override
	protected void onJoin(JoinEvent e) {
		Bot bot = e.getBot();
		IdentService service = bot.manager.getService(IdentService.class);
		NickServIdentMethod method = service.getMethod(NickServIdentMethod.class);
		if (method == null || !method.isAvailable())
			return;
		
		if (e.getUser() == e.getBot().getUserBot())
			e.getBot().sendRaw().rawLine(String.format("WHO %s %%na", e.getChannel().getName()));
	}
	
	@Override
	protected void onNickChange(NickChangeEvent e) {
		Bot bot = e.getBot();
		IdentService service = bot.manager.getService(IdentService.class);
		NickServIdentMethod method = service.getMethod(NickServIdentMethod.class);
		if (method != null && method.isAvailable())
			method.onNickChange(e.getOldNick(), e.getNewNick());
	}
	
	@Override
	protected void onQuit(QuitEvent e) {
		Bot bot = e.getBot();
		IdentService service = bot.manager.getService(IdentService.class);
		NickServIdentMethod method = service.getMethod(NickServIdentMethod.class);
		if (method != null && method.isAvailable())
			method.onQuit(e.getUser().getNick());
	}
	
	@Override
	protected void onPart(PartEvent e) {
		Bot bot = e.getBot();
		IdentService service = bot.manager.getService(IdentService.class);
		NickServIdentMethod method = service.getMethod(NickServIdentMethod.class);
		if (method == null || !method.isAvailable())
			return;
		
		String nick = e.getUser().getNick();
		Bot foundBot = bot.manager.bots.findOne(bot2 -> {
			for (Channel channel : bot2.getUserBot().getChannels()) {
				if (channel.getUsersNicks().contains(nick))
					return true;
			}
			return false;
		});
		if (foundBot == null)
			method.onQuit(nick);
	}
	
	@Override
	protected void onServerResponse(ServerResponseEvent e) {
		if (e.getCode() != 354)
			return;
		
		Bot bot = e.getBot();
		IdentService service = bot.manager.getService(IdentService.class);
		NickServIdentMethod method = service.getMethod(NickServIdentMethod.class);
		if (method == null || !method.isAvailable())
			return;
		
		List<String> response = e.getParsedResponse();
		if (response.size() == 3)
			method.onServerResponseEntry(response.get(1), response.get(2));
	}
}