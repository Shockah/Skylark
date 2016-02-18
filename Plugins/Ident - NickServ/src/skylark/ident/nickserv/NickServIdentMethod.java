package skylark.ident.nickserv;

import java.util.Date;
import java.util.Map;
import org.pircbotx.User;
import org.pircbotx.snapshot.UserSnapshot;
import skylark.ident.IdentMethod;
import skylark.ident.IdentMethodFactory;
import skylark.old.BotManager;
import skylark.old.pircbotx.Bot;
import skylark.old.pircbotx.event.Whois2Event;
import skylark.old.util.Dates;
import skylark.old.util.Lazy;
import skylark.old.util.Synced;

public class NickServIdentMethod extends IdentMethod {
	public static final String
		OPERATOR_STATUS_NETWORK_SERVICE = "Network Service";
	
	public static final long
		DEFAULT_TRUST_TIME = 1000l * 60l * 5l;
	
	protected final Plugin plugin;
	protected final NickServManager nickServManager;
	protected final Map<String, Entry> cache = Synced.map();
	protected boolean hasWhoX = false;
	protected boolean hasExtendedJoin = false;
	protected boolean hasAccountNotify = false;
	
	protected Lazy<Boolean> available = Lazy.of(this::checkAvailability);
	
	protected NickServIdentMethod(Plugin plugin, BotManager manager, String id, String name) {
		super(manager, id, name, CREDIBILITY_HIGH);
		this.plugin = plugin;
		nickServManager = new NickServManager(this);
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
		return whois != null && OPERATOR_STATUS_NETWORK_SERVICE.equals(whois.getOperatorStatus());
	}
	
	public String getIdentFor(User user) {
		Entry entry = cache.get(user.getNick());
		if (entry == null || entry.account == null || (entry.trustedUntil != null && Dates.isInPast(entry.trustedUntil)))
			entry = retrieveFor(user);
		return entry == null ? null : entry.account;
	}
	
	public String getIdentFor(String nick) {
		Entry entry = cache.get(nick);
		if (entry == null || entry.account == null || (entry.trustedUntil != null && Dates.isInPast(entry.trustedUntil)))
			return null;
		return entry.account;
	}
	
	public void putIdentFor(String nick, String account, Source source) {
		cache.put(nick, new Entry(nick, account));
	}
	
	public void userNickChanged(String oldNick, String newNick) {
		synchronized (cache) {
			Entry entry = cache.get(oldNick);
			if (entry != null) {
				cache.put(newNick, entry);
				cache.remove(oldNick);
			}
		}
	}
	
	public void userQuit(UserSnapshot user) {
		cache.remove(user.getNick());
	}
	
	public Entry retrieveFor(User user) {
		String account = nickServManager.syncRequestForUser(user);
		return new Entry(user.getNick(), account);
	}
	
	public boolean alwaysTrusts() {
		return hasWhoX && hasExtendedJoin && hasAccountNotify;
	}
	
	public class Entry {
		public final String nick;
		public final String account;
		public final Date trustedUntil;
		
		public Entry(String nick, String account) {
			this(nick, account, alwaysTrusts() ? null : Dates.inFuture(plugin.trustTimeSetting.get()));
		}
		
		public Entry(String nick, String account, Date trustedUntil) {
			this.nick = nick;
			this.account = account;
			this.trustedUntil = trustedUntil;
		}
	}
	
	public static enum Source {
		ExtendedJoin, AccountNotify, WhoX;
	}
}