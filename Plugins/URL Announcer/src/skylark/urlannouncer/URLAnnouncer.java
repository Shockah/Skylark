package skylark.urlannouncer;

import pl.shockah.func.Func1;

public abstract class URLAnnouncer {
	public static final int
		PRIORITY_HIGH = 1000,
		PRIORITY_LOW = 0,
		PRIORITY_MEDIUM = (PRIORITY_HIGH + PRIORITY_LOW) / 2,
		PRIORITY_MEDIUM_LOW = (PRIORITY_MEDIUM + PRIORITY_LOW) / 2,
		PRIORITY_MEDIUM_HIGH = (PRIORITY_MEDIUM + PRIORITY_HIGH) / 2;
	
	public final skylark.Plugin plugin;
	public final int priority;
	
	public URLAnnouncer(skylark.Plugin plugin, int priority) {
		this.plugin = plugin;
		this.priority = priority;
	}
	
	public abstract boolean matches(String url);
	
	public abstract String text(String url);
	
	public class Delegate extends URLAnnouncer {
		protected final Func1<String, Boolean> funcMatches;
		protected final Func1<String, String> funcText;
		
		public Delegate(skylark.Plugin plugin, int priority, Func1<String, Boolean> funcMatches, Func1<String, String> funcText) {
			super(plugin, priority);
			this.funcMatches = funcMatches;
			this.funcText = funcText;
		}
		
		public boolean matches(String url) {
			return funcMatches.f(url);
		}
		
		public String text(String url) {
			return funcText.f(url);
		}
	}
}