package sident.ns;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.WhoisEvent;
import pl.shockah.Util;
import shocky3.BotManager;
import sident.IdentHandler;

public class NickServIdentHandler extends IdentHandler {
	public static final int
		MAX_WAIT_TIME = 1000 * 3, //3 seconds
		RECHECK_DELAY = 1000 * 60 * 5; //5 minutes
	
	public final Plugin plugin;
	protected WhoisEvent<PircBotX> whois = null;
	protected Map<String, UserEntry> map = Collections.synchronizedMap(new HashMap<String, UserEntry>());
	protected boolean availableWHOX = false, availableExtendedJoin = false, availableAccountNotify = false;
	protected int requests = 0;
	
	public NickServIdentHandler(Plugin plugin) {
		this(plugin, null);
	}
	public NickServIdentHandler(Plugin plugin, BotManager manager) {
		super(manager, "ns", "NickServ", IdentHandler.OVERHEAD_MEDIUM);
		this.plugin = plugin;
	}
	
	public IdentHandler copy(BotManager manager) {
		return new NickServIdentHandler(plugin, manager);
	}
	
	public boolean shouldTrust() {
		return availableWHOX && availableExtendedJoin && availableAccountNotify;
	}
	
	public void setAccount(String nick, String account) {
		setAccount(nick, account, shouldTrust());
	}
	public void setAccount(String nick, String account, boolean trust) {
		if (account != null) {
			if (account.equals("0") || account.equals("*")) {
				account = null;
			}
		}
		nick = nick.toLowerCase();
		
		if (map.containsKey(nick)) {
			map.remove(nick);
		}
		map.put(nick, new UserEntry(account, trust));
	}
	
	protected void onServerResponseEntry(String nick, String account) {
		setAccount(nick, account);
	}
	protected void onServerResponseEnd() {
		requests = Math.max(requests - 1, 0);
	}
	
	public boolean checkAvailability() {
		if (manager == null) return false;
		if (manager.bots.isEmpty()) {
			manager.connectNewBot();
		}
		
		PircBotX bot = manager.bots.get(0);
		availableWHOX = bot.getServerInfo().isWhoX();
		availableExtendedJoin = bot.getEnabledCapabilities().contains("extended-join");
		availableAccountNotify = bot.getEnabledCapabilities().contains("account-notify");
		
		whois = null;
		long sentAt = System.currentTimeMillis();
		bot.sendRaw().rawLine("WHOIS NickServ");
		while (whois == null) {
			long now = System.currentTimeMillis();
			if (now - sentAt >= MAX_WAIT_TIME) return false;
			Util.sleep(50);
		}
		
		return whois != null;
	}
	
	public String account(User user) {
		String nick = user.getNick().toLowerCase();
		if (map.containsKey(nick)) {
			UserEntry ue = map.get(nick);
			if (ue.isStillValid()) {
				return ue.acc;
			} else {
				map.remove(nick);
			}
		}
		
		if (manager.bots.isEmpty()) {
			manager.connectNewBot();
		}
		PircBotX bot = manager.bots.get(0);
		long sentAt = System.currentTimeMillis();
		bot.sendIRC().message("NickServ", String.format("acc %s *", nick));
		while (!map.containsKey(nick)) {
			long now = System.currentTimeMillis();
			if (now - sentAt >= MAX_WAIT_TIME) return null;
			Util.sleep(50);
		}
		
		UserEntry ue = map.get(nick);
		if (ue.acc == null) {
			map.remove(nick);
		}
		return ue.acc;
	}
	
	public boolean isAccount(User user, String account) {
		String acc = account(user);
		return Util.equals(acc, account);
	}
	
	public static class UserEntry {
		public final String acc;
		public final long checkTime;
		public final boolean trust;
		
		public UserEntry(String acc, boolean trust) {
			this.acc = acc;
			checkTime = System.currentTimeMillis();
			this.trust = trust;
		}
		
		public boolean isStillValid() {
			if (trust) return true;
			if (acc == null) return false;
			long now = System.currentTimeMillis();
			return now - checkTime < RECHECK_DELAY;
		}
	}
}