package me.shockah.skylark;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import me.shockah.skylark.event.Whois2Event;
import me.shockah.skylark.func.Action1;
import me.shockah.skylark.util.Box;
import me.shockah.skylark.util.ReadWriteList;
import org.pircbotx.User;

public class WhoisManager extends SkylarkListenerAdapter {
	public static final long DEFAULT_SYNC_REQUEST_TIMEOUT = 5000;
	public static final long DEFAULT_SYNC_REQUEST_RETRY_TIME = 20;
	
	public final Bot bot;
	public final BotManager manager;
	
	protected final ReadWriteList<Request> userRequests = new ReadWriteList<>(new ArrayList<>());
	
	public WhoisManager(Bot bot) {
		this.bot = bot;
		manager = bot.manager;
	}
	
	public void asyncRequestForUser(User user, Action1<Whois2Event> f) {
		asyncRequestForUser(user.getNick(), f);
	}
	
	public void asyncRequestForUser(String nick, Action1<Whois2Event> f) {
		userRequests.add(new Request(nick, f));
		bot.sendRaw().rawLine(String.format("WHOIS %s", nick));
	}
	
	public Whois2Event syncRequestForUser(User user) {
		return syncRequestForUser(user.getNick(), DEFAULT_SYNC_REQUEST_TIMEOUT, DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public Whois2Event syncRequestForUser(User user, long timeout) {
		return syncRequestForUser(user.getNick(), timeout, DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public Whois2Event syncRequestForUser(User user, long timeout, long retryTime) {
		return syncRequestForUser(user.getNick(), timeout, retryTime);
	}
	
	public Whois2Event syncRequestForUser(String nick) {
		return syncRequestForUser(nick, DEFAULT_SYNC_REQUEST_TIMEOUT, DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public Whois2Event syncRequestForUser(String nick, long timeout) {
		return syncRequestForUser(nick, timeout, DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public Whois2Event syncRequestForUser(String nick, long timeout, long retryTime) {
		CountDownLatch latch = new CountDownLatch(1);
		Box<Whois2Event> box = new Box<>();
		asyncRequestForUser(nick, e -> {
			box.value = e;
			latch.countDown();
		});
		try {
			latch.await(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
		}
		return box.value;
	}
	
	public void onWhois2(Whois2Event e) {
		userRequests.iterateAndWrite((request, it) -> {
			if (e.getNick().equalsIgnoreCase(request.nick)) {
				request.func.call(e);
				it.remove();
				it.stop();
			}
		});
	}
	
	public static class Request {
		public final String nick;
		public final Action1<Whois2Event> func;
		
		public Request(String nick, Action1<Whois2Event> func) {
			this.nick = nick;
			this.func = func;
		}
	}
}