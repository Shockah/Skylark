package sfactoids;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import shocky3.PluginInfo;

public class Plugin extends shocky3.Plugin {
	@Dependency protected static sident.Plugin pluginIdent;
	@Dependency protected static scommands.Plugin pluginCmd;
	
	protected List<FactoidParser> parsers = Collections.synchronizedList(new LinkedList<FactoidParser>());
	
	public void add(FactoidParser... fps) {
		synchronized (parsers) {for (FactoidParser fp : fps) {
			if (!parsers.contains(fp))
				parsers.add(fp);
		}}
	}
	public void remove(FactoidParser... fps) {
		synchronized (parsers) {for (FactoidParser fp : fps)
			parsers.remove(fp);
		}
	}
	public FactoidParser findParserByID(String id) {
		synchronized (parsers) {for (FactoidParser fp : parsers)
			if (fp.id.equals(id))
				return fp;
		}
		return null;
	}
	
	public FactoidCommandProvider provider;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		pluginCmd.pattern.add(provider = new FactoidCommandProvider(this));
		pluginCmd.provider.add(
			new CmdRemember(this),
			new CmdForget(this),
			new CmdInfo(this)
		);
		
		parsers.clear();
		add(new AliasFactoidParser());
	}
}