package skylark.ident.nickserv;

import java.util.Date;
import java.util.Map;
import org.pircbotx.User;
import skylark.BotManager;
import skylark.ident.IdentMethod;
import skylark.ident.IdentMethodFactory;
import skylark.util.Lazy;
import skylark.util.Synced;

public class NickServIdentMethod extends IdentMethod {
	public static final long
		DEFAULT_TRUST_TIME = 1000l * 60l * 5l;
	
	protected final Map<User, Entry> cache = Synced.map();
	protected boolean hasWhoX = false;
	protected boolean hasExtendedJoin = false;
	protected boolean hasAccountNotify = false;
	
	protected Lazy<Boolean> available = Lazy.of(this::checkAvailability);
	
	protected NickServIdentMethod(BotManager manager, String id, String name) {
		super(manager, id, name, CREDIBILITY_HIGH);
	}
	
	protected NickServIdentMethod(BotManager manager, IdentMethodFactory factory) {
		this(manager, factory.id, factory.name);
	}
	
	public boolean isAvailable() {
		return available.get();
	}
	
	protected boolean checkAvailability() {
		return true;
	}
	
	public String getIdentFor(User user) {
		return null;
	}
	
	public class Entry {
		public final User user;
		public final String account;
		public final Date trustedUntil;
		
		public Entry(User user, String account, Date trustedUntil) {
			this.user = user;
			this.account = account;
			this.trustedUntil = trustedUntil;
		}
	}
}