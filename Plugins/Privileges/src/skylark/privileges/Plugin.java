package skylark.privileges;

import java.util.Map;
import skylark.PluginInfo;
import skylark.util.Synced;

public class Plugin extends skylark.Plugin {
	protected final Map<String, Group> groups = Synced.map();
	
	@Dependency
	protected skylark.ident.Plugin identPlugin;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		
	}
	
	protected void onUnload() {
		groups.clear();
	}
	
	
}