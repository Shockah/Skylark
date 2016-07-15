package io.shockah.skylark.groovy;

import java.util.Map;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.shockah.skylark.commands.CommandsPlugin;
import io.shockah.skylark.factoids.FactoidType;
import io.shockah.skylark.factoids.FactoidsPlugin;
import io.shockah.skylark.plugin.Plugin;
import io.shockah.skylark.plugin.PluginManager;

public class GroovyPlugin extends Plugin {
	@Dependency
	protected CommandsPlugin commandsPlugin;
	
	@Dependency("io.shockah.skylark.factoids")
	protected Plugin factoidsOptionalPlugin;
	
	private GroovyCommand command;
	
	private Object factoidType;
	
	public GroovyPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.addNamedCommand(command = new GroovyCommand(this));
		
		if (factoidsOptionalPlugin != null) {
			FactoidsPlugin factoidsPlugin = (FactoidsPlugin)factoidsOptionalPlugin;
			FactoidType factoidType = new GroovyFactoidType(this);
			this.factoidType = factoidType;
			factoidsPlugin.addType(factoidType);
		}
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeNamedCommand(command);
		
		if (factoidsOptionalPlugin != null) {
			FactoidsPlugin factoidsPlugin = (FactoidsPlugin)factoidsOptionalPlugin;
			factoidsPlugin.removeType((FactoidType)factoidType);
		}
	}
	
	public GroovyShell getShell() {
		return new GroovyShell(new Binding());
	}
	
	public GroovyShell getShell(Map<String, Object> variables) {
		return new GroovyShell(new Binding(variables));
	}
}