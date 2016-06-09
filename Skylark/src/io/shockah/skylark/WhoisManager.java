package io.shockah.skylark;

import io.shockah.skylark.event.Whois2Event;
import io.shockah.skylark.func.Action1;
import io.shockah.skylark.util.Box;
import io.shockah.skylark.util.ReadWriteList;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.pircbotx.User;

public class WhoisManager extends SkylarkListenerAdapter {
	public static final long DEFAULT_SYNC_REQUEST_TIMEOUT = 5000l;
	
	public final Bot bot;
	public final BotManager manager;
	
	protected final ReadWriteList<Request> userRequests = new ReadWriteList<>(new ArrayList<>());
	
	public WhoisManager(Bot bot) {
		this.bot = bot;
		manager = bot.manager;
	}
	
	public void asyncRequest(User user, Action1<Whois2Event> f) {
		asyncRequest(user.getNick(), f);
	}
	
	public void asyncRequest(String nick, Action1<Whois2Event> f) {
		userRequests.add(new Request(nick, f));
		bot.sendRaw().rawLine(String.format("WHOIS %s", nick));
	}
	
	public Whois2Event syncRequest(User user) {
		return syncRequest(user.getNick(), DEFAULT_SYNC_REQUEST_TIMEOUT);
	}
	
	public Whois2Event syncRequest(User user, long timeout) {
		return syncRequest(user.getNick(), timeout);
	}
	
	public Whois2Event syncRequest(String nick) {
		return syncRequest(nick, DEFAULT_SYNC_REQUEST_TIMEOUT);
	}
	
	public Whois2Event syncRequest(String nick, long timeout) {
		CountDownLatch latch = new CountDownLatch(1);
		Box<Whois2Event> box = new Box<>();
		asyncRequest(nick, e -> {
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
	
	private static class Request {
		public final String nick;
		public final Action1<Whois2Event> func;
		
		public Request(String nick, Action1<Whois2Event> func) {
			this.nick = nick;
			this.func = func;
		}
	}
}