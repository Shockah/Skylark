package skylark.commands;

import pl.shockah.func.Func2;
import skylark.JSONThing;
import skylark.pircbotx.event.GenericUserMessageEvent;

public abstract class Command {
	public final skylark.Plugin plugin;
	public final String name;
	public final String privilege;
	public final CommandInputParser parser;
	
	public Command(skylark.Plugin plugin, String name, CommandInputParser parser) {
		this(plugin, name, null, parser);
	}
	
	public Command(skylark.Plugin plugin, String name, String privilege, CommandInputParser parser) {
		this.plugin = plugin;
		this.name = name;
		this.privilege = privilege;
		this.parser = parser;
	}
	
	protected final CommandOutput execute(CommandStack stack, GenericUserMessageEvent e, JSONThing json) {
		if (privilege != null && !Plugin.privilegesPlugin.hasPrivilege(e.getUser(), privilege))
			return new CommandOutput(String.format("Missing privilege '%s'", privilege));
		return onExecute(stack, e, json);
	}
	
	protected abstract CommandOutput onExecute(CommandStack stack, GenericUserMessageEvent e, JSONThing json);
	
	public static class Delegate extends Command {
		public final Func2<GenericUserMessageEvent, JSONThing, CommandOutput> func;
		
		public Delegate(skylark.Plugin plugin, String name, CommandInputParser parser, Func2<GenericUserMessageEvent, JSONThing, CommandOutput> func) {
			super(plugin, name, parser);
			this.func = func;
		}
		
		public Delegate(skylark.Plugin plugin, String name, String privilege, CommandInputParser parser, Func2<GenericUserMessageEvent, JSONThing, CommandOutput> func) {
			super(plugin, name, privilege, parser);
			this.func = func;
		}
		
		protected CommandOutput onExecute(CommandStack stack, GenericUserMessageEvent e, JSONThing json) {
			return func.f(e, json);
		}
	}
}