package skylark;

import java.util.LinkedList;
import java.util.List;
import org.pircbotx.User;
import org.pircbotx.hooks.events.WhoisEvent;
import pl.shockah.Box;
import pl.shockah.Util;
import pl.shockah.func.Action1;
import skylark.pircbotx.Bot;
import skylark.pircbotx.CustomListenerAdapter;
import skylark.util.Synced;

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
	
	public void asyncRequestForUser(User user, Action1<WhoisEvent> f) {
		asyncRequestForUser(user.getNick(), f);
	}
	
	public void asyncRequestForUser(String nick, Action1<WhoisEvent> f) {
		userRequests.add(new Request(nick, f));
		bot.sendRaw().rawLine(String.format("WHOIS %s", nick));
	}
	
	public WhoisEvent syncRequestForUser(User user) {
		return syncRequestForUser(user.getNick(), DEFAULT_SYNC_REQUEST_TIMEOUT, DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public WhoisEvent syncRequestForUser(User user, long timeout) {
		return syncRequestForUser(user.getNick(), timeout, DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public WhoisEvent syncRequestForUser(User user, long timeout, long retryTime) {
		return syncRequestForUser(user.getNick(), timeout, retryTime);
	}
	
	public WhoisEvent syncRequestForUser(String nick) {
		return syncRequestForUser(nick, DEFAULT_SYNC_REQUEST_TIMEOUT, DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public WhoisEvent syncRequestForUser(String nick, long timeout) {
		return syncRequestForUser(nick, timeout, DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public WhoisEvent syncRequestForUser(String nick, long timeout, long retryTime) {
		long current = System.currentTimeMillis();
		
		Box<WhoisEvent> box = new Box<>();
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
	
	public void onWhois(WhoisEvent e) {
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
		public final Action1<WhoisEvent> func;
		
		public Request(String nick, Action1<WhoisEvent> func) {
			this.nick = nick;
			this.func = func;
		}
	}
}