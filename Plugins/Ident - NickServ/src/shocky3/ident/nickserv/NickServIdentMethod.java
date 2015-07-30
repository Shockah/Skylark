package shocky3.ident.nickserv;

import org.pircbotx.User;
import shocky3.BotManager;
import shocky3.ident.IdentMethod;
import shocky3.ident.IdentMethodFactory;

public class NickServIdentMethod extends IdentMethod {
	protected NickServIdentMethod(BotManager manager, String id, String name) {
		super(manager, id, name, CREDIBILITY_HIGH);
	}
	protected NickServIdentMethod(BotManager manager, IdentMethodFactory factory) {
		this(manager, factory.id, factory.name);
	}
	
	public String getIdentFor(User user) {
		return null;
	}
}