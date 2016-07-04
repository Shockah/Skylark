package io.shockah.skylark.factoids;

import java.util.Date;
import java.util.Map;
import io.shockah.skylark.Bot;
import io.shockah.skylark.DatabaseManager;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandCall.Medium;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandValue;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.factoids.RememberCommand.Input;
import io.shockah.skylark.factoids.db.Factoid;
import io.shockah.skylark.factoids.db.FactoidIdent;
import io.shockah.skylark.ident.IdentMethod;

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
		DatabaseManager databaseManager = plugin.manager.app.databaseManager;
		
		databaseManager.delete(Factoid.class, (builder, where) -> {
			where
				.equals(Factoid.NAME_COLUMN, input.name)
				.equals(Factoid.ACTIVE_COLUMN, false)
				.equals(Factoid.CONTEXT_COLUMN, input.context);
			if (input.context == Factoid.Context.Channel)
				where
					.equals(Factoid.SERVER_COLUMN, call.event.<Bot>getBot().manager.name)
					.equals(Factoid.CHANNEL_COLUMN, call.event.getChannel().getName());
			else if (input.context == Factoid.Context.Server)
				where.equals(Factoid.SERVER_COLUMN, call.event.<Bot>getBot().manager.name);
		});
		
		Factoid factoid = databaseManager.create(Factoid.class, obj -> {
			obj.server = call.event.<Bot>getBot().manager.name;
			obj.channel = call.event.getChannel().getName();
			obj.context = input.context;
			obj.name = input.name;
			obj.raw = input.raw;
			obj.date = new Date();
		});
		
		Map<IdentMethod, String> idents = plugin.identPlugin.getIdentsForUser(call.event.getUser());
		for (Map.Entry<IdentMethod, String> entry : idents.entrySet()) {
			if (entry.getValue() != null)
				FactoidIdent.createOf(databaseManager, factoid, entry.getKey(), entry.getValue());
		}
		
		if (call.outputMedium == null)
			call.outputMedium = Medium.Notice;
		return new CommandValue.Simple<>(factoid, "Done.");
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