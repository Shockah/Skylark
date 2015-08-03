package skylark.ident.nickserv;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.User;
import pl.shockah.Box;
import pl.shockah.Util;
import pl.shockah.func.Action1;
import skylark.BotManager;
import skylark.WhoisManager;
import skylark.util.Synced;

public class NickServManager {
	public final NickServIdentMethod identMethod;
	public final BotManager manager;
	protected final List<Request> userRequests = Synced.list(new LinkedList<>());
	protected final Pattern accountPattern = Pattern.compile("([a-z0-9_\\-\\[\\]\\\\^{}|`]+) \\-\\> ((?:[a-z0-9_\\-\\[\\]\\\\^{}|`]+)|\\*) ACC ([0-3]).*");
	
	public NickServManager(NickServIdentMethod identMethod) {
		this.identMethod = identMethod;
		manager = identMethod.manager;
	}
	
	public void asyncRequestForUser(User user, Action1<String> f) {
		userRequests.add(new Request(user.getNick(), f));
		user.getBot().sendIRC().message("NickServ", String.format("acc %s *", user.getNick()));
	}
	
	public void asyncRequestForUser(String nick, Action1<String> f) {
		userRequests.add(new Request(nick, f));
		manager.anyBot().sendIRC().message("NickServ", String.format("acc %s *", nick));
	}
	
	public String syncRequestForUser(User user) {
		return syncRequestForUser(user, WhoisManager.DEFAULT_SYNC_REQUEST_TIMEOUT, WhoisManager.DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public String syncRequestForUser(User user, long timeout) {
		return syncRequestForUser(user, timeout, WhoisManager.DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public String syncRequestForUser(String nick) {
		return syncRequestForUser(nick, WhoisManager.DEFAULT_SYNC_REQUEST_TIMEOUT, WhoisManager.DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public String syncRequestForUser(String nick, long timeout) {
		return syncRequestForUser(nick, timeout, WhoisManager.DEFAULT_SYNC_REQUEST_RETRY_TIME);
	}
	
	public String syncRequestForUser(User user, long timeout, long retryTime) {
		long current = System.currentTimeMillis();
		
		Box<String> box = new Box<>();
		asyncRequestForUser(user, account -> {
			box.value = account;
		});
		do {
			long ncurrent = System.currentTimeMillis();
			if (ncurrent - current >= timeout)
				return null;
			Util.sleep(retryTime);
		} while (box.value == null);
		return box.value;
	}
	
	public String syncRequestForUser(String nick, long timeout, long retryTime) {
		long current = System.currentTimeMillis();
		
		Box<String> box = new Box<>();
		asyncRequestForUser(nick, account -> {
			box.value = account;
		});
		do {
			long ncurrent = System.currentTimeMillis();
			if (ncurrent - current >= timeout)
				return null;
			Util.sleep(retryTime);
		} while (box.value == null);
		return box.value;
	}
	
	public void onResponse(String message) {
		Matcher m = accountPattern.matcher(message);
		if (m.find()) {
			String nick = m.group(1);
			String account = m.group(2);
			int accType = Integer.parseInt(m.group(3));
			
			if (accType == 3)
				onAccount(nick, account.equals("*") ? null : account);
			else
				onAccount(nick, null);
		}
	}
	
	public void onAccount(String nick, String account) {
		Synced.iterate(userRequests, (request, ith) -> {
			if (nick.equalsIgnoreCase(request.nick)) {
				request.func.f(account);
				ith.remove();
				ith.stop();
			}
		});
	}
	
	public static class Request {
		public final String nick;
		public final Action1<String> func;
		
		public Request(String nick, Action1<String> func) {
			this.nick = nick;
			this.func = func;
		}
	}
}