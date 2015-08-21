package skylark.privileges;

import java.util.List;
import java.util.regex.Pattern;
import org.pircbotx.User;
import pl.shockah.json.JSONList;
import pl.shockah.json.JSONObject;
import skylark.BotManager;
import skylark.ident.IdentMethod;
import skylark.pircbotx.Bot;
import skylark.util.JSON;
import skylark.util.Synced;
import com.mongodb.DBCollection;

public class Group {
	public final Plugin plugin;
	
	public final String name;
	public final List<IdentSet> idents = Synced.list();
	public final List<String> privileges = Synced.list();
	
	public Group(Plugin plugin, String name) {
		this.plugin = plugin;
		this.name = name;
	}
	
	public boolean isMember(User user) {
		synchronized (idents) {
			BotManager manager = user.<Bot>getBot().manager;
			for (IdentSet identSet : idents) {
				boolean all = true;
				for (Ident ident : identSet.idents) {
					IdentMethod method = Plugin.identPlugin.getForID(manager, ident.id);
					String acc = method.getIdentFor(user);
					if (acc == null || !ident.pattern.matcher(acc).find()) {
						all = false;
						break;
					}
				}
				if (all)
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
	
	public void upsert() {
		JSONObject query = JSONObject.make(
			"name", name
		);
		JSONObject j = query.copy();
		
		JSONList<JSONList<?>> jIdents = j.putNewList("idents").ofLists();
		Synced.forEach(idents, identSet -> {
			JSONList<String> jIdentSet = jIdents.addNewList().ofStrings();
			Synced.forEach(identSet.idents, ident -> {
				jIdentSet.add(ident.getFullIdent());
			});
		});
		
		JSONList<String> jPrivileges = j.putNewList("privileges").ofStrings();
		Synced.forEach(privileges, privilege -> {
			jPrivileges.add(privilege);
		});
		
		DBCollection dbc = plugin.botApp.collection(plugin);
		dbc.update(JSON.toDBObject(query), JSON.toDBObject(j), true, false);
	}
	
	public static class IdentSet {
		public final List<Ident> idents = Synced.list();
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
		
		public String getFullIdent() {
			return String.format("%s:%s", id, pattern.pattern());
		}
	}
}