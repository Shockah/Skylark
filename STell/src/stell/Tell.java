package stell;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.User;
import pl.shockah.Pair;
import shocky3.BotManager;
import shocky3.TimeDuration;
import sident.IdentHandler;

public class Tell {
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