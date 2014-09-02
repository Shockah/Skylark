package surlannounce;

import java.util.List;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import pl.shockah.func.Func;
import shocky3.Plugin;
import shocky3.Shocky;

public class DefaultURLAnnouncer extends URLAnnouncer {
	public DefaultURLAnnouncer(Plugin plugin) {
		super(plugin);
	}

	public void provide(List<Pair<Func<String>, EPriority>> candidates, Shocky botApp, MessageEvent<PircBotX> e, final String url) {
		candidates.add(new Pair<Func<String>, EPriority>(new Func<String>(){
			public String f() {
				return String.format("[%s]", TitleExtractor.getPageTitle(url));
			}
		}, EPriority.Low));
	}
}