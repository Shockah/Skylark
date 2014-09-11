package skarma;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import pl.shockah.Box;
import pl.shockah.json.JSONObject;
import shocky3.JSONUtil;
import shocky3.PluginInfo;
import shocky3.pircbotx.Bot;
import sident.IdentHandler;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Plugin extends shocky3.ListenerPlugin {
	public static final Pattern
		REGEX_KARMA = Pattern.compile("^([^\\s\\+\\-=]{2,})((?:\\+\\+)|(?:\\-\\-)|(?:==))$");
	
	@Dependency(internalName = "Shocky.Ident") protected static shocky3.Plugin pluginIdent;
	
	public final Map<String, Integer> karma = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		DBCollection dbc = botApp.collection(this);
		synchronized (karma) {
			for (DBObject dbo : JSONUtil.all(dbc.find())) {
				JSONObject j = JSONUtil.fromDBObject(dbo);
				karma.put(j.getString("name"), j.getInt("karma"));
			}
		}
	}
	
	protected void onMessage(MessageEvent<Bot> e) {
		Matcher m = REGEX_KARMA.matcher(e.getMessage());
		if (m.find()) {
			String inputTarget = m.group(1);
			String op = m.group(2);
			
			Box<User> refUser = new Box<>();
			String target = findKarmaTarget(e, inputTarget, refUser);
			if (target != null) {
				synchronized (this.karma) {
					Integer karma = null;
					if (this.karma.containsKey(target)) {
						karma = this.karma.get(target);
					}
					
					boolean existed = karma != null;
					if (karma == null) karma = 0;
					int karmaOld = karma;
					
					switch (op) {
						case "++":
							karma++;
							break;
						case "--":
							karma--;
							break;
					}
					if (karma > karmaOld && refUser != null && e.getUser().equals(refUser.value)) {
						if (e.getChannel().isOp(e.getBot().getUserBot())) {
							e.getChannel().send().kick(e.getUser(), e.getMessage());
							return;
						}
					}
					e.respond(String.format("%s == %d", target.substring(target.indexOf(':') + 1), karma));
					if (karma != karmaOld) {
						this.karma.put(target, karma);
						DBCollection dbc = botApp.collection(pinfo.internalName());
						if (existed) {
							dbc.update(JSONUtil.toDBObject(JSONObject.make(
								"name", target
							)), JSONUtil.toDBObject(JSONObject.make("$set", JSONObject.make(
								"karma", karma
							))));
						} else {
							dbc.insert(JSONUtil.toDBObject(JSONObject.make(
								"name", target,
								"karma", karma
							)));
						}
					}
				}
			}
		}
	}
	
	public String findKarmaTarget(GenericChannelUserEvent<Bot> e, String target) {
		return findKarmaTarget(e, target, null);
	}
	public String findKarmaTarget(GenericChannelUserEvent<Bot> e, String target, Box<User> refUser) {
		if (target.indexOf(':') != -1) {
			return target;
		}
		if (pluginIdent != null) {
			sident.Plugin pluginIdent = (sident.Plugin)Plugin.pluginIdent;
			if (e != null) {
				synchronized (pluginIdent.identHandlers) {L: for (User user : e.getChannel().getUsers()) {
					if (user.getNick().equalsIgnoreCase(target)) {
						if (refUser != null) {
							refUser.value = user;
						}
						List<IdentHandler> handlers = new LinkedList<>(pluginIdent.identHandlers.get(e.getBot().manager));
						Collections.sort(handlers, IdentHandler.comparatorCredibility);
						for (IdentHandler handler : handlers) {
							if (handler.isAvailable()) {
								String account = handler.account(user);
								if (account != null) {
									target = String.format("%s:%s", handler.id, account);
									break L;
								}
							}
						}
					}
				}}
			}
		}
		if (target.indexOf(':') == -1) {
			target = String.format(":%s", target);
		}
		return target;
	}
}