package skylark.privileges;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.pircbotx.User;
import pl.shockah.json.JSONList;
import skylark.old.PluginInfo;
import skylark.old.util.JSON;
import skylark.old.util.Synced;

public class Plugin extends skylark.old.Plugin {
	protected final Map<String, Group> groups = Synced.map();
	
	@Dependency
	protected static skylark.ident.Plugin identPlugin;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		synchronized (groups) {
			JSON.forEachJSONObject(botApp.collection(this).find(), j -> {
				Group group = new Group(this, j.getString("name"));
				for (JSONList<?> jIdentSets : j.getList("idents").ofLists()) {
					Group.IdentSet identSet = new Group.IdentSet();
					for (String fullIdent : jIdentSets.ofStrings()) {
						String identId = identPlugin.getForFullIdent(fullIdent).id;
						String identRegex = identPlugin.getIdentFromFullIdent(fullIdent);
						identSet.idents.add(new Group.Ident(identId, identRegex));
					}
					group.idents.add(identSet);
				}
				group.privileges.addAll(j.getList("privileges").ofStrings());
				groups.put(group.name, group);
			});
		}
	}
	
	protected void onUnload() {
		groups.clear();
	}
	
	public List<Group> getGroups(User user) {
		List<Group> list = new ArrayList<>();
		Synced.forEach(groups, group -> {
			if (group.isMember(user))
				list.add(group);
		});
		return list;
	}
	
	public List<String> getPrivileges(User user) {
		List<String> list = new ArrayList<>();
		Synced.forEach(groups, group -> {
			if (group.isMember(user))
				for (String privilege : group.privileges)
					if (!list.contains(privilege))
						list.add(privilege);
		});
		return list;
	}
	
	public boolean hasPrivilege(User user, String privilege) {
		synchronized (groups) {
			for (Map.Entry<String, Group> entry : groups.entrySet())
				if (entry.getValue().isMember(user) && entry.getValue().isAllowed(privilege))
					return true;
		}
		return false;
	}
}