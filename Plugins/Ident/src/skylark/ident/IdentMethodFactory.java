package skylark.ident;

import pl.shockah.func.Func2;
import skylark.old.BotManager;

public abstract class IdentMethodFactory {
	public final String id;
	public final String name;
	
	public IdentMethodFactory(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public abstract IdentMethod create(BotManager manager);
	
	public static class Delegate extends IdentMethodFactory {
		protected final Func2<IdentMethodFactory, BotManager, IdentMethod> func;
		
		public Delegate(String id, String name, Func2<IdentMethodFactory, BotManager, IdentMethod> func) {
			super(id, name);
			this.func = func;
		}

		public IdentMethod create(BotManager manager) {
			return func.f(this, manager);
		}
	}
}