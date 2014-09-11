package sident;

import java.util.Comparator;
import org.pircbotx.User;
import pl.shockah.Box;
import pl.shockah.Util;
import shocky3.BotManager;

public abstract class IdentHandler {
	public static final int
		OVERHEAD_LOW = 0,
		OVERHEAD_MEDIUM = 500,
		OVERHEAD_HIGH = 1000;
	public static final int
		CREDIBILITY_NONE = -1,
		CREDIBILITY_LOW = 0,
		CREDIBILITY_MEDIUM = 500,
		CREDIBILITY_HIGH = 1000;
	
	public static final Comparator<IdentHandler>
		comparatorOverhead = new Comparator<IdentHandler>(){
			public int compare(IdentHandler h1, IdentHandler h2) {
				return Integer.compare(h1.overhead, h2.overhead);
			}
		},
		comparatorCredibility = new Comparator<IdentHandler>(){
			public int compare(IdentHandler h1, IdentHandler h2) {
				return Integer.compare(h2.credibility, h1.credibility);
			}
		};
	
	public final BotManager manager;
	public final String id, name;
	public final int overhead, credibility;
	public final boolean userFriendly;
	protected Box<Boolean> available = null;
	
	public IdentHandler(String id, String name, int overhead, int credibility, boolean userFriendly) {
		this(null, id, name, overhead, credibility, userFriendly);
	}
	public IdentHandler(BotManager manager, String id, String name, int overhead, int credibility, boolean userFriendly) {
		this.manager = manager;
		this.id = id;
		this.name = name;
		this.overhead = overhead;
		this.credibility = credibility;
		this.userFriendly = userFriendly;
	}
	
	public final boolean equals(Object o) {
		if (!(o instanceof IdentHandler)) return false;
		IdentHandler h = (IdentHandler)o;
		return getClass() == h.getClass() && Util.equals(manager, h.manager);
	}
	
	public boolean isAvailable() {
		if (available == null) {
			available = new Box<>(checkAvailability());
		}
		return available.value;
	}
	
	public abstract IdentHandler copy(BotManager manager);
	public abstract boolean checkAvailability();
	public abstract String account(User user);
	public abstract boolean isAccount(User user, String account);
}