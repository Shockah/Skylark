package skylark.ident.nickserv;

import java.util.Date;
import java.util.Map;
import org.pircbotx.User;
import skylark.BotManager;
import skylark.ident.IdentMethod;
import skylark.ident.IdentMethodFactory;
import skylark.pircbotx.Bot;
import skylark.pircbotx.event.Whois2Event;
import skylark.util.Dates;
import skylark.util.Lazy;
import skylark.util.Synced;

public class NickServIdentMethod extends IdentMethod {
	public static final long
		DEFAULT_TRUST_TIME = 1000l * 60l * 5l;
	
	protected final Plugin plugin;
	protected final Map<User, Entry> cache = Synced.map();
	protected boolean hasWhoX = false;
	protected boolean hasExtendedJoin = false;
	protected boolean hasAccountNotify = false;
	
	protected Lazy<Boolean> available = Lazy.of(this::checkAvailability);
	
	protected NickServIdentMethod(Plugin plugin, BotManager manager, String id, String name) {
		super(manager, id, name, CREDIBILITY_HIGH);
		this.plugin = plugin;
	}
	
	protected NickServIdentMethod(Plugin plugin, BotManager manager, IdentMethodFactory factory) {
		this(plugin, manager, factory.id, factory.name);
	}
	
	public boolean isAvailable() {
		return available.get();
	}
	
	protected boolean checkAvailability() {
		if (manager == null)
			return false;
		
		Bot bot = null;
		synchronized (manager.bots) {
			if (manager.bots.isEmpty())
				manager.connectNewBot();
			bot = manager.bots.get(0);
		}
		
		hasWhoX = bot.getServerInfo().isWhoX();
		hasExtendedJoin = bot.getEnabledCapabilities().contains("extended-join");
		hasAccountNotify = bot.getEnabledCapabilities().contains("account-notify");
		
		Whois2Event whois = bot.whoisManager.syncRequestForUser("NickServ");
		return whois != null && whois.getMessages().length != 0 && whois.getMessages()[0].equals("is a Network Service");
	}
	
	public String getIdentFor(User user) {
		Entry entry = cache.get(user);
		if (entry == null || entry.account == null || (entry.trustedUntil != null && Dates.isInPast(entry.trustedUntil)))
			entry = retrieveFor(user);
		return entry == null ? null : entry.account;
	}
	
	public void putIdentFor(User user, String account, Source source) {
		cache.put(user, new Entry(user, account));
	}
	
	public Entry retrieveFor(User user) {
		//TODO: /msg NickServ acc <user> *
		return null;
	}
	
	public boolean alwaysTrusts() {
		return hasWhoX && hasExtendedJoin && hasAccountNotify;
	}
	
	public class Entry {
		public final User user;
		public final String account;
		public final Date trustedUntil;
		
		public Entry(User user, String account) {
			this(user, account, alwaysTrusts() ? null : Dates.inFuture(plugin.trustTimeSetting.get()));
		}
		
		public Entry(User user, String account, Date trustedUntil) {
			this.user = user;
			this.account = account;
			this.trustedUntil = trustedUntil;
		}
	}
	
	public static enum Source {
		ExtendedJoin, AccountNotify, WhoX;
	}
}