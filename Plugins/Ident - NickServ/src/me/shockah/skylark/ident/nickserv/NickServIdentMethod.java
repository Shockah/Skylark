package me.shockah.skylark.ident.nickserv;

import me.shockah.skylark.Bot;
import me.shockah.skylark.event.Whois2Event;
import me.shockah.skylark.ident.IdentMethod;
import me.shockah.skylark.ident.IdentMethodFactory;
import me.shockah.skylark.ident.IdentService;
import me.shockah.skylark.util.Lazy;
import org.pircbotx.User;

public class NickServIdentMethod extends IdentMethod {
	public static final String METHOD_NAME = "Name";
	public static final String METHOD_PREFIX = "n";
	
	public static final String OPERATOR_STATUS_NETWORK_SERVICE = "Network Service";
	
	protected final Lazy<Boolean> available = Lazy.of(this::checkAvailability);
	
	protected boolean hasWhoX = false;
	protected boolean hasExtendedJoin = false;
	protected boolean hasAccountNotify = false;
	
	public NickServIdentMethod(IdentService service, IdentMethodFactory factory) {
		super(service, factory, METHOD_NAME, METHOD_PREFIX);
	}

	@Override
	public boolean isAvailable() {
		return available.get();
	}
	
	protected boolean checkAvailability() {
		Bot bot = getAnyBot();
		hasWhoX = bot.getServerInfo().isWhoX();
		hasExtendedJoin = bot.getEnabledCapabilities().contains("extended-join");
		hasAccountNotify = bot.getEnabledCapabilities().contains("account-notify");
		
		Whois2Event whois = bot.whoisManager.syncRequestForUser("NickServ");
		return whois != null && OPERATOR_STATUS_NETWORK_SERVICE.equals(whois.getOperatorStatus());
	}
	
	protected Bot getAnyBot() {
		return service.manager.bots.readOperation(bots -> {
			if (service.manager.bots.isEmpty())
				return service.manager.connectNewBot();
			return service.manager.bots.get(0);
		});
	}

	@Override
	public String getForUser(User user) {
		//TODO:
		return null;
	}
	
	public static class Factory extends IdentMethodFactory {
		public Factory() {
			super(METHOD_NAME, METHOD_PREFIX);
		}

		@Override
		public IdentMethod create(IdentService service) {
			return new NickServIdentMethod(service, this);
		}
	}
}