package io.shockah.skylark.groovy;

import java.util.Map;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.collect.ImmutableMap;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.transform.TimedInterrupt;
import io.shockah.skylark.commands.CommandsPlugin;
import io.shockah.skylark.factoids.FactoidType;
import io.shockah.skylark.factoids.FactoidsPlugin;
import io.shockah.skylark.func.Func1;
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
		return getShell(new GroovySandbox());
	}
	
	public GroovyShell getShell(Map<String, Object> variables) {
		return getShell(variables, new GroovySandbox());
	}
	
	public GroovyShell getShell(GroovySandbox sandbox) {
		return getShell(new Binding(), sandbox);
	}
	
	public GroovyShell getShell(Map<String, Object> variables, GroovySandbox sandbox) {
		return getShell(new Binding(variables), sandbox);
	}
	
	private Func1<String, Object> getEvalFunction(Binding binding, GroovySandbox sandbox) {
		return input -> getShell(binding, sandbox).evaluate(input);
	}
	
	private GroovyShell getShell(Binding binding, GroovySandbox sandbox) {
		binding.setVariable("eval", getEvalFunction(binding, sandbox));
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.addCompilationCustomizers(
				new SandboxTransformer(),
				new ASTTransformationCustomizer(ImmutableMap.of("value", 10), TimedInterrupt.class),
				new ImportCustomizer().addImports(HttpRequest.class.getName())
		);
		GroovyShell shell = new GroovyShell(manager.pluginClassLoader, binding, cc);
		sandbox.register();
		return shell;
	}
}