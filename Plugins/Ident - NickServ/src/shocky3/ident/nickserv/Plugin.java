package shocky3.ident.nickserv;

import shocky3.PluginInfo;
import shocky3.ident.IdentMethodFactory;
import shocky3.settings.SettingsContext;

public class Plugin extends shocky3.Plugin {
	public static final String
		TRUST_TIME_KEY = "TrustTime";
	
	@Dependency
	protected shocky3.settings.Plugin settingsPlugin;
	@Dependency
	protected shocky3.ident.Plugin identPlugin;
	
	protected SettingsContext settings;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		settings = settingsPlugin.getContext(this);
		settings.putDefault(TRUST_TIME_KEY, NickServIdentMethod.DEFAULT_TRUST_TIME);
		
		identPlugin.register(
			new IdentMethodFactory.Delegate("nickserv", "NickServ account",
				(factory, manager) -> new NickServIdentMethod(manager, factory))
		);
	}
}