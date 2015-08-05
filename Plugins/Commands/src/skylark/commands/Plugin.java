package skylark.commands;

import java.util.List;
import skylark.PluginInfo;
import skylark.util.Synced;

public class Plugin extends skylark.Plugin {
	protected final List<CommandPattern> patterns = Synced.list();
	protected final List<CommandProvider> providers = Synced.list();
	
	@Dependency
	protected skylark.settings.Plugin settingsPlugin;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		register(
			new DefaultCommandPattern(this)
		);
		
		register(
			new DefaultCommandProvider()
		);
	}
	
	protected void onUnload() {
		patterns.clear();
		providers.clear();
	}
	
	public void register(CommandProvider provider) {
		providers.add(provider);
	}
	
	public void register(CommandProvider... providers) {
		for (CommandProvider provider : providers)
			register(provider);
	}
	
	public void unregister(CommandProvider provider) {
		providers.remove(provider);
	}
	
	public void unregister(CommandProvider... providers) {
		for (CommandProvider provider : providers)
			unregister(provider);
	}
	
	public void register(CommandPattern pattern) {
		patterns.add(pattern);
	}
	
	public void register(CommandPattern... patterns) {
		for (CommandPattern pattern : patterns)
			register(pattern);
	}
	
	public void unregister(CommandPattern pattern) {
		patterns.remove(pattern);
	}
	
	public void unregister(CommandPattern... patterns) {
		for (CommandPattern pattern : patterns)
			unregister(pattern);
	}
}