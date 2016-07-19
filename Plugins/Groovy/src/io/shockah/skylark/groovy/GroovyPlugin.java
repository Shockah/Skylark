package io.shockah.skylark.groovy;

import java.util.Map;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.collect.ImmutableMap;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.transform.TimedInterrupt;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandsPlugin;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.factoids.FactoidType;
import io.shockah.skylark.factoids.FactoidsPlugin;
import io.shockah.skylark.func.Func1;
import io.shockah.skylark.plugin.Plugin;
import io.shockah.skylark.plugin.PluginManager;

public class GroovyPlugin extends Plugin {
	public static final int TIMEOUT = 30;
	
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
	
	public GroovyShell getShell(GenericUserMessageEvent event) {
		return getShell(new GroovySandboxImpl(), event);
	}
	
	public GroovyShell getShell(Map<String, Object> variables, GenericUserMessageEvent event) {
		return getShell(variables, new GroovySandboxImpl(), event);
	}
	
	public GroovyShell getShell(GroovyInterceptor sandbox, GenericUserMessageEvent event) {
		return getShell(new Binding(), sandbox, event);
	}
	
	public GroovyShell getShell(Map<String, Object> variables, GroovyInterceptor sandbox, GenericUserMessageEvent event) {
		return getShell(new Binding(variables), sandbox, event);
	}
	
	private Func1<String, Object> getEvalFunction(Binding binding, GroovyInterceptor sandbox, GenericUserMessageEvent event) {
		return input -> getShell(binding, sandbox, event).evaluate(input);
	}
	
	private GroovyShell getShell(Binding binding, GroovyInterceptor sandbox, GenericUserMessageEvent event) {
		binding.setVariable("eval", getEvalFunction(binding, sandbox, event));
		binding.setVariable("commands", new DynamicCommandHandler(this, event));
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.addCompilationCustomizers(
				new SandboxTransformer(),
				new ASTTransformationCustomizer(ImmutableMap.of("value", TIMEOUT), TimedInterrupt.class),
				new ImportCustomizer()
					.addStarImports("java.lang.reflect")
					.addImports(HttpRequest.class.getName())
					.addImports(CommandCall.class.getName())
		);
		GroovyShell shell = new GroovyShell(manager.pluginClassLoader, binding, cc);
		sandbox.register();
		return shell;
	}
}