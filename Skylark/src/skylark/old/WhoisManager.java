package skylark.old;

import java.util.LinkedList;
import java.util.List;
import org.pircbotx.User;
import pl.shockah.Box;
import pl.shockah.Util;
import pl.shockah.func.Action1;
import skylark.old.pircbotx.Bot;
import skylark.old.pircbotx.CustomListenerAdapter;
import skylark.old.pircbotx.event.Whois2Event;
import skylark.old.util.Synced;

public class WhoisManager extends CustomListenerAdapter {
	public static final long
		DEFAULT_SYNC_REQUEST_TIMEOUT = 5000,
		DEFAULT_SYNC_REQUEST_RETRY_TIME = 20;
	
	public final Bot bot;
	public final BotManager manager;
	
	protected final List<Request> userRequests = Synced.list(new LinkedList<>());
	
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
		long current = System.currentTimeMillis();
		
		Box<Whois2Event> box = new Box<>();
		asyncRequestForUser(nick, e -> {
			box.value = e;
		});
		do {
			long ncurrent = System.currentTimeMillis();
			if (ncurrent - current >= timeout)
				return null;
			Util.sleep(retryTime);
		} while (box.value == null);
		return box.value;
	}
	
	public void onWhois2(Whois2Event e) {
		Synced.iterate(userRequests, (request, ith) -> {
			if (e.getNick().equalsIgnoreCase(request.nick)) {
				request.func.f(e);
				ith.remove();
				ith.stop();
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