package skylark.ident.nickserv;

import skylark.BotManager;
import skylark.PluginInfo;
import skylark.ident.IdentMethodFactory;
import skylark.pircbotx.Bot;
import skylark.pircbotx.event.AccountNotifyEvent;
import skylark.pircbotx.event.ExtendedJoinEvent;
import skylark.settings.Setting;

public class Plugin extends skylark.ListenerPlugin {
	public static final String
		TRUST_TIME_KEY = "TrustTime";
	
	public static final String
		IDENT_METHOD_ID = "nickserv",
		IDENT_METHOD_NAME = "NickServ account";
	
	@Dependency
	protected skylark.settings.Plugin settingsPlugin;
	@Dependency
	protected skylark.ident.Plugin identPlugin;
	
	protected Setting<Long> trustTimeSetting;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		trustTimeSetting = settingsPlugin.<Long>getSetting(this, TRUST_TIME_KEY);
		trustTimeSetting.putDefault(NickServIdentMethod.DEFAULT_TRUST_TIME);
		
		identPlugin.register(
			new IdentMethodFactory.Delegate(IDENT_METHOD_ID, IDENT_METHOD_NAME,
				(factory, manager) -> new NickServIdentMethod(this, manager, factory))
		);
	}
	
	protected void onExtendedJoin(ExtendedJoinEvent e) {
		BotManager manager = e.<Bot>getBot().manager;
		NickServIdentMethod method = identPlugin.getForID(manager, IDENT_METHOD_ID);
		if (method != null)
			method.putIdentFor(e.getUser(), e.getAccount(), NickServIdentMethod.Source.ExtendedJoin);
	}
	
	protected void onAccountNotify(AccountNotifyEvent e) {
		BotManager manager = e.<Bot>getBot().manager;
		NickServIdentMethod method = identPlugin.getForID(manager, IDENT_METHOD_ID);
		if (method != null)
			method.putIdentFor(e.getUser(), e.getAccount(), NickServIdentMethod.Source.AccountNotify);
	}
}