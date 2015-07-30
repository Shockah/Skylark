package shocky3.ident;

import org.pircbotx.User;
import pl.shockah.func.Func1;
import shocky3.BotManager;

public abstract class IdentMethod {
	public static final int
		CREDIBILITY_HIGH = 1000,
		CREDIBILITY_LOW = 0,
		CREDIBILITY_MEDIUM = (CREDIBILITY_HIGH + CREDIBILITY_LOW) / 2,
		CREDIBILITY_MEDIUM_LOW = (CREDIBILITY_MEDIUM + CREDIBILITY_LOW) / 2,
		CREDIBILITY_MEDIUM_HIGH = (CREDIBILITY_MEDIUM + CREDIBILITY_HIGH) / 2;
	
	public final BotManager manager;
	public final String id;
	public final String name;
	public final int credibility;
	
	protected IdentMethod(BotManager manager, String id, String name, int credibility) {
		this.manager = manager;
		this.id = id;
		this.name = name;
		this.credibility = credibility;
	}
	
	protected IdentMethod(BotManager manager, IdentMethodFactory factory, int credibility) {
		this(manager, factory.id, factory.name, credibility);
	}
	
	public abstract String getIdentFor(User user);
	
	public final String getFullIdentFor(User user) {
		return String.format("%s:%s", id, getIdentFor(user));
	}
	
	public boolean isAvailable() {
		return true;
	}
	
	public static class Delegate extends IdentMethod {
		protected final Func1<User, String> func;
		
		public Delegate(BotManager manager, String id, String name, int credibility, Func1<User, String> func) {
			super(manager, id, name, credibility);
			this.func = func;
		}
		
		public Delegate(BotManager manager, IdentMethodFactory factory, int credibility, Func1<User, String> func) {
			super(manager, factory, credibility);
			this.func = func;
		}
		
		public String getIdentFor(User user) {
			return func.f(user);
		}
	}
}