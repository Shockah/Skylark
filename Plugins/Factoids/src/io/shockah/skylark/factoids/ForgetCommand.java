package io.shockah.skylark.factoids;

import io.shockah.skylark.UnexpectedException;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandCall.Medium;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.factoids.ForgetCommand.Input;
import io.shockah.skylark.factoids.db.Factoid;

public class ForgetCommand extends NamedCommand<Input, Factoid> {
	private final FactoidsPlugin plugin;
	
	public ForgetCommand(FactoidsPlugin plugin) {
		super("forget", "f");
		this.plugin = plugin;
	}
	
	@Override
	public Input parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
		if (input.isEmpty())
			throw new CommandParseException("Not enough arguments.");
		String[] split = input.split("\\s");
		
		Factoid.Context context = plugin.getDefaultContext();
		String name = null;
		
		if (split[0].charAt(0) == '@') {
			String contextName = split[0].substring(1);
			context = Factoid.Context.valueOf(contextName);
			if (context == null)
				throw new CommandParseException(String.format("Invalid factoid context: %s", contextName));
			
			name = split[1];
		} else {
			name = split[0];
		}
		
		return new Input(context, name);
	}

	@Override
	public CommandResult<Factoid> call(CommandCall call, Input input) {
		Factoid factoid = plugin.findActiveFactoid(call.event, input.name, input.context);
		if (factoid != null) {
			try {
				factoid.active = false;
				factoid.update();
			} catch (Exception e) {
				throw new UnexpectedException(e);
			}
		}
		
		if (call.outputMedium == null)
			call.outputMedium = Medium.Notice;
		return CommandResult.of(factoid, factoid == null ? "Factoid doesn't exist." : "Forgot: " + factoid.raw);
	}
	
	public static final class Input {
		public final Factoid.Context context;
		public final String name;
		
		public Input(Factoid.Context context, String name) {
			this.context = context;
			this.name = name;
		}
	}
}