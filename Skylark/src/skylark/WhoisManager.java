package skylark;

import java.util.LinkedList;
import java.util.List;
import org.pircbotx.User;
import org.pircbotx.hooks.events.WhoisEvent;
import pl.shockah.Pair;
import pl.shockah.func.Action1;
import skylark.pircbotx.Bot;
import skylark.pircbotx.CustomListenerAdapter;
import skylark.util.Synced;

public class WhoisManager extends CustomListenerAdapter {
	public final Bot bot;
	public final BotManager manager;
	
	protected final List<Pair<String, Action1<WhoisEvent>>> userRequests = Synced.list(new LinkedList<>());
	
	public WhoisManager(Bot bot) {
		this.bot = bot;
		manager = bot.manager;
	}
	
	public void requestForUser(User user, Action1<WhoisEvent> f) {
		requestForUser(user.getNick(), f);
	}
	
	public void requestForUser(String nick, Action1<WhoisEvent> f) {
		userRequests.add(new Pair<>(nick, f));
		bot.sendRaw().rawLine(String.format("WHOIS %s", nick));
	}
	
	public void onWhois(WhoisEvent e) {
		synchronized (userRequests) {
			//TODO: loop over userRequests
		}
	}
}