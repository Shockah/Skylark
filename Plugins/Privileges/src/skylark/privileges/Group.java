package skylark.privileges;

import java.util.List;
import java.util.regex.Pattern;
import org.pircbotx.User;
import skylark.BotManager;
import skylark.ident.IdentMethod;
import skylark.pircbotx.Bot;
import skylark.util.Synced;

public class Group {
	public final Plugin plugin;
	public String name;
	public final List<Ident> idents = Synced.list();
	public final List<String> privileges = Synced.list();
	
	public Group(Plugin plugin, String name) {
		this.plugin = plugin;
		this.name = name;
	}
	
	public boolean isMember(User user) {
		synchronized (idents) {
			BotManager manager = ((Bot)user.getBot()).manager;
			for (Ident ident : idents) {
				IdentMethod method = plugin.identPlugin.getForID(manager, ident.id);
				String acc = method.getIdentFor(user);
				if (ident.pattern.matcher(acc).find())
					return true;
			}
		}
		return false;
	}
	
	public boolean isAllowed(String checkPrivilege) {
		synchronized (privileges) {
			String[] checkSplit = checkPrivilege.split("\\.");
			L: for (String privilege : privileges) {
				String[] privilegeSplit = privilege.split("\\.");
				int minLength = Math.min(checkSplit.length, privilegeSplit.length);
				for (int i = 0; i < minLength; i++) {
					if (privilegeSplit[i].equals("*"))
						return true;
					if (!privilegeSplit[i].equals(checkSplit[i]))
						continue L;
				}
				if (checkSplit.length == privilegeSplit.length)
					return true;
			}
		}
		return false;
	}
	
	public static class Ident {
		public final String id;
		public final Pattern pattern;
		
		public Ident(String id, String regex) {
			this(id, Pattern.compile(regex));
		}
		
		public Ident(String id, Pattern pattern) {
			this.id = id;
			this.pattern = pattern;
		}
	}
}