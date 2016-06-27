package io.shockah.skylark.factoids;

import io.shockah.skylark.Bot;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandValue;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.factoids.RememberCommand.Input;
import io.shockah.skylark.factoids.db.Factoid;
import java.util.Date;

public class RememberCommand extends NamedCommand<Input, Factoid> {
	private final FactoidsPlugin plugin;
	
	public RememberCommand(FactoidsPlugin plugin) {
		super("remember", "r");
		this.plugin = plugin;
	}
	
	@Override
	public Input parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
		String[] split = input.split("\\s");
		if (split.length == 1)
			throw new CommandParseException("Not enough arguments.");
		
		Factoid.Context context = plugin.getDefaultContext();
		String name = null;
		String raw = null;
		
		if (split[0].charAt(0) == '@') {
			String contextName = split[0].substring(1);
			context = Factoid.Context.valueOf(contextName);
			if (context == null)
				throw new CommandParseException(String.format("Invalid factoid context: %s", contextName));
			
			name = split[1];
			raw = input.substring(split[0].length() + split[1].length() + 2);
		} else {
			name = split[0];
			raw = input.substring(split[0].length() + 1);
		}
		
		return new Input(context, name, raw);
	}

	@Override
	public CommandValue<Factoid> call(CommandCall call, Input input) {
		return new CommandValue<>(plugin.manager.app.databaseManager.create(Factoid.class, Integer.class, factoid -> {
			factoid.server = call.event.<Bot>getBot().manager.name;
			factoid.channel = call.event.getChannel().getName();
			factoid.context = input.context;
			factoid.name = input.name;
			factoid.raw = input.raw;
			factoid.date = new Date();
		}));
	}
	
	public static final class Input {
		public final Factoid.Context context;
		public final String name;
		public final String raw;
		
		public Input(Factoid.Context context, String name, String raw) {
			this.context = context;
			this.name = name;
			this.raw = raw;
		}
	}
}