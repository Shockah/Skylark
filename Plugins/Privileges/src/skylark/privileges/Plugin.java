package skylark.privileges;

import java.util.Map;
import skylark.PluginInfo;
import skylark.util.JSON;
import skylark.util.Synced;

public class Plugin extends skylark.Plugin {
	protected final Map<String, Group> groups = Synced.map();
	
	@Dependency
	protected skylark.ident.Plugin identPlugin;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		synchronized (groups) {
			JSON.forEachJSONObject(botApp.collection(this).find(), j -> {
				Group group = new Group(this, j.getString("name"));
				for (String fullIdent : j.getList("idents").ofStrings()) {
					String identId = identPlugin.getForFullIdent(fullIdent).id;
					String identRegex = identPlugin.getIdentFromFullIdent(fullIdent);
					group.idents.add(new Group.Ident(identId, identRegex));
				}
				group.privileges.addAll(j.getList("privileges").ofStrings());
				groups.put(group.name, group);
			});
		}
	}
	
	protected void onUnload() {
		groups.clear();
	}
}