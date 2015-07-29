package shocky3.ident;

import java.util.List;
import shocky3.PluginInfo;
import shocky3.util.Synced;

public class Plugin extends shocky3.Plugin {
	public List<IdentMethod> identMethods = Synced.list();
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
}