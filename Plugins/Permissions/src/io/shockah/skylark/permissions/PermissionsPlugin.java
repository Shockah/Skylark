package io.shockah.skylark.permissions;

import java.util.List;
import java.util.stream.Collectors;
import com.mongodb.DBCollection;
import io.shockah.skylark.ident.IdentPlugin;
import io.shockah.skylark.plugin.Plugin;
import io.shockah.skylark.plugin.PluginManager;
import io.shockah.skylark.util.JSON;
import io.shockah.skylark.util.Lazy;

public class PermissionsPlugin extends Plugin {
	@Dependency
	protected IdentPlugin identPlugin;
	
	private Lazy<DBCollection> groupCollection = Lazy.of(() -> collection("groups"));
	private Lazy<DBCollection> identEntryCollection = Lazy.of(() -> collection("identEntries"));
	
	public PermissionsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	private DBCollection collection(String subcollection) {
		String collection = info.packageName();
		if (subcollection != null)
			collection += "|" + subcollection;
		return manager.app.databaseManager.collection(collection);
	}
	
	protected DBCollection groupCollection() {
		return groupCollection.get();
	}
	
	protected DBCollection identEntryCollection() {
		return identEntryCollection.get();
	}
	
	public List<Group> getGroups() {
		return JSON.collectJSON(groupCollection().find()).stream()
			.map(json -> Group.fromJSON(json))
			.collect(Collectors.toList());
	}
}