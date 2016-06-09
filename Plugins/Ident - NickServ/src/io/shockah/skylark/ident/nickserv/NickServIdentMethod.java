package io.shockah.skylark.ident.nickserv;

import io.shockah.skylark.Bot;
import io.shockah.skylark.event.Whois2Event;
import io.shockah.skylark.func.Action2;
import io.shockah.skylark.ident.IdentMethod;
import io.shockah.skylark.ident.IdentMethodFactory;
import io.shockah.skylark.ident.IdentService;
import io.shockah.skylark.util.Box;
import io.shockah.skylark.util.Dates;
import io.shockah.skylark.util.Lazy;
import io.shockah.skylark.util.ReadWriteList;
import io.shockah.skylark.util.ReadWriteMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.pircbotx.User;

public class NickServIdentMethod extends IdentMethod {
	public static final String METHOD_NAME = "NickServ";
	public static final String METHOD_PREFIX = "ns";
	
	public static final long DEFAULT_SYNC_REQUEST_TIMEOUT = 5000l;
	public static final long DEFAULT_EXPIRATION_TIME = 1000l * 60l * 5l;
	public static final String OPERATOR_STATUS_NETWORK_SERVICE = "Network Service";
	
	protected final Lazy<Boolean> available = Lazy.of(this::checkAvailability);
	protected final ReadWriteMap<String, Entry> cache = new ReadWriteMap<>(new HashMap<>());
	protected final ReadWriteList<Request> userRequests = new ReadWriteList<>(new ArrayList<>());
	
	protected boolean hasWhoX = false;
	protected boolean hasExtendedJoin = false;
	protected boolean hasAccountNotify = false;
	
	public NickServIdentMethod(IdentService service, IdentMethodFactory factory) {
		super(service, factory, METHOD_NAME, METHOD_PREFIX);
	}

	@Override
	public boolean isAvailable() {
		return available.get();
	}
	
	protected boolean checkAvailability() {
		Bot bot = getAnyBot();
		hasWhoX = bot.getServerInfo().isWhoX();
		hasExtendedJoin = bot.getEnabledCapabilities().contains("extended-join");
		hasAccountNotify = bot.getEnabledCapabilities().contains("account-notify");
		
		Whois2Event whois = bot.whoisManager.syncRequest("NickServ");
		return whois != null && OPERATOR_STATUS_NETWORK_SERVICE.equals(whois.getOperatorStatus());
	}
	
	public boolean hasWhoX() {
		available.get();
		return hasWhoX;
	}
	
	public boolean hasExtendedJoin() {
		available.get();
		return hasExtendedJoin;
	}
	
	public boolean hasAccountNotify() {
		available.get();
		return hasAccountNotify;
	}
	
	protected Bot getAnyBot() {
		return service.manager.bots.readOperation(bots -> {
			if (service.manager.bots.isEmpty())
				return service.manager.connectNewBot();
			return service.manager.bots.get(0);
		});
	}

	@Override
	public String getForUser(User user) {
		Entry entry = cache.get(user.getNick());
		if (entry == null || entry.account == null || entry.expired()) {
			String account = syncRequest(user);
			entry = new Entry(account, getNewEntryExpirationDate());
		}
		cache.put(user.getNick(), entry);
		return entry.account;
	}
	
	public void asyncRequest(User user, Action2<String, String> f) {
		userRequests.add(new Request(user.getNick(), f));
		user.getBot().sendIRC().message("NickServ", String.format("acc %s *", user.getNick()));
	}
	
	public void asyncRequest(String nick, Action2<String, String> f) {
		userRequests.add(new Request(nick, f));
		getAnyBot().sendIRC().message("NickServ", String.format("acc %s *", nick));
	}
	
	public String syncRequest(User user) {
		return syncRequest(user.getNick(), DEFAULT_SYNC_REQUEST_TIMEOUT);
	}
	
	public String syncRequest(User user, long timeout) {
		return syncRequest(user.getNick(), timeout);
	}
	
	public String syncRequest(String nick) {
		return syncRequest(nick, DEFAULT_SYNC_REQUEST_TIMEOUT);
	}
	
	public String syncRequest(String nick, long timeout) {
		CountDownLatch latch = new CountDownLatch(1);
		Box<String> box = new Box<>();
		asyncRequest(nick, (responseNick, account) -> {
			box.value = account;
			latch.countDown();
		});
		try {
			latch.await(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
		}
		return box.value;
	}
	
	public void onNickServNotice(String nick, String account) {
		userRequests.iterateAndWrite((request, it) -> {
			if (request.nick.equals(nick)) {
				request.func.call(nick, account);
				it.remove();
				it.stop();
			}
		});
	}
	
	public void onAccountNotify(String nick, String account) {
		putEntry(nick, account);
	}
	
	public void onExtendedJoin(String nick, String account) {
		putEntry(nick, account);
	}
	
	public void onServerResponseEntry(String nick, String account) {
		putEntry(nick, account);
	}
	
	private void putEntry(String nick, String account) {
		cache.put(nick, new Entry(account, getNewEntryExpirationDate()));
	}
	
	public void onNickChange(String oldNick, String newNick) {
		cache.writeOperation(cache -> {
			if (cache.containsKey(oldNick)) {
				Entry entry = cache.get(oldNick);
				cache.remove(oldNick);
				cache.put(newNick, entry);
			}
		});
	}
	
	public void onQuit(String nick) {
		cache.remove(nick);
	}
	
	private Date getNewEntryExpirationDate() {
		return hasWhoX && hasAccountNotify && hasExtendedJoin ? null : new Date(new Date().getTime() + DEFAULT_EXPIRATION_TIME);
	}
	
	private static class Entry {
		public final String account;
		public final Date expirationDate;
		
		public Entry(String account, Date expirationDate) {
			this.account = account;
			this.expirationDate = expirationDate;
		}
		
		public boolean expired() {
			return expirationDate == null ? false : Dates.isInPast(expirationDate);
		}
	}
	
	private static class Request {
		public final String nick;
		public final Action2<String, String> func;
		
		public Request(String nick, Action2<String, String> f) {
			this.nick = nick;
			func = f;
		}
	}
	
	public static class Factory extends IdentMethodFactory {
		public Factory() {
			super(METHOD_NAME, METHOD_PREFIX);
		}

		@Override
		public IdentMethod create(IdentService service) {
			return new NickServIdentMethod(service, this);
		}
	}
}