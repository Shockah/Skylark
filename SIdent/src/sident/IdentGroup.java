package sident;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.User;
import shocky3.BotManager;
import shocky3.Shocky;

public class IdentGroup {
	public final Shocky botApp;
	public final Plugin plugin;
	public final BotManager manager;
	public final String name;
	public final List<String> idents = Collections.synchronizedList(new LinkedList<String>());
	public final List<String> permissions = Collections.synchronizedList(new LinkedList<String>());
	
	public IdentGroup(Plugin plugin, BotManager manager, String name) {
		this.plugin = plugin;
		this.botApp = plugin.botApp;
		this.manager = manager;
		this.name = name;
	}
	
	public boolean userBelongs(User user) {
		synchronized (idents) {for (String ident : idents) {
			if (ident.equals("*")) {
				return true;
			}
			IdentHandler handler = plugin.getIdentHandlerFor(manager, ident);
			if (handler != null) {
				if (handler.isAvailable()) {
					return handler.isAccount(user, ident.split(":")[1]);
				}
			}
		}}
		return false;
	}
	
	public boolean hasPermission(String permission) {
		String[] spl = permission.split("\\.");
		synchronized (permissions) {for (String permission2 : permissions) {
			String[] spl2 = permission2.split("\\.");
			boolean pass = true;
			for (int i = 0; i < spl.length; i++) {
				String s1 = spl[i];
				if (i >= spl2.length) {
					pass = false;
					break;
				}
				String s2 = spl2[i];
				if (s2.equals("*")) {
					return true;
				} else if (!s1.equals(s2)) {
					pass = false;
					break;
				}
			}
			if (pass) {
				return true;
			}
		}}
		return false;
	}
}