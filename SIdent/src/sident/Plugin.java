package sident;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericUserEvent;
import pl.shockah.Pair;
import pl.shockah.json.JSONObject;
import shocky3.BotManager;
import shocky3.JSONUtil;
import shocky3.PluginInfo;
import shocky3.pircbotx.Bot;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Plugin extends shocky3.Plugin {
	public static final Pattern
		REGEX_IDENT_FORMATTER = Pattern.compile(".*%([^%]+?)%.*");
	
	public final Map<BotManager, List<IdentHandler>> identHandlers = Collections.synchronizedMap(new HashMap<BotManager, List<IdentHandler>>());
	public final Map<BotManager, List<IdentGroup>> identGroups = Collections.synchronizedMap(new HashMap<BotManager, List<IdentGroup>>());
	public IdentHandler handlerServer, handlerNick, handlerHost;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		identHandlers.put(null, Collections.synchronizedList(new LinkedList<IdentHandler>()));
		identGroups.put(null, Collections.synchronizedList(new LinkedList<IdentGroup>()));
		add(
			handlerServer = new ServerIdentHandler(),
			handlerNick = new NickIdentHandler(),
			handlerHost = new HostIdentHandler()
		);
		
		readConfig();
	}
	
	protected void onBotStarted(BotManager manager, Bot bot) {
		if (!identHandlers.containsKey(manager)) {
			identHandlers.put(manager, Collections.synchronizedList(new LinkedList<IdentHandler>()));
		}
		List<IdentHandler> handlers = identHandlers.get(manager);
		for (IdentHandler h : identHandlers.get(null)) {
			IdentHandler copy = h.copy(manager);
			if (!handlers.contains(copy)) {
				handlers.add(copy);
			}
		}
	}
	
	public void add(IdentHandler... hs) {
		for (Map.Entry<BotManager, List<IdentHandler>> entry : identHandlers.entrySet()) {
			for (IdentHandler h : hs) {
				IdentHandler copy = h.copy(entry.getKey());
				if (!entry.getValue().contains(copy)) {
					entry.getValue().add(copy);
				}
			}
		}
	}
	public void remove(IdentHandler... hs) {
		for (Map.Entry<BotManager, List<IdentHandler>> entry : identHandlers.entrySet()) {
			for (IdentHandler h : hs) {
				IdentHandler copy = h.copy(entry.getKey());
				entry.getValue().remove(copy);
			}
		}
	}
	
	public void prepare(BotManager manager) {
		if (manager == null) return;
		
		if (!identHandlers.containsKey(manager)) {
			identHandlers.put(manager, Collections.synchronizedList(new LinkedList<IdentHandler>()));
		}
		List<IdentHandler> listNull = identHandlers.get(null);
		List<IdentHandler> list = identHandlers.get(manager);
		for (IdentHandler handler : listNull) {
			IdentHandler copy = handler.copy(manager);
			if (!list.contains(copy)) {
				list.add(copy);
			}
		}
	}
	
	public IdentHandler getIdentHandlerFor(BotManager manager, String account) {
		prepare(manager);
		int index = account.indexOf(':');
		String id = index == -1 ? account : account.substring(0, index);
		for (IdentHandler h : identHandlers.get(manager)) {
			if (h.id.equals(id)) {
				return h;
			}
		}
		if (manager != null) {
			for (IdentHandler h : identHandlers.get(null)) {
				if (h.id.equals(id)) {
					IdentHandler newh = h.copy(manager);
					identHandlers.get(manager).add(newh);
					return newh;
				}
			}
		}
		return null;
	}
	
	public List<IdentGroup> userIdentGroups(Event<Bot> e, User user) {
		return userIdentGroups(e.getBot(), user);
	}
	public List<IdentGroup> userIdentGroups(GenericUserEvent<Bot> e) {
		return userIdentGroups(e.getBot(), e.getUser());
	}
	public List<IdentGroup> userIdentGroups(Bot bot, User user) {
		return userIdentGroups(botApp.serverManager.byBot(bot), user);
	}
	public List<IdentGroup> userIdentGroups(BotManager manager, User user) {
		List<IdentGroup> list = new LinkedList<>();
		if (manager != null) {
			if (identGroups.containsKey(manager)) {
				for (IdentGroup igroup : identGroups.get(manager)) {
					if (igroup.userBelongs(user)) {
						list.add(igroup);
					}
				}
			}
			manager = null;
		}
		if (manager == null) {
			for (IdentGroup igroup : identGroups.get(null)) {
				if (igroup.userBelongs(user)) {
					list.add(igroup);
				}
			}
		}
		return list;
	}
	
	public List<IdentGroup> permissionIdentGroups(Event<Bot> e, String permission) {
		return permissionIdentGroups(e.getBot(), permission);
	}
	public List<IdentGroup> permissionIdentGroups(Bot bot, String permission) {
		return permissionIdentGroups(botApp.serverManager.byBot(bot), permission);
	}
	public List<IdentGroup> permissionIdentGroups(BotManager manager, String permission) {
		List<IdentGroup> list = new LinkedList<>();
		if (manager != null) {
			if (identGroups.containsKey(manager)) {
				for (IdentGroup igroup : identGroups.get(manager)) {
					if (igroup.hasPermission(permission)) {
						list.add(igroup);
					}
				}
			}
			manager = null;
		}
		if (manager == null) {
			for (IdentGroup igroup : identGroups.get(null)) {
				if (igroup.hasPermission(permission)) {
					list.add(igroup);
				}
				
			}
		}
		return list;
	}
	
	public boolean userHasPermission(Event<Bot> e, User user, String... permissions) {
		return userHasPermission(e.getBot(), user, permissions);
	}
	public boolean userHasPermission(GenericUserEvent<Bot> e, String... permissions) {
		return userHasPermission(e.getBot(), e.getUser(), permissions);
	}
	public boolean userHasPermission(Bot bot, User user, String... permissions) {
		return userHasPermission(botApp.serverManager.byBot(bot), user, permissions);
	}
	public boolean userHasPermission(BotManager manager, User user, String... permissions) {
		prepare(manager);
		List<Pair<IdentHandler, String>> list = new LinkedList<>();
		for (String permission : permissions) {
			for (IdentGroup igroup : permissionIdentGroups(manager, permission)) {
				for (String ident : igroup.idents) {
					IdentHandler handler = getIdentHandlerFor(igroup.manager, ident);
					if (handler != null) {
						list.add(new Pair<>(handler, ident));
					}
				}
			}
		}
		Collections.sort(list, new Comparator<Pair<IdentHandler, String>>(){
			public int compare(Pair<IdentHandler, String> p1, Pair<IdentHandler, String> p2) {
				return IdentHandler.comparatorOverhead.compare(p1.get1(), p2.get1());
			}
		});
		
		for (Pair<IdentHandler, String> p : list) {
			if (p.get1().isAvailable() && p.get1().isAccount(user, p.get2().split(":")[1])) {
				return true;
			}
		}
		return false;
	}
	
	public boolean userHasPermission(Event<Bot> e, User user, shocky3.Plugin plugin, String... permissions) {
		return userHasPermission(e.getBot(), user, plugin, permissions);
	}
	public boolean userHasPermission(GenericUserEvent<Bot> e, shocky3.Plugin plugin, String... permissions) {
		return userHasPermission(e.getBot(), e.getUser(), plugin, permissions);
	}
	public boolean userHasPermission(Bot bot, User user, shocky3.Plugin plugin, String... permissions) {
		return userHasPermission(botApp.serverManager.byBot(bot), user, plugin, permissions);
	}
	public boolean userHasPermission(BotManager manager, User user, shocky3.Plugin plugin, String... permissions) {
		for (int i = 0; i < permissions.length; i++) {
			permissions[i] = String.format("%s.%s", plugin.pinfo.internalName(), permissions[i]);
		}
		return userHasPermission(manager, user, permissions);
	}
	
	public String formatIdent(User user, String format, Object... args) {
		BotManager manager = botApp.serverManager.byBot(user);
		prepare(manager);
		
		List<Pair<IdentHandler, String>> list = new LinkedList<>();
		for (IdentHandler handler : identHandlers.get(manager)) {
			if (handler.isAvailable()) {
				list.add(new Pair<>(handler, handler.account(user)));
			}
		}
		
		return formatIdent(list, format, args);
	}
	public String formatIdent(List<Pair<IdentHandler, String>> list, String format, Object... args) {
		String cur = format;
		Matcher m;
		while ((m = REGEX_IDENT_FORMATTER.matcher(cur)).find()) {
			boolean didChange = false;
			String hid = m.group(1);
			
			if (hid.startsWith("arg")) {
				hid = hid.substring(3);
				try {
					int index = Integer.parseInt(hid);
					if (index < args.length) {
						cur = m.replaceAll(args[index] == null ? "null" : args[index].toString());
						continue;
					}
				} catch (Exception e) {}
			}
			
			if (hid.equals("_")) {
				StringBuilder sb = new StringBuilder();
				for (Pair<IdentHandler, String> pair : list) {
					sb.append(String.format(", %s: %s", pair.get1().name, pair.get2()));
				}
				cur = m.replaceAll(sb.toString().substring(2));
				break;
			}
			
			ListIterator<Pair<IdentHandler, String>> lit = list.listIterator();
			while (lit.hasNext()) {
				Pair<IdentHandler, String> pair = lit.next();
				if (pair.get1().id.equals(hid)) {
					cur = m.replaceAll(String.format("%s: %s", pair.get1().name, pair.get2()));
					lit.remove();
					break;
				}
			}
			
			if (!didChange) {
				cur = m.replaceAll("");
			}
		}
		
		return cur;
	}
	
	public void readConfig() {
		DBCollection dbc = botApp.collection(this);
		for (DBObject dbo : JSONUtil.all(dbc.find())) {
			JSONObject j = JSONUtil.fromDBObject(dbo);
			
			BotManager manager = botApp.serverManager.byServerName(j.getString("server"));
			IdentGroup igroup = new IdentGroup(this, manager, j.getString("name"));
			for (String s : j.getList("idents").ofStrings()) {
				igroup.idents.add(s);
			}
			for (String s : j.getList("permissions").ofStrings()) {
				igroup.permissions.add(s);
			}
			
			if (!identGroups.containsKey(manager)) {
				identGroups.put(manager, Collections.synchronizedList(new LinkedList<IdentGroup>()));
			}
			identGroups.get(manager).add(igroup);
		}
	}
}