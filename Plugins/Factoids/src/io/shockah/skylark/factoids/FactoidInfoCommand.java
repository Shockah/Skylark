package io.shockah.skylark.factoids;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import com.google.common.base.Joiner;
import io.shockah.skylark.commands.CommandCall;
import io.shockah.skylark.commands.CommandParseException;
import io.shockah.skylark.commands.CommandResult;
import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.factoids.FactoidInfoCommand.Input;
import io.shockah.skylark.factoids.db.Factoid;
import io.shockah.skylark.ident.IdentMethodFactory;
import io.shockah.skylark.util.TimeDuration;

public class FactoidInfoCommand extends NamedCommand<Input, Factoid> {
	private final FactoidsPlugin plugin;
	
	public FactoidInfoCommand(FactoidsPlugin plugin) {
		super("factoid");
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
		
		if (factoid == null) {
			return CommandResult.of(null, "Factoid doesn't exist.");
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			List<String> lines = new ArrayList<>();
			lines.add(String.format(
					"Factoid: %s | Context: %s | Type: %s | Date: %s UTC (%s ago)",
					factoid.name, factoid.context.name(), factoid.type,
					sdf.format(factoid.date), TimeDuration.format(factoid.date)
			));
			lines.add(String.format("Source: %s", factoid.raw));
			lines.add(String.format("User: %s", Joiner.on("; ").join(factoid.getIdents().stream()
					.map(ident -> {
						IdentMethodFactory identMethodFactory = plugin.identPlugin.getFactoryForPrefix(ident.prefix);
						String identName = identMethodFactory == null ? ident.prefix : identMethodFactory.name;
						return String.format("%s: %s", identName, ident.account);
					})
					.collect(Collectors.toList())
			)));
			return CommandResult.of(factoid, Joiner.on("\n").join(lines));
		}
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