package shocky3.ident;

import org.pircbotx.User;
import pl.shockah.func.Func1;

public abstract class IdentMethod {
	public static final int
		CREDIBILITY_HIGH = 1000,
		CREDIBILITY_LOW = 0,
		CREDIBILITY_MEDIUM = (CREDIBILITY_HIGH + CREDIBILITY_LOW) / 2;
	
	public final String id;
	public final String name;
	public final int credibility;
	
	public IdentMethod(String id, String name, int credibility) {
		this.id = id;
		this.name = name;
		this.credibility = credibility;
	}
	
	public abstract String getIdentFor(User user);
	
	public static class Delegate extends IdentMethod {
		protected final Func1<User, String> func;
		
		public Delegate(String id, String name, int credibility, Func1<User, String> func) {
			super(id, name, credibility);
			this.func = func;
		}
		
		public String getIdentFor(User user) {
			return func.f(user);
		}
	}
}