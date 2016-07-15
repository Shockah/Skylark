package io.shockah.skylark.groovy;

import java.util.Map;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import com.google.common.collect.ImmutableMap;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.transform.TimedInterrupt;
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
	}
	
	@Override
	protected void onAllPluginsLoaded() {
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
		return getShell(new Binding());
	}
	
	public GroovyShell getShell(Map<String, Object> variables) {
		return getShell(new Binding(variables));
	}
	
	private GroovyShell getShell(Binding binding) {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.addCompilationCustomizers(
				new SandboxTransformer(),
				new ASTTransformationCustomizer(ImmutableMap.of("value", 10), TimedInterrupt.class)
		);
		new GroovySandbox().register();
		return new GroovyShell(binding, cc);
	}
}