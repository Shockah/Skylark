package skylark.ident.nickserv;

import skylark.PluginInfo;
import skylark.ident.IdentMethodFactory;
import skylark.settings.SettingsContext;

public class Plugin extends skylark.Plugin {
	public static final String
		TRUST_TIME_KEY = "TrustTime";
	
	@Dependency
	protected skylark.settings.Plugin settingsPlugin;
	@Dependency
	protected skylark.ident.Plugin identPlugin;
	
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