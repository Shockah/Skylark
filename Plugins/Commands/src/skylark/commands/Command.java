package skylark.commands;

import java.util.ArrayList;
import java.util.List;
import me.shockah.skylark.event.GenericUserMessageEvent;
import pl.shockah.func.Func2;
import pl.shockah.json.JSONObject;

public abstract class Command {
	public static String getSimpleArg(JSONObject json) {
		if (json.contains("arg"))
			return json.getString("arg");
		else if (json.contains("args"))
			return String.join(" ", json.getList("args").ofStrings());
		return "";
	}
	
	public static String[] getSplitArgs(JSONObject json) {
		if (json.contains("args"))
			return json.getList("args").ofStrings().toArray(new String[0]);
		else if (json.contains("arg"))
			return json.getString("arg").split("\\s");
		return new String[0];
	}
	
	public final skylark.old.Plugin plugin;
	public final String name;
	public final String privilege;
	public final CommandInputParser parser;
	
	public Command(skylark.old.Plugin plugin, String name, CommandInputParser parser) {
		this(plugin, name, null, parser);
	}
	
	public Command(skylark.old.Plugin plugin, String name, String privilege, CommandInputParser parser) {
		this.plugin = plugin;
		this.name = name;
		this.privilege = privilege;
		this.parser = parser;
	}
	
	protected final CommandOutput execute(CommandStack stack, GenericUserMessageEvent e, JSONObject json) {
		List<String> messages = new ArrayList<>();
		boolean allowed = isAllowed(stack, e, json, messages);
		
		if (!allowed) {
			String message = String.join(" ", messages);
			return new CommandOutput("Not allowed to use this command" + (message.equals("") ? "." : ": " + message));
		}
		return onExecute(stack, e, json);
	}
	
	protected final boolean isAllowed(CommandStack stack, GenericUserMessageEvent e, JSONObject json) {
		return isAllowed(stack, e, json, null);
	}
	
	protected boolean isAllowed(CommandStack stack, GenericUserMessageEvent e, JSONObject json, List<String> messages) {
		if (privilege != null && !Plugin.privilegesPlugin.hasPrivilege(e.getUser(), privilege)) {
			if (messages != null)
				messages.add(String.format("Missing privilege '%s'.", privilege));
			return false;
		}
		return true;
	}
	
	protected abstract CommandOutput onExecute(CommandStack stack, GenericUserMessageEvent e, JSONObject json);
	
	public static class Delegate extends Command {
		public final Func2<GenericUserMessageEvent, JSONObject, CommandOutput> func;
		
		public Delegate(skylark.old.Plugin plugin, String name, CommandInputParser parser, Func2<GenericUserMessageEvent, JSONObject, CommandOutput> func) {
			super(plugin, name, parser);
			this.func = func;
		}
		
		public Delegate(skylark.old.Plugin plugin, String name, String privilege, CommandInputParser parser, Func2<GenericUserMessageEvent, JSONObject, CommandOutput> func) {
			super(plugin, name, privilege, parser);
			this.func = func;
		}
		
		protected CommandOutput onExecute(CommandStack stack, GenericUserMessageEvent e, JSONObject json) {
			return func.f(e, json);
		}
	}
}