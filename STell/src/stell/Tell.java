package stell;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.User;
import pl.shockah.Pair;
import pl.shockah.json.JSONList;
import pl.shockah.json.JSONObject;
import shocky3.BotManager;
import shocky3.JSONUtil;
import shocky3.Shocky;
import shocky3.TimeDuration;
import sident.IdentHandler;
import com.mongodb.DBCollection;

public class Tell {
	public static int nextID = 0;
	
	public static Tell read(Shocky botApp, JSONObject j) {
		int id = j.getInt("tellid");
		Date date = new Date(j.getInt("date") * 1000l);
		String message = j.getString("message");
		
		BotManager managerSender = botApp.serverManager.byServerName(j.getString("serverFrom"));
		BotManager managerReceiver = botApp.serverManager.byServerName(j.getString("serverTo"));
		
		List<Pair<IdentHandler, String>> dataSender = new LinkedList<>();
		for (JSONObject jSender : j.getList("sender").ofObjects()) {
			IdentHandler handler = Plugin.pluginIdent.getIdentHandlerFor(managerSender, jSender.getString("handler"));
			String account = jSender.getString("account");
			dataSender.add(new Pair<>(handler, account));
		}
		
		List<Pair<IdentHandler, String>> dataReceiver = new LinkedList<>();
		for (JSONObject jReceiver : j.getList("receiver").ofObjects()) {
			IdentHandler handler = Plugin.pluginIdent.getIdentHandlerFor(managerReceiver, jReceiver.getString("handler"));
			String account = jReceiver.getString("account");
			dataReceiver.add(new Pair<>(handler, account));
		}
		
		return new Tell(id, dataSender, dataReceiver, date, message);
	}
	
	public static JSONObject write(Tell tell) {
		JSONObject j = JSONObject.make(
			"tellid", tell.id,
			"date", (int)(tell.date.getTime() / 1000),
			"message", tell.message,
			"serverFrom", tell.managerSender.name,
			"serverTo", tell.managerReceiver.name
		);
		
		JSONList<JSONObject> jSender = j.putNewList("sender").ofObjects();
		for (Pair<IdentHandler, String> pair : tell.dataSender) {
			jSender.add(JSONObject.make(
				"handler", pair.get1().id,
				"account", pair.get2()
			));
		}
		
		JSONList<JSONObject> jReceiver = j.putNewList("receiver").ofObjects();
		for (Pair<IdentHandler, String> pair : tell.dataReceiver) {
			jReceiver.add(JSONObject.make(
				"handler", pair.get1().id,
				"account", pair.get2()
			));
		}
		
		return j;
	}
	public static void writeDB(shocky3.Plugin plugin, Tell tell) {
		DBCollection dbc = plugin.botApp.collection(plugin.pinfo.internalName());
		removeDB(plugin, tell);
		dbc.insert(JSONUtil.toDBObject(write(tell)));
	}
	
	public static void removeDB(shocky3.Plugin plugin, Tell tell) {
		DBCollection dbc = plugin.botApp.collection(plugin.pinfo.internalName());
		dbc.remove(JSONUtil.toDBObject(JSONObject.make("tellid", tell.id)));
	}
	
	public static void updateIDDB(shocky3.Plugin plugin) {
		DBCollection dbc = plugin.botApp.collection(String.format("%s.Settings", plugin.pinfo.internalName()));
		dbc.update(JSONUtil.toDBObject(new JSONObject()), JSONUtil.toDBObject(JSONObject.make("nextID", nextID)), true, false);
	}
	
	public static List<Pair<IdentHandler, String>> createData(BotManager manager, User user) {
		List<Pair<IdentHandler, String>> list = new LinkedList<>();
		for (IdentHandler handler : Plugin.pluginIdent.identHandlers.get(null)) {
			IdentHandler handler2 = Plugin.pluginIdent.getIdentHandlerFor(manager, handler.id);
			if (handler2.isAvailable()) {
				list.add(new Pair<>(handler2, handler2.account(user)));
			}
		}
		return list;
	}
	public static Tell create(BotManager manager, User sender, List<Pair<IdentHandler, String>> dataReceiver, String message) {
		return create(manager, sender, dataReceiver, new Date(), message);
	}
	public static Tell create(BotManager manager, User sender, List<Pair<IdentHandler, String>> dataReceiver, Date date, String message) {
		return new Tell(createData(manager, sender), dataReceiver, date, message);
	}
	
	public static void sortData(List<Pair<IdentHandler, String>> data) {
		Collections.sort(data, new Comparator<Pair<IdentHandler, String>>(){
			public int compare(Pair<IdentHandler, String> p1, Pair<IdentHandler, String> p2) {
				boolean isSrv1 = p1.get1().id.equals(Plugin.pluginIdent.handlerServer.id);
				boolean isSrv2 = p2.get1().id.equals(Plugin.pluginIdent.handlerServer.id);
				if (isSrv1 && isSrv2) return 0;
				if (isSrv1 != isSrv2) return isSrv1 ? -1 : 1;
				
				if (p1.get1().userFriendly != p2.get1().userFriendly) {
					return p1.get1().userFriendly ? -1 : 1;
				}
				if (p1.get1().credibility != p2.get1().credibility) {
					return Integer.compare(p2.get1().credibility, p1.get1().credibility);
				}
				return 0;
			}
		});
	}
	
	public final int id;
	public final List<Pair<IdentHandler, String>>
		dataSender = new LinkedList<>(),
		dataReceiver = new LinkedList<>();
	public final Date date;
	public final String message;
	public final BotManager managerSender, managerReceiver;
	
	public Tell(List<Pair<IdentHandler, String>> dataSender, List<Pair<IdentHandler, String>> dataReceiver, String message) {
		this(dataSender, dataReceiver, new Date(), message);
	}
	public Tell(List<Pair<IdentHandler, String>> dataSender, List<Pair<IdentHandler, String>> dataReceiver, Date date, String message) {
		this(nextID++, dataSender, dataReceiver, date, message);
	}
	protected Tell(int id, List<Pair<IdentHandler, String>> dataSender, List<Pair<IdentHandler, String>> dataReceiver, Date date, String message) {
		this.id = id;
		sortData(dataSender);
		sortData(dataReceiver);
		this.dataSender.addAll(dataSender);
		this.dataReceiver.addAll(dataReceiver);
		this.date = date;
		this.message = message;
		managerSender = dataSender.get(0).get1().manager;
		managerReceiver = dataReceiver.get(0).get1().manager;
	}
	
	public boolean matches(BotManager manager, User user) {
		if (manager != this.managerReceiver) return false;
		for (Pair<IdentHandler, String> pair : dataReceiver) {
			if (!pair.get1().account(user).equals(pair.get2())) return false;
		}
		return true;
	}
	
	public String buildMessage() {
		String server = null, nick = null;
		
		for (Pair<IdentHandler, String> pair : dataSender) {
			if (managerSender != managerReceiver) {
				if (pair.get1().id.equals(Plugin.pluginIdent.handlerServer.id)) {
					server = pair.get2();
				}
			}
			if (pair.get1().id.equals(Plugin.pluginIdent.handlerNick.id)) {
				nick = pair.get2();
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (Pair<IdentHandler, String> pair : dataSender) {
			if (!pair.get1().id.equals(Plugin.pluginIdent.handlerServer.id) && !pair.get1().id.equals(Plugin.pluginIdent.handlerNick.id)) {
				sb.append(", ");
				sb.append(String.format("%s: %s", pair.get1().name, pair.get2()));
			}
		}
		return String.format("[%s] <%s%s> %s\nAdditional info: %s", TimeDuration.format(date) + " ago", nick, server == null ? "" : "@" + server, message, sb.toString().substring(2));
	}
}