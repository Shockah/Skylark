package skylark.commands;

import java.util.List;
import pl.shockah.json.JSONObject;
import skylark.old.Plugin;
import skylark.old.pircbotx.event.GenericUserMessageEvent;

public abstract class TypedArgCommand<T> extends Command {
	public TypedArgCommand(Plugin plugin, String name, CommandInputParser parser) {
		super(plugin, name, parser);
	}
	
	public TypedArgCommand(Plugin plugin, String name, String privilege, CommandInputParser parser) {
		super(plugin, name, privilege, parser);
	}
	
	protected final boolean isAllowed(CommandStack stack, GenericUserMessageEvent e, JSONObject json, List<String> messages) {
		return isAllowed(stack, e, getArg(e, json), messages);
	}
	
	protected final CommandOutput onExecute(CommandStack stack, GenericUserMessageEvent e, JSONObject json) {
		return onExecute(stack, e, getArg(e, json));
	}
	
	protected abstract T getArg(GenericUserMessageEvent e, JSONObject json);
	
	protected boolean isAllowed(CommandStack stack, GenericUserMessageEvent e, T arg, List<String> messages) {
		return super.isAllowed(stack, e, null, messages);
	}
	
	protected abstract CommandOutput onExecute(CommandStack stack, GenericUserMessageEvent e, T arg);
}