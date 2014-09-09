package surlannounce;

import java.util.List;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import pl.shockah.func.Func;
import shocky3.Shocky;
import shocky3.pircbotx.Bot;

public abstract class URLAnnouncer {
	public static enum EPriority {
		Low(1), Medium(2), High(3);
		
		public final int value;
		
		private EPriority(int value) {
			this.value = value;
		}
	}
	
	public final shocky3.Plugin plugin;
	
	public URLAnnouncer(shocky3.Plugin plugin) {
		this.plugin = plugin;
	}
	
	public abstract void provide(List<Pair<Func<String>, EPriority>> candidates, Shocky botApp, MessageEvent<Bot> e, String url);
}