package skarma;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import pl.shockah.Box;
import pl.shockah.Pair;
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
	
	public final List<Pair<String, Integer>> karma = Collections.synchronizedList(new LinkedList<Pair<String, Integer>>());
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		DBCollection dbc = botApp.collection(this);
		synchronized (karma) {
			for (DBObject dbo : JSONUtil.all(dbc.find())) {
				JSONObject j = JSONUtil.fromDBObject(dbo);
				karma.add(new Pair<>(j.getString("name"), j.getInt("karma")));
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
					int karma = 0;
					Pair<String, Integer> pair = null;
					for (Pair<String, Integer> karmap : this.karma) {
						if (karmap.get1().equals(target)) {
							karma = karmap.get2();
							pair = karmap;
							break;
						}
					}
					
					boolean existed = pair != null;
					if (pair == null) pair = new Pair<>(target, karma);
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
						pair.set2(karma);
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
				L: for (User user : e.getChannel().getUsers()) {
					if (user.getNick().equalsIgnoreCase(target)) {
						if (refUser != null) {
							refUser.value = user;
						}
						synchronized (pluginIdent.identHandlers) {
							List<IdentHandler> handlers = new LinkedList<>(pluginIdent.identHandlers.get(botApp.serverManager.byBot(e)));
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
					}
				}
			}
		}
		if (target.indexOf(':') == -1) {
			target = String.format(":%s", target);
		}
		return target;
	}
}